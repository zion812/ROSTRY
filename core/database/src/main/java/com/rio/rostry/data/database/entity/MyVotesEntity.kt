package com.rio.rostry.data.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Stores local votes before they are synced to Firebase.
 * "Zero-Cost" strategy: Allow voting offline, batch sync later.
 */
@Entity(
    tableName = "my_votes",
    indices = [
        Index(value = ["competitionId", "participantId"], unique = true),
        Index(value = ["synced"])
    ]
)
data class MyVotesEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val competitionId: String,
    val participantId: String, // The bird/user being voted for
    val votedAt: Long = System.currentTimeMillis(),
    val points: Int = 1, // Weighted voting possible in future
    val synced: Boolean = false,
    val syncError: String? = null
)
