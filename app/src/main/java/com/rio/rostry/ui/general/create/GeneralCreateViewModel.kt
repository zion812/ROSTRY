package com.rio.rostry.ui.general.create

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.repository.social.SocialRepository
import com.rio.rostry.session.CurrentUserProvider
import com.rio.rostry.ui.general.analytics.GeneralAnalyticsTracker
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class GeneralCreateViewModel @Inject constructor(
    private val socialRepository: SocialRepository,
    private val currentUserProvider: CurrentUserProvider,
    private val analytics: GeneralAnalyticsTracker
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
                    socialRepository.createPost(
                        authorId = userId,
                        type = "TEXT",
                        text = payloadText.ifBlank { null },
                        mediaUrl = null,
                        thumbnailUrl = null,
                        productId = null
                    )
                } else {
                    val first = current.attachments.first()
                    socialRepository.createPostWithMedia(
                        authorId = userId,
                        type = if (first.isVideo) "VIDEO" else "IMAGE",
                        text = payloadText.ifBlank { null },
                        mediaUri = first.uri,
                        isVideo = first.isVideo
                    )
                }
            }.onSuccess {
                _uiState.value = CreateUiState(successMessage = "Post shared successfully!")
            }.onFailure { throwable ->
                _uiState.update { it.copy(isPosting = false, error = throwable.message ?: "Failed to share post") }
            }
        }
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
