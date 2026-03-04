package com.rio.rostry.data.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "coins",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["userId"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = TransferEntity::class, // Optional: link coin transaction to a financial transfer
            parentColumns = ["transferId"],
            childColumns = ["relatedTransferId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index(value = ["userId"]), Index(value = ["relatedTransferId"])]
)
data class CoinEntity(
    @PrimaryKey val coinTransactionId: String, // Unique ID for each coin transaction
    val userId: String, // Foreign key to UserEntity
    val amount: Double, // Number of coins, can be positive (earned) or negative (spent)
    val type: String, // e.g., "EARNED_PURCHASE", "SPENT_REDEEM", "BONUS", "ADJUSTMENT"
    val description: String? = null,
    val relatedTransferId: String? = null, // If coins were bought or are related to a financial transaction
    val relatedOrderId: String? = null, // If coins were earned/spent on a specific order
    val transactionDate: Long = System.currentTimeMillis(),
    // If you need to store total balance, it's often better to calculate it or store it on the UserEntity
    // to avoid denormalization issues. However, for a ledger-style system, this is fine.
)
