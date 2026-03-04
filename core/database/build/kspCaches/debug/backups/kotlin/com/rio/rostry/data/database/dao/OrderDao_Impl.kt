package com.rio.rostry.`data`.database.dao

import androidx.room.EntityDeleteOrUpdateAdapter
import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.appendPlaceholders
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performInTransactionSuspending
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.rio.rostry.`data`.database.entity.OrderEntity
import com.rio.rostry.`data`.database.entity.OrderItemEntity
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
public class OrderDao_Impl(
  __db: RoomDatabase,
) : OrderDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfOrderEntity: EntityInsertAdapter<OrderEntity>

  private val __insertAdapterOfOrderItemEntity: EntityInsertAdapter<OrderItemEntity>

  private val __updateAdapterOfOrderEntity: EntityDeleteOrUpdateAdapter<OrderEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfOrderEntity = object : EntityInsertAdapter<OrderEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `orders` (`orderId`,`buyerId`,`sellerId`,`productId`,`quantity`,`unit`,`totalAmount`,`status`,`shippingAddress`,`paymentMethod`,`paymentStatus`,`orderDate`,`expectedDeliveryDate`,`actualDeliveryDate`,`notes`,`createdAt`,`updatedAt`,`lastModifiedAt`,`isDeleted`,`deletedAt`,`dirty`,`deliveryMethod`,`deliveryType`,`deliveryAddressJson`,`negotiationStatus`,`negotiatedPrice`,`originalPrice`,`cancellationReason`,`cancellationTime`,`billImageUri`,`paymentSlipUri`,`otp`,`otpEntered`,`isVerified`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: OrderEntity) {
        statement.bindText(1, entity.orderId)
        val _tmpBuyerId: String? = entity.buyerId
        if (_tmpBuyerId == null) {
          statement.bindNull(2)
        } else {
          statement.bindText(2, _tmpBuyerId)
        }
        statement.bindText(3, entity.sellerId)
        statement.bindText(4, entity.productId)
        statement.bindDouble(5, entity.quantity)
        val _tmpUnit: String? = entity.unit
        if (_tmpUnit == null) {
          statement.bindNull(6)
        } else {
          statement.bindText(6, _tmpUnit)
        }
        statement.bindDouble(7, entity.totalAmount)
        statement.bindText(8, entity.status)
        statement.bindText(9, entity.shippingAddress)
        val _tmpPaymentMethod: String? = entity.paymentMethod
        if (_tmpPaymentMethod == null) {
          statement.bindNull(10)
        } else {
          statement.bindText(10, _tmpPaymentMethod)
        }
        statement.bindText(11, entity.paymentStatus)
        statement.bindLong(12, entity.orderDate)
        val _tmpExpectedDeliveryDate: Long? = entity.expectedDeliveryDate
        if (_tmpExpectedDeliveryDate == null) {
          statement.bindNull(13)
        } else {
          statement.bindLong(13, _tmpExpectedDeliveryDate)
        }
        val _tmpActualDeliveryDate: Long? = entity.actualDeliveryDate
        if (_tmpActualDeliveryDate == null) {
          statement.bindNull(14)
        } else {
          statement.bindLong(14, _tmpActualDeliveryDate)
        }
        val _tmpNotes: String? = entity.notes
        if (_tmpNotes == null) {
          statement.bindNull(15)
        } else {
          statement.bindText(15, _tmpNotes)
        }
        statement.bindLong(16, entity.createdAt)
        statement.bindLong(17, entity.updatedAt)
        statement.bindLong(18, entity.lastModifiedAt)
        val _tmp: Int = if (entity.isDeleted) 1 else 0
        statement.bindLong(19, _tmp.toLong())
        val _tmpDeletedAt: Long? = entity.deletedAt
        if (_tmpDeletedAt == null) {
          statement.bindNull(20)
        } else {
          statement.bindLong(20, _tmpDeletedAt)
        }
        val _tmp_1: Int = if (entity.dirty) 1 else 0
        statement.bindLong(21, _tmp_1.toLong())
        val _tmpDeliveryMethod: String? = entity.deliveryMethod
        if (_tmpDeliveryMethod == null) {
          statement.bindNull(22)
        } else {
          statement.bindText(22, _tmpDeliveryMethod)
        }
        val _tmpDeliveryType: String? = entity.deliveryType
        if (_tmpDeliveryType == null) {
          statement.bindNull(23)
        } else {
          statement.bindText(23, _tmpDeliveryType)
        }
        val _tmpDeliveryAddressJson: String? = entity.deliveryAddressJson
        if (_tmpDeliveryAddressJson == null) {
          statement.bindNull(24)
        } else {
          statement.bindText(24, _tmpDeliveryAddressJson)
        }
        val _tmpNegotiationStatus: String? = entity.negotiationStatus
        if (_tmpNegotiationStatus == null) {
          statement.bindNull(25)
        } else {
          statement.bindText(25, _tmpNegotiationStatus)
        }
        val _tmpNegotiatedPrice: Double? = entity.negotiatedPrice
        if (_tmpNegotiatedPrice == null) {
          statement.bindNull(26)
        } else {
          statement.bindDouble(26, _tmpNegotiatedPrice)
        }
        val _tmpOriginalPrice: Double? = entity.originalPrice
        if (_tmpOriginalPrice == null) {
          statement.bindNull(27)
        } else {
          statement.bindDouble(27, _tmpOriginalPrice)
        }
        val _tmpCancellationReason: String? = entity.cancellationReason
        if (_tmpCancellationReason == null) {
          statement.bindNull(28)
        } else {
          statement.bindText(28, _tmpCancellationReason)
        }
        val _tmpCancellationTime: Long? = entity.cancellationTime
        if (_tmpCancellationTime == null) {
          statement.bindNull(29)
        } else {
          statement.bindLong(29, _tmpCancellationTime)
        }
        val _tmpBillImageUri: String? = entity.billImageUri
        if (_tmpBillImageUri == null) {
          statement.bindNull(30)
        } else {
          statement.bindText(30, _tmpBillImageUri)
        }
        val _tmpPaymentSlipUri: String? = entity.paymentSlipUri
        if (_tmpPaymentSlipUri == null) {
          statement.bindNull(31)
        } else {
          statement.bindText(31, _tmpPaymentSlipUri)
        }
        val _tmpOtp: String? = entity.otp
        if (_tmpOtp == null) {
          statement.bindNull(32)
        } else {
          statement.bindText(32, _tmpOtp)
        }
        val _tmpOtpEntered: String? = entity.otpEntered
        if (_tmpOtpEntered == null) {
          statement.bindNull(33)
        } else {
          statement.bindText(33, _tmpOtpEntered)
        }
        val _tmp_2: Int = if (entity.isVerified) 1 else 0
        statement.bindLong(34, _tmp_2.toLong())
      }
    }
    this.__insertAdapterOfOrderItemEntity = object : EntityInsertAdapter<OrderItemEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `order_items` (`orderId`,`productId`,`quantity`,`priceAtPurchase`,`unitAtPurchase`) VALUES (?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: OrderItemEntity) {
        statement.bindText(1, entity.orderId)
        statement.bindText(2, entity.productId)
        statement.bindDouble(3, entity.quantity)
        statement.bindDouble(4, entity.priceAtPurchase)
        statement.bindText(5, entity.unitAtPurchase)
      }
    }
    this.__updateAdapterOfOrderEntity = object : EntityDeleteOrUpdateAdapter<OrderEntity>() {
      protected override fun createQuery(): String =
          "UPDATE OR ABORT `orders` SET `orderId` = ?,`buyerId` = ?,`sellerId` = ?,`productId` = ?,`quantity` = ?,`unit` = ?,`totalAmount` = ?,`status` = ?,`shippingAddress` = ?,`paymentMethod` = ?,`paymentStatus` = ?,`orderDate` = ?,`expectedDeliveryDate` = ?,`actualDeliveryDate` = ?,`notes` = ?,`createdAt` = ?,`updatedAt` = ?,`lastModifiedAt` = ?,`isDeleted` = ?,`deletedAt` = ?,`dirty` = ?,`deliveryMethod` = ?,`deliveryType` = ?,`deliveryAddressJson` = ?,`negotiationStatus` = ?,`negotiatedPrice` = ?,`originalPrice` = ?,`cancellationReason` = ?,`cancellationTime` = ?,`billImageUri` = ?,`paymentSlipUri` = ?,`otp` = ?,`otpEntered` = ?,`isVerified` = ? WHERE `orderId` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: OrderEntity) {
        statement.bindText(1, entity.orderId)
        val _tmpBuyerId: String? = entity.buyerId
        if (_tmpBuyerId == null) {
          statement.bindNull(2)
        } else {
          statement.bindText(2, _tmpBuyerId)
        }
        statement.bindText(3, entity.sellerId)
        statement.bindText(4, entity.productId)
        statement.bindDouble(5, entity.quantity)
        val _tmpUnit: String? = entity.unit
        if (_tmpUnit == null) {
          statement.bindNull(6)
        } else {
          statement.bindText(6, _tmpUnit)
        }
        statement.bindDouble(7, entity.totalAmount)
        statement.bindText(8, entity.status)
        statement.bindText(9, entity.shippingAddress)
        val _tmpPaymentMethod: String? = entity.paymentMethod
        if (_tmpPaymentMethod == null) {
          statement.bindNull(10)
        } else {
          statement.bindText(10, _tmpPaymentMethod)
        }
        statement.bindText(11, entity.paymentStatus)
        statement.bindLong(12, entity.orderDate)
        val _tmpExpectedDeliveryDate: Long? = entity.expectedDeliveryDate
        if (_tmpExpectedDeliveryDate == null) {
          statement.bindNull(13)
        } else {
          statement.bindLong(13, _tmpExpectedDeliveryDate)
        }
        val _tmpActualDeliveryDate: Long? = entity.actualDeliveryDate
        if (_tmpActualDeliveryDate == null) {
          statement.bindNull(14)
        } else {
          statement.bindLong(14, _tmpActualDeliveryDate)
        }
        val _tmpNotes: String? = entity.notes
        if (_tmpNotes == null) {
          statement.bindNull(15)
        } else {
          statement.bindText(15, _tmpNotes)
        }
        statement.bindLong(16, entity.createdAt)
        statement.bindLong(17, entity.updatedAt)
        statement.bindLong(18, entity.lastModifiedAt)
        val _tmp: Int = if (entity.isDeleted) 1 else 0
        statement.bindLong(19, _tmp.toLong())
        val _tmpDeletedAt: Long? = entity.deletedAt
        if (_tmpDeletedAt == null) {
          statement.bindNull(20)
        } else {
          statement.bindLong(20, _tmpDeletedAt)
        }
        val _tmp_1: Int = if (entity.dirty) 1 else 0
        statement.bindLong(21, _tmp_1.toLong())
        val _tmpDeliveryMethod: String? = entity.deliveryMethod
        if (_tmpDeliveryMethod == null) {
          statement.bindNull(22)
        } else {
          statement.bindText(22, _tmpDeliveryMethod)
        }
        val _tmpDeliveryType: String? = entity.deliveryType
        if (_tmpDeliveryType == null) {
          statement.bindNull(23)
        } else {
          statement.bindText(23, _tmpDeliveryType)
        }
        val _tmpDeliveryAddressJson: String? = entity.deliveryAddressJson
        if (_tmpDeliveryAddressJson == null) {
          statement.bindNull(24)
        } else {
          statement.bindText(24, _tmpDeliveryAddressJson)
        }
        val _tmpNegotiationStatus: String? = entity.negotiationStatus
        if (_tmpNegotiationStatus == null) {
          statement.bindNull(25)
        } else {
          statement.bindText(25, _tmpNegotiationStatus)
        }
        val _tmpNegotiatedPrice: Double? = entity.negotiatedPrice
        if (_tmpNegotiatedPrice == null) {
          statement.bindNull(26)
        } else {
          statement.bindDouble(26, _tmpNegotiatedPrice)
        }
        val _tmpOriginalPrice: Double? = entity.originalPrice
        if (_tmpOriginalPrice == null) {
          statement.bindNull(27)
        } else {
          statement.bindDouble(27, _tmpOriginalPrice)
        }
        val _tmpCancellationReason: String? = entity.cancellationReason
        if (_tmpCancellationReason == null) {
          statement.bindNull(28)
        } else {
          statement.bindText(28, _tmpCancellationReason)
        }
        val _tmpCancellationTime: Long? = entity.cancellationTime
        if (_tmpCancellationTime == null) {
          statement.bindNull(29)
        } else {
          statement.bindLong(29, _tmpCancellationTime)
        }
        val _tmpBillImageUri: String? = entity.billImageUri
        if (_tmpBillImageUri == null) {
          statement.bindNull(30)
        } else {
          statement.bindText(30, _tmpBillImageUri)
        }
        val _tmpPaymentSlipUri: String? = entity.paymentSlipUri
        if (_tmpPaymentSlipUri == null) {
          statement.bindNull(31)
        } else {
          statement.bindText(31, _tmpPaymentSlipUri)
        }
        val _tmpOtp: String? = entity.otp
        if (_tmpOtp == null) {
          statement.bindNull(32)
        } else {
          statement.bindText(32, _tmpOtp)
        }
        val _tmpOtpEntered: String? = entity.otpEntered
        if (_tmpOtpEntered == null) {
          statement.bindNull(33)
        } else {
          statement.bindText(33, _tmpOtpEntered)
        }
        val _tmp_2: Int = if (entity.isVerified) 1 else 0
        statement.bindLong(34, _tmp_2.toLong())
        statement.bindText(35, entity.orderId)
      }
    }
  }

  public override suspend fun insertOrder(order: OrderEntity): Long = performSuspending(__db, false,
      true) { _connection ->
    val _result: Long = __insertAdapterOfOrderEntity.insertAndReturnId(_connection, order)
    _result
  }

  public override suspend fun insertOrUpdate(order: OrderEntity): Unit = performSuspending(__db,
      false, true) { _connection ->
    __insertAdapterOfOrderEntity.insert(_connection, order)
  }

  public override suspend fun insertOrUpdate(orders: List<OrderEntity>): Unit =
      performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfOrderEntity.insert(_connection, orders)
  }

  public override suspend fun insertOrderItems(items: List<OrderItemEntity>): Unit =
      performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfOrderItemEntity.insert(_connection, items)
  }

  public override suspend fun updateOrder(order: OrderEntity): Unit = performSuspending(__db, false,
      true) { _connection ->
    __updateAdapterOfOrderEntity.handle(_connection, order)
  }

  public override suspend fun update(order: OrderEntity): Unit = performSuspending(__db, false,
      true) { _connection ->
    __updateAdapterOfOrderEntity.handle(_connection, order)
  }

  public override suspend fun insertOrderWithItems(order: OrderEntity,
      items: List<OrderItemEntity>): Unit = performInTransactionSuspending(__db) {
    super@OrderDao_Impl.insertOrderWithItems(order, items)
  }

  public override fun getOrderById(orderId: String): Flow<OrderEntity?> {
    val _sql: String = "SELECT * FROM orders WHERE orderId = ?"
    return createFlow(__db, false, arrayOf("orders")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, orderId)
        val _columnIndexOfOrderId: Int = getColumnIndexOrThrow(_stmt, "orderId")
        val _columnIndexOfBuyerId: Int = getColumnIndexOrThrow(_stmt, "buyerId")
        val _columnIndexOfSellerId: Int = getColumnIndexOrThrow(_stmt, "sellerId")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfQuantity: Int = getColumnIndexOrThrow(_stmt, "quantity")
        val _columnIndexOfUnit: Int = getColumnIndexOrThrow(_stmt, "unit")
        val _columnIndexOfTotalAmount: Int = getColumnIndexOrThrow(_stmt, "totalAmount")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfShippingAddress: Int = getColumnIndexOrThrow(_stmt, "shippingAddress")
        val _columnIndexOfPaymentMethod: Int = getColumnIndexOrThrow(_stmt, "paymentMethod")
        val _columnIndexOfPaymentStatus: Int = getColumnIndexOrThrow(_stmt, "paymentStatus")
        val _columnIndexOfOrderDate: Int = getColumnIndexOrThrow(_stmt, "orderDate")
        val _columnIndexOfExpectedDeliveryDate: Int = getColumnIndexOrThrow(_stmt,
            "expectedDeliveryDate")
        val _columnIndexOfActualDeliveryDate: Int = getColumnIndexOrThrow(_stmt,
            "actualDeliveryDate")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfLastModifiedAt: Int = getColumnIndexOrThrow(_stmt, "lastModifiedAt")
        val _columnIndexOfIsDeleted: Int = getColumnIndexOrThrow(_stmt, "isDeleted")
        val _columnIndexOfDeletedAt: Int = getColumnIndexOrThrow(_stmt, "deletedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfDeliveryMethod: Int = getColumnIndexOrThrow(_stmt, "deliveryMethod")
        val _columnIndexOfDeliveryType: Int = getColumnIndexOrThrow(_stmt, "deliveryType")
        val _columnIndexOfDeliveryAddressJson: Int = getColumnIndexOrThrow(_stmt,
            "deliveryAddressJson")
        val _columnIndexOfNegotiationStatus: Int = getColumnIndexOrThrow(_stmt, "negotiationStatus")
        val _columnIndexOfNegotiatedPrice: Int = getColumnIndexOrThrow(_stmt, "negotiatedPrice")
        val _columnIndexOfOriginalPrice: Int = getColumnIndexOrThrow(_stmt, "originalPrice")
        val _columnIndexOfCancellationReason: Int = getColumnIndexOrThrow(_stmt,
            "cancellationReason")
        val _columnIndexOfCancellationTime: Int = getColumnIndexOrThrow(_stmt, "cancellationTime")
        val _columnIndexOfBillImageUri: Int = getColumnIndexOrThrow(_stmt, "billImageUri")
        val _columnIndexOfPaymentSlipUri: Int = getColumnIndexOrThrow(_stmt, "paymentSlipUri")
        val _columnIndexOfOtp: Int = getColumnIndexOrThrow(_stmt, "otp")
        val _columnIndexOfOtpEntered: Int = getColumnIndexOrThrow(_stmt, "otpEntered")
        val _columnIndexOfIsVerified: Int = getColumnIndexOrThrow(_stmt, "isVerified")
        val _result: OrderEntity?
        if (_stmt.step()) {
          val _tmpOrderId: String
          _tmpOrderId = _stmt.getText(_columnIndexOfOrderId)
          val _tmpBuyerId: String?
          if (_stmt.isNull(_columnIndexOfBuyerId)) {
            _tmpBuyerId = null
          } else {
            _tmpBuyerId = _stmt.getText(_columnIndexOfBuyerId)
          }
          val _tmpSellerId: String
          _tmpSellerId = _stmt.getText(_columnIndexOfSellerId)
          val _tmpProductId: String
          _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          val _tmpQuantity: Double
          _tmpQuantity = _stmt.getDouble(_columnIndexOfQuantity)
          val _tmpUnit: String?
          if (_stmt.isNull(_columnIndexOfUnit)) {
            _tmpUnit = null
          } else {
            _tmpUnit = _stmt.getText(_columnIndexOfUnit)
          }
          val _tmpTotalAmount: Double
          _tmpTotalAmount = _stmt.getDouble(_columnIndexOfTotalAmount)
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpShippingAddress: String
          _tmpShippingAddress = _stmt.getText(_columnIndexOfShippingAddress)
          val _tmpPaymentMethod: String?
          if (_stmt.isNull(_columnIndexOfPaymentMethod)) {
            _tmpPaymentMethod = null
          } else {
            _tmpPaymentMethod = _stmt.getText(_columnIndexOfPaymentMethod)
          }
          val _tmpPaymentStatus: String
          _tmpPaymentStatus = _stmt.getText(_columnIndexOfPaymentStatus)
          val _tmpOrderDate: Long
          _tmpOrderDate = _stmt.getLong(_columnIndexOfOrderDate)
          val _tmpExpectedDeliveryDate: Long?
          if (_stmt.isNull(_columnIndexOfExpectedDeliveryDate)) {
            _tmpExpectedDeliveryDate = null
          } else {
            _tmpExpectedDeliveryDate = _stmt.getLong(_columnIndexOfExpectedDeliveryDate)
          }
          val _tmpActualDeliveryDate: Long?
          if (_stmt.isNull(_columnIndexOfActualDeliveryDate)) {
            _tmpActualDeliveryDate = null
          } else {
            _tmpActualDeliveryDate = _stmt.getLong(_columnIndexOfActualDeliveryDate)
          }
          val _tmpNotes: String?
          if (_stmt.isNull(_columnIndexOfNotes)) {
            _tmpNotes = null
          } else {
            _tmpNotes = _stmt.getText(_columnIndexOfNotes)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
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
          val _tmpDeliveryMethod: String?
          if (_stmt.isNull(_columnIndexOfDeliveryMethod)) {
            _tmpDeliveryMethod = null
          } else {
            _tmpDeliveryMethod = _stmt.getText(_columnIndexOfDeliveryMethod)
          }
          val _tmpDeliveryType: String?
          if (_stmt.isNull(_columnIndexOfDeliveryType)) {
            _tmpDeliveryType = null
          } else {
            _tmpDeliveryType = _stmt.getText(_columnIndexOfDeliveryType)
          }
          val _tmpDeliveryAddressJson: String?
          if (_stmt.isNull(_columnIndexOfDeliveryAddressJson)) {
            _tmpDeliveryAddressJson = null
          } else {
            _tmpDeliveryAddressJson = _stmt.getText(_columnIndexOfDeliveryAddressJson)
          }
          val _tmpNegotiationStatus: String?
          if (_stmt.isNull(_columnIndexOfNegotiationStatus)) {
            _tmpNegotiationStatus = null
          } else {
            _tmpNegotiationStatus = _stmt.getText(_columnIndexOfNegotiationStatus)
          }
          val _tmpNegotiatedPrice: Double?
          if (_stmt.isNull(_columnIndexOfNegotiatedPrice)) {
            _tmpNegotiatedPrice = null
          } else {
            _tmpNegotiatedPrice = _stmt.getDouble(_columnIndexOfNegotiatedPrice)
          }
          val _tmpOriginalPrice: Double?
          if (_stmt.isNull(_columnIndexOfOriginalPrice)) {
            _tmpOriginalPrice = null
          } else {
            _tmpOriginalPrice = _stmt.getDouble(_columnIndexOfOriginalPrice)
          }
          val _tmpCancellationReason: String?
          if (_stmt.isNull(_columnIndexOfCancellationReason)) {
            _tmpCancellationReason = null
          } else {
            _tmpCancellationReason = _stmt.getText(_columnIndexOfCancellationReason)
          }
          val _tmpCancellationTime: Long?
          if (_stmt.isNull(_columnIndexOfCancellationTime)) {
            _tmpCancellationTime = null
          } else {
            _tmpCancellationTime = _stmt.getLong(_columnIndexOfCancellationTime)
          }
          val _tmpBillImageUri: String?
          if (_stmt.isNull(_columnIndexOfBillImageUri)) {
            _tmpBillImageUri = null
          } else {
            _tmpBillImageUri = _stmt.getText(_columnIndexOfBillImageUri)
          }
          val _tmpPaymentSlipUri: String?
          if (_stmt.isNull(_columnIndexOfPaymentSlipUri)) {
            _tmpPaymentSlipUri = null
          } else {
            _tmpPaymentSlipUri = _stmt.getText(_columnIndexOfPaymentSlipUri)
          }
          val _tmpOtp: String?
          if (_stmt.isNull(_columnIndexOfOtp)) {
            _tmpOtp = null
          } else {
            _tmpOtp = _stmt.getText(_columnIndexOfOtp)
          }
          val _tmpOtpEntered: String?
          if (_stmt.isNull(_columnIndexOfOtpEntered)) {
            _tmpOtpEntered = null
          } else {
            _tmpOtpEntered = _stmt.getText(_columnIndexOfOtpEntered)
          }
          val _tmpIsVerified: Boolean
          val _tmp_2: Int
          _tmp_2 = _stmt.getLong(_columnIndexOfIsVerified).toInt()
          _tmpIsVerified = _tmp_2 != 0
          _result =
              OrderEntity(_tmpOrderId,_tmpBuyerId,_tmpSellerId,_tmpProductId,_tmpQuantity,_tmpUnit,_tmpTotalAmount,_tmpStatus,_tmpShippingAddress,_tmpPaymentMethod,_tmpPaymentStatus,_tmpOrderDate,_tmpExpectedDeliveryDate,_tmpActualDeliveryDate,_tmpNotes,_tmpCreatedAt,_tmpUpdatedAt,_tmpLastModifiedAt,_tmpIsDeleted,_tmpDeletedAt,_tmpDirty,_tmpDeliveryMethod,_tmpDeliveryType,_tmpDeliveryAddressJson,_tmpNegotiationStatus,_tmpNegotiatedPrice,_tmpOriginalPrice,_tmpCancellationReason,_tmpCancellationTime,_tmpBillImageUri,_tmpPaymentSlipUri,_tmpOtp,_tmpOtpEntered,_tmpIsVerified)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun findById(orderId: String): OrderEntity? {
    val _sql: String = "SELECT * FROM orders WHERE orderId = ? LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, orderId)
        val _columnIndexOfOrderId: Int = getColumnIndexOrThrow(_stmt, "orderId")
        val _columnIndexOfBuyerId: Int = getColumnIndexOrThrow(_stmt, "buyerId")
        val _columnIndexOfSellerId: Int = getColumnIndexOrThrow(_stmt, "sellerId")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfQuantity: Int = getColumnIndexOrThrow(_stmt, "quantity")
        val _columnIndexOfUnit: Int = getColumnIndexOrThrow(_stmt, "unit")
        val _columnIndexOfTotalAmount: Int = getColumnIndexOrThrow(_stmt, "totalAmount")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfShippingAddress: Int = getColumnIndexOrThrow(_stmt, "shippingAddress")
        val _columnIndexOfPaymentMethod: Int = getColumnIndexOrThrow(_stmt, "paymentMethod")
        val _columnIndexOfPaymentStatus: Int = getColumnIndexOrThrow(_stmt, "paymentStatus")
        val _columnIndexOfOrderDate: Int = getColumnIndexOrThrow(_stmt, "orderDate")
        val _columnIndexOfExpectedDeliveryDate: Int = getColumnIndexOrThrow(_stmt,
            "expectedDeliveryDate")
        val _columnIndexOfActualDeliveryDate: Int = getColumnIndexOrThrow(_stmt,
            "actualDeliveryDate")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfLastModifiedAt: Int = getColumnIndexOrThrow(_stmt, "lastModifiedAt")
        val _columnIndexOfIsDeleted: Int = getColumnIndexOrThrow(_stmt, "isDeleted")
        val _columnIndexOfDeletedAt: Int = getColumnIndexOrThrow(_stmt, "deletedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfDeliveryMethod: Int = getColumnIndexOrThrow(_stmt, "deliveryMethod")
        val _columnIndexOfDeliveryType: Int = getColumnIndexOrThrow(_stmt, "deliveryType")
        val _columnIndexOfDeliveryAddressJson: Int = getColumnIndexOrThrow(_stmt,
            "deliveryAddressJson")
        val _columnIndexOfNegotiationStatus: Int = getColumnIndexOrThrow(_stmt, "negotiationStatus")
        val _columnIndexOfNegotiatedPrice: Int = getColumnIndexOrThrow(_stmt, "negotiatedPrice")
        val _columnIndexOfOriginalPrice: Int = getColumnIndexOrThrow(_stmt, "originalPrice")
        val _columnIndexOfCancellationReason: Int = getColumnIndexOrThrow(_stmt,
            "cancellationReason")
        val _columnIndexOfCancellationTime: Int = getColumnIndexOrThrow(_stmt, "cancellationTime")
        val _columnIndexOfBillImageUri: Int = getColumnIndexOrThrow(_stmt, "billImageUri")
        val _columnIndexOfPaymentSlipUri: Int = getColumnIndexOrThrow(_stmt, "paymentSlipUri")
        val _columnIndexOfOtp: Int = getColumnIndexOrThrow(_stmt, "otp")
        val _columnIndexOfOtpEntered: Int = getColumnIndexOrThrow(_stmt, "otpEntered")
        val _columnIndexOfIsVerified: Int = getColumnIndexOrThrow(_stmt, "isVerified")
        val _result: OrderEntity?
        if (_stmt.step()) {
          val _tmpOrderId: String
          _tmpOrderId = _stmt.getText(_columnIndexOfOrderId)
          val _tmpBuyerId: String?
          if (_stmt.isNull(_columnIndexOfBuyerId)) {
            _tmpBuyerId = null
          } else {
            _tmpBuyerId = _stmt.getText(_columnIndexOfBuyerId)
          }
          val _tmpSellerId: String
          _tmpSellerId = _stmt.getText(_columnIndexOfSellerId)
          val _tmpProductId: String
          _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          val _tmpQuantity: Double
          _tmpQuantity = _stmt.getDouble(_columnIndexOfQuantity)
          val _tmpUnit: String?
          if (_stmt.isNull(_columnIndexOfUnit)) {
            _tmpUnit = null
          } else {
            _tmpUnit = _stmt.getText(_columnIndexOfUnit)
          }
          val _tmpTotalAmount: Double
          _tmpTotalAmount = _stmt.getDouble(_columnIndexOfTotalAmount)
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpShippingAddress: String
          _tmpShippingAddress = _stmt.getText(_columnIndexOfShippingAddress)
          val _tmpPaymentMethod: String?
          if (_stmt.isNull(_columnIndexOfPaymentMethod)) {
            _tmpPaymentMethod = null
          } else {
            _tmpPaymentMethod = _stmt.getText(_columnIndexOfPaymentMethod)
          }
          val _tmpPaymentStatus: String
          _tmpPaymentStatus = _stmt.getText(_columnIndexOfPaymentStatus)
          val _tmpOrderDate: Long
          _tmpOrderDate = _stmt.getLong(_columnIndexOfOrderDate)
          val _tmpExpectedDeliveryDate: Long?
          if (_stmt.isNull(_columnIndexOfExpectedDeliveryDate)) {
            _tmpExpectedDeliveryDate = null
          } else {
            _tmpExpectedDeliveryDate = _stmt.getLong(_columnIndexOfExpectedDeliveryDate)
          }
          val _tmpActualDeliveryDate: Long?
          if (_stmt.isNull(_columnIndexOfActualDeliveryDate)) {
            _tmpActualDeliveryDate = null
          } else {
            _tmpActualDeliveryDate = _stmt.getLong(_columnIndexOfActualDeliveryDate)
          }
          val _tmpNotes: String?
          if (_stmt.isNull(_columnIndexOfNotes)) {
            _tmpNotes = null
          } else {
            _tmpNotes = _stmt.getText(_columnIndexOfNotes)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
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
          val _tmpDeliveryMethod: String?
          if (_stmt.isNull(_columnIndexOfDeliveryMethod)) {
            _tmpDeliveryMethod = null
          } else {
            _tmpDeliveryMethod = _stmt.getText(_columnIndexOfDeliveryMethod)
          }
          val _tmpDeliveryType: String?
          if (_stmt.isNull(_columnIndexOfDeliveryType)) {
            _tmpDeliveryType = null
          } else {
            _tmpDeliveryType = _stmt.getText(_columnIndexOfDeliveryType)
          }
          val _tmpDeliveryAddressJson: String?
          if (_stmt.isNull(_columnIndexOfDeliveryAddressJson)) {
            _tmpDeliveryAddressJson = null
          } else {
            _tmpDeliveryAddressJson = _stmt.getText(_columnIndexOfDeliveryAddressJson)
          }
          val _tmpNegotiationStatus: String?
          if (_stmt.isNull(_columnIndexOfNegotiationStatus)) {
            _tmpNegotiationStatus = null
          } else {
            _tmpNegotiationStatus = _stmt.getText(_columnIndexOfNegotiationStatus)
          }
          val _tmpNegotiatedPrice: Double?
          if (_stmt.isNull(_columnIndexOfNegotiatedPrice)) {
            _tmpNegotiatedPrice = null
          } else {
            _tmpNegotiatedPrice = _stmt.getDouble(_columnIndexOfNegotiatedPrice)
          }
          val _tmpOriginalPrice: Double?
          if (_stmt.isNull(_columnIndexOfOriginalPrice)) {
            _tmpOriginalPrice = null
          } else {
            _tmpOriginalPrice = _stmt.getDouble(_columnIndexOfOriginalPrice)
          }
          val _tmpCancellationReason: String?
          if (_stmt.isNull(_columnIndexOfCancellationReason)) {
            _tmpCancellationReason = null
          } else {
            _tmpCancellationReason = _stmt.getText(_columnIndexOfCancellationReason)
          }
          val _tmpCancellationTime: Long?
          if (_stmt.isNull(_columnIndexOfCancellationTime)) {
            _tmpCancellationTime = null
          } else {
            _tmpCancellationTime = _stmt.getLong(_columnIndexOfCancellationTime)
          }
          val _tmpBillImageUri: String?
          if (_stmt.isNull(_columnIndexOfBillImageUri)) {
            _tmpBillImageUri = null
          } else {
            _tmpBillImageUri = _stmt.getText(_columnIndexOfBillImageUri)
          }
          val _tmpPaymentSlipUri: String?
          if (_stmt.isNull(_columnIndexOfPaymentSlipUri)) {
            _tmpPaymentSlipUri = null
          } else {
            _tmpPaymentSlipUri = _stmt.getText(_columnIndexOfPaymentSlipUri)
          }
          val _tmpOtp: String?
          if (_stmt.isNull(_columnIndexOfOtp)) {
            _tmpOtp = null
          } else {
            _tmpOtp = _stmt.getText(_columnIndexOfOtp)
          }
          val _tmpOtpEntered: String?
          if (_stmt.isNull(_columnIndexOfOtpEntered)) {
            _tmpOtpEntered = null
          } else {
            _tmpOtpEntered = _stmt.getText(_columnIndexOfOtpEntered)
          }
          val _tmpIsVerified: Boolean
          val _tmp_2: Int
          _tmp_2 = _stmt.getLong(_columnIndexOfIsVerified).toInt()
          _tmpIsVerified = _tmp_2 != 0
          _result =
              OrderEntity(_tmpOrderId,_tmpBuyerId,_tmpSellerId,_tmpProductId,_tmpQuantity,_tmpUnit,_tmpTotalAmount,_tmpStatus,_tmpShippingAddress,_tmpPaymentMethod,_tmpPaymentStatus,_tmpOrderDate,_tmpExpectedDeliveryDate,_tmpActualDeliveryDate,_tmpNotes,_tmpCreatedAt,_tmpUpdatedAt,_tmpLastModifiedAt,_tmpIsDeleted,_tmpDeletedAt,_tmpDirty,_tmpDeliveryMethod,_tmpDeliveryType,_tmpDeliveryAddressJson,_tmpNegotiationStatus,_tmpNegotiatedPrice,_tmpOriginalPrice,_tmpCancellationReason,_tmpCancellationTime,_tmpBillImageUri,_tmpPaymentSlipUri,_tmpOtp,_tmpOtpEntered,_tmpIsVerified)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getOrdersByBuyerId(buyerId: String): Flow<List<OrderEntity>> {
    val _sql: String = "SELECT * FROM orders WHERE buyerId = ? ORDER BY orderDate DESC"
    return createFlow(__db, false, arrayOf("orders")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, buyerId)
        val _columnIndexOfOrderId: Int = getColumnIndexOrThrow(_stmt, "orderId")
        val _columnIndexOfBuyerId: Int = getColumnIndexOrThrow(_stmt, "buyerId")
        val _columnIndexOfSellerId: Int = getColumnIndexOrThrow(_stmt, "sellerId")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfQuantity: Int = getColumnIndexOrThrow(_stmt, "quantity")
        val _columnIndexOfUnit: Int = getColumnIndexOrThrow(_stmt, "unit")
        val _columnIndexOfTotalAmount: Int = getColumnIndexOrThrow(_stmt, "totalAmount")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfShippingAddress: Int = getColumnIndexOrThrow(_stmt, "shippingAddress")
        val _columnIndexOfPaymentMethod: Int = getColumnIndexOrThrow(_stmt, "paymentMethod")
        val _columnIndexOfPaymentStatus: Int = getColumnIndexOrThrow(_stmt, "paymentStatus")
        val _columnIndexOfOrderDate: Int = getColumnIndexOrThrow(_stmt, "orderDate")
        val _columnIndexOfExpectedDeliveryDate: Int = getColumnIndexOrThrow(_stmt,
            "expectedDeliveryDate")
        val _columnIndexOfActualDeliveryDate: Int = getColumnIndexOrThrow(_stmt,
            "actualDeliveryDate")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfLastModifiedAt: Int = getColumnIndexOrThrow(_stmt, "lastModifiedAt")
        val _columnIndexOfIsDeleted: Int = getColumnIndexOrThrow(_stmt, "isDeleted")
        val _columnIndexOfDeletedAt: Int = getColumnIndexOrThrow(_stmt, "deletedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfDeliveryMethod: Int = getColumnIndexOrThrow(_stmt, "deliveryMethod")
        val _columnIndexOfDeliveryType: Int = getColumnIndexOrThrow(_stmt, "deliveryType")
        val _columnIndexOfDeliveryAddressJson: Int = getColumnIndexOrThrow(_stmt,
            "deliveryAddressJson")
        val _columnIndexOfNegotiationStatus: Int = getColumnIndexOrThrow(_stmt, "negotiationStatus")
        val _columnIndexOfNegotiatedPrice: Int = getColumnIndexOrThrow(_stmt, "negotiatedPrice")
        val _columnIndexOfOriginalPrice: Int = getColumnIndexOrThrow(_stmt, "originalPrice")
        val _columnIndexOfCancellationReason: Int = getColumnIndexOrThrow(_stmt,
            "cancellationReason")
        val _columnIndexOfCancellationTime: Int = getColumnIndexOrThrow(_stmt, "cancellationTime")
        val _columnIndexOfBillImageUri: Int = getColumnIndexOrThrow(_stmt, "billImageUri")
        val _columnIndexOfPaymentSlipUri: Int = getColumnIndexOrThrow(_stmt, "paymentSlipUri")
        val _columnIndexOfOtp: Int = getColumnIndexOrThrow(_stmt, "otp")
        val _columnIndexOfOtpEntered: Int = getColumnIndexOrThrow(_stmt, "otpEntered")
        val _columnIndexOfIsVerified: Int = getColumnIndexOrThrow(_stmt, "isVerified")
        val _result: MutableList<OrderEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: OrderEntity
          val _tmpOrderId: String
          _tmpOrderId = _stmt.getText(_columnIndexOfOrderId)
          val _tmpBuyerId: String?
          if (_stmt.isNull(_columnIndexOfBuyerId)) {
            _tmpBuyerId = null
          } else {
            _tmpBuyerId = _stmt.getText(_columnIndexOfBuyerId)
          }
          val _tmpSellerId: String
          _tmpSellerId = _stmt.getText(_columnIndexOfSellerId)
          val _tmpProductId: String
          _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          val _tmpQuantity: Double
          _tmpQuantity = _stmt.getDouble(_columnIndexOfQuantity)
          val _tmpUnit: String?
          if (_stmt.isNull(_columnIndexOfUnit)) {
            _tmpUnit = null
          } else {
            _tmpUnit = _stmt.getText(_columnIndexOfUnit)
          }
          val _tmpTotalAmount: Double
          _tmpTotalAmount = _stmt.getDouble(_columnIndexOfTotalAmount)
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpShippingAddress: String
          _tmpShippingAddress = _stmt.getText(_columnIndexOfShippingAddress)
          val _tmpPaymentMethod: String?
          if (_stmt.isNull(_columnIndexOfPaymentMethod)) {
            _tmpPaymentMethod = null
          } else {
            _tmpPaymentMethod = _stmt.getText(_columnIndexOfPaymentMethod)
          }
          val _tmpPaymentStatus: String
          _tmpPaymentStatus = _stmt.getText(_columnIndexOfPaymentStatus)
          val _tmpOrderDate: Long
          _tmpOrderDate = _stmt.getLong(_columnIndexOfOrderDate)
          val _tmpExpectedDeliveryDate: Long?
          if (_stmt.isNull(_columnIndexOfExpectedDeliveryDate)) {
            _tmpExpectedDeliveryDate = null
          } else {
            _tmpExpectedDeliveryDate = _stmt.getLong(_columnIndexOfExpectedDeliveryDate)
          }
          val _tmpActualDeliveryDate: Long?
          if (_stmt.isNull(_columnIndexOfActualDeliveryDate)) {
            _tmpActualDeliveryDate = null
          } else {
            _tmpActualDeliveryDate = _stmt.getLong(_columnIndexOfActualDeliveryDate)
          }
          val _tmpNotes: String?
          if (_stmt.isNull(_columnIndexOfNotes)) {
            _tmpNotes = null
          } else {
            _tmpNotes = _stmt.getText(_columnIndexOfNotes)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
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
          val _tmpDeliveryMethod: String?
          if (_stmt.isNull(_columnIndexOfDeliveryMethod)) {
            _tmpDeliveryMethod = null
          } else {
            _tmpDeliveryMethod = _stmt.getText(_columnIndexOfDeliveryMethod)
          }
          val _tmpDeliveryType: String?
          if (_stmt.isNull(_columnIndexOfDeliveryType)) {
            _tmpDeliveryType = null
          } else {
            _tmpDeliveryType = _stmt.getText(_columnIndexOfDeliveryType)
          }
          val _tmpDeliveryAddressJson: String?
          if (_stmt.isNull(_columnIndexOfDeliveryAddressJson)) {
            _tmpDeliveryAddressJson = null
          } else {
            _tmpDeliveryAddressJson = _stmt.getText(_columnIndexOfDeliveryAddressJson)
          }
          val _tmpNegotiationStatus: String?
          if (_stmt.isNull(_columnIndexOfNegotiationStatus)) {
            _tmpNegotiationStatus = null
          } else {
            _tmpNegotiationStatus = _stmt.getText(_columnIndexOfNegotiationStatus)
          }
          val _tmpNegotiatedPrice: Double?
          if (_stmt.isNull(_columnIndexOfNegotiatedPrice)) {
            _tmpNegotiatedPrice = null
          } else {
            _tmpNegotiatedPrice = _stmt.getDouble(_columnIndexOfNegotiatedPrice)
          }
          val _tmpOriginalPrice: Double?
          if (_stmt.isNull(_columnIndexOfOriginalPrice)) {
            _tmpOriginalPrice = null
          } else {
            _tmpOriginalPrice = _stmt.getDouble(_columnIndexOfOriginalPrice)
          }
          val _tmpCancellationReason: String?
          if (_stmt.isNull(_columnIndexOfCancellationReason)) {
            _tmpCancellationReason = null
          } else {
            _tmpCancellationReason = _stmt.getText(_columnIndexOfCancellationReason)
          }
          val _tmpCancellationTime: Long?
          if (_stmt.isNull(_columnIndexOfCancellationTime)) {
            _tmpCancellationTime = null
          } else {
            _tmpCancellationTime = _stmt.getLong(_columnIndexOfCancellationTime)
          }
          val _tmpBillImageUri: String?
          if (_stmt.isNull(_columnIndexOfBillImageUri)) {
            _tmpBillImageUri = null
          } else {
            _tmpBillImageUri = _stmt.getText(_columnIndexOfBillImageUri)
          }
          val _tmpPaymentSlipUri: String?
          if (_stmt.isNull(_columnIndexOfPaymentSlipUri)) {
            _tmpPaymentSlipUri = null
          } else {
            _tmpPaymentSlipUri = _stmt.getText(_columnIndexOfPaymentSlipUri)
          }
          val _tmpOtp: String?
          if (_stmt.isNull(_columnIndexOfOtp)) {
            _tmpOtp = null
          } else {
            _tmpOtp = _stmt.getText(_columnIndexOfOtp)
          }
          val _tmpOtpEntered: String?
          if (_stmt.isNull(_columnIndexOfOtpEntered)) {
            _tmpOtpEntered = null
          } else {
            _tmpOtpEntered = _stmt.getText(_columnIndexOfOtpEntered)
          }
          val _tmpIsVerified: Boolean
          val _tmp_2: Int
          _tmp_2 = _stmt.getLong(_columnIndexOfIsVerified).toInt()
          _tmpIsVerified = _tmp_2 != 0
          _item =
              OrderEntity(_tmpOrderId,_tmpBuyerId,_tmpSellerId,_tmpProductId,_tmpQuantity,_tmpUnit,_tmpTotalAmount,_tmpStatus,_tmpShippingAddress,_tmpPaymentMethod,_tmpPaymentStatus,_tmpOrderDate,_tmpExpectedDeliveryDate,_tmpActualDeliveryDate,_tmpNotes,_tmpCreatedAt,_tmpUpdatedAt,_tmpLastModifiedAt,_tmpIsDeleted,_tmpDeletedAt,_tmpDirty,_tmpDeliveryMethod,_tmpDeliveryType,_tmpDeliveryAddressJson,_tmpNegotiationStatus,_tmpNegotiatedPrice,_tmpOriginalPrice,_tmpCancellationReason,_tmpCancellationTime,_tmpBillImageUri,_tmpPaymentSlipUri,_tmpOtp,_tmpOtpEntered,_tmpIsVerified)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getOrdersByBuyerIdSuspend(buyerId: String): List<OrderEntity> {
    val _sql: String = "SELECT * FROM orders WHERE buyerId = ? ORDER BY orderDate DESC"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, buyerId)
        val _columnIndexOfOrderId: Int = getColumnIndexOrThrow(_stmt, "orderId")
        val _columnIndexOfBuyerId: Int = getColumnIndexOrThrow(_stmt, "buyerId")
        val _columnIndexOfSellerId: Int = getColumnIndexOrThrow(_stmt, "sellerId")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfQuantity: Int = getColumnIndexOrThrow(_stmt, "quantity")
        val _columnIndexOfUnit: Int = getColumnIndexOrThrow(_stmt, "unit")
        val _columnIndexOfTotalAmount: Int = getColumnIndexOrThrow(_stmt, "totalAmount")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfShippingAddress: Int = getColumnIndexOrThrow(_stmt, "shippingAddress")
        val _columnIndexOfPaymentMethod: Int = getColumnIndexOrThrow(_stmt, "paymentMethod")
        val _columnIndexOfPaymentStatus: Int = getColumnIndexOrThrow(_stmt, "paymentStatus")
        val _columnIndexOfOrderDate: Int = getColumnIndexOrThrow(_stmt, "orderDate")
        val _columnIndexOfExpectedDeliveryDate: Int = getColumnIndexOrThrow(_stmt,
            "expectedDeliveryDate")
        val _columnIndexOfActualDeliveryDate: Int = getColumnIndexOrThrow(_stmt,
            "actualDeliveryDate")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfLastModifiedAt: Int = getColumnIndexOrThrow(_stmt, "lastModifiedAt")
        val _columnIndexOfIsDeleted: Int = getColumnIndexOrThrow(_stmt, "isDeleted")
        val _columnIndexOfDeletedAt: Int = getColumnIndexOrThrow(_stmt, "deletedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfDeliveryMethod: Int = getColumnIndexOrThrow(_stmt, "deliveryMethod")
        val _columnIndexOfDeliveryType: Int = getColumnIndexOrThrow(_stmt, "deliveryType")
        val _columnIndexOfDeliveryAddressJson: Int = getColumnIndexOrThrow(_stmt,
            "deliveryAddressJson")
        val _columnIndexOfNegotiationStatus: Int = getColumnIndexOrThrow(_stmt, "negotiationStatus")
        val _columnIndexOfNegotiatedPrice: Int = getColumnIndexOrThrow(_stmt, "negotiatedPrice")
        val _columnIndexOfOriginalPrice: Int = getColumnIndexOrThrow(_stmt, "originalPrice")
        val _columnIndexOfCancellationReason: Int = getColumnIndexOrThrow(_stmt,
            "cancellationReason")
        val _columnIndexOfCancellationTime: Int = getColumnIndexOrThrow(_stmt, "cancellationTime")
        val _columnIndexOfBillImageUri: Int = getColumnIndexOrThrow(_stmt, "billImageUri")
        val _columnIndexOfPaymentSlipUri: Int = getColumnIndexOrThrow(_stmt, "paymentSlipUri")
        val _columnIndexOfOtp: Int = getColumnIndexOrThrow(_stmt, "otp")
        val _columnIndexOfOtpEntered: Int = getColumnIndexOrThrow(_stmt, "otpEntered")
        val _columnIndexOfIsVerified: Int = getColumnIndexOrThrow(_stmt, "isVerified")
        val _result: MutableList<OrderEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: OrderEntity
          val _tmpOrderId: String
          _tmpOrderId = _stmt.getText(_columnIndexOfOrderId)
          val _tmpBuyerId: String?
          if (_stmt.isNull(_columnIndexOfBuyerId)) {
            _tmpBuyerId = null
          } else {
            _tmpBuyerId = _stmt.getText(_columnIndexOfBuyerId)
          }
          val _tmpSellerId: String
          _tmpSellerId = _stmt.getText(_columnIndexOfSellerId)
          val _tmpProductId: String
          _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          val _tmpQuantity: Double
          _tmpQuantity = _stmt.getDouble(_columnIndexOfQuantity)
          val _tmpUnit: String?
          if (_stmt.isNull(_columnIndexOfUnit)) {
            _tmpUnit = null
          } else {
            _tmpUnit = _stmt.getText(_columnIndexOfUnit)
          }
          val _tmpTotalAmount: Double
          _tmpTotalAmount = _stmt.getDouble(_columnIndexOfTotalAmount)
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpShippingAddress: String
          _tmpShippingAddress = _stmt.getText(_columnIndexOfShippingAddress)
          val _tmpPaymentMethod: String?
          if (_stmt.isNull(_columnIndexOfPaymentMethod)) {
            _tmpPaymentMethod = null
          } else {
            _tmpPaymentMethod = _stmt.getText(_columnIndexOfPaymentMethod)
          }
          val _tmpPaymentStatus: String
          _tmpPaymentStatus = _stmt.getText(_columnIndexOfPaymentStatus)
          val _tmpOrderDate: Long
          _tmpOrderDate = _stmt.getLong(_columnIndexOfOrderDate)
          val _tmpExpectedDeliveryDate: Long?
          if (_stmt.isNull(_columnIndexOfExpectedDeliveryDate)) {
            _tmpExpectedDeliveryDate = null
          } else {
            _tmpExpectedDeliveryDate = _stmt.getLong(_columnIndexOfExpectedDeliveryDate)
          }
          val _tmpActualDeliveryDate: Long?
          if (_stmt.isNull(_columnIndexOfActualDeliveryDate)) {
            _tmpActualDeliveryDate = null
          } else {
            _tmpActualDeliveryDate = _stmt.getLong(_columnIndexOfActualDeliveryDate)
          }
          val _tmpNotes: String?
          if (_stmt.isNull(_columnIndexOfNotes)) {
            _tmpNotes = null
          } else {
            _tmpNotes = _stmt.getText(_columnIndexOfNotes)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
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
          val _tmpDeliveryMethod: String?
          if (_stmt.isNull(_columnIndexOfDeliveryMethod)) {
            _tmpDeliveryMethod = null
          } else {
            _tmpDeliveryMethod = _stmt.getText(_columnIndexOfDeliveryMethod)
          }
          val _tmpDeliveryType: String?
          if (_stmt.isNull(_columnIndexOfDeliveryType)) {
            _tmpDeliveryType = null
          } else {
            _tmpDeliveryType = _stmt.getText(_columnIndexOfDeliveryType)
          }
          val _tmpDeliveryAddressJson: String?
          if (_stmt.isNull(_columnIndexOfDeliveryAddressJson)) {
            _tmpDeliveryAddressJson = null
          } else {
            _tmpDeliveryAddressJson = _stmt.getText(_columnIndexOfDeliveryAddressJson)
          }
          val _tmpNegotiationStatus: String?
          if (_stmt.isNull(_columnIndexOfNegotiationStatus)) {
            _tmpNegotiationStatus = null
          } else {
            _tmpNegotiationStatus = _stmt.getText(_columnIndexOfNegotiationStatus)
          }
          val _tmpNegotiatedPrice: Double?
          if (_stmt.isNull(_columnIndexOfNegotiatedPrice)) {
            _tmpNegotiatedPrice = null
          } else {
            _tmpNegotiatedPrice = _stmt.getDouble(_columnIndexOfNegotiatedPrice)
          }
          val _tmpOriginalPrice: Double?
          if (_stmt.isNull(_columnIndexOfOriginalPrice)) {
            _tmpOriginalPrice = null
          } else {
            _tmpOriginalPrice = _stmt.getDouble(_columnIndexOfOriginalPrice)
          }
          val _tmpCancellationReason: String?
          if (_stmt.isNull(_columnIndexOfCancellationReason)) {
            _tmpCancellationReason = null
          } else {
            _tmpCancellationReason = _stmt.getText(_columnIndexOfCancellationReason)
          }
          val _tmpCancellationTime: Long?
          if (_stmt.isNull(_columnIndexOfCancellationTime)) {
            _tmpCancellationTime = null
          } else {
            _tmpCancellationTime = _stmt.getLong(_columnIndexOfCancellationTime)
          }
          val _tmpBillImageUri: String?
          if (_stmt.isNull(_columnIndexOfBillImageUri)) {
            _tmpBillImageUri = null
          } else {
            _tmpBillImageUri = _stmt.getText(_columnIndexOfBillImageUri)
          }
          val _tmpPaymentSlipUri: String?
          if (_stmt.isNull(_columnIndexOfPaymentSlipUri)) {
            _tmpPaymentSlipUri = null
          } else {
            _tmpPaymentSlipUri = _stmt.getText(_columnIndexOfPaymentSlipUri)
          }
          val _tmpOtp: String?
          if (_stmt.isNull(_columnIndexOfOtp)) {
            _tmpOtp = null
          } else {
            _tmpOtp = _stmt.getText(_columnIndexOfOtp)
          }
          val _tmpOtpEntered: String?
          if (_stmt.isNull(_columnIndexOfOtpEntered)) {
            _tmpOtpEntered = null
          } else {
            _tmpOtpEntered = _stmt.getText(_columnIndexOfOtpEntered)
          }
          val _tmpIsVerified: Boolean
          val _tmp_2: Int
          _tmp_2 = _stmt.getLong(_columnIndexOfIsVerified).toInt()
          _tmpIsVerified = _tmp_2 != 0
          _item =
              OrderEntity(_tmpOrderId,_tmpBuyerId,_tmpSellerId,_tmpProductId,_tmpQuantity,_tmpUnit,_tmpTotalAmount,_tmpStatus,_tmpShippingAddress,_tmpPaymentMethod,_tmpPaymentStatus,_tmpOrderDate,_tmpExpectedDeliveryDate,_tmpActualDeliveryDate,_tmpNotes,_tmpCreatedAt,_tmpUpdatedAt,_tmpLastModifiedAt,_tmpIsDeleted,_tmpDeletedAt,_tmpDirty,_tmpDeliveryMethod,_tmpDeliveryType,_tmpDeliveryAddressJson,_tmpNegotiationStatus,_tmpNegotiatedPrice,_tmpOriginalPrice,_tmpCancellationReason,_tmpCancellationTime,_tmpBillImageUri,_tmpPaymentSlipUri,_tmpOtp,_tmpOtpEntered,_tmpIsVerified)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getOrdersBySellerId(sellerId: String): Flow<List<OrderEntity>> {
    val _sql: String = "SELECT * FROM orders WHERE sellerId = ? ORDER BY orderDate DESC"
    return createFlow(__db, false, arrayOf("orders")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, sellerId)
        val _columnIndexOfOrderId: Int = getColumnIndexOrThrow(_stmt, "orderId")
        val _columnIndexOfBuyerId: Int = getColumnIndexOrThrow(_stmt, "buyerId")
        val _columnIndexOfSellerId: Int = getColumnIndexOrThrow(_stmt, "sellerId")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfQuantity: Int = getColumnIndexOrThrow(_stmt, "quantity")
        val _columnIndexOfUnit: Int = getColumnIndexOrThrow(_stmt, "unit")
        val _columnIndexOfTotalAmount: Int = getColumnIndexOrThrow(_stmt, "totalAmount")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfShippingAddress: Int = getColumnIndexOrThrow(_stmt, "shippingAddress")
        val _columnIndexOfPaymentMethod: Int = getColumnIndexOrThrow(_stmt, "paymentMethod")
        val _columnIndexOfPaymentStatus: Int = getColumnIndexOrThrow(_stmt, "paymentStatus")
        val _columnIndexOfOrderDate: Int = getColumnIndexOrThrow(_stmt, "orderDate")
        val _columnIndexOfExpectedDeliveryDate: Int = getColumnIndexOrThrow(_stmt,
            "expectedDeliveryDate")
        val _columnIndexOfActualDeliveryDate: Int = getColumnIndexOrThrow(_stmt,
            "actualDeliveryDate")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfLastModifiedAt: Int = getColumnIndexOrThrow(_stmt, "lastModifiedAt")
        val _columnIndexOfIsDeleted: Int = getColumnIndexOrThrow(_stmt, "isDeleted")
        val _columnIndexOfDeletedAt: Int = getColumnIndexOrThrow(_stmt, "deletedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfDeliveryMethod: Int = getColumnIndexOrThrow(_stmt, "deliveryMethod")
        val _columnIndexOfDeliveryType: Int = getColumnIndexOrThrow(_stmt, "deliveryType")
        val _columnIndexOfDeliveryAddressJson: Int = getColumnIndexOrThrow(_stmt,
            "deliveryAddressJson")
        val _columnIndexOfNegotiationStatus: Int = getColumnIndexOrThrow(_stmt, "negotiationStatus")
        val _columnIndexOfNegotiatedPrice: Int = getColumnIndexOrThrow(_stmt, "negotiatedPrice")
        val _columnIndexOfOriginalPrice: Int = getColumnIndexOrThrow(_stmt, "originalPrice")
        val _columnIndexOfCancellationReason: Int = getColumnIndexOrThrow(_stmt,
            "cancellationReason")
        val _columnIndexOfCancellationTime: Int = getColumnIndexOrThrow(_stmt, "cancellationTime")
        val _columnIndexOfBillImageUri: Int = getColumnIndexOrThrow(_stmt, "billImageUri")
        val _columnIndexOfPaymentSlipUri: Int = getColumnIndexOrThrow(_stmt, "paymentSlipUri")
        val _columnIndexOfOtp: Int = getColumnIndexOrThrow(_stmt, "otp")
        val _columnIndexOfOtpEntered: Int = getColumnIndexOrThrow(_stmt, "otpEntered")
        val _columnIndexOfIsVerified: Int = getColumnIndexOrThrow(_stmt, "isVerified")
        val _result: MutableList<OrderEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: OrderEntity
          val _tmpOrderId: String
          _tmpOrderId = _stmt.getText(_columnIndexOfOrderId)
          val _tmpBuyerId: String?
          if (_stmt.isNull(_columnIndexOfBuyerId)) {
            _tmpBuyerId = null
          } else {
            _tmpBuyerId = _stmt.getText(_columnIndexOfBuyerId)
          }
          val _tmpSellerId: String
          _tmpSellerId = _stmt.getText(_columnIndexOfSellerId)
          val _tmpProductId: String
          _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          val _tmpQuantity: Double
          _tmpQuantity = _stmt.getDouble(_columnIndexOfQuantity)
          val _tmpUnit: String?
          if (_stmt.isNull(_columnIndexOfUnit)) {
            _tmpUnit = null
          } else {
            _tmpUnit = _stmt.getText(_columnIndexOfUnit)
          }
          val _tmpTotalAmount: Double
          _tmpTotalAmount = _stmt.getDouble(_columnIndexOfTotalAmount)
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpShippingAddress: String
          _tmpShippingAddress = _stmt.getText(_columnIndexOfShippingAddress)
          val _tmpPaymentMethod: String?
          if (_stmt.isNull(_columnIndexOfPaymentMethod)) {
            _tmpPaymentMethod = null
          } else {
            _tmpPaymentMethod = _stmt.getText(_columnIndexOfPaymentMethod)
          }
          val _tmpPaymentStatus: String
          _tmpPaymentStatus = _stmt.getText(_columnIndexOfPaymentStatus)
          val _tmpOrderDate: Long
          _tmpOrderDate = _stmt.getLong(_columnIndexOfOrderDate)
          val _tmpExpectedDeliveryDate: Long?
          if (_stmt.isNull(_columnIndexOfExpectedDeliveryDate)) {
            _tmpExpectedDeliveryDate = null
          } else {
            _tmpExpectedDeliveryDate = _stmt.getLong(_columnIndexOfExpectedDeliveryDate)
          }
          val _tmpActualDeliveryDate: Long?
          if (_stmt.isNull(_columnIndexOfActualDeliveryDate)) {
            _tmpActualDeliveryDate = null
          } else {
            _tmpActualDeliveryDate = _stmt.getLong(_columnIndexOfActualDeliveryDate)
          }
          val _tmpNotes: String?
          if (_stmt.isNull(_columnIndexOfNotes)) {
            _tmpNotes = null
          } else {
            _tmpNotes = _stmt.getText(_columnIndexOfNotes)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
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
          val _tmpDeliveryMethod: String?
          if (_stmt.isNull(_columnIndexOfDeliveryMethod)) {
            _tmpDeliveryMethod = null
          } else {
            _tmpDeliveryMethod = _stmt.getText(_columnIndexOfDeliveryMethod)
          }
          val _tmpDeliveryType: String?
          if (_stmt.isNull(_columnIndexOfDeliveryType)) {
            _tmpDeliveryType = null
          } else {
            _tmpDeliveryType = _stmt.getText(_columnIndexOfDeliveryType)
          }
          val _tmpDeliveryAddressJson: String?
          if (_stmt.isNull(_columnIndexOfDeliveryAddressJson)) {
            _tmpDeliveryAddressJson = null
          } else {
            _tmpDeliveryAddressJson = _stmt.getText(_columnIndexOfDeliveryAddressJson)
          }
          val _tmpNegotiationStatus: String?
          if (_stmt.isNull(_columnIndexOfNegotiationStatus)) {
            _tmpNegotiationStatus = null
          } else {
            _tmpNegotiationStatus = _stmt.getText(_columnIndexOfNegotiationStatus)
          }
          val _tmpNegotiatedPrice: Double?
          if (_stmt.isNull(_columnIndexOfNegotiatedPrice)) {
            _tmpNegotiatedPrice = null
          } else {
            _tmpNegotiatedPrice = _stmt.getDouble(_columnIndexOfNegotiatedPrice)
          }
          val _tmpOriginalPrice: Double?
          if (_stmt.isNull(_columnIndexOfOriginalPrice)) {
            _tmpOriginalPrice = null
          } else {
            _tmpOriginalPrice = _stmt.getDouble(_columnIndexOfOriginalPrice)
          }
          val _tmpCancellationReason: String?
          if (_stmt.isNull(_columnIndexOfCancellationReason)) {
            _tmpCancellationReason = null
          } else {
            _tmpCancellationReason = _stmt.getText(_columnIndexOfCancellationReason)
          }
          val _tmpCancellationTime: Long?
          if (_stmt.isNull(_columnIndexOfCancellationTime)) {
            _tmpCancellationTime = null
          } else {
            _tmpCancellationTime = _stmt.getLong(_columnIndexOfCancellationTime)
          }
          val _tmpBillImageUri: String?
          if (_stmt.isNull(_columnIndexOfBillImageUri)) {
            _tmpBillImageUri = null
          } else {
            _tmpBillImageUri = _stmt.getText(_columnIndexOfBillImageUri)
          }
          val _tmpPaymentSlipUri: String?
          if (_stmt.isNull(_columnIndexOfPaymentSlipUri)) {
            _tmpPaymentSlipUri = null
          } else {
            _tmpPaymentSlipUri = _stmt.getText(_columnIndexOfPaymentSlipUri)
          }
          val _tmpOtp: String?
          if (_stmt.isNull(_columnIndexOfOtp)) {
            _tmpOtp = null
          } else {
            _tmpOtp = _stmt.getText(_columnIndexOfOtp)
          }
          val _tmpOtpEntered: String?
          if (_stmt.isNull(_columnIndexOfOtpEntered)) {
            _tmpOtpEntered = null
          } else {
            _tmpOtpEntered = _stmt.getText(_columnIndexOfOtpEntered)
          }
          val _tmpIsVerified: Boolean
          val _tmp_2: Int
          _tmp_2 = _stmt.getLong(_columnIndexOfIsVerified).toInt()
          _tmpIsVerified = _tmp_2 != 0
          _item =
              OrderEntity(_tmpOrderId,_tmpBuyerId,_tmpSellerId,_tmpProductId,_tmpQuantity,_tmpUnit,_tmpTotalAmount,_tmpStatus,_tmpShippingAddress,_tmpPaymentMethod,_tmpPaymentStatus,_tmpOrderDate,_tmpExpectedDeliveryDate,_tmpActualDeliveryDate,_tmpNotes,_tmpCreatedAt,_tmpUpdatedAt,_tmpLastModifiedAt,_tmpIsDeleted,_tmpDeletedAt,_tmpDirty,_tmpDeliveryMethod,_tmpDeliveryType,_tmpDeliveryAddressJson,_tmpNegotiationStatus,_tmpNegotiatedPrice,_tmpOriginalPrice,_tmpCancellationReason,_tmpCancellationTime,_tmpBillImageUri,_tmpPaymentSlipUri,_tmpOtp,_tmpOtpEntered,_tmpIsVerified)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getOrdersByStatus(status: String): Flow<List<OrderEntity>> {
    val _sql: String = "SELECT * FROM orders WHERE status = ? ORDER BY orderDate DESC"
    return createFlow(__db, false, arrayOf("orders")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, status)
        val _columnIndexOfOrderId: Int = getColumnIndexOrThrow(_stmt, "orderId")
        val _columnIndexOfBuyerId: Int = getColumnIndexOrThrow(_stmt, "buyerId")
        val _columnIndexOfSellerId: Int = getColumnIndexOrThrow(_stmt, "sellerId")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfQuantity: Int = getColumnIndexOrThrow(_stmt, "quantity")
        val _columnIndexOfUnit: Int = getColumnIndexOrThrow(_stmt, "unit")
        val _columnIndexOfTotalAmount: Int = getColumnIndexOrThrow(_stmt, "totalAmount")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfShippingAddress: Int = getColumnIndexOrThrow(_stmt, "shippingAddress")
        val _columnIndexOfPaymentMethod: Int = getColumnIndexOrThrow(_stmt, "paymentMethod")
        val _columnIndexOfPaymentStatus: Int = getColumnIndexOrThrow(_stmt, "paymentStatus")
        val _columnIndexOfOrderDate: Int = getColumnIndexOrThrow(_stmt, "orderDate")
        val _columnIndexOfExpectedDeliveryDate: Int = getColumnIndexOrThrow(_stmt,
            "expectedDeliveryDate")
        val _columnIndexOfActualDeliveryDate: Int = getColumnIndexOrThrow(_stmt,
            "actualDeliveryDate")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfLastModifiedAt: Int = getColumnIndexOrThrow(_stmt, "lastModifiedAt")
        val _columnIndexOfIsDeleted: Int = getColumnIndexOrThrow(_stmt, "isDeleted")
        val _columnIndexOfDeletedAt: Int = getColumnIndexOrThrow(_stmt, "deletedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfDeliveryMethod: Int = getColumnIndexOrThrow(_stmt, "deliveryMethod")
        val _columnIndexOfDeliveryType: Int = getColumnIndexOrThrow(_stmt, "deliveryType")
        val _columnIndexOfDeliveryAddressJson: Int = getColumnIndexOrThrow(_stmt,
            "deliveryAddressJson")
        val _columnIndexOfNegotiationStatus: Int = getColumnIndexOrThrow(_stmt, "negotiationStatus")
        val _columnIndexOfNegotiatedPrice: Int = getColumnIndexOrThrow(_stmt, "negotiatedPrice")
        val _columnIndexOfOriginalPrice: Int = getColumnIndexOrThrow(_stmt, "originalPrice")
        val _columnIndexOfCancellationReason: Int = getColumnIndexOrThrow(_stmt,
            "cancellationReason")
        val _columnIndexOfCancellationTime: Int = getColumnIndexOrThrow(_stmt, "cancellationTime")
        val _columnIndexOfBillImageUri: Int = getColumnIndexOrThrow(_stmt, "billImageUri")
        val _columnIndexOfPaymentSlipUri: Int = getColumnIndexOrThrow(_stmt, "paymentSlipUri")
        val _columnIndexOfOtp: Int = getColumnIndexOrThrow(_stmt, "otp")
        val _columnIndexOfOtpEntered: Int = getColumnIndexOrThrow(_stmt, "otpEntered")
        val _columnIndexOfIsVerified: Int = getColumnIndexOrThrow(_stmt, "isVerified")
        val _result: MutableList<OrderEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: OrderEntity
          val _tmpOrderId: String
          _tmpOrderId = _stmt.getText(_columnIndexOfOrderId)
          val _tmpBuyerId: String?
          if (_stmt.isNull(_columnIndexOfBuyerId)) {
            _tmpBuyerId = null
          } else {
            _tmpBuyerId = _stmt.getText(_columnIndexOfBuyerId)
          }
          val _tmpSellerId: String
          _tmpSellerId = _stmt.getText(_columnIndexOfSellerId)
          val _tmpProductId: String
          _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          val _tmpQuantity: Double
          _tmpQuantity = _stmt.getDouble(_columnIndexOfQuantity)
          val _tmpUnit: String?
          if (_stmt.isNull(_columnIndexOfUnit)) {
            _tmpUnit = null
          } else {
            _tmpUnit = _stmt.getText(_columnIndexOfUnit)
          }
          val _tmpTotalAmount: Double
          _tmpTotalAmount = _stmt.getDouble(_columnIndexOfTotalAmount)
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpShippingAddress: String
          _tmpShippingAddress = _stmt.getText(_columnIndexOfShippingAddress)
          val _tmpPaymentMethod: String?
          if (_stmt.isNull(_columnIndexOfPaymentMethod)) {
            _tmpPaymentMethod = null
          } else {
            _tmpPaymentMethod = _stmt.getText(_columnIndexOfPaymentMethod)
          }
          val _tmpPaymentStatus: String
          _tmpPaymentStatus = _stmt.getText(_columnIndexOfPaymentStatus)
          val _tmpOrderDate: Long
          _tmpOrderDate = _stmt.getLong(_columnIndexOfOrderDate)
          val _tmpExpectedDeliveryDate: Long?
          if (_stmt.isNull(_columnIndexOfExpectedDeliveryDate)) {
            _tmpExpectedDeliveryDate = null
          } else {
            _tmpExpectedDeliveryDate = _stmt.getLong(_columnIndexOfExpectedDeliveryDate)
          }
          val _tmpActualDeliveryDate: Long?
          if (_stmt.isNull(_columnIndexOfActualDeliveryDate)) {
            _tmpActualDeliveryDate = null
          } else {
            _tmpActualDeliveryDate = _stmt.getLong(_columnIndexOfActualDeliveryDate)
          }
          val _tmpNotes: String?
          if (_stmt.isNull(_columnIndexOfNotes)) {
            _tmpNotes = null
          } else {
            _tmpNotes = _stmt.getText(_columnIndexOfNotes)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
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
          val _tmpDeliveryMethod: String?
          if (_stmt.isNull(_columnIndexOfDeliveryMethod)) {
            _tmpDeliveryMethod = null
          } else {
            _tmpDeliveryMethod = _stmt.getText(_columnIndexOfDeliveryMethod)
          }
          val _tmpDeliveryType: String?
          if (_stmt.isNull(_columnIndexOfDeliveryType)) {
            _tmpDeliveryType = null
          } else {
            _tmpDeliveryType = _stmt.getText(_columnIndexOfDeliveryType)
          }
          val _tmpDeliveryAddressJson: String?
          if (_stmt.isNull(_columnIndexOfDeliveryAddressJson)) {
            _tmpDeliveryAddressJson = null
          } else {
            _tmpDeliveryAddressJson = _stmt.getText(_columnIndexOfDeliveryAddressJson)
          }
          val _tmpNegotiationStatus: String?
          if (_stmt.isNull(_columnIndexOfNegotiationStatus)) {
            _tmpNegotiationStatus = null
          } else {
            _tmpNegotiationStatus = _stmt.getText(_columnIndexOfNegotiationStatus)
          }
          val _tmpNegotiatedPrice: Double?
          if (_stmt.isNull(_columnIndexOfNegotiatedPrice)) {
            _tmpNegotiatedPrice = null
          } else {
            _tmpNegotiatedPrice = _stmt.getDouble(_columnIndexOfNegotiatedPrice)
          }
          val _tmpOriginalPrice: Double?
          if (_stmt.isNull(_columnIndexOfOriginalPrice)) {
            _tmpOriginalPrice = null
          } else {
            _tmpOriginalPrice = _stmt.getDouble(_columnIndexOfOriginalPrice)
          }
          val _tmpCancellationReason: String?
          if (_stmt.isNull(_columnIndexOfCancellationReason)) {
            _tmpCancellationReason = null
          } else {
            _tmpCancellationReason = _stmt.getText(_columnIndexOfCancellationReason)
          }
          val _tmpCancellationTime: Long?
          if (_stmt.isNull(_columnIndexOfCancellationTime)) {
            _tmpCancellationTime = null
          } else {
            _tmpCancellationTime = _stmt.getLong(_columnIndexOfCancellationTime)
          }
          val _tmpBillImageUri: String?
          if (_stmt.isNull(_columnIndexOfBillImageUri)) {
            _tmpBillImageUri = null
          } else {
            _tmpBillImageUri = _stmt.getText(_columnIndexOfBillImageUri)
          }
          val _tmpPaymentSlipUri: String?
          if (_stmt.isNull(_columnIndexOfPaymentSlipUri)) {
            _tmpPaymentSlipUri = null
          } else {
            _tmpPaymentSlipUri = _stmt.getText(_columnIndexOfPaymentSlipUri)
          }
          val _tmpOtp: String?
          if (_stmt.isNull(_columnIndexOfOtp)) {
            _tmpOtp = null
          } else {
            _tmpOtp = _stmt.getText(_columnIndexOfOtp)
          }
          val _tmpOtpEntered: String?
          if (_stmt.isNull(_columnIndexOfOtpEntered)) {
            _tmpOtpEntered = null
          } else {
            _tmpOtpEntered = _stmt.getText(_columnIndexOfOtpEntered)
          }
          val _tmpIsVerified: Boolean
          val _tmp_2: Int
          _tmp_2 = _stmt.getLong(_columnIndexOfIsVerified).toInt()
          _tmpIsVerified = _tmp_2 != 0
          _item =
              OrderEntity(_tmpOrderId,_tmpBuyerId,_tmpSellerId,_tmpProductId,_tmpQuantity,_tmpUnit,_tmpTotalAmount,_tmpStatus,_tmpShippingAddress,_tmpPaymentMethod,_tmpPaymentStatus,_tmpOrderDate,_tmpExpectedDeliveryDate,_tmpActualDeliveryDate,_tmpNotes,_tmpCreatedAt,_tmpUpdatedAt,_tmpLastModifiedAt,_tmpIsDeleted,_tmpDeletedAt,_tmpDirty,_tmpDeliveryMethod,_tmpDeliveryType,_tmpDeliveryAddressJson,_tmpNegotiationStatus,_tmpNegotiatedPrice,_tmpOriginalPrice,_tmpCancellationReason,_tmpCancellationTime,_tmpBillImageUri,_tmpPaymentSlipUri,_tmpOtp,_tmpOtpEntered,_tmpIsVerified)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getOrdersByStatus(buyerId: String, statuses: List<String>):
      Flow<List<OrderEntity>> {
    val _stringBuilder: StringBuilder = StringBuilder()
    _stringBuilder.append("SELECT * FROM orders WHERE buyerId = ")
    _stringBuilder.append("?")
    _stringBuilder.append(" AND status IN (")
    val _inputSize: Int = statuses.size
    appendPlaceholders(_stringBuilder, _inputSize)
    _stringBuilder.append(")")
    val _sql: String = _stringBuilder.toString()
    return createFlow(__db, false, arrayOf("orders")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, buyerId)
        _argIndex = 2
        for (_item: String in statuses) {
          _stmt.bindText(_argIndex, _item)
          _argIndex++
        }
        val _columnIndexOfOrderId: Int = getColumnIndexOrThrow(_stmt, "orderId")
        val _columnIndexOfBuyerId: Int = getColumnIndexOrThrow(_stmt, "buyerId")
        val _columnIndexOfSellerId: Int = getColumnIndexOrThrow(_stmt, "sellerId")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfQuantity: Int = getColumnIndexOrThrow(_stmt, "quantity")
        val _columnIndexOfUnit: Int = getColumnIndexOrThrow(_stmt, "unit")
        val _columnIndexOfTotalAmount: Int = getColumnIndexOrThrow(_stmt, "totalAmount")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfShippingAddress: Int = getColumnIndexOrThrow(_stmt, "shippingAddress")
        val _columnIndexOfPaymentMethod: Int = getColumnIndexOrThrow(_stmt, "paymentMethod")
        val _columnIndexOfPaymentStatus: Int = getColumnIndexOrThrow(_stmt, "paymentStatus")
        val _columnIndexOfOrderDate: Int = getColumnIndexOrThrow(_stmt, "orderDate")
        val _columnIndexOfExpectedDeliveryDate: Int = getColumnIndexOrThrow(_stmt,
            "expectedDeliveryDate")
        val _columnIndexOfActualDeliveryDate: Int = getColumnIndexOrThrow(_stmt,
            "actualDeliveryDate")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfLastModifiedAt: Int = getColumnIndexOrThrow(_stmt, "lastModifiedAt")
        val _columnIndexOfIsDeleted: Int = getColumnIndexOrThrow(_stmt, "isDeleted")
        val _columnIndexOfDeletedAt: Int = getColumnIndexOrThrow(_stmt, "deletedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfDeliveryMethod: Int = getColumnIndexOrThrow(_stmt, "deliveryMethod")
        val _columnIndexOfDeliveryType: Int = getColumnIndexOrThrow(_stmt, "deliveryType")
        val _columnIndexOfDeliveryAddressJson: Int = getColumnIndexOrThrow(_stmt,
            "deliveryAddressJson")
        val _columnIndexOfNegotiationStatus: Int = getColumnIndexOrThrow(_stmt, "negotiationStatus")
        val _columnIndexOfNegotiatedPrice: Int = getColumnIndexOrThrow(_stmt, "negotiatedPrice")
        val _columnIndexOfOriginalPrice: Int = getColumnIndexOrThrow(_stmt, "originalPrice")
        val _columnIndexOfCancellationReason: Int = getColumnIndexOrThrow(_stmt,
            "cancellationReason")
        val _columnIndexOfCancellationTime: Int = getColumnIndexOrThrow(_stmt, "cancellationTime")
        val _columnIndexOfBillImageUri: Int = getColumnIndexOrThrow(_stmt, "billImageUri")
        val _columnIndexOfPaymentSlipUri: Int = getColumnIndexOrThrow(_stmt, "paymentSlipUri")
        val _columnIndexOfOtp: Int = getColumnIndexOrThrow(_stmt, "otp")
        val _columnIndexOfOtpEntered: Int = getColumnIndexOrThrow(_stmt, "otpEntered")
        val _columnIndexOfIsVerified: Int = getColumnIndexOrThrow(_stmt, "isVerified")
        val _result: MutableList<OrderEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item_1: OrderEntity
          val _tmpOrderId: String
          _tmpOrderId = _stmt.getText(_columnIndexOfOrderId)
          val _tmpBuyerId: String?
          if (_stmt.isNull(_columnIndexOfBuyerId)) {
            _tmpBuyerId = null
          } else {
            _tmpBuyerId = _stmt.getText(_columnIndexOfBuyerId)
          }
          val _tmpSellerId: String
          _tmpSellerId = _stmt.getText(_columnIndexOfSellerId)
          val _tmpProductId: String
          _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          val _tmpQuantity: Double
          _tmpQuantity = _stmt.getDouble(_columnIndexOfQuantity)
          val _tmpUnit: String?
          if (_stmt.isNull(_columnIndexOfUnit)) {
            _tmpUnit = null
          } else {
            _tmpUnit = _stmt.getText(_columnIndexOfUnit)
          }
          val _tmpTotalAmount: Double
          _tmpTotalAmount = _stmt.getDouble(_columnIndexOfTotalAmount)
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpShippingAddress: String
          _tmpShippingAddress = _stmt.getText(_columnIndexOfShippingAddress)
          val _tmpPaymentMethod: String?
          if (_stmt.isNull(_columnIndexOfPaymentMethod)) {
            _tmpPaymentMethod = null
          } else {
            _tmpPaymentMethod = _stmt.getText(_columnIndexOfPaymentMethod)
          }
          val _tmpPaymentStatus: String
          _tmpPaymentStatus = _stmt.getText(_columnIndexOfPaymentStatus)
          val _tmpOrderDate: Long
          _tmpOrderDate = _stmt.getLong(_columnIndexOfOrderDate)
          val _tmpExpectedDeliveryDate: Long?
          if (_stmt.isNull(_columnIndexOfExpectedDeliveryDate)) {
            _tmpExpectedDeliveryDate = null
          } else {
            _tmpExpectedDeliveryDate = _stmt.getLong(_columnIndexOfExpectedDeliveryDate)
          }
          val _tmpActualDeliveryDate: Long?
          if (_stmt.isNull(_columnIndexOfActualDeliveryDate)) {
            _tmpActualDeliveryDate = null
          } else {
            _tmpActualDeliveryDate = _stmt.getLong(_columnIndexOfActualDeliveryDate)
          }
          val _tmpNotes: String?
          if (_stmt.isNull(_columnIndexOfNotes)) {
            _tmpNotes = null
          } else {
            _tmpNotes = _stmt.getText(_columnIndexOfNotes)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
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
          val _tmpDeliveryMethod: String?
          if (_stmt.isNull(_columnIndexOfDeliveryMethod)) {
            _tmpDeliveryMethod = null
          } else {
            _tmpDeliveryMethod = _stmt.getText(_columnIndexOfDeliveryMethod)
          }
          val _tmpDeliveryType: String?
          if (_stmt.isNull(_columnIndexOfDeliveryType)) {
            _tmpDeliveryType = null
          } else {
            _tmpDeliveryType = _stmt.getText(_columnIndexOfDeliveryType)
          }
          val _tmpDeliveryAddressJson: String?
          if (_stmt.isNull(_columnIndexOfDeliveryAddressJson)) {
            _tmpDeliveryAddressJson = null
          } else {
            _tmpDeliveryAddressJson = _stmt.getText(_columnIndexOfDeliveryAddressJson)
          }
          val _tmpNegotiationStatus: String?
          if (_stmt.isNull(_columnIndexOfNegotiationStatus)) {
            _tmpNegotiationStatus = null
          } else {
            _tmpNegotiationStatus = _stmt.getText(_columnIndexOfNegotiationStatus)
          }
          val _tmpNegotiatedPrice: Double?
          if (_stmt.isNull(_columnIndexOfNegotiatedPrice)) {
            _tmpNegotiatedPrice = null
          } else {
            _tmpNegotiatedPrice = _stmt.getDouble(_columnIndexOfNegotiatedPrice)
          }
          val _tmpOriginalPrice: Double?
          if (_stmt.isNull(_columnIndexOfOriginalPrice)) {
            _tmpOriginalPrice = null
          } else {
            _tmpOriginalPrice = _stmt.getDouble(_columnIndexOfOriginalPrice)
          }
          val _tmpCancellationReason: String?
          if (_stmt.isNull(_columnIndexOfCancellationReason)) {
            _tmpCancellationReason = null
          } else {
            _tmpCancellationReason = _stmt.getText(_columnIndexOfCancellationReason)
          }
          val _tmpCancellationTime: Long?
          if (_stmt.isNull(_columnIndexOfCancellationTime)) {
            _tmpCancellationTime = null
          } else {
            _tmpCancellationTime = _stmt.getLong(_columnIndexOfCancellationTime)
          }
          val _tmpBillImageUri: String?
          if (_stmt.isNull(_columnIndexOfBillImageUri)) {
            _tmpBillImageUri = null
          } else {
            _tmpBillImageUri = _stmt.getText(_columnIndexOfBillImageUri)
          }
          val _tmpPaymentSlipUri: String?
          if (_stmt.isNull(_columnIndexOfPaymentSlipUri)) {
            _tmpPaymentSlipUri = null
          } else {
            _tmpPaymentSlipUri = _stmt.getText(_columnIndexOfPaymentSlipUri)
          }
          val _tmpOtp: String?
          if (_stmt.isNull(_columnIndexOfOtp)) {
            _tmpOtp = null
          } else {
            _tmpOtp = _stmt.getText(_columnIndexOfOtp)
          }
          val _tmpOtpEntered: String?
          if (_stmt.isNull(_columnIndexOfOtpEntered)) {
            _tmpOtpEntered = null
          } else {
            _tmpOtpEntered = _stmt.getText(_columnIndexOfOtpEntered)
          }
          val _tmpIsVerified: Boolean
          val _tmp_2: Int
          _tmp_2 = _stmt.getLong(_columnIndexOfIsVerified).toInt()
          _tmpIsVerified = _tmp_2 != 0
          _item_1 =
              OrderEntity(_tmpOrderId,_tmpBuyerId,_tmpSellerId,_tmpProductId,_tmpQuantity,_tmpUnit,_tmpTotalAmount,_tmpStatus,_tmpShippingAddress,_tmpPaymentMethod,_tmpPaymentStatus,_tmpOrderDate,_tmpExpectedDeliveryDate,_tmpActualDeliveryDate,_tmpNotes,_tmpCreatedAt,_tmpUpdatedAt,_tmpLastModifiedAt,_tmpIsDeleted,_tmpDeletedAt,_tmpDirty,_tmpDeliveryMethod,_tmpDeliveryType,_tmpDeliveryAddressJson,_tmpNegotiationStatus,_tmpNegotiatedPrice,_tmpOriginalPrice,_tmpCancellationReason,_tmpCancellationTime,_tmpBillImageUri,_tmpPaymentSlipUri,_tmpOtp,_tmpOtpEntered,_tmpIsVerified)
          _result.add(_item_1)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getOrderItemsByOrderId(orderId: String): Flow<List<OrderItemEntity>> {
    val _sql: String = "SELECT * FROM order_items WHERE orderId = ?"
    return createFlow(__db, false, arrayOf("order_items")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, orderId)
        val _columnIndexOfOrderId: Int = getColumnIndexOrThrow(_stmt, "orderId")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfQuantity: Int = getColumnIndexOrThrow(_stmt, "quantity")
        val _columnIndexOfPriceAtPurchase: Int = getColumnIndexOrThrow(_stmt, "priceAtPurchase")
        val _columnIndexOfUnitAtPurchase: Int = getColumnIndexOrThrow(_stmt, "unitAtPurchase")
        val _result: MutableList<OrderItemEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: OrderItemEntity
          val _tmpOrderId: String
          _tmpOrderId = _stmt.getText(_columnIndexOfOrderId)
          val _tmpProductId: String
          _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          val _tmpQuantity: Double
          _tmpQuantity = _stmt.getDouble(_columnIndexOfQuantity)
          val _tmpPriceAtPurchase: Double
          _tmpPriceAtPurchase = _stmt.getDouble(_columnIndexOfPriceAtPurchase)
          val _tmpUnitAtPurchase: String
          _tmpUnitAtPurchase = _stmt.getText(_columnIndexOfUnitAtPurchase)
          _item =
              OrderItemEntity(_tmpOrderId,_tmpProductId,_tmpQuantity,_tmpPriceAtPurchase,_tmpUnitAtPurchase)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getOrderItemsList(orderId: String): List<OrderItemEntity> {
    val _sql: String = "SELECT * FROM order_items WHERE orderId = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, orderId)
        val _columnIndexOfOrderId: Int = getColumnIndexOrThrow(_stmt, "orderId")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfQuantity: Int = getColumnIndexOrThrow(_stmt, "quantity")
        val _columnIndexOfPriceAtPurchase: Int = getColumnIndexOrThrow(_stmt, "priceAtPurchase")
        val _columnIndexOfUnitAtPurchase: Int = getColumnIndexOrThrow(_stmt, "unitAtPurchase")
        val _result: MutableList<OrderItemEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: OrderItemEntity
          val _tmpOrderId: String
          _tmpOrderId = _stmt.getText(_columnIndexOfOrderId)
          val _tmpProductId: String
          _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          val _tmpQuantity: Double
          _tmpQuantity = _stmt.getDouble(_columnIndexOfQuantity)
          val _tmpPriceAtPurchase: Double
          _tmpPriceAtPurchase = _stmt.getDouble(_columnIndexOfPriceAtPurchase)
          val _tmpUnitAtPurchase: String
          _tmpUnitAtPurchase = _stmt.getText(_columnIndexOfUnitAtPurchase)
          _item =
              OrderItemEntity(_tmpOrderId,_tmpProductId,_tmpQuantity,_tmpPriceAtPurchase,_tmpUnitAtPurchase)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getUpdatedSince(since: Long, limit: Int): List<OrderEntity> {
    val _sql: String = "SELECT * FROM orders WHERE updatedAt > ? ORDER BY updatedAt ASC LIMIT ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, since)
        _argIndex = 2
        _stmt.bindLong(_argIndex, limit.toLong())
        val _columnIndexOfOrderId: Int = getColumnIndexOrThrow(_stmt, "orderId")
        val _columnIndexOfBuyerId: Int = getColumnIndexOrThrow(_stmt, "buyerId")
        val _columnIndexOfSellerId: Int = getColumnIndexOrThrow(_stmt, "sellerId")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfQuantity: Int = getColumnIndexOrThrow(_stmt, "quantity")
        val _columnIndexOfUnit: Int = getColumnIndexOrThrow(_stmt, "unit")
        val _columnIndexOfTotalAmount: Int = getColumnIndexOrThrow(_stmt, "totalAmount")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfShippingAddress: Int = getColumnIndexOrThrow(_stmt, "shippingAddress")
        val _columnIndexOfPaymentMethod: Int = getColumnIndexOrThrow(_stmt, "paymentMethod")
        val _columnIndexOfPaymentStatus: Int = getColumnIndexOrThrow(_stmt, "paymentStatus")
        val _columnIndexOfOrderDate: Int = getColumnIndexOrThrow(_stmt, "orderDate")
        val _columnIndexOfExpectedDeliveryDate: Int = getColumnIndexOrThrow(_stmt,
            "expectedDeliveryDate")
        val _columnIndexOfActualDeliveryDate: Int = getColumnIndexOrThrow(_stmt,
            "actualDeliveryDate")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfLastModifiedAt: Int = getColumnIndexOrThrow(_stmt, "lastModifiedAt")
        val _columnIndexOfIsDeleted: Int = getColumnIndexOrThrow(_stmt, "isDeleted")
        val _columnIndexOfDeletedAt: Int = getColumnIndexOrThrow(_stmt, "deletedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfDeliveryMethod: Int = getColumnIndexOrThrow(_stmt, "deliveryMethod")
        val _columnIndexOfDeliveryType: Int = getColumnIndexOrThrow(_stmt, "deliveryType")
        val _columnIndexOfDeliveryAddressJson: Int = getColumnIndexOrThrow(_stmt,
            "deliveryAddressJson")
        val _columnIndexOfNegotiationStatus: Int = getColumnIndexOrThrow(_stmt, "negotiationStatus")
        val _columnIndexOfNegotiatedPrice: Int = getColumnIndexOrThrow(_stmt, "negotiatedPrice")
        val _columnIndexOfOriginalPrice: Int = getColumnIndexOrThrow(_stmt, "originalPrice")
        val _columnIndexOfCancellationReason: Int = getColumnIndexOrThrow(_stmt,
            "cancellationReason")
        val _columnIndexOfCancellationTime: Int = getColumnIndexOrThrow(_stmt, "cancellationTime")
        val _columnIndexOfBillImageUri: Int = getColumnIndexOrThrow(_stmt, "billImageUri")
        val _columnIndexOfPaymentSlipUri: Int = getColumnIndexOrThrow(_stmt, "paymentSlipUri")
        val _columnIndexOfOtp: Int = getColumnIndexOrThrow(_stmt, "otp")
        val _columnIndexOfOtpEntered: Int = getColumnIndexOrThrow(_stmt, "otpEntered")
        val _columnIndexOfIsVerified: Int = getColumnIndexOrThrow(_stmt, "isVerified")
        val _result: MutableList<OrderEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: OrderEntity
          val _tmpOrderId: String
          _tmpOrderId = _stmt.getText(_columnIndexOfOrderId)
          val _tmpBuyerId: String?
          if (_stmt.isNull(_columnIndexOfBuyerId)) {
            _tmpBuyerId = null
          } else {
            _tmpBuyerId = _stmt.getText(_columnIndexOfBuyerId)
          }
          val _tmpSellerId: String
          _tmpSellerId = _stmt.getText(_columnIndexOfSellerId)
          val _tmpProductId: String
          _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          val _tmpQuantity: Double
          _tmpQuantity = _stmt.getDouble(_columnIndexOfQuantity)
          val _tmpUnit: String?
          if (_stmt.isNull(_columnIndexOfUnit)) {
            _tmpUnit = null
          } else {
            _tmpUnit = _stmt.getText(_columnIndexOfUnit)
          }
          val _tmpTotalAmount: Double
          _tmpTotalAmount = _stmt.getDouble(_columnIndexOfTotalAmount)
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpShippingAddress: String
          _tmpShippingAddress = _stmt.getText(_columnIndexOfShippingAddress)
          val _tmpPaymentMethod: String?
          if (_stmt.isNull(_columnIndexOfPaymentMethod)) {
            _tmpPaymentMethod = null
          } else {
            _tmpPaymentMethod = _stmt.getText(_columnIndexOfPaymentMethod)
          }
          val _tmpPaymentStatus: String
          _tmpPaymentStatus = _stmt.getText(_columnIndexOfPaymentStatus)
          val _tmpOrderDate: Long
          _tmpOrderDate = _stmt.getLong(_columnIndexOfOrderDate)
          val _tmpExpectedDeliveryDate: Long?
          if (_stmt.isNull(_columnIndexOfExpectedDeliveryDate)) {
            _tmpExpectedDeliveryDate = null
          } else {
            _tmpExpectedDeliveryDate = _stmt.getLong(_columnIndexOfExpectedDeliveryDate)
          }
          val _tmpActualDeliveryDate: Long?
          if (_stmt.isNull(_columnIndexOfActualDeliveryDate)) {
            _tmpActualDeliveryDate = null
          } else {
            _tmpActualDeliveryDate = _stmt.getLong(_columnIndexOfActualDeliveryDate)
          }
          val _tmpNotes: String?
          if (_stmt.isNull(_columnIndexOfNotes)) {
            _tmpNotes = null
          } else {
            _tmpNotes = _stmt.getText(_columnIndexOfNotes)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
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
          val _tmpDeliveryMethod: String?
          if (_stmt.isNull(_columnIndexOfDeliveryMethod)) {
            _tmpDeliveryMethod = null
          } else {
            _tmpDeliveryMethod = _stmt.getText(_columnIndexOfDeliveryMethod)
          }
          val _tmpDeliveryType: String?
          if (_stmt.isNull(_columnIndexOfDeliveryType)) {
            _tmpDeliveryType = null
          } else {
            _tmpDeliveryType = _stmt.getText(_columnIndexOfDeliveryType)
          }
          val _tmpDeliveryAddressJson: String?
          if (_stmt.isNull(_columnIndexOfDeliveryAddressJson)) {
            _tmpDeliveryAddressJson = null
          } else {
            _tmpDeliveryAddressJson = _stmt.getText(_columnIndexOfDeliveryAddressJson)
          }
          val _tmpNegotiationStatus: String?
          if (_stmt.isNull(_columnIndexOfNegotiationStatus)) {
            _tmpNegotiationStatus = null
          } else {
            _tmpNegotiationStatus = _stmt.getText(_columnIndexOfNegotiationStatus)
          }
          val _tmpNegotiatedPrice: Double?
          if (_stmt.isNull(_columnIndexOfNegotiatedPrice)) {
            _tmpNegotiatedPrice = null
          } else {
            _tmpNegotiatedPrice = _stmt.getDouble(_columnIndexOfNegotiatedPrice)
          }
          val _tmpOriginalPrice: Double?
          if (_stmt.isNull(_columnIndexOfOriginalPrice)) {
            _tmpOriginalPrice = null
          } else {
            _tmpOriginalPrice = _stmt.getDouble(_columnIndexOfOriginalPrice)
          }
          val _tmpCancellationReason: String?
          if (_stmt.isNull(_columnIndexOfCancellationReason)) {
            _tmpCancellationReason = null
          } else {
            _tmpCancellationReason = _stmt.getText(_columnIndexOfCancellationReason)
          }
          val _tmpCancellationTime: Long?
          if (_stmt.isNull(_columnIndexOfCancellationTime)) {
            _tmpCancellationTime = null
          } else {
            _tmpCancellationTime = _stmt.getLong(_columnIndexOfCancellationTime)
          }
          val _tmpBillImageUri: String?
          if (_stmt.isNull(_columnIndexOfBillImageUri)) {
            _tmpBillImageUri = null
          } else {
            _tmpBillImageUri = _stmt.getText(_columnIndexOfBillImageUri)
          }
          val _tmpPaymentSlipUri: String?
          if (_stmt.isNull(_columnIndexOfPaymentSlipUri)) {
            _tmpPaymentSlipUri = null
          } else {
            _tmpPaymentSlipUri = _stmt.getText(_columnIndexOfPaymentSlipUri)
          }
          val _tmpOtp: String?
          if (_stmt.isNull(_columnIndexOfOtp)) {
            _tmpOtp = null
          } else {
            _tmpOtp = _stmt.getText(_columnIndexOfOtp)
          }
          val _tmpOtpEntered: String?
          if (_stmt.isNull(_columnIndexOfOtpEntered)) {
            _tmpOtpEntered = null
          } else {
            _tmpOtpEntered = _stmt.getText(_columnIndexOfOtpEntered)
          }
          val _tmpIsVerified: Boolean
          val _tmp_2: Int
          _tmp_2 = _stmt.getLong(_columnIndexOfIsVerified).toInt()
          _tmpIsVerified = _tmp_2 != 0
          _item =
              OrderEntity(_tmpOrderId,_tmpBuyerId,_tmpSellerId,_tmpProductId,_tmpQuantity,_tmpUnit,_tmpTotalAmount,_tmpStatus,_tmpShippingAddress,_tmpPaymentMethod,_tmpPaymentStatus,_tmpOrderDate,_tmpExpectedDeliveryDate,_tmpActualDeliveryDate,_tmpNotes,_tmpCreatedAt,_tmpUpdatedAt,_tmpLastModifiedAt,_tmpIsDeleted,_tmpDeletedAt,_tmpDirty,_tmpDeliveryMethod,_tmpDeliveryType,_tmpDeliveryAddressJson,_tmpNegotiationStatus,_tmpNegotiatedPrice,_tmpOriginalPrice,_tmpCancellationReason,_tmpCancellationTime,_tmpBillImageUri,_tmpPaymentSlipUri,_tmpOtp,_tmpOtpEntered,_tmpIsVerified)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getRecentOrders(buyerId: String, limit: Int): Flow<List<OrderEntity>> {
    val _sql: String = "SELECT * FROM orders WHERE buyerId = ? ORDER BY orderDate DESC LIMIT ?"
    return createFlow(__db, false, arrayOf("orders")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, buyerId)
        _argIndex = 2
        _stmt.bindLong(_argIndex, limit.toLong())
        val _columnIndexOfOrderId: Int = getColumnIndexOrThrow(_stmt, "orderId")
        val _columnIndexOfBuyerId: Int = getColumnIndexOrThrow(_stmt, "buyerId")
        val _columnIndexOfSellerId: Int = getColumnIndexOrThrow(_stmt, "sellerId")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfQuantity: Int = getColumnIndexOrThrow(_stmt, "quantity")
        val _columnIndexOfUnit: Int = getColumnIndexOrThrow(_stmt, "unit")
        val _columnIndexOfTotalAmount: Int = getColumnIndexOrThrow(_stmt, "totalAmount")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfShippingAddress: Int = getColumnIndexOrThrow(_stmt, "shippingAddress")
        val _columnIndexOfPaymentMethod: Int = getColumnIndexOrThrow(_stmt, "paymentMethod")
        val _columnIndexOfPaymentStatus: Int = getColumnIndexOrThrow(_stmt, "paymentStatus")
        val _columnIndexOfOrderDate: Int = getColumnIndexOrThrow(_stmt, "orderDate")
        val _columnIndexOfExpectedDeliveryDate: Int = getColumnIndexOrThrow(_stmt,
            "expectedDeliveryDate")
        val _columnIndexOfActualDeliveryDate: Int = getColumnIndexOrThrow(_stmt,
            "actualDeliveryDate")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfLastModifiedAt: Int = getColumnIndexOrThrow(_stmt, "lastModifiedAt")
        val _columnIndexOfIsDeleted: Int = getColumnIndexOrThrow(_stmt, "isDeleted")
        val _columnIndexOfDeletedAt: Int = getColumnIndexOrThrow(_stmt, "deletedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfDeliveryMethod: Int = getColumnIndexOrThrow(_stmt, "deliveryMethod")
        val _columnIndexOfDeliveryType: Int = getColumnIndexOrThrow(_stmt, "deliveryType")
        val _columnIndexOfDeliveryAddressJson: Int = getColumnIndexOrThrow(_stmt,
            "deliveryAddressJson")
        val _columnIndexOfNegotiationStatus: Int = getColumnIndexOrThrow(_stmt, "negotiationStatus")
        val _columnIndexOfNegotiatedPrice: Int = getColumnIndexOrThrow(_stmt, "negotiatedPrice")
        val _columnIndexOfOriginalPrice: Int = getColumnIndexOrThrow(_stmt, "originalPrice")
        val _columnIndexOfCancellationReason: Int = getColumnIndexOrThrow(_stmt,
            "cancellationReason")
        val _columnIndexOfCancellationTime: Int = getColumnIndexOrThrow(_stmt, "cancellationTime")
        val _columnIndexOfBillImageUri: Int = getColumnIndexOrThrow(_stmt, "billImageUri")
        val _columnIndexOfPaymentSlipUri: Int = getColumnIndexOrThrow(_stmt, "paymentSlipUri")
        val _columnIndexOfOtp: Int = getColumnIndexOrThrow(_stmt, "otp")
        val _columnIndexOfOtpEntered: Int = getColumnIndexOrThrow(_stmt, "otpEntered")
        val _columnIndexOfIsVerified: Int = getColumnIndexOrThrow(_stmt, "isVerified")
        val _result: MutableList<OrderEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: OrderEntity
          val _tmpOrderId: String
          _tmpOrderId = _stmt.getText(_columnIndexOfOrderId)
          val _tmpBuyerId: String?
          if (_stmt.isNull(_columnIndexOfBuyerId)) {
            _tmpBuyerId = null
          } else {
            _tmpBuyerId = _stmt.getText(_columnIndexOfBuyerId)
          }
          val _tmpSellerId: String
          _tmpSellerId = _stmt.getText(_columnIndexOfSellerId)
          val _tmpProductId: String
          _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          val _tmpQuantity: Double
          _tmpQuantity = _stmt.getDouble(_columnIndexOfQuantity)
          val _tmpUnit: String?
          if (_stmt.isNull(_columnIndexOfUnit)) {
            _tmpUnit = null
          } else {
            _tmpUnit = _stmt.getText(_columnIndexOfUnit)
          }
          val _tmpTotalAmount: Double
          _tmpTotalAmount = _stmt.getDouble(_columnIndexOfTotalAmount)
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpShippingAddress: String
          _tmpShippingAddress = _stmt.getText(_columnIndexOfShippingAddress)
          val _tmpPaymentMethod: String?
          if (_stmt.isNull(_columnIndexOfPaymentMethod)) {
            _tmpPaymentMethod = null
          } else {
            _tmpPaymentMethod = _stmt.getText(_columnIndexOfPaymentMethod)
          }
          val _tmpPaymentStatus: String
          _tmpPaymentStatus = _stmt.getText(_columnIndexOfPaymentStatus)
          val _tmpOrderDate: Long
          _tmpOrderDate = _stmt.getLong(_columnIndexOfOrderDate)
          val _tmpExpectedDeliveryDate: Long?
          if (_stmt.isNull(_columnIndexOfExpectedDeliveryDate)) {
            _tmpExpectedDeliveryDate = null
          } else {
            _tmpExpectedDeliveryDate = _stmt.getLong(_columnIndexOfExpectedDeliveryDate)
          }
          val _tmpActualDeliveryDate: Long?
          if (_stmt.isNull(_columnIndexOfActualDeliveryDate)) {
            _tmpActualDeliveryDate = null
          } else {
            _tmpActualDeliveryDate = _stmt.getLong(_columnIndexOfActualDeliveryDate)
          }
          val _tmpNotes: String?
          if (_stmt.isNull(_columnIndexOfNotes)) {
            _tmpNotes = null
          } else {
            _tmpNotes = _stmt.getText(_columnIndexOfNotes)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
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
          val _tmpDeliveryMethod: String?
          if (_stmt.isNull(_columnIndexOfDeliveryMethod)) {
            _tmpDeliveryMethod = null
          } else {
            _tmpDeliveryMethod = _stmt.getText(_columnIndexOfDeliveryMethod)
          }
          val _tmpDeliveryType: String?
          if (_stmt.isNull(_columnIndexOfDeliveryType)) {
            _tmpDeliveryType = null
          } else {
            _tmpDeliveryType = _stmt.getText(_columnIndexOfDeliveryType)
          }
          val _tmpDeliveryAddressJson: String?
          if (_stmt.isNull(_columnIndexOfDeliveryAddressJson)) {
            _tmpDeliveryAddressJson = null
          } else {
            _tmpDeliveryAddressJson = _stmt.getText(_columnIndexOfDeliveryAddressJson)
          }
          val _tmpNegotiationStatus: String?
          if (_stmt.isNull(_columnIndexOfNegotiationStatus)) {
            _tmpNegotiationStatus = null
          } else {
            _tmpNegotiationStatus = _stmt.getText(_columnIndexOfNegotiationStatus)
          }
          val _tmpNegotiatedPrice: Double?
          if (_stmt.isNull(_columnIndexOfNegotiatedPrice)) {
            _tmpNegotiatedPrice = null
          } else {
            _tmpNegotiatedPrice = _stmt.getDouble(_columnIndexOfNegotiatedPrice)
          }
          val _tmpOriginalPrice: Double?
          if (_stmt.isNull(_columnIndexOfOriginalPrice)) {
            _tmpOriginalPrice = null
          } else {
            _tmpOriginalPrice = _stmt.getDouble(_columnIndexOfOriginalPrice)
          }
          val _tmpCancellationReason: String?
          if (_stmt.isNull(_columnIndexOfCancellationReason)) {
            _tmpCancellationReason = null
          } else {
            _tmpCancellationReason = _stmt.getText(_columnIndexOfCancellationReason)
          }
          val _tmpCancellationTime: Long?
          if (_stmt.isNull(_columnIndexOfCancellationTime)) {
            _tmpCancellationTime = null
          } else {
            _tmpCancellationTime = _stmt.getLong(_columnIndexOfCancellationTime)
          }
          val _tmpBillImageUri: String?
          if (_stmt.isNull(_columnIndexOfBillImageUri)) {
            _tmpBillImageUri = null
          } else {
            _tmpBillImageUri = _stmt.getText(_columnIndexOfBillImageUri)
          }
          val _tmpPaymentSlipUri: String?
          if (_stmt.isNull(_columnIndexOfPaymentSlipUri)) {
            _tmpPaymentSlipUri = null
          } else {
            _tmpPaymentSlipUri = _stmt.getText(_columnIndexOfPaymentSlipUri)
          }
          val _tmpOtp: String?
          if (_stmt.isNull(_columnIndexOfOtp)) {
            _tmpOtp = null
          } else {
            _tmpOtp = _stmt.getText(_columnIndexOfOtp)
          }
          val _tmpOtpEntered: String?
          if (_stmt.isNull(_columnIndexOfOtpEntered)) {
            _tmpOtpEntered = null
          } else {
            _tmpOtpEntered = _stmt.getText(_columnIndexOfOtpEntered)
          }
          val _tmpIsVerified: Boolean
          val _tmp_2: Int
          _tmp_2 = _stmt.getLong(_columnIndexOfIsVerified).toInt()
          _tmpIsVerified = _tmp_2 != 0
          _item =
              OrderEntity(_tmpOrderId,_tmpBuyerId,_tmpSellerId,_tmpProductId,_tmpQuantity,_tmpUnit,_tmpTotalAmount,_tmpStatus,_tmpShippingAddress,_tmpPaymentMethod,_tmpPaymentStatus,_tmpOrderDate,_tmpExpectedDeliveryDate,_tmpActualDeliveryDate,_tmpNotes,_tmpCreatedAt,_tmpUpdatedAt,_tmpLastModifiedAt,_tmpIsDeleted,_tmpDeletedAt,_tmpDirty,_tmpDeliveryMethod,_tmpDeliveryType,_tmpDeliveryAddressJson,_tmpNegotiationStatus,_tmpNegotiatedPrice,_tmpOriginalPrice,_tmpCancellationReason,_tmpCancellationTime,_tmpBillImageUri,_tmpPaymentSlipUri,_tmpOtp,_tmpOtpEntered,_tmpIsVerified)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun sumDeliveredForSellerBetween(
    sellerId: String,
    start: Long,
    end: Long,
  ): Double {
    val _sql: String =
        "SELECT IFNULL(SUM(totalAmount), 0) FROM orders WHERE sellerId = ? AND status = 'DELIVERED' AND updatedAt BETWEEN ? AND ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, sellerId)
        _argIndex = 2
        _stmt.bindLong(_argIndex, start)
        _argIndex = 3
        _stmt.bindLong(_argIndex, end)
        val _result: Double
        if (_stmt.step()) {
          val _tmp: Double
          _tmp = _stmt.getDouble(0)
          _result = _tmp
        } else {
          _result = 0.0
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun countDeliveredForSellerBetween(
    sellerId: String,
    start: Long,
    end: Long,
  ): Int {
    val _sql: String =
        "SELECT COUNT(*) FROM orders WHERE sellerId = ? AND status = 'DELIVERED' AND updatedAt BETWEEN ? AND ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, sellerId)
        _argIndex = 2
        _stmt.bindLong(_argIndex, start)
        _argIndex = 3
        _stmt.bindLong(_argIndex, end)
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

  public override suspend fun getDeliveredOrdersForFarmerBetween(
    farmerId: String,
    start: Long,
    end: Long,
  ): List<OrderEntity> {
    val _sql: String =
        "SELECT * FROM orders WHERE sellerId = ? AND status = 'DELIVERED' AND updatedAt BETWEEN ? AND ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, farmerId)
        _argIndex = 2
        _stmt.bindLong(_argIndex, start)
        _argIndex = 3
        _stmt.bindLong(_argIndex, end)
        val _columnIndexOfOrderId: Int = getColumnIndexOrThrow(_stmt, "orderId")
        val _columnIndexOfBuyerId: Int = getColumnIndexOrThrow(_stmt, "buyerId")
        val _columnIndexOfSellerId: Int = getColumnIndexOrThrow(_stmt, "sellerId")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfQuantity: Int = getColumnIndexOrThrow(_stmt, "quantity")
        val _columnIndexOfUnit: Int = getColumnIndexOrThrow(_stmt, "unit")
        val _columnIndexOfTotalAmount: Int = getColumnIndexOrThrow(_stmt, "totalAmount")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfShippingAddress: Int = getColumnIndexOrThrow(_stmt, "shippingAddress")
        val _columnIndexOfPaymentMethod: Int = getColumnIndexOrThrow(_stmt, "paymentMethod")
        val _columnIndexOfPaymentStatus: Int = getColumnIndexOrThrow(_stmt, "paymentStatus")
        val _columnIndexOfOrderDate: Int = getColumnIndexOrThrow(_stmt, "orderDate")
        val _columnIndexOfExpectedDeliveryDate: Int = getColumnIndexOrThrow(_stmt,
            "expectedDeliveryDate")
        val _columnIndexOfActualDeliveryDate: Int = getColumnIndexOrThrow(_stmt,
            "actualDeliveryDate")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfLastModifiedAt: Int = getColumnIndexOrThrow(_stmt, "lastModifiedAt")
        val _columnIndexOfIsDeleted: Int = getColumnIndexOrThrow(_stmt, "isDeleted")
        val _columnIndexOfDeletedAt: Int = getColumnIndexOrThrow(_stmt, "deletedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfDeliveryMethod: Int = getColumnIndexOrThrow(_stmt, "deliveryMethod")
        val _columnIndexOfDeliveryType: Int = getColumnIndexOrThrow(_stmt, "deliveryType")
        val _columnIndexOfDeliveryAddressJson: Int = getColumnIndexOrThrow(_stmt,
            "deliveryAddressJson")
        val _columnIndexOfNegotiationStatus: Int = getColumnIndexOrThrow(_stmt, "negotiationStatus")
        val _columnIndexOfNegotiatedPrice: Int = getColumnIndexOrThrow(_stmt, "negotiatedPrice")
        val _columnIndexOfOriginalPrice: Int = getColumnIndexOrThrow(_stmt, "originalPrice")
        val _columnIndexOfCancellationReason: Int = getColumnIndexOrThrow(_stmt,
            "cancellationReason")
        val _columnIndexOfCancellationTime: Int = getColumnIndexOrThrow(_stmt, "cancellationTime")
        val _columnIndexOfBillImageUri: Int = getColumnIndexOrThrow(_stmt, "billImageUri")
        val _columnIndexOfPaymentSlipUri: Int = getColumnIndexOrThrow(_stmt, "paymentSlipUri")
        val _columnIndexOfOtp: Int = getColumnIndexOrThrow(_stmt, "otp")
        val _columnIndexOfOtpEntered: Int = getColumnIndexOrThrow(_stmt, "otpEntered")
        val _columnIndexOfIsVerified: Int = getColumnIndexOrThrow(_stmt, "isVerified")
        val _result: MutableList<OrderEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: OrderEntity
          val _tmpOrderId: String
          _tmpOrderId = _stmt.getText(_columnIndexOfOrderId)
          val _tmpBuyerId: String?
          if (_stmt.isNull(_columnIndexOfBuyerId)) {
            _tmpBuyerId = null
          } else {
            _tmpBuyerId = _stmt.getText(_columnIndexOfBuyerId)
          }
          val _tmpSellerId: String
          _tmpSellerId = _stmt.getText(_columnIndexOfSellerId)
          val _tmpProductId: String
          _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          val _tmpQuantity: Double
          _tmpQuantity = _stmt.getDouble(_columnIndexOfQuantity)
          val _tmpUnit: String?
          if (_stmt.isNull(_columnIndexOfUnit)) {
            _tmpUnit = null
          } else {
            _tmpUnit = _stmt.getText(_columnIndexOfUnit)
          }
          val _tmpTotalAmount: Double
          _tmpTotalAmount = _stmt.getDouble(_columnIndexOfTotalAmount)
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpShippingAddress: String
          _tmpShippingAddress = _stmt.getText(_columnIndexOfShippingAddress)
          val _tmpPaymentMethod: String?
          if (_stmt.isNull(_columnIndexOfPaymentMethod)) {
            _tmpPaymentMethod = null
          } else {
            _tmpPaymentMethod = _stmt.getText(_columnIndexOfPaymentMethod)
          }
          val _tmpPaymentStatus: String
          _tmpPaymentStatus = _stmt.getText(_columnIndexOfPaymentStatus)
          val _tmpOrderDate: Long
          _tmpOrderDate = _stmt.getLong(_columnIndexOfOrderDate)
          val _tmpExpectedDeliveryDate: Long?
          if (_stmt.isNull(_columnIndexOfExpectedDeliveryDate)) {
            _tmpExpectedDeliveryDate = null
          } else {
            _tmpExpectedDeliveryDate = _stmt.getLong(_columnIndexOfExpectedDeliveryDate)
          }
          val _tmpActualDeliveryDate: Long?
          if (_stmt.isNull(_columnIndexOfActualDeliveryDate)) {
            _tmpActualDeliveryDate = null
          } else {
            _tmpActualDeliveryDate = _stmt.getLong(_columnIndexOfActualDeliveryDate)
          }
          val _tmpNotes: String?
          if (_stmt.isNull(_columnIndexOfNotes)) {
            _tmpNotes = null
          } else {
            _tmpNotes = _stmt.getText(_columnIndexOfNotes)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
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
          val _tmpDeliveryMethod: String?
          if (_stmt.isNull(_columnIndexOfDeliveryMethod)) {
            _tmpDeliveryMethod = null
          } else {
            _tmpDeliveryMethod = _stmt.getText(_columnIndexOfDeliveryMethod)
          }
          val _tmpDeliveryType: String?
          if (_stmt.isNull(_columnIndexOfDeliveryType)) {
            _tmpDeliveryType = null
          } else {
            _tmpDeliveryType = _stmt.getText(_columnIndexOfDeliveryType)
          }
          val _tmpDeliveryAddressJson: String?
          if (_stmt.isNull(_columnIndexOfDeliveryAddressJson)) {
            _tmpDeliveryAddressJson = null
          } else {
            _tmpDeliveryAddressJson = _stmt.getText(_columnIndexOfDeliveryAddressJson)
          }
          val _tmpNegotiationStatus: String?
          if (_stmt.isNull(_columnIndexOfNegotiationStatus)) {
            _tmpNegotiationStatus = null
          } else {
            _tmpNegotiationStatus = _stmt.getText(_columnIndexOfNegotiationStatus)
          }
          val _tmpNegotiatedPrice: Double?
          if (_stmt.isNull(_columnIndexOfNegotiatedPrice)) {
            _tmpNegotiatedPrice = null
          } else {
            _tmpNegotiatedPrice = _stmt.getDouble(_columnIndexOfNegotiatedPrice)
          }
          val _tmpOriginalPrice: Double?
          if (_stmt.isNull(_columnIndexOfOriginalPrice)) {
            _tmpOriginalPrice = null
          } else {
            _tmpOriginalPrice = _stmt.getDouble(_columnIndexOfOriginalPrice)
          }
          val _tmpCancellationReason: String?
          if (_stmt.isNull(_columnIndexOfCancellationReason)) {
            _tmpCancellationReason = null
          } else {
            _tmpCancellationReason = _stmt.getText(_columnIndexOfCancellationReason)
          }
          val _tmpCancellationTime: Long?
          if (_stmt.isNull(_columnIndexOfCancellationTime)) {
            _tmpCancellationTime = null
          } else {
            _tmpCancellationTime = _stmt.getLong(_columnIndexOfCancellationTime)
          }
          val _tmpBillImageUri: String?
          if (_stmt.isNull(_columnIndexOfBillImageUri)) {
            _tmpBillImageUri = null
          } else {
            _tmpBillImageUri = _stmt.getText(_columnIndexOfBillImageUri)
          }
          val _tmpPaymentSlipUri: String?
          if (_stmt.isNull(_columnIndexOfPaymentSlipUri)) {
            _tmpPaymentSlipUri = null
          } else {
            _tmpPaymentSlipUri = _stmt.getText(_columnIndexOfPaymentSlipUri)
          }
          val _tmpOtp: String?
          if (_stmt.isNull(_columnIndexOfOtp)) {
            _tmpOtp = null
          } else {
            _tmpOtp = _stmt.getText(_columnIndexOfOtp)
          }
          val _tmpOtpEntered: String?
          if (_stmt.isNull(_columnIndexOfOtpEntered)) {
            _tmpOtpEntered = null
          } else {
            _tmpOtpEntered = _stmt.getText(_columnIndexOfOtpEntered)
          }
          val _tmpIsVerified: Boolean
          val _tmp_2: Int
          _tmp_2 = _stmt.getLong(_columnIndexOfIsVerified).toInt()
          _tmpIsVerified = _tmp_2 != 0
          _item =
              OrderEntity(_tmpOrderId,_tmpBuyerId,_tmpSellerId,_tmpProductId,_tmpQuantity,_tmpUnit,_tmpTotalAmount,_tmpStatus,_tmpShippingAddress,_tmpPaymentMethod,_tmpPaymentStatus,_tmpOrderDate,_tmpExpectedDeliveryDate,_tmpActualDeliveryDate,_tmpNotes,_tmpCreatedAt,_tmpUpdatedAt,_tmpLastModifiedAt,_tmpIsDeleted,_tmpDeletedAt,_tmpDirty,_tmpDeliveryMethod,_tmpDeliveryType,_tmpDeliveryAddressJson,_tmpNegotiationStatus,_tmpNegotiatedPrice,_tmpOriginalPrice,_tmpCancellationReason,_tmpCancellationTime,_tmpBillImageUri,_tmpPaymentSlipUri,_tmpOtp,_tmpOtpEntered,_tmpIsVerified)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getOrdersForBatch(batchId: String): List<OrderEntity> {
    val _sql: String =
        "SELECT DISTINCT o.* FROM orders o INNER JOIN order_items oi ON o.orderId = oi.orderId WHERE oi.productId IN (SELECT productId FROM products WHERE batchId = ?)"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, batchId)
        val _columnIndexOfOrderId: Int = getColumnIndexOrThrow(_stmt, "orderId")
        val _columnIndexOfBuyerId: Int = getColumnIndexOrThrow(_stmt, "buyerId")
        val _columnIndexOfSellerId: Int = getColumnIndexOrThrow(_stmt, "sellerId")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfQuantity: Int = getColumnIndexOrThrow(_stmt, "quantity")
        val _columnIndexOfUnit: Int = getColumnIndexOrThrow(_stmt, "unit")
        val _columnIndexOfTotalAmount: Int = getColumnIndexOrThrow(_stmt, "totalAmount")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfShippingAddress: Int = getColumnIndexOrThrow(_stmt, "shippingAddress")
        val _columnIndexOfPaymentMethod: Int = getColumnIndexOrThrow(_stmt, "paymentMethod")
        val _columnIndexOfPaymentStatus: Int = getColumnIndexOrThrow(_stmt, "paymentStatus")
        val _columnIndexOfOrderDate: Int = getColumnIndexOrThrow(_stmt, "orderDate")
        val _columnIndexOfExpectedDeliveryDate: Int = getColumnIndexOrThrow(_stmt,
            "expectedDeliveryDate")
        val _columnIndexOfActualDeliveryDate: Int = getColumnIndexOrThrow(_stmt,
            "actualDeliveryDate")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfLastModifiedAt: Int = getColumnIndexOrThrow(_stmt, "lastModifiedAt")
        val _columnIndexOfIsDeleted: Int = getColumnIndexOrThrow(_stmt, "isDeleted")
        val _columnIndexOfDeletedAt: Int = getColumnIndexOrThrow(_stmt, "deletedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfDeliveryMethod: Int = getColumnIndexOrThrow(_stmt, "deliveryMethod")
        val _columnIndexOfDeliveryType: Int = getColumnIndexOrThrow(_stmt, "deliveryType")
        val _columnIndexOfDeliveryAddressJson: Int = getColumnIndexOrThrow(_stmt,
            "deliveryAddressJson")
        val _columnIndexOfNegotiationStatus: Int = getColumnIndexOrThrow(_stmt, "negotiationStatus")
        val _columnIndexOfNegotiatedPrice: Int = getColumnIndexOrThrow(_stmt, "negotiatedPrice")
        val _columnIndexOfOriginalPrice: Int = getColumnIndexOrThrow(_stmt, "originalPrice")
        val _columnIndexOfCancellationReason: Int = getColumnIndexOrThrow(_stmt,
            "cancellationReason")
        val _columnIndexOfCancellationTime: Int = getColumnIndexOrThrow(_stmt, "cancellationTime")
        val _columnIndexOfBillImageUri: Int = getColumnIndexOrThrow(_stmt, "billImageUri")
        val _columnIndexOfPaymentSlipUri: Int = getColumnIndexOrThrow(_stmt, "paymentSlipUri")
        val _columnIndexOfOtp: Int = getColumnIndexOrThrow(_stmt, "otp")
        val _columnIndexOfOtpEntered: Int = getColumnIndexOrThrow(_stmt, "otpEntered")
        val _columnIndexOfIsVerified: Int = getColumnIndexOrThrow(_stmt, "isVerified")
        val _result: MutableList<OrderEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: OrderEntity
          val _tmpOrderId: String
          _tmpOrderId = _stmt.getText(_columnIndexOfOrderId)
          val _tmpBuyerId: String?
          if (_stmt.isNull(_columnIndexOfBuyerId)) {
            _tmpBuyerId = null
          } else {
            _tmpBuyerId = _stmt.getText(_columnIndexOfBuyerId)
          }
          val _tmpSellerId: String
          _tmpSellerId = _stmt.getText(_columnIndexOfSellerId)
          val _tmpProductId: String
          _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          val _tmpQuantity: Double
          _tmpQuantity = _stmt.getDouble(_columnIndexOfQuantity)
          val _tmpUnit: String?
          if (_stmt.isNull(_columnIndexOfUnit)) {
            _tmpUnit = null
          } else {
            _tmpUnit = _stmt.getText(_columnIndexOfUnit)
          }
          val _tmpTotalAmount: Double
          _tmpTotalAmount = _stmt.getDouble(_columnIndexOfTotalAmount)
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpShippingAddress: String
          _tmpShippingAddress = _stmt.getText(_columnIndexOfShippingAddress)
          val _tmpPaymentMethod: String?
          if (_stmt.isNull(_columnIndexOfPaymentMethod)) {
            _tmpPaymentMethod = null
          } else {
            _tmpPaymentMethod = _stmt.getText(_columnIndexOfPaymentMethod)
          }
          val _tmpPaymentStatus: String
          _tmpPaymentStatus = _stmt.getText(_columnIndexOfPaymentStatus)
          val _tmpOrderDate: Long
          _tmpOrderDate = _stmt.getLong(_columnIndexOfOrderDate)
          val _tmpExpectedDeliveryDate: Long?
          if (_stmt.isNull(_columnIndexOfExpectedDeliveryDate)) {
            _tmpExpectedDeliveryDate = null
          } else {
            _tmpExpectedDeliveryDate = _stmt.getLong(_columnIndexOfExpectedDeliveryDate)
          }
          val _tmpActualDeliveryDate: Long?
          if (_stmt.isNull(_columnIndexOfActualDeliveryDate)) {
            _tmpActualDeliveryDate = null
          } else {
            _tmpActualDeliveryDate = _stmt.getLong(_columnIndexOfActualDeliveryDate)
          }
          val _tmpNotes: String?
          if (_stmt.isNull(_columnIndexOfNotes)) {
            _tmpNotes = null
          } else {
            _tmpNotes = _stmt.getText(_columnIndexOfNotes)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
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
          val _tmpDeliveryMethod: String?
          if (_stmt.isNull(_columnIndexOfDeliveryMethod)) {
            _tmpDeliveryMethod = null
          } else {
            _tmpDeliveryMethod = _stmt.getText(_columnIndexOfDeliveryMethod)
          }
          val _tmpDeliveryType: String?
          if (_stmt.isNull(_columnIndexOfDeliveryType)) {
            _tmpDeliveryType = null
          } else {
            _tmpDeliveryType = _stmt.getText(_columnIndexOfDeliveryType)
          }
          val _tmpDeliveryAddressJson: String?
          if (_stmt.isNull(_columnIndexOfDeliveryAddressJson)) {
            _tmpDeliveryAddressJson = null
          } else {
            _tmpDeliveryAddressJson = _stmt.getText(_columnIndexOfDeliveryAddressJson)
          }
          val _tmpNegotiationStatus: String?
          if (_stmt.isNull(_columnIndexOfNegotiationStatus)) {
            _tmpNegotiationStatus = null
          } else {
            _tmpNegotiationStatus = _stmt.getText(_columnIndexOfNegotiationStatus)
          }
          val _tmpNegotiatedPrice: Double?
          if (_stmt.isNull(_columnIndexOfNegotiatedPrice)) {
            _tmpNegotiatedPrice = null
          } else {
            _tmpNegotiatedPrice = _stmt.getDouble(_columnIndexOfNegotiatedPrice)
          }
          val _tmpOriginalPrice: Double?
          if (_stmt.isNull(_columnIndexOfOriginalPrice)) {
            _tmpOriginalPrice = null
          } else {
            _tmpOriginalPrice = _stmt.getDouble(_columnIndexOfOriginalPrice)
          }
          val _tmpCancellationReason: String?
          if (_stmt.isNull(_columnIndexOfCancellationReason)) {
            _tmpCancellationReason = null
          } else {
            _tmpCancellationReason = _stmt.getText(_columnIndexOfCancellationReason)
          }
          val _tmpCancellationTime: Long?
          if (_stmt.isNull(_columnIndexOfCancellationTime)) {
            _tmpCancellationTime = null
          } else {
            _tmpCancellationTime = _stmt.getLong(_columnIndexOfCancellationTime)
          }
          val _tmpBillImageUri: String?
          if (_stmt.isNull(_columnIndexOfBillImageUri)) {
            _tmpBillImageUri = null
          } else {
            _tmpBillImageUri = _stmt.getText(_columnIndexOfBillImageUri)
          }
          val _tmpPaymentSlipUri: String?
          if (_stmt.isNull(_columnIndexOfPaymentSlipUri)) {
            _tmpPaymentSlipUri = null
          } else {
            _tmpPaymentSlipUri = _stmt.getText(_columnIndexOfPaymentSlipUri)
          }
          val _tmpOtp: String?
          if (_stmt.isNull(_columnIndexOfOtp)) {
            _tmpOtp = null
          } else {
            _tmpOtp = _stmt.getText(_columnIndexOfOtp)
          }
          val _tmpOtpEntered: String?
          if (_stmt.isNull(_columnIndexOfOtpEntered)) {
            _tmpOtpEntered = null
          } else {
            _tmpOtpEntered = _stmt.getText(_columnIndexOfOtpEntered)
          }
          val _tmpIsVerified: Boolean
          val _tmp_2: Int
          _tmp_2 = _stmt.getLong(_columnIndexOfIsVerified).toInt()
          _tmpIsVerified = _tmp_2 != 0
          _item =
              OrderEntity(_tmpOrderId,_tmpBuyerId,_tmpSellerId,_tmpProductId,_tmpQuantity,_tmpUnit,_tmpTotalAmount,_tmpStatus,_tmpShippingAddress,_tmpPaymentMethod,_tmpPaymentStatus,_tmpOrderDate,_tmpExpectedDeliveryDate,_tmpActualDeliveryDate,_tmpNotes,_tmpCreatedAt,_tmpUpdatedAt,_tmpLastModifiedAt,_tmpIsDeleted,_tmpDeletedAt,_tmpDirty,_tmpDeliveryMethod,_tmpDeliveryType,_tmpDeliveryAddressJson,_tmpNegotiationStatus,_tmpNegotiatedPrice,_tmpOriginalPrice,_tmpCancellationReason,_tmpCancellationTime,_tmpBillImageUri,_tmpPaymentSlipUri,_tmpOtp,_tmpOtpEntered,_tmpIsVerified)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getOrdersForProduct(productId: String): List<OrderEntity> {
    val _sql: String =
        "SELECT DISTINCT o.* FROM orders o INNER JOIN order_items oi ON o.orderId = oi.orderId WHERE oi.productId = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, productId)
        val _columnIndexOfOrderId: Int = getColumnIndexOrThrow(_stmt, "orderId")
        val _columnIndexOfBuyerId: Int = getColumnIndexOrThrow(_stmt, "buyerId")
        val _columnIndexOfSellerId: Int = getColumnIndexOrThrow(_stmt, "sellerId")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfQuantity: Int = getColumnIndexOrThrow(_stmt, "quantity")
        val _columnIndexOfUnit: Int = getColumnIndexOrThrow(_stmt, "unit")
        val _columnIndexOfTotalAmount: Int = getColumnIndexOrThrow(_stmt, "totalAmount")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfShippingAddress: Int = getColumnIndexOrThrow(_stmt, "shippingAddress")
        val _columnIndexOfPaymentMethod: Int = getColumnIndexOrThrow(_stmt, "paymentMethod")
        val _columnIndexOfPaymentStatus: Int = getColumnIndexOrThrow(_stmt, "paymentStatus")
        val _columnIndexOfOrderDate: Int = getColumnIndexOrThrow(_stmt, "orderDate")
        val _columnIndexOfExpectedDeliveryDate: Int = getColumnIndexOrThrow(_stmt,
            "expectedDeliveryDate")
        val _columnIndexOfActualDeliveryDate: Int = getColumnIndexOrThrow(_stmt,
            "actualDeliveryDate")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfLastModifiedAt: Int = getColumnIndexOrThrow(_stmt, "lastModifiedAt")
        val _columnIndexOfIsDeleted: Int = getColumnIndexOrThrow(_stmt, "isDeleted")
        val _columnIndexOfDeletedAt: Int = getColumnIndexOrThrow(_stmt, "deletedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfDeliveryMethod: Int = getColumnIndexOrThrow(_stmt, "deliveryMethod")
        val _columnIndexOfDeliveryType: Int = getColumnIndexOrThrow(_stmt, "deliveryType")
        val _columnIndexOfDeliveryAddressJson: Int = getColumnIndexOrThrow(_stmt,
            "deliveryAddressJson")
        val _columnIndexOfNegotiationStatus: Int = getColumnIndexOrThrow(_stmt, "negotiationStatus")
        val _columnIndexOfNegotiatedPrice: Int = getColumnIndexOrThrow(_stmt, "negotiatedPrice")
        val _columnIndexOfOriginalPrice: Int = getColumnIndexOrThrow(_stmt, "originalPrice")
        val _columnIndexOfCancellationReason: Int = getColumnIndexOrThrow(_stmt,
            "cancellationReason")
        val _columnIndexOfCancellationTime: Int = getColumnIndexOrThrow(_stmt, "cancellationTime")
        val _columnIndexOfBillImageUri: Int = getColumnIndexOrThrow(_stmt, "billImageUri")
        val _columnIndexOfPaymentSlipUri: Int = getColumnIndexOrThrow(_stmt, "paymentSlipUri")
        val _columnIndexOfOtp: Int = getColumnIndexOrThrow(_stmt, "otp")
        val _columnIndexOfOtpEntered: Int = getColumnIndexOrThrow(_stmt, "otpEntered")
        val _columnIndexOfIsVerified: Int = getColumnIndexOrThrow(_stmt, "isVerified")
        val _result: MutableList<OrderEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: OrderEntity
          val _tmpOrderId: String
          _tmpOrderId = _stmt.getText(_columnIndexOfOrderId)
          val _tmpBuyerId: String?
          if (_stmt.isNull(_columnIndexOfBuyerId)) {
            _tmpBuyerId = null
          } else {
            _tmpBuyerId = _stmt.getText(_columnIndexOfBuyerId)
          }
          val _tmpSellerId: String
          _tmpSellerId = _stmt.getText(_columnIndexOfSellerId)
          val _tmpProductId: String
          _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          val _tmpQuantity: Double
          _tmpQuantity = _stmt.getDouble(_columnIndexOfQuantity)
          val _tmpUnit: String?
          if (_stmt.isNull(_columnIndexOfUnit)) {
            _tmpUnit = null
          } else {
            _tmpUnit = _stmt.getText(_columnIndexOfUnit)
          }
          val _tmpTotalAmount: Double
          _tmpTotalAmount = _stmt.getDouble(_columnIndexOfTotalAmount)
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpShippingAddress: String
          _tmpShippingAddress = _stmt.getText(_columnIndexOfShippingAddress)
          val _tmpPaymentMethod: String?
          if (_stmt.isNull(_columnIndexOfPaymentMethod)) {
            _tmpPaymentMethod = null
          } else {
            _tmpPaymentMethod = _stmt.getText(_columnIndexOfPaymentMethod)
          }
          val _tmpPaymentStatus: String
          _tmpPaymentStatus = _stmt.getText(_columnIndexOfPaymentStatus)
          val _tmpOrderDate: Long
          _tmpOrderDate = _stmt.getLong(_columnIndexOfOrderDate)
          val _tmpExpectedDeliveryDate: Long?
          if (_stmt.isNull(_columnIndexOfExpectedDeliveryDate)) {
            _tmpExpectedDeliveryDate = null
          } else {
            _tmpExpectedDeliveryDate = _stmt.getLong(_columnIndexOfExpectedDeliveryDate)
          }
          val _tmpActualDeliveryDate: Long?
          if (_stmt.isNull(_columnIndexOfActualDeliveryDate)) {
            _tmpActualDeliveryDate = null
          } else {
            _tmpActualDeliveryDate = _stmt.getLong(_columnIndexOfActualDeliveryDate)
          }
          val _tmpNotes: String?
          if (_stmt.isNull(_columnIndexOfNotes)) {
            _tmpNotes = null
          } else {
            _tmpNotes = _stmt.getText(_columnIndexOfNotes)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
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
          val _tmpDeliveryMethod: String?
          if (_stmt.isNull(_columnIndexOfDeliveryMethod)) {
            _tmpDeliveryMethod = null
          } else {
            _tmpDeliveryMethod = _stmt.getText(_columnIndexOfDeliveryMethod)
          }
          val _tmpDeliveryType: String?
          if (_stmt.isNull(_columnIndexOfDeliveryType)) {
            _tmpDeliveryType = null
          } else {
            _tmpDeliveryType = _stmt.getText(_columnIndexOfDeliveryType)
          }
          val _tmpDeliveryAddressJson: String?
          if (_stmt.isNull(_columnIndexOfDeliveryAddressJson)) {
            _tmpDeliveryAddressJson = null
          } else {
            _tmpDeliveryAddressJson = _stmt.getText(_columnIndexOfDeliveryAddressJson)
          }
          val _tmpNegotiationStatus: String?
          if (_stmt.isNull(_columnIndexOfNegotiationStatus)) {
            _tmpNegotiationStatus = null
          } else {
            _tmpNegotiationStatus = _stmt.getText(_columnIndexOfNegotiationStatus)
          }
          val _tmpNegotiatedPrice: Double?
          if (_stmt.isNull(_columnIndexOfNegotiatedPrice)) {
            _tmpNegotiatedPrice = null
          } else {
            _tmpNegotiatedPrice = _stmt.getDouble(_columnIndexOfNegotiatedPrice)
          }
          val _tmpOriginalPrice: Double?
          if (_stmt.isNull(_columnIndexOfOriginalPrice)) {
            _tmpOriginalPrice = null
          } else {
            _tmpOriginalPrice = _stmt.getDouble(_columnIndexOfOriginalPrice)
          }
          val _tmpCancellationReason: String?
          if (_stmt.isNull(_columnIndexOfCancellationReason)) {
            _tmpCancellationReason = null
          } else {
            _tmpCancellationReason = _stmt.getText(_columnIndexOfCancellationReason)
          }
          val _tmpCancellationTime: Long?
          if (_stmt.isNull(_columnIndexOfCancellationTime)) {
            _tmpCancellationTime = null
          } else {
            _tmpCancellationTime = _stmt.getLong(_columnIndexOfCancellationTime)
          }
          val _tmpBillImageUri: String?
          if (_stmt.isNull(_columnIndexOfBillImageUri)) {
            _tmpBillImageUri = null
          } else {
            _tmpBillImageUri = _stmt.getText(_columnIndexOfBillImageUri)
          }
          val _tmpPaymentSlipUri: String?
          if (_stmt.isNull(_columnIndexOfPaymentSlipUri)) {
            _tmpPaymentSlipUri = null
          } else {
            _tmpPaymentSlipUri = _stmt.getText(_columnIndexOfPaymentSlipUri)
          }
          val _tmpOtp: String?
          if (_stmt.isNull(_columnIndexOfOtp)) {
            _tmpOtp = null
          } else {
            _tmpOtp = _stmt.getText(_columnIndexOfOtp)
          }
          val _tmpOtpEntered: String?
          if (_stmt.isNull(_columnIndexOfOtpEntered)) {
            _tmpOtpEntered = null
          } else {
            _tmpOtpEntered = _stmt.getText(_columnIndexOfOtpEntered)
          }
          val _tmpIsVerified: Boolean
          val _tmp_2: Int
          _tmp_2 = _stmt.getLong(_columnIndexOfIsVerified).toInt()
          _tmpIsVerified = _tmp_2 != 0
          _item =
              OrderEntity(_tmpOrderId,_tmpBuyerId,_tmpSellerId,_tmpProductId,_tmpQuantity,_tmpUnit,_tmpTotalAmount,_tmpStatus,_tmpShippingAddress,_tmpPaymentMethod,_tmpPaymentStatus,_tmpOrderDate,_tmpExpectedDeliveryDate,_tmpActualDeliveryDate,_tmpNotes,_tmpCreatedAt,_tmpUpdatedAt,_tmpLastModifiedAt,_tmpIsDeleted,_tmpDeletedAt,_tmpDirty,_tmpDeliveryMethod,_tmpDeliveryType,_tmpDeliveryAddressJson,_tmpNegotiationStatus,_tmpNegotiatedPrice,_tmpOriginalPrice,_tmpCancellationReason,_tmpCancellationTime,_tmpBillImageUri,_tmpPaymentSlipUri,_tmpOtp,_tmpOtpEntered,_tmpIsVerified)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getAllOrdersSnapshot(limit: Int): List<OrderEntity> {
    val _sql: String = "SELECT * FROM orders ORDER BY orderDate DESC LIMIT ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, limit.toLong())
        val _columnIndexOfOrderId: Int = getColumnIndexOrThrow(_stmt, "orderId")
        val _columnIndexOfBuyerId: Int = getColumnIndexOrThrow(_stmt, "buyerId")
        val _columnIndexOfSellerId: Int = getColumnIndexOrThrow(_stmt, "sellerId")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfQuantity: Int = getColumnIndexOrThrow(_stmt, "quantity")
        val _columnIndexOfUnit: Int = getColumnIndexOrThrow(_stmt, "unit")
        val _columnIndexOfTotalAmount: Int = getColumnIndexOrThrow(_stmt, "totalAmount")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfShippingAddress: Int = getColumnIndexOrThrow(_stmt, "shippingAddress")
        val _columnIndexOfPaymentMethod: Int = getColumnIndexOrThrow(_stmt, "paymentMethod")
        val _columnIndexOfPaymentStatus: Int = getColumnIndexOrThrow(_stmt, "paymentStatus")
        val _columnIndexOfOrderDate: Int = getColumnIndexOrThrow(_stmt, "orderDate")
        val _columnIndexOfExpectedDeliveryDate: Int = getColumnIndexOrThrow(_stmt,
            "expectedDeliveryDate")
        val _columnIndexOfActualDeliveryDate: Int = getColumnIndexOrThrow(_stmt,
            "actualDeliveryDate")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfLastModifiedAt: Int = getColumnIndexOrThrow(_stmt, "lastModifiedAt")
        val _columnIndexOfIsDeleted: Int = getColumnIndexOrThrow(_stmt, "isDeleted")
        val _columnIndexOfDeletedAt: Int = getColumnIndexOrThrow(_stmt, "deletedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfDeliveryMethod: Int = getColumnIndexOrThrow(_stmt, "deliveryMethod")
        val _columnIndexOfDeliveryType: Int = getColumnIndexOrThrow(_stmt, "deliveryType")
        val _columnIndexOfDeliveryAddressJson: Int = getColumnIndexOrThrow(_stmt,
            "deliveryAddressJson")
        val _columnIndexOfNegotiationStatus: Int = getColumnIndexOrThrow(_stmt, "negotiationStatus")
        val _columnIndexOfNegotiatedPrice: Int = getColumnIndexOrThrow(_stmt, "negotiatedPrice")
        val _columnIndexOfOriginalPrice: Int = getColumnIndexOrThrow(_stmt, "originalPrice")
        val _columnIndexOfCancellationReason: Int = getColumnIndexOrThrow(_stmt,
            "cancellationReason")
        val _columnIndexOfCancellationTime: Int = getColumnIndexOrThrow(_stmt, "cancellationTime")
        val _columnIndexOfBillImageUri: Int = getColumnIndexOrThrow(_stmt, "billImageUri")
        val _columnIndexOfPaymentSlipUri: Int = getColumnIndexOrThrow(_stmt, "paymentSlipUri")
        val _columnIndexOfOtp: Int = getColumnIndexOrThrow(_stmt, "otp")
        val _columnIndexOfOtpEntered: Int = getColumnIndexOrThrow(_stmt, "otpEntered")
        val _columnIndexOfIsVerified: Int = getColumnIndexOrThrow(_stmt, "isVerified")
        val _result: MutableList<OrderEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: OrderEntity
          val _tmpOrderId: String
          _tmpOrderId = _stmt.getText(_columnIndexOfOrderId)
          val _tmpBuyerId: String?
          if (_stmt.isNull(_columnIndexOfBuyerId)) {
            _tmpBuyerId = null
          } else {
            _tmpBuyerId = _stmt.getText(_columnIndexOfBuyerId)
          }
          val _tmpSellerId: String
          _tmpSellerId = _stmt.getText(_columnIndexOfSellerId)
          val _tmpProductId: String
          _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          val _tmpQuantity: Double
          _tmpQuantity = _stmt.getDouble(_columnIndexOfQuantity)
          val _tmpUnit: String?
          if (_stmt.isNull(_columnIndexOfUnit)) {
            _tmpUnit = null
          } else {
            _tmpUnit = _stmt.getText(_columnIndexOfUnit)
          }
          val _tmpTotalAmount: Double
          _tmpTotalAmount = _stmt.getDouble(_columnIndexOfTotalAmount)
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpShippingAddress: String
          _tmpShippingAddress = _stmt.getText(_columnIndexOfShippingAddress)
          val _tmpPaymentMethod: String?
          if (_stmt.isNull(_columnIndexOfPaymentMethod)) {
            _tmpPaymentMethod = null
          } else {
            _tmpPaymentMethod = _stmt.getText(_columnIndexOfPaymentMethod)
          }
          val _tmpPaymentStatus: String
          _tmpPaymentStatus = _stmt.getText(_columnIndexOfPaymentStatus)
          val _tmpOrderDate: Long
          _tmpOrderDate = _stmt.getLong(_columnIndexOfOrderDate)
          val _tmpExpectedDeliveryDate: Long?
          if (_stmt.isNull(_columnIndexOfExpectedDeliveryDate)) {
            _tmpExpectedDeliveryDate = null
          } else {
            _tmpExpectedDeliveryDate = _stmt.getLong(_columnIndexOfExpectedDeliveryDate)
          }
          val _tmpActualDeliveryDate: Long?
          if (_stmt.isNull(_columnIndexOfActualDeliveryDate)) {
            _tmpActualDeliveryDate = null
          } else {
            _tmpActualDeliveryDate = _stmt.getLong(_columnIndexOfActualDeliveryDate)
          }
          val _tmpNotes: String?
          if (_stmt.isNull(_columnIndexOfNotes)) {
            _tmpNotes = null
          } else {
            _tmpNotes = _stmt.getText(_columnIndexOfNotes)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
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
          val _tmpDeliveryMethod: String?
          if (_stmt.isNull(_columnIndexOfDeliveryMethod)) {
            _tmpDeliveryMethod = null
          } else {
            _tmpDeliveryMethod = _stmt.getText(_columnIndexOfDeliveryMethod)
          }
          val _tmpDeliveryType: String?
          if (_stmt.isNull(_columnIndexOfDeliveryType)) {
            _tmpDeliveryType = null
          } else {
            _tmpDeliveryType = _stmt.getText(_columnIndexOfDeliveryType)
          }
          val _tmpDeliveryAddressJson: String?
          if (_stmt.isNull(_columnIndexOfDeliveryAddressJson)) {
            _tmpDeliveryAddressJson = null
          } else {
            _tmpDeliveryAddressJson = _stmt.getText(_columnIndexOfDeliveryAddressJson)
          }
          val _tmpNegotiationStatus: String?
          if (_stmt.isNull(_columnIndexOfNegotiationStatus)) {
            _tmpNegotiationStatus = null
          } else {
            _tmpNegotiationStatus = _stmt.getText(_columnIndexOfNegotiationStatus)
          }
          val _tmpNegotiatedPrice: Double?
          if (_stmt.isNull(_columnIndexOfNegotiatedPrice)) {
            _tmpNegotiatedPrice = null
          } else {
            _tmpNegotiatedPrice = _stmt.getDouble(_columnIndexOfNegotiatedPrice)
          }
          val _tmpOriginalPrice: Double?
          if (_stmt.isNull(_columnIndexOfOriginalPrice)) {
            _tmpOriginalPrice = null
          } else {
            _tmpOriginalPrice = _stmt.getDouble(_columnIndexOfOriginalPrice)
          }
          val _tmpCancellationReason: String?
          if (_stmt.isNull(_columnIndexOfCancellationReason)) {
            _tmpCancellationReason = null
          } else {
            _tmpCancellationReason = _stmt.getText(_columnIndexOfCancellationReason)
          }
          val _tmpCancellationTime: Long?
          if (_stmt.isNull(_columnIndexOfCancellationTime)) {
            _tmpCancellationTime = null
          } else {
            _tmpCancellationTime = _stmt.getLong(_columnIndexOfCancellationTime)
          }
          val _tmpBillImageUri: String?
          if (_stmt.isNull(_columnIndexOfBillImageUri)) {
            _tmpBillImageUri = null
          } else {
            _tmpBillImageUri = _stmt.getText(_columnIndexOfBillImageUri)
          }
          val _tmpPaymentSlipUri: String?
          if (_stmt.isNull(_columnIndexOfPaymentSlipUri)) {
            _tmpPaymentSlipUri = null
          } else {
            _tmpPaymentSlipUri = _stmt.getText(_columnIndexOfPaymentSlipUri)
          }
          val _tmpOtp: String?
          if (_stmt.isNull(_columnIndexOfOtp)) {
            _tmpOtp = null
          } else {
            _tmpOtp = _stmt.getText(_columnIndexOfOtp)
          }
          val _tmpOtpEntered: String?
          if (_stmt.isNull(_columnIndexOfOtpEntered)) {
            _tmpOtpEntered = null
          } else {
            _tmpOtpEntered = _stmt.getText(_columnIndexOfOtpEntered)
          }
          val _tmpIsVerified: Boolean
          val _tmp_2: Int
          _tmp_2 = _stmt.getLong(_columnIndexOfIsVerified).toInt()
          _tmpIsVerified = _tmp_2 != 0
          _item =
              OrderEntity(_tmpOrderId,_tmpBuyerId,_tmpSellerId,_tmpProductId,_tmpQuantity,_tmpUnit,_tmpTotalAmount,_tmpStatus,_tmpShippingAddress,_tmpPaymentMethod,_tmpPaymentStatus,_tmpOrderDate,_tmpExpectedDeliveryDate,_tmpActualDeliveryDate,_tmpNotes,_tmpCreatedAt,_tmpUpdatedAt,_tmpLastModifiedAt,_tmpIsDeleted,_tmpDeletedAt,_tmpDirty,_tmpDeliveryMethod,_tmpDeliveryType,_tmpDeliveryAddressJson,_tmpNegotiationStatus,_tmpNegotiatedPrice,_tmpOriginalPrice,_tmpCancellationReason,_tmpCancellationTime,_tmpBillImageUri,_tmpPaymentSlipUri,_tmpOtp,_tmpOtpEntered,_tmpIsVerified)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getDirty(): List<OrderEntity> {
    val _sql: String = "SELECT * FROM orders WHERE dirty = 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfOrderId: Int = getColumnIndexOrThrow(_stmt, "orderId")
        val _columnIndexOfBuyerId: Int = getColumnIndexOrThrow(_stmt, "buyerId")
        val _columnIndexOfSellerId: Int = getColumnIndexOrThrow(_stmt, "sellerId")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfQuantity: Int = getColumnIndexOrThrow(_stmt, "quantity")
        val _columnIndexOfUnit: Int = getColumnIndexOrThrow(_stmt, "unit")
        val _columnIndexOfTotalAmount: Int = getColumnIndexOrThrow(_stmt, "totalAmount")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfShippingAddress: Int = getColumnIndexOrThrow(_stmt, "shippingAddress")
        val _columnIndexOfPaymentMethod: Int = getColumnIndexOrThrow(_stmt, "paymentMethod")
        val _columnIndexOfPaymentStatus: Int = getColumnIndexOrThrow(_stmt, "paymentStatus")
        val _columnIndexOfOrderDate: Int = getColumnIndexOrThrow(_stmt, "orderDate")
        val _columnIndexOfExpectedDeliveryDate: Int = getColumnIndexOrThrow(_stmt,
            "expectedDeliveryDate")
        val _columnIndexOfActualDeliveryDate: Int = getColumnIndexOrThrow(_stmt,
            "actualDeliveryDate")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfLastModifiedAt: Int = getColumnIndexOrThrow(_stmt, "lastModifiedAt")
        val _columnIndexOfIsDeleted: Int = getColumnIndexOrThrow(_stmt, "isDeleted")
        val _columnIndexOfDeletedAt: Int = getColumnIndexOrThrow(_stmt, "deletedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfDeliveryMethod: Int = getColumnIndexOrThrow(_stmt, "deliveryMethod")
        val _columnIndexOfDeliveryType: Int = getColumnIndexOrThrow(_stmt, "deliveryType")
        val _columnIndexOfDeliveryAddressJson: Int = getColumnIndexOrThrow(_stmt,
            "deliveryAddressJson")
        val _columnIndexOfNegotiationStatus: Int = getColumnIndexOrThrow(_stmt, "negotiationStatus")
        val _columnIndexOfNegotiatedPrice: Int = getColumnIndexOrThrow(_stmt, "negotiatedPrice")
        val _columnIndexOfOriginalPrice: Int = getColumnIndexOrThrow(_stmt, "originalPrice")
        val _columnIndexOfCancellationReason: Int = getColumnIndexOrThrow(_stmt,
            "cancellationReason")
        val _columnIndexOfCancellationTime: Int = getColumnIndexOrThrow(_stmt, "cancellationTime")
        val _columnIndexOfBillImageUri: Int = getColumnIndexOrThrow(_stmt, "billImageUri")
        val _columnIndexOfPaymentSlipUri: Int = getColumnIndexOrThrow(_stmt, "paymentSlipUri")
        val _columnIndexOfOtp: Int = getColumnIndexOrThrow(_stmt, "otp")
        val _columnIndexOfOtpEntered: Int = getColumnIndexOrThrow(_stmt, "otpEntered")
        val _columnIndexOfIsVerified: Int = getColumnIndexOrThrow(_stmt, "isVerified")
        val _result: MutableList<OrderEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: OrderEntity
          val _tmpOrderId: String
          _tmpOrderId = _stmt.getText(_columnIndexOfOrderId)
          val _tmpBuyerId: String?
          if (_stmt.isNull(_columnIndexOfBuyerId)) {
            _tmpBuyerId = null
          } else {
            _tmpBuyerId = _stmt.getText(_columnIndexOfBuyerId)
          }
          val _tmpSellerId: String
          _tmpSellerId = _stmt.getText(_columnIndexOfSellerId)
          val _tmpProductId: String
          _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          val _tmpQuantity: Double
          _tmpQuantity = _stmt.getDouble(_columnIndexOfQuantity)
          val _tmpUnit: String?
          if (_stmt.isNull(_columnIndexOfUnit)) {
            _tmpUnit = null
          } else {
            _tmpUnit = _stmt.getText(_columnIndexOfUnit)
          }
          val _tmpTotalAmount: Double
          _tmpTotalAmount = _stmt.getDouble(_columnIndexOfTotalAmount)
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpShippingAddress: String
          _tmpShippingAddress = _stmt.getText(_columnIndexOfShippingAddress)
          val _tmpPaymentMethod: String?
          if (_stmt.isNull(_columnIndexOfPaymentMethod)) {
            _tmpPaymentMethod = null
          } else {
            _tmpPaymentMethod = _stmt.getText(_columnIndexOfPaymentMethod)
          }
          val _tmpPaymentStatus: String
          _tmpPaymentStatus = _stmt.getText(_columnIndexOfPaymentStatus)
          val _tmpOrderDate: Long
          _tmpOrderDate = _stmt.getLong(_columnIndexOfOrderDate)
          val _tmpExpectedDeliveryDate: Long?
          if (_stmt.isNull(_columnIndexOfExpectedDeliveryDate)) {
            _tmpExpectedDeliveryDate = null
          } else {
            _tmpExpectedDeliveryDate = _stmt.getLong(_columnIndexOfExpectedDeliveryDate)
          }
          val _tmpActualDeliveryDate: Long?
          if (_stmt.isNull(_columnIndexOfActualDeliveryDate)) {
            _tmpActualDeliveryDate = null
          } else {
            _tmpActualDeliveryDate = _stmt.getLong(_columnIndexOfActualDeliveryDate)
          }
          val _tmpNotes: String?
          if (_stmt.isNull(_columnIndexOfNotes)) {
            _tmpNotes = null
          } else {
            _tmpNotes = _stmt.getText(_columnIndexOfNotes)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
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
          val _tmpDeliveryMethod: String?
          if (_stmt.isNull(_columnIndexOfDeliveryMethod)) {
            _tmpDeliveryMethod = null
          } else {
            _tmpDeliveryMethod = _stmt.getText(_columnIndexOfDeliveryMethod)
          }
          val _tmpDeliveryType: String?
          if (_stmt.isNull(_columnIndexOfDeliveryType)) {
            _tmpDeliveryType = null
          } else {
            _tmpDeliveryType = _stmt.getText(_columnIndexOfDeliveryType)
          }
          val _tmpDeliveryAddressJson: String?
          if (_stmt.isNull(_columnIndexOfDeliveryAddressJson)) {
            _tmpDeliveryAddressJson = null
          } else {
            _tmpDeliveryAddressJson = _stmt.getText(_columnIndexOfDeliveryAddressJson)
          }
          val _tmpNegotiationStatus: String?
          if (_stmt.isNull(_columnIndexOfNegotiationStatus)) {
            _tmpNegotiationStatus = null
          } else {
            _tmpNegotiationStatus = _stmt.getText(_columnIndexOfNegotiationStatus)
          }
          val _tmpNegotiatedPrice: Double?
          if (_stmt.isNull(_columnIndexOfNegotiatedPrice)) {
            _tmpNegotiatedPrice = null
          } else {
            _tmpNegotiatedPrice = _stmt.getDouble(_columnIndexOfNegotiatedPrice)
          }
          val _tmpOriginalPrice: Double?
          if (_stmt.isNull(_columnIndexOfOriginalPrice)) {
            _tmpOriginalPrice = null
          } else {
            _tmpOriginalPrice = _stmt.getDouble(_columnIndexOfOriginalPrice)
          }
          val _tmpCancellationReason: String?
          if (_stmt.isNull(_columnIndexOfCancellationReason)) {
            _tmpCancellationReason = null
          } else {
            _tmpCancellationReason = _stmt.getText(_columnIndexOfCancellationReason)
          }
          val _tmpCancellationTime: Long?
          if (_stmt.isNull(_columnIndexOfCancellationTime)) {
            _tmpCancellationTime = null
          } else {
            _tmpCancellationTime = _stmt.getLong(_columnIndexOfCancellationTime)
          }
          val _tmpBillImageUri: String?
          if (_stmt.isNull(_columnIndexOfBillImageUri)) {
            _tmpBillImageUri = null
          } else {
            _tmpBillImageUri = _stmt.getText(_columnIndexOfBillImageUri)
          }
          val _tmpPaymentSlipUri: String?
          if (_stmt.isNull(_columnIndexOfPaymentSlipUri)) {
            _tmpPaymentSlipUri = null
          } else {
            _tmpPaymentSlipUri = _stmt.getText(_columnIndexOfPaymentSlipUri)
          }
          val _tmpOtp: String?
          if (_stmt.isNull(_columnIndexOfOtp)) {
            _tmpOtp = null
          } else {
            _tmpOtp = _stmt.getText(_columnIndexOfOtp)
          }
          val _tmpOtpEntered: String?
          if (_stmt.isNull(_columnIndexOfOtpEntered)) {
            _tmpOtpEntered = null
          } else {
            _tmpOtpEntered = _stmt.getText(_columnIndexOfOtpEntered)
          }
          val _tmpIsVerified: Boolean
          val _tmp_2: Int
          _tmp_2 = _stmt.getLong(_columnIndexOfIsVerified).toInt()
          _tmpIsVerified = _tmp_2 != 0
          _item =
              OrderEntity(_tmpOrderId,_tmpBuyerId,_tmpSellerId,_tmpProductId,_tmpQuantity,_tmpUnit,_tmpTotalAmount,_tmpStatus,_tmpShippingAddress,_tmpPaymentMethod,_tmpPaymentStatus,_tmpOrderDate,_tmpExpectedDeliveryDate,_tmpActualDeliveryDate,_tmpNotes,_tmpCreatedAt,_tmpUpdatedAt,_tmpLastModifiedAt,_tmpIsDeleted,_tmpDeletedAt,_tmpDirty,_tmpDeliveryMethod,_tmpDeliveryType,_tmpDeliveryAddressJson,_tmpNegotiationStatus,_tmpNegotiatedPrice,_tmpOriginalPrice,_tmpCancellationReason,_tmpCancellationTime,_tmpBillImageUri,_tmpPaymentSlipUri,_tmpOtp,_tmpOtpEntered,_tmpIsVerified)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun countAllOrders(): Int {
    val _sql: String = "SELECT COUNT(*) FROM orders"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
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

  public override suspend fun countCompletedOrders(): Int {
    val _sql: String = "SELECT COUNT(*) FROM orders WHERE status = 'DELIVERED'"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
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

  public override suspend fun countPendingOrders(): Int {
    val _sql: String =
        "SELECT COUNT(*) FROM orders WHERE status IN ('PENDING_PAYMENT', 'PLACED', 'CONFIRMED', 'PROCESSING')"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
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

  public override suspend fun countOrdersSince(since: Long): Int {
    val _sql: String = "SELECT COUNT(*) FROM orders WHERE orderDate >= ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
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

  public override suspend fun getTotalRevenue(): Double? {
    val _sql: String = "SELECT SUM(totalAmount) FROM orders WHERE status = 'DELIVERED'"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _result: Double?
        if (_stmt.step()) {
          val _tmp: Double?
          if (_stmt.isNull(0)) {
            _tmp = null
          } else {
            _tmp = _stmt.getDouble(0)
          }
          _result = _tmp
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getRevenueSince(since: Long): Double? {
    val _sql: String =
        "SELECT SUM(totalAmount) FROM orders WHERE status = 'DELIVERED' AND orderDate >= ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, since)
        val _result: Double?
        if (_stmt.step()) {
          val _tmp: Double?
          if (_stmt.isNull(0)) {
            _tmp = null
          } else {
            _tmp = _stmt.getDouble(0)
          }
          _result = _tmp
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getAllOrderValues(): List<OrderValueTuple> {
    val _sql: String =
        "SELECT productId as id, totalAmount as value FROM orders WHERE status = 'DELIVERED'"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = 0
        val _columnIndexOfValue: Int = 1
        val _result: MutableList<OrderValueTuple> = mutableListOf()
        while (_stmt.step()) {
          val _item: OrderValueTuple
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpValue: Double
          _tmpValue = _stmt.getDouble(_columnIndexOfValue)
          _item = OrderValueTuple(_tmpId,_tmpValue)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getAllSellerValues(): List<OrderValueTuple> {
    val _sql: String =
        "SELECT sellerId as id, totalAmount as value FROM orders WHERE status = 'DELIVERED'"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = 0
        val _columnIndexOfValue: Int = 1
        val _result: MutableList<OrderValueTuple> = mutableListOf()
        while (_stmt.step()) {
          val _item: OrderValueTuple
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpValue: Double
          _tmpValue = _stmt.getDouble(_columnIndexOfValue)
          _item = OrderValueTuple(_tmpId,_tmpValue)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun deleteOrderById(orderId: String) {
    val _sql: String = "DELETE FROM orders WHERE orderId = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, orderId)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun deleteAllOrders() {
    val _sql: String = "DELETE FROM orders"
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
    val _sql: String = "DELETE FROM orders WHERE isDeleted = 1"
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
