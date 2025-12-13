package com.rio.rostry.ui.enthusiast.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.rio.rostry.data.database.entity.FarmAlertEntity
import com.rio.rostry.ui.theme.Dimens

@Composable
fun EnthusiastAlertCard(
    alerts: List<FarmAlertEntity>,
    onDismiss: (() -> Unit)? = null
) {
    val hasAlerts = alerts.isNotEmpty()
    val containerColor = if (hasAlerts) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.tertiaryContainer
    val onContainerColor = if (hasAlerts) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onTertiaryContainer
    val headerIcon = if (hasAlerts) Icons.Filled.Warning else Icons.Filled.CheckCircle

    Card(
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        Column(
            modifier = Modifier.padding(Dimens.space_large),
            verticalArrangement = Arrangement.spacedBy(Dimens.space_medium)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Dimens.space_medium)
            ) {
                Icon(
                    imageVector = headerIcon,
                    contentDescription = null,
                    tint = onContainerColor,
                    modifier = Modifier.size(Dimens.icon_medium)
                )
                Text(
                    text = "Critical Alerts",
                    style = MaterialTheme.typography.titleMedium,
                    color = onContainerColor
                )
            }

            if (hasAlerts) {
                alerts.forEach { alert ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(Dimens.space_medium)
                    ) {
                        val badgeColor = when (alert.severity.uppercase()) {
                            "CRITICAL", "HIGH" -> MaterialTheme.colorScheme.error
                            "MEDIUM" -> MaterialTheme.colorScheme.tertiary
                            "LOW", "INFO" -> MaterialTheme.colorScheme.secondary
                            else -> MaterialTheme.colorScheme.secondary
                        }
                        Badge(
                            containerColor = badgeColor,
                            contentColor = Color.White
                        ) {
                            Text(alert.severity)
                        }
                        Text(
                            text = alert.message,
                            style = MaterialTheme.typography.bodyMedium,
                            color = onContainerColor
                        )
                    }
                }
                if (onDismiss != null) {
                    Button(onClick = onDismiss) {
                        Text("Dismiss All")
                    }
                }
            } else {
                Text(
                    text = "No critical alerts",
                    style = MaterialTheme.typography.bodyMedium,
                    color = onContainerColor
                )
            }
        }
    }
}