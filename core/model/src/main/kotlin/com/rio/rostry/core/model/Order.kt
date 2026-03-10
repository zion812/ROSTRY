package com.rio.rostry.core.model

/**
 * Domain model representing an order in the system.
 */
data class Order(
    val id: String,
    val buyerId: String,
    val buyerName: String?,
    val sellerId: String,
    val sellerName: String?,
    val items: List<OrderItem>,
    val status: OrderStatus,
    val subtotal: Double,
    val shippingCost: Double,
    val tax: Double,
    val total: Double,
    val currency: String = "USD",
    val shippingAddress: Address?,
    val billingAddress: Address?,
    val paymentMethod: String?,
    val paymentStatus: PaymentStatus,
    val notes: String?,
    val trackingNumber: String?,
    val estimatedDelivery: Long?,
    val createdAt: Long,
    val updatedAt: Long,
    val completedAt: Long?
) {
    /**
     * Checks if the order can be cancelled.
     */
    fun canCancel(): Boolean = status in listOf(
        OrderStatus.PENDING,
        OrderStatus.CONFIRMED,
        OrderStatus.PROCESSING
    )
    
    /**
     * Checks if the order can be modified.
     */
    fun canModify(): Boolean = status == OrderStatus.PENDING
    
    /**
     * Checks if the order is in a final state.
     */
    fun isFinal(): Boolean = status in listOf(
        OrderStatus.COMPLETED,
        OrderStatus.CANCELLED,
        OrderStatus.REFUNDED
    )
    
    /**
     * Gets the total number of items in the order.
     */
    fun getTotalItemCount(): Int = items.sumOf { it.quantity }
    
    /**
     * Calculates the order total from items.
     */
    fun calculateTotal(): Double = subtotal + shippingCost + tax
}

/**
 * Domain model representing an item within an order.
 */
data class OrderItem(
    val id: String,
    val orderId: String,
    val productId: String,
    val productName: String,
    val productImageUrl: String?,
    val price: Double,
    val quantity: Int,
    val subtotal: Double
) {
    /**
     * Validates that the subtotal matches price * quantity.
     */
    fun isValid(): Boolean = subtotal == price * quantity
}

/**
 * Order status enum.
 */
enum class OrderStatus {
    PENDING,
    CONFIRMED,
    PROCESSING,
    SHIPPED,
    DELIVERED,
    COMPLETED,
    CANCELLED,
    REFUNDED,
    DISPUTED
}

/**
 * Payment status enum.
 */
enum class PaymentStatus {
    PENDING,
    AUTHORIZED,
    CAPTURED,
    FAILED,
    REFUNDED,
    PARTIALLY_REFUNDED
}

/**
 * Address model for shipping and billing.
 */
data class Address(
    val street: String,
    val city: String,
    val state: String,
    val postalCode: String,
    val country: String,
    val phone: String?
) {
    /**
     * Formats the address as a single line.
     */
    fun toSingleLine(): String = "$street, $city, $state $postalCode, $country"
}
