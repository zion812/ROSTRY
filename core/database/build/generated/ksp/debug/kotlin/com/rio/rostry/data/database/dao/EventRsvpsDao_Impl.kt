package com.rio.rostry.`data`.database.dao

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.rio.rostry.`data`.database.entity.EventRsvpEntity
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
public class EventRsvpsDao_Impl(
  __db: RoomDatabase,
) : EventRsvpsDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfEventRsvpEntity: EntityInsertAdapter<EventRsvpEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfEventRsvpEntity = object : EntityInsertAdapter<EventRsvpEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `event_rsvps` (`id`,`eventId`,`userId`,`status`,`updatedAt`) VALUES (?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: EventRsvpEntity) {
        statement.bindText(1, entity.id)
        statement.bindText(2, entity.eventId)
        statement.bindText(3, entity.userId)
        statement.bindText(4, entity.status)
        statement.bindLong(5, entity.updatedAt)
      }
    }
  }

  public override suspend fun upsert(rsvp: EventRsvpEntity): Unit = performSuspending(__db, false,
      true) { _connection ->
    __insertAdapterOfEventRsvpEntity.insert(_connection, rsvp)
  }

  public override fun streamForEvent(eventId: String): Flow<List<EventRsvpEntity>> {
    val _sql: String = "SELECT * FROM event_rsvps WHERE eventId = ?"
    return createFlow(__db, false, arrayOf("event_rsvps")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, eventId)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfEventId: Int = getColumnIndexOrThrow(_stmt, "eventId")
        val _columnIndexOfUserId: Int = getColumnIndexOrThrow(_stmt, "userId")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _result: MutableList<EventRsvpEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: EventRsvpEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpEventId: String
          _tmpEventId = _stmt.getText(_columnIndexOfEventId)
          val _tmpUserId: String
          _tmpUserId = _stmt.getText(_columnIndexOfUserId)
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          _item = EventRsvpEntity(_tmpId,_tmpEventId,_tmpUserId,_tmpStatus,_tmpUpdatedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun streamForUser(userId: String): Flow<List<EventRsvpEntity>> {
    val _sql: String = "SELECT * FROM event_rsvps WHERE userId = ?"
    return createFlow(__db, false, arrayOf("event_rsvps")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, userId)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfEventId: Int = getColumnIndexOrThrow(_stmt, "eventId")
        val _columnIndexOfUserId: Int = getColumnIndexOrThrow(_stmt, "userId")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _result: MutableList<EventRsvpEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: EventRsvpEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpEventId: String
          _tmpEventId = _stmt.getText(_columnIndexOfEventId)
          val _tmpUserId: String
          _tmpUserId = _stmt.getText(_columnIndexOfUserId)
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          _item = EventRsvpEntity(_tmpId,_tmpEventId,_tmpUserId,_tmpStatus,_tmpUpdatedAt)
          _result.add(_item)
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
