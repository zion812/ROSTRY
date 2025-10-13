package com.rio.rostry.ui.events

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.database.dao.EventRsvpsDao
import com.rio.rostry.data.database.dao.EventsDao
import com.rio.rostry.data.database.entity.EventEntity
import com.rio.rostry.data.database.entity.EventRsvpEntity
import com.rio.rostry.session.CurrentUserProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class EventsViewModel @Inject constructor(
    private val eventsDao: EventsDao,
    private val rsvpsDao: EventRsvpsDao,
    private val currentUserProvider: CurrentUserProvider,
) : ViewModel() {

    val upcoming: StateFlow<List<EventEntity>> =
        eventsDao.streamUpcoming(System.currentTimeMillis()).stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    fun createEvent(title: String, description: String?, location: String?, startTime: Long, endTime: Long?) {
        viewModelScope.launch {
            val e = EventEntity(
                eventId = UUID.randomUUID().toString(),
                groupId = null,
                title = title,
                description = description,
                location = location,
                startTime = startTime,
                endTime = endTime,
            )
            eventsDao.upsert(e)
        }
    }

    fun rsvp(eventId: String, userId: String, status: String) {
        viewModelScope.launch {
            val r = EventRsvpEntity(
                id = UUID.randomUUID().toString(),
                eventId = eventId,
                userId = userId,
                status = status,
                updatedAt = System.currentTimeMillis()
            )
            rsvpsDao.upsert(r)
        }
    }

    fun rsvpSelf(eventId: String, status: String) {
        val uid = currentUserProvider.userIdOrNull() ?: return
        rsvp(eventId, uid, status)
    }
}
