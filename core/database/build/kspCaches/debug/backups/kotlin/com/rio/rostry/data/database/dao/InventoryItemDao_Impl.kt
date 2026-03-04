package com.rio.rostry.`data`.database.dao

import androidx.room.EntityDeleteOrUpdateAdapter
import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.rio.rostry.`data`.database.entity.InventoryItemEntity
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
public class InventoryItemDao_Impl(
  __db: RoomDatabase,
) : InventoryItemDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfInventoryItemEntity: EntityInsertAdapter<InventoryItemEntity>

  private val __updateAdapterOfInventoryItemEntity: EntityDeleteOrUpdateAdapter<InventoryItemEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfInventoryItemEntity = object : EntityInsertAdapter<InventoryItemEntity>()
        {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `farm_inventory` (`inventoryId`,`farmerId`,`sourceAssetId`,`sourceBatchId`,`name`,`sku`,`category`,`quantityAvailable`,`quantityReserved`,`unit`,`producedAt`,`expiresAt`,`qualityGrade`,`notes`,`createdAt`,`updatedAt`,`dirty`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: InventoryItemEntity) {
        statement.bindText(1, entity.inventoryId)
        statement.bindText(2, entity.farmerId)
        val _tmpSourceAssetId: String? = entity.sourceAssetId
        if (_tmpSourceAssetId == null) {
          statement.bindNull(3)
        } else {
          statement.bindText(3, _tmpSourceAssetId)
        }
        val _tmpSourceBatchId: String? = entity.sourceBatchId
        if (_tmpSourceBatchId == null) {
          statement.bindNull(4)
        } else {
          statement.bindText(4, _tmpSourceBatchId)
        }
        statement.bindText(5, entity.name)
        val _tmpSku: String? = entity.sku
        if (_tmpSku == null) {
          statement.bindNull(6)
        } else {
          statement.bindText(6, _tmpSku)
        }
        statement.bindText(7, entity.category)
        statement.bindDouble(8, entity.quantityAvailable)
        statement.bindDouble(9, entity.quantityReserved)
        statement.bindText(10, entity.unit)
        val _tmpProducedAt: Long? = entity.producedAt
        if (_tmpProducedAt == null) {
          statement.bindNull(11)
        } else {
          statement.bindLong(11, _tmpProducedAt)
        }
        val _tmpExpiresAt: Long? = entity.expiresAt
        if (_tmpExpiresAt == null) {
          statement.bindNull(12)
        } else {
          statement.bindLong(12, _tmpExpiresAt)
        }
        val _tmpQualityGrade: String? = entity.qualityGrade
        if (_tmpQualityGrade == null) {
          statement.bindNull(13)
        } else {
          statement.bindText(13, _tmpQualityGrade)
        }
        val _tmpNotes: String? = entity.notes
        if (_tmpNotes == null) {
          statement.bindNull(14)
        } else {
          statement.bindText(14, _tmpNotes)
        }
        statement.bindLong(15, entity.createdAt)
        statement.bindLong(16, entity.updatedAt)
        val _tmp: Int = if (entity.dirty) 1 else 0
        statement.bindLong(17, _tmp.toLong())
      }
    }
    this.__updateAdapterOfInventoryItemEntity = object :
        EntityDeleteOrUpdateAdapter<InventoryItemEntity>() {
      protected override fun createQuery(): String =
          "UPDATE OR ABORT `farm_inventory` SET `inventoryId` = ?,`farmerId` = ?,`sourceAssetId` = ?,`sourceBatchId` = ?,`name` = ?,`sku` = ?,`category` = ?,`quantityAvailable` = ?,`quantityReserved` = ?,`unit` = ?,`producedAt` = ?,`expiresAt` = ?,`qualityGrade` = ?,`notes` = ?,`createdAt` = ?,`updatedAt` = ?,`dirty` = ? WHERE `inventoryId` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: InventoryItemEntity) {
        statement.bindText(1, entity.inventoryId)
        statement.bindText(2, entity.farmerId)
        val _tmpSourceAssetId: String? = entity.sourceAssetId
        if (_tmpSourceAssetId == null) {
          statement.bindNull(3)
        } else {
          statement.bindText(3, _tmpSourceAssetId)
        }
        val _tmpSourceBatchId: String? = entity.sourceBatchId
        if (_tmpSourceBatchId == null) {
          statement.bindNull(4)
        } else {
          statement.bindText(4, _tmpSourceBatchId)
        }
        statement.bindText(5, entity.name)
        val _tmpSku: String? = entity.sku
        if (_tmpSku == null) {
          statement.bindNull(6)
        } else {
          statement.bindText(6, _tmpSku)
        }
        statement.bindText(7, entity.category)
        statement.bindDouble(8, entity.quantityAvailable)
        statement.bindDouble(9, entity.quantityReserved)
        statement.bindText(10, entity.unit)
        val _tmpProducedAt: Long? = entity.producedAt
        if (_tmpProducedAt == null) {
          statement.bindNull(11)
        } else {
          statement.bindLong(11, _tmpProducedAt)
        }
        val _tmpExpiresAt: Long? = entity.expiresAt
        if (_tmpExpiresAt == null) {
          statement.bindNull(12)
        } else {
          statement.bindLong(12, _tmpExpiresAt)
        }
        val _tmpQualityGrade: String? = entity.qualityGrade
        if (_tmpQualityGrade == null) {
          statement.bindNull(13)
        } else {
          statement.bindText(13, _tmpQualityGrade)
        }
        val _tmpNotes: String? = entity.notes
        if (_tmpNotes == null) {
          statement.bindNull(14)
        } else {
          statement.bindText(14, _tmpNotes)
        }
        statement.bindLong(15, entity.createdAt)
        statement.bindLong(16, entity.updatedAt)
        val _tmp: Int = if (entity.dirty) 1 else 0
        statement.bindLong(17, _tmp.toLong())
        statement.bindText(18, entity.inventoryId)
      }
    }
  }

  public override suspend fun upsert(item: InventoryItemEntity): Unit = performSuspending(__db,
      false, true) { _connection ->
    __insertAdapterOfInventoryItemEntity.insert(_connection, item)
  }

  public override suspend fun updateInventory(item: InventoryItemEntity): Unit =
      performSuspending(__db, false, true) { _connection ->
    __updateAdapterOfInventoryItemEntity.handle(_connection, item)
  }

  public override fun getInventoryById(id: String): Flow<InventoryItemEntity?> {
    val _sql: String = "SELECT * FROM farm_inventory WHERE inventoryId = ?"
    return createFlow(__db, false, arrayOf("farm_inventory")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, id)
        val _columnIndexOfInventoryId: Int = getColumnIndexOrThrow(_stmt, "inventoryId")
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfSourceAssetId: Int = getColumnIndexOrThrow(_stmt, "sourceAssetId")
        val _columnIndexOfSourceBatchId: Int = getColumnIndexOrThrow(_stmt, "sourceBatchId")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfSku: Int = getColumnIndexOrThrow(_stmt, "sku")
        val _columnIndexOfCategory: Int = getColumnIndexOrThrow(_stmt, "category")
        val _columnIndexOfQuantityAvailable: Int = getColumnIndexOrThrow(_stmt, "quantityAvailable")
        val _columnIndexOfQuantityReserved: Int = getColumnIndexOrThrow(_stmt, "quantityReserved")
        val _columnIndexOfUnit: Int = getColumnIndexOrThrow(_stmt, "unit")
        val _columnIndexOfProducedAt: Int = getColumnIndexOrThrow(_stmt, "producedAt")
        val _columnIndexOfExpiresAt: Int = getColumnIndexOrThrow(_stmt, "expiresAt")
        val _columnIndexOfQualityGrade: Int = getColumnIndexOrThrow(_stmt, "qualityGrade")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _result: InventoryItemEntity?
        if (_stmt.step()) {
          val _tmpInventoryId: String
          _tmpInventoryId = _stmt.getText(_columnIndexOfInventoryId)
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpSourceAssetId: String?
          if (_stmt.isNull(_columnIndexOfSourceAssetId)) {
            _tmpSourceAssetId = null
          } else {
            _tmpSourceAssetId = _stmt.getText(_columnIndexOfSourceAssetId)
          }
          val _tmpSourceBatchId: String?
          if (_stmt.isNull(_columnIndexOfSourceBatchId)) {
            _tmpSourceBatchId = null
          } else {
            _tmpSourceBatchId = _stmt.getText(_columnIndexOfSourceBatchId)
          }
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpSku: String?
          if (_stmt.isNull(_columnIndexOfSku)) {
            _tmpSku = null
          } else {
            _tmpSku = _stmt.getText(_columnIndexOfSku)
          }
          val _tmpCategory: String
          _tmpCategory = _stmt.getText(_columnIndexOfCategory)
          val _tmpQuantityAvailable: Double
          _tmpQuantityAvailable = _stmt.getDouble(_columnIndexOfQuantityAvailable)
          val _tmpQuantityReserved: Double
          _tmpQuantityReserved = _stmt.getDouble(_columnIndexOfQuantityReserved)
          val _tmpUnit: String
          _tmpUnit = _stmt.getText(_columnIndexOfUnit)
          val _tmpProducedAt: Long?
          if (_stmt.isNull(_columnIndexOfProducedAt)) {
            _tmpProducedAt = null
          } else {
            _tmpProducedAt = _stmt.getLong(_columnIndexOfProducedAt)
          }
          val _tmpExpiresAt: Long?
          if (_stmt.isNull(_columnIndexOfExpiresAt)) {
            _tmpExpiresAt = null
          } else {
            _tmpExpiresAt = _stmt.getLong(_columnIndexOfExpiresAt)
          }
          val _tmpQualityGrade: String?
          if (_stmt.isNull(_columnIndexOfQualityGrade)) {
            _tmpQualityGrade = null
          } else {
            _tmpQualityGrade = _stmt.getText(_columnIndexOfQualityGrade)
          }
          val _tmpNotes: String?
          if (_stmt.isNull(_columnIndexOfNotes)) {
            _tmpNotes = null
          } else {
            _tmpNotes = _stmt.getText(_columnIndexOfNotes)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpDirty: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp != 0
          _result =
              InventoryItemEntity(_tmpInventoryId,_tmpFarmerId,_tmpSourceAssetId,_tmpSourceBatchId,_tmpName,_tmpSku,_tmpCategory,_tmpQuantityAvailable,_tmpQuantityReserved,_tmpUnit,_tmpProducedAt,_tmpExpiresAt,_tmpQualityGrade,_tmpNotes,_tmpCreatedAt,_tmpUpdatedAt,_tmpDirty)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getInventoryByFarmer(farmerId: String): Flow<List<InventoryItemEntity>> {
    val _sql: String = "SELECT * FROM farm_inventory WHERE farmerId = ? ORDER BY name ASC"
    return createFlow(__db, false, arrayOf("farm_inventory")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, farmerId)
        val _columnIndexOfInventoryId: Int = getColumnIndexOrThrow(_stmt, "inventoryId")
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfSourceAssetId: Int = getColumnIndexOrThrow(_stmt, "sourceAssetId")
        val _columnIndexOfSourceBatchId: Int = getColumnIndexOrThrow(_stmt, "sourceBatchId")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfSku: Int = getColumnIndexOrThrow(_stmt, "sku")
        val _columnIndexOfCategory: Int = getColumnIndexOrThrow(_stmt, "category")
        val _columnIndexOfQuantityAvailable: Int = getColumnIndexOrThrow(_stmt, "quantityAvailable")
        val _columnIndexOfQuantityReserved: Int = getColumnIndexOrThrow(_stmt, "quantityReserved")
        val _columnIndexOfUnit: Int = getColumnIndexOrThrow(_stmt, "unit")
        val _columnIndexOfProducedAt: Int = getColumnIndexOrThrow(_stmt, "producedAt")
        val _columnIndexOfExpiresAt: Int = getColumnIndexOrThrow(_stmt, "expiresAt")
        val _columnIndexOfQualityGrade: Int = getColumnIndexOrThrow(_stmt, "qualityGrade")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _result: MutableList<InventoryItemEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: InventoryItemEntity
          val _tmpInventoryId: String
          _tmpInventoryId = _stmt.getText(_columnIndexOfInventoryId)
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpSourceAssetId: String?
          if (_stmt.isNull(_columnIndexOfSourceAssetId)) {
            _tmpSourceAssetId = null
          } else {
            _tmpSourceAssetId = _stmt.getText(_columnIndexOfSourceAssetId)
          }
          val _tmpSourceBatchId: String?
          if (_stmt.isNull(_columnIndexOfSourceBatchId)) {
            _tmpSourceBatchId = null
          } else {
            _tmpSourceBatchId = _stmt.getText(_columnIndexOfSourceBatchId)
          }
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpSku: String?
          if (_stmt.isNull(_columnIndexOfSku)) {
            _tmpSku = null
          } else {
            _tmpSku = _stmt.getText(_columnIndexOfSku)
          }
          val _tmpCategory: String
          _tmpCategory = _stmt.getText(_columnIndexOfCategory)
          val _tmpQuantityAvailable: Double
          _tmpQuantityAvailable = _stmt.getDouble(_columnIndexOfQuantityAvailable)
          val _tmpQuantityReserved: Double
          _tmpQuantityReserved = _stmt.getDouble(_columnIndexOfQuantityReserved)
          val _tmpUnit: String
          _tmpUnit = _stmt.getText(_columnIndexOfUnit)
          val _tmpProducedAt: Long?
          if (_stmt.isNull(_columnIndexOfProducedAt)) {
            _tmpProducedAt = null
          } else {
            _tmpProducedAt = _stmt.getLong(_columnIndexOfProducedAt)
          }
          val _tmpExpiresAt: Long?
          if (_stmt.isNull(_columnIndexOfExpiresAt)) {
            _tmpExpiresAt = null
          } else {
            _tmpExpiresAt = _stmt.getLong(_columnIndexOfExpiresAt)
          }
          val _tmpQualityGrade: String?
          if (_stmt.isNull(_columnIndexOfQualityGrade)) {
            _tmpQualityGrade = null
          } else {
            _tmpQualityGrade = _stmt.getText(_columnIndexOfQualityGrade)
          }
          val _tmpNotes: String?
          if (_stmt.isNull(_columnIndexOfNotes)) {
            _tmpNotes = null
          } else {
            _tmpNotes = _stmt.getText(_columnIndexOfNotes)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpDirty: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp != 0
          _item =
              InventoryItemEntity(_tmpInventoryId,_tmpFarmerId,_tmpSourceAssetId,_tmpSourceBatchId,_tmpName,_tmpSku,_tmpCategory,_tmpQuantityAvailable,_tmpQuantityReserved,_tmpUnit,_tmpProducedAt,_tmpExpiresAt,_tmpQualityGrade,_tmpNotes,_tmpCreatedAt,_tmpUpdatedAt,_tmpDirty)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getInventoryBySourceAsset(assetId: String):
      List<InventoryItemEntity> {
    val _sql: String = "SELECT * FROM farm_inventory WHERE sourceAssetId = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, assetId)
        val _columnIndexOfInventoryId: Int = getColumnIndexOrThrow(_stmt, "inventoryId")
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfSourceAssetId: Int = getColumnIndexOrThrow(_stmt, "sourceAssetId")
        val _columnIndexOfSourceBatchId: Int = getColumnIndexOrThrow(_stmt, "sourceBatchId")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfSku: Int = getColumnIndexOrThrow(_stmt, "sku")
        val _columnIndexOfCategory: Int = getColumnIndexOrThrow(_stmt, "category")
        val _columnIndexOfQuantityAvailable: Int = getColumnIndexOrThrow(_stmt, "quantityAvailable")
        val _columnIndexOfQuantityReserved: Int = getColumnIndexOrThrow(_stmt, "quantityReserved")
        val _columnIndexOfUnit: Int = getColumnIndexOrThrow(_stmt, "unit")
        val _columnIndexOfProducedAt: Int = getColumnIndexOrThrow(_stmt, "producedAt")
        val _columnIndexOfExpiresAt: Int = getColumnIndexOrThrow(_stmt, "expiresAt")
        val _columnIndexOfQualityGrade: Int = getColumnIndexOrThrow(_stmt, "qualityGrade")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _result: MutableList<InventoryItemEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: InventoryItemEntity
          val _tmpInventoryId: String
          _tmpInventoryId = _stmt.getText(_columnIndexOfInventoryId)
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpSourceAssetId: String?
          if (_stmt.isNull(_columnIndexOfSourceAssetId)) {
            _tmpSourceAssetId = null
          } else {
            _tmpSourceAssetId = _stmt.getText(_columnIndexOfSourceAssetId)
          }
          val _tmpSourceBatchId: String?
          if (_stmt.isNull(_columnIndexOfSourceBatchId)) {
            _tmpSourceBatchId = null
          } else {
            _tmpSourceBatchId = _stmt.getText(_columnIndexOfSourceBatchId)
          }
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpSku: String?
          if (_stmt.isNull(_columnIndexOfSku)) {
            _tmpSku = null
          } else {
            _tmpSku = _stmt.getText(_columnIndexOfSku)
          }
          val _tmpCategory: String
          _tmpCategory = _stmt.getText(_columnIndexOfCategory)
          val _tmpQuantityAvailable: Double
          _tmpQuantityAvailable = _stmt.getDouble(_columnIndexOfQuantityAvailable)
          val _tmpQuantityReserved: Double
          _tmpQuantityReserved = _stmt.getDouble(_columnIndexOfQuantityReserved)
          val _tmpUnit: String
          _tmpUnit = _stmt.getText(_columnIndexOfUnit)
          val _tmpProducedAt: Long?
          if (_stmt.isNull(_columnIndexOfProducedAt)) {
            _tmpProducedAt = null
          } else {
            _tmpProducedAt = _stmt.getLong(_columnIndexOfProducedAt)
          }
          val _tmpExpiresAt: Long?
          if (_stmt.isNull(_columnIndexOfExpiresAt)) {
            _tmpExpiresAt = null
          } else {
            _tmpExpiresAt = _stmt.getLong(_columnIndexOfExpiresAt)
          }
          val _tmpQualityGrade: String?
          if (_stmt.isNull(_columnIndexOfQualityGrade)) {
            _tmpQualityGrade = null
          } else {
            _tmpQualityGrade = _stmt.getText(_columnIndexOfQualityGrade)
          }
          val _tmpNotes: String?
          if (_stmt.isNull(_columnIndexOfNotes)) {
            _tmpNotes = null
          } else {
            _tmpNotes = _stmt.getText(_columnIndexOfNotes)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpDirty: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp != 0
          _item =
              InventoryItemEntity(_tmpInventoryId,_tmpFarmerId,_tmpSourceAssetId,_tmpSourceBatchId,_tmpName,_tmpSku,_tmpCategory,_tmpQuantityAvailable,_tmpQuantityReserved,_tmpUnit,_tmpProducedAt,_tmpExpiresAt,_tmpQualityGrade,_tmpNotes,_tmpCreatedAt,_tmpUpdatedAt,_tmpDirty)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getUpdatedSince(sinceTime: Long, limit: Int):
      List<InventoryItemEntity> {
    val _sql: String = "SELECT * FROM farm_inventory WHERE updatedAt > ? LIMIT ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, sinceTime)
        _argIndex = 2
        _stmt.bindLong(_argIndex, limit.toLong())
        val _columnIndexOfInventoryId: Int = getColumnIndexOrThrow(_stmt, "inventoryId")
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfSourceAssetId: Int = getColumnIndexOrThrow(_stmt, "sourceAssetId")
        val _columnIndexOfSourceBatchId: Int = getColumnIndexOrThrow(_stmt, "sourceBatchId")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfSku: Int = getColumnIndexOrThrow(_stmt, "sku")
        val _columnIndexOfCategory: Int = getColumnIndexOrThrow(_stmt, "category")
        val _columnIndexOfQuantityAvailable: Int = getColumnIndexOrThrow(_stmt, "quantityAvailable")
        val _columnIndexOfQuantityReserved: Int = getColumnIndexOrThrow(_stmt, "quantityReserved")
        val _columnIndexOfUnit: Int = getColumnIndexOrThrow(_stmt, "unit")
        val _columnIndexOfProducedAt: Int = getColumnIndexOrThrow(_stmt, "producedAt")
        val _columnIndexOfExpiresAt: Int = getColumnIndexOrThrow(_stmt, "expiresAt")
        val _columnIndexOfQualityGrade: Int = getColumnIndexOrThrow(_stmt, "qualityGrade")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _result: MutableList<InventoryItemEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: InventoryItemEntity
          val _tmpInventoryId: String
          _tmpInventoryId = _stmt.getText(_columnIndexOfInventoryId)
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpSourceAssetId: String?
          if (_stmt.isNull(_columnIndexOfSourceAssetId)) {
            _tmpSourceAssetId = null
          } else {
            _tmpSourceAssetId = _stmt.getText(_columnIndexOfSourceAssetId)
          }
          val _tmpSourceBatchId: String?
          if (_stmt.isNull(_columnIndexOfSourceBatchId)) {
            _tmpSourceBatchId = null
          } else {
            _tmpSourceBatchId = _stmt.getText(_columnIndexOfSourceBatchId)
          }
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpSku: String?
          if (_stmt.isNull(_columnIndexOfSku)) {
            _tmpSku = null
          } else {
            _tmpSku = _stmt.getText(_columnIndexOfSku)
          }
          val _tmpCategory: String
          _tmpCategory = _stmt.getText(_columnIndexOfCategory)
          val _tmpQuantityAvailable: Double
          _tmpQuantityAvailable = _stmt.getDouble(_columnIndexOfQuantityAvailable)
          val _tmpQuantityReserved: Double
          _tmpQuantityReserved = _stmt.getDouble(_columnIndexOfQuantityReserved)
          val _tmpUnit: String
          _tmpUnit = _stmt.getText(_columnIndexOfUnit)
          val _tmpProducedAt: Long?
          if (_stmt.isNull(_columnIndexOfProducedAt)) {
            _tmpProducedAt = null
          } else {
            _tmpProducedAt = _stmt.getLong(_columnIndexOfProducedAt)
          }
          val _tmpExpiresAt: Long?
          if (_stmt.isNull(_columnIndexOfExpiresAt)) {
            _tmpExpiresAt = null
          } else {
            _tmpExpiresAt = _stmt.getLong(_columnIndexOfExpiresAt)
          }
          val _tmpQualityGrade: String?
          if (_stmt.isNull(_columnIndexOfQualityGrade)) {
            _tmpQualityGrade = null
          } else {
            _tmpQualityGrade = _stmt.getText(_columnIndexOfQualityGrade)
          }
          val _tmpNotes: String?
          if (_stmt.isNull(_columnIndexOfNotes)) {
            _tmpNotes = null
          } else {
            _tmpNotes = _stmt.getText(_columnIndexOfNotes)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpDirty: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp != 0
          _item =
              InventoryItemEntity(_tmpInventoryId,_tmpFarmerId,_tmpSourceAssetId,_tmpSourceBatchId,_tmpName,_tmpSku,_tmpCategory,_tmpQuantityAvailable,_tmpQuantityReserved,_tmpUnit,_tmpProducedAt,_tmpExpiresAt,_tmpQualityGrade,_tmpNotes,_tmpCreatedAt,_tmpUpdatedAt,_tmpDirty)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getAllInventory(): Flow<List<InventoryItemEntity>> {
    val _sql: String = "SELECT * FROM farm_inventory"
    return createFlow(__db, false, arrayOf("farm_inventory")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfInventoryId: Int = getColumnIndexOrThrow(_stmt, "inventoryId")
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfSourceAssetId: Int = getColumnIndexOrThrow(_stmt, "sourceAssetId")
        val _columnIndexOfSourceBatchId: Int = getColumnIndexOrThrow(_stmt, "sourceBatchId")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfSku: Int = getColumnIndexOrThrow(_stmt, "sku")
        val _columnIndexOfCategory: Int = getColumnIndexOrThrow(_stmt, "category")
        val _columnIndexOfQuantityAvailable: Int = getColumnIndexOrThrow(_stmt, "quantityAvailable")
        val _columnIndexOfQuantityReserved: Int = getColumnIndexOrThrow(_stmt, "quantityReserved")
        val _columnIndexOfUnit: Int = getColumnIndexOrThrow(_stmt, "unit")
        val _columnIndexOfProducedAt: Int = getColumnIndexOrThrow(_stmt, "producedAt")
        val _columnIndexOfExpiresAt: Int = getColumnIndexOrThrow(_stmt, "expiresAt")
        val _columnIndexOfQualityGrade: Int = getColumnIndexOrThrow(_stmt, "qualityGrade")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _result: MutableList<InventoryItemEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: InventoryItemEntity
          val _tmpInventoryId: String
          _tmpInventoryId = _stmt.getText(_columnIndexOfInventoryId)
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpSourceAssetId: String?
          if (_stmt.isNull(_columnIndexOfSourceAssetId)) {
            _tmpSourceAssetId = null
          } else {
            _tmpSourceAssetId = _stmt.getText(_columnIndexOfSourceAssetId)
          }
          val _tmpSourceBatchId: String?
          if (_stmt.isNull(_columnIndexOfSourceBatchId)) {
            _tmpSourceBatchId = null
          } else {
            _tmpSourceBatchId = _stmt.getText(_columnIndexOfSourceBatchId)
          }
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpSku: String?
          if (_stmt.isNull(_columnIndexOfSku)) {
            _tmpSku = null
          } else {
            _tmpSku = _stmt.getText(_columnIndexOfSku)
          }
          val _tmpCategory: String
          _tmpCategory = _stmt.getText(_columnIndexOfCategory)
          val _tmpQuantityAvailable: Double
          _tmpQuantityAvailable = _stmt.getDouble(_columnIndexOfQuantityAvailable)
          val _tmpQuantityReserved: Double
          _tmpQuantityReserved = _stmt.getDouble(_columnIndexOfQuantityReserved)
          val _tmpUnit: String
          _tmpUnit = _stmt.getText(_columnIndexOfUnit)
          val _tmpProducedAt: Long?
          if (_stmt.isNull(_columnIndexOfProducedAt)) {
            _tmpProducedAt = null
          } else {
            _tmpProducedAt = _stmt.getLong(_columnIndexOfProducedAt)
          }
          val _tmpExpiresAt: Long?
          if (_stmt.isNull(_columnIndexOfExpiresAt)) {
            _tmpExpiresAt = null
          } else {
            _tmpExpiresAt = _stmt.getLong(_columnIndexOfExpiresAt)
          }
          val _tmpQualityGrade: String?
          if (_stmt.isNull(_columnIndexOfQualityGrade)) {
            _tmpQualityGrade = null
          } else {
            _tmpQualityGrade = _stmt.getText(_columnIndexOfQualityGrade)
          }
          val _tmpNotes: String?
          if (_stmt.isNull(_columnIndexOfNotes)) {
            _tmpNotes = null
          } else {
            _tmpNotes = _stmt.getText(_columnIndexOfNotes)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpDirty: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp != 0
          _item =
              InventoryItemEntity(_tmpInventoryId,_tmpFarmerId,_tmpSourceAssetId,_tmpSourceBatchId,_tmpName,_tmpSku,_tmpCategory,_tmpQuantityAvailable,_tmpQuantityReserved,_tmpUnit,_tmpProducedAt,_tmpExpiresAt,_tmpQualityGrade,_tmpNotes,_tmpCreatedAt,_tmpUpdatedAt,_tmpDirty)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getInventoryByIdSync(id: String): InventoryItemEntity? {
    val _sql: String = "SELECT * FROM farm_inventory WHERE inventoryId = ? LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, id)
        val _columnIndexOfInventoryId: Int = getColumnIndexOrThrow(_stmt, "inventoryId")
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfSourceAssetId: Int = getColumnIndexOrThrow(_stmt, "sourceAssetId")
        val _columnIndexOfSourceBatchId: Int = getColumnIndexOrThrow(_stmt, "sourceBatchId")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfSku: Int = getColumnIndexOrThrow(_stmt, "sku")
        val _columnIndexOfCategory: Int = getColumnIndexOrThrow(_stmt, "category")
        val _columnIndexOfQuantityAvailable: Int = getColumnIndexOrThrow(_stmt, "quantityAvailable")
        val _columnIndexOfQuantityReserved: Int = getColumnIndexOrThrow(_stmt, "quantityReserved")
        val _columnIndexOfUnit: Int = getColumnIndexOrThrow(_stmt, "unit")
        val _columnIndexOfProducedAt: Int = getColumnIndexOrThrow(_stmt, "producedAt")
        val _columnIndexOfExpiresAt: Int = getColumnIndexOrThrow(_stmt, "expiresAt")
        val _columnIndexOfQualityGrade: Int = getColumnIndexOrThrow(_stmt, "qualityGrade")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _result: InventoryItemEntity?
        if (_stmt.step()) {
          val _tmpInventoryId: String
          _tmpInventoryId = _stmt.getText(_columnIndexOfInventoryId)
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpSourceAssetId: String?
          if (_stmt.isNull(_columnIndexOfSourceAssetId)) {
            _tmpSourceAssetId = null
          } else {
            _tmpSourceAssetId = _stmt.getText(_columnIndexOfSourceAssetId)
          }
          val _tmpSourceBatchId: String?
          if (_stmt.isNull(_columnIndexOfSourceBatchId)) {
            _tmpSourceBatchId = null
          } else {
            _tmpSourceBatchId = _stmt.getText(_columnIndexOfSourceBatchId)
          }
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpSku: String?
          if (_stmt.isNull(_columnIndexOfSku)) {
            _tmpSku = null
          } else {
            _tmpSku = _stmt.getText(_columnIndexOfSku)
          }
          val _tmpCategory: String
          _tmpCategory = _stmt.getText(_columnIndexOfCategory)
          val _tmpQuantityAvailable: Double
          _tmpQuantityAvailable = _stmt.getDouble(_columnIndexOfQuantityAvailable)
          val _tmpQuantityReserved: Double
          _tmpQuantityReserved = _stmt.getDouble(_columnIndexOfQuantityReserved)
          val _tmpUnit: String
          _tmpUnit = _stmt.getText(_columnIndexOfUnit)
          val _tmpProducedAt: Long?
          if (_stmt.isNull(_columnIndexOfProducedAt)) {
            _tmpProducedAt = null
          } else {
            _tmpProducedAt = _stmt.getLong(_columnIndexOfProducedAt)
          }
          val _tmpExpiresAt: Long?
          if (_stmt.isNull(_columnIndexOfExpiresAt)) {
            _tmpExpiresAt = null
          } else {
            _tmpExpiresAt = _stmt.getLong(_columnIndexOfExpiresAt)
          }
          val _tmpQualityGrade: String?
          if (_stmt.isNull(_columnIndexOfQualityGrade)) {
            _tmpQualityGrade = null
          } else {
            _tmpQualityGrade = _stmt.getText(_columnIndexOfQualityGrade)
          }
          val _tmpNotes: String?
          if (_stmt.isNull(_columnIndexOfNotes)) {
            _tmpNotes = null
          } else {
            _tmpNotes = _stmt.getText(_columnIndexOfNotes)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpDirty: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp != 0
          _result =
              InventoryItemEntity(_tmpInventoryId,_tmpFarmerId,_tmpSourceAssetId,_tmpSourceBatchId,_tmpName,_tmpSku,_tmpCategory,_tmpQuantityAvailable,_tmpQuantityReserved,_tmpUnit,_tmpProducedAt,_tmpExpiresAt,_tmpQualityGrade,_tmpNotes,_tmpCreatedAt,_tmpUpdatedAt,_tmpDirty)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun updateAvailableQuantity(
    id: String,
    qty: Double,
    updatedAt: Long,
  ) {
    val _sql: String =
        "UPDATE farm_inventory SET quantityAvailable = ?, updatedAt = ?, dirty = 1 WHERE inventoryId = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindDouble(_argIndex, qty)
        _argIndex = 2
        _stmt.bindLong(_argIndex, updatedAt)
        _argIndex = 3
        _stmt.bindText(_argIndex, id)
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
