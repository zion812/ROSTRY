package com.rio.rostry.di

import android.content.Context
import androidx.room.Room
import com.rio.rostry.data.local.RostryDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideRostryDatabase(@ApplicationContext context: Context): RostryDatabase {
        return Room.databaseBuilder(
            context,
            RostryDatabase::class.java,
            "rostry_database"
        ).build()
    }

    @Provides
    @Singleton
    fun provideUserDao(database: RostryDatabase) = database.userDao()

    @Provides
    @Singleton
    fun provideProductDao(database: RostryDatabase) = database.productDao()

    @Provides
    @Singleton
    fun provideOrderDao(database: RostryDatabase) = database.orderDao()

    @Provides
    @Singleton
    fun provideTransferDao(database: RostryDatabase) = database.transferDao()

    @Provides
    @Singleton
    fun provideCoinDao(database: RostryDatabase) = database.coinDao()

    @Provides
    @Singleton
    fun provideNotificationDao(database: RostryDatabase) = database.notificationDao()
}