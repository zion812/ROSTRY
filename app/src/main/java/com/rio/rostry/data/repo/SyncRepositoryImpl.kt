package com.rio.rostry.data.repo

import android.content.Context
import androidx.work.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.rio.rostry.data.local.FowlDao
import com.rio.rostry.data.local.FowlRecordDao
import com.rio.rostry.data.models.Fowl
import com.rio.rostry.data.models.FowlRecord
import com.rio.rostry.utils.NetworkMonitor
import com.rio.rostry.utils.PerformanceLogger
import com.rio.rostry.worker.SyncWorker
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlin.system.measureTimeMillis
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class SyncRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val fowlDao: FowlDao,
    private val fowlRecordDao: FowlRecordDao,
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val networkMonitor: NetworkMonitor,
    private val performanceLogger: PerformanceLogger
) : SyncRepository {

    private val workManager = WorkManager.getInstance(context)

    override val syncWorkInfo: Flow<WorkInfo?> =
        workManager.getWorkInfosForUniqueWorkFlow("sync-work")
            .map { it.firstOrNull() }

    override suspend fun sync(): Boolean {
        if (!networkMonitor.isConnected().first()) {
            Timber.d("Sync skipped: No network connection")
            return true // Not a failure, just offline
        }

        val userId = auth.currentUser?.uid
        if (userId.isNullOrEmpty()) return false

        var totalItemsSynced = 0
        var success = false
        val duration = measureTimeMillis {
            try {
                totalItemsSynced += syncFowlData(userId)
                totalItemsSynced += syncAllFowlRecords(userId)
                success = true
            } catch (e: Exception) {
                Timber.e(e, "Error during data synchronization")
                success = false
            }
        }

        performanceLogger.logSync(duration, success, totalItemsSynced)
        return success
    }

    override fun startSync() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val syncRequest = OneTimeWorkRequestBuilder<SyncWorker>()
            .setConstraints(constraints)
            .setBackoffCriteria(
                BackoffPolicy.LINEAR,
                WorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS
            )
            .build()

        workManager.enqueueUniqueWork(
            "sync-work",
            ExistingWorkPolicy.KEEP,
            syncRequest
        )
    }

    private suspend fun syncFowlData(userId: String): Int {
        val snapshot = firestore.collection("users").document(userId).collection("fowls").get().await()
        val fowls = snapshot.toObjects(Fowl::class.java)
        fowlDao.insertFowls(fowls)
        return fowls.size
    }

    private suspend fun syncAllFowlRecords(userId: String): Int {
        var recordsSynced = 0
        val fowlsSnapshot = firestore.collection("users").document(userId).collection("fowls").get().await()
        for (fowlDocument in fowlsSnapshot.documents) {
            val fowlId = fowlDocument.id
            val recordsSnapshot = firestore.collection("users").document(userId)
                .collection("fowls").document(fowlId)
                .collection("records").get().await()
            val records = recordsSnapshot.toObjects(FowlRecord::class.java).map { it.copy(userId = userId) }
            fowlRecordDao.insertRecords(records)
            recordsSynced += records.size
        }
        return recordsSynced
    }
}
