package com.rio.rostry.`data`.database.dao

import androidx.room.EntityDeleteOrUpdateAdapter
import androidx.room.EntityInsertAdapter
import androidx.room.EntityUpsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.rio.rostry.`data`.database.entity.EventStatus
import com.rio.rostry.`data`.database.entity.FarmEventEntity
import com.rio.rostry.`data`.database.entity.FarmEventType
import com.rio.rostry.`data`.database.entity.RecurrenceType
import javax.`annotation`.processing.Generated
import kotlin.IllegalArgumentException
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
public class FarmEventDao_Impl(
  __db: RoomDatabase,
) : FarmEventDao {
  private val __db: RoomDatabase

  private val __upsertAdapterOfFarmEventEntity: EntityUpsertAdapter<FarmEventEntity>
  init {
    this.__db = __db
    this.__upsertAdapterOfFarmEventEntity = EntityUpsertAdapter<FarmEventEntity>(object :
        EntityInsertAdapter<FarmEventEntity>() {
      protected override fun createQuery(): String =
          "INSERT INTO `farm_events` (`eventId`,`farmerId`,`eventType`,`title`,`description`,`scheduledAt`,`completedAt`,`recurrence`,`productId`,`batchId`,`reminderBefore`,`status`,`metadata`,`createdAt`,`updatedAt`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: FarmEventEntity) {
        statement.bindText(1, entity.eventId)
        statement.bindText(2, entity.farmerId)
        statement.bindText(3, __FarmEventType_enumToString(entity.eventType))
        statement.bindText(4, entity.title)
        statement.bindText(5, entity.description)
        statement.bindLong(6, entity.scheduledAt)
        val _tmpCompletedAt: Long? = entity.completedAt
        if (_tmpCompletedAt == null) {
          statement.bindNull(7)
        } else {
          statement.bindLong(7, _tmpCompletedAt)
        }
        statement.bindText(8, __RecurrenceType_enumToString(entity.recurrence))
        val _tmpProductId: String? = entity.productId
        if (_tmpProductId == null) {
          statement.bindNull(9)
        } else {
          statement.bindText(9, _tmpProductId)
        }
        val _tmpBatchId: String? = entity.batchId
        if (_tmpBatchId == null) {
          statement.bindNull(10)
        } else {
          statement.bindText(10, _tmpBatchId)
        }
        statement.bindLong(11, entity.reminderBefore)
        statement.bindText(12, __EventStatus_enumToString(entity.status))
        val _tmpMetadata: String? = entity.metadata
        if (_tmpMetadata == null) {
          statement.bindNull(13)
        } else {
          statement.bindText(13, _tmpMetadata)
        }
        statement.bindLong(14, entity.createdAt)
        statement.bindLong(15, entity.updatedAt)
      }
    }, object : EntityDeleteOrUpdateAdapter<FarmEventEntity>() {
      protected override fun createQuery(): String =
          "UPDATE `farm_events` SET `eventId` = ?,`farmerId` = ?,`eventType` = ?,`title` = ?,`description` = ?,`scheduledAt` = ?,`completedAt` = ?,`recurrence` = ?,`productId` = ?,`batchId` = ?,`reminderBefore` = ?,`status` = ?,`metadata` = ?,`createdAt` = ?,`updatedAt` = ? WHERE `eventId` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: FarmEventEntity) {
        statement.bindText(1, entity.eventId)
        statement.bindText(2, entity.farmerId)
        statement.bindText(3, __FarmEventType_enumToString(entity.eventType))
        statement.bindText(4, entity.title)
        statement.bindText(5, entity.description)
        statement.bindLong(6, entity.scheduledAt)
        val _tmpCompletedAt: Long? = entity.completedAt
        if (_tmpCompletedAt == null) {
          statement.bindNull(7)
        } else {
          statement.bindLong(7, _tmpCompletedAt)
        }
        statement.bindText(8, __RecurrenceType_enumToString(entity.recurrence))
        val _tmpProductId: String? = entity.productId
        if (_tmpProductId == null) {
          statement.bindNull(9)
        } else {
          statement.bindText(9, _tmpProductId)
        }
        val _tmpBatchId: String? = entity.batchId
        if (_tmpBatchId == null) {
          statement.bindNull(10)
        } else {
          statement.bindText(10, _tmpBatchId)
        }
        statement.bindLong(11, entity.reminderBefore)
        statement.bindText(12, __EventStatus_enumToString(entity.status))
        val _tmpMetadata: String? = entity.metadata
        if (_tmpMetadata == null) {
          statement.bindNull(13)
        } else {
          statement.bindText(13, _tmpMetadata)
        }
        statement.bindLong(14, entity.createdAt)
        statement.bindLong(15, entity.updatedAt)
        statement.bindText(16, entity.eventId)
      }
    })
  }

  public override suspend fun upsert(event: FarmEventEntity): Unit = performSuspending(__db, false,
      true) { _connection ->
    __upsertAdapterOfFarmEventEntity.upsert(_connection, event)
  }

  public override fun getEventsByFarmer(farmerId: String): Flow<List<FarmEventEntity>> {
    val _sql: String = "SELECT * FROM farm_events WHERE farmerId = ? ORDER BY scheduledAt ASC"
    return createFlow(__db, false, arrayOf("farm_events")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, farmerId)
        val _columnIndexOfEventId: Int = getColumnIndexOrThrow(_stmt, "eventId")
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfEventType: Int = getColumnIndexOrThrow(_stmt, "eventType")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfScheduledAt: Int = getColumnIndexOrThrow(_stmt, "scheduledAt")
        val _columnIndexOfCompletedAt: Int = getColumnIndexOrThrow(_stmt, "completedAt")
        val _columnIndexOfRecurrence: Int = getColumnIndexOrThrow(_stmt, "recurrence")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfBatchId: Int = getColumnIndexOrThrow(_stmt, "batchId")
        val _columnIndexOfReminderBefore: Int = getColumnIndexOrThrow(_stmt, "reminderBefore")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfMetadata: Int = getColumnIndexOrThrow(_stmt, "metadata")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _result: MutableList<FarmEventEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: FarmEventEntity
          val _tmpEventId: String
          _tmpEventId = _stmt.getText(_columnIndexOfEventId)
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpEventType: FarmEventType
          _tmpEventType = __FarmEventType_stringToEnum(_stmt.getText(_columnIndexOfEventType))
          val _tmpTitle: String
          _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          val _tmpDescription: String
          _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          val _tmpScheduledAt: Long
          _tmpScheduledAt = _stmt.getLong(_columnIndexOfScheduledAt)
          val _tmpCompletedAt: Long?
          if (_stmt.isNull(_columnIndexOfCompletedAt)) {
            _tmpCompletedAt = null
          } else {
            _tmpCompletedAt = _stmt.getLong(_columnIndexOfCompletedAt)
          }
          val _tmpRecurrence: RecurrenceType
          _tmpRecurrence = __RecurrenceType_stringToEnum(_stmt.getText(_columnIndexOfRecurrence))
          val _tmpProductId: String?
          if (_stmt.isNull(_columnIndexOfProductId)) {
            _tmpProductId = null
          } else {
            _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          }
          val _tmpBatchId: String?
          if (_stmt.isNull(_columnIndexOfBatchId)) {
            _tmpBatchId = null
          } else {
            _tmpBatchId = _stmt.getText(_columnIndexOfBatchId)
          }
          val _tmpReminderBefore: Long
          _tmpReminderBefore = _stmt.getLong(_columnIndexOfReminderBefore)
          val _tmpStatus: EventStatus
          _tmpStatus = __EventStatus_stringToEnum(_stmt.getText(_columnIndexOfStatus))
          val _tmpMetadata: String?
          if (_stmt.isNull(_columnIndexOfMetadata)) {
            _tmpMetadata = null
          } else {
            _tmpMetadata = _stmt.getText(_columnIndexOfMetadata)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          _item =
              FarmEventEntity(_tmpEventId,_tmpFarmerId,_tmpEventType,_tmpTitle,_tmpDescription,_tmpScheduledAt,_tmpCompletedAt,_tmpRecurrence,_tmpProductId,_tmpBatchId,_tmpReminderBefore,_tmpStatus,_tmpMetadata,_tmpCreatedAt,_tmpUpdatedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getEventsByDateRange(
    farmerId: String,
    startDate: Long,
    endDate: Long,
  ): Flow<List<FarmEventEntity>> {
    val _sql: String =
        "SELECT * FROM farm_events WHERE farmerId = ? AND scheduledAt BETWEEN ? AND ? ORDER BY scheduledAt ASC"
    return createFlow(__db, false, arrayOf("farm_events")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, farmerId)
        _argIndex = 2
        _stmt.bindLong(_argIndex, startDate)
        _argIndex = 3
        _stmt.bindLong(_argIndex, endDate)
        val _columnIndexOfEventId: Int = getColumnIndexOrThrow(_stmt, "eventId")
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfEventType: Int = getColumnIndexOrThrow(_stmt, "eventType")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfScheduledAt: Int = getColumnIndexOrThrow(_stmt, "scheduledAt")
        val _columnIndexOfCompletedAt: Int = getColumnIndexOrThrow(_stmt, "completedAt")
        val _columnIndexOfRecurrence: Int = getColumnIndexOrThrow(_stmt, "recurrence")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfBatchId: Int = getColumnIndexOrThrow(_stmt, "batchId")
        val _columnIndexOfReminderBefore: Int = getColumnIndexOrThrow(_stmt, "reminderBefore")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfMetadata: Int = getColumnIndexOrThrow(_stmt, "metadata")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _result: MutableList<FarmEventEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: FarmEventEntity
          val _tmpEventId: String
          _tmpEventId = _stmt.getText(_columnIndexOfEventId)
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpEventType: FarmEventType
          _tmpEventType = __FarmEventType_stringToEnum(_stmt.getText(_columnIndexOfEventType))
          val _tmpTitle: String
          _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          val _tmpDescription: String
          _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          val _tmpScheduledAt: Long
          _tmpScheduledAt = _stmt.getLong(_columnIndexOfScheduledAt)
          val _tmpCompletedAt: Long?
          if (_stmt.isNull(_columnIndexOfCompletedAt)) {
            _tmpCompletedAt = null
          } else {
            _tmpCompletedAt = _stmt.getLong(_columnIndexOfCompletedAt)
          }
          val _tmpRecurrence: RecurrenceType
          _tmpRecurrence = __RecurrenceType_stringToEnum(_stmt.getText(_columnIndexOfRecurrence))
          val _tmpProductId: String?
          if (_stmt.isNull(_columnIndexOfProductId)) {
            _tmpProductId = null
          } else {
            _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          }
          val _tmpBatchId: String?
          if (_stmt.isNull(_columnIndexOfBatchId)) {
            _tmpBatchId = null
          } else {
            _tmpBatchId = _stmt.getText(_columnIndexOfBatchId)
          }
          val _tmpReminderBefore: Long
          _tmpReminderBefore = _stmt.getLong(_columnIndexOfReminderBefore)
          val _tmpStatus: EventStatus
          _tmpStatus = __EventStatus_stringToEnum(_stmt.getText(_columnIndexOfStatus))
          val _tmpMetadata: String?
          if (_stmt.isNull(_columnIndexOfMetadata)) {
            _tmpMetadata = null
          } else {
            _tmpMetadata = _stmt.getText(_columnIndexOfMetadata)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          _item =
              FarmEventEntity(_tmpEventId,_tmpFarmerId,_tmpEventType,_tmpTitle,_tmpDescription,_tmpScheduledAt,_tmpCompletedAt,_tmpRecurrence,_tmpProductId,_tmpBatchId,_tmpReminderBefore,_tmpStatus,_tmpMetadata,_tmpCreatedAt,_tmpUpdatedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getUpcomingEvents(
    farmerId: String,
    currentTime: Long,
    limit: Int,
  ): Flow<List<FarmEventEntity>> {
    val _sql: String =
        "SELECT * FROM farm_events WHERE farmerId = ? AND status = 'PENDING' AND scheduledAt >= ? ORDER BY scheduledAt ASC LIMIT ?"
    return createFlow(__db, false, arrayOf("farm_events")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, farmerId)
        _argIndex = 2
        _stmt.bindLong(_argIndex, currentTime)
        _argIndex = 3
        _stmt.bindLong(_argIndex, limit.toLong())
        val _columnIndexOfEventId: Int = getColumnIndexOrThrow(_stmt, "eventId")
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfEventType: Int = getColumnIndexOrThrow(_stmt, "eventType")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfScheduledAt: Int = getColumnIndexOrThrow(_stmt, "scheduledAt")
        val _columnIndexOfCompletedAt: Int = getColumnIndexOrThrow(_stmt, "completedAt")
        val _columnIndexOfRecurrence: Int = getColumnIndexOrThrow(_stmt, "recurrence")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfBatchId: Int = getColumnIndexOrThrow(_stmt, "batchId")
        val _columnIndexOfReminderBefore: Int = getColumnIndexOrThrow(_stmt, "reminderBefore")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfMetadata: Int = getColumnIndexOrThrow(_stmt, "metadata")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _result: MutableList<FarmEventEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: FarmEventEntity
          val _tmpEventId: String
          _tmpEventId = _stmt.getText(_columnIndexOfEventId)
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpEventType: FarmEventType
          _tmpEventType = __FarmEventType_stringToEnum(_stmt.getText(_columnIndexOfEventType))
          val _tmpTitle: String
          _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          val _tmpDescription: String
          _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          val _tmpScheduledAt: Long
          _tmpScheduledAt = _stmt.getLong(_columnIndexOfScheduledAt)
          val _tmpCompletedAt: Long?
          if (_stmt.isNull(_columnIndexOfCompletedAt)) {
            _tmpCompletedAt = null
          } else {
            _tmpCompletedAt = _stmt.getLong(_columnIndexOfCompletedAt)
          }
          val _tmpRecurrence: RecurrenceType
          _tmpRecurrence = __RecurrenceType_stringToEnum(_stmt.getText(_columnIndexOfRecurrence))
          val _tmpProductId: String?
          if (_stmt.isNull(_columnIndexOfProductId)) {
            _tmpProductId = null
          } else {
            _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          }
          val _tmpBatchId: String?
          if (_stmt.isNull(_columnIndexOfBatchId)) {
            _tmpBatchId = null
          } else {
            _tmpBatchId = _stmt.getText(_columnIndexOfBatchId)
          }
          val _tmpReminderBefore: Long
          _tmpReminderBefore = _stmt.getLong(_columnIndexOfReminderBefore)
          val _tmpStatus: EventStatus
          _tmpStatus = __EventStatus_stringToEnum(_stmt.getText(_columnIndexOfStatus))
          val _tmpMetadata: String?
          if (_stmt.isNull(_columnIndexOfMetadata)) {
            _tmpMetadata = null
          } else {
            _tmpMetadata = _stmt.getText(_columnIndexOfMetadata)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          _item =
              FarmEventEntity(_tmpEventId,_tmpFarmerId,_tmpEventType,_tmpTitle,_tmpDescription,_tmpScheduledAt,_tmpCompletedAt,_tmpRecurrence,_tmpProductId,_tmpBatchId,_tmpReminderBefore,_tmpStatus,_tmpMetadata,_tmpCreatedAt,_tmpUpdatedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getEventsByType(farmerId: String, type: FarmEventType):
      Flow<List<FarmEventEntity>> {
    val _sql: String =
        "SELECT * FROM farm_events WHERE farmerId = ? AND eventType = ? ORDER BY scheduledAt ASC"
    return createFlow(__db, false, arrayOf("farm_events")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, farmerId)
        _argIndex = 2
        _stmt.bindText(_argIndex, __FarmEventType_enumToString(type))
        val _columnIndexOfEventId: Int = getColumnIndexOrThrow(_stmt, "eventId")
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfEventType: Int = getColumnIndexOrThrow(_stmt, "eventType")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfScheduledAt: Int = getColumnIndexOrThrow(_stmt, "scheduledAt")
        val _columnIndexOfCompletedAt: Int = getColumnIndexOrThrow(_stmt, "completedAt")
        val _columnIndexOfRecurrence: Int = getColumnIndexOrThrow(_stmt, "recurrence")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfBatchId: Int = getColumnIndexOrThrow(_stmt, "batchId")
        val _columnIndexOfReminderBefore: Int = getColumnIndexOrThrow(_stmt, "reminderBefore")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfMetadata: Int = getColumnIndexOrThrow(_stmt, "metadata")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _result: MutableList<FarmEventEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: FarmEventEntity
          val _tmpEventId: String
          _tmpEventId = _stmt.getText(_columnIndexOfEventId)
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpEventType: FarmEventType
          _tmpEventType = __FarmEventType_stringToEnum(_stmt.getText(_columnIndexOfEventType))
          val _tmpTitle: String
          _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          val _tmpDescription: String
          _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          val _tmpScheduledAt: Long
          _tmpScheduledAt = _stmt.getLong(_columnIndexOfScheduledAt)
          val _tmpCompletedAt: Long?
          if (_stmt.isNull(_columnIndexOfCompletedAt)) {
            _tmpCompletedAt = null
          } else {
            _tmpCompletedAt = _stmt.getLong(_columnIndexOfCompletedAt)
          }
          val _tmpRecurrence: RecurrenceType
          _tmpRecurrence = __RecurrenceType_stringToEnum(_stmt.getText(_columnIndexOfRecurrence))
          val _tmpProductId: String?
          if (_stmt.isNull(_columnIndexOfProductId)) {
            _tmpProductId = null
          } else {
            _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          }
          val _tmpBatchId: String?
          if (_stmt.isNull(_columnIndexOfBatchId)) {
            _tmpBatchId = null
          } else {
            _tmpBatchId = _stmt.getText(_columnIndexOfBatchId)
          }
          val _tmpReminderBefore: Long
          _tmpReminderBefore = _stmt.getLong(_columnIndexOfReminderBefore)
          val _tmpStatus: EventStatus
          _tmpStatus = __EventStatus_stringToEnum(_stmt.getText(_columnIndexOfStatus))
          val _tmpMetadata: String?
          if (_stmt.isNull(_columnIndexOfMetadata)) {
            _tmpMetadata = null
          } else {
            _tmpMetadata = _stmt.getText(_columnIndexOfMetadata)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          _item =
              FarmEventEntity(_tmpEventId,_tmpFarmerId,_tmpEventType,_tmpTitle,_tmpDescription,_tmpScheduledAt,_tmpCompletedAt,_tmpRecurrence,_tmpProductId,_tmpBatchId,_tmpReminderBefore,_tmpStatus,_tmpMetadata,_tmpCreatedAt,_tmpUpdatedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getPendingEventsInRange(
    farmerId: String,
    startTime: Long,
    endTime: Long,
  ): List<FarmEventEntity> {
    val _sql: String =
        "SELECT * FROM farm_events WHERE farmerId = ? AND status = 'PENDING' AND scheduledAt BETWEEN ? AND ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, farmerId)
        _argIndex = 2
        _stmt.bindLong(_argIndex, startTime)
        _argIndex = 3
        _stmt.bindLong(_argIndex, endTime)
        val _columnIndexOfEventId: Int = getColumnIndexOrThrow(_stmt, "eventId")
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfEventType: Int = getColumnIndexOrThrow(_stmt, "eventType")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfScheduledAt: Int = getColumnIndexOrThrow(_stmt, "scheduledAt")
        val _columnIndexOfCompletedAt: Int = getColumnIndexOrThrow(_stmt, "completedAt")
        val _columnIndexOfRecurrence: Int = getColumnIndexOrThrow(_stmt, "recurrence")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfBatchId: Int = getColumnIndexOrThrow(_stmt, "batchId")
        val _columnIndexOfReminderBefore: Int = getColumnIndexOrThrow(_stmt, "reminderBefore")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfMetadata: Int = getColumnIndexOrThrow(_stmt, "metadata")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _result: MutableList<FarmEventEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: FarmEventEntity
          val _tmpEventId: String
          _tmpEventId = _stmt.getText(_columnIndexOfEventId)
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpEventType: FarmEventType
          _tmpEventType = __FarmEventType_stringToEnum(_stmt.getText(_columnIndexOfEventType))
          val _tmpTitle: String
          _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          val _tmpDescription: String
          _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          val _tmpScheduledAt: Long
          _tmpScheduledAt = _stmt.getLong(_columnIndexOfScheduledAt)
          val _tmpCompletedAt: Long?
          if (_stmt.isNull(_columnIndexOfCompletedAt)) {
            _tmpCompletedAt = null
          } else {
            _tmpCompletedAt = _stmt.getLong(_columnIndexOfCompletedAt)
          }
          val _tmpRecurrence: RecurrenceType
          _tmpRecurrence = __RecurrenceType_stringToEnum(_stmt.getText(_columnIndexOfRecurrence))
          val _tmpProductId: String?
          if (_stmt.isNull(_columnIndexOfProductId)) {
            _tmpProductId = null
          } else {
            _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          }
          val _tmpBatchId: String?
          if (_stmt.isNull(_columnIndexOfBatchId)) {
            _tmpBatchId = null
          } else {
            _tmpBatchId = _stmt.getText(_columnIndexOfBatchId)
          }
          val _tmpReminderBefore: Long
          _tmpReminderBefore = _stmt.getLong(_columnIndexOfReminderBefore)
          val _tmpStatus: EventStatus
          _tmpStatus = __EventStatus_stringToEnum(_stmt.getText(_columnIndexOfStatus))
          val _tmpMetadata: String?
          if (_stmt.isNull(_columnIndexOfMetadata)) {
            _tmpMetadata = null
          } else {
            _tmpMetadata = _stmt.getText(_columnIndexOfMetadata)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          _item =
              FarmEventEntity(_tmpEventId,_tmpFarmerId,_tmpEventType,_tmpTitle,_tmpDescription,_tmpScheduledAt,_tmpCompletedAt,_tmpRecurrence,_tmpProductId,_tmpBatchId,_tmpReminderBefore,_tmpStatus,_tmpMetadata,_tmpCreatedAt,_tmpUpdatedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getCompletedEventsByFarmer(farmerId: String): List<FarmEventEntity> {
    val _sql: String = "SELECT * FROM farm_events WHERE farmerId = ? AND status = 'COMPLETED'"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, farmerId)
        val _columnIndexOfEventId: Int = getColumnIndexOrThrow(_stmt, "eventId")
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfEventType: Int = getColumnIndexOrThrow(_stmt, "eventType")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfScheduledAt: Int = getColumnIndexOrThrow(_stmt, "scheduledAt")
        val _columnIndexOfCompletedAt: Int = getColumnIndexOrThrow(_stmt, "completedAt")
        val _columnIndexOfRecurrence: Int = getColumnIndexOrThrow(_stmt, "recurrence")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfBatchId: Int = getColumnIndexOrThrow(_stmt, "batchId")
        val _columnIndexOfReminderBefore: Int = getColumnIndexOrThrow(_stmt, "reminderBefore")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfMetadata: Int = getColumnIndexOrThrow(_stmt, "metadata")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _result: MutableList<FarmEventEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: FarmEventEntity
          val _tmpEventId: String
          _tmpEventId = _stmt.getText(_columnIndexOfEventId)
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpEventType: FarmEventType
          _tmpEventType = __FarmEventType_stringToEnum(_stmt.getText(_columnIndexOfEventType))
          val _tmpTitle: String
          _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          val _tmpDescription: String
          _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          val _tmpScheduledAt: Long
          _tmpScheduledAt = _stmt.getLong(_columnIndexOfScheduledAt)
          val _tmpCompletedAt: Long?
          if (_stmt.isNull(_columnIndexOfCompletedAt)) {
            _tmpCompletedAt = null
          } else {
            _tmpCompletedAt = _stmt.getLong(_columnIndexOfCompletedAt)
          }
          val _tmpRecurrence: RecurrenceType
          _tmpRecurrence = __RecurrenceType_stringToEnum(_stmt.getText(_columnIndexOfRecurrence))
          val _tmpProductId: String?
          if (_stmt.isNull(_columnIndexOfProductId)) {
            _tmpProductId = null
          } else {
            _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          }
          val _tmpBatchId: String?
          if (_stmt.isNull(_columnIndexOfBatchId)) {
            _tmpBatchId = null
          } else {
            _tmpBatchId = _stmt.getText(_columnIndexOfBatchId)
          }
          val _tmpReminderBefore: Long
          _tmpReminderBefore = _stmt.getLong(_columnIndexOfReminderBefore)
          val _tmpStatus: EventStatus
          _tmpStatus = __EventStatus_stringToEnum(_stmt.getText(_columnIndexOfStatus))
          val _tmpMetadata: String?
          if (_stmt.isNull(_columnIndexOfMetadata)) {
            _tmpMetadata = null
          } else {
            _tmpMetadata = _stmt.getText(_columnIndexOfMetadata)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          _item =
              FarmEventEntity(_tmpEventId,_tmpFarmerId,_tmpEventType,_tmpTitle,_tmpDescription,_tmpScheduledAt,_tmpCompletedAt,_tmpRecurrence,_tmpProductId,_tmpBatchId,_tmpReminderBefore,_tmpStatus,_tmpMetadata,_tmpCreatedAt,_tmpUpdatedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun markCompleted(eventId: String, completedAt: Long) {
    val _sql: String =
        "UPDATE farm_events SET status = 'COMPLETED', completedAt = ? WHERE eventId = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, completedAt)
        _argIndex = 2
        _stmt.bindText(_argIndex, eventId)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun deleteEvent(eventId: String) {
    val _sql: String = "DELETE FROM farm_events WHERE eventId = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, eventId)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  private fun __FarmEventType_enumToString(_value: FarmEventType): String = when (_value) {
    FarmEventType.VACCINATION -> "VACCINATION"
    FarmEventType.DEWORMING -> "DEWORMING"
    FarmEventType.BIOSECURITY -> "BIOSECURITY"
    FarmEventType.FEEDING -> "FEEDING"
    FarmEventType.WEIGHING -> "WEIGHING"
    FarmEventType.CLEANING -> "CLEANING"
    FarmEventType.OTHER -> "OTHER"
  }

  private fun __RecurrenceType_enumToString(_value: RecurrenceType): String = when (_value) {
    RecurrenceType.ONCE -> "ONCE"
    RecurrenceType.DAILY -> "DAILY"
    RecurrenceType.WEEKLY -> "WEEKLY"
    RecurrenceType.MONTHLY -> "MONTHLY"
  }

  private fun __EventStatus_enumToString(_value: EventStatus): String = when (_value) {
    EventStatus.PENDING -> "PENDING"
    EventStatus.COMPLETED -> "COMPLETED"
    EventStatus.CANCELLED -> "CANCELLED"
  }

  private fun __FarmEventType_stringToEnum(_value: String): FarmEventType = when (_value) {
    "VACCINATION" -> FarmEventType.VACCINATION
    "DEWORMING" -> FarmEventType.DEWORMING
    "BIOSECURITY" -> FarmEventType.BIOSECURITY
    "FEEDING" -> FarmEventType.FEEDING
    "WEIGHING" -> FarmEventType.WEIGHING
    "CLEANING" -> FarmEventType.CLEANING
    "OTHER" -> FarmEventType.OTHER
    else -> throw IllegalArgumentException("Can't convert value to enum, unknown value: " + _value)
  }

  private fun __RecurrenceType_stringToEnum(_value: String): RecurrenceType = when (_value) {
    "ONCE" -> RecurrenceType.ONCE
    "DAILY" -> RecurrenceType.DAILY
    "WEEKLY" -> RecurrenceType.WEEKLY
    "MONTHLY" -> RecurrenceType.MONTHLY
    else -> throw IllegalArgumentException("Can't convert value to enum, unknown value: " + _value)
  }

  private fun __EventStatus_stringToEnum(_value: String): EventStatus = when (_value) {
    "PENDING" -> EventStatus.PENDING
    "COMPLETED" -> EventStatus.COMPLETED
    "CANCELLED" -> EventStatus.CANCELLED
    else -> throw IllegalArgumentException("Can't convert value to enum, unknown value: " + _value)
  }

  public companion object {
    public fun getRequiredConverters(): List<KClass<*>> = emptyList()
  }
}
