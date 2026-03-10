package com.rio.rostry.domain.farm.repository

import com.rio.rostry.core.model.Result
import com.rio.rostry.core.model.Expense
import com.rio.rostry.core.model.ExpenseSummary
import com.rio.rostry.core.model.MonthlyExpenseSummary
import kotlinx.coroutines.flow.Flow

/**
 * Repository contract for expense management.
 */
interface ExpenseRepository {
    /**
     * Observe all expenses for the current farmer.
     */
    fun observeExpenses(): Flow<List<Expense>>

    /**
     * Observe expenses by category.
     */
    fun observeExpensesByCategory(category: String): Flow<List<Expense>>

    /**
     * Observe expenses for a specific asset.
     */
    fun observeExpensesForAsset(assetId: String): Flow<List<Expense>>

    /**
     * Observe expenses for a date range.
     */
    fun observeExpensesForDateRange(startDate: Long, endDate: Long): Flow<List<Expense>>

    /**
     * Get total expenses for current farmer.
     */
    suspend fun getTotalExpenses(): Result<Double>

    /**
     * Get total expenses by category.
     */
    suspend fun getTotalByCategory(category: String): Result<Double>

    /**
     * Get expense summary by category.
     */
    suspend fun getExpenseSummaryByCategory(): Result<List<ExpenseSummary>>

    /**
     * Get total for a specific asset.
     */
    suspend fun getTotalForAsset(assetId: String): Result<Double>

    /**
     * Get monthly expense summary.
     */
    suspend fun getMonthlyExpenseSummary(year: Int, month: Int): Result<MonthlyExpenseSummary>

    /**
     * Add a new expense.
     */
    suspend fun addExpense(
        category: String,
        amount: Double,
        description: String? = null,
        expenseDate: Long = System.currentTimeMillis(),
        assetId: String? = null
    ): Result<Expense>

    /**
     * Update an existing expense.
     */
    suspend fun updateExpense(expense: Expense): Result<Unit>

    /**
     * Delete an expense.
     */
    suspend fun deleteExpense(expenseId: String): Result<Unit>
}

