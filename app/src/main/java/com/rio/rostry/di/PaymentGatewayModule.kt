package com.rio.rostry.di

import com.rio.rostry.marketplace.payment.DefaultPaymentGateway
import com.rio.rostry.marketplace.payment.PaymentGateway
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PaymentGatewayModule {

    @Provides
    @Singleton
    fun providePaymentGateway(): PaymentGateway = DefaultPaymentGateway()
}
