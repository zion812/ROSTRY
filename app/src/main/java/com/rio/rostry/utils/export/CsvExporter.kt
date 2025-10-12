package com.rio.rostry.utils.export

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import android.content.Intent
import java.io.File
import java.io.FileOutputStream

object CsvExporter {
    fun writeCsv(context: Context, fileName: String, headers: List<String>, rows: List<List<String>>): Uri {
        val dir = File(context.filesDir, "reports")
        if (!dir.exists()) dir.mkdirs()
        val file = File(dir, fileName)
        FileOutputStream(file).use { fos ->
            val header = headers.joinToString(",") + "\n"
            fos.write(header.toByteArray())
            rows.forEach { r ->
                val line = r.joinToString(",") + "\n"
                fos.write(line.toByteArray())
            }
            fos.flush()
        }
        val authority = context.packageName + ".fileprovider"
        return FileProvider.getUriForFile(context, authority, file)
    }

    /**
     * Export KPI map to CSV with headers (Metric, Value, Unit) and optional date range header.
     * Returns a shareable Uri wrapped in Resource for error handling.
     */
    fun exportKpis(
        context: Context,
        kpis: Map<String, Any>,
        fileName: String,
        dateRange: Pair<Long, Long>? = null,
        units: Map<String, String>? = null
    ): com.rio.rostry.utils.Resource<Uri> {
        return try {
            val dir = File(context.filesDir, "reports")
            if (!dir.exists()) dir.mkdirs()
            val file = File(dir, fileName)
            FileOutputStream(file).use { fos ->
                // Header
                fos.write("Rostry KPI Export\n".toByteArray())
                dateRange?.let { (start, end) ->
                    val fmt = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
                    val hdr = "Date Range,${fmt.format(java.util.Date(start))},${fmt.format(java.util.Date(end))}\n"
                    fos.write(hdr.toByteArray())
                }
                fos.write("Metric,Value,Unit\n".toByteArray())
                kpis.forEach { (k, v) ->
                    val value = when (v) {
                        is Number -> v.toString()
                        is String -> v.replace(",", " ")
                        is Boolean -> v.toString()
                        else -> v.toString().replace(",", " ")
                    }
                    val unit = units?.get(k) ?: ""
                    fos.write("$k,$value,$unit\n".toByteArray())
                }
                fos.flush()
            }
            val authority = context.packageName + ".fileprovider"
            val uri = FileProvider.getUriForFile(context, authority, file)
            com.rio.rostry.utils.Resource.Success(uri)
        } catch (t: Throwable) {
            com.rio.rostry.utils.Resource.Error(t.message ?: "Failed to export CSV")
        }
    }

    fun shareCsv(context: Context, uri: Uri, subject: String = "Rostry Report", title: String = "Share CSV"): Intent {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/csv"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, subject)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        return Intent.createChooser(intent, title)
    }

    fun showExportNotification(context: Context, uri: Uri, title: String = "Report exported", text: String = "Tap to view") {
        val channelId = "reports"
        // best-effort channel creation
        val nm = NotificationManagerCompat.from(context)
        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.stat_notify_more)
            .setContentTitle(title)
            .setContentText(text)
            .setAutoCancel(true)
        nm.notify(301, builder.build())
    }
}
