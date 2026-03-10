package com.rio.rostry.core.model

/**
 * Sealed class representing the result of an operation.
 * 
 * Phase 2: Domain and Data Decoupling
 * Used for consistent error handling across domain and data layers
 */
sealed class Result<out T> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val exception: Throwable) : Result<Nothing>()

    val isSuccess: Boolean
        get() = this is Success<*>

    val isError: Boolean
        get() = this is Error

    @Suppress("UNCHECKED_CAST")
    fun getOrNull(): T? = when (this) {
        is Success<*> -> data as T?
        is Error -> null
    }

    fun exceptionOrNull(): Throwable? = when (this) {
        is Success<*> -> null
        is Error -> exception
    }

    override fun toString(): String = when (this) {
        is Success<*> -> "Success[data=$data]"
        is Error -> "Error[exception=$exception]"
    }
}

/**
 * Extension function to run only on success
 */
inline fun <T, R> Result<T>.onSuccess(action: (T) -> R): Result<T> {
    if (this is Result.Success) {
        @Suppress("UNCHECKED_CAST")
        action(this.data as T)
    }
    return this
}

/**
 * Extension function to run only on error
 */
inline fun <T, R> Result<T>.onError(action: (Throwable) -> R): Result<T> {
    if (this is Result.Error) {
        action(this.exception)
    }
    return this
}

/**
 * Map the success value
 */
inline fun <T, R> Result<T>.map(transform: (T) -> R): Result<R> {
    return when (this) {
        is Result.Success -> {
            @Suppress("UNCHECKED_CAST")
            Result.Success(transform(this.data as T))
        }
        is Result.Error -> this
    }
}

/**
 * Get the data or throw exception on error
 */
@Suppress("UNCHECKED_CAST")
fun <T> Result<T>.getOrThrow(): T {
    return when (this) {
        is Result.Success -> this.data as T
        is Result.Error -> throw exception
    }
}
