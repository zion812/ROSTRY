package com.rio.rostry.`data`.database.dao

import androidx.room.EntityDeleteOrUpdateAdapter
import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.rio.rostry.`data`.database.AppDatabase
import com.rio.rostry.`data`.database.entity.MarketListingEntity
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
public class MarketListingDao_Impl(
  __db: RoomDatabase,
) : MarketListingDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfMarketListingEntity: EntityInsertAdapter<MarketListingEntity>

  private val __updateAdapterOfMarketListingEntity: EntityDeleteOrUpdateAdapter<MarketListingEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfMarketListingEntity = object : EntityInsertAdapter<MarketListingEntity>()
        {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `market_listings` (`listingId`,`sellerId`,`inventoryId`,`title`,`description`,`price`,`currency`,`priceUnit`,`category`,`tags`,`deliveryOptions`,`deliveryCost`,`locationName`,`latitude`,`longitude`,`minOrderQuantity`,`maxOrderQuantity`,`imageUrls`,`status`,`isActive`,`viewsCount`,`inquiriesCount`,`leadTimeDays`,`createdAt`,`updatedAt`,`expiresAt`,`dirty`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: MarketListingEntity) {
        statement.bindText(1, entity.listingId)
        statement.bindText(2, entity.sellerId)
        statement.bindText(3, entity.inventoryId)
        statement.bindText(4, entity.title)
        statement.bindText(5, entity.description)
        statement.bindDouble(6, entity.price)
        statement.bindText(7, entity.currency)
        statement.bindText(8, entity.priceUnit)
        statement.bindText(9, entity.category)
        val _tmp: String? = AppDatabase.Converters.fromStringList(entity.tags)
        if (_tmp == null) {
          statement.bindNull(10)
        } else {
          statement.bindText(10, _tmp)
        }
        val _tmp_1: String? = AppDatabase.Converters.fromStringList(entity.deliveryOptions)
        if (_tmp_1 == null) {
          statement.bindNull(11)
        } else {
          statement.bindText(11, _tmp_1)
        }
        val _tmpDeliveryCost: Double? = entity.deliveryCost
        if (_tmpDeliveryCost == null) {
          statement.bindNull(12)
        } else {
          statement.bindDouble(12, _tmpDeliveryCost)
        }
        val _tmpLocationName: String? = entity.locationName
        if (_tmpLocationName == null) {
          statement.bindNull(13)
        } else {
          statement.bindText(13, _tmpLocationName)
        }
        val _tmpLatitude: Double? = entity.latitude
        if (_tmpLatitude == null) {
          statement.bindNull(14)
        } else {
          statement.bindDouble(14, _tmpLatitude)
        }
        val _tmpLongitude: Double? = entity.longitude
        if (_tmpLongitude == null) {
          statement.bindNull(15)
        } else {
          statement.bindDouble(15, _tmpLongitude)
        }
        statement.bindDouble(16, entity.minOrderQuantity)
        val _tmpMaxOrderQuantity: Double? = entity.maxOrderQuantity
        if (_tmpMaxOrderQuantity == null) {
          statement.bindNull(17)
        } else {
          statement.bindDouble(17, _tmpMaxOrderQuantity)
        }
        val _tmp_2: String? = AppDatabase.Converters.fromStringList(entity.imageUrls)
        if (_tmp_2 == null) {
          statement.bindNull(18)
        } else {
          statement.bindText(18, _tmp_2)
        }
        statement.bindText(19, entity.status)
        val _tmp_3: Int = if (entity.isActive) 1 else 0
        statement.bindLong(20, _tmp_3.toLong())
        statement.bindLong(21, entity.viewsCount.toLong())
        statement.bindLong(22, entity.inquiriesCount.toLong())
        statement.bindLong(23, entity.leadTimeDays.toLong())
        statement.bindLong(24, entity.createdAt)
        statement.bindLong(25, entity.updatedAt)
        val _tmpExpiresAt: Long? = entity.expiresAt
        if (_tmpExpiresAt == null) {
          statement.bindNull(26)
        } else {
          statement.bindLong(26, _tmpExpiresAt)
        }
        val _tmp_4: Int = if (entity.dirty) 1 else 0
        statement.bindLong(27, _tmp_4.toLong())
      }
    }
    this.__updateAdapterOfMarketListingEntity = object :
        EntityDeleteOrUpdateAdapter<MarketListingEntity>() {
      protected override fun createQuery(): String =
          "UPDATE OR ABORT `market_listings` SET `listingId` = ?,`sellerId` = ?,`inventoryId` = ?,`title` = ?,`description` = ?,`price` = ?,`currency` = ?,`priceUnit` = ?,`category` = ?,`tags` = ?,`deliveryOptions` = ?,`deliveryCost` = ?,`locationName` = ?,`latitude` = ?,`longitude` = ?,`minOrderQuantity` = ?,`maxOrderQuantity` = ?,`imageUrls` = ?,`status` = ?,`isActive` = ?,`viewsCount` = ?,`inquiriesCount` = ?,`leadTimeDays` = ?,`createdAt` = ?,`updatedAt` = ?,`expiresAt` = ?,`dirty` = ? WHERE `listingId` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: MarketListingEntity) {
        statement.bindText(1, entity.listingId)
        statement.bindText(2, entity.sellerId)
        statement.bindText(3, entity.inventoryId)
        statement.bindText(4, entity.title)
        statement.bindText(5, entity.description)
        statement.bindDouble(6, entity.price)
        statement.bindText(7, entity.currency)
        statement.bindText(8, entity.priceUnit)
        statement.bindText(9, entity.category)
        val _tmp: String? = AppDatabase.Converters.fromStringList(entity.tags)
        if (_tmp == null) {
          statement.bindNull(10)
        } else {
          statement.bindText(10, _tmp)
        }
        val _tmp_1: String? = AppDatabase.Converters.fromStringList(entity.deliveryOptions)
        if (_tmp_1 == null) {
          statement.bindNull(11)
        } else {
          statement.bindText(11, _tmp_1)
        }
        val _tmpDeliveryCost: Double? = entity.deliveryCost
        if (_tmpDeliveryCost == null) {
          statement.bindNull(12)
        } else {
          statement.bindDouble(12, _tmpDeliveryCost)
        }
        val _tmpLocationName: String? = entity.locationName
        if (_tmpLocationName == null) {
          statement.bindNull(13)
        } else {
          statement.bindText(13, _tmpLocationName)
        }
        val _tmpLatitude: Double? = entity.latitude
        if (_tmpLatitude == null) {
          statement.bindNull(14)
        } else {
          statement.bindDouble(14, _tmpLatitude)
        }
        val _tmpLongitude: Double? = entity.longitude
        if (_tmpLongitude == null) {
          statement.bindNull(15)
        } else {
          statement.bindDouble(15, _tmpLongitude)
        }
        statement.bindDouble(16, entity.minOrderQuantity)
        val _tmpMaxOrderQuantity: Double? = entity.maxOrderQuantity
        if (_tmpMaxOrderQuantity == null) {
          statement.bindNull(17)
        } else {
          statement.bindDouble(17, _tmpMaxOrderQuantity)
        }
        val _tmp_2: String? = AppDatabase.Converters.fromStringList(entity.imageUrls)
        if (_tmp_2 == null) {
          statement.bindNull(18)
        } else {
          statement.bindText(18, _tmp_2)
        }
        statement.bindText(19, entity.status)
        val _tmp_3: Int = if (entity.isActive) 1 else 0
        statement.bindLong(20, _tmp_3.toLong())
        statement.bindLong(21, entity.viewsCount.toLong())
        statement.bindLong(22, entity.inquiriesCount.toLong())
        statement.bindLong(23, entity.leadTimeDays.toLong())
        statement.bindLong(24, entity.createdAt)
        statement.bindLong(25, entity.updatedAt)
        val _tmpExpiresAt: Long? = entity.expiresAt
        if (_tmpExpiresAt == null) {
          statement.bindNull(26)
        } else {
          statement.bindLong(26, _tmpExpiresAt)
        }
        val _tmp_4: Int = if (entity.dirty) 1 else 0
        statement.bindLong(27, _tmp_4.toLong())
        statement.bindText(28, entity.listingId)
      }
    }
  }

  public override suspend fun upsert(listing: MarketListingEntity): Unit = performSuspending(__db,
      false, true) { _connection ->
    __insertAdapterOfMarketListingEntity.insert(_connection, listing)
  }

  public override suspend fun updateListing(listing: MarketListingEntity): Unit =
      performSuspending(__db, false, true) { _connection ->
    __updateAdapterOfMarketListingEntity.handle(_connection, listing)
  }

  public override fun getListingById(id: String): Flow<MarketListingEntity?> {
    val _sql: String = "SELECT * FROM market_listings WHERE listingId = ?"
    return createFlow(__db, false, arrayOf("market_listings")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, id)
        val _columnIndexOfListingId: Int = getColumnIndexOrThrow(_stmt, "listingId")
        val _columnIndexOfSellerId: Int = getColumnIndexOrThrow(_stmt, "sellerId")
        val _columnIndexOfInventoryId: Int = getColumnIndexOrThrow(_stmt, "inventoryId")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfPrice: Int = getColumnIndexOrThrow(_stmt, "price")
        val _columnIndexOfCurrency: Int = getColumnIndexOrThrow(_stmt, "currency")
        val _columnIndexOfPriceUnit: Int = getColumnIndexOrThrow(_stmt, "priceUnit")
        val _columnIndexOfCategory: Int = getColumnIndexOrThrow(_stmt, "category")
        val _columnIndexOfTags: Int = getColumnIndexOrThrow(_stmt, "tags")
        val _columnIndexOfDeliveryOptions: Int = getColumnIndexOrThrow(_stmt, "deliveryOptions")
        val _columnIndexOfDeliveryCost: Int = getColumnIndexOrThrow(_stmt, "deliveryCost")
        val _columnIndexOfLocationName: Int = getColumnIndexOrThrow(_stmt, "locationName")
        val _columnIndexOfLatitude: Int = getColumnIndexOrThrow(_stmt, "latitude")
        val _columnIndexOfLongitude: Int = getColumnIndexOrThrow(_stmt, "longitude")
        val _columnIndexOfMinOrderQuantity: Int = getColumnIndexOrThrow(_stmt, "minOrderQuantity")
        val _columnIndexOfMaxOrderQuantity: Int = getColumnIndexOrThrow(_stmt, "maxOrderQuantity")
        val _columnIndexOfImageUrls: Int = getColumnIndexOrThrow(_stmt, "imageUrls")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfIsActive: Int = getColumnIndexOrThrow(_stmt, "isActive")
        val _columnIndexOfViewsCount: Int = getColumnIndexOrThrow(_stmt, "viewsCount")
        val _columnIndexOfInquiriesCount: Int = getColumnIndexOrThrow(_stmt, "inquiriesCount")
        val _columnIndexOfLeadTimeDays: Int = getColumnIndexOrThrow(_stmt, "leadTimeDays")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfExpiresAt: Int = getColumnIndexOrThrow(_stmt, "expiresAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _result: MarketListingEntity?
        if (_stmt.step()) {
          val _tmpListingId: String
          _tmpListingId = _stmt.getText(_columnIndexOfListingId)
          val _tmpSellerId: String
          _tmpSellerId = _stmt.getText(_columnIndexOfSellerId)
          val _tmpInventoryId: String
          _tmpInventoryId = _stmt.getText(_columnIndexOfInventoryId)
          val _tmpTitle: String
          _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          val _tmpDescription: String
          _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          val _tmpPrice: Double
          _tmpPrice = _stmt.getDouble(_columnIndexOfPrice)
          val _tmpCurrency: String
          _tmpCurrency = _stmt.getText(_columnIndexOfCurrency)
          val _tmpPriceUnit: String
          _tmpPriceUnit = _stmt.getText(_columnIndexOfPriceUnit)
          val _tmpCategory: String
          _tmpCategory = _stmt.getText(_columnIndexOfCategory)
          val _tmpTags: List<String>
          val _tmp: String?
          if (_stmt.isNull(_columnIndexOfTags)) {
            _tmp = null
          } else {
            _tmp = _stmt.getText(_columnIndexOfTags)
          }
          val _tmp_1: List<String>? = AppDatabase.Converters.toStringList(_tmp)
          if (_tmp_1 == null) {
            error("Expected NON-NULL 'kotlin.collections.List<kotlin.String>', but it was NULL.")
          } else {
            _tmpTags = _tmp_1
          }
          val _tmpDeliveryOptions: List<String>
          val _tmp_2: String?
          if (_stmt.isNull(_columnIndexOfDeliveryOptions)) {
            _tmp_2 = null
          } else {
            _tmp_2 = _stmt.getText(_columnIndexOfDeliveryOptions)
          }
          val _tmp_3: List<String>? = AppDatabase.Converters.toStringList(_tmp_2)
          if (_tmp_3 == null) {
            error("Expected NON-NULL 'kotlin.collections.List<kotlin.String>', but it was NULL.")
          } else {
            _tmpDeliveryOptions = _tmp_3
          }
          val _tmpDeliveryCost: Double?
          if (_stmt.isNull(_columnIndexOfDeliveryCost)) {
            _tmpDeliveryCost = null
          } else {
            _tmpDeliveryCost = _stmt.getDouble(_columnIndexOfDeliveryCost)
          }
          val _tmpLocationName: String?
          if (_stmt.isNull(_columnIndexOfLocationName)) {
            _tmpLocationName = null
          } else {
            _tmpLocationName = _stmt.getText(_columnIndexOfLocationName)
          }
          val _tmpLatitude: Double?
          if (_stmt.isNull(_columnIndexOfLatitude)) {
            _tmpLatitude = null
          } else {
            _tmpLatitude = _stmt.getDouble(_columnIndexOfLatitude)
          }
          val _tmpLongitude: Double?
          if (_stmt.isNull(_columnIndexOfLongitude)) {
            _tmpLongitude = null
          } else {
            _tmpLongitude = _stmt.getDouble(_columnIndexOfLongitude)
          }
          val _tmpMinOrderQuantity: Double
          _tmpMinOrderQuantity = _stmt.getDouble(_columnIndexOfMinOrderQuantity)
          val _tmpMaxOrderQuantity: Double?
          if (_stmt.isNull(_columnIndexOfMaxOrderQuantity)) {
            _tmpMaxOrderQuantity = null
          } else {
            _tmpMaxOrderQuantity = _stmt.getDouble(_columnIndexOfMaxOrderQuantity)
          }
          val _tmpImageUrls: List<String>
          val _tmp_4: String?
          if (_stmt.isNull(_columnIndexOfImageUrls)) {
            _tmp_4 = null
          } else {
            _tmp_4 = _stmt.getText(_columnIndexOfImageUrls)
          }
          val _tmp_5: List<String>? = AppDatabase.Converters.toStringList(_tmp_4)
          if (_tmp_5 == null) {
            error("Expected NON-NULL 'kotlin.collections.List<kotlin.String>', but it was NULL.")
          } else {
            _tmpImageUrls = _tmp_5
          }
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpIsActive: Boolean
          val _tmp_6: Int
          _tmp_6 = _stmt.getLong(_columnIndexOfIsActive).toInt()
          _tmpIsActive = _tmp_6 != 0
          val _tmpViewsCount: Int
          _tmpViewsCount = _stmt.getLong(_columnIndexOfViewsCount).toInt()
          val _tmpInquiriesCount: Int
          _tmpInquiriesCount = _stmt.getLong(_columnIndexOfInquiriesCount).toInt()
          val _tmpLeadTimeDays: Int
          _tmpLeadTimeDays = _stmt.getLong(_columnIndexOfLeadTimeDays).toInt()
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpExpiresAt: Long?
          if (_stmt.isNull(_columnIndexOfExpiresAt)) {
            _tmpExpiresAt = null
          } else {
            _tmpExpiresAt = _stmt.getLong(_columnIndexOfExpiresAt)
          }
          val _tmpDirty: Boolean
          val _tmp_7: Int
          _tmp_7 = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp_7 != 0
          _result =
              MarketListingEntity(_tmpListingId,_tmpSellerId,_tmpInventoryId,_tmpTitle,_tmpDescription,_tmpPrice,_tmpCurrency,_tmpPriceUnit,_tmpCategory,_tmpTags,_tmpDeliveryOptions,_tmpDeliveryCost,_tmpLocationName,_tmpLatitude,_tmpLongitude,_tmpMinOrderQuantity,_tmpMaxOrderQuantity,_tmpImageUrls,_tmpStatus,_tmpIsActive,_tmpViewsCount,_tmpInquiriesCount,_tmpLeadTimeDays,_tmpCreatedAt,_tmpUpdatedAt,_tmpExpiresAt,_tmpDirty)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getListingsBySeller(sellerId: String): Flow<List<MarketListingEntity>> {
    val _sql: String = "SELECT * FROM market_listings WHERE sellerId = ? ORDER BY updatedAt DESC"
    return createFlow(__db, false, arrayOf("market_listings")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, sellerId)
        val _columnIndexOfListingId: Int = getColumnIndexOrThrow(_stmt, "listingId")
        val _columnIndexOfSellerId: Int = getColumnIndexOrThrow(_stmt, "sellerId")
        val _columnIndexOfInventoryId: Int = getColumnIndexOrThrow(_stmt, "inventoryId")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfPrice: Int = getColumnIndexOrThrow(_stmt, "price")
        val _columnIndexOfCurrency: Int = getColumnIndexOrThrow(_stmt, "currency")
        val _columnIndexOfPriceUnit: Int = getColumnIndexOrThrow(_stmt, "priceUnit")
        val _columnIndexOfCategory: Int = getColumnIndexOrThrow(_stmt, "category")
        val _columnIndexOfTags: Int = getColumnIndexOrThrow(_stmt, "tags")
        val _columnIndexOfDeliveryOptions: Int = getColumnIndexOrThrow(_stmt, "deliveryOptions")
        val _columnIndexOfDeliveryCost: Int = getColumnIndexOrThrow(_stmt, "deliveryCost")
        val _columnIndexOfLocationName: Int = getColumnIndexOrThrow(_stmt, "locationName")
        val _columnIndexOfLatitude: Int = getColumnIndexOrThrow(_stmt, "latitude")
        val _columnIndexOfLongitude: Int = getColumnIndexOrThrow(_stmt, "longitude")
        val _columnIndexOfMinOrderQuantity: Int = getColumnIndexOrThrow(_stmt, "minOrderQuantity")
        val _columnIndexOfMaxOrderQuantity: Int = getColumnIndexOrThrow(_stmt, "maxOrderQuantity")
        val _columnIndexOfImageUrls: Int = getColumnIndexOrThrow(_stmt, "imageUrls")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfIsActive: Int = getColumnIndexOrThrow(_stmt, "isActive")
        val _columnIndexOfViewsCount: Int = getColumnIndexOrThrow(_stmt, "viewsCount")
        val _columnIndexOfInquiriesCount: Int = getColumnIndexOrThrow(_stmt, "inquiriesCount")
        val _columnIndexOfLeadTimeDays: Int = getColumnIndexOrThrow(_stmt, "leadTimeDays")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfExpiresAt: Int = getColumnIndexOrThrow(_stmt, "expiresAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _result: MutableList<MarketListingEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: MarketListingEntity
          val _tmpListingId: String
          _tmpListingId = _stmt.getText(_columnIndexOfListingId)
          val _tmpSellerId: String
          _tmpSellerId = _stmt.getText(_columnIndexOfSellerId)
          val _tmpInventoryId: String
          _tmpInventoryId = _stmt.getText(_columnIndexOfInventoryId)
          val _tmpTitle: String
          _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          val _tmpDescription: String
          _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          val _tmpPrice: Double
          _tmpPrice = _stmt.getDouble(_columnIndexOfPrice)
          val _tmpCurrency: String
          _tmpCurrency = _stmt.getText(_columnIndexOfCurrency)
          val _tmpPriceUnit: String
          _tmpPriceUnit = _stmt.getText(_columnIndexOfPriceUnit)
          val _tmpCategory: String
          _tmpCategory = _stmt.getText(_columnIndexOfCategory)
          val _tmpTags: List<String>
          val _tmp: String?
          if (_stmt.isNull(_columnIndexOfTags)) {
            _tmp = null
          } else {
            _tmp = _stmt.getText(_columnIndexOfTags)
          }
          val _tmp_1: List<String>? = AppDatabase.Converters.toStringList(_tmp)
          if (_tmp_1 == null) {
            error("Expected NON-NULL 'kotlin.collections.List<kotlin.String>', but it was NULL.")
          } else {
            _tmpTags = _tmp_1
          }
          val _tmpDeliveryOptions: List<String>
          val _tmp_2: String?
          if (_stmt.isNull(_columnIndexOfDeliveryOptions)) {
            _tmp_2 = null
          } else {
            _tmp_2 = _stmt.getText(_columnIndexOfDeliveryOptions)
          }
          val _tmp_3: List<String>? = AppDatabase.Converters.toStringList(_tmp_2)
          if (_tmp_3 == null) {
            error("Expected NON-NULL 'kotlin.collections.List<kotlin.String>', but it was NULL.")
          } else {
            _tmpDeliveryOptions = _tmp_3
          }
          val _tmpDeliveryCost: Double?
          if (_stmt.isNull(_columnIndexOfDeliveryCost)) {
            _tmpDeliveryCost = null
          } else {
            _tmpDeliveryCost = _stmt.getDouble(_columnIndexOfDeliveryCost)
          }
          val _tmpLocationName: String?
          if (_stmt.isNull(_columnIndexOfLocationName)) {
            _tmpLocationName = null
          } else {
            _tmpLocationName = _stmt.getText(_columnIndexOfLocationName)
          }
          val _tmpLatitude: Double?
          if (_stmt.isNull(_columnIndexOfLatitude)) {
            _tmpLatitude = null
          } else {
            _tmpLatitude = _stmt.getDouble(_columnIndexOfLatitude)
          }
          val _tmpLongitude: Double?
          if (_stmt.isNull(_columnIndexOfLongitude)) {
            _tmpLongitude = null
          } else {
            _tmpLongitude = _stmt.getDouble(_columnIndexOfLongitude)
          }
          val _tmpMinOrderQuantity: Double
          _tmpMinOrderQuantity = _stmt.getDouble(_columnIndexOfMinOrderQuantity)
          val _tmpMaxOrderQuantity: Double?
          if (_stmt.isNull(_columnIndexOfMaxOrderQuantity)) {
            _tmpMaxOrderQuantity = null
          } else {
            _tmpMaxOrderQuantity = _stmt.getDouble(_columnIndexOfMaxOrderQuantity)
          }
          val _tmpImageUrls: List<String>
          val _tmp_4: String?
          if (_stmt.isNull(_columnIndexOfImageUrls)) {
            _tmp_4 = null
          } else {
            _tmp_4 = _stmt.getText(_columnIndexOfImageUrls)
          }
          val _tmp_5: List<String>? = AppDatabase.Converters.toStringList(_tmp_4)
          if (_tmp_5 == null) {
            error("Expected NON-NULL 'kotlin.collections.List<kotlin.String>', but it was NULL.")
          } else {
            _tmpImageUrls = _tmp_5
          }
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpIsActive: Boolean
          val _tmp_6: Int
          _tmp_6 = _stmt.getLong(_columnIndexOfIsActive).toInt()
          _tmpIsActive = _tmp_6 != 0
          val _tmpViewsCount: Int
          _tmpViewsCount = _stmt.getLong(_columnIndexOfViewsCount).toInt()
          val _tmpInquiriesCount: Int
          _tmpInquiriesCount = _stmt.getLong(_columnIndexOfInquiriesCount).toInt()
          val _tmpLeadTimeDays: Int
          _tmpLeadTimeDays = _stmt.getLong(_columnIndexOfLeadTimeDays).toInt()
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpExpiresAt: Long?
          if (_stmt.isNull(_columnIndexOfExpiresAt)) {
            _tmpExpiresAt = null
          } else {
            _tmpExpiresAt = _stmt.getLong(_columnIndexOfExpiresAt)
          }
          val _tmpDirty: Boolean
          val _tmp_7: Int
          _tmp_7 = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp_7 != 0
          _item =
              MarketListingEntity(_tmpListingId,_tmpSellerId,_tmpInventoryId,_tmpTitle,_tmpDescription,_tmpPrice,_tmpCurrency,_tmpPriceUnit,_tmpCategory,_tmpTags,_tmpDeliveryOptions,_tmpDeliveryCost,_tmpLocationName,_tmpLatitude,_tmpLongitude,_tmpMinOrderQuantity,_tmpMaxOrderQuantity,_tmpImageUrls,_tmpStatus,_tmpIsActive,_tmpViewsCount,_tmpInquiriesCount,_tmpLeadTimeDays,_tmpCreatedAt,_tmpUpdatedAt,_tmpExpiresAt,_tmpDirty)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getAllPublicListings(): Flow<List<MarketListingEntity>> {
    val _sql: String =
        "SELECT * FROM market_listings WHERE status = 'PUBLISHED' AND isActive = 1 ORDER BY updatedAt DESC"
    return createFlow(__db, false, arrayOf("market_listings")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfListingId: Int = getColumnIndexOrThrow(_stmt, "listingId")
        val _columnIndexOfSellerId: Int = getColumnIndexOrThrow(_stmt, "sellerId")
        val _columnIndexOfInventoryId: Int = getColumnIndexOrThrow(_stmt, "inventoryId")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfPrice: Int = getColumnIndexOrThrow(_stmt, "price")
        val _columnIndexOfCurrency: Int = getColumnIndexOrThrow(_stmt, "currency")
        val _columnIndexOfPriceUnit: Int = getColumnIndexOrThrow(_stmt, "priceUnit")
        val _columnIndexOfCategory: Int = getColumnIndexOrThrow(_stmt, "category")
        val _columnIndexOfTags: Int = getColumnIndexOrThrow(_stmt, "tags")
        val _columnIndexOfDeliveryOptions: Int = getColumnIndexOrThrow(_stmt, "deliveryOptions")
        val _columnIndexOfDeliveryCost: Int = getColumnIndexOrThrow(_stmt, "deliveryCost")
        val _columnIndexOfLocationName: Int = getColumnIndexOrThrow(_stmt, "locationName")
        val _columnIndexOfLatitude: Int = getColumnIndexOrThrow(_stmt, "latitude")
        val _columnIndexOfLongitude: Int = getColumnIndexOrThrow(_stmt, "longitude")
        val _columnIndexOfMinOrderQuantity: Int = getColumnIndexOrThrow(_stmt, "minOrderQuantity")
        val _columnIndexOfMaxOrderQuantity: Int = getColumnIndexOrThrow(_stmt, "maxOrderQuantity")
        val _columnIndexOfImageUrls: Int = getColumnIndexOrThrow(_stmt, "imageUrls")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfIsActive: Int = getColumnIndexOrThrow(_stmt, "isActive")
        val _columnIndexOfViewsCount: Int = getColumnIndexOrThrow(_stmt, "viewsCount")
        val _columnIndexOfInquiriesCount: Int = getColumnIndexOrThrow(_stmt, "inquiriesCount")
        val _columnIndexOfLeadTimeDays: Int = getColumnIndexOrThrow(_stmt, "leadTimeDays")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfExpiresAt: Int = getColumnIndexOrThrow(_stmt, "expiresAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _result: MutableList<MarketListingEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: MarketListingEntity
          val _tmpListingId: String
          _tmpListingId = _stmt.getText(_columnIndexOfListingId)
          val _tmpSellerId: String
          _tmpSellerId = _stmt.getText(_columnIndexOfSellerId)
          val _tmpInventoryId: String
          _tmpInventoryId = _stmt.getText(_columnIndexOfInventoryId)
          val _tmpTitle: String
          _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          val _tmpDescription: String
          _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          val _tmpPrice: Double
          _tmpPrice = _stmt.getDouble(_columnIndexOfPrice)
          val _tmpCurrency: String
          _tmpCurrency = _stmt.getText(_columnIndexOfCurrency)
          val _tmpPriceUnit: String
          _tmpPriceUnit = _stmt.getText(_columnIndexOfPriceUnit)
          val _tmpCategory: String
          _tmpCategory = _stmt.getText(_columnIndexOfCategory)
          val _tmpTags: List<String>
          val _tmp: String?
          if (_stmt.isNull(_columnIndexOfTags)) {
            _tmp = null
          } else {
            _tmp = _stmt.getText(_columnIndexOfTags)
          }
          val _tmp_1: List<String>? = AppDatabase.Converters.toStringList(_tmp)
          if (_tmp_1 == null) {
            error("Expected NON-NULL 'kotlin.collections.List<kotlin.String>', but it was NULL.")
          } else {
            _tmpTags = _tmp_1
          }
          val _tmpDeliveryOptions: List<String>
          val _tmp_2: String?
          if (_stmt.isNull(_columnIndexOfDeliveryOptions)) {
            _tmp_2 = null
          } else {
            _tmp_2 = _stmt.getText(_columnIndexOfDeliveryOptions)
          }
          val _tmp_3: List<String>? = AppDatabase.Converters.toStringList(_tmp_2)
          if (_tmp_3 == null) {
            error("Expected NON-NULL 'kotlin.collections.List<kotlin.String>', but it was NULL.")
          } else {
            _tmpDeliveryOptions = _tmp_3
          }
          val _tmpDeliveryCost: Double?
          if (_stmt.isNull(_columnIndexOfDeliveryCost)) {
            _tmpDeliveryCost = null
          } else {
            _tmpDeliveryCost = _stmt.getDouble(_columnIndexOfDeliveryCost)
          }
          val _tmpLocationName: String?
          if (_stmt.isNull(_columnIndexOfLocationName)) {
            _tmpLocationName = null
          } else {
            _tmpLocationName = _stmt.getText(_columnIndexOfLocationName)
          }
          val _tmpLatitude: Double?
          if (_stmt.isNull(_columnIndexOfLatitude)) {
            _tmpLatitude = null
          } else {
            _tmpLatitude = _stmt.getDouble(_columnIndexOfLatitude)
          }
          val _tmpLongitude: Double?
          if (_stmt.isNull(_columnIndexOfLongitude)) {
            _tmpLongitude = null
          } else {
            _tmpLongitude = _stmt.getDouble(_columnIndexOfLongitude)
          }
          val _tmpMinOrderQuantity: Double
          _tmpMinOrderQuantity = _stmt.getDouble(_columnIndexOfMinOrderQuantity)
          val _tmpMaxOrderQuantity: Double?
          if (_stmt.isNull(_columnIndexOfMaxOrderQuantity)) {
            _tmpMaxOrderQuantity = null
          } else {
            _tmpMaxOrderQuantity = _stmt.getDouble(_columnIndexOfMaxOrderQuantity)
          }
          val _tmpImageUrls: List<String>
          val _tmp_4: String?
          if (_stmt.isNull(_columnIndexOfImageUrls)) {
            _tmp_4 = null
          } else {
            _tmp_4 = _stmt.getText(_columnIndexOfImageUrls)
          }
          val _tmp_5: List<String>? = AppDatabase.Converters.toStringList(_tmp_4)
          if (_tmp_5 == null) {
            error("Expected NON-NULL 'kotlin.collections.List<kotlin.String>', but it was NULL.")
          } else {
            _tmpImageUrls = _tmp_5
          }
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpIsActive: Boolean
          val _tmp_6: Int
          _tmp_6 = _stmt.getLong(_columnIndexOfIsActive).toInt()
          _tmpIsActive = _tmp_6 != 0
          val _tmpViewsCount: Int
          _tmpViewsCount = _stmt.getLong(_columnIndexOfViewsCount).toInt()
          val _tmpInquiriesCount: Int
          _tmpInquiriesCount = _stmt.getLong(_columnIndexOfInquiriesCount).toInt()
          val _tmpLeadTimeDays: Int
          _tmpLeadTimeDays = _stmt.getLong(_columnIndexOfLeadTimeDays).toInt()
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpExpiresAt: Long?
          if (_stmt.isNull(_columnIndexOfExpiresAt)) {
            _tmpExpiresAt = null
          } else {
            _tmpExpiresAt = _stmt.getLong(_columnIndexOfExpiresAt)
          }
          val _tmpDirty: Boolean
          val _tmp_7: Int
          _tmp_7 = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp_7 != 0
          _item =
              MarketListingEntity(_tmpListingId,_tmpSellerId,_tmpInventoryId,_tmpTitle,_tmpDescription,_tmpPrice,_tmpCurrency,_tmpPriceUnit,_tmpCategory,_tmpTags,_tmpDeliveryOptions,_tmpDeliveryCost,_tmpLocationName,_tmpLatitude,_tmpLongitude,_tmpMinOrderQuantity,_tmpMaxOrderQuantity,_tmpImageUrls,_tmpStatus,_tmpIsActive,_tmpViewsCount,_tmpInquiriesCount,_tmpLeadTimeDays,_tmpCreatedAt,_tmpUpdatedAt,_tmpExpiresAt,_tmpDirty)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun searchListings(query: String): Flow<List<MarketListingEntity>> {
    val _sql: String =
        "SELECT * FROM market_listings WHERE status = 'PUBLISHED' AND isActive = 1 AND (title LIKE ? OR description LIKE ?)"
    return createFlow(__db, false, arrayOf("market_listings")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, query)
        _argIndex = 2
        _stmt.bindText(_argIndex, query)
        val _columnIndexOfListingId: Int = getColumnIndexOrThrow(_stmt, "listingId")
        val _columnIndexOfSellerId: Int = getColumnIndexOrThrow(_stmt, "sellerId")
        val _columnIndexOfInventoryId: Int = getColumnIndexOrThrow(_stmt, "inventoryId")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfPrice: Int = getColumnIndexOrThrow(_stmt, "price")
        val _columnIndexOfCurrency: Int = getColumnIndexOrThrow(_stmt, "currency")
        val _columnIndexOfPriceUnit: Int = getColumnIndexOrThrow(_stmt, "priceUnit")
        val _columnIndexOfCategory: Int = getColumnIndexOrThrow(_stmt, "category")
        val _columnIndexOfTags: Int = getColumnIndexOrThrow(_stmt, "tags")
        val _columnIndexOfDeliveryOptions: Int = getColumnIndexOrThrow(_stmt, "deliveryOptions")
        val _columnIndexOfDeliveryCost: Int = getColumnIndexOrThrow(_stmt, "deliveryCost")
        val _columnIndexOfLocationName: Int = getColumnIndexOrThrow(_stmt, "locationName")
        val _columnIndexOfLatitude: Int = getColumnIndexOrThrow(_stmt, "latitude")
        val _columnIndexOfLongitude: Int = getColumnIndexOrThrow(_stmt, "longitude")
        val _columnIndexOfMinOrderQuantity: Int = getColumnIndexOrThrow(_stmt, "minOrderQuantity")
        val _columnIndexOfMaxOrderQuantity: Int = getColumnIndexOrThrow(_stmt, "maxOrderQuantity")
        val _columnIndexOfImageUrls: Int = getColumnIndexOrThrow(_stmt, "imageUrls")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfIsActive: Int = getColumnIndexOrThrow(_stmt, "isActive")
        val _columnIndexOfViewsCount: Int = getColumnIndexOrThrow(_stmt, "viewsCount")
        val _columnIndexOfInquiriesCount: Int = getColumnIndexOrThrow(_stmt, "inquiriesCount")
        val _columnIndexOfLeadTimeDays: Int = getColumnIndexOrThrow(_stmt, "leadTimeDays")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfExpiresAt: Int = getColumnIndexOrThrow(_stmt, "expiresAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _result: MutableList<MarketListingEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: MarketListingEntity
          val _tmpListingId: String
          _tmpListingId = _stmt.getText(_columnIndexOfListingId)
          val _tmpSellerId: String
          _tmpSellerId = _stmt.getText(_columnIndexOfSellerId)
          val _tmpInventoryId: String
          _tmpInventoryId = _stmt.getText(_columnIndexOfInventoryId)
          val _tmpTitle: String
          _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          val _tmpDescription: String
          _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          val _tmpPrice: Double
          _tmpPrice = _stmt.getDouble(_columnIndexOfPrice)
          val _tmpCurrency: String
          _tmpCurrency = _stmt.getText(_columnIndexOfCurrency)
          val _tmpPriceUnit: String
          _tmpPriceUnit = _stmt.getText(_columnIndexOfPriceUnit)
          val _tmpCategory: String
          _tmpCategory = _stmt.getText(_columnIndexOfCategory)
          val _tmpTags: List<String>
          val _tmp: String?
          if (_stmt.isNull(_columnIndexOfTags)) {
            _tmp = null
          } else {
            _tmp = _stmt.getText(_columnIndexOfTags)
          }
          val _tmp_1: List<String>? = AppDatabase.Converters.toStringList(_tmp)
          if (_tmp_1 == null) {
            error("Expected NON-NULL 'kotlin.collections.List<kotlin.String>', but it was NULL.")
          } else {
            _tmpTags = _tmp_1
          }
          val _tmpDeliveryOptions: List<String>
          val _tmp_2: String?
          if (_stmt.isNull(_columnIndexOfDeliveryOptions)) {
            _tmp_2 = null
          } else {
            _tmp_2 = _stmt.getText(_columnIndexOfDeliveryOptions)
          }
          val _tmp_3: List<String>? = AppDatabase.Converters.toStringList(_tmp_2)
          if (_tmp_3 == null) {
            error("Expected NON-NULL 'kotlin.collections.List<kotlin.String>', but it was NULL.")
          } else {
            _tmpDeliveryOptions = _tmp_3
          }
          val _tmpDeliveryCost: Double?
          if (_stmt.isNull(_columnIndexOfDeliveryCost)) {
            _tmpDeliveryCost = null
          } else {
            _tmpDeliveryCost = _stmt.getDouble(_columnIndexOfDeliveryCost)
          }
          val _tmpLocationName: String?
          if (_stmt.isNull(_columnIndexOfLocationName)) {
            _tmpLocationName = null
          } else {
            _tmpLocationName = _stmt.getText(_columnIndexOfLocationName)
          }
          val _tmpLatitude: Double?
          if (_stmt.isNull(_columnIndexOfLatitude)) {
            _tmpLatitude = null
          } else {
            _tmpLatitude = _stmt.getDouble(_columnIndexOfLatitude)
          }
          val _tmpLongitude: Double?
          if (_stmt.isNull(_columnIndexOfLongitude)) {
            _tmpLongitude = null
          } else {
            _tmpLongitude = _stmt.getDouble(_columnIndexOfLongitude)
          }
          val _tmpMinOrderQuantity: Double
          _tmpMinOrderQuantity = _stmt.getDouble(_columnIndexOfMinOrderQuantity)
          val _tmpMaxOrderQuantity: Double?
          if (_stmt.isNull(_columnIndexOfMaxOrderQuantity)) {
            _tmpMaxOrderQuantity = null
          } else {
            _tmpMaxOrderQuantity = _stmt.getDouble(_columnIndexOfMaxOrderQuantity)
          }
          val _tmpImageUrls: List<String>
          val _tmp_4: String?
          if (_stmt.isNull(_columnIndexOfImageUrls)) {
            _tmp_4 = null
          } else {
            _tmp_4 = _stmt.getText(_columnIndexOfImageUrls)
          }
          val _tmp_5: List<String>? = AppDatabase.Converters.toStringList(_tmp_4)
          if (_tmp_5 == null) {
            error("Expected NON-NULL 'kotlin.collections.List<kotlin.String>', but it was NULL.")
          } else {
            _tmpImageUrls = _tmp_5
          }
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpIsActive: Boolean
          val _tmp_6: Int
          _tmp_6 = _stmt.getLong(_columnIndexOfIsActive).toInt()
          _tmpIsActive = _tmp_6 != 0
          val _tmpViewsCount: Int
          _tmpViewsCount = _stmt.getLong(_columnIndexOfViewsCount).toInt()
          val _tmpInquiriesCount: Int
          _tmpInquiriesCount = _stmt.getLong(_columnIndexOfInquiriesCount).toInt()
          val _tmpLeadTimeDays: Int
          _tmpLeadTimeDays = _stmt.getLong(_columnIndexOfLeadTimeDays).toInt()
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpExpiresAt: Long?
          if (_stmt.isNull(_columnIndexOfExpiresAt)) {
            _tmpExpiresAt = null
          } else {
            _tmpExpiresAt = _stmt.getLong(_columnIndexOfExpiresAt)
          }
          val _tmpDirty: Boolean
          val _tmp_7: Int
          _tmp_7 = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp_7 != 0
          _item =
              MarketListingEntity(_tmpListingId,_tmpSellerId,_tmpInventoryId,_tmpTitle,_tmpDescription,_tmpPrice,_tmpCurrency,_tmpPriceUnit,_tmpCategory,_tmpTags,_tmpDeliveryOptions,_tmpDeliveryCost,_tmpLocationName,_tmpLatitude,_tmpLongitude,_tmpMinOrderQuantity,_tmpMaxOrderQuantity,_tmpImageUrls,_tmpStatus,_tmpIsActive,_tmpViewsCount,_tmpInquiriesCount,_tmpLeadTimeDays,_tmpCreatedAt,_tmpUpdatedAt,_tmpExpiresAt,_tmpDirty)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun filterInBounds(
    minLat: Double?,
    maxLat: Double?,
    minLng: Double?,
    maxLng: Double?,
  ): List<MarketListingEntity> {
    val _sql: String = """
        |
        |        SELECT * FROM market_listings 
        |        WHERE status = 'PUBLISHED' AND isActive = 1
        |        AND (? IS NULL OR latitude >= ?) 
        |        AND (? IS NULL OR latitude <= ?) 
        |        AND (? IS NULL OR longitude >= ?) 
        |        AND (? IS NULL OR longitude <= ?)
        |    
        """.trimMargin()
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        if (minLat == null) {
          _stmt.bindNull(_argIndex)
        } else {
          _stmt.bindDouble(_argIndex, minLat)
        }
        _argIndex = 2
        if (minLat == null) {
          _stmt.bindNull(_argIndex)
        } else {
          _stmt.bindDouble(_argIndex, minLat)
        }
        _argIndex = 3
        if (maxLat == null) {
          _stmt.bindNull(_argIndex)
        } else {
          _stmt.bindDouble(_argIndex, maxLat)
        }
        _argIndex = 4
        if (maxLat == null) {
          _stmt.bindNull(_argIndex)
        } else {
          _stmt.bindDouble(_argIndex, maxLat)
        }
        _argIndex = 5
        if (minLng == null) {
          _stmt.bindNull(_argIndex)
        } else {
          _stmt.bindDouble(_argIndex, minLng)
        }
        _argIndex = 6
        if (minLng == null) {
          _stmt.bindNull(_argIndex)
        } else {
          _stmt.bindDouble(_argIndex, minLng)
        }
        _argIndex = 7
        if (maxLng == null) {
          _stmt.bindNull(_argIndex)
        } else {
          _stmt.bindDouble(_argIndex, maxLng)
        }
        _argIndex = 8
        if (maxLng == null) {
          _stmt.bindNull(_argIndex)
        } else {
          _stmt.bindDouble(_argIndex, maxLng)
        }
        val _columnIndexOfListingId: Int = getColumnIndexOrThrow(_stmt, "listingId")
        val _columnIndexOfSellerId: Int = getColumnIndexOrThrow(_stmt, "sellerId")
        val _columnIndexOfInventoryId: Int = getColumnIndexOrThrow(_stmt, "inventoryId")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfPrice: Int = getColumnIndexOrThrow(_stmt, "price")
        val _columnIndexOfCurrency: Int = getColumnIndexOrThrow(_stmt, "currency")
        val _columnIndexOfPriceUnit: Int = getColumnIndexOrThrow(_stmt, "priceUnit")
        val _columnIndexOfCategory: Int = getColumnIndexOrThrow(_stmt, "category")
        val _columnIndexOfTags: Int = getColumnIndexOrThrow(_stmt, "tags")
        val _columnIndexOfDeliveryOptions: Int = getColumnIndexOrThrow(_stmt, "deliveryOptions")
        val _columnIndexOfDeliveryCost: Int = getColumnIndexOrThrow(_stmt, "deliveryCost")
        val _columnIndexOfLocationName: Int = getColumnIndexOrThrow(_stmt, "locationName")
        val _columnIndexOfLatitude: Int = getColumnIndexOrThrow(_stmt, "latitude")
        val _columnIndexOfLongitude: Int = getColumnIndexOrThrow(_stmt, "longitude")
        val _columnIndexOfMinOrderQuantity: Int = getColumnIndexOrThrow(_stmt, "minOrderQuantity")
        val _columnIndexOfMaxOrderQuantity: Int = getColumnIndexOrThrow(_stmt, "maxOrderQuantity")
        val _columnIndexOfImageUrls: Int = getColumnIndexOrThrow(_stmt, "imageUrls")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfIsActive: Int = getColumnIndexOrThrow(_stmt, "isActive")
        val _columnIndexOfViewsCount: Int = getColumnIndexOrThrow(_stmt, "viewsCount")
        val _columnIndexOfInquiriesCount: Int = getColumnIndexOrThrow(_stmt, "inquiriesCount")
        val _columnIndexOfLeadTimeDays: Int = getColumnIndexOrThrow(_stmt, "leadTimeDays")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfExpiresAt: Int = getColumnIndexOrThrow(_stmt, "expiresAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _result: MutableList<MarketListingEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: MarketListingEntity
          val _tmpListingId: String
          _tmpListingId = _stmt.getText(_columnIndexOfListingId)
          val _tmpSellerId: String
          _tmpSellerId = _stmt.getText(_columnIndexOfSellerId)
          val _tmpInventoryId: String
          _tmpInventoryId = _stmt.getText(_columnIndexOfInventoryId)
          val _tmpTitle: String
          _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          val _tmpDescription: String
          _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          val _tmpPrice: Double
          _tmpPrice = _stmt.getDouble(_columnIndexOfPrice)
          val _tmpCurrency: String
          _tmpCurrency = _stmt.getText(_columnIndexOfCurrency)
          val _tmpPriceUnit: String
          _tmpPriceUnit = _stmt.getText(_columnIndexOfPriceUnit)
          val _tmpCategory: String
          _tmpCategory = _stmt.getText(_columnIndexOfCategory)
          val _tmpTags: List<String>
          val _tmp: String?
          if (_stmt.isNull(_columnIndexOfTags)) {
            _tmp = null
          } else {
            _tmp = _stmt.getText(_columnIndexOfTags)
          }
          val _tmp_1: List<String>? = AppDatabase.Converters.toStringList(_tmp)
          if (_tmp_1 == null) {
            error("Expected NON-NULL 'kotlin.collections.List<kotlin.String>', but it was NULL.")
          } else {
            _tmpTags = _tmp_1
          }
          val _tmpDeliveryOptions: List<String>
          val _tmp_2: String?
          if (_stmt.isNull(_columnIndexOfDeliveryOptions)) {
            _tmp_2 = null
          } else {
            _tmp_2 = _stmt.getText(_columnIndexOfDeliveryOptions)
          }
          val _tmp_3: List<String>? = AppDatabase.Converters.toStringList(_tmp_2)
          if (_tmp_3 == null) {
            error("Expected NON-NULL 'kotlin.collections.List<kotlin.String>', but it was NULL.")
          } else {
            _tmpDeliveryOptions = _tmp_3
          }
          val _tmpDeliveryCost: Double?
          if (_stmt.isNull(_columnIndexOfDeliveryCost)) {
            _tmpDeliveryCost = null
          } else {
            _tmpDeliveryCost = _stmt.getDouble(_columnIndexOfDeliveryCost)
          }
          val _tmpLocationName: String?
          if (_stmt.isNull(_columnIndexOfLocationName)) {
            _tmpLocationName = null
          } else {
            _tmpLocationName = _stmt.getText(_columnIndexOfLocationName)
          }
          val _tmpLatitude: Double?
          if (_stmt.isNull(_columnIndexOfLatitude)) {
            _tmpLatitude = null
          } else {
            _tmpLatitude = _stmt.getDouble(_columnIndexOfLatitude)
          }
          val _tmpLongitude: Double?
          if (_stmt.isNull(_columnIndexOfLongitude)) {
            _tmpLongitude = null
          } else {
            _tmpLongitude = _stmt.getDouble(_columnIndexOfLongitude)
          }
          val _tmpMinOrderQuantity: Double
          _tmpMinOrderQuantity = _stmt.getDouble(_columnIndexOfMinOrderQuantity)
          val _tmpMaxOrderQuantity: Double?
          if (_stmt.isNull(_columnIndexOfMaxOrderQuantity)) {
            _tmpMaxOrderQuantity = null
          } else {
            _tmpMaxOrderQuantity = _stmt.getDouble(_columnIndexOfMaxOrderQuantity)
          }
          val _tmpImageUrls: List<String>
          val _tmp_4: String?
          if (_stmt.isNull(_columnIndexOfImageUrls)) {
            _tmp_4 = null
          } else {
            _tmp_4 = _stmt.getText(_columnIndexOfImageUrls)
          }
          val _tmp_5: List<String>? = AppDatabase.Converters.toStringList(_tmp_4)
          if (_tmp_5 == null) {
            error("Expected NON-NULL 'kotlin.collections.List<kotlin.String>', but it was NULL.")
          } else {
            _tmpImageUrls = _tmp_5
          }
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpIsActive: Boolean
          val _tmp_6: Int
          _tmp_6 = _stmt.getLong(_columnIndexOfIsActive).toInt()
          _tmpIsActive = _tmp_6 != 0
          val _tmpViewsCount: Int
          _tmpViewsCount = _stmt.getLong(_columnIndexOfViewsCount).toInt()
          val _tmpInquiriesCount: Int
          _tmpInquiriesCount = _stmt.getLong(_columnIndexOfInquiriesCount).toInt()
          val _tmpLeadTimeDays: Int
          _tmpLeadTimeDays = _stmt.getLong(_columnIndexOfLeadTimeDays).toInt()
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpExpiresAt: Long?
          if (_stmt.isNull(_columnIndexOfExpiresAt)) {
            _tmpExpiresAt = null
          } else {
            _tmpExpiresAt = _stmt.getLong(_columnIndexOfExpiresAt)
          }
          val _tmpDirty: Boolean
          val _tmp_7: Int
          _tmp_7 = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp_7 != 0
          _item =
              MarketListingEntity(_tmpListingId,_tmpSellerId,_tmpInventoryId,_tmpTitle,_tmpDescription,_tmpPrice,_tmpCurrency,_tmpPriceUnit,_tmpCategory,_tmpTags,_tmpDeliveryOptions,_tmpDeliveryCost,_tmpLocationName,_tmpLatitude,_tmpLongitude,_tmpMinOrderQuantity,_tmpMaxOrderQuantity,_tmpImageUrls,_tmpStatus,_tmpIsActive,_tmpViewsCount,_tmpInquiriesCount,_tmpLeadTimeDays,_tmpCreatedAt,_tmpUpdatedAt,_tmpExpiresAt,_tmpDirty)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getUpdatedSince(sinceTime: Long, limit: Int):
      List<MarketListingEntity> {
    val _sql: String = "SELECT * FROM market_listings WHERE updatedAt > ? LIMIT ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, sinceTime)
        _argIndex = 2
        _stmt.bindLong(_argIndex, limit.toLong())
        val _columnIndexOfListingId: Int = getColumnIndexOrThrow(_stmt, "listingId")
        val _columnIndexOfSellerId: Int = getColumnIndexOrThrow(_stmt, "sellerId")
        val _columnIndexOfInventoryId: Int = getColumnIndexOrThrow(_stmt, "inventoryId")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfPrice: Int = getColumnIndexOrThrow(_stmt, "price")
        val _columnIndexOfCurrency: Int = getColumnIndexOrThrow(_stmt, "currency")
        val _columnIndexOfPriceUnit: Int = getColumnIndexOrThrow(_stmt, "priceUnit")
        val _columnIndexOfCategory: Int = getColumnIndexOrThrow(_stmt, "category")
        val _columnIndexOfTags: Int = getColumnIndexOrThrow(_stmt, "tags")
        val _columnIndexOfDeliveryOptions: Int = getColumnIndexOrThrow(_stmt, "deliveryOptions")
        val _columnIndexOfDeliveryCost: Int = getColumnIndexOrThrow(_stmt, "deliveryCost")
        val _columnIndexOfLocationName: Int = getColumnIndexOrThrow(_stmt, "locationName")
        val _columnIndexOfLatitude: Int = getColumnIndexOrThrow(_stmt, "latitude")
        val _columnIndexOfLongitude: Int = getColumnIndexOrThrow(_stmt, "longitude")
        val _columnIndexOfMinOrderQuantity: Int = getColumnIndexOrThrow(_stmt, "minOrderQuantity")
        val _columnIndexOfMaxOrderQuantity: Int = getColumnIndexOrThrow(_stmt, "maxOrderQuantity")
        val _columnIndexOfImageUrls: Int = getColumnIndexOrThrow(_stmt, "imageUrls")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfIsActive: Int = getColumnIndexOrThrow(_stmt, "isActive")
        val _columnIndexOfViewsCount: Int = getColumnIndexOrThrow(_stmt, "viewsCount")
        val _columnIndexOfInquiriesCount: Int = getColumnIndexOrThrow(_stmt, "inquiriesCount")
        val _columnIndexOfLeadTimeDays: Int = getColumnIndexOrThrow(_stmt, "leadTimeDays")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfExpiresAt: Int = getColumnIndexOrThrow(_stmt, "expiresAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _result: MutableList<MarketListingEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: MarketListingEntity
          val _tmpListingId: String
          _tmpListingId = _stmt.getText(_columnIndexOfListingId)
          val _tmpSellerId: String
          _tmpSellerId = _stmt.getText(_columnIndexOfSellerId)
          val _tmpInventoryId: String
          _tmpInventoryId = _stmt.getText(_columnIndexOfInventoryId)
          val _tmpTitle: String
          _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          val _tmpDescription: String
          _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          val _tmpPrice: Double
          _tmpPrice = _stmt.getDouble(_columnIndexOfPrice)
          val _tmpCurrency: String
          _tmpCurrency = _stmt.getText(_columnIndexOfCurrency)
          val _tmpPriceUnit: String
          _tmpPriceUnit = _stmt.getText(_columnIndexOfPriceUnit)
          val _tmpCategory: String
          _tmpCategory = _stmt.getText(_columnIndexOfCategory)
          val _tmpTags: List<String>
          val _tmp: String?
          if (_stmt.isNull(_columnIndexOfTags)) {
            _tmp = null
          } else {
            _tmp = _stmt.getText(_columnIndexOfTags)
          }
          val _tmp_1: List<String>? = AppDatabase.Converters.toStringList(_tmp)
          if (_tmp_1 == null) {
            error("Expected NON-NULL 'kotlin.collections.List<kotlin.String>', but it was NULL.")
          } else {
            _tmpTags = _tmp_1
          }
          val _tmpDeliveryOptions: List<String>
          val _tmp_2: String?
          if (_stmt.isNull(_columnIndexOfDeliveryOptions)) {
            _tmp_2 = null
          } else {
            _tmp_2 = _stmt.getText(_columnIndexOfDeliveryOptions)
          }
          val _tmp_3: List<String>? = AppDatabase.Converters.toStringList(_tmp_2)
          if (_tmp_3 == null) {
            error("Expected NON-NULL 'kotlin.collections.List<kotlin.String>', but it was NULL.")
          } else {
            _tmpDeliveryOptions = _tmp_3
          }
          val _tmpDeliveryCost: Double?
          if (_stmt.isNull(_columnIndexOfDeliveryCost)) {
            _tmpDeliveryCost = null
          } else {
            _tmpDeliveryCost = _stmt.getDouble(_columnIndexOfDeliveryCost)
          }
          val _tmpLocationName: String?
          if (_stmt.isNull(_columnIndexOfLocationName)) {
            _tmpLocationName = null
          } else {
            _tmpLocationName = _stmt.getText(_columnIndexOfLocationName)
          }
          val _tmpLatitude: Double?
          if (_stmt.isNull(_columnIndexOfLatitude)) {
            _tmpLatitude = null
          } else {
            _tmpLatitude = _stmt.getDouble(_columnIndexOfLatitude)
          }
          val _tmpLongitude: Double?
          if (_stmt.isNull(_columnIndexOfLongitude)) {
            _tmpLongitude = null
          } else {
            _tmpLongitude = _stmt.getDouble(_columnIndexOfLongitude)
          }
          val _tmpMinOrderQuantity: Double
          _tmpMinOrderQuantity = _stmt.getDouble(_columnIndexOfMinOrderQuantity)
          val _tmpMaxOrderQuantity: Double?
          if (_stmt.isNull(_columnIndexOfMaxOrderQuantity)) {
            _tmpMaxOrderQuantity = null
          } else {
            _tmpMaxOrderQuantity = _stmt.getDouble(_columnIndexOfMaxOrderQuantity)
          }
          val _tmpImageUrls: List<String>
          val _tmp_4: String?
          if (_stmt.isNull(_columnIndexOfImageUrls)) {
            _tmp_4 = null
          } else {
            _tmp_4 = _stmt.getText(_columnIndexOfImageUrls)
          }
          val _tmp_5: List<String>? = AppDatabase.Converters.toStringList(_tmp_4)
          if (_tmp_5 == null) {
            error("Expected NON-NULL 'kotlin.collections.List<kotlin.String>', but it was NULL.")
          } else {
            _tmpImageUrls = _tmp_5
          }
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpIsActive: Boolean
          val _tmp_6: Int
          _tmp_6 = _stmt.getLong(_columnIndexOfIsActive).toInt()
          _tmpIsActive = _tmp_6 != 0
          val _tmpViewsCount: Int
          _tmpViewsCount = _stmt.getLong(_columnIndexOfViewsCount).toInt()
          val _tmpInquiriesCount: Int
          _tmpInquiriesCount = _stmt.getLong(_columnIndexOfInquiriesCount).toInt()
          val _tmpLeadTimeDays: Int
          _tmpLeadTimeDays = _stmt.getLong(_columnIndexOfLeadTimeDays).toInt()
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpExpiresAt: Long?
          if (_stmt.isNull(_columnIndexOfExpiresAt)) {
            _tmpExpiresAt = null
          } else {
            _tmpExpiresAt = _stmt.getLong(_columnIndexOfExpiresAt)
          }
          val _tmpDirty: Boolean
          val _tmp_7: Int
          _tmp_7 = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp_7 != 0
          _item =
              MarketListingEntity(_tmpListingId,_tmpSellerId,_tmpInventoryId,_tmpTitle,_tmpDescription,_tmpPrice,_tmpCurrency,_tmpPriceUnit,_tmpCategory,_tmpTags,_tmpDeliveryOptions,_tmpDeliveryCost,_tmpLocationName,_tmpLatitude,_tmpLongitude,_tmpMinOrderQuantity,_tmpMaxOrderQuantity,_tmpImageUrls,_tmpStatus,_tmpIsActive,_tmpViewsCount,_tmpInquiriesCount,_tmpLeadTimeDays,_tmpCreatedAt,_tmpUpdatedAt,_tmpExpiresAt,_tmpDirty)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun purgeOldSoldListings(staleTime: Long) {
    val _sql: String = "DELETE FROM market_listings WHERE status = 'SOLD_OUT' AND updatedAt < ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, staleTime)
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
