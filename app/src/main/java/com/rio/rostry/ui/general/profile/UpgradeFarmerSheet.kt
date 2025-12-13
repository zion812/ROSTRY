package com.rio.rostry.ui.general.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.rio.rostry.data.database.entity.UserEntity
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpgradeFarmerSheet(
    sheetState: SheetState,
    currentProfile: UserEntity,
    onDismissRequest: () -> Unit,
    onUpgrade: (String, Int, String, Long, String, Double?, Double?) -> Unit
) {
    var address by remember { mutableStateOf(currentProfile.address ?: "") }
    var chickenCount by remember { mutableStateOf("") }
    var farmerType by remember { mutableStateOf("Backyard Farmer") }
    var raisingSince by remember { mutableStateOf("") }
    var favoriteBreed by remember { mutableStateOf("") }
    
    // Location verification state
    var lat by remember { mutableStateOf<Double?>(null) }
    var lng by remember { mutableStateOf<Double?>(null) }
    var isLocating by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context.applicationContext) }

    val locationPermissionLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            isLocating = true
            try {
                fusedLocationClient.getCurrentLocation(
                    Priority.PRIORITY_HIGH_ACCURACY,
                    null
                ).addOnSuccessListener { location ->
                    isLocating = false
                    if (location != null) {
                        lat = location.latitude
                        lng = location.longitude
                        // Optionally reverse geocode here to fill address, but keeping simple for now
                    }
                }.addOnFailureListener {
                    isLocating = false
                    // Handle failure
                }
            } catch (e: SecurityException) {
                isLocating = false
            }
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Become a Farmer",
                style = MaterialTheme.typography.headlineSmall
            )
            Text(
                text = "Tell us about your flock to start your farming journey!",
                style = MaterialTheme.typography.bodyMedium
            )

            // Location Section
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Farm Location / Address") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    maxLines = 3,
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                if (ContextCompat.checkSelfPermission(
                                        context,
                                        android.Manifest.permission.ACCESS_FINE_LOCATION
                                    ) == android.content.pm.PackageManager.PERMISSION_GRANTED
                                ) {
                                    isLocating = true
                                    fusedLocationClient.getCurrentLocation(
                                        Priority.PRIORITY_HIGH_ACCURACY,
                                        null
                                    ).addOnSuccessListener { location ->
                                        isLocating = false
                                        if (location != null) {
                                            lat = location.latitude
                                            lng = location.longitude
                                        }
                                    }
                                } else {
                                    locationPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
                                }
                            }
                        ) {
                            if (isLocating) {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                            } else {
                                Icon(
                                    imageVector = if (lat != null) Icons.Filled.CheckCircle else Icons.Filled.MyLocation,
                                    contentDescription = "Get Location",
                                    tint = if (lat != null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                )
                if (lat != null && lng != null) {
                    Text(
                        text = "Verified Coordinates: $lat, $lng",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                } else {
                    Text(
                        text = "Tap the icon to verify exact location (Recommended)",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.surfaceVariant
                    )
                }
            }

            OutlinedTextField(
                value = chickenCount,
                onValueChange = { if (it.all { char -> char.isDigit() }) chickenCount = it },
                label = { Text("Number of Chickens") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                )
            )

            Column {
                Text("What kind of farmer are you?", style = MaterialTheme.typography.labelLarge)
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
            }

            OutlinedTextField(
                value = raisingSince,
                onValueChange = { if (it.length <= 4 && it.all { char -> char.isDigit() }) raisingSince = it },
                label = { Text("Raising Since (Year)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                ),
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

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = onDismissRequest) {
                    Text("Cancel")
                }
                Spacer(modifier = Modifier.padding(8.dp))
                
                val isFormValid = address.isNotBlank() && 
                                  chickenCount.isNotBlank() && 
                                  raisingSince.length == 4 && 
                                  favoriteBreed.isNotBlank()
                
                android.util.Log.d("UpgradeFarmerSheet", "Recomposition: valid=$isFormValid (addr=${address.isNotBlank()}, count=${chickenCount.isNotBlank()}, year=${raisingSince.length}==4, breed=${favoriteBreed.isNotBlank()})")

                Button(
                    onClick = {
                        android.util.Log.d("UpgradeFarmerSheet", "Upgrade button clicked!")
                        if (!isFormValid) {
                            android.util.Log.w("UpgradeFarmerSheet", "Form invalid on click: addr='$address', count='$chickenCount', year='$raisingSince', breed='$favoriteBreed'")
                            return@Button
                        }
                        android.util.Log.d("UpgradeFarmerSheet", "Form VALID. Proceeding with upgrade.")
                        val count = chickenCount.toIntOrNull() ?: 0
                        val year = raisingSince.toLongOrNull() ?: 0L
                        onUpgrade(address, count, farmerType, year, favoriteBreed, lat, lng)
                    },
                    // TEMPORARILY ENABLE ALWAYS FOR DEBUGGING
                    enabled = true // was: isFormValid
                ) {
                    Text(if (isFormValid) "Upgrade" else "Upgrade (Invalid)")
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
