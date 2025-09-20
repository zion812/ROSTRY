package com.rio.rostry.domain.model

import java.util.Date

data class FamilyTree(
    val id: String,
    val parentId: String,
    val childId: String,
    val relationshipType: String,
    val generation: Int,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
)