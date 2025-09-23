package com.rio.rostry.di

import com.rio.rostry.data.database.dao.AnalyticsDao
import com.rio.rostry.data.repository.analytics.AnalyticsRepository
import com.rio.rostry.data.repository.analytics.AnalyticsRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AnalyticsModule {
    @Provides
    @Singleton
    fun provideAnalyticsRepository(analyticsDao: AnalyticsDao): AnalyticsRepository {
        return AnalyticsRepositoryImpl(analyticsDao)
    }
}
