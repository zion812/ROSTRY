package com.rio.rostry.ui.components

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.rio.rostry.data.database.dao.OutboxDao
import com.rio.rostry.data.database.dao.SyncStateDao
import com.rio.rostry.data.sync.SyncManager
import com.rio.rostry.utils.network.ConnectivityManager
import com.rio.rostry.workers.OutboxSyncWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SyncStatusViewModel @Inject constructor(
    private val connectivityManager: ConnectivityManager,
    private val outboxDao: OutboxDao,
    private val syncManager: SyncManager,
    private val syncStateDao: SyncStateDao,
    private val workManager: WorkManager
) : ViewModel() {

    data class SyncState(
        val status: SyncStatus,
        val pendingCount: Int,
        val lastSyncTimestamp: Long?,
        val errorMessage: String?,
        val isRetrying: Boolean
    )

    enum class SyncStatus { SYNCED, SYNCING, OFFLINE, ERROR }

    sealed class SyncEvent {
        object ViewSyncDetails : SyncEvent()
    }

    private val _syncState = MutableStateFlow(
        SyncState(
            status = SyncStatus.OFFLINE,
            pendingCount = 0,
            lastSyncTimestamp = null,
            errorMessage = null,
            isRetrying = false
        )
    )
    val syncState: StateFlow<SyncState> = _syncState.asStateFlow()

    private val _events = MutableSharedFlow<SyncEvent>()
    val events: SharedFlow<SyncEvent> = _events.asSharedFlow()

    init {
        viewModelScope.launch {
            combine(
                connectivityManager.observe(),
                outboxDao.getPendingCount(),
                syncManager.syncProgressFlow,
                syncStateDao.observe() // Assuming SyncStateDao has observe() returning Flow<SyncStateEntity?>
            ) { connectivity, pending, progress, syncEntity ->
                val lastSyncTimestamp = syncEntity?.let {
                    // Assuming SyncStateEntity has fields like lastProductSyncAt, etc.
                    maxOf(
                        it.lastProductSyncAt,
                        it.lastOrderSyncAt,
                        it.lastTransferSyncAt,
                        it.lastTrackingSyncAt,
                        it.lastChatSyncAt,
                        it.lastBreedingSyncAt,
                        it.lastAlertSyncAt,
                        it.lastDashboardSyncAt,
                        it.lastVaccinationSyncAt,
                        it.lastGrowthSyncAt,
                        it.lastQuarantineSyncAt,
                        it.lastMortalitySyncAt,
                        it.lastHatchingSyncAt,
                        it.lastDailyLogSyncAt,
                        it.lastTaskSyncAt,
                        it.lastUserSyncAt,
                        it.lastEnthusiastBreedingSyncAt,
                        it.lastEnthusiastDashboardSyncAt
                    )
                }

                val errorMessage = if (progress.errors.isNotEmpty()) progress.errors.joinToString("; ") else null

                val status = when {
                    !connectivity.isOnline -> SyncStatus.OFFLINE
                    pending > 0 -> SyncStatus.SYNCING
                    errorMessage != null -> SyncStatus.ERROR
                    else -> SyncStatus.SYNCED
                }

                SyncState(
                    status = status,
                    pendingCount = pending,
                    lastSyncTimestamp = lastSyncTimestamp,
                    errorMessage = errorMessage,
                    isRetrying = false // Can be updated based on retry action
                )
            }.catch { e ->
                // Handle errors, e.g., emit ERROR status
                _syncState.value = _syncState.value.copy(
                    status = SyncStatus.ERROR,
                    errorMessage = e.message ?: "Unknown error"
                )
            }.collect { state ->
                _syncState.value = state
            }
        }
    }

    fun retrySync() {
        val workRequest = OneTimeWorkRequestBuilder<OutboxSyncWorker>().build()
        workManager.enqueue(workRequest)
        // Optionally update isRetrying
        _syncState.value = _syncState.value.copy(isRetrying = true)
    }

    fun viewSyncDetails() {
        viewModelScope.launch {
            _events.emit(SyncEvent.ViewSyncDetails)
        }
    }
}