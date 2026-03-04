package com.rio.rostry.utils

import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

object CurrencyUtils {
    private val locale = Locale("en", "IN")
    private val currency = Currency.getInstance("INR")
    private val formatter = NumberFormat.getCurrencyInstance(locale).apply {
        maximumFractionDigits = 0
        this.currency = CurrencyUtils.currency
    }

    fun formatPrice(amount: Double): String {
        return formatter.format(amount)
    }
}
