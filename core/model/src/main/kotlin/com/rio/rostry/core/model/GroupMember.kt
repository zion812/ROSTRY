package com.rio.rostry.core.model

/**
 * Domain model for a group membership.
 */
data class GroupMember(
    val membershipId: String,
    val groupId: String,
    val userId: String,
    val role: String,
    val joinedAt: Long
)
