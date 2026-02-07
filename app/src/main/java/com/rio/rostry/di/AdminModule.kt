package com.rio.rostry.di

import com.rio.rostry.data.repository.AdminRepository
import com.rio.rostry.data.repository.AdminRepositoryImpl
import com.rio.rostry.data.repository.SystemConfigRepository
import com.rio.rostry.data.repository.SystemConfigRepositoryImpl
import com.rio.rostry.data.repository.ModerationRepository
import com.rio.rostry.data.repository.ModerationRepositoryImpl
import com.rio.rostry.data.repository.ReportRepository
import com.rio.rostry.data.repository.ReportRepositoryImpl
import com.rio.rostry.data.repository.admin.AdminMortalityRepository
import com.rio.rostry.data.repository.admin.AdminMortalityRepositoryImpl
import com.rio.rostry.data.repository.AuditRepository
import com.rio.rostry.data.repository.AuditRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AdminModule {

    @Binds
    @Singleton
    abstract fun bindAdminRepository(impl: AdminRepositoryImpl): AdminRepository

    @Binds
    @Singleton
    abstract fun bindSystemConfigRepository(impl: SystemConfigRepositoryImpl): SystemConfigRepository

    @Binds
    @Singleton
    abstract fun bindModerationRepository(impl: ModerationRepositoryImpl): ModerationRepository

    @Binds
    @Singleton
    abstract fun bindReportRepository(impl: ReportRepositoryImpl): ReportRepository

    @Binds
    @Singleton
    abstract fun bindAdminMortalityRepository(impl: AdminMortalityRepositoryImpl): AdminMortalityRepository

    @Binds
    @Singleton
    abstract fun bindAuditRepository(impl: AuditRepositoryImpl): AuditRepository
}
