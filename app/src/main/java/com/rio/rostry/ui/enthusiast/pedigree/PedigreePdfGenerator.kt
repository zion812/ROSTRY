package com.rio.rostry.ui.enthusiast.pedigree

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.os.Environment
import android.provider.MediaStore
import com.rio.rostry.domain.pedigree.PedigreeTree
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PedigreePdfGenerator(private val context: Context) {

    fun generateAndSavePdf(birdName: String, tree: PedigreeTree): String? {
        val document = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 size
        val page = document.startPage(pageInfo)
        val canvas = page.canvas

        drawCertificate(canvas, birdName, tree, pageInfo.pageWidth, pageInfo.pageHeight)

        document.finishPage(page)

        val fileName = "Pedigree_${birdName.replace(" ", "_")}_${System.currentTimeMillis()}.pdf"
        
        try {
            val contentValues = android.content.ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            }

            val uri = context.contentResolver.insert(
                MediaStore.Files.getContentUri("external"),
                contentValues
            ) ?: throw IOException("Failed to create MediaStore entry")

            context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                document.writeTo(outputStream)
            }

            return fileName
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        } finally {
            document.close()
        }
    }

    private fun drawCertificate(canvas: Canvas, birdName: String, tree: PedigreeTree, width: Int, height: Int) {
        val paint = Paint()
        
        // Background
        paint.color = Color.WHITE
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
        
        // Border
        paint.color = Color.parseColor("#4A8C2A") // Farmer Green
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 20f
        canvas.drawRect(40f, 40f, width - 40f, height - 40f, paint)
        
        // Inner Border
        paint.strokeWidth = 2f
        canvas.drawRect(55f, 55f, width - 55f, height - 55f, paint)

        // Title
        paint.style = Paint.Style.FILL
        paint.textSize = 40f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textAlign = Paint.Align.CENTER
        paint.color = Color.BLACK
        canvas.drawText("CERTIFICATE OF PEDIGREE", width / 2f, 150f, paint)
        
        // Bird Name
        paint.textSize = 30f
        paint.color = Color.parseColor("#1B5E20")
        canvas.drawText(birdName, width / 2f, 220f, paint)
        
        // Breed
        tree.bird.breed?.let {
            paint.textSize = 20f
            paint.color = Color.DKGRAY
            paint.typeface = Typeface.DEFAULT
            canvas.drawText("Breed: $it", width / 2f, 260f, paint)
        }

        // Tree Visualization
        // Root Bird (Left) -> Parents (Right)
        // Since we want a certificate look, standard bracket View is better.
        // Or left-to-right tree.
        
        val startX = 80f
        val startY = 400f
        val levelWidth = 150f
        
        drawNode(canvas, tree, startX, startY, 0, levelWidth, paint)
        
        // Footer
        paint.textSize = 12f
        paint.color = Color.GRAY
        paint.textAlign = Paint.Align.CENTER
        val date = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date())
        canvas.drawText("Generated on $date by ROSTRY App", width / 2f, height - 80f, paint)
    }
    
    // Recursive drawing
    private fun drawNode(
        canvas: Canvas, 
        tree: PedigreeTree, 
        x: Float, 
        y: Float, 
        level: Int, 
        levelWidth: Float, 
        paint: Paint
    ) {
        // Draw connection lines first
        paint.color = Color.BLACK
        paint.strokeWidth = 1f
        paint.style = Paint.Style.STROKE
        
        val childY = y
        val nextX = x + levelWidth
        
        val offset = 100f / (level + 1) // Reduce spacing as we go deeper
        
        val sireY = y - offset
        val damY = y + offset
        
        if (level < 3) { // Max depth 3 for visualisation
            if (tree.sire != null) {
                canvas.drawLine(x + 50, y, nextX - 10, sireY + 10, paint) // To Sire
                drawNode(canvas, tree.sire, nextX, sireY, level + 1, levelWidth, paint)
            }
            if (tree.dam != null) {
                canvas.drawLine(x + 50, y, nextX - 10, damY - 10, paint) // To Dam
                drawNode(canvas, tree.dam, nextX, damY, level + 1, levelWidth, paint)
            }
        }
        
        // Draw Box
        paint.style = Paint.Style.FILL
        paint.color = if (level == 0) Color.parseColor("#E8F5E9") else Color.parseColor("#FAFAFA")
        val rectHeight = 40f
        val rectWidth = 120f
        canvas.drawRect(x, y - rectHeight/2, x + rectWidth, y + rectHeight/2, paint)
        
        paint.style = Paint.Style.STROKE
        paint.color = Color.GRAY
        canvas.drawRect(x, y - rectHeight/2, x + rectWidth, y + rectHeight/2, paint)
        
        // Text
        paint.style = Paint.Style.FILL
        paint.color = Color.BLACK
        paint.textAlign = Paint.Align.LEFT
        paint.textSize = 12f
        // paint.typeface = Typeface.DEFAULT_BOLD
        
        val name = tree.bird.name
        canvas.drawText(if(name.length > 15) name.take(13)+".." else name, x + 5, y + 5, paint)
    }
}
