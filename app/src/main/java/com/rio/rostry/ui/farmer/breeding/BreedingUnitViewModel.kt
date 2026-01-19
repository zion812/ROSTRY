package com.rio.rostry.ui.farmer.breeding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.repository.EnthusiastBreedingRepository
import com.rio.rostry.session.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.rio.rostry.data.database.entity.BreedingPairEntity
import com.rio.rostry.utils.Resource

@HiltViewModel
class BreedingUnitViewModel @Inject constructor(
    private val repository: EnthusiastBreedingRepository,
    private val firebaseAuth: com.google.firebase.auth.FirebaseAuth
) : ViewModel() {

    private val _currentFarmerId = kotlinx.coroutines.flow.flow { firebaseAuth.currentUser?.uid?.let { emit(it) } }

    val breedingPairs: StateFlow<List<BreedingPairEntity>> = _currentFarmerId.flatMapLatest { id ->
        if (id == null) flowOf(emptyList())
        else repository.observeActivePairs(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun clearError() {
        _error.value = null
    }

    fun collectEggs(pairId: String, count: Int, grade: String, weight: Double?) {
        viewModelScope.launch {
            when (val result = repository.collectEggs(pairId, count, grade, weight)) {
                is Resource.Success -> {
                    // Success handled by UI (dialog close) or toast
                }
                is Resource.Error -> {
                    _error.value = result.message
                }
                else -> {}
            }
        }
    }
}
