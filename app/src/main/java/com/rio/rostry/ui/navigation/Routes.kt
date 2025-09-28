package com.rio.rostry.ui.navigation

import com.rio.rostry.domain.model.UserType

data class BottomNavDestination(
    val route: String,
    val label: String
)

data class RoleNavigationConfig(
    val role: UserType,
    val startDestination: String,
    val bottomNav: List<BottomNavDestination>,
    val accessibleRoutes: Set<String>
)

object Routes {
    const val ROOT = "root"

    object Auth {
        const val PHONE = "auth/phone"
        const val OTP = "auth/otp/{verificationId}"
    }

    object Common {
        const val PROFILE = "profile"
        const val VERIFY_FARMER_LOCATION = "verify/farmer/location"
        const val VERIFY_ENTHUSIAST_KYC = "verify/enthusiast/kyc"
    }

    object Onboarding {
        const val GENERAL = "onboard/general"
        const val FARMER = "onboard/farmer"
        const val ENTHUSIAST = "onboard/enthusiast"
    }

    object Product {
        const val DETAILS = "product/{productId}"
        const val TRACEABILITY = "traceability/{productId}"
        const val CREATE = "product/create"
        const val EXPLORE = "product/explore"
        const val MARKET = "product/market"
        const val CART = "product/cart"
        const val SANDBOX = "product/sandbox"
    }

    object Transfers {
        const val DETAILS = "transfer/{transferId}"
        const val LIST = "transfer/list"
        const val VERIFY = "transfer/{transferId}/verify"
        const val CREATE = "transfer/create"
    }

    object Social {
        const val FEED = "social/feed"
        const val GROUPS = "social/groups"
        const val EVENTS = "social/events"
        const val EXPERT = "social/expert"
        const val MODERATION = "social/moderation"
        const val LEADERBOARD = "social/leaderboard"
        const val LIVE = "social/live"
    }

    object Messaging {
        const val THREAD = "messages/{threadId}"
        const val GROUP = "group/{groupId}/chat"
        const val OUTBOX = "messages/outbox"
    }

    object Notifications {
        const val LIST = "notifications"
    }

    // Developer/Showcase routes (debug-only usage recommended)
    object Showcase {
        const val COMPONENT_GALLERY = "dev/component_gallery"
    }

    object Analytics {
        const val GENERAL = "analytics/general"
        const val FARMER = "analytics/farmer"
        const val ENTHUSIAST = "analytics/enthusiast"
        const val REPORTS = "analytics/reports"
        const val DASHBOARD = "analytics/dashboard"
    }

    object Monitoring {
        const val VACCINATION = "monitoring/vaccination"
        const val MORTALITY = "monitoring/mortality"
        const val QUARANTINE = "monitoring/quarantine"
        const val BREEDING = "monitoring/breeding"
        const val GROWTH = "monitoring/growth"
        const val HATCHING = "monitoring/hatching"
        const val DASHBOARD = "monitoring/dashboard"
        const val PERFORMANCE = "monitoring/performance"
    }

    object GeneralNav {
        const val HOME = "home/general"
        const val MARKET = "general/market"
        const val EXPLORE = "general/explore"
        const val CREATE = "general/create"
        const val CART = "general/cart"
        const val PROFILE = "general/profile"
    }

    object FarmerNav {
        const val HOME = "home/farmer"
        const val MARKET = "farmer/market"
        const val CREATE = "farmer/create"
        const val COMMUNITY = "farmer/community"
        const val PROFILE = "farmer/profile"
    }

    object EnthusiastNav {
        const val HOME = "home/enthusiast"
        const val EXPLORE = "enthusiast/explore"
        const val CREATE = "enthusiast/create"
        const val DASHBOARD = "enthusiast/dashboard"
        const val TRANSFERS = "enthusiast/transfers"
    }

    private val generalConfig = RoleNavigationConfig(
        role = UserType.GENERAL,
        startDestination = GeneralNav.HOME,
        bottomNav = emptyList(),
        accessibleRoutes = setOf(
            GeneralNav.HOME,
            GeneralNav.MARKET,
            GeneralNav.EXPLORE,
            GeneralNav.CREATE,
            GeneralNav.CART,
            GeneralNav.PROFILE,
            Product.DETAILS,
            Product.TRACEABILITY,
            Social.FEED,
            Messaging.THREAD
        )
    )

    private val farmerConfig = RoleNavigationConfig(
        role = UserType.FARMER,
        startDestination = FarmerNav.HOME,
        bottomNav = listOf(
            BottomNavDestination(FarmerNav.HOME, "Home"),
            BottomNavDestination(FarmerNav.MARKET, "Market"),
            BottomNavDestination(FarmerNav.CREATE, "Create"),
            BottomNavDestination(FarmerNav.COMMUNITY, "Community"),
            BottomNavDestination(FarmerNav.PROFILE, "Profile")
        ),
        accessibleRoutes = setOf(
            FarmerNav.HOME,
            FarmerNav.MARKET,
            FarmerNav.CREATE,
            FarmerNav.COMMUNITY,
            FarmerNav.PROFILE,
            Common.PROFILE,
            Product.CREATE,
            Social.FEED,
            Social.GROUPS,
            Social.EVENTS,
            Transfers.DETAILS,
            Analytics.FARMER
        )
    )

    private val enthusiastConfig = RoleNavigationConfig(
        role = UserType.ENTHUSIAST,
        startDestination = EnthusiastNav.HOME,
        bottomNav = listOf(
            BottomNavDestination(EnthusiastNav.HOME, "Home"),
            BottomNavDestination(EnthusiastNav.EXPLORE, "Explore"),
            BottomNavDestination(EnthusiastNav.CREATE, "Create"),
            BottomNavDestination(EnthusiastNav.DASHBOARD, "Dashboard"),
            BottomNavDestination(EnthusiastNav.TRANSFERS, "Transfers")
        ),
        accessibleRoutes = setOf(
            EnthusiastNav.HOME,
            EnthusiastNav.EXPLORE,
            EnthusiastNav.CREATE,
            EnthusiastNav.DASHBOARD,
            EnthusiastNav.TRANSFERS,
            Common.PROFILE,
            Analytics.ENTHUSIAST,
            Analytics.REPORTS,
            Transfers.DETAILS,
            Transfers.LIST,
            Product.TRACEABILITY,
            Messaging.THREAD
        )
    )

    fun configFor(role: UserType): RoleNavigationConfig = when (role) {
        UserType.GENERAL -> generalConfig
        UserType.FARMER -> farmerConfig
        UserType.ENTHUSIAST -> enthusiastConfig
    }

    const val AUTH_PHONE = Auth.PHONE
    const val AUTH_OTP = Auth.OTP
    const val HOME_GENERAL = GeneralNav.HOME
    const val HOME_FARMER = FarmerNav.HOME
    const val HOME_ENTHUSIAST = EnthusiastNav.HOME
    const val PROFILE = Common.PROFILE
    const val VERIFY_FARMER_LOCATION = Common.VERIFY_FARMER_LOCATION
    const val VERIFY_ENTHUSIAST_KYC = Common.VERIFY_ENTHUSIAST_KYC
    const val ONBOARD_GENERAL = Onboarding.GENERAL
    const val ONBOARD_FARMER = Onboarding.FARMER
    const val ONBOARD_ENTHUSIAST = Onboarding.ENTHUSIAST
    const val PRODUCT_DETAILS = Product.DETAILS
    const val TRACEABILITY = Product.TRACEABILITY
    const val PRODUCT_CREATE = Product.CREATE
    const val PRODUCT_EXPLORE = Product.EXPLORE
    const val PRODUCT_MARKET = Product.MARKET
    const val PRODUCT_CART = Product.CART
    const val PRODUCT_SANDBOX = Product.SANDBOX
    const val TRANSFER_DETAILS = Transfers.DETAILS
    const val TRANSFER_LIST = Transfers.LIST
    const val TRANSFER_VERIFY = Transfers.VERIFY
    const val TRANSFER_CREATE = Transfers.CREATE
    const val SOCIAL_FEED = Social.FEED
    const val MESSAGES_THREAD = Messaging.THREAD
    const val MESSAGES_GROUP = Messaging.GROUP
    const val MESSAGES_OUTBOX = Messaging.OUTBOX
    const val NOTIFICATIONS = Notifications.LIST
    const val GROUPS = Social.GROUPS
    const val EVENTS = Social.EVENTS
    const val EXPERT_BOOKING = Social.EXPERT
    const val MODERATION = Social.MODERATION
    const val LEADERBOARD = Social.LEADERBOARD
    const val LIVE_BROADCAST = Social.LIVE
    const val ANALYTICS_GENERAL = Analytics.GENERAL
    const val ANALYTICS_FARMER = Analytics.FARMER
    const val ANALYTICS_ENTHUSIAST = Analytics.ENTHUSIAST
    const val REPORTS = Analytics.REPORTS
    const val ANALYTICS_DASHBOARD = Analytics.DASHBOARD
    const val COMPONENT_GALLERY = Showcase.COMPONENT_GALLERY
    const val MONITORING_DASHBOARD = Monitoring.DASHBOARD
    const val MONITORING_VACCINATION = Monitoring.VACCINATION
    const val MONITORING_MORTALITY = Monitoring.MORTALITY
    const val MONITORING_QUARANTINE = Monitoring.QUARANTINE
    const val MONITORING_BREEDING = Monitoring.BREEDING
    const val MONITORING_GROWTH = Monitoring.GROWTH
    const val MONITORING_HATCHING = Monitoring.HATCHING
    const val MONITORING_PERFORMANCE = Monitoring.PERFORMANCE

} // Closing brace for object Routes
