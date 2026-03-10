package com.rio.rostry.data.monitoring.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.rio.rostry.core.model.*
import com.rio.rostry.domain.monitoring.repository.AnalyticsRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of AnalyticsRepository using Firebase Firestore.
 * 
 * Phase 2: Domain and Data Decoupling
 * Requirement 4.2 - Data modules provide concrete implementations
 */
@Singleton
class AnalyticsRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : AnalyticsRepository {

    private val analyticsCollection = firestore.collection("analytics")
    private val eventsCollection = firestore.collection("analytics_events")

    override fun generalDashboard(userId: String): Flow<GeneralDashboard> = callbackFlow {
        val listener = analyticsCollection
            .document(userId)
            .collection("general_dashboard")
            .document("current")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val dashboard = snapshot?.toObject(GeneralDashboard::class.java)
                if (dashboard != null) {
                    trySend(dashboard)
                }
            }
        awaitClose { listener.remove() }
    }

    override fun farmerDashboard(userId: String): Flow<FarmAnalyticsDashboard> = callbackFlow {
        val listener = analyticsCollection
            .document(userId)
            .collection("farmer_dashboard")
            .document("current")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val dashboard = snapshot?.toObject(FarmAnalyticsDashboard::class.java)
                if (dashboard != null) {
                    trySend(dashboard)
                }
            }
        awaitClose { listener.remove() }
    }

    override fun enthusiastDashboard(userId: String): Flow<EnthusiastAnalyticsDashboard> = callbackFlow {
        val listener = analyticsCollection
            .document(userId)
            .collection("enthusiast_dashboard")
            .document("current")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val dashboard = snapshot?.toObject(EnthusiastAnalyticsDashboard::class.java)
                if (dashboard != null) {
                    trySend(dashboard)
                }
            }
        awaitClose { listener.remove() }
    }

    override fun observeDailyGoals(userId: String): Flow<List<DailyGoal>> = callbackFlow {
        val listener = analyticsCollection
            .document(userId)
            .collection("daily_goals")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val goals = snapshot?.documents?.mapNotNull {
                    it.toObject(DailyGoal::class.java)
                } ?: emptyList()
                trySend(goals)
            }
        awaitClose { listener.remove() }
    }

    override suspend fun calculateGoalProgress(userId: String): Map<String, Float> {
        return try {
            val snapshot = analyticsCollection
                .document(userId)
                .collection("goal_progress")
                .document("current")
                .get()
                .await()
            
            @Suppress("UNCHECKED_CAST")
            snapshot.data as? Map<String, Float> ?: emptyMap()
        } catch (e: Exception) {
            emptyMap()
        }
    }

    override fun getActionableInsights(userId: String): Flow<List<ActionableInsight>> = callbackFlow {
        val listener = analyticsCollection
            .document(userId)
            .collection("insights")
            .whereEqualTo("isActionable", true)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val insights = snapshot?.documents?.mapNotNull {
                    it.toObject(ActionableInsight::class.java)
                } ?: emptyList()
                trySend(insights)
            }
        awaitClose { listener.remove() }
    }

    override suspend fun trackFarmToMarketplaceListClicked(userId: String, productId: String, source: String) {
        trackEvent(userId, "farm_to_marketplace_list_clicked", mapOf(
            "productId" to productId,
            "source" to source
        ))
    }

    override suspend fun trackFarmToMarketplacePrefillInitiated(userId: String, productId: String) {
        trackEvent(userId, "farm_to_marketplace_prefill_initiated", mapOf(
            "productId" to productId
        ))
    }

    override suspend fun trackFarmToMarketplacePrefillSuccess(userId: String, productId: String, fieldsCount: Int) {
        trackEvent(userId, "farm_to_marketplace_prefill_success", mapOf(
            "productId" to productId,
            "fieldsCount" to fieldsCount
        ))
    }

    override suspend fun trackFarmToMarketplaceListingSubmitted(userId: String, productId: String, listingId: String) {
        trackEvent(userId, "farm_to_marketplace_listing_submitted", mapOf(
            "productId" to productId,
            "listingId" to listingId
        ))
    }

    override suspend fun trackMarketplaceToFarmDialogShown(userId: String, productId: String) {
        trackEvent(userId, "marketplace_to_farm_dialog_shown", mapOf(
            "productId" to productId
        ))
    }

    override suspend fun trackMarketplaceToFarmAdded(userId: String, productId: String, recordsCreated: Int) {
        trackEvent(userId, "marketplace_to_farm_added", mapOf(
            "productId" to productId,
            "recordsCreated" to recordsCreated
        ))
    }

    override suspend fun trackMarketplaceToFarmDialogDismissed(userId: String, productId: String) {
        trackEvent(userId, "marketplace_to_farm_dialog_dismissed", mapOf(
            "productId" to productId
        ))
    }

    override suspend fun trackSecurityEvent(userId: String, eventType: String, resourceId: String) {
        trackEvent(userId, "security_event", mapOf(
            "eventType" to eventType,
            "resourceId" to resourceId
        ))
    }

    override suspend fun trackOrderRated(orderId: String, rating: Int) {
        trackEvent("system", "order_rated", mapOf(
            "orderId" to orderId,
            "rating" to rating
        ))
    }

    override suspend fun trackOrderCancelled(orderId: String, reason: String?) {
        trackEvent("system", "order_cancelled", mapOf(
            "orderId" to orderId,
            "reason" to (reason ?: "")
        ))
    }

    override suspend fun trackOrderAccepted(orderId: String) {
        trackEvent("system", "order_accepted", mapOf(
            "orderId" to orderId
        ))
    }

    override suspend fun trackBillSubmitted(orderId: String, amount: Double) {
        trackEvent("system", "bill_submitted", mapOf(
            "orderId" to orderId,
            "amount" to amount
        ))
    }

    override suspend fun trackPaymentSlipUploaded(orderId: String) {
        trackEvent("system", "payment_slip_uploaded", mapOf(
            "orderId" to orderId
        ))
    }

    override suspend fun trackPaymentConfirmed(orderId: String) {
        trackEvent("system", "payment_confirmed", mapOf(
            "orderId" to orderId
        ))
    }

    private suspend fun trackEvent(userId: String, eventType: String, properties: Map<String, Any>) {
        try {
            val event = hashMapOf(
                "userId" to userId,
                "eventType" to eventType,
                "properties" to properties,
                "timestamp" to System.currentTimeMillis()
            )
            eventsCollection.add(event).await()
        } catch (e: Exception) {
            // Log error but don't throw - analytics should not break app flow
        }
    }
}
