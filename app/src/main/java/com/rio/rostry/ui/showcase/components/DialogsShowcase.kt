package com.rio.rostry.ui.showcase.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.*
import androidx.compose.runtime.*
import com.rio.rostry.ui.theme.Dimens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialogsShowcase() {
    var open by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(Dimens.space_large)) {
        Text("Dialogs", style = MaterialTheme.typography.titleLarge)
        Button(onClick = { open = true }) { Text("Open dialog") }
    }

    if (open) {
        AlertDialog(
            onDismissRequest = { open = false },
            title = { Text("Confirm action") },
            text = { Text("This is a sample Material 3 AlertDialog.") },
            confirmButton = { TextButton(onClick = { open = false }) { Text("OK") } },
            dismissButton = { TextButton(onClick = { open = false }) { Text("Cancel") } }
        )
    }
}
