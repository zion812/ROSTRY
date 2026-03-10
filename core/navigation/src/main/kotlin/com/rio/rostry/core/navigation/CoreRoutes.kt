package com.rio.rostry.core.navigation

import com.rio.rostry.domain.model.UserType

/**
 * Common navigation routes used across modules.
 */
object CoreRoutes {
    const val ROOT = "root"
    const val MONITORING_TASKS = "monitoring/tasks"
    const val STORAGE_QUOTA = "storage/quota"
    const val VERIFY_FARMER_LOCATION = "verify/farmer/location"
    const val COMPLIANCE = "compliance"
    const val MONITORING_FARM_LOG = "monitoring/farm_log"

    object Auth {
        const val WELCOME = "auth/welcome"
        const val PHONE = "auth/phone"
        const val OTP = "auth/otp/{verificationId}"
    }
    
    object Farmer {
        const val DASHBOARD = "farmer/dashboard"
        const val ASSETS = "farmer/assets"
        const val CALENDAR = "farmer/calendar"
        const val DIGITAL_FARM = "farmer/digital_farm"
        const val MARKET = "farmer/market"
        const val CREATE = "farmer/create"
    }

    // Keep compatibility with legacy structure where possible
    object FarmerNav {
        const val DASHBOARD = Farmer.DASHBOARD
        const val FARM_ASSETS = Farmer.ASSETS
        const val CALENDAR = Farmer.CALENDAR
        const val DIGITAL_FARM = Farmer.DIGITAL_FARM
        const val MARKET = Farmer.MARKET
        const val CREATE = Farmer.CREATE
    }

    object Monitoring {
        const val TASKS = MONITORING_TASKS
        const val VACCINATION = "monitoring/vaccination"
        const val QUARANTINE = "monitoring/quarantine"
        const val GROWTH = "monitoring/growth"
        const val DAILY_LOG = "monitoring/daily_log"
        const val HATCHING = "monitoring/hatching"
        const val MORTALITY = "monitoring/mortality"
        const val BREEDING = "monitoring/breeding"
        const val FARM_DOCUMENT = "monitoring/farm_document"
        const val EXPENSE_LEDGER = "monitoring/expense_ledger"
    }

    object Order {
        const val MY_ORDERS_FARMER = "orders/farmer"
        const val MY_ORDERS_ENTHUSIAST = "orders/enthusiast"
        const val MY_ORDERS = "orders/my"
        
        fun paymentVerify(id: String) = "orders/payment/verify/$id"
        fun quoteNegotiation(id: String, isBuyer: Boolean) = "orders/quote/$id?isBuyer=$isBuyer"
    }
    
    object Builders {
        fun monitoringVaccinationWithFilter(filter: String) = "monitoring/vaccination?filter=$filter"
        fun monitoringVaccinationWithProductId(id: String) = "monitoring/vaccination/product/$id"
        fun monitoringTasks(filter: String) = "monitoring/tasks?filter=$filter"
        fun monitoringQuarantine(filter: String) = "monitoring/quarantine?filter=$filter"
        fun monitoringGrowthWithFilter(filter: String) = "monitoring/growth?filter=$filter"
        fun monitoringGrowthWithProductId(id: String) = "monitoring/growth/product/$id"
        fun monitoringDailyLog() = "monitoring/daily_log"
        fun monitoringHatching() = "monitoring/hatching"
        fun monitoringMortality() = "monitoring/mortality"
        fun monitoringBreeding() = "monitoring/breeding"
        fun productsWithFilter(filter: String) = "market/list?filter=$filter"
        fun upgradeWizard(role: com.rio.rostry.domain.model.UserType) = "upgrade/wizard/${role.name}"
        fun createListingFromAsset(assetId: String) = "farmer/create_listing/$assetId"
        fun transfersWithFilter(filter: String) = "transfers/list?filter=$filter"
    }
}
