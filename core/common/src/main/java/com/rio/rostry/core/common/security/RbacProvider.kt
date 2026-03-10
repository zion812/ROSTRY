package com.rio.rostry.core.common.security

/**
 * Abstraction for role-based access control checks.
 * The actual implementation lives in the app module (RbacGuard).
 * Core/data modules depend only on this interface.
 */
interface RbacProvider {
    suspend fun canEditLineage(): Boolean
}
