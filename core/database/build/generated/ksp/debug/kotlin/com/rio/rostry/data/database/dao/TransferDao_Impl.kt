package com.rio.rostry.`data`.database.dao

import androidx.paging.PagingSource
import androidx.room.EntityDeleteOrUpdateAdapter
import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.RoomRawQuery
import androidx.room.coroutines.createFlow
import androidx.room.paging.LimitOffsetPagingSource
import androidx.room.util.appendPlaceholders
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.getTotalChangedRows
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.rio.rostry.`data`.database.entity.TransferEntity
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
public class TransferDao_Impl(
  __db: RoomDatabase,
) : TransferDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfTransferEntity: EntityInsertAdapter<TransferEntity>

  private val __updateAdapterOfTransferEntity: EntityDeleteOrUpdateAdapter<TransferEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfTransferEntity = object : EntityInsertAdapter<TransferEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `transfers` (`transferId`,`productId`,`fromUserId`,`toUserId`,`orderId`,`amount`,`currency`,`type`,`status`,`transactionReference`,`notes`,`gpsLat`,`gpsLng`,`sellerPhotoUrl`,`buyerPhotoUrl`,`timeoutAt`,`conditionsJson`,`transferCode`,`lineageSnapshotJson`,`claimedAt`,`transferType`,`growthSnapshotJson`,`healthSnapshotJson`,`transferCodeExpiresAt`,`initiatedAt`,`completedAt`,`updatedAt`,`lastModifiedAt`,`isDeleted`,`deletedAt`,`dirty`,`syncedAt`,`mergedAt`,`mergeCount`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: TransferEntity) {
        statement.bindText(1, entity.transferId)
        val _tmpProductId: String? = entity.productId
        if (_tmpProductId == null) {
          statement.bindNull(2)
        } else {
          statement.bindText(2, _tmpProductId)
        }
        val _tmpFromUserId: String? = entity.fromUserId
        if (_tmpFromUserId == null) {
          statement.bindNull(3)
        } else {
          statement.bindText(3, _tmpFromUserId)
        }
        val _tmpToUserId: String? = entity.toUserId
        if (_tmpToUserId == null) {
          statement.bindNull(4)
        } else {
          statement.bindText(4, _tmpToUserId)
        }
        val _tmpOrderId: String? = entity.orderId
        if (_tmpOrderId == null) {
          statement.bindNull(5)
        } else {
          statement.bindText(5, _tmpOrderId)
        }
        statement.bindDouble(6, entity.amount)
        statement.bindText(7, entity.currency)
        statement.bindText(8, entity.type)
        statement.bindText(9, entity.status)
        val _tmpTransactionReference: String? = entity.transactionReference
        if (_tmpTransactionReference == null) {
          statement.bindNull(10)
        } else {
          statement.bindText(10, _tmpTransactionReference)
        }
        val _tmpNotes: String? = entity.notes
        if (_tmpNotes == null) {
          statement.bindNull(11)
        } else {
          statement.bindText(11, _tmpNotes)
        }
        val _tmpGpsLat: Double? = entity.gpsLat
        if (_tmpGpsLat == null) {
          statement.bindNull(12)
        } else {
          statement.bindDouble(12, _tmpGpsLat)
        }
        val _tmpGpsLng: Double? = entity.gpsLng
        if (_tmpGpsLng == null) {
          statement.bindNull(13)
        } else {
          statement.bindDouble(13, _tmpGpsLng)
        }
        val _tmpSellerPhotoUrl: String? = entity.sellerPhotoUrl
        if (_tmpSellerPhotoUrl == null) {
          statement.bindNull(14)
        } else {
          statement.bindText(14, _tmpSellerPhotoUrl)
        }
        val _tmpBuyerPhotoUrl: String? = entity.buyerPhotoUrl
        if (_tmpBuyerPhotoUrl == null) {
          statement.bindNull(15)
        } else {
          statement.bindText(15, _tmpBuyerPhotoUrl)
        }
        val _tmpTimeoutAt: Long? = entity.timeoutAt
        if (_tmpTimeoutAt == null) {
          statement.bindNull(16)
        } else {
          statement.bindLong(16, _tmpTimeoutAt)
        }
        val _tmpConditionsJson: String? = entity.conditionsJson
        if (_tmpConditionsJson == null) {
          statement.bindNull(17)
        } else {
          statement.bindText(17, _tmpConditionsJson)
        }
        val _tmpTransferCode: String? = entity.transferCode
        if (_tmpTransferCode == null) {
          statement.bindNull(18)
        } else {
          statement.bindText(18, _tmpTransferCode)
        }
        val _tmpLineageSnapshotJson: String? = entity.lineageSnapshotJson
        if (_tmpLineageSnapshotJson == null) {
          statement.bindNull(19)
        } else {
          statement.bindText(19, _tmpLineageSnapshotJson)
        }
        val _tmpClaimedAt: Long? = entity.claimedAt
        if (_tmpClaimedAt == null) {
          statement.bindNull(20)
        } else {
          statement.bindLong(20, _tmpClaimedAt)
        }
        statement.bindText(21, entity.transferType)
        val _tmpGrowthSnapshotJson: String? = entity.growthSnapshotJson
        if (_tmpGrowthSnapshotJson == null) {
          statement.bindNull(22)
        } else {
          statement.bindText(22, _tmpGrowthSnapshotJson)
        }
        val _tmpHealthSnapshotJson: String? = entity.healthSnapshotJson
        if (_tmpHealthSnapshotJson == null) {
          statement.bindNull(23)
        } else {
          statement.bindText(23, _tmpHealthSnapshotJson)
        }
        val _tmpTransferCodeExpiresAt: Long? = entity.transferCodeExpiresAt
        if (_tmpTransferCodeExpiresAt == null) {
          statement.bindNull(24)
        } else {
          statement.bindLong(24, _tmpTransferCodeExpiresAt)
        }
        statement.bindLong(25, entity.initiatedAt)
        val _tmpCompletedAt: Long? = entity.completedAt
        if (_tmpCompletedAt == null) {
          statement.bindNull(26)
        } else {
          statement.bindLong(26, _tmpCompletedAt)
        }
        statement.bindLong(27, entity.updatedAt)
        statement.bindLong(28, entity.lastModifiedAt)
        val _tmp: Int = if (entity.isDeleted) 1 else 0
        statement.bindLong(29, _tmp.toLong())
        val _tmpDeletedAt: Long? = entity.deletedAt
        if (_tmpDeletedAt == null) {
          statement.bindNull(30)
        } else {
          statement.bindLong(30, _tmpDeletedAt)
        }
        val _tmp_1: Int = if (entity.dirty) 1 else 0
        statement.bindLong(31, _tmp_1.toLong())
        val _tmpSyncedAt: Long? = entity.syncedAt
        if (_tmpSyncedAt == null) {
          statement.bindNull(32)
        } else {
          statement.bindLong(32, _tmpSyncedAt)
        }
        val _tmpMergedAt: Long? = entity.mergedAt
        if (_tmpMergedAt == null) {
          statement.bindNull(33)
        } else {
          statement.bindLong(33, _tmpMergedAt)
        }
        statement.bindLong(34, entity.mergeCount.toLong())
      }
    }
    this.__updateAdapterOfTransferEntity = object : EntityDeleteOrUpdateAdapter<TransferEntity>() {
      protected override fun createQuery(): String =
          "UPDATE OR ABORT `transfers` SET `transferId` = ?,`productId` = ?,`fromUserId` = ?,`toUserId` = ?,`orderId` = ?,`amount` = ?,`currency` = ?,`type` = ?,`status` = ?,`transactionReference` = ?,`notes` = ?,`gpsLat` = ?,`gpsLng` = ?,`sellerPhotoUrl` = ?,`buyerPhotoUrl` = ?,`timeoutAt` = ?,`conditionsJson` = ?,`transferCode` = ?,`lineageSnapshotJson` = ?,`claimedAt` = ?,`transferType` = ?,`growthSnapshotJson` = ?,`healthSnapshotJson` = ?,`transferCodeExpiresAt` = ?,`initiatedAt` = ?,`completedAt` = ?,`updatedAt` = ?,`lastModifiedAt` = ?,`isDeleted` = ?,`deletedAt` = ?,`dirty` = ?,`syncedAt` = ?,`mergedAt` = ?,`mergeCount` = ? WHERE `transferId` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: TransferEntity) {
        statement.bindText(1, entity.transferId)
        val _tmpProductId: String? = entity.productId
        if (_tmpProductId == null) {
          statement.bindNull(2)
        } else {
          statement.bindText(2, _tmpProductId)
        }
        val _tmpFromUserId: String? = entity.fromUserId
        if (_tmpFromUserId == null) {
          statement.bindNull(3)
        } else {
          statement.bindText(3, _tmpFromUserId)
        }
        val _tmpToUserId: String? = entity.toUserId
        if (_tmpToUserId == null) {
          statement.bindNull(4)
        } else {
          statement.bindText(4, _tmpToUserId)
        }
        val _tmpOrderId: String? = entity.orderId
        if (_tmpOrderId == null) {
          statement.bindNull(5)
        } else {
          statement.bindText(5, _tmpOrderId)
        }
        statement.bindDouble(6, entity.amount)
        statement.bindText(7, entity.currency)
        statement.bindText(8, entity.type)
        statement.bindText(9, entity.status)
        val _tmpTransactionReference: String? = entity.transactionReference
        if (_tmpTransactionReference == null) {
          statement.bindNull(10)
        } else {
          statement.bindText(10, _tmpTransactionReference)
        }
        val _tmpNotes: String? = entity.notes
        if (_tmpNotes == null) {
          statement.bindNull(11)
        } else {
          statement.bindText(11, _tmpNotes)
        }
        val _tmpGpsLat: Double? = entity.gpsLat
        if (_tmpGpsLat == null) {
          statement.bindNull(12)
        } else {
          statement.bindDouble(12, _tmpGpsLat)
        }
        val _tmpGpsLng: Double? = entity.gpsLng
        if (_tmpGpsLng == null) {
          statement.bindNull(13)
        } else {
          statement.bindDouble(13, _tmpGpsLng)
        }
        val _tmpSellerPhotoUrl: String? = entity.sellerPhotoUrl
        if (_tmpSellerPhotoUrl == null) {
          statement.bindNull(14)
        } else {
          statement.bindText(14, _tmpSellerPhotoUrl)
        }
        val _tmpBuyerPhotoUrl: String? = entity.buyerPhotoUrl
        if (_tmpBuyerPhotoUrl == null) {
          statement.bindNull(15)
        } else {
          statement.bindText(15, _tmpBuyerPhotoUrl)
        }
        val _tmpTimeoutAt: Long? = entity.timeoutAt
        if (_tmpTimeoutAt == null) {
          statement.bindNull(16)
        } else {
          statement.bindLong(16, _tmpTimeoutAt)
        }
        val _tmpConditionsJson: String? = entity.conditionsJson
        if (_tmpConditionsJson == null) {
          statement.bindNull(17)
        } else {
          statement.bindText(17, _tmpConditionsJson)
        }
        val _tmpTransferCode: String? = entity.transferCode
        if (_tmpTransferCode == null) {
          statement.bindNull(18)
        } else {
          statement.bindText(18, _tmpTransferCode)
        }
        val _tmpLineageSnapshotJson: String? = entity.lineageSnapshotJson
        if (_tmpLineageSnapshotJson == null) {
          statement.bindNull(19)
        } else {
          statement.bindText(19, _tmpLineageSnapshotJson)
        }
        val _tmpClaimedAt: Long? = entity.claimedAt
        if (_tmpClaimedAt == null) {
          statement.bindNull(20)
        } else {
          statement.bindLong(20, _tmpClaimedAt)
        }
        statement.bindText(21, entity.transferType)
        val _tmpGrowthSnapshotJson: String? = entity.growthSnapshotJson
        if (_tmpGrowthSnapshotJson == null) {
          statement.bindNull(22)
        } else {
          statement.bindText(22, _tmpGrowthSnapshotJson)
        }
        val _tmpHealthSnapshotJson: String? = entity.healthSnapshotJson
        if (_tmpHealthSnapshotJson == null) {
          statement.bindNull(23)
        } else {
          statement.bindText(23, _tmpHealthSnapshotJson)
        }
        val _tmpTransferCodeExpiresAt: Long? = entity.transferCodeExpiresAt
        if (_tmpTransferCodeExpiresAt == null) {
          statement.bindNull(24)
        } else {
          statement.bindLong(24, _tmpTransferCodeExpiresAt)
        }
        statement.bindLong(25, entity.initiatedAt)
        val _tmpCompletedAt: Long? = entity.completedAt
        if (_tmpCompletedAt == null) {
          statement.bindNull(26)
        } else {
          statement.bindLong(26, _tmpCompletedAt)
        }
        statement.bindLong(27, entity.updatedAt)
        statement.bindLong(28, entity.lastModifiedAt)
        val _tmp: Int = if (entity.isDeleted) 1 else 0
        statement.bindLong(29, _tmp.toLong())
        val _tmpDeletedAt: Long? = entity.deletedAt
        if (_tmpDeletedAt == null) {
          statement.bindNull(30)
        } else {
          statement.bindLong(30, _tmpDeletedAt)
        }
        val _tmp_1: Int = if (entity.dirty) 1 else 0
        statement.bindLong(31, _tmp_1.toLong())
        val _tmpSyncedAt: Long? = entity.syncedAt
        if (_tmpSyncedAt == null) {
          statement.bindNull(32)
        } else {
          statement.bindLong(32, _tmpSyncedAt)
        }
        val _tmpMergedAt: Long? = entity.mergedAt
        if (_tmpMergedAt == null) {
          statement.bindNull(33)
        } else {
          statement.bindLong(33, _tmpMergedAt)
        }
        statement.bindLong(34, entity.mergeCount.toLong())
        statement.bindText(35, entity.transferId)
      }
    }
  }

  public override suspend fun insertTransfer(transfer: TransferEntity): Unit =
      performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfTransferEntity.insert(_connection, transfer)
  }

  public override suspend fun upsert(transfer: TransferEntity): Unit = performSuspending(__db,
      false, true) { _connection ->
    __insertAdapterOfTransferEntity.insert(_connection, transfer)
  }

  public override suspend fun upsert(transfers: List<TransferEntity>): Unit =
      performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfTransferEntity.insert(_connection, transfers)
  }

  public override suspend fun updateTransfer(transfer: TransferEntity): Unit =
      performSuspending(__db, false, true) { _connection ->
    __updateAdapterOfTransferEntity.handle(_connection, transfer)
  }

  public override fun getTransferById(transferId: String): Flow<TransferEntity?> {
    val _sql: String = "SELECT * FROM transfers WHERE transferId = ?"
    return createFlow(__db, false, arrayOf("transfers")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, transferId)
        val _columnIndexOfTransferId: Int = getColumnIndexOrThrow(_stmt, "transferId")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfFromUserId: Int = getColumnIndexOrThrow(_stmt, "fromUserId")
        val _columnIndexOfToUserId: Int = getColumnIndexOrThrow(_stmt, "toUserId")
        val _columnIndexOfOrderId: Int = getColumnIndexOrThrow(_stmt, "orderId")
        val _columnIndexOfAmount: Int = getColumnIndexOrThrow(_stmt, "amount")
        val _columnIndexOfCurrency: Int = getColumnIndexOrThrow(_stmt, "currency")
        val _columnIndexOfType: Int = getColumnIndexOrThrow(_stmt, "type")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfTransactionReference: Int = getColumnIndexOrThrow(_stmt,
            "transactionReference")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfGpsLat: Int = getColumnIndexOrThrow(_stmt, "gpsLat")
        val _columnIndexOfGpsLng: Int = getColumnIndexOrThrow(_stmt, "gpsLng")
        val _columnIndexOfSellerPhotoUrl: Int = getColumnIndexOrThrow(_stmt, "sellerPhotoUrl")
        val _columnIndexOfBuyerPhotoUrl: Int = getColumnIndexOrThrow(_stmt, "buyerPhotoUrl")
        val _columnIndexOfTimeoutAt: Int = getColumnIndexOrThrow(_stmt, "timeoutAt")
        val _columnIndexOfConditionsJson: Int = getColumnIndexOrThrow(_stmt, "conditionsJson")
        val _columnIndexOfTransferCode: Int = getColumnIndexOrThrow(_stmt, "transferCode")
        val _columnIndexOfLineageSnapshotJson: Int = getColumnIndexOrThrow(_stmt,
            "lineageSnapshotJson")
        val _columnIndexOfClaimedAt: Int = getColumnIndexOrThrow(_stmt, "claimedAt")
        val _columnIndexOfTransferType: Int = getColumnIndexOrThrow(_stmt, "transferType")
        val _columnIndexOfGrowthSnapshotJson: Int = getColumnIndexOrThrow(_stmt,
            "growthSnapshotJson")
        val _columnIndexOfHealthSnapshotJson: Int = getColumnIndexOrThrow(_stmt,
            "healthSnapshotJson")
        val _columnIndexOfTransferCodeExpiresAt: Int = getColumnIndexOrThrow(_stmt,
            "transferCodeExpiresAt")
        val _columnIndexOfInitiatedAt: Int = getColumnIndexOrThrow(_stmt, "initiatedAt")
        val _columnIndexOfCompletedAt: Int = getColumnIndexOrThrow(_stmt, "completedAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfLastModifiedAt: Int = getColumnIndexOrThrow(_stmt, "lastModifiedAt")
        val _columnIndexOfIsDeleted: Int = getColumnIndexOrThrow(_stmt, "isDeleted")
        val _columnIndexOfDeletedAt: Int = getColumnIndexOrThrow(_stmt, "deletedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _columnIndexOfMergedAt: Int = getColumnIndexOrThrow(_stmt, "mergedAt")
        val _columnIndexOfMergeCount: Int = getColumnIndexOrThrow(_stmt, "mergeCount")
        val _result: TransferEntity?
        if (_stmt.step()) {
          val _tmpTransferId: String
          _tmpTransferId = _stmt.getText(_columnIndexOfTransferId)
          val _tmpProductId: String?
          if (_stmt.isNull(_columnIndexOfProductId)) {
            _tmpProductId = null
          } else {
            _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          }
          val _tmpFromUserId: String?
          if (_stmt.isNull(_columnIndexOfFromUserId)) {
            _tmpFromUserId = null
          } else {
            _tmpFromUserId = _stmt.getText(_columnIndexOfFromUserId)
          }
          val _tmpToUserId: String?
          if (_stmt.isNull(_columnIndexOfToUserId)) {
            _tmpToUserId = null
          } else {
            _tmpToUserId = _stmt.getText(_columnIndexOfToUserId)
          }
          val _tmpOrderId: String?
          if (_stmt.isNull(_columnIndexOfOrderId)) {
            _tmpOrderId = null
          } else {
            _tmpOrderId = _stmt.getText(_columnIndexOfOrderId)
          }
          val _tmpAmount: Double
          _tmpAmount = _stmt.getDouble(_columnIndexOfAmount)
          val _tmpCurrency: String
          _tmpCurrency = _stmt.getText(_columnIndexOfCurrency)
          val _tmpType: String
          _tmpType = _stmt.getText(_columnIndexOfType)
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpTransactionReference: String?
          if (_stmt.isNull(_columnIndexOfTransactionReference)) {
            _tmpTransactionReference = null
          } else {
            _tmpTransactionReference = _stmt.getText(_columnIndexOfTransactionReference)
          }
          val _tmpNotes: String?
          if (_stmt.isNull(_columnIndexOfNotes)) {
            _tmpNotes = null
          } else {
            _tmpNotes = _stmt.getText(_columnIndexOfNotes)
          }
          val _tmpGpsLat: Double?
          if (_stmt.isNull(_columnIndexOfGpsLat)) {
            _tmpGpsLat = null
          } else {
            _tmpGpsLat = _stmt.getDouble(_columnIndexOfGpsLat)
          }
          val _tmpGpsLng: Double?
          if (_stmt.isNull(_columnIndexOfGpsLng)) {
            _tmpGpsLng = null
          } else {
            _tmpGpsLng = _stmt.getDouble(_columnIndexOfGpsLng)
          }
          val _tmpSellerPhotoUrl: String?
          if (_stmt.isNull(_columnIndexOfSellerPhotoUrl)) {
            _tmpSellerPhotoUrl = null
          } else {
            _tmpSellerPhotoUrl = _stmt.getText(_columnIndexOfSellerPhotoUrl)
          }
          val _tmpBuyerPhotoUrl: String?
          if (_stmt.isNull(_columnIndexOfBuyerPhotoUrl)) {
            _tmpBuyerPhotoUrl = null
          } else {
            _tmpBuyerPhotoUrl = _stmt.getText(_columnIndexOfBuyerPhotoUrl)
          }
          val _tmpTimeoutAt: Long?
          if (_stmt.isNull(_columnIndexOfTimeoutAt)) {
            _tmpTimeoutAt = null
          } else {
            _tmpTimeoutAt = _stmt.getLong(_columnIndexOfTimeoutAt)
          }
          val _tmpConditionsJson: String?
          if (_stmt.isNull(_columnIndexOfConditionsJson)) {
            _tmpConditionsJson = null
          } else {
            _tmpConditionsJson = _stmt.getText(_columnIndexOfConditionsJson)
          }
          val _tmpTransferCode: String?
          if (_stmt.isNull(_columnIndexOfTransferCode)) {
            _tmpTransferCode = null
          } else {
            _tmpTransferCode = _stmt.getText(_columnIndexOfTransferCode)
          }
          val _tmpLineageSnapshotJson: String?
          if (_stmt.isNull(_columnIndexOfLineageSnapshotJson)) {
            _tmpLineageSnapshotJson = null
          } else {
            _tmpLineageSnapshotJson = _stmt.getText(_columnIndexOfLineageSnapshotJson)
          }
          val _tmpClaimedAt: Long?
          if (_stmt.isNull(_columnIndexOfClaimedAt)) {
            _tmpClaimedAt = null
          } else {
            _tmpClaimedAt = _stmt.getLong(_columnIndexOfClaimedAt)
          }
          val _tmpTransferType: String
          _tmpTransferType = _stmt.getText(_columnIndexOfTransferType)
          val _tmpGrowthSnapshotJson: String?
          if (_stmt.isNull(_columnIndexOfGrowthSnapshotJson)) {
            _tmpGrowthSnapshotJson = null
          } else {
            _tmpGrowthSnapshotJson = _stmt.getText(_columnIndexOfGrowthSnapshotJson)
          }
          val _tmpHealthSnapshotJson: String?
          if (_stmt.isNull(_columnIndexOfHealthSnapshotJson)) {
            _tmpHealthSnapshotJson = null
          } else {
            _tmpHealthSnapshotJson = _stmt.getText(_columnIndexOfHealthSnapshotJson)
          }
          val _tmpTransferCodeExpiresAt: Long?
          if (_stmt.isNull(_columnIndexOfTransferCodeExpiresAt)) {
            _tmpTransferCodeExpiresAt = null
          } else {
            _tmpTransferCodeExpiresAt = _stmt.getLong(_columnIndexOfTransferCodeExpiresAt)
          }
          val _tmpInitiatedAt: Long
          _tmpInitiatedAt = _stmt.getLong(_columnIndexOfInitiatedAt)
          val _tmpCompletedAt: Long?
          if (_stmt.isNull(_columnIndexOfCompletedAt)) {
            _tmpCompletedAt = null
          } else {
            _tmpCompletedAt = _stmt.getLong(_columnIndexOfCompletedAt)
          }
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpLastModifiedAt: Long
          _tmpLastModifiedAt = _stmt.getLong(_columnIndexOfLastModifiedAt)
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
          val _tmpSyncedAt: Long?
          if (_stmt.isNull(_columnIndexOfSyncedAt)) {
            _tmpSyncedAt = null
          } else {
            _tmpSyncedAt = _stmt.getLong(_columnIndexOfSyncedAt)
          }
          val _tmpMergedAt: Long?
          if (_stmt.isNull(_columnIndexOfMergedAt)) {
            _tmpMergedAt = null
          } else {
            _tmpMergedAt = _stmt.getLong(_columnIndexOfMergedAt)
          }
          val _tmpMergeCount: Int
          _tmpMergeCount = _stmt.getLong(_columnIndexOfMergeCount).toInt()
          _result =
              TransferEntity(_tmpTransferId,_tmpProductId,_tmpFromUserId,_tmpToUserId,_tmpOrderId,_tmpAmount,_tmpCurrency,_tmpType,_tmpStatus,_tmpTransactionReference,_tmpNotes,_tmpGpsLat,_tmpGpsLng,_tmpSellerPhotoUrl,_tmpBuyerPhotoUrl,_tmpTimeoutAt,_tmpConditionsJson,_tmpTransferCode,_tmpLineageSnapshotJson,_tmpClaimedAt,_tmpTransferType,_tmpGrowthSnapshotJson,_tmpHealthSnapshotJson,_tmpTransferCodeExpiresAt,_tmpInitiatedAt,_tmpCompletedAt,_tmpUpdatedAt,_tmpLastModifiedAt,_tmpIsDeleted,_tmpDeletedAt,_tmpDirty,_tmpSyncedAt,_tmpMergedAt,_tmpMergeCount)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getTransfersFromUser(userId: String): Flow<List<TransferEntity>> {
    val _sql: String = "SELECT * FROM transfers WHERE fromUserId = ? ORDER BY initiatedAt DESC"
    return createFlow(__db, false, arrayOf("transfers")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, userId)
        val _columnIndexOfTransferId: Int = getColumnIndexOrThrow(_stmt, "transferId")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfFromUserId: Int = getColumnIndexOrThrow(_stmt, "fromUserId")
        val _columnIndexOfToUserId: Int = getColumnIndexOrThrow(_stmt, "toUserId")
        val _columnIndexOfOrderId: Int = getColumnIndexOrThrow(_stmt, "orderId")
        val _columnIndexOfAmount: Int = getColumnIndexOrThrow(_stmt, "amount")
        val _columnIndexOfCurrency: Int = getColumnIndexOrThrow(_stmt, "currency")
        val _columnIndexOfType: Int = getColumnIndexOrThrow(_stmt, "type")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfTransactionReference: Int = getColumnIndexOrThrow(_stmt,
            "transactionReference")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfGpsLat: Int = getColumnIndexOrThrow(_stmt, "gpsLat")
        val _columnIndexOfGpsLng: Int = getColumnIndexOrThrow(_stmt, "gpsLng")
        val _columnIndexOfSellerPhotoUrl: Int = getColumnIndexOrThrow(_stmt, "sellerPhotoUrl")
        val _columnIndexOfBuyerPhotoUrl: Int = getColumnIndexOrThrow(_stmt, "buyerPhotoUrl")
        val _columnIndexOfTimeoutAt: Int = getColumnIndexOrThrow(_stmt, "timeoutAt")
        val _columnIndexOfConditionsJson: Int = getColumnIndexOrThrow(_stmt, "conditionsJson")
        val _columnIndexOfTransferCode: Int = getColumnIndexOrThrow(_stmt, "transferCode")
        val _columnIndexOfLineageSnapshotJson: Int = getColumnIndexOrThrow(_stmt,
            "lineageSnapshotJson")
        val _columnIndexOfClaimedAt: Int = getColumnIndexOrThrow(_stmt, "claimedAt")
        val _columnIndexOfTransferType: Int = getColumnIndexOrThrow(_stmt, "transferType")
        val _columnIndexOfGrowthSnapshotJson: Int = getColumnIndexOrThrow(_stmt,
            "growthSnapshotJson")
        val _columnIndexOfHealthSnapshotJson: Int = getColumnIndexOrThrow(_stmt,
            "healthSnapshotJson")
        val _columnIndexOfTransferCodeExpiresAt: Int = getColumnIndexOrThrow(_stmt,
            "transferCodeExpiresAt")
        val _columnIndexOfInitiatedAt: Int = getColumnIndexOrThrow(_stmt, "initiatedAt")
        val _columnIndexOfCompletedAt: Int = getColumnIndexOrThrow(_stmt, "completedAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfLastModifiedAt: Int = getColumnIndexOrThrow(_stmt, "lastModifiedAt")
        val _columnIndexOfIsDeleted: Int = getColumnIndexOrThrow(_stmt, "isDeleted")
        val _columnIndexOfDeletedAt: Int = getColumnIndexOrThrow(_stmt, "deletedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _columnIndexOfMergedAt: Int = getColumnIndexOrThrow(_stmt, "mergedAt")
        val _columnIndexOfMergeCount: Int = getColumnIndexOrThrow(_stmt, "mergeCount")
        val _result: MutableList<TransferEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: TransferEntity
          val _tmpTransferId: String
          _tmpTransferId = _stmt.getText(_columnIndexOfTransferId)
          val _tmpProductId: String?
          if (_stmt.isNull(_columnIndexOfProductId)) {
            _tmpProductId = null
          } else {
            _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          }
          val _tmpFromUserId: String?
          if (_stmt.isNull(_columnIndexOfFromUserId)) {
            _tmpFromUserId = null
          } else {
            _tmpFromUserId = _stmt.getText(_columnIndexOfFromUserId)
          }
          val _tmpToUserId: String?
          if (_stmt.isNull(_columnIndexOfToUserId)) {
            _tmpToUserId = null
          } else {
            _tmpToUserId = _stmt.getText(_columnIndexOfToUserId)
          }
          val _tmpOrderId: String?
          if (_stmt.isNull(_columnIndexOfOrderId)) {
            _tmpOrderId = null
          } else {
            _tmpOrderId = _stmt.getText(_columnIndexOfOrderId)
          }
          val _tmpAmount: Double
          _tmpAmount = _stmt.getDouble(_columnIndexOfAmount)
          val _tmpCurrency: String
          _tmpCurrency = _stmt.getText(_columnIndexOfCurrency)
          val _tmpType: String
          _tmpType = _stmt.getText(_columnIndexOfType)
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpTransactionReference: String?
          if (_stmt.isNull(_columnIndexOfTransactionReference)) {
            _tmpTransactionReference = null
          } else {
            _tmpTransactionReference = _stmt.getText(_columnIndexOfTransactionReference)
          }
          val _tmpNotes: String?
          if (_stmt.isNull(_columnIndexOfNotes)) {
            _tmpNotes = null
          } else {
            _tmpNotes = _stmt.getText(_columnIndexOfNotes)
          }
          val _tmpGpsLat: Double?
          if (_stmt.isNull(_columnIndexOfGpsLat)) {
            _tmpGpsLat = null
          } else {
            _tmpGpsLat = _stmt.getDouble(_columnIndexOfGpsLat)
          }
          val _tmpGpsLng: Double?
          if (_stmt.isNull(_columnIndexOfGpsLng)) {
            _tmpGpsLng = null
          } else {
            _tmpGpsLng = _stmt.getDouble(_columnIndexOfGpsLng)
          }
          val _tmpSellerPhotoUrl: String?
          if (_stmt.isNull(_columnIndexOfSellerPhotoUrl)) {
            _tmpSellerPhotoUrl = null
          } else {
            _tmpSellerPhotoUrl = _stmt.getText(_columnIndexOfSellerPhotoUrl)
          }
          val _tmpBuyerPhotoUrl: String?
          if (_stmt.isNull(_columnIndexOfBuyerPhotoUrl)) {
            _tmpBuyerPhotoUrl = null
          } else {
            _tmpBuyerPhotoUrl = _stmt.getText(_columnIndexOfBuyerPhotoUrl)
          }
          val _tmpTimeoutAt: Long?
          if (_stmt.isNull(_columnIndexOfTimeoutAt)) {
            _tmpTimeoutAt = null
          } else {
            _tmpTimeoutAt = _stmt.getLong(_columnIndexOfTimeoutAt)
          }
          val _tmpConditionsJson: String?
          if (_stmt.isNull(_columnIndexOfConditionsJson)) {
            _tmpConditionsJson = null
          } else {
            _tmpConditionsJson = _stmt.getText(_columnIndexOfConditionsJson)
          }
          val _tmpTransferCode: String?
          if (_stmt.isNull(_columnIndexOfTransferCode)) {
            _tmpTransferCode = null
          } else {
            _tmpTransferCode = _stmt.getText(_columnIndexOfTransferCode)
          }
          val _tmpLineageSnapshotJson: String?
          if (_stmt.isNull(_columnIndexOfLineageSnapshotJson)) {
            _tmpLineageSnapshotJson = null
          } else {
            _tmpLineageSnapshotJson = _stmt.getText(_columnIndexOfLineageSnapshotJson)
          }
          val _tmpClaimedAt: Long?
          if (_stmt.isNull(_columnIndexOfClaimedAt)) {
            _tmpClaimedAt = null
          } else {
            _tmpClaimedAt = _stmt.getLong(_columnIndexOfClaimedAt)
          }
          val _tmpTransferType: String
          _tmpTransferType = _stmt.getText(_columnIndexOfTransferType)
          val _tmpGrowthSnapshotJson: String?
          if (_stmt.isNull(_columnIndexOfGrowthSnapshotJson)) {
            _tmpGrowthSnapshotJson = null
          } else {
            _tmpGrowthSnapshotJson = _stmt.getText(_columnIndexOfGrowthSnapshotJson)
          }
          val _tmpHealthSnapshotJson: String?
          if (_stmt.isNull(_columnIndexOfHealthSnapshotJson)) {
            _tmpHealthSnapshotJson = null
          } else {
            _tmpHealthSnapshotJson = _stmt.getText(_columnIndexOfHealthSnapshotJson)
          }
          val _tmpTransferCodeExpiresAt: Long?
          if (_stmt.isNull(_columnIndexOfTransferCodeExpiresAt)) {
            _tmpTransferCodeExpiresAt = null
          } else {
            _tmpTransferCodeExpiresAt = _stmt.getLong(_columnIndexOfTransferCodeExpiresAt)
          }
          val _tmpInitiatedAt: Long
          _tmpInitiatedAt = _stmt.getLong(_columnIndexOfInitiatedAt)
          val _tmpCompletedAt: Long?
          if (_stmt.isNull(_columnIndexOfCompletedAt)) {
            _tmpCompletedAt = null
          } else {
            _tmpCompletedAt = _stmt.getLong(_columnIndexOfCompletedAt)
          }
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpLastModifiedAt: Long
          _tmpLastModifiedAt = _stmt.getLong(_columnIndexOfLastModifiedAt)
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
          val _tmpSyncedAt: Long?
          if (_stmt.isNull(_columnIndexOfSyncedAt)) {
            _tmpSyncedAt = null
          } else {
            _tmpSyncedAt = _stmt.getLong(_columnIndexOfSyncedAt)
          }
          val _tmpMergedAt: Long?
          if (_stmt.isNull(_columnIndexOfMergedAt)) {
            _tmpMergedAt = null
          } else {
            _tmpMergedAt = _stmt.getLong(_columnIndexOfMergedAt)
          }
          val _tmpMergeCount: Int
          _tmpMergeCount = _stmt.getLong(_columnIndexOfMergeCount).toInt()
          _item =
              TransferEntity(_tmpTransferId,_tmpProductId,_tmpFromUserId,_tmpToUserId,_tmpOrderId,_tmpAmount,_tmpCurrency,_tmpType,_tmpStatus,_tmpTransactionReference,_tmpNotes,_tmpGpsLat,_tmpGpsLng,_tmpSellerPhotoUrl,_tmpBuyerPhotoUrl,_tmpTimeoutAt,_tmpConditionsJson,_tmpTransferCode,_tmpLineageSnapshotJson,_tmpClaimedAt,_tmpTransferType,_tmpGrowthSnapshotJson,_tmpHealthSnapshotJson,_tmpTransferCodeExpiresAt,_tmpInitiatedAt,_tmpCompletedAt,_tmpUpdatedAt,_tmpLastModifiedAt,_tmpIsDeleted,_tmpDeletedAt,_tmpDirty,_tmpSyncedAt,_tmpMergedAt,_tmpMergeCount)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getTransfersToUser(userId: String): Flow<List<TransferEntity>> {
    val _sql: String = "SELECT * FROM transfers WHERE toUserId = ? ORDER BY initiatedAt DESC"
    return createFlow(__db, false, arrayOf("transfers")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, userId)
        val _columnIndexOfTransferId: Int = getColumnIndexOrThrow(_stmt, "transferId")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfFromUserId: Int = getColumnIndexOrThrow(_stmt, "fromUserId")
        val _columnIndexOfToUserId: Int = getColumnIndexOrThrow(_stmt, "toUserId")
        val _columnIndexOfOrderId: Int = getColumnIndexOrThrow(_stmt, "orderId")
        val _columnIndexOfAmount: Int = getColumnIndexOrThrow(_stmt, "amount")
        val _columnIndexOfCurrency: Int = getColumnIndexOrThrow(_stmt, "currency")
        val _columnIndexOfType: Int = getColumnIndexOrThrow(_stmt, "type")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfTransactionReference: Int = getColumnIndexOrThrow(_stmt,
            "transactionReference")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfGpsLat: Int = getColumnIndexOrThrow(_stmt, "gpsLat")
        val _columnIndexOfGpsLng: Int = getColumnIndexOrThrow(_stmt, "gpsLng")
        val _columnIndexOfSellerPhotoUrl: Int = getColumnIndexOrThrow(_stmt, "sellerPhotoUrl")
        val _columnIndexOfBuyerPhotoUrl: Int = getColumnIndexOrThrow(_stmt, "buyerPhotoUrl")
        val _columnIndexOfTimeoutAt: Int = getColumnIndexOrThrow(_stmt, "timeoutAt")
        val _columnIndexOfConditionsJson: Int = getColumnIndexOrThrow(_stmt, "conditionsJson")
        val _columnIndexOfTransferCode: Int = getColumnIndexOrThrow(_stmt, "transferCode")
        val _columnIndexOfLineageSnapshotJson: Int = getColumnIndexOrThrow(_stmt,
            "lineageSnapshotJson")
        val _columnIndexOfClaimedAt: Int = getColumnIndexOrThrow(_stmt, "claimedAt")
        val _columnIndexOfTransferType: Int = getColumnIndexOrThrow(_stmt, "transferType")
        val _columnIndexOfGrowthSnapshotJson: Int = getColumnIndexOrThrow(_stmt,
            "growthSnapshotJson")
        val _columnIndexOfHealthSnapshotJson: Int = getColumnIndexOrThrow(_stmt,
            "healthSnapshotJson")
        val _columnIndexOfTransferCodeExpiresAt: Int = getColumnIndexOrThrow(_stmt,
            "transferCodeExpiresAt")
        val _columnIndexOfInitiatedAt: Int = getColumnIndexOrThrow(_stmt, "initiatedAt")
        val _columnIndexOfCompletedAt: Int = getColumnIndexOrThrow(_stmt, "completedAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfLastModifiedAt: Int = getColumnIndexOrThrow(_stmt, "lastModifiedAt")
        val _columnIndexOfIsDeleted: Int = getColumnIndexOrThrow(_stmt, "isDeleted")
        val _columnIndexOfDeletedAt: Int = getColumnIndexOrThrow(_stmt, "deletedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _columnIndexOfMergedAt: Int = getColumnIndexOrThrow(_stmt, "mergedAt")
        val _columnIndexOfMergeCount: Int = getColumnIndexOrThrow(_stmt, "mergeCount")
        val _result: MutableList<TransferEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: TransferEntity
          val _tmpTransferId: String
          _tmpTransferId = _stmt.getText(_columnIndexOfTransferId)
          val _tmpProductId: String?
          if (_stmt.isNull(_columnIndexOfProductId)) {
            _tmpProductId = null
          } else {
            _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          }
          val _tmpFromUserId: String?
          if (_stmt.isNull(_columnIndexOfFromUserId)) {
            _tmpFromUserId = null
          } else {
            _tmpFromUserId = _stmt.getText(_columnIndexOfFromUserId)
          }
          val _tmpToUserId: String?
          if (_stmt.isNull(_columnIndexOfToUserId)) {
            _tmpToUserId = null
          } else {
            _tmpToUserId = _stmt.getText(_columnIndexOfToUserId)
          }
          val _tmpOrderId: String?
          if (_stmt.isNull(_columnIndexOfOrderId)) {
            _tmpOrderId = null
          } else {
            _tmpOrderId = _stmt.getText(_columnIndexOfOrderId)
          }
          val _tmpAmount: Double
          _tmpAmount = _stmt.getDouble(_columnIndexOfAmount)
          val _tmpCurrency: String
          _tmpCurrency = _stmt.getText(_columnIndexOfCurrency)
          val _tmpType: String
          _tmpType = _stmt.getText(_columnIndexOfType)
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpTransactionReference: String?
          if (_stmt.isNull(_columnIndexOfTransactionReference)) {
            _tmpTransactionReference = null
          } else {
            _tmpTransactionReference = _stmt.getText(_columnIndexOfTransactionReference)
          }
          val _tmpNotes: String?
          if (_stmt.isNull(_columnIndexOfNotes)) {
            _tmpNotes = null
          } else {
            _tmpNotes = _stmt.getText(_columnIndexOfNotes)
          }
          val _tmpGpsLat: Double?
          if (_stmt.isNull(_columnIndexOfGpsLat)) {
            _tmpGpsLat = null
          } else {
            _tmpGpsLat = _stmt.getDouble(_columnIndexOfGpsLat)
          }
          val _tmpGpsLng: Double?
          if (_stmt.isNull(_columnIndexOfGpsLng)) {
            _tmpGpsLng = null
          } else {
            _tmpGpsLng = _stmt.getDouble(_columnIndexOfGpsLng)
          }
          val _tmpSellerPhotoUrl: String?
          if (_stmt.isNull(_columnIndexOfSellerPhotoUrl)) {
            _tmpSellerPhotoUrl = null
          } else {
            _tmpSellerPhotoUrl = _stmt.getText(_columnIndexOfSellerPhotoUrl)
          }
          val _tmpBuyerPhotoUrl: String?
          if (_stmt.isNull(_columnIndexOfBuyerPhotoUrl)) {
            _tmpBuyerPhotoUrl = null
          } else {
            _tmpBuyerPhotoUrl = _stmt.getText(_columnIndexOfBuyerPhotoUrl)
          }
          val _tmpTimeoutAt: Long?
          if (_stmt.isNull(_columnIndexOfTimeoutAt)) {
            _tmpTimeoutAt = null
          } else {
            _tmpTimeoutAt = _stmt.getLong(_columnIndexOfTimeoutAt)
          }
          val _tmpConditionsJson: String?
          if (_stmt.isNull(_columnIndexOfConditionsJson)) {
            _tmpConditionsJson = null
          } else {
            _tmpConditionsJson = _stmt.getText(_columnIndexOfConditionsJson)
          }
          val _tmpTransferCode: String?
          if (_stmt.isNull(_columnIndexOfTransferCode)) {
            _tmpTransferCode = null
          } else {
            _tmpTransferCode = _stmt.getText(_columnIndexOfTransferCode)
          }
          val _tmpLineageSnapshotJson: String?
          if (_stmt.isNull(_columnIndexOfLineageSnapshotJson)) {
            _tmpLineageSnapshotJson = null
          } else {
            _tmpLineageSnapshotJson = _stmt.getText(_columnIndexOfLineageSnapshotJson)
          }
          val _tmpClaimedAt: Long?
          if (_stmt.isNull(_columnIndexOfClaimedAt)) {
            _tmpClaimedAt = null
          } else {
            _tmpClaimedAt = _stmt.getLong(_columnIndexOfClaimedAt)
          }
          val _tmpTransferType: String
          _tmpTransferType = _stmt.getText(_columnIndexOfTransferType)
          val _tmpGrowthSnapshotJson: String?
          if (_stmt.isNull(_columnIndexOfGrowthSnapshotJson)) {
            _tmpGrowthSnapshotJson = null
          } else {
            _tmpGrowthSnapshotJson = _stmt.getText(_columnIndexOfGrowthSnapshotJson)
          }
          val _tmpHealthSnapshotJson: String?
          if (_stmt.isNull(_columnIndexOfHealthSnapshotJson)) {
            _tmpHealthSnapshotJson = null
          } else {
            _tmpHealthSnapshotJson = _stmt.getText(_columnIndexOfHealthSnapshotJson)
          }
          val _tmpTransferCodeExpiresAt: Long?
          if (_stmt.isNull(_columnIndexOfTransferCodeExpiresAt)) {
            _tmpTransferCodeExpiresAt = null
          } else {
            _tmpTransferCodeExpiresAt = _stmt.getLong(_columnIndexOfTransferCodeExpiresAt)
          }
          val _tmpInitiatedAt: Long
          _tmpInitiatedAt = _stmt.getLong(_columnIndexOfInitiatedAt)
          val _tmpCompletedAt: Long?
          if (_stmt.isNull(_columnIndexOfCompletedAt)) {
            _tmpCompletedAt = null
          } else {
            _tmpCompletedAt = _stmt.getLong(_columnIndexOfCompletedAt)
          }
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpLastModifiedAt: Long
          _tmpLastModifiedAt = _stmt.getLong(_columnIndexOfLastModifiedAt)
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
          val _tmpSyncedAt: Long?
          if (_stmt.isNull(_columnIndexOfSyncedAt)) {
            _tmpSyncedAt = null
          } else {
            _tmpSyncedAt = _stmt.getLong(_columnIndexOfSyncedAt)
          }
          val _tmpMergedAt: Long?
          if (_stmt.isNull(_columnIndexOfMergedAt)) {
            _tmpMergedAt = null
          } else {
            _tmpMergedAt = _stmt.getLong(_columnIndexOfMergedAt)
          }
          val _tmpMergeCount: Int
          _tmpMergeCount = _stmt.getLong(_columnIndexOfMergeCount).toInt()
          _item =
              TransferEntity(_tmpTransferId,_tmpProductId,_tmpFromUserId,_tmpToUserId,_tmpOrderId,_tmpAmount,_tmpCurrency,_tmpType,_tmpStatus,_tmpTransactionReference,_tmpNotes,_tmpGpsLat,_tmpGpsLng,_tmpSellerPhotoUrl,_tmpBuyerPhotoUrl,_tmpTimeoutAt,_tmpConditionsJson,_tmpTransferCode,_tmpLineageSnapshotJson,_tmpClaimedAt,_tmpTransferType,_tmpGrowthSnapshotJson,_tmpHealthSnapshotJson,_tmpTransferCodeExpiresAt,_tmpInitiatedAt,_tmpCompletedAt,_tmpUpdatedAt,_tmpLastModifiedAt,_tmpIsDeleted,_tmpDeletedAt,_tmpDirty,_tmpSyncedAt,_tmpMergedAt,_tmpMergeCount)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getTransfersByOrderId(orderId: String): Flow<List<TransferEntity>> {
    val _sql: String = "SELECT * FROM transfers WHERE orderId = ? ORDER BY initiatedAt DESC"
    return createFlow(__db, false, arrayOf("transfers")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, orderId)
        val _columnIndexOfTransferId: Int = getColumnIndexOrThrow(_stmt, "transferId")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfFromUserId: Int = getColumnIndexOrThrow(_stmt, "fromUserId")
        val _columnIndexOfToUserId: Int = getColumnIndexOrThrow(_stmt, "toUserId")
        val _columnIndexOfOrderId: Int = getColumnIndexOrThrow(_stmt, "orderId")
        val _columnIndexOfAmount: Int = getColumnIndexOrThrow(_stmt, "amount")
        val _columnIndexOfCurrency: Int = getColumnIndexOrThrow(_stmt, "currency")
        val _columnIndexOfType: Int = getColumnIndexOrThrow(_stmt, "type")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfTransactionReference: Int = getColumnIndexOrThrow(_stmt,
            "transactionReference")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfGpsLat: Int = getColumnIndexOrThrow(_stmt, "gpsLat")
        val _columnIndexOfGpsLng: Int = getColumnIndexOrThrow(_stmt, "gpsLng")
        val _columnIndexOfSellerPhotoUrl: Int = getColumnIndexOrThrow(_stmt, "sellerPhotoUrl")
        val _columnIndexOfBuyerPhotoUrl: Int = getColumnIndexOrThrow(_stmt, "buyerPhotoUrl")
        val _columnIndexOfTimeoutAt: Int = getColumnIndexOrThrow(_stmt, "timeoutAt")
        val _columnIndexOfConditionsJson: Int = getColumnIndexOrThrow(_stmt, "conditionsJson")
        val _columnIndexOfTransferCode: Int = getColumnIndexOrThrow(_stmt, "transferCode")
        val _columnIndexOfLineageSnapshotJson: Int = getColumnIndexOrThrow(_stmt,
            "lineageSnapshotJson")
        val _columnIndexOfClaimedAt: Int = getColumnIndexOrThrow(_stmt, "claimedAt")
        val _columnIndexOfTransferType: Int = getColumnIndexOrThrow(_stmt, "transferType")
        val _columnIndexOfGrowthSnapshotJson: Int = getColumnIndexOrThrow(_stmt,
            "growthSnapshotJson")
        val _columnIndexOfHealthSnapshotJson: Int = getColumnIndexOrThrow(_stmt,
            "healthSnapshotJson")
        val _columnIndexOfTransferCodeExpiresAt: Int = getColumnIndexOrThrow(_stmt,
            "transferCodeExpiresAt")
        val _columnIndexOfInitiatedAt: Int = getColumnIndexOrThrow(_stmt, "initiatedAt")
        val _columnIndexOfCompletedAt: Int = getColumnIndexOrThrow(_stmt, "completedAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfLastModifiedAt: Int = getColumnIndexOrThrow(_stmt, "lastModifiedAt")
        val _columnIndexOfIsDeleted: Int = getColumnIndexOrThrow(_stmt, "isDeleted")
        val _columnIndexOfDeletedAt: Int = getColumnIndexOrThrow(_stmt, "deletedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _columnIndexOfMergedAt: Int = getColumnIndexOrThrow(_stmt, "mergedAt")
        val _columnIndexOfMergeCount: Int = getColumnIndexOrThrow(_stmt, "mergeCount")
        val _result: MutableList<TransferEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: TransferEntity
          val _tmpTransferId: String
          _tmpTransferId = _stmt.getText(_columnIndexOfTransferId)
          val _tmpProductId: String?
          if (_stmt.isNull(_columnIndexOfProductId)) {
            _tmpProductId = null
          } else {
            _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          }
          val _tmpFromUserId: String?
          if (_stmt.isNull(_columnIndexOfFromUserId)) {
            _tmpFromUserId = null
          } else {
            _tmpFromUserId = _stmt.getText(_columnIndexOfFromUserId)
          }
          val _tmpToUserId: String?
          if (_stmt.isNull(_columnIndexOfToUserId)) {
            _tmpToUserId = null
          } else {
            _tmpToUserId = _stmt.getText(_columnIndexOfToUserId)
          }
          val _tmpOrderId: String?
          if (_stmt.isNull(_columnIndexOfOrderId)) {
            _tmpOrderId = null
          } else {
            _tmpOrderId = _stmt.getText(_columnIndexOfOrderId)
          }
          val _tmpAmount: Double
          _tmpAmount = _stmt.getDouble(_columnIndexOfAmount)
          val _tmpCurrency: String
          _tmpCurrency = _stmt.getText(_columnIndexOfCurrency)
          val _tmpType: String
          _tmpType = _stmt.getText(_columnIndexOfType)
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpTransactionReference: String?
          if (_stmt.isNull(_columnIndexOfTransactionReference)) {
            _tmpTransactionReference = null
          } else {
            _tmpTransactionReference = _stmt.getText(_columnIndexOfTransactionReference)
          }
          val _tmpNotes: String?
          if (_stmt.isNull(_columnIndexOfNotes)) {
            _tmpNotes = null
          } else {
            _tmpNotes = _stmt.getText(_columnIndexOfNotes)
          }
          val _tmpGpsLat: Double?
          if (_stmt.isNull(_columnIndexOfGpsLat)) {
            _tmpGpsLat = null
          } else {
            _tmpGpsLat = _stmt.getDouble(_columnIndexOfGpsLat)
          }
          val _tmpGpsLng: Double?
          if (_stmt.isNull(_columnIndexOfGpsLng)) {
            _tmpGpsLng = null
          } else {
            _tmpGpsLng = _stmt.getDouble(_columnIndexOfGpsLng)
          }
          val _tmpSellerPhotoUrl: String?
          if (_stmt.isNull(_columnIndexOfSellerPhotoUrl)) {
            _tmpSellerPhotoUrl = null
          } else {
            _tmpSellerPhotoUrl = _stmt.getText(_columnIndexOfSellerPhotoUrl)
          }
          val _tmpBuyerPhotoUrl: String?
          if (_stmt.isNull(_columnIndexOfBuyerPhotoUrl)) {
            _tmpBuyerPhotoUrl = null
          } else {
            _tmpBuyerPhotoUrl = _stmt.getText(_columnIndexOfBuyerPhotoUrl)
          }
          val _tmpTimeoutAt: Long?
          if (_stmt.isNull(_columnIndexOfTimeoutAt)) {
            _tmpTimeoutAt = null
          } else {
            _tmpTimeoutAt = _stmt.getLong(_columnIndexOfTimeoutAt)
          }
          val _tmpConditionsJson: String?
          if (_stmt.isNull(_columnIndexOfConditionsJson)) {
            _tmpConditionsJson = null
          } else {
            _tmpConditionsJson = _stmt.getText(_columnIndexOfConditionsJson)
          }
          val _tmpTransferCode: String?
          if (_stmt.isNull(_columnIndexOfTransferCode)) {
            _tmpTransferCode = null
          } else {
            _tmpTransferCode = _stmt.getText(_columnIndexOfTransferCode)
          }
          val _tmpLineageSnapshotJson: String?
          if (_stmt.isNull(_columnIndexOfLineageSnapshotJson)) {
            _tmpLineageSnapshotJson = null
          } else {
            _tmpLineageSnapshotJson = _stmt.getText(_columnIndexOfLineageSnapshotJson)
          }
          val _tmpClaimedAt: Long?
          if (_stmt.isNull(_columnIndexOfClaimedAt)) {
            _tmpClaimedAt = null
          } else {
            _tmpClaimedAt = _stmt.getLong(_columnIndexOfClaimedAt)
          }
          val _tmpTransferType: String
          _tmpTransferType = _stmt.getText(_columnIndexOfTransferType)
          val _tmpGrowthSnapshotJson: String?
          if (_stmt.isNull(_columnIndexOfGrowthSnapshotJson)) {
            _tmpGrowthSnapshotJson = null
          } else {
            _tmpGrowthSnapshotJson = _stmt.getText(_columnIndexOfGrowthSnapshotJson)
          }
          val _tmpHealthSnapshotJson: String?
          if (_stmt.isNull(_columnIndexOfHealthSnapshotJson)) {
            _tmpHealthSnapshotJson = null
          } else {
            _tmpHealthSnapshotJson = _stmt.getText(_columnIndexOfHealthSnapshotJson)
          }
          val _tmpTransferCodeExpiresAt: Long?
          if (_stmt.isNull(_columnIndexOfTransferCodeExpiresAt)) {
            _tmpTransferCodeExpiresAt = null
          } else {
            _tmpTransferCodeExpiresAt = _stmt.getLong(_columnIndexOfTransferCodeExpiresAt)
          }
          val _tmpInitiatedAt: Long
          _tmpInitiatedAt = _stmt.getLong(_columnIndexOfInitiatedAt)
          val _tmpCompletedAt: Long?
          if (_stmt.isNull(_columnIndexOfCompletedAt)) {
            _tmpCompletedAt = null
          } else {
            _tmpCompletedAt = _stmt.getLong(_columnIndexOfCompletedAt)
          }
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpLastModifiedAt: Long
          _tmpLastModifiedAt = _stmt.getLong(_columnIndexOfLastModifiedAt)
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
          val _tmpSyncedAt: Long?
          if (_stmt.isNull(_columnIndexOfSyncedAt)) {
            _tmpSyncedAt = null
          } else {
            _tmpSyncedAt = _stmt.getLong(_columnIndexOfSyncedAt)
          }
          val _tmpMergedAt: Long?
          if (_stmt.isNull(_columnIndexOfMergedAt)) {
            _tmpMergedAt = null
          } else {
            _tmpMergedAt = _stmt.getLong(_columnIndexOfMergedAt)
          }
          val _tmpMergeCount: Int
          _tmpMergeCount = _stmt.getLong(_columnIndexOfMergeCount).toInt()
          _item =
              TransferEntity(_tmpTransferId,_tmpProductId,_tmpFromUserId,_tmpToUserId,_tmpOrderId,_tmpAmount,_tmpCurrency,_tmpType,_tmpStatus,_tmpTransactionReference,_tmpNotes,_tmpGpsLat,_tmpGpsLng,_tmpSellerPhotoUrl,_tmpBuyerPhotoUrl,_tmpTimeoutAt,_tmpConditionsJson,_tmpTransferCode,_tmpLineageSnapshotJson,_tmpClaimedAt,_tmpTransferType,_tmpGrowthSnapshotJson,_tmpHealthSnapshotJson,_tmpTransferCodeExpiresAt,_tmpInitiatedAt,_tmpCompletedAt,_tmpUpdatedAt,_tmpLastModifiedAt,_tmpIsDeleted,_tmpDeletedAt,_tmpDirty,_tmpSyncedAt,_tmpMergedAt,_tmpMergeCount)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getTransfersByType(type: String): Flow<List<TransferEntity>> {
    val _sql: String = "SELECT * FROM transfers WHERE type = ? ORDER BY initiatedAt DESC"
    return createFlow(__db, false, arrayOf("transfers")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, type)
        val _columnIndexOfTransferId: Int = getColumnIndexOrThrow(_stmt, "transferId")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfFromUserId: Int = getColumnIndexOrThrow(_stmt, "fromUserId")
        val _columnIndexOfToUserId: Int = getColumnIndexOrThrow(_stmt, "toUserId")
        val _columnIndexOfOrderId: Int = getColumnIndexOrThrow(_stmt, "orderId")
        val _columnIndexOfAmount: Int = getColumnIndexOrThrow(_stmt, "amount")
        val _columnIndexOfCurrency: Int = getColumnIndexOrThrow(_stmt, "currency")
        val _columnIndexOfType: Int = getColumnIndexOrThrow(_stmt, "type")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfTransactionReference: Int = getColumnIndexOrThrow(_stmt,
            "transactionReference")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfGpsLat: Int = getColumnIndexOrThrow(_stmt, "gpsLat")
        val _columnIndexOfGpsLng: Int = getColumnIndexOrThrow(_stmt, "gpsLng")
        val _columnIndexOfSellerPhotoUrl: Int = getColumnIndexOrThrow(_stmt, "sellerPhotoUrl")
        val _columnIndexOfBuyerPhotoUrl: Int = getColumnIndexOrThrow(_stmt, "buyerPhotoUrl")
        val _columnIndexOfTimeoutAt: Int = getColumnIndexOrThrow(_stmt, "timeoutAt")
        val _columnIndexOfConditionsJson: Int = getColumnIndexOrThrow(_stmt, "conditionsJson")
        val _columnIndexOfTransferCode: Int = getColumnIndexOrThrow(_stmt, "transferCode")
        val _columnIndexOfLineageSnapshotJson: Int = getColumnIndexOrThrow(_stmt,
            "lineageSnapshotJson")
        val _columnIndexOfClaimedAt: Int = getColumnIndexOrThrow(_stmt, "claimedAt")
        val _columnIndexOfTransferType: Int = getColumnIndexOrThrow(_stmt, "transferType")
        val _columnIndexOfGrowthSnapshotJson: Int = getColumnIndexOrThrow(_stmt,
            "growthSnapshotJson")
        val _columnIndexOfHealthSnapshotJson: Int = getColumnIndexOrThrow(_stmt,
            "healthSnapshotJson")
        val _columnIndexOfTransferCodeExpiresAt: Int = getColumnIndexOrThrow(_stmt,
            "transferCodeExpiresAt")
        val _columnIndexOfInitiatedAt: Int = getColumnIndexOrThrow(_stmt, "initiatedAt")
        val _columnIndexOfCompletedAt: Int = getColumnIndexOrThrow(_stmt, "completedAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfLastModifiedAt: Int = getColumnIndexOrThrow(_stmt, "lastModifiedAt")
        val _columnIndexOfIsDeleted: Int = getColumnIndexOrThrow(_stmt, "isDeleted")
        val _columnIndexOfDeletedAt: Int = getColumnIndexOrThrow(_stmt, "deletedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _columnIndexOfMergedAt: Int = getColumnIndexOrThrow(_stmt, "mergedAt")
        val _columnIndexOfMergeCount: Int = getColumnIndexOrThrow(_stmt, "mergeCount")
        val _result: MutableList<TransferEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: TransferEntity
          val _tmpTransferId: String
          _tmpTransferId = _stmt.getText(_columnIndexOfTransferId)
          val _tmpProductId: String?
          if (_stmt.isNull(_columnIndexOfProductId)) {
            _tmpProductId = null
          } else {
            _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          }
          val _tmpFromUserId: String?
          if (_stmt.isNull(_columnIndexOfFromUserId)) {
            _tmpFromUserId = null
          } else {
            _tmpFromUserId = _stmt.getText(_columnIndexOfFromUserId)
          }
          val _tmpToUserId: String?
          if (_stmt.isNull(_columnIndexOfToUserId)) {
            _tmpToUserId = null
          } else {
            _tmpToUserId = _stmt.getText(_columnIndexOfToUserId)
          }
          val _tmpOrderId: String?
          if (_stmt.isNull(_columnIndexOfOrderId)) {
            _tmpOrderId = null
          } else {
            _tmpOrderId = _stmt.getText(_columnIndexOfOrderId)
          }
          val _tmpAmount: Double
          _tmpAmount = _stmt.getDouble(_columnIndexOfAmount)
          val _tmpCurrency: String
          _tmpCurrency = _stmt.getText(_columnIndexOfCurrency)
          val _tmpType: String
          _tmpType = _stmt.getText(_columnIndexOfType)
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpTransactionReference: String?
          if (_stmt.isNull(_columnIndexOfTransactionReference)) {
            _tmpTransactionReference = null
          } else {
            _tmpTransactionReference = _stmt.getText(_columnIndexOfTransactionReference)
          }
          val _tmpNotes: String?
          if (_stmt.isNull(_columnIndexOfNotes)) {
            _tmpNotes = null
          } else {
            _tmpNotes = _stmt.getText(_columnIndexOfNotes)
          }
          val _tmpGpsLat: Double?
          if (_stmt.isNull(_columnIndexOfGpsLat)) {
            _tmpGpsLat = null
          } else {
            _tmpGpsLat = _stmt.getDouble(_columnIndexOfGpsLat)
          }
          val _tmpGpsLng: Double?
          if (_stmt.isNull(_columnIndexOfGpsLng)) {
            _tmpGpsLng = null
          } else {
            _tmpGpsLng = _stmt.getDouble(_columnIndexOfGpsLng)
          }
          val _tmpSellerPhotoUrl: String?
          if (_stmt.isNull(_columnIndexOfSellerPhotoUrl)) {
            _tmpSellerPhotoUrl = null
          } else {
            _tmpSellerPhotoUrl = _stmt.getText(_columnIndexOfSellerPhotoUrl)
          }
          val _tmpBuyerPhotoUrl: String?
          if (_stmt.isNull(_columnIndexOfBuyerPhotoUrl)) {
            _tmpBuyerPhotoUrl = null
          } else {
            _tmpBuyerPhotoUrl = _stmt.getText(_columnIndexOfBuyerPhotoUrl)
          }
          val _tmpTimeoutAt: Long?
          if (_stmt.isNull(_columnIndexOfTimeoutAt)) {
            _tmpTimeoutAt = null
          } else {
            _tmpTimeoutAt = _stmt.getLong(_columnIndexOfTimeoutAt)
          }
          val _tmpConditionsJson: String?
          if (_stmt.isNull(_columnIndexOfConditionsJson)) {
            _tmpConditionsJson = null
          } else {
            _tmpConditionsJson = _stmt.getText(_columnIndexOfConditionsJson)
          }
          val _tmpTransferCode: String?
          if (_stmt.isNull(_columnIndexOfTransferCode)) {
            _tmpTransferCode = null
          } else {
            _tmpTransferCode = _stmt.getText(_columnIndexOfTransferCode)
          }
          val _tmpLineageSnapshotJson: String?
          if (_stmt.isNull(_columnIndexOfLineageSnapshotJson)) {
            _tmpLineageSnapshotJson = null
          } else {
            _tmpLineageSnapshotJson = _stmt.getText(_columnIndexOfLineageSnapshotJson)
          }
          val _tmpClaimedAt: Long?
          if (_stmt.isNull(_columnIndexOfClaimedAt)) {
            _tmpClaimedAt = null
          } else {
            _tmpClaimedAt = _stmt.getLong(_columnIndexOfClaimedAt)
          }
          val _tmpTransferType: String
          _tmpTransferType = _stmt.getText(_columnIndexOfTransferType)
          val _tmpGrowthSnapshotJson: String?
          if (_stmt.isNull(_columnIndexOfGrowthSnapshotJson)) {
            _tmpGrowthSnapshotJson = null
          } else {
            _tmpGrowthSnapshotJson = _stmt.getText(_columnIndexOfGrowthSnapshotJson)
          }
          val _tmpHealthSnapshotJson: String?
          if (_stmt.isNull(_columnIndexOfHealthSnapshotJson)) {
            _tmpHealthSnapshotJson = null
          } else {
            _tmpHealthSnapshotJson = _stmt.getText(_columnIndexOfHealthSnapshotJson)
          }
          val _tmpTransferCodeExpiresAt: Long?
          if (_stmt.isNull(_columnIndexOfTransferCodeExpiresAt)) {
            _tmpTransferCodeExpiresAt = null
          } else {
            _tmpTransferCodeExpiresAt = _stmt.getLong(_columnIndexOfTransferCodeExpiresAt)
          }
          val _tmpInitiatedAt: Long
          _tmpInitiatedAt = _stmt.getLong(_columnIndexOfInitiatedAt)
          val _tmpCompletedAt: Long?
          if (_stmt.isNull(_columnIndexOfCompletedAt)) {
            _tmpCompletedAt = null
          } else {
            _tmpCompletedAt = _stmt.getLong(_columnIndexOfCompletedAt)
          }
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpLastModifiedAt: Long
          _tmpLastModifiedAt = _stmt.getLong(_columnIndexOfLastModifiedAt)
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
          val _tmpSyncedAt: Long?
          if (_stmt.isNull(_columnIndexOfSyncedAt)) {
            _tmpSyncedAt = null
          } else {
            _tmpSyncedAt = _stmt.getLong(_columnIndexOfSyncedAt)
          }
          val _tmpMergedAt: Long?
          if (_stmt.isNull(_columnIndexOfMergedAt)) {
            _tmpMergedAt = null
          } else {
            _tmpMergedAt = _stmt.getLong(_columnIndexOfMergedAt)
          }
          val _tmpMergeCount: Int
          _tmpMergeCount = _stmt.getLong(_columnIndexOfMergeCount).toInt()
          _item =
              TransferEntity(_tmpTransferId,_tmpProductId,_tmpFromUserId,_tmpToUserId,_tmpOrderId,_tmpAmount,_tmpCurrency,_tmpType,_tmpStatus,_tmpTransactionReference,_tmpNotes,_tmpGpsLat,_tmpGpsLng,_tmpSellerPhotoUrl,_tmpBuyerPhotoUrl,_tmpTimeoutAt,_tmpConditionsJson,_tmpTransferCode,_tmpLineageSnapshotJson,_tmpClaimedAt,_tmpTransferType,_tmpGrowthSnapshotJson,_tmpHealthSnapshotJson,_tmpTransferCodeExpiresAt,_tmpInitiatedAt,_tmpCompletedAt,_tmpUpdatedAt,_tmpLastModifiedAt,_tmpIsDeleted,_tmpDeletedAt,_tmpDirty,_tmpSyncedAt,_tmpMergedAt,_tmpMergeCount)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getTransfersByStatus(status: String): Flow<List<TransferEntity>> {
    val _sql: String = "SELECT * FROM transfers WHERE status = ? ORDER BY initiatedAt DESC"
    return createFlow(__db, false, arrayOf("transfers")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, status)
        val _columnIndexOfTransferId: Int = getColumnIndexOrThrow(_stmt, "transferId")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfFromUserId: Int = getColumnIndexOrThrow(_stmt, "fromUserId")
        val _columnIndexOfToUserId: Int = getColumnIndexOrThrow(_stmt, "toUserId")
        val _columnIndexOfOrderId: Int = getColumnIndexOrThrow(_stmt, "orderId")
        val _columnIndexOfAmount: Int = getColumnIndexOrThrow(_stmt, "amount")
        val _columnIndexOfCurrency: Int = getColumnIndexOrThrow(_stmt, "currency")
        val _columnIndexOfType: Int = getColumnIndexOrThrow(_stmt, "type")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfTransactionReference: Int = getColumnIndexOrThrow(_stmt,
            "transactionReference")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfGpsLat: Int = getColumnIndexOrThrow(_stmt, "gpsLat")
        val _columnIndexOfGpsLng: Int = getColumnIndexOrThrow(_stmt, "gpsLng")
        val _columnIndexOfSellerPhotoUrl: Int = getColumnIndexOrThrow(_stmt, "sellerPhotoUrl")
        val _columnIndexOfBuyerPhotoUrl: Int = getColumnIndexOrThrow(_stmt, "buyerPhotoUrl")
        val _columnIndexOfTimeoutAt: Int = getColumnIndexOrThrow(_stmt, "timeoutAt")
        val _columnIndexOfConditionsJson: Int = getColumnIndexOrThrow(_stmt, "conditionsJson")
        val _columnIndexOfTransferCode: Int = getColumnIndexOrThrow(_stmt, "transferCode")
        val _columnIndexOfLineageSnapshotJson: Int = getColumnIndexOrThrow(_stmt,
            "lineageSnapshotJson")
        val _columnIndexOfClaimedAt: Int = getColumnIndexOrThrow(_stmt, "claimedAt")
        val _columnIndexOfTransferType: Int = getColumnIndexOrThrow(_stmt, "transferType")
        val _columnIndexOfGrowthSnapshotJson: Int = getColumnIndexOrThrow(_stmt,
            "growthSnapshotJson")
        val _columnIndexOfHealthSnapshotJson: Int = getColumnIndexOrThrow(_stmt,
            "healthSnapshotJson")
        val _columnIndexOfTransferCodeExpiresAt: Int = getColumnIndexOrThrow(_stmt,
            "transferCodeExpiresAt")
        val _columnIndexOfInitiatedAt: Int = getColumnIndexOrThrow(_stmt, "initiatedAt")
        val _columnIndexOfCompletedAt: Int = getColumnIndexOrThrow(_stmt, "completedAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfLastModifiedAt: Int = getColumnIndexOrThrow(_stmt, "lastModifiedAt")
        val _columnIndexOfIsDeleted: Int = getColumnIndexOrThrow(_stmt, "isDeleted")
        val _columnIndexOfDeletedAt: Int = getColumnIndexOrThrow(_stmt, "deletedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _columnIndexOfMergedAt: Int = getColumnIndexOrThrow(_stmt, "mergedAt")
        val _columnIndexOfMergeCount: Int = getColumnIndexOrThrow(_stmt, "mergeCount")
        val _result: MutableList<TransferEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: TransferEntity
          val _tmpTransferId: String
          _tmpTransferId = _stmt.getText(_columnIndexOfTransferId)
          val _tmpProductId: String?
          if (_stmt.isNull(_columnIndexOfProductId)) {
            _tmpProductId = null
          } else {
            _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          }
          val _tmpFromUserId: String?
          if (_stmt.isNull(_columnIndexOfFromUserId)) {
            _tmpFromUserId = null
          } else {
            _tmpFromUserId = _stmt.getText(_columnIndexOfFromUserId)
          }
          val _tmpToUserId: String?
          if (_stmt.isNull(_columnIndexOfToUserId)) {
            _tmpToUserId = null
          } else {
            _tmpToUserId = _stmt.getText(_columnIndexOfToUserId)
          }
          val _tmpOrderId: String?
          if (_stmt.isNull(_columnIndexOfOrderId)) {
            _tmpOrderId = null
          } else {
            _tmpOrderId = _stmt.getText(_columnIndexOfOrderId)
          }
          val _tmpAmount: Double
          _tmpAmount = _stmt.getDouble(_columnIndexOfAmount)
          val _tmpCurrency: String
          _tmpCurrency = _stmt.getText(_columnIndexOfCurrency)
          val _tmpType: String
          _tmpType = _stmt.getText(_columnIndexOfType)
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpTransactionReference: String?
          if (_stmt.isNull(_columnIndexOfTransactionReference)) {
            _tmpTransactionReference = null
          } else {
            _tmpTransactionReference = _stmt.getText(_columnIndexOfTransactionReference)
          }
          val _tmpNotes: String?
          if (_stmt.isNull(_columnIndexOfNotes)) {
            _tmpNotes = null
          } else {
            _tmpNotes = _stmt.getText(_columnIndexOfNotes)
          }
          val _tmpGpsLat: Double?
          if (_stmt.isNull(_columnIndexOfGpsLat)) {
            _tmpGpsLat = null
          } else {
            _tmpGpsLat = _stmt.getDouble(_columnIndexOfGpsLat)
          }
          val _tmpGpsLng: Double?
          if (_stmt.isNull(_columnIndexOfGpsLng)) {
            _tmpGpsLng = null
          } else {
            _tmpGpsLng = _stmt.getDouble(_columnIndexOfGpsLng)
          }
          val _tmpSellerPhotoUrl: String?
          if (_stmt.isNull(_columnIndexOfSellerPhotoUrl)) {
            _tmpSellerPhotoUrl = null
          } else {
            _tmpSellerPhotoUrl = _stmt.getText(_columnIndexOfSellerPhotoUrl)
          }
          val _tmpBuyerPhotoUrl: String?
          if (_stmt.isNull(_columnIndexOfBuyerPhotoUrl)) {
            _tmpBuyerPhotoUrl = null
          } else {
            _tmpBuyerPhotoUrl = _stmt.getText(_columnIndexOfBuyerPhotoUrl)
          }
          val _tmpTimeoutAt: Long?
          if (_stmt.isNull(_columnIndexOfTimeoutAt)) {
            _tmpTimeoutAt = null
          } else {
            _tmpTimeoutAt = _stmt.getLong(_columnIndexOfTimeoutAt)
          }
          val _tmpConditionsJson: String?
          if (_stmt.isNull(_columnIndexOfConditionsJson)) {
            _tmpConditionsJson = null
          } else {
            _tmpConditionsJson = _stmt.getText(_columnIndexOfConditionsJson)
          }
          val _tmpTransferCode: String?
          if (_stmt.isNull(_columnIndexOfTransferCode)) {
            _tmpTransferCode = null
          } else {
            _tmpTransferCode = _stmt.getText(_columnIndexOfTransferCode)
          }
          val _tmpLineageSnapshotJson: String?
          if (_stmt.isNull(_columnIndexOfLineageSnapshotJson)) {
            _tmpLineageSnapshotJson = null
          } else {
            _tmpLineageSnapshotJson = _stmt.getText(_columnIndexOfLineageSnapshotJson)
          }
          val _tmpClaimedAt: Long?
          if (_stmt.isNull(_columnIndexOfClaimedAt)) {
            _tmpClaimedAt = null
          } else {
            _tmpClaimedAt = _stmt.getLong(_columnIndexOfClaimedAt)
          }
          val _tmpTransferType: String
          _tmpTransferType = _stmt.getText(_columnIndexOfTransferType)
          val _tmpGrowthSnapshotJson: String?
          if (_stmt.isNull(_columnIndexOfGrowthSnapshotJson)) {
            _tmpGrowthSnapshotJson = null
          } else {
            _tmpGrowthSnapshotJson = _stmt.getText(_columnIndexOfGrowthSnapshotJson)
          }
          val _tmpHealthSnapshotJson: String?
          if (_stmt.isNull(_columnIndexOfHealthSnapshotJson)) {
            _tmpHealthSnapshotJson = null
          } else {
            _tmpHealthSnapshotJson = _stmt.getText(_columnIndexOfHealthSnapshotJson)
          }
          val _tmpTransferCodeExpiresAt: Long?
          if (_stmt.isNull(_columnIndexOfTransferCodeExpiresAt)) {
            _tmpTransferCodeExpiresAt = null
          } else {
            _tmpTransferCodeExpiresAt = _stmt.getLong(_columnIndexOfTransferCodeExpiresAt)
          }
          val _tmpInitiatedAt: Long
          _tmpInitiatedAt = _stmt.getLong(_columnIndexOfInitiatedAt)
          val _tmpCompletedAt: Long?
          if (_stmt.isNull(_columnIndexOfCompletedAt)) {
            _tmpCompletedAt = null
          } else {
            _tmpCompletedAt = _stmt.getLong(_columnIndexOfCompletedAt)
          }
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpLastModifiedAt: Long
          _tmpLastModifiedAt = _stmt.getLong(_columnIndexOfLastModifiedAt)
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
          val _tmpSyncedAt: Long?
          if (_stmt.isNull(_columnIndexOfSyncedAt)) {
            _tmpSyncedAt = null
          } else {
            _tmpSyncedAt = _stmt.getLong(_columnIndexOfSyncedAt)
          }
          val _tmpMergedAt: Long?
          if (_stmt.isNull(_columnIndexOfMergedAt)) {
            _tmpMergedAt = null
          } else {
            _tmpMergedAt = _stmt.getLong(_columnIndexOfMergedAt)
          }
          val _tmpMergeCount: Int
          _tmpMergeCount = _stmt.getLong(_columnIndexOfMergeCount).toInt()
          _item =
              TransferEntity(_tmpTransferId,_tmpProductId,_tmpFromUserId,_tmpToUserId,_tmpOrderId,_tmpAmount,_tmpCurrency,_tmpType,_tmpStatus,_tmpTransactionReference,_tmpNotes,_tmpGpsLat,_tmpGpsLng,_tmpSellerPhotoUrl,_tmpBuyerPhotoUrl,_tmpTimeoutAt,_tmpConditionsJson,_tmpTransferCode,_tmpLineageSnapshotJson,_tmpClaimedAt,_tmpTransferType,_tmpGrowthSnapshotJson,_tmpHealthSnapshotJson,_tmpTransferCodeExpiresAt,_tmpInitiatedAt,_tmpCompletedAt,_tmpUpdatedAt,_tmpLastModifiedAt,_tmpIsDeleted,_tmpDeletedAt,_tmpDirty,_tmpSyncedAt,_tmpMergedAt,_tmpMergeCount)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getUpdatedSince(since: Long, limit: Int): List<TransferEntity> {
    val _sql: String = "SELECT * FROM transfers WHERE updatedAt > ? ORDER BY updatedAt ASC LIMIT ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, since)
        _argIndex = 2
        _stmt.bindLong(_argIndex, limit.toLong())
        val _columnIndexOfTransferId: Int = getColumnIndexOrThrow(_stmt, "transferId")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfFromUserId: Int = getColumnIndexOrThrow(_stmt, "fromUserId")
        val _columnIndexOfToUserId: Int = getColumnIndexOrThrow(_stmt, "toUserId")
        val _columnIndexOfOrderId: Int = getColumnIndexOrThrow(_stmt, "orderId")
        val _columnIndexOfAmount: Int = getColumnIndexOrThrow(_stmt, "amount")
        val _columnIndexOfCurrency: Int = getColumnIndexOrThrow(_stmt, "currency")
        val _columnIndexOfType: Int = getColumnIndexOrThrow(_stmt, "type")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfTransactionReference: Int = getColumnIndexOrThrow(_stmt,
            "transactionReference")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfGpsLat: Int = getColumnIndexOrThrow(_stmt, "gpsLat")
        val _columnIndexOfGpsLng: Int = getColumnIndexOrThrow(_stmt, "gpsLng")
        val _columnIndexOfSellerPhotoUrl: Int = getColumnIndexOrThrow(_stmt, "sellerPhotoUrl")
        val _columnIndexOfBuyerPhotoUrl: Int = getColumnIndexOrThrow(_stmt, "buyerPhotoUrl")
        val _columnIndexOfTimeoutAt: Int = getColumnIndexOrThrow(_stmt, "timeoutAt")
        val _columnIndexOfConditionsJson: Int = getColumnIndexOrThrow(_stmt, "conditionsJson")
        val _columnIndexOfTransferCode: Int = getColumnIndexOrThrow(_stmt, "transferCode")
        val _columnIndexOfLineageSnapshotJson: Int = getColumnIndexOrThrow(_stmt,
            "lineageSnapshotJson")
        val _columnIndexOfClaimedAt: Int = getColumnIndexOrThrow(_stmt, "claimedAt")
        val _columnIndexOfTransferType: Int = getColumnIndexOrThrow(_stmt, "transferType")
        val _columnIndexOfGrowthSnapshotJson: Int = getColumnIndexOrThrow(_stmt,
            "growthSnapshotJson")
        val _columnIndexOfHealthSnapshotJson: Int = getColumnIndexOrThrow(_stmt,
            "healthSnapshotJson")
        val _columnIndexOfTransferCodeExpiresAt: Int = getColumnIndexOrThrow(_stmt,
            "transferCodeExpiresAt")
        val _columnIndexOfInitiatedAt: Int = getColumnIndexOrThrow(_stmt, "initiatedAt")
        val _columnIndexOfCompletedAt: Int = getColumnIndexOrThrow(_stmt, "completedAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfLastModifiedAt: Int = getColumnIndexOrThrow(_stmt, "lastModifiedAt")
        val _columnIndexOfIsDeleted: Int = getColumnIndexOrThrow(_stmt, "isDeleted")
        val _columnIndexOfDeletedAt: Int = getColumnIndexOrThrow(_stmt, "deletedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _columnIndexOfMergedAt: Int = getColumnIndexOrThrow(_stmt, "mergedAt")
        val _columnIndexOfMergeCount: Int = getColumnIndexOrThrow(_stmt, "mergeCount")
        val _result: MutableList<TransferEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: TransferEntity
          val _tmpTransferId: String
          _tmpTransferId = _stmt.getText(_columnIndexOfTransferId)
          val _tmpProductId: String?
          if (_stmt.isNull(_columnIndexOfProductId)) {
            _tmpProductId = null
          } else {
            _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          }
          val _tmpFromUserId: String?
          if (_stmt.isNull(_columnIndexOfFromUserId)) {
            _tmpFromUserId = null
          } else {
            _tmpFromUserId = _stmt.getText(_columnIndexOfFromUserId)
          }
          val _tmpToUserId: String?
          if (_stmt.isNull(_columnIndexOfToUserId)) {
            _tmpToUserId = null
          } else {
            _tmpToUserId = _stmt.getText(_columnIndexOfToUserId)
          }
          val _tmpOrderId: String?
          if (_stmt.isNull(_columnIndexOfOrderId)) {
            _tmpOrderId = null
          } else {
            _tmpOrderId = _stmt.getText(_columnIndexOfOrderId)
          }
          val _tmpAmount: Double
          _tmpAmount = _stmt.getDouble(_columnIndexOfAmount)
          val _tmpCurrency: String
          _tmpCurrency = _stmt.getText(_columnIndexOfCurrency)
          val _tmpType: String
          _tmpType = _stmt.getText(_columnIndexOfType)
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpTransactionReference: String?
          if (_stmt.isNull(_columnIndexOfTransactionReference)) {
            _tmpTransactionReference = null
          } else {
            _tmpTransactionReference = _stmt.getText(_columnIndexOfTransactionReference)
          }
          val _tmpNotes: String?
          if (_stmt.isNull(_columnIndexOfNotes)) {
            _tmpNotes = null
          } else {
            _tmpNotes = _stmt.getText(_columnIndexOfNotes)
          }
          val _tmpGpsLat: Double?
          if (_stmt.isNull(_columnIndexOfGpsLat)) {
            _tmpGpsLat = null
          } else {
            _tmpGpsLat = _stmt.getDouble(_columnIndexOfGpsLat)
          }
          val _tmpGpsLng: Double?
          if (_stmt.isNull(_columnIndexOfGpsLng)) {
            _tmpGpsLng = null
          } else {
            _tmpGpsLng = _stmt.getDouble(_columnIndexOfGpsLng)
          }
          val _tmpSellerPhotoUrl: String?
          if (_stmt.isNull(_columnIndexOfSellerPhotoUrl)) {
            _tmpSellerPhotoUrl = null
          } else {
            _tmpSellerPhotoUrl = _stmt.getText(_columnIndexOfSellerPhotoUrl)
          }
          val _tmpBuyerPhotoUrl: String?
          if (_stmt.isNull(_columnIndexOfBuyerPhotoUrl)) {
            _tmpBuyerPhotoUrl = null
          } else {
            _tmpBuyerPhotoUrl = _stmt.getText(_columnIndexOfBuyerPhotoUrl)
          }
          val _tmpTimeoutAt: Long?
          if (_stmt.isNull(_columnIndexOfTimeoutAt)) {
            _tmpTimeoutAt = null
          } else {
            _tmpTimeoutAt = _stmt.getLong(_columnIndexOfTimeoutAt)
          }
          val _tmpConditionsJson: String?
          if (_stmt.isNull(_columnIndexOfConditionsJson)) {
            _tmpConditionsJson = null
          } else {
            _tmpConditionsJson = _stmt.getText(_columnIndexOfConditionsJson)
          }
          val _tmpTransferCode: String?
          if (_stmt.isNull(_columnIndexOfTransferCode)) {
            _tmpTransferCode = null
          } else {
            _tmpTransferCode = _stmt.getText(_columnIndexOfTransferCode)
          }
          val _tmpLineageSnapshotJson: String?
          if (_stmt.isNull(_columnIndexOfLineageSnapshotJson)) {
            _tmpLineageSnapshotJson = null
          } else {
            _tmpLineageSnapshotJson = _stmt.getText(_columnIndexOfLineageSnapshotJson)
          }
          val _tmpClaimedAt: Long?
          if (_stmt.isNull(_columnIndexOfClaimedAt)) {
            _tmpClaimedAt = null
          } else {
            _tmpClaimedAt = _stmt.getLong(_columnIndexOfClaimedAt)
          }
          val _tmpTransferType: String
          _tmpTransferType = _stmt.getText(_columnIndexOfTransferType)
          val _tmpGrowthSnapshotJson: String?
          if (_stmt.isNull(_columnIndexOfGrowthSnapshotJson)) {
            _tmpGrowthSnapshotJson = null
          } else {
            _tmpGrowthSnapshotJson = _stmt.getText(_columnIndexOfGrowthSnapshotJson)
          }
          val _tmpHealthSnapshotJson: String?
          if (_stmt.isNull(_columnIndexOfHealthSnapshotJson)) {
            _tmpHealthSnapshotJson = null
          } else {
            _tmpHealthSnapshotJson = _stmt.getText(_columnIndexOfHealthSnapshotJson)
          }
          val _tmpTransferCodeExpiresAt: Long?
          if (_stmt.isNull(_columnIndexOfTransferCodeExpiresAt)) {
            _tmpTransferCodeExpiresAt = null
          } else {
            _tmpTransferCodeExpiresAt = _stmt.getLong(_columnIndexOfTransferCodeExpiresAt)
          }
          val _tmpInitiatedAt: Long
          _tmpInitiatedAt = _stmt.getLong(_columnIndexOfInitiatedAt)
          val _tmpCompletedAt: Long?
          if (_stmt.isNull(_columnIndexOfCompletedAt)) {
            _tmpCompletedAt = null
          } else {
            _tmpCompletedAt = _stmt.getLong(_columnIndexOfCompletedAt)
          }
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpLastModifiedAt: Long
          _tmpLastModifiedAt = _stmt.getLong(_columnIndexOfLastModifiedAt)
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
          val _tmpSyncedAt: Long?
          if (_stmt.isNull(_columnIndexOfSyncedAt)) {
            _tmpSyncedAt = null
          } else {
            _tmpSyncedAt = _stmt.getLong(_columnIndexOfSyncedAt)
          }
          val _tmpMergedAt: Long?
          if (_stmt.isNull(_columnIndexOfMergedAt)) {
            _tmpMergedAt = null
          } else {
            _tmpMergedAt = _stmt.getLong(_columnIndexOfMergedAt)
          }
          val _tmpMergeCount: Int
          _tmpMergeCount = _stmt.getLong(_columnIndexOfMergeCount).toInt()
          _item =
              TransferEntity(_tmpTransferId,_tmpProductId,_tmpFromUserId,_tmpToUserId,_tmpOrderId,_tmpAmount,_tmpCurrency,_tmpType,_tmpStatus,_tmpTransactionReference,_tmpNotes,_tmpGpsLat,_tmpGpsLng,_tmpSellerPhotoUrl,_tmpBuyerPhotoUrl,_tmpTimeoutAt,_tmpConditionsJson,_tmpTransferCode,_tmpLineageSnapshotJson,_tmpClaimedAt,_tmpTransferType,_tmpGrowthSnapshotJson,_tmpHealthSnapshotJson,_tmpTransferCodeExpiresAt,_tmpInitiatedAt,_tmpCompletedAt,_tmpUpdatedAt,_tmpLastModifiedAt,_tmpIsDeleted,_tmpDeletedAt,_tmpDirty,_tmpSyncedAt,_tmpMergedAt,_tmpMergeCount)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getTransfersByProduct(productId: String): List<TransferEntity> {
    val _sql: String = "SELECT * FROM transfers WHERE productId = ? ORDER BY initiatedAt ASC"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, productId)
        val _columnIndexOfTransferId: Int = getColumnIndexOrThrow(_stmt, "transferId")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfFromUserId: Int = getColumnIndexOrThrow(_stmt, "fromUserId")
        val _columnIndexOfToUserId: Int = getColumnIndexOrThrow(_stmt, "toUserId")
        val _columnIndexOfOrderId: Int = getColumnIndexOrThrow(_stmt, "orderId")
        val _columnIndexOfAmount: Int = getColumnIndexOrThrow(_stmt, "amount")
        val _columnIndexOfCurrency: Int = getColumnIndexOrThrow(_stmt, "currency")
        val _columnIndexOfType: Int = getColumnIndexOrThrow(_stmt, "type")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfTransactionReference: Int = getColumnIndexOrThrow(_stmt,
            "transactionReference")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfGpsLat: Int = getColumnIndexOrThrow(_stmt, "gpsLat")
        val _columnIndexOfGpsLng: Int = getColumnIndexOrThrow(_stmt, "gpsLng")
        val _columnIndexOfSellerPhotoUrl: Int = getColumnIndexOrThrow(_stmt, "sellerPhotoUrl")
        val _columnIndexOfBuyerPhotoUrl: Int = getColumnIndexOrThrow(_stmt, "buyerPhotoUrl")
        val _columnIndexOfTimeoutAt: Int = getColumnIndexOrThrow(_stmt, "timeoutAt")
        val _columnIndexOfConditionsJson: Int = getColumnIndexOrThrow(_stmt, "conditionsJson")
        val _columnIndexOfTransferCode: Int = getColumnIndexOrThrow(_stmt, "transferCode")
        val _columnIndexOfLineageSnapshotJson: Int = getColumnIndexOrThrow(_stmt,
            "lineageSnapshotJson")
        val _columnIndexOfClaimedAt: Int = getColumnIndexOrThrow(_stmt, "claimedAt")
        val _columnIndexOfTransferType: Int = getColumnIndexOrThrow(_stmt, "transferType")
        val _columnIndexOfGrowthSnapshotJson: Int = getColumnIndexOrThrow(_stmt,
            "growthSnapshotJson")
        val _columnIndexOfHealthSnapshotJson: Int = getColumnIndexOrThrow(_stmt,
            "healthSnapshotJson")
        val _columnIndexOfTransferCodeExpiresAt: Int = getColumnIndexOrThrow(_stmt,
            "transferCodeExpiresAt")
        val _columnIndexOfInitiatedAt: Int = getColumnIndexOrThrow(_stmt, "initiatedAt")
        val _columnIndexOfCompletedAt: Int = getColumnIndexOrThrow(_stmt, "completedAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfLastModifiedAt: Int = getColumnIndexOrThrow(_stmt, "lastModifiedAt")
        val _columnIndexOfIsDeleted: Int = getColumnIndexOrThrow(_stmt, "isDeleted")
        val _columnIndexOfDeletedAt: Int = getColumnIndexOrThrow(_stmt, "deletedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _columnIndexOfMergedAt: Int = getColumnIndexOrThrow(_stmt, "mergedAt")
        val _columnIndexOfMergeCount: Int = getColumnIndexOrThrow(_stmt, "mergeCount")
        val _result: MutableList<TransferEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: TransferEntity
          val _tmpTransferId: String
          _tmpTransferId = _stmt.getText(_columnIndexOfTransferId)
          val _tmpProductId: String?
          if (_stmt.isNull(_columnIndexOfProductId)) {
            _tmpProductId = null
          } else {
            _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          }
          val _tmpFromUserId: String?
          if (_stmt.isNull(_columnIndexOfFromUserId)) {
            _tmpFromUserId = null
          } else {
            _tmpFromUserId = _stmt.getText(_columnIndexOfFromUserId)
          }
          val _tmpToUserId: String?
          if (_stmt.isNull(_columnIndexOfToUserId)) {
            _tmpToUserId = null
          } else {
            _tmpToUserId = _stmt.getText(_columnIndexOfToUserId)
          }
          val _tmpOrderId: String?
          if (_stmt.isNull(_columnIndexOfOrderId)) {
            _tmpOrderId = null
          } else {
            _tmpOrderId = _stmt.getText(_columnIndexOfOrderId)
          }
          val _tmpAmount: Double
          _tmpAmount = _stmt.getDouble(_columnIndexOfAmount)
          val _tmpCurrency: String
          _tmpCurrency = _stmt.getText(_columnIndexOfCurrency)
          val _tmpType: String
          _tmpType = _stmt.getText(_columnIndexOfType)
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpTransactionReference: String?
          if (_stmt.isNull(_columnIndexOfTransactionReference)) {
            _tmpTransactionReference = null
          } else {
            _tmpTransactionReference = _stmt.getText(_columnIndexOfTransactionReference)
          }
          val _tmpNotes: String?
          if (_stmt.isNull(_columnIndexOfNotes)) {
            _tmpNotes = null
          } else {
            _tmpNotes = _stmt.getText(_columnIndexOfNotes)
          }
          val _tmpGpsLat: Double?
          if (_stmt.isNull(_columnIndexOfGpsLat)) {
            _tmpGpsLat = null
          } else {
            _tmpGpsLat = _stmt.getDouble(_columnIndexOfGpsLat)
          }
          val _tmpGpsLng: Double?
          if (_stmt.isNull(_columnIndexOfGpsLng)) {
            _tmpGpsLng = null
          } else {
            _tmpGpsLng = _stmt.getDouble(_columnIndexOfGpsLng)
          }
          val _tmpSellerPhotoUrl: String?
          if (_stmt.isNull(_columnIndexOfSellerPhotoUrl)) {
            _tmpSellerPhotoUrl = null
          } else {
            _tmpSellerPhotoUrl = _stmt.getText(_columnIndexOfSellerPhotoUrl)
          }
          val _tmpBuyerPhotoUrl: String?
          if (_stmt.isNull(_columnIndexOfBuyerPhotoUrl)) {
            _tmpBuyerPhotoUrl = null
          } else {
            _tmpBuyerPhotoUrl = _stmt.getText(_columnIndexOfBuyerPhotoUrl)
          }
          val _tmpTimeoutAt: Long?
          if (_stmt.isNull(_columnIndexOfTimeoutAt)) {
            _tmpTimeoutAt = null
          } else {
            _tmpTimeoutAt = _stmt.getLong(_columnIndexOfTimeoutAt)
          }
          val _tmpConditionsJson: String?
          if (_stmt.isNull(_columnIndexOfConditionsJson)) {
            _tmpConditionsJson = null
          } else {
            _tmpConditionsJson = _stmt.getText(_columnIndexOfConditionsJson)
          }
          val _tmpTransferCode: String?
          if (_stmt.isNull(_columnIndexOfTransferCode)) {
            _tmpTransferCode = null
          } else {
            _tmpTransferCode = _stmt.getText(_columnIndexOfTransferCode)
          }
          val _tmpLineageSnapshotJson: String?
          if (_stmt.isNull(_columnIndexOfLineageSnapshotJson)) {
            _tmpLineageSnapshotJson = null
          } else {
            _tmpLineageSnapshotJson = _stmt.getText(_columnIndexOfLineageSnapshotJson)
          }
          val _tmpClaimedAt: Long?
          if (_stmt.isNull(_columnIndexOfClaimedAt)) {
            _tmpClaimedAt = null
          } else {
            _tmpClaimedAt = _stmt.getLong(_columnIndexOfClaimedAt)
          }
          val _tmpTransferType: String
          _tmpTransferType = _stmt.getText(_columnIndexOfTransferType)
          val _tmpGrowthSnapshotJson: String?
          if (_stmt.isNull(_columnIndexOfGrowthSnapshotJson)) {
            _tmpGrowthSnapshotJson = null
          } else {
            _tmpGrowthSnapshotJson = _stmt.getText(_columnIndexOfGrowthSnapshotJson)
          }
          val _tmpHealthSnapshotJson: String?
          if (_stmt.isNull(_columnIndexOfHealthSnapshotJson)) {
            _tmpHealthSnapshotJson = null
          } else {
            _tmpHealthSnapshotJson = _stmt.getText(_columnIndexOfHealthSnapshotJson)
          }
          val _tmpTransferCodeExpiresAt: Long?
          if (_stmt.isNull(_columnIndexOfTransferCodeExpiresAt)) {
            _tmpTransferCodeExpiresAt = null
          } else {
            _tmpTransferCodeExpiresAt = _stmt.getLong(_columnIndexOfTransferCodeExpiresAt)
          }
          val _tmpInitiatedAt: Long
          _tmpInitiatedAt = _stmt.getLong(_columnIndexOfInitiatedAt)
          val _tmpCompletedAt: Long?
          if (_stmt.isNull(_columnIndexOfCompletedAt)) {
            _tmpCompletedAt = null
          } else {
            _tmpCompletedAt = _stmt.getLong(_columnIndexOfCompletedAt)
          }
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpLastModifiedAt: Long
          _tmpLastModifiedAt = _stmt.getLong(_columnIndexOfLastModifiedAt)
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
          val _tmpSyncedAt: Long?
          if (_stmt.isNull(_columnIndexOfSyncedAt)) {
            _tmpSyncedAt = null
          } else {
            _tmpSyncedAt = _stmt.getLong(_columnIndexOfSyncedAt)
          }
          val _tmpMergedAt: Long?
          if (_stmt.isNull(_columnIndexOfMergedAt)) {
            _tmpMergedAt = null
          } else {
            _tmpMergedAt = _stmt.getLong(_columnIndexOfMergedAt)
          }
          val _tmpMergeCount: Int
          _tmpMergeCount = _stmt.getLong(_columnIndexOfMergeCount).toInt()
          _item =
              TransferEntity(_tmpTransferId,_tmpProductId,_tmpFromUserId,_tmpToUserId,_tmpOrderId,_tmpAmount,_tmpCurrency,_tmpType,_tmpStatus,_tmpTransactionReference,_tmpNotes,_tmpGpsLat,_tmpGpsLng,_tmpSellerPhotoUrl,_tmpBuyerPhotoUrl,_tmpTimeoutAt,_tmpConditionsJson,_tmpTransferCode,_tmpLineageSnapshotJson,_tmpClaimedAt,_tmpTransferType,_tmpGrowthSnapshotJson,_tmpHealthSnapshotJson,_tmpTransferCodeExpiresAt,_tmpInitiatedAt,_tmpCompletedAt,_tmpUpdatedAt,_tmpLastModifiedAt,_tmpIsDeleted,_tmpDeletedAt,_tmpDirty,_tmpSyncedAt,_tmpMergedAt,_tmpMergeCount)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getById(transferId: String): TransferEntity? {
    val _sql: String = "SELECT * FROM transfers WHERE transferId = ? LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, transferId)
        val _columnIndexOfTransferId: Int = getColumnIndexOrThrow(_stmt, "transferId")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfFromUserId: Int = getColumnIndexOrThrow(_stmt, "fromUserId")
        val _columnIndexOfToUserId: Int = getColumnIndexOrThrow(_stmt, "toUserId")
        val _columnIndexOfOrderId: Int = getColumnIndexOrThrow(_stmt, "orderId")
        val _columnIndexOfAmount: Int = getColumnIndexOrThrow(_stmt, "amount")
        val _columnIndexOfCurrency: Int = getColumnIndexOrThrow(_stmt, "currency")
        val _columnIndexOfType: Int = getColumnIndexOrThrow(_stmt, "type")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfTransactionReference: Int = getColumnIndexOrThrow(_stmt,
            "transactionReference")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfGpsLat: Int = getColumnIndexOrThrow(_stmt, "gpsLat")
        val _columnIndexOfGpsLng: Int = getColumnIndexOrThrow(_stmt, "gpsLng")
        val _columnIndexOfSellerPhotoUrl: Int = getColumnIndexOrThrow(_stmt, "sellerPhotoUrl")
        val _columnIndexOfBuyerPhotoUrl: Int = getColumnIndexOrThrow(_stmt, "buyerPhotoUrl")
        val _columnIndexOfTimeoutAt: Int = getColumnIndexOrThrow(_stmt, "timeoutAt")
        val _columnIndexOfConditionsJson: Int = getColumnIndexOrThrow(_stmt, "conditionsJson")
        val _columnIndexOfTransferCode: Int = getColumnIndexOrThrow(_stmt, "transferCode")
        val _columnIndexOfLineageSnapshotJson: Int = getColumnIndexOrThrow(_stmt,
            "lineageSnapshotJson")
        val _columnIndexOfClaimedAt: Int = getColumnIndexOrThrow(_stmt, "claimedAt")
        val _columnIndexOfTransferType: Int = getColumnIndexOrThrow(_stmt, "transferType")
        val _columnIndexOfGrowthSnapshotJson: Int = getColumnIndexOrThrow(_stmt,
            "growthSnapshotJson")
        val _columnIndexOfHealthSnapshotJson: Int = getColumnIndexOrThrow(_stmt,
            "healthSnapshotJson")
        val _columnIndexOfTransferCodeExpiresAt: Int = getColumnIndexOrThrow(_stmt,
            "transferCodeExpiresAt")
        val _columnIndexOfInitiatedAt: Int = getColumnIndexOrThrow(_stmt, "initiatedAt")
        val _columnIndexOfCompletedAt: Int = getColumnIndexOrThrow(_stmt, "completedAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfLastModifiedAt: Int = getColumnIndexOrThrow(_stmt, "lastModifiedAt")
        val _columnIndexOfIsDeleted: Int = getColumnIndexOrThrow(_stmt, "isDeleted")
        val _columnIndexOfDeletedAt: Int = getColumnIndexOrThrow(_stmt, "deletedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _columnIndexOfMergedAt: Int = getColumnIndexOrThrow(_stmt, "mergedAt")
        val _columnIndexOfMergeCount: Int = getColumnIndexOrThrow(_stmt, "mergeCount")
        val _result: TransferEntity?
        if (_stmt.step()) {
          val _tmpTransferId: String
          _tmpTransferId = _stmt.getText(_columnIndexOfTransferId)
          val _tmpProductId: String?
          if (_stmt.isNull(_columnIndexOfProductId)) {
            _tmpProductId = null
          } else {
            _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          }
          val _tmpFromUserId: String?
          if (_stmt.isNull(_columnIndexOfFromUserId)) {
            _tmpFromUserId = null
          } else {
            _tmpFromUserId = _stmt.getText(_columnIndexOfFromUserId)
          }
          val _tmpToUserId: String?
          if (_stmt.isNull(_columnIndexOfToUserId)) {
            _tmpToUserId = null
          } else {
            _tmpToUserId = _stmt.getText(_columnIndexOfToUserId)
          }
          val _tmpOrderId: String?
          if (_stmt.isNull(_columnIndexOfOrderId)) {
            _tmpOrderId = null
          } else {
            _tmpOrderId = _stmt.getText(_columnIndexOfOrderId)
          }
          val _tmpAmount: Double
          _tmpAmount = _stmt.getDouble(_columnIndexOfAmount)
          val _tmpCurrency: String
          _tmpCurrency = _stmt.getText(_columnIndexOfCurrency)
          val _tmpType: String
          _tmpType = _stmt.getText(_columnIndexOfType)
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpTransactionReference: String?
          if (_stmt.isNull(_columnIndexOfTransactionReference)) {
            _tmpTransactionReference = null
          } else {
            _tmpTransactionReference = _stmt.getText(_columnIndexOfTransactionReference)
          }
          val _tmpNotes: String?
          if (_stmt.isNull(_columnIndexOfNotes)) {
            _tmpNotes = null
          } else {
            _tmpNotes = _stmt.getText(_columnIndexOfNotes)
          }
          val _tmpGpsLat: Double?
          if (_stmt.isNull(_columnIndexOfGpsLat)) {
            _tmpGpsLat = null
          } else {
            _tmpGpsLat = _stmt.getDouble(_columnIndexOfGpsLat)
          }
          val _tmpGpsLng: Double?
          if (_stmt.isNull(_columnIndexOfGpsLng)) {
            _tmpGpsLng = null
          } else {
            _tmpGpsLng = _stmt.getDouble(_columnIndexOfGpsLng)
          }
          val _tmpSellerPhotoUrl: String?
          if (_stmt.isNull(_columnIndexOfSellerPhotoUrl)) {
            _tmpSellerPhotoUrl = null
          } else {
            _tmpSellerPhotoUrl = _stmt.getText(_columnIndexOfSellerPhotoUrl)
          }
          val _tmpBuyerPhotoUrl: String?
          if (_stmt.isNull(_columnIndexOfBuyerPhotoUrl)) {
            _tmpBuyerPhotoUrl = null
          } else {
            _tmpBuyerPhotoUrl = _stmt.getText(_columnIndexOfBuyerPhotoUrl)
          }
          val _tmpTimeoutAt: Long?
          if (_stmt.isNull(_columnIndexOfTimeoutAt)) {
            _tmpTimeoutAt = null
          } else {
            _tmpTimeoutAt = _stmt.getLong(_columnIndexOfTimeoutAt)
          }
          val _tmpConditionsJson: String?
          if (_stmt.isNull(_columnIndexOfConditionsJson)) {
            _tmpConditionsJson = null
          } else {
            _tmpConditionsJson = _stmt.getText(_columnIndexOfConditionsJson)
          }
          val _tmpTransferCode: String?
          if (_stmt.isNull(_columnIndexOfTransferCode)) {
            _tmpTransferCode = null
          } else {
            _tmpTransferCode = _stmt.getText(_columnIndexOfTransferCode)
          }
          val _tmpLineageSnapshotJson: String?
          if (_stmt.isNull(_columnIndexOfLineageSnapshotJson)) {
            _tmpLineageSnapshotJson = null
          } else {
            _tmpLineageSnapshotJson = _stmt.getText(_columnIndexOfLineageSnapshotJson)
          }
          val _tmpClaimedAt: Long?
          if (_stmt.isNull(_columnIndexOfClaimedAt)) {
            _tmpClaimedAt = null
          } else {
            _tmpClaimedAt = _stmt.getLong(_columnIndexOfClaimedAt)
          }
          val _tmpTransferType: String
          _tmpTransferType = _stmt.getText(_columnIndexOfTransferType)
          val _tmpGrowthSnapshotJson: String?
          if (_stmt.isNull(_columnIndexOfGrowthSnapshotJson)) {
            _tmpGrowthSnapshotJson = null
          } else {
            _tmpGrowthSnapshotJson = _stmt.getText(_columnIndexOfGrowthSnapshotJson)
          }
          val _tmpHealthSnapshotJson: String?
          if (_stmt.isNull(_columnIndexOfHealthSnapshotJson)) {
            _tmpHealthSnapshotJson = null
          } else {
            _tmpHealthSnapshotJson = _stmt.getText(_columnIndexOfHealthSnapshotJson)
          }
          val _tmpTransferCodeExpiresAt: Long?
          if (_stmt.isNull(_columnIndexOfTransferCodeExpiresAt)) {
            _tmpTransferCodeExpiresAt = null
          } else {
            _tmpTransferCodeExpiresAt = _stmt.getLong(_columnIndexOfTransferCodeExpiresAt)
          }
          val _tmpInitiatedAt: Long
          _tmpInitiatedAt = _stmt.getLong(_columnIndexOfInitiatedAt)
          val _tmpCompletedAt: Long?
          if (_stmt.isNull(_columnIndexOfCompletedAt)) {
            _tmpCompletedAt = null
          } else {
            _tmpCompletedAt = _stmt.getLong(_columnIndexOfCompletedAt)
          }
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpLastModifiedAt: Long
          _tmpLastModifiedAt = _stmt.getLong(_columnIndexOfLastModifiedAt)
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
          val _tmpSyncedAt: Long?
          if (_stmt.isNull(_columnIndexOfSyncedAt)) {
            _tmpSyncedAt = null
          } else {
            _tmpSyncedAt = _stmt.getLong(_columnIndexOfSyncedAt)
          }
          val _tmpMergedAt: Long?
          if (_stmt.isNull(_columnIndexOfMergedAt)) {
            _tmpMergedAt = null
          } else {
            _tmpMergedAt = _stmt.getLong(_columnIndexOfMergedAt)
          }
          val _tmpMergeCount: Int
          _tmpMergeCount = _stmt.getLong(_columnIndexOfMergeCount).toInt()
          _result =
              TransferEntity(_tmpTransferId,_tmpProductId,_tmpFromUserId,_tmpToUserId,_tmpOrderId,_tmpAmount,_tmpCurrency,_tmpType,_tmpStatus,_tmpTransactionReference,_tmpNotes,_tmpGpsLat,_tmpGpsLng,_tmpSellerPhotoUrl,_tmpBuyerPhotoUrl,_tmpTimeoutAt,_tmpConditionsJson,_tmpTransferCode,_tmpLineageSnapshotJson,_tmpClaimedAt,_tmpTransferType,_tmpGrowthSnapshotJson,_tmpHealthSnapshotJson,_tmpTransferCodeExpiresAt,_tmpInitiatedAt,_tmpCompletedAt,_tmpUpdatedAt,_tmpLastModifiedAt,_tmpIsDeleted,_tmpDeletedAt,_tmpDirty,_tmpSyncedAt,_tmpMergedAt,_tmpMergeCount)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getByProductAndStatus(productId: String, status: String):
      List<TransferEntity> {
    val _sql: String =
        "SELECT * FROM transfers WHERE productId = ? AND status = ? ORDER BY initiatedAt DESC"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, productId)
        _argIndex = 2
        _stmt.bindText(_argIndex, status)
        val _columnIndexOfTransferId: Int = getColumnIndexOrThrow(_stmt, "transferId")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfFromUserId: Int = getColumnIndexOrThrow(_stmt, "fromUserId")
        val _columnIndexOfToUserId: Int = getColumnIndexOrThrow(_stmt, "toUserId")
        val _columnIndexOfOrderId: Int = getColumnIndexOrThrow(_stmt, "orderId")
        val _columnIndexOfAmount: Int = getColumnIndexOrThrow(_stmt, "amount")
        val _columnIndexOfCurrency: Int = getColumnIndexOrThrow(_stmt, "currency")
        val _columnIndexOfType: Int = getColumnIndexOrThrow(_stmt, "type")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfTransactionReference: Int = getColumnIndexOrThrow(_stmt,
            "transactionReference")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfGpsLat: Int = getColumnIndexOrThrow(_stmt, "gpsLat")
        val _columnIndexOfGpsLng: Int = getColumnIndexOrThrow(_stmt, "gpsLng")
        val _columnIndexOfSellerPhotoUrl: Int = getColumnIndexOrThrow(_stmt, "sellerPhotoUrl")
        val _columnIndexOfBuyerPhotoUrl: Int = getColumnIndexOrThrow(_stmt, "buyerPhotoUrl")
        val _columnIndexOfTimeoutAt: Int = getColumnIndexOrThrow(_stmt, "timeoutAt")
        val _columnIndexOfConditionsJson: Int = getColumnIndexOrThrow(_stmt, "conditionsJson")
        val _columnIndexOfTransferCode: Int = getColumnIndexOrThrow(_stmt, "transferCode")
        val _columnIndexOfLineageSnapshotJson: Int = getColumnIndexOrThrow(_stmt,
            "lineageSnapshotJson")
        val _columnIndexOfClaimedAt: Int = getColumnIndexOrThrow(_stmt, "claimedAt")
        val _columnIndexOfTransferType: Int = getColumnIndexOrThrow(_stmt, "transferType")
        val _columnIndexOfGrowthSnapshotJson: Int = getColumnIndexOrThrow(_stmt,
            "growthSnapshotJson")
        val _columnIndexOfHealthSnapshotJson: Int = getColumnIndexOrThrow(_stmt,
            "healthSnapshotJson")
        val _columnIndexOfTransferCodeExpiresAt: Int = getColumnIndexOrThrow(_stmt,
            "transferCodeExpiresAt")
        val _columnIndexOfInitiatedAt: Int = getColumnIndexOrThrow(_stmt, "initiatedAt")
        val _columnIndexOfCompletedAt: Int = getColumnIndexOrThrow(_stmt, "completedAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfLastModifiedAt: Int = getColumnIndexOrThrow(_stmt, "lastModifiedAt")
        val _columnIndexOfIsDeleted: Int = getColumnIndexOrThrow(_stmt, "isDeleted")
        val _columnIndexOfDeletedAt: Int = getColumnIndexOrThrow(_stmt, "deletedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _columnIndexOfMergedAt: Int = getColumnIndexOrThrow(_stmt, "mergedAt")
        val _columnIndexOfMergeCount: Int = getColumnIndexOrThrow(_stmt, "mergeCount")
        val _result: MutableList<TransferEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: TransferEntity
          val _tmpTransferId: String
          _tmpTransferId = _stmt.getText(_columnIndexOfTransferId)
          val _tmpProductId: String?
          if (_stmt.isNull(_columnIndexOfProductId)) {
            _tmpProductId = null
          } else {
            _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          }
          val _tmpFromUserId: String?
          if (_stmt.isNull(_columnIndexOfFromUserId)) {
            _tmpFromUserId = null
          } else {
            _tmpFromUserId = _stmt.getText(_columnIndexOfFromUserId)
          }
          val _tmpToUserId: String?
          if (_stmt.isNull(_columnIndexOfToUserId)) {
            _tmpToUserId = null
          } else {
            _tmpToUserId = _stmt.getText(_columnIndexOfToUserId)
          }
          val _tmpOrderId: String?
          if (_stmt.isNull(_columnIndexOfOrderId)) {
            _tmpOrderId = null
          } else {
            _tmpOrderId = _stmt.getText(_columnIndexOfOrderId)
          }
          val _tmpAmount: Double
          _tmpAmount = _stmt.getDouble(_columnIndexOfAmount)
          val _tmpCurrency: String
          _tmpCurrency = _stmt.getText(_columnIndexOfCurrency)
          val _tmpType: String
          _tmpType = _stmt.getText(_columnIndexOfType)
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpTransactionReference: String?
          if (_stmt.isNull(_columnIndexOfTransactionReference)) {
            _tmpTransactionReference = null
          } else {
            _tmpTransactionReference = _stmt.getText(_columnIndexOfTransactionReference)
          }
          val _tmpNotes: String?
          if (_stmt.isNull(_columnIndexOfNotes)) {
            _tmpNotes = null
          } else {
            _tmpNotes = _stmt.getText(_columnIndexOfNotes)
          }
          val _tmpGpsLat: Double?
          if (_stmt.isNull(_columnIndexOfGpsLat)) {
            _tmpGpsLat = null
          } else {
            _tmpGpsLat = _stmt.getDouble(_columnIndexOfGpsLat)
          }
          val _tmpGpsLng: Double?
          if (_stmt.isNull(_columnIndexOfGpsLng)) {
            _tmpGpsLng = null
          } else {
            _tmpGpsLng = _stmt.getDouble(_columnIndexOfGpsLng)
          }
          val _tmpSellerPhotoUrl: String?
          if (_stmt.isNull(_columnIndexOfSellerPhotoUrl)) {
            _tmpSellerPhotoUrl = null
          } else {
            _tmpSellerPhotoUrl = _stmt.getText(_columnIndexOfSellerPhotoUrl)
          }
          val _tmpBuyerPhotoUrl: String?
          if (_stmt.isNull(_columnIndexOfBuyerPhotoUrl)) {
            _tmpBuyerPhotoUrl = null
          } else {
            _tmpBuyerPhotoUrl = _stmt.getText(_columnIndexOfBuyerPhotoUrl)
          }
          val _tmpTimeoutAt: Long?
          if (_stmt.isNull(_columnIndexOfTimeoutAt)) {
            _tmpTimeoutAt = null
          } else {
            _tmpTimeoutAt = _stmt.getLong(_columnIndexOfTimeoutAt)
          }
          val _tmpConditionsJson: String?
          if (_stmt.isNull(_columnIndexOfConditionsJson)) {
            _tmpConditionsJson = null
          } else {
            _tmpConditionsJson = _stmt.getText(_columnIndexOfConditionsJson)
          }
          val _tmpTransferCode: String?
          if (_stmt.isNull(_columnIndexOfTransferCode)) {
            _tmpTransferCode = null
          } else {
            _tmpTransferCode = _stmt.getText(_columnIndexOfTransferCode)
          }
          val _tmpLineageSnapshotJson: String?
          if (_stmt.isNull(_columnIndexOfLineageSnapshotJson)) {
            _tmpLineageSnapshotJson = null
          } else {
            _tmpLineageSnapshotJson = _stmt.getText(_columnIndexOfLineageSnapshotJson)
          }
          val _tmpClaimedAt: Long?
          if (_stmt.isNull(_columnIndexOfClaimedAt)) {
            _tmpClaimedAt = null
          } else {
            _tmpClaimedAt = _stmt.getLong(_columnIndexOfClaimedAt)
          }
          val _tmpTransferType: String
          _tmpTransferType = _stmt.getText(_columnIndexOfTransferType)
          val _tmpGrowthSnapshotJson: String?
          if (_stmt.isNull(_columnIndexOfGrowthSnapshotJson)) {
            _tmpGrowthSnapshotJson = null
          } else {
            _tmpGrowthSnapshotJson = _stmt.getText(_columnIndexOfGrowthSnapshotJson)
          }
          val _tmpHealthSnapshotJson: String?
          if (_stmt.isNull(_columnIndexOfHealthSnapshotJson)) {
            _tmpHealthSnapshotJson = null
          } else {
            _tmpHealthSnapshotJson = _stmt.getText(_columnIndexOfHealthSnapshotJson)
          }
          val _tmpTransferCodeExpiresAt: Long?
          if (_stmt.isNull(_columnIndexOfTransferCodeExpiresAt)) {
            _tmpTransferCodeExpiresAt = null
          } else {
            _tmpTransferCodeExpiresAt = _stmt.getLong(_columnIndexOfTransferCodeExpiresAt)
          }
          val _tmpInitiatedAt: Long
          _tmpInitiatedAt = _stmt.getLong(_columnIndexOfInitiatedAt)
          val _tmpCompletedAt: Long?
          if (_stmt.isNull(_columnIndexOfCompletedAt)) {
            _tmpCompletedAt = null
          } else {
            _tmpCompletedAt = _stmt.getLong(_columnIndexOfCompletedAt)
          }
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpLastModifiedAt: Long
          _tmpLastModifiedAt = _stmt.getLong(_columnIndexOfLastModifiedAt)
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
          val _tmpSyncedAt: Long?
          if (_stmt.isNull(_columnIndexOfSyncedAt)) {
            _tmpSyncedAt = null
          } else {
            _tmpSyncedAt = _stmt.getLong(_columnIndexOfSyncedAt)
          }
          val _tmpMergedAt: Long?
          if (_stmt.isNull(_columnIndexOfMergedAt)) {
            _tmpMergedAt = null
          } else {
            _tmpMergedAt = _stmt.getLong(_columnIndexOfMergedAt)
          }
          val _tmpMergeCount: Int
          _tmpMergeCount = _stmt.getLong(_columnIndexOfMergeCount).toInt()
          _item =
              TransferEntity(_tmpTransferId,_tmpProductId,_tmpFromUserId,_tmpToUserId,_tmpOrderId,_tmpAmount,_tmpCurrency,_tmpType,_tmpStatus,_tmpTransactionReference,_tmpNotes,_tmpGpsLat,_tmpGpsLng,_tmpSellerPhotoUrl,_tmpBuyerPhotoUrl,_tmpTimeoutAt,_tmpConditionsJson,_tmpTransferCode,_tmpLineageSnapshotJson,_tmpClaimedAt,_tmpTransferType,_tmpGrowthSnapshotJson,_tmpHealthSnapshotJson,_tmpTransferCodeExpiresAt,_tmpInitiatedAt,_tmpCompletedAt,_tmpUpdatedAt,_tmpLastModifiedAt,_tmpIsDeleted,_tmpDeletedAt,_tmpDirty,_tmpSyncedAt,_tmpMergedAt,_tmpMergeCount)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun countRecentPending(
    productId: String,
    fromUserId: String,
    toUserId: String,
    since: Long,
  ): Int {
    val _sql: String =
        "SELECT COUNT(*) FROM transfers WHERE productId = ? AND fromUserId = ? AND toUserId = ? AND status = 'PENDING' AND initiatedAt > ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, productId)
        _argIndex = 2
        _stmt.bindText(_argIndex, fromUserId)
        _argIndex = 3
        _stmt.bindText(_argIndex, toUserId)
        _argIndex = 4
        _stmt.bindLong(_argIndex, since)
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

  public override suspend fun getPendingTimedOut(now: Long): List<TransferEntity> {
    val _sql: String =
        "SELECT * FROM transfers WHERE status = 'PENDING' AND timeoutAt IS NOT NULL AND timeoutAt < ? ORDER BY timeoutAt ASC"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, now)
        val _columnIndexOfTransferId: Int = getColumnIndexOrThrow(_stmt, "transferId")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfFromUserId: Int = getColumnIndexOrThrow(_stmt, "fromUserId")
        val _columnIndexOfToUserId: Int = getColumnIndexOrThrow(_stmt, "toUserId")
        val _columnIndexOfOrderId: Int = getColumnIndexOrThrow(_stmt, "orderId")
        val _columnIndexOfAmount: Int = getColumnIndexOrThrow(_stmt, "amount")
        val _columnIndexOfCurrency: Int = getColumnIndexOrThrow(_stmt, "currency")
        val _columnIndexOfType: Int = getColumnIndexOrThrow(_stmt, "type")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfTransactionReference: Int = getColumnIndexOrThrow(_stmt,
            "transactionReference")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfGpsLat: Int = getColumnIndexOrThrow(_stmt, "gpsLat")
        val _columnIndexOfGpsLng: Int = getColumnIndexOrThrow(_stmt, "gpsLng")
        val _columnIndexOfSellerPhotoUrl: Int = getColumnIndexOrThrow(_stmt, "sellerPhotoUrl")
        val _columnIndexOfBuyerPhotoUrl: Int = getColumnIndexOrThrow(_stmt, "buyerPhotoUrl")
        val _columnIndexOfTimeoutAt: Int = getColumnIndexOrThrow(_stmt, "timeoutAt")
        val _columnIndexOfConditionsJson: Int = getColumnIndexOrThrow(_stmt, "conditionsJson")
        val _columnIndexOfTransferCode: Int = getColumnIndexOrThrow(_stmt, "transferCode")
        val _columnIndexOfLineageSnapshotJson: Int = getColumnIndexOrThrow(_stmt,
            "lineageSnapshotJson")
        val _columnIndexOfClaimedAt: Int = getColumnIndexOrThrow(_stmt, "claimedAt")
        val _columnIndexOfTransferType: Int = getColumnIndexOrThrow(_stmt, "transferType")
        val _columnIndexOfGrowthSnapshotJson: Int = getColumnIndexOrThrow(_stmt,
            "growthSnapshotJson")
        val _columnIndexOfHealthSnapshotJson: Int = getColumnIndexOrThrow(_stmt,
            "healthSnapshotJson")
        val _columnIndexOfTransferCodeExpiresAt: Int = getColumnIndexOrThrow(_stmt,
            "transferCodeExpiresAt")
        val _columnIndexOfInitiatedAt: Int = getColumnIndexOrThrow(_stmt, "initiatedAt")
        val _columnIndexOfCompletedAt: Int = getColumnIndexOrThrow(_stmt, "completedAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfLastModifiedAt: Int = getColumnIndexOrThrow(_stmt, "lastModifiedAt")
        val _columnIndexOfIsDeleted: Int = getColumnIndexOrThrow(_stmt, "isDeleted")
        val _columnIndexOfDeletedAt: Int = getColumnIndexOrThrow(_stmt, "deletedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _columnIndexOfMergedAt: Int = getColumnIndexOrThrow(_stmt, "mergedAt")
        val _columnIndexOfMergeCount: Int = getColumnIndexOrThrow(_stmt, "mergeCount")
        val _result: MutableList<TransferEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: TransferEntity
          val _tmpTransferId: String
          _tmpTransferId = _stmt.getText(_columnIndexOfTransferId)
          val _tmpProductId: String?
          if (_stmt.isNull(_columnIndexOfProductId)) {
            _tmpProductId = null
          } else {
            _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          }
          val _tmpFromUserId: String?
          if (_stmt.isNull(_columnIndexOfFromUserId)) {
            _tmpFromUserId = null
          } else {
            _tmpFromUserId = _stmt.getText(_columnIndexOfFromUserId)
          }
          val _tmpToUserId: String?
          if (_stmt.isNull(_columnIndexOfToUserId)) {
            _tmpToUserId = null
          } else {
            _tmpToUserId = _stmt.getText(_columnIndexOfToUserId)
          }
          val _tmpOrderId: String?
          if (_stmt.isNull(_columnIndexOfOrderId)) {
            _tmpOrderId = null
          } else {
            _tmpOrderId = _stmt.getText(_columnIndexOfOrderId)
          }
          val _tmpAmount: Double
          _tmpAmount = _stmt.getDouble(_columnIndexOfAmount)
          val _tmpCurrency: String
          _tmpCurrency = _stmt.getText(_columnIndexOfCurrency)
          val _tmpType: String
          _tmpType = _stmt.getText(_columnIndexOfType)
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpTransactionReference: String?
          if (_stmt.isNull(_columnIndexOfTransactionReference)) {
            _tmpTransactionReference = null
          } else {
            _tmpTransactionReference = _stmt.getText(_columnIndexOfTransactionReference)
          }
          val _tmpNotes: String?
          if (_stmt.isNull(_columnIndexOfNotes)) {
            _tmpNotes = null
          } else {
            _tmpNotes = _stmt.getText(_columnIndexOfNotes)
          }
          val _tmpGpsLat: Double?
          if (_stmt.isNull(_columnIndexOfGpsLat)) {
            _tmpGpsLat = null
          } else {
            _tmpGpsLat = _stmt.getDouble(_columnIndexOfGpsLat)
          }
          val _tmpGpsLng: Double?
          if (_stmt.isNull(_columnIndexOfGpsLng)) {
            _tmpGpsLng = null
          } else {
            _tmpGpsLng = _stmt.getDouble(_columnIndexOfGpsLng)
          }
          val _tmpSellerPhotoUrl: String?
          if (_stmt.isNull(_columnIndexOfSellerPhotoUrl)) {
            _tmpSellerPhotoUrl = null
          } else {
            _tmpSellerPhotoUrl = _stmt.getText(_columnIndexOfSellerPhotoUrl)
          }
          val _tmpBuyerPhotoUrl: String?
          if (_stmt.isNull(_columnIndexOfBuyerPhotoUrl)) {
            _tmpBuyerPhotoUrl = null
          } else {
            _tmpBuyerPhotoUrl = _stmt.getText(_columnIndexOfBuyerPhotoUrl)
          }
          val _tmpTimeoutAt: Long?
          if (_stmt.isNull(_columnIndexOfTimeoutAt)) {
            _tmpTimeoutAt = null
          } else {
            _tmpTimeoutAt = _stmt.getLong(_columnIndexOfTimeoutAt)
          }
          val _tmpConditionsJson: String?
          if (_stmt.isNull(_columnIndexOfConditionsJson)) {
            _tmpConditionsJson = null
          } else {
            _tmpConditionsJson = _stmt.getText(_columnIndexOfConditionsJson)
          }
          val _tmpTransferCode: String?
          if (_stmt.isNull(_columnIndexOfTransferCode)) {
            _tmpTransferCode = null
          } else {
            _tmpTransferCode = _stmt.getText(_columnIndexOfTransferCode)
          }
          val _tmpLineageSnapshotJson: String?
          if (_stmt.isNull(_columnIndexOfLineageSnapshotJson)) {
            _tmpLineageSnapshotJson = null
          } else {
            _tmpLineageSnapshotJson = _stmt.getText(_columnIndexOfLineageSnapshotJson)
          }
          val _tmpClaimedAt: Long?
          if (_stmt.isNull(_columnIndexOfClaimedAt)) {
            _tmpClaimedAt = null
          } else {
            _tmpClaimedAt = _stmt.getLong(_columnIndexOfClaimedAt)
          }
          val _tmpTransferType: String
          _tmpTransferType = _stmt.getText(_columnIndexOfTransferType)
          val _tmpGrowthSnapshotJson: String?
          if (_stmt.isNull(_columnIndexOfGrowthSnapshotJson)) {
            _tmpGrowthSnapshotJson = null
          } else {
            _tmpGrowthSnapshotJson = _stmt.getText(_columnIndexOfGrowthSnapshotJson)
          }
          val _tmpHealthSnapshotJson: String?
          if (_stmt.isNull(_columnIndexOfHealthSnapshotJson)) {
            _tmpHealthSnapshotJson = null
          } else {
            _tmpHealthSnapshotJson = _stmt.getText(_columnIndexOfHealthSnapshotJson)
          }
          val _tmpTransferCodeExpiresAt: Long?
          if (_stmt.isNull(_columnIndexOfTransferCodeExpiresAt)) {
            _tmpTransferCodeExpiresAt = null
          } else {
            _tmpTransferCodeExpiresAt = _stmt.getLong(_columnIndexOfTransferCodeExpiresAt)
          }
          val _tmpInitiatedAt: Long
          _tmpInitiatedAt = _stmt.getLong(_columnIndexOfInitiatedAt)
          val _tmpCompletedAt: Long?
          if (_stmt.isNull(_columnIndexOfCompletedAt)) {
            _tmpCompletedAt = null
          } else {
            _tmpCompletedAt = _stmt.getLong(_columnIndexOfCompletedAt)
          }
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpLastModifiedAt: Long
          _tmpLastModifiedAt = _stmt.getLong(_columnIndexOfLastModifiedAt)
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
          val _tmpSyncedAt: Long?
          if (_stmt.isNull(_columnIndexOfSyncedAt)) {
            _tmpSyncedAt = null
          } else {
            _tmpSyncedAt = _stmt.getLong(_columnIndexOfSyncedAt)
          }
          val _tmpMergedAt: Long?
          if (_stmt.isNull(_columnIndexOfMergedAt)) {
            _tmpMergedAt = null
          } else {
            _tmpMergedAt = _stmt.getLong(_columnIndexOfMergedAt)
          }
          val _tmpMergeCount: Int
          _tmpMergeCount = _stmt.getLong(_columnIndexOfMergeCount).toInt()
          _item =
              TransferEntity(_tmpTransferId,_tmpProductId,_tmpFromUserId,_tmpToUserId,_tmpOrderId,_tmpAmount,_tmpCurrency,_tmpType,_tmpStatus,_tmpTransactionReference,_tmpNotes,_tmpGpsLat,_tmpGpsLng,_tmpSellerPhotoUrl,_tmpBuyerPhotoUrl,_tmpTimeoutAt,_tmpConditionsJson,_tmpTransferCode,_tmpLineageSnapshotJson,_tmpClaimedAt,_tmpTransferType,_tmpGrowthSnapshotJson,_tmpHealthSnapshotJson,_tmpTransferCodeExpiresAt,_tmpInitiatedAt,_tmpCompletedAt,_tmpUpdatedAt,_tmpLastModifiedAt,_tmpIsDeleted,_tmpDeletedAt,_tmpDirty,_tmpSyncedAt,_tmpMergedAt,_tmpMergeCount)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getUserTransfersBetween(
    userId: String,
    start: Long?,
    end: Long?,
  ): Flow<List<TransferEntity>> {
    val _sql: String =
        "SELECT * FROM transfers WHERE (fromUserId = ? OR toUserId = ?) AND (? IS NULL OR initiatedAt >= ?) AND (? IS NULL OR initiatedAt <= ?) ORDER BY initiatedAt DESC"
    return createFlow(__db, false, arrayOf("transfers")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, userId)
        _argIndex = 2
        _stmt.bindText(_argIndex, userId)
        _argIndex = 3
        if (start == null) {
          _stmt.bindNull(_argIndex)
        } else {
          _stmt.bindLong(_argIndex, start)
        }
        _argIndex = 4
        if (start == null) {
          _stmt.bindNull(_argIndex)
        } else {
          _stmt.bindLong(_argIndex, start)
        }
        _argIndex = 5
        if (end == null) {
          _stmt.bindNull(_argIndex)
        } else {
          _stmt.bindLong(_argIndex, end)
        }
        _argIndex = 6
        if (end == null) {
          _stmt.bindNull(_argIndex)
        } else {
          _stmt.bindLong(_argIndex, end)
        }
        val _columnIndexOfTransferId: Int = getColumnIndexOrThrow(_stmt, "transferId")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfFromUserId: Int = getColumnIndexOrThrow(_stmt, "fromUserId")
        val _columnIndexOfToUserId: Int = getColumnIndexOrThrow(_stmt, "toUserId")
        val _columnIndexOfOrderId: Int = getColumnIndexOrThrow(_stmt, "orderId")
        val _columnIndexOfAmount: Int = getColumnIndexOrThrow(_stmt, "amount")
        val _columnIndexOfCurrency: Int = getColumnIndexOrThrow(_stmt, "currency")
        val _columnIndexOfType: Int = getColumnIndexOrThrow(_stmt, "type")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfTransactionReference: Int = getColumnIndexOrThrow(_stmt,
            "transactionReference")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfGpsLat: Int = getColumnIndexOrThrow(_stmt, "gpsLat")
        val _columnIndexOfGpsLng: Int = getColumnIndexOrThrow(_stmt, "gpsLng")
        val _columnIndexOfSellerPhotoUrl: Int = getColumnIndexOrThrow(_stmt, "sellerPhotoUrl")
        val _columnIndexOfBuyerPhotoUrl: Int = getColumnIndexOrThrow(_stmt, "buyerPhotoUrl")
        val _columnIndexOfTimeoutAt: Int = getColumnIndexOrThrow(_stmt, "timeoutAt")
        val _columnIndexOfConditionsJson: Int = getColumnIndexOrThrow(_stmt, "conditionsJson")
        val _columnIndexOfTransferCode: Int = getColumnIndexOrThrow(_stmt, "transferCode")
        val _columnIndexOfLineageSnapshotJson: Int = getColumnIndexOrThrow(_stmt,
            "lineageSnapshotJson")
        val _columnIndexOfClaimedAt: Int = getColumnIndexOrThrow(_stmt, "claimedAt")
        val _columnIndexOfTransferType: Int = getColumnIndexOrThrow(_stmt, "transferType")
        val _columnIndexOfGrowthSnapshotJson: Int = getColumnIndexOrThrow(_stmt,
            "growthSnapshotJson")
        val _columnIndexOfHealthSnapshotJson: Int = getColumnIndexOrThrow(_stmt,
            "healthSnapshotJson")
        val _columnIndexOfTransferCodeExpiresAt: Int = getColumnIndexOrThrow(_stmt,
            "transferCodeExpiresAt")
        val _columnIndexOfInitiatedAt: Int = getColumnIndexOrThrow(_stmt, "initiatedAt")
        val _columnIndexOfCompletedAt: Int = getColumnIndexOrThrow(_stmt, "completedAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfLastModifiedAt: Int = getColumnIndexOrThrow(_stmt, "lastModifiedAt")
        val _columnIndexOfIsDeleted: Int = getColumnIndexOrThrow(_stmt, "isDeleted")
        val _columnIndexOfDeletedAt: Int = getColumnIndexOrThrow(_stmt, "deletedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _columnIndexOfMergedAt: Int = getColumnIndexOrThrow(_stmt, "mergedAt")
        val _columnIndexOfMergeCount: Int = getColumnIndexOrThrow(_stmt, "mergeCount")
        val _result: MutableList<TransferEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: TransferEntity
          val _tmpTransferId: String
          _tmpTransferId = _stmt.getText(_columnIndexOfTransferId)
          val _tmpProductId: String?
          if (_stmt.isNull(_columnIndexOfProductId)) {
            _tmpProductId = null
          } else {
            _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          }
          val _tmpFromUserId: String?
          if (_stmt.isNull(_columnIndexOfFromUserId)) {
            _tmpFromUserId = null
          } else {
            _tmpFromUserId = _stmt.getText(_columnIndexOfFromUserId)
          }
          val _tmpToUserId: String?
          if (_stmt.isNull(_columnIndexOfToUserId)) {
            _tmpToUserId = null
          } else {
            _tmpToUserId = _stmt.getText(_columnIndexOfToUserId)
          }
          val _tmpOrderId: String?
          if (_stmt.isNull(_columnIndexOfOrderId)) {
            _tmpOrderId = null
          } else {
            _tmpOrderId = _stmt.getText(_columnIndexOfOrderId)
          }
          val _tmpAmount: Double
          _tmpAmount = _stmt.getDouble(_columnIndexOfAmount)
          val _tmpCurrency: String
          _tmpCurrency = _stmt.getText(_columnIndexOfCurrency)
          val _tmpType: String
          _tmpType = _stmt.getText(_columnIndexOfType)
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpTransactionReference: String?
          if (_stmt.isNull(_columnIndexOfTransactionReference)) {
            _tmpTransactionReference = null
          } else {
            _tmpTransactionReference = _stmt.getText(_columnIndexOfTransactionReference)
          }
          val _tmpNotes: String?
          if (_stmt.isNull(_columnIndexOfNotes)) {
            _tmpNotes = null
          } else {
            _tmpNotes = _stmt.getText(_columnIndexOfNotes)
          }
          val _tmpGpsLat: Double?
          if (_stmt.isNull(_columnIndexOfGpsLat)) {
            _tmpGpsLat = null
          } else {
            _tmpGpsLat = _stmt.getDouble(_columnIndexOfGpsLat)
          }
          val _tmpGpsLng: Double?
          if (_stmt.isNull(_columnIndexOfGpsLng)) {
            _tmpGpsLng = null
          } else {
            _tmpGpsLng = _stmt.getDouble(_columnIndexOfGpsLng)
          }
          val _tmpSellerPhotoUrl: String?
          if (_stmt.isNull(_columnIndexOfSellerPhotoUrl)) {
            _tmpSellerPhotoUrl = null
          } else {
            _tmpSellerPhotoUrl = _stmt.getText(_columnIndexOfSellerPhotoUrl)
          }
          val _tmpBuyerPhotoUrl: String?
          if (_stmt.isNull(_columnIndexOfBuyerPhotoUrl)) {
            _tmpBuyerPhotoUrl = null
          } else {
            _tmpBuyerPhotoUrl = _stmt.getText(_columnIndexOfBuyerPhotoUrl)
          }
          val _tmpTimeoutAt: Long?
          if (_stmt.isNull(_columnIndexOfTimeoutAt)) {
            _tmpTimeoutAt = null
          } else {
            _tmpTimeoutAt = _stmt.getLong(_columnIndexOfTimeoutAt)
          }
          val _tmpConditionsJson: String?
          if (_stmt.isNull(_columnIndexOfConditionsJson)) {
            _tmpConditionsJson = null
          } else {
            _tmpConditionsJson = _stmt.getText(_columnIndexOfConditionsJson)
          }
          val _tmpTransferCode: String?
          if (_stmt.isNull(_columnIndexOfTransferCode)) {
            _tmpTransferCode = null
          } else {
            _tmpTransferCode = _stmt.getText(_columnIndexOfTransferCode)
          }
          val _tmpLineageSnapshotJson: String?
          if (_stmt.isNull(_columnIndexOfLineageSnapshotJson)) {
            _tmpLineageSnapshotJson = null
          } else {
            _tmpLineageSnapshotJson = _stmt.getText(_columnIndexOfLineageSnapshotJson)
          }
          val _tmpClaimedAt: Long?
          if (_stmt.isNull(_columnIndexOfClaimedAt)) {
            _tmpClaimedAt = null
          } else {
            _tmpClaimedAt = _stmt.getLong(_columnIndexOfClaimedAt)
          }
          val _tmpTransferType: String
          _tmpTransferType = _stmt.getText(_columnIndexOfTransferType)
          val _tmpGrowthSnapshotJson: String?
          if (_stmt.isNull(_columnIndexOfGrowthSnapshotJson)) {
            _tmpGrowthSnapshotJson = null
          } else {
            _tmpGrowthSnapshotJson = _stmt.getText(_columnIndexOfGrowthSnapshotJson)
          }
          val _tmpHealthSnapshotJson: String?
          if (_stmt.isNull(_columnIndexOfHealthSnapshotJson)) {
            _tmpHealthSnapshotJson = null
          } else {
            _tmpHealthSnapshotJson = _stmt.getText(_columnIndexOfHealthSnapshotJson)
          }
          val _tmpTransferCodeExpiresAt: Long?
          if (_stmt.isNull(_columnIndexOfTransferCodeExpiresAt)) {
            _tmpTransferCodeExpiresAt = null
          } else {
            _tmpTransferCodeExpiresAt = _stmt.getLong(_columnIndexOfTransferCodeExpiresAt)
          }
          val _tmpInitiatedAt: Long
          _tmpInitiatedAt = _stmt.getLong(_columnIndexOfInitiatedAt)
          val _tmpCompletedAt: Long?
          if (_stmt.isNull(_columnIndexOfCompletedAt)) {
            _tmpCompletedAt = null
          } else {
            _tmpCompletedAt = _stmt.getLong(_columnIndexOfCompletedAt)
          }
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpLastModifiedAt: Long
          _tmpLastModifiedAt = _stmt.getLong(_columnIndexOfLastModifiedAt)
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
          val _tmpSyncedAt: Long?
          if (_stmt.isNull(_columnIndexOfSyncedAt)) {
            _tmpSyncedAt = null
          } else {
            _tmpSyncedAt = _stmt.getLong(_columnIndexOfSyncedAt)
          }
          val _tmpMergedAt: Long?
          if (_stmt.isNull(_columnIndexOfMergedAt)) {
            _tmpMergedAt = null
          } else {
            _tmpMergedAt = _stmt.getLong(_columnIndexOfMergedAt)
          }
          val _tmpMergeCount: Int
          _tmpMergeCount = _stmt.getLong(_columnIndexOfMergeCount).toInt()
          _item =
              TransferEntity(_tmpTransferId,_tmpProductId,_tmpFromUserId,_tmpToUserId,_tmpOrderId,_tmpAmount,_tmpCurrency,_tmpType,_tmpStatus,_tmpTransactionReference,_tmpNotes,_tmpGpsLat,_tmpGpsLng,_tmpSellerPhotoUrl,_tmpBuyerPhotoUrl,_tmpTimeoutAt,_tmpConditionsJson,_tmpTransferCode,_tmpLineageSnapshotJson,_tmpClaimedAt,_tmpTransferType,_tmpGrowthSnapshotJson,_tmpHealthSnapshotJson,_tmpTransferCodeExpiresAt,_tmpInitiatedAt,_tmpCompletedAt,_tmpUpdatedAt,_tmpLastModifiedAt,_tmpIsDeleted,_tmpDeletedAt,_tmpDirty,_tmpSyncedAt,_tmpMergedAt,_tmpMergeCount)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getAllTransfersBetween(start: Long, end: Long): Flow<List<TransferEntity>> {
    val _sql: String =
        "SELECT * FROM transfers WHERE initiatedAt >= ? AND initiatedAt <= ? AND isDeleted = 0 ORDER BY initiatedAt DESC"
    return createFlow(__db, false, arrayOf("transfers")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, start)
        _argIndex = 2
        _stmt.bindLong(_argIndex, end)
        val _columnIndexOfTransferId: Int = getColumnIndexOrThrow(_stmt, "transferId")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfFromUserId: Int = getColumnIndexOrThrow(_stmt, "fromUserId")
        val _columnIndexOfToUserId: Int = getColumnIndexOrThrow(_stmt, "toUserId")
        val _columnIndexOfOrderId: Int = getColumnIndexOrThrow(_stmt, "orderId")
        val _columnIndexOfAmount: Int = getColumnIndexOrThrow(_stmt, "amount")
        val _columnIndexOfCurrency: Int = getColumnIndexOrThrow(_stmt, "currency")
        val _columnIndexOfType: Int = getColumnIndexOrThrow(_stmt, "type")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfTransactionReference: Int = getColumnIndexOrThrow(_stmt,
            "transactionReference")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfGpsLat: Int = getColumnIndexOrThrow(_stmt, "gpsLat")
        val _columnIndexOfGpsLng: Int = getColumnIndexOrThrow(_stmt, "gpsLng")
        val _columnIndexOfSellerPhotoUrl: Int = getColumnIndexOrThrow(_stmt, "sellerPhotoUrl")
        val _columnIndexOfBuyerPhotoUrl: Int = getColumnIndexOrThrow(_stmt, "buyerPhotoUrl")
        val _columnIndexOfTimeoutAt: Int = getColumnIndexOrThrow(_stmt, "timeoutAt")
        val _columnIndexOfConditionsJson: Int = getColumnIndexOrThrow(_stmt, "conditionsJson")
        val _columnIndexOfTransferCode: Int = getColumnIndexOrThrow(_stmt, "transferCode")
        val _columnIndexOfLineageSnapshotJson: Int = getColumnIndexOrThrow(_stmt,
            "lineageSnapshotJson")
        val _columnIndexOfClaimedAt: Int = getColumnIndexOrThrow(_stmt, "claimedAt")
        val _columnIndexOfTransferType: Int = getColumnIndexOrThrow(_stmt, "transferType")
        val _columnIndexOfGrowthSnapshotJson: Int = getColumnIndexOrThrow(_stmt,
            "growthSnapshotJson")
        val _columnIndexOfHealthSnapshotJson: Int = getColumnIndexOrThrow(_stmt,
            "healthSnapshotJson")
        val _columnIndexOfTransferCodeExpiresAt: Int = getColumnIndexOrThrow(_stmt,
            "transferCodeExpiresAt")
        val _columnIndexOfInitiatedAt: Int = getColumnIndexOrThrow(_stmt, "initiatedAt")
        val _columnIndexOfCompletedAt: Int = getColumnIndexOrThrow(_stmt, "completedAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfLastModifiedAt: Int = getColumnIndexOrThrow(_stmt, "lastModifiedAt")
        val _columnIndexOfIsDeleted: Int = getColumnIndexOrThrow(_stmt, "isDeleted")
        val _columnIndexOfDeletedAt: Int = getColumnIndexOrThrow(_stmt, "deletedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _columnIndexOfMergedAt: Int = getColumnIndexOrThrow(_stmt, "mergedAt")
        val _columnIndexOfMergeCount: Int = getColumnIndexOrThrow(_stmt, "mergeCount")
        val _result: MutableList<TransferEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: TransferEntity
          val _tmpTransferId: String
          _tmpTransferId = _stmt.getText(_columnIndexOfTransferId)
          val _tmpProductId: String?
          if (_stmt.isNull(_columnIndexOfProductId)) {
            _tmpProductId = null
          } else {
            _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          }
          val _tmpFromUserId: String?
          if (_stmt.isNull(_columnIndexOfFromUserId)) {
            _tmpFromUserId = null
          } else {
            _tmpFromUserId = _stmt.getText(_columnIndexOfFromUserId)
          }
          val _tmpToUserId: String?
          if (_stmt.isNull(_columnIndexOfToUserId)) {
            _tmpToUserId = null
          } else {
            _tmpToUserId = _stmt.getText(_columnIndexOfToUserId)
          }
          val _tmpOrderId: String?
          if (_stmt.isNull(_columnIndexOfOrderId)) {
            _tmpOrderId = null
          } else {
            _tmpOrderId = _stmt.getText(_columnIndexOfOrderId)
          }
          val _tmpAmount: Double
          _tmpAmount = _stmt.getDouble(_columnIndexOfAmount)
          val _tmpCurrency: String
          _tmpCurrency = _stmt.getText(_columnIndexOfCurrency)
          val _tmpType: String
          _tmpType = _stmt.getText(_columnIndexOfType)
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpTransactionReference: String?
          if (_stmt.isNull(_columnIndexOfTransactionReference)) {
            _tmpTransactionReference = null
          } else {
            _tmpTransactionReference = _stmt.getText(_columnIndexOfTransactionReference)
          }
          val _tmpNotes: String?
          if (_stmt.isNull(_columnIndexOfNotes)) {
            _tmpNotes = null
          } else {
            _tmpNotes = _stmt.getText(_columnIndexOfNotes)
          }
          val _tmpGpsLat: Double?
          if (_stmt.isNull(_columnIndexOfGpsLat)) {
            _tmpGpsLat = null
          } else {
            _tmpGpsLat = _stmt.getDouble(_columnIndexOfGpsLat)
          }
          val _tmpGpsLng: Double?
          if (_stmt.isNull(_columnIndexOfGpsLng)) {
            _tmpGpsLng = null
          } else {
            _tmpGpsLng = _stmt.getDouble(_columnIndexOfGpsLng)
          }
          val _tmpSellerPhotoUrl: String?
          if (_stmt.isNull(_columnIndexOfSellerPhotoUrl)) {
            _tmpSellerPhotoUrl = null
          } else {
            _tmpSellerPhotoUrl = _stmt.getText(_columnIndexOfSellerPhotoUrl)
          }
          val _tmpBuyerPhotoUrl: String?
          if (_stmt.isNull(_columnIndexOfBuyerPhotoUrl)) {
            _tmpBuyerPhotoUrl = null
          } else {
            _tmpBuyerPhotoUrl = _stmt.getText(_columnIndexOfBuyerPhotoUrl)
          }
          val _tmpTimeoutAt: Long?
          if (_stmt.isNull(_columnIndexOfTimeoutAt)) {
            _tmpTimeoutAt = null
          } else {
            _tmpTimeoutAt = _stmt.getLong(_columnIndexOfTimeoutAt)
          }
          val _tmpConditionsJson: String?
          if (_stmt.isNull(_columnIndexOfConditionsJson)) {
            _tmpConditionsJson = null
          } else {
            _tmpConditionsJson = _stmt.getText(_columnIndexOfConditionsJson)
          }
          val _tmpTransferCode: String?
          if (_stmt.isNull(_columnIndexOfTransferCode)) {
            _tmpTransferCode = null
          } else {
            _tmpTransferCode = _stmt.getText(_columnIndexOfTransferCode)
          }
          val _tmpLineageSnapshotJson: String?
          if (_stmt.isNull(_columnIndexOfLineageSnapshotJson)) {
            _tmpLineageSnapshotJson = null
          } else {
            _tmpLineageSnapshotJson = _stmt.getText(_columnIndexOfLineageSnapshotJson)
          }
          val _tmpClaimedAt: Long?
          if (_stmt.isNull(_columnIndexOfClaimedAt)) {
            _tmpClaimedAt = null
          } else {
            _tmpClaimedAt = _stmt.getLong(_columnIndexOfClaimedAt)
          }
          val _tmpTransferType: String
          _tmpTransferType = _stmt.getText(_columnIndexOfTransferType)
          val _tmpGrowthSnapshotJson: String?
          if (_stmt.isNull(_columnIndexOfGrowthSnapshotJson)) {
            _tmpGrowthSnapshotJson = null
          } else {
            _tmpGrowthSnapshotJson = _stmt.getText(_columnIndexOfGrowthSnapshotJson)
          }
          val _tmpHealthSnapshotJson: String?
          if (_stmt.isNull(_columnIndexOfHealthSnapshotJson)) {
            _tmpHealthSnapshotJson = null
          } else {
            _tmpHealthSnapshotJson = _stmt.getText(_columnIndexOfHealthSnapshotJson)
          }
          val _tmpTransferCodeExpiresAt: Long?
          if (_stmt.isNull(_columnIndexOfTransferCodeExpiresAt)) {
            _tmpTransferCodeExpiresAt = null
          } else {
            _tmpTransferCodeExpiresAt = _stmt.getLong(_columnIndexOfTransferCodeExpiresAt)
          }
          val _tmpInitiatedAt: Long
          _tmpInitiatedAt = _stmt.getLong(_columnIndexOfInitiatedAt)
          val _tmpCompletedAt: Long?
          if (_stmt.isNull(_columnIndexOfCompletedAt)) {
            _tmpCompletedAt = null
          } else {
            _tmpCompletedAt = _stmt.getLong(_columnIndexOfCompletedAt)
          }
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpLastModifiedAt: Long
          _tmpLastModifiedAt = _stmt.getLong(_columnIndexOfLastModifiedAt)
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
          val _tmpSyncedAt: Long?
          if (_stmt.isNull(_columnIndexOfSyncedAt)) {
            _tmpSyncedAt = null
          } else {
            _tmpSyncedAt = _stmt.getLong(_columnIndexOfSyncedAt)
          }
          val _tmpMergedAt: Long?
          if (_stmt.isNull(_columnIndexOfMergedAt)) {
            _tmpMergedAt = null
          } else {
            _tmpMergedAt = _stmt.getLong(_columnIndexOfMergedAt)
          }
          val _tmpMergeCount: Int
          _tmpMergeCount = _stmt.getLong(_columnIndexOfMergeCount).toInt()
          _item =
              TransferEntity(_tmpTransferId,_tmpProductId,_tmpFromUserId,_tmpToUserId,_tmpOrderId,_tmpAmount,_tmpCurrency,_tmpType,_tmpStatus,_tmpTransactionReference,_tmpNotes,_tmpGpsLat,_tmpGpsLng,_tmpSellerPhotoUrl,_tmpBuyerPhotoUrl,_tmpTimeoutAt,_tmpConditionsJson,_tmpTransferCode,_tmpLineageSnapshotJson,_tmpClaimedAt,_tmpTransferType,_tmpGrowthSnapshotJson,_tmpHealthSnapshotJson,_tmpTransferCodeExpiresAt,_tmpInitiatedAt,_tmpCompletedAt,_tmpUpdatedAt,_tmpLastModifiedAt,_tmpIsDeleted,_tmpDeletedAt,_tmpDirty,_tmpSyncedAt,_tmpMergedAt,_tmpMergeCount)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getUserTransfersByTypeBetween(
    userId: String,
    type: String,
    start: Long?,
    end: Long?,
  ): Flow<List<TransferEntity>> {
    val _sql: String =
        "SELECT * FROM transfers WHERE (fromUserId = ? OR toUserId = ?) AND type = ? AND (? IS NULL OR initiatedAt >= ?) AND (? IS NULL OR initiatedAt <= ?) ORDER BY initiatedAt DESC"
    return createFlow(__db, false, arrayOf("transfers")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, userId)
        _argIndex = 2
        _stmt.bindText(_argIndex, userId)
        _argIndex = 3
        _stmt.bindText(_argIndex, type)
        _argIndex = 4
        if (start == null) {
          _stmt.bindNull(_argIndex)
        } else {
          _stmt.bindLong(_argIndex, start)
        }
        _argIndex = 5
        if (start == null) {
          _stmt.bindNull(_argIndex)
        } else {
          _stmt.bindLong(_argIndex, start)
        }
        _argIndex = 6
        if (end == null) {
          _stmt.bindNull(_argIndex)
        } else {
          _stmt.bindLong(_argIndex, end)
        }
        _argIndex = 7
        if (end == null) {
          _stmt.bindNull(_argIndex)
        } else {
          _stmt.bindLong(_argIndex, end)
        }
        val _columnIndexOfTransferId: Int = getColumnIndexOrThrow(_stmt, "transferId")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfFromUserId: Int = getColumnIndexOrThrow(_stmt, "fromUserId")
        val _columnIndexOfToUserId: Int = getColumnIndexOrThrow(_stmt, "toUserId")
        val _columnIndexOfOrderId: Int = getColumnIndexOrThrow(_stmt, "orderId")
        val _columnIndexOfAmount: Int = getColumnIndexOrThrow(_stmt, "amount")
        val _columnIndexOfCurrency: Int = getColumnIndexOrThrow(_stmt, "currency")
        val _columnIndexOfType: Int = getColumnIndexOrThrow(_stmt, "type")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfTransactionReference: Int = getColumnIndexOrThrow(_stmt,
            "transactionReference")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfGpsLat: Int = getColumnIndexOrThrow(_stmt, "gpsLat")
        val _columnIndexOfGpsLng: Int = getColumnIndexOrThrow(_stmt, "gpsLng")
        val _columnIndexOfSellerPhotoUrl: Int = getColumnIndexOrThrow(_stmt, "sellerPhotoUrl")
        val _columnIndexOfBuyerPhotoUrl: Int = getColumnIndexOrThrow(_stmt, "buyerPhotoUrl")
        val _columnIndexOfTimeoutAt: Int = getColumnIndexOrThrow(_stmt, "timeoutAt")
        val _columnIndexOfConditionsJson: Int = getColumnIndexOrThrow(_stmt, "conditionsJson")
        val _columnIndexOfTransferCode: Int = getColumnIndexOrThrow(_stmt, "transferCode")
        val _columnIndexOfLineageSnapshotJson: Int = getColumnIndexOrThrow(_stmt,
            "lineageSnapshotJson")
        val _columnIndexOfClaimedAt: Int = getColumnIndexOrThrow(_stmt, "claimedAt")
        val _columnIndexOfTransferType: Int = getColumnIndexOrThrow(_stmt, "transferType")
        val _columnIndexOfGrowthSnapshotJson: Int = getColumnIndexOrThrow(_stmt,
            "growthSnapshotJson")
        val _columnIndexOfHealthSnapshotJson: Int = getColumnIndexOrThrow(_stmt,
            "healthSnapshotJson")
        val _columnIndexOfTransferCodeExpiresAt: Int = getColumnIndexOrThrow(_stmt,
            "transferCodeExpiresAt")
        val _columnIndexOfInitiatedAt: Int = getColumnIndexOrThrow(_stmt, "initiatedAt")
        val _columnIndexOfCompletedAt: Int = getColumnIndexOrThrow(_stmt, "completedAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfLastModifiedAt: Int = getColumnIndexOrThrow(_stmt, "lastModifiedAt")
        val _columnIndexOfIsDeleted: Int = getColumnIndexOrThrow(_stmt, "isDeleted")
        val _columnIndexOfDeletedAt: Int = getColumnIndexOrThrow(_stmt, "deletedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _columnIndexOfMergedAt: Int = getColumnIndexOrThrow(_stmt, "mergedAt")
        val _columnIndexOfMergeCount: Int = getColumnIndexOrThrow(_stmt, "mergeCount")
        val _result: MutableList<TransferEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: TransferEntity
          val _tmpTransferId: String
          _tmpTransferId = _stmt.getText(_columnIndexOfTransferId)
          val _tmpProductId: String?
          if (_stmt.isNull(_columnIndexOfProductId)) {
            _tmpProductId = null
          } else {
            _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          }
          val _tmpFromUserId: String?
          if (_stmt.isNull(_columnIndexOfFromUserId)) {
            _tmpFromUserId = null
          } else {
            _tmpFromUserId = _stmt.getText(_columnIndexOfFromUserId)
          }
          val _tmpToUserId: String?
          if (_stmt.isNull(_columnIndexOfToUserId)) {
            _tmpToUserId = null
          } else {
            _tmpToUserId = _stmt.getText(_columnIndexOfToUserId)
          }
          val _tmpOrderId: String?
          if (_stmt.isNull(_columnIndexOfOrderId)) {
            _tmpOrderId = null
          } else {
            _tmpOrderId = _stmt.getText(_columnIndexOfOrderId)
          }
          val _tmpAmount: Double
          _tmpAmount = _stmt.getDouble(_columnIndexOfAmount)
          val _tmpCurrency: String
          _tmpCurrency = _stmt.getText(_columnIndexOfCurrency)
          val _tmpType: String
          _tmpType = _stmt.getText(_columnIndexOfType)
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpTransactionReference: String?
          if (_stmt.isNull(_columnIndexOfTransactionReference)) {
            _tmpTransactionReference = null
          } else {
            _tmpTransactionReference = _stmt.getText(_columnIndexOfTransactionReference)
          }
          val _tmpNotes: String?
          if (_stmt.isNull(_columnIndexOfNotes)) {
            _tmpNotes = null
          } else {
            _tmpNotes = _stmt.getText(_columnIndexOfNotes)
          }
          val _tmpGpsLat: Double?
          if (_stmt.isNull(_columnIndexOfGpsLat)) {
            _tmpGpsLat = null
          } else {
            _tmpGpsLat = _stmt.getDouble(_columnIndexOfGpsLat)
          }
          val _tmpGpsLng: Double?
          if (_stmt.isNull(_columnIndexOfGpsLng)) {
            _tmpGpsLng = null
          } else {
            _tmpGpsLng = _stmt.getDouble(_columnIndexOfGpsLng)
          }
          val _tmpSellerPhotoUrl: String?
          if (_stmt.isNull(_columnIndexOfSellerPhotoUrl)) {
            _tmpSellerPhotoUrl = null
          } else {
            _tmpSellerPhotoUrl = _stmt.getText(_columnIndexOfSellerPhotoUrl)
          }
          val _tmpBuyerPhotoUrl: String?
          if (_stmt.isNull(_columnIndexOfBuyerPhotoUrl)) {
            _tmpBuyerPhotoUrl = null
          } else {
            _tmpBuyerPhotoUrl = _stmt.getText(_columnIndexOfBuyerPhotoUrl)
          }
          val _tmpTimeoutAt: Long?
          if (_stmt.isNull(_columnIndexOfTimeoutAt)) {
            _tmpTimeoutAt = null
          } else {
            _tmpTimeoutAt = _stmt.getLong(_columnIndexOfTimeoutAt)
          }
          val _tmpConditionsJson: String?
          if (_stmt.isNull(_columnIndexOfConditionsJson)) {
            _tmpConditionsJson = null
          } else {
            _tmpConditionsJson = _stmt.getText(_columnIndexOfConditionsJson)
          }
          val _tmpTransferCode: String?
          if (_stmt.isNull(_columnIndexOfTransferCode)) {
            _tmpTransferCode = null
          } else {
            _tmpTransferCode = _stmt.getText(_columnIndexOfTransferCode)
          }
          val _tmpLineageSnapshotJson: String?
          if (_stmt.isNull(_columnIndexOfLineageSnapshotJson)) {
            _tmpLineageSnapshotJson = null
          } else {
            _tmpLineageSnapshotJson = _stmt.getText(_columnIndexOfLineageSnapshotJson)
          }
          val _tmpClaimedAt: Long?
          if (_stmt.isNull(_columnIndexOfClaimedAt)) {
            _tmpClaimedAt = null
          } else {
            _tmpClaimedAt = _stmt.getLong(_columnIndexOfClaimedAt)
          }
          val _tmpTransferType: String
          _tmpTransferType = _stmt.getText(_columnIndexOfTransferType)
          val _tmpGrowthSnapshotJson: String?
          if (_stmt.isNull(_columnIndexOfGrowthSnapshotJson)) {
            _tmpGrowthSnapshotJson = null
          } else {
            _tmpGrowthSnapshotJson = _stmt.getText(_columnIndexOfGrowthSnapshotJson)
          }
          val _tmpHealthSnapshotJson: String?
          if (_stmt.isNull(_columnIndexOfHealthSnapshotJson)) {
            _tmpHealthSnapshotJson = null
          } else {
            _tmpHealthSnapshotJson = _stmt.getText(_columnIndexOfHealthSnapshotJson)
          }
          val _tmpTransferCodeExpiresAt: Long?
          if (_stmt.isNull(_columnIndexOfTransferCodeExpiresAt)) {
            _tmpTransferCodeExpiresAt = null
          } else {
            _tmpTransferCodeExpiresAt = _stmt.getLong(_columnIndexOfTransferCodeExpiresAt)
          }
          val _tmpInitiatedAt: Long
          _tmpInitiatedAt = _stmt.getLong(_columnIndexOfInitiatedAt)
          val _tmpCompletedAt: Long?
          if (_stmt.isNull(_columnIndexOfCompletedAt)) {
            _tmpCompletedAt = null
          } else {
            _tmpCompletedAt = _stmt.getLong(_columnIndexOfCompletedAt)
          }
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpLastModifiedAt: Long
          _tmpLastModifiedAt = _stmt.getLong(_columnIndexOfLastModifiedAt)
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
          val _tmpSyncedAt: Long?
          if (_stmt.isNull(_columnIndexOfSyncedAt)) {
            _tmpSyncedAt = null
          } else {
            _tmpSyncedAt = _stmt.getLong(_columnIndexOfSyncedAt)
          }
          val _tmpMergedAt: Long?
          if (_stmt.isNull(_columnIndexOfMergedAt)) {
            _tmpMergedAt = null
          } else {
            _tmpMergedAt = _stmt.getLong(_columnIndexOfMergedAt)
          }
          val _tmpMergeCount: Int
          _tmpMergeCount = _stmt.getLong(_columnIndexOfMergeCount).toInt()
          _item =
              TransferEntity(_tmpTransferId,_tmpProductId,_tmpFromUserId,_tmpToUserId,_tmpOrderId,_tmpAmount,_tmpCurrency,_tmpType,_tmpStatus,_tmpTransactionReference,_tmpNotes,_tmpGpsLat,_tmpGpsLng,_tmpSellerPhotoUrl,_tmpBuyerPhotoUrl,_tmpTimeoutAt,_tmpConditionsJson,_tmpTransferCode,_tmpLineageSnapshotJson,_tmpClaimedAt,_tmpTransferType,_tmpGrowthSnapshotJson,_tmpHealthSnapshotJson,_tmpTransferCodeExpiresAt,_tmpInitiatedAt,_tmpCompletedAt,_tmpUpdatedAt,_tmpLastModifiedAt,_tmpIsDeleted,_tmpDeletedAt,_tmpDirty,_tmpSyncedAt,_tmpMergedAt,_tmpMergeCount)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getUserTransfersByStatusBetween(
    userId: String,
    status: String,
    start: Long?,
    end: Long?,
  ): Flow<List<TransferEntity>> {
    val _sql: String =
        "SELECT * FROM transfers WHERE (fromUserId = ? OR toUserId = ?) AND status = ? AND (? IS NULL OR initiatedAt >= ?) AND (? IS NULL OR initiatedAt <= ?) ORDER BY initiatedAt DESC"
    return createFlow(__db, false, arrayOf("transfers")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, userId)
        _argIndex = 2
        _stmt.bindText(_argIndex, userId)
        _argIndex = 3
        _stmt.bindText(_argIndex, status)
        _argIndex = 4
        if (start == null) {
          _stmt.bindNull(_argIndex)
        } else {
          _stmt.bindLong(_argIndex, start)
        }
        _argIndex = 5
        if (start == null) {
          _stmt.bindNull(_argIndex)
        } else {
          _stmt.bindLong(_argIndex, start)
        }
        _argIndex = 6
        if (end == null) {
          _stmt.bindNull(_argIndex)
        } else {
          _stmt.bindLong(_argIndex, end)
        }
        _argIndex = 7
        if (end == null) {
          _stmt.bindNull(_argIndex)
        } else {
          _stmt.bindLong(_argIndex, end)
        }
        val _columnIndexOfTransferId: Int = getColumnIndexOrThrow(_stmt, "transferId")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfFromUserId: Int = getColumnIndexOrThrow(_stmt, "fromUserId")
        val _columnIndexOfToUserId: Int = getColumnIndexOrThrow(_stmt, "toUserId")
        val _columnIndexOfOrderId: Int = getColumnIndexOrThrow(_stmt, "orderId")
        val _columnIndexOfAmount: Int = getColumnIndexOrThrow(_stmt, "amount")
        val _columnIndexOfCurrency: Int = getColumnIndexOrThrow(_stmt, "currency")
        val _columnIndexOfType: Int = getColumnIndexOrThrow(_stmt, "type")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfTransactionReference: Int = getColumnIndexOrThrow(_stmt,
            "transactionReference")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfGpsLat: Int = getColumnIndexOrThrow(_stmt, "gpsLat")
        val _columnIndexOfGpsLng: Int = getColumnIndexOrThrow(_stmt, "gpsLng")
        val _columnIndexOfSellerPhotoUrl: Int = getColumnIndexOrThrow(_stmt, "sellerPhotoUrl")
        val _columnIndexOfBuyerPhotoUrl: Int = getColumnIndexOrThrow(_stmt, "buyerPhotoUrl")
        val _columnIndexOfTimeoutAt: Int = getColumnIndexOrThrow(_stmt, "timeoutAt")
        val _columnIndexOfConditionsJson: Int = getColumnIndexOrThrow(_stmt, "conditionsJson")
        val _columnIndexOfTransferCode: Int = getColumnIndexOrThrow(_stmt, "transferCode")
        val _columnIndexOfLineageSnapshotJson: Int = getColumnIndexOrThrow(_stmt,
            "lineageSnapshotJson")
        val _columnIndexOfClaimedAt: Int = getColumnIndexOrThrow(_stmt, "claimedAt")
        val _columnIndexOfTransferType: Int = getColumnIndexOrThrow(_stmt, "transferType")
        val _columnIndexOfGrowthSnapshotJson: Int = getColumnIndexOrThrow(_stmt,
            "growthSnapshotJson")
        val _columnIndexOfHealthSnapshotJson: Int = getColumnIndexOrThrow(_stmt,
            "healthSnapshotJson")
        val _columnIndexOfTransferCodeExpiresAt: Int = getColumnIndexOrThrow(_stmt,
            "transferCodeExpiresAt")
        val _columnIndexOfInitiatedAt: Int = getColumnIndexOrThrow(_stmt, "initiatedAt")
        val _columnIndexOfCompletedAt: Int = getColumnIndexOrThrow(_stmt, "completedAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfLastModifiedAt: Int = getColumnIndexOrThrow(_stmt, "lastModifiedAt")
        val _columnIndexOfIsDeleted: Int = getColumnIndexOrThrow(_stmt, "isDeleted")
        val _columnIndexOfDeletedAt: Int = getColumnIndexOrThrow(_stmt, "deletedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _columnIndexOfMergedAt: Int = getColumnIndexOrThrow(_stmt, "mergedAt")
        val _columnIndexOfMergeCount: Int = getColumnIndexOrThrow(_stmt, "mergeCount")
        val _result: MutableList<TransferEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: TransferEntity
          val _tmpTransferId: String
          _tmpTransferId = _stmt.getText(_columnIndexOfTransferId)
          val _tmpProductId: String?
          if (_stmt.isNull(_columnIndexOfProductId)) {
            _tmpProductId = null
          } else {
            _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          }
          val _tmpFromUserId: String?
          if (_stmt.isNull(_columnIndexOfFromUserId)) {
            _tmpFromUserId = null
          } else {
            _tmpFromUserId = _stmt.getText(_columnIndexOfFromUserId)
          }
          val _tmpToUserId: String?
          if (_stmt.isNull(_columnIndexOfToUserId)) {
            _tmpToUserId = null
          } else {
            _tmpToUserId = _stmt.getText(_columnIndexOfToUserId)
          }
          val _tmpOrderId: String?
          if (_stmt.isNull(_columnIndexOfOrderId)) {
            _tmpOrderId = null
          } else {
            _tmpOrderId = _stmt.getText(_columnIndexOfOrderId)
          }
          val _tmpAmount: Double
          _tmpAmount = _stmt.getDouble(_columnIndexOfAmount)
          val _tmpCurrency: String
          _tmpCurrency = _stmt.getText(_columnIndexOfCurrency)
          val _tmpType: String
          _tmpType = _stmt.getText(_columnIndexOfType)
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpTransactionReference: String?
          if (_stmt.isNull(_columnIndexOfTransactionReference)) {
            _tmpTransactionReference = null
          } else {
            _tmpTransactionReference = _stmt.getText(_columnIndexOfTransactionReference)
          }
          val _tmpNotes: String?
          if (_stmt.isNull(_columnIndexOfNotes)) {
            _tmpNotes = null
          } else {
            _tmpNotes = _stmt.getText(_columnIndexOfNotes)
          }
          val _tmpGpsLat: Double?
          if (_stmt.isNull(_columnIndexOfGpsLat)) {
            _tmpGpsLat = null
          } else {
            _tmpGpsLat = _stmt.getDouble(_columnIndexOfGpsLat)
          }
          val _tmpGpsLng: Double?
          if (_stmt.isNull(_columnIndexOfGpsLng)) {
            _tmpGpsLng = null
          } else {
            _tmpGpsLng = _stmt.getDouble(_columnIndexOfGpsLng)
          }
          val _tmpSellerPhotoUrl: String?
          if (_stmt.isNull(_columnIndexOfSellerPhotoUrl)) {
            _tmpSellerPhotoUrl = null
          } else {
            _tmpSellerPhotoUrl = _stmt.getText(_columnIndexOfSellerPhotoUrl)
          }
          val _tmpBuyerPhotoUrl: String?
          if (_stmt.isNull(_columnIndexOfBuyerPhotoUrl)) {
            _tmpBuyerPhotoUrl = null
          } else {
            _tmpBuyerPhotoUrl = _stmt.getText(_columnIndexOfBuyerPhotoUrl)
          }
          val _tmpTimeoutAt: Long?
          if (_stmt.isNull(_columnIndexOfTimeoutAt)) {
            _tmpTimeoutAt = null
          } else {
            _tmpTimeoutAt = _stmt.getLong(_columnIndexOfTimeoutAt)
          }
          val _tmpConditionsJson: String?
          if (_stmt.isNull(_columnIndexOfConditionsJson)) {
            _tmpConditionsJson = null
          } else {
            _tmpConditionsJson = _stmt.getText(_columnIndexOfConditionsJson)
          }
          val _tmpTransferCode: String?
          if (_stmt.isNull(_columnIndexOfTransferCode)) {
            _tmpTransferCode = null
          } else {
            _tmpTransferCode = _stmt.getText(_columnIndexOfTransferCode)
          }
          val _tmpLineageSnapshotJson: String?
          if (_stmt.isNull(_columnIndexOfLineageSnapshotJson)) {
            _tmpLineageSnapshotJson = null
          } else {
            _tmpLineageSnapshotJson = _stmt.getText(_columnIndexOfLineageSnapshotJson)
          }
          val _tmpClaimedAt: Long?
          if (_stmt.isNull(_columnIndexOfClaimedAt)) {
            _tmpClaimedAt = null
          } else {
            _tmpClaimedAt = _stmt.getLong(_columnIndexOfClaimedAt)
          }
          val _tmpTransferType: String
          _tmpTransferType = _stmt.getText(_columnIndexOfTransferType)
          val _tmpGrowthSnapshotJson: String?
          if (_stmt.isNull(_columnIndexOfGrowthSnapshotJson)) {
            _tmpGrowthSnapshotJson = null
          } else {
            _tmpGrowthSnapshotJson = _stmt.getText(_columnIndexOfGrowthSnapshotJson)
          }
          val _tmpHealthSnapshotJson: String?
          if (_stmt.isNull(_columnIndexOfHealthSnapshotJson)) {
            _tmpHealthSnapshotJson = null
          } else {
            _tmpHealthSnapshotJson = _stmt.getText(_columnIndexOfHealthSnapshotJson)
          }
          val _tmpTransferCodeExpiresAt: Long?
          if (_stmt.isNull(_columnIndexOfTransferCodeExpiresAt)) {
            _tmpTransferCodeExpiresAt = null
          } else {
            _tmpTransferCodeExpiresAt = _stmt.getLong(_columnIndexOfTransferCodeExpiresAt)
          }
          val _tmpInitiatedAt: Long
          _tmpInitiatedAt = _stmt.getLong(_columnIndexOfInitiatedAt)
          val _tmpCompletedAt: Long?
          if (_stmt.isNull(_columnIndexOfCompletedAt)) {
            _tmpCompletedAt = null
          } else {
            _tmpCompletedAt = _stmt.getLong(_columnIndexOfCompletedAt)
          }
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpLastModifiedAt: Long
          _tmpLastModifiedAt = _stmt.getLong(_columnIndexOfLastModifiedAt)
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
          val _tmpSyncedAt: Long?
          if (_stmt.isNull(_columnIndexOfSyncedAt)) {
            _tmpSyncedAt = null
          } else {
            _tmpSyncedAt = _stmt.getLong(_columnIndexOfSyncedAt)
          }
          val _tmpMergedAt: Long?
          if (_stmt.isNull(_columnIndexOfMergedAt)) {
            _tmpMergedAt = null
          } else {
            _tmpMergedAt = _stmt.getLong(_columnIndexOfMergedAt)
          }
          val _tmpMergeCount: Int
          _tmpMergeCount = _stmt.getLong(_columnIndexOfMergeCount).toInt()
          _item =
              TransferEntity(_tmpTransferId,_tmpProductId,_tmpFromUserId,_tmpToUserId,_tmpOrderId,_tmpAmount,_tmpCurrency,_tmpType,_tmpStatus,_tmpTransactionReference,_tmpNotes,_tmpGpsLat,_tmpGpsLng,_tmpSellerPhotoUrl,_tmpBuyerPhotoUrl,_tmpTimeoutAt,_tmpConditionsJson,_tmpTransferCode,_tmpLineageSnapshotJson,_tmpClaimedAt,_tmpTransferType,_tmpGrowthSnapshotJson,_tmpHealthSnapshotJson,_tmpTransferCodeExpiresAt,_tmpInitiatedAt,_tmpCompletedAt,_tmpUpdatedAt,_tmpLastModifiedAt,_tmpIsDeleted,_tmpDeletedAt,_tmpDirty,_tmpSyncedAt,_tmpMergedAt,_tmpMergeCount)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun pagingHistory(
    userId: String,
    type: String?,
    status: String?,
    start: Long?,
    end: Long?,
  ): PagingSource<Int, TransferEntity> {
    val _sql: String =
        "SELECT * FROM transfers WHERE (fromUserId = ? OR toUserId = ?) AND status != 'PENDING' AND (? IS NULL OR type = ?) AND (? IS NULL OR status = ?) AND (? IS NULL OR initiatedAt >= ?) AND (? IS NULL OR initiatedAt <= ?) ORDER BY initiatedAt DESC"
    val _rawQuery: RoomRawQuery = RoomRawQuery(_sql) { _stmt ->
      var _argIndex: Int = 1
      _stmt.bindText(_argIndex, userId)
      _argIndex = 2
      _stmt.bindText(_argIndex, userId)
      _argIndex = 3
      if (type == null) {
        _stmt.bindNull(_argIndex)
      } else {
        _stmt.bindText(_argIndex, type)
      }
      _argIndex = 4
      if (type == null) {
        _stmt.bindNull(_argIndex)
      } else {
        _stmt.bindText(_argIndex, type)
      }
      _argIndex = 5
      if (status == null) {
        _stmt.bindNull(_argIndex)
      } else {
        _stmt.bindText(_argIndex, status)
      }
      _argIndex = 6
      if (status == null) {
        _stmt.bindNull(_argIndex)
      } else {
        _stmt.bindText(_argIndex, status)
      }
      _argIndex = 7
      if (start == null) {
        _stmt.bindNull(_argIndex)
      } else {
        _stmt.bindLong(_argIndex, start)
      }
      _argIndex = 8
      if (start == null) {
        _stmt.bindNull(_argIndex)
      } else {
        _stmt.bindLong(_argIndex, start)
      }
      _argIndex = 9
      if (end == null) {
        _stmt.bindNull(_argIndex)
      } else {
        _stmt.bindLong(_argIndex, end)
      }
      _argIndex = 10
      if (end == null) {
        _stmt.bindNull(_argIndex)
      } else {
        _stmt.bindLong(_argIndex, end)
      }
    }
    return object : LimitOffsetPagingSource<TransferEntity>(_rawQuery, __db, "transfers") {
      protected override suspend fun convertRows(limitOffsetQuery: RoomRawQuery, itemCount: Int):
          List<TransferEntity> = performSuspending(__db, true, false) { _connection ->
        val _stmt: SQLiteStatement = _connection.prepare(limitOffsetQuery.sql)
        limitOffsetQuery.getBindingFunction().invoke(_stmt)
        try {
          val _columnIndexOfTransferId: Int = getColumnIndexOrThrow(_stmt, "transferId")
          val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
          val _columnIndexOfFromUserId: Int = getColumnIndexOrThrow(_stmt, "fromUserId")
          val _columnIndexOfToUserId: Int = getColumnIndexOrThrow(_stmt, "toUserId")
          val _columnIndexOfOrderId: Int = getColumnIndexOrThrow(_stmt, "orderId")
          val _columnIndexOfAmount: Int = getColumnIndexOrThrow(_stmt, "amount")
          val _columnIndexOfCurrency: Int = getColumnIndexOrThrow(_stmt, "currency")
          val _columnIndexOfType: Int = getColumnIndexOrThrow(_stmt, "type")
          val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
          val _columnIndexOfTransactionReference: Int = getColumnIndexOrThrow(_stmt,
              "transactionReference")
          val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
          val _columnIndexOfGpsLat: Int = getColumnIndexOrThrow(_stmt, "gpsLat")
          val _columnIndexOfGpsLng: Int = getColumnIndexOrThrow(_stmt, "gpsLng")
          val _columnIndexOfSellerPhotoUrl: Int = getColumnIndexOrThrow(_stmt, "sellerPhotoUrl")
          val _columnIndexOfBuyerPhotoUrl: Int = getColumnIndexOrThrow(_stmt, "buyerPhotoUrl")
          val _columnIndexOfTimeoutAt: Int = getColumnIndexOrThrow(_stmt, "timeoutAt")
          val _columnIndexOfConditionsJson: Int = getColumnIndexOrThrow(_stmt, "conditionsJson")
          val _columnIndexOfTransferCode: Int = getColumnIndexOrThrow(_stmt, "transferCode")
          val _columnIndexOfLineageSnapshotJson: Int = getColumnIndexOrThrow(_stmt,
              "lineageSnapshotJson")
          val _columnIndexOfClaimedAt: Int = getColumnIndexOrThrow(_stmt, "claimedAt")
          val _columnIndexOfTransferType: Int = getColumnIndexOrThrow(_stmt, "transferType")
          val _columnIndexOfGrowthSnapshotJson: Int = getColumnIndexOrThrow(_stmt,
              "growthSnapshotJson")
          val _columnIndexOfHealthSnapshotJson: Int = getColumnIndexOrThrow(_stmt,
              "healthSnapshotJson")
          val _columnIndexOfTransferCodeExpiresAt: Int = getColumnIndexOrThrow(_stmt,
              "transferCodeExpiresAt")
          val _columnIndexOfInitiatedAt: Int = getColumnIndexOrThrow(_stmt, "initiatedAt")
          val _columnIndexOfCompletedAt: Int = getColumnIndexOrThrow(_stmt, "completedAt")
          val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
          val _columnIndexOfLastModifiedAt: Int = getColumnIndexOrThrow(_stmt, "lastModifiedAt")
          val _columnIndexOfIsDeleted: Int = getColumnIndexOrThrow(_stmt, "isDeleted")
          val _columnIndexOfDeletedAt: Int = getColumnIndexOrThrow(_stmt, "deletedAt")
          val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
          val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
          val _columnIndexOfMergedAt: Int = getColumnIndexOrThrow(_stmt, "mergedAt")
          val _columnIndexOfMergeCount: Int = getColumnIndexOrThrow(_stmt, "mergeCount")
          val _result: MutableList<TransferEntity> = mutableListOf()
          while (_stmt.step()) {
            val _item: TransferEntity
            val _tmpTransferId: String
            _tmpTransferId = _stmt.getText(_columnIndexOfTransferId)
            val _tmpProductId: String?
            if (_stmt.isNull(_columnIndexOfProductId)) {
              _tmpProductId = null
            } else {
              _tmpProductId = _stmt.getText(_columnIndexOfProductId)
            }
            val _tmpFromUserId: String?
            if (_stmt.isNull(_columnIndexOfFromUserId)) {
              _tmpFromUserId = null
            } else {
              _tmpFromUserId = _stmt.getText(_columnIndexOfFromUserId)
            }
            val _tmpToUserId: String?
            if (_stmt.isNull(_columnIndexOfToUserId)) {
              _tmpToUserId = null
            } else {
              _tmpToUserId = _stmt.getText(_columnIndexOfToUserId)
            }
            val _tmpOrderId: String?
            if (_stmt.isNull(_columnIndexOfOrderId)) {
              _tmpOrderId = null
            } else {
              _tmpOrderId = _stmt.getText(_columnIndexOfOrderId)
            }
            val _tmpAmount: Double
            _tmpAmount = _stmt.getDouble(_columnIndexOfAmount)
            val _tmpCurrency: String
            _tmpCurrency = _stmt.getText(_columnIndexOfCurrency)
            val _tmpType: String
            _tmpType = _stmt.getText(_columnIndexOfType)
            val _tmpStatus: String
            _tmpStatus = _stmt.getText(_columnIndexOfStatus)
            val _tmpTransactionReference: String?
            if (_stmt.isNull(_columnIndexOfTransactionReference)) {
              _tmpTransactionReference = null
            } else {
              _tmpTransactionReference = _stmt.getText(_columnIndexOfTransactionReference)
            }
            val _tmpNotes: String?
            if (_stmt.isNull(_columnIndexOfNotes)) {
              _tmpNotes = null
            } else {
              _tmpNotes = _stmt.getText(_columnIndexOfNotes)
            }
            val _tmpGpsLat: Double?
            if (_stmt.isNull(_columnIndexOfGpsLat)) {
              _tmpGpsLat = null
            } else {
              _tmpGpsLat = _stmt.getDouble(_columnIndexOfGpsLat)
            }
            val _tmpGpsLng: Double?
            if (_stmt.isNull(_columnIndexOfGpsLng)) {
              _tmpGpsLng = null
            } else {
              _tmpGpsLng = _stmt.getDouble(_columnIndexOfGpsLng)
            }
            val _tmpSellerPhotoUrl: String?
            if (_stmt.isNull(_columnIndexOfSellerPhotoUrl)) {
              _tmpSellerPhotoUrl = null
            } else {
              _tmpSellerPhotoUrl = _stmt.getText(_columnIndexOfSellerPhotoUrl)
            }
            val _tmpBuyerPhotoUrl: String?
            if (_stmt.isNull(_columnIndexOfBuyerPhotoUrl)) {
              _tmpBuyerPhotoUrl = null
            } else {
              _tmpBuyerPhotoUrl = _stmt.getText(_columnIndexOfBuyerPhotoUrl)
            }
            val _tmpTimeoutAt: Long?
            if (_stmt.isNull(_columnIndexOfTimeoutAt)) {
              _tmpTimeoutAt = null
            } else {
              _tmpTimeoutAt = _stmt.getLong(_columnIndexOfTimeoutAt)
            }
            val _tmpConditionsJson: String?
            if (_stmt.isNull(_columnIndexOfConditionsJson)) {
              _tmpConditionsJson = null
            } else {
              _tmpConditionsJson = _stmt.getText(_columnIndexOfConditionsJson)
            }
            val _tmpTransferCode: String?
            if (_stmt.isNull(_columnIndexOfTransferCode)) {
              _tmpTransferCode = null
            } else {
              _tmpTransferCode = _stmt.getText(_columnIndexOfTransferCode)
            }
            val _tmpLineageSnapshotJson: String?
            if (_stmt.isNull(_columnIndexOfLineageSnapshotJson)) {
              _tmpLineageSnapshotJson = null
            } else {
              _tmpLineageSnapshotJson = _stmt.getText(_columnIndexOfLineageSnapshotJson)
            }
            val _tmpClaimedAt: Long?
            if (_stmt.isNull(_columnIndexOfClaimedAt)) {
              _tmpClaimedAt = null
            } else {
              _tmpClaimedAt = _stmt.getLong(_columnIndexOfClaimedAt)
            }
            val _tmpTransferType: String
            _tmpTransferType = _stmt.getText(_columnIndexOfTransferType)
            val _tmpGrowthSnapshotJson: String?
            if (_stmt.isNull(_columnIndexOfGrowthSnapshotJson)) {
              _tmpGrowthSnapshotJson = null
            } else {
              _tmpGrowthSnapshotJson = _stmt.getText(_columnIndexOfGrowthSnapshotJson)
            }
            val _tmpHealthSnapshotJson: String?
            if (_stmt.isNull(_columnIndexOfHealthSnapshotJson)) {
              _tmpHealthSnapshotJson = null
            } else {
              _tmpHealthSnapshotJson = _stmt.getText(_columnIndexOfHealthSnapshotJson)
            }
            val _tmpTransferCodeExpiresAt: Long?
            if (_stmt.isNull(_columnIndexOfTransferCodeExpiresAt)) {
              _tmpTransferCodeExpiresAt = null
            } else {
              _tmpTransferCodeExpiresAt = _stmt.getLong(_columnIndexOfTransferCodeExpiresAt)
            }
            val _tmpInitiatedAt: Long
            _tmpInitiatedAt = _stmt.getLong(_columnIndexOfInitiatedAt)
            val _tmpCompletedAt: Long?
            if (_stmt.isNull(_columnIndexOfCompletedAt)) {
              _tmpCompletedAt = null
            } else {
              _tmpCompletedAt = _stmt.getLong(_columnIndexOfCompletedAt)
            }
            val _tmpUpdatedAt: Long
            _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
            val _tmpLastModifiedAt: Long
            _tmpLastModifiedAt = _stmt.getLong(_columnIndexOfLastModifiedAt)
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
            val _tmpSyncedAt: Long?
            if (_stmt.isNull(_columnIndexOfSyncedAt)) {
              _tmpSyncedAt = null
            } else {
              _tmpSyncedAt = _stmt.getLong(_columnIndexOfSyncedAt)
            }
            val _tmpMergedAt: Long?
            if (_stmt.isNull(_columnIndexOfMergedAt)) {
              _tmpMergedAt = null
            } else {
              _tmpMergedAt = _stmt.getLong(_columnIndexOfMergedAt)
            }
            val _tmpMergeCount: Int
            _tmpMergeCount = _stmt.getLong(_columnIndexOfMergeCount).toInt()
            _item =
                TransferEntity(_tmpTransferId,_tmpProductId,_tmpFromUserId,_tmpToUserId,_tmpOrderId,_tmpAmount,_tmpCurrency,_tmpType,_tmpStatus,_tmpTransactionReference,_tmpNotes,_tmpGpsLat,_tmpGpsLng,_tmpSellerPhotoUrl,_tmpBuyerPhotoUrl,_tmpTimeoutAt,_tmpConditionsJson,_tmpTransferCode,_tmpLineageSnapshotJson,_tmpClaimedAt,_tmpTransferType,_tmpGrowthSnapshotJson,_tmpHealthSnapshotJson,_tmpTransferCodeExpiresAt,_tmpInitiatedAt,_tmpCompletedAt,_tmpUpdatedAt,_tmpLastModifiedAt,_tmpIsDeleted,_tmpDeletedAt,_tmpDirty,_tmpSyncedAt,_tmpMergedAt,_tmpMergeCount)
            _result.add(_item)
          }
          _result
        } finally {
          _stmt.close()
        }
      }
    }
  }

  public override suspend fun getDirty(limit: Int): List<TransferEntity> {
    val _sql: String = "SELECT * FROM transfers WHERE dirty = 1 ORDER BY updatedAt ASC LIMIT ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, limit.toLong())
        val _columnIndexOfTransferId: Int = getColumnIndexOrThrow(_stmt, "transferId")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfFromUserId: Int = getColumnIndexOrThrow(_stmt, "fromUserId")
        val _columnIndexOfToUserId: Int = getColumnIndexOrThrow(_stmt, "toUserId")
        val _columnIndexOfOrderId: Int = getColumnIndexOrThrow(_stmt, "orderId")
        val _columnIndexOfAmount: Int = getColumnIndexOrThrow(_stmt, "amount")
        val _columnIndexOfCurrency: Int = getColumnIndexOrThrow(_stmt, "currency")
        val _columnIndexOfType: Int = getColumnIndexOrThrow(_stmt, "type")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfTransactionReference: Int = getColumnIndexOrThrow(_stmt,
            "transactionReference")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfGpsLat: Int = getColumnIndexOrThrow(_stmt, "gpsLat")
        val _columnIndexOfGpsLng: Int = getColumnIndexOrThrow(_stmt, "gpsLng")
        val _columnIndexOfSellerPhotoUrl: Int = getColumnIndexOrThrow(_stmt, "sellerPhotoUrl")
        val _columnIndexOfBuyerPhotoUrl: Int = getColumnIndexOrThrow(_stmt, "buyerPhotoUrl")
        val _columnIndexOfTimeoutAt: Int = getColumnIndexOrThrow(_stmt, "timeoutAt")
        val _columnIndexOfConditionsJson: Int = getColumnIndexOrThrow(_stmt, "conditionsJson")
        val _columnIndexOfTransferCode: Int = getColumnIndexOrThrow(_stmt, "transferCode")
        val _columnIndexOfLineageSnapshotJson: Int = getColumnIndexOrThrow(_stmt,
            "lineageSnapshotJson")
        val _columnIndexOfClaimedAt: Int = getColumnIndexOrThrow(_stmt, "claimedAt")
        val _columnIndexOfTransferType: Int = getColumnIndexOrThrow(_stmt, "transferType")
        val _columnIndexOfGrowthSnapshotJson: Int = getColumnIndexOrThrow(_stmt,
            "growthSnapshotJson")
        val _columnIndexOfHealthSnapshotJson: Int = getColumnIndexOrThrow(_stmt,
            "healthSnapshotJson")
        val _columnIndexOfTransferCodeExpiresAt: Int = getColumnIndexOrThrow(_stmt,
            "transferCodeExpiresAt")
        val _columnIndexOfInitiatedAt: Int = getColumnIndexOrThrow(_stmt, "initiatedAt")
        val _columnIndexOfCompletedAt: Int = getColumnIndexOrThrow(_stmt, "completedAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfLastModifiedAt: Int = getColumnIndexOrThrow(_stmt, "lastModifiedAt")
        val _columnIndexOfIsDeleted: Int = getColumnIndexOrThrow(_stmt, "isDeleted")
        val _columnIndexOfDeletedAt: Int = getColumnIndexOrThrow(_stmt, "deletedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _columnIndexOfMergedAt: Int = getColumnIndexOrThrow(_stmt, "mergedAt")
        val _columnIndexOfMergeCount: Int = getColumnIndexOrThrow(_stmt, "mergeCount")
        val _result: MutableList<TransferEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: TransferEntity
          val _tmpTransferId: String
          _tmpTransferId = _stmt.getText(_columnIndexOfTransferId)
          val _tmpProductId: String?
          if (_stmt.isNull(_columnIndexOfProductId)) {
            _tmpProductId = null
          } else {
            _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          }
          val _tmpFromUserId: String?
          if (_stmt.isNull(_columnIndexOfFromUserId)) {
            _tmpFromUserId = null
          } else {
            _tmpFromUserId = _stmt.getText(_columnIndexOfFromUserId)
          }
          val _tmpToUserId: String?
          if (_stmt.isNull(_columnIndexOfToUserId)) {
            _tmpToUserId = null
          } else {
            _tmpToUserId = _stmt.getText(_columnIndexOfToUserId)
          }
          val _tmpOrderId: String?
          if (_stmt.isNull(_columnIndexOfOrderId)) {
            _tmpOrderId = null
          } else {
            _tmpOrderId = _stmt.getText(_columnIndexOfOrderId)
          }
          val _tmpAmount: Double
          _tmpAmount = _stmt.getDouble(_columnIndexOfAmount)
          val _tmpCurrency: String
          _tmpCurrency = _stmt.getText(_columnIndexOfCurrency)
          val _tmpType: String
          _tmpType = _stmt.getText(_columnIndexOfType)
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpTransactionReference: String?
          if (_stmt.isNull(_columnIndexOfTransactionReference)) {
            _tmpTransactionReference = null
          } else {
            _tmpTransactionReference = _stmt.getText(_columnIndexOfTransactionReference)
          }
          val _tmpNotes: String?
          if (_stmt.isNull(_columnIndexOfNotes)) {
            _tmpNotes = null
          } else {
            _tmpNotes = _stmt.getText(_columnIndexOfNotes)
          }
          val _tmpGpsLat: Double?
          if (_stmt.isNull(_columnIndexOfGpsLat)) {
            _tmpGpsLat = null
          } else {
            _tmpGpsLat = _stmt.getDouble(_columnIndexOfGpsLat)
          }
          val _tmpGpsLng: Double?
          if (_stmt.isNull(_columnIndexOfGpsLng)) {
            _tmpGpsLng = null
          } else {
            _tmpGpsLng = _stmt.getDouble(_columnIndexOfGpsLng)
          }
          val _tmpSellerPhotoUrl: String?
          if (_stmt.isNull(_columnIndexOfSellerPhotoUrl)) {
            _tmpSellerPhotoUrl = null
          } else {
            _tmpSellerPhotoUrl = _stmt.getText(_columnIndexOfSellerPhotoUrl)
          }
          val _tmpBuyerPhotoUrl: String?
          if (_stmt.isNull(_columnIndexOfBuyerPhotoUrl)) {
            _tmpBuyerPhotoUrl = null
          } else {
            _tmpBuyerPhotoUrl = _stmt.getText(_columnIndexOfBuyerPhotoUrl)
          }
          val _tmpTimeoutAt: Long?
          if (_stmt.isNull(_columnIndexOfTimeoutAt)) {
            _tmpTimeoutAt = null
          } else {
            _tmpTimeoutAt = _stmt.getLong(_columnIndexOfTimeoutAt)
          }
          val _tmpConditionsJson: String?
          if (_stmt.isNull(_columnIndexOfConditionsJson)) {
            _tmpConditionsJson = null
          } else {
            _tmpConditionsJson = _stmt.getText(_columnIndexOfConditionsJson)
          }
          val _tmpTransferCode: String?
          if (_stmt.isNull(_columnIndexOfTransferCode)) {
            _tmpTransferCode = null
          } else {
            _tmpTransferCode = _stmt.getText(_columnIndexOfTransferCode)
          }
          val _tmpLineageSnapshotJson: String?
          if (_stmt.isNull(_columnIndexOfLineageSnapshotJson)) {
            _tmpLineageSnapshotJson = null
          } else {
            _tmpLineageSnapshotJson = _stmt.getText(_columnIndexOfLineageSnapshotJson)
          }
          val _tmpClaimedAt: Long?
          if (_stmt.isNull(_columnIndexOfClaimedAt)) {
            _tmpClaimedAt = null
          } else {
            _tmpClaimedAt = _stmt.getLong(_columnIndexOfClaimedAt)
          }
          val _tmpTransferType: String
          _tmpTransferType = _stmt.getText(_columnIndexOfTransferType)
          val _tmpGrowthSnapshotJson: String?
          if (_stmt.isNull(_columnIndexOfGrowthSnapshotJson)) {
            _tmpGrowthSnapshotJson = null
          } else {
            _tmpGrowthSnapshotJson = _stmt.getText(_columnIndexOfGrowthSnapshotJson)
          }
          val _tmpHealthSnapshotJson: String?
          if (_stmt.isNull(_columnIndexOfHealthSnapshotJson)) {
            _tmpHealthSnapshotJson = null
          } else {
            _tmpHealthSnapshotJson = _stmt.getText(_columnIndexOfHealthSnapshotJson)
          }
          val _tmpTransferCodeExpiresAt: Long?
          if (_stmt.isNull(_columnIndexOfTransferCodeExpiresAt)) {
            _tmpTransferCodeExpiresAt = null
          } else {
            _tmpTransferCodeExpiresAt = _stmt.getLong(_columnIndexOfTransferCodeExpiresAt)
          }
          val _tmpInitiatedAt: Long
          _tmpInitiatedAt = _stmt.getLong(_columnIndexOfInitiatedAt)
          val _tmpCompletedAt: Long?
          if (_stmt.isNull(_columnIndexOfCompletedAt)) {
            _tmpCompletedAt = null
          } else {
            _tmpCompletedAt = _stmt.getLong(_columnIndexOfCompletedAt)
          }
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpLastModifiedAt: Long
          _tmpLastModifiedAt = _stmt.getLong(_columnIndexOfLastModifiedAt)
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
          val _tmpSyncedAt: Long?
          if (_stmt.isNull(_columnIndexOfSyncedAt)) {
            _tmpSyncedAt = null
          } else {
            _tmpSyncedAt = _stmt.getLong(_columnIndexOfSyncedAt)
          }
          val _tmpMergedAt: Long?
          if (_stmt.isNull(_columnIndexOfMergedAt)) {
            _tmpMergedAt = null
          } else {
            _tmpMergedAt = _stmt.getLong(_columnIndexOfMergedAt)
          }
          val _tmpMergeCount: Int
          _tmpMergeCount = _stmt.getLong(_columnIndexOfMergeCount).toInt()
          _item =
              TransferEntity(_tmpTransferId,_tmpProductId,_tmpFromUserId,_tmpToUserId,_tmpOrderId,_tmpAmount,_tmpCurrency,_tmpType,_tmpStatus,_tmpTransactionReference,_tmpNotes,_tmpGpsLat,_tmpGpsLng,_tmpSellerPhotoUrl,_tmpBuyerPhotoUrl,_tmpTimeoutAt,_tmpConditionsJson,_tmpTransferCode,_tmpLineageSnapshotJson,_tmpClaimedAt,_tmpTransferType,_tmpGrowthSnapshotJson,_tmpHealthSnapshotJson,_tmpTransferCodeExpiresAt,_tmpInitiatedAt,_tmpCompletedAt,_tmpUpdatedAt,_tmpLastModifiedAt,_tmpIsDeleted,_tmpDeletedAt,_tmpDirty,_tmpSyncedAt,_tmpMergedAt,_tmpMergeCount)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeDirtyByUser(userId: String): Flow<List<TransferEntity>> {
    val _sql: String =
        "SELECT * FROM transfers WHERE (fromUserId = ? OR toUserId = ?) AND dirty = 1"
    return createFlow(__db, false, arrayOf("transfers")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, userId)
        _argIndex = 2
        _stmt.bindText(_argIndex, userId)
        val _columnIndexOfTransferId: Int = getColumnIndexOrThrow(_stmt, "transferId")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfFromUserId: Int = getColumnIndexOrThrow(_stmt, "fromUserId")
        val _columnIndexOfToUserId: Int = getColumnIndexOrThrow(_stmt, "toUserId")
        val _columnIndexOfOrderId: Int = getColumnIndexOrThrow(_stmt, "orderId")
        val _columnIndexOfAmount: Int = getColumnIndexOrThrow(_stmt, "amount")
        val _columnIndexOfCurrency: Int = getColumnIndexOrThrow(_stmt, "currency")
        val _columnIndexOfType: Int = getColumnIndexOrThrow(_stmt, "type")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfTransactionReference: Int = getColumnIndexOrThrow(_stmt,
            "transactionReference")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfGpsLat: Int = getColumnIndexOrThrow(_stmt, "gpsLat")
        val _columnIndexOfGpsLng: Int = getColumnIndexOrThrow(_stmt, "gpsLng")
        val _columnIndexOfSellerPhotoUrl: Int = getColumnIndexOrThrow(_stmt, "sellerPhotoUrl")
        val _columnIndexOfBuyerPhotoUrl: Int = getColumnIndexOrThrow(_stmt, "buyerPhotoUrl")
        val _columnIndexOfTimeoutAt: Int = getColumnIndexOrThrow(_stmt, "timeoutAt")
        val _columnIndexOfConditionsJson: Int = getColumnIndexOrThrow(_stmt, "conditionsJson")
        val _columnIndexOfTransferCode: Int = getColumnIndexOrThrow(_stmt, "transferCode")
        val _columnIndexOfLineageSnapshotJson: Int = getColumnIndexOrThrow(_stmt,
            "lineageSnapshotJson")
        val _columnIndexOfClaimedAt: Int = getColumnIndexOrThrow(_stmt, "claimedAt")
        val _columnIndexOfTransferType: Int = getColumnIndexOrThrow(_stmt, "transferType")
        val _columnIndexOfGrowthSnapshotJson: Int = getColumnIndexOrThrow(_stmt,
            "growthSnapshotJson")
        val _columnIndexOfHealthSnapshotJson: Int = getColumnIndexOrThrow(_stmt,
            "healthSnapshotJson")
        val _columnIndexOfTransferCodeExpiresAt: Int = getColumnIndexOrThrow(_stmt,
            "transferCodeExpiresAt")
        val _columnIndexOfInitiatedAt: Int = getColumnIndexOrThrow(_stmt, "initiatedAt")
        val _columnIndexOfCompletedAt: Int = getColumnIndexOrThrow(_stmt, "completedAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfLastModifiedAt: Int = getColumnIndexOrThrow(_stmt, "lastModifiedAt")
        val _columnIndexOfIsDeleted: Int = getColumnIndexOrThrow(_stmt, "isDeleted")
        val _columnIndexOfDeletedAt: Int = getColumnIndexOrThrow(_stmt, "deletedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _columnIndexOfMergedAt: Int = getColumnIndexOrThrow(_stmt, "mergedAt")
        val _columnIndexOfMergeCount: Int = getColumnIndexOrThrow(_stmt, "mergeCount")
        val _result: MutableList<TransferEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: TransferEntity
          val _tmpTransferId: String
          _tmpTransferId = _stmt.getText(_columnIndexOfTransferId)
          val _tmpProductId: String?
          if (_stmt.isNull(_columnIndexOfProductId)) {
            _tmpProductId = null
          } else {
            _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          }
          val _tmpFromUserId: String?
          if (_stmt.isNull(_columnIndexOfFromUserId)) {
            _tmpFromUserId = null
          } else {
            _tmpFromUserId = _stmt.getText(_columnIndexOfFromUserId)
          }
          val _tmpToUserId: String?
          if (_stmt.isNull(_columnIndexOfToUserId)) {
            _tmpToUserId = null
          } else {
            _tmpToUserId = _stmt.getText(_columnIndexOfToUserId)
          }
          val _tmpOrderId: String?
          if (_stmt.isNull(_columnIndexOfOrderId)) {
            _tmpOrderId = null
          } else {
            _tmpOrderId = _stmt.getText(_columnIndexOfOrderId)
          }
          val _tmpAmount: Double
          _tmpAmount = _stmt.getDouble(_columnIndexOfAmount)
          val _tmpCurrency: String
          _tmpCurrency = _stmt.getText(_columnIndexOfCurrency)
          val _tmpType: String
          _tmpType = _stmt.getText(_columnIndexOfType)
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpTransactionReference: String?
          if (_stmt.isNull(_columnIndexOfTransactionReference)) {
            _tmpTransactionReference = null
          } else {
            _tmpTransactionReference = _stmt.getText(_columnIndexOfTransactionReference)
          }
          val _tmpNotes: String?
          if (_stmt.isNull(_columnIndexOfNotes)) {
            _tmpNotes = null
          } else {
            _tmpNotes = _stmt.getText(_columnIndexOfNotes)
          }
          val _tmpGpsLat: Double?
          if (_stmt.isNull(_columnIndexOfGpsLat)) {
            _tmpGpsLat = null
          } else {
            _tmpGpsLat = _stmt.getDouble(_columnIndexOfGpsLat)
          }
          val _tmpGpsLng: Double?
          if (_stmt.isNull(_columnIndexOfGpsLng)) {
            _tmpGpsLng = null
          } else {
            _tmpGpsLng = _stmt.getDouble(_columnIndexOfGpsLng)
          }
          val _tmpSellerPhotoUrl: String?
          if (_stmt.isNull(_columnIndexOfSellerPhotoUrl)) {
            _tmpSellerPhotoUrl = null
          } else {
            _tmpSellerPhotoUrl = _stmt.getText(_columnIndexOfSellerPhotoUrl)
          }
          val _tmpBuyerPhotoUrl: String?
          if (_stmt.isNull(_columnIndexOfBuyerPhotoUrl)) {
            _tmpBuyerPhotoUrl = null
          } else {
            _tmpBuyerPhotoUrl = _stmt.getText(_columnIndexOfBuyerPhotoUrl)
          }
          val _tmpTimeoutAt: Long?
          if (_stmt.isNull(_columnIndexOfTimeoutAt)) {
            _tmpTimeoutAt = null
          } else {
            _tmpTimeoutAt = _stmt.getLong(_columnIndexOfTimeoutAt)
          }
          val _tmpConditionsJson: String?
          if (_stmt.isNull(_columnIndexOfConditionsJson)) {
            _tmpConditionsJson = null
          } else {
            _tmpConditionsJson = _stmt.getText(_columnIndexOfConditionsJson)
          }
          val _tmpTransferCode: String?
          if (_stmt.isNull(_columnIndexOfTransferCode)) {
            _tmpTransferCode = null
          } else {
            _tmpTransferCode = _stmt.getText(_columnIndexOfTransferCode)
          }
          val _tmpLineageSnapshotJson: String?
          if (_stmt.isNull(_columnIndexOfLineageSnapshotJson)) {
            _tmpLineageSnapshotJson = null
          } else {
            _tmpLineageSnapshotJson = _stmt.getText(_columnIndexOfLineageSnapshotJson)
          }
          val _tmpClaimedAt: Long?
          if (_stmt.isNull(_columnIndexOfClaimedAt)) {
            _tmpClaimedAt = null
          } else {
            _tmpClaimedAt = _stmt.getLong(_columnIndexOfClaimedAt)
          }
          val _tmpTransferType: String
          _tmpTransferType = _stmt.getText(_columnIndexOfTransferType)
          val _tmpGrowthSnapshotJson: String?
          if (_stmt.isNull(_columnIndexOfGrowthSnapshotJson)) {
            _tmpGrowthSnapshotJson = null
          } else {
            _tmpGrowthSnapshotJson = _stmt.getText(_columnIndexOfGrowthSnapshotJson)
          }
          val _tmpHealthSnapshotJson: String?
          if (_stmt.isNull(_columnIndexOfHealthSnapshotJson)) {
            _tmpHealthSnapshotJson = null
          } else {
            _tmpHealthSnapshotJson = _stmt.getText(_columnIndexOfHealthSnapshotJson)
          }
          val _tmpTransferCodeExpiresAt: Long?
          if (_stmt.isNull(_columnIndexOfTransferCodeExpiresAt)) {
            _tmpTransferCodeExpiresAt = null
          } else {
            _tmpTransferCodeExpiresAt = _stmt.getLong(_columnIndexOfTransferCodeExpiresAt)
          }
          val _tmpInitiatedAt: Long
          _tmpInitiatedAt = _stmt.getLong(_columnIndexOfInitiatedAt)
          val _tmpCompletedAt: Long?
          if (_stmt.isNull(_columnIndexOfCompletedAt)) {
            _tmpCompletedAt = null
          } else {
            _tmpCompletedAt = _stmt.getLong(_columnIndexOfCompletedAt)
          }
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpLastModifiedAt: Long
          _tmpLastModifiedAt = _stmt.getLong(_columnIndexOfLastModifiedAt)
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
          val _tmpSyncedAt: Long?
          if (_stmt.isNull(_columnIndexOfSyncedAt)) {
            _tmpSyncedAt = null
          } else {
            _tmpSyncedAt = _stmt.getLong(_columnIndexOfSyncedAt)
          }
          val _tmpMergedAt: Long?
          if (_stmt.isNull(_columnIndexOfMergedAt)) {
            _tmpMergedAt = null
          } else {
            _tmpMergedAt = _stmt.getLong(_columnIndexOfMergedAt)
          }
          val _tmpMergeCount: Int
          _tmpMergeCount = _stmt.getLong(_columnIndexOfMergeCount).toInt()
          _item =
              TransferEntity(_tmpTransferId,_tmpProductId,_tmpFromUserId,_tmpToUserId,_tmpOrderId,_tmpAmount,_tmpCurrency,_tmpType,_tmpStatus,_tmpTransactionReference,_tmpNotes,_tmpGpsLat,_tmpGpsLng,_tmpSellerPhotoUrl,_tmpBuyerPhotoUrl,_tmpTimeoutAt,_tmpConditionsJson,_tmpTransferCode,_tmpLineageSnapshotJson,_tmpClaimedAt,_tmpTransferType,_tmpGrowthSnapshotJson,_tmpHealthSnapshotJson,_tmpTransferCodeExpiresAt,_tmpInitiatedAt,_tmpCompletedAt,_tmpUpdatedAt,_tmpLastModifiedAt,_tmpIsDeleted,_tmpDeletedAt,_tmpDirty,_tmpSyncedAt,_tmpMergedAt,_tmpMergeCount)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observePendingCountForFarmer(userId: String): Flow<Int> {
    val _sql: String =
        "SELECT COUNT(*) FROM transfers WHERE (fromUserId = ? OR toUserId = ?) AND status = 'PENDING'"
    return createFlow(__db, false, arrayOf("transfers")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, userId)
        _argIndex = 2
        _stmt.bindText(_argIndex, userId)
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

  public override fun observeAwaitingVerificationCountForFarmer(userId: String): Flow<Int> {
    val _sql: String = "SELECT COUNT(*) FROM transfers WHERE toUserId = ? AND status = 'PENDING'"
    return createFlow(__db, false, arrayOf("transfers")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, userId)
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

  public override fun observeRecentTransfersForFarmer(userId: String, since: Long):
      Flow<List<TransferEntity>> {
    val _sql: String =
        "SELECT * FROM transfers WHERE (fromUserId = ? OR toUserId = ?) AND initiatedAt >= ? ORDER BY initiatedAt DESC"
    return createFlow(__db, false, arrayOf("transfers")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, userId)
        _argIndex = 2
        _stmt.bindText(_argIndex, userId)
        _argIndex = 3
        _stmt.bindLong(_argIndex, since)
        val _columnIndexOfTransferId: Int = getColumnIndexOrThrow(_stmt, "transferId")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfFromUserId: Int = getColumnIndexOrThrow(_stmt, "fromUserId")
        val _columnIndexOfToUserId: Int = getColumnIndexOrThrow(_stmt, "toUserId")
        val _columnIndexOfOrderId: Int = getColumnIndexOrThrow(_stmt, "orderId")
        val _columnIndexOfAmount: Int = getColumnIndexOrThrow(_stmt, "amount")
        val _columnIndexOfCurrency: Int = getColumnIndexOrThrow(_stmt, "currency")
        val _columnIndexOfType: Int = getColumnIndexOrThrow(_stmt, "type")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfTransactionReference: Int = getColumnIndexOrThrow(_stmt,
            "transactionReference")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfGpsLat: Int = getColumnIndexOrThrow(_stmt, "gpsLat")
        val _columnIndexOfGpsLng: Int = getColumnIndexOrThrow(_stmt, "gpsLng")
        val _columnIndexOfSellerPhotoUrl: Int = getColumnIndexOrThrow(_stmt, "sellerPhotoUrl")
        val _columnIndexOfBuyerPhotoUrl: Int = getColumnIndexOrThrow(_stmt, "buyerPhotoUrl")
        val _columnIndexOfTimeoutAt: Int = getColumnIndexOrThrow(_stmt, "timeoutAt")
        val _columnIndexOfConditionsJson: Int = getColumnIndexOrThrow(_stmt, "conditionsJson")
        val _columnIndexOfTransferCode: Int = getColumnIndexOrThrow(_stmt, "transferCode")
        val _columnIndexOfLineageSnapshotJson: Int = getColumnIndexOrThrow(_stmt,
            "lineageSnapshotJson")
        val _columnIndexOfClaimedAt: Int = getColumnIndexOrThrow(_stmt, "claimedAt")
        val _columnIndexOfTransferType: Int = getColumnIndexOrThrow(_stmt, "transferType")
        val _columnIndexOfGrowthSnapshotJson: Int = getColumnIndexOrThrow(_stmt,
            "growthSnapshotJson")
        val _columnIndexOfHealthSnapshotJson: Int = getColumnIndexOrThrow(_stmt,
            "healthSnapshotJson")
        val _columnIndexOfTransferCodeExpiresAt: Int = getColumnIndexOrThrow(_stmt,
            "transferCodeExpiresAt")
        val _columnIndexOfInitiatedAt: Int = getColumnIndexOrThrow(_stmt, "initiatedAt")
        val _columnIndexOfCompletedAt: Int = getColumnIndexOrThrow(_stmt, "completedAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfLastModifiedAt: Int = getColumnIndexOrThrow(_stmt, "lastModifiedAt")
        val _columnIndexOfIsDeleted: Int = getColumnIndexOrThrow(_stmt, "isDeleted")
        val _columnIndexOfDeletedAt: Int = getColumnIndexOrThrow(_stmt, "deletedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _columnIndexOfMergedAt: Int = getColumnIndexOrThrow(_stmt, "mergedAt")
        val _columnIndexOfMergeCount: Int = getColumnIndexOrThrow(_stmt, "mergeCount")
        val _result: MutableList<TransferEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: TransferEntity
          val _tmpTransferId: String
          _tmpTransferId = _stmt.getText(_columnIndexOfTransferId)
          val _tmpProductId: String?
          if (_stmt.isNull(_columnIndexOfProductId)) {
            _tmpProductId = null
          } else {
            _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          }
          val _tmpFromUserId: String?
          if (_stmt.isNull(_columnIndexOfFromUserId)) {
            _tmpFromUserId = null
          } else {
            _tmpFromUserId = _stmt.getText(_columnIndexOfFromUserId)
          }
          val _tmpToUserId: String?
          if (_stmt.isNull(_columnIndexOfToUserId)) {
            _tmpToUserId = null
          } else {
            _tmpToUserId = _stmt.getText(_columnIndexOfToUserId)
          }
          val _tmpOrderId: String?
          if (_stmt.isNull(_columnIndexOfOrderId)) {
            _tmpOrderId = null
          } else {
            _tmpOrderId = _stmt.getText(_columnIndexOfOrderId)
          }
          val _tmpAmount: Double
          _tmpAmount = _stmt.getDouble(_columnIndexOfAmount)
          val _tmpCurrency: String
          _tmpCurrency = _stmt.getText(_columnIndexOfCurrency)
          val _tmpType: String
          _tmpType = _stmt.getText(_columnIndexOfType)
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpTransactionReference: String?
          if (_stmt.isNull(_columnIndexOfTransactionReference)) {
            _tmpTransactionReference = null
          } else {
            _tmpTransactionReference = _stmt.getText(_columnIndexOfTransactionReference)
          }
          val _tmpNotes: String?
          if (_stmt.isNull(_columnIndexOfNotes)) {
            _tmpNotes = null
          } else {
            _tmpNotes = _stmt.getText(_columnIndexOfNotes)
          }
          val _tmpGpsLat: Double?
          if (_stmt.isNull(_columnIndexOfGpsLat)) {
            _tmpGpsLat = null
          } else {
            _tmpGpsLat = _stmt.getDouble(_columnIndexOfGpsLat)
          }
          val _tmpGpsLng: Double?
          if (_stmt.isNull(_columnIndexOfGpsLng)) {
            _tmpGpsLng = null
          } else {
            _tmpGpsLng = _stmt.getDouble(_columnIndexOfGpsLng)
          }
          val _tmpSellerPhotoUrl: String?
          if (_stmt.isNull(_columnIndexOfSellerPhotoUrl)) {
            _tmpSellerPhotoUrl = null
          } else {
            _tmpSellerPhotoUrl = _stmt.getText(_columnIndexOfSellerPhotoUrl)
          }
          val _tmpBuyerPhotoUrl: String?
          if (_stmt.isNull(_columnIndexOfBuyerPhotoUrl)) {
            _tmpBuyerPhotoUrl = null
          } else {
            _tmpBuyerPhotoUrl = _stmt.getText(_columnIndexOfBuyerPhotoUrl)
          }
          val _tmpTimeoutAt: Long?
          if (_stmt.isNull(_columnIndexOfTimeoutAt)) {
            _tmpTimeoutAt = null
          } else {
            _tmpTimeoutAt = _stmt.getLong(_columnIndexOfTimeoutAt)
          }
          val _tmpConditionsJson: String?
          if (_stmt.isNull(_columnIndexOfConditionsJson)) {
            _tmpConditionsJson = null
          } else {
            _tmpConditionsJson = _stmt.getText(_columnIndexOfConditionsJson)
          }
          val _tmpTransferCode: String?
          if (_stmt.isNull(_columnIndexOfTransferCode)) {
            _tmpTransferCode = null
          } else {
            _tmpTransferCode = _stmt.getText(_columnIndexOfTransferCode)
          }
          val _tmpLineageSnapshotJson: String?
          if (_stmt.isNull(_columnIndexOfLineageSnapshotJson)) {
            _tmpLineageSnapshotJson = null
          } else {
            _tmpLineageSnapshotJson = _stmt.getText(_columnIndexOfLineageSnapshotJson)
          }
          val _tmpClaimedAt: Long?
          if (_stmt.isNull(_columnIndexOfClaimedAt)) {
            _tmpClaimedAt = null
          } else {
            _tmpClaimedAt = _stmt.getLong(_columnIndexOfClaimedAt)
          }
          val _tmpTransferType: String
          _tmpTransferType = _stmt.getText(_columnIndexOfTransferType)
          val _tmpGrowthSnapshotJson: String?
          if (_stmt.isNull(_columnIndexOfGrowthSnapshotJson)) {
            _tmpGrowthSnapshotJson = null
          } else {
            _tmpGrowthSnapshotJson = _stmt.getText(_columnIndexOfGrowthSnapshotJson)
          }
          val _tmpHealthSnapshotJson: String?
          if (_stmt.isNull(_columnIndexOfHealthSnapshotJson)) {
            _tmpHealthSnapshotJson = null
          } else {
            _tmpHealthSnapshotJson = _stmt.getText(_columnIndexOfHealthSnapshotJson)
          }
          val _tmpTransferCodeExpiresAt: Long?
          if (_stmt.isNull(_columnIndexOfTransferCodeExpiresAt)) {
            _tmpTransferCodeExpiresAt = null
          } else {
            _tmpTransferCodeExpiresAt = _stmt.getLong(_columnIndexOfTransferCodeExpiresAt)
          }
          val _tmpInitiatedAt: Long
          _tmpInitiatedAt = _stmt.getLong(_columnIndexOfInitiatedAt)
          val _tmpCompletedAt: Long?
          if (_stmt.isNull(_columnIndexOfCompletedAt)) {
            _tmpCompletedAt = null
          } else {
            _tmpCompletedAt = _stmt.getLong(_columnIndexOfCompletedAt)
          }
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpLastModifiedAt: Long
          _tmpLastModifiedAt = _stmt.getLong(_columnIndexOfLastModifiedAt)
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
          val _tmpSyncedAt: Long?
          if (_stmt.isNull(_columnIndexOfSyncedAt)) {
            _tmpSyncedAt = null
          } else {
            _tmpSyncedAt = _stmt.getLong(_columnIndexOfSyncedAt)
          }
          val _tmpMergedAt: Long?
          if (_stmt.isNull(_columnIndexOfMergedAt)) {
            _tmpMergedAt = null
          } else {
            _tmpMergedAt = _stmt.getLong(_columnIndexOfMergedAt)
          }
          val _tmpMergeCount: Int
          _tmpMergeCount = _stmt.getLong(_columnIndexOfMergeCount).toInt()
          _item =
              TransferEntity(_tmpTransferId,_tmpProductId,_tmpFromUserId,_tmpToUserId,_tmpOrderId,_tmpAmount,_tmpCurrency,_tmpType,_tmpStatus,_tmpTransactionReference,_tmpNotes,_tmpGpsLat,_tmpGpsLng,_tmpSellerPhotoUrl,_tmpBuyerPhotoUrl,_tmpTimeoutAt,_tmpConditionsJson,_tmpTransferCode,_tmpLineageSnapshotJson,_tmpClaimedAt,_tmpTransferType,_tmpGrowthSnapshotJson,_tmpHealthSnapshotJson,_tmpTransferCodeExpiresAt,_tmpInitiatedAt,_tmpCompletedAt,_tmpUpdatedAt,_tmpLastModifiedAt,_tmpIsDeleted,_tmpDeletedAt,_tmpDirty,_tmpSyncedAt,_tmpMergedAt,_tmpMergeCount)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeTransfersByStatusForFarmer(userId: String, status: String):
      Flow<List<TransferEntity>> {
    val _sql: String =
        "SELECT * FROM transfers WHERE (fromUserId = ? OR toUserId = ?) AND status = ? ORDER BY initiatedAt DESC"
    return createFlow(__db, false, arrayOf("transfers")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, userId)
        _argIndex = 2
        _stmt.bindText(_argIndex, userId)
        _argIndex = 3
        _stmt.bindText(_argIndex, status)
        val _columnIndexOfTransferId: Int = getColumnIndexOrThrow(_stmt, "transferId")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfFromUserId: Int = getColumnIndexOrThrow(_stmt, "fromUserId")
        val _columnIndexOfToUserId: Int = getColumnIndexOrThrow(_stmt, "toUserId")
        val _columnIndexOfOrderId: Int = getColumnIndexOrThrow(_stmt, "orderId")
        val _columnIndexOfAmount: Int = getColumnIndexOrThrow(_stmt, "amount")
        val _columnIndexOfCurrency: Int = getColumnIndexOrThrow(_stmt, "currency")
        val _columnIndexOfType: Int = getColumnIndexOrThrow(_stmt, "type")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfTransactionReference: Int = getColumnIndexOrThrow(_stmt,
            "transactionReference")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfGpsLat: Int = getColumnIndexOrThrow(_stmt, "gpsLat")
        val _columnIndexOfGpsLng: Int = getColumnIndexOrThrow(_stmt, "gpsLng")
        val _columnIndexOfSellerPhotoUrl: Int = getColumnIndexOrThrow(_stmt, "sellerPhotoUrl")
        val _columnIndexOfBuyerPhotoUrl: Int = getColumnIndexOrThrow(_stmt, "buyerPhotoUrl")
        val _columnIndexOfTimeoutAt: Int = getColumnIndexOrThrow(_stmt, "timeoutAt")
        val _columnIndexOfConditionsJson: Int = getColumnIndexOrThrow(_stmt, "conditionsJson")
        val _columnIndexOfTransferCode: Int = getColumnIndexOrThrow(_stmt, "transferCode")
        val _columnIndexOfLineageSnapshotJson: Int = getColumnIndexOrThrow(_stmt,
            "lineageSnapshotJson")
        val _columnIndexOfClaimedAt: Int = getColumnIndexOrThrow(_stmt, "claimedAt")
        val _columnIndexOfTransferType: Int = getColumnIndexOrThrow(_stmt, "transferType")
        val _columnIndexOfGrowthSnapshotJson: Int = getColumnIndexOrThrow(_stmt,
            "growthSnapshotJson")
        val _columnIndexOfHealthSnapshotJson: Int = getColumnIndexOrThrow(_stmt,
            "healthSnapshotJson")
        val _columnIndexOfTransferCodeExpiresAt: Int = getColumnIndexOrThrow(_stmt,
            "transferCodeExpiresAt")
        val _columnIndexOfInitiatedAt: Int = getColumnIndexOrThrow(_stmt, "initiatedAt")
        val _columnIndexOfCompletedAt: Int = getColumnIndexOrThrow(_stmt, "completedAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfLastModifiedAt: Int = getColumnIndexOrThrow(_stmt, "lastModifiedAt")
        val _columnIndexOfIsDeleted: Int = getColumnIndexOrThrow(_stmt, "isDeleted")
        val _columnIndexOfDeletedAt: Int = getColumnIndexOrThrow(_stmt, "deletedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _columnIndexOfMergedAt: Int = getColumnIndexOrThrow(_stmt, "mergedAt")
        val _columnIndexOfMergeCount: Int = getColumnIndexOrThrow(_stmt, "mergeCount")
        val _result: MutableList<TransferEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: TransferEntity
          val _tmpTransferId: String
          _tmpTransferId = _stmt.getText(_columnIndexOfTransferId)
          val _tmpProductId: String?
          if (_stmt.isNull(_columnIndexOfProductId)) {
            _tmpProductId = null
          } else {
            _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          }
          val _tmpFromUserId: String?
          if (_stmt.isNull(_columnIndexOfFromUserId)) {
            _tmpFromUserId = null
          } else {
            _tmpFromUserId = _stmt.getText(_columnIndexOfFromUserId)
          }
          val _tmpToUserId: String?
          if (_stmt.isNull(_columnIndexOfToUserId)) {
            _tmpToUserId = null
          } else {
            _tmpToUserId = _stmt.getText(_columnIndexOfToUserId)
          }
          val _tmpOrderId: String?
          if (_stmt.isNull(_columnIndexOfOrderId)) {
            _tmpOrderId = null
          } else {
            _tmpOrderId = _stmt.getText(_columnIndexOfOrderId)
          }
          val _tmpAmount: Double
          _tmpAmount = _stmt.getDouble(_columnIndexOfAmount)
          val _tmpCurrency: String
          _tmpCurrency = _stmt.getText(_columnIndexOfCurrency)
          val _tmpType: String
          _tmpType = _stmt.getText(_columnIndexOfType)
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpTransactionReference: String?
          if (_stmt.isNull(_columnIndexOfTransactionReference)) {
            _tmpTransactionReference = null
          } else {
            _tmpTransactionReference = _stmt.getText(_columnIndexOfTransactionReference)
          }
          val _tmpNotes: String?
          if (_stmt.isNull(_columnIndexOfNotes)) {
            _tmpNotes = null
          } else {
            _tmpNotes = _stmt.getText(_columnIndexOfNotes)
          }
          val _tmpGpsLat: Double?
          if (_stmt.isNull(_columnIndexOfGpsLat)) {
            _tmpGpsLat = null
          } else {
            _tmpGpsLat = _stmt.getDouble(_columnIndexOfGpsLat)
          }
          val _tmpGpsLng: Double?
          if (_stmt.isNull(_columnIndexOfGpsLng)) {
            _tmpGpsLng = null
          } else {
            _tmpGpsLng = _stmt.getDouble(_columnIndexOfGpsLng)
          }
          val _tmpSellerPhotoUrl: String?
          if (_stmt.isNull(_columnIndexOfSellerPhotoUrl)) {
            _tmpSellerPhotoUrl = null
          } else {
            _tmpSellerPhotoUrl = _stmt.getText(_columnIndexOfSellerPhotoUrl)
          }
          val _tmpBuyerPhotoUrl: String?
          if (_stmt.isNull(_columnIndexOfBuyerPhotoUrl)) {
            _tmpBuyerPhotoUrl = null
          } else {
            _tmpBuyerPhotoUrl = _stmt.getText(_columnIndexOfBuyerPhotoUrl)
          }
          val _tmpTimeoutAt: Long?
          if (_stmt.isNull(_columnIndexOfTimeoutAt)) {
            _tmpTimeoutAt = null
          } else {
            _tmpTimeoutAt = _stmt.getLong(_columnIndexOfTimeoutAt)
          }
          val _tmpConditionsJson: String?
          if (_stmt.isNull(_columnIndexOfConditionsJson)) {
            _tmpConditionsJson = null
          } else {
            _tmpConditionsJson = _stmt.getText(_columnIndexOfConditionsJson)
          }
          val _tmpTransferCode: String?
          if (_stmt.isNull(_columnIndexOfTransferCode)) {
            _tmpTransferCode = null
          } else {
            _tmpTransferCode = _stmt.getText(_columnIndexOfTransferCode)
          }
          val _tmpLineageSnapshotJson: String?
          if (_stmt.isNull(_columnIndexOfLineageSnapshotJson)) {
            _tmpLineageSnapshotJson = null
          } else {
            _tmpLineageSnapshotJson = _stmt.getText(_columnIndexOfLineageSnapshotJson)
          }
          val _tmpClaimedAt: Long?
          if (_stmt.isNull(_columnIndexOfClaimedAt)) {
            _tmpClaimedAt = null
          } else {
            _tmpClaimedAt = _stmt.getLong(_columnIndexOfClaimedAt)
          }
          val _tmpTransferType: String
          _tmpTransferType = _stmt.getText(_columnIndexOfTransferType)
          val _tmpGrowthSnapshotJson: String?
          if (_stmt.isNull(_columnIndexOfGrowthSnapshotJson)) {
            _tmpGrowthSnapshotJson = null
          } else {
            _tmpGrowthSnapshotJson = _stmt.getText(_columnIndexOfGrowthSnapshotJson)
          }
          val _tmpHealthSnapshotJson: String?
          if (_stmt.isNull(_columnIndexOfHealthSnapshotJson)) {
            _tmpHealthSnapshotJson = null
          } else {
            _tmpHealthSnapshotJson = _stmt.getText(_columnIndexOfHealthSnapshotJson)
          }
          val _tmpTransferCodeExpiresAt: Long?
          if (_stmt.isNull(_columnIndexOfTransferCodeExpiresAt)) {
            _tmpTransferCodeExpiresAt = null
          } else {
            _tmpTransferCodeExpiresAt = _stmt.getLong(_columnIndexOfTransferCodeExpiresAt)
          }
          val _tmpInitiatedAt: Long
          _tmpInitiatedAt = _stmt.getLong(_columnIndexOfInitiatedAt)
          val _tmpCompletedAt: Long?
          if (_stmt.isNull(_columnIndexOfCompletedAt)) {
            _tmpCompletedAt = null
          } else {
            _tmpCompletedAt = _stmt.getLong(_columnIndexOfCompletedAt)
          }
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpLastModifiedAt: Long
          _tmpLastModifiedAt = _stmt.getLong(_columnIndexOfLastModifiedAt)
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
          val _tmpSyncedAt: Long?
          if (_stmt.isNull(_columnIndexOfSyncedAt)) {
            _tmpSyncedAt = null
          } else {
            _tmpSyncedAt = _stmt.getLong(_columnIndexOfSyncedAt)
          }
          val _tmpMergedAt: Long?
          if (_stmt.isNull(_columnIndexOfMergedAt)) {
            _tmpMergedAt = null
          } else {
            _tmpMergedAt = _stmt.getLong(_columnIndexOfMergedAt)
          }
          val _tmpMergeCount: Int
          _tmpMergeCount = _stmt.getLong(_columnIndexOfMergeCount).toInt()
          _item =
              TransferEntity(_tmpTransferId,_tmpProductId,_tmpFromUserId,_tmpToUserId,_tmpOrderId,_tmpAmount,_tmpCurrency,_tmpType,_tmpStatus,_tmpTransactionReference,_tmpNotes,_tmpGpsLat,_tmpGpsLng,_tmpSellerPhotoUrl,_tmpBuyerPhotoUrl,_tmpTimeoutAt,_tmpConditionsJson,_tmpTransferCode,_tmpLineageSnapshotJson,_tmpClaimedAt,_tmpTransferType,_tmpGrowthSnapshotJson,_tmpHealthSnapshotJson,_tmpTransferCodeExpiresAt,_tmpInitiatedAt,_tmpCompletedAt,_tmpUpdatedAt,_tmpLastModifiedAt,_tmpIsDeleted,_tmpDeletedAt,_tmpDirty,_tmpSyncedAt,_tmpMergedAt,_tmpMergeCount)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun findById(transferId: String): TransferEntity? {
    val _sql: String = "SELECT * FROM transfers WHERE transferId = ? LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, transferId)
        val _columnIndexOfTransferId: Int = getColumnIndexOrThrow(_stmt, "transferId")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfFromUserId: Int = getColumnIndexOrThrow(_stmt, "fromUserId")
        val _columnIndexOfToUserId: Int = getColumnIndexOrThrow(_stmt, "toUserId")
        val _columnIndexOfOrderId: Int = getColumnIndexOrThrow(_stmt, "orderId")
        val _columnIndexOfAmount: Int = getColumnIndexOrThrow(_stmt, "amount")
        val _columnIndexOfCurrency: Int = getColumnIndexOrThrow(_stmt, "currency")
        val _columnIndexOfType: Int = getColumnIndexOrThrow(_stmt, "type")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfTransactionReference: Int = getColumnIndexOrThrow(_stmt,
            "transactionReference")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfGpsLat: Int = getColumnIndexOrThrow(_stmt, "gpsLat")
        val _columnIndexOfGpsLng: Int = getColumnIndexOrThrow(_stmt, "gpsLng")
        val _columnIndexOfSellerPhotoUrl: Int = getColumnIndexOrThrow(_stmt, "sellerPhotoUrl")
        val _columnIndexOfBuyerPhotoUrl: Int = getColumnIndexOrThrow(_stmt, "buyerPhotoUrl")
        val _columnIndexOfTimeoutAt: Int = getColumnIndexOrThrow(_stmt, "timeoutAt")
        val _columnIndexOfConditionsJson: Int = getColumnIndexOrThrow(_stmt, "conditionsJson")
        val _columnIndexOfTransferCode: Int = getColumnIndexOrThrow(_stmt, "transferCode")
        val _columnIndexOfLineageSnapshotJson: Int = getColumnIndexOrThrow(_stmt,
            "lineageSnapshotJson")
        val _columnIndexOfClaimedAt: Int = getColumnIndexOrThrow(_stmt, "claimedAt")
        val _columnIndexOfTransferType: Int = getColumnIndexOrThrow(_stmt, "transferType")
        val _columnIndexOfGrowthSnapshotJson: Int = getColumnIndexOrThrow(_stmt,
            "growthSnapshotJson")
        val _columnIndexOfHealthSnapshotJson: Int = getColumnIndexOrThrow(_stmt,
            "healthSnapshotJson")
        val _columnIndexOfTransferCodeExpiresAt: Int = getColumnIndexOrThrow(_stmt,
            "transferCodeExpiresAt")
        val _columnIndexOfInitiatedAt: Int = getColumnIndexOrThrow(_stmt, "initiatedAt")
        val _columnIndexOfCompletedAt: Int = getColumnIndexOrThrow(_stmt, "completedAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfLastModifiedAt: Int = getColumnIndexOrThrow(_stmt, "lastModifiedAt")
        val _columnIndexOfIsDeleted: Int = getColumnIndexOrThrow(_stmt, "isDeleted")
        val _columnIndexOfDeletedAt: Int = getColumnIndexOrThrow(_stmt, "deletedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _columnIndexOfMergedAt: Int = getColumnIndexOrThrow(_stmt, "mergedAt")
        val _columnIndexOfMergeCount: Int = getColumnIndexOrThrow(_stmt, "mergeCount")
        val _result: TransferEntity?
        if (_stmt.step()) {
          val _tmpTransferId: String
          _tmpTransferId = _stmt.getText(_columnIndexOfTransferId)
          val _tmpProductId: String?
          if (_stmt.isNull(_columnIndexOfProductId)) {
            _tmpProductId = null
          } else {
            _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          }
          val _tmpFromUserId: String?
          if (_stmt.isNull(_columnIndexOfFromUserId)) {
            _tmpFromUserId = null
          } else {
            _tmpFromUserId = _stmt.getText(_columnIndexOfFromUserId)
          }
          val _tmpToUserId: String?
          if (_stmt.isNull(_columnIndexOfToUserId)) {
            _tmpToUserId = null
          } else {
            _tmpToUserId = _stmt.getText(_columnIndexOfToUserId)
          }
          val _tmpOrderId: String?
          if (_stmt.isNull(_columnIndexOfOrderId)) {
            _tmpOrderId = null
          } else {
            _tmpOrderId = _stmt.getText(_columnIndexOfOrderId)
          }
          val _tmpAmount: Double
          _tmpAmount = _stmt.getDouble(_columnIndexOfAmount)
          val _tmpCurrency: String
          _tmpCurrency = _stmt.getText(_columnIndexOfCurrency)
          val _tmpType: String
          _tmpType = _stmt.getText(_columnIndexOfType)
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpTransactionReference: String?
          if (_stmt.isNull(_columnIndexOfTransactionReference)) {
            _tmpTransactionReference = null
          } else {
            _tmpTransactionReference = _stmt.getText(_columnIndexOfTransactionReference)
          }
          val _tmpNotes: String?
          if (_stmt.isNull(_columnIndexOfNotes)) {
            _tmpNotes = null
          } else {
            _tmpNotes = _stmt.getText(_columnIndexOfNotes)
          }
          val _tmpGpsLat: Double?
          if (_stmt.isNull(_columnIndexOfGpsLat)) {
            _tmpGpsLat = null
          } else {
            _tmpGpsLat = _stmt.getDouble(_columnIndexOfGpsLat)
          }
          val _tmpGpsLng: Double?
          if (_stmt.isNull(_columnIndexOfGpsLng)) {
            _tmpGpsLng = null
          } else {
            _tmpGpsLng = _stmt.getDouble(_columnIndexOfGpsLng)
          }
          val _tmpSellerPhotoUrl: String?
          if (_stmt.isNull(_columnIndexOfSellerPhotoUrl)) {
            _tmpSellerPhotoUrl = null
          } else {
            _tmpSellerPhotoUrl = _stmt.getText(_columnIndexOfSellerPhotoUrl)
          }
          val _tmpBuyerPhotoUrl: String?
          if (_stmt.isNull(_columnIndexOfBuyerPhotoUrl)) {
            _tmpBuyerPhotoUrl = null
          } else {
            _tmpBuyerPhotoUrl = _stmt.getText(_columnIndexOfBuyerPhotoUrl)
          }
          val _tmpTimeoutAt: Long?
          if (_stmt.isNull(_columnIndexOfTimeoutAt)) {
            _tmpTimeoutAt = null
          } else {
            _tmpTimeoutAt = _stmt.getLong(_columnIndexOfTimeoutAt)
          }
          val _tmpConditionsJson: String?
          if (_stmt.isNull(_columnIndexOfConditionsJson)) {
            _tmpConditionsJson = null
          } else {
            _tmpConditionsJson = _stmt.getText(_columnIndexOfConditionsJson)
          }
          val _tmpTransferCode: String?
          if (_stmt.isNull(_columnIndexOfTransferCode)) {
            _tmpTransferCode = null
          } else {
            _tmpTransferCode = _stmt.getText(_columnIndexOfTransferCode)
          }
          val _tmpLineageSnapshotJson: String?
          if (_stmt.isNull(_columnIndexOfLineageSnapshotJson)) {
            _tmpLineageSnapshotJson = null
          } else {
            _tmpLineageSnapshotJson = _stmt.getText(_columnIndexOfLineageSnapshotJson)
          }
          val _tmpClaimedAt: Long?
          if (_stmt.isNull(_columnIndexOfClaimedAt)) {
            _tmpClaimedAt = null
          } else {
            _tmpClaimedAt = _stmt.getLong(_columnIndexOfClaimedAt)
          }
          val _tmpTransferType: String
          _tmpTransferType = _stmt.getText(_columnIndexOfTransferType)
          val _tmpGrowthSnapshotJson: String?
          if (_stmt.isNull(_columnIndexOfGrowthSnapshotJson)) {
            _tmpGrowthSnapshotJson = null
          } else {
            _tmpGrowthSnapshotJson = _stmt.getText(_columnIndexOfGrowthSnapshotJson)
          }
          val _tmpHealthSnapshotJson: String?
          if (_stmt.isNull(_columnIndexOfHealthSnapshotJson)) {
            _tmpHealthSnapshotJson = null
          } else {
            _tmpHealthSnapshotJson = _stmt.getText(_columnIndexOfHealthSnapshotJson)
          }
          val _tmpTransferCodeExpiresAt: Long?
          if (_stmt.isNull(_columnIndexOfTransferCodeExpiresAt)) {
            _tmpTransferCodeExpiresAt = null
          } else {
            _tmpTransferCodeExpiresAt = _stmt.getLong(_columnIndexOfTransferCodeExpiresAt)
          }
          val _tmpInitiatedAt: Long
          _tmpInitiatedAt = _stmt.getLong(_columnIndexOfInitiatedAt)
          val _tmpCompletedAt: Long?
          if (_stmt.isNull(_columnIndexOfCompletedAt)) {
            _tmpCompletedAt = null
          } else {
            _tmpCompletedAt = _stmt.getLong(_columnIndexOfCompletedAt)
          }
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpLastModifiedAt: Long
          _tmpLastModifiedAt = _stmt.getLong(_columnIndexOfLastModifiedAt)
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
          val _tmpSyncedAt: Long?
          if (_stmt.isNull(_columnIndexOfSyncedAt)) {
            _tmpSyncedAt = null
          } else {
            _tmpSyncedAt = _stmt.getLong(_columnIndexOfSyncedAt)
          }
          val _tmpMergedAt: Long?
          if (_stmt.isNull(_columnIndexOfMergedAt)) {
            _tmpMergedAt = null
          } else {
            _tmpMergedAt = _stmt.getLong(_columnIndexOfMergedAt)
          }
          val _tmpMergeCount: Int
          _tmpMergeCount = _stmt.getLong(_columnIndexOfMergeCount).toInt()
          _result =
              TransferEntity(_tmpTransferId,_tmpProductId,_tmpFromUserId,_tmpToUserId,_tmpOrderId,_tmpAmount,_tmpCurrency,_tmpType,_tmpStatus,_tmpTransactionReference,_tmpNotes,_tmpGpsLat,_tmpGpsLng,_tmpSellerPhotoUrl,_tmpBuyerPhotoUrl,_tmpTimeoutAt,_tmpConditionsJson,_tmpTransferCode,_tmpLineageSnapshotJson,_tmpClaimedAt,_tmpTransferType,_tmpGrowthSnapshotJson,_tmpHealthSnapshotJson,_tmpTransferCodeExpiresAt,_tmpInitiatedAt,_tmpCompletedAt,_tmpUpdatedAt,_tmpLastModifiedAt,_tmpIsDeleted,_tmpDeletedAt,_tmpDirty,_tmpSyncedAt,_tmpMergedAt,_tmpMergeCount)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun findActiveTransferForProduct(productId: String): TransferEntity? {
    val _sql: String =
        "SELECT * FROM transfers WHERE productId = ? AND status IN ('TRANSFER_PENDING', 'CLAIMED') AND isDeleted = 0 LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, productId)
        val _columnIndexOfTransferId: Int = getColumnIndexOrThrow(_stmt, "transferId")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfFromUserId: Int = getColumnIndexOrThrow(_stmt, "fromUserId")
        val _columnIndexOfToUserId: Int = getColumnIndexOrThrow(_stmt, "toUserId")
        val _columnIndexOfOrderId: Int = getColumnIndexOrThrow(_stmt, "orderId")
        val _columnIndexOfAmount: Int = getColumnIndexOrThrow(_stmt, "amount")
        val _columnIndexOfCurrency: Int = getColumnIndexOrThrow(_stmt, "currency")
        val _columnIndexOfType: Int = getColumnIndexOrThrow(_stmt, "type")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfTransactionReference: Int = getColumnIndexOrThrow(_stmt,
            "transactionReference")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfGpsLat: Int = getColumnIndexOrThrow(_stmt, "gpsLat")
        val _columnIndexOfGpsLng: Int = getColumnIndexOrThrow(_stmt, "gpsLng")
        val _columnIndexOfSellerPhotoUrl: Int = getColumnIndexOrThrow(_stmt, "sellerPhotoUrl")
        val _columnIndexOfBuyerPhotoUrl: Int = getColumnIndexOrThrow(_stmt, "buyerPhotoUrl")
        val _columnIndexOfTimeoutAt: Int = getColumnIndexOrThrow(_stmt, "timeoutAt")
        val _columnIndexOfConditionsJson: Int = getColumnIndexOrThrow(_stmt, "conditionsJson")
        val _columnIndexOfTransferCode: Int = getColumnIndexOrThrow(_stmt, "transferCode")
        val _columnIndexOfLineageSnapshotJson: Int = getColumnIndexOrThrow(_stmt,
            "lineageSnapshotJson")
        val _columnIndexOfClaimedAt: Int = getColumnIndexOrThrow(_stmt, "claimedAt")
        val _columnIndexOfTransferType: Int = getColumnIndexOrThrow(_stmt, "transferType")
        val _columnIndexOfGrowthSnapshotJson: Int = getColumnIndexOrThrow(_stmt,
            "growthSnapshotJson")
        val _columnIndexOfHealthSnapshotJson: Int = getColumnIndexOrThrow(_stmt,
            "healthSnapshotJson")
        val _columnIndexOfTransferCodeExpiresAt: Int = getColumnIndexOrThrow(_stmt,
            "transferCodeExpiresAt")
        val _columnIndexOfInitiatedAt: Int = getColumnIndexOrThrow(_stmt, "initiatedAt")
        val _columnIndexOfCompletedAt: Int = getColumnIndexOrThrow(_stmt, "completedAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfLastModifiedAt: Int = getColumnIndexOrThrow(_stmt, "lastModifiedAt")
        val _columnIndexOfIsDeleted: Int = getColumnIndexOrThrow(_stmt, "isDeleted")
        val _columnIndexOfDeletedAt: Int = getColumnIndexOrThrow(_stmt, "deletedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _columnIndexOfMergedAt: Int = getColumnIndexOrThrow(_stmt, "mergedAt")
        val _columnIndexOfMergeCount: Int = getColumnIndexOrThrow(_stmt, "mergeCount")
        val _result: TransferEntity?
        if (_stmt.step()) {
          val _tmpTransferId: String
          _tmpTransferId = _stmt.getText(_columnIndexOfTransferId)
          val _tmpProductId: String?
          if (_stmt.isNull(_columnIndexOfProductId)) {
            _tmpProductId = null
          } else {
            _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          }
          val _tmpFromUserId: String?
          if (_stmt.isNull(_columnIndexOfFromUserId)) {
            _tmpFromUserId = null
          } else {
            _tmpFromUserId = _stmt.getText(_columnIndexOfFromUserId)
          }
          val _tmpToUserId: String?
          if (_stmt.isNull(_columnIndexOfToUserId)) {
            _tmpToUserId = null
          } else {
            _tmpToUserId = _stmt.getText(_columnIndexOfToUserId)
          }
          val _tmpOrderId: String?
          if (_stmt.isNull(_columnIndexOfOrderId)) {
            _tmpOrderId = null
          } else {
            _tmpOrderId = _stmt.getText(_columnIndexOfOrderId)
          }
          val _tmpAmount: Double
          _tmpAmount = _stmt.getDouble(_columnIndexOfAmount)
          val _tmpCurrency: String
          _tmpCurrency = _stmt.getText(_columnIndexOfCurrency)
          val _tmpType: String
          _tmpType = _stmt.getText(_columnIndexOfType)
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpTransactionReference: String?
          if (_stmt.isNull(_columnIndexOfTransactionReference)) {
            _tmpTransactionReference = null
          } else {
            _tmpTransactionReference = _stmt.getText(_columnIndexOfTransactionReference)
          }
          val _tmpNotes: String?
          if (_stmt.isNull(_columnIndexOfNotes)) {
            _tmpNotes = null
          } else {
            _tmpNotes = _stmt.getText(_columnIndexOfNotes)
          }
          val _tmpGpsLat: Double?
          if (_stmt.isNull(_columnIndexOfGpsLat)) {
            _tmpGpsLat = null
          } else {
            _tmpGpsLat = _stmt.getDouble(_columnIndexOfGpsLat)
          }
          val _tmpGpsLng: Double?
          if (_stmt.isNull(_columnIndexOfGpsLng)) {
            _tmpGpsLng = null
          } else {
            _tmpGpsLng = _stmt.getDouble(_columnIndexOfGpsLng)
          }
          val _tmpSellerPhotoUrl: String?
          if (_stmt.isNull(_columnIndexOfSellerPhotoUrl)) {
            _tmpSellerPhotoUrl = null
          } else {
            _tmpSellerPhotoUrl = _stmt.getText(_columnIndexOfSellerPhotoUrl)
          }
          val _tmpBuyerPhotoUrl: String?
          if (_stmt.isNull(_columnIndexOfBuyerPhotoUrl)) {
            _tmpBuyerPhotoUrl = null
          } else {
            _tmpBuyerPhotoUrl = _stmt.getText(_columnIndexOfBuyerPhotoUrl)
          }
          val _tmpTimeoutAt: Long?
          if (_stmt.isNull(_columnIndexOfTimeoutAt)) {
            _tmpTimeoutAt = null
          } else {
            _tmpTimeoutAt = _stmt.getLong(_columnIndexOfTimeoutAt)
          }
          val _tmpConditionsJson: String?
          if (_stmt.isNull(_columnIndexOfConditionsJson)) {
            _tmpConditionsJson = null
          } else {
            _tmpConditionsJson = _stmt.getText(_columnIndexOfConditionsJson)
          }
          val _tmpTransferCode: String?
          if (_stmt.isNull(_columnIndexOfTransferCode)) {
            _tmpTransferCode = null
          } else {
            _tmpTransferCode = _stmt.getText(_columnIndexOfTransferCode)
          }
          val _tmpLineageSnapshotJson: String?
          if (_stmt.isNull(_columnIndexOfLineageSnapshotJson)) {
            _tmpLineageSnapshotJson = null
          } else {
            _tmpLineageSnapshotJson = _stmt.getText(_columnIndexOfLineageSnapshotJson)
          }
          val _tmpClaimedAt: Long?
          if (_stmt.isNull(_columnIndexOfClaimedAt)) {
            _tmpClaimedAt = null
          } else {
            _tmpClaimedAt = _stmt.getLong(_columnIndexOfClaimedAt)
          }
          val _tmpTransferType: String
          _tmpTransferType = _stmt.getText(_columnIndexOfTransferType)
          val _tmpGrowthSnapshotJson: String?
          if (_stmt.isNull(_columnIndexOfGrowthSnapshotJson)) {
            _tmpGrowthSnapshotJson = null
          } else {
            _tmpGrowthSnapshotJson = _stmt.getText(_columnIndexOfGrowthSnapshotJson)
          }
          val _tmpHealthSnapshotJson: String?
          if (_stmt.isNull(_columnIndexOfHealthSnapshotJson)) {
            _tmpHealthSnapshotJson = null
          } else {
            _tmpHealthSnapshotJson = _stmt.getText(_columnIndexOfHealthSnapshotJson)
          }
          val _tmpTransferCodeExpiresAt: Long?
          if (_stmt.isNull(_columnIndexOfTransferCodeExpiresAt)) {
            _tmpTransferCodeExpiresAt = null
          } else {
            _tmpTransferCodeExpiresAt = _stmt.getLong(_columnIndexOfTransferCodeExpiresAt)
          }
          val _tmpInitiatedAt: Long
          _tmpInitiatedAt = _stmt.getLong(_columnIndexOfInitiatedAt)
          val _tmpCompletedAt: Long?
          if (_stmt.isNull(_columnIndexOfCompletedAt)) {
            _tmpCompletedAt = null
          } else {
            _tmpCompletedAt = _stmt.getLong(_columnIndexOfCompletedAt)
          }
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpLastModifiedAt: Long
          _tmpLastModifiedAt = _stmt.getLong(_columnIndexOfLastModifiedAt)
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
          val _tmpSyncedAt: Long?
          if (_stmt.isNull(_columnIndexOfSyncedAt)) {
            _tmpSyncedAt = null
          } else {
            _tmpSyncedAt = _stmt.getLong(_columnIndexOfSyncedAt)
          }
          val _tmpMergedAt: Long?
          if (_stmt.isNull(_columnIndexOfMergedAt)) {
            _tmpMergedAt = null
          } else {
            _tmpMergedAt = _stmt.getLong(_columnIndexOfMergedAt)
          }
          val _tmpMergeCount: Int
          _tmpMergeCount = _stmt.getLong(_columnIndexOfMergeCount).toInt()
          _result =
              TransferEntity(_tmpTransferId,_tmpProductId,_tmpFromUserId,_tmpToUserId,_tmpOrderId,_tmpAmount,_tmpCurrency,_tmpType,_tmpStatus,_tmpTransactionReference,_tmpNotes,_tmpGpsLat,_tmpGpsLng,_tmpSellerPhotoUrl,_tmpBuyerPhotoUrl,_tmpTimeoutAt,_tmpConditionsJson,_tmpTransferCode,_tmpLineageSnapshotJson,_tmpClaimedAt,_tmpTransferType,_tmpGrowthSnapshotJson,_tmpHealthSnapshotJson,_tmpTransferCodeExpiresAt,_tmpInitiatedAt,_tmpCompletedAt,_tmpUpdatedAt,_tmpLastModifiedAt,_tmpIsDeleted,_tmpDeletedAt,_tmpDirty,_tmpSyncedAt,_tmpMergedAt,_tmpMergeCount)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun findByTransferCode(code: String): TransferEntity? {
    val _sql: String = "SELECT * FROM transfers WHERE transferCode = ? AND isDeleted = 0 LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, code)
        val _columnIndexOfTransferId: Int = getColumnIndexOrThrow(_stmt, "transferId")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfFromUserId: Int = getColumnIndexOrThrow(_stmt, "fromUserId")
        val _columnIndexOfToUserId: Int = getColumnIndexOrThrow(_stmt, "toUserId")
        val _columnIndexOfOrderId: Int = getColumnIndexOrThrow(_stmt, "orderId")
        val _columnIndexOfAmount: Int = getColumnIndexOrThrow(_stmt, "amount")
        val _columnIndexOfCurrency: Int = getColumnIndexOrThrow(_stmt, "currency")
        val _columnIndexOfType: Int = getColumnIndexOrThrow(_stmt, "type")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfTransactionReference: Int = getColumnIndexOrThrow(_stmt,
            "transactionReference")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfGpsLat: Int = getColumnIndexOrThrow(_stmt, "gpsLat")
        val _columnIndexOfGpsLng: Int = getColumnIndexOrThrow(_stmt, "gpsLng")
        val _columnIndexOfSellerPhotoUrl: Int = getColumnIndexOrThrow(_stmt, "sellerPhotoUrl")
        val _columnIndexOfBuyerPhotoUrl: Int = getColumnIndexOrThrow(_stmt, "buyerPhotoUrl")
        val _columnIndexOfTimeoutAt: Int = getColumnIndexOrThrow(_stmt, "timeoutAt")
        val _columnIndexOfConditionsJson: Int = getColumnIndexOrThrow(_stmt, "conditionsJson")
        val _columnIndexOfTransferCode: Int = getColumnIndexOrThrow(_stmt, "transferCode")
        val _columnIndexOfLineageSnapshotJson: Int = getColumnIndexOrThrow(_stmt,
            "lineageSnapshotJson")
        val _columnIndexOfClaimedAt: Int = getColumnIndexOrThrow(_stmt, "claimedAt")
        val _columnIndexOfTransferType: Int = getColumnIndexOrThrow(_stmt, "transferType")
        val _columnIndexOfGrowthSnapshotJson: Int = getColumnIndexOrThrow(_stmt,
            "growthSnapshotJson")
        val _columnIndexOfHealthSnapshotJson: Int = getColumnIndexOrThrow(_stmt,
            "healthSnapshotJson")
        val _columnIndexOfTransferCodeExpiresAt: Int = getColumnIndexOrThrow(_stmt,
            "transferCodeExpiresAt")
        val _columnIndexOfInitiatedAt: Int = getColumnIndexOrThrow(_stmt, "initiatedAt")
        val _columnIndexOfCompletedAt: Int = getColumnIndexOrThrow(_stmt, "completedAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfLastModifiedAt: Int = getColumnIndexOrThrow(_stmt, "lastModifiedAt")
        val _columnIndexOfIsDeleted: Int = getColumnIndexOrThrow(_stmt, "isDeleted")
        val _columnIndexOfDeletedAt: Int = getColumnIndexOrThrow(_stmt, "deletedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _columnIndexOfMergedAt: Int = getColumnIndexOrThrow(_stmt, "mergedAt")
        val _columnIndexOfMergeCount: Int = getColumnIndexOrThrow(_stmt, "mergeCount")
        val _result: TransferEntity?
        if (_stmt.step()) {
          val _tmpTransferId: String
          _tmpTransferId = _stmt.getText(_columnIndexOfTransferId)
          val _tmpProductId: String?
          if (_stmt.isNull(_columnIndexOfProductId)) {
            _tmpProductId = null
          } else {
            _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          }
          val _tmpFromUserId: String?
          if (_stmt.isNull(_columnIndexOfFromUserId)) {
            _tmpFromUserId = null
          } else {
            _tmpFromUserId = _stmt.getText(_columnIndexOfFromUserId)
          }
          val _tmpToUserId: String?
          if (_stmt.isNull(_columnIndexOfToUserId)) {
            _tmpToUserId = null
          } else {
            _tmpToUserId = _stmt.getText(_columnIndexOfToUserId)
          }
          val _tmpOrderId: String?
          if (_stmt.isNull(_columnIndexOfOrderId)) {
            _tmpOrderId = null
          } else {
            _tmpOrderId = _stmt.getText(_columnIndexOfOrderId)
          }
          val _tmpAmount: Double
          _tmpAmount = _stmt.getDouble(_columnIndexOfAmount)
          val _tmpCurrency: String
          _tmpCurrency = _stmt.getText(_columnIndexOfCurrency)
          val _tmpType: String
          _tmpType = _stmt.getText(_columnIndexOfType)
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpTransactionReference: String?
          if (_stmt.isNull(_columnIndexOfTransactionReference)) {
            _tmpTransactionReference = null
          } else {
            _tmpTransactionReference = _stmt.getText(_columnIndexOfTransactionReference)
          }
          val _tmpNotes: String?
          if (_stmt.isNull(_columnIndexOfNotes)) {
            _tmpNotes = null
          } else {
            _tmpNotes = _stmt.getText(_columnIndexOfNotes)
          }
          val _tmpGpsLat: Double?
          if (_stmt.isNull(_columnIndexOfGpsLat)) {
            _tmpGpsLat = null
          } else {
            _tmpGpsLat = _stmt.getDouble(_columnIndexOfGpsLat)
          }
          val _tmpGpsLng: Double?
          if (_stmt.isNull(_columnIndexOfGpsLng)) {
            _tmpGpsLng = null
          } else {
            _tmpGpsLng = _stmt.getDouble(_columnIndexOfGpsLng)
          }
          val _tmpSellerPhotoUrl: String?
          if (_stmt.isNull(_columnIndexOfSellerPhotoUrl)) {
            _tmpSellerPhotoUrl = null
          } else {
            _tmpSellerPhotoUrl = _stmt.getText(_columnIndexOfSellerPhotoUrl)
          }
          val _tmpBuyerPhotoUrl: String?
          if (_stmt.isNull(_columnIndexOfBuyerPhotoUrl)) {
            _tmpBuyerPhotoUrl = null
          } else {
            _tmpBuyerPhotoUrl = _stmt.getText(_columnIndexOfBuyerPhotoUrl)
          }
          val _tmpTimeoutAt: Long?
          if (_stmt.isNull(_columnIndexOfTimeoutAt)) {
            _tmpTimeoutAt = null
          } else {
            _tmpTimeoutAt = _stmt.getLong(_columnIndexOfTimeoutAt)
          }
          val _tmpConditionsJson: String?
          if (_stmt.isNull(_columnIndexOfConditionsJson)) {
            _tmpConditionsJson = null
          } else {
            _tmpConditionsJson = _stmt.getText(_columnIndexOfConditionsJson)
          }
          val _tmpTransferCode: String?
          if (_stmt.isNull(_columnIndexOfTransferCode)) {
            _tmpTransferCode = null
          } else {
            _tmpTransferCode = _stmt.getText(_columnIndexOfTransferCode)
          }
          val _tmpLineageSnapshotJson: String?
          if (_stmt.isNull(_columnIndexOfLineageSnapshotJson)) {
            _tmpLineageSnapshotJson = null
          } else {
            _tmpLineageSnapshotJson = _stmt.getText(_columnIndexOfLineageSnapshotJson)
          }
          val _tmpClaimedAt: Long?
          if (_stmt.isNull(_columnIndexOfClaimedAt)) {
            _tmpClaimedAt = null
          } else {
            _tmpClaimedAt = _stmt.getLong(_columnIndexOfClaimedAt)
          }
          val _tmpTransferType: String
          _tmpTransferType = _stmt.getText(_columnIndexOfTransferType)
          val _tmpGrowthSnapshotJson: String?
          if (_stmt.isNull(_columnIndexOfGrowthSnapshotJson)) {
            _tmpGrowthSnapshotJson = null
          } else {
            _tmpGrowthSnapshotJson = _stmt.getText(_columnIndexOfGrowthSnapshotJson)
          }
          val _tmpHealthSnapshotJson: String?
          if (_stmt.isNull(_columnIndexOfHealthSnapshotJson)) {
            _tmpHealthSnapshotJson = null
          } else {
            _tmpHealthSnapshotJson = _stmt.getText(_columnIndexOfHealthSnapshotJson)
          }
          val _tmpTransferCodeExpiresAt: Long?
          if (_stmt.isNull(_columnIndexOfTransferCodeExpiresAt)) {
            _tmpTransferCodeExpiresAt = null
          } else {
            _tmpTransferCodeExpiresAt = _stmt.getLong(_columnIndexOfTransferCodeExpiresAt)
          }
          val _tmpInitiatedAt: Long
          _tmpInitiatedAt = _stmt.getLong(_columnIndexOfInitiatedAt)
          val _tmpCompletedAt: Long?
          if (_stmt.isNull(_columnIndexOfCompletedAt)) {
            _tmpCompletedAt = null
          } else {
            _tmpCompletedAt = _stmt.getLong(_columnIndexOfCompletedAt)
          }
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpLastModifiedAt: Long
          _tmpLastModifiedAt = _stmt.getLong(_columnIndexOfLastModifiedAt)
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
          val _tmpSyncedAt: Long?
          if (_stmt.isNull(_columnIndexOfSyncedAt)) {
            _tmpSyncedAt = null
          } else {
            _tmpSyncedAt = _stmt.getLong(_columnIndexOfSyncedAt)
          }
          val _tmpMergedAt: Long?
          if (_stmt.isNull(_columnIndexOfMergedAt)) {
            _tmpMergedAt = null
          } else {
            _tmpMergedAt = _stmt.getLong(_columnIndexOfMergedAt)
          }
          val _tmpMergeCount: Int
          _tmpMergeCount = _stmt.getLong(_columnIndexOfMergeCount).toInt()
          _result =
              TransferEntity(_tmpTransferId,_tmpProductId,_tmpFromUserId,_tmpToUserId,_tmpOrderId,_tmpAmount,_tmpCurrency,_tmpType,_tmpStatus,_tmpTransactionReference,_tmpNotes,_tmpGpsLat,_tmpGpsLng,_tmpSellerPhotoUrl,_tmpBuyerPhotoUrl,_tmpTimeoutAt,_tmpConditionsJson,_tmpTransferCode,_tmpLineageSnapshotJson,_tmpClaimedAt,_tmpTransferType,_tmpGrowthSnapshotJson,_tmpHealthSnapshotJson,_tmpTransferCodeExpiresAt,_tmpInitiatedAt,_tmpCompletedAt,_tmpUpdatedAt,_tmpLastModifiedAt,_tmpIsDeleted,_tmpDeletedAt,_tmpDirty,_tmpSyncedAt,_tmpMergedAt,_tmpMergeCount)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun findByFromUserAndStatus(userId: String, status: String):
      List<TransferEntity> {
    val _sql: String =
        "SELECT * FROM transfers WHERE fromUserId = ? AND status = ? AND isDeleted = 0 ORDER BY initiatedAt DESC"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, userId)
        _argIndex = 2
        _stmt.bindText(_argIndex, status)
        val _columnIndexOfTransferId: Int = getColumnIndexOrThrow(_stmt, "transferId")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfFromUserId: Int = getColumnIndexOrThrow(_stmt, "fromUserId")
        val _columnIndexOfToUserId: Int = getColumnIndexOrThrow(_stmt, "toUserId")
        val _columnIndexOfOrderId: Int = getColumnIndexOrThrow(_stmt, "orderId")
        val _columnIndexOfAmount: Int = getColumnIndexOrThrow(_stmt, "amount")
        val _columnIndexOfCurrency: Int = getColumnIndexOrThrow(_stmt, "currency")
        val _columnIndexOfType: Int = getColumnIndexOrThrow(_stmt, "type")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfTransactionReference: Int = getColumnIndexOrThrow(_stmt,
            "transactionReference")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfGpsLat: Int = getColumnIndexOrThrow(_stmt, "gpsLat")
        val _columnIndexOfGpsLng: Int = getColumnIndexOrThrow(_stmt, "gpsLng")
        val _columnIndexOfSellerPhotoUrl: Int = getColumnIndexOrThrow(_stmt, "sellerPhotoUrl")
        val _columnIndexOfBuyerPhotoUrl: Int = getColumnIndexOrThrow(_stmt, "buyerPhotoUrl")
        val _columnIndexOfTimeoutAt: Int = getColumnIndexOrThrow(_stmt, "timeoutAt")
        val _columnIndexOfConditionsJson: Int = getColumnIndexOrThrow(_stmt, "conditionsJson")
        val _columnIndexOfTransferCode: Int = getColumnIndexOrThrow(_stmt, "transferCode")
        val _columnIndexOfLineageSnapshotJson: Int = getColumnIndexOrThrow(_stmt,
            "lineageSnapshotJson")
        val _columnIndexOfClaimedAt: Int = getColumnIndexOrThrow(_stmt, "claimedAt")
        val _columnIndexOfTransferType: Int = getColumnIndexOrThrow(_stmt, "transferType")
        val _columnIndexOfGrowthSnapshotJson: Int = getColumnIndexOrThrow(_stmt,
            "growthSnapshotJson")
        val _columnIndexOfHealthSnapshotJson: Int = getColumnIndexOrThrow(_stmt,
            "healthSnapshotJson")
        val _columnIndexOfTransferCodeExpiresAt: Int = getColumnIndexOrThrow(_stmt,
            "transferCodeExpiresAt")
        val _columnIndexOfInitiatedAt: Int = getColumnIndexOrThrow(_stmt, "initiatedAt")
        val _columnIndexOfCompletedAt: Int = getColumnIndexOrThrow(_stmt, "completedAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfLastModifiedAt: Int = getColumnIndexOrThrow(_stmt, "lastModifiedAt")
        val _columnIndexOfIsDeleted: Int = getColumnIndexOrThrow(_stmt, "isDeleted")
        val _columnIndexOfDeletedAt: Int = getColumnIndexOrThrow(_stmt, "deletedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _columnIndexOfMergedAt: Int = getColumnIndexOrThrow(_stmt, "mergedAt")
        val _columnIndexOfMergeCount: Int = getColumnIndexOrThrow(_stmt, "mergeCount")
        val _result: MutableList<TransferEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: TransferEntity
          val _tmpTransferId: String
          _tmpTransferId = _stmt.getText(_columnIndexOfTransferId)
          val _tmpProductId: String?
          if (_stmt.isNull(_columnIndexOfProductId)) {
            _tmpProductId = null
          } else {
            _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          }
          val _tmpFromUserId: String?
          if (_stmt.isNull(_columnIndexOfFromUserId)) {
            _tmpFromUserId = null
          } else {
            _tmpFromUserId = _stmt.getText(_columnIndexOfFromUserId)
          }
          val _tmpToUserId: String?
          if (_stmt.isNull(_columnIndexOfToUserId)) {
            _tmpToUserId = null
          } else {
            _tmpToUserId = _stmt.getText(_columnIndexOfToUserId)
          }
          val _tmpOrderId: String?
          if (_stmt.isNull(_columnIndexOfOrderId)) {
            _tmpOrderId = null
          } else {
            _tmpOrderId = _stmt.getText(_columnIndexOfOrderId)
          }
          val _tmpAmount: Double
          _tmpAmount = _stmt.getDouble(_columnIndexOfAmount)
          val _tmpCurrency: String
          _tmpCurrency = _stmt.getText(_columnIndexOfCurrency)
          val _tmpType: String
          _tmpType = _stmt.getText(_columnIndexOfType)
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpTransactionReference: String?
          if (_stmt.isNull(_columnIndexOfTransactionReference)) {
            _tmpTransactionReference = null
          } else {
            _tmpTransactionReference = _stmt.getText(_columnIndexOfTransactionReference)
          }
          val _tmpNotes: String?
          if (_stmt.isNull(_columnIndexOfNotes)) {
            _tmpNotes = null
          } else {
            _tmpNotes = _stmt.getText(_columnIndexOfNotes)
          }
          val _tmpGpsLat: Double?
          if (_stmt.isNull(_columnIndexOfGpsLat)) {
            _tmpGpsLat = null
          } else {
            _tmpGpsLat = _stmt.getDouble(_columnIndexOfGpsLat)
          }
          val _tmpGpsLng: Double?
          if (_stmt.isNull(_columnIndexOfGpsLng)) {
            _tmpGpsLng = null
          } else {
            _tmpGpsLng = _stmt.getDouble(_columnIndexOfGpsLng)
          }
          val _tmpSellerPhotoUrl: String?
          if (_stmt.isNull(_columnIndexOfSellerPhotoUrl)) {
            _tmpSellerPhotoUrl = null
          } else {
            _tmpSellerPhotoUrl = _stmt.getText(_columnIndexOfSellerPhotoUrl)
          }
          val _tmpBuyerPhotoUrl: String?
          if (_stmt.isNull(_columnIndexOfBuyerPhotoUrl)) {
            _tmpBuyerPhotoUrl = null
          } else {
            _tmpBuyerPhotoUrl = _stmt.getText(_columnIndexOfBuyerPhotoUrl)
          }
          val _tmpTimeoutAt: Long?
          if (_stmt.isNull(_columnIndexOfTimeoutAt)) {
            _tmpTimeoutAt = null
          } else {
            _tmpTimeoutAt = _stmt.getLong(_columnIndexOfTimeoutAt)
          }
          val _tmpConditionsJson: String?
          if (_stmt.isNull(_columnIndexOfConditionsJson)) {
            _tmpConditionsJson = null
          } else {
            _tmpConditionsJson = _stmt.getText(_columnIndexOfConditionsJson)
          }
          val _tmpTransferCode: String?
          if (_stmt.isNull(_columnIndexOfTransferCode)) {
            _tmpTransferCode = null
          } else {
            _tmpTransferCode = _stmt.getText(_columnIndexOfTransferCode)
          }
          val _tmpLineageSnapshotJson: String?
          if (_stmt.isNull(_columnIndexOfLineageSnapshotJson)) {
            _tmpLineageSnapshotJson = null
          } else {
            _tmpLineageSnapshotJson = _stmt.getText(_columnIndexOfLineageSnapshotJson)
          }
          val _tmpClaimedAt: Long?
          if (_stmt.isNull(_columnIndexOfClaimedAt)) {
            _tmpClaimedAt = null
          } else {
            _tmpClaimedAt = _stmt.getLong(_columnIndexOfClaimedAt)
          }
          val _tmpTransferType: String
          _tmpTransferType = _stmt.getText(_columnIndexOfTransferType)
          val _tmpGrowthSnapshotJson: String?
          if (_stmt.isNull(_columnIndexOfGrowthSnapshotJson)) {
            _tmpGrowthSnapshotJson = null
          } else {
            _tmpGrowthSnapshotJson = _stmt.getText(_columnIndexOfGrowthSnapshotJson)
          }
          val _tmpHealthSnapshotJson: String?
          if (_stmt.isNull(_columnIndexOfHealthSnapshotJson)) {
            _tmpHealthSnapshotJson = null
          } else {
            _tmpHealthSnapshotJson = _stmt.getText(_columnIndexOfHealthSnapshotJson)
          }
          val _tmpTransferCodeExpiresAt: Long?
          if (_stmt.isNull(_columnIndexOfTransferCodeExpiresAt)) {
            _tmpTransferCodeExpiresAt = null
          } else {
            _tmpTransferCodeExpiresAt = _stmt.getLong(_columnIndexOfTransferCodeExpiresAt)
          }
          val _tmpInitiatedAt: Long
          _tmpInitiatedAt = _stmt.getLong(_columnIndexOfInitiatedAt)
          val _tmpCompletedAt: Long?
          if (_stmt.isNull(_columnIndexOfCompletedAt)) {
            _tmpCompletedAt = null
          } else {
            _tmpCompletedAt = _stmt.getLong(_columnIndexOfCompletedAt)
          }
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpLastModifiedAt: Long
          _tmpLastModifiedAt = _stmt.getLong(_columnIndexOfLastModifiedAt)
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
          val _tmpSyncedAt: Long?
          if (_stmt.isNull(_columnIndexOfSyncedAt)) {
            _tmpSyncedAt = null
          } else {
            _tmpSyncedAt = _stmt.getLong(_columnIndexOfSyncedAt)
          }
          val _tmpMergedAt: Long?
          if (_stmt.isNull(_columnIndexOfMergedAt)) {
            _tmpMergedAt = null
          } else {
            _tmpMergedAt = _stmt.getLong(_columnIndexOfMergedAt)
          }
          val _tmpMergeCount: Int
          _tmpMergeCount = _stmt.getLong(_columnIndexOfMergeCount).toInt()
          _item =
              TransferEntity(_tmpTransferId,_tmpProductId,_tmpFromUserId,_tmpToUserId,_tmpOrderId,_tmpAmount,_tmpCurrency,_tmpType,_tmpStatus,_tmpTransactionReference,_tmpNotes,_tmpGpsLat,_tmpGpsLng,_tmpSellerPhotoUrl,_tmpBuyerPhotoUrl,_tmpTimeoutAt,_tmpConditionsJson,_tmpTransferCode,_tmpLineageSnapshotJson,_tmpClaimedAt,_tmpTransferType,_tmpGrowthSnapshotJson,_tmpHealthSnapshotJson,_tmpTransferCodeExpiresAt,_tmpInitiatedAt,_tmpCompletedAt,_tmpUpdatedAt,_tmpLastModifiedAt,_tmpIsDeleted,_tmpDeletedAt,_tmpDirty,_tmpSyncedAt,_tmpMergedAt,_tmpMergeCount)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun findByToUserAndStatus(userId: String, status: String):
      List<TransferEntity> {
    val _sql: String =
        "SELECT * FROM transfers WHERE toUserId = ? AND status = ? AND isDeleted = 0 ORDER BY initiatedAt DESC"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, userId)
        _argIndex = 2
        _stmt.bindText(_argIndex, status)
        val _columnIndexOfTransferId: Int = getColumnIndexOrThrow(_stmt, "transferId")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfFromUserId: Int = getColumnIndexOrThrow(_stmt, "fromUserId")
        val _columnIndexOfToUserId: Int = getColumnIndexOrThrow(_stmt, "toUserId")
        val _columnIndexOfOrderId: Int = getColumnIndexOrThrow(_stmt, "orderId")
        val _columnIndexOfAmount: Int = getColumnIndexOrThrow(_stmt, "amount")
        val _columnIndexOfCurrency: Int = getColumnIndexOrThrow(_stmt, "currency")
        val _columnIndexOfType: Int = getColumnIndexOrThrow(_stmt, "type")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfTransactionReference: Int = getColumnIndexOrThrow(_stmt,
            "transactionReference")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfGpsLat: Int = getColumnIndexOrThrow(_stmt, "gpsLat")
        val _columnIndexOfGpsLng: Int = getColumnIndexOrThrow(_stmt, "gpsLng")
        val _columnIndexOfSellerPhotoUrl: Int = getColumnIndexOrThrow(_stmt, "sellerPhotoUrl")
        val _columnIndexOfBuyerPhotoUrl: Int = getColumnIndexOrThrow(_stmt, "buyerPhotoUrl")
        val _columnIndexOfTimeoutAt: Int = getColumnIndexOrThrow(_stmt, "timeoutAt")
        val _columnIndexOfConditionsJson: Int = getColumnIndexOrThrow(_stmt, "conditionsJson")
        val _columnIndexOfTransferCode: Int = getColumnIndexOrThrow(_stmt, "transferCode")
        val _columnIndexOfLineageSnapshotJson: Int = getColumnIndexOrThrow(_stmt,
            "lineageSnapshotJson")
        val _columnIndexOfClaimedAt: Int = getColumnIndexOrThrow(_stmt, "claimedAt")
        val _columnIndexOfTransferType: Int = getColumnIndexOrThrow(_stmt, "transferType")
        val _columnIndexOfGrowthSnapshotJson: Int = getColumnIndexOrThrow(_stmt,
            "growthSnapshotJson")
        val _columnIndexOfHealthSnapshotJson: Int = getColumnIndexOrThrow(_stmt,
            "healthSnapshotJson")
        val _columnIndexOfTransferCodeExpiresAt: Int = getColumnIndexOrThrow(_stmt,
            "transferCodeExpiresAt")
        val _columnIndexOfInitiatedAt: Int = getColumnIndexOrThrow(_stmt, "initiatedAt")
        val _columnIndexOfCompletedAt: Int = getColumnIndexOrThrow(_stmt, "completedAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfLastModifiedAt: Int = getColumnIndexOrThrow(_stmt, "lastModifiedAt")
        val _columnIndexOfIsDeleted: Int = getColumnIndexOrThrow(_stmt, "isDeleted")
        val _columnIndexOfDeletedAt: Int = getColumnIndexOrThrow(_stmt, "deletedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _columnIndexOfMergedAt: Int = getColumnIndexOrThrow(_stmt, "mergedAt")
        val _columnIndexOfMergeCount: Int = getColumnIndexOrThrow(_stmt, "mergeCount")
        val _result: MutableList<TransferEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: TransferEntity
          val _tmpTransferId: String
          _tmpTransferId = _stmt.getText(_columnIndexOfTransferId)
          val _tmpProductId: String?
          if (_stmt.isNull(_columnIndexOfProductId)) {
            _tmpProductId = null
          } else {
            _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          }
          val _tmpFromUserId: String?
          if (_stmt.isNull(_columnIndexOfFromUserId)) {
            _tmpFromUserId = null
          } else {
            _tmpFromUserId = _stmt.getText(_columnIndexOfFromUserId)
          }
          val _tmpToUserId: String?
          if (_stmt.isNull(_columnIndexOfToUserId)) {
            _tmpToUserId = null
          } else {
            _tmpToUserId = _stmt.getText(_columnIndexOfToUserId)
          }
          val _tmpOrderId: String?
          if (_stmt.isNull(_columnIndexOfOrderId)) {
            _tmpOrderId = null
          } else {
            _tmpOrderId = _stmt.getText(_columnIndexOfOrderId)
          }
          val _tmpAmount: Double
          _tmpAmount = _stmt.getDouble(_columnIndexOfAmount)
          val _tmpCurrency: String
          _tmpCurrency = _stmt.getText(_columnIndexOfCurrency)
          val _tmpType: String
          _tmpType = _stmt.getText(_columnIndexOfType)
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpTransactionReference: String?
          if (_stmt.isNull(_columnIndexOfTransactionReference)) {
            _tmpTransactionReference = null
          } else {
            _tmpTransactionReference = _stmt.getText(_columnIndexOfTransactionReference)
          }
          val _tmpNotes: String?
          if (_stmt.isNull(_columnIndexOfNotes)) {
            _tmpNotes = null
          } else {
            _tmpNotes = _stmt.getText(_columnIndexOfNotes)
          }
          val _tmpGpsLat: Double?
          if (_stmt.isNull(_columnIndexOfGpsLat)) {
            _tmpGpsLat = null
          } else {
            _tmpGpsLat = _stmt.getDouble(_columnIndexOfGpsLat)
          }
          val _tmpGpsLng: Double?
          if (_stmt.isNull(_columnIndexOfGpsLng)) {
            _tmpGpsLng = null
          } else {
            _tmpGpsLng = _stmt.getDouble(_columnIndexOfGpsLng)
          }
          val _tmpSellerPhotoUrl: String?
          if (_stmt.isNull(_columnIndexOfSellerPhotoUrl)) {
            _tmpSellerPhotoUrl = null
          } else {
            _tmpSellerPhotoUrl = _stmt.getText(_columnIndexOfSellerPhotoUrl)
          }
          val _tmpBuyerPhotoUrl: String?
          if (_stmt.isNull(_columnIndexOfBuyerPhotoUrl)) {
            _tmpBuyerPhotoUrl = null
          } else {
            _tmpBuyerPhotoUrl = _stmt.getText(_columnIndexOfBuyerPhotoUrl)
          }
          val _tmpTimeoutAt: Long?
          if (_stmt.isNull(_columnIndexOfTimeoutAt)) {
            _tmpTimeoutAt = null
          } else {
            _tmpTimeoutAt = _stmt.getLong(_columnIndexOfTimeoutAt)
          }
          val _tmpConditionsJson: String?
          if (_stmt.isNull(_columnIndexOfConditionsJson)) {
            _tmpConditionsJson = null
          } else {
            _tmpConditionsJson = _stmt.getText(_columnIndexOfConditionsJson)
          }
          val _tmpTransferCode: String?
          if (_stmt.isNull(_columnIndexOfTransferCode)) {
            _tmpTransferCode = null
          } else {
            _tmpTransferCode = _stmt.getText(_columnIndexOfTransferCode)
          }
          val _tmpLineageSnapshotJson: String?
          if (_stmt.isNull(_columnIndexOfLineageSnapshotJson)) {
            _tmpLineageSnapshotJson = null
          } else {
            _tmpLineageSnapshotJson = _stmt.getText(_columnIndexOfLineageSnapshotJson)
          }
          val _tmpClaimedAt: Long?
          if (_stmt.isNull(_columnIndexOfClaimedAt)) {
            _tmpClaimedAt = null
          } else {
            _tmpClaimedAt = _stmt.getLong(_columnIndexOfClaimedAt)
          }
          val _tmpTransferType: String
          _tmpTransferType = _stmt.getText(_columnIndexOfTransferType)
          val _tmpGrowthSnapshotJson: String?
          if (_stmt.isNull(_columnIndexOfGrowthSnapshotJson)) {
            _tmpGrowthSnapshotJson = null
          } else {
            _tmpGrowthSnapshotJson = _stmt.getText(_columnIndexOfGrowthSnapshotJson)
          }
          val _tmpHealthSnapshotJson: String?
          if (_stmt.isNull(_columnIndexOfHealthSnapshotJson)) {
            _tmpHealthSnapshotJson = null
          } else {
            _tmpHealthSnapshotJson = _stmt.getText(_columnIndexOfHealthSnapshotJson)
          }
          val _tmpTransferCodeExpiresAt: Long?
          if (_stmt.isNull(_columnIndexOfTransferCodeExpiresAt)) {
            _tmpTransferCodeExpiresAt = null
          } else {
            _tmpTransferCodeExpiresAt = _stmt.getLong(_columnIndexOfTransferCodeExpiresAt)
          }
          val _tmpInitiatedAt: Long
          _tmpInitiatedAt = _stmt.getLong(_columnIndexOfInitiatedAt)
          val _tmpCompletedAt: Long?
          if (_stmt.isNull(_columnIndexOfCompletedAt)) {
            _tmpCompletedAt = null
          } else {
            _tmpCompletedAt = _stmt.getLong(_columnIndexOfCompletedAt)
          }
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpLastModifiedAt: Long
          _tmpLastModifiedAt = _stmt.getLong(_columnIndexOfLastModifiedAt)
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
          val _tmpSyncedAt: Long?
          if (_stmt.isNull(_columnIndexOfSyncedAt)) {
            _tmpSyncedAt = null
          } else {
            _tmpSyncedAt = _stmt.getLong(_columnIndexOfSyncedAt)
          }
          val _tmpMergedAt: Long?
          if (_stmt.isNull(_columnIndexOfMergedAt)) {
            _tmpMergedAt = null
          } else {
            _tmpMergedAt = _stmt.getLong(_columnIndexOfMergedAt)
          }
          val _tmpMergeCount: Int
          _tmpMergeCount = _stmt.getLong(_columnIndexOfMergeCount).toInt()
          _item =
              TransferEntity(_tmpTransferId,_tmpProductId,_tmpFromUserId,_tmpToUserId,_tmpOrderId,_tmpAmount,_tmpCurrency,_tmpType,_tmpStatus,_tmpTransactionReference,_tmpNotes,_tmpGpsLat,_tmpGpsLng,_tmpSellerPhotoUrl,_tmpBuyerPhotoUrl,_tmpTimeoutAt,_tmpConditionsJson,_tmpTransferCode,_tmpLineageSnapshotJson,_tmpClaimedAt,_tmpTransferType,_tmpGrowthSnapshotJson,_tmpHealthSnapshotJson,_tmpTransferCodeExpiresAt,_tmpInitiatedAt,_tmpCompletedAt,_tmpUpdatedAt,_tmpLastModifiedAt,_tmpIsDeleted,_tmpDeletedAt,_tmpDirty,_tmpSyncedAt,_tmpMergedAt,_tmpMergeCount)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getExpiredEnthusiastTransfers(now: Long): List<TransferEntity> {
    val _sql: String =
        "SELECT * FROM transfers WHERE type = 'ENTHUSIAST_TRANSFER' AND status = 'PENDING' AND transferCodeExpiresAt IS NOT NULL AND transferCodeExpiresAt < ? ORDER BY transferCodeExpiresAt ASC"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, now)
        val _columnIndexOfTransferId: Int = getColumnIndexOrThrow(_stmt, "transferId")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfFromUserId: Int = getColumnIndexOrThrow(_stmt, "fromUserId")
        val _columnIndexOfToUserId: Int = getColumnIndexOrThrow(_stmt, "toUserId")
        val _columnIndexOfOrderId: Int = getColumnIndexOrThrow(_stmt, "orderId")
        val _columnIndexOfAmount: Int = getColumnIndexOrThrow(_stmt, "amount")
        val _columnIndexOfCurrency: Int = getColumnIndexOrThrow(_stmt, "currency")
        val _columnIndexOfType: Int = getColumnIndexOrThrow(_stmt, "type")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfTransactionReference: Int = getColumnIndexOrThrow(_stmt,
            "transactionReference")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfGpsLat: Int = getColumnIndexOrThrow(_stmt, "gpsLat")
        val _columnIndexOfGpsLng: Int = getColumnIndexOrThrow(_stmt, "gpsLng")
        val _columnIndexOfSellerPhotoUrl: Int = getColumnIndexOrThrow(_stmt, "sellerPhotoUrl")
        val _columnIndexOfBuyerPhotoUrl: Int = getColumnIndexOrThrow(_stmt, "buyerPhotoUrl")
        val _columnIndexOfTimeoutAt: Int = getColumnIndexOrThrow(_stmt, "timeoutAt")
        val _columnIndexOfConditionsJson: Int = getColumnIndexOrThrow(_stmt, "conditionsJson")
        val _columnIndexOfTransferCode: Int = getColumnIndexOrThrow(_stmt, "transferCode")
        val _columnIndexOfLineageSnapshotJson: Int = getColumnIndexOrThrow(_stmt,
            "lineageSnapshotJson")
        val _columnIndexOfClaimedAt: Int = getColumnIndexOrThrow(_stmt, "claimedAt")
        val _columnIndexOfTransferType: Int = getColumnIndexOrThrow(_stmt, "transferType")
        val _columnIndexOfGrowthSnapshotJson: Int = getColumnIndexOrThrow(_stmt,
            "growthSnapshotJson")
        val _columnIndexOfHealthSnapshotJson: Int = getColumnIndexOrThrow(_stmt,
            "healthSnapshotJson")
        val _columnIndexOfTransferCodeExpiresAt: Int = getColumnIndexOrThrow(_stmt,
            "transferCodeExpiresAt")
        val _columnIndexOfInitiatedAt: Int = getColumnIndexOrThrow(_stmt, "initiatedAt")
        val _columnIndexOfCompletedAt: Int = getColumnIndexOrThrow(_stmt, "completedAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfLastModifiedAt: Int = getColumnIndexOrThrow(_stmt, "lastModifiedAt")
        val _columnIndexOfIsDeleted: Int = getColumnIndexOrThrow(_stmt, "isDeleted")
        val _columnIndexOfDeletedAt: Int = getColumnIndexOrThrow(_stmt, "deletedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _columnIndexOfMergedAt: Int = getColumnIndexOrThrow(_stmt, "mergedAt")
        val _columnIndexOfMergeCount: Int = getColumnIndexOrThrow(_stmt, "mergeCount")
        val _result: MutableList<TransferEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: TransferEntity
          val _tmpTransferId: String
          _tmpTransferId = _stmt.getText(_columnIndexOfTransferId)
          val _tmpProductId: String?
          if (_stmt.isNull(_columnIndexOfProductId)) {
            _tmpProductId = null
          } else {
            _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          }
          val _tmpFromUserId: String?
          if (_stmt.isNull(_columnIndexOfFromUserId)) {
            _tmpFromUserId = null
          } else {
            _tmpFromUserId = _stmt.getText(_columnIndexOfFromUserId)
          }
          val _tmpToUserId: String?
          if (_stmt.isNull(_columnIndexOfToUserId)) {
            _tmpToUserId = null
          } else {
            _tmpToUserId = _stmt.getText(_columnIndexOfToUserId)
          }
          val _tmpOrderId: String?
          if (_stmt.isNull(_columnIndexOfOrderId)) {
            _tmpOrderId = null
          } else {
            _tmpOrderId = _stmt.getText(_columnIndexOfOrderId)
          }
          val _tmpAmount: Double
          _tmpAmount = _stmt.getDouble(_columnIndexOfAmount)
          val _tmpCurrency: String
          _tmpCurrency = _stmt.getText(_columnIndexOfCurrency)
          val _tmpType: String
          _tmpType = _stmt.getText(_columnIndexOfType)
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpTransactionReference: String?
          if (_stmt.isNull(_columnIndexOfTransactionReference)) {
            _tmpTransactionReference = null
          } else {
            _tmpTransactionReference = _stmt.getText(_columnIndexOfTransactionReference)
          }
          val _tmpNotes: String?
          if (_stmt.isNull(_columnIndexOfNotes)) {
            _tmpNotes = null
          } else {
            _tmpNotes = _stmt.getText(_columnIndexOfNotes)
          }
          val _tmpGpsLat: Double?
          if (_stmt.isNull(_columnIndexOfGpsLat)) {
            _tmpGpsLat = null
          } else {
            _tmpGpsLat = _stmt.getDouble(_columnIndexOfGpsLat)
          }
          val _tmpGpsLng: Double?
          if (_stmt.isNull(_columnIndexOfGpsLng)) {
            _tmpGpsLng = null
          } else {
            _tmpGpsLng = _stmt.getDouble(_columnIndexOfGpsLng)
          }
          val _tmpSellerPhotoUrl: String?
          if (_stmt.isNull(_columnIndexOfSellerPhotoUrl)) {
            _tmpSellerPhotoUrl = null
          } else {
            _tmpSellerPhotoUrl = _stmt.getText(_columnIndexOfSellerPhotoUrl)
          }
          val _tmpBuyerPhotoUrl: String?
          if (_stmt.isNull(_columnIndexOfBuyerPhotoUrl)) {
            _tmpBuyerPhotoUrl = null
          } else {
            _tmpBuyerPhotoUrl = _stmt.getText(_columnIndexOfBuyerPhotoUrl)
          }
          val _tmpTimeoutAt: Long?
          if (_stmt.isNull(_columnIndexOfTimeoutAt)) {
            _tmpTimeoutAt = null
          } else {
            _tmpTimeoutAt = _stmt.getLong(_columnIndexOfTimeoutAt)
          }
          val _tmpConditionsJson: String?
          if (_stmt.isNull(_columnIndexOfConditionsJson)) {
            _tmpConditionsJson = null
          } else {
            _tmpConditionsJson = _stmt.getText(_columnIndexOfConditionsJson)
          }
          val _tmpTransferCode: String?
          if (_stmt.isNull(_columnIndexOfTransferCode)) {
            _tmpTransferCode = null
          } else {
            _tmpTransferCode = _stmt.getText(_columnIndexOfTransferCode)
          }
          val _tmpLineageSnapshotJson: String?
          if (_stmt.isNull(_columnIndexOfLineageSnapshotJson)) {
            _tmpLineageSnapshotJson = null
          } else {
            _tmpLineageSnapshotJson = _stmt.getText(_columnIndexOfLineageSnapshotJson)
          }
          val _tmpClaimedAt: Long?
          if (_stmt.isNull(_columnIndexOfClaimedAt)) {
            _tmpClaimedAt = null
          } else {
            _tmpClaimedAt = _stmt.getLong(_columnIndexOfClaimedAt)
          }
          val _tmpTransferType: String
          _tmpTransferType = _stmt.getText(_columnIndexOfTransferType)
          val _tmpGrowthSnapshotJson: String?
          if (_stmt.isNull(_columnIndexOfGrowthSnapshotJson)) {
            _tmpGrowthSnapshotJson = null
          } else {
            _tmpGrowthSnapshotJson = _stmt.getText(_columnIndexOfGrowthSnapshotJson)
          }
          val _tmpHealthSnapshotJson: String?
          if (_stmt.isNull(_columnIndexOfHealthSnapshotJson)) {
            _tmpHealthSnapshotJson = null
          } else {
            _tmpHealthSnapshotJson = _stmt.getText(_columnIndexOfHealthSnapshotJson)
          }
          val _tmpTransferCodeExpiresAt: Long?
          if (_stmt.isNull(_columnIndexOfTransferCodeExpiresAt)) {
            _tmpTransferCodeExpiresAt = null
          } else {
            _tmpTransferCodeExpiresAt = _stmt.getLong(_columnIndexOfTransferCodeExpiresAt)
          }
          val _tmpInitiatedAt: Long
          _tmpInitiatedAt = _stmt.getLong(_columnIndexOfInitiatedAt)
          val _tmpCompletedAt: Long?
          if (_stmt.isNull(_columnIndexOfCompletedAt)) {
            _tmpCompletedAt = null
          } else {
            _tmpCompletedAt = _stmt.getLong(_columnIndexOfCompletedAt)
          }
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpLastModifiedAt: Long
          _tmpLastModifiedAt = _stmt.getLong(_columnIndexOfLastModifiedAt)
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
          val _tmpSyncedAt: Long?
          if (_stmt.isNull(_columnIndexOfSyncedAt)) {
            _tmpSyncedAt = null
          } else {
            _tmpSyncedAt = _stmt.getLong(_columnIndexOfSyncedAt)
          }
          val _tmpMergedAt: Long?
          if (_stmt.isNull(_columnIndexOfMergedAt)) {
            _tmpMergedAt = null
          } else {
            _tmpMergedAt = _stmt.getLong(_columnIndexOfMergedAt)
          }
          val _tmpMergeCount: Int
          _tmpMergeCount = _stmt.getLong(_columnIndexOfMergeCount).toInt()
          _item =
              TransferEntity(_tmpTransferId,_tmpProductId,_tmpFromUserId,_tmpToUserId,_tmpOrderId,_tmpAmount,_tmpCurrency,_tmpType,_tmpStatus,_tmpTransactionReference,_tmpNotes,_tmpGpsLat,_tmpGpsLng,_tmpSellerPhotoUrl,_tmpBuyerPhotoUrl,_tmpTimeoutAt,_tmpConditionsJson,_tmpTransferCode,_tmpLineageSnapshotJson,_tmpClaimedAt,_tmpTransferType,_tmpGrowthSnapshotJson,_tmpHealthSnapshotJson,_tmpTransferCodeExpiresAt,_tmpInitiatedAt,_tmpCompletedAt,_tmpUpdatedAt,_tmpLastModifiedAt,_tmpIsDeleted,_tmpDeletedAt,_tmpDirty,_tmpSyncedAt,_tmpMergedAt,_tmpMergeCount)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun deleteTransferById(transferId: String) {
    val _sql: String = "DELETE FROM transfers WHERE transferId = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, transferId)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun deleteAllTransfers() {
    val _sql: String = "DELETE FROM transfers"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun purgeDeleted() {
    val _sql: String = "DELETE FROM transfers WHERE isDeleted = 1"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun updateStatusAndTimestamps(
    transferId: String,
    status: String,
    updatedAt: Long,
    completedAt: Long?,
  ) {
    val _sql: String =
        "UPDATE transfers SET status = ?, updatedAt = ?, completedAt = ? WHERE transferId = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, status)
        _argIndex = 2
        _stmt.bindLong(_argIndex, updatedAt)
        _argIndex = 3
        if (completedAt == null) {
          _stmt.bindNull(_argIndex)
        } else {
          _stmt.bindLong(_argIndex, completedAt)
        }
        _argIndex = 4
        _stmt.bindText(_argIndex, transferId)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun markTimedOut(ids: List<String>, updatedAt: Long) {
    val _stringBuilder: StringBuilder = StringBuilder()
    _stringBuilder.append("UPDATE transfers SET status = 'TIMEOUT', updatedAt = ")
    _stringBuilder.append("?")
    _stringBuilder.append(" WHERE transferId IN (")
    val _inputSize: Int = ids.size
    appendPlaceholders(_stringBuilder, _inputSize)
    _stringBuilder.append(")")
    val _sql: String = _stringBuilder.toString()
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, updatedAt)
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

  public override suspend fun clearDirty(transferIds: List<String>, syncedAt: Long) {
    val _stringBuilder: StringBuilder = StringBuilder()
    _stringBuilder.append("UPDATE transfers SET dirty = 0, syncedAt = ")
    _stringBuilder.append("?")
    _stringBuilder.append(" WHERE transferId IN (")
    val _inputSize: Int = transferIds.size
    appendPlaceholders(_stringBuilder, _inputSize)
    _stringBuilder.append(")")
    val _sql: String = _stringBuilder.toString()
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, syncedAt)
        _argIndex = 2
        for (_item: String in transferIds) {
          _stmt.bindText(_argIndex, _item)
          _argIndex++
        }
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun deleteCompletedBefore(cutoff: Long): Int {
    val _sql: String =
        "DELETE FROM transfers WHERE status IN ('COMPLETED', 'FAILED', 'CANCELLED', 'TIMEOUT') AND completedAt < ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, cutoff)
        _stmt.step()
        getTotalChangedRows(_connection)
      } finally {
        _stmt.close()
      }
    }
  }

  public companion object {
    public fun getRequiredConverters(): List<KClass<*>> = emptyList()
  }
}
