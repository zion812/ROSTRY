package com.rio.rostry.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.rio.rostry.ui.order.evidence.*

/**
 * Navigation graph for Evidence-Based Order System screens.
 */
fun NavGraphBuilder.evidenceOrderNavGraph(
    navController: NavController
) {
    // My Orders - Buyer view
    composable(Routes.Order.MY_ORDERS) {
        MyOrdersScreen(
            isFarmer = false,
            onNavigateBack = { navController.popBackStack() },
            onOrderClick = { orderId ->
                navController.navigate(Routes.Order.orderTracking(orderId))
            },
            onQuoteClick = { quoteId, isBuyer ->
                navController.navigate(Routes.Order.quoteNegotiation(quoteId, isBuyer))
            },
            onPaymentVerify = { paymentId ->
                navController.navigate(Routes.Order.paymentVerify(paymentId))
            }
        )
    }

    // My Orders - Farmer/Seller view
    composable(Routes.Order.MY_ORDERS_FARMER) {
        MyOrdersScreen(
            isFarmer = true,
            onNavigateBack = { navController.popBackStack() },
            onOrderClick = { orderId ->
                navController.navigate(Routes.Order.orderTracking(orderId))
            },
            onQuoteClick = { quoteId, isBuyer ->
                navController.navigate(Routes.Order.quoteNegotiation(quoteId, isBuyer))
            },
            onPaymentVerify = { paymentId ->
                navController.navigate(Routes.Order.paymentVerify(paymentId))
            }
        )
    }

    // Create Enquiry
    composable(
        route = Routes.Order.CREATE_ENQUIRY,
        arguments = listOf(
            navArgument("productId") { type = NavType.StringType },
            navArgument("sellerId") { type = NavType.StringType }
        )
    ) { backStackEntry ->
        val productId = backStackEntry.arguments?.getString("productId") ?: ""
        val sellerId = backStackEntry.arguments?.getString("sellerId") ?: ""
        
        // In real app, fetch product details from repository
        CreateEnquiryScreen(
            productId = productId,
            productName = "Product Name", // Would come from repository
            sellerId = sellerId,
            sellerName = "Seller Name", // Would come from repository
            basePrice = 250.0, // Would come from repository
            onNavigateBack = { navController.popBackStack() },
            onEnquirySent = { quoteId ->
                navController.navigate(Routes.Order.quoteNegotiation(quoteId, true)) {
                    popUpTo(Routes.Order.CREATE_ENQUIRY) { inclusive = true }
                }
            }
        )
    }

    // Quote Negotiation - Buyer
    composable(
        route = Routes.Order.QUOTE_NEGOTIATION_BUYER,
        arguments = listOf(
            navArgument("quoteId") { type = NavType.StringType }
        )
    ) { backStackEntry ->
        val quoteId = backStackEntry.arguments?.getString("quoteId") ?: ""
        
        QuoteNegotiationScreen(
            quoteId = quoteId,
            isBuyer = true,
            onNavigateBack = { navController.popBackStack() },
            onProceedToPayment = { orderId ->
                navController.navigate(Routes.Order.orderTracking(orderId)) {
                    popUpTo(Routes.Order.QUOTE_NEGOTIATION_BUYER) { inclusive = true }
                }
            }
        )
    }

    // Quote Negotiation - Seller
    composable(
        route = Routes.Order.QUOTE_NEGOTIATION_SELLER,
        arguments = listOf(
            navArgument("quoteId") { type = NavType.StringType }
        )
    ) { backStackEntry ->
        val quoteId = backStackEntry.arguments?.getString("quoteId") ?: ""
        
        QuoteNegotiationScreen(
            quoteId = quoteId,
            isBuyer = false,
            onNavigateBack = { navController.popBackStack() },
            onProceedToPayment = { orderId ->
                navController.navigate(Routes.Order.orderTracking(orderId)) {
                    popUpTo(Routes.Order.QUOTE_NEGOTIATION_SELLER) { inclusive = true }
                }
            }
        )
    }

    // Payment Proof Upload
    composable(
        route = Routes.Order.PAYMENT_PROOF,
        arguments = listOf(
            navArgument("paymentId") { type = NavType.StringType }
        )
    ) { backStackEntry ->
        val paymentId = backStackEntry.arguments?.getString("paymentId") ?: ""
        
        PaymentProofScreen(
            paymentId = paymentId,
            onNavigateBack = { navController.popBackStack() },
            onPaymentSubmitted = { navController.popBackStack() }
        )
    }

    // Delivery OTP - Buyer view
    composable(
        route = Routes.Order.DELIVERY_OTP_BUYER,
        arguments = listOf(
            navArgument("orderId") { type = NavType.StringType }
        )
    ) { backStackEntry ->
        val orderId = backStackEntry.arguments?.getString("orderId") ?: ""
        
        DeliveryOtpScreen(
            orderId = orderId,
            isBuyer = true,
            onNavigateBack = { navController.popBackStack() },
            onDeliveryConfirmed = { navController.popBackStack() }
        )
    }

    // Delivery OTP - Seller view
    composable(
        route = Routes.Order.DELIVERY_OTP_SELLER,
        arguments = listOf(
            navArgument("orderId") { type = NavType.StringType }
        )
    ) { backStackEntry ->
        val orderId = backStackEntry.arguments?.getString("orderId") ?: ""
        
        DeliveryOtpScreen(
            orderId = orderId,
            isBuyer = false,
            onNavigateBack = { navController.popBackStack() },
            onDeliveryConfirmed = { navController.popBackStack() }
        )
    }

    // Order Tracking
    composable(
        route = Routes.Order.ORDER_TRACKING,
        arguments = listOf(
            navArgument("orderId") { type = NavType.StringType }
        )
    ) { backStackEntry ->
        val orderId = backStackEntry.arguments?.getString("orderId") ?: ""
        
        OrderTrackingScreen(
            orderId = orderId,
            onNavigateBack = { navController.popBackStack() },
            onPaymentProof = { paymentId ->
                navController.navigate(Routes.Order.paymentProof(paymentId))
            },
            onDeliveryOtp = {
                // Determine if buyer or seller based on current user
                // For now, default to buyer
                navController.navigate(Routes.Order.deliveryOtp(orderId, true))
            },
            onRaiseDispute = {
                navController.navigate(Routes.Order.raiseDispute(orderId))
            }
        )
    }

    // Raise Dispute
    composable(
        route = Routes.Order.RAISE_DISPUTE,
        arguments = listOf(
            navArgument("orderId") { type = NavType.StringType }
        )
    ) { backStackEntry ->
        val orderId = backStackEntry.arguments?.getString("orderId") ?: ""
        
        RaiseDisputeScreen(
            orderId = orderId,
            onNavigateBack = { navController.popBackStack() },
            onDisputeRaised = { navController.popBackStack() }
        )
    }

    // My Disputes
    composable(Routes.Order.MY_DISPUTES) {
        MyDisputesScreen(
            onNavigateBack = { navController.popBackStack() },
            onDisputeClick = { disputeId ->
                navController.navigate(Routes.Order.disputeDetails(disputeId))
            }
        )
    }

    // Payment Verify (Seller)
    composable(
        route = Routes.Order.PAYMENT_VERIFY,
        arguments = listOf(
            navArgument("paymentId") { type = NavType.StringType }
        )
    ) { backStackEntry ->
        val paymentId = backStackEntry.arguments?.getString("paymentId") ?: ""
        
        PaymentVerifyScreen(
            paymentId = paymentId,
            onNavigateBack = { navController.popBackStack() },
            onVerified = { navController.popBackStack() }
        )
    }

    // Order Review
    composable(
        route = Routes.Order.ORDER_REVIEW,
        arguments = listOf(
            navArgument("orderId") { type = NavType.StringType }
        )
    ) { backStackEntry ->
        val orderId = backStackEntry.arguments?.getString("orderId") ?: ""
        
        // In real app, fetch product and seller details from repository
        OrderReviewScreen(
            orderId = orderId,
            productName = "Product Name", // Would come from repository
            sellerName = "Seller Name", // Would come from repository
            onNavigateBack = { navController.popBackStack() },
            onReviewSubmitted = { 
                navController.popBackStack()
            }
        )
    }
}
