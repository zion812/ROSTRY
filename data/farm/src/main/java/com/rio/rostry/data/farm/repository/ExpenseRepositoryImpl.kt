package com.rio.rostry.data.farm.repository

import com.rio.rostry.core.model.Result
import com.rio.rostry.core.common.session.CurrentUserProvider
import com.rio.rostry.core.model.Expense
import com.rio.rostry.core.model.ExpenseSummary
import com.rio.rostry.core.model.MonthlyExpenseSummary
import com.rio.rostry.data.database.dao.ExpenseDao
import com.rio.rostry.data.database.entity.ExpenseEntity
import com.rio.rostry.data.farm.mapper.toEntity
import com.rio.rostry.data.farm.mapper.toExpense
import com.rio.rostry.domain.farm.repository.ExpenseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import java.util.Calendar
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of expense management operations.
 */
@Singleton
class ExpenseRepositoryImpl @Inject constructor(
    private val expenseDao: ExpenseDao,
    private val currentUserProvider: CurrentUserProvider
) : ExpenseRepository {

    override fun observeExpenses(): Flow<List<Expense>> {
        val farmerId = currentUserProvider.userIdOrNull() ?: return flowOf(emptyList())
        return expenseDao.observeForFarmer(farmerId).map { entities ->
            entities.map { it.toExpense() }
        }
    }

    override fun observeExpensesByCategory(category: String): Flow<List<Expense>> {
        val farmerId = currentUserProvider.userIdOrNull() ?: return flowOf(emptyList())
        return expenseDao.observeForFarmerByCategory(farmerId, category).map { entities ->
            entities.map { it.toExpense() }
        }
    }

    override fun observeExpensesForAsset(assetId: String): Flow<List<Expense>> {
        return expenseDao.observeForAsset(assetId).map { entities ->
            entities.map { it.toExpense() }
        }
    }

    override fun observeExpensesForDateRange(startDate: Long, endDate: Long): Flow<List<Expense>> {
        val farmerId = currentUserProvider.userIdOrNull() ?: return flowOf(emptyList())
        return expenseDao.observeForDateRange(farmerId, startDate, endDate).map { entities ->
            entities.map { it.toExpense() }
        }
    }

    override suspend fun getTotalExpenses(): Result<Double> {
        return try {
            val farmerId = currentUserProvider.userIdOrNull() ?: return Result.Success(0.0)
            val total = expenseDao.getTotalForFarmer(farmerId)
            Result.Success(total)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun getTotalByCategory(category: String): Result<Double> {
        return try {
            val farmerId = currentUserProvider.userIdOrNull() ?: return Result.Success(0.0)
            val total = expenseDao.getTotalByCategory(farmerId, category)
            Result.Success(total)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun getExpenseSummaryByCategory(): Result<List<ExpenseSummary>> {
        return try {
            val farmerId = currentUserProvider.userIdOrNull() ?: return Result.Success(emptyList())
            val allExpenses = expenseDao.observeForFarmer(farmerId).first()
            
            val summary = allExpenses
                .groupBy { it.category }
                .map { (category, expenses) ->
                    ExpenseSummary(
                        category = category,
                        totalAmount = expenses.sumOf { it.amount },
                        count = expenses.size
                    )
                }
                .sortedByDescending { it.totalAmount }
            
            Result.Success(summary)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun getTotalForAsset(assetId: String): Result<Double> {
        return try {
            val total = expenseDao.getTotalForAsset(assetId)
            Result.Success(total)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun getMonthlyExpenseSummary(year: Int, month: Int): Result<MonthlyExpenseSummary> {
        return try {
            val farmerId = currentUserProvider.userIdOrNull() 
                ?: return Result.Success(MonthlyExpenseSummary(month, year, 0.0, emptyMap()))
            
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
            
            val summary = MonthlyExpenseSummary(
                month = month,
                year = year,
                totalAmount = expenses.sumOf { it.amount },
                byCategory = byCategory
            )
            
            Result.Success(summary)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun addExpense(
        category: String,
        amount: Double,
        description: String?,
        expenseDate: Long,
        assetId: String?
    ): Result<Expense> {
        return try {
            val farmerId = currentUserProvider.userIdOrNull() 
                ?: return Result.Error(Exception("User not authenticated"))
            
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
            Result.Success(expense.toExpense())
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun updateExpense(expense: Expense): Result<Unit> {
        return try {
            val entity = expense.toEntity().copy(
                updatedAt = System.currentTimeMillis(),
                dirty = true
            )
            expenseDao.update(entity)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun deleteExpense(expenseId: String): Result<Unit> {
        return try {
            expenseDao.deleteById(expenseId)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}

