package com.rio.rostry.ui.auth.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rio.rostry.domain.model.User
import com.rio.rostry.domain.model.UserType
import com.rio.rostry.presentation.auth.AuthViewModel

@Composable
fun RoleUpgradeScreen(
    user: User,
    onRoleUpgraded: () -> Unit,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    var selectedRole by remember { mutableStateOf(user.userType) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Upgrade Your Role",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Current Role: ${user.userType}",
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Radio buttons for role selection
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Only show upgrade options (can't downgrade)
            if (user.userType == UserType.GENERAL) {
                RoleOption(
                    role = UserType.FARMER,
                    selected = selectedRole == UserType.FARMER,
                    onSelect = { selectedRole = UserType.FARMER }
                )
                
                RoleOption(
                    role = UserType.ENTHUSIAST,
                    selected = selectedRole == UserType.ENTHUSIAST,
                    onSelect = { selectedRole = UserType.ENTHUSIAST }
                )
            } else if (user.userType == UserType.FARMER) {
                RoleOption(
                    role = UserType.ENTHUSIAST,
                    selected = selectedRole == UserType.ENTHUSIAST,
                    onSelect = { selectedRole = UserType.ENTHUSIAST }
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                if (selectedRole != user.userType) {
                    isLoading = true
                    // In a real implementation, we would call the ViewModel method
                    // authViewModel.upgradeUserRole(user.id, selectedRole)
                    // For now, we'll simulate the success
                    onRoleUpgraded()
                } else {
                    errorMessage = "Please select a different role"
                }
            },
            enabled = !isLoading && selectedRole != user.userType,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text("Upgrade Role")
        }

        errorMessage?.let { 
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}