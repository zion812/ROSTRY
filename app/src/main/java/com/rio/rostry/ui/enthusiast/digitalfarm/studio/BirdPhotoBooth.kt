package com.rio.rostry.ui.enthusiast.digitalfarm.studio

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import com.rio.rostry.domain.model.BirdAppearance
import com.rio.rostry.domain.model.StudioBackground
import com.rio.rostry.ui.enthusiast.digitalfarm.BirdPartRenderer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.OutputStream

/**
 * Utility to render high-resolution photos of the bird and save them to the gallery.
 */
object BirdPhotoBooth {

    private const val PHOTO_WIDTH = 2048
    private const val PHOTO_HEIGHT = 2048

    suspend fun captureAndSave(
        context: Context,
        appearance: BirdAppearance,
        filename: String,
        rotation: Float = 0f
    ): Uri? = withContext(Dispatchers.IO) {
        // 1. Create Bitmap
        val imageBitmap = ImageBitmap(PHOTO_WIDTH, PHOTO_HEIGHT)
        val canvas = Canvas(imageBitmap)
        val density = Density(context)
        val layoutDirection = LayoutDirection.Ltr
        
        // 2. Render to Canvas
        val drawScope = CanvasDrawScope()
        drawScope.draw(density, layoutDirection, canvas, Size(PHOTO_WIDTH.toFloat(), PHOTO_HEIGHT.toFloat())) {
            // Background
            val bgBrush = when (appearance.background) {
                StudioBackground.STUDIO -> androidx.compose.ui.graphics.Brush.radialGradient(
                    colors = listOf(Color(0xFFE0E0E0), Color(0xFFB0B0B0)),
                    center = Offset(PHOTO_WIDTH / 2f, PHOTO_HEIGHT / 2f),
                    radius = PHOTO_WIDTH.toFloat()
                )
                StudioBackground.BARN -> androidx.compose.ui.graphics.Brush.verticalGradient(
                    colors = listOf(Color.DarkGray, Color(0xFF3E2723), Color(0xFF5D4037)),
                    startY = 0f,
                    endY = PHOTO_HEIGHT.toFloat()
                )
                StudioBackground.OUTDOORS -> androidx.compose.ui.graphics.Brush.verticalGradient(
                    colors = listOf(Color(0xFF81D4FA), Color(0xFF81C784)),
                    startY = 0f,
                    endY = PHOTO_HEIGHT.toFloat()
                )
                StudioBackground.STAGE -> androidx.compose.ui.graphics.Brush.radialGradient(
                    colors = listOf(Color(0xFFFFF9C4).copy(alpha = 0.6f), Color.Black),
                    center = Offset(PHOTO_WIDTH / 2f, PHOTO_HEIGHT * 0.2f),
                    radius = PHOTO_WIDTH.toFloat()
                )
            }
            drawRect(brush = bgBrush, size = Size(PHOTO_WIDTH.toFloat(), PHOTO_HEIGHT.toFloat()))

            // Draw Bird
            // Center and Scale Up
            // Base scale in Renderer is relative to screen density roughly, 
            // but here we have fixed pixels. 
            // Let's assume standard bird height is ~300-400 units.
            // We want it to fill ~80% of 2048px.
            
            val scaleFactor = 10.0f // Match viewport zoom for consistent look
            val cx = PHOTO_WIDTH / 2f
            val cy = PHOTO_HEIGHT * 0.55f // Slightly below center so bird body fills frame
            
            translate(cx, cy) {
                scale(scaleFactor) {
                     with(BirdPartRenderer) {
                        drawBirdFromAppearance(
                            x = 0f, 
                            y = 0f, 
                            appearance = appearance, 
                            isSelected = false, 
                            animTime = 0f, // Static pose 
                            bobOffset = 0f,
                            rotation = rotation
                        )
                    }
                }
            }
            
            // Watermark
            // nativeCanvas.drawText... (Skipping for now to keep simple)
        }

        // 3. Save to MediaStore
        saveBitmapToGallery(context, imageBitmap.asAndroidBitmap(), filename)
    }

    private fun saveBitmapToGallery(context: Context, bitmap: Bitmap, filename: String): Uri? {
        val resolver = context.contentResolver
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "$filename.png")
            put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/BirdStudio")
                put(MediaStore.MediaColumns.IS_PENDING, 1)
            }
        }

        val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues) ?: return null

        return try {
            resolver.openOutputStream(uri)?.use { stream ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            }
            
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                contentValues.clear()
                contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
                resolver.update(uri, contentValues, null, null)
            }
            uri
        } catch (e: Exception) {
            e.printStackTrace()
            // Cleanup if failed?
            resolver.delete(uri, null, null)
            null
        }
    }
}
