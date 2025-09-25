package com.rio.rostry.ui.enthusiast

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.database.dao.TransferDao
import com.rio.rostry.data.database.entity.TransferEntity
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
    private val currentUserProvider: CurrentUserProvider
) : ViewModel() {

    data class UiState(
        val loading: Boolean = true,
        val pending: List<TransferEntity> = emptyList(),
        val history: List<TransferEntity> = emptyList(),
        val error: String? = null
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
                        val pending = all.filter { it.status.equals("PENDING", ignoreCase = true) }
                            .sortedByDescending { it.initiatedAt }
                        val history = all.filter { !it.status.equals("PENDING", ignoreCase = true) }
                            .sortedByDescending { it.initiatedAt }
                        _state.value = UiState(loading = false, pending = pending, history = history, error = null)
                    }
            } catch (e: Exception) {
                _state.value = _state.value.copy(loading = false, error = e.message)
            }
        }
    }
}
