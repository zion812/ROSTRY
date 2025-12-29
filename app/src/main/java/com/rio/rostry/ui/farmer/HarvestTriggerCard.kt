package com.rio.rostry.ui.farmer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rio.rostry.data.database.entity.FarmAlertEntity

/**
 * HarvestTriggerCard - Displays when a batch reaches market-ready status.
 * Shows batch details and provides a "Sell Now" action to create a listing.
 * 
 * Part of the Farmer-First Sales Pipeline (Phase 2).
 */
@Composable
fun HarvestTriggerCard(
    alert: FarmAlertEntity,
    onSellNow: (String) -> Unit,
    onDismiss: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // Parse data from message and actionRoute
    // Message format: "Batch is market-ready! 100 birds, 1500g avg, 6 weeks old"
    // ActionRoute format: "farmer/create_listing/{assetId}"
    val metadata = remember(alert.message, alert.actionRoute) {
        try {
            val message = alert.message
            // Extract batchId from actionRoute
            val batchId = alert.actionRoute?.substringAfterLast("/") ?: ""
            // Parse numbers from message using regex
            val quantity = Regex("""(\d+)\s*birds""").find(message)?.groupValues?.get(1)?.toIntOrNull() ?: 0
            val avgWeight = Regex("""(\d+)g\s*avg""").find(message)?.groupValues?.get(1)?.toIntOrNull() ?: 0
            val ageWeeks = Regex("""(\d+)\s*weeks""").find(message)?.groupValues?.get(1)?.toIntOrNull() ?: 0
            HarvestMetadata(batchId, quantity, avgWeight, ageWeeks)
        } catch (e: Exception) {
            HarvestMetadata("", 0, 0, 0)
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF4CAF50),
                            Color(0xFF81C784)
                        )
                    )
                )
                .padding(16.dp)
        ) {
            Column {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Agriculture,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Ready to Sell!",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    
                    TextButton(
                        onClick = { onDismiss(alert.alertId) },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = Color.White.copy(alpha = 0.7f)
                        )
                    ) {
                        Text("Later", style = MaterialTheme.typography.labelMedium)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Batch details
                Text(
                    text = alert.message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.9f)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Stats row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatChip(label = "Birds", value = "${metadata.quantity}")
                    StatChip(label = "Weight", value = "${metadata.avgWeight}g")
                    StatChip(label = "Age", value = "${metadata.ageWeeks} wks")
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Action button
                Button(
                    onClick = { onSellNow(metadata.batchId) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color(0xFF4CAF50)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AddShoppingCart,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Create Listing",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun StatChip(label: String, value: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White.copy(alpha = 0.2f))
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = Color.White.copy(alpha = 0.8f)
        )
    }
}

private data class HarvestMetadata(
    val batchId: String,
    val quantity: Int,
    val avgWeight: Int,
    val ageWeeks: Int
)
