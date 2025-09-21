package com.rio.rostry.domain.model

enum class UserType {
    GENERAL,
    FARMER,
    ENTHUSIAST;

    fun nextLevel(): UserType? = when (this) {
        GENERAL -> FARMER
        FARMER -> ENTHUSIAST
        ENTHUSIAST -> null
    }
}
