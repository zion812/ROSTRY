package com.rio.rostry.utils

private val INDIAN_PHONE_REGEX = Regex("^\\+91\\d{10}$")
private val DIGITS_REGEX = Regex("\\d+")

fun normalizeToE164India(input: String): String? {
    val digits = DIGITS_REGEX.findAll(input).joinToString("") { it.value }
    // If already starts with 91 and length 12 (without +), add plus
    return when {
        input.startsWith("+91") && INDIAN_PHONE_REGEX.matches(input) -> input
        digits.length == 10 -> "+91$digits"
        digits.length == 12 && digits.startsWith("91") -> "+$digits"
        else -> null
    }
}

fun isValidIndianPhone(input: String): Boolean = normalizeToE164India(input) != null
