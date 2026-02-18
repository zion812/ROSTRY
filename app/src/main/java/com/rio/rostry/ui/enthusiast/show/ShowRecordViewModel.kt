package com.rio.rostry.ui.enthusiast.show

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.database.entity.ShowRecordEntity
import com.rio.rostry.data.repository.ShowRecordRepository
import com.rio.rostry.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ShowRecordViewModel @Inject constructor(
    private val repository: ShowRecordRepository
) : ViewModel() {

    private val _selectedBirdId = MutableStateFlow<String?>(null)
    
    // UI State
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val records: StateFlow<List<ShowRecordEntity>> = _selectedBirdId
        .flatMapLatest { id ->
            if (id == null) flowOf(emptyList())
            else repository.getRecordsForProduct(id)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Deprecated manual flow
    // private val _records = MutableStateFlow<List<ShowRecordEntity>>(emptyList())
    // val showRecords = _records.asStateFlow()

    // Constants
    companion object {
        const val TYPE_SHOW = "SHOW"
        const val RESULT_WIN = "WIN"
    }

    fun selectBird(birdId: String) {
        _selectedBirdId.value = birdId
    }

    fun addRecord(
        birdId: String,
        ownerId: String,
        eventName: String,
        eventDate: Long,
        type: String = TYPE_SHOW,
        result: String = RESULT_WIN,
        notes: String? = null
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            val record = ShowRecordEntity(
                recordId = UUID.randomUUID().toString(),
                productId = birdId,
                ownerId = ownerId,
                recordType = type,
                eventName = eventName,
                eventDate = eventDate,
                result = result,
                notes = notes,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
            
            when (val result = repository.addRecord(record)) {
                is Resource.Success -> {
                    // Flow will auto-update
                    _error.value = null
                }
                is Resource.Error -> {
                    _error.value = result.message
                }
                else -> {}
            }
            _isLoading.value = false
        }
    }
    
    fun deleteRecord(recordId: String) {
         viewModelScope.launch {
            repository.deleteRecord(recordId)
         }
    }

    fun clearError() {
        _error.value = null
    }
}
