package com.rio.rostry.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.rio.rostry.data.base.BaseRepository
import com.rio.rostry.data.database.entity.DisputeEntity
import com.rio.rostry.data.database.entity.DisputeStatus
import com.rio.rostry.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.channels.awaitClose

@Singleton
class DisputeRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
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
        val updates = mapOf(
            "status" to status,
            "resolution" to resolution,
            "resolvedByAdminId" to adminId,
            "resolvedAt" to System.currentTimeMillis()
        )
        disputesCollection.document(disputeId).update(updates).await()
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
