package com.rio.rostry.ui.general.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.rio.rostry.data.database.entity.UserEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpgradeEnthusiastSheet(
    sheetState: SheetState,
    currentProfile: UserEntity,
    onDismissRequest: () -> Unit,
    onUpgrade: (String, Int, String, Long, String) -> Unit
) {
    var address by remember { mutableStateOf(currentProfile.address ?: "") }
    var chickenCount by remember { mutableStateOf("") }
    var farmerType by remember { mutableStateOf("Backyard Farmer") }
    var raisingSince by remember { mutableStateOf("") }
    var favoriteBreed by remember { mutableStateOf("") }

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
                text = "Become an Enthusiast",
                style = MaterialTheme.typography.headlineSmall
            )
            Text(
                text = "Tell us about your flock to unlock advanced features!",
                style = MaterialTheme.typography.bodyMedium
            )

            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                label = { Text("Farm Location / Address") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                maxLines = 3
            )

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
                Button(
                    onClick = {
                        val count = chickenCount.toIntOrNull() ?: 0
                        val year = raisingSince.toLongOrNull() ?: 0L
                        onUpgrade(address, count, farmerType, year, favoriteBreed)
                    },
                    enabled = address.isNotBlank() && chickenCount.isNotBlank() && raisingSince.length == 4 && favoriteBreed.isNotBlank()
                ) {
                    Text("Upgrade")
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
