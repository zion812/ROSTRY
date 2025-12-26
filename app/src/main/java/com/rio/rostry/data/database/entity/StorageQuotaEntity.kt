package com.rio.rostry.data.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Caches storage quota information locally for offline display.
 * 
 * Synced periodically with Firebase to track actual cloud usage.
 */
@Entity(
    tableName = "storage_quota",
    indices = [
        Index("lastCalculatedAt")
    ]
)
data class StorageQuotaEntity(
    @PrimaryKey
    val userId: String,
    
    // Quota limits (from role)
    val quotaBytes: Long,
    val publicLimitBytes: Long,
    val privateLimitBytes: Long,
    
    // Current usage
    val usedBytes: Long,
    val publicUsedBytes: Long,
    val privateUsedBytes: Long,
    
    // Breakdown by type
    val imageBytes: Long = 0,
    val documentBytes: Long = 0,
    val dataBytes: Long = 0,
    
    // Metadata
    val warningLevel: String = "NORMAL", // NORMAL, WARNING, CRITICAL, EXCEEDED
    val lastCalculatedAt: Long,
    val lastSyncedAt: Long? = null,
    val updatedAt: Long
) {
    val usagePercentage: Float
        get() = if (quotaBytes > 0) (usedBytes.toFloat() / quotaBytes) * 100f else 0f
    
    val publicUsagePercentage: Float
        get() = if (publicLimitBytes > 0) (publicUsedBytes.toFloat() / publicLimitBytes) * 100f else 0f
    
    val privateUsagePercentage: Float
        get() = if (privateLimitBytes > 0) (privateUsedBytes.toFloat() / privateLimitBytes) * 100f else 0f
    
    val isOverQuota: Boolean
        get() = usedBytes > quotaBytes
    
    val remainingBytes: Long
        get() = (quotaBytes - usedBytes).coerceAtLeast(0)
    
    companion object {
        fun create(
            userId: String,
            quotaBytes: Long,
            publicLimitBytes: Long,
            privateLimitBytes: Long
        ): StorageQuotaEntity {
            val now = System.currentTimeMillis()
            return StorageQuotaEntity(
                userId = userId,
                quotaBytes = quotaBytes,
                publicLimitBytes = publicLimitBytes,
                privateLimitBytes = privateLimitBytes,
                usedBytes = 0,
                publicUsedBytes = 0,
                privateUsedBytes = 0,
                lastCalculatedAt = now,
                updatedAt = now
            )
        }
    }
}
