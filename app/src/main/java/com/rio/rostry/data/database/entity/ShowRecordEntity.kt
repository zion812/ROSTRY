package com.rio.rostry.data.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * ShowRecordEntity - Records competition/exhibition results for Enthusiast birds.
 * 
 * Tracks:
 * - Show/competition participation
 * - Results (wins, placements, awards)
 * - Sparring matches (for fighting breeds)
 * - Exhibition scores
 * 
 * Used for:
 * - Performance tracking and analytics
 * - Showcase card badges
 * - Market value assessment
 */
@Entity(
    tableName = "show_records",
    foreignKeys = [
        ForeignKey(
            entity = ProductEntity::class,
            parentColumns = ["productId"],
            childColumns = ["productId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["productId"]),
        Index(value = ["eventDate"]),
        Index(value = ["recordType"])
    ]
)
data class ShowRecordEntity(
    @PrimaryKey val recordId: String,
    val productId: String,          // Bird this record belongs to
    val ownerId: String,            // Owner at time of event
    
    // Event details
    val recordType: String,         // "SHOW", "EXHIBITION", "SPARRING", "COMPETITION"
    val eventName: String,          // e.g., "State Poultry Show 2024"
    val eventLocation: String? = null,
    val eventDate: Long,            // When the event occurred
    
    // Results
    val result: String,             // "WIN", "LOSS", "DRAW", "1ST", "2ND", "3RD", "PARTICIPATED"
    val placement: Int? = null,     // Numeric placement if applicable
    val totalParticipants: Int? = null, // Total birds in category
    val category: String? = null,   // Competition category
    val score: Double? = null,      // Judge score if applicable
    
    // Additional details
    val opponentName: String? = null,    // For sparring records
    val opponentOwnerName: String? = null,
    val judgesNotes: String? = null,
    val awards: String? = null,     // JSON list of awards won
    val photoUrls: String = "[]",   // JSON array of event photos
    
    // Verification
    val isVerified: Boolean = false,
    val verifiedBy: String? = null,
    val certificateUrl: String? = null,
    
    // Metadata
    val notes: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isDeleted: Boolean = false,
    val deletedAt: Long? = null,
    
    // Sync state
    val dirty: Boolean = false,
    val syncedAt: Long? = null
) {
    companion object {
        // Record types
        const val TYPE_SHOW = "SHOW"
        const val TYPE_EXHIBITION = "EXHIBITION"
        const val TYPE_SPARRING = "SPARRING"
        const val TYPE_COMPETITION = "COMPETITION"
        
        // Results
        const val RESULT_WIN = "WIN"
        const val RESULT_LOSS = "LOSS"
        const val RESULT_DRAW = "DRAW"
        const val RESULT_1ST = "1ST"
        const val RESULT_2ND = "2ND"
        const val RESULT_3RD = "3RD"
        const val RESULT_PARTICIPATED = "PARTICIPATED"
    }
    
    /**
     * Check if this record represents a victory/win.
     */
    val isWin: Boolean
        get() = result in listOf(RESULT_WIN, RESULT_1ST)
    
    /**
     * Check if this record represents a podium finish.
     */
    val isPodium: Boolean
        get() = result in listOf(RESULT_1ST, RESULT_2ND, RESULT_3RD)
}
