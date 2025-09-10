package com.rio.rostry.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.rio.rostry.data.local.entities.CoinTransactionEntity
import com.rio.rostry.data.local.entities.NotificationEntity
import com.rio.rostry.data.local.entities.OrderEntity
import com.rio.rostry.data.local.entities.ProductEntity
import com.rio.rostry.data.local.entities.TransferEntity
import com.rio.rostry.data.local.entities.UserEntity
import com.rio.rostry.data.local.dao.CoinDao
import com.rio.rostry.data.local.dao.NotificationDao
import com.rio.rostry.data.local.dao.OrderDao
import com.rio.rostry.data.local.dao.ProductDao
import com.rio.rostry.data.local.dao.TransferDao
import com.rio.rostry.data.local.dao.UserDao
import com.rio.rostry.data.local.dao.ProductTrackingDao
import com.rio.rostry.data.local.dao.FamilyTreeDao
import com.rio.rostry.data.local.dao.ChatMessageDao
import com.rio.rostry.data.local.entities.ProductTrackingEntity
import com.rio.rostry.data.local.entities.FamilyTreeEntity
import com.rio.rostry.data.local.entities.ChatMessageEntity
import com.rio.rostry.data.local.entities.SyncStateEntity
import com.rio.rostry.data.local.dao.SyncStateDao

@Database(
    entities = [
        OutboxEntity::class,
        UserEntity::class,
        ProductEntity::class,
        OrderEntity::class,
        TransferEntity::class,
        CoinTransactionEntity::class,
        NotificationEntity::class,
        ProductTrackingEntity::class,
        FamilyTreeEntity::class,
        ChatMessageEntity::class,
        SyncStateEntity::class
    ],
    version = 4,
    exportSchema = false
)
@TypeConverters(OutboxTypeConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun outboxDao(): OutboxDao
    abstract fun userDao(): UserDao
    abstract fun productDao(): ProductDao
    abstract fun orderDao(): OrderDao
    abstract fun transferDao(): TransferDao
    abstract fun coinDao(): CoinDao
    abstract fun notificationDao(): NotificationDao
    abstract fun productTrackingDao(): ProductTrackingDao
    abstract fun familyTreeDao(): FamilyTreeDao
    abstract fun chatMessageDao(): ChatMessageDao
    abstract fun syncStateDao(): SyncStateDao
}

