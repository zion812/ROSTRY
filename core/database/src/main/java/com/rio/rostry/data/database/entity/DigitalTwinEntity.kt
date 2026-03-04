package com.rio.rostry.data.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * 🧬 DigitalTwinEntity — Unified Bird Identity Record
 *
 * This is the SINGLE SOURCE OF TRUTH for a bird's Digital Twin.
 * It unifies data currently fragmented across ProductEntity, FarmAssetEntity,
 * DigitalTwinProfile, BirdAppearance, and BirdTraitRecordEntity.
 *
 * Architecture:
 * - Links to ProductEntity/FarmAssetEntity via birdId (one-to-one)
 * - Aggregated scores computed from BirdEventEntity log
 * - Appearance stored in metadataJson (serialized BirdAppearance)
 * - Genetics stored in geneticsJson (serialized GeneticProfile)
 *
 * This entity is the "passport" of the bird — the core identity record
 * that enables breeder-grade assessment, market valuation, and genetic tracking.
 *
 * Schema maps to the target BirdDigitalTwin JSON structure:
 * ```
 * BirdDigitalTwin {
 *   id, registryId, ownerId,
 *   breedProfile { baseBreed, strainType, localStrainName, geneticPurityScore },
 *   morphology { bodyType, boneDensityScore, heightCm, weightKg, ... },
 *   plumage { baseColor, neckColor, wingPattern, tailType, ... },
 *   lifecycle { stage, ageDays, maturityScore, breedingStatus },
 *   genetics { sireId, damId, generationDepth, inbreedingCoefficient },
 *   health { vaccinationLog, injuryHistory, staminaScore },
 *   performance { aggressionIndex, enduranceScore, intelligenceScore },
 *   market { valuationScore, verifiedStatus, certificationLevel }
 * }
 * ```
 */
@Entity(
    tableName = "digital_twins",
    foreignKeys = [
        ForeignKey(
            entity = ProductEntity::class,
            parentColumns = ["productId"],
            childColumns = ["birdId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("birdId", unique = true),
        Index("ownerId"),
        Index("baseBreed"),
        Index("strainType"),
        Index("lifecycleStage"),
        Index("certificationLevel"),
        Index("dirty")
    ]
)
data class DigitalTwinEntity(
    @PrimaryKey val twinId: String,

    // ==================== IDENTITY ====================
    /** Links to ProductEntity.productId (1-to-1) */
    val birdId: String,

    /** Structured registry ID (e.g., "RST-ASL-RZA-001") */
    val registryId: String? = null,

    /** Current owner */
    val ownerId: String,

    /** Display name */
    val birdName: String? = null,

    // ==================== BREED PROFILE ====================
    /** Base breed (always "Aseel" for now, extensible) */
    val baseBreed: String = "Aseel",

    /** Strain type: Kulangi, Madras, Malay, Reza, Mianwali, Sindhi, etc. */
    val strainType: String? = null,

    /** Local strain name (user-defined, e.g., "Rani Line") */
    val localStrainName: String? = null,

    /** Genetic purity score (0-100, calculated from lineage analysis) */
    val geneticPurityScore: Int? = null,

    // ==================== MORPHOLOGY (Structure Assessment) ====================
    /** Body type classification: DAGA, DEGA, KAKI, MIXED */
    val bodyType: String? = null,

    /** Bone density score (0-100) — derived from bone thickness, joint measurements */
    val boneDensityScore: Int? = null,

    /** Current height in cm */
    val heightCm: Double? = null,

    /** Current weight in kg */
    val weightKg: Double? = null,

    /** Beak type: STRAIGHT, PARROT, MEDIUM, HOOKED */
    val beakType: String? = null,

    /** Comb type: CUTTED, SINGLE, PEA, WALNUT */
    val combType: String? = null,

    /** Skin color: WHITE, YELLOW, BLACK, SLATE */
    val skinColor: String? = null,

    /** Leg color: YELLOW, WHITE, BLACK, SLATE, MIXED */
    val legColor: String? = null,

    /** Spur type: SINGLE, DOUBLE, DEVELOPING, NONE */
    val spurType: String? = null,

    /** Morphology composite score (0-100) — weighted average of all morphology traits */
    val morphologyScore: Int? = null,

    // ==================== PLUMAGE (Color DNA) ====================
    /** Primary body color hex */
    val primaryBodyColor: Long? = null,

    /** Neck hackle color hex */
    val neckHackleColor: Long? = null,

    /** Wing highlight color hex */
    val wingHighlightColor: Long? = null,

    /** Tail color hex */
    val tailColor: Long? = null,

    /** Tail iridescence flag */
    val tailIridescent: Boolean = false,

    /** Plumage pattern type: SOLID, SPECKLED, LACED, BARRED, etc. */
    val plumagePattern: String? = null,

    /** Local color name from LocalBirdType (KAKI, SETHU, DEGA, etc.) */
    val localColorCode: String? = null,

    /** Color category code for market filtering */
    val colorCategoryCode: String? = null,

    // ==================== LIFECYCLE ====================
    /** Current lifecycle stage (AseelLifecycleStage name) */
    val lifecycleStage: String = "CHICK",

    /** Age in days (cached, recomputed periodically) */
    val ageDays: Int? = null,

    /** Maturity score (0-100) — how close to breed standard for current stage */
    val maturityScore: Int? = null,

    /** Breeding status: NONE, ACTIVE, PROVEN, RETIRED */
    val breedingStatus: String = "NONE",

    /** Gender: MALE, FEMALE */
    val gender: String? = null,

    /** Birth/hatch date */
    val birthDate: Long? = null,

    // ==================== GENETICS ====================
    /** Sire (father) ProductEntity ID */
    val sireId: String? = null,

    /** Dam (mother) ProductEntity ID */
    val damId: String? = null,

    /** Generation depth in pedigree (0 = foundation, 1 = F1, etc.) */
    val generationDepth: Int = 0,

    /** Inbreeding coefficient (0.0 - 1.0) */
    val inbreedingCoefficient: Double? = null,

    /** Serialized GeneticProfile JSON */
    val geneticsJson: String? = null,

    /** Genetics composite score (0-100) */
    val geneticsScore: Int? = null,

    // ==================== HEALTH ====================
    /** Total vaccinations administered */
    val vaccinationCount: Int = 0,

    /** Total injuries recorded */
    val injuryCount: Int = 0,

    /** Stamina score (0-100) — derived from health history, age, training */
    val staminaScore: Int? = null,

    /** Overall health score (0-100) */
    val healthScore: Int? = null,

    /** Current health status: HEALTHY, SICK, INJURED, RECOVERING */
    val currentHealthStatus: String = "HEALTHY",

    // ==================== PERFORMANCE ====================
    /** Aggression index (0-100) — from behavioral trait records and fight history */
    val aggressionIndex: Int? = null,

    /** Endurance score (0-100) — from stamina tests and fight duration history */
    val enduranceScore: Int? = null,

    /** Intelligence score (0-100) — from trainability assessments */
    val intelligenceScore: Int? = null,

    /** Total fights participated */
    val totalFights: Int = 0,

    /** Total fight wins */
    val fightWins: Int = 0,

    /** Performance composite score (0-100) */
    val performanceScore: Int? = null,

    // ==================== MARKET ====================
    /**
     * Market valuation score (0-100)
     * Formula: (Morphology × 0.4) + (Genetics × 0.3) + (Performance × 0.2) + (Health × 0.1)
     */
    val valuationScore: Int? = null,

    /** Is this bird's identity verified by platform? */
    val verifiedStatus: Boolean = false,

    /** Certification level: NONE, REGISTERED, VERIFIED, CHAMPION */
    val certificationLevel: String = "NONE",

    /** Estimated market value in INR */
    val estimatedValueInr: Double? = null,

    // ==================== SHOW RECORD SUMMARY ====================
    /** Total shows participated */
    val totalShows: Int = 0,

    /** Total show wins/placements */
    val showWins: Int = 0,

    /** Best show placement */
    val bestPlacement: Int? = null,

    // ==================== BREEDING RECORD SUMMARY ====================
    /** Total breeding attempts */
    val totalBreedingAttempts: Int = 0,

    /** Successful breeding count */
    val successfulBreedings: Int = 0,

    /** Total offspring produced */
    val totalOffspring: Int = 0,

    // ==================== SERIALIZED DATA ====================
    /** Full BirdAppearance JSON (for renderer) */
    val appearanceJson: String? = null,

    /** Additional metadata JSON */
    val metadataJson: String = "{}",

    // ==================== AUDIT ====================
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val dirty: Boolean = true,
    val syncedAt: Long? = null,
    val isDeleted: Boolean = false,
    val deletedAt: Long? = null
) {
    /**
     * Compute the market valuation score from component scores.
     * (Morphology × 0.4) + (Genetics × 0.3) + (Performance × 0.2) + (Health × 0.1)
     */
    fun computeValuationScore(): Int {
        val m = morphologyScore ?: return 0
        val g = geneticsScore ?: 50
        val p = performanceScore ?: 50
        val h = healthScore ?: 50
        return ((m * 0.4) + (g * 0.3) + (p * 0.2) + (h * 0.1)).toInt().coerceIn(0, 100)
    }

    /** Certification level check helpers */
    val isRegistered: Boolean get() = certificationLevel != "NONE"
    val isVerified: Boolean get() = certificationLevel == "VERIFIED" || certificationLevel == "CHAMPION"
    val isChampion: Boolean get() = certificationLevel == "CHAMPION"

    /** Win rate for fights */
    val fightWinRate: Double
        get() = if (totalFights > 0) fightWins.toDouble() / totalFights else 0.0

    /** Breeding success rate */
    val breedingSuccessRate: Double
        get() = if (totalBreedingAttempts > 0) successfulBreedings.toDouble() / totalBreedingAttempts else 0.0
}
