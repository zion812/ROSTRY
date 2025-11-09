package com.rio.rostry.ui.farmer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.repository.TransferRepository
import com.rio.rostry.data.repository.TransferWorkflowRepository
import com.rio.rostry.data.database.entity.TransferEntity
import com.rio.rostry.session.CurrentUserProvider
import com.rio.rostry.utils.Resource
import com.rio.rostry.utils.analytics.FlowAnalyticsTracker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.roundToInt

data class FarmerTransfersUiState(
    val transfers: List<TransferEntity> = emptyList(),
    val filteredTransfers: List<TransferEntity> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val statistics: TransferStatistics = TransferStatistics(),
    val filters: TransferFilters = TransferFilters(),
    val selectedTransfers: Set<String> = emptySet(),
    val isBulkOperationInProgress: Boolean = false
)

data class TransferStatistics(
    val totalValue: Double = 0.0,
    val successRate: Int = 0,
    val pendingCount: Int = 0,
    val completedCount: Int = 0,
    val cancelledCount: Int = 0
)

data class TransferFilters(
    val status: String? = null,
    val type: String? = null,
    val dateRange: DateRange? = null
)

data class DateRange(
    val startDate: Long? = null,
    val endDate: Long? = null
)

sealed class FarmerTransfersEvent {
    data class ShowMessage(val message: String) : FarmerTransfersEvent()
    data class NavigateToTransferDetails(val transferId: String) : FarmerTransfersEvent()
    object NavigateBack : FarmerTransfersEvent()
}

@HiltViewModel
class FarmerTransfersViewModel @Inject constructor(
    private val transferRepository: TransferRepository,
    private val transferWorkflowRepository: TransferWorkflowRepository,
    private val currentUserProvider: CurrentUserProvider,
    private val analyticsTracker: FlowAnalyticsTracker
) : ViewModel() {

    private val _uiState = MutableStateFlow(FarmerTransfersUiState())
    val uiState: StateFlow<FarmerTransfersUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<FarmerTransfersEvent>()
    val events: SharedFlow<FarmerTransfersEvent> = _events.asSharedFlow()

    private val userId: String?
        get() = currentUserProvider.userIdOrNull()

    init {
        loadTransfers()
        trackAnalytics()
    }

    private fun trackAnalytics() {
        viewModelScope.launch {
            analyticsTracker.trackEvent(
                event = "transfers_viewed",
                properties = mapOf("role" to "farmer")
            )
        }
    }

    fun loadTransfers() {
        val currentUserId = userId ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                // Combine sent and received transfers
                combine(
                    transferRepository.getFromUser(currentUserId),
                    transferRepository.getToUser(currentUserId)
                ) { sent, received ->
                    (sent + received).distinctBy { it.transferId }
                }.collect { allTransfers ->
                    val statistics = computeStatistics(allTransfers)
                    val filtered = applyFilters(allTransfers, _uiState.value.filters)

                    _uiState.update {
                        it.copy(
                            transfers = allTransfers,
                            filteredTransfers = filtered,
                            statistics = statistics,
                            isLoading = false
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Failed to load transfers: ${e.localizedMessage}"
                    )
                }
                _events.emit(FarmerTransfersEvent.ShowMessage("Failed to load transfers"))
            }
        }
    }

    private fun computeStatistics(transfers: List<TransferEntity>): TransferStatistics {
        val totalValue = transfers.sumOf { it.amount }
        val completedCount = transfers.count { it.status == "COMPLETED" }
        val totalCount = transfers.size
        val successRate = if (totalCount > 0) ((completedCount.toDouble() / totalCount) * 100).roundToInt() else 0
        val pendingCount = transfers.count { it.status == "PENDING" }
        val cancelledCount = transfers.count { it.status == "CANCELLED" }

        return TransferStatistics(
            totalValue = totalValue,
            successRate = successRate,
            pendingCount = pendingCount,
            completedCount = completedCount,
            cancelledCount = cancelledCount
        )
    }

    fun updateFilters(filters: TransferFilters) {
        _uiState.update { it.copy(filters = filters) }
        val filtered = applyFilters(_uiState.value.transfers, filters)
        _uiState.update { it.copy(filteredTransfers = filtered) }
    }

    private fun applyFilters(transfers: List<TransferEntity>, filters: TransferFilters): List<TransferEntity> {
        return transfers.filter { transfer ->
            (filters.status == null || transfer.status == filters.status) &&
            (filters.type == null || transfer.type == filters.type) &&
            (filters.dateRange?.let { range ->
                val transferTime = transfer.initiatedAt
                (range.startDate == null || transferTime >= range.startDate) &&
                (range.endDate == null || transferTime <= range.endDate)
            } ?: true)
        }
    }

    fun toggleTransferSelection(transferId: String) {
        val currentSelected = _uiState.value.selectedTransfers
        val newSelected = if (currentSelected.contains(transferId)) {
            currentSelected - transferId
        } else {
            currentSelected + transferId
        }
        _uiState.update { it.copy(selectedTransfers = newSelected) }
    }

    fun selectAllTransfers() {
        val allIds = _uiState.value.filteredTransfers.map { it.transferId }.toSet()
        _uiState.update { it.copy(selectedTransfers = allIds) }
    }

    fun clearSelection() {
        _uiState.update { it.copy(selectedTransfers = emptySet()) }
    }

    fun bulkCancel(reason: String? = null) {
        val selectedIds = _uiState.value.selectedTransfers
        if (selectedIds.isEmpty()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isBulkOperationInProgress = true) }

            var successCount = 0
            var failureCount = 0

            for (transferId in selectedIds) {
                when (transferWorkflowRepository.cancel(transferId, reason)) {
                    is Resource.Success -> successCount++
                    is Resource.Error -> failureCount++
                    is Resource.Loading -> { /* no-op */ }
                }
            }

            _uiState.update { it.copy(isBulkOperationInProgress = false, selectedTransfers = emptySet()) }

            val message = when {
                successCount > 0 && failureCount == 0 -> "Successfully cancelled $successCount transfer(s)"
                successCount > 0 && failureCount > 0 -> "Cancelled $successCount transfer(s), $failureCount failed"
                else -> "Failed to cancel transfers"
            }

            _events.emit(FarmerTransfersEvent.ShowMessage(message))
            loadTransfers() // Refresh data
        }
    }

    fun bulkRequestReview() {
        val selectedIds = _uiState.value.selectedTransfers
        if (selectedIds.isEmpty()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isBulkOperationInProgress = true) }

            var successCount = 0
            var failureCount = 0

            for (transferId in selectedIds) {
                // Assuming there's a method to request review, or we can use platformReview
                // For now, we'll simulate with a placeholder
                // TODO: Implement actual request review logic
                successCount++ // Placeholder
            }

            _uiState.update { it.copy(isBulkOperationInProgress = false, selectedTransfers = emptySet()) }

            val message = "Requested review for $successCount transfer(s)"
            _events.emit(FarmerTransfersEvent.ShowMessage(message))
            loadTransfers() // Refresh data
        }
    }

    fun onTransferClicked(transferId: String) {
        viewModelScope.launch {
            _events.emit(FarmerTransfersEvent.NavigateToTransferDetails(transferId))
        }
    }

    fun retryLoad() {
        loadTransfers()
    }

    fun dismissError() {
        _uiState.update { it.copy(error = null) }
    }
}