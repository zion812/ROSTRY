package com.rio.rostry.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics

/**
 * Dialog for prompting farmers to add a purchased product to their farm monitoring system.
 */
@Composable
fun AddToFarmDialog(
    productName: String,
    productId: String,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit,
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Filled.Agriculture,
                contentDescription = "Add to farm",
                tint = MaterialTheme.colorScheme.primary
            )
        },
        title = {
            Text(
                text = "Add to Your Farm?",
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Text(
                text = "Track $productName's health, growth, and vaccination schedule in your farm monitoring system.",
                style = MaterialTheme.typography.bodyMedium
            )
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(productId) },
                enabled = !isLoading,
                modifier = Modifier.semantics {
                    contentDescription = "Confirm add to farm"
                }
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.then(Modifier)
                    )
                } else {
                    Text("Yes, Add to Farm")
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isLoading,
                modifier = Modifier.semantics {
                    contentDescription = "Dismiss dialog"
                }
            ) {
                Text("Not Now")
            }
        },
        modifier = modifier.semantics {
            contentDescription = "Dialog prompting to add product to farm monitoring"
        }
    )
}
