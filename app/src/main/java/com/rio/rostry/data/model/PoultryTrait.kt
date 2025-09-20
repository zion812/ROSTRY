package com.rio.rostry.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

/**
 * Junction table linking poultry to their genetic traits
 */
@Entity(tableName = "poultry_traits", primaryKeys = ["poultryId", "traitId"])
data class PoultryTrait(
    val poultryId: String,
    val traitId: String,
    val isDominant: Boolean = false, // Whether this is the expressed trait
    val createdAt: Date = Date()
)