package com.rio.rostry.di

import com.rio.rostry.core.common.sync.SyncManager
import com.rio.rostry.data.sync.SyncManager as SyncManagerImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SyncModule {

    @Binds
    @Singleton
    abstract fun bindSyncManager(
        syncManagerImpl: SyncManagerImpl
    ): SyncManager
}
