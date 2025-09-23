package com.rio.rostry.utils.images

import android.content.Context
import android.net.Uri
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.default
import id.zelory.compressor.constraint.format
import id.zelory.compressor.constraint.quality
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

object ImageCompressor {
    // Compress an image Uri to a temporary JPEG with target size and quality for low-end devices
    suspend fun compressForUpload(context: Context, input: File, lowBandwidth: Boolean): File = withContext(Dispatchers.IO) {
        val quality = if (lowBandwidth) 60 else 80
        val maxWidth = if (lowBandwidth) 720 else 1080
        val maxHeight = if (lowBandwidth) 720 else 1080
        Compressor.compress(context, input) {
            default(width = maxWidth, height = maxHeight)
            quality(quality)
            format(android.graphics.Bitmap.CompressFormat.JPEG)
        }
    }
}
