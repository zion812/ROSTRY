package com.rio.rostry.navigation

sealed class Screen(val route: String) {
    object FowlRegistration : Screen("fowl_registration")
    object FowlDetail : Screen("fowl_detail/{fowlId}") {
        fun createRoute(fowlId: String) = "fowl_detail/$fowlId"
    }
    object FowlRecordCreation : Screen("fowl_record_creation/{fowlId}") {
        fun createRoute(fowlId: String) = "fowl_record_creation/$fowlId"
    }
}
