package com.rio.rostry.data.monitoring.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.rio.rostry.core.model.OnboardingActivity
import com.rio.rostry.core.model.OnboardingStats
import com.rio.rostry.domain.monitoring.repository.FarmOnboardingRepository
import com.rio.rostry.utils.Resource
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of FarmOnboardingRepository using Firebase Firestore.
 * 
 * Phase 2: Domain and Data Decoupling
 * Requirement 4.2 - Data modules provide concrete implementations
 */
@Singleton
class FarmOnboardingRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : FarmOnboardingRepository {

    private val onboardingCollection = firestore.collection("farm_onboarding")
    private val activitiesCollection = firestore.collection("onboarding_activities")

    override suspend fun addProductToFarmMonitoring(
        productId: String,
        farmerId: String,
        healthStatus: String
    ): Resource<List<String>> {
        return try {
            // Create farm monitoring records for the product
            val recordIds = mutableListOf<String>()
            
            // Create a health record
            val healthRecordId = "${productId}_health_${System.currentTimeMillis()}"
            val healthRecord = hashMapOf(
                "id" to healthRecordId,
                "productId" to productId,
                "farmerId" to farmerId,
                "healthStatus" to healthStatus,
                "createdAt" to System.currentTimeMillis()
            )
            firestore.collection("health_records").document(healthRecordId).set(healthRecord).await()
            recordIds.add(healthRecordId)
            
            // Track onboarding activity
            val activityId = "${farmerId}_${productId}_${System.currentTimeMillis()}"
            val activity = hashMapOf(
                "id" to activityId,
                "farmerId" to farmerId,
                "productId" to productId,
                "activityType" to "PRODUCT_ADDED",
                "timestamp" to System.currentTimeMillis()
            )
            activitiesCollection.document(activityId).set(activity).await()
            
            Resource.Success(recordIds)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to add product to farm monitoring")
        }
    }

    override fun observeRecentOnboardingActivity(farmerId: String, days: Int): Flow<List<OnboardingActivity>> = callbackFlow {
        val cutoffTime = System.currentTimeMillis() - (days * 24 * 60 * 60 * 1000L)
        
        val listener = activitiesCollection
            .whereEqualTo("farmerId", farmerId)
            .whereGreaterThan("timestamp", cutoffTime)
            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val activities = snapshot?.documents?.mapNotNull {
                    it.toObject(OnboardingActivity::class.java)
                } ?: emptyList()
                trySend(activities)
            }
        awaitClose { listener.remove() }
    }

    override suspend fun getOnboardingStats(farmerId: String): OnboardingStats {
        return try {
            val document = onboardingCollection
                .document(farmerId)
                .collection("stats")
                .document("current")
                .get()
                .await()
            
            document.toObject(OnboardingStats::class.java) ?: OnboardingStats(
                birdsAddedThisWeek = 0,
                batchesAddedThisWeek = 0,
                tasksGenerated = 0
            )
        } catch (e: Exception) {
            OnboardingStats(
                birdsAddedThisWeek = 0,
                batchesAddedThisWeek = 0,
                tasksGenerated = 0
            )
        }
    }
}
