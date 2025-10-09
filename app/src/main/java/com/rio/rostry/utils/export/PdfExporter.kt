package com.rio.rostry.utils.export

import android.content.Context
import android.graphics.pdf.PdfDocument
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

object PdfExporter {
    data class TableSection(
        val title: String,
        val headers: List<String>,
        val rows: List<List<String>>
    )

    fun writeSimpleTable(
        context: Context,
        fileName: String,
        title: String,
        headers: List<String>,
        rows: List<List<String>>
    ): Uri {
        val doc = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 ~ 72dpi
        val page = doc.startPage(pageInfo)
        val canvas = page.canvas
        val paint = android.graphics.Paint().apply {
            color = android.graphics.Color.BLACK
            textSize = 14f
            isAntiAlias = true
        }
        var y = 40f
        canvas.drawText(title, 40f, y, paint)
        y += 24f
        // headers
        val headerText = headers.joinToString("  |  ")
        canvas.drawText(headerText, 40f, y, paint)
        y += 18f
        // rows
        rows.forEach { r ->
            val line = r.joinToString("  |  ")
            if (y > 800f) return@forEach
            canvas.drawText(line, 40f, y, paint)
            y += 16f
        }
        doc.finishPage(page)

        val dir = File(context.filesDir, "reports")
        if (!dir.exists()) dir.mkdirs()
        val file = File(dir, fileName)
        FileOutputStream(file).use { fos -> doc.writeTo(fos) }
        doc.close()
        val authority = context.packageName + ".fileprovider"
        return FileProvider.getUriForFile(context, authority, file)
    }

    fun writeReport(
        context: Context,
        fileName: String,
        docTitle: String,
        coverBitmap: android.graphics.Bitmap?,
        sections: List<TableSection>
    ): Uri {
        val doc = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4
        val page = doc.startPage(pageInfo)
        val canvas = page.canvas
        val paint = android.graphics.Paint().apply {
            color = android.graphics.Color.BLACK
            textSize = 14f
            isAntiAlias = true
        }
        var y = 40f
        // Title
        paint.textSize = 18f
        canvas.drawText(docTitle, 40f, y, paint)
        paint.textSize = 14f
        y += 20f
        // Cover bitmap (e.g., QR)
        coverBitmap?.let { bmp ->
            val targetW = 120
            val aspect = bmp.height.toFloat() / bmp.width
            val targetH = (targetW * aspect).toInt().coerceAtLeast(120)
            val scaled = android.graphics.Bitmap.createScaledBitmap(bmp, targetW, targetH, true)
            canvas.drawBitmap(scaled, 40f, y, null)
            y += targetH + 12f
        }
        // Sections
        sections.forEach { sec ->
            if (y > 760f) {
                doc.finishPage(page)
                val nextInfo = PdfDocument.PageInfo.Builder(595, 842, doc.pages.size + 1).create()
                val next = doc.startPage(nextInfo)
                val c = next.canvas
                y = 40f
                drawSection(c, paint, sec, y).also { y = it }
                doc.finishPage(next)
            } else {
                y = drawSection(canvas, paint, sec, y)
            }
            y += 8f
        }

        // If only one page was used and not finished yet
        if (doc.pages.isEmpty()) doc.finishPage(page)

        val dir = File(context.filesDir, "reports")
        if (!dir.exists()) dir.mkdirs()
        val file = File(dir, fileName)
        FileOutputStream(file).use { fos -> doc.writeTo(fos) }
        doc.close()
        val authority = context.packageName + ".fileprovider"
        return FileProvider.getUriForFile(context, authority, file)
    }

    private fun drawSection(
        canvas: android.graphics.Canvas,
        paint: android.graphics.Paint,
        section: TableSection,
        startY: Float
    ): Float {
        var y = startY
        paint.textSize = 16f
        canvas.drawText(section.title, 40f, y, paint)
        paint.textSize = 13f
        y += 18f
        // headers
        val headerText = section.headers.joinToString("  |  ")
        canvas.drawText(headerText, 40f, y, paint)
        y += 16f
        section.rows.forEach { r ->
            val line = r.joinToString("  |  ")
            canvas.drawText(line, 40f, y, paint)
            y += 14f
        }
        return y
    }
}
