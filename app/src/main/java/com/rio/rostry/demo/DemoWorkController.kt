package com.rio.rostry.demo

import android.content.Context
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.rio.rostry.workers.OutgoingMessageWorker
import com.rio.rostry.workers.SyncWorker
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DemoWorkController @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun runSyncNow() {
        val req = OneTimeWorkRequestBuilder<SyncWorker>()
            .build()
        WorkManager.getInstance(context).enqueue(req)
    }

    fun runOutgoingNow(requireNetwork: Boolean = false) {
        val constraints = if (requireNetwork) {
            Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
        } else null
        val builder = OneTimeWorkRequestBuilder<OutgoingMessageWorker>()
        if (constraints != null) builder.setConstraints(constraints)
        WorkManager.getInstance(context).enqueue(builder.build())
    }
}
