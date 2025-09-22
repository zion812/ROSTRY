package com.rio.rostry.ui.traceability

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.database.dao.BreedingRecordDao
import com.rio.rostry.data.repository.TraceabilityRepository
import com.rio.rostry.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class TraceabilityViewModel @Inject constructor(
    private val traceRepo: TraceabilityRepository,
    private val breedingDao: BreedingRecordDao
) : ViewModel() {

    data class UiState(
        val loading: Boolean = false,
        val rootId: String = "",
        val layersUp: Map<Int, List<String>> = emptyMap(),
        val layersDown: Map<Int, List<String>> = emptyMap(),
        val edges: List<Pair<String, String>> = emptyList(),
        val transferChain: List<Any> = emptyList(),
        val error: String? = null,
    )

    private val _state = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = _state.asStateFlow()

    fun load(productId: String, maxDepth: Int = 5) {
        _state.value = _state.value.copy(loading = true, rootId = productId, error = null)
        viewModelScope.launch {
            val ancRes = traceRepo.ancestors(productId, maxDepth)
            val descRes = traceRepo.descendants(productId, maxDepth)
            val chainRes = traceRepo.getTransferChain(productId)
            val anc = if (ancRes is Resource.Success) ancRes.data ?: emptyMap() else emptyMap()
            val desc = if (descRes is Resource.Success) descRes.data ?: emptyMap() else emptyMap()
            val chain = if (chainRes is Resource.Success) chainRes.data ?: emptyList() else emptyList()

            // Build edge list: for all nodes encountered, query breeding records by parent and map to child edges
            val nodes = buildSet {
                add(productId)
                anc.values.forEach { addAll(it) }
                desc.values.forEach { addAll(it) }
            }
            val edges = mutableListOf<Pair<String, String>>()
            for (n in nodes) {
                val recs = breedingDao.recordsByParent(n)
                recs.forEach { r -> edges += (r.parentId to r.childId); edges += (r.partnerId to r.childId) }
            }
            _state.value = _state.value.copy(loading = false, layersUp = anc, layersDown = desc, edges = edges, transferChain = chain)
        }
    }
}
