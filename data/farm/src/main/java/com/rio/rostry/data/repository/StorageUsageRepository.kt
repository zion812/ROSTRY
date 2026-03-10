package com.rio.rostry.data.repository

import com.rio.rostry.data.database.dao.StorageQuotaDao
import com.rio.rostry.data.database.entity.StorageQuotaEntity
import com.rio.rostry.data.storage.CloudStorageQuotaManager
import com.rio.rostry.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for managing cloud storage usage and quotas.
 */
@Singleton
class StorageUsageRepository @Inject constructor(
    private val storageQuotaDao: StorageQuotaDao,
    private val quotaManager: CloudStorageQuotaManager,
    private val userDao: com.rio.rostry.data.database.dao.UserDao
) {

    fun observeQuota(userId: String): Flow<StorageQuotaEntity?> {
        return storageQuotaDao.observeByUserId(userId)
    }

    suspend fun getQuota(userId: String): StorageQuotaEntity? {
        return storageQuotaDao.getByUserId(userId)
    }

    /**
     * Recalculates storage usage by scanning cloud storage and updating local cache.
     */
    suspend fun refreshUsage(userId: String): Resource<StorageQuotaEntity> {
        return try {
            val result = quotaManager.calculateStorageUsage(userId)
            val usage = when (result) {
                is Resource.Success -> result.data
                is Resource.Error -> throw Exception(result.message)
                else -> throw Exception("Unknown error calculating storage usage")
            }
            
            if (usage == null) {
                val existing = storageQuotaDao.getByUserId(userId)
                return if (existing != null) Resource.Success(existing)
                else Resource.Error("Could not calculate storage usage")
            }
            
            val existing = storageQuotaDao.getByUserId(userId)
            val now = System.currentTimeMillis()
            
            val updated = if (existing != null) {
                existing.copy(
                    usedBytes = usage.totalUsedBytes,
                    publicUsedBytes = usage.publicUsedBytes,
                    privateUsedBytes = usage.privateUsedBytes,
                    lastCalculatedAt = now,
                    updatedAt = now
                )
            } else {
                val user = userDao.getUserById(userId).firstOrNull()
                val roleQuota = quotaManager.getQuotaForRole(user?.role ?: com.rio.rostry.domain.model.UserType.GENERAL)
                StorageQuotaEntity.create(
                    userId = userId,
                    quotaBytes = roleQuota.totalQuotaBytes,
                    publicLimitBytes = roleQuota.publicLimitBytes,
                    privateLimitBytes = roleQuota.privateLimitBytes
                ).copy(
                    usedBytes = usage.totalUsedBytes,
                    publicUsedBytes = usage.publicUsedBytes,
                    privateUsedBytes = usage.privateUsedBytes,
                    lastCalculatedAt = now,
                    updatedAt = now
                )
            }
            
            storageQuotaDao.insert(updated)
            Resource.Success(updated)
        } catch (e: Exception) {
            Timber.e(e, "Failed to refresh storage usage for $userId")
            Resource.Error(e.message ?: "Failed to refresh usage")
        }
    }

    /**
     * Checks if enough space is available for an upcoming upload.
     */
    suspend fun hasEnoughSpace(userId: String, incomingBytes: Long): Boolean {
        val quota = getQuota(userId) ?: return true // Default to true if no quota record
        if (quota.quotaBytes <= 0) return true // Treat zero as unset (safety fallback)
        return (quota.usedBytes + incomingBytes) <= quota.quotaBytes
    }

    /**
     * Incrementally updates usage after a successful upload/delete.
     */
    suspend fun adjustUsage(userId: String, bytesDelta: Long) {
        val existing = storageQuotaDao.getByUserId(userId) ?: return
        val updated = existing.copy(
            usedBytes = (existing.usedBytes + bytesDelta).coerceAtLeast(0),
            lastCalculatedAt = System.currentTimeMillis()
        )
        storageQuotaDao.insert(updated)
    }
}
