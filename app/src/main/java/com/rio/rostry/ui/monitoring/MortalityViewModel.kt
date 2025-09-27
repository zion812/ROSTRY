package com.rio.rostry.ui.monitoring

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.rio.rostry.data.database.entity.MortalityRecordEntity
import com.rio.rostry.data.repository.monitoring.MortalityRepository
import com.rio.rostry.data.database.dao.DayCount
import com.rio.rostry.data.database.dao.CategoryCount
import java.util.UUID
import kotlin.math.max

@HiltViewModel
class MortalityViewModel @Inject constructor(
    private val repo: MortalityRepository
) : ViewModel() {

    data class Stats(
        val total: Int = 0,
        val totalCostInr: Double = 0.0,
        val avgAgeWeeks: Double? = null,
    )

    data class UiState(
        val days: Int = 30,
        val stats: Stats = Stats(),
        val trend: List<DayCount> = emptyList(),
        val distribution: List<CategoryCount> = emptyList(),
        val recent: List<MortalityRecordEntity> = emptyList(),
        val isLoading: Boolean = false,
        val error: String? = null,
        val newRecord: NewRecord = NewRecord()
    )

    data class NewRecord(
        val productId: String? = null,
        val causeCategory: String = "ILLNESS",
        val circumstances: String = "",
        val ageWeeks: String = "",
        val disposalMethod: String = "",
        val financialImpactInr: String = "",
        val occurredAt: Long = System.currentTimeMillis()
    )

    private val _ui = MutableStateFlow(UiState())
    val ui: StateFlow<UiState> = _ui.asStateFlow()

    init {
        // Observe recent events
        viewModelScope.launch {
            repo.observeAll().collect { list ->
                _ui.update { it.copy(recent = list) }
            }
        }
        refresh()
    }

    fun setDays(days: Int) {
        _ui.update { it.copy(days = days) }
        refresh()
    }

    fun updateNewRecord(transform: (NewRecord) -> NewRecord) {
        _ui.update { it.copy(newRecord = transform(it.newRecord)) }
    }

    fun recordMortality() {
        val r = _ui.value.newRecord
        val ageWeeksInt = r.ageWeeks.toIntOrNull()
        val cost = r.financialImpactInr.toDoubleOrNull()
        val entity = MortalityRecordEntity(
            deathId = UUID.randomUUID().toString(),
            productId = r.productId,
            causeCategory = r.causeCategory.ifBlank { "OTHER" },
            circumstances = r.circumstances.ifBlank { null },
            ageWeeks = ageWeeksInt,
            disposalMethod = r.disposalMethod.ifBlank { null },
            financialImpactInr = cost,
            occurredAt = max(0L, r.occurredAt)
        )
        viewModelScope.launch {
            repo.insert(entity)
            // reset form (keep category to speed repeated entries)
            _ui.update { it.copy(newRecord = it.newRecord.copy(circumstances = "", ageWeeks = "", disposalMethod = "", financialImpactInr = "")) }
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
                val s = repo.stats(start, end)
                val t = repo.trendByDay(start, end)
                val d = repo.distributionByCause(start, end)
                _ui.update { it.copy(
                    stats = Stats(total = s.total, totalCostInr = s.totalCostInr, avgAgeWeeks = s.avgAgeWeeks),
                    trend = t,
                    distribution = d,
                    isLoading = false
                ) }
            } catch (e: Exception) {
                _ui.update { it.copy(isLoading = false, error = e.message ?: "Unknown error") }
            }
        }
    }
}
