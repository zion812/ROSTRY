package com.rio.rostry.core.common.permissions

import com.rio.rostry.core.common.session.SessionManager
import com.rio.rostry.domain.model.UserType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Centralized permission checking service.
 * 
 * Provides methods to:
 * - Check if a permission is granted
 * - Check if a feature requires login
 * - Get all permissions for a user type
 * - Observe permission changes
 */
@Singleton
class PermissionChecker @Inject constructor(
    private val sessionManager: SessionManager
) {
    /**
     * Check if a specific permission is granted for the current session.
     * 
     * @param permission The permission to check
     * @return true if the permission is granted, false otherwise
     */
    fun hasPermission(permission: Permission): Flow<Boolean> {
        return combine(
            sessionManager.sessionRole(),
            sessionManager.isGuestSession()
        ) { userType, isGuest ->
            if (isGuest) {
                permission in GuestPermissions.allowed
            } else if (userType != null) {
                val permissions = PermissionDefaults.getDefaultPermissions(userType)
                permission in permissions
            } else {
                false
            }
        }
    }

    /**
     * Check if a feature requires authentication.
     * 
     * @param permission The permission that might require login
     * @return true if the feature requires login, false if accessible to guests
     */
    fun requiresLogin(permission: Permission): Boolean {
        return permission in GuestPermissions.requiresLogin
    }

    /**
     * Get all permissions for a user type.
     * 
     * @param userType The user type
     * @return Set of permissions granted to this user type
     */
    fun getPermissionsForUserType(userType: UserType): Set<Permission> {
        return PermissionDefaults.getDefaultPermissions(userType)
    }

    /**
     * Get permissions available to guests.
     * 
     * @return Set of permissions available without login
     */
    fun getGuestPermissions(): Set<Permission> {
        return GuestPermissions.allowed
    }

    /**
     * Check if the current session is a guest session.
     * 
     * @return Flow emitting true if in guest mode
     */
    fun isGuestSession(): Flow<Boolean> {
        return sessionManager.isGuestSession()
    }

    /**
     * Get the current user type.
     * 
     * @return Flow emitting the current user type or null
     */
    fun getCurrentUserType(): Flow<UserType?> {
        return sessionManager.sessionRole()
    }

    /**
     * Check if user can perform an action that requires write access.
     * 
     * @param permission The permission to check
     * @return true if the action is allowed
     */
    fun canWrite(permission: Permission): Flow<Boolean> {
        return hasPermission(permission)
    }

    /**
     * Check if user can perform admin/moderation actions.
     * 
     * @return true if user has admin or moderator role
     */
    fun isAdminOrModerator(): Flow<Boolean> {
        return sessionManager.sessionRole().map { userType ->
            userType in listOf(UserType.ADMIN, UserType.MODERATOR)
        }
    }

    /**
     * Check if user is a farmer.
     * 
     * @return true if user has farmer role
     */
    fun isFarmer(): Flow<Boolean> {
        return sessionManager.sessionRole().map { userType ->
            userType == UserType.FARMER
        }
    }

    /**
     * Check if user is an enthusiast.
     * 
     * @return true if user has enthusiast role
     */
    fun isEnthusiast(): Flow<Boolean> {
        return sessionManager.sessionRole().map { userType ->
            userType == UserType.ENTHUSIAST
        }
    }

    /**
     * Check if user is authenticated (not a guest).
     * 
     * @return true if user is authenticated
     */
    fun isAuthenticated(): Flow<Boolean> {
        return sessionManager.sessionRole().map { it != null }
    }
}

/**
 * Extension function to check permission with a single value.
 * Use this for one-time checks outside of Compose.
 */
suspend fun PermissionChecker.checkOnce(permission: Permission): Boolean {
    var result = false
    hasPermission(permission).collect { isGranted ->
        result = isGranted
        return@collect
    }
    return result
}