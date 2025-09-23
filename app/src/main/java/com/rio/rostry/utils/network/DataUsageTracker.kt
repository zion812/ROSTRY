package com.rio.rostry.utils.network

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import com.rio.rostry.session.sessionDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataUsageTracker @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val scope = CoroutineScope(Dispatchers.IO)

    private object Keys {
        val upload = longPreferencesKey("net_upload_bytes")
        val download = longPreferencesKey("net_download_bytes")
    }

    fun addUpload(host: String, bytes: Long) {
        if (bytes <= 0) return
        scope.launch { context.sessionDataStore.edit { it[Keys.upload] = (it[Keys.upload] ?: 0L) + bytes } }
    }

    fun addDownload(host: String, bytes: Long) {
        if (bytes <= 0) return
        scope.launch { context.sessionDataStore.edit { it[Keys.download] = (it[Keys.download] ?: 0L) + bytes } }
    }
}
