package com.rio.rostry.domain.social.repository

import com.rio.rostry.core.model.Result
import com.rio.rostry.core.model.Event
import com.rio.rostry.core.model.EventRsvp
import com.rio.rostry.core.model.Group
import com.rio.rostry.core.model.GroupMember
import kotlinx.coroutines.flow.Flow

/**
 * Repository contract for community operations including groups and events.
 */
interface CommunityRepository {
    // Group operations
    suspend fun createGroup(name: String, description: String?, ownerId: String, category: String?): Result<String>
    suspend fun getGroup(groupId: String): Result<Group?>
    fun getAllGroups(): Flow<List<Group>>
    fun getGroupsByCategory(category: String): Flow<List<Group>>
    fun getUserGroups(userId: String): Flow<List<Group>>
    fun getUserGroupIds(userId: String): Flow<List<String>>
    fun searchGroups(query: String): Flow<List<Group>>

    // Community suggestions
    fun getFriendsCommunities(friendIds: List<String>): Flow<List<Group>>
    fun getUserCommunities(userId: String): Flow<List<Group>>

    // Group membership
    suspend fun joinGroup(groupId: String, userId: String, role: String = "MEMBER"): Result<Unit>
    suspend fun leaveGroup(groupId: String, userId: String): Result<Unit>
    fun getGroupMembers(groupId: String): Flow<List<GroupMember>>
    suspend fun updateMemberRole(groupId: String, userId: String, newRole: String): Result<Unit>

    // Event operations
    suspend fun createEvent(
        groupId: String?,
        title: String,
        description: String?,
        location: String?,
        startTime: Long,
        endTime: Long?
    ): Result<String>
    suspend fun getEvent(eventId: String): Result<Event?>
    fun getUpcomingEvents(now: Long): Flow<List<Event>>
    fun getGroupEvents(groupId: String): Flow<List<Event>>
    fun getUserEvents(userId: String): Flow<List<Event>>

    // RSVP operations
    suspend fun rsvpToEvent(eventId: String, userId: String, status: String): Result<Unit>
    fun getEventRsvps(eventId: String): Flow<List<EventRsvp>>
    fun getUserRsvps(userId: String): Flow<List<EventRsvp>>
}

