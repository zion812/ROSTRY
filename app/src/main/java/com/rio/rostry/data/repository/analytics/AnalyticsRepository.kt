package com.rio.rostry.data.repository.analytics

import com.rio.rostry.data.database.dao.AnalyticsDao
import com.rio.rostry.data.database.dao.DailyLogDao
import com.rio.rostry.data.database.dao.TaskDao
import com.rio.rostry.data.database.dao.VaccinationRecordDao
import com.rio.rostry.data.database.entity.AnalyticsDailyEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.firstOrNull
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

// Data class for daily goals tracking
data class DailyGoal(
    val goalId: String,
    val type: String, // TASKS, DAILY_LOGS, VACCINATIONS
    val title: String,
    val description: String,
    val targetCount: Int,
    val currentCount: Int,
    val progress: Float, // 0.0 to 1.0
    val priority: String, // HIGH, MEDIUM, LOW
    val deepLink: String,
    val iconName: String,
    val completedAt: Long? = null
)

// Data class for actionable insights with deep links
data class ActionableInsight(
    val id: String,
    val title: String,
    val description: String,
    val deepLink: String,
    val priority: String // HIGH, MEDIUM, LOW
)

interface AnalyticsRepository {
    fun generalDashboard(userId: String): Flow<GeneralDashboard>
    fun farmerDashboard(userId: String): Flow<FarmerDashboard>
    fun enthusiastDashboard(userId: String): Flow<EnthusiastDashboard>

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

    // Comment 7 & Comment 10: Security event tracking
    suspend fun trackSecurityEvent(userId: String, eventType: String, resourceId: String)
}

@Singleton
class AnalyticsRepositoryImpl @Inject constructor(
    private val analyticsDao: AnalyticsDao,
    private val firebaseAnalytics: com.google.firebase.analytics.FirebaseAnalytics,
    private val taskDao: TaskDao,
    private val dailyLogDao: DailyLogDao,
    private val vaccinationRecordDao: VaccinationRecordDao,
    private val productDao: com.rio.rostry.data.database.dao.ProductDao,
    private val hatchingBatchDao: com.rio.rostry.data.database.dao.HatchingBatchDao
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
    
    override fun observeDailyGoals(userId: String): Flow<List<DailyGoal>> {
        val now = System.currentTimeMillis()
        val endOfDay = now + 24 * 60 * 60 * 1000L // Approximate end of day
        val startOfDay = now - (now % (24 * 60 * 60 * 1000L)) // Start of today

        // 1. Tasks Goal Flow
        val tasksGoalFlow = combine(
            taskDao.observeDueWindowForFarmer(userId, now, endOfDay).map { it.size },
            taskDao.observeCompletedCountForFarmerBetween(userId, startOfDay, endOfDay)
        ) { dueCount, doneCount ->
            val target = dueCount + doneCount
            // If no tasks at all, maybe default to a small target or hide? 
            // For now, if target is 0, we show 0/0 (100%) or hide. Let's show 0/5 as a fallback if 0 to encourage activity?
            // User wants "data driven". If 0 tasks, goal is met?
            val finalTarget = if (target == 0) 5 else target
            DailyGoal(
                goalId = "tasks_today",
                type = "TASKS",
                title = "Complete Daily Tasks",
                description = "Finish scheduled farm tasks for today",
                targetCount = finalTarget,
                currentCount = doneCount,
                progress = if (finalTarget > 0) (doneCount.toFloat() / finalTarget).coerceAtMost(1f) else 1f,
                priority = "HIGH",
                deepLink = "farmer/tasks",
                iconName = "task_icon"
            )
        }

        // 2. Logs Goal Flow
        val logsGoalFlow = combine(
            dailyLogDao.observeCountForFarmerBetween(userId, startOfDay, endOfDay),
            productDao.observeActiveCountByOwnerId(userId),
            hatchingBatchDao.observeActiveForFarmer(userId, now)
        ) { logsCount, activeProducts, activeBatches ->
            // Goal: Log once for every ~5 birds or 1 per batch?
            // Simple heuristic: 1 log per active batch + 1 log per 10 individual birds?
            // Or just: Target = 1 if any birds exist?
            // Let's say Target = (Active Batches) + (Active Birds / 10). Min 1.
            val derivedTarget = activeBatches + (activeProducts / 10).coerceAtLeast(1)
            DailyGoal(
                goalId = "logs_today",
                type = "DAILY_LOGS",
                title = "Record Daily Logs",
                description = "Log daily updates for your birds/batches",
                targetCount = derivedTarget,
                currentCount = logsCount,
                progress = if (derivedTarget > 0) (logsCount.toFloat() / derivedTarget).coerceAtMost(1f) else 1f,
                priority = "MEDIUM",
                deepLink = "farmer/logs",
                iconName = "log_icon"
            )
        }

        // 3. Vaccinations Goal Flow
        val vaccGoalFlow = combine(
            vaccinationRecordDao.observeDueForFarmer(userId, startOfDay, endOfDay),
            vaccinationRecordDao.observeAdministeredCountForFarmerBetween(userId, startOfDay, endOfDay)
        ) { dueCount, doneCount ->
            val total = dueCount + doneCount
            if (total > 0) {
                DailyGoal(
                    goalId = "vaccinations_today",
                    type = "VACCINATIONS",
                    title = "Administer Vaccinations",
                    description = "Complete due vaccinations for your flock",
                    targetCount = total,
                    currentCount = doneCount,
                    progress = (doneCount.toFloat() / total).coerceAtMost(1f),
                    priority = "HIGH",
                    deepLink = "farmer/vaccinations",
                    iconName = "vaccination_icon"
                )
            } else {
                null // No vaccinations due today
            }
        }

        return combine(tasksGoalFlow, logsGoalFlow, vaccGoalFlow) { taskGoal, logGoal, vaccGoal ->
            listOfNotNull(taskGoal, logGoal, vaccGoal)
        }
    }

    override suspend fun calculateGoalProgress(userId: String): Map<String, Float> {
        val now = System.currentTimeMillis()
        val endOfDay = now + 24 * 60 * 60 * 1000L
        val startOfDay = now - (now % (24 * 60 * 60 * 1000L))

        val tasksCount = taskDao.observeDueWindowForFarmer(userId, now, endOfDay).map { it.size }.firstOrNull() ?: 0
        val logsCount = dailyLogDao.observeCountForFarmerBetween(userId, startOfDay, endOfDay).firstOrNull() ?: 0
        val vaccCount = vaccinationRecordDao.countDueForFarmer(userId, startOfDay, endOfDay)

        return mapOf(
            "TASKS" to (tasksCount / 5f).coerceAtMost(1f),
            "DAILY_LOGS" to (logsCount / 3f).coerceAtMost(1f),
            "VACCINATIONS" to (vaccCount / 2f).coerceAtMost(1f)
        )
    }

    override fun getActionableInsights(userId: String): Flow<List<ActionableInsight>> {
        // Build on existing FarmerDashboard suggestions, adding deep links for actionability
        return farmerDashboard(userId).map { dashboard ->
            dashboard.suggestions.mapIndexed { index, suggestion ->
                val deepLink = when {
                    suggestion.contains("views") -> "farmer/marketplace"
                    suggestion.contains("engagement") -> "farmer/social"
                    suggestion.contains("revenue") -> "farmer/analytics"
                    else -> "farmer/home"
                }
                ActionableInsight(
                    id = "insight_$index",
                    title = "Insight ${index + 1}",
                    description = suggestion,
                    deepLink = deepLink,
                    priority = if (index == 0) "HIGH" else "MEDIUM"
                )
            }
        }
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
