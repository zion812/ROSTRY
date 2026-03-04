package com.rio.rostry.`data`.database.dao

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.rio.rostry.`data`.database.entity.AssetLifecycleEventEntity
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

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class AssetLifecycleEventDao_Impl(
  __db: RoomDatabase,
) : AssetLifecycleEventDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfAssetLifecycleEventEntity:
      EntityInsertAdapter<AssetLifecycleEventEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfAssetLifecycleEventEntity = object :
        EntityInsertAdapter<AssetLifecycleEventEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `asset_lifecycle_events` (`eventId`,`assetId`,`farmerId`,`eventType`,`fromStage`,`toStage`,`eventData`,`triggeredBy`,`occurredAt`,`recordedAt`,`recordedBy`,`notes`,`mediaItemsJson`,`dirty`,`syncedAt`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: AssetLifecycleEventEntity) {
        statement.bindText(1, entity.eventId)
        statement.bindText(2, entity.assetId)
        statement.bindText(3, entity.farmerId)
        statement.bindText(4, entity.eventType)
        val _tmpFromStage: String? = entity.fromStage
        if (_tmpFromStage == null) {
          statement.bindNull(5)
        } else {
          statement.bindText(5, _tmpFromStage)
        }
        val _tmpToStage: String? = entity.toStage
        if (_tmpToStage == null) {
          statement.bindNull(6)
        } else {
          statement.bindText(6, _tmpToStage)
        }
        statement.bindText(7, entity.eventData)
        statement.bindText(8, entity.triggeredBy)
        statement.bindLong(9, entity.occurredAt)
        statement.bindLong(10, entity.recordedAt)
        statement.bindText(11, entity.recordedBy)
        val _tmpNotes: String? = entity.notes
        if (_tmpNotes == null) {
          statement.bindNull(12)
        } else {
          statement.bindText(12, _tmpNotes)
        }
        val _tmpMediaItemsJson: String? = entity.mediaItemsJson
        if (_tmpMediaItemsJson == null) {
          statement.bindNull(13)
        } else {
          statement.bindText(13, _tmpMediaItemsJson)
        }
        val _tmp: Int = if (entity.dirty) 1 else 0
        statement.bindLong(14, _tmp.toLong())
        val _tmpSyncedAt: Long? = entity.syncedAt
        if (_tmpSyncedAt == null) {
          statement.bindNull(15)
        } else {
          statement.bindLong(15, _tmpSyncedAt)
        }
      }
    }
  }

  public override suspend fun insert(event: AssetLifecycleEventEntity): Unit =
      performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfAssetLifecycleEventEntity.insert(_connection, event)
  }

  public override suspend fun insertAll(events: List<AssetLifecycleEventEntity>): Unit =
      performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfAssetLifecycleEventEntity.insert(_connection, events)
  }

  public override suspend fun getEventsForAsset(assetId: String): List<AssetLifecycleEventEntity> {
    val _sql: String =
        "SELECT * FROM asset_lifecycle_events WHERE assetId = ? ORDER BY occurredAt DESC"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, assetId)
        val _columnIndexOfEventId: Int = getColumnIndexOrThrow(_stmt, "eventId")
        val _columnIndexOfAssetId: Int = getColumnIndexOrThrow(_stmt, "assetId")
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfEventType: Int = getColumnIndexOrThrow(_stmt, "eventType")
        val _columnIndexOfFromStage: Int = getColumnIndexOrThrow(_stmt, "fromStage")
        val _columnIndexOfToStage: Int = getColumnIndexOrThrow(_stmt, "toStage")
        val _columnIndexOfEventData: Int = getColumnIndexOrThrow(_stmt, "eventData")
        val _columnIndexOfTriggeredBy: Int = getColumnIndexOrThrow(_stmt, "triggeredBy")
        val _columnIndexOfOccurredAt: Int = getColumnIndexOrThrow(_stmt, "occurredAt")
        val _columnIndexOfRecordedAt: Int = getColumnIndexOrThrow(_stmt, "recordedAt")
        val _columnIndexOfRecordedBy: Int = getColumnIndexOrThrow(_stmt, "recordedBy")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfMediaItemsJson: Int = getColumnIndexOrThrow(_stmt, "mediaItemsJson")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _result: MutableList<AssetLifecycleEventEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: AssetLifecycleEventEntity
          val _tmpEventId: String
          _tmpEventId = _stmt.getText(_columnIndexOfEventId)
          val _tmpAssetId: String
          _tmpAssetId = _stmt.getText(_columnIndexOfAssetId)
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpEventType: String
          _tmpEventType = _stmt.getText(_columnIndexOfEventType)
          val _tmpFromStage: String?
          if (_stmt.isNull(_columnIndexOfFromStage)) {
            _tmpFromStage = null
          } else {
            _tmpFromStage = _stmt.getText(_columnIndexOfFromStage)
          }
          val _tmpToStage: String?
          if (_stmt.isNull(_columnIndexOfToStage)) {
            _tmpToStage = null
          } else {
            _tmpToStage = _stmt.getText(_columnIndexOfToStage)
          }
          val _tmpEventData: String
          _tmpEventData = _stmt.getText(_columnIndexOfEventData)
          val _tmpTriggeredBy: String
          _tmpTriggeredBy = _stmt.getText(_columnIndexOfTriggeredBy)
          val _tmpOccurredAt: Long
          _tmpOccurredAt = _stmt.getLong(_columnIndexOfOccurredAt)
          val _tmpRecordedAt: Long
          _tmpRecordedAt = _stmt.getLong(_columnIndexOfRecordedAt)
          val _tmpRecordedBy: String
          _tmpRecordedBy = _stmt.getText(_columnIndexOfRecordedBy)
          val _tmpNotes: String?
          if (_stmt.isNull(_columnIndexOfNotes)) {
            _tmpNotes = null
          } else {
            _tmpNotes = _stmt.getText(_columnIndexOfNotes)
          }
          val _tmpMediaItemsJson: String?
          if (_stmt.isNull(_columnIndexOfMediaItemsJson)) {
            _tmpMediaItemsJson = null
          } else {
            _tmpMediaItemsJson = _stmt.getText(_columnIndexOfMediaItemsJson)
          }
          val _tmpDirty: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp != 0
          val _tmpSyncedAt: Long?
          if (_stmt.isNull(_columnIndexOfSyncedAt)) {
            _tmpSyncedAt = null
          } else {
            _tmpSyncedAt = _stmt.getLong(_columnIndexOfSyncedAt)
          }
          _item =
              AssetLifecycleEventEntity(_tmpEventId,_tmpAssetId,_tmpFarmerId,_tmpEventType,_tmpFromStage,_tmpToStage,_tmpEventData,_tmpTriggeredBy,_tmpOccurredAt,_tmpRecordedAt,_tmpRecordedBy,_tmpNotes,_tmpMediaItemsJson,_tmpDirty,_tmpSyncedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getEventById(eventId: String): AssetLifecycleEventEntity? {
    val _sql: String = "SELECT * FROM asset_lifecycle_events WHERE eventId = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, eventId)
        val _columnIndexOfEventId: Int = getColumnIndexOrThrow(_stmt, "eventId")
        val _columnIndexOfAssetId: Int = getColumnIndexOrThrow(_stmt, "assetId")
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfEventType: Int = getColumnIndexOrThrow(_stmt, "eventType")
        val _columnIndexOfFromStage: Int = getColumnIndexOrThrow(_stmt, "fromStage")
        val _columnIndexOfToStage: Int = getColumnIndexOrThrow(_stmt, "toStage")
        val _columnIndexOfEventData: Int = getColumnIndexOrThrow(_stmt, "eventData")
        val _columnIndexOfTriggeredBy: Int = getColumnIndexOrThrow(_stmt, "triggeredBy")
        val _columnIndexOfOccurredAt: Int = getColumnIndexOrThrow(_stmt, "occurredAt")
        val _columnIndexOfRecordedAt: Int = getColumnIndexOrThrow(_stmt, "recordedAt")
        val _columnIndexOfRecordedBy: Int = getColumnIndexOrThrow(_stmt, "recordedBy")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfMediaItemsJson: Int = getColumnIndexOrThrow(_stmt, "mediaItemsJson")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _result: AssetLifecycleEventEntity?
        if (_stmt.step()) {
          val _tmpEventId: String
          _tmpEventId = _stmt.getText(_columnIndexOfEventId)
          val _tmpAssetId: String
          _tmpAssetId = _stmt.getText(_columnIndexOfAssetId)
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpEventType: String
          _tmpEventType = _stmt.getText(_columnIndexOfEventType)
          val _tmpFromStage: String?
          if (_stmt.isNull(_columnIndexOfFromStage)) {
            _tmpFromStage = null
          } else {
            _tmpFromStage = _stmt.getText(_columnIndexOfFromStage)
          }
          val _tmpToStage: String?
          if (_stmt.isNull(_columnIndexOfToStage)) {
            _tmpToStage = null
          } else {
            _tmpToStage = _stmt.getText(_columnIndexOfToStage)
          }
          val _tmpEventData: String
          _tmpEventData = _stmt.getText(_columnIndexOfEventData)
          val _tmpTriggeredBy: String
          _tmpTriggeredBy = _stmt.getText(_columnIndexOfTriggeredBy)
          val _tmpOccurredAt: Long
          _tmpOccurredAt = _stmt.getLong(_columnIndexOfOccurredAt)
          val _tmpRecordedAt: Long
          _tmpRecordedAt = _stmt.getLong(_columnIndexOfRecordedAt)
          val _tmpRecordedBy: String
          _tmpRecordedBy = _stmt.getText(_columnIndexOfRecordedBy)
          val _tmpNotes: String?
          if (_stmt.isNull(_columnIndexOfNotes)) {
            _tmpNotes = null
          } else {
            _tmpNotes = _stmt.getText(_columnIndexOfNotes)
          }
          val _tmpMediaItemsJson: String?
          if (_stmt.isNull(_columnIndexOfMediaItemsJson)) {
            _tmpMediaItemsJson = null
          } else {
            _tmpMediaItemsJson = _stmt.getText(_columnIndexOfMediaItemsJson)
          }
          val _tmpDirty: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp != 0
          val _tmpSyncedAt: Long?
          if (_stmt.isNull(_columnIndexOfSyncedAt)) {
            _tmpSyncedAt = null
          } else {
            _tmpSyncedAt = _stmt.getLong(_columnIndexOfSyncedAt)
          }
          _result =
              AssetLifecycleEventEntity(_tmpEventId,_tmpAssetId,_tmpFarmerId,_tmpEventType,_tmpFromStage,_tmpToStage,_tmpEventData,_tmpTriggeredBy,_tmpOccurredAt,_tmpRecordedAt,_tmpRecordedBy,_tmpNotes,_tmpMediaItemsJson,_tmpDirty,_tmpSyncedAt)
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
