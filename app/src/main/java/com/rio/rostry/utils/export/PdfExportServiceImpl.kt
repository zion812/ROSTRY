package com.rio.rostry.utils.export

import android.content.Context
import android.net.Uri
import com.rio.rostry.domain.monitoring.service.PdfExportService
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of PdfExportService that delegates to PdfExporter.
 * This bridges the domain interface with the app-module PdfExporter utility.
 */
@Singleton
class PdfExportServiceImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : PdfExportService {

    override suspend fun writeReport(
        fileName: String,
        docTitle: String,
        sections: List<PdfExportService.TableSection>
    ): Uri {
        // Map domain TableSection to PdfExporter.TableSection
        val exporterSections = sections.map { section ->
            PdfExporter.TableSection(
                title = section.title,
                headers = section.headers,
                rows = section.rows
            )
        }
        return PdfExporter.writeReport(
            context = context,
            fileName = fileName,
            docTitle = docTitle,
            coverBitmap = null,
            sections = exporterSections
        )
    }
}
