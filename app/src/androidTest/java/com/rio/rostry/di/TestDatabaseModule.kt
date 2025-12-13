package com.rio.rostry.di

import com.rio.rostry.data.database.AppDatabase
import com.rio.rostry.data.database.dao.*
import com.rio.rostry.data.database.dao.BreedDao
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [DatabaseModule::class]
)
object TestDatabaseExtraModule {

    // AppDatabase is provided by TestSyncModule.provideInMemoryDb

    @Provides @Singleton fun provideCoinDao(db: AppDatabase): CoinDao = db.coinDao()
    @Provides @Singleton fun provideNotificationDao(db: AppDatabase): NotificationDao = db.notificationDao()
    @Provides @Singleton fun provideProductTrackingDao(db: AppDatabase): ProductTrackingDao = db.productTrackingDao()
    @Provides @Singleton fun provideFamilyTreeDao(db: AppDatabase): FamilyTreeDao = db.familyTreeDao()
    @Provides @Singleton fun provideChatMessageDao(db: AppDatabase): ChatMessageDao = db.chatMessageDao()
    @Provides @Singleton fun provideSyncStateDao(db: AppDatabase): SyncStateDao = db.syncStateDao()
    @Provides @Singleton fun provideAuctionDao(db: AppDatabase): AuctionDao = db.auctionDao()
    @Provides @Singleton fun provideBidDao(db: AppDatabase): BidDao = db.bidDao()
    @Provides @Singleton fun provideCartDao(db: AppDatabase): CartDao = db.cartDao()
    @Provides @Singleton fun provideWishlistDao(db: AppDatabase): WishlistDao = db.wishlistDao()
    @Provides @Singleton fun providePaymentDao(db: AppDatabase): PaymentDao = db.paymentDao()
    @Provides @Singleton fun provideCoinLedgerDao(db: AppDatabase): CoinLedgerDao = db.coinLedgerDao()
    @Provides @Singleton fun provideDeliveryHubDao(db: AppDatabase): DeliveryHubDao = db.deliveryHubDao()
    @Provides @Singleton fun provideOrderTrackingEventDao(db: AppDatabase): OrderTrackingEventDao = db.orderTrackingEventDao()
    @Provides @Singleton fun provideInvoiceDao(db: AppDatabase): InvoiceDao = db.invoiceDao()
    @Provides @Singleton fun provideRefundDao(db: AppDatabase): RefundDao = db.refundDao()
    @Provides @Singleton fun provideBreedingRecordDao(db: AppDatabase): BreedingRecordDao = db.breedingRecordDao()
    @Provides @Singleton fun provideTraitDao(db: AppDatabase): TraitDao = db.traitDao()
    @Provides @Singleton fun provideProductTraitDao(db: AppDatabase): ProductTraitDao = db.productTraitDao()
    @Provides @Singleton fun provideLifecycleEventDao(db: AppDatabase): LifecycleEventDao = db.lifecycleEventDao()
    @Provides @Singleton fun provideTransferVerificationDao(db: AppDatabase): TransferVerificationDao = db.transferVerificationDao()
    @Provides @Singleton fun provideDisputeDao(db: AppDatabase): DisputeDao = db.disputeDao()
    @Provides @Singleton fun provideAuditLogDao(db: AppDatabase): AuditLogDao = db.auditLogDao()
    @Provides @Singleton fun provideBreedDao(db: AppDatabase): BreedDao = db.breedDao()

    // Farm monitoring
    @Provides @Singleton fun provideGrowthRecordDao(db: AppDatabase): GrowthRecordDao = db.growthRecordDao()
    @Provides @Singleton fun provideQuarantineRecordDao(db: AppDatabase): QuarantineRecordDao = db.quarantineRecordDao()
    @Provides @Singleton fun provideMortalityRecordDao(db: AppDatabase): MortalityRecordDao = db.mortalityRecordDao()
    @Provides @Singleton fun provideVaccinationRecordDao(db: AppDatabase): VaccinationRecordDao = db.vaccinationRecordDao()
    // HatchingBatchDao, HatchingLogDao, BreedingPairDao are already provided in TestSyncModule
    @Provides @Singleton fun provideFarmAlertDao(db: AppDatabase): FarmAlertDao = db.farmAlertDao()
    @Provides @Singleton fun provideListingDraftDao(db: AppDatabase): ListingDraftDao = db.listingDraftDao()
    @Provides @Singleton fun provideFarmerDashboardSnapshotDao(db: AppDatabase): FarmerDashboardSnapshotDao = db.farmerDashboardSnapshotDao()

    // Enthusiast breeding
    @Provides @Singleton fun provideMatingLogDao(db: AppDatabase): MatingLogDao = db.matingLogDao()
    // EggCollectionDao and BreedingPairDao are provided in TestSyncModule
    @Provides @Singleton fun provideEnthusiastDashboardSnapshotDao(db: AppDatabase): EnthusiastDashboardSnapshotDao = db.enthusiastDashboardSnapshotDao()

    // Social
    @Provides @Singleton fun providePostsDao(db: AppDatabase): PostsDao = db.postsDao()
    @Provides @Singleton fun provideCommentsDao(db: AppDatabase): CommentsDao = db.commentsDao()
    @Provides @Singleton fun provideLikesDao(db: AppDatabase): LikesDao = db.likesDao()
    @Provides @Singleton fun provideFollowsDao(db: AppDatabase): FollowsDao = db.followsDao()
    @Provides @Singleton fun provideGroupsDao(db: AppDatabase): GroupsDao = db.groupsDao()
    @Provides @Singleton fun provideGroupMembersDao(db: AppDatabase): GroupMembersDao = db.groupMembersDao()
    @Provides @Singleton fun provideEventsDao(db: AppDatabase): EventsDao = db.eventsDao()
    @Provides @Singleton fun provideExpertBookingsDao(db: AppDatabase): ExpertBookingsDao = db.expertBookingsDao()
    @Provides @Singleton fun provideModerationReportsDao(db: AppDatabase): ModerationReportsDao = db.moderationReportsDao()
    @Provides @Singleton fun provideBadgesDao(db: AppDatabase): BadgesDao = db.badgesDao()
    @Provides @Singleton fun provideReputationDao(db: AppDatabase): ReputationDao = db.reputationDao()
    @Provides @Singleton fun provideStoriesDao(db: AppDatabase): StoriesDao = db.storiesDao()


    // Gamification
    @Provides @Singleton fun provideUserProgressDao(db: AppDatabase): UserProgressDao = db.userProgressDao()
    @Provides @Singleton fun provideLeaderboardDao(db: AppDatabase): LeaderboardDao = db.leaderboardDao()

    // Messaging / misc
    @Provides @Singleton fun provideOutgoingMessageDao(db: AppDatabase): OutgoingMessageDao = db.outgoingMessageDao()
    @Provides @Singleton fun provideRateLimitDao(db: AppDatabase): RateLimitDao = db.rateLimitDao()
    @Provides @Singleton fun provideEventRsvpsDao(db: AppDatabase): EventRsvpsDao = db.eventRsvpsDao()
    @Provides @Singleton fun provideAnalyticsDao(db: AppDatabase): AnalyticsDao = db.analyticsDao()
    @Provides @Singleton fun provideReportsDao(db: AppDatabase): ReportsDao = db.reportsDao()
    @Provides @Singleton fun provideThreadMetadataDao(db: AppDatabase): ThreadMetadataDao = db.threadMetadataDao()
    @Provides @Singleton fun provideCommunityRecommendationDao(db: AppDatabase): CommunityRecommendationDao = db.communityRecommendationDao()
    @Provides @Singleton fun provideUserInterestDao(db: AppDatabase): UserInterestDao = db.userInterestDao()
    @Provides @Singleton fun provideExpertProfileDao(db: AppDatabase): ExpertProfileDao = db.expertProfileDao()
}
