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
import com.rio.rostry.`data`.database.entity.FarmActivityLogEntity
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
import kotlin.text.StringBuilder
import kotlinx.coroutines.flow.Flow

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class FarmActivityLogDao_Impl(
  __db: RoomDatabase,
) : FarmActivityLogDao {
  private val __db: RoomDatabase

  private val __upsertAdapterOfFarmActivityLogEntity: EntityUpsertAdapter<FarmActivityLogEntity>
  init {
    this.__db = __db
    this.__upsertAdapterOfFarmActivityLogEntity = EntityUpsertAdapter<FarmActivityLogEntity>(object
        : EntityInsertAdapter<FarmActivityLogEntity>() {
      protected override fun createQuery(): String =
          "INSERT INTO `farm_activity_logs` (`activityId`,`farmerId`,`productId`,`activityType`,`amountInr`,`quantity`,`category`,`description`,`notes`,`photoUrls`,`mediaItemsJson`,`createdAt`,`updatedAt`,`dirty`,`syncedAt`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: FarmActivityLogEntity) {
        statement.bindText(1, entity.activityId)
        statement.bindText(2, entity.farmerId)
        val _tmpProductId: String? = entity.productId
        if (_tmpProductId == null) {
          statement.bindNull(3)
        } else {
          statement.bindText(3, _tmpProductId)
        }
        statement.bindText(4, entity.activityType)
        val _tmpAmountInr: Double? = entity.amountInr
        if (_tmpAmountInr == null) {
          statement.bindNull(5)
        } else {
          statement.bindDouble(5, _tmpAmountInr)
        }
        val _tmpQuantity: Double? = entity.quantity
        if (_tmpQuantity == null) {
          statement.bindNull(6)
        } else {
          statement.bindDouble(6, _tmpQuantity)
        }
        val _tmpCategory: String? = entity.category
        if (_tmpCategory == null) {
          statement.bindNull(7)
        } else {
          statement.bindText(7, _tmpCategory)
        }
        val _tmpDescription: String? = entity.description
        if (_tmpDescription == null) {
          statement.bindNull(8)
        } else {
          statement.bindText(8, _tmpDescription)
        }
        val _tmpNotes: String? = entity.notes
        if (_tmpNotes == null) {
          statement.bindNull(9)
        } else {
          statement.bindText(9, _tmpNotes)
        }
        val _tmpPhotoUrls: String? = entity.photoUrls
        if (_tmpPhotoUrls == null) {
          statement.bindNull(10)
        } else {
          statement.bindText(10, _tmpPhotoUrls)
        }
        val _tmpMediaItemsJson: String? = entity.mediaItemsJson
        if (_tmpMediaItemsJson == null) {
          statement.bindNull(11)
        } else {
          statement.bindText(11, _tmpMediaItemsJson)
        }
        statement.bindLong(12, entity.createdAt)
        statement.bindLong(13, entity.updatedAt)
        val _tmp: Int = if (entity.dirty) 1 else 0
        statement.bindLong(14, _tmp.toLong())
        val _tmpSyncedAt: Long? = entity.syncedAt
        if (_tmpSyncedAt == null) {
          statement.bindNull(15)
        } else {
          statement.bindLong(15, _tmpSyncedAt)
        }
      }
    }, object : EntityDeleteOrUpdateAdapter<FarmActivityLogEntity>() {
      protected override fun createQuery(): String =
          "UPDATE `farm_activity_logs` SET `activityId` = ?,`farmerId` = ?,`productId` = ?,`activityType` = ?,`amountInr` = ?,`quantity` = ?,`category` = ?,`description` = ?,`notes` = ?,`photoUrls` = ?,`mediaItemsJson` = ?,`createdAt` = ?,`updatedAt` = ?,`dirty` = ?,`syncedAt` = ? WHERE `activityId` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: FarmActivityLogEntity) {
        statement.bindText(1, entity.activityId)
        statement.bindText(2, entity.farmerId)
        val _tmpProductId: String? = entity.productId
        if (_tmpProductId == null) {
          statement.bindNull(3)
        } else {
          statement.bindText(3, _tmpProductId)
        }
        statement.bindText(4, entity.activityType)
        val _tmpAmountInr: Double? = entity.amountInr
        if (_tmpAmountInr == null) {
          statement.bindNull(5)
        } else {
          statement.bindDouble(5, _tmpAmountInr)
        }
        val _tmpQuantity: Double? = entity.quantity
        if (_tmpQuantity == null) {
          statement.bindNull(6)
        } else {
          statement.bindDouble(6, _tmpQuantity)
        }
        val _tmpCategory: String? = entity.category
        if (_tmpCategory == null) {
          statement.bindNull(7)
        } else {
          statement.bindText(7, _tmpCategory)
        }
        val _tmpDescription: String? = entity.description
        if (_tmpDescription == null) {
          statement.bindNull(8)
        } else {
          statement.bindText(8, _tmpDescription)
        }
        val _tmpNotes: String? = entity.notes
        if (_tmpNotes == null) {
          statement.bindNull(9)
        } else {
          statement.bindText(9, _tmpNotes)
        }
        val _tmpPhotoUrls: String? = entity.photoUrls
        if (_tmpPhotoUrls == null) {
          statement.bindNull(10)
        } else {
          statement.bindText(10, _tmpPhotoUrls)
        }
        val _tmpMediaItemsJson: String? = entity.mediaItemsJson
        if (_tmpMediaItemsJson == null) {
          statement.bindNull(11)
        } else {
          statement.bindText(11, _tmpMediaItemsJson)
        }
        statement.bindLong(12, entity.createdAt)
        statement.bindLong(13, entity.updatedAt)
        val _tmp: Int = if (entity.dirty) 1 else 0
        statement.bindLong(14, _tmp.toLong())
        val _tmpSyncedAt: Long? = entity.syncedAt
        if (_tmpSyncedAt == null) {
          statement.bindNull(15)
        } else {
          statement.bindLong(15, _tmpSyncedAt)
        }
        statement.bindText(16, entity.activityId)
      }
    })
  }

  public override suspend fun upsert(log: FarmActivityLogEntity): Unit = performSuspending(__db,
      false, true) { _connection ->
    __upsertAdapterOfFarmActivityLogEntity.upsert(_connection, log)
  }

  public override suspend fun upsertAll(logs: List<FarmActivityLogEntity>): Unit =
      performSuspending(__db, false, true) { _connection ->
    __upsertAdapterOfFarmActivityLogEntity.upsert(_connection, logs)
  }

  public override fun observeForFarmer(farmerId: String): Flow<List<FarmActivityLogEntity>> {
    val _sql: String = "SELECT * FROM farm_activity_logs WHERE farmerId = ? ORDER BY createdAt DESC"
    return createFlow(__db, false, arrayOf("farm_activity_logs")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, farmerId)
        val _columnIndexOfActivityId: Int = getColumnIndexOrThrow(_stmt, "activityId")
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfActivityType: Int = getColumnIndexOrThrow(_stmt, "activityType")
        val _columnIndexOfAmountInr: Int = getColumnIndexOrThrow(_stmt, "amountInr")
        val _columnIndexOfQuantity: Int = getColumnIndexOrThrow(_stmt, "quantity")
        val _columnIndexOfCategory: Int = getColumnIndexOrThrow(_stmt, "category")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfPhotoUrls: Int = getColumnIndexOrThrow(_stmt, "photoUrls")
        val _columnIndexOfMediaItemsJson: Int = getColumnIndexOrThrow(_stmt, "mediaItemsJson")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _result: MutableList<FarmActivityLogEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: FarmActivityLogEntity
          val _tmpActivityId: String
          _tmpActivityId = _stmt.getText(_columnIndexOfActivityId)
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpProductId: String?
          if (_stmt.isNull(_columnIndexOfProductId)) {
            _tmpProductId = null
          } else {
            _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          }
          val _tmpActivityType: String
          _tmpActivityType = _stmt.getText(_columnIndexOfActivityType)
          val _tmpAmountInr: Double?
          if (_stmt.isNull(_columnIndexOfAmountInr)) {
            _tmpAmountInr = null
          } else {
            _tmpAmountInr = _stmt.getDouble(_columnIndexOfAmountInr)
          }
          val _tmpQuantity: Double?
          if (_stmt.isNull(_columnIndexOfQuantity)) {
            _tmpQuantity = null
          } else {
            _tmpQuantity = _stmt.getDouble(_columnIndexOfQuantity)
          }
          val _tmpCategory: String?
          if (_stmt.isNull(_columnIndexOfCategory)) {
            _tmpCategory = null
          } else {
            _tmpCategory = _stmt.getText(_columnIndexOfCategory)
          }
          val _tmpDescription: String?
          if (_stmt.isNull(_columnIndexOfDescription)) {
            _tmpDescription = null
          } else {
            _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          }
          val _tmpNotes: String?
          if (_stmt.isNull(_columnIndexOfNotes)) {
            _tmpNotes = null
          } else {
            _tmpNotes = _stmt.getText(_columnIndexOfNotes)
          }
          val _tmpPhotoUrls: String?
          if (_stmt.isNull(_columnIndexOfPhotoUrls)) {
            _tmpPhotoUrls = null
          } else {
            _tmpPhotoUrls = _stmt.getText(_columnIndexOfPhotoUrls)
          }
          val _tmpMediaItemsJson: String?
          if (_stmt.isNull(_columnIndexOfMediaItemsJson)) {
            _tmpMediaItemsJson = null
          } else {
            _tmpMediaItemsJson = _stmt.getText(_columnIndexOfMediaItemsJson)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
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
              FarmActivityLogEntity(_tmpActivityId,_tmpFarmerId,_tmpProductId,_tmpActivityType,_tmpAmountInr,_tmpQuantity,_tmpCategory,_tmpDescription,_tmpNotes,_tmpPhotoUrls,_tmpMediaItemsJson,_tmpCreatedAt,_tmpUpdatedAt,_tmpDirty,_tmpSyncedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeLatestForFarmer(farmerId: String): Flow<FarmActivityLogEntity?> {
    val _sql: String =
        "SELECT * FROM farm_activity_logs WHERE farmerId = ? ORDER BY createdAt DESC LIMIT 1"
    return createFlow(__db, false, arrayOf("farm_activity_logs")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, farmerId)
        val _columnIndexOfActivityId: Int = getColumnIndexOrThrow(_stmt, "activityId")
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfActivityType: Int = getColumnIndexOrThrow(_stmt, "activityType")
        val _columnIndexOfAmountInr: Int = getColumnIndexOrThrow(_stmt, "amountInr")
        val _columnIndexOfQuantity: Int = getColumnIndexOrThrow(_stmt, "quantity")
        val _columnIndexOfCategory: Int = getColumnIndexOrThrow(_stmt, "category")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfPhotoUrls: Int = getColumnIndexOrThrow(_stmt, "photoUrls")
        val _columnIndexOfMediaItemsJson: Int = getColumnIndexOrThrow(_stmt, "mediaItemsJson")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _result: FarmActivityLogEntity?
        if (_stmt.step()) {
          val _tmpActivityId: String
          _tmpActivityId = _stmt.getText(_columnIndexOfActivityId)
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpProductId: String?
          if (_stmt.isNull(_columnIndexOfProductId)) {
            _tmpProductId = null
          } else {
            _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          }
          val _tmpActivityType: String
          _tmpActivityType = _stmt.getText(_columnIndexOfActivityType)
          val _tmpAmountInr: Double?
          if (_stmt.isNull(_columnIndexOfAmountInr)) {
            _tmpAmountInr = null
          } else {
            _tmpAmountInr = _stmt.getDouble(_columnIndexOfAmountInr)
          }
          val _tmpQuantity: Double?
          if (_stmt.isNull(_columnIndexOfQuantity)) {
            _tmpQuantity = null
          } else {
            _tmpQuantity = _stmt.getDouble(_columnIndexOfQuantity)
          }
          val _tmpCategory: String?
          if (_stmt.isNull(_columnIndexOfCategory)) {
            _tmpCategory = null
          } else {
            _tmpCategory = _stmt.getText(_columnIndexOfCategory)
          }
          val _tmpDescription: String?
          if (_stmt.isNull(_columnIndexOfDescription)) {
            _tmpDescription = null
          } else {
            _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          }
          val _tmpNotes: String?
          if (_stmt.isNull(_columnIndexOfNotes)) {
            _tmpNotes = null
          } else {
            _tmpNotes = _stmt.getText(_columnIndexOfNotes)
          }
          val _tmpPhotoUrls: String?
          if (_stmt.isNull(_columnIndexOfPhotoUrls)) {
            _tmpPhotoUrls = null
          } else {
            _tmpPhotoUrls = _stmt.getText(_columnIndexOfPhotoUrls)
          }
          val _tmpMediaItemsJson: String?
          if (_stmt.isNull(_columnIndexOfMediaItemsJson)) {
            _tmpMediaItemsJson = null
          } else {
            _tmpMediaItemsJson = _stmt.getText(_columnIndexOfMediaItemsJson)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
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
              FarmActivityLogEntity(_tmpActivityId,_tmpFarmerId,_tmpProductId,_tmpActivityType,_tmpAmountInr,_tmpQuantity,_tmpCategory,_tmpDescription,_tmpNotes,_tmpPhotoUrls,_tmpMediaItemsJson,_tmpCreatedAt,_tmpUpdatedAt,_tmpDirty,_tmpSyncedAt)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeForFarmerByType(farmerId: String, type: String):
      Flow<List<FarmActivityLogEntity>> {
    val _sql: String =
        "SELECT * FROM farm_activity_logs WHERE farmerId = ? AND activityType = ? ORDER BY createdAt DESC"
    return createFlow(__db, false, arrayOf("farm_activity_logs")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, farmerId)
        _argIndex = 2
        _stmt.bindText(_argIndex, type)
        val _columnIndexOfActivityId: Int = getColumnIndexOrThrow(_stmt, "activityId")
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfActivityType: Int = getColumnIndexOrThrow(_stmt, "activityType")
        val _columnIndexOfAmountInr: Int = getColumnIndexOrThrow(_stmt, "amountInr")
        val _columnIndexOfQuantity: Int = getColumnIndexOrThrow(_stmt, "quantity")
        val _columnIndexOfCategory: Int = getColumnIndexOrThrow(_stmt, "category")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfPhotoUrls: Int = getColumnIndexOrThrow(_stmt, "photoUrls")
        val _columnIndexOfMediaItemsJson: Int = getColumnIndexOrThrow(_stmt, "mediaItemsJson")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _result: MutableList<FarmActivityLogEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: FarmActivityLogEntity
          val _tmpActivityId: String
          _tmpActivityId = _stmt.getText(_columnIndexOfActivityId)
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpProductId: String?
          if (_stmt.isNull(_columnIndexOfProductId)) {
            _tmpProductId = null
          } else {
            _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          }
          val _tmpActivityType: String
          _tmpActivityType = _stmt.getText(_columnIndexOfActivityType)
          val _tmpAmountInr: Double?
          if (_stmt.isNull(_columnIndexOfAmountInr)) {
            _tmpAmountInr = null
          } else {
            _tmpAmountInr = _stmt.getDouble(_columnIndexOfAmountInr)
          }
          val _tmpQuantity: Double?
          if (_stmt.isNull(_columnIndexOfQuantity)) {
            _tmpQuantity = null
          } else {
            _tmpQuantity = _stmt.getDouble(_columnIndexOfQuantity)
          }
          val _tmpCategory: String?
          if (_stmt.isNull(_columnIndexOfCategory)) {
            _tmpCategory = null
          } else {
            _tmpCategory = _stmt.getText(_columnIndexOfCategory)
          }
          val _tmpDescription: String?
          if (_stmt.isNull(_columnIndexOfDescription)) {
            _tmpDescription = null
          } else {
            _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          }
          val _tmpNotes: String?
          if (_stmt.isNull(_columnIndexOfNotes)) {
            _tmpNotes = null
          } else {
            _tmpNotes = _stmt.getText(_columnIndexOfNotes)
          }
          val _tmpPhotoUrls: String?
          if (_stmt.isNull(_columnIndexOfPhotoUrls)) {
            _tmpPhotoUrls = null
          } else {
            _tmpPhotoUrls = _stmt.getText(_columnIndexOfPhotoUrls)
          }
          val _tmpMediaItemsJson: String?
          if (_stmt.isNull(_columnIndexOfMediaItemsJson)) {
            _tmpMediaItemsJson = null
          } else {
            _tmpMediaItemsJson = _stmt.getText(_columnIndexOfMediaItemsJson)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
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
              FarmActivityLogEntity(_tmpActivityId,_tmpFarmerId,_tmpProductId,_tmpActivityType,_tmpAmountInr,_tmpQuantity,_tmpCategory,_tmpDescription,_tmpNotes,_tmpPhotoUrls,_tmpMediaItemsJson,_tmpCreatedAt,_tmpUpdatedAt,_tmpDirty,_tmpSyncedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeForFarmerBetween(
    farmerId: String,
    start: Long,
    end: Long,
  ): Flow<List<FarmActivityLogEntity>> {
    val _sql: String =
        "SELECT * FROM farm_activity_logs WHERE farmerId = ? AND createdAt BETWEEN ? AND ? ORDER BY createdAt DESC"
    return createFlow(__db, false, arrayOf("farm_activity_logs")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, farmerId)
        _argIndex = 2
        _stmt.bindLong(_argIndex, start)
        _argIndex = 3
        _stmt.bindLong(_argIndex, end)
        val _columnIndexOfActivityId: Int = getColumnIndexOrThrow(_stmt, "activityId")
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfActivityType: Int = getColumnIndexOrThrow(_stmt, "activityType")
        val _columnIndexOfAmountInr: Int = getColumnIndexOrThrow(_stmt, "amountInr")
        val _columnIndexOfQuantity: Int = getColumnIndexOrThrow(_stmt, "quantity")
        val _columnIndexOfCategory: Int = getColumnIndexOrThrow(_stmt, "category")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfPhotoUrls: Int = getColumnIndexOrThrow(_stmt, "photoUrls")
        val _columnIndexOfMediaItemsJson: Int = getColumnIndexOrThrow(_stmt, "mediaItemsJson")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _result: MutableList<FarmActivityLogEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: FarmActivityLogEntity
          val _tmpActivityId: String
          _tmpActivityId = _stmt.getText(_columnIndexOfActivityId)
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpProductId: String?
          if (_stmt.isNull(_columnIndexOfProductId)) {
            _tmpProductId = null
          } else {
            _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          }
          val _tmpActivityType: String
          _tmpActivityType = _stmt.getText(_columnIndexOfActivityType)
          val _tmpAmountInr: Double?
          if (_stmt.isNull(_columnIndexOfAmountInr)) {
            _tmpAmountInr = null
          } else {
            _tmpAmountInr = _stmt.getDouble(_columnIndexOfAmountInr)
          }
          val _tmpQuantity: Double?
          if (_stmt.isNull(_columnIndexOfQuantity)) {
            _tmpQuantity = null
          } else {
            _tmpQuantity = _stmt.getDouble(_columnIndexOfQuantity)
          }
          val _tmpCategory: String?
          if (_stmt.isNull(_columnIndexOfCategory)) {
            _tmpCategory = null
          } else {
            _tmpCategory = _stmt.getText(_columnIndexOfCategory)
          }
          val _tmpDescription: String?
          if (_stmt.isNull(_columnIndexOfDescription)) {
            _tmpDescription = null
          } else {
            _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          }
          val _tmpNotes: String?
          if (_stmt.isNull(_columnIndexOfNotes)) {
            _tmpNotes = null
          } else {
            _tmpNotes = _stmt.getText(_columnIndexOfNotes)
          }
          val _tmpPhotoUrls: String?
          if (_stmt.isNull(_columnIndexOfPhotoUrls)) {
            _tmpPhotoUrls = null
          } else {
            _tmpPhotoUrls = _stmt.getText(_columnIndexOfPhotoUrls)
          }
          val _tmpMediaItemsJson: String?
          if (_stmt.isNull(_columnIndexOfMediaItemsJson)) {
            _tmpMediaItemsJson = null
          } else {
            _tmpMediaItemsJson = _stmt.getText(_columnIndexOfMediaItemsJson)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
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
              FarmActivityLogEntity(_tmpActivityId,_tmpFarmerId,_tmpProductId,_tmpActivityType,_tmpAmountInr,_tmpQuantity,_tmpCategory,_tmpDescription,_tmpNotes,_tmpPhotoUrls,_tmpMediaItemsJson,_tmpCreatedAt,_tmpUpdatedAt,_tmpDirty,_tmpSyncedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeForProduct(productId: String): Flow<List<FarmActivityLogEntity>> {
    val _sql: String =
        "SELECT * FROM farm_activity_logs WHERE productId = ? ORDER BY createdAt DESC"
    return createFlow(__db, false, arrayOf("farm_activity_logs")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, productId)
        val _columnIndexOfActivityId: Int = getColumnIndexOrThrow(_stmt, "activityId")
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfActivityType: Int = getColumnIndexOrThrow(_stmt, "activityType")
        val _columnIndexOfAmountInr: Int = getColumnIndexOrThrow(_stmt, "amountInr")
        val _columnIndexOfQuantity: Int = getColumnIndexOrThrow(_stmt, "quantity")
        val _columnIndexOfCategory: Int = getColumnIndexOrThrow(_stmt, "category")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfPhotoUrls: Int = getColumnIndexOrThrow(_stmt, "photoUrls")
        val _columnIndexOfMediaItemsJson: Int = getColumnIndexOrThrow(_stmt, "mediaItemsJson")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _result: MutableList<FarmActivityLogEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: FarmActivityLogEntity
          val _tmpActivityId: String
          _tmpActivityId = _stmt.getText(_columnIndexOfActivityId)
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpProductId: String?
          if (_stmt.isNull(_columnIndexOfProductId)) {
            _tmpProductId = null
          } else {
            _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          }
          val _tmpActivityType: String
          _tmpActivityType = _stmt.getText(_columnIndexOfActivityType)
          val _tmpAmountInr: Double?
          if (_stmt.isNull(_columnIndexOfAmountInr)) {
            _tmpAmountInr = null
          } else {
            _tmpAmountInr = _stmt.getDouble(_columnIndexOfAmountInr)
          }
          val _tmpQuantity: Double?
          if (_stmt.isNull(_columnIndexOfQuantity)) {
            _tmpQuantity = null
          } else {
            _tmpQuantity = _stmt.getDouble(_columnIndexOfQuantity)
          }
          val _tmpCategory: String?
          if (_stmt.isNull(_columnIndexOfCategory)) {
            _tmpCategory = null
          } else {
            _tmpCategory = _stmt.getText(_columnIndexOfCategory)
          }
          val _tmpDescription: String?
          if (_stmt.isNull(_columnIndexOfDescription)) {
            _tmpDescription = null
          } else {
            _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          }
          val _tmpNotes: String?
          if (_stmt.isNull(_columnIndexOfNotes)) {
            _tmpNotes = null
          } else {
            _tmpNotes = _stmt.getText(_columnIndexOfNotes)
          }
          val _tmpPhotoUrls: String?
          if (_stmt.isNull(_columnIndexOfPhotoUrls)) {
            _tmpPhotoUrls = null
          } else {
            _tmpPhotoUrls = _stmt.getText(_columnIndexOfPhotoUrls)
          }
          val _tmpMediaItemsJson: String?
          if (_stmt.isNull(_columnIndexOfMediaItemsJson)) {
            _tmpMediaItemsJson = null
          } else {
            _tmpMediaItemsJson = _stmt.getText(_columnIndexOfMediaItemsJson)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
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
              FarmActivityLogEntity(_tmpActivityId,_tmpFarmerId,_tmpProductId,_tmpActivityType,_tmpAmountInr,_tmpQuantity,_tmpCategory,_tmpDescription,_tmpNotes,_tmpPhotoUrls,_tmpMediaItemsJson,_tmpCreatedAt,_tmpUpdatedAt,_tmpDirty,_tmpSyncedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getTotalExpensesBetween(
    farmerId: String,
    start: Long,
    end: Long,
  ): Double? {
    val _sql: String =
        "SELECT SUM(amountInr) FROM farm_activity_logs WHERE farmerId = ? AND activityType = 'EXPENSE' AND createdAt BETWEEN ? AND ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, farmerId)
        _argIndex = 2
        _stmt.bindLong(_argIndex, start)
        _argIndex = 3
        _stmt.bindLong(_argIndex, end)
        val _result: Double?
        if (_stmt.step()) {
          val _tmp: Double?
          if (_stmt.isNull(0)) {
            _tmp = null
          } else {
            _tmp = _stmt.getDouble(0)
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

  public override suspend fun getById(activityId: String): FarmActivityLogEntity? {
    val _sql: String = "SELECT * FROM farm_activity_logs WHERE activityId = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, activityId)
        val _columnIndexOfActivityId: Int = getColumnIndexOrThrow(_stmt, "activityId")
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfActivityType: Int = getColumnIndexOrThrow(_stmt, "activityType")
        val _columnIndexOfAmountInr: Int = getColumnIndexOrThrow(_stmt, "amountInr")
        val _columnIndexOfQuantity: Int = getColumnIndexOrThrow(_stmt, "quantity")
        val _columnIndexOfCategory: Int = getColumnIndexOrThrow(_stmt, "category")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfPhotoUrls: Int = getColumnIndexOrThrow(_stmt, "photoUrls")
        val _columnIndexOfMediaItemsJson: Int = getColumnIndexOrThrow(_stmt, "mediaItemsJson")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _result: FarmActivityLogEntity?
        if (_stmt.step()) {
          val _tmpActivityId: String
          _tmpActivityId = _stmt.getText(_columnIndexOfActivityId)
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpProductId: String?
          if (_stmt.isNull(_columnIndexOfProductId)) {
            _tmpProductId = null
          } else {
            _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          }
          val _tmpActivityType: String
          _tmpActivityType = _stmt.getText(_columnIndexOfActivityType)
          val _tmpAmountInr: Double?
          if (_stmt.isNull(_columnIndexOfAmountInr)) {
            _tmpAmountInr = null
          } else {
            _tmpAmountInr = _stmt.getDouble(_columnIndexOfAmountInr)
          }
          val _tmpQuantity: Double?
          if (_stmt.isNull(_columnIndexOfQuantity)) {
            _tmpQuantity = null
          } else {
            _tmpQuantity = _stmt.getDouble(_columnIndexOfQuantity)
          }
          val _tmpCategory: String?
          if (_stmt.isNull(_columnIndexOfCategory)) {
            _tmpCategory = null
          } else {
            _tmpCategory = _stmt.getText(_columnIndexOfCategory)
          }
          val _tmpDescription: String?
          if (_stmt.isNull(_columnIndexOfDescription)) {
            _tmpDescription = null
          } else {
            _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          }
          val _tmpNotes: String?
          if (_stmt.isNull(_columnIndexOfNotes)) {
            _tmpNotes = null
          } else {
            _tmpNotes = _stmt.getText(_columnIndexOfNotes)
          }
          val _tmpPhotoUrls: String?
          if (_stmt.isNull(_columnIndexOfPhotoUrls)) {
            _tmpPhotoUrls = null
          } else {
            _tmpPhotoUrls = _stmt.getText(_columnIndexOfPhotoUrls)
          }
          val _tmpMediaItemsJson: String?
          if (_stmt.isNull(_columnIndexOfMediaItemsJson)) {
            _tmpMediaItemsJson = null
          } else {
            _tmpMediaItemsJson = _stmt.getText(_columnIndexOfMediaItemsJson)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
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
              FarmActivityLogEntity(_tmpActivityId,_tmpFarmerId,_tmpProductId,_tmpActivityType,_tmpAmountInr,_tmpQuantity,_tmpCategory,_tmpDescription,_tmpNotes,_tmpPhotoUrls,_tmpMediaItemsJson,_tmpCreatedAt,_tmpUpdatedAt,_tmpDirty,_tmpSyncedAt)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getDirty(farmerId: String): List<FarmActivityLogEntity> {
    val _sql: String = "SELECT * FROM farm_activity_logs WHERE dirty = 1 AND farmerId = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, farmerId)
        val _columnIndexOfActivityId: Int = getColumnIndexOrThrow(_stmt, "activityId")
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfActivityType: Int = getColumnIndexOrThrow(_stmt, "activityType")
        val _columnIndexOfAmountInr: Int = getColumnIndexOrThrow(_stmt, "amountInr")
        val _columnIndexOfQuantity: Int = getColumnIndexOrThrow(_stmt, "quantity")
        val _columnIndexOfCategory: Int = getColumnIndexOrThrow(_stmt, "category")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfPhotoUrls: Int = getColumnIndexOrThrow(_stmt, "photoUrls")
        val _columnIndexOfMediaItemsJson: Int = getColumnIndexOrThrow(_stmt, "mediaItemsJson")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _result: MutableList<FarmActivityLogEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: FarmActivityLogEntity
          val _tmpActivityId: String
          _tmpActivityId = _stmt.getText(_columnIndexOfActivityId)
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpProductId: String?
          if (_stmt.isNull(_columnIndexOfProductId)) {
            _tmpProductId = null
          } else {
            _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          }
          val _tmpActivityType: String
          _tmpActivityType = _stmt.getText(_columnIndexOfActivityType)
          val _tmpAmountInr: Double?
          if (_stmt.isNull(_columnIndexOfAmountInr)) {
            _tmpAmountInr = null
          } else {
            _tmpAmountInr = _stmt.getDouble(_columnIndexOfAmountInr)
          }
          val _tmpQuantity: Double?
          if (_stmt.isNull(_columnIndexOfQuantity)) {
            _tmpQuantity = null
          } else {
            _tmpQuantity = _stmt.getDouble(_columnIndexOfQuantity)
          }
          val _tmpCategory: String?
          if (_stmt.isNull(_columnIndexOfCategory)) {
            _tmpCategory = null
          } else {
            _tmpCategory = _stmt.getText(_columnIndexOfCategory)
          }
          val _tmpDescription: String?
          if (_stmt.isNull(_columnIndexOfDescription)) {
            _tmpDescription = null
          } else {
            _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          }
          val _tmpNotes: String?
          if (_stmt.isNull(_columnIndexOfNotes)) {
            _tmpNotes = null
          } else {
            _tmpNotes = _stmt.getText(_columnIndexOfNotes)
          }
          val _tmpPhotoUrls: String?
          if (_stmt.isNull(_columnIndexOfPhotoUrls)) {
            _tmpPhotoUrls = null
          } else {
            _tmpPhotoUrls = _stmt.getText(_columnIndexOfPhotoUrls)
          }
          val _tmpMediaItemsJson: String?
          if (_stmt.isNull(_columnIndexOfMediaItemsJson)) {
            _tmpMediaItemsJson = null
          } else {
            _tmpMediaItemsJson = _stmt.getText(_columnIndexOfMediaItemsJson)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
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
              FarmActivityLogEntity(_tmpActivityId,_tmpFarmerId,_tmpProductId,_tmpActivityType,_tmpAmountInr,_tmpQuantity,_tmpCategory,_tmpDescription,_tmpNotes,_tmpPhotoUrls,_tmpMediaItemsJson,_tmpCreatedAt,_tmpUpdatedAt,_tmpDirty,_tmpSyncedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getCountForFarmer(farmerId: String): Int {
    val _sql: String = "SELECT COUNT(*) FROM farm_activity_logs WHERE farmerId = ?"
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

  public override suspend fun getTotalExpensesForAsset(
    assetId: String,
    startDate: Long,
    endDate: Long,
  ): Double {
    val _sql: String =
        "SELECT COALESCE(SUM(amountInr), 0.0) FROM farm_activity_logs WHERE productId = ? AND activityType IN ('EXPENSE', 'FEED', 'MEDICATION') AND createdAt BETWEEN ? AND ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, assetId)
        _argIndex = 2
        _stmt.bindLong(_argIndex, startDate)
        _argIndex = 3
        _stmt.bindLong(_argIndex, endDate)
        val _result: Double
        if (_stmt.step()) {
          val _tmp: Double
          _tmp = _stmt.getDouble(0)
          _result = _tmp
        } else {
          _result = 0.0
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getTotalExpensesForAssetAllTime(assetId: String): Double {
    val _sql: String =
        "SELECT COALESCE(SUM(amountInr), 0.0) FROM farm_activity_logs WHERE productId = ? AND activityType IN ('EXPENSE', 'FEED', 'MEDICATION')"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, assetId)
        val _result: Double
        if (_stmt.step()) {
          val _tmp: Double
          _tmp = _stmt.getDouble(0)
          _result = _tmp
        } else {
          _result = 0.0
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getTotalFeedExpensesForAsset(assetId: String): Double {
    val _sql: String =
        "SELECT COALESCE(SUM(amountInr), 0.0) FROM farm_activity_logs WHERE productId = ? AND activityType = 'FEED'"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, assetId)
        val _result: Double
        if (_stmt.step()) {
          val _tmp: Double
          _tmp = _stmt.getDouble(0)
          _result = _tmp
        } else {
          _result = 0.0
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getTotalMedicationExpensesForAsset(assetId: String): Double {
    val _sql: String =
        "SELECT COALESCE(SUM(amountInr), 0.0) FROM farm_activity_logs WHERE productId = ? AND activityType = 'MEDICATION'"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, assetId)
        val _result: Double
        if (_stmt.step()) {
          val _tmp: Double
          _tmp = _stmt.getDouble(0)
          _result = _tmp
        } else {
          _result = 0.0
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getTotalOtherExpensesForAsset(assetId: String): Double {
    val _sql: String =
        "SELECT COALESCE(SUM(amountInr), 0.0) FROM farm_activity_logs WHERE productId = ? AND activityType = 'EXPENSE'"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, assetId)
        val _result: Double
        if (_stmt.step()) {
          val _tmp: Double
          _tmp = _stmt.getDouble(0)
          _result = _tmp
        } else {
          _result = 0.0
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getTotalFeedQuantityForAsset(assetId: String): Double {
    val _sql: String =
        "SELECT COALESCE(SUM(quantity), 0.0) FROM farm_activity_logs WHERE productId = ? AND activityType = 'FEED'"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, assetId)
        val _result: Double
        if (_stmt.step()) {
          val _tmp: Double
          _tmp = _stmt.getDouble(0)
          _result = _tmp
        } else {
          _result = 0.0
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getTotalFeedExpensesByFarmer(farmerId: String): Double {
    val _sql: String =
        "SELECT COALESCE(SUM(amountInr), 0.0) FROM farm_activity_logs WHERE farmerId = ? AND activityType = 'FEED'"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, farmerId)
        val _result: Double
        if (_stmt.step()) {
          val _tmp: Double
          _tmp = _stmt.getDouble(0)
          _result = _tmp
        } else {
          _result = 0.0
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getTotalMedicationExpensesByFarmer(farmerId: String): Double {
    val _sql: String =
        "SELECT COALESCE(SUM(amountInr), 0.0) FROM farm_activity_logs WHERE farmerId = ? AND activityType = 'MEDICATION'"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, farmerId)
        val _result: Double
        if (_stmt.step()) {
          val _tmp: Double
          _tmp = _stmt.getDouble(0)
          _result = _tmp
        } else {
          _result = 0.0
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getTotalOtherExpensesByFarmer(farmerId: String): Double {
    val _sql: String =
        "SELECT COALESCE(SUM(amountInr), 0.0) FROM farm_activity_logs WHERE farmerId = ? AND activityType = 'EXPENSE'"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, farmerId)
        val _result: Double
        if (_stmt.step()) {
          val _tmp: Double
          _tmp = _stmt.getDouble(0)
          _result = _tmp
        } else {
          _result = 0.0
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getTotalExpensesByFarmerBetween(
    farmerId: String,
    start: Long,
    end: Long,
  ): Double {
    val _sql: String =
        "SELECT COALESCE(SUM(amountInr), 0.0) FROM farm_activity_logs WHERE farmerId = ? AND createdAt BETWEEN ? AND ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, farmerId)
        _argIndex = 2
        _stmt.bindLong(_argIndex, start)
        _argIndex = 3
        _stmt.bindLong(_argIndex, end)
        val _result: Double
        if (_stmt.step()) {
          val _tmp: Double
          _tmp = _stmt.getDouble(0)
          _result = _tmp
        } else {
          _result = 0.0
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getTotalFeedExpensesByFarmerBetween(
    farmerId: String,
    start: Long,
    end: Long,
  ): Double {
    val _sql: String =
        "SELECT COALESCE(SUM(amountInr), 0.0) FROM farm_activity_logs WHERE farmerId = ? AND activityType = 'FEED' AND createdAt BETWEEN ? AND ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, farmerId)
        _argIndex = 2
        _stmt.bindLong(_argIndex, start)
        _argIndex = 3
        _stmt.bindLong(_argIndex, end)
        val _result: Double
        if (_stmt.step()) {
          val _tmp: Double
          _tmp = _stmt.getDouble(0)
          _result = _tmp
        } else {
          _result = 0.0
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getTotalMedicationExpensesByFarmerBetween(
    farmerId: String,
    start: Long,
    end: Long,
  ): Double {
    val _sql: String =
        "SELECT COALESCE(SUM(amountInr), 0.0) FROM farm_activity_logs WHERE farmerId = ? AND activityType = 'MEDICATION' AND createdAt BETWEEN ? AND ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, farmerId)
        _argIndex = 2
        _stmt.bindLong(_argIndex, start)
        _argIndex = 3
        _stmt.bindLong(_argIndex, end)
        val _result: Double
        if (_stmt.step()) {
          val _tmp: Double
          _tmp = _stmt.getDouble(0)
          _result = _tmp
        } else {
          _result = 0.0
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getTotalOtherExpensesByFarmerBetween(
    farmerId: String,
    start: Long,
    end: Long,
  ): Double {
    val _sql: String =
        "SELECT COALESCE(SUM(amountInr), 0.0) FROM farm_activity_logs WHERE farmerId = ? AND activityType = 'EXPENSE' AND createdAt BETWEEN ? AND ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, farmerId)
        _argIndex = 2
        _stmt.bindLong(_argIndex, start)
        _argIndex = 3
        _stmt.bindLong(_argIndex, end)
        val _result: Double
        if (_stmt.step()) {
          val _tmp: Double
          _tmp = _stmt.getDouble(0)
          _result = _tmp
        } else {
          _result = 0.0
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun markSynced(ids: List<String>, syncedAt: Long) {
    val _stringBuilder: StringBuilder = StringBuilder()
    _stringBuilder.append("UPDATE farm_activity_logs SET dirty = 0, syncedAt = ")
    _stringBuilder.append("?")
    _stringBuilder.append(" WHERE activityId IN (")
    val _inputSize: Int = ids.size
    appendPlaceholders(_stringBuilder, _inputSize)
    _stringBuilder.append(")")
    val _sql: String = _stringBuilder.toString()
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, syncedAt)
        _argIndex = 2
        for (_item: String in ids) {
          _stmt.bindText(_argIndex, _item)
          _argIndex++
        }
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun delete(activityId: String) {
    val _sql: String = "DELETE FROM farm_activity_logs WHERE activityId = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, activityId)
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
