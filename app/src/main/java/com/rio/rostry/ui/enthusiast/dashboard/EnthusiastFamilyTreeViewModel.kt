package com.rio.rostry.ui.enthusiast.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import com.rio.rostry.data.repository.TraceabilityRepository
import com.rio.rostry.utils.Resource

@HiltViewModel
class EnthusiastFamilyTreeViewModel @Inject constructor(
    private val traceRepo: TraceabilityRepository
) : ViewModel() {

    private val _rootId = MutableStateFlow("")
    private val _showAnc = MutableStateFlow(true)
    private val _showDesc = MutableStateFlow(true)
    private val _depth = MutableStateFlow(3)

    val rootId: StateFlow<String> = _rootId.asStateFlow()
    val showAnc: StateFlow<Boolean> = _showAnc.asStateFlow()
    val showDesc: StateFlow<Boolean> = _showDesc.asStateFlow()
    val depth: StateFlow<Int> = _depth.asStateFlow()

    private val _layersUp = MutableStateFlow<Map<Int, List<String>>>(emptyMap())
    private val _layersDown = MutableStateFlow<Map<Int, List<String>>>(emptyMap())
    private val _edges = MutableStateFlow<List<Pair<String, String>>>(emptyList())

    val layersUp: StateFlow<Map<Int, List<String>>> = _layersUp.asStateFlow()
    val layersDown: StateFlow<Map<Int, List<String>>> = _layersDown.asStateFlow()
    val edges: StateFlow<List<Pair<String, String>>> = _edges.asStateFlow()

    val resetKey: StateFlow<Int> = combine(_depth, _showAnc, _showDesc) { d, _, _ -> d }
        .stateIn(viewModelScope, SharingStarted.Eagerly, 3)

    fun setRoot(id: String) {
        _rootId.value = id
        reload()
    }

    fun toggleAnc() { _showAnc.value = !_showAnc.value; reload() }
    fun toggleDesc() { _showDesc.value = !_showDesc.value; reload() }
    fun setDepth(d: Int) { _depth.value = d.coerceIn(1, 5); reload() }

    private fun reload() {
        val id = _rootId.value
        if (id.isBlank()) {
            _layersUp.value = emptyMap(); _layersDown.value = emptyMap(); _edges.value = emptyList(); return
        }
        viewModelScope.launch {
            val d = _depth.value
            val currentEdges = mutableSetOf<Pair<String, String>>()
            
            if (_showAnc.value) {
                when (val res = traceRepo.getAncestryGraph(id, d)) {
                    is Resource.Success -> {
                        val graph = res.data
                        if (graph != null) {
                            // Convert graph to layers for the simple layer-view
                            // Convert graph to layers and edges
                            _layersUp.value = rebuildLayersUp(id, graph.edges)
                            currentEdges.addAll(graph.edges)
                        } else {
                            _layersUp.value = emptyMap()
                        }
                    }
                    else -> _layersUp.value = emptyMap()
                }
            } else _layersUp.value = emptyMap()
            
            if (_showDesc.value) {
                when (val res = traceRepo.getDescendancyGraph(id, d)) {
                    is Resource.Success -> {
                        val graph = res.data
                        if (graph != null) {
                             val downLayers = rebuildLayersDown(id, graph.edges)
                             _layersDown.value = downLayers
                             currentEdges.addAll(graph.edges)
                        } else {
                            _layersDown.value = emptyMap()
                        }
                    }
                    else -> _layersDown.value = emptyMap()
                }
            } else _layersDown.value = emptyMap()
            
            _edges.value = currentEdges.toList()
        }
    }
    
    private fun rebuildLayersUp(root: String, edges: List<Pair<String, String>>): Map<Int, List<String>> {
        val parentMap = edges.groupBy { it.second }.mapValues { it.value.map { p -> p.first } }
        val layers = mutableMapOf<Int, MutableList<String>>()
        var curr = setOf(root)
        var depth = 1 // ancestors start at layer 1 (0 is root)
        while(curr.isNotEmpty() && depth <= 5) {
            val next = mutableSetOf<String>()
            curr.forEach { child ->
                parentMap[child]?.let { parents -> next.addAll(parents) }
            }
            if (next.isNotEmpty()) {
                layers[depth] = next.toMutableList()
            }
            curr = next
            depth++
        }
        return layers
    }

    private fun rebuildLayersDown(root: String, edges: List<Pair<String, String>>): Map<Int, List<String>> {
        val childMap = edges.groupBy { it.first }.mapValues { it.value.map { c -> c.second } }
        val layers = mutableMapOf<Int, MutableList<String>>()
        var curr = setOf(root)
        var depth = 1
        while(curr.isNotEmpty() && depth <= 5) {
            val next = mutableSetOf<String>()
            curr.forEach { parent ->
                childMap[parent]?.let { children -> next.addAll(children) }
            }
            if (next.isNotEmpty()) {
                layers[depth] = next.toMutableList()
            }
            curr = next
            depth++
        }
        return layers
    }
}
