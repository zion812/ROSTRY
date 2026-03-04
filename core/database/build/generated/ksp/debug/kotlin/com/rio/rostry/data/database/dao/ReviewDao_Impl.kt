package com.rio.rostry.`data`.database.dao

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.rio.rostry.`data`.database.entity.RatingStatsEntity
import com.rio.rostry.`data`.database.entity.ReviewEntity
import com.rio.rostry.`data`.database.entity.ReviewHelpfulEntity
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
public class ReviewDao_Impl(
  __db: RoomDatabase,
) : ReviewDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfReviewEntity: EntityInsertAdapter<ReviewEntity>

  private val __insertAdapterOfReviewHelpfulEntity: EntityInsertAdapter<ReviewHelpfulEntity>

  private val __insertAdapterOfRatingStatsEntity: EntityInsertAdapter<RatingStatsEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfReviewEntity = object : EntityInsertAdapter<ReviewEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `reviews` (`reviewId`,`productId`,`sellerId`,`orderId`,`reviewerId`,`rating`,`title`,`content`,`isVerifiedPurchase`,`helpfulCount`,`responseFromSeller`,`responseAt`,`createdAt`,`updatedAt`,`isDeleted`,`dirty`,`adminFlagged`,`moderationNote`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: ReviewEntity) {
        statement.bindText(1, entity.reviewId)
        val _tmpProductId: String? = entity.productId
        if (_tmpProductId == null) {
          statement.bindNull(2)
        } else {
          statement.bindText(2, _tmpProductId)
        }
        statement.bindText(3, entity.sellerId)
        val _tmpOrderId: String? = entity.orderId
        if (_tmpOrderId == null) {
          statement.bindNull(4)
        } else {
          statement.bindText(4, _tmpOrderId)
        }
        statement.bindText(5, entity.reviewerId)
        statement.bindLong(6, entity.rating.toLong())
        val _tmpTitle: String? = entity.title
        if (_tmpTitle == null) {
          statement.bindNull(7)
        } else {
          statement.bindText(7, _tmpTitle)
        }
        val _tmpContent: String? = entity.content
        if (_tmpContent == null) {
          statement.bindNull(8)
        } else {
          statement.bindText(8, _tmpContent)
        }
        val _tmp: Int = if (entity.isVerifiedPurchase) 1 else 0
        statement.bindLong(9, _tmp.toLong())
        statement.bindLong(10, entity.helpfulCount.toLong())
        val _tmpResponseFromSeller: String? = entity.responseFromSeller
        if (_tmpResponseFromSeller == null) {
          statement.bindNull(11)
        } else {
          statement.bindText(11, _tmpResponseFromSeller)
        }
        val _tmpResponseAt: Long? = entity.responseAt
        if (_tmpResponseAt == null) {
          statement.bindNull(12)
        } else {
          statement.bindLong(12, _tmpResponseAt)
        }
        statement.bindLong(13, entity.createdAt)
        statement.bindLong(14, entity.updatedAt)
        val _tmp_1: Int = if (entity.isDeleted) 1 else 0
        statement.bindLong(15, _tmp_1.toLong())
        val _tmp_2: Int = if (entity.dirty) 1 else 0
        statement.bindLong(16, _tmp_2.toLong())
        val _tmp_3: Int = if (entity.adminFlagged) 1 else 0
        statement.bindLong(17, _tmp_3.toLong())
        val _tmpModerationNote: String? = entity.moderationNote
        if (_tmpModerationNote == null) {
          statement.bindNull(18)
        } else {
          statement.bindText(18, _tmpModerationNote)
        }
      }
    }
    this.__insertAdapterOfReviewHelpfulEntity = object : EntityInsertAdapter<ReviewHelpfulEntity>()
        {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `review_helpful` (`reviewId`,`userId`,`createdAt`) VALUES (?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: ReviewHelpfulEntity) {
        statement.bindText(1, entity.reviewId)
        statement.bindText(2, entity.userId)
        statement.bindLong(3, entity.createdAt)
      }
    }
    this.__insertAdapterOfRatingStatsEntity = object : EntityInsertAdapter<RatingStatsEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `rating_stats` (`statsId`,`sellerId`,`productId`,`averageRating`,`totalReviews`,`rating5Count`,`rating4Count`,`rating3Count`,`rating2Count`,`rating1Count`,`verifiedPurchaseCount`,`lastUpdated`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: RatingStatsEntity) {
        statement.bindText(1, entity.statsId)
        val _tmpSellerId: String? = entity.sellerId
        if (_tmpSellerId == null) {
          statement.bindNull(2)
        } else {
          statement.bindText(2, _tmpSellerId)
        }
        val _tmpProductId: String? = entity.productId
        if (_tmpProductId == null) {
          statement.bindNull(3)
        } else {
          statement.bindText(3, _tmpProductId)
        }
        statement.bindDouble(4, entity.averageRating)
        statement.bindLong(5, entity.totalReviews.toLong())
        statement.bindLong(6, entity.rating5Count.toLong())
        statement.bindLong(7, entity.rating4Count.toLong())
        statement.bindLong(8, entity.rating3Count.toLong())
        statement.bindLong(9, entity.rating2Count.toLong())
        statement.bindLong(10, entity.rating1Count.toLong())
        statement.bindLong(11, entity.verifiedPurchaseCount.toLong())
        statement.bindLong(12, entity.lastUpdated)
      }
    }
  }

  public override suspend fun upsert(review: ReviewEntity): Unit = performSuspending(__db, false,
      true) { _connection ->
    __insertAdapterOfReviewEntity.insert(_connection, review)
  }

  public override suspend fun upsertHelpful(helpful: ReviewHelpfulEntity): Unit =
      performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfReviewHelpfulEntity.insert(_connection, helpful)
  }

  public override suspend fun upsertStats(stats: RatingStatsEntity): Unit = performSuspending(__db,
      false, true) { _connection ->
    __insertAdapterOfRatingStatsEntity.insert(_connection, stats)
  }

  public override suspend fun findById(reviewId: String): ReviewEntity? {
    val _sql: String = "SELECT * FROM reviews WHERE reviewId = ? AND isDeleted = 0"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, reviewId)
        val _columnIndexOfReviewId: Int = getColumnIndexOrThrow(_stmt, "reviewId")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfSellerId: Int = getColumnIndexOrThrow(_stmt, "sellerId")
        val _columnIndexOfOrderId: Int = getColumnIndexOrThrow(_stmt, "orderId")
        val _columnIndexOfReviewerId: Int = getColumnIndexOrThrow(_stmt, "reviewerId")
        val _columnIndexOfRating: Int = getColumnIndexOrThrow(_stmt, "rating")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfContent: Int = getColumnIndexOrThrow(_stmt, "content")
        val _columnIndexOfIsVerifiedPurchase: Int = getColumnIndexOrThrow(_stmt,
            "isVerifiedPurchase")
        val _columnIndexOfHelpfulCount: Int = getColumnIndexOrThrow(_stmt, "helpfulCount")
        val _columnIndexOfResponseFromSeller: Int = getColumnIndexOrThrow(_stmt,
            "responseFromSeller")
        val _columnIndexOfResponseAt: Int = getColumnIndexOrThrow(_stmt, "responseAt")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfIsDeleted: Int = getColumnIndexOrThrow(_stmt, "isDeleted")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfAdminFlagged: Int = getColumnIndexOrThrow(_stmt, "adminFlagged")
        val _columnIndexOfModerationNote: Int = getColumnIndexOrThrow(_stmt, "moderationNote")
        val _result: ReviewEntity?
        if (_stmt.step()) {
          val _tmpReviewId: String
          _tmpReviewId = _stmt.getText(_columnIndexOfReviewId)
          val _tmpProductId: String?
          if (_stmt.isNull(_columnIndexOfProductId)) {
            _tmpProductId = null
          } else {
            _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          }
          val _tmpSellerId: String
          _tmpSellerId = _stmt.getText(_columnIndexOfSellerId)
          val _tmpOrderId: String?
          if (_stmt.isNull(_columnIndexOfOrderId)) {
            _tmpOrderId = null
          } else {
            _tmpOrderId = _stmt.getText(_columnIndexOfOrderId)
          }
          val _tmpReviewerId: String
          _tmpReviewerId = _stmt.getText(_columnIndexOfReviewerId)
          val _tmpRating: Int
          _tmpRating = _stmt.getLong(_columnIndexOfRating).toInt()
          val _tmpTitle: String?
          if (_stmt.isNull(_columnIndexOfTitle)) {
            _tmpTitle = null
          } else {
            _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          }
          val _tmpContent: String?
          if (_stmt.isNull(_columnIndexOfContent)) {
            _tmpContent = null
          } else {
            _tmpContent = _stmt.getText(_columnIndexOfContent)
          }
          val _tmpIsVerifiedPurchase: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsVerifiedPurchase).toInt()
          _tmpIsVerifiedPurchase = _tmp != 0
          val _tmpHelpfulCount: Int
          _tmpHelpfulCount = _stmt.getLong(_columnIndexOfHelpfulCount).toInt()
          val _tmpResponseFromSeller: String?
          if (_stmt.isNull(_columnIndexOfResponseFromSeller)) {
            _tmpResponseFromSeller = null
          } else {
            _tmpResponseFromSeller = _stmt.getText(_columnIndexOfResponseFromSeller)
          }
          val _tmpResponseAt: Long?
          if (_stmt.isNull(_columnIndexOfResponseAt)) {
            _tmpResponseAt = null
          } else {
            _tmpResponseAt = _stmt.getLong(_columnIndexOfResponseAt)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpIsDeleted: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfIsDeleted).toInt()
          _tmpIsDeleted = _tmp_1 != 0
          val _tmpDirty: Boolean
          val _tmp_2: Int
          _tmp_2 = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp_2 != 0
          val _tmpAdminFlagged: Boolean
          val _tmp_3: Int
          _tmp_3 = _stmt.getLong(_columnIndexOfAdminFlagged).toInt()
          _tmpAdminFlagged = _tmp_3 != 0
          val _tmpModerationNote: String?
          if (_stmt.isNull(_columnIndexOfModerationNote)) {
            _tmpModerationNote = null
          } else {
            _tmpModerationNote = _stmt.getText(_columnIndexOfModerationNote)
          }
          _result =
              ReviewEntity(_tmpReviewId,_tmpProductId,_tmpSellerId,_tmpOrderId,_tmpReviewerId,_tmpRating,_tmpTitle,_tmpContent,_tmpIsVerifiedPurchase,_tmpHelpfulCount,_tmpResponseFromSeller,_tmpResponseAt,_tmpCreatedAt,_tmpUpdatedAt,_tmpIsDeleted,_tmpDirty,_tmpAdminFlagged,_tmpModerationNote)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getProductReviews(productId: String): Flow<List<ReviewEntity>> {
    val _sql: String =
        "SELECT * FROM reviews WHERE productId = ? AND isDeleted = 0 ORDER BY createdAt DESC"
    return createFlow(__db, false, arrayOf("reviews")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, productId)
        val _columnIndexOfReviewId: Int = getColumnIndexOrThrow(_stmt, "reviewId")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfSellerId: Int = getColumnIndexOrThrow(_stmt, "sellerId")
        val _columnIndexOfOrderId: Int = getColumnIndexOrThrow(_stmt, "orderId")
        val _columnIndexOfReviewerId: Int = getColumnIndexOrThrow(_stmt, "reviewerId")
        val _columnIndexOfRating: Int = getColumnIndexOrThrow(_stmt, "rating")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfContent: Int = getColumnIndexOrThrow(_stmt, "content")
        val _columnIndexOfIsVerifiedPurchase: Int = getColumnIndexOrThrow(_stmt,
            "isVerifiedPurchase")
        val _columnIndexOfHelpfulCount: Int = getColumnIndexOrThrow(_stmt, "helpfulCount")
        val _columnIndexOfResponseFromSeller: Int = getColumnIndexOrThrow(_stmt,
            "responseFromSeller")
        val _columnIndexOfResponseAt: Int = getColumnIndexOrThrow(_stmt, "responseAt")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfIsDeleted: Int = getColumnIndexOrThrow(_stmt, "isDeleted")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfAdminFlagged: Int = getColumnIndexOrThrow(_stmt, "adminFlagged")
        val _columnIndexOfModerationNote: Int = getColumnIndexOrThrow(_stmt, "moderationNote")
        val _result: MutableList<ReviewEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: ReviewEntity
          val _tmpReviewId: String
          _tmpReviewId = _stmt.getText(_columnIndexOfReviewId)
          val _tmpProductId: String?
          if (_stmt.isNull(_columnIndexOfProductId)) {
            _tmpProductId = null
          } else {
            _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          }
          val _tmpSellerId: String
          _tmpSellerId = _stmt.getText(_columnIndexOfSellerId)
          val _tmpOrderId: String?
          if (_stmt.isNull(_columnIndexOfOrderId)) {
            _tmpOrderId = null
          } else {
            _tmpOrderId = _stmt.getText(_columnIndexOfOrderId)
          }
          val _tmpReviewerId: String
          _tmpReviewerId = _stmt.getText(_columnIndexOfReviewerId)
          val _tmpRating: Int
          _tmpRating = _stmt.getLong(_columnIndexOfRating).toInt()
          val _tmpTitle: String?
          if (_stmt.isNull(_columnIndexOfTitle)) {
            _tmpTitle = null
          } else {
            _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          }
          val _tmpContent: String?
          if (_stmt.isNull(_columnIndexOfContent)) {
            _tmpContent = null
          } else {
            _tmpContent = _stmt.getText(_columnIndexOfContent)
          }
          val _tmpIsVerifiedPurchase: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsVerifiedPurchase).toInt()
          _tmpIsVerifiedPurchase = _tmp != 0
          val _tmpHelpfulCount: Int
          _tmpHelpfulCount = _stmt.getLong(_columnIndexOfHelpfulCount).toInt()
          val _tmpResponseFromSeller: String?
          if (_stmt.isNull(_columnIndexOfResponseFromSeller)) {
            _tmpResponseFromSeller = null
          } else {
            _tmpResponseFromSeller = _stmt.getText(_columnIndexOfResponseFromSeller)
          }
          val _tmpResponseAt: Long?
          if (_stmt.isNull(_columnIndexOfResponseAt)) {
            _tmpResponseAt = null
          } else {
            _tmpResponseAt = _stmt.getLong(_columnIndexOfResponseAt)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpIsDeleted: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfIsDeleted).toInt()
          _tmpIsDeleted = _tmp_1 != 0
          val _tmpDirty: Boolean
          val _tmp_2: Int
          _tmp_2 = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp_2 != 0
          val _tmpAdminFlagged: Boolean
          val _tmp_3: Int
          _tmp_3 = _stmt.getLong(_columnIndexOfAdminFlagged).toInt()
          _tmpAdminFlagged = _tmp_3 != 0
          val _tmpModerationNote: String?
          if (_stmt.isNull(_columnIndexOfModerationNote)) {
            _tmpModerationNote = null
          } else {
            _tmpModerationNote = _stmt.getText(_columnIndexOfModerationNote)
          }
          _item =
              ReviewEntity(_tmpReviewId,_tmpProductId,_tmpSellerId,_tmpOrderId,_tmpReviewerId,_tmpRating,_tmpTitle,_tmpContent,_tmpIsVerifiedPurchase,_tmpHelpfulCount,_tmpResponseFromSeller,_tmpResponseAt,_tmpCreatedAt,_tmpUpdatedAt,_tmpIsDeleted,_tmpDirty,_tmpAdminFlagged,_tmpModerationNote)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getSellerReviews(sellerId: String): Flow<List<ReviewEntity>> {
    val _sql: String =
        "SELECT * FROM reviews WHERE sellerId = ? AND isDeleted = 0 ORDER BY createdAt DESC"
    return createFlow(__db, false, arrayOf("reviews")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, sellerId)
        val _columnIndexOfReviewId: Int = getColumnIndexOrThrow(_stmt, "reviewId")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfSellerId: Int = getColumnIndexOrThrow(_stmt, "sellerId")
        val _columnIndexOfOrderId: Int = getColumnIndexOrThrow(_stmt, "orderId")
        val _columnIndexOfReviewerId: Int = getColumnIndexOrThrow(_stmt, "reviewerId")
        val _columnIndexOfRating: Int = getColumnIndexOrThrow(_stmt, "rating")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfContent: Int = getColumnIndexOrThrow(_stmt, "content")
        val _columnIndexOfIsVerifiedPurchase: Int = getColumnIndexOrThrow(_stmt,
            "isVerifiedPurchase")
        val _columnIndexOfHelpfulCount: Int = getColumnIndexOrThrow(_stmt, "helpfulCount")
        val _columnIndexOfResponseFromSeller: Int = getColumnIndexOrThrow(_stmt,
            "responseFromSeller")
        val _columnIndexOfResponseAt: Int = getColumnIndexOrThrow(_stmt, "responseAt")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfIsDeleted: Int = getColumnIndexOrThrow(_stmt, "isDeleted")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfAdminFlagged: Int = getColumnIndexOrThrow(_stmt, "adminFlagged")
        val _columnIndexOfModerationNote: Int = getColumnIndexOrThrow(_stmt, "moderationNote")
        val _result: MutableList<ReviewEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: ReviewEntity
          val _tmpReviewId: String
          _tmpReviewId = _stmt.getText(_columnIndexOfReviewId)
          val _tmpProductId: String?
          if (_stmt.isNull(_columnIndexOfProductId)) {
            _tmpProductId = null
          } else {
            _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          }
          val _tmpSellerId: String
          _tmpSellerId = _stmt.getText(_columnIndexOfSellerId)
          val _tmpOrderId: String?
          if (_stmt.isNull(_columnIndexOfOrderId)) {
            _tmpOrderId = null
          } else {
            _tmpOrderId = _stmt.getText(_columnIndexOfOrderId)
          }
          val _tmpReviewerId: String
          _tmpReviewerId = _stmt.getText(_columnIndexOfReviewerId)
          val _tmpRating: Int
          _tmpRating = _stmt.getLong(_columnIndexOfRating).toInt()
          val _tmpTitle: String?
          if (_stmt.isNull(_columnIndexOfTitle)) {
            _tmpTitle = null
          } else {
            _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          }
          val _tmpContent: String?
          if (_stmt.isNull(_columnIndexOfContent)) {
            _tmpContent = null
          } else {
            _tmpContent = _stmt.getText(_columnIndexOfContent)
          }
          val _tmpIsVerifiedPurchase: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsVerifiedPurchase).toInt()
          _tmpIsVerifiedPurchase = _tmp != 0
          val _tmpHelpfulCount: Int
          _tmpHelpfulCount = _stmt.getLong(_columnIndexOfHelpfulCount).toInt()
          val _tmpResponseFromSeller: String?
          if (_stmt.isNull(_columnIndexOfResponseFromSeller)) {
            _tmpResponseFromSeller = null
          } else {
            _tmpResponseFromSeller = _stmt.getText(_columnIndexOfResponseFromSeller)
          }
          val _tmpResponseAt: Long?
          if (_stmt.isNull(_columnIndexOfResponseAt)) {
            _tmpResponseAt = null
          } else {
            _tmpResponseAt = _stmt.getLong(_columnIndexOfResponseAt)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpIsDeleted: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfIsDeleted).toInt()
          _tmpIsDeleted = _tmp_1 != 0
          val _tmpDirty: Boolean
          val _tmp_2: Int
          _tmp_2 = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp_2 != 0
          val _tmpAdminFlagged: Boolean
          val _tmp_3: Int
          _tmp_3 = _stmt.getLong(_columnIndexOfAdminFlagged).toInt()
          _tmpAdminFlagged = _tmp_3 != 0
          val _tmpModerationNote: String?
          if (_stmt.isNull(_columnIndexOfModerationNote)) {
            _tmpModerationNote = null
          } else {
            _tmpModerationNote = _stmt.getText(_columnIndexOfModerationNote)
          }
          _item =
              ReviewEntity(_tmpReviewId,_tmpProductId,_tmpSellerId,_tmpOrderId,_tmpReviewerId,_tmpRating,_tmpTitle,_tmpContent,_tmpIsVerifiedPurchase,_tmpHelpfulCount,_tmpResponseFromSeller,_tmpResponseAt,_tmpCreatedAt,_tmpUpdatedAt,_tmpIsDeleted,_tmpDirty,_tmpAdminFlagged,_tmpModerationNote)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getUserReviews(userId: String): Flow<List<ReviewEntity>> {
    val _sql: String =
        "SELECT * FROM reviews WHERE reviewerId = ? AND isDeleted = 0 ORDER BY createdAt DESC"
    return createFlow(__db, false, arrayOf("reviews")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, userId)
        val _columnIndexOfReviewId: Int = getColumnIndexOrThrow(_stmt, "reviewId")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfSellerId: Int = getColumnIndexOrThrow(_stmt, "sellerId")
        val _columnIndexOfOrderId: Int = getColumnIndexOrThrow(_stmt, "orderId")
        val _columnIndexOfReviewerId: Int = getColumnIndexOrThrow(_stmt, "reviewerId")
        val _columnIndexOfRating: Int = getColumnIndexOrThrow(_stmt, "rating")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfContent: Int = getColumnIndexOrThrow(_stmt, "content")
        val _columnIndexOfIsVerifiedPurchase: Int = getColumnIndexOrThrow(_stmt,
            "isVerifiedPurchase")
        val _columnIndexOfHelpfulCount: Int = getColumnIndexOrThrow(_stmt, "helpfulCount")
        val _columnIndexOfResponseFromSeller: Int = getColumnIndexOrThrow(_stmt,
            "responseFromSeller")
        val _columnIndexOfResponseAt: Int = getColumnIndexOrThrow(_stmt, "responseAt")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfIsDeleted: Int = getColumnIndexOrThrow(_stmt, "isDeleted")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfAdminFlagged: Int = getColumnIndexOrThrow(_stmt, "adminFlagged")
        val _columnIndexOfModerationNote: Int = getColumnIndexOrThrow(_stmt, "moderationNote")
        val _result: MutableList<ReviewEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: ReviewEntity
          val _tmpReviewId: String
          _tmpReviewId = _stmt.getText(_columnIndexOfReviewId)
          val _tmpProductId: String?
          if (_stmt.isNull(_columnIndexOfProductId)) {
            _tmpProductId = null
          } else {
            _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          }
          val _tmpSellerId: String
          _tmpSellerId = _stmt.getText(_columnIndexOfSellerId)
          val _tmpOrderId: String?
          if (_stmt.isNull(_columnIndexOfOrderId)) {
            _tmpOrderId = null
          } else {
            _tmpOrderId = _stmt.getText(_columnIndexOfOrderId)
          }
          val _tmpReviewerId: String
          _tmpReviewerId = _stmt.getText(_columnIndexOfReviewerId)
          val _tmpRating: Int
          _tmpRating = _stmt.getLong(_columnIndexOfRating).toInt()
          val _tmpTitle: String?
          if (_stmt.isNull(_columnIndexOfTitle)) {
            _tmpTitle = null
          } else {
            _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          }
          val _tmpContent: String?
          if (_stmt.isNull(_columnIndexOfContent)) {
            _tmpContent = null
          } else {
            _tmpContent = _stmt.getText(_columnIndexOfContent)
          }
          val _tmpIsVerifiedPurchase: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsVerifiedPurchase).toInt()
          _tmpIsVerifiedPurchase = _tmp != 0
          val _tmpHelpfulCount: Int
          _tmpHelpfulCount = _stmt.getLong(_columnIndexOfHelpfulCount).toInt()
          val _tmpResponseFromSeller: String?
          if (_stmt.isNull(_columnIndexOfResponseFromSeller)) {
            _tmpResponseFromSeller = null
          } else {
            _tmpResponseFromSeller = _stmt.getText(_columnIndexOfResponseFromSeller)
          }
          val _tmpResponseAt: Long?
          if (_stmt.isNull(_columnIndexOfResponseAt)) {
            _tmpResponseAt = null
          } else {
            _tmpResponseAt = _stmt.getLong(_columnIndexOfResponseAt)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpIsDeleted: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfIsDeleted).toInt()
          _tmpIsDeleted = _tmp_1 != 0
          val _tmpDirty: Boolean
          val _tmp_2: Int
          _tmp_2 = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp_2 != 0
          val _tmpAdminFlagged: Boolean
          val _tmp_3: Int
          _tmp_3 = _stmt.getLong(_columnIndexOfAdminFlagged).toInt()
          _tmpAdminFlagged = _tmp_3 != 0
          val _tmpModerationNote: String?
          if (_stmt.isNull(_columnIndexOfModerationNote)) {
            _tmpModerationNote = null
          } else {
            _tmpModerationNote = _stmt.getText(_columnIndexOfModerationNote)
          }
          _item =
              ReviewEntity(_tmpReviewId,_tmpProductId,_tmpSellerId,_tmpOrderId,_tmpReviewerId,_tmpRating,_tmpTitle,_tmpContent,_tmpIsVerifiedPurchase,_tmpHelpfulCount,_tmpResponseFromSeller,_tmpResponseAt,_tmpCreatedAt,_tmpUpdatedAt,_tmpIsDeleted,_tmpDirty,_tmpAdminFlagged,_tmpModerationNote)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getReviewByOrder(orderId: String): ReviewEntity? {
    val _sql: String = "SELECT * FROM reviews WHERE orderId = ? AND isDeleted = 0 LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, orderId)
        val _columnIndexOfReviewId: Int = getColumnIndexOrThrow(_stmt, "reviewId")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfSellerId: Int = getColumnIndexOrThrow(_stmt, "sellerId")
        val _columnIndexOfOrderId: Int = getColumnIndexOrThrow(_stmt, "orderId")
        val _columnIndexOfReviewerId: Int = getColumnIndexOrThrow(_stmt, "reviewerId")
        val _columnIndexOfRating: Int = getColumnIndexOrThrow(_stmt, "rating")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfContent: Int = getColumnIndexOrThrow(_stmt, "content")
        val _columnIndexOfIsVerifiedPurchase: Int = getColumnIndexOrThrow(_stmt,
            "isVerifiedPurchase")
        val _columnIndexOfHelpfulCount: Int = getColumnIndexOrThrow(_stmt, "helpfulCount")
        val _columnIndexOfResponseFromSeller: Int = getColumnIndexOrThrow(_stmt,
            "responseFromSeller")
        val _columnIndexOfResponseAt: Int = getColumnIndexOrThrow(_stmt, "responseAt")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfIsDeleted: Int = getColumnIndexOrThrow(_stmt, "isDeleted")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfAdminFlagged: Int = getColumnIndexOrThrow(_stmt, "adminFlagged")
        val _columnIndexOfModerationNote: Int = getColumnIndexOrThrow(_stmt, "moderationNote")
        val _result: ReviewEntity?
        if (_stmt.step()) {
          val _tmpReviewId: String
          _tmpReviewId = _stmt.getText(_columnIndexOfReviewId)
          val _tmpProductId: String?
          if (_stmt.isNull(_columnIndexOfProductId)) {
            _tmpProductId = null
          } else {
            _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          }
          val _tmpSellerId: String
          _tmpSellerId = _stmt.getText(_columnIndexOfSellerId)
          val _tmpOrderId: String?
          if (_stmt.isNull(_columnIndexOfOrderId)) {
            _tmpOrderId = null
          } else {
            _tmpOrderId = _stmt.getText(_columnIndexOfOrderId)
          }
          val _tmpReviewerId: String
          _tmpReviewerId = _stmt.getText(_columnIndexOfReviewerId)
          val _tmpRating: Int
          _tmpRating = _stmt.getLong(_columnIndexOfRating).toInt()
          val _tmpTitle: String?
          if (_stmt.isNull(_columnIndexOfTitle)) {
            _tmpTitle = null
          } else {
            _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          }
          val _tmpContent: String?
          if (_stmt.isNull(_columnIndexOfContent)) {
            _tmpContent = null
          } else {
            _tmpContent = _stmt.getText(_columnIndexOfContent)
          }
          val _tmpIsVerifiedPurchase: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsVerifiedPurchase).toInt()
          _tmpIsVerifiedPurchase = _tmp != 0
          val _tmpHelpfulCount: Int
          _tmpHelpfulCount = _stmt.getLong(_columnIndexOfHelpfulCount).toInt()
          val _tmpResponseFromSeller: String?
          if (_stmt.isNull(_columnIndexOfResponseFromSeller)) {
            _tmpResponseFromSeller = null
          } else {
            _tmpResponseFromSeller = _stmt.getText(_columnIndexOfResponseFromSeller)
          }
          val _tmpResponseAt: Long?
          if (_stmt.isNull(_columnIndexOfResponseAt)) {
            _tmpResponseAt = null
          } else {
            _tmpResponseAt = _stmt.getLong(_columnIndexOfResponseAt)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpIsDeleted: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfIsDeleted).toInt()
          _tmpIsDeleted = _tmp_1 != 0
          val _tmpDirty: Boolean
          val _tmp_2: Int
          _tmp_2 = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp_2 != 0
          val _tmpAdminFlagged: Boolean
          val _tmp_3: Int
          _tmp_3 = _stmt.getLong(_columnIndexOfAdminFlagged).toInt()
          _tmpAdminFlagged = _tmp_3 != 0
          val _tmpModerationNote: String?
          if (_stmt.isNull(_columnIndexOfModerationNote)) {
            _tmpModerationNote = null
          } else {
            _tmpModerationNote = _stmt.getText(_columnIndexOfModerationNote)
          }
          _result =
              ReviewEntity(_tmpReviewId,_tmpProductId,_tmpSellerId,_tmpOrderId,_tmpReviewerId,_tmpRating,_tmpTitle,_tmpContent,_tmpIsVerifiedPurchase,_tmpHelpfulCount,_tmpResponseFromSeller,_tmpResponseAt,_tmpCreatedAt,_tmpUpdatedAt,_tmpIsDeleted,_tmpDirty,_tmpAdminFlagged,_tmpModerationNote)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun countProductReviews(productId: String): Flow<Int> {
    val _sql: String = "SELECT COUNT(*) FROM reviews WHERE productId = ? AND isDeleted = 0"
    return createFlow(__db, false, arrayOf("reviews")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, productId)
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

  public override fun countSellerReviews(sellerId: String): Flow<Int> {
    val _sql: String = "SELECT COUNT(*) FROM reviews WHERE sellerId = ? AND isDeleted = 0"
    return createFlow(__db, false, arrayOf("reviews")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, sellerId)
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

  public override fun averageProductRating(productId: String): Flow<Double?> {
    val _sql: String = "SELECT AVG(rating) FROM reviews WHERE productId = ? AND isDeleted = 0"
    return createFlow(__db, false, arrayOf("reviews")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, productId)
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

  public override fun averageSellerRating(sellerId: String): Flow<Double?> {
    val _sql: String = "SELECT AVG(rating) FROM reviews WHERE sellerId = ? AND isDeleted = 0"
    return createFlow(__db, false, arrayOf("reviews")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, sellerId)
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

  public override fun getSellerStats(sellerId: String): Flow<RatingStatsEntity?> {
    val _sql: String = "SELECT * FROM rating_stats WHERE sellerId = ? LIMIT 1"
    return createFlow(__db, false, arrayOf("rating_stats")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, sellerId)
        val _columnIndexOfStatsId: Int = getColumnIndexOrThrow(_stmt, "statsId")
        val _columnIndexOfSellerId: Int = getColumnIndexOrThrow(_stmt, "sellerId")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfAverageRating: Int = getColumnIndexOrThrow(_stmt, "averageRating")
        val _columnIndexOfTotalReviews: Int = getColumnIndexOrThrow(_stmt, "totalReviews")
        val _columnIndexOfRating5Count: Int = getColumnIndexOrThrow(_stmt, "rating5Count")
        val _columnIndexOfRating4Count: Int = getColumnIndexOrThrow(_stmt, "rating4Count")
        val _columnIndexOfRating3Count: Int = getColumnIndexOrThrow(_stmt, "rating3Count")
        val _columnIndexOfRating2Count: Int = getColumnIndexOrThrow(_stmt, "rating2Count")
        val _columnIndexOfRating1Count: Int = getColumnIndexOrThrow(_stmt, "rating1Count")
        val _columnIndexOfVerifiedPurchaseCount: Int = getColumnIndexOrThrow(_stmt,
            "verifiedPurchaseCount")
        val _columnIndexOfLastUpdated: Int = getColumnIndexOrThrow(_stmt, "lastUpdated")
        val _result: RatingStatsEntity?
        if (_stmt.step()) {
          val _tmpStatsId: String
          _tmpStatsId = _stmt.getText(_columnIndexOfStatsId)
          val _tmpSellerId: String?
          if (_stmt.isNull(_columnIndexOfSellerId)) {
            _tmpSellerId = null
          } else {
            _tmpSellerId = _stmt.getText(_columnIndexOfSellerId)
          }
          val _tmpProductId: String?
          if (_stmt.isNull(_columnIndexOfProductId)) {
            _tmpProductId = null
          } else {
            _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          }
          val _tmpAverageRating: Double
          _tmpAverageRating = _stmt.getDouble(_columnIndexOfAverageRating)
          val _tmpTotalReviews: Int
          _tmpTotalReviews = _stmt.getLong(_columnIndexOfTotalReviews).toInt()
          val _tmpRating5Count: Int
          _tmpRating5Count = _stmt.getLong(_columnIndexOfRating5Count).toInt()
          val _tmpRating4Count: Int
          _tmpRating4Count = _stmt.getLong(_columnIndexOfRating4Count).toInt()
          val _tmpRating3Count: Int
          _tmpRating3Count = _stmt.getLong(_columnIndexOfRating3Count).toInt()
          val _tmpRating2Count: Int
          _tmpRating2Count = _stmt.getLong(_columnIndexOfRating2Count).toInt()
          val _tmpRating1Count: Int
          _tmpRating1Count = _stmt.getLong(_columnIndexOfRating1Count).toInt()
          val _tmpVerifiedPurchaseCount: Int
          _tmpVerifiedPurchaseCount = _stmt.getLong(_columnIndexOfVerifiedPurchaseCount).toInt()
          val _tmpLastUpdated: Long
          _tmpLastUpdated = _stmt.getLong(_columnIndexOfLastUpdated)
          _result =
              RatingStatsEntity(_tmpStatsId,_tmpSellerId,_tmpProductId,_tmpAverageRating,_tmpTotalReviews,_tmpRating5Count,_tmpRating4Count,_tmpRating3Count,_tmpRating2Count,_tmpRating1Count,_tmpVerifiedPurchaseCount,_tmpLastUpdated)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getProductStats(productId: String): Flow<RatingStatsEntity?> {
    val _sql: String = "SELECT * FROM rating_stats WHERE productId = ? LIMIT 1"
    return createFlow(__db, false, arrayOf("rating_stats")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, productId)
        val _columnIndexOfStatsId: Int = getColumnIndexOrThrow(_stmt, "statsId")
        val _columnIndexOfSellerId: Int = getColumnIndexOrThrow(_stmt, "sellerId")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfAverageRating: Int = getColumnIndexOrThrow(_stmt, "averageRating")
        val _columnIndexOfTotalReviews: Int = getColumnIndexOrThrow(_stmt, "totalReviews")
        val _columnIndexOfRating5Count: Int = getColumnIndexOrThrow(_stmt, "rating5Count")
        val _columnIndexOfRating4Count: Int = getColumnIndexOrThrow(_stmt, "rating4Count")
        val _columnIndexOfRating3Count: Int = getColumnIndexOrThrow(_stmt, "rating3Count")
        val _columnIndexOfRating2Count: Int = getColumnIndexOrThrow(_stmt, "rating2Count")
        val _columnIndexOfRating1Count: Int = getColumnIndexOrThrow(_stmt, "rating1Count")
        val _columnIndexOfVerifiedPurchaseCount: Int = getColumnIndexOrThrow(_stmt,
            "verifiedPurchaseCount")
        val _columnIndexOfLastUpdated: Int = getColumnIndexOrThrow(_stmt, "lastUpdated")
        val _result: RatingStatsEntity?
        if (_stmt.step()) {
          val _tmpStatsId: String
          _tmpStatsId = _stmt.getText(_columnIndexOfStatsId)
          val _tmpSellerId: String?
          if (_stmt.isNull(_columnIndexOfSellerId)) {
            _tmpSellerId = null
          } else {
            _tmpSellerId = _stmt.getText(_columnIndexOfSellerId)
          }
          val _tmpProductId: String?
          if (_stmt.isNull(_columnIndexOfProductId)) {
            _tmpProductId = null
          } else {
            _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          }
          val _tmpAverageRating: Double
          _tmpAverageRating = _stmt.getDouble(_columnIndexOfAverageRating)
          val _tmpTotalReviews: Int
          _tmpTotalReviews = _stmt.getLong(_columnIndexOfTotalReviews).toInt()
          val _tmpRating5Count: Int
          _tmpRating5Count = _stmt.getLong(_columnIndexOfRating5Count).toInt()
          val _tmpRating4Count: Int
          _tmpRating4Count = _stmt.getLong(_columnIndexOfRating4Count).toInt()
          val _tmpRating3Count: Int
          _tmpRating3Count = _stmt.getLong(_columnIndexOfRating3Count).toInt()
          val _tmpRating2Count: Int
          _tmpRating2Count = _stmt.getLong(_columnIndexOfRating2Count).toInt()
          val _tmpRating1Count: Int
          _tmpRating1Count = _stmt.getLong(_columnIndexOfRating1Count).toInt()
          val _tmpVerifiedPurchaseCount: Int
          _tmpVerifiedPurchaseCount = _stmt.getLong(_columnIndexOfVerifiedPurchaseCount).toInt()
          val _tmpLastUpdated: Long
          _tmpLastUpdated = _stmt.getLong(_columnIndexOfLastUpdated)
          _result =
              RatingStatsEntity(_tmpStatsId,_tmpSellerId,_tmpProductId,_tmpAverageRating,_tmpTotalReviews,_tmpRating5Count,_tmpRating4Count,_tmpRating3Count,_tmpRating2Count,_tmpRating1Count,_tmpVerifiedPurchaseCount,_tmpLastUpdated)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun hasMarkedHelpful(reviewId: String, userId: String): Boolean {
    val _sql: String =
        "SELECT EXISTS(SELECT 1 FROM review_helpful WHERE reviewId = ? AND userId = ?)"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, reviewId)
        _argIndex = 2
        _stmt.bindText(_argIndex, userId)
        val _result: Boolean
        if (_stmt.step()) {
          val _tmp: Int
          _tmp = _stmt.getLong(0).toInt()
          _result = _tmp != 0
        } else {
          _result = false
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getDirtyReviews(): List<ReviewEntity> {
    val _sql: String = "SELECT * FROM reviews WHERE dirty = 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfReviewId: Int = getColumnIndexOrThrow(_stmt, "reviewId")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfSellerId: Int = getColumnIndexOrThrow(_stmt, "sellerId")
        val _columnIndexOfOrderId: Int = getColumnIndexOrThrow(_stmt, "orderId")
        val _columnIndexOfReviewerId: Int = getColumnIndexOrThrow(_stmt, "reviewerId")
        val _columnIndexOfRating: Int = getColumnIndexOrThrow(_stmt, "rating")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfContent: Int = getColumnIndexOrThrow(_stmt, "content")
        val _columnIndexOfIsVerifiedPurchase: Int = getColumnIndexOrThrow(_stmt,
            "isVerifiedPurchase")
        val _columnIndexOfHelpfulCount: Int = getColumnIndexOrThrow(_stmt, "helpfulCount")
        val _columnIndexOfResponseFromSeller: Int = getColumnIndexOrThrow(_stmt,
            "responseFromSeller")
        val _columnIndexOfResponseAt: Int = getColumnIndexOrThrow(_stmt, "responseAt")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfIsDeleted: Int = getColumnIndexOrThrow(_stmt, "isDeleted")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfAdminFlagged: Int = getColumnIndexOrThrow(_stmt, "adminFlagged")
        val _columnIndexOfModerationNote: Int = getColumnIndexOrThrow(_stmt, "moderationNote")
        val _result: MutableList<ReviewEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: ReviewEntity
          val _tmpReviewId: String
          _tmpReviewId = _stmt.getText(_columnIndexOfReviewId)
          val _tmpProductId: String?
          if (_stmt.isNull(_columnIndexOfProductId)) {
            _tmpProductId = null
          } else {
            _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          }
          val _tmpSellerId: String
          _tmpSellerId = _stmt.getText(_columnIndexOfSellerId)
          val _tmpOrderId: String?
          if (_stmt.isNull(_columnIndexOfOrderId)) {
            _tmpOrderId = null
          } else {
            _tmpOrderId = _stmt.getText(_columnIndexOfOrderId)
          }
          val _tmpReviewerId: String
          _tmpReviewerId = _stmt.getText(_columnIndexOfReviewerId)
          val _tmpRating: Int
          _tmpRating = _stmt.getLong(_columnIndexOfRating).toInt()
          val _tmpTitle: String?
          if (_stmt.isNull(_columnIndexOfTitle)) {
            _tmpTitle = null
          } else {
            _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          }
          val _tmpContent: String?
          if (_stmt.isNull(_columnIndexOfContent)) {
            _tmpContent = null
          } else {
            _tmpContent = _stmt.getText(_columnIndexOfContent)
          }
          val _tmpIsVerifiedPurchase: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsVerifiedPurchase).toInt()
          _tmpIsVerifiedPurchase = _tmp != 0
          val _tmpHelpfulCount: Int
          _tmpHelpfulCount = _stmt.getLong(_columnIndexOfHelpfulCount).toInt()
          val _tmpResponseFromSeller: String?
          if (_stmt.isNull(_columnIndexOfResponseFromSeller)) {
            _tmpResponseFromSeller = null
          } else {
            _tmpResponseFromSeller = _stmt.getText(_columnIndexOfResponseFromSeller)
          }
          val _tmpResponseAt: Long?
          if (_stmt.isNull(_columnIndexOfResponseAt)) {
            _tmpResponseAt = null
          } else {
            _tmpResponseAt = _stmt.getLong(_columnIndexOfResponseAt)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpIsDeleted: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfIsDeleted).toInt()
          _tmpIsDeleted = _tmp_1 != 0
          val _tmpDirty: Boolean
          val _tmp_2: Int
          _tmp_2 = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp_2 != 0
          val _tmpAdminFlagged: Boolean
          val _tmp_3: Int
          _tmp_3 = _stmt.getLong(_columnIndexOfAdminFlagged).toInt()
          _tmpAdminFlagged = _tmp_3 != 0
          val _tmpModerationNote: String?
          if (_stmt.isNull(_columnIndexOfModerationNote)) {
            _tmpModerationNote = null
          } else {
            _tmpModerationNote = _stmt.getText(_columnIndexOfModerationNote)
          }
          _item =
              ReviewEntity(_tmpReviewId,_tmpProductId,_tmpSellerId,_tmpOrderId,_tmpReviewerId,_tmpRating,_tmpTitle,_tmpContent,_tmpIsVerifiedPurchase,_tmpHelpfulCount,_tmpResponseFromSeller,_tmpResponseAt,_tmpCreatedAt,_tmpUpdatedAt,_tmpIsDeleted,_tmpDirty,_tmpAdminFlagged,_tmpModerationNote)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getFlaggedReviews(): Flow<List<ReviewEntity>> {
    val _sql: String = "SELECT * FROM reviews WHERE adminFlagged = 1 ORDER BY createdAt DESC"
    return createFlow(__db, false, arrayOf("reviews")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfReviewId: Int = getColumnIndexOrThrow(_stmt, "reviewId")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfSellerId: Int = getColumnIndexOrThrow(_stmt, "sellerId")
        val _columnIndexOfOrderId: Int = getColumnIndexOrThrow(_stmt, "orderId")
        val _columnIndexOfReviewerId: Int = getColumnIndexOrThrow(_stmt, "reviewerId")
        val _columnIndexOfRating: Int = getColumnIndexOrThrow(_stmt, "rating")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfContent: Int = getColumnIndexOrThrow(_stmt, "content")
        val _columnIndexOfIsVerifiedPurchase: Int = getColumnIndexOrThrow(_stmt,
            "isVerifiedPurchase")
        val _columnIndexOfHelpfulCount: Int = getColumnIndexOrThrow(_stmt, "helpfulCount")
        val _columnIndexOfResponseFromSeller: Int = getColumnIndexOrThrow(_stmt,
            "responseFromSeller")
        val _columnIndexOfResponseAt: Int = getColumnIndexOrThrow(_stmt, "responseAt")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfIsDeleted: Int = getColumnIndexOrThrow(_stmt, "isDeleted")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfAdminFlagged: Int = getColumnIndexOrThrow(_stmt, "adminFlagged")
        val _columnIndexOfModerationNote: Int = getColumnIndexOrThrow(_stmt, "moderationNote")
        val _result: MutableList<ReviewEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: ReviewEntity
          val _tmpReviewId: String
          _tmpReviewId = _stmt.getText(_columnIndexOfReviewId)
          val _tmpProductId: String?
          if (_stmt.isNull(_columnIndexOfProductId)) {
            _tmpProductId = null
          } else {
            _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          }
          val _tmpSellerId: String
          _tmpSellerId = _stmt.getText(_columnIndexOfSellerId)
          val _tmpOrderId: String?
          if (_stmt.isNull(_columnIndexOfOrderId)) {
            _tmpOrderId = null
          } else {
            _tmpOrderId = _stmt.getText(_columnIndexOfOrderId)
          }
          val _tmpReviewerId: String
          _tmpReviewerId = _stmt.getText(_columnIndexOfReviewerId)
          val _tmpRating: Int
          _tmpRating = _stmt.getLong(_columnIndexOfRating).toInt()
          val _tmpTitle: String?
          if (_stmt.isNull(_columnIndexOfTitle)) {
            _tmpTitle = null
          } else {
            _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          }
          val _tmpContent: String?
          if (_stmt.isNull(_columnIndexOfContent)) {
            _tmpContent = null
          } else {
            _tmpContent = _stmt.getText(_columnIndexOfContent)
          }
          val _tmpIsVerifiedPurchase: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsVerifiedPurchase).toInt()
          _tmpIsVerifiedPurchase = _tmp != 0
          val _tmpHelpfulCount: Int
          _tmpHelpfulCount = _stmt.getLong(_columnIndexOfHelpfulCount).toInt()
          val _tmpResponseFromSeller: String?
          if (_stmt.isNull(_columnIndexOfResponseFromSeller)) {
            _tmpResponseFromSeller = null
          } else {
            _tmpResponseFromSeller = _stmt.getText(_columnIndexOfResponseFromSeller)
          }
          val _tmpResponseAt: Long?
          if (_stmt.isNull(_columnIndexOfResponseAt)) {
            _tmpResponseAt = null
          } else {
            _tmpResponseAt = _stmt.getLong(_columnIndexOfResponseAt)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpIsDeleted: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfIsDeleted).toInt()
          _tmpIsDeleted = _tmp_1 != 0
          val _tmpDirty: Boolean
          val _tmp_2: Int
          _tmp_2 = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp_2 != 0
          val _tmpAdminFlagged: Boolean
          val _tmp_3: Int
          _tmp_3 = _stmt.getLong(_columnIndexOfAdminFlagged).toInt()
          _tmpAdminFlagged = _tmp_3 != 0
          val _tmpModerationNote: String?
          if (_stmt.isNull(_columnIndexOfModerationNote)) {
            _tmpModerationNote = null
          } else {
            _tmpModerationNote = _stmt.getText(_columnIndexOfModerationNote)
          }
          _item =
              ReviewEntity(_tmpReviewId,_tmpProductId,_tmpSellerId,_tmpOrderId,_tmpReviewerId,_tmpRating,_tmpTitle,_tmpContent,_tmpIsVerifiedPurchase,_tmpHelpfulCount,_tmpResponseFromSeller,_tmpResponseAt,_tmpCreatedAt,_tmpUpdatedAt,_tmpIsDeleted,_tmpDirty,_tmpAdminFlagged,_tmpModerationNote)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun removeHelpful(reviewId: String, userId: String) {
    val _sql: String = "DELETE FROM review_helpful WHERE reviewId = ? AND userId = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, reviewId)
        _argIndex = 2
        _stmt.bindText(_argIndex, userId)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun incrementHelpful(reviewId: String) {
    val _sql: String = "UPDATE reviews SET helpfulCount = helpfulCount + 1 WHERE reviewId = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, reviewId)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun decrementHelpful(reviewId: String) {
    val _sql: String =
        "UPDATE reviews SET helpfulCount = helpfulCount - 1 WHERE reviewId = ? AND helpfulCount > 0"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, reviewId)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun addSellerResponse(
    reviewId: String,
    response: String,
    responseAt: Long,
    updatedAt: Long,
  ) {
    val _sql: String =
        "UPDATE reviews SET responseFromSeller = ?, responseAt = ?, updatedAt = ? WHERE reviewId = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, response)
        _argIndex = 2
        _stmt.bindLong(_argIndex, responseAt)
        _argIndex = 3
        _stmt.bindLong(_argIndex, updatedAt)
        _argIndex = 4
        _stmt.bindText(_argIndex, reviewId)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun softDelete(reviewId: String, deletedAt: Long) {
    val _sql: String =
        "UPDATE reviews SET isDeleted = 1, updatedAt = ?, dirty = 1 WHERE reviewId = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, deletedAt)
        _argIndex = 2
        _stmt.bindText(_argIndex, reviewId)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun markClean(reviewId: String) {
    val _sql: String = "UPDATE reviews SET dirty = 0 WHERE reviewId = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, reviewId)
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
