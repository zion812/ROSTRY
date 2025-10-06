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
            if (_showAnc.value) {
                when (val res = traceRepo.ancestors(id, d)) {
                    is Resource.Success -> _layersUp.value = res.data ?: emptyMap()
                    else -> _layersUp.value = emptyMap()
                }
            } else _layersUp.value = emptyMap()
            if (_showDesc.value) {
                when (val res = traceRepo.descendants(id, d)) {
                    is Resource.Success -> _layersDown.value = res.data ?: emptyMap()
                    else -> _layersDown.value = emptyMap()
                }
            } else _layersDown.value = emptyMap()
            // Build simple edges between adjacent layers (best-effort visualization)
            val e = mutableListOf<Pair<String, String>>()
            _layersUp.value.forEach { (_, nodes) -> nodes.forEach { e += (id to it) } }
            _layersDown.value.forEach { (_, nodes) -> nodes.forEach { e += (id to it) } }
            _edges.value = e
        }
    }
}
