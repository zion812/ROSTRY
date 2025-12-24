package com.rio.rostry.ui.order.evidence

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.rio.rostry.data.database.entity.OrderDisputeEntity
import com.rio.rostry.domain.model.DisputeReason
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

/**
 * Raise Dispute Screen - Allows users to raise a dispute for an order.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RaiseDisputeScreen(
    orderId: String,
    onNavigateBack: () -> Unit,
    onDisputeRaised: () -> Unit,
    viewModel: EvidenceOrderViewModel = hiltViewModel()
) {
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val successMessage by viewModel.successMessage.collectAsState()

    var selectedReason by remember { mutableStateOf<DisputeReason?>(null) }
    var description by remember { mutableStateOf("") }
    var requestedResolution by remember { mutableStateOf("") }
    var claimedAmount by remember { mutableStateOf("") }
    var evidenceUris by remember { mutableStateOf<List<Uri>>(emptyList()) }

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri> ->
        evidenceUris = evidenceUris + uris
    }

    LaunchedEffect(successMessage) {
        if (successMessage?.contains("Dispute raised") == true) {
            kotlinx.coroutines.delay(1500)
            onDisputeRaised()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Raise a Dispute", fontWeight = FontWeight.Bold)
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Warning Banner
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF3C7))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = Color(0xFFD97706),
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Before you proceed",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF92400E)
                            )
                            Text(
                                text = "Try to resolve the issue with the other party first. Disputes may take 3-5 days to resolve.",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFFB45309)
                            )
                        }
                    }
                }
            }

            // Reason Selection
            item {
                Text(
                    text = "What's the issue?",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                DisputeReasonSelector(
                    selected = selectedReason,
                    onSelect = { selectedReason = it }
                )
            }

            // Description
            item {
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Describe the issue in detail") },
                    placeholder = { Text("Explain what happened and when...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    shape = RoundedCornerShape(12.dp),
                    maxLines = 6
                )
            }

            // Requested Resolution
            item {
                OutlinedTextField(
                    value = requestedResolution,
                    onValueChange = { requestedResolution = it },
                    label = { Text("What resolution do you want?") },
                    placeholder = { Text("e.g., Full refund, replacement, partial refund...") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    maxLines = 2
                )
            }

            // Claimed Amount (if monetary dispute)
            if (selectedReason in listOf(
                DisputeReason.PAYMENT_NOT_RECEIVED,
                DisputeReason.REFUND_NOT_RECEIVED,
                DisputeReason.PRICE_DISPUTE
            )) {
                item {
                    OutlinedTextField(
                        value = claimedAmount,
                        onValueChange = { claimedAmount = it },
                        label = { Text("Amount in dispute") },
                        leadingIcon = { Text("₹") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            // Evidence Upload
            item {
                Text(
                    text = "Add evidence (photos/screenshots)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                EvidenceUploadSection(
                    evidenceUris = evidenceUris,
                    onAddEvidence = { imagePicker.launch("image/*") },
                    onRemoveEvidence = { uri ->
                        evidenceUris = evidenceUris.filter { it != uri }
                    }
                )
            }

            // Submit Button
            item {
                Button(
                    onClick = {
                        selectedReason?.let { reason ->
                            viewModel.raiseDispute(
                                orderId = orderId,
                                reason = reason,
                                description = description,
                                requestedResolution = requestedResolution.ifBlank { null },
                                claimedAmount = claimedAmount.toDoubleOrNull()
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    enabled = selectedReason != null && description.length >= 20 && !isLoading,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFDC2626)
                    )
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White
                        )
                    } else {
                        Icon(Icons.Default.Gavel, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Submit Dispute", fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Error Message
            error?.let {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFEE2E2)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Error, null, tint = Color(0xFFDC2626))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(it, color = Color(0xFFDC2626))
                        }
                    }
                }
            }

            // Success Message
            successMessage?.let {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFDCFCE7)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.CheckCircle, null, tint = Color(0xFF059669))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(it, color = Color(0xFF059669))
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(32.dp)) }
        }
    }
}

@Composable
private fun DisputeReasonSelector(
    selected: DisputeReason?,
    onSelect: (DisputeReason) -> Unit
) {
    val reasons = listOf(
        DisputeReason.WRONG_PRODUCT to Icons.Default.SwapHoriz,
        DisputeReason.QUALITY_ISSUE to Icons.Default.ThumbDown,
        DisputeReason.QUANTITY_MISMATCH to Icons.Default.Numbers,
        DisputeReason.DELIVERY_ISSUE to Icons.Default.LocalShipping,
        DisputeReason.PAYMENT_NOT_RECEIVED to Icons.Default.MoneyOff,
        DisputeReason.SELLER_UNRESPONSIVE to Icons.Default.PhoneMissed,
        DisputeReason.BUYER_UNRESPONSIVE to Icons.Default.PhoneMissed,
        DisputeReason.OTHER to Icons.Default.MoreHoriz
    )

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        reasons.chunked(2).forEach { rowReasons ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rowReasons.forEach { (reason, icon) ->
                    val isSelected = selected == reason

                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onSelect(reason) },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) Color(0xFFFEE2E2) else Color(0xFFF9FAFB)
                        ),
                        border = if (isSelected) {
                            androidx.compose.foundation.BorderStroke(2.dp, Color(0xFFDC2626))
                        } else {
                            androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE5E7EB))
                        }
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = if (isSelected) Color(0xFFDC2626) else Color(0xFF6B7280),
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = reason.displayName,
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isSelected) Color(0xFFB91C1C) else Color(0xFF374151),
                                textAlign = TextAlign.Center,
                                maxLines = 2
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EvidenceUploadSection(
    evidenceUris: List<Uri>,
    onAddEvidence: () -> Unit,
    onRemoveEvidence: (Uri) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Add button
        item {
            Card(
                modifier = Modifier
                    .size(100.dp)
                    .clickable(onClick = onAddEvidence),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF3F4F6)),
                border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFFE5E7EB))
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AddPhotoAlternate,
                        contentDescription = "Add photo",
                        tint = Color(0xFF6B7280),
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Add Photo",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF6B7280)
                    )
                }
            }
        }

        // Uploaded images
        items(evidenceUris) { uri ->
            Box(modifier = Modifier.size(100.dp)) {
                AsyncImage(
                    model = uri,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(12.dp))
                )

                // Remove button
                IconButton(
                    onClick = { onRemoveEvidence(uri) },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                        .size(24.dp)
                        .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Remove",
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}

/**
 * My Disputes Screen - Shows list of user's disputes.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyDisputesScreen(
    onNavigateBack: () -> Unit,
    onDisputeClick: (String) -> Unit,
    viewModel: EvidenceOrderViewModel = hiltViewModel()
) {
    val disputes by viewModel.disputes.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("My Disputes", fontWeight = FontWeight.Bold)
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        if (disputes.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = Color(0xFF10B981)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No disputes",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF10B981)
                    )
                    Text(
                        text = "All your transactions are going smoothly!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF6B7280)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(disputes, key = { it.disputeId }) { dispute ->
                    DisputeCard(
                        dispute = dispute,
                        onClick = { onDisputeClick(dispute.disputeId) }
                    )
                }
            }
        }
    }
}

@Composable
private fun DisputeCard(
    dispute: OrderDisputeEntity,
    onClick: () -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("dd MMM, yyyy", Locale.getDefault()) }
    val currencyFormat = remember { NumberFormat.getCurrencyInstance(Locale("en", "IN")) }

    val reason = DisputeReason.fromString(dispute.reason)

    val statusColor = when (dispute.status) {
        "OPEN" -> Color(0xFFF59E0B)
        "UNDER_REVIEW" -> Color(0xFF6366F1)
        "ESCALATED" -> Color(0xFFDC2626)
        "RESOLVED" -> Color(0xFF10B981)
        else -> Color(0xFF6B7280)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(statusColor.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Gavel,
                            contentDescription = null,
                            tint = statusColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = reason.displayName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = dateFormat.format(Date(dispute.createdAt)),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = statusColor.copy(alpha = 0.1f)
                ) {
                    Text(
                        text = dispute.status.replace("_", " "),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelMedium,
                        color = statusColor,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = dispute.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2
            )

            dispute.claimedAmount?.let { amount ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Claimed: ${currencyFormat.format(amount)}",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color(0xFFDC2626),
                    fontWeight = FontWeight.Medium
                )
            }

            if (dispute.status == "RESOLVED") {
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFDCFCE7)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color(0xFF059669),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = dispute.resolutionType?.replace("_", " ") ?: "Resolved",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF059669)
                        )
                    }
                }
            }
        }
    }
}
