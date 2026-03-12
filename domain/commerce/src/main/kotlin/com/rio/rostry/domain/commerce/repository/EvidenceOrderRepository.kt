package com.rio.rostry.domain.commerce.repository

import com.rio.rostry.core.common.Result
import kotlinx.coroutines.flow.Flow

/**
 * Domain interface for the Evidence-Based Order system.
 *
 * Implements the "Agreement + Evidence + Confirmation" model where
 * money happens outside the app and trust is created inside via
 * evidence and approvals.
 */
interface EvidenceOrderRepository {

    // ── Quote Management ──
    suspend fun createEnquiry(
        buyerId: String, sellerId: String, productId: String,
        productName: String, quantity: Double, unit: String,
        deliveryType: String, deliveryAddress: String?,
        deliveryLatitude: Double?, deliveryLongitude: Double?,
        paymentPreference: String, buyerNotes: String?
    ): Result<Map<String, Any>>

    suspend fun sendQuote(
        quoteId: String, basePrice: Double, deliveryCharge: Double,
        packingCharge: Double, allowedPaymentTypes: List<String>,
        sellerNotes: String?, expiresInHours: Int = 24
    ): Result<Map<String, Any>>

    suspend fun counterOffer(
        originalQuoteId: String, newPrice: Double? = null,
        newDeliveryCharge: Double? = null, notes: String?
    ): Result<Map<String, Any>>

    suspend fun buyerAgreeToQuote(quoteId: String): Result<Map<String, Any>>
    suspend fun sellerAgreeToQuote(quoteId: String): Result<Map<String, Any>>

    // ── Payment Management ──
    suspend fun createPaymentRequest(
        orderId: String, quoteId: String, phase: String,
        amount: Double, method: String, dueInHours: Int
    ): Result<Map<String, Any>>

    suspend fun submitPaymentProof(paymentId: String, evidenceId: String, transactionRef: String?): Result<Unit>
    suspend fun verifyPayment(paymentId: String, verifiedBy: String, notes: String?): Result<Unit>
    suspend fun rejectPayment(paymentId: String, reason: String): Result<Unit>

    // ── Evidence ──
    suspend fun uploadEvidence(
        orderId: String, evidenceType: String, uploadedBy: String,
        uploadedByRole: String, imageUri: String?, videoUri: String?,
        textContent: String?, geoLatitude: Double?, geoLongitude: Double?
    ): Result<Map<String, Any>>

    suspend fun verifyEvidence(evidenceId: String, verifiedBy: String, note: String?): Result<Unit>
    fun getOrderEvidence(orderId: String): Flow<List<Map<String, Any>>>

    // ── Delivery ──
    suspend fun generateDeliveryOtp(orderId: String): Result<String>
    suspend fun verifyDeliveryOtp(orderId: String, otp: String, confirmedBy: String, verifierLat: Double? = null, verifierLng: Double? = null): Result<Unit>
    suspend fun confirmDeliveryWithPhoto(orderId: String, deliveryPhotoId: String, buyerPhotoId: String?, confirmedBy: String): Result<Unit>
    suspend fun markBalanceCollected(orderId: String, evidenceId: String?): Result<Unit>

    // ── State Machine ──
    suspend fun transitionOrderState(orderId: String, newStatus: String, performedBy: String, performedByRole: String, notes: String?): Result<Unit>

    // ── Disputes ──
    suspend fun raiseDispute(
        orderId: String, raisedBy: String, raisedByRole: String,
        reason: String, description: String, requestedResolution: String?,
        claimedAmount: Double?, evidenceIds: List<String>?
    ): Result<Map<String, Any>>

    suspend fun resolveDispute(disputeId: String, resolvedBy: String, resolutionType: String, notes: String?, refundAmount: Double?): Result<Unit>
    fun getUserActiveDisputes(userId: String): Flow<List<Map<String, Any>>>

    // ── Queries ──
    fun getBuyerActiveQuotes(buyerId: String): Flow<List<Map<String, Any>>>
    fun getSellerActiveQuotes(sellerId: String): Flow<List<Map<String, Any>>>
    fun getOrderAuditTrail(orderId: String): Flow<List<Map<String, Any>>>
    fun getPaymentsAwaitingVerification(sellerId: String): Flow<List<Map<String, Any>>>
    fun getPendingPaymentsForBuyer(buyerId: String): Flow<List<Map<String, Any>>>

    // ── Timeouts ──
    suspend fun expireOldQuotes(now: Long = System.currentTimeMillis()): Result<Int>
    suspend fun expireOverduePayments()

    // ── Reviews ──
    suspend fun submitReview(orderId: String, reviewerId: String, rating: Int, content: String?, wouldRecommend: Boolean?): Result<Unit>
}
