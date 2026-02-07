package com.rio.rostry.utils.export

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.os.Environment
import android.provider.MediaStore
import com.rio.rostry.data.repository.AssetSummary
import com.rio.rostry.data.repository.FarmDocumentation
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton
import dagger.hilt.android.qualifiers.ApplicationContext

/**
 * PDF Generator for Farm-Wide Documentation.
 * Creates multi-page PDF with farm overview, asset inventory, and financial summaries.
 */
@Singleton
class FarmDocumentPdfGenerator @Inject constructor(
    @ApplicationContext private val context: Context
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
     * Generate PDF for farm-wide documentation.
     * @return Filename if successful, null otherwise.
     */
    fun generatePdf(documentation: FarmDocumentation, assetSummaries: List<AssetSummary>): String? {
        val document = PdfDocument()
        
        try {
            var pageNum = 1
            
            // Page 1: Cover
            val coverPage = createPage(document, pageNum++)
            drawCoverPage(coverPage.canvas, documentation)
            document.finishPage(coverPage)
            
            // Page 2: Asset Inventory Summary
            val inventoryPage = createPage(document, pageNum++)
            drawInventoryPage(inventoryPage.canvas, documentation)
            document.finishPage(inventoryPage)
            
            // Page 3: Individual Asset Details
            if (assetSummaries.isNotEmpty()) {
                val assetsPage = createPage(document, pageNum++)
                drawAssetDetailsPage(assetsPage.canvas, assetSummaries)
                document.finishPage(assetsPage)
            }
            
            // Page 4: Financial Summary
            if (documentation.totalExpensesInr > 0) {
                val financialPage = createPage(document, pageNum++)
                drawFinancialPage(financialPage.canvas, documentation)
                document.finishPage(financialPage)
            }
            
            // Save to Downloads
            val fileName = "Farm_Report_${documentation.farmName.replace(" ", "_")}_${System.currentTimeMillis()}.pdf"
            
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
    
    private fun drawCoverPage(canvas: Canvas, doc: FarmDocumentation) {
        val paint = Paint().apply { isAntiAlias = true }
        
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
        canvas.drawText("FARM DOCUMENTATION REPORT", pageWidth / 2f, 100f, paint)
        
        // Farm Name
        paint.textSize = 24f
        paint.color = Color.BLACK
        canvas.drawText(doc.farmName, pageWidth / 2f, 150f, paint)
        
        // Farm Overview Box
        var y = 220f
        paint.textAlign = Paint.Align.LEFT
        paint.textSize = 14f
        paint.typeface = Typeface.DEFAULT
        
        drawLabelValue(canvas, paint, "Total Assets:", doc.assets.size.toString(), marginLeft, y)
        y += 24f
        drawLabelValue(canvas, paint, "Active Days:", doc.activeDaysRange, marginLeft, y)
        y += 24f
        doc.earliestBirthDate?.let {
            drawLabelValue(canvas, paint, "Earliest Record:", dateFormat.format(Date(it)), marginLeft, y)
            y += 24f
        }
        drawLabelValue(canvas, paint, "Report Date:", dateTimeFormat.format(Date(doc.generatedAt)), marginLeft, y)
        
        // Summary Statistics Box
        y = 380f
        paint.textSize = 16f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.color = Color.parseColor("#1B5E20")
        canvas.drawText("FARM SUMMARY", marginLeft, y, paint)
        
        y += 30f
        paint.textSize = 14f
        paint.typeface = Typeface.DEFAULT
        paint.color = Color.BLACK
        
        drawLabelValue(canvas, paint, "Total Feed:", "${String.format("%.1f", doc.totalFeedKg)} kg", marginLeft, y)
        y += 24f
        drawLabelValue(canvas, paint, "Total Expenses:", "₹${String.format("%.0f", doc.totalExpensesInr)}", marginLeft, y)
        y += 24f
        drawLabelValue(canvas, paint, "Total Vaccinations:", doc.totalVaccinations.toString(), marginLeft, y)
        y += 24f
        drawLabelValue(canvas, paint, "Total Mortality:", doc.totalMortality.toString(), marginLeft, y)
        y += 24f
        drawLabelValue(canvas, paint, "Total Activities:", doc.totalActivities.toString(), marginLeft, y)
        
        // Asset Type Breakdown
        y = 560f
        paint.textSize = 16f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.color = Color.parseColor("#1B5E20")
        canvas.drawText("ASSETS BY TYPE", marginLeft, y, paint)
        
        y += 24f
        paint.textSize = 12f
        paint.typeface = Typeface.DEFAULT
        paint.color = Color.BLACK
        
        for ((type, assets) in doc.assetsByType) {
            val totalQty = assets.sumOf { it.quantity.toInt() }
            canvas.drawText("• $type: ${assets.size} assets ($totalQty total quantity)", marginLeft + 10, y, paint)
            y += 18f
        }
        
        // Footer
        paint.textSize = 10f
        paint.textAlign = Paint.Align.CENTER
        paint.color = Color.GRAY
        canvas.drawText("Generated on ${dateTimeFormat.format(Date())} by ROSTRY App", pageWidth / 2f, pageHeight - 50f, paint)
    }
    
    private fun drawInventoryPage(canvas: Canvas, doc: FarmDocumentation) {
        val paint = Paint().apply { isAntiAlias = true }
        var y = marginTop
        
        paint.textSize = 20f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.color = Color.parseColor("#1B5E20")
        canvas.drawText("ASSET INVENTORY", marginLeft, y, paint)
        y += 40f
        
        // Assets by Status
        paint.textSize = 14f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("Status Breakdown", marginLeft, y, paint)
        y += 24f
        
        paint.textSize = 12f
        paint.typeface = Typeface.DEFAULT
        paint.color = Color.BLACK
        
        for ((status, assets) in doc.assetsByStatus) {
            val totalQty = assets.sumOf { it.quantity.toInt() }
            canvas.drawText("• $status: ${assets.size} assets ($totalQty qty)", marginLeft + 10, y, paint)
            y += 18f
        }
        
        y += 30f
        
        // Asset List Table
        paint.textSize = 14f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.color = Color.parseColor("#1B5E20")
        canvas.drawText("ASSET LIST", marginLeft, y, paint)
        y += 24f
        
        // Table headers
        paint.textSize = 10f
        paint.color = Color.BLACK
        canvas.drawText("Name", marginLeft, y, paint)
        canvas.drawText("Type", marginLeft + 150, y, paint)
        canvas.drawText("Qty", marginLeft + 250, y, paint)
        canvas.drawText("Status", marginLeft + 300, y, paint)
        canvas.drawText("Age", marginLeft + 400, y, paint)
        y += 16f
        
        // Draw separator line
        paint.strokeWidth = 1f
        canvas.drawLine(marginLeft, y - 4, pageWidth - marginRight, y - 4, paint)
        
        paint.typeface = Typeface.DEFAULT
        for (asset in doc.assets.take(25)) { // Limit to fit page
            val ageWeeks = asset.birthDate?.let {
                val days = ((System.currentTimeMillis() - it) / (1000 * 60 * 60 * 24)).toInt()
                "${days / 7}w"
            } ?: "N/A"
            
            canvas.drawText(asset.name.take(20), marginLeft, y, paint)
            canvas.drawText(asset.assetType.take(12), marginLeft + 150, y, paint)
            canvas.drawText(asset.quantity.toInt().toString(), marginLeft + 250, y, paint)
            canvas.drawText(asset.status.take(10), marginLeft + 300, y, paint)
            canvas.drawText(ageWeeks, marginLeft + 400, y, paint)
            y += 14f
            
            if (y > pageHeight - marginBottom) break
        }
        
        if (doc.assets.size > 25) {
            y += 10f
            paint.color = Color.GRAY
            canvas.drawText("... and ${doc.assets.size - 25} more assets", marginLeft, y, paint)
        }
    }
    
    private fun drawAssetDetailsPage(canvas: Canvas, summaries: List<AssetSummary>) {
        val paint = Paint().apply { isAntiAlias = true }
        var y = marginTop
        
        paint.textSize = 20f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.color = Color.parseColor("#1B5E20")
        canvas.drawText("ASSET PERFORMANCE SUMMARY", marginLeft, y, paint)
        y += 40f
        
        // Table headers
        paint.textSize = 9f
        paint.color = Color.BLACK
        canvas.drawText("Asset", marginLeft, y, paint)
        canvas.drawText("Vax", marginLeft + 100, y, paint)
        canvas.drawText("Growth", marginLeft + 140, y, paint)
        canvas.drawText("Mortality", marginLeft + 190, y, paint)
        canvas.drawText("Feed (kg)", marginLeft + 260, y, paint)
        canvas.drawText("Expenses (₹)", marginLeft + 330, y, paint)
        canvas.drawText("Weight (g)", marginLeft + 420, y, paint)
        y += 16f
        
        // Draw separator line
        paint.strokeWidth = 1f
        canvas.drawLine(marginLeft, y - 4, pageWidth - marginRight, y - 4, paint)
        
        paint.typeface = Typeface.DEFAULT
        for (summary in summaries.take(30)) { // Limit to fit page
            canvas.drawText(summary.asset.name.take(14), marginLeft, y, paint)
            canvas.drawText(summary.vaccinationCount.toString(), marginLeft + 100, y, paint)
            canvas.drawText(summary.growthRecordCount.toString(), marginLeft + 140, y, paint)
            canvas.drawText(summary.mortalityCount.toString(), marginLeft + 190, y, paint)
            canvas.drawText(String.format("%.1f", summary.feedKg), marginLeft + 260, y, paint)
            canvas.drawText(String.format("%.0f", summary.expensesInr), marginLeft + 330, y, paint)
            canvas.drawText(summary.currentWeight?.let { String.format("%.0f", it) } ?: "-", marginLeft + 420, y, paint)
            y += 14f
            
            if (y > pageHeight - marginBottom) break
        }
    }
    
    private fun drawFinancialPage(canvas: Canvas, doc: FarmDocumentation) {
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
        
        drawLabelValue(canvas, paint, "Total Expenses:", "₹${String.format("%.0f", doc.totalExpensesInr)}", marginLeft, y)
        y += 30f
        
        // Expenses by type
        paint.textSize = 14f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.color = Color.parseColor("#1B5E20")
        canvas.drawText("EXPENSES BY CATEGORY", marginLeft, y, paint)
        y += 24f
        
        paint.textSize = 12f
        paint.typeface = Typeface.DEFAULT
        paint.color = Color.BLACK
        
        for ((type, amount) in doc.expensesByType.entries.sortedByDescending { it.value }) {
            val percentage = if (doc.totalExpensesInr > 0) (amount / doc.totalExpensesInr * 100) else 0.0
            drawLabelValue(canvas, paint, "  $type:", "₹${String.format("%.0f", amount)} (${String.format("%.1f", percentage)}%)", marginLeft, y)
            y += 20f
        }
        
        y += 30f
        
        // Cost analysis
        val totalAssetQty = doc.assets.sumOf { it.quantity }
        if (totalAssetQty > 1) {
            paint.textSize = 14f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            paint.color = Color.parseColor("#1B5E20")
            canvas.drawText("COST ANALYSIS", marginLeft, y, paint)
            y += 24f
            
            paint.textSize = 12f
            paint.typeface = Typeface.DEFAULT
            paint.color = Color.BLACK
            
            val costPerUnit = doc.totalExpensesInr / totalAssetQty
            val feedPerUnit = doc.totalFeedKg / totalAssetQty
            
            drawLabelValue(canvas, paint, "Cost per Unit:", "₹${String.format("%.2f", costPerUnit)}", marginLeft, y)
            y += 20f
            drawLabelValue(canvas, paint, "Feed per Unit:", "${String.format("%.2f", feedPerUnit)} kg", marginLeft, y)
        }
    }
    
    private fun drawLabelValue(canvas: Canvas, paint: Paint, label: String, value: String, x: Float, y: Float) {
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText(label, x, y, paint)
        paint.typeface = Typeface.DEFAULT
        canvas.drawText(value, x + 150, y, paint)
    }
}
