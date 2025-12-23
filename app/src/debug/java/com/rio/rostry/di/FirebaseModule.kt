package com.rio.rostry.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {
    // AppCheckProviderFactory is provided in AppCheckModule
}
