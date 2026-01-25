package com.rio.rostry.ui.admin.commerce

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.database.entity.InvoiceEntity
import com.rio.rostry.data.repository.InvoiceRepository
import com.rio.rostry.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminInvoiceViewModel @Inject constructor(
    private val invoiceRepository: InvoiceRepository
) : ViewModel() {

    private val _invoices = MutableStateFlow<List<InvoiceEntity>>(emptyList())
    val invoices: StateFlow<List<InvoiceEntity>> = _invoices.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        loadInvoices()
    }

    fun loadInvoices() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            when (val result = invoiceRepository.getAllInvoicesAdmin()) {
                is Resource.Success -> {
                    _invoices.value = result.data ?: emptyList()
                }
                is Resource.Error -> {
                    _error.value = result.message
                }
                is Resource.Loading -> {
                    // Handled by local state
                }
            }
            _isLoading.value = false
        }
    }
}
