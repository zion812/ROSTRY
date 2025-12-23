package com.rio.rostry.di

import com.google.firebase.appcheck.AppCheckProviderFactory
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppCheckModule {

    @Provides
    @Singleton
    fun provideAppCheckProviderFactory(): AppCheckProviderFactory {
        return PlayIntegrityAppCheckProviderFactory.getInstance()
    }
}
