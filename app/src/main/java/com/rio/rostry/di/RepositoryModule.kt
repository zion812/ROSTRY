package com.rio.rostry.di

import com.rio.rostry.data.repository.ProductRepository
import com.rio.rostry.data.repository.ProductRepositoryImpl
import com.rio.rostry.data.repository.UserRepository
import com.rio.rostry.data.repository.UserRepositoryImpl
// Import your other repository interfaces and implementations here
import com.rio.rostry.domain.auth.AuthRepository
import com.rio.rostry.data.auth.AuthRepositoryImpl
import com.rio.rostry.data.repository.TrackingRepository
import com.rio.rostry.data.repository.TrackingRepositoryImpl
import com.rio.rostry.data.repository.FamilyTreeRepository
import com.rio.rostry.data.repository.FamilyTreeRepositoryImpl
import com.rio.rostry.data.repository.ChatRepository
import com.rio.rostry.data.repository.ChatRepositoryImpl
import com.rio.rostry.data.repository.TransferRepository
import com.rio.rostry.data.repository.TransferRepositoryImpl
import com.rio.rostry.data.repository.ProductMarketplaceRepository
import com.rio.rostry.data.repository.ProductMarketplaceRepositoryImpl
import com.rio.rostry.data.repository.AuctionRepository
import com.rio.rostry.data.repository.AuctionRepositoryImpl
import com.rio.rostry.data.repository.CartRepository
import com.rio.rostry.data.repository.CartRepositoryImpl
import com.rio.rostry.data.repository.WishlistRepository
import com.rio.rostry.data.repository.WishlistRepositoryImpl
import com.rio.rostry.data.repository.OrderManagementRepository
import com.rio.rostry.data.repository.OrderManagementRepositoryImpl
import com.rio.rostry.data.repository.PaymentRepository
import com.rio.rostry.data.repository.PaymentRepositoryImpl
import com.rio.rostry.data.repository.CoinRepository
import com.rio.rostry.data.repository.CoinRepositoryImpl
import com.rio.rostry.data.repository.LogisticsRepository
import com.rio.rostry.data.repository.LogisticsRepositoryImpl
import com.rio.rostry.data.repository.InvoiceRepository
import com.rio.rostry.data.repository.InvoiceRepositoryImpl
import com.rio.rostry.data.repository.TraceabilityRepository
import com.rio.rostry.data.repository.TraceabilityRepositoryImpl
import com.rio.rostry.data.repository.TransferWorkflowRepository
import com.rio.rostry.data.repository.TransferWorkflowRepositoryImpl

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindUserRepository(userRepositoryImpl: UserRepositoryImpl): UserRepository

    @Binds
    @Singleton
    abstract fun bindProductRepository(productRepositoryImpl: ProductRepositoryImpl): ProductRepository

    // ... and so on for other repositories

    @Binds
    @Singleton
    abstract fun bindAuthRepository(authRepositoryImpl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun bindTrackingRepository(impl: TrackingRepositoryImpl): TrackingRepository

    @Binds
    @Singleton
    abstract fun bindFamilyTreeRepository(impl: FamilyTreeRepositoryImpl): FamilyTreeRepository

    @Binds
    @Singleton
    abstract fun bindChatRepository(impl: ChatRepositoryImpl): ChatRepository

    @Binds
    @Singleton
    abstract fun bindTransferRepository(impl: TransferRepositoryImpl): TransferRepository

    @Binds
    @Singleton
    abstract fun bindProductMarketplaceRepository(impl: ProductMarketplaceRepositoryImpl): ProductMarketplaceRepository

    @Binds
    @Singleton
    abstract fun bindAuctionRepository(impl: AuctionRepositoryImpl): AuctionRepository

    @Binds
    @Singleton
    abstract fun bindCartRepository(impl: CartRepositoryImpl): CartRepository

    @Binds
    @Singleton
    abstract fun bindWishlistRepository(impl: WishlistRepositoryImpl): WishlistRepository

    @Binds
    @Singleton
    abstract fun bindOrderManagementRepository(impl: OrderManagementRepositoryImpl): OrderManagementRepository

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
    abstract fun bindTraceabilityRepository(impl: TraceabilityRepositoryImpl): TraceabilityRepository

    @Binds
    @Singleton
    abstract fun bindTransferWorkflowRepository(impl: TransferWorkflowRepositoryImpl): TransferWorkflowRepository
}
