package com.rio.rostry.domain.model

enum class Permission {
    // Marketplace
    BROWSE_MARKET, // Allows browsing products in the marketplace
    PLACE_ORDER, // Allows placing orders for products
    LIST_PRODUCT, // Allows listing products for sale
    MANAGE_ORDERS, // Allows managing orders (farmer side)
    // Farm
    BASIC_TRACKING, // Allows basic farm tracking features
    BREEDING_RECORDS, // Allows managing breeding records
    ADVANCED_TRACKING, // Allows advanced tracking features
    TRANSFER_SYSTEM, // Allows initiating transfers
    // Lineage
    VIEW_LINEAGE, // Allows viewing family tree relationships (public for all)
    EDIT_LINEAGE, // Allows editing family tree relationships
    // Admin
    ADMIN_VERIFICATION, // Allows approving/rejecting KYC submissions
    VIEW_AUDIT_LOGS, // Allows viewing audit trail
    // General
    BASIC_PROFILE, // Allows basic profile management
    COIN_MANAGEMENT // Allows managing coins
}