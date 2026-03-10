package com.rio.rostry

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.activity.viewModels
import com.google.firebase.auth.FirebaseAuth
import com.rio.rostry.domain.model.UserType
import com.rio.rostry.core.common.session.SessionManager
import com.rio.rostry.ui.theme.AppRole
import com.rio.rostry.ui.theme.ROSTRYTheme
import com.rio.rostry.ui.navigation.AppNavHost
import com.rio.rostry.utils.network.FeatureToggles
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import javax.inject.Inject

// Import NavigationRegistry for Phase 1 modularization
import com.rio.rostry.core.navigation.NavigationRegistry

/** Maps a domain [UserType] to the design-system [AppRole]. */
private fun UserType?.toAppRole(): AppRole? = when (this) {
    UserType.FARMER -> AppRole.FARMER
    UserType.ENTHUSIAST -> AppRole.ENTHUSIAST
    UserType.GENERAL, UserType.ADMIN, UserType.SUPPORT, UserType.MODERATOR -> AppRole.GENERAL
    null -> null
}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject lateinit var sessionManager: SessionManager
    @Inject lateinit var featureToggles: FeatureToggles
    @Inject lateinit var firebaseAuth: FirebaseAuth
    private val viewModel: com.rio.rostry.ui.main.MainViewModel by viewModels()

    // Get NavigationRegistry from Application (Phase 1 modularization)
    private val navigationRegistry: NavigationRegistry by lazy {
        (application as RostryApp).navigationRegistry
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // Trigger ViewModel init
        viewModel.fetchCurrentUserProfile()

        setContent {
            val rawRoleState = sessionManager.sessionRole().collectAsStateWithLifecycle(initialValue = null)

            // Debounce role state to prevent rapid UI transitions during upgrade
            // This ensures the role is stable for 500ms before triggering a navigation change
            var stableRole by remember { mutableStateOf<UserType?>(null) }

            LaunchedEffect(rawRoleState.value) {
                // If we have no current role, update immediately (first load)
                if (stableRole == null && rawRoleState.value != null) {
                    stableRole = rawRoleState.value
                    return@LaunchedEffect
                }

                // If role changed, wait for stability before updating
                if (rawRoleState.value != stableRole) {
                    delay(500L) // Wait 500ms for role to stabilize
                    // Double-check that the value is still the same after the delay
                    if (rawRoleState.value != stableRole) {
                        stableRole = rawRoleState.value
                    }
                }
            }

            AnimatedContent(
                targetState = stableRole,
                transitionSpec = {
                    fadeIn(animationSpec = tween(500)) togetherWith
                    fadeOut(animationSpec = tween(500))
                },
                label = "role_transition"
            ) { role ->
                ROSTRYTheme(appRole = role.toAppRole()) {
                    // Pass NavigationRegistry to AppNavHost for Phase 1 modularization
                    AppNavHost(
                        featureToggles = featureToggles,
                        firebaseAuth = firebaseAuth,
                        navigationRegistry = navigationRegistry
                    )
                }
            }
        }
    }
}
