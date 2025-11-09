package com.rio.rostry.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.work.WorkManager
import com.rio.rostry.domain.auth.repository.AuthRepository
import com.rio.rostry.utils.analytics.FlowAnalyticsTracker
import com.rio.rostry.utils.analytics.FlowAnalyticsTrackerImpl
import com.rio.rostry.utils.performance.DatabaseOptimizer
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class FlowAnalyticsBindingsModule {
    @Binds
    @Singleton
    abstract fun bindFlowAnalyticsTracker(impl: FlowAnalyticsTrackerImpl): FlowAnalyticsTracker
}

@Module
@InstallIn(SingletonComponent::class)
object UtilsModule {

    @Provides
    @Singleton
    fun provideDatabaseOptimizer(): DatabaseOptimizer = DatabaseOptimizer

    @Provides
    @Singleton
    fun provideWorkManager(@ApplicationContext context: Context): WorkManager = WorkManager.getInstance(context)

    @Provides
    @Singleton
    fun providePreferencesDataStore(@ApplicationContext context: Context): DataStore<Preferences> =
        PreferenceDataStoreFactory.create { context.preferencesDataStoreFile("user_prefs") }

    @Provides
    @Singleton
    fun provideAuthRepositoryDefault(
        @Named("AuthRepositoryNew") repo: AuthRepository
    ): AuthRepository = repo
}
