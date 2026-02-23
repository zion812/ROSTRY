package com.rio.rostry.di

import com.rio.rostry.domain.manager.DegradationManager
import com.rio.rostry.domain.manager.DegradationManagerImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DegradationModule {

    @Binds
    @Singleton
    abstract fun bindDegradationManager(
        impl: DegradationManagerImpl
    ): DegradationManager
}
