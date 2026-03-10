package com.rio.rostry.ui.marketplace.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.rio.rostry.core.navigation.NavigationProvider
import com.rio.rostry.ui.common.ErrorScreen

/**
 * Navigation provider for marketplace feature.
 * Handles orders, product browsing, cart, marketplace disputes, and auction screens.
 * 
 * Migrated from AppNavHost - Task 6.1.4
 * Total routes: 9
 * 
 * Routes included:
 * - Order.MY_ORDERS (buyer view)
 * - Order.MY_ORDERS_FARMER (seller view)
 * - Routes.ORDER_DETAILS (order tracking)
 * - Routes.PRODUCT_DETAILS (product details with deep links)
 * - Product.AUCTION (auction screen)
 * - GeneralNav.MARKET (general market browsing)
 * - GeneralNav.CART (shopping cart)
 * - Marketplace.CREATE_DISPUTE (dispute creation)
 * - Routes.PRODUCT_SANDBOX (QA/demo sandbox)
 * 
 * NOTE: This provider is in the app module because the screens haven't been migrated
 * to the feature:marketplace module yet. Once screens are migrated, this provider
 * should be moved to feature:marketplace module.
 */
class MarketplaceNavigationProvider : NavigationProvider {
    override val featureId: String = "marketplace"
    
    /**
     * Local route definitions for marketplace feature.
     */
    private object Routes {
        const val MY_ORDERS = "orders"
        const val MY_ORDERS_FARMER = "orders/farmer"
        const val ORDER_DETAILS = "order/{orderId}"
        const val PRODUCT_DETAILS = "product/{productId}"
        const val AUCTION = "auction/{auctionId}"
        const val GENERAL_MARKET = "general/market"
        const val GENERAL_CART = "general/cart"
        const val CREATE_DISPUTE = "marketplace/dispute/create/{transferId}/{reportedUserId}"
        const val PRODUCT_SANDBOX = "product/sandbox"
        
        // External routes
        const val TRACEABILITY = "traceability/{productId}"
        const val USER_PROFILE = "user/{userId}"
        const val MESSAGES_THREAD = "messages/{threadId}"
    }

    override fun buildGraph(navGraphBuilder: NavGraphBuilder, navController: NavHostController) {
        navGraphBuilder.apply {
            // ============ Order Management Routes ============
            
            // My Orders Screen - Buyer view
            composable(Routes.MY_ORDERS) {
                com.rio.rostry.ui.order.evidence.MyOrdersScreen(
                    isFarmer = false,
                    onNavigateBack = { navController.popBackStack() },
                    onOrderClick = { orderId -> navController.navigate("order/$orderId") },
                    onQuoteClick = { quoteId, isBuyer -> 
                        val role = if (isBuyer) "buyer" else "seller"
                        navController.navigate("order/quote/$quoteId/$role")
                    },
                    onPaymentVerify = { paymentId -> 
                        navController.navigate("order/payment/$paymentId/verify")
                    }
                )
            }
            
            // My Orders Screen - Farmer/Seller view
            composable(Routes.MY_ORDERS_FARMER) {
                com.rio.rostry.ui.order.evidence.MyOrdersScreen(
                    isFarmer = true,
                    onNavigateBack = { navController.popBackStack() },
                    onOrderClick = { orderId -> navController.navigate("order/$orderId") },
                    onQuoteClick = { quoteId, isBuyer -> 
                        val role = if (isBuyer) "buyer" else "seller"
                        navController.navigate("order/quote/$quoteId/$role")
                    },
                    onPaymentVerify = { paymentId -> 
                        navController.navigate("order/payment/$paymentId/verify")
                    }
                )
            }
            
            // Order Details/Tracking Screen
            composable(
                route = Routes.ORDER_DETAILS,
                arguments = listOf(navArgument("orderId") { type = NavType.StringType }),
                deepLinks = listOf(navDeepLink { uriPattern = "rostry://order/{orderId}" })
            ) { backStackEntry ->
                val orderId = backStackEntry.arguments?.getString("orderId") ?: ""
                if (orderId.isBlank()) {
                    android.util.Log.w("MarketplaceNavigationProvider", "Invalid argument for route: order details")
                    ErrorScreen(message = "Invalid order ID", onBack = { navController.popBackStack() })
                } else {
                    com.rio.rostry.ui.order.OrderTrackingScreen(
                        orderId = orderId,
                        onNavigateBack = { navController.popBackStack() },
                        onOrderCancelled = { navController.popBackStack() },
                        onRateOrder = { _ -> /* Rating handled in screen */ },
                        onContactSupport = { /* Navigate to support */ }
                    )
                }
            }
            
            // ============ Product & Marketplace Routes ============
            
            // Product Details Screen
            composable(
                route = Routes.PRODUCT_DETAILS,
                arguments = listOf(navArgument("productId") { type = NavType.StringType }),
                deepLinks = listOf(
                    navDeepLink { uriPattern = "rostry://product/{productId}" }
                )
            ) { backStackEntry ->
                val productId = backStackEntry.arguments?.getString("productId") ?: ""
                if (productId.isBlank()) {
                    android.util.Log.w("MarketplaceNavigationProvider", "Invalid argument for route: product details")
                    ErrorScreen(message = "Invalid product ID", onBack = { navController.popBackStack() })
                } else {
                    com.rio.rostry.ui.product.ProductDetailsScreen(
                        productId = productId,
                        onOpenTraceability = { navController.navigate("traceability/$productId") },
                        onOpenSellerProfile = { userId -> navController.navigate("user/$userId") },
                        onChatWithSeller = { sellerId -> navController.navigate("messages/$sellerId") },
                        onNavigateToAuction = { auctionId -> navController.navigate("auction/$auctionId") },
                        onNavigateToCart = { navController.navigate(Routes.GENERAL_CART) }
                    )
                }
            }
            
            // Auction Screen
            composable(
                route = Routes.AUCTION,
                arguments = listOf(navArgument("auctionId") { type = NavType.StringType })
            ) { backStackEntry ->
                val auctionId = backStackEntry.arguments?.getString("auctionId") ?: ""
                com.rio.rostry.ui.auction.AuctionScreen(
                    auctionId = auctionId,
                    onBack = { navController.popBackStack() }
                )
            }
            
            // General Market Screen
            composable(Routes.GENERAL_MARKET) {
                com.rio.rostry.ui.general.market.GeneralMarketRoute(
                    onOpenProductDetails = { productId -> navController.navigate("product/$productId") },
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            
            // General Cart Screen
            composable(Routes.GENERAL_CART) {
                com.rio.rostry.ui.general.cart.GeneralCartRoute(
                    onCheckoutComplete = { orderId -> navController.navigate("order/$orderId") }
                )
            }
            
            // ============ Marketplace Dispute Routes ============
            
            // Create Dispute Screen
            composable(
                route = Routes.CREATE_DISPUTE,
                arguments = listOf(
                    navArgument("transferId") { type = NavType.StringType },
                    navArgument("reportedUserId") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val transferId = backStackEntry.arguments?.getString("transferId") ?: ""
                val reportedUserId = backStackEntry.arguments?.getString("reportedUserId") ?: ""
                com.rio.rostry.feature.marketplace.ui.dispute.CreateDisputeScreen(
                    transferId = transferId,
                    reportedUserId = reportedUserId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            
            // ============ Developer/QA Routes ============
            
            // Marketplace Sandbox for QA/demo to exercise product validation and payments
            composable(Routes.PRODUCT_SANDBOX) {
                com.rio.rostry.feature.marketplace.ui.MarketplaceSandboxScreen()
            }
        }
    }

    override fun getDeepLinks(): List<String> = listOf(
        "rostry://marketplace",
        "rostry://product/*",
        "rostry://order/*",
        "https://rostry.app/marketplace",
        "https://rostry.app/product/*",
        "https://rostry.app/order/*"
    )
}
