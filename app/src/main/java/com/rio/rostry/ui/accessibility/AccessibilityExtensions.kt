package com.rio.rostry.ui.accessibility

import androidx.compose.foundation.layout.sizeIn
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.dp

/**
 * Accessibility Compose extensions for ROSTRY app.
 *
 * Provides:
 * - Minimum touch target sizing (48x48dp per WCAG 2.1 AA)
 * - Semantic annotations for screen readers (TalkBack)
 * - Content description helpers
 * - State announcement helpers
 * - Heading semantics for navigation
 *
 * Requirements: 25.1-25.8
 */

// ─── Touch Target ─────────────────────────────────────────────────────

/**
 * Ensures minimum 48x48dp touch target for interactive elements.
 * WCAG 2.1 Level AA requires minimum touch targets of 44x44 CSS pixels;
 * Android Material guidelines recommend 48x48dp.
 */
fun Modifier.accessibleTouchTarget(): Modifier =
    this.sizeIn(minWidth = 48.dp, minHeight = 48.dp)

/**
 * Ensures minimum touch target with explicit size for larger targets.
 */
fun Modifier.accessibleTouchTarget(minSizeDp: Int): Modifier =
    this.sizeIn(minWidth = minSizeDp.dp, minHeight = minSizeDp.dp)

// ─── Semantic Annotations ─────────────────────────────────────────────

/**
 * Adds content description for screen readers.
 * Use on images, icons, and decorative elements that carry meaning.
 */
fun Modifier.accessibleDescription(description: String): Modifier =
    this.semantics { contentDescription = description }

/**
 * Marks a composable as a heading for screen reader navigation.
 * TalkBack users can navigate between headings using gestures.
 */
fun Modifier.accessibleHeading(): Modifier =
    this.semantics { heading() }

/**
 * Adds state description for screen readers (e.g., "Selected", "Expanded").
 * Use for toggles, checkboxes, expandable sections.
 */
fun Modifier.accessibleState(state: String): Modifier =
    this.semantics { stateDescription = state }

/**
 * Combines content description with role for interactive elements.
 * E.g., a card that acts as a button should announce its role.
 */
fun Modifier.accessibleAction(description: String, actionRole: Role = Role.Button): Modifier =
    this.semantics {
        contentDescription = description
        role = actionRole
    }

// ─── Descriptions for common UI patterns ──────────────────────────────

/**
 * Standard content descriptions for common icon buttons.
 */
object AccessibilityLabels {
    const val BACK = "Navigate back"
    const val CLOSE = "Close"
    const val MENU = "Open menu"
    const val SEARCH = "Search"
    const val FILTER = "Filter"
    const val SORT = "Sort"
    const val ADD = "Add new item"
    const val DELETE = "Delete"
    const val EDIT = "Edit"
    const val SHARE = "Share"
    const val REFRESH = "Refresh"
    const val SETTINGS = "Settings"
    const val NOTIFICATIONS = "Notifications"
    const val CART = "Shopping cart"
    const val PROFILE = "Profile"
    const val FAVORITE = "Add to favorites"
    const val UNFAVORITE = "Remove from favorites"
    const val EXPAND = "Expand"
    const val COLLAPSE = "Collapse"
    const val MORE_OPTIONS = "More options"
    const val CAMERA = "Take photo"
    const val GALLERY = "Choose from gallery"
    const val LOCATION = "Set location"
    const val CALENDAR = "Select date"
    const val SEND = "Send"
    const val SAVE = "Save"
    const val CANCEL = "Cancel"
    const val RETRY = "Retry"
    const val LOADING = "Loading, please wait"
    const val ERROR = "An error occurred"

    // Product-specific
    fun productImage(name: String) = "Image of $name"
    fun productPrice(price: String) = "Price: $price"
    fun productStatus(status: String) = "Status: $status"

    // Transfer-specific
    fun transferStatus(status: String) = "Transfer status: $status"
    fun transferAction(action: String) = "Transfer action: $action"

    // Bird-specific
    fun birdAge(weeks: Int) = "$weeks weeks old"
    fun birdHealth(status: String) = "Health status: $status"
    fun birdBreed(breed: String) = "Breed: $breed"

    // Navigation
    fun tabSelected(name: String) = "$name tab, selected"
    fun tabUnselected(name: String) = "$name tab"
    fun pageCount(current: Int, total: Int) = "Page $current of $total"
}

/**
 * Color contrast utilities.
 * WCAG 2.1 Level AA requires:
 * - Normal text: 4.5:1 contrast ratio
 * - Large text (18sp+ or 14sp+ bold): 3:1 contrast ratio
 */
object ContrastChecker {
    /**
     * Calculate relative luminance of a color (0.0 to 1.0).
     * Uses the WCAG 2.1 formula.
     */
    fun relativeLuminance(r: Float, g: Float, b: Float): Double {
        fun linearize(c: Float): Double {
            val sRGB = c.toDouble()
            return if (sRGB <= 0.03928) sRGB / 12.92 else Math.pow((sRGB + 0.055) / 1.055, 2.4)
        }
        return 0.2126 * linearize(r) + 0.7152 * linearize(g) + 0.0722 * linearize(b)
    }

    /**
     * Calculate contrast ratio between two colors.
     * Returns a value from 1.0 (no contrast) to 21.0 (max contrast).
     */
    fun contrastRatio(l1: Double, l2: Double): Double {
        val lighter = maxOf(l1, l2)
        val darker = minOf(l1, l2)
        return (lighter + 0.05) / (darker + 0.05)
    }

    /**
     * Check if the contrast ratio meets WCAG AA for normal text (4.5:1).
     */
    fun meetsAA(ratio: Double): Boolean = ratio >= 4.5

    /**
     * Check if the contrast ratio meets WCAG AA for large text (3:1).
     */
    fun meetsAALargeText(ratio: Double): Boolean = ratio >= 3.0

    /**
     * Check if the contrast ratio meets WCAG AAA (7:1).
     */
    fun meetsAAA(ratio: Double): Boolean = ratio >= 7.0
}
