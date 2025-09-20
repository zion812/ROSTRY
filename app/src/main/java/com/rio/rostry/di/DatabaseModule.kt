package com.rio.rostry.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import com.rio.rostry.data.local.RostryDatabase
import com.rio.rostry.data.local.migrations.Migration1To2
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    private val MIGRATIONS: Array<Migration> = arrayOf(
        Migration1To2
    )

    @Provides
    @Singleton
    fun provideRostryDatabase(@ApplicationContext context: Context): RostryDatabase {
        return Room.databaseBuilder(
            context,
            RostryDatabase::class.java,
            "rostry_database"
        ).addMigrations(*MIGRATIONS)
        .build()
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
    
    @Provides
    @Singleton
    fun provideProductTrackingDao(database: RostryDatabase) = database.productTrackingDao()
    
    @Provides
    @Singleton
    fun provideFamilyTreeDao(database: RostryDatabase) = database.familyTreeDao()
    
    @Provides
    @Singleton
    fun provideChatMessageDao(database: RostryDatabase) = database.chatMessageDao()
    
    // New DAOs for poultry traceability system
    @Provides
    @Singleton
    fun providePoultryDao(database: RostryDatabase) = database.poultryDao()
    
    @Provides
    @Singleton
    fun provideBreedingRecordDao(database: RostryDatabase) = database.breedingRecordDao()
    
    @Provides
    @Singleton
    fun provideGeneticTraitDao(database: RostryDatabase) = database.geneticTraitDao()
    
    @Provides
    @Singleton
    fun providePoultryTraitDao(database: RostryDatabase) = database.poultryTraitDao()
    
    @Provides
    @Singleton
    fun provideLifecycleMilestoneDao(database: RostryDatabase) = database.lifecycleMilestoneDao()
    
    @Provides
    @Singleton
    fun provideVaccinationRecordDao(database: RostryDatabase) = database.vaccinationRecordDao()
    
    @Provides
    @Singleton
    fun provideTransferRecordDao(database: RostryDatabase) = database.transferRecordDao()
}