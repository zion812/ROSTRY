// This file is deprecated and replaced by AppNavigation.kt
// It is kept temporarily for reference but should be removed in future versions
package com.rio.rostry.navigation

// Placeholder objects to prevent compilation errors if there are any remaining references
// These will be removed when all references are updated to use AppNavigation.kt

object MarketplaceScreen
object AddEditFowlScreen {
    data class Add(val fowlId: String? = null)
}
object FowlDetailScreen {
    data class Detail(val fowlId: String)
}
object TransferScreen