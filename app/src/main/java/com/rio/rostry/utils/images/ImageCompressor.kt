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
        // Optimization: If file is smaller than 100KB, skip compression
        if (input.length() < 100 * 1024) {
            Timber.d("ImageCompressor: Skipping compression for small file (${input.length() / 1024} KB)")
            return@withContext input
        }

        // Target: 100KB for standard, 50KB for low bandwidth
        // We use Resolution + Quality to achieve this roughly without iterative loops which are slow
        val quality = if (lowBandwidth) 40 else 60
        val maxDimension = if (lowBandwidth) 800 else 1024

        // Phase 1: Pre-downsample
        val preScaledFile = preDownsampleIfNeeded(context, input, maxDimension * 2)
        val fileToCompress = preScaledFile ?: input

        // Phase 2: Compressor
        try {
            Compressor.compress(context, fileToCompress) {
                default(width = maxDimension, height = maxDimension)
                quality(quality)
                format(android.graphics.Bitmap.CompressFormat.JPEG)
                // Note: 'size(100_000)' could be used but is iterative/slow. 
                // Using tuned quality/resolution is faster for bulk operations.
            }
        } catch (e: Exception) {
            Timber.e(e, "Compression failed, falling back to original")
            input 
        } finally {
            if (preScaledFile != null && preScaledFile != input) {
                runCatching { preScaledFile.delete() }
            }
        }
    }

    /**
     * Aggressive compression for Daily Logs (Farmer usage).
     * Target: ~50KB per photo.
     * Strategy: 720p resolution, 50% quality.
     */
    suspend fun compressForDailyLog(context: Context, input: File): File = withContext(Dispatchers.IO) {
        if (input.length() < 75 * 1024) {
             return@withContext input
        }

        try {
            Compressor.compress(context, input) {
                default(width = 720, height = 720)
                quality(50)
                format(android.graphics.Bitmap.CompressFormat.JPEG)
            }
        } catch (e: Exception) {
            Timber.e(e, "Daily log compression failed, using original")
            input // Fallback
        }
    }

    /**
     * Enhanced compression strategy for large files (10-20MB).
     */
    suspend fun compressForLargeUpload(context: Context, input: File): File = withContext(Dispatchers.IO) {
        val sizeBytes = input.length()
        if (sizeBytes < 5 * 1024 * 1024) return@withContext input
        
        val sizeMB = sizeBytes / (1024 * 1024)
        val quality = if (sizeMB > 10) 70 else 80
        val maxDimension = 1440 // 1440p max for large uploads
        
        try {
            // Phase 1: Aggressive downsampling
            val preScaledFile = preDownsampleIfNeeded(context, input, maxDimension)
            val fileToCompress = preScaledFile ?: input

            // Phase 2: Final compression
            Compressor.compress(context, fileToCompress) {
                default(width = maxDimension, height = maxDimension)
                quality(quality)
                format(android.graphics.Bitmap.CompressFormat.JPEG)
            }
        } catch (e: Exception) {
            Timber.e(e, "Large file compression failed, using original")
            input
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
