package com.rio.rostry.domain.farm.usecase

import com.rio.rostry.core.common.Result

/** Domain interface for verified lineage badge determination. */
interface VerifiedLineageBadgeUseCase {
    suspend fun getBadgeLevel(assetId: String): Result<String>
    suspend fun isLineageVerified(assetId: String): Boolean
}
