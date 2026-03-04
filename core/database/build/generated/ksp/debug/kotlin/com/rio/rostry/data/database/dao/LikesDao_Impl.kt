package com.rio.rostry.`data`.database.dao

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.rio.rostry.`data`.database.entity.LikeEntity
import javax.`annotation`.processing.Generated
import kotlin.Boolean
import kotlin.Int
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import kotlin.reflect.KClass
import kotlinx.coroutines.flow.Flow

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class LikesDao_Impl(
  __db: RoomDatabase,
) : LikesDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfLikeEntity: EntityInsertAdapter<LikeEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfLikeEntity = object : EntityInsertAdapter<LikeEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `likes` (`likeId`,`postId`,`userId`,`createdAt`) VALUES (?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: LikeEntity) {
        statement.bindText(1, entity.likeId)
        statement.bindText(2, entity.postId)
        statement.bindText(3, entity.userId)
        statement.bindLong(4, entity.createdAt)
      }
    }
  }

  public override suspend fun insert(like: LikeEntity): Unit = performSuspending(__db, false, true)
      { _connection ->
    __insertAdapterOfLikeEntity.insert(_connection, like)
  }

  public override fun countLikes(postId: String): Flow<Int> {
    val _sql: String = "SELECT COUNT(*) FROM likes WHERE postId = ?"
    return createFlow(__db, false, arrayOf("likes")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, postId)
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

  public override fun isLiked(postId: String, userId: String): Flow<Boolean> {
    val _sql: String = "SELECT EXISTS(SELECT 1 FROM likes WHERE postId = ? AND userId = ?)"
    return createFlow(__db, false, arrayOf("likes")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, postId)
        _argIndex = 2
        _stmt.bindText(_argIndex, userId)
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

  public override suspend fun isLikedSuspend(postId: String, userId: String): Boolean {
    val _sql: String = "SELECT EXISTS(SELECT 1 FROM likes WHERE postId = ? AND userId = ?)"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, postId)
        _argIndex = 2
        _stmt.bindText(_argIndex, userId)
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

  public override suspend fun countByUser(userId: String): Int {
    val _sql: String = "SELECT COUNT(*) FROM likes WHERE userId = ?"
    return performSuspending(__db, true, false) { _connection ->
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

  public override suspend fun delete(postId: String, userId: String) {
    val _sql: String = "DELETE FROM likes WHERE postId = ? AND userId = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, postId)
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
