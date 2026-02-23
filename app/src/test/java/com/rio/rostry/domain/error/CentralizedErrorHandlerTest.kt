package com.rio.rostry.domain.error

import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.io.IOException

import com.rio.rostry.data.database.dao.ErrorLogDao
import com.rio.rostry.data.resilience.FallbackManager
import io.mockk.mockk
import io.mockk.every

class CentralizedErrorHandlerTest {

    private lateinit var errorHandler: CentralizedErrorHandler
    private val errorLogDao: ErrorLogDao = mockk(relaxed = true)
    private val fallbackManager: FallbackManager = mockk(relaxed = true)

    @Before
    fun setup() {
        every { fallbackManager.getFriendlyErrorMessage(any()) } answers {
            val err = firstArg<Throwable>()
            when (err) {
                is IOException -> "Network error. Please check your connection."
                is IllegalArgumentException -> err.message ?: "Invalid input"
                else -> "An unexpected error occurred. Please try again later."
            }
        }
        errorHandler = CentralizedErrorHandler(errorLogDao, fallbackManager)
    }

    @Test
    fun categorize_givenIOException_returnsRecoverable() {
        val exception = IOException("Network unreachable")
        val category = errorHandler.categorize(exception)
        assertEquals(ErrorCategory.RECOVERABLE, category)
    }

    @Test
    fun categorize_givenIllegalArgumentException_returnsUserActionable() {
        val exception = IllegalArgumentException("Invalid input format")
        val category = errorHandler.categorize(exception)
        assertEquals(ErrorCategory.USER_ACTIONABLE, category)
    }

    @Test
    fun categorize_givenOutOfMemoryError_returnsFatal() {
        val error = OutOfMemoryError("OOM")
        val category = errorHandler.categorize(error)
        assertEquals(ErrorCategory.FATAL, category)
    }

    @Test
    fun handle_givenIOException_shouldRetryTrue() = runTest {
        val exception = IOException("Network unreachable")
        val result = errorHandler.handle(exception, "TestOp", null)

        assertTrue(result.handled)
        assertTrue(result.shouldRetry)
        assertTrue(result.userMessage.contains("Network error", ignoreCase = true))
    }

    @Test
    fun handle_givenOutOfMemory_shouldRetryFalse() = runTest {
        val error = OutOfMemoryError("OOM")
        val result = errorHandler.handle(error, "TestOp", null)

        assertTrue(result.handled)
        assertFalse(result.shouldRetry)
        assertTrue(result.userMessage.contains("unexpected error", ignoreCase = true))
    }
}
