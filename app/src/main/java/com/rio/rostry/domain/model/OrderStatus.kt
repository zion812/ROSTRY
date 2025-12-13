package com.rio.rostry.domain.model

enum class OrderStatus(val displayName: String) {
    PENDING_PAYMENT("Pending Payment"),
    PLACED("Placed"),
    CONFIRMED("Confirmed"),
    PROCESSING("Processing"),
    OUT_FOR_DELIVERY("Out for Delivery"),
    DELIVERED("Delivered"),
    CANCELLED("Cancelled"),
    REFUNDED("Refunded");

    val isCancellable: Boolean
        get() = this in listOf(PLACED, CONFIRMED, PROCESSING)

    val isTerminal: Boolean
        get() = this in listOf(DELIVERED, CANCELLED, REFUNDED)

    fun toStoredString(): String = name

    companion object {
        fun fromString(value: String): OrderStatus {
            return try {
                valueOf(value.uppercase())
            } catch (e: IllegalArgumentException) {
                PENDING_PAYMENT
            }
        }
    }
}
