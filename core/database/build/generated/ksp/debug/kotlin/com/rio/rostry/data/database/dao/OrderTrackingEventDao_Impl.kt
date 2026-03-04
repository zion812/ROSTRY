package com.rio.rostry.`data`.database.dao

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.rio.rostry.`data`.database.entity.OrderTrackingEventEntity
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
public class OrderTrackingEventDao_Impl(
  __db: RoomDatabase,
) : OrderTrackingEventDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfOrderTrackingEventEntity:
      EntityInsertAdapter<OrderTrackingEventEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfOrderTrackingEventEntity = object :
        EntityInsertAdapter<OrderTrackingEventEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `order_tracking_events` (`eventId`,`orderId`,`status`,`hubId`,`note`,`timestamp`) VALUES (?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: OrderTrackingEventEntity) {
        statement.bindText(1, entity.eventId)
        statement.bindText(2, entity.orderId)
        statement.bindText(3, entity.status)
        val _tmpHubId: String? = entity.hubId
        if (_tmpHubId == null) {
          statement.bindNull(4)
        } else {
          statement.bindText(4, _tmpHubId)
        }
        val _tmpNote: String? = entity.note
        if (_tmpNote == null) {
          statement.bindNull(5)
        } else {
          statement.bindText(5, _tmpNote)
        }
        statement.bindLong(6, entity.timestamp)
      }
    }
  }

  public override suspend fun insert(event: OrderTrackingEventEntity): Unit =
      performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfOrderTrackingEventEntity.insert(_connection, event)
  }

  public override fun observeByOrder(orderId: String): Flow<List<OrderTrackingEventEntity>> {
    val _sql: String =
        "SELECT * FROM order_tracking_events WHERE orderId = ? ORDER BY timestamp ASC"
    return createFlow(__db, false, arrayOf("order_tracking_events")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, orderId)
        val _columnIndexOfEventId: Int = getColumnIndexOrThrow(_stmt, "eventId")
        val _columnIndexOfOrderId: Int = getColumnIndexOrThrow(_stmt, "orderId")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfHubId: Int = getColumnIndexOrThrow(_stmt, "hubId")
        val _columnIndexOfNote: Int = getColumnIndexOrThrow(_stmt, "note")
        val _columnIndexOfTimestamp: Int = getColumnIndexOrThrow(_stmt, "timestamp")
        val _result: MutableList<OrderTrackingEventEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: OrderTrackingEventEntity
          val _tmpEventId: String
          _tmpEventId = _stmt.getText(_columnIndexOfEventId)
          val _tmpOrderId: String
          _tmpOrderId = _stmt.getText(_columnIndexOfOrderId)
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpHubId: String?
          if (_stmt.isNull(_columnIndexOfHubId)) {
            _tmpHubId = null
          } else {
            _tmpHubId = _stmt.getText(_columnIndexOfHubId)
          }
          val _tmpNote: String?
          if (_stmt.isNull(_columnIndexOfNote)) {
            _tmpNote = null
          } else {
            _tmpNote = _stmt.getText(_columnIndexOfNote)
          }
          val _tmpTimestamp: Long
          _tmpTimestamp = _stmt.getLong(_columnIndexOfTimestamp)
          _item =
              OrderTrackingEventEntity(_tmpEventId,_tmpOrderId,_tmpStatus,_tmpHubId,_tmpNote,_tmpTimestamp)
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
