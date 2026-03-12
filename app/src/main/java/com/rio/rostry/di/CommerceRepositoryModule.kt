package com.rio.rostry.di

import com.rio.rostry.data.repository.AdvancedOrderService
import com.rio.rostry.domain.commerce.repository.CoinRepository
import com.rio.rostry.domain.commerce.repository.CoinRepositoryImpl
import com.rio.rostry.domain.commerce.repository.EvidenceOrderRepository
import com.rio.rostry.domain.commerce.repository.EvidenceOrderRepositoryImpl
import com.rio.rostry.domain.commerce.repository.ProductMarketplaceRepository
import com.rio.rostry.data.repository.ProductMarketplaceRepositoryImpl
import com.rio.rostry.domain.commerce.engine.RecommendationEngine
import com.rio.rostry.ai.RecommendationEngineImpl
import com.rio.rostry.data.repository.OrderManagementRepository
import com.rio.rostry.data.repository.OrderManagementRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Temporary Hilt module for commerce repositories not yet migrated to data:commerce.
 * The following are already bound in CommerceDataModule and MUST NOT be duplicated here:
 * - PaymentRepository, InvoiceRepository, ReviewRepository, LogisticsRepository
 * - CartRepository, WishlistRepository, MarketListingRepository, AuctionRepository
 * - ProductRepository, OrderRepository, MarketplaceRepository, ListingDraftRepository, TransactionRepository, DisputeRepository
 *
 * TODO: Migrate remaining bindings to data:commerce and delete this file.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class CommerceRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindRecommendationEngine(impl: RecommendationEngineImpl): RecommendationEngine

    @Binds
    @Singleton
    abstract fun bindProductMarketplaceRepository(impl: ProductMarketplaceRepositoryImpl): ProductMarketplaceRepository

    @Binds
    @Singleton
    abstract fun bindOrderManagementRepository(impl: OrderManagementRepositoryImpl): OrderManagementRepository

    @Binds
    @Singleton
    abstract fun bindCoinRepository(impl: CoinRepositoryImpl): CoinRepository

    @Binds
    @Singleton
    abstract fun bindEvidenceOrderRepository(impl: EvidenceOrderRepositoryImpl): EvidenceOrderRepository
}

