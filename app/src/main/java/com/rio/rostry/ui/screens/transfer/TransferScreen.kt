package com.rio.rostry.ui.screens.transfer

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.rio.rostry.data.models.TransferLog
import com.rio.rostry.data.models.TransferStatus
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun TransferScreen(
    transfers: List<TransferLog>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Transfers",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (transfers.isEmpty()) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    text = "No transfers yet.",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        } else {
            // Summary cards
            TransferSummary(transfers)
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Transfer list
            Text(
                text = "Transfer History",
                style = MaterialTheme.typography.titleMedium
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            transfers.forEach { transfer ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        // Status header
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = getStatusIcon(transfer.status),
                                contentDescription = "Status",
                                tint = getStatusColor(transfer.status),
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = transfer.status.name,
                                style = MaterialTheme.typography.titleMedium,
                                color = getStatusColor(transfer.status)
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            Text(
                                text = formatDate(transfer.timestamp),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        
                        Divider(modifier = Modifier.padding(vertical = 8.dp))
                        
                        // Transfer details
                        DetailItem("Transfer ID", transfer.transferId.take(8))
                        DetailItem("Fowl ID", transfer.fowlId.take(8))
                        DetailItem("From", transfer.giverId.take(8))
                        DetailItem("To", transfer.receiverId.take(8))
                        
                        // Proof section
                        if (transfer.proofUrls.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Proof Documents",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            transfer.proofUrls.forEach { url ->
                                Text(
                                    text = url,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TransferSummary(transfers: List<TransferLog>) {
    val pendingCount = transfers.count { it.status == TransferStatus.PENDING }
    val verifiedCount = transfers.count { it.status == TransferStatus.VERIFIED }
    val rejectedCount = transfers.count { it.status == TransferStatus.REJECTED }
    
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Summary",
                style = MaterialTheme.typography.titleMedium
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                SummaryItem("Pending", pendingCount, MaterialTheme.colorScheme.primary)
                SummaryItem("Verified", verifiedCount, MaterialTheme.colorScheme.secondary)
                SummaryItem("Rejected", rejectedCount, MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
fun SummaryItem(label: String, count: Int, color: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = count.toString(),
            style = MaterialTheme.typography.headlineMedium,
            color = color
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
fun DetailItem(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
    ) {
        Text(
            text = "$label: ",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(2f)
        )
    }
}

@Composable
private fun getStatusIcon(status: TransferStatus) = when (status) {
    TransferStatus.PENDING -> Icons.Default.Info
    TransferStatus.VERIFIED -> Icons.Default.Check
    TransferStatus.REJECTED -> Icons.Default.Warning
}

@Composable
private fun getStatusColor(status: TransferStatus) = when (status) {
    TransferStatus.PENDING -> MaterialTheme.colorScheme.primary
    TransferStatus.VERIFIED -> MaterialTheme.colorScheme.secondary
    TransferStatus.REJECTED -> MaterialTheme.colorScheme.error
}

private fun formatDate(timestamp: Long): String {
    return try {
        val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        sdf.format(Date(timestamp))
    } catch (e: Exception) {
        "Unknown"
    }
}