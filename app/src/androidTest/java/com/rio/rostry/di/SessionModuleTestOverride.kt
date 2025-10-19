package com.rio.rostry.di

import com.rio.rostry.session.RolePreferenceDataSource
import com.rio.rostry.session.RolePreferenceStorage
import com.rio.rostry.ui.navigation.RoleStartDestinationProvider
import com.rio.rostry.ui.navigation.RoleStartDestinationProviderImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

object CurrentUserHolder {
    @Volatile var userId: String? = null
}

class TestCurrentUserProvider @javax.inject.Inject constructor() : com.rio.rostry.session.CurrentUserProvider {
    override fun userIdOrNull(): String? = CurrentUserHolder.userId
    override fun isAuthenticated(): Boolean = CurrentUserHolder.userId != null
}

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [SessionModule::class]
)
abstract class SessionModuleTestOverride {
    @Binds
    @Singleton
    abstract fun bindCurrentUserProvider(impl: TestCurrentUserProvider): com.rio.rostry.session.CurrentUserProvider

    @Binds
    @Singleton
    abstract fun bindRolePreferenceDataSource(impl: RolePreferenceStorage): RolePreferenceDataSource

    @Binds
    @Singleton
    abstract fun bindRoleStartDestinationProvider(impl: RoleStartDestinationProviderImpl): RoleStartDestinationProvider
}
