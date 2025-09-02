package com.rio.rostry.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.gson.Gson
import com.rio.rostry.data.location.LocationService
import com.rio.rostry.data.repo.*
import com.rio.rostry.data.profile.SpecialtiesProvider
import com.rio.rostry.data.profile.DefaultSpecialtiesProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import io.mockk.mockk
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [AppModule::class, RepositoryModule::class, ServiceModule::class, LocationModule::class]
)
object TestAppModule {

    @Provides
    @Singleton
    fun provideMarketplaceRepository(): MarketplaceRepository = mockk(relaxed = true)

    @Provides
    @Singleton
    fun provideMarketplaceV2Repository(): MarketplaceV2Repository = mockk(relaxed = true)

    @Provides
    @Singleton
    fun provideLocationService(): LocationService = mockk(relaxed = true)

    @Provides
    @Singleton
    fun provideSyncRepository(): SyncRepository = mockk(relaxed = true)

    @Provides
    @Singleton
    fun provideUserRepository(): UserRepository = mockk(relaxed = true)

    @Provides
    @Singleton
    fun provideFowlRepository(): FowlRepository = mockk(relaxed = true)

    @Provides
    @Singleton
    fun provideStorageRepository(): StorageRepository = mockk(relaxed = true)

    @Provides
    @Singleton
    fun provideAdminRepository(): AdminRepository = mockk(relaxed = true)

    @Provides
    @Singleton
    fun provideTransferRepository(): TransferRepository = mockk(relaxed = true)

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = mockk(relaxed = true)

    @Provides
    @Singleton
    fun provideAnalyticsRepository(): AnalyticsRepository = mockk(relaxed = true)

    // Additional providers required by workers/modules in androidTest graph
    @Provides
    @Singleton
    fun provideFirebaseFunctions(): FirebaseFunctions = mockk(relaxed = true)

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore = mockk(relaxed = true)

    @Provides
    @Singleton
    fun provideProfileRepository(): ProfileRepository = mockk(relaxed = true)

    @Provides
    @Singleton
    fun provideFirebaseAnalytics(): FirebaseAnalytics = mockk(relaxed = true)

    @Provides
    @Singleton
    fun provideSpecialtiesProvider(): SpecialtiesProvider = DefaultSpecialtiesProvider()

    @Provides
    @Singleton
    fun provideGson(): Gson = Gson()
}
