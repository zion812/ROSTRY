package com.rio.rostry.ui.utils

import android.view.HapticFeedbackConstants
import android.view.View
import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.platform.LocalView

/**
 * Centralized haptic feedback patterns for premium UI interactions.
 * Maps semantic feedback types to Android's HapticFeedbackConstants.
 */
enum class HapticType {
    /** Light tap - for subtle selections and hovers */
    LIGHT,
    /** Medium feedback - for confirmations and toggles */
    MEDIUM,
    /** Heavy thud - for significant actions */
    HEAVY,
    /** Success pattern - for positive completions */
    SUCCESS,
    /** Warning pattern - for caution actions */
    WARNING,
    /** Error pattern - for failed actions */
    ERROR,
    /** Selection change - for tab/filter switching */
    SELECTION
}

/**
 * Maps HapticType to Android's HapticFeedbackConstants.
 */
fun HapticType.toFeedbackConstant(): Int = when (this) {
    HapticType.LIGHT -> HapticFeedbackConstants.CLOCK_TICK
    HapticType.MEDIUM -> HapticFeedbackConstants.CONTEXT_CLICK
    HapticType.HEAVY -> HapticFeedbackConstants.LONG_PRESS
    HapticType.SUCCESS -> HapticFeedbackConstants.CONFIRM
    HapticType.WARNING -> HapticFeedbackConstants.REJECT
    HapticType.ERROR -> HapticFeedbackConstants.REJECT
    HapticType.SELECTION -> HapticFeedbackConstants.KEYBOARD_TAP
}

/**
 * Performs haptic feedback on the given view.
 */
fun View.performHaptic(type: HapticType) {
    performHapticFeedback(type.toFeedbackConstant())
}

/**
 * Composable helper to get a haptic feedback trigger.
 */
@Composable
fun rememberHapticFeedback(): (HapticType) -> Unit {
    val view = LocalView.current
    return { type -> view.performHaptic(type) }
}

/**
 * Modifier extension for haptic feedback on click.
 */
fun Modifier.hapticFeedback(
    type: HapticType = HapticType.LIGHT,
    enabled: Boolean = true,
    onClick: () -> Unit
): Modifier = composed {
    val view = LocalView.current
    this.clickable(
        enabled = enabled,
        onClick = {
            if (enabled) {
                view.performHaptic(type)
            }
            onClick()
        }
    )
}
