package com.rio.rostry.domain.model

enum class MandatoryVaccine(
    val displayName: String,
    val minAgeDays: Int,
    val maxAgeDays: Int,
    val method: String,
    val benefit: String,
    val notes: String
) {
    MAREKS(
        displayName = "Marek’s Disease Vaccine (HVT)",
        minAgeDays = 0,
        maxAgeDays = 1,
        method = "Subcutaneous Injection (SC)",
        benefit = "Lifelong protection from tumor diseases",
        notes = "Mandatory at hatchery level; 100% cold chain maintenance is required."
    ),
    ND_PRIMARY(
        displayName = "F1 / Lasota (ND-Ranikhet)",
        minAgeDays = 5,
        maxAgeDays = 7,
        method = "Eye/Nose Drop or Oral",
        benefit = "ND Primary Protection (IgA Mucosal Immunity)",
        notes = "Stop water/feed 1 hour before vaccination; Check for MDA (Maternal Derived Antibody) effect."
    ),
    INFECTIOUS_BRONCHITIS(
        displayName = "Infectious Bronchitis (IB) Vaccine",
        minAgeDays = 10,
        maxAgeDays = 15,
        method = "Eye/Nose Drop",
        benefit = "Protection against respiratory virus",
        notes = "Good to administer combined with ND; Must choose a vaccine suitable for the regional strain."
    ),
    GUMBORO(
        displayName = "Gumboro (IBD Vaccine)",
        minAgeDays = 14,
        maxAgeDays = 16,
        method = "Mixed in water",
        benefit = "Protection and prevention of IBD",
        notes = "Mandatory gap of at least 5 days after ND vaccine; Timing may vary based on field strain."
    ),
    ND_BOOSTER(
        displayName = "Lasota – ND Booster",
        minAgeDays = 21,
        maxAgeDays = 25,
        method = "Drop / Oral",
        benefit = "ND Booster immunity",
        notes = "Second dose is extremely necessary in high-risk field areas."
    ),
    FOWL_POX(
        displayName = "Fowl Pox Vaccine",
        minAgeDays = 42, // 6 weeks
        maxAgeDays = 56, // 8 weeks
        method = "Wing-web scar method",
        benefit = "Prevention of skin pox/lesions",
        notes = "Considered successful if a \"take\" mark appears 5-7 days after vaccination."
    )
}
