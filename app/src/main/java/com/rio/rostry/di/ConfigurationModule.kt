package com.rio.rostry.di

import com.rio.rostry.data.database.dao.ConfigurationCacheDao
import com.rio.rostry.domain.manager.ConfigurationManager
import com.rio.rostry.domain.manager.RemoteConfigurationManager
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ConfigurationModule {

    @Provides
    @Singleton
    fun provideConfigurationManager(
        configCacheDao: ConfigurationCacheDao,
        gson: Gson
    ): ConfigurationManager {
        return RemoteConfigurationManager(configCacheDao, gson)
    }
}
