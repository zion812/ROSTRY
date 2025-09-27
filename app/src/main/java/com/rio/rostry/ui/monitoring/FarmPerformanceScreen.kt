package com.rio.rostry.ui.monitoring

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.rio.rostry.ui.theme.LocalSpacing

@Composable
fun FarmPerformanceScreen() {
    val sp = LocalSpacing.current
    Column(
        modifier = Modifier.fillMaxSize().padding(sp.lg),
        verticalArrangement = Arrangement.spacedBy(sp.sm)
    ) {
        Text("Farm Performance", style = MaterialTheme.typography.titleLarge)
        ElevatedCard {
            Column(Modifier.padding(sp.sm), verticalArrangement = Arrangement.spacedBy(sp.xs)) {
                Text("Weekly performance reports will appear here.")
            }
        }
    }
}
