package com.rio.rostry.di

import com.rio.rostry.domain.session.SessionRefresher
import com.rio.rostry.domain.session.SessionRefresherImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module providing upgrade-related dependencies.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class UpgradeModule {
    
    /**
     * Provides SessionRefresher abstraction to avoid ViewModel-to-ViewModel injection.
     */
    @Binds
    @Singleton
    abstract fun bindSessionRefresher(impl: SessionRefresherImpl): SessionRefresher
}
