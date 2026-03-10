package com.rio.rostry.ui.farmer.breeding

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun EggCollectionDialog(
    pairId: String,
    onDismiss: () -> Unit,
    onSubmit: (Int, String, Double?) -> Unit
) {
    var count by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var grade by remember { mutableStateOf("A") } // Default grade

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Log Egg Collection") },
        text = {
            Column(modifier = Modifier.padding(top = 8.dp)) {
                OutlinedTextField(
                    value = count,
                    onValueChange = { if (it.all { char -> char.isDigit() }) count = it },
                    label = { Text("Number of Eggs") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = weight,
                    onValueChange = { weight = it }, // Allow decimal
                    label = { Text("Total Weight (g) - Optional") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                )
                // Grade selection could be a dropdown, simplified for now
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val countInt = count.toIntOrNull()
                    if (countInt != null && countInt > 0) {
                        onSubmit(countInt, grade, weight.toDoubleOrNull())
                    }
                },
                enabled = count.toIntOrNull() != null && count.toInt() > 0
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
