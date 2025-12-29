package com.rio.rostry.ui.enthusiast.pedigree

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.database.entity.ProductEntity
import com.rio.rostry.data.repository.ProductRepository
import com.rio.rostry.domain.pedigree.PedigreeCompleteness
import com.rio.rostry.domain.pedigree.PedigreeRepository
import com.rio.rostry.domain.pedigree.PedigreeTree
import com.rio.rostry.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the Pedigree Screen.
 * 
 * Loads a recursive 3-generation pedigree tree for Enthusiast birds using PedigreeRepository.
 * Supports:
 * - Recursive ancestor traversal (sire/dam lines)
 * - Parent linking (assigning sire/dam to a bird)
 * - Pedigree completeness calculation
 * - Offspring tracking
 */
@HiltViewModel
class PedigreeViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val pedigreeRepository: PedigreeRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val productId: String = savedStateHandle["productId"] ?: ""

    private val _uiState = MutableStateFlow<PedigreeUiState>(PedigreeUiState.Loading)
    val uiState: StateFlow<PedigreeUiState> = _uiState.asStateFlow()
    
    // Parent selection state
    private val _parentSelectionState = MutableStateFlow<ParentSelectionState>(ParentSelectionState.Hidden)
    val parentSelectionState: StateFlow<ParentSelectionState> = _parentSelectionState.asStateFlow()

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
                // Get root bird details
                val productRes = productRepository.getProductById(productId).first()
                val product = productRes.data

                if (product == null) {
                    _uiState.value = PedigreeUiState.Error("Product not found")
                    return@launch
                }

                // Build recursive pedigree tree (3 generations)
                when (val treeResult = pedigreeRepository.getFullPedigree(productId, maxDepth = 3)) {
                    is Resource.Success -> {
                        val tree = treeResult.data!!
                        
                        // Get offspring count
                        val offspring = when (val offspringResult = pedigreeRepository.getOffspring(productId)) {
                            is Resource.Success -> offspringResult.data ?: emptyList()
                            else -> emptyList()
                        }
                        
                        _uiState.value = PedigreeUiState.Success(
                            rootBird = product,
                            pedigreeTree = tree,
                            completeness = tree.completeness,
                            ancestorCount = tree.countAncestors(),
                            offspringCount = offspring.size
                        )
                    }
                    is Resource.Error -> {
                        _uiState.value = PedigreeUiState.Error(treeResult.message ?: "Failed to load pedigree")
                    }
                    is Resource.Loading -> {
                        // Keep loading state
                    }
                }
            } catch (e: Exception) {
                _uiState.value = PedigreeUiState.Error(e.message ?: "Failed to load pedigree")
            }
        }
    }
    
    /**
     * Open parent selection dialog for linking sire or dam.
     */
    fun openParentSelection(parentGender: String) {
        viewModelScope.launch {
            val currentState = _uiState.value as? PedigreeUiState.Success ?: return@launch
            val ownerId = currentState.rootBird.sellerId
            
            _parentSelectionState.value = ParentSelectionState.Loading
            
            when (val result = pedigreeRepository.getPotentialParents(
                ownerId = ownerId,
                excludeId = productId,
                gender = parentGender
            )) {
                is Resource.Success -> {
                    _parentSelectionState.value = ParentSelectionState.Visible(
                        candidates = result.data ?: emptyList(),
                        targetGender = parentGender,
                        targetLabel = if (parentGender == "male") "Sire (Father)" else "Dam (Mother)"
                    )
                }
                is Resource.Error -> {
                    _parentSelectionState.value = ParentSelectionState.Error(result.message ?: "Failed to load candidates")
                }
                is Resource.Loading -> {
                    // Keep loading
                }
            }
        }
    }
    
    /**
     * Link a selected parent to the current bird.
     */
    fun linkParent(parentId: String) {
        viewModelScope.launch {
            val selectionState = _parentSelectionState.value as? ParentSelectionState.Visible ?: return@launch
            val isSire = selectionState.targetGender == "male"
            
            _parentSelectionState.value = ParentSelectionState.Loading
            
            val result = pedigreeRepository.linkParents(
                birdId = productId,
                sireId = if (isSire) parentId else null,
                damId = if (!isSire) parentId else null
            )
            
            when (result) {
                is Resource.Success -> {
                    _parentSelectionState.value = ParentSelectionState.Hidden
                    // Refresh the pedigree tree to show new parent
                    loadPedigree()
                }
                is Resource.Error -> {
                    _parentSelectionState.value = ParentSelectionState.Error(result.message ?: "Failed to link parent")
                }
                is Resource.Loading -> {
                    // Keep loading
                }
            }
        }
    }
    
    /**
     * Close parent selection dialog.
     */
    fun closeParentSelection() {
        _parentSelectionState.value = ParentSelectionState.Hidden
    }

    fun refresh() {
        loadPedigree()
    }
}

/**
 * UI State for the Pedigree Screen.
 */
sealed class PedigreeUiState {
    data object Loading : PedigreeUiState()
    data class Success(
        val rootBird: ProductEntity,
        val pedigreeTree: PedigreeTree,
        val completeness: PedigreeCompleteness,
        val ancestorCount: Int,
        val offspringCount: Int
    ) : PedigreeUiState()
    data class Error(val message: String) : PedigreeUiState()
}

/**
 * State for parent selection dialog.
 */
sealed class ParentSelectionState {
    data object Hidden : ParentSelectionState()
    data object Loading : ParentSelectionState()
    data class Visible(
        val candidates: List<ProductEntity>,
        val targetGender: String,
        val targetLabel: String
    ) : ParentSelectionState()
    data class Error(val message: String) : ParentSelectionState()
}
