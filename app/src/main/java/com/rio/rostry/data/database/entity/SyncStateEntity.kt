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
    val lastChatSyncAt: Long = 0L,
    // Farm monitoring sync timestamps
    val lastBreedingSyncAt: Long = 0L,
    val lastAlertSyncAt: Long = 0L,
    val lastDashboardSyncAt: Long = 0L,
    val lastVaccinationSyncAt: Long = 0L,
    val lastGrowthSyncAt: Long = 0L,
    val lastQuarantineSyncAt: Long = 0L,
    val lastMortalitySyncAt: Long = 0L,
    val lastHatchingSyncAt: Long = 0L,
    val lastHatchingLogSyncAt: Long = 0L,
    // Enthusiast-specific windows
    val lastEnthusiastBreedingSyncAt: Long = 0L,
    val lastEnthusiastDashboardSyncAt: Long = 0L,
    // Dedicated cursors
    val lastDailyLogSyncAt: Long = 0L,
    val lastTaskSyncAt: Long = 0L
)
