package com.rio.rostry.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.rio.rostry.data.models.Converters
import com.rio.rostry.data.models.Fowl
import com.rio.rostry.data.models.FowlRecord
import com.rio.rostry.data.models.Transfer
import com.rio.rostry.data.models.User
import com.rio.rostry.data.models.market.*

@Database(
    entities = [
        User::class, Fowl::class, FowlRecord::class, Transfer::class,
        MarketplaceListing::class, Conversation::class, Message::class, WishlistItem::class, CartItem::class
    ],
    version = 9, exportSchema = false
)
@TypeConverters(Converters::class)
abstract class RostryDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun fowlDao(): FowlDao
    abstract fun fowlRecordDao(): FowlRecordDao
    abstract fun transferDao(): TransferDao
    abstract fun marketplaceDao(): MarketplaceDao
}
