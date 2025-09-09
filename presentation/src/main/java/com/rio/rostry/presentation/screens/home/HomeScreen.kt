package com.rio.rostry.presentation.screens.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.rio.rostry.domain.model.UserType
import com.rio.rostry.presentation.navigation.Routes
import com.rio.rostry.presentation.viewmodel.RoleUpgradeViewModel
import com.rio.rostry.presentation.viewmodel.RoleUpgradeUiState
import com.rio.rostry.presentation.viewmodel.SessionViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.LaunchedEffect

@Composable
fun HomeScreen(navController: NavHostController? = null) {
    val sessionVm: SessionViewModel = hiltViewModel()
    val userType by sessionVm.userType.collectAsState(initial = null)

    val upgradeVm: RoleUpgradeViewModel = hiltViewModel()
    val upgradeState by upgradeVm.state.collectAsState()

    LaunchedEffect(upgradeState) {
        if (upgradeState is RoleUpgradeUiState.UpgradedToFarmer) {
            navController?.navigate(Routes.FARMER_LOCATION_VERIFY) {
                popUpTo(Routes.HOME_GENERAL)
            }
        }
    }

    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        if (userType == UserType.GENERAL) {
            Button(onClick = { upgradeVm.upgradeToFarmer() }, modifier = Modifier.padding(16.dp)) {
                Text("Upgrade to Farmer")
            }
        } else {
            Text(text = "ROSTRY Home", style = MaterialTheme.typography.headlineMedium)
        }
    }
}
