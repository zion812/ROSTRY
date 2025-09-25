package com.rio.rostry.ui.monitoring.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.repository.monitoring.QuarantineRepository
import com.rio.rostry.data.database.entity.QuarantineRecordEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class QuarantineViewModel @Inject constructor(
    private val repo: QuarantineRepository
) : ViewModel() {

    data class UiState(
        val productId: String = "",
        val active: List<QuarantineRecordEntity> = emptyList(),
        val history: List<QuarantineRecordEntity> = emptyList()
    )

    private val _ui = MutableStateFlow(UiState())
    val ui: StateFlow<UiState> = _ui.asStateFlow()

    fun observe(productId: String) {
        _ui.update { it.copy(productId = productId) }
        viewModelScope.launch {
            repo.observe(productId).collect { list ->
                _ui.update { it.copy(active = list.filter { r -> r.status == "ACTIVE" }, history = list.filter { r -> r.status != "ACTIVE" }) }
            }
        }
    }

    fun start(productId: String, reason: String, protocol: String?) {
        viewModelScope.launch {
            val rec = QuarantineRecordEntity(
                quarantineId = UUID.randomUUID().toString(),
                productId = productId,
                reason = reason,
                protocol = protocol
            )
            repo.insert(rec)
        }
    }

    fun complete(rec: QuarantineRecordEntity, status: String = "RECOVERED") {
        viewModelScope.launch {
            repo.update(rec.copy(status = status, endedAt = System.currentTimeMillis()))
        }
    }
}
