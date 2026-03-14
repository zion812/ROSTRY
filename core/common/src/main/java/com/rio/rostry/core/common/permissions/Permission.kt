package com.rio.rostry.core.common.permissions

/**
 * Permission definitions for feature-level access control.
 * 
 * Each permission represents a specific capability that can be granted
 * based on user type and authentication status.
 */
enum class Permission(
    val displayName: String,
    val description: String
) {
    // ==================== MARKETPLACE ====================
    MARKETPLACE_BROWSE("Browse Marketplace", "View products and search"),
    MARKETPLACE_VIEW_DETAILS("View Product Details", "See full product information"),
    MARKETPLACE_ADD_TO_CART("Add to Cart", "Add items to shopping cart"),
    MARKETPLACE_PLACE_ORDER("Place Order", "Complete purchase transactions"),
    MARKETPLACE_CREATE_LISTING("Create Listing", "List products for sale"),
    MARKETPLACE_EDIT_LISTING("Edit Listing", "Modify own product listings"),
    MARKETPLACE_DELETE_LISTING("Delete Listing", "Remove own product listings"),

    // ==================== SOCIAL ====================
    SOCIAL_VIEW_FEED("View Social Feed", "Browse community posts"),
    SOCIAL_CREATE_POST("Create Post", "Share content with community"),
    SOCIAL_LIKE_POST("Like Posts", "Interact with community content"),
    SOCIAL_COMMENT("Comment", "Participate in discussions"),
    SOCIAL_FOLLOW("Follow Users", "Connect with other users"),
    SOCIAL_MESSAGE("Send Messages", "Private communication"),

    // ==================== FARM MANAGEMENT ====================
    FARM_VIEW_DASHBOARD("View Farm Dashboard", "Access farm overview"),
    FARM_ADD_BIRD("Add Birds", "Register new birds"),
    FARM_EDIT_BIRD("Edit Birds", "Modify bird records"),
    FARM_VIEW_BIRDS("View Birds", "Access bird inventory"),
    FARM_TRANSFER("Transfer Birds", "Initiate bird transfers"),
    FARM_VERIFY_TRANSFER("Verify Transfers", "Approve/reject transfers"),
    FARM_VIEW_ANALYTICS("View Analytics", "Access farm statistics"),

    // ==================== TRACEABILITY ====================
    TRACEABILITY_VIEW("View Traceability", "Access product history"),
    TRACEABILITY_GENERATE_QR("Generate QR Codes", "Create traceable codes"),
    TRACEABILITY_SCAN_QR("Scan QR Codes", "Read traceable codes"),

    // ==================== PROFILE ====================
    PROFILE_VIEW("View Profile", "Access own profile"),
    PROFILE_EDIT("Edit Profile", "Modify personal information"),
    PROFILE_VIEW_OTHERS("View Other Profiles", "See public profiles"),

    // ==================== ADMIN/MODERATION ====================
    ADMIN_VIEW_DASHBOARD("Admin Dashboard", "Access admin overview"),
    ADMIN_MANAGE_USERS("Manage Users", "User administration"),
    ADMIN_VIEW_AUDIT("View Audit Logs", "System activity logs"),
    MODERATE_CONTENT("Moderate Content", "Review reported content"),
    MODERATE_PRODUCTS("Moderate Products", "Product approval workflow"),
    VERIFY_USERS("Verify Users", "KYC verification"),

    // ==================== GUEST LIMITATIONS ====================
    GUEST_CART_SAVE("Save Cart", "Save cart for later (requires login)"),
    GUEST_WISHLIST("Wishlist", "Save items (requires login)"),
    GUEST_CHECKOUT("Checkout", "Complete purchase (requires login)")
}

/**
 * Maps user types to their default permissions.
 * Permissions can be further refined based on verification status.
 */
object PermissionDefaults {
    
    fun getDefaultPermissions(userType: com.rio.rostry.domain.model.UserType): Set<Permission> {
        return when (userType) {
            com.rio.rostry.domain.model.UserType.GENERAL -> generalPermissions
            com.rio.rostry.domain.model.UserType.FARMER -> generalPermissions + farmerPermissions
            com.rio.rostry.domain.model.UserType.ENTHUSIAST -> generalPermissions + enthusiastPermissions
            com.rio.rostry.domain.model.UserType.SUPPORT -> supportPermissions
            com.rio.rostry.domain.model.UserType.MODERATOR -> moderatorPermissions
            com.rio.rostry.domain.model.UserType.ADMIN -> adminPermissions
        }
    }

    // Base permissions available to all authenticated users
    private val generalPermissions = setOf(
        Permission.MARKETPLACE_BROWSE,
        Permission.MARKETPLACE_VIEW_DETAILS,
        Permission.MARKETPLACE_ADD_TO_CART,
        Permission.SOCIAL_VIEW_FEED,
        Permission.SOCIAL_LIKE_POST,
        Permission.SOCIAL_COMMENT,
        Permission.SOCIAL_FOLLOW,
        Permission.PROFILE_VIEW,
        Permission.PROFILE_EDIT,
        Permission.PROFILE_VIEW_OTHERS,
        Permission.TRACEABILITY_VIEW,
        Permission.TRACEABILITY_SCAN_QR
    )

    // Additional permissions for Farmers
    private val farmerPermissions = setOf(
        Permission.MARKETPLACE_CREATE_LISTING,
        Permission.MARKETPLACE_EDIT_LISTING,
        Permission.MARKETPLACE_DELETE_LISTING,
        Permission.MARKETPLACE_PLACE_ORDER,
        Permission.SOCIAL_CREATE_POST,
        Permission.SOCIAL_MESSAGE,
        Permission.FARM_VIEW_DASHBOARD,
        Permission.FARM_ADD_BIRD,
        Permission.FARM_EDIT_BIRD,
        Permission.FARM_VIEW_BIRDS,
        Permission.FARM_TRANSFER,
        Permission.FARM_VIEW_ANALYTICS,
        Permission.TRACEABILITY_GENERATE_QR
    )

    // Additional permissions for Enthusiasts
    private val enthusiastPermissions = setOf(
        Permission.MARKETPLACE_PLACE_ORDER,
        Permission.SOCIAL_CREATE_POST,
        Permission.SOCIAL_MESSAGE,
        Permission.FARM_VIEW_DASHBOARD,
        Permission.FARM_ADD_BIRD,
        Permission.FARM_EDIT_BIRD,
        Permission.FARM_VIEW_BIRDS,
        Permission.FARM_TRANSFER,
        Permission.FARM_VERIFY_TRANSFER,
        Permission.FARM_VIEW_ANALYTICS,
        Permission.TRACEABILITY_GENERATE_QR
    )

    // Support agent permissions
    private val supportPermissions = setOf(
        Permission.SOCIAL_VIEW_FEED,
        Permission.PROFILE_VIEW_OTHERS,
        Permission.ADMIN_VIEW_DASHBOARD
    )

    // Moderator permissions
    private val moderatorPermissions = setOf(
        Permission.SOCIAL_VIEW_FEED,
        Permission.MODERATE_CONTENT,
        Permission.MODERATE_PRODUCTS,
        Permission.PROFILE_VIEW_OTHERS,
        Permission.ADMIN_VIEW_DASHBOARD
    )

    // Admin permissions (full access)
    private val adminPermissions = Permission.entries.toSet()
}

/**
 * Guest-specific permissions (limited access without login)
 */
object GuestPermissions {
    val allowed = setOf(
        Permission.MARKETPLACE_BROWSE,
        Permission.MARKETPLACE_VIEW_DETAILS,
        Permission.SOCIAL_VIEW_FEED,
        Permission.PROFILE_VIEW_OTHERS,
        Permission.TRACEABILITY_VIEW,
        Permission.TRACEABILITY_SCAN_QR
    )

    val requiresLogin = setOf(
        Permission.GUEST_CART_SAVE,
        Permission.GUEST_WISHLIST,
        Permission.GUEST_CHECKOUT,
        Permission.MARKETPLACE_ADD_TO_CART,
        Permission.MARKETPLACE_PLACE_ORDER,
        Permission.SOCIAL_CREATE_POST,
        Permission.SOCIAL_MESSAGE,
        Permission.FARM_VIEW_DASHBOARD,
        Permission.FARM_ADD_BIRD,
        Permission.FARM_TRANSFER
    )
}