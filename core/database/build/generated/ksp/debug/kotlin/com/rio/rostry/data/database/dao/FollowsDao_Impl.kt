package com.rio.rostry.`data`.database.dao

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.rio.rostry.`data`.database.entity.FollowEntity
import javax.`annotation`.processing.Generated
import kotlin.Boolean
import kotlin.Int
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
public class FollowsDao_Impl(
  __db: RoomDatabase,
) : FollowsDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfFollowEntity: EntityInsertAdapter<FollowEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfFollowEntity = object : EntityInsertAdapter<FollowEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `follows` (`followId`,`followerId`,`followedId`,`createdAt`) VALUES (?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: FollowEntity) {
        statement.bindText(1, entity.followId)
        statement.bindText(2, entity.followerId)
        statement.bindText(3, entity.followedId)
        statement.bindLong(4, entity.createdAt)
      }
    }
  }

  public override suspend fun upsert(follow: FollowEntity): Unit = performSuspending(__db, false,
      true) { _connection ->
    __insertAdapterOfFollowEntity.insert(_connection, follow)
  }

  public override fun followersCount(userId: String): Flow<Int> {
    val _sql: String = "SELECT COUNT(*) FROM follows WHERE followedId = ?"
    return createFlow(__db, false, arrayOf("follows")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, userId)
        val _result: Int
        if (_stmt.step()) {
          val _tmp: Int
          _tmp = _stmt.getLong(0).toInt()
          _result = _tmp
        } else {
          _result = 0
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun followingCount(userId: String): Flow<Int> {
    val _sql: String = "SELECT COUNT(*) FROM follows WHERE followerId = ?"
    return createFlow(__db, false, arrayOf("follows")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, userId)
        val _result: Int
        if (_stmt.step()) {
          val _tmp: Int
          _tmp = _stmt.getLong(0).toInt()
          _result = _tmp
        } else {
          _result = 0
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun followingIds(userId: String): Flow<List<String>> {
    val _sql: String = "SELECT followedId FROM follows WHERE followerId = ?"
    return createFlow(__db, false, arrayOf("follows")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, userId)
        val _result: MutableList<String> = mutableListOf()
        while (_stmt.step()) {
          val _item: String
          _item = _stmt.getText(0)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun isFollowing(followerId: String, followedId: String): Flow<Boolean> {
    val _sql: String =
        "SELECT EXISTS(SELECT 1 FROM follows WHERE followerId = ? AND followedId = ?)"
    return createFlow(__db, false, arrayOf("follows")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, followerId)
        _argIndex = 2
        _stmt.bindText(_argIndex, followedId)
        val _result: Boolean
        if (_stmt.step()) {
          val _tmp: Int
          _tmp = _stmt.getLong(0).toInt()
          _result = _tmp != 0
        } else {
          _result = false
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun isFollowingSuspend(followerId: String, followedId: String): Boolean {
    val _sql: String =
        "SELECT EXISTS(SELECT 1 FROM follows WHERE followerId = ? AND followedId = ?)"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, followerId)
        _argIndex = 2
        _stmt.bindText(_argIndex, followedId)
        val _result: Boolean
        if (_stmt.step()) {
          val _tmp: Int
          _tmp = _stmt.getLong(0).toInt()
          _result = _tmp != 0
        } else {
          _result = false
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun unfollow(followerId: String, followedId: String) {
    val _sql: String = "DELETE FROM follows WHERE followerId = ? AND followedId = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, followerId)
        _argIndex = 2
        _stmt.bindText(_argIndex, followedId)
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
