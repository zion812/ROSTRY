package com.rio.rostry.data.monitoring.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.rio.rostry.core.model.Result
import com.rio.rostry.core.model.HealthRecord
import com.rio.rostry.domain.monitoring.repository.HealthTrackingRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of HealthTrackingRepository using Firebase Firestore.
 * 
 * Phase 2: Domain and Data Decoupling
 * Requirement 4.2 - Data modules provide concrete implementations
 */
@Singleton
class HealthTrackingRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : HealthTrackingRepository {

    private val healthRecordsCollection = firestore.collection("health_records")

    override fun getHealthRecords(assetId: String): Flow<List<HealthRecord>> = callbackFlow {
        val listener = healthRecordsCollection
            .whereEqualTo("assetId", assetId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val records = snapshot?.documents?.mapNotNull {
                    it.toObject(HealthRecord::class.java)
                } ?: emptyList()
                trySend(records)
            }
        awaitClose { listener.remove() }
    }

    override suspend fun createHealthRecord(record: HealthRecord): Result<HealthRecord> {
        return try {
            healthRecordsCollection.document(record.id).set(record).await()
            Result.Success(record)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun updateHealthRecord(record: HealthRecord): Result<Unit> {
        return try {
            healthRecordsCollection.document(record.id).set(record).await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun deleteHealthRecord(recordId: String): Result<Unit> {
        return try {
            healthRecordsCollection.document(recordId).delete().await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override fun getHealthRecordsByType(assetId: String, type: String): Flow<List<HealthRecord>> = callbackFlow {
        val listener = healthRecordsCollection
            .whereEqualTo("assetId", assetId)
            .whereEqualTo("recordType", type)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val records = snapshot?.documents?.mapNotNull {
                    it.toObject(HealthRecord::class.java)
                } ?: emptyList()
                trySend(records)
            }
        awaitClose { listener.remove() }
    }
}
