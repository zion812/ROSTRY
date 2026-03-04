package com.rio.rostry.`data`.database.dao

import androidx.room.EntityDeleteOrUpdateAdapter
import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.rio.rostry.`data`.database.entity.DeliveryHubEntity
import javax.`annotation`.processing.Generated
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

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class DeliveryHubDao_Impl(
  __db: RoomDatabase,
) : DeliveryHubDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfDeliveryHubEntity: EntityInsertAdapter<DeliveryHubEntity>

  private val __updateAdapterOfDeliveryHubEntity: EntityDeleteOrUpdateAdapter<DeliveryHubEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfDeliveryHubEntity = object : EntityInsertAdapter<DeliveryHubEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `delivery_hubs` (`hubId`,`name`,`latitude`,`longitude`,`address`,`createdAt`,`updatedAt`) VALUES (?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: DeliveryHubEntity) {
        statement.bindText(1, entity.hubId)
        statement.bindText(2, entity.name)
        val _tmpLatitude: Double? = entity.latitude
        if (_tmpLatitude == null) {
          statement.bindNull(3)
        } else {
          statement.bindDouble(3, _tmpLatitude)
        }
        val _tmpLongitude: Double? = entity.longitude
        if (_tmpLongitude == null) {
          statement.bindNull(4)
        } else {
          statement.bindDouble(4, _tmpLongitude)
        }
        val _tmpAddress: String? = entity.address
        if (_tmpAddress == null) {
          statement.bindNull(5)
        } else {
          statement.bindText(5, _tmpAddress)
        }
        statement.bindLong(6, entity.createdAt)
        statement.bindLong(7, entity.updatedAt)
      }
    }
    this.__updateAdapterOfDeliveryHubEntity = object :
        EntityDeleteOrUpdateAdapter<DeliveryHubEntity>() {
      protected override fun createQuery(): String =
          "UPDATE OR ABORT `delivery_hubs` SET `hubId` = ?,`name` = ?,`latitude` = ?,`longitude` = ?,`address` = ?,`createdAt` = ?,`updatedAt` = ? WHERE `hubId` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: DeliveryHubEntity) {
        statement.bindText(1, entity.hubId)
        statement.bindText(2, entity.name)
        val _tmpLatitude: Double? = entity.latitude
        if (_tmpLatitude == null) {
          statement.bindNull(3)
        } else {
          statement.bindDouble(3, _tmpLatitude)
        }
        val _tmpLongitude: Double? = entity.longitude
        if (_tmpLongitude == null) {
          statement.bindNull(4)
        } else {
          statement.bindDouble(4, _tmpLongitude)
        }
        val _tmpAddress: String? = entity.address
        if (_tmpAddress == null) {
          statement.bindNull(5)
        } else {
          statement.bindText(5, _tmpAddress)
        }
        statement.bindLong(6, entity.createdAt)
        statement.bindLong(7, entity.updatedAt)
        statement.bindText(8, entity.hubId)
      }
    }
  }

  public override suspend fun upsert(hub: DeliveryHubEntity): Unit = performSuspending(__db, false,
      true) { _connection ->
    __insertAdapterOfDeliveryHubEntity.insert(_connection, hub)
  }

  public override suspend fun update(hub: DeliveryHubEntity): Unit = performSuspending(__db, false,
      true) { _connection ->
    __updateAdapterOfDeliveryHubEntity.handle(_connection, hub)
  }

  public override suspend fun listAll(): List<DeliveryHubEntity> {
    val _sql: String = "SELECT * FROM delivery_hubs"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfHubId: Int = getColumnIndexOrThrow(_stmt, "hubId")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfLatitude: Int = getColumnIndexOrThrow(_stmt, "latitude")
        val _columnIndexOfLongitude: Int = getColumnIndexOrThrow(_stmt, "longitude")
        val _columnIndexOfAddress: Int = getColumnIndexOrThrow(_stmt, "address")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _result: MutableList<DeliveryHubEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: DeliveryHubEntity
          val _tmpHubId: String
          _tmpHubId = _stmt.getText(_columnIndexOfHubId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpLatitude: Double?
          if (_stmt.isNull(_columnIndexOfLatitude)) {
            _tmpLatitude = null
          } else {
            _tmpLatitude = _stmt.getDouble(_columnIndexOfLatitude)
          }
          val _tmpLongitude: Double?
          if (_stmt.isNull(_columnIndexOfLongitude)) {
            _tmpLongitude = null
          } else {
            _tmpLongitude = _stmt.getDouble(_columnIndexOfLongitude)
          }
          val _tmpAddress: String?
          if (_stmt.isNull(_columnIndexOfAddress)) {
            _tmpAddress = null
          } else {
            _tmpAddress = _stmt.getText(_columnIndexOfAddress)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          _item =
              DeliveryHubEntity(_tmpHubId,_tmpName,_tmpLatitude,_tmpLongitude,_tmpAddress,_tmpCreatedAt,_tmpUpdatedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun findInBox(
    minLat: Double?,
    maxLat: Double?,
    minLng: Double?,
    maxLng: Double?,
  ): List<DeliveryHubEntity> {
    val _sql: String =
        "SELECT * FROM delivery_hubs WHERE (? IS NULL OR latitude >= ?) AND (? IS NULL OR latitude <= ?) AND (? IS NULL OR longitude >= ?) AND (? IS NULL OR longitude <= ?)"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        if (minLat == null) {
          _stmt.bindNull(_argIndex)
        } else {
          _stmt.bindDouble(_argIndex, minLat)
        }
        _argIndex = 2
        if (minLat == null) {
          _stmt.bindNull(_argIndex)
        } else {
          _stmt.bindDouble(_argIndex, minLat)
        }
        _argIndex = 3
        if (maxLat == null) {
          _stmt.bindNull(_argIndex)
        } else {
          _stmt.bindDouble(_argIndex, maxLat)
        }
        _argIndex = 4
        if (maxLat == null) {
          _stmt.bindNull(_argIndex)
        } else {
          _stmt.bindDouble(_argIndex, maxLat)
        }
        _argIndex = 5
        if (minLng == null) {
          _stmt.bindNull(_argIndex)
        } else {
          _stmt.bindDouble(_argIndex, minLng)
        }
        _argIndex = 6
        if (minLng == null) {
          _stmt.bindNull(_argIndex)
        } else {
          _stmt.bindDouble(_argIndex, minLng)
        }
        _argIndex = 7
        if (maxLng == null) {
          _stmt.bindNull(_argIndex)
        } else {
          _stmt.bindDouble(_argIndex, maxLng)
        }
        _argIndex = 8
        if (maxLng == null) {
          _stmt.bindNull(_argIndex)
        } else {
          _stmt.bindDouble(_argIndex, maxLng)
        }
        val _columnIndexOfHubId: Int = getColumnIndexOrThrow(_stmt, "hubId")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfLatitude: Int = getColumnIndexOrThrow(_stmt, "latitude")
        val _columnIndexOfLongitude: Int = getColumnIndexOrThrow(_stmt, "longitude")
        val _columnIndexOfAddress: Int = getColumnIndexOrThrow(_stmt, "address")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _result: MutableList<DeliveryHubEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: DeliveryHubEntity
          val _tmpHubId: String
          _tmpHubId = _stmt.getText(_columnIndexOfHubId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpLatitude: Double?
          if (_stmt.isNull(_columnIndexOfLatitude)) {
            _tmpLatitude = null
          } else {
            _tmpLatitude = _stmt.getDouble(_columnIndexOfLatitude)
          }
          val _tmpLongitude: Double?
          if (_stmt.isNull(_columnIndexOfLongitude)) {
            _tmpLongitude = null
          } else {
            _tmpLongitude = _stmt.getDouble(_columnIndexOfLongitude)
          }
          val _tmpAddress: String?
          if (_stmt.isNull(_columnIndexOfAddress)) {
            _tmpAddress = null
          } else {
            _tmpAddress = _stmt.getText(_columnIndexOfAddress)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          _item =
              DeliveryHubEntity(_tmpHubId,_tmpName,_tmpLatitude,_tmpLongitude,_tmpAddress,_tmpCreatedAt,_tmpUpdatedAt)
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
