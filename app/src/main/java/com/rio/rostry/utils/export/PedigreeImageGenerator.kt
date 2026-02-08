package com.rio.rostry.utils.export

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Environment
import android.provider.MediaStore
import com.rio.rostry.domain.pedigree.PedigreeTree
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Generates high-quality image certificates for Pedigree Trees.
 * Uses shared PedigreeRenderer for drawing content.
 */
@Singleton
class PedigreeImageGenerator @Inject constructor(
    private val context: Context
) {
    // High-res dimensions (approx A4 Landscape ratio)
    private val width = 2400
    private val height = 1700

    fun generateAndSaveImage(birdName: String, tree: PedigreeTree): String? {
        val fileName = "Pedigree_${birdName.replace(" ", "_")}_${System.currentTimeMillis()}.jpg"

        val contentValues = android.content.ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/ROSTRY/Pedigree")
        }

        return try {
            val uri = context.contentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues
            ) ?: throw IOException("Failed to create MediaStore entry")
            
            // Create Bitmap and Canvas
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            
            // Draw
            val renderer = PedigreeRenderer(canvas, width, height)
            renderer.draw(tree)
            
            // Save to stream
            context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
            }
            
            bitmap.recycle()
            fileName
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
