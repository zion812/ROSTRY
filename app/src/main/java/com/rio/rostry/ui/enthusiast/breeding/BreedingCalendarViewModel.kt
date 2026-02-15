package com.rio.rostry.ui.enthusiast.breeding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.database.dao.MatingLogDao
import com.rio.rostry.data.database.dao.VaccinationRecordDao
import com.rio.rostry.data.database.entity.HatchingBatchEntity
import com.rio.rostry.data.repository.EnthusiastBreedingRepository
import com.rio.rostry.session.CurrentUserProvider
import com.rio.rostry.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*
import javax.inject.Inject

@HiltViewModel
class BreedingCalendarViewModel @Inject constructor(
    private val breedingRepository: EnthusiastBreedingRepository,
    private val currentUserProvider: CurrentUserProvider,
    private val matingLogDao: MatingLogDao,
    private val vaccinationRecordDao: VaccinationRecordDao
) : ViewModel() {

    data class BreedingEvent(
        val id: String,
        val title: String,
        val date: Long,
        val type: EventType,
        val description: String
    )

    enum class EventType {
        HATCH_DUE,
        VACCINATION,
        MATING
    }

    data class UiState(
        val events: List<BreedingEvent> = emptyList(),
        val selectedDate: Long = System.currentTimeMillis(),
        val isLoading: Boolean = false
    )

    private val _state = MutableStateFlow(UiState())
    val state = _state.asStateFlow()

    init {
        loadEvents()
    }

    private fun loadEvents() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val userId = currentUserProvider.userIdOrNull() ?: return@launch

            // Gather events from various sources
            // 1. Hatching Due Dates (already uses real data)
            breedingRepository.observeHatchingDue(userId, 30).collect { batches ->
                val hatchEvents = batches.map { batch ->
                    BreedingEvent(
                        id = batch.batchId,
                        title = "Hatch Due: ${batch.name}",
                        date = batch.expectedHatchAt ?: 0L,
                        type = EventType.HATCH_DUE,
                        description = "Eggs from ${batch.sourceCollectionId ?: "unknown"}"
                    )
                }

                // 2. Mating events from MatingLogDao (recent matings for context)
                val matingEvents = loadMatingEvents(userId)

                // 3. Vaccination events from VaccinationRecordDao (upcoming/overdue)
                val vaxEvents = loadVaccinationEvents(userId)

                val allEvents = (hatchEvents + matingEvents + vaxEvents).sortedBy { it.date }
                
                _state.update { it.copy(
                    isLoading = false,
                    events = allEvents
                ) }
            }
        }
    }

    /**
     * Load real mating events from MatingLogDao.
     * Shows recent matings per pair as calendar events.
     */
    private suspend fun loadMatingEvents(userId: String): List<BreedingEvent> {
        return try {
            val recentMatings = matingLogDao.getRecentByFarmer(userId, 10)
            recentMatings.map { log ->
                BreedingEvent(
                    id = log.logId,
                    title = "Mating: ${log.pairId.take(8)}…",
                    date = log.matedAt,
                    type = EventType.MATING,
                    description = log.observedBehavior ?: log.notes ?: "Mating recorded"
                )
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to load mating events")
            emptyList()
        }
    }

    /**
     * Load real vaccination events from VaccinationRecordDao.
     * Shows upcoming scheduled vaccinations that haven't been administered yet.
     */
    private suspend fun loadVaccinationEvents(userId: String): List<BreedingEvent> {
        return try {
            val now = System.currentTimeMillis()
            // Get overdue + upcoming vaccinations
            val overdue = vaccinationRecordDao.getOverdueForFarmer(userId, now)
            val upcoming = vaccinationRecordDao.dueReminders(now + 30L * 24 * 60 * 60 * 1000)
            
            (overdue + upcoming).distinctBy { it.vaccinationId }.map { vax ->
                val isOverdue = vax.scheduledAt < now
                BreedingEvent(
                    id = vax.vaccinationId,
                    title = "${if (isOverdue) "⚠️ OVERDUE: " else ""}${vax.vaccineType}",
                    date = vax.scheduledAt,
                    type = EventType.VACCINATION,
                    description = "For bird: ${vax.productId.take(8)}…"
                )
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to load vaccination events")
            emptyList()
        }
    }

    fun selectDate(date: Long) {
        _state.update { it.copy(selectedDate = date) }
    }
}
