package com.rio.rostry.ui.order.evidence

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.rio.rostry.data.database.entity.*
import com.rio.rostry.domain.model.EvidenceOrderStatus
import com.rio.rostry.domain.model.EvidenceType
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

/**
 * Order Tracking Screen - Shows order progress with timeline and evidence.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderTrackingScreen(
    orderId: String,
    onNavigateBack: () -> Unit,
    onPaymentProof: (String) -> Unit,
    onDeliveryOtp: () -> Unit,
    onRaiseDispute: () -> Unit,
    viewModel: EvidenceOrderViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val orderEvidence by viewModel.orderEvidence.collectAsState()
    val auditTrail by viewModel.auditTrail.collectAsState()
    val currentUserId by viewModel.currentUserId.collectAsState()

    val quote = uiState.currentQuote

    LaunchedEffect(orderId) {
        viewModel.loadOrderDetails(orderId)
    }

    val currencyFormat = remember { NumberFormat.getCurrencyInstance(Locale("en", "IN")) }
    val dateFormat = remember { SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Order Tracking", fontWeight = FontWeight.Bold)
                        Text(
                            text = orderId.take(8) + "...",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onRaiseDispute) {
                        Icon(
                            imageVector = Icons.Default.ReportProblem,
                            contentDescription = "Raise Dispute",
                            tint = Color(0xFFDC2626)
                        )
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
            // Current Status Banner
            item {
                quote?.let { q ->
                    CurrentStatusBanner(
                        status = EvidenceOrderStatus.fromString(q.status),
                        onActionClick = {
                            when (q.status) {
                                "ADVANCE_PENDING" -> onPaymentProof(orderId)
                                "DISPATCHED", "READY_FOR_PICKUP" -> onDeliveryOtp()
                                else -> {}
                            }
                        }
                    )
                }
            }

            // Order Summary Card
            item {
                quote?.let { q ->
                    OrderSummaryCard(
                        productName = q.productName,
                        quantity = q.quantity,
                        unit = q.unit,
                        total = q.finalTotal,
                        sellerName = "Seller" // Would come from user lookup
                    )
                }
            }

            // Progress Timeline
            item {
                Text(
                    text = "Order Timeline",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                quote?.let { q ->
                    OrderTimeline(
                        currentStatus = EvidenceOrderStatus.fromString(q.status),
                        auditTrail = auditTrail
                    )
                }
            }

            // Evidence Gallery
            if (orderEvidence.isNotEmpty()) {
                item {
                    Text(
                        text = "Evidence & Proofs",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                item {
                    EvidenceGallery(evidences = orderEvidence)
                }
            }

            // Action Buttons
            item {
                quote?.let { q ->
                    ActionSection(
                        status = EvidenceOrderStatus.fromString(q.status),
                        isBuyer = q.buyerId == currentUserId,
                        onPaymentProof = { onPaymentProof(orderId) },
                        onDeliveryOtp = onDeliveryOtp,
                        onRaiseDispute = onRaiseDispute
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(32.dp)) }
        }
    }
}

@Composable
private fun CurrentStatusBanner(
    status: EvidenceOrderStatus,
    onActionClick: () -> Unit
) {
    val (backgroundColor, textColor, icon, actionText) = when (status) {
        EvidenceOrderStatus.ENQUIRY -> listOf(Color(0xFFFEF3C7), Color(0xFF92400E), Icons.Default.Pending, null)
        EvidenceOrderStatus.QUOTE_SENT -> listOf(Color(0xFFDCFCE7), Color(0xFF065F46), Icons.Default.Send, null)
        EvidenceOrderStatus.AGREEMENT_LOCKED -> listOf(Color(0xFFDCFCE7), Color(0xFF065F46), Icons.Default.Lock, null)
        EvidenceOrderStatus.ADVANCE_PENDING -> listOf(Color(0xFFFEE2E2), Color(0xFFB91C1C), Icons.Default.Payment, "Upload Payment Proof")
        EvidenceOrderStatus.PAYMENT_PROOF_SUBMITTED -> listOf(Color(0xFFFEF3C7), Color(0xFF92400E), Icons.Default.HourglassTop, null)
        EvidenceOrderStatus.PAYMENT_VERIFIED -> listOf(Color(0xFFDCFCE7), Color(0xFF065F46), Icons.Default.CheckCircle, null)
        EvidenceOrderStatus.PREPARING -> listOf(Color(0xFFE0E7FF), Color(0xFF3730A3), Icons.Default.Inventory, null)
        EvidenceOrderStatus.DISPATCHED -> listOf(Color(0xFFE0E7FF), Color(0xFF3730A3), Icons.Default.LocalShipping, "Confirm Delivery")
        EvidenceOrderStatus.READY_FOR_PICKUP -> listOf(Color(0xFFE0E7FF), Color(0xFF3730A3), Icons.Default.Store, "Confirm Pickup")
        EvidenceOrderStatus.DELIVERED -> listOf(Color(0xFFDCFCE7), Color(0xFF065F46), Icons.Default.Verified, null)
        EvidenceOrderStatus.COMPLETED -> listOf(Color(0xFF10B981), Color.White, Icons.Default.CheckCircle, null)
        EvidenceOrderStatus.DISPUTE -> listOf(Color(0xFFFEE2E2), Color(0xFFB91C1C), Icons.Default.Warning, null)
        EvidenceOrderStatus.ESCALATED -> listOf(Color(0xFFFEE2E2), Color(0xFFB91C1C), Icons.Default.Gavel, null)
        EvidenceOrderStatus.CANCELLED -> listOf(Color(0xFFF3F4F6), Color(0xFF6B7280), Icons.Default.Cancel, null)
        EvidenceOrderStatus.EXPIRED -> listOf(Color(0xFFF3F4F6), Color(0xFF6B7280), Icons.Default.Timer, null)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor as Color)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon as ImageVector,
                    contentDescription = null,
                    tint = textColor as Color,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = status.displayName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )
                    Text(
                        text = status.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = textColor.copy(alpha = 0.8f)
                    )
                }
            }

            (actionText as? String)?.let { action ->
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = onActionClick,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (status == EvidenceOrderStatus.ADVANCE_PENDING) 
                            Color(0xFFDC2626) else Color(0xFF6366F1)
                    )
                ) {
                    Text(action, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun OrderSummaryCard(
    productName: String,
    quantity: Double,
    unit: String,
    total: Double,
    sellerName: String
) {
    val currencyFormat = remember { NumberFormat.getCurrencyInstance(Locale("en", "IN")) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(Color(0xFF6366F1), Color(0xFF8B5CF6))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Storefront,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = productName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${quantity.toInt()} $unit • $sellerName",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = currencyFormat.format(total),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF10B981)
                )
            }
        }
    }
}

@Composable
private fun OrderTimeline(
    currentStatus: EvidenceOrderStatus,
    auditTrail: List<OrderAuditLogEntity>
) {
    val dateFormat = remember { SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()) }
    
    val timelineSteps = listOf(
        EvidenceOrderStatus.ENQUIRY,
        EvidenceOrderStatus.QUOTE_SENT,
        EvidenceOrderStatus.AGREEMENT_LOCKED,
        EvidenceOrderStatus.ADVANCE_PENDING,
        EvidenceOrderStatus.PAYMENT_VERIFIED,
        EvidenceOrderStatus.DISPATCHED,
        EvidenceOrderStatus.DELIVERED,
        EvidenceOrderStatus.COMPLETED
    )

    val currentIndex = timelineSteps.indexOf(currentStatus).coerceAtLeast(0)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            timelineSteps.forEachIndexed { index, status ->
                val isCompleted = index < currentIndex
                val isCurrent = index == currentIndex
                val isPending = index > currentIndex

                // Find audit log for this status
                val auditLog = auditTrail.find { it.toState == status.value }

                TimelineStep(
                    status = status,
                    isCompleted = isCompleted,
                    isCurrent = isCurrent,
                    isPending = isPending,
                    timestamp = auditLog?.timestamp?.let { dateFormat.format(Date(it)) },
                    isLast = index == timelineSteps.lastIndex
                )
            }
        }
    }
}

@Composable
private fun TimelineStep(
    status: EvidenceOrderStatus,
    isCompleted: Boolean,
    isCurrent: Boolean,
    isPending: Boolean,
    timestamp: String?,
    isLast: Boolean
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        // Timeline indicator
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(40.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(
                        when {
                            isCompleted -> Color(0xFF10B981)
                            isCurrent -> Color(0xFF6366F1)
                            else -> Color(0xFFE5E7EB)
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                when {
                    isCompleted -> Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                    isCurrent -> CircularProgressIndicator(
                        modifier = Modifier.size(14.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                }
            }

            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(40.dp)
                        .background(
                            if (isCompleted) Color(0xFF10B981) else Color(0xFFE5E7EB)
                        )
                )
            }
        }

        // Step content
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 12.dp, bottom = if (isLast) 0.dp else 16.dp)
        ) {
            Text(
                text = status.displayName,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium,
                color = when {
                    isCurrent -> Color(0xFF6366F1)
                    isPending -> Color(0xFF9CA3AF)
                    else -> MaterialTheme.colorScheme.onSurface
                }
            )
            timestamp?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF9CA3AF)
                )
            }
        }
    }
}

@Composable
private fun EvidenceGallery(evidences: List<OrderEvidenceEntity>) {
    val groupedEvidence = evidences.groupBy { EvidenceType.fromString(it.evidenceType) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            groupedEvidence.forEach { (type, items) ->
                Text(
                    text = type.displayName,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(items) { evidence ->
                        EvidenceItem(evidence = evidence)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun EvidenceItem(evidence: OrderEvidenceEntity) {
    val dateFormat = remember { SimpleDateFormat("dd MMM, hh:mm", Locale.getDefault()) }

    Card(
        modifier = Modifier
            .width(120.dp)
            .height(160.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .background(Color(0xFFF3F4F6)),
                contentAlignment = Alignment.Center
            ) {
                if (evidence.imageUri != null) {
                    AsyncImage(
                        model = evidence.imageUri,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Description,
                        contentDescription = null,
                        tint = Color(0xFF9CA3AF),
                        modifier = Modifier.size(32.dp)
                    )
                }

                // Verified badge
                if (evidence.isVerified) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(4.dp)
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF10B981)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }
            }

            Column(
                modifier = Modifier.padding(8.dp)
            ) {
                Text(
                    text = dateFormat.format(Date(evidence.createdAt)),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF6B7280)
                )
                Text(
                    text = if (evidence.isVerified) "Verified ✓" else "Pending",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (evidence.isVerified) Color(0xFF10B981) else Color(0xFFF59E0B),
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun ActionSection(
    status: EvidenceOrderStatus,
    isBuyer: Boolean,
    onPaymentProof: () -> Unit,
    onDeliveryOtp: () -> Unit,
    onRaiseDispute: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        when {
            status == EvidenceOrderStatus.ADVANCE_PENDING && isBuyer -> {
                Button(
                    onClick = onPaymentProof,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6366F1))
                ) {
                    Icon(Icons.Default.Upload, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Upload Payment Proof", fontWeight = FontWeight.Bold)
                }
            }
            status == EvidenceOrderStatus.DISPATCHED || status == EvidenceOrderStatus.READY_FOR_PICKUP -> {
                Button(
                    onClick = onDeliveryOtp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                ) {
                    Icon(Icons.Default.QrCode, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        if (isBuyer) "Show Delivery OTP" else "Enter Delivery OTP",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Raise dispute is always available except for completed/cancelled
        if (status !in listOf(
            EvidenceOrderStatus.COMPLETED,
            EvidenceOrderStatus.CANCELLED,
            EvidenceOrderStatus.EXPIRED,
            EvidenceOrderStatus.ENQUIRY
        )) {
            OutlinedButton(
                onClick = onRaiseDispute,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color(0xFFDC2626)
                )
            ) {
                Icon(Icons.Default.ReportProblem, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Raise a Dispute")
            }
        }
    }
}
