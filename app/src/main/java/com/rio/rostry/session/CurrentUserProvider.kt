package com.rio.rostry.session

import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject
import javax.inject.Singleton

interface CurrentUserProvider {
    fun userIdOrNull(): String?
    fun isAuthenticated(): Boolean
    
    // Impersonation
    fun impersonate(userId: String)
    fun clearImpersonation()
    fun isImpersonating(): Boolean
}

@Singleton
class FirebaseCurrentUserProvider @Inject constructor(
    private val auth: FirebaseAuth
) : CurrentUserProvider {
    
    private var impersonatedUserId: String? = null

    override fun userIdOrNull(): String? {
        return impersonatedUserId ?: auth.currentUser?.uid
    }
    
    override fun isAuthenticated(): Boolean = auth.currentUser != null

    override fun impersonate(userId: String) {
         impersonatedUserId = userId
    }

    override fun clearImpersonation() {
        impersonatedUserId = null
    }

    override fun isImpersonating(): Boolean = impersonatedUserId != null
}
