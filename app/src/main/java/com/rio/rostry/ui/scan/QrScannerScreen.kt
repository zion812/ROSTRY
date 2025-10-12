package com.rio.rostry.ui.scan

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Minimal QR scanner placeholder. Allows typing/pasting a Product ID and returns it via onResult.
 * Can be replaced with a CameraX + ML Kit implementation later.
 */
@Composable
fun QrScannerScreen(
    onResult: (String) -> Unit,
    onValidate: ((String) -> Boolean)? = null,
    hint: String = "Product ID or rostry://product/{id}"
) {
    val vm: QrScannerViewModel = hiltViewModel()
    var value by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Scan QR")
        ElevatedCard {
            Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = value,
                    onValueChange = { value = it },
                    label = { Text(hint) },
                    modifier = Modifier.fillMaxWidth()
                )
                if (error != null) {
                    Text(error!!, color = androidx.compose.ui.graphics.Color.Red)
                }
                Button(onClick = {
                    val input = value.trim()
                    if (input.isBlank()) return@Button
                    val productId = parseProductId(input)
                    if (productId.isBlank()) {
                        error = "Invalid QR content"
                        return@Button
                    }
                    // Validate existence using DAO via ViewModel
                    CoroutineScope(Dispatchers.Main).launch {
                        val exists = vm.productExists(productId)
                        if (!exists || (onValidate != null && !onValidate(productId))) {
                            error = "Product not found. Scan a valid ROSTRY QR code."
                            return@launch
                        }
                        error = null
                        onResult(productId)
                    }
                }) { Text("Confirm") }
                TextButton(onClick = { value = "" }) { Text("Clear") }
            }
        }
        Text("Tip: This is a placeholder. A CameraX-based scanner can be added later.")
    }
}

private fun parseProductId(text: String): String {
    val trimmed = text.trim()
    val prefix = "rostry://product/"
    return if (trimmed.startsWith(prefix, ignoreCase = true)) {
        trimmed.removePrefix(prefix).takeWhile { !it.isWhitespace() && it != '?' && it != '#' }
    } else trimmed
}
