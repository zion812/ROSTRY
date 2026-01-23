package com.rio.rostry.ui.admin.audit

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rio.rostry.data.database.entity.AdminAuditLogEntity
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminAuditScreen(
    viewModel: AdminAuditViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Security Audit Logs") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (uiState.logs.isEmpty()) {
                Text("No logs found.", modifier = Modifier.align(Alignment.Center))
            } else {
                LazyColumn {
                    items(uiState.logs) { log ->
                        AuditLogItem(log)
                        HorizontalDivider()
                    }
                }
            }
        }
    }
}

@Composable
fun AuditLogItem(log: AdminAuditLogEntity) {
    val dateFormatter = remember { SimpleDateFormat("dd MMM HH:mm:ss", Locale.getDefault()) }

    ListItem(
        headlineContent = { 
            Text(
                text = "${log.actionType} by ${log.adminName ?: log.adminId.take(8)}", 
                fontWeight = FontWeight.Bold
            ) 
        },
        supportingContent = {
            Column {
                Text("Target: ${log.targetType}:${log.targetId ?: "N/A"}")
                if (!log.details.isNullOrBlank()) {
                    Text("Details: ${log.details}", style = MaterialTheme.typography.bodySmall)
                }
            }
        },
        trailingContent = {
            Text(
                text = dateFormatter.format(Date(log.timestamp)),
                style = MaterialTheme.typography.labelSmall
            )
        }
    )
}
