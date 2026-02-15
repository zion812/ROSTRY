package com.rio.rostry.ui.enthusiast.lineage

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.database.dao.MedicalEventDao
import com.rio.rostry.data.database.dao.ProductDao
import com.rio.rostry.data.database.entity.MedicalEventEntity
import com.rio.rostry.data.database.entity.ProductEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HealthLogUiState(
    val bird: ProductEntity? = null,
    val events: List<MedicalEventEntity> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class HealthLogViewModel @Inject constructor(
    private val medicalEventDao: MedicalEventDao,
    private val productDao: ProductDao,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val productId: String = savedStateHandle.get<String>("productId") ?: ""
    
    private val _uiState = MutableStateFlow(HealthLogUiState())
    val uiState: StateFlow<HealthLogUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val bird = productDao.findById(productId)
            _uiState.update { it.copy(bird = bird) }
            medicalEventDao.observeByBird(productId).collect { events ->
                _uiState.update { it.copy(events = events, isLoading = false) }
            }
        }
    }
}
