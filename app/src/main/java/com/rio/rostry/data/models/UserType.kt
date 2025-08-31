package com.rio.rostry.data.models

sealed class UserType(val role: String) {
    object General : UserType("general")
    object Farmer : UserType("farmer")
    object HighLevelEnthusiast : UserType("high_level_enthusiast")
}
