package com.rio.rostry.navigation

sealed class Screen(val route: String) {
    object Analytics : Screen("analytics")
    object FowlRegistration : Screen("fowl_registration")
    object FowlDetail : Screen("fowl_detail/{fowlId}") {
        fun createRoute(fowlId: String) = "fowl_detail/$fowlId"
    }
    object FowlRecordCreation : Screen("fowl_record_creation/{fowlId}") {
        fun createRoute(fowlId: String) = "fowl_record_creation/$fowlId"
    }
    object InitiateTransfer : Screen("initiate_transfer/{fowlId}") {
        fun createRoute(fowlId: String) = "initiate_transfer/$fowlId"
    }
    object TransferVerification : Screen("transfer_verification/{transferId}") {
        fun createRoute(transferId: String) = "transfer_verification/$transferId"
    }
    object FowlHistory : Screen("fowl_history/{fowlId}") {
        fun createRoute(fowlId: String) = "fowl_history/$fowlId"
    }
}
