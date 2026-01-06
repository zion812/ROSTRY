package com.rio.rostry.ui.farmer.asset

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.database.dao.*
import com.rio.rostry.data.database.entity.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.net.URLDecoder
import javax.inject.Inject

/**
 * Unified timeline event representing any type of activity for a bird/batch.
 */
sealed class TimelineEvent(
    open val id: String,
    open val timestamp: Long,
    open val type: String
) {
    data class Vaccination(
        override val id: String,
        override val timestamp: Long,
        val vaccineType: String,
        val isAdministered: Boolean,
        val notes: String?
    ) : TimelineEvent(id, timestamp, "VACCINATION")

    data class Growth(
        override val id: String,
        override val timestamp: Long,
        val week: Int,
        val weightGrams: Double?,
        val healthStatus: String?
    ) : TimelineEvent(id, timestamp, "GROWTH")

    data class Mortality(
        override val id: String,
        override val timestamp: Long,
        val causeCategory: String,
        val quantity: Int
    ) : TimelineEvent(id, timestamp, "MORTALITY")

    data class Activity(
        override val id: String,
        override val timestamp: Long,
        val activityType: String,
        val description: String?,
        val amountInr: Double?
    ) : TimelineEvent(id, timestamp, "ACTIVITY")

    data class DailyLog(
        override val id: String,
        override val timestamp: Long,
        val feedKg: Double?,
        val weightGrams: Double?,
        val notes: String?
    ) : TimelineEvent(id, timestamp, "DAILY_LOG")
}

@HiltViewModel
class BirdHistoryViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val vaccinationDao: VaccinationRecordDao,
    private val growthDao: GrowthRecordDao,
    private val mortalityDao: MortalityRecordDao,
    private val activityLogDao: FarmActivityLogDao,
    private val dailyLogDao: DailyLogDao,
    private val farmAssetDao: FarmAssetDao
) : ViewModel() {

    data class UiState(
        val isLoading: Boolean = true,
        val asset: FarmAssetEntity? = null,
        val events: List<TimelineEvent> = emptyList(),
        val filteredEvents: List<TimelineEvent> = emptyList(),
        val selectedFilter: String? = null, // null = ALL
        val error: String? = null
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val assetId: String = savedStateHandle.get<String>("assetId")?.let {
        URLDecoder.decode(it, "UTF-8")
    } ?: ""

    val filterOptions = listOf("ALL", "VACCINATION", "GROWTH", "MORTALITY", "ACTIVITY", "DAILY_LOG")

    init {
        loadHistory()
    }

    private fun loadHistory() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                // Load asset info using the Flow
                val asset = farmAssetDao.getAssetById(assetId).first()
                
                // Aggregate events from all sources using productId (assetId maps to productId in records)
                val events = mutableListOf<TimelineEvent>()

                // Load vaccinations
                vaccinationDao.getRecordsByProduct(assetId).forEach { v ->
                    events.add(
                        TimelineEvent.Vaccination(
                            id = v.vaccinationId,
                            timestamp = v.administeredAt ?: v.scheduledAt,
                            vaccineType = v.vaccineType,
                            isAdministered = v.administeredAt != null,
                            notes = v.efficacyNotes
                        )
                    )
                }

                // Load growth records
                growthDao.getAllByProduct(assetId).forEach { g ->
                    events.add(
                        TimelineEvent.Growth(
                            id = g.recordId,
                            timestamp = g.createdAt,
                            week = g.week,
                            weightGrams = g.weightGrams,
                            healthStatus = g.healthStatus
                        )
                    )
                }

                // Load mortality records
                mortalityDao.getByProduct(assetId).forEach { m ->
                    events.add(
                        TimelineEvent.Mortality(
                            id = m.deathId,
                            timestamp = m.occurredAt,
                            causeCategory = m.causeCategory,
                            quantity = m.quantity
                        )
                    )
                }

                // Load activity logs
                activityLogDao.observeForProduct(assetId).first().forEach { a ->
                    events.add(
                        TimelineEvent.Activity(
                            id = a.activityId,
                            timestamp = a.createdAt,
                            activityType = a.activityType,
                            description = a.description,
                            amountInr = a.amountInr
                        )
                    )
                }

                // Load daily logs
                dailyLogDao.observeForProduct(assetId).first().forEach { d ->
                    events.add(
                        TimelineEvent.DailyLog(
                            id = d.logId,
                            timestamp = d.logDate,
                            feedKg = d.feedKg,
                            weightGrams = d.weightGrams,
                            notes = d.notes
                        )
                    )
                }

                // Sort by timestamp descending (newest first)
                val sortedEvents = events.sortedByDescending { it.timestamp }

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    asset = asset,
                    events = sortedEvents,
                    filteredEvents = sortedEvents
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to load history"
                )
            }
        }
    }

    fun setFilter(filter: String?) {
        val newFilter = if (filter == "ALL") null else filter
        val filtered = if (newFilter == null) {
            _uiState.value.events
        } else {
            _uiState.value.events.filter { it.type == newFilter }
        }
        _uiState.value = _uiState.value.copy(
            selectedFilter = newFilter,
            filteredEvents = filtered
        )
    }
}
