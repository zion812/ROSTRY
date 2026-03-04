package com.rio.rostry.`data`.database.dao

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.appendPlaceholders
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.rio.rostry.`data`.database.entity.ShowRecordEntity
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
public class ShowRecordDao_Impl(
  __db: RoomDatabase,
) : ShowRecordDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfShowRecordEntity: EntityInsertAdapter<ShowRecordEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfShowRecordEntity = object : EntityInsertAdapter<ShowRecordEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `show_records` (`recordId`,`productId`,`ownerId`,`recordType`,`eventName`,`eventLocation`,`eventDate`,`result`,`placement`,`totalParticipants`,`category`,`score`,`opponentName`,`opponentOwnerName`,`judgesNotes`,`awards`,`photoUrls`,`isVerified`,`verifiedBy`,`certificateUrl`,`notes`,`createdAt`,`updatedAt`,`isDeleted`,`deletedAt`,`dirty`,`syncedAt`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: ShowRecordEntity) {
        statement.bindText(1, entity.recordId)
        statement.bindText(2, entity.productId)
        statement.bindText(3, entity.ownerId)
        statement.bindText(4, entity.recordType)
        statement.bindText(5, entity.eventName)
        val _tmpEventLocation: String? = entity.eventLocation
        if (_tmpEventLocation == null) {
          statement.bindNull(6)
        } else {
          statement.bindText(6, _tmpEventLocation)
        }
        statement.bindLong(7, entity.eventDate)
        statement.bindText(8, entity.result)
        val _tmpPlacement: Int? = entity.placement
        if (_tmpPlacement == null) {
          statement.bindNull(9)
        } else {
          statement.bindLong(9, _tmpPlacement.toLong())
        }
        val _tmpTotalParticipants: Int? = entity.totalParticipants
        if (_tmpTotalParticipants == null) {
          statement.bindNull(10)
        } else {
          statement.bindLong(10, _tmpTotalParticipants.toLong())
        }
        val _tmpCategory: String? = entity.category
        if (_tmpCategory == null) {
          statement.bindNull(11)
        } else {
          statement.bindText(11, _tmpCategory)
        }
        val _tmpScore: Double? = entity.score
        if (_tmpScore == null) {
          statement.bindNull(12)
        } else {
          statement.bindDouble(12, _tmpScore)
        }
        val _tmpOpponentName: String? = entity.opponentName
        if (_tmpOpponentName == null) {
          statement.bindNull(13)
        } else {
          statement.bindText(13, _tmpOpponentName)
        }
        val _tmpOpponentOwnerName: String? = entity.opponentOwnerName
        if (_tmpOpponentOwnerName == null) {
          statement.bindNull(14)
        } else {
          statement.bindText(14, _tmpOpponentOwnerName)
        }
        val _tmpJudgesNotes: String? = entity.judgesNotes
        if (_tmpJudgesNotes == null) {
          statement.bindNull(15)
        } else {
          statement.bindText(15, _tmpJudgesNotes)
        }
        val _tmpAwards: String? = entity.awards
        if (_tmpAwards == null) {
          statement.bindNull(16)
        } else {
          statement.bindText(16, _tmpAwards)
        }
        statement.bindText(17, entity.photoUrls)
        val _tmp: Int = if (entity.isVerified) 1 else 0
        statement.bindLong(18, _tmp.toLong())
        val _tmpVerifiedBy: String? = entity.verifiedBy
        if (_tmpVerifiedBy == null) {
          statement.bindNull(19)
        } else {
          statement.bindText(19, _tmpVerifiedBy)
        }
        val _tmpCertificateUrl: String? = entity.certificateUrl
        if (_tmpCertificateUrl == null) {
          statement.bindNull(20)
        } else {
          statement.bindText(20, _tmpCertificateUrl)
        }
        val _tmpNotes: String? = entity.notes
        if (_tmpNotes == null) {
          statement.bindNull(21)
        } else {
          statement.bindText(21, _tmpNotes)
        }
        statement.bindLong(22, entity.createdAt)
        statement.bindLong(23, entity.updatedAt)
        val _tmp_1: Int = if (entity.isDeleted) 1 else 0
        statement.bindLong(24, _tmp_1.toLong())
        val _tmpDeletedAt: Long? = entity.deletedAt
        if (_tmpDeletedAt == null) {
          statement.bindNull(25)
        } else {
          statement.bindLong(25, _tmpDeletedAt)
        }
        val _tmp_2: Int = if (entity.dirty) 1 else 0
        statement.bindLong(26, _tmp_2.toLong())
        val _tmpSyncedAt: Long? = entity.syncedAt
        if (_tmpSyncedAt == null) {
          statement.bindNull(27)
        } else {
          statement.bindLong(27, _tmpSyncedAt)
        }
      }
    }
  }

  public override suspend fun upsert(record: ShowRecordEntity): Unit = performSuspending(__db,
      false, true) { _connection ->
    __insertAdapterOfShowRecordEntity.insert(_connection, record)
  }

  public override suspend fun upsertAll(records: List<ShowRecordEntity>): Unit =
      performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfShowRecordEntity.insert(_connection, records)
  }

  public override suspend fun findById(recordId: String): ShowRecordEntity? {
    val _sql: String = "SELECT * FROM show_records WHERE recordId = ? LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, recordId)
        val _columnIndexOfRecordId: Int = getColumnIndexOrThrow(_stmt, "recordId")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfOwnerId: Int = getColumnIndexOrThrow(_stmt, "ownerId")
        val _columnIndexOfRecordType: Int = getColumnIndexOrThrow(_stmt, "recordType")
        val _columnIndexOfEventName: Int = getColumnIndexOrThrow(_stmt, "eventName")
        val _columnIndexOfEventLocation: Int = getColumnIndexOrThrow(_stmt, "eventLocation")
        val _columnIndexOfEventDate: Int = getColumnIndexOrThrow(_stmt, "eventDate")
        val _columnIndexOfResult: Int = getColumnIndexOrThrow(_stmt, "result")
        val _columnIndexOfPlacement: Int = getColumnIndexOrThrow(_stmt, "placement")
        val _columnIndexOfTotalParticipants: Int = getColumnIndexOrThrow(_stmt, "totalParticipants")
        val _columnIndexOfCategory: Int = getColumnIndexOrThrow(_stmt, "category")
        val _columnIndexOfScore: Int = getColumnIndexOrThrow(_stmt, "score")
        val _columnIndexOfOpponentName: Int = getColumnIndexOrThrow(_stmt, "opponentName")
        val _columnIndexOfOpponentOwnerName: Int = getColumnIndexOrThrow(_stmt, "opponentOwnerName")
        val _columnIndexOfJudgesNotes: Int = getColumnIndexOrThrow(_stmt, "judgesNotes")
        val _columnIndexOfAwards: Int = getColumnIndexOrThrow(_stmt, "awards")
        val _columnIndexOfPhotoUrls: Int = getColumnIndexOrThrow(_stmt, "photoUrls")
        val _columnIndexOfIsVerified: Int = getColumnIndexOrThrow(_stmt, "isVerified")
        val _columnIndexOfVerifiedBy: Int = getColumnIndexOrThrow(_stmt, "verifiedBy")
        val _columnIndexOfCertificateUrl: Int = getColumnIndexOrThrow(_stmt, "certificateUrl")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfIsDeleted: Int = getColumnIndexOrThrow(_stmt, "isDeleted")
        val _columnIndexOfDeletedAt: Int = getColumnIndexOrThrow(_stmt, "deletedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _result: ShowRecordEntity?
        if (_stmt.step()) {
          val _tmpRecordId: String
          _tmpRecordId = _stmt.getText(_columnIndexOfRecordId)
          val _tmpProductId: String
          _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          val _tmpOwnerId: String
          _tmpOwnerId = _stmt.getText(_columnIndexOfOwnerId)
          val _tmpRecordType: String
          _tmpRecordType = _stmt.getText(_columnIndexOfRecordType)
          val _tmpEventName: String
          _tmpEventName = _stmt.getText(_columnIndexOfEventName)
          val _tmpEventLocation: String?
          if (_stmt.isNull(_columnIndexOfEventLocation)) {
            _tmpEventLocation = null
          } else {
            _tmpEventLocation = _stmt.getText(_columnIndexOfEventLocation)
          }
          val _tmpEventDate: Long
          _tmpEventDate = _stmt.getLong(_columnIndexOfEventDate)
          val _tmpResult: String
          _tmpResult = _stmt.getText(_columnIndexOfResult)
          val _tmpPlacement: Int?
          if (_stmt.isNull(_columnIndexOfPlacement)) {
            _tmpPlacement = null
          } else {
            _tmpPlacement = _stmt.getLong(_columnIndexOfPlacement).toInt()
          }
          val _tmpTotalParticipants: Int?
          if (_stmt.isNull(_columnIndexOfTotalParticipants)) {
            _tmpTotalParticipants = null
          } else {
            _tmpTotalParticipants = _stmt.getLong(_columnIndexOfTotalParticipants).toInt()
          }
          val _tmpCategory: String?
          if (_stmt.isNull(_columnIndexOfCategory)) {
            _tmpCategory = null
          } else {
            _tmpCategory = _stmt.getText(_columnIndexOfCategory)
          }
          val _tmpScore: Double?
          if (_stmt.isNull(_columnIndexOfScore)) {
            _tmpScore = null
          } else {
            _tmpScore = _stmt.getDouble(_columnIndexOfScore)
          }
          val _tmpOpponentName: String?
          if (_stmt.isNull(_columnIndexOfOpponentName)) {
            _tmpOpponentName = null
          } else {
            _tmpOpponentName = _stmt.getText(_columnIndexOfOpponentName)
          }
          val _tmpOpponentOwnerName: String?
          if (_stmt.isNull(_columnIndexOfOpponentOwnerName)) {
            _tmpOpponentOwnerName = null
          } else {
            _tmpOpponentOwnerName = _stmt.getText(_columnIndexOfOpponentOwnerName)
          }
          val _tmpJudgesNotes: String?
          if (_stmt.isNull(_columnIndexOfJudgesNotes)) {
            _tmpJudgesNotes = null
          } else {
            _tmpJudgesNotes = _stmt.getText(_columnIndexOfJudgesNotes)
          }
          val _tmpAwards: String?
          if (_stmt.isNull(_columnIndexOfAwards)) {
            _tmpAwards = null
          } else {
            _tmpAwards = _stmt.getText(_columnIndexOfAwards)
          }
          val _tmpPhotoUrls: String
          _tmpPhotoUrls = _stmt.getText(_columnIndexOfPhotoUrls)
          val _tmpIsVerified: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsVerified).toInt()
          _tmpIsVerified = _tmp != 0
          val _tmpVerifiedBy: String?
          if (_stmt.isNull(_columnIndexOfVerifiedBy)) {
            _tmpVerifiedBy = null
          } else {
            _tmpVerifiedBy = _stmt.getText(_columnIndexOfVerifiedBy)
          }
          val _tmpCertificateUrl: String?
          if (_stmt.isNull(_columnIndexOfCertificateUrl)) {
            _tmpCertificateUrl = null
          } else {
            _tmpCertificateUrl = _stmt.getText(_columnIndexOfCertificateUrl)
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
          val _tmpIsDeleted: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfIsDeleted).toInt()
          _tmpIsDeleted = _tmp_1 != 0
          val _tmpDeletedAt: Long?
          if (_stmt.isNull(_columnIndexOfDeletedAt)) {
            _tmpDeletedAt = null
          } else {
            _tmpDeletedAt = _stmt.getLong(_columnIndexOfDeletedAt)
          }
          val _tmpDirty: Boolean
          val _tmp_2: Int
          _tmp_2 = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp_2 != 0
          val _tmpSyncedAt: Long?
          if (_stmt.isNull(_columnIndexOfSyncedAt)) {
            _tmpSyncedAt = null
          } else {
            _tmpSyncedAt = _stmt.getLong(_columnIndexOfSyncedAt)
          }
          _result =
              ShowRecordEntity(_tmpRecordId,_tmpProductId,_tmpOwnerId,_tmpRecordType,_tmpEventName,_tmpEventLocation,_tmpEventDate,_tmpResult,_tmpPlacement,_tmpTotalParticipants,_tmpCategory,_tmpScore,_tmpOpponentName,_tmpOpponentOwnerName,_tmpJudgesNotes,_tmpAwards,_tmpPhotoUrls,_tmpIsVerified,_tmpVerifiedBy,_tmpCertificateUrl,_tmpNotes,_tmpCreatedAt,_tmpUpdatedAt,_tmpIsDeleted,_tmpDeletedAt,_tmpDirty,_tmpSyncedAt)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getByProduct(productId: String): List<ShowRecordEntity> {
    val _sql: String =
        "SELECT * FROM show_records WHERE productId = ? AND isDeleted = 0 ORDER BY eventDate DESC"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, productId)
        val _columnIndexOfRecordId: Int = getColumnIndexOrThrow(_stmt, "recordId")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfOwnerId: Int = getColumnIndexOrThrow(_stmt, "ownerId")
        val _columnIndexOfRecordType: Int = getColumnIndexOrThrow(_stmt, "recordType")
        val _columnIndexOfEventName: Int = getColumnIndexOrThrow(_stmt, "eventName")
        val _columnIndexOfEventLocation: Int = getColumnIndexOrThrow(_stmt, "eventLocation")
        val _columnIndexOfEventDate: Int = getColumnIndexOrThrow(_stmt, "eventDate")
        val _columnIndexOfResult: Int = getColumnIndexOrThrow(_stmt, "result")
        val _columnIndexOfPlacement: Int = getColumnIndexOrThrow(_stmt, "placement")
        val _columnIndexOfTotalParticipants: Int = getColumnIndexOrThrow(_stmt, "totalParticipants")
        val _columnIndexOfCategory: Int = getColumnIndexOrThrow(_stmt, "category")
        val _columnIndexOfScore: Int = getColumnIndexOrThrow(_stmt, "score")
        val _columnIndexOfOpponentName: Int = getColumnIndexOrThrow(_stmt, "opponentName")
        val _columnIndexOfOpponentOwnerName: Int = getColumnIndexOrThrow(_stmt, "opponentOwnerName")
        val _columnIndexOfJudgesNotes: Int = getColumnIndexOrThrow(_stmt, "judgesNotes")
        val _columnIndexOfAwards: Int = getColumnIndexOrThrow(_stmt, "awards")
        val _columnIndexOfPhotoUrls: Int = getColumnIndexOrThrow(_stmt, "photoUrls")
        val _columnIndexOfIsVerified: Int = getColumnIndexOrThrow(_stmt, "isVerified")
        val _columnIndexOfVerifiedBy: Int = getColumnIndexOrThrow(_stmt, "verifiedBy")
        val _columnIndexOfCertificateUrl: Int = getColumnIndexOrThrow(_stmt, "certificateUrl")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfIsDeleted: Int = getColumnIndexOrThrow(_stmt, "isDeleted")
        val _columnIndexOfDeletedAt: Int = getColumnIndexOrThrow(_stmt, "deletedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _result: MutableList<ShowRecordEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: ShowRecordEntity
          val _tmpRecordId: String
          _tmpRecordId = _stmt.getText(_columnIndexOfRecordId)
          val _tmpProductId: String
          _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          val _tmpOwnerId: String
          _tmpOwnerId = _stmt.getText(_columnIndexOfOwnerId)
          val _tmpRecordType: String
          _tmpRecordType = _stmt.getText(_columnIndexOfRecordType)
          val _tmpEventName: String
          _tmpEventName = _stmt.getText(_columnIndexOfEventName)
          val _tmpEventLocation: String?
          if (_stmt.isNull(_columnIndexOfEventLocation)) {
            _tmpEventLocation = null
          } else {
            _tmpEventLocation = _stmt.getText(_columnIndexOfEventLocation)
          }
          val _tmpEventDate: Long
          _tmpEventDate = _stmt.getLong(_columnIndexOfEventDate)
          val _tmpResult: String
          _tmpResult = _stmt.getText(_columnIndexOfResult)
          val _tmpPlacement: Int?
          if (_stmt.isNull(_columnIndexOfPlacement)) {
            _tmpPlacement = null
          } else {
            _tmpPlacement = _stmt.getLong(_columnIndexOfPlacement).toInt()
          }
          val _tmpTotalParticipants: Int?
          if (_stmt.isNull(_columnIndexOfTotalParticipants)) {
            _tmpTotalParticipants = null
          } else {
            _tmpTotalParticipants = _stmt.getLong(_columnIndexOfTotalParticipants).toInt()
          }
          val _tmpCategory: String?
          if (_stmt.isNull(_columnIndexOfCategory)) {
            _tmpCategory = null
          } else {
            _tmpCategory = _stmt.getText(_columnIndexOfCategory)
          }
          val _tmpScore: Double?
          if (_stmt.isNull(_columnIndexOfScore)) {
            _tmpScore = null
          } else {
            _tmpScore = _stmt.getDouble(_columnIndexOfScore)
          }
          val _tmpOpponentName: String?
          if (_stmt.isNull(_columnIndexOfOpponentName)) {
            _tmpOpponentName = null
          } else {
            _tmpOpponentName = _stmt.getText(_columnIndexOfOpponentName)
          }
          val _tmpOpponentOwnerName: String?
          if (_stmt.isNull(_columnIndexOfOpponentOwnerName)) {
            _tmpOpponentOwnerName = null
          } else {
            _tmpOpponentOwnerName = _stmt.getText(_columnIndexOfOpponentOwnerName)
          }
          val _tmpJudgesNotes: String?
          if (_stmt.isNull(_columnIndexOfJudgesNotes)) {
            _tmpJudgesNotes = null
          } else {
            _tmpJudgesNotes = _stmt.getText(_columnIndexOfJudgesNotes)
          }
          val _tmpAwards: String?
          if (_stmt.isNull(_columnIndexOfAwards)) {
            _tmpAwards = null
          } else {
            _tmpAwards = _stmt.getText(_columnIndexOfAwards)
          }
          val _tmpPhotoUrls: String
          _tmpPhotoUrls = _stmt.getText(_columnIndexOfPhotoUrls)
          val _tmpIsVerified: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsVerified).toInt()
          _tmpIsVerified = _tmp != 0
          val _tmpVerifiedBy: String?
          if (_stmt.isNull(_columnIndexOfVerifiedBy)) {
            _tmpVerifiedBy = null
          } else {
            _tmpVerifiedBy = _stmt.getText(_columnIndexOfVerifiedBy)
          }
          val _tmpCertificateUrl: String?
          if (_stmt.isNull(_columnIndexOfCertificateUrl)) {
            _tmpCertificateUrl = null
          } else {
            _tmpCertificateUrl = _stmt.getText(_columnIndexOfCertificateUrl)
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
          val _tmpIsDeleted: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfIsDeleted).toInt()
          _tmpIsDeleted = _tmp_1 != 0
          val _tmpDeletedAt: Long?
          if (_stmt.isNull(_columnIndexOfDeletedAt)) {
            _tmpDeletedAt = null
          } else {
            _tmpDeletedAt = _stmt.getLong(_columnIndexOfDeletedAt)
          }
          val _tmpDirty: Boolean
          val _tmp_2: Int
          _tmp_2 = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp_2 != 0
          val _tmpSyncedAt: Long?
          if (_stmt.isNull(_columnIndexOfSyncedAt)) {
            _tmpSyncedAt = null
          } else {
            _tmpSyncedAt = _stmt.getLong(_columnIndexOfSyncedAt)
          }
          _item =
              ShowRecordEntity(_tmpRecordId,_tmpProductId,_tmpOwnerId,_tmpRecordType,_tmpEventName,_tmpEventLocation,_tmpEventDate,_tmpResult,_tmpPlacement,_tmpTotalParticipants,_tmpCategory,_tmpScore,_tmpOpponentName,_tmpOpponentOwnerName,_tmpJudgesNotes,_tmpAwards,_tmpPhotoUrls,_tmpIsVerified,_tmpVerifiedBy,_tmpCertificateUrl,_tmpNotes,_tmpCreatedAt,_tmpUpdatedAt,_tmpIsDeleted,_tmpDeletedAt,_tmpDirty,_tmpSyncedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeByProduct(productId: String): Flow<List<ShowRecordEntity>> {
    val _sql: String =
        "SELECT * FROM show_records WHERE productId = ? AND isDeleted = 0 ORDER BY eventDate DESC"
    return createFlow(__db, false, arrayOf("show_records")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, productId)
        val _columnIndexOfRecordId: Int = getColumnIndexOrThrow(_stmt, "recordId")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfOwnerId: Int = getColumnIndexOrThrow(_stmt, "ownerId")
        val _columnIndexOfRecordType: Int = getColumnIndexOrThrow(_stmt, "recordType")
        val _columnIndexOfEventName: Int = getColumnIndexOrThrow(_stmt, "eventName")
        val _columnIndexOfEventLocation: Int = getColumnIndexOrThrow(_stmt, "eventLocation")
        val _columnIndexOfEventDate: Int = getColumnIndexOrThrow(_stmt, "eventDate")
        val _columnIndexOfResult: Int = getColumnIndexOrThrow(_stmt, "result")
        val _columnIndexOfPlacement: Int = getColumnIndexOrThrow(_stmt, "placement")
        val _columnIndexOfTotalParticipants: Int = getColumnIndexOrThrow(_stmt, "totalParticipants")
        val _columnIndexOfCategory: Int = getColumnIndexOrThrow(_stmt, "category")
        val _columnIndexOfScore: Int = getColumnIndexOrThrow(_stmt, "score")
        val _columnIndexOfOpponentName: Int = getColumnIndexOrThrow(_stmt, "opponentName")
        val _columnIndexOfOpponentOwnerName: Int = getColumnIndexOrThrow(_stmt, "opponentOwnerName")
        val _columnIndexOfJudgesNotes: Int = getColumnIndexOrThrow(_stmt, "judgesNotes")
        val _columnIndexOfAwards: Int = getColumnIndexOrThrow(_stmt, "awards")
        val _columnIndexOfPhotoUrls: Int = getColumnIndexOrThrow(_stmt, "photoUrls")
        val _columnIndexOfIsVerified: Int = getColumnIndexOrThrow(_stmt, "isVerified")
        val _columnIndexOfVerifiedBy: Int = getColumnIndexOrThrow(_stmt, "verifiedBy")
        val _columnIndexOfCertificateUrl: Int = getColumnIndexOrThrow(_stmt, "certificateUrl")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfIsDeleted: Int = getColumnIndexOrThrow(_stmt, "isDeleted")
        val _columnIndexOfDeletedAt: Int = getColumnIndexOrThrow(_stmt, "deletedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _result: MutableList<ShowRecordEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: ShowRecordEntity
          val _tmpRecordId: String
          _tmpRecordId = _stmt.getText(_columnIndexOfRecordId)
          val _tmpProductId: String
          _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          val _tmpOwnerId: String
          _tmpOwnerId = _stmt.getText(_columnIndexOfOwnerId)
          val _tmpRecordType: String
          _tmpRecordType = _stmt.getText(_columnIndexOfRecordType)
          val _tmpEventName: String
          _tmpEventName = _stmt.getText(_columnIndexOfEventName)
          val _tmpEventLocation: String?
          if (_stmt.isNull(_columnIndexOfEventLocation)) {
            _tmpEventLocation = null
          } else {
            _tmpEventLocation = _stmt.getText(_columnIndexOfEventLocation)
          }
          val _tmpEventDate: Long
          _tmpEventDate = _stmt.getLong(_columnIndexOfEventDate)
          val _tmpResult: String
          _tmpResult = _stmt.getText(_columnIndexOfResult)
          val _tmpPlacement: Int?
          if (_stmt.isNull(_columnIndexOfPlacement)) {
            _tmpPlacement = null
          } else {
            _tmpPlacement = _stmt.getLong(_columnIndexOfPlacement).toInt()
          }
          val _tmpTotalParticipants: Int?
          if (_stmt.isNull(_columnIndexOfTotalParticipants)) {
            _tmpTotalParticipants = null
          } else {
            _tmpTotalParticipants = _stmt.getLong(_columnIndexOfTotalParticipants).toInt()
          }
          val _tmpCategory: String?
          if (_stmt.isNull(_columnIndexOfCategory)) {
            _tmpCategory = null
          } else {
            _tmpCategory = _stmt.getText(_columnIndexOfCategory)
          }
          val _tmpScore: Double?
          if (_stmt.isNull(_columnIndexOfScore)) {
            _tmpScore = null
          } else {
            _tmpScore = _stmt.getDouble(_columnIndexOfScore)
          }
          val _tmpOpponentName: String?
          if (_stmt.isNull(_columnIndexOfOpponentName)) {
            _tmpOpponentName = null
          } else {
            _tmpOpponentName = _stmt.getText(_columnIndexOfOpponentName)
          }
          val _tmpOpponentOwnerName: String?
          if (_stmt.isNull(_columnIndexOfOpponentOwnerName)) {
            _tmpOpponentOwnerName = null
          } else {
            _tmpOpponentOwnerName = _stmt.getText(_columnIndexOfOpponentOwnerName)
          }
          val _tmpJudgesNotes: String?
          if (_stmt.isNull(_columnIndexOfJudgesNotes)) {
            _tmpJudgesNotes = null
          } else {
            _tmpJudgesNotes = _stmt.getText(_columnIndexOfJudgesNotes)
          }
          val _tmpAwards: String?
          if (_stmt.isNull(_columnIndexOfAwards)) {
            _tmpAwards = null
          } else {
            _tmpAwards = _stmt.getText(_columnIndexOfAwards)
          }
          val _tmpPhotoUrls: String
          _tmpPhotoUrls = _stmt.getText(_columnIndexOfPhotoUrls)
          val _tmpIsVerified: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsVerified).toInt()
          _tmpIsVerified = _tmp != 0
          val _tmpVerifiedBy: String?
          if (_stmt.isNull(_columnIndexOfVerifiedBy)) {
            _tmpVerifiedBy = null
          } else {
            _tmpVerifiedBy = _stmt.getText(_columnIndexOfVerifiedBy)
          }
          val _tmpCertificateUrl: String?
          if (_stmt.isNull(_columnIndexOfCertificateUrl)) {
            _tmpCertificateUrl = null
          } else {
            _tmpCertificateUrl = _stmt.getText(_columnIndexOfCertificateUrl)
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
          val _tmpIsDeleted: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfIsDeleted).toInt()
          _tmpIsDeleted = _tmp_1 != 0
          val _tmpDeletedAt: Long?
          if (_stmt.isNull(_columnIndexOfDeletedAt)) {
            _tmpDeletedAt = null
          } else {
            _tmpDeletedAt = _stmt.getLong(_columnIndexOfDeletedAt)
          }
          val _tmpDirty: Boolean
          val _tmp_2: Int
          _tmp_2 = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp_2 != 0
          val _tmpSyncedAt: Long?
          if (_stmt.isNull(_columnIndexOfSyncedAt)) {
            _tmpSyncedAt = null
          } else {
            _tmpSyncedAt = _stmt.getLong(_columnIndexOfSyncedAt)
          }
          _item =
              ShowRecordEntity(_tmpRecordId,_tmpProductId,_tmpOwnerId,_tmpRecordType,_tmpEventName,_tmpEventLocation,_tmpEventDate,_tmpResult,_tmpPlacement,_tmpTotalParticipants,_tmpCategory,_tmpScore,_tmpOpponentName,_tmpOpponentOwnerName,_tmpJudgesNotes,_tmpAwards,_tmpPhotoUrls,_tmpIsVerified,_tmpVerifiedBy,_tmpCertificateUrl,_tmpNotes,_tmpCreatedAt,_tmpUpdatedAt,_tmpIsDeleted,_tmpDeletedAt,_tmpDirty,_tmpSyncedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getByOwner(ownerId: String): List<ShowRecordEntity> {
    val _sql: String =
        "SELECT * FROM show_records WHERE ownerId = ? AND isDeleted = 0 ORDER BY eventDate DESC"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, ownerId)
        val _columnIndexOfRecordId: Int = getColumnIndexOrThrow(_stmt, "recordId")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfOwnerId: Int = getColumnIndexOrThrow(_stmt, "ownerId")
        val _columnIndexOfRecordType: Int = getColumnIndexOrThrow(_stmt, "recordType")
        val _columnIndexOfEventName: Int = getColumnIndexOrThrow(_stmt, "eventName")
        val _columnIndexOfEventLocation: Int = getColumnIndexOrThrow(_stmt, "eventLocation")
        val _columnIndexOfEventDate: Int = getColumnIndexOrThrow(_stmt, "eventDate")
        val _columnIndexOfResult: Int = getColumnIndexOrThrow(_stmt, "result")
        val _columnIndexOfPlacement: Int = getColumnIndexOrThrow(_stmt, "placement")
        val _columnIndexOfTotalParticipants: Int = getColumnIndexOrThrow(_stmt, "totalParticipants")
        val _columnIndexOfCategory: Int = getColumnIndexOrThrow(_stmt, "category")
        val _columnIndexOfScore: Int = getColumnIndexOrThrow(_stmt, "score")
        val _columnIndexOfOpponentName: Int = getColumnIndexOrThrow(_stmt, "opponentName")
        val _columnIndexOfOpponentOwnerName: Int = getColumnIndexOrThrow(_stmt, "opponentOwnerName")
        val _columnIndexOfJudgesNotes: Int = getColumnIndexOrThrow(_stmt, "judgesNotes")
        val _columnIndexOfAwards: Int = getColumnIndexOrThrow(_stmt, "awards")
        val _columnIndexOfPhotoUrls: Int = getColumnIndexOrThrow(_stmt, "photoUrls")
        val _columnIndexOfIsVerified: Int = getColumnIndexOrThrow(_stmt, "isVerified")
        val _columnIndexOfVerifiedBy: Int = getColumnIndexOrThrow(_stmt, "verifiedBy")
        val _columnIndexOfCertificateUrl: Int = getColumnIndexOrThrow(_stmt, "certificateUrl")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfIsDeleted: Int = getColumnIndexOrThrow(_stmt, "isDeleted")
        val _columnIndexOfDeletedAt: Int = getColumnIndexOrThrow(_stmt, "deletedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _result: MutableList<ShowRecordEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: ShowRecordEntity
          val _tmpRecordId: String
          _tmpRecordId = _stmt.getText(_columnIndexOfRecordId)
          val _tmpProductId: String
          _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          val _tmpOwnerId: String
          _tmpOwnerId = _stmt.getText(_columnIndexOfOwnerId)
          val _tmpRecordType: String
          _tmpRecordType = _stmt.getText(_columnIndexOfRecordType)
          val _tmpEventName: String
          _tmpEventName = _stmt.getText(_columnIndexOfEventName)
          val _tmpEventLocation: String?
          if (_stmt.isNull(_columnIndexOfEventLocation)) {
            _tmpEventLocation = null
          } else {
            _tmpEventLocation = _stmt.getText(_columnIndexOfEventLocation)
          }
          val _tmpEventDate: Long
          _tmpEventDate = _stmt.getLong(_columnIndexOfEventDate)
          val _tmpResult: String
          _tmpResult = _stmt.getText(_columnIndexOfResult)
          val _tmpPlacement: Int?
          if (_stmt.isNull(_columnIndexOfPlacement)) {
            _tmpPlacement = null
          } else {
            _tmpPlacement = _stmt.getLong(_columnIndexOfPlacement).toInt()
          }
          val _tmpTotalParticipants: Int?
          if (_stmt.isNull(_columnIndexOfTotalParticipants)) {
            _tmpTotalParticipants = null
          } else {
            _tmpTotalParticipants = _stmt.getLong(_columnIndexOfTotalParticipants).toInt()
          }
          val _tmpCategory: String?
          if (_stmt.isNull(_columnIndexOfCategory)) {
            _tmpCategory = null
          } else {
            _tmpCategory = _stmt.getText(_columnIndexOfCategory)
          }
          val _tmpScore: Double?
          if (_stmt.isNull(_columnIndexOfScore)) {
            _tmpScore = null
          } else {
            _tmpScore = _stmt.getDouble(_columnIndexOfScore)
          }
          val _tmpOpponentName: String?
          if (_stmt.isNull(_columnIndexOfOpponentName)) {
            _tmpOpponentName = null
          } else {
            _tmpOpponentName = _stmt.getText(_columnIndexOfOpponentName)
          }
          val _tmpOpponentOwnerName: String?
          if (_stmt.isNull(_columnIndexOfOpponentOwnerName)) {
            _tmpOpponentOwnerName = null
          } else {
            _tmpOpponentOwnerName = _stmt.getText(_columnIndexOfOpponentOwnerName)
          }
          val _tmpJudgesNotes: String?
          if (_stmt.isNull(_columnIndexOfJudgesNotes)) {
            _tmpJudgesNotes = null
          } else {
            _tmpJudgesNotes = _stmt.getText(_columnIndexOfJudgesNotes)
          }
          val _tmpAwards: String?
          if (_stmt.isNull(_columnIndexOfAwards)) {
            _tmpAwards = null
          } else {
            _tmpAwards = _stmt.getText(_columnIndexOfAwards)
          }
          val _tmpPhotoUrls: String
          _tmpPhotoUrls = _stmt.getText(_columnIndexOfPhotoUrls)
          val _tmpIsVerified: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsVerified).toInt()
          _tmpIsVerified = _tmp != 0
          val _tmpVerifiedBy: String?
          if (_stmt.isNull(_columnIndexOfVerifiedBy)) {
            _tmpVerifiedBy = null
          } else {
            _tmpVerifiedBy = _stmt.getText(_columnIndexOfVerifiedBy)
          }
          val _tmpCertificateUrl: String?
          if (_stmt.isNull(_columnIndexOfCertificateUrl)) {
            _tmpCertificateUrl = null
          } else {
            _tmpCertificateUrl = _stmt.getText(_columnIndexOfCertificateUrl)
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
          val _tmpIsDeleted: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfIsDeleted).toInt()
          _tmpIsDeleted = _tmp_1 != 0
          val _tmpDeletedAt: Long?
          if (_stmt.isNull(_columnIndexOfDeletedAt)) {
            _tmpDeletedAt = null
          } else {
            _tmpDeletedAt = _stmt.getLong(_columnIndexOfDeletedAt)
          }
          val _tmpDirty: Boolean
          val _tmp_2: Int
          _tmp_2 = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp_2 != 0
          val _tmpSyncedAt: Long?
          if (_stmt.isNull(_columnIndexOfSyncedAt)) {
            _tmpSyncedAt = null
          } else {
            _tmpSyncedAt = _stmt.getLong(_columnIndexOfSyncedAt)
          }
          _item =
              ShowRecordEntity(_tmpRecordId,_tmpProductId,_tmpOwnerId,_tmpRecordType,_tmpEventName,_tmpEventLocation,_tmpEventDate,_tmpResult,_tmpPlacement,_tmpTotalParticipants,_tmpCategory,_tmpScore,_tmpOpponentName,_tmpOpponentOwnerName,_tmpJudgesNotes,_tmpAwards,_tmpPhotoUrls,_tmpIsVerified,_tmpVerifiedBy,_tmpCertificateUrl,_tmpNotes,_tmpCreatedAt,_tmpUpdatedAt,_tmpIsDeleted,_tmpDeletedAt,_tmpDirty,_tmpSyncedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeByOwner(ownerId: String): Flow<List<ShowRecordEntity>> {
    val _sql: String =
        "SELECT * FROM show_records WHERE ownerId = ? AND isDeleted = 0 ORDER BY eventDate DESC"
    return createFlow(__db, false, arrayOf("show_records")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, ownerId)
        val _columnIndexOfRecordId: Int = getColumnIndexOrThrow(_stmt, "recordId")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfOwnerId: Int = getColumnIndexOrThrow(_stmt, "ownerId")
        val _columnIndexOfRecordType: Int = getColumnIndexOrThrow(_stmt, "recordType")
        val _columnIndexOfEventName: Int = getColumnIndexOrThrow(_stmt, "eventName")
        val _columnIndexOfEventLocation: Int = getColumnIndexOrThrow(_stmt, "eventLocation")
        val _columnIndexOfEventDate: Int = getColumnIndexOrThrow(_stmt, "eventDate")
        val _columnIndexOfResult: Int = getColumnIndexOrThrow(_stmt, "result")
        val _columnIndexOfPlacement: Int = getColumnIndexOrThrow(_stmt, "placement")
        val _columnIndexOfTotalParticipants: Int = getColumnIndexOrThrow(_stmt, "totalParticipants")
        val _columnIndexOfCategory: Int = getColumnIndexOrThrow(_stmt, "category")
        val _columnIndexOfScore: Int = getColumnIndexOrThrow(_stmt, "score")
        val _columnIndexOfOpponentName: Int = getColumnIndexOrThrow(_stmt, "opponentName")
        val _columnIndexOfOpponentOwnerName: Int = getColumnIndexOrThrow(_stmt, "opponentOwnerName")
        val _columnIndexOfJudgesNotes: Int = getColumnIndexOrThrow(_stmt, "judgesNotes")
        val _columnIndexOfAwards: Int = getColumnIndexOrThrow(_stmt, "awards")
        val _columnIndexOfPhotoUrls: Int = getColumnIndexOrThrow(_stmt, "photoUrls")
        val _columnIndexOfIsVerified: Int = getColumnIndexOrThrow(_stmt, "isVerified")
        val _columnIndexOfVerifiedBy: Int = getColumnIndexOrThrow(_stmt, "verifiedBy")
        val _columnIndexOfCertificateUrl: Int = getColumnIndexOrThrow(_stmt, "certificateUrl")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfIsDeleted: Int = getColumnIndexOrThrow(_stmt, "isDeleted")
        val _columnIndexOfDeletedAt: Int = getColumnIndexOrThrow(_stmt, "deletedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _result: MutableList<ShowRecordEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: ShowRecordEntity
          val _tmpRecordId: String
          _tmpRecordId = _stmt.getText(_columnIndexOfRecordId)
          val _tmpProductId: String
          _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          val _tmpOwnerId: String
          _tmpOwnerId = _stmt.getText(_columnIndexOfOwnerId)
          val _tmpRecordType: String
          _tmpRecordType = _stmt.getText(_columnIndexOfRecordType)
          val _tmpEventName: String
          _tmpEventName = _stmt.getText(_columnIndexOfEventName)
          val _tmpEventLocation: String?
          if (_stmt.isNull(_columnIndexOfEventLocation)) {
            _tmpEventLocation = null
          } else {
            _tmpEventLocation = _stmt.getText(_columnIndexOfEventLocation)
          }
          val _tmpEventDate: Long
          _tmpEventDate = _stmt.getLong(_columnIndexOfEventDate)
          val _tmpResult: String
          _tmpResult = _stmt.getText(_columnIndexOfResult)
          val _tmpPlacement: Int?
          if (_stmt.isNull(_columnIndexOfPlacement)) {
            _tmpPlacement = null
          } else {
            _tmpPlacement = _stmt.getLong(_columnIndexOfPlacement).toInt()
          }
          val _tmpTotalParticipants: Int?
          if (_stmt.isNull(_columnIndexOfTotalParticipants)) {
            _tmpTotalParticipants = null
          } else {
            _tmpTotalParticipants = _stmt.getLong(_columnIndexOfTotalParticipants).toInt()
          }
          val _tmpCategory: String?
          if (_stmt.isNull(_columnIndexOfCategory)) {
            _tmpCategory = null
          } else {
            _tmpCategory = _stmt.getText(_columnIndexOfCategory)
          }
          val _tmpScore: Double?
          if (_stmt.isNull(_columnIndexOfScore)) {
            _tmpScore = null
          } else {
            _tmpScore = _stmt.getDouble(_columnIndexOfScore)
          }
          val _tmpOpponentName: String?
          if (_stmt.isNull(_columnIndexOfOpponentName)) {
            _tmpOpponentName = null
          } else {
            _tmpOpponentName = _stmt.getText(_columnIndexOfOpponentName)
          }
          val _tmpOpponentOwnerName: String?
          if (_stmt.isNull(_columnIndexOfOpponentOwnerName)) {
            _tmpOpponentOwnerName = null
          } else {
            _tmpOpponentOwnerName = _stmt.getText(_columnIndexOfOpponentOwnerName)
          }
          val _tmpJudgesNotes: String?
          if (_stmt.isNull(_columnIndexOfJudgesNotes)) {
            _tmpJudgesNotes = null
          } else {
            _tmpJudgesNotes = _stmt.getText(_columnIndexOfJudgesNotes)
          }
          val _tmpAwards: String?
          if (_stmt.isNull(_columnIndexOfAwards)) {
            _tmpAwards = null
          } else {
            _tmpAwards = _stmt.getText(_columnIndexOfAwards)
          }
          val _tmpPhotoUrls: String
          _tmpPhotoUrls = _stmt.getText(_columnIndexOfPhotoUrls)
          val _tmpIsVerified: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsVerified).toInt()
          _tmpIsVerified = _tmp != 0
          val _tmpVerifiedBy: String?
          if (_stmt.isNull(_columnIndexOfVerifiedBy)) {
            _tmpVerifiedBy = null
          } else {
            _tmpVerifiedBy = _stmt.getText(_columnIndexOfVerifiedBy)
          }
          val _tmpCertificateUrl: String?
          if (_stmt.isNull(_columnIndexOfCertificateUrl)) {
            _tmpCertificateUrl = null
          } else {
            _tmpCertificateUrl = _stmt.getText(_columnIndexOfCertificateUrl)
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
          val _tmpIsDeleted: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfIsDeleted).toInt()
          _tmpIsDeleted = _tmp_1 != 0
          val _tmpDeletedAt: Long?
          if (_stmt.isNull(_columnIndexOfDeletedAt)) {
            _tmpDeletedAt = null
          } else {
            _tmpDeletedAt = _stmt.getLong(_columnIndexOfDeletedAt)
          }
          val _tmpDirty: Boolean
          val _tmp_2: Int
          _tmp_2 = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp_2 != 0
          val _tmpSyncedAt: Long?
          if (_stmt.isNull(_columnIndexOfSyncedAt)) {
            _tmpSyncedAt = null
          } else {
            _tmpSyncedAt = _stmt.getLong(_columnIndexOfSyncedAt)
          }
          _item =
              ShowRecordEntity(_tmpRecordId,_tmpProductId,_tmpOwnerId,_tmpRecordType,_tmpEventName,_tmpEventLocation,_tmpEventDate,_tmpResult,_tmpPlacement,_tmpTotalParticipants,_tmpCategory,_tmpScore,_tmpOpponentName,_tmpOpponentOwnerName,_tmpJudgesNotes,_tmpAwards,_tmpPhotoUrls,_tmpIsVerified,_tmpVerifiedBy,_tmpCertificateUrl,_tmpNotes,_tmpCreatedAt,_tmpUpdatedAt,_tmpIsDeleted,_tmpDeletedAt,_tmpDirty,_tmpSyncedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getByProductAndType(productId: String, type: String):
      List<ShowRecordEntity> {
    val _sql: String =
        "SELECT * FROM show_records WHERE productId = ? AND recordType = ? AND isDeleted = 0 ORDER BY eventDate DESC"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, productId)
        _argIndex = 2
        _stmt.bindText(_argIndex, type)
        val _columnIndexOfRecordId: Int = getColumnIndexOrThrow(_stmt, "recordId")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfOwnerId: Int = getColumnIndexOrThrow(_stmt, "ownerId")
        val _columnIndexOfRecordType: Int = getColumnIndexOrThrow(_stmt, "recordType")
        val _columnIndexOfEventName: Int = getColumnIndexOrThrow(_stmt, "eventName")
        val _columnIndexOfEventLocation: Int = getColumnIndexOrThrow(_stmt, "eventLocation")
        val _columnIndexOfEventDate: Int = getColumnIndexOrThrow(_stmt, "eventDate")
        val _columnIndexOfResult: Int = getColumnIndexOrThrow(_stmt, "result")
        val _columnIndexOfPlacement: Int = getColumnIndexOrThrow(_stmt, "placement")
        val _columnIndexOfTotalParticipants: Int = getColumnIndexOrThrow(_stmt, "totalParticipants")
        val _columnIndexOfCategory: Int = getColumnIndexOrThrow(_stmt, "category")
        val _columnIndexOfScore: Int = getColumnIndexOrThrow(_stmt, "score")
        val _columnIndexOfOpponentName: Int = getColumnIndexOrThrow(_stmt, "opponentName")
        val _columnIndexOfOpponentOwnerName: Int = getColumnIndexOrThrow(_stmt, "opponentOwnerName")
        val _columnIndexOfJudgesNotes: Int = getColumnIndexOrThrow(_stmt, "judgesNotes")
        val _columnIndexOfAwards: Int = getColumnIndexOrThrow(_stmt, "awards")
        val _columnIndexOfPhotoUrls: Int = getColumnIndexOrThrow(_stmt, "photoUrls")
        val _columnIndexOfIsVerified: Int = getColumnIndexOrThrow(_stmt, "isVerified")
        val _columnIndexOfVerifiedBy: Int = getColumnIndexOrThrow(_stmt, "verifiedBy")
        val _columnIndexOfCertificateUrl: Int = getColumnIndexOrThrow(_stmt, "certificateUrl")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfIsDeleted: Int = getColumnIndexOrThrow(_stmt, "isDeleted")
        val _columnIndexOfDeletedAt: Int = getColumnIndexOrThrow(_stmt, "deletedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _result: MutableList<ShowRecordEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: ShowRecordEntity
          val _tmpRecordId: String
          _tmpRecordId = _stmt.getText(_columnIndexOfRecordId)
          val _tmpProductId: String
          _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          val _tmpOwnerId: String
          _tmpOwnerId = _stmt.getText(_columnIndexOfOwnerId)
          val _tmpRecordType: String
          _tmpRecordType = _stmt.getText(_columnIndexOfRecordType)
          val _tmpEventName: String
          _tmpEventName = _stmt.getText(_columnIndexOfEventName)
          val _tmpEventLocation: String?
          if (_stmt.isNull(_columnIndexOfEventLocation)) {
            _tmpEventLocation = null
          } else {
            _tmpEventLocation = _stmt.getText(_columnIndexOfEventLocation)
          }
          val _tmpEventDate: Long
          _tmpEventDate = _stmt.getLong(_columnIndexOfEventDate)
          val _tmpResult: String
          _tmpResult = _stmt.getText(_columnIndexOfResult)
          val _tmpPlacement: Int?
          if (_stmt.isNull(_columnIndexOfPlacement)) {
            _tmpPlacement = null
          } else {
            _tmpPlacement = _stmt.getLong(_columnIndexOfPlacement).toInt()
          }
          val _tmpTotalParticipants: Int?
          if (_stmt.isNull(_columnIndexOfTotalParticipants)) {
            _tmpTotalParticipants = null
          } else {
            _tmpTotalParticipants = _stmt.getLong(_columnIndexOfTotalParticipants).toInt()
          }
          val _tmpCategory: String?
          if (_stmt.isNull(_columnIndexOfCategory)) {
            _tmpCategory = null
          } else {
            _tmpCategory = _stmt.getText(_columnIndexOfCategory)
          }
          val _tmpScore: Double?
          if (_stmt.isNull(_columnIndexOfScore)) {
            _tmpScore = null
          } else {
            _tmpScore = _stmt.getDouble(_columnIndexOfScore)
          }
          val _tmpOpponentName: String?
          if (_stmt.isNull(_columnIndexOfOpponentName)) {
            _tmpOpponentName = null
          } else {
            _tmpOpponentName = _stmt.getText(_columnIndexOfOpponentName)
          }
          val _tmpOpponentOwnerName: String?
          if (_stmt.isNull(_columnIndexOfOpponentOwnerName)) {
            _tmpOpponentOwnerName = null
          } else {
            _tmpOpponentOwnerName = _stmt.getText(_columnIndexOfOpponentOwnerName)
          }
          val _tmpJudgesNotes: String?
          if (_stmt.isNull(_columnIndexOfJudgesNotes)) {
            _tmpJudgesNotes = null
          } else {
            _tmpJudgesNotes = _stmt.getText(_columnIndexOfJudgesNotes)
          }
          val _tmpAwards: String?
          if (_stmt.isNull(_columnIndexOfAwards)) {
            _tmpAwards = null
          } else {
            _tmpAwards = _stmt.getText(_columnIndexOfAwards)
          }
          val _tmpPhotoUrls: String
          _tmpPhotoUrls = _stmt.getText(_columnIndexOfPhotoUrls)
          val _tmpIsVerified: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsVerified).toInt()
          _tmpIsVerified = _tmp != 0
          val _tmpVerifiedBy: String?
          if (_stmt.isNull(_columnIndexOfVerifiedBy)) {
            _tmpVerifiedBy = null
          } else {
            _tmpVerifiedBy = _stmt.getText(_columnIndexOfVerifiedBy)
          }
          val _tmpCertificateUrl: String?
          if (_stmt.isNull(_columnIndexOfCertificateUrl)) {
            _tmpCertificateUrl = null
          } else {
            _tmpCertificateUrl = _stmt.getText(_columnIndexOfCertificateUrl)
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
          val _tmpIsDeleted: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfIsDeleted).toInt()
          _tmpIsDeleted = _tmp_1 != 0
          val _tmpDeletedAt: Long?
          if (_stmt.isNull(_columnIndexOfDeletedAt)) {
            _tmpDeletedAt = null
          } else {
            _tmpDeletedAt = _stmt.getLong(_columnIndexOfDeletedAt)
          }
          val _tmpDirty: Boolean
          val _tmp_2: Int
          _tmp_2 = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp_2 != 0
          val _tmpSyncedAt: Long?
          if (_stmt.isNull(_columnIndexOfSyncedAt)) {
            _tmpSyncedAt = null
          } else {
            _tmpSyncedAt = _stmt.getLong(_columnIndexOfSyncedAt)
          }
          _item =
              ShowRecordEntity(_tmpRecordId,_tmpProductId,_tmpOwnerId,_tmpRecordType,_tmpEventName,_tmpEventLocation,_tmpEventDate,_tmpResult,_tmpPlacement,_tmpTotalParticipants,_tmpCategory,_tmpScore,_tmpOpponentName,_tmpOpponentOwnerName,_tmpJudgesNotes,_tmpAwards,_tmpPhotoUrls,_tmpIsVerified,_tmpVerifiedBy,_tmpCertificateUrl,_tmpNotes,_tmpCreatedAt,_tmpUpdatedAt,_tmpIsDeleted,_tmpDeletedAt,_tmpDirty,_tmpSyncedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun countWins(productId: String): Int {
    val _sql: String =
        "SELECT COUNT(*) FROM show_records WHERE productId = ? AND result IN ('WIN', '1ST') AND isDeleted = 0"
    return performSuspending(__db, true, false) { _connection ->
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

  public override suspend fun countPodiums(productId: String): Int {
    val _sql: String =
        "SELECT COUNT(*) FROM show_records WHERE productId = ? AND result IN ('1ST', '2ND', '3RD') AND isDeleted = 0"
    return performSuspending(__db, true, false) { _connection ->
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

  public override suspend fun countTotal(productId: String): Int {
    val _sql: String = "SELECT COUNT(*) FROM show_records WHERE productId = ? AND isDeleted = 0"
    return performSuspending(__db, true, false) { _connection ->
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

  public override suspend fun getSparringRecords(productId: String): List<ShowRecordEntity> {
    val _sql: String =
        "SELECT * FROM show_records WHERE productId = ? AND recordType = 'SPARRING' AND isDeleted = 0 ORDER BY eventDate DESC"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, productId)
        val _columnIndexOfRecordId: Int = getColumnIndexOrThrow(_stmt, "recordId")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfOwnerId: Int = getColumnIndexOrThrow(_stmt, "ownerId")
        val _columnIndexOfRecordType: Int = getColumnIndexOrThrow(_stmt, "recordType")
        val _columnIndexOfEventName: Int = getColumnIndexOrThrow(_stmt, "eventName")
        val _columnIndexOfEventLocation: Int = getColumnIndexOrThrow(_stmt, "eventLocation")
        val _columnIndexOfEventDate: Int = getColumnIndexOrThrow(_stmt, "eventDate")
        val _columnIndexOfResult: Int = getColumnIndexOrThrow(_stmt, "result")
        val _columnIndexOfPlacement: Int = getColumnIndexOrThrow(_stmt, "placement")
        val _columnIndexOfTotalParticipants: Int = getColumnIndexOrThrow(_stmt, "totalParticipants")
        val _columnIndexOfCategory: Int = getColumnIndexOrThrow(_stmt, "category")
        val _columnIndexOfScore: Int = getColumnIndexOrThrow(_stmt, "score")
        val _columnIndexOfOpponentName: Int = getColumnIndexOrThrow(_stmt, "opponentName")
        val _columnIndexOfOpponentOwnerName: Int = getColumnIndexOrThrow(_stmt, "opponentOwnerName")
        val _columnIndexOfJudgesNotes: Int = getColumnIndexOrThrow(_stmt, "judgesNotes")
        val _columnIndexOfAwards: Int = getColumnIndexOrThrow(_stmt, "awards")
        val _columnIndexOfPhotoUrls: Int = getColumnIndexOrThrow(_stmt, "photoUrls")
        val _columnIndexOfIsVerified: Int = getColumnIndexOrThrow(_stmt, "isVerified")
        val _columnIndexOfVerifiedBy: Int = getColumnIndexOrThrow(_stmt, "verifiedBy")
        val _columnIndexOfCertificateUrl: Int = getColumnIndexOrThrow(_stmt, "certificateUrl")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfIsDeleted: Int = getColumnIndexOrThrow(_stmt, "isDeleted")
        val _columnIndexOfDeletedAt: Int = getColumnIndexOrThrow(_stmt, "deletedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _result: MutableList<ShowRecordEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: ShowRecordEntity
          val _tmpRecordId: String
          _tmpRecordId = _stmt.getText(_columnIndexOfRecordId)
          val _tmpProductId: String
          _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          val _tmpOwnerId: String
          _tmpOwnerId = _stmt.getText(_columnIndexOfOwnerId)
          val _tmpRecordType: String
          _tmpRecordType = _stmt.getText(_columnIndexOfRecordType)
          val _tmpEventName: String
          _tmpEventName = _stmt.getText(_columnIndexOfEventName)
          val _tmpEventLocation: String?
          if (_stmt.isNull(_columnIndexOfEventLocation)) {
            _tmpEventLocation = null
          } else {
            _tmpEventLocation = _stmt.getText(_columnIndexOfEventLocation)
          }
          val _tmpEventDate: Long
          _tmpEventDate = _stmt.getLong(_columnIndexOfEventDate)
          val _tmpResult: String
          _tmpResult = _stmt.getText(_columnIndexOfResult)
          val _tmpPlacement: Int?
          if (_stmt.isNull(_columnIndexOfPlacement)) {
            _tmpPlacement = null
          } else {
            _tmpPlacement = _stmt.getLong(_columnIndexOfPlacement).toInt()
          }
          val _tmpTotalParticipants: Int?
          if (_stmt.isNull(_columnIndexOfTotalParticipants)) {
            _tmpTotalParticipants = null
          } else {
            _tmpTotalParticipants = _stmt.getLong(_columnIndexOfTotalParticipants).toInt()
          }
          val _tmpCategory: String?
          if (_stmt.isNull(_columnIndexOfCategory)) {
            _tmpCategory = null
          } else {
            _tmpCategory = _stmt.getText(_columnIndexOfCategory)
          }
          val _tmpScore: Double?
          if (_stmt.isNull(_columnIndexOfScore)) {
            _tmpScore = null
          } else {
            _tmpScore = _stmt.getDouble(_columnIndexOfScore)
          }
          val _tmpOpponentName: String?
          if (_stmt.isNull(_columnIndexOfOpponentName)) {
            _tmpOpponentName = null
          } else {
            _tmpOpponentName = _stmt.getText(_columnIndexOfOpponentName)
          }
          val _tmpOpponentOwnerName: String?
          if (_stmt.isNull(_columnIndexOfOpponentOwnerName)) {
            _tmpOpponentOwnerName = null
          } else {
            _tmpOpponentOwnerName = _stmt.getText(_columnIndexOfOpponentOwnerName)
          }
          val _tmpJudgesNotes: String?
          if (_stmt.isNull(_columnIndexOfJudgesNotes)) {
            _tmpJudgesNotes = null
          } else {
            _tmpJudgesNotes = _stmt.getText(_columnIndexOfJudgesNotes)
          }
          val _tmpAwards: String?
          if (_stmt.isNull(_columnIndexOfAwards)) {
            _tmpAwards = null
          } else {
            _tmpAwards = _stmt.getText(_columnIndexOfAwards)
          }
          val _tmpPhotoUrls: String
          _tmpPhotoUrls = _stmt.getText(_columnIndexOfPhotoUrls)
          val _tmpIsVerified: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsVerified).toInt()
          _tmpIsVerified = _tmp != 0
          val _tmpVerifiedBy: String?
          if (_stmt.isNull(_columnIndexOfVerifiedBy)) {
            _tmpVerifiedBy = null
          } else {
            _tmpVerifiedBy = _stmt.getText(_columnIndexOfVerifiedBy)
          }
          val _tmpCertificateUrl: String?
          if (_stmt.isNull(_columnIndexOfCertificateUrl)) {
            _tmpCertificateUrl = null
          } else {
            _tmpCertificateUrl = _stmt.getText(_columnIndexOfCertificateUrl)
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
          val _tmpIsDeleted: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfIsDeleted).toInt()
          _tmpIsDeleted = _tmp_1 != 0
          val _tmpDeletedAt: Long?
          if (_stmt.isNull(_columnIndexOfDeletedAt)) {
            _tmpDeletedAt = null
          } else {
            _tmpDeletedAt = _stmt.getLong(_columnIndexOfDeletedAt)
          }
          val _tmpDirty: Boolean
          val _tmp_2: Int
          _tmp_2 = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp_2 != 0
          val _tmpSyncedAt: Long?
          if (_stmt.isNull(_columnIndexOfSyncedAt)) {
            _tmpSyncedAt = null
          } else {
            _tmpSyncedAt = _stmt.getLong(_columnIndexOfSyncedAt)
          }
          _item =
              ShowRecordEntity(_tmpRecordId,_tmpProductId,_tmpOwnerId,_tmpRecordType,_tmpEventName,_tmpEventLocation,_tmpEventDate,_tmpResult,_tmpPlacement,_tmpTotalParticipants,_tmpCategory,_tmpScore,_tmpOpponentName,_tmpOpponentOwnerName,_tmpJudgesNotes,_tmpAwards,_tmpPhotoUrls,_tmpIsVerified,_tmpVerifiedBy,_tmpCertificateUrl,_tmpNotes,_tmpCreatedAt,_tmpUpdatedAt,_tmpIsDeleted,_tmpDeletedAt,_tmpDirty,_tmpSyncedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getCompetitionRecords(productId: String): List<ShowRecordEntity> {
    val _sql: String =
        "SELECT * FROM show_records WHERE productId = ? AND recordType IN ('SHOW', 'EXHIBITION', 'COMPETITION') AND isDeleted = 0 ORDER BY eventDate DESC"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, productId)
        val _columnIndexOfRecordId: Int = getColumnIndexOrThrow(_stmt, "recordId")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfOwnerId: Int = getColumnIndexOrThrow(_stmt, "ownerId")
        val _columnIndexOfRecordType: Int = getColumnIndexOrThrow(_stmt, "recordType")
        val _columnIndexOfEventName: Int = getColumnIndexOrThrow(_stmt, "eventName")
        val _columnIndexOfEventLocation: Int = getColumnIndexOrThrow(_stmt, "eventLocation")
        val _columnIndexOfEventDate: Int = getColumnIndexOrThrow(_stmt, "eventDate")
        val _columnIndexOfResult: Int = getColumnIndexOrThrow(_stmt, "result")
        val _columnIndexOfPlacement: Int = getColumnIndexOrThrow(_stmt, "placement")
        val _columnIndexOfTotalParticipants: Int = getColumnIndexOrThrow(_stmt, "totalParticipants")
        val _columnIndexOfCategory: Int = getColumnIndexOrThrow(_stmt, "category")
        val _columnIndexOfScore: Int = getColumnIndexOrThrow(_stmt, "score")
        val _columnIndexOfOpponentName: Int = getColumnIndexOrThrow(_stmt, "opponentName")
        val _columnIndexOfOpponentOwnerName: Int = getColumnIndexOrThrow(_stmt, "opponentOwnerName")
        val _columnIndexOfJudgesNotes: Int = getColumnIndexOrThrow(_stmt, "judgesNotes")
        val _columnIndexOfAwards: Int = getColumnIndexOrThrow(_stmt, "awards")
        val _columnIndexOfPhotoUrls: Int = getColumnIndexOrThrow(_stmt, "photoUrls")
        val _columnIndexOfIsVerified: Int = getColumnIndexOrThrow(_stmt, "isVerified")
        val _columnIndexOfVerifiedBy: Int = getColumnIndexOrThrow(_stmt, "verifiedBy")
        val _columnIndexOfCertificateUrl: Int = getColumnIndexOrThrow(_stmt, "certificateUrl")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfIsDeleted: Int = getColumnIndexOrThrow(_stmt, "isDeleted")
        val _columnIndexOfDeletedAt: Int = getColumnIndexOrThrow(_stmt, "deletedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _result: MutableList<ShowRecordEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: ShowRecordEntity
          val _tmpRecordId: String
          _tmpRecordId = _stmt.getText(_columnIndexOfRecordId)
          val _tmpProductId: String
          _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          val _tmpOwnerId: String
          _tmpOwnerId = _stmt.getText(_columnIndexOfOwnerId)
          val _tmpRecordType: String
          _tmpRecordType = _stmt.getText(_columnIndexOfRecordType)
          val _tmpEventName: String
          _tmpEventName = _stmt.getText(_columnIndexOfEventName)
          val _tmpEventLocation: String?
          if (_stmt.isNull(_columnIndexOfEventLocation)) {
            _tmpEventLocation = null
          } else {
            _tmpEventLocation = _stmt.getText(_columnIndexOfEventLocation)
          }
          val _tmpEventDate: Long
          _tmpEventDate = _stmt.getLong(_columnIndexOfEventDate)
          val _tmpResult: String
          _tmpResult = _stmt.getText(_columnIndexOfResult)
          val _tmpPlacement: Int?
          if (_stmt.isNull(_columnIndexOfPlacement)) {
            _tmpPlacement = null
          } else {
            _tmpPlacement = _stmt.getLong(_columnIndexOfPlacement).toInt()
          }
          val _tmpTotalParticipants: Int?
          if (_stmt.isNull(_columnIndexOfTotalParticipants)) {
            _tmpTotalParticipants = null
          } else {
            _tmpTotalParticipants = _stmt.getLong(_columnIndexOfTotalParticipants).toInt()
          }
          val _tmpCategory: String?
          if (_stmt.isNull(_columnIndexOfCategory)) {
            _tmpCategory = null
          } else {
            _tmpCategory = _stmt.getText(_columnIndexOfCategory)
          }
          val _tmpScore: Double?
          if (_stmt.isNull(_columnIndexOfScore)) {
            _tmpScore = null
          } else {
            _tmpScore = _stmt.getDouble(_columnIndexOfScore)
          }
          val _tmpOpponentName: String?
          if (_stmt.isNull(_columnIndexOfOpponentName)) {
            _tmpOpponentName = null
          } else {
            _tmpOpponentName = _stmt.getText(_columnIndexOfOpponentName)
          }
          val _tmpOpponentOwnerName: String?
          if (_stmt.isNull(_columnIndexOfOpponentOwnerName)) {
            _tmpOpponentOwnerName = null
          } else {
            _tmpOpponentOwnerName = _stmt.getText(_columnIndexOfOpponentOwnerName)
          }
          val _tmpJudgesNotes: String?
          if (_stmt.isNull(_columnIndexOfJudgesNotes)) {
            _tmpJudgesNotes = null
          } else {
            _tmpJudgesNotes = _stmt.getText(_columnIndexOfJudgesNotes)
          }
          val _tmpAwards: String?
          if (_stmt.isNull(_columnIndexOfAwards)) {
            _tmpAwards = null
          } else {
            _tmpAwards = _stmt.getText(_columnIndexOfAwards)
          }
          val _tmpPhotoUrls: String
          _tmpPhotoUrls = _stmt.getText(_columnIndexOfPhotoUrls)
          val _tmpIsVerified: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsVerified).toInt()
          _tmpIsVerified = _tmp != 0
          val _tmpVerifiedBy: String?
          if (_stmt.isNull(_columnIndexOfVerifiedBy)) {
            _tmpVerifiedBy = null
          } else {
            _tmpVerifiedBy = _stmt.getText(_columnIndexOfVerifiedBy)
          }
          val _tmpCertificateUrl: String?
          if (_stmt.isNull(_columnIndexOfCertificateUrl)) {
            _tmpCertificateUrl = null
          } else {
            _tmpCertificateUrl = _stmt.getText(_columnIndexOfCertificateUrl)
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
          val _tmpIsDeleted: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfIsDeleted).toInt()
          _tmpIsDeleted = _tmp_1 != 0
          val _tmpDeletedAt: Long?
          if (_stmt.isNull(_columnIndexOfDeletedAt)) {
            _tmpDeletedAt = null
          } else {
            _tmpDeletedAt = _stmt.getLong(_columnIndexOfDeletedAt)
          }
          val _tmpDirty: Boolean
          val _tmp_2: Int
          _tmp_2 = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp_2 != 0
          val _tmpSyncedAt: Long?
          if (_stmt.isNull(_columnIndexOfSyncedAt)) {
            _tmpSyncedAt = null
          } else {
            _tmpSyncedAt = _stmt.getLong(_columnIndexOfSyncedAt)
          }
          _item =
              ShowRecordEntity(_tmpRecordId,_tmpProductId,_tmpOwnerId,_tmpRecordType,_tmpEventName,_tmpEventLocation,_tmpEventDate,_tmpResult,_tmpPlacement,_tmpTotalParticipants,_tmpCategory,_tmpScore,_tmpOpponentName,_tmpOpponentOwnerName,_tmpJudgesNotes,_tmpAwards,_tmpPhotoUrls,_tmpIsVerified,_tmpVerifiedBy,_tmpCertificateUrl,_tmpNotes,_tmpCreatedAt,_tmpUpdatedAt,_tmpIsDeleted,_tmpDeletedAt,_tmpDirty,_tmpSyncedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getStatsByProduct(productId: String): List<ShowRecordStats> {
    val _sql: String = """
        |
        |        SELECT recordType, 
        |               COUNT(*) as total,
        |               SUM(CASE WHEN result IN ('WIN', '1ST') THEN 1 ELSE 0 END) as wins,
        |               SUM(CASE WHEN result IN ('1ST', '2ND', '3RD') THEN 1 ELSE 0 END) as podiums
        |        FROM show_records 
        |        WHERE productId = ? AND isDeleted = 0 
        |        GROUP BY recordType
        |    
        """.trimMargin()
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, productId)
        val _columnIndexOfRecordType: Int = 0
        val _columnIndexOfTotal: Int = 1
        val _columnIndexOfWins: Int = 2
        val _columnIndexOfPodiums: Int = 3
        val _result: MutableList<ShowRecordStats> = mutableListOf()
        while (_stmt.step()) {
          val _item: ShowRecordStats
          val _tmpRecordType: String
          _tmpRecordType = _stmt.getText(_columnIndexOfRecordType)
          val _tmpTotal: Int
          _tmpTotal = _stmt.getLong(_columnIndexOfTotal).toInt()
          val _tmpWins: Int
          _tmpWins = _stmt.getLong(_columnIndexOfWins).toInt()
          val _tmpPodiums: Int
          _tmpPodiums = _stmt.getLong(_columnIndexOfPodiums).toInt()
          _item = ShowRecordStats(_tmpRecordType,_tmpTotal,_tmpWins,_tmpPodiums)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getDirty(limit: Int): List<ShowRecordEntity> {
    val _sql: String = "SELECT * FROM show_records WHERE dirty = 1 ORDER BY updatedAt ASC LIMIT ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, limit.toLong())
        val _columnIndexOfRecordId: Int = getColumnIndexOrThrow(_stmt, "recordId")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfOwnerId: Int = getColumnIndexOrThrow(_stmt, "ownerId")
        val _columnIndexOfRecordType: Int = getColumnIndexOrThrow(_stmt, "recordType")
        val _columnIndexOfEventName: Int = getColumnIndexOrThrow(_stmt, "eventName")
        val _columnIndexOfEventLocation: Int = getColumnIndexOrThrow(_stmt, "eventLocation")
        val _columnIndexOfEventDate: Int = getColumnIndexOrThrow(_stmt, "eventDate")
        val _columnIndexOfResult: Int = getColumnIndexOrThrow(_stmt, "result")
        val _columnIndexOfPlacement: Int = getColumnIndexOrThrow(_stmt, "placement")
        val _columnIndexOfTotalParticipants: Int = getColumnIndexOrThrow(_stmt, "totalParticipants")
        val _columnIndexOfCategory: Int = getColumnIndexOrThrow(_stmt, "category")
        val _columnIndexOfScore: Int = getColumnIndexOrThrow(_stmt, "score")
        val _columnIndexOfOpponentName: Int = getColumnIndexOrThrow(_stmt, "opponentName")
        val _columnIndexOfOpponentOwnerName: Int = getColumnIndexOrThrow(_stmt, "opponentOwnerName")
        val _columnIndexOfJudgesNotes: Int = getColumnIndexOrThrow(_stmt, "judgesNotes")
        val _columnIndexOfAwards: Int = getColumnIndexOrThrow(_stmt, "awards")
        val _columnIndexOfPhotoUrls: Int = getColumnIndexOrThrow(_stmt, "photoUrls")
        val _columnIndexOfIsVerified: Int = getColumnIndexOrThrow(_stmt, "isVerified")
        val _columnIndexOfVerifiedBy: Int = getColumnIndexOrThrow(_stmt, "verifiedBy")
        val _columnIndexOfCertificateUrl: Int = getColumnIndexOrThrow(_stmt, "certificateUrl")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfIsDeleted: Int = getColumnIndexOrThrow(_stmt, "isDeleted")
        val _columnIndexOfDeletedAt: Int = getColumnIndexOrThrow(_stmt, "deletedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _result: MutableList<ShowRecordEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: ShowRecordEntity
          val _tmpRecordId: String
          _tmpRecordId = _stmt.getText(_columnIndexOfRecordId)
          val _tmpProductId: String
          _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          val _tmpOwnerId: String
          _tmpOwnerId = _stmt.getText(_columnIndexOfOwnerId)
          val _tmpRecordType: String
          _tmpRecordType = _stmt.getText(_columnIndexOfRecordType)
          val _tmpEventName: String
          _tmpEventName = _stmt.getText(_columnIndexOfEventName)
          val _tmpEventLocation: String?
          if (_stmt.isNull(_columnIndexOfEventLocation)) {
            _tmpEventLocation = null
          } else {
            _tmpEventLocation = _stmt.getText(_columnIndexOfEventLocation)
          }
          val _tmpEventDate: Long
          _tmpEventDate = _stmt.getLong(_columnIndexOfEventDate)
          val _tmpResult: String
          _tmpResult = _stmt.getText(_columnIndexOfResult)
          val _tmpPlacement: Int?
          if (_stmt.isNull(_columnIndexOfPlacement)) {
            _tmpPlacement = null
          } else {
            _tmpPlacement = _stmt.getLong(_columnIndexOfPlacement).toInt()
          }
          val _tmpTotalParticipants: Int?
          if (_stmt.isNull(_columnIndexOfTotalParticipants)) {
            _tmpTotalParticipants = null
          } else {
            _tmpTotalParticipants = _stmt.getLong(_columnIndexOfTotalParticipants).toInt()
          }
          val _tmpCategory: String?
          if (_stmt.isNull(_columnIndexOfCategory)) {
            _tmpCategory = null
          } else {
            _tmpCategory = _stmt.getText(_columnIndexOfCategory)
          }
          val _tmpScore: Double?
          if (_stmt.isNull(_columnIndexOfScore)) {
            _tmpScore = null
          } else {
            _tmpScore = _stmt.getDouble(_columnIndexOfScore)
          }
          val _tmpOpponentName: String?
          if (_stmt.isNull(_columnIndexOfOpponentName)) {
            _tmpOpponentName = null
          } else {
            _tmpOpponentName = _stmt.getText(_columnIndexOfOpponentName)
          }
          val _tmpOpponentOwnerName: String?
          if (_stmt.isNull(_columnIndexOfOpponentOwnerName)) {
            _tmpOpponentOwnerName = null
          } else {
            _tmpOpponentOwnerName = _stmt.getText(_columnIndexOfOpponentOwnerName)
          }
          val _tmpJudgesNotes: String?
          if (_stmt.isNull(_columnIndexOfJudgesNotes)) {
            _tmpJudgesNotes = null
          } else {
            _tmpJudgesNotes = _stmt.getText(_columnIndexOfJudgesNotes)
          }
          val _tmpAwards: String?
          if (_stmt.isNull(_columnIndexOfAwards)) {
            _tmpAwards = null
          } else {
            _tmpAwards = _stmt.getText(_columnIndexOfAwards)
          }
          val _tmpPhotoUrls: String
          _tmpPhotoUrls = _stmt.getText(_columnIndexOfPhotoUrls)
          val _tmpIsVerified: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsVerified).toInt()
          _tmpIsVerified = _tmp != 0
          val _tmpVerifiedBy: String?
          if (_stmt.isNull(_columnIndexOfVerifiedBy)) {
            _tmpVerifiedBy = null
          } else {
            _tmpVerifiedBy = _stmt.getText(_columnIndexOfVerifiedBy)
          }
          val _tmpCertificateUrl: String?
          if (_stmt.isNull(_columnIndexOfCertificateUrl)) {
            _tmpCertificateUrl = null
          } else {
            _tmpCertificateUrl = _stmt.getText(_columnIndexOfCertificateUrl)
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
          val _tmpIsDeleted: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfIsDeleted).toInt()
          _tmpIsDeleted = _tmp_1 != 0
          val _tmpDeletedAt: Long?
          if (_stmt.isNull(_columnIndexOfDeletedAt)) {
            _tmpDeletedAt = null
          } else {
            _tmpDeletedAt = _stmt.getLong(_columnIndexOfDeletedAt)
          }
          val _tmpDirty: Boolean
          val _tmp_2: Int
          _tmp_2 = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp_2 != 0
          val _tmpSyncedAt: Long?
          if (_stmt.isNull(_columnIndexOfSyncedAt)) {
            _tmpSyncedAt = null
          } else {
            _tmpSyncedAt = _stmt.getLong(_columnIndexOfSyncedAt)
          }
          _item =
              ShowRecordEntity(_tmpRecordId,_tmpProductId,_tmpOwnerId,_tmpRecordType,_tmpEventName,_tmpEventLocation,_tmpEventDate,_tmpResult,_tmpPlacement,_tmpTotalParticipants,_tmpCategory,_tmpScore,_tmpOpponentName,_tmpOpponentOwnerName,_tmpJudgesNotes,_tmpAwards,_tmpPhotoUrls,_tmpIsVerified,_tmpVerifiedBy,_tmpCertificateUrl,_tmpNotes,_tmpCreatedAt,_tmpUpdatedAt,_tmpIsDeleted,_tmpDeletedAt,_tmpDirty,_tmpSyncedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun countWinsByOwner(ownerId: String): Int {
    val _sql: String =
        "SELECT COUNT(*) FROM show_records WHERE ownerId = ? AND result IN ('WIN', '1ST') AND isDeleted = 0"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, ownerId)
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

  public override suspend fun countTotalByOwner(ownerId: String): Int {
    val _sql: String = "SELECT COUNT(*) FROM show_records WHERE ownerId = ? AND isDeleted = 0"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, ownerId)
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

  public override suspend fun softDelete(recordId: String, deletedAt: Long) {
    val _sql: String =
        "UPDATE show_records SET isDeleted = 1, deletedAt = ?, dirty = 1 WHERE recordId = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, deletedAt)
        _argIndex = 2
        _stmt.bindText(_argIndex, recordId)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun clearDirty(ids: List<String>, syncedAt: Long) {
    val _stringBuilder: StringBuilder = StringBuilder()
    _stringBuilder.append("UPDATE show_records SET dirty = 0, syncedAt = ")
    _stringBuilder.append("?")
    _stringBuilder.append(" WHERE recordId IN (")
    val _inputSize: Int = ids.size
    appendPlaceholders(_stringBuilder, _inputSize)
    _stringBuilder.append(")")
    val _sql: String = _stringBuilder.toString()
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, syncedAt)
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

  public override suspend fun purgeDeleted() {
    val _sql: String = "DELETE FROM show_records WHERE isDeleted = 1"
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
