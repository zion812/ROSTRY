package com.rio.rostry.ui.navigation

object Routes {
    const val ROOT = "root"
    const val AUTH_PHONE = "auth/phone"
    const val AUTH_OTP = "auth/otp/{verificationId}"
    const val HOME_GENERAL = "home/general"
    const val HOME_FARMER = "home/farmer"
    const val HOME_ENTHUSIAST = "home/enthusiast"
    const val PROFILE = "profile"
    const val VERIFY_FARMER_LOCATION = "verify/farmer/location"
    const val VERIFY_ENTHUSIAST_KYC = "verify/enthusiast/kyc"
    const val ONBOARD_GENERAL = "onboard/general"
    const val ONBOARD_FARMER = "onboard/farmer"
    const val ONBOARD_ENTHUSIAST = "onboard/enthusiast"

    // Product + Traceability
    const val PRODUCT_DETAILS = "product/{productId}"
    const val TRACEABILITY = "traceability/{productId}"

    // Transfer details
    const val TRANSFER_DETAILS = "transfer/{transferId}"

    // Social
    const val SOCIAL_FEED = "social/feed"
    const val MESSAGES_THREAD = "messages/{threadId}"
    const val MESSAGES_GROUP = "group/{groupId}/chat"
    const val GROUPS = "social/groups"
    const val EVENTS = "social/events"
    const val EXPERT_BOOKING = "social/expert"
    const val MODERATION = "social/moderation"
    const val LEADERBOARD = "social/leaderboard"
}
