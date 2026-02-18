package com.rio.rostry.domain.logic

import com.rio.rostry.domain.model.BirdAppearance
import com.rio.rostry.domain.model.LocalBirdType
import com.rio.rostry.domain.model.PartColor
import com.rio.rostry.domain.model.PlumagePattern

/**
 * Logic for classifying a bird's physical appearance into a Local Telugu Type.
 * Follows the decision tree rules defined in Phase H.
 */
object BirdColorClassifier {

    data class ClassificationResult(
        val type: LocalBirdType,
        val confidence: Float,
        val reasoning: String
    )

    fun classify(appearance: BirdAppearance): ClassificationResult {
        val body = appearance.backColor
        val neck = appearance.chestColor // Approximating neck color using chest for now if they are usually linked
        val wing = appearance.wingColor
        val chest = appearance.chestColor
        val pattern = appearance.chest // Using chest pattern as primary body pattern
        
        // --- DECISION TREE ---

        // 1. KAKI (Black Body + Solid)
        if (isBlack(body) && pattern == PlumagePattern.SOLID) {
            return result(LocalBirdType.KAKI, 0.95f, "Solid black body.")
        }

        // 2. SETHU (White Body + Solid)
        if (isWhite(body) && pattern == PlumagePattern.SOLID) {
            return result(LocalBirdType.SETHU, 0.95f, "Solid white body.")
        }

        // 3. DEGA (Red Body + Solid)
        if (isRed(body) && pattern == PlumagePattern.SOLID) {
            return result(LocalBirdType.DEGA, 0.90f, "Solid red (eagle-colored) body.")
        }

        // 4. ABRASU (Golden Body + Solid/Buff)
        if ((isGold(body) || body == PartColor.BUFF) && pattern == PlumagePattern.SOLID) {
            return result(LocalBirdType.ABRASU, 0.85f, "Golden/Buff body.")
        }

        // 5. GERUVA (White Body + Red Neck/Highlights)
        if (isWhite(body) && (isRed(neck) || isRed(wing)) && pattern != PlumagePattern.SOLID) {
             return result(LocalBirdType.GERUVA, 0.80f, "White body with light red highlights.")
        }

        // 6. SAVALA (Black Neck + Non-Black Body)
        if (isBlack(neck) && !isBlack(body)) {
            return result(LocalBirdType.SAVALA, 0.85f, "Prominent black neck feathers.")
        }

        // 7. PARLA (Black & White distributed / Neck)
        // Check for Barred/Laced or Black/White mix
        if ((isBlack(neck) || isWhite(neck)) && 
            (pattern == PlumagePattern.BARRED || pattern == PlumagePattern.LACED || pattern == PlumagePattern.MOTTLED) &&
            (isBlack(body) || isWhite(body))) {
            return result(LocalBirdType.PARLA, 0.80f, "Even distribution of black and white.")
        }

        // 8. NEMALI (Yellow on Wings/Back)
        if (isYellow(wing) || isYellow(body) || wing == PartColor.WILLOW) {
            return result(LocalBirdType.NEMALI, 0.85f, "Yellow/Peacock coloring on wings or back.")
        }

        // 9. PINGALA (White Wings + Dark Body)
        if (isWhite(wing) && (isBrown(body) || isBlack(body))) {
            return result(LocalBirdType.PINGALA, 0.85f, "White wings on dark body.")
        }

        // 10. NALLA BORA (Black Breast + Red Body/Back)
        if (isBlack(chest) && (isRed(body) || isRed(appearance.backColor))) {
            return result(LocalBirdType.NALLA_BORA, 0.90f, "Black breast with red body.")
        }

        // 11. MAILA (Red Body + Ash/Gray Wings)
        if (isRed(body) && (isGray(wing) || isGray(chest))) {
            return result(LocalBirdType.MAILA, 0.85f, "Red body with ash/grey mix.")
        }

        // 12. POOLA (Feather Blend / Speckled)
        if (pattern == PlumagePattern.SPECKLED || pattern == PlumagePattern.SPLASH) {
            return result(LocalBirdType.POOLA, 0.80f, "Speckled/Flowery feather blend.")
        }

        // 13. KOWJU (Tri-color: Black + Red + Yellow/Gold)
        // Harder to detect with simple property checks, assume Partridge/DoubleLaced implies complex mix
        if (pattern == PlumagePattern.DOUBLE_LACED || pattern == PlumagePattern.PENCILED) {
             return result(LocalBirdType.KOWJU, 0.75f, "Complex tri-color pattern (Partridge).")
        }

         // 14. MUNGISA (Brown Body + Ash/Gray Wings)
        if (isBrown(body) && (isGray(wing) || wing == PartColor.BLUE)) {
            return result(LocalBirdType.MUNGISA, 0.80f, "Brown body with grey wings (Mongoose).")
        }

        // 15. KOKKIRAYI (Black Body + Multi-color Pattern)
        if (isBlack(body) && pattern != PlumagePattern.SOLID) {
            return result(LocalBirdType.KOKKIRAYI, 0.80f, "Black body with mixed colored feathers.")
        }

        // Fallback
        return result(LocalBirdType.UNKNOWN, 0.0f, "Does not match specific local criteria.")
    }

    private fun result(type: LocalBirdType, confidence: Float, reasoning: String) = 
        ClassificationResult(type, confidence, reasoning)

    // --- Helper predicates for color groups ---

    private fun isBlack(c: PartColor) = c == PartColor.BLACK || c == PartColor.GREEN_BLACK || c == PartColor.CHOCOLATE
    private fun isWhite(c: PartColor) = c == PartColor.WHITE || c == PartColor.CREAM || c == PartColor.SILVER
    private fun isRed(c: PartColor) = c == PartColor.RED || c == PartColor.DARK_RED || c == PartColor.MAHOGANY
    private fun isYellow(c: PartColor) = c == PartColor.YELLOW || c == PartColor.GOLD || c == PartColor.WHEATEN
    private fun isGold(c: PartColor) = c == PartColor.GOLD || c == PartColor.BUFF
    private fun isBrown(c: PartColor) = c == PartColor.BROWN || c == PartColor.DARK_BROWN || c == PartColor.MAHOGANY
    private fun isGray(c: PartColor) = c == PartColor.GRAY || c == PartColor.SLATE || c == PartColor.BLUE || c == PartColor.SPLASH_BLUE

}
