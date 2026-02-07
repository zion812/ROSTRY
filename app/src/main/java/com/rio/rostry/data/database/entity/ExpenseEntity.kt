package com.rio.rostry.data.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entity for tracking farm expenses.
 * Categories: FEED, VACCINE, MEDICATION, LABOR, EQUIPMENT, UTILITIES, OTHER
 */
@Entity(
    tableName = "expenses",
    indices = [
        Index("farmerId"),
        Index("assetId"),
        Index("category"),
        Index("expenseDate")
    ]
)
data class ExpenseEntity(
    @PrimaryKey
    val expenseId: String,
    val farmerId: String,
    val assetId: String? = null, // Optional link to specific asset
    val category: String, // FEED, VACCINE, MEDICATION, LABOR, EQUIPMENT, UTILITIES, OTHER
    val amount: Double,
    val description: String? = null,
    val expenseDate: Long,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val dirty: Boolean = true,
    val syncedAt: Long? = null
)

/**
 * Expense categories for categorization and reporting.
 */
object ExpenseCategory {
    const val FEED = "FEED"
    const val VACCINE = "VACCINE"
    const val MEDICATION = "MEDICATION"
    const val LABOR = "LABOR"
    const val EQUIPMENT = "EQUIPMENT"
    const val UTILITIES = "UTILITIES"
    const val OTHER = "OTHER"
    
    val all = listOf(FEED, VACCINE, MEDICATION, LABOR, EQUIPMENT, UTILITIES, OTHER)
}
