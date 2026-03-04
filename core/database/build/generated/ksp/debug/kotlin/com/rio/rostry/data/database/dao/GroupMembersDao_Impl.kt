package com.rio.rostry.`data`.database.dao

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.rio.rostry.`data`.database.entity.GroupMemberEntity
import javax.`annotation`.processing.Generated
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import kotlin.collections.MutableList
import kotlin.collections.mutableListOf
import kotlin.reflect.KClass
import kotlinx.coroutines.flow.Flow

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class GroupMembersDao_Impl(
  __db: RoomDatabase,
) : GroupMembersDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfGroupMemberEntity: EntityInsertAdapter<GroupMemberEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfGroupMemberEntity = object : EntityInsertAdapter<GroupMemberEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `group_members` (`membershipId`,`groupId`,`userId`,`role`,`joinedAt`) VALUES (?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: GroupMemberEntity) {
        statement.bindText(1, entity.membershipId)
        statement.bindText(2, entity.groupId)
        statement.bindText(3, entity.userId)
        statement.bindText(4, entity.role)
        statement.bindLong(5, entity.joinedAt)
      }
    }
  }

  public override suspend fun upsert(member: GroupMemberEntity): Unit = performSuspending(__db,
      false, true) { _connection ->
    __insertAdapterOfGroupMemberEntity.insert(_connection, member)
  }

  public override fun streamMembers(groupId: String): Flow<List<GroupMemberEntity>> {
    val _sql: String = "SELECT * FROM group_members WHERE groupId = ?"
    return createFlow(__db, false, arrayOf("group_members")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, groupId)
        val _columnIndexOfMembershipId: Int = getColumnIndexOrThrow(_stmt, "membershipId")
        val _columnIndexOfGroupId: Int = getColumnIndexOrThrow(_stmt, "groupId")
        val _columnIndexOfUserId: Int = getColumnIndexOrThrow(_stmt, "userId")
        val _columnIndexOfRole: Int = getColumnIndexOrThrow(_stmt, "role")
        val _columnIndexOfJoinedAt: Int = getColumnIndexOrThrow(_stmt, "joinedAt")
        val _result: MutableList<GroupMemberEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: GroupMemberEntity
          val _tmpMembershipId: String
          _tmpMembershipId = _stmt.getText(_columnIndexOfMembershipId)
          val _tmpGroupId: String
          _tmpGroupId = _stmt.getText(_columnIndexOfGroupId)
          val _tmpUserId: String
          _tmpUserId = _stmt.getText(_columnIndexOfUserId)
          val _tmpRole: String
          _tmpRole = _stmt.getText(_columnIndexOfRole)
          val _tmpJoinedAt: Long
          _tmpJoinedAt = _stmt.getLong(_columnIndexOfJoinedAt)
          _item = GroupMemberEntity(_tmpMembershipId,_tmpGroupId,_tmpUserId,_tmpRole,_tmpJoinedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getMember(groupId: String, userId: String): GroupMemberEntity? {
    val _sql: String = "SELECT * FROM group_members WHERE groupId = ? AND userId = ? LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, groupId)
        _argIndex = 2
        _stmt.bindText(_argIndex, userId)
        val _columnIndexOfMembershipId: Int = getColumnIndexOrThrow(_stmt, "membershipId")
        val _columnIndexOfGroupId: Int = getColumnIndexOrThrow(_stmt, "groupId")
        val _columnIndexOfUserId: Int = getColumnIndexOrThrow(_stmt, "userId")
        val _columnIndexOfRole: Int = getColumnIndexOrThrow(_stmt, "role")
        val _columnIndexOfJoinedAt: Int = getColumnIndexOrThrow(_stmt, "joinedAt")
        val _result: GroupMemberEntity?
        if (_stmt.step()) {
          val _tmpMembershipId: String
          _tmpMembershipId = _stmt.getText(_columnIndexOfMembershipId)
          val _tmpGroupId: String
          _tmpGroupId = _stmt.getText(_columnIndexOfGroupId)
          val _tmpUserId: String
          _tmpUserId = _stmt.getText(_columnIndexOfUserId)
          val _tmpRole: String
          _tmpRole = _stmt.getText(_columnIndexOfRole)
          val _tmpJoinedAt: Long
          _tmpJoinedAt = _stmt.getLong(_columnIndexOfJoinedAt)
          _result = GroupMemberEntity(_tmpMembershipId,_tmpGroupId,_tmpUserId,_tmpRole,_tmpJoinedAt)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun leave(groupId: String, userId: String) {
    val _sql: String = "DELETE FROM group_members WHERE groupId = ? AND userId = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, groupId)
        _argIndex = 2
        _stmt.bindText(_argIndex, userId)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public companion object {
    public fun getRequiredConverters(): List<KClass<*>> = emptyList()
  }
}
