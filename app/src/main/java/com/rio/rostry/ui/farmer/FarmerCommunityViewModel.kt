package com.rio.rostry.ui.farmer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.community.CommunityEngagementService
import com.rio.rostry.data.database.entity.EventEntity
import com.rio.rostry.data.database.entity.GroupEntity
import com.rio.rostry.data.database.entity.PostEntity
import com.rio.rostry.data.repository.CommunityRepository
import com.rio.rostry.data.repository.social.MessagingRepository
import com.rio.rostry.data.database.entity.UserEntity
import com.rio.rostry.data.repository.UserRepository
import com.rio.rostry.session.CurrentUserProvider
import com.rio.rostry.session.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FarmerCommunityViewModel @Inject constructor(
    private val communityEngagementService: CommunityEngagementService,
    private val communityRepository: CommunityRepository,
    private val messagingRepository: MessagingRepository,
    private val userRepository: UserRepository,
    private val currentUserProvider: CurrentUserProvider,
    private val sessionManager: SessionManager
) : ViewModel() {
    
    sealed class SearchResult {
        object Empty : SearchResult()
        data class Success(val groups: List<GroupEntity>, val users: List<UserEntity>) : SearchResult()
    }

    private val userId: String?
        get() = currentUserProvider.userIdOrNull()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    val activeThreads: StateFlow<List<CommunityEngagementService.ThreadPreview>> = userId?.let { uid ->
        communityEngagementService.getActiveThreads(uid)
            .catch { emit(emptyList()) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    } ?: MutableStateFlow(emptyList())

    val suggestedGroups: StateFlow<List<GroupEntity>> = userId?.let { uid ->
        communityEngagementService.getSuggestedGroups(uid, 10)
            .catch { emit(emptyList()) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    } ?: MutableStateFlow(emptyList())

    val upcomingEvents: StateFlow<List<EventEntity>> = userId?.let { uid ->
        communityEngagementService.getUpcomingEvents(uid, 10)
            .catch { emit(emptyList()) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    } ?: MutableStateFlow(emptyList())

    val availableExperts: StateFlow<List<CommunityEngagementService.ExpertInfo>> = userId?.let { uid ->
        communityEngagementService.getAvailableExperts(null)
            .catch { emit(emptyList()) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    } ?: MutableStateFlow(emptyList())

    val trendingPosts: StateFlow<List<PostEntity>> = userId?.let { uid ->
        communityEngagementService.getTrendingPosts(uid, 20)
            .catch { emit(emptyList()) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    } ?: MutableStateFlow(emptyList())

    val unreadCount: StateFlow<Int> = userId?.let { uid ->
        messagingRepository.streamUnreadCount(uid)
            .catch { emit(0) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
    } ?: MutableStateFlow(0)

    fun refresh() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Refresh logic would trigger data re-fetch
                // For flows, they auto-refresh, but we could add manual triggers
            } finally {
                _isLoading.value = false
            }
        }
    }

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    @OptIn(kotlinx.coroutines.FlowPreview::class)
    val searchResults: StateFlow<SearchResult> = _searchQuery
        .debounce(300)
        .flatMapLatest { query ->
            if (query.isBlank()) {
                flowOf(SearchResult.Empty)
            } else {
                val groupsFlow = communityRepository.searchGroups(query)
                val usersFlow = userRepository.searchUsers(query)

                combine(groupsFlow, usersFlow) { groups, users ->
                    // Filter out current user from results
                    val filteredUsers = users.filter { it.userId != userId }
                    SearchResult.Success(groups, filteredUsers)
                }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SearchResult.Empty)

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun clearSearch() {
        _searchQuery.value = ""
    }

    fun startChat(withUserId: String, onSuccess: (String) -> Unit) {
        // Just create a simple thread context for P2P chat
        createThreadWithContext(withUserId, "DIRECT_MESSAGE", null, onSuccess)
    }

    fun createThreadWithContext(toUserId: String, contextType: String, relatedEntityId: String?, onSuccess: (String) -> Unit = {}) {
        viewModelScope.launch {
            try {
                val fromUserId = userId ?: return@launch
                val context = CommunityEngagementService.ThreadContext(
                    type = contextType,
                    relatedEntityId = relatedEntityId,
                    topic = null
                )
                val threadId = communityEngagementService.createContextualThread(fromUserId, toUserId, context)
                onSuccess(threadId)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun joinGroup(groupId: String) {
        viewModelScope.launch {
            try {
                val uid = userId ?: return@launch
                communityRepository.joinGroup(groupId, uid)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun rsvpToEvent(eventId: String, status: String) {
        viewModelScope.launch {
            try {
                val uid = userId ?: return@launch
                communityRepository.rsvpToEvent(eventId, uid, status)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun bookExpert(expertId: String, topic: String, startTime: Long) {
        viewModelScope.launch {
            try {
                val uid = userId ?: return@launch
                // Would create an expert booking
                createThreadWithContext(expertId, "EXPERT_CONSULT", null)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun dismissThread(threadId: String) {
        viewModelScope.launch {
            try {
                // Would implement thread dismissal/muting logic
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}
