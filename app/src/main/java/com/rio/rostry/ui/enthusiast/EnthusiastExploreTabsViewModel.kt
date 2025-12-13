package com.rio.rostry.ui.enthusiast

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.rio.rostry.data.database.dao.PostsDao
import com.rio.rostry.data.database.dao.EventsDao
import com.rio.rostry.data.database.dao.EventRsvpsDao
import com.rio.rostry.data.repository.ProductRepository
import com.rio.rostry.data.database.dao.LikesDao
import com.rio.rostry.data.database.dao.CommentsDao
import com.rio.rostry.utils.Resource
import com.rio.rostry.data.database.entity.ProductEntity
import com.rio.rostry.data.database.entity.EventEntity
import com.rio.rostry.data.database.entity.EventRsvpEntity
import com.rio.rostry.data.database.entity.PostEntity
import com.rio.rostry.data.database.entity.LikeEntity
import com.rio.rostry.data.database.entity.CommentEntity
import com.rio.rostry.utils.media.MediaUploadManager
import com.rio.rostry.utils.images.ImageCompressor
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.UUID

@HiltViewModel
class EnthusiastExploreTabsViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val eventsDao: EventsDao,
    private val rsvpsDao: EventRsvpsDao,
    private val postsDao: PostsDao,
    private val likesDao: LikesDao,
    private val commentsDao: CommentsDao,
    private val uploadManager: MediaUploadManager,
    private val auth: FirebaseAuth,
    @dagger.hilt.android.qualifiers.ApplicationContext private val appContext: android.content.Context,
) : ViewModel() {

    data class UiState(
        val products: List<ProductEntity> = emptyList(),
        val events: List<EventEntity> = emptyList(),
        val rsvps: List<EventRsvpEntity> = emptyList(),
        val showcasePosts: List<PostEntity> = emptyList(),
        val loading: Boolean = false,
        val error: String? = null
    )

    private val _state = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = _state.asStateFlow()

    init {
        loadTab(0) // Load Products by default
    }

    fun loadTab(index: Int) {
        viewModelScope.launch {
            _state.value = _state.value.copy(loading = true, error = null)
            try {
                when (index) {
                    0 -> loadProducts()
                    1 -> loadEvents()
                    2 -> loadShowcase()
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(loading = false, error = e.message)
            }
        }
    }

    private suspend fun loadProducts() {
        val products = when (val result = productRepository.getAllProducts().first()) {
            is com.rio.rostry.utils.Resource.Success -> result.data?.take(50) ?: emptyList()
            else -> emptyList()
        }
        _state.value = _state.value.copy(products = products, loading = false)
    }

    private suspend fun loadEvents() {
        val userId = auth.currentUser?.uid ?: ""
        val now = System.currentTimeMillis()
        val events = eventsDao.streamUpcoming(now).first().take(50)
        val rsvps = rsvpsDao.streamForUser(userId).first()
        _state.value = _state.value.copy(events = events, rsvps = rsvps, loading = false)
    }

    private suspend fun loadShowcase() {
        // Fetch showcase posts (types: PRODUCT_SHOWCASE, BREEDING_ACHIEVEMENT)
        val all = postsDao.getTrending(50)
        val showcase = all.filter { it.type in listOf("PRODUCT_SHOWCASE", "BREEDING_ACHIEVEMENT") }
        _state.value = _state.value.copy(showcasePosts = showcase, loading = false)
    }

    fun rsvpToEvent(eventId: String, status: String) {
        val userId = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            try {
                rsvpsDao.upsert(
                    EventRsvpEntity(
                        id = UUID.randomUUID().toString(),
                        eventId = eventId,
                        userId = userId,
                        status = status,
                        updatedAt = System.currentTimeMillis()
                    )
                )
                loadEvents() // refresh
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = e.message)
            }
        }
    }

    fun createEvent(
        title: String,
        startTime: Long,
        endTime: Long?,
        location: String?,
        description: String?,
        bannerLocalUri: String?
    ) {
        val userId = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            try {
                _state.value = _state.value.copy(loading = true, error = null)
                val bannerRemote = bannerLocalUri?.let {
                    uploadAndAwaitRemote("events/${'$'}userId/${'$'}{System.currentTimeMillis()}_banner.jpg", it)
                }
                val ev = com.rio.rostry.data.database.entity.EventEntity(
                    eventId = UUID.randomUUID().toString(),
                    groupId = null,
                    title = title,
                    description = description,
                    location = location,
                    startTime = startTime,
                    endTime = endTime
                )
                eventsDao.upsert(ev)
                // Auto-RSVP creator as GOING
                rsvpsDao.upsert(
                    com.rio.rostry.data.database.entity.EventRsvpEntity(
                        id = UUID.randomUUID().toString(),
                        eventId = ev.eventId,
                        userId = userId,
                        status = "GOING",
                        updatedAt = System.currentTimeMillis()
                    )
                )
                loadEvents()
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = e.message, loading = false)
            }
        }
    }

    fun likePost(postId: String) {
        val userId = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            try {
                likesDao.insert(
                    LikeEntity(
                        likeId = UUID.randomUUID().toString(),
                        postId = postId,
                        userId = userId,
                        createdAt = System.currentTimeMillis()
                    )
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = e.message)
            }
        }
    }

    fun commentOnPost(postId: String, text: String) {
        val userId = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            try {
                commentsDao.upsert(
                    CommentEntity(
                        commentId = UUID.randomUUID().toString(),
                        postId = postId,
                        authorId = userId,
                        text = text,
                        createdAt = System.currentTimeMillis()
                    )
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = e.message)
            }
        }
    }

    fun createShowcase(
        caption: String?,
        localUri: String,
        linkedProductId: String?
    ) {
        val userId = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            try {
                _state.value = _state.value.copy(loading = true, error = null)
                val now = System.currentTimeMillis()
                val remote = uploadAndAwaitRemote("showcase/${'$'}userId/${'$'}now", localUri)
                val post = com.rio.rostry.data.database.entity.PostEntity(
                    postId = UUID.randomUUID().toString(),
                    authorId = userId,
                    type = "PRODUCT_SHOWCASE",
                    text = caption,
                    mediaUrl = remote,
                    thumbnailUrl = null,
                    productId = linkedProductId,
                    createdAt = now,
                    updatedAt = now,
                    hashtags = null,
                    mentions = null,
                    parentPostId = null
                )
                postsDao.upsert(post)
                loadShowcase()
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = e.message, loading = false)
            }
        }
    }

    private suspend fun uploadAndAwaitRemote(remotePath: String, localUriString: String): String {
        // Simple await: enqueue and return the remote path used. Progress subscribers handled by other screens.
        // For now, we treat the remotePath as addressable URL.
        return try {
            val uri = android.net.Uri.parse(localUriString)
            val inputFile = kotlin.run {
                val inStream = appContext.contentResolver.openInputStream(uri)
                val tmp = java.io.File.createTempFile("upl_", ".bin", appContext.cacheDir)
                inStream?.use { input -> java.io.FileOutputStream(tmp).use { out -> input.copyTo(out) } }
                tmp
            }
            val compressed = ImageCompressor.compressForUpload(appContext, inputFile, lowBandwidth = false)
            uploadManager.enqueue(MediaUploadManager.UploadTask(localPath = compressed.absolutePath, remotePath = remotePath))
            remotePath
        } catch (_: Exception) {
            remotePath
        }
    }
}

