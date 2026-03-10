package com.rio.rostry.data.admin.di

import com.rio.rostry.data.admin.repository.AdminMortalityRepositoryImpl
import com.rio.rostry.data.admin.repository.AdminProductRepositoryImpl
import com.rio.rostry.data.admin.repository.AdminRepositoryImpl
import com.rio.rostry.data.admin.repository.AuditRepositoryImpl
import com.rio.rostry.data.admin.repository.ModerationRepositoryImpl
import com.rio.rostry.data.admin.repository.SystemConfigRepositoryImpl
import com.rio.rostry.domain.admin.repository.AdminMortalityRepository
import com.rio.rostry.domain.admin.repository.AdminProductRepository
import com.rio.rostry.domain.admin.repository.AdminRepository
import com.rio.rostry.domain.admin.repository.AuditRepository
import com.rio.rostry.domain.admin.repository.ModerationRepository
import com.rio.rostry.domain.admin.repository.SystemConfigRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for binding admin data implementations.
 * 
 * Phase 2: Domain and Data Decoupling
 * Requirement 4.5 - Data modules use Hilt to bind implementations
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class AdminDataModule {

    @Binds
    @Singleton
    abstract fun bindAdminRepository(
        impl: AdminRepositoryImpl
    ): AdminRepository

    @Binds
    @Singleton
    abstract fun bindModerationRepository(
        impl: ModerationRepositoryImpl
    ): ModerationRepository

    @Binds
    @Singleton
    abstract fun bindSystemConfigRepository(
        impl: SystemConfigRepositoryImpl
    ): SystemConfigRepository

    @Binds
    @Singleton
    abstract fun bindAuditRepository(
        impl: AuditRepositoryImpl
    ): AuditRepository

    @Binds
    @Singleton
    abstract fun bindAdminProductRepository(
        impl: AdminProductRepositoryImpl
    ): AdminProductRepository

    @Binds
    @Singleton
    abstract fun bindAdminMortalityRepository(
        impl: AdminMortalityRepositoryImpl
    ): AdminMortalityRepository
}
