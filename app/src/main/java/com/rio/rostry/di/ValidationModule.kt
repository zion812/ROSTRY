package com.rio.rostry.di

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
    fun provideValidationFramework(): ValidationFramework {
        return ValidationFrameworkImpl()
    }
}
