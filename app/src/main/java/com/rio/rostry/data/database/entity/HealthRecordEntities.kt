package com.rio.rostry.data.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

/**
 * MedicalEventEntity - General health events beyond vaccinations.
 * 
 * Tracks:
 * - Illnesses and diagnoses
 * - Injuries
 * - Treatments and medications
 * - Health checkups
 * - Deworming
 * 
 * This complements the existing VaccinationRecordEntity in FarmMonitoringEntities.kt
 * for complete health tracking coverage.
 */
@Entity(
    tableName = "medical_events",
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
        Index("farmerId"),
        Index("eventType"),
        Index("eventDate"),
        Index("status")
    ]
)
data class MedicalEventEntity(
    @PrimaryKey val eventId: String = UUID.randomUUID().toString(),
    
    // Foreign key to ProductEntity
    val birdId: String,
    
    // Owner ID for farm-level queries
    val farmerId: String,
    
    // Event classification
    val eventType: String,                      // ILLNESS, INJURY, CHECKUP, TREATMENT, DEWORMING, OTHER
    val severity: String = "MILD",              // MILD, MODERATE, SEVERE, CRITICAL
    
    // Details
    val eventDate: Long,                        // When event occurred/was diagnosed
    val resolvedDate: Long? = null,             // When issue was resolved (if applicable)
    val diagnosis: String? = null,              // e.g., "Respiratory Infection", "Bumblefoot"
    val symptoms: String? = null,               // Observed symptoms
    val treatment: String? = null,              // Treatment provided
    val medication: String? = null,             // Medication name
    val dosage: String? = null,                 // Medication dosage
    val treatmentDuration: String? = null,      // e.g., "5 days", "Until resolved"
    
    // Outcome
    val status: String = "ACTIVE",              // ACTIVE, RESOLVED, CHRONIC, DECEASED
    val outcome: String? = null,                // e.g., "Full recovery", "Ongoing management"
    
    // Provider
    val treatedBy: String? = null,              // Vet name or self
    val vetVisit: Boolean = false,              // Whether a vet was consulted
    val vetNotes: String? = null,               // Notes from vet visit
    
    // Cost tracking
    val cost: Double? = null,                   // Treatment/medication cost
    
    // Notes and media
    val notes: String? = null,
    val mediaUrlsJson: String? = null,          // Photos/videos of condition
    
    // Audit
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val dirty: Boolean = false,
    val syncedAt: Long? = null
)
