package com.rio.rostry.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Glassmorphic card component for premium hero sections.
 * Features a frosted glass effect with subtle border and blur.
 * 
 * Inspired by Instagram Stories and Apple's UI design language.
 */
@Composable
fun GlassmorphicCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color.White.copy(alpha = 0.1f),
    borderColor: Color = Color.White.copy(alpha = 0.2f),
    borderWidth: Dp = 1.dp,
    cornerRadius: Dp = 16.dp,
    contentPadding: Dp = 16.dp,
    content: @Composable BoxScope.() -> Unit
) {
    val shape = RoundedCornerShape(cornerRadius)
    
    Box(
        modifier = modifier
            .clip(shape)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        backgroundColor.copy(alpha = backgroundColor.alpha * 1.2f),
                        backgroundColor,
                        backgroundColor.copy(alpha = backgroundColor.alpha * 0.8f)
                    )
                )
            )
            .border(
                width = borderWidth,
                brush = Brush.linearGradient(
                    colors = listOf(
                        borderColor,
                        borderColor.copy(alpha = borderColor.alpha * 0.5f),
                        borderColor
                    )
                ),
                shape = shape
            )
            .padding(contentPadding),
        content = content
    )
}

/**
 * Glassmorphic card variant with darker background for overlay usage.
 */
@Composable
fun GlassmorphicOverlay(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 12.dp,
    contentPadding: Dp = 12.dp,
    content: @Composable BoxScope.() -> Unit
) {
    GlassmorphicCard(
        modifier = modifier,
        backgroundColor = Color.Black.copy(alpha = 0.4f),
        borderColor = Color.White.copy(alpha = 0.1f),
        cornerRadius = cornerRadius,
        contentPadding = contentPadding,
        content = content
    )
}
