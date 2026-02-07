package com.rio.rostry.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.rio.rostry.data.database.dao.FarmAlertDao
import com.rio.rostry.data.database.dao.FarmerDashboardSnapshotDao
import com.rio.rostry.data.database.entity.FarmerDashboardSnapshotEntity
import com.rio.rostry.data.repository.TransactionRepository
import com.rio.rostry.session.CurrentUserProvider
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import timber.log.Timber
import java.util.UUID
import kotlinx.coroutines.flow.firstOrNull

@HiltWorker
class DailyAnalyticsWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val farmerDashboardSnapshotDao: FarmerDashboardSnapshotDao,
    private val analyticsDao: com.rio.rostry.data.database.dao.AnalyticsDao,
    private val transactionRepository: TransactionRepository,
    private val currentUserProvider: CurrentUserProvider,
    private val userRepository: com.rio.rostry.data.repository.UserRepository,
    private val orderRepository: com.rio.rostry.data.repository.OrderManagementRepository
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            Timber.d("Starting daily analytics generation...")
            
            // In a real implementation, we would loop through all active farmers
            // For now, we grab the current user ID if available, or just log
            val userId = currentUserProvider.userIdOrNull()
            
            if (!userId.isNullOrEmpty()) {
                generateSnapshotForFarmer(userId)
            }
            
            Result.success()
        } catch (e: Exception) {
            Timber.e(e, "Failed to generate daily analytics")
            Result.retry()
        }
    }

    private suspend fun generateSnapshotForFarmer(farmerId: String) {
        val now = System.currentTimeMillis()
        val todayDateKey = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US).format(java.util.Date(now))
        
        // 1. Generate Daily Analytics Entity (Admin View)
        val user = userRepository.getCurrentUserSuspend()
        // Check for admin role
        val isAdmin = user?.role == com.rio.rostry.domain.model.UserType.ADMIN
        val isFarmer = user?.role == com.rio.rostry.domain.model.UserType.FARMER

        if (isAdmin) {
             // Generate Global Admin Stats
             val commerceStats = orderRepository.getCommerceStats()
             // val totalUsers = userRepository.countAllUsers() // Not used in Entity currently
             
             val dailyEntity = com.rio.rostry.data.database.entity.AnalyticsDailyEntity(
                id = UUID.randomUUID().toString(),
                userId = farmerId,
                role = "ADMIN",
                dateKey = todayDateKey,
                salesRevenue = commerceStats.revenueThisWeek,
                ordersCount = commerceStats.ordersThisWeek,
                productViews = 0,
                likesCount = 0,
                commentsCount = 0,
                transfersCount = 0,
                breedingSuccessRate = 0.0,
                engagementScore = 0.0,
                createdAt = now
            )
            analyticsDao.upsertDaily(dailyEntity)
        }

        if (isFarmer) {
            // 2. Generate Farmer Dashboard Snapshot
            val transactions = transactionRepository.streamTransactionsByUser(farmerId).firstOrNull() ?: emptyList()
            val revenue = transactions.filter { it.status == "SUCCESS" }.sumOf { it.amount }
            
            val weekStart = now - 7 * 24 * 60 * 60 * 1000
            val weekEnd = now
            
            val snapshot = FarmerDashboardSnapshotEntity(
                snapshotId = UUID.randomUUID().toString(),
                farmerId = farmerId,
                weekStartAt = weekStart,
                weekEndAt = weekEnd,
                revenueInr = revenue, 
                ordersCount = transactions.count(), // Approximation based on transactions
                mortalityRate = 0.0, // Need MortalityRepo
                complianceScore = 85.0 // Mock for now
            )
            
            farmerDashboardSnapshotDao.upsert(snapshot)
            Timber.d("Generated analytics snapshot for farmer: $farmerId")
        }
    }
}
