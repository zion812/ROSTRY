package com.rio.rostry.`data`.database.dao

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.rio.rostry.`data`.database.entity.OrderDisputeEntity
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
public class OrderDisputeDao_Impl(
  __db: RoomDatabase,
) : OrderDisputeDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfOrderDisputeEntity: EntityInsertAdapter<OrderDisputeEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfOrderDisputeEntity = object : EntityInsertAdapter<OrderDisputeEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `order_disputes` (`disputeId`,`orderId`,`raisedBy`,`raisedByRole`,`againstUserId`,`reason`,`description`,`requestedResolution`,`claimedAmount`,`evidenceIds`,`status`,`resolvedAt`,`resolvedBy`,`resolutionType`,`resolutionNotes`,`refundedAmount`,`lastResponseAt`,`responseCount`,`escalatedAt`,`escalationReason`,`adminNotes`,`createdAt`,`updatedAt`,`dirty`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: OrderDisputeEntity) {
        statement.bindText(1, entity.disputeId)
        statement.bindText(2, entity.orderId)
        statement.bindText(3, entity.raisedBy)
        statement.bindText(4, entity.raisedByRole)
        statement.bindText(5, entity.againstUserId)
        statement.bindText(6, entity.reason)
        statement.bindText(7, entity.description)
        val _tmpRequestedResolution: String? = entity.requestedResolution
        if (_tmpRequestedResolution == null) {
          statement.bindNull(8)
        } else {
          statement.bindText(8, _tmpRequestedResolution)
        }
        val _tmpClaimedAmount: Double? = entity.claimedAmount
        if (_tmpClaimedAmount == null) {
          statement.bindNull(9)
        } else {
          statement.bindDouble(9, _tmpClaimedAmount)
        }
        val _tmpEvidenceIds: String? = entity.evidenceIds
        if (_tmpEvidenceIds == null) {
          statement.bindNull(10)
        } else {
          statement.bindText(10, _tmpEvidenceIds)
        }
        statement.bindText(11, entity.status)
        val _tmpResolvedAt: Long? = entity.resolvedAt
        if (_tmpResolvedAt == null) {
          statement.bindNull(12)
        } else {
          statement.bindLong(12, _tmpResolvedAt)
        }
        val _tmpResolvedBy: String? = entity.resolvedBy
        if (_tmpResolvedBy == null) {
          statement.bindNull(13)
        } else {
          statement.bindText(13, _tmpResolvedBy)
        }
        val _tmpResolutionType: String? = entity.resolutionType
        if (_tmpResolutionType == null) {
          statement.bindNull(14)
        } else {
          statement.bindText(14, _tmpResolutionType)
        }
        val _tmpResolutionNotes: String? = entity.resolutionNotes
        if (_tmpResolutionNotes == null) {
          statement.bindNull(15)
        } else {
          statement.bindText(15, _tmpResolutionNotes)
        }
        val _tmpRefundedAmount: Double? = entity.refundedAmount
        if (_tmpRefundedAmount == null) {
          statement.bindNull(16)
        } else {
          statement.bindDouble(16, _tmpRefundedAmount)
        }
        val _tmpLastResponseAt: Long? = entity.lastResponseAt
        if (_tmpLastResponseAt == null) {
          statement.bindNull(17)
        } else {
          statement.bindLong(17, _tmpLastResponseAt)
        }
        statement.bindLong(18, entity.responseCount.toLong())
        val _tmpEscalatedAt: Long? = entity.escalatedAt
        if (_tmpEscalatedAt == null) {
          statement.bindNull(19)
        } else {
          statement.bindLong(19, _tmpEscalatedAt)
        }
        val _tmpEscalationReason: String? = entity.escalationReason
        if (_tmpEscalationReason == null) {
          statement.bindNull(20)
        } else {
          statement.bindText(20, _tmpEscalationReason)
        }
        val _tmpAdminNotes: String? = entity.adminNotes
        if (_tmpAdminNotes == null) {
          statement.bindNull(21)
        } else {
          statement.bindText(21, _tmpAdminNotes)
        }
        statement.bindLong(22, entity.createdAt)
        statement.bindLong(23, entity.updatedAt)
        val _tmp: Int = if (entity.dirty) 1 else 0
        statement.bindLong(24, _tmp.toLong())
      }
    }
  }

  public override suspend fun upsert(dispute: OrderDisputeEntity): Unit = performSuspending(__db,
      false, true) { _connection ->
    __insertAdapterOfOrderDisputeEntity.insert(_connection, dispute)
  }

  public override suspend fun findById(disputeId: String): OrderDisputeEntity? {
    val _sql: String = "SELECT * FROM order_disputes WHERE disputeId = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, disputeId)
        val _columnIndexOfDisputeId: Int = getColumnIndexOrThrow(_stmt, "disputeId")
        val _columnIndexOfOrderId: Int = getColumnIndexOrThrow(_stmt, "orderId")
        val _columnIndexOfRaisedBy: Int = getColumnIndexOrThrow(_stmt, "raisedBy")
        val _columnIndexOfRaisedByRole: Int = getColumnIndexOrThrow(_stmt, "raisedByRole")
        val _columnIndexOfAgainstUserId: Int = getColumnIndexOrThrow(_stmt, "againstUserId")
        val _columnIndexOfReason: Int = getColumnIndexOrThrow(_stmt, "reason")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfRequestedResolution: Int = getColumnIndexOrThrow(_stmt,
            "requestedResolution")
        val _columnIndexOfClaimedAmount: Int = getColumnIndexOrThrow(_stmt, "claimedAmount")
        val _columnIndexOfEvidenceIds: Int = getColumnIndexOrThrow(_stmt, "evidenceIds")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfResolvedAt: Int = getColumnIndexOrThrow(_stmt, "resolvedAt")
        val _columnIndexOfResolvedBy: Int = getColumnIndexOrThrow(_stmt, "resolvedBy")
        val _columnIndexOfResolutionType: Int = getColumnIndexOrThrow(_stmt, "resolutionType")
        val _columnIndexOfResolutionNotes: Int = getColumnIndexOrThrow(_stmt, "resolutionNotes")
        val _columnIndexOfRefundedAmount: Int = getColumnIndexOrThrow(_stmt, "refundedAmount")
        val _columnIndexOfLastResponseAt: Int = getColumnIndexOrThrow(_stmt, "lastResponseAt")
        val _columnIndexOfResponseCount: Int = getColumnIndexOrThrow(_stmt, "responseCount")
        val _columnIndexOfEscalatedAt: Int = getColumnIndexOrThrow(_stmt, "escalatedAt")
        val _columnIndexOfEscalationReason: Int = getColumnIndexOrThrow(_stmt, "escalationReason")
        val _columnIndexOfAdminNotes: Int = getColumnIndexOrThrow(_stmt, "adminNotes")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _result: OrderDisputeEntity?
        if (_stmt.step()) {
          val _tmpDisputeId: String
          _tmpDisputeId = _stmt.getText(_columnIndexOfDisputeId)
          val _tmpOrderId: String
          _tmpOrderId = _stmt.getText(_columnIndexOfOrderId)
          val _tmpRaisedBy: String
          _tmpRaisedBy = _stmt.getText(_columnIndexOfRaisedBy)
          val _tmpRaisedByRole: String
          _tmpRaisedByRole = _stmt.getText(_columnIndexOfRaisedByRole)
          val _tmpAgainstUserId: String
          _tmpAgainstUserId = _stmt.getText(_columnIndexOfAgainstUserId)
          val _tmpReason: String
          _tmpReason = _stmt.getText(_columnIndexOfReason)
          val _tmpDescription: String
          _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          val _tmpRequestedResolution: String?
          if (_stmt.isNull(_columnIndexOfRequestedResolution)) {
            _tmpRequestedResolution = null
          } else {
            _tmpRequestedResolution = _stmt.getText(_columnIndexOfRequestedResolution)
          }
          val _tmpClaimedAmount: Double?
          if (_stmt.isNull(_columnIndexOfClaimedAmount)) {
            _tmpClaimedAmount = null
          } else {
            _tmpClaimedAmount = _stmt.getDouble(_columnIndexOfClaimedAmount)
          }
          val _tmpEvidenceIds: String?
          if (_stmt.isNull(_columnIndexOfEvidenceIds)) {
            _tmpEvidenceIds = null
          } else {
            _tmpEvidenceIds = _stmt.getText(_columnIndexOfEvidenceIds)
          }
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpResolvedAt: Long?
          if (_stmt.isNull(_columnIndexOfResolvedAt)) {
            _tmpResolvedAt = null
          } else {
            _tmpResolvedAt = _stmt.getLong(_columnIndexOfResolvedAt)
          }
          val _tmpResolvedBy: String?
          if (_stmt.isNull(_columnIndexOfResolvedBy)) {
            _tmpResolvedBy = null
          } else {
            _tmpResolvedBy = _stmt.getText(_columnIndexOfResolvedBy)
          }
          val _tmpResolutionType: String?
          if (_stmt.isNull(_columnIndexOfResolutionType)) {
            _tmpResolutionType = null
          } else {
            _tmpResolutionType = _stmt.getText(_columnIndexOfResolutionType)
          }
          val _tmpResolutionNotes: String?
          if (_stmt.isNull(_columnIndexOfResolutionNotes)) {
            _tmpResolutionNotes = null
          } else {
            _tmpResolutionNotes = _stmt.getText(_columnIndexOfResolutionNotes)
          }
          val _tmpRefundedAmount: Double?
          if (_stmt.isNull(_columnIndexOfRefundedAmount)) {
            _tmpRefundedAmount = null
          } else {
            _tmpRefundedAmount = _stmt.getDouble(_columnIndexOfRefundedAmount)
          }
          val _tmpLastResponseAt: Long?
          if (_stmt.isNull(_columnIndexOfLastResponseAt)) {
            _tmpLastResponseAt = null
          } else {
            _tmpLastResponseAt = _stmt.getLong(_columnIndexOfLastResponseAt)
          }
          val _tmpResponseCount: Int
          _tmpResponseCount = _stmt.getLong(_columnIndexOfResponseCount).toInt()
          val _tmpEscalatedAt: Long?
          if (_stmt.isNull(_columnIndexOfEscalatedAt)) {
            _tmpEscalatedAt = null
          } else {
            _tmpEscalatedAt = _stmt.getLong(_columnIndexOfEscalatedAt)
          }
          val _tmpEscalationReason: String?
          if (_stmt.isNull(_columnIndexOfEscalationReason)) {
            _tmpEscalationReason = null
          } else {
            _tmpEscalationReason = _stmt.getText(_columnIndexOfEscalationReason)
          }
          val _tmpAdminNotes: String?
          if (_stmt.isNull(_columnIndexOfAdminNotes)) {
            _tmpAdminNotes = null
          } else {
            _tmpAdminNotes = _stmt.getText(_columnIndexOfAdminNotes)
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
              OrderDisputeEntity(_tmpDisputeId,_tmpOrderId,_tmpRaisedBy,_tmpRaisedByRole,_tmpAgainstUserId,_tmpReason,_tmpDescription,_tmpRequestedResolution,_tmpClaimedAmount,_tmpEvidenceIds,_tmpStatus,_tmpResolvedAt,_tmpResolvedBy,_tmpResolutionType,_tmpResolutionNotes,_tmpRefundedAmount,_tmpLastResponseAt,_tmpResponseCount,_tmpEscalatedAt,_tmpEscalationReason,_tmpAdminNotes,_tmpCreatedAt,_tmpUpdatedAt,_tmpDirty)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getOrderDisputes(orderId: String): Flow<List<OrderDisputeEntity>> {
    val _sql: String = "SELECT * FROM order_disputes WHERE orderId = ? ORDER BY createdAt DESC"
    return createFlow(__db, false, arrayOf("order_disputes")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, orderId)
        val _columnIndexOfDisputeId: Int = getColumnIndexOrThrow(_stmt, "disputeId")
        val _columnIndexOfOrderId: Int = getColumnIndexOrThrow(_stmt, "orderId")
        val _columnIndexOfRaisedBy: Int = getColumnIndexOrThrow(_stmt, "raisedBy")
        val _columnIndexOfRaisedByRole: Int = getColumnIndexOrThrow(_stmt, "raisedByRole")
        val _columnIndexOfAgainstUserId: Int = getColumnIndexOrThrow(_stmt, "againstUserId")
        val _columnIndexOfReason: Int = getColumnIndexOrThrow(_stmt, "reason")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfRequestedResolution: Int = getColumnIndexOrThrow(_stmt,
            "requestedResolution")
        val _columnIndexOfClaimedAmount: Int = getColumnIndexOrThrow(_stmt, "claimedAmount")
        val _columnIndexOfEvidenceIds: Int = getColumnIndexOrThrow(_stmt, "evidenceIds")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfResolvedAt: Int = getColumnIndexOrThrow(_stmt, "resolvedAt")
        val _columnIndexOfResolvedBy: Int = getColumnIndexOrThrow(_stmt, "resolvedBy")
        val _columnIndexOfResolutionType: Int = getColumnIndexOrThrow(_stmt, "resolutionType")
        val _columnIndexOfResolutionNotes: Int = getColumnIndexOrThrow(_stmt, "resolutionNotes")
        val _columnIndexOfRefundedAmount: Int = getColumnIndexOrThrow(_stmt, "refundedAmount")
        val _columnIndexOfLastResponseAt: Int = getColumnIndexOrThrow(_stmt, "lastResponseAt")
        val _columnIndexOfResponseCount: Int = getColumnIndexOrThrow(_stmt, "responseCount")
        val _columnIndexOfEscalatedAt: Int = getColumnIndexOrThrow(_stmt, "escalatedAt")
        val _columnIndexOfEscalationReason: Int = getColumnIndexOrThrow(_stmt, "escalationReason")
        val _columnIndexOfAdminNotes: Int = getColumnIndexOrThrow(_stmt, "adminNotes")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _result: MutableList<OrderDisputeEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: OrderDisputeEntity
          val _tmpDisputeId: String
          _tmpDisputeId = _stmt.getText(_columnIndexOfDisputeId)
          val _tmpOrderId: String
          _tmpOrderId = _stmt.getText(_columnIndexOfOrderId)
          val _tmpRaisedBy: String
          _tmpRaisedBy = _stmt.getText(_columnIndexOfRaisedBy)
          val _tmpRaisedByRole: String
          _tmpRaisedByRole = _stmt.getText(_columnIndexOfRaisedByRole)
          val _tmpAgainstUserId: String
          _tmpAgainstUserId = _stmt.getText(_columnIndexOfAgainstUserId)
          val _tmpReason: String
          _tmpReason = _stmt.getText(_columnIndexOfReason)
          val _tmpDescription: String
          _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          val _tmpRequestedResolution: String?
          if (_stmt.isNull(_columnIndexOfRequestedResolution)) {
            _tmpRequestedResolution = null
          } else {
            _tmpRequestedResolution = _stmt.getText(_columnIndexOfRequestedResolution)
          }
          val _tmpClaimedAmount: Double?
          if (_stmt.isNull(_columnIndexOfClaimedAmount)) {
            _tmpClaimedAmount = null
          } else {
            _tmpClaimedAmount = _stmt.getDouble(_columnIndexOfClaimedAmount)
          }
          val _tmpEvidenceIds: String?
          if (_stmt.isNull(_columnIndexOfEvidenceIds)) {
            _tmpEvidenceIds = null
          } else {
            _tmpEvidenceIds = _stmt.getText(_columnIndexOfEvidenceIds)
          }
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpResolvedAt: Long?
          if (_stmt.isNull(_columnIndexOfResolvedAt)) {
            _tmpResolvedAt = null
          } else {
            _tmpResolvedAt = _stmt.getLong(_columnIndexOfResolvedAt)
          }
          val _tmpResolvedBy: String?
          if (_stmt.isNull(_columnIndexOfResolvedBy)) {
            _tmpResolvedBy = null
          } else {
            _tmpResolvedBy = _stmt.getText(_columnIndexOfResolvedBy)
          }
          val _tmpResolutionType: String?
          if (_stmt.isNull(_columnIndexOfResolutionType)) {
            _tmpResolutionType = null
          } else {
            _tmpResolutionType = _stmt.getText(_columnIndexOfResolutionType)
          }
          val _tmpResolutionNotes: String?
          if (_stmt.isNull(_columnIndexOfResolutionNotes)) {
            _tmpResolutionNotes = null
          } else {
            _tmpResolutionNotes = _stmt.getText(_columnIndexOfResolutionNotes)
          }
          val _tmpRefundedAmount: Double?
          if (_stmt.isNull(_columnIndexOfRefundedAmount)) {
            _tmpRefundedAmount = null
          } else {
            _tmpRefundedAmount = _stmt.getDouble(_columnIndexOfRefundedAmount)
          }
          val _tmpLastResponseAt: Long?
          if (_stmt.isNull(_columnIndexOfLastResponseAt)) {
            _tmpLastResponseAt = null
          } else {
            _tmpLastResponseAt = _stmt.getLong(_columnIndexOfLastResponseAt)
          }
          val _tmpResponseCount: Int
          _tmpResponseCount = _stmt.getLong(_columnIndexOfResponseCount).toInt()
          val _tmpEscalatedAt: Long?
          if (_stmt.isNull(_columnIndexOfEscalatedAt)) {
            _tmpEscalatedAt = null
          } else {
            _tmpEscalatedAt = _stmt.getLong(_columnIndexOfEscalatedAt)
          }
          val _tmpEscalationReason: String?
          if (_stmt.isNull(_columnIndexOfEscalationReason)) {
            _tmpEscalationReason = null
          } else {
            _tmpEscalationReason = _stmt.getText(_columnIndexOfEscalationReason)
          }
          val _tmpAdminNotes: String?
          if (_stmt.isNull(_columnIndexOfAdminNotes)) {
            _tmpAdminNotes = null
          } else {
            _tmpAdminNotes = _stmt.getText(_columnIndexOfAdminNotes)
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
              OrderDisputeEntity(_tmpDisputeId,_tmpOrderId,_tmpRaisedBy,_tmpRaisedByRole,_tmpAgainstUserId,_tmpReason,_tmpDescription,_tmpRequestedResolution,_tmpClaimedAmount,_tmpEvidenceIds,_tmpStatus,_tmpResolvedAt,_tmpResolvedBy,_tmpResolutionType,_tmpResolutionNotes,_tmpRefundedAmount,_tmpLastResponseAt,_tmpResponseCount,_tmpEscalatedAt,_tmpEscalationReason,_tmpAdminNotes,_tmpCreatedAt,_tmpUpdatedAt,_tmpDirty)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getUserActiveDisputes(userId: String): Flow<List<OrderDisputeEntity>> {
    val _sql: String =
        "SELECT * FROM order_disputes WHERE raisedBy = ? AND status NOT IN ('RESOLVED', 'CLOSED') ORDER BY createdAt DESC"
    return createFlow(__db, false, arrayOf("order_disputes")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, userId)
        val _columnIndexOfDisputeId: Int = getColumnIndexOrThrow(_stmt, "disputeId")
        val _columnIndexOfOrderId: Int = getColumnIndexOrThrow(_stmt, "orderId")
        val _columnIndexOfRaisedBy: Int = getColumnIndexOrThrow(_stmt, "raisedBy")
        val _columnIndexOfRaisedByRole: Int = getColumnIndexOrThrow(_stmt, "raisedByRole")
        val _columnIndexOfAgainstUserId: Int = getColumnIndexOrThrow(_stmt, "againstUserId")
        val _columnIndexOfReason: Int = getColumnIndexOrThrow(_stmt, "reason")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfRequestedResolution: Int = getColumnIndexOrThrow(_stmt,
            "requestedResolution")
        val _columnIndexOfClaimedAmount: Int = getColumnIndexOrThrow(_stmt, "claimedAmount")
        val _columnIndexOfEvidenceIds: Int = getColumnIndexOrThrow(_stmt, "evidenceIds")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfResolvedAt: Int = getColumnIndexOrThrow(_stmt, "resolvedAt")
        val _columnIndexOfResolvedBy: Int = getColumnIndexOrThrow(_stmt, "resolvedBy")
        val _columnIndexOfResolutionType: Int = getColumnIndexOrThrow(_stmt, "resolutionType")
        val _columnIndexOfResolutionNotes: Int = getColumnIndexOrThrow(_stmt, "resolutionNotes")
        val _columnIndexOfRefundedAmount: Int = getColumnIndexOrThrow(_stmt, "refundedAmount")
        val _columnIndexOfLastResponseAt: Int = getColumnIndexOrThrow(_stmt, "lastResponseAt")
        val _columnIndexOfResponseCount: Int = getColumnIndexOrThrow(_stmt, "responseCount")
        val _columnIndexOfEscalatedAt: Int = getColumnIndexOrThrow(_stmt, "escalatedAt")
        val _columnIndexOfEscalationReason: Int = getColumnIndexOrThrow(_stmt, "escalationReason")
        val _columnIndexOfAdminNotes: Int = getColumnIndexOrThrow(_stmt, "adminNotes")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _result: MutableList<OrderDisputeEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: OrderDisputeEntity
          val _tmpDisputeId: String
          _tmpDisputeId = _stmt.getText(_columnIndexOfDisputeId)
          val _tmpOrderId: String
          _tmpOrderId = _stmt.getText(_columnIndexOfOrderId)
          val _tmpRaisedBy: String
          _tmpRaisedBy = _stmt.getText(_columnIndexOfRaisedBy)
          val _tmpRaisedByRole: String
          _tmpRaisedByRole = _stmt.getText(_columnIndexOfRaisedByRole)
          val _tmpAgainstUserId: String
          _tmpAgainstUserId = _stmt.getText(_columnIndexOfAgainstUserId)
          val _tmpReason: String
          _tmpReason = _stmt.getText(_columnIndexOfReason)
          val _tmpDescription: String
          _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          val _tmpRequestedResolution: String?
          if (_stmt.isNull(_columnIndexOfRequestedResolution)) {
            _tmpRequestedResolution = null
          } else {
            _tmpRequestedResolution = _stmt.getText(_columnIndexOfRequestedResolution)
          }
          val _tmpClaimedAmount: Double?
          if (_stmt.isNull(_columnIndexOfClaimedAmount)) {
            _tmpClaimedAmount = null
          } else {
            _tmpClaimedAmount = _stmt.getDouble(_columnIndexOfClaimedAmount)
          }
          val _tmpEvidenceIds: String?
          if (_stmt.isNull(_columnIndexOfEvidenceIds)) {
            _tmpEvidenceIds = null
          } else {
            _tmpEvidenceIds = _stmt.getText(_columnIndexOfEvidenceIds)
          }
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpResolvedAt: Long?
          if (_stmt.isNull(_columnIndexOfResolvedAt)) {
            _tmpResolvedAt = null
          } else {
            _tmpResolvedAt = _stmt.getLong(_columnIndexOfResolvedAt)
          }
          val _tmpResolvedBy: String?
          if (_stmt.isNull(_columnIndexOfResolvedBy)) {
            _tmpResolvedBy = null
          } else {
            _tmpResolvedBy = _stmt.getText(_columnIndexOfResolvedBy)
          }
          val _tmpResolutionType: String?
          if (_stmt.isNull(_columnIndexOfResolutionType)) {
            _tmpResolutionType = null
          } else {
            _tmpResolutionType = _stmt.getText(_columnIndexOfResolutionType)
          }
          val _tmpResolutionNotes: String?
          if (_stmt.isNull(_columnIndexOfResolutionNotes)) {
            _tmpResolutionNotes = null
          } else {
            _tmpResolutionNotes = _stmt.getText(_columnIndexOfResolutionNotes)
          }
          val _tmpRefundedAmount: Double?
          if (_stmt.isNull(_columnIndexOfRefundedAmount)) {
            _tmpRefundedAmount = null
          } else {
            _tmpRefundedAmount = _stmt.getDouble(_columnIndexOfRefundedAmount)
          }
          val _tmpLastResponseAt: Long?
          if (_stmt.isNull(_columnIndexOfLastResponseAt)) {
            _tmpLastResponseAt = null
          } else {
            _tmpLastResponseAt = _stmt.getLong(_columnIndexOfLastResponseAt)
          }
          val _tmpResponseCount: Int
          _tmpResponseCount = _stmt.getLong(_columnIndexOfResponseCount).toInt()
          val _tmpEscalatedAt: Long?
          if (_stmt.isNull(_columnIndexOfEscalatedAt)) {
            _tmpEscalatedAt = null
          } else {
            _tmpEscalatedAt = _stmt.getLong(_columnIndexOfEscalatedAt)
          }
          val _tmpEscalationReason: String?
          if (_stmt.isNull(_columnIndexOfEscalationReason)) {
            _tmpEscalationReason = null
          } else {
            _tmpEscalationReason = _stmt.getText(_columnIndexOfEscalationReason)
          }
          val _tmpAdminNotes: String?
          if (_stmt.isNull(_columnIndexOfAdminNotes)) {
            _tmpAdminNotes = null
          } else {
            _tmpAdminNotes = _stmt.getText(_columnIndexOfAdminNotes)
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
              OrderDisputeEntity(_tmpDisputeId,_tmpOrderId,_tmpRaisedBy,_tmpRaisedByRole,_tmpAgainstUserId,_tmpReason,_tmpDescription,_tmpRequestedResolution,_tmpClaimedAmount,_tmpEvidenceIds,_tmpStatus,_tmpResolvedAt,_tmpResolvedBy,_tmpResolutionType,_tmpResolutionNotes,_tmpRefundedAmount,_tmpLastResponseAt,_tmpResponseCount,_tmpEscalatedAt,_tmpEscalationReason,_tmpAdminNotes,_tmpCreatedAt,_tmpUpdatedAt,_tmpDirty)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getAllOpenDisputes(): Flow<List<OrderDisputeEntity>> {
    val _sql: String =
        "SELECT * FROM order_disputes WHERE status IN ('OPEN', 'UNDER_REVIEW', 'AWAITING_RESPONSE') ORDER BY createdAt"
    return createFlow(__db, false, arrayOf("order_disputes")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfDisputeId: Int = getColumnIndexOrThrow(_stmt, "disputeId")
        val _columnIndexOfOrderId: Int = getColumnIndexOrThrow(_stmt, "orderId")
        val _columnIndexOfRaisedBy: Int = getColumnIndexOrThrow(_stmt, "raisedBy")
        val _columnIndexOfRaisedByRole: Int = getColumnIndexOrThrow(_stmt, "raisedByRole")
        val _columnIndexOfAgainstUserId: Int = getColumnIndexOrThrow(_stmt, "againstUserId")
        val _columnIndexOfReason: Int = getColumnIndexOrThrow(_stmt, "reason")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfRequestedResolution: Int = getColumnIndexOrThrow(_stmt,
            "requestedResolution")
        val _columnIndexOfClaimedAmount: Int = getColumnIndexOrThrow(_stmt, "claimedAmount")
        val _columnIndexOfEvidenceIds: Int = getColumnIndexOrThrow(_stmt, "evidenceIds")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfResolvedAt: Int = getColumnIndexOrThrow(_stmt, "resolvedAt")
        val _columnIndexOfResolvedBy: Int = getColumnIndexOrThrow(_stmt, "resolvedBy")
        val _columnIndexOfResolutionType: Int = getColumnIndexOrThrow(_stmt, "resolutionType")
        val _columnIndexOfResolutionNotes: Int = getColumnIndexOrThrow(_stmt, "resolutionNotes")
        val _columnIndexOfRefundedAmount: Int = getColumnIndexOrThrow(_stmt, "refundedAmount")
        val _columnIndexOfLastResponseAt: Int = getColumnIndexOrThrow(_stmt, "lastResponseAt")
        val _columnIndexOfResponseCount: Int = getColumnIndexOrThrow(_stmt, "responseCount")
        val _columnIndexOfEscalatedAt: Int = getColumnIndexOrThrow(_stmt, "escalatedAt")
        val _columnIndexOfEscalationReason: Int = getColumnIndexOrThrow(_stmt, "escalationReason")
        val _columnIndexOfAdminNotes: Int = getColumnIndexOrThrow(_stmt, "adminNotes")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _result: MutableList<OrderDisputeEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: OrderDisputeEntity
          val _tmpDisputeId: String
          _tmpDisputeId = _stmt.getText(_columnIndexOfDisputeId)
          val _tmpOrderId: String
          _tmpOrderId = _stmt.getText(_columnIndexOfOrderId)
          val _tmpRaisedBy: String
          _tmpRaisedBy = _stmt.getText(_columnIndexOfRaisedBy)
          val _tmpRaisedByRole: String
          _tmpRaisedByRole = _stmt.getText(_columnIndexOfRaisedByRole)
          val _tmpAgainstUserId: String
          _tmpAgainstUserId = _stmt.getText(_columnIndexOfAgainstUserId)
          val _tmpReason: String
          _tmpReason = _stmt.getText(_columnIndexOfReason)
          val _tmpDescription: String
          _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          val _tmpRequestedResolution: String?
          if (_stmt.isNull(_columnIndexOfRequestedResolution)) {
            _tmpRequestedResolution = null
          } else {
            _tmpRequestedResolution = _stmt.getText(_columnIndexOfRequestedResolution)
          }
          val _tmpClaimedAmount: Double?
          if (_stmt.isNull(_columnIndexOfClaimedAmount)) {
            _tmpClaimedAmount = null
          } else {
            _tmpClaimedAmount = _stmt.getDouble(_columnIndexOfClaimedAmount)
          }
          val _tmpEvidenceIds: String?
          if (_stmt.isNull(_columnIndexOfEvidenceIds)) {
            _tmpEvidenceIds = null
          } else {
            _tmpEvidenceIds = _stmt.getText(_columnIndexOfEvidenceIds)
          }
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpResolvedAt: Long?
          if (_stmt.isNull(_columnIndexOfResolvedAt)) {
            _tmpResolvedAt = null
          } else {
            _tmpResolvedAt = _stmt.getLong(_columnIndexOfResolvedAt)
          }
          val _tmpResolvedBy: String?
          if (_stmt.isNull(_columnIndexOfResolvedBy)) {
            _tmpResolvedBy = null
          } else {
            _tmpResolvedBy = _stmt.getText(_columnIndexOfResolvedBy)
          }
          val _tmpResolutionType: String?
          if (_stmt.isNull(_columnIndexOfResolutionType)) {
            _tmpResolutionType = null
          } else {
            _tmpResolutionType = _stmt.getText(_columnIndexOfResolutionType)
          }
          val _tmpResolutionNotes: String?
          if (_stmt.isNull(_columnIndexOfResolutionNotes)) {
            _tmpResolutionNotes = null
          } else {
            _tmpResolutionNotes = _stmt.getText(_columnIndexOfResolutionNotes)
          }
          val _tmpRefundedAmount: Double?
          if (_stmt.isNull(_columnIndexOfRefundedAmount)) {
            _tmpRefundedAmount = null
          } else {
            _tmpRefundedAmount = _stmt.getDouble(_columnIndexOfRefundedAmount)
          }
          val _tmpLastResponseAt: Long?
          if (_stmt.isNull(_columnIndexOfLastResponseAt)) {
            _tmpLastResponseAt = null
          } else {
            _tmpLastResponseAt = _stmt.getLong(_columnIndexOfLastResponseAt)
          }
          val _tmpResponseCount: Int
          _tmpResponseCount = _stmt.getLong(_columnIndexOfResponseCount).toInt()
          val _tmpEscalatedAt: Long?
          if (_stmt.isNull(_columnIndexOfEscalatedAt)) {
            _tmpEscalatedAt = null
          } else {
            _tmpEscalatedAt = _stmt.getLong(_columnIndexOfEscalatedAt)
          }
          val _tmpEscalationReason: String?
          if (_stmt.isNull(_columnIndexOfEscalationReason)) {
            _tmpEscalationReason = null
          } else {
            _tmpEscalationReason = _stmt.getText(_columnIndexOfEscalationReason)
          }
          val _tmpAdminNotes: String?
          if (_stmt.isNull(_columnIndexOfAdminNotes)) {
            _tmpAdminNotes = null
          } else {
            _tmpAdminNotes = _stmt.getText(_columnIndexOfAdminNotes)
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
              OrderDisputeEntity(_tmpDisputeId,_tmpOrderId,_tmpRaisedBy,_tmpRaisedByRole,_tmpAgainstUserId,_tmpReason,_tmpDescription,_tmpRequestedResolution,_tmpClaimedAmount,_tmpEvidenceIds,_tmpStatus,_tmpResolvedAt,_tmpResolvedBy,_tmpResolutionType,_tmpResolutionNotes,_tmpRefundedAmount,_tmpLastResponseAt,_tmpResponseCount,_tmpEscalatedAt,_tmpEscalationReason,_tmpAdminNotes,_tmpCreatedAt,_tmpUpdatedAt,_tmpDirty)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getEscalatedDisputes(): Flow<List<OrderDisputeEntity>> {
    val _sql: String =
        "SELECT * FROM order_disputes WHERE status = 'ESCALATED' ORDER BY escalatedAt"
    return createFlow(__db, false, arrayOf("order_disputes")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfDisputeId: Int = getColumnIndexOrThrow(_stmt, "disputeId")
        val _columnIndexOfOrderId: Int = getColumnIndexOrThrow(_stmt, "orderId")
        val _columnIndexOfRaisedBy: Int = getColumnIndexOrThrow(_stmt, "raisedBy")
        val _columnIndexOfRaisedByRole: Int = getColumnIndexOrThrow(_stmt, "raisedByRole")
        val _columnIndexOfAgainstUserId: Int = getColumnIndexOrThrow(_stmt, "againstUserId")
        val _columnIndexOfReason: Int = getColumnIndexOrThrow(_stmt, "reason")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfRequestedResolution: Int = getColumnIndexOrThrow(_stmt,
            "requestedResolution")
        val _columnIndexOfClaimedAmount: Int = getColumnIndexOrThrow(_stmt, "claimedAmount")
        val _columnIndexOfEvidenceIds: Int = getColumnIndexOrThrow(_stmt, "evidenceIds")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfResolvedAt: Int = getColumnIndexOrThrow(_stmt, "resolvedAt")
        val _columnIndexOfResolvedBy: Int = getColumnIndexOrThrow(_stmt, "resolvedBy")
        val _columnIndexOfResolutionType: Int = getColumnIndexOrThrow(_stmt, "resolutionType")
        val _columnIndexOfResolutionNotes: Int = getColumnIndexOrThrow(_stmt, "resolutionNotes")
        val _columnIndexOfRefundedAmount: Int = getColumnIndexOrThrow(_stmt, "refundedAmount")
        val _columnIndexOfLastResponseAt: Int = getColumnIndexOrThrow(_stmt, "lastResponseAt")
        val _columnIndexOfResponseCount: Int = getColumnIndexOrThrow(_stmt, "responseCount")
        val _columnIndexOfEscalatedAt: Int = getColumnIndexOrThrow(_stmt, "escalatedAt")
        val _columnIndexOfEscalationReason: Int = getColumnIndexOrThrow(_stmt, "escalationReason")
        val _columnIndexOfAdminNotes: Int = getColumnIndexOrThrow(_stmt, "adminNotes")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _result: MutableList<OrderDisputeEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: OrderDisputeEntity
          val _tmpDisputeId: String
          _tmpDisputeId = _stmt.getText(_columnIndexOfDisputeId)
          val _tmpOrderId: String
          _tmpOrderId = _stmt.getText(_columnIndexOfOrderId)
          val _tmpRaisedBy: String
          _tmpRaisedBy = _stmt.getText(_columnIndexOfRaisedBy)
          val _tmpRaisedByRole: String
          _tmpRaisedByRole = _stmt.getText(_columnIndexOfRaisedByRole)
          val _tmpAgainstUserId: String
          _tmpAgainstUserId = _stmt.getText(_columnIndexOfAgainstUserId)
          val _tmpReason: String
          _tmpReason = _stmt.getText(_columnIndexOfReason)
          val _tmpDescription: String
          _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          val _tmpRequestedResolution: String?
          if (_stmt.isNull(_columnIndexOfRequestedResolution)) {
            _tmpRequestedResolution = null
          } else {
            _tmpRequestedResolution = _stmt.getText(_columnIndexOfRequestedResolution)
          }
          val _tmpClaimedAmount: Double?
          if (_stmt.isNull(_columnIndexOfClaimedAmount)) {
            _tmpClaimedAmount = null
          } else {
            _tmpClaimedAmount = _stmt.getDouble(_columnIndexOfClaimedAmount)
          }
          val _tmpEvidenceIds: String?
          if (_stmt.isNull(_columnIndexOfEvidenceIds)) {
            _tmpEvidenceIds = null
          } else {
            _tmpEvidenceIds = _stmt.getText(_columnIndexOfEvidenceIds)
          }
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpResolvedAt: Long?
          if (_stmt.isNull(_columnIndexOfResolvedAt)) {
            _tmpResolvedAt = null
          } else {
            _tmpResolvedAt = _stmt.getLong(_columnIndexOfResolvedAt)
          }
          val _tmpResolvedBy: String?
          if (_stmt.isNull(_columnIndexOfResolvedBy)) {
            _tmpResolvedBy = null
          } else {
            _tmpResolvedBy = _stmt.getText(_columnIndexOfResolvedBy)
          }
          val _tmpResolutionType: String?
          if (_stmt.isNull(_columnIndexOfResolutionType)) {
            _tmpResolutionType = null
          } else {
            _tmpResolutionType = _stmt.getText(_columnIndexOfResolutionType)
          }
          val _tmpResolutionNotes: String?
          if (_stmt.isNull(_columnIndexOfResolutionNotes)) {
            _tmpResolutionNotes = null
          } else {
            _tmpResolutionNotes = _stmt.getText(_columnIndexOfResolutionNotes)
          }
          val _tmpRefundedAmount: Double?
          if (_stmt.isNull(_columnIndexOfRefundedAmount)) {
            _tmpRefundedAmount = null
          } else {
            _tmpRefundedAmount = _stmt.getDouble(_columnIndexOfRefundedAmount)
          }
          val _tmpLastResponseAt: Long?
          if (_stmt.isNull(_columnIndexOfLastResponseAt)) {
            _tmpLastResponseAt = null
          } else {
            _tmpLastResponseAt = _stmt.getLong(_columnIndexOfLastResponseAt)
          }
          val _tmpResponseCount: Int
          _tmpResponseCount = _stmt.getLong(_columnIndexOfResponseCount).toInt()
          val _tmpEscalatedAt: Long?
          if (_stmt.isNull(_columnIndexOfEscalatedAt)) {
            _tmpEscalatedAt = null
          } else {
            _tmpEscalatedAt = _stmt.getLong(_columnIndexOfEscalatedAt)
          }
          val _tmpEscalationReason: String?
          if (_stmt.isNull(_columnIndexOfEscalationReason)) {
            _tmpEscalationReason = null
          } else {
            _tmpEscalationReason = _stmt.getText(_columnIndexOfEscalationReason)
          }
          val _tmpAdminNotes: String?
          if (_stmt.isNull(_columnIndexOfAdminNotes)) {
            _tmpAdminNotes = null
          } else {
            _tmpAdminNotes = _stmt.getText(_columnIndexOfAdminNotes)
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
              OrderDisputeEntity(_tmpDisputeId,_tmpOrderId,_tmpRaisedBy,_tmpRaisedByRole,_tmpAgainstUserId,_tmpReason,_tmpDescription,_tmpRequestedResolution,_tmpClaimedAmount,_tmpEvidenceIds,_tmpStatus,_tmpResolvedAt,_tmpResolvedBy,_tmpResolutionType,_tmpResolutionNotes,_tmpRefundedAmount,_tmpLastResponseAt,_tmpResponseCount,_tmpEscalatedAt,_tmpEscalationReason,_tmpAdminNotes,_tmpCreatedAt,_tmpUpdatedAt,_tmpDirty)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getDirtyRecords(): List<OrderDisputeEntity> {
    val _sql: String = "SELECT * FROM order_disputes WHERE dirty = 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfDisputeId: Int = getColumnIndexOrThrow(_stmt, "disputeId")
        val _columnIndexOfOrderId: Int = getColumnIndexOrThrow(_stmt, "orderId")
        val _columnIndexOfRaisedBy: Int = getColumnIndexOrThrow(_stmt, "raisedBy")
        val _columnIndexOfRaisedByRole: Int = getColumnIndexOrThrow(_stmt, "raisedByRole")
        val _columnIndexOfAgainstUserId: Int = getColumnIndexOrThrow(_stmt, "againstUserId")
        val _columnIndexOfReason: Int = getColumnIndexOrThrow(_stmt, "reason")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfRequestedResolution: Int = getColumnIndexOrThrow(_stmt,
            "requestedResolution")
        val _columnIndexOfClaimedAmount: Int = getColumnIndexOrThrow(_stmt, "claimedAmount")
        val _columnIndexOfEvidenceIds: Int = getColumnIndexOrThrow(_stmt, "evidenceIds")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfResolvedAt: Int = getColumnIndexOrThrow(_stmt, "resolvedAt")
        val _columnIndexOfResolvedBy: Int = getColumnIndexOrThrow(_stmt, "resolvedBy")
        val _columnIndexOfResolutionType: Int = getColumnIndexOrThrow(_stmt, "resolutionType")
        val _columnIndexOfResolutionNotes: Int = getColumnIndexOrThrow(_stmt, "resolutionNotes")
        val _columnIndexOfRefundedAmount: Int = getColumnIndexOrThrow(_stmt, "refundedAmount")
        val _columnIndexOfLastResponseAt: Int = getColumnIndexOrThrow(_stmt, "lastResponseAt")
        val _columnIndexOfResponseCount: Int = getColumnIndexOrThrow(_stmt, "responseCount")
        val _columnIndexOfEscalatedAt: Int = getColumnIndexOrThrow(_stmt, "escalatedAt")
        val _columnIndexOfEscalationReason: Int = getColumnIndexOrThrow(_stmt, "escalationReason")
        val _columnIndexOfAdminNotes: Int = getColumnIndexOrThrow(_stmt, "adminNotes")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _result: MutableList<OrderDisputeEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: OrderDisputeEntity
          val _tmpDisputeId: String
          _tmpDisputeId = _stmt.getText(_columnIndexOfDisputeId)
          val _tmpOrderId: String
          _tmpOrderId = _stmt.getText(_columnIndexOfOrderId)
          val _tmpRaisedBy: String
          _tmpRaisedBy = _stmt.getText(_columnIndexOfRaisedBy)
          val _tmpRaisedByRole: String
          _tmpRaisedByRole = _stmt.getText(_columnIndexOfRaisedByRole)
          val _tmpAgainstUserId: String
          _tmpAgainstUserId = _stmt.getText(_columnIndexOfAgainstUserId)
          val _tmpReason: String
          _tmpReason = _stmt.getText(_columnIndexOfReason)
          val _tmpDescription: String
          _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          val _tmpRequestedResolution: String?
          if (_stmt.isNull(_columnIndexOfRequestedResolution)) {
            _tmpRequestedResolution = null
          } else {
            _tmpRequestedResolution = _stmt.getText(_columnIndexOfRequestedResolution)
          }
          val _tmpClaimedAmount: Double?
          if (_stmt.isNull(_columnIndexOfClaimedAmount)) {
            _tmpClaimedAmount = null
          } else {
            _tmpClaimedAmount = _stmt.getDouble(_columnIndexOfClaimedAmount)
          }
          val _tmpEvidenceIds: String?
          if (_stmt.isNull(_columnIndexOfEvidenceIds)) {
            _tmpEvidenceIds = null
          } else {
            _tmpEvidenceIds = _stmt.getText(_columnIndexOfEvidenceIds)
          }
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpResolvedAt: Long?
          if (_stmt.isNull(_columnIndexOfResolvedAt)) {
            _tmpResolvedAt = null
          } else {
            _tmpResolvedAt = _stmt.getLong(_columnIndexOfResolvedAt)
          }
          val _tmpResolvedBy: String?
          if (_stmt.isNull(_columnIndexOfResolvedBy)) {
            _tmpResolvedBy = null
          } else {
            _tmpResolvedBy = _stmt.getText(_columnIndexOfResolvedBy)
          }
          val _tmpResolutionType: String?
          if (_stmt.isNull(_columnIndexOfResolutionType)) {
            _tmpResolutionType = null
          } else {
            _tmpResolutionType = _stmt.getText(_columnIndexOfResolutionType)
          }
          val _tmpResolutionNotes: String?
          if (_stmt.isNull(_columnIndexOfResolutionNotes)) {
            _tmpResolutionNotes = null
          } else {
            _tmpResolutionNotes = _stmt.getText(_columnIndexOfResolutionNotes)
          }
          val _tmpRefundedAmount: Double?
          if (_stmt.isNull(_columnIndexOfRefundedAmount)) {
            _tmpRefundedAmount = null
          } else {
            _tmpRefundedAmount = _stmt.getDouble(_columnIndexOfRefundedAmount)
          }
          val _tmpLastResponseAt: Long?
          if (_stmt.isNull(_columnIndexOfLastResponseAt)) {
            _tmpLastResponseAt = null
          } else {
            _tmpLastResponseAt = _stmt.getLong(_columnIndexOfLastResponseAt)
          }
          val _tmpResponseCount: Int
          _tmpResponseCount = _stmt.getLong(_columnIndexOfResponseCount).toInt()
          val _tmpEscalatedAt: Long?
          if (_stmt.isNull(_columnIndexOfEscalatedAt)) {
            _tmpEscalatedAt = null
          } else {
            _tmpEscalatedAt = _stmt.getLong(_columnIndexOfEscalatedAt)
          }
          val _tmpEscalationReason: String?
          if (_stmt.isNull(_columnIndexOfEscalationReason)) {
            _tmpEscalationReason = null
          } else {
            _tmpEscalationReason = _stmt.getText(_columnIndexOfEscalationReason)
          }
          val _tmpAdminNotes: String?
          if (_stmt.isNull(_columnIndexOfAdminNotes)) {
            _tmpAdminNotes = null
          } else {
            _tmpAdminNotes = _stmt.getText(_columnIndexOfAdminNotes)
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
              OrderDisputeEntity(_tmpDisputeId,_tmpOrderId,_tmpRaisedBy,_tmpRaisedByRole,_tmpAgainstUserId,_tmpReason,_tmpDescription,_tmpRequestedResolution,_tmpClaimedAmount,_tmpEvidenceIds,_tmpStatus,_tmpResolvedAt,_tmpResolvedBy,_tmpResolutionType,_tmpResolutionNotes,_tmpRefundedAmount,_tmpLastResponseAt,_tmpResponseCount,_tmpEscalatedAt,_tmpEscalationReason,_tmpAdminNotes,_tmpCreatedAt,_tmpUpdatedAt,_tmpDirty)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun updateStatus(
    disputeId: String,
    status: String,
    updatedAt: Long,
  ) {
    val _sql: String =
        "UPDATE order_disputes SET status = ?, updatedAt = ?, dirty = 1 WHERE disputeId = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, status)
        _argIndex = 2
        _stmt.bindLong(_argIndex, updatedAt)
        _argIndex = 3
        _stmt.bindText(_argIndex, disputeId)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun escalate(
    disputeId: String,
    reason: String,
    escalatedAt: Long,
  ) {
    val _sql: String =
        "UPDATE order_disputes SET status = 'ESCALATED', escalatedAt = ?, escalationReason = ?, updatedAt = ?, dirty = 1 WHERE disputeId = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, escalatedAt)
        _argIndex = 2
        _stmt.bindText(_argIndex, reason)
        _argIndex = 3
        _stmt.bindLong(_argIndex, escalatedAt)
        _argIndex = 4
        _stmt.bindText(_argIndex, disputeId)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun resolve(
    disputeId: String,
    resolvedBy: String,
    resolutionType: String,
    notes: String?,
    refundAmount: Double?,
    resolvedAt: Long,
  ) {
    val _sql: String =
        "UPDATE order_disputes SET status = 'RESOLVED', resolvedAt = ?, resolvedBy = ?, resolutionType = ?, resolutionNotes = ?, refundedAmount = ?, updatedAt = ?, dirty = 1 WHERE disputeId = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, resolvedAt)
        _argIndex = 2
        _stmt.bindText(_argIndex, resolvedBy)
        _argIndex = 3
        _stmt.bindText(_argIndex, resolutionType)
        _argIndex = 4
        if (notes == null) {
          _stmt.bindNull(_argIndex)
        } else {
          _stmt.bindText(_argIndex, notes)
        }
        _argIndex = 5
        if (refundAmount == null) {
          _stmt.bindNull(_argIndex)
        } else {
          _stmt.bindDouble(_argIndex, refundAmount)
        }
        _argIndex = 6
        _stmt.bindLong(_argIndex, resolvedAt)
        _argIndex = 7
        _stmt.bindText(_argIndex, disputeId)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun markClean(disputeId: String) {
    val _sql: String = "UPDATE order_disputes SET dirty = 0 WHERE disputeId = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, disputeId)
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
