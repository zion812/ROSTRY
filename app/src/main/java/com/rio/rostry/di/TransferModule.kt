package com.rio.rostry.di

import com.rio.rostry.domain.transfer.TransferSystem
import com.rio.rostry.domain.transfer.TransferSystemImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for Transfer System dependencies.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class TransferModule {
    
    @Binds
    @Singleton
    abstract fun bindTransferSystem(
        impl: TransferSystemImpl
    ): TransferSystem
}
