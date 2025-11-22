package com.rio.rostry.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

// Simplified version - TODO: Implement full version when Compose APIs stabilize
fun Modifier.hapticClick(onClick: () -> Unit): Modifier {
    return this.clickable(onClick = onClick)
}

fun Modifier.hapticPressAnimation(onClick: () -> Unit): Modifier {
    return this.clickable(onClick = onClick)
}

fun Modifier.pressAnimation(): Modifier {
    return this
}

fun Modifier.pulse(enabled: Boolean = true): Modifier {
    return this
}

@Composable
fun FadeScaleVisibility(
    visible: Boolean,
    content: @Composable () -> Unit
) {
    if (visible) {
        content()
    }
}

@Composable
fun SlideUpFadeVisibility(
    visible: Boolean,
    content: @Composable () -> Unit
) {
    if (visible) {
        content()
    }
}
