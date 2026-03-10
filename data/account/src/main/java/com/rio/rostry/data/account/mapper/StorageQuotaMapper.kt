package com.rio.rostry.data.account.mapper

import com.rio.rostry.core.model.StorageQuota
import com.rio.rostry.data.database.entity.StorageQuotaEntity

/**
 * Converts StorageQuotaEntity to domain model.
 */
fun StorageQuotaEntity.toStorageQuota(): StorageQuota {
    return StorageQuota(
        userId = userId,
        quotaBytes = quotaBytes,
        usedBytes = usedBytes,
        publicLimitBytes = publicLimitBytes,
        privateLimitBytes = privateLimitBytes,
        publicUsedBytes = publicUsedBytes,
        privateUsedBytes = privateUsedBytes,
        lastCalculatedAt = lastCalculatedAt,
        createdAt = updatedAt,
        updatedAt = updatedAt
    )
}

/**
 * Converts domain model to StorageQuotaEntity.
 */
fun StorageQuota.toEntity(): StorageQuotaEntity {
    return StorageQuotaEntity(
        userId = userId,
        quotaBytes = quotaBytes,
        usedBytes = usedBytes,
        publicLimitBytes = publicLimitBytes,
        privateLimitBytes = privateLimitBytes,
        publicUsedBytes = publicUsedBytes,
        privateUsedBytes = privateUsedBytes,
        lastCalculatedAt = lastCalculatedAt,
        updatedAt = updatedAt
    )
}
