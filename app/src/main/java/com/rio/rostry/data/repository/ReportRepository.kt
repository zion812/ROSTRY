package com.rio.rostry.data.repository

import com.rio.rostry.data.base.BaseRepository
import com.rio.rostry.data.database.dao.OrderDao
import com.rio.rostry.data.database.dao.UserDao
import com.rio.rostry.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.firstOrNull
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

interface ReportRepository {
    suspend fun generateUserGrowthReport(): Resource<File>
    suspend fun generateCommerceReport(): Resource<File>
}

@Singleton
class ReportRepositoryImpl @Inject constructor(
    private val userDao: UserDao,
    private val orderDao: OrderDao,
    @dagger.hilt.android.qualifiers.ApplicationContext private val context: android.content.Context
) : BaseRepository(), ReportRepository {

    override suspend fun generateUserGrowthReport(): Resource<File> = safeCall {
        // 1. Fetch Data
        val users = userDao.getAllUsersSnapshot() // Using snapshot for consistency vs Flow
        
        // 2. Build CSV Content
        val csvHeader = "User ID,Full Name,Role,Email,Phone,Joined At,Verified\n"
        val csvBody = StringBuilder()
        csvBody.append(csvHeader)
        
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
        
        users.forEach { user ->
            val joinedAt = user.createdAt?.let { dateFormat.format(it) } ?: "N/A"
            csvBody.append("${user.userId},\"${user.fullName}\",${user.userType},${user.email ?: ""},${user.phoneNumber ?: ""},$joinedAt,${user.locationVerified}\n")
        }
        
        // 3. Save to File
        saveCsvFile("user_growth_report.csv", csvBody.toString())
    }.filter { it !is Resource.Loading<*> }.firstOrNull() as? Resource<File> ?: Resource.Error("Failed to generate user report")

    override suspend fun generateCommerceReport(): Resource<File> = safeCall {
        // 1. Fetch Data
        val orders = orderDao.getAllOrdersSnapshot(1000) // Limit to recent 1000 for safety
        
        // 2. Build CSV Content
        val csvHeader = "Order ID,Buyer ID,Total Amount,Status,Date,Payment Method\n"
        val csvBody = StringBuilder()
        csvBody.append(csvHeader)
        
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
        
        orders.forEach { order ->
            val date = dateFormat.format(Date(order.orderDate))
            csvBody.append("${order.orderId},${order.buyerId},${order.totalAmount},${order.status},$date,${order.paymentMethod}\n")
        }
        
         // 3. Save to File
        saveCsvFile("commerce_report.csv", csvBody.toString())
    }.filter { it !is Resource.Loading<*> }.firstOrNull() as? Resource<File> ?: Resource.Error("Failed to generate commerce report")

    private fun saveCsvFile(fileName: String, content: String): File {
        val reportsDir = File(context.cacheDir, "reports")
        if (!reportsDir.exists()) reportsDir.mkdirs()
        
        val file = File(reportsDir, fileName)
        file.writeText(content)
        return file
    }
}
