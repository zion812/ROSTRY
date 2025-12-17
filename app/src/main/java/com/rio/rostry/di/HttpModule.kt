package com.rio.rostry.di

import android.content.Context
import android.net.TrafficStats
import com.rio.rostry.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Cache
import okhttp3.CertificatePinner
import okhttp3.EventListener
import okhttp3.OkHttpClient
import okhttp3.Call
import okhttp3.Connection
import timber.log.Timber
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object HttpModule {

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class DefaultOkHttpClient

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class StorageOkHttpClient

    /**
     * Certificate pinning configuration for critical endpoints.
     * Pins SHA-256 hashes of public keys (not leaf certificates) for easier rotation.
     * 
     * Production pins for:
     * - Firebase Firestore
     * - Firebase Storage
     * - Firebase Functions
     * 
     * Note: Backup pins are included to prevent app breakage during certificate rotation.
     * Update pins when certificates are rotated (monitor expiry dates).
     */
    private fun createCertificatePinner(): CertificatePinner {
        return if (BuildConfig.DEBUG) {
            // Disable pinning in debug builds for development flexibility
            Timber.d("Certificate pinning: DISABLED (debug build)")
            CertificatePinner.DEFAULT
        } else {
            // Production certificate pinning
            Timber.i("Certificate pinning: ENABLED (release build)")
            CertificatePinner.Builder()
                // Firebase Firestore
                .add("firestore.googleapis.com",
                    "sha256/FEzVOUp4dF3gI0ZVPRJhFbSJVXR+uQmMH65xhs1glH4=", // Primary Google pin
                    "sha256/iie1VXtL7HzAMF+/PVPR9xzT80kQxdZeJ+zduCB3uj0=", // Backup Google pin
                    "sha256/pL3oJYoiqYu8BaXh2X/Z0tPQBLqNXaHHNWlmYPpbOdQ=") // Backup GeoTrust pin
                // Firebase Storage uses Google CDN which serves from multiple domains
                // (e.g. upload.video.google.com), so we cannot reliably pin certificates.
                // Firebase Functions (replace with your actual functions domain)
                // Uncomment and update when Cloud Functions are deployed:
                // .add("YOUR_PROJECT_ID.cloudfunctions.net",
                //     "sha256/YOUR_PIN_HERE=",
                //     "sha256/YOUR_BACKUP_PIN_HERE=")
                .build()
        }
    }

    @Provides
    @Singleton
    fun provideOkHttpCache(@ApplicationContext context: Context): Cache {
        // Allow disk reads for cache initialization to prevent StrictMode violations
        val oldPolicy = android.os.StrictMode.getThreadPolicy()
        android.os.StrictMode.setThreadPolicy(
            android.os.StrictMode.ThreadPolicy.Builder(oldPolicy)
                .permitDiskReads()
                .build()
        )
        try {
            // 10 MB HTTP cache for API/Coil
            val cacheDir = File(context.cacheDir, "http_cache")
            return Cache(cacheDir, 10L * 1024 * 1024)
        } finally {
            android.os.StrictMode.setThreadPolicy(oldPolicy)
        }
    }

    @Provides
    @Singleton
    @DefaultOkHttpClient
    fun provideOkHttpClient(
        cache: Cache,
        offline: com.rio.rostry.utils.network.OfflineCacheInterceptor,
        online: com.rio.rostry.utils.network.OnlineCacheInterceptor,
        retry: com.rio.rostry.utils.network.RetryInterceptor,
        usage: com.rio.rostry.utils.network.DataUsageInterceptor,
    ): OkHttpClient {
        val certificatePinner = createCertificatePinner()

        return OkHttpClient.Builder()
            .cache(cache)
            .certificatePinner(certificatePinner)
            .addInterceptor(usage)
            .addInterceptor(offline)
            .addNetworkInterceptor(online)
            .addInterceptor(retry)
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .eventListener(object : EventListener() {
                override fun connectionAcquired(call: Call, connection: Connection) {
                    // Tag network socket for monitoring and debugging
                    try {
                        TrafficStats.setThreadStatsTag(THREAD_STATS_TAG_HTTP)
                        Timber.d("Socket tagged for HTTP traffic: $THREAD_STATS_TAG_HTTP")
                    } catch (e: Exception) {
                        Timber.w(e, "Failed to tag socket for traffic stats")
                    }
                }

                override fun connectionReleased(call: Call, connection: Connection) {
                    // Clear the tag after connection is released
                    try {
                        TrafficStats.clearThreadStatsTag()
                        Timber.d("Socket tag cleared for HTTP traffic")
                    } catch (e: Exception) {
                        Timber.w(e, "Failed to clear socket tag")
                    }
                }

                override fun callFailed(call: Call, ioe: java.io.IOException) {
                    if (ioe is javax.net.ssl.SSLPeerUnverifiedException) {
                        Timber.e(ioe, "Certificate pinning failure detected")
                    } else if (ioe is javax.net.ssl.SSLProtocolException) {
                        Timber.e(ioe, "SSL Protocol Exception detected - possible certificate decoding issue")
                    }
                    // Clear the tag also in case of failure
                    try {
                        TrafficStats.clearThreadStatsTag()
                    } catch (e: Exception) {
                        Timber.w(e, "Failed to clear socket tag on failure")
                    }
                }
            })
            .build()
    }

    private const val THREAD_STATS_TAG_HTTP = 0x1001 // Unique tag for HTTP traffic

    /**
     * Provides a separate OkHttpClient instance for Firebase Storage operations
     * This can be used to bypass certificate pinning issues with firebasestorage.googleapis.com
     */
    @Provides
    @Singleton
    @StorageOkHttpClient
    fun provideStorageOkHttpClient(
        cache: Cache,
        offline: com.rio.rostry.utils.network.OfflineCacheInterceptor,
        online: com.rio.rostry.utils.network.OnlineCacheInterceptor,
        retry: com.rio.rostry.utils.network.RetryInterceptor,
        usage: com.rio.rostry.utils.network.DataUsageInterceptor,
    ): OkHttpClient {
        // Certificate pinner that may be relaxed for Firebase Storage in debug builds
        val certificatePinner = if (BuildConfig.DEBUG) {
            // For debug builds, potentially disable pinning for firebasestorage.googleapis.com
            // This allows us to test SSL protocol issues without certificate pinning interference
            CertificatePinner.Builder()
                // Firebase Firestore (keep pinning for non-storage)
                .add("firestore.googleapis.com",
                    "sha256/FEzVOUp4dF3gI0ZVPRJhFbSJVXR+uQmMH65xhs1glH4=", // Primary Google pin
                    "sha256/iie1VXtL7HzAMF+/PVPR9xzT80kQxdZeJ+zduCB3uj0=", // Backup Google pin
                    "sha256/pL3oJYoiqYu8BaXh2X/Z0tPQBLqNXaHHNWlmYPpbOdQ=") // Backup GeoTrust pin
                // No certificate pinning for Firebase Storage - it uses Google CDN infrastructure
                .build()
        } else {
            // Production certificate pinning
            createCertificatePinner()
        }

        return OkHttpClient.Builder()
            .cache(cache)
            .certificatePinner(certificatePinner)
            .addInterceptor(usage)
            .addInterceptor(offline)
            .addNetworkInterceptor(online)
            .addInterceptor(retry)
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            // Force HTTP/1.1 for Firebase Storage requests to avoid potential HTTP/2 issues
            .protocols(listOf(okhttp3.Protocol.HTTP_1_1))
            .eventListener(object : EventListener() {
                override fun connectionAcquired(call: Call, connection: Connection) {
                    try {
                        TrafficStats.setThreadStatsTag(THREAD_STATS_TAG_HTTP)
                        Timber.d("Storage Socket tagged for HTTP traffic: $THREAD_STATS_TAG_HTTP")
                    } catch (e: Exception) {
                        Timber.w(e, "Failed to tag storage socket for traffic stats")
                    }
                }

                override fun connectionReleased(call: Call, connection: Connection) {
                    try {
                        TrafficStats.clearThreadStatsTag()
                        Timber.d("Storage Socket tag cleared for HTTP traffic")
                    } catch (e: Exception) {
                        Timber.w(e, "Failed to clear storage socket tag")
                    }
                }

                override fun callFailed(call: Call, ioe: java.io.IOException) {
                    if (ioe is javax.net.ssl.SSLPeerUnverifiedException) {
                        Timber.e(ioe, "Storage Certificate pinning failure detected")
                    } else if (ioe is javax.net.ssl.SSLProtocolException) {
                        Timber.e(ioe, "Storage SSL Protocol Exception detected - BAD_DECRYPT possibly due to emulator or network environment")
                    }
                    try {
                        TrafficStats.clearThreadStatsTag()
                    } catch (e: Exception) {
                        Timber.w(e, "Failed to clear storage socket tag on failure")
                    }
                }
            })
            .build()
    }
}
