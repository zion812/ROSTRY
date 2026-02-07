package com.rio.rostry.utils.export

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import com.rio.rostry.data.repository.AssetDocumentation
import com.rio.rostry.data.repository.LifecycleTimelineEntry
import com.rio.rostry.data.repository.TimelineEventType
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

/**
 * PDF Generator for Asset Lifecycle Documentation.
 * Creates multi-page PDF with cover, timeline, tables, and summaries.
 */
@Singleton
class AssetDocumentPdfGenerator @Inject constructor(
    private val context: Context
) {
    private val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    private val dateTimeFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
    
    // Page dimensions (A4 at 72dpi)
    private val pageWidth = 595
    private val pageHeight = 842
    private val marginLeft = 50f
    private val marginRight = 50f
    private val marginTop = 60f
    private val marginBottom = 60f
    private val contentWidth = pageWidth - marginLeft - marginRight
    
    /**
     * Generate PDF for asset documentation.
     * @return Filename if successful, null otherwise.
     */
    fun generatePdf(documentation: AssetDocumentation, timeline: List<LifecycleTimelineEntry>): String? {
        val document = PdfDocument()
        
        try {
            var pageNum = 1
            
            // Page 1: Cover
            val coverPage = createPage(document, pageNum++)
            drawCoverPage(coverPage.canvas, documentation)
            document.finishPage(coverPage)
            
            // Page 2: Timeline
            if (timeline.isNotEmpty()) {
                val timelinePage = createPage(document, pageNum++)
                drawTimelinePage(timelinePage.canvas, timeline)
                document.finishPage(timelinePage)
            }
            
            // Page 3: Records (Vaccination, Growth, Activity)
            val recordsPage = createPage(document, pageNum++)
            drawRecordsPage(recordsPage.canvas, documentation)
            document.finishPage(recordsPage)
            
            // Page 4: Financial Summary
            if (documentation.totalExpensesInr > 0) {
                val financialPage = createPage(document, pageNum++)
                drawFinancialPage(financialPage.canvas, documentation)
                document.finishPage(financialPage)
            }
            
            // Save to Downloads
            val fileName = "Asset_${documentation.asset.name.replace(" ", "_")}_${System.currentTimeMillis()}.pdf"
            
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
    
    private fun createPage(document: PdfDocument, pageNum: Int): PdfDocument.Page {
        val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNum).create()
        return document.startPage(pageInfo)
    }
    
    private fun drawCoverPage(canvas: Canvas, doc: AssetDocumentation) {
        val paint = Paint().apply {
            isAntiAlias = true
        }
        
        // Background
        paint.color = Color.WHITE
        canvas.drawRect(0f, 0f, pageWidth.toFloat(), pageHeight.toFloat(), paint)
        
        // Border
        paint.color = Color.parseColor("#4A8C2A") // Farmer Green
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 8f
        canvas.drawRect(30f, 30f, pageWidth - 30f, pageHeight - 30f, paint)
        
        // Title
        paint.style = Paint.Style.FILL
        paint.textSize = 28f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textAlign = Paint.Align.CENTER
        paint.color = Color.parseColor("#1B5E20")
        canvas.drawText("ASSET LIFECYCLE DOCUMENTATION", pageWidth / 2f, 100f, paint)
        
        // Asset Name
        paint.textSize = 24f
        paint.color = Color.BLACK
        canvas.drawText(doc.asset.name, pageWidth / 2f, 150f, paint)
        
        // Asset Details Box
        var y = 220f
        paint.textAlign = Paint.Align.LEFT
        paint.textSize = 14f
        paint.typeface = Typeface.DEFAULT
        
        drawLabelValue(canvas, paint, "Asset Type:", doc.asset.assetType, marginLeft, y)
        y += 24f
        drawLabelValue(canvas, paint, "Breed:", doc.asset.breed ?: "N/A", marginLeft, y)
        y += 24f
        drawLabelValue(canvas, paint, "Status:", doc.asset.status, marginLeft, y)
        y += 24f
        drawLabelValue(canvas, paint, "Quantity:", doc.asset.quantity.toString(), marginLeft, y)
        y += 24f
        doc.asset.birthDate?.let {
            drawLabelValue(canvas, paint, "Birth/Acquisition:", dateFormat.format(Date(it)), marginLeft, y)
            y += 24f
        }
        drawLabelValue(canvas, paint, "Days Active:", "${doc.daysActive} days", marginLeft, y)
        y += 24f
        drawLabelValue(canvas, paint, "Current Weight:", doc.currentWeight?.let { "${it}g" } ?: "N/A", marginLeft, y)
        
        // Summary Box
        y = 450f
        paint.textSize = 16f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.color = Color.parseColor("#1B5E20")
        canvas.drawText("SUMMARY", marginLeft, y, paint)
        
        y += 30f
        paint.textSize = 14f
        paint.typeface = Typeface.DEFAULT
        paint.color = Color.BLACK
        
        drawLabelValue(canvas, paint, "Total Feed:", "${String.format("%.1f", doc.totalFeedKg)} kg", marginLeft, y)
        y += 24f
        drawLabelValue(canvas, paint, "Total Expenses:", "₹${String.format("%.0f", doc.totalExpensesInr)}", marginLeft, y)
        y += 24f
        drawLabelValue(canvas, paint, "Vaccinations:", doc.vaccinationCount.toString(), marginLeft, y)
        y += 24f
        drawLabelValue(canvas, paint, "Mortality:", doc.totalMortality.toString(), marginLeft, y)
        
        // Footer
        paint.textSize = 10f
        paint.textAlign = Paint.Align.CENTER
        paint.color = Color.GRAY
        canvas.drawText("Generated on ${dateTimeFormat.format(Date())} by ROSTRY App", pageWidth / 2f, pageHeight - 50f, paint)
    }
    
    private fun drawTimelinePage(canvas: Canvas, timeline: List<LifecycleTimelineEntry>) {
        val paint = Paint().apply { isAntiAlias = true }
        
        // Title
        paint.textSize = 20f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.color = Color.parseColor("#1B5E20")
        canvas.drawText("LIFECYCLE TIMELINE", marginLeft, marginTop, paint)
        
        var y = marginTop + 40f
        paint.textSize = 12f
        paint.typeface = Typeface.DEFAULT
        
        for (entry in timeline.take(20)) { // Limit to fit page
            // Timeline dot
            val dotColor = when (entry.type) {
                TimelineEventType.BIRTH -> Color.parseColor("#4CAF50")
                TimelineEventType.VACCINATION -> Color.parseColor("#2196F3")
                TimelineEventType.GROWTH_UPDATE -> Color.parseColor("#9C27B0")
                TimelineEventType.MORTALITY -> Color.parseColor("#F44336")
                TimelineEventType.STAGE_CHANGE -> Color.parseColor("#FF9800")
                TimelineEventType.SALE -> Color.parseColor("#795548")
                TimelineEventType.HEALTH_EVENT -> Color.parseColor("#E91E63")
                else -> Color.GRAY
            }
            
            paint.color = dotColor
            paint.style = Paint.Style.FILL
            canvas.drawCircle(marginLeft + 10, y, 6f, paint)
            
            // Vertical line
            if (entry != timeline.last()) {
                paint.color = Color.LTGRAY
                paint.strokeWidth = 2f
                canvas.drawLine(marginLeft + 10, y + 8, marginLeft + 10, y + 32, paint)
            }
            
            // Date
            paint.color = Color.GRAY
            paint.textSize = 10f
            canvas.drawText(dateFormat.format(Date(entry.date)), marginLeft + 30, y - 4, paint)
            
            // Title
            paint.color = Color.BLACK
            paint.textSize = 12f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            canvas.drawText(entry.title, marginLeft + 30, y + 10, paint)
            
            // Description
            if (entry.description.isNotEmpty()) {
                paint.typeface = Typeface.DEFAULT
                paint.textSize = 10f
                paint.color = Color.DKGRAY
                canvas.drawText(entry.description.take(60), marginLeft + 30, y + 22, paint)
            }
            
            y += 40f
            if (y > pageHeight - marginBottom) break
        }
    }
    
    private fun drawRecordsPage(canvas: Canvas, doc: AssetDocumentation) {
        val paint = Paint().apply { isAntiAlias = true }
        var y = marginTop
        
        // Vaccination Records
        paint.textSize = 16f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.color = Color.parseColor("#1B5E20")
        canvas.drawText("VACCINATION RECORDS", marginLeft, y, paint)
        y += 24f
        
        paint.textSize = 10f
        paint.typeface = Typeface.DEFAULT
        paint.color = Color.BLACK
        
        if (doc.vaccinations.isEmpty()) {
            canvas.drawText("No vaccination records", marginLeft, y, paint)
            y += 20f
        } else {
            // Table headers
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            canvas.drawText("Date", marginLeft, y, paint)
            canvas.drawText("Vaccine", marginLeft + 100, y, paint)
            canvas.drawText("Status", marginLeft + 280, y, paint)
            y += 16f
            
            paint.typeface = Typeface.DEFAULT
            for (vax in doc.vaccinations.take(10)) {
                val date = vax.administeredAt ?: vax.scheduledAt
                canvas.drawText(dateFormat.format(Date(date)), marginLeft, y, paint)
                canvas.drawText(vax.vaccineType.take(25), marginLeft + 100, y, paint)
                canvas.drawText(if (vax.administeredAt != null) "Administered" else "Scheduled", marginLeft + 280, y, paint)
                y += 14f
            }
        }
        
        y += 30f
        
        // Growth Records
        paint.textSize = 16f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.color = Color.parseColor("#1B5E20")
        canvas.drawText("GROWTH RECORDS", marginLeft, y, paint)
        y += 24f
        
        paint.textSize = 10f
        paint.color = Color.BLACK
        
        if (doc.growthRecords.isEmpty()) {
            canvas.drawText("No growth records", marginLeft, y, paint)
        } else {
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            canvas.drawText("Date", marginLeft, y, paint)
            canvas.drawText("Weight (g)", marginLeft + 120, y, paint)
            canvas.drawText("Notes", marginLeft + 220, y, paint)
            y += 16f
            
            paint.typeface = Typeface.DEFAULT
            for (growth in doc.growthRecords.take(10)) {
                canvas.drawText(dateFormat.format(Date(growth.createdAt)), marginLeft, y, paint)
                canvas.drawText(growth.weightGrams?.toString() ?: "-", marginLeft + 120, y, paint)
                canvas.drawText(growth.healthStatus?.take(30) ?: "", marginLeft + 220, y, paint)
                y += 14f
            }
        }
    }
    
    private fun drawFinancialPage(canvas: Canvas, doc: AssetDocumentation) {
        val paint = Paint().apply { isAntiAlias = true }
        var y = marginTop
        
        paint.textSize = 20f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.color = Color.parseColor("#1B5E20")
        canvas.drawText("FINANCIAL SUMMARY", marginLeft, y, paint)
        y += 40f
        
        paint.textSize = 14f
        paint.typeface = Typeface.DEFAULT
        paint.color = Color.BLACK
        
        // Expenses by type
        val expensesByType = doc.activities
            .filter { it.amountInr != null && it.amountInr > 0 }
            .groupBy { it.activityType }
            .mapValues { (_, logs) -> logs.sumOf { it.amountInr ?: 0.0 } }
        
        drawLabelValue(canvas, paint, "Total Expenses:", "₹${String.format("%.0f", doc.totalExpensesInr)}", marginLeft, y)
        y += 30f
        
        paint.textSize = 12f
        for ((type, amount) in expensesByType) {
            drawLabelValue(canvas, paint, "  $type:", "₹${String.format("%.0f", amount)}", marginLeft, y)
            y += 20f
        }
        
        y += 30f
        
        // Cost per unit (if quantity > 1)
        if (doc.asset.quantity > 1) {
            val costPerUnit = doc.totalExpensesInr / doc.asset.quantity
            drawLabelValue(canvas, paint, "Cost per Unit:", "₹${String.format("%.2f", costPerUnit)}", marginLeft, y)
        }
    }
    
    private fun drawLabelValue(canvas: Canvas, paint: Paint, label: String, value: String, x: Float, y: Float) {
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText(label, x, y, paint)
        paint.typeface = Typeface.DEFAULT
        canvas.drawText(value, x + 150, y, paint)
    }
}
