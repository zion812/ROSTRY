package com.rio.rostry.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.models.Fowl
import com.rio.rostry.data.models.HealthRecord
import com.rio.rostry.repository.FowlRepository
import com.rio.rostry.repository.DataException
import com.rio.rostry.repository.DataErrorType
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

class FowlViewModel(private val repo: FowlRepository = FowlRepository()) : ViewModel() {
    private val _fowls = mutableStateOf<List<Fowl>>(emptyList())
    val fowls = _fowls

    private val _loading = mutableStateOf(false)
    val loading = _loading

    private val _error = mutableStateOf<FowlError?>(null)
    val error = _error

    private val _selectedFowl = mutableStateOf<Fowl?>(null)
    val selectedFowl = _selectedFowl

    fun fetchFowls(ownerUserId: String, retryCount: Int = 3) {
        // Don't fetch if user ID is empty
        if (ownerUserId.isBlank()) {
            _error.value = FowlError(
                type = DataErrorType.UNKNOWN_ERROR,
                message = "User not authenticated"
            )
            return
        }
        
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            
            val result = repo.getFowlsForUser(ownerUserId)
            if (result.isSuccess) {
                _fowls.value = result.getOrNull() ?: emptyList()
            } else {
                // Implement retry mechanism
                if (retryCount > 0) {
                    delay(1000) // Wait 1 second before retrying
                    fetchFowls(ownerUserId, retryCount - 1)
                } else {
                    handleError(result.exceptionOrNull())
                }
            }
            
            _loading.value = false
        }
    }

    fun addFowl(fowl: Fowl, retryCount: Int = 3) {
        // Don't add if user ID is empty
        if (fowl.ownerUserId.isBlank()) {
            _error.value = FowlError(
                type = DataErrorType.UNKNOWN_ERROR,
                message = "User not authenticated"
            )
            return
        }
        
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            
            val result = repo.addFowl(fowl)
            if (result.isSuccess) {
                fetchFowls(fowl.ownerUserId) // Refresh list
            } else {
                // Implement retry mechanism
                if (retryCount > 0) {
                    delay(1000) // Wait 1 second before retrying
                    addFowl(fowl, retryCount - 1)
                } else {
                    handleError(result.exceptionOrNull())
                }
            }
            
            _loading.value = false
        }
    }

    fun updateFowl(fowl: Fowl, retryCount: Int = 3) {
        // Don't update if user ID is empty
        if (fowl.ownerUserId.isBlank()) {
            _error.value = FowlError(
                type = DataErrorType.UNKNOWN_ERROR,
                message = "User not authenticated"
            )
            return
        }
        
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            
            val result = repo.updateFowl(fowl)
            if (result.isSuccess) {
                fetchFowls(fowl.ownerUserId) // Refresh list
            } else {
                // Implement retry mechanism
                if (retryCount > 0) {
                    delay(1000) // Wait 1 second before retrying
                    updateFowl(fowl, retryCount - 1)
                } else {
                    handleError(result.exceptionOrNull())
                }
            }
            
            _loading.value = false
        }
    }

    fun deleteFowl(fowlId: String, ownerUserId: String, retryCount: Int = 3) {
        // Don't delete if user ID is empty
        if (ownerUserId.isBlank()) {
            _error.value = FowlError(
                type = DataErrorType.UNKNOWN_ERROR,
                message = "User not authenticated"
            )
            return
        }
        
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
            
            val result = repo.deleteFowl(fowlId)
            if (result.isSuccess) {
                fetchFowls(ownerUserId) // Refresh list
            } else {
                // Implement retry mechanism
                if (retryCount > 0) {
                    delay(1000) // Wait 1 second before retrying
                    deleteFowl(fowlId, ownerUserId, retryCount - 1)
                } else {
                    handleError(result.exceptionOrNull())
                }
            }
            
            _loading.value = false
        }
    }

    fun selectFowl(fowl: Fowl?) {
        _selectedFowl.value = fowl
    }

    fun addHealthRecord(fowlId: String, healthRecord: HealthRecord, ownerUserId: String, retryCount: Int = 3) {
        // Don't add health record if user ID is empty
        if (ownerUserId.isBlank()) {
            _error.value = FowlError(
                type = DataErrorType.UNKNOWN_ERROR,
                message = "User not authenticated"
            )
            return
        }
        
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
                fetchFowls(ownerUserId) // Refresh list
            } else {
                // Implement retry mechanism
                if (retryCount > 0) {
                    delay(1000) // Wait 1 second before retrying
                    addHealthRecord(fowlId, healthRecord, ownerUserId, retryCount - 1)
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

// Fowl error data class
data class FowlError(
    val type: DataErrorType,
    val message: String
)