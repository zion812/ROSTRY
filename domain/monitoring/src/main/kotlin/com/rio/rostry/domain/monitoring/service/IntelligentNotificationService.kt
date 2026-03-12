package com.rio.rostry.domain.monitoring.service

import kotlinx.coroutines.flow.Flow

/**
 * Domain interface for the intelligent notification dispatch service.
 *
 * Handles the creation, batching, and delivery of context-aware notifications
 * across farm, transfer, order, social, verification, and lifecycle domains.
 */
interface IntelligentNotificationService {

    suspend fun notifyFarmEvent(
        type: String,
        productId: String,
        title: String,
        message: String,
        metadata: Map<String, Any>? = null
    )

    suspend fun notifyTransferEvent(
        type: String,
        transferId: String,
        title: String,
        message: String
    )

    suspend fun notifyOrderUpdate(orderId: String, status: String, title: String, message: String)

    suspend fun notifyOnboardingComplete(productId: String, productName: String, taskCount: Int, isBatch: Boolean)

    suspend fun notifyComplianceIssue(productId: String, productName: String)

    suspend fun notifyGoalProgress(goalType: String, progress: Int)

    suspend fun notifyVerificationEvent(type: String, userId: String, title: String, message: String)

    suspend fun notifySocialEvent(type: String, refId: String, title: String, message: String, fromUser: String)

    suspend fun flushBatchedNotifications()

    fun getBatchedCount(): Flow<Int>
}
