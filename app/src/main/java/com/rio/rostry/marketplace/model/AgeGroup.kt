package com.rio.rostry.marketplace.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class AgeGroup {
    @SerialName("CHICK_0_5_WEEKS")
    CHICK_0_5_WEEKS,

    @SerialName("YOUNG_5_20_WEEKS")
    YOUNG_5_20_WEEKS,

    @SerialName("ADULT_20_52_WEEKS")
    ADULT_20_52_WEEKS,

    @SerialName("BREEDER_12_MONTHS_PLUS")
    BREEDER_12_MONTHS_PLUS;

    companion object {
        fun fromBirthDate(birthDateMillis: Long?, now: Long = System.currentTimeMillis()): AgeGroup? {
            birthDateMillis ?: return null
            val days = ((now - birthDateMillis).coerceAtLeast(0)).div(1000L * 60 * 60 * 24).toInt()
            return when {
                days < 0 -> null
                days <= 35 -> CHICK_0_5_WEEKS
                days <= 140 -> YOUNG_5_20_WEEKS
                days <= 364 -> ADULT_20_52_WEEKS
                else -> BREEDER_12_MONTHS_PLUS
            }
        }
    }
}
