package com.rio.rostry.domain.monitoring.repository

import android.net.Uri
import com.rio.rostry.core.common.Result

/**
 * Domain interface for generating farm reports.
 * Migrated from app module as part of Phase 1 repository migration.
 */
interface ReportGenerationRepository {
    suspend fun generateMonthlyReport(
        farmerId: String,
        farmerName: String,
        month: Int,
        year: Int
    ): Result<Uri>
}
