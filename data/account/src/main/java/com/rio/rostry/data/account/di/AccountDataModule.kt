package com.rio.rostry.data.account.di

import com.rio.rostry.data.account.repository.AuthRepositoryImpl
import com.rio.rostry.data.account.repository.CoinRepositoryImpl
import com.rio.rostry.data.account.repository.EnthusiastVerificationRepositoryImpl
import com.rio.rostry.data.account.repository.FeedbackRepositoryImpl
import com.rio.rostry.data.account.repository.RoleMigrationRepositoryImpl
import com.rio.rostry.data.account.repository.RoleUpgradeRequestRepositoryImpl
import com.rio.rostry.data.account.repository.StorageRepositoryImpl
import com.rio.rostry.data.account.repository.StorageUsageRepositoryImpl
import com.rio.rostry.data.account.repository.UserRepositoryImpl
import com.rio.rostry.data.account.repository.VerificationDraftRepositoryImpl
import com.rio.rostry.domain.account.repository.AuthRepository
import com.rio.rostry.domain.account.repository.CoinRepository
import com.rio.rostry.domain.account.repository.EnthusiastVerificationRepository
import com.rio.rostry.domain.account.repository.FeedbackRepository
import com.rio.rostry.domain.account.repository.RoleMigrationRepository
import com.rio.rostry.domain.account.repository.RoleUpgradeRequestRepository
import com.rio.rostry.domain.account.repository.StorageRepository
import com.rio.rostry.domain.account.repository.StorageUsageRepository
import com.rio.rostry.domain.account.repository.UserRepository
import com.rio.rostry.domain.account.repository.VerificationDraftRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for binding account data implementations.
 * 
 * Phase 2: Domain and Data Decoupling
 * Requirement 4.5 - Data modules use Hilt to bind implementations
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class AccountDataModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        impl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindUserRepository(
        impl: UserRepositoryImpl
    ): UserRepository

    @Binds
    @Singleton
    abstract fun bindCoinRepository(
        impl: CoinRepositoryImpl
    ): CoinRepository

    @Binds
    @Singleton
    abstract fun bindRoleMigrationRepository(
        impl: RoleMigrationRepositoryImpl
    ): RoleMigrationRepository

    @Binds
    @Singleton
    abstract fun bindRoleUpgradeRequestRepository(
        impl: RoleUpgradeRequestRepositoryImpl
    ): RoleUpgradeRequestRepository

    @Binds
    @Singleton
    abstract fun bindFeedbackRepository(
        impl: FeedbackRepositoryImpl
    ): FeedbackRepository

    @Binds
    @Singleton
    abstract fun bindStorageRepository(
        impl: StorageRepositoryImpl
    ): StorageRepository

    @Binds
    @Singleton
    abstract fun bindStorageUsageRepository(
        impl: StorageUsageRepositoryImpl
    ): StorageUsageRepository

    @Binds
    @Singleton
    abstract fun bindEnthusiastVerificationRepository(
        impl: EnthusiastVerificationRepositoryImpl
    ): EnthusiastVerificationRepository

    @Binds
    @Singleton
    abstract fun bindVerificationDraftRepository(
        impl: VerificationDraftRepositoryImpl
    ): VerificationDraftRepository
}
