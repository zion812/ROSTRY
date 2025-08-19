package com.rio.rostry.data.models

import com.google.firebase.firestore.DocumentId

data class User(
    @DocumentId
    var userId: String = "",
    var name: String = "",
    var email: String = "",
    var phone: String = "",
    var type: UserType = UserType.GENERAL,
    var profilePictureUrl: String? = null,
    var createdAt: Long = 0L,
    var updatedAt: Long = 0L
)

enum class UserType {
    FARMER,
    ENTHUSIAST,
    GENERAL
}