package com.rio.rostry.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.rio.rostry.data.database.dao.FarmAlertDao
import com.rio.rostry.data.database.dao.ProductDao
import com.rio.rostry.data.database.entity.FarmAlertEntity
import com.rio.rostry.data.repository.TraceabilityRepository
import com.rio.rostry.ui.navigation.RouteConstants
import com.rio.rostry.utils.notif.EnthusiastNotifier
import com.rio.rostry.utils.Resource
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import timber.log.Timber
import java.util.UUID

@HiltWorker
class HealthAnomalyWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val productDao: ProductDao,
    private val alertDao: FarmAlertDao,
    private val traceability: TraceabilityRepository
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val now = System.currentTimeMillis()
        val products = productDao.getActiveWithBirth()
        
        for (p in products) {
            processLineageAudit(p, now)
        }
        return Result.success()
    }

    private suspend fun processLineageAudit(
        p: com.rio.rostry.data.database.entity.ProductEntity,
        now: Long
    ) {
        val ancestors = traceability.ancestors(p.productId, maxDepth = 3)
        when (ancestors) {
            is Resource.Error -> {
                alertDao.upsert(
                    FarmAlertEntity(
                        alertId = UUID.randomUUID().toString(),
                        farmerId = p.sellerId,
                        alertType = "LINEAGE_INCONSISTENCY",
                        severity = "MEDIUM",
                        message = "Lineage inconsistency for ${p.name}: ${ancestors.message}",
                        actionRoute = "family_tree/${p.productId}",
                        createdAt = now
                    )
                )
                EnthusiastNotifier.lineageAlert(applicationContext, p.productId, "Lineage alert: ${ancestors.message}")
            }
            else -> {}
        }
    }
}
