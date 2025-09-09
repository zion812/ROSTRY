package com.rio.rostry.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val title: String,
    val message: String,
    val read: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
