package com.rio.rostry.di

import com.rio.rostry.data.repository.UserRepositoryImpl
import com.rio.rostry.data.repository.ProductRepositoryImpl
import com.rio.rostry.data.repository.OrderRepositoryImpl
import com.rio.rostry.data.repository.TransferRepositoryImpl
import com.rio.rostry.data.repository.CoinRepositoryImpl
import com.rio.rostry.data.repository.NotificationRepositoryImpl
import com.rio.rostry.domain.repository.UserRepository
import com.rio.rostry.domain.repository.ProductRepository
import com.rio.rostry.domain.repository.OrderRepository
import com.rio.rostry.domain.repository.TransferRepository
import com.rio.rostry.domain.repository.CoinRepository
import com.rio.rostry.domain.repository.NotificationRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindUserRepository(userRepositoryImpl: UserRepositoryImpl): UserRepository

    @Binds
    abstract fun bindProductRepository(productRepositoryImpl: ProductRepositoryImpl): ProductRepository

    @Binds
    abstract fun bindOrderRepository(orderRepositoryImpl: OrderRepositoryImpl): OrderRepository

    @Binds
    abstract fun bindTransferRepository(transferRepositoryImpl: TransferRepositoryImpl): TransferRepository

    @Binds
    abstract fun bindCoinRepository(coinRepositoryImpl: CoinRepositoryImpl): CoinRepository

    @Binds
    abstract fun bindNotificationRepository(notificationRepositoryImpl: NotificationRepositoryImpl): NotificationRepository
}