package com.rio.rostry.data.database.entity

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * FarmTimelineEventEntity represents a public-facing timeline event.
 * These are derived from internal farm logs with privacy filtering applied.
 * 
 * Part of the "Glass Box" system - making farmer actions visible to build trust.
 */
@Keep
@Entity(
    tableName = "farm_timeline_events",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["userId"],
            childColumns = ["farmerId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["farmerId"]),
        Index(value = ["eventType"]),
        Index(value = ["eventDate"]),
        Index(value = ["isPublic"])
    ]
)
data class FarmTimelineEventEntity(
    @PrimaryKey val eventId: String = "",
    val farmerId: String = "",
    
    // Event Content
    val eventType: String = "",             // VACCINATION, SANITATION, MILESTONE, SALE, BATCH_ADDED, WEIGHT_CHECK, BUYER_FEEDBACK
    val title: String = "",                 // "Vaccination Complete"
    val description: String = "",           // "Batch A vaccinated against Newcastle Disease"
    val iconType: String? = null,           // "vaccine", "spray", "trophy", "chick", "scale"
    val imageUrl: String? = null,           // Optional photo
    
    // Source Linkage (for audit, not public)
    val sourceType: String? = null,         // "VACCINATION_RECORD", "FARM_ACTIVITY_LOG", "ORDER"
    val sourceId: String? = null,           // Links to original record
    
    // Trust Impact
    val trustPointsEarned: Int = 0,         // Points added to trust score
    
    // Visibility
    val isPublic: Boolean = true,           // Farmer can hide specific events
    val isMilestone: Boolean = false,       // Special milestone events
    
    // Timing
    val eventDate: Long = System.currentTimeMillis(),  // When the action occurred
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val dirty: Boolean = true,
    val syncedAt: Long? = null
) {
    // No-arg constructor for Firestore
    constructor() : this(eventId = "")
    
    companion object {
        // Event Types
        const val TYPE_VACCINATION = "VACCINATION"
        const val TYPE_SANITATION = "SANITATION"
        const val TYPE_MILESTONE = "MILESTONE"
        const val TYPE_SALE = "SALE"
        const val TYPE_BATCH_ADDED = "BATCH_ADDED"
        const val TYPE_WEIGHT_CHECK = "WEIGHT_CHECK"
        const val TYPE_BUYER_FEEDBACK = "BUYER_FEEDBACK"
        const val TYPE_FEED = "FEED"
        
        // Icon Types
        const val ICON_VACCINE = "vaccine"
        const val ICON_SPRAY = "spray"
        const val ICON_TROPHY = "trophy"
        const val ICON_CHICK = "chick"
        const val ICON_SCALE = "scale"
        const val ICON_SALE = "sale"
        const val ICON_STAR = "star"
        const val ICON_FEED = "feed"
    }
}
