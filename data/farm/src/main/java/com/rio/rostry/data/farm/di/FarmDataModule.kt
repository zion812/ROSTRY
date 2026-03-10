package com.rio.rostry.data.farm.di

import com.rio.rostry.data.farm.repository.AssetBatchOperationRepositoryImpl
import com.rio.rostry.data.farm.repository.AssetLifecycleRepositoryImpl
import com.rio.rostry.data.farm.repository.ExpenseRepositoryImpl
import com.rio.rostry.data.farm.repository.FarmAssetRepositoryImpl
import com.rio.rostry.data.farm.repository.InventoryRepositoryImpl
import com.rio.rostry.data.farm.repository.PublicBirdRepositoryImpl
import com.rio.rostry.data.farm.repository.TraceabilityRepositoryImpl
import com.rio.rostry.data.farm.repository.TrackingRepositoryImpl
import com.rio.rostry.data.farm.repository.TransferRepositoryImpl
import com.rio.rostry.data.farm.repository.TransferWorkflowRepositoryImpl
import com.rio.rostry.domain.farm.repository.AssetBatchOperationRepository
import com.rio.rostry.domain.farm.repository.AssetLifecycleRepository
import com.rio.rostry.domain.farm.repository.ExpenseRepository
import com.rio.rostry.domain.farm.repository.FarmAssetRepository
import com.rio.rostry.domain.farm.repository.InventoryRepository
import com.rio.rostry.domain.farm.repository.PublicBirdRepository
import com.rio.rostry.domain.farm.repository.TraceabilityRepository
import com.rio.rostry.domain.farm.repository.TrackingRepository
import com.rio.rostry.domain.farm.repository.TransferRepository
import com.rio.rostry.domain.farm.repository.TransferWorkflowRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for binding farm data implementations.
 * 
 * Phase 2: Domain and Data Decoupling
 * Requirement 4.5 - Data modules use Hilt to bind implementations
 * Task 11.4.3 - Farm Domain repository migration
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class FarmDataModule {

    @Binds
    @Singleton
    abstract fun bindFarmAssetRepository(
        impl: FarmAssetRepositoryImpl
    ): FarmAssetRepository

    @Binds
    @Singleton
    abstract fun bindInventoryRepository(
        impl: InventoryRepositoryImpl
    ): InventoryRepository

    @Binds
    @Singleton
    abstract fun bindTrackingRepository(
        impl: TrackingRepositoryImpl
    ): TrackingRepository

    @Binds
    @Singleton
    abstract fun bindPublicBirdRepository(
        impl: PublicBirdRepositoryImpl
    ): PublicBirdRepository

    @Binds
    @Singleton
    abstract fun bindExpenseRepository(
        impl: ExpenseRepositoryImpl
    ): ExpenseRepository

    @Binds
    @Singleton
    abstract fun bindTransferRepository(
        impl: TransferRepositoryImpl
    ): TransferRepository

    @Binds
    @Singleton
    abstract fun bindTransferWorkflowRepository(
        impl: TransferWorkflowRepositoryImpl
    ): TransferWorkflowRepository

    @Binds
    @Singleton
    abstract fun bindTraceabilityRepository(
        impl: TraceabilityRepositoryImpl
    ): TraceabilityRepository

    @Binds
    @Singleton
    abstract fun bindAssetBatchOperationRepository(
        impl: AssetBatchOperationRepositoryImpl
    ): AssetBatchOperationRepository

    @Binds
    @Singleton
    abstract fun bindAssetLifecycleRepository(
        impl: AssetLifecycleRepositoryImpl
    ): AssetLifecycleRepository
}
