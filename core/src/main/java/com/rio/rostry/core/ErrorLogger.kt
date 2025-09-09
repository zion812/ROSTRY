package com.rio.rostry.core

interface ErrorLogger {
    fun log(throwable: Throwable, message: String? = null)
    fun setUserId(id: String)
    fun setCustomKey(key: String, value: String)
}
