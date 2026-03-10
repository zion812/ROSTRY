package com.rio.rostry.di

import com.rio.rostry.data.repository.AlertRepository
import com.rio.rostry.data.repository.RoleMigrationRepository
import com.rio.rostry.data.repository.RoleMigrationRepositoryImpl
import com.rio.rostry.data.repository.RoleUpgradeRequestRepository
import com.rio.rostry.data.repository.RoleUpgradeRequestRepositoryImpl
import com.rio.rostry.data.repository.TransferRepository
import com.rio.rostry.data.repository.TransferRepositoryImpl
import com.rio.rostry.data.repository.TransferWorkflowRepository
import com.rio.rostry.data.repository.TransferWorkflowRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class OperationsRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindTransferRepository(impl: TransferRepositoryImpl): TransferRepository

    @Binds
    @Singleton
    abstract fun bindTransferWorkflowRepository(impl: TransferWorkflowRepositoryImpl): TransferWorkflowRepository



    @Binds
    @Singleton
    abstract fun bindRoleUpgradeRequestRepository(impl: RoleUpgradeRequestRepositoryImpl): RoleUpgradeRequestRepository

    @Binds
    @Singleton
    abstract fun bindRoleMigrationRepository(impl: RoleMigrationRepositoryImpl): RoleMigrationRepository


}
