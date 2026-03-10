package com.rio.rostry.core.common.security

/**
 * Lightweight audit logging interface.
 * Implementations live in higher-level modules (e.g. app:SecurityManager).
 * Core/data modules can depend on this interface without pulling in Android Keystore
 * or other heavyweight security primitives.
 */
interface AuditLogger {
    fun audit(event: String, metadata: Map<String, Any?> = emptyMap())
}

/**
 * No-op implementation for modules that don't need audit logging but need
 * to satisfy the dependency.
 */
object NoOpAuditLogger : AuditLogger {
    override fun audit(event: String, metadata: Map<String, Any?>) {
        // No-op: auditing is optional in lower-level modules
    }
}
