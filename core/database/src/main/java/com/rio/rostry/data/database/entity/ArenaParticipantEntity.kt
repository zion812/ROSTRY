package com.rio.rostry.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "arena_participants",
    foreignKeys = [
        ForeignKey(
            entity = ProductEntity::class,
            parentColumns = ["productId"],
            childColumns = ["birdId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = CompetitionEntryEntity::class,
            parentColumns = ["competitionId"],
            childColumns = ["competitionId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("birdId"),
        Index("competitionId"),
        Index("ownerId")
    ]
)
data class ArenaParticipantEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val competitionId: String,
    val birdId: String,
    val ownerId: String,
    
    // Snapshot data for display to avoid complex joins for basic lists
    val birdName: String,
    val birdImageUrl: String?,
    val breed: String,
    
    val entryTime: Long = System.currentTimeMillis(),
    val totalVotes: Int = 0,
    val averageScore: Float = 0f,
    val rank: Int = 0
)
