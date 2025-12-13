package com.rio.rostry.utils
  
import android.graphics.Bitmap
import android.graphics.Color
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
  
object QrUtils {
    fun generateQr(content: String, size: Int = 512): Bitmap {
        val bitMatrix: BitMatrix = MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, size, size)
        val bmp = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        for (x in 0 until size) {
            for (y in 0 until size) {
                bmp.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
            }
        }
        return bmp
    }
  
    /**
     * Generates a QR code bitmap for general product view.
     * The QR code contains a deep-link to `rostry://product/{id}` which navigates to product details.
     * Optionally embeds a human-readable birdCode as a query parameter for easier local exchanges.
     * Use this for general product identification.
     */
    fun productQrBitmap(id: String, birdCode: String? = null, size: Int = 512): Bitmap {
        val deepLink = "rostry://product/$id"
        val payload = if (birdCode != null) "$deepLink?birdCode=$birdCode" else deepLink
        return generateQr(payload, size)
    }
  
    /**
     * Generates a QR code bitmap for product lineage/family tree view.
     * The QR code contains a deep-link to `rostry://traceability/{id}` (aligned with Routes.TRACEABILITY) which navigates to the traceability screen.
     * Optionally embeds a human-readable birdCode as a query parameter for easier local exchanges.
     * Use this for traceability and lineage visualization.
     */
    fun productLineageQrBitmap(id: String, birdCode: String? = null, size: Int = 512): Bitmap {
        val deepLink = "rostry://traceability/$id"
        val payload = if (birdCode != null) "$deepLink?birdCode=$birdCode" else deepLink
        return generateQr(payload, size)
    }
}
