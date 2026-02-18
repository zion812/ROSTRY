package com.rio.rostry.data.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "breeding_plans",
    foreignKeys = [
        ForeignKey(
            entity = ProductEntity::class,
            parentColumns = ["productId"],
            childColumns = ["sireId"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = ProductEntity::class,
            parentColumns = ["productId"],
            childColumns = ["damId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index("sireId"),
        Index("damId"),
        Index("farmerId")
    ]
)
data class BreedingPlanEntity(
    @PrimaryKey val planId: String = UUID.randomUUID().toString(),
    val farmerId: String,
    
    val sireId: String?,
    val sireName: String?, // Snapshot in case parent is deleted
    
    val damId: String?,
    val damName: String?, // Snapshot in case parent is deleted
    
    val createdAt: Long = System.currentTimeMillis(),
    val note: String? = null,
    
    // JSON array of SimulatedOffspring
    val simulatedOffspringJson: String,
    
    val status: String = "PLANNED", // PLANNED, COMPLETED, ARCHIVED
    val priority: Int = 1 // 1=Normal, 2=High
)
