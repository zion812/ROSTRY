package com.rio.rostry.domain.validation

/**
 * Sanitizes and validates text inputs to prevent injection attacks.
 */
class TextInputValidator : InputValidator<String> {

    override fun validate(value: String): InputValidationResult {
        val sanitized = sanitize(value)
        return if (sanitized.isBlank()) {
            InputValidationResult.Invalid(
                listOf(InputValidationError("text", "Text cannot be empty after sanitization", "TEXT_EMPTY"))
            )
        } else {
            InputValidationResult.Valid
        }
    }

    /**
     * Sanitize text input:
     * 1. Trim whitespace
     * 2. Remove control characters
     * 3. Remove SQL injection patterns
     * 4. Escape HTML special characters
     * 5. Normalize Unicode
     */
    fun sanitize(input: String): String {
        var result = input.trim()

        // Remove control characters (except newlines and tabs)
        result = result.replace(Regex("[\\x00-\\x08\\x0B\\x0C\\x0E-\\x1F\\x7F]"), "")

        // Remove SQL injection patterns
        result = result.replace(Regex("('\\s*;|--\\s|/\\*|\\*/|;\\s*DROP|;\\s*DELETE|;\\s*INSERT|;\\s*UPDATE|;\\s*ALTER|UNION\\s+SELECT|OR\\s+1\\s*=\\s*1)", RegexOption.IGNORE_CASE), "")

        // Escape HTML special characters
        result = result
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&#x27;")

        // Normalize Unicode (NFC form)
        result = java.text.Normalizer.normalize(result, java.text.Normalizer.Form.NFC)

        return result
    }
}
