package com.rio.rostry.core.model

/**
 * Domain model for cloud storage quota information.
 */
data class StorageQuota(
    val userId: String,
    val quotaBytes: Long,
    val usedBytes: Long,
    val publicLimitBytes: Long,
    val privateLimitBytes: Long,
    val publicUsedBytes: Long,
    val privateUsedBytes: Long,
    val lastCalculatedAt: Long,
    val createdAt: Long,
    val updatedAt: Long
) {
    val availableBytes: Long
        get() = (quotaBytes - usedBytes).coerceAtLeast(0)
    
    val usagePercent: Int
        get() = if (quotaBytes > 0) ((usedBytes * 100) / quotaBytes).toInt() else 0
    
    val isNearLimit: Boolean
        get() = usagePercent >= 80
    
    val isOverLimit: Boolean
        get() = usedBytes >= quotaBytes
}
