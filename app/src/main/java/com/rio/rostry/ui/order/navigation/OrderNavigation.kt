package com.rio.rostry.ui.order.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.rio.rostry.core.navigation.NavigationProvider
import com.rio.rostry.core.navigation.NavigationRoute
import com.rio.rostry.ui.order.evidence.MyOrdersScreen
import com.rio.rostry.ui.order.evidence.CreateEnquiryScreen
import com.rio.rostry.ui.order.evidence.QuoteNegotiationScreen
import com.rio.rostry.ui.order.evidence.PaymentVerifyScreen
import com.rio.rostry.ui.order.evidence.OrderTrackingScreen
import com.rio.rostry.ui.order.evidence.OrderReviewScreen
import com.rio.rostry.ui.order.evidence.DisputeDetailScreen
import com.rio.rostry.ui.order.evidence.RaiseDisputeScreen

/**
 * Navigation routes for orders feature (Evidence-Based Order System).
 */
sealed class OrderRoute(override val route: String) : NavigationRoute {
    object MyOrders : OrderRoute("orders")
    object MyOrdersFarmer : OrderRoute("orders/farmer")
    object Details : OrderRoute("order/{orderId}") {
        fun createRoute(orderId: String) = "order/$orderId"
    }
    object CreateEnquiry : OrderRoute("order/enquiry/{productId}/{sellerId}") {
        fun createRoute(productId: String, sellerId: String) = "order/enquiry/$productId/$sellerId"
    }
    object QuoteNegotiation : OrderRoute("order/quote/{quoteId}") {
        fun createRoute(quoteId: String) = "order/quote/$quoteId"
    }
    object QuoteNegotiationBuyer : OrderRoute("order/quote/{quoteId}/buyer") {
        fun createRoute(quoteId: String) = "order/quote/$quoteId/buyer"
    }
    object QuoteNegotiationSeller : OrderRoute("order/quote/{quoteId}/seller") {
        fun createRoute(quoteId: String) = "order/quote/$quoteId/seller"
    }
    object PaymentProof : OrderRoute("order/payment/{paymentId}/proof") {
        fun createRoute(paymentId: String) = "order/payment/$paymentId/proof"
    }
    object DeliveryOtpBuyer : OrderRoute("order/{orderId}/delivery/otp/buyer") {
        fun createRoute(orderId: String) = "order/$orderId/delivery/otp/buyer"
    }
    object DeliveryOtpSeller : OrderRoute("order/{orderId}/delivery/otp/seller") {
        fun createRoute(orderId: String) = "order/$orderId/delivery/otp/seller"
    }
    object OrderTracking : OrderRoute("order/{orderId}/tracking") {
        fun createRoute(orderId: String) = "order/$orderId/tracking"
    }
    object RaiseDispute : OrderRoute("order/{orderId}/dispute") {
        fun createRoute(orderId: String) = "order/$orderId/dispute"
    }
    object MyDisputes : OrderRoute("orders/disputes")
    object DisputeDetails : OrderRoute("dispute/{disputeId}") {
        fun createRoute(disputeId: String) = "dispute/$disputeId"
    }
    object PaymentVerify : OrderRoute("order/payment/{paymentId}/verify") {
        fun createRoute(paymentId: String) = "order/payment/$paymentId/verify"
    }
    object OrderReview : OrderRoute("order/{orderId}/review") {
        fun createRoute(orderId: String) = "order/$orderId/review"
    }
    object SellerReviews : OrderRoute("seller/{sellerId}/reviews") {
        fun createRoute(sellerId: String) = "seller/$sellerId/reviews"
    }
}

/**
 * Navigation provider for orders feature.
 * 
 * Connects all evidence-based order system screens with proper navigation callbacks.
 */
class OrderNavigationProvider : NavigationProvider {
    override val featureId: String = "orders"

    override fun buildGraph(navGraphBuilder: NavGraphBuilder, navController: NavHostController) {
        navGraphBuilder.apply {
            composable(OrderRoute.MyOrders.route) {
                MyOrdersScreen(
                    isFarmer = false,
                    onNavigateBack = { navController.popBackStack() },
                    onOrderClick = { orderId ->
                        navController.navigate(OrderRoute.Details.createRoute(orderId))
                    },
                    onQuoteClick = { quoteId, isBuyer ->
                        if (isBuyer) {
                            navController.navigate(OrderRoute.QuoteNegotiationBuyer.createRoute(quoteId))
                        } else {
                            navController.navigate(OrderRoute.QuoteNegotiationSeller.createRoute(quoteId))
                        }
                    },
                    onPaymentVerify = { paymentId ->
                        navController.navigate(OrderRoute.PaymentVerify.createRoute(paymentId))
                    }
                )
            }

            composable(OrderRoute.MyOrdersFarmer.route) {
                MyOrdersScreen(
                    isFarmer = true,
                    onNavigateBack = { navController.popBackStack() },
                    onOrderClick = { orderId ->
                        navController.navigate(OrderRoute.Details.createRoute(orderId))
                    },
                    onQuoteClick = { quoteId, isBuyer ->
                        navController.navigate(OrderRoute.QuoteNegotiationSeller.createRoute(quoteId))
                    },
                    onPaymentVerify = { paymentId ->
                        navController.navigate(OrderRoute.PaymentVerify.createRoute(paymentId))
                    }
                )
            }

            composable(OrderRoute.Details.route) { backStackEntry ->
                val orderId = backStackEntry.arguments?.getString("orderId") ?: ""
                // Order details - using tracking screen as detail view
                OrderTrackingScreen(
                    orderId = orderId,
                    onNavigateBack = { navController.popBackStack() },
                    onRaiseDispute = {
                        navController.navigate(OrderRoute.RaiseDispute.createRoute(orderId))
                    }
                )
            }

            composable(OrderRoute.CreateEnquiry.route) { backStackEntry ->
                val productId = backStackEntry.arguments?.getString("productId") ?: ""
                val sellerId = backStackEntry.arguments?.getString("sellerId") ?: ""
                CreateEnquiryScreen(
                    productId = productId,
                    sellerId = sellerId,
                    onNavigateBack = { navController.popBackStack() },
                    onEnquiryCreated = { quoteId ->
                        navController.navigate(OrderRoute.QuoteNegotiation.createRoute(quoteId)) {
                            popUpTo(OrderRoute.MyOrders.route)
                        }
                    }
                )
            }

            composable(OrderRoute.QuoteNegotiation.route) { backStackEntry ->
                val quoteId = backStackEntry.arguments?.getString("quoteId") ?: ""
                QuoteNegotiationScreen(
                    quoteId = quoteId,
                    onNavigateBack = { navController.popBackStack() },
                    onAgreementLocked = {
                        navController.popBackStack(OrderRoute.MyOrders.route, false)
                    }
                )
            }

            composable(OrderRoute.QuoteNegotiationBuyer.route) { backStackEntry ->
                val quoteId = backStackEntry.arguments?.getString("quoteId") ?: ""
                // Buyer-specific quote negotiation view
                QuoteNegotiationScreen(
                    quoteId = quoteId,
                    isBuyerView = true,
                    onNavigateBack = { navController.popBackStack() },
                    onAgreementLocked = {
                        navController.popBackStack(OrderRoute.MyOrders.route, false)
                    }
                )
            }

            composable(OrderRoute.QuoteNegotiationSeller.route) { backStackEntry ->
                val quoteId = backStackEntry.arguments?.getString("quoteId") ?: ""
                // Seller-specific quote negotiation view
                QuoteNegotiationScreen(
                    quoteId = quoteId,
                    isBuyerView = false,
                    onNavigateBack = { navController.popBackStack() },
                    onAgreementLocked = {
                        navController.popBackStack(OrderRoute.MyOrdersFarmer.route, false)
                    }
                )
            }

            composable(OrderRoute.PaymentProof.route) { backStackEntry ->
                val paymentId = backStackEntry.arguments?.getString("paymentId") ?: ""
                // Payment proof upload screen - placeholder
                // TODO: Implement PaymentProofScreen
                androidx.compose.material3.Text("Payment Proof Upload - Coming Soon")
            }

            composable(OrderRoute.DeliveryOtpBuyer.route) { backStackEntry ->
                val orderId = backStackEntry.arguments?.getString("orderId") ?: ""
                // Delivery OTP generation for buyer - placeholder
                // TODO: Implement DeliveryOtpBuyerScreen
                androidx.compose.material3.Text("Generate Delivery OTP - Coming Soon")
            }

            composable(OrderRoute.DeliveryOtpSeller.route) { backStackEntry ->
                val orderId = backStackEntry.arguments?.getString("orderId") ?: ""
                // Delivery OTP verification for seller - placeholder
                // TODO: Implement DeliveryOtpSellerScreen
                androidx.compose.material3.Text("Verify Delivery OTP - Coming Soon")
            }

            composable(OrderRoute.OrderTracking.route) { backStackEntry ->
                val orderId = backStackEntry.arguments?.getString("orderId") ?: ""
                OrderTrackingScreen(
                    orderId = orderId,
                    onNavigateBack = { navController.popBackStack() },
                    onRaiseDispute = {
                        navController.navigate(OrderRoute.RaiseDispute.createRoute(orderId))
                    }
                )
            }

            composable(OrderRoute.RaiseDispute.route) { backStackEntry ->
                val orderId = backStackEntry.arguments?.getString("orderId") ?: ""
                RaiseDisputeScreen(
                    orderId = orderId,
                    onNavigateBack = { navController.popBackStack() },
                    onDisputeRaised = { disputeId ->
                        navController.navigate(OrderRoute.DisputeDetails.createRoute(disputeId)) {
                            popUpTo(OrderRoute.MyOrders.route)
                        }
                    }
                )
            }

            composable(OrderRoute.MyDisputes.route) {
                // My disputes list - placeholder
                // TODO: Implement MyDisputesScreen showing user's disputes
                androidx.compose.material3.Text("My Disputes - Coming Soon")
            }

            composable(OrderRoute.DisputeDetails.route) { backStackEntry ->
                val disputeId = backStackEntry.arguments?.getString("disputeId") ?: ""
                DisputeDetailScreen(
                    disputeId = disputeId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(OrderRoute.PaymentVerify.route) { backStackEntry ->
                val paymentId = backStackEntry.arguments?.getString("paymentId") ?: ""
                PaymentVerifyScreen(
                    paymentId = paymentId,
                    onNavigateBack = { navController.popBackStack() },
                    onVerified = {
                        navController.popBackStack(OrderRoute.MyOrders.route, false)
                    }
                )
            }

            composable(OrderRoute.OrderReview.route) { backStackEntry ->
                val orderId = backStackEntry.arguments?.getString("orderId") ?: ""
                OrderReviewScreen(
                    orderId = orderId,
                    onNavigateBack = { navController.popBackStack() },
                    onReviewSubmitted = {
                        navController.popBackStack(OrderRoute.MyOrders.route, false)
                    }
                )
            }

            composable(OrderRoute.SellerReviews.route) { backStackEntry ->
                val sellerId = backStackEntry.arguments?.getString("sellerId") ?: ""
                // Seller reviews - placeholder
                // TODO: Implement SellerReviewsScreen showing seller's reviews and ratings
                androidx.compose.material3.Text("Seller Reviews for: $sellerId - Coming Soon")
            }
        }
    }

    override fun getDeepLinks(): List<String> = listOf(
        "rostry://orders",
        "rostry://order/{orderId}",
        "rostry://order/{orderId}/tracking",
        "rostry://order/{orderId}/dispute",
        "rostry://order/quote/{quoteId}",
        "https://rostry.app/orders"
    )
}
