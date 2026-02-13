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
    private val pedigreeRepository: PedigreeRepository,
    @dagger.hilt.android.qualifiers.ApplicationContext private val context: Context
) {
    companion object {
        private const val CARD_WIDTH = 1080
        private const val CARD_HEIGHT = 1920
        private const val PHOTO_SIZE = 650 // Increased from 400
        private const val CORNER_RADIUS = 60f
    }
    
    /**
     * Generate a showcase card bitmap for a bird.
     */
    /**
     * Generate a showcase card bitmap for a bird.
     */
    suspend fun generateCard(
        bird: ProductEntity,
        config: ShowcaseConfig,
        stats: List<com.rio.rostry.data.database.dao.ShowRecordStats>,
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
                
                // Draw card with theme
                drawBackground(canvas, config.theme)
                drawBirdPhoto(canvas, bird, config.theme)
                drawBirdInfo(canvas, bird, config, config.theme)
                
                // Draw dynamic sections based on config
                var currentY = 1300f // Adjusted starting Y for metrics
                
                if (config.showPedigreeBadge || config.showVaccinationBadge) {
                    drawBadges(canvas, completeness, vaccinationCount, config, currentY)
                    currentY += 160f
                }
                
                if (config.showWins || config.showAge || config.showWeight) {
                     drawStats(canvas, bird, stats, config, currentY)
                }

                drawBranding(canvas, config.theme)
                
                // Save to cache
                val file = saveToCacheFile(bitmap, bird.productId)
                
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
    
    private fun drawBackground(canvas: Canvas, theme: ShowcaseTheme) {
        val paint = Paint()
        
        // Background Gradient
        val colors = when(theme) {
            ShowcaseTheme.DARK_PREMIUM -> intArrayOf(0xFF1a1a2e.toInt(), 0xFF16213e.toInt(), 0xFF0f3460.toInt())
            ShowcaseTheme.LIGHT_ELEGANCE -> intArrayOf(0xFFFFFFFF.toInt(), 0xFFF5F5F5.toInt(), 0xFFE0E0E0.toInt())
            ShowcaseTheme.GOLD_LUXURY -> intArrayOf(0xFF2C2C2C.toInt(), 0xFF1C1C1C.toInt(), 0xFF000000.toInt())
            ShowcaseTheme.NATURE_FRESH -> intArrayOf(0xFFE8F5E9.toInt(), 0xFFC8E6C9.toInt(), 0xFFA5D6A7.toInt())
        }
        
        paint.shader = LinearGradient(
            0f, 0f, 0f, CARD_HEIGHT.toFloat(),
            colors,
            null,
            Shader.TileMode.CLAMP
        )
        
        val rect = RectF(0f, 0f, CARD_WIDTH.toFloat(), CARD_HEIGHT.toFloat())
        canvas.drawRoundRect(rect, CORNER_RADIUS, CORNER_RADIUS, paint)
        
        // Add decorative elements
        drawDecorativeElements(canvas, theme)
    }
    
    private fun drawDecorativeElements(canvas: Canvas, theme: ShowcaseTheme) {
        val paint = Paint().apply {
            color = theme.accentColor
            alpha = 30
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
    
    private suspend fun drawBirdPhoto(canvas: Canvas, bird: ProductEntity, theme: ShowcaseTheme) {
        val centerX = CARD_WIDTH / 2f
        val photoY = 280f // Moved up slightly to accommodate larger size
        
        // Draw glowing ring behind photo
        val ringPaint = Paint().apply {
            shader = LinearGradient(
                centerX - PHOTO_SIZE / 2, photoY,
                centerX + PHOTO_SIZE / 2, photoY + PHOTO_SIZE,
                intArrayOf(theme.accentColor, Color.WHITE), // Added White for shine effect 
                null,
                Shader.TileMode.MIRROR
            )
            style = Paint.Style.STROKE
            strokeWidth = 15f // Thicker ring
            if (theme == ShowcaseTheme.DARK_PREMIUM || theme == ShowcaseTheme.GOLD_LUXURY) {
                setShadowLayer(40f, 0f, 0f, theme.accentColor)
            }
        }
        canvas.drawCircle(centerX, photoY + PHOTO_SIZE / 2, PHOTO_SIZE / 2f + 20, ringPaint)
        
        // Load and draw bird photo
        val photoBitmap = loadBirdPhoto(bird)
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
                color = if (theme == ShowcaseTheme.LIGHT_ELEGANCE || theme == ShowcaseTheme.NATURE_FRESH) 
                    Color.LTGRAY else Color.parseColor("#2a2a4a")
            }
            canvas.drawCircle(centerX, photoY + PHOTO_SIZE / 2, PHOTO_SIZE / 2f, placeholderPaint)
            
            // Draw bird icon placeholder
            val textPaint = Paint().apply {
                color = theme.primaryColor
                textSize = 120f
                textAlign = Paint.Align.CENTER
            }
            canvas.drawText("🐔", centerX, photoY + PHOTO_SIZE / 2 + 40, textPaint)
        }
    }
    
    private suspend fun loadBirdPhoto(bird: ProductEntity): Bitmap? {
        val imageUrl = bird.imageUrls.firstOrNull() ?: return null
        
        return try {
            withContext(Dispatchers.IO) {
                if (imageUrl.startsWith("content://") || imageUrl.startsWith("file://")) {
                    val uri = Uri.parse(imageUrl)
                    context.contentResolver.openInputStream(uri)?.use { input ->
                        BitmapFactory.decodeStream(input)
                    }
                } else {
                    val url = URL(imageUrl)
                    val connection = url.openConnection()
                    connection.connectTimeout = 5000
                    connection.readTimeout = 5000
                    val input = connection.getInputStream()
                    BitmapFactory.decodeStream(input)
                }
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
    
    private fun drawBirdInfo(canvas: Canvas, bird: ProductEntity, config: ShowcaseConfig, theme: ShowcaseTheme) {
        val centerX = CARD_WIDTH / 2f
        val nameY = 1050f // Pushed down due to larger photo
        
        val textColor = if (theme == ShowcaseTheme.LIGHT_ELEGANCE || theme == ShowcaseTheme.NATURE_FRESH) Color.BLACK else Color.WHITE
        val subTextColor = if (theme == ShowcaseTheme.LIGHT_ELEGANCE || theme == ShowcaseTheme.NATURE_FRESH) Color.DKGRAY else Color.LTGRAY

        // Bird name
        val namePaint = Paint().apply {
            color = textColor
            textSize = 80f // Slightly larger
            textAlign = Paint.Align.CENTER
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }
        canvas.drawText(bird.name, centerX, nameY, namePaint)
        
        // Breed
        bird.breed?.let { breed ->
            val breedPaint = Paint().apply {
                color = subTextColor
                textSize = 42f
                textAlign = Paint.Align.CENTER
                isAntiAlias = true
            }
            canvas.drawText(breed, centerX, nameY + 60, breedPaint)
        }
        
        // Color/variety if available
        bird.color?.let { color ->
            val colorPaint = Paint().apply {
                this.color = subTextColor
                textSize = 36f
                textAlign = Paint.Align.CENTER
                isAntiAlias = true
            }
            canvas.drawText(color, centerX, nameY + 110, colorPaint)
        }
    }
    
    private fun drawBadges(canvas: Canvas, completeness: PedigreeCompleteness, vaccinationCount: Int, config: ShowcaseConfig, y: Float) {
        val centerX = CARD_WIDTH / 2f
        val badgeSpacing = 320f
        
        if (config.showPedigreeBadge && config.showVaccinationBadge) {
             drawBadge(canvas, centerX - badgeSpacing / 2, y, "🌳", completeness.label, Color.parseColor(
                when (completeness) {
                    PedigreeCompleteness.THREE_GEN -> "#FFD700"
                    PedigreeCompleteness.TWO_GEN -> "#4CAF50"
                    PedigreeCompleteness.ONE_GEN -> "#FF9800"
                    PedigreeCompleteness.NONE -> "#9E9E9E"
                }
            ), config.theme)
            
            val vaccineStatus = when {
                vaccinationCount >= 3 -> "Fully Vaxxed"
                vaccinationCount > 0 -> "$vaccinationCount Vaccines"
                else -> "Not Vaxxed"
            }
            drawBadge(canvas, centerX + badgeSpacing / 2, y, "💉", vaccineStatus, 
               if (vaccinationCount >= 3) Color.parseColor("#4CAF50") else Color.parseColor("#FF9800"), 
               config.theme
            )
        } else if (config.showPedigreeBadge) {
             drawBadge(canvas, centerX, y, "🌳", completeness.label, Color.parseColor(
                when (completeness) {
                    PedigreeCompleteness.THREE_GEN -> "#FFD700"
                    PedigreeCompleteness.TWO_GEN -> "#4CAF50"
                    PedigreeCompleteness.ONE_GEN -> "#FF9800"
                    PedigreeCompleteness.NONE -> "#9E9E9E"
                }
            ), config.theme)
        } else if (config.showVaccinationBadge) {
             val vaccineStatus = when {
                vaccinationCount >= 3 -> "Fully Vaxxed"
                vaccinationCount > 0 -> "$vaccinationCount Vaccines"
                else -> "Not Vaxxed"
            }
            drawBadge(canvas, centerX, y, "💉", vaccineStatus, 
               if (vaccinationCount >= 3) Color.parseColor("#4CAF50") else Color.parseColor("#FF9800"),
               config.theme
            )
        }
    }
    
    private fun drawStats(
        canvas: Canvas, 
        bird: ProductEntity, 
        stats: List<com.rio.rostry.data.database.dao.ShowRecordStats>, 
        config: ShowcaseConfig, 
        startY: Float
    ) {
        val centerX = CARD_WIDTH / 2f
        var currentY = startY + 40f
        
        val textColor = if (config.theme == ShowcaseTheme.LIGHT_ELEGANCE || config.theme == ShowcaseTheme.NATURE_FRESH) Color.BLACK else Color.WHITE
        
        val paint = Paint().apply {
            color = textColor
            textSize = 34f
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }

        if (config.showWins) {
            val totalWins = stats.sumOf { it.wins }
            val totalPodiums = stats.sumOf { it.podiums }
            if (totalWins > 0 || totalPodiums > 0) {
                 canvas.drawText("🏆  $totalWins Wins • $totalPodiums Podiums", centerX, currentY, paint)
                 currentY += 50f
            }
        }
        
        if (config.showAge) {
             val age = getAgeString(bird.birthDate)
             if (age.isNotEmpty()) {
                 canvas.drawText("📅  Age: $age", centerX, currentY, paint)
                 currentY += 50f
             }
        }
        
        if (config.showWeight) {
             // Assuming we might have weight in future, for now placeholder if configured
             // Or maybe we use price? Let's skip for now unless requested.
        }
    }
    
    private fun getAgeString(birthDate: Long?): String {
        if (birthDate == null) return ""
        val diff = System.currentTimeMillis() - birthDate
        val days = diff / (1000 * 60 * 60 * 24)
        return when {
            days > 365 -> "${days / 365} Years"
            days > 30 -> "${days / 30} Months"
            else -> "$days Days"
        }
    }


    private fun drawBadge(canvas: Canvas, x: Float, y: Float, icon: String, text: String, accentColor: Int, theme: ShowcaseTheme) {
        val badgeWidth = 320f // Wider
        val badgeHeight = 110f // Sleeker
        
        val bgPaint = Paint().apply {
            // Glassmorphism effect: Semi-transparent background
            color = if (theme == ShowcaseTheme.LIGHT_ELEGANCE || theme == ShowcaseTheme.NATURE_FRESH) 
                Color.parseColor("#CCFFFFFF") else Color.parseColor("#40000000")
            isAntiAlias = true
        }
        
        val rect = RectF(x - badgeWidth / 2, y, x + badgeWidth / 2, y + badgeHeight)
        canvas.drawRoundRect(rect, 55f, 55f, bgPaint) // Fully rounded pill shape
        
        // Badge border (Subtle)
        val borderPaint = Paint().apply {
            color = accentColor
            style = Paint.Style.STROKE
            strokeWidth = 2f
            isAntiAlias = true
            alpha = 180
        }
        canvas.drawRoundRect(rect, 55f, 55f, borderPaint)
        
        // Icon
        val iconPaint = Paint().apply {
            textSize = 36f
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }
        // Draw icon and text side-by-side for Pill layout
        // Instead of stacked
        val combinedPaint = Paint().apply {
            color = if (theme == ShowcaseTheme.LIGHT_ELEGANCE || theme == ShowcaseTheme.NATURE_FRESH) Color.BLACK else Color.WHITE
            textSize = 32f
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        
        // Draw icon + text centered together
        // "$icon  $text"
        canvas.drawText("$icon  $text", x, y + 70, combinedPaint)
    }
    
    private fun drawBranding(canvas: Canvas, theme: ShowcaseTheme) {
        val centerX = CARD_WIDTH / 2f
        val brandY = CARD_HEIGHT - 150f
        
        // ROSTRY logo text
        val logoPaint = Paint().apply {
            color = theme.accentColor
            textSize = 64f
            textAlign = Paint.Align.CENTER
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }
        canvas.drawText("ROSTRY", centerX, brandY, logoPaint)
        
        // Tagline
        val taglinePaint = Paint().apply {
            color = if (theme == ShowcaseTheme.LIGHT_ELEGANCE || theme == ShowcaseTheme.NATURE_FRESH) Color.DKGRAY else Color.LTGRAY
            textSize = 28f
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }
        canvas.drawText("Premium Poultry Management", centerX, brandY + 40, taglinePaint)
    }
    
    private fun saveToCacheFile(bitmap: Bitmap, productId: String): File {
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
