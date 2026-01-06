package com.rio.rostry.ui.monitoring

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.database.dao.MortalityRecordDao
import com.rio.rostry.data.database.dao.ProductDao
import com.rio.rostry.data.database.entity.MortalityRecordEntity
import com.rio.rostry.data.database.entity.ProductEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.net.URLDecoder
import javax.inject.Inject

@HiltViewModel
class MortalityDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val mortalityDao: MortalityRecordDao,
    private val productDao: ProductDao
) : ViewModel() {

    data class UiState(
        val isLoading: Boolean = true,
        val record: MortalityRecordEntity? = null,
        val product: ProductEntity? = null,
        val error: String? = null
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val deathId: String = savedStateHandle.get<String>("deathId")?.let {
        URLDecoder.decode(it, "UTF-8")
    } ?: ""

    init {
        loadMortalityDetails()
    }

    private fun loadMortalityDetails() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val record = mortalityDao.getById(deathId)
                if (record != null) {
                    val product = record.productId?.let { productDao.findById(it) }
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        record = record,
                        product = product
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Mortality record not found"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to load details"
                )
            }
        }
    }

    fun updateCircumstances(circumstances: String) {
        viewModelScope.launch {
            val current = _uiState.value.record ?: return@launch
            val updated = current.copy(
                circumstances = circumstances,
                updatedAt = System.currentTimeMillis(),
                dirty = true
            )
            mortalityDao.upsert(updated)
            _uiState.value = _uiState.value.copy(record = updated)
        }
    }
}
