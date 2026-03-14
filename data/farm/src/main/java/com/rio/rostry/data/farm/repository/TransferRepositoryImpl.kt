package com.rio.rostry.data.farm.repository

import android.content.Context
import android.graphics.pdf.PdfDocument
import com.google.firebase.firestore.FirebaseFirestore
import com.opencsv.CSVWriter
import com.rio.rostry.data.database.dao.TransferDao
import com.rio.rostry.data.database.entity.AuditLogEntity
import com.rio.rostry.data.database.entity.TransferEntity
import com.rio.rostry.core.model.Result
import com.rio.rostry.core.model.Transfer
import com.rio.rostry.core.model.TransferAnalytics
import com.rio.rostry.domain.farm.repository.AuditLogRepository
import com.rio.rostry.domain.farm.repository.TransferRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.Job
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
import timber.log.Timber

/**
 * Implementation of TransferRepository using Room database and Firebase Firestore.
 *
 * Handles transfer lifecycle, analytics, and reporting for farm asset ownership changes.
 * 
 * Note: This implementation works with both domain models (Transfer) and database entities
 * (TransferEntity). Conversion happens at the repository boundary.
 */
@Singleton
class TransferRepositoryImpl @Inject constructor(
    private val dao: TransferDao,
    private val firestore: FirebaseFirestore,
    private val auditLogRepository: AuditLogRepository,
    @ApplicationContext private val context: Context
) : TransferRepository {

    private val transfersCollection = firestore.collection("transfers")

    // ═══════════════════════════════════════════════════════════════════
    // DATABASE OPERATIONS (Room)
    // ═══════════════════════════════════════════════════════════════════

    override fun getById(transferId: String): Flow<Transfer?> = callbackFlow {
        val observer = dao.getTransferById(transferId)
        val subscription = observer.collect { entity ->
            trySend(entity?.toDomainModel())
        }
        awaitClose { }
    }

    override fun getFromUser(userId: String): Flow<List<Transfer>> = callbackFlow {
        val observer = dao.getTransfersFromUser(userId)
        val subscription = observer.collect { entities ->
            trySend(entities.map { it.toDomainModel() })
        }
        awaitClose { }
    }

    override fun getToUser(userId: String): Flow<List<Transfer>> = callbackFlow {
        val observer = dao.getTransfersToUser(userId)
        val subscription = observer.collect { entities ->
            trySend(entities.map { it.toDomainModel() })
        }
        awaitClose { }
    }

    override suspend fun upsert(transfer: Transfer): Result<Unit> {
        return try {
            val now = System.currentTimeMillis()
            val entity = transfer.toEntity().copy(
                updatedAt = now,
                lastModifiedAt = now,
                dirty = true
            )
            dao.upsert(entity)
            
            // Sync to Firestore
            syncToFirestore(transfer)
            
            Result.Success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Failed to upsert transfer: ${transfer.transferId}")
            Result.Error(e)
        }
    }

    override suspend fun softDelete(transferId: String): Result<Unit> {
        return try {
            val now = System.currentTimeMillis()
            val existingTransfer = dao.getTransferById(transferId).first()
            
            if (existingTransfer != null) {
                dao.upsert(
                    existingTransfer.copy(
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
            
            Result.Success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Failed to soft delete transfer: $transferId")
            Result.Error(e)
        }
    }

    override fun observePendingCountForFarmer(userId: String): Flow<Int> = callbackFlow {
        val observer = dao.observePendingCountForFarmer(userId)
        val subscription = observer.collect { count ->
            trySend(count)
        }
        awaitClose { }
    }

    override fun observeAwaitingVerificationCountForFarmer(userId: String): Flow<Int> = callbackFlow {
        val observer = dao.observeAwaitingVerificationCountForFarmer(userId)
        val subscription = observer.collect { count ->
            trySend(count)
        }
        awaitClose { }
    }

    override fun observeRecentActivity(userId: String): Flow<List<Transfer>> = callbackFlow {
        val sevenDaysAgo = System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000L
        val observer = dao.getUserTransfersBetween(userId, sevenDaysAgo, null)
        val subscription = observer.collect { entities ->
            trySend(entities.map { it.toDomainModel() })
        }
        awaitClose { }
    }

    override suspend fun getTransferStatusSummary(userId: String): Result<Map<String, Int>> {
        return try {
            val transfers = dao.getUserTransfersBetween(userId, null, null).first()
            val summary = transfers.groupBy { it.status }.mapValues { it.value.size }
            Result.Success(summary)
        } catch (e: Exception) {
            Timber.e(e, "Failed to get transfer status summary")
            Result.Error(e)
        }
    }

    // ═══════════════════════════════════════════════════════════════════
    // ENTHUSIAST TRANSFER
    // ═══════════════════════════════════════════════════════════════════

    override suspend fun initiateEnthusiastTransfer(
        productId: String,
        fromUserId: String,
        toUserId: String,
        lineageSnapshotJson: String,
        healthSnapshotJson: String,
        transferCode: String
    ): Result<Transfer> {
        return try {
            val now = System.currentTimeMillis()
            // 15 minute timeout per requirements
            val expiresAt = now + (15 * 60 * 1000)

            val transfer = Transfer(
                transferId = UUID.randomUUID().toString(),
                productId = productId,
                fromUserId = fromUserId,
                toUserId = toUserId,
                orderId = null,
                amount = 0.0, // Enthusiast transfers are non-monetary in this scope
                currency = "INR",
                type = "ENTHUSIAST_TRANSFER",
                status = "PENDING",
                transferCode = transferCode,
                transferCodeExpiresAt = expiresAt,
                transferType = "ENTHUSIAST_TRANSFER",
                lineageSnapshotJson = lineageSnapshotJson,
                healthSnapshotJson = healthSnapshotJson,
                sellerPhotoUrl = null,
                buyerPhotoUrl = null,
                gpsLat = null,
                gpsLng = null,
                timeoutAt = null,
                conditionsJson = null,
                transactionReference = null,
                notes = null,
                initiatedAt = now,
                completedAt = null,
                updatedAt = now,
                lastModifiedAt = now,
                isDeleted = false,
                deletedAt = null,
                dirty = true
            )
            
            // Save to database
            val entity = transfer.toEntity()
            dao.upsert(entity)

            // Log initiation via AuditLogRepository
            auditLogRepository.insert(
                com.rio.rostry.core.model.AuditLog(
                    logId = UUID.randomUUID().toString(),
                    type = "TRANSFER",
                    refId = transfer.transferId,
                    action = "INITIATE_ENTHUSIAST_TRANSFER",
                    actorUserId = fromUserId,
                    detailsJson = """{"productId":"$productId","toUserId":"$toUserId"}""",
                    createdAt = System.currentTimeMillis()
                )
            )

            // Sync to Firestore
            syncToFirestore(transfer)

            Timber.i("Initiated enthusiast transfer: ${transfer.transferId}")
            Result.Success(transfer)
        } catch (e: Exception) {
            Timber.e(e, "Failed to initiate enthusiast transfer")
            Result.Error(e)
        }
    }

    // ═══════════════════════════════════════════════════════════════════
    // ANALYTICS & REPORTING
    // ═══════════════════════════════════════════════════════════════════

    override suspend fun getTransferAnalytics(period: String): Result<TransferAnalytics> {
        return try {
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

            val analytics = TransferAnalytics(
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
            
            Result.Success(analytics)
        } catch (e: Exception) {
            Timber.e(e, "Failed to get transfer analytics")
            Result.Error(e)
        }
    }

    override suspend fun generateTransferReportCsv(
        userId: String,
        startDate: Long,
        endDate: Long
    ): Result<File> {
        return try {
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

            Timber.i("Generated CSV transfer report: ${file.absolutePath}")
            Result.Success(file)
        } catch (e: Exception) {
            Timber.e(e, "Failed to generate CSV transfer report")
            Result.Error(e)
        }
    }

    override suspend fun generateTransferReportPdf(
        userId: String,
        startDate: Long,
        endDate: Long
    ): Result<File> {
        return try {
            val reportsDir = File(context.filesDir, "reports/transfer")
            if (!reportsDir.exists()) reportsDir.mkdirs()
            val file = File(reportsDir, "transfer_report_${System.currentTimeMillis()}.pdf")

            val transfers = dao.getUserTransfersBetween(userId, startDate, endDate).first()
            val totalValue = transfers.filter { it.status == "COMPLETED" }.sumOf { it.amount }

            val pdfDocument = PdfDocument()
            val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 size
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
            canvas.drawText("Period: ${formatDate(startDate)} - ${formatDate(endDate)}", 40f, 65f, paint)

            var yPosition = 100f
            canvas.drawText("Summary", 40f, yPosition, boldPaint)
            yPosition += 25f
            canvas.drawText("Total Transfers: ${transfers.size}", 40f, yPosition, paint)
            yPosition += 20f
            canvas.drawText("Total Value: ₹$totalValue", 40f, yPosition, paint)
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
                    val newPage = pdfDocument.startPage(pageInfo)
                    yPosition = 40f
                }
                canvas.drawText(transfer.transferId, 40f, yPosition, paint)
                canvas.drawText("₹${transfer.amount}", 150f, yPosition, paint)
                canvas.drawText(transfer.status, 250f, yPosition, paint)
                canvas.drawText(formatDate(transfer.initiatedAt), 350f, yPosition, paint)
                yPosition += 20f
            }

            pdfDocument.finishPage(page)

            FileOutputStream(file).use { out ->
                pdfDocument.writeTo(out)
            }
            pdfDocument.close()

            Timber.i("Generated PDF transfer report: ${file.absolutePath}")
            Result.Success(file)
        } catch (e: Exception) {
            Timber.e(e, "Failed to generate PDF transfer report")
            Result.Error(e)
        }
    }

    // ═══════════════════════════════════════════════════════════════════
    // FIREBASE SYNC
    // ═══════════════════════════════════════════════════════════════════

    /**
     * Syncs transfer to Firebase Firestore for cross-device availability.
     */
    private suspend fun syncToFirestore(transfer: Transfer) {
        try {
            transfersCollection.document(transfer.transferId).set(transfer).await()
            Timber.d("Synced transfer to Firestore: ${transfer.transferId}")
        } catch (e: Exception) {
            Timber.w(e, "Failed to sync transfer to Firestore")
            // Don't fail the operation - local database is source of truth
        }
    }

    /**
     * Formats timestamp to readable date string.
     */
    private fun formatDate(timestamp: Long): String {
        val sdf = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
        return sdf.format(java.util.Date(timestamp))
    }
}

// ═══════════════════════════════════════════════════════════════════
// MODEL MAPPING EXTENSIONS
// ═══════════════════════════════════════════════════════════════════

/**
 * Converts database entity to domain model.
 */
private fun TransferEntity.toDomainModel(): Transfer {
    return Transfer(
        transferId = this.transferId,
        productId = this.productId,
        fromUserId = this.fromUserId,
        toUserId = this.toUserId,
        orderId = this.orderId,
        amount = this.amount,
        currency = this.currency,
        type = this.type ?: "",
        status = this.status,
        transferCode = this.transferCode,
        transferCodeExpiresAt = this.transferCodeExpiresAt,
        transferType = this.transferType,
        lineageSnapshotJson = this.lineageSnapshotJson,
        healthSnapshotJson = this.healthSnapshotJson,
        sellerPhotoUrl = this.sellerPhotoUrl,
        buyerPhotoUrl = this.buyerPhotoUrl,
        gpsLat = this.gpsLat,
        gpsLng = this.gpsLng,
        timeoutAt = this.timeoutAt,
        conditionsJson = this.conditionsJson,
        transactionReference = this.transactionReference,
        notes = this.notes,
        initiatedAt = this.initiatedAt,
        completedAt = this.completedAt,
        updatedAt = this.updatedAt,
        lastModifiedAt = this.lastModifiedAt,
        isDeleted = this.isDeleted,
        deletedAt = this.deletedAt,
        dirty = this.dirty
    )
}

/**
 * Converts domain model to database entity.
 */
private fun Transfer.toEntity(): TransferEntity {
    return TransferEntity(
        transferId = this.transferId,
        productId = this.productId,
        fromUserId = this.fromUserId,
        toUserId = this.toUserId,
        orderId = this.orderId,
        amount = this.amount,
        currency = this.currency,
        type = this.type.ifEmpty { "TRANSFER" },
        status = this.status,
        transferCode = this.transferCode,
        transferCodeExpiresAt = this.transferCodeExpiresAt,
        transferType = this.transferType ?: "STANDARD",
        lineageSnapshotJson = this.lineageSnapshotJson,
        healthSnapshotJson = this.healthSnapshotJson,
        sellerPhotoUrl = this.sellerPhotoUrl,
        buyerPhotoUrl = this.buyerPhotoUrl,
        gpsLat = this.gpsLat,
        gpsLng = this.gpsLng,
        timeoutAt = this.timeoutAt,
        conditionsJson = this.conditionsJson,
        transactionReference = this.transactionReference,
        notes = this.notes,
        initiatedAt = this.initiatedAt,
        completedAt = this.completedAt,
        updatedAt = this.updatedAt,
        lastModifiedAt = this.lastModifiedAt,
        isDeleted = this.isDeleted,
        deletedAt = this.deletedAt,
        dirty = this.dirty
    )
}
