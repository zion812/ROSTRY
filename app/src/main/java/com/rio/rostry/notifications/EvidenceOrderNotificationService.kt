package com.rio.rostry.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.rio.rostry.R
import com.rio.rostry.domain.model.EvidenceOrderStatus
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Service for sending Evidence-Based Order System notifications.
 * Handles all order-related push notifications.
 */
@Singleton
class EvidenceOrderNotificationService @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        const val CHANNEL_ID_ORDER = "evidence_order_channel"
        const val CHANNEL_NAME_ORDER = "Order Updates"
        const val CHANNEL_DESC_ORDER = "Notifications for order status updates, payments, and deliveries"
        
        const val CHANNEL_ID_PAYMENT = "evidence_payment_channel"
        const val CHANNEL_NAME_PAYMENT = "Payment Notifications"
        const val CHANNEL_DESC_PAYMENT = "Payment reminders and verification alerts"
        
        const val CHANNEL_ID_DELIVERY = "evidence_delivery_channel"
        const val CHANNEL_NAME_DELIVERY = "Delivery Notifications"
        const val CHANNEL_DESC_DELIVERY = "Delivery OTP and confirmation alerts"
        
        const val NOTIFICATION_ID_BASE = 5000
    }
    
    init {
        createNotificationChannels()
    }
    
    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            
            // Order updates channel
            val orderChannel = NotificationChannel(
                CHANNEL_ID_ORDER,
                CHANNEL_NAME_ORDER,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = CHANNEL_DESC_ORDER
            }
            
            // Payment channel - higher importance
            val paymentChannel = NotificationChannel(
                CHANNEL_ID_PAYMENT,
                CHANNEL_NAME_PAYMENT,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = CHANNEL_DESC_PAYMENT
            }
            
            // Delivery channel - high importance
            val deliveryChannel = NotificationChannel(
                CHANNEL_ID_DELIVERY,
                CHANNEL_NAME_DELIVERY,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = CHANNEL_DESC_DELIVERY
            }
            
            notificationManager.createNotificationChannels(
                listOf(orderChannel, paymentChannel, deliveryChannel)
            )
            
            Timber.d("EvidenceOrderNotificationService: Created notification channels")
        }
    }
    
    /**
     * Notify about a new enquiry received (for seller).
     */
    fun notifyNewEnquiry(
        orderId: String,
        productName: String,
        quantity: Int,
        unit: String,
        buyerName: String?
    ) {
        val title = "New Order Enquiry!"
        val message = "${buyerName ?: "A buyer"} wants to buy $quantity $unit of $productName"
        
        showNotification(
            notificationId = NOTIFICATION_ID_BASE + orderId.hashCode(),
            channelId = CHANNEL_ID_ORDER,
            title = title,
            message = message,
            orderId = orderId,
            icon = R.mipmap.ic_launcher
        )
    }
    
    /**
     * Notify about a quote received (for buyer).
     */
    fun notifyQuoteReceived(
        orderId: String,
        productName: String,
        totalAmount: Double,
        sellerName: String?
    ) {
        val title = "Quote Received"
        val message = "${sellerName ?: "Seller"} quoted ₹${totalAmount.toInt()} for $productName"
        
        showNotification(
            notificationId = NOTIFICATION_ID_BASE + orderId.hashCode() + 1,
            channelId = CHANNEL_ID_ORDER,
            title = title,
            message = message,
            orderId = orderId,
            icon = R.mipmap.ic_launcher
        )
    }
    
    /**
     * Notify about counter offer.
     */
    fun notifyCounterOffer(
        orderId: String,
        productName: String,
        newAmount: Double,
        fromBuyer: Boolean
    ) {
        val title = "Counter Offer"
        val message = "${if (fromBuyer) "Buyer" else "Seller"} proposed ₹${newAmount.toInt()} for $productName"
        
        showNotification(
            notificationId = NOTIFICATION_ID_BASE + orderId.hashCode() + 2,
            channelId = CHANNEL_ID_ORDER,
            title = title,
            message = message,
            orderId = orderId,
            icon = R.mipmap.ic_launcher
        )
    }
    
    /**
     * Notify when agreement is locked.
     */
    fun notifyAgreementLocked(
        orderId: String,
        productName: String,
        totalAmount: Double
    ) {
        val title = "Price Locked! 🔒"
        val message = "$productName at ₹${totalAmount.toInt()} - Agreement confirmed by both parties"
        
        showNotification(
            notificationId = NOTIFICATION_ID_BASE + orderId.hashCode() + 3,
            channelId = CHANNEL_ID_ORDER,
            title = title,
            message = message,
            orderId = orderId,
            icon = R.mipmap.ic_launcher
        )
    }
    
    /**
     * Notify about payment reminder.
     */
    fun notifyPaymentReminder(
        orderId: String,
        amount: Double,
        hoursRemaining: Int?
    ) {
        val title = "Payment Pending"
        val timeText = if (hoursRemaining != null) " (${hoursRemaining}h remaining)" else ""
        val message = "Complete your payment of ₹${amount.toInt()}$timeText"
        
        showNotification(
            notificationId = NOTIFICATION_ID_BASE + orderId.hashCode() + 10,
            channelId = CHANNEL_ID_PAYMENT,
            title = title,
            message = message,
            orderId = orderId,
            icon = R.mipmap.ic_launcher
        )
    }
    
    /**
     * Notify seller about payment proof submitted.
     */
    fun notifyPaymentProofSubmitted(
        orderId: String,
        amount: Double,
        buyerName: String?
    ) {
        val title = "Payment Proof Received"
        val message = "${buyerName ?: "Buyer"} submitted payment proof for ₹${amount.toInt()}. Please verify."
        
        showNotification(
            notificationId = NOTIFICATION_ID_BASE + orderId.hashCode() + 11,
            channelId = CHANNEL_ID_PAYMENT,
            title = title,
            message = message,
            orderId = orderId,
            icon = R.mipmap.ic_launcher
        )
    }
    
    /**
     * Notify buyer about payment verification.
     */
    fun notifyPaymentVerified(
        orderId: String,
        amount: Double,
        approved: Boolean
    ) {
        val title = if (approved) "Payment Verified ✓" else "Payment Rejected"
        val message = if (approved) 
            "Your payment of ₹${amount.toInt()} has been confirmed" 
            else "Your payment proof was not accepted. Please resubmit."
        
        showNotification(
            notificationId = NOTIFICATION_ID_BASE + orderId.hashCode() + 12,
            channelId = CHANNEL_ID_PAYMENT,
            title = title,
            message = message,
            orderId = orderId,
            icon = R.mipmap.ic_launcher
        )
    }
    
    /**
     * Notify about order dispatched.
     */
    fun notifyOrderDispatched(
        orderId: String,
        productName: String,
        estimatedDelivery: String?
    ) {
        val title = "Order Dispatched! 🚚"
        val message = if (estimatedDelivery != null)
            "$productName is on its way. Expected: $estimatedDelivery"
            else "$productName has been dispatched"
        
        showNotification(
            notificationId = NOTIFICATION_ID_BASE + orderId.hashCode() + 20,
            channelId = CHANNEL_ID_DELIVERY,
            title = title,
            message = message,
            orderId = orderId,
            icon = R.mipmap.ic_launcher
        )
    }
    
    /**
     * Notify about order ready for pickup.
     */
    fun notifyReadyForPickup(
        orderId: String,
        productName: String,
        location: String?
    ) {
        val title = "Ready for Pickup! 📦"
        val message = if (location != null)
            "$productName is ready at $location"
            else "$productName is ready for collection"
        
        showNotification(
            notificationId = NOTIFICATION_ID_BASE + orderId.hashCode() + 21,
            channelId = CHANNEL_ID_DELIVERY,
            title = title,
            message = message,
            orderId = orderId,
            icon = R.mipmap.ic_launcher
        )
    }
    
    /**
     * Notify buyer about delivery OTP.
     */
    fun notifyDeliveryOtpGenerated(
        orderId: String,
        otp: String
    ) {
        val title = "Your Delivery OTP"
        val message = "Share OTP $otp with the seller when you receive your order"
        
        showNotification(
            notificationId = NOTIFICATION_ID_BASE + orderId.hashCode() + 22,
            channelId = CHANNEL_ID_DELIVERY,
            title = title,
            message = message,
            orderId = orderId,
            icon = R.mipmap.ic_launcher
        )
    }
    
    /**
     * Notify about order delivered.
     */
    fun notifyOrderDelivered(
        orderId: String,
        productName: String
    ) {
        val title = "Order Delivered! ✅"
        val message = "$productName has been successfully delivered. Rate your experience!"
        
        showNotification(
            notificationId = NOTIFICATION_ID_BASE + orderId.hashCode() + 23,
            channelId = CHANNEL_ID_DELIVERY,
            title = title,
            message = message,
            orderId = orderId,
            icon = R.mipmap.ic_launcher
        )
    }
    
    /**
     * Notify about dispute raised.
     */
    fun notifyDisputeRaised(
        orderId: String,
        reason: String,
        raisedByOther: Boolean
    ) {
        val title = if (raisedByOther) "Dispute Raised" else "Dispute Submitted"
        val message = if (raisedByOther)
            "A dispute has been raised on order #${orderId.takeLast(6)}: $reason"
            else "Your dispute has been submitted. We'll review within 3 days."
        
        showNotification(
            notificationId = NOTIFICATION_ID_BASE + orderId.hashCode() + 30,
            channelId = CHANNEL_ID_ORDER,
            title = title,
            message = message,
            orderId = orderId,
            icon = R.mipmap.ic_launcher
        )
    }
    
    /**
     * Notify about dispute resolution.
     */
    fun notifyDisputeResolved(
        orderId: String,
        resolution: String
    ) {
        val title = "Dispute Resolved"
        val message = "Your dispute has been resolved: $resolution"
        
        showNotification(
            notificationId = NOTIFICATION_ID_BASE + orderId.hashCode() + 31,
            channelId = CHANNEL_ID_ORDER,
            title = title,
            message = message,
            orderId = orderId,
            icon = R.mipmap.ic_launcher
        )
    }
    
    /**
     * Notify about order completion.
     */
    fun notifyOrderCompleted(
        orderId: String,
        productName: String
    ) {
        val title = "Order Completed! 🎉"
        val message = "Transaction for $productName is complete. Thank you for using ROSTRY!"
        
        showNotification(
            notificationId = NOTIFICATION_ID_BASE + orderId.hashCode() + 40,
            channelId = CHANNEL_ID_ORDER,
            title = title,
            message = message,
            orderId = orderId,
            icon = R.mipmap.ic_launcher
        )
    }
    
    private fun showNotification(
        notificationId: Int,
        channelId: String,
        title: String,
        message: String,
        orderId: String,
        icon: Int
    ) {
        try {
            // Create pending intent to open order tracking
            val intent = Intent().apply {
                action = "com.rio.rostry.ACTION_VIEW_ORDER"
                putExtra("orderId", orderId)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            
            val pendingIntent = PendingIntent.getActivity(
                context,
                notificationId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            
            val notification = NotificationCompat.Builder(context, channelId)
                .setSmallIcon(icon)
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(NotificationCompat.BigTextStyle().bigText(message))
                .setPriority(
                    if (channelId == CHANNEL_ID_PAYMENT || channelId == CHANNEL_ID_DELIVERY)
                        NotificationCompat.PRIORITY_HIGH 
                    else NotificationCompat.PRIORITY_DEFAULT
                )
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build()
            
            NotificationManagerCompat.from(context).notify(notificationId, notification)
            Timber.d("EvidenceOrderNotificationService: Sent notification - $title")
        } catch (e: SecurityException) {
            Timber.w(e, "EvidenceOrderNotificationService: Permission denied for notifications")
        } catch (e: Exception) {
            Timber.e(e, "EvidenceOrderNotificationService: Failed to send notification")
        }
    }
}
