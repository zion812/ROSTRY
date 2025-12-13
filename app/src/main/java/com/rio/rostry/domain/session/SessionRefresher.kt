package com.rio.rostry.domain.session

/**
 * Abstraction for refreshing user profile/session state.
 * 
 * This interface allows ViewModels to trigger session refresh without
 * directly depending on SessionViewModel, maintaining proper Hilt scoping.
 */
interface SessionRefresher {
    /**
     * Refreshes the user's profile and session state.
     * Should be called after role upgrades or profile changes to ensure
     * UI reflects the latest user data.
     */
    suspend fun refreshUserProfile()
}
