package com.rio.rostry.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

/**
 * Represents a vaccination record for a poultry
 */
@Entity(tableName = "vaccination_records")
data class VaccinationRecord(
    @PrimaryKey
    val id: String,
    val poultryId: String,
    val vaccineName: String,
    val vaccinationDate: Date,
    val nextDueDate: Date? = null,
    val notes: String? = null,
    val administeredBy: String? = null, // Vet name or user ID
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
)