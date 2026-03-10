package com.rio.rostry.core.model

/**
 * Domain model representing a family tree.
 * 
 * Groups breeding records by parent pair.
 */
data class FamilyTree(
    val familyTreeId: String,
    val maleId: String?,
    val femaleId: String?,
    val pairId: String?,
    val createdAt: Long,
    val updatedAt: Long
)
