package com.rio.rostry.ui.social

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.rio.rostry.data.database.entity.PostEntity
import com.rio.rostry.data.database.entity.CommentEntity
import com.rio.rostry.data.repository.social.SocialRepository
import com.rio.rostry.data.database.dao.ModerationReportsDao
import com.rio.rostry.data.database.entity.ModerationReportEntity
import com.rio.rostry.data.database.dao.ReactionDao
import com.rio.rostry.data.database.entity.ReactionEntity
import com.rio.rostry.data.database.dao.CommentsDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class SocialFeedViewModel @Inject constructor(
    private val socialRepository: SocialRepository,
    private val moderationReportsDao: ModerationReportsDao,
    private val reactionDao: ReactionDao,
    private val commentsDao: CommentsDao,
) : ViewModel() {
    fun feed(): Flow<PagingData<PostEntity>> = socialRepository.feedRanked().cachedIn(viewModelScope)

    suspend fun like(postId: String, userId: String) = socialRepository.like(postId, userId)
    suspend fun unlike(postId: String, userId: String) = socialRepository.unlike(postId, userId)
    suspend fun addComment(postId: String, userId: String, text: String) = socialRepository.addComment(postId, userId, text)

    fun comments(postId: String): Flow<List<CommentEntity>> = socialRepository.streamComments(postId)

    suspend fun reportPost(postId: String, reporterId: String, reason: String) {
        val report = ModerationReportEntity(
            reportId = java.util.UUID.randomUUID().toString(),
            targetType = "POST",
            targetId = postId,
            reporterId = reporterId,
            reason = reason,
            status = "OPEN",
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis(),
        )
        moderationReportsDao.upsert(report)
    }

    fun userReaction(postId: String, userId: String): Flow<ReactionEntity?> = reactionDao.streamUserReaction(postId, userId)

    fun reactionCounts(postId: String): Flow<List<ReactionDao.ReactionCount>> = reactionDao.countsByType(postId)

    suspend fun setReaction(postId: String, userId: String, type: String) {
        val entity = ReactionEntity(
            reactionId = java.util.UUID.randomUUID().toString(),
            postId = postId,
            userId = userId,
            type = type,
            createdAt = System.currentTimeMillis()
        )
        reactionDao.upsert(entity)
    }

    suspend fun clearReaction(postId: String, userId: String) {
        reactionDao.remove(postId, userId)
    }

    suspend fun addReply(postId: String, parentCommentId: String, userId: String, text: String) {
        val e = CommentEntity(
            commentId = java.util.UUID.randomUUID().toString(),
            postId = postId,
            authorId = userId,
            parentCommentId = parentCommentId,
            text = text,
            createdAt = System.currentTimeMillis()
        )
        commentsDao.upsert(e)
    }
}
