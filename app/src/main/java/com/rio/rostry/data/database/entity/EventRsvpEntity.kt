package com.rio.rostry.data.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "event_rsvps", indices = [Index(value=["eventId","userId"], unique = true)])
data class EventRsvpEntity(
    @PrimaryKey val id: String,
    val eventId: String,
    val userId: String,
    val status: String, // GOING, INTERESTED, NOT_GOING
    val updatedAt: Long,
)
