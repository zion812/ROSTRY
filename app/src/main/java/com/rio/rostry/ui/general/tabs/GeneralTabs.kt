package com.rio.rostry.ui.general.tabs

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun GeneralExploreTab(modifier: Modifier = Modifier) {
    com.rio.rostry.ui.general.explore.GeneralExploreRoute(
        onOpenSocialFeed = { /* handled by parent, no-op here */ },
        onOpenMessages = { /* handled by parent or navigated from parent */ }
    )
}

@Composable
fun GeneralCreateTab(modifier: Modifier = Modifier) {
    com.rio.rostry.ui.general.create.GeneralCreateRoute(
        onPostCreated = { /* no-op */ }
    )
}

@Composable
fun GeneralCartTab(modifier: Modifier = Modifier) {
    com.rio.rostry.ui.general.cart.GeneralCartRoute(
        onCheckoutComplete = { /* no-op */ }
    )
}

@Composable
fun GeneralProfileTab(modifier: Modifier = Modifier) {
    com.rio.rostry.ui.general.profile.GeneralProfileRoute()
}
