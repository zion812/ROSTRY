package com.rio.rostry.ui.moderation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun ModerationScreen(vm: ModerationViewModel = hiltViewModel()) {
    val reports by vm.openReports.collectAsState()
    Column(Modifier.fillMaxSize().padding(12.dp)) {
        Text("Open Reports", style = MaterialTheme.typography.titleMedium)
        LazyColumn(Modifier.fillMaxSize()) {
            items(reports) { r ->
                Card(Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
                    Column(Modifier.fillMaxWidth().padding(12.dp)) {
                        Text("${r.targetType} • ${r.targetId}")
                        Text("Reason: ${r.reason}")
                        Row(Modifier.fillMaxWidth()) {
                            Button(onClick = { vm.updateStatus(r.reportId, "UNDER_REVIEW") }, modifier = Modifier.padding(end = 8.dp)) { Text("Review") }
                            Button(onClick = { vm.updateStatus(r.reportId, "RESOLVED") }, modifier = Modifier.padding(end = 8.dp)) { Text("Resolve") }
                            Button(onClick = { vm.updateStatus(r.reportId, "REJECTED") }) { Text("Reject") }
                        }
                    }
                }
            }
        }
    }
}
