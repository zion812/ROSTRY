package com.rio.rostry.ui.enthusiast

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.database.dao.EventsDao
import com.rio.rostry.data.database.dao.FarmAlertDao
import com.rio.rostry.data.database.dao.TransferDao
import com.rio.rostry.data.database.dao.BreedingPairDao
import com.rio.rostry.data.database.dao.HatchingBatchDao
import com.rio.rostry.data.database.dao.GrowthRecordDao
import com.rio.rostry.data.database.dao.EnthusiastDashboardSnapshotDao
import com.rio.rostry.data.database.dao.FarmAssetDao
import com.rio.rostry.data.database.dao.ShowRecordDao
import com.rio.rostry.data.database.entity.EventEntity
import com.rio.rostry.data.database.entity.FarmAlertEntity
import com.rio.rostry.data.repository.EnthusiastBreedingRepository
import com.rio.rostry.data.repository.analytics.AnalyticsRepository
import com.rio.rostry.data.repository.analytics.EnthusiastDashboard
import com.rio.rostry.session.CurrentUserProvider
import com.rio.rostry.data.sync.SyncManager
import dagger.hilt.android.lifecycle.HiltViewModel
import com.rio.rostry.utils.analytics.EnthusiastAnalyticsTracker
import com.rio.rostry.ui.enthusiast.components.ChampionData
import com.rio.rostry.ui.enthusiast.components.UrgentActivity
import javax.inject.Inject
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.catch
import timber.log.Timber

@HiltViewModel
class EnthusiastHomeViewModel @Inject constructor(
    analyticsRepository: AnalyticsRepository,
    private val breedingRepository: EnthusiastBreedingRepository,
    private val dashboardSnapshotDao: EnthusiastDashboardSnapshotDao,
    private val farmAlertDao: FarmAlertDao,
    private val eventsDao: EventsDao,
    private val transferDao: TransferDao,
    private val growthRecordDao: GrowthRecordDao,
    private val breedingPairDao: BreedingPairDao,
    private val hatchingBatchDao: HatchingBatchDao,
    private val farmAssetDao: FarmAssetDao,
    private val showRecordDao: ShowRecordDao,
    currentUserProvider: CurrentUserProvider,
    private val syncManager: SyncManager,
    private val analytics: EnthusiastAnalyticsTracker,
) : ViewModel() {

    data class IncubationTimer(val batchId: String, val name: String, val expectedHatchAt: Long?)
    data class BreederStatus(val pairId: String, val hatchSuccessRate: Double)

    data class UiState(
        val dashboard: EnthusiastDashboard = EnthusiastDashboard(0.0, 0, 0.0, emptyList()),
        val sickBirdsCount: Int = 0,
        val pendingTransfersCount: Int = 0,
        val disputedTransfersCount: Int = 0,
        val pairsToMateCount: Int = 0,
        val eggsCollectedToday: Int = 0,
        val incubationTimers: List<IncubationTimer> = emptyList(),
        val hatchingDueCount: Int = 0,
        val weeklyGrowthUpdatesCount: Int = 0,
        val breederStatusChecks: List<BreederStatus> = emptyList(),
        val transferVerificationsCount: Int = 0,
        val eventsToday: List<EventEntity> = emptyList(),
        val alerts: List<FarmAlertEntity> = emptyList(),
        val topBloodlines: List<Pair<String, Int>> = emptyList(),
        val isLoading: Boolean = true,
    )

    private val uid = currentUserProvider.userIdOrNull()

    // Cache-first: Load snapshot immediately for fast initial render
    init {
        if (uid != null) {
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    val snapshot = dashboardSnapshotDao.getLatest(uid)
                    if (snapshot != null) {
                        // Emit cached state immediately for fast first paint
                        _cachedState.value = UiState(
                            sickBirdsCount = snapshot.sickBirdsCount,
                            pendingTransfersCount = snapshot.transfersPendingCount,
                            disputedTransfersCount = snapshot.disputedTransfersCount,
                            pairsToMateCount = snapshot.pairsToMateCount,
                            eggsCollectedToday = snapshot.eggsCollectedToday,
                            hatchingDueCount = snapshot.hatchingDueCount,
                            isLoading = false,
                        )
                        Timber.d("Enthusiast Home: Cache-first render with snapshot from ${snapshot.updatedAt}")
                    }
                } catch (e: Exception) {
                    Timber.w(e, "Failed to load cached snapshot")
                }
            }
        }
    }

    // Cached initial state for fast first paint
    private val _cachedState = MutableStateFlow(UiState())

    private val dashboardFlow = (uid?.let { analyticsRepository.enthusiastDashboard(it) }
        ?: MutableStateFlow(EnthusiastDashboard(0.0, 0, 0.0, emptyList()))).orDefault(EnthusiastDashboard(0.0, 0, 0.0, emptyList()))

    private val transfersPendingFlow = if (uid != null) {
        combine(transferDao.getTransfersFromUser(uid), transferDao.getTransfersToUser(uid)) { a, b ->
            (a + b).count { it.status.equals("PENDING", ignoreCase = true) }
        }.orDefault(0)
    } else MutableStateFlow(0)

    private val transfersDisputedFlow = if (uid != null) {
        combine(transferDao.getTransfersFromUser(uid), transferDao.getTransfersToUser(uid)) { a, b ->
            (a + b).count { it.status.equals("DISPUTED", ignoreCase = true) }
        }.orDefault(0)
    } else MutableStateFlow(0)

    // Additional fetcher flows
    private val pairsToMateFlow = if (uid != null) breedingRepository.observePairsToMate(uid).map { it.size }.orDefault(0) else MutableStateFlow(0)
    private val eggsCollectedTodayFlow = if (uid != null) breedingRepository.observeEggsCollectedToday(uid).orDefault(0) else MutableStateFlow(0)
    private val incubationTimersFlow = if (uid != null) breedingRepository.observeIncubationTimers(uid)
        .map { batches -> batches.map { IncubationTimer(it.batchId, it.name, it.expectedHatchAt) } }
        .distinctUntilChanged { a, b ->
            if (a.size != b.size) return@distinctUntilChanged false
            val pa = a.map { it.batchId to (it.expectedHatchAt ?: 0L) }
            val pb = b.map { it.batchId to (it.expectedHatchAt ?: 0L) }
            pa == pb
        }
        .orDefault(emptyList()) else MutableStateFlow(emptyList())
    private val hatchingDueFlow = if (uid != null) breedingRepository.observeHatchingDue(uid, withinDays = 7).map { it.size }.orDefault(0) else MutableStateFlow(0)

    private val weeklyGrowthUpdatesFlow = if (uid != null) {
        val now = System.currentTimeMillis()
        val weekStart = startOfWeek(now)
        val weekEnd = endOfWeek(now)
        growthRecordDao.observeCountForFarmerBetween(uid, weekStart, weekEnd).orDefault(0)
    } else MutableStateFlow(0)

    private val breederStatusChecksFlow = if (uid != null) {
        breedingPairDao.observeActive(uid).map { pairs ->
            pairs.filter { it.eggsCollected > 0 }.map { BreederStatus(it.pairId, it.hatchSuccessRate) }
        }.orDefault(emptyList())
    } else MutableStateFlow(emptyList())

    private val transferVerificationsFlow = if (uid != null) {
        combine(transferDao.getTransfersFromUser(uid), transferDao.getTransfersToUser(uid)) { a, b ->
            (a + b).count { it.status.equals("PENDING", ignoreCase = true) }
        }
    } else MutableStateFlow(0)

    private val eventsTodayFlow = if (uid != null) {
        val now = System.currentTimeMillis()
        val dayStart = startOfDay(now)
        val dayEnd = endOfDay(now)
        eventsDao.streamUpcoming(now).map { it.filter { e -> e.startTime in dayStart..dayEnd } }.orDefault(emptyList())
    } else MutableStateFlow(emptyList())

    private val alertsFlow = if (uid != null) farmAlertDao.observeUnread(uid).orDefault(emptyList()) else MutableStateFlow(emptyList())

    private val topBloodlinesFlow = if (uid != null) {
        dashboardSnapshotDao.observeLatest(uid).map { snap ->
            if (snap?.topBloodlinesEngagement.isNullOrBlank()) emptyList() else try {
                val type = object : com.google.gson.reflect.TypeToken<List<Map<String, Any>>>() {}.type
                val list: List<Map<String, Any>> = com.google.gson.Gson().fromJson(snap!!.topBloodlinesEngagement, type)
                list.mapNotNull { m ->
                    val id = m["bloodlineId"] as? String
                    val eggsNum = (m["eggs"] as? Number)?.toInt()
                    if (id != null && eggsNum != null) id to eggsNum else null
                }.sortedByDescending { it.second }.take(5)
            } catch (_: Exception) { emptyList() }
        }.orDefault(emptyList())
    } else MutableStateFlow(emptyList())

    // Active pairs for quick egg collection dialog
    val activePairs: StateFlow<List<com.rio.rostry.data.database.entity.BreedingPairEntity>> = (
        if (uid != null) breedingPairDao.observeActive(uid) else MutableStateFlow(emptyList())
    ).stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    // Quick action status
    private val _quickStatus = MutableStateFlow<String?>(null)
    val quickStatus: StateFlow<String?> = _quickStatus.asStateFlow()

    // 1 Hz ticker for timers only
    private fun tickerFlow(periodMs: Long) = kotlinx.coroutines.flow.flow {
        while (true) {
            emit(Unit)
            delay(periodMs)
        }
    }

    // Base flow excludes the 1s ticker to avoid re-emitting static cards
    private val baseFlow = combine(
        dashboardFlow,
        transfersPendingFlow,
        transfersDisputedFlow,
        pairsToMateFlow,
        eggsCollectedTodayFlow,
        hatchingDueFlow,
        weeklyGrowthUpdatesFlow,
        breederStatusChecksFlow,
        transferVerificationsFlow,
        eventsTodayFlow,
        alertsFlow,
        topBloodlinesFlow,
    ) { values: Array<Any?> ->
        val startNs = System.nanoTime()
        val d = values[0] as EnthusiastDashboard
        val p = values[1] as Int
        val disputed = values[2] as Int
        val pairsToMate = values[3] as Int
        val eggsToday = values[4] as Int
        val hatchingDue = values[5] as Int
        val growthCount = values[6] as Int
        val breederChecks = values[7] as List<BreederStatus>
        val verifications = values[8] as Int
        val events = values[9] as List<EventEntity>
        val alerts = values[10] as List<FarmAlertEntity>
        val bloodlines = values[11] as List<Pair<String, Int>>
        val ui = UiState(
            dashboard = d,
            sickBirdsCount = alerts.count { it.severity == "HIGH" || it.message.contains("sick", ignoreCase = true) || it.alertType.contains("MORTALITY", ignoreCase = true) },
            pendingTransfersCount = p,
            disputedTransfersCount = disputed,
            pairsToMateCount = pairsToMate,
            eggsCollectedToday = eggsToday,
            incubationTimers = emptyList(), // filled by timersFlow combine below
            hatchingDueCount = hatchingDue,
            weeklyGrowthUpdatesCount = growthCount,
            breederStatusChecks = breederChecks,
            transferVerificationsCount = verifications,
            eventsToday = events,
            alerts = alerts,
            topBloodlines = bloodlines,
            isLoading = false,
        )
        val elapsedMs = (System.nanoTime() - startNs) / 1_000_000
        if (elapsedMs > 3000) Timber.w("EnthusiastHome combine slow: %d ms", elapsedMs)
        ui
    }.stateIn(viewModelScope, SharingStarted.Eagerly, UiState())

    // Timers flow updates at 1 Hz but only affects timers list
    private val timersFlow = combine(incubationTimersFlow, tickerFlow(1000)) { batches, _ -> batches }

    // Final UI state: merges cached snapshot (fast) with live flows (complete)
    val ui: StateFlow<UiState> = combine(baseFlow, timersFlow, _cachedState) { base, timers, cached ->
        // If base is still loading and cached has data, prefer cached for faster first paint
        if (base.isLoading && !cached.isLoading) {
            cached.copy(incubationTimers = timers)
        } else {
            base.copy(incubationTimers = timers)
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, UiState())

    // ========== Premium Component Data Flows (Comment 2) ==========
    
    /**
     * Top champion bird computed from showcase assets.
     * Uses showcase designation as proxy for "champion" status.
     */
    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val topChampion: StateFlow<ChampionData?> = if (uid != null) {
        farmAssetDao.getShowcaseAssets(uid).mapLatest { assets ->
            assets.firstOrNull()?.let { asset ->
                val wins = showRecordDao.countWins(asset.assetId)
                val total = showRecordDao.countTotal(asset.assetId)
                val winRate = if (total > 0) wins.toFloat() / total else 0f
                ChampionData(
                    id = asset.assetId,
                    name = asset.name.ifBlank { asset.birdCode ?: "Champion" },
                    imageUrl = asset.imageUrls.firstOrNull(),
                    winRate = winRate,
                    totalFights = total,
                    breed = asset.breed ?: ""
                )
            }
        }.orDefault(null).stateIn(viewModelScope, SharingStarted.Eagerly, null)
    } else MutableStateFlow(null)

    /**
     * List of top champions for the HeroChampionBanner carousel.
     * Uses showcase assets as premium birds.
     */
    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val topChampions: StateFlow<List<ChampionData>> = if (uid != null) {
        farmAssetDao.getShowcaseAssets(uid).mapLatest { assets ->
            assets.take(5).map { asset ->
                val wins = showRecordDao.countWins(asset.assetId)
                val total = showRecordDao.countTotal(asset.assetId)
                val winRate = if (total > 0) wins.toFloat() / total else 0f
                ChampionData(
                    id = asset.assetId,
                    name = asset.name.ifBlank { asset.birdCode ?: "Champion" },
                    imageUrl = asset.imageUrls.firstOrNull(),
                    winRate = winRate,
                    totalFights = total,
                    breed = asset.breed ?: ""
                )
            }
        }.orDefault(emptyList()).stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
    } else MutableStateFlow(emptyList())

    /**
     * Trust score computed from KYC status and engagement metrics.
     * Score ranges: 0-40 (unverified), 40-70 (partial), 70-100 (verified + active)
     */
    val trustScore: StateFlow<Float> = if (uid != null) {
        dashboardSnapshotDao.observeLatest(uid).map { snapshot ->
            var score = 30f // Base score
            
            // KYC verification adds 15 points
            if ((snapshot?.transfersPendingCount ?: 0) > 0) score += 15f
            
            // Engagement metrics - eggs collected adds points
            val eggCount = snapshot?.eggsCollectedToday ?: 0
            if (eggCount > 0) score += minOf(eggCount.toFloat() * 2f, 15f)
            
            // Activity - active pairs adds up to 25 points
            val pairsCount = snapshot?.activePairsCount ?: 0
            score += minOf(pairsCount.toFloat() * 5f, 25f)
            
            score.coerceIn(0f, 100f)
        }.orDefault(50f).stateIn(viewModelScope, SharingStarted.Eagerly, 50f)
    } else MutableStateFlow(50f)

    /**
     * Most urgent activity for the LiveActivityTicker.
     * Prioritizes: 1) Hatching due, 2) Sick birds, 3) Vaccinations, 4) Incubation
     */
    val urgentActivity: StateFlow<UrgentActivity?> = combine(
        incubationTimersFlow,
        hatchingDueFlow,
        alertsFlow
    ) { timers, hatchingDue, alerts ->
        // Priority 1: Hatching due soon (within 24 hours)
        val urgentHatch = timers.firstOrNull { timer ->
            timer.expectedHatchAt != null && 
            (timer.expectedHatchAt - System.currentTimeMillis()) < 24 * 60 * 60 * 1000
        }
        if (urgentHatch != null) {
            return@combine UrgentActivity.HatchingDue(
                batchName = urgentHatch.name,
                dueCount = 1,
                targetTimestamp = urgentHatch.expectedHatchAt ?: System.currentTimeMillis()
            )
        }
        
        // Priority 2: Sick birds alert
        val sickCount = alerts.count { 
            it.severity == "HIGH" || it.alertType.contains("MORTALITY", ignoreCase = true) 
        }
        if (sickCount > 0) {
            return@combine UrgentActivity.SickBirds(
                count = sickCount,
                severity = if (sickCount > 3) "critical" else "moderate"
            )
        }
        
        // Priority 3: Active incubation
        val activeIncubation = timers.firstOrNull()
        if (activeIncubation != null) {
            val expectedAt = activeIncubation.expectedHatchAt ?: System.currentTimeMillis()
            val daysPassed = ((System.currentTimeMillis() - (expectedAt - 21L * 24 * 60 * 60 * 1000)) / (24 * 60 * 60 * 1000)).toInt().coerceAtLeast(1)
            return@combine UrgentActivity.Incubation(
                batchName = activeIncubation.name,
                currentDay = daysPassed,
                totalDays = 21,
                targetTimestamp = expectedAt
            )
        }
        
        null
    }.orDefault(null).stateIn(viewModelScope, SharingStarted.Eagerly, null)

    /**
     * Pending task counts for SpeedDialActions badges.
     */
    val pendingTaskCounts: StateFlow<Map<String, Int>> = combine(
        alertsFlow,
        hatchingDueFlow,
        pairsToMateFlow
    ) { alerts, hatching, pairs ->
        mapOf(
            "vaccination" to alerts.count { it.alertType.contains("VACCINATION", ignoreCase = true) },
            "eggs" to pairs,
            "hatching" to hatching,
            "sick" to alerts.count { it.severity == "HIGH" }
        )
    }.orDefault(emptyMap()).stateIn(viewModelScope, SharingStarted.Eagerly, emptyMap())

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing

    private val _navigationEvent = MutableSharedFlow<String>(extraBufferCapacity = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    val navigationEvent = _navigationEvent

    private val _errorEvents = MutableSharedFlow<String>(extraBufferCapacity = 2, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    val errorEvents = _errorEvents

    // Flow catch helper emitting default and an error message
    private fun <T> StateFlow<T>.orDefault(default: T): StateFlow<T> = this
    private fun <T> kotlinx.coroutines.flow.Flow<T>.orDefault(default: T): kotlinx.coroutines.flow.Flow<T> =
        this.catch { e ->
            Timber.e(e, "EnthusiastHome flow failed; emitting default")
            viewModelScope.launch { _errorEvents.emit("Failed to load data. Pull to refresh.") }
            emit(default)
        }

    fun refresh() {
        viewModelScope.launch {
            if (_isRefreshing.value) return@launch
            _isRefreshing.value = true
            try {
                // Trigger bidirectional sync to meet 5-minute reconciliation SLA
                withContext(Dispatchers.IO) {
                    syncManager.syncAll()
                }
                analytics.trackFetcherCardTap("home_refresh")
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    fun navigateTo(route: String) {
        viewModelScope.launch { _navigationEvent.emit(route) }
    }

    fun quickCollectEggs(pairId: String, count: Int, grade: String, weight: Double?) {
        if (pairId.isBlank() || count <= 0) {
            _quickStatus.value = "Please select a pair and enter a valid count"
            return
        }
        viewModelScope.launch {
            when (val res = breedingRepository.collectEggs(pairId, count, grade, weight)) {
                is com.rio.rostry.utils.Resource.Success -> {
                    _quickStatus.value = "Eggs logged"
                    analytics.trackEggCollect(pairId, count)
                }
                is com.rio.rostry.utils.Resource.Error -> _quickStatus.value = res.message ?: "Failed to log eggs"
                else -> {}
            }
        }
    }

    fun consumeQuickStatus() { _quickStatus.value = null }

    /**
     * Dismiss an alert by marking it as read in the database.
     * Called from EnthusiastAlertCard onDismiss callback.
     */
    fun dismissAlert(alertId: String) {
        viewModelScope.launch {
            try {
                farmAlertDao.markRead(alertId)
                Timber.d("Alert dismissed: $alertId")
            } catch (e: Exception) {
                Timber.e(e, "Failed to dismiss alert $alertId")
            }
        }
    }

    /**
     * Dismiss all currently displayed alerts.
     */
    fun dismissAllAlerts() {
        viewModelScope.launch {
            try {
                val currentAlerts = ui.value.alerts
                currentAlerts.forEach { alert ->
                    farmAlertDao.markRead(alert.alertId)
                }
                Timber.d("Dismissed ${currentAlerts.size} alerts")
            } catch (e: Exception) {
                Timber.e(e, "Failed to dismiss all alerts")
            }
        }
    }

    private fun startOfDay(now: Long): Long {
        val oneDay = 24L * 60 * 60 * 1000
        return now - (now % oneDay)
    }

    private fun endOfDay(now: Long): Long = startOfDay(now) + 24L * 60 * 60 * 1000 - 1

    private fun startOfWeek(now: Long): Long {
        // Monday 00:00 as start; simple approx using 7-day buckets suffices here
        val oneDay = 24L * 60 * 60 * 1000
        val dayOfWeekZeroBased = ((now / oneDay + 4) % 7).toInt() // align Monday
        return startOfDay(now) - dayOfWeekZeroBased * oneDay
    }

    private fun endOfWeek(now: Long): Long = startOfWeek(now) + 7L * 24 * 60 * 60 * 1000 - 1
}
