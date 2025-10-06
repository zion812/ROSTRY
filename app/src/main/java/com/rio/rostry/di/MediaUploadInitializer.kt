package com.rio.rostry.di

import android.content.Context
import com.rio.rostry.data.database.dao.UploadTaskDao
import com.rio.rostry.utils.media.MediaUploadManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MediaUploadInitializer @Inject constructor(
    private val manager: MediaUploadManager,
    private val uploadTaskDao: UploadTaskDao,
    @ApplicationContext private val context: Context
) {
    init {
        manager.attachOutbox(uploadTaskDao, context)
    }
}
