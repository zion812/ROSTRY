package com.rio.rostry.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun SkeletonLoader(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "shimmer")
    val offset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_offset"
    )

    val shimmerColors = listOf(
        Color.LightGray.copy(alpha = 0.6f),
        Color.LightGray.copy(alpha = 0.2f),
        Color.LightGray.copy(alpha = 0.6f)
    )

    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset(offset - 200f, offset - 200f),
        end = Offset(offset, offset)
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(brush)
    )
}

@Composable
fun ProductCardSkeleton(modifier: Modifier = Modifier) {
    Card(modifier = modifier) {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            SkeletonLoader(Modifier.fillMaxWidth().height(128.dp))
            SkeletonLoader(Modifier.width(100.dp).height(20.dp))
            SkeletonLoader(Modifier.width(150.dp).height(16.dp))
            SkeletonLoader(Modifier.width(80.dp).height(24.dp))
        }
    }
}

@Composable
fun ListItemSkeleton(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SkeletonLoader(Modifier.size(48.dp))
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            SkeletonLoader(Modifier.width(120.dp).height(16.dp))
            SkeletonLoader(Modifier.width(180.dp).height(14.dp))
        }
    }
}

@Composable
fun MetricCardSkeleton(modifier: Modifier = Modifier) {
    Card(modifier = modifier) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            SkeletonLoader(Modifier.width(80.dp).height(14.dp))
            SkeletonLoader(Modifier.width(120.dp).height(24.dp))
            SkeletonLoader(Modifier.width(100.dp).height(12.dp))
        }
    }
}

@Composable
fun PostCardSkeleton(modifier: Modifier = Modifier) {
    Card(modifier = modifier) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SkeletonLoader(Modifier.size(40.dp))
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    SkeletonLoader(Modifier.width(100.dp).height(14.dp))
                    SkeletonLoader(Modifier.width(80.dp).height(12.dp))
                }
            }
            SkeletonLoader(Modifier.fillMaxWidth().height(16.dp))
            SkeletonLoader(Modifier.fillMaxWidth().height(14.dp))
            SkeletonLoader(Modifier.width(200.dp).height(14.dp))
            SkeletonLoader(Modifier.fillMaxWidth().height(200.dp))
        }
    }
}

@Composable
fun FormFieldSkeleton(modifier: Modifier = Modifier) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        SkeletonLoader(Modifier.width(80.dp).height(14.dp))
        SkeletonLoader(Modifier.fillMaxWidth().height(56.dp))
    }
}

@Composable
fun LoadingOverlay(
    isLoading: Boolean,
    message: String? = null,
    modifier: Modifier = Modifier
) {
    if (isLoading) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center
        ) {
            Card {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    CircularProgressIndicator()
                    message?.let {
                        Text(it, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyState(
    icon: ImageVector,
    title: String,
    description: String,
    primaryAction: Pair<String, () -> Unit>? = null,
    secondaryAction: Pair<String, () -> Unit>? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(24.dp))
        primaryAction?.let { (label, action) ->
            Button(onClick = action) {
                Text(label)
            }
        }
        secondaryAction?.let { (label, action) ->
            Spacer(Modifier.height(8.dp))
            TextButton(onClick = action) {
                Text(label)
            }
        }
    }
}

@Composable
fun ErrorState(
    error: String,
    retryAction: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "⚠️",
            style = MaterialTheme.typography.displayLarge
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = "Something went wrong",
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = error,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.error
        )
        retryAction?.let { action ->
            Spacer(Modifier.height(24.dp))
            Button(onClick = action) {
                Text("Retry")
            }
        }
    }
}
