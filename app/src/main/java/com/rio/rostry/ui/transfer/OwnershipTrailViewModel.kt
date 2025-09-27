package com.rio.rostry.ui.transfer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.database.dao.TransferDao
import com.rio.rostry.data.database.dao.TransferVerificationDao
import com.rio.rostry.data.database.entity.TransferEntity
import com.rio.rostry.data.database.entity.TransferVerificationEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OwnershipTrailViewModel @Inject constructor(
    private val transferDao: TransferDao,
    private val verificationDao: TransferVerificationDao,
) : ViewModel() {

    data class UiState(
        val loading: Boolean = true,
        val transfer: TransferEntity? = null,
        val verifications: List<TransferVerificationEntity> = emptyList(),
        val error: String? = null
    )

    private val _state = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = _state.asStateFlow()

    fun load(transferId: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(loading = true, error = null)
            try {
                val t = transferDao.getTransferById(transferId)
                val v = verificationDao.streamByTransfer(transferId)
                combine(t, v) { t1, v1 -> t1 to v1 }.collect { (te, list) ->
                    _state.value = _state.value.copy(loading = false, transfer = te, verifications = list)
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(loading = false, error = e.message)
            }
        }
    }
}
