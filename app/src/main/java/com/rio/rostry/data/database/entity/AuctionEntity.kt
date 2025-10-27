package com.rio.rostry.data.database.entity

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "auctions",
    foreignKeys = [
        ForeignKey(
            entity = ProductEntity::class,
            parentColumns = ["productId"],
            childColumns = ["productId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("productId")]
)
@Keep
data class AuctionEntity(
    @PrimaryKey val auctionId: String = "",
    val productId: String = "",
    val startsAt: Long = 0L,
    val endsAt: Long = 0L,
    val minPrice: Double = 0.0,
    val currentPrice: Double = 0.0,
    val isActive: Boolean = true,
    val createdAt: Long = 0L,
    val updatedAt: Long = 0L
)
