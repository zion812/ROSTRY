package com.rio.rostry.di

import com.rio.rostry.data.repository.FamilyTreeRepository
import com.rio.rostry.data.repository.FamilyTreeRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Temporary Hilt module for repositories not yet migrated to data modules.
 * TODO: Migrate FamilyTreeRepository to data:farm and delete this file.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class CoreRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindFamilyTreeRepository(impl: FamilyTreeRepositoryImpl): FamilyTreeRepository
}

