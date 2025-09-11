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
import com.rio.rostry.data.local.entities.CartEntity
import com.rio.rostry.data.local.entities.AuctionProductEntity
import com.rio.rostry.data.local.entities.BidEntity
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
import com.rio.rostry.data.local.dao.CartDao
import com.rio.rostry.data.local.dao.AuctionDao
import com.rio.rostry.data.local.dao.BidDao
import com.rio.rostry.data.local.entities.PaymentEntity
import com.rio.rostry.data.local.entities.HubEntity
import com.rio.rostry.data.local.entities.DeliveryAssignmentEntity
import com.rio.rostry.data.local.entities.OrderEventEntity
import com.rio.rostry.data.local.entities.InvoiceEntity
import com.rio.rostry.data.local.dao.PaymentDao
import com.rio.rostry.data.local.dao.HubDao
import com.rio.rostry.data.local.dao.DeliveryAssignmentDao
import com.rio.rostry.data.local.dao.OrderEventDao
import com.rio.rostry.data.local.dao.InvoiceDao
import com.rio.rostry.data.local.dao.OrderItemDao
import com.rio.rostry.data.local.dao.RefundDao
import com.rio.rostry.data.local.entities.OrderItemEntity
import com.rio.rostry.data.local.entities.RefundEntity
import com.rio.rostry.data.local.dao.BreedingRecordDao
import com.rio.rostry.data.local.dao.TraitDao
import com.rio.rostry.data.local.dao.LifecycleEntryDao
import com.rio.rostry.data.local.dao.LineageCacheDao
import com.rio.rostry.data.local.entities.BreedingRecordEntity
import com.rio.rostry.data.local.entities.BreedingOffspringCrossRefEntity
import com.rio.rostry.data.local.entities.TraitEntity
import com.rio.rostry.data.local.entities.ProductTraitCrossRefEntity
import com.rio.rostry.data.local.entities.LifecycleEntryEntity
import com.rio.rostry.data.local.entities.LineageCacheEntity

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
        SyncStateEntity::class,
        CartEntity::class,
        AuctionProductEntity::class,
        BidEntity::class,
        PaymentEntity::class,
        HubEntity::class,
        DeliveryAssignmentEntity::class,
        OrderEventEntity::class,
        InvoiceEntity::class,
        OrderItemEntity::class,
        RefundEntity::class,
        // Traceability schema additions
        BreedingRecordEntity::class,
        BreedingOffspringCrossRefEntity::class,
        TraitEntity::class,
        ProductTraitCrossRefEntity::class,
        LifecycleEntryEntity::class,
        LineageCacheEntity::class,
    ],
    version = 10,
    exportSchema = false
)
@TypeConverters(OutboxTypeConverters::class, ListTypeConverters::class)
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
    abstract fun cartDao(): CartDao
    abstract fun auctionDao(): AuctionDao
    abstract fun bidDao(): BidDao
    abstract fun paymentDao(): PaymentDao
    abstract fun hubDao(): HubDao
    abstract fun deliveryAssignmentDao(): DeliveryAssignmentDao
    abstract fun orderEventDao(): OrderEventDao
    abstract fun invoiceDao(): InvoiceDao
    abstract fun orderItemDao(): OrderItemDao
    abstract fun refundDao(): RefundDao
    // Traceability DAOs
    abstract fun breedingRecordDao(): BreedingRecordDao
    abstract fun traitDao(): TraitDao
    abstract fun lifecycleEntryDao(): LifecycleEntryDao
    abstract fun lineageCacheDao(): LineageCacheDao
}

