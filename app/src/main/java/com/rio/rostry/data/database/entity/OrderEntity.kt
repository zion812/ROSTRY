package com.rio.rostry.data.database.entity

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.rio.rostry.domain.model.OrderStatus
import com.rio.rostry.domain.model.PaymentStatus

@Entity(
    tableName = "orders",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["userId"],
            childColumns = ["buyerId"],
            onDelete = ForeignKey.SET_NULL // Or CASCADE, depending on business logic if a user is deleted
        )
    ],
    indices = [Index(value = ["buyerId"]), Index(value = ["sellerId"])]
)
@Keep
data class OrderEntity(
    @PrimaryKey val orderId: String = "",
    val buyerId: String? = null,
    val sellerId: String = "",
    val totalAmount: Double = 0.0,
    /**
     * Allowed values: "PENDING_PAYMENT", "PLACED", "CONFIRMED", "PROCESSING", "OUT_FOR_DELIVERY", "DELIVERED", "CANCELLED", "REFUNDED".
     * Use OrderStatus enum for type-safe access.
     */
    val status: String = "",
    val shippingAddress: String = "",
    val paymentMethod: String? = null,
    /**
     * Allowed values: "PENDING", "SUCCESS", "FAILED", "REFUNDED".
     * Use PaymentStatus enum for type-safe access.
     */
    val paymentStatus: String = "pending",
    val orderDate: Long = 0L,
    val expectedDeliveryDate: Long? = null,
    val actualDeliveryDate: Long? = null,
    val notes: String? = null,
    val createdAt: Long = 0L,
    val updatedAt: Long = 0L,
    val lastModifiedAt: Long = 0L,
    val isDeleted: Boolean = false,
    val deletedAt: Long? = null,
    val dirty: Boolean = false,
    // Delivery & Negotiation
    val deliveryType: String? = null, // "SELF_PICKUP" or "FARMER_DELIVERY"
    val negotiationStatus: String? = null, // "REQUESTED", "NEGOTIATING", "AGREED"
    val negotiatedPrice: Double? = null,
    val originalPrice: Double? = null,
    val cancellationReason: String? = null,
    val cancellationTime: Long? = null,
    val billImageUri: String? = null,
    val paymentSlipUri: String? = null
)

fun OrderEntity.statusEnum(): OrderStatus = OrderStatus.fromString(this.status)

fun OrderEntity.withStatus(newStatus: OrderStatus): OrderEntity = this.copy(status = newStatus.toStoredString())

fun OrderEntity.paymentStatusEnum(): PaymentStatus = PaymentStatus.fromString(this.paymentStatus)

fun OrderEntity.withPaymentStatus(newStatus: PaymentStatus): OrderEntity = this.copy(paymentStatus = newStatus.toStoredString())