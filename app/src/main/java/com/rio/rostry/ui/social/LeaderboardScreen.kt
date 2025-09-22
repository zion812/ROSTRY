package com.rio.rostry.ui.social

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun LeaderboardScreen(vm: LeaderboardViewModel = hiltViewModel()) {
    val top by vm.top.collectAsState()
    Column(Modifier.fillMaxSize().padding(12.dp)) {
        Text(text = "Leaderboard", style = MaterialTheme.typography.titleMedium)
        LazyColumn(Modifier.fillMaxSize()) {
            items(top) { rep ->
                Card(Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
                    Column(Modifier.fillMaxWidth().padding(12.dp)) {
                        Text(text = rep.userId.take(12))
                        Text(text = "Score: ${rep.score}")
                    }
                }
            }
        }
    }
}
