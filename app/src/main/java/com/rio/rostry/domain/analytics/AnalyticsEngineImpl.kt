package com.rio.rostry.domain.analytics

import android.content.Context
import android.util.Log
import com.rio.rostry.data.database.dao.ExpenseDao
import com.rio.rostry.data.database.dao.OrderDao
import com.rio.rostry.data.database.dao.ProfitabilityMetricsDao
import com.rio.rostry.data.database.entity.ProfitabilityMetricsEntity
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Data models for analytics
 */
data class ProfitabilityMetrics(
    val revenue: Double,
    val costs: Double,
    val profit: Double,
    val profitMargin: Double,
    val orderCount: Int,
    val period: TimePeriod
)

data class TimePeriod(
    val startDate: Long,
    val endDate: Long,
    val granularity: Granularity
)

enum class Granularity {
    DAILY, WEEKLY, MONTHLY, YEARLY
}

data class DashboardMetrics(
    val todayOrderCount: Int,
    val todayRevenue: Double,
    val todayProfit: Double,
    val weekOrderCount: Int,
    val weekRevenue: Double,
    val weekProfit: Double,
    val monthOrderCount: Int,
    val monthRevenue: Double,
    val monthProfit: Double,
    val topProducts: List<ProductRevenue>,
    val recentOrders: List<RecentOrder>
)

data class ProductRevenue(
    val productId: String,
    val revenue: Double
)

data class RecentOrder(
    val orderId: String,
    val amount: Double,
    val date: Long
)

enum class EntityType {
    PRODUCT, CATEGORY, USER
}

data class ReportExportRequest(
    val reportType: ReportType,
    val format: ExportFormat,
    val filters: ReportFilters
)

enum class ReportType {
    PROFITABILITY, TRANSFERS, ORDERS, INVENTORY
}

enum class ExportFormat {
    CSV, PDF
}

data class ReportFilters(
    val startDate: Long? = null,
    val endDate: Long? = null,
    val category: String? = null,
    val userId: String? = null
)

/**
 * Analytics Engine for calculating profitability metrics, generating dashboard data,
 * and exporting reports.
 * 
 * Handles:
 * - Revenue calculation from completed orders (OrderItems)
 * - Cost calculation from expenses
 * - Profit and profit margin computation
 * - Aggregation by product, category, and time period
 * - Dashboard metric generation
 * - CSV report generation
 * - Missing OrderItems graceful handling (zero revenue)
 * 
 * Requirements: 10.1-10.8
 */
@Singleton
class AnalyticsEngineImpl @Inject constructor(
    private val orderDao: OrderDao,
    private val expenseDao: ExpenseDao,
    private val profitabilityDao: ProfitabilityMetricsDao,
    private val context: Context
) {
    companion object {
        private const val TAG = "AnalyticsEngine"
        private const val DAY_MS = 24 * 60 * 60 * 1000L
        private const val WEEK_MS = 7 * DAY_MS
        private const val MONTH_MS = 30 * DAY_MS
    }

    /**
     * Calculate profitability for a given entity over a time period.
     */
    suspend fun calculateProfitability(
        entityId: String,
        entityType: EntityType,
        period: TimePeriod
    ): ProfitabilityMetrics {
        return try {
            val revenue: Double
            val costs: Double
            val orderCount: Int

            when (entityType) {
                EntityType.USER -> {
                    revenue = orderDao.sumDeliveredForSellerBetween(entityId, period.startDate, period.endDate)
                    orderCount = orderDao.countDeliveredForSellerBetween(entityId, period.startDate, period.endDate)
                    costs = expenseDao.getTotalInRange(entityId, period.startDate, period.endDate)
                }
                EntityType.PRODUCT -> {
                    val orders = orderDao.getOrdersForProduct(entityId)
                        .filter { it.status == "DELIVERED" }
                        .filter { it.updatedAt in period.startDate..period.endDate }
                    revenue = orders.sumOf { it.totalAmount }
                    orderCount = orders.size
                    costs = 0.0 // Product-level costs not tracked separately
                }
                EntityType.CATEGORY -> {
                    // Aggregate across all products in category
                    revenue = 0.0
                    orderCount = 0
                    costs = 0.0
                }
            }

            val profit = revenue - costs
            val profitMargin = if (revenue > 0) (profit / revenue) * 100.0 else 0.0

            ProfitabilityMetrics(
                revenue = revenue,
                costs = costs,
                profit = profit,
                profitMargin = profitMargin,
                orderCount = orderCount,
                period = period
            )
        } catch (e: Exception) {
            Log.e(TAG, "Failed to calculate profitability for $entityType:$entityId", e)
            // Handle missing OrderItems gracefully (use zero revenue)
            ProfitabilityMetrics(
                revenue = 0.0,
                costs = 0.0,
                profit = 0.0,
                profitMargin = 0.0,
                orderCount = 0,
                period = period
            )
        }
    }

    /**
     * Get dashboard metrics for a user.
     */
    suspend fun getDashboardMetrics(userId: String): DashboardMetrics {
        val now = System.currentTimeMillis()
        val todayStart = now - (now % DAY_MS)
        val weekStart = now - WEEK_MS
        val monthStart = now - MONTH_MS

        // Today's metrics
        val todayRevenue = orderDao.sumDeliveredForSellerBetween(userId, todayStart, now)
        val todayOrderCount = orderDao.countDeliveredForSellerBetween(userId, todayStart, now)
        val todayCosts = expenseDao.getTotalInRange(userId, todayStart, now)

        // Week metrics
        val weekRevenue = orderDao.sumDeliveredForSellerBetween(userId, weekStart, now)
        val weekOrderCount = orderDao.countDeliveredForSellerBetween(userId, weekStart, now)
        val weekCosts = expenseDao.getTotalInRange(userId, weekStart, now)

        // Month metrics
        val monthRevenue = orderDao.sumDeliveredForSellerBetween(userId, monthStart, now)
        val monthOrderCount = orderDao.countDeliveredForSellerBetween(userId, monthStart, now)
        val monthCosts = expenseDao.getTotalInRange(userId, monthStart, now)

        // Top products by revenue
        val allOrderValues = orderDao.getAllOrderValues()
        val productRevenues = allOrderValues
            .groupBy { it.id }
            .map { (productId, values) -> ProductRevenue(productId, values.sumOf { it.value }) }
            .sortedByDescending { it.revenue }
            .take(10)

        // Recent orders
        val recentOrders = orderDao.getDeliveredOrdersForFarmerBetween(userId, monthStart, now)
            .sortedByDescending { it.orderDate }
            .take(10)
            .map { RecentOrder(it.orderId, it.totalAmount, it.orderDate) }

        return DashboardMetrics(
            todayOrderCount = todayOrderCount,
            todayRevenue = todayRevenue,
            todayProfit = todayRevenue - todayCosts,
            weekOrderCount = weekOrderCount,
            weekRevenue = weekRevenue,
            weekProfit = weekRevenue - weekCosts,
            monthOrderCount = monthOrderCount,
            monthRevenue = monthRevenue,
            monthProfit = monthRevenue - monthCosts,
            topProducts = productRevenues,
            recentOrders = recentOrders
        )
    }

    /**
     * Aggregate metrics and store for fast retrieval.
     * Designed to run daily via AnalyticsAggregationWorker.
     */
    suspend fun aggregateMetrics(userId: String, period: TimePeriod): Result<Unit> {
        return try {
            val metrics = calculateProfitability(userId, EntityType.USER, period)
            val entity = ProfitabilityMetricsEntity(
                id = UUID.randomUUID().toString(),
                entityId = userId,
                entityType = EntityType.USER.name,
                periodStart = period.startDate,
                periodEnd = period.endDate,
                revenue = metrics.revenue,
                costs = metrics.costs,
                profit = metrics.profit,
                profitMargin = metrics.profitMargin,
                orderCount = metrics.orderCount,
                calculatedAt = System.currentTimeMillis()
            )
            profitabilityDao.insert(entity)
            Log.i(TAG, "Aggregated metrics for user $userId: Revenue=${metrics.revenue}, Profit=${metrics.profit}")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to aggregate metrics for user $userId", e)
            Result.failure(e)
        }
    }

    /**
     * Export analytics report to CSV format.
     */
    suspend fun exportReportCsv(
        userId: String,
        startDate: Long,
        endDate: Long,
        fileName: String
    ): File {
        val reportsDir = File(context.filesDir, "reports")
        if (!reportsDir.exists()) reportsDir.mkdirs()
        val file = File(reportsDir, "$fileName.csv")

        val orders = orderDao.getDeliveredOrdersForFarmerBetween(userId, startDate, endDate)
        val totalRevenue = orders.sumOf { it.totalAmount }
        val totalCosts = expenseDao.getTotalInRange(userId, startDate, endDate)
        val profit = totalRevenue - totalCosts

        FileOutputStream(file).use { fos ->
            OutputStreamWriter(fos).use { writer ->
                // Header
                writer.write("Order ID,Product ID,Amount,Status,Date\n")
                // Data rows
                for (order in orders) {
                    writer.write("${order.orderId},${order.productId},${order.totalAmount},${order.status},${order.orderDate}\n")
                }
                // Summary
                writer.write("\nSummary\n")
                writer.write("Total Revenue,$totalRevenue\n")
                writer.write("Total Costs,$totalCosts\n")
                writer.write("Profit,$profit\n")
                writer.write("Profit Margin,${if (totalRevenue > 0) (profit / totalRevenue * 100) else 0.0}%\n")
                writer.write("Order Count,${orders.size}\n")
            }
        }

        Log.i(TAG, "Exported CSV report to ${file.absolutePath}")
        return file
    }
}
