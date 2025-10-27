package com.rio.rostry

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val roleState = sessionManager.sessionRole().collectAsStateWithLifecycle(initialValue = null)
            ROSTRYTheme(userRole = roleState.value) {
                AppNavHost(featureToggles = featureToggles, firebaseAuth = firebaseAuth)
            }
        }
    }
}