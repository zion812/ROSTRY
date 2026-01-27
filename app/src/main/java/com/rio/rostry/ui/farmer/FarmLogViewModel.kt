package com.rio.rostry.ui.farmer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.database.entity.FarmActivityLogEntity
import com.rio.rostry.data.repository.FarmActivityLogRepository
import com.rio.rostry.data.repository.ProductRepository
import com.rio.rostry.session.CurrentUserProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

/**
 * ViewModel for the Farm Log screen.
 * Provides access to all farm activity logs with filtering capabilities.
 */
@HiltViewModel
class FarmLogViewModel @Inject constructor(
    private val repository: FarmActivityLogRepository,
    private val productRepository: ProductRepository,
    private val currentUserProvider: CurrentUserProvider
) : ViewModel() {

    data class DailySummary(
        val feedKg: Double = 0.0,
        val expenseInr: Double = 0.0,
        val mortalityCount: Int = 0,
        val costBreakdown: Map<String, Double> = emptyMap() // Breakdown by activity type (FEED, MEDICATION, etc.)
    )

    data class UiState(
        val logs: List<FarmActivityLogEntity> = emptyList(),
        val filteredLogs: List<FarmActivityLogEntity> = emptyList(),
        val groupedLogs: Map<String, List<FarmActivityLogEntity>> = emptyMap(),
        val dailySummaries: Map<String, DailySummary> = emptyMap(),
        val selectedType: String? = null, // null = all types
        val isLoading: Boolean = true,
        val totalExpenses: Double = 0.0,
        val activeProducts: List<com.rio.rostry.data.database.entity.ProductEntity> = emptyList()
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _selectedType = MutableStateFlow<String?>(null)

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            val userId = currentUserProvider.userIdOrNull() ?: return@launch
            
            // Collect logs and products concurrently
            combine(
                repository.observeForFarmer(userId),
                productRepository.getProductsBySeller(userId),
                _selectedType
            ) { logs, productsResult, type ->
                val filtered = if (type == null) logs else logs.filter { it.activityType == type }
                val totalExpenses = logs
                    .filter { it.activityType == "EXPENSE" }
                    .sumOf { it.amountInr ?: 0.0 }
                
                // Grouping Logic
                val grouped = filtered.groupBy { log ->
                    val date = java.time.Instant.ofEpochMilli(log.createdAt)
                        .atZone(java.time.ZoneId.systemDefault())
                        .toLocalDate()
                    val today = java.time.LocalDate.now()
                    when (date) {
                        today -> "Today"
                        today.minusDays(1) -> "Yesterday"
                        else -> java.time.format.DateTimeFormatter.ofPattern("dd MMM yyyy").format(date)
                    }
                }
                
                // Daily Summaries Logic
                val summaries = grouped.mapValues { (_, groupLogs) ->
                    val breakdown = groupLogs
                        .filter { it.amountInr != null && it.amountInr > 0 }
                        .groupBy { it.activityType }
                        .mapValues { (_, logs) -> logs.sumOf { it.amountInr!! } }

                    DailySummary(
                        feedKg = groupLogs.filter { it.activityType == "FEED" }.sumOf { it.quantity ?: 0.0 },
                        expenseInr = groupLogs.filter { it.activityType == "EXPENSE" || it.amountInr != null }.sumOf { it.amountInr ?: 0.0 },
                        mortalityCount = groupLogs.filter { it.activityType == "MORTALITY" }.sumOf { (it.quantity ?: 0.0).toInt() },
                        costBreakdown = breakdown
                    )
                }
                
                val products = productsResult.data ?: emptyList()
                
                UiState(
                    logs = logs,
                    filteredLogs = filtered,
                    groupedLogs = grouped,
                    dailySummaries = summaries,
                    selectedType = type,
                    isLoading = false,
                    totalExpenses = totalExpenses,
                    activeProducts = products
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun setFilter(type: String?) {
        _selectedType.value = type
    }

    fun submitQuickLog(
        productIds: Set<String>,
        type: QuickLogType,
        value: Double,
        notes: String?
    ) {
        viewModelScope.launch {
            val userId = currentUserProvider.userIdOrNull() ?: return@launch
            val now = System.currentTimeMillis()
            
            if (productIds.isEmpty()) {
                // General Farm Log
                val log = FarmActivityLogEntity(
                    activityId = UUID.randomUUID().toString(),
                    farmerId = userId,
                    productId = null,
                    activityType = type.name,
                    description = notes ?: "Quick Log: ${type.label}",
                    quantity = if (type == QuickLogType.MORTALITY || type == QuickLogType.FEED || type == QuickLogType.VACCINATION) value else null,
                    amountInr = if (type == QuickLogType.EXPENSE || type == QuickLogType.MAINTENANCE) value else null,
                    createdAt = now,
                    updatedAt = now,
                    dirty = true
                )
                repository.upsert(log)
            } else {
                // Product-specific logs
                productIds.forEach { productId ->
                    val log = FarmActivityLogEntity(
                        activityId = UUID.randomUUID().toString(),
                        farmerId = userId,
                        productId = productId,
                        activityType = type.name, // Matches QuickLogType enum name
                        description = notes ?: "Quick Log: ${type.label}",
                        quantity = if (type == QuickLogType.MORTALITY || type == QuickLogType.FEED || type == QuickLogType.VACCINATION) value else null,
                        amountInr = if (type == QuickLogType.EXPENSE || type == QuickLogType.MAINTENANCE) value else null,
                        createdAt = now,
                        updatedAt = now,
                        dirty = true
                    )
                    repository.upsert(log)
                }
            }
        }
    }

    fun getActivityTypes(): List<String> = listOf(
        "MORTALITY", "FEED", "EXPENSE", "WEIGHT", 
        "VACCINATION", "DEWORMING", "MEDICATION", 
        "SANITATION", "MAINTENANCE", "OTHER"
    )

    companion object {
        fun formatDate(timestamp: Long): String {
            val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
            return sdf.format(Date(timestamp))
        }
    }
}
