package com.rio.rostry.ui.farmer.expense

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.database.entity.ExpenseCategory
import com.rio.rostry.data.database.entity.ExpenseEntity
import com.rio.rostry.data.repository.ExpenseRepository
import com.rio.rostry.data.repository.ExpenseSummary
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

data class ExpenseLedgerUiState(
    val expenses: List<ExpenseEntity> = emptyList(),
    val expenseSummary: List<ExpenseSummary> = emptyList(),
    val totalExpenses: Double = 0.0,
    val selectedCategory: String? = null,
    val selectedMonth: Int = Calendar.getInstance().get(Calendar.MONTH),
    val selectedYear: Int = Calendar.getInstance().get(Calendar.YEAR),
    val isLoading: Boolean = true,
    val showAddDialog: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null
)

@HiltViewModel
class ExpenseLedgerViewModel @Inject constructor(
    private val expenseRepository: ExpenseRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExpenseLedgerUiState())
    val uiState: StateFlow<ExpenseLedgerUiState> = _uiState.asStateFlow()

    init {
        loadExpenses()
    }

    private fun loadExpenses() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            try {
                // Get date range for selected month
                val calendar = Calendar.getInstance()
                calendar.set(_uiState.value.selectedYear, _uiState.value.selectedMonth, 1, 0, 0, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                val startDate = calendar.timeInMillis
                
                calendar.add(Calendar.MONTH, 1)
                val endDate = calendar.timeInMillis - 1
                
                // Observe expenses for the selected date range
                expenseRepository.observeExpensesForDateRange(startDate, endDate)
                    .collect { expenses ->
                        val filtered = if (_uiState.value.selectedCategory != null) {
                            expenses.filter { it.category == _uiState.value.selectedCategory }
                        } else {
                            expenses
                        }
                        
                        _uiState.update { state ->
                            state.copy(
                                expenses = filtered,
                                totalExpenses = filtered.sumOf { it.amount },
                                isLoading = false
                            )
                        }
                    }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false, 
                        error = e.message ?: "Failed to load expenses"
                    ) 
                }
            }
        }
        
        // Also load summary
        viewModelScope.launch {
            val summary = expenseRepository.getExpenseSummaryByCategory()
            _uiState.update { it.copy(expenseSummary = summary) }
        }
    }

    fun selectCategory(category: String?) {
        _uiState.update { it.copy(selectedCategory = category) }
        loadExpenses()
    }

    fun selectMonth(year: Int, month: Int) {
        _uiState.update { it.copy(selectedYear = year, selectedMonth = month) }
        loadExpenses()
    }

    fun showAddDialog() {
        _uiState.update { it.copy(showAddDialog = true) }
    }

    fun hideAddDialog() {
        _uiState.update { it.copy(showAddDialog = false) }
    }

    fun addExpense(
        category: String,
        amount: Double,
        description: String?,
        expenseDate: Long,
        assetId: String? = null
    ) {
        viewModelScope.launch {
            try {
                expenseRepository.addExpense(
                    category = category,
                    amount = amount,
                    description = description,
                    expenseDate = expenseDate,
                    assetId = assetId
                )
                _uiState.update { 
                    it.copy(
                        showAddDialog = false,
                        successMessage = "Expense added successfully"
                    )
                }
                loadExpenses()
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message ?: "Failed to add expense") }
            }
        }
    }

    fun deleteExpense(expenseId: String) {
        viewModelScope.launch {
            try {
                expenseRepository.deleteExpense(expenseId)
                _uiState.update { it.copy(successMessage = "Expense deleted") }
                loadExpenses()
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message ?: "Failed to delete expense") }
            }
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(error = null, successMessage = null) }
    }
}
