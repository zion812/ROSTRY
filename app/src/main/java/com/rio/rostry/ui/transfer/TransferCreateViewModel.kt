package com.rio.rostry.ui.transfer

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
import kotlinx.coroutines.launch
import java.util.UUID

@HiltViewModel
class TransferCreateViewModel @Inject constructor(
    private val transferDao: TransferDao,
    private val currentUserProvider: CurrentUserProvider,
) : ViewModel() {

    data class UiState(
        val productId: String = "",
        val toUserId: String = "",
        val amount: String = "",
        val currency: String = "USD",
        val type: String = "PAYMENT",
        val notes: String = "",
        val loading: Boolean = false,
        val error: String? = null,
        val successTransferId: String? = null,
    )

    private val _state = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = _state.asStateFlow()

    fun update(field: String, value: String) {
        _state.value = when (field) {
            "productId" -> _state.value.copy(productId = value)
            "+toUserId" -> _state.value.copy(toUserId = value)
            "toUserId" -> _state.value.copy(toUserId = value)
            "amount" -> _state.value.copy(amount = value)
            "currency" -> _state.value.copy(currency = value)
            "type" -> _state.value.copy(type = value)
            "notes" -> _state.value.copy(notes = value)
            else -> _state.value
        }
    }

    fun create() {
        val s = _state.value
        val amt = s.amount.toDoubleOrNull() ?: 0.0
        viewModelScope.launch {
            try {
                _state.value = s.copy(loading = true, error = null, successTransferId = null)
                val id = UUID.randomUUID().toString()
                val now = System.currentTimeMillis()
                val entity = TransferEntity(
                    transferId = id,
                    productId = s.productId.ifBlank { null },
                    fromUserId = currentUserProvider.userIdOrNull(),
                    toUserId = s.toUserId.ifBlank { null },
                    orderId = null,
                    amount = amt,
                    currency = s.currency.ifBlank { "USD" },
                    type = s.type.ifBlank { "PAYMENT" },
                    status = "PENDING",
                    transactionReference = null,
                    notes = s.notes.ifBlank { null },
                    initiatedAt = now,
                    updatedAt = now,
                    lastModifiedAt = now,
                    dirty = true
                )
                transferDao.upsert(entity)
                _state.value = _state.value.copy(loading = false, successTransferId = id)
            } catch (e: Exception) {
                _state.value = _state.value.copy(loading = false, error = e.message)
            }
        }
    }
}
