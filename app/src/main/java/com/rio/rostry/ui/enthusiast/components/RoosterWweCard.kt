package com.rio.rostry.ui.enthusiast.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.rio.rostry.ui.theme.EnthusiastGold
import com.rio.rostry.ui.theme.EnthusiastGoldVariant
import com.rio.rostry.ui.theme.EnthusiastObsidian
import com.rio.rostry.ui.theme.EnthusiastVelvet

/**
 * A WWE-style trading card for a rooster, designed to be captured as an image for sharing.
 * Aspect Ratio: 9:16 (Story format)
 */
@Composable
fun RoosterWweCard(
    name: String,
    breed: String,
    imageUrl: String?,
    wins: Int,
    draws: Int,
    losses: Int,
    aggression: Float, // 0.0 - 1.0
    stamina: Float,    // 0.0 - 1.0
    power: Float,      // 0.0 - 1.0
    modifier: Modifier = Modifier
) {
    // Card Container
    Box(
        modifier = modifier
            .aspectRatio(9f / 16f)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        EnthusiastObsidian,
                        Color(0xFF2D1B4D), // Deep Purple
                        EnthusiastObsidian
                    )
                )
            )
            .border(
                width = 4.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        EnthusiastGold,
                        EnthusiastGoldVariant
                    )
                ),
                shape = androidx.compose.ui.graphics.RectangleShape // Box shape default is rectangle
            )
    ) {
        // --- Layer 1: Background Effects (Subtle Patterns) ---
        // (Simplified for MVP)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // --- Header: Branding ---
            Text(
                text = "ROSTRY",
                style = MaterialTheme.typography.labelMedium.copy(
                    letterSpacing = 4.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = EnthusiastGold.copy(alpha = 0.7f)
            )
            
            Spacer(modifier = Modifier.height(16.dp))

            // --- Hero Image ---
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .border(2.dp, EnthusiastGold, RoundedCornerShape(16.dp))
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.Black.copy(alpha = 0.3f))
            ) {
                if (!imageUrl.isNullOrBlank()) {
                    Image(
                        painter = rememberAsyncImagePainter(imageUrl),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    // Placeholder
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = null,
                        modifier = Modifier
                            .size(64.dp)
                            .align(Alignment.Center),
                        tint = EnthusiastGold.copy(alpha = 0.5f)
                    )
                }
                
                // Breed Badge Overlay
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 16.dp)
                        .background(EnthusiastVelvet.copy(alpha = 0.9f), RoundedCornerShape(50))
                        .border(1.dp, EnthusiastGold, RoundedCornerShape(50))
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = breed.uppercase(),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- Name & Record ---
            Text(
                text = name.uppercase(),
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp
                ),
                color = EnthusiastGold,
                textAlign = TextAlign.Center
            )
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(top = 8.dp)
            ) {
                RecordBadge("W", wins.toString(), Color(0xFF4CAF50))
                RecordBadge("D", draws.toString(), Color(0xFFFFC107))
                RecordBadge("L", losses.toString(), Color(0xFFF44336))
            }

            Spacer(modifier = Modifier.height(32.dp))

            // --- Stats Bars ---
            StatRow("AGGRESSION", aggression, Icons.Default.Bolt)
            StatRow("STAMINA", stamina, Icons.Default.Shield)
            StatRow("POWER", power, Icons.Default.Star)

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun RecordBadge(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = Color.White
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = color
        )
    }
}

@Composable
private fun StatRow(label: String, value: Float, icon: ImageVector) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = EnthusiastGold,
            modifier = Modifier.size(20.dp)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            color = Color.White,
            modifier = Modifier
                .width(80.dp)
                .padding(start = 8.dp)
        )
        LinearProgressIndicator(
            progress = { value },
            modifier = Modifier
                .weight(1f)
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = EnthusiastGold,
            trackColor = Color.White.copy(alpha = 0.2f),
        )
        Text(
            text = "${(value * 100).toInt()}",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            color = EnthusiastGold,
            modifier = Modifier
                .width(36.dp)
                .padding(start = 8.dp),
            textAlign = TextAlign.End
        )
    }
}

@Preview
@Composable
fun PreviewRoosterCard() {
    RoosterWweCard(
        name = "King Slayer",
        breed = "Kelso",
        imageUrl = null,
        wins = 12,
        draws = 1,
        losses = 0,
        aggression = 0.95f,
        stamina = 0.85f,
        power = 0.90f
    )
}
