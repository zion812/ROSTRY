package com.rio.rostry.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Cache
import okhttp3.OkHttpClient
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object HttpModule {

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
        return OkHttpClient.Builder()
            .cache(cache)
            .addInterceptor(usage)
            .addInterceptor(offline)
            .addNetworkInterceptor(online)
            .addInterceptor(retry)
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .build()
    }
}
