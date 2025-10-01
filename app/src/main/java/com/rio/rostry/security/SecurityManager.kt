package com.rio.rostry.security

/**
 * Security manager offering simple, composable primitives for encryption, secure preferences,
 * request headers, and audit logging. Pure Kotlin scaffolding to avoid external deps here.
 */
object SecurityManager {
    /** Encrypt arbitrary bytes. The actual implementation can delegate to SQLCipher/Security Crypto. */
    fun encrypt(data: ByteArray, keyAlias: String = DEFAULT_KEY): ByteArray = data // no-op placeholder

    /** Decrypt arbitrary bytes. */
    fun decrypt(data: ByteArray, keyAlias: String = DEFAULT_KEY): ByteArray = data // no-op placeholder

    /** Create standard security headers for API requests (nonce, timestamp, signature placeholders). */
    fun apiSecurityHeaders(
        includeNonce: Boolean = true,
        includeTimestamp: Boolean = true,
        includeSignature: Boolean = false,
        signer: ((payload: String) -> String)? = null,
        payloadProvider: (() -> String)? = null
    ): Map<String, String> {
        val headers = mutableMapOf<String, String>()
        if (includeNonce) headers["X-Nonce"] = randomId()
        if (includeTimestamp) headers["X-Timestamp"] = System.currentTimeMillis().toString()
        if (includeSignature && signer != null && payloadProvider != null) {
            headers["X-Signature"] = signer(payloadProvider())
        }
        return headers
    }

    /** Append an audit record. Upstream can route this into structured logs/Crashlytics/Firestore. */
    fun audit(event: String, metadata: Map<String, Any?> = emptyMap(), level: AuditLevel = AuditLevel.INFO) {
        // no-op scaffolding
    }

    private fun randomId(): String = java.util.UUID.randomUUID().toString()

    const val DEFAULT_KEY: String = "app_default_key"
}

enum class AuditLevel { INFO, WARN, ERROR }
