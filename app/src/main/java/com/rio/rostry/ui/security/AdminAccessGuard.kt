package com.rio.rostry.ui.security

import com.rio.rostry.domain.model.UserType
import com.rio.rostry.session.CurrentUserProvider
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AdminAccessGuard @Inject constructor(
    private val currentUserProvider: CurrentUserProvider,
    private val userRepository: com.rio.rostry.data.repository.UserRepository
) {
    suspend fun hasAccess(): Boolean {
        val uid = currentUserProvider.userIdOrNull() ?: return false
        val user = userRepository.getCurrentUserSuspend()
        return user?.role == UserType.ADMIN
    }

    // Future: Granular permission checks based on sub-roles
    suspend fun hasPermission(permission: String): Boolean {
        if (!hasAccess()) return false
        // TODO: Implement granular RBAC here
        return true
    }
}
