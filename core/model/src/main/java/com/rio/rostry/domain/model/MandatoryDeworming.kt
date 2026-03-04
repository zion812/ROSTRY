package com.rio.rostry.domain.model

enum class MandatoryDeworming(
    val displayName: String,
    val medications: String,
    val minAgeDays: Int,
    val maxAgeDays: Int?, // Null implies indefinite/adult
    val method: String,
    val notes: String,
    val isRecurring: Boolean = false,
    val recurrenceDays: Int? = null
) {
    FIRST_DOSE(
        displayName = "1st Deworming (Age 1 Month)",
        medications = "Piperazine Citrate / Albendazole",
        minAgeDays = 28, // ~1 Month
        maxAgeDays = 35,
        method = "Mix in water (Empty Stomach)",
        notes = "First Deworming. Give feed 2 hours after administration."
    ),
    GROWER_EARLY(
        displayName = "Grower Deworming (2-3 Months)",
        medications = "Albendazole / Levamisole",
        minAgeDays = 60,
        maxAgeDays = 90,
        method = "Mixed in water",
        notes = "Every 40-45 days. Ensure bird is hydrated before."
    ),
    GROWER_LATE(
        displayName = "Grower Deworming (3-6 Months)",
        medications = "Fenbendazole / Ivermectin",
        minAgeDays = 91,
        maxAgeDays = 180,
        method = "Low dose based on weight",
        notes = "Give only sweet water on the day of medication."
    ),
    ADULT_LAYERS(
        displayName = "Adult Deworming (6 Months+)",
        medications = "Albendazole 10% / Ivermectin Drops",
        minAgeDays = 181,
        maxAgeDays = null, // Adult
        method = "Day 1 & Day 7 (2 doses)",
        notes = "Careful during egg production. Consult vet if unsure.",
        isRecurring = true,
        recurrenceDays = 60 // Every 2 months (as per 1 Year+ rule, simplifying for 6m+)
    )
}
