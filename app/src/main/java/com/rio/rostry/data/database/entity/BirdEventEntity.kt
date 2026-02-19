package com.rio.rostry.data.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

/**
 * 🧬 BirdEventEntity — Unified Event Log for Digital Bird Twin
 *
 * Every significant event in a bird's life is recorded here as an immutable log entry.
 * This forms the "event sourcing" backbone of the Digital Twin system.
 *
 * Events drive score changes:
 * - WEIGHT_RECORDED → updates growth trajectory
 * - INJURY → decreases StaminaScore
 * - FIGHT_WIN → increases AggressionIndex, MarketScore
 * - FIGHT_LOSS → adjusts EnduranceScore
 * - BREEDING_SUCCESS → increases MarketScore, BreederReputation
 * - BREEDING_FAILURE → tracked for fertility analysis
 * - STAGE_TRANSITION → lifecycle milestone
 * - VACCINATION → health compliance
 * - OWNER_TRANSFER → provenance chain
 * - SHOW_RESULT → competition record
 * - TRAIT_RECORDED → phenotypic measurement
 * - VALUATION_UPDATE → market score change
 * - HEALTH_EVENT → illness, injury, recovery
 *
 * Architecture: Append-only ledger. Events are never deleted or modified.
 * This creates an immutable provenance chain for each bird.
 */
@Entity(
    tableName = "bird_events",
    foreignKeys = [
        ForeignKey(
            entity = ProductEntity::class,
            parentColumns = ["productId"],
            childColumns = ["birdId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("birdId"),
        Index("ownerId"),
        Index("eventType"),
        Index("eventDate"),
        Index(value = ["birdId", "eventType"]),
        Index(value = ["birdId", "eventDate"]),
        Index("dirty")
    ]
)
data class BirdEventEntity(
    @PrimaryKey val eventId: String = UUID.randomUUID().toString(),

    // ==================== IDENTITY ====================
    /** Reference to ProductEntity.productId */
    val birdId: String,

    /** Owner at time of event (for provenance) */
    val ownerId: String,

    // ==================== EVENT CLASSIFICATION ====================
    /** Event type — determines which scores are affected */
    val eventType: String,     // See BirdEventType constants below

    /** Human-readable event title */
    val eventTitle: String,

    /** Detailed description or notes */
    val eventDescription: String? = null,

    // ==================== TIMING ====================
    /** When this event occurred */
    val eventDate: Long,

    /** Age of bird in days at time of event (snapshot) */
    val ageDaysAtEvent: Int? = null,

    /** Lifecycle stage at time of event (snapshot) */
    val lifecycleStageAtEvent: String? = null,

    // ==================== MEASUREMENTS / DATA ====================
    /** Primary numeric value (context depends on eventType) */
    val numericValue: Double? = null,

    /** Secondary numeric value */
    val numericValue2: Double? = null,

    /** String value for non-numeric data */
    val stringValue: String? = null,

    /** JSON blob for complex event data */
    val dataJson: String? = null,

    // ==================== SCORE IMPACT ====================
    /** Change to morphology score (can be negative) */
    val morphologyScoreDelta: Int? = null,

    /** Change to genetics score */
    val geneticsScoreDelta: Int? = null,

    /** Change to performance score */
    val performanceScoreDelta: Int? = null,

    /** Change to health score */
    val healthScoreDelta: Int? = null,

    /** Change to market valuation score */
    val marketScoreDelta: Int? = null,

    // ==================== PROVENANCE ====================
    /** Who recorded this event */
    val recordedBy: String? = null,

    /** Is this event verified by a 3rd party? */
    val isVerified: Boolean = false,

    /** Verifier ID (admin, vet, show judge) */
    val verifiedBy: String? = null,

    /** Media evidence */
    val mediaUrlsJson: String? = null,

    // ==================== AUDIT ====================
    val createdAt: Long = System.currentTimeMillis(),
    val dirty: Boolean = true,
    val syncedAt: Long? = null
) {
    companion object {
        // ==================== EVENT TYPE CONSTANTS ====================

        // Growth & Physical
        const val TYPE_WEIGHT_RECORDED = "WEIGHT_RECORDED"
        const val TYPE_HEIGHT_RECORDED = "HEIGHT_RECORDED"
        const val TYPE_TRAIT_RECORDED = "TRAIT_RECORDED"

        // Health
        const val TYPE_VACCINATION = "VACCINATION"
        const val TYPE_ILLNESS = "ILLNESS"
        const val TYPE_INJURY = "INJURY"
        const val TYPE_RECOVERY = "RECOVERY"
        const val TYPE_DEWORMING = "DEWORMING"

        // Performance
        const val TYPE_FIGHT_WIN = "FIGHT_WIN"
        const val TYPE_FIGHT_LOSS = "FIGHT_LOSS"
        const val TYPE_FIGHT_DRAW = "FIGHT_DRAW"
        const val TYPE_SHOW_RESULT = "SHOW_RESULT"
        const val TYPE_TRAINING_SESSION = "TRAINING_SESSION"

        // Breeding
        const val TYPE_BREEDING_ATTEMPT = "BREEDING_ATTEMPT"
        const val TYPE_BREEDING_SUCCESS = "BREEDING_SUCCESS"
        const val TYPE_BREEDING_FAILURE = "BREEDING_FAILURE"
        const val TYPE_CLUTCH_LAID = "CLUTCH_LAID"
        const val TYPE_HATCHING_COMPLETE = "HATCHING_COMPLETE"

        // Lifecycle
        const val TYPE_STAGE_TRANSITION = "STAGE_TRANSITION"
        const val TYPE_BIRTH = "BIRTH"
        const val TYPE_DEATH = "DEATH"

        // Market & Ownership
        const val TYPE_OWNER_TRANSFER = "OWNER_TRANSFER"
        const val TYPE_VALUATION_UPDATE = "VALUATION_UPDATE"
        const val TYPE_LISTED_FOR_SALE = "LISTED_FOR_SALE"
        const val TYPE_SOLD = "SOLD"

        // Administrative
        const val TYPE_PHOTO_ADDED = "PHOTO_ADDED"
        const val TYPE_NOTE_ADDED = "NOTE_ADDED"
        const val TYPE_APPEARANCE_UPDATED = "APPEARANCE_UPDATED"
    }
}
