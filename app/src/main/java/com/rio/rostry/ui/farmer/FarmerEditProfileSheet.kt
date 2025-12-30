package com.rio.rostry.ui.farmer

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.rio.rostry.data.database.entity.UserEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FarmerEditProfileSheet(
    sheetState: SheetState,
    currentProfile: UserEntity,
    onDismissRequest: () -> Unit,
    onSave: (UserEntity) -> Unit
) {
    var fullName by remember { mutableStateOf(currentProfile.fullName ?: "") }
    var email by remember { mutableStateOf(currentProfile.email ?: "") }
    var phoneNumber by remember { mutableStateOf(currentProfile.phoneNumber ?: "") }
    var bio by remember { mutableStateOf(currentProfile.bio ?: "") }
    var address by remember { mutableStateOf(currentProfile.address ?: "") }
    
    // Farmer specific
    var chickenCount by remember { mutableStateOf(currentProfile.chickenCount?.toString() ?: "") }
    var farmerType by remember { mutableStateOf(currentProfile.farmerType ?: "Backyard Farmer") }
    var raisingSince by remember { mutableStateOf(currentProfile.raisingSince?.toString() ?: "") }
    var favoriteBreed by remember { mutableStateOf(currentProfile.favoriteBreed ?: "") }

    // Farm Address details (optional, if we want to be granular)
    var farmAddressLine1 by remember { mutableStateOf(currentProfile.farmAddressLine1 ?: "") }
    var farmAddressLine2 by remember { mutableStateOf(currentProfile.farmAddressLine2 ?: "") }
    var farmCity by remember { mutableStateOf(currentProfile.farmCity ?: "") }
    var farmState by remember { mutableStateOf(currentProfile.farmState ?: "") }
    var farmPostalCode by remember { mutableStateOf(currentProfile.farmPostalCode ?: "") }
    var farmCountry by remember { mutableStateOf(currentProfile.farmCountry ?: "") }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Edit Farm Profile",
                style = MaterialTheme.typography.headlineSmall
            )

            // Basic Info
            OutlinedTextField(
                value = fullName,
                onValueChange = { fullName = it },
                label = { Text("Farm / Farmer Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next)
            )

            OutlinedTextField(
                value = phoneNumber,
                onValueChange = { phoneNumber = it },
                label = { Text("Phone Number") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Next)
            )

            OutlinedTextField(
                value = bio,
                onValueChange = { bio = it },
                label = { Text("Bio / Farm Description") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                maxLines = 4
            )

            // Location
            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                label = { Text("Primary Location") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                maxLines = 3
            )
            
            Divider()
            Text("Farm Details", style = MaterialTheme.typography.titleMedium)

            OutlinedTextField(
                value = chickenCount,
                onValueChange = { if (it.all { char -> char.isDigit() }) chickenCount = it },
                label = { Text("Bird Count") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next)
            )

            Column {
                Text("Farmer Type", style = MaterialTheme.typography.labelMedium)
                Row(
                    Modifier
                        .fillMaxWidth()
                        .selectable(
                            selected = farmerType == "Backyard Farmer",
                            onClick = { farmerType = "Backyard Farmer" }
                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = farmerType == "Backyard Farmer",
                        onClick = { farmerType = "Backyard Farmer" }
                    )
                    Text("Backyard Farmer")
                }
                Row(
                    Modifier
                        .fillMaxWidth()
                        .selectable(
                            selected = farmerType == "Love to Raise",
                            onClick = { farmerType = "Love to Raise" }
                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = farmerType == "Love to Raise",
                        onClick = { farmerType = "Love to Raise" }
                    )
                    Text("Love to Raise")
                }
                 Row(
                    Modifier
                        .fillMaxWidth()
                        .selectable(
                            selected = farmerType == "Commercial",
                            onClick = { farmerType = "Commercial" }
                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = farmerType == "Commercial",
                        onClick = { farmerType = "Commercial" }
                    )
                    Text("Commercial")
                }
            }

            OutlinedTextField(
                value = raisingSince,
                onValueChange = { if (it.length <= 4 && it.all { char -> char.isDigit() }) raisingSince = it },
                label = { Text("Raising Since (Year)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
                placeholder = { Text("e.g. 2020") }
            )

            OutlinedTextField(
                value = favoriteBreed,
                onValueChange = { favoriteBreed = it },
                label = { Text("Favorite Breed") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
            )

            // Detailed Address (Optional)
            if (address.isNotBlank()) {
                 OutlinedTextField(
                    value = farmAddressLine1,
                    onValueChange = { farmAddressLine1 = it },
                    label = { Text("Farm Address Line 1 (Optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = farmAddressLine2,
                    onValueChange = { farmAddressLine2 = it },
                    label = { Text("Farm Address Line 2 (Optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                 OutlinedTextField(
                    value = farmCity,
                    onValueChange = { farmCity = it },
                    label = { Text("City (Optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = farmState,
                    onValueChange = { farmState = it },
                    label = { Text("State (Optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = farmPostalCode,
                    onValueChange = { farmPostalCode = it },
                    label = { Text("Postal Code (Optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = farmCountry,
                    onValueChange = { farmCountry = it },
                    label = { Text("Country (Optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = onDismissRequest) {
                    Text("Cancel")
                }
                Spacer(modifier = Modifier.padding(8.dp))
                Button(
                    onClick = { 
                        val updatedUser = currentProfile.copy(
                            fullName = fullName,
                            email = email,
                            phoneNumber = phoneNumber.takeIf { it.isNotBlank() },
                            bio = bio,
                            address = address,
                            chickenCount = chickenCount.toIntOrNull(),
                            farmerType = farmerType,
                            raisingSince = raisingSince.toLongOrNull(),
                            favoriteBreed = favoriteBreed,
                            farmAddressLine1 = farmAddressLine1.takeIf { it.isNotBlank() },
                            farmAddressLine2 = farmAddressLine2.takeIf { it.isNotBlank() },
                            farmCity = farmCity.takeIf { it.isNotBlank() },
                            farmState = farmState.takeIf { it.isNotBlank() },
                            farmPostalCode = farmPostalCode.takeIf { it.isNotBlank() },
                            farmCountry = farmCountry.takeIf { it.isNotBlank() }
                        )
                        onSave(updatedUser) 
                    }
                ) {
                    Text("Save Changes")
                }
            }
            Spacer(modifier = Modifier.height(24.dp)) 
        }
    }
}
