package com.rio.rostry.domain.session

import com.rio.rostry.data.repository.UserRepository
import com.rio.rostry.session.CurrentUserProvider
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of SessionRefresher using UserRepository.
 * 
 * This implementation refreshes the session state after role upgrades
 * by reloading user data from the repository, which triggers observers
 * to update the UI with the latest user information.
 */
@Singleton
class SessionRefresherImpl @Inject constructor(
    private val userRepository: UserRepository,
    private val currentUserProvider: CurrentUserProvider
) : SessionRefresher {
    override suspend fun refreshUserProfile() {
        // Trigger a fresh fetch from repository which updates all observers
        val userId = currentUserProvider.userIdOrNull() ?: return
        userRepository.getUserById(userId).first()
    }
}
