package com.rio.rostry.utils

private val E164_REGEX = Regex("^\\+[1-9]\\d{1,14}$")
private val DIGITS_REGEX = Regex("\\d+")

/**
 * Normalize an input to E.164 if it already contains a leading '+' and valid digits.
 * This function does not guess country codes. Prefer providing country code in the UI.
 */
fun normalizeToE164(input: String): String? {
    val trimmed = input.trim()
    return if (E164_REGEX.matches(trimmed)) trimmed else null
}

/**
 * Validate whether the given input is a valid E.164 number.
 */
fun isValidE164(input: String): Boolean = normalizeToE164(input) != null

/**
 * Format a national number with a provided country code (e.g., "+1", "+91") into E.164.
 * Returns null if inputs are invalid or the resulting number doesn't match E.164.
 */
fun formatToE164(countryCode: String, nationalNumber: String): String? {
    val cc = countryCode.trim().let { if (it.startsWith("+")) it else "+$it" }
    if (!cc.matches(Regex("^\\+[1-9]\\d{0,3}$"))) return null
    val nn = nationalNumber.trim().filter { it.isDigit() }
    if (nn.isEmpty()) return null
    val merged = cc + nn
    return if (E164_REGEX.matches(merged)) merged else null
}
