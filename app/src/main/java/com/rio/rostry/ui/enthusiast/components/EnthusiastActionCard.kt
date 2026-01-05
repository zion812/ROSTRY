package com.rio.rostry.ui.enthusiast.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Badge
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.rio.rostry.ui.theme.Dimens
import com.rio.rostry.ui.components.PremiumCard

@Composable
fun EnthusiastActionCard(
    title: String,
    icon: ImageVector,
    count: Int? = null,
    description: String? = null,
    badge: Pair<Int, Color>? = null,
    content: @Composable () -> Unit = {},
    actions: @Composable (androidx.compose.foundation.layout.RowScope.() -> Unit)
) {
    PremiumCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(Dimens.space_large),
            verticalArrangement = Arrangement.spacedBy(Dimens.space_medium)
        ) {
            // Header row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Dimens.space_medium)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(Dimens.icon_medium)
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium
                )
                badge?.let { (badgeCount, badgeColor) ->
                    Badge(containerColor = badgeColor) {
                        Text(badgeCount.toString())
                    }
                }
            }

            // Optional count
            count?.let {
                Text(
                    text = it.toString(),
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            // Optional description
            description?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Content slot
            content()

            // Actions row
            Row(
                horizontalArrangement = Arrangement.spacedBy(Dimens.space_medium),
                modifier = Modifier.fillMaxWidth()
            ) {
                actions()
            }
        }
    }
}