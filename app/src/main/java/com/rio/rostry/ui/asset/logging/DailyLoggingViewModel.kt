package com.rio.rostry.ui.asset.logging

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.database.entity.DailyLogEntity
import com.rio.rostry.domain.monitoring.EnhancedDailyLogService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DailyLoggingViewModel @Inject constructor(
    private val dailyLogService: EnhancedDailyLogService
) : ViewModel() {

    private val _dailyLogs = MutableStateFlow<List<DailyLogEntity>>(emptyList())
    val dailyLogs: StateFlow<List<DailyLogEntity>> = _dailyLogs.asStateFlow()

    fun loadLogsForAsset(assetId: String) {
        viewModelScope.launch {
            _dailyLogs.value = dailyLogService.getLogsForAsset(assetId)
        }
    }

    fun logDailyMetrics(
        assetId: String,
        farmerId: String,
        weightGrams: Double?,
        feedIntakeGrams: Double?,
        waterIntakeMl: Double?,
        healthObservations: String?,
        date: Long
    ) {
        viewModelScope.launch {
            dailyLogService.logDailyMetrics(
                assetId, farmerId, weightGrams, feedIntakeGrams, waterIntakeMl, healthObservations, date
            )
            loadLogsForAsset(assetId)
        }
    }
}
