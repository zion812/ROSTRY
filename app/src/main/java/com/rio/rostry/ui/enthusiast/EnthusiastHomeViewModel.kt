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
import com.rio.rostry.data.database.entity.EventEntity
import com.rio.rostry.data.database.entity.FarmAlertEntity
import com.rio.rostry.data.repository.EnthusiastBreedingRepository
import com.rio.rostry.data.repository.analytics.AnalyticsRepository
import com.rio.rostry.data.repository.analytics.EnthusiastDashboard
import com.rio.rostry.session.CurrentUserProvider
import com.rio.rostry.data.sync.SyncManager
import dagger.hilt.android.lifecycle.HiltViewModel
import com.rio.rostry.utils.analytics.EnthusiastAnalyticsTracker
import javax.inject.Inject
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.asStateFlow
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
    currentUserProvider: CurrentUserProvider,
    private val syncManager: SyncManager,
    private val analytics: EnthusiastAnalyticsTracker,
) : ViewModel() {

    data class IncubationTimer(val batchId: String, val name: String, val expectedHatchAt: Long?)
    data class BreederStatus(val pairId: String, val hatchSuccessRate: Double)

    data class UiState(
        val dashboard: EnthusiastDashboard = EnthusiastDashboard(0.0, 0, 0.0, emptyList()),
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
    private val incubationTimersFlow = if (uid != null) breedingRepository.observeIncubationTimers(uid).map { batches ->
        batches.map { IncubationTimer(it.batchId, it.name, it.expectedHatchAt) }
    }.orDefault(emptyList()) else MutableStateFlow(emptyList())
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

    val ui: StateFlow<UiState> = combine(baseFlow, timersFlow) { base, timers ->
        base.copy(incubationTimers = timers)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, UiState())

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
