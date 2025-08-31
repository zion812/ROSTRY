package com.rio.rostry.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey val uid: String = "",
    val name: String = "",
    val email: String = "",
    val phone: String? = null,
    val location: String = "",
    val userType: String = "",
    val language: String = ""
)
