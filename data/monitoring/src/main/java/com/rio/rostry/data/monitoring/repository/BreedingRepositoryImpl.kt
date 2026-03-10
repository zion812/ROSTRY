package com.rio.rostry.data.monitoring.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.rio.rostry.core.model.BreedingPair
import com.rio.rostry.domain.monitoring.repository.BreedingRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of BreedingRepository using Firebase Firestore.
 * 
 * Phase 2: Domain and Data Decoupling
 * Requirement 4.2 - Data modules provide concrete implementations
 */
@Singleton
class BreedingRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : BreedingRepository {

    private val breedingCollection = firestore.collection("breeding_pairs")

    override fun observeActive(farmerId: String): Flow<List<BreedingPair>> = callbackFlow {
        val listener = breedingCollection
            .whereEqualTo("farmerId", farmerId)
            .whereEqualTo("isActive", true)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val pairs = snapshot?.documents?.mapNotNull {
                    it.toObject(BreedingPair::class.java)
                } ?: emptyList()
                trySend(pairs)
            }
        awaitClose { listener.remove() }
    }

    override fun observeActiveCount(farmerId: String): Flow<Int> = callbackFlow {
        val listener = breedingCollection
            .whereEqualTo("farmerId", farmerId)
            .whereEqualTo("isActive", true)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(0)
                    return@addSnapshotListener
                }
                trySend(snapshot?.size() ?: 0)
            }
        awaitClose { listener.remove() }
    }

    override suspend fun upsert(pair: BreedingPair) {
        try {
            breedingCollection.document(pair.pairId).set(pair).await()
        } catch (e: Exception) {
            // Log error
            throw e
        }
    }

    override suspend fun countActive(farmerId: String): Int {
        return try {
            val snapshot = breedingCollection
                .whereEqualTo("farmerId", farmerId)
                .whereEqualTo("isActive", true)
                .get()
                .await()
            snapshot.size()
        } catch (e: Exception) {
            0
        }
    }

    override suspend fun getById(pairId: String): BreedingPair? {
        return try {
            val document = breedingCollection.document(pairId).get().await()
            document.toObject(BreedingPair::class.java)
        } catch (e: Exception) {
            null
        }
    }
}
