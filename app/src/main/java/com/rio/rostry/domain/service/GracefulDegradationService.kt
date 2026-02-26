package com.rio.rostry.domain.service

import android.util.Log
import com.rio.rostry.data.database.dao.ProductDao
import com.rio.rostry.data.database.dao.ProfitabilityMetricsDao
import com.rio.rostry.data.database.entity.ProductEntity
import com.rio.rostry.domain.manager.DegradationManager
import com.rio.rostry.domain.manager.DegradedService
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Degradation event for audit logging
 */
data class DegradationEvent(
    val service: DegradedService,
    val action: String, // DEGRADED, RECOVERED
    val timestamp: Long,
    val details: String
)

/**
 * Graceful Degradation Service.
 *
 * Provides fallback behavior when services are unavailable:
 * - Recommendation service → display popular products
 * - Analytics service → display cached metrics
 * - Media service → allow text-only product creation
 * - Notification service → queue notifications for later delivery
 *
 * Also manages:
 * - UI degradation banners
 * - Automatic recovery detection
 * - Degradation event logging
 * - Core workflow prioritization
 *
 * Requirements: 21.1-21.8
 */
@Singleton
class GracefulDegradationService @Inject constructor(
    private val degradationManager: DegradationManager,
    private val productDao: ProductDao,
    private val profitabilityDao: ProfitabilityMetricsDao
) {
    companion object {
        private const val TAG = "GracefulDegradation"
    }

    private val degradationEvents = mutableListOf<DegradationEvent>()
    private val queuedNotifications = mutableListOf<QueuedNotification>()

    /**
     * Observe degraded services for UI banner display.
     */
    val degradedServices: StateFlow<Set<DegradedService>>
        get() = degradationManager.degradedServices

    /**
     * Report a service as degraded and log the event.
     */
    fun reportServiceDegraded(service: DegradedService, reason: String = "") {
        degradationManager.reportDegraded(service)
        val event = DegradationEvent(
            service = service,
            action = "DEGRADED",
            timestamp = System.currentTimeMillis(),
            details = reason
        )
        degradationEvents.add(event)
        Log.w(TAG, "Service DEGRADED: $service - $reason")
    }

    /**
     * Report a service as recovered and log the event.
     */
    fun reportServiceRecovered(service: DegradedService) {
        degradationManager.reportRecovered(service)
        val event = DegradationEvent(
            service = service,
            action = "RECOVERED",
            timestamp = System.currentTimeMillis(),
            details = "Service recovered"
        )
        degradationEvents.add(event)
        Log.i(TAG, "Service RECOVERED: $service")
    }

    /**
     * Fallback: Get popular products when recommendation service is unavailable.
     */
    suspend fun getPopularProductsFallback(limit: Int = 10): List<ProductEntity> {
        if (!degradationManager.isDegraded(DegradedService.RECOMMENDATION)) {
            return emptyList() // Service is healthy, use normal path
        }

        Log.d(TAG, "Using popular products fallback (recommendation service degraded)")
        return productDao.getAllProductsSnapshot()
            .filter { !it.isDeleted }
            .sortedByDescending { it.updatedAt }
            .take(limit)
    }

    /**
     * Fallback: Get cached metrics when analytics service is unavailable.
     */
    suspend fun getCachedMetricsFallback(userId: String): CachedDashboardMetrics? {
        if (!degradationManager.isDegraded(DegradedService.ANALYTICS)) {
            return null // Service is healthy
        }

        Log.d(TAG, "Using cached metrics fallback (analytics service degraded)")
        val latestMetrics = profitabilityDao.getLatestMetrics(userId, "USER")
        return latestMetrics?.let {
            CachedDashboardMetrics(
                revenue = it.revenue,
                costs = it.costs,
                profit = it.profit,
                profitMargin = it.profitMargin,
                orderCount = it.orderCount,
                calculatedAt = it.calculatedAt,
                isStale = true
            )
        }
    }

    /**
     * Check if text-only product creation should be offered.
     */
    fun isMediaServiceDegraded(): Boolean {
        return degradationManager.isDegraded(DegradedService.MEDIA_UPLOAD)
    }

    /**
     * Queue a notification for later delivery when notification service is unavailable.
     */
    fun queueNotification(userId: String, title: String, body: String, type: String) {
        if (!degradationManager.isDegraded(DegradedService.NOTIFICATIONS)) {
            return // Service is healthy, don't queue
        }

        queuedNotifications.add(
            QueuedNotification(
                userId = userId,
                title = title,
                body = body,
                type = type,
                queuedAt = System.currentTimeMillis()
            )
        )
        Log.d(TAG, "Queued notification for user $userId (notification service degraded)")
    }

    /**
     * Get queued notifications for delivery when service recovers.
     */
    fun getQueuedNotifications(): List<QueuedNotification> = queuedNotifications.toList()

    /**
     * Clear queued notifications after successful delivery.
     */
    fun clearDeliveredNotifications(deliveredIds: Set<String>) {
        queuedNotifications.removeAll { it.id in deliveredIds }
    }

    /**
     * Get degradation event log for audit.
     */
    fun getDegradationEvents(): List<DegradationEvent> = degradationEvents.toList()

    /**
     * Get currently affected services for UI banner display.
     */
    fun getAffectedServicesDescription(): List<String> {
        return degradationManager.degradedServices.value.map { service ->
            when (service) {
                DegradedService.RECOMMENDATION -> "Product recommendations may be limited"
                DegradedService.ANALYTICS -> "Analytics showing cached data"
                DegradedService.MEDIA_UPLOAD -> "Image upload temporarily unavailable"
                DegradedService.NOTIFICATIONS -> "Notifications may be delayed"
                DegradedService.NETWORK -> "Network connectivity issues detected"
            }
        }
    }

    /**
     * Check if core workflows should be prioritized over auxiliary features.
     * Core: product CRUD, orders, transfers
     * Auxiliary: recommendations, analytics, notifications
     */
    fun shouldPrioritizeCoreWorkflows(): Boolean {
        val degradedCount = degradationManager.degradedServices.value.size
        return degradedCount >= 2 // If 2+ services are degraded, prioritize core
    }
}

/**
 * Cached dashboard metrics (potentially stale)
 */
data class CachedDashboardMetrics(
    val revenue: Double,
    val costs: Double,
    val profit: Double,
    val profitMargin: Double,
    val orderCount: Int,
    val calculatedAt: Long,
    val isStale: Boolean = false
)

/**
 * Queued notification for deferred delivery
 */
data class QueuedNotification(
    val id: String = java.util.UUID.randomUUID().toString(),
    val userId: String,
    val title: String,
    val body: String,
    val type: String,
    val queuedAt: Long
)
