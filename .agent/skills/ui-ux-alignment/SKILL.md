---
name: ui-ux-alignment
description: Enforces technical architecture for the UI/UX, ensuring proper component placement, edge alignment, and adherence to modern UI/UX practices (e.g., Material Design 3). Use this when scaffolding UI, reviewing UI code, or asked to improve the user experience.
---

# UI/UX Alignment Skill

This skill outlines how the Antigravity agent should approach UI development and reviews in the ROSTRY project, ensuring that the frontend technical architecture is sound, visually appealing, and highly usable.

## Execution Strategy

When asked to build a screen, review UI code, or improve the user experience, you must enforce the following standards:

### 1. Structural Architecture & Placement
*   **Logical Grouping**: Ensure related UI elements are grouped correctly using `Column`, `Row`, and `Box`. Do not overuse deep nesting; flatten the UI hierarchy where possible.
*   **Scaffold Usage**: Always use the Material 3 `Scaffold` for top-level screens to properly place the `TopAppBar`, `FloatingActionButton`, `BottomBar`, and main content area.
*   **State Separation**: Keep the UI entirely focused on rendering state. All business logic and complex derivations MUST happen in the ViewModel. The UI should only react to state changes and pass explicit user intents (clicks, typing) back to the ViewModel as events.

### 2. Edge Alignment and Spacing (The Grid System)
*   **Consistent Paddings**: Never hardcode random Dp values (e.g., `13.dp`, `17.dp`). Stick to a strict 4dp/8dp grid system (e.g., `4.dp`, `8.dp`, `16.dp`, `24.dp`, `32.dp`).
*   **Edge-to-Edge**: Utilize `WindowInsets` properly in Jetpack Compose to ensure the UI draws behind the system UI (status bar, navigation bar) where appropriate, using `statusBarsPadding()` or `navigationBarsPadding()` so content is not obscured.
*   **Alignment**: Explicitly align items within Rows and Columns using `verticalAlignment` and `horizontalArrangement`. Ensure baselines of text align perfectly.

### 3. Modern UI/UX Practices
*   **Material Design 3 (M3)**: Strictly use M3 components (`androidx.compose.material3.*`) rather than legacy Material 2 components.
*   **Theming**: Never hardcode colors. Always reference the established `MaterialTheme.colorScheme` (e.g., `MaterialTheme.colorScheme.primary`, `MaterialTheme.colorScheme.surfaceVariant`). This guarantees support for Dark Mode and dynamic theming.
*   **Typography**: Use `MaterialTheme.typography` for all text styling. Avoid manually setting font sizes and weights on individual `Text` composables unless absolutely necessary for a custom design.
*   **Feedback & Animations**:
    *   Ensure all clickable elements have visual feedback (Ripple is applied by default in Compose).
    *   Use `AnimatedVisibility` for elements that appear or disappear to provide a fluid user experience.
    *   Add loading skeletons or indeterminate progress indicators when waiting for network/database loads.

### 4. Accessibility (a11y)
*   Ensure all images and icons have meaningful `contentDescription`s (or `null` if purely decorative).
*   Enforce a minimum touch target size of `48.dp` for all clickable elements to ensure they are easy to tap on physical devices.

## When Reviewing Existing UI
If the user asks you to "align" or "fix the UI" of an existing file, look specifically for hardcoded colors, inconsistent paddings, and components that aren't properly utilizing the Compose `Modifier` system for layout weighting and alignment. Refactor them to use the consistent grid and M3 theme.
