package com.rio.rostry.`data`.database.dao

import androidx.room.EntityDeleteOrUpdateAdapter
import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.rio.rostry.`data`.database.entity.HubAssignmentEntity
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
import kotlinx.coroutines.flow.Flow

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class HubAssignmentDao_Impl(
  __db: RoomDatabase,
) : HubAssignmentDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfHubAssignmentEntity: EntityInsertAdapter<HubAssignmentEntity>

  private val __updateAdapterOfHubAssignmentEntity: EntityDeleteOrUpdateAdapter<HubAssignmentEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfHubAssignmentEntity = object : EntityInsertAdapter<HubAssignmentEntity>()
        {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `hub_assignments` (`product_id`,`hub_id`,`distance_km`,`assigned_at`,`seller_location_lat`,`seller_location_lon`) VALUES (?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: HubAssignmentEntity) {
        statement.bindText(1, entity.productId)
        statement.bindText(2, entity.hubId)
        statement.bindDouble(3, entity.distanceKm)
        statement.bindLong(4, entity.assignedAt)
        statement.bindDouble(5, entity.sellerLocationLat)
        statement.bindDouble(6, entity.sellerLocationLon)
      }
    }
    this.__updateAdapterOfHubAssignmentEntity = object :
        EntityDeleteOrUpdateAdapter<HubAssignmentEntity>() {
      protected override fun createQuery(): String =
          "UPDATE OR ABORT `hub_assignments` SET `product_id` = ?,`hub_id` = ?,`distance_km` = ?,`assigned_at` = ?,`seller_location_lat` = ?,`seller_location_lon` = ? WHERE `product_id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: HubAssignmentEntity) {
        statement.bindText(1, entity.productId)
        statement.bindText(2, entity.hubId)
        statement.bindDouble(3, entity.distanceKm)
        statement.bindLong(4, entity.assignedAt)
        statement.bindDouble(5, entity.sellerLocationLat)
        statement.bindDouble(6, entity.sellerLocationLon)
        statement.bindText(7, entity.productId)
      }
    }
  }

  public override suspend fun insert(hubAssignment: HubAssignmentEntity): Unit =
      performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfHubAssignmentEntity.insert(_connection, hubAssignment)
  }

  public override suspend fun insertAll(hubAssignments: List<HubAssignmentEntity>): Unit =
      performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfHubAssignmentEntity.insert(_connection, hubAssignments)
  }

  public override suspend fun update(hubAssignment: HubAssignmentEntity): Unit =
      performSuspending(__db, false, true) { _connection ->
    __updateAdapterOfHubAssignmentEntity.handle(_connection, hubAssignment)
  }

  public override suspend fun getByProductId(productId: String): HubAssignmentEntity? {
    val _sql: String = "SELECT * FROM hub_assignments WHERE product_id = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, productId)
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "product_id")
        val _columnIndexOfHubId: Int = getColumnIndexOrThrow(_stmt, "hub_id")
        val _columnIndexOfDistanceKm: Int = getColumnIndexOrThrow(_stmt, "distance_km")
        val _columnIndexOfAssignedAt: Int = getColumnIndexOrThrow(_stmt, "assigned_at")
        val _columnIndexOfSellerLocationLat: Int = getColumnIndexOrThrow(_stmt,
            "seller_location_lat")
        val _columnIndexOfSellerLocationLon: Int = getColumnIndexOrThrow(_stmt,
            "seller_location_lon")
        val _result: HubAssignmentEntity?
        if (_stmt.step()) {
          val _tmpProductId: String
          _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          val _tmpHubId: String
          _tmpHubId = _stmt.getText(_columnIndexOfHubId)
          val _tmpDistanceKm: Double
          _tmpDistanceKm = _stmt.getDouble(_columnIndexOfDistanceKm)
          val _tmpAssignedAt: Long
          _tmpAssignedAt = _stmt.getLong(_columnIndexOfAssignedAt)
          val _tmpSellerLocationLat: Double
          _tmpSellerLocationLat = _stmt.getDouble(_columnIndexOfSellerLocationLat)
          val _tmpSellerLocationLon: Double
          _tmpSellerLocationLon = _stmt.getDouble(_columnIndexOfSellerLocationLon)
          _result =
              HubAssignmentEntity(_tmpProductId,_tmpHubId,_tmpDistanceKm,_tmpAssignedAt,_tmpSellerLocationLat,_tmpSellerLocationLon)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeByProductId(productId: String): Flow<HubAssignmentEntity?> {
    val _sql: String = "SELECT * FROM hub_assignments WHERE product_id = ?"
    return createFlow(__db, false, arrayOf("hub_assignments")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, productId)
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "product_id")
        val _columnIndexOfHubId: Int = getColumnIndexOrThrow(_stmt, "hub_id")
        val _columnIndexOfDistanceKm: Int = getColumnIndexOrThrow(_stmt, "distance_km")
        val _columnIndexOfAssignedAt: Int = getColumnIndexOrThrow(_stmt, "assigned_at")
        val _columnIndexOfSellerLocationLat: Int = getColumnIndexOrThrow(_stmt,
            "seller_location_lat")
        val _columnIndexOfSellerLocationLon: Int = getColumnIndexOrThrow(_stmt,
            "seller_location_lon")
        val _result: HubAssignmentEntity?
        if (_stmt.step()) {
          val _tmpProductId: String
          _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          val _tmpHubId: String
          _tmpHubId = _stmt.getText(_columnIndexOfHubId)
          val _tmpDistanceKm: Double
          _tmpDistanceKm = _stmt.getDouble(_columnIndexOfDistanceKm)
          val _tmpAssignedAt: Long
          _tmpAssignedAt = _stmt.getLong(_columnIndexOfAssignedAt)
          val _tmpSellerLocationLat: Double
          _tmpSellerLocationLat = _stmt.getDouble(_columnIndexOfSellerLocationLat)
          val _tmpSellerLocationLon: Double
          _tmpSellerLocationLon = _stmt.getDouble(_columnIndexOfSellerLocationLon)
          _result =
              HubAssignmentEntity(_tmpProductId,_tmpHubId,_tmpDistanceKm,_tmpAssignedAt,_tmpSellerLocationLat,_tmpSellerLocationLon)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getByHubId(hubId: String): List<HubAssignmentEntity> {
    val _sql: String = "SELECT * FROM hub_assignments WHERE hub_id = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, hubId)
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "product_id")
        val _columnIndexOfHubId: Int = getColumnIndexOrThrow(_stmt, "hub_id")
        val _columnIndexOfDistanceKm: Int = getColumnIndexOrThrow(_stmt, "distance_km")
        val _columnIndexOfAssignedAt: Int = getColumnIndexOrThrow(_stmt, "assigned_at")
        val _columnIndexOfSellerLocationLat: Int = getColumnIndexOrThrow(_stmt,
            "seller_location_lat")
        val _columnIndexOfSellerLocationLon: Int = getColumnIndexOrThrow(_stmt,
            "seller_location_lon")
        val _result: MutableList<HubAssignmentEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: HubAssignmentEntity
          val _tmpProductId: String
          _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          val _tmpHubId: String
          _tmpHubId = _stmt.getText(_columnIndexOfHubId)
          val _tmpDistanceKm: Double
          _tmpDistanceKm = _stmt.getDouble(_columnIndexOfDistanceKm)
          val _tmpAssignedAt: Long
          _tmpAssignedAt = _stmt.getLong(_columnIndexOfAssignedAt)
          val _tmpSellerLocationLat: Double
          _tmpSellerLocationLat = _stmt.getDouble(_columnIndexOfSellerLocationLat)
          val _tmpSellerLocationLon: Double
          _tmpSellerLocationLon = _stmt.getDouble(_columnIndexOfSellerLocationLon)
          _item =
              HubAssignmentEntity(_tmpProductId,_tmpHubId,_tmpDistanceKm,_tmpAssignedAt,_tmpSellerLocationLat,_tmpSellerLocationLon)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeByHubId(hubId: String): Flow<List<HubAssignmentEntity>> {
    val _sql: String = "SELECT * FROM hub_assignments WHERE hub_id = ?"
    return createFlow(__db, false, arrayOf("hub_assignments")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, hubId)
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "product_id")
        val _columnIndexOfHubId: Int = getColumnIndexOrThrow(_stmt, "hub_id")
        val _columnIndexOfDistanceKm: Int = getColumnIndexOrThrow(_stmt, "distance_km")
        val _columnIndexOfAssignedAt: Int = getColumnIndexOrThrow(_stmt, "assigned_at")
        val _columnIndexOfSellerLocationLat: Int = getColumnIndexOrThrow(_stmt,
            "seller_location_lat")
        val _columnIndexOfSellerLocationLon: Int = getColumnIndexOrThrow(_stmt,
            "seller_location_lon")
        val _result: MutableList<HubAssignmentEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: HubAssignmentEntity
          val _tmpProductId: String
          _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          val _tmpHubId: String
          _tmpHubId = _stmt.getText(_columnIndexOfHubId)
          val _tmpDistanceKm: Double
          _tmpDistanceKm = _stmt.getDouble(_columnIndexOfDistanceKm)
          val _tmpAssignedAt: Long
          _tmpAssignedAt = _stmt.getLong(_columnIndexOfAssignedAt)
          val _tmpSellerLocationLat: Double
          _tmpSellerLocationLat = _stmt.getDouble(_columnIndexOfSellerLocationLat)
          val _tmpSellerLocationLon: Double
          _tmpSellerLocationLon = _stmt.getDouble(_columnIndexOfSellerLocationLon)
          _item =
              HubAssignmentEntity(_tmpProductId,_tmpHubId,_tmpDistanceKm,_tmpAssignedAt,_tmpSellerLocationLat,_tmpSellerLocationLon)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getHubLoadCount(hubId: String): Int {
    val _sql: String = "SELECT COUNT(*) FROM hub_assignments WHERE hub_id = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, hubId)
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

  public override suspend fun deleteByProductId(productId: String) {
    val _sql: String = "DELETE FROM hub_assignments WHERE product_id = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, productId)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun deleteAll() {
    val _sql: String = "DELETE FROM hub_assignments"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
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
