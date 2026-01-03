package com.rio.rostry.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.google.firebase.auth.FirebaseAuth
import com.rio.rostry.data.database.dao.EnthusiastDashboardSnapshotDao
import com.rio.rostry.data.database.dao.HatchingLogDao
import com.rio.rostry.data.database.dao.BreedingPairDao
import com.rio.rostry.data.database.dao.EggCollectionDao
import com.rio.rostry.data.database.dao.MatingLogDao
import com.rio.rostry.data.database.dao.HatchingBatchDao
import com.rio.rostry.data.database.dao.TransferDao
import com.rio.rostry.data.database.entity.EnthusiastDashboardSnapshotEntity
import com.rio.rostry.utils.notif.EnthusiastNotifier
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.util.Calendar
import java.util.UUID
import java.util.concurrent.TimeUnit

@HiltWorker
class EnthusiastPerformanceWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val transferDao: TransferDao,
    private val snapshotDao: EnthusiastDashboardSnapshotDao,
    private val hatchingLogDao: HatchingLogDao,
    private val breedingPairDao: BreedingPairDao,
    private val eggCollectionDao: EggCollectionDao,
    private val matingLogDao: MatingLogDao,
    private val hatchingBatchDao: HatchingBatchDao,
    private val traceability: com.rio.rostry.data.repository.TraceabilityRepository,
    private val auth: FirebaseAuth
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val userId = auth.currentUser?.uid ?: return@withContext Result.failure()

            // Compute current week window
            val cal = Calendar.getInstance()
            cal.firstDayOfWeek = Calendar.MONDAY
            cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
            cal.set(Calendar.HOUR_OF_DAY, 0)
            cal.set(Calendar.MINUTE, 0)
            cal.set(Calendar.SECOND, 0)
            cal.set(Calendar.MILLISECOND, 0)
            val weekStart = cal.timeInMillis
            cal.add(Calendar.DAY_OF_WEEK, 6)
            cal.set(Calendar.HOUR_OF_DAY, 23)
            cal.set(Calendar.MINUTE, 59)
            cal.set(Calendar.SECOND, 59)
            val weekEnd = cal.timeInMillis

            // Transfer metrics (pending, disputed) from both directions
            val fromMe = transferDao.getTransfersFromUser(userId).first()
            val toMe = transferDao.getTransfersToUser(userId).first()
            val all = fromMe + toMe
            val pendingCount = all.count { it.status.equals("PENDING", ignoreCase = true) }
            val disputedCount = all.count { it.status.equals("DISPUTED", ignoreCase = true) }

            // 30-day hatch vs eggs set
            val now = System.currentTimeMillis()
            val thirtyDaysAgo = now - 30L * 24 * 60 * 60 * 1000
            val hatched30 = hatchingLogDao.countHatchedBetweenForFarmer(userId, thirtyDaysAgo, now)
            val eggsSet30 = hatchingLogDao.countEggsSetBetweenForFarmer(userId, thirtyDaysAgo, now)
            val hatchRate30 = if (eggsSet30 > 0) hatched30.toDouble() / eggsSet30 else 0.0

            // Average breeder success across active pairs (use stored hatchSuccessRate)
            val activePairs = breedingPairDao.observeActive(userId).first()
            val byPairId = activePairs.associateBy { it.pairId }
            val avgBreederSuccess = if (activePairs.isNotEmpty()) activePairs.map { it.hatchSuccessRate }.average() else 0.0

            // Active pairs count
            val activePairsCount = activePairs.size

            // Eggs collected this week via DAO aggregate
            val eggsWeek = eggCollectionDao.countEggsForFarmerBetween(userId, weekStart, weekEnd)

            // Hatching due in next 7 days via DAO aggregate
            val sevenDaysEnd = now + 7L * 24 * 60 * 60 * 1000
            val due7 = hatchingBatchDao.countDueThisWeekForFarmer(userId, now, sevenDaysEnd)

            // Pairs to mate: last-mated > 7 days ago or never (reuse sevenDaysMs from week cutoff logic)
            val lastList = matingLogDao.observeLastMatedByFarmer(userId).first()
            val lastMap = lastList.associate { it.pairId to (it.lastMated ?: 0L) }
            val sevenDaysMs = 7L * 24 * 60 * 60 * 1000
            val cutoff = now - sevenDaysMs
            val pairsToMateCount = activePairs.count { pair ->
                val last = lastMap[pair.pairId]
                last == null || last == 0L || last < cutoff
            }

            // Top bloodlines engagement: aggregate eggs per bloodline root
            val rootCache = mutableMapOf<String, String>() // maleProductId -> root bloodline id
            val collectionsThisWeek = eggCollectionDao.getCollectionsDueBetween(weekStart, weekEnd).filter { it.farmerId == userId }
            val bloodlineMap = mutableMapOf<String, Int>() // bloodlineId -> eggs count
            for (coll in collectionsThisWeek) {
                // Use cached active pairs map to avoid repeated Flow queries
                val pair = byPairId[coll.pairId]
                if (pair != null) {
                    val maleId = pair.maleProductId
                    // Resolve root from cache or compute via ancestors()
                    val root = rootCache[maleId] ?: run {
                        val anc = traceability.ancestors(maleId, maxDepth = 5)
                        val computed = when (anc) {
                            is com.rio.rostry.utils.Resource.Success -> {
                                val levels = anc.data ?: emptyMap()
                                levels.maxByOrNull { it.key }?.value?.firstOrNull() ?: maleId
                            }
                            else -> maleId
                        }
                        rootCache[maleId] = computed
                        computed
                    }
                    bloodlineMap[root] = (bloodlineMap[root] ?: 0) + coll.eggsCollected
                }
            }
            val bloodlinesJson = com.google.gson.Gson().toJson(
                bloodlineMap.entries.map { mapOf("bloodlineId" to it.key, "eggs" to it.value) }
            )

            // NEW: Compute additional Enthusiast KPIs
            // Incubating count: batches with status INCUBATING or ACTIVE
            val incubatingCount = hatchingBatchDao.countIncubatingForFarmer(userId, now)
            
            // Eggs collected today
            val dayStart = now - (now % (24 * 60 * 60 * 1000))
            val eggsToday = eggCollectionDao.countEggsForFarmerBetween(userId, dayStart, now)

            // Persist snapshot with all fields
            val snapshot = EnthusiastDashboardSnapshotEntity(
                snapshotId = UUID.randomUUID().toString(),
                userId = userId,
                weekStartAt = weekStart,
                weekEndAt = weekEnd,
                hatchRateLast30Days = hatchRate30,
                breederSuccessRate = avgBreederSuccess,
                disputedTransfersCount = disputedCount,
                topBloodlinesEngagement = bloodlinesJson,
                activePairsCount = activePairsCount,
                eggsCollectedCount = eggsWeek,
                hatchingDueCount = due7,
                transfersPendingCount = pendingCount,
                pairsToMateCount = pairsToMateCount,
                incubatingCount = incubatingCount,
                sickBirdsCount = 0, // TODO: Query from FarmAlertDao when available
                eggsCollectedToday = eggsToday,
                createdAt = System.currentTimeMillis(),
                dirty = true,
                syncedAt = null
            )

            snapshotDao.upsert(snapshot)

            // Lightweight notifications
            if (due7 > 0) {
                EnthusiastNotifier.hatchingDue(applicationContext, due7)
            }
            if (pairsToMateCount > 0) {
                EnthusiastNotifier.pairToMate(applicationContext, pairsToMateCount)
            }
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    companion object {
        private const val UNIQUE_NAME = "EnthusiastPerformanceWorkerWeekly"
        fun schedule(context: Context) {
            val request = PeriodicWorkRequestBuilder<EnthusiastPerformanceWorker>(7, TimeUnit.DAYS)
                .setBackoffCriteria(androidx.work.BackoffPolicy.EXPONENTIAL, 10, TimeUnit.MINUTES)
                .build()
            WorkManager.getInstance(context)
                .enqueueUniquePeriodicWork(UNIQUE_NAME, ExistingPeriodicWorkPolicy.KEEP, request)
        }
    }
}
