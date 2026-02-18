package com.rio.rostry.domain.genetics

/**
 * definitions of standard Poultry Genes.
 * Based on real-world poultry genetics.
 */

enum class GeneType(val symbol: String, val isSexLinked: Boolean = false) {
    E_LOCUS("E"),       // Extended Black (Base Color)
    S_LOCUS("S", true), // Silver/Gold (Sex Linked)
    B_LOCUS("B", true), // Barring (Sex Linked)
    CO_LOCUS("Co"),     // Columbian (Restrictor)
    PG_LOCUS("Pg"),     // Pattern Gene (Organizes black pigment)
    ML_LOCUS("Ml"),     // Melanotic (Darkens pattern)
    MO_LOCUS("Mo"),     // Mottling
    BL_LOCUS("Bl"),     // Blue (Dilution)
    W_LOCUS("W"),       // Dominant White
    C_LOCUS("C")        // Recessive White (Color inhibitor)
}

interface Allele {
    val symbol: String
    val dominance: Int // Higher = more dominant
    val geneType: GeneType
}

// ==================== E LOCUS (Base Color) ====================
enum class AlleleE(override val symbol: String, override val dominance: Int) : Allele {
    EXTENDED("E", 5),       // Solid Black base
    BIRCHEN("ER", 4),       // Birchen (Silver/Gold chest)
    DOMINANT_WHEATEN("eWh", 3), // Wheaten
    WILD_TYPE("e+", 2),     // Black Breasted Red
    BROWN("eb", 1);         // Dark Brown (Partridge)

    override val geneType = GeneType.E_LOCUS
}

// ==================== S LOCUS (Silver/Gold) - Sex Linked ====================
enum class AlleleS(override val symbol: String, override val dominance: Int) : Allele {
    SILVER("S", 2),
    GOLD("s+", 1);

    override val geneType = GeneType.S_LOCUS
}

// ==================== B LOCUS (Barring) - Sex Linked ====================
enum class AlleleB(override val symbol: String, override val dominance: Int) : Allele {
    BARRED("B", 2),
    NON_BARRED("b+", 1);

    override val geneType = GeneType.B_LOCUS
}

// ==================== Co LOCUS (Columbian) ====================
enum class AlleleCo(override val symbol: String, override val dominance: Int) : Allele {
    COLUMBIAN("Co", 2),    // Restricts black to neck/tail
    NON_COLUMBIAN("co+", 1);

    override val geneType = GeneType.CO_LOCUS
}

// ==================== Pg LOCUS (Pattern Gene) ====================
enum class AllelePg(override val symbol: String, override val dominance: Int) : Allele {
    PATTERNED("Pg", 2),    // Organizing gene (Lacing/Penciling)
    NON_PATTERNED("pg+", 1);

    override val geneType = GeneType.PG_LOCUS
}

// ==================== Ml LOCUS (Melanotic) ====================
enum class AlleleMl(override val symbol: String, override val dominance: Int) : Allele {
    MELANOTIC("Ml", 2),    // Extends black (needed for Lacing/Double Lacing)
    NON_MELANOTIC("ml+", 1);

    override val geneType = GeneType.ML_LOCUS
}

// ==================== Mo LOCUS (Mottling) ====================
enum class AlleleMo(override val symbol: String, override val dominance: Int) : Allele {
    NON_MOTTLED("Mo+", 2), // Dominant wild type (No mottling)
    MOTTLED("mo", 1);      // Recessive mottling

    override val geneType = GeneType.MO_LOCUS
}

// ==================== Bl LOCUS (Blue) - Incomplete Dominant ====================
enum class AlleleBl(override val symbol: String, override val dominance: Int) : Allele {
    BLUE("Bl", 2),         // Dilutes black to blue. Bl/Bl = Splash. Bl/bl = Blue.
    BLACK("bl+", 1);

    override val geneType = GeneType.BL_LOCUS
}
