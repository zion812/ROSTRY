package com.rio.rostry.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.rio.rostry.domain.model.CompetitionStatus

@Entity(tableName = "competitions")
data class CompetitionEntryEntity(
    @PrimaryKey val competitionId: String,
    val title: String,
    val description: String,
    val startTime: Long,
    val endTime: Long,
    val region: String,
    val status: CompetitionStatus, 
    val bannerUrl: String? = null,
    val entryFee: Double? = null,
    val prizePool: String? = null, // JSON/String like "$500 + Badge"
    val participantCount: Int = 0,
    val participantsPreviewJson: String? = null, // Top 3 avatar URLs
    val rulesJson: String? = null,
    val bracketsJson: String? = null,       // Structure of the tournament tree
    val leaderboardJson: String? = null,    // Ranked list of participants
    val galleryUrlsJson: String? = null,    // List of event photos
    val createdAt: Long = System.currentTimeMillis()
)
