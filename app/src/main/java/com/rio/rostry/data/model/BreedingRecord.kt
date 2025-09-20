package com.rio.rostry.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

/**
 * Represents a breeding event between two poultry
 */
@Entity(tableName = "breeding_records")
data class BreedingRecord(
    @PrimaryKey
    val id: String,
    val parentId1: String, // First parent ID
    val parentId2: String, // Second parent ID
    val breedingDate: Date,
    val expectedHatchDate: Date,
    val actualHatchDate: Date? = null,
    val clutchSize: Int = 0, // Number of eggs
    val hatchCount: Int = 0, // Number of successful hatches
    val successRate: Double = 0.0, // Hatch count / clutch size
    val notes: String? = null,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
)