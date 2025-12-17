package com.rio.rostry.ui.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    onNavigateBack: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val ui by viewModel.ui.collectAsState()
    
    // Initialize state with current user data
    var fullName by remember(ui.user) { mutableStateOf(ui.user?.fullName ?: "") }
    var phoneNumber by remember(ui.user) { mutableStateOf(ui.user?.phoneNumber ?: "") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Profile") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
             if (ui.isLoading) {
                 CircularProgressIndicator()
             }
             
             OutlinedTextField(
                 value = fullName,
                 onValueChange = { fullName = it },
                 label = { Text("Full Name") },
                 modifier = Modifier.fillMaxWidth()
             )
             
             OutlinedTextField(
                 value = phoneNumber,
                 onValueChange = { phoneNumber = it },
                 label = { Text("Phone Number") },
                 modifier = Modifier.fillMaxWidth()
             )
             
             Button(
                 onClick = {
                     ui.user?.let { user ->
                         val updated = user.copy(
                             fullName = fullName,
                             phoneNumber = phoneNumber,
                             updatedAt = java.util.Date()
                         )
                         viewModel.updateUser(updated)
                         onNavigateBack()
                     }
                 },
                 enabled = fullName.isNotBlank() && phoneNumber.isNotBlank() && !ui.isLoading,
                 modifier = Modifier.fillMaxWidth()
             ) {
                 Text("Save Changes")
             }
             
             ui.error?.let {
                 Text(it, color = MaterialTheme.colorScheme.error)
             }
        }
    }
}
