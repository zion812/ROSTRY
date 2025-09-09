package com.rio.rostry.data.local.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import java.util.UUID

@Entity(tableName = "outbox")
@TypeConverters(OutboxTypeConverters::class)
data class OutboxEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val type: String,
    val payloadJson: String,
    val createdAt: Long = System.currentTimeMillis(),
    val attempt: Int = 0
)
