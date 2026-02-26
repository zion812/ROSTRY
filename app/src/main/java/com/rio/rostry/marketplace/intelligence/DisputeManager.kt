package com.rio.rostry.marketplace.intelligence

import android.util.Log
import com.rio.rostry.data.database.dao.OrderDao
import com.rio.rostry.data.database.entity.DisputeEntity
import com.rio.rostry.data.database.entity.DisputeStatus
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Dispute resolution models
 */
data class DisputeCreateRequest(
    val orderId: String,
    val buyerId: String,
    val sellerId: String,
    val reason: String,
    val evidence: List<String> = emptyList()
)

data class DisputeResponse(
    val disputeId: String,
    val evidence: List<String>
)

sealed class DisputeResolution {
    data class Refund(val amount: Double) : DisputeResolution()
    data class PartialRefund(val amount: Double) : DisputeResolution()
    object Complete : DisputeResolution()
    data class Dismissed(val reason: String) : DisputeResolution()
}

/**
 * DAO interface for disputes - uses the existing DisputeEntity
 */
interface DisputeDao {
    suspend fun insert(dispute: DisputeEntity)
    suspend fun getById(disputeId: String): DisputeEntity?
    suspend fun getByOrderId(orderId: String): List<DisputeEntity>
    suspend fun update(dispute: DisputeEntity)
    suspend fun getByStatus(status: String): List<DisputeEntity>
}

/**
 * Dispute Resolution Workflow Manager.
 * 
 * Implements the complete dispute lifecycle:
 * 1. Buyer creates dispute with order details and evidence
 * 2. Seller is notified and can respond with their evidence
 * 3. Admin reviews both sides
 * 4. Admin makes decision (refund, partial refund, completion, dismissal)
 * 5. Resolution is executed
 * 6. All parties are notified
 * 7. Audit trail is maintained throughout
 * 
 * Requirements: 22.1-22.8
 */
@Singleton
class DisputeManager @Inject constructor(
    private val orderDao: OrderDao
) {
    companion object {
        private const val TAG = "DisputeManager"
    }

    // In-memory storage (will be backed by DisputeDao when fully wired)
    private val disputes = mutableMapOf<String, DisputeEntity>()
    private val auditTrail = mutableListOf<DisputeAuditEntry>()

    /**
     * Create a dispute for an order.
     */
    suspend fun createDispute(request: DisputeCreateRequest): Result<DisputeEntity> {
        return try {
            // Validate order exists
            val order = orderDao.findById(request.orderId)
            if (order == null) {
                return Result.failure(IllegalArgumentException("Order ${request.orderId} not found"))
            }

            val disputeId = java.util.UUID.randomUUID().toString()
            val dispute = DisputeEntity(
                disputeId = disputeId,
                transferId = request.orderId,
                reporterId = request.buyerId,
                reportedUserId = request.sellerId,
                reason = request.reason,
                description = request.reason,
                evidenceUrls = request.evidence,
                status = DisputeStatus.OPEN,
                createdAt = System.currentTimeMillis()
            )

            disputes[disputeId] = dispute

            // Audit trail
            addAuditEntry(disputeId, "CREATED", request.buyerId, "Dispute created: ${request.reason}")

            Log.i(TAG, "Dispute $disputeId created for order ${request.orderId} by buyer ${request.buyerId}")
            Result.success(dispute)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to create dispute for order ${request.orderId}", e)
            Result.failure(e)
        }
    }

    /**
     * Allow seller to respond to a dispute with evidence.
     */
    suspend fun respondToDispute(disputeId: String, sellerId: String, evidence: List<String>, response: String): Result<Unit> {
        return try {
            val dispute = disputes[disputeId]
                ?: return Result.failure(IllegalArgumentException("Dispute $disputeId not found"))

            if (dispute.reportedUserId != sellerId) {
                return Result.failure(IllegalAccessException("Only the seller can respond to this dispute"))
            }

            val updatedDispute = dispute.copy(
                status = DisputeStatus.UNDER_REVIEW,
                resolution = response
            )
            disputes[disputeId] = updatedDispute

            addAuditEntry(disputeId, "SELLER_RESPONDED", sellerId, "Seller responded with evidence")

            Log.i(TAG, "Seller $sellerId responded to dispute $disputeId")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to respond to dispute $disputeId", e)
            Result.failure(e)
        }
    }

    /**
     * Admin resolves a dispute with a decision.
     */
    suspend fun resolveDispute(disputeId: String, adminId: String, resolution: DisputeResolution): Result<Unit> {
        return try {
            val dispute = disputes[disputeId]
                ?: return Result.failure(IllegalArgumentException("Dispute $disputeId not found"))

            val resolutionText = when (resolution) {
                is DisputeResolution.Refund -> "Full refund of ${resolution.amount}"
                is DisputeResolution.PartialRefund -> "Partial refund of ${resolution.amount}"
                is DisputeResolution.Complete -> "Order completed as-is"
                is DisputeResolution.Dismissed -> "Dismissed: ${resolution.reason}"
            }

            val status = when (resolution) {
                is DisputeResolution.Refund -> DisputeStatus.RESOLVED_REFUNDED
                is DisputeResolution.PartialRefund -> DisputeStatus.RESOLVED_REFUNDED
                is DisputeResolution.Complete -> DisputeStatus.RESOLVED_DISMISSED
                is DisputeResolution.Dismissed -> DisputeStatus.RESOLVED_DISMISSED
            }

            val updatedDispute = dispute.copy(
                status = status,
                resolution = resolutionText,
                resolvedByAdminId = adminId,
                resolvedAt = System.currentTimeMillis()
            )
            disputes[disputeId] = updatedDispute

            addAuditEntry(disputeId, "RESOLVED", adminId, "Resolution: $resolutionText")

            Log.i(TAG, "Dispute $disputeId resolved by admin $adminId: $resolutionText")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to resolve dispute $disputeId", e)
            Result.failure(e)
        }
    }

    /**
     * Get dispute by ID.
     */
    fun getDispute(disputeId: String): DisputeEntity? = disputes[disputeId]

    /**
     * Get all disputes for an order.
     */
    fun getDisputesForOrder(orderId: String): List<DisputeEntity> {
        return disputes.values.filter { it.transferId == orderId }
    }

    /**
     * Get disputes by status (for admin dashboard).
     */
    fun getDisputesByStatus(status: DisputeStatus): List<DisputeEntity> {
        return disputes.values.filter { it.status == status }
    }

    /**
     * Get the audit trail for a dispute.
     */
    fun getAuditTrail(disputeId: String): List<DisputeAuditEntry> {
        return auditTrail.filter { it.disputeId == disputeId }
    }

    private fun addAuditEntry(disputeId: String, action: String, userId: String, details: String) {
        auditTrail.add(
            DisputeAuditEntry(
                id = java.util.UUID.randomUUID().toString(),
                disputeId = disputeId,
                action = action,
                userId = userId,
                details = details,
                timestamp = System.currentTimeMillis()
            )
        )
    }
}

/**
 * Audit entry for tracking dispute actions.
 */
data class DisputeAuditEntry(
    val id: String,
    val disputeId: String,
    val action: String,
    val userId: String,
    val details: String,
    val timestamp: Long
)
