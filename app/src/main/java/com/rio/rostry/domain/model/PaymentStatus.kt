package com.rio.rostry.domain.model

enum class PaymentStatus(val displayName: String) {
    PENDING("Pending"),
    SUCCESS("Success"),
    FAILED("Failed"),
    REFUNDED("Refunded");
    
    /**
     * Companion object for PaymentStatus.
     * Supports legacy values like "Paid", "PAID", "PAID_SUCCESS" which are normalized to canonical enum values.
     */
    companion object {
        fun fromString(value: String): PaymentStatus = when (value.uppercase()) {
            "PENDING" -> PENDING
            "SUCCESS" -> SUCCESS
            "PAID" -> SUCCESS
            "PAID_SUCCESS" -> SUCCESS
            "FAILED" -> FAILED
            "REFUNDED" -> REFUNDED
            else -> PENDING
        }
    }

    fun toStoredString(): String = name

    val isPaid: Boolean get() = this == SUCCESS || this == REFUNDED

    val isError: Boolean get() = this == FAILED
}
