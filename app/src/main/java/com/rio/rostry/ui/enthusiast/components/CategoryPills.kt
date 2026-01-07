package com.rio.rostry.ui.enthusiast.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rio.rostry.ui.theme.ElectricBlue
import com.rio.rostry.ui.theme.EnthusiastGold
import com.rio.rostry.ui.theme.RostryBlue

/**
 * Category types for explore filtering.
 */
enum class ExploreCategory(
    val emoji: String,
    val label: String,
    val activeColor: Color
) {
    ALL("🔍", "All", ElectricBlue),
    HOT("🔥", "Hot", Color(0xFFFF6B35)),
    CHAMPIONS("🏆", "Champions", EnthusiastGold),
    NEARBY("📍", "Nearby", Color(0xFF10B981)),
    FRESH("🥚", "Fresh", Color(0xFFFFA726)),
    VERIFIED("✓", "Verified", RostryBlue)
}

/**
 * Amazon-inspired horizontal scrolling category pills.
 * Features:
 * - Horizontal scroll with snap behavior
 * - Gradient highlight for selected pill
 * - Scale animation on selection
 * - Haptic feedback
 */
@Composable
fun CategoryPills(
    selectedCategory: ExploreCategory,
    onCategorySelected: (ExploreCategory) -> Unit,
    modifier: Modifier = Modifier,
    categories: List<ExploreCategory> = ExploreCategory.values().toList()
) {
    val scrollState = rememberScrollState()
    val haptic = LocalHapticFeedback.current
    
    Row(
        modifier = modifier
            .horizontalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        categories.forEach { category ->
            val isSelected = category == selectedCategory
            
            CategoryPill(
                category = category,
                isSelected = isSelected,
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onCategorySelected(category)
                }
            )
        }
    }
}

@Composable
private fun CategoryPill(
    category: ExploreCategory,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.05f else 1f,
        animationSpec = tween(200),
        label = "pillScale"
    )
    
    val containerColor by animateColorAsState(
        targetValue = if (isSelected) category.activeColor else MaterialTheme.colorScheme.surfaceVariant,
        animationSpec = tween(200),
        label = "pillColor"
    )
    
    val contentColor by animateColorAsState(
        targetValue = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
        animationSpec = tween(200),
        label = "pillContentColor"
    )
    
    FilterChip(
        selected = isSelected,
        onClick = onClick,
        label = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = category.emoji,
                    modifier = Modifier.scale(if (isSelected) 1.1f else 1f)
                )
                Text(
                    text = category.label,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                    color = contentColor
                )
            }
        },
        colors = FilterChipDefaults.filterChipColors(
            containerColor = containerColor.copy(alpha = 0.15f),
            selectedContainerColor = containerColor,
            labelColor = contentColor,
            selectedLabelColor = Color.White
        ),
        border = FilterChipDefaults.filterChipBorder(
            enabled = true,
            selected = isSelected,
            borderColor = if (isSelected) Color.Transparent else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
            selectedBorderColor = Color.Transparent
        ),
        shape = RoundedCornerShape(20.dp),
        modifier = modifier.scale(scale)
    )
}

/**
 * Compact category pills variant for overlay usage.
 */
@Composable
fun CompactCategoryPills(
    selectedCategory: ExploreCategory,
    onCategorySelected: (ExploreCategory) -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color.Black.copy(alpha = 0.5f)
) {
    com.rio.rostry.ui.components.GlassmorphicOverlay(
        modifier = modifier,
        cornerRadius = 24.dp,
        contentPadding = 4.dp
    ) {
        CategoryPills(
            selectedCategory = selectedCategory,
            onCategorySelected = onCategorySelected,
            modifier = Modifier.padding(0.dp)
        )
    }
}

/**
 * Single category badge for use in cards.
 */
@Composable
fun CategoryBadge(
    category: ExploreCategory,
    modifier: Modifier = Modifier
) {
    FilterChip(
        selected = true,
        onClick = { },
        label = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(text = category.emoji, modifier = Modifier.size(12.dp))
                Text(
                    text = category.label,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Medium
                )
            }
        },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = category.activeColor.copy(alpha = 0.8f),
            selectedLabelColor = Color.White
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
    )
}
