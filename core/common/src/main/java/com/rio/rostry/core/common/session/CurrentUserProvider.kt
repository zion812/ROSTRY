package com.rio.rostry.core.common.session

/**
 * Interface providing common session operations like finding current user id and authentication state.
 */
interface CurrentUserProvider {
    fun userIdOrNull(): String?
    fun isAuthenticated(): Boolean
    fun impersonate(userId: String)
    fun clearImpersonation()
    fun isImpersonating(): Boolean
}
