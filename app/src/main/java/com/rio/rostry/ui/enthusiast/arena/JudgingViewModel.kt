package com.rio.rostry.ui.enthusiast.arena

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.database.entity.ArenaParticipantEntity
import com.rio.rostry.data.database.entity.ProductEntity
import com.rio.rostry.domain.arena.JudgingService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class JudgingViewModel @Inject constructor(
    private val judgingService: JudgingService
) : ViewModel() {

    private val _uiState = MutableStateFlow(JudgingUiState())
    val uiState: StateFlow<JudgingUiState> = _uiState.asStateFlow()

    fun loadQueue(competitionId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            judgingService.getJudgingQueue(competitionId).collectLatest { queue ->
                _uiState.value = _uiState.value.copy(
                    queue = queue,
                    currentIndex = 0,
                    isLoading = false
                )
            }
        }
    }

    fun submitVote(score: Float) {
        val currentState = _uiState.value
        val currentPair = currentState.currentPair ?: return
        
        viewModelScope.launch {
            _uiState.value = currentState.copy(isSubmitting = true)
            
            // Submit mock vote
            judgingService.submitVote(currentPair.first.id, score)
            
            // Update UI to next bird
            if (currentState.currentIndex < currentState.queue.size - 1) {
                _uiState.value = currentState.copy(
                    currentIndex = currentState.currentIndex + 1,
                    isSubmitting = false,
                    voteScore = 0f // Reset score
                )
            } else {
                _uiState.value = currentState.copy(
                    isFinished = true,
                    isSubmitting = false
                )
            }
        }
    }
    
    fun updateScore(newScore: Float) {
        _uiState.value = _uiState.value.copy(voteScore = newScore)
    }
}

data class JudgingUiState(
    val queue: List<Pair<ArenaParticipantEntity, ProductEntity>> = emptyList(),
    val currentIndex: Int = 0,
    val voteScore: Float = 0f,
    val isLoading: Boolean = false,
    val isSubmitting: Boolean = false,
    val isFinished: Boolean = false
) {
    val currentPair: Pair<ArenaParticipantEntity, ProductEntity>?
        get() = if (queue.isNotEmpty() && currentIndex in queue.indices) queue[currentIndex] else null
        
    val progress: Float
        get() = if (queue.isEmpty()) 0f else (currentIndex + 1) / queue.size.toFloat()
}
