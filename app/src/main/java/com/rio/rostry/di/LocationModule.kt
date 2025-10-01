package com.rio.rostry.di

import android.app.Application
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import com.rio.rostry.marketplace.location.LocationSearchService
import com.rio.rostry.marketplace.location.LocationService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import timber.log.Timber

@Module
@InstallIn(SingletonComponent::class)
object LocationModule {

    @Provides
    @Singleton
    fun providePlacesClient(app: Application): PlacesClient {
        if (!Places.isInitialized()) {
            // Defensive init (RostryApp initializes too)
            Places.initializeWithNewPlacesApiEnabled(app, com.rio.rostry.BuildConfig.MAPS_API_KEY)
            Timber.d("Places SDK initialized from LocationModule")
        }
        return Places.createClient(app)
    }

    @Provides
    @Singleton
    fun provideLocationService(): LocationService = LocationService

    @Provides
    @Singleton
    fun provideLocationSearchService(
        placesClient: PlacesClient,
        locationService: LocationService
    ): LocationSearchService = LocationSearchService(placesClient, locationService)
}
