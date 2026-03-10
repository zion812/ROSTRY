package com.rio.rostry.di

import com.rio.rostry.data.repository.AdvancedOrderService
import com.rio.rostry.data.repository.AuctionRepository
import com.rio.rostry.data.repository.AuctionRepositoryImpl
import com.rio.rostry.domain.commerce.repository.CartRepository
import com.rio.rostry.domain.commerce.repository.CartRepositoryImpl
import com.rio.rostry.domain.commerce.repository.CoinRepository
import com.rio.rostry.domain.commerce.repository.CoinRepositoryImpl
import com.rio.rostry.domain.commerce.repository.EvidenceOrderRepository
import com.rio.rostry.domain.commerce.repository.EvidenceOrderRepositoryImpl
import com.rio.rostry.domain.commerce.repository.InventoryRepository
import com.rio.rostry.domain.farm.repository.InventoryRepositoryImpl
import com.rio.rostry.domain.commerce.repository.InvoiceRepository
import com.rio.rostry.data.repository.InvoiceRepositoryImpl
import com.rio.rostry.domain.commerce.repository.LogisticsRepository
import com.rio.rostry.data.repository.LogisticsRepositoryImpl
import com.rio.rostry.domain.commerce.repository.ProductMarketplaceRepository
import com.rio.rostry.data.repository.ProductMarketplaceRepositoryImpl
import com.rio.rostry.domain.commerce.repository.MarketListingRepository
import com.rio.rostry.data.repository.MarketListingRepositoryImpl
import com.rio.rostry.domain.commerce.repository.WishlistRepository
import com.rio.rostry.data.repository.WishlistRepositoryImpl
import com.rio.rostry.domain.commerce.engine.RecommendationEngine
import com.rio.rostry.ai.RecommendationEngineImpl
import com.rio.rostry.domain.commerce.repository.ReviewRepository
import com.rio.rostry.data.repository.ReviewRepositoryImpl
import com.rio.rostry.domain.commerce.repository.OrderRepository
import com.rio.rostry.domain.commerce.repository.PaymentRepository
import com.rio.rostry.data.repository.PaymentRepositoryImpl
import com.rio.rostry.data.repository.OrderManagementRepository
import com.rio.rostry.data.repository.OrderManagementRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

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
    abstract fun bindMarketListingRepository(impl: MarketListingRepositoryImpl): MarketListingRepository

    @Binds
    @Singleton
    abstract fun bindWishlistRepository(impl: WishlistRepositoryImpl): WishlistRepository


    @Binds
    @Singleton
    abstract fun bindAuctionRepository(impl: AuctionRepositoryImpl): AuctionRepository

    @Binds
    @Singleton
    abstract fun bindCartRepository(impl: CartRepositoryImpl): CartRepository

    @Binds
    @Singleton
    abstract fun bindOrderManagementRepository(impl: OrderManagementRepositoryImpl): OrderManagementRepository

    @Binds
    @Singleton
    abstract fun bindOrderRepository(impl: AdvancedOrderService): OrderRepository

    @Binds
    @Singleton
    abstract fun bindPaymentRepository(impl: PaymentRepositoryImpl): PaymentRepository

    @Binds
    @Singleton
    abstract fun bindCoinRepository(impl: CoinRepositoryImpl): CoinRepository

    @Binds
    @Singleton
    abstract fun bindLogisticsRepository(impl: LogisticsRepositoryImpl): LogisticsRepository

    @Binds
    @Singleton
    abstract fun bindInvoiceRepository(impl: InvoiceRepositoryImpl): InvoiceRepository



    @Binds
    @Singleton
    abstract fun bindReviewRepository(impl: ReviewRepositoryImpl): ReviewRepository

    @Binds
    @Singleton
    abstract fun bindEvidenceOrderRepository(impl: EvidenceOrderRepositoryImpl): EvidenceOrderRepository
}
