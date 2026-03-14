package com.rio.rostry.domain.admin.model

enum class ContentType { PRODUCT, REVIEW }

data class ModerationItem(
    val id: String,
    val type: ContentType,
    val title: String,
    val content: String,
    val flaggedAt: Long,
    val reason: String?
)
