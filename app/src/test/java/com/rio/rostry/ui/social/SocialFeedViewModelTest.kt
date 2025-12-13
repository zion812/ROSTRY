package com.rio.rostry.ui.social

import com.rio.rostry.data.database.entity.PostEntity
import com.rio.rostry.data.repository.social.SocialRepository
import com.rio.rostry.data.repository.UserRepository
import com.rio.rostry.session.CurrentUserProvider
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import android.net.Uri
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import io.mockk.mockk

private class FakeSocialRepo : SocialRepository {
    var lastLike: Pair<String, String>? = null
    var lastUnlike: Pair<String, String>? = null
    var lastComment: Triple<String, String, String>? = null

    override fun feed(pageSize: Int): Flow<PagingData<PostEntity>> = flowOf(PagingData.empty())
    override fun feedRanked(pageSize: Int): Flow<PagingData<PostEntity>> = flowOf(PagingData.empty())
    override fun getUserPosts(userId: String, pageSize: Int): Flow<PagingData<PostEntity>> = flowOf(PagingData.empty())
    override suspend fun createPost(
        authorId: String,
        type: String,
        text: String?,
        mediaUrl: String?,
        thumbnailUrl: String?,
        productId: String?,
        hashtags: List<String>?,
        mentions: List<String>?,
        parentPostId: String?
    ): String = ""
    override suspend fun createPostWithMedia(authorId: String, type: String, text: String?, mediaUri: Uri, isVideo: Boolean): String = ""
    override fun streamComments(postId: String): Flow<List<com.rio.rostry.data.database.entity.CommentEntity>> = flowOf(emptyList())
    override fun getReplies(postId: String): Flow<List<PostEntity>> = flowOf(emptyList())
    override suspend fun createStory(authorId: String, mediaUri: Uri, isVideo: Boolean): String = ""
    override fun getReputation(userId: String): Flow<com.rio.rostry.data.database.entity.ReputationEntity?> = flowOf(null)
    override fun streamActiveStories(): Flow<List<com.rio.rostry.data.database.entity.StoryEntity>> = flowOf(emptyList())
    override suspend fun like(postId: String, userId: String) { lastLike = postId to userId }
    override suspend fun unlike(postId: String, userId: String) { lastUnlike = postId to userId }
    override suspend fun addComment(postId: String, userId: String, text: String) { lastComment = Triple(postId, userId, text) }
    override fun countLikes(postId: String): Flow<Int> = flowOf(0)
    override suspend fun getEngagementMetrics(postId: String): com.rio.rostry.data.repository.social.EngagementMetrics =
        com.rio.rostry.data.repository.social.EngagementMetrics(postId, 0, 0, 0, 0, 0.0)
    override suspend fun getEngagementMetricsBatch(postIds: List<String>): Map<String, com.rio.rostry.data.repository.social.EngagementMetrics> =
        emptyMap()
    override suspend fun getTrendingPosts(limit: Int, daysBack: Int): List<PostEntity> = emptyList()
    override suspend fun getTrendingHashtags(limit: Int, daysBack: Int): List<String> = emptyList()
    override suspend fun trackPostView(postId: String, userId: String, durationSeconds: Int) {}
}

private class FakeUserProvider(private val uid: String?) : CurrentUserProvider {
    override fun userIdOrNull(): String? = uid
    override fun isAuthenticated(): Boolean = uid != null
}

class SocialFeedViewModelTest {

    @Test
    fun usesCurrentUserProviderForActions() = runBlocking {
        val repo = FakeSocialRepo()
        val provider = FakeUserProvider("u_123")
        val userRepository: UserRepository = mockk(relaxed = true)
        val vm = SocialFeedViewModel(repo, provider, userRepository)

        vm.like("p1")
        vm.unlike("p2")
        vm.addComment("p3", "hello")

        assertEquals("p1" to "u_123", repo.lastLike)
        assertEquals("p2" to "u_123", repo.lastUnlike)
        assertEquals(Triple("p3", "u_123", "hello"), repo.lastComment)
    }
}
