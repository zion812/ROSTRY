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
import com.rio.rostry.data.repository.monitoring.VaccinationStats
import com.rio.rostry.data.database.dao.CategoryCount

@HiltViewModel
class VaccinationViewModel @Inject constructor(
    private val repo: VaccinationRepository
): ViewModel() {

    data class UiState(
        val productId: String = "",
        val records: List<VaccinationRecordEntity> = emptyList(),
        val days: Int = 30,
        val stats: VaccinationStats? = null,
        val distribution: List<CategoryCount> = emptyList(),
        val due: List<VaccinationRecordEntity> = emptyList(),
        val isLoading: Boolean = false,
        val error: String? = null
    )

    private val _ui = MutableStateFlow(UiState())
    val ui: StateFlow<UiState> = _ui.asStateFlow()

    init {
        refresh()
    }

    fun observe(productId: String) {
        _ui.update { it.copy(productId = productId) }
        viewModelScope.launch {
            repo.observe(productId).collect { list ->
                _ui.update { it.copy(records = list) }
            }
        }
    }

    fun setDays(days: Int) {
        _ui.update { it.copy(days = days) }
        refresh()
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
            refresh()
        }
    }

    fun refresh() {
        val now = System.currentTimeMillis()
        val start = now - _ui.value.days * 24L * 60 * 60 * 1000
        val end = now
        viewModelScope.launch {
            _ui.update { it.copy(isLoading = true, error = null) }
            try {
                val s = repo.stats(start, end, now)
                val d = repo.distributionByVaccineAdministered(start, end)
                val dueList = repo.dueReminders(now)
                _ui.update { it.copy(stats = s, distribution = d, due = dueList, isLoading = false) }
            } catch (e: Exception) {
                _ui.update { it.copy(isLoading = false, error = e.message ?: "Unknown error") }
            }
        }
    }

    fun markAdministered(vaccinationId: String) {
        val candidate = (_ui.value.due + _ui.value.records).firstOrNull { it.vaccinationId == vaccinationId }
        candidate?.let { rec ->
            viewModelScope.launch {
                val updated = rec.copy(administeredAt = System.currentTimeMillis())
                repo.upsert(updated)
                refresh()
            }
        }
    }

    fun recordAdministeredNow(
        productId: String,
        vaccineType: String,
        doseMl: Double? = null,
        costInr: Double? = null,
        supplier: String? = null,
        batchCode: String? = null
    ) {
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            val rec = VaccinationRecordEntity(
                vaccinationId = UUID.randomUUID().toString(),
                productId = productId,
                vaccineType = vaccineType,
                supplier = supplier,
                batchCode = batchCode,
                doseMl = doseMl,
                scheduledAt = now,
                administeredAt = now,
                efficacyNotes = null,
                costInr = costInr
            )
            repo.upsert(rec)
            refresh()
        }
    }
}
