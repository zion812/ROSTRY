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
        WishlistEntity::class
    ],
    version = 7, // Bumped to 7 for marketplace features and product columns
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
    }
}
