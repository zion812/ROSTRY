package com.rio.rostry.ui.accessibility

/**
 * Accessibility utilities for screen reader hints, focus order, and high-contrast toggles.
 * Pure scaffolding independent of Compose to keep it lightweight here.
 */
object AccessibilityManager {
    /** Whether to force high-contrast theme. App theming layer can consult this flag. */
    @Volatile
    var forceHighContrast: Boolean = false

    /** Optional global flag to increase touch targets. */
    @Volatile
    var largeTouchTargets: Boolean = false

    /** Compute recommended minimum touch target size in dp. */
    fun recommendedTouchTargetDp(): Int = if (largeTouchTargets) 56 else 48

    /** Provide a contentDescription fallback if none is set. */
    fun fallbackDescription(label: String?, default: String = ""): String = label?.takeIf { it.isNotBlank() } ?: default
}
