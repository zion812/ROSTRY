package com.rio.rostry.data.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "rate_limits", indices = [Index(value=["userId","action"], unique = true)])
data class RateLimitEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val action: String, // post, comment, like, message
    val lastAt: Long,
)
