package com.rio.rostry.ui.navigation

import com.rio.rostry.domain.model.UserType
import javax.inject.Inject
import javax.inject.Singleton

interface RoleStartDestinationProvider {
    fun configFor(role: UserType): RoleNavigationConfig
}

@Singleton
class RoleStartDestinationProviderImpl @Inject constructor() : RoleStartDestinationProvider {
    override fun configFor(role: UserType): RoleNavigationConfig = Routes.configFor(role)
}
