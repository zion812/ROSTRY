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
}

@Singleton
class AnalyticsRepositoryImpl @Inject constructor(
    private val analyticsDao: AnalyticsDao,
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
}
