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
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.flatMapLatest
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
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

    private val statusFilter = MutableStateFlow("ALL")
    private val typeFilter = MutableStateFlow("ALL")
    private val dateRange = MutableStateFlow<Pair<Long?, Long?>>(null to null)
    private val refreshTick = MutableStateFlow(0)

    init {
        viewModelScope.launch {
            try {
                _state.update { it.copy(loading = true, error = null) }
                val uid = currentUserProvider.userIdOrNull()
                if (uid.isNullOrBlank()) {
                    _state.value = UiState(loading = false, pending = emptyList(), history = emptyList(), error = null)
                    return@launch
                }
                combine(
                    statusFilter,
                    typeFilter,
                    dateRange,
                    refreshTick
                ) { s, t, range, _ ->
                    Triple(s, t, range)
                }
                    .debounce(200)
                    .flatMapLatest { (s, t, range) ->
                        val (startDate, endDate) = range
                        // Choose DAO flow to push filters to DB where possible
                        when {
                            t != "ALL" -> transferDao.getUserTransfersByTypeBetween(uid, t, startDate, endDate)
                            // For status, we only constrain history list; pending is split anyway. Keep base flow broad.
                            else -> transferDao.getUserTransfersBetween(uid, startDate, endDate)
                        }.distinctUntilChanged()
                            .map { all -> Pair(all, Triple(s, t, range)) }
                    }
                    .collect { pair ->
                        val all = pair.first
                        val (s, t, range) = pair.second
                        val (startDate, endDate) = range
                        val dated = all // already date-filtered by DAO
                        val pending = dated.filter { it.status.equals("PENDING", true) }
                            .sortedByDescending { it.initiatedAt }
                        val historyBase = dated.filter { !it.status.equals("PENDING", true) }
                        val statusFilteredHistory = when (s) {
                            "VERIFIED" -> historyBase.filter { it.status.equals("VERIFIED", true) }
                            "COMPLETED" -> historyBase.filter { it.status.equals("COMPLETED", true) }
                            "DISPUTED" -> historyBase.filter { it.status.equals("DISPUTED", true) }
                            else -> historyBase
                        }.sortedByDescending { it.initiatedAt }
                        _state.update {
                            it.copy(
                                loading = false,
                                pending = pending,
                                history = statusFilteredHistory,
                                statusFilter = s,
                                typeFilter = t,
                                startDate = startDate,
                                endDate = endDate
                            )
                        }
                    }
            } catch (e: Exception) {
                _state.update { it.copy(loading = false, error = e.message) }
            }
        }
    }

    // Paging 3 for history (excludes PENDING in DAO). Recomputes on filter changes.
    val pagingHistoryFlow: Flow<PagingData<TransferEntity>> = combine(
        typeFilter,
        statusFilter,
        dateRange,
        refreshTick
    ) { t, s, r, _ ->
        Triple(t, s, r)
    }
        .debounce(200)
        .flatMapLatest { triple ->
            val (t, s, range) = triple
            val (start, end) = range
            val uid = currentUserProvider.userIdOrNull() ?: ""
            val typeOrNull = t.takeIf { it != "ALL" }
            val statusOrNull = s.takeIf { it !in setOf("ALL", "PENDING") }
            Pager(
                config = PagingConfig(pageSize = 20, prefetchDistance = 10, enablePlaceholders = false)
            ) {
                transferDao.pagingHistory(
                    userId = uid,
                    type = typeOrNull,
                    status = statusOrNull,
                    start = start,
                    end = end
                )
            }.flow
        }
        .cachedIn(viewModelScope)

    fun setStatusFilter(value: String) { statusFilter.value = value; _state.update { it.copy(statusFilter = value) } }
    fun setTypeFilter(value: String) { typeFilter.value = value; _state.update { it.copy(typeFilter = value) } }
    fun setDateRange(start: Long?, end: Long?) { dateRange.value = (start to end); _state.update { it.copy(startDate = start, endDate = end) } }

    fun refresh() { refreshTick.value = refreshTick.value + 1 }

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
        }
    }

    fun requestPlatformReviewSelected() {
        val ids = _state.value.selection.toList()
        if (ids.isEmpty()) return
        viewModelScope.launch {
            ids.forEach { workflow.platformApproveIfNeeded(it) }
            clearSelection()
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
