package com.rio.rostry.monitoring

/**
 * Error reporting facade to enrich and forward errors to the configured sink (e.g., Crashlytics).
 */
object ErrorReporting {
    fun logNonFatal(throwable: Throwable, context: Map<String, String> = emptyMap()) {
        // no-op scaffolding; wire to Crashlytics.recordException with custom keys
    }

    fun setUserId(userId: String?) {
        // no-op scaffolding; wire to Crashlytics.setUserId
    }

    fun setCustomKey(key: String, value: String) {
        // no-op scaffolding; wire to Crashlytics.setCustomKey
    }
}
