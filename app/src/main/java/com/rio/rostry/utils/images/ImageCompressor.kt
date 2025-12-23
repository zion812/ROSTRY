package com.rio.rostry.utils.images

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.default
import id.zelory.compressor.constraint.format
import id.zelory.compressor.constraint.quality
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import timber.log.Timber

object ImageCompressor {
    /**
     * Compress an image for upload. 
     * Optimization: If file is already < 2MB, skip compression to save memory and CPU.
     * Phase 1: Downsample large images using BitmapFactory to avoid OOM.
     * Phase 2: Use Compressor library for final optimization.
     */
    suspend fun compressForUpload(context: Context, input: File, lowBandwidth: Boolean): File = withContext(Dispatchers.IO) {
        // Optimization: If file is smaller than 2MB, skip compression
        if (input.length() < 2 * 1024 * 1024) {
            Timber.d("ImageCompressor: Skipping compression for small file (${input.length() / 1024} KB)")
            return@withContext input
        }

        val quality = if (lowBandwidth) 60 else 80
        val maxDimension = if (lowBandwidth) 720 else 1080

        // Phase 1: Pre-downsample if the image is very large to prevent OOM
        val preScaledFile = preDownsampleIfNeeded(context, input, maxDimension * 2) // Allow 2x for Compressor to work with
        val fileToCompress = preScaledFile ?: input

        // Phase 2: Use Compressor for final optimization
        try {
            Compressor.compress(context, fileToCompress) {
                default(width = maxDimension, height = maxDimension)
                quality(quality)
                format(android.graphics.Bitmap.CompressFormat.JPEG)
            }
        } catch (e: Exception) {
            Timber.e(e, "Compression failed, falling back to original")
            input // Fallback to original if compression fails
        } finally {
            // Clean up pre-scaled temp file if we created one
            if (preScaledFile != null && preScaledFile != input) {
                runCatching { preScaledFile.delete() }
            }
        }
    }

    /**
     * Pre-downsample a large image using inSampleSize to avoid loading full resolution into memory.
     * Returns null if no downsampling was needed.
     */
    private fun preDownsampleIfNeeded(context: Context, input: File, targetMaxDimension: Int): File? {
        // First, just decode bounds (no memory allocation)
        val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeFile(input.absolutePath, options)
        
        val width = options.outWidth
        val height = options.outHeight
        
        // If the image is already small enough, don't bother
        if (width <= 0 || height <= 0 || (width <= targetMaxDimension && height <= targetMaxDimension)) {
            return null
        }

        // Calculate inSampleSize (power of 2 for efficiency)
        var sampleSize = 1
        while (width / sampleSize > targetMaxDimension || height / sampleSize > targetMaxDimension) {
            sampleSize *= 2
        }
        
        // If sampleSize is 1, no downsampling needed
        if (sampleSize <= 1) return null
        
        Timber.d("ImageCompressor: Pre-downsampling ${width}x${height} with sampleSize=$sampleSize")

        // Decode with downsampling
        val decodeOptions = BitmapFactory.Options().apply { inSampleSize = sampleSize }
        val bitmap = BitmapFactory.decodeFile(input.absolutePath, decodeOptions) ?: return null
        
        // Write to temp file
        val tempFile = File.createTempFile("prescaled_", ".jpg", context.cacheDir)
        try {
            FileOutputStream(tempFile).use { out ->
                bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 90, out)
            }
        } finally {
            bitmap.recycle()
        }
        
        return tempFile
    }
}
