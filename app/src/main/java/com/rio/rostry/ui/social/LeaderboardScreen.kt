package com.rio.rostry.ui.social

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rio.rostry.ui.components.EmptyState

@Composable
fun LeaderboardScreen(vm: LeaderboardViewModel = hiltViewModel()) {
    val top by vm.top.collectAsState()
    val selectedTabIndex = remember { mutableStateOf(0) } // Default to "Weekly"
    val tabs = listOf("Weekly", "Monthly", "All Time")

    Column(Modifier.fillMaxSize().padding(12.dp)) {
        Text(text = "Leaderboard", style = MaterialTheme.typography.titleMedium)
        TabRow(selectedTabIndex = selectedTabIndex.value) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex.value == index,
                    onClick = {
                        selectedTabIndex.value = index
                        when (index) {
                            0 -> vm.setPeriod("week")
                            1 -> vm.setPeriod("month")
                            2 -> vm.setPeriod("all")
                        }
                    },
                    text = { Text(title) }
                )
            }
        }
        if (top.isEmpty()) {
            EmptyState(
                icon = Icons.Default.Person,
                title = "No leaderboard yet",
                description = "Start engaging to earn points!"
            )
        } else {
            LazyColumn(
                Modifier.fillMaxSize()
            ) {
                itemsIndexed(top) { index, rep ->
                    val rank = index + 1
                    val badge = when (rank) {
                        1 -> "🥇"
                        2 -> "🥈"
                        3 -> "🥉"
                        else -> "$rank"
                    }
                    Card(Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
                        Row(
                            Modifier.fillMaxWidth().padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(text = badge, style = MaterialTheme.typography.titleMedium)
                            Icon(Icons.Default.Person, contentDescription = "Avatar")
                            Column(Modifier.weight(1f)) {
                                Text(text = rep.displayName)
                                Text(text = "Score: ${rep.score.toString().reversed().chunked(3).joinToString(",").reversed()}")
                            }
                        }
                    }
                }
            }
        }
    }
}
