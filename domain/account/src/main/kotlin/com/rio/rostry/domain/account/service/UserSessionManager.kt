package com.rio.rostry.domain.account.service

import com.rio.rostry.core.common.Result
import kotlinx.coroutines.flow.Flow

/** Domain interface for managing user sessions. */
interface UserSessionManager {
    fun getActiveSession(): Flow<Map<String, Any>?>
    suspend fun closeSession(sessionId: String): Result<Unit>
}
