package com.rio.rostry.data.base

import com.rio.rostry.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Response
import timber.log.Timber

/**
 * Base Repository class to encapsulate common data handling logic.
 * Provides helper functions for making network calls and emitting Resource states.
 */
abstract class BaseRepository {

    /**
     * Helper function to safely make a network call and emit results as a Flow of Resource.
     * @param apiCall Suspending lambda function that performs the actual network request and returns a Retrofit Response.
     */
    protected fun <T> safeApiCall(apiCall: suspend () -> Response<T>): Flow<Resource<T>> = flow {
        emit(Resource.Loading<T>())
        try {
            val response = apiCall()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    emit(Resource.Success<T>(body))
                } else {
                    emit(Resource.Error<T>("Response body is null"))
                }
            } else {
                val errorBody = response.errorBody()?.string() ?: "Unknown error"
                Timber.e("API call failed with error: ${response.code()} - $errorBody")
                emit(Resource.Error<T>("API call failed: ${response.message()} ($errorBody)"))
            }
        } catch (e: Exception) {
            Timber.e(e, "API call exception")
            emit(Resource.Error<T>("Network error: ${e.message}", null))
        }
    }.flowOn(Dispatchers.IO) // Execute the flow on the IO dispatcher

    /**
     * Simpler version for calls that don't use Retrofit's Response wrapper, e.g., Firebase calls.
     * @param call Suspending lambda function that performs the data operation and returns the data or throws an exception.
     */
    protected fun <T> safeCall(call: suspend () -> T): Flow<Resource<T>> = flow {
        emit(Resource.Loading<T>())
        try {
            val result = call()
            emit(Resource.Success<T>(result))
        } catch (e: Exception) {
            Timber.e(e, "Data operation exception")
            emit(Resource.Error<T>("Operation failed: ${e.message}", null))
        }
    }.flowOn(Dispatchers.IO)

    // You can add more common repository logic here, such as:
    // - Handling database operations with error wrapping
    // - Logic for combining network and local data sources for offline-first strategy
}