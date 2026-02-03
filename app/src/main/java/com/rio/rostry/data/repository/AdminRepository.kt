package com.rio.rostry.data.repository

import com.rio.rostry.domain.model.AdminUserProfile
import com.rio.rostry.utils.Resource
import kotlinx.coroutines.flow.Flow

interface AdminRepository {
    /**
     * Fetches a comprehensive profile for a user, including stats and recent activity.
     */
    fun getUserFullProfile(userId: String): Flow<Resource<AdminUserProfile>>
    
    // Future expansion:
    // fun getPlatformOverview(): Flow<Resource<PlatformStats>>
}
