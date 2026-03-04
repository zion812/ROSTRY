package com.rio.rostry.`data`.database.dao

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.rio.rostry.`data`.database.entity.BirdEventEntity
import javax.`annotation`.processing.Generated
import kotlin.Boolean
import kotlin.Double
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
public class BirdEventDao_Impl(
  __db: RoomDatabase,
) : BirdEventDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfBirdEventEntity: EntityInsertAdapter<BirdEventEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfBirdEventEntity = object : EntityInsertAdapter<BirdEventEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `bird_events` (`eventId`,`birdId`,`ownerId`,`eventType`,`eventTitle`,`eventDescription`,`eventDate`,`ageDaysAtEvent`,`lifecycleStageAtEvent`,`numericValue`,`numericValue2`,`stringValue`,`dataJson`,`morphologyScoreDelta`,`geneticsScoreDelta`,`performanceScoreDelta`,`healthScoreDelta`,`marketScoreDelta`,`recordedBy`,`isVerified`,`verifiedBy`,`mediaUrlsJson`,`createdAt`,`dirty`,`syncedAt`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: BirdEventEntity) {
        statement.bindText(1, entity.eventId)
        statement.bindText(2, entity.birdId)
        statement.bindText(3, entity.ownerId)
        statement.bindText(4, entity.eventType)
        statement.bindText(5, entity.eventTitle)
        val _tmpEventDescription: String? = entity.eventDescription
        if (_tmpEventDescription == null) {
          statement.bindNull(6)
        } else {
          statement.bindText(6, _tmpEventDescription)
        }
        statement.bindLong(7, entity.eventDate)
        val _tmpAgeDaysAtEvent: Int? = entity.ageDaysAtEvent
        if (_tmpAgeDaysAtEvent == null) {
          statement.bindNull(8)
        } else {
          statement.bindLong(8, _tmpAgeDaysAtEvent.toLong())
        }
        val _tmpLifecycleStageAtEvent: String? = entity.lifecycleStageAtEvent
        if (_tmpLifecycleStageAtEvent == null) {
          statement.bindNull(9)
        } else {
          statement.bindText(9, _tmpLifecycleStageAtEvent)
        }
        val _tmpNumericValue: Double? = entity.numericValue
        if (_tmpNumericValue == null) {
          statement.bindNull(10)
        } else {
          statement.bindDouble(10, _tmpNumericValue)
        }
        val _tmpNumericValue2: Double? = entity.numericValue2
        if (_tmpNumericValue2 == null) {
          statement.bindNull(11)
        } else {
          statement.bindDouble(11, _tmpNumericValue2)
        }
        val _tmpStringValue: String? = entity.stringValue
        if (_tmpStringValue == null) {
          statement.bindNull(12)
        } else {
          statement.bindText(12, _tmpStringValue)
        }
        val _tmpDataJson: String? = entity.dataJson
        if (_tmpDataJson == null) {
          statement.bindNull(13)
        } else {
          statement.bindText(13, _tmpDataJson)
        }
        val _tmpMorphologyScoreDelta: Int? = entity.morphologyScoreDelta
        if (_tmpMorphologyScoreDelta == null) {
          statement.bindNull(14)
        } else {
          statement.bindLong(14, _tmpMorphologyScoreDelta.toLong())
        }
        val _tmpGeneticsScoreDelta: Int? = entity.geneticsScoreDelta
        if (_tmpGeneticsScoreDelta == null) {
          statement.bindNull(15)
        } else {
          statement.bindLong(15, _tmpGeneticsScoreDelta.toLong())
        }
        val _tmpPerformanceScoreDelta: Int? = entity.performanceScoreDelta
        if (_tmpPerformanceScoreDelta == null) {
          statement.bindNull(16)
        } else {
          statement.bindLong(16, _tmpPerformanceScoreDelta.toLong())
        }
        val _tmpHealthScoreDelta: Int? = entity.healthScoreDelta
        if (_tmpHealthScoreDelta == null) {
          statement.bindNull(17)
        } else {
          statement.bindLong(17, _tmpHealthScoreDelta.toLong())
        }
        val _tmpMarketScoreDelta: Int? = entity.marketScoreDelta
        if (_tmpMarketScoreDelta == null) {
          statement.bindNull(18)
        } else {
          statement.bindLong(18, _tmpMarketScoreDelta.toLong())
        }
        val _tmpRecordedBy: String? = entity.recordedBy
        if (_tmpRecordedBy == null) {
          statement.bindNull(19)
        } else {
          statement.bindText(19, _tmpRecordedBy)
        }
        val _tmp: Int = if (entity.isVerified) 1 else 0
        statement.bindLong(20, _tmp.toLong())
        val _tmpVerifiedBy: String? = entity.verifiedBy
        if (_tmpVerifiedBy == null) {
          statement.bindNull(21)
        } else {
          statement.bindText(21, _tmpVerifiedBy)
        }
        val _tmpMediaUrlsJson: String? = entity.mediaUrlsJson
        if (_tmpMediaUrlsJson == null) {
          statement.bindNull(22)
        } else {
          statement.bindText(22, _tmpMediaUrlsJson)
        }
        statement.bindLong(23, entity.createdAt)
        val _tmp_1: Int = if (entity.dirty) 1 else 0
        statement.bindLong(24, _tmp_1.toLong())
        val _tmpSyncedAt: Long? = entity.syncedAt
        if (_tmpSyncedAt == null) {
          statement.bindNull(25)
        } else {
          statement.bindLong(25, _tmpSyncedAt)
        }
      }
    }
  }

  public override suspend fun insert(event: BirdEventEntity): Unit = performSuspending(__db, false,
      true) { _connection ->
    __insertAdapterOfBirdEventEntity.insert(_connection, event)
  }

  public override suspend fun insertAll(events: List<BirdEventEntity>): Unit =
      performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfBirdEventEntity.insert(_connection, events)
  }

  public override fun getEventsForBird(birdId: String): Flow<List<BirdEventEntity>> {
    val _sql: String = "SELECT * FROM bird_events WHERE birdId = ? ORDER BY eventDate DESC"
    return createFlow(__db, false, arrayOf("bird_events")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, birdId)
        val _columnIndexOfEventId: Int = getColumnIndexOrThrow(_stmt, "eventId")
        val _columnIndexOfBirdId: Int = getColumnIndexOrThrow(_stmt, "birdId")
        val _columnIndexOfOwnerId: Int = getColumnIndexOrThrow(_stmt, "ownerId")
        val _columnIndexOfEventType: Int = getColumnIndexOrThrow(_stmt, "eventType")
        val _columnIndexOfEventTitle: Int = getColumnIndexOrThrow(_stmt, "eventTitle")
        val _columnIndexOfEventDescription: Int = getColumnIndexOrThrow(_stmt, "eventDescription")
        val _columnIndexOfEventDate: Int = getColumnIndexOrThrow(_stmt, "eventDate")
        val _columnIndexOfAgeDaysAtEvent: Int = getColumnIndexOrThrow(_stmt, "ageDaysAtEvent")
        val _columnIndexOfLifecycleStageAtEvent: Int = getColumnIndexOrThrow(_stmt,
            "lifecycleStageAtEvent")
        val _columnIndexOfNumericValue: Int = getColumnIndexOrThrow(_stmt, "numericValue")
        val _columnIndexOfNumericValue2: Int = getColumnIndexOrThrow(_stmt, "numericValue2")
        val _columnIndexOfStringValue: Int = getColumnIndexOrThrow(_stmt, "stringValue")
        val _columnIndexOfDataJson: Int = getColumnIndexOrThrow(_stmt, "dataJson")
        val _columnIndexOfMorphologyScoreDelta: Int = getColumnIndexOrThrow(_stmt,
            "morphologyScoreDelta")
        val _columnIndexOfGeneticsScoreDelta: Int = getColumnIndexOrThrow(_stmt,
            "geneticsScoreDelta")
        val _columnIndexOfPerformanceScoreDelta: Int = getColumnIndexOrThrow(_stmt,
            "performanceScoreDelta")
        val _columnIndexOfHealthScoreDelta: Int = getColumnIndexOrThrow(_stmt, "healthScoreDelta")
        val _columnIndexOfMarketScoreDelta: Int = getColumnIndexOrThrow(_stmt, "marketScoreDelta")
        val _columnIndexOfRecordedBy: Int = getColumnIndexOrThrow(_stmt, "recordedBy")
        val _columnIndexOfIsVerified: Int = getColumnIndexOrThrow(_stmt, "isVerified")
        val _columnIndexOfVerifiedBy: Int = getColumnIndexOrThrow(_stmt, "verifiedBy")
        val _columnIndexOfMediaUrlsJson: Int = getColumnIndexOrThrow(_stmt, "mediaUrlsJson")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _result: MutableList<BirdEventEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: BirdEventEntity
          val _tmpEventId: String
          _tmpEventId = _stmt.getText(_columnIndexOfEventId)
          val _tmpBirdId: String
          _tmpBirdId = _stmt.getText(_columnIndexOfBirdId)
          val _tmpOwnerId: String
          _tmpOwnerId = _stmt.getText(_columnIndexOfOwnerId)
          val _tmpEventType: String
          _tmpEventType = _stmt.getText(_columnIndexOfEventType)
          val _tmpEventTitle: String
          _tmpEventTitle = _stmt.getText(_columnIndexOfEventTitle)
          val _tmpEventDescription: String?
          if (_stmt.isNull(_columnIndexOfEventDescription)) {
            _tmpEventDescription = null
          } else {
            _tmpEventDescription = _stmt.getText(_columnIndexOfEventDescription)
          }
          val _tmpEventDate: Long
          _tmpEventDate = _stmt.getLong(_columnIndexOfEventDate)
          val _tmpAgeDaysAtEvent: Int?
          if (_stmt.isNull(_columnIndexOfAgeDaysAtEvent)) {
            _tmpAgeDaysAtEvent = null
          } else {
            _tmpAgeDaysAtEvent = _stmt.getLong(_columnIndexOfAgeDaysAtEvent).toInt()
          }
          val _tmpLifecycleStageAtEvent: String?
          if (_stmt.isNull(_columnIndexOfLifecycleStageAtEvent)) {
            _tmpLifecycleStageAtEvent = null
          } else {
            _tmpLifecycleStageAtEvent = _stmt.getText(_columnIndexOfLifecycleStageAtEvent)
          }
          val _tmpNumericValue: Double?
          if (_stmt.isNull(_columnIndexOfNumericValue)) {
            _tmpNumericValue = null
          } else {
            _tmpNumericValue = _stmt.getDouble(_columnIndexOfNumericValue)
          }
          val _tmpNumericValue2: Double?
          if (_stmt.isNull(_columnIndexOfNumericValue2)) {
            _tmpNumericValue2 = null
          } else {
            _tmpNumericValue2 = _stmt.getDouble(_columnIndexOfNumericValue2)
          }
          val _tmpStringValue: String?
          if (_stmt.isNull(_columnIndexOfStringValue)) {
            _tmpStringValue = null
          } else {
            _tmpStringValue = _stmt.getText(_columnIndexOfStringValue)
          }
          val _tmpDataJson: String?
          if (_stmt.isNull(_columnIndexOfDataJson)) {
            _tmpDataJson = null
          } else {
            _tmpDataJson = _stmt.getText(_columnIndexOfDataJson)
          }
          val _tmpMorphologyScoreDelta: Int?
          if (_stmt.isNull(_columnIndexOfMorphologyScoreDelta)) {
            _tmpMorphologyScoreDelta = null
          } else {
            _tmpMorphologyScoreDelta = _stmt.getLong(_columnIndexOfMorphologyScoreDelta).toInt()
          }
          val _tmpGeneticsScoreDelta: Int?
          if (_stmt.isNull(_columnIndexOfGeneticsScoreDelta)) {
            _tmpGeneticsScoreDelta = null
          } else {
            _tmpGeneticsScoreDelta = _stmt.getLong(_columnIndexOfGeneticsScoreDelta).toInt()
          }
          val _tmpPerformanceScoreDelta: Int?
          if (_stmt.isNull(_columnIndexOfPerformanceScoreDelta)) {
            _tmpPerformanceScoreDelta = null
          } else {
            _tmpPerformanceScoreDelta = _stmt.getLong(_columnIndexOfPerformanceScoreDelta).toInt()
          }
          val _tmpHealthScoreDelta: Int?
          if (_stmt.isNull(_columnIndexOfHealthScoreDelta)) {
            _tmpHealthScoreDelta = null
          } else {
            _tmpHealthScoreDelta = _stmt.getLong(_columnIndexOfHealthScoreDelta).toInt()
          }
          val _tmpMarketScoreDelta: Int?
          if (_stmt.isNull(_columnIndexOfMarketScoreDelta)) {
            _tmpMarketScoreDelta = null
          } else {
            _tmpMarketScoreDelta = _stmt.getLong(_columnIndexOfMarketScoreDelta).toInt()
          }
          val _tmpRecordedBy: String?
          if (_stmt.isNull(_columnIndexOfRecordedBy)) {
            _tmpRecordedBy = null
          } else {
            _tmpRecordedBy = _stmt.getText(_columnIndexOfRecordedBy)
          }
          val _tmpIsVerified: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsVerified).toInt()
          _tmpIsVerified = _tmp != 0
          val _tmpVerifiedBy: String?
          if (_stmt.isNull(_columnIndexOfVerifiedBy)) {
            _tmpVerifiedBy = null
          } else {
            _tmpVerifiedBy = _stmt.getText(_columnIndexOfVerifiedBy)
          }
          val _tmpMediaUrlsJson: String?
          if (_stmt.isNull(_columnIndexOfMediaUrlsJson)) {
            _tmpMediaUrlsJson = null
          } else {
            _tmpMediaUrlsJson = _stmt.getText(_columnIndexOfMediaUrlsJson)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
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
              BirdEventEntity(_tmpEventId,_tmpBirdId,_tmpOwnerId,_tmpEventType,_tmpEventTitle,_tmpEventDescription,_tmpEventDate,_tmpAgeDaysAtEvent,_tmpLifecycleStageAtEvent,_tmpNumericValue,_tmpNumericValue2,_tmpStringValue,_tmpDataJson,_tmpMorphologyScoreDelta,_tmpGeneticsScoreDelta,_tmpPerformanceScoreDelta,_tmpHealthScoreDelta,_tmpMarketScoreDelta,_tmpRecordedBy,_tmpIsVerified,_tmpVerifiedBy,_tmpMediaUrlsJson,_tmpCreatedAt,_tmpDirty,_tmpSyncedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getEventsForBirdByType(birdId: String, eventType: String):
      Flow<List<BirdEventEntity>> {
    val _sql: String =
        "SELECT * FROM bird_events WHERE birdId = ? AND eventType = ? ORDER BY eventDate DESC"
    return createFlow(__db, false, arrayOf("bird_events")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, birdId)
        _argIndex = 2
        _stmt.bindText(_argIndex, eventType)
        val _columnIndexOfEventId: Int = getColumnIndexOrThrow(_stmt, "eventId")
        val _columnIndexOfBirdId: Int = getColumnIndexOrThrow(_stmt, "birdId")
        val _columnIndexOfOwnerId: Int = getColumnIndexOrThrow(_stmt, "ownerId")
        val _columnIndexOfEventType: Int = getColumnIndexOrThrow(_stmt, "eventType")
        val _columnIndexOfEventTitle: Int = getColumnIndexOrThrow(_stmt, "eventTitle")
        val _columnIndexOfEventDescription: Int = getColumnIndexOrThrow(_stmt, "eventDescription")
        val _columnIndexOfEventDate: Int = getColumnIndexOrThrow(_stmt, "eventDate")
        val _columnIndexOfAgeDaysAtEvent: Int = getColumnIndexOrThrow(_stmt, "ageDaysAtEvent")
        val _columnIndexOfLifecycleStageAtEvent: Int = getColumnIndexOrThrow(_stmt,
            "lifecycleStageAtEvent")
        val _columnIndexOfNumericValue: Int = getColumnIndexOrThrow(_stmt, "numericValue")
        val _columnIndexOfNumericValue2: Int = getColumnIndexOrThrow(_stmt, "numericValue2")
        val _columnIndexOfStringValue: Int = getColumnIndexOrThrow(_stmt, "stringValue")
        val _columnIndexOfDataJson: Int = getColumnIndexOrThrow(_stmt, "dataJson")
        val _columnIndexOfMorphologyScoreDelta: Int = getColumnIndexOrThrow(_stmt,
            "morphologyScoreDelta")
        val _columnIndexOfGeneticsScoreDelta: Int = getColumnIndexOrThrow(_stmt,
            "geneticsScoreDelta")
        val _columnIndexOfPerformanceScoreDelta: Int = getColumnIndexOrThrow(_stmt,
            "performanceScoreDelta")
        val _columnIndexOfHealthScoreDelta: Int = getColumnIndexOrThrow(_stmt, "healthScoreDelta")
        val _columnIndexOfMarketScoreDelta: Int = getColumnIndexOrThrow(_stmt, "marketScoreDelta")
        val _columnIndexOfRecordedBy: Int = getColumnIndexOrThrow(_stmt, "recordedBy")
        val _columnIndexOfIsVerified: Int = getColumnIndexOrThrow(_stmt, "isVerified")
        val _columnIndexOfVerifiedBy: Int = getColumnIndexOrThrow(_stmt, "verifiedBy")
        val _columnIndexOfMediaUrlsJson: Int = getColumnIndexOrThrow(_stmt, "mediaUrlsJson")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _result: MutableList<BirdEventEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: BirdEventEntity
          val _tmpEventId: String
          _tmpEventId = _stmt.getText(_columnIndexOfEventId)
          val _tmpBirdId: String
          _tmpBirdId = _stmt.getText(_columnIndexOfBirdId)
          val _tmpOwnerId: String
          _tmpOwnerId = _stmt.getText(_columnIndexOfOwnerId)
          val _tmpEventType: String
          _tmpEventType = _stmt.getText(_columnIndexOfEventType)
          val _tmpEventTitle: String
          _tmpEventTitle = _stmt.getText(_columnIndexOfEventTitle)
          val _tmpEventDescription: String?
          if (_stmt.isNull(_columnIndexOfEventDescription)) {
            _tmpEventDescription = null
          } else {
            _tmpEventDescription = _stmt.getText(_columnIndexOfEventDescription)
          }
          val _tmpEventDate: Long
          _tmpEventDate = _stmt.getLong(_columnIndexOfEventDate)
          val _tmpAgeDaysAtEvent: Int?
          if (_stmt.isNull(_columnIndexOfAgeDaysAtEvent)) {
            _tmpAgeDaysAtEvent = null
          } else {
            _tmpAgeDaysAtEvent = _stmt.getLong(_columnIndexOfAgeDaysAtEvent).toInt()
          }
          val _tmpLifecycleStageAtEvent: String?
          if (_stmt.isNull(_columnIndexOfLifecycleStageAtEvent)) {
            _tmpLifecycleStageAtEvent = null
          } else {
            _tmpLifecycleStageAtEvent = _stmt.getText(_columnIndexOfLifecycleStageAtEvent)
          }
          val _tmpNumericValue: Double?
          if (_stmt.isNull(_columnIndexOfNumericValue)) {
            _tmpNumericValue = null
          } else {
            _tmpNumericValue = _stmt.getDouble(_columnIndexOfNumericValue)
          }
          val _tmpNumericValue2: Double?
          if (_stmt.isNull(_columnIndexOfNumericValue2)) {
            _tmpNumericValue2 = null
          } else {
            _tmpNumericValue2 = _stmt.getDouble(_columnIndexOfNumericValue2)
          }
          val _tmpStringValue: String?
          if (_stmt.isNull(_columnIndexOfStringValue)) {
            _tmpStringValue = null
          } else {
            _tmpStringValue = _stmt.getText(_columnIndexOfStringValue)
          }
          val _tmpDataJson: String?
          if (_stmt.isNull(_columnIndexOfDataJson)) {
            _tmpDataJson = null
          } else {
            _tmpDataJson = _stmt.getText(_columnIndexOfDataJson)
          }
          val _tmpMorphologyScoreDelta: Int?
          if (_stmt.isNull(_columnIndexOfMorphologyScoreDelta)) {
            _tmpMorphologyScoreDelta = null
          } else {
            _tmpMorphologyScoreDelta = _stmt.getLong(_columnIndexOfMorphologyScoreDelta).toInt()
          }
          val _tmpGeneticsScoreDelta: Int?
          if (_stmt.isNull(_columnIndexOfGeneticsScoreDelta)) {
            _tmpGeneticsScoreDelta = null
          } else {
            _tmpGeneticsScoreDelta = _stmt.getLong(_columnIndexOfGeneticsScoreDelta).toInt()
          }
          val _tmpPerformanceScoreDelta: Int?
          if (_stmt.isNull(_columnIndexOfPerformanceScoreDelta)) {
            _tmpPerformanceScoreDelta = null
          } else {
            _tmpPerformanceScoreDelta = _stmt.getLong(_columnIndexOfPerformanceScoreDelta).toInt()
          }
          val _tmpHealthScoreDelta: Int?
          if (_stmt.isNull(_columnIndexOfHealthScoreDelta)) {
            _tmpHealthScoreDelta = null
          } else {
            _tmpHealthScoreDelta = _stmt.getLong(_columnIndexOfHealthScoreDelta).toInt()
          }
          val _tmpMarketScoreDelta: Int?
          if (_stmt.isNull(_columnIndexOfMarketScoreDelta)) {
            _tmpMarketScoreDelta = null
          } else {
            _tmpMarketScoreDelta = _stmt.getLong(_columnIndexOfMarketScoreDelta).toInt()
          }
          val _tmpRecordedBy: String?
          if (_stmt.isNull(_columnIndexOfRecordedBy)) {
            _tmpRecordedBy = null
          } else {
            _tmpRecordedBy = _stmt.getText(_columnIndexOfRecordedBy)
          }
          val _tmpIsVerified: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsVerified).toInt()
          _tmpIsVerified = _tmp != 0
          val _tmpVerifiedBy: String?
          if (_stmt.isNull(_columnIndexOfVerifiedBy)) {
            _tmpVerifiedBy = null
          } else {
            _tmpVerifiedBy = _stmt.getText(_columnIndexOfVerifiedBy)
          }
          val _tmpMediaUrlsJson: String?
          if (_stmt.isNull(_columnIndexOfMediaUrlsJson)) {
            _tmpMediaUrlsJson = null
          } else {
            _tmpMediaUrlsJson = _stmt.getText(_columnIndexOfMediaUrlsJson)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
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
              BirdEventEntity(_tmpEventId,_tmpBirdId,_tmpOwnerId,_tmpEventType,_tmpEventTitle,_tmpEventDescription,_tmpEventDate,_tmpAgeDaysAtEvent,_tmpLifecycleStageAtEvent,_tmpNumericValue,_tmpNumericValue2,_tmpStringValue,_tmpDataJson,_tmpMorphologyScoreDelta,_tmpGeneticsScoreDelta,_tmpPerformanceScoreDelta,_tmpHealthScoreDelta,_tmpMarketScoreDelta,_tmpRecordedBy,_tmpIsVerified,_tmpVerifiedBy,_tmpMediaUrlsJson,_tmpCreatedAt,_tmpDirty,_tmpSyncedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getLatestEvent(birdId: String, eventType: String): BirdEventEntity? {
    val _sql: String =
        "SELECT * FROM bird_events WHERE birdId = ? AND eventType = ? ORDER BY eventDate DESC LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, birdId)
        _argIndex = 2
        _stmt.bindText(_argIndex, eventType)
        val _columnIndexOfEventId: Int = getColumnIndexOrThrow(_stmt, "eventId")
        val _columnIndexOfBirdId: Int = getColumnIndexOrThrow(_stmt, "birdId")
        val _columnIndexOfOwnerId: Int = getColumnIndexOrThrow(_stmt, "ownerId")
        val _columnIndexOfEventType: Int = getColumnIndexOrThrow(_stmt, "eventType")
        val _columnIndexOfEventTitle: Int = getColumnIndexOrThrow(_stmt, "eventTitle")
        val _columnIndexOfEventDescription: Int = getColumnIndexOrThrow(_stmt, "eventDescription")
        val _columnIndexOfEventDate: Int = getColumnIndexOrThrow(_stmt, "eventDate")
        val _columnIndexOfAgeDaysAtEvent: Int = getColumnIndexOrThrow(_stmt, "ageDaysAtEvent")
        val _columnIndexOfLifecycleStageAtEvent: Int = getColumnIndexOrThrow(_stmt,
            "lifecycleStageAtEvent")
        val _columnIndexOfNumericValue: Int = getColumnIndexOrThrow(_stmt, "numericValue")
        val _columnIndexOfNumericValue2: Int = getColumnIndexOrThrow(_stmt, "numericValue2")
        val _columnIndexOfStringValue: Int = getColumnIndexOrThrow(_stmt, "stringValue")
        val _columnIndexOfDataJson: Int = getColumnIndexOrThrow(_stmt, "dataJson")
        val _columnIndexOfMorphologyScoreDelta: Int = getColumnIndexOrThrow(_stmt,
            "morphologyScoreDelta")
        val _columnIndexOfGeneticsScoreDelta: Int = getColumnIndexOrThrow(_stmt,
            "geneticsScoreDelta")
        val _columnIndexOfPerformanceScoreDelta: Int = getColumnIndexOrThrow(_stmt,
            "performanceScoreDelta")
        val _columnIndexOfHealthScoreDelta: Int = getColumnIndexOrThrow(_stmt, "healthScoreDelta")
        val _columnIndexOfMarketScoreDelta: Int = getColumnIndexOrThrow(_stmt, "marketScoreDelta")
        val _columnIndexOfRecordedBy: Int = getColumnIndexOrThrow(_stmt, "recordedBy")
        val _columnIndexOfIsVerified: Int = getColumnIndexOrThrow(_stmt, "isVerified")
        val _columnIndexOfVerifiedBy: Int = getColumnIndexOrThrow(_stmt, "verifiedBy")
        val _columnIndexOfMediaUrlsJson: Int = getColumnIndexOrThrow(_stmt, "mediaUrlsJson")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _result: BirdEventEntity?
        if (_stmt.step()) {
          val _tmpEventId: String
          _tmpEventId = _stmt.getText(_columnIndexOfEventId)
          val _tmpBirdId: String
          _tmpBirdId = _stmt.getText(_columnIndexOfBirdId)
          val _tmpOwnerId: String
          _tmpOwnerId = _stmt.getText(_columnIndexOfOwnerId)
          val _tmpEventType: String
          _tmpEventType = _stmt.getText(_columnIndexOfEventType)
          val _tmpEventTitle: String
          _tmpEventTitle = _stmt.getText(_columnIndexOfEventTitle)
          val _tmpEventDescription: String?
          if (_stmt.isNull(_columnIndexOfEventDescription)) {
            _tmpEventDescription = null
          } else {
            _tmpEventDescription = _stmt.getText(_columnIndexOfEventDescription)
          }
          val _tmpEventDate: Long
          _tmpEventDate = _stmt.getLong(_columnIndexOfEventDate)
          val _tmpAgeDaysAtEvent: Int?
          if (_stmt.isNull(_columnIndexOfAgeDaysAtEvent)) {
            _tmpAgeDaysAtEvent = null
          } else {
            _tmpAgeDaysAtEvent = _stmt.getLong(_columnIndexOfAgeDaysAtEvent).toInt()
          }
          val _tmpLifecycleStageAtEvent: String?
          if (_stmt.isNull(_columnIndexOfLifecycleStageAtEvent)) {
            _tmpLifecycleStageAtEvent = null
          } else {
            _tmpLifecycleStageAtEvent = _stmt.getText(_columnIndexOfLifecycleStageAtEvent)
          }
          val _tmpNumericValue: Double?
          if (_stmt.isNull(_columnIndexOfNumericValue)) {
            _tmpNumericValue = null
          } else {
            _tmpNumericValue = _stmt.getDouble(_columnIndexOfNumericValue)
          }
          val _tmpNumericValue2: Double?
          if (_stmt.isNull(_columnIndexOfNumericValue2)) {
            _tmpNumericValue2 = null
          } else {
            _tmpNumericValue2 = _stmt.getDouble(_columnIndexOfNumericValue2)
          }
          val _tmpStringValue: String?
          if (_stmt.isNull(_columnIndexOfStringValue)) {
            _tmpStringValue = null
          } else {
            _tmpStringValue = _stmt.getText(_columnIndexOfStringValue)
          }
          val _tmpDataJson: String?
          if (_stmt.isNull(_columnIndexOfDataJson)) {
            _tmpDataJson = null
          } else {
            _tmpDataJson = _stmt.getText(_columnIndexOfDataJson)
          }
          val _tmpMorphologyScoreDelta: Int?
          if (_stmt.isNull(_columnIndexOfMorphologyScoreDelta)) {
            _tmpMorphologyScoreDelta = null
          } else {
            _tmpMorphologyScoreDelta = _stmt.getLong(_columnIndexOfMorphologyScoreDelta).toInt()
          }
          val _tmpGeneticsScoreDelta: Int?
          if (_stmt.isNull(_columnIndexOfGeneticsScoreDelta)) {
            _tmpGeneticsScoreDelta = null
          } else {
            _tmpGeneticsScoreDelta = _stmt.getLong(_columnIndexOfGeneticsScoreDelta).toInt()
          }
          val _tmpPerformanceScoreDelta: Int?
          if (_stmt.isNull(_columnIndexOfPerformanceScoreDelta)) {
            _tmpPerformanceScoreDelta = null
          } else {
            _tmpPerformanceScoreDelta = _stmt.getLong(_columnIndexOfPerformanceScoreDelta).toInt()
          }
          val _tmpHealthScoreDelta: Int?
          if (_stmt.isNull(_columnIndexOfHealthScoreDelta)) {
            _tmpHealthScoreDelta = null
          } else {
            _tmpHealthScoreDelta = _stmt.getLong(_columnIndexOfHealthScoreDelta).toInt()
          }
          val _tmpMarketScoreDelta: Int?
          if (_stmt.isNull(_columnIndexOfMarketScoreDelta)) {
            _tmpMarketScoreDelta = null
          } else {
            _tmpMarketScoreDelta = _stmt.getLong(_columnIndexOfMarketScoreDelta).toInt()
          }
          val _tmpRecordedBy: String?
          if (_stmt.isNull(_columnIndexOfRecordedBy)) {
            _tmpRecordedBy = null
          } else {
            _tmpRecordedBy = _stmt.getText(_columnIndexOfRecordedBy)
          }
          val _tmpIsVerified: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsVerified).toInt()
          _tmpIsVerified = _tmp != 0
          val _tmpVerifiedBy: String?
          if (_stmt.isNull(_columnIndexOfVerifiedBy)) {
            _tmpVerifiedBy = null
          } else {
            _tmpVerifiedBy = _stmt.getText(_columnIndexOfVerifiedBy)
          }
          val _tmpMediaUrlsJson: String?
          if (_stmt.isNull(_columnIndexOfMediaUrlsJson)) {
            _tmpMediaUrlsJson = null
          } else {
            _tmpMediaUrlsJson = _stmt.getText(_columnIndexOfMediaUrlsJson)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
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
          _result =
              BirdEventEntity(_tmpEventId,_tmpBirdId,_tmpOwnerId,_tmpEventType,_tmpEventTitle,_tmpEventDescription,_tmpEventDate,_tmpAgeDaysAtEvent,_tmpLifecycleStageAtEvent,_tmpNumericValue,_tmpNumericValue2,_tmpStringValue,_tmpDataJson,_tmpMorphologyScoreDelta,_tmpGeneticsScoreDelta,_tmpPerformanceScoreDelta,_tmpHealthScoreDelta,_tmpMarketScoreDelta,_tmpRecordedBy,_tmpIsVerified,_tmpVerifiedBy,_tmpMediaUrlsJson,_tmpCreatedAt,_tmpDirty,_tmpSyncedAt)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getRecentEvents(birdId: String, limit: Int): List<BirdEventEntity> {
    val _sql: String = "SELECT * FROM bird_events WHERE birdId = ? ORDER BY eventDate DESC LIMIT ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, birdId)
        _argIndex = 2
        _stmt.bindLong(_argIndex, limit.toLong())
        val _columnIndexOfEventId: Int = getColumnIndexOrThrow(_stmt, "eventId")
        val _columnIndexOfBirdId: Int = getColumnIndexOrThrow(_stmt, "birdId")
        val _columnIndexOfOwnerId: Int = getColumnIndexOrThrow(_stmt, "ownerId")
        val _columnIndexOfEventType: Int = getColumnIndexOrThrow(_stmt, "eventType")
        val _columnIndexOfEventTitle: Int = getColumnIndexOrThrow(_stmt, "eventTitle")
        val _columnIndexOfEventDescription: Int = getColumnIndexOrThrow(_stmt, "eventDescription")
        val _columnIndexOfEventDate: Int = getColumnIndexOrThrow(_stmt, "eventDate")
        val _columnIndexOfAgeDaysAtEvent: Int = getColumnIndexOrThrow(_stmt, "ageDaysAtEvent")
        val _columnIndexOfLifecycleStageAtEvent: Int = getColumnIndexOrThrow(_stmt,
            "lifecycleStageAtEvent")
        val _columnIndexOfNumericValue: Int = getColumnIndexOrThrow(_stmt, "numericValue")
        val _columnIndexOfNumericValue2: Int = getColumnIndexOrThrow(_stmt, "numericValue2")
        val _columnIndexOfStringValue: Int = getColumnIndexOrThrow(_stmt, "stringValue")
        val _columnIndexOfDataJson: Int = getColumnIndexOrThrow(_stmt, "dataJson")
        val _columnIndexOfMorphologyScoreDelta: Int = getColumnIndexOrThrow(_stmt,
            "morphologyScoreDelta")
        val _columnIndexOfGeneticsScoreDelta: Int = getColumnIndexOrThrow(_stmt,
            "geneticsScoreDelta")
        val _columnIndexOfPerformanceScoreDelta: Int = getColumnIndexOrThrow(_stmt,
            "performanceScoreDelta")
        val _columnIndexOfHealthScoreDelta: Int = getColumnIndexOrThrow(_stmt, "healthScoreDelta")
        val _columnIndexOfMarketScoreDelta: Int = getColumnIndexOrThrow(_stmt, "marketScoreDelta")
        val _columnIndexOfRecordedBy: Int = getColumnIndexOrThrow(_stmt, "recordedBy")
        val _columnIndexOfIsVerified: Int = getColumnIndexOrThrow(_stmt, "isVerified")
        val _columnIndexOfVerifiedBy: Int = getColumnIndexOrThrow(_stmt, "verifiedBy")
        val _columnIndexOfMediaUrlsJson: Int = getColumnIndexOrThrow(_stmt, "mediaUrlsJson")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _result: MutableList<BirdEventEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: BirdEventEntity
          val _tmpEventId: String
          _tmpEventId = _stmt.getText(_columnIndexOfEventId)
          val _tmpBirdId: String
          _tmpBirdId = _stmt.getText(_columnIndexOfBirdId)
          val _tmpOwnerId: String
          _tmpOwnerId = _stmt.getText(_columnIndexOfOwnerId)
          val _tmpEventType: String
          _tmpEventType = _stmt.getText(_columnIndexOfEventType)
          val _tmpEventTitle: String
          _tmpEventTitle = _stmt.getText(_columnIndexOfEventTitle)
          val _tmpEventDescription: String?
          if (_stmt.isNull(_columnIndexOfEventDescription)) {
            _tmpEventDescription = null
          } else {
            _tmpEventDescription = _stmt.getText(_columnIndexOfEventDescription)
          }
          val _tmpEventDate: Long
          _tmpEventDate = _stmt.getLong(_columnIndexOfEventDate)
          val _tmpAgeDaysAtEvent: Int?
          if (_stmt.isNull(_columnIndexOfAgeDaysAtEvent)) {
            _tmpAgeDaysAtEvent = null
          } else {
            _tmpAgeDaysAtEvent = _stmt.getLong(_columnIndexOfAgeDaysAtEvent).toInt()
          }
          val _tmpLifecycleStageAtEvent: String?
          if (_stmt.isNull(_columnIndexOfLifecycleStageAtEvent)) {
            _tmpLifecycleStageAtEvent = null
          } else {
            _tmpLifecycleStageAtEvent = _stmt.getText(_columnIndexOfLifecycleStageAtEvent)
          }
          val _tmpNumericValue: Double?
          if (_stmt.isNull(_columnIndexOfNumericValue)) {
            _tmpNumericValue = null
          } else {
            _tmpNumericValue = _stmt.getDouble(_columnIndexOfNumericValue)
          }
          val _tmpNumericValue2: Double?
          if (_stmt.isNull(_columnIndexOfNumericValue2)) {
            _tmpNumericValue2 = null
          } else {
            _tmpNumericValue2 = _stmt.getDouble(_columnIndexOfNumericValue2)
          }
          val _tmpStringValue: String?
          if (_stmt.isNull(_columnIndexOfStringValue)) {
            _tmpStringValue = null
          } else {
            _tmpStringValue = _stmt.getText(_columnIndexOfStringValue)
          }
          val _tmpDataJson: String?
          if (_stmt.isNull(_columnIndexOfDataJson)) {
            _tmpDataJson = null
          } else {
            _tmpDataJson = _stmt.getText(_columnIndexOfDataJson)
          }
          val _tmpMorphologyScoreDelta: Int?
          if (_stmt.isNull(_columnIndexOfMorphologyScoreDelta)) {
            _tmpMorphologyScoreDelta = null
          } else {
            _tmpMorphologyScoreDelta = _stmt.getLong(_columnIndexOfMorphologyScoreDelta).toInt()
          }
          val _tmpGeneticsScoreDelta: Int?
          if (_stmt.isNull(_columnIndexOfGeneticsScoreDelta)) {
            _tmpGeneticsScoreDelta = null
          } else {
            _tmpGeneticsScoreDelta = _stmt.getLong(_columnIndexOfGeneticsScoreDelta).toInt()
          }
          val _tmpPerformanceScoreDelta: Int?
          if (_stmt.isNull(_columnIndexOfPerformanceScoreDelta)) {
            _tmpPerformanceScoreDelta = null
          } else {
            _tmpPerformanceScoreDelta = _stmt.getLong(_columnIndexOfPerformanceScoreDelta).toInt()
          }
          val _tmpHealthScoreDelta: Int?
          if (_stmt.isNull(_columnIndexOfHealthScoreDelta)) {
            _tmpHealthScoreDelta = null
          } else {
            _tmpHealthScoreDelta = _stmt.getLong(_columnIndexOfHealthScoreDelta).toInt()
          }
          val _tmpMarketScoreDelta: Int?
          if (_stmt.isNull(_columnIndexOfMarketScoreDelta)) {
            _tmpMarketScoreDelta = null
          } else {
            _tmpMarketScoreDelta = _stmt.getLong(_columnIndexOfMarketScoreDelta).toInt()
          }
          val _tmpRecordedBy: String?
          if (_stmt.isNull(_columnIndexOfRecordedBy)) {
            _tmpRecordedBy = null
          } else {
            _tmpRecordedBy = _stmt.getText(_columnIndexOfRecordedBy)
          }
          val _tmpIsVerified: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsVerified).toInt()
          _tmpIsVerified = _tmp != 0
          val _tmpVerifiedBy: String?
          if (_stmt.isNull(_columnIndexOfVerifiedBy)) {
            _tmpVerifiedBy = null
          } else {
            _tmpVerifiedBy = _stmt.getText(_columnIndexOfVerifiedBy)
          }
          val _tmpMediaUrlsJson: String?
          if (_stmt.isNull(_columnIndexOfMediaUrlsJson)) {
            _tmpMediaUrlsJson = null
          } else {
            _tmpMediaUrlsJson = _stmt.getText(_columnIndexOfMediaUrlsJson)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
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
              BirdEventEntity(_tmpEventId,_tmpBirdId,_tmpOwnerId,_tmpEventType,_tmpEventTitle,_tmpEventDescription,_tmpEventDate,_tmpAgeDaysAtEvent,_tmpLifecycleStageAtEvent,_tmpNumericValue,_tmpNumericValue2,_tmpStringValue,_tmpDataJson,_tmpMorphologyScoreDelta,_tmpGeneticsScoreDelta,_tmpPerformanceScoreDelta,_tmpHealthScoreDelta,_tmpMarketScoreDelta,_tmpRecordedBy,_tmpIsVerified,_tmpVerifiedBy,_tmpMediaUrlsJson,_tmpCreatedAt,_tmpDirty,_tmpSyncedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getEventsForOwner(ownerId: String, limit: Int): Flow<List<BirdEventEntity>> {
    val _sql: String = "SELECT * FROM bird_events WHERE ownerId = ? ORDER BY eventDate DESC LIMIT ?"
    return createFlow(__db, false, arrayOf("bird_events")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, ownerId)
        _argIndex = 2
        _stmt.bindLong(_argIndex, limit.toLong())
        val _columnIndexOfEventId: Int = getColumnIndexOrThrow(_stmt, "eventId")
        val _columnIndexOfBirdId: Int = getColumnIndexOrThrow(_stmt, "birdId")
        val _columnIndexOfOwnerId: Int = getColumnIndexOrThrow(_stmt, "ownerId")
        val _columnIndexOfEventType: Int = getColumnIndexOrThrow(_stmt, "eventType")
        val _columnIndexOfEventTitle: Int = getColumnIndexOrThrow(_stmt, "eventTitle")
        val _columnIndexOfEventDescription: Int = getColumnIndexOrThrow(_stmt, "eventDescription")
        val _columnIndexOfEventDate: Int = getColumnIndexOrThrow(_stmt, "eventDate")
        val _columnIndexOfAgeDaysAtEvent: Int = getColumnIndexOrThrow(_stmt, "ageDaysAtEvent")
        val _columnIndexOfLifecycleStageAtEvent: Int = getColumnIndexOrThrow(_stmt,
            "lifecycleStageAtEvent")
        val _columnIndexOfNumericValue: Int = getColumnIndexOrThrow(_stmt, "numericValue")
        val _columnIndexOfNumericValue2: Int = getColumnIndexOrThrow(_stmt, "numericValue2")
        val _columnIndexOfStringValue: Int = getColumnIndexOrThrow(_stmt, "stringValue")
        val _columnIndexOfDataJson: Int = getColumnIndexOrThrow(_stmt, "dataJson")
        val _columnIndexOfMorphologyScoreDelta: Int = getColumnIndexOrThrow(_stmt,
            "morphologyScoreDelta")
        val _columnIndexOfGeneticsScoreDelta: Int = getColumnIndexOrThrow(_stmt,
            "geneticsScoreDelta")
        val _columnIndexOfPerformanceScoreDelta: Int = getColumnIndexOrThrow(_stmt,
            "performanceScoreDelta")
        val _columnIndexOfHealthScoreDelta: Int = getColumnIndexOrThrow(_stmt, "healthScoreDelta")
        val _columnIndexOfMarketScoreDelta: Int = getColumnIndexOrThrow(_stmt, "marketScoreDelta")
        val _columnIndexOfRecordedBy: Int = getColumnIndexOrThrow(_stmt, "recordedBy")
        val _columnIndexOfIsVerified: Int = getColumnIndexOrThrow(_stmt, "isVerified")
        val _columnIndexOfVerifiedBy: Int = getColumnIndexOrThrow(_stmt, "verifiedBy")
        val _columnIndexOfMediaUrlsJson: Int = getColumnIndexOrThrow(_stmt, "mediaUrlsJson")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _result: MutableList<BirdEventEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: BirdEventEntity
          val _tmpEventId: String
          _tmpEventId = _stmt.getText(_columnIndexOfEventId)
          val _tmpBirdId: String
          _tmpBirdId = _stmt.getText(_columnIndexOfBirdId)
          val _tmpOwnerId: String
          _tmpOwnerId = _stmt.getText(_columnIndexOfOwnerId)
          val _tmpEventType: String
          _tmpEventType = _stmt.getText(_columnIndexOfEventType)
          val _tmpEventTitle: String
          _tmpEventTitle = _stmt.getText(_columnIndexOfEventTitle)
          val _tmpEventDescription: String?
          if (_stmt.isNull(_columnIndexOfEventDescription)) {
            _tmpEventDescription = null
          } else {
            _tmpEventDescription = _stmt.getText(_columnIndexOfEventDescription)
          }
          val _tmpEventDate: Long
          _tmpEventDate = _stmt.getLong(_columnIndexOfEventDate)
          val _tmpAgeDaysAtEvent: Int?
          if (_stmt.isNull(_columnIndexOfAgeDaysAtEvent)) {
            _tmpAgeDaysAtEvent = null
          } else {
            _tmpAgeDaysAtEvent = _stmt.getLong(_columnIndexOfAgeDaysAtEvent).toInt()
          }
          val _tmpLifecycleStageAtEvent: String?
          if (_stmt.isNull(_columnIndexOfLifecycleStageAtEvent)) {
            _tmpLifecycleStageAtEvent = null
          } else {
            _tmpLifecycleStageAtEvent = _stmt.getText(_columnIndexOfLifecycleStageAtEvent)
          }
          val _tmpNumericValue: Double?
          if (_stmt.isNull(_columnIndexOfNumericValue)) {
            _tmpNumericValue = null
          } else {
            _tmpNumericValue = _stmt.getDouble(_columnIndexOfNumericValue)
          }
          val _tmpNumericValue2: Double?
          if (_stmt.isNull(_columnIndexOfNumericValue2)) {
            _tmpNumericValue2 = null
          } else {
            _tmpNumericValue2 = _stmt.getDouble(_columnIndexOfNumericValue2)
          }
          val _tmpStringValue: String?
          if (_stmt.isNull(_columnIndexOfStringValue)) {
            _tmpStringValue = null
          } else {
            _tmpStringValue = _stmt.getText(_columnIndexOfStringValue)
          }
          val _tmpDataJson: String?
          if (_stmt.isNull(_columnIndexOfDataJson)) {
            _tmpDataJson = null
          } else {
            _tmpDataJson = _stmt.getText(_columnIndexOfDataJson)
          }
          val _tmpMorphologyScoreDelta: Int?
          if (_stmt.isNull(_columnIndexOfMorphologyScoreDelta)) {
            _tmpMorphologyScoreDelta = null
          } else {
            _tmpMorphologyScoreDelta = _stmt.getLong(_columnIndexOfMorphologyScoreDelta).toInt()
          }
          val _tmpGeneticsScoreDelta: Int?
          if (_stmt.isNull(_columnIndexOfGeneticsScoreDelta)) {
            _tmpGeneticsScoreDelta = null
          } else {
            _tmpGeneticsScoreDelta = _stmt.getLong(_columnIndexOfGeneticsScoreDelta).toInt()
          }
          val _tmpPerformanceScoreDelta: Int?
          if (_stmt.isNull(_columnIndexOfPerformanceScoreDelta)) {
            _tmpPerformanceScoreDelta = null
          } else {
            _tmpPerformanceScoreDelta = _stmt.getLong(_columnIndexOfPerformanceScoreDelta).toInt()
          }
          val _tmpHealthScoreDelta: Int?
          if (_stmt.isNull(_columnIndexOfHealthScoreDelta)) {
            _tmpHealthScoreDelta = null
          } else {
            _tmpHealthScoreDelta = _stmt.getLong(_columnIndexOfHealthScoreDelta).toInt()
          }
          val _tmpMarketScoreDelta: Int?
          if (_stmt.isNull(_columnIndexOfMarketScoreDelta)) {
            _tmpMarketScoreDelta = null
          } else {
            _tmpMarketScoreDelta = _stmt.getLong(_columnIndexOfMarketScoreDelta).toInt()
          }
          val _tmpRecordedBy: String?
          if (_stmt.isNull(_columnIndexOfRecordedBy)) {
            _tmpRecordedBy = null
          } else {
            _tmpRecordedBy = _stmt.getText(_columnIndexOfRecordedBy)
          }
          val _tmpIsVerified: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsVerified).toInt()
          _tmpIsVerified = _tmp != 0
          val _tmpVerifiedBy: String?
          if (_stmt.isNull(_columnIndexOfVerifiedBy)) {
            _tmpVerifiedBy = null
          } else {
            _tmpVerifiedBy = _stmt.getText(_columnIndexOfVerifiedBy)
          }
          val _tmpMediaUrlsJson: String?
          if (_stmt.isNull(_columnIndexOfMediaUrlsJson)) {
            _tmpMediaUrlsJson = null
          } else {
            _tmpMediaUrlsJson = _stmt.getText(_columnIndexOfMediaUrlsJson)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
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
              BirdEventEntity(_tmpEventId,_tmpBirdId,_tmpOwnerId,_tmpEventType,_tmpEventTitle,_tmpEventDescription,_tmpEventDate,_tmpAgeDaysAtEvent,_tmpLifecycleStageAtEvent,_tmpNumericValue,_tmpNumericValue2,_tmpStringValue,_tmpDataJson,_tmpMorphologyScoreDelta,_tmpGeneticsScoreDelta,_tmpPerformanceScoreDelta,_tmpHealthScoreDelta,_tmpMarketScoreDelta,_tmpRecordedBy,_tmpIsVerified,_tmpVerifiedBy,_tmpMediaUrlsJson,_tmpCreatedAt,_tmpDirty,_tmpSyncedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getFightHistory(birdId: String): Flow<List<BirdEventEntity>> {
    val _sql: String = """
        |
        |        SELECT * FROM bird_events 
        |        WHERE birdId = ? 
        |        AND eventType IN ('FIGHT_WIN', 'FIGHT_LOSS', 'FIGHT_DRAW') 
        |        ORDER BY eventDate DESC
        |    
        """.trimMargin()
    return createFlow(__db, false, arrayOf("bird_events")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, birdId)
        val _columnIndexOfEventId: Int = getColumnIndexOrThrow(_stmt, "eventId")
        val _columnIndexOfBirdId: Int = getColumnIndexOrThrow(_stmt, "birdId")
        val _columnIndexOfOwnerId: Int = getColumnIndexOrThrow(_stmt, "ownerId")
        val _columnIndexOfEventType: Int = getColumnIndexOrThrow(_stmt, "eventType")
        val _columnIndexOfEventTitle: Int = getColumnIndexOrThrow(_stmt, "eventTitle")
        val _columnIndexOfEventDescription: Int = getColumnIndexOrThrow(_stmt, "eventDescription")
        val _columnIndexOfEventDate: Int = getColumnIndexOrThrow(_stmt, "eventDate")
        val _columnIndexOfAgeDaysAtEvent: Int = getColumnIndexOrThrow(_stmt, "ageDaysAtEvent")
        val _columnIndexOfLifecycleStageAtEvent: Int = getColumnIndexOrThrow(_stmt,
            "lifecycleStageAtEvent")
        val _columnIndexOfNumericValue: Int = getColumnIndexOrThrow(_stmt, "numericValue")
        val _columnIndexOfNumericValue2: Int = getColumnIndexOrThrow(_stmt, "numericValue2")
        val _columnIndexOfStringValue: Int = getColumnIndexOrThrow(_stmt, "stringValue")
        val _columnIndexOfDataJson: Int = getColumnIndexOrThrow(_stmt, "dataJson")
        val _columnIndexOfMorphologyScoreDelta: Int = getColumnIndexOrThrow(_stmt,
            "morphologyScoreDelta")
        val _columnIndexOfGeneticsScoreDelta: Int = getColumnIndexOrThrow(_stmt,
            "geneticsScoreDelta")
        val _columnIndexOfPerformanceScoreDelta: Int = getColumnIndexOrThrow(_stmt,
            "performanceScoreDelta")
        val _columnIndexOfHealthScoreDelta: Int = getColumnIndexOrThrow(_stmt, "healthScoreDelta")
        val _columnIndexOfMarketScoreDelta: Int = getColumnIndexOrThrow(_stmt, "marketScoreDelta")
        val _columnIndexOfRecordedBy: Int = getColumnIndexOrThrow(_stmt, "recordedBy")
        val _columnIndexOfIsVerified: Int = getColumnIndexOrThrow(_stmt, "isVerified")
        val _columnIndexOfVerifiedBy: Int = getColumnIndexOrThrow(_stmt, "verifiedBy")
        val _columnIndexOfMediaUrlsJson: Int = getColumnIndexOrThrow(_stmt, "mediaUrlsJson")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _result: MutableList<BirdEventEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: BirdEventEntity
          val _tmpEventId: String
          _tmpEventId = _stmt.getText(_columnIndexOfEventId)
          val _tmpBirdId: String
          _tmpBirdId = _stmt.getText(_columnIndexOfBirdId)
          val _tmpOwnerId: String
          _tmpOwnerId = _stmt.getText(_columnIndexOfOwnerId)
          val _tmpEventType: String
          _tmpEventType = _stmt.getText(_columnIndexOfEventType)
          val _tmpEventTitle: String
          _tmpEventTitle = _stmt.getText(_columnIndexOfEventTitle)
          val _tmpEventDescription: String?
          if (_stmt.isNull(_columnIndexOfEventDescription)) {
            _tmpEventDescription = null
          } else {
            _tmpEventDescription = _stmt.getText(_columnIndexOfEventDescription)
          }
          val _tmpEventDate: Long
          _tmpEventDate = _stmt.getLong(_columnIndexOfEventDate)
          val _tmpAgeDaysAtEvent: Int?
          if (_stmt.isNull(_columnIndexOfAgeDaysAtEvent)) {
            _tmpAgeDaysAtEvent = null
          } else {
            _tmpAgeDaysAtEvent = _stmt.getLong(_columnIndexOfAgeDaysAtEvent).toInt()
          }
          val _tmpLifecycleStageAtEvent: String?
          if (_stmt.isNull(_columnIndexOfLifecycleStageAtEvent)) {
            _tmpLifecycleStageAtEvent = null
          } else {
            _tmpLifecycleStageAtEvent = _stmt.getText(_columnIndexOfLifecycleStageAtEvent)
          }
          val _tmpNumericValue: Double?
          if (_stmt.isNull(_columnIndexOfNumericValue)) {
            _tmpNumericValue = null
          } else {
            _tmpNumericValue = _stmt.getDouble(_columnIndexOfNumericValue)
          }
          val _tmpNumericValue2: Double?
          if (_stmt.isNull(_columnIndexOfNumericValue2)) {
            _tmpNumericValue2 = null
          } else {
            _tmpNumericValue2 = _stmt.getDouble(_columnIndexOfNumericValue2)
          }
          val _tmpStringValue: String?
          if (_stmt.isNull(_columnIndexOfStringValue)) {
            _tmpStringValue = null
          } else {
            _tmpStringValue = _stmt.getText(_columnIndexOfStringValue)
          }
          val _tmpDataJson: String?
          if (_stmt.isNull(_columnIndexOfDataJson)) {
            _tmpDataJson = null
          } else {
            _tmpDataJson = _stmt.getText(_columnIndexOfDataJson)
          }
          val _tmpMorphologyScoreDelta: Int?
          if (_stmt.isNull(_columnIndexOfMorphologyScoreDelta)) {
            _tmpMorphologyScoreDelta = null
          } else {
            _tmpMorphologyScoreDelta = _stmt.getLong(_columnIndexOfMorphologyScoreDelta).toInt()
          }
          val _tmpGeneticsScoreDelta: Int?
          if (_stmt.isNull(_columnIndexOfGeneticsScoreDelta)) {
            _tmpGeneticsScoreDelta = null
          } else {
            _tmpGeneticsScoreDelta = _stmt.getLong(_columnIndexOfGeneticsScoreDelta).toInt()
          }
          val _tmpPerformanceScoreDelta: Int?
          if (_stmt.isNull(_columnIndexOfPerformanceScoreDelta)) {
            _tmpPerformanceScoreDelta = null
          } else {
            _tmpPerformanceScoreDelta = _stmt.getLong(_columnIndexOfPerformanceScoreDelta).toInt()
          }
          val _tmpHealthScoreDelta: Int?
          if (_stmt.isNull(_columnIndexOfHealthScoreDelta)) {
            _tmpHealthScoreDelta = null
          } else {
            _tmpHealthScoreDelta = _stmt.getLong(_columnIndexOfHealthScoreDelta).toInt()
          }
          val _tmpMarketScoreDelta: Int?
          if (_stmt.isNull(_columnIndexOfMarketScoreDelta)) {
            _tmpMarketScoreDelta = null
          } else {
            _tmpMarketScoreDelta = _stmt.getLong(_columnIndexOfMarketScoreDelta).toInt()
          }
          val _tmpRecordedBy: String?
          if (_stmt.isNull(_columnIndexOfRecordedBy)) {
            _tmpRecordedBy = null
          } else {
            _tmpRecordedBy = _stmt.getText(_columnIndexOfRecordedBy)
          }
          val _tmpIsVerified: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsVerified).toInt()
          _tmpIsVerified = _tmp != 0
          val _tmpVerifiedBy: String?
          if (_stmt.isNull(_columnIndexOfVerifiedBy)) {
            _tmpVerifiedBy = null
          } else {
            _tmpVerifiedBy = _stmt.getText(_columnIndexOfVerifiedBy)
          }
          val _tmpMediaUrlsJson: String?
          if (_stmt.isNull(_columnIndexOfMediaUrlsJson)) {
            _tmpMediaUrlsJson = null
          } else {
            _tmpMediaUrlsJson = _stmt.getText(_columnIndexOfMediaUrlsJson)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
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
              BirdEventEntity(_tmpEventId,_tmpBirdId,_tmpOwnerId,_tmpEventType,_tmpEventTitle,_tmpEventDescription,_tmpEventDate,_tmpAgeDaysAtEvent,_tmpLifecycleStageAtEvent,_tmpNumericValue,_tmpNumericValue2,_tmpStringValue,_tmpDataJson,_tmpMorphologyScoreDelta,_tmpGeneticsScoreDelta,_tmpPerformanceScoreDelta,_tmpHealthScoreDelta,_tmpMarketScoreDelta,_tmpRecordedBy,_tmpIsVerified,_tmpVerifiedBy,_tmpMediaUrlsJson,_tmpCreatedAt,_tmpDirty,_tmpSyncedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getBreedingHistory(birdId: String): Flow<List<BirdEventEntity>> {
    val _sql: String = """
        |
        |        SELECT * FROM bird_events 
        |        WHERE birdId = ? 
        |        AND eventType IN ('BREEDING_ATTEMPT', 'BREEDING_SUCCESS', 'BREEDING_FAILURE', 'CLUTCH_LAID', 'HATCHING_COMPLETE') 
        |        ORDER BY eventDate DESC
        |    
        """.trimMargin()
    return createFlow(__db, false, arrayOf("bird_events")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, birdId)
        val _columnIndexOfEventId: Int = getColumnIndexOrThrow(_stmt, "eventId")
        val _columnIndexOfBirdId: Int = getColumnIndexOrThrow(_stmt, "birdId")
        val _columnIndexOfOwnerId: Int = getColumnIndexOrThrow(_stmt, "ownerId")
        val _columnIndexOfEventType: Int = getColumnIndexOrThrow(_stmt, "eventType")
        val _columnIndexOfEventTitle: Int = getColumnIndexOrThrow(_stmt, "eventTitle")
        val _columnIndexOfEventDescription: Int = getColumnIndexOrThrow(_stmt, "eventDescription")
        val _columnIndexOfEventDate: Int = getColumnIndexOrThrow(_stmt, "eventDate")
        val _columnIndexOfAgeDaysAtEvent: Int = getColumnIndexOrThrow(_stmt, "ageDaysAtEvent")
        val _columnIndexOfLifecycleStageAtEvent: Int = getColumnIndexOrThrow(_stmt,
            "lifecycleStageAtEvent")
        val _columnIndexOfNumericValue: Int = getColumnIndexOrThrow(_stmt, "numericValue")
        val _columnIndexOfNumericValue2: Int = getColumnIndexOrThrow(_stmt, "numericValue2")
        val _columnIndexOfStringValue: Int = getColumnIndexOrThrow(_stmt, "stringValue")
        val _columnIndexOfDataJson: Int = getColumnIndexOrThrow(_stmt, "dataJson")
        val _columnIndexOfMorphologyScoreDelta: Int = getColumnIndexOrThrow(_stmt,
            "morphologyScoreDelta")
        val _columnIndexOfGeneticsScoreDelta: Int = getColumnIndexOrThrow(_stmt,
            "geneticsScoreDelta")
        val _columnIndexOfPerformanceScoreDelta: Int = getColumnIndexOrThrow(_stmt,
            "performanceScoreDelta")
        val _columnIndexOfHealthScoreDelta: Int = getColumnIndexOrThrow(_stmt, "healthScoreDelta")
        val _columnIndexOfMarketScoreDelta: Int = getColumnIndexOrThrow(_stmt, "marketScoreDelta")
        val _columnIndexOfRecordedBy: Int = getColumnIndexOrThrow(_stmt, "recordedBy")
        val _columnIndexOfIsVerified: Int = getColumnIndexOrThrow(_stmt, "isVerified")
        val _columnIndexOfVerifiedBy: Int = getColumnIndexOrThrow(_stmt, "verifiedBy")
        val _columnIndexOfMediaUrlsJson: Int = getColumnIndexOrThrow(_stmt, "mediaUrlsJson")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _result: MutableList<BirdEventEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: BirdEventEntity
          val _tmpEventId: String
          _tmpEventId = _stmt.getText(_columnIndexOfEventId)
          val _tmpBirdId: String
          _tmpBirdId = _stmt.getText(_columnIndexOfBirdId)
          val _tmpOwnerId: String
          _tmpOwnerId = _stmt.getText(_columnIndexOfOwnerId)
          val _tmpEventType: String
          _tmpEventType = _stmt.getText(_columnIndexOfEventType)
          val _tmpEventTitle: String
          _tmpEventTitle = _stmt.getText(_columnIndexOfEventTitle)
          val _tmpEventDescription: String?
          if (_stmt.isNull(_columnIndexOfEventDescription)) {
            _tmpEventDescription = null
          } else {
            _tmpEventDescription = _stmt.getText(_columnIndexOfEventDescription)
          }
          val _tmpEventDate: Long
          _tmpEventDate = _stmt.getLong(_columnIndexOfEventDate)
          val _tmpAgeDaysAtEvent: Int?
          if (_stmt.isNull(_columnIndexOfAgeDaysAtEvent)) {
            _tmpAgeDaysAtEvent = null
          } else {
            _tmpAgeDaysAtEvent = _stmt.getLong(_columnIndexOfAgeDaysAtEvent).toInt()
          }
          val _tmpLifecycleStageAtEvent: String?
          if (_stmt.isNull(_columnIndexOfLifecycleStageAtEvent)) {
            _tmpLifecycleStageAtEvent = null
          } else {
            _tmpLifecycleStageAtEvent = _stmt.getText(_columnIndexOfLifecycleStageAtEvent)
          }
          val _tmpNumericValue: Double?
          if (_stmt.isNull(_columnIndexOfNumericValue)) {
            _tmpNumericValue = null
          } else {
            _tmpNumericValue = _stmt.getDouble(_columnIndexOfNumericValue)
          }
          val _tmpNumericValue2: Double?
          if (_stmt.isNull(_columnIndexOfNumericValue2)) {
            _tmpNumericValue2 = null
          } else {
            _tmpNumericValue2 = _stmt.getDouble(_columnIndexOfNumericValue2)
          }
          val _tmpStringValue: String?
          if (_stmt.isNull(_columnIndexOfStringValue)) {
            _tmpStringValue = null
          } else {
            _tmpStringValue = _stmt.getText(_columnIndexOfStringValue)
          }
          val _tmpDataJson: String?
          if (_stmt.isNull(_columnIndexOfDataJson)) {
            _tmpDataJson = null
          } else {
            _tmpDataJson = _stmt.getText(_columnIndexOfDataJson)
          }
          val _tmpMorphologyScoreDelta: Int?
          if (_stmt.isNull(_columnIndexOfMorphologyScoreDelta)) {
            _tmpMorphologyScoreDelta = null
          } else {
            _tmpMorphologyScoreDelta = _stmt.getLong(_columnIndexOfMorphologyScoreDelta).toInt()
          }
          val _tmpGeneticsScoreDelta: Int?
          if (_stmt.isNull(_columnIndexOfGeneticsScoreDelta)) {
            _tmpGeneticsScoreDelta = null
          } else {
            _tmpGeneticsScoreDelta = _stmt.getLong(_columnIndexOfGeneticsScoreDelta).toInt()
          }
          val _tmpPerformanceScoreDelta: Int?
          if (_stmt.isNull(_columnIndexOfPerformanceScoreDelta)) {
            _tmpPerformanceScoreDelta = null
          } else {
            _tmpPerformanceScoreDelta = _stmt.getLong(_columnIndexOfPerformanceScoreDelta).toInt()
          }
          val _tmpHealthScoreDelta: Int?
          if (_stmt.isNull(_columnIndexOfHealthScoreDelta)) {
            _tmpHealthScoreDelta = null
          } else {
            _tmpHealthScoreDelta = _stmt.getLong(_columnIndexOfHealthScoreDelta).toInt()
          }
          val _tmpMarketScoreDelta: Int?
          if (_stmt.isNull(_columnIndexOfMarketScoreDelta)) {
            _tmpMarketScoreDelta = null
          } else {
            _tmpMarketScoreDelta = _stmt.getLong(_columnIndexOfMarketScoreDelta).toInt()
          }
          val _tmpRecordedBy: String?
          if (_stmt.isNull(_columnIndexOfRecordedBy)) {
            _tmpRecordedBy = null
          } else {
            _tmpRecordedBy = _stmt.getText(_columnIndexOfRecordedBy)
          }
          val _tmpIsVerified: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsVerified).toInt()
          _tmpIsVerified = _tmp != 0
          val _tmpVerifiedBy: String?
          if (_stmt.isNull(_columnIndexOfVerifiedBy)) {
            _tmpVerifiedBy = null
          } else {
            _tmpVerifiedBy = _stmt.getText(_columnIndexOfVerifiedBy)
          }
          val _tmpMediaUrlsJson: String?
          if (_stmt.isNull(_columnIndexOfMediaUrlsJson)) {
            _tmpMediaUrlsJson = null
          } else {
            _tmpMediaUrlsJson = _stmt.getText(_columnIndexOfMediaUrlsJson)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
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
              BirdEventEntity(_tmpEventId,_tmpBirdId,_tmpOwnerId,_tmpEventType,_tmpEventTitle,_tmpEventDescription,_tmpEventDate,_tmpAgeDaysAtEvent,_tmpLifecycleStageAtEvent,_tmpNumericValue,_tmpNumericValue2,_tmpStringValue,_tmpDataJson,_tmpMorphologyScoreDelta,_tmpGeneticsScoreDelta,_tmpPerformanceScoreDelta,_tmpHealthScoreDelta,_tmpMarketScoreDelta,_tmpRecordedBy,_tmpIsVerified,_tmpVerifiedBy,_tmpMediaUrlsJson,_tmpCreatedAt,_tmpDirty,_tmpSyncedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getHealthHistory(birdId: String): Flow<List<BirdEventEntity>> {
    val _sql: String = """
        |
        |        SELECT * FROM bird_events 
        |        WHERE birdId = ? 
        |        AND eventType IN ('VACCINATION', 'ILLNESS', 'INJURY', 'RECOVERY', 'DEWORMING') 
        |        ORDER BY eventDate DESC
        |    
        """.trimMargin()
    return createFlow(__db, false, arrayOf("bird_events")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, birdId)
        val _columnIndexOfEventId: Int = getColumnIndexOrThrow(_stmt, "eventId")
        val _columnIndexOfBirdId: Int = getColumnIndexOrThrow(_stmt, "birdId")
        val _columnIndexOfOwnerId: Int = getColumnIndexOrThrow(_stmt, "ownerId")
        val _columnIndexOfEventType: Int = getColumnIndexOrThrow(_stmt, "eventType")
        val _columnIndexOfEventTitle: Int = getColumnIndexOrThrow(_stmt, "eventTitle")
        val _columnIndexOfEventDescription: Int = getColumnIndexOrThrow(_stmt, "eventDescription")
        val _columnIndexOfEventDate: Int = getColumnIndexOrThrow(_stmt, "eventDate")
        val _columnIndexOfAgeDaysAtEvent: Int = getColumnIndexOrThrow(_stmt, "ageDaysAtEvent")
        val _columnIndexOfLifecycleStageAtEvent: Int = getColumnIndexOrThrow(_stmt,
            "lifecycleStageAtEvent")
        val _columnIndexOfNumericValue: Int = getColumnIndexOrThrow(_stmt, "numericValue")
        val _columnIndexOfNumericValue2: Int = getColumnIndexOrThrow(_stmt, "numericValue2")
        val _columnIndexOfStringValue: Int = getColumnIndexOrThrow(_stmt, "stringValue")
        val _columnIndexOfDataJson: Int = getColumnIndexOrThrow(_stmt, "dataJson")
        val _columnIndexOfMorphologyScoreDelta: Int = getColumnIndexOrThrow(_stmt,
            "morphologyScoreDelta")
        val _columnIndexOfGeneticsScoreDelta: Int = getColumnIndexOrThrow(_stmt,
            "geneticsScoreDelta")
        val _columnIndexOfPerformanceScoreDelta: Int = getColumnIndexOrThrow(_stmt,
            "performanceScoreDelta")
        val _columnIndexOfHealthScoreDelta: Int = getColumnIndexOrThrow(_stmt, "healthScoreDelta")
        val _columnIndexOfMarketScoreDelta: Int = getColumnIndexOrThrow(_stmt, "marketScoreDelta")
        val _columnIndexOfRecordedBy: Int = getColumnIndexOrThrow(_stmt, "recordedBy")
        val _columnIndexOfIsVerified: Int = getColumnIndexOrThrow(_stmt, "isVerified")
        val _columnIndexOfVerifiedBy: Int = getColumnIndexOrThrow(_stmt, "verifiedBy")
        val _columnIndexOfMediaUrlsJson: Int = getColumnIndexOrThrow(_stmt, "mediaUrlsJson")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _result: MutableList<BirdEventEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: BirdEventEntity
          val _tmpEventId: String
          _tmpEventId = _stmt.getText(_columnIndexOfEventId)
          val _tmpBirdId: String
          _tmpBirdId = _stmt.getText(_columnIndexOfBirdId)
          val _tmpOwnerId: String
          _tmpOwnerId = _stmt.getText(_columnIndexOfOwnerId)
          val _tmpEventType: String
          _tmpEventType = _stmt.getText(_columnIndexOfEventType)
          val _tmpEventTitle: String
          _tmpEventTitle = _stmt.getText(_columnIndexOfEventTitle)
          val _tmpEventDescription: String?
          if (_stmt.isNull(_columnIndexOfEventDescription)) {
            _tmpEventDescription = null
          } else {
            _tmpEventDescription = _stmt.getText(_columnIndexOfEventDescription)
          }
          val _tmpEventDate: Long
          _tmpEventDate = _stmt.getLong(_columnIndexOfEventDate)
          val _tmpAgeDaysAtEvent: Int?
          if (_stmt.isNull(_columnIndexOfAgeDaysAtEvent)) {
            _tmpAgeDaysAtEvent = null
          } else {
            _tmpAgeDaysAtEvent = _stmt.getLong(_columnIndexOfAgeDaysAtEvent).toInt()
          }
          val _tmpLifecycleStageAtEvent: String?
          if (_stmt.isNull(_columnIndexOfLifecycleStageAtEvent)) {
            _tmpLifecycleStageAtEvent = null
          } else {
            _tmpLifecycleStageAtEvent = _stmt.getText(_columnIndexOfLifecycleStageAtEvent)
          }
          val _tmpNumericValue: Double?
          if (_stmt.isNull(_columnIndexOfNumericValue)) {
            _tmpNumericValue = null
          } else {
            _tmpNumericValue = _stmt.getDouble(_columnIndexOfNumericValue)
          }
          val _tmpNumericValue2: Double?
          if (_stmt.isNull(_columnIndexOfNumericValue2)) {
            _tmpNumericValue2 = null
          } else {
            _tmpNumericValue2 = _stmt.getDouble(_columnIndexOfNumericValue2)
          }
          val _tmpStringValue: String?
          if (_stmt.isNull(_columnIndexOfStringValue)) {
            _tmpStringValue = null
          } else {
            _tmpStringValue = _stmt.getText(_columnIndexOfStringValue)
          }
          val _tmpDataJson: String?
          if (_stmt.isNull(_columnIndexOfDataJson)) {
            _tmpDataJson = null
          } else {
            _tmpDataJson = _stmt.getText(_columnIndexOfDataJson)
          }
          val _tmpMorphologyScoreDelta: Int?
          if (_stmt.isNull(_columnIndexOfMorphologyScoreDelta)) {
            _tmpMorphologyScoreDelta = null
          } else {
            _tmpMorphologyScoreDelta = _stmt.getLong(_columnIndexOfMorphologyScoreDelta).toInt()
          }
          val _tmpGeneticsScoreDelta: Int?
          if (_stmt.isNull(_columnIndexOfGeneticsScoreDelta)) {
            _tmpGeneticsScoreDelta = null
          } else {
            _tmpGeneticsScoreDelta = _stmt.getLong(_columnIndexOfGeneticsScoreDelta).toInt()
          }
          val _tmpPerformanceScoreDelta: Int?
          if (_stmt.isNull(_columnIndexOfPerformanceScoreDelta)) {
            _tmpPerformanceScoreDelta = null
          } else {
            _tmpPerformanceScoreDelta = _stmt.getLong(_columnIndexOfPerformanceScoreDelta).toInt()
          }
          val _tmpHealthScoreDelta: Int?
          if (_stmt.isNull(_columnIndexOfHealthScoreDelta)) {
            _tmpHealthScoreDelta = null
          } else {
            _tmpHealthScoreDelta = _stmt.getLong(_columnIndexOfHealthScoreDelta).toInt()
          }
          val _tmpMarketScoreDelta: Int?
          if (_stmt.isNull(_columnIndexOfMarketScoreDelta)) {
            _tmpMarketScoreDelta = null
          } else {
            _tmpMarketScoreDelta = _stmt.getLong(_columnIndexOfMarketScoreDelta).toInt()
          }
          val _tmpRecordedBy: String?
          if (_stmt.isNull(_columnIndexOfRecordedBy)) {
            _tmpRecordedBy = null
          } else {
            _tmpRecordedBy = _stmt.getText(_columnIndexOfRecordedBy)
          }
          val _tmpIsVerified: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsVerified).toInt()
          _tmpIsVerified = _tmp != 0
          val _tmpVerifiedBy: String?
          if (_stmt.isNull(_columnIndexOfVerifiedBy)) {
            _tmpVerifiedBy = null
          } else {
            _tmpVerifiedBy = _stmt.getText(_columnIndexOfVerifiedBy)
          }
          val _tmpMediaUrlsJson: String?
          if (_stmt.isNull(_columnIndexOfMediaUrlsJson)) {
            _tmpMediaUrlsJson = null
          } else {
            _tmpMediaUrlsJson = _stmt.getText(_columnIndexOfMediaUrlsJson)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
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
              BirdEventEntity(_tmpEventId,_tmpBirdId,_tmpOwnerId,_tmpEventType,_tmpEventTitle,_tmpEventDescription,_tmpEventDate,_tmpAgeDaysAtEvent,_tmpLifecycleStageAtEvent,_tmpNumericValue,_tmpNumericValue2,_tmpStringValue,_tmpDataJson,_tmpMorphologyScoreDelta,_tmpGeneticsScoreDelta,_tmpPerformanceScoreDelta,_tmpHealthScoreDelta,_tmpMarketScoreDelta,_tmpRecordedBy,_tmpIsVerified,_tmpVerifiedBy,_tmpMediaUrlsJson,_tmpCreatedAt,_tmpDirty,_tmpSyncedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getWeightHistory(birdId: String): Flow<List<BirdEventEntity>> {
    val _sql: String = """
        |
        |        SELECT * FROM bird_events 
        |        WHERE birdId = ? AND eventType = 'WEIGHT_RECORDED' 
        |        ORDER BY eventDate ASC
        |    
        """.trimMargin()
    return createFlow(__db, false, arrayOf("bird_events")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, birdId)
        val _columnIndexOfEventId: Int = getColumnIndexOrThrow(_stmt, "eventId")
        val _columnIndexOfBirdId: Int = getColumnIndexOrThrow(_stmt, "birdId")
        val _columnIndexOfOwnerId: Int = getColumnIndexOrThrow(_stmt, "ownerId")
        val _columnIndexOfEventType: Int = getColumnIndexOrThrow(_stmt, "eventType")
        val _columnIndexOfEventTitle: Int = getColumnIndexOrThrow(_stmt, "eventTitle")
        val _columnIndexOfEventDescription: Int = getColumnIndexOrThrow(_stmt, "eventDescription")
        val _columnIndexOfEventDate: Int = getColumnIndexOrThrow(_stmt, "eventDate")
        val _columnIndexOfAgeDaysAtEvent: Int = getColumnIndexOrThrow(_stmt, "ageDaysAtEvent")
        val _columnIndexOfLifecycleStageAtEvent: Int = getColumnIndexOrThrow(_stmt,
            "lifecycleStageAtEvent")
        val _columnIndexOfNumericValue: Int = getColumnIndexOrThrow(_stmt, "numericValue")
        val _columnIndexOfNumericValue2: Int = getColumnIndexOrThrow(_stmt, "numericValue2")
        val _columnIndexOfStringValue: Int = getColumnIndexOrThrow(_stmt, "stringValue")
        val _columnIndexOfDataJson: Int = getColumnIndexOrThrow(_stmt, "dataJson")
        val _columnIndexOfMorphologyScoreDelta: Int = getColumnIndexOrThrow(_stmt,
            "morphologyScoreDelta")
        val _columnIndexOfGeneticsScoreDelta: Int = getColumnIndexOrThrow(_stmt,
            "geneticsScoreDelta")
        val _columnIndexOfPerformanceScoreDelta: Int = getColumnIndexOrThrow(_stmt,
            "performanceScoreDelta")
        val _columnIndexOfHealthScoreDelta: Int = getColumnIndexOrThrow(_stmt, "healthScoreDelta")
        val _columnIndexOfMarketScoreDelta: Int = getColumnIndexOrThrow(_stmt, "marketScoreDelta")
        val _columnIndexOfRecordedBy: Int = getColumnIndexOrThrow(_stmt, "recordedBy")
        val _columnIndexOfIsVerified: Int = getColumnIndexOrThrow(_stmt, "isVerified")
        val _columnIndexOfVerifiedBy: Int = getColumnIndexOrThrow(_stmt, "verifiedBy")
        val _columnIndexOfMediaUrlsJson: Int = getColumnIndexOrThrow(_stmt, "mediaUrlsJson")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _result: MutableList<BirdEventEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: BirdEventEntity
          val _tmpEventId: String
          _tmpEventId = _stmt.getText(_columnIndexOfEventId)
          val _tmpBirdId: String
          _tmpBirdId = _stmt.getText(_columnIndexOfBirdId)
          val _tmpOwnerId: String
          _tmpOwnerId = _stmt.getText(_columnIndexOfOwnerId)
          val _tmpEventType: String
          _tmpEventType = _stmt.getText(_columnIndexOfEventType)
          val _tmpEventTitle: String
          _tmpEventTitle = _stmt.getText(_columnIndexOfEventTitle)
          val _tmpEventDescription: String?
          if (_stmt.isNull(_columnIndexOfEventDescription)) {
            _tmpEventDescription = null
          } else {
            _tmpEventDescription = _stmt.getText(_columnIndexOfEventDescription)
          }
          val _tmpEventDate: Long
          _tmpEventDate = _stmt.getLong(_columnIndexOfEventDate)
          val _tmpAgeDaysAtEvent: Int?
          if (_stmt.isNull(_columnIndexOfAgeDaysAtEvent)) {
            _tmpAgeDaysAtEvent = null
          } else {
            _tmpAgeDaysAtEvent = _stmt.getLong(_columnIndexOfAgeDaysAtEvent).toInt()
          }
          val _tmpLifecycleStageAtEvent: String?
          if (_stmt.isNull(_columnIndexOfLifecycleStageAtEvent)) {
            _tmpLifecycleStageAtEvent = null
          } else {
            _tmpLifecycleStageAtEvent = _stmt.getText(_columnIndexOfLifecycleStageAtEvent)
          }
          val _tmpNumericValue: Double?
          if (_stmt.isNull(_columnIndexOfNumericValue)) {
            _tmpNumericValue = null
          } else {
            _tmpNumericValue = _stmt.getDouble(_columnIndexOfNumericValue)
          }
          val _tmpNumericValue2: Double?
          if (_stmt.isNull(_columnIndexOfNumericValue2)) {
            _tmpNumericValue2 = null
          } else {
            _tmpNumericValue2 = _stmt.getDouble(_columnIndexOfNumericValue2)
          }
          val _tmpStringValue: String?
          if (_stmt.isNull(_columnIndexOfStringValue)) {
            _tmpStringValue = null
          } else {
            _tmpStringValue = _stmt.getText(_columnIndexOfStringValue)
          }
          val _tmpDataJson: String?
          if (_stmt.isNull(_columnIndexOfDataJson)) {
            _tmpDataJson = null
          } else {
            _tmpDataJson = _stmt.getText(_columnIndexOfDataJson)
          }
          val _tmpMorphologyScoreDelta: Int?
          if (_stmt.isNull(_columnIndexOfMorphologyScoreDelta)) {
            _tmpMorphologyScoreDelta = null
          } else {
            _tmpMorphologyScoreDelta = _stmt.getLong(_columnIndexOfMorphologyScoreDelta).toInt()
          }
          val _tmpGeneticsScoreDelta: Int?
          if (_stmt.isNull(_columnIndexOfGeneticsScoreDelta)) {
            _tmpGeneticsScoreDelta = null
          } else {
            _tmpGeneticsScoreDelta = _stmt.getLong(_columnIndexOfGeneticsScoreDelta).toInt()
          }
          val _tmpPerformanceScoreDelta: Int?
          if (_stmt.isNull(_columnIndexOfPerformanceScoreDelta)) {
            _tmpPerformanceScoreDelta = null
          } else {
            _tmpPerformanceScoreDelta = _stmt.getLong(_columnIndexOfPerformanceScoreDelta).toInt()
          }
          val _tmpHealthScoreDelta: Int?
          if (_stmt.isNull(_columnIndexOfHealthScoreDelta)) {
            _tmpHealthScoreDelta = null
          } else {
            _tmpHealthScoreDelta = _stmt.getLong(_columnIndexOfHealthScoreDelta).toInt()
          }
          val _tmpMarketScoreDelta: Int?
          if (_stmt.isNull(_columnIndexOfMarketScoreDelta)) {
            _tmpMarketScoreDelta = null
          } else {
            _tmpMarketScoreDelta = _stmt.getLong(_columnIndexOfMarketScoreDelta).toInt()
          }
          val _tmpRecordedBy: String?
          if (_stmt.isNull(_columnIndexOfRecordedBy)) {
            _tmpRecordedBy = null
          } else {
            _tmpRecordedBy = _stmt.getText(_columnIndexOfRecordedBy)
          }
          val _tmpIsVerified: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsVerified).toInt()
          _tmpIsVerified = _tmp != 0
          val _tmpVerifiedBy: String?
          if (_stmt.isNull(_columnIndexOfVerifiedBy)) {
            _tmpVerifiedBy = null
          } else {
            _tmpVerifiedBy = _stmt.getText(_columnIndexOfVerifiedBy)
          }
          val _tmpMediaUrlsJson: String?
          if (_stmt.isNull(_columnIndexOfMediaUrlsJson)) {
            _tmpMediaUrlsJson = null
          } else {
            _tmpMediaUrlsJson = _stmt.getText(_columnIndexOfMediaUrlsJson)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
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
              BirdEventEntity(_tmpEventId,_tmpBirdId,_tmpOwnerId,_tmpEventType,_tmpEventTitle,_tmpEventDescription,_tmpEventDate,_tmpAgeDaysAtEvent,_tmpLifecycleStageAtEvent,_tmpNumericValue,_tmpNumericValue2,_tmpStringValue,_tmpDataJson,_tmpMorphologyScoreDelta,_tmpGeneticsScoreDelta,_tmpPerformanceScoreDelta,_tmpHealthScoreDelta,_tmpMarketScoreDelta,_tmpRecordedBy,_tmpIsVerified,_tmpVerifiedBy,_tmpMediaUrlsJson,_tmpCreatedAt,_tmpDirty,_tmpSyncedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getScoreDeltas(birdId: String): ScoreDeltaSummary {
    val _sql: String = """
        |
        |        SELECT 
        |            COALESCE(SUM(morphologyScoreDelta), 0) as morphDelta,
        |            COALESCE(SUM(geneticsScoreDelta), 0) as genDelta,
        |            COALESCE(SUM(performanceScoreDelta), 0) as perfDelta,
        |            COALESCE(SUM(healthScoreDelta), 0) as healthDelta,
        |            COALESCE(SUM(marketScoreDelta), 0) as marketDelta
        |        FROM bird_events WHERE birdId = ?
        |    
        """.trimMargin()
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, birdId)
        val _columnIndexOfMorphDelta: Int = 0
        val _columnIndexOfGenDelta: Int = 1
        val _columnIndexOfPerfDelta: Int = 2
        val _columnIndexOfHealthDelta: Int = 3
        val _columnIndexOfMarketDelta: Int = 4
        val _result: ScoreDeltaSummary
        if (_stmt.step()) {
          val _tmpMorphDelta: Int
          _tmpMorphDelta = _stmt.getLong(_columnIndexOfMorphDelta).toInt()
          val _tmpGenDelta: Int
          _tmpGenDelta = _stmt.getLong(_columnIndexOfGenDelta).toInt()
          val _tmpPerfDelta: Int
          _tmpPerfDelta = _stmt.getLong(_columnIndexOfPerfDelta).toInt()
          val _tmpHealthDelta: Int
          _tmpHealthDelta = _stmt.getLong(_columnIndexOfHealthDelta).toInt()
          val _tmpMarketDelta: Int
          _tmpMarketDelta = _stmt.getLong(_columnIndexOfMarketDelta).toInt()
          _result =
              ScoreDeltaSummary(_tmpMorphDelta,_tmpGenDelta,_tmpPerfDelta,_tmpHealthDelta,_tmpMarketDelta)
        } else {
          error("The query result was empty, but expected a single row to return a NON-NULL object of type <com.rio.rostry.`data`.database.dao.ScoreDeltaSummary>.")
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun countEvents(birdId: String, eventType: String): Int {
    val _sql: String = "SELECT COUNT(*) FROM bird_events WHERE birdId = ? AND eventType = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, birdId)
        _argIndex = 2
        _stmt.bindText(_argIndex, eventType)
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

  public override suspend fun countFightWins(birdId: String): Int {
    val _sql: String =
        "SELECT COUNT(*) FROM bird_events WHERE birdId = ? AND eventType = 'FIGHT_WIN'"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, birdId)
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

  public override suspend fun countTotalFights(birdId: String): Int {
    val _sql: String =
        "SELECT COUNT(*) FROM bird_events WHERE birdId = ? AND eventType IN ('FIGHT_WIN', 'FIGHT_LOSS', 'FIGHT_DRAW')"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, birdId)
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

  public override suspend fun getDirtyEvents(): List<BirdEventEntity> {
    val _sql: String = "SELECT * FROM bird_events WHERE dirty = 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfEventId: Int = getColumnIndexOrThrow(_stmt, "eventId")
        val _columnIndexOfBirdId: Int = getColumnIndexOrThrow(_stmt, "birdId")
        val _columnIndexOfOwnerId: Int = getColumnIndexOrThrow(_stmt, "ownerId")
        val _columnIndexOfEventType: Int = getColumnIndexOrThrow(_stmt, "eventType")
        val _columnIndexOfEventTitle: Int = getColumnIndexOrThrow(_stmt, "eventTitle")
        val _columnIndexOfEventDescription: Int = getColumnIndexOrThrow(_stmt, "eventDescription")
        val _columnIndexOfEventDate: Int = getColumnIndexOrThrow(_stmt, "eventDate")
        val _columnIndexOfAgeDaysAtEvent: Int = getColumnIndexOrThrow(_stmt, "ageDaysAtEvent")
        val _columnIndexOfLifecycleStageAtEvent: Int = getColumnIndexOrThrow(_stmt,
            "lifecycleStageAtEvent")
        val _columnIndexOfNumericValue: Int = getColumnIndexOrThrow(_stmt, "numericValue")
        val _columnIndexOfNumericValue2: Int = getColumnIndexOrThrow(_stmt, "numericValue2")
        val _columnIndexOfStringValue: Int = getColumnIndexOrThrow(_stmt, "stringValue")
        val _columnIndexOfDataJson: Int = getColumnIndexOrThrow(_stmt, "dataJson")
        val _columnIndexOfMorphologyScoreDelta: Int = getColumnIndexOrThrow(_stmt,
            "morphologyScoreDelta")
        val _columnIndexOfGeneticsScoreDelta: Int = getColumnIndexOrThrow(_stmt,
            "geneticsScoreDelta")
        val _columnIndexOfPerformanceScoreDelta: Int = getColumnIndexOrThrow(_stmt,
            "performanceScoreDelta")
        val _columnIndexOfHealthScoreDelta: Int = getColumnIndexOrThrow(_stmt, "healthScoreDelta")
        val _columnIndexOfMarketScoreDelta: Int = getColumnIndexOrThrow(_stmt, "marketScoreDelta")
        val _columnIndexOfRecordedBy: Int = getColumnIndexOrThrow(_stmt, "recordedBy")
        val _columnIndexOfIsVerified: Int = getColumnIndexOrThrow(_stmt, "isVerified")
        val _columnIndexOfVerifiedBy: Int = getColumnIndexOrThrow(_stmt, "verifiedBy")
        val _columnIndexOfMediaUrlsJson: Int = getColumnIndexOrThrow(_stmt, "mediaUrlsJson")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _result: MutableList<BirdEventEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: BirdEventEntity
          val _tmpEventId: String
          _tmpEventId = _stmt.getText(_columnIndexOfEventId)
          val _tmpBirdId: String
          _tmpBirdId = _stmt.getText(_columnIndexOfBirdId)
          val _tmpOwnerId: String
          _tmpOwnerId = _stmt.getText(_columnIndexOfOwnerId)
          val _tmpEventType: String
          _tmpEventType = _stmt.getText(_columnIndexOfEventType)
          val _tmpEventTitle: String
          _tmpEventTitle = _stmt.getText(_columnIndexOfEventTitle)
          val _tmpEventDescription: String?
          if (_stmt.isNull(_columnIndexOfEventDescription)) {
            _tmpEventDescription = null
          } else {
            _tmpEventDescription = _stmt.getText(_columnIndexOfEventDescription)
          }
          val _tmpEventDate: Long
          _tmpEventDate = _stmt.getLong(_columnIndexOfEventDate)
          val _tmpAgeDaysAtEvent: Int?
          if (_stmt.isNull(_columnIndexOfAgeDaysAtEvent)) {
            _tmpAgeDaysAtEvent = null
          } else {
            _tmpAgeDaysAtEvent = _stmt.getLong(_columnIndexOfAgeDaysAtEvent).toInt()
          }
          val _tmpLifecycleStageAtEvent: String?
          if (_stmt.isNull(_columnIndexOfLifecycleStageAtEvent)) {
            _tmpLifecycleStageAtEvent = null
          } else {
            _tmpLifecycleStageAtEvent = _stmt.getText(_columnIndexOfLifecycleStageAtEvent)
          }
          val _tmpNumericValue: Double?
          if (_stmt.isNull(_columnIndexOfNumericValue)) {
            _tmpNumericValue = null
          } else {
            _tmpNumericValue = _stmt.getDouble(_columnIndexOfNumericValue)
          }
          val _tmpNumericValue2: Double?
          if (_stmt.isNull(_columnIndexOfNumericValue2)) {
            _tmpNumericValue2 = null
          } else {
            _tmpNumericValue2 = _stmt.getDouble(_columnIndexOfNumericValue2)
          }
          val _tmpStringValue: String?
          if (_stmt.isNull(_columnIndexOfStringValue)) {
            _tmpStringValue = null
          } else {
            _tmpStringValue = _stmt.getText(_columnIndexOfStringValue)
          }
          val _tmpDataJson: String?
          if (_stmt.isNull(_columnIndexOfDataJson)) {
            _tmpDataJson = null
          } else {
            _tmpDataJson = _stmt.getText(_columnIndexOfDataJson)
          }
          val _tmpMorphologyScoreDelta: Int?
          if (_stmt.isNull(_columnIndexOfMorphologyScoreDelta)) {
            _tmpMorphologyScoreDelta = null
          } else {
            _tmpMorphologyScoreDelta = _stmt.getLong(_columnIndexOfMorphologyScoreDelta).toInt()
          }
          val _tmpGeneticsScoreDelta: Int?
          if (_stmt.isNull(_columnIndexOfGeneticsScoreDelta)) {
            _tmpGeneticsScoreDelta = null
          } else {
            _tmpGeneticsScoreDelta = _stmt.getLong(_columnIndexOfGeneticsScoreDelta).toInt()
          }
          val _tmpPerformanceScoreDelta: Int?
          if (_stmt.isNull(_columnIndexOfPerformanceScoreDelta)) {
            _tmpPerformanceScoreDelta = null
          } else {
            _tmpPerformanceScoreDelta = _stmt.getLong(_columnIndexOfPerformanceScoreDelta).toInt()
          }
          val _tmpHealthScoreDelta: Int?
          if (_stmt.isNull(_columnIndexOfHealthScoreDelta)) {
            _tmpHealthScoreDelta = null
          } else {
            _tmpHealthScoreDelta = _stmt.getLong(_columnIndexOfHealthScoreDelta).toInt()
          }
          val _tmpMarketScoreDelta: Int?
          if (_stmt.isNull(_columnIndexOfMarketScoreDelta)) {
            _tmpMarketScoreDelta = null
          } else {
            _tmpMarketScoreDelta = _stmt.getLong(_columnIndexOfMarketScoreDelta).toInt()
          }
          val _tmpRecordedBy: String?
          if (_stmt.isNull(_columnIndexOfRecordedBy)) {
            _tmpRecordedBy = null
          } else {
            _tmpRecordedBy = _stmt.getText(_columnIndexOfRecordedBy)
          }
          val _tmpIsVerified: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsVerified).toInt()
          _tmpIsVerified = _tmp != 0
          val _tmpVerifiedBy: String?
          if (_stmt.isNull(_columnIndexOfVerifiedBy)) {
            _tmpVerifiedBy = null
          } else {
            _tmpVerifiedBy = _stmt.getText(_columnIndexOfVerifiedBy)
          }
          val _tmpMediaUrlsJson: String?
          if (_stmt.isNull(_columnIndexOfMediaUrlsJson)) {
            _tmpMediaUrlsJson = null
          } else {
            _tmpMediaUrlsJson = _stmt.getText(_columnIndexOfMediaUrlsJson)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
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
              BirdEventEntity(_tmpEventId,_tmpBirdId,_tmpOwnerId,_tmpEventType,_tmpEventTitle,_tmpEventDescription,_tmpEventDate,_tmpAgeDaysAtEvent,_tmpLifecycleStageAtEvent,_tmpNumericValue,_tmpNumericValue2,_tmpStringValue,_tmpDataJson,_tmpMorphologyScoreDelta,_tmpGeneticsScoreDelta,_tmpPerformanceScoreDelta,_tmpHealthScoreDelta,_tmpMarketScoreDelta,_tmpRecordedBy,_tmpIsVerified,_tmpVerifiedBy,_tmpMediaUrlsJson,_tmpCreatedAt,_tmpDirty,_tmpSyncedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun markSynced(eventId: String, syncedAt: Long) {
    val _sql: String = "UPDATE bird_events SET dirty = 0, syncedAt = ? WHERE eventId = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, syncedAt)
        _argIndex = 2
        _stmt.bindText(_argIndex, eventId)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun deleteAllForBird(birdId: String) {
    val _sql: String = "DELETE FROM bird_events WHERE birdId = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, birdId)
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
