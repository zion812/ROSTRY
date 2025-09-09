package com.rio.rostry.core

sealed class Resource<out T> {
    data class Success<T>(val data: T) : Resource<T>()
    data class Error(val throwable: Throwable, val message: String? = null) : Resource<Nothing>()
    data object Loading : Resource<Nothing>()
}
