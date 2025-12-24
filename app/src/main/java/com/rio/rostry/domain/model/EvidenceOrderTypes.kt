package com.rio.rostry.domain.model

/**
 * Evidence-Based Order Status Machine
 * 
 * This 10-state workflow ensures every step requires proof and both sides must confirm.
 * The platform acts as a trusted middleman for workflow + proof + agreement.
 */
enum class EvidenceOrderStatus(val value: String, val displayName: String, val description: String) {
    // Stage 1: Enquiry/Quote
    ENQUIRY("ENQUIRY", "Enquiry Sent", "Buyer has sent an enquiry with quantity, location, and payment preference"),
    QUOTE_SENT("QUOTE_SENT", "Quote Received", "Seller has confirmed final price, delivery fee, and allowed payment methods"),
    AGREEMENT_LOCKED("AGREEMENT_LOCKED", "Agreement Locked", "Both parties agreed to price, delivery, and payment method. No edits allowed."),
    
    // Stage 2: Payment
    ADVANCE_PENDING("ADVANCE_PENDING", "Awaiting Advance Payment", "Buyer must upload payment proof for advance amount"),
    PAYMENT_PROOF_SUBMITTED("PAYMENT_PROOF_SUBMITTED", "Payment Proof Submitted", "Buyer has uploaded payment screenshot/slip; awaiting seller verification"),
    PAYMENT_VERIFIED("PAYMENT_VERIFIED", "Payment Verified", "Seller has confirmed receiving the payment"),
    
    // Stage 3: Fulfillment
    PREPARING("PREPARING", "Preparing Order", "Seller is preparing/packing the order"),
    DISPATCHED("DISPATCHED", "Dispatched", "Seller has shipped; dispatch proof uploaded"),
    READY_FOR_PICKUP("READY_FOR_PICKUP", "Ready for Pickup", "Order is ready; buyer can come to pick up"),
    
    // Stage 4: Delivery
    DELIVERED("DELIVERED", "Delivered", "Buyer has confirmed receiving the order with proof"),
    COMPLETED("COMPLETED", "Completed", "Transaction complete; feedback enabled"),
    
    // Error states
    DISPUTE("DISPUTE", "Dispute Raised", "A dispute has been raised by either party"),
    ESCALATED("ESCALATED", "Escalated to Admin", "Dispute escalated to ROSTRY admin for resolution"),
    CANCELLED("CANCELLED", "Cancelled", "Order was cancelled before completion"),
    EXPIRED("EXPIRED", "Expired", "Order expired due to timeout");

    companion object {
        fun fromString(value: String): EvidenceOrderStatus =
            values().find { it.value == value } ?: ENQUIRY
            
        /**
         * Valid state transitions based on the workflow.
         */
        fun getValidTransitions(current: EvidenceOrderStatus): Set<EvidenceOrderStatus> = when (current) {
            ENQUIRY -> setOf(QUOTE_SENT, CANCELLED, EXPIRED)
            QUOTE_SENT -> setOf(AGREEMENT_LOCKED, CANCELLED, EXPIRED)
            AGREEMENT_LOCKED -> setOf(ADVANCE_PENDING, PAYMENT_PROOF_SUBMITTED, PREPARING, CANCELLED) // Depends on payment type
            ADVANCE_PENDING -> setOf(PAYMENT_PROOF_SUBMITTED, CANCELLED, EXPIRED)
            PAYMENT_PROOF_SUBMITTED -> setOf(PAYMENT_VERIFIED, DISPUTE, CANCELLED)
            PAYMENT_VERIFIED -> setOf(PREPARING, DISPATCHED, READY_FOR_PICKUP)
            PREPARING -> setOf(DISPATCHED, READY_FOR_PICKUP, CANCELLED)
            DISPATCHED -> setOf(DELIVERED, DISPUTE)
            READY_FOR_PICKUP -> setOf(DELIVERED, DISPUTE, EXPIRED)
            DELIVERED -> setOf(COMPLETED, DISPUTE)
            COMPLETED -> emptySet()
            DISPUTE -> setOf(ESCALATED, COMPLETED, CANCELLED)
            ESCALATED -> setOf(COMPLETED, CANCELLED)
            CANCELLED -> emptySet()
            EXPIRED -> emptySet()
        }
        
        fun canTransition(from: EvidenceOrderStatus, to: EvidenceOrderStatus): Boolean =
            getValidTransitions(from).contains(to)
    }
}

/**
 * Payment types supported by the evidence-based system.
 */
enum class OrderPaymentType(val value: String, val displayName: String, val requiresAdvance: Boolean, val advancePercent: Int) {
    COD("COD", "Cash on Delivery", false, 0),
    FULL_ADVANCE("FULL_ADVANCE", "Full Advance Payment", true, 100),
    SPLIT_50_50("SPLIT_50_50", "50% Advance + 50% on Delivery", true, 50),
    BUYER_PICKUP("BUYER_PICKUP", "Buyer Pickup (Pay at Location)", false, 0);

    companion object {
        fun fromString(value: String): OrderPaymentType =
            values().find { it.value == value } ?: COD
    }
}

/**
 * Delivery types for orders.
 */
enum class OrderDeliveryType(val value: String, val displayName: String) {
    SELLER_DELIVERY("SELLER_DELIVERY", "Seller Delivers to Buyer"),
    BUYER_PICKUP("BUYER_PICKUP", "Buyer Picks Up from Seller"),
    THIRD_PARTY("THIRD_PARTY", "Third-Party Courier");

    companion object {
        fun fromString(value: String): OrderDeliveryType =
            values().find { it.value == value } ?: SELLER_DELIVERY
    }
}

/**
 * Types of evidence that can be collected.
 */
enum class EvidenceType(val value: String, val displayName: String, val requiredFor: String) {
    // Payment proofs
    PAYMENT_SCREENSHOT("PAYMENT_SCREENSHOT", "UPI/Bank Screenshot", "advance_payment"),
    BANK_SLIP("BANK_SLIP", "Bank Transfer Slip", "advance_payment"),
    CASH_RECEIPT("CASH_RECEIPT", "Cash Receipt Photo", "cod_payment"),
    TRANSACTION_ID("TRANSACTION_ID", "Transaction Reference ID", "payment_verification"),
    
    // Seller proofs
    PACKING_PHOTO("PACKING_PHOTO", "Product Packing Photo", "dispatch"),
    DISPATCH_PHOTO("DISPATCH_PHOTO", "Dispatch/Loading Photo", "dispatch"),
    INVOICE_PHOTO("INVOICE_PHOTO", "Invoice/Bill Photo", "dispatch"),
    
    // Delivery proofs
    DELIVERY_PHOTO("DELIVERY_PHOTO", "Delivery Handover Photo", "delivery"),
    BUYER_CONFIRMATION_PHOTO("BUYER_CONFIRMATION_PHOTO", "Buyer with Product Photo", "delivery"),
    OTP_CONFIRMATION("OTP_CONFIRMATION", "OTP Confirmation Code", "delivery"),
    GPS_LOCATION("GPS_LOCATION", "GPS Location at Delivery", "delivery"),
    
    // Dispute proofs
    DISPUTE_EVIDENCE("DISPUTE_EVIDENCE", "Dispute Evidence Photo/Video", "dispute"),
    CHAT_SNAPSHOT("CHAT_SNAPSHOT", "Chat Conversation Snapshot", "dispute");

    companion object {
        fun fromString(value: String): EvidenceType =
            values().find { it.value == value } ?: PAYMENT_SCREENSHOT
            
        fun getRequiredForStage(stage: String): List<EvidenceType> =
            values().filter { it.requiredFor == stage }
    }
}

/**
 * Dispute reasons for escalation.
 */
enum class DisputeReason(val value: String, val displayName: String) {
    PAYMENT_NOT_RECEIVED("PAYMENT_NOT_RECEIVED", "Payment not received by seller"),
    WRONG_PRODUCT("WRONG_PRODUCT", "Wrong product delivered"),
    QUALITY_ISSUE("QUALITY_ISSUE", "Product quality not as described"),
    QUANTITY_MISMATCH("QUANTITY_MISMATCH", "Quantity doesn't match order"),
    DELIVERY_ISSUE("DELIVERY_ISSUE", "Delivery not completed"),
    REFUND_NOT_RECEIVED("REFUND_NOT_RECEIVED", "Refund not processed"),
    SELLER_UNRESPONSIVE("SELLER_UNRESPONSIVE", "Seller not responding"),
    BUYER_UNRESPONSIVE("BUYER_UNRESPONSIVE", "Buyer not responding"),
    PRICE_DISPUTE("PRICE_DISPUTE", "Price disagreement after agreement"),
    OTHER("OTHER", "Other issue");

    companion object {
        fun fromString(value: String): DisputeReason =
            values().find { it.value == value } ?: OTHER
    }
}
