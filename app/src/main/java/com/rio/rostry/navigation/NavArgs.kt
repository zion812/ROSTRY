package com.rio.rostry.navigation

import androidx.lifecycle.SavedStateHandle

/**
 * Extension functions to retrieve navigation arguments from SavedStateHandle
 * These provide a type-safe way to access arguments in ViewModels
 */
object NavArgs {
    const val FOWL_ID = "fowlId"
}

// Extension functions for type-safe argument retrieval
fun SavedStateHandle.fowlId(): String = this.get<String>(NavArgs.FOWL_ID) ?: ""

// Add more extension functions as needed for other arguments