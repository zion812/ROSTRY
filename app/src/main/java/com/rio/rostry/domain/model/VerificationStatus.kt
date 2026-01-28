package com.rio.rostry.domain.model

enum class VerificationStatus {
    UNVERIFIED,
    PENDING,
    PENDING_UPGRADE,
    VERIFIED,
    REJECTED;

    companion object {
        fun fromString(value: String?): VerificationStatus {
            if (value.isNullOrEmpty()) {
                return UNVERIFIED
            }
            return try {
                value.uppercase().let {
                    VerificationStatus.valueOf(it)
                }
            } catch (e: IllegalArgumentException) {
                UNVERIFIED
            }
        }
    }
}
