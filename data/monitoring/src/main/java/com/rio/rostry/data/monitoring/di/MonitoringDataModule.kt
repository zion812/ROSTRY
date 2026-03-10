package com.rio.rostry.data.monitoring.di

import com.rio.rostry.data.monitoring.repository.*
import com.rio.rostry.data.monitoring.repository.DailyLogRepositoryImpl
import com.rio.rostry.domain.monitoring.repository.*
import com.rio.rostry.domain.monitoring.repository.DailyLogRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for binding monitoring data implementations.
 * 
 * Phase 2: Domain and Data Decoupling
 * Requirement 4.5 - Data modules use Hilt to bind implementations
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class MonitoringDataModule {

    @Binds
    @Singleton
    abstract fun bindHealthTrackingRepository(
        impl: HealthTrackingRepositoryImpl
    ): HealthTrackingRepository

    @Binds
    @Singleton
    abstract fun bindTaskRepository(
        impl: TaskRepositoryImpl
    ): TaskRepository

    @Binds
    @Singleton
    abstract fun bindAnalyticsRepository(
        impl: AnalyticsRepositoryImpl
    ): AnalyticsRepository

    @Binds
    @Singleton
    abstract fun bindBreedingRepository(
        impl: BreedingRepositoryImpl
    ): BreedingRepository

    @Binds
    @Singleton
    abstract fun bindFarmAlertRepository(
        impl: FarmAlertRepositoryImpl
    ): FarmAlertRepository

    @Binds
    @Singleton
    abstract fun bindFarmerDashboardRepository(
        impl: FarmerDashboardRepositoryImpl
    ): FarmerDashboardRepository

    @Binds
    @Singleton
    abstract fun bindFarmOnboardingRepository(
        impl: FarmOnboardingRepositoryImpl
    ): FarmOnboardingRepository

    @Binds
    @Singleton
    abstract fun bindGrowthRepository(
        impl: GrowthRepositoryImpl
    ): GrowthRepository

    @Binds
    @Singleton
    abstract fun bindQuarantineRepository(
        impl: QuarantineRepositoryImpl
    ): QuarantineRepository

    @Binds
    @Singleton
    abstract fun bindMortalityRepository(
        impl: MortalityRepositoryImpl
    ): MortalityRepository

    @Binds
    @Singleton
    abstract fun bindVaccinationRepository(
        impl: VaccinationRepositoryImpl
    ): VaccinationRepository

    @Binds
    @Singleton
    abstract fun bindHatchingRepository(
        impl: HatchingRepositoryImpl
    ): HatchingRepository

    @Binds
    @Singleton
    abstract fun bindFarmPerformanceRepository(
        impl: FarmPerformanceRepositoryImpl
    ): FarmPerformanceRepository

    @Binds
    @Singleton
    abstract fun bindBreedRepository(
        impl: BreedRepositoryImpl
    ): BreedRepository

    @Binds
    @Singleton
    abstract fun bindBreedingPlanRepository(
        impl: BreedingPlanRepositoryImpl
    ): BreedingPlanRepository

    @Binds
    @Singleton
    abstract fun bindShowRecordRepository(
        impl: ShowRecordRepositoryImpl
    ): ShowRecordRepository

    @Binds
    @Singleton
    abstract fun bindFarmActivityLogRepository(
        impl: FarmActivityLogRepositoryImpl
    ): FarmActivityLogRepository

    @Binds
    @Singleton
    abstract fun bindFarmVerificationRepository(
        impl: FarmVerificationRepositoryImpl
    ): FarmVerificationRepository

    @Binds
    @Singleton
    abstract fun bindEnhancedDailyLogRepository(
        impl: EnhancedDailyLogRepositoryImpl
    ): EnhancedDailyLogRepository

    @Binds
    @Singleton
    abstract fun bindBiosecurityRepository(
        impl: BiosecurityRepositoryImpl
    ): BiosecurityRepository

    @Binds
    @Singleton
    abstract fun bindAlertRepository(
        impl: AlertRepositoryImpl
    ): AlertRepository

    @Binds
    @Singleton
    abstract fun bindTaskSchedulingRepository(
        impl: TaskSchedulingRepositoryImpl
    ): TaskSchedulingRepository

    @Binds
    @Singleton
    abstract fun bindProfitabilityRepository(
        impl: ProfitabilityRepositoryImpl
    ): ProfitabilityRepository

    @Binds
    @Singleton
    abstract fun bindDailyLogRepository(
        impl: DailyLogRepositoryImpl
    ): DailyLogRepository
}
