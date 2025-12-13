package com.rio.rostry.di

import com.rio.rostry.data.repository.BreedRepository
import com.rio.rostry.data.repository.BreedRepositoryImpl
import com.rio.rostry.data.repository.LikesRepository
import com.rio.rostry.data.repository.LikesRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class TestFakesModule {

    @Binds
    @Singleton
    abstract fun bindLikesRepository(impl: LikesRepositoryImpl): LikesRepository

    @Binds
    @Singleton
    abstract fun bindBreedRepository(impl: BreedRepositoryImpl): BreedRepository
}
