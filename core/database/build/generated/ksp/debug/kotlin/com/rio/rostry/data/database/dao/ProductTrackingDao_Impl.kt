package com.rio.rostry.`data`.database.dao

import androidx.room.EntityDeleteOrUpdateAdapter
import androidx.room.EntityInsertAdapter
import androidx.room.EntityUpsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.rio.rostry.`data`.database.entity.ProductTrackingEntity
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
public class ProductTrackingDao_Impl(
  __db: RoomDatabase,
) : ProductTrackingDao {
  private val __db: RoomDatabase

  private val __upsertAdapterOfProductTrackingEntity: EntityUpsertAdapter<ProductTrackingEntity>
  init {
    this.__db = __db
    this.__upsertAdapterOfProductTrackingEntity = EntityUpsertAdapter<ProductTrackingEntity>(object
        : EntityInsertAdapter<ProductTrackingEntity>() {
      protected override fun createQuery(): String =
          "INSERT INTO `product_tracking` (`trackingId`,`productId`,`ownerId`,`status`,`metadataJson`,`timestamp`,`createdAt`,`updatedAt`,`isDeleted`,`deletedAt`,`dirty`) VALUES (?,?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: ProductTrackingEntity) {
        statement.bindText(1, entity.trackingId)
        statement.bindText(2, entity.productId)
        statement.bindText(3, entity.ownerId)
        statement.bindText(4, entity.status)
        val _tmpMetadataJson: String? = entity.metadataJson
        if (_tmpMetadataJson == null) {
          statement.bindNull(5)
        } else {
          statement.bindText(5, _tmpMetadataJson)
        }
        statement.bindLong(6, entity.timestamp)
        statement.bindLong(7, entity.createdAt)
        statement.bindLong(8, entity.updatedAt)
        val _tmp: Int = if (entity.isDeleted) 1 else 0
        statement.bindLong(9, _tmp.toLong())
        val _tmpDeletedAt: Long? = entity.deletedAt
        if (_tmpDeletedAt == null) {
          statement.bindNull(10)
        } else {
          statement.bindLong(10, _tmpDeletedAt)
        }
        val _tmp_1: Int = if (entity.dirty) 1 else 0
        statement.bindLong(11, _tmp_1.toLong())
      }
    }, object : EntityDeleteOrUpdateAdapter<ProductTrackingEntity>() {
      protected override fun createQuery(): String =
          "UPDATE `product_tracking` SET `trackingId` = ?,`productId` = ?,`ownerId` = ?,`status` = ?,`metadataJson` = ?,`timestamp` = ?,`createdAt` = ?,`updatedAt` = ?,`isDeleted` = ?,`deletedAt` = ?,`dirty` = ? WHERE `trackingId` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: ProductTrackingEntity) {
        statement.bindText(1, entity.trackingId)
        statement.bindText(2, entity.productId)
        statement.bindText(3, entity.ownerId)
        statement.bindText(4, entity.status)
        val _tmpMetadataJson: String? = entity.metadataJson
        if (_tmpMetadataJson == null) {
          statement.bindNull(5)
        } else {
          statement.bindText(5, _tmpMetadataJson)
        }
        statement.bindLong(6, entity.timestamp)
        statement.bindLong(7, entity.createdAt)
        statement.bindLong(8, entity.updatedAt)
        val _tmp: Int = if (entity.isDeleted) 1 else 0
        statement.bindLong(9, _tmp.toLong())
        val _tmpDeletedAt: Long? = entity.deletedAt
        if (_tmpDeletedAt == null) {
          statement.bindNull(10)
        } else {
          statement.bindLong(10, _tmpDeletedAt)
        }
        val _tmp_1: Int = if (entity.dirty) 1 else 0
        statement.bindLong(11, _tmp_1.toLong())
        statement.bindText(12, entity.trackingId)
      }
    })
  }

  public override suspend fun upsertAll(items: List<ProductTrackingEntity>): Unit =
      performSuspending(__db, false, true) { _connection ->
    __upsertAdapterOfProductTrackingEntity.upsert(_connection, items)
  }

  public override suspend fun upsert(item: ProductTrackingEntity): Unit = performSuspending(__db,
      false, true) { _connection ->
    __upsertAdapterOfProductTrackingEntity.upsert(_connection, item)
  }

  public override fun observeByProduct(productId: String): Flow<List<ProductTrackingEntity>> {
    val _sql: String =
        "SELECT * FROM product_tracking WHERE productId = ? AND isDeleted = 0 ORDER BY timestamp DESC"
    return createFlow(__db, false, arrayOf("product_tracking")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, productId)
        val _columnIndexOfTrackingId: Int = getColumnIndexOrThrow(_stmt, "trackingId")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfOwnerId: Int = getColumnIndexOrThrow(_stmt, "ownerId")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfMetadataJson: Int = getColumnIndexOrThrow(_stmt, "metadataJson")
        val _columnIndexOfTimestamp: Int = getColumnIndexOrThrow(_stmt, "timestamp")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfIsDeleted: Int = getColumnIndexOrThrow(_stmt, "isDeleted")
        val _columnIndexOfDeletedAt: Int = getColumnIndexOrThrow(_stmt, "deletedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _result: MutableList<ProductTrackingEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: ProductTrackingEntity
          val _tmpTrackingId: String
          _tmpTrackingId = _stmt.getText(_columnIndexOfTrackingId)
          val _tmpProductId: String
          _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          val _tmpOwnerId: String
          _tmpOwnerId = _stmt.getText(_columnIndexOfOwnerId)
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpMetadataJson: String?
          if (_stmt.isNull(_columnIndexOfMetadataJson)) {
            _tmpMetadataJson = null
          } else {
            _tmpMetadataJson = _stmt.getText(_columnIndexOfMetadataJson)
          }
          val _tmpTimestamp: Long
          _tmpTimestamp = _stmt.getLong(_columnIndexOfTimestamp)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpIsDeleted: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsDeleted).toInt()
          _tmpIsDeleted = _tmp != 0
          val _tmpDeletedAt: Long?
          if (_stmt.isNull(_columnIndexOfDeletedAt)) {
            _tmpDeletedAt = null
          } else {
            _tmpDeletedAt = _stmt.getLong(_columnIndexOfDeletedAt)
          }
          val _tmpDirty: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp_1 != 0
          _item =
              ProductTrackingEntity(_tmpTrackingId,_tmpProductId,_tmpOwnerId,_tmpStatus,_tmpMetadataJson,_tmpTimestamp,_tmpCreatedAt,_tmpUpdatedAt,_tmpIsDeleted,_tmpDeletedAt,_tmpDirty)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getUpdatedSince(since: Long, limit: Int):
      List<ProductTrackingEntity> {
    val _sql: String =
        "SELECT * FROM product_tracking WHERE updatedAt > ? ORDER BY updatedAt ASC LIMIT ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, since)
        _argIndex = 2
        _stmt.bindLong(_argIndex, limit.toLong())
        val _columnIndexOfTrackingId: Int = getColumnIndexOrThrow(_stmt, "trackingId")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfOwnerId: Int = getColumnIndexOrThrow(_stmt, "ownerId")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfMetadataJson: Int = getColumnIndexOrThrow(_stmt, "metadataJson")
        val _columnIndexOfTimestamp: Int = getColumnIndexOrThrow(_stmt, "timestamp")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfIsDeleted: Int = getColumnIndexOrThrow(_stmt, "isDeleted")
        val _columnIndexOfDeletedAt: Int = getColumnIndexOrThrow(_stmt, "deletedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _result: MutableList<ProductTrackingEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: ProductTrackingEntity
          val _tmpTrackingId: String
          _tmpTrackingId = _stmt.getText(_columnIndexOfTrackingId)
          val _tmpProductId: String
          _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          val _tmpOwnerId: String
          _tmpOwnerId = _stmt.getText(_columnIndexOfOwnerId)
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpMetadataJson: String?
          if (_stmt.isNull(_columnIndexOfMetadataJson)) {
            _tmpMetadataJson = null
          } else {
            _tmpMetadataJson = _stmt.getText(_columnIndexOfMetadataJson)
          }
          val _tmpTimestamp: Long
          _tmpTimestamp = _stmt.getLong(_columnIndexOfTimestamp)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpIsDeleted: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsDeleted).toInt()
          _tmpIsDeleted = _tmp != 0
          val _tmpDeletedAt: Long?
          if (_stmt.isNull(_columnIndexOfDeletedAt)) {
            _tmpDeletedAt = null
          } else {
            _tmpDeletedAt = _stmt.getLong(_columnIndexOfDeletedAt)
          }
          val _tmpDirty: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp_1 != 0
          _item =
              ProductTrackingEntity(_tmpTrackingId,_tmpProductId,_tmpOwnerId,_tmpStatus,_tmpMetadataJson,_tmpTimestamp,_tmpCreatedAt,_tmpUpdatedAt,_tmpIsDeleted,_tmpDeletedAt,_tmpDirty)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getByProductId(productId: String): ProductTrackingEntity? {
    val _sql: String =
        "SELECT * FROM product_tracking WHERE productId = ? AND isDeleted = 0 ORDER BY timestamp DESC LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, productId)
        val _columnIndexOfTrackingId: Int = getColumnIndexOrThrow(_stmt, "trackingId")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfOwnerId: Int = getColumnIndexOrThrow(_stmt, "ownerId")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfMetadataJson: Int = getColumnIndexOrThrow(_stmt, "metadataJson")
        val _columnIndexOfTimestamp: Int = getColumnIndexOrThrow(_stmt, "timestamp")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfIsDeleted: Int = getColumnIndexOrThrow(_stmt, "isDeleted")
        val _columnIndexOfDeletedAt: Int = getColumnIndexOrThrow(_stmt, "deletedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _result: ProductTrackingEntity?
        if (_stmt.step()) {
          val _tmpTrackingId: String
          _tmpTrackingId = _stmt.getText(_columnIndexOfTrackingId)
          val _tmpProductId: String
          _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          val _tmpOwnerId: String
          _tmpOwnerId = _stmt.getText(_columnIndexOfOwnerId)
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpMetadataJson: String?
          if (_stmt.isNull(_columnIndexOfMetadataJson)) {
            _tmpMetadataJson = null
          } else {
            _tmpMetadataJson = _stmt.getText(_columnIndexOfMetadataJson)
          }
          val _tmpTimestamp: Long
          _tmpTimestamp = _stmt.getLong(_columnIndexOfTimestamp)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpIsDeleted: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsDeleted).toInt()
          _tmpIsDeleted = _tmp != 0
          val _tmpDeletedAt: Long?
          if (_stmt.isNull(_columnIndexOfDeletedAt)) {
            _tmpDeletedAt = null
          } else {
            _tmpDeletedAt = _stmt.getLong(_columnIndexOfDeletedAt)
          }
          val _tmpDirty: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp_1 != 0
          _result =
              ProductTrackingEntity(_tmpTrackingId,_tmpProductId,_tmpOwnerId,_tmpStatus,_tmpMetadataJson,_tmpTimestamp,_tmpCreatedAt,_tmpUpdatedAt,_tmpIsDeleted,_tmpDeletedAt,_tmpDirty)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun purgeDeleted() {
    val _sql: String = "DELETE FROM product_tracking WHERE isDeleted = 1"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun clearDirtyCustom(trackingId: String, updatedAt: Long) {
    val _sql: String = "UPDATE product_tracking SET dirty = 0, updatedAt = ? WHERE trackingId = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, updatedAt)
        _argIndex = 2
        _stmt.bindText(_argIndex, trackingId)
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
