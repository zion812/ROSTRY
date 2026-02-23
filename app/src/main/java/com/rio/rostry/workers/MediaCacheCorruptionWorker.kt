package com.rio.rostry.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.rio.rostry.data.database.dao.MediaCacheMetadataDao
import com.rio.rostry.data.database.dao.MediaItemDao
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

@HiltWorker
class MediaCacheCorruptionWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val mediaCacheMetadataDao: MediaCacheMetadataDao,
    private val mediaItemDao: MediaItemDao
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            // Check for DB records without files
            val allMetadata = mediaCacheMetadataDao.getLeastRecentlyUsed(10000)
            for (metadata in allMetadata) {
                val file = File(metadata.localPath)
                if (!file.exists()) {
                    mediaCacheMetadataDao.deleteCacheMetadata(metadata)
                    mediaItemDao.updateCacheStatus(metadata.mediaId, false)
                }
            }
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }
}
