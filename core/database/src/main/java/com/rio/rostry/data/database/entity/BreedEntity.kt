package com.rio.rostry.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.rio.rostry.data.database.AppDatabase

@Entity(tableName = "breeds")
data class BreedEntity(
    @PrimaryKey val breedId: String,
    val name: String,
    val description: String,
    val culinaryProfile: String, // "Soft", "Textured", "Medicinal"
    val farmingDifficulty: String, // "Beginner", "Expert"
    val imageUrl: String? = null,
    val tags: List<String> = emptyList() // e.g., "Broody", "Good Forger"
)
