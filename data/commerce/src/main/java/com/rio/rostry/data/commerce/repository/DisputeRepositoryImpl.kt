package com.rio.rostry.data.commerce.repository

import com.google.firebase.firestore.FirebaseFirestore

import com.rio.rostry.data.database.dao.AuditLogDao
import com.rio.rostry.data.database.entity.AuditLogEntity
import com.rio.rostry.data.database.entity.DisputeEntity
import com.rio.rostry.data.database.entity.DisputeStatus as EntityDisputeStatus
import com.rio.rostry.domain.commerce.service.CommerceNotificationService
import com.rio.rostry.domain.commerce.repository.DisputeRepository
import com.rio.rostry.core.common.Result
import com.rio.rostry.core.model.Dispute
import com.rio.rostry.core.model.DisputeStatus
import com.rio.rostry.data.commerce.mapper.toEntity
import com.rio.rostry.data.commerce.mapper.toDomainModel
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
    private val notificationService: CommerceNotificationService,
    private val gson: Gson
) : DisputeRepository {

    private val disputesCollection = firestore.collection("disputes")

    override suspend fun createDispute(dispute: Dispute): Result<Unit> {
        return try {
            disputesCollection.document(dispute.id).set(dispute.toEntity()).await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override fun getDisputesForUser(userId: String): Flow<Result<List<Dispute>>> = callbackFlow {
        val listener = disputesCollection
            .whereEqualTo("reporterId", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Result.Error(Exception(error.message ?: "Unknown error")))
                    return@addSnapshotListener
                }
                val disputes = snapshot?.toObjects(DisputeEntity::class.java) ?: emptyList()
                trySend(Result.Success(disputes.map { it.toDomainModel() }))
            }
        awaitClose { listener.remove() }
    }

    override fun getAllOpenDisputes(): Flow<Result<List<Dispute>>> = callbackFlow {
        val listener = disputesCollection
            .whereIn("status", listOf(EntityDisputeStatus.OPEN.name, EntityDisputeStatus.UNDER_REVIEW.name))
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Result.Error(Exception(error.message ?: "Unknown error")))
                    return@addSnapshotListener
                }
                val disputes = snapshot?.toObjects(DisputeEntity::class.java) ?: emptyList()
                trySend(Result.Success(disputes.map { it.toDomainModel() }))
            }
        awaitClose { listener.remove() }
    }

    override suspend fun resolveDispute(
        disputeId: String,
        status: DisputeStatus,
        resolution: String,
        adminId: String
    ): Result<Unit> {
        return try {
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
            
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun respondToDispute(
        disputeId: String,
        sellerId: String,
        evidence: String
    ): Result<Unit> {
        return try {
            val now = System.currentTimeMillis()
            val updates = mapOf(
                "sellerResponse" to evidence,
                "sellerRespondedAt" to now,
                "status" to EntityDisputeStatus.UNDER_REVIEW.name
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
            
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun getDisputeById(disputeId: String): Result<Dispute?> {
        return try {
            val snapshot = disputesCollection.document(disputeId).get().await()
            val entity = snapshot.toObject(DisputeEntity::class.java)
            Result.Success(entity?.toDomainModel())
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override fun getResolvedDisputes(): Flow<Result<List<Dispute>>> = callbackFlow {
        val listener = disputesCollection
            .whereIn("status", listOf(
                EntityDisputeStatus.RESOLVED_REFUNDED.name, 
                EntityDisputeStatus.RESOLVED_DISMISSED.name,
                EntityDisputeStatus.RESOLVED_WARNING_ISSUED.name
            ))
            .orderBy("resolvedAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .limit(50)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Result.Error(Exception(error.message ?: "Unknown error")))
                    return@addSnapshotListener
                }
                val disputes = snapshot?.toObjects(DisputeEntity::class.java) ?: emptyList()
                trySend(Result.Success(disputes.map { it.toDomainModel() }))
            }
        awaitClose { listener.remove() }
    }
}
