package com.rio.rostry.domain.farm.service

import com.rio.rostry.core.common.Result

/**
 * Domain interface for pedigree PDF certificate generation.
 *
 * Generates A4 landscape PDF certificates containing the full
 * pedigree tree of a bird, suitable for printing and sharing.
 */
interface PedigreePdfGenerator {

    /**
     * Generate and save a PDF for a bird's pedigree.
     *
     * @param birdName Display name for the certificate header.
     * @param birdId Product ID used to fetch the pedigree tree.
     * @return The file name of the saved PDF, or null on failure.
     */
    suspend fun generateAndSavePdf(birdName: String, birdId: String): Result<String>
}
