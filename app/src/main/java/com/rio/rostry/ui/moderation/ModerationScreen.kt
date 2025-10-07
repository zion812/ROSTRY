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
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun ModerationScreen(vm: ModerationViewModel = hiltViewModel()) {
    val reports by vm.openReports.collectAsState()
    val pending by vm.pendingVerifications.collectAsState()
    val stats by vm.verificationStats.collectAsState()
    val selectedTab = remember { mutableIntStateOf(0) }

    Column(Modifier.fillMaxSize().padding(12.dp)) {
        TabRow(selectedTabIndex = selectedTab.intValue) {
            Tab(selected = selectedTab.intValue == 0, onClick = { selectedTab.intValue = 0 }, text = { Text("Reports") })
            Tab(selected = selectedTab.intValue == 1, onClick = { selectedTab.intValue = 1 }, text = { Text("Verifications") })
        }
        when (selectedTab.intValue) {
            0 -> {
                Text("Open Reports", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(vertical = 8.dp))
                LazyColumn(Modifier.fillMaxSize()) {
                    items(reports) { r ->
                        Card(Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
                            Column(Modifier.fillMaxWidth().padding(12.dp)) {
                                Text("${'$'}{r.targetType} • ${'$'}{r.targetId}")
                                Text("Reason: ${'$'}{r.reason}")
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
            1 -> {
                Text("Pending Verifications: ${'$'}{stats.pending}", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(vertical = 8.dp))
                LazyColumn(Modifier.fillMaxSize()) {
                    items(pending) { v ->
                        Card(Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
                            Column(Modifier.fillMaxWidth().padding(12.dp)) {
                                Text("User: ${'$'}{v.userId} • ${'$'}{v.requestType}")
                                v.farmLat?.let { Text("Location: ${'$'}it, ${'$'}{v.farmLng}") }
                                if (v.kycLevel != null) Text("KYC Level: ${'$'}{v.kycLevel}")
                                Text("Docs: ${'$'}{v.documentUrls.size} • Images: ${'$'}{v.imageUrls.size}")
                                Row(Modifier.fillMaxWidth()) {
                                    Button(onClick = { vm.approveVerification(v.userId, v.userType) }, modifier = Modifier.padding(end = 8.dp)) { Text("Approve") }
                                    Button(onClick = { vm.rejectVerification(v.userId, "Insufficient clarity") }) { Text("Reject") }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
