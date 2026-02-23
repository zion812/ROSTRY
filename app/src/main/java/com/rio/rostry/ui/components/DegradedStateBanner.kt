package com.rio.rostry.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.rio.rostry.domain.manager.DegradedService

@Composable
fun DegradedStateBanner(
    degradedServices: Set<DegradedService>,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = degradedServices.isNotEmpty(),
        enter = expandVertically(),
        exit = shrinkVertically()
    ) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.errorContainer)
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            val message = when {
                degradedServices.contains(DegradedService.NETWORK) -> "You are offline. Showing cached data."
                degradedServices.size == 1 -> {
                    val serviceName = degradedServices.first().name.lowercase().replace("_", " ")
                    "Limited functionality: $serviceName is degraded."
                }
                else -> "Limited functionality: Multiple services are currently degraded."
            }
            
            Text(
                text = message,
                color = MaterialTheme.colorScheme.onErrorContainer,
                style = MaterialTheme.typography.labelMedium,
                textAlign = TextAlign.Center
            )
        }
    }
}
