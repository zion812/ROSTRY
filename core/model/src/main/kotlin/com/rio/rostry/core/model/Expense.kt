package com.rio.rostry.core.model

/**
 * Domain model for an expense record.
 */
data class Expense(
    val expenseId: String,
    val farmerId: String,
    val assetId: String?,
    val category: String,
    val amount: Double,
    val description: String?,
    val expenseDate: Long,
    val createdAt: Long,
    val updatedAt: Long
)

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
    val month: Int,
    val year: Int,
    val totalAmount: Double,
    val byCategory: Map<String, Double>
)
