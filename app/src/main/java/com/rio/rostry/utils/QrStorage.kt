package com.rio.rostry.utils

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

object QrStorage {
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
}
