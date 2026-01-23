package com.rio.rostry.domain.social

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ModerationService @Inject constructor() {

    // MVP: Hardcoded blocklist. In production, fetch from server/DB.
    private val prohibitedWords = listOf(
        "spam", "scam", "fraud", "exploit", "abuse", 
        "fake", "malware", "virus", "phishing"
        // Add more offensive terms as needed
    )

    fun containsProhibitedContent(text: String?): Boolean {
        if (text.isNullOrBlank()) return false
        val normalized = text.lowercase()
        return prohibitedWords.any { word -> normalized.contains(word) }
    }

    fun validateContent(text: String?) {
        if (containsProhibitedContent(text)) {
            throw SecurityException("Content contains prohibited terms and violates community guidelines.")
        }
    }
}
