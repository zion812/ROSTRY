package com.rio.rostry.ui.enthusiast.arena

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.AccountTree
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rio.rostry.data.database.entity.CompetitionEntryEntity
import coil.compose.AsyncImage
import com.rio.rostry.domain.model.CompetitionStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompetitionDetailScreen(
    competitionId: String,
    onNavigateBack: () -> Unit,
    viewModel: VirtualArenaViewModel = hiltViewModel()
) {
    // In a real app, this would be collected from a specific ID flow
    // For now we'll assume the VM has the list and we find it or fetch it
    // But since we don't have getCompetitionById exposed yet, let's just get the list and find it
    // Or better, update VM to have a detail flow. 
    // For this step, I'll mock the data access slightly or assume VM has it. 
    // Actually, let's fix the VM first if needed, but for UI scaffold:
    
    val competition = viewModel.getCompetitionById(competitionId).collectAsState(initial = null).value
    
    // Tabs
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Overview", "Bracket", "Leaderboard", "Gallery")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(competition?.title ?: "Competition Details") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            TabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { 
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                val icon = when(index) {
                                    0 -> Icons.Default.Info
                                    1 -> Icons.Default.AccountTree
                                    2 -> Icons.Default.EmojiEvents
                                    else -> Icons.Default.PhotoLibrary
                                }
                                Icon(icon, null, Modifier.size(16.dp))
                                Spacer(Modifier.width(4.dp))
                                Text(title, fontSize = 12.sp) 
                            }
                        }
                    )
                }
            }

            if (competition != null) {
                when (selectedTab) {
                    0 -> OverviewTab(competition)
                    1 -> BracketTab(competition)
                    2 -> LeaderboardTab()
                    3 -> GalleryTab()
                }
            } else {
                 Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                     CircularProgressIndicator()
                 }
            }
        }
    }
}

@Composable
fun OverviewTab(comp: CompetitionEntryEntity) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
             if (comp.bannerUrl != null) {
                AsyncImage(
                    model = comp.bannerUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Icon(Icons.Default.EmojiEvents, null, Modifier.size(64.dp), tint = MaterialTheme.colorScheme.onPrimaryContainer)
            }
        }
        
        Text(comp.title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        
        StatusChip(comp.status)
        
        Text("Description", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Text(comp.description, style = MaterialTheme.typography.bodyMedium)
        
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(Modifier.padding(16.dp)) {
                Text("Details", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                DetailRow("Entry Fee", comp.entryFee?.let { "₹$it" } ?: "Free")
                DetailRow("Prize Pool", comp.prizePool ?: "N/A")
                DetailRow("Participants", "${comp.participantCount}")
                DetailRow("Region", comp.region)
                DetailRow("Start Date", java.text.SimpleDateFormat("MMM dd, yyyy").format(java.util.Date(comp.startTime)))
            }
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun BracketTab(comp: CompetitionEntryEntity) {
    // Determine bracket structure from JSON or mock
    val rounds = listOf("Quarter Finals", "Semi-Finals", "Finals")
    
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            "Tournament Bracket", 
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.titleMedium
        )
        
        LazyRow(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            items(rounds.size) { roundIndex ->
                BracketRoundColumn(
                    roundName = rounds[roundIndex],
                    matchCount = 4 / (1 shl roundIndex) // 4, 2, 1
                )
            }
        }
    }
}

@Composable
fun BracketRoundColumn(roundName: String, matchCount: Int) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(180.dp),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            roundName, 
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        // This logic vertically centers matches
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            repeat(matchCount) {
                MatchCard()
            }
        }
    }
}

@Composable
fun MatchCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp)
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center
        ) {
            ParticipantRow("Player 1", true, "Win")
            Divider(color = MaterialTheme.colorScheme.outlineVariant)
            ParticipantRow("Player 2", false)
        }
    }
}

@Composable
fun ParticipantRow(name: String, isWinner: Boolean, status: String = "") {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(if (isWinner) Color(0xFFE8F5E9) else Color.Transparent)
            .padding(horizontal = 8.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            name, 
            fontSize = 12.sp, 
            fontWeight = if(isWinner) FontWeight.Bold else FontWeight.Normal,
            maxLines = 1
        )
        if (isWinner) {
            Text(status, fontSize = 10.sp, color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun LeaderboardTab() {
    val leaderboard = listOf(
        Triple(1, "Champion Farm", 1250),
        Triple(2, "Sunny Side Up", 1100),
        Triple(3, "Golden Roosters", 950),
        Triple(4, "Red Barn", 800),
        Triple(5, "Blue Ribbon", 750)
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp)
    ) {
        item {
            Text("Top Performers", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(bottom = 8.dp))
        }
        items(leaderboard) { (rank, name, score) ->
            ListItem(
                headlineContent = { Text(name, fontWeight = FontWeight.SemiBold) },
                leadingContent = {
                    Surface(
                        color = when(rank) {
                            1 -> Color(0xFFFFD700) // Gold
                            2 -> Color(0xFFE0E0E0) // Silver
                            3 -> Color(0xFFCD7F32) // Bronze
                            else -> MaterialTheme.colorScheme.surfaceVariant
                        },
                        shape = CircleShape,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = "#$rank",
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                        }
                    }
                },
                supportingContent = { Text("Score: $score") }
            )
            Divider()
        }
    }
}

@Composable
fun GalleryTab() {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(6) {
            Card(
                modifier = Modifier.aspectRatio(1f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize().background(Color.LightGray),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.PhotoLibrary, null, tint = Color.Gray)
                }
            }
        }
    }
}
