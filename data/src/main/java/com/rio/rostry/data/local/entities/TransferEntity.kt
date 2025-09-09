package com.rio.rostry.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "transfers",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["fromUserId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["toUserId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("fromUserId"), Index("toUserId")]
)
data class TransferEntity(
    @PrimaryKey val id: String,
    val fromUserId: String,
    val toUserId: String,
    val productId: String?,
    val status: String,
    val createdAt: Long = System.currentTimeMillis()
)
