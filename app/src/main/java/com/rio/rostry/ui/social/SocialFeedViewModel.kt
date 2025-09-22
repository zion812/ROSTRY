package com.rio.rostry.ui.social

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.rio.rostry.data.database.entity.PostEntity
import com.rio.rostry.data.repository.social.SocialRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class SocialFeedViewModel @Inject constructor(
    private val socialRepository: SocialRepository,
) : ViewModel() {
    fun feed(): Flow<PagingData<PostEntity>> = socialRepository.feedRanked().cachedIn(viewModelScope)

    suspend fun like(postId: String, userId: String) = socialRepository.like(postId, userId)
    suspend fun unlike(postId: String, userId: String) = socialRepository.unlike(postId, userId)
    suspend fun addComment(postId: String, userId: String, text: String) = socialRepository.addComment(postId, userId, text)
}
