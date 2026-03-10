package com.rio.rostry.data.repository

import android.content.Context
import android.graphics.pdf.PdfDocument
import com.opencsv.CSVWriter
import com.rio.rostry.data.database.dao.AuditLogDao
import com.rio.rostry.data.database.dao.TransferDao
import com.rio.rostry.data.database.entity.AuditLogEntity
import com.rio.rostry.data.database.entity.TransferEntity
import com.rio.rostry.data.database.entity.TransferAnalyticsEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Transfer analytics data
 */
data class TransferAnalytics(
    val totalTransfers: Int,
    val completedTransfers: Int,
    val pendingTransfers: Int,
    val cancelledTransfers: Int,
    val totalValue: Double,
    val averageTransferValue: Double,
    val period: String,
    val transfersByStatus: Map<String, Int>,
    val transfersByType: Map<String, Int>
)

interface TransferRepository {
    fun getById(transferId: String): Flow<TransferEntity?>
    fun getFromUser(userId: String): Flow<List<TransferEntity>>
    fun getToUser(userId: String): Flow<List<TransferEntity>>
    suspend fun upsert(transfer: TransferEntity)
    suspend fun softDelete(transferId: String)
    fun observePendingCountForFarmer(userId: String): Flow<Int>
    fun observeAwaitingVerificationCountForFarmer(userId: String): Flow<Int>
    fun observeRecentActivity(userId: String): Flow<List<TransferEntity>>
    suspend fun getTransferStatusSummary(userId: String): Map<String, Int>
    suspend fun initiateEnthusiastTransfer(
        productId: String,
        fromUserId: String,
        toUserId: String,
        lineageSnapshotJson: String,
        healthSnapshotJson: String,
        transferCode: String
    ): TransferEntity
    
    // Transfer Analytics (Comment 4)
    suspend fun getTransferAnalytics(period: String): TransferAnalytics
    suspend fun generateTransferReportCsv(userId: String, startDate: Long, endDate: Long): File
    suspend fun generateTransferReportPdf(userId: String, startDate: Long, endDate: Long): File
}

@Singleton
class TransferRepositoryImpl @Inject constructor(
    private val dao: TransferDao,
    private val auditLogDao: AuditLogDao,
    private val context: Context
) : TransferRepository {

    override fun getById(transferId: String): Flow<TransferEntity?> = dao.getTransferById(transferId)

    override fun getFromUser(userId: String): Flow<List<TransferEntity>> = dao.getTransfersFromUser(userId)

    override fun getToUser(userId: String): Flow<List<TransferEntity>> = dao.getTransfersToUser(userId)

    override suspend fun upsert(transfer: TransferEntity) {
        val now = System.currentTimeMillis()
        dao.upsert(transfer.copy(updatedAt = now, lastModifiedAt = now, dirty = true))
    }

    override suspend fun softDelete(transferId: String) {
        val now = System.currentTimeMillis()
        dao.upsert(
            TransferEntity(
                transferId = transferId,
                fromUserId = null,
                toUserId = null,
                orderId = null,
                amount = 0.0,
                currency = "",
                type = "",
                status = "",
                transactionReference = null,
                notes = null,
                initiatedAt = now,
                completedAt = null,
                updatedAt = now,
                lastModifiedAt = now,
                isDeleted = true,
                deletedAt = now,
                dirty = true
            )
        )
    }

    override fun observePendingCountForFarmer(userId: String): Flow<Int> = dao.observePendingCountForFarmer(userId)

    override fun observeAwaitingVerificationCountForFarmer(userId: String): Flow<Int> = dao.observeAwaitingVerificationCountForFarmer(userId)

    override fun observeRecentActivity(userId: String): Flow<List<TransferEntity>> {
        val sevenDaysAgo = System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000L
        return dao.getUserTransfersBetween(userId, sevenDaysAgo, null)
    }

    override suspend fun getTransferStatusSummary(userId: String): Map<String, Int> {
        val transfers = dao.getUserTransfersBetween(userId, null, null).first()
        return transfers.groupBy { it.status }.mapValues { it.value.size }
    }

    override suspend fun initiateEnthusiastTransfer(
        productId: String,
        fromUserId: String,
        toUserId: String,
        lineageSnapshotJson: String,
        healthSnapshotJson: String,
        transferCode: String
    ): TransferEntity {
        val now = System.currentTimeMillis()
        // 15 minute timeout per requirements
        val expiresAt = now + (15 * 60 * 1000)
        
        val transfer = TransferEntity(
            transferId = java.util.UUID.randomUUID().toString(),
            productId = productId,
            fromUserId = fromUserId,
            toUserId = toUserId,
            amount = 0.0, // Enthusiast transfers are non-monetary in this scope
            type = "ENTHUSIAST_TRANSFER",
            status = "PENDING",
            transferCode = transferCode,
            transferCodeExpiresAt = expiresAt,
            transferType = "ENTHUSIAST_TRANSFER",
            lineageSnapshotJson = lineageSnapshotJson,
            healthSnapshotJson = healthSnapshotJson,
            initiatedAt = now,
            updatedAt = now,
            lastModifiedAt = now,
            dirty = true
        )
        dao.upsert(transfer)

        // Log initiation
        auditLogDao.insert(
            com.rio.rostry.data.database.entity.AuditLogEntity(
                logId = UUID.randomUUID().toString(),
                type = "TRANSFER",
                refId = transfer.transferId,
                action = "INITIATE_ENTHUSIAST_TRANSFER",
                actorUserId = fromUserId,
                detailsJson = "{\"productId\":\"$productId\",\"toUserId\":\"$toUserId\"}",
                createdAt = now
            )
        )

        return transfer
    }

    // ─── Transfer Analytics (Comment 4) ────────────────────────────────────

    override suspend fun getTransferAnalytics(period: String): TransferAnalytics {
        val now = System.currentTimeMillis()
        val periodStart = when (period) {
            "daily" -> now - (24 * 60 * 60 * 1000L)
            "weekly" -> now - (7 * 24 * 60 * 60 * 1000L)
            "monthly" -> now - (30 * 24 * 60 * 60 * 1000L)
            else -> now - (24 * 60 * 60 * 1000L) // Default to daily
        }

        val transfers = dao.getAllTransfersBetween(periodStart, now).first()

        val completed = transfers.count { it.status == "COMPLETED" }
        val pending = transfers.count { it.status == "PENDING" || it.status == "IN_PROGRESS" }
        val cancelled = transfers.count { it.status == "CANCELLED" }
        val totalValue = transfers.filter { it.status == "COMPLETED" }.sumOf { it.amount }

        return TransferAnalytics(
            totalTransfers = transfers.size,
            completedTransfers = completed,
            pendingTransfers = pending,
            cancelledTransfers = cancelled,
            totalValue = totalValue,
            averageTransferValue = if (completed > 0) totalValue / completed else 0.0,
            period = period,
            transfersByStatus = transfers.groupBy { it.status }.mapValues { it.value.size },
            transfersByType = transfers.groupBy { it.type ?: "UNKNOWN" }.mapValues { it.value.size }
        )
    }

    override suspend fun generateTransferReportCsv(
        userId: String,
        startDate: Long,
        endDate: Long
    ): File {
        val reportsDir = File(context.filesDir, "reports/transfer")
        if (!reportsDir.exists()) reportsDir.mkdirs()
        val file = File(reportsDir, "transfer_report_${System.currentTimeMillis()}.csv")

        val transfers = dao.getUserTransfersBetween(userId, startDate, endDate).first()

        FileOutputStream(file).use { fos ->
            OutputStreamWriter(fos).use { writer ->
                CSVWriter(writer).use { csvWriter ->
                    // Header
                    csvWriter.writeNext(
                        arrayOf(
                            "Transfer ID", "From User", "To User", "Amount",
                            "Status", "Type", "Date"
                        )
                    )

                    // Data rows
                    for (transfer in transfers) {
                        csvWriter.writeNext(
                            arrayOf(
                                transfer.transferId,
                                transfer.fromUserId ?: "",
                                transfer.toUserId ?: "",
                                transfer.amount.toString(),
                                transfer.status,
                                transfer.type ?: "UNKNOWN",
                                transfer.initiatedAt.toString()
                            )
                        )
                    }

                    // Summary
                    val totalValue = transfers.filter { it.status == "COMPLETED" }.sumOf { it.amount }
                    csvWriter.writeNext(emptyArray())
                    csvWriter.writeNext(arrayOf("Summary"))
                    csvWriter.writeNext(arrayOf("Total Transfers", transfers.size.toString()))
                    csvWriter.writeNext(arrayOf("Completed", transfers.count { it.status == "COMPLETED" }.toString()))
                    csvWriter.writeNext(arrayOf("Pending", transfers.count { it.status == "PENDING" }.toString()))
                    csvWriter.writeNext(arrayOf("Total Value", totalValue.toString()))
                }
            }
        }

        return file
    }

    override suspend fun generateTransferReportPdf(
        userId: String,
        startDate: Long,
        endDate: Long
    ): File {
        val reportsDir = File(context.filesDir, "reports/transfer")
        if (!reportsDir.exists()) reportsDir.mkdirs()
        val file = File(reportsDir, "transfer_report_${System.currentTimeMillis()}.pdf")

        val transfers = dao.getUserTransfersBetween(userId, startDate, endDate).first()
        val totalValue = transfers.filter { it.status == "COMPLETED" }.sumOf { it.amount }

        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas

        val paint = android.graphics.Paint().apply {
            color = android.graphics.Color.BLACK
            textSize = 12f
        }

        val boldPaint = android.graphics.Paint().apply {
            color = android.graphics.Color.BLACK
            textSize = 12f
            isFakeBoldText = true
        }

        val titlePaint = android.graphics.Paint().apply {
            color = android.graphics.Color.BLACK
            textSize = 18f
            isFakeBoldText = true
        }

        // Title
        canvas.drawText("Transfer Report", 40f, 40f, titlePaint)
        canvas.drawText("Period: $startDate - $endDate", 40f, 65f, paint)

        var yPosition = 100f
        canvas.drawText("Summary", 40f, yPosition, boldPaint)
        yPosition += 25f
        canvas.drawText("Total Transfers: ${transfers.size}", 40f, yPosition, paint)
        yPosition += 20f
        canvas.drawText("Total Value: $totalValue", 40f, yPosition, paint)
        yPosition += 40f

        // Table header
        canvas.drawText("Transfers", 40f, yPosition, boldPaint)
        yPosition += 25f
        canvas.drawText("ID", 40f, yPosition, boldPaint)
        canvas.drawText("Amount", 150f, yPosition, boldPaint)
        canvas.drawText("Status", 250f, yPosition, boldPaint)
        canvas.drawText("Date", 350f, yPosition, boldPaint)
        yPosition += 20f

        for (transfer in transfers) {
            if (yPosition > 780f) {
                pdfDocument.finishPage(page)
                pdfDocument.startPage(pageInfo)
                yPosition = 40f
            }
            canvas.drawText(transfer.transferId, 40f, yPosition, paint)
            canvas.drawText("${transfer.amount}", 150f, yPosition, paint)
            canvas.drawText(transfer.status, 250f, yPosition, paint)
            canvas.drawText("${transfer.initiatedAt}", 350f, yPosition, paint)
            yPosition += 20f
        }

        pdfDocument.finishPage(page)

        FileOutputStream(file).use { out ->
            pdfDocument.writeTo(out)
        }
        pdfDocument.close()

        return file
    }
}
