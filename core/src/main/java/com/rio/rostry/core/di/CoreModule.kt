package com.rio.rostry.core.di

import com.rio.rostry.core.AppDispatchers
import com.rio.rostry.core.DefaultAppDispatchers
import com.rio.rostry.core.NetworkMonitor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CoreModule {
    @Provides
    @Singleton
    fun provideDispatchers(): AppDispatchers = DefaultAppDispatchers()

    @Provides
    @Singleton
    fun provideNetworkMonitor(monitor: NetworkMonitor): NetworkMonitor = monitor
}
