package com.rio.rostry.ui.monitoring

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.database.dao.VaccinationRecordDao
import com.rio.rostry.data.database.dao.ProductDao
import com.rio.rostry.data.database.entity.VaccinationRecordEntity
import com.rio.rostry.data.database.entity.ProductEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.net.URLDecoder
import javax.inject.Inject

@HiltViewModel
class VaccinationDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val vaccinationDao: VaccinationRecordDao,
    private val productDao: ProductDao
) : ViewModel() {

    data class UiState(
        val isLoading: Boolean = true,
        val vaccination: VaccinationRecordEntity? = null,
        val product: ProductEntity? = null,
        val error: String? = null
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val vaccinationId: String = savedStateHandle.get<String>("vaccinationId")?.let {
        URLDecoder.decode(it, "UTF-8")
    } ?: ""

    init {
        loadVaccinationDetails()
    }

    private fun loadVaccinationDetails() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val vaccination = vaccinationDao.getById(vaccinationId)
                if (vaccination != null) {
                    val product = productDao.findById(vaccination.productId)
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        vaccination = vaccination,
                        product = product
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Vaccination record not found"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to load vaccination details"
                )
            }
        }
    }

    fun markAsAdministered() {
        viewModelScope.launch {
            val current = _uiState.value.vaccination ?: return@launch
            val updated = current.copy(
                administeredAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis(),
                dirty = true
            )
            vaccinationDao.upsert(updated)
            _uiState.value = _uiState.value.copy(vaccination = updated)
        }
    }

    fun updateEfficacyNotes(notes: String) {
        viewModelScope.launch {
            val current = _uiState.value.vaccination ?: return@launch
            val updated = current.copy(
                efficacyNotes = notes,
                updatedAt = System.currentTimeMillis(),
                dirty = true
            )
            vaccinationDao.upsert(updated)
            _uiState.value = _uiState.value.copy(vaccination = updated)
        }
    }
}
