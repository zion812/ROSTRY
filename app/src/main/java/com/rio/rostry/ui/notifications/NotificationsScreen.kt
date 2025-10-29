package com.rio.rostry.ui.notifications

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun NotificationsScreen(
    vm: NotificationsViewModel,
    onOpenMessages: () -> Unit,
    onOpenOrders: () -> Unit,
    onBack: () -> Unit,
    onOpenRoute: (String) -> Unit,
) {
    val state by vm.ui.collectAsState()
    LaunchedEffect(Unit) { vm.refresh() }

    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Notifications", style = MaterialTheme.typography.titleLarge)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(onClick = onBack, modifier = Modifier.weight(1f)) { Text("Back") }
            Button(onClick = onOpenMessages, modifier = Modifier.weight(1f)) { Text("Messages (${state.unreadMessages})") }
            Button(onClick = onOpenOrders, modifier = Modifier.weight(1f)) { Text("Orders (${state.pendingOrders})") }
        }
        Divider()
        if (state.items.isEmpty()) {
            com.rio.rostry.ui.components.EmptyState(
                title = "You're all caught up",
                subtitle = "No new notifications",
                modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp)
            )
        } else {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(items = state.items, key = { it.hashCode() }) { n ->
                    Card {
                        Column(Modifier.padding(12.dp)) {
                            Text(n.title, style = MaterialTheme.typography.titleMedium)
                            Text(n.body, modifier = Modifier.padding(top = 4.dp))
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                            ) {
                                Button(onClick = { onOpenRoute(n.route) }) { Text("Open") }
                            }
                        }
                    }
                }
            }
        }
    }
}

