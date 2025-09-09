package com.rio.rostry.di

import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.rio.rostry.core.ErrorLogger
import com.rio.rostry.logging.CrashlyticsErrorLogger
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppBindingsModule {
    @Binds
    @Singleton
    abstract fun bindErrorLogger(impl: CrashlyticsErrorLogger): ErrorLogger
}

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideCrashlytics(): FirebaseCrashlytics = FirebaseCrashlytics.getInstance()
}
