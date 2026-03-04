package com.rio.rostry.`data`.database.dao

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.rio.rostry.`data`.database.entity.LifecycleEventEntity
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
public class LifecycleEventDao_Impl(
  __db: RoomDatabase,
) : LifecycleEventDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfLifecycleEventEntity: EntityInsertAdapter<LifecycleEventEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfLifecycleEventEntity = object :
        EntityInsertAdapter<LifecycleEventEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `lifecycle_events` (`eventId`,`productId`,`week`,`stage`,`type`,`notes`,`timestamp`) VALUES (?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: LifecycleEventEntity) {
        statement.bindText(1, entity.eventId)
        statement.bindText(2, entity.productId)
        statement.bindLong(3, entity.week.toLong())
        statement.bindText(4, entity.stage)
        statement.bindText(5, entity.type)
        val _tmpNotes: String? = entity.notes
        if (_tmpNotes == null) {
          statement.bindNull(6)
        } else {
          statement.bindText(6, _tmpNotes)
        }
        statement.bindLong(7, entity.timestamp)
      }
    }
  }

  public override suspend fun insert(event: LifecycleEventEntity): Unit = performSuspending(__db,
      false, true) { _connection ->
    __insertAdapterOfLifecycleEventEntity.insert(_connection, event)
  }

  public override fun observeForProduct(productId: String): Flow<List<LifecycleEventEntity>> {
    val _sql: String =
        "SELECT * FROM lifecycle_events WHERE productId = ? ORDER BY week ASC, timestamp ASC"
    return createFlow(__db, false, arrayOf("lifecycle_events")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, productId)
        val _columnIndexOfEventId: Int = getColumnIndexOrThrow(_stmt, "eventId")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfWeek: Int = getColumnIndexOrThrow(_stmt, "week")
        val _columnIndexOfStage: Int = getColumnIndexOrThrow(_stmt, "stage")
        val _columnIndexOfType: Int = getColumnIndexOrThrow(_stmt, "type")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfTimestamp: Int = getColumnIndexOrThrow(_stmt, "timestamp")
        val _result: MutableList<LifecycleEventEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: LifecycleEventEntity
          val _tmpEventId: String
          _tmpEventId = _stmt.getText(_columnIndexOfEventId)
          val _tmpProductId: String
          _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          val _tmpWeek: Int
          _tmpWeek = _stmt.getLong(_columnIndexOfWeek).toInt()
          val _tmpStage: String
          _tmpStage = _stmt.getText(_columnIndexOfStage)
          val _tmpType: String
          _tmpType = _stmt.getText(_columnIndexOfType)
          val _tmpNotes: String?
          if (_stmt.isNull(_columnIndexOfNotes)) {
            _tmpNotes = null
          } else {
            _tmpNotes = _stmt.getText(_columnIndexOfNotes)
          }
          val _tmpTimestamp: Long
          _tmpTimestamp = _stmt.getLong(_columnIndexOfTimestamp)
          _item =
              LifecycleEventEntity(_tmpEventId,_tmpProductId,_tmpWeek,_tmpStage,_tmpType,_tmpNotes,_tmpTimestamp)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun range(
    productId: String,
    startWeek: Int,
    endWeek: Int,
  ): List<LifecycleEventEntity> {
    val _sql: String =
        "SELECT * FROM lifecycle_events WHERE productId = ? AND week BETWEEN ? AND ? ORDER BY week ASC"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, productId)
        _argIndex = 2
        _stmt.bindLong(_argIndex, startWeek.toLong())
        _argIndex = 3
        _stmt.bindLong(_argIndex, endWeek.toLong())
        val _columnIndexOfEventId: Int = getColumnIndexOrThrow(_stmt, "eventId")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfWeek: Int = getColumnIndexOrThrow(_stmt, "week")
        val _columnIndexOfStage: Int = getColumnIndexOrThrow(_stmt, "stage")
        val _columnIndexOfType: Int = getColumnIndexOrThrow(_stmt, "type")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfTimestamp: Int = getColumnIndexOrThrow(_stmt, "timestamp")
        val _result: MutableList<LifecycleEventEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: LifecycleEventEntity
          val _tmpEventId: String
          _tmpEventId = _stmt.getText(_columnIndexOfEventId)
          val _tmpProductId: String
          _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          val _tmpWeek: Int
          _tmpWeek = _stmt.getLong(_columnIndexOfWeek).toInt()
          val _tmpStage: String
          _tmpStage = _stmt.getText(_columnIndexOfStage)
          val _tmpType: String
          _tmpType = _stmt.getText(_columnIndexOfType)
          val _tmpNotes: String?
          if (_stmt.isNull(_columnIndexOfNotes)) {
            _tmpNotes = null
          } else {
            _tmpNotes = _stmt.getText(_columnIndexOfNotes)
          }
          val _tmpTimestamp: Long
          _tmpTimestamp = _stmt.getLong(_columnIndexOfTimestamp)
          _item =
              LifecycleEventEntity(_tmpEventId,_tmpProductId,_tmpWeek,_tmpStage,_tmpType,_tmpNotes,_tmpTimestamp)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun existsEvent(
    productId: String,
    type: String,
    week: Int,
  ): Boolean {
    val _sql: String =
        "SELECT EXISTS(SELECT 1 FROM lifecycle_events WHERE productId = ? AND type = ? AND week = ?)"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, productId)
        _argIndex = 2
        _stmt.bindText(_argIndex, type)
        _argIndex = 3
        _stmt.bindLong(_argIndex, week.toLong())
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

  public companion object {
    public fun getRequiredConverters(): List<KClass<*>> = emptyList()
  }
}
