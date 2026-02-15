package com.rio.rostry.ui.enthusiast.digitalfarm

import com.rio.rostry.domain.model.*
import org.json.JSONObject

/**
 * Robust JSON parser for BirdAppearance.
 * Handles both wrapped {"birdAppearance":{...}} and flat {...} formats.
 * Safely falls back to defaults if fields are missing or invalid.
 */
fun parseAppearanceFromJson(json: String): BirdAppearance? {
    if (json.isBlank()) return null
    
    try {
        val root = JSONObject(json)
        val obj = if (root.has("birdAppearance")) root.getJSONObject("birdAppearance") else root
        
        return BirdAppearance(
            comb = obj.safeEnum("comb", CombStyle.SINGLE),
            combColor = obj.safeEnum("combColor", PartColor.RED),
            beak = obj.safeEnum("beak", BeakStyle.MEDIUM),
            beakColor = obj.safeEnum("beakColor", PartColor.HORN),
            chest = obj.safeEnum("chest", PlumagePattern.SOLID),
            chestColor = obj.safeEnum("chestColor", PartColor.WHITE),
            back = obj.safeEnum("back", BackStyle.SMOOTH),
            backColor = obj.safeEnum("backColor", PartColor.WHITE),
            crown = obj.safeEnum("crown", CrownStyle.CLEAN),
            crownColor = obj.safeEnum("crownColor", PartColor.WHITE),
            wings = obj.safeEnum("wings", WingStyle.FOLDED),
            wingColor = obj.safeEnum("wingColor", PartColor.WHITE),
            wingPattern = obj.safeEnum("wingPattern", PlumagePattern.SOLID),
            tail = obj.safeEnum("tail", TailStyle.SHORT),
            tailColor = obj.safeEnum("tailColor", PartColor.BLACK),
            legs = obj.safeEnum("legs", LegStyle.CLEAN),
            legColor = obj.safeEnum("legColor", PartColor.YELLOW),
            joints = obj.safeEnum("joints", JointStyle.STANDARD),
            nails = obj.safeEnum("nails", NailStyle.SHORT),
            nailColor = obj.safeEnum("nailColor", PartColor.HORN),
            eye = obj.safeEnum("eye", EyeColor.ORANGE),
            wattle = obj.safeEnum("wattle", WattleStyle.MEDIUM),
            wattleColor = obj.safeEnum("wattleColor", PartColor.RED),
            earLobe = obj.safeEnum("earLobe", EarLobeColor.RED),
            bodySize = obj.safeEnum("bodySize", BodySize.MEDIUM),
            isMale = obj.optBoolean("isMale", true),
            
            // V2 Fields
            stance = obj.safeEnum("stance", Stance.NORMAL),
            sheen = obj.safeEnum("sheen", Sheen.MATTE),
            neck = obj.safeEnum("neck", NeckStyle.MEDIUM),
            breast = obj.safeEnum("breast", BreastShape.ROUND),
            skin = obj.safeEnum("skin", SkinColor.YELLOW),
            headShape = obj.safeEnum("headShape", HeadShape.ROUND)
        )
    } catch (e: Exception) {
        e.printStackTrace()
        return null
    }
}

/**
 * Helper to safely parse enums from JSON object
 */
private inline fun <reified T : Enum<T>> JSONObject.safeEnum(key: String, default: T): T {
    if (!has(key)) return default
    return try {
        val value = getString(key)
        enumValueOf<T>(value)
    } catch (e: Exception) {
        default
    }
}
