package com.rio.rostry.ui.transfer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.database.dao.AuditLogDao
import com.rio.rostry.data.database.dao.DisputeDao
import com.rio.rostry.data.database.dao.TransferDao
import com.rio.rostry.data.database.dao.TransferVerificationDao
import com.rio.rostry.data.database.entity.AuditLogEntity
import com.rio.rostry.data.database.entity.DisputeEntity
import com.rio.rostry.data.database.entity.TransferEntity
import com.rio.rostry.data.database.entity.TransferVerificationEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransferDetailsViewModel @Inject constructor(
    private val transferDao: TransferDao,
    private val verificationDao: TransferVerificationDao,
    private val disputeDao: DisputeDao,
    private val auditLogDao: AuditLogDao,
) : ViewModel() {

    data class UiState(
        val loading: Boolean = false,
        val transfer: TransferEntity? = null,
        val verifications: List<TransferVerificationEntity> = emptyList(),
        val disputes: List<DisputeEntity> = emptyList(),
        val logs: List<AuditLogEntity> = emptyList(),
        val error: String? = null,
    )

    private val _state = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = _state.asStateFlow()

    fun load(transferId: String) {
        _state.update { it.copy(loading = true, error = null) }
        viewModelScope.launch {
            try {
                val transferFlow = transferDao.getTransferById(transferId)
                val verFlow = verificationDao.observeByTransferId(transferId)
                val dspFlow = disputeDao.observeByTransferId(transferId)
                val logsFlow = auditLogDao.streamByRef(transferId)
                combine(transferFlow, verFlow, dspFlow, logsFlow) { t, v, d, l ->
                    _state.value.copy(loading = false, transfer = t, verifications = v, disputes = d, logs = l)
                }.collect { newState -> _state.value = newState }
            } catch (e: Exception) {
                _state.update { it.copy(loading = false, error = e.message) }
            }
        }
    }
}
