package com.rio.rostry.di

import android.content.Context
import androidx.room.Room
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.gson.Gson
import com.rio.rostry.data.local.RostryDatabase
import com.rio.rostry.data.local.UserDao
import com.rio.rostry.data.local.FowlDao
import com.rio.rostry.data.local.FowlRecordDao
import com.rio.rostry.data.local.TransferDao
import com.rio.rostry.data.local.MarketplaceDao
import com.rio.rostry.data.local.migrations.MIGRATION_8_9
import com.rio.rostry.data.repo.FowlRepository
import com.rio.rostry.data.repo.FowlRepositoryImpl
import com.rio.rostry.data.repo.StorageRepository
import com.rio.rostry.data.repo.StorageRepositoryImpl
import com.rio.rostry.data.repo.UserRepository
import com.rio.rostry.data.repo.UserRepositoryImpl
import com.rio.rostry.data.repo.MarketplaceRepository
import com.rio.rostry.data.repo.MarketplaceRepositoryImpl
import com.rio.rostry.data.repo.ProfileRepository
import com.rio.rostry.data.repo.ProfileRepositoryImpl
import com.rio.rostry.utils.NetworkMonitor
import com.rio.rostry.utils.NetworkMonitorImpl
import com.rio.rostry.utils.PerformanceLogger
import com.rio.rostry.data.profile.SpecialtiesProvider
import com.rio.rostry.data.profile.DefaultSpecialtiesProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
@Suppress("UNUSED_PARAMETER", "unused") // Suppress warnings for Hilt module
object AppModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore {
        return Firebase.firestore
    }

    @Provides
    @Singleton
    fun provideFirebaseFunctions(): FirebaseFunctions {
        return Firebase.functions
    }

    @Provides
    @Singleton
    fun provideFirebaseStorage(): FirebaseStorage {
        return Firebase.storage
    }

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return Gson()
    }

    @Provides
    @Singleton
    fun provideFirebaseAnalytics(@ApplicationContext context: Context): FirebaseAnalytics {
        return FirebaseAnalytics.getInstance(context)
    }

    @Provides
    @Singleton
    fun provideRostryDatabase(@ApplicationContext context: Context): RostryDatabase {
        return Room.databaseBuilder(
            context,
            RostryDatabase::class.java,
            "rostry_database"
        ).addMigrations(MIGRATION_8_9).build()
    }

    @Provides
    @Singleton
    fun provideUserDao(database: RostryDatabase): UserDao = database.userDao()

    @Provides
    @Singleton
    fun provideFowlDao(database: RostryDatabase): FowlDao = database.fowlDao()

    @Provides
    @Singleton
    fun provideFowlRecordDao(database: RostryDatabase): FowlRecordDao = database.fowlRecordDao()

    @Provides
    @Singleton
    fun provideTransferDao(database: RostryDatabase): TransferDao = database.transferDao()

    @Provides
    @Singleton
    fun provideMarketplaceDao(database: RostryDatabase): MarketplaceDao = database.marketplaceDao()

    @Provides
    @Singleton
    fun provideSpecialtiesProvider(): SpecialtiesProvider = DefaultSpecialtiesProvider()

    @Provides
    @Singleton
    fun provideUserRepository(
        auth: FirebaseAuth,
        firestore: FirebaseFirestore,
        userDao: UserDao,
        networkMonitor: NetworkMonitor,
        performanceLogger: PerformanceLogger
    ): UserRepository = UserRepositoryImpl(auth, firestore, userDao, networkMonitor, performanceLogger)

    @Provides
    @Singleton
    fun provideFowlRepository(
        fowlDao: FowlDao,
        fowlRecordDao: FowlRecordDao,
        firestore: FirebaseFirestore,
        auth: FirebaseAuth,
        networkMonitor: NetworkMonitor,
        performanceLogger: PerformanceLogger
    ): FowlRepository = FowlRepositoryImpl(fowlDao, fowlRecordDao, firestore, auth, networkMonitor, performanceLogger)

    @Provides
    @Singleton
    fun provideStorageRepository(
        storage: FirebaseStorage,
        @ApplicationContext context: Context,
        networkMonitor: NetworkMonitor,
        performanceLogger: PerformanceLogger
    ): StorageRepository = StorageRepositoryImpl(storage, context, networkMonitor, performanceLogger)

    @Provides
    @Singleton
    fun provideMarketplaceRepository(
        firestore: FirebaseFirestore,
        auth: FirebaseAuth,
        marketplaceDao: MarketplaceDao
    ): MarketplaceRepository = MarketplaceRepositoryImpl(firestore, auth, marketplaceDao)

    @Provides
    @Singleton
    fun provideProfileRepository(
        auth: FirebaseAuth,
        firestore: FirebaseFirestore,
        functions: FirebaseFunctions,
        storageRepository: StorageRepository
    ): ProfileRepository = ProfileRepositoryImpl(auth, firestore, functions, storageRepository)

    @Provides
    @Singleton
    fun provideNetworkMonitor(@ApplicationContext context: Context): NetworkMonitor = NetworkMonitorImpl(context)

    @Provides
    @Singleton
    fun providePerformanceLogger(firestore: FirebaseFirestore, auth: FirebaseAuth): PerformanceLogger = PerformanceLogger(firestore, auth)

}
