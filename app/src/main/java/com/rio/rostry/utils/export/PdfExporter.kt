package com.rio.rostry.utils.export

import android.content.Context
import android.graphics.pdf.PdfDocument
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

object PdfExporter {
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
}
