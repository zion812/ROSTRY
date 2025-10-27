package com.rio.rostry.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.rio.rostry.data.database.dao.*
import com.rio.rostry.data.database.entity.FarmerDashboardSnapshotEntity
import com.rio.rostry.session.SessionManager
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import java.util.Calendar
import java.util.UUID
import java.util.concurrent.TimeUnit

@HiltWorker
class FarmPerformanceWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val vaccinationRecordDao: VaccinationRecordDao,
    private val growthRecordDao: GrowthRecordDao,
    private val mortalityRecordDao: MortalityRecordDao,
    private val hatchingBatchDao: HatchingBatchDao,
    private val hatchingLogDao: HatchingLogDao,
    private val quarantineRecordDao: QuarantineRecordDao,
    private val orderDao: OrderDao,
    private val productDao: ProductDao,
    private val farmerDashboardSnapshotDao: FarmerDashboardSnapshotDao,
    private val firebaseAuth: com.google.firebase.auth.FirebaseAuth,
    private val dailyLogDao: DailyLogDao,
    private val transferDao: TransferDao
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val farmerId = firebaseAuth.currentUser?.uid ?: return@withContext Result.failure()
            
            // Calculate current week's start/end (Monday 00:00 to Sunday 23:59)
            val calendar = Calendar.getInstance()
            calendar.firstDayOfWeek = Calendar.MONDAY
            calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            val weekStart = calendar.timeInMillis
            
            calendar.add(Calendar.DAY_OF_WEEK, 6)
            calendar.set(Calendar.HOUR_OF_DAY, 23)
            calendar.set(Calendar.MINUTE, 59)
            calendar.set(Calendar.SECOND, 59)
            val weekEnd = calendar.timeInMillis
            
            // Query each DAO for counts/aggregates within the week
            val allOrders = orderDao.getOrdersByStatus("COMPLETED").first()
            val weekOrders = allOrders.filter { order ->
                order.createdAt in weekStart..weekEnd
            }
            val revenueInr = weekOrders.sumOf { it.totalAmount }
            val ordersCount = weekOrders.size
            
            // Hatching success rate: hatched / eggs set in week
            val hatchedCount = hatchingLogDao.countHatchedBetweenForFarmer(farmerId, weekStart, weekEnd)
            val eggsSetCount = hatchingLogDao.countEggsSetBetweenForFarmer(farmerId, weekStart, weekEnd)
            val hatchSuccessRate = if (eggsSetCount > 0) hatchedCount.toDouble() / eggsSetCount else 0.0
            
            // Mortality: absolute count and rate (if we can estimate population)
            val deathsCount = mortalityRecordDao.countForFarmerBetween(farmerId, weekStart, weekEnd)
            // Estimate population from active products (birds) for the farmer
            // This is a rough estimate - in production, maintain a proper population tracker
            val estimatedPopulation = productDao.countActiveByOwnerId(farmerId)
            val mortalityRate = if (estimatedPopulation > 0) {
                deathsCount.toDouble() / estimatedPopulation
            } else {
                0.0 // No population to compute rate against
            }
            
            // Vaccination completion rate: administered / scheduled in week
            val scheduledVaccinations = vaccinationRecordDao.countScheduledBetweenForFarmer(farmerId, weekStart, weekEnd)
            val administeredVaccinations = vaccinationRecordDao.countAdministeredBetween(weekStart, weekEnd)
            val vaccinationCompletionRate = if (scheduledVaccinations > 0) 
                administeredVaccinations.toDouble() / scheduledVaccinations else 0.0
            
            // Growth records count in week
            val growthRecordsCount = growthRecordDao.countForFarmerBetween(farmerId, weekStart, weekEnd)
            
            // Quarantine active count
            val quarantineActiveCount = quarantineRecordDao.countActiveForFarmer(farmerId)
            
            // Farm-marketplace bridge: Products ready to list
            // Count products that have growth records and are NOT in active quarantine
            val allGrowthRecords = growthRecordDao.getAllByFarmer(farmerId)
            val productsWithGrowth = allGrowthRecords.map { it.productId }.distinct()
            val activeQuarantineProducts = quarantineRecordDao.getAllActiveForFarmer(farmerId)
                .map { it.productId }
                .toSet()
            val productsReadyToListCount = productsWithGrowth.count { !activeQuarantineProducts.contains(it) }

            // Daily logs aggregation for the week
            val weeklyLogs = dailyLogDao.observeForFarmerBetween(farmerId, weekStart, weekEnd).first()
            val avgFeedKg = weeklyLogs.mapNotNull { it.feedKg }.let { list -> if (list.isNotEmpty()) list.average() else null }
            val medicationUsageCount = weeklyLogs.count { !it.medicationJson.isNullOrBlank() }
            val activityCounts = weeklyLogs.groupBy { it.activityLevel ?: "UNKNOWN" }.mapValues { it.value.size }
            // Compliance: expect ~1 log per bird per day
            val expectedLogs = (estimatedPopulation * 7).coerceAtLeast(1)
            val dailyLogComplianceRate = if (expectedLogs > 0) weeklyLogs.size.toDouble() / expectedLogs else null
            // Suggestions
            val suggestions = mutableListOf<String>()
            if ((dailyLogComplianceRate ?: 1.0) < 0.5) suggestions += "Log daily observations for better insights"
            if (medicationUsageCount > 0) suggestions += "Review medication usage trends"
            val actionSuggestions = if (suggestions.isNotEmpty()) suggestions.joinToString(prefix = "[\"", separator = "\",\"", postfix = "\"]") else null
            
            // Transfer metrics
            val allTransfers = transferDao.getUserTransfersBetween(farmerId, weekStart, weekEnd).first()
            val transfersInitiatedCount = allTransfers.count { it.fromUserId == farmerId }
            val transfersCompletedCount = allTransfers.count { it.status == "COMPLETED" }
            
            // Onboarding count: birds/batches added this week
            val onboardingCount = productDao.getProductsBySeller(farmerId).first().count { it.createdAt in weekStart..weekEnd }
            
            // Compliance score: percentage of products meeting transfer eligibility
            // Simplified: active products not in quarantine
            val activeProductsCount = productDao.countActiveByOwnerId(farmerId)
            val complianceScore = if (activeProductsCount > 0) {
                (activeProductsCount - quarantineActiveCount).toDouble() / activeProductsCount * 100.0
            } else {
                0.0
            }
            
            // Create snapshot
            val snapshot = FarmerDashboardSnapshotEntity(
                snapshotId = UUID.randomUUID().toString(),
                farmerId = farmerId,
                weekStartAt = weekStart,
                weekEndAt = weekEnd,
                revenueInr = revenueInr,
                ordersCount = ordersCount,
                hatchSuccessRate = hatchSuccessRate,
                mortalityRate = mortalityRate,
                deathsCount = deathsCount,
                vaccinationCompletionRate = vaccinationCompletionRate,
                growthRecordsCount = growthRecordsCount,
                quarantineActiveCount = quarantineActiveCount,
                productsReadyToListCount = productsReadyToListCount,
                avgFeedKg = avgFeedKg,
                medicationUsageCount = medicationUsageCount,
                dailyLogComplianceRate = dailyLogComplianceRate,
                actionSuggestions = actionSuggestions,
                transfersInitiatedCount = transfersInitiatedCount,
                transfersCompletedCount = transfersCompletedCount,
                complianceScore = complianceScore,
                onboardingCount = onboardingCount,
                dailyGoalsCompletedCount = 0, // TODO: implement daily goals tracking
                analyticsInsightsCount = 0, // TODO: implement analytics insights count
                createdAt = System.currentTimeMillis(),
                dirty = true
            )
            
            farmerDashboardSnapshotDao.upsert(snapshot)
            
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    companion object {
        private const val UNIQUE_NAME = "FarmPerformanceWorkerWeekly"
        fun schedule(context: Context) {
            val request = PeriodicWorkRequestBuilder<FarmPerformanceWorker>(7, TimeUnit.DAYS)
                .setBackoffCriteria(androidx.work.BackoffPolicy.EXPONENTIAL, 10, TimeUnit.MINUTES)
                .build()
            WorkManager.getInstance(context)
                .enqueueUniquePeriodicWork(UNIQUE_NAME, ExistingPeriodicWorkPolicy.KEEP, request)
        }
    }
}