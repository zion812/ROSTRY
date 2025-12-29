package com.rio.rostry.domain.showcase

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.Typeface
import android.net.Uri
import androidx.core.content.FileProvider
import com.rio.rostry.data.database.entity.ProductEntity
import com.rio.rostry.domain.pedigree.PedigreeCompleteness
import com.rio.rostry.domain.pedigree.PedigreeRepository
import com.rio.rostry.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import javax.inject.Inject
import javax.inject.Singleton

/**
 * ShowcaseCardGenerator - Creates shareable showcase images for Enthusiast birds.
 * 
 * Generates a premium-looking card with:
 * - Bird photo (circular)
 * - Name and breed
 * - Pedigree completeness badge
 * - Vaccination status badge
 * - ROSTRY branding
 * 
 * Cards can be shared to WhatsApp, Instagram, etc.
 */
@Singleton
class ShowcaseCardGenerator @Inject constructor(
    private val pedigreeRepository: PedigreeRepository
) {
    companion object {
        private const val CARD_WIDTH = 1080
        private const val CARD_HEIGHT = 1920
        private const val PHOTO_SIZE = 400
        private const val CORNER_RADIUS = 60f
    }
    
    /**
     * Generate a showcase card bitmap for a bird.
     */
    suspend fun generateCard(
        context: Context,
        bird: ProductEntity,
        vaccinationCount: Int = 0
    ): Resource<ShowcaseCard> {
        return withContext(Dispatchers.IO) {
            try {
                // Get pedigree completeness
                val completeness = when (val result = pedigreeRepository.getPedigreeCompleteness(bird.productId)) {
                    is Resource.Success -> result.data ?: PedigreeCompleteness.NONE
                    else -> PedigreeCompleteness.NONE
                }
                
                // Create bitmap
                val bitmap = Bitmap.createBitmap(CARD_WIDTH, CARD_HEIGHT, Bitmap.Config.ARGB_8888)
                val canvas = Canvas(bitmap)
                
                // Draw card
                drawBackground(canvas)
                drawBirdPhoto(canvas, bird, context)
                drawBirdInfo(canvas, bird)
                drawBadges(canvas, completeness, vaccinationCount)
                drawBranding(canvas)
                
                // Save to cache
                val file = saveToCacheFile(context, bitmap, bird.productId)
                
                Resource.Success(ShowcaseCard(
                    bitmap = bitmap,
                    file = file,
                    bird = bird,
                    completeness = completeness
                ))
            } catch (e: Exception) {
                Timber.e(e, "Failed to generate showcase card for ${bird.productId}")
                Resource.Error(e.message ?: "Failed to generate card")
            }
        }
    }
    
    private fun drawBackground(canvas: Canvas) {
        val paint = Paint().apply {
            shader = LinearGradient(
                0f, 0f, 0f, CARD_HEIGHT.toFloat(),
                intArrayOf(
                    Color.parseColor("#1a1a2e"),
                    Color.parseColor("#16213e"),
                    Color.parseColor("#0f3460")
                ),
                null,
                Shader.TileMode.CLAMP
            )
        }
        
        val rect = RectF(0f, 0f, CARD_WIDTH.toFloat(), CARD_HEIGHT.toFloat())
        canvas.drawRoundRect(rect, CORNER_RADIUS, CORNER_RADIUS, paint)
        
        // Add subtle decorative elements
        drawDecorativeElements(canvas)
    }
    
    private fun drawDecorativeElements(canvas: Canvas) {
        val paint = Paint().apply {
            color = Color.parseColor("#FFD700")
            alpha = 20
            style = Paint.Style.STROKE
            strokeWidth = 3f
        }
        
        // Top decorative arc
        canvas.drawArc(
            RectF(-200f, -200f, 400f, 400f),
            0f, 180f, false, paint
        )
        
        // Bottom decorative arc
        canvas.drawArc(
            RectF(CARD_WIDTH - 200f, CARD_HEIGHT - 200f, CARD_WIDTH + 200f, CARD_HEIGHT + 200f),
            180f, 180f, false, paint
        )
    }
    
    private suspend fun drawBirdPhoto(canvas: Canvas, bird: ProductEntity, context: Context) {
        val centerX = CARD_WIDTH / 2f
        val photoY = 350f
        
        // Draw glowing ring behind photo
        val ringPaint = Paint().apply {
            shader = LinearGradient(
                centerX - PHOTO_SIZE / 2, photoY,
                centerX + PHOTO_SIZE / 2, photoY + PHOTO_SIZE,
                intArrayOf(
                    Color.parseColor("#FFD700"),
                    Color.parseColor("#FFA500")
                ),
                null,
                Shader.TileMode.CLAMP
            )
            style = Paint.Style.STROKE
            strokeWidth = 8f
            setShadowLayer(20f, 0f, 0f, Color.parseColor("#FFD700"))
        }
        canvas.drawCircle(centerX, photoY + PHOTO_SIZE / 2, PHOTO_SIZE / 2f + 10, ringPaint)
        
        // Load and draw bird photo
        val photoBitmap = loadBirdPhoto(bird, context)
        if (photoBitmap != null) {
            val circularPhoto = getCircularBitmap(photoBitmap, PHOTO_SIZE)
            canvas.drawBitmap(
                circularPhoto,
                centerX - PHOTO_SIZE / 2,
                photoY,
                null
            )
        } else {
            // Draw placeholder
            val placeholderPaint = Paint().apply {
                color = Color.parseColor("#2a2a4a")
            }
            canvas.drawCircle(centerX, photoY + PHOTO_SIZE / 2, PHOTO_SIZE / 2f, placeholderPaint)
            
            // Draw bird icon placeholder
            val textPaint = Paint().apply {
                color = Color.WHITE
                textSize = 120f
                textAlign = Paint.Align.CENTER
            }
            canvas.drawText("🐔", centerX, photoY + PHOTO_SIZE / 2 + 40, textPaint)
        }
    }
    
    private suspend fun loadBirdPhoto(bird: ProductEntity, context: Context): Bitmap? {
        val imageUrl = bird.imageUrls.firstOrNull() ?: return null
        
        return try {
            withContext(Dispatchers.IO) {
                val url = URL(imageUrl)
                val connection = url.openConnection()
                connection.connectTimeout = 5000
                connection.readTimeout = 5000
                val input = connection.getInputStream()
                BitmapFactory.decodeStream(input)
            }
        } catch (e: Exception) {
            Timber.w(e, "Failed to load bird photo: $imageUrl")
            null
        }
    }
    
    private fun getCircularBitmap(bitmap: Bitmap, size: Int): Bitmap {
        val scaled = Bitmap.createScaledBitmap(bitmap, size, size, true)
        val output = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)
        
        val paint = Paint().apply {
            isAntiAlias = true
        }
        
        canvas.drawCircle(size / 2f, size / 2f, size / 2f, paint)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(scaled, 0f, 0f, paint)
        
        return output
    }
    
    private fun drawBirdInfo(canvas: Canvas, bird: ProductEntity) {
        val centerX = CARD_WIDTH / 2f
        val nameY = 850f
        
        // Bird name
        val namePaint = Paint().apply {
            color = Color.WHITE
            textSize = 72f
            textAlign = Paint.Align.CENTER
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }
        canvas.drawText(bird.name, centerX, nameY, namePaint)
        
        // Breed
        bird.breed?.let { breed ->
            val breedPaint = Paint().apply {
                color = Color.parseColor("#B0B0B0")
                textSize = 42f
                textAlign = Paint.Align.CENTER
                isAntiAlias = true
            }
            canvas.drawText(breed, centerX, nameY + 60, breedPaint)
        }
        
        // Color/variety if available
        bird.color?.let { color ->
            val colorPaint = Paint().apply {
                this.color = Color.parseColor("#808080")
                textSize = 36f
                textAlign = Paint.Align.CENTER
                isAntiAlias = true
            }
            canvas.drawText(color, centerX, nameY + 110, colorPaint)
        }
    }
    
    private fun drawBadges(canvas: Canvas, completeness: PedigreeCompleteness, vaccinationCount: Int) {
        val centerX = CARD_WIDTH / 2f
        val badgeY = 1100f
        val badgeSpacing = 320f
        
        // Pedigree badge
        drawBadge(
            canvas,
            centerX - badgeSpacing / 2,
            badgeY,
            "🌳",
            completeness.label,
            Color.parseColor(
                when (completeness) {
                    PedigreeCompleteness.THREE_GEN -> "#FFD700"
                    PedigreeCompleteness.TWO_GEN -> "#4CAF50"
                    PedigreeCompleteness.ONE_GEN -> "#FF9800"
                    PedigreeCompleteness.NONE -> "#9E9E9E"
                }
            )
        )
        
        // Vaccination badge
        val vaccineStatus = when {
            vaccinationCount >= 3 -> "Fully Vaccinated"
            vaccinationCount > 0 -> "$vaccinationCount Vaccines"
            else -> "Not Vaccinated"
        }
        drawBadge(
            canvas,
            centerX + badgeSpacing / 2,
            badgeY,
            "💉",
            vaccineStatus,
            if (vaccinationCount >= 3) Color.parseColor("#4CAF50") else Color.parseColor("#FF9800")
        )
    }
    
    private fun drawBadge(canvas: Canvas, x: Float, y: Float, icon: String, text: String, accentColor: Int) {
        val badgeWidth = 280f
        val badgeHeight = 120f
        
        // Badge background
        val bgPaint = Paint().apply {
            color = Color.parseColor("#1e1e3f")
            isAntiAlias = true
        }
        val rect = RectF(x - badgeWidth / 2, y, x + badgeWidth / 2, y + badgeHeight)
        canvas.drawRoundRect(rect, 20f, 20f, bgPaint)
        
        // Badge border
        val borderPaint = Paint().apply {
            color = accentColor
            style = Paint.Style.STROKE
            strokeWidth = 3f
            isAntiAlias = true
        }
        canvas.drawRoundRect(rect, 20f, 20f, borderPaint)
        
        // Icon
        val iconPaint = Paint().apply {
            textSize = 40f
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }
        canvas.drawText(icon, x, y + 45, iconPaint)
        
        // Text
        val textPaint = Paint().apply {
            color = Color.WHITE
            textSize = 28f
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }
        canvas.drawText(text, x, y + 90, textPaint)
    }
    
    private fun drawBranding(canvas: Canvas) {
        val centerX = CARD_WIDTH / 2f
        val brandY = CARD_HEIGHT - 150f
        
        // ROSTRY logo text
        val logoPaint = Paint().apply {
            color = Color.parseColor("#FFD700")
            textSize = 64f
            textAlign = Paint.Align.CENTER
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }
        canvas.drawText("ROSTRY", centerX, brandY, logoPaint)
        
        // Tagline
        val taglinePaint = Paint().apply {
            color = Color.parseColor("#808080")
            textSize = 28f
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }
        canvas.drawText("Premium Poultry Management", centerX, brandY + 40, taglinePaint)
    }
    
    private fun saveToCacheFile(context: Context, bitmap: Bitmap, productId: String): File {
        val cacheDir = File(context.cacheDir, "showcase_cards")
        if (!cacheDir.exists()) cacheDir.mkdirs()
        
        val file = File(cacheDir, "showcase_${productId}.png")
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        }
        
        return file
    }
    
    /**
     * Share a showcase card via Android share sheet.
     */
    fun shareCard(context: Context, card: ShowcaseCard) {
        val uri: Uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            card.file
        )
        
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "image/png"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_TEXT, """
                🐔 Meet ${card.bird.name}!
                ${card.bird.breed ?: ""}
                
                ${card.completeness.label} • Registered on ROSTRY
                
                #ROSTRY #Poultry #${card.bird.breed?.replace(" ", "") ?: "Birds"}
            """.trimIndent())
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        
        context.startActivity(Intent.createChooser(shareIntent, "Share ${card.bird.name}'s Showcase Card"))
    }
}

/**
 * Data class representing a generated showcase card.
 */
data class ShowcaseCard(
    val bitmap: Bitmap,
    val file: File,
    val bird: ProductEntity,
    val completeness: PedigreeCompleteness
)
