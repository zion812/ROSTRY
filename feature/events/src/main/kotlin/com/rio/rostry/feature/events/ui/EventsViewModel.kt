package com.rio.rostry.ui.events

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

class EventsViewModel : ViewModel() {

    private val _events = MutableStateFlow<List<EventItem>>(emptyList())
    val upcoming: StateFlow<List<EventItem>> =
        _events.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    fun createEvent(title: String, description: String?, location: String?, startTime: Long, endTime: Long?) {
        viewModelScope.launch {
            val newEvent = EventItem(
                eventId = UUID.randomUUID().toString(),
                title = title.ifBlank { "Untitled" },
                location = location
            )
            _events.value = listOf(newEvent) + _events.value
        }
    }

    fun rsvp(eventId: String, userId: String, status: String) {
        // Placeholder until repository/DAO wiring is reintroduced.
    }

    fun rsvpSelf(eventId: String, status: String) {
        // Placeholder until session wiring is reintroduced.
    }
}
