package com.rio.rostry.domain.farm.repository

import com.rio.rostry.core.model.Result

/**
 * Repository contract for public bird lookup operations.
 * 
 * Phase 2: Domain and Data Decoupling
 * Task 11.4.3 - Farm Domain repository migration
 */
interface PublicBirdRepository {
    /**
     * Look up a bird by its unique bird code.
     * @param birdCode The bird code to look up
     * @return Result containing the public bird view or error
     */
    suspend fun lookupBird(birdCode: String): Result<PublicBirdView>
}

/**
 * Sealed class representing different views of a public bird.
 */
sealed class PublicBirdView {
    /**
     * Full bird details are available.
     * @param product The product entity with full details
     */
    data class Full(val product: com.rio.rostry.core.model.Product) : PublicBirdView()

    /**
     * Bird exists but details are restricted.
     * @param birdCode The bird code
     * @param message The restriction message
     */
    data class Restricted(val birdCode: String, val message: String) : PublicBirdView()
}
