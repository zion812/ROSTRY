package com.rio.rostry.domain.farm.usecase

import com.rio.rostry.core.model.FarmAsset
import com.rio.rostry.core.model.Result

/**
 * Use case for creating a farm asset.
 * 
 * Phase 2: Domain and Data Decoupling
 * Requirement 4.1 - Domain modules define interfaces without implementation details
 */
interface CreateFarmAssetUseCase {
    /**
     * Create a new farm asset.
     * @param request The asset creation request
     * @return Result containing the created asset or error
     */
    suspend operator fun invoke(request: CreateFarmAssetRequest): Result<FarmAsset>
}

/**
 * Request data for creating a farm asset.
 */
data class CreateFarmAssetRequest(
    val farmerId: String,
    val assetType: String,
    val breed: String?,
    val gender: String?,
    val birthDate: String?,
    val location: String?
)
