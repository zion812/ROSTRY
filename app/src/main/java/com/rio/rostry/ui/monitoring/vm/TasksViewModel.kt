package com.rio.rostry.ui.monitoring.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.database.entity.TaskEntity
import com.rio.rostry.data.repository.monitoring.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class TaskTab { DUE, OVERDUE, COMPLETED }

@HiltViewModel
class TasksViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    private val firebaseAuth: com.google.firebase.auth.FirebaseAuth
) : ViewModel() {

    data class TasksUiState(
        val dueTasks: List<TaskEntity> = emptyList(),
        val overdueTasks: List<TaskEntity> = emptyList(),
        val completedTasks: List<TaskEntity> = emptyList(),
        val selectedTab: TaskTab = TaskTab.DUE,
        val isLoading: Boolean = true,
        val error: String? = null
    )

    private val selectedTab = MutableStateFlow(TaskTab.DUE)

    val uiState: StateFlow<TasksUiState> = selectedTab.flatMapLatest { tab ->
        val farmerId = firebaseAuth.currentUser?.uid
        val now = System.currentTimeMillis()
        if (farmerId == null) {
            MutableStateFlow(TasksUiState(isLoading = false))
        } else {
            combine(
                taskRepository.observeDue(farmerId, now),
                taskRepository.observeOverdue(farmerId, now),
                taskRepository.observeRecentCompleted(farmerId, 50)
            ) { due, overdue, completed ->
                TasksUiState(
                    dueTasks = due,
                    overdueTasks = overdue,
                    completedTasks = completed,
                    selectedTab = tab,
                    isLoading = false
                )
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TasksUiState())

    fun selectTab(tab: TaskTab) { selectedTab.value = tab }

    fun markComplete(taskId: String) {
        val uid = firebaseAuth.currentUser?.uid ?: return
        viewModelScope.launch { taskRepository.markComplete(taskId, uid) }
    }

    fun delete(taskId: String) {
        viewModelScope.launch { taskRepository.delete(taskId) }
    }
}
