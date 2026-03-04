package com.rio.rostry.`data`.database.dao

import androidx.room.EntityDeleteOrUpdateAdapter
import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.rio.rostry.`data`.database.entity.FarmTimelineEventEntity
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
public class FarmTimelineEventDao_Impl(
  __db: RoomDatabase,
) : FarmTimelineEventDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfFarmTimelineEventEntity: EntityInsertAdapter<FarmTimelineEventEntity>

  private val __updateAdapterOfFarmTimelineEventEntity:
      EntityDeleteOrUpdateAdapter<FarmTimelineEventEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfFarmTimelineEventEntity = object :
        EntityInsertAdapter<FarmTimelineEventEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `farm_timeline_events` (`eventId`,`farmerId`,`eventType`,`title`,`description`,`iconType`,`imageUrl`,`sourceType`,`sourceId`,`trustPointsEarned`,`isPublic`,`isMilestone`,`eventDate`,`createdAt`,`updatedAt`,`dirty`,`syncedAt`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: FarmTimelineEventEntity) {
        statement.bindText(1, entity.eventId)
        statement.bindText(2, entity.farmerId)
        statement.bindText(3, entity.eventType)
        statement.bindText(4, entity.title)
        statement.bindText(5, entity.description)
        val _tmpIconType: String? = entity.iconType
        if (_tmpIconType == null) {
          statement.bindNull(6)
        } else {
          statement.bindText(6, _tmpIconType)
        }
        val _tmpImageUrl: String? = entity.imageUrl
        if (_tmpImageUrl == null) {
          statement.bindNull(7)
        } else {
          statement.bindText(7, _tmpImageUrl)
        }
        val _tmpSourceType: String? = entity.sourceType
        if (_tmpSourceType == null) {
          statement.bindNull(8)
        } else {
          statement.bindText(8, _tmpSourceType)
        }
        val _tmpSourceId: String? = entity.sourceId
        if (_tmpSourceId == null) {
          statement.bindNull(9)
        } else {
          statement.bindText(9, _tmpSourceId)
        }
        statement.bindLong(10, entity.trustPointsEarned.toLong())
        val _tmp: Int = if (entity.isPublic) 1 else 0
        statement.bindLong(11, _tmp.toLong())
        val _tmp_1: Int = if (entity.isMilestone) 1 else 0
        statement.bindLong(12, _tmp_1.toLong())
        statement.bindLong(13, entity.eventDate)
        statement.bindLong(14, entity.createdAt)
        statement.bindLong(15, entity.updatedAt)
        val _tmp_2: Int = if (entity.dirty) 1 else 0
        statement.bindLong(16, _tmp_2.toLong())
        val _tmpSyncedAt: Long? = entity.syncedAt
        if (_tmpSyncedAt == null) {
          statement.bindNull(17)
        } else {
          statement.bindLong(17, _tmpSyncedAt)
        }
      }
    }
    this.__updateAdapterOfFarmTimelineEventEntity = object :
        EntityDeleteOrUpdateAdapter<FarmTimelineEventEntity>() {
      protected override fun createQuery(): String =
          "UPDATE OR ABORT `farm_timeline_events` SET `eventId` = ?,`farmerId` = ?,`eventType` = ?,`title` = ?,`description` = ?,`iconType` = ?,`imageUrl` = ?,`sourceType` = ?,`sourceId` = ?,`trustPointsEarned` = ?,`isPublic` = ?,`isMilestone` = ?,`eventDate` = ?,`createdAt` = ?,`updatedAt` = ?,`dirty` = ?,`syncedAt` = ? WHERE `eventId` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: FarmTimelineEventEntity) {
        statement.bindText(1, entity.eventId)
        statement.bindText(2, entity.farmerId)
        statement.bindText(3, entity.eventType)
        statement.bindText(4, entity.title)
        statement.bindText(5, entity.description)
        val _tmpIconType: String? = entity.iconType
        if (_tmpIconType == null) {
          statement.bindNull(6)
        } else {
          statement.bindText(6, _tmpIconType)
        }
        val _tmpImageUrl: String? = entity.imageUrl
        if (_tmpImageUrl == null) {
          statement.bindNull(7)
        } else {
          statement.bindText(7, _tmpImageUrl)
        }
        val _tmpSourceType: String? = entity.sourceType
        if (_tmpSourceType == null) {
          statement.bindNull(8)
        } else {
          statement.bindText(8, _tmpSourceType)
        }
        val _tmpSourceId: String? = entity.sourceId
        if (_tmpSourceId == null) {
          statement.bindNull(9)
        } else {
          statement.bindText(9, _tmpSourceId)
        }
        statement.bindLong(10, entity.trustPointsEarned.toLong())
        val _tmp: Int = if (entity.isPublic) 1 else 0
        statement.bindLong(11, _tmp.toLong())
        val _tmp_1: Int = if (entity.isMilestone) 1 else 0
        statement.bindLong(12, _tmp_1.toLong())
        statement.bindLong(13, entity.eventDate)
        statement.bindLong(14, entity.createdAt)
        statement.bindLong(15, entity.updatedAt)
        val _tmp_2: Int = if (entity.dirty) 1 else 0
        statement.bindLong(16, _tmp_2.toLong())
        val _tmpSyncedAt: Long? = entity.syncedAt
        if (_tmpSyncedAt == null) {
          statement.bindNull(17)
        } else {
          statement.bindLong(17, _tmpSyncedAt)
        }
        statement.bindText(18, entity.eventId)
      }
    }
  }

  public override suspend fun insert(event: FarmTimelineEventEntity): Unit = performSuspending(__db,
      false, true) { _connection ->
    __insertAdapterOfFarmTimelineEventEntity.insert(_connection, event)
  }

  public override suspend fun insertAll(events: List<FarmTimelineEventEntity>): Unit =
      performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfFarmTimelineEventEntity.insert(_connection, events)
  }

  public override suspend fun update(event: FarmTimelineEventEntity): Unit = performSuspending(__db,
      false, true) { _connection ->
    __updateAdapterOfFarmTimelineEventEntity.handle(_connection, event)
  }

  public override suspend fun findById(eventId: String): FarmTimelineEventEntity? {
    val _sql: String = "SELECT * FROM farm_timeline_events WHERE eventId = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, eventId)
        val _columnIndexOfEventId: Int = getColumnIndexOrThrow(_stmt, "eventId")
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfEventType: Int = getColumnIndexOrThrow(_stmt, "eventType")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfIconType: Int = getColumnIndexOrThrow(_stmt, "iconType")
        val _columnIndexOfImageUrl: Int = getColumnIndexOrThrow(_stmt, "imageUrl")
        val _columnIndexOfSourceType: Int = getColumnIndexOrThrow(_stmt, "sourceType")
        val _columnIndexOfSourceId: Int = getColumnIndexOrThrow(_stmt, "sourceId")
        val _columnIndexOfTrustPointsEarned: Int = getColumnIndexOrThrow(_stmt, "trustPointsEarned")
        val _columnIndexOfIsPublic: Int = getColumnIndexOrThrow(_stmt, "isPublic")
        val _columnIndexOfIsMilestone: Int = getColumnIndexOrThrow(_stmt, "isMilestone")
        val _columnIndexOfEventDate: Int = getColumnIndexOrThrow(_stmt, "eventDate")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _result: FarmTimelineEventEntity?
        if (_stmt.step()) {
          val _tmpEventId: String
          _tmpEventId = _stmt.getText(_columnIndexOfEventId)
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpEventType: String
          _tmpEventType = _stmt.getText(_columnIndexOfEventType)
          val _tmpTitle: String
          _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          val _tmpDescription: String
          _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          val _tmpIconType: String?
          if (_stmt.isNull(_columnIndexOfIconType)) {
            _tmpIconType = null
          } else {
            _tmpIconType = _stmt.getText(_columnIndexOfIconType)
          }
          val _tmpImageUrl: String?
          if (_stmt.isNull(_columnIndexOfImageUrl)) {
            _tmpImageUrl = null
          } else {
            _tmpImageUrl = _stmt.getText(_columnIndexOfImageUrl)
          }
          val _tmpSourceType: String?
          if (_stmt.isNull(_columnIndexOfSourceType)) {
            _tmpSourceType = null
          } else {
            _tmpSourceType = _stmt.getText(_columnIndexOfSourceType)
          }
          val _tmpSourceId: String?
          if (_stmt.isNull(_columnIndexOfSourceId)) {
            _tmpSourceId = null
          } else {
            _tmpSourceId = _stmt.getText(_columnIndexOfSourceId)
          }
          val _tmpTrustPointsEarned: Int
          _tmpTrustPointsEarned = _stmt.getLong(_columnIndexOfTrustPointsEarned).toInt()
          val _tmpIsPublic: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsPublic).toInt()
          _tmpIsPublic = _tmp != 0
          val _tmpIsMilestone: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfIsMilestone).toInt()
          _tmpIsMilestone = _tmp_1 != 0
          val _tmpEventDate: Long
          _tmpEventDate = _stmt.getLong(_columnIndexOfEventDate)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpDirty: Boolean
          val _tmp_2: Int
          _tmp_2 = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp_2 != 0
          val _tmpSyncedAt: Long?
          if (_stmt.isNull(_columnIndexOfSyncedAt)) {
            _tmpSyncedAt = null
          } else {
            _tmpSyncedAt = _stmt.getLong(_columnIndexOfSyncedAt)
          }
          _result =
              FarmTimelineEventEntity(_tmpEventId,_tmpFarmerId,_tmpEventType,_tmpTitle,_tmpDescription,_tmpIconType,_tmpImageUrl,_tmpSourceType,_tmpSourceId,_tmpTrustPointsEarned,_tmpIsPublic,_tmpIsMilestone,_tmpEventDate,_tmpCreatedAt,_tmpUpdatedAt,_tmpDirty,_tmpSyncedAt)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observePublicTimeline(farmerId: String, limit: Int):
      Flow<List<FarmTimelineEventEntity>> {
    val _sql: String =
        "SELECT * FROM farm_timeline_events WHERE farmerId = ? AND isPublic = 1 ORDER BY eventDate DESC LIMIT ?"
    return createFlow(__db, false, arrayOf("farm_timeline_events")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, farmerId)
        _argIndex = 2
        _stmt.bindLong(_argIndex, limit.toLong())
        val _columnIndexOfEventId: Int = getColumnIndexOrThrow(_stmt, "eventId")
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfEventType: Int = getColumnIndexOrThrow(_stmt, "eventType")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfIconType: Int = getColumnIndexOrThrow(_stmt, "iconType")
        val _columnIndexOfImageUrl: Int = getColumnIndexOrThrow(_stmt, "imageUrl")
        val _columnIndexOfSourceType: Int = getColumnIndexOrThrow(_stmt, "sourceType")
        val _columnIndexOfSourceId: Int = getColumnIndexOrThrow(_stmt, "sourceId")
        val _columnIndexOfTrustPointsEarned: Int = getColumnIndexOrThrow(_stmt, "trustPointsEarned")
        val _columnIndexOfIsPublic: Int = getColumnIndexOrThrow(_stmt, "isPublic")
        val _columnIndexOfIsMilestone: Int = getColumnIndexOrThrow(_stmt, "isMilestone")
        val _columnIndexOfEventDate: Int = getColumnIndexOrThrow(_stmt, "eventDate")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _result: MutableList<FarmTimelineEventEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: FarmTimelineEventEntity
          val _tmpEventId: String
          _tmpEventId = _stmt.getText(_columnIndexOfEventId)
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpEventType: String
          _tmpEventType = _stmt.getText(_columnIndexOfEventType)
          val _tmpTitle: String
          _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          val _tmpDescription: String
          _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          val _tmpIconType: String?
          if (_stmt.isNull(_columnIndexOfIconType)) {
            _tmpIconType = null
          } else {
            _tmpIconType = _stmt.getText(_columnIndexOfIconType)
          }
          val _tmpImageUrl: String?
          if (_stmt.isNull(_columnIndexOfImageUrl)) {
            _tmpImageUrl = null
          } else {
            _tmpImageUrl = _stmt.getText(_columnIndexOfImageUrl)
          }
          val _tmpSourceType: String?
          if (_stmt.isNull(_columnIndexOfSourceType)) {
            _tmpSourceType = null
          } else {
            _tmpSourceType = _stmt.getText(_columnIndexOfSourceType)
          }
          val _tmpSourceId: String?
          if (_stmt.isNull(_columnIndexOfSourceId)) {
            _tmpSourceId = null
          } else {
            _tmpSourceId = _stmt.getText(_columnIndexOfSourceId)
          }
          val _tmpTrustPointsEarned: Int
          _tmpTrustPointsEarned = _stmt.getLong(_columnIndexOfTrustPointsEarned).toInt()
          val _tmpIsPublic: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsPublic).toInt()
          _tmpIsPublic = _tmp != 0
          val _tmpIsMilestone: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfIsMilestone).toInt()
          _tmpIsMilestone = _tmp_1 != 0
          val _tmpEventDate: Long
          _tmpEventDate = _stmt.getLong(_columnIndexOfEventDate)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpDirty: Boolean
          val _tmp_2: Int
          _tmp_2 = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp_2 != 0
          val _tmpSyncedAt: Long?
          if (_stmt.isNull(_columnIndexOfSyncedAt)) {
            _tmpSyncedAt = null
          } else {
            _tmpSyncedAt = _stmt.getLong(_columnIndexOfSyncedAt)
          }
          _item =
              FarmTimelineEventEntity(_tmpEventId,_tmpFarmerId,_tmpEventType,_tmpTitle,_tmpDescription,_tmpIconType,_tmpImageUrl,_tmpSourceType,_tmpSourceId,_tmpTrustPointsEarned,_tmpIsPublic,_tmpIsMilestone,_tmpEventDate,_tmpCreatedAt,_tmpUpdatedAt,_tmpDirty,_tmpSyncedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeFullTimeline(farmerId: String, limit: Int):
      Flow<List<FarmTimelineEventEntity>> {
    val _sql: String =
        "SELECT * FROM farm_timeline_events WHERE farmerId = ? ORDER BY eventDate DESC LIMIT ?"
    return createFlow(__db, false, arrayOf("farm_timeline_events")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, farmerId)
        _argIndex = 2
        _stmt.bindLong(_argIndex, limit.toLong())
        val _columnIndexOfEventId: Int = getColumnIndexOrThrow(_stmt, "eventId")
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfEventType: Int = getColumnIndexOrThrow(_stmt, "eventType")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfIconType: Int = getColumnIndexOrThrow(_stmt, "iconType")
        val _columnIndexOfImageUrl: Int = getColumnIndexOrThrow(_stmt, "imageUrl")
        val _columnIndexOfSourceType: Int = getColumnIndexOrThrow(_stmt, "sourceType")
        val _columnIndexOfSourceId: Int = getColumnIndexOrThrow(_stmt, "sourceId")
        val _columnIndexOfTrustPointsEarned: Int = getColumnIndexOrThrow(_stmt, "trustPointsEarned")
        val _columnIndexOfIsPublic: Int = getColumnIndexOrThrow(_stmt, "isPublic")
        val _columnIndexOfIsMilestone: Int = getColumnIndexOrThrow(_stmt, "isMilestone")
        val _columnIndexOfEventDate: Int = getColumnIndexOrThrow(_stmt, "eventDate")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _result: MutableList<FarmTimelineEventEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: FarmTimelineEventEntity
          val _tmpEventId: String
          _tmpEventId = _stmt.getText(_columnIndexOfEventId)
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpEventType: String
          _tmpEventType = _stmt.getText(_columnIndexOfEventType)
          val _tmpTitle: String
          _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          val _tmpDescription: String
          _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          val _tmpIconType: String?
          if (_stmt.isNull(_columnIndexOfIconType)) {
            _tmpIconType = null
          } else {
            _tmpIconType = _stmt.getText(_columnIndexOfIconType)
          }
          val _tmpImageUrl: String?
          if (_stmt.isNull(_columnIndexOfImageUrl)) {
            _tmpImageUrl = null
          } else {
            _tmpImageUrl = _stmt.getText(_columnIndexOfImageUrl)
          }
          val _tmpSourceType: String?
          if (_stmt.isNull(_columnIndexOfSourceType)) {
            _tmpSourceType = null
          } else {
            _tmpSourceType = _stmt.getText(_columnIndexOfSourceType)
          }
          val _tmpSourceId: String?
          if (_stmt.isNull(_columnIndexOfSourceId)) {
            _tmpSourceId = null
          } else {
            _tmpSourceId = _stmt.getText(_columnIndexOfSourceId)
          }
          val _tmpTrustPointsEarned: Int
          _tmpTrustPointsEarned = _stmt.getLong(_columnIndexOfTrustPointsEarned).toInt()
          val _tmpIsPublic: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsPublic).toInt()
          _tmpIsPublic = _tmp != 0
          val _tmpIsMilestone: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfIsMilestone).toInt()
          _tmpIsMilestone = _tmp_1 != 0
          val _tmpEventDate: Long
          _tmpEventDate = _stmt.getLong(_columnIndexOfEventDate)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpDirty: Boolean
          val _tmp_2: Int
          _tmp_2 = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp_2 != 0
          val _tmpSyncedAt: Long?
          if (_stmt.isNull(_columnIndexOfSyncedAt)) {
            _tmpSyncedAt = null
          } else {
            _tmpSyncedAt = _stmt.getLong(_columnIndexOfSyncedAt)
          }
          _item =
              FarmTimelineEventEntity(_tmpEventId,_tmpFarmerId,_tmpEventType,_tmpTitle,_tmpDescription,_tmpIconType,_tmpImageUrl,_tmpSourceType,_tmpSourceId,_tmpTrustPointsEarned,_tmpIsPublic,_tmpIsMilestone,_tmpEventDate,_tmpCreatedAt,_tmpUpdatedAt,_tmpDirty,_tmpSyncedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeByType(
    farmerId: String,
    type: String,
    limit: Int,
  ): Flow<List<FarmTimelineEventEntity>> {
    val _sql: String =
        "SELECT * FROM farm_timeline_events WHERE farmerId = ? AND eventType = ? ORDER BY eventDate DESC LIMIT ?"
    return createFlow(__db, false, arrayOf("farm_timeline_events")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, farmerId)
        _argIndex = 2
        _stmt.bindText(_argIndex, type)
        _argIndex = 3
        _stmt.bindLong(_argIndex, limit.toLong())
        val _columnIndexOfEventId: Int = getColumnIndexOrThrow(_stmt, "eventId")
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfEventType: Int = getColumnIndexOrThrow(_stmt, "eventType")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfIconType: Int = getColumnIndexOrThrow(_stmt, "iconType")
        val _columnIndexOfImageUrl: Int = getColumnIndexOrThrow(_stmt, "imageUrl")
        val _columnIndexOfSourceType: Int = getColumnIndexOrThrow(_stmt, "sourceType")
        val _columnIndexOfSourceId: Int = getColumnIndexOrThrow(_stmt, "sourceId")
        val _columnIndexOfTrustPointsEarned: Int = getColumnIndexOrThrow(_stmt, "trustPointsEarned")
        val _columnIndexOfIsPublic: Int = getColumnIndexOrThrow(_stmt, "isPublic")
        val _columnIndexOfIsMilestone: Int = getColumnIndexOrThrow(_stmt, "isMilestone")
        val _columnIndexOfEventDate: Int = getColumnIndexOrThrow(_stmt, "eventDate")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _result: MutableList<FarmTimelineEventEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: FarmTimelineEventEntity
          val _tmpEventId: String
          _tmpEventId = _stmt.getText(_columnIndexOfEventId)
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpEventType: String
          _tmpEventType = _stmt.getText(_columnIndexOfEventType)
          val _tmpTitle: String
          _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          val _tmpDescription: String
          _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          val _tmpIconType: String?
          if (_stmt.isNull(_columnIndexOfIconType)) {
            _tmpIconType = null
          } else {
            _tmpIconType = _stmt.getText(_columnIndexOfIconType)
          }
          val _tmpImageUrl: String?
          if (_stmt.isNull(_columnIndexOfImageUrl)) {
            _tmpImageUrl = null
          } else {
            _tmpImageUrl = _stmt.getText(_columnIndexOfImageUrl)
          }
          val _tmpSourceType: String?
          if (_stmt.isNull(_columnIndexOfSourceType)) {
            _tmpSourceType = null
          } else {
            _tmpSourceType = _stmt.getText(_columnIndexOfSourceType)
          }
          val _tmpSourceId: String?
          if (_stmt.isNull(_columnIndexOfSourceId)) {
            _tmpSourceId = null
          } else {
            _tmpSourceId = _stmt.getText(_columnIndexOfSourceId)
          }
          val _tmpTrustPointsEarned: Int
          _tmpTrustPointsEarned = _stmt.getLong(_columnIndexOfTrustPointsEarned).toInt()
          val _tmpIsPublic: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsPublic).toInt()
          _tmpIsPublic = _tmp != 0
          val _tmpIsMilestone: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfIsMilestone).toInt()
          _tmpIsMilestone = _tmp_1 != 0
          val _tmpEventDate: Long
          _tmpEventDate = _stmt.getLong(_columnIndexOfEventDate)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpDirty: Boolean
          val _tmp_2: Int
          _tmp_2 = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp_2 != 0
          val _tmpSyncedAt: Long?
          if (_stmt.isNull(_columnIndexOfSyncedAt)) {
            _tmpSyncedAt = null
          } else {
            _tmpSyncedAt = _stmt.getLong(_columnIndexOfSyncedAt)
          }
          _item =
              FarmTimelineEventEntity(_tmpEventId,_tmpFarmerId,_tmpEventType,_tmpTitle,_tmpDescription,_tmpIconType,_tmpImageUrl,_tmpSourceType,_tmpSourceId,_tmpTrustPointsEarned,_tmpIsPublic,_tmpIsMilestone,_tmpEventDate,_tmpCreatedAt,_tmpUpdatedAt,_tmpDirty,_tmpSyncedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeMilestones(farmerId: String): Flow<List<FarmTimelineEventEntity>> {
    val _sql: String =
        "SELECT * FROM farm_timeline_events WHERE farmerId = ? AND isMilestone = 1 ORDER BY eventDate DESC"
    return createFlow(__db, false, arrayOf("farm_timeline_events")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, farmerId)
        val _columnIndexOfEventId: Int = getColumnIndexOrThrow(_stmt, "eventId")
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfEventType: Int = getColumnIndexOrThrow(_stmt, "eventType")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfIconType: Int = getColumnIndexOrThrow(_stmt, "iconType")
        val _columnIndexOfImageUrl: Int = getColumnIndexOrThrow(_stmt, "imageUrl")
        val _columnIndexOfSourceType: Int = getColumnIndexOrThrow(_stmt, "sourceType")
        val _columnIndexOfSourceId: Int = getColumnIndexOrThrow(_stmt, "sourceId")
        val _columnIndexOfTrustPointsEarned: Int = getColumnIndexOrThrow(_stmt, "trustPointsEarned")
        val _columnIndexOfIsPublic: Int = getColumnIndexOrThrow(_stmt, "isPublic")
        val _columnIndexOfIsMilestone: Int = getColumnIndexOrThrow(_stmt, "isMilestone")
        val _columnIndexOfEventDate: Int = getColumnIndexOrThrow(_stmt, "eventDate")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _result: MutableList<FarmTimelineEventEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: FarmTimelineEventEntity
          val _tmpEventId: String
          _tmpEventId = _stmt.getText(_columnIndexOfEventId)
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpEventType: String
          _tmpEventType = _stmt.getText(_columnIndexOfEventType)
          val _tmpTitle: String
          _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          val _tmpDescription: String
          _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          val _tmpIconType: String?
          if (_stmt.isNull(_columnIndexOfIconType)) {
            _tmpIconType = null
          } else {
            _tmpIconType = _stmt.getText(_columnIndexOfIconType)
          }
          val _tmpImageUrl: String?
          if (_stmt.isNull(_columnIndexOfImageUrl)) {
            _tmpImageUrl = null
          } else {
            _tmpImageUrl = _stmt.getText(_columnIndexOfImageUrl)
          }
          val _tmpSourceType: String?
          if (_stmt.isNull(_columnIndexOfSourceType)) {
            _tmpSourceType = null
          } else {
            _tmpSourceType = _stmt.getText(_columnIndexOfSourceType)
          }
          val _tmpSourceId: String?
          if (_stmt.isNull(_columnIndexOfSourceId)) {
            _tmpSourceId = null
          } else {
            _tmpSourceId = _stmt.getText(_columnIndexOfSourceId)
          }
          val _tmpTrustPointsEarned: Int
          _tmpTrustPointsEarned = _stmt.getLong(_columnIndexOfTrustPointsEarned).toInt()
          val _tmpIsPublic: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsPublic).toInt()
          _tmpIsPublic = _tmp != 0
          val _tmpIsMilestone: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfIsMilestone).toInt()
          _tmpIsMilestone = _tmp_1 != 0
          val _tmpEventDate: Long
          _tmpEventDate = _stmt.getLong(_columnIndexOfEventDate)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpDirty: Boolean
          val _tmp_2: Int
          _tmp_2 = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp_2 != 0
          val _tmpSyncedAt: Long?
          if (_stmt.isNull(_columnIndexOfSyncedAt)) {
            _tmpSyncedAt = null
          } else {
            _tmpSyncedAt = _stmt.getLong(_columnIndexOfSyncedAt)
          }
          _item =
              FarmTimelineEventEntity(_tmpEventId,_tmpFarmerId,_tmpEventType,_tmpTitle,_tmpDescription,_tmpIconType,_tmpImageUrl,_tmpSourceType,_tmpSourceId,_tmpTrustPointsEarned,_tmpIsPublic,_tmpIsMilestone,_tmpEventDate,_tmpCreatedAt,_tmpUpdatedAt,_tmpDirty,_tmpSyncedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getRecentEvents(farmerId: String): Flow<List<FarmTimelineEventEntity>> {
    val _sql: String =
        "SELECT * FROM farm_timeline_events WHERE farmerId = ? ORDER BY eventDate DESC LIMIT 5"
    return createFlow(__db, false, arrayOf("farm_timeline_events")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, farmerId)
        val _columnIndexOfEventId: Int = getColumnIndexOrThrow(_stmt, "eventId")
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfEventType: Int = getColumnIndexOrThrow(_stmt, "eventType")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfIconType: Int = getColumnIndexOrThrow(_stmt, "iconType")
        val _columnIndexOfImageUrl: Int = getColumnIndexOrThrow(_stmt, "imageUrl")
        val _columnIndexOfSourceType: Int = getColumnIndexOrThrow(_stmt, "sourceType")
        val _columnIndexOfSourceId: Int = getColumnIndexOrThrow(_stmt, "sourceId")
        val _columnIndexOfTrustPointsEarned: Int = getColumnIndexOrThrow(_stmt, "trustPointsEarned")
        val _columnIndexOfIsPublic: Int = getColumnIndexOrThrow(_stmt, "isPublic")
        val _columnIndexOfIsMilestone: Int = getColumnIndexOrThrow(_stmt, "isMilestone")
        val _columnIndexOfEventDate: Int = getColumnIndexOrThrow(_stmt, "eventDate")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _result: MutableList<FarmTimelineEventEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: FarmTimelineEventEntity
          val _tmpEventId: String
          _tmpEventId = _stmt.getText(_columnIndexOfEventId)
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpEventType: String
          _tmpEventType = _stmt.getText(_columnIndexOfEventType)
          val _tmpTitle: String
          _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          val _tmpDescription: String
          _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          val _tmpIconType: String?
          if (_stmt.isNull(_columnIndexOfIconType)) {
            _tmpIconType = null
          } else {
            _tmpIconType = _stmt.getText(_columnIndexOfIconType)
          }
          val _tmpImageUrl: String?
          if (_stmt.isNull(_columnIndexOfImageUrl)) {
            _tmpImageUrl = null
          } else {
            _tmpImageUrl = _stmt.getText(_columnIndexOfImageUrl)
          }
          val _tmpSourceType: String?
          if (_stmt.isNull(_columnIndexOfSourceType)) {
            _tmpSourceType = null
          } else {
            _tmpSourceType = _stmt.getText(_columnIndexOfSourceType)
          }
          val _tmpSourceId: String?
          if (_stmt.isNull(_columnIndexOfSourceId)) {
            _tmpSourceId = null
          } else {
            _tmpSourceId = _stmt.getText(_columnIndexOfSourceId)
          }
          val _tmpTrustPointsEarned: Int
          _tmpTrustPointsEarned = _stmt.getLong(_columnIndexOfTrustPointsEarned).toInt()
          val _tmpIsPublic: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsPublic).toInt()
          _tmpIsPublic = _tmp != 0
          val _tmpIsMilestone: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfIsMilestone).toInt()
          _tmpIsMilestone = _tmp_1 != 0
          val _tmpEventDate: Long
          _tmpEventDate = _stmt.getLong(_columnIndexOfEventDate)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpDirty: Boolean
          val _tmp_2: Int
          _tmp_2 = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp_2 != 0
          val _tmpSyncedAt: Long?
          if (_stmt.isNull(_columnIndexOfSyncedAt)) {
            _tmpSyncedAt = null
          } else {
            _tmpSyncedAt = _stmt.getLong(_columnIndexOfSyncedAt)
          }
          _item =
              FarmTimelineEventEntity(_tmpEventId,_tmpFarmerId,_tmpEventType,_tmpTitle,_tmpDescription,_tmpIconType,_tmpImageUrl,_tmpSourceType,_tmpSourceId,_tmpTrustPointsEarned,_tmpIsPublic,_tmpIsMilestone,_tmpEventDate,_tmpCreatedAt,_tmpUpdatedAt,_tmpDirty,_tmpSyncedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun existsForSource(sourceType: String, sourceId: String): Boolean {
    val _sql: String =
        "SELECT COUNT(*) > 0 FROM farm_timeline_events WHERE sourceType = ? AND sourceId = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, sourceType)
        _argIndex = 2
        _stmt.bindText(_argIndex, sourceId)
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

  public override suspend fun countByType(farmerId: String, type: String): Int {
    val _sql: String =
        "SELECT COUNT(*) FROM farm_timeline_events WHERE farmerId = ? AND eventType = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, farmerId)
        _argIndex = 2
        _stmt.bindText(_argIndex, type)
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

  public override suspend fun getTotalTrustPoints(farmerId: String): Int? {
    val _sql: String = "SELECT SUM(trustPointsEarned) FROM farm_timeline_events WHERE farmerId = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, farmerId)
        val _result: Int?
        if (_stmt.step()) {
          val _tmp: Int?
          if (_stmt.isNull(0)) {
            _tmp = null
          } else {
            _tmp = _stmt.getLong(0).toInt()
          }
          _result = _tmp
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getDirtyEvents(limit: Int): List<FarmTimelineEventEntity> {
    val _sql: String = "SELECT * FROM farm_timeline_events WHERE dirty = 1 LIMIT ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, limit.toLong())
        val _columnIndexOfEventId: Int = getColumnIndexOrThrow(_stmt, "eventId")
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfEventType: Int = getColumnIndexOrThrow(_stmt, "eventType")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfIconType: Int = getColumnIndexOrThrow(_stmt, "iconType")
        val _columnIndexOfImageUrl: Int = getColumnIndexOrThrow(_stmt, "imageUrl")
        val _columnIndexOfSourceType: Int = getColumnIndexOrThrow(_stmt, "sourceType")
        val _columnIndexOfSourceId: Int = getColumnIndexOrThrow(_stmt, "sourceId")
        val _columnIndexOfTrustPointsEarned: Int = getColumnIndexOrThrow(_stmt, "trustPointsEarned")
        val _columnIndexOfIsPublic: Int = getColumnIndexOrThrow(_stmt, "isPublic")
        val _columnIndexOfIsMilestone: Int = getColumnIndexOrThrow(_stmt, "isMilestone")
        val _columnIndexOfEventDate: Int = getColumnIndexOrThrow(_stmt, "eventDate")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _result: MutableList<FarmTimelineEventEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: FarmTimelineEventEntity
          val _tmpEventId: String
          _tmpEventId = _stmt.getText(_columnIndexOfEventId)
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpEventType: String
          _tmpEventType = _stmt.getText(_columnIndexOfEventType)
          val _tmpTitle: String
          _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          val _tmpDescription: String
          _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          val _tmpIconType: String?
          if (_stmt.isNull(_columnIndexOfIconType)) {
            _tmpIconType = null
          } else {
            _tmpIconType = _stmt.getText(_columnIndexOfIconType)
          }
          val _tmpImageUrl: String?
          if (_stmt.isNull(_columnIndexOfImageUrl)) {
            _tmpImageUrl = null
          } else {
            _tmpImageUrl = _stmt.getText(_columnIndexOfImageUrl)
          }
          val _tmpSourceType: String?
          if (_stmt.isNull(_columnIndexOfSourceType)) {
            _tmpSourceType = null
          } else {
            _tmpSourceType = _stmt.getText(_columnIndexOfSourceType)
          }
          val _tmpSourceId: String?
          if (_stmt.isNull(_columnIndexOfSourceId)) {
            _tmpSourceId = null
          } else {
            _tmpSourceId = _stmt.getText(_columnIndexOfSourceId)
          }
          val _tmpTrustPointsEarned: Int
          _tmpTrustPointsEarned = _stmt.getLong(_columnIndexOfTrustPointsEarned).toInt()
          val _tmpIsPublic: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsPublic).toInt()
          _tmpIsPublic = _tmp != 0
          val _tmpIsMilestone: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfIsMilestone).toInt()
          _tmpIsMilestone = _tmp_1 != 0
          val _tmpEventDate: Long
          _tmpEventDate = _stmt.getLong(_columnIndexOfEventDate)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpDirty: Boolean
          val _tmp_2: Int
          _tmp_2 = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp_2 != 0
          val _tmpSyncedAt: Long?
          if (_stmt.isNull(_columnIndexOfSyncedAt)) {
            _tmpSyncedAt = null
          } else {
            _tmpSyncedAt = _stmt.getLong(_columnIndexOfSyncedAt)
          }
          _item =
              FarmTimelineEventEntity(_tmpEventId,_tmpFarmerId,_tmpEventType,_tmpTitle,_tmpDescription,_tmpIconType,_tmpImageUrl,_tmpSourceType,_tmpSourceId,_tmpTrustPointsEarned,_tmpIsPublic,_tmpIsMilestone,_tmpEventDate,_tmpCreatedAt,_tmpUpdatedAt,_tmpDirty,_tmpSyncedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun setEventVisibility(
    eventId: String,
    isPublic: Boolean,
    updatedAt: Long,
  ) {
    val _sql: String =
        "UPDATE farm_timeline_events SET isPublic = ?, updatedAt = ?, dirty = 1 WHERE eventId = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        val _tmp: Int = if (isPublic) 1 else 0
        _stmt.bindLong(_argIndex, _tmp.toLong())
        _argIndex = 2
        _stmt.bindLong(_argIndex, updatedAt)
        _argIndex = 3
        _stmt.bindText(_argIndex, eventId)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun hideAllByType(
    farmerId: String,
    eventType: String,
    updatedAt: Long,
  ) {
    val _sql: String =
        "UPDATE farm_timeline_events SET isPublic = 0, updatedAt = ?, dirty = 1 WHERE farmerId = ? AND eventType = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, updatedAt)
        _argIndex = 2
        _stmt.bindText(_argIndex, farmerId)
        _argIndex = 3
        _stmt.bindText(_argIndex, eventType)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun markSynced(eventId: String, syncedAt: Long) {
    val _sql: String = "UPDATE farm_timeline_events SET dirty = 0, syncedAt = ? WHERE eventId = ?"
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

  public override suspend fun deleteAllForFarmer(farmerId: String) {
    val _sql: String = "DELETE FROM farm_timeline_events WHERE farmerId = ?"
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
