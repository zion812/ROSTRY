package com.rio.rostry.utils

sealed class Resource<out T> {
    abstract val data: T?
    abstract val message: String?

    data class Success<T>(override val data: T) : Resource<T>() {
        override val message: String? = null
    }

    data class Error<T>(override val message: String, override val data: T? = null) : Resource<T>()

    data class Loading<T>(override val data: T? = null) : Resource<T>() {
        override val message: String? = null
    }
}
