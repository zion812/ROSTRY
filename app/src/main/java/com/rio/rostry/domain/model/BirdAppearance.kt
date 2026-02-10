package com.rio.rostry.domain.model

import androidx.compose.ui.graphics.Color

/**
 * BGMI-style Bird Appearance System
 *
 * Each bird has a fully customizable appearance composed of individual
 * body parts. Each part has a style variant + color. Appearance can be
 * auto-derived from breed or manually customized by Enthusiast users.
 */
data class BirdAppearance(
    val comb: CombStyle = CombStyle.SINGLE,
    val combColor: PartColor = PartColor.RED,
    val beak: BeakStyle = BeakStyle.MEDIUM,
    val beakColor: PartColor = PartColor.HORN,
    val chest: PlumagePattern = PlumagePattern.SOLID,
    val chestColor: PartColor = PartColor.WHITE,
    val back: BackStyle = BackStyle.SMOOTH,
    val backColor: PartColor = PartColor.WHITE,
    val crown: CrownStyle = CrownStyle.CLEAN,
    val crownColor: PartColor = PartColor.WHITE,
    val wings: WingStyle = WingStyle.FOLDED,
    val wingColor: PartColor = PartColor.WHITE,
    val wingPattern: PlumagePattern = PlumagePattern.SOLID,
    val tail: TailStyle = TailStyle.SHORT,
    val tailColor: PartColor = PartColor.BLACK,
    val legs: LegStyle = LegStyle.CLEAN,
    val legColor: PartColor = PartColor.YELLOW,
    val joints: JointStyle = JointStyle.STANDARD,
    val nails: NailStyle = NailStyle.SHORT,
    val nailColor: PartColor = PartColor.HORN,
    val eye: EyeColor = EyeColor.ORANGE,
    val wattle: WattleStyle = WattleStyle.MEDIUM,
    val wattleColor: PartColor = PartColor.RED,
    val earLobe: EarLobeColor = EarLobeColor.RED,
    val bodySize: BodySize = BodySize.MEDIUM,
    val isMale: Boolean = true
)

// ==================== COMB ====================

enum class CombStyle {
    SINGLE,     // Upright serrated (most common - Leghorn, RIR)
    ROSE,       // Flat, fleshy, bumpy (Wyandotte, Hamburg)
    PEA,        // Three low ridges (Aseel, Brahma, Cornish)
    WALNUT,     // Round bumpy (Silkie, Malay)
    BUTTERCUP,  // Cup-shaped with points (Sicilian Buttercup)
    V_SHAPED,   // Two horn-like points (Houdan, Sultan)
    STRAWBERRY, // Low rounded (Malay derivative)
    NONE        // For young chicks
}

// ==================== BEAK ====================

enum class BeakStyle {
    SHORT,      // Compact (Aseel, Shamo)
    MEDIUM,     // Standard (most breeds)
    LONG,       // Extended (Leghorn, Minorca)
    HOOKED,     // Downward curve (game bird trait)
    CURVED      // Slight curve (ornamental)
}

// ==================== PLUMAGE PATTERN ====================

enum class PlumagePattern {
    SOLID,      // Single uniform color
    SPECKLED,   // Random spots (Speckled Sussex)
    LACED,      // Feather edge in different color (Wyandotte)
    BARRED,     // Alternating light/dark stripes (Plymouth Rock)
    MOTTLED,    // Irregular patches (Ancona)
    PENCILED,   // Fine concentric lines (Partridge)
    COLUMBIAN,  // White body, dark neck/tail (Light Brahma)
    SPLASH,     // Random blue-gray splashes (Blue Cochin)
    BUFF,       // Golden-tan solid (Buff Orpington)
    DOUBLE_LACED // Double ring pattern (Barnevelder)
}

// ==================== BACK ====================

enum class BackStyle {
    SMOOTH,     // Sleek, close feathering
    HACKLE,     // Flowing neck/saddle hackle feathers
    SADDLE,     // Prominent saddle feathers (rooster)
    CUSHION     // Rounded, puffy back (Cochin)
}

// ==================== CROWN ====================

enum class CrownStyle {
    CLEAN,      // No head feathers
    CREST,      // Small tuft (Appenzeller)
    POLISH,     // Full round pompom (Polish)
    MOHAWK,     // Upright crest (Houdan)
    MUFF        // Side whiskers / muffs (Ameraucana)
}

// ==================== WINGS ====================

enum class WingStyle {
    FOLDED,     // Tucked against body (normal)
    SPREAD,     // Slightly opened
    CLIPPED,    // Trimmed flight feathers
    ANGEL,      // Drooping wings (Serama trait)
    TIGHT       // Very tight to body (game bird)
}

// ==================== TAIL ====================

enum class TailStyle {
    SHORT,      // Minimal tail (Araucana, Cornish)
    SICKLE,     // Standard rooster sickle feathers
    LONG_SICKLE, // Exaggerated long sickles (Phoenix, Onagadori)
    FAN,        // Wide fan tail (Fantail pigeon-like)
    SQUIRREL,   // Forward-curving (Japanese Bantam)
    WHIP,       // Long, thin (Sumatra)
    NONE        // Rumpless (Araucana)
}

// ==================== LEGS ====================

enum class LegStyle {
    CLEAN,      // Bare shanks (most breeds)
    FEATHERED,  // Light feathering (Brahma, Cochin)
    HEAVILY_FEATHERED, // Full leg muffs (Silkie, Sultan)
    SPURRED,    // Prominent spurs (game birds, Aseel)
    BOOTED      // Full vulture hocks + feet feathers (d'Uccle)
}

// ==================== JOINTS ====================

enum class JointStyle {
    STANDARD,    // Normal proportions
    HEAVY,       // Thick, stocky (Cornish, Malay)
    SLIM,        // Fine-boned, elegant (Leghorn)
    LONG,        // Long shanks (Modern Game)
    SHORT        // Compact, low-set (Japanese Bantam)
}

// ==================== NAILS / SPURS ====================

enum class NailStyle {
    SHORT,       // Standard trimmed
    LONG_SPUR,   // Single spur (most roosters)
    DOUBLE_SPUR, // Rare double spur
    CURVED,      // Curved fighting spur (Aseel)
    NONE         // Young chicks / hens
}

// ==================== EYE ====================

enum class EyeColor {
    ORANGE,     // Most common (RIR, Leghorn)
    RED,        // Game birds, Aseel
    PEARL,      // Light gray-white (Silkie)
    BAY,        // Dark reddish-brown (Wyandotte)
    DARK,       // Nearly black (Sumatra, Ayam Cemani)
    YELLOW      // Bright yellow (Modern Game)
}

// ==================== WATTLE ====================

enum class WattleStyle {
    NONE,       // Absent (young / some breeds)
    SMALL,      // Minimal (Ameraucana, Silkie)
    MEDIUM,     // Standard
    LARGE,      // Prominent (Leghorn, Minorca)
    PENDULOUS   // Very elongated (Spanish)
}

// ==================== EAR LOBE ====================

enum class EarLobeColor {
    RED,        // Most breeds (brown egg layers)
    WHITE,      // White egg layers (Leghorn, Minorca)
    BLUE,       // Silkie, Araucana
    TURQUOISE   // Ameraucana
}

// ==================== BODY SIZE ====================

enum class BodySize {
    TINY,       // Serama (smallest)
    BANTAM,     // Small (Japanese Bantam, d'Uccle)
    SMALL,      // Light breeds (Leghorn)
    MEDIUM,     // Standard breeds (RIR, Plymouth Rock)
    LARGE,      // Heavy breeds (Brahma, Cochin)
    XLARGE      // Giant breeds (Jersey Giant)
}

// ==================== PART COLORS ====================

enum class PartColor(val color: Color) {
    // Body colors
    WHITE(Color(0xFFFAFAFA)),
    CREAM(Color(0xFFFFF8E1)),
    BUFF(Color(0xFFFFCC80)),
    GOLD(Color(0xFFFFB300)),
    RED(Color(0xFFE53935)),
    DARK_RED(Color(0xFFB71C1C)),
    MAHOGANY(Color(0xFF6D4C41)),
    BROWN(Color(0xFF8D6E63)),
    DARK_BROWN(Color(0xFF4E342E)),
    CHOCOLATE(Color(0xFF3E2723)),
    BLACK(Color(0xFF212121)),
    BLUE(Color(0xFF78909C)),        // Blue-gray (Andalusian)
    SPLASH_BLUE(Color(0xFFB0BEC5)), // Light splash
    SILVER(Color(0xFFBDBDBD)),
    GRAY(Color(0xFF9E9E9E)),
    LAVENDER(Color(0xFFCE93D8)),    // Lavender Orpington

    // Leg/beak specific
    YELLOW(Color(0xFFFFB74D)),
    SLATE(Color(0xFF607D8B)),
    WILLOW(Color(0xFF8BC34A)),      // Green-yellow
    WHITE_PINK(Color(0xFFFFCDD2)),
    HORN(Color(0xFFD7CCC8)),        // Horn-colored beak
    DARK_HORN(Color(0xFF795548)),

    // Special
    GREEN_BLACK(Color(0xFF1B5E20)), // Beetle-green sheen
    COPPER(Color(0xFFBF360C)),      // Copper neck
    BIRCHEN(Color(0xFF455A64)),     // Silver-black birchen
    WHEATEN(Color(0xFFFFE082))      // Wheaten pattern
}

// ==================== BREED -> APPEARANCE MAPPER ====================

/**
 * Auto-derives a BirdAppearance from breed name and gender.
 * Maps to common real-world breed standards.
 */
fun deriveAppearanceFromBreed(breed: String?, gender: String?, ageWeeks: Int): BirdAppearance {
    val isMale = gender?.equals("Male", ignoreCase = true) ?: true
    val isChick = ageWeeks < 4

    // Chick default
    if (isChick) {
        return BirdAppearance(
            comb = CombStyle.NONE,
            combColor = PartColor.RED,
            beak = BeakStyle.SHORT,
            beakColor = PartColor.HORN,
            chest = PlumagePattern.SOLID,
            chestColor = PartColor.CREAM,
            back = BackStyle.SMOOTH,
            backColor = PartColor.CREAM,
            crown = CrownStyle.CLEAN,
            wings = WingStyle.FOLDED,
            wingColor = PartColor.CREAM,
            tail = TailStyle.NONE,
            tailColor = PartColor.CREAM,
            legs = LegStyle.CLEAN,
            legColor = PartColor.YELLOW,
            joints = JointStyle.SHORT,
            nails = NailStyle.NONE,
            eye = EyeColor.DARK,
            wattle = WattleStyle.NONE,
            earLobe = EarLobeColor.RED,
            bodySize = BodySize.TINY,
            isMale = isMale
        )
    }

    return when (breed?.lowercase()?.trim()) {
        // ---- Indian Game / Fighter Breeds ----
        "aseel", "asil", "aseel fighter" -> BirdAppearance(
            comb = CombStyle.PEA,
            combColor = PartColor.RED,
            beak = BeakStyle.SHORT,
            beakColor = PartColor.HORN,
            chest = PlumagePattern.SOLID,
            chestColor = if (isMale) PartColor.RED else PartColor.BROWN,
            back = BackStyle.SMOOTH,
            backColor = if (isMale) PartColor.DARK_RED else PartColor.BROWN,
            crown = CrownStyle.CLEAN,
            wings = WingStyle.TIGHT,
            wingColor = if (isMale) PartColor.RED else PartColor.BROWN,
            tail = TailStyle.SHORT,
            tailColor = PartColor.BLACK,
            legs = LegStyle.SPURRED,
            legColor = PartColor.YELLOW,
            joints = JointStyle.HEAVY,
            nails = NailStyle.CURVED,
            nailColor = PartColor.HORN,
            eye = EyeColor.RED,
            wattle = WattleStyle.SMALL,
            wattleColor = PartColor.RED,
            earLobe = EarLobeColor.RED,
            bodySize = BodySize.LARGE,
            isMale = isMale
        )

        "kadaknath", "kadak nath" -> BirdAppearance(
            comb = CombStyle.SINGLE,
            combColor = PartColor.DARK_RED,
            beak = BeakStyle.MEDIUM,
            beakColor = PartColor.BLACK,
            chest = PlumagePattern.SOLID,
            chestColor = PartColor.BLACK,
            back = BackStyle.SMOOTH,
            backColor = PartColor.GREEN_BLACK,
            crown = CrownStyle.CLEAN,
            wings = WingStyle.FOLDED,
            wingColor = PartColor.GREEN_BLACK,
            tail = if (isMale) TailStyle.SICKLE else TailStyle.SHORT,
            tailColor = PartColor.GREEN_BLACK,
            legs = LegStyle.CLEAN,
            legColor = PartColor.SLATE,
            joints = JointStyle.STANDARD,
            nails = if (isMale) NailStyle.LONG_SPUR else NailStyle.SHORT,
            eye = EyeColor.DARK,
            wattle = WattleStyle.MEDIUM,
            wattleColor = PartColor.DARK_RED,
            earLobe = EarLobeColor.RED,
            bodySize = BodySize.MEDIUM,
            isMale = isMale
        )

        // ---- American / Dual Purpose ----
        "rhode island red", "rir", "rhode island" -> BirdAppearance(
            comb = CombStyle.SINGLE,
            combColor = PartColor.RED,
            beak = BeakStyle.MEDIUM,
            beakColor = PartColor.HORN,
            chest = PlumagePattern.SOLID,
            chestColor = PartColor.MAHOGANY,
            back = BackStyle.SMOOTH,
            backColor = PartColor.MAHOGANY,
            crown = CrownStyle.CLEAN,
            wings = WingStyle.FOLDED,
            wingColor = PartColor.MAHOGANY,
            wingPattern = PlumagePattern.SOLID,
            tail = if (isMale) TailStyle.SICKLE else TailStyle.SHORT,
            tailColor = PartColor.GREEN_BLACK,
            legs = LegStyle.CLEAN,
            legColor = PartColor.YELLOW,
            joints = JointStyle.STANDARD,
            nails = if (isMale) NailStyle.LONG_SPUR else NailStyle.SHORT,
            eye = EyeColor.ORANGE,
            wattle = WattleStyle.MEDIUM,
            earLobe = EarLobeColor.RED,
            bodySize = BodySize.MEDIUM,
            isMale = isMale
        )

        "plymouth rock", "barred rock", "barred plymouth rock" -> BirdAppearance(
            comb = CombStyle.SINGLE,
            combColor = PartColor.RED,
            beak = BeakStyle.MEDIUM,
            beakColor = PartColor.HORN,
            chest = PlumagePattern.BARRED,
            chestColor = PartColor.GRAY,
            back = BackStyle.SMOOTH,
            backColor = PartColor.GRAY,
            crown = CrownStyle.CLEAN,
            wings = WingStyle.FOLDED,
            wingColor = PartColor.GRAY,
            wingPattern = PlumagePattern.BARRED,
            tail = if (isMale) TailStyle.SICKLE else TailStyle.SHORT,
            tailColor = PartColor.BLACK,
            legs = LegStyle.CLEAN,
            legColor = PartColor.YELLOW,
            joints = JointStyle.STANDARD,
            nails = if (isMale) NailStyle.LONG_SPUR else NailStyle.SHORT,
            eye = EyeColor.BAY,
            wattle = WattleStyle.MEDIUM,
            earLobe = EarLobeColor.RED,
            bodySize = BodySize.LARGE,
            isMale = isMale
        )

        // ---- British Breeds ----
        "buff orpington", "orpington" -> BirdAppearance(
            comb = CombStyle.SINGLE,
            combColor = PartColor.RED,
            beak = BeakStyle.MEDIUM,
            beakColor = PartColor.HORN,
            chest = PlumagePattern.BUFF,
            chestColor = PartColor.BUFF,
            back = BackStyle.CUSHION,
            backColor = PartColor.BUFF,
            crown = CrownStyle.CLEAN,
            wings = WingStyle.FOLDED,
            wingColor = PartColor.BUFF,
            tail = if (isMale) TailStyle.SICKLE else TailStyle.SHORT,
            tailColor = PartColor.BUFF,
            legs = LegStyle.CLEAN,
            legColor = PartColor.WHITE_PINK,
            joints = JointStyle.HEAVY,
            nails = if (isMale) NailStyle.LONG_SPUR else NailStyle.SHORT,
            eye = EyeColor.ORANGE,
            wattle = WattleStyle.MEDIUM,
            earLobe = EarLobeColor.RED,
            bodySize = BodySize.LARGE,
            isMale = isMale
        )

        // ---- Asian Breeds ----
        "brahma", "light brahma" -> BirdAppearance(
            comb = CombStyle.PEA,
            combColor = PartColor.RED,
            beak = BeakStyle.SHORT,
            beakColor = PartColor.HORN,
            chest = PlumagePattern.COLUMBIAN,
            chestColor = PartColor.WHITE,
            back = BackStyle.CUSHION,
            backColor = PartColor.WHITE,
            crown = CrownStyle.CLEAN,
            wings = WingStyle.FOLDED,
            wingColor = PartColor.WHITE,
            tail = if (isMale) TailStyle.SICKLE else TailStyle.SHORT,
            tailColor = PartColor.BLACK,
            legs = LegStyle.FEATHERED,
            legColor = PartColor.YELLOW,
            joints = JointStyle.HEAVY,
            nails = if (isMale) NailStyle.LONG_SPUR else NailStyle.SHORT,
            eye = EyeColor.BAY,
            wattle = WattleStyle.SMALL,
            earLobe = EarLobeColor.RED,
            bodySize = BodySize.XLARGE,
            isMale = isMale
        )

        "silkie", "silk" -> BirdAppearance(
            comb = CombStyle.WALNUT,
            combColor = PartColor.DARK_RED,
            beak = BeakStyle.SHORT,
            beakColor = PartColor.SLATE,
            chest = PlumagePattern.SOLID,
            chestColor = PartColor.WHITE,
            back = BackStyle.CUSHION,
            backColor = PartColor.WHITE,
            crown = CrownStyle.POLISH,
            crownColor = PartColor.WHITE,
            wings = WingStyle.ANGEL,
            wingColor = PartColor.WHITE,
            tail = TailStyle.SHORT,
            tailColor = PartColor.WHITE,
            legs = LegStyle.HEAVILY_FEATHERED,
            legColor = PartColor.SLATE,
            joints = JointStyle.SHORT,
            nails = NailStyle.SHORT,
            eye = EyeColor.PEARL,
            wattle = WattleStyle.SMALL,
            wattleColor = PartColor.DARK_RED,
            earLobe = EarLobeColor.BLUE,
            bodySize = BodySize.BANTAM,
            isMale = isMale
        )

        // ---- Egg Layers ----
        "leghorn", "white leghorn" -> BirdAppearance(
            comb = CombStyle.SINGLE,
            combColor = PartColor.RED,
            beak = BeakStyle.LONG,
            beakColor = PartColor.YELLOW,
            chest = PlumagePattern.SOLID,
            chestColor = PartColor.WHITE,
            back = BackStyle.SMOOTH,
            backColor = PartColor.WHITE,
            crown = CrownStyle.CLEAN,
            wings = WingStyle.FOLDED,
            wingColor = PartColor.WHITE,
            tail = if (isMale) TailStyle.LONG_SICKLE else TailStyle.SHORT,
            tailColor = PartColor.WHITE,
            legs = LegStyle.CLEAN,
            legColor = PartColor.YELLOW,
            joints = JointStyle.SLIM,
            nails = if (isMale) NailStyle.LONG_SPUR else NailStyle.SHORT,
            eye = EyeColor.ORANGE,
            wattle = WattleStyle.LARGE,
            earLobe = EarLobeColor.WHITE,
            bodySize = BodySize.SMALL,
            isMale = isMale
        )

        // ---- Ornamental ----
        "polish" -> BirdAppearance(
            comb = CombStyle.V_SHAPED,
            combColor = PartColor.RED,
            beak = BeakStyle.MEDIUM,
            beakColor = PartColor.HORN,
            chest = PlumagePattern.SOLID,
            chestColor = PartColor.WHITE,
            back = BackStyle.SMOOTH,
            backColor = PartColor.WHITE,
            crown = CrownStyle.POLISH,
            crownColor = PartColor.WHITE,
            wings = WingStyle.FOLDED,
            wingColor = PartColor.WHITE,
            tail = if (isMale) TailStyle.SICKLE else TailStyle.SHORT,
            tailColor = PartColor.WHITE,
            legs = LegStyle.CLEAN,
            legColor = PartColor.SLATE,
            joints = JointStyle.SLIM,
            nails = NailStyle.SHORT,
            eye = EyeColor.ORANGE,
            wattle = WattleStyle.SMALL,
            earLobe = EarLobeColor.WHITE,
            bodySize = BodySize.MEDIUM,
            isMale = isMale
        )

        "wyandotte", "silver laced wyandotte" -> BirdAppearance(
            comb = CombStyle.ROSE,
            combColor = PartColor.RED,
            beak = BeakStyle.MEDIUM,
            beakColor = PartColor.DARK_HORN,
            chest = PlumagePattern.LACED,
            chestColor = PartColor.SILVER,
            back = BackStyle.CUSHION,
            backColor = PartColor.SILVER,
            crown = CrownStyle.CLEAN,
            wings = WingStyle.FOLDED,
            wingColor = PartColor.SILVER,
            wingPattern = PlumagePattern.LACED,
            tail = if (isMale) TailStyle.SICKLE else TailStyle.SHORT,
            tailColor = PartColor.BLACK,
            legs = LegStyle.CLEAN,
            legColor = PartColor.YELLOW,
            joints = JointStyle.STANDARD,
            nails = if (isMale) NailStyle.LONG_SPUR else NailStyle.SHORT,
            eye = EyeColor.BAY,
            wattle = WattleStyle.MEDIUM,
            earLobe = EarLobeColor.RED,
            bodySize = BodySize.LARGE,
            isMale = isMale
        )

        // ---- Default / Unknown ----
        else -> BirdAppearance(
            comb = if (isMale) CombStyle.SINGLE else CombStyle.SINGLE,
            combColor = PartColor.RED,
            beak = BeakStyle.MEDIUM,
            beakColor = PartColor.HORN,
            chest = PlumagePattern.SOLID,
            chestColor = if (isMale) PartColor.WHITE else PartColor.BROWN,
            back = BackStyle.SMOOTH,
            backColor = if (isMale) PartColor.WHITE else PartColor.BROWN,
            crown = CrownStyle.CLEAN,
            wings = WingStyle.FOLDED,
            wingColor = if (isMale) PartColor.WHITE else PartColor.BROWN,
            tail = if (isMale) TailStyle.SICKLE else TailStyle.SHORT,
            tailColor = if (isMale) PartColor.BLACK else PartColor.BROWN,
            legs = LegStyle.CLEAN,
            legColor = PartColor.YELLOW,
            joints = JointStyle.STANDARD,
            nails = if (isMale) NailStyle.LONG_SPUR else NailStyle.SHORT,
            eye = EyeColor.ORANGE,
            wattle = if (isMale) WattleStyle.MEDIUM else WattleStyle.SMALL,
            earLobe = EarLobeColor.RED,
            bodySize = BodySize.MEDIUM,
            isMale = isMale
        )
    }
}
