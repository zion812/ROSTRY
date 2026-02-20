package com.rio.rostry.ui.enthusiast.community

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * GroupsScreen - Community group discovery.
 * Shows suggested community groups that users can navigate into.
 * Groups are populated from a local seed list until the community backend arrives.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToGroup: (String) -> Unit = {}
) {
    val suggestedGroups = listOf(
        SuggestedGroup("breed_aseel", "Aseel Enthusiasts", "Discuss Aseel breeding, shows, and care", "Breed Specific"),
        SuggestedGroup("breed_kadaknath", "Kadaknath Breeders India", "Share Kadaknath genetics and husbandry tips", "Breed Specific"),
        SuggestedGroup("region_ap", "AP/TS Poultry Hub", "Andhra Pradesh & Telangana breeders network", "Regional"),
        SuggestedGroup("tips_nutrition", "Nutrition & Feed Science", "Optimize feed conversion and bird health", "Knowledge"),
        SuggestedGroup("show_circuit", "Show Circuit India", "Exhibition schedules, results, and judge insights", "Shows & Events"),
        SuggestedGroup("beginners", "New Breeders Welcome", "Ask questions, share your journey", "General")
    )

    var searchQuery by androidx.compose.runtime.mutableStateOf("")
    var isSearching by androidx.compose.runtime.mutableStateOf(false)

    val filteredGroups = if (searchQuery.isBlank()) {
        suggestedGroups
    } else {
        suggestedGroups.filter { 
            it.name.contains(searchQuery, ignoreCase = true) || 
            it.description.contains(searchQuery, ignoreCase = true) ||
            it.category.contains(searchQuery, ignoreCase = true)
        }
    }

    Scaffold(
        topBar = {
            if (!isSearching) {
                TopAppBar(
                    title = { Text("Community Groups") },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                    actions = {
                        IconButton(onClick = { isSearching = true }) {
                            Icon(Icons.Filled.Search, contentDescription = "Search")
                        }
                    }
                )
            } else {
                androidx.compose.material3.SearchBar(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    onSearch = { /* Done via filtering */ },
                    active = true,
                    onActiveChange = { if (!it) { isSearching = false; searchQuery = "" } },
                    placeholder = { Text("Search groups...") },
                    leadingIcon = {
                        IconButton(onClick = { isSearching = false; searchQuery = "" }) {
                            Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Filled.Close, contentDescription = "Clear")
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    GroupList(filteredGroups, onNavigateToGroup)
                }
            }
        }
    ) { padding ->
        if (!isSearching) {
            GroupList(filteredGroups, onNavigateToGroup, padding)
        }
    }
}

@Composable
private fun GroupList(
    groups: List<SuggestedGroup>,
    onNavigateToGroup: (String) -> Unit,
    padding: PaddingValues = PaddingValues(0.dp)
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        if (padding != PaddingValues(0.dp)) {
            item {
                Text(
                    "Suggested Groups",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Text(
                    "Discover communities that match your interests",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
        }

        if (groups.isEmpty()) {
            item {
                Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                    Text("No groups found", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }

        items(groups) { group ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToGroup(group.id) },
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Group,
                        contentDescription = null,
                        modifier = Modifier.size(40.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            group.name,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            group.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2
                        )
                        Spacer(Modifier.height(4.dp))
                        AssistChip(
                            onClick = {},
                            label = { Text(group.category, style = MaterialTheme.typography.labelSmall) }
                        )
                    }
                }
            }
        }

        item {
            Spacer(Modifier.height(16.dp))
            Text(
                "More groups will appear as the community grows",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                modifier = Modifier.fillMaxWidth(),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

private data class SuggestedGroup(
    val id: String,
    val name: String,
    val description: String,
    val category: String
)
