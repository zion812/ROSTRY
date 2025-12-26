package com.rio.rostry.ui.general.explore

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.rio.rostry.data.database.dao.PostsDao
import com.rio.rostry.data.database.entity.BreedEntity
import com.rio.rostry.data.database.entity.PostEntity
import com.rio.rostry.data.database.entity.ProductEntity
import com.rio.rostry.data.repository.LikesRepository
import com.rio.rostry.data.repository.UserRepository
import com.rio.rostry.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GeneralExploreViewModel @Inject constructor(
    private val postsDao: PostsDao,
    private val userDao: com.rio.rostry.data.database.dao.UserDao,
    private val likesRepository: LikesRepository,
    private val userRepository: UserRepository,
    private val breedRepository: com.rio.rostry.data.repository.BreedRepository,
    private val productRepository: com.rio.rostry.data.repository.ProductRepository
) : ViewModel() {

    data class UiState(
        val query: String = "",
        val filter: ExploreFilter = ExploreFilter.ALL,
        val error: String? = null,
        val profilePreview: UserPreview? = null,
        val recommendedBreeds: List<BreedEntity> = emptyList(),
        val starterKits: List<ProductEntity> = emptyList(),
        val nearbyFarmers: List<com.rio.rostry.data.database.entity.UserEntity> = emptyList(),
        val dailyTip: com.rio.rostry.domain.model.EducationalContent? = null,
        val educationalContent: List<com.rio.rostry.domain.model.EducationalContent> = emptyList()
    )

    data class UserPreview(
        val userId: String,
        val displayName: String?,
        val avatarUrl: String?,
        val location: String?
    )

    enum class ExploreFilter {
        ALL, FOLLOWING, POPULAR, NEARBY
    }

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        loadDailyContent()
    }

    private fun loadDailyContent() {
        viewModelScope.launch {
            // Load breeds
            launch {
                breedRepository.getAllBreeds().collect { breeds ->
                    _uiState.update { it.copy(recommendedBreeds = breeds.take(5)) }
                }
            }
            
            // Load educational content (Mock for now, but simulated async load)
            launch {
                val content = listOf(
                    com.rio.rostry.domain.model.EducationalContent("1", "Native Breeds: Nutritional Benefits", "Why native chicken meat is healthier.", com.rio.rostry.domain.model.ContentType.ARTICLE, "https://firebasestorage.googleapis.com/v0/b/rostry-dev.appspot.com/o/demo%2Fkadaknath.jpg?alt=media", duration = 5),
                    com.rio.rostry.domain.model.EducationalContent("2", "Choosing the Right Bird for Your Dish", "Guide to selecting birds based on age and tenderness.", com.rio.rostry.domain.model.ContentType.GUIDE, "https://firebasestorage.googleapis.com/v0/b/rostry-dev.appspot.com/o/demo%2Fcooking_guide.jpg?alt=media", duration = 8),
                    com.rio.rostry.domain.model.EducationalContent("3", "Backyard Farming 101", "Getting started with your own coop.", com.rio.rostry.domain.model.ContentType.TUTORIAL, "https://firebasestorage.googleapis.com/v0/b/rostry-dev.appspot.com/o/demo%2Fcoop_setup.jpg?alt=media", duration = 12),
                    com.rio.rostry.domain.model.EducationalContent("4", "Video: Vaccinating Chicks", "Step-by-step video guide.", com.rio.rostry.domain.model.ContentType.VIDEO, "https://firebasestorage.googleapis.com/v0/b/rostry-dev.appspot.com/o/demo%2Fvaccination_thumb.jpg?alt=media", duration = 4),
                    com.rio.rostry.domain.model.EducationalContent("5", "Recipe: Spicy Country Chicken", "Traditional slow-cooked recipe.", com.rio.rostry.domain.model.ContentType.GUIDE, "https://firebasestorage.googleapis.com/v0/b/rostry-dev.appspot.com/o/demo%2Fcurry.jpg?alt=media", duration = 45)
                )
                
                val tip = com.rio.rostry.domain.model.EducationalContent(
                    "tip1", 
                    "Did you know?", 
                    "Local free-range eggs have 1/3 less cholesterol and 1/4 less saturated fat than commercial eggs.", 
                    com.rio.rostry.domain.model.ContentType.TIP,
                    null
                )

                _uiState.update { it.copy(educationalContent = content, dailyTip = tip) }
            }

            // Load nearby farmers
            launch {
                try {
                    // Simulating geospatial search by fetching all farmers and taking some
                    // In real app, use GeoFire or specific query
                    val all = userDao.getAllUsers().first()
                    val farmers = all.filter { 
                        it.role == com.rio.rostry.domain.model.UserType.FARMER || 
                        it.role == com.rio.rostry.domain.model.UserType.ENTHUSIAST 
                    }.take(10)
                    _uiState.update { it.copy(nearbyFarmers = farmers) }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    val feed: Flow<PagingData<PostEntity>> = Pager(
        config = PagingConfig(pageSize = 20)
    ) {
        postsDao.paging()
    }.flow.cachedIn(viewModelScope)

    fun updateQuery(newQuery: String) {
        _uiState.update { it.copy(query = newQuery) }
    }

    fun setFilter(filter: ExploreFilter) {
        _uiState.update { it.copy(filter = filter) }
    }

    fun like(postId: String) {
        viewModelScope.launch {
            likesRepository.likePost(postId)
        }
    }

    fun unlike(postId: String) {
        viewModelScope.launch {
            likesRepository.unlikePost(postId)
        }
    }

    fun openProfilePreview(userId: String) {
        viewModelScope.launch {
            try {
                val res = userRepository.getUserById(userId).first()
                if (res is Resource.Success) {
                     val user = res.data
                     if (user != null) {
                        _uiState.update {
                            it.copy(
                                profilePreview = UserPreview(
                                    userId = user.userId,
                                    displayName = user.fullName,
                                    avatarUrl = user.profilePictureUrl,
                                    location = user.address
                                )
                            )
                        }
                    }
                } else if (res is Resource.Error) {
                    _uiState.update { it.copy(error = "Failed to load profile") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Failed to load profile: ${e.message}") }
            }
        }
    }

    fun closeProfilePreview() {
        _uiState.update { it.copy(profilePreview = null) }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun filterBreedsByTaste(profile: String) {
        viewModelScope.launch {
            breedRepository.getBreedsByCulinaryProfile(profile).collect { breeds ->
                 _uiState.update { it.copy(recommendedBreeds = breeds) }
            }
        }
    }

    fun fetchStarterKits() {
        // Future implementation: filter products by category 'Starter Kit'
    }
}
