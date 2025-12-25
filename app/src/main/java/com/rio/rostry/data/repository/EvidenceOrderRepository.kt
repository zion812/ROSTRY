package com.rio.rostry.data.repository

import com.rio.rostry.data.database.dao.*
import com.rio.rostry.data.database.entity.*
import com.rio.rostry.data.database.entity.ReviewEntity
import com.rio.rostry.domain.model.*
import com.rio.rostry.utils.LocationUtils
import com.rio.rostry.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

/**
 * Evidence-Based Order Repository
 * 
 * Implements the "Agreement + Evidence + Confirmation" model where:
 * - Money happens outside the app (UPI/bank/cash)
 * - Trust is created inside the app via evidence + approvals
 * - The platform becomes the record of truth
 */
interface EvidenceOrderRepository {
    // Quote Management
    suspend fun createEnquiry(
        buyerId: String,
        sellerId: String,
        productId: String,
        productName: String,
        quantity: Double,
        unit: String,
        deliveryType: OrderDeliveryType,
        deliveryAddress: String?,
        deliveryLatitude: Double?,
        deliveryLongitude: Double?,
        paymentPreference: OrderPaymentType,
        buyerNotes: String?
    ): Resource<OrderQuoteEntity>
    
    suspend fun sendQuote(
        quoteId: String,
        basePrice: Double,
        deliveryCharge: Double,
        packingCharge: Double,
        allowedPaymentTypes: List<OrderPaymentType>,
        sellerNotes: String?,
        expiresInHours: Int = 24
    ): Resource<OrderQuoteEntity>
    
    suspend fun counterOffer(
        originalQuoteId: String,
        newPrice: Double? = null,
        newDeliveryCharge: Double? = null,
        notes: String?
    ): Resource<OrderQuoteEntity>
    
    suspend fun buyerAgreeToQuote(quoteId: String): Resource<OrderQuoteEntity>
    suspend fun sellerAgreeToQuote(quoteId: String): Resource<OrderQuoteEntity>
    
    // Payment Management
    suspend fun createPaymentRequest(
        orderId: String,
        quoteId: String,
        phase: String, // "ADVANCE", "BALANCE", "FULL"
        amount: Double,
        method: String,
        dueInHours: Int
    ): Resource<OrderPaymentEntity>
    
    suspend fun submitPaymentProof(
        paymentId: String,
        evidenceId: String,
        transactionRef: String?
    ): Resource<Unit>
    
    suspend fun verifyPayment(
        paymentId: String,
        verifiedBy: String,
        notes: String?
    ): Resource<Unit>
    
    suspend fun rejectPayment(
        paymentId: String,
        reason: String
    ): Resource<Unit>
    
    // Evidence Management
    suspend fun uploadEvidence(
        orderId: String,
        evidenceType: EvidenceType,
        uploadedBy: String,
        uploadedByRole: String,
        imageUri: String?,
        videoUri: String?,
        textContent: String?,
        geoLatitude: Double?,
        geoLongitude: Double?
    ): Resource<OrderEvidenceEntity>
    
    suspend fun verifyEvidence(
        evidenceId: String,
        verifiedBy: String,
        note: String?
    ): Resource<Unit>
    
    fun getOrderEvidence(orderId: String): Flow<List<OrderEvidenceEntity>>
    
    // Delivery Confirmation
    suspend fun generateDeliveryOtp(orderId: String): Resource<String>
    suspend fun verifyDeliveryOtp(
        orderId: String, 
        otp: String, 
        confirmedBy: String,
        verifierLat: Double? = null,
        verifierLng: Double? = null
    ): Resource<Unit>
    suspend fun confirmDeliveryWithPhoto(orderId: String, deliveryPhotoId: String, buyerPhotoId: String?, confirmedBy: String): Resource<Unit>
    suspend fun markBalanceCollected(orderId: String, evidenceId: String?): Resource<Unit>
    
    // Order State Machine
    suspend fun transitionOrderState(
        orderId: String,
        newStatus: EvidenceOrderStatus,
        performedBy: String,
        performedByRole: String,
        notes: String?
    ): Resource<Unit>
    
    // Disputes
    suspend fun raiseDispute(
        orderId: String,
        raisedBy: String,
        raisedByRole: String,
        reason: DisputeReason,
        description: String,
        requestedResolution: String?,
        claimedAmount: Double?,
        evidenceIds: List<String>?
    ): Resource<OrderDisputeEntity>
    
    suspend fun escalateDispute(disputeId: String, reason: String): Resource<Unit>
    suspend fun resolveDispute(
        disputeId: String,
        resolvedBy: String,
        resolutionType: String,
        notes: String?,
        refundAmount: Double?
    ): Resource<Unit>
    
    fun getUserActiveDisputes(userId: String): Flow<List<OrderDisputeEntity>>
    
    // Queries
    fun getBuyerActiveQuotes(buyerId: String): Flow<List<OrderQuoteEntity>>
    fun getSellerActiveQuotes(sellerId: String): Flow<List<OrderQuoteEntity>>
    fun getOrderAuditTrail(orderId: String): Flow<List<OrderAuditLogEntity>>
    fun getPaymentsAwaitingVerification(sellerId: String): Flow<List<OrderPaymentEntity>>
    fun getPendingPaymentsForBuyer(buyerId: String): Flow<List<OrderPaymentEntity>>
    
    // Timeout handlers
    suspend fun expireOldQuotes(now: Long = System.currentTimeMillis()): Resource<Int>
    suspend fun expireOverduePayments()
    suspend fun escalateDispute(disputeId: String): Resource<Unit>

    // Reviews
    suspend fun submitReview(
        orderId: String,
        reviewerId: String,
        rating: Int,
        content: String?,
        wouldRecommend: Boolean?
    ): Resource<Unit>
}

@Singleton
class EvidenceOrderRepositoryImpl @Inject constructor(
    private val orderDao: OrderDao,
    private val quoteDao: OrderQuoteDao,
    private val paymentDao: OrderPaymentDao,
    private val evidenceDao: OrderEvidenceDao,
    private val confirmationDao: DeliveryConfirmationDao,
    private val disputeDao: OrderDisputeDao,
    private val auditLogDao: OrderAuditLogDao,
    private val reviewDao: ReviewDao
) : EvidenceOrderRepository {

    // ==================== QUOTE MANAGEMENT ====================
    
    override suspend fun createEnquiry(
        buyerId: String,
        sellerId: String,
        productId: String,
        productName: String,
        quantity: Double,
        unit: String,
        deliveryType: OrderDeliveryType,
        deliveryAddress: String?,
        deliveryLatitude: Double?,
        deliveryLongitude: Double?,
        paymentPreference: OrderPaymentType,
        buyerNotes: String?
    ): Resource<OrderQuoteEntity> {
        return try {
            if (quantity <= 0) return Resource.Error("Quantity must be greater than zero")
            
            val now = System.currentTimeMillis()
            val orderId = UUID.randomUUID().toString()
            val quoteId = UUID.randomUUID().toString()
            
            // Create order in ENQUIRY state
            val order = OrderEntity(
                orderId = orderId,
                buyerId = buyerId,
                sellerId = sellerId,
                status = EvidenceOrderStatus.ENQUIRY.value,
                createdAt = now,
                updatedAt = now,
                lastModifiedAt = now,
                dirty = true
            )
            orderDao.insertOrUpdate(order)
            
            // Create quote draft
            val quote = OrderQuoteEntity(
                quoteId = quoteId,
                orderId = orderId,
                buyerId = buyerId,
                sellerId = sellerId,
                productId = productId,
                productName = productName,
                quantity = quantity,
                unit = unit,
                basePrice = 0.0,
                totalProductPrice = 0.0,
                deliveryCharge = 0.0,
                finalTotal = 0.0,
                deliveryType = deliveryType.value,
                deliveryAddress = deliveryAddress,
                deliveryLatitude = deliveryLatitude,
                deliveryLongitude = deliveryLongitude,
                paymentType = paymentPreference.value,
                status = QuoteStatus.DRAFT.value,
                version = 1,
                buyerNotes = buyerNotes,
                createdAt = now,
                updatedAt = now
            )
            quoteDao.upsert(quote)
            
            // Audit log
            logAction(orderId, "ENQUIRY_CREATED", null, EvidenceOrderStatus.ENQUIRY.value,
                buyerId, "BUYER", "Buyer created enquiry for $quantity $unit of $productName")
            
            Resource.Success(quote)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to create enquiry")
        }
    }
    
    override suspend fun sendQuote(
        quoteId: String,
        basePrice: Double,
        deliveryCharge: Double,
        packingCharge: Double,
        allowedPaymentTypes: List<OrderPaymentType>,
        sellerNotes: String?,
        expiresInHours: Int
    ): Resource<OrderQuoteEntity> {
        return try {
            if (basePrice < 0 || deliveryCharge < 0 || packingCharge < 0) {
                return Resource.Error("Prices cannot be negative")
            }
            val existing = quoteDao.findById(quoteId) 
                ?: return Resource.Error("Quote not found")
            
            val now = System.currentTimeMillis()
            val totalProductPrice = basePrice * existing.quantity
            val finalTotal = totalProductPrice + deliveryCharge + packingCharge - existing.discount
            
            val updated = existing.copy(
                basePrice = basePrice,
                totalProductPrice = totalProductPrice,
                deliveryCharge = deliveryCharge,
                packingCharge = packingCharge,
                finalTotal = finalTotal,
                advanceAmount = if (existing.paymentType == OrderPaymentType.FULL_ADVANCE.value) finalTotal
                    else if (existing.paymentType == OrderPaymentType.SPLIT_50_50.value) finalTotal * 0.5
                    else null,
                balanceAmount = if (existing.paymentType == OrderPaymentType.SPLIT_50_50.value) finalTotal * 0.5
                    else if (existing.paymentType == OrderPaymentType.COD.value) finalTotal
                    else null,
                status = QuoteStatus.SENT.value,
                sellerNotes = sellerNotes,
                expiresAt = now + (expiresInHours * 60 * 60 * 1000L),
                updatedAt = now
            )
            quoteDao.upsert(updated)
            
            // Update order status
            transitionOrderStateInternal(
                existing.orderId, 
                EvidenceOrderStatus.QUOTE_SENT, 
                existing.sellerId, 
                "SELLER",
                "Quote sent: ₹$finalTotal"
            )
            
            Resource.Success(updated)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to send quote")
        }
    }
    
    override suspend fun counterOffer(
        originalQuoteId: String,
        newPrice: Double?,
        newDeliveryCharge: Double?,
        notes: String?
    ): Resource<OrderQuoteEntity> {
        return try {
            if ((newPrice != null && newPrice < 0) || (newDeliveryCharge != null && newDeliveryCharge < 0)) {
                return Resource.Error("Counter offer prices cannot be negative")
            }
            val original = quoteDao.findById(originalQuoteId) 
                ?: return Resource.Error("Original quote not found")
            
            val now = System.currentTimeMillis()
            val basePrice = newPrice ?: original.basePrice
            val deliveryCharge = newDeliveryCharge ?: original.deliveryCharge
            val totalProductPrice = basePrice * original.quantity
            val finalTotal = totalProductPrice + deliveryCharge + original.packingCharge - original.discount
            
            val counterQuote = original.copy(
                quoteId = UUID.randomUUID().toString(),
                basePrice = basePrice,
                totalProductPrice = totalProductPrice,
                deliveryCharge = deliveryCharge,
                finalTotal = finalTotal,
                advanceAmount = calculateAdvance(original.paymentType, finalTotal),
                balanceAmount = calculateBalance(original.paymentType, finalTotal),
                status = QuoteStatus.NEGOTIATING.value,
                version = original.version + 1,
                previousQuoteId = originalQuoteId,
                buyerAgreedAt = null,
                sellerAgreedAt = null,
                lockedAt = null,
                buyerNotes = notes,
                createdAt = now,
                updatedAt = now
            )
            
            // Mark original as superseded
            quoteDao.updateStatus(originalQuoteId, QuoteStatus.SUPERSEDED.value, now)
            quoteDao.upsert(counterQuote)
            
            logAction(original.orderId, "COUNTER_OFFER", QuoteStatus.NEGOTIATING.value, QuoteStatus.NEGOTIATING.value,
                original.buyerId, "BUYER", "Counter offer: ₹$finalTotal (was ₹${original.finalTotal})")
            
            Resource.Success(counterQuote)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to create counter offer")
        }
    }
    
    override suspend fun buyerAgreeToQuote(quoteId: String): Resource<OrderQuoteEntity> {
        return try {
            val quote = quoteDao.findById(quoteId) 
                ?: return Resource.Error("Quote not found")
            
            if (quote.status == QuoteStatus.LOCKED.value) {
                return Resource.Error("Quote is already locked")
            }
            
            val now = System.currentTimeMillis()
            quoteDao.buyerAgree(quoteId, now)
            
            val updated = quoteDao.findById(quoteId)!!
            
            if (updated.status == QuoteStatus.LOCKED.value) {
                // Both agreed - lock the price and transition order
                transitionOrderStateInternal(
                    quote.orderId,
                    EvidenceOrderStatus.AGREEMENT_LOCKED,
                    quote.buyerId,
                    "BUYER",
                    "Agreement locked at ₹${quote.finalTotal}"
                )
                
                // Create payment request if advance required
                val paymentType = OrderPaymentType.fromString(quote.paymentType)
                if (paymentType.requiresAdvance) {
                    createPaymentRequest(
                        quote.orderId,
                        quoteId,
                        if (paymentType == OrderPaymentType.FULL_ADVANCE) PaymentPhase.FULL.value else PaymentPhase.ADVANCE.value,
                        quote.advanceAmount ?: quote.finalTotal,
                        "UPI", // Default, buyer can change
                        24
                    )
                }
            }
            
            logAction(quote.orderId, "BUYER_AGREED", quote.status, updated.status,
                quote.buyerId, "BUYER", "Buyer agreed to quote")
            
            Resource.Success(updated)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to agree to quote")
        }
    }
    
    override suspend fun sellerAgreeToQuote(quoteId: String): Resource<OrderQuoteEntity> {
        return try {
            val quote = quoteDao.findById(quoteId) 
                ?: return Resource.Error("Quote not found")
            
            if (quote.status == QuoteStatus.LOCKED.value) {
                return Resource.Error("Quote is already locked")
            }
            
            val now = System.currentTimeMillis()
            quoteDao.sellerAgree(quoteId, now)
            
            val updated = quoteDao.findById(quoteId)!!
            
            if (updated.status == QuoteStatus.LOCKED.value) {
                transitionOrderStateInternal(
                    quote.orderId,
                    EvidenceOrderStatus.AGREEMENT_LOCKED,
                    quote.sellerId,
                    "SELLER",
                    "Agreement locked at ₹${quote.finalTotal}"
                )
            }
            
            logAction(quote.orderId, "SELLER_AGREED", quote.status, updated.status,
                quote.sellerId, "SELLER", "Seller agreed to quote")
            
            Resource.Success(updated)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to agree to quote")
        }
    }

    // ==================== PAYMENT MANAGEMENT ====================
    
    override suspend fun createPaymentRequest(
        orderId: String,
        quoteId: String,
        phase: String,
        amount: Double,
        method: String,
        dueInHours: Int
    ): Resource<OrderPaymentEntity> {
        return try {
            if (amount <= 0) return Resource.Error("Payment amount must be positive")
            val quote = quoteDao.findById(quoteId) 
                ?: return Resource.Error("Quote not found")
            
            val now = System.currentTimeMillis()
            val payment = OrderPaymentEntity(
                paymentId = UUID.randomUUID().toString(),
                orderId = orderId,
                quoteId = quoteId,
                payerId = quote.buyerId,
                receiverId = quote.sellerId,
                paymentPhase = phase,
                amount = amount,
                method = method,
                status = "PENDING",
                dueAt = now + (dueInHours * 60 * 60 * 1000L),
                createdAt = now,
                updatedAt = now
            )
            paymentDao.upsert(payment)
            
            // Transition to appropriate status
            val newStatus = if (phase == PaymentPhase.ADVANCE.value || phase == PaymentPhase.FULL.value) 
                EvidenceOrderStatus.ADVANCE_PENDING else null
            
            if (newStatus != null) {
                transitionOrderStateInternal(orderId, newStatus, "SYSTEM", "SYSTEM",
                    "Payment request created: ₹$amount ($phase)")
            }
            
            Resource.Success(payment)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to create payment request")
        }
    }
    
    override suspend fun submitPaymentProof(
        paymentId: String,
        evidenceId: String,
        transactionRef: String?
    ): Resource<Unit> {
        return try {
            val payment = paymentDao.findById(paymentId) 
                ?: return Resource.Error("Payment not found")
            
            if (payment.status != "PENDING") {
                return Resource.Error("Payment is not in pending state")
            }
            
            val now = System.currentTimeMillis()
            paymentDao.submitProof(paymentId, evidenceId, transactionRef, now)
            
            transitionOrderStateInternal(
                payment.orderId,
                EvidenceOrderStatus.PAYMENT_PROOF_SUBMITTED,
                payment.payerId,
                "BUYER",
                "Payment proof submitted: ${transactionRef ?: "Photo uploaded"}"
            )
            
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to submit payment proof")
        }
    }
    
    override suspend fun verifyPayment(
        paymentId: String,
        verifiedBy: String,
        notes: String?
    ): Resource<Unit> {
        return try {
            val payment = paymentDao.findById(paymentId) 
                ?: return Resource.Error("Payment not found")
            
            if (payment.status != "PROOF_SUBMITTED") {
                return Resource.Error("Payment proof not yet submitted")
            }
            
            val now = System.currentTimeMillis()
            paymentDao.markVerified(paymentId, verifiedBy, now)
            
            // Check if all required payments are verified
            val quote = quoteDao.findById(payment.quoteId)
            val totalVerified = paymentDao.getTotalVerifiedAmount(payment.orderId) ?: 0.0
            val paymentType = OrderPaymentType.fromString(quote?.paymentType ?: "COD")
            
            // Determine next state
            val nextStatus = when {
                paymentType == OrderPaymentType.FULL_ADVANCE && totalVerified >= (quote?.finalTotal ?: 0.0) ->
                    EvidenceOrderStatus.PAYMENT_VERIFIED
                paymentType == OrderPaymentType.SPLIT_50_50 && payment.paymentPhase == PaymentPhase.ADVANCE.value ->
                    EvidenceOrderStatus.PAYMENT_VERIFIED
                else -> EvidenceOrderStatus.PAYMENT_VERIFIED
            }
            
            transitionOrderStateInternal(
                payment.orderId,
                nextStatus,
                verifiedBy,
                "SELLER",
                "Payment verified: ₹${payment.amount}"
            )
            
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to verify payment")
        }
    }
    
    override suspend fun rejectPayment(paymentId: String, reason: String): Resource<Unit> {
        return try {
            val payment = paymentDao.findById(paymentId) 
                ?: return Resource.Error("Payment not found")
            
            val now = System.currentTimeMillis()
            paymentDao.markRejected(paymentId, reason, now)
            
            logAction(payment.orderId, "PAYMENT_REJECTED", null, null,
                payment.receiverId, "SELLER", "Payment rejected: $reason")
            
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to reject payment")
        }
    }

    // ==================== EVIDENCE MANAGEMENT ====================
    
    override suspend fun uploadEvidence(
        orderId: String,
        evidenceType: EvidenceType,
        uploadedBy: String,
        uploadedByRole: String,
        imageUri: String?,
        videoUri: String?,
        textContent: String?,
        geoLatitude: Double?,
        geoLongitude: Double?
    ): Resource<OrderEvidenceEntity> {
        return try {
            // Validate: Only allow evidence upload if order isn't COMPLETED, CANCELLED or EXPIRED
            val order = orderDao.findById(orderId) ?: return Resource.Error("Order not found")
            val status = EvidenceOrderStatus.fromString(order.status)
            if (status == EvidenceOrderStatus.COMPLETED || status == EvidenceOrderStatus.CANCELLED || status == EvidenceOrderStatus.EXPIRED) {
                return Resource.Error("Cannot upload evidence for ${status.displayName} order")
            }

            val now = System.currentTimeMillis()
            val evidence = OrderEvidenceEntity(
                evidenceId = UUID.randomUUID().toString(),
                orderId = orderId,
                evidenceType = evidenceType.value,
                uploadedBy = uploadedBy,
                uploadedByRole = uploadedByRole,
                imageUri = imageUri,
                videoUri = videoUri,
                textContent = textContent,
                geoLatitude = geoLatitude,
                geoLongitude = geoLongitude,
                geoAddress = null, // Will be reverse geocoded
                deviceTimestamp = now,
                createdAt = now,
                updatedAt = now
            )
            evidenceDao.upsert(evidence)
            
            logAction(orderId, "EVIDENCE_UPLOADED", null, null,
                uploadedBy, uploadedByRole, "Evidence uploaded: ${evidenceType.displayName}")
            
            Resource.Success(evidence)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to upload evidence")
        }
    }
    
    override suspend fun verifyEvidence(
        evidenceId: String,
        verifiedBy: String,
        note: String?
    ): Resource<Unit> {
        return try {
            val now = System.currentTimeMillis()
            evidenceDao.markVerified(evidenceId, verifiedBy, now, note, now)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to verify evidence")
        }
    }
    
    override fun getOrderEvidence(orderId: String): Flow<List<OrderEvidenceEntity>> =
        evidenceDao.getOrderEvidence(orderId)

    // ==================== DELIVERY CONFIRMATION ====================
    
    override suspend fun generateDeliveryOtp(orderId: String): Resource<String> {
        return try {
            val order = orderDao.findById(orderId) 
                ?: return Resource.Error("Order not found")
            
            val now = System.currentTimeMillis()
            val otp = generateOtp()
            
            val confirmation = DeliveryConfirmationEntity(
                confirmationId = UUID.randomUUID().toString(),
                orderId = orderId,
                buyerId = order.buyerId ?: "",
                sellerId = order.sellerId,
                deliveryOtp = otp,
                otpGeneratedAt = now,
                otpExpiresAt = now + (4 * 60 * 60 * 1000L), // 4 hours
                status = "PENDING",
                createdAt = now,
                updatedAt = now
            )
            confirmationDao.upsert(confirmation)
            
            logAction(orderId, "OTP_GENERATED", null, null,
                "SYSTEM", "SYSTEM", "Delivery OTP generated")
            
            Resource.Success(otp)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to generate OTP")
        }
    }
    
    override suspend fun verifyDeliveryOtp(
        orderId: String, 
        otp: String, 
        confirmedBy: String,
        verifierLat: Double?,
        verifierLng: Double?
    ): Resource<Unit> {
        return try {
            val confirmation = confirmationDao.getByOrderId(orderId) 
                ?: return Resource.Error("Delivery confirmation not found")
            
            val now = System.currentTimeMillis()
            
            // Check expiry
            if (now > confirmation.otpExpiresAt) {
                return Resource.Error("OTP has expired")
            }
            
            // Check attempts
            if (confirmation.otpAttempts >= confirmation.maxOtpAttempts) {
                return Resource.Error("Maximum OTP attempts exceeded")
            }
            
            // Verify OTP
            if (confirmation.deliveryOtp != otp) {
                confirmationDao.incrementOtpAttempts(confirmation.confirmationId, now)
                return Resource.Error("Invalid OTP")
            }

            // GPS CHECK
            if (verifierLat != null && verifierLng != null) {
                val quote = quoteDao.getLatestQuote(orderId)
                if (quote != null && quote.deliveryLatitude != null && quote.deliveryLongitude != null) {
                    val distanceKm = LocationUtils.calculateDistance(
                        verifierLat, verifierLng,
                        quote.deliveryLatitude, quote.deliveryLongitude
                    )
                    
                    // Allow 0.5km (500m) radius
                    if (distanceKm > 0.5) {
                        return Resource.Error("Location verification failed. You are ${LocationUtils.formatDistance(distanceKm)} away from delivery location.")
                    }
                }
            }
            
            confirmationDao.confirmWithOtp(confirmation.confirmationId, confirmedBy, now)
            
            transitionOrderStateInternal(
                orderId,
                EvidenceOrderStatus.DELIVERED,
                confirmedBy,
                "SELLER",
                "Delivery confirmed via OTP"
            )
            
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to verify OTP")
        }
    }
    
    override suspend fun confirmDeliveryWithPhoto(
        orderId: String,
        deliveryPhotoId: String,
        buyerPhotoId: String?,
        confirmedBy: String
    ): Resource<Unit> {
        return try {
            val confirmation = confirmationDao.getByOrderId(orderId) 
                ?: return Resource.Error("Delivery confirmation not found")
            
            val now = System.currentTimeMillis()
            confirmationDao.confirmWithPhoto(
                confirmation.confirmationId,
                deliveryPhotoId,
                buyerPhotoId,
                confirmedBy,
                now
            )
            
            transitionOrderStateInternal(
                orderId,
                EvidenceOrderStatus.DELIVERED,
                confirmedBy,
                "BUYER",
                "Delivery confirmed via photo"
            )
            
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to confirm delivery")
        }
    }
    
    override suspend fun markBalanceCollected(orderId: String, evidenceId: String?): Resource<Unit> {
        return try {
            val confirmation = confirmationDao.getByOrderId(orderId) 
                ?: return Resource.Error("Delivery confirmation not found")
            
            val now = System.currentTimeMillis()
            confirmationDao.markBalanceCollected(confirmation.confirmationId, evidenceId, now)
            
            // Complete the order
            transitionOrderStateInternal(
                orderId,
                EvidenceOrderStatus.COMPLETED,
                confirmation.sellerId,
                "SELLER",
                "Balance collected, order completed"
            )
            
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to mark balance collected")
        }
    }

    // ==================== ORDER STATE MACHINE ====================
    
    override suspend fun transitionOrderState(
        orderId: String,
        newStatus: EvidenceOrderStatus,
        performedBy: String,
        performedByRole: String,
        notes: String?
    ): Resource<Unit> = transitionOrderStateInternal(orderId, newStatus, performedBy, performedByRole, notes)
    
    private suspend fun transitionOrderStateInternal(
        orderId: String,
        newStatus: EvidenceOrderStatus,
        performedBy: String,
        performedByRole: String,
        notes: String?
    ): Resource<Unit> {
        return try {
            val order = orderDao.findById(orderId) 
                ?: return Resource.Error("Order not found")
            
            val currentStatus = EvidenceOrderStatus.fromString(order.status)
            
            // Validate transition
            if (!EvidenceOrderStatus.canTransition(currentStatus, newStatus)) {
                return Resource.Error("Invalid transition from ${currentStatus.value} to ${newStatus.value}")
            }
            
            val now = System.currentTimeMillis()
            val updated = order.copy(
                status = newStatus.value,
                updatedAt = now,
                lastModifiedAt = now,
                dirty = true
            )
            orderDao.insertOrUpdate(updated)
            
            logAction(orderId, "STATE_CHANGE", currentStatus.value, newStatus.value,
                performedBy, performedByRole, notes ?: "Status changed to ${newStatus.displayName}")
            
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to transition order state")
        }
    }

    // ==================== DISPUTES ====================
    
    override suspend fun raiseDispute(
        orderId: String,
        raisedBy: String,
        raisedByRole: String,
        reason: DisputeReason,
        description: String,
        requestedResolution: String?,
        claimedAmount: Double?,
        evidenceIds: List<String>?
    ): Resource<OrderDisputeEntity> {
        return try {
            val order = orderDao.findById(orderId) 
                ?: return Resource.Error("Order not found")
            
            val againstUser = if (raisedByRole == "BUYER") order.sellerId else order.buyerId ?: ""
            
            val now = System.currentTimeMillis()
            val dispute = OrderDisputeEntity(
                disputeId = UUID.randomUUID().toString(),
                orderId = orderId,
                raisedBy = raisedBy,
                raisedByRole = raisedByRole,
                againstUserId = againstUser,
                reason = reason.value,
                description = description,
                requestedResolution = requestedResolution,
                claimedAmount = claimedAmount,
                evidenceIds = evidenceIds?.joinToString(","),
                status = "OPEN",
                createdAt = now,
                updatedAt = now
            )
            disputeDao.upsert(dispute)
            
            transitionOrderStateInternal(
                orderId,
                EvidenceOrderStatus.DISPUTE,
                raisedBy,
                raisedByRole,
                "Dispute raised: ${reason.displayName}"
            )
            
            Resource.Success(dispute)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to raise dispute")
        }
    }
    
    override suspend fun escalateDispute(disputeId: String, reason: String): Resource<Unit> {
        return try {
            val now = System.currentTimeMillis()
            disputeDao.escalate(disputeId, reason, now)
            
            val dispute = disputeDao.findById(disputeId)
            if (dispute != null) {
                transitionOrderStateInternal(
                    dispute.orderId,
                    EvidenceOrderStatus.ESCALATED,
                    "SYSTEM",
                    "SYSTEM",
                    "Dispute escalated to admin: $reason"
                )
            }
            
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to escalate dispute")
        }
    }
    
    override suspend fun resolveDispute(
        disputeId: String,
        resolvedBy: String,
        resolutionType: String,
        notes: String?,
        refundAmount: Double?
    ): Resource<Unit> {
        return try {
            val now = System.currentTimeMillis()
            disputeDao.resolve(disputeId, resolvedBy, resolutionType, notes, refundAmount, now)
            
            val dispute = disputeDao.findById(disputeId)
            if (dispute != null) {
                val finalStatus = if (resolutionType == "MUTUAL_CANCEL") 
                    EvidenceOrderStatus.CANCELLED else EvidenceOrderStatus.COMPLETED
                
                transitionOrderStateInternal(
                    dispute.orderId,
                    finalStatus,
                    resolvedBy,
                    "ADMIN",
                    "Dispute resolved: $resolutionType"
                )
            }
            
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to resolve dispute")
        }
    }

    override suspend fun submitReview(
        orderId: String,
        reviewerId: String,
        rating: Int,
        content: String?,
        wouldRecommend: Boolean?
    ): Resource<Unit> {
        return try {
            val order = orderDao.findById(orderId) 
                ?: return Resource.Error("Order not found")

            val now = System.currentTimeMillis()
            val finalContent = StringBuilder().apply {
                if (!content.isNullOrBlank()) append(content).append("\n\n")
                if (wouldRecommend != null) append("Recommended: ").append(if (wouldRecommend) "Yes" else "No")
            }.toString()

            // Try to find the product ID from the quote if possible
            // val quote = quoteDao.findByOrderId(orderId) 
            
            val review = ReviewEntity(
                reviewId = UUID.randomUUID().toString(),
                productId = null, 
                sellerId = order.sellerId,
                orderId = orderId,
                reviewerId = reviewerId,
                rating = rating,
                title = "Verified Order Review",
                content = finalContent.ifBlank { null },
                isVerifiedPurchase = true,
                createdAt = now,
                updatedAt = now,
                dirty = true,
                responseFromSeller = null,
                responseAt = null
            )
            
            reviewDao.upsert(review)
            
            logAction(orderId, "REVIEW_SUBMITTED", null, null,
                reviewerId, "BUYER", "Review submitted: $rating stars")
                
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to submit review")
        }
    }
    
    override fun getUserActiveDisputes(userId: String): Flow<List<OrderDisputeEntity>> =
        disputeDao.getUserActiveDisputes(userId)

    // ==================== QUERIES ====================
    
    override fun getBuyerActiveQuotes(buyerId: String): Flow<List<OrderQuoteEntity>> =
        quoteDao.getBuyerActiveQuotes(buyerId)
    
    override fun getSellerActiveQuotes(sellerId: String): Flow<List<OrderQuoteEntity>> =
        quoteDao.getSellerActiveQuotes(sellerId)
    
    override fun getOrderAuditTrail(orderId: String): Flow<List<OrderAuditLogEntity>> =
        auditLogDao.getOrderAuditTrail(orderId)
    
    override fun getPaymentsAwaitingVerification(sellerId: String): Flow<List<OrderPaymentEntity>> =
        paymentDao.getPaymentsAwaitingVerification(sellerId)
    
    override fun getPendingPaymentsForBuyer(buyerId: String): Flow<List<OrderPaymentEntity>> =
        paymentDao.getPendingPaymentsForBuyer(buyerId)

    // ==================== TIMEOUT HANDLERS ====================
    
    override suspend fun expireOldQuotes(now: Long): Resource<Int> {
        return try {
            val expiredCount = quoteDao.expireOldQuotes(now)
            Resource.Success(expiredCount)
        } catch (e: Exception) {
            Resource.Error("Failed to expire quotes: ${e.message}")
        }
    }
    
    override suspend fun expireOverduePayments() {
        val now = System.currentTimeMillis()
        paymentDao.expireOverduePayments(now)
    }
    
    override suspend fun escalateDispute(disputeId: String): Resource<Unit> {
        return try {
            val dispute = disputeDao.findById(disputeId) ?: return Resource.Error("Dispute not found")
            
            disputeDao.escalate(
                disputeId = disputeId,
                reason = "Auto-escalated after 3 days without resolution",
                escalatedAt = System.currentTimeMillis()
            )
            
            logAction(
                orderId = dispute.orderId,
                action = "DISPUTE_ESCALATED",
                fromState = dispute.status,
                toState = "ESCALATED",
                performedBy = "SYSTEM",
                performedByRole = "SYSTEM",
                description = "Dispute automatically escalated after 3 days without resolution"
            )
            
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error("Failed to escalate dispute: ${e.message}")
        }
    }

    // ==================== HELPERS ====================
    
    private fun generateOtp(): String = 
        (100000 + Random.nextInt(900000)).toString()
    
    private fun calculateAdvance(paymentType: String, total: Double): Double? {
        return when (OrderPaymentType.fromString(paymentType)) {
            OrderPaymentType.FULL_ADVANCE -> total
            OrderPaymentType.SPLIT_50_50 -> total * 0.5
            else -> null
        }
    }
    
    private fun calculateBalance(paymentType: String, total: Double): Double? {
        return when (OrderPaymentType.fromString(paymentType)) {
            OrderPaymentType.SPLIT_50_50 -> total * 0.5
            OrderPaymentType.COD, OrderPaymentType.BUYER_PICKUP -> total
            else -> null
        }
    }
    
    private suspend fun logAction(
        orderId: String,
        action: String,
        fromState: String?,
        toState: String?,
        performedBy: String,
        performedByRole: String,
        description: String
    ) {
        val log = OrderAuditLogEntity(
            logId = UUID.randomUUID().toString(),
            orderId = orderId,
            action = action,
            fromState = fromState,
            toState = toState,
            performedBy = performedBy,
            performedByRole = performedByRole,
            description = description,
            timestamp = System.currentTimeMillis()
        )
        auditLogDao.insert(log)
    }
}
