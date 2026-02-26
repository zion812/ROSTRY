package com.rio.rostry.di

import com.rio.rostry.data.database.AppDatabase
import com.rio.rostry.domain.validation.EntityValidator
import com.rio.rostry.domain.validation.ValidationFramework
import com.rio.rostry.domain.validation.ValidationFrameworkImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ValidationModule {

    @Provides
    @Singleton
    fun provideEntityValidator(database: AppDatabase): EntityValidator {
        return EntityValidator(database)
    }

    @Provides
    @Singleton
    fun provideValidationFramework(entityValidator: EntityValidator): ValidationFramework {
        return ValidationFrameworkImpl(entityValidator)
    }
}
