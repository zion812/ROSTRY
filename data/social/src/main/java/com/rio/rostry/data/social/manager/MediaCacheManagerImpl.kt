package com.rio.rostry.data.social.manager

import android.content.Context
import android.net.Uri
import com.rio.rostry.data.database.dao.MediaCacheMetadataDao
import com.rio.rostry.data.database.entity.MediaCacheMetadataEntity
import com.rio.rostry.domain.social.manager.MediaCacheManager
import com.rio.rostry.core.common.Result
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

    override suspend fun cacheMedia(mediaId: String, sourceUri: String): Result<File> = withContext(Dispatchers.IO) {
        try {
            val parsedUri = Uri.parse(sourceUri)
            val destinationFile = File(cacheDir, "$mediaId.media")
            
            context.contentResolver.openInputStream(parsedUri)?.use { input ->
                destinationFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            } ?: return@withContext Result.Error(Exception("CacheCorruption")) // Simplified error for now

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
            
            Result.Success(destinationFile)
        } catch (e: Exception) {
            Result.Error(Exception("InsufficientSpace", e))
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
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(Exception("PermissionDenied", e))
        }
    }

    override suspend fun enforceLruPolicy(maxSpaceBytes: Long): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            var currentSize = mediaCacheMetadataDao.getTotalCacheSize() ?: 0L
            if (currentSize <= maxSpaceBytes) return@withContext Result.Success(Unit)

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
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(Exception("CacheCorruption", e))
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
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}
