package com.rio.rostry.ui.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.rio.rostry.data.models.UserType
import com.rio.rostry.ui.components.RostryDropdown
import com.rio.rostry.ui.components.RostryTextField

@Composable
fun ProfileSetupScreen(
    onProfileSetupComplete: (String, String, UserType, String, () -> Unit, (String) -> Unit) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var userType: UserType by remember { mutableStateOf(UserType.General) }
    var language by remember { mutableStateOf("English") }
    var isLoading by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Profile Setup", style = androidx.compose.material3.MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        RostryTextField(
            value = name,
            onValueChange = { name = it },
            label = "Name"
        )

        RostryTextField(
            value = location,
            onValueChange = { location = it },
            label = "Location"
        )

        val userTypes = listOf(UserType.General, UserType.Farmer, UserType.HighLevelEnthusiast)
        RostryDropdown(
            label = "User Type",
            items = userTypes.map { it.javaClass.simpleName },
            selectedItem = userType.javaClass.simpleName,
            onItemSelected = { selectedName ->
                userType = userTypes.find { it.javaClass.simpleName == selectedName } ?: UserType.General
            }
        )

        RostryDropdown(
            label = "Language",
            items = listOf("English", "Spanish", "French"), // Add more languages as needed
            selectedItem = language,
            onItemSelected = { language = it }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            isLoading = true
            onProfileSetupComplete(name, location, userType, language, {
                isLoading = false // Ensure isLoading is reset on success
                Toast.makeText(context, "Profile Setup Successful", Toast.LENGTH_SHORT).show()
            }, {
                isLoading = false
                Toast.makeText(context, "Profile Setup Failed: $it", Toast.LENGTH_SHORT).show()
            })
        }) {
            if (isLoading) {
                CircularProgressIndicator()
            } else {
                Text(text = "Complete Profile")
            }
        }
    }
}
