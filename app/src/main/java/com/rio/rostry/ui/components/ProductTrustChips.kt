package com.rio.rostry.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rio.rostry.data.database.entity.ProductEntity
import java.time.LocalDate
import java.time.temporal.ChronoUnit

/**
 * Trust Chips Row - Shows trust indicators on product cards.
 * - Verified Farmer: Check if seller is verified
 * - Age Band: Calculate from birthDate
 */
@Composable
fun TrustChipRow(
    product: ProductEntity,
    isSellerVerified: Boolean = false,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Verified Farmer Chip
        if (isSellerVerified) {
            TrustChip(
                icon = Icons.Default.Verified,
                text = "Verified",
                containerColor = Color(0xFF4CAF50),
                contentColor = Color.White
            )
        }

        // Age Band Chip
        product.birthDate?.let { birthDate ->
            val ageBand = calculateAgeBand(birthDate)
            if (ageBand.isNotEmpty()) {
                TrustChip(
                    icon = Icons.Default.Schedule,
                    text = ageBand,
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }

        // Weight Ready Chip
        if ((product.weightGrams ?: 0.0) > 2000) {
            TrustChip(
                icon = Icons.Default.Star,
                text = "Ready",
                containerColor = Color(0xFFFFF8E1),
                contentColor = Color(0xFFFF8F00)
            )
        }
    }
}

@Composable
private fun TrustChip(
    icon: ImageVector,
    text: String,
    containerColor: Color,
    contentColor: Color
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = containerColor
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(12.dp),
                tint = contentColor
            )
            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Medium,
                color = contentColor
            )
        }
    }
}

/**
 * Calculate age band from birth date.
 */
private fun calculateAgeBand(birthDate: Long): String {
    val birthLocalDate = java.time.Instant.ofEpochMilli(birthDate)
        .atZone(java.time.ZoneId.systemDefault())
        .toLocalDate()
    val now = LocalDate.now()
    val weeksOld = ChronoUnit.WEEKS.between(birthLocalDate, now)
    val monthsOld = ChronoUnit.MONTHS.between(birthLocalDate, now)

    return when {
        weeksOld < 4 -> "${weeksOld}-${weeksOld + 1} weeks"
        monthsOld < 12 -> "${monthsOld}-${monthsOld + 1} months"
        else -> "${monthsOld / 12}+ years"
    }
}

/**
 * Compact Product Card with trust chips.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompactProductCard(
    product: ProductEntity,
    isSellerVerified: Boolean = false,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.width(160.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Product Image placeholder
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp),
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text("🐔", style = MaterialTheme.typography.headlineMedium)
                }
            }

            Spacer(Modifier.height(8.dp))

            // Name
            Text(
                text = product.name,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1
            )

            // Breed
            product.breed?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }

            Spacer(Modifier.height(4.dp))

            // Price
            Text(
                text = "₹${product.price?.toInt() ?: 0}",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(Modifier.height(6.dp))

            // Trust Chips
            TrustChipRow(
                product = product,
                isSellerVerified = isSellerVerified,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
