package com.rio.rostry.ui.enthusiast.breeding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.database.entity.HatchingBatchEntity
import com.rio.rostry.data.repository.EnthusiastBreedingRepository
import com.rio.rostry.session.CurrentUserProvider
import com.rio.rostry.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class BreedingCalendarViewModel @Inject constructor(
    private val breedingRepository: EnthusiastBreedingRepository,
    private val currentUserProvider: CurrentUserProvider
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
            // 1. Hatching Due Dates
            breedingRepository.observeHatchingDue(userId, 30).collect { batches ->
                val hatchEvents = batches.map { batch ->
                    BreedingEvent(
                        id = batch.batchId,
                        title = "Hatch Due: ${batch.name}",
                        date = batch.expectedHatchAt ?: 0L,
                        type = EventType.HATCH_DUE,
                        description = "Eggs from ${batch.sourceCollectionId}"
                    )
                }

                // 2. Mating Reminders (Mock logic for now, could be real recurrent schedules)
                val matingEvents = mockMatingEvents()

                // 3. Vaccination (Mock for now)
                val vaxEvents = mockVaccinationEvents()

                val allEvents = (hatchEvents + matingEvents + vaxEvents).sortedBy { it.date }
                
                _state.update { it.copy(
                    isLoading = false,
                    events = allEvents
                ) }
            }
        }
    }

    // Mock data generators for demo purposes
    private fun mockMatingEvents(): List<BreedingEvent> {
        val events = mutableListOf<BreedingEvent>()
        val cal = Calendar.getInstance()
        for (i in 1..3) {
            cal.add(Calendar.DAY_OF_YEAR, 2)
            events.add(BreedingEvent(
                id = "mating_$i",
                title = "Planned Mating",
                date = cal.timeInMillis,
                type = EventType.MATING,
                description = "Pair $i scheduled"
            ))
        }
        return events
    }

    private fun mockVaccinationEvents(): List<BreedingEvent> {
        val events = mutableListOf<BreedingEvent>()
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, 5)
        events.add(BreedingEvent(
            id = "vax_1",
            title = "Mareks Vax",
            date = cal.timeInMillis,
            type = EventType.VACCINATION,
            description = "Batch #102 chicks"
        ))
        return events
    }

    fun selectDate(date: Long) {
        _state.update { it.copy(selectedDate = date) }
    }
}
