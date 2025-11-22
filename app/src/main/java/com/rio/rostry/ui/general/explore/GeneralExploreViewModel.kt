package com.rio.rostry.ui.general.explore

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.filter
import androidx.paging.cachedIn
import androidx.paging.map
import com.rio.rostry.data.database.dao.FollowsDao
import com.rio.rostry.data.database.entity.PostEntity
import com.rio.rostry.data.database.entity.UserEntity
import com.rio.rostry.data.repository.social.SocialRepository
import com.rio.rostry.data.repository.UserRepository
import com.rio.rostry.session.CurrentUserProvider
import com.rio.rostry.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.Locale
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.flatMapLatest

@HiltViewModel
class GeneralExploreViewModel @Inject constructor(
    private val socialRepository: SocialRepository,
    private val followsDao: FollowsDao,
    private val userRepository: UserRepository,
    private val currentUserProvider: CurrentUserProvider
) : ViewModel() {

    enum class ExploreFilter { RECENT, POPULAR, NEARBY, FOLLOWING }

    data class QueryTokens(
        val keywords: List<String> = emptyList(),
        val hashtags: List<String> = emptyList(),
        val mentions: List<String> = emptyList(),
        val locations: List<String> = emptyList(),
        val breeds: List<String> = emptyList(),
        val colors: List<String> = emptyList()
    ) {
        val isEmpty: Boolean
            get() = keywords.isEmpty() && hashtags.isEmpty() && mentions.isEmpty() &&
                locations.isEmpty() && breeds.isEmpty() && colors.isEmpty()
    }

    data class UserPreview(
        val userId: String,
        val displayName: String?,
        val headline: String?,
        val location: String?,
        val followers: Int?,
        val avatarUrl: String?,
        val isLoading: Boolean = false
    )

    data class ExploreUiState(
        val filter: ExploreFilter = ExploreFilter.RECENT,
        val query: String = "",
        val tokens: QueryTokens = QueryTokens(),
        val profilePreview: UserPreview? = null,
        val error: String? = null
    )

    data class EducationalContent(
        val id: String,
        val title: String,
        val description: String,
        val type: String, // ARTICLE, GUIDE, TOOL
        val imageUrl: String? = null
    )

    private val _educationalContent = MutableStateFlow(listOf(
        EducationalContent(
            id = "1",
            title = "Why Native?",
            description = "Discover the health and taste benefits of native breeds.",
            type = "ARTICLE",
            imageUrl = "https://example.com/native_chicken.jpg" // Placeholder
        ),
        EducationalContent(
            id = "2",
            title = "Meat Selection Guide",
            description = "Choose the right bird for your dish based on age and sex.",
            type = "TOOL",
            imageUrl = "https://example.com/meat_selection.jpg"
        ),
        EducationalContent(
            id = "3",
            title = "Start Backyard Farming",
            description = "A beginner's guide to raising your own flock.",
            type = "GUIDE",
            imageUrl = "https://example.com/backyard_farm.jpg"
        )
    ))
    val educationalContent: StateFlow<List<EducationalContent>> = _educationalContent.asStateFlow()

    private val filter = MutableStateFlow(ExploreFilter.RECENT)
    private val query = MutableStateFlow("")
    private val profilePreview = MutableStateFlow<UserPreview?>(null)
    private val error = MutableStateFlow<String?>(null)

    private val currentUserId = currentUserProvider.userIdOrNull()

    private val followingIds: StateFlow<List<String>> = (currentUserId?.let { uid ->
        followsDao.followingIds(uid)
    } ?: flowOf(emptyList())).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    private val tokens: StateFlow<QueryTokens> = query.map { parseQuery(it) }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = QueryTokens()
    )

    val uiState: StateFlow<ExploreUiState> = combine(filter, query, tokens, profilePreview, error) {
            filterValue, queryValue, tokensValue, preview, errorValue ->
        ExploreUiState(
            filter = filterValue,
            query = queryValue,
            tokens = tokensValue,
            profilePreview = preview,
            error = errorValue
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ExploreUiState()
    )

    val feed: Flow<PagingData<PostEntity>> = combine(filter, tokens, followingIds) { filterValue, tokensValue, followingList ->
        Triple(filterValue, tokensValue, followingList)
    }.flatMapLatest { (filterValue, tokensValue, followingList) ->
        val baseFlow = when (filterValue) {
            ExploreFilter.RECENT -> socialRepository.feed()
            ExploreFilter.POPULAR -> socialRepository.feedRanked()
            ExploreFilter.NEARBY -> socialRepository.feed()
            ExploreFilter.FOLLOWING -> socialRepository.feed()
        }
        baseFlow.map { pagingData ->
            pagingData.filter { post ->
                matchesFilter(post, filterValue, followingList) && matchesQuery(post, tokensValue)
            }
        }
    }.cachedIn(viewModelScope)

    fun setFilter(newFilter: ExploreFilter) {
        filter.value = newFilter
    }

    fun updateQuery(newQuery: String) {
        query.value = newQuery
    }

    fun clearError() {
        error.value = null
    }

    fun like(postId: String) {
        val userId = currentUserId ?: return
        viewModelScope.launch {
            runCatching { socialRepository.like(postId, userId) }
                .onFailure { throwable ->
                    error.value = throwable.message ?: "Unable to like post"
                }
        }
    }

    fun unlike(postId: String) {
        val userId = currentUserId ?: return
        viewModelScope.launch {
            runCatching { socialRepository.unlike(postId, userId) }
                .onFailure { throwable ->
                    error.value = throwable.message ?: "Unable to unlike post"
                }
        }
    }

    fun openProfilePreview(userId: String) {
        profilePreview.value = UserPreview(
            userId = userId,
            displayName = null,
            headline = null,
            location = null,
            followers = null,
            avatarUrl = null,
            isLoading = true
        )
        viewModelScope.launch {
            userRepository.getUserById(userId).collect { resource ->
                when (resource) {
                    is Resource.Error -> {
                        error.value = resource.message ?: "Unable to load profile"
                        profilePreview.value = null
                        return@collect
                    }
                    is Resource.Loading -> Unit
                    is Resource.Success -> {
                        val user = resource.data
                        profilePreview.value = UserPreview(
                            userId = userId,
                            displayName = user?.fullName,
                            headline = user?.address,
                            location = user?.address,
                            followers = null,
                            avatarUrl = user?.profilePictureUrl,
                            isLoading = false
                        )
                        return@collect
                    }
                }
            }
        }
    }

    fun closeProfilePreview() {
        profilePreview.value = null
    }

    private fun matchesFilter(post: PostEntity, filter: ExploreFilter, followingIds: List<String>): Boolean {
        return when (filter) {
            ExploreFilter.RECENT, ExploreFilter.POPULAR -> true
            ExploreFilter.NEARBY -> true // Nearby heuristic handled client-side; lacking geo metadata
            ExploreFilter.FOLLOWING -> followingIds.isEmpty() || followingIds.contains(post.authorId)
        }
    }

    private fun matchesQuery(post: PostEntity, tokens: QueryTokens): Boolean {
        if (tokens.isEmpty) return true
        val text = (post.text ?: "")
        val haystack = text.lowercase(Locale.getDefault())
        val keywordMatch = tokens.keywords.all { haystack.contains(it) }
        val hashtagsMatch = tokens.hashtags.all { haystack.contains(it) }
        val mentionsMatch = tokens.mentions.all { haystack.contains(it) }
        val locationMatch = tokens.locations.all { haystack.contains(it) }
        val breedMatch = tokens.breeds.all { haystack.contains(it) }
        val colorMatch = tokens.colors.all { haystack.contains(it) }
        return keywordMatch && hashtagsMatch && mentionsMatch && locationMatch && breedMatch && colorMatch
    }

    private fun parseQuery(raw: String): QueryTokens {
        if (raw.isBlank()) return QueryTokens()
        val keywords = mutableListOf<String>()
        val hashtags = mutableListOf<String>()
        val mentions = mutableListOf<String>()
        val locations = mutableListOf<String>()
        val breeds = mutableListOf<String>()
        val colors = mutableListOf<String>()
        raw.split(" ")
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .forEach { token ->
                when {
                    token.startsWith("#") -> hashtags += token.lowercase(Locale.getDefault())
                    token.startsWith("@") -> mentions += token.lowercase(Locale.getDefault())
                    token.startsWith("loc:", ignoreCase = true) -> locations += token.removePrefix("loc:").lowercase(Locale.getDefault())
                    token.startsWith("breed:", ignoreCase = true) -> breeds += token.removePrefix("breed:").lowercase(Locale.getDefault())
                    token.startsWith("color:", ignoreCase = true) -> colors += token.removePrefix("color:").lowercase(Locale.getDefault())
                    else -> keywords += token.lowercase(Locale.getDefault())
                }
            }
        return QueryTokens(
            keywords = keywords,
            hashtags = hashtags,
            mentions = mentions,
            locations = locations,
            breeds = breeds,
            colors = colors
        )
    }
}
