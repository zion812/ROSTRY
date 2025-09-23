package com.rio.rostry.session

import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject
import javax.inject.Singleton

interface CurrentUserProvider {
    fun userIdOrNull(): String?
}

@Singleton
class FirebaseCurrentUserProvider @Inject constructor(
    private val auth: FirebaseAuth
) : CurrentUserProvider {
    override fun userIdOrNull(): String? = auth.currentUser?.uid
}
