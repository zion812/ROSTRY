package com.rio.rostry.di

import com.rio.rostry.data.database.entity.ProductEntity
import com.rio.rostry.data.repository.*
import com.rio.rostry.data.repository.AuctionRepositoryImpl
import com.rio.rostry.ui.general.market.GeneralMarketViewModel
import com.rio.rostry.utils.Resource
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.testing.TestInstallIn
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Singleton

// Seeded catalog for marketplace tests
private val SeedCatalog: List<ProductEntity> = listOf(
    ProductEntity(productId = "product-1", name = "Product 1", sellerId = "seller-a", price = 100.0, location = "Delhi", breed = "broiler", quantity = 5.0, updatedAt = 1L),
    ProductEntity(productId = "product-2", name = "Product 2", sellerId = "seller-a", price = 250.0, location = "Delhi", breed = "layer", quantity = 2.0, updatedAt = 2L, familyTreeId = "ft-1"),
    ProductEntity(productId = "product-3", name = "Product 3", sellerId = "seller-b", price = 600.0, location = "Bengaluru", breed = "broiler", quantity = 12.0, updatedAt = 3L),
    ProductEntity(productId = "product-4", name = "Product 4", sellerId = "seller-c", price = 1500.0, location = "Chennai", breed = "kadaknath", quantity = 1.0, updatedAt = 4L, familyTreeId = "ft-2"),
    ProductEntity(productId = "product-5", name = "Product 5", sellerId = "seller-c", price = 80.0, location = "Pune", breed = "broiler", quantity = 25.0, updatedAt = 5L),
    ProductEntity(productId = "product-6", name = "Product 6", sellerId = "seller-d", price = 999.0, location = "Mumbai", breed = "layer", quantity = 9.0, updatedAt = 6L)
)

class FakeProductRepository @javax.inject.Inject constructor() : ProductRepository {
    private val productsFlow = MutableStateFlow<Resource<List<ProductEntity>>>(Resource.Success(SeedCatalog))

    override fun validateProductReferences(product: ProductEntity): Boolean = true
    override fun validateProductReferences(products: List<ProductEntity>): ProductRepository.ValidationResult =
        ProductRepository.ValidationResult(true, emptyList())

    override fun getAllProducts(): Flow<Resource<List<ProductEntity>>> = productsFlow
    override fun getProductById(productId: String): Flow<Resource<ProductEntity?>> =
        flowOf(Resource.Success(SeedCatalog.find { it.productId == productId }))
    override fun getProductsBySeller(sellerId: String): Flow<Resource<List<ProductEntity>>> =
        flowOf(Resource.Success(SeedCatalog.filter { it.sellerId == sellerId }))
    override fun getProductsByCategory(category: String): Flow<Resource<List<ProductEntity>>> = flowOf(Resource.Success(emptyList()))
    override suspend fun updateProduct(product: ProductEntity): Resource<Unit> = Resource.Success(Unit)
    override suspend fun deleteProduct(productId: String): Resource<Unit> = Resource.Success(Unit)
    override suspend fun syncProductsFromRemote(): Resource<Unit> = Resource.Success(Unit)
    override fun searchProducts(query: String): Flow<Resource<List<ProductEntity>>> =
        flowOf(Resource.Success(SeedCatalog.filter { it.name.contains(query, true) || (it.breed?.contains(query, true) == true) }))
    override suspend fun autocompleteProducts(prefix: String, limit: Int): List<ProductEntity> =
        SeedCatalog.filter { it.name.startsWith(prefix, true) }.take(limit)
    override suspend fun filterVerified(limit: Int, offset: Int): Resource<List<ProductEntity>> =
        Resource.Success(SeedCatalog.filter { it.sellerId.isNotBlank() }.drop(offset).take(limit))
    override suspend fun filterNearby(centerLat: Double, centerLng: Double, radiusKm: Double, limit: Int, offset: Int): Resource<List<ProductEntity>> =
        Resource.Success(SeedCatalog.drop(offset).take(limit))
    override suspend fun filterByBreed(breed: String?, minPrice: Double, maxPrice: Double, limit: Int, offset: Int): Resource<List<ProductEntity>> =
        Resource.Success(SeedCatalog.filter { (breed == null || it.breed?.equals(breed, true) == true) && it.price in minPrice..maxPrice }.drop(offset).take(limit))
    override suspend fun filterByAgeDays(minAgeDays: Int?, maxAgeDays: Int?, nowMillis: Long, limit: Int, offset: Int): Resource<List<ProductEntity>> =
        Resource.Success(SeedCatalog.drop(offset).take(limit))
    override suspend fun filterTraceable(onlyTraceable: Boolean, base: List<ProductEntity>?): Resource<List<ProductEntity>> =
        Resource.Success((base ?: SeedCatalog).filter { it.familyTreeId != null })
    override suspend fun backfillBirdCodes(): Resource<Int> = Resource.Success(0)
    override suspend fun updateQrCodeUrl(productId: String, url: String?, updatedAt: Long): Resource<Unit> = Resource.Success(Unit)

    override suspend fun findById(productId: String): ProductEntity? = SeedCatalog.find { it.productId == productId }

    override suspend fun upsert(product: ProductEntity): Resource<Unit> = Resource.Success(Unit)

    override fun observeActiveWithBirth(): Flow<List<ProductEntity>> = flowOf(SeedCatalog.filter { it.birthDate != null && !it.isDeleted })

    override fun observeRecentlyAddedForFarmer(farmerId: String, since: Long): Flow<List<ProductEntity>> = flowOf(SeedCatalog.filter { it.sellerId == farmerId && it.createdAt >= since })

    override fun observeEligibleForTransferCountForFarmer(farmerId: String): Flow<Int> = flowOf(SeedCatalog.count { it.sellerId == farmerId && !it.isDeleted })

    override suspend fun countActiveByOwnerId(ownerId: String): Int =
        SeedCatalog.count { it.sellerId == ownerId && !it.isDeleted && (it.lifecycleStatus ?: "ACTIVE") == "ACTIVE" }

    override suspend fun seedStarterKits() {
        // No-op for fake repository - starter kits not needed in tests
    }
}

class FakeProductMarketplaceRepository @javax.inject.Inject constructor() : ProductMarketplaceRepository {
    override suspend fun createProduct(product: ProductEntity, imageBytes: List<ByteArray>): Resource<String> =
        Resource.Success(product.productId.ifBlank { "id-${System.currentTimeMillis()}" })

    override suspend fun updateProduct(product: ProductEntity): Resource<Unit> = Resource.Success(Unit)

    override suspend fun deleteProduct(productId: String): Resource<Unit> = Resource.Success(Unit)

    override suspend fun autocomplete(prefix: String, limit: Int): Resource<List<ProductEntity>> =
        Resource.Success(SeedCatalog.filter { it.name.contains(prefix, true) }.take(limit))

    override suspend fun filterByPriceBreed(minPrice: Double, maxPrice: Double, breed: String?, limit: Int, offset: Int): Resource<List<ProductEntity>> =
        Resource.Success(SeedCatalog.filter { it.price in minPrice..maxPrice && (breed == null || it.breed?.equals(breed, true) == true) }.drop(offset).take(limit))

    override suspend fun filterByAgeGroup(
        group: com.rio.rostry.utils.ValidationUtils.AgeGroup,
        limit: Int,
        offset: Int,
        now: Long
    ): Resource<List<ProductEntity>> = Resource.Success(SeedCatalog.drop(offset).take(limit))

    override suspend fun filterByBoundingBox(
        minLat: Double?, maxLat: Double?, minLng: Double?, maxLng: Double?, limit: Int, offset: Int
    ): Resource<List<ProductEntity>> = Resource.Success(SeedCatalog.drop(offset).take(limit))

    override suspend fun filterVerified(limit: Int, offset: Int): Resource<List<ProductEntity>> =
        Resource.Success(SeedCatalog.drop(offset).take(limit))

    override suspend fun filterByDateRange(startDate: Long?, endDate: Long?, limit: Int, offset: Int): Resource<List<ProductEntity>> =
        Resource.Success(SeedCatalog.drop(offset).take(limit))
}

class FakeCartRepository @javax.inject.Inject constructor() : CartRepository {
    private val items = mutableMapOf<String, MutableMap<String, Double>>() // userId -> (productId -> qty)

    override fun observeCart(userId: String): kotlinx.coroutines.flow.Flow<List<com.rio.rostry.data.database.entity.CartItemEntity>> =
        kotlinx.coroutines.flow.flowOf(emptyList())

    override suspend fun addOrUpdateItem(userId: String, productId: String, quantity: Double, buyerLat: Double?, buyerLon: Double?): Resource<Unit> {
        val userMap = items.getOrPut(userId) { mutableMapOf() }
        userMap[productId] = (userMap[productId] ?: 0.0) + quantity
        return Resource.Success(Unit)
    }

    override suspend fun removeItem(userId: String, productId: String): Resource<Unit> {
        items[userId]?.remove(productId)
        return Resource.Success(Unit)
    }

    override suspend fun updateQuantity(userId: String, productId: String, quantity: Double): Resource<Unit> {
        val userMap = items.getOrPut(userId) { mutableMapOf() }
        userMap[productId] = quantity
        return Resource.Success(Unit)
    }
}

class FakeWishlistRepository @javax.inject.Inject constructor() : WishlistRepository {
    private val store = mutableMapOf<String, MutableSet<String>>()
    override suspend fun add(userId: String, productId: String): Resource<Unit> {
        store.getOrPut(userId) { mutableSetOf() }.add(productId)
        return Resource.Success(Unit)
    }
    override suspend fun remove(userId: String, productId: String): Resource<Unit> {
        store.getOrPut(userId) { mutableSetOf() }.remove(productId)
        return Resource.Success(Unit)
    }
    override fun observe(userId: String): Flow<List<com.rio.rostry.data.database.entity.WishlistEntity>> =
        flowOf(store[userId].orEmpty().map { com.rio.rostry.data.database.entity.WishlistEntity(userId = userId, productId = it, addedAt = 0L) })
}

// Recommendation engine not present in this codebase; no-op provider removed.

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [RepositoryModule::class]
)
abstract class TestDataModule {
    // Bind fakes
    @Binds @Singleton abstract fun bindProductRepository(impl: FakeProductRepository): ProductRepository
    @Binds @Singleton abstract fun bindProductMarketplaceRepository(impl: FakeProductMarketplaceRepository): ProductMarketplaceRepository
    @Binds @Singleton abstract fun bindCartRepository(impl: FakeCartRepository): CartRepository
    @Binds @Singleton abstract fun bindWishlistRepository(impl: FakeWishlistRepository): WishlistRepository

    // Keep production bindings for other repos by delegating to their real impls
    @Binds @Singleton abstract fun bindUserRepository(impl: UserRepositoryImpl): UserRepository
    @Binds @Singleton abstract fun bindLegacyAuthRepository(impl: com.rio.rostry.data.auth.AuthRepositoryImpl): com.rio.rostry.domain.auth.AuthRepository
    @Binds @Singleton abstract fun bindAuthRepository(impl: com.rio.rostry.data.auth.AuthRepositoryImplNew): com.rio.rostry.domain.auth.repository.AuthRepository
    @Binds @Singleton abstract fun bindTrackingRepository(impl: TrackingRepositoryImpl): TrackingRepository
    @Binds @Singleton abstract fun bindFamilyTreeRepository(impl: FamilyTreeRepositoryImpl): FamilyTreeRepository
    @Binds @Singleton abstract fun bindChatRepository(impl: ChatRepositoryImpl): ChatRepository
    @Binds @Singleton abstract fun bindTransferRepository(impl: TransferRepositoryImpl): TransferRepository
    @Binds @Singleton abstract fun bindOrderManagementRepository(impl: OrderManagementRepositoryImpl): OrderManagementRepository
    @Binds @Singleton abstract fun bindOrderRepository(impl: AdvancedOrderService): OrderRepository
    @Binds @Singleton abstract fun bindPaymentRepository(impl: PaymentRepositoryImpl): PaymentRepository
    @Binds @Singleton abstract fun bindCoinRepository(impl: CoinRepositoryImpl): CoinRepository
    @Binds @Singleton abstract fun bindLogisticsRepository(impl: LogisticsRepositoryImpl): LogisticsRepository
    @Binds @Singleton abstract fun bindInvoiceRepository(impl: InvoiceRepositoryImpl): InvoiceRepository
    @Binds @Singleton abstract fun bindTraceabilityRepository(impl: TraceabilityRepositoryImpl): TraceabilityRepository
    @Binds @Singleton abstract fun bindTransferWorkflowRepository(impl: TransferWorkflowRepositoryImpl): TransferWorkflowRepository
    @Binds @Singleton abstract fun bindCommunityRepository(impl: CommunityRepositoryImpl): CommunityRepository
    @Binds @Singleton abstract fun bindEnthusiastBreedingRepository(impl: com.rio.rostry.data.repository.EnthusiastBreedingRepositoryImpl): com.rio.rostry.data.repository.EnthusiastBreedingRepository
    @Binds @Singleton abstract fun bindTaskRepository(impl: com.rio.rostry.data.repository.monitoring.TaskRepositoryImpl): com.rio.rostry.data.repository.monitoring.TaskRepository
    @Binds @Singleton abstract fun bindDailyLogRepository(impl: com.rio.rostry.data.repository.monitoring.DailyLogRepositoryImpl): com.rio.rostry.data.repository.monitoring.DailyLogRepository
    @Binds @Singleton abstract fun bindSocialRepository(impl: com.rio.rostry.data.repository.social.SocialRepositoryImpl): com.rio.rostry.data.repository.social.SocialRepository
    @Binds @Singleton abstract fun bindMessagingRepository(impl: com.rio.rostry.data.repository.social.MessagingRepositoryImpl): com.rio.rostry.data.repository.social.MessagingRepository
    @Binds @Singleton abstract fun bindGrowthRepository(impl: com.rio.rostry.data.repository.monitoring.GrowthRepositoryImpl): com.rio.rostry.data.repository.monitoring.GrowthRepository
    @Binds @Singleton abstract fun bindQuarantineRepository(impl: com.rio.rostry.data.repository.monitoring.QuarantineRepositoryImpl): com.rio.rostry.data.repository.monitoring.QuarantineRepository
    @Binds @Singleton abstract fun bindMortalityRepository(impl: com.rio.rostry.data.repository.monitoring.MortalityRepositoryImpl): com.rio.rostry.data.repository.monitoring.MortalityRepository
    @Binds @Singleton abstract fun bindVaccinationRepository(impl: com.rio.rostry.data.repository.monitoring.VaccinationRepositoryImpl): com.rio.rostry.data.repository.monitoring.VaccinationRepository
    @Binds @Singleton abstract fun bindHatchingRepository(impl: com.rio.rostry.data.repository.monitoring.HatchingRepositoryImpl): com.rio.rostry.data.repository.monitoring.HatchingRepository
    @Binds @Singleton abstract fun bindFarmPerformanceRepository(impl: com.rio.rostry.data.repository.monitoring.FarmPerformanceRepositoryImpl): com.rio.rostry.data.repository.monitoring.FarmPerformanceRepository
    @Binds @Singleton abstract fun bindBreedingRepository(impl: com.rio.rostry.data.repository.monitoring.BreedingRepositoryImpl): com.rio.rostry.data.repository.monitoring.BreedingRepository
    @Binds @Singleton abstract fun bindFarmAlertRepository(impl: com.rio.rostry.data.repository.monitoring.FarmAlertRepositoryImpl): com.rio.rostry.data.repository.monitoring.FarmAlertRepository
    @Binds @Singleton abstract fun bindListingDraftRepository(impl: com.rio.rostry.data.repository.monitoring.ListingDraftRepositoryImpl): com.rio.rostry.data.repository.monitoring.ListingDraftRepository
    @Binds @Singleton abstract fun bindFarmerDashboardRepository(impl: com.rio.rostry.data.repository.monitoring.FarmerDashboardRepositoryImpl): com.rio.rostry.data.repository.monitoring.FarmerDashboardRepository
    @Binds @Singleton abstract fun bindFarmOnboardingRepository(impl: com.rio.rostry.data.repository.monitoring.FarmOnboardingRepositoryImpl): com.rio.rostry.data.repository.monitoring.FarmOnboardingRepository
    @Binds @Singleton abstract fun bindAuctionRepository(impl: AuctionRepositoryImpl): AuctionRepository
}

// Removed TestAuxModule as RecommendationEngine is not part of the current project schema
