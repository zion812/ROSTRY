package com.rio.rostry.ui.enthusiast.pedigree

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.database.entity.ProductEntity
import com.rio.rostry.data.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the Pedigree Screen.
 * Shows product details for pedigree view.
 */
@HiltViewModel
class PedigreeViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val productId: String = savedStateHandle["productId"] ?: ""

    private val _uiState = MutableStateFlow<PedigreeUiState>(PedigreeUiState.Loading)
    val uiState: StateFlow<PedigreeUiState> = _uiState.asStateFlow()

    init {
        if (productId.isNotBlank()) {
            loadPedigree()
        } else {
            _uiState.value = PedigreeUiState.Error("No product ID provided")
        }
    }

    private fun loadPedigree() {
        viewModelScope.launch {
            _uiState.value = PedigreeUiState.Loading

            try {
                val productRes = productRepository.getProductById(productId).first()
                val product = productRes.data

                if (product == null) {
                    _uiState.value = PedigreeUiState.Error("Product not found")
                    return@launch
                }

                // Build simple pedigree tree (single node for now)
                val rootNode = PedigreeNode(bird = product, parents = emptyList())

                _uiState.value = PedigreeUiState.Success(
                    rootBird = product,
                    pedigreeTree = rootNode
                )
            } catch (e: Exception) {
                _uiState.value = PedigreeUiState.Error(e.message ?: "Failed to load pedigree")
            }
        }
    }

    fun refresh() {
        loadPedigree()
    }
}

sealed class PedigreeUiState {
    object Loading : PedigreeUiState()
    data class Success(
        val rootBird: ProductEntity,
        val pedigreeTree: PedigreeNode
    ) : PedigreeUiState()
    data class Error(val message: String) : PedigreeUiState()
}

data class PedigreeNode(
    val bird: ProductEntity,
    val parents: List<PedigreeNode>
)
