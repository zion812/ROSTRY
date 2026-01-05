package com.rio.rostry.ui.enthusiast.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.rio.rostry.ui.theme.Dimens
import com.rio.rostry.ui.components.PremiumCard
import androidx.compose.foundation.clickable
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.RoundedCornerShape

@Composable
fun EnthusiastKpiCard(
    title: String,
    value: String,
    icon: ImageVector,
    trendIndicator: String? = null,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    val internalModifier = modifier
        .width(140.dp)
        .semantics { contentDescription = "$title: $value" }
        .then(if (onClick != null) Modifier.clip(RoundedCornerShape(24.dp)).clickable { onClick() } else Modifier)

    PremiumCard(
        modifier = internalModifier
    ) {
        Column(
            modifier = Modifier.padding(Dimens.space_small),
            verticalArrangement = Arrangement.spacedBy(Dimens.space_small),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            trendIndicator?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}