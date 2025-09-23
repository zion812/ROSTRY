package com.rio.rostry.di

import android.graphics.Bitmap
import coil.ImageLoader
import coil.disk.DiskCache
import coil.memory.MemoryCache
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CoilModule {

    @Provides
    @Singleton
    fun provideImageLoader(
        client: OkHttpClient,
        app: android.app.Application
    ): ImageLoader {
        val disk = DiskCache.Builder()
            .directory(app.cacheDir.resolve("image_cache"))
            .maxSizeBytes(50L * 1024 * 1024) // 50MB
            .build()
        val memory = MemoryCache.Builder(app)
            .maxSizePercent(0.15) // 15% of app memory class
            .strongReferencesEnabled(true)
            .build()
        return ImageLoader.Builder(app)
            .okHttpClient(client)
            .crossfade(true)
            .respectCacheHeaders(false)
            .bitmapConfig(Bitmap.Config.RGB_565) // reduce memory on low-end devices
            .allowRgb565(true)
            .diskCache(disk)
            .memoryCache(memory)
            .build()
    }
}
