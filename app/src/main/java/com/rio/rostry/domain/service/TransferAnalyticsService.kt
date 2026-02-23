package com.rio.rostry.domain.service

import com.rio.rostry.data.database.dao.TransferDao
import com.rio.rostry.data.database.dao.AuditLogDao
import com.rio.rostry.domain.error.ErrorHandler
import kotlinx.coroutines.flow.firstOrNull
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Transfer analytics service tracking transfer metrics and generating reports.
 */
@Singleton
class TransferAnalyticsService @Inject constructor(
    private val transferDao: TransferDao,
    private val auditLogDao: AuditLogDao,
    private val errorHandler: ErrorHandler
) {

    data class TransferMetrics(
        val totalTransfers: Int,
        val completedTransfers: Int,
        val pendingTransfers: Int,
        val averageCompletionTimeMs: Long,
        val periodStart: Long,
        val periodEnd: Long
    )

    /**
     * Get transfer metrics for a user over a period.
     */
    suspend fun getMetrics(
        userId: String,
        periodStart: Long,
        periodEnd: Long
    ): Result<TransferMetrics> {
        return try {
            val fromTransfers = transferDao.getTransfersFromUser(userId).firstOrNull() ?: emptyList()
            val toTransfers = transferDao.getTransfersToUser(userId).firstOrNull() ?: emptyList()
            val transfers = fromTransfers + toTransfers
            
            val periodTransfers = transfers.filter {
                it.initiatedAt in periodStart..periodEnd
            }

            val completed = periodTransfers.filter { it.status == "COMPLETED" }
            val pending = periodTransfers.filter { it.status == "PENDING" }

            val avgTime = if (completed.isNotEmpty()) {
                completed.mapNotNull { t ->
                    val completedAt = t.completedAt ?: t.updatedAt
                    val createdAt = t.initiatedAt
                    if (completedAt > createdAt) completedAt - createdAt else null
                }.average().toLong()
            } else 0L

            Result.success(
                TransferMetrics(
                    totalTransfers = periodTransfers.size,
                    completedTransfers = completed.size,
                    pendingTransfers = pending.size,
                    averageCompletionTimeMs = avgTime,
                    periodStart = periodStart,
                    periodEnd = periodEnd
                )
            )
        } catch (e: Exception) {
            errorHandler.handle(e, "TransferAnalyticsService.getMetrics")
            Result.failure(e)
        }
    }
}
