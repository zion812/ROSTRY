package com.rio.rostry.domain.model

/**
 * Data category classification for hybrid cloud storage strategy.
 * 
 * Determines where and how data is stored:
 * - PUBLIC_CLOUD_REQUIRED: Always synced to cloud, accessible by other users
 * - PRIVATE_OFFLINE_FIRST: Local-first storage, optional cloud backup
 * - HYBRID_SYNC: Bidirectional sync with offline support
 */
enum class DataCategory(
    val displayName: String,
    val requiresCloud: Boolean,
    val allowsOffline: Boolean
) {
    /**
     * Public data that must be stored on cloud for accessibility.
     * Examples: Market listings, farmer profiles, showcases
     */
    PUBLIC_CLOUD_REQUIRED(
        displayName = "Public Cloud",
        requiresCloud = true,
        allowsOffline = false
    ),

    /**
     * Private operational data stored locally with optional cloud backup.
     * Examples: Farm assets (private), daily logs, health records
     */
    PRIVATE_OFFLINE_FIRST(
        displayName = "Private Local",
        requiresCloud = false,
        allowsOffline = true
    ),

    /**
     * Data requiring bidirectional sync between local and cloud.
     * Examples: Orders, transfers, social posts
     */
    HYBRID_SYNC(
        displayName = "Hybrid Sync",
        requiresCloud = false,
        allowsOffline = true
    );

    companion object {
        /**
         * Get storage category for entity type based on role and visibility.
         */
        fun forEntity(
            entityType: String,
            isPublic: Boolean = false,
            userRole: UserType = UserType.FARMER
        ): DataCategory {
            return when {
                // Market listings are always public cloud
                entityType == "MarketListing" -> PUBLIC_CLOUD_REQUIRED
                // User profiles are public cloud
                entityType == "User" && isPublic -> PUBLIC_CLOUD_REQUIRED
                // Farm assets: showcases are public, others are private (for FARMER)
                entityType == "FarmAsset" && isPublic -> PUBLIC_CLOUD_REQUIRED
                entityType == "FarmAsset" && userRole == UserType.ENTHUSIAST -> HYBRID_SYNC
                entityType == "FarmAsset" -> PRIVATE_OFFLINE_FIRST
                // Daily logs: private for FARMER, synced for ENTHUSIAST
                entityType == "DailyLog" && userRole == UserType.ENTHUSIAST -> HYBRID_SYNC
                entityType == "DailyLog" -> PRIVATE_OFFLINE_FIRST
                // Orders and transfers always sync
                entityType in listOf("Order", "Transfer") -> HYBRID_SYNC
                // Default to hybrid sync
                else -> HYBRID_SYNC
            }
        }
    }
}

/**
 * Annotation to mark entity data category for automatic classification.
 */
annotation class DataCategoryType(val category: DataCategory)

/**
 * Storage quota definition per user role.
 */
data class RoleStorageQuota(
    val role: UserType,
    val totalQuotaBytes: Long,
    val publicLimitBytes: Long,
    val privateLimitBytes: Long,
    val backupEnabled: Boolean,
    val backupFrequencyDays: Int
) {
    companion object {
        private const val MB = 1024L * 1024L
        private const val GB = 1024L * MB

        val GENERAL = RoleStorageQuota(
            role = UserType.GENERAL,
            totalQuotaBytes = 5 * MB,
            publicLimitBytes = 5 * MB,
            privateLimitBytes = 0,
            backupEnabled = false,
            backupFrequencyDays = 0
        )

        val FARMER = RoleStorageQuota(
            role = UserType.FARMER,
            totalQuotaBytes = 10 * MB,
            publicLimitBytes = 5 * MB,
            privateLimitBytes = 5 * MB,
            backupEnabled = true,
            backupFrequencyDays = 7
        )

        val ENTHUSIAST = RoleStorageQuota(
            role = UserType.ENTHUSIAST,
            totalQuotaBytes = 25 * MB,
            publicLimitBytes = 15 * MB,
            privateLimitBytes = 10 * MB,
            backupEnabled = true,
            backupFrequencyDays = 1
        )

        fun forRole(role: UserType): RoleStorageQuota = when (role) {
            UserType.GENERAL -> GENERAL
            UserType.FARMER -> FARMER
            UserType.ENTHUSIAST -> ENTHUSIAST
            UserType.ADMIN, UserType.SUPPORT, UserType.MODERATOR -> ENTHUSIAST // Admins get max quota
        }
    }
}

/**
 * Current storage usage breakdown.
 */
data class StorageUsage(
    val totalUsedBytes: Long,
    val publicUsedBytes: Long,
    val privateUsedBytes: Long,
    val imageBytes: Long,
    val documentBytes: Long,
    val dataBytes: Long,
    val lastCalculatedAt: Long = System.currentTimeMillis()
) {
    val totalUsedMB: Double get() = totalUsedBytes / (1024.0 * 1024.0)
    val publicUsedMB: Double get() = publicUsedBytes / (1024.0 * 1024.0)
    val privateUsedMB: Double get() = privateUsedBytes / (1024.0 * 1024.0)
}

/**
 * Quota status with usage percentages and warnings.
 */
data class QuotaStatus(
    val quota: RoleStorageQuota,
    val usage: StorageUsage,
    val usagePercentage: Float,
    val publicUsagePercentage: Float,
    val privateUsagePercentage: Float,
    val warningLevel: QuotaWarningLevel
) {
    companion object {
        fun calculate(quota: RoleStorageQuota, usage: StorageUsage): QuotaStatus {
            val usagePercent = if (quota.totalQuotaBytes > 0) {
                (usage.totalUsedBytes.toFloat() / quota.totalQuotaBytes) * 100f
            } else 0f

            val publicPercent = if (quota.publicLimitBytes > 0) {
                (usage.publicUsedBytes.toFloat() / quota.publicLimitBytes) * 100f
            } else 0f

            val privatePercent = if (quota.privateLimitBytes > 0) {
                (usage.privateUsedBytes.toFloat() / quota.privateLimitBytes) * 100f
            } else 0f

            val warningLevel = when {
                usagePercent >= 100f -> QuotaWarningLevel.EXCEEDED
                usagePercent >= 90f -> QuotaWarningLevel.CRITICAL
                usagePercent >= 80f -> QuotaWarningLevel.WARNING
                else -> QuotaWarningLevel.NORMAL
            }

            return QuotaStatus(
                quota = quota,
                usage = usage,
                usagePercentage = usagePercent,
                publicUsagePercentage = publicPercent,
                privateUsagePercentage = privatePercent,
                warningLevel = warningLevel
            )
        }
    }
}

/**
 * Warning levels for storage quota.
 */
enum class QuotaWarningLevel {
    NORMAL,    // < 80%
    WARNING,   // 80-89%
    CRITICAL,  // 90-99%
    EXCEEDED   // >= 100%
}
