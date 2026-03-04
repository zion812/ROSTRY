package com.rio.rostry.`data`.database.dao

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.rio.rostry.`data`.database.entity.ExpertBookingEntity
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
public class ExpertBookingsDao_Impl(
  __db: RoomDatabase,
) : ExpertBookingsDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfExpertBookingEntity: EntityInsertAdapter<ExpertBookingEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfExpertBookingEntity = object : EntityInsertAdapter<ExpertBookingEntity>()
        {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `expert_bookings` (`bookingId`,`expertId`,`userId`,`topic`,`startTime`,`endTime`,`status`) VALUES (?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: ExpertBookingEntity) {
        statement.bindText(1, entity.bookingId)
        statement.bindText(2, entity.expertId)
        statement.bindText(3, entity.userId)
        val _tmpTopic: String? = entity.topic
        if (_tmpTopic == null) {
          statement.bindNull(4)
        } else {
          statement.bindText(4, _tmpTopic)
        }
        statement.bindLong(5, entity.startTime)
        statement.bindLong(6, entity.endTime)
        statement.bindText(7, entity.status)
      }
    }
  }

  public override suspend fun upsert(booking: ExpertBookingEntity): Unit = performSuspending(__db,
      false, true) { _connection ->
    __insertAdapterOfExpertBookingEntity.insert(_connection, booking)
  }

  public override fun streamUserBookings(userId: String): Flow<List<ExpertBookingEntity>> {
    val _sql: String = "SELECT * FROM expert_bookings WHERE userId = ? ORDER BY startTime DESC"
    return createFlow(__db, false, arrayOf("expert_bookings")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, userId)
        val _columnIndexOfBookingId: Int = getColumnIndexOrThrow(_stmt, "bookingId")
        val _columnIndexOfExpertId: Int = getColumnIndexOrThrow(_stmt, "expertId")
        val _columnIndexOfUserId: Int = getColumnIndexOrThrow(_stmt, "userId")
        val _columnIndexOfTopic: Int = getColumnIndexOrThrow(_stmt, "topic")
        val _columnIndexOfStartTime: Int = getColumnIndexOrThrow(_stmt, "startTime")
        val _columnIndexOfEndTime: Int = getColumnIndexOrThrow(_stmt, "endTime")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _result: MutableList<ExpertBookingEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: ExpertBookingEntity
          val _tmpBookingId: String
          _tmpBookingId = _stmt.getText(_columnIndexOfBookingId)
          val _tmpExpertId: String
          _tmpExpertId = _stmt.getText(_columnIndexOfExpertId)
          val _tmpUserId: String
          _tmpUserId = _stmt.getText(_columnIndexOfUserId)
          val _tmpTopic: String?
          if (_stmt.isNull(_columnIndexOfTopic)) {
            _tmpTopic = null
          } else {
            _tmpTopic = _stmt.getText(_columnIndexOfTopic)
          }
          val _tmpStartTime: Long
          _tmpStartTime = _stmt.getLong(_columnIndexOfStartTime)
          val _tmpEndTime: Long
          _tmpEndTime = _stmt.getLong(_columnIndexOfEndTime)
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          _item =
              ExpertBookingEntity(_tmpBookingId,_tmpExpertId,_tmpUserId,_tmpTopic,_tmpStartTime,_tmpEndTime,_tmpStatus)
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
