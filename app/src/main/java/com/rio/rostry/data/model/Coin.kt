package com.rio.rostry.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "coins")
data class Coin(
    @PrimaryKey
    val id: String,
    val userId: String,
    val balance: Double,
    val totalEarned: Double,
    val totalSpent: Double,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
)