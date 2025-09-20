package com.rio.rostry.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "notifications")
data class Notification(
    @PrimaryKey
    val id: String,
    val userId: String,
    val title: String,
    val message: String,
    val type: String, // order, payment, system
    val isRead: Boolean = false,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
)