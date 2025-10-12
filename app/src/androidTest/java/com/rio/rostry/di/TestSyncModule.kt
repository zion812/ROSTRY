package com.rio.rostry.di

import android.content.Context
import androidx.room.Room
import com.rio.rostry.data.database.AppDatabase
import com.rio.rostry.data.sync.SyncRemote
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [DatabaseModule::class]
)
object TestDatabaseModule {

    @Provides
    @Singleton
    fun provideInMemoryDb(@ApplicationContext context: Context): AppDatabase =
        Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()

    // Re-expose all needed DAOs the same way production module does
    @Provides @Singleton fun provideUserDao(db: AppDatabase) = db.userDao()
    @Provides @Singleton fun provideProductDao(db: AppDatabase) = db.productDao()
    @Provides @Singleton fun provideOrderDao(db: AppDatabase) = db.orderDao()
    @Provides @Singleton fun provideTransferDao(db: AppDatabase) = db.transferDao()
    @Provides @Singleton fun provideOutboxDao(db: AppDatabase) = db.outboxDao()
    @Provides @Singleton fun provideDailyLogDao(db: AppDatabase) = db.dailyLogDao()
    @Provides @Singleton fun provideTaskDao(db: AppDatabase) = db.taskDao()
    @Provides @Singleton fun provideUploadTaskDao(db: AppDatabase) = db.uploadTaskDao()
    // Farm monitoring DAOs used by tests
    @Provides @Singleton fun provideHatchingBatchDao(db: AppDatabase) = db.hatchingBatchDao()
    @Provides @Singleton fun provideHatchingLogDao(db: AppDatabase) = db.hatchingLogDao()
    @Provides @Singleton fun provideEggCollectionDao(db: AppDatabase) = db.eggCollectionDao()
    @Provides @Singleton fun provideBreedingPairDao(db: AppDatabase) = db.breedingPairDao()
}

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [RemoteModule::class]
)
abstract class TestRepositoryBindingsModule {
    @Binds
    @Singleton
    abstract fun bindSyncRemote(impl: com.rio.rostry.test.fakes.FakeSyncRemote): SyncRemote
}
