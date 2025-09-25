package com.rio.rostry.ui.social

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun LiveBroadcastScreen(
    onBack: () -> Unit
) {
    var title by rememberSaveable { mutableStateOf("") }
    var isLive by remember { mutableStateOf(false) }
    var viewers by remember { mutableIntStateOf(0) }
    var audience by rememberSaveable { mutableIntStateOf(0) }

    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Live Broadcasting Studio")
        ElevatedCard {
            Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth()
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = { audience = 0 }, enabled = audience != 0) { Text("Public") }
                    OutlinedButton(onClick = { audience = 1 }, enabled = audience != 1) { Text("Followers") }
                    OutlinedButton(onClick = { audience = 2 }, enabled = audience != 2) { Text("Verified") }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (!isLive) {
                        Button(onClick = { isLive = true; viewers = 1 }, enabled = title.isNotBlank()) { Text("Go Live") }
                    } else {
                        Button(onClick = { isLive = false; viewers = 0 }) { Text("End Live") }
                    }
                    OutlinedButton(onClick = onBack) { Text("Back") }
                }
                Text("Status: " + if (isLive) "LIVE • Viewers: $viewers" else "Offline")
            }
        }
        ElevatedCard {
            Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Chat & Activity (coming soon)")
                Text("• Real-time chat, reactions, and viewer list will appear here during live.")
            }
        }
    }
}
