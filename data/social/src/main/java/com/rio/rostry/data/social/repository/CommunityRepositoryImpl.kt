package com.rio.rostry.data.social.repository

import com.rio.rostry.core.model.Result
import com.rio.rostry.core.model.Event
import com.rio.rostry.core.model.EventRsvp
import com.rio.rostry.core.model.Group
import com.rio.rostry.core.model.GroupMember
import com.rio.rostry.data.database.dao.EventRsvpsDao
import com.rio.rostry.data.database.dao.EventsDao
import com.rio.rostry.data.database.dao.GroupMembersDao
import com.rio.rostry.data.database.dao.GroupsDao
import com.rio.rostry.data.database.entity.EventEntity
import com.rio.rostry.data.database.entity.EventRsvpEntity
import com.rio.rostry.data.database.entity.GroupEntity
import com.rio.rostry.data.database.entity.GroupMemberEntity
import com.rio.rostry.data.social.mapper.toEvent
import com.rio.rostry.data.social.mapper.toEventRsvp
import com.rio.rostry.data.social.mapper.toGroup
import com.rio.rostry.data.social.mapper.toGroupMember
import com.rio.rostry.domain.social.repository.CommunityRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of community operations including groups and events.
 */
@Singleton
class CommunityRepositoryImpl @Inject constructor(
    private val groupsDao: GroupsDao,
    private val groupMembersDao: GroupMembersDao,
    private val eventsDao: EventsDao,
    private val eventRsvpsDao: EventRsvpsDao
) : CommunityRepository {

    override suspend fun createGroup(
        name: String,
        description: String?,
        ownerId: String,
        category: String?
    ): Result<String> {
        return try {
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
            Result.Success(groupId)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun getGroup(groupId: String): Result<Group?> {
        return try {
            val entity = groupsDao.getById(groupId)
            Result.Success(entity?.toGroup())
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override fun getAllGroups(): Flow<List<Group>> {
        return groupsDao.streamAll().map { entities ->
            entities.map { it.toGroup() }
        }
    }

    override fun getGroupsByCategory(category: String): Flow<List<Group>> {
        return groupsDao.streamAll().map { entities ->
            entities.filter { it.category == category }.map { it.toGroup() }
        }
    }

    override fun getUserGroups(userId: String): Flow<List<Group>> {
        return combine(
            groupMembersDao.streamMembers(""), // We'll need to query by userId
            groupsDao.streamAll()
        ) { members, groups ->
            val userGroupIds = members.filter { it.userId == userId }.map { it.groupId }.toSet()
            groups.filter { it.groupId in userGroupIds }.map { it.toGroup() }
        }
    }

    override fun searchGroups(query: String): Flow<List<Group>> {
        return groupsDao.streamAll().map { entities ->
            entities.filter {
                it.name.contains(query, ignoreCase = true) ||
                it.description?.contains(query, ignoreCase = true) == true
            }.map { it.toGroup() }
        }
    }

    override suspend fun joinGroup(groupId: String, userId: String, role: String): Result<Unit> {
        return try {
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
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun leaveGroup(groupId: String, userId: String): Result<Unit> {
        return try {
            groupMembersDao.leave(groupId, userId)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override fun getGroupMembers(groupId: String): Flow<List<GroupMember>> {
        return groupMembersDao.streamMembers(groupId).map { entities ->
            entities.map { it.toGroupMember() }
        }
    }

    override suspend fun updateMemberRole(groupId: String, userId: String, newRole: String): Result<Unit> {
        return try {
            val member = groupMembersDao.getMember(groupId, userId)
            if (member != null) {
                groupMembersDao.upsert(member.copy(role = newRole))
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun createEvent(
        groupId: String?,
        title: String,
        description: String?,
        location: String?,
        startTime: Long,
        endTime: Long?
    ): Result<String> {
        return try {
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
            Result.Success(eventId)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun getEvent(eventId: String): Result<Event?> {
        return try {
            val entity = eventsDao.getById(eventId)
            Result.Success(entity?.toEvent())
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override fun getUpcomingEvents(now: Long): Flow<List<Event>> {
        return eventsDao.streamUpcoming(now).map { entities ->
            entities.map { it.toEvent() }
        }
    }

    override fun getGroupEvents(groupId: String): Flow<List<Event>> {
        return eventsDao.streamUpcoming(0).map { entities ->
            entities.filter { it.groupId == groupId }.map { it.toEvent() }
        }
    }

    override fun getUserEvents(userId: String): Flow<List<Event>> {
        return combine(
            eventRsvpsDao.streamForUser(userId),
            eventsDao.streamUpcoming(0)
        ) { rsvps, events ->
            val eventIds = rsvps.map { it.eventId }.toSet()
            events.filter { it.eventId in eventIds }.map { it.toEvent() }
        }
    }

    override suspend fun rsvpToEvent(eventId: String, userId: String, status: String): Result<Unit> {
        return try {
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
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override fun getEventRsvps(eventId: String): Flow<List<EventRsvp>> {
        return eventRsvpsDao.streamForEvent(eventId).map { entities ->
            entities.map { it.toEventRsvp() }
        }
    }

    override fun getUserRsvps(userId: String): Flow<List<EventRsvp>> {
        return eventRsvpsDao.streamForUser(userId).map { entities ->
            entities.map { it.toEventRsvp() }
        }
    }
}

