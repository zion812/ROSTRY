package com.rio.rostry.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "family_tree")
data class FamilyTree(
    @PrimaryKey
    val id: String,
    val parentId: String, // Reference to parent product
    val childId: String, // Reference to child product
    val relationshipType: String, // genetic, crossbreed, etc.
    val generation: Int,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
    // Temporarily removing isDeleted and deletedAt for compilation
    // val isDeleted: Boolean = false,
    // val deletedAt: Date? = null
)