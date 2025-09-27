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
import androidx.hilt.navigation.compose.hiltViewModel
import com.rio.rostry.ui.components.EmptyState
import com.rio.rostry.ui.theme.LocalSpacing

@Composable
fun ReportsScreen(vm: ReportsViewModel = hiltViewModel()) {
    val reports = vm.reports.collectAsState().value
    val context = LocalContext.current
    val sp = LocalSpacing.current
    Column(Modifier.fillMaxSize().padding(sp.lg)) {
        Text("Reports", style = MaterialTheme.typography.titleLarge)
        Button(onClick = { vm.generateWeeklyPdf() }, modifier = Modifier.padding(top = sp.xs)) {
            Text("Generate Weekly PDF")
        }
        Button(onClick = { vm.generateWeeklyCsv() }, modifier = Modifier.padding(top = sp.xs)) {
            Text("Generate Weekly CSV")
        }
        if (reports.isEmpty()) {
            EmptyState(
                title = "No reports yet",
                message = "Generate a PDF or CSV report to get started.",
                primaryActionLabel = "Generate PDF",
                onPrimaryAction = { vm.generateWeeklyPdf() },
                modifier = Modifier.fillMaxSize()
            )
        } else {
            LazyColumn(Modifier.fillMaxSize().padding(top = sp.xs)) {
                items(reports) { r ->
                    Card(Modifier.fillMaxWidth().padding(vertical = sp.xs)) {
                        Column(Modifier.fillMaxWidth().padding(sp.sm)) {
                            Text("${r.type} • ${r.format}", style = MaterialTheme.typography.titleMedium)
                            Text("From ${r.periodStart} to ${r.periodEnd}", style = MaterialTheme.typography.bodyMedium)
                            r.uri?.let { uriStr ->
                                Button(onClick = {
                                    val intent = Intent(Intent.ACTION_SEND).apply {
                                        type = if (r.format == "CSV") "text/csv" else "application/pdf"
                                        putExtra(Intent.EXTRA_STREAM, android.net.Uri.parse(uriStr))
                                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                    }
                                    context.startActivity(Intent.createChooser(intent, "Share report"))
                                }, modifier = Modifier.padding(top = sp.xs)) {
                                    Text("Share")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
