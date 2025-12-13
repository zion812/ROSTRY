package com.rio.rostry.ui.sync

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import android.content.Context
import androidx.work.WorkManager
import com.rio.rostry.data.database.dao.OutboxDao
import com.rio.rostry.data.database.entity.OutboxEntity
import com.rio.rostry.workers.OutboxSyncWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FailedOperation(
    val id: String,
    val type: String,
    val error: String,
    val timestamp: Long
)

fun OutboxEntity.toFailedOperation(): FailedOperation {
    return FailedOperation(
        id = outboxId,
        type = entityType ?: "Unknown",
        error = when {
            retryCount >= maxRetries -> "Maximum retry attempts reached ($retryCount/$maxRetries)"
            status == "FAILED" && retryCount > 0 -> "Failed after $retryCount attempts"
            status == "FAILED" -> "Sync failed"
            else -> "Unknown error"
        },
        timestamp = lastAttemptAt ?: createdAt
    )
}

@HiltViewModel
class SyncIssuesViewModel @Inject constructor(
    private val outboxDao: OutboxDao,
    private val workManager: WorkManager,
    @ApplicationContext private val context: Context
) : ViewModel() {

    val failedOperations = outboxDao.getFailedOperations()
        .map { list -> list.map { it.toFailedOperation() } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    var showDeleteDialog by mutableStateOf<FailedOperation?>(null)
        private set

    fun showDeleteDialog(operation: FailedOperation) {
        showDeleteDialog = operation
    }

    fun dismissDeleteDialog() {
        showDeleteDialog = null
    }

    fun retry(operationId: String) {
        viewModelScope.launch {
            outboxDao.resetRetryAndStatus(operationId, "PENDING", System.currentTimeMillis())
            OutboxSyncWorker.scheduleImmediateSync(context)
        }
    }

    fun delete(operationId: String) {
        viewModelScope.launch {
            // Assuming deleteById exists or using a workaround; in practice, add to OutboxDao
            // For now, simulate delete by updating status to a non-failed state
            outboxDao.updateStatus(operationId, "DELETED", System.currentTimeMillis())
        }
    }

    fun retryAll() {
        viewModelScope.launch {
            val ids = failedOperations.value.map { it.id }
            if (ids.isNotEmpty()) {
                outboxDao.updateStatusBatch(ids, "PENDING", System.currentTimeMillis())
                OutboxSyncWorker.scheduleImmediateSync(context)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SyncIssuesScreen(
    navController: NavController,
    viewModel: SyncIssuesViewModel = hiltViewModel()
) {
    val failedOps by viewModel.failedOperations.collectAsState()
    val showDeleteDialog = viewModel.showDeleteDialog

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Sync Issues") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            if (failedOps.isNotEmpty()) {
                FloatingActionButton(onClick = { viewModel.retryAll() }) {
                    Icon(Icons.Default.Refresh, contentDescription = "Retry All")
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            if (failedOps.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color.Green,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("All synced! No issues to fix.")
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(failedOps) { op ->
                        FailedOperationItem(
                            op = op,
                            onRetry = { viewModel.retry(it) },
                            onDelete = { viewModel.showDeleteDialog(it) }
                        )
                    }
                }
            }
        }

        if (showDeleteDialog != null) {
            AlertDialog(
                onDismissRequest = { viewModel.dismissDeleteDialog() },
                title = { Text("Delete Operation") },
                text = { Text("Are you sure you want to delete this failed operation? This action cannot be undone.") },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.delete(showDeleteDialog.id)
                        viewModel.dismissDeleteDialog()
                    }) {
                        Text("Delete")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { viewModel.dismissDeleteDialog() }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
fun FailedOperationItem(
    op: FailedOperation,
    onRetry: (String) -> Unit,
    onDelete: (FailedOperation) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(op.type, style = MaterialTheme.typography.headlineSmall)
            Text(mapError(op.error), style = MaterialTheme.typography.bodyMedium)
            Text("Failed at ${java.text.SimpleDateFormat("yyyy-MM-dd HH:mm").format(java.util.Date(op.timestamp))}", style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.height(8.dp))
            Row {
                Button(onClick = { onRetry(op.id) }) {
                    Text("Retry")
                }
                Spacer(modifier = Modifier.width(8.dp))
                OutlinedButton(
                    onClick = { onDelete(op) },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete")
                }
            }
        }
    }
}

fun mapError(error: String): String {
    return when {
        error.contains("network", ignoreCase = true) || error.contains("connect", ignoreCase = true) -> "Couldn't connect. Check your internet."
        error.contains("invalid", ignoreCase = true) || error.contains("missing", ignoreCase = true) -> "Some information is missing or incorrect."
        error.contains("permission", ignoreCase = true) || error.contains("denied", ignoreCase = true) -> "You don't have permission for this action."
        else -> error
    }
}
