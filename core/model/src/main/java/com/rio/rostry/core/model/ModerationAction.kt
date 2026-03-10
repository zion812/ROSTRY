package com.rio.rostry.core.model

/**
 * Moderation action model for content moderation.
 */
data class ModerationAction(
    val id: String,
    val contentType: ContentType,
    val contentId: String,
    val reportedBy: String,
    val reason: String,
    val status: ModerationStatus = ModerationStatus.PENDING,
    val assignedTo: String? = null,
    val resolution: String? = null,
    val resolvedAt: Long? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

enum class ContentType {
    POST,
    COMMENT,
    LISTING,
    USER_PROFILE,
    MESSAGE
}

enum class ModerationStatus {
    PENDING,
    UNDER_REVIEW,
    APPROVED,
    REJECTED,
    SUSPENDED
}
