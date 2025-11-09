package com.rio.rostry.di

import android.content.Context
import com.google.android.libraries.places.api.Places
import com.rio.rostry.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import timber.log.Timber
import javax.inject.Singleton

/**
 * Provides lazy initialization for Places SDK.
 * Places SDK is now initialized on-demand rather than at app startup
 * to improve startup performance.
 */
@Module
@InstallIn(SingletonComponent::class)
object PlacesModule {

    @Provides
    @Singleton
    fun providePlacesInitializer(@ApplicationContext context: Context): PlacesInitializer {
        return PlacesInitializer(context)
    }
}

/**
 * Lazy initializer for Places SDK.
 * Call [initialize] before first use of Places API.
 */
class PlacesInitializer(private val context: Context) {
    
    @Volatile
    private var initialized = false
    
    fun initialize() {
        if (!initialized) {
            synchronized(this) {
                if (!initialized) {
                    if (!Places.isInitialized()) {
                        Places.initializeWithNewPlacesApiEnabled(context, BuildConfig.MAPS_API_KEY)
                        Timber.d("Places SDK initialized (lazy)")
                    }
                    initialized = true
                }
            }
        }
    }
    
    fun isInitialized(): Boolean = initialized
}
