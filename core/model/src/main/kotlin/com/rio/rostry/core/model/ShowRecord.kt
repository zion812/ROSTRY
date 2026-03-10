package com.rio.rostry.core.model

/**
 * Domain model for show/competition records.
 * 
 * Phase 2: Domain and Data Decoupling
 * Requirement 4.1 - Domain models are framework-independent
 */
data class ShowRecord(
    val id: String,
    val productId: String,
    val ownerId: String,
    
    // Event details
    val recordType: String, // "SHOW", "EXHIBITION", "SPARRING", "COMPETITION"
    val eventName: String,
    val eventLocation: String? = null,
    val eventDate: Long,
    
    // Results
    val result: String, // "WIN", "LOSS", "DRAW", "1ST", "2ND", "3RD", "PARTICIPATED"
    val placement: Int? = null,
    val totalParticipants: Int? = null,
    val category: String? = null,
    val score: Double? = null,
    
    // Additional details
    val opponentName: String? = null,
    val opponentOwnerName: String? = null,
    val judgesNotes: String? = null,
    val awards: String? = null,
    val photoUrls: String = "[]",
    
    // Verification
    val isVerified: Boolean = false,
    val verifiedBy: String? = null,
    val certificateUrl: String? = null,
    
    // Metadata
    val notes: String? = null,
    val createdAt: Long,
    val updatedAt: Long,
    val isDeleted: Boolean = false,
    val deletedAt: Long? = null,
    
    // Sync state
    val dirty: Boolean = false,
    val syncedAt: Long? = null
) {
    val isWin: Boolean
        get() = result in listOf("WIN", "1ST")
}

/**
 * Statistics for show records.
 */
data class ShowRecordStats(
    val recordType: String,
    val total: Int,
    val wins: Int,
    val podiums: Int
)
