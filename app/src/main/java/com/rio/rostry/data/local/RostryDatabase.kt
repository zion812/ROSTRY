package com.rio.rostry.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.rio.rostry.data.model.User
import com.rio.rostry.data.model.Product
import com.rio.rostry.data.model.Order
import com.rio.rostry.data.model.Transfer
import com.rio.rostry.data.model.Coin
import com.rio.rostry.data.model.Notification
import com.rio.rostry.data.model.ProductTracking
import com.rio.rostry.data.model.FamilyTree
import com.rio.rostry.data.model.ChatMessage

@Database(
    entities = [User::class, Product::class, Order::class, Transfer::class, Coin::class, Notification::class, 
               ProductTracking::class, FamilyTree::class, ChatMessage::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class RostryDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun productDao(): ProductDao
    abstract fun orderDao(): OrderDao
    abstract fun transferDao(): TransferDao
    abstract fun coinDao(): CoinDao
    abstract fun notificationDao(): NotificationDao
    abstract fun productTrackingDao(): ProductTrackingDao
    abstract fun familyTreeDao(): FamilyTreeDao
    abstract fun chatMessageDao(): ChatMessageDao
}