package com.rio.rostry.core.model

/**
 * Domain model for marketplace listing drafts.
 */
data class ListingDraft(
    val draftId: String = "",
    val farmerId: String = "",
    val step: String = "",
    val formDataJson: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val expiresAt: Long? = null
)
