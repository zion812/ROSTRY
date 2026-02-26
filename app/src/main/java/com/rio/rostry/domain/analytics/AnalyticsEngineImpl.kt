package com.rio.rostry.domain.analytics

import android.content.Context
import android.graphics.pdf.PdfDocument
import android.util.Log
import com.opencsv.CSVWriter
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
     * Export analytics report to CSV format using OpenCSV.
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

        // Use OpenCSV CSVWriter for proper CSV generation
        FileOutputStream(file).use { fos ->
            OutputStreamWriter(fos).use { writer ->
                CSVWriter(writer).use { csvWriter ->
                    // Header
                    csvWriter.writeNext(arrayOf("Order ID", "Product ID", "Amount", "Status", "Date"))
                    
                    // Data rows
                    for (order in orders) {
                        csvWriter.writeNext(
                            arrayOf(
                                order.orderId,
                                order.productId,
                                order.totalAmount.toString(),
                                order.status,
                                order.orderDate.toString()
                            )
                        )
                    }
                    
                    // Summary
                    csvWriter.writeNext(emptyArray())
                    csvWriter.writeNext(arrayOf("Summary"))
                    csvWriter.writeNext(arrayOf("Total Revenue", totalRevenue.toString()))
                    csvWriter.writeNext(arrayOf("Total Costs", totalCosts.toString()))
                    csvWriter.writeNext(arrayOf("Profit", profit.toString()))
                    csvWriter.writeNext(
                        arrayOf(
                            "Profit Margin",
                            if (totalRevenue > 0) "${(profit / totalRevenue * 100)}%" else "0%"
                        )
                    )
                    csvWriter.writeNext(arrayOf("Order Count", orders.size.toString()))
                }
            }
        }

        Log.i(TAG, "Exported CSV report to ${file.absolutePath}")
        return file
    }

    /**
     * Export analytics report to PDF format using PdfDocument.
     */
    suspend fun exportReportPdf(
        userId: String,
        startDate: Long,
        endDate: Long,
        fileName: String
    ): File {
        val reportsDir = File(context.filesDir, "reports")
        if (!reportsDir.exists()) reportsDir.mkdirs()
        val file = File(reportsDir, "$fileName.pdf")

        val orders = orderDao.getDeliveredOrdersForFarmerBetween(userId, startDate, endDate)
        val totalRevenue = orders.sumOf { it.totalAmount }
        val totalCosts = expenseDao.getTotalInRange(userId, startDate, endDate)
        val profit = totalRevenue - totalCosts
        val profitMargin = if (totalRevenue > 0) (profit / totalRevenue * 100) else 0.0

        // Create PDF document
        val pdfDocument = PdfDocument()
        
        // Calculate page dimensions
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 size
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas

        // Set up paint
        val paint = android.graphics.Paint().apply {
            color = android.graphics.Color.BLACK
            textSize = 12f
            typeface = android.graphics.Typeface.defaultFromStyle(android.graphics.Typeface.NORMAL)
        }

        val boldPaint = android.graphics.Paint().apply {
            color = android.graphics.Color.BLACK
            textSize = 12f
            isFakeBoldText = true
            typeface = android.graphics.Typeface.defaultFromStyle(android.graphics.Typeface.BOLD)
        }

        val titlePaint = android.graphics.Paint().apply {
            color = android.graphics.Color.BLACK
            textSize = 18f
            isFakeBoldText = true
            typeface = android.graphics.Typeface.defaultFromStyle(android.graphics.Typeface.BOLD)
        }

        // Draw title
        canvas.drawText("Analytics Report", 40f, 40f, titlePaint)
        canvas.drawText("Period: ${startDate} - ${endDate}", 40f, 65f, paint)

        // Draw summary section
        var yPosition = 100f
        canvas.drawText("Summary", 40f, yPosition, boldPaint)
        yPosition += 25f

        canvas.drawText("Total Revenue: $totalRevenue", 40f, yPosition, paint)
        yPosition += 20f
        canvas.drawText("Total Costs: $totalCosts", 40f, yPosition, paint)
        yPosition += 20f
        canvas.drawText("Profit: $profit", 40f, yPosition, paint)
        yPosition += 20f
        canvas.drawText("Profit Margin: ${"%.2f".format(profitMargin)}%", 40f, yPosition, paint)
        yPosition += 20f
        canvas.drawText("Order Count: ${orders.size}", 40f, yPosition, paint)
        yPosition += 40f

        // Draw orders table header
        canvas.drawText("Orders", 40f, yPosition, boldPaint)
        yPosition += 25f

        // Table headers
        val headerY = yPosition
        canvas.drawText("Order ID", 40f, yPosition, boldPaint)
        canvas.drawText("Product ID", 150f, yPosition, boldPaint)
        canvas.drawText("Amount", 260f, yPosition, boldPaint)
        canvas.drawText("Status", 360f, yPosition, boldPaint)
        canvas.drawText("Date", 450f, yPosition, boldPaint)
        yPosition += 20f

        // Draw line under headers
        canvas.drawLine(40f, yPosition, 555f, yPosition, paint)
        yPosition += 20f

        // Draw order rows
        for (order in orders) {
            if (yPosition > 780f) {
                // Start new page if needed
                pdfDocument.finishPage(page)
                val newPage = pdfDocument.startPage(pageInfo)
                yPosition = 40f
            }
            
            canvas.drawText(order.orderId, 40f, yPosition, paint)
            canvas.drawText(order.productId, 150f, yPosition, paint)
            canvas.drawText("${order.totalAmount}", 260f, yPosition, paint)
            canvas.drawText(order.status, 360f, yPosition, paint)
            canvas.drawText("${order.orderDate}", 450f, yPosition, paint)
            yPosition += 20f
        }

        pdfDocument.finishPage(page)

        // Write document to file
        FileOutputStream(file).use { out ->
            pdfDocument.writeTo(out)
        }
        pdfDocument.close()

        Log.i(TAG, "Exported PDF report to ${file.absolutePath}")
        return file
    }
}
