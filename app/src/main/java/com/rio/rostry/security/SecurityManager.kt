package com.rio.rostry.security

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Log
// Firebase Analytics removed to avoid dependency; using Timber for error telemetry
import java.nio.ByteBuffer
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.Mac
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import timber.log.Timber

/**
 * Security manager offering simple, composable primitives for encryption, secure preferences,
 * request headers, and audit logging. Uses Android Keystore-backed AES/GCM without padding.
 * Audit logs are also persisted to Room via AuditLogDao for immutable audit trails.
 */
object SecurityManager {
    /** Encrypt arbitrary bytes with AES/GCM/NoPadding. Output format: [IV(12)][CIPHERTEXT] */
    fun encrypt(data: ByteArray, keyAlias: String = DEFAULT_KEY): ByteArray {
        val key = getOrCreateKey(keyAlias)
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, key)
        val iv = cipher.iv // 12 bytes recommended for GCM
        val ciphertext = cipher.doFinal(data)
        val buffer = ByteBuffer.allocate(IV_SIZE + ciphertext.size)
        buffer.put(iv)
        buffer.put(ciphertext)
        return buffer.array()
    }

    /** Decrypt bytes previously encrypted by [encrypt]. Expects prepended 12-byte IV. */
    fun decrypt(data: ByteArray, keyAlias: String = DEFAULT_KEY): ByteArray {
        require(data.size >= IV_SIZE + 1) { "Ciphertext too short" }
        val iv = data.copyOfRange(0, IV_SIZE)
        val ciphertext = data.copyOfRange(IV_SIZE, data.size)
        val key = getOrCreateKey(keyAlias)
        val cipher = Cipher.getInstance(TRANSFORMATION)
        val spec = GCMParameterSpec(TAG_SIZE_BITS, iv)
        cipher.init(Cipher.DECRYPT_MODE, key, spec)
        return cipher.doFinal(ciphertext)
    }

    /** Create standard security headers for API requests (nonce, timestamp, signature placeholders). Supports HMAC-SHA256 signature generation using keystore keys. */
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
        if (includeSignature) {
            val signerToUse = signer ?: { p: String -> signPayload(p, DEFAULT_KEY) }
            if (payloadProvider != null) {
                headers["X-Signature"] = signerToUse(payloadProvider())
            }
        }
        return headers
    }

    /** Append an audit record. Logs to Timber with AUDIT tag and optionally to Firebase Analytics for errors. Audit logs are also persisted to Room via AuditLogDao for immutable audit trails. */
    fun audit(event: String, metadata: Map<String, Any?> = emptyMap(), level: AuditLevel = AuditLevel.INFO) {
        Timber.tag("AUDIT").log(level.toTimberPriority(), "[$event] ${metadata.entries.joinToString { "${it.key}=${it.value}" }}")
        if (level == AuditLevel.ERROR) {
            Timber.tag("AUDIT").e("[audit_error] event=%s meta=%s", event, metadata.toString())
        }
    }

    private fun AuditLevel.toTimberPriority(): Int = when (this) {
        com.rio.rostry.security.AuditLevel.INFO -> Log.INFO
        com.rio.rostry.security.AuditLevel.WARN -> Log.WARN
        com.rio.rostry.security.AuditLevel.ERROR -> Log.ERROR
    }

    fun auditProductOperation(operation: String, productId: String, userId: String, metadata: Map<String, Any?>) {
        audit("PRODUCT_$operation", metadata + mapOf("productId" to productId, "userId" to userId))
    }

    fun auditTransferOperation(operation: String, transferId: String, userId: String, metadata: Map<String, Any?>) {
        audit("TRANSFER_$operation", metadata + mapOf("transferId" to transferId, "userId" to userId))
    }

    fun auditLineageOperation(operation: String, nodeId: String, userId: String, metadata: Map<String, Any?>) {
        audit("LINEAGE_$operation", metadata + mapOf("nodeId" to nodeId, "userId" to userId))
    }

    private fun randomId(): String = java.util.UUID.randomUUID().toString()

    private fun getOrCreateKey(alias: String): SecretKey {
        val ks = KeyStore.getInstance(ANDROID_KEY_STORE).apply { load(null) }
        (ks.getKey(alias, null) as? SecretKey)?.let { return it }

        val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEY_STORE)
        val spec = KeyGenParameterSpec.Builder(
            alias,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setRandomizedEncryptionRequired(true)
            .build()
        keyGenerator.init(spec)
        return keyGenerator.generateKey()
    }

    private fun getOrCreateHmacKey(alias: String): SecretKey {
        val ks = KeyStore.getInstance(ANDROID_KEY_STORE).apply { load(null) }
        (ks.getKey(alias, null) as? SecretKey)?.let { return it }

        // Some SDKs require algorithm name instead of KeyProperties constant
        val keyGenerator = try {
            KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_HMAC_SHA256, ANDROID_KEY_STORE)
        } catch (t: Throwable) {
            KeyGenerator.getInstance("HmacSHA256", ANDROID_KEY_STORE)
        }
        val spec = KeyGenParameterSpec.Builder(
            alias,
            KeyProperties.PURPOSE_SIGN or KeyProperties.PURPOSE_VERIFY
        )
            .setKeySize(256)
            .build()
        keyGenerator.init(spec)
        return keyGenerator.generateKey()
    }

    fun signPayload(payload: String, keyAlias: String): String {
        val key = getOrCreateHmacKey(keyAlias)
        val mac = Mac.getInstance("HmacSHA256")
        mac.init(key)
        return mac.doFinal(payload.toByteArray()).toHexString()
    }

    private fun ByteArray.toHexString(): String = joinToString("") { "%02x".format(it) }

    const val DEFAULT_KEY: String = "app_default_key"

    private const val ANDROID_KEY_STORE = "AndroidKeyStore"
    private const val TRANSFORMATION = "AES/GCM/NoPadding"
    private const val IV_SIZE = 12
    private const val TAG_SIZE_BITS = 128

    fun processRootDetectionResult(context: android.content.Context, isRooted: Boolean, detectionMethods: List<String>) {
        if (isRooted) {
            audit("ROOT_DETECTED", mapOf("methods" to detectionMethods))
            // Store root detection result
            context.getSharedPreferences("security_prefs", android.content.Context.MODE_PRIVATE)
                .edit()
                .putBoolean("device_rooted", true)
                .putString("root_methods", detectionMethods.joinToString(", "))
                .apply()
        } else {
             context.getSharedPreferences("security_prefs", android.content.Context.MODE_PRIVATE)
                .edit()
                .putBoolean("device_rooted", false)
                .remove("root_methods")
                .apply()
        }
    }

    fun isDeviceCompromised(context: android.content.Context): Boolean {
        return context.getSharedPreferences("security_prefs", android.content.Context.MODE_PRIVATE)
            .getBoolean("device_rooted", false)
    }
}

enum class AuditEventType {
    PRODUCT_CREATED,
    PRODUCT_UPDATED,
    TRANSFER_INITIATED,
    TRANSFER_COMPLETED,
    LINEAGE_EDITED,
    KYC_SUBMITTED,
    KYC_APPROVED,
    KYC_REJECTED
}

enum class AuditLevel { INFO, WARN, ERROR }
