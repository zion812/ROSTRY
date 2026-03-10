package com.rio.rostry.core.model

/**
 * Domain model for a community group.
 */
data class Group(
    val groupId: String,
    val name: String,
    val description: String?,
    val ownerId: String,
    val category: String?,
    val createdAt: Long
)
