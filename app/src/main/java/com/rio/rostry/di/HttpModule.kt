package com.rio.rostry.di

import android.content.Context
import com.rio.rostry.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Cache
import okhttp3.CertificatePinner
import okhttp3.OkHttpClient
import timber.log.Timber
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object HttpModule {

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
                // Firebase Storage
                .add("firebasestorage.googleapis.com",
                    "sha256/FEzVOUp4dF3gI0ZVPRJhFbSJVXR+uQmMH65xhs1glH4=",
                    "sha256/iie1VXtL7HzAMF+/PVPR9xzT80kQxdZeJ+zduCB3uj0=",
                    "sha256/pL3oJYoiqYu8BaXh2X/Z0tPQBLqNXaHHNWlmYPpbOdQ=")
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
        // 10 MB HTTP cache for API/Coil
        val cacheDir = File(context.cacheDir, "http_cache")
        return Cache(cacheDir, 10L * 1024 * 1024)
    }

    @Provides
    @Singleton
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
            .eventListener(object : okhttp3.EventListener() {
                override fun callFailed(call: okhttp3.Call, ioe: java.io.IOException) {
                    if (ioe is javax.net.ssl.SSLPeerUnverifiedException) {
                        Timber.e(ioe, "Certificate pinning failure detected")
                    }
                }
            })
            .build()
    }
}
