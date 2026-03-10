package com.rio.rostry.data.database.mapper

import com.rio.rostry.core.model.Comment
import com.rio.rostry.core.model.Post
import com.rio.rostry.core.model.PostVisibility
import com.rio.rostry.core.model.Reputation
import com.rio.rostry.core.model.Story
import com.rio.rostry.data.database.entity.CommentEntity
import com.rio.rostry.data.database.entity.PostEntity
import com.rio.rostry.data.database.entity.ReputationEntity
import com.rio.rostry.data.database.entity.StoryEntity

/**
 * Extension function to map PostEntity to domain Post model.
 */
fun PostEntity.toDomain(): Post {
    return Post(
        id = postId,
        authorId = authorId,
        content = text ?: "",
        images = if (type == "IMAGE") listOfNotNull(mediaUrl) else emptyList(),
        videos = if (type == "VIDEO") listOfNotNull(mediaUrl) else emptyList(),
        visibility = PostVisibility.PUBLIC, // Mapping could be more sophisticated
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

/**
 * Extension function to map CommentEntity to domain Comment model.
 */
fun CommentEntity.toDomain(): Comment {
    return Comment(
        id = commentId,
        postId = postId,
        authorId = authorId,
        text = text,
        createdAt = createdAt
    )
}

/**
 * Extension function to map StoryEntity to domain Story model.
 */
fun StoryEntity.toDomain(): Story {
    return Story(
        id = storyId,
        authorId = authorId,
        mediaUrl = mediaUrl,
        createdAt = createdAt,
        expiresAt = expiresAt
    )
}

/**
 * Extension function to map ReputationEntity to domain Reputation model.
 */
fun ReputationEntity.toDomain(): Reputation {
    return Reputation(
        id = repId,
        userId = userId,
        score = score,
        updatedAt = updatedAt
    )
}
