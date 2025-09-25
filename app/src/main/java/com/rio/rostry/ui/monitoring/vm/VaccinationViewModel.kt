package com.rio.rostry.ui.monitoring.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.repository.monitoring.VaccinationRepository
import com.rio.rostry.data.database.entity.VaccinationRecordEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class VaccinationViewModel @Inject constructor(
    private val repo: VaccinationRepository
): ViewModel() {

    data class UiState(
        val productId: String = "",
        val records: List<VaccinationRecordEntity> = emptyList()
    )

    private val _ui = MutableStateFlow(UiState())
    val ui: StateFlow<UiState> = _ui.asStateFlow()

    fun observe(productId: String) {
        _ui.update { it.copy(productId = productId) }
        viewModelScope.launch {
            repo.observe(productId).collect { list ->
                _ui.update { it.copy(records = list) }
            }
        }
    }

    fun schedule(
        productId: String,
        vaccineType: String,
        scheduledAt: Long,
        supplier: String? = null,
        batchCode: String? = null,
        doseMl: Double? = null,
        costInr: Double? = null
    ) {
        viewModelScope.launch {
            val rec = VaccinationRecordEntity(
                vaccinationId = UUID.randomUUID().toString(),
                productId = productId,
                vaccineType = vaccineType,
                supplier = supplier,
                batchCode = batchCode,
                doseMl = doseMl,
                scheduledAt = scheduledAt,
                administeredAt = null,
                efficacyNotes = null,
                costInr = costInr
            )
            repo.upsert(rec)
        }
    }
}
