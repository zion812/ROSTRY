package com.rio.rostry.`data`.database.dao

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.rio.rostry.`data`.database.entity.AlertEntity
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
public class AlertDao_Impl(
  __db: RoomDatabase,
) : AlertDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfAlertEntity: EntityInsertAdapter<AlertEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfAlertEntity = object : EntityInsertAdapter<AlertEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `alerts` (`id`,`userId`,`title`,`message`,`severity`,`type`,`relatedId`,`createdAt`,`isRead`,`isDismissed`) VALUES (?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: AlertEntity) {
        statement.bindText(1, entity.id)
        statement.bindText(2, entity.userId)
        statement.bindText(3, entity.title)
        statement.bindText(4, entity.message)
        statement.bindText(5, entity.severity)
        statement.bindText(6, entity.type)
        val _tmpRelatedId: String? = entity.relatedId
        if (_tmpRelatedId == null) {
          statement.bindNull(7)
        } else {
          statement.bindText(7, _tmpRelatedId)
        }
        statement.bindLong(8, entity.createdAt)
        val _tmp: Int = if (entity.isRead) 1 else 0
        statement.bindLong(9, _tmp.toLong())
        val _tmp_1: Int = if (entity.isDismissed) 1 else 0
        statement.bindLong(10, _tmp_1.toLong())
      }
    }
  }

  public override suspend fun insert(alert: AlertEntity): Unit = performSuspending(__db, false,
      true) { _connection ->
    __insertAdapterOfAlertEntity.insert(_connection, alert)
  }

  public override fun streamAlerts(userId: String): Flow<List<AlertEntity>> {
    val _sql: String =
        "SELECT * FROM alerts WHERE userId = ? AND isDismissed = 0 ORDER BY createdAt DESC"
    return createFlow(__db, false, arrayOf("alerts")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, userId)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfUserId: Int = getColumnIndexOrThrow(_stmt, "userId")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfMessage: Int = getColumnIndexOrThrow(_stmt, "message")
        val _columnIndexOfSeverity: Int = getColumnIndexOrThrow(_stmt, "severity")
        val _columnIndexOfType: Int = getColumnIndexOrThrow(_stmt, "type")
        val _columnIndexOfRelatedId: Int = getColumnIndexOrThrow(_stmt, "relatedId")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfIsRead: Int = getColumnIndexOrThrow(_stmt, "isRead")
        val _columnIndexOfIsDismissed: Int = getColumnIndexOrThrow(_stmt, "isDismissed")
        val _result: MutableList<AlertEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: AlertEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpUserId: String
          _tmpUserId = _stmt.getText(_columnIndexOfUserId)
          val _tmpTitle: String
          _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          val _tmpMessage: String
          _tmpMessage = _stmt.getText(_columnIndexOfMessage)
          val _tmpSeverity: String
          _tmpSeverity = _stmt.getText(_columnIndexOfSeverity)
          val _tmpType: String
          _tmpType = _stmt.getText(_columnIndexOfType)
          val _tmpRelatedId: String?
          if (_stmt.isNull(_columnIndexOfRelatedId)) {
            _tmpRelatedId = null
          } else {
            _tmpRelatedId = _stmt.getText(_columnIndexOfRelatedId)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpIsRead: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsRead).toInt()
          _tmpIsRead = _tmp != 0
          val _tmpIsDismissed: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfIsDismissed).toInt()
          _tmpIsDismissed = _tmp_1 != 0
          _item =
              AlertEntity(_tmpId,_tmpUserId,_tmpTitle,_tmpMessage,_tmpSeverity,_tmpType,_tmpRelatedId,_tmpCreatedAt,_tmpIsRead,_tmpIsDismissed)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun countUnread(userId: String): Flow<Int> {
    val _sql: String =
        "SELECT COUNT(*) FROM alerts WHERE userId = ? AND isRead = 0 AND isDismissed = 0"
    return createFlow(__db, false, arrayOf("alerts")) { _connection ->
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

  public override suspend fun markAsRead(alertId: String) {
    val _sql: String = "UPDATE alerts SET isRead = 1 WHERE id = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, alertId)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun markAllAsRead(userId: String) {
    val _sql: String = "UPDATE alerts SET isRead = 1 WHERE userId = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, userId)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun dismiss(alertId: String) {
    val _sql: String = "UPDATE alerts SET isDismissed = 1 WHERE id = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, alertId)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun deleteOlderThan(timestamp: Long) {
    val _sql: String = "DELETE FROM alerts WHERE createdAt < ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, timestamp)
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
