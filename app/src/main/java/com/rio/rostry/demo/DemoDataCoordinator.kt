package com.rio.rostry.demo

import com.rio.rostry.data.demo.DemoAccounts
import com.rio.rostry.data.demo.DemoSeeders
import com.rio.rostry.domain.model.UserType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DemoDataCoordinator @Inject constructor(
    private val seeders: DemoSeeders,
    private val demoMode: DemoModeManager
) {
    private val scope = CoroutineScope(Dispatchers.IO)

    fun seedAllAsync() {
        scope.launch {
            DemoAccounts.all.forEach { profile ->
                runCatching { seeders.seedProfile(profile) }
            }
        }
    }

    fun seedByRoleAsync(role: UserType) {
        scope.launch {
            DemoAccounts.profilesByRole(role).forEach { profile ->
                runCatching { seeders.seedProfile(profile) }
            }
        }
    }

    fun resetSession() {
        // Intentionally minimal: callers can clear caches or re-install app for clean DB.
        // We can extend this to delete demo IDs specifically if needed.
        demoMode.resetAll()
    }
}
