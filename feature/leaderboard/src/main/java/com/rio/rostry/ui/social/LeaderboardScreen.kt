package com.rio.rostry.ui.social
import com.rio.rostry.domain.monitoring.repository.ShowRecordRepository
import com.rio.rostry.domain.error.ErrorHandler

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun LeaderboardScreen(
    top: List<LeaderboardEntry>,
    onPeriodSelected: (String) -> Unit
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Weekly", "Monthly", "All Time")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        Text(text = "Leaderboard", style = MaterialTheme.typography.titleMedium)
        TabRow(selectedTabIndex = selectedTabIndex) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = {
                        selectedTabIndex = index
                        onPeriodSelected(
                            when (index) {
                                0 -> "week"
                                1 -> "month"
                                else -> "all"
                            }
                        )
                    },
                    text = { Text(title) }
                )
            }
        }

        if (top.isEmpty()) {
            LeaderboardEmptyState()
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                itemsIndexed(top) { index, rep ->
                    val rank = index + 1
                    val badge = when (rank) {
                        1 -> "1st"
                        2 -> "2nd"
                        3 -> "3rd"
                        else -> "$rank"
                    }
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(text = badge, style = MaterialTheme.typography.titleMedium)
                            Icon(Icons.Default.Person, contentDescription = "Avatar")
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = rep.displayName)
                                Text(text = "Score: ${formatScore(rep.score)}")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LeaderboardEmptyState() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = "No leaderboard yet",
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = "Start engaging to earn points!",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

private fun formatScore(score: Long): String =
    score.toString().reversed().chunked(3).joinToString(",").reversed()
