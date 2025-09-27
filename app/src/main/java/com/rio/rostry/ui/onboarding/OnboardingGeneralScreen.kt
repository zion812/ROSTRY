package com.rio.rostry.ui.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rio.rostry.domain.model.UserType
import kotlinx.coroutines.flow.collectLatest

@Composable
fun OnboardingGeneralScreen(
    onDone: (UserType) -> Unit
) {
    val vm: OnboardingViewModel = hiltViewModel()
    val ui by vm.ui.collectAsState()

    LaunchedEffect(Unit) {
        // Ensure role is preselected to GENERAL if not set
        if (ui.selectedUserType == null) vm.selectUserType(UserType.GENERAL)
    }

    LaunchedEffect(Unit) {
        // Observe navigation
        vm.nav.collectLatest { event ->
            when (event) {
                is OnboardingViewModel.Nav.ToHome -> onDone(event.role)
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Welcome, General User", style = MaterialTheme.typography.headlineSmall)
        Text("Unlock features:")
        UserType.GENERAL.primaryFeatures.forEach { Text("• $it", style = MaterialTheme.typography.bodyMedium) }

        Spacer(modifier = Modifier.height(8.dp))

        ProfileTextField(
            label = "Full Name",
            value = ui.fullName,
            onValueChange = vm::updateFullName,
            error = ui.validationErrors["fullName"]
        )
        ProfileTextField(
            label = "Address (optional)",
            value = ui.address,
            onValueChange = vm::updateAddress,
            error = ui.validationErrors["address"]
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                vm.selectUserType(UserType.GENERAL)
                vm.completeOnboarding()
            },
            enabled = !ui.isLoading,
            modifier = Modifier.fillMaxWidth()
        ) { Text("Continue") }

        ui.error?.let { Text("Error: $it", color = MaterialTheme.colorScheme.error) }
    }
}
