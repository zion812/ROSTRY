package com.rio.rostry.domain.digitaltwin.lifecycle

/**
 * 🥚 EggProfile — Visual configuration for the Egg stage
 *
 * When bird is in the EGG stage, the renderer shows an egg
 * instead of a bird. This profile controls the egg's visual.
 */
data class EggProfile(
    /** Shell color */
    val shellColor: EggShellColor = EggShellColor.CREAM,

    /** Fertility status */
    val fertilityStatus: FertilityStatus = FertilityStatus.UNKNOWN,

    /** Incubation progress: 0.0 (just set) → 1.0 (about to hatch) */
    val incubationProgress: Float = 0f,

    /** Days into incubation (0-21 typical for chickens) */
    val incubationDays: Int = 0,

    /** Predicted hatch probability (0.0-1.0) based on conditions */
    val hatchProbability: Float = 0.85f,

    /** Temperature status during incubation */
    val temperatureStatus: TemperatureStatus = TemperatureStatus.OPTIMAL,

    /** Candling result (if checked) */
    val candlingResult: CandlingResult = CandlingResult.NOT_CHECKED,

    /** Expected hatch date as epoch millis (nullable) */
    val expectedHatchDate: Long? = null
)

enum class EggShellColor(val displayName: String, val colorHex: Long) {
    WHITE("White", 0xFFF5F5DC),
    CREAM("Cream", 0xFFFFF8DC),
    LIGHT_BROWN("Light Brown", 0xFFDEB887),
    BROWN("Brown", 0xFFC4A882),
    DARK_BROWN("Dark Brown", 0xFF8B7355),
    TINTED("Tinted", 0xFFE8D5B7),
    OLIVE("Olive", 0xFF808040),
    BLUE("Blue", 0xFFB0C4DE),
    GREEN("Green", 0xFFACC3A6)
}

enum class FertilityStatus(val displayName: String, val emoji: String) {
    FERTILE("Fertile", "✅"),
    INFERTILE("Infertile", "❌"),
    UNKNOWN("Unknown", "❓")
}

enum class TemperatureStatus(val displayName: String) {
    TOO_LOW("Too Low"),
    LOW("Slightly Low"),
    OPTIMAL("Optimal"),
    HIGH("Slightly High"),
    TOO_HIGH("Too High")
}

enum class CandlingResult(val displayName: String, val emoji: String) {
    NOT_CHECKED("Not Checked", "🔍"),
    CLEAR("Clear (Infertile)", "⚪"),
    DEVELOPING("Developing", "🟢"),
    BLOOD_RING("Blood Ring (Dead)", "🔴"),
    QUITTER("Quitter", "⚫"),
    READY_TO_HATCH("Ready to Hatch", "🐣")
}
