package com.rio.rostry

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.google.firebase.FirebaseApp
import com.rio.rostry.navigation.NavigationAnalytics
import com.rio.rostry.navigation.RostryNavGraph
import com.rio.rostry.ui.theme.ROSTRYTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize Firebase
        FirebaseApp.initializeApp(this)
        
        setContent {
            ROSTRYTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Use the navigation graph
                    val navController = rememberNavController()
                    
                    // Set up navigation tracking
                    NavigationAnalytics.setupNavigationTracking(navController)
                    
                    RostryNavGraph(navController = navController)
                }
            }
        }
    }
}