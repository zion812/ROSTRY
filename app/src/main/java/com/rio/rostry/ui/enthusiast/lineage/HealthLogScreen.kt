package com.rio.rostry.ui.enthusiast.lineage

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rio.rostry.data.database.entity.MedicalEventEntity
import java.text.SimpleDateFormat
import java.util.*

// Reuse premium colors
private val CyanAccent = Color(0xFF00E5FF)
private val GoldAccent = Color(0xFFFFD700)
private val SurfaceDark = Color(0xFF1A1228)
private val CardDark = Color(0xFF261D35)
private val SuccessGreen = Color(0xFF4CAF50)
private val WarningOrange = Color(0xFFFF9800)
private val ErrorRed = Color(0xFFEF5350)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthLogScreen(
    productId: String,
    onNavigateBack: () -> Unit,
    viewModel: HealthLogViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Health Log", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        uiState.bird?.let { bird ->
                            Text(
                                bird.name,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SurfaceDark,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        containerColor = SurfaceDark
    ) { padding ->
        if (uiState.isLoading) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = CyanAccent)
            }
        } else if (uiState.events.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Filled.HealthAndSafety,
                        contentDescription = null,
                        tint = SuccessGreen.copy(alpha = 0.5f),
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(Modifier.height(16.dp))
                    Text("No Health Events", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text(
                        "This bird has a clean health record",
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 14.sp
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
                // Summary header
                item {
                    HealthSummaryHeader(events = uiState.events)
                }

                items(uiState.events) { event ->
                    HealthEventCard(event = event, dateFormat = dateFormat)
                }

                item { Spacer(Modifier.height(32.dp)) }
            }
        }
    }
}

@Composable
private fun HealthSummaryHeader(events: List<MedicalEventEntity>) {
    val activeCount = events.count { it.outcome.isNullOrBlank() || it.outcome == "ONGOING" }
    val resolvedCount = events.size - activeCount

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardDark)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatColumn("Total", events.size.toString(), Color.White)
            StatColumn("Active", activeCount.toString(), if (activeCount > 0) WarningOrange else SuccessGreen)
            StatColumn("Resolved", resolvedCount.toString(), SuccessGreen)
        }
    }
}

@Composable
private fun StatColumn(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, color = color, fontWeight = FontWeight.Bold, fontSize = 22.sp)
        Text(label, color = Color.White.copy(alpha = 0.5f), fontSize = 12.sp)
    }
}

@Composable
private fun HealthEventCard(
    event: MedicalEventEntity,
    dateFormat: SimpleDateFormat
) {
    val severityColor = when (event.severity?.uppercase()) {
        "CRITICAL" -> ErrorRed
        "HIGH" -> WarningOrange
        "MEDIUM" -> GoldAccent
        else -> Color.White.copy(alpha = 0.5f)
    }

    val isActive = event.outcome.isNullOrBlank() || event.outcome == "ONGOING"

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardDark)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Severity indicator
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(severityColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    when (event.eventType?.uppercase()) {
                        "VACCINATION" -> Icons.Filled.Vaccines
                        "INJURY" -> Icons.Filled.PersonalInjury
                        "ILLNESS" -> Icons.Filled.Sick
                        else -> Icons.Filled.MedicalServices
                    },
                    contentDescription = null,
                    tint = severityColor,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        event.eventType?.replaceFirstChar { it.uppercase() } ?: "Event",
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        modifier = Modifier.weight(1f)
                    )
                    // Status badge
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (isActive) WarningOrange.copy(alpha = 0.2f) else SuccessGreen.copy(alpha = 0.2f)
                    ) {
                        Text(
                            if (isActive) "Active" else "Resolved",
                            color = if (isActive) WarningOrange else SuccessGreen,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }

                event.diagnosis?.let { diagnosis ->
                    Text(
                        diagnosis,
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 13.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                event.symptoms?.let { symptoms ->
                    Text(
                        "Symptoms: $symptoms",
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 11.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(Modifier.height(4.dp))
                Text(
                    dateFormat.format(Date(event.eventDate)),
                    color = Color.White.copy(alpha = 0.4f),
                    fontSize = 11.sp
                )
            }
        }
    }
}
