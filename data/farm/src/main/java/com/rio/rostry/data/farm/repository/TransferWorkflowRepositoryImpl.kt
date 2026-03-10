package com.rio.rostry.data.farm.repository

import com.google.gson.Gson
import com.rio.rostry.core.model.Result
import com.rio.rostry.data.database.dao.AuditLogDao
import com.rio.rostry.data.database.dao.DisputeDao
import com.rio.rostry.data.database.dao.OutboxDao
import com.rio.rostry.data.database.dao.ProductDao
import com.rio.rostry.data.database.dao.TransferDao
import com.rio.rostry.data.database.dao.TransferVerificationDao
import com.rio.rostry.data.database.entity.AuditLogEntity
import com.rio.rostry.data.database.entity.DisputeEntity
import com.rio.rostry.data.database.entity.DisputeStatus
import com.rio.rostry.data.database.entity.OutboxEntity
import com.rio.rostry.data.database.entity.TransferEntity
import com.rio.rostry.data.database.entity.TransferVerificationEntity
import com.rio.rostry.domain.farm.repository.TraceabilityRepository
import com.rio.rostry.domain.farm.repository.TransferWorkflowRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Lightweight transfer workflow implementation that stays module-safe.
 */
@Singleton
class TransferWorkflowRepositoryImpl @Inject constructor(
    private val transferDao: TransferDao,
    private val verificationDao: TransferVerificationDao,
    private val disputeDao: DisputeDao,
    private val auditLogDao: AuditLogDao,
    private val traceabilityRepository: TraceabilityRepository,
    private val productDao: ProductDao,
    private val outboxDao: OutboxDao? = null,
    private val gson: Gson = Gson()
) : TransferWorkflowRepository {

    private fun now() = System.currentTimeMillis()

    override suspend fun validateTransferEligibility(
        productId: String,
        fromUserId: String,
        toUserId: String?,
        logOnFailure: Boolean
    ): Result<Unit> = withContext(Dispatchers.IO) {
        val product = productDao.findById(productId)
            ?: return@withContext Result.Error(IllegalStateException("Product not found"))

        if (product.sellerId != fromUserId) {
            return@withContext Result.Error(IllegalStateException("You do not own this product"))
        }

        when (product.lifecycleStatus?.uppercase()) {
            "QUARANTINE" -> return@withContext Result.Error(IllegalStateException("Cannot transfer products in quarantine"))
            "DECEASED" -> return@withContext Result.Error(IllegalStateException("Cannot transfer deceased birds"))
            "TRANSFERRED" -> return@withContext Result.Error(IllegalStateException("Product already transferred"))
        }

        when (val report = traceabilityRepository.getTransferEligibilityReport(productId)) {
            is Result.Success -> {
                val eligible = report.data["eligible"] as? Boolean ?: true
                if (!eligible) {
                    val reasons = (report.data["reasons"] as? List<*>)?.filterIsInstance<String>().orEmpty()
                    val msg = reasons.joinToString("; ").ifBlank { "Not eligible for transfer" }
                    if (logOnFailure) {
                        auditLogDao.insert(
                            AuditLogEntity.createValidationFailureLog(
                                refId = productId,
                                action = "TRANSFER_BLOCKED",
                                actorUserId = fromUserId,
                                reasons = reasons.ifEmpty { listOf(msg) }
                            )
                        )
                    }
                    return@withContext Result.Error(IllegalStateException(msg))
                }
            }
            is Result.Error -> return@withContext Result.Error(report.exception)
        }

        Result.Success(Unit)
    }

    override suspend fun initiate(
        productId: String,
        fromUserId: String,
        toUserId: String,
        amount: Double,
        currency: String,
        sellerPhotoUrl: String?,
        gpsLat: Double?,
        gpsLng: Double?,
        conditionsJson: String?,
        timeoutAt: Long?
    ): Result<String> = withContext(Dispatchers.IO) {
        when (val eligibility = validateTransferEligibility(productId, fromUserId, toUserId, logOnFailure = true)) {
            is Result.Error -> return@withContext Result.Error(eligibility.exception)
            is Result.Success -> Unit
        }

        val transferId = UUID.randomUUID().toString()
        val entity = TransferEntity(
            transferId = transferId,
            productId = productId,
            fromUserId = fromUserId,
            toUserId = toUserId,
            amount = amount,
            currency = currency,
            type = "OWNERSHIP",
            status = "PENDING",
            sellerPhotoUrl = sellerPhotoUrl,
            gpsLat = gpsLat,
            gpsLng = gpsLng,
            timeoutAt = timeoutAt,
            conditionsJson = conditionsJson,
            initiatedAt = now(),
            updatedAt = now(),
            lastModifiedAt = now(),
            dirty = true
        )
        transferDao.upsert(entity)

        verificationDao.upsert(
            TransferVerificationEntity(
                verificationId = UUID.randomUUID().toString(),
                transferId = transferId,
                step = "SELLER_INIT",
                status = "APPROVED",
                photoBeforeUrl = sellerPhotoUrl,
                gpsLat = gpsLat,
                gpsLng = gpsLng,
                createdAt = now(),
                updatedAt = now()
            )
        )

        outboxDao?.insert(
            OutboxEntity(
                outboxId = UUID.randomUUID().toString(),
                userId = fromUserId,
                entityType = OutboxEntity.TYPE_TRANSFER,
                entityId = transferId,
                operation = "CREATE",
                payloadJson = gson.toJson(entity),
                createdAt = now(),
                priority = "CRITICAL"
            )
        )

        auditLogDao.insert(
            AuditLogEntity(
                logId = UUID.randomUUID().toString(),
                type = "TRANSFER",
                refId = transferId,
                action = "INITIATE",
                actorUserId = fromUserId,
                detailsJson = gson.toJson(entity),
                createdAt = now()
            )
        )

        Result.Success(transferId)
    }

    override suspend fun appendSellerEvidence(
        transferId: String,
        photoBeforeUrl: String?,
        photoAfterUrl: String?,
        photoBeforeMetaJson: String?,
        photoAfterMetaJson: String?
    ): Result<Unit> = withContext(Dispatchers.IO) {
        verificationDao.upsert(
            TransferVerificationEntity(
                verificationId = UUID.randomUUID().toString(),
                transferId = transferId,
                step = "SELLER_INIT",
                status = "APPROVED",
                photoBeforeUrl = photoBeforeUrl,
                photoAfterUrl = photoAfterUrl,
                photoBeforeMetaJson = photoBeforeMetaJson,
                photoAfterMetaJson = photoAfterMetaJson,
                createdAt = now(),
                updatedAt = now()
            )
        )
        Result.Success(Unit)
    }

    override suspend fun buyerVerify(
        transferId: String,
        buyerPhotoUrl: String?,
        buyerGpsLat: Double?,
        buyerGpsLng: Double?,
        identityDocType: String?,
        identityDocRef: String?,
        identityDocNumber: String?,
        buyerPhotoMetaJson: String?,
        gpsExplanation: String?
    ): Result<Unit> = withContext(Dispatchers.IO) {
        val transfer = transferDao.getById(transferId)
            ?: return@withContext Result.Error(IllegalStateException("Transfer not found"))

        verificationDao.upsert(
            TransferVerificationEntity(
                verificationId = UUID.randomUUID().toString(),
                transferId = transferId,
                step = "BUYER_VERIFY",
                status = "APPROVED",
                photoAfterUrl = buyerPhotoUrl,
                photoAfterMetaJson = buyerPhotoMetaJson,
                gpsLat = buyerGpsLat,
                gpsLng = buyerGpsLng,
                identityDocType = identityDocType,
                identityDocRef = identityDocRef,
                notes = listOfNotNull(
                    identityDocNumber?.let { "identityDocNumber=$it" },
                    gpsExplanation?.let { "gpsExplanation=$it" }
                ).joinToString(";"),
                createdAt = now(),
                updatedAt = now()
            )
        )

        transferDao.upsert(transfer.copy(buyerPhotoUrl = buyerPhotoUrl, updatedAt = now(), lastModifiedAt = now(), dirty = true))
        Result.Success(Unit)
    }

    override suspend fun platformApproveIfNeeded(transferId: String): Result<Unit> = withContext(Dispatchers.IO) {
        val transfer = transferDao.getById(transferId)
            ?: return@withContext Result.Error(IllegalStateException("Transfer not found"))

        if (transfer.amount > 10_000.0) {
            verificationDao.upsert(
                TransferVerificationEntity(
                    verificationId = UUID.randomUUID().toString(),
                    transferId = transferId,
                    step = "PLATFORM_REVIEW",
                    status = "APPROVED",
                    createdAt = now(),
                    updatedAt = now()
                )
            )
        }
        Result.Success(Unit)
    }

    override suspend fun platformReview(
        transferId: String,
        approved: Boolean,
        notes: String?,
        actorUserId: String?
    ): Result<Unit> = withContext(Dispatchers.IO) {
        verificationDao.upsert(
            TransferVerificationEntity(
                verificationId = UUID.randomUUID().toString(),
                transferId = transferId,
                step = "PLATFORM_REVIEW",
                status = if (approved) "APPROVED" else "REJECTED",
                notes = notes,
                createdAt = now(),
                updatedAt = now()
            )
        )
        auditLogDao.insert(
            AuditLogEntity(
                logId = UUID.randomUUID().toString(),
                type = "VERIFICATION",
                refId = transferId,
                action = if (approved) "PLATFORM_APPROVE" else "PLATFORM_REJECT",
                actorUserId = actorUserId,
                detailsJson = notes,
                createdAt = now()
            )
        )
        Result.Success(Unit)
    }

    override suspend fun complete(transferId: String): Result<Unit> = withContext(Dispatchers.IO) {
        val transfer = transferDao.getById(transferId)
            ?: return@withContext Result.Error(IllegalStateException("Transfer not found"))

        transferDao.updateStatusAndTimestamps(transferId, "COMPLETED", now(), completedAt = now())

        val productId = transfer.productId
        val newOwnerId = transfer.toUserId
        if (productId != null && newOwnerId != null) {
            val product = productDao.findById(productId)
            if (product != null) {
                productDao.upsert(
                    product.copy(
                        sellerId = newOwnerId,
                        status = "private",
                        updatedAt = now(),
                        lastModifiedAt = now(),
                        dirty = true
                    )
                )
            }
        }

        auditLogDao.insert(
            AuditLogEntity(
                logId = UUID.randomUUID().toString(),
                type = "TRANSFER",
                refId = transferId,
                action = "COMPLETE",
                actorUserId = transfer.toUserId,
                detailsJson = null,
                createdAt = now()
            )
        )

        Result.Success(Unit)
    }

    override suspend fun cancel(transferId: String, reason: String?): Result<Unit> = withContext(Dispatchers.IO) {
        transferDao.updateStatusAndTimestamps(transferId, "CANCELLED", now(), completedAt = null)
        auditLogDao.insert(
            AuditLogEntity(
                logId = UUID.randomUUID().toString(),
                type = "TRANSFER",
                refId = transferId,
                action = "CANCEL",
                actorUserId = null,
                detailsJson = reason,
                createdAt = now()
            )
        )
        Result.Success(Unit)
    }

    override suspend fun raiseDispute(
        transferId: String,
        raisedByUserId: String,
        reason: String
    ): Result<String> = withContext(Dispatchers.IO) {
        val disputeId = UUID.randomUUID().toString()
        disputeDao.upsert(
            DisputeEntity(
                disputeId = disputeId,
                transferId = transferId,
                reporterId = raisedByUserId,
                reason = reason,
                status = DisputeStatus.OPEN,
                createdAt = now()
            )
        )
        Result.Success(disputeId)
    }

    override suspend fun resolveDispute(
        disputeId: String,
        resolutionNotes: String,
        resolved: Boolean
    ): Result<Unit> = withContext(Dispatchers.IO) {
        val existing = disputeDao.getById(disputeId)
            ?: return@withContext Result.Error(IllegalStateException("Dispute not found"))

        disputeDao.upsert(
            existing.copy(
                status = if (resolved) DisputeStatus.RESOLVED_DISMISSED else DisputeStatus.RESOLVED_WARNING_ISSUED,
                resolution = resolutionNotes,
                resolvedAt = now()
            )
        )
        Result.Success(Unit)
    }

    override suspend fun computeTrustScore(transferId: String): Result<Int> = withContext(Dispatchers.IO) {
        val verifications = verificationDao.getByTransferId(transferId)
        val disputes = disputeDao.getByTransferId(transferId)

        var score = 50
        if (verifications.any { it.step == "SELLER_INIT" && it.status == "APPROVED" }) score += 15
        if (verifications.any { it.step == "BUYER_VERIFY" && it.status == "APPROVED" }) score += 20
        if (verifications.any { it.step == "PLATFORM_REVIEW" && it.status == "APPROVED" }) score += 10
        score -= disputes.size * 5

        Result.Success(score.coerceIn(0, 100))
    }

    override suspend fun generateDocumentation(transferId: String): Result<String> = withContext(Dispatchers.IO) {
        val transfer = transferDao.getById(transferId)
            ?: return@withContext Result.Error(IllegalStateException("Transfer not found"))

        val payload = mapOf(
            "transfer" to transfer,
            "verifications" to verificationDao.getByTransferId(transferId),
            "disputes" to disputeDao.getByTransferId(transferId),
            "auditLogs" to auditLogDao.getByRef(transferId)
        )
        Result.Success(gson.toJson(payload))
    }

    override suspend fun retryFailedTransfer(transferId: String): Result<Unit> = withContext(Dispatchers.IO) {
        val transfer = transferDao.getById(transferId)
            ?: return@withContext Result.Error(IllegalStateException("Transfer not found"))

        if (transfer.status != "FAILED") {
            return@withContext Result.Error(IllegalStateException("Transfer is not in FAILED state"))
        }

        transferDao.upsert(transfer.copy(status = "PENDING", updatedAt = now(), lastModifiedAt = now(), dirty = true))
        Result.Success(Unit)
    }
}
