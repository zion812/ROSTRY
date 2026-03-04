package com.rio.rostry.data.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Stores evidence/proofs for order transactions.
 * Each order can have multiple evidence items forming an "Evidence Pack".
 * This creates trust without a payment gateway - the platform becomes the record of truth.
 */
@Entity(
    tableName = "order_evidence",
    indices = [
        Index("orderId"),
        Index("uploadedBy"),
        Index("evidenceType"),
        Index("createdAt")
    ]
)
data class OrderEvidenceEntity(
    @PrimaryKey val evidenceId: String,
    val orderId: String,
    val evidenceType: String, // From EvidenceType enum
    val uploadedBy: String,   // User ID who uploaded
    val uploadedByRole: String, // "BUYER" or "SELLER"
    
    // Evidence content
    val imageUri: String?,     // Photo/screenshot URI
    val videoUri: String?,     // Video URI (for disputes)
    val textContent: String?,  // Transaction ID, OTP, notes
    val geoLatitude: Double?,  // GPS location
    val geoLongitude: Double?,
    val geoAddress: String?,   // Reverse geocoded address
    
    // Verification
    val isVerified: Boolean = false,
    val verifiedBy: String? = null,    // Who verified (other party or admin)
    val verifiedAt: Long? = null,
    val verificationNote: String? = null,
    
    // Metadata
    val deviceTimestamp: Long, // When captured on device
    val createdAt: Long,
    val updatedAt: Long,
    val isDeleted: Boolean = false,
    val dirty: Boolean = true
)

/**
 * Price quote entity for the 2-step pricing agreement.
 * 1. Seller sets base price
 * 2. Buyer submits delivery location
 * 3. Seller confirms delivery charge
 * 4. System generates final locked price
 */
@Entity(
    tableName = "order_quotes",
    indices = [
        Index("orderId"),
        Index("buyerId"),
        Index("sellerId"),
        Index("status")
    ]
)
data class OrderQuoteEntity(
    @PrimaryKey val quoteId: String,
    val orderId: String,
    val buyerId: String,
    val sellerId: String,
    val productId: String,
    
    // Product details at time of quote
    val productName: String,
    val quantity: Double,
    val unit: String, // "BIRDS", "KG", "TRAYS", etc.
    
    // Pricing breakdown
    val basePrice: Double,           // Seller's product price per unit
    val totalProductPrice: Double,   // basePrice * quantity
    val deliveryCharge: Double,      // Calculated based on distance
    val packingCharge: Double = 0.0, // Optional packing fee
    val platformFee: Double = 0.0,   // ROSTRY fee (if any)
    val discount: Double = 0.0,      // Any applied discount
    val finalTotal: Double,          // Grand total
    
    // Delivery details
    val deliveryType: String,        // From OrderDeliveryType
    val deliveryDistance: Double? = null,   // Distance in km
    val deliveryAddress: String? = null,
    val deliveryLatitude: Double? = null,
    val deliveryLongitude: Double? = null,
    val pickupAddress: String? = null,      // For buyer pickup
    val pickupLatitude: Double? = null,
    val pickupLongitude: Double? = null,
    
    // Payment agreement
    val paymentType: String,         // From OrderPaymentType
    val advanceAmount: Double? = null,      // Required advance (if split/full)
    val balanceAmount: Double? = null,      // Balance due on delivery
    
    // Quote status
    val status: String, // "DRAFT", "SENT", "NEGOTIATING", "BUYER_AGREED", "SELLER_AGREED", "LOCKED", "EXPIRED", "REJECTED"
    
    // Agreement tracking
    val buyerAgreedAt: Long? = null,
    val sellerAgreedAt: Long? = null,
    val lockedAt: Long? = null,      // When both agreed and locked
    val expiresAt: Long? = null,     // Quote expiry time
    
    // Version control for negotiations
    val version: Int = 1,
    val previousQuoteId: String? = null, // If this is a counter-offer
    
    // Notes
    val buyerNotes: String? = null,
    val sellerNotes: String? = null,
    
    // Timestamps
    val createdAt: Long,
    val updatedAt: Long,
    val dirty: Boolean = true
)

/**
 * Tracks individual payments for split payment scenarios.
 * Allows tracking advance + balance separately.
 */
@Entity(
    tableName = "order_payments",
    indices = [
        Index("orderId"),
        Index("payerId"),
        Index("paymentPhase"),
        Index("status")
    ]
)
data class OrderPaymentEntity(
    @PrimaryKey val paymentId: String,
    val orderId: String,
    val quoteId: String,
    val payerId: String,        // Buyer ID
    val receiverId: String,     // Seller ID
    
    // Payment phase
    val paymentPhase: String,   // "ADVANCE", "BALANCE", "FULL"
    val amount: Double,
    val currency: String = "INR",
    
    // Payment method
    val method: String,         // "UPI", "BANK_TRANSFER", "CASH"
    val upiId: String? = null,
    val bankDetails: String? = null, // JSON with account details
    
    // Status tracking
    val status: String,         // "PENDING", "PROOF_SUBMITTED", "VERIFIED", "REJECTED", "REFUNDED"
    
    // Proof references
    val proofEvidenceId: String? = null,  // Link to evidence
    val transactionRef: String? = null,    // UPI/Bank transaction ID
    
    // Verification
    val verifiedAt: Long? = null,
    val verifiedBy: String? = null,
    val rejectionReason: String? = null,
    
    // Refund tracking
    val refundedAmount: Double? = null,
    val refundedAt: Long? = null,
    val refundReason: String? = null,
    
    // Timeout
    val dueAt: Long,            // When payment is due
    val expiredAt: Long? = null,
    
    // Timestamps
    val createdAt: Long,
    val updatedAt: Long,
    val dirty: Boolean = true
)

/**
 * Delivery confirmation with OTP-based verification.
 */
@Entity(
    tableName = "delivery_confirmations",
    indices = [
        Index("orderId"),
        Index("status")
    ]
)
data class DeliveryConfirmationEntity(
    @PrimaryKey val confirmationId: String,
    val orderId: String,
    val buyerId: String,
    val sellerId: String,
    
    // OTP for delivery verification
    val deliveryOtp: String,        // 6-digit OTP sent to buyer
    val otpGeneratedAt: Long,
    val otpExpiresAt: Long,         // OTP valid for X hours
    val otpAttempts: Int = 0,       // Failed attempts
    val maxOtpAttempts: Int = 3,
    
    // Confirmation
    val status: String, // "PENDING", "OTP_VERIFIED", "PHOTO_CONFIRMED", "DISPUTED"
    val confirmationMethod: String? = null, // "OTP", "PHOTO", "GPS", "MANUAL"
    
    // Evidence references
    val deliveryPhotoEvidenceId: String? = null,
    val buyerConfirmationEvidenceId: String? = null,
    val gpsEvidenceId: String? = null,
    
    // Confirmation details
    val confirmedAt: Long? = null,
    val confirmedBy: String? = null,
    val deliveryNotes: String? = null,
    
    // For COD - final payment confirmation
    val balanceCollected: Boolean = false,
    val balanceCollectedAt: Long? = null,
    val balanceEvidenceId: String? = null,
    
    // Timestamps
    val createdAt: Long,
    val updatedAt: Long,
    val dirty: Boolean = true
)

/**
 * Order dispute entity for tracking disagreements.
 */
@Entity(
    tableName = "order_disputes",
    indices = [
        Index("orderId"),
        Index("raisedBy"),
        Index("status")
    ]
)
data class OrderDisputeEntity(
    @PrimaryKey val disputeId: String,
    val orderId: String,
    val raisedBy: String,           // User ID who raised dispute
    val raisedByRole: String,       // "BUYER" or "SELLER"
    val againstUserId: String,      // User dispute is against
    
    // Dispute details
    val reason: String,             // From DisputeReason enum
    val description: String,
    val requestedResolution: String?, // What the user wants
    val claimedAmount: Double?,     // Amount in dispute (if monetary)
    
    // Evidence
    val evidenceIds: String? = null, // Comma-separated evidence IDs
    
    // Status
    val status: String, // "OPEN", "UNDER_REVIEW", "AWAITING_RESPONSE", "RESOLVED", "ESCALATED", "CLOSED"
    
    // Resolution
    val resolvedAt: Long? = null,
    val resolvedBy: String? = null,  // Admin or automatic
    val resolutionType: String? = null, // "REFUND_BUYER", "RELEASE_TO_SELLER", "PARTIAL_REFUND", "MUTUAL_CANCEL"
    val resolutionNotes: String? = null,
    val refundedAmount: Double? = null,
    
    // Communication
    val lastResponseAt: Long? = null,
    val responseCount: Int = 0,
    
    // Escalation
    val escalatedAt: Long? = null,
    val escalationReason: String? = null,
    val adminNotes: String? = null,
    
    // Timestamps
    val createdAt: Long,
    val updatedAt: Long,
    val dirty: Boolean = true
)

/**
 * Audit log for all order state changes.
 * Provides complete audit trail for disputes.
 */
@Entity(
    tableName = "order_audit_logs",
    indices = [
        Index("orderId"),
        Index("performedBy"),
        Index("timestamp")
    ]
)
data class OrderAuditLogEntity(
    @PrimaryKey val logId: String,
    val orderId: String,
    val action: String,             // "STATE_CHANGE", "PAYMENT", "EVIDENCE_UPLOAD", "DISPUTE", etc.
    val fromState: String?,
    val toState: String?,
    val performedBy: String,
    val performedByRole: String,    // "BUYER", "SELLER", "SYSTEM", "ADMIN"
    val description: String,
    val metadata: String? = null,   // JSON with additional details
    val evidenceId: String? = null,
    val ipAddress: String? = null,
    val deviceInfo: String? = null,
    val timestamp: Long
)
