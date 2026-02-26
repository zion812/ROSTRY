package com.rio.rostry.domain.batch

import com.rio.rostry.domain.error.ErrorHandler
import com.rio.rostry.domain.validation.InputValidationResult
import com.rio.rostry.domain.validation.InputValidator
import com.rio.rostry.domain.validation.BatchValidationResult
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Generic atomic batch processor for database operations.
 * 
 * Guarantees atomicity: either all items succeed or all fail.
 * Validates all items before starting, limits batch size to 100 items,
 * and provides comprehensive logging for batch operations.
 * 
 * Requirements: 17.1, 17.2, 17.3, 17.4, 17.5, 17.6, 17.7, 17.8
 */
@Singleton
class AtomicBatchProcessor @Inject constructor(
    private val errorHandler: ErrorHandler
) {

    companion object {
        const val MAX_BATCH_SIZE = 100
    }

    /**
     * Execute a batch operation atomically within a database transaction.
     *
     * @param items List of items to process.
     * @param operationName Name of the operation for logging purposes.
     * @param validator Optional validator – all items are validated before processing begins.
     * @param operation Suspending function applied to each item within the transaction.
     * @param transactionBlock Wraps the entire batch in a database transaction.
     * @return [BatchResult] with success/failure details.
     * 
     * Requirements:
     * - 17.1: All operations execute within transactions
     * - 17.2: Any failure triggers rollback
     * - 17.3: Validates all items before transaction
     * - 17.4: Returns validation errors without modifying database
     * - 17.5: Logs start and completion with item counts
     * - 17.6: Logs rollback reason
     * - 17.7: Limits batch size to 100 items
     * - 17.8: Ensures atomicity (all or nothing)
     */
    suspend fun <T> executeBatch(
        items: List<T>,
        operationName: String = "BatchOperation",
        validator: InputValidator<T>? = null,
        operation: suspend (T) -> Unit,
        transactionBlock: suspend (suspend () -> Unit) -> Unit
    ): BatchResult<T> {
        // 1. Check batch size (Requirement 17.7)
        if (items.isEmpty()) {
            Timber.d("[$operationName] Batch operation skipped: empty batch")
            return BatchResult.Success(processedCount = 0)
        }
        
        if (items.size > MAX_BATCH_SIZE) {
            val errorMsg = "Batch size ${items.size} exceeds maximum of $MAX_BATCH_SIZE"
            Timber.w("[$operationName] $errorMsg")
            return BatchResult.Failure(
                error = errorMsg,
                failedAtIndex = null
            )
        }

        // Log batch start (Requirement 17.5)
        Timber.i("[$operationName] Starting batch operation with ${items.size} items")

        // 2. Validate all items upfront before transaction (Requirements 17.3, 17.4)
        if (validator != null) {
            val validationResult = validateAll(items, validator)
            if (!validationResult.isAllValid) {
                val firstError = validationResult.invalid.entries.first()
                val errorMsg = firstError.value.joinToString("; ") { it.message }
                val message = "Item at index ${firstError.key} failed validation: $errorMsg"
                
                // Log validation failure (Requirement 17.4)
                Timber.w("[$operationName] Batch validation failed: ${validationResult.invalid.size} invalid items. $message")
                
                return BatchResult.ValidationFailed(
                    validationResult = validationResult,
                    message = message
                )
            }
            Timber.d("[$operationName] All ${items.size} items passed validation")
        }

        // 3. Execute atomically inside transaction (Requirements 17.1, 17.2, 17.8)
        return try {
            var processedCount = 0
            transactionBlock {
                for ((index, item) in items.withIndex()) {
                    try {
                        operation(item)
                        processedCount++
                    } catch (e: Exception) {
                        // Transaction will be rolled back, propagate exception
                        Timber.e(e, "[$operationName] Batch operation failed at index $index")
                        throw BatchItemException(index, e)
                    }
                }
            }
            
            // Log successful completion (Requirement 17.5)
            Timber.i("[$operationName] Batch operation completed successfully: $processedCount items processed")
            BatchResult.Success(processedCount = processedCount)
            
        } catch (e: BatchItemException) {
            // Log rollback with reason (Requirement 17.6)
            val rollbackReason = "Failed at item ${e.failedIndex}: ${e.cause?.message ?: "Unknown error"}"
            Timber.e(e, "[$operationName] Batch operation rolled back. Reason: $rollbackReason")
            
            errorHandler.handle(e.cause ?: e, operationName)
            
            BatchResult.Failure(
                error = rollbackReason,
                failedAtIndex = e.failedIndex
            )
        } catch (e: Exception) {
            // Log rollback with reason (Requirement 17.6)
            val rollbackReason = "Transaction failed: ${e.message}"
            Timber.e(e, "[$operationName] Batch operation rolled back. Reason: $rollbackReason")
            
            errorHandler.handle(e, operationName)
            
            BatchResult.Failure(
                error = rollbackReason,
                failedAtIndex = null
            )
        }
    }

    /**
     * Validates all items in a batch before processing.
     * 
     * Requirement 17.3: Validate all items before beginning transaction
     */
    private fun <T> validateAll(items: List<T>, validator: InputValidator<T>): BatchValidationResult {
        val validIndices = mutableListOf<Int>()
        val invalidMap = mutableMapOf<Int, List<com.rio.rostry.domain.validation.InputValidationError>>()

        items.forEachIndexed { index, item ->
            val result = validator.validate(item)
            when (result) {
                is InputValidationResult.Valid -> validIndices.add(index)
                is InputValidationResult.Invalid -> invalidMap[index] = result.errors
            }
        }
        return BatchValidationResult(valid = validIndices, invalid = invalidMap)
    }
}

/**
 * Result of a batch operation.
 */
sealed class BatchResult<out T> {
    data class Success<T>(val processedCount: Int) : BatchResult<T>()
    data class Failure<T>(val error: String, val failedAtIndex: Int?) : BatchResult<T>()
    data class ValidationFailed<T>(
        val validationResult: BatchValidationResult,
        val message: String
    ) : BatchResult<T>()
}

/**
 * Exception thrown when a single item in a batch fails.
 * Used to propagate the failing index for debugging.
 */
class BatchItemException(
    val failedIndex: Int,
    cause: Throwable
) : Exception("Batch item at index $failedIndex failed", cause)
