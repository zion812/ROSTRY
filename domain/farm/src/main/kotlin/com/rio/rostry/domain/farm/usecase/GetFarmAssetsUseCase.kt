package com.rio.rostry.domain.farm.usecase

import com.rio.rostry.core.model.FarmAsset
import kotlinx.coroutines.flow.Flow

/**
 * Use case for retrieving farm assets.
 * 
 * Phase 2: Domain and Data Decoupling
 * Requirement 4.1 - Domain modules define interfaces without implementation details
 */
interface GetFarmAssetsUseCase {
    /**
     * Get all farm assets for a farmer.
     * @param farmerId The farmer ID
     * @return Flow of farm assets
     */
    operator fun invoke(farmerId: String): Flow<List<FarmAsset>>
}
