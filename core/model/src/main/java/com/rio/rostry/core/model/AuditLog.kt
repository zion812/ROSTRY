package com.rio.rostry.core.model

/**
 * Domain model representing an audit log entry.
 * 
 * Used for tracking all system events, validations, and compliance actions.
 */
data class AuditLog(
    val logId: String,
    val type: String,
    val refId: String,
    val action: String,
    val actorUserId: String?,
    val detailsJson: String?,
    val createdAt: Long = System.currentTimeMillis()
) {
    companion object {
        /**
         * Factory method to create an audit log entry for validation failures.
         */
        fun createValidationFailureLog(
            refId: String,
            action: String,
            actorUserId: String?,
            reasons: List<String>
        ): AuditLog {
            val details = mapOf(
                "reasons" to reasons,
                "timestamp" to System.currentTimeMillis()
            )
            return AuditLog(
                logId = java.util.UUID.randomUUID().toString(),
                type = "VALIDATION_FAILURE",
                refId = refId,
                action = action,
                actorUserId = actorUserId,
                detailsJson = com.google.gson.Gson().toJson(details),
                createdAt = System.currentTimeMillis()
            )
        }
    }
}
