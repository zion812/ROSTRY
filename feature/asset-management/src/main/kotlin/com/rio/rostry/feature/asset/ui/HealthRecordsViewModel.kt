package com.rio.rostry.ui.asset.health

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.database.entity.AssetHealthRecordEntity
import com.rio.rostry.domain.health.AssetHealthManager
import com.rio.rostry.domain.health.VaccinationProtocolEngine
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HealthRecordsViewModel @Inject constructor(
    private val healthManager: AssetHealthManager,
    private val vaccinationEngine: VaccinationProtocolEngine
) : ViewModel() {

    private val _healthRecords = MutableStateFlow<List<AssetHealthRecordEntity>>(emptyList())
    val healthRecords: StateFlow<List<AssetHealthRecordEntity>> = _healthRecords.asStateFlow()

    fun loadHealthRecords(assetId: String) {
        viewModelScope.launch {
            _healthRecords.value = healthManager.getHealthHistory(assetId)
        }
    }

    fun recordHealthEvent(
        assetId: String,
        farmerId: String,
        recordType: String, // e.g., VACCINE, TREATMENT
        recordData: String,
        healthScore: Int,
        notes: String?
    ) {
        viewModelScope.launch {
            healthManager.recordHealthEvent(
                assetId = assetId,
                farmerId = farmerId,
                recordType = recordType,
                recordData = recordData,
                healthScore = healthScore,
                veterinarianNotes = notes
            )
            loadHealthRecords(assetId)
        }
    }

    fun generateVaccinationProtocol(assetId: String, farmerId: String, hatchDate: Long) {
        viewModelScope.launch {
            vaccinationEngine.generateProtocolForAsset(assetId, farmerId, hatchDate)
        }
    }
}
