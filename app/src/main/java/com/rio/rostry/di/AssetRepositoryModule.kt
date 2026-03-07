package com.rio.rostry.di

import com.rio.rostry.data.manager.MediaCacheManagerImpl
import com.rio.rostry.data.repository.AssetBatchOperationRepositoryImpl
import com.rio.rostry.data.repository.AssetLifecycleRepositoryImpl
import com.rio.rostry.data.repository.BreedingPlanRepository
import com.rio.rostry.data.repository.BreedingPlanRepositoryImpl
import com.rio.rostry.data.repository.EnhancedDailyLogRepositoryImpl
import com.rio.rostry.data.repository.MediaGalleryRepositoryImpl
import com.rio.rostry.data.repository.TaskSchedulingRepositoryImpl
import com.rio.rostry.domain.manager.MediaCacheManager
import com.rio.rostry.domain.repository.AssetBatchOperationRepository
import com.rio.rostry.domain.repository.AssetLifecycleRepository
import com.rio.rostry.domain.repository.EnhancedDailyLogRepository
import com.rio.rostry.domain.repository.MediaGalleryRepository
import com.rio.rostry.domain.repository.TaskSchedulingRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AssetRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindBreedingPlanRepository(impl: BreedingPlanRepositoryImpl): BreedingPlanRepository

    @Binds
    @Singleton
    abstract fun bindAssetLifecycleRepository(impl: AssetLifecycleRepositoryImpl): AssetLifecycleRepository

    @Binds
    @Singleton
    abstract fun bindAssetBatchOperationRepository(impl: AssetBatchOperationRepositoryImpl): AssetBatchOperationRepository

    @Binds
    @Singleton
    abstract fun bindTaskSchedulingRepository(impl: TaskSchedulingRepositoryImpl): TaskSchedulingRepository

    @Binds
    @Singleton
    abstract fun bindEnhancedDailyLogRepository(impl: EnhancedDailyLogRepositoryImpl): EnhancedDailyLogRepository

    @Binds
    @Singleton
    abstract fun bindMediaCacheManager(impl: MediaCacheManagerImpl): MediaCacheManager

    @Binds
    @Singleton
    abstract fun bindMediaGalleryRepository(impl: MediaGalleryRepositoryImpl): MediaGalleryRepository
}
