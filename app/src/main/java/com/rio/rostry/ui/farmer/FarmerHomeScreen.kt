package com.rio.rostry.ui.farmer

import androidx.compose.foundation.background
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun FarmerHomeScreen(
    onListProduct: () -> Unit,
    onCheckOrders: () -> Unit,
    onMessageBuyers: () -> Unit,
) {
    Surface(Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Dashboard summary
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                StatCard(title = "Today's Orders", value = "12")
                StatCard(title = "Revenue", value = "₹18,450")
                StatCard(title = "Alerts", value = "2")
            }

            // Health tips carousel
            Column {
                Text("Daily Health Tips", style = MaterialTheme.typography.titleMedium)
                val tips = listOf(
                    "Hydrate birds early morning in heat",
                    "Rotate feed to balance protein",
                    "Disinfect waterers weekly",
                    "Observe stool for early illness signs",
                )
                LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.padding(top = 8.dp)) {
                    items(tips) { tip ->
                        Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)) {
                            Text(tip, modifier = Modifier.padding(12.dp))
                        }
                    }
                }
            }

            // Medication reminders
            Column {
                Text("Medication Reminders", style = MaterialTheme.typography.titleMedium)
                ReminderRow("Broiler Batch A", "Deworming due today")
                Divider()
                ReminderRow("Layer Batch Q", "Vitamin boost tomorrow")
            }

            // Weather widget (placeholder)
            Column(modifier = Modifier.fillMaxWidth()) {
                Text("Weather", style = MaterialTheme.typography.titleMedium)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                        .height(72.dp)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) { Text("28°C • Humid") }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                        .height(72.dp)
                        .background(MaterialTheme.colorScheme.tertiaryContainer),
                    contentAlignment = Alignment.Center
                ) { Text("Rain chance 40%") }
            }

            // Quick actions
            Column(modifier = Modifier.fillMaxWidth()) {
                Text("Quick Actions", style = MaterialTheme.typography.titleMedium)
                Column(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = onListProduct, modifier = Modifier.fillMaxWidth()) { Text("List Product") }
                    OutlinedButton(onClick = onCheckOrders, modifier = Modifier.fillMaxWidth()) { Text("Check Orders") }
                    OutlinedButton(onClick = onMessageBuyers, modifier = Modifier.fillMaxWidth()) { Text("Message Buyers") }
                }
            }

            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun StatCard(title: String, value: String, modifier: Modifier = Modifier) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(Modifier.padding(12.dp)) {
            Text(title, style = MaterialTheme.typography.labelMedium, color = Color.Gray)
            Text(value, style = MaterialTheme.typography.titleLarge)
        }
    }
}

@Composable
private fun ReminderRow(batch: String, message: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(batch, style = MaterialTheme.typography.bodyLarge)
            Text(message, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
        }
        val done = remember { mutableStateOf(false) }
        val text = if (done.value) "Done" else "Mark done"
        OutlinedButton(onClick = { done.value = !done.value }) { Text(text) }
    }
}
