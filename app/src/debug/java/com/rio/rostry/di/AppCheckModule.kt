package com.rio.rostry.di

import com.google.firebase.appcheck.AppCheckProviderFactory
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
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
        // You can hardcode a token here for physical device testing.
        // Register this same string in Firebase Console -> App Check -> Manage Debug Tokens
        // Example: "your-custom-debug-token-here"
        
        // For now, we will still use the dynamic one but I'll add a placeholder 
        // that you can replace to make it permanent.
        return DebugAppCheckProviderFactory.getInstance()
    }
}
