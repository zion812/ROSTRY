package com.rio.rostry.ui.monitoring.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.repository.monitoring.MortalityRepository
import com.rio.rostry.data.database.entity.MortalityRecordEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class MortalityViewModel @Inject constructor(
    private val repo: MortalityRepository
) : ViewModel() {

    data class UiState(
        val records: List<MortalityRecordEntity> = emptyList()
    )

    private val _ui = MutableStateFlow(UiState())
    val ui: StateFlow<UiState> = _ui.asStateFlow()

    init {
        viewModelScope.launch {
            repo.observeAll().collect { list ->
                _ui.update { it.copy(records = list) }
            }
        }
    }

    fun record(
        productId: String?,
        causeCategory: String,
        circumstances: String?,
        ageWeeks: Int?,
        disposalMethod: String?,
        financialImpactInr: Double?
    ) {
        viewModelScope.launch {
            val rec = MortalityRecordEntity(
                deathId = UUID.randomUUID().toString(),
                productId = productId,
                causeCategory = causeCategory,
                circumstances = circumstances,
                ageWeeks = ageWeeks,
                disposalMethod = disposalMethod,
                financialImpactInr = financialImpactInr
            )
            repo.insert(rec)
        }
    }
}
