package com.rio.rostry.data.manager

import android.content.Context
import android.net.Uri
import com.rio.rostry.data.database.dao.MediaCacheMetadataDao
import com.rio.rostry.data.database.entity.MediaCacheMetadataEntity
import com.rio.rostry.domain.manager.MediaCacheManager
import com.rio.rostry.domain.error.MediaError
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MediaCacheManagerImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val mediaCacheMetadataDao: MediaCacheMetadataDao
) : MediaCacheManager {

    private val cacheDir: File by lazy {
        File(context.cacheDir, "gallery_media").apply {
            if (!exists()) mkdirs()
        }
    }

    override suspend fun cacheMedia(mediaId: String, sourceUri: Uri): Result<File> = withContext(Dispatchers.IO) {
        try {
            val destinationFile = File(cacheDir, "$mediaId.media")
            
            context.contentResolver.openInputStream(sourceUri)?.use { input ->
                destinationFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            } ?: return@withContext Result.failure(MediaError.StorageError.CacheCorruption)

            val fileSize = destinationFile.length()
            val now = System.currentTimeMillis()
            
            val metadata = MediaCacheMetadataEntity(
                mediaId = mediaId,
                localPath = destinationFile.absolutePath,
                fileSize = fileSize,
                downloadedAt = now,
                lastAccessedAt = now,
                accessCount = 1
            )
            mediaCacheMetadataDao.insertCacheMetadata(metadata)
            
            Result.success(destinationFile)
        } catch (e: Exception) {
            Result.failure(MediaError.StorageError.InsufficientSpace)
        }
    }

    override suspend fun getCachedMedia(mediaId: String): File? = withContext(Dispatchers.IO) {
        val metadata = mediaCacheMetadataDao.getCacheMetadata(mediaId) ?: return@withContext null
        val file = File(metadata.localPath)
        if (file.exists()) {
            mediaCacheMetadataDao.updateAccess(mediaId, System.currentTimeMillis())
            file
        } else {
            mediaCacheMetadataDao.deleteCacheMetadata(metadata)
            null
        }
    }

    override suspend fun removeCachedMedia(mediaId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val metadata = mediaCacheMetadataDao.getCacheMetadata(mediaId)
            if (metadata != null) {
                val file = File(metadata.localPath)
                if (file.exists()) file.delete()
                mediaCacheMetadataDao.deleteCacheMetadata(metadata)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(MediaError.StorageError.PermissionDenied)
        }
    }

    override suspend fun enforceLruPolicy(maxSpaceBytes: Long): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            var currentSize = mediaCacheMetadataDao.getTotalCacheSize() ?: 0L
            if (currentSize <= maxSpaceBytes) return@withContext Result.success(Unit)

            val excessBytes = currentSize - maxSpaceBytes
            var bytesFreed = 0L
            val limit = 50

            while (bytesFreed < excessBytes) {
                val lruItems = mediaCacheMetadataDao.getLeastRecentlyUsed(limit)
                if (lruItems.isEmpty()) break

                for (item in lruItems) {
                    val file = File(item.localPath)
                    if (file.exists()) file.delete()
                    mediaCacheMetadataDao.deleteCacheMetadata(item)
                    bytesFreed += item.fileSize
                    currentSize -= item.fileSize

                    if (currentSize <= maxSpaceBytes) break
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(MediaError.StorageError.CacheCorruption)
        }
    }

    override suspend fun clearCache(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val limit = 1000
            val allItems = mediaCacheMetadataDao.getLeastRecentlyUsed(limit)
            for (item in allItems) {
                val file = File(item.localPath)
                if (file.exists()) file.delete()
                mediaCacheMetadataDao.deleteCacheMetadata(item)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
