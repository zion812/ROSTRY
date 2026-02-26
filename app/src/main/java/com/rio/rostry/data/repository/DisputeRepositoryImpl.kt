package com.rio.rostry.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.rio.rostry.data.base.BaseRepository
import com.rio.rostry.data.database.dao.AuditLogDao
import com.rio.rostry.data.database.entity.AuditLogEntity
import com.rio.rostry.data.database.entity.DisputeEntity
import com.rio.rostry.data.database.entity.DisputeStatus
import com.rio.rostry.notifications.IntelligentNotificationService
import com.rio.rostry.notifications.OrderEventType
import com.rio.rostry.utils.Resource
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.channels.awaitClose
import java.util.UUID

@Singleton
class DisputeRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auditLogDao: AuditLogDao,
    private val notificationService: IntelligentNotificationService,
    private val gson: Gson
) : BaseRepository(), DisputeRepository {

    private val disputesCollection = firestore.collection("disputes")

    override suspend fun createDispute(dispute: DisputeEntity): Resource<Unit> = safeCall {
        disputesCollection.document(dispute.disputeId).set(dispute).await()
        Unit
    }.firstResult() // Reusing the helper pattern or would implement properly in Base

    // Simplified helper since BaseRepository usually returns Flow
    private suspend fun <T> Flow<Resource<T>>.firstResult(): Resource<T> {
         var result: Resource<T> = Resource.Loading()
         this.collect { 
             if (it !is Resource.Loading<*>) {
                 result = it
                 return@collect 
             }
         }
         return result
    }

    override fun getDisputesForUser(userId: String): Flow<Resource<List<DisputeEntity>>> = callbackFlow {
        val listener = disputesCollection
            .whereEqualTo("reporterId", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Resource.Error(error.message ?: "Unknown error"))
                    return@addSnapshotListener
                }
                val disputes = snapshot?.toObjects(DisputeEntity::class.java) ?: emptyList()
                trySend(Resource.Success(disputes))
            }
        awaitClose { listener.remove() }
    }

    override fun getAllOpenDisputes(): Flow<Resource<List<DisputeEntity>>> = callbackFlow {
        val listener = disputesCollection
            .whereIn("status", listOf(DisputeStatus.OPEN.name, DisputeStatus.UNDER_REVIEW.name))
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Resource.Error(error.message ?: "Unknown error"))
                    return@addSnapshotListener
                }
                val disputes = snapshot?.toObjects(DisputeEntity::class.java) ?: emptyList()
                trySend(Resource.Success(disputes))
            }
        awaitClose { listener.remove() }
    }

    override suspend fun resolveDispute(
        disputeId: String,
        status: DisputeStatus,
        resolution: String,
        adminId: String
    ): Resource<Unit> = safeCall {
        val now = System.currentTimeMillis()
        val updates = mapOf(
            "status" to status.name,
            "resolution" to resolution,
            "resolvedByAdminId" to adminId,
            "resolvedAt" to now
        )
        disputesCollection.document(disputeId).update(updates).await()
        
        // Get dispute details for notification and audit
        val disputeSnapshot = disputesCollection.document(disputeId).get().await()
        val dispute = disputeSnapshot.toObject(DisputeEntity::class.java)
        
        // AUDIT LOG: Insert audit log for dispute resolution
        auditLogDao.insert(
            AuditLogEntity(
                logId = UUID.randomUUID().toString(),
                type = "DISPUTE_RESOLVED",
                refId = disputeId,
                action = "RESOLVE_DISPUTE",
                actorUserId = adminId,
                detailsJson = gson.toJson(
                    mapOf(
                        "disputeId" to disputeId,
                        "status" to status.name,
                        "resolution" to resolution,
                        "adminId" to adminId,
                        "reporterId" to (dispute?.reporterId ?: "unknown"),
                        "sellerId" to (dispute?.reportedUserId ?: "unknown")
                    )
                ),
                createdAt = now
            )
        )
        
        // Notify involved parties
        dispute?.let { d ->
            // Notify reporter
            notificationService.notifyOrderUpdate(
                orderId = d.transferId.ifBlank { disputeId },
                status = "DISPUTE_RESOLVED",
                title = "Dispute Resolved",
                message = "Your dispute has been resolved: $resolution"
            )
            
            // Notify seller
            notificationService.onOrderStatusChanged(
                orderId = d.transferId.ifBlank { disputeId },
                userId = d.reportedUserId,
                oldStatus = "DISPUTED",
                newStatus = status.name,
                title = "Dispute Resolved",
                message = "The dispute for transfer ${d.transferId} has been resolved: $resolution"
            )
        }
        
        Unit
    }.firstResult()

    /**
     * Respond to a dispute (seller response)
     */
    override suspend fun respondToDispute(
        disputeId: String,
        sellerId: String,
        evidence: String
    ): Resource<Unit> = safeCall {
        val now = System.currentTimeMillis()
        val updates = mapOf(
            "sellerResponse" to evidence,
            "sellerRespondedAt" to now,
            "status" to DisputeStatus.UNDER_REVIEW.name
        )
        disputesCollection.document(disputeId).update(updates).await()
        
        // Get dispute details for notification
        val disputeSnapshot = disputesCollection.document(disputeId).get().await()
        val dispute = disputeSnapshot.toObject(DisputeEntity::class.java)
        
        // AUDIT LOG: Insert audit log for seller response
        auditLogDao.insert(
            AuditLogEntity(
                logId = UUID.randomUUID().toString(),
                type = "DISPUTE_RESPONSE_SUBMITTED",
                refId = disputeId,
                action = "RESPOND_TO_DISPUTE",
                actorUserId = sellerId,
                detailsJson = gson.toJson(
                    mapOf(
                        "disputeId" to disputeId,
                        "sellerId" to sellerId,
                        "evidence" to evidence
                    )
                ),
                createdAt = now
            )
        )
        
        // Notify reporter that seller has responded
        dispute?.let { d ->
            notificationService.notifyOrderUpdate(
                orderId = d.transferId.ifBlank { disputeId },
                status = "DISPUTE_UNDER_REVIEW",
                title = "Seller Responded to Dispute",
                message = "The seller has submitted their response. Your dispute is now under review."
            )
        }
        
        Unit
    }.firstResult()

    override suspend fun getDisputeById(disputeId: String): Resource<DisputeEntity?> = safeCall {
        val snapshot = disputesCollection.document(disputeId).get().await()
        snapshot.toObject(DisputeEntity::class.java)
    }.firstResult()

    override fun getResolvedDisputes(): Flow<Resource<List<DisputeEntity>>> = callbackFlow {
        val listener = disputesCollection
            .whereIn("status", listOf(
                DisputeStatus.RESOLVED_REFUNDED.name, 
                DisputeStatus.RESOLVED_DISMISSED.name,
                DisputeStatus.RESOLVED_WARNING_ISSUED.name
            ))
            .orderBy("resolvedAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .limit(50)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Resource.Error(error.message ?: "Unknown error"))
                    return@addSnapshotListener
                }
                val disputes = snapshot?.toObjects(DisputeEntity::class.java) ?: emptyList()
                trySend(Resource.Success(disputes))
            }
        awaitClose { listener.remove() }
    }
}
