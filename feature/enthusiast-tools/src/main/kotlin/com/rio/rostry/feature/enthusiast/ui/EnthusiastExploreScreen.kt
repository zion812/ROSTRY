package com.rio.rostry.feature.enthusiast.ui
import com.rio.rostry.domain.monitoring.repository.ShowRecordRepository
import com.rio.rostry.domain.error.ErrorHandler

import androidx.compose.runtime.Composable

@Composable
fun EnthusiastExploreScreen(
    onOpenProduct: (String) -> Unit,
    onOpenEvent: (String) -> Unit,
    onShare: (String) -> Unit,
    onOpenGallery: () -> Unit = {},
    onNavigateBack: () -> Unit = {}
) {
    // Delegate to the tabbed implementation
    EnthusiastExploreTabs(
        onOpenProduct = onOpenProduct,
        onOpenEvent = onOpenEvent,
        onShare = onShare
    )
}
