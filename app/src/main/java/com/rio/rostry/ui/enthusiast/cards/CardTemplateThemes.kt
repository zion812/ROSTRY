package com.rio.rostry.ui.enthusiast.cards

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rio.rostry.ui.components.pressAnimation
import com.rio.rostry.ui.theme.Coral
import com.rio.rostry.ui.theme.DeepNavy
import com.rio.rostry.ui.theme.ElectricBlue
import com.rio.rostry.ui.theme.EnthusiastGold
import com.rio.rostry.ui.theme.EnthusiastGoldVariant
import com.rio.rostry.ui.theme.RostryBlue

/**
 * Card template themes for Rooster Cards.
 * Each template defines colors, gradients, and styling.
 */
sealed class CardTemplate(
    val name: String,
    val description: String,
    val backgroundGradient: Brush,
    val primaryColor: Color,
    val secondaryColor: Color,
    val textColor: Color,
    val accentColor: Color
) {
    /** Aggressive, battle-ready theme with black and red. */
    object Brawler : CardTemplate(
        name = "Brawler",
        description = "Aggressive & Battle-Ready",
        backgroundGradient = Brush.linearGradient(
            colors = listOf(
                Color(0xFF1A0A0A),
                Color(0xFF2D0A0A),
                Color(0xFF1A0A0A)
            )
        ),
        primaryColor = Coral,
        secondaryColor = Color(0xFF8B0000),
        textColor = Color.White,
        accentColor = Color(0xFFFF4500)
    )
    
    /** Elegant, exhibition theme with gold and white. */
    object Showstopper : CardTemplate(
        name = "Showstopper",
        description = "Elegant & Exhibition",
        backgroundGradient = Brush.linearGradient(
            colors = listOf(
                Color(0xFF1A1A0A),
                Color(0xFF2D2A1A),
                Color(0xFF1A1A0A)
            )
        ),
        primaryColor = EnthusiastGold,
        secondaryColor = EnthusiastGoldVariant,
        textColor = Color.White,
        accentColor = Color(0xFFFFF8DC)
    )
    
    /** Scientific, data-rich theme with deep blue. */
    object Geneticist : CardTemplate(
        name = "Geneticist",
        description = "Scientific & Data-Rich",
        backgroundGradient = Brush.linearGradient(
            colors = listOf(
                DeepNavy,
                Color(0xFF0A2848),
                DeepNavy
            )
        ),
        primaryColor = ElectricBlue,
        secondaryColor = RostryBlue,
        textColor = Color.White,
        accentColor = Color(0xFF87CEEB)
    )
    
    /** Classic, nostalgic theme with sepia tones. */
    object Vintage : CardTemplate(
        name = "Vintage",
        description = "Classic & Nostalgic",
        backgroundGradient = Brush.linearGradient(
            colors = listOf(
                Color(0xFF2D2014),
                Color(0xFF3D2A1A),
                Color(0xFF2D2014)
            )
        ),
        primaryColor = Color(0xFFD4A574),
        secondaryColor = Color(0xFF8B7355),
        textColor = Color(0xFFF5DEB3),
        accentColor = Color(0xFFDEB887)
    )
    
    companion object {
        fun all(): List<CardTemplate> = listOf(Brawler, Showstopper, Geneticist, Vintage)
    }
}

/**
 * Template selector for rooster cards.
 * Shows miniature previews of each template.
 */
@Composable
fun CardTemplateSelector(
    selectedTemplate: CardTemplate,
    onTemplateSelected: (CardTemplate) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val haptic = LocalHapticFeedback.current
    
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Choose a Style",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(scrollState)
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            CardTemplate.all().forEach { template ->
                TemplatePreview(
                    template = template,
                    isSelected = template == selectedTemplate,
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onTemplateSelected(template)
                    }
                )
            }
        }
    }
}

@Composable
private fun TemplatePreview(
    template: CardTemplate,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = if (isSelected) template.primaryColor else Color.Transparent
    val borderWidth = if (isSelected) 2.dp else 0.dp
    
    Card(
        onClick = onClick,
        modifier = modifier
            .width(100.dp)
            .aspectRatio(0.7f)
            .border(borderWidth, borderColor, RoundedCornerShape(12.dp))
            .pressAnimation(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 8.dp else 2.dp
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(0.7f)
                .background(template.backgroundGradient)
                .padding(8.dp)
        ) {
            Column(
                modifier = Modifier.align(Alignment.BottomStart)
            ) {
                // Mini card preview elements
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(template.primaryColor.copy(alpha = 0.3f))
                )
                
                Spacer(Modifier.height(4.dp))
                
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(6.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(template.textColor.copy(alpha = 0.7f))
                )
                
                Spacer(Modifier.height(2.dp))
                
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(template.textColor.copy(alpha = 0.4f))
                )
            }
            
            // Template name badge
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .clip(RoundedCornerShape(4.dp))
                    .background(template.primaryColor.copy(alpha = 0.8f))
                    .padding(horizontal = 4.dp, vertical = 2.dp)
            ) {
                Text(
                    text = template.name,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

/**
 * Returns the emoji indicator for each template.
 */
fun CardTemplate.emoji(): String = when (this) {
    CardTemplate.Brawler -> "🔥"
    CardTemplate.Showstopper -> "✨"
    CardTemplate.Geneticist -> "🧬"
    CardTemplate.Vintage -> "📜"
}
