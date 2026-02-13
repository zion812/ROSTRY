package com.rio.rostry.ui.enthusiast.creation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.database.entity.ProductEntity
import com.rio.rostry.data.repository.ProductRepository
import com.rio.rostry.domain.showcase.ShowcaseCard
import com.rio.rostry.domain.showcase.ShowcaseCardGenerator
import com.rio.rostry.domain.showcase.ShowcaseConfig
import com.rio.rostry.domain.showcase.ShowcaseTheme
import com.rio.rostry.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShowcaseCardGeneratorViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val showcaseCardGenerator: ShowcaseCardGenerator,
    private val showRecordDao: com.rio.rostry.data.database.dao.ShowRecordDao,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val productId: String = checkNotNull(savedStateHandle["productId"])

    private val _uiState = MutableStateFlow(ShowcaseUiState())
    val uiState: StateFlow<ShowcaseUiState> = _uiState.asStateFlow()

    init {
        loadProduct()
    }

    private fun loadProduct() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            // Fetch product directly for now, or observe if needed
            val productRes = productRepository.getProductById(productId)
            
            productRes.collect { result ->
                when (result) {
                    is Resource.Success -> {
                        _uiState.update { 
                            it.copy(
                                isLoading = false,
                                product = result.data
                            )
                        }
                    }
                    is Resource.Error -> {
                        _uiState.update { 
                            it.copy(
                                isLoading = false,
                                error = result.message
                            )
                        }
                    }
                    is Resource.Loading -> {
                        _uiState.update { it.copy(isLoading = true) }
                    }
                }
            }
        }
    }

    fun updateConfig(config: ShowcaseConfig) {
        _uiState.update { it.copy(config = config) }
    }
    
    fun generateCard(onSuccess: (ShowcaseCard) -> Unit) {
        val state = uiState.value
        val product = state.product ?: return
        
        viewModelScope.launch {
            _uiState.update { it.copy(isGenerating = true) }
            
            // Fetch real stats
            val stats = try {
                showRecordDao.getStatsByProduct(productId)
            } catch (e: Exception) {
                emptyList()
            }
            
            // Calculate vaccination count (approximate from JSON check or 0 for now)
            val vaccineCount = if (!product.vaccinationRecordsJson.isNullOrEmpty()) 1 else 0

            val result = showcaseCardGenerator.generateCard(
                bird = product,
                config = state.config,
                stats = stats,
                vaccinationCount = vaccineCount
            )
            
            _uiState.update { it.copy(isGenerating = false) }
            
            when (result) {
                is Resource.Success -> {
                    result.data?.let(onSuccess)
                }
                is Resource.Error -> {
                    _uiState.update { it.copy(error = result.message) }
                }
                else -> {}
            }
        }
    }
}

data class ShowcaseUiState(
    val isLoading: Boolean = false,
    val isGenerating: Boolean = false,
    val product: ProductEntity? = null,
    val error: String? = null,
    val config: ShowcaseConfig = ShowcaseConfig(
        theme = ShowcaseTheme.DARK_PREMIUM,
        showPedigreeBadge = true,
        showVaccinationBadge = true,
        showWins = true,
        showAge = true
    )
)
