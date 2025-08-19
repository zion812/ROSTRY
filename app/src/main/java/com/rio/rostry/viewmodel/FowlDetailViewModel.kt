package com.rio.rostry.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.models.Fowl
import com.rio.rostry.data.models.HealthRecord
import com.rio.rostry.navigation.fowlId
import com.rio.rostry.repository.FowlRepository
import com.rio.rostry.repository.DataException
import com.rio.rostry.repository.DataErrorType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

class FowlDetailViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val repo: FowlRepository = FowlRepository()
) : ViewModel() {
    
    private val _fowl = MutableStateFlow<Fowl?>(null)
    val fowl: StateFlow<Fowl?> = _fowl.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _error = MutableStateFlow<FowlError?>(null)
    val error: StateFlow<FowlError?> = _error.asStateFlow()

    init {
        loadFowl()
    }

    private fun loadFowl(retryCount: Int = 3) {
        val fowlId = savedStateHandle.fowlId()
        
        if (fowlId.isBlank()) {
            _error.value = FowlError(
                type = DataErrorType.UNKNOWN_ERROR,
                message = "Invalid fowl ID"
            )
            return
        }
        
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            
            val result = repo.getFowl(fowlId)
            if (result.isSuccess) {
                _fowl.value = result.getOrNull()
            } else {
                // Implement retry mechanism
                if (retryCount > 0) {
                    delay(1000) // Wait 1 second before retrying
                    loadFowl(retryCount - 1)
                } else {
                    handleError(result.exceptionOrNull())
                }
            }
            
            _loading.value = false
        }
    }

    fun refreshFowl() {
        loadFowl()
    }

    fun addHealthRecord(healthRecord: HealthRecord, retryCount: Int = 3) {
        val fowlId = savedStateHandle.fowlId()
        
        if (fowlId.isBlank()) {
            _error.value = FowlError(
                type = DataErrorType.UNKNOWN_ERROR,
                message = "Invalid fowl ID"
            )
            return
        }
        
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            
            val result = repo.addHealthRecord(fowlId, healthRecord)
            if (result.isSuccess) {
                loadFowl() // Refresh fowl data
            } else {
                // Implement retry mechanism
                if (retryCount > 0) {
                    delay(1000) // Wait 1 second before retrying
                    addHealthRecord(healthRecord, retryCount - 1)
                } else {
                    handleError(result.exceptionOrNull())
                }
            }
            
            _loading.value = false
        }
    }

    fun clearError() {
        _error.value = null
    }
    
    private fun handleError(throwable: Throwable?) {
        when (throwable) {
            is DataException -> {
                _error.value = FowlError(
                    type = throwable.errorType,
                    message = throwable.message ?: "Data operation failed"
                )
            }
            is IllegalArgumentException -> {
                _error.value = FowlError(
                    type = DataErrorType.UNKNOWN_ERROR,
                    message = throwable.message ?: "Invalid data provided"
                )
            }
            else -> {
                _error.value = FowlError(
                    type = DataErrorType.UNKNOWN_ERROR,
                    message = throwable?.message ?: "An unknown error occurred"
                )
            }
        }
    }
}