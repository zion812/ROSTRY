package com.rio.rostry.`data`.database.dao

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.rio.rostry.`data`.database.entity.DashboardCacheEntity
import javax.`annotation`.processing.Generated
import kotlin.Double
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import kotlin.reflect.KClass
import kotlinx.coroutines.flow.Flow

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class DashboardCacheDao_Impl(
  __db: RoomDatabase,
) : DashboardCacheDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfDashboardCacheEntity: EntityInsertAdapter<DashboardCacheEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfDashboardCacheEntity = object :
        EntityInsertAdapter<DashboardCacheEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `dashboard_cache` (`cacheId`,`farmerId`,`totalBirds`,`totalBatches`,`pendingVaccines`,`overdueVaccines`,`avgFcr`,`totalFeedKgThisMonth`,`totalMortalityThisMonth`,`estimatedHarvestDate`,`daysUntilHarvest`,`healthyCount`,`quarantinedCount`,`alertCount`,`computedAt`,`computationDurationMs`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: DashboardCacheEntity) {
        statement.bindText(1, entity.cacheId)
        statement.bindText(2, entity.farmerId)
        statement.bindLong(3, entity.totalBirds.toLong())
        statement.bindLong(4, entity.totalBatches.toLong())
        statement.bindLong(5, entity.pendingVaccines.toLong())
        statement.bindLong(6, entity.overdueVaccines.toLong())
        statement.bindDouble(7, entity.avgFcr)
        statement.bindDouble(8, entity.totalFeedKgThisMonth)
        statement.bindLong(9, entity.totalMortalityThisMonth.toLong())
        val _tmpEstimatedHarvestDate: Long? = entity.estimatedHarvestDate
        if (_tmpEstimatedHarvestDate == null) {
          statement.bindNull(10)
        } else {
          statement.bindLong(10, _tmpEstimatedHarvestDate)
        }
        val _tmpDaysUntilHarvest: Int? = entity.daysUntilHarvest
        if (_tmpDaysUntilHarvest == null) {
          statement.bindNull(11)
        } else {
          statement.bindLong(11, _tmpDaysUntilHarvest.toLong())
        }
        statement.bindLong(12, entity.healthyCount.toLong())
        statement.bindLong(13, entity.quarantinedCount.toLong())
        statement.bindLong(14, entity.alertCount.toLong())
        statement.bindLong(15, entity.computedAt)
        statement.bindLong(16, entity.computationDurationMs)
      }
    }
  }

  public override suspend fun upsert(cache: DashboardCacheEntity): Unit = performSuspending(__db,
      false, true) { _connection ->
    __insertAdapterOfDashboardCacheEntity.insert(_connection, cache)
  }

  public override suspend fun getForFarmer(farmerId: String): DashboardCacheEntity? {
    val _sql: String = "SELECT * FROM dashboard_cache WHERE farmerId = ? LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, farmerId)
        val _columnIndexOfCacheId: Int = getColumnIndexOrThrow(_stmt, "cacheId")
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfTotalBirds: Int = getColumnIndexOrThrow(_stmt, "totalBirds")
        val _columnIndexOfTotalBatches: Int = getColumnIndexOrThrow(_stmt, "totalBatches")
        val _columnIndexOfPendingVaccines: Int = getColumnIndexOrThrow(_stmt, "pendingVaccines")
        val _columnIndexOfOverdueVaccines: Int = getColumnIndexOrThrow(_stmt, "overdueVaccines")
        val _columnIndexOfAvgFcr: Int = getColumnIndexOrThrow(_stmt, "avgFcr")
        val _columnIndexOfTotalFeedKgThisMonth: Int = getColumnIndexOrThrow(_stmt,
            "totalFeedKgThisMonth")
        val _columnIndexOfTotalMortalityThisMonth: Int = getColumnIndexOrThrow(_stmt,
            "totalMortalityThisMonth")
        val _columnIndexOfEstimatedHarvestDate: Int = getColumnIndexOrThrow(_stmt,
            "estimatedHarvestDate")
        val _columnIndexOfDaysUntilHarvest: Int = getColumnIndexOrThrow(_stmt, "daysUntilHarvest")
        val _columnIndexOfHealthyCount: Int = getColumnIndexOrThrow(_stmt, "healthyCount")
        val _columnIndexOfQuarantinedCount: Int = getColumnIndexOrThrow(_stmt, "quarantinedCount")
        val _columnIndexOfAlertCount: Int = getColumnIndexOrThrow(_stmt, "alertCount")
        val _columnIndexOfComputedAt: Int = getColumnIndexOrThrow(_stmt, "computedAt")
        val _columnIndexOfComputationDurationMs: Int = getColumnIndexOrThrow(_stmt,
            "computationDurationMs")
        val _result: DashboardCacheEntity?
        if (_stmt.step()) {
          val _tmpCacheId: String
          _tmpCacheId = _stmt.getText(_columnIndexOfCacheId)
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpTotalBirds: Int
          _tmpTotalBirds = _stmt.getLong(_columnIndexOfTotalBirds).toInt()
          val _tmpTotalBatches: Int
          _tmpTotalBatches = _stmt.getLong(_columnIndexOfTotalBatches).toInt()
          val _tmpPendingVaccines: Int
          _tmpPendingVaccines = _stmt.getLong(_columnIndexOfPendingVaccines).toInt()
          val _tmpOverdueVaccines: Int
          _tmpOverdueVaccines = _stmt.getLong(_columnIndexOfOverdueVaccines).toInt()
          val _tmpAvgFcr: Double
          _tmpAvgFcr = _stmt.getDouble(_columnIndexOfAvgFcr)
          val _tmpTotalFeedKgThisMonth: Double
          _tmpTotalFeedKgThisMonth = _stmt.getDouble(_columnIndexOfTotalFeedKgThisMonth)
          val _tmpTotalMortalityThisMonth: Int
          _tmpTotalMortalityThisMonth = _stmt.getLong(_columnIndexOfTotalMortalityThisMonth).toInt()
          val _tmpEstimatedHarvestDate: Long?
          if (_stmt.isNull(_columnIndexOfEstimatedHarvestDate)) {
            _tmpEstimatedHarvestDate = null
          } else {
            _tmpEstimatedHarvestDate = _stmt.getLong(_columnIndexOfEstimatedHarvestDate)
          }
          val _tmpDaysUntilHarvest: Int?
          if (_stmt.isNull(_columnIndexOfDaysUntilHarvest)) {
            _tmpDaysUntilHarvest = null
          } else {
            _tmpDaysUntilHarvest = _stmt.getLong(_columnIndexOfDaysUntilHarvest).toInt()
          }
          val _tmpHealthyCount: Int
          _tmpHealthyCount = _stmt.getLong(_columnIndexOfHealthyCount).toInt()
          val _tmpQuarantinedCount: Int
          _tmpQuarantinedCount = _stmt.getLong(_columnIndexOfQuarantinedCount).toInt()
          val _tmpAlertCount: Int
          _tmpAlertCount = _stmt.getLong(_columnIndexOfAlertCount).toInt()
          val _tmpComputedAt: Long
          _tmpComputedAt = _stmt.getLong(_columnIndexOfComputedAt)
          val _tmpComputationDurationMs: Long
          _tmpComputationDurationMs = _stmt.getLong(_columnIndexOfComputationDurationMs)
          _result =
              DashboardCacheEntity(_tmpCacheId,_tmpFarmerId,_tmpTotalBirds,_tmpTotalBatches,_tmpPendingVaccines,_tmpOverdueVaccines,_tmpAvgFcr,_tmpTotalFeedKgThisMonth,_tmpTotalMortalityThisMonth,_tmpEstimatedHarvestDate,_tmpDaysUntilHarvest,_tmpHealthyCount,_tmpQuarantinedCount,_tmpAlertCount,_tmpComputedAt,_tmpComputationDurationMs)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeForFarmer(farmerId: String): Flow<DashboardCacheEntity?> {
    val _sql: String = "SELECT * FROM dashboard_cache WHERE farmerId = ? LIMIT 1"
    return createFlow(__db, false, arrayOf("dashboard_cache")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, farmerId)
        val _columnIndexOfCacheId: Int = getColumnIndexOrThrow(_stmt, "cacheId")
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfTotalBirds: Int = getColumnIndexOrThrow(_stmt, "totalBirds")
        val _columnIndexOfTotalBatches: Int = getColumnIndexOrThrow(_stmt, "totalBatches")
        val _columnIndexOfPendingVaccines: Int = getColumnIndexOrThrow(_stmt, "pendingVaccines")
        val _columnIndexOfOverdueVaccines: Int = getColumnIndexOrThrow(_stmt, "overdueVaccines")
        val _columnIndexOfAvgFcr: Int = getColumnIndexOrThrow(_stmt, "avgFcr")
        val _columnIndexOfTotalFeedKgThisMonth: Int = getColumnIndexOrThrow(_stmt,
            "totalFeedKgThisMonth")
        val _columnIndexOfTotalMortalityThisMonth: Int = getColumnIndexOrThrow(_stmt,
            "totalMortalityThisMonth")
        val _columnIndexOfEstimatedHarvestDate: Int = getColumnIndexOrThrow(_stmt,
            "estimatedHarvestDate")
        val _columnIndexOfDaysUntilHarvest: Int = getColumnIndexOrThrow(_stmt, "daysUntilHarvest")
        val _columnIndexOfHealthyCount: Int = getColumnIndexOrThrow(_stmt, "healthyCount")
        val _columnIndexOfQuarantinedCount: Int = getColumnIndexOrThrow(_stmt, "quarantinedCount")
        val _columnIndexOfAlertCount: Int = getColumnIndexOrThrow(_stmt, "alertCount")
        val _columnIndexOfComputedAt: Int = getColumnIndexOrThrow(_stmt, "computedAt")
        val _columnIndexOfComputationDurationMs: Int = getColumnIndexOrThrow(_stmt,
            "computationDurationMs")
        val _result: DashboardCacheEntity?
        if (_stmt.step()) {
          val _tmpCacheId: String
          _tmpCacheId = _stmt.getText(_columnIndexOfCacheId)
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpTotalBirds: Int
          _tmpTotalBirds = _stmt.getLong(_columnIndexOfTotalBirds).toInt()
          val _tmpTotalBatches: Int
          _tmpTotalBatches = _stmt.getLong(_columnIndexOfTotalBatches).toInt()
          val _tmpPendingVaccines: Int
          _tmpPendingVaccines = _stmt.getLong(_columnIndexOfPendingVaccines).toInt()
          val _tmpOverdueVaccines: Int
          _tmpOverdueVaccines = _stmt.getLong(_columnIndexOfOverdueVaccines).toInt()
          val _tmpAvgFcr: Double
          _tmpAvgFcr = _stmt.getDouble(_columnIndexOfAvgFcr)
          val _tmpTotalFeedKgThisMonth: Double
          _tmpTotalFeedKgThisMonth = _stmt.getDouble(_columnIndexOfTotalFeedKgThisMonth)
          val _tmpTotalMortalityThisMonth: Int
          _tmpTotalMortalityThisMonth = _stmt.getLong(_columnIndexOfTotalMortalityThisMonth).toInt()
          val _tmpEstimatedHarvestDate: Long?
          if (_stmt.isNull(_columnIndexOfEstimatedHarvestDate)) {
            _tmpEstimatedHarvestDate = null
          } else {
            _tmpEstimatedHarvestDate = _stmt.getLong(_columnIndexOfEstimatedHarvestDate)
          }
          val _tmpDaysUntilHarvest: Int?
          if (_stmt.isNull(_columnIndexOfDaysUntilHarvest)) {
            _tmpDaysUntilHarvest = null
          } else {
            _tmpDaysUntilHarvest = _stmt.getLong(_columnIndexOfDaysUntilHarvest).toInt()
          }
          val _tmpHealthyCount: Int
          _tmpHealthyCount = _stmt.getLong(_columnIndexOfHealthyCount).toInt()
          val _tmpQuarantinedCount: Int
          _tmpQuarantinedCount = _stmt.getLong(_columnIndexOfQuarantinedCount).toInt()
          val _tmpAlertCount: Int
          _tmpAlertCount = _stmt.getLong(_columnIndexOfAlertCount).toInt()
          val _tmpComputedAt: Long
          _tmpComputedAt = _stmt.getLong(_columnIndexOfComputedAt)
          val _tmpComputationDurationMs: Long
          _tmpComputationDurationMs = _stmt.getLong(_columnIndexOfComputationDurationMs)
          _result =
              DashboardCacheEntity(_tmpCacheId,_tmpFarmerId,_tmpTotalBirds,_tmpTotalBatches,_tmpPendingVaccines,_tmpOverdueVaccines,_tmpAvgFcr,_tmpTotalFeedKgThisMonth,_tmpTotalMortalityThisMonth,_tmpEstimatedHarvestDate,_tmpDaysUntilHarvest,_tmpHealthyCount,_tmpQuarantinedCount,_tmpAlertCount,_tmpComputedAt,_tmpComputationDurationMs)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getLastComputedAt(farmerId: String): Long? {
    val _sql: String = "SELECT computedAt FROM dashboard_cache WHERE farmerId = ? LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, farmerId)
        val _result: Long?
        if (_stmt.step()) {
          if (_stmt.isNull(0)) {
            _result = null
          } else {
            _result = _stmt.getLong(0)
          }
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun deleteForFarmer(farmerId: String) {
    val _sql: String = "DELETE FROM dashboard_cache WHERE farmerId = ?"
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

  public override suspend fun deleteStale(olderThan: Long) {
    val _sql: String = "DELETE FROM dashboard_cache WHERE computedAt < ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, olderThan)
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
