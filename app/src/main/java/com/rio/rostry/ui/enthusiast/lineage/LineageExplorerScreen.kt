package com.rio.rostry.ui.enthusiast.lineage

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rio.rostry.domain.pedigree.PedigreeTree
import com.rio.rostry.domain.service.BreedingValueResult

// Premium Enthusiast colors
private val PurplePrimary = Color(0xFF673AB7)
private val PurpleDark = Color(0xFF512DA8)
private val CyanAccent = Color(0xFF00E5FF)
private val GoldAccent = Color(0xFFFFD700)
private val SurfaceDark = Color(0xFF1A1228)
private val CardDark = Color(0xFF261D35)
private val SuccessGreen = Color(0xFF4CAF50)
private val WarningOrange = Color(0xFFFF9800)
private val ErrorRed = Color(0xFFEF5350)
private val PinkAccent = Color(0xFFFF69B4)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LineageExplorerScreen(
    productId: String,
    onNavigateBack: () -> Unit,
    onNavigateToBirdProfile: (String) -> Unit,
    viewModel: LineageExplorerViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Lineage Explorer", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        uiState.rootBird?.let {
                            Text(it.name, fontSize = 12.sp, color = GoldAccent)
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
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // View mode tabs
            ViewModeTabs(
                currentMode = uiState.viewMode,
                onModeChange = viewModel::setViewMode,
                ancestorCount = uiState.ancestorTree?.countAncestors() ?: 0,
                descendantCount = uiState.descendants.size
            )

            // Selected node BVI card (if any)
            uiState.selectedNodeBvi?.let { bvi ->
                BviSummaryBar(bvi = bvi, birdName = uiState.selectedNode?.bird?.name ?: "")
            }

            when (uiState.viewMode) {
                LineageViewMode.ANCESTORS -> {
                    uiState.ancestorTree?.let { tree ->
                        AncestorTreeView(
                            tree = tree,
                            expandedNodes = uiState.expandedNodes,
                            selectedNodeId = uiState.selectedNode?.bird?.id,
                            onNodeClick = viewModel::selectNode,
                            onToggleExpand = viewModel::toggleNodeExpansion,
                            onViewProfile = onNavigateToBirdProfile,
                            getContribution = viewModel::geneticContribution
                        )
                    } ?: Box(
                        Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No pedigree data", color = Color.White.copy(alpha = 0.5f))
                    }
                }

                LineageViewMode.DESCENDANTS -> {
                    DescendantListView(
                        descendants = uiState.descendants,
                        onViewProfile = onNavigateToBirdProfile
                    )
                }
            }
        }
    }
}

@Composable
private fun ViewModeTabs(
    currentMode: LineageViewMode,
    onModeChange: (LineageViewMode) -> Unit,
    ancestorCount: Int,
    descendantCount: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ModeTab(
            label = "Ancestors ($ancestorCount)",
            icon = Icons.Filled.AccountTree,
            selected = currentMode == LineageViewMode.ANCESTORS,
            onClick = { onModeChange(LineageViewMode.ANCESTORS) },
            modifier = Modifier.weight(1f)
        )
        ModeTab(
            label = "Offspring ($descendantCount)",
            icon = Icons.Filled.ChildCare,
            selected = currentMode == LineageViewMode.DESCENDANTS,
            onClick = { onModeChange(LineageViewMode.DESCENDANTS) },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun ModeTab(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) PurplePrimary else CardDark
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = if (selected) CyanAccent else Color.White.copy(alpha = 0.5f),
                modifier = Modifier.size(18.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                label,
                color = if (selected) Color.White else Color.White.copy(alpha = 0.6f),
                fontSize = 13.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}

@Composable
private fun BviSummaryBar(bvi: BreedingValueResult, birdName: String) {
    val bviColor = when (bvi.rating) {
        "Elite" -> GoldAccent
        "Strong" -> SuccessGreen
        "Average" -> WarningOrange
        "Developing" -> CyanAccent
        else -> Color.White.copy(alpha = 0.5f)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardDark)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // BVI score circle
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(bviColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "${(bvi.bvi * 100).toInt()}",
                    color = bviColor,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp
                )
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "BVI: ${bvi.rating}",
                    color = bviColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Text(
                    "${bvi.traitCount} traits • ${bvi.showWins}/${bvi.showTotal} shows • ${bvi.offspringCount} offspring",
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 11.sp
                )
            }
        }
    }
}

@Composable
private fun AncestorTreeView(
    tree: PedigreeTree,
    expandedNodes: Set<String>,
    selectedNodeId: String?,
    onNodeClick: (PedigreeTree) -> Unit,
    onToggleExpand: (String) -> Unit,
    onViewProfile: (String) -> Unit,
    getContribution: (Int) -> Float
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        item {
            AncestorNode(
                tree = tree,
                depth = 0,
                expandedNodes = expandedNodes,
                selectedNodeId = selectedNodeId,
                onNodeClick = onNodeClick,
                onToggleExpand = onToggleExpand,
                onViewProfile = onViewProfile,
                getContribution = getContribution
            )
        }
        item { Spacer(Modifier.height(80.dp)) }
    }
}

@Composable
private fun AncestorNode(
    tree: PedigreeTree,
    depth: Int,
    expandedNodes: Set<String>,
    selectedNodeId: String?,
    onNodeClick: (PedigreeTree) -> Unit,
    onToggleExpand: (String) -> Unit,
    onViewProfile: (String) -> Unit,
    getContribution: (Int) -> Float
) {
    val isExpanded = tree.bird.id in expandedNodes || depth == 0
    val isSelected = tree.bird.id == selectedNodeId
    val hasChildren = tree.sire != null || tree.dam != null
    val contribution = getContribution(depth)

    val genderColor = when {
        tree.bird.name.contains("♂", true) -> CyanAccent
        tree.bird.name.contains("♀", true) -> PinkAccent
        else -> Color.White // We show gender via relation
    }

    Column(modifier = Modifier.padding(start = (depth * 20).dp)) {
        // Node card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onNodeClick(tree) },
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isSelected) PurplePrimary.copy(alpha = 0.6f) else CardDark
            ),
            border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, CyanAccent) else null
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Expand/collapse toggle
                if (hasChildren) {
                    IconButton(
                        onClick = { onToggleExpand(tree.bird.id) },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            if (isExpanded) Icons.Filled.ExpandMore else Icons.Filled.ChevronRight,
                            contentDescription = null,
                            tint = CyanAccent,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                } else {
                    Spacer(Modifier.width(28.dp))
                }

                Spacer(Modifier.width(4.dp))

                // Generation badge
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(
                            when (depth) {
                                0 -> CyanAccent.copy(alpha = 0.2f)
                                1 -> PurplePrimary.copy(alpha = 0.3f)
                                2 -> GoldAccent.copy(alpha = 0.2f)
                                else -> Color.White.copy(alpha = 0.1f)
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "G$depth",
                        color = when (depth) {
                            0 -> CyanAccent
                            1 -> PurplePrimary
                            2 -> GoldAccent
                            else -> Color.White.copy(alpha = 0.6f)
                        },
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(Modifier.width(10.dp))

                // Bird info
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        tree.bird.name,
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Row {
                        tree.bird.breed?.let { breed ->
                            Text(
                                breed,
                                color = GoldAccent.copy(alpha = 0.7f),
                                fontSize = 11.sp
                            )
                            Text(" • ", color = Color.White.copy(alpha = 0.3f), fontSize = 11.sp)
                        }
                        Text(
                            "${contribution}% DNA",
                            color = CyanAccent.copy(alpha = 0.7f),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // View profile button
                if (!tree.bird.isGuestParent) {
                    IconButton(
                        onClick = { onViewProfile(tree.bird.id) },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            Icons.Filled.OpenInNew,
                            contentDescription = "View Profile",
                            tint = Color.White.copy(alpha = 0.4f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }

        // Children (sire + dam)
        AnimatedVisibility(visible = isExpanded && hasChildren) {
            Column(
                modifier = Modifier.padding(top = 4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                tree.sire?.let { sire ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Spacer(Modifier.width((depth * 20 + 16).dp))
                        Text("♂ Sire", color = CyanAccent, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                    AncestorNode(
                        tree = sire,
                        depth = depth + 1,
                        expandedNodes = expandedNodes,
                        selectedNodeId = selectedNodeId,
                        onNodeClick = onNodeClick,
                        onToggleExpand = onToggleExpand,
                        onViewProfile = onViewProfile,
                        getContribution = getContribution
                    )
                }
                tree.dam?.let { dam ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Spacer(Modifier.width((depth * 20 + 16).dp))
                        Text("♀ Dam", color = PinkAccent, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                    AncestorNode(
                        tree = dam,
                        depth = depth + 1,
                        expandedNodes = expandedNodes,
                        selectedNodeId = selectedNodeId,
                        onNodeClick = onNodeClick,
                        onToggleExpand = onToggleExpand,
                        onViewProfile = onViewProfile,
                        getContribution = getContribution
                    )
                }
            }
        }
    }
}

@Composable
private fun DescendantListView(
    descendants: List<com.rio.rostry.data.database.entity.ProductEntity>,
    onViewProfile: (String) -> Unit
) {
    if (descendants.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    Icons.Filled.ChildCare,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.3f),
                    modifier = Modifier.size(64.dp)
                )
                Spacer(Modifier.height(16.dp))
                Text("No Offspring Yet", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text(
                    "Record matings to start tracking descendants",
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 13.sp
                )
            }
        }
        return
    }

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(descendants) { child ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onViewProfile(child.productId) },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = CardDark)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(PurplePrimary.copy(alpha = 0.3f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Filled.Pets,
                            contentDescription = null,
                            tint = CyanAccent,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            child.name,
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                        Row {
                            child.breed?.let { breed ->
                                Text(breed, color = GoldAccent.copy(alpha = 0.7f), fontSize = 11.sp)
                            }
                            child.gender?.let { gender ->
                                Text(
                                    " • ${if (gender.equals("Male", true)) "♂" else "♀"}",
                                    color = if (gender.equals("Male", true)) CyanAccent else PinkAccent,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            child.ageWeeks?.let { age ->
                                Text(
                                    " • ${age}w",
                                    color = Color.White.copy(alpha = 0.5f),
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }

                    Icon(
                        Icons.Filled.ChevronRight,
                        contentDescription = "View",
                        tint = Color.White.copy(alpha = 0.3f)
                    )
                }
            }
        }

        item { Spacer(Modifier.height(80.dp)) }
    }
}
