package com.rio.rostry.ui.farmer.log

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.database.entity.FarmActivityLogEntity
import com.rio.rostry.data.repository.FarmActivityLogRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FarmActivityDetailViewModel @Inject constructor(
    private val repository: FarmActivityLogRepository
) : ViewModel() {

    private val _activityLog = MutableStateFlow<FarmActivityLogEntity?>(null)
    val activityLog = _activityLog.asStateFlow()

    fun loadActivity(id: String) {
        viewModelScope.launch {
            _activityLog.value = repository.getById(id)
        }
    }
    
    fun deleteActivity(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _activityLog.value?.let { log ->
                repository.deleteActivity(log.activityId)
                onSuccess()
            }
        }
    }
}
