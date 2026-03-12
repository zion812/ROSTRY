package com.rio.rostry.domain.social.service

import com.rio.rostry.core.common.Result

/**
 * Domain interface for showcase card generation and sharing.
 *
 * Creates premium shareable images featuring a bird's photo,
 * breed, pedigree badge, and awards for social sharing.
 */
interface ShowcaseCardGenerator {

    /**
     * Generate a showcase card for a bird.
     *
     * @param birdId Product ID of the bird.
     * @param config Configuration for which sections to show.
     * @return File path to the generated card image, or error.
     */
    suspend fun generateCard(birdId: String, config: Map<String, Any>): Result<String>
}
