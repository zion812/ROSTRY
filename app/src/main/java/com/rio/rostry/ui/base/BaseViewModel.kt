package com.rio.rostry.ui.base

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import timber.log.Timber

/**
 * Base ViewModel class to encapsulate common ViewModel logic.
 * It includes a CoroutineScope that is automatically cancelled when the ViewModel is cleared.
 */
abstract class BaseViewModel : ViewModel() {

    // ViewModel-specific CoroutineScope
    private val viewModelJob = SupervisorJob()
    protected val viewModelScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    // Example of a generic error state flow
    private val _errorState = MutableStateFlow<String?>(null)
    val errorState: StateFlow<String?> = _errorState

    protected fun handleError(errorMessage: String?) {
        Timber.e(errorMessage)
        _errorState.value = errorMessage
    }

    protected fun clearError() {
        _errorState.value = null
    }

    override fun onCleared() {
        super.onCleared()
        // Cancel all coroutines started by this ViewModel
        viewModelScope.cancel()
        Timber.d("BaseViewModel onCleared and coroutines cancelled")
    }
}
