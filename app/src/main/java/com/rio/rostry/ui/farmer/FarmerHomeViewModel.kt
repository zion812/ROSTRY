package com.rio.rostry.ui.farmer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.database.entity.FarmAlertEntity
import com.rio.rostry.data.database.entity.FarmerDashboardSnapshotEntity
import com.rio.rostry.data.repository.monitoring.BreedingRepository
import com.rio.rostry.data.repository.monitoring.FarmAlertRepository
import com.rio.rostry.data.repository.monitoring.FarmerDashboardRepository
import dagger.hilt.android.lifecycle.HiltViewModel
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
    val unreadAlerts: List<FarmAlertEntity> = emptyList(),
    val weeklySnapshot: FarmerDashboardSnapshotEntity? = null,
    val isLoading: Boolean = true
)

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
    private val firebaseAuth: com.google.firebase.auth.FirebaseAuth
) : ViewModel() {

    private val _navigationEvent = MutableSharedFlow<String>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    // Reactive time ticker for live updates (updates every minute)
    private val timeTickerFlow = flow {
        while (true) {
            emit(System.currentTimeMillis())
            delay(60_000) // Update every minute
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), System.currentTimeMillis())

    // Reactive farmerId from Firebase Auth
    private val farmerId: Flow<String> = callbackFlow {
        val authStateListener = com.google.firebase.auth.FirebaseAuth.AuthStateListener { auth ->
            auth.currentUser?.uid?.let { trySend(it) }
        }
        firebaseAuth.addAuthStateListener(authStateListener)
        firebaseAuth.currentUser?.uid?.let { send(it) }
        awaitClose { firebaseAuth.removeAuthStateListener(authStateListener) }
    }.distinctUntilChanged()

    val uiState: StateFlow<FarmerHomeUiState> = farmerId.flatMapLatest { id: String ->
        timeTickerFlow.flatMapLatest { now: Long ->
            // Compute time windows reactively
            val endOfDay = now + TimeUnit.DAYS.toMillis(1)
            val weekStart = now - TimeUnit.DAYS.toMillis(7)
            val weekEnd = now + TimeUnit.DAYS.toMillis(7)
            val twelveHoursAgo = now - TimeUnit.HOURS.toMillis(12)
            
            combine(
                vaccinationRecordDao.observeDueForFarmer(id, now, endOfDay),
                vaccinationRecordDao.observeOverdueForFarmer(id, now),
                growthRecordDao.observeCountForFarmerBetween(id, weekStart, now),
                quarantineRecordDao.observeActiveForFarmer(id),
                quarantineRecordDao.observeUpdatesOverdueForFarmer(id, twelveHoursAgo),
                hatchingBatchDao.observeActiveForFarmer(id, now),
                hatchingBatchDao.observeDueThisWeekForFarmer(id, now, weekEnd),
                mortalityRecordDao.observeCountForFarmerBetween(id, weekStart, now),
                breedingRepository.observeActiveCount(id),
                farmAlertRepository.observeUnread(id),
                farmerDashboardRepository.observeLatest(id)
            ) { values: Array<Any?> ->
                val vacDue = values[0] as Int
                val vacOverdue = values[1] as Int
                val growth = values[2] as Int
                val quarActive = values[3] as Int
                val quarOverdue = values[4] as Int
                val hatchActive = values[5] as Int
                val hatchDue = values[6] as Int
                val mortality = values[7] as Int
                val breeding = values[8] as Int
                val alerts = values[9] as List<FarmAlertEntity>
                val snapshot = values[10] as FarmerDashboardSnapshotEntity?
                
                FarmerHomeUiState(
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
                    unreadAlerts = alerts,
                    weeklySnapshot = snapshot,
                    isLoading = false
                )
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = FarmerHomeUiState()
    )

    fun refreshData() {
        // Flows are reactive and auto-update; no manual refresh needed
        // This method kept for compatibility
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
}
