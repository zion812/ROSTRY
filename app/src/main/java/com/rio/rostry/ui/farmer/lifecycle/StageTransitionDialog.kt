package com.rio.rostry.ui.farmer.lifecycle

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.rio.rostry.domain.model.LifecycleSubStage

@Composable
fun StageTransitionDialog(
    assetName: String,
    oldStage: String,
    newStage: String,
    onDismiss: () -> Unit,
    onConfirm: (mortalityCount: Int, totalFeedKg: Double, notes: String) -> Unit
) {
    var mortality by remember { mutableStateOf("") }
    var feed by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Stage Transition: $oldStage → $newStage") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("Transitioning $assetName to $newStage. Please record summary data for the previous stage.")
                
                OutlinedTextField(
                    value = mortality,
                    onValueChange = { if (it.all { char -> char.isDigit() }) mortality = it },
                    label = { Text("Total Mortality in $oldStage") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                
                OutlinedTextField(
                    value = feed,
                    onValueChange = { if (it.matches(Regex("^\\d*\\.?\\d*\$"))) feed = it },
                    label = { Text("Total Feed Consumed (kg)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Transition Notes") },
                    minLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(
                        mortality.toIntOrNull() ?: 0,
                        feed.toDoubleOrNull() ?: 0.0,
                        notes
                    )
                }
            ) {
                Text("Confirm Transition")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
