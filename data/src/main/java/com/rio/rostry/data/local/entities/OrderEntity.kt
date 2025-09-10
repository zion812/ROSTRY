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
        ),
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["sellerId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ProductEntity::class,
            parentColumns = ["id"],
            childColumns = ["productId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("buyerId"),
        Index("sellerId"),
        Index("productId"),
        Index(value = ["buyerId", "sellerId", "createdAt"], unique = false)
    ]
)
data class OrderEntity(
    @PrimaryKey val id: String,
    val buyerId: String,
    val sellerId: String,
    val productId: String,
    val quantity: Int,
    val status: String,
    val createdAt: Long = System.currentTimeMillis()
)
