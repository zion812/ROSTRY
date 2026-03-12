package com.rio.rostry.domain.farm.service

import com.rio.rostry.core.common.Result

/** Domain interface for pedigree image generation (visual tree export). */
interface PedigreeImageGenerator {
    suspend fun generateImage(birdId: String, depth: Int = 3): Result<String>
}
