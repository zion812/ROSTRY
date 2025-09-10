package com.rio.rostry.data.di

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import net.sqlcipher.database.SupportFactory
import net.sqlcipher.database.SQLiteDatabase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.storage.FirebaseStorage
import com.rio.rostry.data.local.db.AppDatabase
import com.rio.rostry.data.local.db.OutboxDao
import com.rio.rostry.data.auth.AuthRepositoryImpl
import com.rio.rostry.data.repository.OutboxRepositoryImpl
import com.rio.rostry.data.rbac.RbacServiceImpl
import com.rio.rostry.data.session.SessionManagerImpl
import com.rio.rostry.data.profile.UserProfileRepositoryImpl
import com.rio.rostry.domain.auth.AuthRepository
import com.rio.rostry.domain.repository.OutboxRepository
import com.rio.rostry.domain.rbac.RbacService
import com.rio.rostry.domain.session.SessionManager
import com.rio.rostry.domain.profile.UserProfileRepository
// Using fully qualified names for migrations below to avoid import resolution issues
import com.rio.rostry.data.local.dao.SyncStateDao
import com.rio.rostry.data.local.dao.ProductDao
import com.rio.rostry.data.local.dao.OrderDao
import com.rio.rostry.data.local.dao.TransferDao
import com.rio.rostry.domain.sync.SyncManager
import com.rio.rostry.data.sync.SyncManagerImpl
import com.rio.rostry.domain.product.ProductRepository
import com.rio.rostry.data.product.ProductRepositoryImpl
import com.rio.rostry.data.remote.FunctionsApi
import com.rio.rostry.domain.order.OrderRepository
import com.rio.rostry.data.order.OrderRepositoryImpl
import com.rio.rostry.data.transfer.TransferRepositoryImpl
import com.rio.rostry.domain.transfer.TransferRepository
import com.rio.rostry.data.local.dao.NotificationDao
import com.rio.rostry.domain.notification.NotificationRepository
import com.rio.rostry.data.notification.NotificationRepositoryImpl
import com.rio.rostry.data.chat.ChatRepositoryImpl
import com.rio.rostry.domain.chat.ChatRepository
import com.rio.rostry.data.local.dao.ChatMessageDao
import com.rio.rostry.data.local.dao.ProductTrackingDao
import com.rio.rostry.domain.producttracking.ProductTrackingRepository
import com.rio.rostry.data.producttracking.ProductTrackingRepositoryImpl
import com.rio.rostry.data.local.dao.FamilyTreeDao
import com.rio.rostry.domain.familytree.FamilyTreeRepository
import com.rio.rostry.data.familytree.FamilyTreeRepositoryImpl
import com.rio.rostry.data.security.KeyProvider
import com.rio.rostry.data.listing.ProductListingService
import com.rio.rostry.domain.product.ListingUseCase
import com.rio.rostry.data.product.ListingUseCaseImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context,
        factory: SupportFactory,
    ): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "rostry.db")
            .addMigrations(
                com.rio.rostry.data.local.migrations.MIGRATION_2_3,
                com.rio.rostry.data.local.migrations.MIGRATION_3_4,
                com.rio.rostry.data.local.migrations.MIGRATION_4_5,
                com.rio.rostry.data.local.migrations.MIGRATION_5_6,
                com.rio.rostry.data.local.migrations.MIGRATION_6_7,
            )
            .openHelperFactory(factory)
            .build()

    @Provides
    fun provideOutboxDao(db: AppDatabase): OutboxDao = db.outboxDao()

    @Provides
    fun provideSyncStateDao(db: AppDatabase): SyncStateDao = db.syncStateDao()

    @Provides
    fun provideCartDao(db: AppDatabase): com.rio.rostry.data.local.dao.CartDao = db.cartDao()

    @Provides
    fun provideProductDao(db: AppDatabase): ProductDao = db.productDao()

    @Provides
    fun provideOrderDao(db: AppDatabase): OrderDao = db.orderDao()

    @Provides
    fun provideTransferDao(db: AppDatabase): TransferDao = db.transferDao()

    @Provides
    fun provideChatMessageDao(db: AppDatabase): ChatMessageDao = db.chatMessageDao()

    @Provides
    fun provideProductTrackingDao(db: AppDatabase): ProductTrackingDao = db.productTrackingDao()

    @Provides
    fun provideFamilyTreeDao(db: AppDatabase): FamilyTreeDao = db.familyTreeDao()

    @Provides
    @Singleton
    fun provideOutboxRepository(dao: OutboxDao): OutboxRepository = OutboxRepositoryImpl(dao)

    // Preferences for session management
    @Provides
    @Singleton
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences =
        context.getSharedPreferences("rostry_session", Context.MODE_PRIVATE)

    // SQLCipher SupportFactory using a keystore-protected passphrase
    @Provides
    @Singleton
    fun provideSqlCipherFactory(
        @ApplicationContext context: Context,
        keyProvider: KeyProvider,
    ): SupportFactory {
        SQLiteDatabase.loadLibs(context)
        val passphrase = keyProvider.getOrCreateDatabaseKey()
        return SupportFactory(passphrase)
    }

    @Provides
    @Singleton
    fun provideSessionManager(prefs: SharedPreferences): SessionManager = SessionManagerImpl(prefs)

    @Provides
    @Singleton
    fun provideRbacService(): RbacService = RbacServiceImpl()

    @Provides
    @Singleton
    fun provideProductListingService(
        storage: FirebaseStorage,
        imageCompressor: com.rio.rostry.data.image.ImageCompressor,
        productRepository: com.rio.rostry.domain.product.ProductRepository,
    ): ProductListingService = ProductListingService(storage, imageCompressor, productRepository)

    @Provides
    @Singleton
    fun provideListingUseCase(impl: ListingUseCaseImpl): ListingUseCase = impl

    @Provides
    @Singleton
    fun provideAuthRepository(
        auth: FirebaseAuth,
        firestore: FirebaseFirestore,
    ): AuthRepository = AuthRepositoryImpl(auth, firestore)

    @Provides
    @Singleton
    fun provideUserProfileRepository(
        firestore: FirebaseFirestore,
    ): UserProfileRepository = UserProfileRepositoryImpl(firestore)

    // Sync & repositories
    @Provides
    @Singleton
    fun provideSyncManager(
        syncStateDao: SyncStateDao,
        outboxDao: OutboxDao,
        functionsApi: FunctionsApi,
        productDao: ProductDao,
        orderDao: OrderDao,
        transferDao: TransferDao,
        chatMessageDao: ChatMessageDao,
        notificationDao: NotificationDao,
        productTrackingDao: ProductTrackingDao,
        familyTreeDao: FamilyTreeDao,
    ): SyncManager = SyncManagerImpl(
        syncStateDao,
        outboxDao,
        functionsApi,
        productDao,
        orderDao,
        transferDao,
        notificationDao,
        chatMessageDao,
        productTrackingDao,
        familyTreeDao,
    )

    @Provides
    @Singleton
    fun provideProductRepository(
        productDao: ProductDao,
        outboxRepository: OutboxRepository,
        @ApplicationContext context: Context,
        staleRefresher: com.rio.rostry.data.cache.StaleRefresher,
    ): ProductRepository = ProductRepositoryImpl(productDao, outboxRepository, context, staleRefresher)

    @Provides
    @Singleton
    fun provideOrderRepository(
        orderDao: OrderDao,
        outboxRepository: OutboxRepository,
        @ApplicationContext context: Context,
        staleRefresher: com.rio.rostry.data.cache.StaleRefresher,
    ): OrderRepository = OrderRepositoryImpl(orderDao, outboxRepository, context, staleRefresher)

    @Provides
    @Singleton
    fun provideFunctionsApi(functions: FirebaseFunctions): FunctionsApi = FunctionsApi(functions)

    @Provides
    @Singleton
    fun provideTransferRepository(
        transferDao: TransferDao,
        outboxRepository: OutboxRepository,
        @ApplicationContext context: Context,
        staleRefresher: com.rio.rostry.data.cache.StaleRefresher,
    ): TransferRepository = TransferRepositoryImpl(transferDao, outboxRepository, context, staleRefresher)

    @Provides
    fun provideNotificationDao(db: AppDatabase): NotificationDao = db.notificationDao()

    @Provides
    @Singleton
    fun provideNotificationRepository(
        notificationDao: NotificationDao,
        staleRefresher: com.rio.rostry.data.cache.StaleRefresher,
    ): NotificationRepository = NotificationRepositoryImpl(notificationDao, staleRefresher)

    @Provides
    @Singleton
    fun provideChatRepository(
        chatMessageDao: ChatMessageDao,
        outboxRepository: OutboxRepository,
        @ApplicationContext context: Context,
        staleRefresher: com.rio.rostry.data.cache.StaleRefresher,
    ): ChatRepository = ChatRepositoryImpl(chatMessageDao, outboxRepository, context, staleRefresher)

    @Provides
    @Singleton
    fun provideProductTrackingRepository(
        productTrackingDao: ProductTrackingDao,
        outboxRepository: OutboxRepository,
        @ApplicationContext context: Context,
        staleRefresher: com.rio.rostry.data.cache.StaleRefresher,
    ): ProductTrackingRepository = ProductTrackingRepositoryImpl(productTrackingDao, outboxRepository, context, staleRefresher)

    @Provides
    @Singleton
    fun provideFamilyTreeRepository(
        familyTreeDao: FamilyTreeDao,
        outboxRepository: OutboxRepository,
        @ApplicationContext context: Context,
        staleRefresher: com.rio.rostry.data.cache.StaleRefresher,
    ): FamilyTreeRepository = FamilyTreeRepositoryImpl(familyTreeDao, outboxRepository, context, staleRefresher)

    @Provides
    fun provideAuctionDao(db: AppDatabase): com.rio.rostry.data.local.dao.AuctionDao = db.auctionDao()

    @Provides
    fun provideBidDao(db: AppDatabase): com.rio.rostry.data.local.dao.BidDao = db.bidDao()

    @Provides
    @Singleton
    fun provideBiddingRepository(
        auctionDao: com.rio.rostry.data.local.dao.AuctionDao,
        bidDao: com.rio.rostry.data.local.dao.BidDao,
        functionsApi: com.rio.rostry.data.remote.FunctionsApi,
        productDao: ProductDao,
    ): com.rio.rostry.domain.bidding.BiddingRepository =
        com.rio.rostry.data.bidding.BiddingRepositoryImpl(auctionDao, bidDao, functionsApi, productDao)

    @Provides
    @Singleton
    fun provideCartRepository(
        cartDao: com.rio.rostry.data.local.dao.CartDao,
        productDao: com.rio.rostry.data.local.dao.ProductDao,
        orderRepository: com.rio.rostry.domain.order.OrderRepository,
    ): com.rio.rostry.domain.cart.CartRepository =
        com.rio.rostry.data.cart.CartRepositoryImpl(cartDao, productDao, orderRepository)
}

