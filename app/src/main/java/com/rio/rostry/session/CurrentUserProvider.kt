package com.rio.rostry.session

interface CurrentUserProvider {
    fun userIdOrNull(): String?
    fun isAuthenticated(): Boolean
    fun impersonate(userId: String)
    fun clearImpersonation()
    fun isImpersonating(): Boolean
}
