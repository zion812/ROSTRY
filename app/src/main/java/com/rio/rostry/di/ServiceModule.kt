package com.rio.rostry.di

import com.rio.rostry.data.location.LocationService
import com.rio.rostry.data.location.LocationServiceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class ServiceModule {

    @Binds
    abstract fun bindLocationService(impl: LocationServiceImpl): LocationService
}
