package com.rio.rostry.di

import com.rio.rostry.data.database.dao.AnalyticsDao
import com.rio.rostry.data.repository.analytics.AnalyticsRepository
import com.rio.rostry.data.repository.analytics.AnalyticsRepositoryImpl
import com.rio.rostry.ui.general.analytics.GeneralAnalyticsTracker
import com.rio.rostry.ui.general.analytics.GeneralAnalyticsTrackerImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AnalyticsBindingsModule {
    @Binds
    @Singleton
    abstract fun bindGeneralAnalyticsTracker(impl: GeneralAnalyticsTrackerImpl): GeneralAnalyticsTracker
}

@Module
@InstallIn(SingletonComponent::class)
object AnalyticsModule {
    @Provides
    @Singleton
    fun provideAnalyticsRepository(analyticsDao: AnalyticsDao): AnalyticsRepository {
        return AnalyticsRepositoryImpl(analyticsDao)
    }
}
