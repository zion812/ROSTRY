package com.rio.rostry.ui.enthusiast.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.rio.rostry.ui.components.GlassmorphicCard
import com.rio.rostry.ui.components.GradientButton
import com.rio.rostry.ui.theme.ElectricBlue
import com.rio.rostry.ui.theme.EnthusiastGold
import com.rio.rostry.ui.theme.EnthusiastGoldVariant
import com.rio.rostry.ui.theme.RostryBlue
import kotlinx.coroutines.delay
import kotlin.random.Random

/**
 * Champion data class for the hero banner.
 */
data class ChampionData(
    val id: String,
    val name: String,
    val imageUrl: String?,
    val winRate: Float,
    val totalFights: Int,
    val breed: String = ""
)

/**
 * Instagram Stories-inspired hero banner showcasing top champion bird.
 * Features:
 * - Glassmorphic container with parallax scroll effect
 * - Auto-rotation through top champions
 * - Particle glow effect around champion image
 * - Share button with gradient styling
 */
@Composable
fun HeroChampionBanner(
    champions: List<ChampionData>,
    scrollState: ScrollState? = null,
    modifier: Modifier = Modifier,
    onShareCard: (ChampionData) -> Unit = {}
) {
    if (champions.isEmpty()) return
    
    var currentIndex by remember { mutableIntStateOf(0) }
    val currentChampion = champions.getOrNull(currentIndex) ?: champions.first()
    
    // Auto-rotate every 5 seconds
    LaunchedEffect(champions.size) {
        while (champions.size > 1) {
            delay(5000)
            currentIndex = (currentIndex + 1) % champions.size
        }
    }
    
    // Parallax effect based on scroll
    val parallaxOffset = scrollState?.value?.times(0.5f) ?: 0f
    
    GlassmorphicCard(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
            .graphicsLayer {
                translationY = parallaxOffset
            },
        backgroundColor = Color.Black.copy(alpha = 0.3f),
        borderColor = EnthusiastGold.copy(alpha = 0.3f)
    ) {
        AnimatedContent(
            targetState = currentChampion,
            transitionSpec = {
                slideInHorizontally(
                    animationSpec = tween(500, easing = FastOutSlowInEasing)
                ) { it } + fadeIn(animationSpec = tween(300)) togetherWith
                slideOutHorizontally(
                    animationSpec = tween(500, easing = FastOutSlowInEasing)
                ) { -it } + fadeOut(animationSpec = tween(300))
            },
            label = "championTransition"
        ) { champion ->
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Champion Image with particle glow
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .padding(8.dp)
                ) {
                    // Particle glow effect
                    ParticleGlow(
                        modifier = Modifier.fillMaxSize(),
                        color = EnthusiastGold
                    )
                    
                    // Champion image
                    AsyncImage(
                        model = champion.imageUrl,
                        contentDescription = "Champion ${champion.name}",
                        modifier = Modifier
                            .size(100.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .align(Alignment.Center),
                        contentScale = ContentScale.Crop
                    )
                }
                
                // Stats section
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "🏆 TOP CHAMPION",
                        style = MaterialTheme.typography.labelSmall,
                        color = EnthusiastGold.copy(alpha = 0.8f),
                        letterSpacing = 1.5.sp
                    )
                    
                    Spacer(Modifier.height(4.dp))
                    
                    Text(
                        text = champion.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    
                    if (champion.breed.isNotEmpty()) {
                        Text(
                            text = champion.breed,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }
                    
                    Spacer(Modifier.height(8.dp))
                    
                    Row(
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Text(
                            text = "${(champion.winRate * 100).toInt()}%",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = EnthusiastGold
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = "Win Rate",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.6f),
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }
                    
                    Text(
                        text = "${champion.totalFights} total fights",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.5f)
                    )
                    
                    Spacer(Modifier.height(12.dp))
                    
                    GradientButton(
                        text = "Share Card",
                        icon = Icons.Default.Share,
                        onClick = { onShareCard(champion) },
                        height = 36.dp,
                        modifier = Modifier.width(130.dp)
                    )
                }
            }
        }
        
        // Page indicators for multiple champions
        if (champions.size > 1) {
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                champions.forEachIndexed { index, _ ->
                    Box(
                        modifier = Modifier
                            .size(if (index == currentIndex) 8.dp else 6.dp)
                            .clip(CircleShape)
                            .background(
                                if (index == currentIndex) EnthusiastGold
                                else Color.White.copy(alpha = 0.4f)
                            )
                    )
                }
            }
        }
    }
}

/**
 * Simple particle glow effect around an element.
 */
@Composable
private fun ParticleGlow(
    modifier: Modifier = Modifier,
    color: Color = EnthusiastGold,
    particleCount: Int = 8
) {
    val particles = remember {
        List(particleCount) {
            Triple(
                Random.nextFloat() * 360f, // angle
                0.7f + Random.nextFloat() * 0.3f, // distance multiplier
                2f + Random.nextFloat() * 3f // size
            )
        }
    }
    
    val animatedAlpha by animateFloatAsState(
        targetValue = 0.6f,
        animationSpec = tween(1000),
        label = "particleAlpha"
    )
    
    Canvas(modifier = modifier) {
        val centerX = size.width / 2
        val centerY = size.height / 2
        val radius = size.minDimension / 2
        
        particles.forEach { (angle, distMult, particleSize) ->
            val rad = Math.toRadians(angle.toDouble())
            val dist = radius * distMult
            val x = centerX + (dist * kotlin.math.cos(rad)).toFloat()
            val y = centerY + (dist * kotlin.math.sin(rad)).toFloat()
            
            drawCircle(
                color = color.copy(alpha = animatedAlpha * (0.3f + Random.nextFloat() * 0.4f)),
                radius = particleSize.dp.toPx(),
                center = androidx.compose.ui.geometry.Offset(x, y)
            )
        }
    }
}

/**
 * Compact champion banner variant for when space is limited.
 */
@Composable
fun CompactChampionBanner(
    champion: ChampionData,
    modifier: Modifier = Modifier,
    onTap: () -> Unit = {}
) {
    GlassmorphicCard(
        modifier = modifier
            .fillMaxWidth()
            .height(80.dp),
        backgroundColor = Color.Black.copy(alpha = 0.2f),
        contentPadding = 12.dp
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AsyncImage(
                model = champion.imageUrl,
                contentDescription = null,
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = champion.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
                Text(
                    text = "${(champion.winRate * 100).toInt()}% Win Rate • ${champion.totalFights} fights",
                    style = MaterialTheme.typography.bodySmall,
                    color = EnthusiastGold.copy(alpha = 0.8f)
                )
            }
            
            Text(
                text = "🏆",
                fontSize = 24.sp
            )
        }
    }
}
