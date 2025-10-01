package com.rio.rostry.ui.insights

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun InsightsScreen() {
    Column(Modifier.padding(16.dp)) {
        Text("Smart Insights", style = MaterialTheme.typography.headlineSmall)
        Text("Personalized recommendations will appear here.")
    }
}
