package com.rio.rostry.ui.scan

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Minimal QR scanner placeholder. Allows typing/pasting a Product ID and returns it via onResult.
 * Can be replaced with a CameraX + ML Kit implementation later.
 */
@Composable
fun QrScannerScreen(onResult: (String) -> Unit) {
    var value by remember { mutableStateOf("") }
    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Scan QR")
        ElevatedCard {
            Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = value,
                    onValueChange = { value = it },
                    label = { Text("Product ID") },
                    modifier = Modifier.fillMaxWidth()
                )
                Button(onClick = { if (value.isNotBlank()) onResult(value) }) { Text("Confirm") }
            }
        }
        Text("Tip: This is a placeholder. A CameraX-based scanner can be added later.")
    }
}
