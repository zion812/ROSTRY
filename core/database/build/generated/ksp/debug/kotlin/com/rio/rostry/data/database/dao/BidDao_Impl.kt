package com.rio.rostry.`data`.database.dao

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.rio.rostry.`data`.database.entity.BidEntity
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
public class BidDao_Impl(
  __db: RoomDatabase,
) : BidDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfBidEntity: EntityInsertAdapter<BidEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfBidEntity = object : EntityInsertAdapter<BidEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `bids` (`bidId`,`auctionId`,`userId`,`amount`,`placedAt`,`isAutoBid`,`maxAmount`,`isWinning`,`wasOutbid`,`outbidAt`,`outbidNotified`,`isRetracted`,`retractedReason`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: BidEntity) {
        statement.bindText(1, entity.bidId)
        statement.bindText(2, entity.auctionId)
        statement.bindText(3, entity.userId)
        statement.bindDouble(4, entity.amount)
        statement.bindLong(5, entity.placedAt)
        val _tmp: Int = if (entity.isAutoBid) 1 else 0
        statement.bindLong(6, _tmp.toLong())
        val _tmpMaxAmount: Double? = entity.maxAmount
        if (_tmpMaxAmount == null) {
          statement.bindNull(7)
        } else {
          statement.bindDouble(7, _tmpMaxAmount)
        }
        val _tmp_1: Int = if (entity.isWinning) 1 else 0
        statement.bindLong(8, _tmp_1.toLong())
        val _tmp_2: Int = if (entity.wasOutbid) 1 else 0
        statement.bindLong(9, _tmp_2.toLong())
        val _tmpOutbidAt: Long? = entity.outbidAt
        if (_tmpOutbidAt == null) {
          statement.bindNull(10)
        } else {
          statement.bindLong(10, _tmpOutbidAt)
        }
        val _tmp_3: Int = if (entity.outbidNotified) 1 else 0
        statement.bindLong(11, _tmp_3.toLong())
        val _tmp_4: Int = if (entity.isRetracted) 1 else 0
        statement.bindLong(12, _tmp_4.toLong())
        val _tmpRetractedReason: String? = entity.retractedReason
        if (_tmpRetractedReason == null) {
          statement.bindNull(13)
        } else {
          statement.bindText(13, _tmpRetractedReason)
        }
      }
    }
  }

  public override suspend fun insert(bid: BidEntity): Unit = performSuspending(__db, false, true) {
      _connection ->
    __insertAdapterOfBidEntity.insert(_connection, bid)
  }

  public override fun observeBids(auctionId: String): Flow<List<BidEntity>> {
    val _sql: String = "SELECT * FROM bids WHERE auctionId = ? ORDER BY amount DESC, placedAt DESC"
    return createFlow(__db, false, arrayOf("bids")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, auctionId)
        val _columnIndexOfBidId: Int = getColumnIndexOrThrow(_stmt, "bidId")
        val _columnIndexOfAuctionId: Int = getColumnIndexOrThrow(_stmt, "auctionId")
        val _columnIndexOfUserId: Int = getColumnIndexOrThrow(_stmt, "userId")
        val _columnIndexOfAmount: Int = getColumnIndexOrThrow(_stmt, "amount")
        val _columnIndexOfPlacedAt: Int = getColumnIndexOrThrow(_stmt, "placedAt")
        val _columnIndexOfIsAutoBid: Int = getColumnIndexOrThrow(_stmt, "isAutoBid")
        val _columnIndexOfMaxAmount: Int = getColumnIndexOrThrow(_stmt, "maxAmount")
        val _columnIndexOfIsWinning: Int = getColumnIndexOrThrow(_stmt, "isWinning")
        val _columnIndexOfWasOutbid: Int = getColumnIndexOrThrow(_stmt, "wasOutbid")
        val _columnIndexOfOutbidAt: Int = getColumnIndexOrThrow(_stmt, "outbidAt")
        val _columnIndexOfOutbidNotified: Int = getColumnIndexOrThrow(_stmt, "outbidNotified")
        val _columnIndexOfIsRetracted: Int = getColumnIndexOrThrow(_stmt, "isRetracted")
        val _columnIndexOfRetractedReason: Int = getColumnIndexOrThrow(_stmt, "retractedReason")
        val _result: MutableList<BidEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: BidEntity
          val _tmpBidId: String
          _tmpBidId = _stmt.getText(_columnIndexOfBidId)
          val _tmpAuctionId: String
          _tmpAuctionId = _stmt.getText(_columnIndexOfAuctionId)
          val _tmpUserId: String
          _tmpUserId = _stmt.getText(_columnIndexOfUserId)
          val _tmpAmount: Double
          _tmpAmount = _stmt.getDouble(_columnIndexOfAmount)
          val _tmpPlacedAt: Long
          _tmpPlacedAt = _stmt.getLong(_columnIndexOfPlacedAt)
          val _tmpIsAutoBid: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsAutoBid).toInt()
          _tmpIsAutoBid = _tmp != 0
          val _tmpMaxAmount: Double?
          if (_stmt.isNull(_columnIndexOfMaxAmount)) {
            _tmpMaxAmount = null
          } else {
            _tmpMaxAmount = _stmt.getDouble(_columnIndexOfMaxAmount)
          }
          val _tmpIsWinning: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfIsWinning).toInt()
          _tmpIsWinning = _tmp_1 != 0
          val _tmpWasOutbid: Boolean
          val _tmp_2: Int
          _tmp_2 = _stmt.getLong(_columnIndexOfWasOutbid).toInt()
          _tmpWasOutbid = _tmp_2 != 0
          val _tmpOutbidAt: Long?
          if (_stmt.isNull(_columnIndexOfOutbidAt)) {
            _tmpOutbidAt = null
          } else {
            _tmpOutbidAt = _stmt.getLong(_columnIndexOfOutbidAt)
          }
          val _tmpOutbidNotified: Boolean
          val _tmp_3: Int
          _tmp_3 = _stmt.getLong(_columnIndexOfOutbidNotified).toInt()
          _tmpOutbidNotified = _tmp_3 != 0
          val _tmpIsRetracted: Boolean
          val _tmp_4: Int
          _tmp_4 = _stmt.getLong(_columnIndexOfIsRetracted).toInt()
          _tmpIsRetracted = _tmp_4 != 0
          val _tmpRetractedReason: String?
          if (_stmt.isNull(_columnIndexOfRetractedReason)) {
            _tmpRetractedReason = null
          } else {
            _tmpRetractedReason = _stmt.getText(_columnIndexOfRetractedReason)
          }
          _item =
              BidEntity(_tmpBidId,_tmpAuctionId,_tmpUserId,_tmpAmount,_tmpPlacedAt,_tmpIsAutoBid,_tmpMaxAmount,_tmpIsWinning,_tmpWasOutbid,_tmpOutbidAt,_tmpOutbidNotified,_tmpIsRetracted,_tmpRetractedReason)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getHighestBid(auctionId: String): BidEntity? {
    val _sql: String =
        "SELECT * FROM bids WHERE auctionId = ? ORDER BY amount DESC, placedAt DESC LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, auctionId)
        val _columnIndexOfBidId: Int = getColumnIndexOrThrow(_stmt, "bidId")
        val _columnIndexOfAuctionId: Int = getColumnIndexOrThrow(_stmt, "auctionId")
        val _columnIndexOfUserId: Int = getColumnIndexOrThrow(_stmt, "userId")
        val _columnIndexOfAmount: Int = getColumnIndexOrThrow(_stmt, "amount")
        val _columnIndexOfPlacedAt: Int = getColumnIndexOrThrow(_stmt, "placedAt")
        val _columnIndexOfIsAutoBid: Int = getColumnIndexOrThrow(_stmt, "isAutoBid")
        val _columnIndexOfMaxAmount: Int = getColumnIndexOrThrow(_stmt, "maxAmount")
        val _columnIndexOfIsWinning: Int = getColumnIndexOrThrow(_stmt, "isWinning")
        val _columnIndexOfWasOutbid: Int = getColumnIndexOrThrow(_stmt, "wasOutbid")
        val _columnIndexOfOutbidAt: Int = getColumnIndexOrThrow(_stmt, "outbidAt")
        val _columnIndexOfOutbidNotified: Int = getColumnIndexOrThrow(_stmt, "outbidNotified")
        val _columnIndexOfIsRetracted: Int = getColumnIndexOrThrow(_stmt, "isRetracted")
        val _columnIndexOfRetractedReason: Int = getColumnIndexOrThrow(_stmt, "retractedReason")
        val _result: BidEntity?
        if (_stmt.step()) {
          val _tmpBidId: String
          _tmpBidId = _stmt.getText(_columnIndexOfBidId)
          val _tmpAuctionId: String
          _tmpAuctionId = _stmt.getText(_columnIndexOfAuctionId)
          val _tmpUserId: String
          _tmpUserId = _stmt.getText(_columnIndexOfUserId)
          val _tmpAmount: Double
          _tmpAmount = _stmt.getDouble(_columnIndexOfAmount)
          val _tmpPlacedAt: Long
          _tmpPlacedAt = _stmt.getLong(_columnIndexOfPlacedAt)
          val _tmpIsAutoBid: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsAutoBid).toInt()
          _tmpIsAutoBid = _tmp != 0
          val _tmpMaxAmount: Double?
          if (_stmt.isNull(_columnIndexOfMaxAmount)) {
            _tmpMaxAmount = null
          } else {
            _tmpMaxAmount = _stmt.getDouble(_columnIndexOfMaxAmount)
          }
          val _tmpIsWinning: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfIsWinning).toInt()
          _tmpIsWinning = _tmp_1 != 0
          val _tmpWasOutbid: Boolean
          val _tmp_2: Int
          _tmp_2 = _stmt.getLong(_columnIndexOfWasOutbid).toInt()
          _tmpWasOutbid = _tmp_2 != 0
          val _tmpOutbidAt: Long?
          if (_stmt.isNull(_columnIndexOfOutbidAt)) {
            _tmpOutbidAt = null
          } else {
            _tmpOutbidAt = _stmt.getLong(_columnIndexOfOutbidAt)
          }
          val _tmpOutbidNotified: Boolean
          val _tmp_3: Int
          _tmp_3 = _stmt.getLong(_columnIndexOfOutbidNotified).toInt()
          _tmpOutbidNotified = _tmp_3 != 0
          val _tmpIsRetracted: Boolean
          val _tmp_4: Int
          _tmp_4 = _stmt.getLong(_columnIndexOfIsRetracted).toInt()
          _tmpIsRetracted = _tmp_4 != 0
          val _tmpRetractedReason: String?
          if (_stmt.isNull(_columnIndexOfRetractedReason)) {
            _tmpRetractedReason = null
          } else {
            _tmpRetractedReason = _stmt.getText(_columnIndexOfRetractedReason)
          }
          _result =
              BidEntity(_tmpBidId,_tmpAuctionId,_tmpUserId,_tmpAmount,_tmpPlacedAt,_tmpIsAutoBid,_tmpMaxAmount,_tmpIsWinning,_tmpWasOutbid,_tmpOutbidAt,_tmpOutbidNotified,_tmpIsRetracted,_tmpRetractedReason)
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
