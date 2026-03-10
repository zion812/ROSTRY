package com.rio.rostry.data.farm.repository

import com.rio.rostry.core.model.Result
import com.rio.rostry.domain.farm.repository.PublicBirdRepository
import com.rio.rostry.domain.farm.repository.PublicBirdView
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Compile-safe public bird repository stub used during modular migration.
 */
@Singleton
class PublicBirdRepositoryImpl @Inject constructor() : PublicBirdRepository {
    override suspend fun lookupBird(birdCode: String): Result<PublicBirdView> {
        return Result.Success(
            PublicBirdView.Restricted(
                birdCode = birdCode,
                message = "Public lookup is temporarily limited during migration"
            )
        )
    }
}
