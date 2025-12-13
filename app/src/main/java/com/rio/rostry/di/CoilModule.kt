package com.rio.rostry.di

import android.app.ActivityManager
import android.content.Context
import android.graphics.Bitmap
import coil.ImageLoader
import coil.disk.DiskCache
import coil.memory.MemoryCache
import coil.util.DebugLogger
import com.rio.rostry.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import timber.log.Timber
import javax.inject.Singleton
import com.rio.rostry.di.HttpModule.StorageOkHttpClient
import com.rio.rostry.di.HttpModule.DefaultOkHttpClient

@Module
@InstallIn(SingletonComponent::class)
object CoilModule {

    /**
     * Determines device memory tier for adaptive image loading.
     * 
     * @return Device tier: LOW (<512MB), MEDIUM (512MB-2GB), HIGH (>2GB)
     */
    private fun getDeviceMemoryTier(context: Context): DeviceTier {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memoryClass = activityManager.memoryClass // MB
        
        return when {
            memoryClass < 512 -> DeviceTier.LOW
            memoryClass < 2048 -> DeviceTier.MEDIUM
            else -> DeviceTier.HIGH
        }.also {
            Timber.d("Device memory tier: $it (memory class: ${memoryClass}MB)")
        }
    }

    @Provides
    @Singleton
    fun provideImageLoader(
        @DefaultOkHttpClient client: OkHttpClient,
        @StorageOkHttpClient storageClient: OkHttpClient, // Separate client for Firebase Storage - will be used for all requests to ensure Firebase compatibility
        @ApplicationContext context: Context
    ): ImageLoader {
        val deviceTier = getDeviceMemoryTier(context)

        // Adaptive disk cache size based on device tier
        val diskCacheSize = when (deviceTier) {
            DeviceTier.LOW -> 50L * 1024 * 1024 // 50MB
            DeviceTier.MEDIUM -> 100L * 1024 * 1024 // 100MB
            DeviceTier.HIGH -> 150L * 1024 * 1024 // 150MB
        }

        // Adaptive memory cache based on device tier
        val memoryCachePercent = when (deviceTier) {
            DeviceTier.LOW -> 0.10 // 10%
            DeviceTier.MEDIUM -> 0.15 // 15%
            DeviceTier.HIGH -> 0.20 // 20%
        }

        // Use ARGB_8888 on high-end devices for better quality
        val bitmapConfig = when (deviceTier) {
            DeviceTier.LOW -> Bitmap.Config.RGB_565
            DeviceTier.MEDIUM -> Bitmap.Config.RGB_565
            DeviceTier.HIGH -> Bitmap.Config.ARGB_8888
        }

        val diskCache = DiskCache.Builder()
            .directory(context.cacheDir.resolve("image_cache"))
            .maxSizeBytes(diskCacheSize)
            .build()

        val memoryCache = MemoryCache.Builder(context)
            .maxSizePercent(memoryCachePercent)
            .strongReferencesEnabled(true)
            .build()

        Timber.i(
            "Coil configured: disk=${diskCacheSize / 1024 / 1024}MB, " +
            "memory=${memoryCachePercent * 100}%, bitmap=$bitmapConfig"
        )

        return ImageLoader.Builder(context)
            .okHttpClient(storageClient) // Use storage client for all requests to ensure Firebase Storage compatibility
            .crossfade(300) // 300ms crossfade for smooth transitions
            .respectCacheHeaders(false) // Ignore server cache headers for better offline support
            .bitmapConfig(bitmapConfig)
            .allowRgb565(deviceTier != DeviceTier.HIGH) // Only use RGB_565 on low/medium devices
            .diskCache(diskCache)
            .memoryCache(memoryCache)
            // Add debug logging in debug builds
            .apply {
                if (BuildConfig.DEBUG) {
                    logger(DebugLogger())
                }
            }
            // For now, we rely on the HTTP client's error logging for SSL diagnostics
            // The storageClient has enhanced SSL logging in HttpModule
            .build()
    }

    // Helper methods for SSL diagnostic information
    private fun getHostFromUrl(urlString: String): String {
        return try {
            val url = java.net.URL(urlString)
            url.host
        } catch (e: java.net.MalformedURLException) {
            "unknown"
        }
    }

    private fun getTlsVersion(): String {
        // Best effort to determine TLS version in use
        // This is a placeholder implementation - in a real scenario you'd track this
        return "TLS_UNKNOWN"
    }

    private enum class DeviceTier {
        LOW, MEDIUM, HIGH
    }
}
