package com.rio.rostry.di

import com.rio.rostry.data.sync.FirestoreService
import com.rio.rostry.data.sync.SyncRemote
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RemoteModule {
    @Binds
    @Singleton
    abstract fun bindSyncRemote(impl: FirestoreService): SyncRemote
}
