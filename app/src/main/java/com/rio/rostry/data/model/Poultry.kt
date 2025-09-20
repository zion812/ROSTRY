package com.rio.rostry.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "poultry")
data class Poultry(
    @PrimaryKey
    val id: String,
    val name: String,
    val breed: String,
    val gender: String, // MALE, FEMALE
    val color: String,
    val hatchDate: Date,
    val status: String, // CHICK, GROWING, ADULT, BREEDER, SOLD, DECEASED
    val parentId1: String? = null, // For family tree tracking
    val parentId2: String? = null, // For family tree tracking (second parent in case of crossbreeding)
    val generation: Int = 0, // 0 for base generation
    val breederStatus: Boolean = false, // Eligible for breeding after 52 weeks
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
)