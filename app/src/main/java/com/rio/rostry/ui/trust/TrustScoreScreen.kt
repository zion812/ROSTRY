package com.rio.rostry.ui.trust

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun TrustScoreScreen(
    userId: String? = null,
    onBack: () -> Unit,
    vm: TrustScoreViewModel = hiltViewModel()
) {
    LaunchedEffect(userId) { vm.load(userId) }
    val state = vm.state.collectAsStateWithLifecycle().value

    Column(Modifier.fillMaxSize().padding(12.dp)) {
        Text("Trust Score", style = MaterialTheme.typography.titleMedium)
        if (state.error != null) {
            Text(text = state.error ?: "", color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 8.dp))
        }
        Card(Modifier.fillMaxWidth().padding(top = 12.dp)) {
            Column(Modifier.fillMaxWidth().padding(12.dp)) {
                Text("User: ${state.userId}")
                Text("Score: ${"%.1f".format(state.score)}")
            }
        }
        Card(Modifier.fillMaxWidth().padding(top = 12.dp)) {
            Column(Modifier.fillMaxWidth().padding(12.dp)) {
                Text("Breakdown", style = MaterialTheme.typography.titleSmall)
                Text("Success Rate: ${"%.1f".format(state.breakdown.successRate)}%")
                Text("Authenticity Checks: ${"%.1f".format(state.breakdown.authenticityChecks)}%")
                Text("Docs Quality: ${"%.1f".format(state.breakdown.docsQuality)}%")
                Text("Disputes Impact: ${"%.1f".format(state.breakdown.disputesImpact)}%")
                Text("Response Time: ${"%.1f".format(state.breakdown.responseTime)}%")
            }
        }
        Row(Modifier.fillMaxWidth().padding(top = 12.dp)) {
            TextButton(onClick = onBack) { Text("Back") }
        }
    }
}
