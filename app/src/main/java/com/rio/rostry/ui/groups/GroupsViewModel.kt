package com.rio.rostry.ui.groups

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.database.dao.GroupMembersDao
import com.rio.rostry.data.database.dao.GroupsDao
import com.rio.rostry.data.database.entity.GroupEntity
import com.rio.rostry.data.database.entity.GroupMemberEntity
import com.rio.rostry.session.CurrentUserProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class GroupsViewModel @Inject constructor(
    private val groupsDao: GroupsDao,
    private val membersDao: GroupMembersDao,
    private val currentUserProvider: CurrentUserProvider
) : ViewModel() {

    data class UiState(
        val groups: List<GroupEntity> = emptyList(),
        val memberships: Map<String, Boolean> = emptyMap(),
        val error: String? = null
    )

    val ui: StateFlow<UiState> = combine(
        groupsDao.streamAll(),
        currentUserProvider.userIdOrNull()?.let { uid ->
            // Aggregate membership flags per group for this user
            combineGroupMemberships(uid)
        } ?: kotlinx.coroutines.flow.flowOf(emptyMap())
    ) { groups, membershipMap ->
        UiState(groups = groups, memberships = membershipMap, error = null)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), UiState())

    private fun combineGroupMemberships(userId: String): StateFlow<Map<String, Boolean>> =
        // Placeholder implementation: mark none as member until a per-user membership stream is available.
        groupsDao.streamAll()
            .map { groups ->
                val map = mutableMapOf<String, Boolean>()
                groups.forEach { g -> map[g.groupId] = false }
                map
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyMap())

    fun join(groupId: String) {
        val userId = currentUserProvider.userIdOrNull() ?: return
        viewModelScope.launch {
            val entity = GroupMemberEntity(
                membershipId = UUID.randomUUID().toString(),
                groupId = groupId,
                userId = userId,
                role = "MEMBER",
                joinedAt = System.currentTimeMillis()
            )
            membersDao.upsert(entity)
        }
    }

    fun leave(groupId: String) {
        val userId = currentUserProvider.userIdOrNull() ?: return
        viewModelScope.launch {
            membersDao.leave(groupId, userId)
        }
    }
}
