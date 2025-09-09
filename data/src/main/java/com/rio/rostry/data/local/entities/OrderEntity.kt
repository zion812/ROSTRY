package com.rio.rostry.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "orders",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["buyerId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("buyerId")]
)
data class OrderEntity(
    @PrimaryKey val id: String,
    val buyerId: String,
    val totalAmount: Long,
    val currency: String = "INR",
    val status: String,
    val createdAt: Long = System.currentTimeMillis()
)
