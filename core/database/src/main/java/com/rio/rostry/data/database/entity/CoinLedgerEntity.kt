package com.rio.rostry.data.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "coin_ledger",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["userId"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("userId")]
)
data class CoinLedgerEntity(
    @PrimaryKey val entryId: String,
    val userId: String,
    val type: String, // PURCHASE, USE, REFUND
    val coins: Int,
    val amountInInr: Double,
    val refId: String? = null,
    val notes: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)
