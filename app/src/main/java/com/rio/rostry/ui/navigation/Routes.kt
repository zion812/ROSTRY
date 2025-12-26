package com.rio.rostry.ui.navigation

import com.rio.rostry.domain.model.UserType
import java.net.URLEncoder

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
        const val WELCOME = "auth/welcome"
        const val PHONE = "auth/phone"
        const val OTP = "auth/otp/{verificationId}"
        const val PHONE_VERIFY = "auth/phone_verify"
    }

    object Scan {
        const val QR = "scan/qr"
    }

    object Common {
        const val PROFILE = "profile"
        const val VERIFY_FARMER_LOCATION = "verify/farmer/location"

        const val VERIFY_ENTHUSIAST_KYC = "verify/enthusiast/kyc"
        const val VERIFICATION_WITH_TYPE = "verification/{upgradeType}"
        const val STORAGE_QUOTA = "storage/quota"
    }

    object User {
        const val PROFILE = "user/{userId}"
        const val PROFILE_EDIT = "user/edit"
    }

    object Onboarding {
        const val GENERAL = "onboard/general"
        const val FARMER = "onboard/farmer"
        const val ENTHUSIAST = "onboard/enthusiast"
        const val FARM_BIRD = "onboard/farm/bird"
        const val FARM_BATCH = "onboard/farm/batch"
        const val USER_SETUP = "onboard/user_setup"
    }

    object Product {
        const val DETAILS = "product/{productId}"
        const val TRACEABILITY = "traceability/{productId}"
        const val FAMILY_TREE = "product/{productId}/family_tree"
        const val LINEAGE_PREVIEW = "lineage/{productId}"
        const val CREATE = "product/create"
        const val EXPLORE = "product/explore"
        const val MARKET = "product/market"
        const val CART = "product/cart"
        const val SANDBOX = "product/sandbox"
        const val AUCTION = "auction/{auctionId}"
    }

    /**
     * Order routes for displaying order details, tracking, and status.
     * Includes Evidence-Based Order System routes.
     */
    object Order {
        const val DETAILS = "order/{orderId}"
        
        // Evidence-Based Order System Routes
        const val MY_ORDERS = "orders"
        const val MY_ORDERS_FARMER = "orders/farmer"
        const val CREATE_ENQUIRY = "order/enquiry/{productId}/{sellerId}"
        const val QUOTE_NEGOTIATION = "order/quote/{quoteId}"
        const val QUOTE_NEGOTIATION_BUYER = "order/quote/{quoteId}/buyer"
        const val QUOTE_NEGOTIATION_SELLER = "order/quote/{quoteId}/seller"
        const val PAYMENT_PROOF = "order/payment/{paymentId}/proof"
        const val DELIVERY_OTP_BUYER = "order/{orderId}/delivery/otp/buyer"
        const val DELIVERY_OTP_SELLER = "order/{orderId}/delivery/otp/seller"
        const val ORDER_TRACKING = "order/{orderId}/tracking"
        const val RAISE_DISPUTE = "order/{orderId}/dispute"
        const val MY_DISPUTES = "orders/disputes"
        const val DISPUTE_DETAILS = "dispute/{disputeId}"
        const val PAYMENT_VERIFY = "order/payment/{paymentId}/verify"
        
        // Review routes
        const val ORDER_REVIEW = "order/{orderId}/review"
        const val SELLER_REVIEWS = "seller/{sellerId}/reviews"
        
        // Helper functions
        fun createEnquiry(productId: String, sellerId: String) = 
            "order/enquiry/$productId/$sellerId"
        fun quoteNegotiation(quoteId: String, isBuyer: Boolean) = 
            if (isBuyer) "order/quote/$quoteId/buyer" else "order/quote/$quoteId/seller"
        fun paymentProof(paymentId: String) = "order/payment/$paymentId/proof"
        fun deliveryOtp(orderId: String, isBuyer: Boolean) = 
            if (isBuyer) "order/$orderId/delivery/otp/buyer" else "order/$orderId/delivery/otp/seller"
        fun orderTracking(orderId: String) = "order/$orderId/tracking"
        fun raiseDispute(orderId: String) = "order/$orderId/dispute"
        fun disputeDetails(disputeId: String) = "dispute/$disputeId"
        fun paymentVerify(paymentId: String) = "order/payment/$paymentId/verify"
        fun orderReview(orderId: String) = "order/$orderId/review"
        fun sellerReviews(sellerId: String) = "seller/$sellerId/reviews"
    }

    object Transfers {
        const val DETAILS = "transfer/{transferId}"
        const val LIST = "transfer/list"
        const val VERIFY = "transfer/{transferId}/verify"
        const val CREATE = "transfer/create"
        const val FARMER_TRANSFERS = "farmer/transfers"
        const val COMPLIANCE_DETAILS = "compliance/{productId}"
    }

    object Social {
        const val FEED = "social/feed"
        const val GROUPS = "social/groups"
        const val EVENTS = "social/events"
        const val EXPERT = "social/expert"

        const val MODERATION = "social/moderation"
        const val MODERATION_VERIFICATIONS = "moderation/verifications"
        const val LEADERBOARD = "social/leaderboard"
        const val LIVE = "social/live"
        const val STORY_VIEWER = "social/story/viewer/{initialIndex}"
        const val STORY_CREATOR = "social/story/creator"
        const val DISCUSSION_DETAIL = "social/discussion/{postId}"
    }

    object Messaging {
        const val THREAD = "messages/{threadId}"
        const val GROUP = "group/{groupId}/chat"
        const val OUTBOX = "messages/outbox"
    }

    object Notifications {
        const val LIST = "notifications"
    }

    object Settings {
        const val ROOT = "settings"
        const val ADDRESS_SELECTION = "settings/address_selection"
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
        // Detail deep-link route for batch
        const val HATCHING_BATCH = "hatching/batch/{batchId}"
        const val DASHBOARD = "monitoring/dashboard"
        const val PERFORMANCE = "monitoring/performance"
        const val DAILY_LOG = "monitoring/daily_log"
        const val DAILY_LOG_PRODUCT = "monitoring/daily_log/{productId}"
        const val TASKS = "monitoring/tasks"
    }

    // Loveable product feature routes
    object Loveable {
        const val ACHIEVEMENTS = "love/achievements"
        const val INSIGHTS = "love/insights"
        const val FEEDBACK = "love/feedback"
        const val HELP = "love/help"
        const val COMMUNITY = "love/community"
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
        // New route with optional prefillProductId query parameter for farm-to-marketplace bridge
        const val CREATE_WITH_PREFILL = "farmer/create?prefillProductId={prefillProductId}"
        const val COMMUNITY = "farmer/community"
        const val PROFILE = "farmer/profile"
        const val DIGITAL_FARM = "farmer/digital_farm"
        
        // NEW: Farm Asset Management Routes (Phase 1 of Farm-Market Separation)
        const val FARM_ASSETS = "farmer/farm_assets"
        const val ASSET_DETAILS = "farmer/asset/{assetId}"
        const val CREATE_ASSET = "farmer/create_asset"
        const val CREATE_LISTING_FROM_ASSET = "farmer/create_listing/{assetId}"
    }

    object EnthusiastNav {
        const val HOME = "home/enthusiast"
        const val EXPLORE = "enthusiast/explore"
        const val CREATE = "enthusiast/create"
        const val DASHBOARD = "enthusiast/dashboard"
        const val TRANSFERS = "enthusiast/transfers"
        const val EGG_COLLECTION = "enthusiast/egg_collection"
        const val DIGITAL_FARM = "enthusiast/digital_farm" // Evolutionary Visuals Feature
    }

    object Sync {
        const val ISSUES = "sync/issues"
        const val STATUS = "sync/status"
    }

    object Upgrade {
        const val WIZARD = "upgrade/wizard/{targetRole}"
        const val POST_ONBOARDING = "upgrade/post_onboarding/{newRole}"
    }

    private val generalConfig = RoleNavigationConfig(
        role = UserType.GENERAL,
        startDestination = GeneralNav.HOME,
        bottomNav = emptyList(),
        accessibleRoutes = setOf(
            GeneralNav.HOME,
            Settings.ROOT,
            Settings.ADDRESS_SELECTION,
            User.PROFILE,
            Common.STORAGE_QUOTA,
            Product.DETAILS,
            Product.TRACEABILITY,
            Social.FEED,
            Messaging.THREAD,
            Order.DETAILS,
            Sync.ISSUES,
            Upgrade.WIZARD,
            Upgrade.POST_ONBOARDING
        )
    )

    private val farmerConfig = RoleNavigationConfig(
        role = UserType.FARMER,
        startDestination = FarmerNav.HOME,
        bottomNav = listOf(
            BottomNavDestination(FarmerNav.HOME, "Home"),
            BottomNavDestination(FarmerNav.FARM_ASSETS, "My Farm"), // NEW: Dedicated farm management tab
            BottomNavDestination(FarmerNav.MARKET, "Market"), // Browse marketplace
            BottomNavDestination(Analytics.FARMER, "Analytics"),
            BottomNavDestination(FarmerNav.PROFILE, "Profile")
        ),
        accessibleRoutes = setOf(
            FarmerNav.HOME,
            FarmerNav.MARKET,
            FarmerNav.CREATE,
            FarmerNav.COMMUNITY,
            FarmerNav.PROFILE,
            Common.PROFILE,
            Common.VERIFY_FARMER_LOCATION,
            Common.STORAGE_QUOTA,
            Settings.ROOT,
            Settings.ADDRESS_SELECTION,
            User.PROFILE,
            Product.CREATE,
            Social.FEED,
            Social.GROUPS,
            Social.EVENTS,
            Scan.QR,
            Transfers.DETAILS,
            Transfers.FARMER_TRANSFERS,
            Transfers.COMPLIANCE_DETAILS,
            "compliance",
            Analytics.FARMER,
            Monitoring.DAILY_LOG,
            Monitoring.DAILY_LOG_PRODUCT,
            Monitoring.TASKS,
            Monitoring.VACCINATION,
            Monitoring.MORTALITY,
            Monitoring.QUARANTINE,
            Monitoring.BREEDING,
            Monitoring.GROWTH,
            Monitoring.HATCHING,
            Monitoring.HATCHING_BATCH,
            Monitoring.DASHBOARD,
            Monitoring.PERFORMANCE,
            Onboarding.FARM_BIRD,
            Onboarding.FARM_BATCH,
            Order.DETAILS,
            Sync.ISSUES,
            Upgrade.WIZARD,
            Upgrade.POST_ONBOARDING,
            FarmerNav.DIGITAL_FARM,
            // NEW: Farm Asset Management routes
            FarmerNav.FARM_ASSETS,
            FarmerNav.ASSET_DETAILS,
            FarmerNav.CREATE_ASSET,
            FarmerNav.CREATE_LISTING_FROM_ASSET
        )
    )

    private val enthusiastConfig = RoleNavigationConfig(
        role = UserType.ENTHUSIAST,
        startDestination = EnthusiastNav.HOME,
        bottomNav = listOf(
            BottomNavDestination(EnthusiastNav.HOME, "Home"),
            BottomNavDestination(EnthusiastNav.EXPLORE, "Explore"),
            BottomNavDestination(Social.FEED, "Community"), // Explicit Community tab
            BottomNavDestination(EnthusiastNav.DASHBOARD, "Dashboard"),
            BottomNavDestination(Common.PROFILE, "Profile") // Use Common Profile or specific if exists
        ),
        accessibleRoutes = setOf(
            EnthusiastNav.HOME,
            EnthusiastNav.EXPLORE,
            EnthusiastNav.CREATE,
            EnthusiastNav.DASHBOARD,
            EnthusiastNav.TRANSFERS,
            Common.PROFILE,
            Common.VERIFY_ENTHUSIAST_KYC,
            Common.STORAGE_QUOTA,
            Settings.ROOT,
            Settings.ADDRESS_SELECTION,
            User.PROFILE,
            Analytics.ENTHUSIAST,
            Analytics.REPORTS,
            Transfers.DETAILS,
            Transfers.LIST,
            Product.TRACEABILITY,
            Scan.QR,
            Messaging.THREAD,
            Social.FEED, // Ensure accessible
            Monitoring.DAILY_LOG,
            Monitoring.DAILY_LOG_PRODUCT,
            Monitoring.TASKS,
            Monitoring.DASHBOARD,
            Monitoring.VACCINATION,
            Monitoring.MORTALITY,
            Monitoring.QUARANTINE,
            Monitoring.BREEDING,
            Monitoring.GROWTH,
            Monitoring.HATCHING,
            Monitoring.HATCHING_BATCH,
            Monitoring.PERFORMANCE,
            Product.FAMILY_TREE,
            EnthusiastNav.EGG_COLLECTION,
            Onboarding.FARM_BIRD,
            Onboarding.FARM_BATCH,
            Order.DETAILS,
            Sync.ISSUES,
            Upgrade.WIZARD,
            Upgrade.POST_ONBOARDING
        )
    )

    fun configFor(role: UserType): RoleNavigationConfig = when (role) {
        UserType.GENERAL -> generalConfig
        UserType.FARMER -> farmerConfig
        UserType.ENTHUSIAST -> enthusiastConfig
    }

    const val AUTH_WELCOME = Auth.WELCOME
    const val AUTH_PHONE = Auth.PHONE
    const val AUTH_OTP = Auth.OTP
    const val AUTH_PHONE_VERIFY = Auth.PHONE_VERIFY
    const val HOME_GENERAL = GeneralNav.HOME
    const val HOME_FARMER = FarmerNav.HOME
    const val HOME_ENTHUSIAST = EnthusiastNav.HOME
    const val PROFILE = Common.PROFILE
    const val USER_PROFILE = User.PROFILE
    const val VERIFY_FARMER_LOCATION = Common.VERIFY_FARMER_LOCATION
    const val VERIFY_ENTHUSIAST_KYC = Common.VERIFY_ENTHUSIAST_KYC
    const val ONBOARD_GENERAL = Onboarding.GENERAL
    const val ONBOARD_FARMER = Onboarding.FARMER
    const val ONBOARD_ENTHUSIAST = Onboarding.ENTHUSIAST
    const val PRODUCT_DETAILS = Product.DETAILS
    const val TRACEABILITY = Product.TRACEABILITY
    const val PRODUCT_FAMILY_TREE = Product.FAMILY_TREE
    const val LINEAGE_PREVIEW = Product.LINEAGE_PREVIEW
    const val SCAN_QR = Scan.QR
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
    const val SETTINGS = Settings.ROOT
    const val ADDRESS_SELECTION = Settings.ADDRESS_SELECTION
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
    const val MONITORING_HATCHING_BATCH = Monitoring.HATCHING_BATCH
    const val MONITORING_PERFORMANCE = Monitoring.PERFORMANCE
    const val MONITORING_DAILY_LOG = Monitoring.DAILY_LOG
    const val MONITORING_DAILY_LOG_PRODUCT = Monitoring.DAILY_LOG_PRODUCT
    const val MONITORING_TASKS = Monitoring.TASKS
    const val ORDER_DETAILS = Order.DETAILS
    const val FARMER_TRANSFERS = Transfers.FARMER_TRANSFERS
    const val COMPLIANCE_DETAILS = Transfers.COMPLIANCE_DETAILS
    const val COMPLIANCE = "compliance"
    const val SYNC_ISSUES = Sync.ISSUES
    const val SYNC_STATUS = Sync.STATUS
    const val UPGRADE_WIZARD = Upgrade.WIZARD
    const val UPGRADE_POST_ONBOARDING = Upgrade.POST_ONBOARDING
    const val STORAGE_QUOTA = Common.STORAGE_QUOTA

    //Loveable aliases
    const val ACHIEVEMENTS = Loveable.ACHIEVEMENTS
    const val INSIGHTS = Loveable.INSIGHTS
    const val FEEDBACK = Loveable.FEEDBACK
    const val HELP = Loveable.HELP
    const val COMMUNITY = Loveable.COMMUNITY

    // Community Hub routes
    object CommunityHub {
        const val HUB = "community/hub"
        const val GROUP_DETAILS = "community/group/{groupId}"
        const val EVENT_DETAILS = "community/event/{eventId}"
        const val EXPERT_PROFILE = "community/expert/{expertId}"
        
        fun createGroupRoute(groupId: String) = "community/group/$groupId"
        fun createEventRoute(eventId: String) = "community/event/$eventId"
        fun createExpertRoute(expertId: String) = "community/expert/$expertId"
    }
    
    const val COMMUNITY_HUB = CommunityHub.HUB
    const val GROUP_DETAILS = CommunityHub.GROUP_DETAILS
    const val EVENT_DETAILS = CommunityHub.EVENT_DETAILS
    const val EXPERT_PROFILE = CommunityHub.EXPERT_PROFILE

    // Consolidated base for traceability-related routes
    const val TRACEABILITY_BASE = "traceability"

    /**
     * Whitelist of allowed query parameters per base route.
     * Whenever a Builder introduces a new query parameter, it must be added here to pass validation.
     */
    private val routeQueryWhitelist: Map<String, Set<String>> = mapOf(
        Auth.PHONE to emptySet(),
        Auth.OTP to emptySet(),
        FarmerNav.CREATE to setOf("prefillProductId", "pairId"),
        Onboarding.FARM_BIRD to setOf("role"),
        Onboarding.FARM_BATCH to setOf("role"),
        SCAN_QR to setOf("context", "transferId"),
        Monitoring.TASKS to setOf("filter"),
        Monitoring.VACCINATION to setOf("filter", "productId"),
        Monitoring.GROWTH to setOf("filter", "productId"),
        Monitoring.HATCHING to setOf("filter"),
        Monitoring.MORTALITY to setOf("filter"),
        Monitoring.BREEDING to setOf("filter"),
        Monitoring.QUARANTINE to setOf("filter"),
        Monitoring.DAILY_LOG to setOf("filter"),
        Product.MARKET to setOf("filter"),
        Transfers.LIST to setOf("status"),
        Social.FEED to setOf("postId")
    )

    // Helper to extract the base route (before '?')
    private fun baseOf(route: String): String = route.substringBefore("?")

    // Helper to extract query keys from a route
    fun extractQueryKeys(route: String): Set<String> {
        val query = route.substringAfter("?", "")
        if (query.isBlank()) return emptySet()
        return query.split("&")
            .mapNotNull { part -> part.substringBefore("=", missingDelimiterValue = "").takeIf { it.isNotBlank() } }
            .toSet()
    }

    /**
     * Check if a concrete route string (e.g., "product/123") matches any of the route patterns
     * in [allowed], where patterns may contain path parameters in braces (e.g., "product/{productId}").
     */
    fun isRouteAccessible(allowed: Set<String>, concreteRoute: String): Boolean {
        val full = concreteRoute
        val base = baseOf(full)
        val queryKeys = extractQueryKeys(full)

        // Step 1: Base route must match one of the allowed patterns
        val baseMatches = run {
            if (allowed.contains(base)) true else allowed.any { pattern ->
                val regex = pattern
                    .replace("/", "\\/")
                    .replace(Regex("\\{[^/}]+\\}"), "[^/]+")
                    .let { "^$it$".toRegex() }
                regex.matches(base)
            }
        }
        if (!baseMatches) return false

        // Step 2: If there are query params, validate against whitelist for the base route
        if (queryKeys.isNotEmpty()) {
            val allowedKeys = routeQueryWhitelist[base] ?: emptySet()
            if (!queryKeys.all { it in allowedKeys }) return false
        }
        return true
    }

    object Builders {
        fun productDetails(id: String): String = "product/${URLEncoder.encode(id, "UTF-8")}"
        fun auction(id: String): String = "auction/${URLEncoder.encode(id, "UTF-8")}"
        /**
         * Builds the route for the traceability screen, showing the family tree and lineage information.
         * This route displays the TraceabilityScreen with the product as root.
         */
        fun traceability(id: String): String = "$TRACEABILITY_BASE/${URLEncoder.encode(id, "UTF-8")}"
        /**
         * Builds the route for the family tree view, an alternative entry point to the traceability screen.
         * This route also displays the TraceabilityScreen but may have different initial focus or UI.
         */
        fun familyTree(id: String): String = "product/${URLEncoder.encode(id, "UTF-8")}/family_tree"
        /**
         * Generates the deep-link URL for the lineage view of a product.
         * This can be used for QR codes or sharing to directly open the family tree.
         */
        fun lineageDeepLink(id: String): String = "rostry://product/${URLEncoder.encode(id, "UTF-8")}/lineage"
        fun transferDetails(id: String): String = "transfer/${URLEncoder.encode(id, "UTF-8") }"
        fun transferVerify(id: String): String = "transfer/${URLEncoder.encode(id, "UTF-8")}/verify"
        fun messagesThread(id: String): String = "messages/${URLEncoder.encode(id, "UTF-8")}"
        fun userProfile(id: String): String = "user/${URLEncoder.encode(id, "UTF-8")}"

        fun farmerCreateWithPrefill(productId: String, pairId: String? = null): String {
            val pid = URLEncoder.encode(productId, "UTF-8")
            val q = buildList {
                add("prefillProductId=$pid")
                if (!pairId.isNullOrBlank()) add("pairId=${URLEncoder.encode(pairId, "UTF-8")}")
            }.joinToString("&")
            return "${FarmerNav.CREATE}?$q"
        }

        fun scanQr(context: String = "traceability", transferId: String? = null): String {
            val parts = buildList {
                add("context=${URLEncoder.encode(context, "UTF-8")}")
                if (!transferId.isNullOrBlank()) add("transferId=${URLEncoder.encode(transferId, "UTF-8")}")
            }
            val q = parts.joinToString("&")
            return "$SCAN_QR?$q"
        }

        fun onboardingFarmBird(role: String? = null): String {
            val q = role?.takeIf { it.isNotBlank() }?.let { "?role=${URLEncoder.encode(it, "UTF-8")}" } ?: ""
            return Onboarding.FARM_BIRD + q
        }

        fun onboardingFarmBatch(role: String? = null): String {
            val q = role?.takeIf { it.isNotBlank() }?.let { "?role=${URLEncoder.encode(it, "UTF-8")}" } ?: ""
            return Onboarding.FARM_BATCH + q
        }

        // Monitoring helpers
        fun dailyLog(productId: String): String =
            "${Monitoring.DAILY_LOG}/${URLEncoder.encode(productId, "UTF-8")}"

        fun monitoringVaccinationWithProductId(productId: String): String =
            "${Monitoring.VACCINATION}?productId=${URLEncoder.encode(productId, "UTF-8")}"

        fun monitoringGrowthWithProductId(productId: String): String =
            "${Monitoring.GROWTH}?productId=${URLEncoder.encode(productId, "UTF-8")}"

        /**
         * Builds the route for order details, showing order tracking and status.
         */
        fun orderDetails(id: String): String = "order/${URLEncoder.encode(id, "UTF-8")}"

        /**
         * Builds the deep-link URL for a social post, used for sharing or notifications.
         */
        fun socialPost(postId: String): String = "social/feed?postId=${URLEncoder.encode(postId, "UTF-8")}"

        fun storyViewer(initialIndex: Int): String = "social/story/viewer/$initialIndex"
        fun storyCreator(): String = Social.STORY_CREATOR
        fun discussionDetail(postId: String): String = "social/discussion/${URLEncoder.encode(postId, "UTF-8")}"

        fun farmerTransfers(): String = Transfers.FARMER_TRANSFERS

        fun complianceDetails(productId: String): String = "compliance/${URLEncoder.encode(productId, "UTF-8")}"

        fun transfersWithFilter(status: String): String = "${Transfers.LIST}?status=${URLEncoder.encode(status, "UTF-8")}"

        fun productsWithFilter(filter: String): String = "${Product.MARKET}?filter=${URLEncoder.encode(filter, "UTF-8")}"

        fun monitoringTasks(filter: String? = null): String =
            if (filter != null) "${Monitoring.TASKS}?filter=${URLEncoder.encode(filter, "UTF-8")}" else Monitoring.TASKS

        fun monitoringVaccinationWithFilter(filter: String? = null): String =
            if (filter != null) "${Monitoring.VACCINATION}?filter=${URLEncoder.encode(filter, "UTF-8")}" else Monitoring.VACCINATION

        fun monitoringQuarantine(filter: String? = null): String =
            if (filter != null) "${Monitoring.QUARANTINE}?filter=${URLEncoder.encode(filter, "UTF-8")}" else Monitoring.QUARANTINE

        fun monitoringGrowthWithFilter(filter: String? = null): String =
            if (filter != null) "${Monitoring.GROWTH}?filter=${URLEncoder.encode(filter, "UTF-8")}" else Monitoring.GROWTH

        fun monitoringDailyLog(filter: String? = null): String =
            if (filter != null) "${Monitoring.DAILY_LOG}?filter=${URLEncoder.encode(filter, "UTF-8")}" else Monitoring.DAILY_LOG

        fun monitoringHatching(filter: String? = null): String =
            if (filter != null) "${Monitoring.HATCHING}?filter=${URLEncoder.encode(filter, "UTF-8")}" else Monitoring.HATCHING

        fun monitoringMortality(filter: String? = null): String =
            if (filter != null) "${Monitoring.MORTALITY}?filter=${URLEncoder.encode(filter, "UTF-8")}" else Monitoring.MORTALITY

        fun monitoringBreeding(filter: String? = null): String =
            if (filter != null) "${Monitoring.BREEDING}?filter=${URLEncoder.encode(filter, "UTF-8")}" else Monitoring.BREEDING

        fun syncIssues() = SYNC_ISSUES

        fun syncStatus() = SYNC_STATUS

        fun upgradeWizard(targetRole: UserType): String = "upgrade/wizard/${targetRole.name}"
        
        fun upgradePostOnboarding(newRole: UserType): String = "upgrade/post_onboarding/${newRole.name}"

        fun verificationWithType(upgradeType: com.rio.rostry.domain.model.UpgradeType): String = "verification/${upgradeType.name}"
        
        // NEW: Farm Asset Management builders
        fun assetDetails(assetId: String): String = "farmer/asset/${URLEncoder.encode(assetId, "UTF-8")}"
        fun createListingFromAsset(assetId: String): String = "farmer/create_listing/${URLEncoder.encode(assetId, "UTF-8")}"
    }

} // Closing brace for object Routes