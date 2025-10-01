package com.rio.rostry.data.repository

import com.rio.rostry.data.database.dao.*
import com.rio.rostry.data.database.entity.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.combine
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

interface CommunityRepository {
    // Group operations
    suspend fun createGroup(name: String, description: String?, ownerId: String, category: String?): String
    suspend fun getGroup(groupId: String): GroupEntity?
    fun getAllGroups(): Flow<List<GroupEntity>>
    fun getGroupsByCategory(category: String): Flow<List<GroupEntity>>
    fun getUserGroups(userId: String): Flow<List<GroupEntity>>
    fun searchGroups(query: String): Flow<List<GroupEntity>>

    // Group membership
    suspend fun joinGroup(groupId: String, userId: String, role: String = "MEMBER")
    suspend fun leaveGroup(groupId: String, userId: String)
    fun getGroupMembers(groupId: String): Flow<List<GroupMemberEntity>>
    suspend fun updateMemberRole(groupId: String, userId: String, newRole: String)

    // Event operations
    suspend fun createEvent(groupId: String?, title: String, description: String?, location: String?, startTime: Long, endTime: Long?): String
    suspend fun getEvent(eventId: String): EventEntity?
    fun getUpcomingEvents(now: Long): Flow<List<EventEntity>>
    fun getGroupEvents(groupId: String): Flow<List<EventEntity>>
    fun getUserEvents(userId: String): Flow<List<EventEntity>>

    // RSVP operations
    suspend fun rsvpToEvent(eventId: String, userId: String, status: String)
    fun getEventRsvps(eventId: String): Flow<List<EventRsvpEntity>>
    fun getUserRsvps(userId: String): Flow<List<EventRsvpEntity>>
}

@Singleton
class CommunityRepositoryImpl @Inject constructor(
    private val groupsDao: GroupsDao,
    private val groupMembersDao: GroupMembersDao,
    private val eventsDao: EventsDao,
    private val eventRsvpsDao: EventRsvpsDao
) : CommunityRepository {

    override suspend fun createGroup(name: String, description: String?, ownerId: String, category: String?): String {
        val groupId = UUID.randomUUID().toString()
        val now = System.currentTimeMillis()
        groupsDao.upsert(
            GroupEntity(
                groupId = groupId,
                name = name,
                description = description,
                ownerId = ownerId,
                category = category,
                createdAt = now
            )
        )
        // Automatically add owner as admin
        joinGroup(groupId, ownerId, "ADMIN")
        return groupId
    }

    override suspend fun getGroup(groupId: String): GroupEntity? {
        return groupsDao.getById(groupId)
    }

    override fun getAllGroups(): Flow<List<GroupEntity>> = groupsDao.streamAll()

    override fun getGroupsByCategory(category: String): Flow<List<GroupEntity>> {
        return groupsDao.streamAll().map { groups ->
            groups.filter { it.category == category }
        }
    }

    override fun getUserGroups(userId: String): Flow<List<GroupEntity>> {
        return combine(
            groupMembersDao.streamMembers(""), // We'll need to query by userId
            groupsDao.streamAll()
        ) { members, groups ->
            val userGroupIds = members.filter { it.userId == userId }.map { it.groupId }.toSet()
            groups.filter { it.groupId in userGroupIds }
        }
    }

    override fun searchGroups(query: String): Flow<List<GroupEntity>> {
        return groupsDao.streamAll().map { groups ->
            groups.filter {
                it.name.contains(query, ignoreCase = true) ||
                it.description?.contains(query, ignoreCase = true) == true
            }
        }
    }

    override suspend fun joinGroup(groupId: String, userId: String, role: String) {
        val membershipId = UUID.randomUUID().toString()
        groupMembersDao.upsert(
            GroupMemberEntity(
                membershipId = membershipId,
                groupId = groupId,
                userId = userId,
                role = role,
                joinedAt = System.currentTimeMillis()
            )
        )
    }

    override suspend fun leaveGroup(groupId: String, userId: String) {
        groupMembersDao.leave(groupId, userId)
    }

    override fun getGroupMembers(groupId: String): Flow<List<GroupMemberEntity>> {
        return groupMembersDao.streamMembers(groupId)
    }

    override suspend fun updateMemberRole(groupId: String, userId: String, newRole: String) {
        val member = groupMembersDao.getMember(groupId, userId)
        if (member != null) {
            groupMembersDao.upsert(member.copy(role = newRole))
        }
    }

    override suspend fun createEvent(
        groupId: String?,
        title: String,
        description: String?,
        location: String?,
        startTime: Long,
        endTime: Long?
    ): String {
        val eventId = UUID.randomUUID().toString()
        eventsDao.upsert(
            EventEntity(
                eventId = eventId,
                groupId = groupId,
                title = title,
                description = description,
                location = location,
                startTime = startTime,
                endTime = endTime
            )
        )
        return eventId
    }

    override suspend fun getEvent(eventId: String): EventEntity? {
        return eventsDao.getById(eventId)
    }

    override fun getUpcomingEvents(now: Long): Flow<List<EventEntity>> {
        return eventsDao.streamUpcoming(now)
    }

    override fun getGroupEvents(groupId: String): Flow<List<EventEntity>> {
        return eventsDao.streamUpcoming(0).map { events ->
            events.filter { it.groupId == groupId }
        }
    }

    override fun getUserEvents(userId: String): Flow<List<EventEntity>> {
        return combine(
            eventRsvpsDao.streamForUser(userId),
            eventsDao.streamUpcoming(0)
        ) { rsvps, events ->
            val eventIds = rsvps.map { it.eventId }.toSet()
            events.filter { it.eventId in eventIds }
        }
    }

    override suspend fun rsvpToEvent(eventId: String, userId: String, status: String) {
        val rsvpId = UUID.randomUUID().toString()
        eventRsvpsDao.upsert(
            EventRsvpEntity(
                id = rsvpId,
                eventId = eventId,
                userId = userId,
                status = status,
                updatedAt = System.currentTimeMillis()
            )
        )
    }

    override fun getEventRsvps(eventId: String): Flow<List<EventRsvpEntity>> {
        return eventRsvpsDao.streamForEvent(eventId)
    }

    override fun getUserRsvps(userId: String): Flow<List<EventRsvpEntity>> {
        return eventRsvpsDao.streamForUser(userId)
    }
}
