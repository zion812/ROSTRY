package com.rio.rostry.ui.analytics

import android.content.Intent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun ReportsScreen(vm: ReportsViewModel = hiltViewModel()) {
    val reports = vm.reports.collectAsState().value
    val context = LocalContext.current
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Reports", style = MaterialTheme.typography.titleMedium)
        Button(onClick = { vm.generateWeeklyPdf() }, modifier = Modifier.padding(top = 8.dp)) {
            Text("Generate Weekly PDF")
        }
        Button(onClick = { vm.generateWeeklyCsv() }, modifier = Modifier.padding(top = 8.dp)) {
            Text("Generate Weekly CSV")
        }
        LazyColumn(Modifier.fillMaxSize().padding(top = 8.dp)) {
            items(reports) { r ->
                Card(Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
                    Column(Modifier.fillMaxWidth().padding(12.dp)) {
                        Text("${r.type} • ${r.format}")
                        Text("From ${r.periodStart} to ${r.periodEnd}")
                        r.uri?.let { uriStr ->
                            Button(onClick = {
                                val intent = Intent(Intent.ACTION_SEND).apply {
                                    type = if (r.format == "CSV") "text/csv" else "application/pdf"
                                    putExtra(Intent.EXTRA_STREAM, android.net.Uri.parse(uriStr))
                                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                }
                                context.startActivity(Intent.createChooser(intent, "Share report"))
                            }, modifier = Modifier.padding(top = 8.dp)) {
                                Text("Share")
                            }
                        }
                    }
                }
            }
        }
    }
}
