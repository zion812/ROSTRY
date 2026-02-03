package com.rio.rostry.ui.admin.moderation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.repository.ContentType
import com.rio.rostry.data.repository.ModerationItem
import com.rio.rostry.data.repository.ModerationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ModerationViewModel @Inject constructor(
    private val repository: ModerationRepository
) : ViewModel() {

    private val _state = MutableStateFlow<State>(State.Loading)
    val state = _state.asStateFlow()

    sealed class State {
        object Loading : State()
        data class Content(val items: List<ModerationItem>) : State()
        data class Error(val message: String) : State()
    }

    init {
        loadQueue()
    }

    private fun loadQueue() {
        viewModelScope.launch {
            repository.getModerationQueue().collect {
                _state.value = State.Content(it)
            }
        }
    }

    fun approve(item: ModerationItem) {
        viewModelScope.launch {
            repository.approve(item.id, item.type)
        }
    }

    fun reject(item: ModerationItem, reason: String) {
        viewModelScope.launch {
            repository.reject(item.id, item.type, reason)
        }
    }
}
