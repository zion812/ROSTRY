package com.rio.rostry.ui.farmer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.database.entity.FarmAlertEntity
import com.rio.rostry.data.database.entity.FarmerDashboardSnapshotEntity
import com.rio.rostry.data.repository.monitoring.BreedingRepository
import com.rio.rostry.data.repository.monitoring.FarmAlertRepository
import com.rio.rostry.data.repository.monitoring.FarmerDashboardRepository
import com.rio.rostry.data.database.dao.TaskDao
import com.rio.rostry.data.database.dao.DailyLogDao
import com.rio.rostry.data.repository.ProductRepository
import com.rio.rostry.data.sync.SyncManager
import com.rio.rostry.data.repository.TransferRepository
import com.rio.rostry.data.repository.TraceabilityRepository
import com.rio.rostry.data.repository.analytics.AnalyticsRepository
import com.rio.rostry.data.repository.UserRepository
import com.rio.rostry.data.repository.analytics.DailyGoal
import com.rio.rostry.data.repository.analytics.ActionableInsight
import timber.log.Timber
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

data class FarmerHomeUiState(
    val vaccinationDueCount: Int = 0,
    val vaccinationOverdueCount: Int = 0,
    val growthRecordsThisWeek: Int = 0,
    val quarantineActiveCount: Int = 0,
    val quarantineUpdatesDue: Int = 0,
    val hatchingBatchesActive: Int = 0,
    val hatchingDueThisWeek: Int = 0,
    val mortalityLast7Days: Int = 0,
    val breedingPairsActive: Int = 0,
    val productsReadyToListCount: Int = 0, // Farm-marketplace bridge
    val tasksDueCount: Int = 0,
    val tasksOverdueCount: Int = 0,
    val dailyLogsThisWeek: Int = 0,
    val unreadAlerts: List<FarmAlertEntity> = emptyList(),
    val weeklySnapshot: FarmerDashboardSnapshotEntity? = null,
    val batchesDueForSplit: Int = 0,
    val urgentKpiCount: Int = 0,
    val isLoading: Boolean = true,
    val transfersPendingCount: Int = 0,
    val transfersAwaitingVerificationCount: Int = 0,
    val recentlyAddedBirdsCount: Int = 0,
    val recentlyAddedBatchesCount: Int = 0,
    val productsEligibleForTransferCount: Int = 0,
    val complianceAlertsCount: Int = 0,
    val kycVerified: Boolean = false,
    val dailyGoals: List<DailyGoal> = emptyList(),
    val analyticsInsights: List<ActionableInsight> = emptyList(),
    val verificationStatus: com.rio.rostry.domain.model.VerificationStatus = com.rio.rostry.domain.model.VerificationStatus.UNVERIFIED,
    val recentActivity: List<com.rio.rostry.data.repository.monitoring.OnboardingActivity> = emptyList(),
    val widgets: List<DashboardWidget> = emptyList()
)

data class DashboardWidget(
    val type: WidgetType,
    val title: String,
    val count: Int,
    val alertCount: Int = 0,
    val alertLevel: AlertLevel = AlertLevel.NORMAL,
    val actionLabel: String
)

enum class AlertLevel { NORMAL, INFO, WARNING, CRITICAL }

enum class WidgetType {
    DAILY_LOG, TASKS, VACCINATION, GROWTH, QUARANTINE, HATCHING, 
    MORTALITY, BREEDING, READY_TO_LIST, NEW_LISTING, TRANSFERS, COMPLIANCE,
    TRANSFERS_PENDING, TRANSFERS_VERIFICATION
}

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class FarmerHomeViewModel @Inject constructor(
    private val vaccinationRecordDao: com.rio.rostry.data.database.dao.VaccinationRecordDao,
    private val growthRecordDao: com.rio.rostry.data.database.dao.GrowthRecordDao,
    private val quarantineRecordDao: com.rio.rostry.data.database.dao.QuarantineRecordDao,
    private val mortalityRecordDao: com.rio.rostry.data.database.dao.MortalityRecordDao,
    private val hatchingBatchDao: com.rio.rostry.data.database.dao.HatchingBatchDao,
    private val breedingRepository: BreedingRepository,
    private val farmAlertRepository: FarmAlertRepository,
    private val farmerDashboardRepository: FarmerDashboardRepository,
    private val taskDao: TaskDao,
    private val dailyLogDao: DailyLogDao,
    private val productRepository: ProductRepository,
    private val firebaseAuth: com.google.firebase.auth.FirebaseAuth,
    private val syncManager: SyncManager,
    private val transferRepository: TransferRepository,
    private val traceabilityRepository: TraceabilityRepository,
    private val analyticsRepository: AnalyticsRepository,
    private val userRepository: UserRepository,
    private val farmOnboardingRepository: com.rio.rostry.data.repository.monitoring.FarmOnboardingRepository
) : ViewModel() {

    private val _navigationEvent = MutableSharedFlow<String>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    private val _errorEvents = MutableSharedFlow<String>()
    val errorEvents = _errorEvents.asSharedFlow()

    // Pull-to-refresh indicator
    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing


    // Reactive farmerId from Firebase Auth (nullable). Emits null when signed out so UI can stop loading.
    private val farmerId: Flow<String?> = callbackFlow {
        val authStateListener = com.google.firebase.auth.FirebaseAuth.AuthStateListener { auth ->
            trySend(auth.currentUser?.uid)
        }
        firebaseAuth.addAuthStateListener(authStateListener)
        // Emit current state (may be null)
        trySend(firebaseAuth.currentUser?.uid)
        awaitClose { firebaseAuth.removeAuthStateListener(authStateListener) }
    }.distinctUntilChanged()

    // Helper to make a flow resilient and emit a default on error
    private fun <T> Flow<T>.orDefault(default: T): Flow<T> = this.catch { e ->
        Timber.e(e, "FarmerHome flow failed; emitting default")
        // notify UI once per failure site
        viewModelScope.launch { _errorEvents.emit("Failed to load data. Pull to refresh.") }
        emit(default)
    }

    val uiState: StateFlow<FarmerHomeUiState> = farmerId.flatMapLatest { id: String? ->
        // If no authenticated Firebase user, show non-loading empty dashboard instead of spinning forever
        if (id == null) {
            flow { emit(FarmerHomeUiState(isLoading = false)) }
        } else {
            // Compute time windows once; rely on DAO flows for updates (no periodic ticker)
            val now = System.currentTimeMillis()
            val endOfDay = now + TimeUnit.DAYS.toMillis(1)
            val weekStart = now - TimeUnit.DAYS.toMillis(7)
            val weekEnd = now + TimeUnit.DAYS.toMillis(7)
            val twelveHoursAgo = now - TimeUnit.HOURS.toMillis(12)
            val startNs = System.nanoTime()
            combine(
                vaccinationRecordDao.observeDueForFarmer(id, now, endOfDay).orDefault(0),
                vaccinationRecordDao.observeOverdueForFarmer(id, now).orDefault(0),
                growthRecordDao.observeCountForFarmerBetween(id, weekStart, now).orDefault(0),
                quarantineRecordDao.observeActiveForFarmer(id).orDefault(0),
                quarantineRecordDao.observeUpdatesOverdueForFarmer(id, twelveHoursAgo).orDefault(0),
                hatchingBatchDao.observeActiveForFarmer(id, now).orDefault(0),
                hatchingBatchDao.observeDueThisWeekForFarmer(id, now, weekEnd).orDefault(0),
                mortalityRecordDao.observeCountForFarmerBetween(id, weekStart, now).orDefault(0),
                breedingRepository.observeActiveCount(id).orDefault(0),
                farmAlertRepository.observeUnread(id).orDefault(emptyList()),
                farmerDashboardRepository.observeLatest(id).orDefault(null),
                taskDao.observeOverdueCountForFarmer(id, now).orDefault(0),
                taskDao.observeDueForFarmer(id, now).orDefault(emptyList()),
                dailyLogDao.observeCountForFarmerBetween(id, weekStart, now).orDefault(0),
                productRepository.observeActiveWithBirth().map { products ->
                    products.count { it.sellerId == id && it.isBatch == true && ((it.ageWeeks ?: 0) >= 12) && it.splitAt == null }
                }.orDefault(0),
                transferRepository.observePendingCountForFarmer(id).orDefault(0),
                transferRepository.observeAwaitingVerificationCountForFarmer(id).orDefault(0),
                productRepository.observeRecentlyAddedForFarmer(id, weekStart).map { list -> list.count { it.isBatch != true } }.orDefault(0),
                productRepository.observeRecentlyAddedForFarmer(id, weekStart).map { list -> list.count { it.isBatch == true } }.orDefault(0),
                productRepository.observeEligibleForTransferCountForFarmer(id).orDefault(0),
                traceabilityRepository.observeComplianceAlertsCount(id).orDefault(0),
                traceabilityRepository.observeKycStatus(id).orDefault(false),
                analyticsRepository.observeDailyGoals(id).orDefault(emptyList()),
                analyticsRepository.getActionableInsights(id).orDefault(emptyList()),
                userRepository.getCurrentUser().map { res ->
                    (res as? com.rio.rostry.utils.Resource.Success)?.data?.verificationStatus ?: com.rio.rostry.domain.model.VerificationStatus.UNVERIFIED
                }.orDefault(com.rio.rostry.domain.model.VerificationStatus.UNVERIFIED),
                farmOnboardingRepository.observeRecentOnboardingActivity(id, 7).orDefault(emptyList())
            ) { values: Array<Any?> ->
                val vacDue = values[0] as? Int ?: 0
                val vacOverdue = values[1] as? Int ?: 0
                val growth = values[2] as? Int ?: 0
                val quarActive = values[3] as? Int ?: 0
                val quarOverdue = values[4] as? Int ?: 0
                val hatchActive = values[5] as? Int ?: 0
                val hatchDue = values[6] as? Int ?: 0
                val mortality = values[7] as? Int ?: 0
                val breeding = values[8] as? Int ?: 0
                @Suppress("UNCHECKED_CAST")
                val alerts = values[9] as? List<FarmAlertEntity> ?: emptyList()
                val snapshot = values[10] as? FarmerDashboardSnapshotEntity
                val overdueCount = values[11] as? Int ?: 0
                @Suppress("UNCHECKED_CAST")
                val dueTasks = values[12] as? List<com.rio.rostry.data.database.entity.TaskEntity> ?: emptyList()
                val dailyLogsCount = values[13] as? Int ?: 0
                val batchesDue = values[14] as? Int ?: 0
                val transfersPending = values[15] as? Int ?: 0
                val transfersAwaiting = values[16] as? Int ?: 0
                val recentlyAddedBirds = values[17] as? Int ?: 0
                val recentlyAddedBatches = values[18] as? Int ?: 0
                val productsEligible = values[19] as? Int ?: 0
                val complianceAlerts = values[20] as? Int ?: 0
                val kycVerified = values[21] as? Boolean ?: false
                @Suppress("UNCHECKED_CAST")
                val dailyGoals = values[22] as? List<DailyGoal> ?: emptyList()
                @Suppress("UNCHECKED_CAST")
                val analyticsInsights = values[23] as? List<ActionableInsight> ?: emptyList()
                val verStatus = values[24] as? com.rio.rostry.domain.model.VerificationStatus ?: com.rio.rostry.domain.model.VerificationStatus.UNVERIFIED
                @Suppress("UNCHECKED_CAST")
                val recentActivityList = values[25] as? List<com.rio.rostry.data.repository.monitoring.OnboardingActivity> ?: emptyList()

                val elapsedMs = (System.nanoTime() - startNs) / 1_000_000
                if (elapsedMs > 3000) {
                    Timber.w("FarmerHome combine slow: %d ms", elapsedMs)
                }

                val baseState = FarmerHomeUiState(
                    vaccinationDueCount = vacDue,
                    vaccinationOverdueCount = vacOverdue,
                    growthRecordsThisWeek = growth,
                    quarantineActiveCount = quarActive,
                    quarantineUpdatesDue = quarOverdue,
                    hatchingBatchesActive = hatchActive,
                    hatchingDueThisWeek = hatchDue,
                    mortalityLast7Days = mortality,
                    breedingPairsActive = breeding,
                    productsReadyToListCount = snapshot?.productsReadyToListCount ?: 0,
                    tasksDueCount = dueTasks.size,
                    tasksOverdueCount = overdueCount,
                    dailyLogsThisWeek = dailyLogsCount,
                    unreadAlerts = alerts,
                    weeklySnapshot = snapshot,
                    batchesDueForSplit = batchesDue,
                    urgentKpiCount = overdueCount + vacOverdue + quarOverdue + batchesDue,
                    isLoading = false,
                    transfersPendingCount = transfersPending,
                    transfersAwaitingVerificationCount = transfersAwaiting,
                    recentlyAddedBirdsCount = recentlyAddedBirds,
                    recentlyAddedBatchesCount = recentlyAddedBatches,
                    productsEligibleForTransferCount = productsEligible,
                    complianceAlertsCount = complianceAlerts,
                    kycVerified = kycVerified,
                    dailyGoals = dailyGoals,
                    analyticsInsights = analyticsInsights,
                    verificationStatus = verStatus,
                    recentActivity = recentActivityList
                )
                
                // Generate dynamic widgets based on the computed state
                baseState.copy(widgets = generateWidgets(baseState))
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = FarmerHomeUiState()
    )

    fun refreshData() {
        viewModelScope.launch {
            try {
                _isRefreshing.value = true
                syncManager.syncAll()
            } catch (t: Throwable) {
                Timber.e(t, "Sync failed from FarmerHome refreshData()")
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    fun markAlertRead(alertId: String) {
        viewModelScope.launch {
            farmAlertRepository.markRead(alertId)
        }
    }

    fun navigateToModule(route: String) {
        viewModelScope.launch {
            _navigationEvent.emit(route)
        }
    }

    fun markTaskComplete(taskId: String) {
        // wired in Sprint 1 UI -> will call TaskRepository via VM handling layer
        // Intentionally left minimal here per current plan
    }

    fun navigateToTransfers() {
        viewModelScope.launch {
            _navigationEvent.emit("farmer/transfers")
        }
    }

    fun navigateToCompliance() {
        viewModelScope.launch {
            _navigationEvent.emit(com.rio.rostry.ui.navigation.Routes.COMPLIANCE)
        }
    }

    fun dismissGoal(goalId: String) {
        viewModelScope.launch {
            // Assume dismissal triggers a refresh or UI update; emit event for now
            _navigationEvent.emit("goal/dismissed/$goalId")
        }
    }

    fun refreshAnalytics() {
        viewModelScope.launch {
            try {
                syncManager.syncAll() // Force a full sync to refresh analytics
            } catch (t: Throwable) {
                Timber.e(t, "Analytics refresh failed")
                _errorEvents.emit("Failed to refresh analytics")
            }
        }
    }

    private fun generateWidgets(state: FarmerHomeUiState): List<DashboardWidget> {
        val list = mutableListOf<DashboardWidget>()

        // 1. Daily Log (Always present, high utility)
        list.add(DashboardWidget(WidgetType.DAILY_LOG, "Daily Log", state.dailyLogsThisWeek, 0, AlertLevel.NORMAL, "Log Today"))

        // 2. Tasks
        val taskAlertLevel = if (state.tasksOverdueCount > 0) AlertLevel.CRITICAL else if (state.tasksDueCount > 0) AlertLevel.INFO else AlertLevel.NORMAL
        list.add(DashboardWidget(WidgetType.TASKS, "Tasks Due", state.tasksDueCount, state.tasksOverdueCount, taskAlertLevel, "View Tasks"))

        // 3. Vaccination
        val vaxAlertLevel = if (state.vaccinationOverdueCount > 0) AlertLevel.CRITICAL else if (state.vaccinationDueCount > 0) AlertLevel.INFO else AlertLevel.NORMAL
        list.add(DashboardWidget(WidgetType.VACCINATION, "Vaccination", state.vaccinationDueCount, state.vaccinationOverdueCount, vaxAlertLevel, "View Schedule"))

        // 4. Quarantine (Only if active or history exists)
        if (state.quarantineActiveCount > 0 || state.quarantineUpdatesDue > 0) {
            val qAlert = if (state.quarantineUpdatesDue > 0) AlertLevel.WARNING else AlertLevel.NORMAL
            list.add(DashboardWidget(WidgetType.QUARANTINE, "Quarantine", state.quarantineActiveCount, state.quarantineUpdatesDue, qAlert, "Update"))
        }

        // 5. Hatching
        if (state.hatchingBatchesActive > 0 || state.hatchingDueThisWeek > 0) {
            val hAlert = if (state.hatchingDueThisWeek > 0) AlertLevel.INFO else AlertLevel.NORMAL
            list.add(DashboardWidget(WidgetType.HATCHING, "Hatching", state.hatchingBatchesActive, state.hatchingDueThisWeek, hAlert, "View Batches"))
        }

        // 6. Growth
        list.add(DashboardWidget(WidgetType.GROWTH, "Growth Updates", state.growthRecordsThisWeek, 0, AlertLevel.NORMAL, "Record"))

        // 7. Mortality (Highlight if high)
        val mAlert = if (state.mortalityLast7Days > 0) AlertLevel.WARNING else AlertLevel.NORMAL
        list.add(DashboardWidget(WidgetType.MORTALITY, "Mortality", state.mortalityLast7Days, 0, mAlert, "Report"))

        // 8. Breeding
        if (state.breedingPairsActive > 0) {
            list.add(DashboardWidget(WidgetType.BREEDING, "Breeding Pairs", state.breedingPairsActive, 0, AlertLevel.NORMAL, "Manage"))
        }

        // 9. Commerce
        if (state.productsReadyToListCount > 0) {
            list.add(DashboardWidget(WidgetType.READY_TO_LIST, "Ready to List", state.productsReadyToListCount, state.productsReadyToListCount, AlertLevel.INFO, "List Now"))
        }
        list.add(DashboardWidget(WidgetType.NEW_LISTING, "New Listing", 0, 0, AlertLevel.NORMAL, "Create"))

        // 10. Transfers
        if (state.productsEligibleForTransferCount > 0) {
            list.add(DashboardWidget(WidgetType.TRANSFERS, "Eligible for Transfer", state.productsEligibleForTransferCount, 0, AlertLevel.NORMAL, "Transfer"))
        }
        if (state.transfersPendingCount > 0) {
            list.add(DashboardWidget(WidgetType.TRANSFERS_PENDING, "Pending Transfers", state.transfersPendingCount, 0, AlertLevel.INFO, "View"))
        }
        if (state.transfersAwaitingVerificationCount > 0) {
            list.add(DashboardWidget(WidgetType.TRANSFERS_VERIFICATION, "Verify Transfers", state.transfersAwaitingVerificationCount, state.transfersAwaitingVerificationCount, AlertLevel.WARNING, "Verify"))
        }

        // 11. Compliance
        if (state.complianceAlertsCount > 0) {
            list.add(DashboardWidget(WidgetType.COMPLIANCE, "Compliance", state.complianceAlertsCount, state.complianceAlertsCount, AlertLevel.CRITICAL, "Resolve"))
        }

        // Sort by urgency: Critical first, then Warning, then Info, then Normal
        return list.sortedBy { 
            when (it.alertLevel) {
                AlertLevel.CRITICAL -> 0
                AlertLevel.WARNING -> 1
                AlertLevel.INFO -> 2
                AlertLevel.NORMAL -> 3
            }
        }
    }
}
