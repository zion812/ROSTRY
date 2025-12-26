package com.rio.rostry.ui.upgrade

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.rio.rostry.data.database.entity.RoleMigrationEntity
import com.rio.rostry.utils.Resource

@Composable
fun MigrationProgressDialog(
    status: Resource<RoleMigrationEntity?>,
    onDismiss: () -> Unit,
    onComplete: () -> Unit
) {
    Dialog(onDismissRequest = { /* Prevent dismissal during critical migration */ }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Role Migration",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                when (status) {
                    is Resource.Loading -> {
                        CircularProgressIndicator()
                        Text("Initializing migration...")
                    }
                    is Resource.Success -> {
                        val entity = status.data
                        if (entity == null) {
                            Text("No active migration found.")
                            TextButton(onClick = onDismiss) { Text("Close") }
                        } else {
                            MigrationStatusContent(entity, onDismiss, onComplete)
                        }
                    }
                    is Resource.Error -> {
                        Icon(Icons.Default.Error, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(48.dp))
                        Text("Migration Failed", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                        Text(status.message ?: "An unexpected error occurred.")
                        Button(onClick = onDismiss) { Text("Close") }
                    }
                }
            }
        }
    }
}

@Composable
private fun MigrationStatusContent(
    entity: RoleMigrationEntity,
    onDismiss: () -> Unit,
    onComplete: () -> Unit
) {
    val progress = entity.progressPercentage / 100f
    
    LinearProgressIndicator(
        progress = progress,
        modifier = Modifier.fillMaxWidth().height(8.dp),
        strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
    )
    
    Text(
        text = "${entity.progressPercentage.toInt()}%",
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.Bold
    )

    Text(
        text = when (entity.status) {
            RoleMigrationEntity.STATUS_PENDING -> "Preparing migration..."
            RoleMigrationEntity.STATUS_IN_PROGRESS -> "Moving your farm data to the cloud..."
            RoleMigrationEntity.STATUS_COMPLETED -> "Migration successful!"
            RoleMigrationEntity.STATUS_FAILED -> "Migration failed."
            RoleMigrationEntity.STATUS_ROLLED_BACK -> "Changes rolled back."
            else -> "Processing..."
        },
        style = MaterialTheme.typography.bodyMedium
    )

    if (entity.status == RoleMigrationEntity.STATUS_COMPLETED) {
        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF4CAF50), modifier = Modifier.size(48.dp))
        Button(
            onClick = onComplete,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Go to Enthusiast Dashboard")
        }
    } else if (entity.status == RoleMigrationEntity.STATUS_FAILED || entity.status == RoleMigrationEntity.STATUS_ROLLED_BACK) {
        Icon(Icons.Default.Error, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(48.dp))
        entity.errorMessage?.let {
            Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }
        Button(
            onClick = onDismiss,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Back to Role Upgrade")
        }
    }
}
