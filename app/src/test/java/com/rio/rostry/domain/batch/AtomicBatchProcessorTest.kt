package com.rio.rostry.domain.batch

import com.rio.rostry.domain.error.ErrorHandler
import com.rio.rostry.domain.validation.InputValidationError
import com.rio.rostry.domain.validation.InputValidationResult
import com.rio.rostry.domain.validation.InputValidator
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for AtomicBatchProcessor.
 * 
 * Tests Requirements: 17.1, 17.2, 17.3, 17.4, 17.5, 17.6, 17.7, 17.8
 */
class AtomicBatchProcessorTest {

    private lateinit var errorHandler: ErrorHandler
    private lateinit var batchProcessor: AtomicBatchProcessor

    @Before
    fun setup() {
        errorHandler = mockk(relaxed = true)
        batchProcessor = AtomicBatchProcessor(errorHandler)
    }

    @Test
    fun `executeBatch with empty list returns success with zero count`() = runTest {
        // Arrange
        val items = emptyList<String>()
        val operation: suspend (String) -> Unit = mockk()
        val transactionBlock: suspend (suspend () -> Unit) -> Unit = mockk()

        // Act
        val result = batchProcessor.executeBatch(
            items = items,
            operationName = "TestOperation",
            operation = operation,
            transactionBlock = transactionBlock
        )

        // Assert
        assertTrue(result is BatchResult.Success)
        assertEquals(0, (result as BatchResult.Success).processedCount)
    }

    @Test
    fun `executeBatch with items exceeding max size returns failure`() = runTest {
        // Arrange
        val items = (1..101).map { "item$it" }
        val operation: suspend (String) -> Unit = mockk()
        val transactionBlock: suspend (suspend () -> Unit) -> Unit = mockk()

        // Act
        val result = batchProcessor.executeBatch(
            items = items,
            operationName = "TestOperation",
            operation = operation,
            transactionBlock = transactionBlock
        )

        // Assert
        assertTrue(result is BatchResult.Failure)
        assertTrue((result as BatchResult.Failure).error.contains("exceeds maximum"))
    }

    @Test
    fun `executeBatch with validation failure returns ValidationFailed`() = runTest {
        // Arrange
        val items = listOf("valid", "invalid", "valid")
        val validator = object : InputValidator<String> {
            override fun validate(value: String): InputValidationResult {
                return if (value == "invalid") {
                    InputValidationResult.Invalid(
                        listOf(
                            InputValidationError(
                                field = "value",
                                message = "Value is invalid",
                                code = "INVALID"
                            )
                        )
                    )
                } else {
                    InputValidationResult.Valid
                }
            }
        }
        val operation: suspend (String) -> Unit = mockk()
        val transactionBlock: suspend (suspend () -> Unit) -> Unit = mockk()

        // Act
        val result = batchProcessor.executeBatch(
            items = items,
            operationName = "TestOperation",
            validator = validator,
            operation = operation,
            transactionBlock = transactionBlock
        )

        // Assert
        assertTrue(result is BatchResult.ValidationFailed)
        val validationResult = (result as BatchResult.ValidationFailed).validationResult
        assertFalse(validationResult.isAllValid)
        assertEquals(1, validationResult.invalid.size)
        assertTrue(validationResult.invalid.containsKey(1))
    }

    @Test
    fun `executeBatch with successful operations returns success`() = runTest {
        // Arrange
        val items = listOf("item1", "item2", "item3")
        val processedItems = mutableListOf<String>()
        val operation: suspend (String) -> Unit = { item ->
            processedItems.add(item)
        }
        val transactionBlock: suspend (suspend () -> Unit) -> Unit = { block ->
            block()
        }

        // Act
        val result = batchProcessor.executeBatch(
            items = items,
            operationName = "TestOperation",
            operation = operation,
            transactionBlock = transactionBlock
        )

        // Assert
        assertTrue(result is BatchResult.Success)
        assertEquals(3, (result as BatchResult.Success).processedCount)
        assertEquals(items, processedItems)
    }

    @Test
    fun `executeBatch with operation failure triggers rollback`() = runTest {
        // Arrange
        val items = listOf("item1", "item2", "item3")
        val processedItems = mutableListOf<String>()
        val operation: suspend (String) -> Unit = { item ->
            processedItems.add(item)
            if (item == "item2") {
                throw RuntimeException("Operation failed")
            }
        }
        val transactionBlock: suspend (suspend () -> Unit) -> Unit = { block ->
            try {
                block()
            } catch (e: Exception) {
                // Simulate rollback by clearing processed items
                processedItems.clear()
                throw e
            }
        }

        // Act
        val result = batchProcessor.executeBatch(
            items = items,
            operationName = "TestOperation",
            operation = operation,
            transactionBlock = transactionBlock
        )

        // Assert
        assertTrue(result is BatchResult.Failure)
        val failure = result as BatchResult.Failure
        assertEquals(1, failure.failedAtIndex)
        assertTrue(failure.error.contains("Failed at item 1"))
        
        // Verify rollback occurred (items cleared)
        assertTrue(processedItems.isEmpty())
        
        // Verify error handler was called
        coVerify { errorHandler.handle(any(), "TestOperation") }
    }

    @Test
    fun `executeBatch validates all items before transaction`() = runTest {
        // Arrange
        val items = listOf("item1", "item2", "item3")
        var validationCount = 0
        val validator = object : InputValidator<String> {
            override fun validate(value: String): InputValidationResult {
                validationCount++
                return InputValidationResult.Valid
            }
        }
        var transactionStarted = false
        val operation: suspend (String) -> Unit = mockk(relaxed = true)
        val transactionBlock: suspend (suspend () -> Unit) -> Unit = { block ->
            transactionStarted = true
            block()
        }

        // Act
        batchProcessor.executeBatch(
            items = items,
            operationName = "TestOperation",
            validator = validator,
            operation = operation,
            transactionBlock = transactionBlock
        )

        // Assert - all items validated before transaction
        assertEquals(3, validationCount)
        assertTrue(transactionStarted)
    }

    @Test
    fun `executeBatch with validation failure does not start transaction`() = runTest {
        // Arrange
        val items = listOf("valid", "invalid")
        val validator = object : InputValidator<String> {
            override fun validate(value: String): InputValidationResult {
                return if (value == "invalid") {
                    InputValidationResult.Invalid(
                        listOf(
                            InputValidationError("value", "Invalid", "INVALID")
                        )
                    )
                } else {
                    InputValidationResult.Valid
                }
            }
        }
        var transactionStarted = false
        val operation: suspend (String) -> Unit = mockk()
        val transactionBlock: suspend (suspend () -> Unit) -> Unit = {
            transactionStarted = true
            it()
        }

        // Act
        val result = batchProcessor.executeBatch(
            items = items,
            operationName = "TestOperation",
            validator = validator,
            operation = operation,
            transactionBlock = transactionBlock
        )

        // Assert
        assertTrue(result is BatchResult.ValidationFailed)
        assertFalse(transactionStarted)
    }

    @Test
    fun `executeBatch enforces 100 item limit`() = runTest {
        // Arrange - exactly 100 items should succeed
        val items100 = (1..100).map { "item$it" }
        val operation: suspend (String) -> Unit = mockk(relaxed = true)
        val transactionBlock: suspend (suspend () -> Unit) -> Unit = { it() }

        // Act
        val result100 = batchProcessor.executeBatch(
            items = items100,
            operationName = "TestOperation",
            operation = operation,
            transactionBlock = transactionBlock
        )

        // Assert
        assertTrue(result100 is BatchResult.Success)
        assertEquals(100, (result100 as BatchResult.Success).processedCount)

        // Arrange - 101 items should fail
        val items101 = (1..101).map { "item$it" }

        // Act
        val result101 = batchProcessor.executeBatch(
            items = items101,
            operationName = "TestOperation",
            operation = operation,
            transactionBlock = transactionBlock
        )

        // Assert
        assertTrue(result101 is BatchResult.Failure)
    }
}
