package com.rio.rostry.domain.batch

import com.rio.rostry.data.database.AppDatabase
import com.rio.rostry.domain.validation.ValidationFramework
import com.rio.rostry.domain.validation.InputValidator
import androidx.room.withTransaction
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Helper class for executing batch operations with validation and transactions.
 * 
 * Provides convenient methods for common batch operations with built-in
 * validation, transaction management, and logging.
 * 
 * Requirements: 17.1, 17.2, 17.3, 17.4, 17.5, 17.6, 17.7, 17.8
 */
@Singleton
class BatchOperationHelper @Inject constructor(
    private val atomicBatchProcessor: AtomicBatchProcessor,
    private val validationFramework: ValidationFramework,
    private val database: AppDatabase
) {

    /**
     * Executes a batch operation with automatic transaction management.
     * 
     * This is a convenience method that wraps the AtomicBatchProcessor
     * and provides automatic database transaction handling.
     * 
     * @param items List of items to process
     * @param operationName Name of the operation for logging
     * @param validator Optional validator for items
     * @param operation The operation to perform on each item
     * @return BatchResult indicating success or failure
     * 
     * Example usage:
     * ```
     * val result = batchOperationHelper.executeBatch(
     *     items = productIds,
     *     operationName = "UpdateProductStatus",
     *     validator = ProductIdValidator(),
     *     operation = { productId ->
     *         productDao.updateStatus(productId, "ACTIVE")
     *     }
     * )
     * ```
     */
    suspend fun <T> executeBatch(
        items: List<T>,
        operationName: String,
        validator: InputValidator<T>? = null,
        operation: suspend (T) -> Unit
    ): BatchResult<T> {
        return atomicBatchProcessor.executeBatch(
            items = items,
            operationName = operationName,
            validator = validator,
            operation = operation,
            transactionBlock = { block ->
                database.withTransaction {
                    block()
                }
            }
        )
    }

    /**
     * Executes a batch operation with foreign key validation.
     * 
     * Validates that all foreign key references exist before executing
     * the batch operation. This prevents referential integrity violations.
     * 
     * @param items List of items to process
     * @param operationName Name of the operation for logging
     * @param entityType Type of entity for foreign key validation
     * @param extractId Function to extract the foreign key ID from each item
     * @param operation The operation to perform on each item
     * @return BatchResult indicating success or failure
     * 
     * Example usage:
     * ```
     * val result = batchOperationHelper.executeBatchWithForeignKeyValidation(
     *     items = transfers,
     *     operationName = "CreateTransfers",
     *     entityType = "user",
     *     extractId = { it.recipientId },
     *     operation = { transfer ->
     *         transferDao.insert(transfer)
     *     }
     * )
     * ```
     */
    suspend fun <T> executeBatchWithForeignKeyValidation(
        items: List<T>,
        operationName: String,
        entityType: String,
        extractId: (T) -> String,
        operation: suspend (T) -> Unit
    ): BatchResult<T> {
        // First, validate all foreign keys
        val ids = items.map(extractId)
        val validationResult = validationFramework.validateForeignKeysBatch(entityType, ids)
        
        if (!validationResult.isAllValid) {
            val firstError = validationResult.invalid.entries.first()
            val errorMsg = "Foreign key validation failed for $entityType at index ${firstError.key}"
            Timber.w("[$operationName] $errorMsg")
            
            return BatchResult.ValidationFailed(
                validationResult = validationResult,
                message = errorMsg
            )
        }
        
        // If validation passes, execute the batch operation
        return executeBatch(
            items = items,
            operationName = operationName,
            validator = null, // Already validated
            operation = operation
        )
    }

    /**
     * Executes a batch insert operation.
     * 
     * Convenience method for batch inserts with automatic transaction management.
     * 
     * @param items List of items to insert
     * @param operationName Name of the operation for logging
     * @param validator Optional validator for items
     * @param insertOperation The insert operation to perform
     * @return BatchResult indicating success or failure
     */
    suspend fun <T> executeBatchInsert(
        items: List<T>,
        operationName: String,
        validator: InputValidator<T>? = null,
        insertOperation: suspend (T) -> Unit
    ): BatchResult<T> {
        return executeBatch(
            items = items,
            operationName = operationName,
            validator = validator,
            operation = insertOperation
        )
    }

    /**
     * Executes a batch update operation.
     * 
     * Convenience method for batch updates with automatic transaction management.
     * 
     * @param items List of items to update
     * @param operationName Name of the operation for logging
     * @param validator Optional validator for items
     * @param updateOperation The update operation to perform
     * @return BatchResult indicating success or failure
     */
    suspend fun <T> executeBatchUpdate(
        items: List<T>,
        operationName: String,
        validator: InputValidator<T>? = null,
        updateOperation: suspend (T) -> Unit
    ): BatchResult<T> {
        return executeBatch(
            items = items,
            operationName = operationName,
            validator = validator,
            operation = updateOperation
        )
    }

    /**
     * Executes a batch delete operation.
     * 
     * Convenience method for batch deletes with automatic transaction management.
     * 
     * @param items List of items to delete
     * @param operationName Name of the operation for logging
     * @param validator Optional validator for items
     * @param deleteOperation The delete operation to perform
     * @return BatchResult indicating success or failure
     */
    suspend fun <T> executeBatchDelete(
        items: List<T>,
        operationName: String,
        validator: InputValidator<T>? = null,
        deleteOperation: suspend (T) -> Unit
    ): BatchResult<T> {
        return executeBatch(
            items = items,
            operationName = operationName,
            validator = validator,
            operation = deleteOperation
        )
    }
}
