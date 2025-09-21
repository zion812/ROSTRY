package com.rio.rostry.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sync_state")
data class SyncStateEntity(
    @PrimaryKey val id: String = "global",
    val lastSyncAt: Long = 0L,
    val lastUserSyncAt: Long = 0L,
    val lastProductSyncAt: Long = 0L,
    val lastOrderSyncAt: Long = 0L,
    val lastTrackingSyncAt: Long = 0L,
    val lastTransferSyncAt: Long = 0L,
    val lastChatSyncAt: Long = 0L
)
