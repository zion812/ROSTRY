package com.rio.rostry.ui.order.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.rio.rostry.core.navigation.NavigationProvider
import com.rio.rostry.core.navigation.NavigationRoute

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
 */
class OrderNavigationProvider : NavigationProvider {
    override val featureId: String = "orders"

    override fun buildGraph(navGraphBuilder: NavGraphBuilder, navController: NavHostController) {
        navGraphBuilder.apply {
            composable(OrderRoute.MyOrders.route) {
                // TODO: Connect to OrderListScreen
            }

            composable(OrderRoute.MyOrdersFarmer.route) {
                // TODO: Connect to FarmerOrdersScreen
            }

            composable(OrderRoute.Details.route) { backStackEntry ->
                val orderId = backStackEntry.arguments?.getString("orderId") ?: ""
                // TODO: Connect to OrderDetailScreen
            }

            composable(OrderRoute.CreateEnquiry.route) { backStackEntry ->
                val productId = backStackEntry.arguments?.getString("productId") ?: ""
                val sellerId = backStackEntry.arguments?.getString("sellerId") ?: ""
                // TODO: Connect to CreateEnquiryScreen
            }

            composable(OrderRoute.QuoteNegotiation.route) { backStackEntry ->
                val quoteId = backStackEntry.arguments?.getString("quoteId") ?: ""
                // TODO: Connect to QuoteNegotiationScreen
            }

            composable(OrderRoute.QuoteNegotiationBuyer.route) { backStackEntry ->
                val quoteId = backStackEntry.arguments?.getString("quoteId") ?: ""
                // TODO: Connect to QuoteNegotiationBuyerScreen
            }

            composable(OrderRoute.QuoteNegotiationSeller.route) { backStackEntry ->
                val quoteId = backStackEntry.arguments?.getString("quoteId") ?: ""
                // TODO: Connect to QuoteNegotiationSellerScreen
            }

            composable(OrderRoute.PaymentProof.route) { backStackEntry ->
                val paymentId = backStackEntry.arguments?.getString("paymentId") ?: ""
                // TODO: Connect to PaymentProofScreen
            }

            composable(OrderRoute.DeliveryOtpBuyer.route) { backStackEntry ->
                val orderId = backStackEntry.arguments?.getString("orderId") ?: ""
                // TODO: Connect to DeliveryOtpBuyerScreen
            }

            composable(OrderRoute.DeliveryOtpSeller.route) { backStackEntry ->
                val orderId = backStackEntry.arguments?.getString("orderId") ?: ""
                // TODO: Connect to DeliveryOtpSellerScreen
            }

            composable(OrderRoute.OrderTracking.route) { backStackEntry ->
                val orderId = backStackEntry.arguments?.getString("orderId") ?: ""
                // TODO: Connect to OrderTrackingScreen
            }

            composable(OrderRoute.RaiseDispute.route) { backStackEntry ->
                val orderId = backStackEntry.arguments?.getString("orderId") ?: ""
                // TODO: Connect to RaiseDisputeScreen
            }

            composable(OrderRoute.MyDisputes.route) {
                // TODO: Connect to MyDisputesScreen
            }

            composable(OrderRoute.DisputeDetails.route) { backStackEntry ->
                val disputeId = backStackEntry.arguments?.getString("disputeId") ?: ""
                // TODO: Connect to DisputeDetailsScreen
            }

            composable(OrderRoute.PaymentVerify.route) { backStackEntry ->
                val paymentId = backStackEntry.arguments?.getString("paymentId") ?: ""
                // TODO: Connect to PaymentVerifyScreen
            }

            composable(OrderRoute.OrderReview.route) { backStackEntry ->
                val orderId = backStackEntry.arguments?.getString("orderId") ?: ""
                // TODO: Connect to OrderReviewScreen
            }

            composable(OrderRoute.SellerReviews.route) { backStackEntry ->
                val sellerId = backStackEntry.arguments?.getString("sellerId") ?: ""
                // TODO: Connect to SellerReviewsScreen
            }
        }
    }

    override fun getDeepLinks(): List<String> = listOf(
        "rostry://orders",
        "https://rostry.app/orders"
    )
}
