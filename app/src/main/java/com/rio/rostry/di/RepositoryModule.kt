package com.rio.rostry.di

import com.rio.rostry.data.repo.SyncRepository
import com.rio.rostry.data.repo.AdminRepository
import com.rio.rostry.data.repo.AdminRepositoryImpl
import com.rio.rostry.data.repo.SyncRepositoryImpl
import com.rio.rostry.data.repo.TransferRepository
import com.rio.rostry.data.repo.TransferRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindSyncRepository(impl: SyncRepositoryImpl): SyncRepository

    @Binds
    abstract fun bindTransferRepository(impl: TransferRepositoryImpl): TransferRepository

    @Binds
    abstract fun bindAdminRepository(impl: AdminRepositoryImpl): AdminRepository
}
