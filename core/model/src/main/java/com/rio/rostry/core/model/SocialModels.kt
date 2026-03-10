package com.rio.rostry.core.model

/**
 * Domain model for a social post comment.
 */
data class Comment(
    val id: String,
    val postId: String,
    val authorId: String,
    val text: String,
    val createdAt: Long
)

/**
 * Domain model for a social story.
 */
data class Story(
    val id: String,
    val authorId: String,
    val mediaUrl: String,
    val createdAt: Long,
    val expiresAt: Long
)

/**
 * Domain model for user reputation.
 */
data class Reputation(
    val id: String,
    val userId: String,
    val score: Int,
    val updatedAt: Long
)
