package com.rio.rostry.domain.usecase

import android.graphics.Bitmap
import com.rio.rostry.data.local.TransferRecordDao
import com.rio.rostry.data.model.TransferRecord
import com.rio.rostry.util.QRCodeGenerator
import java.util.*
import javax.inject.Inject

class GenerateTransferQRCodeUseCase @Inject constructor(
    private val transferRecordDao: TransferRecordDao,
    private val qrCodeGenerator: QRCodeGenerator
) {
    suspend operator fun invoke(
        poultryId: String,
        fromOwnerId: String,
        toOwnerId: String,
        transferReason: String,
        notes: String? = null
    ): Result<Bitmap> {
        return try {
            // Create transfer record
            val transferRecord = TransferRecord(
                id = UUID.randomUUID().toString(),
                poultryId = poultryId,
                fromOwnerId = fromOwnerId,
                toOwnerId = toOwnerId,
                transferDate = Date(),
                transferReason = transferReason,
                notes = notes,
                qrCode = null, // Will be set after generating QR code
                createdAt = Date(),
                updatedAt = Date()
            )
            
            // Insert transfer record
            transferRecordDao.insertTransferRecord(transferRecord)
            
            // Generate QR code with transfer record ID
            val qrCodeBitmap = qrCodeGenerator.generateQRCode(transferRecord.id)
            
            // Update transfer record with QR code
            val updatedTransferRecord = transferRecord.copy(qrCode = transferRecord.id)
            transferRecordDao.updateTransferRecord(updatedTransferRecord)
            
            Result.success(qrCodeBitmap)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}