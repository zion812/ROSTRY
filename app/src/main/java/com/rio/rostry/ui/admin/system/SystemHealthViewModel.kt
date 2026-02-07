package com.rio.rostry.ui.admin.system

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class SystemHealthViewModel @Inject constructor() : ViewModel() {

    data class SystemState(
        val apiStatus: HealthStatus = HealthStatus.OPERATIONAL,
        val dbStatus: HealthStatus = HealthStatus.OPERATIONAL,
        val authStatus: HealthStatus = HealthStatus.OPERATIONAL,
        val notificationStatus: HealthStatus = HealthStatus.DEGRADED, // Simulating a minor issue
        val apiLatency: Long = 45,
        val dbQueriesPerMin: Int = 2300,
        val memoryUsage: Int = 67,
        val cpuUsage: Int = 34,
        val activeConnections: Int = 156,
        val errorRate: Float = 0.02f
    )

    enum class HealthStatus {
        OPERATIONAL, DEGRADED, DOWN, MAINTENANCE
    }

    private val _uiState = MutableStateFlow(SystemState())
    val uiState = _uiState.asStateFlow()

    init {
        startMonitoring()
    }

    private fun startMonitoring() {
        viewModelScope.launch {
            while (true) {
                delay(3000) // Update every 3 seconds
                updateMetrics()
            }
        }
    }

    private fun updateMetrics() {
        _uiState.update { current ->
            current.copy(
                apiLatency = (40..150).random().toLong(),
                dbQueriesPerMin = (2000..3000).random(),
                memoryUsage = (60..80).random(),
                cpuUsage = (20..50).random(),
                activeConnections = (140..180).random(),
                errorRate = (Random.nextFloat() * 0.1f)
            )
        }
    }
}
