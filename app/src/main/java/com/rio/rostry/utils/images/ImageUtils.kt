package com.rio.rostry.utils.images

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.exifinterface.media.ExifInterface

object ImageUtils {
    fun loadThumbnail(context: Context, uri: Uri, maxW: Int = 512, maxH: Int = 512): Bitmap? {
        return try {
            context.contentResolver.openInputStream(uri)?.use { input ->
                val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
                BitmapFactory.decodeStream(input, null, options)
                val (w, h) = options.outWidth to options.outHeight
                val sample = computeInSampleSize(w, h, maxW, maxH)
                context.contentResolver.openInputStream(uri)?.use { input2 ->
                    val opts = BitmapFactory.Options().apply { inSampleSize = sample }
                    BitmapFactory.decodeStream(input2, null, opts)
                }
            }
        } catch (_: Exception) { null }
    }

    fun exifSummary(context: Context, uri: Uri): String? {
        return try {
            context.contentResolver.openInputStream(uri)?.use { input ->
                val exif = ExifInterface(input)
                val dt = exif.getAttribute(ExifInterface.TAG_DATETIME)
                val ll = exif.latLong
                val lat = ll?.getOrNull(0)
                val lng = ll?.getOrNull(1)
                buildString {
                    append("EXIF: ")
                    if (dt != null) append("Time=$dt ")
                    if (lat != null && lng != null) append("GPS=$lat,$lng")
                }.ifBlank { null }
            }
        } catch (_: Exception) { null }
    }

    private fun computeInSampleSize(width: Int, height: Int, reqWidth: Int, reqHeight: Int): Int {
        var inSampleSize = 1
        if (height > reqHeight || width > reqWidth) {
            val halfHeight = height / 2
            val halfWidth = width / 2
            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }
}
