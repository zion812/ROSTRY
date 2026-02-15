package com.rio.rostry.ui.enthusiast.lineage

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.database.dao.ProductDao
import com.rio.rostry.data.database.entity.ProductEntity
import com.rio.rostry.domain.pedigree.PedigreeRepository
import com.rio.rostry.domain.pedigree.PedigreeTree
import com.rio.rostry.domain.service.BreedingValueResult
import com.rio.rostry.domain.service.BreedingValueService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LineageExplorerUiState(
    val rootBird: ProductEntity? = null,
    val ancestorTree: PedigreeTree? = null,
    val descendants: List<ProductEntity> = emptyList(),
    val selectedNode: PedigreeTree? = null,
    val selectedNodeBvi: BreedingValueResult? = null,
    val viewMode: LineageViewMode = LineageViewMode.ANCESTORS,
    val expandedNodes: Set<String> = emptySet(),
    val isLoading: Boolean = true,
    val error: String? = null
)

enum class LineageViewMode { ANCESTORS, DESCENDANTS }

@HiltViewModel
class LineageExplorerViewModel @Inject constructor(
    private val pedigreeRepository: PedigreeRepository,
    private val productDao: ProductDao,
    private val breedingValueService: BreedingValueService,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val productId: String = savedStateHandle.get<String>("productId") ?: ""

    private val _uiState = MutableStateFlow(LineageExplorerUiState())
    val uiState: StateFlow<LineageExplorerUiState> = _uiState.asStateFlow()

    init {
        loadLineage()
    }

    private fun loadLineage() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val bird = productDao.findById(productId)
                if (bird == null) {
                    _uiState.update { it.copy(isLoading = false, error = "Bird not found") }
                    return@launch
                }

                // Load ancestor tree (up to 5 generations)
                val pedigreeResult = pedigreeRepository.getFullPedigree(productId, 5)
                val tree = pedigreeResult.data

                // Load descendants
                val descendants = productDao.getOffspring(productId)

                _uiState.update {
                    it.copy(
                        rootBird = bird,
                        ancestorTree = tree,
                        descendants = descendants,
                        selectedNode = tree,
                        isLoading = false
                    )
                }

                // Load BVI for root bird in background
                launch {
                    try {
                        val bvi = breedingValueService.calculateBVI(productId)
                        _uiState.update { it.copy(selectedNodeBvi = bvi) }
                    } catch (_: Exception) {}
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun setViewMode(mode: LineageViewMode) {
        _uiState.update { it.copy(viewMode = mode) }
    }

    fun selectNode(node: PedigreeTree) {
        _uiState.update { it.copy(selectedNode = node, selectedNodeBvi = null) }
        // Load BVI for selected node
        viewModelScope.launch {
            try {
                val bvi = breedingValueService.calculateBVI(node.bird.id)
                _uiState.update {
                    if (it.selectedNode?.bird?.id == node.bird.id) {
                        it.copy(selectedNodeBvi = bvi)
                    } else it
                }
            } catch (_: Exception) {}
        }
    }

    fun toggleNodeExpansion(birdId: String) {
        _uiState.update {
            val newExpanded = it.expandedNodes.toMutableSet()
            if (birdId in newExpanded) newExpanded.remove(birdId) else newExpanded.add(birdId)
            it.copy(expandedNodes = newExpanded)
        }
    }

    /** Genetic contribution percentage for a node at a given generation depth */
    fun geneticContribution(generation: Int): Float {
        // Parents: 50%, Grandparents: 25%, Great-grandparents: 12.5%, etc.
        return (50f / (1 shl generation)).coerceAtLeast(0f)
    }
}
