package com.rio.rostry.`data`.database.dao

import androidx.room.EntityDeleteOrUpdateAdapter
import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.rio.rostry.`data`.database.entity.BreedingPlanEntity
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
public class BreedingPlanDao_Impl(
  __db: RoomDatabase,
) : BreedingPlanDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfBreedingPlanEntity: EntityInsertAdapter<BreedingPlanEntity>

  private val __deleteAdapterOfBreedingPlanEntity: EntityDeleteOrUpdateAdapter<BreedingPlanEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfBreedingPlanEntity = object : EntityInsertAdapter<BreedingPlanEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `breeding_plans` (`planId`,`farmerId`,`sireId`,`sireName`,`damId`,`damName`,`createdAt`,`note`,`simulatedOffspringJson`,`status`,`priority`) VALUES (?,?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: BreedingPlanEntity) {
        statement.bindText(1, entity.planId)
        statement.bindText(2, entity.farmerId)
        val _tmpSireId: String? = entity.sireId
        if (_tmpSireId == null) {
          statement.bindNull(3)
        } else {
          statement.bindText(3, _tmpSireId)
        }
        val _tmpSireName: String? = entity.sireName
        if (_tmpSireName == null) {
          statement.bindNull(4)
        } else {
          statement.bindText(4, _tmpSireName)
        }
        val _tmpDamId: String? = entity.damId
        if (_tmpDamId == null) {
          statement.bindNull(5)
        } else {
          statement.bindText(5, _tmpDamId)
        }
        val _tmpDamName: String? = entity.damName
        if (_tmpDamName == null) {
          statement.bindNull(6)
        } else {
          statement.bindText(6, _tmpDamName)
        }
        statement.bindLong(7, entity.createdAt)
        val _tmpNote: String? = entity.note
        if (_tmpNote == null) {
          statement.bindNull(8)
        } else {
          statement.bindText(8, _tmpNote)
        }
        statement.bindText(9, entity.simulatedOffspringJson)
        statement.bindText(10, entity.status)
        statement.bindLong(11, entity.priority.toLong())
      }
    }
    this.__deleteAdapterOfBreedingPlanEntity = object :
        EntityDeleteOrUpdateAdapter<BreedingPlanEntity>() {
      protected override fun createQuery(): String =
          "DELETE FROM `breeding_plans` WHERE `planId` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: BreedingPlanEntity) {
        statement.bindText(1, entity.planId)
      }
    }
  }

  public override suspend fun insertPlan(plan: BreedingPlanEntity): Unit = performSuspending(__db,
      false, true) { _connection ->
    __insertAdapterOfBreedingPlanEntity.insert(_connection, plan)
  }

  public override suspend fun deletePlan(plan: BreedingPlanEntity): Unit = performSuspending(__db,
      false, true) { _connection ->
    __deleteAdapterOfBreedingPlanEntity.handle(_connection, plan)
  }

  public override fun getAllPlans(farmerId: String): Flow<List<BreedingPlanEntity>> {
    val _sql: String = "SELECT * FROM breeding_plans WHERE farmerId = ? ORDER BY createdAt DESC"
    return createFlow(__db, false, arrayOf("breeding_plans")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, farmerId)
        val _columnIndexOfPlanId: Int = getColumnIndexOrThrow(_stmt, "planId")
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfSireId: Int = getColumnIndexOrThrow(_stmt, "sireId")
        val _columnIndexOfSireName: Int = getColumnIndexOrThrow(_stmt, "sireName")
        val _columnIndexOfDamId: Int = getColumnIndexOrThrow(_stmt, "damId")
        val _columnIndexOfDamName: Int = getColumnIndexOrThrow(_stmt, "damName")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfNote: Int = getColumnIndexOrThrow(_stmt, "note")
        val _columnIndexOfSimulatedOffspringJson: Int = getColumnIndexOrThrow(_stmt,
            "simulatedOffspringJson")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfPriority: Int = getColumnIndexOrThrow(_stmt, "priority")
        val _result: MutableList<BreedingPlanEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: BreedingPlanEntity
          val _tmpPlanId: String
          _tmpPlanId = _stmt.getText(_columnIndexOfPlanId)
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpSireId: String?
          if (_stmt.isNull(_columnIndexOfSireId)) {
            _tmpSireId = null
          } else {
            _tmpSireId = _stmt.getText(_columnIndexOfSireId)
          }
          val _tmpSireName: String?
          if (_stmt.isNull(_columnIndexOfSireName)) {
            _tmpSireName = null
          } else {
            _tmpSireName = _stmt.getText(_columnIndexOfSireName)
          }
          val _tmpDamId: String?
          if (_stmt.isNull(_columnIndexOfDamId)) {
            _tmpDamId = null
          } else {
            _tmpDamId = _stmt.getText(_columnIndexOfDamId)
          }
          val _tmpDamName: String?
          if (_stmt.isNull(_columnIndexOfDamName)) {
            _tmpDamName = null
          } else {
            _tmpDamName = _stmt.getText(_columnIndexOfDamName)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpNote: String?
          if (_stmt.isNull(_columnIndexOfNote)) {
            _tmpNote = null
          } else {
            _tmpNote = _stmt.getText(_columnIndexOfNote)
          }
          val _tmpSimulatedOffspringJson: String
          _tmpSimulatedOffspringJson = _stmt.getText(_columnIndexOfSimulatedOffspringJson)
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpPriority: Int
          _tmpPriority = _stmt.getLong(_columnIndexOfPriority).toInt()
          _item =
              BreedingPlanEntity(_tmpPlanId,_tmpFarmerId,_tmpSireId,_tmpSireName,_tmpDamId,_tmpDamName,_tmpCreatedAt,_tmpNote,_tmpSimulatedOffspringJson,_tmpStatus,_tmpPriority)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getPlanById(planId: String): BreedingPlanEntity? {
    val _sql: String = "SELECT * FROM breeding_plans WHERE planId = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, planId)
        val _columnIndexOfPlanId: Int = getColumnIndexOrThrow(_stmt, "planId")
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfSireId: Int = getColumnIndexOrThrow(_stmt, "sireId")
        val _columnIndexOfSireName: Int = getColumnIndexOrThrow(_stmt, "sireName")
        val _columnIndexOfDamId: Int = getColumnIndexOrThrow(_stmt, "damId")
        val _columnIndexOfDamName: Int = getColumnIndexOrThrow(_stmt, "damName")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfNote: Int = getColumnIndexOrThrow(_stmt, "note")
        val _columnIndexOfSimulatedOffspringJson: Int = getColumnIndexOrThrow(_stmt,
            "simulatedOffspringJson")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfPriority: Int = getColumnIndexOrThrow(_stmt, "priority")
        val _result: BreedingPlanEntity?
        if (_stmt.step()) {
          val _tmpPlanId: String
          _tmpPlanId = _stmt.getText(_columnIndexOfPlanId)
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpSireId: String?
          if (_stmt.isNull(_columnIndexOfSireId)) {
            _tmpSireId = null
          } else {
            _tmpSireId = _stmt.getText(_columnIndexOfSireId)
          }
          val _tmpSireName: String?
          if (_stmt.isNull(_columnIndexOfSireName)) {
            _tmpSireName = null
          } else {
            _tmpSireName = _stmt.getText(_columnIndexOfSireName)
          }
          val _tmpDamId: String?
          if (_stmt.isNull(_columnIndexOfDamId)) {
            _tmpDamId = null
          } else {
            _tmpDamId = _stmt.getText(_columnIndexOfDamId)
          }
          val _tmpDamName: String?
          if (_stmt.isNull(_columnIndexOfDamName)) {
            _tmpDamName = null
          } else {
            _tmpDamName = _stmt.getText(_columnIndexOfDamName)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpNote: String?
          if (_stmt.isNull(_columnIndexOfNote)) {
            _tmpNote = null
          } else {
            _tmpNote = _stmt.getText(_columnIndexOfNote)
          }
          val _tmpSimulatedOffspringJson: String
          _tmpSimulatedOffspringJson = _stmt.getText(_columnIndexOfSimulatedOffspringJson)
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpPriority: Int
          _tmpPriority = _stmt.getLong(_columnIndexOfPriority).toInt()
          _result =
              BreedingPlanEntity(_tmpPlanId,_tmpFarmerId,_tmpSireId,_tmpSireName,_tmpDamId,_tmpDamName,_tmpCreatedAt,_tmpNote,_tmpSimulatedOffspringJson,_tmpStatus,_tmpPriority)
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
