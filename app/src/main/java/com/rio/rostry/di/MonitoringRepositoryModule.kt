package com.rio.rostry.di

import com.rio.rostry.data.repository.BiosecurityRepository
import com.rio.rostry.data.repository.BiosecurityRepositoryImpl
import com.rio.rostry.data.repository.BreedRepository
import com.rio.rostry.data.repository.BreedRepositoryImpl
import com.rio.rostry.data.repository.CommunityRepository
import com.rio.rostry.data.repository.CommunityRepositoryImpl
import com.rio.rostry.data.repository.EnthusiastVerificationRepository
import com.rio.rostry.data.repository.EnthusiastVerificationRepositoryImpl
import com.rio.rostry.data.repository.FarmActivityLogRepository
import com.rio.rostry.data.repository.FarmActivityLogRepositoryImpl
import com.rio.rostry.data.repository.FarmAssetRepository
import com.rio.rostry.data.repository.FarmAssetRepositoryImpl
import com.rio.rostry.data.repository.FarmVerificationRepository
import com.rio.rostry.data.repository.FarmVerificationRepositoryImpl
import com.rio.rostry.data.repository.ShowRecordRepository
import com.rio.rostry.data.repository.ShowRecordRepositoryImpl
import com.rio.rostry.data.repository.analytics.ProfitabilityRepository
import com.rio.rostry.data.repository.analytics.ProfitabilityRepositoryImpl
import com.rio.rostry.data.repository.monitoring.BreedingRepository
import com.rio.rostry.data.repository.monitoring.BreedingRepositoryImpl
import com.rio.rostry.data.repository.monitoring.DailyLogRepository
import com.rio.rostry.data.repository.monitoring.DailyLogRepositoryImpl
import com.rio.rostry.data.repository.monitoring.FarmAlertRepository
import com.rio.rostry.data.repository.monitoring.FarmAlertRepositoryImpl
import com.rio.rostry.data.repository.monitoring.FarmOnboardingRepository
import com.rio.rostry.data.repository.monitoring.FarmOnboardingRepositoryImpl
import com.rio.rostry.data.repository.monitoring.FarmPerformanceRepository
import com.rio.rostry.data.repository.monitoring.FarmPerformanceRepositoryImpl
import com.rio.rostry.data.repository.monitoring.FarmerDashboardRepository
import com.rio.rostry.data.repository.monitoring.FarmerDashboardRepositoryImpl
import com.rio.rostry.data.repository.monitoring.GrowthRepository
import com.rio.rostry.data.repository.monitoring.GrowthRepositoryImpl
import com.rio.rostry.data.repository.monitoring.HatchingRepository
import com.rio.rostry.data.repository.monitoring.HatchingRepositoryImpl
import com.rio.rostry.data.repository.monitoring.ListingDraftRepository
import com.rio.rostry.data.repository.monitoring.ListingDraftRepositoryImpl
import com.rio.rostry.data.repository.monitoring.MortalityRepository
import com.rio.rostry.data.repository.monitoring.MortalityRepositoryImpl
import com.rio.rostry.data.repository.monitoring.QuarantineRepository
import com.rio.rostry.data.repository.monitoring.QuarantineRepositoryImpl
import com.rio.rostry.data.repository.monitoring.TaskRepository
import com.rio.rostry.data.repository.monitoring.TaskRepositoryImpl
import com.rio.rostry.data.repository.monitoring.VaccinationRepository
import com.rio.rostry.data.repository.monitoring.VaccinationRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class MonitoringRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindGrowthRepository(impl: GrowthRepositoryImpl): GrowthRepository

    @Binds
    @Singleton
    abstract fun bindQuarantineRepository(impl: QuarantineRepositoryImpl): QuarantineRepository

    @Binds
    @Singleton
    abstract fun bindMortalityRepository(impl: MortalityRepositoryImpl): MortalityRepository

    @Binds
    @Singleton
    abstract fun bindVaccinationRepository(impl: VaccinationRepositoryImpl): VaccinationRepository

    @Binds
    @Singleton
    abstract fun bindHatchingRepository(impl: HatchingRepositoryImpl): HatchingRepository

    @Binds
    @Singleton
    abstract fun bindFarmPerformanceRepository(impl: FarmPerformanceRepositoryImpl): FarmPerformanceRepository

    @Binds
    @Singleton
    abstract fun bindBreedingRepository(impl: BreedingRepositoryImpl): BreedingRepository

    @Binds
    @Singleton
    abstract fun bindFarmAlertRepository(impl: FarmAlertRepositoryImpl): FarmAlertRepository

    @Binds
    @Singleton
    abstract fun bindListingDraftRepository(impl: ListingDraftRepositoryImpl): ListingDraftRepository

    @Binds
    @Singleton
    abstract fun bindFarmerDashboardRepository(impl: FarmerDashboardRepositoryImpl): FarmerDashboardRepository

    @Binds
    @Singleton
    abstract fun bindFarmOnboardingRepository(impl: FarmOnboardingRepositoryImpl): FarmOnboardingRepository

    @Binds
    @Singleton
    abstract fun bindDailyLogRepository(impl: DailyLogRepositoryImpl): DailyLogRepository

    @Binds
    @Singleton
    abstract fun bindTaskRepository(impl: TaskRepositoryImpl): TaskRepository

    @Binds
    @Singleton
    abstract fun bindBreedRepository(impl: BreedRepositoryImpl): BreedRepository

    @Binds
    @Singleton
    abstract fun bindFarmVerificationRepository(impl: FarmVerificationRepositoryImpl): FarmVerificationRepository

    @Binds
    @Singleton
    abstract fun bindEnthusiastVerificationRepository(impl: EnthusiastVerificationRepositoryImpl): EnthusiastVerificationRepository

    @Binds
    @Singleton
    abstract fun bindFarmAssetRepository(impl: FarmAssetRepositoryImpl): FarmAssetRepository

    @Binds
    @Singleton
    abstract fun bindFarmActivityLogRepository(impl: FarmActivityLogRepositoryImpl): FarmActivityLogRepository

    @Binds
    @Singleton
    abstract fun bindBiosecurityRepository(impl: BiosecurityRepositoryImpl): BiosecurityRepository

    @Binds
    @Singleton
    abstract fun bindProfitabilityRepository(impl: ProfitabilityRepositoryImpl): ProfitabilityRepository

    @Binds
    @Singleton
    abstract fun bindCommunityRepository(impl: CommunityRepositoryImpl): CommunityRepository

    @Binds
    @Singleton
    abstract fun bindShowRecordRepository(impl: ShowRecordRepositoryImpl): ShowRecordRepository
}
