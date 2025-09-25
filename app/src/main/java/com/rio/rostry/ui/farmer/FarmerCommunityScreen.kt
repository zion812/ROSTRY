package com.rio.rostry.ui.farmer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun FarmerCommunityScreen(
    onOpenThread: (String) -> Unit,
    onOpenGroupDirectory: () -> Unit,
    onOpenExpertBooking: () -> Unit,
    onOpenRegionalNews: () -> Unit,
) {
    Surface(Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Community", style = MaterialTheme.typography.titleLarge)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                Button(onClick = onOpenGroupDirectory, modifier = Modifier.weight(1f)) { Text("Groups") }
                Button(onClick = onOpenExpertBooking, modifier = Modifier.weight(1f)) { Text("Expert Consult") }
                OutlinedButton(onClick = onOpenRegionalNews, modifier = Modifier.weight(1f)) { Text("Regional News") }
            }
            Divider()
            val threads = listOf(
                ThreadPreview("t-101", "Buyer queries about broilers", "Last: Can you deliver by Friday?"),
                ThreadPreview("t-102", "Vet advice: respiratory issues", "Last: Increase ventilation")
            )
            Text("Messages", style = MaterialTheme.typography.titleMedium)
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(threads) { t ->
                    Card { Column(Modifier.padding(12.dp)) {
                        Text(t.title, style = MaterialTheme.typography.titleMedium)
                        Text(t.snippet, modifier = Modifier.padding(top = 4.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 8.dp)) {
                            Button(onClick = { onOpenThread(t.id) }) { Text("Open") }
                            OutlinedButton(onClick = { /* mute */ }) { Text("Mute") }
                        }
                    } }
                }
            }
        }
    }
}

data class ThreadPreview(val id: String, val title: String, val snippet: String)
