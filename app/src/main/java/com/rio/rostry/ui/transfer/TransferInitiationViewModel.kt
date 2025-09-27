package com.rio.rostry.ui.transfer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.repository.TransferRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class TransferInitiationViewModel @Inject constructor(
    private val transferRepository: TransferRepository,
) : ViewModel() {

    data class UiState(
        val creating: Boolean = false,
        val error: String? = null,
        val createdTransferId: String? = null
    )

    private val _ui = MutableStateFlow(UiState())
    val ui: StateFlow<UiState> = _ui.asStateFlow()

    fun initiate(
        fromUserId: String,
        toUserId: String,
        productId: String?,
        amount: Double,
        currency: String,
        terms: String?,
        gpsLat: Double?,
        gpsLng: Double?
    ) {
        viewModelScope.launch {
            _ui.value = UiState(creating = true)
            runCatching {
                val id = UUID.randomUUID().toString()
                val entity = transferRepository.initiate(
                    transferId = id,
                    fromUserId = fromUserId,
                    toUserId = toUserId,
                    productId = productId,
                    amount = amount,
                    currency = currency,
                    terms = terms,
                    gpsLat = gpsLat,
                    gpsLng = gpsLng
                )
                _ui.value = UiState(creating = false, createdTransferId = entity.transferId)
            }.onFailure { e ->
                _ui.value = UiState(creating = false, error = e.message)
            }
        }
    }
}
