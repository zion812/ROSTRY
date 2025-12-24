package com.rio.rostry.data.database.dao

import androidx.room.*
import com.rio.rostry.data.database.entity.*
import kotlinx.coroutines.flow.Flow

/**
 * DAO for Order Evidence operations.
 */
@Dao
interface OrderEvidenceDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(evidence: OrderEvidenceEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(evidences: List<OrderEvidenceEntity>)
    
    @Query("SELECT * FROM order_evidence WHERE evidenceId = :evidenceId AND isDeleted = 0")
    suspend fun findById(evidenceId: String): OrderEvidenceEntity?
    
    @Query("SELECT * FROM order_evidence WHERE orderId = :orderId AND isDeleted = 0 ORDER BY createdAt DESC")
    fun getOrderEvidence(orderId: String): Flow<List<OrderEvidenceEntity>>
    
    @Query("SELECT * FROM order_evidence WHERE orderId = :orderId AND evidenceType = :type AND isDeleted = 0")
    suspend fun getEvidenceByType(orderId: String, type: String): List<OrderEvidenceEntity>
    
    @Query("SELECT * FROM order_evidence WHERE orderId = :orderId AND uploadedBy = :userId AND isDeleted = 0")
    fun getEvidenceByUser(orderId: String, userId: String): Flow<List<OrderEvidenceEntity>>
    
    @Query("SELECT * FROM order_evidence WHERE orderId = :orderId AND isVerified = 0 AND isDeleted = 0")
    fun getUnverifiedEvidence(orderId: String): Flow<List<OrderEvidenceEntity>>
    
    @Query("UPDATE order_evidence SET isVerified = 1, verifiedBy = :verifiedBy, verifiedAt = :verifiedAt, verificationNote = :note, updatedAt = :updatedAt, dirty = 1 WHERE evidenceId = :evidenceId")
    suspend fun markVerified(evidenceId: String, verifiedBy: String, verifiedAt: Long, note: String?, updatedAt: Long)
    
    @Query("UPDATE order_evidence SET isDeleted = 1, updatedAt = :deletedAt, dirty = 1 WHERE evidenceId = :evidenceId")
    suspend fun softDelete(evidenceId: String, deletedAt: Long)
    
    @Query("SELECT * FROM order_evidence WHERE dirty = 1")
    suspend fun getDirtyRecords(): List<OrderEvidenceEntity>
    
    @Query("UPDATE order_evidence SET dirty = 0 WHERE evidenceId = :evidenceId")
    suspend fun markClean(evidenceId: String)
}

/**
 * DAO for Order Quote operations.
 */
@Dao
interface OrderQuoteDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(quote: OrderQuoteEntity)
    
    @Query("SELECT * FROM order_quotes WHERE quoteId = :quoteId")
    suspend fun findById(quoteId: String): OrderQuoteEntity?
    
    @Query("SELECT * FROM order_quotes WHERE orderId = :orderId ORDER BY version DESC LIMIT 1")
    suspend fun getLatestQuote(orderId: String): OrderQuoteEntity?
    
    @Query("SELECT * FROM order_quotes WHERE orderId = :orderId ORDER BY version DESC")
    fun getQuoteHistory(orderId: String): Flow<List<OrderQuoteEntity>>
    
    @Query("SELECT * FROM order_quotes WHERE buyerId = :buyerId AND status NOT IN ('EXPIRED', 'REJECTED') ORDER BY createdAt DESC")
    fun getBuyerActiveQuotes(buyerId: String): Flow<List<OrderQuoteEntity>>
    
    @Query("SELECT * FROM order_quotes WHERE sellerId = :sellerId AND status NOT IN ('EXPIRED', 'REJECTED') ORDER BY createdAt DESC")
    fun getSellerActiveQuotes(sellerId: String): Flow<List<OrderQuoteEntity>>
    
    @Query("SELECT * FROM order_quotes WHERE status = :status ORDER BY createdAt DESC")
    fun getQuotesByStatus(status: String): Flow<List<OrderQuoteEntity>>
    
    @Query("UPDATE order_quotes SET status = :status, updatedAt = :updatedAt, dirty = 1 WHERE quoteId = :quoteId")
    suspend fun updateStatus(quoteId: String, status: String, updatedAt: Long)
    
    @Query("UPDATE order_quotes SET buyerAgreedAt = :agreedAt, status = CASE WHEN sellerAgreedAt IS NOT NULL THEN 'LOCKED' ELSE 'BUYER_AGREED' END, lockedAt = CASE WHEN sellerAgreedAt IS NOT NULL THEN :agreedAt ELSE NULL END, updatedAt = :agreedAt, dirty = 1 WHERE quoteId = :quoteId")
    suspend fun buyerAgree(quoteId: String, agreedAt: Long)
    
    @Query("UPDATE order_quotes SET sellerAgreedAt = :agreedAt, status = CASE WHEN buyerAgreedAt IS NOT NULL THEN 'LOCKED' ELSE 'SELLER_AGREED' END, lockedAt = CASE WHEN buyerAgreedAt IS NOT NULL THEN :agreedAt ELSE NULL END, updatedAt = :agreedAt, dirty = 1 WHERE quoteId = :quoteId")
    suspend fun sellerAgree(quoteId: String, agreedAt: Long)
    
    @Query("UPDATE order_quotes SET status = 'EXPIRED', updatedAt = :expiredAt, dirty = 1 WHERE expiresAt < :expiredAt AND status NOT IN ('LOCKED', 'EXPIRED', 'REJECTED')")
    suspend fun expireOldQuotes(expiredAt: Long)
    
    @Query("SELECT * FROM order_quotes WHERE dirty = 1")
    suspend fun getDirtyRecords(): List<OrderQuoteEntity>
    
    @Query("UPDATE order_quotes SET dirty = 0 WHERE quoteId = :quoteId")
    suspend fun markClean(quoteId: String)
}

/**
 * DAO for Order Payment tracking.
 */
@Dao
interface OrderPaymentDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(payment: OrderPaymentEntity)
    
    @Query("SELECT * FROM order_payments WHERE paymentId = :paymentId")
    suspend fun findById(paymentId: String): OrderPaymentEntity?
    
    @Query("SELECT * FROM order_payments WHERE orderId = :orderId ORDER BY createdAt")
    fun getOrderPayments(orderId: String): Flow<List<OrderPaymentEntity>>
    
    @Query("SELECT * FROM order_payments WHERE orderId = :orderId AND paymentPhase = :phase LIMIT 1")
    suspend fun getPaymentByPhase(orderId: String, phase: String): OrderPaymentEntity?
    
    @Query("SELECT * FROM order_payments WHERE payerId = :payerId AND status = 'PENDING' ORDER BY dueAt")
    fun getPendingPayments(payerId: String): Flow<List<OrderPaymentEntity>>
    
    @Query("SELECT * FROM order_payments WHERE receiverId = :receiverId AND status = 'PROOF_SUBMITTED' ORDER BY updatedAt DESC")
    fun getPaymentsAwaitingVerification(receiverId: String): Flow<List<OrderPaymentEntity>>
    
    @Query("UPDATE order_payments SET status = 'PROOF_SUBMITTED', proofEvidenceId = :evidenceId, transactionRef = :transactionRef, updatedAt = :updatedAt, dirty = 1 WHERE paymentId = :paymentId")
    suspend fun submitProof(paymentId: String, evidenceId: String, transactionRef: String?, updatedAt: Long)
    
    @Query("UPDATE order_payments SET status = 'VERIFIED', verifiedAt = :verifiedAt, verifiedBy = :verifiedBy, updatedAt = :verifiedAt, dirty = 1 WHERE paymentId = :paymentId")
    suspend fun markVerified(paymentId: String, verifiedBy: String, verifiedAt: Long)
    
    @Query("UPDATE order_payments SET status = 'REJECTED', rejectionReason = :reason, updatedAt = :updatedAt, dirty = 1 WHERE paymentId = :paymentId")
    suspend fun markRejected(paymentId: String, reason: String, updatedAt: Long)
    
    @Query("UPDATE order_payments SET status = 'EXPIRED', expiredAt = :expiredAt, dirty = 1 WHERE dueAt < :expiredAt AND status = 'PENDING'")
    suspend fun expireOverduePayments(expiredAt: Long)
    
    @Query("SELECT SUM(amount) FROM order_payments WHERE orderId = :orderId AND status = 'VERIFIED'")
    suspend fun getTotalVerifiedAmount(orderId: String): Double?
    
    @Query("SELECT * FROM order_payments WHERE dirty = 1")
    suspend fun getDirtyRecords(): List<OrderPaymentEntity>
    
    @Query("UPDATE order_payments SET dirty = 0 WHERE paymentId = :paymentId")
    suspend fun markClean(paymentId: String)
}

/**
 * DAO for Delivery Confirmation.
 */
@Dao
interface DeliveryConfirmationDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(confirmation: DeliveryConfirmationEntity)
    
    @Query("SELECT * FROM delivery_confirmations WHERE confirmationId = :confirmationId")
    suspend fun findById(confirmationId: String): DeliveryConfirmationEntity?
    
    @Query("SELECT * FROM delivery_confirmations WHERE orderId = :orderId LIMIT 1")
    suspend fun getByOrderId(orderId: String): DeliveryConfirmationEntity?
    
    @Query("SELECT * FROM delivery_confirmations WHERE orderId = :orderId")
    fun observeByOrderId(orderId: String): Flow<DeliveryConfirmationEntity?>
    
    @Query("SELECT * FROM delivery_confirmations WHERE buyerId = :buyerId AND status = 'PENDING' ORDER BY createdAt DESC")
    fun getPendingConfirmations(buyerId: String): Flow<List<DeliveryConfirmationEntity>>
    
    @Query("UPDATE delivery_confirmations SET otpAttempts = otpAttempts + 1, updatedAt = :updatedAt, dirty = 1 WHERE confirmationId = :confirmationId")
    suspend fun incrementOtpAttempts(confirmationId: String, updatedAt: Long)
    
    @Query("UPDATE delivery_confirmations SET status = 'OTP_VERIFIED', confirmationMethod = 'OTP', confirmedAt = :confirmedAt, confirmedBy = :confirmedBy, updatedAt = :confirmedAt, dirty = 1 WHERE confirmationId = :confirmationId")
    suspend fun confirmWithOtp(confirmationId: String, confirmedBy: String, confirmedAt: Long)
    
    @Query("UPDATE delivery_confirmations SET status = 'PHOTO_CONFIRMED', confirmationMethod = 'PHOTO', deliveryPhotoEvidenceId = :photoEvidenceId, buyerConfirmationEvidenceId = :buyerPhotoEvidenceId, confirmedAt = :confirmedAt, confirmedBy = :confirmedBy, updatedAt = :confirmedAt, dirty = 1 WHERE confirmationId = :confirmationId")
    suspend fun confirmWithPhoto(confirmationId: String, photoEvidenceId: String?, buyerPhotoEvidenceId: String?, confirmedBy: String, confirmedAt: Long)
    
    @Query("UPDATE delivery_confirmations SET balanceCollected = 1, balanceCollectedAt = :collectedAt, balanceEvidenceId = :evidenceId, updatedAt = :collectedAt, dirty = 1 WHERE confirmationId = :confirmationId")
    suspend fun markBalanceCollected(confirmationId: String, evidenceId: String?, collectedAt: Long)
    
    @Query("SELECT * FROM delivery_confirmations WHERE dirty = 1")
    suspend fun getDirtyRecords(): List<DeliveryConfirmationEntity>
    
    @Query("UPDATE delivery_confirmations SET dirty = 0 WHERE confirmationId = :confirmationId")
    suspend fun markClean(confirmationId: String)
}

/**
 * DAO for Order Disputes.
 */
@Dao
interface OrderDisputeDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(dispute: OrderDisputeEntity)
    
    @Query("SELECT * FROM order_disputes WHERE disputeId = :disputeId")
    suspend fun findById(disputeId: String): OrderDisputeEntity?
    
    @Query("SELECT * FROM order_disputes WHERE orderId = :orderId ORDER BY createdAt DESC")
    fun getOrderDisputes(orderId: String): Flow<List<OrderDisputeEntity>>
    
    @Query("SELECT * FROM order_disputes WHERE raisedBy = :userId AND status NOT IN ('RESOLVED', 'CLOSED') ORDER BY createdAt DESC")
    fun getUserActiveDisputes(userId: String): Flow<List<OrderDisputeEntity>>
    
    @Query("SELECT * FROM order_disputes WHERE status IN ('OPEN', 'UNDER_REVIEW', 'AWAITING_RESPONSE') ORDER BY createdAt")
    fun getAllOpenDisputes(): Flow<List<OrderDisputeEntity>>
    
    @Query("SELECT * FROM order_disputes WHERE status = 'ESCALATED' ORDER BY escalatedAt")
    fun getEscalatedDisputes(): Flow<List<OrderDisputeEntity>>
    
    @Query("UPDATE order_disputes SET status = :status, updatedAt = :updatedAt, dirty = 1 WHERE disputeId = :disputeId")
    suspend fun updateStatus(disputeId: String, status: String, updatedAt: Long)
    
    @Query("UPDATE order_disputes SET status = 'ESCALATED', escalatedAt = :escalatedAt, escalationReason = :reason, updatedAt = :escalatedAt, dirty = 1 WHERE disputeId = :disputeId")
    suspend fun escalate(disputeId: String, reason: String, escalatedAt: Long)
    
    @Query("UPDATE order_disputes SET status = 'RESOLVED', resolvedAt = :resolvedAt, resolvedBy = :resolvedBy, resolutionType = :resolutionType, resolutionNotes = :notes, refundedAmount = :refundAmount, updatedAt = :resolvedAt, dirty = 1 WHERE disputeId = :disputeId")
    suspend fun resolve(disputeId: String, resolvedBy: String, resolutionType: String, notes: String?, refundAmount: Double?, resolvedAt: Long)
    
    @Query("SELECT * FROM order_disputes WHERE dirty = 1")
    suspend fun getDirtyRecords(): List<OrderDisputeEntity>
    
    @Query("UPDATE order_disputes SET dirty = 0 WHERE disputeId = :disputeId")
    suspend fun markClean(disputeId: String)
}

/**
 * DAO for Order Audit Logs.
 */
@Dao
interface OrderAuditLogDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(log: OrderAuditLogEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(logs: List<OrderAuditLogEntity>)
    
    @Query("SELECT * FROM order_audit_logs WHERE orderId = :orderId ORDER BY timestamp DESC")
    fun getOrderAuditTrail(orderId: String): Flow<List<OrderAuditLogEntity>>
    
    @Query("SELECT * FROM order_audit_logs WHERE orderId = :orderId AND action = :action ORDER BY timestamp DESC")
    fun getAuditByAction(orderId: String, action: String): Flow<List<OrderAuditLogEntity>>
    
    @Query("SELECT * FROM order_audit_logs WHERE performedBy = :userId ORDER BY timestamp DESC LIMIT :limit")
    fun getUserAuditLogs(userId: String, limit: Int): Flow<List<OrderAuditLogEntity>>
    
    @Query("DELETE FROM order_audit_logs WHERE timestamp < :beforeTimestamp")
    suspend fun deleteOldLogs(beforeTimestamp: Long)
}
