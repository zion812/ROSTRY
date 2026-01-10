package com.rio.rostry.accessibility

import android.content.Context
import android.view.View
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Enhanced accessibility manager providing comprehensive accessibility utilities
 * for the ROSTRY app. Supports screen reader detection, announcements, focus
 * management, and Compose integration.
 * 
 * WCAG 2.1 AA Compliance Support:
 * - Screen reader (TalkBack) detection
 * - Accessibility announcements for dynamic content
 * - Touch exploration detection
 * - Reduced motion preferences
 * - Text scaling support
 */
@Singleton
class EnhancedAccessibilityManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val accessibilityManager: AccessibilityManager? = 
        context.getSystemService(Context.ACCESSIBILITY_SERVICE) as? AccessibilityManager

    // User preferences
    var reduceMotion: Boolean = false
    var textScale: Float = 1f

    /**
     * Check if a screen reader (TalkBack) is currently enabled.
     * Used to provide enhanced descriptions and navigation.
     */
    val isScreenReaderEnabled: Boolean
        get() = accessibilityManager?.isEnabled == true &&
                accessibilityManager.isTouchExplorationEnabled

    /**
     * Check if touch exploration mode is active.
     * When enabled, users navigate by touch and need spoken feedback.
     */
    val isTouchExplorationEnabled: Boolean
        get() = accessibilityManager?.isTouchExplorationEnabled == true

    /**
     * Check if Switch Access or similar accessibility service is enabled.
     */
    val isSwitchAccessEnabled: Boolean
        get() = accessibilityManager?.isEnabled == true &&
                !accessibilityManager.isTouchExplorationEnabled

    /**
     * Check if any accessibility service is currently enabled.
     */
    fun isAccessibilityEnabled(): Boolean {
        return accessibilityManager?.isEnabled == true
    }

    /**
     * Announce a message for accessibility services (TalkBack).
     * Use for dynamic content changes that screen readers should announce.
     * 
     * @param message The message to announce
     * @param isPolite If true, waits for current speech to finish. If false, interrupts.
     */
    fun announceForAccessibility(message: String, isPolite: Boolean = true) {
        if (!isAccessibilityEnabled()) return
        
        val event = AccessibilityEvent.obtain().apply {
            eventType = if (isPolite) {
                AccessibilityEvent.TYPE_ANNOUNCEMENT
            } else {
                AccessibilityEvent.TYPE_VIEW_FOCUSED
            }
            className = EnhancedAccessibilityManager::class.java.name
            packageName = context.packageName
            text.add(message)
        }
        
        accessibilityManager?.sendAccessibilityEvent(event)
    }

    /**
     * Request accessibility focus on a specific view.
     * Use when important content appears that should be immediately announced.
     * 
     * @param view The view to focus
     */
    fun setAccessibilityFocus(view: View) {
        if (!isAccessibilityEnabled()) return
        view.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_FOCUSED)
    }

    /**
     * Get recommended animation duration based on accessibility settings.
     * Returns 0 if reduce motion is enabled.
     * 
     * @param defaultDuration The default animation duration in milliseconds
     */
    fun getAnimationDuration(defaultDuration: Long): Long {
        return if (reduceMotion) 0L else defaultDuration
    }

    /**
     * Get scaled text size based on user preferences.
     * 
     * @param baseSizeSp The base text size in SP
     */
    fun getScaledTextSize(baseSizeSp: Float): Float {
        return baseSizeSp * textScale
    }

    /**
     * Build a content description with context.
     * Appends state information for interactive elements.
     * 
     * @param label The base label
     * @param state Optional state description (e.g., "selected", "disabled")
     * @param hint Optional action hint (e.g., "Double tap to activate")
     */
    fun buildContentDescription(
        label: String,
        state: String? = null,
        hint: String? = null
    ): String {
        return buildString {
            append(label)
            state?.let { append(", $it") }
            hint?.let { append(". $it") }
        }
    }

    companion object {
        // Content description templates
        const val TEMPLATE_BUTTON = "%s button"
        const val TEMPLATE_BUTTON_WITH_COUNT = "%s button, %d items"
        const val TEMPLATE_INCREASE = "Increase %s"
        const val TEMPLATE_DECREASE = "Decrease %s"
        const val TEMPLATE_REMOVE = "Remove %s"
        const val TEMPLATE_TOGGLE = "%s, %s" // label, on/off
        const val TEMPLATE_HEADING = "%s, heading"
        
        // Accessibility action hints
        const val HINT_DOUBLE_TAP = "Double tap to activate"
        const val HINT_DOUBLE_TAP_HOLD = "Double tap and hold for more options"
        const val HINT_SWIPE = "Swipe left or right to adjust"
    }
}

/**
 * CompositionLocal for accessing EnhancedAccessibilityManager in Compose.
 */
val LocalAccessibilityManager = compositionLocalOf<EnhancedAccessibilityManager?> { null }

/**
 * Provider composable for EnhancedAccessibilityManager.
 * Wrap your app content with this to enable accessibility utilities.
 */
@Composable
fun ProvideAccessibilityManager(
    manager: EnhancedAccessibilityManager,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(LocalAccessibilityManager provides manager) {
        content()
    }
}

/**
 * Composable helper to get the current accessibility manager.
 * Returns null if not provided.
 */
@Composable
fun rememberAccessibilityManager(): EnhancedAccessibilityManager? {
    return LocalAccessibilityManager.current
}

/**
 * Extension function to create accessible content description for quantity controls.
 */
fun EnhancedAccessibilityManager.quantityControlDescription(
    productName: String,
    action: QuantityAction,
    currentQuantity: Int? = null
): String {
    val actionText = when (action) {
        QuantityAction.INCREASE -> String.format(TEMPLATE_INCREASE, productName)
        QuantityAction.DECREASE -> String.format(TEMPLATE_DECREASE, productName)
        QuantityAction.REMOVE -> String.format(TEMPLATE_REMOVE, productName)
    }
    return if (currentQuantity != null && action != QuantityAction.REMOVE) {
        "$actionText, current quantity $currentQuantity"
    } else {
        actionText
    }
}

enum class QuantityAction {
    INCREASE, DECREASE, REMOVE
}

// Template constants for use outside the class
private const val TEMPLATE_INCREASE = "Increase quantity of %s"
private const val TEMPLATE_DECREASE = "Decrease quantity of %s"
private const val TEMPLATE_REMOVE = "Remove %s from cart"
