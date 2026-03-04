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
import com.rio.rostry.`data`.database.entity.FarmerDashboardSnapshotEntity
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
public class FarmerDashboardSnapshotDao_Impl(
  __db: RoomDatabase,
) : FarmerDashboardSnapshotDao {
  private val __db: RoomDatabase

  private val __upsertAdapterOfFarmerDashboardSnapshotEntity:
      EntityUpsertAdapter<FarmerDashboardSnapshotEntity>
  init {
    this.__db = __db
    this.__upsertAdapterOfFarmerDashboardSnapshotEntity =
        EntityUpsertAdapter<FarmerDashboardSnapshotEntity>(object :
        EntityInsertAdapter<FarmerDashboardSnapshotEntity>() {
      protected override fun createQuery(): String =
          "INSERT INTO `farmer_dashboard_snapshots` (`snapshotId`,`farmerId`,`weekStartAt`,`weekEndAt`,`revenueInr`,`ordersCount`,`hatchSuccessRate`,`mortalityRate`,`deathsCount`,`vaccinationCompletionRate`,`growthRecordsCount`,`quarantineActiveCount`,`productsReadyToListCount`,`avgFeedKg`,`medicationUsageCount`,`dailyLogComplianceRate`,`actionSuggestions`,`transfersInitiatedCount`,`transfersCompletedCount`,`complianceScore`,`onboardingCount`,`dailyGoalsCompletedCount`,`analyticsInsightsCount`,`createdAt`,`updatedAt`,`dirty`,`syncedAt`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement,
          entity: FarmerDashboardSnapshotEntity) {
        statement.bindText(1, entity.snapshotId)
        statement.bindText(2, entity.farmerId)
        statement.bindLong(3, entity.weekStartAt)
        statement.bindLong(4, entity.weekEndAt)
        statement.bindDouble(5, entity.revenueInr)
        statement.bindLong(6, entity.ordersCount.toLong())
        statement.bindDouble(7, entity.hatchSuccessRate)
        statement.bindDouble(8, entity.mortalityRate)
        statement.bindLong(9, entity.deathsCount.toLong())
        statement.bindDouble(10, entity.vaccinationCompletionRate)
        statement.bindLong(11, entity.growthRecordsCount.toLong())
        statement.bindLong(12, entity.quarantineActiveCount.toLong())
        statement.bindLong(13, entity.productsReadyToListCount.toLong())
        val _tmpAvgFeedKg: Double? = entity.avgFeedKg
        if (_tmpAvgFeedKg == null) {
          statement.bindNull(14)
        } else {
          statement.bindDouble(14, _tmpAvgFeedKg)
        }
        val _tmpMedicationUsageCount: Int? = entity.medicationUsageCount
        if (_tmpMedicationUsageCount == null) {
          statement.bindNull(15)
        } else {
          statement.bindLong(15, _tmpMedicationUsageCount.toLong())
        }
        val _tmpDailyLogComplianceRate: Double? = entity.dailyLogComplianceRate
        if (_tmpDailyLogComplianceRate == null) {
          statement.bindNull(16)
        } else {
          statement.bindDouble(16, _tmpDailyLogComplianceRate)
        }
        val _tmpActionSuggestions: String? = entity.actionSuggestions
        if (_tmpActionSuggestions == null) {
          statement.bindNull(17)
        } else {
          statement.bindText(17, _tmpActionSuggestions)
        }
        statement.bindLong(18, entity.transfersInitiatedCount.toLong())
        statement.bindLong(19, entity.transfersCompletedCount.toLong())
        statement.bindDouble(20, entity.complianceScore)
        statement.bindLong(21, entity.onboardingCount.toLong())
        statement.bindLong(22, entity.dailyGoalsCompletedCount.toLong())
        statement.bindLong(23, entity.analyticsInsightsCount.toLong())
        statement.bindLong(24, entity.createdAt)
        statement.bindLong(25, entity.updatedAt)
        val _tmp: Int = if (entity.dirty) 1 else 0
        statement.bindLong(26, _tmp.toLong())
        val _tmpSyncedAt: Long? = entity.syncedAt
        if (_tmpSyncedAt == null) {
          statement.bindNull(27)
        } else {
          statement.bindLong(27, _tmpSyncedAt)
        }
      }
    }, object : EntityDeleteOrUpdateAdapter<FarmerDashboardSnapshotEntity>() {
      protected override fun createQuery(): String =
          "UPDATE `farmer_dashboard_snapshots` SET `snapshotId` = ?,`farmerId` = ?,`weekStartAt` = ?,`weekEndAt` = ?,`revenueInr` = ?,`ordersCount` = ?,`hatchSuccessRate` = ?,`mortalityRate` = ?,`deathsCount` = ?,`vaccinationCompletionRate` = ?,`growthRecordsCount` = ?,`quarantineActiveCount` = ?,`productsReadyToListCount` = ?,`avgFeedKg` = ?,`medicationUsageCount` = ?,`dailyLogComplianceRate` = ?,`actionSuggestions` = ?,`transfersInitiatedCount` = ?,`transfersCompletedCount` = ?,`complianceScore` = ?,`onboardingCount` = ?,`dailyGoalsCompletedCount` = ?,`analyticsInsightsCount` = ?,`createdAt` = ?,`updatedAt` = ?,`dirty` = ?,`syncedAt` = ? WHERE `snapshotId` = ?"

      protected override fun bind(statement: SQLiteStatement,
          entity: FarmerDashboardSnapshotEntity) {
        statement.bindText(1, entity.snapshotId)
        statement.bindText(2, entity.farmerId)
        statement.bindLong(3, entity.weekStartAt)
        statement.bindLong(4, entity.weekEndAt)
        statement.bindDouble(5, entity.revenueInr)
        statement.bindLong(6, entity.ordersCount.toLong())
        statement.bindDouble(7, entity.hatchSuccessRate)
        statement.bindDouble(8, entity.mortalityRate)
        statement.bindLong(9, entity.deathsCount.toLong())
        statement.bindDouble(10, entity.vaccinationCompletionRate)
        statement.bindLong(11, entity.growthRecordsCount.toLong())
        statement.bindLong(12, entity.quarantineActiveCount.toLong())
        statement.bindLong(13, entity.productsReadyToListCount.toLong())
        val _tmpAvgFeedKg: Double? = entity.avgFeedKg
        if (_tmpAvgFeedKg == null) {
          statement.bindNull(14)
        } else {
          statement.bindDouble(14, _tmpAvgFeedKg)
        }
        val _tmpMedicationUsageCount: Int? = entity.medicationUsageCount
        if (_tmpMedicationUsageCount == null) {
          statement.bindNull(15)
        } else {
          statement.bindLong(15, _tmpMedicationUsageCount.toLong())
        }
        val _tmpDailyLogComplianceRate: Double? = entity.dailyLogComplianceRate
        if (_tmpDailyLogComplianceRate == null) {
          statement.bindNull(16)
        } else {
          statement.bindDouble(16, _tmpDailyLogComplianceRate)
        }
        val _tmpActionSuggestions: String? = entity.actionSuggestions
        if (_tmpActionSuggestions == null) {
          statement.bindNull(17)
        } else {
          statement.bindText(17, _tmpActionSuggestions)
        }
        statement.bindLong(18, entity.transfersInitiatedCount.toLong())
        statement.bindLong(19, entity.transfersCompletedCount.toLong())
        statement.bindDouble(20, entity.complianceScore)
        statement.bindLong(21, entity.onboardingCount.toLong())
        statement.bindLong(22, entity.dailyGoalsCompletedCount.toLong())
        statement.bindLong(23, entity.analyticsInsightsCount.toLong())
        statement.bindLong(24, entity.createdAt)
        statement.bindLong(25, entity.updatedAt)
        val _tmp: Int = if (entity.dirty) 1 else 0
        statement.bindLong(26, _tmp.toLong())
        val _tmpSyncedAt: Long? = entity.syncedAt
        if (_tmpSyncedAt == null) {
          statement.bindNull(27)
        } else {
          statement.bindLong(27, _tmpSyncedAt)
        }
        statement.bindText(28, entity.snapshotId)
      }
    })
  }

  public override suspend fun upsert(snapshot: FarmerDashboardSnapshotEntity): Unit =
      performSuspending(__db, false, true) { _connection ->
    __upsertAdapterOfFarmerDashboardSnapshotEntity.upsert(_connection, snapshot)
  }

  public override suspend fun getLatest(farmerId: String): FarmerDashboardSnapshotEntity? {
    val _sql: String =
        "SELECT * FROM farmer_dashboard_snapshots WHERE farmerId = ? ORDER BY weekStartAt DESC LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, farmerId)
        val _columnIndexOfSnapshotId: Int = getColumnIndexOrThrow(_stmt, "snapshotId")
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfWeekStartAt: Int = getColumnIndexOrThrow(_stmt, "weekStartAt")
        val _columnIndexOfWeekEndAt: Int = getColumnIndexOrThrow(_stmt, "weekEndAt")
        val _columnIndexOfRevenueInr: Int = getColumnIndexOrThrow(_stmt, "revenueInr")
        val _columnIndexOfOrdersCount: Int = getColumnIndexOrThrow(_stmt, "ordersCount")
        val _columnIndexOfHatchSuccessRate: Int = getColumnIndexOrThrow(_stmt, "hatchSuccessRate")
        val _columnIndexOfMortalityRate: Int = getColumnIndexOrThrow(_stmt, "mortalityRate")
        val _columnIndexOfDeathsCount: Int = getColumnIndexOrThrow(_stmt, "deathsCount")
        val _columnIndexOfVaccinationCompletionRate: Int = getColumnIndexOrThrow(_stmt,
            "vaccinationCompletionRate")
        val _columnIndexOfGrowthRecordsCount: Int = getColumnIndexOrThrow(_stmt,
            "growthRecordsCount")
        val _columnIndexOfQuarantineActiveCount: Int = getColumnIndexOrThrow(_stmt,
            "quarantineActiveCount")
        val _columnIndexOfProductsReadyToListCount: Int = getColumnIndexOrThrow(_stmt,
            "productsReadyToListCount")
        val _columnIndexOfAvgFeedKg: Int = getColumnIndexOrThrow(_stmt, "avgFeedKg")
        val _columnIndexOfMedicationUsageCount: Int = getColumnIndexOrThrow(_stmt,
            "medicationUsageCount")
        val _columnIndexOfDailyLogComplianceRate: Int = getColumnIndexOrThrow(_stmt,
            "dailyLogComplianceRate")
        val _columnIndexOfActionSuggestions: Int = getColumnIndexOrThrow(_stmt, "actionSuggestions")
        val _columnIndexOfTransfersInitiatedCount: Int = getColumnIndexOrThrow(_stmt,
            "transfersInitiatedCount")
        val _columnIndexOfTransfersCompletedCount: Int = getColumnIndexOrThrow(_stmt,
            "transfersCompletedCount")
        val _columnIndexOfComplianceScore: Int = getColumnIndexOrThrow(_stmt, "complianceScore")
        val _columnIndexOfOnboardingCount: Int = getColumnIndexOrThrow(_stmt, "onboardingCount")
        val _columnIndexOfDailyGoalsCompletedCount: Int = getColumnIndexOrThrow(_stmt,
            "dailyGoalsCompletedCount")
        val _columnIndexOfAnalyticsInsightsCount: Int = getColumnIndexOrThrow(_stmt,
            "analyticsInsightsCount")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _result: FarmerDashboardSnapshotEntity?
        if (_stmt.step()) {
          val _tmpSnapshotId: String
          _tmpSnapshotId = _stmt.getText(_columnIndexOfSnapshotId)
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpWeekStartAt: Long
          _tmpWeekStartAt = _stmt.getLong(_columnIndexOfWeekStartAt)
          val _tmpWeekEndAt: Long
          _tmpWeekEndAt = _stmt.getLong(_columnIndexOfWeekEndAt)
          val _tmpRevenueInr: Double
          _tmpRevenueInr = _stmt.getDouble(_columnIndexOfRevenueInr)
          val _tmpOrdersCount: Int
          _tmpOrdersCount = _stmt.getLong(_columnIndexOfOrdersCount).toInt()
          val _tmpHatchSuccessRate: Double
          _tmpHatchSuccessRate = _stmt.getDouble(_columnIndexOfHatchSuccessRate)
          val _tmpMortalityRate: Double
          _tmpMortalityRate = _stmt.getDouble(_columnIndexOfMortalityRate)
          val _tmpDeathsCount: Int
          _tmpDeathsCount = _stmt.getLong(_columnIndexOfDeathsCount).toInt()
          val _tmpVaccinationCompletionRate: Double
          _tmpVaccinationCompletionRate = _stmt.getDouble(_columnIndexOfVaccinationCompletionRate)
          val _tmpGrowthRecordsCount: Int
          _tmpGrowthRecordsCount = _stmt.getLong(_columnIndexOfGrowthRecordsCount).toInt()
          val _tmpQuarantineActiveCount: Int
          _tmpQuarantineActiveCount = _stmt.getLong(_columnIndexOfQuarantineActiveCount).toInt()
          val _tmpProductsReadyToListCount: Int
          _tmpProductsReadyToListCount =
              _stmt.getLong(_columnIndexOfProductsReadyToListCount).toInt()
          val _tmpAvgFeedKg: Double?
          if (_stmt.isNull(_columnIndexOfAvgFeedKg)) {
            _tmpAvgFeedKg = null
          } else {
            _tmpAvgFeedKg = _stmt.getDouble(_columnIndexOfAvgFeedKg)
          }
          val _tmpMedicationUsageCount: Int?
          if (_stmt.isNull(_columnIndexOfMedicationUsageCount)) {
            _tmpMedicationUsageCount = null
          } else {
            _tmpMedicationUsageCount = _stmt.getLong(_columnIndexOfMedicationUsageCount).toInt()
          }
          val _tmpDailyLogComplianceRate: Double?
          if (_stmt.isNull(_columnIndexOfDailyLogComplianceRate)) {
            _tmpDailyLogComplianceRate = null
          } else {
            _tmpDailyLogComplianceRate = _stmt.getDouble(_columnIndexOfDailyLogComplianceRate)
          }
          val _tmpActionSuggestions: String?
          if (_stmt.isNull(_columnIndexOfActionSuggestions)) {
            _tmpActionSuggestions = null
          } else {
            _tmpActionSuggestions = _stmt.getText(_columnIndexOfActionSuggestions)
          }
          val _tmpTransfersInitiatedCount: Int
          _tmpTransfersInitiatedCount = _stmt.getLong(_columnIndexOfTransfersInitiatedCount).toInt()
          val _tmpTransfersCompletedCount: Int
          _tmpTransfersCompletedCount = _stmt.getLong(_columnIndexOfTransfersCompletedCount).toInt()
          val _tmpComplianceScore: Double
          _tmpComplianceScore = _stmt.getDouble(_columnIndexOfComplianceScore)
          val _tmpOnboardingCount: Int
          _tmpOnboardingCount = _stmt.getLong(_columnIndexOfOnboardingCount).toInt()
          val _tmpDailyGoalsCompletedCount: Int
          _tmpDailyGoalsCompletedCount =
              _stmt.getLong(_columnIndexOfDailyGoalsCompletedCount).toInt()
          val _tmpAnalyticsInsightsCount: Int
          _tmpAnalyticsInsightsCount = _stmt.getLong(_columnIndexOfAnalyticsInsightsCount).toInt()
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
              FarmerDashboardSnapshotEntity(_tmpSnapshotId,_tmpFarmerId,_tmpWeekStartAt,_tmpWeekEndAt,_tmpRevenueInr,_tmpOrdersCount,_tmpHatchSuccessRate,_tmpMortalityRate,_tmpDeathsCount,_tmpVaccinationCompletionRate,_tmpGrowthRecordsCount,_tmpQuarantineActiveCount,_tmpProductsReadyToListCount,_tmpAvgFeedKg,_tmpMedicationUsageCount,_tmpDailyLogComplianceRate,_tmpActionSuggestions,_tmpTransfersInitiatedCount,_tmpTransfersCompletedCount,_tmpComplianceScore,_tmpOnboardingCount,_tmpDailyGoalsCompletedCount,_tmpAnalyticsInsightsCount,_tmpCreatedAt,_tmpUpdatedAt,_tmpDirty,_tmpSyncedAt)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeLatest(farmerId: String): Flow<FarmerDashboardSnapshotEntity?> {
    val _sql: String =
        "SELECT * FROM farmer_dashboard_snapshots WHERE farmerId = ? ORDER BY weekStartAt DESC LIMIT 1"
    return createFlow(__db, false, arrayOf("farmer_dashboard_snapshots")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, farmerId)
        val _columnIndexOfSnapshotId: Int = getColumnIndexOrThrow(_stmt, "snapshotId")
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfWeekStartAt: Int = getColumnIndexOrThrow(_stmt, "weekStartAt")
        val _columnIndexOfWeekEndAt: Int = getColumnIndexOrThrow(_stmt, "weekEndAt")
        val _columnIndexOfRevenueInr: Int = getColumnIndexOrThrow(_stmt, "revenueInr")
        val _columnIndexOfOrdersCount: Int = getColumnIndexOrThrow(_stmt, "ordersCount")
        val _columnIndexOfHatchSuccessRate: Int = getColumnIndexOrThrow(_stmt, "hatchSuccessRate")
        val _columnIndexOfMortalityRate: Int = getColumnIndexOrThrow(_stmt, "mortalityRate")
        val _columnIndexOfDeathsCount: Int = getColumnIndexOrThrow(_stmt, "deathsCount")
        val _columnIndexOfVaccinationCompletionRate: Int = getColumnIndexOrThrow(_stmt,
            "vaccinationCompletionRate")
        val _columnIndexOfGrowthRecordsCount: Int = getColumnIndexOrThrow(_stmt,
            "growthRecordsCount")
        val _columnIndexOfQuarantineActiveCount: Int = getColumnIndexOrThrow(_stmt,
            "quarantineActiveCount")
        val _columnIndexOfProductsReadyToListCount: Int = getColumnIndexOrThrow(_stmt,
            "productsReadyToListCount")
        val _columnIndexOfAvgFeedKg: Int = getColumnIndexOrThrow(_stmt, "avgFeedKg")
        val _columnIndexOfMedicationUsageCount: Int = getColumnIndexOrThrow(_stmt,
            "medicationUsageCount")
        val _columnIndexOfDailyLogComplianceRate: Int = getColumnIndexOrThrow(_stmt,
            "dailyLogComplianceRate")
        val _columnIndexOfActionSuggestions: Int = getColumnIndexOrThrow(_stmt, "actionSuggestions")
        val _columnIndexOfTransfersInitiatedCount: Int = getColumnIndexOrThrow(_stmt,
            "transfersInitiatedCount")
        val _columnIndexOfTransfersCompletedCount: Int = getColumnIndexOrThrow(_stmt,
            "transfersCompletedCount")
        val _columnIndexOfComplianceScore: Int = getColumnIndexOrThrow(_stmt, "complianceScore")
        val _columnIndexOfOnboardingCount: Int = getColumnIndexOrThrow(_stmt, "onboardingCount")
        val _columnIndexOfDailyGoalsCompletedCount: Int = getColumnIndexOrThrow(_stmt,
            "dailyGoalsCompletedCount")
        val _columnIndexOfAnalyticsInsightsCount: Int = getColumnIndexOrThrow(_stmt,
            "analyticsInsightsCount")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _result: FarmerDashboardSnapshotEntity?
        if (_stmt.step()) {
          val _tmpSnapshotId: String
          _tmpSnapshotId = _stmt.getText(_columnIndexOfSnapshotId)
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpWeekStartAt: Long
          _tmpWeekStartAt = _stmt.getLong(_columnIndexOfWeekStartAt)
          val _tmpWeekEndAt: Long
          _tmpWeekEndAt = _stmt.getLong(_columnIndexOfWeekEndAt)
          val _tmpRevenueInr: Double
          _tmpRevenueInr = _stmt.getDouble(_columnIndexOfRevenueInr)
          val _tmpOrdersCount: Int
          _tmpOrdersCount = _stmt.getLong(_columnIndexOfOrdersCount).toInt()
          val _tmpHatchSuccessRate: Double
          _tmpHatchSuccessRate = _stmt.getDouble(_columnIndexOfHatchSuccessRate)
          val _tmpMortalityRate: Double
          _tmpMortalityRate = _stmt.getDouble(_columnIndexOfMortalityRate)
          val _tmpDeathsCount: Int
          _tmpDeathsCount = _stmt.getLong(_columnIndexOfDeathsCount).toInt()
          val _tmpVaccinationCompletionRate: Double
          _tmpVaccinationCompletionRate = _stmt.getDouble(_columnIndexOfVaccinationCompletionRate)
          val _tmpGrowthRecordsCount: Int
          _tmpGrowthRecordsCount = _stmt.getLong(_columnIndexOfGrowthRecordsCount).toInt()
          val _tmpQuarantineActiveCount: Int
          _tmpQuarantineActiveCount = _stmt.getLong(_columnIndexOfQuarantineActiveCount).toInt()
          val _tmpProductsReadyToListCount: Int
          _tmpProductsReadyToListCount =
              _stmt.getLong(_columnIndexOfProductsReadyToListCount).toInt()
          val _tmpAvgFeedKg: Double?
          if (_stmt.isNull(_columnIndexOfAvgFeedKg)) {
            _tmpAvgFeedKg = null
          } else {
            _tmpAvgFeedKg = _stmt.getDouble(_columnIndexOfAvgFeedKg)
          }
          val _tmpMedicationUsageCount: Int?
          if (_stmt.isNull(_columnIndexOfMedicationUsageCount)) {
            _tmpMedicationUsageCount = null
          } else {
            _tmpMedicationUsageCount = _stmt.getLong(_columnIndexOfMedicationUsageCount).toInt()
          }
          val _tmpDailyLogComplianceRate: Double?
          if (_stmt.isNull(_columnIndexOfDailyLogComplianceRate)) {
            _tmpDailyLogComplianceRate = null
          } else {
            _tmpDailyLogComplianceRate = _stmt.getDouble(_columnIndexOfDailyLogComplianceRate)
          }
          val _tmpActionSuggestions: String?
          if (_stmt.isNull(_columnIndexOfActionSuggestions)) {
            _tmpActionSuggestions = null
          } else {
            _tmpActionSuggestions = _stmt.getText(_columnIndexOfActionSuggestions)
          }
          val _tmpTransfersInitiatedCount: Int
          _tmpTransfersInitiatedCount = _stmt.getLong(_columnIndexOfTransfersInitiatedCount).toInt()
          val _tmpTransfersCompletedCount: Int
          _tmpTransfersCompletedCount = _stmt.getLong(_columnIndexOfTransfersCompletedCount).toInt()
          val _tmpComplianceScore: Double
          _tmpComplianceScore = _stmt.getDouble(_columnIndexOfComplianceScore)
          val _tmpOnboardingCount: Int
          _tmpOnboardingCount = _stmt.getLong(_columnIndexOfOnboardingCount).toInt()
          val _tmpDailyGoalsCompletedCount: Int
          _tmpDailyGoalsCompletedCount =
              _stmt.getLong(_columnIndexOfDailyGoalsCompletedCount).toInt()
          val _tmpAnalyticsInsightsCount: Int
          _tmpAnalyticsInsightsCount = _stmt.getLong(_columnIndexOfAnalyticsInsightsCount).toInt()
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
              FarmerDashboardSnapshotEntity(_tmpSnapshotId,_tmpFarmerId,_tmpWeekStartAt,_tmpWeekEndAt,_tmpRevenueInr,_tmpOrdersCount,_tmpHatchSuccessRate,_tmpMortalityRate,_tmpDeathsCount,_tmpVaccinationCompletionRate,_tmpGrowthRecordsCount,_tmpQuarantineActiveCount,_tmpProductsReadyToListCount,_tmpAvgFeedKg,_tmpMedicationUsageCount,_tmpDailyLogComplianceRate,_tmpActionSuggestions,_tmpTransfersInitiatedCount,_tmpTransfersCompletedCount,_tmpComplianceScore,_tmpOnboardingCount,_tmpDailyGoalsCompletedCount,_tmpAnalyticsInsightsCount,_tmpCreatedAt,_tmpUpdatedAt,_tmpDirty,_tmpSyncedAt)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getByWeek(farmerId: String, weekStartAt: Long):
      FarmerDashboardSnapshotEntity? {
    val _sql: String =
        "SELECT * FROM farmer_dashboard_snapshots WHERE farmerId = ? AND weekStartAt = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, farmerId)
        _argIndex = 2
        _stmt.bindLong(_argIndex, weekStartAt)
        val _columnIndexOfSnapshotId: Int = getColumnIndexOrThrow(_stmt, "snapshotId")
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfWeekStartAt: Int = getColumnIndexOrThrow(_stmt, "weekStartAt")
        val _columnIndexOfWeekEndAt: Int = getColumnIndexOrThrow(_stmt, "weekEndAt")
        val _columnIndexOfRevenueInr: Int = getColumnIndexOrThrow(_stmt, "revenueInr")
        val _columnIndexOfOrdersCount: Int = getColumnIndexOrThrow(_stmt, "ordersCount")
        val _columnIndexOfHatchSuccessRate: Int = getColumnIndexOrThrow(_stmt, "hatchSuccessRate")
        val _columnIndexOfMortalityRate: Int = getColumnIndexOrThrow(_stmt, "mortalityRate")
        val _columnIndexOfDeathsCount: Int = getColumnIndexOrThrow(_stmt, "deathsCount")
        val _columnIndexOfVaccinationCompletionRate: Int = getColumnIndexOrThrow(_stmt,
            "vaccinationCompletionRate")
        val _columnIndexOfGrowthRecordsCount: Int = getColumnIndexOrThrow(_stmt,
            "growthRecordsCount")
        val _columnIndexOfQuarantineActiveCount: Int = getColumnIndexOrThrow(_stmt,
            "quarantineActiveCount")
        val _columnIndexOfProductsReadyToListCount: Int = getColumnIndexOrThrow(_stmt,
            "productsReadyToListCount")
        val _columnIndexOfAvgFeedKg: Int = getColumnIndexOrThrow(_stmt, "avgFeedKg")
        val _columnIndexOfMedicationUsageCount: Int = getColumnIndexOrThrow(_stmt,
            "medicationUsageCount")
        val _columnIndexOfDailyLogComplianceRate: Int = getColumnIndexOrThrow(_stmt,
            "dailyLogComplianceRate")
        val _columnIndexOfActionSuggestions: Int = getColumnIndexOrThrow(_stmt, "actionSuggestions")
        val _columnIndexOfTransfersInitiatedCount: Int = getColumnIndexOrThrow(_stmt,
            "transfersInitiatedCount")
        val _columnIndexOfTransfersCompletedCount: Int = getColumnIndexOrThrow(_stmt,
            "transfersCompletedCount")
        val _columnIndexOfComplianceScore: Int = getColumnIndexOrThrow(_stmt, "complianceScore")
        val _columnIndexOfOnboardingCount: Int = getColumnIndexOrThrow(_stmt, "onboardingCount")
        val _columnIndexOfDailyGoalsCompletedCount: Int = getColumnIndexOrThrow(_stmt,
            "dailyGoalsCompletedCount")
        val _columnIndexOfAnalyticsInsightsCount: Int = getColumnIndexOrThrow(_stmt,
            "analyticsInsightsCount")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _result: FarmerDashboardSnapshotEntity?
        if (_stmt.step()) {
          val _tmpSnapshotId: String
          _tmpSnapshotId = _stmt.getText(_columnIndexOfSnapshotId)
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpWeekStartAt: Long
          _tmpWeekStartAt = _stmt.getLong(_columnIndexOfWeekStartAt)
          val _tmpWeekEndAt: Long
          _tmpWeekEndAt = _stmt.getLong(_columnIndexOfWeekEndAt)
          val _tmpRevenueInr: Double
          _tmpRevenueInr = _stmt.getDouble(_columnIndexOfRevenueInr)
          val _tmpOrdersCount: Int
          _tmpOrdersCount = _stmt.getLong(_columnIndexOfOrdersCount).toInt()
          val _tmpHatchSuccessRate: Double
          _tmpHatchSuccessRate = _stmt.getDouble(_columnIndexOfHatchSuccessRate)
          val _tmpMortalityRate: Double
          _tmpMortalityRate = _stmt.getDouble(_columnIndexOfMortalityRate)
          val _tmpDeathsCount: Int
          _tmpDeathsCount = _stmt.getLong(_columnIndexOfDeathsCount).toInt()
          val _tmpVaccinationCompletionRate: Double
          _tmpVaccinationCompletionRate = _stmt.getDouble(_columnIndexOfVaccinationCompletionRate)
          val _tmpGrowthRecordsCount: Int
          _tmpGrowthRecordsCount = _stmt.getLong(_columnIndexOfGrowthRecordsCount).toInt()
          val _tmpQuarantineActiveCount: Int
          _tmpQuarantineActiveCount = _stmt.getLong(_columnIndexOfQuarantineActiveCount).toInt()
          val _tmpProductsReadyToListCount: Int
          _tmpProductsReadyToListCount =
              _stmt.getLong(_columnIndexOfProductsReadyToListCount).toInt()
          val _tmpAvgFeedKg: Double?
          if (_stmt.isNull(_columnIndexOfAvgFeedKg)) {
            _tmpAvgFeedKg = null
          } else {
            _tmpAvgFeedKg = _stmt.getDouble(_columnIndexOfAvgFeedKg)
          }
          val _tmpMedicationUsageCount: Int?
          if (_stmt.isNull(_columnIndexOfMedicationUsageCount)) {
            _tmpMedicationUsageCount = null
          } else {
            _tmpMedicationUsageCount = _stmt.getLong(_columnIndexOfMedicationUsageCount).toInt()
          }
          val _tmpDailyLogComplianceRate: Double?
          if (_stmt.isNull(_columnIndexOfDailyLogComplianceRate)) {
            _tmpDailyLogComplianceRate = null
          } else {
            _tmpDailyLogComplianceRate = _stmt.getDouble(_columnIndexOfDailyLogComplianceRate)
          }
          val _tmpActionSuggestions: String?
          if (_stmt.isNull(_columnIndexOfActionSuggestions)) {
            _tmpActionSuggestions = null
          } else {
            _tmpActionSuggestions = _stmt.getText(_columnIndexOfActionSuggestions)
          }
          val _tmpTransfersInitiatedCount: Int
          _tmpTransfersInitiatedCount = _stmt.getLong(_columnIndexOfTransfersInitiatedCount).toInt()
          val _tmpTransfersCompletedCount: Int
          _tmpTransfersCompletedCount = _stmt.getLong(_columnIndexOfTransfersCompletedCount).toInt()
          val _tmpComplianceScore: Double
          _tmpComplianceScore = _stmt.getDouble(_columnIndexOfComplianceScore)
          val _tmpOnboardingCount: Int
          _tmpOnboardingCount = _stmt.getLong(_columnIndexOfOnboardingCount).toInt()
          val _tmpDailyGoalsCompletedCount: Int
          _tmpDailyGoalsCompletedCount =
              _stmt.getLong(_columnIndexOfDailyGoalsCompletedCount).toInt()
          val _tmpAnalyticsInsightsCount: Int
          _tmpAnalyticsInsightsCount = _stmt.getLong(_columnIndexOfAnalyticsInsightsCount).toInt()
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
              FarmerDashboardSnapshotEntity(_tmpSnapshotId,_tmpFarmerId,_tmpWeekStartAt,_tmpWeekEndAt,_tmpRevenueInr,_tmpOrdersCount,_tmpHatchSuccessRate,_tmpMortalityRate,_tmpDeathsCount,_tmpVaccinationCompletionRate,_tmpGrowthRecordsCount,_tmpQuarantineActiveCount,_tmpProductsReadyToListCount,_tmpAvgFeedKg,_tmpMedicationUsageCount,_tmpDailyLogComplianceRate,_tmpActionSuggestions,_tmpTransfersInitiatedCount,_tmpTransfersCompletedCount,_tmpComplianceScore,_tmpOnboardingCount,_tmpDailyGoalsCompletedCount,_tmpAnalyticsInsightsCount,_tmpCreatedAt,_tmpUpdatedAt,_tmpDirty,_tmpSyncedAt)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeLastN(farmerId: String, limit: Int):
      Flow<List<FarmerDashboardSnapshotEntity>> {
    val _sql: String =
        "SELECT * FROM farmer_dashboard_snapshots WHERE farmerId = ? ORDER BY weekStartAt DESC LIMIT ?"
    return createFlow(__db, false, arrayOf("farmer_dashboard_snapshots")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, farmerId)
        _argIndex = 2
        _stmt.bindLong(_argIndex, limit.toLong())
        val _columnIndexOfSnapshotId: Int = getColumnIndexOrThrow(_stmt, "snapshotId")
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfWeekStartAt: Int = getColumnIndexOrThrow(_stmt, "weekStartAt")
        val _columnIndexOfWeekEndAt: Int = getColumnIndexOrThrow(_stmt, "weekEndAt")
        val _columnIndexOfRevenueInr: Int = getColumnIndexOrThrow(_stmt, "revenueInr")
        val _columnIndexOfOrdersCount: Int = getColumnIndexOrThrow(_stmt, "ordersCount")
        val _columnIndexOfHatchSuccessRate: Int = getColumnIndexOrThrow(_stmt, "hatchSuccessRate")
        val _columnIndexOfMortalityRate: Int = getColumnIndexOrThrow(_stmt, "mortalityRate")
        val _columnIndexOfDeathsCount: Int = getColumnIndexOrThrow(_stmt, "deathsCount")
        val _columnIndexOfVaccinationCompletionRate: Int = getColumnIndexOrThrow(_stmt,
            "vaccinationCompletionRate")
        val _columnIndexOfGrowthRecordsCount: Int = getColumnIndexOrThrow(_stmt,
            "growthRecordsCount")
        val _columnIndexOfQuarantineActiveCount: Int = getColumnIndexOrThrow(_stmt,
            "quarantineActiveCount")
        val _columnIndexOfProductsReadyToListCount: Int = getColumnIndexOrThrow(_stmt,
            "productsReadyToListCount")
        val _columnIndexOfAvgFeedKg: Int = getColumnIndexOrThrow(_stmt, "avgFeedKg")
        val _columnIndexOfMedicationUsageCount: Int = getColumnIndexOrThrow(_stmt,
            "medicationUsageCount")
        val _columnIndexOfDailyLogComplianceRate: Int = getColumnIndexOrThrow(_stmt,
            "dailyLogComplianceRate")
        val _columnIndexOfActionSuggestions: Int = getColumnIndexOrThrow(_stmt, "actionSuggestions")
        val _columnIndexOfTransfersInitiatedCount: Int = getColumnIndexOrThrow(_stmt,
            "transfersInitiatedCount")
        val _columnIndexOfTransfersCompletedCount: Int = getColumnIndexOrThrow(_stmt,
            "transfersCompletedCount")
        val _columnIndexOfComplianceScore: Int = getColumnIndexOrThrow(_stmt, "complianceScore")
        val _columnIndexOfOnboardingCount: Int = getColumnIndexOrThrow(_stmt, "onboardingCount")
        val _columnIndexOfDailyGoalsCompletedCount: Int = getColumnIndexOrThrow(_stmt,
            "dailyGoalsCompletedCount")
        val _columnIndexOfAnalyticsInsightsCount: Int = getColumnIndexOrThrow(_stmt,
            "analyticsInsightsCount")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _result: MutableList<FarmerDashboardSnapshotEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: FarmerDashboardSnapshotEntity
          val _tmpSnapshotId: String
          _tmpSnapshotId = _stmt.getText(_columnIndexOfSnapshotId)
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpWeekStartAt: Long
          _tmpWeekStartAt = _stmt.getLong(_columnIndexOfWeekStartAt)
          val _tmpWeekEndAt: Long
          _tmpWeekEndAt = _stmt.getLong(_columnIndexOfWeekEndAt)
          val _tmpRevenueInr: Double
          _tmpRevenueInr = _stmt.getDouble(_columnIndexOfRevenueInr)
          val _tmpOrdersCount: Int
          _tmpOrdersCount = _stmt.getLong(_columnIndexOfOrdersCount).toInt()
          val _tmpHatchSuccessRate: Double
          _tmpHatchSuccessRate = _stmt.getDouble(_columnIndexOfHatchSuccessRate)
          val _tmpMortalityRate: Double
          _tmpMortalityRate = _stmt.getDouble(_columnIndexOfMortalityRate)
          val _tmpDeathsCount: Int
          _tmpDeathsCount = _stmt.getLong(_columnIndexOfDeathsCount).toInt()
          val _tmpVaccinationCompletionRate: Double
          _tmpVaccinationCompletionRate = _stmt.getDouble(_columnIndexOfVaccinationCompletionRate)
          val _tmpGrowthRecordsCount: Int
          _tmpGrowthRecordsCount = _stmt.getLong(_columnIndexOfGrowthRecordsCount).toInt()
          val _tmpQuarantineActiveCount: Int
          _tmpQuarantineActiveCount = _stmt.getLong(_columnIndexOfQuarantineActiveCount).toInt()
          val _tmpProductsReadyToListCount: Int
          _tmpProductsReadyToListCount =
              _stmt.getLong(_columnIndexOfProductsReadyToListCount).toInt()
          val _tmpAvgFeedKg: Double?
          if (_stmt.isNull(_columnIndexOfAvgFeedKg)) {
            _tmpAvgFeedKg = null
          } else {
            _tmpAvgFeedKg = _stmt.getDouble(_columnIndexOfAvgFeedKg)
          }
          val _tmpMedicationUsageCount: Int?
          if (_stmt.isNull(_columnIndexOfMedicationUsageCount)) {
            _tmpMedicationUsageCount = null
          } else {
            _tmpMedicationUsageCount = _stmt.getLong(_columnIndexOfMedicationUsageCount).toInt()
          }
          val _tmpDailyLogComplianceRate: Double?
          if (_stmt.isNull(_columnIndexOfDailyLogComplianceRate)) {
            _tmpDailyLogComplianceRate = null
          } else {
            _tmpDailyLogComplianceRate = _stmt.getDouble(_columnIndexOfDailyLogComplianceRate)
          }
          val _tmpActionSuggestions: String?
          if (_stmt.isNull(_columnIndexOfActionSuggestions)) {
            _tmpActionSuggestions = null
          } else {
            _tmpActionSuggestions = _stmt.getText(_columnIndexOfActionSuggestions)
          }
          val _tmpTransfersInitiatedCount: Int
          _tmpTransfersInitiatedCount = _stmt.getLong(_columnIndexOfTransfersInitiatedCount).toInt()
          val _tmpTransfersCompletedCount: Int
          _tmpTransfersCompletedCount = _stmt.getLong(_columnIndexOfTransfersCompletedCount).toInt()
          val _tmpComplianceScore: Double
          _tmpComplianceScore = _stmt.getDouble(_columnIndexOfComplianceScore)
          val _tmpOnboardingCount: Int
          _tmpOnboardingCount = _stmt.getLong(_columnIndexOfOnboardingCount).toInt()
          val _tmpDailyGoalsCompletedCount: Int
          _tmpDailyGoalsCompletedCount =
              _stmt.getLong(_columnIndexOfDailyGoalsCompletedCount).toInt()
          val _tmpAnalyticsInsightsCount: Int
          _tmpAnalyticsInsightsCount = _stmt.getLong(_columnIndexOfAnalyticsInsightsCount).toInt()
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
              FarmerDashboardSnapshotEntity(_tmpSnapshotId,_tmpFarmerId,_tmpWeekStartAt,_tmpWeekEndAt,_tmpRevenueInr,_tmpOrdersCount,_tmpHatchSuccessRate,_tmpMortalityRate,_tmpDeathsCount,_tmpVaccinationCompletionRate,_tmpGrowthRecordsCount,_tmpQuarantineActiveCount,_tmpProductsReadyToListCount,_tmpAvgFeedKg,_tmpMedicationUsageCount,_tmpDailyLogComplianceRate,_tmpActionSuggestions,_tmpTransfersInitiatedCount,_tmpTransfersCompletedCount,_tmpComplianceScore,_tmpOnboardingCount,_tmpDailyGoalsCompletedCount,_tmpAnalyticsInsightsCount,_tmpCreatedAt,_tmpUpdatedAt,_tmpDirty,_tmpSyncedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getLastN(farmerId: String, limit: Int):
      List<FarmerDashboardSnapshotEntity> {
    val _sql: String =
        "SELECT * FROM farmer_dashboard_snapshots WHERE farmerId = ? ORDER BY weekStartAt DESC LIMIT ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, farmerId)
        _argIndex = 2
        _stmt.bindLong(_argIndex, limit.toLong())
        val _columnIndexOfSnapshotId: Int = getColumnIndexOrThrow(_stmt, "snapshotId")
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfWeekStartAt: Int = getColumnIndexOrThrow(_stmt, "weekStartAt")
        val _columnIndexOfWeekEndAt: Int = getColumnIndexOrThrow(_stmt, "weekEndAt")
        val _columnIndexOfRevenueInr: Int = getColumnIndexOrThrow(_stmt, "revenueInr")
        val _columnIndexOfOrdersCount: Int = getColumnIndexOrThrow(_stmt, "ordersCount")
        val _columnIndexOfHatchSuccessRate: Int = getColumnIndexOrThrow(_stmt, "hatchSuccessRate")
        val _columnIndexOfMortalityRate: Int = getColumnIndexOrThrow(_stmt, "mortalityRate")
        val _columnIndexOfDeathsCount: Int = getColumnIndexOrThrow(_stmt, "deathsCount")
        val _columnIndexOfVaccinationCompletionRate: Int = getColumnIndexOrThrow(_stmt,
            "vaccinationCompletionRate")
        val _columnIndexOfGrowthRecordsCount: Int = getColumnIndexOrThrow(_stmt,
            "growthRecordsCount")
        val _columnIndexOfQuarantineActiveCount: Int = getColumnIndexOrThrow(_stmt,
            "quarantineActiveCount")
        val _columnIndexOfProductsReadyToListCount: Int = getColumnIndexOrThrow(_stmt,
            "productsReadyToListCount")
        val _columnIndexOfAvgFeedKg: Int = getColumnIndexOrThrow(_stmt, "avgFeedKg")
        val _columnIndexOfMedicationUsageCount: Int = getColumnIndexOrThrow(_stmt,
            "medicationUsageCount")
        val _columnIndexOfDailyLogComplianceRate: Int = getColumnIndexOrThrow(_stmt,
            "dailyLogComplianceRate")
        val _columnIndexOfActionSuggestions: Int = getColumnIndexOrThrow(_stmt, "actionSuggestions")
        val _columnIndexOfTransfersInitiatedCount: Int = getColumnIndexOrThrow(_stmt,
            "transfersInitiatedCount")
        val _columnIndexOfTransfersCompletedCount: Int = getColumnIndexOrThrow(_stmt,
            "transfersCompletedCount")
        val _columnIndexOfComplianceScore: Int = getColumnIndexOrThrow(_stmt, "complianceScore")
        val _columnIndexOfOnboardingCount: Int = getColumnIndexOrThrow(_stmt, "onboardingCount")
        val _columnIndexOfDailyGoalsCompletedCount: Int = getColumnIndexOrThrow(_stmt,
            "dailyGoalsCompletedCount")
        val _columnIndexOfAnalyticsInsightsCount: Int = getColumnIndexOrThrow(_stmt,
            "analyticsInsightsCount")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _result: MutableList<FarmerDashboardSnapshotEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: FarmerDashboardSnapshotEntity
          val _tmpSnapshotId: String
          _tmpSnapshotId = _stmt.getText(_columnIndexOfSnapshotId)
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpWeekStartAt: Long
          _tmpWeekStartAt = _stmt.getLong(_columnIndexOfWeekStartAt)
          val _tmpWeekEndAt: Long
          _tmpWeekEndAt = _stmt.getLong(_columnIndexOfWeekEndAt)
          val _tmpRevenueInr: Double
          _tmpRevenueInr = _stmt.getDouble(_columnIndexOfRevenueInr)
          val _tmpOrdersCount: Int
          _tmpOrdersCount = _stmt.getLong(_columnIndexOfOrdersCount).toInt()
          val _tmpHatchSuccessRate: Double
          _tmpHatchSuccessRate = _stmt.getDouble(_columnIndexOfHatchSuccessRate)
          val _tmpMortalityRate: Double
          _tmpMortalityRate = _stmt.getDouble(_columnIndexOfMortalityRate)
          val _tmpDeathsCount: Int
          _tmpDeathsCount = _stmt.getLong(_columnIndexOfDeathsCount).toInt()
          val _tmpVaccinationCompletionRate: Double
          _tmpVaccinationCompletionRate = _stmt.getDouble(_columnIndexOfVaccinationCompletionRate)
          val _tmpGrowthRecordsCount: Int
          _tmpGrowthRecordsCount = _stmt.getLong(_columnIndexOfGrowthRecordsCount).toInt()
          val _tmpQuarantineActiveCount: Int
          _tmpQuarantineActiveCount = _stmt.getLong(_columnIndexOfQuarantineActiveCount).toInt()
          val _tmpProductsReadyToListCount: Int
          _tmpProductsReadyToListCount =
              _stmt.getLong(_columnIndexOfProductsReadyToListCount).toInt()
          val _tmpAvgFeedKg: Double?
          if (_stmt.isNull(_columnIndexOfAvgFeedKg)) {
            _tmpAvgFeedKg = null
          } else {
            _tmpAvgFeedKg = _stmt.getDouble(_columnIndexOfAvgFeedKg)
          }
          val _tmpMedicationUsageCount: Int?
          if (_stmt.isNull(_columnIndexOfMedicationUsageCount)) {
            _tmpMedicationUsageCount = null
          } else {
            _tmpMedicationUsageCount = _stmt.getLong(_columnIndexOfMedicationUsageCount).toInt()
          }
          val _tmpDailyLogComplianceRate: Double?
          if (_stmt.isNull(_columnIndexOfDailyLogComplianceRate)) {
            _tmpDailyLogComplianceRate = null
          } else {
            _tmpDailyLogComplianceRate = _stmt.getDouble(_columnIndexOfDailyLogComplianceRate)
          }
          val _tmpActionSuggestions: String?
          if (_stmt.isNull(_columnIndexOfActionSuggestions)) {
            _tmpActionSuggestions = null
          } else {
            _tmpActionSuggestions = _stmt.getText(_columnIndexOfActionSuggestions)
          }
          val _tmpTransfersInitiatedCount: Int
          _tmpTransfersInitiatedCount = _stmt.getLong(_columnIndexOfTransfersInitiatedCount).toInt()
          val _tmpTransfersCompletedCount: Int
          _tmpTransfersCompletedCount = _stmt.getLong(_columnIndexOfTransfersCompletedCount).toInt()
          val _tmpComplianceScore: Double
          _tmpComplianceScore = _stmt.getDouble(_columnIndexOfComplianceScore)
          val _tmpOnboardingCount: Int
          _tmpOnboardingCount = _stmt.getLong(_columnIndexOfOnboardingCount).toInt()
          val _tmpDailyGoalsCompletedCount: Int
          _tmpDailyGoalsCompletedCount =
              _stmt.getLong(_columnIndexOfDailyGoalsCompletedCount).toInt()
          val _tmpAnalyticsInsightsCount: Int
          _tmpAnalyticsInsightsCount = _stmt.getLong(_columnIndexOfAnalyticsInsightsCount).toInt()
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
              FarmerDashboardSnapshotEntity(_tmpSnapshotId,_tmpFarmerId,_tmpWeekStartAt,_tmpWeekEndAt,_tmpRevenueInr,_tmpOrdersCount,_tmpHatchSuccessRate,_tmpMortalityRate,_tmpDeathsCount,_tmpVaccinationCompletionRate,_tmpGrowthRecordsCount,_tmpQuarantineActiveCount,_tmpProductsReadyToListCount,_tmpAvgFeedKg,_tmpMedicationUsageCount,_tmpDailyLogComplianceRate,_tmpActionSuggestions,_tmpTransfersInitiatedCount,_tmpTransfersCompletedCount,_tmpComplianceScore,_tmpOnboardingCount,_tmpDailyGoalsCompletedCount,_tmpAnalyticsInsightsCount,_tmpCreatedAt,_tmpUpdatedAt,_tmpDirty,_tmpSyncedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getDirty(): List<FarmerDashboardSnapshotEntity> {
    val _sql: String = "SELECT * FROM farmer_dashboard_snapshots WHERE dirty = 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfSnapshotId: Int = getColumnIndexOrThrow(_stmt, "snapshotId")
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfWeekStartAt: Int = getColumnIndexOrThrow(_stmt, "weekStartAt")
        val _columnIndexOfWeekEndAt: Int = getColumnIndexOrThrow(_stmt, "weekEndAt")
        val _columnIndexOfRevenueInr: Int = getColumnIndexOrThrow(_stmt, "revenueInr")
        val _columnIndexOfOrdersCount: Int = getColumnIndexOrThrow(_stmt, "ordersCount")
        val _columnIndexOfHatchSuccessRate: Int = getColumnIndexOrThrow(_stmt, "hatchSuccessRate")
        val _columnIndexOfMortalityRate: Int = getColumnIndexOrThrow(_stmt, "mortalityRate")
        val _columnIndexOfDeathsCount: Int = getColumnIndexOrThrow(_stmt, "deathsCount")
        val _columnIndexOfVaccinationCompletionRate: Int = getColumnIndexOrThrow(_stmt,
            "vaccinationCompletionRate")
        val _columnIndexOfGrowthRecordsCount: Int = getColumnIndexOrThrow(_stmt,
            "growthRecordsCount")
        val _columnIndexOfQuarantineActiveCount: Int = getColumnIndexOrThrow(_stmt,
            "quarantineActiveCount")
        val _columnIndexOfProductsReadyToListCount: Int = getColumnIndexOrThrow(_stmt,
            "productsReadyToListCount")
        val _columnIndexOfAvgFeedKg: Int = getColumnIndexOrThrow(_stmt, "avgFeedKg")
        val _columnIndexOfMedicationUsageCount: Int = getColumnIndexOrThrow(_stmt,
            "medicationUsageCount")
        val _columnIndexOfDailyLogComplianceRate: Int = getColumnIndexOrThrow(_stmt,
            "dailyLogComplianceRate")
        val _columnIndexOfActionSuggestions: Int = getColumnIndexOrThrow(_stmt, "actionSuggestions")
        val _columnIndexOfTransfersInitiatedCount: Int = getColumnIndexOrThrow(_stmt,
            "transfersInitiatedCount")
        val _columnIndexOfTransfersCompletedCount: Int = getColumnIndexOrThrow(_stmt,
            "transfersCompletedCount")
        val _columnIndexOfComplianceScore: Int = getColumnIndexOrThrow(_stmt, "complianceScore")
        val _columnIndexOfOnboardingCount: Int = getColumnIndexOrThrow(_stmt, "onboardingCount")
        val _columnIndexOfDailyGoalsCompletedCount: Int = getColumnIndexOrThrow(_stmt,
            "dailyGoalsCompletedCount")
        val _columnIndexOfAnalyticsInsightsCount: Int = getColumnIndexOrThrow(_stmt,
            "analyticsInsightsCount")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _result: MutableList<FarmerDashboardSnapshotEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: FarmerDashboardSnapshotEntity
          val _tmpSnapshotId: String
          _tmpSnapshotId = _stmt.getText(_columnIndexOfSnapshotId)
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpWeekStartAt: Long
          _tmpWeekStartAt = _stmt.getLong(_columnIndexOfWeekStartAt)
          val _tmpWeekEndAt: Long
          _tmpWeekEndAt = _stmt.getLong(_columnIndexOfWeekEndAt)
          val _tmpRevenueInr: Double
          _tmpRevenueInr = _stmt.getDouble(_columnIndexOfRevenueInr)
          val _tmpOrdersCount: Int
          _tmpOrdersCount = _stmt.getLong(_columnIndexOfOrdersCount).toInt()
          val _tmpHatchSuccessRate: Double
          _tmpHatchSuccessRate = _stmt.getDouble(_columnIndexOfHatchSuccessRate)
          val _tmpMortalityRate: Double
          _tmpMortalityRate = _stmt.getDouble(_columnIndexOfMortalityRate)
          val _tmpDeathsCount: Int
          _tmpDeathsCount = _stmt.getLong(_columnIndexOfDeathsCount).toInt()
          val _tmpVaccinationCompletionRate: Double
          _tmpVaccinationCompletionRate = _stmt.getDouble(_columnIndexOfVaccinationCompletionRate)
          val _tmpGrowthRecordsCount: Int
          _tmpGrowthRecordsCount = _stmt.getLong(_columnIndexOfGrowthRecordsCount).toInt()
          val _tmpQuarantineActiveCount: Int
          _tmpQuarantineActiveCount = _stmt.getLong(_columnIndexOfQuarantineActiveCount).toInt()
          val _tmpProductsReadyToListCount: Int
          _tmpProductsReadyToListCount =
              _stmt.getLong(_columnIndexOfProductsReadyToListCount).toInt()
          val _tmpAvgFeedKg: Double?
          if (_stmt.isNull(_columnIndexOfAvgFeedKg)) {
            _tmpAvgFeedKg = null
          } else {
            _tmpAvgFeedKg = _stmt.getDouble(_columnIndexOfAvgFeedKg)
          }
          val _tmpMedicationUsageCount: Int?
          if (_stmt.isNull(_columnIndexOfMedicationUsageCount)) {
            _tmpMedicationUsageCount = null
          } else {
            _tmpMedicationUsageCount = _stmt.getLong(_columnIndexOfMedicationUsageCount).toInt()
          }
          val _tmpDailyLogComplianceRate: Double?
          if (_stmt.isNull(_columnIndexOfDailyLogComplianceRate)) {
            _tmpDailyLogComplianceRate = null
          } else {
            _tmpDailyLogComplianceRate = _stmt.getDouble(_columnIndexOfDailyLogComplianceRate)
          }
          val _tmpActionSuggestions: String?
          if (_stmt.isNull(_columnIndexOfActionSuggestions)) {
            _tmpActionSuggestions = null
          } else {
            _tmpActionSuggestions = _stmt.getText(_columnIndexOfActionSuggestions)
          }
          val _tmpTransfersInitiatedCount: Int
          _tmpTransfersInitiatedCount = _stmt.getLong(_columnIndexOfTransfersInitiatedCount).toInt()
          val _tmpTransfersCompletedCount: Int
          _tmpTransfersCompletedCount = _stmt.getLong(_columnIndexOfTransfersCompletedCount).toInt()
          val _tmpComplianceScore: Double
          _tmpComplianceScore = _stmt.getDouble(_columnIndexOfComplianceScore)
          val _tmpOnboardingCount: Int
          _tmpOnboardingCount = _stmt.getLong(_columnIndexOfOnboardingCount).toInt()
          val _tmpDailyGoalsCompletedCount: Int
          _tmpDailyGoalsCompletedCount =
              _stmt.getLong(_columnIndexOfDailyGoalsCompletedCount).toInt()
          val _tmpAnalyticsInsightsCount: Int
          _tmpAnalyticsInsightsCount = _stmt.getLong(_columnIndexOfAnalyticsInsightsCount).toInt()
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
              FarmerDashboardSnapshotEntity(_tmpSnapshotId,_tmpFarmerId,_tmpWeekStartAt,_tmpWeekEndAt,_tmpRevenueInr,_tmpOrdersCount,_tmpHatchSuccessRate,_tmpMortalityRate,_tmpDeathsCount,_tmpVaccinationCompletionRate,_tmpGrowthRecordsCount,_tmpQuarantineActiveCount,_tmpProductsReadyToListCount,_tmpAvgFeedKg,_tmpMedicationUsageCount,_tmpDailyLogComplianceRate,_tmpActionSuggestions,_tmpTransfersInitiatedCount,_tmpTransfersCompletedCount,_tmpComplianceScore,_tmpOnboardingCount,_tmpDailyGoalsCompletedCount,_tmpAnalyticsInsightsCount,_tmpCreatedAt,_tmpUpdatedAt,_tmpDirty,_tmpSyncedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun clearDirty(snapshotIds: List<String>, syncedAt: Long) {
    val _stringBuilder: StringBuilder = StringBuilder()
    _stringBuilder.append("UPDATE farmer_dashboard_snapshots SET dirty = 0, syncedAt = ")
    _stringBuilder.append("?")
    _stringBuilder.append(" WHERE snapshotId IN (")
    val _inputSize: Int = snapshotIds.size
    appendPlaceholders(_stringBuilder, _inputSize)
    _stringBuilder.append(")")
    val _sql: String = _stringBuilder.toString()
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, syncedAt)
        _argIndex = 2
        for (_item: String in snapshotIds) {
          _stmt.bindText(_argIndex, _item)
          _argIndex++
        }
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
