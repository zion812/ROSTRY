package com.rio.rostry.data.repository

import com.rio.rostry.data.base.BaseRepository
import com.rio.rostry.data.database.dao.AnalyticsDao
import com.rio.rostry.data.database.dao.ExpenseDao
import com.rio.rostry.data.database.dao.InventoryItemDao
import com.rio.rostry.data.database.dao.OrderDao
import com.rio.rostry.data.database.dao.PaymentDao
import com.rio.rostry.data.database.dao.UserDao
import com.rio.rostry.data.database.dao.VaccinationRecordDao
import com.rio.rostry.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import timber.log.Timber
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

interface ReportRepository {
    suspend fun generateUserGrowthReport(): Resource<File>
    suspend fun generateCommerceReport(): Resource<File>
    suspend fun generateInventoryReport(): Resource<File>
    suspend fun generateFinancialReport(): Resource<File>
    suspend fun generateEngagementReport(): Resource<File>
    suspend fun generateVeterinaryReport(): Resource<File>
}

@Singleton
class ReportRepositoryImpl @Inject constructor(
    private val userDao: UserDao,
    private val orderDao: OrderDao,
    private val inventoryItemDao: InventoryItemDao,
    private val expenseDao: ExpenseDao,
    private val paymentDao: PaymentDao,
    private val analyticsDao: AnalyticsDao,
    private val vaccinationRecordDao: VaccinationRecordDao,
    @dagger.hilt.android.qualifiers.ApplicationContext private val context: android.content.Context
) : BaseRepository(), ReportRepository {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
    private val reportDateFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US)

    override suspend fun generateUserGrowthReport(): Resource<File> = safeCall {
        Timber.d("Generating user growth report")
        val users = userDao.getAllUsersSnapshot()

        val csvHeader = "User ID,Full Name,Role,Email,Phone,Joined At,Verified,Location\n"
        val csvBody = StringBuilder().apply {
            append(csvHeader)
            users.forEach { user ->
                val joinedAt = user.createdAt?.let { dateFormat.format(it) } ?: "N/A"
                append("${user.userId},\"${user.fullName}\",${user.userType},${user.email ?: ""},${user.phoneNumber ?: ""},$joinedAt,${user.locationVerified},${user.location ?: ""}\n")
            }
        }

        saveCsvFile("user_growth_${reportDateFormat.format(Date())}.csv", csvBody.toString())
    }.filter { it !is Resource.Loading<*> }.firstOrNull() as? Resource<File>
        ?: Resource.Error("Failed to generate user report")

    override suspend fun generateCommerceReport(): Resource<File> = safeCall {
        Timber.d("Generating commerce report")
        val orders = orderDao.getAllOrdersSnapshot(1000)

        val csvHeader = "Order ID,Buyer ID,Seller ID,Total Amount,Status,Date,Payment Method,Items Count\n"
        val csvBody = StringBuilder().apply {
            append(csvHeader)
            orders.forEach { order ->
                val date = dateFormat.format(Date(order.orderDate))
                append("${order.orderId},${order.buyerId},${order.sellerId ?: ""},${order.totalAmount},${order.status},$date,${order.paymentMethod},${order.itemCount}\n")
            }
        }

        saveCsvFile("commerce_${reportDateFormat.format(Date())}.csv", csvBody.toString())
    }.filter { it !is Resource.Loading<*> }.firstOrNull() as? Resource<File>
        ?: Resource.Error("Failed to generate commerce report")

    override suspend fun generateInventoryReport(): Resource<File> = safeCall {
        Timber.d("Generating inventory report")
        val inventory = inventoryItemDao.getAllInventory().map { it }.firstOrNull() ?: emptyList()

        val csvHeader = "Item ID,Name,Category,Quantity,Unit,Farmer ID,Source Asset,Status,Updated At\n"
        val csvBody = StringBuilder().apply {
            append(csvHeader)
            inventory.forEach { item ->
                val updatedAt = item.updatedAt?.let { dateFormat.format(it) } ?: "N/A"
                append("${item.inventoryId},\"${item.name}\",${item.category},${item.quantityAvailable},${item.unit ?: ""},${item.farmerId},${item.sourceAssetId ?: ""},${item.status},$updatedAt\n")
            }
        }

        saveCsvFile("inventory_${reportDateFormat.format(Date())}.csv", csvBody.toString())
    }.filter { it !is Resource.Loading<*> }.firstOrNull() as? Resource<File>
        ?: Resource.Error("Failed to generate inventory report")

    override suspend fun generateFinancialReport(): Resource<File> = safeCall {
        Timber.d("Generating financial report")
        val now = System.currentTimeMillis()
        val thirtyDaysAgo = now - (30L * 24 * 60 * 60 * 1000)

        // Get expenses
        val expenses = expenseDao.observeForDateRange("", thirtyDaysAgo, now).map { it }.firstOrNull() ?: emptyList()

        // Get payments
        val payments = paymentDao.getAllPaymentsSnapshot(1000)

        val csvHeader = "Type,ID,Category,Amount,Date,Description,Status\n"
        val csvBody = StringBuilder().apply {
            append(csvHeader)
            // Expenses
            expenses.forEach { expense ->
                val date = dateFormat.format(Date(expense.expenseDate))
                append("EXPENSE,${expense.expenseId},${expense.category},${expense.amount},$date,\"${expense.description ?: ""},${expense.status}\n")
            }
            // Payments
            payments.forEach { payment ->
                val date = dateFormat.format(Date(payment.paymentDate))
                append("PAYMENT,${payment.paymentId},${payment.paymentMethod},${payment.amount},$date,\"${payment.transactionId ?: ""}\",${payment.status}\n")
            }
        }

        saveCsvFile("financial_${reportDateFormat.format(Date())}.csv", csvBody.toString())
    }.filter { it !is Resource.Loading<*> }.firstOrNull() as? Resource<File>
        ?: Resource.Error("Failed to generate financial report")

    override suspend fun generateEngagementReport(): Resource<File> = safeCall {
        Timber.d("Generating engagement report")
        val now = System.currentTimeMillis()
        val thirtyDaysAgo = now - (30L * 24 * 60 * 60 * 1000)

        val analytics = analyticsDao.streamAllFarmersRange(
            formatDateKey(thirtyDaysAgo),
            formatDateKey(now)
        ).map { it }.firstOrNull() ?: emptyList()

        val csvHeader = "User ID,Date,Active Sessions,Actions,Posts,Likes,Comments,Time Spent (min)\n"
        val csvBody = StringBuilder().apply {
            append(csvHeader)
            analytics.forEach { entry ->
                append("${entry.userId},${entry.dateKey},${entry.activeSessions},${entry.actionsPerformed},${entry.postsCreated},${entry.likesReceived},${entry.commentsReceived},${entry.timeSpentMinutes}\n")
            }
        }

        saveCsvFile("engagement_${reportDateFormat.format(Date())}.csv", csvBody.toString())
    }.filter { it !is Resource.Loading<*> }.firstOrNull() as? Resource<File>
        ?: Resource.Error("Failed to generate engagement report")

    override suspend fun generateVeterinaryReport(): Resource<File> = safeCall {
        Timber.d("Generating veterinary report")
        val records = vaccinationRecordDao.getAllRecordsSnapshot(1000)

        val csvHeader = "Record ID,Bird ID,Flock ID,Vaccine Type,Administered At,Next Due Date,Administered By,Notes\n"
        val csvBody = StringBuilder().apply {
            append(csvHeader)
            records.forEach { record ->
                val administeredAt = record.administeredAt?.let { dateFormat.format(it) } ?: "N/A"
                val nextDue = record.nextDueDate?.let { dateFormat.format(it) } ?: "N/A"
                append("${record.recordId},${record.birdId},${record.flockId ?: ""},${record.vaccineType},$administeredAt,$nextDue,${record.administeredBy ?: ""},\"${record.notes ?: ""}\"\n")
            }
        }

        saveCsvFile("veterinary_${reportDateFormat.format(Date())}.csv", csvBody.toString())
    }.filter { it !is Resource.Loading<*> }.firstOrNull() as? Resource<File>
        ?: Resource.Error("Failed to generate veterinary report")

    private fun saveCsvFile(fileName: String, content: String): File {
        val reportsDir = File(context.cacheDir, "reports")
        if (!reportsDir.exists()) reportsDir.mkdirs()

        val file = File(reportsDir, fileName)
        file.writeText(content)
        Timber.d("Report saved: ${file.absolutePath}")
        return file
    }

    private fun formatDateKey(timestamp: Long): String {
        val sdf = SimpleDateFormat("yyyyMMdd", Locale.US)
        return sdf.format(Date(timestamp))
    }
}

// Extension to get snapshot from Flow
private fun <T> Flow<T>.firstOrNull(): T? = kotlinx.coroutines.flow.firstOrNull()