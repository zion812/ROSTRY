package com.rio.rostry.workers

import android.content.Context
import android.graphics.pdf.PdfDocument
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class CertificateExportWorker(
    private val appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val transferId = inputData.getString(KEY_TRANSFER_ID) ?: return@withContext Result.failure()
        val certificateJson = inputData.getString(KEY_CERT_JSON) ?: "{}"
        try {
            val file = generatePdf(transferId, certificateJson)
            val uri = FileProvider.getUriForFile(appContext, "${appContext.packageName}.fileprovider", file)
            val out = Data.Builder()
                .putString(KEY_URI, uri.toString())
                .build()
            Result.success(out)
        } catch (e: Exception) {
            Result.failure()
        }
    }

    private fun generatePdf(transferId: String, json: String): File {
        val doc = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 @ 72dpi approx
        val page = doc.startPage(pageInfo)
        val canvas = page.canvas
        val paint = android.graphics.Paint().apply { textSize = 12f }
        var y = 40f
        canvas.drawText("ROSTRY Transfer Certificate", 40f, y, paint)
        y += 24f
        canvas.drawText("Transfer ID: $transferId", 40f, y, paint)
        y += 18f
        // Simple JSON dump (first ~1000 chars)
        val text = json.take(1000)
        val lines = text.chunked(80)
        lines.forEach {
            y += 16f
            canvas.drawText(it, 40f, y, paint)
        }
        doc.finishPage(page)
        val outDir = File(appContext.filesDir, "certs").apply { mkdirs() }
        val outFile = File(outDir, "certificate_${transferId}.pdf")
        FileOutputStream(outFile).use { fos -> doc.writeTo(fos) }
        doc.close()
        return outFile
    }

    companion object {
        const val KEY_TRANSFER_ID = "transferId"
        const val KEY_CERT_JSON = "certificateJson"
        const val KEY_URI = "uri"
    }
}
