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
}
