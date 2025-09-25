package com.rio.rostry.marketplace.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class PaymentType {
    @SerialName("FIXED_PRICE")
    FIXED_PRICE,

    @SerialName("AUCTION_BID")
    AUCTION_BID,

    @SerialName("COD")
    COD,

    @SerialName("ADVANCE_PAYMENT")
    ADVANCE_PAYMENT;

    companion object {
        fun fromString(value: String?): PaymentType? = when (value?.uppercase()) {
            "FIXED_PRICE", "FIXEDPRICE", "BUY_NOW" -> FIXED_PRICE
            "AUCTION_BID", "AUCTION" -> AUCTION_BID
            "COD", "CASH_ON_DELIVERY" -> COD
            "ADVANCE_PAYMENT", "ADVANCE", "PREORDER" -> ADVANCE_PAYMENT
            else -> null
        }
    }
}
