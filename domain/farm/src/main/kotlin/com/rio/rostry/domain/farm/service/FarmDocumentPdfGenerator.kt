package com.rio.rostry.domain.farm.service

import com.rio.rostry.core.common.Result

/** Domain interface for farm document PDF generation. */
interface FarmDocumentPdfGenerator {
    suspend fun generateFarmReport(userId: String): Result<String>
    suspend fun generateAssetReport(assetId: String): Result<String>
}
