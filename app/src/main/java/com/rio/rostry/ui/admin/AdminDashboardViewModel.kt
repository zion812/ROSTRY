package com.rio.rostry.ui.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.repository.AdminProductRepository
import com.rio.rostry.data.repository.UserRepository
import com.rio.rostry.domain.model.UserType
import com.rio.rostry.domain.model.VerificationStatus
import com.rio.rostry.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the Admin Dashboard.
 * 
 * Manages dashboard state including:
 * - Real-time metrics and statistics
 * - Pending actions counts
 * - System health status
 * - Recent activity timeline
 */
@HiltViewModel
class AdminDashboardViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val adminProductRepository: AdminProductRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminDashboardUiState())
    val uiState: StateFlow<AdminDashboardUiState> = _uiState.asStateFlow()

    init {
        loadDashboardData()
    }

    fun loadDashboardData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            try {
                // Load metrics 
                loadUserMetrics()
                loadProductMetrics()
                loadPendingActions()
                
                _uiState.update { it.copy(isLoading = false) }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to load dashboard data"
                    )
                }
            }
        }
    }

    private suspend fun loadUserMetrics() {
        when (val result = userRepository.getSystemUsers(100)) {
            is Resource.Success -> {
                val users = result.data ?: emptyList()
                val totalUsers = users.size
                val farmers = users.count { it.userType == UserType.FARMER.name }
                val enthusiasts = users.count { it.userType == UserType.ENTHUSIAST.name }
                val pendingVerifications = users.count { 
                    it.verificationStatus == VerificationStatus.PENDING
                }
                
                _uiState.update { state ->
                    state.copy(
                        userMetrics = UserMetrics(
                            totalUsers = totalUsers,
                            farmers = farmers,
                            enthusiasts = enthusiasts,
                            pendingVerifications = pendingVerifications,
                            activeToday = 0 // Would need lastLoginAt tracking
                        )
                    )
                }
            }
            is Resource.Error -> {
                _uiState.update { it.copy(error = result.message) }
            }
            is Resource.Loading -> { /* Ignore */ }
        }
    }

    private suspend fun loadProductMetrics() {
        adminProductRepository.getAllProductsAdmin().first { resource ->
            when (resource) {
                is Resource.Success -> {
                    val products = resource.data ?: emptyList()
                    
                    _uiState.update { state ->
                        state.copy(
                            productMetrics = ProductMetrics(
                                totalProducts = products.size,
                                activeListings = products.count { it.status == "active" },
                                flaggedProducts = products.count { it.adminFlagged == true },
                                pendingReview = products.count { it.status == "pending" }
                            )
                        )
                    }
                    true
                }
                is Resource.Error -> true
                is Resource.Loading -> false
            }
        }
    }

    private suspend fun loadPendingActions() {
        // Get pending verifications count
        var pendingVerifications = 0
        
        when (val result = userRepository.getSystemUsers(100)) {
            is Resource.Success -> {
                pendingVerifications = result.data?.count { 
                    it.verificationStatus == VerificationStatus.PENDING
                } ?: 0
            }
            else -> {}
        }
        
        _uiState.update { state ->
            state.copy(
                pendingActions = PendingActions(
                    verifications = pendingVerifications,
                    disputes = 0, // TODO: Implement when DisputeRepository is available
                    reports = 0,
                    upgradeRequests = 0 // TODO: Implement
                )
            )
        }
    }

    fun refreshDashboard() {
        loadDashboardData()
    }
}

/**
 * UI State for Admin Dashboard.
 */
data class AdminDashboardUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val userMetrics: UserMetrics = UserMetrics(),
    val productMetrics: ProductMetrics = ProductMetrics(),
    val orderMetrics: OrderMetrics = OrderMetrics(),
    val pendingActions: PendingActions = PendingActions(),
    val recentActivity: List<ActivityItem> = emptyList(),
    val systemHealth: SystemHealth = SystemHealth()
)

data class UserMetrics(
    val totalUsers: Int = 0,
    val farmers: Int = 0,
    val enthusiasts: Int = 0,
    val pendingVerifications: Int = 0,
    val activeToday: Int = 0,
    val newThisWeek: Int = 0
)

data class ProductMetrics(
    val totalProducts: Int = 0,
    val activeListings: Int = 0,
    val flaggedProducts: Int = 0,
    val pendingReview: Int = 0,
    val soldThisMonth: Int = 0
)

data class OrderMetrics(
    val totalOrders: Int = 0,
    val pendingOrders: Int = 0,
    val completedThisMonth: Int = 0,
    val totalRevenue: Double = 0.0,
    val disputeRate: Float = 0f
)

data class PendingActions(
    val verifications: Int = 0,
    val disputes: Int = 0,
    val reports: Int = 0,
    val upgradeRequests: Int = 0
) {
    val total: Int get() = verifications + disputes + reports + upgradeRequests
}

data class ActivityItem(
    val id: String,
    val type: String,
    val description: String,
    val timestamp: Long,
    val userId: String
)

data class SystemHealth(
    val databaseStatus: HealthStatus = HealthStatus.HEALTHY,
    val firebaseStatus: HealthStatus = HealthStatus.HEALTHY,
    val syncStatus: HealthStatus = HealthStatus.HEALTHY,
    val lastSyncTime: Long = System.currentTimeMillis()
)

enum class HealthStatus {
    HEALTHY, DEGRADED, ERROR, UNKNOWN
}
