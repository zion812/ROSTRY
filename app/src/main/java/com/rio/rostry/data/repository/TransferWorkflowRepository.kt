package com.rio.rostry.data.repository

import com.google.gson.Gson
import com.rio.rostry.data.database.dao.AuditLogDao
import com.rio.rostry.data.database.dao.DisputeDao
import com.rio.rostry.data.database.dao.TransferDao
import com.rio.rostry.data.database.dao.TransferVerificationDao
import com.rio.rostry.data.database.entity.AuditLogEntity
import com.rio.rostry.data.database.entity.DisputeEntity
import com.rio.rostry.data.database.entity.TransferEntity
import com.rio.rostry.data.database.entity.TransferVerificationEntity
import com.rio.rostry.utils.Resource
import com.rio.rostry.utils.VerificationUtils
import com.rio.rostry.utils.notif.TransferNotifier
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID

interface TransferWorkflowRepository {
    suspend fun initiate(
        productId: String,
        fromUserId: String,
        toUserId: String,
        amount: Double,
        currency: String,
        sellerPhotoUrl: String?,
        gpsLat: Double?,
        gpsLng: Double?,
        conditionsJson: String? = null,
        timeoutAt: Long? = null,
    ): Resource<String> // returns transferId

    suspend fun appendSellerEvidence(
        transferId: String,
        photoBeforeUrl: String?,
        photoAfterUrl: String?,
        photoBeforeMetaJson: String?,
        photoAfterMetaJson: String?
    ): Resource<Unit>

    suspend fun buyerVerify(
        transferId: String,
        buyerPhotoUrl: String?,
        buyerGpsLat: Double?,
        buyerGpsLng: Double?,
        identityDocType: String?,
        identityDocRef: String?,
        identityDocNumber: String?,
    ): Resource<Unit>

    suspend fun platformApproveIfNeeded(transferId: String): Resource<Unit>

    suspend fun complete(transferId: String): Resource<Unit>

    suspend fun cancel(transferId: String, reason: String?): Resource<Unit>

    suspend fun raiseDispute(transferId: String, raisedByUserId: String, reason: String): Resource<String>

    suspend fun resolveDispute(disputeId: String, resolutionNotes: String, resolved: Boolean): Resource<Unit>

    suspend fun computeTrustScore(transferId: String): Resource<Int>

    suspend fun generateDocumentation(transferId: String): Resource<String> // JSON package for now
}

@Singleton
class TransferWorkflowRepositoryImpl @Inject constructor(
    private val transferDao: TransferDao,
    private val verificationDao: TransferVerificationDao,
    private val disputeDao: DisputeDao,
    private val auditLogDao: AuditLogDao,
    private val notifier: TransferNotifier,
    private val traceabilityRepository: TraceabilityRepository,
) : TransferWorkflowRepository {

    private fun now() = System.currentTimeMillis()

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
        timeoutAt: Long?,
    ): Resource<String> = withContext(Dispatchers.IO) {
        try {
            // Duplicate prevention: reject if a PENDING exists within last 60s
            val pendingCount = transferDao.countRecentPending(productId, fromUserId, toUserId, since = now() - 60_000)
            if (pendingCount > 0) return@withContext Resource.Error("Duplicate transfer request in cooldown window")

            val transferId = UUID.randomUUID().toString()
            val entity = TransferEntity(
                transferId = transferId,
                productId = productId,
                fromUserId = fromUserId,
                toUserId = toUserId,
                orderId = null,
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
                lastModifiedAt = now()
            )
            // Persist transfer
            transferDao.upsert(entity)

            // Log initiation
            auditLogDao.insert(
                AuditLogEntity(
                    logId = UUID.randomUUID().toString(),
                    type = "TRANSFER",
                    refId = transferId,
                    action = "INITIATE",
                    actorUserId = fromUserId,
                    detailsJson = Gson().toJson(entity),
                    createdAt = now()
                )
            )
            // Create initial verification record
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
            notifier.notifyInitiated(transferId, toUserId)
            Resource.Success(transferId)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to initiate transfer")
        }
    }

    override suspend fun appendSellerEvidence(
        transferId: String,
        photoBeforeUrl: String?,
        photoAfterUrl: String?,
        photoBeforeMetaJson: String?,
        photoAfterMetaJson: String?
    ): Resource<Unit> = withContext(Dispatchers.IO) {
        try {
            val ver = TransferVerificationEntity(
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
            verificationDao.upsert(ver)
            auditLogDao.insert(
                AuditLogEntity(
                    logId = UUID.randomUUID().toString(),
                    type = "VERIFICATION",
                    refId = ver.verificationId,
                    action = "APPEND_SELLER_EVIDENCE",
                    actorUserId = null,
                    detailsJson = Gson().toJson(ver),
                    createdAt = now()
                )
            )
            // Notify using existing method
            notifier.notifyInitiated(transferId, null)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to append seller evidence")
        }
    }

    override suspend fun buyerVerify(
        transferId: String,
        buyerPhotoUrl: String?,
        buyerGpsLat: Double?,
        buyerGpsLng: Double?,
        identityDocType: String?,
        identityDocRef: String?,
        identityDocNumber: String?,
    ): Resource<Unit> = withContext(Dispatchers.IO) {
        try {
            val transfer = transferDao.getById(transferId) ?: return@withContext Resource.Error("Transfer not found")
            // GPS within 100m if both coordinates present
            val gpsOk = if (transfer.gpsLat != null && transfer.gpsLng != null && buyerGpsLat != null && buyerGpsLng != null) {
                VerificationUtils.withinRadius(transfer.gpsLat, transfer.gpsLng, buyerGpsLat, buyerGpsLng, 100.0)
            } else true

            val ver = TransferVerificationEntity(
                verificationId = UUID.randomUUID().toString(),
                transferId = transferId,
                step = "BUYER_VERIFY",
                status = if (gpsOk) "APPROVED" else "REJECTED",
                photoAfterUrl = buyerPhotoUrl,
                gpsLat = buyerGpsLat,
                gpsLng = buyerGpsLng,
                identityDocType = identityDocType,
                identityDocRef = identityDocRef,
                notes = identityDocNumber?.let { "identityDocNumber=$it" },
                createdAt = now(),
                updatedAt = now()
            )
            verificationDao.upsert(ver)
            // persist buyer photo url onto transfer for quick access
            transferDao.upsert(transfer.copy(buyerPhotoUrl = buyerPhotoUrl, updatedAt = now(), lastModifiedAt = now()))
            auditLogDao.insert(
                AuditLogEntity(
                    logId = UUID.randomUUID().toString(),
                    type = "VERIFICATION",
                    refId = ver.verificationId,
                    action = "BUYER_VERIFY",
                    actorUserId = null,
                    detailsJson = Gson().toJson(ver),
                    createdAt = now()
                )
            )
            notifier.notifyBuyerVerified(transferId)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Buyer verification failed")
        }
    }

    override suspend fun platformApproveIfNeeded(transferId: String): Resource<Unit> = withContext(Dispatchers.IO) {
        try {
            val t = transferDao.getById(transferId) ?: return@withContext Resource.Error("Transfer not found")
            if (t.amount > 10000.0) {
                // Optional: run breeding record consistency checks via traceability repository
                // For now, assume true; hook: verify lineage consistency or parentage rules as needed
                val ver = TransferVerificationEntity(
                    verificationId = UUID.randomUUID().toString(),
                    transferId = transferId,
                    step = "PLATFORM_REVIEW",
                    status = "APPROVED",
                    createdAt = now(),
                    updatedAt = now()
                )
                verificationDao.upsert(ver)
                auditLogDao.insert(
                    AuditLogEntity(UUID.randomUUID().toString(), "VERIFICATION", ver.verificationId, "PLATFORM_APPROVE", null, null, now())
                )
            }
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Platform approval failed")
        }
    }

    override suspend fun complete(transferId: String): Resource<Unit> = withContext(Dispatchers.IO) {
        try {
            transferDao.updateStatusAndTimestamps(transferId, "COMPLETED", now(), completedAt = now())
            auditLogDao.insert(
                AuditLogEntity(UUID.randomUUID().toString(), "TRANSFER", transferId, "COMPLETE", null, null, now())
            )
            notifier.notifyCompleted(transferId)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to complete transfer")
        }
    }

    override suspend fun cancel(transferId: String, reason: String?): Resource<Unit> = withContext(Dispatchers.IO) {
        try {
            transferDao.updateStatusAndTimestamps(transferId, "CANCELLED", now(), completedAt = null)
            auditLogDao.insert(
                AuditLogEntity(UUID.randomUUID().toString(), "TRANSFER", transferId, "CANCEL", null, reason, now())
            )
            notifier.notifyCancelled(transferId)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to cancel transfer")
        }
    }

    override suspend fun raiseDispute(transferId: String, raisedByUserId: String, reason: String): Resource<String> = withContext(Dispatchers.IO) {
        try {
            val id = UUID.randomUUID().toString()
            val d = DisputeEntity(
                disputeId = id,
                transferId = transferId,
                raisedByUserId = raisedByUserId,
                reason = reason,
                status = "OPEN",
                createdAt = now(),
                updatedAt = now()
            )
            disputeDao.upsert(d)
            auditLogDao.insert(
                AuditLogEntity(UUID.randomUUID().toString(), "DISPUTE", id, "RAISE_DISPUTE", raisedByUserId, reason, now())
            )
            notifier.notifyDisputeOpened(transferId)
            Resource.Success(id)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to raise dispute")
        }
    }

    override suspend fun resolveDispute(disputeId: String, resolutionNotes: String, resolved: Boolean): Resource<Unit> = withContext(Dispatchers.IO) {
        try {
            val existing = disputeDao.getById(disputeId) ?: return@withContext Resource.Error("Dispute not found")
            val updated = existing.copy(
                status = if (resolved) "RESOLVED" else "REJECTED",
                resolutionNotes = resolutionNotes,
                updatedAt = now()
            )
            disputeDao.upsert(updated)
            auditLogDao.insert(
                AuditLogEntity(UUID.randomUUID().toString(), "DISPUTE", disputeId, "RESOLVE_DISPUTE", null, resolutionNotes, now())
            )
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to resolve dispute")
        }
    }

    override suspend fun computeTrustScore(transferId: String): Resource<Int> = withContext(Dispatchers.IO) {
        try {
            val verifications = verificationDao.getByTransfer(transferId)
            val disputes = disputeDao.getByTransfer(transferId)
            var score = 50
            if (verifications.any { it.step == "SELLER_INIT" && it.status == "APPROVED" }) score += 10
            if (verifications.any { it.step == "BUYER_VERIFY" && it.status == "APPROVED" }) score += 15
            if (verifications.any { it.step == "PLATFORM_REVIEW" && it.status == "APPROVED" }) score += 15
            if (disputes.isEmpty()) score += 10 else score -= (disputes.size * 5)
            score = score.coerceIn(0, 100)
            Resource.Success(score)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to compute trust score")
        }
    }

    override suspend fun generateDocumentation(transferId: String): Resource<String> = withContext(Dispatchers.IO) {
        try {
            val t = transferDao.getById(transferId) ?: return@withContext Resource.Error("Transfer not found")
            val ver = verificationDao.getByTransfer(transferId)
            val dsp = disputeDao.getByTransfer(transferId)
            val logs = auditLogDao.getByRef(transferId)
            val payload = mapOf(
                "transfer" to t,
                "verifications" to ver,
                "disputes" to dsp,
                "logs" to logs
            )
            Resource.Success(Gson().toJson(payload))
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to generate documentation")
        }
    }
}
