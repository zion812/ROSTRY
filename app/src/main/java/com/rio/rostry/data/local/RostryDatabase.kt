package com.rio.rostry.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.rio.rostry.data.models.Fowl
import com.rio.rostry.data.models.FowlRecord
import com.rio.rostry.data.models.FowlTransfer
import com.rio.rostry.data.models.User
import com.rio.rostry.data.models.market.*

@Database(
    entities = [
        User::class, Fowl::class, FowlRecord::class, FowlTransfer::class,
        MarketplaceListing::class, Conversation::class, Message::class, WishlistItem::class, CartItem::class
    ],
    version = 4, exportSchema = false
)
@TypeConverters(Converters::class)
abstract class RostryDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun fowlDao(): FowlDao
    abstract fun fowlRecordDao(): FowlRecordDao
    abstract fun fowlTransferDao(): FowlTransferDao
    abstract fun marketplaceDao(): MarketplaceDao
}
