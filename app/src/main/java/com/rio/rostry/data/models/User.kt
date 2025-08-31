package com.rio.rostry.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.firestore.PropertyName

@Entity(tableName = "users")
data class User(
    @PrimaryKey val uid: String = "",
    val name: String = "",
    val email: String = "",
    val phone: String? = null,
    val location: String = "",
    @get:PropertyName("userType") // Use PropertyName to guide Firestore
    val userType: UserType = UserType.General,
    val language: String = "",
    val isVerified: Boolean = false
) {
    // Add a no-argument constructor for Firestore deserialization
    constructor() : this("", "", "", null, "", UserType.General, "", false)
}
