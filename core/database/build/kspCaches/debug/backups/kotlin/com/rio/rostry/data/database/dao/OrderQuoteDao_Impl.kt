package com.rio.rostry.`data`.database.dao

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.getTotalChangedRows
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.rio.rostry.`data`.database.entity.OrderQuoteEntity
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
public class OrderQuoteDao_Impl(
  __db: RoomDatabase,
) : OrderQuoteDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfOrderQuoteEntity: EntityInsertAdapter<OrderQuoteEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfOrderQuoteEntity = object : EntityInsertAdapter<OrderQuoteEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `order_quotes` (`quoteId`,`orderId`,`buyerId`,`sellerId`,`productId`,`productName`,`quantity`,`unit`,`basePrice`,`totalProductPrice`,`deliveryCharge`,`packingCharge`,`platformFee`,`discount`,`finalTotal`,`deliveryType`,`deliveryDistance`,`deliveryAddress`,`deliveryLatitude`,`deliveryLongitude`,`pickupAddress`,`pickupLatitude`,`pickupLongitude`,`paymentType`,`advanceAmount`,`balanceAmount`,`status`,`buyerAgreedAt`,`sellerAgreedAt`,`lockedAt`,`expiresAt`,`version`,`previousQuoteId`,`buyerNotes`,`sellerNotes`,`createdAt`,`updatedAt`,`dirty`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: OrderQuoteEntity) {
        statement.bindText(1, entity.quoteId)
        statement.bindText(2, entity.orderId)
        statement.bindText(3, entity.buyerId)
        statement.bindText(4, entity.sellerId)
        statement.bindText(5, entity.productId)
        statement.bindText(6, entity.productName)
        statement.bindDouble(7, entity.quantity)
        statement.bindText(8, entity.unit)
        statement.bindDouble(9, entity.basePrice)
        statement.bindDouble(10, entity.totalProductPrice)
        statement.bindDouble(11, entity.deliveryCharge)
        statement.bindDouble(12, entity.packingCharge)
        statement.bindDouble(13, entity.platformFee)
        statement.bindDouble(14, entity.discount)
        statement.bindDouble(15, entity.finalTotal)
        statement.bindText(16, entity.deliveryType)
        val _tmpDeliveryDistance: Double? = entity.deliveryDistance
        if (_tmpDeliveryDistance == null) {
          statement.bindNull(17)
        } else {
          statement.bindDouble(17, _tmpDeliveryDistance)
        }
        val _tmpDeliveryAddress: String? = entity.deliveryAddress
        if (_tmpDeliveryAddress == null) {
          statement.bindNull(18)
        } else {
          statement.bindText(18, _tmpDeliveryAddress)
        }
        val _tmpDeliveryLatitude: Double? = entity.deliveryLatitude
        if (_tmpDeliveryLatitude == null) {
          statement.bindNull(19)
        } else {
          statement.bindDouble(19, _tmpDeliveryLatitude)
        }
        val _tmpDeliveryLongitude: Double? = entity.deliveryLongitude
        if (_tmpDeliveryLongitude == null) {
          statement.bindNull(20)
        } else {
          statement.bindDouble(20, _tmpDeliveryLongitude)
        }
        val _tmpPickupAddress: String? = entity.pickupAddress
        if (_tmpPickupAddress == null) {
          statement.bindNull(21)
        } else {
          statement.bindText(21, _tmpPickupAddress)
        }
        val _tmpPickupLatitude: Double? = entity.pickupLatitude
        if (_tmpPickupLatitude == null) {
          statement.bindNull(22)
        } else {
          statement.bindDouble(22, _tmpPickupLatitude)
        }
        val _tmpPickupLongitude: Double? = entity.pickupLongitude
        if (_tmpPickupLongitude == null) {
          statement.bindNull(23)
        } else {
          statement.bindDouble(23, _tmpPickupLongitude)
        }
        statement.bindText(24, entity.paymentType)
        val _tmpAdvanceAmount: Double? = entity.advanceAmount
        if (_tmpAdvanceAmount == null) {
          statement.bindNull(25)
        } else {
          statement.bindDouble(25, _tmpAdvanceAmount)
        }
        val _tmpBalanceAmount: Double? = entity.balanceAmount
        if (_tmpBalanceAmount == null) {
          statement.bindNull(26)
        } else {
          statement.bindDouble(26, _tmpBalanceAmount)
        }
        statement.bindText(27, entity.status)
        val _tmpBuyerAgreedAt: Long? = entity.buyerAgreedAt
        if (_tmpBuyerAgreedAt == null) {
          statement.bindNull(28)
        } else {
          statement.bindLong(28, _tmpBuyerAgreedAt)
        }
        val _tmpSellerAgreedAt: Long? = entity.sellerAgreedAt
        if (_tmpSellerAgreedAt == null) {
          statement.bindNull(29)
        } else {
          statement.bindLong(29, _tmpSellerAgreedAt)
        }
        val _tmpLockedAt: Long? = entity.lockedAt
        if (_tmpLockedAt == null) {
          statement.bindNull(30)
        } else {
          statement.bindLong(30, _tmpLockedAt)
        }
        val _tmpExpiresAt: Long? = entity.expiresAt
        if (_tmpExpiresAt == null) {
          statement.bindNull(31)
        } else {
          statement.bindLong(31, _tmpExpiresAt)
        }
        statement.bindLong(32, entity.version.toLong())
        val _tmpPreviousQuoteId: String? = entity.previousQuoteId
        if (_tmpPreviousQuoteId == null) {
          statement.bindNull(33)
        } else {
          statement.bindText(33, _tmpPreviousQuoteId)
        }
        val _tmpBuyerNotes: String? = entity.buyerNotes
        if (_tmpBuyerNotes == null) {
          statement.bindNull(34)
        } else {
          statement.bindText(34, _tmpBuyerNotes)
        }
        val _tmpSellerNotes: String? = entity.sellerNotes
        if (_tmpSellerNotes == null) {
          statement.bindNull(35)
        } else {
          statement.bindText(35, _tmpSellerNotes)
        }
        statement.bindLong(36, entity.createdAt)
        statement.bindLong(37, entity.updatedAt)
        val _tmp: Int = if (entity.dirty) 1 else 0
        statement.bindLong(38, _tmp.toLong())
      }
    }
  }

  public override suspend fun upsert(quote: OrderQuoteEntity): Unit = performSuspending(__db, false,
      true) { _connection ->
    __insertAdapterOfOrderQuoteEntity.insert(_connection, quote)
  }

  public override suspend fun findById(quoteId: String): OrderQuoteEntity? {
    val _sql: String = "SELECT * FROM order_quotes WHERE quoteId = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, quoteId)
        val _columnIndexOfQuoteId: Int = getColumnIndexOrThrow(_stmt, "quoteId")
        val _columnIndexOfOrderId: Int = getColumnIndexOrThrow(_stmt, "orderId")
        val _columnIndexOfBuyerId: Int = getColumnIndexOrThrow(_stmt, "buyerId")
        val _columnIndexOfSellerId: Int = getColumnIndexOrThrow(_stmt, "sellerId")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfProductName: Int = getColumnIndexOrThrow(_stmt, "productName")
        val _columnIndexOfQuantity: Int = getColumnIndexOrThrow(_stmt, "quantity")
        val _columnIndexOfUnit: Int = getColumnIndexOrThrow(_stmt, "unit")
        val _columnIndexOfBasePrice: Int = getColumnIndexOrThrow(_stmt, "basePrice")
        val _columnIndexOfTotalProductPrice: Int = getColumnIndexOrThrow(_stmt, "totalProductPrice")
        val _columnIndexOfDeliveryCharge: Int = getColumnIndexOrThrow(_stmt, "deliveryCharge")
        val _columnIndexOfPackingCharge: Int = getColumnIndexOrThrow(_stmt, "packingCharge")
        val _columnIndexOfPlatformFee: Int = getColumnIndexOrThrow(_stmt, "platformFee")
        val _columnIndexOfDiscount: Int = getColumnIndexOrThrow(_stmt, "discount")
        val _columnIndexOfFinalTotal: Int = getColumnIndexOrThrow(_stmt, "finalTotal")
        val _columnIndexOfDeliveryType: Int = getColumnIndexOrThrow(_stmt, "deliveryType")
        val _columnIndexOfDeliveryDistance: Int = getColumnIndexOrThrow(_stmt, "deliveryDistance")
        val _columnIndexOfDeliveryAddress: Int = getColumnIndexOrThrow(_stmt, "deliveryAddress")
        val _columnIndexOfDeliveryLatitude: Int = getColumnIndexOrThrow(_stmt, "deliveryLatitude")
        val _columnIndexOfDeliveryLongitude: Int = getColumnIndexOrThrow(_stmt, "deliveryLongitude")
        val _columnIndexOfPickupAddress: Int = getColumnIndexOrThrow(_stmt, "pickupAddress")
        val _columnIndexOfPickupLatitude: Int = getColumnIndexOrThrow(_stmt, "pickupLatitude")
        val _columnIndexOfPickupLongitude: Int = getColumnIndexOrThrow(_stmt, "pickupLongitude")
        val _columnIndexOfPaymentType: Int = getColumnIndexOrThrow(_stmt, "paymentType")
        val _columnIndexOfAdvanceAmount: Int = getColumnIndexOrThrow(_stmt, "advanceAmount")
        val _columnIndexOfBalanceAmount: Int = getColumnIndexOrThrow(_stmt, "balanceAmount")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfBuyerAgreedAt: Int = getColumnIndexOrThrow(_stmt, "buyerAgreedAt")
        val _columnIndexOfSellerAgreedAt: Int = getColumnIndexOrThrow(_stmt, "sellerAgreedAt")
        val _columnIndexOfLockedAt: Int = getColumnIndexOrThrow(_stmt, "lockedAt")
        val _columnIndexOfExpiresAt: Int = getColumnIndexOrThrow(_stmt, "expiresAt")
        val _columnIndexOfVersion: Int = getColumnIndexOrThrow(_stmt, "version")
        val _columnIndexOfPreviousQuoteId: Int = getColumnIndexOrThrow(_stmt, "previousQuoteId")
        val _columnIndexOfBuyerNotes: Int = getColumnIndexOrThrow(_stmt, "buyerNotes")
        val _columnIndexOfSellerNotes: Int = getColumnIndexOrThrow(_stmt, "sellerNotes")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _result: OrderQuoteEntity?
        if (_stmt.step()) {
          val _tmpQuoteId: String
          _tmpQuoteId = _stmt.getText(_columnIndexOfQuoteId)
          val _tmpOrderId: String
          _tmpOrderId = _stmt.getText(_columnIndexOfOrderId)
          val _tmpBuyerId: String
          _tmpBuyerId = _stmt.getText(_columnIndexOfBuyerId)
          val _tmpSellerId: String
          _tmpSellerId = _stmt.getText(_columnIndexOfSellerId)
          val _tmpProductId: String
          _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          val _tmpProductName: String
          _tmpProductName = _stmt.getText(_columnIndexOfProductName)
          val _tmpQuantity: Double
          _tmpQuantity = _stmt.getDouble(_columnIndexOfQuantity)
          val _tmpUnit: String
          _tmpUnit = _stmt.getText(_columnIndexOfUnit)
          val _tmpBasePrice: Double
          _tmpBasePrice = _stmt.getDouble(_columnIndexOfBasePrice)
          val _tmpTotalProductPrice: Double
          _tmpTotalProductPrice = _stmt.getDouble(_columnIndexOfTotalProductPrice)
          val _tmpDeliveryCharge: Double
          _tmpDeliveryCharge = _stmt.getDouble(_columnIndexOfDeliveryCharge)
          val _tmpPackingCharge: Double
          _tmpPackingCharge = _stmt.getDouble(_columnIndexOfPackingCharge)
          val _tmpPlatformFee: Double
          _tmpPlatformFee = _stmt.getDouble(_columnIndexOfPlatformFee)
          val _tmpDiscount: Double
          _tmpDiscount = _stmt.getDouble(_columnIndexOfDiscount)
          val _tmpFinalTotal: Double
          _tmpFinalTotal = _stmt.getDouble(_columnIndexOfFinalTotal)
          val _tmpDeliveryType: String
          _tmpDeliveryType = _stmt.getText(_columnIndexOfDeliveryType)
          val _tmpDeliveryDistance: Double?
          if (_stmt.isNull(_columnIndexOfDeliveryDistance)) {
            _tmpDeliveryDistance = null
          } else {
            _tmpDeliveryDistance = _stmt.getDouble(_columnIndexOfDeliveryDistance)
          }
          val _tmpDeliveryAddress: String?
          if (_stmt.isNull(_columnIndexOfDeliveryAddress)) {
            _tmpDeliveryAddress = null
          } else {
            _tmpDeliveryAddress = _stmt.getText(_columnIndexOfDeliveryAddress)
          }
          val _tmpDeliveryLatitude: Double?
          if (_stmt.isNull(_columnIndexOfDeliveryLatitude)) {
            _tmpDeliveryLatitude = null
          } else {
            _tmpDeliveryLatitude = _stmt.getDouble(_columnIndexOfDeliveryLatitude)
          }
          val _tmpDeliveryLongitude: Double?
          if (_stmt.isNull(_columnIndexOfDeliveryLongitude)) {
            _tmpDeliveryLongitude = null
          } else {
            _tmpDeliveryLongitude = _stmt.getDouble(_columnIndexOfDeliveryLongitude)
          }
          val _tmpPickupAddress: String?
          if (_stmt.isNull(_columnIndexOfPickupAddress)) {
            _tmpPickupAddress = null
          } else {
            _tmpPickupAddress = _stmt.getText(_columnIndexOfPickupAddress)
          }
          val _tmpPickupLatitude: Double?
          if (_stmt.isNull(_columnIndexOfPickupLatitude)) {
            _tmpPickupLatitude = null
          } else {
            _tmpPickupLatitude = _stmt.getDouble(_columnIndexOfPickupLatitude)
          }
          val _tmpPickupLongitude: Double?
          if (_stmt.isNull(_columnIndexOfPickupLongitude)) {
            _tmpPickupLongitude = null
          } else {
            _tmpPickupLongitude = _stmt.getDouble(_columnIndexOfPickupLongitude)
          }
          val _tmpPaymentType: String
          _tmpPaymentType = _stmt.getText(_columnIndexOfPaymentType)
          val _tmpAdvanceAmount: Double?
          if (_stmt.isNull(_columnIndexOfAdvanceAmount)) {
            _tmpAdvanceAmount = null
          } else {
            _tmpAdvanceAmount = _stmt.getDouble(_columnIndexOfAdvanceAmount)
          }
          val _tmpBalanceAmount: Double?
          if (_stmt.isNull(_columnIndexOfBalanceAmount)) {
            _tmpBalanceAmount = null
          } else {
            _tmpBalanceAmount = _stmt.getDouble(_columnIndexOfBalanceAmount)
          }
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpBuyerAgreedAt: Long?
          if (_stmt.isNull(_columnIndexOfBuyerAgreedAt)) {
            _tmpBuyerAgreedAt = null
          } else {
            _tmpBuyerAgreedAt = _stmt.getLong(_columnIndexOfBuyerAgreedAt)
          }
          val _tmpSellerAgreedAt: Long?
          if (_stmt.isNull(_columnIndexOfSellerAgreedAt)) {
            _tmpSellerAgreedAt = null
          } else {
            _tmpSellerAgreedAt = _stmt.getLong(_columnIndexOfSellerAgreedAt)
          }
          val _tmpLockedAt: Long?
          if (_stmt.isNull(_columnIndexOfLockedAt)) {
            _tmpLockedAt = null
          } else {
            _tmpLockedAt = _stmt.getLong(_columnIndexOfLockedAt)
          }
          val _tmpExpiresAt: Long?
          if (_stmt.isNull(_columnIndexOfExpiresAt)) {
            _tmpExpiresAt = null
          } else {
            _tmpExpiresAt = _stmt.getLong(_columnIndexOfExpiresAt)
          }
          val _tmpVersion: Int
          _tmpVersion = _stmt.getLong(_columnIndexOfVersion).toInt()
          val _tmpPreviousQuoteId: String?
          if (_stmt.isNull(_columnIndexOfPreviousQuoteId)) {
            _tmpPreviousQuoteId = null
          } else {
            _tmpPreviousQuoteId = _stmt.getText(_columnIndexOfPreviousQuoteId)
          }
          val _tmpBuyerNotes: String?
          if (_stmt.isNull(_columnIndexOfBuyerNotes)) {
            _tmpBuyerNotes = null
          } else {
            _tmpBuyerNotes = _stmt.getText(_columnIndexOfBuyerNotes)
          }
          val _tmpSellerNotes: String?
          if (_stmt.isNull(_columnIndexOfSellerNotes)) {
            _tmpSellerNotes = null
          } else {
            _tmpSellerNotes = _stmt.getText(_columnIndexOfSellerNotes)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpDirty: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp != 0
          _result =
              OrderQuoteEntity(_tmpQuoteId,_tmpOrderId,_tmpBuyerId,_tmpSellerId,_tmpProductId,_tmpProductName,_tmpQuantity,_tmpUnit,_tmpBasePrice,_tmpTotalProductPrice,_tmpDeliveryCharge,_tmpPackingCharge,_tmpPlatformFee,_tmpDiscount,_tmpFinalTotal,_tmpDeliveryType,_tmpDeliveryDistance,_tmpDeliveryAddress,_tmpDeliveryLatitude,_tmpDeliveryLongitude,_tmpPickupAddress,_tmpPickupLatitude,_tmpPickupLongitude,_tmpPaymentType,_tmpAdvanceAmount,_tmpBalanceAmount,_tmpStatus,_tmpBuyerAgreedAt,_tmpSellerAgreedAt,_tmpLockedAt,_tmpExpiresAt,_tmpVersion,_tmpPreviousQuoteId,_tmpBuyerNotes,_tmpSellerNotes,_tmpCreatedAt,_tmpUpdatedAt,_tmpDirty)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getLatestQuote(orderId: String): OrderQuoteEntity? {
    val _sql: String = "SELECT * FROM order_quotes WHERE orderId = ? ORDER BY version DESC LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, orderId)
        val _columnIndexOfQuoteId: Int = getColumnIndexOrThrow(_stmt, "quoteId")
        val _columnIndexOfOrderId: Int = getColumnIndexOrThrow(_stmt, "orderId")
        val _columnIndexOfBuyerId: Int = getColumnIndexOrThrow(_stmt, "buyerId")
        val _columnIndexOfSellerId: Int = getColumnIndexOrThrow(_stmt, "sellerId")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfProductName: Int = getColumnIndexOrThrow(_stmt, "productName")
        val _columnIndexOfQuantity: Int = getColumnIndexOrThrow(_stmt, "quantity")
        val _columnIndexOfUnit: Int = getColumnIndexOrThrow(_stmt, "unit")
        val _columnIndexOfBasePrice: Int = getColumnIndexOrThrow(_stmt, "basePrice")
        val _columnIndexOfTotalProductPrice: Int = getColumnIndexOrThrow(_stmt, "totalProductPrice")
        val _columnIndexOfDeliveryCharge: Int = getColumnIndexOrThrow(_stmt, "deliveryCharge")
        val _columnIndexOfPackingCharge: Int = getColumnIndexOrThrow(_stmt, "packingCharge")
        val _columnIndexOfPlatformFee: Int = getColumnIndexOrThrow(_stmt, "platformFee")
        val _columnIndexOfDiscount: Int = getColumnIndexOrThrow(_stmt, "discount")
        val _columnIndexOfFinalTotal: Int = getColumnIndexOrThrow(_stmt, "finalTotal")
        val _columnIndexOfDeliveryType: Int = getColumnIndexOrThrow(_stmt, "deliveryType")
        val _columnIndexOfDeliveryDistance: Int = getColumnIndexOrThrow(_stmt, "deliveryDistance")
        val _columnIndexOfDeliveryAddress: Int = getColumnIndexOrThrow(_stmt, "deliveryAddress")
        val _columnIndexOfDeliveryLatitude: Int = getColumnIndexOrThrow(_stmt, "deliveryLatitude")
        val _columnIndexOfDeliveryLongitude: Int = getColumnIndexOrThrow(_stmt, "deliveryLongitude")
        val _columnIndexOfPickupAddress: Int = getColumnIndexOrThrow(_stmt, "pickupAddress")
        val _columnIndexOfPickupLatitude: Int = getColumnIndexOrThrow(_stmt, "pickupLatitude")
        val _columnIndexOfPickupLongitude: Int = getColumnIndexOrThrow(_stmt, "pickupLongitude")
        val _columnIndexOfPaymentType: Int = getColumnIndexOrThrow(_stmt, "paymentType")
        val _columnIndexOfAdvanceAmount: Int = getColumnIndexOrThrow(_stmt, "advanceAmount")
        val _columnIndexOfBalanceAmount: Int = getColumnIndexOrThrow(_stmt, "balanceAmount")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfBuyerAgreedAt: Int = getColumnIndexOrThrow(_stmt, "buyerAgreedAt")
        val _columnIndexOfSellerAgreedAt: Int = getColumnIndexOrThrow(_stmt, "sellerAgreedAt")
        val _columnIndexOfLockedAt: Int = getColumnIndexOrThrow(_stmt, "lockedAt")
        val _columnIndexOfExpiresAt: Int = getColumnIndexOrThrow(_stmt, "expiresAt")
        val _columnIndexOfVersion: Int = getColumnIndexOrThrow(_stmt, "version")
        val _columnIndexOfPreviousQuoteId: Int = getColumnIndexOrThrow(_stmt, "previousQuoteId")
        val _columnIndexOfBuyerNotes: Int = getColumnIndexOrThrow(_stmt, "buyerNotes")
        val _columnIndexOfSellerNotes: Int = getColumnIndexOrThrow(_stmt, "sellerNotes")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _result: OrderQuoteEntity?
        if (_stmt.step()) {
          val _tmpQuoteId: String
          _tmpQuoteId = _stmt.getText(_columnIndexOfQuoteId)
          val _tmpOrderId: String
          _tmpOrderId = _stmt.getText(_columnIndexOfOrderId)
          val _tmpBuyerId: String
          _tmpBuyerId = _stmt.getText(_columnIndexOfBuyerId)
          val _tmpSellerId: String
          _tmpSellerId = _stmt.getText(_columnIndexOfSellerId)
          val _tmpProductId: String
          _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          val _tmpProductName: String
          _tmpProductName = _stmt.getText(_columnIndexOfProductName)
          val _tmpQuantity: Double
          _tmpQuantity = _stmt.getDouble(_columnIndexOfQuantity)
          val _tmpUnit: String
          _tmpUnit = _stmt.getText(_columnIndexOfUnit)
          val _tmpBasePrice: Double
          _tmpBasePrice = _stmt.getDouble(_columnIndexOfBasePrice)
          val _tmpTotalProductPrice: Double
          _tmpTotalProductPrice = _stmt.getDouble(_columnIndexOfTotalProductPrice)
          val _tmpDeliveryCharge: Double
          _tmpDeliveryCharge = _stmt.getDouble(_columnIndexOfDeliveryCharge)
          val _tmpPackingCharge: Double
          _tmpPackingCharge = _stmt.getDouble(_columnIndexOfPackingCharge)
          val _tmpPlatformFee: Double
          _tmpPlatformFee = _stmt.getDouble(_columnIndexOfPlatformFee)
          val _tmpDiscount: Double
          _tmpDiscount = _stmt.getDouble(_columnIndexOfDiscount)
          val _tmpFinalTotal: Double
          _tmpFinalTotal = _stmt.getDouble(_columnIndexOfFinalTotal)
          val _tmpDeliveryType: String
          _tmpDeliveryType = _stmt.getText(_columnIndexOfDeliveryType)
          val _tmpDeliveryDistance: Double?
          if (_stmt.isNull(_columnIndexOfDeliveryDistance)) {
            _tmpDeliveryDistance = null
          } else {
            _tmpDeliveryDistance = _stmt.getDouble(_columnIndexOfDeliveryDistance)
          }
          val _tmpDeliveryAddress: String?
          if (_stmt.isNull(_columnIndexOfDeliveryAddress)) {
            _tmpDeliveryAddress = null
          } else {
            _tmpDeliveryAddress = _stmt.getText(_columnIndexOfDeliveryAddress)
          }
          val _tmpDeliveryLatitude: Double?
          if (_stmt.isNull(_columnIndexOfDeliveryLatitude)) {
            _tmpDeliveryLatitude = null
          } else {
            _tmpDeliveryLatitude = _stmt.getDouble(_columnIndexOfDeliveryLatitude)
          }
          val _tmpDeliveryLongitude: Double?
          if (_stmt.isNull(_columnIndexOfDeliveryLongitude)) {
            _tmpDeliveryLongitude = null
          } else {
            _tmpDeliveryLongitude = _stmt.getDouble(_columnIndexOfDeliveryLongitude)
          }
          val _tmpPickupAddress: String?
          if (_stmt.isNull(_columnIndexOfPickupAddress)) {
            _tmpPickupAddress = null
          } else {
            _tmpPickupAddress = _stmt.getText(_columnIndexOfPickupAddress)
          }
          val _tmpPickupLatitude: Double?
          if (_stmt.isNull(_columnIndexOfPickupLatitude)) {
            _tmpPickupLatitude = null
          } else {
            _tmpPickupLatitude = _stmt.getDouble(_columnIndexOfPickupLatitude)
          }
          val _tmpPickupLongitude: Double?
          if (_stmt.isNull(_columnIndexOfPickupLongitude)) {
            _tmpPickupLongitude = null
          } else {
            _tmpPickupLongitude = _stmt.getDouble(_columnIndexOfPickupLongitude)
          }
          val _tmpPaymentType: String
          _tmpPaymentType = _stmt.getText(_columnIndexOfPaymentType)
          val _tmpAdvanceAmount: Double?
          if (_stmt.isNull(_columnIndexOfAdvanceAmount)) {
            _tmpAdvanceAmount = null
          } else {
            _tmpAdvanceAmount = _stmt.getDouble(_columnIndexOfAdvanceAmount)
          }
          val _tmpBalanceAmount: Double?
          if (_stmt.isNull(_columnIndexOfBalanceAmount)) {
            _tmpBalanceAmount = null
          } else {
            _tmpBalanceAmount = _stmt.getDouble(_columnIndexOfBalanceAmount)
          }
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpBuyerAgreedAt: Long?
          if (_stmt.isNull(_columnIndexOfBuyerAgreedAt)) {
            _tmpBuyerAgreedAt = null
          } else {
            _tmpBuyerAgreedAt = _stmt.getLong(_columnIndexOfBuyerAgreedAt)
          }
          val _tmpSellerAgreedAt: Long?
          if (_stmt.isNull(_columnIndexOfSellerAgreedAt)) {
            _tmpSellerAgreedAt = null
          } else {
            _tmpSellerAgreedAt = _stmt.getLong(_columnIndexOfSellerAgreedAt)
          }
          val _tmpLockedAt: Long?
          if (_stmt.isNull(_columnIndexOfLockedAt)) {
            _tmpLockedAt = null
          } else {
            _tmpLockedAt = _stmt.getLong(_columnIndexOfLockedAt)
          }
          val _tmpExpiresAt: Long?
          if (_stmt.isNull(_columnIndexOfExpiresAt)) {
            _tmpExpiresAt = null
          } else {
            _tmpExpiresAt = _stmt.getLong(_columnIndexOfExpiresAt)
          }
          val _tmpVersion: Int
          _tmpVersion = _stmt.getLong(_columnIndexOfVersion).toInt()
          val _tmpPreviousQuoteId: String?
          if (_stmt.isNull(_columnIndexOfPreviousQuoteId)) {
            _tmpPreviousQuoteId = null
          } else {
            _tmpPreviousQuoteId = _stmt.getText(_columnIndexOfPreviousQuoteId)
          }
          val _tmpBuyerNotes: String?
          if (_stmt.isNull(_columnIndexOfBuyerNotes)) {
            _tmpBuyerNotes = null
          } else {
            _tmpBuyerNotes = _stmt.getText(_columnIndexOfBuyerNotes)
          }
          val _tmpSellerNotes: String?
          if (_stmt.isNull(_columnIndexOfSellerNotes)) {
            _tmpSellerNotes = null
          } else {
            _tmpSellerNotes = _stmt.getText(_columnIndexOfSellerNotes)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpDirty: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp != 0
          _result =
              OrderQuoteEntity(_tmpQuoteId,_tmpOrderId,_tmpBuyerId,_tmpSellerId,_tmpProductId,_tmpProductName,_tmpQuantity,_tmpUnit,_tmpBasePrice,_tmpTotalProductPrice,_tmpDeliveryCharge,_tmpPackingCharge,_tmpPlatformFee,_tmpDiscount,_tmpFinalTotal,_tmpDeliveryType,_tmpDeliveryDistance,_tmpDeliveryAddress,_tmpDeliveryLatitude,_tmpDeliveryLongitude,_tmpPickupAddress,_tmpPickupLatitude,_tmpPickupLongitude,_tmpPaymentType,_tmpAdvanceAmount,_tmpBalanceAmount,_tmpStatus,_tmpBuyerAgreedAt,_tmpSellerAgreedAt,_tmpLockedAt,_tmpExpiresAt,_tmpVersion,_tmpPreviousQuoteId,_tmpBuyerNotes,_tmpSellerNotes,_tmpCreatedAt,_tmpUpdatedAt,_tmpDirty)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getQuoteHistory(orderId: String): Flow<List<OrderQuoteEntity>> {
    val _sql: String = "SELECT * FROM order_quotes WHERE orderId = ? ORDER BY version DESC"
    return createFlow(__db, false, arrayOf("order_quotes")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, orderId)
        val _columnIndexOfQuoteId: Int = getColumnIndexOrThrow(_stmt, "quoteId")
        val _columnIndexOfOrderId: Int = getColumnIndexOrThrow(_stmt, "orderId")
        val _columnIndexOfBuyerId: Int = getColumnIndexOrThrow(_stmt, "buyerId")
        val _columnIndexOfSellerId: Int = getColumnIndexOrThrow(_stmt, "sellerId")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfProductName: Int = getColumnIndexOrThrow(_stmt, "productName")
        val _columnIndexOfQuantity: Int = getColumnIndexOrThrow(_stmt, "quantity")
        val _columnIndexOfUnit: Int = getColumnIndexOrThrow(_stmt, "unit")
        val _columnIndexOfBasePrice: Int = getColumnIndexOrThrow(_stmt, "basePrice")
        val _columnIndexOfTotalProductPrice: Int = getColumnIndexOrThrow(_stmt, "totalProductPrice")
        val _columnIndexOfDeliveryCharge: Int = getColumnIndexOrThrow(_stmt, "deliveryCharge")
        val _columnIndexOfPackingCharge: Int = getColumnIndexOrThrow(_stmt, "packingCharge")
        val _columnIndexOfPlatformFee: Int = getColumnIndexOrThrow(_stmt, "platformFee")
        val _columnIndexOfDiscount: Int = getColumnIndexOrThrow(_stmt, "discount")
        val _columnIndexOfFinalTotal: Int = getColumnIndexOrThrow(_stmt, "finalTotal")
        val _columnIndexOfDeliveryType: Int = getColumnIndexOrThrow(_stmt, "deliveryType")
        val _columnIndexOfDeliveryDistance: Int = getColumnIndexOrThrow(_stmt, "deliveryDistance")
        val _columnIndexOfDeliveryAddress: Int = getColumnIndexOrThrow(_stmt, "deliveryAddress")
        val _columnIndexOfDeliveryLatitude: Int = getColumnIndexOrThrow(_stmt, "deliveryLatitude")
        val _columnIndexOfDeliveryLongitude: Int = getColumnIndexOrThrow(_stmt, "deliveryLongitude")
        val _columnIndexOfPickupAddress: Int = getColumnIndexOrThrow(_stmt, "pickupAddress")
        val _columnIndexOfPickupLatitude: Int = getColumnIndexOrThrow(_stmt, "pickupLatitude")
        val _columnIndexOfPickupLongitude: Int = getColumnIndexOrThrow(_stmt, "pickupLongitude")
        val _columnIndexOfPaymentType: Int = getColumnIndexOrThrow(_stmt, "paymentType")
        val _columnIndexOfAdvanceAmount: Int = getColumnIndexOrThrow(_stmt, "advanceAmount")
        val _columnIndexOfBalanceAmount: Int = getColumnIndexOrThrow(_stmt, "balanceAmount")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfBuyerAgreedAt: Int = getColumnIndexOrThrow(_stmt, "buyerAgreedAt")
        val _columnIndexOfSellerAgreedAt: Int = getColumnIndexOrThrow(_stmt, "sellerAgreedAt")
        val _columnIndexOfLockedAt: Int = getColumnIndexOrThrow(_stmt, "lockedAt")
        val _columnIndexOfExpiresAt: Int = getColumnIndexOrThrow(_stmt, "expiresAt")
        val _columnIndexOfVersion: Int = getColumnIndexOrThrow(_stmt, "version")
        val _columnIndexOfPreviousQuoteId: Int = getColumnIndexOrThrow(_stmt, "previousQuoteId")
        val _columnIndexOfBuyerNotes: Int = getColumnIndexOrThrow(_stmt, "buyerNotes")
        val _columnIndexOfSellerNotes: Int = getColumnIndexOrThrow(_stmt, "sellerNotes")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _result: MutableList<OrderQuoteEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: OrderQuoteEntity
          val _tmpQuoteId: String
          _tmpQuoteId = _stmt.getText(_columnIndexOfQuoteId)
          val _tmpOrderId: String
          _tmpOrderId = _stmt.getText(_columnIndexOfOrderId)
          val _tmpBuyerId: String
          _tmpBuyerId = _stmt.getText(_columnIndexOfBuyerId)
          val _tmpSellerId: String
          _tmpSellerId = _stmt.getText(_columnIndexOfSellerId)
          val _tmpProductId: String
          _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          val _tmpProductName: String
          _tmpProductName = _stmt.getText(_columnIndexOfProductName)
          val _tmpQuantity: Double
          _tmpQuantity = _stmt.getDouble(_columnIndexOfQuantity)
          val _tmpUnit: String
          _tmpUnit = _stmt.getText(_columnIndexOfUnit)
          val _tmpBasePrice: Double
          _tmpBasePrice = _stmt.getDouble(_columnIndexOfBasePrice)
          val _tmpTotalProductPrice: Double
          _tmpTotalProductPrice = _stmt.getDouble(_columnIndexOfTotalProductPrice)
          val _tmpDeliveryCharge: Double
          _tmpDeliveryCharge = _stmt.getDouble(_columnIndexOfDeliveryCharge)
          val _tmpPackingCharge: Double
          _tmpPackingCharge = _stmt.getDouble(_columnIndexOfPackingCharge)
          val _tmpPlatformFee: Double
          _tmpPlatformFee = _stmt.getDouble(_columnIndexOfPlatformFee)
          val _tmpDiscount: Double
          _tmpDiscount = _stmt.getDouble(_columnIndexOfDiscount)
          val _tmpFinalTotal: Double
          _tmpFinalTotal = _stmt.getDouble(_columnIndexOfFinalTotal)
          val _tmpDeliveryType: String
          _tmpDeliveryType = _stmt.getText(_columnIndexOfDeliveryType)
          val _tmpDeliveryDistance: Double?
          if (_stmt.isNull(_columnIndexOfDeliveryDistance)) {
            _tmpDeliveryDistance = null
          } else {
            _tmpDeliveryDistance = _stmt.getDouble(_columnIndexOfDeliveryDistance)
          }
          val _tmpDeliveryAddress: String?
          if (_stmt.isNull(_columnIndexOfDeliveryAddress)) {
            _tmpDeliveryAddress = null
          } else {
            _tmpDeliveryAddress = _stmt.getText(_columnIndexOfDeliveryAddress)
          }
          val _tmpDeliveryLatitude: Double?
          if (_stmt.isNull(_columnIndexOfDeliveryLatitude)) {
            _tmpDeliveryLatitude = null
          } else {
            _tmpDeliveryLatitude = _stmt.getDouble(_columnIndexOfDeliveryLatitude)
          }
          val _tmpDeliveryLongitude: Double?
          if (_stmt.isNull(_columnIndexOfDeliveryLongitude)) {
            _tmpDeliveryLongitude = null
          } else {
            _tmpDeliveryLongitude = _stmt.getDouble(_columnIndexOfDeliveryLongitude)
          }
          val _tmpPickupAddress: String?
          if (_stmt.isNull(_columnIndexOfPickupAddress)) {
            _tmpPickupAddress = null
          } else {
            _tmpPickupAddress = _stmt.getText(_columnIndexOfPickupAddress)
          }
          val _tmpPickupLatitude: Double?
          if (_stmt.isNull(_columnIndexOfPickupLatitude)) {
            _tmpPickupLatitude = null
          } else {
            _tmpPickupLatitude = _stmt.getDouble(_columnIndexOfPickupLatitude)
          }
          val _tmpPickupLongitude: Double?
          if (_stmt.isNull(_columnIndexOfPickupLongitude)) {
            _tmpPickupLongitude = null
          } else {
            _tmpPickupLongitude = _stmt.getDouble(_columnIndexOfPickupLongitude)
          }
          val _tmpPaymentType: String
          _tmpPaymentType = _stmt.getText(_columnIndexOfPaymentType)
          val _tmpAdvanceAmount: Double?
          if (_stmt.isNull(_columnIndexOfAdvanceAmount)) {
            _tmpAdvanceAmount = null
          } else {
            _tmpAdvanceAmount = _stmt.getDouble(_columnIndexOfAdvanceAmount)
          }
          val _tmpBalanceAmount: Double?
          if (_stmt.isNull(_columnIndexOfBalanceAmount)) {
            _tmpBalanceAmount = null
          } else {
            _tmpBalanceAmount = _stmt.getDouble(_columnIndexOfBalanceAmount)
          }
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpBuyerAgreedAt: Long?
          if (_stmt.isNull(_columnIndexOfBuyerAgreedAt)) {
            _tmpBuyerAgreedAt = null
          } else {
            _tmpBuyerAgreedAt = _stmt.getLong(_columnIndexOfBuyerAgreedAt)
          }
          val _tmpSellerAgreedAt: Long?
          if (_stmt.isNull(_columnIndexOfSellerAgreedAt)) {
            _tmpSellerAgreedAt = null
          } else {
            _tmpSellerAgreedAt = _stmt.getLong(_columnIndexOfSellerAgreedAt)
          }
          val _tmpLockedAt: Long?
          if (_stmt.isNull(_columnIndexOfLockedAt)) {
            _tmpLockedAt = null
          } else {
            _tmpLockedAt = _stmt.getLong(_columnIndexOfLockedAt)
          }
          val _tmpExpiresAt: Long?
          if (_stmt.isNull(_columnIndexOfExpiresAt)) {
            _tmpExpiresAt = null
          } else {
            _tmpExpiresAt = _stmt.getLong(_columnIndexOfExpiresAt)
          }
          val _tmpVersion: Int
          _tmpVersion = _stmt.getLong(_columnIndexOfVersion).toInt()
          val _tmpPreviousQuoteId: String?
          if (_stmt.isNull(_columnIndexOfPreviousQuoteId)) {
            _tmpPreviousQuoteId = null
          } else {
            _tmpPreviousQuoteId = _stmt.getText(_columnIndexOfPreviousQuoteId)
          }
          val _tmpBuyerNotes: String?
          if (_stmt.isNull(_columnIndexOfBuyerNotes)) {
            _tmpBuyerNotes = null
          } else {
            _tmpBuyerNotes = _stmt.getText(_columnIndexOfBuyerNotes)
          }
          val _tmpSellerNotes: String?
          if (_stmt.isNull(_columnIndexOfSellerNotes)) {
            _tmpSellerNotes = null
          } else {
            _tmpSellerNotes = _stmt.getText(_columnIndexOfSellerNotes)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpDirty: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp != 0
          _item =
              OrderQuoteEntity(_tmpQuoteId,_tmpOrderId,_tmpBuyerId,_tmpSellerId,_tmpProductId,_tmpProductName,_tmpQuantity,_tmpUnit,_tmpBasePrice,_tmpTotalProductPrice,_tmpDeliveryCharge,_tmpPackingCharge,_tmpPlatformFee,_tmpDiscount,_tmpFinalTotal,_tmpDeliveryType,_tmpDeliveryDistance,_tmpDeliveryAddress,_tmpDeliveryLatitude,_tmpDeliveryLongitude,_tmpPickupAddress,_tmpPickupLatitude,_tmpPickupLongitude,_tmpPaymentType,_tmpAdvanceAmount,_tmpBalanceAmount,_tmpStatus,_tmpBuyerAgreedAt,_tmpSellerAgreedAt,_tmpLockedAt,_tmpExpiresAt,_tmpVersion,_tmpPreviousQuoteId,_tmpBuyerNotes,_tmpSellerNotes,_tmpCreatedAt,_tmpUpdatedAt,_tmpDirty)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getBuyerActiveQuotes(buyerId: String): Flow<List<OrderQuoteEntity>> {
    val _sql: String =
        "SELECT * FROM order_quotes WHERE buyerId = ? AND status NOT IN ('EXPIRED', 'REJECTED') ORDER BY createdAt DESC"
    return createFlow(__db, false, arrayOf("order_quotes")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, buyerId)
        val _columnIndexOfQuoteId: Int = getColumnIndexOrThrow(_stmt, "quoteId")
        val _columnIndexOfOrderId: Int = getColumnIndexOrThrow(_stmt, "orderId")
        val _columnIndexOfBuyerId: Int = getColumnIndexOrThrow(_stmt, "buyerId")
        val _columnIndexOfSellerId: Int = getColumnIndexOrThrow(_stmt, "sellerId")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfProductName: Int = getColumnIndexOrThrow(_stmt, "productName")
        val _columnIndexOfQuantity: Int = getColumnIndexOrThrow(_stmt, "quantity")
        val _columnIndexOfUnit: Int = getColumnIndexOrThrow(_stmt, "unit")
        val _columnIndexOfBasePrice: Int = getColumnIndexOrThrow(_stmt, "basePrice")
        val _columnIndexOfTotalProductPrice: Int = getColumnIndexOrThrow(_stmt, "totalProductPrice")
        val _columnIndexOfDeliveryCharge: Int = getColumnIndexOrThrow(_stmt, "deliveryCharge")
        val _columnIndexOfPackingCharge: Int = getColumnIndexOrThrow(_stmt, "packingCharge")
        val _columnIndexOfPlatformFee: Int = getColumnIndexOrThrow(_stmt, "platformFee")
        val _columnIndexOfDiscount: Int = getColumnIndexOrThrow(_stmt, "discount")
        val _columnIndexOfFinalTotal: Int = getColumnIndexOrThrow(_stmt, "finalTotal")
        val _columnIndexOfDeliveryType: Int = getColumnIndexOrThrow(_stmt, "deliveryType")
        val _columnIndexOfDeliveryDistance: Int = getColumnIndexOrThrow(_stmt, "deliveryDistance")
        val _columnIndexOfDeliveryAddress: Int = getColumnIndexOrThrow(_stmt, "deliveryAddress")
        val _columnIndexOfDeliveryLatitude: Int = getColumnIndexOrThrow(_stmt, "deliveryLatitude")
        val _columnIndexOfDeliveryLongitude: Int = getColumnIndexOrThrow(_stmt, "deliveryLongitude")
        val _columnIndexOfPickupAddress: Int = getColumnIndexOrThrow(_stmt, "pickupAddress")
        val _columnIndexOfPickupLatitude: Int = getColumnIndexOrThrow(_stmt, "pickupLatitude")
        val _columnIndexOfPickupLongitude: Int = getColumnIndexOrThrow(_stmt, "pickupLongitude")
        val _columnIndexOfPaymentType: Int = getColumnIndexOrThrow(_stmt, "paymentType")
        val _columnIndexOfAdvanceAmount: Int = getColumnIndexOrThrow(_stmt, "advanceAmount")
        val _columnIndexOfBalanceAmount: Int = getColumnIndexOrThrow(_stmt, "balanceAmount")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfBuyerAgreedAt: Int = getColumnIndexOrThrow(_stmt, "buyerAgreedAt")
        val _columnIndexOfSellerAgreedAt: Int = getColumnIndexOrThrow(_stmt, "sellerAgreedAt")
        val _columnIndexOfLockedAt: Int = getColumnIndexOrThrow(_stmt, "lockedAt")
        val _columnIndexOfExpiresAt: Int = getColumnIndexOrThrow(_stmt, "expiresAt")
        val _columnIndexOfVersion: Int = getColumnIndexOrThrow(_stmt, "version")
        val _columnIndexOfPreviousQuoteId: Int = getColumnIndexOrThrow(_stmt, "previousQuoteId")
        val _columnIndexOfBuyerNotes: Int = getColumnIndexOrThrow(_stmt, "buyerNotes")
        val _columnIndexOfSellerNotes: Int = getColumnIndexOrThrow(_stmt, "sellerNotes")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _result: MutableList<OrderQuoteEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: OrderQuoteEntity
          val _tmpQuoteId: String
          _tmpQuoteId = _stmt.getText(_columnIndexOfQuoteId)
          val _tmpOrderId: String
          _tmpOrderId = _stmt.getText(_columnIndexOfOrderId)
          val _tmpBuyerId: String
          _tmpBuyerId = _stmt.getText(_columnIndexOfBuyerId)
          val _tmpSellerId: String
          _tmpSellerId = _stmt.getText(_columnIndexOfSellerId)
          val _tmpProductId: String
          _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          val _tmpProductName: String
          _tmpProductName = _stmt.getText(_columnIndexOfProductName)
          val _tmpQuantity: Double
          _tmpQuantity = _stmt.getDouble(_columnIndexOfQuantity)
          val _tmpUnit: String
          _tmpUnit = _stmt.getText(_columnIndexOfUnit)
          val _tmpBasePrice: Double
          _tmpBasePrice = _stmt.getDouble(_columnIndexOfBasePrice)
          val _tmpTotalProductPrice: Double
          _tmpTotalProductPrice = _stmt.getDouble(_columnIndexOfTotalProductPrice)
          val _tmpDeliveryCharge: Double
          _tmpDeliveryCharge = _stmt.getDouble(_columnIndexOfDeliveryCharge)
          val _tmpPackingCharge: Double
          _tmpPackingCharge = _stmt.getDouble(_columnIndexOfPackingCharge)
          val _tmpPlatformFee: Double
          _tmpPlatformFee = _stmt.getDouble(_columnIndexOfPlatformFee)
          val _tmpDiscount: Double
          _tmpDiscount = _stmt.getDouble(_columnIndexOfDiscount)
          val _tmpFinalTotal: Double
          _tmpFinalTotal = _stmt.getDouble(_columnIndexOfFinalTotal)
          val _tmpDeliveryType: String
          _tmpDeliveryType = _stmt.getText(_columnIndexOfDeliveryType)
          val _tmpDeliveryDistance: Double?
          if (_stmt.isNull(_columnIndexOfDeliveryDistance)) {
            _tmpDeliveryDistance = null
          } else {
            _tmpDeliveryDistance = _stmt.getDouble(_columnIndexOfDeliveryDistance)
          }
          val _tmpDeliveryAddress: String?
          if (_stmt.isNull(_columnIndexOfDeliveryAddress)) {
            _tmpDeliveryAddress = null
          } else {
            _tmpDeliveryAddress = _stmt.getText(_columnIndexOfDeliveryAddress)
          }
          val _tmpDeliveryLatitude: Double?
          if (_stmt.isNull(_columnIndexOfDeliveryLatitude)) {
            _tmpDeliveryLatitude = null
          } else {
            _tmpDeliveryLatitude = _stmt.getDouble(_columnIndexOfDeliveryLatitude)
          }
          val _tmpDeliveryLongitude: Double?
          if (_stmt.isNull(_columnIndexOfDeliveryLongitude)) {
            _tmpDeliveryLongitude = null
          } else {
            _tmpDeliveryLongitude = _stmt.getDouble(_columnIndexOfDeliveryLongitude)
          }
          val _tmpPickupAddress: String?
          if (_stmt.isNull(_columnIndexOfPickupAddress)) {
            _tmpPickupAddress = null
          } else {
            _tmpPickupAddress = _stmt.getText(_columnIndexOfPickupAddress)
          }
          val _tmpPickupLatitude: Double?
          if (_stmt.isNull(_columnIndexOfPickupLatitude)) {
            _tmpPickupLatitude = null
          } else {
            _tmpPickupLatitude = _stmt.getDouble(_columnIndexOfPickupLatitude)
          }
          val _tmpPickupLongitude: Double?
          if (_stmt.isNull(_columnIndexOfPickupLongitude)) {
            _tmpPickupLongitude = null
          } else {
            _tmpPickupLongitude = _stmt.getDouble(_columnIndexOfPickupLongitude)
          }
          val _tmpPaymentType: String
          _tmpPaymentType = _stmt.getText(_columnIndexOfPaymentType)
          val _tmpAdvanceAmount: Double?
          if (_stmt.isNull(_columnIndexOfAdvanceAmount)) {
            _tmpAdvanceAmount = null
          } else {
            _tmpAdvanceAmount = _stmt.getDouble(_columnIndexOfAdvanceAmount)
          }
          val _tmpBalanceAmount: Double?
          if (_stmt.isNull(_columnIndexOfBalanceAmount)) {
            _tmpBalanceAmount = null
          } else {
            _tmpBalanceAmount = _stmt.getDouble(_columnIndexOfBalanceAmount)
          }
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpBuyerAgreedAt: Long?
          if (_stmt.isNull(_columnIndexOfBuyerAgreedAt)) {
            _tmpBuyerAgreedAt = null
          } else {
            _tmpBuyerAgreedAt = _stmt.getLong(_columnIndexOfBuyerAgreedAt)
          }
          val _tmpSellerAgreedAt: Long?
          if (_stmt.isNull(_columnIndexOfSellerAgreedAt)) {
            _tmpSellerAgreedAt = null
          } else {
            _tmpSellerAgreedAt = _stmt.getLong(_columnIndexOfSellerAgreedAt)
          }
          val _tmpLockedAt: Long?
          if (_stmt.isNull(_columnIndexOfLockedAt)) {
            _tmpLockedAt = null
          } else {
            _tmpLockedAt = _stmt.getLong(_columnIndexOfLockedAt)
          }
          val _tmpExpiresAt: Long?
          if (_stmt.isNull(_columnIndexOfExpiresAt)) {
            _tmpExpiresAt = null
          } else {
            _tmpExpiresAt = _stmt.getLong(_columnIndexOfExpiresAt)
          }
          val _tmpVersion: Int
          _tmpVersion = _stmt.getLong(_columnIndexOfVersion).toInt()
          val _tmpPreviousQuoteId: String?
          if (_stmt.isNull(_columnIndexOfPreviousQuoteId)) {
            _tmpPreviousQuoteId = null
          } else {
            _tmpPreviousQuoteId = _stmt.getText(_columnIndexOfPreviousQuoteId)
          }
          val _tmpBuyerNotes: String?
          if (_stmt.isNull(_columnIndexOfBuyerNotes)) {
            _tmpBuyerNotes = null
          } else {
            _tmpBuyerNotes = _stmt.getText(_columnIndexOfBuyerNotes)
          }
          val _tmpSellerNotes: String?
          if (_stmt.isNull(_columnIndexOfSellerNotes)) {
            _tmpSellerNotes = null
          } else {
            _tmpSellerNotes = _stmt.getText(_columnIndexOfSellerNotes)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpDirty: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp != 0
          _item =
              OrderQuoteEntity(_tmpQuoteId,_tmpOrderId,_tmpBuyerId,_tmpSellerId,_tmpProductId,_tmpProductName,_tmpQuantity,_tmpUnit,_tmpBasePrice,_tmpTotalProductPrice,_tmpDeliveryCharge,_tmpPackingCharge,_tmpPlatformFee,_tmpDiscount,_tmpFinalTotal,_tmpDeliveryType,_tmpDeliveryDistance,_tmpDeliveryAddress,_tmpDeliveryLatitude,_tmpDeliveryLongitude,_tmpPickupAddress,_tmpPickupLatitude,_tmpPickupLongitude,_tmpPaymentType,_tmpAdvanceAmount,_tmpBalanceAmount,_tmpStatus,_tmpBuyerAgreedAt,_tmpSellerAgreedAt,_tmpLockedAt,_tmpExpiresAt,_tmpVersion,_tmpPreviousQuoteId,_tmpBuyerNotes,_tmpSellerNotes,_tmpCreatedAt,_tmpUpdatedAt,_tmpDirty)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getSellerActiveQuotes(sellerId: String): Flow<List<OrderQuoteEntity>> {
    val _sql: String =
        "SELECT * FROM order_quotes WHERE sellerId = ? AND status NOT IN ('EXPIRED', 'REJECTED') ORDER BY createdAt DESC"
    return createFlow(__db, false, arrayOf("order_quotes")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, sellerId)
        val _columnIndexOfQuoteId: Int = getColumnIndexOrThrow(_stmt, "quoteId")
        val _columnIndexOfOrderId: Int = getColumnIndexOrThrow(_stmt, "orderId")
        val _columnIndexOfBuyerId: Int = getColumnIndexOrThrow(_stmt, "buyerId")
        val _columnIndexOfSellerId: Int = getColumnIndexOrThrow(_stmt, "sellerId")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfProductName: Int = getColumnIndexOrThrow(_stmt, "productName")
        val _columnIndexOfQuantity: Int = getColumnIndexOrThrow(_stmt, "quantity")
        val _columnIndexOfUnit: Int = getColumnIndexOrThrow(_stmt, "unit")
        val _columnIndexOfBasePrice: Int = getColumnIndexOrThrow(_stmt, "basePrice")
        val _columnIndexOfTotalProductPrice: Int = getColumnIndexOrThrow(_stmt, "totalProductPrice")
        val _columnIndexOfDeliveryCharge: Int = getColumnIndexOrThrow(_stmt, "deliveryCharge")
        val _columnIndexOfPackingCharge: Int = getColumnIndexOrThrow(_stmt, "packingCharge")
        val _columnIndexOfPlatformFee: Int = getColumnIndexOrThrow(_stmt, "platformFee")
        val _columnIndexOfDiscount: Int = getColumnIndexOrThrow(_stmt, "discount")
        val _columnIndexOfFinalTotal: Int = getColumnIndexOrThrow(_stmt, "finalTotal")
        val _columnIndexOfDeliveryType: Int = getColumnIndexOrThrow(_stmt, "deliveryType")
        val _columnIndexOfDeliveryDistance: Int = getColumnIndexOrThrow(_stmt, "deliveryDistance")
        val _columnIndexOfDeliveryAddress: Int = getColumnIndexOrThrow(_stmt, "deliveryAddress")
        val _columnIndexOfDeliveryLatitude: Int = getColumnIndexOrThrow(_stmt, "deliveryLatitude")
        val _columnIndexOfDeliveryLongitude: Int = getColumnIndexOrThrow(_stmt, "deliveryLongitude")
        val _columnIndexOfPickupAddress: Int = getColumnIndexOrThrow(_stmt, "pickupAddress")
        val _columnIndexOfPickupLatitude: Int = getColumnIndexOrThrow(_stmt, "pickupLatitude")
        val _columnIndexOfPickupLongitude: Int = getColumnIndexOrThrow(_stmt, "pickupLongitude")
        val _columnIndexOfPaymentType: Int = getColumnIndexOrThrow(_stmt, "paymentType")
        val _columnIndexOfAdvanceAmount: Int = getColumnIndexOrThrow(_stmt, "advanceAmount")
        val _columnIndexOfBalanceAmount: Int = getColumnIndexOrThrow(_stmt, "balanceAmount")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfBuyerAgreedAt: Int = getColumnIndexOrThrow(_stmt, "buyerAgreedAt")
        val _columnIndexOfSellerAgreedAt: Int = getColumnIndexOrThrow(_stmt, "sellerAgreedAt")
        val _columnIndexOfLockedAt: Int = getColumnIndexOrThrow(_stmt, "lockedAt")
        val _columnIndexOfExpiresAt: Int = getColumnIndexOrThrow(_stmt, "expiresAt")
        val _columnIndexOfVersion: Int = getColumnIndexOrThrow(_stmt, "version")
        val _columnIndexOfPreviousQuoteId: Int = getColumnIndexOrThrow(_stmt, "previousQuoteId")
        val _columnIndexOfBuyerNotes: Int = getColumnIndexOrThrow(_stmt, "buyerNotes")
        val _columnIndexOfSellerNotes: Int = getColumnIndexOrThrow(_stmt, "sellerNotes")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _result: MutableList<OrderQuoteEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: OrderQuoteEntity
          val _tmpQuoteId: String
          _tmpQuoteId = _stmt.getText(_columnIndexOfQuoteId)
          val _tmpOrderId: String
          _tmpOrderId = _stmt.getText(_columnIndexOfOrderId)
          val _tmpBuyerId: String
          _tmpBuyerId = _stmt.getText(_columnIndexOfBuyerId)
          val _tmpSellerId: String
          _tmpSellerId = _stmt.getText(_columnIndexOfSellerId)
          val _tmpProductId: String
          _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          val _tmpProductName: String
          _tmpProductName = _stmt.getText(_columnIndexOfProductName)
          val _tmpQuantity: Double
          _tmpQuantity = _stmt.getDouble(_columnIndexOfQuantity)
          val _tmpUnit: String
          _tmpUnit = _stmt.getText(_columnIndexOfUnit)
          val _tmpBasePrice: Double
          _tmpBasePrice = _stmt.getDouble(_columnIndexOfBasePrice)
          val _tmpTotalProductPrice: Double
          _tmpTotalProductPrice = _stmt.getDouble(_columnIndexOfTotalProductPrice)
          val _tmpDeliveryCharge: Double
          _tmpDeliveryCharge = _stmt.getDouble(_columnIndexOfDeliveryCharge)
          val _tmpPackingCharge: Double
          _tmpPackingCharge = _stmt.getDouble(_columnIndexOfPackingCharge)
          val _tmpPlatformFee: Double
          _tmpPlatformFee = _stmt.getDouble(_columnIndexOfPlatformFee)
          val _tmpDiscount: Double
          _tmpDiscount = _stmt.getDouble(_columnIndexOfDiscount)
          val _tmpFinalTotal: Double
          _tmpFinalTotal = _stmt.getDouble(_columnIndexOfFinalTotal)
          val _tmpDeliveryType: String
          _tmpDeliveryType = _stmt.getText(_columnIndexOfDeliveryType)
          val _tmpDeliveryDistance: Double?
          if (_stmt.isNull(_columnIndexOfDeliveryDistance)) {
            _tmpDeliveryDistance = null
          } else {
            _tmpDeliveryDistance = _stmt.getDouble(_columnIndexOfDeliveryDistance)
          }
          val _tmpDeliveryAddress: String?
          if (_stmt.isNull(_columnIndexOfDeliveryAddress)) {
            _tmpDeliveryAddress = null
          } else {
            _tmpDeliveryAddress = _stmt.getText(_columnIndexOfDeliveryAddress)
          }
          val _tmpDeliveryLatitude: Double?
          if (_stmt.isNull(_columnIndexOfDeliveryLatitude)) {
            _tmpDeliveryLatitude = null
          } else {
            _tmpDeliveryLatitude = _stmt.getDouble(_columnIndexOfDeliveryLatitude)
          }
          val _tmpDeliveryLongitude: Double?
          if (_stmt.isNull(_columnIndexOfDeliveryLongitude)) {
            _tmpDeliveryLongitude = null
          } else {
            _tmpDeliveryLongitude = _stmt.getDouble(_columnIndexOfDeliveryLongitude)
          }
          val _tmpPickupAddress: String?
          if (_stmt.isNull(_columnIndexOfPickupAddress)) {
            _tmpPickupAddress = null
          } else {
            _tmpPickupAddress = _stmt.getText(_columnIndexOfPickupAddress)
          }
          val _tmpPickupLatitude: Double?
          if (_stmt.isNull(_columnIndexOfPickupLatitude)) {
            _tmpPickupLatitude = null
          } else {
            _tmpPickupLatitude = _stmt.getDouble(_columnIndexOfPickupLatitude)
          }
          val _tmpPickupLongitude: Double?
          if (_stmt.isNull(_columnIndexOfPickupLongitude)) {
            _tmpPickupLongitude = null
          } else {
            _tmpPickupLongitude = _stmt.getDouble(_columnIndexOfPickupLongitude)
          }
          val _tmpPaymentType: String
          _tmpPaymentType = _stmt.getText(_columnIndexOfPaymentType)
          val _tmpAdvanceAmount: Double?
          if (_stmt.isNull(_columnIndexOfAdvanceAmount)) {
            _tmpAdvanceAmount = null
          } else {
            _tmpAdvanceAmount = _stmt.getDouble(_columnIndexOfAdvanceAmount)
          }
          val _tmpBalanceAmount: Double?
          if (_stmt.isNull(_columnIndexOfBalanceAmount)) {
            _tmpBalanceAmount = null
          } else {
            _tmpBalanceAmount = _stmt.getDouble(_columnIndexOfBalanceAmount)
          }
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpBuyerAgreedAt: Long?
          if (_stmt.isNull(_columnIndexOfBuyerAgreedAt)) {
            _tmpBuyerAgreedAt = null
          } else {
            _tmpBuyerAgreedAt = _stmt.getLong(_columnIndexOfBuyerAgreedAt)
          }
          val _tmpSellerAgreedAt: Long?
          if (_stmt.isNull(_columnIndexOfSellerAgreedAt)) {
            _tmpSellerAgreedAt = null
          } else {
            _tmpSellerAgreedAt = _stmt.getLong(_columnIndexOfSellerAgreedAt)
          }
          val _tmpLockedAt: Long?
          if (_stmt.isNull(_columnIndexOfLockedAt)) {
            _tmpLockedAt = null
          } else {
            _tmpLockedAt = _stmt.getLong(_columnIndexOfLockedAt)
          }
          val _tmpExpiresAt: Long?
          if (_stmt.isNull(_columnIndexOfExpiresAt)) {
            _tmpExpiresAt = null
          } else {
            _tmpExpiresAt = _stmt.getLong(_columnIndexOfExpiresAt)
          }
          val _tmpVersion: Int
          _tmpVersion = _stmt.getLong(_columnIndexOfVersion).toInt()
          val _tmpPreviousQuoteId: String?
          if (_stmt.isNull(_columnIndexOfPreviousQuoteId)) {
            _tmpPreviousQuoteId = null
          } else {
            _tmpPreviousQuoteId = _stmt.getText(_columnIndexOfPreviousQuoteId)
          }
          val _tmpBuyerNotes: String?
          if (_stmt.isNull(_columnIndexOfBuyerNotes)) {
            _tmpBuyerNotes = null
          } else {
            _tmpBuyerNotes = _stmt.getText(_columnIndexOfBuyerNotes)
          }
          val _tmpSellerNotes: String?
          if (_stmt.isNull(_columnIndexOfSellerNotes)) {
            _tmpSellerNotes = null
          } else {
            _tmpSellerNotes = _stmt.getText(_columnIndexOfSellerNotes)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpDirty: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp != 0
          _item =
              OrderQuoteEntity(_tmpQuoteId,_tmpOrderId,_tmpBuyerId,_tmpSellerId,_tmpProductId,_tmpProductName,_tmpQuantity,_tmpUnit,_tmpBasePrice,_tmpTotalProductPrice,_tmpDeliveryCharge,_tmpPackingCharge,_tmpPlatformFee,_tmpDiscount,_tmpFinalTotal,_tmpDeliveryType,_tmpDeliveryDistance,_tmpDeliveryAddress,_tmpDeliveryLatitude,_tmpDeliveryLongitude,_tmpPickupAddress,_tmpPickupLatitude,_tmpPickupLongitude,_tmpPaymentType,_tmpAdvanceAmount,_tmpBalanceAmount,_tmpStatus,_tmpBuyerAgreedAt,_tmpSellerAgreedAt,_tmpLockedAt,_tmpExpiresAt,_tmpVersion,_tmpPreviousQuoteId,_tmpBuyerNotes,_tmpSellerNotes,_tmpCreatedAt,_tmpUpdatedAt,_tmpDirty)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getQuotesByStatus(status: String): Flow<List<OrderQuoteEntity>> {
    val _sql: String = "SELECT * FROM order_quotes WHERE status = ? ORDER BY createdAt DESC"
    return createFlow(__db, false, arrayOf("order_quotes")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, status)
        val _columnIndexOfQuoteId: Int = getColumnIndexOrThrow(_stmt, "quoteId")
        val _columnIndexOfOrderId: Int = getColumnIndexOrThrow(_stmt, "orderId")
        val _columnIndexOfBuyerId: Int = getColumnIndexOrThrow(_stmt, "buyerId")
        val _columnIndexOfSellerId: Int = getColumnIndexOrThrow(_stmt, "sellerId")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfProductName: Int = getColumnIndexOrThrow(_stmt, "productName")
        val _columnIndexOfQuantity: Int = getColumnIndexOrThrow(_stmt, "quantity")
        val _columnIndexOfUnit: Int = getColumnIndexOrThrow(_stmt, "unit")
        val _columnIndexOfBasePrice: Int = getColumnIndexOrThrow(_stmt, "basePrice")
        val _columnIndexOfTotalProductPrice: Int = getColumnIndexOrThrow(_stmt, "totalProductPrice")
        val _columnIndexOfDeliveryCharge: Int = getColumnIndexOrThrow(_stmt, "deliveryCharge")
        val _columnIndexOfPackingCharge: Int = getColumnIndexOrThrow(_stmt, "packingCharge")
        val _columnIndexOfPlatformFee: Int = getColumnIndexOrThrow(_stmt, "platformFee")
        val _columnIndexOfDiscount: Int = getColumnIndexOrThrow(_stmt, "discount")
        val _columnIndexOfFinalTotal: Int = getColumnIndexOrThrow(_stmt, "finalTotal")
        val _columnIndexOfDeliveryType: Int = getColumnIndexOrThrow(_stmt, "deliveryType")
        val _columnIndexOfDeliveryDistance: Int = getColumnIndexOrThrow(_stmt, "deliveryDistance")
        val _columnIndexOfDeliveryAddress: Int = getColumnIndexOrThrow(_stmt, "deliveryAddress")
        val _columnIndexOfDeliveryLatitude: Int = getColumnIndexOrThrow(_stmt, "deliveryLatitude")
        val _columnIndexOfDeliveryLongitude: Int = getColumnIndexOrThrow(_stmt, "deliveryLongitude")
        val _columnIndexOfPickupAddress: Int = getColumnIndexOrThrow(_stmt, "pickupAddress")
        val _columnIndexOfPickupLatitude: Int = getColumnIndexOrThrow(_stmt, "pickupLatitude")
        val _columnIndexOfPickupLongitude: Int = getColumnIndexOrThrow(_stmt, "pickupLongitude")
        val _columnIndexOfPaymentType: Int = getColumnIndexOrThrow(_stmt, "paymentType")
        val _columnIndexOfAdvanceAmount: Int = getColumnIndexOrThrow(_stmt, "advanceAmount")
        val _columnIndexOfBalanceAmount: Int = getColumnIndexOrThrow(_stmt, "balanceAmount")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfBuyerAgreedAt: Int = getColumnIndexOrThrow(_stmt, "buyerAgreedAt")
        val _columnIndexOfSellerAgreedAt: Int = getColumnIndexOrThrow(_stmt, "sellerAgreedAt")
        val _columnIndexOfLockedAt: Int = getColumnIndexOrThrow(_stmt, "lockedAt")
        val _columnIndexOfExpiresAt: Int = getColumnIndexOrThrow(_stmt, "expiresAt")
        val _columnIndexOfVersion: Int = getColumnIndexOrThrow(_stmt, "version")
        val _columnIndexOfPreviousQuoteId: Int = getColumnIndexOrThrow(_stmt, "previousQuoteId")
        val _columnIndexOfBuyerNotes: Int = getColumnIndexOrThrow(_stmt, "buyerNotes")
        val _columnIndexOfSellerNotes: Int = getColumnIndexOrThrow(_stmt, "sellerNotes")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _result: MutableList<OrderQuoteEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: OrderQuoteEntity
          val _tmpQuoteId: String
          _tmpQuoteId = _stmt.getText(_columnIndexOfQuoteId)
          val _tmpOrderId: String
          _tmpOrderId = _stmt.getText(_columnIndexOfOrderId)
          val _tmpBuyerId: String
          _tmpBuyerId = _stmt.getText(_columnIndexOfBuyerId)
          val _tmpSellerId: String
          _tmpSellerId = _stmt.getText(_columnIndexOfSellerId)
          val _tmpProductId: String
          _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          val _tmpProductName: String
          _tmpProductName = _stmt.getText(_columnIndexOfProductName)
          val _tmpQuantity: Double
          _tmpQuantity = _stmt.getDouble(_columnIndexOfQuantity)
          val _tmpUnit: String
          _tmpUnit = _stmt.getText(_columnIndexOfUnit)
          val _tmpBasePrice: Double
          _tmpBasePrice = _stmt.getDouble(_columnIndexOfBasePrice)
          val _tmpTotalProductPrice: Double
          _tmpTotalProductPrice = _stmt.getDouble(_columnIndexOfTotalProductPrice)
          val _tmpDeliveryCharge: Double
          _tmpDeliveryCharge = _stmt.getDouble(_columnIndexOfDeliveryCharge)
          val _tmpPackingCharge: Double
          _tmpPackingCharge = _stmt.getDouble(_columnIndexOfPackingCharge)
          val _tmpPlatformFee: Double
          _tmpPlatformFee = _stmt.getDouble(_columnIndexOfPlatformFee)
          val _tmpDiscount: Double
          _tmpDiscount = _stmt.getDouble(_columnIndexOfDiscount)
          val _tmpFinalTotal: Double
          _tmpFinalTotal = _stmt.getDouble(_columnIndexOfFinalTotal)
          val _tmpDeliveryType: String
          _tmpDeliveryType = _stmt.getText(_columnIndexOfDeliveryType)
          val _tmpDeliveryDistance: Double?
          if (_stmt.isNull(_columnIndexOfDeliveryDistance)) {
            _tmpDeliveryDistance = null
          } else {
            _tmpDeliveryDistance = _stmt.getDouble(_columnIndexOfDeliveryDistance)
          }
          val _tmpDeliveryAddress: String?
          if (_stmt.isNull(_columnIndexOfDeliveryAddress)) {
            _tmpDeliveryAddress = null
          } else {
            _tmpDeliveryAddress = _stmt.getText(_columnIndexOfDeliveryAddress)
          }
          val _tmpDeliveryLatitude: Double?
          if (_stmt.isNull(_columnIndexOfDeliveryLatitude)) {
            _tmpDeliveryLatitude = null
          } else {
            _tmpDeliveryLatitude = _stmt.getDouble(_columnIndexOfDeliveryLatitude)
          }
          val _tmpDeliveryLongitude: Double?
          if (_stmt.isNull(_columnIndexOfDeliveryLongitude)) {
            _tmpDeliveryLongitude = null
          } else {
            _tmpDeliveryLongitude = _stmt.getDouble(_columnIndexOfDeliveryLongitude)
          }
          val _tmpPickupAddress: String?
          if (_stmt.isNull(_columnIndexOfPickupAddress)) {
            _tmpPickupAddress = null
          } else {
            _tmpPickupAddress = _stmt.getText(_columnIndexOfPickupAddress)
          }
          val _tmpPickupLatitude: Double?
          if (_stmt.isNull(_columnIndexOfPickupLatitude)) {
            _tmpPickupLatitude = null
          } else {
            _tmpPickupLatitude = _stmt.getDouble(_columnIndexOfPickupLatitude)
          }
          val _tmpPickupLongitude: Double?
          if (_stmt.isNull(_columnIndexOfPickupLongitude)) {
            _tmpPickupLongitude = null
          } else {
            _tmpPickupLongitude = _stmt.getDouble(_columnIndexOfPickupLongitude)
          }
          val _tmpPaymentType: String
          _tmpPaymentType = _stmt.getText(_columnIndexOfPaymentType)
          val _tmpAdvanceAmount: Double?
          if (_stmt.isNull(_columnIndexOfAdvanceAmount)) {
            _tmpAdvanceAmount = null
          } else {
            _tmpAdvanceAmount = _stmt.getDouble(_columnIndexOfAdvanceAmount)
          }
          val _tmpBalanceAmount: Double?
          if (_stmt.isNull(_columnIndexOfBalanceAmount)) {
            _tmpBalanceAmount = null
          } else {
            _tmpBalanceAmount = _stmt.getDouble(_columnIndexOfBalanceAmount)
          }
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpBuyerAgreedAt: Long?
          if (_stmt.isNull(_columnIndexOfBuyerAgreedAt)) {
            _tmpBuyerAgreedAt = null
          } else {
            _tmpBuyerAgreedAt = _stmt.getLong(_columnIndexOfBuyerAgreedAt)
          }
          val _tmpSellerAgreedAt: Long?
          if (_stmt.isNull(_columnIndexOfSellerAgreedAt)) {
            _tmpSellerAgreedAt = null
          } else {
            _tmpSellerAgreedAt = _stmt.getLong(_columnIndexOfSellerAgreedAt)
          }
          val _tmpLockedAt: Long?
          if (_stmt.isNull(_columnIndexOfLockedAt)) {
            _tmpLockedAt = null
          } else {
            _tmpLockedAt = _stmt.getLong(_columnIndexOfLockedAt)
          }
          val _tmpExpiresAt: Long?
          if (_stmt.isNull(_columnIndexOfExpiresAt)) {
            _tmpExpiresAt = null
          } else {
            _tmpExpiresAt = _stmt.getLong(_columnIndexOfExpiresAt)
          }
          val _tmpVersion: Int
          _tmpVersion = _stmt.getLong(_columnIndexOfVersion).toInt()
          val _tmpPreviousQuoteId: String?
          if (_stmt.isNull(_columnIndexOfPreviousQuoteId)) {
            _tmpPreviousQuoteId = null
          } else {
            _tmpPreviousQuoteId = _stmt.getText(_columnIndexOfPreviousQuoteId)
          }
          val _tmpBuyerNotes: String?
          if (_stmt.isNull(_columnIndexOfBuyerNotes)) {
            _tmpBuyerNotes = null
          } else {
            _tmpBuyerNotes = _stmt.getText(_columnIndexOfBuyerNotes)
          }
          val _tmpSellerNotes: String?
          if (_stmt.isNull(_columnIndexOfSellerNotes)) {
            _tmpSellerNotes = null
          } else {
            _tmpSellerNotes = _stmt.getText(_columnIndexOfSellerNotes)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpDirty: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp != 0
          _item =
              OrderQuoteEntity(_tmpQuoteId,_tmpOrderId,_tmpBuyerId,_tmpSellerId,_tmpProductId,_tmpProductName,_tmpQuantity,_tmpUnit,_tmpBasePrice,_tmpTotalProductPrice,_tmpDeliveryCharge,_tmpPackingCharge,_tmpPlatformFee,_tmpDiscount,_tmpFinalTotal,_tmpDeliveryType,_tmpDeliveryDistance,_tmpDeliveryAddress,_tmpDeliveryLatitude,_tmpDeliveryLongitude,_tmpPickupAddress,_tmpPickupLatitude,_tmpPickupLongitude,_tmpPaymentType,_tmpAdvanceAmount,_tmpBalanceAmount,_tmpStatus,_tmpBuyerAgreedAt,_tmpSellerAgreedAt,_tmpLockedAt,_tmpExpiresAt,_tmpVersion,_tmpPreviousQuoteId,_tmpBuyerNotes,_tmpSellerNotes,_tmpCreatedAt,_tmpUpdatedAt,_tmpDirty)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getDirtyRecords(): List<OrderQuoteEntity> {
    val _sql: String = "SELECT * FROM order_quotes WHERE dirty = 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfQuoteId: Int = getColumnIndexOrThrow(_stmt, "quoteId")
        val _columnIndexOfOrderId: Int = getColumnIndexOrThrow(_stmt, "orderId")
        val _columnIndexOfBuyerId: Int = getColumnIndexOrThrow(_stmt, "buyerId")
        val _columnIndexOfSellerId: Int = getColumnIndexOrThrow(_stmt, "sellerId")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfProductName: Int = getColumnIndexOrThrow(_stmt, "productName")
        val _columnIndexOfQuantity: Int = getColumnIndexOrThrow(_stmt, "quantity")
        val _columnIndexOfUnit: Int = getColumnIndexOrThrow(_stmt, "unit")
        val _columnIndexOfBasePrice: Int = getColumnIndexOrThrow(_stmt, "basePrice")
        val _columnIndexOfTotalProductPrice: Int = getColumnIndexOrThrow(_stmt, "totalProductPrice")
        val _columnIndexOfDeliveryCharge: Int = getColumnIndexOrThrow(_stmt, "deliveryCharge")
        val _columnIndexOfPackingCharge: Int = getColumnIndexOrThrow(_stmt, "packingCharge")
        val _columnIndexOfPlatformFee: Int = getColumnIndexOrThrow(_stmt, "platformFee")
        val _columnIndexOfDiscount: Int = getColumnIndexOrThrow(_stmt, "discount")
        val _columnIndexOfFinalTotal: Int = getColumnIndexOrThrow(_stmt, "finalTotal")
        val _columnIndexOfDeliveryType: Int = getColumnIndexOrThrow(_stmt, "deliveryType")
        val _columnIndexOfDeliveryDistance: Int = getColumnIndexOrThrow(_stmt, "deliveryDistance")
        val _columnIndexOfDeliveryAddress: Int = getColumnIndexOrThrow(_stmt, "deliveryAddress")
        val _columnIndexOfDeliveryLatitude: Int = getColumnIndexOrThrow(_stmt, "deliveryLatitude")
        val _columnIndexOfDeliveryLongitude: Int = getColumnIndexOrThrow(_stmt, "deliveryLongitude")
        val _columnIndexOfPickupAddress: Int = getColumnIndexOrThrow(_stmt, "pickupAddress")
        val _columnIndexOfPickupLatitude: Int = getColumnIndexOrThrow(_stmt, "pickupLatitude")
        val _columnIndexOfPickupLongitude: Int = getColumnIndexOrThrow(_stmt, "pickupLongitude")
        val _columnIndexOfPaymentType: Int = getColumnIndexOrThrow(_stmt, "paymentType")
        val _columnIndexOfAdvanceAmount: Int = getColumnIndexOrThrow(_stmt, "advanceAmount")
        val _columnIndexOfBalanceAmount: Int = getColumnIndexOrThrow(_stmt, "balanceAmount")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfBuyerAgreedAt: Int = getColumnIndexOrThrow(_stmt, "buyerAgreedAt")
        val _columnIndexOfSellerAgreedAt: Int = getColumnIndexOrThrow(_stmt, "sellerAgreedAt")
        val _columnIndexOfLockedAt: Int = getColumnIndexOrThrow(_stmt, "lockedAt")
        val _columnIndexOfExpiresAt: Int = getColumnIndexOrThrow(_stmt, "expiresAt")
        val _columnIndexOfVersion: Int = getColumnIndexOrThrow(_stmt, "version")
        val _columnIndexOfPreviousQuoteId: Int = getColumnIndexOrThrow(_stmt, "previousQuoteId")
        val _columnIndexOfBuyerNotes: Int = getColumnIndexOrThrow(_stmt, "buyerNotes")
        val _columnIndexOfSellerNotes: Int = getColumnIndexOrThrow(_stmt, "sellerNotes")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _result: MutableList<OrderQuoteEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: OrderQuoteEntity
          val _tmpQuoteId: String
          _tmpQuoteId = _stmt.getText(_columnIndexOfQuoteId)
          val _tmpOrderId: String
          _tmpOrderId = _stmt.getText(_columnIndexOfOrderId)
          val _tmpBuyerId: String
          _tmpBuyerId = _stmt.getText(_columnIndexOfBuyerId)
          val _tmpSellerId: String
          _tmpSellerId = _stmt.getText(_columnIndexOfSellerId)
          val _tmpProductId: String
          _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          val _tmpProductName: String
          _tmpProductName = _stmt.getText(_columnIndexOfProductName)
          val _tmpQuantity: Double
          _tmpQuantity = _stmt.getDouble(_columnIndexOfQuantity)
          val _tmpUnit: String
          _tmpUnit = _stmt.getText(_columnIndexOfUnit)
          val _tmpBasePrice: Double
          _tmpBasePrice = _stmt.getDouble(_columnIndexOfBasePrice)
          val _tmpTotalProductPrice: Double
          _tmpTotalProductPrice = _stmt.getDouble(_columnIndexOfTotalProductPrice)
          val _tmpDeliveryCharge: Double
          _tmpDeliveryCharge = _stmt.getDouble(_columnIndexOfDeliveryCharge)
          val _tmpPackingCharge: Double
          _tmpPackingCharge = _stmt.getDouble(_columnIndexOfPackingCharge)
          val _tmpPlatformFee: Double
          _tmpPlatformFee = _stmt.getDouble(_columnIndexOfPlatformFee)
          val _tmpDiscount: Double
          _tmpDiscount = _stmt.getDouble(_columnIndexOfDiscount)
          val _tmpFinalTotal: Double
          _tmpFinalTotal = _stmt.getDouble(_columnIndexOfFinalTotal)
          val _tmpDeliveryType: String
          _tmpDeliveryType = _stmt.getText(_columnIndexOfDeliveryType)
          val _tmpDeliveryDistance: Double?
          if (_stmt.isNull(_columnIndexOfDeliveryDistance)) {
            _tmpDeliveryDistance = null
          } else {
            _tmpDeliveryDistance = _stmt.getDouble(_columnIndexOfDeliveryDistance)
          }
          val _tmpDeliveryAddress: String?
          if (_stmt.isNull(_columnIndexOfDeliveryAddress)) {
            _tmpDeliveryAddress = null
          } else {
            _tmpDeliveryAddress = _stmt.getText(_columnIndexOfDeliveryAddress)
          }
          val _tmpDeliveryLatitude: Double?
          if (_stmt.isNull(_columnIndexOfDeliveryLatitude)) {
            _tmpDeliveryLatitude = null
          } else {
            _tmpDeliveryLatitude = _stmt.getDouble(_columnIndexOfDeliveryLatitude)
          }
          val _tmpDeliveryLongitude: Double?
          if (_stmt.isNull(_columnIndexOfDeliveryLongitude)) {
            _tmpDeliveryLongitude = null
          } else {
            _tmpDeliveryLongitude = _stmt.getDouble(_columnIndexOfDeliveryLongitude)
          }
          val _tmpPickupAddress: String?
          if (_stmt.isNull(_columnIndexOfPickupAddress)) {
            _tmpPickupAddress = null
          } else {
            _tmpPickupAddress = _stmt.getText(_columnIndexOfPickupAddress)
          }
          val _tmpPickupLatitude: Double?
          if (_stmt.isNull(_columnIndexOfPickupLatitude)) {
            _tmpPickupLatitude = null
          } else {
            _tmpPickupLatitude = _stmt.getDouble(_columnIndexOfPickupLatitude)
          }
          val _tmpPickupLongitude: Double?
          if (_stmt.isNull(_columnIndexOfPickupLongitude)) {
            _tmpPickupLongitude = null
          } else {
            _tmpPickupLongitude = _stmt.getDouble(_columnIndexOfPickupLongitude)
          }
          val _tmpPaymentType: String
          _tmpPaymentType = _stmt.getText(_columnIndexOfPaymentType)
          val _tmpAdvanceAmount: Double?
          if (_stmt.isNull(_columnIndexOfAdvanceAmount)) {
            _tmpAdvanceAmount = null
          } else {
            _tmpAdvanceAmount = _stmt.getDouble(_columnIndexOfAdvanceAmount)
          }
          val _tmpBalanceAmount: Double?
          if (_stmt.isNull(_columnIndexOfBalanceAmount)) {
            _tmpBalanceAmount = null
          } else {
            _tmpBalanceAmount = _stmt.getDouble(_columnIndexOfBalanceAmount)
          }
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpBuyerAgreedAt: Long?
          if (_stmt.isNull(_columnIndexOfBuyerAgreedAt)) {
            _tmpBuyerAgreedAt = null
          } else {
            _tmpBuyerAgreedAt = _stmt.getLong(_columnIndexOfBuyerAgreedAt)
          }
          val _tmpSellerAgreedAt: Long?
          if (_stmt.isNull(_columnIndexOfSellerAgreedAt)) {
            _tmpSellerAgreedAt = null
          } else {
            _tmpSellerAgreedAt = _stmt.getLong(_columnIndexOfSellerAgreedAt)
          }
          val _tmpLockedAt: Long?
          if (_stmt.isNull(_columnIndexOfLockedAt)) {
            _tmpLockedAt = null
          } else {
            _tmpLockedAt = _stmt.getLong(_columnIndexOfLockedAt)
          }
          val _tmpExpiresAt: Long?
          if (_stmt.isNull(_columnIndexOfExpiresAt)) {
            _tmpExpiresAt = null
          } else {
            _tmpExpiresAt = _stmt.getLong(_columnIndexOfExpiresAt)
          }
          val _tmpVersion: Int
          _tmpVersion = _stmt.getLong(_columnIndexOfVersion).toInt()
          val _tmpPreviousQuoteId: String?
          if (_stmt.isNull(_columnIndexOfPreviousQuoteId)) {
            _tmpPreviousQuoteId = null
          } else {
            _tmpPreviousQuoteId = _stmt.getText(_columnIndexOfPreviousQuoteId)
          }
          val _tmpBuyerNotes: String?
          if (_stmt.isNull(_columnIndexOfBuyerNotes)) {
            _tmpBuyerNotes = null
          } else {
            _tmpBuyerNotes = _stmt.getText(_columnIndexOfBuyerNotes)
          }
          val _tmpSellerNotes: String?
          if (_stmt.isNull(_columnIndexOfSellerNotes)) {
            _tmpSellerNotes = null
          } else {
            _tmpSellerNotes = _stmt.getText(_columnIndexOfSellerNotes)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpDirty: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp != 0
          _item =
              OrderQuoteEntity(_tmpQuoteId,_tmpOrderId,_tmpBuyerId,_tmpSellerId,_tmpProductId,_tmpProductName,_tmpQuantity,_tmpUnit,_tmpBasePrice,_tmpTotalProductPrice,_tmpDeliveryCharge,_tmpPackingCharge,_tmpPlatformFee,_tmpDiscount,_tmpFinalTotal,_tmpDeliveryType,_tmpDeliveryDistance,_tmpDeliveryAddress,_tmpDeliveryLatitude,_tmpDeliveryLongitude,_tmpPickupAddress,_tmpPickupLatitude,_tmpPickupLongitude,_tmpPaymentType,_tmpAdvanceAmount,_tmpBalanceAmount,_tmpStatus,_tmpBuyerAgreedAt,_tmpSellerAgreedAt,_tmpLockedAt,_tmpExpiresAt,_tmpVersion,_tmpPreviousQuoteId,_tmpBuyerNotes,_tmpSellerNotes,_tmpCreatedAt,_tmpUpdatedAt,_tmpDirty)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun updateStatus(
    quoteId: String,
    status: String,
    updatedAt: Long,
  ) {
    val _sql: String =
        "UPDATE order_quotes SET status = ?, updatedAt = ?, dirty = 1 WHERE quoteId = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, status)
        _argIndex = 2
        _stmt.bindLong(_argIndex, updatedAt)
        _argIndex = 3
        _stmt.bindText(_argIndex, quoteId)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun buyerAgree(quoteId: String, agreedAt: Long) {
    val _sql: String =
        "UPDATE order_quotes SET buyerAgreedAt = ?, status = CASE WHEN sellerAgreedAt IS NOT NULL THEN 'LOCKED' ELSE 'BUYER_AGREED' END, lockedAt = CASE WHEN sellerAgreedAt IS NOT NULL THEN ? ELSE NULL END, updatedAt = ?, dirty = 1 WHERE quoteId = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, agreedAt)
        _argIndex = 2
        _stmt.bindLong(_argIndex, agreedAt)
        _argIndex = 3
        _stmt.bindLong(_argIndex, agreedAt)
        _argIndex = 4
        _stmt.bindText(_argIndex, quoteId)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun sellerAgree(quoteId: String, agreedAt: Long) {
    val _sql: String =
        "UPDATE order_quotes SET sellerAgreedAt = ?, status = CASE WHEN buyerAgreedAt IS NOT NULL THEN 'LOCKED' ELSE 'SELLER_AGREED' END, lockedAt = CASE WHEN buyerAgreedAt IS NOT NULL THEN ? ELSE NULL END, updatedAt = ?, dirty = 1 WHERE quoteId = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, agreedAt)
        _argIndex = 2
        _stmt.bindLong(_argIndex, agreedAt)
        _argIndex = 3
        _stmt.bindLong(_argIndex, agreedAt)
        _argIndex = 4
        _stmt.bindText(_argIndex, quoteId)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun expireOldQuotes(expiredAt: Long): Int {
    val _sql: String =
        "UPDATE order_quotes SET status = 'EXPIRED', updatedAt = ?, dirty = 1 WHERE expiresAt < ? AND status NOT IN ('LOCKED', 'EXPIRED', 'REJECTED')"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, expiredAt)
        _argIndex = 2
        _stmt.bindLong(_argIndex, expiredAt)
        _stmt.step()
        getTotalChangedRows(_connection)
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun markClean(quoteId: String) {
    val _sql: String = "UPDATE order_quotes SET dirty = 0 WHERE quoteId = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, quoteId)
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
