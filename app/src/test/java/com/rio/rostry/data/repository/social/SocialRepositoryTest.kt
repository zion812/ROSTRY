package com.rio.rostry.data.repository.social

import com.rio.rostry.data.database.dao.CommentsDao
import com.rio.rostry.data.database.dao.LikesDao
import com.rio.rostry.data.database.dao.PostsDao
import com.rio.rostry.data.database.entity.CommentEntity
import com.rio.rostry.data.database.entity.LikeEntity
import com.rio.rostry.data.database.entity.PostEntity
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.util.UUID

@OptIn(ExperimentalCoroutinesApi::class)
class SocialRepositoryTest {

    private lateinit var postsDao: PostsDao
    private lateinit var commentsDao: CommentsDao
    private lateinit var likesDao: LikesDao
    private lateinit var repo: SocialRepository

    @Before
    fun setup() {
        postsDao = mockk(relaxed = true)
        commentsDao = mockk(relaxed = true)
        likesDao = mockk(relaxed = true)
        repo = SocialRepositoryImpl(postsDao, commentsDao, likesDao)
    }

    @Test
    fun createPost_insertsPost() = runTest {
        val author = "user-1"
        val id = repo.createPost(authorId = author, type = "TEXT", text = "Hello", mediaUrl = null, thumbnailUrl = null, productId = null)
        // Verify DAO called
        coVerify { postsDao.upsert(match { it.postId == id && it.authorId == author && it.type == "TEXT" }) }
    }

    @Test
    fun addComment_insertsComment() = runTest {
        val postId = UUID.randomUUID().toString()
        val authorId = "user-2"
        repo.addComment(postId, authorId, "Nice!")
        coVerify { commentsDao.upsert(match { it.postId == postId && it.authorId == authorId && it.text == "Nice!" }) }
    }

    @Test
    fun likeAndUnlike_callsDao() = runTest {
        val postId = UUID.randomUUID().toString()
        val userId = "user-3"
        repo.like(postId, userId)
        coVerify { likesDao.upsert(match { it.postId == postId && it.userId == userId }) }
        repo.unlike(postId, userId)
        coVerify { likesDao.unlike(postId, userId) }
    }
}
