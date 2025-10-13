package com.rio.rostry.security

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.nio.ByteBuffer
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

/**
 * Security manager offering simple, composable primitives for encryption, secure preferences,
 * request headers, and audit logging. Uses Android Keystore-backed AES/GCM without padding.
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

    const val DEFAULT_KEY: String = "app_default_key"

    private const val ANDROID_KEY_STORE = "AndroidKeyStore"
    private const val TRANSFORMATION = "AES/GCM/NoPadding"
    private const val IV_SIZE = 12
    private const val TAG_SIZE_BITS = 128
}

enum class AuditLevel { INFO, WARN, ERROR }
