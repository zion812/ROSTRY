package com.rio.rostry.ui.auth

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import android.widget.Toast
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.rio.rostry.data.models.UserType
import com.rio.rostry.ui.components.RostryDropdown
import com.rio.rostry.ui.components.RostryTextField

@Composable
fun ProfileSetupScreen(
    onProfileSetupComplete: (String, String, UserType, String, String, Uri?, () -> Unit, (String) -> Unit) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var userType: UserType by remember { mutableStateOf(UserType.General) }
    var language by remember { mutableStateOf("English") }
    var bio by remember { mutableStateOf("" )}
    var profileImageUri by remember { mutableStateOf<Uri?>(null) }
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

        RostryTextField(
            value = bio,
            onValueChange = { bio = it },
            label = "Bio"
        )

        val launcher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent()
        ) { uri: Uri? ->
            profileImageUri = uri
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = { launcher.launch("image/*") }) {
            Text("Select Profile Image")
        }

        profileImageUri?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Image(
                painter = rememberAsyncImagePainter(it),
                contentDescription = "Profile Image",
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            isLoading = true
            onProfileSetupComplete(name, location, userType, language, bio, profileImageUri, {
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
