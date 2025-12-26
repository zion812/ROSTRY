package com.rio.rostry.ui.profile

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.database.entity.StorageQuotaEntity
import com.rio.rostry.data.repository.StorageUsageRepository
import com.rio.rostry.data.repository.UserRepository
import com.rio.rostry.utils.Resource
import com.rio.rostry.workers.DataExportWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StorageQuotaViewModel @Inject constructor(
    private val storageUsageRepository: StorageUsageRepository,
    private val userRepository: UserRepository,
    private val application: Application
) : ViewModel() {

    private val _uiState = MutableStateFlow<StorageQuotaUiState>(StorageQuotaUiState.Loading)
    val uiState: StateFlow<StorageQuotaUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            val user = userRepository.getCurrentUserSuspend()
            if (user == null) {
                _uiState.value = StorageQuotaUiState.Error("User not found")
                return@launch
            }

            // Combine flow of quota data
            storageUsageRepository.observeQuota(user.userId)
                .onStart { _uiState.value = StorageQuotaUiState.Loading }
                .collect { quota ->
                    if (quota != null) {
                        _uiState.value = StorageQuotaUiState.Success(
                            quota = quota,
                            roleName = user.userType
                        )
                    } else {
                        // If no quota record, trigger a refresh
                        refreshUsage(user.userId)
                    }
                }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            val user = userRepository.getCurrentUserSuspend() ?: return@launch
            refreshUsage(user.userId)
        }
    }

    private suspend fun refreshUsage(userId: String) {
        _uiState.value = StorageQuotaUiState.Loading
        when (val result = storageUsageRepository.refreshUsage(userId)) {
            is Resource.Success -> {
                // Success will be emitted via the observeQuota flow
            }
            is Resource.Error -> {
                _uiState.value = StorageQuotaUiState.Error(result.message ?: "Failed to refresh storage usage")
            }
            else -> {}
        }
    }

    fun exportData() {
        DataExportWorker.enqueue(application)
    }
}

sealed class StorageQuotaUiState {
    object Loading : StorageQuotaUiState()
    data class Success(
        val quota: StorageQuotaEntity,
        val roleName: String
    ) : StorageQuotaUiState()
    data class Error(val message: String) : StorageQuotaUiState()
}
