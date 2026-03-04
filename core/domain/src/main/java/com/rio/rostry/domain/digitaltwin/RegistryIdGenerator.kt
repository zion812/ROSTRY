package com.rio.rostry.domain.digitaltwin

import java.util.UUID

/**
 * 🆔 RegistryIdGenerator — Generates Structured Aseel Digital Registry IDs
 *
 * Format: RST-{STRAIN}-{COLOR}-{SEQ}
 *
 * Examples:
 *   RST-ASL-RZA-001  → Aseel Reza #001
 *   RST-ASL-KLG-042  → Aseel Kulangi #042
 *   RST-ASL-MDR-007  → Aseel Madras #007
 *   RST-ASL-MXD-100  → Aseel Mixed #100
 *
 * This is the "First structured Aseel Genetic Digital Registry for
 * Andhra & Telangana premium markets" USP identifier.
 */
object RegistryIdGenerator {

    // Strain abbreviations
    private val STRAIN_CODES = mapOf(
        "KULANGI" to "KLG",
        "MADRAS" to "MDR",
        "MALAY" to "MLY",
        "REZA" to "RZA",
        "MIANWALI" to "MWL",
        "SINDHI" to "SND",
        "ASEEL" to "ASL",
        "MIXED" to "MXD",
        "UNKNOWN" to "UNK",
        // Regional variants
        "SOLAN" to "SLN",
        "JAVAN" to "JVN",
        "HYDERABADI" to "HYD",
        "RAJASTHANI" to "RJS"
    )

    // Color/LocalBirdType abbreviations
    private val COLOR_CODES = mapOf(
        "KAKI" to "BLK",    // Black
        "SETHU" to "WHT",   // White
        "DEGA" to "RED",    // Red/Eagle
        "SAVALA" to "SVL",  // Black-necked
        "PARLA" to "PRL",   // B&W
        "KOKKIRAYI" to "KKR", // Multi
        "NEMALI" to "NML",  // Yellow/Peacock
        "KOWJU" to "KWJ",   // Tri-color
        "MAILA" to "MLA",   // Red-Ash
        "POOLA" to "POL",   // Feather blend
        "PINGALA" to "PNG",  // White-wing
        "NALLA_BORA" to "NBR", // Black-breast
        "MUNGISA" to "MGS",  // Mongoose
        "ABRASU" to "ABR",   // Golden
        "GERUVA" to "GRV",   // White-Red
        "UNKNOWN" to "UNK"
    )

    /**
     * Generate a registry ID for a new bird.
     *
     * @param strainType The strain/sub-breed (e.g., "Kulangi", "Reza")
     * @param localColorCode The local color classification (e.g., "KAKI", "DEGA")
     * @param sequenceNumber The sequential number for this owner's birds (1-999)
     * @return Formatted registry ID (e.g., "RST-ASL-RZA-001")
     */
    fun generate(
        strainType: String?,
        localColorCode: String?,
        sequenceNumber: Int
    ): String {
        val strain = STRAIN_CODES[strainType?.uppercase()] ?: "ASL"
        val color = COLOR_CODES[localColorCode?.uppercase()] ?: "UNK"
        val seq = sequenceNumber.toString().padStart(3, '0')

        return "RST-$strain-$color-$seq"
    }

    /**
     * Generate with auto-incrementing sequence from existing count.
     */
    fun generateNext(
        strainType: String?,
        localColorCode: String?,
        existingCount: Int
    ): String {
        return generate(strainType, localColorCode, existingCount + 1)
    }

    /**
     * Parse a registry ID back into components.
     */
    fun parse(registryId: String): RegistryComponents? {
        val parts = registryId.split("-")
        if (parts.size != 4 || parts[0] != "RST") return null

        return RegistryComponents(
            prefix = parts[0],
            strainCode = parts[1],
            colorCode = parts[2],
            sequenceNumber = parts[3].toIntOrNull() ?: 0
        )
    }

    data class RegistryComponents(
        val prefix: String,
        val strainCode: String,
        val colorCode: String,
        val sequenceNumber: Int
    )
}
