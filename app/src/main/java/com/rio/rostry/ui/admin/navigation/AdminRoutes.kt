package com.rio.rostry.ui.admin.navigation

import java.net.URLEncoder

/**
 * Comprehensive admin route constants for the dedicated admin portal.
 * 
 * These routes are separate from the regular user navigation and provide
 * a standalone admin experience with dedicated navigation structure.
 */
object AdminRoutes {
    
    // ========== Core Admin Routes ==========
    const val PORTAL_ROOT = "admin_portal"
    const val DASHBOARD = "admin_portal/dashboard"
    
    // ========== User Management ==========
    object Users {
        const val LIST = "admin_portal/users"
        const val DETAIL = "admin_portal/users/{userId}"
        const val ROLES = "admin_portal/users/roles"
        const val BULK_OPERATIONS = "admin_portal/users/bulk"
        
        fun detail(userId: String) = "admin_portal/users/${URLEncoder.encode(userId, "UTF-8")}"
    }
    
    // ========== Verification ==========
    object Verification {
        const val PENDING = "admin_portal/verification"
        const val DETAIL = "admin_portal/verification/{requestId}"
        const val UPGRADE_REQUESTS = "admin_portal/verification/upgrades"
        
        fun detail(requestId: String) = "admin_portal/verification/${URLEncoder.encode(requestId, "UTF-8")}"
    }
    
    // ========== Commerce Management ==========
    object Commerce {
        const val PRODUCTS = "admin_portal/commerce/products"
        const val PRODUCT_DETAIL = "admin_portal/commerce/products/{productId}"
        const val PRODUCT_MODERATION = "admin_portal/commerce/products/moderation"
        const val ORDERS = "admin_portal/commerce/orders"
        const val ORDER_DETAIL = "admin_portal/commerce/orders/{orderId}"
        const val ORDER_INTERVENTION = "admin_portal/commerce/orders/intervention"
        const val INVOICES = "admin_portal/commerce/invoices"
        const val DISPUTES = "admin_portal/commerce/disputes"
        const val DISPUTE_DETAIL = "admin_portal/commerce/disputes/{disputeId}"
        
        fun productDetail(productId: String) = "admin_portal/commerce/products/${URLEncoder.encode(productId, "UTF-8")}"
        fun orderDetail(orderId: String) = "admin_portal/commerce/orders/${URLEncoder.encode(orderId, "UTF-8")}"
        fun disputeDetail(disputeId: String) = "admin_portal/commerce/disputes/${URLEncoder.encode(disputeId, "UTF-8")}"
    }
    
    // ========== Content Moderation ==========
    object Moderation {
        const val QUEUE = "admin_portal/moderation"
        const val CONTENT_DETAIL = "admin_portal/moderation/{contentId}"
        const val REPORTS = "admin_portal/moderation/reports"
        const val APPEALS = "admin_portal/moderation/appeals"
        
        fun contentDetail(contentId: String) = "admin_portal/moderation/${URLEncoder.encode(contentId, "UTF-8")}"
    }
    
    // ========== System Management ==========
    object System {
        const val CONFIG = "admin_portal/system/config"
        const val HEALTH = "admin_portal/system/health"
        const val FEATURE_TOGGLES = "admin_portal/system/features"
        const val MAINTENANCE = "admin_portal/system/maintenance"
        const val CACHE = "admin_portal/system/cache"
    }
    
    // ========== Biosecurity ==========
    object Biosecurity {
        const val ZONES = "admin_portal/biosecurity/zones"
        const val ADD_ZONE = "admin_portal/biosecurity/add"
        const val ALERTS = "admin_portal/biosecurity/alerts"
    }
    
    // ========== Mortality ==========
    object Mortality {
        const val DASHBOARD = "admin_portal/mortality"
        const val TRENDS = "admin_portal/mortality/trends"
        const val REPORTS = "admin_portal/mortality/reports"
    }
    
    // ========== Monitoring ==========
    object Monitoring {
        const val BIOSECURITY = "admin_portal/monitoring/biosecurity"
        const val MORTALITY = "admin_portal/monitoring/mortality"
        const val ALERTS = "admin_portal/monitoring/alerts"
    }
    
    // ========== Analytics & Reports ==========
    object Analytics {
        const val OVERVIEW = "admin_portal/analytics"
        const val USERS = "admin_portal/analytics/users"
        const val COMMERCE = "admin_portal/analytics/commerce"
        const val ENGAGEMENT = "admin_portal/analytics/engagement"
        const val EXPORTS = "admin_portal/analytics/exports"
    }
    
    object Reports {
        const val GENERATOR = "admin_portal/reports"
        const val TEMPLATES = "admin_portal/reports/templates"
        const val SCHEDULED = "admin_portal/reports/scheduled"
        const val HISTORY = "admin_portal/reports/history"
    }
    
    // ========== Audit & Compliance ==========
    object Audit {
        const val LOGS = "admin_portal/audit"
        const val DETAIL = "admin_portal/audit/{logId}"
        const val EXPORT = "admin_portal/audit/export"
        
        fun detail(logId: String) = "admin_portal/audit/${URLEncoder.encode(logId, "UTF-8")}"
    }
    
    // ========== Communication ==========
    object Communication {
        const val BROADCAST = "admin_portal/communication"
        const val NOTIFICATIONS = "admin_portal/communication/notifications"
        const val ANNOUNCEMENTS = "admin_portal/communication/announcements"
        const val TEMPLATES = "admin_portal/communication/templates"
    }
    
    // ========== Bulk Operations ==========
    object Bulk {
        const val OPERATIONS = "admin_portal/bulk"
        const val IMPORT = "admin_portal/bulk/import"
        const val EXPORT = "admin_portal/bulk/export"
        const val HISTORY = "admin_portal/bulk/history"
    }
    
    /**
     * Admin sidebar section definitions for navigation structure.
     */
    enum class SidebarSection(val label: String, val icon: String) {
        DASHBOARD("Dashboard", "dashboard"),
        USERS("Users", "people"),
        VERIFICATION("Verification", "verified_user"),
        COMMERCE("Commerce", "store"),
        MODERATION("Moderation", "shield"),
        SYSTEM("System", "settings"),
        MONITORING("Monitoring", "monitoring"),
        ANALYTICS("Analytics", "analytics"),
        REPORTS("Reports", "description"),
        AUDIT("Audit", "history"),
        COMMUNICATION("Communication", "campaign"),
        BULK("Bulk Operations", "batch_prediction")
    }
    
    /**
     * Get all accessible admin routes for navigation validation.
     */
    fun allRoutes(): Set<String> = setOf(
        PORTAL_ROOT,
        DASHBOARD,
        Users.LIST,
        Users.DETAIL,
        Users.ROLES,
        Users.BULK_OPERATIONS,
        Verification.PENDING,
        Verification.DETAIL,
        Verification.UPGRADE_REQUESTS,
        Commerce.PRODUCTS,
        Commerce.PRODUCT_DETAIL,
        Commerce.PRODUCT_MODERATION,
        Commerce.ORDERS,
        Commerce.ORDER_DETAIL,
        Commerce.ORDER_INTERVENTION,
        Commerce.INVOICES,
        Commerce.DISPUTES,
        Commerce.DISPUTE_DETAIL,
        Moderation.QUEUE,
        Moderation.CONTENT_DETAIL,
        Moderation.REPORTS,
        Moderation.APPEALS,
        System.CONFIG,
        System.HEALTH,
        System.FEATURE_TOGGLES,
        System.MAINTENANCE,
        System.CACHE,
        Monitoring.BIOSECURITY,
        Monitoring.MORTALITY,
        Monitoring.ALERTS,
        Analytics.OVERVIEW,
        Analytics.USERS,
        Analytics.COMMERCE,
        Analytics.ENGAGEMENT,
        Analytics.EXPORTS,
        Reports.GENERATOR,
        Reports.TEMPLATES,
        Reports.SCHEDULED,
        Reports.HISTORY,
        Audit.LOGS,
        Audit.DETAIL,
        Audit.EXPORT,
        Communication.BROADCAST,
        Communication.NOTIFICATIONS,
        Communication.ANNOUNCEMENTS,
        Communication.TEMPLATES,
        Bulk.OPERATIONS,
        Bulk.IMPORT,
        Bulk.EXPORT,
        Bulk.HISTORY
    )
}
