package com.rio.rostry.core

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

open class BaseRepository(
    private val errorLogger: ErrorLogger? = null
) {
    suspend fun <T> safeCall(
        dispatcher: CoroutineDispatcher,
        block: suspend () -> T
    ): Resource<T> = try {
        val result = withContext(dispatcher) { block() }
        Resource.Success(result)
    } catch (t: Throwable) {
        errorLogger?.log(t)
        Resource.Error(t, t.message)
    }
}
