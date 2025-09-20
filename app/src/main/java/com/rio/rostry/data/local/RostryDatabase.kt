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
import com.rio.rostry.data.model.Poultry
import com.rio.rostry.data.model.BreedingRecord
import com.rio.rostry.data.model.GeneticTrait
import com.rio.rostry.data.model.PoultryTrait
import com.rio.rostry.data.model.LifecycleMilestone
import com.rio.rostry.data.model.VaccinationRecord
import com.rio.rostry.data.model.TransferRecord

@Database(
    entities = [User::class, Product::class, Order::class, Transfer::class, Coin::class, Notification::class, 
               ProductTracking::class, FamilyTree::class, ChatMessage::class, Poultry::class, BreedingRecord::class,
               GeneticTrait::class, PoultryTrait::class, LifecycleMilestone::class, VaccinationRecord::class,
               TransferRecord::class],
    version = 2,
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
    
    // New DAOs for poultry traceability system
    abstract fun poultryDao(): PoultryDao
    abstract fun breedingRecordDao(): BreedingRecordDao
    abstract fun geneticTraitDao(): GeneticTraitDao
    abstract fun poultryTraitDao(): PoultryTraitDao
    abstract fun lifecycleMilestoneDao(): LifecycleMilestoneDao
    abstract fun vaccinationRecordDao(): VaccinationRecordDao
    abstract fun transferRecordDao(): TransferRecordDao
}