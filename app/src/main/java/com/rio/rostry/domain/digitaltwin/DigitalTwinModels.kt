package com.rio.rostry.domain.digitaltwin

import androidx.compose.ui.graphics.Color

/**
 * Aseel Digital Twin 2.0 - Core Identity Models
 *
 * Moving away from simple "Breed Names" to a structure-first identity system.
 */

data class DigitalTwinProfile(
    val structure: StructureProfile = StructureProfile(),
    val color: ColorProfile = ColorProfile(),
    val ageStage: AgeStage = AgeStage.ADULT,
    val asiScore: AseelStructuralIndex = AseelStructuralIndex(0, emptyList())
)

/**
 * Defines the physical chassis of the bird.
 * All values are normalized 0.0 - 1.0 floats relative to the "Ideal Aseel" standard.
 */
data class StructureProfile(
    val neckLength: Float = 0.5f,      // 0.0=Short (Cornish) -> 1.0=Giraffe (Modern Game)
    val legLength: Float = 0.5f,       // 0.0=Creeper -> 1.0=Stilt
    val boneThickness: Float = 0.5f,   // 0.0=Fine (Leghorn) -> 1.0=Massive (Malay)
    val chestDepth: Float = 0.5f,      // 0.0=Flat -> 1.0=Deep Keel
    val featherTightness: Float = 0.5f,// 0.0=Fluffy (Cochin) -> 1.0=Hard/Armored (Aseel)
    val tailCarriage: Float = 0.5f,    // 0.0=Horizontal -> 1.0=Vertical/Squirrel
    val postureAngle: Float = 0.5f,    // 0.0=Horizontal -> 1.0=Vertical Upright
    val bodyWidth: Float = 0.5f        // 0.0=Narrow -> 1.0=Broad/Heart-shaped
)

/**
 * Defines the "Color DNA" to separate structural identity from plumage.
 */
data class ColorProfile(
    val baseType: BaseColorType = BaseColorType.MIXED,
    val distribution: DistributionMap = DistributionMap.MULTI_PATCH,
    val sheen: SheenLevel = SheenLevel.GLOSS,
    
    // Detailed overrides
    val primaryColorHex: Long = 0xFF212121, // Body
    val secondaryColorHex: Long = 0xFFB71C1C, // Neck/Saddle
    val accentColorHex: Long = 0xFF1B5E20 // Tail/Wing
)

enum class BaseColorType {
    BLACK, RED, GOLDEN, WHITE, WHEATEN, BLUE, MIXED
}

enum class DistributionMap {
    SOLID,          // Uniform
    NECK_DOMINANT,  // Colored neck (Hackle)
    BODY_DOMINANT,  // Colored body
    WING_DOMINANT,  // Colored wing bays
    MULTI_PATCH,    // Random patching
    FEATHER_BLEND   // Intricate lacing/penciling
}

enum class SheenLevel {
    MATTE,
    GLOSS,
    IRIDESCENT,  // Beetle green/purple
    METALLIC
}

/**
 * Maturity Engine: Defines the biological stage of the bird.
 */
enum class AgeStage {
    CHICK,       // 0-4 weeks: Round, fuzzy, no comb
    GROWER,      // 1-4 months: Lanky, thin feathers, developing
    SUB_ADULT,   // 4-8 months: Frame filling out, hackles starting
    ADULT,       // 8-18 months: Prime structure, full plumage
    MATURE_ADULT // 18+ months: Heavy bone, thick neck, spurs
}

/**
 * The calculated score (0-100) representing structural quality.
 */
data class AseelStructuralIndex(
    val score: Int,
    val validationWarnings: List<String> = emptyList()
)
