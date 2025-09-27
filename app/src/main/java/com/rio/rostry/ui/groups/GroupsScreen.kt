package com.rio.rostry.ui.groups

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun GroupsScreen(onBack: () -> Unit, vm: GroupsViewModel = hiltViewModel()) {
    val ui by vm.ui.collectAsStateWithLifecycle()

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Community Groups", style = MaterialTheme.typography.titleLarge)
        if (ui.error != null) {
            Text("Error: ${ui.error}", color = MaterialTheme.colorScheme.error)
        }
        if (ui.groups.isEmpty()) {
            Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.Center) {
                CircularProgressIndicator(modifier = Modifier.padding(24.dp))
                Text("No groups found yet")
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxSize()) {
                items(ui.groups) { group ->
                    val isMember = ui.memberships[group.groupId] == true
                    Card(Modifier.fillMaxWidth()) {
                        Column(Modifier.fillMaxWidth().padding(16.dp)) {
                            Text(group.name, style = MaterialTheme.typography.titleMedium)
                            group.description?.let { Text(it, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(top = 4.dp)) }
                            Row(Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(group.category ?: "General", style = MaterialTheme.typography.labelMedium)
                                Button(onClick = {
                                    if (isMember) vm.leave(group.groupId) else vm.join(group.groupId)
                                }) {
                                    Text(if (isMember) "Leave" else "Join")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
