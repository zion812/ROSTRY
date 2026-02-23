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
 * Validates all items before starting, limits batch size, and
 * supports progress tracking.
 */
@Singleton
class AtomicBatchProcessor @Inject constructor(
    private val errorHandler: ErrorHandler
) {

    companion object {
        const val MAX_BATCH_SIZE = 100
    }

    /**
     * Execute a batch operation atomically.
     *
     * @param items List of items to process.
     * @param validator Optional validator – all items are validated before processing begins.
     * @param operation Suspending function applied to each item within the transaction.
     * @param transactionBlock Wraps the entire batch in a database transaction.
     * @return [BatchResult] with success/failure details.
     */
    suspend fun <T> executeBatch(
        items: List<T>,
        validator: InputValidator<T>? = null,
        operation: suspend (T) -> Unit,
        transactionBlock: suspend (suspend () -> Unit) -> Unit
    ): BatchResult<T> {
        // 1. Check batch size
        if (items.isEmpty()) {
            return BatchResult.Success(processedCount = 0)
        }
        if (items.size > MAX_BATCH_SIZE) {
            return BatchResult.Failure(
                error = "Batch size ${items.size} exceeds maximum of $MAX_BATCH_SIZE",
                failedAtIndex = null
            )
        }

        // 2. Validate all items upfront (fail-fast)
        if (validator != null) {
            val validationResult = validateAll(items, validator)
            if (!validationResult.isAllValid) {
                val firstError = validationResult.invalid.entries.first()
                val errorMsg = firstError.value.joinToString("; ") { it.message }
                return BatchResult.ValidationFailed(
                    validationResult = validationResult,
                    message = "Item at index ${firstError.key} failed validation: $errorMsg"
                )
            }
        }

        // 3. Execute atomically inside transaction
        return try {
            var processedCount = 0
            transactionBlock {
                for ((index, item) in items.withIndex()) {
                    try {
                        operation(item)
                        processedCount++
                    } catch (e: Exception) {
                        // Transaction will be rolled back, propagate exception
                        Timber.e(e, "Batch operation failed at index $index")
                        throw BatchItemException(index, e)
                    }
                }
            }
            Timber.d("Batch operation completed successfully: $processedCount items")
            BatchResult.Success(processedCount = processedCount)
        } catch (e: BatchItemException) {
            errorHandler.handle(e.cause ?: e, "AtomicBatchProcessor")
            BatchResult.Failure(
                error = "Failed at item ${e.failedIndex}: ${e.cause?.message ?: "Unknown error"}",
                failedAtIndex = e.failedIndex
            )
        } catch (e: Exception) {
            errorHandler.handle(e, "AtomicBatchProcessor")
            BatchResult.Failure(
                error = "Transaction failed: ${e.message}",
                failedAtIndex = null
            )
        }
    }

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
