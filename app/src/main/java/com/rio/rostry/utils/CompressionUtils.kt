package com.rio.rostry.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.ByteArrayOutputStream

object CompressionUtils {
    // Downscale and compress a JPEG/WEBP image byte array to target size (KB)
    fun compressImage(
        input: ByteArray,
        maxWidth: Int = 1280,
        maxHeight: Int = 1280,
        format: Bitmap.CompressFormat = Bitmap.CompressFormat.JPEG,
        qualityStart: Int = 90,
        targetKb: Int = 300
    ): ByteArray {
        val original = BitmapFactory.decodeByteArray(input, 0, input.size) ?: return input
        val ratio = minOf(maxWidth.toFloat() / original.width, maxHeight.toFloat() / original.height, 1f)
        val scaled = if (ratio < 1f) {
            Bitmap.createScaledBitmap(
                original,
                (original.width * ratio).toInt(),
                (original.height * ratio).toInt(),
                true
            )
        } else original

        var quality = qualityStart
        var result: ByteArray
        do {
            val baos = ByteArrayOutputStream()
            scaled.compress(format, quality, baos)
            result = baos.toByteArray()
            quality -= 10
        } while (result.size > targetKb * 1024 && quality > 30)

        if (scaled != original) scaled.recycle()
        return result
    }
}
