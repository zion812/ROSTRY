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

@Database(
    entities = [
        UserEntity::class,
        ProductEntity::class,
        OrderEntity::class,
        OrderItemEntity::class,
        TransferEntity::class,
        CoinEntity::class,
        NotificationEntity::class
    ],
    version = 2, // Bumped to 2 after adding fields to UserEntity
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
    }
}
