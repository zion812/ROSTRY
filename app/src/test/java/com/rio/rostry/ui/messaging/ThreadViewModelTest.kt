package com.rio.rostry.ui.messaging

import com.rio.rostry.data.database.dao.OutgoingMessageDao
import com.rio.rostry.data.database.entity.OutgoingMessageEntity
import com.rio.rostry.data.repository.social.MessagingRepository
import com.rio.rostry.session.CurrentUserProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

private class FakeOutgoingDao : OutgoingMessageDao {
    val saved = mutableListOf<OutgoingMessageEntity>()
    override suspend fun upsert(msg: OutgoingMessageEntity) { saved.add(msg) }
    override suspend fun update(msg: OutgoingMessageEntity) {
        val i = saved.indexOfFirst { it.id == msg.id }
        if (i >= 0) saved[i] = msg else saved.add(msg)
    }
    override suspend fun getByStatus(status: String, limit: Int): List<OutgoingMessageEntity> =
        saved.filter { it.status == status }.take(limit)
    override suspend fun updateStatus(id: String, status: String) {
        val i = saved.indexOfFirst { it.id == id }
        if (i >= 0) saved[i] = saved[i].copy(status = status)
    }
}

private class FakeMessagingRepo : MessagingRepository {
    var lastSeen: Pair<String, String>? = null
    override suspend fun sendDirectMessage(threadId: String, fromUserId: String, toUserId: String, text: String) {}
    override fun streamThread(threadId: String): Flow<List<MessagingRepository.MessageDTO>> = flowOf(emptyList())
    override suspend fun sendGroupMessage(groupId: String, fromUserId: String, text: String) {}
    override fun streamGroup(groupId: String): Flow<List<MessagingRepository.MessageDTO>> = flowOf(emptyList())
    override suspend fun sendDirectFile(threadId: String, fromUserId: String, toUserId: String, fileUri: android.net.Uri, fileName: String) {}
    override suspend fun sendGroupFile(groupId: String, fromUserId: String, fileUri: android.net.Uri, fileName: String) {}
    override suspend fun markThreadSeen(threadId: String, userId: String) { lastSeen = threadId to userId }
    override fun streamUserThreads(userId: String): Flow<List<String>> = flowOf(emptyList())
    override fun streamUnreadCount(userId: String): Flow<Int> = flowOf(0)
    override suspend fun createThreadWithContext(fromUserId: String, toUserId: String, context: MessagingRepository.ThreadContext): String = ""
    override suspend fun updateThreadMetadata(threadId: String, title: String?, lastMessageAt: Long) {}
    override fun streamThreadMetadata(threadId: String): Flow<MessagingRepository.ThreadMetadata?> = flowOf(null)
    override fun streamUserThreadsWithMetadata(userId: String): Flow<List<MessagingRepository.ThreadWithMetadata>> = flowOf(emptyList())
}

private class FakeUserProvider(private val id: String?) : CurrentUserProvider { override fun userIdOrNull(): String? = id }

class ThreadViewModelTest {
    @Test
    fun bind_marksThreadSeen_withCurrentUser() = runBlocking {
        val repo = FakeMessagingRepo()
        val dao = FakeOutgoingDao()
        val vm = ThreadViewModel(repo, dao, FakeUserProvider("u_9"))
        vm.bind("t1")
        // collect happens asynchronously; markThreadSeen is called in a coroutine
        // Give a brief opportunity (in real tests, use runTest/TestCoroutineDispatcher)
        Thread.sleep(10)
        assertEquals("t1" to "u_9", repo.lastSeen)
    }

    @Test
    fun sendQueuedDmSelf_usesCurrentUserProvider() = runBlocking {
        val repo = FakeMessagingRepo()
        val dao = FakeOutgoingDao()
        val vm = ThreadViewModel(repo, dao, FakeUserProvider("me_1"))
        vm.sendQueuedDmSelf(threadId = "threadA", toUserId = "you_2", text = "hi")
        assertEquals(1, dao.saved.size)
        val msg = dao.saved.first()
        assertEquals("threadA", msg.threadOrGroupId)
        assertEquals("me_1", msg.fromUserId)
        assertEquals("you_2", msg.toUserId)
        assertEquals("hi", msg.bodyText)
    }
}
