package com.rio.rostry.data.monitoring.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.rio.rostry.core.model.FarmAlert
import com.rio.rostry.domain.monitoring.repository.FarmAlertRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of FarmAlertRepository using Firebase Firestore.
 * 
 * Phase 2: Domain and Data Decoupling
 * Requirement 4.2 - Data modules provide concrete implementations
 */
@Singleton
class FarmAlertRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : FarmAlertRepository {

    private val alertsCollection = firestore.collection("farm_alerts")

    override fun observeUnread(farmerId: String): Flow<List<FarmAlert>> = callbackFlow {
        val listener = alertsCollection
            .whereEqualTo("farmerId", farmerId)
            .whereEqualTo("isRead", false)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val alerts = snapshot?.documents?.mapNotNull {
                    it.toObject(FarmAlert::class.java)
                } ?: emptyList()
                trySend(alerts)
            }
        awaitClose { listener.remove() }
    }

    override suspend fun countUnread(farmerId: String): Int {
        return try {
            val snapshot = alertsCollection
                .whereEqualTo("farmerId", farmerId)
                .whereEqualTo("isRead", false)
                .get()
                .await()
            snapshot.size()
        } catch (e: Exception) {
            0
        }
    }

    override suspend fun insert(alert: FarmAlert) {
        try {
            alertsCollection.document(alert.alertId).set(alert).await()
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun markRead(alertId: String) {
        try {
            alertsCollection.document(alertId)
                .update("isRead", true)
                .await()
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun cleanupExpired() {
        try {
            val expiryThreshold = System.currentTimeMillis() - (30L * 24 * 60 * 60 * 1000) // 30 days
            val snapshot = alertsCollection
                .whereLessThan("createdAt", expiryThreshold)
                .get()
                .await()
            
            snapshot.documents.forEach { document ->
                document.reference.delete()
            }
        } catch (e: Exception) {
            // Log error but don't throw - cleanup is best effort
        }
    }
}
