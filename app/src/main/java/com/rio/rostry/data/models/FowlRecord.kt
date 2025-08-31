package com.rio.rostry.data.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    tableName = "fowl_records",
    foreignKeys = [ForeignKey(
        entity = Fowl::class,
        parentColumns = ["id"],
        childColumns = ["fowlId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["fowlId"])]
)
data class FowlRecord(
    @PrimaryKey val id: String,
    val fowlId: String,
    val userId: String = "",
    val date: Date,
    val type: String, // e.g., Vaccination, Update, Measurement
    val details: String, // Could be JSON for structured data
    val notes: String? = null,
    // For measurements
    val weight: Double? = null,
    val height: Double? = null,
    val length: Double? = null
)
