package com.rio.rostry.di

import com.rio.rostry.session.CurrentUserProvider
import com.rio.rostry.session.FirebaseCurrentUserProvider
import com.rio.rostry.session.RolePreferenceDataSource
import com.rio.rostry.session.RolePreferenceStorage
import com.rio.rostry.ui.navigation.RoleStartDestinationProvider
import com.rio.rostry.ui.navigation.RoleStartDestinationProviderImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SessionModule {
    @Binds
    @Singleton
    abstract fun bindCurrentUserProvider(impl: FirebaseCurrentUserProvider): CurrentUserProvider

    @Binds
    @Singleton
    abstract fun bindRolePreferenceDataSource(impl: RolePreferenceStorage): RolePreferenceDataSource

    @Binds
    @Singleton
    abstract fun bindRoleStartDestinationProvider(impl: RoleStartDestinationProviderImpl): RoleStartDestinationProvider
}
