package com.rio.rostry.data.monitoring.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.rio.rostry.core.model.DashboardSnapshot
import com.rio.rostry.domain.monitoring.repository.FarmerDashboardRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of FarmerDashboardRepository using Firebase Firestore.
 * 
 * Phase 2: Domain and Data Decoupling
 * Requirement 4.2 - Data modules provide concrete implementations
 */
@Singleton
class FarmerDashboardRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : FarmerDashboardRepository {

    private val dashboardCollection = firestore.collection("farmer_dashboards")

    override fun observeLatest(farmerId: String): Flow<DashboardSnapshot?> = callbackFlow {
        val listener = dashboardCollection
            .document(farmerId)
            .collection("snapshots")
            .orderBy("weekStartAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .limit(1)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(null)
                    return@addSnapshotListener
                }
                val dashboardSnapshot = snapshot?.documents?.firstOrNull()?.toObject(DashboardSnapshot::class.java)
                trySend(dashboardSnapshot)
            }
        awaitClose { listener.remove() }
    }

    override suspend fun upsert(snapshot: DashboardSnapshot) {
        try {
            dashboardCollection
                .document(snapshot.farmerId)
                .collection("snapshots")
                .document(snapshot.weekStartAt.toString())
                .set(snapshot)
                .await()
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun getByWeek(farmerId: String, weekStartAt: Long): DashboardSnapshot? {
        return try {
            val document = dashboardCollection
                .document(farmerId)
                .collection("snapshots")
                .document(weekStartAt.toString())
                .get()
                .await()
            document.toObject(DashboardSnapshot::class.java)
        } catch (e: Exception) {
            null
        }
    }
}
