package com.rio.rostry.ui.enthusiast.showrecords

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.database.entity.ShowRecordEntity
import com.rio.rostry.data.repository.ShowRecordRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ShowRecordsViewModel @Inject constructor(
    private val showRecordRepository: ShowRecordRepository,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    data class UiState(
        val records: List<ShowRecordEntity> = emptyList(),
        val isLoading: Boolean = false,
        val message: String? = null
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()
    
    private var currentProductId: String? = null

    fun loadRecords(productId: String) {
        currentProductId = productId
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            showRecordRepository.getRecordsForProduct(productId).collect { records ->
                _uiState.update { it.copy(records = records, isLoading = false) }
            }
        }
    }

    fun addRecord(
        productId: String,
        eventName: String,
        recordType: String,
        result: String,
        eventDate: Long,
        placement: Int?,
        notes: String?
    ) {
        viewModelScope.launch {
            val ownerId = firebaseAuth.currentUser?.uid ?: return@launch
            
            val record = ShowRecordEntity(
                recordId = UUID.randomUUID().toString(),
                productId = productId,
                ownerId = ownerId,
                recordType = recordType,
                eventName = eventName,
                eventDate = eventDate,
                result = result,
                placement = placement,
                notes = notes,
                dirty = true
            )
            
            showRecordRepository.addRecord(record)
            _uiState.update { it.copy(message = "Record added successfully") }
        }
    }
    
    fun deleteRecord(recordId: String) {
        viewModelScope.launch {
            showRecordRepository.deleteRecord(recordId)
            _uiState.update { it.copy(message = "Record deleted") }
        }
    }
    
    fun clearMessage() {
        _uiState.update { it.copy(message = null) }
    }
}
