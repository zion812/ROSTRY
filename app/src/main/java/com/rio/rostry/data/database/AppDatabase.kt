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
        EventRsvpEntity::class
    ],
    version = 13, // Bumped to 13 adding social platform tables
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

    object Converters {
        @TypeConverter
        @JvmStatic
        fun fromStringList(value: List<String>?): String? {
            return value?.let { Gson().toJson(it) }
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
            }
        }
    }
}
