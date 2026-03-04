package com.rio.rostry.`data`.database.dao

import androidx.room.EntityDeleteOrUpdateAdapter
import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.rio.rostry.`data`.database.entity.VerificationRequestEntity
import javax.`annotation`.processing.Generated
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
public class VerificationRequestDao_Impl(
  __db: RoomDatabase,
) : VerificationRequestDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfVerificationRequestEntity:
      EntityInsertAdapter<VerificationRequestEntity>

  private val __updateAdapterOfVerificationRequestEntity:
      EntityDeleteOrUpdateAdapter<VerificationRequestEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfVerificationRequestEntity = object :
        EntityInsertAdapter<VerificationRequestEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `verification_requests` (`requestId`,`userId`,`govtIdUrl`,`farmPhotoUrl`,`status`,`rejectionReason`,`submittedAt`,`reviewedAt`,`createdAt`,`updatedAt`) VALUES (?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: VerificationRequestEntity) {
        statement.bindText(1, entity.requestId)
        statement.bindText(2, entity.userId)
        val _tmpGovtIdUrl: String? = entity.govtIdUrl
        if (_tmpGovtIdUrl == null) {
          statement.bindNull(3)
        } else {
          statement.bindText(3, _tmpGovtIdUrl)
        }
        val _tmpFarmPhotoUrl: String? = entity.farmPhotoUrl
        if (_tmpFarmPhotoUrl == null) {
          statement.bindNull(4)
        } else {
          statement.bindText(4, _tmpFarmPhotoUrl)
        }
        statement.bindText(5, entity.status)
        val _tmpRejectionReason: String? = entity.rejectionReason
        if (_tmpRejectionReason == null) {
          statement.bindNull(6)
        } else {
          statement.bindText(6, _tmpRejectionReason)
        }
        val _tmpSubmittedAt: Long? = entity.submittedAt
        if (_tmpSubmittedAt == null) {
          statement.bindNull(7)
        } else {
          statement.bindLong(7, _tmpSubmittedAt)
        }
        val _tmpReviewedAt: Long? = entity.reviewedAt
        if (_tmpReviewedAt == null) {
          statement.bindNull(8)
        } else {
          statement.bindLong(8, _tmpReviewedAt)
        }
        statement.bindLong(9, entity.createdAt)
        statement.bindLong(10, entity.updatedAt)
      }
    }
    this.__updateAdapterOfVerificationRequestEntity = object :
        EntityDeleteOrUpdateAdapter<VerificationRequestEntity>() {
      protected override fun createQuery(): String =
          "UPDATE OR ABORT `verification_requests` SET `requestId` = ?,`userId` = ?,`govtIdUrl` = ?,`farmPhotoUrl` = ?,`status` = ?,`rejectionReason` = ?,`submittedAt` = ?,`reviewedAt` = ?,`createdAt` = ?,`updatedAt` = ? WHERE `requestId` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: VerificationRequestEntity) {
        statement.bindText(1, entity.requestId)
        statement.bindText(2, entity.userId)
        val _tmpGovtIdUrl: String? = entity.govtIdUrl
        if (_tmpGovtIdUrl == null) {
          statement.bindNull(3)
        } else {
          statement.bindText(3, _tmpGovtIdUrl)
        }
        val _tmpFarmPhotoUrl: String? = entity.farmPhotoUrl
        if (_tmpFarmPhotoUrl == null) {
          statement.bindNull(4)
        } else {
          statement.bindText(4, _tmpFarmPhotoUrl)
        }
        statement.bindText(5, entity.status)
        val _tmpRejectionReason: String? = entity.rejectionReason
        if (_tmpRejectionReason == null) {
          statement.bindNull(6)
        } else {
          statement.bindText(6, _tmpRejectionReason)
        }
        val _tmpSubmittedAt: Long? = entity.submittedAt
        if (_tmpSubmittedAt == null) {
          statement.bindNull(7)
        } else {
          statement.bindLong(7, _tmpSubmittedAt)
        }
        val _tmpReviewedAt: Long? = entity.reviewedAt
        if (_tmpReviewedAt == null) {
          statement.bindNull(8)
        } else {
          statement.bindLong(8, _tmpReviewedAt)
        }
        statement.bindLong(9, entity.createdAt)
        statement.bindLong(10, entity.updatedAt)
        statement.bindText(11, entity.requestId)
      }
    }
  }

  public override suspend fun insert(request: VerificationRequestEntity): Unit =
      performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfVerificationRequestEntity.insert(_connection, request)
  }

  public override suspend fun update(request: VerificationRequestEntity): Unit =
      performSuspending(__db, false, true) { _connection ->
    __updateAdapterOfVerificationRequestEntity.handle(_connection, request)
  }

  public override suspend fun getById(requestId: String): VerificationRequestEntity? {
    val _sql: String = "SELECT * FROM verification_requests WHERE requestId = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, requestId)
        val _columnIndexOfRequestId: Int = getColumnIndexOrThrow(_stmt, "requestId")
        val _columnIndexOfUserId: Int = getColumnIndexOrThrow(_stmt, "userId")
        val _columnIndexOfGovtIdUrl: Int = getColumnIndexOrThrow(_stmt, "govtIdUrl")
        val _columnIndexOfFarmPhotoUrl: Int = getColumnIndexOrThrow(_stmt, "farmPhotoUrl")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfRejectionReason: Int = getColumnIndexOrThrow(_stmt, "rejectionReason")
        val _columnIndexOfSubmittedAt: Int = getColumnIndexOrThrow(_stmt, "submittedAt")
        val _columnIndexOfReviewedAt: Int = getColumnIndexOrThrow(_stmt, "reviewedAt")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _result: VerificationRequestEntity?
        if (_stmt.step()) {
          val _tmpRequestId: String
          _tmpRequestId = _stmt.getText(_columnIndexOfRequestId)
          val _tmpUserId: String
          _tmpUserId = _stmt.getText(_columnIndexOfUserId)
          val _tmpGovtIdUrl: String?
          if (_stmt.isNull(_columnIndexOfGovtIdUrl)) {
            _tmpGovtIdUrl = null
          } else {
            _tmpGovtIdUrl = _stmt.getText(_columnIndexOfGovtIdUrl)
          }
          val _tmpFarmPhotoUrl: String?
          if (_stmt.isNull(_columnIndexOfFarmPhotoUrl)) {
            _tmpFarmPhotoUrl = null
          } else {
            _tmpFarmPhotoUrl = _stmt.getText(_columnIndexOfFarmPhotoUrl)
          }
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpRejectionReason: String?
          if (_stmt.isNull(_columnIndexOfRejectionReason)) {
            _tmpRejectionReason = null
          } else {
            _tmpRejectionReason = _stmt.getText(_columnIndexOfRejectionReason)
          }
          val _tmpSubmittedAt: Long?
          if (_stmt.isNull(_columnIndexOfSubmittedAt)) {
            _tmpSubmittedAt = null
          } else {
            _tmpSubmittedAt = _stmt.getLong(_columnIndexOfSubmittedAt)
          }
          val _tmpReviewedAt: Long?
          if (_stmt.isNull(_columnIndexOfReviewedAt)) {
            _tmpReviewedAt = null
          } else {
            _tmpReviewedAt = _stmt.getLong(_columnIndexOfReviewedAt)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          _result =
              VerificationRequestEntity(_tmpRequestId,_tmpUserId,_tmpGovtIdUrl,_tmpFarmPhotoUrl,_tmpStatus,_tmpRejectionReason,_tmpSubmittedAt,_tmpReviewedAt,_tmpCreatedAt,_tmpUpdatedAt)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeById(requestId: String): Flow<VerificationRequestEntity?> {
    val _sql: String = "SELECT * FROM verification_requests WHERE requestId = ?"
    return createFlow(__db, false, arrayOf("verification_requests")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, requestId)
        val _columnIndexOfRequestId: Int = getColumnIndexOrThrow(_stmt, "requestId")
        val _columnIndexOfUserId: Int = getColumnIndexOrThrow(_stmt, "userId")
        val _columnIndexOfGovtIdUrl: Int = getColumnIndexOrThrow(_stmt, "govtIdUrl")
        val _columnIndexOfFarmPhotoUrl: Int = getColumnIndexOrThrow(_stmt, "farmPhotoUrl")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfRejectionReason: Int = getColumnIndexOrThrow(_stmt, "rejectionReason")
        val _columnIndexOfSubmittedAt: Int = getColumnIndexOrThrow(_stmt, "submittedAt")
        val _columnIndexOfReviewedAt: Int = getColumnIndexOrThrow(_stmt, "reviewedAt")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _result: VerificationRequestEntity?
        if (_stmt.step()) {
          val _tmpRequestId: String
          _tmpRequestId = _stmt.getText(_columnIndexOfRequestId)
          val _tmpUserId: String
          _tmpUserId = _stmt.getText(_columnIndexOfUserId)
          val _tmpGovtIdUrl: String?
          if (_stmt.isNull(_columnIndexOfGovtIdUrl)) {
            _tmpGovtIdUrl = null
          } else {
            _tmpGovtIdUrl = _stmt.getText(_columnIndexOfGovtIdUrl)
          }
          val _tmpFarmPhotoUrl: String?
          if (_stmt.isNull(_columnIndexOfFarmPhotoUrl)) {
            _tmpFarmPhotoUrl = null
          } else {
            _tmpFarmPhotoUrl = _stmt.getText(_columnIndexOfFarmPhotoUrl)
          }
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpRejectionReason: String?
          if (_stmt.isNull(_columnIndexOfRejectionReason)) {
            _tmpRejectionReason = null
          } else {
            _tmpRejectionReason = _stmt.getText(_columnIndexOfRejectionReason)
          }
          val _tmpSubmittedAt: Long?
          if (_stmt.isNull(_columnIndexOfSubmittedAt)) {
            _tmpSubmittedAt = null
          } else {
            _tmpSubmittedAt = _stmt.getLong(_columnIndexOfSubmittedAt)
          }
          val _tmpReviewedAt: Long?
          if (_stmt.isNull(_columnIndexOfReviewedAt)) {
            _tmpReviewedAt = null
          } else {
            _tmpReviewedAt = _stmt.getLong(_columnIndexOfReviewedAt)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          _result =
              VerificationRequestEntity(_tmpRequestId,_tmpUserId,_tmpGovtIdUrl,_tmpFarmPhotoUrl,_tmpStatus,_tmpRejectionReason,_tmpSubmittedAt,_tmpReviewedAt,_tmpCreatedAt,_tmpUpdatedAt)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getLatestByUserId(userId: String): VerificationRequestEntity? {
    val _sql: String =
        "SELECT * FROM verification_requests WHERE userId = ? ORDER BY createdAt DESC LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, userId)
        val _columnIndexOfRequestId: Int = getColumnIndexOrThrow(_stmt, "requestId")
        val _columnIndexOfUserId: Int = getColumnIndexOrThrow(_stmt, "userId")
        val _columnIndexOfGovtIdUrl: Int = getColumnIndexOrThrow(_stmt, "govtIdUrl")
        val _columnIndexOfFarmPhotoUrl: Int = getColumnIndexOrThrow(_stmt, "farmPhotoUrl")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfRejectionReason: Int = getColumnIndexOrThrow(_stmt, "rejectionReason")
        val _columnIndexOfSubmittedAt: Int = getColumnIndexOrThrow(_stmt, "submittedAt")
        val _columnIndexOfReviewedAt: Int = getColumnIndexOrThrow(_stmt, "reviewedAt")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _result: VerificationRequestEntity?
        if (_stmt.step()) {
          val _tmpRequestId: String
          _tmpRequestId = _stmt.getText(_columnIndexOfRequestId)
          val _tmpUserId: String
          _tmpUserId = _stmt.getText(_columnIndexOfUserId)
          val _tmpGovtIdUrl: String?
          if (_stmt.isNull(_columnIndexOfGovtIdUrl)) {
            _tmpGovtIdUrl = null
          } else {
            _tmpGovtIdUrl = _stmt.getText(_columnIndexOfGovtIdUrl)
          }
          val _tmpFarmPhotoUrl: String?
          if (_stmt.isNull(_columnIndexOfFarmPhotoUrl)) {
            _tmpFarmPhotoUrl = null
          } else {
            _tmpFarmPhotoUrl = _stmt.getText(_columnIndexOfFarmPhotoUrl)
          }
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpRejectionReason: String?
          if (_stmt.isNull(_columnIndexOfRejectionReason)) {
            _tmpRejectionReason = null
          } else {
            _tmpRejectionReason = _stmt.getText(_columnIndexOfRejectionReason)
          }
          val _tmpSubmittedAt: Long?
          if (_stmt.isNull(_columnIndexOfSubmittedAt)) {
            _tmpSubmittedAt = null
          } else {
            _tmpSubmittedAt = _stmt.getLong(_columnIndexOfSubmittedAt)
          }
          val _tmpReviewedAt: Long?
          if (_stmt.isNull(_columnIndexOfReviewedAt)) {
            _tmpReviewedAt = null
          } else {
            _tmpReviewedAt = _stmt.getLong(_columnIndexOfReviewedAt)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          _result =
              VerificationRequestEntity(_tmpRequestId,_tmpUserId,_tmpGovtIdUrl,_tmpFarmPhotoUrl,_tmpStatus,_tmpRejectionReason,_tmpSubmittedAt,_tmpReviewedAt,_tmpCreatedAt,_tmpUpdatedAt)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeLatestByUserId(userId: String): Flow<VerificationRequestEntity?> {
    val _sql: String =
        "SELECT * FROM verification_requests WHERE userId = ? ORDER BY createdAt DESC LIMIT 1"
    return createFlow(__db, false, arrayOf("verification_requests")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, userId)
        val _columnIndexOfRequestId: Int = getColumnIndexOrThrow(_stmt, "requestId")
        val _columnIndexOfUserId: Int = getColumnIndexOrThrow(_stmt, "userId")
        val _columnIndexOfGovtIdUrl: Int = getColumnIndexOrThrow(_stmt, "govtIdUrl")
        val _columnIndexOfFarmPhotoUrl: Int = getColumnIndexOrThrow(_stmt, "farmPhotoUrl")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfRejectionReason: Int = getColumnIndexOrThrow(_stmt, "rejectionReason")
        val _columnIndexOfSubmittedAt: Int = getColumnIndexOrThrow(_stmt, "submittedAt")
        val _columnIndexOfReviewedAt: Int = getColumnIndexOrThrow(_stmt, "reviewedAt")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _result: VerificationRequestEntity?
        if (_stmt.step()) {
          val _tmpRequestId: String
          _tmpRequestId = _stmt.getText(_columnIndexOfRequestId)
          val _tmpUserId: String
          _tmpUserId = _stmt.getText(_columnIndexOfUserId)
          val _tmpGovtIdUrl: String?
          if (_stmt.isNull(_columnIndexOfGovtIdUrl)) {
            _tmpGovtIdUrl = null
          } else {
            _tmpGovtIdUrl = _stmt.getText(_columnIndexOfGovtIdUrl)
          }
          val _tmpFarmPhotoUrl: String?
          if (_stmt.isNull(_columnIndexOfFarmPhotoUrl)) {
            _tmpFarmPhotoUrl = null
          } else {
            _tmpFarmPhotoUrl = _stmt.getText(_columnIndexOfFarmPhotoUrl)
          }
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpRejectionReason: String?
          if (_stmt.isNull(_columnIndexOfRejectionReason)) {
            _tmpRejectionReason = null
          } else {
            _tmpRejectionReason = _stmt.getText(_columnIndexOfRejectionReason)
          }
          val _tmpSubmittedAt: Long?
          if (_stmt.isNull(_columnIndexOfSubmittedAt)) {
            _tmpSubmittedAt = null
          } else {
            _tmpSubmittedAt = _stmt.getLong(_columnIndexOfSubmittedAt)
          }
          val _tmpReviewedAt: Long?
          if (_stmt.isNull(_columnIndexOfReviewedAt)) {
            _tmpReviewedAt = null
          } else {
            _tmpReviewedAt = _stmt.getLong(_columnIndexOfReviewedAt)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          _result =
              VerificationRequestEntity(_tmpRequestId,_tmpUserId,_tmpGovtIdUrl,_tmpFarmPhotoUrl,_tmpStatus,_tmpRejectionReason,_tmpSubmittedAt,_tmpReviewedAt,_tmpCreatedAt,_tmpUpdatedAt)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeAllByUserId(userId: String): Flow<List<VerificationRequestEntity>> {
    val _sql: String =
        "SELECT * FROM verification_requests WHERE userId = ? ORDER BY createdAt DESC"
    return createFlow(__db, false, arrayOf("verification_requests")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, userId)
        val _columnIndexOfRequestId: Int = getColumnIndexOrThrow(_stmt, "requestId")
        val _columnIndexOfUserId: Int = getColumnIndexOrThrow(_stmt, "userId")
        val _columnIndexOfGovtIdUrl: Int = getColumnIndexOrThrow(_stmt, "govtIdUrl")
        val _columnIndexOfFarmPhotoUrl: Int = getColumnIndexOrThrow(_stmt, "farmPhotoUrl")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfRejectionReason: Int = getColumnIndexOrThrow(_stmt, "rejectionReason")
        val _columnIndexOfSubmittedAt: Int = getColumnIndexOrThrow(_stmt, "submittedAt")
        val _columnIndexOfReviewedAt: Int = getColumnIndexOrThrow(_stmt, "reviewedAt")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _result: MutableList<VerificationRequestEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: VerificationRequestEntity
          val _tmpRequestId: String
          _tmpRequestId = _stmt.getText(_columnIndexOfRequestId)
          val _tmpUserId: String
          _tmpUserId = _stmt.getText(_columnIndexOfUserId)
          val _tmpGovtIdUrl: String?
          if (_stmt.isNull(_columnIndexOfGovtIdUrl)) {
            _tmpGovtIdUrl = null
          } else {
            _tmpGovtIdUrl = _stmt.getText(_columnIndexOfGovtIdUrl)
          }
          val _tmpFarmPhotoUrl: String?
          if (_stmt.isNull(_columnIndexOfFarmPhotoUrl)) {
            _tmpFarmPhotoUrl = null
          } else {
            _tmpFarmPhotoUrl = _stmt.getText(_columnIndexOfFarmPhotoUrl)
          }
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpRejectionReason: String?
          if (_stmt.isNull(_columnIndexOfRejectionReason)) {
            _tmpRejectionReason = null
          } else {
            _tmpRejectionReason = _stmt.getText(_columnIndexOfRejectionReason)
          }
          val _tmpSubmittedAt: Long?
          if (_stmt.isNull(_columnIndexOfSubmittedAt)) {
            _tmpSubmittedAt = null
          } else {
            _tmpSubmittedAt = _stmt.getLong(_columnIndexOfSubmittedAt)
          }
          val _tmpReviewedAt: Long?
          if (_stmt.isNull(_columnIndexOfReviewedAt)) {
            _tmpReviewedAt = null
          } else {
            _tmpReviewedAt = _stmt.getLong(_columnIndexOfReviewedAt)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          _item =
              VerificationRequestEntity(_tmpRequestId,_tmpUserId,_tmpGovtIdUrl,_tmpFarmPhotoUrl,_tmpStatus,_tmpRejectionReason,_tmpSubmittedAt,_tmpReviewedAt,_tmpCreatedAt,_tmpUpdatedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeByStatus(status: String): Flow<List<VerificationRequestEntity>> {
    val _sql: String =
        "SELECT * FROM verification_requests WHERE status = ? ORDER BY submittedAt ASC"
    return createFlow(__db, false, arrayOf("verification_requests")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, status)
        val _columnIndexOfRequestId: Int = getColumnIndexOrThrow(_stmt, "requestId")
        val _columnIndexOfUserId: Int = getColumnIndexOrThrow(_stmt, "userId")
        val _columnIndexOfGovtIdUrl: Int = getColumnIndexOrThrow(_stmt, "govtIdUrl")
        val _columnIndexOfFarmPhotoUrl: Int = getColumnIndexOrThrow(_stmt, "farmPhotoUrl")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfRejectionReason: Int = getColumnIndexOrThrow(_stmt, "rejectionReason")
        val _columnIndexOfSubmittedAt: Int = getColumnIndexOrThrow(_stmt, "submittedAt")
        val _columnIndexOfReviewedAt: Int = getColumnIndexOrThrow(_stmt, "reviewedAt")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _result: MutableList<VerificationRequestEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: VerificationRequestEntity
          val _tmpRequestId: String
          _tmpRequestId = _stmt.getText(_columnIndexOfRequestId)
          val _tmpUserId: String
          _tmpUserId = _stmt.getText(_columnIndexOfUserId)
          val _tmpGovtIdUrl: String?
          if (_stmt.isNull(_columnIndexOfGovtIdUrl)) {
            _tmpGovtIdUrl = null
          } else {
            _tmpGovtIdUrl = _stmt.getText(_columnIndexOfGovtIdUrl)
          }
          val _tmpFarmPhotoUrl: String?
          if (_stmt.isNull(_columnIndexOfFarmPhotoUrl)) {
            _tmpFarmPhotoUrl = null
          } else {
            _tmpFarmPhotoUrl = _stmt.getText(_columnIndexOfFarmPhotoUrl)
          }
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpRejectionReason: String?
          if (_stmt.isNull(_columnIndexOfRejectionReason)) {
            _tmpRejectionReason = null
          } else {
            _tmpRejectionReason = _stmt.getText(_columnIndexOfRejectionReason)
          }
          val _tmpSubmittedAt: Long?
          if (_stmt.isNull(_columnIndexOfSubmittedAt)) {
            _tmpSubmittedAt = null
          } else {
            _tmpSubmittedAt = _stmt.getLong(_columnIndexOfSubmittedAt)
          }
          val _tmpReviewedAt: Long?
          if (_stmt.isNull(_columnIndexOfReviewedAt)) {
            _tmpReviewedAt = null
          } else {
            _tmpReviewedAt = _stmt.getLong(_columnIndexOfReviewedAt)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          _item =
              VerificationRequestEntity(_tmpRequestId,_tmpUserId,_tmpGovtIdUrl,_tmpFarmPhotoUrl,_tmpStatus,_tmpRejectionReason,_tmpSubmittedAt,_tmpReviewedAt,_tmpCreatedAt,_tmpUpdatedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observePendingRequests(): Flow<List<VerificationRequestEntity>> {
    val _sql: String =
        "SELECT * FROM verification_requests WHERE status = 'PENDING' ORDER BY submittedAt ASC"
    return createFlow(__db, false, arrayOf("verification_requests")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfRequestId: Int = getColumnIndexOrThrow(_stmt, "requestId")
        val _columnIndexOfUserId: Int = getColumnIndexOrThrow(_stmt, "userId")
        val _columnIndexOfGovtIdUrl: Int = getColumnIndexOrThrow(_stmt, "govtIdUrl")
        val _columnIndexOfFarmPhotoUrl: Int = getColumnIndexOrThrow(_stmt, "farmPhotoUrl")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfRejectionReason: Int = getColumnIndexOrThrow(_stmt, "rejectionReason")
        val _columnIndexOfSubmittedAt: Int = getColumnIndexOrThrow(_stmt, "submittedAt")
        val _columnIndexOfReviewedAt: Int = getColumnIndexOrThrow(_stmt, "reviewedAt")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _result: MutableList<VerificationRequestEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: VerificationRequestEntity
          val _tmpRequestId: String
          _tmpRequestId = _stmt.getText(_columnIndexOfRequestId)
          val _tmpUserId: String
          _tmpUserId = _stmt.getText(_columnIndexOfUserId)
          val _tmpGovtIdUrl: String?
          if (_stmt.isNull(_columnIndexOfGovtIdUrl)) {
            _tmpGovtIdUrl = null
          } else {
            _tmpGovtIdUrl = _stmt.getText(_columnIndexOfGovtIdUrl)
          }
          val _tmpFarmPhotoUrl: String?
          if (_stmt.isNull(_columnIndexOfFarmPhotoUrl)) {
            _tmpFarmPhotoUrl = null
          } else {
            _tmpFarmPhotoUrl = _stmt.getText(_columnIndexOfFarmPhotoUrl)
          }
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpRejectionReason: String?
          if (_stmt.isNull(_columnIndexOfRejectionReason)) {
            _tmpRejectionReason = null
          } else {
            _tmpRejectionReason = _stmt.getText(_columnIndexOfRejectionReason)
          }
          val _tmpSubmittedAt: Long?
          if (_stmt.isNull(_columnIndexOfSubmittedAt)) {
            _tmpSubmittedAt = null
          } else {
            _tmpSubmittedAt = _stmt.getLong(_columnIndexOfSubmittedAt)
          }
          val _tmpReviewedAt: Long?
          if (_stmt.isNull(_columnIndexOfReviewedAt)) {
            _tmpReviewedAt = null
          } else {
            _tmpReviewedAt = _stmt.getLong(_columnIndexOfReviewedAt)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          _item =
              VerificationRequestEntity(_tmpRequestId,_tmpUserId,_tmpGovtIdUrl,_tmpFarmPhotoUrl,_tmpStatus,_tmpRejectionReason,_tmpSubmittedAt,_tmpReviewedAt,_tmpCreatedAt,_tmpUpdatedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getDraftsByUserId(userId: String): List<VerificationRequestEntity> {
    val _sql: String = "SELECT * FROM verification_requests WHERE userId = ? AND status = 'DRAFT'"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, userId)
        val _columnIndexOfRequestId: Int = getColumnIndexOrThrow(_stmt, "requestId")
        val _columnIndexOfUserId: Int = getColumnIndexOrThrow(_stmt, "userId")
        val _columnIndexOfGovtIdUrl: Int = getColumnIndexOrThrow(_stmt, "govtIdUrl")
        val _columnIndexOfFarmPhotoUrl: Int = getColumnIndexOrThrow(_stmt, "farmPhotoUrl")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfRejectionReason: Int = getColumnIndexOrThrow(_stmt, "rejectionReason")
        val _columnIndexOfSubmittedAt: Int = getColumnIndexOrThrow(_stmt, "submittedAt")
        val _columnIndexOfReviewedAt: Int = getColumnIndexOrThrow(_stmt, "reviewedAt")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _result: MutableList<VerificationRequestEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: VerificationRequestEntity
          val _tmpRequestId: String
          _tmpRequestId = _stmt.getText(_columnIndexOfRequestId)
          val _tmpUserId: String
          _tmpUserId = _stmt.getText(_columnIndexOfUserId)
          val _tmpGovtIdUrl: String?
          if (_stmt.isNull(_columnIndexOfGovtIdUrl)) {
            _tmpGovtIdUrl = null
          } else {
            _tmpGovtIdUrl = _stmt.getText(_columnIndexOfGovtIdUrl)
          }
          val _tmpFarmPhotoUrl: String?
          if (_stmt.isNull(_columnIndexOfFarmPhotoUrl)) {
            _tmpFarmPhotoUrl = null
          } else {
            _tmpFarmPhotoUrl = _stmt.getText(_columnIndexOfFarmPhotoUrl)
          }
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpRejectionReason: String?
          if (_stmt.isNull(_columnIndexOfRejectionReason)) {
            _tmpRejectionReason = null
          } else {
            _tmpRejectionReason = _stmt.getText(_columnIndexOfRejectionReason)
          }
          val _tmpSubmittedAt: Long?
          if (_stmt.isNull(_columnIndexOfSubmittedAt)) {
            _tmpSubmittedAt = null
          } else {
            _tmpSubmittedAt = _stmt.getLong(_columnIndexOfSubmittedAt)
          }
          val _tmpReviewedAt: Long?
          if (_stmt.isNull(_columnIndexOfReviewedAt)) {
            _tmpReviewedAt = null
          } else {
            _tmpReviewedAt = _stmt.getLong(_columnIndexOfReviewedAt)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          _item =
              VerificationRequestEntity(_tmpRequestId,_tmpUserId,_tmpGovtIdUrl,_tmpFarmPhotoUrl,_tmpStatus,_tmpRejectionReason,_tmpSubmittedAt,_tmpReviewedAt,_tmpCreatedAt,_tmpUpdatedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun deleteDraftsByUserId(userId: String) {
    val _sql: String = "DELETE FROM verification_requests WHERE userId = ? AND status = 'DRAFT'"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, userId)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun updateStatus(
    requestId: String,
    status: String,
    updatedAt: Long,
  ) {
    val _sql: String =
        "UPDATE verification_requests SET status = ?, updatedAt = ? WHERE requestId = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, status)
        _argIndex = 2
        _stmt.bindLong(_argIndex, updatedAt)
        _argIndex = 3
        _stmt.bindText(_argIndex, requestId)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun reject(
    requestId: String,
    reason: String,
    reviewedAt: Long,
    updatedAt: Long,
  ) {
    val _sql: String =
        "UPDATE verification_requests SET status = 'REJECTED', rejectionReason = ?, reviewedAt = ?, updatedAt = ? WHERE requestId = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, reason)
        _argIndex = 2
        _stmt.bindLong(_argIndex, reviewedAt)
        _argIndex = 3
        _stmt.bindLong(_argIndex, updatedAt)
        _argIndex = 4
        _stmt.bindText(_argIndex, requestId)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun approve(
    requestId: String,
    reviewedAt: Long,
    updatedAt: Long,
  ) {
    val _sql: String =
        "UPDATE verification_requests SET status = 'APPROVED', reviewedAt = ?, updatedAt = ? WHERE requestId = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, reviewedAt)
        _argIndex = 2
        _stmt.bindLong(_argIndex, updatedAt)
        _argIndex = 3
        _stmt.bindText(_argIndex, requestId)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun updateGovtIdUrl(
    requestId: String,
    govtIdUrl: String,
    updatedAt: Long,
  ) {
    val _sql: String =
        "UPDATE verification_requests SET govtIdUrl = ?, updatedAt = ? WHERE requestId = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, govtIdUrl)
        _argIndex = 2
        _stmt.bindLong(_argIndex, updatedAt)
        _argIndex = 3
        _stmt.bindText(_argIndex, requestId)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun updateFarmPhotoUrl(
    requestId: String,
    farmPhotoUrl: String,
    updatedAt: Long,
  ) {
    val _sql: String =
        "UPDATE verification_requests SET farmPhotoUrl = ?, updatedAt = ? WHERE requestId = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, farmPhotoUrl)
        _argIndex = 2
        _stmt.bindLong(_argIndex, updatedAt)
        _argIndex = 3
        _stmt.bindText(_argIndex, requestId)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun markSubmitted(
    requestId: String,
    submittedAt: Long,
    updatedAt: Long,
  ) {
    val _sql: String =
        "UPDATE verification_requests SET status = 'PENDING', submittedAt = ?, updatedAt = ? WHERE requestId = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, submittedAt)
        _argIndex = 2
        _stmt.bindLong(_argIndex, updatedAt)
        _argIndex = 3
        _stmt.bindText(_argIndex, requestId)
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
