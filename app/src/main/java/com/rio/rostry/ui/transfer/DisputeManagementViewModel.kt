package com.rio.rostry.ui.transfer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.database.dao.DisputeDao
import com.rio.rostry.data.database.dao.AuditLogDao
import com.rio.rostry.data.database.entity.DisputeEntity
import com.rio.rostry.data.database.entity.AuditLogEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class DisputeManagementViewModel @Inject constructor(
    private val disputeDao: DisputeDao,
    private val auditLogDao: AuditLogDao,
) : ViewModel() {

    data class UiState(
        val loading: Boolean = false,
        val error: String? = null,
        val success: String? = null,
        val disputes: List<DisputeEntity> = emptyList(),
        val creating: Boolean = false
    )

    private val _state = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = _state.asStateFlow()

    fun load(transferId: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(loading = true, error = null)
            runCatching {
                disputeDao.getByTransfer(transferId)
            }.onSuccess { list ->
                _state.value = _state.value.copy(loading = false, disputes = list)
            }.onFailure { e ->
                _state.value = _state.value.copy(loading = false, error = e.message)
            }
        }
    }

    fun openDispute(transferId: String, actorUserId: String, reason: String) {
        viewModelScope.launch {
            if (reason.isBlank()) {
                _state.value = _state.value.copy(error = "Please provide a reason")
                return@launch
            }
            _state.value = _state.value.copy(creating = true, error = null)
            val entity = DisputeEntity(
                disputeId = UUID.randomUUID().toString(),
                transferId = transferId,
                raisedByUserId = actorUserId,
                reason = reason,
                status = "OPEN",
                resolutionNotes = null,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
            disputeDao.upsert(entity)
            auditLogDao.insert(
                AuditLogEntity(
                    logId = UUID.randomUUID().toString(),
                    type = "DISPUTE",
                    refId = transferId,
                    action = "OPEN",
                    actorUserId = actorUserId,
                    detailsJson = null
                )
            )
            _state.value = _state.value.copy(creating = false, success = "Dispute opened")
            load(transferId)
        }
    }

    fun setResolution(disputeId: String, transferId: String, resolution: String, status: String) {
        viewModelScope.launch {
            val current = disputeDao.getById(disputeId) ?: return@launch
            val updated = current.copy(resolutionNotes = resolution.ifBlank { null }, status = status, updatedAt = System.currentTimeMillis())
            disputeDao.upsert(updated)
            auditLogDao.insert(
                AuditLogEntity(
                    logId = UUID.randomUUID().toString(),
                    type = "DISPUTE",
                    refId = transferId,
                    action = "RESOLUTION",
                    actorUserId = null,
                    detailsJson = null
                )
            )
            _state.value = _state.value.copy(success = "Updated")
            load(transferId)
        }
    }

    fun consumeSuccess() { _state.value = _state.value.copy(success = null) }
}
