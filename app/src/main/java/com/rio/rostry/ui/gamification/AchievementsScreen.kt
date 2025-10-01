package com.rio.rostry.ui.gamification

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AchievementsScreen() {
    Column(Modifier.padding(16.dp)) {
        Text("Achievements", style = MaterialTheme.typography.headlineSmall)
        Text("Your badges and progress will appear here.")
    }
}
