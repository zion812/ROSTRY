package com.rio.rostry.`data`.database.dao

import androidx.room.EntityDeleteOrUpdateAdapter
import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.getTotalChangedRows
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.rio.rostry.`data`.database.entity.AuctionEntity
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
public class AuctionDao_Impl(
  __db: RoomDatabase,
) : AuctionDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfAuctionEntity: EntityInsertAdapter<AuctionEntity>

  private val __updateAdapterOfAuctionEntity: EntityDeleteOrUpdateAdapter<AuctionEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfAuctionEntity = object : EntityInsertAdapter<AuctionEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `auctions` (`auctionId`,`productId`,`sellerId`,`startsAt`,`endsAt`,`closedAt`,`closedBy`,`minPrice`,`currentPrice`,`reservePrice`,`buyNowPrice`,`bidIncrement`,`bidCount`,`winnerId`,`isReserveMet`,`extensionCount`,`maxExtensions`,`extensionMinutes`,`status`,`isActive`,`viewCount`,`createdAt`,`updatedAt`,`dirty`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: AuctionEntity) {
        statement.bindText(1, entity.auctionId)
        statement.bindText(2, entity.productId)
        statement.bindText(3, entity.sellerId)
        statement.bindLong(4, entity.startsAt)
        statement.bindLong(5, entity.endsAt)
        val _tmpClosedAt: Long? = entity.closedAt
        if (_tmpClosedAt == null) {
          statement.bindNull(6)
        } else {
          statement.bindLong(6, _tmpClosedAt)
        }
        val _tmpClosedBy: String? = entity.closedBy
        if (_tmpClosedBy == null) {
          statement.bindNull(7)
        } else {
          statement.bindText(7, _tmpClosedBy)
        }
        statement.bindDouble(8, entity.minPrice)
        statement.bindDouble(9, entity.currentPrice)
        val _tmpReservePrice: Double? = entity.reservePrice
        if (_tmpReservePrice == null) {
          statement.bindNull(10)
        } else {
          statement.bindDouble(10, _tmpReservePrice)
        }
        val _tmpBuyNowPrice: Double? = entity.buyNowPrice
        if (_tmpBuyNowPrice == null) {
          statement.bindNull(11)
        } else {
          statement.bindDouble(11, _tmpBuyNowPrice)
        }
        statement.bindDouble(12, entity.bidIncrement)
        statement.bindLong(13, entity.bidCount.toLong())
        val _tmpWinnerId: String? = entity.winnerId
        if (_tmpWinnerId == null) {
          statement.bindNull(14)
        } else {
          statement.bindText(14, _tmpWinnerId)
        }
        val _tmp: Int = if (entity.isReserveMet) 1 else 0
        statement.bindLong(15, _tmp.toLong())
        statement.bindLong(16, entity.extensionCount.toLong())
        statement.bindLong(17, entity.maxExtensions.toLong())
        statement.bindLong(18, entity.extensionMinutes.toLong())
        statement.bindText(19, entity.status)
        val _tmp_1: Int = if (entity.isActive) 1 else 0
        statement.bindLong(20, _tmp_1.toLong())
        statement.bindLong(21, entity.viewCount.toLong())
        statement.bindLong(22, entity.createdAt)
        statement.bindLong(23, entity.updatedAt)
        val _tmp_2: Int = if (entity.dirty) 1 else 0
        statement.bindLong(24, _tmp_2.toLong())
      }
    }
    this.__updateAdapterOfAuctionEntity = object : EntityDeleteOrUpdateAdapter<AuctionEntity>() {
      protected override fun createQuery(): String =
          "UPDATE OR ABORT `auctions` SET `auctionId` = ?,`productId` = ?,`sellerId` = ?,`startsAt` = ?,`endsAt` = ?,`closedAt` = ?,`closedBy` = ?,`minPrice` = ?,`currentPrice` = ?,`reservePrice` = ?,`buyNowPrice` = ?,`bidIncrement` = ?,`bidCount` = ?,`winnerId` = ?,`isReserveMet` = ?,`extensionCount` = ?,`maxExtensions` = ?,`extensionMinutes` = ?,`status` = ?,`isActive` = ?,`viewCount` = ?,`createdAt` = ?,`updatedAt` = ?,`dirty` = ? WHERE `auctionId` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: AuctionEntity) {
        statement.bindText(1, entity.auctionId)
        statement.bindText(2, entity.productId)
        statement.bindText(3, entity.sellerId)
        statement.bindLong(4, entity.startsAt)
        statement.bindLong(5, entity.endsAt)
        val _tmpClosedAt: Long? = entity.closedAt
        if (_tmpClosedAt == null) {
          statement.bindNull(6)
        } else {
          statement.bindLong(6, _tmpClosedAt)
        }
        val _tmpClosedBy: String? = entity.closedBy
        if (_tmpClosedBy == null) {
          statement.bindNull(7)
        } else {
          statement.bindText(7, _tmpClosedBy)
        }
        statement.bindDouble(8, entity.minPrice)
        statement.bindDouble(9, entity.currentPrice)
        val _tmpReservePrice: Double? = entity.reservePrice
        if (_tmpReservePrice == null) {
          statement.bindNull(10)
        } else {
          statement.bindDouble(10, _tmpReservePrice)
        }
        val _tmpBuyNowPrice: Double? = entity.buyNowPrice
        if (_tmpBuyNowPrice == null) {
          statement.bindNull(11)
        } else {
          statement.bindDouble(11, _tmpBuyNowPrice)
        }
        statement.bindDouble(12, entity.bidIncrement)
        statement.bindLong(13, entity.bidCount.toLong())
        val _tmpWinnerId: String? = entity.winnerId
        if (_tmpWinnerId == null) {
          statement.bindNull(14)
        } else {
          statement.bindText(14, _tmpWinnerId)
        }
        val _tmp: Int = if (entity.isReserveMet) 1 else 0
        statement.bindLong(15, _tmp.toLong())
        statement.bindLong(16, entity.extensionCount.toLong())
        statement.bindLong(17, entity.maxExtensions.toLong())
        statement.bindLong(18, entity.extensionMinutes.toLong())
        statement.bindText(19, entity.status)
        val _tmp_1: Int = if (entity.isActive) 1 else 0
        statement.bindLong(20, _tmp_1.toLong())
        statement.bindLong(21, entity.viewCount.toLong())
        statement.bindLong(22, entity.createdAt)
        statement.bindLong(23, entity.updatedAt)
        val _tmp_2: Int = if (entity.dirty) 1 else 0
        statement.bindLong(24, _tmp_2.toLong())
        statement.bindText(25, entity.auctionId)
      }
    }
  }

  public override suspend fun upsert(auction: AuctionEntity): Unit = performSuspending(__db, false,
      true) { _connection ->
    __insertAdapterOfAuctionEntity.insert(_connection, auction)
  }

  public override suspend fun update(auction: AuctionEntity): Unit = performSuspending(__db, false,
      true) { _connection ->
    __updateAdapterOfAuctionEntity.handle(_connection, auction)
  }

  public override fun observeById(id: String): Flow<AuctionEntity?> {
    val _sql: String = "SELECT * FROM auctions WHERE auctionId = ?"
    return createFlow(__db, false, arrayOf("auctions")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, id)
        val _columnIndexOfAuctionId: Int = getColumnIndexOrThrow(_stmt, "auctionId")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfSellerId: Int = getColumnIndexOrThrow(_stmt, "sellerId")
        val _columnIndexOfStartsAt: Int = getColumnIndexOrThrow(_stmt, "startsAt")
        val _columnIndexOfEndsAt: Int = getColumnIndexOrThrow(_stmt, "endsAt")
        val _columnIndexOfClosedAt: Int = getColumnIndexOrThrow(_stmt, "closedAt")
        val _columnIndexOfClosedBy: Int = getColumnIndexOrThrow(_stmt, "closedBy")
        val _columnIndexOfMinPrice: Int = getColumnIndexOrThrow(_stmt, "minPrice")
        val _columnIndexOfCurrentPrice: Int = getColumnIndexOrThrow(_stmt, "currentPrice")
        val _columnIndexOfReservePrice: Int = getColumnIndexOrThrow(_stmt, "reservePrice")
        val _columnIndexOfBuyNowPrice: Int = getColumnIndexOrThrow(_stmt, "buyNowPrice")
        val _columnIndexOfBidIncrement: Int = getColumnIndexOrThrow(_stmt, "bidIncrement")
        val _columnIndexOfBidCount: Int = getColumnIndexOrThrow(_stmt, "bidCount")
        val _columnIndexOfWinnerId: Int = getColumnIndexOrThrow(_stmt, "winnerId")
        val _columnIndexOfIsReserveMet: Int = getColumnIndexOrThrow(_stmt, "isReserveMet")
        val _columnIndexOfExtensionCount: Int = getColumnIndexOrThrow(_stmt, "extensionCount")
        val _columnIndexOfMaxExtensions: Int = getColumnIndexOrThrow(_stmt, "maxExtensions")
        val _columnIndexOfExtensionMinutes: Int = getColumnIndexOrThrow(_stmt, "extensionMinutes")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfIsActive: Int = getColumnIndexOrThrow(_stmt, "isActive")
        val _columnIndexOfViewCount: Int = getColumnIndexOrThrow(_stmt, "viewCount")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _result: AuctionEntity?
        if (_stmt.step()) {
          val _tmpAuctionId: String
          _tmpAuctionId = _stmt.getText(_columnIndexOfAuctionId)
          val _tmpProductId: String
          _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          val _tmpSellerId: String
          _tmpSellerId = _stmt.getText(_columnIndexOfSellerId)
          val _tmpStartsAt: Long
          _tmpStartsAt = _stmt.getLong(_columnIndexOfStartsAt)
          val _tmpEndsAt: Long
          _tmpEndsAt = _stmt.getLong(_columnIndexOfEndsAt)
          val _tmpClosedAt: Long?
          if (_stmt.isNull(_columnIndexOfClosedAt)) {
            _tmpClosedAt = null
          } else {
            _tmpClosedAt = _stmt.getLong(_columnIndexOfClosedAt)
          }
          val _tmpClosedBy: String?
          if (_stmt.isNull(_columnIndexOfClosedBy)) {
            _tmpClosedBy = null
          } else {
            _tmpClosedBy = _stmt.getText(_columnIndexOfClosedBy)
          }
          val _tmpMinPrice: Double
          _tmpMinPrice = _stmt.getDouble(_columnIndexOfMinPrice)
          val _tmpCurrentPrice: Double
          _tmpCurrentPrice = _stmt.getDouble(_columnIndexOfCurrentPrice)
          val _tmpReservePrice: Double?
          if (_stmt.isNull(_columnIndexOfReservePrice)) {
            _tmpReservePrice = null
          } else {
            _tmpReservePrice = _stmt.getDouble(_columnIndexOfReservePrice)
          }
          val _tmpBuyNowPrice: Double?
          if (_stmt.isNull(_columnIndexOfBuyNowPrice)) {
            _tmpBuyNowPrice = null
          } else {
            _tmpBuyNowPrice = _stmt.getDouble(_columnIndexOfBuyNowPrice)
          }
          val _tmpBidIncrement: Double
          _tmpBidIncrement = _stmt.getDouble(_columnIndexOfBidIncrement)
          val _tmpBidCount: Int
          _tmpBidCount = _stmt.getLong(_columnIndexOfBidCount).toInt()
          val _tmpWinnerId: String?
          if (_stmt.isNull(_columnIndexOfWinnerId)) {
            _tmpWinnerId = null
          } else {
            _tmpWinnerId = _stmt.getText(_columnIndexOfWinnerId)
          }
          val _tmpIsReserveMet: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsReserveMet).toInt()
          _tmpIsReserveMet = _tmp != 0
          val _tmpExtensionCount: Int
          _tmpExtensionCount = _stmt.getLong(_columnIndexOfExtensionCount).toInt()
          val _tmpMaxExtensions: Int
          _tmpMaxExtensions = _stmt.getLong(_columnIndexOfMaxExtensions).toInt()
          val _tmpExtensionMinutes: Int
          _tmpExtensionMinutes = _stmt.getLong(_columnIndexOfExtensionMinutes).toInt()
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpIsActive: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfIsActive).toInt()
          _tmpIsActive = _tmp_1 != 0
          val _tmpViewCount: Int
          _tmpViewCount = _stmt.getLong(_columnIndexOfViewCount).toInt()
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpDirty: Boolean
          val _tmp_2: Int
          _tmp_2 = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp_2 != 0
          _result =
              AuctionEntity(_tmpAuctionId,_tmpProductId,_tmpSellerId,_tmpStartsAt,_tmpEndsAt,_tmpClosedAt,_tmpClosedBy,_tmpMinPrice,_tmpCurrentPrice,_tmpReservePrice,_tmpBuyNowPrice,_tmpBidIncrement,_tmpBidCount,_tmpWinnerId,_tmpIsReserveMet,_tmpExtensionCount,_tmpMaxExtensions,_tmpExtensionMinutes,_tmpStatus,_tmpIsActive,_tmpViewCount,_tmpCreatedAt,_tmpUpdatedAt,_tmpDirty)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun findById(id: String): AuctionEntity? {
    val _sql: String = "SELECT * FROM auctions WHERE auctionId = ? LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, id)
        val _columnIndexOfAuctionId: Int = getColumnIndexOrThrow(_stmt, "auctionId")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfSellerId: Int = getColumnIndexOrThrow(_stmt, "sellerId")
        val _columnIndexOfStartsAt: Int = getColumnIndexOrThrow(_stmt, "startsAt")
        val _columnIndexOfEndsAt: Int = getColumnIndexOrThrow(_stmt, "endsAt")
        val _columnIndexOfClosedAt: Int = getColumnIndexOrThrow(_stmt, "closedAt")
        val _columnIndexOfClosedBy: Int = getColumnIndexOrThrow(_stmt, "closedBy")
        val _columnIndexOfMinPrice: Int = getColumnIndexOrThrow(_stmt, "minPrice")
        val _columnIndexOfCurrentPrice: Int = getColumnIndexOrThrow(_stmt, "currentPrice")
        val _columnIndexOfReservePrice: Int = getColumnIndexOrThrow(_stmt, "reservePrice")
        val _columnIndexOfBuyNowPrice: Int = getColumnIndexOrThrow(_stmt, "buyNowPrice")
        val _columnIndexOfBidIncrement: Int = getColumnIndexOrThrow(_stmt, "bidIncrement")
        val _columnIndexOfBidCount: Int = getColumnIndexOrThrow(_stmt, "bidCount")
        val _columnIndexOfWinnerId: Int = getColumnIndexOrThrow(_stmt, "winnerId")
        val _columnIndexOfIsReserveMet: Int = getColumnIndexOrThrow(_stmt, "isReserveMet")
        val _columnIndexOfExtensionCount: Int = getColumnIndexOrThrow(_stmt, "extensionCount")
        val _columnIndexOfMaxExtensions: Int = getColumnIndexOrThrow(_stmt, "maxExtensions")
        val _columnIndexOfExtensionMinutes: Int = getColumnIndexOrThrow(_stmt, "extensionMinutes")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfIsActive: Int = getColumnIndexOrThrow(_stmt, "isActive")
        val _columnIndexOfViewCount: Int = getColumnIndexOrThrow(_stmt, "viewCount")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _result: AuctionEntity?
        if (_stmt.step()) {
          val _tmpAuctionId: String
          _tmpAuctionId = _stmt.getText(_columnIndexOfAuctionId)
          val _tmpProductId: String
          _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          val _tmpSellerId: String
          _tmpSellerId = _stmt.getText(_columnIndexOfSellerId)
          val _tmpStartsAt: Long
          _tmpStartsAt = _stmt.getLong(_columnIndexOfStartsAt)
          val _tmpEndsAt: Long
          _tmpEndsAt = _stmt.getLong(_columnIndexOfEndsAt)
          val _tmpClosedAt: Long?
          if (_stmt.isNull(_columnIndexOfClosedAt)) {
            _tmpClosedAt = null
          } else {
            _tmpClosedAt = _stmt.getLong(_columnIndexOfClosedAt)
          }
          val _tmpClosedBy: String?
          if (_stmt.isNull(_columnIndexOfClosedBy)) {
            _tmpClosedBy = null
          } else {
            _tmpClosedBy = _stmt.getText(_columnIndexOfClosedBy)
          }
          val _tmpMinPrice: Double
          _tmpMinPrice = _stmt.getDouble(_columnIndexOfMinPrice)
          val _tmpCurrentPrice: Double
          _tmpCurrentPrice = _stmt.getDouble(_columnIndexOfCurrentPrice)
          val _tmpReservePrice: Double?
          if (_stmt.isNull(_columnIndexOfReservePrice)) {
            _tmpReservePrice = null
          } else {
            _tmpReservePrice = _stmt.getDouble(_columnIndexOfReservePrice)
          }
          val _tmpBuyNowPrice: Double?
          if (_stmt.isNull(_columnIndexOfBuyNowPrice)) {
            _tmpBuyNowPrice = null
          } else {
            _tmpBuyNowPrice = _stmt.getDouble(_columnIndexOfBuyNowPrice)
          }
          val _tmpBidIncrement: Double
          _tmpBidIncrement = _stmt.getDouble(_columnIndexOfBidIncrement)
          val _tmpBidCount: Int
          _tmpBidCount = _stmt.getLong(_columnIndexOfBidCount).toInt()
          val _tmpWinnerId: String?
          if (_stmt.isNull(_columnIndexOfWinnerId)) {
            _tmpWinnerId = null
          } else {
            _tmpWinnerId = _stmt.getText(_columnIndexOfWinnerId)
          }
          val _tmpIsReserveMet: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsReserveMet).toInt()
          _tmpIsReserveMet = _tmp != 0
          val _tmpExtensionCount: Int
          _tmpExtensionCount = _stmt.getLong(_columnIndexOfExtensionCount).toInt()
          val _tmpMaxExtensions: Int
          _tmpMaxExtensions = _stmt.getLong(_columnIndexOfMaxExtensions).toInt()
          val _tmpExtensionMinutes: Int
          _tmpExtensionMinutes = _stmt.getLong(_columnIndexOfExtensionMinutes).toInt()
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpIsActive: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfIsActive).toInt()
          _tmpIsActive = _tmp_1 != 0
          val _tmpViewCount: Int
          _tmpViewCount = _stmt.getLong(_columnIndexOfViewCount).toInt()
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpDirty: Boolean
          val _tmp_2: Int
          _tmp_2 = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp_2 != 0
          _result =
              AuctionEntity(_tmpAuctionId,_tmpProductId,_tmpSellerId,_tmpStartsAt,_tmpEndsAt,_tmpClosedAt,_tmpClosedBy,_tmpMinPrice,_tmpCurrentPrice,_tmpReservePrice,_tmpBuyNowPrice,_tmpBidIncrement,_tmpBidCount,_tmpWinnerId,_tmpIsReserveMet,_tmpExtensionCount,_tmpMaxExtensions,_tmpExtensionMinutes,_tmpStatus,_tmpIsActive,_tmpViewCount,_tmpCreatedAt,_tmpUpdatedAt,_tmpDirty)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun activeAuctions(now: Long): Flow<List<AuctionEntity>> {
    val _sql: String = "SELECT * FROM auctions WHERE isActive = 1 AND startsAt <= ? AND endsAt >= ?"
    return createFlow(__db, false, arrayOf("auctions")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, now)
        _argIndex = 2
        _stmt.bindLong(_argIndex, now)
        val _columnIndexOfAuctionId: Int = getColumnIndexOrThrow(_stmt, "auctionId")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfSellerId: Int = getColumnIndexOrThrow(_stmt, "sellerId")
        val _columnIndexOfStartsAt: Int = getColumnIndexOrThrow(_stmt, "startsAt")
        val _columnIndexOfEndsAt: Int = getColumnIndexOrThrow(_stmt, "endsAt")
        val _columnIndexOfClosedAt: Int = getColumnIndexOrThrow(_stmt, "closedAt")
        val _columnIndexOfClosedBy: Int = getColumnIndexOrThrow(_stmt, "closedBy")
        val _columnIndexOfMinPrice: Int = getColumnIndexOrThrow(_stmt, "minPrice")
        val _columnIndexOfCurrentPrice: Int = getColumnIndexOrThrow(_stmt, "currentPrice")
        val _columnIndexOfReservePrice: Int = getColumnIndexOrThrow(_stmt, "reservePrice")
        val _columnIndexOfBuyNowPrice: Int = getColumnIndexOrThrow(_stmt, "buyNowPrice")
        val _columnIndexOfBidIncrement: Int = getColumnIndexOrThrow(_stmt, "bidIncrement")
        val _columnIndexOfBidCount: Int = getColumnIndexOrThrow(_stmt, "bidCount")
        val _columnIndexOfWinnerId: Int = getColumnIndexOrThrow(_stmt, "winnerId")
        val _columnIndexOfIsReserveMet: Int = getColumnIndexOrThrow(_stmt, "isReserveMet")
        val _columnIndexOfExtensionCount: Int = getColumnIndexOrThrow(_stmt, "extensionCount")
        val _columnIndexOfMaxExtensions: Int = getColumnIndexOrThrow(_stmt, "maxExtensions")
        val _columnIndexOfExtensionMinutes: Int = getColumnIndexOrThrow(_stmt, "extensionMinutes")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfIsActive: Int = getColumnIndexOrThrow(_stmt, "isActive")
        val _columnIndexOfViewCount: Int = getColumnIndexOrThrow(_stmt, "viewCount")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _result: MutableList<AuctionEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: AuctionEntity
          val _tmpAuctionId: String
          _tmpAuctionId = _stmt.getText(_columnIndexOfAuctionId)
          val _tmpProductId: String
          _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          val _tmpSellerId: String
          _tmpSellerId = _stmt.getText(_columnIndexOfSellerId)
          val _tmpStartsAt: Long
          _tmpStartsAt = _stmt.getLong(_columnIndexOfStartsAt)
          val _tmpEndsAt: Long
          _tmpEndsAt = _stmt.getLong(_columnIndexOfEndsAt)
          val _tmpClosedAt: Long?
          if (_stmt.isNull(_columnIndexOfClosedAt)) {
            _tmpClosedAt = null
          } else {
            _tmpClosedAt = _stmt.getLong(_columnIndexOfClosedAt)
          }
          val _tmpClosedBy: String?
          if (_stmt.isNull(_columnIndexOfClosedBy)) {
            _tmpClosedBy = null
          } else {
            _tmpClosedBy = _stmt.getText(_columnIndexOfClosedBy)
          }
          val _tmpMinPrice: Double
          _tmpMinPrice = _stmt.getDouble(_columnIndexOfMinPrice)
          val _tmpCurrentPrice: Double
          _tmpCurrentPrice = _stmt.getDouble(_columnIndexOfCurrentPrice)
          val _tmpReservePrice: Double?
          if (_stmt.isNull(_columnIndexOfReservePrice)) {
            _tmpReservePrice = null
          } else {
            _tmpReservePrice = _stmt.getDouble(_columnIndexOfReservePrice)
          }
          val _tmpBuyNowPrice: Double?
          if (_stmt.isNull(_columnIndexOfBuyNowPrice)) {
            _tmpBuyNowPrice = null
          } else {
            _tmpBuyNowPrice = _stmt.getDouble(_columnIndexOfBuyNowPrice)
          }
          val _tmpBidIncrement: Double
          _tmpBidIncrement = _stmt.getDouble(_columnIndexOfBidIncrement)
          val _tmpBidCount: Int
          _tmpBidCount = _stmt.getLong(_columnIndexOfBidCount).toInt()
          val _tmpWinnerId: String?
          if (_stmt.isNull(_columnIndexOfWinnerId)) {
            _tmpWinnerId = null
          } else {
            _tmpWinnerId = _stmt.getText(_columnIndexOfWinnerId)
          }
          val _tmpIsReserveMet: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsReserveMet).toInt()
          _tmpIsReserveMet = _tmp != 0
          val _tmpExtensionCount: Int
          _tmpExtensionCount = _stmt.getLong(_columnIndexOfExtensionCount).toInt()
          val _tmpMaxExtensions: Int
          _tmpMaxExtensions = _stmt.getLong(_columnIndexOfMaxExtensions).toInt()
          val _tmpExtensionMinutes: Int
          _tmpExtensionMinutes = _stmt.getLong(_columnIndexOfExtensionMinutes).toInt()
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpIsActive: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfIsActive).toInt()
          _tmpIsActive = _tmp_1 != 0
          val _tmpViewCount: Int
          _tmpViewCount = _stmt.getLong(_columnIndexOfViewCount).toInt()
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpDirty: Boolean
          val _tmp_2: Int
          _tmp_2 = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp_2 != 0
          _item =
              AuctionEntity(_tmpAuctionId,_tmpProductId,_tmpSellerId,_tmpStartsAt,_tmpEndsAt,_tmpClosedAt,_tmpClosedBy,_tmpMinPrice,_tmpCurrentPrice,_tmpReservePrice,_tmpBuyNowPrice,_tmpBidIncrement,_tmpBidCount,_tmpWinnerId,_tmpIsReserveMet,_tmpExtensionCount,_tmpMaxExtensions,_tmpExtensionMinutes,_tmpStatus,_tmpIsActive,_tmpViewCount,_tmpCreatedAt,_tmpUpdatedAt,_tmpDirty)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun findByProduct(productId: String): AuctionEntity? {
    val _sql: String = "SELECT * FROM auctions WHERE productId = ? LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, productId)
        val _columnIndexOfAuctionId: Int = getColumnIndexOrThrow(_stmt, "auctionId")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfSellerId: Int = getColumnIndexOrThrow(_stmt, "sellerId")
        val _columnIndexOfStartsAt: Int = getColumnIndexOrThrow(_stmt, "startsAt")
        val _columnIndexOfEndsAt: Int = getColumnIndexOrThrow(_stmt, "endsAt")
        val _columnIndexOfClosedAt: Int = getColumnIndexOrThrow(_stmt, "closedAt")
        val _columnIndexOfClosedBy: Int = getColumnIndexOrThrow(_stmt, "closedBy")
        val _columnIndexOfMinPrice: Int = getColumnIndexOrThrow(_stmt, "minPrice")
        val _columnIndexOfCurrentPrice: Int = getColumnIndexOrThrow(_stmt, "currentPrice")
        val _columnIndexOfReservePrice: Int = getColumnIndexOrThrow(_stmt, "reservePrice")
        val _columnIndexOfBuyNowPrice: Int = getColumnIndexOrThrow(_stmt, "buyNowPrice")
        val _columnIndexOfBidIncrement: Int = getColumnIndexOrThrow(_stmt, "bidIncrement")
        val _columnIndexOfBidCount: Int = getColumnIndexOrThrow(_stmt, "bidCount")
        val _columnIndexOfWinnerId: Int = getColumnIndexOrThrow(_stmt, "winnerId")
        val _columnIndexOfIsReserveMet: Int = getColumnIndexOrThrow(_stmt, "isReserveMet")
        val _columnIndexOfExtensionCount: Int = getColumnIndexOrThrow(_stmt, "extensionCount")
        val _columnIndexOfMaxExtensions: Int = getColumnIndexOrThrow(_stmt, "maxExtensions")
        val _columnIndexOfExtensionMinutes: Int = getColumnIndexOrThrow(_stmt, "extensionMinutes")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfIsActive: Int = getColumnIndexOrThrow(_stmt, "isActive")
        val _columnIndexOfViewCount: Int = getColumnIndexOrThrow(_stmt, "viewCount")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _result: AuctionEntity?
        if (_stmt.step()) {
          val _tmpAuctionId: String
          _tmpAuctionId = _stmt.getText(_columnIndexOfAuctionId)
          val _tmpProductId: String
          _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          val _tmpSellerId: String
          _tmpSellerId = _stmt.getText(_columnIndexOfSellerId)
          val _tmpStartsAt: Long
          _tmpStartsAt = _stmt.getLong(_columnIndexOfStartsAt)
          val _tmpEndsAt: Long
          _tmpEndsAt = _stmt.getLong(_columnIndexOfEndsAt)
          val _tmpClosedAt: Long?
          if (_stmt.isNull(_columnIndexOfClosedAt)) {
            _tmpClosedAt = null
          } else {
            _tmpClosedAt = _stmt.getLong(_columnIndexOfClosedAt)
          }
          val _tmpClosedBy: String?
          if (_stmt.isNull(_columnIndexOfClosedBy)) {
            _tmpClosedBy = null
          } else {
            _tmpClosedBy = _stmt.getText(_columnIndexOfClosedBy)
          }
          val _tmpMinPrice: Double
          _tmpMinPrice = _stmt.getDouble(_columnIndexOfMinPrice)
          val _tmpCurrentPrice: Double
          _tmpCurrentPrice = _stmt.getDouble(_columnIndexOfCurrentPrice)
          val _tmpReservePrice: Double?
          if (_stmt.isNull(_columnIndexOfReservePrice)) {
            _tmpReservePrice = null
          } else {
            _tmpReservePrice = _stmt.getDouble(_columnIndexOfReservePrice)
          }
          val _tmpBuyNowPrice: Double?
          if (_stmt.isNull(_columnIndexOfBuyNowPrice)) {
            _tmpBuyNowPrice = null
          } else {
            _tmpBuyNowPrice = _stmt.getDouble(_columnIndexOfBuyNowPrice)
          }
          val _tmpBidIncrement: Double
          _tmpBidIncrement = _stmt.getDouble(_columnIndexOfBidIncrement)
          val _tmpBidCount: Int
          _tmpBidCount = _stmt.getLong(_columnIndexOfBidCount).toInt()
          val _tmpWinnerId: String?
          if (_stmt.isNull(_columnIndexOfWinnerId)) {
            _tmpWinnerId = null
          } else {
            _tmpWinnerId = _stmt.getText(_columnIndexOfWinnerId)
          }
          val _tmpIsReserveMet: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsReserveMet).toInt()
          _tmpIsReserveMet = _tmp != 0
          val _tmpExtensionCount: Int
          _tmpExtensionCount = _stmt.getLong(_columnIndexOfExtensionCount).toInt()
          val _tmpMaxExtensions: Int
          _tmpMaxExtensions = _stmt.getLong(_columnIndexOfMaxExtensions).toInt()
          val _tmpExtensionMinutes: Int
          _tmpExtensionMinutes = _stmt.getLong(_columnIndexOfExtensionMinutes).toInt()
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpIsActive: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfIsActive).toInt()
          _tmpIsActive = _tmp_1 != 0
          val _tmpViewCount: Int
          _tmpViewCount = _stmt.getLong(_columnIndexOfViewCount).toInt()
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpDirty: Boolean
          val _tmp_2: Int
          _tmp_2 = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp_2 != 0
          _result =
              AuctionEntity(_tmpAuctionId,_tmpProductId,_tmpSellerId,_tmpStartsAt,_tmpEndsAt,_tmpClosedAt,_tmpClosedBy,_tmpMinPrice,_tmpCurrentPrice,_tmpReservePrice,_tmpBuyNowPrice,_tmpBidIncrement,_tmpBidCount,_tmpWinnerId,_tmpIsReserveMet,_tmpExtensionCount,_tmpMaxExtensions,_tmpExtensionMinutes,_tmpStatus,_tmpIsActive,_tmpViewCount,_tmpCreatedAt,_tmpUpdatedAt,_tmpDirty)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getAuctionsBySeller(sellerId: String): Flow<List<AuctionEntity>> {
    val _sql: String = "SELECT * FROM auctions WHERE sellerId = ? ORDER BY createdAt DESC"
    return createFlow(__db, false, arrayOf("auctions")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, sellerId)
        val _columnIndexOfAuctionId: Int = getColumnIndexOrThrow(_stmt, "auctionId")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfSellerId: Int = getColumnIndexOrThrow(_stmt, "sellerId")
        val _columnIndexOfStartsAt: Int = getColumnIndexOrThrow(_stmt, "startsAt")
        val _columnIndexOfEndsAt: Int = getColumnIndexOrThrow(_stmt, "endsAt")
        val _columnIndexOfClosedAt: Int = getColumnIndexOrThrow(_stmt, "closedAt")
        val _columnIndexOfClosedBy: Int = getColumnIndexOrThrow(_stmt, "closedBy")
        val _columnIndexOfMinPrice: Int = getColumnIndexOrThrow(_stmt, "minPrice")
        val _columnIndexOfCurrentPrice: Int = getColumnIndexOrThrow(_stmt, "currentPrice")
        val _columnIndexOfReservePrice: Int = getColumnIndexOrThrow(_stmt, "reservePrice")
        val _columnIndexOfBuyNowPrice: Int = getColumnIndexOrThrow(_stmt, "buyNowPrice")
        val _columnIndexOfBidIncrement: Int = getColumnIndexOrThrow(_stmt, "bidIncrement")
        val _columnIndexOfBidCount: Int = getColumnIndexOrThrow(_stmt, "bidCount")
        val _columnIndexOfWinnerId: Int = getColumnIndexOrThrow(_stmt, "winnerId")
        val _columnIndexOfIsReserveMet: Int = getColumnIndexOrThrow(_stmt, "isReserveMet")
        val _columnIndexOfExtensionCount: Int = getColumnIndexOrThrow(_stmt, "extensionCount")
        val _columnIndexOfMaxExtensions: Int = getColumnIndexOrThrow(_stmt, "maxExtensions")
        val _columnIndexOfExtensionMinutes: Int = getColumnIndexOrThrow(_stmt, "extensionMinutes")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfIsActive: Int = getColumnIndexOrThrow(_stmt, "isActive")
        val _columnIndexOfViewCount: Int = getColumnIndexOrThrow(_stmt, "viewCount")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _result: MutableList<AuctionEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: AuctionEntity
          val _tmpAuctionId: String
          _tmpAuctionId = _stmt.getText(_columnIndexOfAuctionId)
          val _tmpProductId: String
          _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          val _tmpSellerId: String
          _tmpSellerId = _stmt.getText(_columnIndexOfSellerId)
          val _tmpStartsAt: Long
          _tmpStartsAt = _stmt.getLong(_columnIndexOfStartsAt)
          val _tmpEndsAt: Long
          _tmpEndsAt = _stmt.getLong(_columnIndexOfEndsAt)
          val _tmpClosedAt: Long?
          if (_stmt.isNull(_columnIndexOfClosedAt)) {
            _tmpClosedAt = null
          } else {
            _tmpClosedAt = _stmt.getLong(_columnIndexOfClosedAt)
          }
          val _tmpClosedBy: String?
          if (_stmt.isNull(_columnIndexOfClosedBy)) {
            _tmpClosedBy = null
          } else {
            _tmpClosedBy = _stmt.getText(_columnIndexOfClosedBy)
          }
          val _tmpMinPrice: Double
          _tmpMinPrice = _stmt.getDouble(_columnIndexOfMinPrice)
          val _tmpCurrentPrice: Double
          _tmpCurrentPrice = _stmt.getDouble(_columnIndexOfCurrentPrice)
          val _tmpReservePrice: Double?
          if (_stmt.isNull(_columnIndexOfReservePrice)) {
            _tmpReservePrice = null
          } else {
            _tmpReservePrice = _stmt.getDouble(_columnIndexOfReservePrice)
          }
          val _tmpBuyNowPrice: Double?
          if (_stmt.isNull(_columnIndexOfBuyNowPrice)) {
            _tmpBuyNowPrice = null
          } else {
            _tmpBuyNowPrice = _stmt.getDouble(_columnIndexOfBuyNowPrice)
          }
          val _tmpBidIncrement: Double
          _tmpBidIncrement = _stmt.getDouble(_columnIndexOfBidIncrement)
          val _tmpBidCount: Int
          _tmpBidCount = _stmt.getLong(_columnIndexOfBidCount).toInt()
          val _tmpWinnerId: String?
          if (_stmt.isNull(_columnIndexOfWinnerId)) {
            _tmpWinnerId = null
          } else {
            _tmpWinnerId = _stmt.getText(_columnIndexOfWinnerId)
          }
          val _tmpIsReserveMet: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsReserveMet).toInt()
          _tmpIsReserveMet = _tmp != 0
          val _tmpExtensionCount: Int
          _tmpExtensionCount = _stmt.getLong(_columnIndexOfExtensionCount).toInt()
          val _tmpMaxExtensions: Int
          _tmpMaxExtensions = _stmt.getLong(_columnIndexOfMaxExtensions).toInt()
          val _tmpExtensionMinutes: Int
          _tmpExtensionMinutes = _stmt.getLong(_columnIndexOfExtensionMinutes).toInt()
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpIsActive: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfIsActive).toInt()
          _tmpIsActive = _tmp_1 != 0
          val _tmpViewCount: Int
          _tmpViewCount = _stmt.getLong(_columnIndexOfViewCount).toInt()
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpDirty: Boolean
          val _tmp_2: Int
          _tmp_2 = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp_2 != 0
          _item =
              AuctionEntity(_tmpAuctionId,_tmpProductId,_tmpSellerId,_tmpStartsAt,_tmpEndsAt,_tmpClosedAt,_tmpClosedBy,_tmpMinPrice,_tmpCurrentPrice,_tmpReservePrice,_tmpBuyNowPrice,_tmpBidIncrement,_tmpBidCount,_tmpWinnerId,_tmpIsReserveMet,_tmpExtensionCount,_tmpMaxExtensions,_tmpExtensionMinutes,_tmpStatus,_tmpIsActive,_tmpViewCount,_tmpCreatedAt,_tmpUpdatedAt,_tmpDirty)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getSellerAuctionsByStatus(sellerId: String, status: String):
      Flow<List<AuctionEntity>> {
    val _sql: String =
        "SELECT * FROM auctions WHERE sellerId = ? AND status = ? ORDER BY endsAt ASC"
    return createFlow(__db, false, arrayOf("auctions")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, sellerId)
        _argIndex = 2
        _stmt.bindText(_argIndex, status)
        val _columnIndexOfAuctionId: Int = getColumnIndexOrThrow(_stmt, "auctionId")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfSellerId: Int = getColumnIndexOrThrow(_stmt, "sellerId")
        val _columnIndexOfStartsAt: Int = getColumnIndexOrThrow(_stmt, "startsAt")
        val _columnIndexOfEndsAt: Int = getColumnIndexOrThrow(_stmt, "endsAt")
        val _columnIndexOfClosedAt: Int = getColumnIndexOrThrow(_stmt, "closedAt")
        val _columnIndexOfClosedBy: Int = getColumnIndexOrThrow(_stmt, "closedBy")
        val _columnIndexOfMinPrice: Int = getColumnIndexOrThrow(_stmt, "minPrice")
        val _columnIndexOfCurrentPrice: Int = getColumnIndexOrThrow(_stmt, "currentPrice")
        val _columnIndexOfReservePrice: Int = getColumnIndexOrThrow(_stmt, "reservePrice")
        val _columnIndexOfBuyNowPrice: Int = getColumnIndexOrThrow(_stmt, "buyNowPrice")
        val _columnIndexOfBidIncrement: Int = getColumnIndexOrThrow(_stmt, "bidIncrement")
        val _columnIndexOfBidCount: Int = getColumnIndexOrThrow(_stmt, "bidCount")
        val _columnIndexOfWinnerId: Int = getColumnIndexOrThrow(_stmt, "winnerId")
        val _columnIndexOfIsReserveMet: Int = getColumnIndexOrThrow(_stmt, "isReserveMet")
        val _columnIndexOfExtensionCount: Int = getColumnIndexOrThrow(_stmt, "extensionCount")
        val _columnIndexOfMaxExtensions: Int = getColumnIndexOrThrow(_stmt, "maxExtensions")
        val _columnIndexOfExtensionMinutes: Int = getColumnIndexOrThrow(_stmt, "extensionMinutes")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfIsActive: Int = getColumnIndexOrThrow(_stmt, "isActive")
        val _columnIndexOfViewCount: Int = getColumnIndexOrThrow(_stmt, "viewCount")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _result: MutableList<AuctionEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: AuctionEntity
          val _tmpAuctionId: String
          _tmpAuctionId = _stmt.getText(_columnIndexOfAuctionId)
          val _tmpProductId: String
          _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          val _tmpSellerId: String
          _tmpSellerId = _stmt.getText(_columnIndexOfSellerId)
          val _tmpStartsAt: Long
          _tmpStartsAt = _stmt.getLong(_columnIndexOfStartsAt)
          val _tmpEndsAt: Long
          _tmpEndsAt = _stmt.getLong(_columnIndexOfEndsAt)
          val _tmpClosedAt: Long?
          if (_stmt.isNull(_columnIndexOfClosedAt)) {
            _tmpClosedAt = null
          } else {
            _tmpClosedAt = _stmt.getLong(_columnIndexOfClosedAt)
          }
          val _tmpClosedBy: String?
          if (_stmt.isNull(_columnIndexOfClosedBy)) {
            _tmpClosedBy = null
          } else {
            _tmpClosedBy = _stmt.getText(_columnIndexOfClosedBy)
          }
          val _tmpMinPrice: Double
          _tmpMinPrice = _stmt.getDouble(_columnIndexOfMinPrice)
          val _tmpCurrentPrice: Double
          _tmpCurrentPrice = _stmt.getDouble(_columnIndexOfCurrentPrice)
          val _tmpReservePrice: Double?
          if (_stmt.isNull(_columnIndexOfReservePrice)) {
            _tmpReservePrice = null
          } else {
            _tmpReservePrice = _stmt.getDouble(_columnIndexOfReservePrice)
          }
          val _tmpBuyNowPrice: Double?
          if (_stmt.isNull(_columnIndexOfBuyNowPrice)) {
            _tmpBuyNowPrice = null
          } else {
            _tmpBuyNowPrice = _stmt.getDouble(_columnIndexOfBuyNowPrice)
          }
          val _tmpBidIncrement: Double
          _tmpBidIncrement = _stmt.getDouble(_columnIndexOfBidIncrement)
          val _tmpBidCount: Int
          _tmpBidCount = _stmt.getLong(_columnIndexOfBidCount).toInt()
          val _tmpWinnerId: String?
          if (_stmt.isNull(_columnIndexOfWinnerId)) {
            _tmpWinnerId = null
          } else {
            _tmpWinnerId = _stmt.getText(_columnIndexOfWinnerId)
          }
          val _tmpIsReserveMet: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsReserveMet).toInt()
          _tmpIsReserveMet = _tmp != 0
          val _tmpExtensionCount: Int
          _tmpExtensionCount = _stmt.getLong(_columnIndexOfExtensionCount).toInt()
          val _tmpMaxExtensions: Int
          _tmpMaxExtensions = _stmt.getLong(_columnIndexOfMaxExtensions).toInt()
          val _tmpExtensionMinutes: Int
          _tmpExtensionMinutes = _stmt.getLong(_columnIndexOfExtensionMinutes).toInt()
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpIsActive: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfIsActive).toInt()
          _tmpIsActive = _tmp_1 != 0
          val _tmpViewCount: Int
          _tmpViewCount = _stmt.getLong(_columnIndexOfViewCount).toInt()
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpDirty: Boolean
          val _tmp_2: Int
          _tmp_2 = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp_2 != 0
          _item =
              AuctionEntity(_tmpAuctionId,_tmpProductId,_tmpSellerId,_tmpStartsAt,_tmpEndsAt,_tmpClosedAt,_tmpClosedBy,_tmpMinPrice,_tmpCurrentPrice,_tmpReservePrice,_tmpBuyNowPrice,_tmpBidIncrement,_tmpBidCount,_tmpWinnerId,_tmpIsReserveMet,_tmpExtensionCount,_tmpMaxExtensions,_tmpExtensionMinutes,_tmpStatus,_tmpIsActive,_tmpViewCount,_tmpCreatedAt,_tmpUpdatedAt,_tmpDirty)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getExpiredAuctions(now: Long): List<AuctionEntity> {
    val _sql: String = "SELECT * FROM auctions WHERE status = 'ACTIVE' AND endsAt < ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, now)
        val _columnIndexOfAuctionId: Int = getColumnIndexOrThrow(_stmt, "auctionId")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfSellerId: Int = getColumnIndexOrThrow(_stmt, "sellerId")
        val _columnIndexOfStartsAt: Int = getColumnIndexOrThrow(_stmt, "startsAt")
        val _columnIndexOfEndsAt: Int = getColumnIndexOrThrow(_stmt, "endsAt")
        val _columnIndexOfClosedAt: Int = getColumnIndexOrThrow(_stmt, "closedAt")
        val _columnIndexOfClosedBy: Int = getColumnIndexOrThrow(_stmt, "closedBy")
        val _columnIndexOfMinPrice: Int = getColumnIndexOrThrow(_stmt, "minPrice")
        val _columnIndexOfCurrentPrice: Int = getColumnIndexOrThrow(_stmt, "currentPrice")
        val _columnIndexOfReservePrice: Int = getColumnIndexOrThrow(_stmt, "reservePrice")
        val _columnIndexOfBuyNowPrice: Int = getColumnIndexOrThrow(_stmt, "buyNowPrice")
        val _columnIndexOfBidIncrement: Int = getColumnIndexOrThrow(_stmt, "bidIncrement")
        val _columnIndexOfBidCount: Int = getColumnIndexOrThrow(_stmt, "bidCount")
        val _columnIndexOfWinnerId: Int = getColumnIndexOrThrow(_stmt, "winnerId")
        val _columnIndexOfIsReserveMet: Int = getColumnIndexOrThrow(_stmt, "isReserveMet")
        val _columnIndexOfExtensionCount: Int = getColumnIndexOrThrow(_stmt, "extensionCount")
        val _columnIndexOfMaxExtensions: Int = getColumnIndexOrThrow(_stmt, "maxExtensions")
        val _columnIndexOfExtensionMinutes: Int = getColumnIndexOrThrow(_stmt, "extensionMinutes")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfIsActive: Int = getColumnIndexOrThrow(_stmt, "isActive")
        val _columnIndexOfViewCount: Int = getColumnIndexOrThrow(_stmt, "viewCount")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _result: MutableList<AuctionEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: AuctionEntity
          val _tmpAuctionId: String
          _tmpAuctionId = _stmt.getText(_columnIndexOfAuctionId)
          val _tmpProductId: String
          _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          val _tmpSellerId: String
          _tmpSellerId = _stmt.getText(_columnIndexOfSellerId)
          val _tmpStartsAt: Long
          _tmpStartsAt = _stmt.getLong(_columnIndexOfStartsAt)
          val _tmpEndsAt: Long
          _tmpEndsAt = _stmt.getLong(_columnIndexOfEndsAt)
          val _tmpClosedAt: Long?
          if (_stmt.isNull(_columnIndexOfClosedAt)) {
            _tmpClosedAt = null
          } else {
            _tmpClosedAt = _stmt.getLong(_columnIndexOfClosedAt)
          }
          val _tmpClosedBy: String?
          if (_stmt.isNull(_columnIndexOfClosedBy)) {
            _tmpClosedBy = null
          } else {
            _tmpClosedBy = _stmt.getText(_columnIndexOfClosedBy)
          }
          val _tmpMinPrice: Double
          _tmpMinPrice = _stmt.getDouble(_columnIndexOfMinPrice)
          val _tmpCurrentPrice: Double
          _tmpCurrentPrice = _stmt.getDouble(_columnIndexOfCurrentPrice)
          val _tmpReservePrice: Double?
          if (_stmt.isNull(_columnIndexOfReservePrice)) {
            _tmpReservePrice = null
          } else {
            _tmpReservePrice = _stmt.getDouble(_columnIndexOfReservePrice)
          }
          val _tmpBuyNowPrice: Double?
          if (_stmt.isNull(_columnIndexOfBuyNowPrice)) {
            _tmpBuyNowPrice = null
          } else {
            _tmpBuyNowPrice = _stmt.getDouble(_columnIndexOfBuyNowPrice)
          }
          val _tmpBidIncrement: Double
          _tmpBidIncrement = _stmt.getDouble(_columnIndexOfBidIncrement)
          val _tmpBidCount: Int
          _tmpBidCount = _stmt.getLong(_columnIndexOfBidCount).toInt()
          val _tmpWinnerId: String?
          if (_stmt.isNull(_columnIndexOfWinnerId)) {
            _tmpWinnerId = null
          } else {
            _tmpWinnerId = _stmt.getText(_columnIndexOfWinnerId)
          }
          val _tmpIsReserveMet: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsReserveMet).toInt()
          _tmpIsReserveMet = _tmp != 0
          val _tmpExtensionCount: Int
          _tmpExtensionCount = _stmt.getLong(_columnIndexOfExtensionCount).toInt()
          val _tmpMaxExtensions: Int
          _tmpMaxExtensions = _stmt.getLong(_columnIndexOfMaxExtensions).toInt()
          val _tmpExtensionMinutes: Int
          _tmpExtensionMinutes = _stmt.getLong(_columnIndexOfExtensionMinutes).toInt()
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpIsActive: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfIsActive).toInt()
          _tmpIsActive = _tmp_1 != 0
          val _tmpViewCount: Int
          _tmpViewCount = _stmt.getLong(_columnIndexOfViewCount).toInt()
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpDirty: Boolean
          val _tmp_2: Int
          _tmp_2 = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp_2 != 0
          _item =
              AuctionEntity(_tmpAuctionId,_tmpProductId,_tmpSellerId,_tmpStartsAt,_tmpEndsAt,_tmpClosedAt,_tmpClosedBy,_tmpMinPrice,_tmpCurrentPrice,_tmpReservePrice,_tmpBuyNowPrice,_tmpBidIncrement,_tmpBidCount,_tmpWinnerId,_tmpIsReserveMet,_tmpExtensionCount,_tmpMaxExtensions,_tmpExtensionMinutes,_tmpStatus,_tmpIsActive,_tmpViewCount,_tmpCreatedAt,_tmpUpdatedAt,_tmpDirty)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun cancelAuction(auctionId: String, now: Long): Int {
    val _sql: String =
        "UPDATE auctions SET status = 'CANCELLED', closedAt = ?, closedBy = 'SELLER', updatedAt = ?, dirty = 1 WHERE auctionId = ? AND bidCount = 0"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, now)
        _argIndex = 2
        _stmt.bindLong(_argIndex, now)
        _argIndex = 3
        _stmt.bindText(_argIndex, auctionId)
        _stmt.step()
        getTotalChangedRows(_connection)
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun closeAuction(
    auctionId: String,
    status: String,
    closedAt: Long,
  ) {
    val _sql: String =
        "UPDATE auctions SET status = ?, closedAt = ?, closedBy = 'SYSTEM', updatedAt = ?, dirty = 1 WHERE auctionId = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, status)
        _argIndex = 2
        _stmt.bindLong(_argIndex, closedAt)
        _argIndex = 3
        _stmt.bindLong(_argIndex, closedAt)
        _argIndex = 4
        _stmt.bindText(_argIndex, auctionId)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun updateBidState(
    auctionId: String,
    amount: Double,
    winnerId: String,
    now: Long,
  ) {
    val _sql: String =
        "UPDATE auctions SET currentPrice = ?, winnerId = ?, bidCount = bidCount + 1, isReserveMet = CASE WHEN reservePrice IS NOT NULL AND ? >= reservePrice THEN 1 ELSE isReserveMet END, updatedAt = ?, dirty = 1 WHERE auctionId = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindDouble(_argIndex, amount)
        _argIndex = 2
        _stmt.bindText(_argIndex, winnerId)
        _argIndex = 3
        _stmt.bindDouble(_argIndex, amount)
        _argIndex = 4
        _stmt.bindLong(_argIndex, now)
        _argIndex = 5
        _stmt.bindText(_argIndex, auctionId)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun extendAuction(
    auctionId: String,
    newEndsAt: Long,
    now: Long,
  ): Int {
    val _sql: String =
        "UPDATE auctions SET endsAt = ?, extensionCount = extensionCount + 1, updatedAt = ?, dirty = 1 WHERE auctionId = ? AND extensionCount < maxExtensions"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, newEndsAt)
        _argIndex = 2
        _stmt.bindLong(_argIndex, now)
        _argIndex = 3
        _stmt.bindText(_argIndex, auctionId)
        _stmt.step()
        getTotalChangedRows(_connection)
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun incrementViewCount(auctionId: String) {
    val _sql: String = "UPDATE auctions SET viewCount = viewCount + 1 WHERE auctionId = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, auctionId)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun executeBuyNow(
    auctionId: String,
    buyerId: String,
    now: Long,
  ): Int {
    val _sql: String =
        "UPDATE auctions SET status = 'SOLD', currentPrice = buyNowPrice, winnerId = ?, closedAt = ?, closedBy = 'BUYER', updatedAt = ?, dirty = 1 WHERE auctionId = ? AND buyNowPrice IS NOT NULL"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, buyerId)
        _argIndex = 2
        _stmt.bindLong(_argIndex, now)
        _argIndex = 3
        _stmt.bindLong(_argIndex, now)
        _argIndex = 4
        _stmt.bindText(_argIndex, auctionId)
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
