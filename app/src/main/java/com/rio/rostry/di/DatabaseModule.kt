package com.rio.rostry.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.rio.rostry.data.database.AppDatabase
import com.rio.rostry.data.database.dao.CoinDao
import com.rio.rostry.data.database.dao.NotificationDao
import com.rio.rostry.data.database.dao.OrderDao
import com.rio.rostry.data.database.dao.ProductDao
import com.rio.rostry.data.database.dao.AuctionDao
import com.rio.rostry.data.database.dao.BidDao
import com.rio.rostry.data.database.dao.CartDao
import com.rio.rostry.data.database.dao.WishlistDao
import com.rio.rostry.data.database.dao.PaymentDao
import com.rio.rostry.data.database.dao.CoinLedgerDao
import com.rio.rostry.data.database.dao.DeliveryHubDao
import com.rio.rostry.data.database.dao.OrderTrackingEventDao
import com.rio.rostry.data.database.dao.InvoiceDao
import com.rio.rostry.data.database.dao.RefundDao
import com.rio.rostry.data.database.dao.TransferDao
import com.rio.rostry.data.database.dao.UserDao
import com.rio.rostry.data.database.dao.ProductTrackingDao
import com.rio.rostry.data.database.dao.FamilyTreeDao
import com.rio.rostry.data.database.dao.ChatMessageDao
import com.rio.rostry.data.database.dao.SyncStateDao
import com.rio.rostry.data.database.dao.FarmVerificationDao
import com.rio.rostry.data.database.dao.BreedingRecordDao
import com.rio.rostry.data.database.dao.TraitDao
import com.rio.rostry.data.database.dao.ProductTraitDao
import com.rio.rostry.data.database.dao.LifecycleEventDao
import com.rio.rostry.data.database.dao.TransferVerificationDao
import com.rio.rostry.data.database.dao.DisputeDao
import com.rio.rostry.data.database.dao.AuditLogDao
import com.rio.rostry.data.database.dao.PostsDao
import com.rio.rostry.data.database.dao.CommentsDao
import com.rio.rostry.data.database.dao.LikesDao
import com.rio.rostry.data.database.dao.FollowsDao
import com.rio.rostry.data.database.dao.GroupsDao
import com.rio.rostry.data.database.dao.GroupMembersDao
import com.rio.rostry.data.database.dao.EventsDao
import com.rio.rostry.data.database.dao.ExpertBookingsDao
import com.rio.rostry.data.database.dao.ModerationReportsDao
import com.rio.rostry.data.database.dao.BadgesDao
import com.rio.rostry.data.database.dao.ReputationDao
import com.rio.rostry.data.database.dao.StoriesDao
import com.rio.rostry.data.database.dao.OutgoingMessageDao
import com.rio.rostry.data.database.dao.RateLimitDao
import com.rio.rostry.data.database.dao.EventRsvpsDao
import com.rio.rostry.data.database.dao.AnalyticsDao
import com.rio.rostry.data.database.dao.ReportsDao
import com.rio.rostry.data.database.dao.OutboxDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory
import com.rio.rostry.BuildConfig

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        // Derive a passphrase for SQLCipher; in production, store/retrieve securely
        val passphrase: ByteArray = SQLiteDatabase.getBytes("rostry-db-passphrase".toCharArray())
        val factory = SupportFactory(passphrase)

        val builder = Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME
        )
        .openHelperFactory(factory)
        .addMigrations(
            AppDatabase.MIGRATION_2_3,
            AppDatabase.MIGRATION_3_4,
            AppDatabase.MIGRATION_4_5,
            AppDatabase.MIGRATION_5_6,
            AppDatabase.MIGRATION_6_7,
            AppDatabase.MIGRATION_7_8,
            AppDatabase.MIGRATION_8_9,
            AppDatabase.MIGRATION_9_10,
            AppDatabase.MIGRATION_10_11,
            AppDatabase.MIGRATION_11_12,
            AppDatabase.MIGRATION_12_13,
            AppDatabase.MIGRATION_13_14,
            AppDatabase.MIGRATION_14_15,
            AppDatabase.MIGRATION_15_16,
            AppDatabase.MIGRATION_16_17,
            AppDatabase.MIGRATION_17_18,
            AppDatabase.MIGRATION_18_19,
            AppDatabase.MIGRATION_19_20,
            AppDatabase.MIGRATION_20_21,
            AppDatabase.MIGRATION_21_22,
            AppDatabase.MIGRATION_22_23,
            AppDatabase.MIGRATION_23_24,
            AppDatabase.MIGRATION_24_25,
            AppDatabase.MIGRATION_25_26,
            AppDatabase.MIGRATION_26_27,
            AppDatabase.MIGRATION_27_28,
            AppDatabase.MIGRATION_28_29,
            AppDatabase.MIGRATION_29_30,
            AppDatabase.MIGRATION_30_31,
            AppDatabase.MIGRATION_31_32,
            AppDatabase.MIGRATION_32_33,
            AppDatabase.MIGRATION_33_34,
            AppDatabase.MIGRATION_34_35,
            AppDatabase.MIGRATION_35_36,
            AppDatabase.MIGRATION_36_37,
            AppDatabase.MIGRATION_37_38,
            AppDatabase.MIGRATION_38_39,
            AppDatabase.MIGRATION_39_40,
            AppDatabase.MIGRATION_40_41,
            AppDatabase.MIGRATION_41_42,
            AppDatabase.MIGRATION_42_43,
            AppDatabase.MIGRATION_43_44,
            AppDatabase.MIGRATION_44_45,
            AppDatabase.MIGRATION_45_46,
            AppDatabase.MIGRATION_46_47,
            AppDatabase.MIGRATION_47_48,
            AppDatabase.MIGRATION_48_49,
            AppDatabase.MIGRATION_49_50,
            AppDatabase.MIGRATION_50_51,
            AppDatabase.MIGRATION_51_52,
            AppDatabase.MIGRATION_52_53,
            AppDatabase.MIGRATION_53_54
        )

        // Optional: allow destructive migrations only for debug builds
        if (BuildConfig.DEBUG) {
            builder.fallbackToDestructiveMigration()
        } else {
            // In production, at least handle downgrades gracefully
            builder.fallbackToDestructiveMigrationOnDowngrade()
        }
        
        // Add migration callback for debugging
        builder.addCallback(object : RoomDatabase.Callback() {
            override fun onOpen(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                super.onOpen(db)
                android.util.Log.d("DatabaseModule", "Database opened. Version: ${db.version}")
            }
        })

        return builder.build()
    }

    @Provides
    @Singleton
    fun provideUserDao(appDatabase: AppDatabase): UserDao {
        return appDatabase.userDao()
    }

    @Provides
    @Singleton
    fun provideProductDao(appDatabase: AppDatabase): ProductDao {
        return appDatabase.productDao()
    }

    @Provides
    @Singleton
    fun provideOrderDao(appDatabase: AppDatabase): OrderDao {
        return appDatabase.orderDao()
    }

    @Provides
    @Singleton
    fun provideTransferDao(appDatabase: AppDatabase): TransferDao {
        return appDatabase.transferDao()
    }

    @Provides
    @Singleton
    fun provideCoinDao(appDatabase: AppDatabase): CoinDao {
        return appDatabase.coinDao()
    }

    @Provides
    @Singleton
    fun provideNotificationDao(appDatabase: AppDatabase): NotificationDao {
        return appDatabase.notificationDao()
    }

    @Provides
    @Singleton
    fun provideProductTrackingDao(appDatabase: AppDatabase): ProductTrackingDao = appDatabase.productTrackingDao()

    @Provides
    @Singleton
    fun provideFamilyTreeDao(appDatabase: AppDatabase): FamilyTreeDao = appDatabase.familyTreeDao()

    @Provides
    @Singleton
    fun provideChatMessageDao(appDatabase: AppDatabase): ChatMessageDao = appDatabase.chatMessageDao()

    @Provides
    @Singleton
    fun provideSyncStateDao(appDatabase: AppDatabase): SyncStateDao = appDatabase.syncStateDao()

    @Provides
    @Singleton
    fun provideFarmVerificationDao(appDatabase: AppDatabase): FarmVerificationDao = appDatabase.farmVerificationDao()

    @Provides
    @Singleton
    fun provideAuctionDao(appDatabase: AppDatabase): AuctionDao = appDatabase.auctionDao()

    @Provides
    @Singleton
    fun provideBidDao(appDatabase: AppDatabase): BidDao = appDatabase.bidDao()

    @Provides
    @Singleton
    fun provideCartDao(appDatabase: AppDatabase): CartDao = appDatabase.cartDao()

    @Provides
    @Singleton
    fun provideWishlistDao(appDatabase: AppDatabase): WishlistDao = appDatabase.wishlistDao()

    @Provides
    @Singleton
    fun providePaymentDao(appDatabase: AppDatabase): PaymentDao = appDatabase.paymentDao()

    @Provides
    @Singleton
    fun provideCoinLedgerDao(appDatabase: AppDatabase): CoinLedgerDao = appDatabase.coinLedgerDao()

    @Provides
    @Singleton
    fun provideDeliveryHubDao(appDatabase: AppDatabase): DeliveryHubDao = appDatabase.deliveryHubDao()

    @Provides
    @Singleton
    fun provideOrderTrackingEventDao(appDatabase: AppDatabase): OrderTrackingEventDao = appDatabase.orderTrackingEventDao()

    @Provides
    @Singleton
    fun provideInvoiceDao(appDatabase: AppDatabase): InvoiceDao = appDatabase.invoiceDao()

    @Provides
    @Singleton
    fun provideRefundDao(appDatabase: AppDatabase): RefundDao = appDatabase.refundDao()

    @Provides
    @Singleton
    fun provideBreedingRecordDao(appDatabase: AppDatabase): BreedingRecordDao = appDatabase.breedingRecordDao()

    @Provides
    @Singleton
    fun provideTraitDao(appDatabase: AppDatabase): TraitDao = appDatabase.traitDao()

    @Provides
    @Singleton
    fun provideProductTraitDao(appDatabase: AppDatabase): ProductTraitDao = appDatabase.productTraitDao()

    @Provides
    @Singleton
    fun provideLifecycleEventDao(appDatabase: AppDatabase): LifecycleEventDao = appDatabase.lifecycleEventDao()

    @Provides
    @Singleton
    fun provideTransferVerificationDao(appDatabase: AppDatabase): TransferVerificationDao = appDatabase.transferVerificationDao()

    @Provides
    @Singleton
    fun provideDisputeDao(appDatabase: AppDatabase): DisputeDao = appDatabase.disputeDao()

    @Provides
    @Singleton
    fun provideAuditLogDao(appDatabase: AppDatabase): AuditLogDao = appDatabase.auditLogDao()

    // Farm monitoring DAOs
    @Provides
    @Singleton
    fun provideGrowthRecordDao(db: AppDatabase): com.rio.rostry.data.database.dao.GrowthRecordDao = db.growthRecordDao()

    @Provides
    @Singleton
    fun provideQuarantineRecordDao(db: AppDatabase): com.rio.rostry.data.database.dao.QuarantineRecordDao = db.quarantineRecordDao()

    @Provides
    @Singleton
    fun provideMortalityRecordDao(db: AppDatabase): com.rio.rostry.data.database.dao.MortalityRecordDao = db.mortalityRecordDao()

    @Provides
    @Singleton
    fun provideVaccinationRecordDao(db: AppDatabase): com.rio.rostry.data.database.dao.VaccinationRecordDao = db.vaccinationRecordDao()

    @Provides
    @Singleton
    fun provideHatchingBatchDao(db: AppDatabase): com.rio.rostry.data.database.dao.HatchingBatchDao = db.hatchingBatchDao()

    @Provides
    @Singleton
    fun provideHatchingLogDao(db: AppDatabase): com.rio.rostry.data.database.dao.HatchingLogDao = db.hatchingLogDao()

    // New farm monitoring DAOs
    @Provides
    @Singleton
    fun provideBreedingPairDao(db: AppDatabase): com.rio.rostry.data.database.dao.BreedingPairDao = db.breedingPairDao()

    @Provides
    @Singleton
    fun provideLeaderboardDao(appDatabase: AppDatabase): com.rio.rostry.data.database.dao.LeaderboardDao = appDatabase.leaderboardDao()

    @Provides
    @Singleton
    fun provideFarmAlertDao(db: AppDatabase): com.rio.rostry.data.database.dao.FarmAlertDao = db.farmAlertDao()

    @Provides
    @Singleton
    fun provideListingDraftDao(db: AppDatabase): com.rio.rostry.data.database.dao.ListingDraftDao = db.listingDraftDao()

    @Provides
    @Singleton
    fun provideFarmerDashboardSnapshotDao(db: AppDatabase): com.rio.rostry.data.database.dao.FarmerDashboardSnapshotDao = db.farmerDashboardSnapshotDao()

    // Enthusiast breeding DAOs
    @Provides
    @Singleton
    fun provideMatingLogDao(db: AppDatabase): com.rio.rostry.data.database.dao.MatingLogDao = db.matingLogDao()

    @Provides
    @Singleton
    fun provideEggCollectionDao(db: AppDatabase): com.rio.rostry.data.database.dao.EggCollectionDao = db.eggCollectionDao()

    @Provides
    @Singleton
    fun provideEnthusiastDashboardSnapshotDao(db: AppDatabase): com.rio.rostry.data.database.dao.EnthusiastDashboardSnapshotDao = db.enthusiastDashboardSnapshotDao()

    // Social DAOs
    @Provides
    @Singleton
    fun providePostsDao(db: AppDatabase): PostsDao = db.postsDao()

    @Provides
    @Singleton
    fun provideCommentsDao(db: AppDatabase): CommentsDao = db.commentsDao()

    @Provides
    @Singleton
    fun provideLikesDao(db: AppDatabase): LikesDao = db.likesDao()

    @Provides
    @Singleton
    fun provideFollowsDao(db: AppDatabase): FollowsDao = db.followsDao()

    @Provides
    @Singleton
    fun provideGroupsDao(db: AppDatabase): GroupsDao = db.groupsDao()

    @Provides
    @Singleton
    fun provideGroupMembersDao(db: AppDatabase): GroupMembersDao = db.groupMembersDao()

    @Provides
    @Singleton
    fun provideEventsDao(db: AppDatabase): EventsDao = db.eventsDao()

    @Provides
    @Singleton
    fun provideExpertBookingsDao(db: AppDatabase): ExpertBookingsDao = db.expertBookingsDao()

    @Provides
    @Singleton
    fun provideModerationReportsDao(db: AppDatabase): ModerationReportsDao = db.moderationReportsDao()

    @Provides
    @Singleton
    fun provideBadgesDao(db: AppDatabase): BadgesDao = db.badgesDao()

    @Provides
    @Singleton
    fun provideReputationDao(db: AppDatabase): ReputationDao = db.reputationDao()

    @Provides
    @Singleton
    fun provideStoriesDao(db: AppDatabase): StoriesDao = db.storiesDao()

    @Provides
    @Singleton
    fun provideOutgoingMessageDao(db: AppDatabase): OutgoingMessageDao = db.outgoingMessageDao()

    @Provides
    @Singleton
    fun provideRateLimitDao(db: AppDatabase): RateLimitDao = db.rateLimitDao()

    @Provides
    @Singleton
    fun provideEventRsvpsDao(db: AppDatabase): EventRsvpsDao = db.eventRsvpsDao()

    @Provides
    @Singleton
    fun provideAnalyticsDao(db: AppDatabase): AnalyticsDao = db.analyticsDao()

    @Provides
    @Singleton
    fun provideReportsDao(db: AppDatabase): ReportsDao = db.reportsDao()

    // Community Engagement DAOs
    @Provides
    @Singleton
    fun provideThreadMetadataDao(db: AppDatabase): com.rio.rostry.data.database.dao.ThreadMetadataDao = db.threadMetadataDao()

    @Provides
    @Singleton
    fun provideCommunityRecommendationDao(db: AppDatabase): com.rio.rostry.data.database.dao.CommunityRecommendationDao = db.communityRecommendationDao()

    @Provides
    @Singleton
    fun provideUserInterestDao(db: AppDatabase): com.rio.rostry.data.database.dao.UserInterestDao = db.userInterestDao()

    @Provides
    @Singleton
    fun provideExpertProfileDao(db: AppDatabase): com.rio.rostry.data.database.dao.ExpertProfileDao = db.expertProfileDao()

    @Provides
    @Singleton
    fun provideUserProgressDao(appDatabase: AppDatabase): com.rio.rostry.data.database.dao.UserProgressDao = appDatabase.userProgressDao()

    @Provides
    @Singleton
    fun provideOutboxDao(db: AppDatabase): OutboxDao = db.outboxDao()

    @Provides
    @Singleton
    fun provideUploadTaskDao(db: AppDatabase): com.rio.rostry.data.database.dao.UploadTaskDao = db.uploadTaskDao()

    // Sprint 1 DAOs
    @Provides
    @Singleton
    fun provideDailyLogDao(db: AppDatabase): com.rio.rostry.data.database.dao.DailyLogDao = db.dailyLogDao()

    @Provides
    @Singleton
    fun provideTaskDao(db: AppDatabase): com.rio.rostry.data.database.dao.TaskDao = db.taskDao()

    @Provides
    @Singleton
    fun provideBreedDao(db: AppDatabase): com.rio.rostry.data.database.dao.BreedDao = db.breedDao()

    @Provides
    @Singleton
    fun provideFarmAssetDao(db: AppDatabase): com.rio.rostry.data.database.dao.FarmAssetDao = db.farmAssetDao()

    @Provides
    @Singleton
    fun provideInventoryItemDao(db: AppDatabase): com.rio.rostry.data.database.dao.InventoryItemDao = db.inventoryItemDao()

    @Provides
    @Singleton
    fun provideMarketListingDao(db: AppDatabase): com.rio.rostry.data.database.dao.MarketListingDao = db.marketListingDao()

    // Evidence-Based Order System DAOs
    @Provides
    @Singleton
    fun provideOrderEvidenceDao(db: AppDatabase): com.rio.rostry.data.database.dao.OrderEvidenceDao = db.orderEvidenceDao()

    @Provides
    @Singleton
    fun provideOrderQuoteDao(db: AppDatabase): com.rio.rostry.data.database.dao.OrderQuoteDao = db.orderQuoteDao()

    @Provides
    @Singleton
    fun provideOrderPaymentDao(db: AppDatabase): com.rio.rostry.data.database.dao.OrderPaymentDao = db.orderPaymentDao()

    @Provides
    @Singleton
    fun provideDeliveryConfirmationDao(db: AppDatabase): com.rio.rostry.data.database.dao.DeliveryConfirmationDao = db.deliveryConfirmationDao()

    @Provides
    @Singleton
    fun provideOrderDisputeDao(db: AppDatabase): com.rio.rostry.data.database.dao.OrderDisputeDao = db.orderDisputeDao()

    @Provides
    @Singleton
    fun provideOrderAuditLogDao(db: AppDatabase): com.rio.rostry.data.database.dao.OrderAuditLogDao = db.orderAuditLogDao()
}
