package com.rio.rostry.data.database.entity

import androidx.room.*

@Entity(
    tableName = "watched_lineages",
    indices = [
        Index(value = ["assetId"]),
        Index(value = ["lineageHash"]),
        Index(value = ["isDiscoveryFeedEnabled"])
    ]
)
data class WatchedLineageEntity(
    @PrimaryKey val watchId: String,
    val assetId: String,
    
    /**
     * Stable hash representing the biological identity of the lineage.
     * Currently implemented by using the stable assetId.
     */
    val lineageHash: String,
    val birdName: String? = null,
    val breed: String? = null,
    val ownerName: String? = null,
    val ownerAvatarUrl: String? = null,
    val isDiscoveryFeedEnabled: Boolean = true,
    val lastUpdateReceivedAt: Long? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val dirty: Boolean = false
)
