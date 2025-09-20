package com.rio.rostry

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.rio.rostry.presentation.auth.AuthViewModel
import com.rio.rostry.presentation.viewmodel.UserViewModel
import com.rio.rostry.ui.auth.AuthNavGraph
import com.rio.rostry.ui.theme.ROSTRYTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ROSTRYTheme {
                MainApp()
            }
        }
    }
}

@Composable
fun MainApp() {
    val navController = rememberNavController()
    // In a real implementation, we would check if user is logged in and navigate accordingly
    AuthNavGraph(navController = navController)
}

@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    val userViewModel: UserViewModel = hiltViewModel()
    Text(
        text = "Welcome to ROSTRY AgriTech Marketplace!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    ROSTRYTheme {
        MainScreen()
    }
}