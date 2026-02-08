package com.rio.rostry.utils.export

import android.content.Context
import android.graphics.pdf.PdfDocument
import android.os.Environment
import android.provider.MediaStore
import com.rio.rostry.domain.pedigree.PedigreeTree
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Generates specific PDF certificates for Pedigree Trees.
 * Uses shared PedigreeRenderer for drawing content.
 */
@Singleton
class PedigreePdfGenerator @Inject constructor(
    private val context: Context
) {
    // A4 Landscape dimensions (842 x 595)
    private val pageWidth = 842
    private val pageHeight = 595

    fun generateAndSavePdf(birdName: String, tree: PedigreeTree): String? {
        val fileName = "Pedigree_${birdName.replace(" ", "_")}_${System.currentTimeMillis()}.pdf"
        
        val contentValues = android.content.ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
        }

        return try {
            val uri = context.contentResolver.insert(
                MediaStore.Files.getContentUri("external"),
                contentValues
            ) ?: throw IOException("Failed to create MediaStore entry")

            context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                val document = PdfDocument()
                val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()
                val page = document.startPage(pageInfo)
                
                // Use shared renderer
                val renderer = PedigreeRenderer(page.canvas, pageWidth, pageHeight)
                renderer.draw(tree)
                
                document.finishPage(page)
                document.writeTo(outputStream)
                document.close()
            }
            fileName
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
