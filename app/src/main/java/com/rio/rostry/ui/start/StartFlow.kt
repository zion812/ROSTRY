package com.rio.rostry.ui.start

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.firebase.auth.FirebaseAuth
import com.rio.rostry.domain.model.UserType
import com.rio.rostry.ui.start.StartViewModel

/**
 * Start flow determines the user's authentication state and routes accordingly.
 * 
 * @param firebaseAuth Firebase authentication instance
 * @param onNavigateToAuth Navigate to authentication flow
 * @param onNavigateToHome Navigate to home screen with user type
 */
@Composable
fun StartFlow(
    firebaseAuth: FirebaseAuth,
    onNavigateToAuth: () -> Unit,
    onNavigateToHome: (UserType) -> Unit
) {
    val viewModel: StartViewModel = hiltViewModel()
    
    LaunchedEffect(Unit) {
        viewModel.decide(System.currentTimeMillis())
        viewModel.nav.collect { nav ->
            when (nav) {
                is StartViewModel.Nav.ToAuth, is StartViewModel.Nav.ToAuthWelcome -> {
                    onNavigateToAuth()
                }
                is StartViewModel.Nav.ToHome -> {
                    onNavigateToHome(nav.role)
                }
                is StartViewModel.Nav.ToGuestHome -> {
                    onNavigateToHome(nav.role)
                }
            }
        }
    }
    
    // Loading state while determining route
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        CircularProgressIndicator(
            modifier = Modifier.align(Alignment.Center)
        )
    }
}
   }
}
