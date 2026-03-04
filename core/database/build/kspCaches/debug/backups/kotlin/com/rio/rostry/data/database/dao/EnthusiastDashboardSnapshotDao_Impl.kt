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
import com.rio.rostry.`data`.database.entity.EnthusiastDashboardSnapshotEntity
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
public class EnthusiastDashboardSnapshotDao_Impl(
  __db: RoomDatabase,
) : EnthusiastDashboardSnapshotDao {
  private val __db: RoomDatabase

  private val __upsertAdapterOfEnthusiastDashboardSnapshotEntity:
      EntityUpsertAdapter<EnthusiastDashboardSnapshotEntity>
  init {
    this.__db = __db
    this.__upsertAdapterOfEnthusiastDashboardSnapshotEntity =
        EntityUpsertAdapter<EnthusiastDashboardSnapshotEntity>(object :
        EntityInsertAdapter<EnthusiastDashboardSnapshotEntity>() {
      protected override fun createQuery(): String =
          "INSERT INTO `enthusiast_dashboard_snapshots` (`snapshotId`,`userId`,`weekStartAt`,`weekEndAt`,`hatchRateLast30Days`,`breederSuccessRate`,`disputedTransfersCount`,`topBloodlinesEngagement`,`activePairsCount`,`eggsCollectedCount`,`hatchingDueCount`,`transfersPendingCount`,`pairsToMateCount`,`incubatingCount`,`sickBirdsCount`,`eggsCollectedToday`,`createdAt`,`updatedAt`,`dirty`,`syncedAt`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement,
          entity: EnthusiastDashboardSnapshotEntity) {
        statement.bindText(1, entity.snapshotId)
        statement.bindText(2, entity.userId)
        statement.bindLong(3, entity.weekStartAt)
        statement.bindLong(4, entity.weekEndAt)
        statement.bindDouble(5, entity.hatchRateLast30Days)
        statement.bindDouble(6, entity.breederSuccessRate)
        statement.bindLong(7, entity.disputedTransfersCount.toLong())
        val _tmpTopBloodlinesEngagement: String? = entity.topBloodlinesEngagement
        if (_tmpTopBloodlinesEngagement == null) {
          statement.bindNull(8)
        } else {
          statement.bindText(8, _tmpTopBloodlinesEngagement)
        }
        statement.bindLong(9, entity.activePairsCount.toLong())
        statement.bindLong(10, entity.eggsCollectedCount.toLong())
        statement.bindLong(11, entity.hatchingDueCount.toLong())
        statement.bindLong(12, entity.transfersPendingCount.toLong())
        statement.bindLong(13, entity.pairsToMateCount.toLong())
        statement.bindLong(14, entity.incubatingCount.toLong())
        statement.bindLong(15, entity.sickBirdsCount.toLong())
        statement.bindLong(16, entity.eggsCollectedToday.toLong())
        statement.bindLong(17, entity.createdAt)
        statement.bindLong(18, entity.updatedAt)
        val _tmp: Int = if (entity.dirty) 1 else 0
        statement.bindLong(19, _tmp.toLong())
        val _tmpSyncedAt: Long? = entity.syncedAt
        if (_tmpSyncedAt == null) {
          statement.bindNull(20)
        } else {
          statement.bindLong(20, _tmpSyncedAt)
        }
      }
    }, object : EntityDeleteOrUpdateAdapter<EnthusiastDashboardSnapshotEntity>() {
      protected override fun createQuery(): String =
          "UPDATE `enthusiast_dashboard_snapshots` SET `snapshotId` = ?,`userId` = ?,`weekStartAt` = ?,`weekEndAt` = ?,`hatchRateLast30Days` = ?,`breederSuccessRate` = ?,`disputedTransfersCount` = ?,`topBloodlinesEngagement` = ?,`activePairsCount` = ?,`eggsCollectedCount` = ?,`hatchingDueCount` = ?,`transfersPendingCount` = ?,`pairsToMateCount` = ?,`incubatingCount` = ?,`sickBirdsCount` = ?,`eggsCollectedToday` = ?,`createdAt` = ?,`updatedAt` = ?,`dirty` = ?,`syncedAt` = ? WHERE `snapshotId` = ?"

      protected override fun bind(statement: SQLiteStatement,
          entity: EnthusiastDashboardSnapshotEntity) {
        statement.bindText(1, entity.snapshotId)
        statement.bindText(2, entity.userId)
        statement.bindLong(3, entity.weekStartAt)
        statement.bindLong(4, entity.weekEndAt)
        statement.bindDouble(5, entity.hatchRateLast30Days)
        statement.bindDouble(6, entity.breederSuccessRate)
        statement.bindLong(7, entity.disputedTransfersCount.toLong())
        val _tmpTopBloodlinesEngagement: String? = entity.topBloodlinesEngagement
        if (_tmpTopBloodlinesEngagement == null) {
          statement.bindNull(8)
        } else {
          statement.bindText(8, _tmpTopBloodlinesEngagement)
        }
        statement.bindLong(9, entity.activePairsCount.toLong())
        statement.bindLong(10, entity.eggsCollectedCount.toLong())
        statement.bindLong(11, entity.hatchingDueCount.toLong())
        statement.bindLong(12, entity.transfersPendingCount.toLong())
        statement.bindLong(13, entity.pairsToMateCount.toLong())
        statement.bindLong(14, entity.incubatingCount.toLong())
        statement.bindLong(15, entity.sickBirdsCount.toLong())
        statement.bindLong(16, entity.eggsCollectedToday.toLong())
        statement.bindLong(17, entity.createdAt)
        statement.bindLong(18, entity.updatedAt)
        val _tmp: Int = if (entity.dirty) 1 else 0
        statement.bindLong(19, _tmp.toLong())
        val _tmpSyncedAt: Long? = entity.syncedAt
        if (_tmpSyncedAt == null) {
          statement.bindNull(20)
        } else {
          statement.bindLong(20, _tmpSyncedAt)
        }
        statement.bindText(21, entity.snapshotId)
      }
    })
  }

  public override suspend fun upsert(snapshot: EnthusiastDashboardSnapshotEntity): Unit =
      performSuspending(__db, false, true) { _connection ->
    __upsertAdapterOfEnthusiastDashboardSnapshotEntity.upsert(_connection, snapshot)
  }

  public override suspend fun getLatest(userId: String): EnthusiastDashboardSnapshotEntity? {
    val _sql: String =
        "SELECT * FROM enthusiast_dashboard_snapshots WHERE userId = ? ORDER BY weekStartAt DESC LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, userId)
        val _columnIndexOfSnapshotId: Int = getColumnIndexOrThrow(_stmt, "snapshotId")
        val _columnIndexOfUserId: Int = getColumnIndexOrThrow(_stmt, "userId")
        val _columnIndexOfWeekStartAt: Int = getColumnIndexOrThrow(_stmt, "weekStartAt")
        val _columnIndexOfWeekEndAt: Int = getColumnIndexOrThrow(_stmt, "weekEndAt")
        val _columnIndexOfHatchRateLast30Days: Int = getColumnIndexOrThrow(_stmt,
            "hatchRateLast30Days")
        val _columnIndexOfBreederSuccessRate: Int = getColumnIndexOrThrow(_stmt,
            "breederSuccessRate")
        val _columnIndexOfDisputedTransfersCount: Int = getColumnIndexOrThrow(_stmt,
            "disputedTransfersCount")
        val _columnIndexOfTopBloodlinesEngagement: Int = getColumnIndexOrThrow(_stmt,
            "topBloodlinesEngagement")
        val _columnIndexOfActivePairsCount: Int = getColumnIndexOrThrow(_stmt, "activePairsCount")
        val _columnIndexOfEggsCollectedCount: Int = getColumnIndexOrThrow(_stmt,
            "eggsCollectedCount")
        val _columnIndexOfHatchingDueCount: Int = getColumnIndexOrThrow(_stmt, "hatchingDueCount")
        val _columnIndexOfTransfersPendingCount: Int = getColumnIndexOrThrow(_stmt,
            "transfersPendingCount")
        val _columnIndexOfPairsToMateCount: Int = getColumnIndexOrThrow(_stmt, "pairsToMateCount")
        val _columnIndexOfIncubatingCount: Int = getColumnIndexOrThrow(_stmt, "incubatingCount")
        val _columnIndexOfSickBirdsCount: Int = getColumnIndexOrThrow(_stmt, "sickBirdsCount")
        val _columnIndexOfEggsCollectedToday: Int = getColumnIndexOrThrow(_stmt,
            "eggsCollectedToday")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _result: EnthusiastDashboardSnapshotEntity?
        if (_stmt.step()) {
          val _tmpSnapshotId: String
          _tmpSnapshotId = _stmt.getText(_columnIndexOfSnapshotId)
          val _tmpUserId: String
          _tmpUserId = _stmt.getText(_columnIndexOfUserId)
          val _tmpWeekStartAt: Long
          _tmpWeekStartAt = _stmt.getLong(_columnIndexOfWeekStartAt)
          val _tmpWeekEndAt: Long
          _tmpWeekEndAt = _stmt.getLong(_columnIndexOfWeekEndAt)
          val _tmpHatchRateLast30Days: Double
          _tmpHatchRateLast30Days = _stmt.getDouble(_columnIndexOfHatchRateLast30Days)
          val _tmpBreederSuccessRate: Double
          _tmpBreederSuccessRate = _stmt.getDouble(_columnIndexOfBreederSuccessRate)
          val _tmpDisputedTransfersCount: Int
          _tmpDisputedTransfersCount = _stmt.getLong(_columnIndexOfDisputedTransfersCount).toInt()
          val _tmpTopBloodlinesEngagement: String?
          if (_stmt.isNull(_columnIndexOfTopBloodlinesEngagement)) {
            _tmpTopBloodlinesEngagement = null
          } else {
            _tmpTopBloodlinesEngagement = _stmt.getText(_columnIndexOfTopBloodlinesEngagement)
          }
          val _tmpActivePairsCount: Int
          _tmpActivePairsCount = _stmt.getLong(_columnIndexOfActivePairsCount).toInt()
          val _tmpEggsCollectedCount: Int
          _tmpEggsCollectedCount = _stmt.getLong(_columnIndexOfEggsCollectedCount).toInt()
          val _tmpHatchingDueCount: Int
          _tmpHatchingDueCount = _stmt.getLong(_columnIndexOfHatchingDueCount).toInt()
          val _tmpTransfersPendingCount: Int
          _tmpTransfersPendingCount = _stmt.getLong(_columnIndexOfTransfersPendingCount).toInt()
          val _tmpPairsToMateCount: Int
          _tmpPairsToMateCount = _stmt.getLong(_columnIndexOfPairsToMateCount).toInt()
          val _tmpIncubatingCount: Int
          _tmpIncubatingCount = _stmt.getLong(_columnIndexOfIncubatingCount).toInt()
          val _tmpSickBirdsCount: Int
          _tmpSickBirdsCount = _stmt.getLong(_columnIndexOfSickBirdsCount).toInt()
          val _tmpEggsCollectedToday: Int
          _tmpEggsCollectedToday = _stmt.getLong(_columnIndexOfEggsCollectedToday).toInt()
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
              EnthusiastDashboardSnapshotEntity(_tmpSnapshotId,_tmpUserId,_tmpWeekStartAt,_tmpWeekEndAt,_tmpHatchRateLast30Days,_tmpBreederSuccessRate,_tmpDisputedTransfersCount,_tmpTopBloodlinesEngagement,_tmpActivePairsCount,_tmpEggsCollectedCount,_tmpHatchingDueCount,_tmpTransfersPendingCount,_tmpPairsToMateCount,_tmpIncubatingCount,_tmpSickBirdsCount,_tmpEggsCollectedToday,_tmpCreatedAt,_tmpUpdatedAt,_tmpDirty,_tmpSyncedAt)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeLatest(userId: String): Flow<EnthusiastDashboardSnapshotEntity?> {
    val _sql: String =
        "SELECT * FROM enthusiast_dashboard_snapshots WHERE userId = ? ORDER BY weekStartAt DESC LIMIT 1"
    return createFlow(__db, false, arrayOf("enthusiast_dashboard_snapshots")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, userId)
        val _columnIndexOfSnapshotId: Int = getColumnIndexOrThrow(_stmt, "snapshotId")
        val _columnIndexOfUserId: Int = getColumnIndexOrThrow(_stmt, "userId")
        val _columnIndexOfWeekStartAt: Int = getColumnIndexOrThrow(_stmt, "weekStartAt")
        val _columnIndexOfWeekEndAt: Int = getColumnIndexOrThrow(_stmt, "weekEndAt")
        val _columnIndexOfHatchRateLast30Days: Int = getColumnIndexOrThrow(_stmt,
            "hatchRateLast30Days")
        val _columnIndexOfBreederSuccessRate: Int = getColumnIndexOrThrow(_stmt,
            "breederSuccessRate")
        val _columnIndexOfDisputedTransfersCount: Int = getColumnIndexOrThrow(_stmt,
            "disputedTransfersCount")
        val _columnIndexOfTopBloodlinesEngagement: Int = getColumnIndexOrThrow(_stmt,
            "topBloodlinesEngagement")
        val _columnIndexOfActivePairsCount: Int = getColumnIndexOrThrow(_stmt, "activePairsCount")
        val _columnIndexOfEggsCollectedCount: Int = getColumnIndexOrThrow(_stmt,
            "eggsCollectedCount")
        val _columnIndexOfHatchingDueCount: Int = getColumnIndexOrThrow(_stmt, "hatchingDueCount")
        val _columnIndexOfTransfersPendingCount: Int = getColumnIndexOrThrow(_stmt,
            "transfersPendingCount")
        val _columnIndexOfPairsToMateCount: Int = getColumnIndexOrThrow(_stmt, "pairsToMateCount")
        val _columnIndexOfIncubatingCount: Int = getColumnIndexOrThrow(_stmt, "incubatingCount")
        val _columnIndexOfSickBirdsCount: Int = getColumnIndexOrThrow(_stmt, "sickBirdsCount")
        val _columnIndexOfEggsCollectedToday: Int = getColumnIndexOrThrow(_stmt,
            "eggsCollectedToday")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _result: EnthusiastDashboardSnapshotEntity?
        if (_stmt.step()) {
          val _tmpSnapshotId: String
          _tmpSnapshotId = _stmt.getText(_columnIndexOfSnapshotId)
          val _tmpUserId: String
          _tmpUserId = _stmt.getText(_columnIndexOfUserId)
          val _tmpWeekStartAt: Long
          _tmpWeekStartAt = _stmt.getLong(_columnIndexOfWeekStartAt)
          val _tmpWeekEndAt: Long
          _tmpWeekEndAt = _stmt.getLong(_columnIndexOfWeekEndAt)
          val _tmpHatchRateLast30Days: Double
          _tmpHatchRateLast30Days = _stmt.getDouble(_columnIndexOfHatchRateLast30Days)
          val _tmpBreederSuccessRate: Double
          _tmpBreederSuccessRate = _stmt.getDouble(_columnIndexOfBreederSuccessRate)
          val _tmpDisputedTransfersCount: Int
          _tmpDisputedTransfersCount = _stmt.getLong(_columnIndexOfDisputedTransfersCount).toInt()
          val _tmpTopBloodlinesEngagement: String?
          if (_stmt.isNull(_columnIndexOfTopBloodlinesEngagement)) {
            _tmpTopBloodlinesEngagement = null
          } else {
            _tmpTopBloodlinesEngagement = _stmt.getText(_columnIndexOfTopBloodlinesEngagement)
          }
          val _tmpActivePairsCount: Int
          _tmpActivePairsCount = _stmt.getLong(_columnIndexOfActivePairsCount).toInt()
          val _tmpEggsCollectedCount: Int
          _tmpEggsCollectedCount = _stmt.getLong(_columnIndexOfEggsCollectedCount).toInt()
          val _tmpHatchingDueCount: Int
          _tmpHatchingDueCount = _stmt.getLong(_columnIndexOfHatchingDueCount).toInt()
          val _tmpTransfersPendingCount: Int
          _tmpTransfersPendingCount = _stmt.getLong(_columnIndexOfTransfersPendingCount).toInt()
          val _tmpPairsToMateCount: Int
          _tmpPairsToMateCount = _stmt.getLong(_columnIndexOfPairsToMateCount).toInt()
          val _tmpIncubatingCount: Int
          _tmpIncubatingCount = _stmt.getLong(_columnIndexOfIncubatingCount).toInt()
          val _tmpSickBirdsCount: Int
          _tmpSickBirdsCount = _stmt.getLong(_columnIndexOfSickBirdsCount).toInt()
          val _tmpEggsCollectedToday: Int
          _tmpEggsCollectedToday = _stmt.getLong(_columnIndexOfEggsCollectedToday).toInt()
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
              EnthusiastDashboardSnapshotEntity(_tmpSnapshotId,_tmpUserId,_tmpWeekStartAt,_tmpWeekEndAt,_tmpHatchRateLast30Days,_tmpBreederSuccessRate,_tmpDisputedTransfersCount,_tmpTopBloodlinesEngagement,_tmpActivePairsCount,_tmpEggsCollectedCount,_tmpHatchingDueCount,_tmpTransfersPendingCount,_tmpPairsToMateCount,_tmpIncubatingCount,_tmpSickBirdsCount,_tmpEggsCollectedToday,_tmpCreatedAt,_tmpUpdatedAt,_tmpDirty,_tmpSyncedAt)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getByWeek(userId: String, weekStartAt: Long):
      EnthusiastDashboardSnapshotEntity? {
    val _sql: String =
        "SELECT * FROM enthusiast_dashboard_snapshots WHERE userId = ? AND weekStartAt = ? LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, userId)
        _argIndex = 2
        _stmt.bindLong(_argIndex, weekStartAt)
        val _columnIndexOfSnapshotId: Int = getColumnIndexOrThrow(_stmt, "snapshotId")
        val _columnIndexOfUserId: Int = getColumnIndexOrThrow(_stmt, "userId")
        val _columnIndexOfWeekStartAt: Int = getColumnIndexOrThrow(_stmt, "weekStartAt")
        val _columnIndexOfWeekEndAt: Int = getColumnIndexOrThrow(_stmt, "weekEndAt")
        val _columnIndexOfHatchRateLast30Days: Int = getColumnIndexOrThrow(_stmt,
            "hatchRateLast30Days")
        val _columnIndexOfBreederSuccessRate: Int = getColumnIndexOrThrow(_stmt,
            "breederSuccessRate")
        val _columnIndexOfDisputedTransfersCount: Int = getColumnIndexOrThrow(_stmt,
            "disputedTransfersCount")
        val _columnIndexOfTopBloodlinesEngagement: Int = getColumnIndexOrThrow(_stmt,
            "topBloodlinesEngagement")
        val _columnIndexOfActivePairsCount: Int = getColumnIndexOrThrow(_stmt, "activePairsCount")
        val _columnIndexOfEggsCollectedCount: Int = getColumnIndexOrThrow(_stmt,
            "eggsCollectedCount")
        val _columnIndexOfHatchingDueCount: Int = getColumnIndexOrThrow(_stmt, "hatchingDueCount")
        val _columnIndexOfTransfersPendingCount: Int = getColumnIndexOrThrow(_stmt,
            "transfersPendingCount")
        val _columnIndexOfPairsToMateCount: Int = getColumnIndexOrThrow(_stmt, "pairsToMateCount")
        val _columnIndexOfIncubatingCount: Int = getColumnIndexOrThrow(_stmt, "incubatingCount")
        val _columnIndexOfSickBirdsCount: Int = getColumnIndexOrThrow(_stmt, "sickBirdsCount")
        val _columnIndexOfEggsCollectedToday: Int = getColumnIndexOrThrow(_stmt,
            "eggsCollectedToday")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _result: EnthusiastDashboardSnapshotEntity?
        if (_stmt.step()) {
          val _tmpSnapshotId: String
          _tmpSnapshotId = _stmt.getText(_columnIndexOfSnapshotId)
          val _tmpUserId: String
          _tmpUserId = _stmt.getText(_columnIndexOfUserId)
          val _tmpWeekStartAt: Long
          _tmpWeekStartAt = _stmt.getLong(_columnIndexOfWeekStartAt)
          val _tmpWeekEndAt: Long
          _tmpWeekEndAt = _stmt.getLong(_columnIndexOfWeekEndAt)
          val _tmpHatchRateLast30Days: Double
          _tmpHatchRateLast30Days = _stmt.getDouble(_columnIndexOfHatchRateLast30Days)
          val _tmpBreederSuccessRate: Double
          _tmpBreederSuccessRate = _stmt.getDouble(_columnIndexOfBreederSuccessRate)
          val _tmpDisputedTransfersCount: Int
          _tmpDisputedTransfersCount = _stmt.getLong(_columnIndexOfDisputedTransfersCount).toInt()
          val _tmpTopBloodlinesEngagement: String?
          if (_stmt.isNull(_columnIndexOfTopBloodlinesEngagement)) {
            _tmpTopBloodlinesEngagement = null
          } else {
            _tmpTopBloodlinesEngagement = _stmt.getText(_columnIndexOfTopBloodlinesEngagement)
          }
          val _tmpActivePairsCount: Int
          _tmpActivePairsCount = _stmt.getLong(_columnIndexOfActivePairsCount).toInt()
          val _tmpEggsCollectedCount: Int
          _tmpEggsCollectedCount = _stmt.getLong(_columnIndexOfEggsCollectedCount).toInt()
          val _tmpHatchingDueCount: Int
          _tmpHatchingDueCount = _stmt.getLong(_columnIndexOfHatchingDueCount).toInt()
          val _tmpTransfersPendingCount: Int
          _tmpTransfersPendingCount = _stmt.getLong(_columnIndexOfTransfersPendingCount).toInt()
          val _tmpPairsToMateCount: Int
          _tmpPairsToMateCount = _stmt.getLong(_columnIndexOfPairsToMateCount).toInt()
          val _tmpIncubatingCount: Int
          _tmpIncubatingCount = _stmt.getLong(_columnIndexOfIncubatingCount).toInt()
          val _tmpSickBirdsCount: Int
          _tmpSickBirdsCount = _stmt.getLong(_columnIndexOfSickBirdsCount).toInt()
          val _tmpEggsCollectedToday: Int
          _tmpEggsCollectedToday = _stmt.getLong(_columnIndexOfEggsCollectedToday).toInt()
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
              EnthusiastDashboardSnapshotEntity(_tmpSnapshotId,_tmpUserId,_tmpWeekStartAt,_tmpWeekEndAt,_tmpHatchRateLast30Days,_tmpBreederSuccessRate,_tmpDisputedTransfersCount,_tmpTopBloodlinesEngagement,_tmpActivePairsCount,_tmpEggsCollectedCount,_tmpHatchingDueCount,_tmpTransfersPendingCount,_tmpPairsToMateCount,_tmpIncubatingCount,_tmpSickBirdsCount,_tmpEggsCollectedToday,_tmpCreatedAt,_tmpUpdatedAt,_tmpDirty,_tmpSyncedAt)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getDirty(): List<EnthusiastDashboardSnapshotEntity> {
    val _sql: String = "SELECT * FROM enthusiast_dashboard_snapshots WHERE dirty = 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfSnapshotId: Int = getColumnIndexOrThrow(_stmt, "snapshotId")
        val _columnIndexOfUserId: Int = getColumnIndexOrThrow(_stmt, "userId")
        val _columnIndexOfWeekStartAt: Int = getColumnIndexOrThrow(_stmt, "weekStartAt")
        val _columnIndexOfWeekEndAt: Int = getColumnIndexOrThrow(_stmt, "weekEndAt")
        val _columnIndexOfHatchRateLast30Days: Int = getColumnIndexOrThrow(_stmt,
            "hatchRateLast30Days")
        val _columnIndexOfBreederSuccessRate: Int = getColumnIndexOrThrow(_stmt,
            "breederSuccessRate")
        val _columnIndexOfDisputedTransfersCount: Int = getColumnIndexOrThrow(_stmt,
            "disputedTransfersCount")
        val _columnIndexOfTopBloodlinesEngagement: Int = getColumnIndexOrThrow(_stmt,
            "topBloodlinesEngagement")
        val _columnIndexOfActivePairsCount: Int = getColumnIndexOrThrow(_stmt, "activePairsCount")
        val _columnIndexOfEggsCollectedCount: Int = getColumnIndexOrThrow(_stmt,
            "eggsCollectedCount")
        val _columnIndexOfHatchingDueCount: Int = getColumnIndexOrThrow(_stmt, "hatchingDueCount")
        val _columnIndexOfTransfersPendingCount: Int = getColumnIndexOrThrow(_stmt,
            "transfersPendingCount")
        val _columnIndexOfPairsToMateCount: Int = getColumnIndexOrThrow(_stmt, "pairsToMateCount")
        val _columnIndexOfIncubatingCount: Int = getColumnIndexOrThrow(_stmt, "incubatingCount")
        val _columnIndexOfSickBirdsCount: Int = getColumnIndexOrThrow(_stmt, "sickBirdsCount")
        val _columnIndexOfEggsCollectedToday: Int = getColumnIndexOrThrow(_stmt,
            "eggsCollectedToday")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _result: MutableList<EnthusiastDashboardSnapshotEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: EnthusiastDashboardSnapshotEntity
          val _tmpSnapshotId: String
          _tmpSnapshotId = _stmt.getText(_columnIndexOfSnapshotId)
          val _tmpUserId: String
          _tmpUserId = _stmt.getText(_columnIndexOfUserId)
          val _tmpWeekStartAt: Long
          _tmpWeekStartAt = _stmt.getLong(_columnIndexOfWeekStartAt)
          val _tmpWeekEndAt: Long
          _tmpWeekEndAt = _stmt.getLong(_columnIndexOfWeekEndAt)
          val _tmpHatchRateLast30Days: Double
          _tmpHatchRateLast30Days = _stmt.getDouble(_columnIndexOfHatchRateLast30Days)
          val _tmpBreederSuccessRate: Double
          _tmpBreederSuccessRate = _stmt.getDouble(_columnIndexOfBreederSuccessRate)
          val _tmpDisputedTransfersCount: Int
          _tmpDisputedTransfersCount = _stmt.getLong(_columnIndexOfDisputedTransfersCount).toInt()
          val _tmpTopBloodlinesEngagement: String?
          if (_stmt.isNull(_columnIndexOfTopBloodlinesEngagement)) {
            _tmpTopBloodlinesEngagement = null
          } else {
            _tmpTopBloodlinesEngagement = _stmt.getText(_columnIndexOfTopBloodlinesEngagement)
          }
          val _tmpActivePairsCount: Int
          _tmpActivePairsCount = _stmt.getLong(_columnIndexOfActivePairsCount).toInt()
          val _tmpEggsCollectedCount: Int
          _tmpEggsCollectedCount = _stmt.getLong(_columnIndexOfEggsCollectedCount).toInt()
          val _tmpHatchingDueCount: Int
          _tmpHatchingDueCount = _stmt.getLong(_columnIndexOfHatchingDueCount).toInt()
          val _tmpTransfersPendingCount: Int
          _tmpTransfersPendingCount = _stmt.getLong(_columnIndexOfTransfersPendingCount).toInt()
          val _tmpPairsToMateCount: Int
          _tmpPairsToMateCount = _stmt.getLong(_columnIndexOfPairsToMateCount).toInt()
          val _tmpIncubatingCount: Int
          _tmpIncubatingCount = _stmt.getLong(_columnIndexOfIncubatingCount).toInt()
          val _tmpSickBirdsCount: Int
          _tmpSickBirdsCount = _stmt.getLong(_columnIndexOfSickBirdsCount).toInt()
          val _tmpEggsCollectedToday: Int
          _tmpEggsCollectedToday = _stmt.getLong(_columnIndexOfEggsCollectedToday).toInt()
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
              EnthusiastDashboardSnapshotEntity(_tmpSnapshotId,_tmpUserId,_tmpWeekStartAt,_tmpWeekEndAt,_tmpHatchRateLast30Days,_tmpBreederSuccessRate,_tmpDisputedTransfersCount,_tmpTopBloodlinesEngagement,_tmpActivePairsCount,_tmpEggsCollectedCount,_tmpHatchingDueCount,_tmpTransfersPendingCount,_tmpPairsToMateCount,_tmpIncubatingCount,_tmpSickBirdsCount,_tmpEggsCollectedToday,_tmpCreatedAt,_tmpUpdatedAt,_tmpDirty,_tmpSyncedAt)
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
    _stringBuilder.append("UPDATE enthusiast_dashboard_snapshots SET dirty = 0, syncedAt = ")
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
