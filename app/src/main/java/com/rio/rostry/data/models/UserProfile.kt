package com.rio.rostry.data.models

import com.google.firebase.firestore.DocumentId

/**
 * Data class representing a user's profile in Firestore.
 *
 * @param uid The unique ID of the user (typically from Firebase Authentication).
 * @param name The user's display name.
 * @param email The user's email address.
 * @param userType The type of user (e.g., "student", "teacher", "admin").
 * @param location The user's location (e.g., city, country).
 */
data class UserProfile(
    @DocumentId
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val userType: String = "",
    val location: String = ""
)