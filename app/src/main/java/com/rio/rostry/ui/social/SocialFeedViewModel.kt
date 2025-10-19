package com.rio.rostry.ui.social

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.rio.rostry.data.database.entity.PostEntity
import com.rio.rostry.data.repository.social.SocialRepository
import com.rio.rostry.session.CurrentUserProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class SocialFeedViewModel @Inject constructor(
    private val socialRepository: SocialRepository,
    private val currentUserProvider: CurrentUserProvider,
) : ViewModel() {
    fun feed(): Flow<PagingData<PostEntity>> = socialRepository.feedRanked().cachedIn(viewModelScope)

    val isAuthenticated: Boolean
        get() = currentUserProvider.isAuthenticated()

    private fun uidOrThrow(): String = currentUserProvider.userIdOrNull()
        ?: throw IllegalStateException("No authenticated user")

    suspend fun like(postId: String) = socialRepository.like(postId, uidOrThrow())
    suspend fun unlike(postId: String) = socialRepository.unlike(postId, uidOrThrow())
    suspend fun addComment(postId: String, text: String) = socialRepository.addComment(postId, uidOrThrow(), text)
}
