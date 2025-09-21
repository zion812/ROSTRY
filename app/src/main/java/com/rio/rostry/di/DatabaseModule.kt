package com.rio.rostry.di

import android.content.Context
import androidx.room.Room
import com.rio.rostry.data.database.AppDatabase
import com.rio.rostry.data.database.dao.CoinDao
import com.rio.rostry.data.database.dao.NotificationDao
import com.rio.rostry.data.database.dao.OrderDao
import com.rio.rostry.data.database.dao.ProductDao
import com.rio.rostry.data.database.dao.TransferDao
import com.rio.rostry.data.database.dao.UserDao
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
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME
        )
        .fallbackToDestructiveMigration() // Define a proper migration strategy for production
        .build()
    }

    @Provides
    @Singleton
    fun provideUserDao(appDatabase: AppDatabase): UserDao {
        return appDatabase.userDao()
    }

    @Provides
    @Singleton
    fun provideProductDao(appDatabase: AppDatabase): ProductDao {
        return appDatabase.productDao()
    }

    @Provides
    @Singleton
    fun provideOrderDao(appDatabase: AppDatabase): OrderDao {
        return appDatabase.orderDao()
    }

    @Provides
    @Singleton
    fun provideTransferDao(appDatabase: AppDatabase): TransferDao {
        return appDatabase.transferDao()
    }

    @Provides
    @Singleton
    fun provideCoinDao(appDatabase: AppDatabase): CoinDao {
        return appDatabase.coinDao()
    }

    @Provides
    @Singleton
    fun provideNotificationDao(appDatabase: AppDatabase): NotificationDao {
        return appDatabase.notificationDao()
    }
}
