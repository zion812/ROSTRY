package com.rio.rostry.core.model

/**
 * Domain model for an event RSVP.
 */
data class EventRsvp(
    val id: String,
    val eventId: String,
    val userId: String,
    val status: String,
    val updatedAt: Long
)
