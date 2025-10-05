package com.rio.rostry.data.repository.analytics

import com.rio.rostry.data.database.dao.AnalyticsDao
import com.rio.rostry.data.database.entity.AnalyticsDailyEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

// Simple DTOs for dashboards
data class GeneralDashboard(
    val totalOrders: Int,
    val totalSpend: Double,
    val favoriteProducts: List<String>, // placeholder
    val recentEngagement: Int,
    val suggestions: List<String> = emptyList(),
)

data class FarmerDashboard(
    val revenue: Double,
    val orders: Int,
    val productViews: Int,
    val engagementScore: Double,
    val suggestions: List<String> = emptyList(),
)

data class EnthusiastDashboard(
    val breedingSuccessRate: Double,
    val transfers: Int,
    val engagementScore: Double,
    val suggestions: List<String> = emptyList(),
)

interface AnalyticsRepository {
    fun generalDashboard(userId: String): Flow<GeneralDashboard>
    fun farmerDashboard(userId: String): Flow<FarmerDashboard>
    fun enthusiastDashboard(userId: String): Flow<EnthusiastDashboard>
    
    // Farm-Marketplace Bridge Analytics
    suspend fun trackFarmToMarketplaceListClicked(userId: String, productId: String, source: String)
    suspend fun trackFarmToMarketplacePrefillInitiated(userId: String, productId: String)
    suspend fun trackFarmToMarketplacePrefillSuccess(userId: String, productId: String, fieldsCount: Int)
    suspend fun trackFarmToMarketplaceListingSubmitted(userId: String, productId: String, listingId: String)
    suspend fun trackMarketplaceToFarmDialogShown(userId: String, productId: String)
    suspend fun trackMarketplaceToFarmAdded(userId: String, productId: String, recordsCreated: Int)
    suspend fun trackMarketplaceToFarmDialogDismissed(userId: String, productId: String)
    
    // Comment 7 & Comment 10: Security event tracking
    suspend fun trackSecurityEvent(userId: String, eventType: String, resourceId: String)
}

@Singleton
class AnalyticsRepositoryImpl @Inject constructor(
    private val analyticsDao: AnalyticsDao,
    private val firebaseAnalytics: com.google.firebase.analytics.FirebaseAnalytics
) : AnalyticsRepository {

    private val fmt = DateTimeFormatter.ISO_DATE

    private fun lastNDaysKeys(n: Int): Pair<String, String> {
        val to = LocalDate.now()
        val from = to.minusDays(n.toLong())
        return from.format(fmt) to to.format(fmt)
    }

    override fun generalDashboard(userId: String): Flow<GeneralDashboard> {
        val (from, to) = lastNDaysKeys(30)
        return analyticsDao.streamRange(userId, from, to).map { list ->
            val orders = list.sumOf { it.ordersCount }
            val spend = list.sumOf { it.salesRevenue }
            val engagement = list.sumOf { it.likesCount + it.commentsCount }
            val suggestions = buildList {
                if (engagement < 5) add("Engagement is low. Try posting an update or sharing tips to increase activity.")
                if (orders == 0) add("No orders in 30d. Explore marketplace deals or wishlist popular items.")
            }
            GeneralDashboard(
                totalOrders = orders,
                totalSpend = spend,
                favoriteProducts = emptyList(),
                recentEngagement = engagement,
                suggestions = suggestions
            )
        }
    }

    override fun farmerDashboard(userId: String): Flow<FarmerDashboard> {
        val (from, to) = lastNDaysKeys(30)
        return analyticsDao.streamRange(userId, from, to).map { list ->
            val revenue = list.sumOf { it.salesRevenue }
            val orders = list.sumOf { it.ordersCount }
            val views = list.sumOf { it.productViews }
            val engagement7 = list.takeLast(7).sumOf { it.engagementScore }
            val suggestions = buildList {
                if (views > 50 && orders < 5) add("High product views but low orders. Consider improving descriptions or offering a limited-time discount.")
                if (revenue > 1000.0) add("Great revenue this month. Reinvest in top-performing products.")
                if (engagement7 < 10) add("Engagement is soft. Post farm updates or showcase testimonials.")
            }
            FarmerDashboard(
                revenue = revenue,
                orders = orders,
                productViews = views,
                engagementScore = engagement7,
                suggestions = suggestions
            )
        }
    }

    override fun enthusiastDashboard(userId: String): Flow<EnthusiastDashboard> {
        val (from, to) = lastNDaysKeys(30)
        return analyticsDao.streamRange(userId, from, to).map { list ->
            val success = list.map { it.breedingSuccessRate }.average().takeIf { it.isFinite() } ?: 0.0
            val transfers = list.sumOf { it.transfersCount }
            val engagement7 = list.takeLast(7).sumOf { it.engagementScore }
            val suggestions = buildList {
                if (success < 0.3) add("Breeding success is low. Review pairing and incubation conditions.")
                if (transfers == 0) add("No transfers recorded recently. Explore transfer opportunities or community exchanges.")
                if (engagement7 < 10) add("Share progress of your birds to boost engagement.")
            }
            EnthusiastDashboard(
                breedingSuccessRate = success,
                transfers = transfers,
                engagementScore = engagement7,
                suggestions = suggestions
            )
        }
    }
    
    // Farm-Marketplace Bridge Analytics Implementation
    
    override suspend fun trackFarmToMarketplaceListClicked(userId: String, productId: String, source: String) {
        val bundle = android.os.Bundle().apply {
            putString("user_id", userId)
            putString("product_id", productId)
            putString("source", source) // "growth", "vaccination", "breeding"
            putLong("timestamp", System.currentTimeMillis())
        }
        firebaseAnalytics.logEvent("farm_to_marketplace_list_clicked", bundle)
    }
    
    override suspend fun trackFarmToMarketplacePrefillSuccess(userId: String, productId: String, fieldsCount: Int) {
        val bundle = android.os.Bundle().apply {
            putString("user_id", userId)
            putString("product_id", productId)
            putInt("fields_prefilled", fieldsCount)
            putLong("timestamp", System.currentTimeMillis())
        }
        firebaseAnalytics.logEvent("farm_to_marketplace_prefill_success", bundle)
    }
    
    override suspend fun trackFarmToMarketplaceListingSubmitted(userId: String, productId: String, listingId: String) {
        val bundle = android.os.Bundle().apply {
            putString("user_id", userId)
            putString("product_id", productId)
            putString("listing_id", listingId)
            putBoolean("used_prefill", true)
            putLong("timestamp", System.currentTimeMillis())
        }
        firebaseAnalytics.logEvent("farm_to_marketplace_listing_submitted", bundle)
    }
    
    override suspend fun trackMarketplaceToFarmDialogShown(userId: String, productId: String) {
        val bundle = android.os.Bundle().apply {
            putString("user_id", userId)
            putString("product_id", productId)
            putLong("timestamp", System.currentTimeMillis())
        }
        firebaseAnalytics.logEvent("marketplace_to_farm_dialog_shown", bundle)
    }
    
    override suspend fun trackMarketplaceToFarmAdded(userId: String, productId: String, recordsCreated: Int) {
        val bundle = android.os.Bundle().apply {
            putString("user_id", userId)
            putString("product_id", productId)
            putInt("records_created", recordsCreated) // Growth + vaccinations
            putLong("timestamp", System.currentTimeMillis())
        }
        firebaseAnalytics.logEvent("marketplace_to_farm_added", bundle)
    }
    
    override suspend fun trackFarmToMarketplacePrefillInitiated(userId: String, productId: String) {
        val bundle = android.os.Bundle().apply {
            putString("user_id", userId)
            putString("product_id", productId)
            putLong("timestamp", System.currentTimeMillis())
        }
        firebaseAnalytics.logEvent("farm_to_marketplace_prefill_initiated", bundle)
    }
    
    override suspend fun trackMarketplaceToFarmDialogDismissed(userId: String, productId: String) {
        val bundle = android.os.Bundle().apply {
            putString("user_id", userId)
            putString("product_id", productId)
            putLong("timestamp", System.currentTimeMillis())
        }
        firebaseAnalytics.logEvent("marketplace_to_farm_dismissed", bundle)
    }
    
    override suspend fun trackSecurityEvent(userId: String, eventType: String, resourceId: String) {
        val bundle = android.os.Bundle().apply {
            putString("user_id", userId)
            putString("event_type", eventType)
            putString("resource_id", resourceId)
            putLong("timestamp", System.currentTimeMillis())
        }
        firebaseAnalytics.logEvent("security_event", bundle)
        timber.log.Timber.w("Security event: $eventType by user $userId on resource $resourceId")
    }
}
