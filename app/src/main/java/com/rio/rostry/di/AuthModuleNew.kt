package com.rio.rostry.di

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.rio.rostry.data.auth.AuthRepositoryImplNew
import com.rio.rostry.data.auth.mapper.ErrorMapper
import com.rio.rostry.data.auth.source.AuthStateManager
import com.rio.rostry.data.auth.source.FirebaseAuthDataSource
import com.rio.rostry.data.auth.util.RateLimiter
import com.rio.rostry.data.database.dao.RateLimitDao
import com.rio.rostry.domain.auth.repository.AuthRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

/**
 * Hilt module for new authentication system.
 * 
 * Provides all dependencies for clean auth architecture:
 * - Data sources
 * - Utilities
 * - Repository implementation
 * 
 * Usage: @Inject AuthRepository in your ViewModels
 */
@Module
@InstallIn(SingletonComponent::class)
object AuthModuleNew {
    
    /**
     * Provides AuthStateManager for DataStore state persistence
     */
    @Provides
    @Singleton
    fun provideAuthStateManager(
        @ApplicationContext context: Context
    ): AuthStateManager {
        return AuthStateManager(context)
    }
    
    /**
     * Provides RateLimiter for rate limiting logic
     */
    @Provides
    @Singleton
    fun provideRateLimiter(
        rateLimitDao: RateLimitDao
    ): RateLimiter {
        return RateLimiter(rateLimitDao)
    }
    
    /**
     * Provides FirebaseAuthDataSource for Firebase operations
     */
    @Provides
    @Singleton
    fun provideFirebaseAuthDataSource(
        firebaseAuth: FirebaseAuth,
        authStateManager: AuthStateManager
    ): FirebaseAuthDataSource {
        return FirebaseAuthDataSource(firebaseAuth, authStateManager)
    }
    
    /**
     * Provides new AuthRepository implementation
     * 
     * Named "AuthRepositoryNew" to avoid conflicts with old implementation
     * during migration period.
     * 
     * To use new repository: @Inject @Named("AuthRepositoryNew") AuthRepository
     * Or update old modules to use this implementation
     */
    @Provides
    @Singleton
    @Named("AuthRepositoryNew")
    fun provideAuthRepositoryNew(
        firebaseAuthDataSource: FirebaseAuthDataSource,
        rateLimiter: RateLimiter,
        authStateManager: AuthStateManager
    ): AuthRepository {
        return AuthRepositoryImplNew(
            firebaseAuthDataSource = firebaseAuthDataSource,
            rateLimiter = rateLimiter,
            authStateManager = authStateManager
        )
    }
    
    /**
     * If you want to replace old implementation completely:
     * 1. Remove @Named annotation above
     * 2. Remove old AuthRepository provider from other modules
     * 3. Clean rebuild project
     */
}
