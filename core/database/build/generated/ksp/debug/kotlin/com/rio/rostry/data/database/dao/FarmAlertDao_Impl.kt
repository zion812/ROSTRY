package com.rio.rostry.`data`.database.dao

import androidx.room.EntityDeleteOrUpdateAdapter
import androidx.room.EntityInsertAdapter
import androidx.room.EntityUpsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.appendPlaceholders
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.rio.rostry.`data`.database.entity.FarmAlertEntity
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
import kotlin.text.StringBuilder
import kotlinx.coroutines.flow.Flow

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class FarmAlertDao_Impl(
  __db: RoomDatabase,
) : FarmAlertDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfFarmAlertEntity: EntityInsertAdapter<FarmAlertEntity>

  private val __upsertAdapterOfFarmAlertEntity: EntityUpsertAdapter<FarmAlertEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfFarmAlertEntity = object : EntityInsertAdapter<FarmAlertEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `farm_alerts` (`alertId`,`farmerId`,`alertType`,`severity`,`message`,`actionRoute`,`isRead`,`createdAt`,`expiresAt`,`dirty`,`syncedAt`) VALUES (?,?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: FarmAlertEntity) {
        statement.bindText(1, entity.alertId)
        statement.bindText(2, entity.farmerId)
        statement.bindText(3, entity.alertType)
        statement.bindText(4, entity.severity)
        statement.bindText(5, entity.message)
        val _tmpActionRoute: String? = entity.actionRoute
        if (_tmpActionRoute == null) {
          statement.bindNull(6)
        } else {
          statement.bindText(6, _tmpActionRoute)
        }
        val _tmp: Int = if (entity.isRead) 1 else 0
        statement.bindLong(7, _tmp.toLong())
        statement.bindLong(8, entity.createdAt)
        val _tmpExpiresAt: Long? = entity.expiresAt
        if (_tmpExpiresAt == null) {
          statement.bindNull(9)
        } else {
          statement.bindLong(9, _tmpExpiresAt)
        }
        val _tmp_1: Int = if (entity.dirty) 1 else 0
        statement.bindLong(10, _tmp_1.toLong())
        val _tmpSyncedAt: Long? = entity.syncedAt
        if (_tmpSyncedAt == null) {
          statement.bindNull(11)
        } else {
          statement.bindLong(11, _tmpSyncedAt)
        }
      }
    }
    this.__upsertAdapterOfFarmAlertEntity = EntityUpsertAdapter<FarmAlertEntity>(object :
        EntityInsertAdapter<FarmAlertEntity>() {
      protected override fun createQuery(): String =
          "INSERT INTO `farm_alerts` (`alertId`,`farmerId`,`alertType`,`severity`,`message`,`actionRoute`,`isRead`,`createdAt`,`expiresAt`,`dirty`,`syncedAt`) VALUES (?,?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: FarmAlertEntity) {
        statement.bindText(1, entity.alertId)
        statement.bindText(2, entity.farmerId)
        statement.bindText(3, entity.alertType)
        statement.bindText(4, entity.severity)
        statement.bindText(5, entity.message)
        val _tmpActionRoute: String? = entity.actionRoute
        if (_tmpActionRoute == null) {
          statement.bindNull(6)
        } else {
          statement.bindText(6, _tmpActionRoute)
        }
        val _tmp: Int = if (entity.isRead) 1 else 0
        statement.bindLong(7, _tmp.toLong())
        statement.bindLong(8, entity.createdAt)
        val _tmpExpiresAt: Long? = entity.expiresAt
        if (_tmpExpiresAt == null) {
          statement.bindNull(9)
        } else {
          statement.bindLong(9, _tmpExpiresAt)
        }
        val _tmp_1: Int = if (entity.dirty) 1 else 0
        statement.bindLong(10, _tmp_1.toLong())
        val _tmpSyncedAt: Long? = entity.syncedAt
        if (_tmpSyncedAt == null) {
          statement.bindNull(11)
        } else {
          statement.bindLong(11, _tmpSyncedAt)
        }
      }
    }, object : EntityDeleteOrUpdateAdapter<FarmAlertEntity>() {
      protected override fun createQuery(): String =
          "UPDATE `farm_alerts` SET `alertId` = ?,`farmerId` = ?,`alertType` = ?,`severity` = ?,`message` = ?,`actionRoute` = ?,`isRead` = ?,`createdAt` = ?,`expiresAt` = ?,`dirty` = ?,`syncedAt` = ? WHERE `alertId` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: FarmAlertEntity) {
        statement.bindText(1, entity.alertId)
        statement.bindText(2, entity.farmerId)
        statement.bindText(3, entity.alertType)
        statement.bindText(4, entity.severity)
        statement.bindText(5, entity.message)
        val _tmpActionRoute: String? = entity.actionRoute
        if (_tmpActionRoute == null) {
          statement.bindNull(6)
        } else {
          statement.bindText(6, _tmpActionRoute)
        }
        val _tmp: Int = if (entity.isRead) 1 else 0
        statement.bindLong(7, _tmp.toLong())
        statement.bindLong(8, entity.createdAt)
        val _tmpExpiresAt: Long? = entity.expiresAt
        if (_tmpExpiresAt == null) {
          statement.bindNull(9)
        } else {
          statement.bindLong(9, _tmpExpiresAt)
        }
        val _tmp_1: Int = if (entity.dirty) 1 else 0
        statement.bindLong(10, _tmp_1.toLong())
        val _tmpSyncedAt: Long? = entity.syncedAt
        if (_tmpSyncedAt == null) {
          statement.bindNull(11)
        } else {
          statement.bindLong(11, _tmpSyncedAt)
        }
        statement.bindText(12, entity.alertId)
      }
    })
  }

  public override suspend fun insert(alert: FarmAlertEntity): Unit = performSuspending(__db, false,
      true) { _connection ->
    __insertAdapterOfFarmAlertEntity.insert(_connection, alert)
  }

  public override suspend fun upsert(alert: FarmAlertEntity): Unit = performSuspending(__db, false,
      true) { _connection ->
    __upsertAdapterOfFarmAlertEntity.upsert(_connection, alert)
  }

  public override fun observeUnread(farmerId: String): Flow<List<FarmAlertEntity>> {
    val _sql: String =
        "SELECT * FROM farm_alerts WHERE farmerId = ? AND isRead = 0 ORDER BY createdAt DESC"
    return createFlow(__db, false, arrayOf("farm_alerts")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, farmerId)
        val _columnIndexOfAlertId: Int = getColumnIndexOrThrow(_stmt, "alertId")
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfAlertType: Int = getColumnIndexOrThrow(_stmt, "alertType")
        val _columnIndexOfSeverity: Int = getColumnIndexOrThrow(_stmt, "severity")
        val _columnIndexOfMessage: Int = getColumnIndexOrThrow(_stmt, "message")
        val _columnIndexOfActionRoute: Int = getColumnIndexOrThrow(_stmt, "actionRoute")
        val _columnIndexOfIsRead: Int = getColumnIndexOrThrow(_stmt, "isRead")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfExpiresAt: Int = getColumnIndexOrThrow(_stmt, "expiresAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _result: MutableList<FarmAlertEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: FarmAlertEntity
          val _tmpAlertId: String
          _tmpAlertId = _stmt.getText(_columnIndexOfAlertId)
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpAlertType: String
          _tmpAlertType = _stmt.getText(_columnIndexOfAlertType)
          val _tmpSeverity: String
          _tmpSeverity = _stmt.getText(_columnIndexOfSeverity)
          val _tmpMessage: String
          _tmpMessage = _stmt.getText(_columnIndexOfMessage)
          val _tmpActionRoute: String?
          if (_stmt.isNull(_columnIndexOfActionRoute)) {
            _tmpActionRoute = null
          } else {
            _tmpActionRoute = _stmt.getText(_columnIndexOfActionRoute)
          }
          val _tmpIsRead: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsRead).toInt()
          _tmpIsRead = _tmp != 0
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpExpiresAt: Long?
          if (_stmt.isNull(_columnIndexOfExpiresAt)) {
            _tmpExpiresAt = null
          } else {
            _tmpExpiresAt = _stmt.getLong(_columnIndexOfExpiresAt)
          }
          val _tmpDirty: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp_1 != 0
          val _tmpSyncedAt: Long?
          if (_stmt.isNull(_columnIndexOfSyncedAt)) {
            _tmpSyncedAt = null
          } else {
            _tmpSyncedAt = _stmt.getLong(_columnIndexOfSyncedAt)
          }
          _item =
              FarmAlertEntity(_tmpAlertId,_tmpFarmerId,_tmpAlertType,_tmpSeverity,_tmpMessage,_tmpActionRoute,_tmpIsRead,_tmpCreatedAt,_tmpExpiresAt,_tmpDirty,_tmpSyncedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun countUnread(farmerId: String): Int {
    val _sql: String = "SELECT COUNT(*) FROM farm_alerts WHERE farmerId = ? AND isRead = 0"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, farmerId)
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

  public override suspend fun getByFarmer(farmerId: String, limit: Int): List<FarmAlertEntity> {
    val _sql: String =
        "SELECT * FROM farm_alerts WHERE farmerId = ? ORDER BY createdAt DESC LIMIT ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, farmerId)
        _argIndex = 2
        _stmt.bindLong(_argIndex, limit.toLong())
        val _columnIndexOfAlertId: Int = getColumnIndexOrThrow(_stmt, "alertId")
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfAlertType: Int = getColumnIndexOrThrow(_stmt, "alertType")
        val _columnIndexOfSeverity: Int = getColumnIndexOrThrow(_stmt, "severity")
        val _columnIndexOfMessage: Int = getColumnIndexOrThrow(_stmt, "message")
        val _columnIndexOfActionRoute: Int = getColumnIndexOrThrow(_stmt, "actionRoute")
        val _columnIndexOfIsRead: Int = getColumnIndexOrThrow(_stmt, "isRead")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfExpiresAt: Int = getColumnIndexOrThrow(_stmt, "expiresAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _result: MutableList<FarmAlertEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: FarmAlertEntity
          val _tmpAlertId: String
          _tmpAlertId = _stmt.getText(_columnIndexOfAlertId)
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpAlertType: String
          _tmpAlertType = _stmt.getText(_columnIndexOfAlertType)
          val _tmpSeverity: String
          _tmpSeverity = _stmt.getText(_columnIndexOfSeverity)
          val _tmpMessage: String
          _tmpMessage = _stmt.getText(_columnIndexOfMessage)
          val _tmpActionRoute: String?
          if (_stmt.isNull(_columnIndexOfActionRoute)) {
            _tmpActionRoute = null
          } else {
            _tmpActionRoute = _stmt.getText(_columnIndexOfActionRoute)
          }
          val _tmpIsRead: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsRead).toInt()
          _tmpIsRead = _tmp != 0
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpExpiresAt: Long?
          if (_stmt.isNull(_columnIndexOfExpiresAt)) {
            _tmpExpiresAt = null
          } else {
            _tmpExpiresAt = _stmt.getLong(_columnIndexOfExpiresAt)
          }
          val _tmpDirty: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp_1 != 0
          val _tmpSyncedAt: Long?
          if (_stmt.isNull(_columnIndexOfSyncedAt)) {
            _tmpSyncedAt = null
          } else {
            _tmpSyncedAt = _stmt.getLong(_columnIndexOfSyncedAt)
          }
          _item =
              FarmAlertEntity(_tmpAlertId,_tmpFarmerId,_tmpAlertType,_tmpSeverity,_tmpMessage,_tmpActionRoute,_tmpIsRead,_tmpCreatedAt,_tmpExpiresAt,_tmpDirty,_tmpSyncedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getDirty(): List<FarmAlertEntity> {
    val _sql: String = "SELECT * FROM farm_alerts WHERE dirty = 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfAlertId: Int = getColumnIndexOrThrow(_stmt, "alertId")
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfAlertType: Int = getColumnIndexOrThrow(_stmt, "alertType")
        val _columnIndexOfSeverity: Int = getColumnIndexOrThrow(_stmt, "severity")
        val _columnIndexOfMessage: Int = getColumnIndexOrThrow(_stmt, "message")
        val _columnIndexOfActionRoute: Int = getColumnIndexOrThrow(_stmt, "actionRoute")
        val _columnIndexOfIsRead: Int = getColumnIndexOrThrow(_stmt, "isRead")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfExpiresAt: Int = getColumnIndexOrThrow(_stmt, "expiresAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _result: MutableList<FarmAlertEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: FarmAlertEntity
          val _tmpAlertId: String
          _tmpAlertId = _stmt.getText(_columnIndexOfAlertId)
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpAlertType: String
          _tmpAlertType = _stmt.getText(_columnIndexOfAlertType)
          val _tmpSeverity: String
          _tmpSeverity = _stmt.getText(_columnIndexOfSeverity)
          val _tmpMessage: String
          _tmpMessage = _stmt.getText(_columnIndexOfMessage)
          val _tmpActionRoute: String?
          if (_stmt.isNull(_columnIndexOfActionRoute)) {
            _tmpActionRoute = null
          } else {
            _tmpActionRoute = _stmt.getText(_columnIndexOfActionRoute)
          }
          val _tmpIsRead: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsRead).toInt()
          _tmpIsRead = _tmp != 0
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpExpiresAt: Long?
          if (_stmt.isNull(_columnIndexOfExpiresAt)) {
            _tmpExpiresAt = null
          } else {
            _tmpExpiresAt = _stmt.getLong(_columnIndexOfExpiresAt)
          }
          val _tmpDirty: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp_1 != 0
          val _tmpSyncedAt: Long?
          if (_stmt.isNull(_columnIndexOfSyncedAt)) {
            _tmpSyncedAt = null
          } else {
            _tmpSyncedAt = _stmt.getLong(_columnIndexOfSyncedAt)
          }
          _item =
              FarmAlertEntity(_tmpAlertId,_tmpFarmerId,_tmpAlertType,_tmpSeverity,_tmpMessage,_tmpActionRoute,_tmpIsRead,_tmpCreatedAt,_tmpExpiresAt,_tmpDirty,_tmpSyncedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getAlertCountForFarmer(farmerId: String): Int {
    val _sql: String = "SELECT COUNT(*) FROM farm_alerts WHERE farmerId = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, farmerId)
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

  public override suspend fun getByTypeForProduct(productId: String, alertType: String):
      List<FarmAlertEntity> {
    val _sql: String =
        "SELECT * FROM farm_alerts WHERE actionRoute LIKE '%' || ? || '%' AND alertType = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, productId)
        _argIndex = 2
        _stmt.bindText(_argIndex, alertType)
        val _columnIndexOfAlertId: Int = getColumnIndexOrThrow(_stmt, "alertId")
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfAlertType: Int = getColumnIndexOrThrow(_stmt, "alertType")
        val _columnIndexOfSeverity: Int = getColumnIndexOrThrow(_stmt, "severity")
        val _columnIndexOfMessage: Int = getColumnIndexOrThrow(_stmt, "message")
        val _columnIndexOfActionRoute: Int = getColumnIndexOrThrow(_stmt, "actionRoute")
        val _columnIndexOfIsRead: Int = getColumnIndexOrThrow(_stmt, "isRead")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfExpiresAt: Int = getColumnIndexOrThrow(_stmt, "expiresAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _result: MutableList<FarmAlertEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: FarmAlertEntity
          val _tmpAlertId: String
          _tmpAlertId = _stmt.getText(_columnIndexOfAlertId)
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpAlertType: String
          _tmpAlertType = _stmt.getText(_columnIndexOfAlertType)
          val _tmpSeverity: String
          _tmpSeverity = _stmt.getText(_columnIndexOfSeverity)
          val _tmpMessage: String
          _tmpMessage = _stmt.getText(_columnIndexOfMessage)
          val _tmpActionRoute: String?
          if (_stmt.isNull(_columnIndexOfActionRoute)) {
            _tmpActionRoute = null
          } else {
            _tmpActionRoute = _stmt.getText(_columnIndexOfActionRoute)
          }
          val _tmpIsRead: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsRead).toInt()
          _tmpIsRead = _tmp != 0
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpExpiresAt: Long?
          if (_stmt.isNull(_columnIndexOfExpiresAt)) {
            _tmpExpiresAt = null
          } else {
            _tmpExpiresAt = _stmt.getLong(_columnIndexOfExpiresAt)
          }
          val _tmpDirty: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp_1 != 0
          val _tmpSyncedAt: Long?
          if (_stmt.isNull(_columnIndexOfSyncedAt)) {
            _tmpSyncedAt = null
          } else {
            _tmpSyncedAt = _stmt.getLong(_columnIndexOfSyncedAt)
          }
          _item =
              FarmAlertEntity(_tmpAlertId,_tmpFarmerId,_tmpAlertType,_tmpSeverity,_tmpMessage,_tmpActionRoute,_tmpIsRead,_tmpCreatedAt,_tmpExpiresAt,_tmpDirty,_tmpSyncedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun markRead(alertId: String) {
    val _sql: String = "UPDATE farm_alerts SET isRead = 1 WHERE alertId = ?"
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

  public override suspend fun deleteExpired(now: Long) {
    val _sql: String = "DELETE FROM farm_alerts WHERE expiresAt IS NOT NULL AND expiresAt < ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, now)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun clearDirty(alertIds: List<String>, syncedAt: Long) {
    val _stringBuilder: StringBuilder = StringBuilder()
    _stringBuilder.append("UPDATE farm_alerts SET dirty = 0, syncedAt = ")
    _stringBuilder.append("?")
    _stringBuilder.append(" WHERE alertId IN (")
    val _inputSize: Int = alertIds.size
    appendPlaceholders(_stringBuilder, _inputSize)
    _stringBuilder.append(")")
    val _sql: String = _stringBuilder.toString()
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, syncedAt)
        _argIndex = 2
        for (_item: String in alertIds) {
          _stmt.bindText(_argIndex, _item)
          _argIndex++
        }
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun deleteReadAlerts(farmerId: String) {
    val _sql: String = "DELETE FROM farm_alerts WHERE farmerId = ? AND isRead = 1"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, farmerId)
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
