package com.rio.rostry.di

import android.content.Context
import androidx.room.Room
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
import com.rio.rostry.data.database.dao.OutgoingMessageDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        // Derive a passphrase for SQLCipher; in production, store/retrieve securely
        val passphrase: ByteArray = SQLiteDatabase.getBytes("rostry-db-passphrase".toCharArray())
        val factory = SupportFactory(passphrase)

        return Room.databaseBuilder(
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
            AppDatabase.MIGRATION_11_12
        )
        .fallbackToDestructiveMigration()
        .build()
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
    fun provideOutgoingMessageDao(db: AppDatabase): OutgoingMessageDao = db.outgoingMessageDao()
}
