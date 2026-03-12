package com.rio.rostry.domain.error

/**
 * Strategy for recovering from errors automatically.
 */
interface RecoveryStrategy {
    suspend fun recover(error: Throwable, context: ErrorContext): Result<Unit>
}
