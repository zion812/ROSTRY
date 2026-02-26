package com.rio.rostry.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity representing a prohibited term or pattern for content moderation.
 */
@Entity(tableName = "moderation_blocklist")
data class ModerationBlocklistEntity(
    @PrimaryKey
    val term: String,
    val type: String = "EXACT", // EXACT, REGEX, PARTIAL
    val createdAt: Long = System.currentTimeMillis()
)
