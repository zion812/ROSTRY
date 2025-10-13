package com.rio.rostry.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.rio.rostry.data.database.dao.*
import com.rio.rostry.data.database.entity.AnalyticsDailyEntity
import com.rio.rostry.data.database.entity.FarmerDashboardSnapshotEntity
import com.rio.rostry.data.database.entity.EnthusiastDashboardSnapshotEntity
import com.rio.rostry.domain.model.UserType
import com.rio.rostry.session.CurrentUserProvider
import com.rio.rostry.utils.notif.AnalyticsNotifier
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.UUID

@HiltWorker
class AnalyticsAggregationWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val analyticsDao: AnalyticsDao,
    private val likesDao: LikesDao,
    private val commentsDao: CommentsDao,
    private val currentUserProvider: CurrentUserProvider,
    private val analyticsNotifier: AnalyticsNotifier,
    // Injected DAOs for KPIs
    private val orderDao: OrderDao,
    private val vaccinationRecordDao: VaccinationRecordDao,
    private val productDao: ProductDao,
    private val mortalityRecordDao: MortalityRecordDao,
    private val hatchingLogDao: HatchingLogDao,
    private val breedingPairDao: BreedingPairDao,
    private val farmerDashboardSnapshotDao: FarmerDashboardSnapshotDao,
    private val enthusiastDashboardSnapshotDao: EnthusiastDashboardSnapshotDao,
    private val userDao: UserDao,
) : CoroutineWorker(context.applicationContext, params) {

    override suspend fun doWork(): Result {
        val userId = currentUserProvider.userIdOrNull() ?: return Result.success()

        // Determine role
        val role = run {
            val u = userDao.getUserById(userId).first()
            (u?.userType ?: UserType.GENERAL).name
        }

        // Time windows
        val zone = ZoneId.systemDefault()
        val todayDate = LocalDate.now(zone)
        val todayStart = todayDate.atStartOfDay(zone).toInstant().toEpochMilli()
        val todayEnd = todayDate.plusDays(1).atStartOfDay(zone).toInstant().toEpochMilli() - 1
        val weekStart = todayDate.minusDays(6).atStartOfDay(zone).toInstant().toEpochMilli()

        // Engagement
        val likes = likesDao.countByUser(userId)
        val comments = commentsDao.countByUser(userId)
        val engagement = likes + comments

        // Farmer KPIs (if role is FARMER)
        var salesRevenue = 0.0
        var ordersCount = 0
        var vaccCompliance = 0.0
        var hatchRate = 0.0
        var mortalityRate = 0.0
        var breederEggsPerPair = 0.0

        if (role == UserType.FARMER.name) {
            salesRevenue = orderDao.sumDeliveredForSellerBetween(userId, todayStart, todayEnd)
            ordersCount = orderDao.countDeliveredForSellerBetween(userId, todayStart, todayEnd)

            val dueToday = vaccinationRecordDao.countScheduledBetweenForFarmer(userId, todayStart, todayEnd)
            val completedToday = vaccinationRecordDao.countAdministeredBetweenForFarmer(userId, todayStart, todayEnd)
            vaccCompliance = if (dueToday > 0) completedToday.toDouble() / dueToday.toDouble() else 1.0

            val eggsSet = hatchingLogDao.countEggsSetBetweenForFarmer(userId, todayStart, todayEnd)
            val hatched = hatchingLogDao.countHatchedBetweenForFarmer(userId, todayStart, todayEnd)
            hatchRate = if (eggsSet > 0) hatched.toDouble() / eggsSet.toDouble() else 0.0

            val deaths = mortalityRecordDao.countForFarmerBetween(userId, todayStart, todayEnd)
            val active = runCatching { productDao.countActiveByOwnerId(userId) }.getOrDefault(0)
            mortalityRate = if (active > 0) deaths.toDouble() / active.toDouble() else 0.0

            val eggs = runCatching { com.rio.rostry.data.database.dao.EggCollectionDao::class }.fold(
                onSuccess = { /* exists */ },
                onFailure = { /* noop */ }
            )
            val eggsCollected = try {
                // Use EggCollectionDao if available
                val entryPoint = com.rio.rostry.di.AppEntryPoints::class.java // not used; left placeholder
                // We don't have DI here for EggCollectionDao; compute breeder metric via Hatching/Egg logs instead
                hatchingLogDao.countEggsSetBetweenForFarmer(userId, todayStart, todayEnd)
            } catch (_: Throwable) { hatchingLogDao.countEggsSetBetweenForFarmer(userId, todayStart, todayEnd) }
            val pairs = runCatching { breedingPairDao.countActive(userId) }.getOrDefault(0)
            breederEggsPerPair = if (pairs > 0) eggsCollected.toDouble() / pairs.toDouble() else 0.0
        }

        val todayKey = todayDate.format(DateTimeFormatter.ISO_DATE)
        val daily = AnalyticsDailyEntity(
            id = UUID.randomUUID().toString(),
            userId = userId,
            role = role,
            dateKey = todayKey,
            salesRevenue = salesRevenue,
            ordersCount = ordersCount,
            productViews = 0,
            likesCount = likes,
            commentsCount = comments,
            transfersCount = 0,
            breedingSuccessRate = hatchRate,
            engagementScore = engagement.toDouble(),
            createdAt = System.currentTimeMillis()
        )
        analyticsDao.upsertDaily(daily)

        // Weekly aggregation into snapshots
        if (role == UserType.FARMER.name) {
            // Compute week window
            val weekEnd = todayEnd

            val weekRevenue = orderDao.sumDeliveredForSellerBetween(userId, weekStart, weekEnd)
            val weekOrders = orderDao.countDeliveredForSellerBetween(userId, weekStart, weekEnd)
            val weekScheduled = vaccinationRecordDao.countScheduledBetweenForFarmer(userId, weekStart, weekEnd)
            val weekAdmin = vaccinationRecordDao.countAdministeredBetweenForFarmer(userId, weekStart, weekEnd)
            val weekCompliance = if (weekScheduled > 0) weekAdmin.toDouble() / weekScheduled.toDouble() else 1.0
            val weekEggsSet = hatchingLogDao.countEggsSetBetweenForFarmer(userId, weekStart, weekEnd)
            val weekHatched = hatchingLogDao.countHatchedBetweenForFarmer(userId, weekStart, weekEnd)
            val weekHatchRate = if (weekEggsSet > 0) weekHatched.toDouble() / weekEggsSet.toDouble() else 0.0
            val weekDeaths = mortalityRecordDao.countForFarmerBetween(userId, weekStart, weekEnd)
            val activeNow = runCatching { productDao.countActiveByOwnerId(userId) }.getOrDefault(0)
            val weekMortalityRate = if (activeNow > 0) weekDeaths.toDouble() / activeNow.toDouble() else 0.0

            val snapshot = FarmerDashboardSnapshotEntity(
                snapshotId = UUID.randomUUID().toString(),
                farmerId = userId,
                weekStartAt = weekStart,
                weekEndAt = weekEnd,
                revenueInr = weekRevenue,
                ordersCount = weekOrders,
                hatchSuccessRate = weekHatchRate,
                mortalityRate = weekMortalityRate,
                deathsCount = weekDeaths,
                vaccinationCompletionRate = weekCompliance,
                growthRecordsCount = 0,
                quarantineActiveCount = 0,
                productsReadyToListCount = 0,
                createdAt = System.currentTimeMillis(),
                dirty = true
            )
            // Fetch previous snapshot BEFORE upsert for accurate WoW comparison
            val prev = farmerDashboardSnapshotDao.getLatest(userId)
            farmerDashboardSnapshotDao.upsert(snapshot)

            // Insights compared to previous snapshot
            if (prev != null) {
                if (prev.mortalityRate > 0 && (weekMortalityRate - prev.mortalityRate) / prev.mortalityRate > 0.10) {
                    analyticsNotifier.showInsight(
                        title = "Mortality increase",
                        message = "Weekly mortality rose above 10% week-over-week"
                    )
                }
                if (weekCompliance < 0.80) {
                    analyticsNotifier.showInsight(
                        title = "Vaccination compliance low",
                        message = "Weekly vaccination compliance fell below 80%"
                    )
                }
                if (prev.hatchSuccessRate > 0 && (weekHatchRate - prev.hatchSuccessRate) / prev.hatchSuccessRate > 0.05) {
                    analyticsNotifier.showInsight(
                        title = "Hatch rate improved",
                        message = "Weekly hatch success improved >5%"
                    )
                }
            }
        } else if (role == UserType.ENTHUSIAST.name) {
            val weekEnd = todayEnd
            val weekEggsSet = hatchingLogDao.countEggsSetBetweenForFarmer(userId, weekStart, weekEnd)
            val weekHatched = hatchingLogDao.countHatchedBetweenForFarmer(userId, weekStart, weekEnd)
            val hatch30 = if (weekEggsSet > 0) weekHatched.toDouble() / weekEggsSet.toDouble() else 0.0
            val pairs = runCatching { breedingPairDao.countActive(userId) }.getOrDefault(0)
            val eggsCollected = hatchingLogDao.countEggsSetBetweenForFarmer(userId, weekStart, weekEnd)
            val breederPerf = if (pairs > 0) eggsCollected.toDouble() / pairs.toDouble() else 0.0

            val snapshot = EnthusiastDashboardSnapshotEntity(
                snapshotId = UUID.randomUUID().toString(),
                userId = userId,
                weekStartAt = weekStart,
                weekEndAt = weekEnd,
                hatchRateLast30Days = hatch30,
                breederSuccessRate = breederPerf,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis(),
                dirty = true
            )
            enthusiastDashboardSnapshotDao.upsert(snapshot)
        }

        // Engagement insight
        if (engagement >= 10) {
            analyticsNotifier.showInsight(
                title = "Great engagement today!",
                message = "You received $engagement interactions. Keep it up!"
            )
        }
        return Result.success()
    }

    companion object {
        const val WORK_NAME = "AnalyticsAggregationDaily"

        fun schedule(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                .setRequiresBatteryNotLow(true)
                .setRequiresCharging(false)
                .build()
            // Calculate initial delay to schedule at local midnight
            val now = java.time.ZonedDateTime.now()
            val nextMidnight = now.toLocalDate().plusDays(1).atStartOfDay(now.zone)
            val initialDelay = java.time.Duration.between(now, nextMidnight).toMillis()
            val req = androidx.work.PeriodicWorkRequestBuilder<AnalyticsAggregationWorker>(1, java.util.concurrent.TimeUnit.DAYS)
                .setConstraints(constraints)
                .setInitialDelay(initialDelay, java.util.concurrent.TimeUnit.MILLISECONDS)
                .build()
            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.UPDATE,
                req
            )
        }
    }
}
