package com.rio.rostry.ui.admin.commerce

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class InvoicesViewModel @Inject constructor() : ViewModel() {

    data class Invoice(
        val invoiceId: String,
        val orderId: String,
        val buyerName: String,
        val sellerName: String,
        val amount: Double,
        val currency: String = "₹",
        val status: InvoiceStatus,
        val paymentMethod: String,
        val createdAt: Date,
        val paidAt: Date? = null,
        val dueAt: Date? = null
    )

    enum class InvoiceStatus {
        PAID, PENDING, OVERDUE, REFUNDED, PARTIAL
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
            
            delay(500) // Simulate loading
            
            // Mock data - in production would come from OrderPaymentRepository
            val mockInvoices = listOf(
                Invoice(
                    invoiceId = "INV-001",
                    orderId = "ORD-2456",
                    buyerName = "Raju Kumar",
                    sellerName = "Sharma Farms",
                    amount = 15000.0,
                    status = InvoiceStatus.PAID,
                    paymentMethod = "UPI",
                    createdAt = Date(System.currentTimeMillis() - 86400000 * 2),
                    paidAt = Date(System.currentTimeMillis() - 86400000)
                ),
                Invoice(
                    invoiceId = "INV-002",
                    orderId = "ORD-2457",
                    buyerName = "Suresh Reddy",
                    sellerName = "Green Valley Poultry",
                    amount = 28500.0,
                    status = InvoiceStatus.PENDING,
                    paymentMethod = "Bank Transfer",
                    createdAt = Date(System.currentTimeMillis() - 86400000),
                    dueAt = Date(System.currentTimeMillis() + 86400000 * 3)
                ),
                Invoice(
                    invoiceId = "INV-003",
                    orderId = "ORD-2458",
                    buyerName = "Venkat Rao",
                    sellerName = "Sunrise Farms",
                    amount = 8750.0,
                    status = InvoiceStatus.OVERDUE,
                    paymentMethod = "COD",
                    createdAt = Date(System.currentTimeMillis() - 86400000 * 5),
                    dueAt = Date(System.currentTimeMillis() - 86400000 * 2)
                ),
                Invoice(
                    invoiceId = "INV-004",
                    orderId = "ORD-2459",
                    buyerName = "Anil Patil",
                    sellerName = "Sharma Farms",
                    amount = 12000.0,
                    status = InvoiceStatus.REFUNDED,
                    paymentMethod = "UPI",
                    createdAt = Date(System.currentTimeMillis() - 86400000 * 7),
                    paidAt = Date(System.currentTimeMillis() - 86400000 * 6)
                ),
                Invoice(
                    invoiceId = "INV-005",
                    orderId = "ORD-2460",
                    buyerName = "Mohan Das",
                    sellerName = "Premium Poultry",
                    amount = 45000.0,
                    status = InvoiceStatus.PARTIAL,
                    paymentMethod = "Split (Advance + COD)",
                    createdAt = Date(System.currentTimeMillis() - 86400000 * 3)
                )
            )
            
            val paidAmount = mockInvoices.filter { it.status == InvoiceStatus.PAID }.sumOf { it.amount }
            val pendingAmount = mockInvoices.filter { it.status == InvoiceStatus.PENDING || it.status == InvoiceStatus.OVERDUE }.sumOf { it.amount }
            
            _state.update { it.copy(
                isLoading = false,
                invoices = mockInvoices,
                filteredInvoices = mockInvoices,
                totalRevenue = paidAmount,
                pendingAmount = pendingAmount
            ) }
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
        loadInvoices()
    }
}
