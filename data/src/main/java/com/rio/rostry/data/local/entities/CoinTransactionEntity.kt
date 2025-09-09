package com.rio.rostry.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "coin_transactions")
data class CoinTransactionEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val coins: Int,
    val type: String, // EARN/SPEND
    val referenceId: String?,
    val createdAt: Long = System.currentTimeMillis()
)
