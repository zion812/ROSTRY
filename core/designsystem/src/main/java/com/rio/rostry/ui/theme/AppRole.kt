package com.rio.rostry.ui.theme

/**
 * Design-system-level role abstraction.
 *
 * This decouples the theme layer from domain-specific enums like `UserType`.
 * Callers map their domain role to one of these values before passing it
 * to [ROSTRYTheme].
 */
enum class AppRole {
    GENERAL,
    FARMER,
    ENTHUSIAST
}
