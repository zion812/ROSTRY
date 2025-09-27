package com.rio.rostry.ui.transfer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.rio.rostry.data.database.dao.TransferDao
import com.rio.rostry.data.database.dao.TransferVerificationDao
import com.rio.rostry.data.database.entity.TransferEntity
import com.rio.rostry.data.database.entity.TransferVerificationEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransferDocumentationViewModel @Inject constructor(
    private val transferDao: TransferDao,
    private val verificationDao: TransferVerificationDao,
    private val gson: Gson,
) : ViewModel() {

    data class UiState(
        val loading: Boolean = true,
        val transfer: TransferEntity? = null,
        val verifications: List<TransferVerificationEntity> = emptyList(),
        val certificateJson: String? = null,
        val error: String? = null
    )

    private val _state = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = _state.asStateFlow()

    fun load(transferId: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(loading = true, error = null)
            runCatching {
                val t = transferDao.getTransferById(transferId).first()
                val v = verificationDao.getByTransfer(transferId)
                val cert = buildCertificateJson(t, v)
                _state.value = _state.value.copy(loading = false, transfer = t, verifications = v, certificateJson = cert)
            }.onFailure { e ->
                _state.value = _state.value.copy(loading = false, error = e.message)
            }
        }
    }

    private fun buildCertificateJson(transfer: TransferEntity?, steps: List<TransferVerificationEntity>): String {
        val cert = mapOf(
            "transferId" to (transfer?.transferId ?: ""),
            "fromUserId" to (transfer?.fromUserId ?: ""),
            "toUserId" to (transfer?.toUserId ?: ""),
            "status" to (transfer?.status ?: ""),
            "initiatedAt" to (transfer?.initiatedAt ?: 0L),
            "steps" to steps.map { s ->
                mapOf(
                    "step" to s.step,
                    "status" to s.status,
                    "photoBeforeUrl" to s.photoBeforeUrl,
                    "photoAfterUrl" to s.photoAfterUrl,
                    "gpsLat" to s.gpsLat,
                    "gpsLng" to s.gpsLng,
                    "identityDocType" to s.identityDocType,
                    "identityDocRef" to s.identityDocRef,
                    "notes" to s.notes,
                    "createdAt" to s.createdAt
                )
            }
        )
        return gson.toJson(cert)
    }
}
