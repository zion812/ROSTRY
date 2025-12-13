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

    // Placeholder stats (replace with real flows from repo if available)
    val followersCount = flowOf(120) // Mock
    val followingCount = flowOf(45)  // Mock
    val postsCount = flowOf(12)      // Mock

    fun bind(userId: String?) {
        if (userId != null) {
            _userId.value = userId
        } else {
            _userId.value = currentUserProvider.userIdOrNull()
        }
    }

    fun follow() {
        // TODO: Implement follow
    }

    fun unfollow() {
        // TODO: Implement unfollow
    }
}
