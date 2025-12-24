package com.rio.rostry.ui.social.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.rio.rostry.data.database.entity.PostEntity
import com.rio.rostry.data.database.entity.UserEntity
import com.rio.rostry.data.repository.UserRepository
import com.rio.rostry.data.repository.social.SocialRepository
import com.rio.rostry.session.CurrentUserProvider
import com.rio.rostry.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SocialProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val socialRepository: SocialRepository,
    private val currentUserProvider: CurrentUserProvider
) : ViewModel() {

    private val _userId = MutableStateFlow<String?>(null)
    private val _isFollowLoading = MutableStateFlow(false)
    private val _followError = MutableStateFlow<String?>(null)
    
    val user: StateFlow<UserEntity?> = _userId
        .flatMapLatest { uid ->
            val targetId = uid ?: currentUserProvider.userIdOrNull()
            if (targetId != null) {
                userRepository.getUserById(targetId).map { it.data }
            } else {
                flowOf(null)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val posts: Flow<PagingData<PostEntity>> = _userId
        .flatMapLatest { uid ->
            val targetId = uid ?: currentUserProvider.userIdOrNull()
            if (targetId != null) {
                socialRepository.getUserPosts(targetId).cachedIn(viewModelScope)
            } else {
                flowOf(PagingData.empty())
            }
        }

    // Posts count derived from user posts (real data)
    val postsCount: StateFlow<Int> = _userId
        .flatMapLatest { uid ->
            val targetId = uid ?: currentUserProvider.userIdOrNull()
            if (targetId != null) {
                socialRepository.getUserPostsCount(targetId)
            } else {
                flowOf(0)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val followersCount: StateFlow<Int> = _userId
        .flatMapLatest { uid ->
            val targetId = uid ?: currentUserProvider.userIdOrNull()
            if (targetId != null) {
                socialRepository.getFollowersCount(targetId)
            } else {
                flowOf(0)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val followingCount: StateFlow<Int> = _userId
        .flatMapLatest { uid ->
            val targetId = uid ?: currentUserProvider.userIdOrNull()
            if (targetId != null) {
                socialRepository.getFollowingCount(targetId)
            } else {
                flowOf(0)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // Whether current user is following the profile being viewed
    val isFollowing: StateFlow<Boolean> = _userId
        .flatMapLatest { targetId ->
            val currentUserId = currentUserProvider.userIdOrNull()
            if (currentUserId != null && targetId != null && currentUserId != targetId) {
                socialRepository.isFollowing(currentUserId, targetId)
            } else {
                flowOf(false)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    // Whether this is the current user's own profile
    val isOwnProfile: StateFlow<Boolean> = _userId
        .map { targetId ->
            val currentUserId = currentUserProvider.userIdOrNull()
            currentUserId != null && (targetId == null || targetId == currentUserId)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val isFollowLoading: StateFlow<Boolean> = _isFollowLoading.asStateFlow()
    val followError: StateFlow<String?> = _followError.asStateFlow()

    fun bind(userId: String?) {
        if (userId != null) {
            _userId.value = userId
        } else {
            _userId.value = currentUserProvider.userIdOrNull()
        }
    }

    fun follow() {
        val currentUserId = currentUserProvider.userIdOrNull() ?: run {
            _followError.value = "Please sign in to follow users"
            return
        }
        val targetUserId = _userId.value ?: return
        
        if (currentUserId == targetUserId) {
            _followError.value = "You cannot follow yourself"
            return
        }

        viewModelScope.launch {
            _isFollowLoading.value = true
            try {
                socialRepository.follow(currentUserId, targetUserId)
            } catch (e: Exception) {
                _followError.value = e.message ?: "Failed to follow user"
            } finally {
                _isFollowLoading.value = false
            }
        }
    }

    fun unfollow() {
        val currentUserId = currentUserProvider.userIdOrNull() ?: return
        val targetUserId = _userId.value ?: return

        viewModelScope.launch {
            _isFollowLoading.value = true
            try {
                socialRepository.unfollow(currentUserId, targetUserId)
            } catch (e: Exception) {
                _followError.value = e.message ?: "Failed to unfollow user"
            } finally {
                _isFollowLoading.value = false
            }
        }
    }

    fun clearError() {
        _followError.value = null
    }
}
