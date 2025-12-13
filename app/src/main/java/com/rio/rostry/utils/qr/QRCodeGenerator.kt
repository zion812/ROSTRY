package com.rio.rostry.utils.qr

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * QR Code Generator for Transfer System (Phase 5.2)
 * Generates QR codes for secure product transfers
 */
@Singleton
class QRCodeGenerator @Inject constructor(
    @ApplicationContext private val context: Context
) {
    /**
     * Generate QR code for transfer verification
     * Format: "ROSTRY_TRANSFER:{transferId}:{verificationToken}:{timestamp}"
     */
    fun generateTransferQR(
        transferId: String,
        verificationToken: String,
        size: Int = 512
    ): Bitmap? {
        return try {
            val timestamp = System.currentTimeMillis()
            val data = "ROSTRY_TRANSFER:$transferId:$verificationToken:$timestamp"
            
            val hints = hashMapOf<EncodeHintType, Any>().apply {
                put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H)
                put(EncodeHintType.MARGIN, 2)
            }

            val qrCodeWriter = QRCodeWriter()
            val bitMatrix = qrCodeWriter.encode(data, BarcodeFormat.QR_CODE, size, size, hints)

            val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565)
            for (x in 0 until size) {
                for (y in 0 until size) {
                    bitmap.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
                }
            }
            bitmap
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Generate QR code for product tracking
     * Format: "ROSTRY_PRODUCT:{productId}:{birdCode}"
     */
    fun generateProductQR(
        productId: String,
        birdCode: String?,
        size: Int = 512
    ): Bitmap? {
        return try {
            val data = "ROSTRY_PRODUCT:$productId:${birdCode ?: "N/A"}"
            
            val hints = hashMapOf<EncodeHintType, Any>().apply {
                put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H)
                put(EncodeHintType.MARGIN, 2)
            }

            val qrCodeWriter = QRCodeWriter()
            val bitMatrix = qrCodeWriter.encode(data, BarcodeFormat.QR_CODE, size, size, hints)

            val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565)
            for (x in 0 until size) {
                for (y in 0 until size) {
                    bitmap.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
                }
            }
            bitmap
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}

/**
 * QR Code data parser
 */
object QRCodeParser {
    data class TransferQRData(
        val transferId: String,
        val verificationToken: String,
        val timestamp: Long
    )

    data class ProductQRData(
        val productId: String,
        val birdCode: String?
    )

    fun parseTransferQR(rawData: String): TransferQRData? {
        return try {
            val parts = rawData.split(":")
            if (parts.size >= 4 && parts[0] == "ROSTRY_TRANSFER") {
                TransferQRData(
                    transferId = parts[1],
                    verificationToken = parts[2],
                    timestamp = parts[3].toLongOrNull() ?: 0L
                )
            } else null
        } catch (e: Exception) {
            null
        }
    }

    fun parseProductQR(rawData: String): ProductQRData? {
        return try {
            val parts = rawData.split(":")
            if (parts.size >= 3 && parts[0] == "ROSTRY_PRODUCT") {
                ProductQRData(
                    productId = parts[1],
                    birdCode = parts[2].takeIf { it != "N/A" }
                )
            } else null
        } catch (e: Exception) {
            null
        }
    }
}
