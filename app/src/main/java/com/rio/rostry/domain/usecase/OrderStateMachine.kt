package com.rio.rostry.domain.usecase

import com.rio.rostry.data.database.dao.OrderDao
import com.rio.rostry.data.database.entity.OrderEntity
import timber.log.Timber
import javax.inject.Inject

/**
 * OrderStateMachine - Manages offline order status transitions with conflict resolution.
 * Uses Last-Write-Wins (LWW) based on updatedAt timestamps for sync conflicts.
 * 
 * Part of Phase 3: Order Fulfillment.
 * 
 * Order Flow:
 * PENDING → ADVANCE_PAID → CONFIRMED → DISPATCHED → DELIVERED → COMPLETED
 *           ↓              ↓           ↓           ↓
 *           CANCELLED ←←←←←←←←←←←←←←←←←←←←←←←←←←←
 */
class OrderStateMachine @Inject constructor(
    private val orderDao: OrderDao
) {
    companion object {
        // Valid status transitions
        private val VALID_TRANSITIONS = mapOf(
            "PENDING" to listOf("ADVANCE_PAID", "CANCELLED"),
            "ADVANCE_PAID" to listOf("CONFIRMED", "CANCELLED", "REFUND_REQUESTED"),
            "CONFIRMED" to listOf("DISPATCHED", "CANCELLED"),
            "DISPATCHED" to listOf("DELIVERED", "CANCELLED"),
            "DELIVERED" to listOf("COMPLETED"),
            "COMPLETED" to emptyList<String>(),
            "CANCELLED" to emptyList<String>(),
            "REFUND_REQUESTED" to listOf("REFUNDED", "CANCELLED")
        )

        // Terminal states that cannot transition further
        private val TERMINAL_STATES = setOf("COMPLETED", "CANCELLED", "REFUNDED")
    }

    /**
     * Transition an order to a new status.
     * Returns true if successful, false if the transition is invalid.
     */
    suspend fun transition(orderId: String, newStatus: String, actorId: String): TransitionResult {
        val order = orderDao.findById(orderId)
            ?: return TransitionResult.Error("Order not found")

        val currentStatus = order.status.ifBlank { "PENDING" }

        // Check if transition is valid
        if (!isValidTransition(currentStatus, newStatus)) {
            Timber.w("Invalid order transition: $currentStatus → $newStatus for order $orderId")
            return TransitionResult.InvalidTransition(currentStatus, newStatus)
        }

        // Check if order is in terminal state
        if (currentStatus in TERMINAL_STATES) {
            return TransitionResult.Error("Order is in terminal state: $currentStatus")
        }

        // Perform the transition
        val now = System.currentTimeMillis()
        val updatedOrder = order.copy(
            status = newStatus,
            updatedAt = now,
            dirty = true
        )

        try {
            orderDao.insertOrUpdate(updatedOrder)
            Timber.d("Order $orderId transitioned: $currentStatus → $newStatus by $actorId")
            return TransitionResult.Success(updatedOrder)
        } catch (e: Exception) {
            Timber.e(e, "Failed to transition order $orderId")
            return TransitionResult.Error(e.message ?: "Unknown error")
        }
    }

    /**
     * Verify delivery with OTP and mark order as COMPLETED.
     */
    suspend fun verifyDelivery(orderId: String, inputOtp: String, farmerId: String): TransitionResult {
        val order = orderDao.findById(orderId)
            ?: return TransitionResult.Error("Order not found")

        // Verify OTP
        val storedOtp = order.otp  // Using existing otp field
        if (storedOtp.isNullOrBlank()) {
            return TransitionResult.Error("No OTP generated for this order")
        }

        if (storedOtp != inputOtp) {
            Timber.w("OTP mismatch for order $orderId: expected $storedOtp, got $inputOtp")
            return TransitionResult.Error("Invalid OTP")
        }

        // Verify order is in DELIVERED state
        if (order.status != "DELIVERED") {
            return TransitionResult.Error("Order must be in DELIVERED state to verify")
        }

        // Mark as completed
        return transition(orderId, "COMPLETED", farmerId)
    }

    /**
     * Resolve sync conflict using Last-Write-Wins (LWW).
     * Returns the winning order based on updatedAt timestamp.
     */
    fun resolveConflict(local: OrderEntity, remote: OrderEntity): OrderEntity {
        // LWW: prefer the order with the most recent updatedAt
        return if (remote.updatedAt > local.updatedAt) {
            Timber.d("Conflict resolved: remote wins for order ${local.orderId}")
            remote
        } else {
            Timber.d("Conflict resolved: local wins for order ${local.orderId}")
            local
        }
    }

    private fun isValidTransition(currentStatus: String, newStatus: String): Boolean {
        val validNextStates = VALID_TRANSITIONS[currentStatus] ?: emptyList()
        return newStatus in validNextStates
    }

    /**
     * Get all valid next states for an order.
     */
    fun getValidNextStates(currentStatus: String): List<String> {
        return VALID_TRANSITIONS[currentStatus] ?: emptyList()
    }

    /**
     * Check if an order is in a terminal state.
     */
    fun isTerminal(status: String): Boolean {
        return status in TERMINAL_STATES
    }
}

sealed class TransitionResult {
    data class Success(val order: OrderEntity) : TransitionResult()
    data class InvalidTransition(val from: String, val to: String) : TransitionResult()
    data class Error(val message: String) : TransitionResult()
}
