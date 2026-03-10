package com.rio.rostry.ui.navigation

/**
 * Temporary route constants for files that still reference Routes.kt
 * These should be migrated to use NavigationProvider local routes or hardcoded strings
 * 
 * Task 6.2 - Migration helper during Routes.kt removal
 */
object RouteConstants {
    // Monitoring routes
    const val MONITORING_QUARANTINE = "monitoring/quarantine"
    const val MONITORING_VACCINATION = "monitoring/vaccination"
    const val MONITORING_DAILY_LOG = "monitoring/daily_log"
    const val MONITORING_GROWTH = "monitoring/growth"
    const val MONITORING_TASKS = "monitoring/tasks"
    const val MONITORING_PERFORMANCE = "monitoring/performance"
    
    // Farmer routes
    const val FARMER_FARM_ASSETS = "farmer/farm_assets"
    const val FARMER_MARKET = "farmer/market"
    const val FARMER_CREATE = "farmer/create"
    
    // Storage and settings
    const val STORAGE_QUOTA = "storage/quota"
    const val VERIFY_FARMER_LOCATION = "verify/farmer/location"
    
    // Compliance
    const val COMPLIANCE = "compliance"
    
    // Order routes
    const val MY_ORDERS_FARMER = "orders/farmer"
    
    // Monitoring detail routes
    const val FARM_DOCUMENT = "monitoring/farm_document"
    const val EXPENSE_LEDGER = "monitoring/expenses"
    
    // Builder functions
    object Builders {
        fun monitoringVaccinationWithFilter(filter: String) = "monitoring/vaccination?filter=$filter"
        fun monitoringHatching() = "monitoring/hatching"
        fun monitoringTasks(filter: String) = "monitoring/tasks?filter=$filter"
        fun monitoringDailyLog() = "monitoring/daily_log"
        fun monitoringGrowthWithFilter(filter: String) = "monitoring/growth?filter=$filter"
        fun monitoringQuarantine(filter: String) = "monitoring/quarantine?filter=$filter"
        fun monitoringMortality() = "monitoring/mortality"
        fun monitoringBreeding() = "monitoring/breeding"
        fun monitoringVaccinationWithProductId(productId: String) = "monitoring/vaccination?productId=$productId"
        fun monitoringGrowthWithProductId(productId: String) = "monitoring/growth?productId=$productId"
        fun productsWithFilter(filter: String) = "product/market?filter=$filter"
        fun transfersWithFilter(status: String) = "transfer/list?status=$status"
        fun createListingFromAsset(assetId: String) = "farmer/create_listing/$assetId"
        fun paymentVerify(paymentId: String) = "order/payment/$paymentId/verify"
        fun quoteNegotiation(quoteId: String, isBuyer: Boolean) = 
            if (isBuyer) "order/quote/$quoteId/buyer" else "order/quote/$quoteId/seller"
    }
    
    // Order object for compatibility
    object Order {
        const val MY_ORDERS_FARMER = "orders/farmer"
        
        fun paymentVerify(paymentId: String) = "order/payment/$paymentId/verify"
        fun quoteNegotiation(quoteId: String, isBuyer: Boolean) = 
            if (isBuyer) "order/quote/$quoteId/buyer" else "order/quote/$quoteId/seller"
    }
    
    // Monitoring object for compatibility
    object Monitoring {
        const val FARM_DOCUMENT = "monitoring/farm_document"
        const val EXPENSE_LEDGER = "monitoring/expenses"
    }
    
    // FarmerNav object for compatibility
    object FarmerNav {
        const val FARM_ASSETS = "farmer/farm_assets"
        const val MARKET = "farmer/market"
        const val CREATE = "farmer/create"
    }
}
