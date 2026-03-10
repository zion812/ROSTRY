package com.rio.rostry.ui.monitoring

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.database.dao.GrowthRecordDao
import com.rio.rostry.data.database.dao.ProductDao
import com.rio.rostry.data.database.entity.GrowthRecordEntity
import com.rio.rostry.data.database.entity.ProductEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.net.URLDecoder
import javax.inject.Inject

@HiltViewModel
class GrowthRecordDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val growthDao: GrowthRecordDao,
    private val productDao: ProductDao
) : ViewModel() {

    data class UiState(
        val isLoading: Boolean = true,
        val record: GrowthRecordEntity? = null,
        val product: ProductEntity? = null,
        val allRecords: List<GrowthRecordEntity> = emptyList(),
        val error: String? = null
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val recordId: String = savedStateHandle.get<String>("recordId")?.let {
        URLDecoder.decode(it, "UTF-8")
    } ?: ""

    init {
        loadGrowthDetails()
    }

    private fun loadGrowthDetails() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val record = growthDao.getById(recordId)
                if (record != null) {
                    val product = productDao.findById(record.productId)
                    // Load all records for this product to show comparison
                    val allRecords = growthDao.observeForProduct(record.productId).first()
                        .sortedBy { it.week }
                    
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        record = record,
                        product = product,
                        allRecords = allRecords
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Growth record not found"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to load growth details"
                )
            }
        }
    }

    fun updateHealthStatus(status: String) {
        viewModelScope.launch {
            val current = _uiState.value.record ?: return@launch
            val updated = current.copy(
                healthStatus = status,
                updatedAt = System.currentTimeMillis(),
                dirty = true
            )
            growthDao.upsert(updated)
            _uiState.value = _uiState.value.copy(record = updated)
        }
    }
}
