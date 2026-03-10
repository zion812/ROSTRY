package com.rio.rostry.di

import com.rio.rostry.data.auth.AuthRepositoryImpl
import com.rio.rostry.data.repository.ChatRepository
import com.rio.rostry.data.repository.ChatRepositoryImpl
import com.rio.rostry.data.repository.FamilyTreeRepository
import com.rio.rostry.data.repository.FamilyTreeRepositoryImpl
import com.rio.rostry.domain.account.repository.UserRepository
import com.rio.rostry.data.repository.UserRepositoryImpl
import com.rio.rostry.domain.commerce.repository.ProductRepository
import com.rio.rostry.data.repository.ProductRepositoryImpl
import com.rio.rostry.data.repository.WatchedLineagesRepository
import com.rio.rostry.data.repository.WatchedLineagesRepositoryImpl
import com.rio.rostry.domain.auth.AuthRepository
import com.rio.rostry.domain.pedigree.PedigreeRepository
import com.rio.rostry.domain.pedigree.PedigreeRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class CoreRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindUserRepository(impl: UserRepositoryImpl): UserRepository

    @Binds
    @Singleton
    abstract fun bindProductRepository(impl: ProductRepositoryImpl): ProductRepository

    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

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
    abstract fun bindTraceabilityRepository(impl: TraceabilityRepositoryImpl): TraceabilityRepository

    @Binds
    @Singleton
    abstract fun bindWatchedLineagesRepository(impl: WatchedLineagesRepositoryImpl): WatchedLineagesRepository


    @Binds
    @Singleton
    abstract fun bindPedigreeRepository(impl: PedigreeRepositoryImpl): PedigreeRepository
}
