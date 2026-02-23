package com.rio.rostry.di

import com.rio.rostry.data.database.dao.ErrorLogDao
import com.rio.rostry.data.resilience.FallbackManager
import com.rio.rostry.domain.error.CentralizedErrorHandler
import com.rio.rostry.domain.error.ErrorHandler
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ErrorHandlerModule {

    @Provides
    @Singleton
    fun provideErrorHandler(
        errorLogDao: ErrorLogDao,
        fallbackManager: FallbackManager
    ): ErrorHandler {
        return CentralizedErrorHandler(errorLogDao, fallbackManager)
    }
}
