package com.rio.rostry.domain.farm.service

import com.rio.rostry.core.common.Result

/** Domain interface for asset export management (CSV, PDF, etc.). */
interface AssetExportManager {
    suspend fun exportAssetData(assetId: String, format: String = "csv"): Result<String>
    suspend fun exportFarmData(userId: String, format: String = "csv"): Result<String>
}
