package com.rio.rostry.ui.farmer.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.database.entity.EventStatus
import com.rio.rostry.data.database.entity.FarmEventEntity
import com.rio.rostry.data.database.entity.FarmEventType
import com.rio.rostry.data.database.entity.RecurrenceType
import com.rio.rostry.data.repository.CalendarEvent
import com.rio.rostry.data.repository.FarmEventRepository
import com.rio.rostry.session.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf

@HiltViewModel
class FarmCalendarViewModel @Inject constructor(
    private val farmEventRepository: FarmEventRepository,
    private val sessionManager: SessionManager,
    private val firebaseAuth: com.google.firebase.auth.FirebaseAuth,
    private val growthPredictionService: com.rio.rostry.domain.service.GrowthPredictionService,
    private val projectedEventMapper: ProjectedEventMapper,
    private val farmAssetDao: com.rio.rostry.data.database.dao.FarmAssetDao,
    private val growthRecordDao: com.rio.rostry.data.database.dao.GrowthRecordDao
) : ViewModel() {

    private val _selectedDate = MutableStateFlow(System.currentTimeMillis())
    val selectedDate: StateFlow<Long> = _selectedDate.asStateFlow()

    private val _selectedEventType = MutableStateFlow<FarmEventType?>(null)
    val selectedEventType: StateFlow<FarmEventType?> = _selectedEventType.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val currentUserId = kotlinx.coroutines.flow.flow {
        val user = firebaseAuth.currentUser
        if (user != null) emit(user.uid)
        // Also listen for auth changes
        val channel = kotlinx.coroutines.channels.Channel<String?>()
        val listener = com.google.firebase.auth.FirebaseAuth.AuthStateListener { auth ->
            channel.trySend(auth.currentUser?.uid)
        }
        firebaseAuth.addAuthStateListener(listener)
        try {
            for (uid in channel) {
                if (uid != null) emit(uid)
            }
        } finally {
            firebaseAuth.removeAuthStateListener(listener)
        }
    }

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val events: StateFlow<List<CalendarEvent>> = currentUserId
        .flatMapLatest { userId ->
             if (userId.isBlank()) {
                 flowOf(emptyList())
             } else {
                 // Combine actual database events with projected events
                 combine(
                     farmEventRepository.getCalendarEvents(userId),
                     farmAssetDao.getAssetsByFarmer(userId),
                     growthRecordDao.observeAllByFarmer(userId)
                 ) { dbEvents, assets, allGrowthRecords ->
                     
                     val projectedEvents = assets.flatMap { asset ->
                         val assetRecords = allGrowthRecords.filter { it.productId == asset.assetId }
                             .sortedBy { it.createdAt }
                             .map { (it.weightGrams ?: 0).toInt() }
                         
                         // Only predict if enough data
                         if (assetRecords.size >= 2) {
                             val prediction = growthPredictionService.predictGrowthTrajectory(
                                 weights = assetRecords,
                                 breed = asset.breed ?: "Broiler" // Default
                             )
                             
                             projectedEventMapper.mapPredictionToEvents(
                                 batchId = asset.assetId,
                                 batchName = asset.name,
                                 prediction = prediction
                             )
                         } else {
                             emptyList()
                         }
                     }
                     
                     dbEvents + projectedEvents
                 }
             }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val filteredEvents: StateFlow<List<CalendarEvent>> = combine(events, _selectedDate, _selectedEventType) { allEvents, date, type ->
        allEvents.filter { event ->
            // Filter by date (check if same day)
            val eventDate = Instant.ofEpochMilli(event.date).atZone(ZoneId.systemDefault()).toLocalDate()
            val selected = Instant.ofEpochMilli(date).atZone(ZoneId.systemDefault()).toLocalDate()
            val sameDay = eventDate == selected
            
            // Filter by type
            val typeMatch = type == null || event.type == type
            
            sameDay && typeMatch
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )
    
    // For Month View indicators, we might want a map of Date -> List<EventType> or hasEvent boolean
    // But for now, the UI can compute it from 'events' state which holds ALL events.

    fun selectDate(date: Long) {
        _selectedDate.value = date
    }

    fun filterByType(type: FarmEventType?) {
        _selectedEventType.value = type
    }
    
    fun createEvent(
        title: String,
        description: String,
        scheduledAt: Long,
        type: FarmEventType,
        recurrence: RecurrenceType,
        reminderMinutes: Long
    ) {
        viewModelScope.launch {
            val userId = firebaseAuth.currentUser?.uid ?: return@launch
            val newEvent = FarmEventEntity(
                farmerId = userId,
                title = title,
                description = description,
                scheduledAt = scheduledAt,
                eventType = type,
                recurrence = recurrence,
                reminderBefore = reminderMinutes
            )
            farmEventRepository.createEvent(newEvent)
        }
    }

    fun markEventComplete(event: CalendarEvent) {
        viewModelScope.launch {
            if (event.originalEntity != null) {
                val updated = event.originalEntity.copy(
                    status = EventStatus.COMPLETED,
                    completedAt = System.currentTimeMillis()
                )
                farmEventRepository.updateEvent(updated)
            }
            // If it's a TASK, we would hypothetically call taskRepository.completeTask()
            // For now only FarmEventEntity is editable here directly
        }
    }

    fun deleteEvent(eventId: String) {
         viewModelScope.launch {
             farmEventRepository.deleteEvent(eventId)
         }
    }

    fun confirmProjectedEvent(event: CalendarEvent) {
        viewModelScope.launch {
            val userId = firebaseAuth.currentUser?.uid ?: return@launch
            
            val newEvent = com.rio.rostry.data.database.entity.FarmEventEntity(
                farmerId = userId,
                eventType = event.type,
                title = event.title,
                description = event.description,
                scheduledAt = event.date,
                batchId = event.metadata?.get("batchId"),
                metadata = event.metadata?.toString()
            )
            
            farmEventRepository.createEvent(newEvent)
        }
    }
}
