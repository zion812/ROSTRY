package com.rio.rostry.core.model

/**
 * Post model for social platform.
 */
data class Post(
    val id: String,
    val authorId: String,
    val content: String,
    val images: List<String> = emptyList(),
    val videos: List<String> = emptyList(),
    val likesCount: Int = 0,
    val commentsCount: Int = 0,
    val sharesCount: Int = 0,
    val visibility: PostVisibility = PostVisibility.PUBLIC,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

enum class PostVisibility {
    PUBLIC,
    FOLLOWERS_ONLY,
    PRIVATE
}
