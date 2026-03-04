package com.rio.rostry.`data`.database.dao

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.rio.rostry.`data`.database.entity.RateLimitEntity
import javax.`annotation`.processing.Generated
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import kotlin.reflect.KClass

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class RateLimitDao_Impl(
  __db: RoomDatabase,
) : RateLimitDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfRateLimitEntity: EntityInsertAdapter<RateLimitEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfRateLimitEntity = object : EntityInsertAdapter<RateLimitEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `rate_limits` (`id`,`userId`,`action`,`lastAt`) VALUES (?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: RateLimitEntity) {
        statement.bindText(1, entity.id)
        statement.bindText(2, entity.userId)
        statement.bindText(3, entity.action)
        statement.bindLong(4, entity.lastAt)
      }
    }
  }

  public override suspend fun upsert(limit: RateLimitEntity): Unit = performSuspending(__db, false,
      true) { _connection ->
    __insertAdapterOfRateLimitEntity.insert(_connection, limit)
  }

  public override suspend fun `get`(userId: String, action: String): RateLimitEntity? {
    val _sql: String = "SELECT * FROM rate_limits WHERE userId = ? AND action = ? LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, userId)
        _argIndex = 2
        _stmt.bindText(_argIndex, action)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfUserId: Int = getColumnIndexOrThrow(_stmt, "userId")
        val _columnIndexOfAction: Int = getColumnIndexOrThrow(_stmt, "action")
        val _columnIndexOfLastAt: Int = getColumnIndexOrThrow(_stmt, "lastAt")
        val _result: RateLimitEntity?
        if (_stmt.step()) {
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpUserId: String
          _tmpUserId = _stmt.getText(_columnIndexOfUserId)
          val _tmpAction: String
          _tmpAction = _stmt.getText(_columnIndexOfAction)
          val _tmpLastAt: Long
          _tmpLastAt = _stmt.getLong(_columnIndexOfLastAt)
          _result = RateLimitEntity(_tmpId,_tmpUserId,_tmpAction,_tmpLastAt)
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
