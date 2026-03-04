package com.rio.rostry.`data`.database.dao

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.rio.rostry.`data`.database.entity.ComplianceRuleEntity
import javax.`annotation`.processing.Generated
import kotlin.Boolean
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import kotlin.collections.MutableList
import kotlin.collections.mutableListOf
import kotlin.reflect.KClass

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class ComplianceRuleDao_Impl(
  __db: RoomDatabase,
) : ComplianceRuleDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfComplianceRuleEntity: EntityInsertAdapter<ComplianceRuleEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfComplianceRuleEntity = object :
        EntityInsertAdapter<ComplianceRuleEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `compliance_rules` (`ruleId`,`jurisdiction`,`ruleType`,`assetTypes`,`ruleData`,`isActive`,`effectiveFrom`,`effectiveUntil`,`severity`,`description`,`reminderDays`,`createdAt`,`updatedAt`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: ComplianceRuleEntity) {
        statement.bindText(1, entity.ruleId)
        statement.bindText(2, entity.jurisdiction)
        statement.bindText(3, entity.ruleType)
        statement.bindText(4, entity.assetTypes)
        statement.bindText(5, entity.ruleData)
        val _tmp: Int = if (entity.isActive) 1 else 0
        statement.bindLong(6, _tmp.toLong())
        statement.bindLong(7, entity.effectiveFrom)
        val _tmpEffectiveUntil: Long? = entity.effectiveUntil
        if (_tmpEffectiveUntil == null) {
          statement.bindNull(8)
        } else {
          statement.bindLong(8, _tmpEffectiveUntil)
        }
        statement.bindText(9, entity.severity)
        statement.bindText(10, entity.description)
        val _tmpReminderDays: Int? = entity.reminderDays
        if (_tmpReminderDays == null) {
          statement.bindNull(11)
        } else {
          statement.bindLong(11, _tmpReminderDays.toLong())
        }
        statement.bindLong(12, entity.createdAt)
        statement.bindLong(13, entity.updatedAt)
      }
    }
  }

  public override suspend fun insert(rule: ComplianceRuleEntity): Unit = performSuspending(__db,
      false, true) { _connection ->
    __insertAdapterOfComplianceRuleEntity.insert(_connection, rule)
  }

  public override suspend fun getActiveRulesByJurisdiction(jurisdiction: String):
      List<ComplianceRuleEntity> {
    val _sql: String = "SELECT * FROM compliance_rules WHERE jurisdiction = ? AND isActive = 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, jurisdiction)
        val _columnIndexOfRuleId: Int = getColumnIndexOrThrow(_stmt, "ruleId")
        val _columnIndexOfJurisdiction: Int = getColumnIndexOrThrow(_stmt, "jurisdiction")
        val _columnIndexOfRuleType: Int = getColumnIndexOrThrow(_stmt, "ruleType")
        val _columnIndexOfAssetTypes: Int = getColumnIndexOrThrow(_stmt, "assetTypes")
        val _columnIndexOfRuleData: Int = getColumnIndexOrThrow(_stmt, "ruleData")
        val _columnIndexOfIsActive: Int = getColumnIndexOrThrow(_stmt, "isActive")
        val _columnIndexOfEffectiveFrom: Int = getColumnIndexOrThrow(_stmt, "effectiveFrom")
        val _columnIndexOfEffectiveUntil: Int = getColumnIndexOrThrow(_stmt, "effectiveUntil")
        val _columnIndexOfSeverity: Int = getColumnIndexOrThrow(_stmt, "severity")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfReminderDays: Int = getColumnIndexOrThrow(_stmt, "reminderDays")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _result: MutableList<ComplianceRuleEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: ComplianceRuleEntity
          val _tmpRuleId: String
          _tmpRuleId = _stmt.getText(_columnIndexOfRuleId)
          val _tmpJurisdiction: String
          _tmpJurisdiction = _stmt.getText(_columnIndexOfJurisdiction)
          val _tmpRuleType: String
          _tmpRuleType = _stmt.getText(_columnIndexOfRuleType)
          val _tmpAssetTypes: String
          _tmpAssetTypes = _stmt.getText(_columnIndexOfAssetTypes)
          val _tmpRuleData: String
          _tmpRuleData = _stmt.getText(_columnIndexOfRuleData)
          val _tmpIsActive: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsActive).toInt()
          _tmpIsActive = _tmp != 0
          val _tmpEffectiveFrom: Long
          _tmpEffectiveFrom = _stmt.getLong(_columnIndexOfEffectiveFrom)
          val _tmpEffectiveUntil: Long?
          if (_stmt.isNull(_columnIndexOfEffectiveUntil)) {
            _tmpEffectiveUntil = null
          } else {
            _tmpEffectiveUntil = _stmt.getLong(_columnIndexOfEffectiveUntil)
          }
          val _tmpSeverity: String
          _tmpSeverity = _stmt.getText(_columnIndexOfSeverity)
          val _tmpDescription: String
          _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          val _tmpReminderDays: Int?
          if (_stmt.isNull(_columnIndexOfReminderDays)) {
            _tmpReminderDays = null
          } else {
            _tmpReminderDays = _stmt.getLong(_columnIndexOfReminderDays).toInt()
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          _item =
              ComplianceRuleEntity(_tmpRuleId,_tmpJurisdiction,_tmpRuleType,_tmpAssetTypes,_tmpRuleData,_tmpIsActive,_tmpEffectiveFrom,_tmpEffectiveUntil,_tmpSeverity,_tmpDescription,_tmpReminderDays,_tmpCreatedAt,_tmpUpdatedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getRuleById(ruleId: String): ComplianceRuleEntity? {
    val _sql: String = "SELECT * FROM compliance_rules WHERE ruleId = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, ruleId)
        val _columnIndexOfRuleId: Int = getColumnIndexOrThrow(_stmt, "ruleId")
        val _columnIndexOfJurisdiction: Int = getColumnIndexOrThrow(_stmt, "jurisdiction")
        val _columnIndexOfRuleType: Int = getColumnIndexOrThrow(_stmt, "ruleType")
        val _columnIndexOfAssetTypes: Int = getColumnIndexOrThrow(_stmt, "assetTypes")
        val _columnIndexOfRuleData: Int = getColumnIndexOrThrow(_stmt, "ruleData")
        val _columnIndexOfIsActive: Int = getColumnIndexOrThrow(_stmt, "isActive")
        val _columnIndexOfEffectiveFrom: Int = getColumnIndexOrThrow(_stmt, "effectiveFrom")
        val _columnIndexOfEffectiveUntil: Int = getColumnIndexOrThrow(_stmt, "effectiveUntil")
        val _columnIndexOfSeverity: Int = getColumnIndexOrThrow(_stmt, "severity")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfReminderDays: Int = getColumnIndexOrThrow(_stmt, "reminderDays")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _result: ComplianceRuleEntity?
        if (_stmt.step()) {
          val _tmpRuleId: String
          _tmpRuleId = _stmt.getText(_columnIndexOfRuleId)
          val _tmpJurisdiction: String
          _tmpJurisdiction = _stmt.getText(_columnIndexOfJurisdiction)
          val _tmpRuleType: String
          _tmpRuleType = _stmt.getText(_columnIndexOfRuleType)
          val _tmpAssetTypes: String
          _tmpAssetTypes = _stmt.getText(_columnIndexOfAssetTypes)
          val _tmpRuleData: String
          _tmpRuleData = _stmt.getText(_columnIndexOfRuleData)
          val _tmpIsActive: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsActive).toInt()
          _tmpIsActive = _tmp != 0
          val _tmpEffectiveFrom: Long
          _tmpEffectiveFrom = _stmt.getLong(_columnIndexOfEffectiveFrom)
          val _tmpEffectiveUntil: Long?
          if (_stmt.isNull(_columnIndexOfEffectiveUntil)) {
            _tmpEffectiveUntil = null
          } else {
            _tmpEffectiveUntil = _stmt.getLong(_columnIndexOfEffectiveUntil)
          }
          val _tmpSeverity: String
          _tmpSeverity = _stmt.getText(_columnIndexOfSeverity)
          val _tmpDescription: String
          _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          val _tmpReminderDays: Int?
          if (_stmt.isNull(_columnIndexOfReminderDays)) {
            _tmpReminderDays = null
          } else {
            _tmpReminderDays = _stmt.getLong(_columnIndexOfReminderDays).toInt()
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          _result =
              ComplianceRuleEntity(_tmpRuleId,_tmpJurisdiction,_tmpRuleType,_tmpAssetTypes,_tmpRuleData,_tmpIsActive,_tmpEffectiveFrom,_tmpEffectiveUntil,_tmpSeverity,_tmpDescription,_tmpReminderDays,_tmpCreatedAt,_tmpUpdatedAt)
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
