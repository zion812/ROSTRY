package com.rio.rostry.data.repository

import com.rio.rostry.data.database.dao.MediaItemDao
import com.rio.rostry.data.database.dao.MediaTagDao
import com.rio.rostry.data.mapper.toDomainModel
import com.rio.rostry.data.mapper.toEntity
import com.rio.rostry.domain.manager.MediaCacheManager
import com.rio.rostry.domain.model.media.MediaFilter
import com.rio.rostry.domain.model.media.MediaItem
import com.rio.rostry.domain.model.media.MediaTag
import com.rio.rostry.domain.model.media.PaginationState
import com.rio.rostry.domain.repository.MediaGalleryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MediaGalleryRepositoryImpl @Inject constructor(
    private val mediaItemDao: MediaItemDao,
    private val mediaTagDao: MediaTagDao,
    private val mediaCacheManager: MediaCacheManager
) : MediaGalleryRepository {

    override fun observeMedia(
        filter: MediaFilter,
        pagination: PaginationState
    ): Flow<List<MediaItem>> {
        val limit = pagination.pageSize * (pagination.currentPage + 1)
        val offset = 0

        // Determine the media type filter string
        val mediaTypeStr = if (filter.mediaTypes.size == 1) {
            filter.mediaTypes.first().name
        } else null

        // Use the filtered query that respects ownerId, assetId, and mediaType
        return mediaItemDao.observeMediaFiltered(
            ownerId = filter.ownerId,
            assetId = filter.assetId,
            mediaType = mediaTypeStr,
            limit = limit,
            offset = offset
        ).map { entities ->
            entities.map { entity ->
                val tags = mediaTagDao.getTagsForMedia(entity.mediaId)
                entity.toDomainModel(tags)
            }
        }
    }

    override suspend fun getMediaItem(mediaId: String): MediaItem? = withContext(Dispatchers.IO) {
        val entity = mediaItemDao.getMediaById(mediaId) ?: return@withContext null
        val tags = mediaTagDao.getTagsForMedia(mediaId)
        entity.toDomainModel(tags)
    }

    override suspend fun addMediaItem(mediaItem: MediaItem): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            mediaItemDao.insertMedia(mediaItem.toEntity())
            mediaTagDao.insertTags(mediaItem.tags.map { it.toEntity(mediaItem.mediaId) })
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateMediaItem(mediaItem: MediaItem): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            mediaItemDao.updateMedia(mediaItem.toEntity())
            // Sync tags
            mediaTagDao.deleteTagsForMedia(mediaItem.mediaId)
            mediaTagDao.insertTags(mediaItem.tags.map { it.toEntity(mediaItem.mediaId) })
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteMediaItem(mediaId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val entity = mediaItemDao.getMediaById(mediaId)
            if (entity != null) {
                mediaCacheManager.removeCachedMedia(mediaId)
                mediaTagDao.deleteTagsForMedia(mediaId)
                mediaItemDao.deleteMedia(entity)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun addTagsToMedia(mediaId: String, tags: List<MediaTag>): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            mediaTagDao.insertTags(tags.map { it.toEntity(mediaId) })
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun removeTagsFromMedia(mediaId: String, tagIds: List<String>): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val currentTags = mediaTagDao.getTagsForMedia(mediaId)
            val toKeep = currentTags.filter { it.tagId !in tagIds }
            mediaTagDao.deleteTagsForMedia(mediaId)
            mediaTagDao.insertTags(toKeep)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getDistinctTagValues(tagType: String): List<String> = withContext(Dispatchers.IO) {
        mediaTagDao.getDistinctTagValues(tagType)
    }

    override suspend fun getMediaFile(mediaItem: MediaItem): Result<File> = withContext(Dispatchers.IO) {
        val cachedFile = mediaCacheManager.getCachedMedia(mediaItem.mediaId)
        if (cachedFile != null) {
            Result.success(cachedFile)
        } else {
            Result.failure(Exception("File not available locally"))
        }
    }

    override suspend fun exportMediaItems(mediaIds: List<String>, destinationDir: File): Result<File> = withContext(Dispatchers.IO) {
        try {
            val zipFile = File(destinationDir, "export_${System.currentTimeMillis()}.zip")
            if (!destinationDir.exists()) destinationDir.mkdirs()
            zipFile.createNewFile()
            Result.success(zipFile)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun synchronizeCache(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            mediaCacheManager.enforceLruPolicy(100 * 1024 * 1024) // 100MB limit
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun syncWithRemote(): Result<Unit> = withContext(Dispatchers.IO) {
        // SyncManager handles the actual Firestore push/pull for media items
        Result.success(Unit)
    }

    override fun getMediaForAsset(ownerId: String, assetId: String): Flow<List<MediaItem>> {
        return mediaItemDao.getMediaByOwnerAndAsset(ownerId, assetId).map { entities ->
            entities.map { entity ->
                val tags = mediaTagDao.getTagsForMedia(entity.mediaId)
                entity.toDomainModel(tags)
            }
        }
    }

    override suspend fun updateCaption(mediaId: String, caption: String?, notes: String?): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            mediaItemDao.updateCaption(mediaId, caption, notes)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

