package com.rio.rostry.ui.publicaccess

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.domain.repository.PublicBirdRepository
import com.rio.rostry.domain.repository.PublicBirdView
import com.rio.rostry.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PublicBirdLookupViewModel @Inject constructor(
    private val publicBirdRepository: PublicBirdRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<LookupUiState>(LookupUiState.Idle)
    val uiState = _uiState.asStateFlow()

    private val _query = MutableStateFlow("")
    val query = _query.asStateFlow()

    fun onQueryChanged(newQuery: String) {
        _query.value = newQuery
        if (newQuery.length < 3) {
            _uiState.value = LookupUiState.Idle
        }
    }

    fun lookup() {
        val code = _query.value.trim()
        if (code.isBlank()) return

        viewModelScope.launch {
            _uiState.value = LookupUiState.Loading
            when (val result = publicBirdRepository.lookupBird(code)) {
                is Resource.Success -> {
                    _uiState.value = LookupUiState.Success(result.data!!)
                }
                is Resource.Error -> {
                    _uiState.value = LookupUiState.Error(result.message ?: "Bird not found")
                }
                is Resource.Loading -> {
                     _uiState.value = LookupUiState.Loading
                }
            }
        }
    }
}

sealed class LookupUiState {
    object Idle : LookupUiState()
    object Loading : LookupUiState()
    data class Success(val data: PublicBirdView) : LookupUiState()
    data class Error(val message: String) : LookupUiState()
}
