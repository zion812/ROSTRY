package com.rio.rostry.utils

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

object QrStorage {
    /**
     * Saves a general product QR code bitmap to a file named "product_{productId}.png".
     * This QR code is used for general product views, linking to rostry://product/{productId}.
     * Use saveProductLineageQr for lineage-specific QR codes.
     */
    fun saveProductQr(context: Context, productId: String, bmp: Bitmap): Uri? {
        return try {
            val dir = File(context.filesDir, "qr")
            if (!dir.exists()) dir.mkdirs()
            val file = File(dir, "product_${productId}.png")
            FileOutputStream(file).use { out ->
                bmp.compress(Bitmap.CompressFormat.PNG, 100, out)
            }
            val authority = context.packageName + ".fileprovider"
            FileProvider.getUriForFile(context, authority, file)
        } catch (_: Throwable) {
            null
        }
    }
    
    /**
     * Saves a lineage-specific product QR code bitmap to a file named "product_lineage_{productId}.png".
     * This QR code is used for family tree views, linking to rostry://product/{productId}/lineage.
     * Use saveProductQr for general product QR codes.
     */
    fun saveProductLineageQr(context: Context, productId: String, bmp: Bitmap): Uri? {
        return try {
            val dir = File(context.filesDir, "qr")
            if (!dir.exists()) dir.mkdirs()
            val file = File(dir, "product_lineage_${productId}.png")
            FileOutputStream(file).use { out ->
                bmp.compress(Bitmap.CompressFormat.PNG, 100, out)
            }
            val authority = context.packageName + ".fileprovider"
            FileProvider.getUriForFile(context, authority, file)
        } catch (_: Throwable) {
            null
        }
    }
}
