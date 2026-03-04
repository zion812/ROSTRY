package com.rio.rostry.`data`.database.dao

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.rio.rostry.`data`.database.entity.ReportEntity
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
public class ReportsDao_Impl(
  __db: RoomDatabase,
) : ReportsDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfReportEntity: EntityInsertAdapter<ReportEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfReportEntity = object : EntityInsertAdapter<ReportEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `reports` (`reportId`,`userId`,`type`,`periodStart`,`periodEnd`,`format`,`uri`,`createdAt`) VALUES (?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: ReportEntity) {
        statement.bindText(1, entity.reportId)
        statement.bindText(2, entity.userId)
        statement.bindText(3, entity.type)
        statement.bindLong(4, entity.periodStart)
        statement.bindLong(5, entity.periodEnd)
        statement.bindText(6, entity.format)
        val _tmpUri: String? = entity.uri
        if (_tmpUri == null) {
          statement.bindNull(7)
        } else {
          statement.bindText(7, _tmpUri)
        }
        statement.bindLong(8, entity.createdAt)
      }
    }
  }

  public override suspend fun upsert(report: ReportEntity): Unit = performSuspending(__db, false,
      true) { _connection ->
    __insertAdapterOfReportEntity.insert(_connection, report)
  }

  public override fun streamReports(userId: String): Flow<List<ReportEntity>> {
    val _sql: String = "SELECT * FROM reports WHERE userId = ? ORDER BY createdAt DESC"
    return createFlow(__db, false, arrayOf("reports")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, userId)
        val _columnIndexOfReportId: Int = getColumnIndexOrThrow(_stmt, "reportId")
        val _columnIndexOfUserId: Int = getColumnIndexOrThrow(_stmt, "userId")
        val _columnIndexOfType: Int = getColumnIndexOrThrow(_stmt, "type")
        val _columnIndexOfPeriodStart: Int = getColumnIndexOrThrow(_stmt, "periodStart")
        val _columnIndexOfPeriodEnd: Int = getColumnIndexOrThrow(_stmt, "periodEnd")
        val _columnIndexOfFormat: Int = getColumnIndexOrThrow(_stmt, "format")
        val _columnIndexOfUri: Int = getColumnIndexOrThrow(_stmt, "uri")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _result: MutableList<ReportEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: ReportEntity
          val _tmpReportId: String
          _tmpReportId = _stmt.getText(_columnIndexOfReportId)
          val _tmpUserId: String
          _tmpUserId = _stmt.getText(_columnIndexOfUserId)
          val _tmpType: String
          _tmpType = _stmt.getText(_columnIndexOfType)
          val _tmpPeriodStart: Long
          _tmpPeriodStart = _stmt.getLong(_columnIndexOfPeriodStart)
          val _tmpPeriodEnd: Long
          _tmpPeriodEnd = _stmt.getLong(_columnIndexOfPeriodEnd)
          val _tmpFormat: String
          _tmpFormat = _stmt.getText(_columnIndexOfFormat)
          val _tmpUri: String?
          if (_stmt.isNull(_columnIndexOfUri)) {
            _tmpUri = null
          } else {
            _tmpUri = _stmt.getText(_columnIndexOfUri)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          _item =
              ReportEntity(_tmpReportId,_tmpUserId,_tmpType,_tmpPeriodStart,_tmpPeriodEnd,_tmpFormat,_tmpUri,_tmpCreatedAt)
          _result.add(_item)
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
