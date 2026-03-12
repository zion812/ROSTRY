package com.rio.rostry.domain.monitoring.repository

import com.rio.rostry.core.common.Result
import kotlinx.coroutines.flow.Flow

/**
 * Domain interface for farm event calendar management.
 *
 * Manages farm calendar events including creation, updates, deletion,
 * and querying of scheduled events and reminders.
 */
interface FarmEventRepository {

    /** Get calendar events for a date range. */
    fun getCalendarEvents(userId: String, startDate: Long, endDate: Long): Flow<List<Map<String, Any>>>

    /** Create a new calendar event. */
    suspend fun createEvent(userId: String, title: String, description: String?, startDate: Long, endDate: Long?, type: String): Result<String>

    /** Update an existing event. */
    suspend fun updateEvent(eventId: String, title: String?, description: String?, startDate: Long?, endDate: Long?): Result<Unit>

    /** Delete an event. */
    suspend fun deleteEvent(eventId: String): Result<Unit>

    /** Get upcoming reminders for a user. */
    suspend fun getUpcomingReminders(userId: String, limit: Int = 10): Result<List<Map<String, Any>>>
}
