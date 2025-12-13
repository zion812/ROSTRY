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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.activity.viewModels
import com.google.firebase.auth.FirebaseAuth
import com.rio.rostry.session.SessionManager
import com.rio.rostry.ui.theme.ROSTRYTheme
import com.rio.rostry.ui.navigation.AppNavHost
import com.rio.rostry.utils.network.FeatureToggles
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject lateinit var sessionManager: SessionManager
    @Inject lateinit var featureToggles: FeatureToggles
    @Inject lateinit var firebaseAuth: FirebaseAuth
    private val viewModel: com.rio.rostry.ui.main.MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // Trigger ViewModel init
        viewModel.fetchCurrentUserProfile()
        
        setContent {
            val roleState = sessionManager.sessionRole().collectAsStateWithLifecycle(initialValue = null)
            
            AnimatedContent(
                targetState = roleState.value,
                transitionSpec = {
                    fadeIn(animationSpec = tween(500)) togetherWith
                    fadeOut(animationSpec = tween(500))
                },
                label = "role_transition"
            ) { role ->
                ROSTRYTheme(userRole = role) {
                    AppNavHost(featureToggles = featureToggles, firebaseAuth = firebaseAuth)
                }
            }
        }
    }
}