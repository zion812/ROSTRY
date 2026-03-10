package com.rio.rostry.ui.farmer

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.database.dao.FarmActivityLogDao
import com.rio.rostry.data.database.dao.ProductDao
import com.rio.rostry.data.database.entity.FarmActivityLogEntity
import com.rio.rostry.data.database.entity.ProductEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.net.URLDecoder
import javax.inject.Inject

@HiltViewModel
class FarmActivityDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val activityLogDao: FarmActivityLogDao,
    private val productDao: ProductDao
) : ViewModel() {

    data class UiState(
        val isLoading: Boolean = true,
        val activity: FarmActivityLogEntity? = null,
        val product: ProductEntity? = null,
        val error: String? = null
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val activityId: String = savedStateHandle.get<String>("activityId")?.let {
        URLDecoder.decode(it, "UTF-8")
    } ?: ""

    init {
        loadActivityDetails()
    }

    private fun loadActivityDetails() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val activity = activityLogDao.getById(activityId)
                if (activity != null) {
                    val product = activity.productId?.let { productDao.findById(it) }
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        activity = activity,
                        product = product
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Activity not found"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to load activity"
                )
            }
        }
    }

    fun updateNotes(notes: String) {
        viewModelScope.launch {
            val current = _uiState.value.activity ?: return@launch
            val updated = current.copy(
                notes = notes,
                updatedAt = System.currentTimeMillis(),
                dirty = true
            )
            activityLogDao.upsert(updated)
            _uiState.value = _uiState.value.copy(activity = updated)
        }
    }
}
