package com.rio.rostry.ui.enthusiast.journal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.database.entity.DailyBirdLogEntity
import com.rio.rostry.data.database.entity.ProductEntity
import com.rio.rostry.data.repository.BirdHealthRepository
import com.rio.rostry.data.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PerformanceJournalViewModel @Inject constructor(
    private val birdHealthRepository: BirdHealthRepository,
    private val productRepository: ProductRepository
) : ViewModel() {

    private val _selectedBirdId = MutableStateFlow<String?>(null)
    val selectedBirdId = _selectedBirdId.asStateFlow()

    // Load user's birds for selection
    val myBirds = productRepository.getAllProducts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), com.rio.rostry.utils.Resource.Loading())

    val logs = _selectedBirdId.flatMapLatest { id ->
        if (id != null) {
            birdHealthRepository.getLogsForBird(id)
        } else {
            flowOf(emptyList())
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val selectedBird = combine(_selectedBirdId, myBirds) { id, resource ->
        if (resource is com.rio.rostry.utils.Resource.Success && id != null) {
            resource.data?.find { it.productId == id }
        } else {
            null
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun selectBird(id: String) {
        _selectedBirdId.value = id
    }

    fun addLog(entry: DailyBirdLogEntity) {
        viewModelScope.launch {
            birdHealthRepository.addLog(entry)
        }
    }

    fun deleteLog(id: Long) {
        viewModelScope.launch {
            birdHealthRepository.deleteLog(id)
        }
    }
}
