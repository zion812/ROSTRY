package com.rio.rostry.`data`.database.dao

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.rio.rostry.`data`.database.entity.GroupEntity
import javax.`annotation`.processing.Generated
import kotlin.Boolean
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
public class GroupsDao_Impl(
  __db: RoomDatabase,
) : GroupsDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfGroupEntity: EntityInsertAdapter<GroupEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfGroupEntity = object : EntityInsertAdapter<GroupEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `groups` (`groupId`,`name`,`description`,`ownerId`,`category`,`isMarketplace`,`createdAt`) VALUES (?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: GroupEntity) {
        statement.bindText(1, entity.groupId)
        statement.bindText(2, entity.name)
        val _tmpDescription: String? = entity.description
        if (_tmpDescription == null) {
          statement.bindNull(3)
        } else {
          statement.bindText(3, _tmpDescription)
        }
        statement.bindText(4, entity.ownerId)
        val _tmpCategory: String? = entity.category
        if (_tmpCategory == null) {
          statement.bindNull(5)
        } else {
          statement.bindText(5, _tmpCategory)
        }
        val _tmp: Int = if (entity.isMarketplace) 1 else 0
        statement.bindLong(6, _tmp.toLong())
        statement.bindLong(7, entity.createdAt)
      }
    }
  }

  public override suspend fun upsert(group: GroupEntity): Unit = performSuspending(__db, false,
      true) { _connection ->
    __insertAdapterOfGroupEntity.insert(_connection, group)
  }

  public override fun streamAll(): Flow<List<GroupEntity>> {
    val _sql: String = "SELECT * FROM groups ORDER BY createdAt DESC"
    return createFlow(__db, false, arrayOf("groups")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfGroupId: Int = getColumnIndexOrThrow(_stmt, "groupId")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfOwnerId: Int = getColumnIndexOrThrow(_stmt, "ownerId")
        val _columnIndexOfCategory: Int = getColumnIndexOrThrow(_stmt, "category")
        val _columnIndexOfIsMarketplace: Int = getColumnIndexOrThrow(_stmt, "isMarketplace")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _result: MutableList<GroupEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: GroupEntity
          val _tmpGroupId: String
          _tmpGroupId = _stmt.getText(_columnIndexOfGroupId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpDescription: String?
          if (_stmt.isNull(_columnIndexOfDescription)) {
            _tmpDescription = null
          } else {
            _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          }
          val _tmpOwnerId: String
          _tmpOwnerId = _stmt.getText(_columnIndexOfOwnerId)
          val _tmpCategory: String?
          if (_stmt.isNull(_columnIndexOfCategory)) {
            _tmpCategory = null
          } else {
            _tmpCategory = _stmt.getText(_columnIndexOfCategory)
          }
          val _tmpIsMarketplace: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsMarketplace).toInt()
          _tmpIsMarketplace = _tmp != 0
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          _item =
              GroupEntity(_tmpGroupId,_tmpName,_tmpDescription,_tmpOwnerId,_tmpCategory,_tmpIsMarketplace,_tmpCreatedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getById(groupId: String): GroupEntity? {
    val _sql: String = "SELECT * FROM groups WHERE groupId = ? LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, groupId)
        val _columnIndexOfGroupId: Int = getColumnIndexOrThrow(_stmt, "groupId")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfOwnerId: Int = getColumnIndexOrThrow(_stmt, "ownerId")
        val _columnIndexOfCategory: Int = getColumnIndexOrThrow(_stmt, "category")
        val _columnIndexOfIsMarketplace: Int = getColumnIndexOrThrow(_stmt, "isMarketplace")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _result: GroupEntity?
        if (_stmt.step()) {
          val _tmpGroupId: String
          _tmpGroupId = _stmt.getText(_columnIndexOfGroupId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpDescription: String?
          if (_stmt.isNull(_columnIndexOfDescription)) {
            _tmpDescription = null
          } else {
            _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          }
          val _tmpOwnerId: String
          _tmpOwnerId = _stmt.getText(_columnIndexOfOwnerId)
          val _tmpCategory: String?
          if (_stmt.isNull(_columnIndexOfCategory)) {
            _tmpCategory = null
          } else {
            _tmpCategory = _stmt.getText(_columnIndexOfCategory)
          }
          val _tmpIsMarketplace: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsMarketplace).toInt()
          _tmpIsMarketplace = _tmp != 0
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          _result =
              GroupEntity(_tmpGroupId,_tmpName,_tmpDescription,_tmpOwnerId,_tmpCategory,_tmpIsMarketplace,_tmpCreatedAt)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public companion object {
    public fun getRequiredConverters(): List<KClass<*>> = emptyList()
  }
}
