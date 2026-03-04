package com.rio.rostry.domain.model

/**
 * Represents educational content for the Learn & Grow section
 */
data class EducationalContent(
    val id: String,
    val title: String,
    val description: String,
    val type: ContentType,
    val imageUrl: String? = null,
    val videoUrl: String? = null,
    val duration: Int? = null, // in minutes
    val publishedAt: Long = System.currentTimeMillis()
)

enum class ContentType {
    ARTICLE,
    VIDEO,
    GUIDE,
    TIP,
    TUTORIAL
}
