package com.rio.rostry.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "genetic_analysis")
data class GeneticAnalysisEntity(
    @PrimaryKey
    val analysisId: String,
    val productId: String,
    val sampleId: String,
    val laboratoryName: String,
    val analysisDate: Long,
    val geneticMarkersJson: String, // JSON map of markers
    val traitsJson: String, // JSON list of detected traits
    val resultSummary: String,
    val documentUrl: String?,
    val createdAt: Long = System.currentTimeMillis()
)
