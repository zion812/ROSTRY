package com.rio.rostry.ui.enthusiast.profile

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.database.entity.ProductEntity
import com.rio.rostry.data.database.entity.ReputationEntity
import com.rio.rostry.data.database.entity.UserEntity
import com.rio.rostry.data.repository.ProductRepository
import com.rio.rostry.data.repository.UserRepository
import com.rio.rostry.data.repository.social.SocialRepository
import com.rio.rostry.data.database.dao.ShowRecordDao
import com.rio.rostry.data.database.dao.BreedingPairDao
import com.rio.rostry.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EnthusiastProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val socialRepository: SocialRepository,
    private val productRepository: ProductRepository,
    private val showRecordDao: ShowRecordDao,
    private val breedingPairDao: BreedingPairDao
) : ViewModel() {

    data class UiState(
        val user: UserEntity? = null,
        val reputation: ReputationEntity? = null,
        val birds: List<ProductEntity> = emptyList(),
        val totalBirds: Int = 0,
        val totalShowWins: Int = 0,
        val totalShows: Int = 0,
        val activePairs: Int = 0,
        val isLoading: Boolean = true,
        val error: String? = null,
        val isSaving: Boolean = false,
        val saveSuccess: Boolean = false
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private var loadJob: kotlinx.coroutines.Job? = null

    init {
        loadProfile()
    }

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    fun loadProfile() {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            userRepository.getCurrentUser()
                .flatMapLatest { userRes ->
                    if (userRes is Resource.Success<UserEntity?> && userRes.data != null) {
                        val user = userRes.data
                        val userId = user.userId

                        combine(
                            socialRepository.getReputation(userId),
                            productRepository.getProductsBySeller(userId)
                        ) { rep, productsRes ->
                            val birds = productsRes.data ?: emptyList()

                            // Fetch show record stats
                            val showWins = try {
                                showRecordDao.countWinsByOwner(userId)
                            } catch (_: Exception) { 0 }

                            val totalShows = try {
                                showRecordDao.countTotalByOwner(userId)
                            } catch (_: Exception) { 0 }

                            // Fetch active breeding pairs
                            val activePairs = try {
                                breedingPairDao.countActive(userId)
                            } catch (_: Exception) { 0 }

                            UiState(
                                user = user,
                                reputation = rep,
                                birds = birds,
                                totalBirds = birds.size,
                                totalShowWins = showWins,
                                totalShows = totalShows,
                                activePairs = activePairs,
                                isLoading = false
                            )
                        }
                    } else if (userRes is Resource.Error) {
                        flowOf(_uiState.value.copy(isLoading = false, error = userRes.message))
                    } else {
                        flowOf(_uiState.value.copy(isLoading = true))
                    }
                }
                .collect { newState ->
                    _uiState.value = newState
                }
        }
    }

    fun updateProfile(updatedUser: UserEntity) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true)
            when (userRepository.updateUserProfile(updatedUser)) {
                is Resource.Success -> {
                    _uiState.value = _uiState.value.copy(isSaving = false, saveSuccess = true)
                    loadProfile()
                }
                is Resource.Error -> {
                    _uiState.value = _uiState.value.copy(isSaving = false, error = "Failed to save profile")
                }
                else -> _uiState.value = _uiState.value.copy(isSaving = false)
            }
        }
    }

    fun uploadProfileImage(imageUri: Uri) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true)
            val userId = _uiState.value.user?.userId ?: return@launch
            when (userRepository.uploadProfileImage(userId, imageUri)) {
                is Resource.Success -> loadProfile()
                is Resource.Error -> _uiState.value = _uiState.value.copy(isSaving = false, error = "Failed to upload image")
                else -> _uiState.value = _uiState.value.copy(isSaving = false)
            }
        }
    }

    fun clearSaveSuccess() {
        _uiState.value = _uiState.value.copy(saveSuccess = false)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
