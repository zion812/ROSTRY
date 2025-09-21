package com.rio.rostry.data.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "bids",
    foreignKeys = [
        ForeignKey(
            entity = AuctionEntity::class,
            parentColumns = ["auctionId"],
            childColumns = ["auctionId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["userId"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("auctionId"), Index("userId")]
)
data class BidEntity(
    @PrimaryKey val bidId: String,
    val auctionId: String,
    val userId: String,
    val amount: Double,
    val placedAt: Long = System.currentTimeMillis()
)
