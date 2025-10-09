package com.rio.rostry.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.rio.rostry.data.database.dao.*
import com.rio.rostry.data.database.entity.*
import com.rio.rostry.domain.model.UserType
import com.rio.rostry.domain.model.VerificationStatus
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

    @Database(
    entities = [
        UserEntity::class,
        ProductEntity::class,
        OrderEntity::class,
        OrderItemEntity::class,
        TransferEntity::class,
        CoinEntity::class,
        NotificationEntity::class,
        ProductTrackingEntity::class,
        FamilyTreeEntity::class,
        ChatMessageEntity::class,
        SyncStateEntity::class,
        AuctionEntity::class,
        BidEntity::class,
        CartItemEntity::class,
        WishlistEntity::class,
        PaymentEntity::class,
        CoinLedgerEntity::class,
        DeliveryHubEntity::class,
        OrderTrackingEventEntity::class,
        InvoiceEntity::class,
        InvoiceLineEntity::class,
        RefundEntity::class,
        BreedingRecordEntity::class,
        TraitEntity::class,
        ProductTraitCrossRef::class,
        LifecycleEventEntity::class,
        TransferVerificationEntity::class,
        DisputeEntity::class,
        AuditLogEntity::class,
        PostEntity::class,
        CommentEntity::class,
        LikeEntity::class,
        FollowEntity::class,
        GroupEntity::class,
        GroupMemberEntity::class,
        EventEntity::class,
        ExpertBookingEntity::class,
        ModerationReportEntity::class,
        BadgeEntity::class,
        ReputationEntity::class,
        OutgoingMessageEntity::class,
        RateLimitEntity::class,
        EventRsvpEntity::class,
        AnalyticsDailyEntity::class,
        ReportEntity::class,
        // Farm monitoring entities
        GrowthRecordEntity::class,
        QuarantineRecordEntity::class,
        MortalityRecordEntity::class,
        VaccinationRecordEntity::class,
        HatchingBatchEntity::class,
        HatchingLogEntity::class,
        // Gamification & Loveable product entities
        AchievementEntity::class,
        UserProgressEntity::class,
        GamificationBadgeEntity::class,
        LeaderboardEntity::class,
        RewardEntity::class,
        // Community entities
        ThreadMetadataEntity::class,
        CommunityRecommendationEntity::class,
        UserInterestEntity::class,
        ExpertProfileEntity::class,
        // Outbox pattern for offline-first
        OutboxEntity::class,
        // New farm monitoring entities
        BreedingPairEntity::class,
        FarmAlertEntity::class,
        ListingDraftEntity::class,
        FarmerDashboardSnapshotEntity::class,
        // Enthusiast-specific entities
        MatingLogEntity::class,
        EggCollectionEntity::class,
        EnthusiastDashboardSnapshotEntity::class,
        UploadTaskEntity::class,
        // New Sprint 1 entities
        DailyLogEntity::class,
        TaskEntity::class
    ],
    version = 31, // 31 adds statusHistoryJson to quarantine_records; 30 added UNIQUE(productId, logDate) on daily_logs
    exportSchema = false // Set to true if you want to export schema to a folder for version control.
)
@TypeConverters(AppDatabase.Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun productDao(): ProductDao
    abstract fun orderDao(): OrderDao
    // OrderItemDao is not strictly needed as its operations are usually through OrderDao or ProductDao relationships
    // but you can create one if direct access to OrderItemEntity is frequently required.
    abstract fun transferDao(): TransferDao
    abstract fun coinDao(): CoinDao
    abstract fun notificationDao(): NotificationDao
    abstract fun productTrackingDao(): ProductTrackingDao
    abstract fun familyTreeDao(): FamilyTreeDao
    abstract fun chatMessageDao(): ChatMessageDao
    abstract fun syncStateDao(): SyncStateDao
    abstract fun auctionDao(): AuctionDao
    abstract fun bidDao(): BidDao
    abstract fun cartDao(): CartDao
    abstract fun wishlistDao(): WishlistDao
    abstract fun paymentDao(): PaymentDao
    abstract fun coinLedgerDao(): CoinLedgerDao
    abstract fun deliveryHubDao(): DeliveryHubDao
    abstract fun orderTrackingEventDao(): OrderTrackingEventDao
    abstract fun invoiceDao(): InvoiceDao
    abstract fun refundDao(): RefundDao
    abstract fun breedingRecordDao(): BreedingRecordDao
    abstract fun traitDao(): TraitDao
    abstract fun productTraitDao(): ProductTraitDao
    abstract fun lifecycleEventDao(): LifecycleEventDao
    abstract fun transferVerificationDao(): TransferVerificationDao
    abstract fun disputeDao(): DisputeDao
    abstract fun auditLogDao(): AuditLogDao

    // Farm monitoring DAOs
    abstract fun growthRecordDao(): com.rio.rostry.data.database.dao.GrowthRecordDao
    abstract fun quarantineRecordDao(): com.rio.rostry.data.database.dao.QuarantineRecordDao
    abstract fun mortalityRecordDao(): com.rio.rostry.data.database.dao.MortalityRecordDao
    abstract fun vaccinationRecordDao(): com.rio.rostry.data.database.dao.VaccinationRecordDao
    abstract fun hatchingBatchDao(): com.rio.rostry.data.database.dao.HatchingBatchDao
    abstract fun hatchingLogDao(): com.rio.rostry.data.database.dao.HatchingLogDao

    // Social DAOs
    abstract fun postsDao(): PostsDao
    abstract fun commentsDao(): CommentsDao
    abstract fun likesDao(): LikesDao
    abstract fun followsDao(): FollowsDao
    abstract fun groupsDao(): GroupsDao
    abstract fun groupMembersDao(): GroupMembersDao
    abstract fun eventsDao(): EventsDao
    abstract fun expertBookingsDao(): ExpertBookingsDao
    abstract fun moderationReportsDao(): ModerationReportsDao
    abstract fun badgesDao(): BadgesDao
    abstract fun reputationDao(): ReputationDao
    abstract fun outgoingMessageDao(): OutgoingMessageDao
    abstract fun rateLimitDao(): RateLimitDao
    abstract fun eventRsvpsDao(): EventRsvpsDao
    abstract fun analyticsDao(): AnalyticsDao
    abstract fun reportsDao(): ReportsDao

    // Gamification DAOs
    abstract fun achievementsDefDao(): com.rio.rostry.data.database.dao.AchievementDao
    abstract fun userProgressDao(): com.rio.rostry.data.database.dao.UserProgressDao
    abstract fun badgesDefDao(): com.rio.rostry.data.database.dao.BadgeDefDao
    abstract fun leaderboardDao(): com.rio.rostry.data.database.dao.LeaderboardDao
    abstract fun rewardsDefDao(): com.rio.rostry.data.database.dao.RewardDefDao

    // Community DAOs
    abstract fun threadMetadataDao(): ThreadMetadataDao
    abstract fun communityRecommendationDao(): CommunityRecommendationDao
    abstract fun userInterestDao(): UserInterestDao
    abstract fun expertProfileDao(): ExpertProfileDao
    abstract fun outboxDao(): OutboxDao

    // New farm monitoring DAOs
    abstract fun breedingPairDao(): BreedingPairDao
    abstract fun farmAlertDao(): FarmAlertDao
    abstract fun listingDraftDao(): ListingDraftDao
    abstract fun farmerDashboardSnapshotDao(): FarmerDashboardSnapshotDao
    // Enthusiast breeding DAOs
    abstract fun matingLogDao(): MatingLogDao
    abstract fun eggCollectionDao(): EggCollectionDao
    abstract fun enthusiastDashboardSnapshotDao(): EnthusiastDashboardSnapshotDao
    abstract fun uploadTaskDao(): UploadTaskDao

    // Sprint 1 new DAOs
    abstract fun dailyLogDao(): com.rio.rostry.data.database.dao.DailyLogDao
    abstract fun taskDao(): com.rio.rostry.data.database.dao.TaskDao

    object Converters {
        @TypeConverter
        @JvmStatic
        fun fromStringList(value: List<String>?): String? {
            return value?.let { Gson().toJson(it) }
        }

        // Create upload_tasks table for media outbox (26 -> 27)
        val MIGRATION_26_27 = object : Migration(26, 27) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `upload_tasks` (" +
                        "`taskId` TEXT NOT NULL, `localPath` TEXT NOT NULL, `remotePath` TEXT NOT NULL, " +
                        "`status` TEXT NOT NULL, `progress` INTEGER NOT NULL, `retries` INTEGER NOT NULL, " +
                        "`createdAt` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL, `error` TEXT, `contextJson` TEXT, " +
                        "PRIMARY KEY(`taskId`))"
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_upload_tasks_status_createdAt` ON `upload_tasks` (`status`, `createdAt`)")
            }
        }

        // Add dedicated sync cursors to sync_state (28 -> 29)
        val MIGRATION_28_29 = object : Migration(28, 29) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE `sync_state` ADD COLUMN `lastDailyLogSyncAt` INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE `sync_state` ADD COLUMN `lastTaskSyncAt` INTEGER NOT NULL DEFAULT 0")
            }
        }

        // Add UNIQUE(productId, logDate) for daily_logs to avoid duplicates (29 -> 30)
        val MIGRATION_29_30 = object : Migration(29, 30) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_daily_logs_product_date_unique` ON `daily_logs` (`productId`, `logDate`)")
            }
        }

        // Add statusHistoryJson to quarantine_records and attempt best-effort backfill (30 -> 31)
        val MIGRATION_30_31 = object : Migration(30, 31) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE `quarantine_records` ADD COLUMN `statusHistoryJson` TEXT")
                // Best-effort: if medicationScheduleJson contains 'statusHistory', copy entire JSON to statusHistoryJson
                // SQLite has limited JSON parsing; do a conservative update using LIKE
                db.execSQL(
                    "UPDATE `quarantine_records` SET `statusHistoryJson` = `medicationScheduleJson` " +
                    "WHERE `statusHistoryJson` IS NULL AND `medicationScheduleJson` LIKE '%statusHistory%'"
                )
            }
        }

        // ... existing migrations up to 23_24 defined below (omitted in this view) ...

        // Add enthusiast-specific sync windows to sync_state
        val MIGRATION_24_25 = object : Migration(24, 25) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE `sync_state` ADD COLUMN `lastEnthusiastBreedingSyncAt` INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE `sync_state` ADD COLUMN `lastEnthusiastDashboardSyncAt` INTEGER NOT NULL DEFAULT 0")
            }
        }

        @TypeConverter
        @JvmStatic
        fun toStringList(value: String?): List<String>? {
            val listType = object : TypeToken<List<String>>() {}.type
            return value?.let { Gson().fromJson(it, listType) }
        }

        // Enums
        @TypeConverter
        @JvmStatic
        fun fromUserType(userType: UserType?): String? = userType?.name

        @TypeConverter
        @JvmStatic
        fun toUserType(name: String?): UserType? = name?.let { runCatching { UserType.valueOf(it) }.getOrNull() }

        @TypeConverter
        @JvmStatic
        fun fromVerificationStatus(status: VerificationStatus?): String? = status?.name

        @TypeConverter
        @JvmStatic
        fun toVerificationStatus(name: String?): VerificationStatus? = name?.let { runCatching { VerificationStatus.valueOf(it) }.getOrNull() }
    }

    companion object {
        const val DATABASE_NAME = "rostry_database"

        // Alias migrations defined in Converters for external references
        val MIGRATION_24_25: Migration = Converters.MIGRATION_24_25
        val MIGRATION_26_27: Migration = Converters.MIGRATION_26_27
        val MIGRATION_28_29: Migration = Converters.MIGRATION_28_29
        val MIGRATION_29_30: Migration = Converters.MIGRATION_29_30
        val MIGRATION_30_31: Migration = Converters.MIGRATION_30_31

        // Add daily_logs and tasks tables; add lifecycle columns to products
        val MIGRATION_27_28 = object : Migration(27, 28) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Create daily_logs table
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `daily_logs` (" +
                        "`logId` TEXT NOT NULL, `productId` TEXT NOT NULL, `farmerId` TEXT NOT NULL, `logDate` INTEGER NOT NULL, " +
                        "`weightGrams` REAL, `feedKg` REAL, `medicationJson` TEXT, `symptomsJson` TEXT, `activityLevel` TEXT, `photoUrls` TEXT, `notes` TEXT, `temperature` REAL, `humidity` REAL, " +
                        "`createdAt` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL, `dirty` INTEGER NOT NULL, `syncedAt` INTEGER, `deviceTimestamp` INTEGER NOT NULL, `author` TEXT, " +
                        "PRIMARY KEY(`logId`), " +
                        "FOREIGN KEY(`productId`) REFERENCES `products`(`productId`) ON UPDATE NO ACTION ON DELETE CASCADE)"
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_daily_logs_productId` ON `daily_logs` (`productId`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_daily_logs_farmerId` ON `daily_logs` (`farmerId`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_daily_logs_logDate` ON `daily_logs` (`logDate`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_daily_logs_createdAt` ON `daily_logs` (`createdAt`)")

                // Create tasks table
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `tasks` (" +
                        "`taskId` TEXT NOT NULL, `farmerId` TEXT NOT NULL, `productId` TEXT, `batchId` TEXT, `taskType` TEXT NOT NULL, `title` TEXT NOT NULL, `description` TEXT, `dueAt` INTEGER NOT NULL, " +
                        "`completedAt` INTEGER, `completedBy` TEXT, `priority` TEXT NOT NULL, `recurrence` TEXT, `notes` TEXT, `createdAt` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL, `dirty` INTEGER NOT NULL, `syncedAt` INTEGER, `snoozeUntil` INTEGER, `metadata` TEXT, " +
                        "PRIMARY KEY(`taskId`), " +
                        "FOREIGN KEY(`productId`) REFERENCES `products`(`productId`) ON UPDATE NO ACTION ON DELETE CASCADE)"
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_tasks_farmerId` ON `tasks` (`farmerId`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_tasks_taskType` ON `tasks` (`taskType`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_tasks_dueAt` ON `tasks` (`dueAt`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_tasks_completedAt` ON `tasks` (`completedAt`)")

                // Add lifecycle columns to products
                db.execSQL("ALTER TABLE `products` ADD COLUMN `stage` TEXT")
                db.execSQL("ALTER TABLE `products` ADD COLUMN `lifecycleStatus` TEXT")
                db.execSQL("ALTER TABLE `products` ADD COLUMN `parentMaleId` TEXT")
                db.execSQL("ALTER TABLE `products` ADD COLUMN `parentFemaleId` TEXT")
                db.execSQL("ALTER TABLE `products` ADD COLUMN `ageWeeks` INTEGER")
                db.execSQL("ALTER TABLE `products` ADD COLUMN `lastStageTransitionAt` INTEGER")
                db.execSQL("ALTER TABLE `products` ADD COLUMN `breederEligibleAt` INTEGER")
                db.execSQL("ALTER TABLE `products` ADD COLUMN `isBatch` INTEGER")
                // Default lifecycleStatus to ACTIVE for existing rows
                db.execSQL("UPDATE `products` SET lifecycleStatus = 'ACTIVE' WHERE lifecycleStatus IS NULL")

                // Extend farmer_dashboard_snapshots with daily log metrics
                db.execSQL("ALTER TABLE `farmer_dashboard_snapshots` ADD COLUMN `avgFeedKg` REAL")
                db.execSQL("ALTER TABLE `farmer_dashboard_snapshots` ADD COLUMN `medicationUsageCount` INTEGER")
                db.execSQL("ALTER TABLE `farmer_dashboard_snapshots` ADD COLUMN `dailyLogComplianceRate` REAL")
                db.execSQL("ALTER TABLE `farmer_dashboard_snapshots` ADD COLUMN `actionSuggestions` TEXT")
            }
        }

        // Minimal migration creating new tables (idempotent IF NOT EXISTS)
        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `product_tracking` (" +
                        "`trackingId` TEXT NOT NULL, `productId` TEXT NOT NULL, `ownerId` TEXT NOT NULL, " +
                        "`status` TEXT NOT NULL, `metadataJson` TEXT, `timestamp` INTEGER NOT NULL, " +
                        "`createdAt` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL, `isDeleted` INTEGER NOT NULL, `deletedAt` INTEGER, " +
                        "PRIMARY KEY(`trackingId`), " +
                        "FOREIGN KEY(`productId`) REFERENCES `products`(`productId`) ON UPDATE NO ACTION ON DELETE CASCADE, " +
                        "FOREIGN KEY(`ownerId`) REFERENCES `users`(`userId`) ON UPDATE NO ACTION ON DELETE CASCADE)"
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_product_tracking_productId` ON `product_tracking` (`productId`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_product_tracking_ownerId` ON `product_tracking` (`ownerId`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_product_tracking_productId_timestamp` ON `product_tracking` (`productId`, `timestamp`)")

                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `family_tree` (" +
                        "`nodeId` TEXT NOT NULL, `productId` TEXT NOT NULL, `parentProductId` TEXT, `childProductId` TEXT, `relationType` TEXT, " +
                        "`createdAt` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL, `isDeleted` INTEGER NOT NULL, `deletedAt` INTEGER, " +
                        "PRIMARY KEY(`nodeId`), " +
                        "FOREIGN KEY(`productId`) REFERENCES `products`(`productId`) ON UPDATE NO ACTION ON DELETE CASCADE, " +
                        "FOREIGN KEY(`parentProductId`) REFERENCES `products`(`productId`) ON UPDATE NO ACTION ON DELETE CASCADE, " +
                        "FOREIGN KEY(`childProductId`) REFERENCES `products`(`productId`) ON UPDATE NO ACTION ON DELETE CASCADE)"
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_family_tree_productId` ON `family_tree` (`productId`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_family_tree_parentProductId` ON `family_tree` (`parentProductId`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_family_tree_childProductId` ON `family_tree` (`childProductId`)")
                db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_family_tree_product_parent_child` ON `family_tree` (`productId`, `parentProductId`, `childProductId`)")

                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `chat_messages` (" +
                        "`messageId` TEXT NOT NULL, `senderId` TEXT NOT NULL, `receiverId` TEXT NOT NULL, `body` TEXT NOT NULL, `mediaUrl` TEXT, " +
                        "`sentAt` INTEGER NOT NULL, `deliveredAt` INTEGER, `readAt` INTEGER, `createdAt` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL, `isDeleted` INTEGER NOT NULL, `deletedAt` INTEGER, " +
                        "PRIMARY KEY(`messageId`), " +
                        "FOREIGN KEY(`senderId`) REFERENCES `users`(`userId`) ON UPDATE NO ACTION ON DELETE CASCADE, " +
                        "FOREIGN KEY(`receiverId`) REFERENCES `users`(`userId`) ON UPDATE NO ACTION ON DELETE CASCADE)"
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_chat_messages_senderId` ON `chat_messages` (`senderId`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_chat_messages_receiverId` ON `chat_messages` (`receiverId`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_chat_messages_sender_receiver` ON `chat_messages` (`senderId`, `receiverId`)")

                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `sync_state` (" +
                        "`id` TEXT NOT NULL, `lastSyncAt` INTEGER NOT NULL, `lastUserSyncAt` INTEGER NOT NULL, `lastProductSyncAt` INTEGER NOT NULL, `lastOrderSyncAt` INTEGER NOT NULL, `lastTrackingSyncAt` INTEGER NOT NULL, " +
                        "PRIMARY KEY(`id`))"
                )
            }
        }

        // Add offline-first columns to products
        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Add columns with sensible defaults if they don't exist
                db.execSQL("ALTER TABLE `products` ADD COLUMN `lastModifiedAt` INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE `products` ADD COLUMN `isDeleted` INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE `products` ADD COLUMN `deletedAt` INTEGER")
                db.execSQL("ALTER TABLE `products` ADD COLUMN `dirty` INTEGER NOT NULL DEFAULT 0")
            }
        }

        // Add offline-first columns to orders and transfers
        val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Orders
                db.execSQL("ALTER TABLE `orders` ADD COLUMN `lastModifiedAt` INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE `orders` ADD COLUMN `isDeleted` INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE `orders` ADD COLUMN `deletedAt` INTEGER")
                db.execSQL("ALTER TABLE `orders` ADD COLUMN `dirty` INTEGER NOT NULL DEFAULT 0")

                // Transfers
                db.execSQL("ALTER TABLE `transfers` ADD COLUMN `lastModifiedAt` INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE `transfers` ADD COLUMN `isDeleted` INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE `transfers` ADD COLUMN `deletedAt` INTEGER")
                db.execSQL("ALTER TABLE `transfers` ADD COLUMN `dirty` INTEGER NOT NULL DEFAULT 0")
            }
        }

        // Add new sync_state columns for transfers and chat
        val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE `sync_state` ADD COLUMN `lastTransferSyncAt` INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE `sync_state` ADD COLUMN `lastChatSyncAt` INTEGER NOT NULL DEFAULT 0")
            }
        }

        // Add marketplace-related columns to products and create marketplace tables
        val MIGRATION_6_7 = object : Migration(6, 7) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Products new columns
                db.execSQL("ALTER TABLE `products` ADD COLUMN `latitude` REAL")
                db.execSQL("ALTER TABLE `products` ADD COLUMN `longitude` REAL")
                db.execSQL("ALTER TABLE `products` ADD COLUMN `birthDate` INTEGER")
                db.execSQL("ALTER TABLE `products` ADD COLUMN `vaccinationRecordsJson` TEXT")
                db.execSQL("ALTER TABLE `products` ADD COLUMN `weightGrams` REAL")
                db.execSQL("ALTER TABLE `products` ADD COLUMN `heightCm` REAL")
                db.execSQL("ALTER TABLE `products` ADD COLUMN `gender` TEXT")
                db.execSQL("ALTER TABLE `products` ADD COLUMN `color` TEXT")
                db.execSQL("ALTER TABLE `products` ADD COLUMN `breed` TEXT")
                db.execSQL("ALTER TABLE `products` ADD COLUMN `familyTreeId` TEXT")
                db.execSQL("ALTER TABLE `products` ADD COLUMN `parentIdsJson` TEXT")
                db.execSQL("ALTER TABLE `products` ADD COLUMN `breedingStatus` TEXT")
                db.execSQL("ALTER TABLE `products` ADD COLUMN `transferHistoryJson` TEXT")

                // Auction table
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `auctions` (" +
                        "`auctionId` TEXT NOT NULL, `productId` TEXT NOT NULL, `startsAt` INTEGER NOT NULL, `endsAt` INTEGER NOT NULL, " +
                        "`minPrice` REAL NOT NULL, `currentPrice` REAL NOT NULL, `isActive` INTEGER NOT NULL, " +
                        "PRIMARY KEY(`auctionId`), " +
                        "FOREIGN KEY(`productId`) REFERENCES `products`(`productId`) ON UPDATE NO ACTION ON DELETE CASCADE)"
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_auctions_productId` ON `auctions` (`productId`)")

                // Bids table
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `bids` (" +
                        "`bidId` TEXT NOT NULL, `auctionId` TEXT NOT NULL, `userId` TEXT NOT NULL, `amount` REAL NOT NULL, `placedAt` INTEGER NOT NULL, " +
                        "PRIMARY KEY(`bidId`), " +
                        "FOREIGN KEY(`auctionId`) REFERENCES `auctions`(`auctionId`) ON UPDATE NO ACTION ON DELETE CASCADE, " +
                        "FOREIGN KEY(`userId`) REFERENCES `users`(`userId`) ON UPDATE NO ACTION ON DELETE CASCADE)"
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_bids_auctionId` ON `bids` (`auctionId`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_bids_userId` ON `bids` (`userId`)")

                // Cart items table
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `cart_items` (" +
                        "`id` TEXT NOT NULL, `userId` TEXT NOT NULL, `productId` TEXT NOT NULL, `quantity` REAL NOT NULL, `addedAt` INTEGER NOT NULL, " +
                        "PRIMARY KEY(`id`), " +
                        "FOREIGN KEY(`userId`) REFERENCES `users`(`userId`) ON UPDATE NO ACTION ON DELETE CASCADE, " +
                        "FOREIGN KEY(`productId`) REFERENCES `products`(`productId`) ON UPDATE NO ACTION ON DELETE CASCADE)"
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_cart_items_userId` ON `cart_items` (`userId`)")

                // Wishlist table (composite key userId+productId emulated via unique index)
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `wishlist` (" +
                        "`userId` TEXT NOT NULL, `productId` TEXT NOT NULL, `addedAt` INTEGER NOT NULL, " +
                        "PRIMARY KEY(`userId`, `productId`), " +
                        "FOREIGN KEY(`userId`) REFERENCES `users`(`userId`) ON UPDATE NO ACTION ON DELETE CASCADE, " +
                        "FOREIGN KEY(`productId`) REFERENCES `products`(`productId`) ON UPDATE NO ACTION ON DELETE CASCADE)"
                )
            }
        }

        // Payments, coin ledger, logistics tracking, invoices
        val MIGRATION_7_8 = object : Migration(7, 8) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Payments
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `payments` (" +
                        "`paymentId` TEXT NOT NULL, `orderId` TEXT NOT NULL, `userId` TEXT NOT NULL, `method` TEXT NOT NULL, `amount` REAL NOT NULL, `currency` TEXT NOT NULL, " +
                        "`status` TEXT NOT NULL, `providerRef` TEXT, `upiUri` TEXT, `idempotencyKey` TEXT NOT NULL, `createdAt` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL, " +
                        "PRIMARY KEY(`paymentId`), " +
                        "FOREIGN KEY(`orderId`) REFERENCES `orders`(`orderId`) ON UPDATE NO ACTION ON DELETE CASCADE, " +
                        "FOREIGN KEY(`userId`) REFERENCES `users`(`userId`) ON UPDATE NO ACTION ON DELETE CASCADE)"
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_payments_orderId` ON `payments` (`orderId`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_payments_userId` ON `payments` (`userId`)")
                db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_payments_idempotencyKey` ON `payments` (`idempotencyKey`)")

                // Coin ledger
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `coin_ledger` (" +
                        "`entryId` TEXT NOT NULL, `userId` TEXT NOT NULL, `type` TEXT NOT NULL, `coins` INTEGER NOT NULL, `amountInInr` REAL NOT NULL, `refId` TEXT, `notes` TEXT, `createdAt` INTEGER NOT NULL, " +
                        "PRIMARY KEY(`entryId`), " +
                        "FOREIGN KEY(`userId`) REFERENCES `users`(`userId`) ON UPDATE NO ACTION ON DELETE CASCADE)"
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_coin_ledger_userId` ON `coin_ledger` (`userId`)")

                // Delivery hubs
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `delivery_hubs` (" +
                        "`hubId` TEXT NOT NULL, `name` TEXT NOT NULL, `latitude` REAL, `longitude` REAL, `address` TEXT, `createdAt` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL, " +
                        "PRIMARY KEY(`hubId`))"
                )

                // Order tracking events
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `order_tracking_events` (" +
                        "`eventId` TEXT NOT NULL, `orderId` TEXT NOT NULL, `status` TEXT NOT NULL, `hubId` TEXT, `note` TEXT, `timestamp` INTEGER NOT NULL, " +
                        "PRIMARY KEY(`eventId`), " +
                        "FOREIGN KEY(`orderId`) REFERENCES `orders`(`orderId`) ON UPDATE NO ACTION ON DELETE CASCADE, " +
                        "FOREIGN KEY(`hubId`) REFERENCES `delivery_hubs`(`hubId`) ON UPDATE NO ACTION ON DELETE SET NULL)"
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_order_tracking_events_orderId` ON `order_tracking_events` (`orderId`)")

                // Invoices
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `invoices` (" +
                        "`invoiceId` TEXT NOT NULL, `orderId` TEXT NOT NULL, `subtotal` REAL NOT NULL, `gstPercent` REAL NOT NULL, `gstAmount` REAL NOT NULL, `total` REAL NOT NULL, `createdAt` INTEGER NOT NULL, " +
                        "PRIMARY KEY(`invoiceId`), " +
                        "FOREIGN KEY(`orderId`) REFERENCES `orders`(`orderId`) ON UPDATE NO ACTION ON DELETE CASCADE)"
                )
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `invoice_lines` (" +
                        "`lineId` TEXT NOT NULL, `invoiceId` TEXT NOT NULL, `description` TEXT NOT NULL, `qty` REAL NOT NULL, `unitPrice` REAL NOT NULL, `lineTotal` REAL NOT NULL, " +
                        "PRIMARY KEY(`lineId`), " +
                        "FOREIGN KEY(`invoiceId`) REFERENCES `invoices`(`invoiceId`) ON UPDATE NO ACTION ON DELETE CASCADE)"
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_invoice_lines_invoiceId` ON `invoice_lines` (`invoiceId`)")
            }
        }

        // Refunds table for payment refunds
        val MIGRATION_8_9 = object : Migration(8, 9) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `refunds` (" +
                        "`refundId` TEXT NOT NULL, `paymentId` TEXT NOT NULL, `orderId` TEXT NOT NULL, `amount` REAL NOT NULL, `reason` TEXT, `createdAt` INTEGER NOT NULL, " +
                        "PRIMARY KEY(`refundId`), " +
                        "FOREIGN KEY(`paymentId`) REFERENCES `payments`(`paymentId`) ON UPDATE NO ACTION ON DELETE CASCADE, " +
                        "FOREIGN KEY(`orderId`) REFERENCES `orders`(`orderId`) ON UPDATE NO ACTION ON DELETE CASCADE)"
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_refunds_paymentId` ON `refunds` (`paymentId`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_refunds_orderId` ON `refunds` (`orderId`)")
            }
        }

        // Traceability: breeding records, traits, lifecycle events
        val MIGRATION_9_10 = object : Migration(9, 10) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // breeding_records
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `breeding_records` (" +
                        "`recordId` TEXT NOT NULL, `parentId` TEXT NOT NULL, `partnerId` TEXT NOT NULL, `childId` TEXT NOT NULL, `success` INTEGER NOT NULL, `notes` TEXT, `timestamp` INTEGER NOT NULL, " +
                        "PRIMARY KEY(`recordId`), " +
                        "FOREIGN KEY(`parentId`) REFERENCES `products`(`productId`) ON UPDATE NO ACTION ON DELETE CASCADE, " +
                        "FOREIGN KEY(`partnerId`) REFERENCES `products`(`productId`) ON UPDATE NO ACTION ON DELETE CASCADE, " +
                        "FOREIGN KEY(`childId`) REFERENCES `products`(`productId`) ON UPDATE NO ACTION ON DELETE CASCADE)"
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_breeding_records_parentId` ON `breeding_records` (`parentId`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_breeding_records_partnerId` ON `breeding_records` (`partnerId`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_breeding_records_childId` ON `breeding_records` (`childId`)")

                // traits
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `traits` (" +
                        "`traitId` TEXT NOT NULL, `name` TEXT NOT NULL, `description` TEXT, PRIMARY KEY(`traitId`))"
                )

                // product_traits junction
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `product_traits` (" +
                        "`productId` TEXT NOT NULL, `traitId` TEXT NOT NULL, " +
                        "PRIMARY KEY(`productId`, `traitId`), " +
                        "FOREIGN KEY(`productId`) REFERENCES `products`(`productId`) ON UPDATE NO ACTION ON DELETE CASCADE, " +
                        "FOREIGN KEY(`traitId`) REFERENCES `traits`(`traitId`) ON UPDATE NO ACTION ON DELETE CASCADE)"
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_product_traits_traitId` ON `product_traits` (`traitId`)")

                // lifecycle_events
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `lifecycle_events` (" +
                        "`eventId` TEXT NOT NULL, `productId` TEXT NOT NULL, `week` INTEGER NOT NULL, `stage` TEXT NOT NULL, `type` TEXT NOT NULL, `notes` TEXT, `timestamp` INTEGER NOT NULL, " +
                        "PRIMARY KEY(`eventId`), " +
                        "FOREIGN KEY(`productId`) REFERENCES `products`(`productId`) ON UPDATE NO ACTION ON DELETE CASCADE)"
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_lifecycle_events_productId` ON `lifecycle_events` (`productId`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_lifecycle_events_week` ON `lifecycle_events` (`week`)")
            }
        }

        // Add productId to transfers and index for traceability linkage
        val MIGRATION_10_11 = object : Migration(10, 11) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Add nullable column to avoid data loss
                db.execSQL("ALTER TABLE `transfers` ADD COLUMN `productId` TEXT")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_transfers_productId` ON `transfers` (`productId`)")
            }
        }

        // Transfer workflow: verification, disputes, audit; add columns to transfers for proofs, gps, conditions
        val MIGRATION_11_12 = object : Migration(11, 12) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // New tables
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `transfer_verifications` (" +
                        "`verificationId` TEXT NOT NULL, `transferId` TEXT NOT NULL, `step` TEXT NOT NULL, `status` TEXT NOT NULL, " +
                        "`photoBeforeUrl` TEXT, `photoAfterUrl` TEXT, `photoBeforeMetaJson` TEXT, `photoAfterMetaJson` TEXT, " +
                        "`gpsLat` REAL, `gpsLng` REAL, `identityDocType` TEXT, `identityDocRef` TEXT, `notes` TEXT, `createdAt` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL, " +
                        "PRIMARY KEY(`verificationId`), " +
                        "FOREIGN KEY(`transferId`) REFERENCES `transfers`(`transferId`) ON UPDATE NO ACTION ON DELETE CASCADE)"
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_transfer_verifications_transferId` ON `transfer_verifications` (`transferId`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_transfer_verifications_status` ON `transfer_verifications` (`status`)")

                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `transfer_disputes` (" +
                        "`disputeId` TEXT NOT NULL, `transferId` TEXT NOT NULL, `raisedByUserId` TEXT NOT NULL, `reason` TEXT NOT NULL, `status` TEXT NOT NULL, `resolutionNotes` TEXT, `createdAt` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL, " +
                        "PRIMARY KEY(`disputeId`), " +
                        "FOREIGN KEY(`transferId`) REFERENCES `transfers`(`transferId`) ON UPDATE NO ACTION ON DELETE CASCADE)"
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_transfer_disputes_transferId` ON `transfer_disputes` (`transferId`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_transfer_disputes_status` ON `transfer_disputes` (`status`)")

                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `audit_logs` (" +
                        "`logId` TEXT NOT NULL, `type` TEXT NOT NULL, `refId` TEXT NOT NULL, `action` TEXT NOT NULL, `actorUserId` TEXT, `detailsJson` TEXT, `createdAt` INTEGER NOT NULL, " +
                        "PRIMARY KEY(`logId`))"
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_audit_logs_refId` ON `audit_logs` (`refId`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_audit_logs_type` ON `audit_logs` (`type`)")

                // Extend transfers with verification-related columns
                db.execSQL("ALTER TABLE `transfers` ADD COLUMN `gpsLat` REAL")
                db.execSQL("ALTER TABLE `transfers` ADD COLUMN `gpsLng` REAL")
                db.execSQL("ALTER TABLE `transfers` ADD COLUMN `sellerPhotoUrl` TEXT")
                db.execSQL("ALTER TABLE `transfers` ADD COLUMN `buyerPhotoUrl` TEXT")
                db.execSQL("ALTER TABLE `transfers` ADD COLUMN `timeoutAt` INTEGER")
                db.execSQL("ALTER TABLE `transfers` ADD COLUMN `conditionsJson` TEXT")
            }
        }

        val MIGRATION_12_13 = object : Migration(12, 13) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Posts
                db.execSQL("CREATE TABLE IF NOT EXISTS `posts` (`postId` TEXT NOT NULL, `authorId` TEXT NOT NULL, `type` TEXT NOT NULL, `text` TEXT, `mediaUrl` TEXT, `thumbnailUrl` TEXT, `productId` TEXT, `createdAt` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL, PRIMARY KEY(`postId`))")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_posts_authorId` ON `posts` (`authorId`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_posts_createdAt` ON `posts` (`createdAt`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_posts_type` ON `posts` (`type`)")

                // Comments
                db.execSQL("CREATE TABLE IF NOT EXISTS `comments` (`commentId` TEXT NOT NULL, `postId` TEXT NOT NULL, `authorId` TEXT NOT NULL, `text` TEXT NOT NULL, `createdAt` INTEGER NOT NULL, PRIMARY KEY(`commentId`))")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_comments_postId` ON `comments` (`postId`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_comments_authorId` ON `comments` (`authorId`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_comments_createdAt` ON `comments` (`createdAt`)")

                // Likes
                db.execSQL("CREATE TABLE IF NOT EXISTS `likes` (`likeId` TEXT NOT NULL, `postId` TEXT NOT NULL, `userId` TEXT NOT NULL, `createdAt` INTEGER NOT NULL, PRIMARY KEY(`likeId`))")
                db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_likes_postId_userId` ON `likes` (`postId`, `userId`)")

                // Follows
                db.execSQL("CREATE TABLE IF NOT EXISTS `follows` (`followId` TEXT NOT NULL, `followerId` TEXT NOT NULL, `followedId` TEXT NOT NULL, `createdAt` INTEGER NOT NULL, PRIMARY KEY(`followId`))")
                db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_follows_followerId_followedId` ON `follows` (`followerId`, `followedId`)")

                // Groups
                db.execSQL("CREATE TABLE IF NOT EXISTS `groups` (`groupId` TEXT NOT NULL, `name` TEXT NOT NULL, `description` TEXT, `ownerId` TEXT NOT NULL, `category` TEXT, `createdAt` INTEGER NOT NULL, PRIMARY KEY(`groupId`))")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_groups_ownerId` ON `groups` (`ownerId`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_groups_name` ON `groups` (`name`)")

                // Group members
                db.execSQL("CREATE TABLE IF NOT EXISTS `group_members` (`membershipId` TEXT NOT NULL, `groupId` TEXT NOT NULL, `userId` TEXT NOT NULL, `role` TEXT NOT NULL, `joinedAt` INTEGER NOT NULL, PRIMARY KEY(`membershipId`))")
                db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_group_members_groupId_userId` ON `group_members` (`groupId`, `userId`)")

                // Events
                db.execSQL("CREATE TABLE IF NOT EXISTS `events` (`eventId` TEXT NOT NULL, `groupId` TEXT, `title` TEXT NOT NULL, `description` TEXT, `location` TEXT, `startTime` INTEGER NOT NULL, `endTime` INTEGER, PRIMARY KEY(`eventId`))")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_events_groupId` ON `events` (`groupId`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_events_startTime` ON `events` (`startTime`)")

                // Expert bookings
                db.execSQL("CREATE TABLE IF NOT EXISTS `expert_bookings` (`bookingId` TEXT NOT NULL, `expertId` TEXT NOT NULL, `userId` TEXT NOT NULL, `topic` TEXT, `startTime` INTEGER NOT NULL, `endTime` INTEGER NOT NULL, `status` TEXT NOT NULL, PRIMARY KEY(`bookingId`))")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_expert_bookings_expertId` ON `expert_bookings` (`expertId`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_expert_bookings_userId` ON `expert_bookings` (`userId`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_expert_bookings_startTime` ON `expert_bookings` (`startTime`)")

                // Moderation reports
                db.execSQL("CREATE TABLE IF NOT EXISTS `moderation_reports` (`reportId` TEXT NOT NULL, `targetType` TEXT NOT NULL, `targetId` TEXT NOT NULL, `reporterId` TEXT NOT NULL, `reason` TEXT NOT NULL, `status` TEXT NOT NULL, `createdAt` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL, PRIMARY KEY(`reportId`))")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_moderation_reports_targetType` ON `moderation_reports` (`targetType`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_moderation_reports_targetId` ON `moderation_reports` (`targetId`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_moderation_reports_status` ON `moderation_reports` (`status`)")

                // Badges
                db.execSQL("CREATE TABLE IF NOT EXISTS `badges` (`badgeId` TEXT NOT NULL, `userId` TEXT NOT NULL, `name` TEXT NOT NULL, `description` TEXT, `awardedAt` INTEGER NOT NULL, PRIMARY KEY(`badgeId`))")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_badges_userId` ON `badges` (`userId`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_badges_awardedAt` ON `badges` (`awardedAt`)")

                // Reputation
                db.execSQL("CREATE TABLE IF NOT EXISTS `reputation` (`repId` TEXT NOT NULL, `userId` TEXT NOT NULL, `score` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL, PRIMARY KEY(`repId`))")
                db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_reputation_userId` ON `reputation` (`userId`)")

                // Outgoing messages queue
                db.execSQL("CREATE TABLE IF NOT EXISTS `outgoing_messages` (`id` TEXT NOT NULL, `kind` TEXT NOT NULL, `threadOrGroupId` TEXT NOT NULL, `fromUserId` TEXT NOT NULL, `toUserId` TEXT, `bodyText` TEXT, `fileUri` TEXT, `fileName` TEXT, `status` TEXT NOT NULL, `createdAt` INTEGER NOT NULL, PRIMARY KEY(`id`))")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_outgoing_messages_status` ON `outgoing_messages` (`status`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_outgoing_messages_createdAt` ON `outgoing_messages` (`createdAt`)")

                // Rate limits
                db.execSQL("CREATE TABLE IF NOT EXISTS `rate_limits` (`id` TEXT NOT NULL, `userId` TEXT NOT NULL, `action` TEXT NOT NULL, `lastAt` INTEGER NOT NULL, PRIMARY KEY(`id`))")
                db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_rate_limits_userId_action` ON `rate_limits` (`userId`, `action`)")

                // Event RSVPs
                db.execSQL("CREATE TABLE IF NOT EXISTS `event_rsvps` (`id` TEXT NOT NULL, `eventId` TEXT NOT NULL, `userId` TEXT NOT NULL, `status` TEXT NOT NULL, `updatedAt` INTEGER NOT NULL, PRIMARY KEY(`id`))")
                db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_event_rsvps_eventId_userId` ON `event_rsvps` (`eventId`, `userId`)")

                // Analytics daily
                db.execSQL("CREATE TABLE IF NOT EXISTS `analytics_daily` (`id` TEXT NOT NULL, `userId` TEXT NOT NULL, `role` TEXT NOT NULL, `dateKey` TEXT NOT NULL, `salesRevenue` REAL NOT NULL, `ordersCount` INTEGER NOT NULL, `productViews` INTEGER NOT NULL, `likesCount` INTEGER NOT NULL, `commentsCount` INTEGER NOT NULL, `transfersCount` INTEGER NOT NULL, `breedingSuccessRate` REAL NOT NULL, `engagementScore` REAL NOT NULL, `createdAt` INTEGER NOT NULL, PRIMARY KEY(`id`))")
                db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_analytics_daily_userId_dateKey` ON `analytics_daily` (`userId`, `dateKey`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_analytics_daily_role` ON `analytics_daily` (`role`)")

                // Reports
                db.execSQL("CREATE TABLE IF NOT EXISTS `reports` (`reportId` TEXT NOT NULL, `userId` TEXT NOT NULL, `type` TEXT NOT NULL, `periodStart` INTEGER NOT NULL, `periodEnd` INTEGER NOT NULL, `format` TEXT NOT NULL, `uri` TEXT, `createdAt` INTEGER NOT NULL, PRIMARY KEY(`reportId`))")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_reports_userId` ON `reports` (`userId`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_reports_periodStart` ON `reports` (`periodStart`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_reports_type` ON `reports` (`type`)")
            }
        }

        // Farm monitoring tables
        val MIGRATION_13_14 = object : Migration(13, 14) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // growth_records
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `growth_records` (" +
                        "`recordId` TEXT NOT NULL, `productId` TEXT NOT NULL, `week` INTEGER NOT NULL, " +
                        "`weightGrams` REAL, `heightCm` REAL, `photoUrl` TEXT, `healthStatus` TEXT, `milestone` TEXT, `createdAt` INTEGER NOT NULL, " +
                        "PRIMARY KEY(`recordId`), " +
                        "FOREIGN KEY(`productId`) REFERENCES `products`(`productId`) ON UPDATE NO ACTION ON DELETE CASCADE)"
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_growth_records_productId` ON `growth_records` (`productId`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_growth_records_week` ON `growth_records` (`week`)")

                // quarantine_records
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `quarantine_records` (" +
                        "`quarantineId` TEXT NOT NULL, `productId` TEXT NOT NULL, `reason` TEXT NOT NULL, `protocol` TEXT, `medicationScheduleJson` TEXT, `vetNotes` TEXT, `startedAt` INTEGER NOT NULL, `endedAt` INTEGER, `status` TEXT NOT NULL, " +
                        "PRIMARY KEY(`quarantineId`), " +
                        "FOREIGN KEY(`productId`) REFERENCES `products`(`productId`) ON UPDATE NO ACTION ON DELETE CASCADE)"
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_quarantine_records_productId` ON `quarantine_records` (`productId`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_quarantine_records_status` ON `quarantine_records` (`status`)")

                // mortality_records
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `mortality_records` (" +
                        "`deathId` TEXT NOT NULL, `productId` TEXT, `causeCategory` TEXT NOT NULL, `circumstances` TEXT, `ageWeeks` INTEGER, `disposalMethod` TEXT, `financialImpactInr` REAL, `occurredAt` INTEGER NOT NULL, " +
                        "PRIMARY KEY(`deathId`), " +
                        "FOREIGN KEY(`productId`) REFERENCES `products`(`productId`) ON UPDATE NO ACTION ON DELETE SET NULL)"
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_mortality_records_productId` ON `mortality_records` (`productId`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_mortality_records_causeCategory` ON `mortality_records` (`causeCategory`)")

                // vaccination_records
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `vaccination_records` (" +
                        "`vaccinationId` TEXT NOT NULL, `productId` TEXT NOT NULL, `vaccineType` TEXT NOT NULL, `supplier` TEXT, `batchCode` TEXT, `doseMl` REAL, `scheduledAt` INTEGER NOT NULL, `administeredAt` INTEGER, `efficacyNotes` TEXT, `costInr` REAL, `createdAt` INTEGER NOT NULL, " +
                        "PRIMARY KEY(`vaccinationId`), " +
                        "FOREIGN KEY(`productId`) REFERENCES `products`(`productId`) ON UPDATE NO ACTION ON DELETE CASCADE)"
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_vaccination_records_productId` ON `vaccination_records` (`productId`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_vaccination_records_vaccineType` ON `vaccination_records` (`vaccineType`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_vaccination_records_scheduledAt` ON `vaccination_records` (`scheduledAt`)")

                // hatching_batches
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `hatching_batches` (" +
                        "`batchId` TEXT NOT NULL, `name` TEXT NOT NULL, `startedAt` INTEGER NOT NULL, `expectedHatchAt` INTEGER, `temperatureC` REAL, `humidityPct` REAL, `notes` TEXT, " +
                        "PRIMARY KEY(`batchId`))"
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_hatching_batches_name` ON `hatching_batches` (`name`)")

                // hatching_logs
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `hatching_logs` (" +
                        "`logId` TEXT NOT NULL, `batchId` TEXT NOT NULL, `productId` TEXT, `eventType` TEXT NOT NULL, `qualityScore` INTEGER, `temperatureC` REAL, `humidityPct` REAL, `notes` TEXT, `createdAt` INTEGER NOT NULL, " +
                        "PRIMARY KEY(`logId`), " +
                        "FOREIGN KEY(`batchId`) REFERENCES `hatching_batches`(`batchId`) ON UPDATE NO ACTION ON DELETE CASCADE, " +
                        "FOREIGN KEY(`productId`) REFERENCES `products`(`productId`) ON UPDATE NO ACTION ON DELETE SET NULL)"
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_hatching_logs_batchId` ON `hatching_logs` (`batchId`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_hatching_logs_productId` ON `hatching_logs` (`productId`)")
            }
        }

        // Gamification tables
        val MIGRATION_14_15 = object : Migration(14, 15) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Achievements definitions
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `achievements_def` (" +
                        "`achievementId` TEXT NOT NULL, `name` TEXT NOT NULL, `description` TEXT, `points` INTEGER NOT NULL, `category` TEXT, `icon` TEXT, " +
                        "PRIMARY KEY(`achievementId`))"
                )
                // User progress
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `user_progress` (" +
                        "`id` TEXT NOT NULL, `userId` TEXT NOT NULL, `achievementId` TEXT NOT NULL, `progress` INTEGER NOT NULL, `target` INTEGER NOT NULL, `unlockedAt` INTEGER, `updatedAt` INTEGER NOT NULL, " +
                        "PRIMARY KEY(`id`))"
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_user_progress_userId` ON `user_progress` (`userId`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_user_progress_achievementId` ON `user_progress` (`achievementId`)")

                // Badges definitions
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `badges_def` (" +
                        "`badgeId` TEXT NOT NULL, `name` TEXT NOT NULL, `description` TEXT, `icon` TEXT, " +
                        "PRIMARY KEY(`badgeId`))"
                )

                // Leaderboard
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `leaderboard` (" +
                        "`id` TEXT NOT NULL, `periodKey` TEXT NOT NULL, `userId` TEXT NOT NULL, `score` INTEGER NOT NULL, `rank` INTEGER NOT NULL, " +
                        "PRIMARY KEY(`id`))"
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_leaderboard_periodKey` ON `leaderboard` (`periodKey`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_leaderboard_rank` ON `leaderboard` (`rank`)")

                // Rewards definitions
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `rewards_def` (" +
                        "`rewardId` TEXT NOT NULL, `name` TEXT NOT NULL, `description` TEXT, `pointsRequired` INTEGER NOT NULL, " +
                        "PRIMARY KEY(`rewardId`))"
                )
            }
        }

        // Community engagement tables
        val MIGRATION_15_16 = object : Migration(15, 16) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // thread_metadata
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `thread_metadata` (" +
                        "`threadId` TEXT NOT NULL, `title` TEXT, `contextType` TEXT, `relatedEntityId` TEXT, `topic` TEXT, " +
                        "`participantIds` TEXT NOT NULL, `lastMessageAt` INTEGER NOT NULL, `createdAt` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL, " +
                        "PRIMARY KEY(`threadId`))"
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_thread_metadata_contextType` ON `thread_metadata` (`contextType`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_thread_metadata_lastMessageAt` ON `thread_metadata` (`lastMessageAt`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_thread_metadata_createdAt` ON `thread_metadata` (`createdAt`)")

                // community_recommendations
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `community_recommendations` (" +
                        "`recommendationId` TEXT NOT NULL, `userId` TEXT NOT NULL, `type` TEXT NOT NULL, `targetId` TEXT NOT NULL, " +
                        "`score` REAL NOT NULL, `reason` TEXT, `createdAt` INTEGER NOT NULL, `expiresAt` INTEGER NOT NULL, `dismissed` INTEGER NOT NULL DEFAULT 0, " +
                        "PRIMARY KEY(`recommendationId`))"
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_community_recommendations_userId` ON `community_recommendations` (`userId`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_community_recommendations_type` ON `community_recommendations` (`type`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_community_recommendations_score` ON `community_recommendations` (`score`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_community_recommendations_expiresAt` ON `community_recommendations` (`expiresAt`)")

                // user_interests
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `user_interests` (" +
                        "`interestId` TEXT NOT NULL, `userId` TEXT NOT NULL, `category` TEXT NOT NULL, `value` TEXT NOT NULL, " +
                        "`weight` REAL NOT NULL, `updatedAt` INTEGER NOT NULL, " +
                        "PRIMARY KEY(`interestId`))"
                )
                db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_user_interests_userId_category_value` ON `user_interests` (`userId`, `category`, `value`)")

                // expert_profiles
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `expert_profiles` (" +
                        "`userId` TEXT NOT NULL, `specialties` TEXT NOT NULL, `bio` TEXT, `rating` REAL NOT NULL, " +
                        "`totalConsultations` INTEGER NOT NULL, `availableForBooking` INTEGER NOT NULL, `hourlyRate` REAL, `updatedAt` INTEGER NOT NULL, " +
                        "PRIMARY KEY(`userId`))"
                )
            }
        }

        // Add KYC document upload columns to users table
        val MIGRATION_16_17 = object : Migration(16, 17) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Add new KYC-related columns to users table with appropriate defaults
                db.execSQL("ALTER TABLE `users` ADD COLUMN `kycDocumentUrls` TEXT")
                db.execSQL("ALTER TABLE `users` ADD COLUMN `kycImageUrls` TEXT")
                db.execSQL("ALTER TABLE `users` ADD COLUMN `kycDocumentTypes` TEXT")
                db.execSQL("ALTER TABLE `users` ADD COLUMN `kycUploadStatus` TEXT")
                db.execSQL("ALTER TABLE `users` ADD COLUMN `kycUploadedAt` INTEGER")
                db.execSQL("ALTER TABLE `users` ADD COLUMN `kycVerifiedAt` INTEGER")
                db.execSQL("ALTER TABLE `users` ADD COLUMN `kycRejectionReason` TEXT")
            }
        }

        // Add outbox table for offline-first queued mutations
        val MIGRATION_17_18 = object : Migration(17, 18) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `outbox` (" +
                        "`outboxId` TEXT NOT NULL, `userId` TEXT NOT NULL, `entityType` TEXT NOT NULL, " +
                        "`entityId` TEXT NOT NULL, `operation` TEXT NOT NULL, `payloadJson` TEXT NOT NULL, " +
                        "`createdAt` INTEGER NOT NULL, `retryCount` INTEGER NOT NULL, `lastAttemptAt` INTEGER, " +
                        "`status` TEXT NOT NULL, " +
                        "PRIMARY KEY(`outboxId`))"
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_outbox_userId` ON `outbox` (`userId`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_outbox_status` ON `outbox` (`status`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_outbox_createdAt` ON `outbox` (`createdAt`)")
            }
        }

        // Add new farm monitoring entities for farmer home dashboard
        val MIGRATION_18_19 = object : Migration(18, 19) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // breeding_pairs
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `breeding_pairs` (" +
                        "`pairId` TEXT NOT NULL, `farmerId` TEXT NOT NULL, `maleProductId` TEXT NOT NULL, `femaleProductId` TEXT NOT NULL, " +
                        "`pairedAt` INTEGER NOT NULL, `status` TEXT NOT NULL, `eggsCollected` INTEGER NOT NULL, `hatchSuccessRate` REAL NOT NULL, " +
                        "`notes` TEXT, `createdAt` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL, `dirty` INTEGER NOT NULL, `syncedAt` INTEGER, " +
                        "PRIMARY KEY(`pairId`), " +
                        "FOREIGN KEY(`maleProductId`) REFERENCES `products`(`productId`) ON UPDATE NO ACTION ON DELETE CASCADE, " +
                        "FOREIGN KEY(`femaleProductId`) REFERENCES `products`(`productId`) ON UPDATE NO ACTION ON DELETE CASCADE)"
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_breeding_pairs_farmerId` ON `breeding_pairs` (`farmerId`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_breeding_pairs_status` ON `breeding_pairs` (`status`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_breeding_pairs_maleProductId` ON `breeding_pairs` (`maleProductId`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_breeding_pairs_femaleProductId` ON `breeding_pairs` (`femaleProductId`)")

                // farm_alerts
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `farm_alerts` (" +
                        "`alertId` TEXT NOT NULL, `farmerId` TEXT NOT NULL, `alertType` TEXT NOT NULL, `severity` TEXT NOT NULL, " +
                        "`message` TEXT NOT NULL, `actionRoute` TEXT, `isRead` INTEGER NOT NULL, `createdAt` INTEGER NOT NULL, " +
                        "`expiresAt` INTEGER, `dirty` INTEGER NOT NULL, `syncedAt` INTEGER, " +
                        "PRIMARY KEY(`alertId`))"
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_farm_alerts_farmerId` ON `farm_alerts` (`farmerId`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_farm_alerts_isRead` ON `farm_alerts` (`isRead`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_farm_alerts_createdAt` ON `farm_alerts` (`createdAt`)")

                // listing_drafts
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `listing_drafts` (" +
                        "`draftId` TEXT NOT NULL, `farmerId` TEXT NOT NULL, `step` TEXT NOT NULL, `formDataJson` TEXT NOT NULL, " +
                        "`createdAt` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL, `expiresAt` INTEGER, " +
                        "PRIMARY KEY(`draftId`))"
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_listing_drafts_farmerId` ON `listing_drafts` (`farmerId`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_listing_drafts_updatedAt` ON `listing_drafts` (`updatedAt`)")

                // farmer_dashboard_snapshots
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `farmer_dashboard_snapshots` (" +
                        "`snapshotId` TEXT NOT NULL, `farmerId` TEXT NOT NULL, `weekStartAt` INTEGER NOT NULL, `weekEndAt` INTEGER NOT NULL, " +
                        "`revenueInr` REAL NOT NULL, `ordersCount` INTEGER NOT NULL, `hatchSuccessRate` REAL NOT NULL, " +
                        "`mortalityRate` REAL NOT NULL, `vaccinationCompletionRate` REAL NOT NULL, `growthRecordsCount` INTEGER NOT NULL, " +
                        "`quarantineActiveCount` INTEGER NOT NULL, `createdAt` INTEGER NOT NULL, `dirty` INTEGER NOT NULL, `syncedAt` INTEGER, " +
                        "PRIMARY KEY(`snapshotId`))"
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_farmer_dashboard_snapshots_farmerId` ON `farmer_dashboard_snapshots` (`farmerId`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_farmer_dashboard_snapshots_weekStartAt` ON `farmer_dashboard_snapshots` (`weekStartAt`)")
            }
        }

        // Add farmerId and sync metadata to legacy farm monitoring entities
        val MIGRATION_19_20 = object : Migration(19, 20) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Add farmerId and sync columns to growth_records
                db.execSQL("ALTER TABLE `growth_records` ADD COLUMN `farmerId` TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE `growth_records` ADD COLUMN `updatedAt` INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE `growth_records` ADD COLUMN `dirty` INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE `growth_records` ADD COLUMN `syncedAt` INTEGER")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_growth_records_farmerId` ON `growth_records` (`farmerId`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_growth_records_createdAt` ON `growth_records` (`createdAt`)")

                // Add farmerId and sync columns to quarantine_records
                db.execSQL("ALTER TABLE `quarantine_records` ADD COLUMN `farmerId` TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE `quarantine_records` ADD COLUMN `lastUpdatedAt` INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE `quarantine_records` ADD COLUMN `updatesCount` INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE `quarantine_records` ADD COLUMN `updatedAt` INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE `quarantine_records` ADD COLUMN `dirty` INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE `quarantine_records` ADD COLUMN `syncedAt` INTEGER")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_quarantine_records_farmerId` ON `quarantine_records` (`farmerId`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_quarantine_records_startedAt` ON `quarantine_records` (`startedAt`)")

                // Add farmerId and sync columns to mortality_records
                db.execSQL("ALTER TABLE `mortality_records` ADD COLUMN `farmerId` TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE `mortality_records` ADD COLUMN `updatedAt` INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE `mortality_records` ADD COLUMN `dirty` INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE `mortality_records` ADD COLUMN `syncedAt` INTEGER")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_mortality_records_farmerId` ON `mortality_records` (`farmerId`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_mortality_records_occurredAt` ON `mortality_records` (`occurredAt`)")

                // Add farmerId and sync columns to vaccination_records
                db.execSQL("ALTER TABLE `vaccination_records` ADD COLUMN `farmerId` TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE `vaccination_records` ADD COLUMN `updatedAt` INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE `vaccination_records` ADD COLUMN `dirty` INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE `vaccination_records` ADD COLUMN `syncedAt` INTEGER")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_vaccination_records_farmerId` ON `vaccination_records` (`farmerId`)")

                // Add farmerId and sync columns to hatching_batches
                db.execSQL("ALTER TABLE `hatching_batches` ADD COLUMN `farmerId` TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE `hatching_batches` ADD COLUMN `updatedAt` INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE `hatching_batches` ADD COLUMN `dirty` INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE `hatching_batches` ADD COLUMN `syncedAt` INTEGER")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_hatching_batches_farmerId` ON `hatching_batches` (`farmerId`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_hatching_batches_expectedHatchAt` ON `hatching_batches` (`expectedHatchAt`)")

                // Add farmerId and sync columns to hatching_logs
                db.execSQL("ALTER TABLE `hatching_logs` ADD COLUMN `farmerId` TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE `hatching_logs` ADD COLUMN `updatedAt` INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE `hatching_logs` ADD COLUMN `dirty` INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE `hatching_logs` ADD COLUMN `syncedAt` INTEGER")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_hatching_logs_farmerId` ON `hatching_logs` (`farmerId`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_hatching_logs_createdAt` ON `hatching_logs` (`createdAt`)")

                // Backfill farmerId from related products where possible (growth, vaccination, quarantine)
                // This is a best-effort - products table has sellerId which maps to farmerId
                db.execSQL("""
                    UPDATE growth_records 
                    SET farmerId = (SELECT sellerId FROM products WHERE products.productId = growth_records.productId)
                    WHERE EXISTS (SELECT 1 FROM products WHERE products.productId = growth_records.productId)
                """)
                
                db.execSQL("""
                    UPDATE vaccination_records 
                    SET farmerId = (SELECT sellerId FROM products WHERE products.productId = vaccination_records.productId)
                    WHERE EXISTS (SELECT 1 FROM products WHERE products.productId = vaccination_records.productId)
                """)
                
                db.execSQL("""
                    UPDATE quarantine_records 
                    SET farmerId = (SELECT sellerId FROM products WHERE products.productId = quarantine_records.productId)
                    WHERE EXISTS (SELECT 1 FROM products WHERE products.productId = quarantine_records.productId)
                """)
                
                db.execSQL("""
                    UPDATE mortality_records 
                    SET farmerId = (SELECT sellerId FROM products WHERE products.productId = mortality_records.productId)
                    WHERE productId IS NOT NULL 
                    AND EXISTS (SELECT 1 FROM products WHERE products.productId = mortality_records.productId)
                """)

                // Set updatedAt to createdAt for existing records
                db.execSQL("UPDATE growth_records SET updatedAt = createdAt")
                db.execSQL("UPDATE quarantine_records SET updatedAt = startedAt, lastUpdatedAt = startedAt")
                db.execSQL("UPDATE mortality_records SET updatedAt = occurredAt")
                db.execSQL("UPDATE vaccination_records SET updatedAt = createdAt")
                db.execSQL("UPDATE hatching_batches SET updatedAt = startedAt")
                db.execSQL("UPDATE hatching_logs SET updatedAt = createdAt")
            }
        }

        // Add farm monitoring sync timestamp columns to sync_state
        val MIGRATION_20_21 = object : Migration(20, 21) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Add farm monitoring sync timestamp columns
                db.execSQL("ALTER TABLE `sync_state` ADD COLUMN `lastBreedingSyncAt` INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE `sync_state` ADD COLUMN `lastAlertSyncAt` INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE `sync_state` ADD COLUMN `lastDashboardSyncAt` INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE `sync_state` ADD COLUMN `lastVaccinationSyncAt` INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE `sync_state` ADD COLUMN `lastGrowthSyncAt` INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE `sync_state` ADD COLUMN `lastQuarantineSyncAt` INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE `sync_state` ADD COLUMN `lastMortalitySyncAt` INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE `sync_state` ADD COLUMN `lastHatchingSyncAt` INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE `sync_state` ADD COLUMN `lastHatchingLogSyncAt` INTEGER NOT NULL DEFAULT 0")
            }
        }

        // Add deathsCount column to farmer_dashboard_snapshots
        val MIGRATION_21_22 = object : Migration(21, 22) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE `farmer_dashboard_snapshots` ADD COLUMN `deathsCount` INTEGER NOT NULL DEFAULT 0")
            }
        }

        // Add enthusiast-specific breeding entities and dashboard snapshots
        val MIGRATION_22_23 = object : Migration(22, 23) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // mating_logs
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `mating_logs` (" +
                        "`logId` TEXT NOT NULL, `pairId` TEXT NOT NULL, `farmerId` TEXT NOT NULL, `matedAt` INTEGER NOT NULL, " +
                        "`observedBehavior` TEXT, `environmentalConditions` TEXT, `notes` TEXT, " +
                        "`createdAt` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL, `dirty` INTEGER NOT NULL, `syncedAt` INTEGER, " +
                        "PRIMARY KEY(`logId`), " +
                        "FOREIGN KEY(`pairId`) REFERENCES `breeding_pairs`(`pairId`) ON UPDATE NO ACTION ON DELETE CASCADE)"
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_mating_logs_pairId` ON `mating_logs` (`pairId`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_mating_logs_farmerId` ON `mating_logs` (`farmerId`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_mating_logs_matedAt` ON `mating_logs` (`matedAt`)")

                // egg_collections
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `egg_collections` (" +
                        "`collectionId` TEXT NOT NULL, `pairId` TEXT NOT NULL, `farmerId` TEXT NOT NULL, `eggsCollected` INTEGER NOT NULL, `collectedAt` INTEGER NOT NULL, " +
                        "`qualityGrade` TEXT NOT NULL, `weight` REAL, `notes` TEXT, " +
                        "`createdAt` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL, `dirty` INTEGER NOT NULL, `syncedAt` INTEGER, " +
                        "PRIMARY KEY(`collectionId`), " +
                        "FOREIGN KEY(`pairId`) REFERENCES `breeding_pairs`(`pairId`) ON UPDATE NO ACTION ON DELETE CASCADE)"
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_egg_collections_pairId` ON `egg_collections` (`pairId`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_egg_collections_farmerId` ON `egg_collections` (`farmerId`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_egg_collections_collectedAt` ON `egg_collections` (`collectedAt`)")

                // enthusiast_dashboard_snapshots
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `enthusiast_dashboard_snapshots` (" +
                        "`snapshotId` TEXT NOT NULL, `userId` TEXT NOT NULL, `weekStartAt` INTEGER NOT NULL, `weekEndAt` INTEGER NOT NULL, " +
                        "`hatchRateLast30Days` REAL NOT NULL, `breederSuccessRate` REAL NOT NULL, `disputedTransfersCount` INTEGER NOT NULL, `topBloodlinesEngagement` TEXT, " +
                        "`activePairsCount` INTEGER NOT NULL, `eggsCollectedCount` INTEGER NOT NULL, `hatchingDueCount` INTEGER NOT NULL, `transfersPendingCount` INTEGER NOT NULL, " +
                        "`createdAt` INTEGER NOT NULL, `dirty` INTEGER NOT NULL, `syncedAt` INTEGER, " +
                        "PRIMARY KEY(`snapshotId`))"
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_enthusiast_dashboard_snapshots_userId` ON `enthusiast_dashboard_snapshots` (`userId`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_enthusiast_dashboard_snapshots_weekStartAt` ON `enthusiast_dashboard_snapshots` (`weekStartAt`)")
            }
        }

        // Add eggsCount and sourceCollectionId to hatching_batches (23 -> 24)
        val MIGRATION_23_24 = object : Migration(23, 24) {
            override fun migrate(db: SupportSQLiteDatabase) {
                try { db.execSQL("ALTER TABLE `hatching_batches` ADD COLUMN `eggsCount` INTEGER") } catch (_: Exception) {}
                try { db.execSQL("ALTER TABLE `hatching_batches` ADD COLUMN `sourceCollectionId` TEXT") } catch (_: Exception) {}
            }
        }

        // Add updatedAt to enthusiast_dashboard_snapshots (25 -> 26)
        val MIGRATION_25_26 = object : Migration(25, 26) {
            override fun migrate(db: SupportSQLiteDatabase) {
                try { db.execSQL("ALTER TABLE `enthusiast_dashboard_snapshots` ADD COLUMN `updatedAt` INTEGER NOT NULL DEFAULT 0") } catch (_: Exception) {}
                // Backfill updatedAt = createdAt where possible
                try { db.execSQL("UPDATE enthusiast_dashboard_snapshots SET updatedAt = createdAt WHERE updatedAt = 0") } catch (_: Exception) {}
            }
        }
    }
}
