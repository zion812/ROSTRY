package com.rio.rostry.data.repository

import com.rio.rostry.data.database.dao.ExpenseDao
import com.rio.rostry.data.database.entity.ExpenseCategory
import com.rio.rostry.data.database.entity.ExpenseEntity
import com.rio.rostry.session.CurrentUserProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Summary of expenses by category.
 */
data class ExpenseSummary(
    val category: String,
    val totalAmount: Double,
    val count: Int
)

/**
 * Monthly expense summary.
 */
data class MonthlyExpenseSummary(
    val month: Int, // 0-11
    val year: Int,
    val totalAmount: Double,
    val byCategory: Map<String, Double>
)

/**
 * Repository for managing expense records.
 */
@Singleton
class ExpenseRepository @Inject constructor(
    private val expenseDao: ExpenseDao,
    private val currentUserProvider: CurrentUserProvider
) {

    /**
     * Observe all expenses for the current farmer.
     */
    fun observeExpenses(): Flow<List<ExpenseEntity>> {
        val farmerId = currentUserProvider.userIdOrNull() ?: return kotlinx.coroutines.flow.flowOf(emptyList())
        return expenseDao.observeForFarmer(farmerId)
    }

    /**
     * Observe expenses by category.
     */
    fun observeExpensesByCategory(category: String): Flow<List<ExpenseEntity>> {
        val farmerId = currentUserProvider.userIdOrNull() ?: return kotlinx.coroutines.flow.flowOf(emptyList())
        return expenseDao.observeForFarmerByCategory(farmerId, category)
    }

    /**
     * Observe expenses for a specific asset.
     */
    fun observeExpensesForAsset(assetId: String): Flow<List<ExpenseEntity>> {
        return expenseDao.observeForAsset(assetId)
    }

    /**
     * Observe expenses for a date range.
     */
    fun observeExpensesForDateRange(startDate: Long, endDate: Long): Flow<List<ExpenseEntity>> {
        val farmerId = currentUserProvider.userIdOrNull() ?: return kotlinx.coroutines.flow.flowOf(emptyList())
        return expenseDao.observeForDateRange(farmerId, startDate, endDate)
    }

    /**
     * Get total expenses for current farmer.
     */
    suspend fun getTotalExpenses(): Double {
        val farmerId = currentUserProvider.userIdOrNull() ?: return 0.0
        return expenseDao.getTotalForFarmer(farmerId)
    }

    /**
     * Get total expenses by category.
     */
    suspend fun getTotalByCategory(category: String): Double {
        val farmerId = currentUserProvider.userIdOrNull() ?: return 0.0
        return expenseDao.getTotalByCategory(farmerId, category)
    }

    /**
     * Get expense summary by category.
     */
    suspend fun getExpenseSummaryByCategory(): List<ExpenseSummary> {
        val farmerId = currentUserProvider.userIdOrNull() ?: return emptyList()
        val allExpenses = expenseDao.observeForFarmer(farmerId).first()
        
        return allExpenses
            .groupBy { it.category }
            .map { (category, expenses) ->
                ExpenseSummary(
                    category = category,
                    totalAmount = expenses.sumOf { it.amount },
                    count = expenses.size
                )
            }
            .sortedByDescending { it.totalAmount }
    }

    /**
     * Get total for a specific asset.
     */
    suspend fun getTotalForAsset(assetId: String): Double {
        return expenseDao.getTotalForAsset(assetId)
    }

    /**
     * Get monthly expense summary.
     */
    suspend fun getMonthlyExpenseSummary(year: Int, month: Int): MonthlyExpenseSummary {
        val farmerId = currentUserProvider.userIdOrNull() ?: return MonthlyExpenseSummary(month, year, 0.0, emptyMap())
        
        val calendar = Calendar.getInstance()
        calendar.set(year, month, 1, 0, 0, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startDate = calendar.timeInMillis
        
        calendar.add(Calendar.MONTH, 1)
        val endDate = calendar.timeInMillis - 1
        
        val expenses = expenseDao.observeForDateRange(farmerId, startDate, endDate).first()
        
        val byCategory = expenses
            .groupBy { it.category }
            .mapValues { (_, list) -> list.sumOf { it.amount } }
        
        return MonthlyExpenseSummary(
            month = month,
            year = year,
            totalAmount = expenses.sumOf { it.amount },
            byCategory = byCategory
        )
    }

    /**
     * Add a new expense.
     */
    suspend fun addExpense(
        category: String,
        amount: Double,
        description: String? = null,
        expenseDate: Long = System.currentTimeMillis(),
        assetId: String? = null
    ): ExpenseEntity? {
        val farmerId = currentUserProvider.userIdOrNull() ?: return null
        
        val expense = ExpenseEntity(
            expenseId = UUID.randomUUID().toString(),
            farmerId = farmerId,
            assetId = assetId,
            category = category,
            amount = amount,
            description = description,
            expenseDate = expenseDate,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        
        expenseDao.insert(expense)
        return expense
    }

    /**
     * Update an existing expense.
     */
    suspend fun updateExpense(expense: ExpenseEntity) {
        expenseDao.update(expense.copy(updatedAt = System.currentTimeMillis(), dirty = true))
    }

    /**
     * Delete an expense.
     */
    suspend fun deleteExpense(expenseId: String) {
        expenseDao.deleteById(expenseId)
    }
}
