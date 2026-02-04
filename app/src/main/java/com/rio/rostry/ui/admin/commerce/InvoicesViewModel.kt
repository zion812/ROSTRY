package com.rio.rostry.ui.admin.commerce

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class InvoicesViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    data class Invoice(
        val invoiceId: String,
        val orderId: String,
        val buyerName: String, // In a real app, fetch User by ID
        val sellerName: String, // In a real app, fetch User by ID
        val amount: Double,
        val currency: String = "₹",
        val status: InvoiceStatus,
        val paymentMethod: String,
        val createdAt: Date,
        val paidAt: Date? = null,
        val dueAt: Date? = null
    )

    enum class InvoiceStatus {
        PAID, PENDING, OVERDUE, REFUNDED, PARTIAL, UNKNOWN
    }

    data class UiState(
        val isLoading: Boolean = true,
        val invoices: List<Invoice> = emptyList(),
        val filteredInvoices: List<Invoice> = emptyList(),
        val filterStatus: InvoiceStatus? = null,
        val searchQuery: String = "",
        val totalRevenue: Double = 0.0,
        val pendingAmount: Double = 0.0,
        val error: String? = null
    )

    private val _state = MutableStateFlow(UiState())
    val state = _state.asStateFlow()

    init {
        loadInvoices()
    }

    private fun loadInvoices() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            
            transactionRepository.streamAllTransactions().collect { transactions ->
                val invoices = transactions.map { transaction ->
                    Invoice(
                        invoiceId = transaction.transactionId,
                        orderId = transaction.orderId,
                        buyerName = transaction.userId, // Placeholder: Use ID as name for now until user Repo lookup
                        sellerName = "System", // Placeholder
                        amount = transaction.amount,
                        currency = transaction.currency,
                        status = mapStatus(transaction.status),
                        paymentMethod = transaction.paymentMethod,
                        createdAt = Date(transaction.timestamp),
                        paidAt = if (transaction.status == "SUCCESS") Date(transaction.timestamp) else null
                    )
                }
                
                val paidAmount = invoices.filter { it.status == InvoiceStatus.PAID }.sumOf { it.amount }
                val pendingAmount = invoices.filter { it.status == InvoiceStatus.PENDING || it.status == InvoiceStatus.OVERDUE }.sumOf { it.amount }
                
                _state.update { it.copy(
                    isLoading = false,
                    invoices = invoices,
                    filteredInvoices = filterInvoices(invoices, it.filterStatus, it.searchQuery),
                    totalRevenue = paidAmount,
                    pendingAmount = pendingAmount
                ) }
            }
        }
    }

    private fun mapStatus(status: String): InvoiceStatus {
        return when (status.uppercase()) {
            "SUCCESS", "PAID" -> InvoiceStatus.PAID
            "PENDING" -> InvoiceStatus.PENDING
            "FAILED", "OVERDUE" -> InvoiceStatus.OVERDUE
            "REFUNDED" -> InvoiceStatus.REFUNDED
            else -> InvoiceStatus.UNKNOWN
        }
    }

    fun onSearchQueryChanged(query: String) {
        _state.update { state ->
            state.copy(
                searchQuery = query,
                filteredInvoices = filterInvoices(state.invoices, state.filterStatus, query)
            )
        }
    }

    fun onFilterChanged(status: InvoiceStatus?) {
        _state.update { state ->
            state.copy(
                filterStatus = status,
                filteredInvoices = filterInvoices(state.invoices, status, state.searchQuery)
            )
        }
    }

    private fun filterInvoices(invoices: List<Invoice>, status: InvoiceStatus?, query: String): List<Invoice> {
        return invoices.filter { invoice ->
            val matchesStatus = status == null || invoice.status == status
            val matchesQuery = query.isBlank() ||
                invoice.invoiceId.contains(query, ignoreCase = true) ||
                invoice.orderId.contains(query, ignoreCase = true) ||
                invoice.buyerName.contains(query, ignoreCase = true) ||
                invoice.sellerName.contains(query, ignoreCase = true)
            matchesStatus && matchesQuery
        }
    }

    fun refresh() {
        // Flow updates automatically
    }
}
