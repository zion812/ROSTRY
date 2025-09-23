package com.rio.rostry.di

import com.rio.rostry.utils.notif.AnalyticsNotifier
import com.rio.rostry.utils.notif.AnalyticsNotifierImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class NotifModule {
    @Binds
    @Singleton
    abstract fun bindAnalyticsNotifier(impl: AnalyticsNotifierImpl): AnalyticsNotifier
}
