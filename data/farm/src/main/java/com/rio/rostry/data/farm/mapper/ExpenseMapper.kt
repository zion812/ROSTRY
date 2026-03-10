package com.rio.rostry.data.farm.mapper

import com.rio.rostry.core.model.Expense
import com.rio.rostry.data.database.entity.ExpenseEntity

/**
 * Converts ExpenseEntity to domain model.
 */
fun ExpenseEntity.toExpense(): Expense {
    return Expense(
        expenseId = this.expenseId,
        farmerId = this.farmerId,
        assetId = this.assetId,
        category = this.category,
        amount = this.amount,
        description = this.description,
        expenseDate = this.expenseDate,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
}

/**
 * Converts domain model to ExpenseEntity.
 */
fun Expense.toEntity(): ExpenseEntity {
    return ExpenseEntity(
        expenseId = this.expenseId,
        farmerId = this.farmerId,
        assetId = this.assetId,
        category = this.category,
        amount = this.amount,
        description = this.description,
        expenseDate = this.expenseDate,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
}
