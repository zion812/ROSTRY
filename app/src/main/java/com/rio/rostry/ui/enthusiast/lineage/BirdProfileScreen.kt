package com.rio.rostry.ui.enthusiast.lineage

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rio.rostry.data.database.entity.BirdTraitRecordEntity
import com.rio.rostry.data.database.entity.MedicalEventEntity
import com.rio.rostry.data.database.entity.ProductEntity
import com.rio.rostry.domain.service.BreedingValueResult
import java.text.SimpleDateFormat
import java.util.*

// Reuse Enthusiast premium colors
private val PurplePrimary = Color(0xFF673AB7)
private val PurpleDark = Color(0xFF512DA8)
private val CyanAccent = Color(0xFF00E5FF)
private val GoldAccent = Color(0xFFFFD700)
private val SurfaceDark = Color(0xFF1A1228)
private val CardDark = Color(0xFF261D35)
private val SuccessGreen = Color(0xFF4CAF50)
private val WarningOrange = Color(0xFFFF9800)
private val ErrorRed = Color(0xFFEF5350)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BirdProfileScreen(
    productId: String,
    onNavigateBack: () -> Unit,
    onNavigateToTraitRecording: (String) -> Unit,
    onNavigateToHealthLog: (String) -> Unit,
    onNavigateToPedigree: (String) -> Unit,
    onNavigateToLineageExplorer: (String) -> Unit = {},
    onNavigateToMateFinder: (String) -> Unit = {},
    viewModel: BirdProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        uiState.bird?.name ?: "Bird Profile",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
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
        } else if (uiState.bird == null) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("Bird not found", color = Color.White.copy(alpha = 0.5f))
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Hero Card
                item {
                    BirdHeroCard(
                        bird = uiState.bird!!,
                        traitCompleteness = uiState.traitCompleteness
                    )
                }

                // Quick Actions
                item {
                    QuickActionsRow(
                        productId = productId,
                        onTraitRecording = onNavigateToTraitRecording,
                        onHealthLog = onNavigateToHealthLog,
                        onPedigree = onNavigateToPedigree,
                        onLineageExplorer = onNavigateToLineageExplorer,
                        onMateFinder = onNavigateToMateFinder
                    )
                }

                // BVI Card
                uiState.bvi?.let { bvi ->
                    item {
                        BviCard(bvi = bvi)
                    }
                }

                // Lineage Card
                item {
                    LineageCard(
                        sire = uiState.sire,
                        dam = uiState.dam,
                        offspringCount = uiState.offspring.size,
                        onPedigreeClick = { onNavigateToPedigree(productId) }
                    )
                }

                // Trait Summary
                if (uiState.traitRecords.isNotEmpty()) {
                    item {
                        TraitSummaryCard(
                            records = uiState.traitRecords,
                            onViewAll = { onNavigateToTraitRecording(productId) }
                        )
                    }
                }

                // Health Summary
                item {
                    HealthSummaryCard(
                        events = uiState.healthEvents,
                        onViewAll = { onNavigateToHealthLog(productId) }
                    )
                }

                // Offspring
                if (uiState.offspring.isNotEmpty()) {
                    item {
                        OffspringCard(offspring = uiState.offspring)
                    }
                }

                item { Spacer(Modifier.height(32.dp)) }
            }
        }
    }
}

@Composable
private fun BirdHeroCard(
    bird: ProductEntity,
    traitCompleteness: Float
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        listOf(PurpleDark, PurplePrimary, PurpleDark)
                    )
                )
                .padding(20.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Avatar
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(CyanAccent.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Filled.Pets,
                            contentDescription = null,
                            tint = CyanAccent,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Spacer(Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            bird.name,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp
                        )
                        bird.breed?.let { breed ->
                            Text(
                                breed,
                                color = GoldAccent,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            bird.gender?.let { gender ->
                                Text(
                                    if (gender.equals("Male", true)) "♂" else "♀",
                                    color = if (gender.equals("Male", true)) CyanAccent else Color(0xFFFF69B4),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(Modifier.width(8.dp))
                            }
                            bird.ageWeeks?.let { age ->
                                Text("${age}w old", color = Color.White.copy(alpha = 0.7f), fontSize = 13.sp)
                            }
                            bird.weightGrams?.let { weight ->
                                Text(" • ${weight.toInt()}g", color = Color.White.copy(alpha = 0.7f), fontSize = 13.sp)
                            }
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Trait completeness bar
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "Data Completeness",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 12.sp
                        )
                        Text(
                            "${(traitCompleteness * 100).toInt()}%",
                            color = when {
                                traitCompleteness >= 0.75f -> SuccessGreen
                                traitCompleteness >= 0.4f -> WarningOrange
                                else -> ErrorRed
                            },
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(Modifier.height(4.dp))
                    LinearProgressIndicator(
                        progress = { traitCompleteness },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = when {
                            traitCompleteness >= 0.75f -> SuccessGreen
                            traitCompleteness >= 0.4f -> WarningOrange
                            else -> ErrorRed
                        },
                        trackColor = Color.White.copy(alpha = 0.15f)
                    )
                }
            }
        }
    }
}

@Composable
private fun QuickActionsRow(
    productId: String,
    onTraitRecording: (String) -> Unit,
    onHealthLog: (String) -> Unit,
    onPedigree: (String) -> Unit,
    onLineageExplorer: (String) -> Unit,
    onMateFinder: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        QuickActionButton(
            icon = Icons.Filled.Science,
            label = "Record\nTraits",
            color = CyanAccent,
            modifier = Modifier.weight(1f),
            onClick = { onTraitRecording(productId) }
        )
        QuickActionButton(
            icon = Icons.Filled.MedicalServices,
            label = "Health\nLog",
            color = SuccessGreen,
            modifier = Modifier.weight(1f),
            onClick = { onHealthLog(productId) }
        )
        QuickActionButton(
            icon = Icons.Filled.AccountTree,
            label = "Pedigree\nTree",
            color = GoldAccent,
            modifier = Modifier.weight(1f),
            onClick = { onPedigree(productId) }
        )
        QuickActionButton(
            icon = Icons.Filled.Explore,
            label = "Lineage\nExplorer",
            color = PurplePrimary,
            modifier = Modifier.weight(1f),
            onClick = { onLineageExplorer(productId) }
        )
    }
    Spacer(modifier = Modifier.height(8.dp))
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        QuickActionButton(
            icon = Icons.Filled.Favorite,
            label = "Find\nBest Mate",
            color = Color(0xFFE91E63),
            modifier = Modifier.weight(1f),
            onClick = { onMateFinder(productId) }
        )
        Spacer(modifier = Modifier.weight(3f))
    }
}

@Composable
private fun QuickActionButton(
    icon: ImageVector,
    label: String,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardDark)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(22.dp))
            }
            Spacer(Modifier.height(8.dp))
            Text(
                label,
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                lineHeight = 16.sp,
                maxLines = 2,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

@Composable
private fun LineageCard(
    sire: ProductEntity?,
    dam: ProductEntity?,
    offspringCount: Int,
    onPedigreeClick: () -> Unit
) {
    SectionCard(
        title = "Lineage",
        icon = Icons.Filled.FamilyRestroom,
        action = {
            TextButton(onClick = onPedigreeClick) {
                Text("Full Tree →", color = CyanAccent, fontSize = 12.sp)
            }
        }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Sire
            ParentChip(
                label = "Sire (♂)",
                name = sire?.name ?: "Unknown",
                breed = sire?.breed,
                color = CyanAccent,
                modifier = Modifier.weight(1f)
            )
            // Dam
            ParentChip(
                label = "Dam (♀)",
                name = dam?.name ?: "Unknown",
                breed = dam?.breed,
                color = Color(0xFFFF69B4),
                modifier = Modifier.weight(1f)
            )
        }
        
        if (offspringCount > 0) {
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Filled.ChildCare,
                    contentDescription = null,
                    tint = GoldAccent,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    "$offspringCount offspring",
                    color = GoldAccent,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun ParentChip(
    label: String,
    name: String,
    breed: String?,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(color.copy(alpha = 0.1f))
            .padding(12.dp)
    ) {
        Column {
            Text(label, color = color, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Text(
                name,
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            breed?.let {
                Text(it, color = Color.White.copy(alpha = 0.5f), fontSize = 11.sp, maxLines = 1)
            }
        }
    }
}

@Composable
private fun TraitSummaryCard(
    records: List<BirdTraitRecordEntity>,
    onViewAll: () -> Unit
) {
    val categories = records.groupBy { it.traitCategory }
    
    SectionCard(
        title = "Traits",
        icon = Icons.Filled.Science,
        action = {
            TextButton(onClick = onViewAll) {
                Text("View All →", color = CyanAccent, fontSize = 12.sp)
            }
        }
    ) {
        // Show latest value per trait (max 6)
        val latestPerTrait = records
            .groupBy { it.traitName }
            .mapValues { (_, recs) -> recs.maxByOrNull { it.recordedAt }!! }
            .values
            .take(6)

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            latestPerTrait.forEach { record ->
                val displayName = record.traitName.replace("_", " ").replaceFirstChar { it.uppercase() }
                val displayValue = buildString {
                    append(record.traitValue)
                    record.traitUnit?.let { u ->
                        if (u == "score_1_10") append("/10") else append(" $u")
                    }
                }
                
                SuggestionChip(
                    onClick = onViewAll,
                    label = {
                        Text(
                            "$displayName: $displayValue",
                            fontSize = 11.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    colors = SuggestionChipDefaults.suggestionChipColors(
                        containerColor = PurplePrimary.copy(alpha = 0.3f),
                        labelColor = Color.White
                    ),
                    border = SuggestionChipDefaults.suggestionChipBorder(
                        enabled = true,
                        borderColor = PurplePrimary.copy(alpha = 0.5f)
                    )
                )
            }
        }

        Spacer(Modifier.height(8.dp))
        Text(
            "${categories.size} categories • ${records.map { it.traitName }.distinct().size} traits recorded",
            color = Color.White.copy(alpha = 0.5f),
            fontSize = 11.sp
        )
    }
}

@Composable
private fun HealthSummaryCard(
    events: List<MedicalEventEntity>,
    onViewAll: () -> Unit
) {
    val activeIssues = events.count { it.outcome.isNullOrBlank() || it.outcome == "ONGOING" }
    val totalEvents = events.size

    SectionCard(
        title = "Health",
        icon = Icons.Filled.MedicalServices,
        action = {
            TextButton(onClick = onViewAll) {
                Text("View All →", color = CyanAccent, fontSize = 12.sp)
            }
        }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            HealthStat(
                label = "Total Events",
                value = totalEvents.toString(),
                color = Color.White
            )
            HealthStat(
                label = "Active Issues",
                value = activeIssues.toString(),
                color = if (activeIssues > 0) WarningOrange else SuccessGreen
            )
            HealthStat(
                label = "Status",
                value = if (activeIssues == 0) "Healthy" else "Attention",
                color = if (activeIssues == 0) SuccessGreen else WarningOrange
            )
        }

        if (events.isEmpty()) {
            Spacer(Modifier.height(8.dp))
            Text(
                "No health events recorded yet",
                color = Color.White.copy(alpha = 0.4f),
                fontSize = 12.sp
            )
        }
    }
}

@Composable
private fun HealthStat(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, color = color, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Text(label, color = Color.White.copy(alpha = 0.5f), fontSize = 11.sp)
    }
}

@Composable
private fun OffspringCard(offspring: List<ProductEntity>) {
    SectionCard(
        title = "Offspring (${offspring.size})",
        icon = Icons.Filled.ChildCare
    ) {
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(offspring) { child ->
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = PurplePrimary.copy(alpha = 0.2f))
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp).width(100.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(CyanAccent.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Filled.Pets,
                                contentDescription = null,
                                tint = CyanAccent,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(Modifier.height(6.dp))
                        Text(
                            child.name,
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        child.gender?.let { gender ->
                            Text(
                                if (gender.equals("Male", true)) "♂" else "♀",
                                color = if (gender.equals("Male", true)) CyanAccent else Color(0xFFFF69B4),
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionCard(
    title: String,
    icon: ImageVector,
    action: @Composable (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardDark)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(icon, contentDescription = null, tint = CyanAccent, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Text(
                    title,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    modifier = Modifier.weight(1f)
                )
                action?.invoke()
            }
            Spacer(Modifier.height(12.dp))
            content()
        }
    }
}

@Composable
private fun BviCard(bvi: BreedingValueResult) {
    val bviColor = when (bvi.rating) {
        "Elite" -> GoldAccent
        "Strong" -> SuccessGreen
        "Average" -> WarningOrange
        "Developing" -> CyanAccent
        else -> Color.White.copy(alpha = 0.5f)
    }

    SectionCard(
        title = "Breeding Value",
        icon = Icons.Filled.TrendingUp
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // BVI Score circle
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(bviColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "${(bvi.bvi * 100).toInt()}",
                        color = bviColor,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 20.sp
                    )
                    Text("BVI", color = bviColor.copy(alpha = 0.8f), fontSize = 9.sp)
                }
            }

            Spacer(Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                // Rating badge
                Text(
                    bvi.rating,
                    color = bviColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )

                Spacer(Modifier.height(4.dp))

                // Sub-scores
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    if (bvi.traitCount > 0) {
                        Text("${bvi.traitCount} traits", color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp)
                    }
                    if (bvi.showTotal > 0) {
                        Text("${bvi.showWins}/${bvi.showTotal} shows", color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp)
                    }
                    if (bvi.offspringCount > 0) {
                        Text("${bvi.offspringCount} offspring", color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp)
                    }
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        // Recommendation
        Text(
            bvi.recommendation,
            color = Color.White.copy(alpha = 0.7f),
            fontSize = 12.sp,
            lineHeight = 16.sp
        )
    }
}
