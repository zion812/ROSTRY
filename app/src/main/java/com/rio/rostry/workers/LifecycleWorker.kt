package com.rio.rostry.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.rio.rostry.data.database.dao.LifecycleEventDao
import com.rio.rostry.data.database.dao.ProductDao
import com.rio.rostry.data.database.entity.LifecycleEventEntity
import com.rio.rostry.utils.ValidationUtils
import com.rio.rostry.utils.MilestoneNotifier
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.util.UUID
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class LifecycleWorker(
    appContext: Context,
    params: WorkerParameters,
    private val productDao: ProductDao,
    private val lifecycleDao: LifecycleEventDao,
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val now = System.currentTimeMillis()
            MilestoneNotifier.ensureChannel(applicationContext)
            // For simplicity, scan products snapshot; in a full impl, add DAO to list active items only
            val products = productDao.getAllProducts().first()
            for (p in products) {
                val week = p.birthDate?.let { ((now - it) / (7L * 24 * 60 * 60 * 1000)).toInt() } ?: continue
                val stage = when {
                    week < 5 -> "CHICK"
                    week < 20 -> "GROWTH"
                    week < 52 -> "ADULT"
                    else -> "BREEDER"
                }
                // Sample milestone rules
                val milestones = mutableListOf<LifecycleEventEntity>()
                if (stage == "CHICK" && (week == 0 || week == 2 || week == 4)) {
                    milestones += LifecycleEventEntity(
                        eventId = UUID.randomUUID().toString(),
                        productId = p.productId,
                        week = week,
                        stage = stage,
                        type = "VACCINATION",
                        notes = "Vaccination checkpoint (week $week)"
                    )
                }
                if (stage == "GROWTH") {
                    milestones += LifecycleEventEntity(
                        eventId = UUID.randomUUID().toString(),
                        productId = p.productId,
                        week = week,
                        stage = stage,
                        type = "GROWTH_UPDATE",
                        notes = "Weekly growth update"
                    )
                }
                if (stage == "ADULT" && (week == 20 || week == 30 || week == 40)) {
                    milestones += LifecycleEventEntity(
                        eventId = UUID.randomUUID().toString(),
                        productId = p.productId,
                        week = week,
                        stage = stage,
                        type = "MILESTONE",
                        notes = "Adult stage milestone (week $week)"
                    )
                }
                if (stage == "BREEDER" && week == 52) {
                    milestones += LifecycleEventEntity(
                        eventId = UUID.randomUUID().toString(),
                        productId = p.productId,
                        week = week,
                        stage = stage,
                        type = "MILESTONE",
                        notes = "Breeder eligibility"
                    )
                }
                // Persist generated milestones and notify
                milestones.forEach { 
                    lifecycleDao.insert(it)
                    MilestoneNotifier.notify(applicationContext, p.productId, it)
                }
            }
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    companion object {
        private const val UNIQUE_NAME = "LifecycleWorkerPeriodic"
        fun schedule(context: Context) {
            val request = PeriodicWorkRequestBuilder<LifecycleWorker>(1, TimeUnit.DAYS)
                .build()
            WorkManager.getInstance(context)
                .enqueueUniquePeriodicWork(UNIQUE_NAME, ExistingPeriodicWorkPolicy.UPDATE, request)
        }
    }
}
