package com.rio.rostry.di

import com.rio.rostry.data.repository.ProductRepository
import com.rio.rostry.data.repository.ProductRepositoryImpl
import com.rio.rostry.data.repository.UserRepository
import com.rio.rostry.data.repository.UserRepositoryImpl
import com.rio.rostry.data.repository.LikesRepository
import com.rio.rostry.data.repository.LikesRepositoryImpl
import com.rio.rostry.data.repository.EnthusiastBreedingRepository
import com.rio.rostry.data.repository.EnthusiastBreedingRepositoryImpl
// Import your other repository interfaces and implementations here
import com.rio.rostry.domain.auth.AuthRepository
import com.rio.rostry.data.auth.AuthRepositoryImpl
import com.rio.rostry.data.repository.TrackingRepository
import com.rio.rostry.data.repository.TrackingRepositoryImpl
import com.rio.rostry.data.repository.FamilyTreeRepository
import com.rio.rostry.data.repository.FamilyTreeRepositoryImpl
import com.rio.rostry.data.repository.ChatRepository
import com.rio.rostry.data.repository.ChatRepositoryImpl
import com.rio.rostry.data.repository.TransferRepository
import com.rio.rostry.data.repository.TransferRepositoryImpl
import com.rio.rostry.data.repository.ProductMarketplaceRepository
import com.rio.rostry.data.repository.ProductMarketplaceRepositoryImpl
import com.rio.rostry.data.repository.AuctionRepository
import com.rio.rostry.data.repository.AuctionRepositoryImpl
import com.rio.rostry.data.repository.CartRepository
import com.rio.rostry.data.repository.CartRepositoryImpl
import com.rio.rostry.data.repository.WishlistRepository
import com.rio.rostry.data.repository.WishlistRepositoryImpl
import com.rio.rostry.data.repository.OrderManagementRepository
import com.rio.rostry.data.repository.OrderManagementRepositoryImpl
import com.rio.rostry.data.repository.PaymentRepository
import com.rio.rostry.data.repository.PaymentRepositoryImpl
import com.rio.rostry.data.repository.CoinRepository
import com.rio.rostry.data.repository.CoinRepositoryImpl
import com.rio.rostry.data.repository.LogisticsRepository
import com.rio.rostry.data.repository.LogisticsRepositoryImpl
import com.rio.rostry.data.repository.OrderRepository
import com.rio.rostry.data.repository.AdvancedOrderService
import com.rio.rostry.data.repository.InvoiceRepository
import com.rio.rostry.data.repository.InvoiceRepositoryImpl
import com.rio.rostry.data.repository.TraceabilityRepository
import com.rio.rostry.data.repository.TraceabilityRepositoryImpl
import com.rio.rostry.data.repository.TransferWorkflowRepository
import com.rio.rostry.data.repository.TransferWorkflowRepositoryImpl
import com.rio.rostry.data.repository.social.SocialRepository
import com.rio.rostry.data.repository.social.SocialRepositoryImpl
import com.rio.rostry.data.repository.social.MessagingRepository
import com.rio.rostry.data.repository.social.MessagingRepositoryImpl
import com.rio.rostry.data.repository.monitoring.GrowthRepository
import com.rio.rostry.data.repository.monitoring.GrowthRepositoryImpl
import com.rio.rostry.data.repository.monitoring.QuarantineRepository
import com.rio.rostry.data.repository.monitoring.QuarantineRepositoryImpl
import com.rio.rostry.data.repository.monitoring.MortalityRepository
import com.rio.rostry.data.repository.monitoring.FarmOnboardingRepository
import com.rio.rostry.data.repository.monitoring.FarmOnboardingRepositoryImpl
import com.rio.rostry.data.repository.monitoring.MortalityRepositoryImpl
import com.rio.rostry.data.repository.monitoring.VaccinationRepository
import com.rio.rostry.data.repository.monitoring.VaccinationRepositoryImpl
import com.rio.rostry.data.repository.monitoring.HatchingRepository
import com.rio.rostry.data.repository.monitoring.HatchingRepositoryImpl
import com.rio.rostry.data.repository.monitoring.FarmPerformanceRepository
import com.rio.rostry.data.repository.monitoring.FarmPerformanceRepositoryImpl
import com.rio.rostry.data.repository.monitoring.DailyLogRepository
import com.rio.rostry.data.repository.monitoring.DailyLogRepositoryImpl
import com.rio.rostry.data.repository.monitoring.TaskRepository
import com.rio.rostry.data.repository.monitoring.TaskRepositoryImpl
import com.rio.rostry.data.repository.FarmVerificationRepository
import com.rio.rostry.data.repository.FarmVerificationRepositoryImpl
import com.rio.rostry.data.repository.FarmAssetRepository
import com.rio.rostry.data.repository.FarmAssetRepositoryImpl
import com.rio.rostry.data.repository.InventoryRepository
import com.rio.rostry.data.repository.InventoryRepositoryImpl
import com.rio.rostry.data.repository.MarketListingRepository
import com.rio.rostry.data.repository.MarketListingRepositoryImpl
import com.rio.rostry.domain.pedigree.PedigreeRepository
import com.rio.rostry.domain.pedigree.PedigreeRepositoryImpl
import com.rio.rostry.marketplace.payment.DefaultPaymentGateway
import com.rio.rostry.marketplace.payment.PaymentGateway

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindUserRepository(userRepositoryImpl: UserRepositoryImpl): UserRepository

    @Binds
    @Singleton
    abstract fun bindProductRepository(productRepositoryImpl: ProductRepositoryImpl): ProductRepository

    // ... and so on for other repositories

    @Binds
    @Singleton
    abstract fun bindAuthRepository(authRepositoryImpl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun bindTrackingRepository(impl: TrackingRepositoryImpl): TrackingRepository

    @Binds
    @Singleton
    abstract fun bindFamilyTreeRepository(impl: FamilyTreeRepositoryImpl): FamilyTreeRepository

    @Binds
    @Singleton
    abstract fun bindChatRepository(impl: ChatRepositoryImpl): ChatRepository

    @Binds
    @Singleton
    abstract fun bindTransferRepository(impl: TransferRepositoryImpl): TransferRepository

    @Binds
    @Singleton
    abstract fun bindProductMarketplaceRepository(impl: ProductMarketplaceRepositoryImpl): ProductMarketplaceRepository

    @Binds
    @Singleton
    abstract fun bindAuctionRepository(impl: AuctionRepositoryImpl): AuctionRepository

    @Binds
    @Singleton
    abstract fun bindCartRepository(impl: CartRepositoryImpl): CartRepository

    @Binds
    @Singleton
    abstract fun bindWishlistRepository(impl: WishlistRepositoryImpl): WishlistRepository

    @Binds
    @Singleton
    abstract fun bindOrderManagementRepository(impl: OrderManagementRepositoryImpl): OrderManagementRepository

    @Binds
    @Singleton
    abstract fun bindOrderRepository(impl: AdvancedOrderService): OrderRepository

    @Binds
    @Singleton
    abstract fun bindPaymentRepository(impl: PaymentRepositoryImpl): PaymentRepository

    @Binds
    @Singleton
    abstract fun bindCoinRepository(impl: CoinRepositoryImpl): CoinRepository

    @Binds
    @Singleton
    abstract fun bindLogisticsRepository(impl: LogisticsRepositoryImpl): LogisticsRepository

    @Binds
    @Singleton
    abstract fun bindInvoiceRepository(impl: InvoiceRepositoryImpl): InvoiceRepository

    @Binds
    @Singleton
    abstract fun bindTraceabilityRepository(impl: TraceabilityRepositoryImpl): TraceabilityRepository

    @Binds
    @Singleton
    abstract fun bindTransferWorkflowRepository(impl: TransferWorkflowRepositoryImpl): TransferWorkflowRepository

    @Binds
    @Singleton
    abstract fun bindSocialRepository(impl: SocialRepositoryImpl): SocialRepository

    @Binds
    @Singleton
    abstract fun bindMessagingRepository(impl: MessagingRepositoryImpl): MessagingRepository

    // Monitoring repositories
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
    abstract fun bindBreedingRepository(impl: com.rio.rostry.data.repository.monitoring.BreedingRepositoryImpl): com.rio.rostry.data.repository.monitoring.BreedingRepository

    @Binds
    @Singleton
    abstract fun bindFarmAlertRepository(impl: com.rio.rostry.data.repository.monitoring.FarmAlertRepositoryImpl): com.rio.rostry.data.repository.monitoring.FarmAlertRepository

    @Binds
    @Singleton
    abstract fun bindListingDraftRepository(impl: com.rio.rostry.data.repository.monitoring.ListingDraftRepositoryImpl): com.rio.rostry.data.repository.monitoring.ListingDraftRepository

    @Binds
    @Singleton
    abstract fun bindFarmerDashboardRepository(impl: com.rio.rostry.data.repository.monitoring.FarmerDashboardRepositoryImpl): com.rio.rostry.data.repository.monitoring.FarmerDashboardRepository

    @Binds
    @Singleton
    abstract fun bindFarmOnboardingRepository(impl: FarmOnboardingRepositoryImpl): FarmOnboardingRepository

    @Binds
    @Singleton
    abstract fun bindCommunityRepository(impl: com.rio.rostry.data.repository.CommunityRepositoryImpl): com.rio.rostry.data.repository.CommunityRepository

    // Enthusiast breeding repository
    @Binds
    @Singleton
    abstract fun bindEnthusiastBreedingRepository(impl: EnthusiastBreedingRepositoryImpl): EnthusiastBreedingRepository

    // Sprint 1 repositories
    @Binds
    @Singleton
    abstract fun bindDailyLogRepository(impl: DailyLogRepositoryImpl): DailyLogRepository

    @Binds
    @Singleton
    abstract fun bindTaskRepository(impl: TaskRepositoryImpl): TaskRepository

    @Binds
    @Singleton
    abstract fun bindBreedRepository(impl: com.rio.rostry.data.repository.BreedRepositoryImpl): com.rio.rostry.data.repository.BreedRepository

    @Binds
    @Singleton
    abstract fun bindLikesRepository(impl: LikesRepositoryImpl): LikesRepository

    @Binds
    @Singleton
    abstract fun bindFeedbackRepository(impl: com.rio.rostry.data.repository.FeedbackRepositoryImpl): com.rio.rostry.data.repository.FeedbackRepository

    @Binds
    @Singleton
    abstract fun bindFarmVerificationRepository(impl: com.rio.rostry.data.repository.FarmVerificationRepositoryImpl): com.rio.rostry.data.repository.FarmVerificationRepository

    @Binds
    @Singleton
    abstract fun bindEnthusiastVerificationRepository(impl: com.rio.rostry.data.repository.EnthusiastVerificationRepositoryImpl): com.rio.rostry.data.repository.EnthusiastVerificationRepository

    @Binds
    @Singleton
    abstract fun bindFarmAssetRepository(impl: com.rio.rostry.data.repository.FarmAssetRepositoryImpl): com.rio.rostry.data.repository.FarmAssetRepository

    @Binds
    @Singleton
    abstract fun bindInventoryRepository(impl: com.rio.rostry.data.repository.InventoryRepositoryImpl): com.rio.rostry.data.repository.InventoryRepository

    @Binds
    @Singleton
    abstract fun bindMarketListingRepository(impl: com.rio.rostry.data.repository.MarketListingRepositoryImpl): com.rio.rostry.data.repository.MarketListingRepository

    // Reviews & Ratings
    @Binds
    @Singleton
    abstract fun bindReviewRepository(impl: com.rio.rostry.data.repository.ReviewRepositoryImpl): com.rio.rostry.data.repository.ReviewRepository

    // Evidence-Based Order System
    @Binds
    @Singleton
    abstract fun bindEvidenceOrderRepository(impl: com.rio.rostry.data.repository.EvidenceOrderRepositoryImpl): com.rio.rostry.data.repository.EvidenceOrderRepository

    // Enthusiast Pedigree Repository
    @Binds
    @Singleton
    abstract fun bindPedigreeRepository(impl: PedigreeRepositoryImpl): PedigreeRepository

    @Binds
    @Singleton
    abstract fun bindShowRecordRepository(impl: com.rio.rostry.data.repository.ShowRecordRepositoryImpl): com.rio.rostry.data.repository.ShowRecordRepository

    // Farm Activity Log Repository (expenses, sanitation, etc.)
    @Binds
    @Singleton
    abstract fun bindFarmActivityLogRepository(impl: com.rio.rostry.data.repository.FarmActivityLogRepositoryImpl): com.rio.rostry.data.repository.FarmActivityLogRepository

    // Public Bird Access Repository
    @Binds
    @Singleton
    abstract fun bindPublicBirdRepository(impl: com.rio.rostry.data.repository.PublicBirdRepositoryImpl): com.rio.rostry.domain.repository.PublicBirdRepository

    @Binds
    @Singleton
    abstract fun bindBiosecurityRepository(impl: com.rio.rostry.data.repository.BiosecurityRepositoryImpl): com.rio.rostry.data.repository.BiosecurityRepository

    @Binds
    @Singleton
    abstract fun bindAdminMortalityRepository(impl: com.rio.rostry.data.repository.admin.AdminMortalityRepositoryImpl): com.rio.rostry.data.repository.admin.AdminMortalityRepository

    @Binds
    @Singleton
    abstract fun bindDisputeRepository(impl: com.rio.rostry.data.repository.DisputeRepositoryImpl): com.rio.rostry.data.repository.DisputeRepository

    @Binds
    @Singleton
    abstract fun bindAuditRepository(impl: com.rio.rostry.data.repository.AuditRepositoryImpl): com.rio.rostry.data.repository.AuditRepository

    @Binds
    @Singleton
    abstract fun bindRoleUpgradeRequestRepository(impl: com.rio.rostry.data.repository.RoleUpgradeRequestRepositoryImpl): com.rio.rostry.data.repository.RoleUpgradeRequestRepository

    @Binds
    @Singleton
    abstract fun bindRoleMigrationRepository(impl: com.rio.rostry.data.repository.RoleMigrationRepositoryImpl): com.rio.rostry.data.repository.RoleMigrationRepository

    @Binds
    @Singleton
    abstract fun bindAdminRepository(impl: com.rio.rostry.data.repository.AdminRepositoryImpl): com.rio.rostry.data.repository.AdminRepository
}

@Module
@InstallIn(SingletonComponent::class)
object PaymentModule {
    @Provides
    @Singleton
    fun providePaymentGateway(): PaymentGateway = DefaultPaymentGateway()
}