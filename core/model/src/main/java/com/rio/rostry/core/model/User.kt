package com.rio.rostry.core.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * User model representing an authenticated user.
 * 
 * Phase 2: Domain and Data Decoupling
 * Used across domain and data layers for user representation
 */
@Parcelize
data class User(
    val id: String,
    val email: String? = null,
    val phoneNumber: String? = null,
    val displayName: String? = null,
    val photoUrl: String? = null,
    val isEmailVerified: Boolean = false,
    val isPhoneVerified: Boolean = false,
    val createdAt: Long? = null,
    val updatedAt: Long? = null
) : Parcelable {
    companion object {
        val EMPTY = User(id = "")
    }
}
