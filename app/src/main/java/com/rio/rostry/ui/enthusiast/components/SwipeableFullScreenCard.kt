package com.rio.rostry.ui.enthusiast.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.rio.rostry.ui.components.DoubleTapLikeOverlay
import com.rio.rostry.ui.components.GlassmorphicOverlay
import com.rio.rostry.ui.components.wishlistHeartAnimation
import com.rio.rostry.ui.theme.Coral
import com.rio.rostry.ui.theme.EnthusiastGold
import kotlinx.coroutines.delay
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

/**
 * Featured bird data for the explore screen.
 */
data class FeaturedBird(
    val id: String,
    val name: String,
    val imageUrl: String?,
    val farmName: String,
    val breed: String,
    val location: String,
    val description: String,
    val respectCount: Int,
    val commentCount: Int,
    val isRespected: Boolean = false,
    val isVerified: Boolean = false
)

/**
 * TikTok/Instagram-style full-screen swipeable card.
 * Features:
 * - Full-screen image with gradient overlay
 * - Double-tap "Respect" interaction with floating emoji
 * - Engagement row (heart, comment, share)
 * - Swipe-up gesture for next card
 */
@Composable
fun SwipeableFullScreenCard(
    bird: FeaturedBird,
    modifier: Modifier = Modifier,
    onSwipeUp: () -> Unit = {},
    onRespect: () -> Unit = {},
    onComment: () -> Unit = {},
    onShare: () -> Unit = {},
    onFarmTap: () -> Unit = {}
) {
    var offsetY by remember { mutableFloatStateOf(0f) }
    var showRespectOverlay by remember { mutableStateOf(false) }
    var localRespected by remember(bird.isRespected) { mutableStateOf(bird.isRespected) }
    var localRespectCount by remember(bird.respectCount) { mutableStateOf(bird.respectCount) }
    val haptic = LocalHapticFeedback.current
    
    val animatedOffset by animateFloatAsState(
        targetValue = offsetY,
        animationSpec = tween(if (offsetY == 0f) 300 else 0),
        label = "cardOffset"
    )
    
    // Swipe indicator visibility
    var showSwipeHint by remember { mutableStateOf(true) }
    LaunchedEffect(Unit) {
        delay(3000)
        showSwipeHint = false
    }
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .offset { IntOffset(0, animatedOffset.roundToInt()) }
            .pointerInput(Unit) {
                detectVerticalDragGestures(
                    onDragEnd = {
                        if (offsetY < -100) {
                            onSwipeUp()
                        }
                        offsetY = 0f
                    },
                    onDragCancel = { offsetY = 0f },
                    onVerticalDrag = { _, dragAmount ->
                        offsetY = (offsetY + dragAmount).coerceIn(-size.height.toFloat() / 2, 100f)
                    }
                )
            }
            .pointerInput(Unit) {
                detectTapGestures(
                    onDoubleTap = {
                        if (!localRespected) {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            localRespected = true
                            localRespectCount++
                            showRespectOverlay = true
                            onRespect()
                        }
                    }
                )
            }
    ) {
        // Background Image
        AsyncImage(
            model = bird.imageUrl,
            contentDescription = bird.name,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        
        // Gradient overlay at bottom
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.3f),
                            Color.Black.copy(alpha = 0.7f),
                            Color.Black.copy(alpha = 0.85f)
                        )
                    )
                )
        )
        
        // Double-tap Respect overlay
        DoubleTapLikeOverlay(
            visible = showRespectOverlay,
            emoji = "👊",
            onAnimationComplete = { showRespectOverlay = false }
        )
        
        // Engagement sidebar (right side, TikTok-style)
        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 12.dp, bottom = 100.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Respect button
            EngagementButton(
                icon = if (localRespected) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                count = localRespectCount,
                isActive = localRespected,
                activeColor = Coral,
                onClick = {
                    if (!localRespected) {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        localRespected = true
                        localRespectCount++
                        onRespect()
                    }
                },
                modifier = Modifier.wishlistHeartAnimation(localRespected)
            )
            
            // Comment button
            EngagementButton(
                icon = Icons.Default.Chat,
                count = bird.commentCount,
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onComment()
                }
            )
            
            // Share button
            EngagementButton(
                icon = Icons.Default.Share,
                count = null,
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onShare()
                }
            )
        }
        
        // Bottom content overlay
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .padding(start = 16.dp, end = 80.dp, bottom = 32.dp)
        ) {
            // Farm name with verified badge
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.pointerInput(Unit) {
                    detectTapGestures(onTap = { onFarmTap() })
                }
            ) {
                Text(
                    text = bird.farmName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                if (bird.isVerified) {
                    Spacer(Modifier.width(4.dp))
                    Text(text = "✓", color = EnthusiastGold, fontWeight = FontWeight.Bold)
                }
            }
            
            Spacer(Modifier.height(4.dp))
            
            // Bird name and breed
            Text(
                text = "${bird.name} • ${bird.breed}",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White.copy(alpha = 0.9f)
            )
            
            // Location
            Text(
                text = "📍 ${bird.location}",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.7f)
            )
            
            Spacer(Modifier.height(8.dp))
            
            // Description with expansion
            ExpandableDescription(text = bird.description)
        }
        
        // Swipe up hint
        AnimatedVisibility(
            visible = showSwipeHint,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut(),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 8.dp)
        ) {
            GlassmorphicOverlay(
                cornerRadius = 20.dp,
                contentPadding = 8.dp
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowUp,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "Swipe up for next",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun EngagementButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    count: Int?,
    modifier: Modifier = Modifier,
    isActive: Boolean = false,
    activeColor: Color = Coral,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        IconButton(
            onClick = onClick,
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Color.Black.copy(alpha = 0.3f))
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isActive) activeColor else Color.White,
                modifier = Modifier.size(28.dp)
            )
        }
        count?.let {
            Text(
                text = formatCount(it),
                style = MaterialTheme.typography.labelSmall,
                color = Color.White,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun ExpandableDescription(
    text: String,
    maxLines: Int = 2
) {
    var expanded by remember { mutableStateOf(false) }
    
    Column {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White.copy(alpha = 0.85f),
            maxLines = if (expanded) Int.MAX_VALUE else maxLines,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.pointerInput(Unit) {
                detectTapGestures(onTap = { expanded = !expanded })
            }
        )
        
        if (!expanded && text.length > 80) {
            Text(
                text = "more",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.6f),
                fontWeight = FontWeight.Medium
            )
        }
    }
}

private fun formatCount(count: Int): String = when {
    count >= 1_000_000 -> "${count / 1_000_000}M"
    count >= 1_000 -> "${count / 1_000}K"
    else -> count.toString()
}
