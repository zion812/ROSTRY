package com.rio.rostry.`data`.database.dao

import androidx.room.EntityDeleteOrUpdateAdapter
import androidx.room.EntityInsertAdapter
import androidx.room.EntityUpsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.rio.rostry.`data`.database.entity.SyncStateEntity
import javax.`annotation`.processing.Generated
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
public class SyncStateDao_Impl(
  __db: RoomDatabase,
) : SyncStateDao {
  private val __db: RoomDatabase

  private val __upsertAdapterOfSyncStateEntity: EntityUpsertAdapter<SyncStateEntity>
  init {
    this.__db = __db
    this.__upsertAdapterOfSyncStateEntity = EntityUpsertAdapter<SyncStateEntity>(object :
        EntityInsertAdapter<SyncStateEntity>() {
      protected override fun createQuery(): String =
          "INSERT INTO `sync_state` (`id`,`lastSyncAt`,`lastUserSyncAt`,`lastProductSyncAt`,`lastOrderSyncAt`,`lastTrackingSyncAt`,`lastTransferSyncAt`,`lastChatSyncAt`,`lastBreedingSyncAt`,`lastAlertSyncAt`,`lastDashboardSyncAt`,`lastVaccinationSyncAt`,`lastGrowthSyncAt`,`lastQuarantineSyncAt`,`lastMortalitySyncAt`,`lastHatchingSyncAt`,`lastHatchingLogSyncAt`,`lastEnthusiastBreedingSyncAt`,`lastEnthusiastDashboardSyncAt`,`lastDailyLogSyncAt`,`lastBatchSummarySyncAt`,`lastTaskSyncAt`,`lastExpenseSyncAt`,`lastProofSyncAt`,`lastGeneticAnalysisSyncAt`,`lastIoTDeviceSyncAt`,`lastIoTDataSyncAt`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: SyncStateEntity) {
        statement.bindText(1, entity.id)
        statement.bindLong(2, entity.lastSyncAt)
        statement.bindLong(3, entity.lastUserSyncAt)
        statement.bindLong(4, entity.lastProductSyncAt)
        statement.bindLong(5, entity.lastOrderSyncAt)
        statement.bindLong(6, entity.lastTrackingSyncAt)
        statement.bindLong(7, entity.lastTransferSyncAt)
        statement.bindLong(8, entity.lastChatSyncAt)
        statement.bindLong(9, entity.lastBreedingSyncAt)
        statement.bindLong(10, entity.lastAlertSyncAt)
        statement.bindLong(11, entity.lastDashboardSyncAt)
        statement.bindLong(12, entity.lastVaccinationSyncAt)
        statement.bindLong(13, entity.lastGrowthSyncAt)
        statement.bindLong(14, entity.lastQuarantineSyncAt)
        statement.bindLong(15, entity.lastMortalitySyncAt)
        statement.bindLong(16, entity.lastHatchingSyncAt)
        statement.bindLong(17, entity.lastHatchingLogSyncAt)
        statement.bindLong(18, entity.lastEnthusiastBreedingSyncAt)
        statement.bindLong(19, entity.lastEnthusiastDashboardSyncAt)
        statement.bindLong(20, entity.lastDailyLogSyncAt)
        statement.bindLong(21, entity.lastBatchSummarySyncAt)
        statement.bindLong(22, entity.lastTaskSyncAt)
        statement.bindLong(23, entity.lastExpenseSyncAt)
        statement.bindLong(24, entity.lastProofSyncAt)
        statement.bindLong(25, entity.lastGeneticAnalysisSyncAt)
        statement.bindLong(26, entity.lastIoTDeviceSyncAt)
        statement.bindLong(27, entity.lastIoTDataSyncAt)
      }
    }, object : EntityDeleteOrUpdateAdapter<SyncStateEntity>() {
      protected override fun createQuery(): String =
          "UPDATE `sync_state` SET `id` = ?,`lastSyncAt` = ?,`lastUserSyncAt` = ?,`lastProductSyncAt` = ?,`lastOrderSyncAt` = ?,`lastTrackingSyncAt` = ?,`lastTransferSyncAt` = ?,`lastChatSyncAt` = ?,`lastBreedingSyncAt` = ?,`lastAlertSyncAt` = ?,`lastDashboardSyncAt` = ?,`lastVaccinationSyncAt` = ?,`lastGrowthSyncAt` = ?,`lastQuarantineSyncAt` = ?,`lastMortalitySyncAt` = ?,`lastHatchingSyncAt` = ?,`lastHatchingLogSyncAt` = ?,`lastEnthusiastBreedingSyncAt` = ?,`lastEnthusiastDashboardSyncAt` = ?,`lastDailyLogSyncAt` = ?,`lastBatchSummarySyncAt` = ?,`lastTaskSyncAt` = ?,`lastExpenseSyncAt` = ?,`lastProofSyncAt` = ?,`lastGeneticAnalysisSyncAt` = ?,`lastIoTDeviceSyncAt` = ?,`lastIoTDataSyncAt` = ? WHERE `id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: SyncStateEntity) {
        statement.bindText(1, entity.id)
        statement.bindLong(2, entity.lastSyncAt)
        statement.bindLong(3, entity.lastUserSyncAt)
        statement.bindLong(4, entity.lastProductSyncAt)
        statement.bindLong(5, entity.lastOrderSyncAt)
        statement.bindLong(6, entity.lastTrackingSyncAt)
        statement.bindLong(7, entity.lastTransferSyncAt)
        statement.bindLong(8, entity.lastChatSyncAt)
        statement.bindLong(9, entity.lastBreedingSyncAt)
        statement.bindLong(10, entity.lastAlertSyncAt)
        statement.bindLong(11, entity.lastDashboardSyncAt)
        statement.bindLong(12, entity.lastVaccinationSyncAt)
        statement.bindLong(13, entity.lastGrowthSyncAt)
        statement.bindLong(14, entity.lastQuarantineSyncAt)
        statement.bindLong(15, entity.lastMortalitySyncAt)
        statement.bindLong(16, entity.lastHatchingSyncAt)
        statement.bindLong(17, entity.lastHatchingLogSyncAt)
        statement.bindLong(18, entity.lastEnthusiastBreedingSyncAt)
        statement.bindLong(19, entity.lastEnthusiastDashboardSyncAt)
        statement.bindLong(20, entity.lastDailyLogSyncAt)
        statement.bindLong(21, entity.lastBatchSummarySyncAt)
        statement.bindLong(22, entity.lastTaskSyncAt)
        statement.bindLong(23, entity.lastExpenseSyncAt)
        statement.bindLong(24, entity.lastProofSyncAt)
        statement.bindLong(25, entity.lastGeneticAnalysisSyncAt)
        statement.bindLong(26, entity.lastIoTDeviceSyncAt)
        statement.bindLong(27, entity.lastIoTDataSyncAt)
        statement.bindText(28, entity.id)
      }
    })
  }

  public override suspend fun upsert(state: SyncStateEntity): Unit = performSuspending(__db, false,
      true) { _connection ->
    __upsertAdapterOfSyncStateEntity.upsert(_connection, state)
  }

  public override fun observe(): Flow<SyncStateEntity?> {
    val _sql: String = "SELECT * FROM sync_state WHERE id = 'global' LIMIT 1"
    return createFlow(__db, false, arrayOf("sync_state")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfLastSyncAt: Int = getColumnIndexOrThrow(_stmt, "lastSyncAt")
        val _columnIndexOfLastUserSyncAt: Int = getColumnIndexOrThrow(_stmt, "lastUserSyncAt")
        val _columnIndexOfLastProductSyncAt: Int = getColumnIndexOrThrow(_stmt, "lastProductSyncAt")
        val _columnIndexOfLastOrderSyncAt: Int = getColumnIndexOrThrow(_stmt, "lastOrderSyncAt")
        val _columnIndexOfLastTrackingSyncAt: Int = getColumnIndexOrThrow(_stmt,
            "lastTrackingSyncAt")
        val _columnIndexOfLastTransferSyncAt: Int = getColumnIndexOrThrow(_stmt,
            "lastTransferSyncAt")
        val _columnIndexOfLastChatSyncAt: Int = getColumnIndexOrThrow(_stmt, "lastChatSyncAt")
        val _columnIndexOfLastBreedingSyncAt: Int = getColumnIndexOrThrow(_stmt,
            "lastBreedingSyncAt")
        val _columnIndexOfLastAlertSyncAt: Int = getColumnIndexOrThrow(_stmt, "lastAlertSyncAt")
        val _columnIndexOfLastDashboardSyncAt: Int = getColumnIndexOrThrow(_stmt,
            "lastDashboardSyncAt")
        val _columnIndexOfLastVaccinationSyncAt: Int = getColumnIndexOrThrow(_stmt,
            "lastVaccinationSyncAt")
        val _columnIndexOfLastGrowthSyncAt: Int = getColumnIndexOrThrow(_stmt, "lastGrowthSyncAt")
        val _columnIndexOfLastQuarantineSyncAt: Int = getColumnIndexOrThrow(_stmt,
            "lastQuarantineSyncAt")
        val _columnIndexOfLastMortalitySyncAt: Int = getColumnIndexOrThrow(_stmt,
            "lastMortalitySyncAt")
        val _columnIndexOfLastHatchingSyncAt: Int = getColumnIndexOrThrow(_stmt,
            "lastHatchingSyncAt")
        val _columnIndexOfLastHatchingLogSyncAt: Int = getColumnIndexOrThrow(_stmt,
            "lastHatchingLogSyncAt")
        val _columnIndexOfLastEnthusiastBreedingSyncAt: Int = getColumnIndexOrThrow(_stmt,
            "lastEnthusiastBreedingSyncAt")
        val _columnIndexOfLastEnthusiastDashboardSyncAt: Int = getColumnIndexOrThrow(_stmt,
            "lastEnthusiastDashboardSyncAt")
        val _columnIndexOfLastDailyLogSyncAt: Int = getColumnIndexOrThrow(_stmt,
            "lastDailyLogSyncAt")
        val _columnIndexOfLastBatchSummarySyncAt: Int = getColumnIndexOrThrow(_stmt,
            "lastBatchSummarySyncAt")
        val _columnIndexOfLastTaskSyncAt: Int = getColumnIndexOrThrow(_stmt, "lastTaskSyncAt")
        val _columnIndexOfLastExpenseSyncAt: Int = getColumnIndexOrThrow(_stmt, "lastExpenseSyncAt")
        val _columnIndexOfLastProofSyncAt: Int = getColumnIndexOrThrow(_stmt, "lastProofSyncAt")
        val _columnIndexOfLastGeneticAnalysisSyncAt: Int = getColumnIndexOrThrow(_stmt,
            "lastGeneticAnalysisSyncAt")
        val _columnIndexOfLastIoTDeviceSyncAt: Int = getColumnIndexOrThrow(_stmt,
            "lastIoTDeviceSyncAt")
        val _columnIndexOfLastIoTDataSyncAt: Int = getColumnIndexOrThrow(_stmt, "lastIoTDataSyncAt")
        val _result: SyncStateEntity?
        if (_stmt.step()) {
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpLastSyncAt: Long
          _tmpLastSyncAt = _stmt.getLong(_columnIndexOfLastSyncAt)
          val _tmpLastUserSyncAt: Long
          _tmpLastUserSyncAt = _stmt.getLong(_columnIndexOfLastUserSyncAt)
          val _tmpLastProductSyncAt: Long
          _tmpLastProductSyncAt = _stmt.getLong(_columnIndexOfLastProductSyncAt)
          val _tmpLastOrderSyncAt: Long
          _tmpLastOrderSyncAt = _stmt.getLong(_columnIndexOfLastOrderSyncAt)
          val _tmpLastTrackingSyncAt: Long
          _tmpLastTrackingSyncAt = _stmt.getLong(_columnIndexOfLastTrackingSyncAt)
          val _tmpLastTransferSyncAt: Long
          _tmpLastTransferSyncAt = _stmt.getLong(_columnIndexOfLastTransferSyncAt)
          val _tmpLastChatSyncAt: Long
          _tmpLastChatSyncAt = _stmt.getLong(_columnIndexOfLastChatSyncAt)
          val _tmpLastBreedingSyncAt: Long
          _tmpLastBreedingSyncAt = _stmt.getLong(_columnIndexOfLastBreedingSyncAt)
          val _tmpLastAlertSyncAt: Long
          _tmpLastAlertSyncAt = _stmt.getLong(_columnIndexOfLastAlertSyncAt)
          val _tmpLastDashboardSyncAt: Long
          _tmpLastDashboardSyncAt = _stmt.getLong(_columnIndexOfLastDashboardSyncAt)
          val _tmpLastVaccinationSyncAt: Long
          _tmpLastVaccinationSyncAt = _stmt.getLong(_columnIndexOfLastVaccinationSyncAt)
          val _tmpLastGrowthSyncAt: Long
          _tmpLastGrowthSyncAt = _stmt.getLong(_columnIndexOfLastGrowthSyncAt)
          val _tmpLastQuarantineSyncAt: Long
          _tmpLastQuarantineSyncAt = _stmt.getLong(_columnIndexOfLastQuarantineSyncAt)
          val _tmpLastMortalitySyncAt: Long
          _tmpLastMortalitySyncAt = _stmt.getLong(_columnIndexOfLastMortalitySyncAt)
          val _tmpLastHatchingSyncAt: Long
          _tmpLastHatchingSyncAt = _stmt.getLong(_columnIndexOfLastHatchingSyncAt)
          val _tmpLastHatchingLogSyncAt: Long
          _tmpLastHatchingLogSyncAt = _stmt.getLong(_columnIndexOfLastHatchingLogSyncAt)
          val _tmpLastEnthusiastBreedingSyncAt: Long
          _tmpLastEnthusiastBreedingSyncAt =
              _stmt.getLong(_columnIndexOfLastEnthusiastBreedingSyncAt)
          val _tmpLastEnthusiastDashboardSyncAt: Long
          _tmpLastEnthusiastDashboardSyncAt =
              _stmt.getLong(_columnIndexOfLastEnthusiastDashboardSyncAt)
          val _tmpLastDailyLogSyncAt: Long
          _tmpLastDailyLogSyncAt = _stmt.getLong(_columnIndexOfLastDailyLogSyncAt)
          val _tmpLastBatchSummarySyncAt: Long
          _tmpLastBatchSummarySyncAt = _stmt.getLong(_columnIndexOfLastBatchSummarySyncAt)
          val _tmpLastTaskSyncAt: Long
          _tmpLastTaskSyncAt = _stmt.getLong(_columnIndexOfLastTaskSyncAt)
          val _tmpLastExpenseSyncAt: Long
          _tmpLastExpenseSyncAt = _stmt.getLong(_columnIndexOfLastExpenseSyncAt)
          val _tmpLastProofSyncAt: Long
          _tmpLastProofSyncAt = _stmt.getLong(_columnIndexOfLastProofSyncAt)
          val _tmpLastGeneticAnalysisSyncAt: Long
          _tmpLastGeneticAnalysisSyncAt = _stmt.getLong(_columnIndexOfLastGeneticAnalysisSyncAt)
          val _tmpLastIoTDeviceSyncAt: Long
          _tmpLastIoTDeviceSyncAt = _stmt.getLong(_columnIndexOfLastIoTDeviceSyncAt)
          val _tmpLastIoTDataSyncAt: Long
          _tmpLastIoTDataSyncAt = _stmt.getLong(_columnIndexOfLastIoTDataSyncAt)
          _result =
              SyncStateEntity(_tmpId,_tmpLastSyncAt,_tmpLastUserSyncAt,_tmpLastProductSyncAt,_tmpLastOrderSyncAt,_tmpLastTrackingSyncAt,_tmpLastTransferSyncAt,_tmpLastChatSyncAt,_tmpLastBreedingSyncAt,_tmpLastAlertSyncAt,_tmpLastDashboardSyncAt,_tmpLastVaccinationSyncAt,_tmpLastGrowthSyncAt,_tmpLastQuarantineSyncAt,_tmpLastMortalitySyncAt,_tmpLastHatchingSyncAt,_tmpLastHatchingLogSyncAt,_tmpLastEnthusiastBreedingSyncAt,_tmpLastEnthusiastDashboardSyncAt,_tmpLastDailyLogSyncAt,_tmpLastBatchSummarySyncAt,_tmpLastTaskSyncAt,_tmpLastExpenseSyncAt,_tmpLastProofSyncAt,_tmpLastGeneticAnalysisSyncAt,_tmpLastIoTDeviceSyncAt,_tmpLastIoTDataSyncAt)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun `get`(): SyncStateEntity? {
    val _sql: String = "SELECT * FROM sync_state WHERE id = 'global' LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfLastSyncAt: Int = getColumnIndexOrThrow(_stmt, "lastSyncAt")
        val _columnIndexOfLastUserSyncAt: Int = getColumnIndexOrThrow(_stmt, "lastUserSyncAt")
        val _columnIndexOfLastProductSyncAt: Int = getColumnIndexOrThrow(_stmt, "lastProductSyncAt")
        val _columnIndexOfLastOrderSyncAt: Int = getColumnIndexOrThrow(_stmt, "lastOrderSyncAt")
        val _columnIndexOfLastTrackingSyncAt: Int = getColumnIndexOrThrow(_stmt,
            "lastTrackingSyncAt")
        val _columnIndexOfLastTransferSyncAt: Int = getColumnIndexOrThrow(_stmt,
            "lastTransferSyncAt")
        val _columnIndexOfLastChatSyncAt: Int = getColumnIndexOrThrow(_stmt, "lastChatSyncAt")
        val _columnIndexOfLastBreedingSyncAt: Int = getColumnIndexOrThrow(_stmt,
            "lastBreedingSyncAt")
        val _columnIndexOfLastAlertSyncAt: Int = getColumnIndexOrThrow(_stmt, "lastAlertSyncAt")
        val _columnIndexOfLastDashboardSyncAt: Int = getColumnIndexOrThrow(_stmt,
            "lastDashboardSyncAt")
        val _columnIndexOfLastVaccinationSyncAt: Int = getColumnIndexOrThrow(_stmt,
            "lastVaccinationSyncAt")
        val _columnIndexOfLastGrowthSyncAt: Int = getColumnIndexOrThrow(_stmt, "lastGrowthSyncAt")
        val _columnIndexOfLastQuarantineSyncAt: Int = getColumnIndexOrThrow(_stmt,
            "lastQuarantineSyncAt")
        val _columnIndexOfLastMortalitySyncAt: Int = getColumnIndexOrThrow(_stmt,
            "lastMortalitySyncAt")
        val _columnIndexOfLastHatchingSyncAt: Int = getColumnIndexOrThrow(_stmt,
            "lastHatchingSyncAt")
        val _columnIndexOfLastHatchingLogSyncAt: Int = getColumnIndexOrThrow(_stmt,
            "lastHatchingLogSyncAt")
        val _columnIndexOfLastEnthusiastBreedingSyncAt: Int = getColumnIndexOrThrow(_stmt,
            "lastEnthusiastBreedingSyncAt")
        val _columnIndexOfLastEnthusiastDashboardSyncAt: Int = getColumnIndexOrThrow(_stmt,
            "lastEnthusiastDashboardSyncAt")
        val _columnIndexOfLastDailyLogSyncAt: Int = getColumnIndexOrThrow(_stmt,
            "lastDailyLogSyncAt")
        val _columnIndexOfLastBatchSummarySyncAt: Int = getColumnIndexOrThrow(_stmt,
            "lastBatchSummarySyncAt")
        val _columnIndexOfLastTaskSyncAt: Int = getColumnIndexOrThrow(_stmt, "lastTaskSyncAt")
        val _columnIndexOfLastExpenseSyncAt: Int = getColumnIndexOrThrow(_stmt, "lastExpenseSyncAt")
        val _columnIndexOfLastProofSyncAt: Int = getColumnIndexOrThrow(_stmt, "lastProofSyncAt")
        val _columnIndexOfLastGeneticAnalysisSyncAt: Int = getColumnIndexOrThrow(_stmt,
            "lastGeneticAnalysisSyncAt")
        val _columnIndexOfLastIoTDeviceSyncAt: Int = getColumnIndexOrThrow(_stmt,
            "lastIoTDeviceSyncAt")
        val _columnIndexOfLastIoTDataSyncAt: Int = getColumnIndexOrThrow(_stmt, "lastIoTDataSyncAt")
        val _result: SyncStateEntity?
        if (_stmt.step()) {
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpLastSyncAt: Long
          _tmpLastSyncAt = _stmt.getLong(_columnIndexOfLastSyncAt)
          val _tmpLastUserSyncAt: Long
          _tmpLastUserSyncAt = _stmt.getLong(_columnIndexOfLastUserSyncAt)
          val _tmpLastProductSyncAt: Long
          _tmpLastProductSyncAt = _stmt.getLong(_columnIndexOfLastProductSyncAt)
          val _tmpLastOrderSyncAt: Long
          _tmpLastOrderSyncAt = _stmt.getLong(_columnIndexOfLastOrderSyncAt)
          val _tmpLastTrackingSyncAt: Long
          _tmpLastTrackingSyncAt = _stmt.getLong(_columnIndexOfLastTrackingSyncAt)
          val _tmpLastTransferSyncAt: Long
          _tmpLastTransferSyncAt = _stmt.getLong(_columnIndexOfLastTransferSyncAt)
          val _tmpLastChatSyncAt: Long
          _tmpLastChatSyncAt = _stmt.getLong(_columnIndexOfLastChatSyncAt)
          val _tmpLastBreedingSyncAt: Long
          _tmpLastBreedingSyncAt = _stmt.getLong(_columnIndexOfLastBreedingSyncAt)
          val _tmpLastAlertSyncAt: Long
          _tmpLastAlertSyncAt = _stmt.getLong(_columnIndexOfLastAlertSyncAt)
          val _tmpLastDashboardSyncAt: Long
          _tmpLastDashboardSyncAt = _stmt.getLong(_columnIndexOfLastDashboardSyncAt)
          val _tmpLastVaccinationSyncAt: Long
          _tmpLastVaccinationSyncAt = _stmt.getLong(_columnIndexOfLastVaccinationSyncAt)
          val _tmpLastGrowthSyncAt: Long
          _tmpLastGrowthSyncAt = _stmt.getLong(_columnIndexOfLastGrowthSyncAt)
          val _tmpLastQuarantineSyncAt: Long
          _tmpLastQuarantineSyncAt = _stmt.getLong(_columnIndexOfLastQuarantineSyncAt)
          val _tmpLastMortalitySyncAt: Long
          _tmpLastMortalitySyncAt = _stmt.getLong(_columnIndexOfLastMortalitySyncAt)
          val _tmpLastHatchingSyncAt: Long
          _tmpLastHatchingSyncAt = _stmt.getLong(_columnIndexOfLastHatchingSyncAt)
          val _tmpLastHatchingLogSyncAt: Long
          _tmpLastHatchingLogSyncAt = _stmt.getLong(_columnIndexOfLastHatchingLogSyncAt)
          val _tmpLastEnthusiastBreedingSyncAt: Long
          _tmpLastEnthusiastBreedingSyncAt =
              _stmt.getLong(_columnIndexOfLastEnthusiastBreedingSyncAt)
          val _tmpLastEnthusiastDashboardSyncAt: Long
          _tmpLastEnthusiastDashboardSyncAt =
              _stmt.getLong(_columnIndexOfLastEnthusiastDashboardSyncAt)
          val _tmpLastDailyLogSyncAt: Long
          _tmpLastDailyLogSyncAt = _stmt.getLong(_columnIndexOfLastDailyLogSyncAt)
          val _tmpLastBatchSummarySyncAt: Long
          _tmpLastBatchSummarySyncAt = _stmt.getLong(_columnIndexOfLastBatchSummarySyncAt)
          val _tmpLastTaskSyncAt: Long
          _tmpLastTaskSyncAt = _stmt.getLong(_columnIndexOfLastTaskSyncAt)
          val _tmpLastExpenseSyncAt: Long
          _tmpLastExpenseSyncAt = _stmt.getLong(_columnIndexOfLastExpenseSyncAt)
          val _tmpLastProofSyncAt: Long
          _tmpLastProofSyncAt = _stmt.getLong(_columnIndexOfLastProofSyncAt)
          val _tmpLastGeneticAnalysisSyncAt: Long
          _tmpLastGeneticAnalysisSyncAt = _stmt.getLong(_columnIndexOfLastGeneticAnalysisSyncAt)
          val _tmpLastIoTDeviceSyncAt: Long
          _tmpLastIoTDeviceSyncAt = _stmt.getLong(_columnIndexOfLastIoTDeviceSyncAt)
          val _tmpLastIoTDataSyncAt: Long
          _tmpLastIoTDataSyncAt = _stmt.getLong(_columnIndexOfLastIoTDataSyncAt)
          _result =
              SyncStateEntity(_tmpId,_tmpLastSyncAt,_tmpLastUserSyncAt,_tmpLastProductSyncAt,_tmpLastOrderSyncAt,_tmpLastTrackingSyncAt,_tmpLastTransferSyncAt,_tmpLastChatSyncAt,_tmpLastBreedingSyncAt,_tmpLastAlertSyncAt,_tmpLastDashboardSyncAt,_tmpLastVaccinationSyncAt,_tmpLastGrowthSyncAt,_tmpLastQuarantineSyncAt,_tmpLastMortalitySyncAt,_tmpLastHatchingSyncAt,_tmpLastHatchingLogSyncAt,_tmpLastEnthusiastBreedingSyncAt,_tmpLastEnthusiastDashboardSyncAt,_tmpLastDailyLogSyncAt,_tmpLastBatchSummarySyncAt,_tmpLastTaskSyncAt,_tmpLastExpenseSyncAt,_tmpLastProofSyncAt,_tmpLastGeneticAnalysisSyncAt,_tmpLastIoTDeviceSyncAt,_tmpLastIoTDataSyncAt)
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
