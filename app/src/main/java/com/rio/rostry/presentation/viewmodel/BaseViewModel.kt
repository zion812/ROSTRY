package com.rio.rostry.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

abstract class BaseViewModel : ViewModel() {

    protected fun <T> executeWithLoading(
        stateFlow: MutableStateFlow<Resource<T>>,
        execute: suspend () -> T
    ) {
        viewModelScope.launch {
            stateFlow.value = Resource.Loading
            try {
                val result = execute()
                stateFlow.value = Resource.Success(result)
            } catch (e: Exception) {
                stateFlow.value = Resource.Error(e.message ?: "An error occurred", e)
            }
        }
    }

    protected fun <T> updateStateFlow(
        stateFlow: MutableStateFlow<Resource<T>>,
        resource: Resource<T>
    ) {
        stateFlow.value = resource
    }
}