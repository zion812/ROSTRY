package com.rio.rostry.domain.monitoring.repository

import com.rio.rostry.core.model.*
import kotlinx.coroutines.flow.Flow

interface AnalyticsRepository {
    fun generalDashboard(userId: String): Flow<GeneralDashboard>
    fun farmerDashboard(userId: String): Flow<FarmAnalyticsDashboard>
    fun enthusiastDashboard(userId: String): Flow<EnthusiastAnalyticsDashboard>

    // Daily goals tracking methods
    fun observeDailyGoals(userId: String): Flow<List<DailyGoal>>
    suspend fun calculateGoalProgress(userId: String): Map<String, Float>
    fun getActionableInsights(userId: String): Flow<List<ActionableInsight>>

    // Farm-Marketplace Bridge Analytics
    suspend fun trackFarmToMarketplaceListClicked(userId: String, productId: String, source: String)
    suspend fun trackFarmToMarketplacePrefillInitiated(userId: String, productId: String)
    suspend fun trackFarmToMarketplacePrefillSuccess(userId: String, productId: String, fieldsCount: Int)
    suspend fun trackFarmToMarketplaceListingSubmitted(userId: String, productId: String, listingId: String)
    suspend fun trackMarketplaceToFarmDialogShown(userId: String, productId: String)
    suspend fun trackMarketplaceToFarmAdded(userId: String, productId: String, recordsCreated: Int)
    suspend fun trackMarketplaceToFarmDialogDismissed(userId: String, productId: String)

    // Security event tracking
    suspend fun trackSecurityEvent(userId: String, eventType: String, resourceId: String)
    
    // Order Lifecycle Analytics
    suspend fun trackOrderRated(orderId: String, rating: Int)
    suspend fun trackOrderCancelled(orderId: String, reason: String?)
    suspend fun trackOrderAccepted(orderId: String)
    suspend fun trackBillSubmitted(orderId: String, amount: Double)
    suspend fun trackPaymentSlipUploaded(orderId: String)
    suspend fun trackPaymentConfirmed(orderId: String)
}
