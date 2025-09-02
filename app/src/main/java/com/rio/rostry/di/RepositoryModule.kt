package com.rio.rostry.di

import com.rio.rostry.data.repo.SyncRepository
import com.rio.rostry.data.repo.AdminRepository
import com.rio.rostry.data.repo.AdminRepositoryImpl
import com.rio.rostry.data.repo.AnalyticsRepository
import com.rio.rostry.data.repo.AnalyticsRepositoryImpl
import com.rio.rostry.data.repo.SyncRepositoryImpl
import com.rio.rostry.data.repo.TransferRepository
import com.rio.rostry.data.repo.TransferRepositoryImpl
import com.rio.rostry.data.repo.MarketplaceV2Repository
import com.rio.rostry.data.repo.MarketplaceV2RepositoryImpl
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

    @Binds
    abstract fun bindAnalyticsRepository(impl: AnalyticsRepositoryImpl): AnalyticsRepository

    @Binds
    abstract fun bindMarketplaceV2Repository(impl: MarketplaceV2RepositoryImpl): MarketplaceV2Repository
}
