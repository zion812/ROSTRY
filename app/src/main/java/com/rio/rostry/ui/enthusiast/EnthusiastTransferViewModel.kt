package com.rio.rostry.ui.enthusiast

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.database.dao.TransferDao
import com.rio.rostry.data.database.entity.TransferEntity
import com.rio.rostry.data.repository.TransferWorkflowRepository
import com.rio.rostry.session.CurrentUserProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

@HiltViewModel
class EnthusiastTransferViewModel @Inject constructor(
    private val transferDao: TransferDao,
    private val currentUserProvider: CurrentUserProvider,
    private val workflow: TransferWorkflowRepository,
) : ViewModel() {

    data class UiState(
        val loading: Boolean = true,
        val pending: List<TransferEntity> = emptyList(),
        val history: List<TransferEntity> = emptyList(),
        val error: String? = null,
        val statusFilter: String = "ALL", // ALL, PENDING, VERIFIED, COMPLETED, DISPUTED
        val typeFilter: String = "ALL", // ALL, INCOMING, OUTGOING
        val startDate: Long? = null,
        val endDate: Long? = null,
        val selection: Set<String> = emptySet(),
    )

    private val _state = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = _state.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            try {
                _state.value = _state.value.copy(loading = true, error = null)
                val uid = currentUserProvider.userIdOrNull()
                if (uid.isNullOrBlank()) {
                    _state.value = UiState(loading = false, pending = emptyList(), history = emptyList(), error = null)
                    return@launch
                }
                val from = transferDao.getTransfersFromUser(uid)
                val to = transferDao.getTransfersToUser(uid)
                combine(from, to) { a, b -> (a + b).distinctBy { it.transferId } }.
                    collect { all ->
                        val f = _state.value
                        val typed = when (f.typeFilter) {
                            "INCOMING" -> all.filter { it.toUserId == uid }
                            "OUTGOING" -> all.filter { it.fromUserId == uid }
                            else -> all
                        }
                        val dated = typed.filter { t ->
                            (f.startDate == null || (t.initiatedAt ?: 0L) >= f.startDate) &&
                            (f.endDate == null || (t.initiatedAt ?: 0L) <= f.endDate)
                        }
                        val pending = dated.filter { it.status.equals("PENDING", ignoreCase = true) }
                            .sortedByDescending { it.initiatedAt }
                        val history = dated.filter { !it.status.equals("PENDING", ignoreCase = true) }
                            .sortedByDescending { it.initiatedAt }
                        val statusFilteredPending = when (f.statusFilter) {
                            "PENDING" -> pending
                            else -> pending
                        }
                        val statusFilteredHistory = when (f.statusFilter) {
                            "VERIFIED" -> history.filter { it.status.equals("VERIFIED", true) }
                            "COMPLETED" -> history.filter { it.status.equals("COMPLETED", true) }
                            "DISPUTED" -> history.filter { it.status.equals("DISPUTED", true) }
                            else -> history
                        }
                        _state.value = f.copy(loading = false, pending = statusFilteredPending, history = statusFilteredHistory)
                    }
            } catch (e: Exception) {
                _state.value = _state.value.copy(loading = false, error = e.message)
            }
        }
    }

    fun setStatusFilter(value: String) { _state.value = _state.value.copy(statusFilter = value); refresh() }
    fun setTypeFilter(value: String) { _state.value = _state.value.copy(typeFilter = value); refresh() }
    fun setDateRange(start: Long?, end: Long?) { _state.value = _state.value.copy(startDate = start, endDate = end); refresh() }

    fun toggleSelection(id: String) {
        val cur = _state.value.selection.toMutableSet()
        if (!cur.add(id)) cur.remove(id)
        _state.value = _state.value.copy(selection = cur)
    }
    fun clearSelection() { _state.value = _state.value.copy(selection = emptySet()) }

    fun bulkCancelSelected() {
        val ids = _state.value.selection.toList()
        if (ids.isEmpty()) return
        viewModelScope.launch {
            ids.forEach { workflow.cancel(it, reason = "User bulk cancel") }
            clearSelection()
            refresh()
        }
    }

    fun requestPlatformReviewSelected() {
        val ids = _state.value.selection.toList()
        if (ids.isEmpty()) return
        viewModelScope.launch {
            ids.forEach { workflow.platformApproveIfNeeded(it) }
            clearSelection()
            refresh()
        }
    }

    suspend fun computeTrustScore(transferId: String): Int? {
        return when (val res = workflow.computeTrustScore(transferId)) {
            is com.rio.rostry.utils.Resource.Success -> res.data
            else -> null
        }
    }

    suspend fun generateDocumentation(transferId: String): String? {
        return when (val res = workflow.generateDocumentation(transferId)) {
            is com.rio.rostry.utils.Resource.Success -> res.data
            else -> null
        }
    }
}
