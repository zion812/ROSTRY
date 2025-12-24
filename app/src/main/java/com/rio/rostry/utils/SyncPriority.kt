package com.rio.rostry.utils

/**
 * Defines sync priorities for the outbox queue.
 * Lower numbers = higher priority (processed first).
 */
object SyncPriority {
    /**
     * IMMEDIATE (0): Critical operations that should sync as soon as possible.
     * Examples: Payment confirmations, Order status changes, Security events
     */
    const val IMMEDIATE = 0
    
    /**
     * HIGH (1): Important operations that need quick sync.
     * Examples: Messages, Order placements, Transfer requests
     */
    const val HIGH = 1
    
    /**
     * NORMAL (2): Regular operations with standard sync timing.
     * Examples: Product listings, Profile updates, Post creation
     */
    const val NORMAL = 2
    
    /**
     * LOW (3): Non-urgent operations that can wait.
     * Examples: Analytics events, Preferences, View tracking
     */
    const val LOW = 3
    
    /**
     * Maps operation types to their default priority.
     */
    fun forOperation(operation: String): Int = when (operation) {
        // Critical - sync immediately
        "PAYMENT_CONFIRM",
        "ORDER_STATUS_CHANGE",
        "ORDER_CANCEL",
        "SECURITY_EVENT" -> IMMEDIATE
        
        // High priority
        "MESSAGE",
        "ORDER_PLACE",
        "TRANSFER_REQUEST",
        "OFFER_SENT" -> HIGH
        
        // Normal priority
        "PRODUCT_CREATE",
        "PRODUCT_UPDATE",
        "PROFILE_UPDATE",
        "POST_CREATE",
        "REVIEW_SUBMIT" -> NORMAL
        
        // Low priority
        "ANALYTICS_EVENT",
        "VIEW_TRACK",
        "PREFERENCE_UPDATE",
        "NOTIFICATION_STATE" -> LOW
        
        // Default
        else -> NORMAL
    }
}

/**
 * Message delivery states for tracking message lifecycle.
 */
object MessageState {
    const val PENDING = "PENDING"     // Queued locally, not yet sent
    const val SENDING = "SENDING"     // Currently being sent
    const val SENT = "SENT"           // Successfully sent to server
    const val DELIVERED = "DELIVERED" // Delivered to recipient device
    const val READ = "READ"           // Read by recipient
    const val FAILED = "FAILED"       // Failed to send (after retries)
}
