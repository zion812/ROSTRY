package com.rio.rostry.ui.community

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.community.CommunityEngagementService
import com.rio.rostry.data.database.entity.EventEntity
import com.rio.rostry.data.database.entity.GroupEntity
import com.rio.rostry.data.database.entity.PostEntity
import com.rio.rostry.data.repository.CommunityRepository
import com.rio.rostry.data.repository.social.MessagingRepository
import com.rio.rostry.data.repository.social.SocialRepository
import com.rio.rostry.domain.model.UserType
import com.rio.rostry.session.CurrentUserProvider
import com.rio.rostry.session.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CommunityHubViewModel @Inject constructor(
    private val communityEngagementService: CommunityEngagementService,
    private val communityRepository: CommunityRepository,
    private val messagingRepository: MessagingRepository,
    private val socialRepository: SocialRepository,
    private val currentUserProvider: CurrentUserProvider,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val userId: String?
        get() = currentUserProvider.userIdOrNull()

    private val _selectedTab = MutableStateFlow(0)
    val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    val userType: StateFlow<UserType?> = sessionManager.sessionRole()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val activeThreads: StateFlow<List<MessagingRepository.ThreadWithMetadata>> = userId?.let { uid ->
        messagingRepository.streamUserThreadsWithMetadata(uid)
            .catch { emit(emptyList()) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    } ?: MutableStateFlow(emptyList())

    val suggestedConnections: StateFlow<List<CommunityEngagementService.ConnectionSuggestion>> = flow {
        userId?.let { uid ->
            val type = userType.first() ?: UserType.GENERAL
            val connections = communityEngagementService.suggestConnections(uid, type)
            emit(connections)
        } ?: emit(emptyList())
    }.catch { emit(emptyList()) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

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

    val userGroups: StateFlow<List<GroupEntity>> = userId?.let { uid ->
        communityRepository.getUserGroups(uid)
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
                // Trigger refresh logic - flows will auto-update
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun selectTab(index: Int) {
        _selectedTab.value = index
    }

    fun startThread(toUserId: String, contextType: String, relatedEntityId: String?) {
        viewModelScope.launch {
            try {
                val fromUserId = userId ?: return@launch
                val context = CommunityEngagementService.ThreadContext(
                    type = contextType,
                    relatedEntityId = relatedEntityId,
                    topic = null
                )
                communityEngagementService.createContextualThread(fromUserId, toUserId, context)
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

    fun leaveGroup(groupId: String) {
        viewModelScope.launch {
            try {
                val uid = userId ?: return@launch
                communityRepository.leaveGroup(groupId, uid)
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
                startThread(expertId, "EXPERT_CONSULT", null)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun dismissRecommendation(recommendationId: String) {
        viewModelScope.launch {
            try {
                // Would implement dismissal logic
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun filterThreadsByContext(contextType: String?) {
        // Would implement filtering logic
    }
}
