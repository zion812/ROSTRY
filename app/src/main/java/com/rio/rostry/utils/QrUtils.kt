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
     * Use this for general product identification.
     */
    fun productQrBitmap(id: String, size: Int = 512): Bitmap {
        val deepLink = "rostry://product/$id"
        return generateQr(deepLink, size)
    }

    /**
     * Generates a QR code bitmap for product lineage/family tree view.
     * The QR code contains a deep-link to `rostry://product/{id}/lineage` which navigates to the family tree view.
     * Use this for traceability and lineage visualization.
     */
    fun productLineageQrBitmap(id: String, size: Int = 512): Bitmap {
        val deepLink = "rostry://product/$id/lineage"
        return generateQr(deepLink, size)
    }
}
