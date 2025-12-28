package com.rio.rostry.data.storage

import com.google.firebase.storage.FirebaseStorage
import com.rio.rostry.data.database.dao.StorageQuotaDao
import com.rio.rostry.data.database.entity.StorageQuotaEntity
import com.rio.rostry.domain.model.QuotaStatus
import com.rio.rostry.domain.model.QuotaWarningLevel
import com.rio.rostry.domain.model.RoleStorageQuota
import com.rio.rostry.domain.model.StorageUsage
import com.rio.rostry.domain.model.UserType
import com.rio.rostry.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages cloud storage quotas per user role.
 * 
 * Responsibilities:
 * - Calculate current storage usage across Firebase Storage
 * - Enforce quota limits per role (GENERAL: 5MB, FARMER: 10MB, ENTHUSIAST: 25MB)
 * - Track usage by category (images, documents, data)
 * - Emit quota warnings at threshold levels (80%, 90%, 100%)
 */
@Singleton
class CloudStorageQuotaManager @Inject constructor(
    private val storageQuotaDao: StorageQuotaDao,
    private val firebaseStorage: FirebaseStorage
) {
    companion object {
        private const val TAG = "CloudStorageQuotaManager"
        
        // Storage paths
        const val USERS_PATH = "users"
        const val PUBLIC_PATH = "public"
        const val PRIVATE_PATH = "private"
        const val PROFILE_PATH = "profile"
        const val LISTINGS_PATH = "listings"
        const val SHOWCASES_PATH = "showcases"
        const val FARM_ASSETS_PATH = "farm_assets"
        const val RECORDS_PATH = "records"
        const val BACKUPS_PATH = "backups"
    }
    
    /**
     * Get the quota configuration for a user role.
     */
    fun getQuotaForRole(role: UserType): RoleStorageQuota = RoleStorageQuota.forRole(role)
    
    /**
     * Observe quota status for a user.
     */
    fun observeQuotaStatus(userId: String, role: UserType): Flow<QuotaStatus?> {
        val quota = getQuotaForRole(role)
        return storageQuotaDao.observeByUserId(userId).map { entity ->
            entity?.let {
                val usage = StorageUsage(
                    totalUsedBytes = it.usedBytes,
                    publicUsedBytes = it.publicUsedBytes,
                    privateUsedBytes = it.privateUsedBytes,
                    imageBytes = it.imageBytes,
                    documentBytes = it.documentBytes,
                    dataBytes = it.dataBytes,
                    lastCalculatedAt = it.lastCalculatedAt
                )
                QuotaStatus.calculate(quota, usage)
            }
        }
    }
    
    /**
     * Get cached quota status synchronously.
     */
    suspend fun getCachedQuotaStatus(userId: String, role: UserType): QuotaStatus? {
        val entity = storageQuotaDao.getByUserId(userId) ?: return null
        val quota = getQuotaForRole(role)
        val usage = StorageUsage(
            totalUsedBytes = entity.usedBytes,
            publicUsedBytes = entity.publicUsedBytes,
            privateUsedBytes = entity.privateUsedBytes,
            imageBytes = entity.imageBytes,
            documentBytes = entity.documentBytes,
            dataBytes = entity.dataBytes,
            lastCalculatedAt = entity.lastCalculatedAt
        )
        return QuotaStatus.calculate(quota, usage)
    }
    
    /**
     * Initialize quota tracking for a user.
     */
    suspend fun initializeQuota(userId: String, role: UserType) {
        val existing = storageQuotaDao.getByUserId(userId)
        if (existing == null) {
            val quota = getQuotaForRole(role)
            storageQuotaDao.insert(
                StorageQuotaEntity.create(
                    userId = userId,
                    quotaBytes = quota.totalQuotaBytes,
                    publicLimitBytes = quota.publicLimitBytes,
                    privateLimitBytes = quota.privateLimitBytes
                )
            )
            Timber.d("Initialized quota for user $userId with role ${role.name}")
        }
    }
    
    /**
     * Update quota limits when role changes (e.g., upgrade to ENTHUSIAST).
     */
    suspend fun updateQuotaLimits(userId: String, newRole: UserType) {
        val quota = getQuotaForRole(newRole)
        storageQuotaDao.updateQuotaLimits(
            userId = userId,
            quotaBytes = quota.totalQuotaBytes,
            publicLimitBytes = quota.publicLimitBytes,
            privateLimitBytes = quota.privateLimitBytes
        )
        Timber.d("Updated quota limits for user $userId to role ${newRole.name}")
    }
    
    /**
     * Calculate actual storage usage from Firebase Storage.
     * This is an expensive operation and should be run infrequently.
     */
    suspend fun calculateStorageUsage(userId: String): Resource<StorageUsage> = withContext(Dispatchers.IO) {
        try {
            val userRef = firebaseStorage.reference.child("$USERS_PATH/$userId")
            
            var totalBytes = 0L
            var publicBytes = 0L
            var privateBytes = 0L
            var imageBytes = 0L
            var documentBytes = 0L
            var dataBytes = 0L
            
            // Calculate public storage
            try {
                val publicRef = userRef.child(PUBLIC_PATH)
                val publicUsage = calculateDirectorySize(publicRef.path)
                publicBytes = publicUsage.totalBytes
                imageBytes += publicUsage.imageBytes
                documentBytes += publicUsage.documentBytes
                dataBytes += publicUsage.dataBytes
            } catch (e: Exception) {
                Timber.w(e, "Error calculating public storage for $userId")
            }
            
            // Calculate private storage
            try {
                val privateRef = userRef.child(PRIVATE_PATH)
                val privateUsage = calculateDirectorySize(privateRef.path)
                privateBytes = privateUsage.totalBytes
                imageBytes += privateUsage.imageBytes
                documentBytes += privateUsage.documentBytes
                dataBytes += privateUsage.dataBytes
            } catch (e: Exception) {
                Timber.w(e, "Error calculating private storage for $userId")
            }
            
            totalBytes = publicBytes + privateBytes
            
            val usage = StorageUsage(
                totalUsedBytes = totalBytes,
                publicUsedBytes = publicBytes,
                privateUsedBytes = privateBytes,
                imageBytes = imageBytes,
                documentBytes = documentBytes,
                dataBytes = dataBytes
            )
            
            // Update local cache
            updateLocalUsage(userId, usage)
            
            Timber.d("Calculated storage for $userId: ${usage.totalUsedMB}MB total")
            Resource.Success(usage)
        } catch (e: Exception) {
            Timber.e(e, "Failed to calculate storage usage for $userId")
            Resource.Error("Failed to calculate storage: ${e.message}")
        }
    }
    
    /**
     * Check if user can upload a file of given size.
     */
    suspend fun canUpload(userId: String, role: UserType, fileSize: Long, isPublic: Boolean): Boolean {
        val entity = storageQuotaDao.getByUserId(userId) ?: return true // Allow if no quota tracked yet
        val quota = getQuotaForRole(role)
        
        // Check total quota
        if (entity.usedBytes + fileSize > quota.totalQuotaBytes) {
            return false
        }
        
        // Check category-specific limit
        if (isPublic && entity.publicUsedBytes + fileSize > quota.publicLimitBytes) {
            return false
        }
        if (!isPublic && entity.privateUsedBytes + fileSize > quota.privateLimitBytes) {
            return false
        }
        
        return true
    }
    
    /**
     * Get remaining storage space.
     */
    suspend fun getRemainingSpace(userId: String, role: UserType): Long {
        val entity = storageQuotaDao.getByUserId(userId) ?: return getQuotaForRole(role).totalQuotaBytes
        val quota = getQuotaForRole(role)
        return (quota.totalQuotaBytes - entity.usedBytes).coerceAtLeast(0)
    }
    
    /**
     * Increment usage after successful upload.
     */
    suspend fun incrementUsage(userId: String, bytes: Long, isPublic: Boolean, isImage: Boolean) {
        val entity = storageQuotaDao.getByUserId(userId) ?: return
        val now = System.currentTimeMillis()
        
        val newUsed = entity.usedBytes + bytes
        val newPublicUsed = if (isPublic) entity.publicUsedBytes + bytes else entity.publicUsedBytes
        val newPrivateUsed = if (!isPublic) entity.privateUsedBytes + bytes else entity.privateUsedBytes
        val newImageBytes = if (isImage) entity.imageBytes + bytes else entity.imageBytes
        val newDocBytes = if (!isImage) entity.documentBytes + bytes else entity.documentBytes
        
        val warningLevel = calculateWarningLevel(newUsed, entity.quotaBytes)
        
        storageQuotaDao.updateUsage(
            userId = userId,
            usedBytes = newUsed,
            publicUsedBytes = newPublicUsed,
            privateUsedBytes = newPrivateUsed,
            imageBytes = newImageBytes,
            documentBytes = newDocBytes,
            dataBytes = entity.dataBytes,
            warningLevel = warningLevel.name,
            timestamp = now
        )
    }
    
    /**
     * Decrement usage after successful deletion.
     */
    suspend fun decrementUsage(userId: String, bytes: Long, isPublic: Boolean, isImage: Boolean) {
        val entity = storageQuotaDao.getByUserId(userId) ?: return
        val now = System.currentTimeMillis()
        
        val newUsed = (entity.usedBytes - bytes).coerceAtLeast(0)
        val newPublicUsed = if (isPublic) (entity.publicUsedBytes - bytes).coerceAtLeast(0) else entity.publicUsedBytes
        val newPrivateUsed = if (!isPublic) (entity.privateUsedBytes - bytes).coerceAtLeast(0) else entity.privateUsedBytes
        val newImageBytes = if (isImage) (entity.imageBytes - bytes).coerceAtLeast(0) else entity.imageBytes
        val newDocBytes = if (!isImage) (entity.documentBytes - bytes).coerceAtLeast(0) else entity.documentBytes
        
        val warningLevel = calculateWarningLevel(newUsed, entity.quotaBytes)
        
        storageQuotaDao.updateUsage(
            userId = userId,
            usedBytes = newUsed,
            publicUsedBytes = newPublicUsed,
            privateUsedBytes = newPrivateUsed,
            imageBytes = newImageBytes,
            documentBytes = newDocBytes,
            dataBytes = entity.dataBytes,
            warningLevel = warningLevel.name,
            timestamp = now
        )
    }
    
    // Private helpers
    
    private suspend fun updateLocalUsage(userId: String, usage: StorageUsage) {
        val entity = storageQuotaDao.getByUserId(userId)
        if (entity != null) {
            val warningLevel = calculateWarningLevel(usage.totalUsedBytes, entity.quotaBytes)
            storageQuotaDao.updateUsage(
                userId = userId,
                usedBytes = usage.totalUsedBytes,
                publicUsedBytes = usage.publicUsedBytes,
                privateUsedBytes = usage.privateUsedBytes,
                imageBytes = usage.imageBytes,
                documentBytes = usage.documentBytes,
                dataBytes = usage.dataBytes,
                warningLevel = warningLevel.name
            )
        }
    }
    
    private fun calculateWarningLevel(usedBytes: Long, quotaBytes: Long): QuotaWarningLevel {
        if (quotaBytes <= 0) return QuotaWarningLevel.NORMAL
        val percentage = (usedBytes.toFloat() / quotaBytes) * 100f
        return when {
            percentage >= 100f -> QuotaWarningLevel.EXCEEDED
            percentage >= 90f -> QuotaWarningLevel.CRITICAL
            percentage >= 80f -> QuotaWarningLevel.WARNING
            else -> QuotaWarningLevel.NORMAL
        }
    }
    
    private data class DirectoryUsage(
        val totalBytes: Long,
        val imageBytes: Long,
        val documentBytes: Long,
        val dataBytes: Long
    )
    
    private suspend fun calculateDirectorySize(path: String): DirectoryUsage {
        var totalBytes = 0L
        var imageBytes = 0L
        var documentBytes = 0L
        var dataBytes = 0L
        
        try {
            val ref = firebaseStorage.reference.child(path)
            val result = ref.listAll().await()
            
            // Calculate size of files in this directory
            for (item in result.items) {
                try {
                    val metadata = item.metadata.await()
                    val size = metadata.sizeBytes
                    totalBytes += size
                    
                    val contentType = metadata.contentType ?: ""
                    when {
                        contentType.startsWith("image/") -> imageBytes += size
                        contentType.startsWith("application/pdf") || 
                        contentType.startsWith("application/") -> documentBytes += size
                        else -> dataBytes += size
                    }
                } catch (e: Exception) {
                    Timber.w(e, "Error getting metadata for ${item.path}")
                }
            }
            
            // Recursively calculate subdirectories
            for (prefix in result.prefixes) {
                val subUsage = calculateDirectorySize(prefix.path)
                totalBytes += subUsage.totalBytes
                imageBytes += subUsage.imageBytes
                documentBytes += subUsage.documentBytes
                dataBytes += subUsage.dataBytes
            }
        } catch (e: Exception) {
            Timber.w(e, "Error listing directory $path")
        }
        
        return DirectoryUsage(totalBytes, imageBytes, documentBytes, dataBytes)
    }
}
