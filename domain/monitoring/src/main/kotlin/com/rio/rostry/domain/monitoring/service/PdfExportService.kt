package com.rio.rostry.domain.monitoring.service

import android.net.Uri

/**
 * Domain service interface for PDF report generation.
 * The actual implementation (PdfExporter) lives in the app module.
 * This interface allows data:monitoring to generate reports without depending on app module.
 */
interface PdfExportService {

    data class TableSection(
        val title: String,
        val headers: List<String>,
        val rows: List<List<String>>
    )

    suspend fun writeReport(
        fileName: String,
        docTitle: String,
        sections: List<TableSection>
    ): Uri
}
