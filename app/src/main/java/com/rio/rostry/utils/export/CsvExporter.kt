package com.rio.rostry.utils.export

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
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
}
