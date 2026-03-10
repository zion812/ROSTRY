package com.rio.rostry.data.commerce.di

import com.rio.rostry.data.commerce.repository.ProductRepositoryImpl
import com.rio.rostry.data.commerce.repository.CartRepositoryImpl
import com.rio.rostry.data.commerce.repository.WishlistRepositoryImpl
import com.rio.rostry.data.commerce.repository.OrderRepositoryImpl
import com.rio.rostry.data.commerce.repository.MarketplaceRepositoryImpl
import com.rio.rostry.data.commerce.repository.ListingDraftRepositoryImpl
import com.rio.rostry.data.commerce.repository.TransactionRepositoryImpl
import com.rio.rostry.data.commerce.repository.AuctionRepositoryImpl
import com.rio.rostry.domain.commerce.repository.ProductRepository
import com.rio.rostry.domain.commerce.repository.CartRepository
import com.rio.rostry.domain.commerce.repository.WishlistRepository
import com.rio.rostry.domain.commerce.repository.OrderRepository
import com.rio.rostry.domain.commerce.repository.MarketplaceRepository
import com.rio.rostry.domain.commerce.repository.ListingDraftRepository
import com.rio.rostry.domain.commerce.repository.TransactionRepository
import com.rio.rostry.domain.commerce.repository.MarketListingRepository
import com.rio.rostry.data.commerce.repository.MarketListingRepositoryImpl
import com.rio.rostry.domain.commerce.repository.DisputeRepository
import com.rio.rostry.data.commerce.repository.DisputeRepositoryImpl
import com.rio.rostry.domain.commerce.repository.AuctionRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for binding commerce data layer implementations to domain interfaces.
 * 
 * This module provides dependency injection bindings for all commerce-related repositories.
 * Implementations are in the data:commerce module, interfaces are in domain:commerce.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class CommerceDataModule {
    
    /**
     * Binds ProductRepositoryImpl to ProductRepository interface.
     */
    @Binds
    @Singleton
    abstract fun bindProductRepository(
        impl: ProductRepositoryImpl
    ): ProductRepository
    
    /**
     * Binds CartRepositoryImpl to CartRepository interface.
     */
    @Binds
    @Singleton
    abstract fun bindCartRepository(
        impl: CartRepositoryImpl
    ): CartRepository
    
    /**
     * Binds WishlistRepositoryImpl to WishlistRepository interface.
     */
    @Binds
    @Singleton
    abstract fun bindWishlistRepository(
        impl: WishlistRepositoryImpl
    ): WishlistRepository
    
    /**
     * Binds OrderRepositoryImpl to OrderRepository interface.
     */
    @Binds
    @Singleton
    abstract fun bindOrderRepository(
        impl: OrderRepositoryImpl
    ): OrderRepository
    
    /**
     * Binds MarketplaceRepositoryImpl to MarketplaceRepository interface.
     */
    @Binds
    @Singleton
    abstract fun bindMarketplaceRepository(
        impl: MarketplaceRepositoryImpl
    ): MarketplaceRepository
    
    /**
     * Binds ListingDraftRepositoryImpl to ListingDraftRepository interface.
     */
    @Binds
    @Singleton
    abstract fun bindListingDraftRepository(
        impl: ListingDraftRepositoryImpl
    ): ListingDraftRepository
    
    /**
     * Binds TransactionRepositoryImpl to TransactionRepository interface.
     */
    @Binds
    @Singleton
    abstract fun bindTransactionRepository(
        impl: TransactionRepositoryImpl
    ): TransactionRepository

    /**
     * Binds MarketListingRepositoryImpl to MarketListingRepository interface.
     */
    @Binds
    @Singleton
    abstract fun bindMarketListingRepository(
        impl: MarketListingRepositoryImpl
    ): MarketListingRepository

    /**
     * Binds DisputeRepositoryImpl to DisputeRepository interface.
     */
    @Binds
    @Singleton
    abstract fun bindDisputeRepository(
        impl: DisputeRepositoryImpl
    ): DisputeRepository

    /**
     * Binds AuctionRepositoryImpl to AuctionRepository interface.
     */
    @Binds
    @Singleton
    abstract fun bindAuctionRepository(
        impl: AuctionRepositoryImpl
    ): AuctionRepository
}
