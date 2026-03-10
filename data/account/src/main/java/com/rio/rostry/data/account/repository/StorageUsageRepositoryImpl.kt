package com.rio.rostry.data.account.repository

import com.rio.rostry.core.model.Result
import com.rio.rostry.core.model.StorageQuota
import com.rio.rostry.data.account.mapper.toStorageQuota
import com.rio.rostry.data.database.dao.StorageQuotaDao
import com.rio.rostry.data.database.entity.StorageQuotaEntity
import com.rio.rostry.domain.account.repository.StorageUsageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Lightweight quota cache implementation used by modularized account data module.
 */
@Singleton
class StorageUsageRepositoryImpl @Inject constructor(
    private val storageQuotaDao: StorageQuotaDao
) : StorageUsageRepository {

    companion object {
        private const val DEFAULT_QUOTA_BYTES = 25L * 1024L * 1024L // 25 MB
        private const val DEFAULT_PUBLIC_LIMIT_BYTES = 20L * 1024L * 1024L // 20 MB
        private const val DEFAULT_PRIVATE_LIMIT_BYTES = 5L * 1024L * 1024L // 5 MB
    }

    override fun observeQuota(userId: String): Flow<StorageQuota?> {
        return storageQuotaDao.observeByUserId(userId).map { it?.toStorageQuota() }
    }

    override suspend fun getQuota(userId: String): StorageQuota? {
        return storageQuotaDao.getByUserId(userId)?.toStorageQuota()
    }

    override suspend fun refreshUsage(userId: String): Result<StorageQuota> {
        return try {
            val now = System.currentTimeMillis()
            val existing = storageQuotaDao.getByUserId(userId)
            val entity = if (existing != null) {
                existing.copy(
                    lastCalculatedAt = now,
                    updatedAt = now
                )
            } else {
                StorageQuotaEntity.create(
                    userId = userId,
                    quotaBytes = DEFAULT_QUOTA_BYTES,
                    publicLimitBytes = DEFAULT_PUBLIC_LIMIT_BYTES,
                    privateLimitBytes = DEFAULT_PRIVATE_LIMIT_BYTES
                ).copy(
                    lastCalculatedAt = now,
                    updatedAt = now
                )
            }
            storageQuotaDao.insert(entity)
            Result.Success(entity.toStorageQuota())
        } catch (e: Exception) {
            
            Result.Error(e)
        }
    }

    override suspend fun hasEnoughSpace(userId: String, incomingBytes: Long): Boolean {
        val quota = storageQuotaDao.getByUserId(userId) ?: return true
        if (quota.quotaBytes <= 0) return true
        return (quota.usedBytes + incomingBytes) <= quota.quotaBytes
    }

    override suspend fun adjustUsage(userId: String, bytesDelta: Long) {
        val existing = storageQuotaDao.getByUserId(userId) ?: return
        val now = System.currentTimeMillis()
        val updated = existing.copy(
            usedBytes = (existing.usedBytes + bytesDelta).coerceAtLeast(0),
            lastCalculatedAt = now,
            updatedAt = now
        )
        storageQuotaDao.insert(updated)
    }
}

