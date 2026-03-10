package com.rio.rostry.core.model

/**
 * Domain model for a community event.
 */
data class Event(
    val eventId: String,
    val groupId: String?,
    val title: String,
    val description: String?,
    val location: String?,
    val startTime: Long,
    val endTime: Long?
)
