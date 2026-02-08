package com.rio.rostry.utils.export

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.rio.rostry.data.repository.AssetDocumentation
import com.rio.rostry.data.repository.LifecycleTimelineEntry
import com.rio.rostry.ui.components.MediaItem
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AssetExportManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val pdfGenerator: AssetDocumentPdfGenerator
) {

    private val gson: Gson = GsonBuilder().setPrettyPrinting().create()

    suspend fun exportAssetPackage(
        documentation: AssetDocumentation,
        timeline: List<LifecycleTimelineEntry>,
        mediaItems: List<MediaItem> // All gathered media
    ): ExportResult = withContext(Dispatchers.IO) {
        val tempDir = File(context.cacheDir, "export_${System.currentTimeMillis()}")
        tempDir.mkdirs()

        try {
            // 1. Generate PDF
            val pdfFile = File(tempDir, "Asset_Report.pdf")
            // We need to modify PdfGenerator to write to file instead of MediaStore directly
            // For now, we'll let existing generator write to Downloads, then copy it.
            // Or better: Creating a specific method in Manager to handle PDF generation to file would be cleaner,
            // but to minimize changes, we'll use a temporary approach or overload the generator.
            // Let's implement a direct PDF generation here or assume we add a "generateToFile" method to PdfGenerator.
            // Use existing PDF generator but we need it to return a File or byte array.
            // The existing generator writes directly to MediaStore.
            // We should refactor PdfGenerator to support writing to a File.
             val pdfResult = pdfGenerator.generatePdfToFile(documentation, timeline, pdfFile)
             if (!pdfResult) return@withContext ExportResult.Error("Failed to generate PDF")

            // 2. Organization Metadata
            val metadataFile = File(tempDir, "metadata.json")
            val metadata = AssetExportMetadata(
                assetName = documentation.asset.name,
                assetId = documentation.asset.assetId,
                generatedAt = System.currentTimeMillis(),
                mediaCount = mediaItems.size
            )
            metadataFile.writeText(gson.toJson(metadata))
            
             // 3. Copy Media
            val mediaDir = File(tempDir, "media")
            if (mediaItems.isNotEmpty()) {
                mediaDir.mkdirs()
                mediaItems.forEachIndexed { index, item ->
                    try {
                        val sourceUri = Uri.parse(item.url)
                        // Resolve URI to File if possible or copy stream
                        val extension = getExtension(item.url)
                        // We use index and extension for uniqueness as MediaItem doesn't have a guaranteed unique ID
                        val destFile = File(mediaDir, "media_${index}.${extension}")
                        
                        context.contentResolver.openInputStream(sourceUri)?.use { input ->
                            destFile.outputStream().use { output ->
                                input.copyTo(output)
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }

            // 4. Create ZIP
            val timestamp = System.currentTimeMillis()
            val zipFileName = "AssetExport_${documentation.asset.name.replace(" ", "_")}_$timestamp.zip"
            // We want to write the ZIP to public "Downloads" directly
             val zipContentValues = android.content.ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, zipFileName)
                put(MediaStore.MediaColumns.MIME_TYPE, "application/zip")
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            }

            val zipUri = context.contentResolver.insert(MediaStore.Files.getContentUri("external"), zipContentValues)
                ?: return@withContext ExportResult.Error("Failed to create MediaStore entry for ZIP")

            context.contentResolver.openOutputStream(zipUri)?.use { outputStream ->
                ZipOutputStream(BufferedOutputStream(outputStream)).use { zipOut ->
                    zipDir(tempDir, tempDir, zipOut)
                }
            }
            
            return@withContext ExportResult.Success(zipFileName)

        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext ExportResult.Error(e.message ?: "Unknown error")
        } finally {
            tempDir.deleteRecursively()
        }
    }

    private fun zipDir(rootDir: File, sourceDir: File, out: ZipOutputStream) {
        val files = sourceDir.listFiles() ?: return
        val buffer = ByteArray(1024)

        for (file in files) {
            if (file.isDirectory) {
                zipDir(rootDir, file, out)
            } else {
                FileInputStream(file).use { fi ->
                    BufferedInputStream(fi).use { origin ->
                        val relativePath = file.path.substring(rootDir.path.length + 1)
                        val entry = ZipEntry(relativePath)
                        out.putNextEntry(entry)
                        var count: Int
                        while (origin.read(buffer, 0, 1024).also { count = it } != -1) {
                            out.write(buffer, 0, count)
                        }
                    }
                }
            }
        }
    }
    
    private fun getExtension(url: String): String {
        return if (url.contains(".jpg") || url.contains(".jpeg")) "jpg"
        else if (url.contains(".png")) "png"
        else if (url.contains(".mp4")) "mp4"
        else "dat"
    }

    data class AssetExportMetadata(
        val assetName: String,
        val assetId: String,
        val generatedAt: Long,
        val mediaCount: Int
    )

    sealed class ExportResult {
        data class Success(val fileName: String) : ExportResult()
        data class Error(val message: String) : ExportResult()
    }
}
