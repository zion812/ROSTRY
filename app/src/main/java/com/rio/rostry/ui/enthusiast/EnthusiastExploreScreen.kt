package com.rio.rostry.ui.enthusiast

import androidx.compose.runtime.Composable

@Composable
fun EnthusiastExploreScreen(
    onOpenProduct: (String) -> Unit,
    onOpenEvent: (String) -> Unit,
    onShare: (String) -> Unit,
    onNavigateBack: () -> Unit = {}
) {
    // Delegate to the tabbed implementation
    EnthusiastExploreTabs(
        onOpenProduct = onOpenProduct,
        onOpenEvent = onOpenEvent,
        onShare = onShare
    )
}
