package com.rio.rostry.di

import com.rio.rostry.data.resilience.CircuitBreakerRegistry
import com.rio.rostry.domain.config.ConfigurationManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ResilienceModule {

    @Provides
    @Singleton
    fun provideCircuitBreakerRegistry(
        configurationManager: ConfigurationManager
    ): CircuitBreakerRegistry {
        return CircuitBreakerRegistry(configurationManager)
    }
}
