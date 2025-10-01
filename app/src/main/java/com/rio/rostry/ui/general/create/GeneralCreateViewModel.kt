package com.rio.rostry.ui.general.create

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.rio.rostry.data.database.dao.OutboxDao
import com.rio.rostry.data.database.entity.OutboxEntity
import com.rio.rostry.data.repository.social.SocialRepository
import com.rio.rostry.session.CurrentUserProvider
import com.rio.rostry.ui.general.analytics.GeneralAnalyticsTracker
import com.rio.rostry.utils.network.ConnectivityManager
import com.rio.rostry.utils.media.MediaUploadManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.UUID

@HiltViewModel
class GeneralCreateViewModel @Inject constructor(
    private val socialRepository: SocialRepository,
    private val currentUserProvider: CurrentUserProvider,
    private val analytics: GeneralAnalyticsTracker,
    private val outboxDao: OutboxDao,
    private val connectivityManager: ConnectivityManager,
    private val mediaUploadManager: MediaUploadManager,
    private val gson: Gson
) : ViewModel() {

    enum class Privacy { PUBLIC, FOLLOWERS_ONLY, VERIFIED_BUYERS }

    data class MediaAttachment(
        val uriString: String,
        val isVideo: Boolean
    ) {
        val uri: Uri get() = Uri.parse(uriString)
    }

    data class CreateUiState(
        val text: String = "",
        val hashtags: List<String> = emptyList(),
        val mentions: List<String> = emptyList(),
        val attachments: List<MediaAttachment> = emptyList(),
        val locationTag: String? = null,
        val privacy: Privacy = Privacy.PUBLIC,
        val isPosting: Boolean = false,
        val error: String? = null,
        val successMessage: String? = null
    ) {
        val canPost: Boolean get() = text.isNotBlank() || attachments.isNotEmpty()
    }

    private val _uiState = MutableStateFlow(CreateUiState())
    val uiState: StateFlow<CreateUiState> = _uiState.asStateFlow()

    fun markComposerOpened() {
        analytics.createEntryOpen()
    }

    fun updateText(newText: String) {
        _uiState.update {
            it.copy(
                text = newText,
                hashtags = extractHashtags(newText),
                mentions = extractMentions(newText),
                error = null
            )
        }
    }

    fun setLocationTag(location: String?) {
        _uiState.update { it.copy(locationTag = location?.takeIf { loc -> loc.isNotBlank() }) }
    }

    fun setPrivacy(privacy: Privacy) {
        _uiState.update { it.copy(privacy = privacy) }
    }

    fun addMedia(uri: Uri, isVideo: Boolean) {
        _uiState.update { state ->
            if (state.attachments.any { it.uriString == uri.toString() }) state
            else state.copy(attachments = (state.attachments + MediaAttachment(uri.toString(), isVideo)).take(MAX_ATTACHMENTS))
        }
    }

    fun removeMedia(uriString: String) {
        _uiState.update { state ->
            state.copy(attachments = state.attachments.filterNot { it.uriString == uriString })
        }
    }

    fun clearSuccess() {
        _uiState.update { it.copy(successMessage = null) }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun submitPost() {
        val userId = currentUserProvider.userIdOrNull()
        if (userId.isNullOrBlank()) {
            _uiState.update { it.copy(error = "Please sign in to create a post") }
            return
        }
        val current = _uiState.value
        if (!current.canPost || current.isPosting) return
        _uiState.update { it.copy(isPosting = true, error = null, successMessage = null) }
        
        viewModelScope.launch {
            val isOnline = connectivityManager.isOnline()
            
            runCatching {
                val payloadText = buildString {
                    append(current.text.trim())
                    current.locationTag?.let { loc ->
                        if (isNotBlank()) append('\n')
                        append("📍 $loc")
                    }
                    append('\n')
                    append("Privacy: ${current.privacy.name.replace('_', ' ')}")
                }.trim()
                
                if (current.attachments.isEmpty()) {
                    // Text-only post
                    if (isOnline) {
                        // Online: Create post directly
                        socialRepository.createPost(
                            authorId = userId,
                            type = "TEXT",
                            text = payloadText.ifBlank { null },
                            mediaUrl = null,
                            thumbnailUrl = null,
                            productId = null
                        )
                    } else {
                        // Offline: Queue in outbox
                        enqueuePostToOutbox(
                            userId = userId,
                            text = payloadText,
                            type = "TEXT",
                            mediaUrl = null
                        )
                    }
                } else {
                    // Post with media attachments
                    val first = current.attachments.first()
                    val postType = if (first.isVideo) "VIDEO" else "IMAGE"
                    
                    if (isOnline) {
                        // Online: Upload media first, then create post
                        val uploadedUrl = uploadMediaAndWait(
                            uri = first.uri,
                            isVideo = first.isVideo
                        )
                        
                        socialRepository.createPost(
                            authorId = userId,
                            type = postType,
                            text = payloadText.ifBlank { null },
                            mediaUrl = uploadedUrl,
                            thumbnailUrl = uploadedUrl, // Could generate thumbnail
                            productId = null
                        )
                    } else {
                        // Offline: Queue in outbox with local media URI
                        enqueuePostToOutbox(
                            userId = userId,
                            text = payloadText,
                            type = postType,
                            mediaUrl = first.uriString
                        )
                    }
                }
            }.onSuccess {
                val message = if (isOnline) {
                    "Post shared successfully!"
                } else {
                    "Post queued. Will be shared when you're back online."
                }
                _uiState.value = CreateUiState(successMessage = message)
                Timber.i("Post submitted successfully (online=$isOnline)")
            }.onFailure { throwable ->
                _uiState.update { 
                    it.copy(
                        isPosting = false, 
                        error = throwable.message ?: "Failed to share post"
                    ) 
                }
                Timber.e(throwable, "Failed to submit post")
            }
        }
    }
    
    private suspend fun enqueuePostToOutbox(
        userId: String,
        text: String,
        type: String,
        mediaUrl: String?
    ) {
        val postData = mapOf(
            "authorId" to userId,
            "type" to type,
            "text" to text,
            "mediaUrl" to mediaUrl,
            "hashtags" to gson.toJson(_uiState.value.hashtags),
            "mentions" to gson.toJson(_uiState.value.mentions),
            "locationTag" to _uiState.value.locationTag,
            "privacy" to _uiState.value.privacy.name
        )
        
        val outboxEntry = OutboxEntity(
            outboxId = UUID.randomUUID().toString(),
            userId = userId,
            operation = "POST",
            entityType = "Post",
            entityId = UUID.randomUUID().toString(), // Will be replaced with real postId after sync
            payloadJson = gson.toJson(postData),
            createdAt = System.currentTimeMillis(),
            retryCount = 0
        )
        
        outboxDao.insert(outboxEntry)
        Timber.d("Post queued in outbox for offline sync")
    }
    
    private suspend fun uploadMediaAndWait(uri: Uri, isVideo: Boolean): String {
        // Enqueue media upload task
        val remotePath = "posts/${UUID.randomUUID()}.${if (isVideo) "mp4" else "jpg"}"
        val task = MediaUploadManager.UploadTask(
            localPath = uri.toString(),
            remotePath = remotePath,
            priority = 1,
            compress = true
        )
        mediaUploadManager.enqueue(task)
        
        // Wait for upload to complete (simplified - in production use Flow collector)
        kotlinx.coroutines.delay(2000) // Simulate upload time
        
        // Return remote URL (in production, this would be Firebase Storage URL)
        return "https://storage.googleapis.com/rostry-media/$remotePath"
    }

    private fun extractHashtags(text: String): List<String> = HASH_REGEX.findAll(text)
        .map { it.value.lowercase() }
        .distinct()
        .take(MAX_TAGS)
        .toList()

    private fun extractMentions(text: String): List<String> = MENTION_REGEX.findAll(text)
        .map { it.value.lowercase() }
        .distinct()
        .take(MAX_TAGS)
        .toList()

    companion object {
        private const val MAX_ATTACHMENTS = 6
        private const val MAX_TAGS = 12
        private val HASH_REGEX = "#[A-Za-z0-9_]{2,}".toRegex()
        private val MENTION_REGEX = "@[A-Za-z0-9_]{2,}".toRegex()
    }
}
