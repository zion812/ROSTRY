package com.rio.rostry.ui.farmer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.repository.TraceabilityRepository
import com.rio.rostry.data.repository.UserRepository
import com.rio.rostry.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ComplianceUiState(
    val isLoading: Boolean = false,
    val alerts: List<ComplianceAlertItem> = emptyList(),
    val error: String? = null
)

data class ComplianceAlertItem(
    val productId: String,
    val productName: String,
    val issues: List<String>,
    val healthScore: Int
)

@HiltViewModel
class ComplianceViewModel @Inject constructor(
    private val traceabilityRepository: TraceabilityRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ComplianceUiState())
    val uiState: StateFlow<ComplianceUiState> = _uiState.asStateFlow()

    init {
        loadComplianceData()
    }

    fun loadComplianceData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            userRepository.getCurrentUser().collectLatest { userRes ->
                if (userRes is Resource.Success) {
                    val user = userRes.data
                    if (user != null) {
                        val userId = user.userId
                        
                        // Fetch alerts
                        when (val alertsRes = traceabilityRepository.getComplianceAlerts(userId)) {
                            is Resource.Success -> {
                                val alertPairs = alertsRes.data ?: emptyList()
                                if (alertPairs.isEmpty()) {
                                    _uiState.value = ComplianceUiState(isLoading = false, alerts = emptyList())
                                    return@collectLatest
                                }

                                val productIds = alertPairs.map { it.first }
                                
                                // Fetch metadata for names
                                when (val metaRes = traceabilityRepository.getNodeMetadataBatch(productIds)) {
                                    is Resource.Success -> {
                                        val metaMap = metaRes.data ?: emptyMap()
                                        
                                        val items = alertPairs.map { (id, reasons) ->
                                            val meta = metaMap[id]
                                            ComplianceAlertItem(
                                                productId = id,
                                                productName = meta?.name ?: "Product #$id",
                                                issues = reasons,
                                                healthScore = meta?.healthScore ?: 0
                                            )
                                        }
                                        
                                        _uiState.value = ComplianceUiState(isLoading = false, alerts = items)
                                    }
                                    is Resource.Error -> {
                                        _uiState.value = ComplianceUiState(isLoading = false, error = metaRes.message)
                                    }
                                    else -> {}
                                }
                            }
                            is Resource.Error -> {
                                _uiState.value = ComplianceUiState(isLoading = false, error = alertsRes.message)
                            }
                            else -> {}
                        }
                    } else {
                         // User null but success - usually means not logged in
                         _uiState.value = ComplianceUiState(isLoading = false, error = "User not logged in")
                    }
                } else if (userRes is Resource.Error) {
                    _uiState.value = ComplianceUiState(isLoading = false, error = userRes.message)
                }
            }
        }
    }
}
