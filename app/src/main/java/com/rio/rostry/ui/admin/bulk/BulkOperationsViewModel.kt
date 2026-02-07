package com.rio.rostry.ui.admin.bulk

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BulkOperationsViewModel @Inject constructor() : ViewModel() {

    private val _runningOperations = MutableStateFlow<Set<String>>(emptySet())
    val runningOperations = _runningOperations.asStateFlow()

    private val _toastEvent = MutableSharedFlow<String>()
    val toastEvent = _toastEvent.asSharedFlow()

    fun runOperation(operationName: String) {
        if (_runningOperations.value.contains(operationName)) return

        viewModelScope.launch {
            _runningOperations.update { it + operationName }
            
            // Simulate operation
            try {
                delay(3000) // Simulate work
                _toastEvent.emit("$operationName completed successfully")
            } catch (e: Exception) {
                _toastEvent.emit("Failed to run $operationName")
            } finally {
                _runningOperations.update { it - operationName }
            }
        }
    }
}
