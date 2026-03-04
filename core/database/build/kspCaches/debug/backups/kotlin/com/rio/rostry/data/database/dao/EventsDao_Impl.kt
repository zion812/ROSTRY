package com.rio.rostry.`data`.database.dao

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.rio.rostry.`data`.database.entity.EventEntity
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
public class EventsDao_Impl(
  __db: RoomDatabase,
) : EventsDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfEventEntity: EntityInsertAdapter<EventEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfEventEntity = object : EntityInsertAdapter<EventEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `events` (`eventId`,`groupId`,`title`,`description`,`location`,`startTime`,`endTime`) VALUES (?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: EventEntity) {
        statement.bindText(1, entity.eventId)
        val _tmpGroupId: String? = entity.groupId
        if (_tmpGroupId == null) {
          statement.bindNull(2)
        } else {
          statement.bindText(2, _tmpGroupId)
        }
        statement.bindText(3, entity.title)
        val _tmpDescription: String? = entity.description
        if (_tmpDescription == null) {
          statement.bindNull(4)
        } else {
          statement.bindText(4, _tmpDescription)
        }
        val _tmpLocation: String? = entity.location
        if (_tmpLocation == null) {
          statement.bindNull(5)
        } else {
          statement.bindText(5, _tmpLocation)
        }
        statement.bindLong(6, entity.startTime)
        val _tmpEndTime: Long? = entity.endTime
        if (_tmpEndTime == null) {
          statement.bindNull(7)
        } else {
          statement.bindLong(7, _tmpEndTime)
        }
      }
    }
  }

  public override suspend fun upsert(event: EventEntity): Unit = performSuspending(__db, false,
      true) { _connection ->
    __insertAdapterOfEventEntity.insert(_connection, event)
  }

  public override fun streamUpcoming(now: Long): Flow<List<EventEntity>> {
    val _sql: String = "SELECT * FROM events WHERE startTime >= ? ORDER BY startTime ASC"
    return createFlow(__db, false, arrayOf("events")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, now)
        val _columnIndexOfEventId: Int = getColumnIndexOrThrow(_stmt, "eventId")
        val _columnIndexOfGroupId: Int = getColumnIndexOrThrow(_stmt, "groupId")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfLocation: Int = getColumnIndexOrThrow(_stmt, "location")
        val _columnIndexOfStartTime: Int = getColumnIndexOrThrow(_stmt, "startTime")
        val _columnIndexOfEndTime: Int = getColumnIndexOrThrow(_stmt, "endTime")
        val _result: MutableList<EventEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: EventEntity
          val _tmpEventId: String
          _tmpEventId = _stmt.getText(_columnIndexOfEventId)
          val _tmpGroupId: String?
          if (_stmt.isNull(_columnIndexOfGroupId)) {
            _tmpGroupId = null
          } else {
            _tmpGroupId = _stmt.getText(_columnIndexOfGroupId)
          }
          val _tmpTitle: String
          _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          val _tmpDescription: String?
          if (_stmt.isNull(_columnIndexOfDescription)) {
            _tmpDescription = null
          } else {
            _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          }
          val _tmpLocation: String?
          if (_stmt.isNull(_columnIndexOfLocation)) {
            _tmpLocation = null
          } else {
            _tmpLocation = _stmt.getText(_columnIndexOfLocation)
          }
          val _tmpStartTime: Long
          _tmpStartTime = _stmt.getLong(_columnIndexOfStartTime)
          val _tmpEndTime: Long?
          if (_stmt.isNull(_columnIndexOfEndTime)) {
            _tmpEndTime = null
          } else {
            _tmpEndTime = _stmt.getLong(_columnIndexOfEndTime)
          }
          _item =
              EventEntity(_tmpEventId,_tmpGroupId,_tmpTitle,_tmpDescription,_tmpLocation,_tmpStartTime,_tmpEndTime)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getById(eventId: String): EventEntity? {
    val _sql: String = "SELECT * FROM events WHERE eventId = ? LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, eventId)
        val _columnIndexOfEventId: Int = getColumnIndexOrThrow(_stmt, "eventId")
        val _columnIndexOfGroupId: Int = getColumnIndexOrThrow(_stmt, "groupId")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfLocation: Int = getColumnIndexOrThrow(_stmt, "location")
        val _columnIndexOfStartTime: Int = getColumnIndexOrThrow(_stmt, "startTime")
        val _columnIndexOfEndTime: Int = getColumnIndexOrThrow(_stmt, "endTime")
        val _result: EventEntity?
        if (_stmt.step()) {
          val _tmpEventId: String
          _tmpEventId = _stmt.getText(_columnIndexOfEventId)
          val _tmpGroupId: String?
          if (_stmt.isNull(_columnIndexOfGroupId)) {
            _tmpGroupId = null
          } else {
            _tmpGroupId = _stmt.getText(_columnIndexOfGroupId)
          }
          val _tmpTitle: String
          _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          val _tmpDescription: String?
          if (_stmt.isNull(_columnIndexOfDescription)) {
            _tmpDescription = null
          } else {
            _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          }
          val _tmpLocation: String?
          if (_stmt.isNull(_columnIndexOfLocation)) {
            _tmpLocation = null
          } else {
            _tmpLocation = _stmt.getText(_columnIndexOfLocation)
          }
          val _tmpStartTime: Long
          _tmpStartTime = _stmt.getLong(_columnIndexOfStartTime)
          val _tmpEndTime: Long?
          if (_stmt.isNull(_columnIndexOfEndTime)) {
            _tmpEndTime = null
          } else {
            _tmpEndTime = _stmt.getLong(_columnIndexOfEndTime)
          }
          _result =
              EventEntity(_tmpEventId,_tmpGroupId,_tmpTitle,_tmpDescription,_tmpLocation,_tmpStartTime,_tmpEndTime)
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
