package com.rio.rostry.data.repository

import com.google.gson.Gson
import com.rio.rostry.data.database.dao.AuditLogDao
import com.rio.rostry.data.database.dao.DisputeDao
import com.rio.rostry.data.database.dao.OutboxDao
import com.rio.rostry.data.database.dao.ProductDao
import com.rio.rostry.data.database.dao.QuarantineRecordDao
import com.rio.rostry.data.database.dao.TransferDao
import com.rio.rostry.data.database.dao.TransferVerificationDao
import com.rio.rostry.data.database.entity.AuditLogEntity
import com.rio.rostry.data.database.entity.DisputeEntity
import com.rio.rostry.data.database.entity.OutboxEntity
import com.rio.rostry.data.database.entity.TransferEntity
import com.rio.rostry.data.database.entity.TransferVerificationEntity
import com.rio.rostry.marketplace.validation.ProductValidator
import com.rio.rostry.utils.Resource
import com.rio.rostry.utils.VerificationUtils
import com.rio.rostry.utils.notif.TransferNotifier
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.util.UUID
import com.rio.rostry.domain.rbac.RbacGuard
import com.rio.rostry.domain.model.Permission
import com.rio.rostry.session.CurrentUserProvider
import com.rio.rostry.notifications.IntelligentNotificationService
import com.rio.rostry.notifications.TransferEventType

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
        buyerPhotoMetaJson: String? = null,
        gpsExplanation: String? = null,
    ): Resource<Unit>

    suspend fun platformApproveIfNeeded(transferId: String): Resource<Unit>

    suspend fun platformReview(
        transferId: String,
        approved: Boolean,
        notes: String?,
        actorUserId: String?
    ): Resource<Unit>

    suspend fun complete(transferId: String): Resource<Unit>

    suspend fun cancel(transferId: String, reason: String?): Resource<Unit>

    suspend fun raiseDispute(transferId: String, raisedByUserId: String, reason: String): Resource<String>

    suspend fun resolveDispute(disputeId: String, resolutionNotes: String, resolved: Boolean): Resource<Unit>

    suspend fun computeTrustScore(transferId: String): Resource<Int>

    suspend fun generateDocumentation(transferId: String): Resource<String> // JSON package for now

    suspend fun validateTransferEligibility(
        productId: String,
        fromUserId: String,
        toUserId: String?,
        logOnFailure: Boolean = true
    ): Resource<Unit>

    suspend fun retryFailedTransfer(transferId: String): Resource<Unit>
}

@Singleton
class TransferWorkflowRepositoryImpl @Inject constructor(
    private val transferDao: TransferDao,
    private val verificationDao: TransferVerificationDao,
    private val disputeDao: DisputeDao,
    private val auditLogDao: AuditLogDao,
    private val notifier: TransferNotifier,
    private val intelligentNotificationService: IntelligentNotificationService,
    private val traceabilityRepository: TraceabilityRepository,
    private val productValidator: ProductValidator,
    private val productDao: ProductDao,
    private val quarantineDao: QuarantineRecordDao,
    private val rbacGuard: RbacGuard,
    private val outboxDao: OutboxDao? = null,
    private val gson: Gson = Gson(),
    private val currentUserProvider: CurrentUserProvider,
) : TransferWorkflowRepository {

    private fun now() = System.currentTimeMillis()

    /**
     * Validates transfer eligibility by checking product existence, ownership, lifecycle status, and farm data freshness via traceability report.
     */
    override suspend fun validateTransferEligibility(
        productId: String,
        fromUserId: String,
        toUserId: String?,
        logOnFailure: Boolean
    ): Resource<Unit> {
        // Enforce that the current session user is the transfer actor, unless they have admin privileges
        val currentUserId = currentUserProvider.userIdOrNull()
        val isAdmin = currentUserId?.let { rbacGuard.canAsync(Permission.ADMIN_VERIFICATION) } ?: false
        if (currentUserId != null && currentUserId != fromUserId && !isAdmin) {
            val msg = "You are not authorized to initiate transfers for other users"
            if (logOnFailure) logValidationFailure(productId, "TRANSFER_BLOCKED", fromUserId, listOf(msg))
            return Resource.Error(msg)
        }
        val product = productDao.findById(productId)
        if (product == null) {
            val msg = "Product not found"
            if (logOnFailure) logValidationFailure(productId, "TRANSFER_BLOCKED", fromUserId, listOf(msg))
            return Resource.Error(msg)
        }

        // Check ownership
        if (product.sellerId != fromUserId) {
            val msg = "You do not own this product"
            if (logOnFailure) logValidationFailure(productId, "TRANSFER_BLOCKED", fromUserId, listOf(msg))
            return Resource.Error(msg)
        }

        if (!rbacGuard.canInitiateTransfer()) {
            val msg = "You don't have permission to initiate transfers"
            if (logOnFailure) logValidationFailure(productId, "TRANSFER_BLOCKED", fromUserId, listOf(msg))
            return Resource.Error(msg)
        }

        // Check lifecycle status
        val lifecycle = product.lifecycleStatus?.uppercase()
        when (lifecycle) {
            "QUARANTINE" -> {
                val msg = "Cannot transfer products in quarantine"
                if (logOnFailure) logValidationFailure(productId, "TRANSFER_BLOCKED", fromUserId, listOf(msg))
                return Resource.Error(msg)
            }
            "DECEASED" -> {
                val msg = "Cannot transfer deceased birds"
                if (logOnFailure) logValidationFailure(productId, "TRANSFER_BLOCKED", fromUserId, listOf(msg))
                return Resource.Error(msg)
            }
            "TRANSFERRED" -> {
                val msg = "Product already transferred"
                if (logOnFailure) logValidationFailure(productId, "TRANSFER_BLOCKED", fromUserId, listOf(msg))
                return Resource.Error(msg)
            }
        }


        // Use traceability report for freshness and related checks
        when (val report = traceabilityRepository.getTransferEligibilityReport(productId)) {
            is Resource.Success -> {
                val data = report.data
                val eligible = (data?.get("eligible") as? Boolean) == true
                if (!eligible) {
                    val reasons = (data?.get("reasons") as? List<*>)?.filterIsInstance<String>().orEmpty()
                    val msg = reasons.joinToString("; ").ifBlank { "Not eligible for transfer" }
                    if (logOnFailure) logValidationFailure(productId, "TRANSFER_BLOCKED", fromUserId, reasons)
                    val pName = productDao.findById(productId)?.name ?: "Product"
                    intelligentNotificationService.notifyComplianceIssue(productId, pName)
                    return Resource.Error(msg)
                }
            }
            is Resource.Error -> {
                val msg = report.message ?: "Eligibility report failed"
                if (logOnFailure) logValidationFailure(productId, "TRANSFER_BLOCKED", fromUserId, listOf(msg))
                val pName = productDao.findById(productId)?.name ?: "Product"
                intelligentNotificationService.notifyComplianceIssue(productId, pName)
                return Resource.Error(msg)
            }
            is Resource.Loading -> Unit
        }

        return Resource.Success(Unit)
    }

    /**
     * Logs validation failures in audit logs.
     */
    private suspend fun logValidationFailure(productId: String?, action: String, actorUserId: String?, reasons: List<String>) {
        val log = AuditLogEntity.createValidationFailureLog(
            refId = productId ?: "UNKNOWN_PRODUCT",
            action = action,
            actorUserId = actorUserId,
            reasons = reasons
        )
        auditLogDao.insert(log)
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
        timeoutAt: Long?,
    ): Resource<String> = withContext(Dispatchers.IO) {
        try {
            // Validate transfer eligibility before proceeding (with logOnFailure=false to avoid double logging)
            val eligibility = validateTransferEligibility(productId, fromUserId, toUserId, logOnFailure = false)
            if (eligibility is Resource.Error) {
                logValidationFailure(productId, "TRANSFER_BLOCKED", fromUserId, listOf(eligibility.message ?: "Unknown error"))
                return@withContext Resource.Error(eligibility.message ?: "Not eligible for transfer")
            }

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

            // Queue to outbox for sync
            val outboxEntry = OutboxEntity(
                outboxId = UUID.randomUUID().toString(),
                userId = fromUserId,
                entityType = OutboxEntity.TYPE_TRANSFER,
                entityId = transferId,
                operation = "CREATE",
                payloadJson = gson.toJson(entity),
                createdAt = now(),
                priority = "CRITICAL"
            )
            outboxDao?.insert(outboxEntry)

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
            intelligentNotificationService.notifyTransferEvent(TransferEventType.INITIATED, transferId, "Transfer Initiated", "A transfer has been initiated for your product.")
            Resource.Success(transferId)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to initiate transfer")
        }
    }

    override suspend fun platformReview(
        transferId: String,
        approved: Boolean,
        notes: String?,
        actorUserId: String?
    ): Resource<Unit> = withContext(Dispatchers.IO) {
        try {
            val status = if (approved) "APPROVED" else "REJECTED"
            val ver = TransferVerificationEntity(
                verificationId = UUID.randomUUID().toString(),
                transferId = transferId,
                step = "PLATFORM_REVIEW",
                status = status,
                notes = notes,
                createdAt = now(),
                updatedAt = now()
            )
            verificationDao.upsert(ver)
            auditLogDao.insert(
                AuditLogEntity(
                    logId = UUID.randomUUID().toString(),
                    type = "VERIFICATION",
                    refId = ver.verificationId,
                    action = if (approved) "PLATFORM_APPROVE" else "PLATFORM_REJECT",
                    actorUserId = actorUserId,
                    detailsJson = notes,
                    createdAt = now()
                )
            )
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Platform review failed")
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
            // Notification routed via IntelligentNotificationService elsewhere
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
        buyerPhotoMetaJson: String?,
        gpsExplanation: String?,
    ): Resource<Unit> = withContext(Dispatchers.IO) {
        try {
            val transfer = transferDao.getById(transferId) ?: return@withContext Resource.Error("Transfer not found")
            // Add buyer authorization check
            val currentUserId = currentUserProvider.userIdOrNull()
            val isAuthorized = currentUserId == transfer.toUserId || rbacGuard.canAsync(Permission.ADMIN_VERIFICATION)
            if (!isAuthorized) {
                val auditLog = AuditLogEntity.createValidationFailureLog(
                    refId = transferId,
                    action = "BUYER_VERIFY_UNAUTHORIZED",
                    actorUserId = currentUserId,
                    reasons = listOf("Unauthorized verification attempt")
                )
                auditLogDao.insert(auditLog)
                return@withContext Resource.Error("You are not authorized to verify this transfer")
            }
            // GPS within 100m if both coordinates present
            val gpsOk = if (transfer.gpsLat != null && transfer.gpsLng != null && buyerGpsLat != null && buyerGpsLng != null) {
                VerificationUtils.withinRadius(transfer.gpsLat, transfer.gpsLng, buyerGpsLat, buyerGpsLng, 100.0)
            } else true

            // Farm data consistency check: verify product status hasn't changed since initiation
            val pid = transfer.productId ?: return@withContext Resource.Error("Transfer missing product reference")
            val product = productDao.findById(pid)
            val statusChanged = product?.lifecycleStatus?.uppercase() in listOf("QUARANTINE", "DECEASED")
            if (statusChanged) {
                val ver = TransferVerificationEntity(
                    verificationId = UUID.randomUUID().toString(),
                    transferId = transferId,
                    step = "BUYER_VERIFY",
                    status = "REJECTED",
                    notes = "Product status changed during transfer (now in quarantine/deceased)",
                    createdAt = now(),
                    updatedAt = now()
                )
                verificationDao.upsert(ver)
                auditLogDao.insert(
                    AuditLogEntity(
                        logId = UUID.randomUUID().toString(),
                        type = "VERIFICATION",
                        refId = ver.verificationId,
                        action = "BUYER_VERIFY_REJECTED",
                        actorUserId = null,
                        detailsJson = Gson().toJson(ver),
                        createdAt = now()
                    )
                )
                return@withContext Resource.Error("Product status changed during transfer")
            }

            val ver = TransferVerificationEntity(
                verificationId = UUID.randomUUID().toString(),
                transferId = transferId,
                step = "BUYER_VERIFY",
                status = if (gpsOk) "APPROVED" else "REJECTED",
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
            verificationDao.upsert(ver)
            // persist buyer photo url onto transfer for quick access
            transferDao.upsert(transfer.copy(buyerPhotoUrl = buyerPhotoUrl, updatedAt = now(), lastModifiedAt = now()))

            // Queue update to outbox for sync
            val outboxEntry = OutboxEntity(
                outboxId = UUID.randomUUID().toString(),
                userId = transfer.fromUserId ?: transfer.toUserId ?: "",
                entityType = OutboxEntity.TYPE_TRANSFER,
                entityId = transferId,
                operation = "UPDATE",
                payloadJson = gson.toJson(transfer.copy(buyerPhotoUrl = buyerPhotoUrl, updatedAt = now(), lastModifiedAt = now())),
                createdAt = now(),
                priority = "HIGH"
            )
            outboxDao?.insert(outboxEntry)

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
            intelligentNotificationService.notifyTransferEvent(TransferEventType.VERIFIED, transferId, "Transfer Verified", "The transfer has been verified by the buyer.")
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
            val transfer = transferDao.getById(transferId) ?: return@withContext Resource.Error("Transfer not found")

            // Remote conflict handling is managed by SyncManager during sync. Proceed to final validations.

            // Final validation: ensure product is still eligible
            val pid = transfer.productId ?: return@withContext Resource.Error("Transfer missing product reference")
            val from = transfer.fromUserId ?: return@withContext Resource.Error("Transfer missing seller reference")
            val eligibility = validateTransferEligibility(pid, from, transfer.toUserId, logOnFailure = false)
            if (eligibility is Resource.Error) {
                // Cancel transfer instead of completing
                transferDao.updateStatusAndTimestamps(transferId, "CANCELLED", now(), completedAt = null)
                logValidationFailure(transfer.productId, "TRANSFER_COMPLETE_VALIDATION_FAILED", null, listOf(eligibility.message ?: "Unknown error"))
                auditLogDao.insert(
                    AuditLogEntity(UUID.randomUUID().toString(), "TRANSFER", transferId, "TRANSFER_COMPLETE_VALIDATION_FAILED", null, eligibility.message ?: "Validation failed during completion", now())
                )
                return@withContext Resource.Error(eligibility.message ?: "Transfer cancelled due to validation failure")
            }

            // 1. Update Transfer Status
            transferDao.updateStatusAndTimestamps(transferId, "COMPLETED", now(), completedAt = now())

            // 2. Update Product Ownership (The Core "Digital Asset" Handover)
            val product = productDao.findById(pid)
            if (product != null && transfer.toUserId != null) {
                val updatedProduct = product.copy(
                    sellerId = transfer.toUserId, // Handover ownership
                    status = "private", // Reset to private for the new owner
                    updatedAt = now(),
                    lastModifiedAt = now(),
                    dirty = true
                )
                productDao.upsert(updatedProduct)

                // Queue product update to outbox
                val productOutboxEntry = OutboxEntity(
                    outboxId = UUID.randomUUID().toString(),
                    userId = transfer.toUserId, // New owner context
                    entityType = OutboxEntity.TYPE_PRODUCT,
                    entityId = pid,
                    operation = "UPDATE",
                    payloadJson = gson.toJson(updatedProduct),
                    createdAt = now(),
                    priority = "CRITICAL"
                )
                outboxDao?.insert(productOutboxEntry)
                
                // Log ownership change
                auditLogDao.insert(
                    AuditLogEntity(UUID.randomUUID().toString(), "PRODUCT", pid, "OWNERSHIP_TRANSFER", transfer.toUserId, "Transferred from ${transfer.fromUserId}", now())
                )
            }

            // 3. Queue Transfer completion to outbox for sync
            val outboxEntry = OutboxEntity(
                outboxId = UUID.randomUUID().toString(),
                userId = transfer.fromUserId ?: transfer.toUserId ?: "",
                entityType = OutboxEntity.TYPE_TRANSFER,
                entityId = transferId,
                operation = "UPDATE",
                payloadJson = gson.toJson(transfer.copy(status = "COMPLETED", updatedAt = now(), lastModifiedAt = now(), completedAt = now())),
                createdAt = now(),
                priority = "CRITICAL"
            )
            outboxDao?.insert(outboxEntry)

            auditLogDao.insert(
                AuditLogEntity(UUID.randomUUID().toString(), "TRANSFER", transferId, "COMPLETE", null, gson.toJson(transfer), now())
            )
            intelligentNotificationService.notifyTransferEvent(TransferEventType.COMPLETED, transferId, "Transfer Completed", "The transfer has been successfully completed.")
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to complete transfer")
        }
    }

    override suspend fun cancel(transferId: String, reason: String?): Resource<Unit> = withContext(Dispatchers.IO) {
        try {
            transferDao.updateStatusAndTimestamps(transferId, "CANCELLED", now(), completedAt = null)

            // Queue cancellation to outbox for sync
            val transfer = transferDao.getById(transferId)
            if (transfer != null) {
                val outboxEntry = OutboxEntity(
                    outboxId = UUID.randomUUID().toString(),
                    userId = transfer.fromUserId ?: transfer.toUserId ?: "",
                    entityType = OutboxEntity.TYPE_TRANSFER,
                    entityId = transferId,
                    operation = "UPDATE",
                    payloadJson = gson.toJson(transfer.copy(status = "CANCELLED", updatedAt = now(), lastModifiedAt = now())),
                    createdAt = now(),
                    priority = "HIGH"
                )
                outboxDao?.insert(outboxEntry)
            }

            auditLogDao.insert(
                AuditLogEntity(UUID.randomUUID().toString(), "TRANSFER", transferId, "CANCEL", null, reason, now())
            )
            intelligentNotificationService.notifyTransferEvent(TransferEventType.CANCELLED, transferId, "Transfer Cancelled", reason ?: "Transfer has been cancelled.")
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
                reporterId = raisedByUserId,
                reason = reason,
                status = com.rio.rostry.data.database.entity.DisputeStatus.OPEN,
                createdAt = now()
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
                status = if (resolved) com.rio.rostry.data.database.entity.DisputeStatus.RESOLVED_DISMISSED else com.rio.rostry.data.database.entity.DisputeStatus.RESOLVED_WARNING_ISSUED, // Default mapping, logic may need adjustment
                resolution = resolutionNotes,
                resolvedAt = now()
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
            val verifications = verificationDao.getByTransferId(transferId)
            val disputes = disputeDao.getByTransferId(transferId)
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
            val ver = verificationDao.getByTransferId(transferId)
            val dsp = disputeDao.getByTransferId(transferId)
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

    override suspend fun retryFailedTransfer(transferId: String): Resource<Unit> = withContext(Dispatchers.IO) {
        try {
            val transfer = transferDao.getById(transferId) ?: return@withContext Resource.Error("Transfer not found")
            if (transfer.status != "FAILED") return@withContext Resource.Error("Transfer is not in FAILED state")

            // Re-queue to outbox with reset retry count
            val outboxEntry = OutboxEntity(
                outboxId = UUID.randomUUID().toString(),
                userId = transfer.fromUserId ?: transfer.toUserId ?: "",
                entityType = OutboxEntity.TYPE_TRANSFER,
                entityId = transferId,
                operation = "UPDATE",
                payloadJson = gson.toJson(transfer),
                createdAt = now(),
                retryCount = 0, // Reset retry count
                priority = "CRITICAL"
            )
            outboxDao?.insert(outboxEntry)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to retry transfer")
        }
    }
}
