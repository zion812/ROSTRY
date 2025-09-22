package com.rio.rostry.utils.moderation

object ContentValidation {
    private val spamKeywords = listOf(
        "free coins", "earn money", "click here", "visit my profile", "xxx", "adult", "loan", "bet", "gambling"
    )

    fun isSpamText(text: String): Boolean {
        val lower = text.lowercase()
        if (lower.count { it == '!' } > 6) return true
        if (lower.windowed(2).any { it[0].isLetter() && it[1].isLetter() && it[0] == it[1] && it[0].isUpperCase() }) return true
        if (spamKeywords.any { lower.contains(it) }) return true
        val urlRegex = "https?://".toRegex()
        val urlCount = urlRegex.findAll(lower).count()
        if (urlCount >= 3) return true
        return false
    }

    data class ValidationResult(val ok: Boolean, val reason: String?)

    fun validatePostText(text: String?): ValidationResult {
        val t = text?.trim().orEmpty()
        if (t.isEmpty()) return ValidationResult(false, "Empty text")
        if (t.length > 5000) return ValidationResult(false, "Text too long")
        if (isSpamText(t)) return ValidationResult(false, "Spam detected")
        return ValidationResult(true, null)
    }

    fun validateCommentText(text: String): ValidationResult {
        if (text.isBlank()) return ValidationResult(false, "Empty comment")
        if (text.length > 2000) return ValidationResult(false, "Comment too long")
        if (isSpamText(text)) return ValidationResult(false, "Spam detected")
        return ValidationResult(true, null)
    }
}
