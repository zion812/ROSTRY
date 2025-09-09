package com.rio.rostry.data.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import android.util.Log
import androidx.room.Room
import com.rio.rostry.data.local.db.AppDatabase
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers

class OutboxSyncWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            // Placeholder: pick oldest outbox item and simulate success
            val db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "rostry.db")
                .fallbackToDestructiveMigration()
                .build()
            val outboxDao = db.outboxDao()
            val item = outboxDao.oldest() ?: return@withContext Result.success()
            // TODO: send to Cloud Function / Firestore transaction
            outboxDao.delete(item)
            Result.success()
        } catch (t: Throwable) {
            Log.e("OutboxSyncWorker", "OutboxSyncWorker failure", t)
            Result.retry()
        }
    }
}
