package com.rio.rostry.ui.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rio.rostry.data.models.UserProfile
import com.rio.rostry.viewmodel.profile.ProfileViewModel
import com.rio.rostry.viewmodel.profile.ProfileViewModelFactory

/**
 * Composable function for the user profile screen.
 */
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = viewModel(factory = ProfileViewModelFactory())
) {
    // Fetch the profile when the screen is first composed
    LaunchedEffect(Unit) {
        viewModel.fetchProfile()
    }

    val profile by viewModel.profile.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val isSaving by viewModel.isSaving.collectAsState()

    var name by remember { mutableStateOf(profile?.name ?: "") }
    var userType by remember { mutableStateOf(profile?.userType ?: "") }
    var location by remember { mutableStateOf(profile?.location ?: "") }

    // Update the local state when the profile is fetched
    LaunchedEffect(profile) {
        profile?.let {
            name = it.name
            userType = it.userType
            location = it.location
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "User Profile",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (errorMessage != null) {
            Text(
                text = "Error: $errorMessage",
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = userType,
            onValueChange = { userType = it },
            label = { Text("User Type") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = location,
            onValueChange = { location = it },
            label = { Text("Location") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        Button(
            onClick = {
                val updatedProfile = profile?.copy(
                    name = name,
                    userType = userType,
                    location = location
                ) ?: UserProfile(
                    uid = "", // This would need to be set properly in a real implementation
                    name = name,
                    email = "", // This would need to be set properly in a real implementation
                    userType = userType,
                    location = location
                )
                viewModel.saveProfile(updatedProfile)
            },
            enabled = !isSaving,
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            if (isSaving) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(if (isSaving) "Saving..." else "Save Profile")
        }

        // Display the saved profile information
        profile?.let {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Saved Profile",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text("Name: ${it.name}")
                    Text("Email: ${it.email}")
                    Text("User Type: ${it.userType}")
                    Text("Location: ${it.location}")
                }
            }
        }
    }
}