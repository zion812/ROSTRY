package com.rio.rostry.utils.images

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.webkit.MimeTypeMap
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.default
import id.zelory.compressor.constraint.format
import id.zelory.compressor.constraint.quality
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import timber.log.Timber

// ============================================================
// FREE TIER MODE: Aggressive compression to stay within 5GB Storage limit
// To relax limits when upgrading to Blaze Plan, set this to false
// ============================================================
private const val FREE_TIER_MODE = true

// Free Tier limits (5GB storage total, target ~50KB per photo)
private const val FREE_TIER_MAX_DIMENSION = 1024
private const val FREE_TIER_MAX_QUALITY = 60
private const val FREE_TIER_TARGET_KB = 100

// Blaze Plan limits (relaxed)
private const val BLAZE_MAX_DIMENSION = 1440
private const val BLAZE_MAX_QUALITY = 80

object ImageCompressor {
    
    /**
     * Check if a file is a video (not allowed on Free Tier).
     * Returns true if the file appears to be a video based on MIME type or extension.
     */
    fun isVideoFile(context: Context, uri: Uri): Boolean {
        val mimeType = context.contentResolver.getType(uri)
        if (mimeType?.startsWith("video/") == true) return true
        
        val extension = MimeTypeMap.getFileExtensionFromUrl(uri.toString())?.lowercase()
        val videoExtensions = setOf("mp4", "mov", "avi", "mkv", "webm", "3gp", "m4v", "flv", "wmv")
        return extension in videoExtensions
    }
    
    /**
     * Check if a file is a video based on file path/extension.
     */
    fun isVideoFile(file: File): Boolean {
        val extension = file.extension.lowercase()
        val videoExtensions = setOf("mp4", "mov", "avi", "mkv", "webm", "3gp", "m4v", "flv", "wmv")
        return extension in videoExtensions
    }
    
    /**
     * Compress an image for upload. 
     * FREE TIER MODE: Enforces 1024x768 max, 60% quality to target ~50-100KB per photo.
     * Phase 1: Downsample large images using BitmapFactory to avoid OOM.
     * Phase 2: Use Compressor library for final optimization.
     */
    suspend fun compressForUpload(context: Context, input: File, lowBandwidth: Boolean): File = withContext(Dispatchers.IO) {
        // FREE TIER: Reject video files
        if (FREE_TIER_MODE && isVideoFile(input)) {
            Timber.w("ImageCompressor: Video upload rejected in Free Tier mode")
            throw IllegalArgumentException("Video uploads are disabled. Please upload photos only.")
        }
        
        // Optimization: If file is smaller than 50KB, skip compression
        val skipThreshold = if (FREE_TIER_MODE) 50 * 1024 else 100 * 1024
        if (input.length() < skipThreshold) {
            Timber.d("ImageCompressor: Skipping compression for small file (${input.length() / 1024} KB)")
            return@withContext input
        }

        // FREE TIER: Enforce strict limits
        val maxDimension = if (FREE_TIER_MODE) {
            FREE_TIER_MAX_DIMENSION
        } else if (lowBandwidth) {
            800
        } else {
            BLAZE_MAX_DIMENSION
        }
        
        val quality = if (FREE_TIER_MODE) {
            FREE_TIER_MAX_QUALITY
        } else if (lowBandwidth) {
            40 
        } else {
            BLAZE_MAX_QUALITY
        }

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
