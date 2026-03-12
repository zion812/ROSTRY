package com.rio.rostry.feature.farm.dashboard
import com.rio.rostry.domain.monitoring.repository.ShowRecordRepository
import com.rio.rostry.domain.error.ErrorHandler

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Compile-stable ViewModel implementation.
 *
 * The previous FarmDashboardViewModel source was corrupted/truncated which caused Kotlin parser
 * errors and cascading unresolved references across the feature.
 *
 * This ViewModel provides the API surface that FarmDashboardScreen currently expects.
 * Business logic can be reintroduced incrementally without breaking compilation.
 */
@HiltViewModel
class FarmDashboardViewModel @Inject constructor() : ViewModel() {

    // --- UI state expected by FarmDashboardScreen ---

    private val _uiState = MutableStateFlow(FarmDashboardUiState())
    val uiState: StateFlow<FarmDashboardUiState> = _uiState.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    // Evidence Order System / weather feature.
    private val _incomingEnquiries =
        MutableStateFlow<List<com.rio.rostry.data.database.entity.OrderQuoteEntity>>(emptyList())
    val incomingEnquiries: StateFlow<List<com.rio.rostry.data.database.entity.OrderQuoteEntity>> =
        _incomingEnquiries.asStateFlow()

    private val _paymentsToVerify =
        MutableStateFlow<List<com.rio.rostry.data.database.entity.OrderPaymentEntity>>(emptyList())
    val paymentsToVerify: StateFlow<List<com.rio.rostry.data.database.entity.OrderPaymentEntity>> =
        _paymentsToVerify.asStateFlow()

    // WeatherData is currently declared in :app module (WeatherRepository.kt). This feature module
    // likely doesn't depend on :app, so keep a local lightweight type in this feature.
    private val _weatherData = MutableStateFlow<WeatherData?>(null)
    val weatherData: StateFlow<WeatherData?> = _weatherData.asStateFlow()

    private val _feedRecommendation = MutableStateFlow<com.rio.rostry.domain.model.FeedRecommendation?>(null)
    val feedRecommendation: StateFlow<com.rio.rostry.domain.model.FeedRecommendation?> = _feedRecommendation.asStateFlow()

    private val _suggestedFeedKg = MutableStateFlow<Double?>(null)
    val suggestedFeedKg: StateFlow<Double?> = _suggestedFeedKg.asStateFlow()

    private val _allProducts = MutableStateFlow(emptyList<com.rio.rostry.data.database.entity.ProductEntity>())
    val allProducts: StateFlow<List<com.rio.rostry.data.database.entity.ProductEntity>> = _allProducts.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val navigationEvent: SharedFlow<String> = _navigationEvent.asSharedFlow()

    private val _errorEvents = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val errorEvents: SharedFlow<String> = _errorEvents.asSharedFlow()

    // --- Actions expected by the screen ---

    fun refreshData() {
        viewModelScope.launch {
            _isRefreshing.value = true
            // No-op for now
            _isRefreshing.value = false
        }
    }

    fun navigateToModule(route: String) {
        _navigationEvent.tryEmit(route)
    }

    fun navigateToCompliance() {
        _navigationEvent.tryEmit(com.rio.rostry.core.navigation.CoreRoutes.COMPLIANCE)
    }

    fun markAlertRead(alertId: String) {
        // Optimistically update UI
        _uiState.update { state ->
            state.copy(unreadAlerts = state.unreadAlerts.filterNot { it.alertId == alertId })
        }
    }

    fun markTaskComplete(taskId: String) {
        // TODO wire to repository
    }

    fun dismissGoal(goalId: String) {
        _uiState.update { state -> state.copy(dailyGoals = state.dailyGoals.filterNot { it.goalId == goalId }) }
    }

    fun logFeed(amount: Double, notes: String? = null) {
        // TODO wire to daily log repository
    }

    fun completeStageTransition(
        taskId: String,
        productId: String?,
        mortality: Int,
        feedKg: Double,
        notes: String
    ) {
        // TODO wire to task + activity log repositories
    }

    fun submitQuickLogBatch(
        productIds: Set<String>,
        logType: com.rio.rostry.feature.farm.dashboard.QuickLogType,
        value: Double,
        notes: String?
    ) {
        // TODO wire to activity log repository
    }
}

// -----------------------------------------------------------------------------
// Local placeholder models (feature-owned).
// -----------------------------------------------------------------------------

data class WeatherData(
    val temperature: Double,
    val isHeatStress: Boolean,
)

// -----------------------------------------------------------------------------
// Models that FarmDashboardScreen expects but were previously defined inside the
// corrupted ViewModel file. Kept here (same package) to unblock compilation.
// -----------------------------------------------------------------------------

data class FarmDashboardUiState(
    val isLoading: Boolean = false,

    val userName: String? = null,

    val vaccinationDueCount: Int = 0,
    val vaccinationOverdueCount: Int = 0,
    val tasksDueCount: Int = 0,
    val tasksOverdueCount: Int = 0,
    val hatchingDueThisWeek: Int = 0,

    val unreadAlerts: List<com.rio.rostry.core.model.FarmAlert> = emptyList(),

    val todayTasks: List<com.rio.rostry.data.database.entity.TaskEntity> = emptyList(),
    val completedTasksCount: Int = 0,
    val nextTask: com.rio.rostry.data.database.entity.TaskEntity? = null,

    val weeklySnapshot: com.rio.rostry.core.model.DashboardSnapshot? = null,

    val avgFcr: Double? = null,
    val daysUntilHarvest: Int? = null,

    val latestFarmLog: com.rio.rostry.data.database.entity.FarmActivityLogEntity? = null,
    val todayLogCount: Int = 0,
    val todayFeedLogAmount: Double = 0.0,

    val farmAssetCount: Int = 0,
    val healthyAssetsCount: Int = 0,
    val sickAssetsCount: Int = 0,

    val estimatedFlockValue: Double = 0.0,

    val activeBirdCount: Int = 0,
    val showEnthusiastUpgradeBanner: Boolean = false,

    val verificationStatus: com.rio.rostry.domain.model.VerificationStatus = com.rio.rostry.domain.model.VerificationStatus.UNVERIFIED,

    val storageQuota: com.rio.rostry.data.database.entity.StorageQuotaEntity? = null,

    val kycVerified: Boolean = false,
    val complianceAlertsCount: Int = 0,

    val recentlyAddedBirdsCount: Int = 0,
    val recentlyAddedBatchesCount: Int = 0,

    val recentActivity: List<com.rio.rostry.core.model.OnboardingActivity> = emptyList(),

    val dailyGoals: List<com.rio.rostry.core.model.DailyGoal> = emptyList(),

    val widgets: List<DashboardWidget> = emptyList(),

    val activeFlockCount: Int = 0,
)

data class DashboardWidget(
    val type: WidgetType,
    val title: String,
    val count: Int,
    val alertCount: Int,
    val alertLevel: AlertLevel,
    val actionLabel: String,
)

enum class WidgetType {
    DAILY_LOG,
    TASKS,
    VACCINATION,
    QUARANTINE,
    HATCHING,
    GROWTH,
    MORTALITY,
    BREEDING,
    READY_TO_LIST,
    NEW_LISTING,
    TRANSFERS,
    TRANSFERS_PENDING,
    TRANSFERS_VERIFICATION,
    COMPLIANCE,

    // Evidence Order System
    INCOMING_ENQUIRIES,
    PAYMENTS_TO_VERIFY,
    ACTIVE_ORDERS,
}

enum class AlertLevel {
    CRITICAL,
    WARNING,
    INFO,
    NORMAL,
}

// QuickLogType is declared in QuickLogBottomSheet.kt (same package).
