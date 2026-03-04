package com.rio.rostry.`data`.database.dao

import androidx.room.EntityDeleteOrUpdateAdapter
import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.rio.rostry.`data`.database.entity.MedicalEventEntity
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
public class MedicalEventDao_Impl(
  __db: RoomDatabase,
) : MedicalEventDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfMedicalEventEntity: EntityInsertAdapter<MedicalEventEntity>

  private val __updateAdapterOfMedicalEventEntity: EntityDeleteOrUpdateAdapter<MedicalEventEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfMedicalEventEntity = object : EntityInsertAdapter<MedicalEventEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `medical_events` (`eventId`,`birdId`,`farmerId`,`eventType`,`severity`,`eventDate`,`resolvedDate`,`diagnosis`,`symptoms`,`treatment`,`medication`,`dosage`,`treatmentDuration`,`status`,`outcome`,`treatedBy`,`vetVisit`,`vetNotes`,`cost`,`notes`,`mediaUrlsJson`,`createdAt`,`updatedAt`,`dirty`,`syncedAt`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: MedicalEventEntity) {
        statement.bindText(1, entity.eventId)
        statement.bindText(2, entity.birdId)
        statement.bindText(3, entity.farmerId)
        statement.bindText(4, entity.eventType)
        statement.bindText(5, entity.severity)
        statement.bindLong(6, entity.eventDate)
        val _tmpResolvedDate: Long? = entity.resolvedDate
        if (_tmpResolvedDate == null) {
          statement.bindNull(7)
        } else {
          statement.bindLong(7, _tmpResolvedDate)
        }
        val _tmpDiagnosis: String? = entity.diagnosis
        if (_tmpDiagnosis == null) {
          statement.bindNull(8)
        } else {
          statement.bindText(8, _tmpDiagnosis)
        }
        val _tmpSymptoms: String? = entity.symptoms
        if (_tmpSymptoms == null) {
          statement.bindNull(9)
        } else {
          statement.bindText(9, _tmpSymptoms)
        }
        val _tmpTreatment: String? = entity.treatment
        if (_tmpTreatment == null) {
          statement.bindNull(10)
        } else {
          statement.bindText(10, _tmpTreatment)
        }
        val _tmpMedication: String? = entity.medication
        if (_tmpMedication == null) {
          statement.bindNull(11)
        } else {
          statement.bindText(11, _tmpMedication)
        }
        val _tmpDosage: String? = entity.dosage
        if (_tmpDosage == null) {
          statement.bindNull(12)
        } else {
          statement.bindText(12, _tmpDosage)
        }
        val _tmpTreatmentDuration: String? = entity.treatmentDuration
        if (_tmpTreatmentDuration == null) {
          statement.bindNull(13)
        } else {
          statement.bindText(13, _tmpTreatmentDuration)
        }
        statement.bindText(14, entity.status)
        val _tmpOutcome: String? = entity.outcome
        if (_tmpOutcome == null) {
          statement.bindNull(15)
        } else {
          statement.bindText(15, _tmpOutcome)
        }
        val _tmpTreatedBy: String? = entity.treatedBy
        if (_tmpTreatedBy == null) {
          statement.bindNull(16)
        } else {
          statement.bindText(16, _tmpTreatedBy)
        }
        val _tmp: Int = if (entity.vetVisit) 1 else 0
        statement.bindLong(17, _tmp.toLong())
        val _tmpVetNotes: String? = entity.vetNotes
        if (_tmpVetNotes == null) {
          statement.bindNull(18)
        } else {
          statement.bindText(18, _tmpVetNotes)
        }
        val _tmpCost: Double? = entity.cost
        if (_tmpCost == null) {
          statement.bindNull(19)
        } else {
          statement.bindDouble(19, _tmpCost)
        }
        val _tmpNotes: String? = entity.notes
        if (_tmpNotes == null) {
          statement.bindNull(20)
        } else {
          statement.bindText(20, _tmpNotes)
        }
        val _tmpMediaUrlsJson: String? = entity.mediaUrlsJson
        if (_tmpMediaUrlsJson == null) {
          statement.bindNull(21)
        } else {
          statement.bindText(21, _tmpMediaUrlsJson)
        }
        statement.bindLong(22, entity.createdAt)
        statement.bindLong(23, entity.updatedAt)
        val _tmp_1: Int = if (entity.dirty) 1 else 0
        statement.bindLong(24, _tmp_1.toLong())
        val _tmpSyncedAt: Long? = entity.syncedAt
        if (_tmpSyncedAt == null) {
          statement.bindNull(25)
        } else {
          statement.bindLong(25, _tmpSyncedAt)
        }
      }
    }
    this.__updateAdapterOfMedicalEventEntity = object :
        EntityDeleteOrUpdateAdapter<MedicalEventEntity>() {
      protected override fun createQuery(): String =
          "UPDATE OR ABORT `medical_events` SET `eventId` = ?,`birdId` = ?,`farmerId` = ?,`eventType` = ?,`severity` = ?,`eventDate` = ?,`resolvedDate` = ?,`diagnosis` = ?,`symptoms` = ?,`treatment` = ?,`medication` = ?,`dosage` = ?,`treatmentDuration` = ?,`status` = ?,`outcome` = ?,`treatedBy` = ?,`vetVisit` = ?,`vetNotes` = ?,`cost` = ?,`notes` = ?,`mediaUrlsJson` = ?,`createdAt` = ?,`updatedAt` = ?,`dirty` = ?,`syncedAt` = ? WHERE `eventId` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: MedicalEventEntity) {
        statement.bindText(1, entity.eventId)
        statement.bindText(2, entity.birdId)
        statement.bindText(3, entity.farmerId)
        statement.bindText(4, entity.eventType)
        statement.bindText(5, entity.severity)
        statement.bindLong(6, entity.eventDate)
        val _tmpResolvedDate: Long? = entity.resolvedDate
        if (_tmpResolvedDate == null) {
          statement.bindNull(7)
        } else {
          statement.bindLong(7, _tmpResolvedDate)
        }
        val _tmpDiagnosis: String? = entity.diagnosis
        if (_tmpDiagnosis == null) {
          statement.bindNull(8)
        } else {
          statement.bindText(8, _tmpDiagnosis)
        }
        val _tmpSymptoms: String? = entity.symptoms
        if (_tmpSymptoms == null) {
          statement.bindNull(9)
        } else {
          statement.bindText(9, _tmpSymptoms)
        }
        val _tmpTreatment: String? = entity.treatment
        if (_tmpTreatment == null) {
          statement.bindNull(10)
        } else {
          statement.bindText(10, _tmpTreatment)
        }
        val _tmpMedication: String? = entity.medication
        if (_tmpMedication == null) {
          statement.bindNull(11)
        } else {
          statement.bindText(11, _tmpMedication)
        }
        val _tmpDosage: String? = entity.dosage
        if (_tmpDosage == null) {
          statement.bindNull(12)
        } else {
          statement.bindText(12, _tmpDosage)
        }
        val _tmpTreatmentDuration: String? = entity.treatmentDuration
        if (_tmpTreatmentDuration == null) {
          statement.bindNull(13)
        } else {
          statement.bindText(13, _tmpTreatmentDuration)
        }
        statement.bindText(14, entity.status)
        val _tmpOutcome: String? = entity.outcome
        if (_tmpOutcome == null) {
          statement.bindNull(15)
        } else {
          statement.bindText(15, _tmpOutcome)
        }
        val _tmpTreatedBy: String? = entity.treatedBy
        if (_tmpTreatedBy == null) {
          statement.bindNull(16)
        } else {
          statement.bindText(16, _tmpTreatedBy)
        }
        val _tmp: Int = if (entity.vetVisit) 1 else 0
        statement.bindLong(17, _tmp.toLong())
        val _tmpVetNotes: String? = entity.vetNotes
        if (_tmpVetNotes == null) {
          statement.bindNull(18)
        } else {
          statement.bindText(18, _tmpVetNotes)
        }
        val _tmpCost: Double? = entity.cost
        if (_tmpCost == null) {
          statement.bindNull(19)
        } else {
          statement.bindDouble(19, _tmpCost)
        }
        val _tmpNotes: String? = entity.notes
        if (_tmpNotes == null) {
          statement.bindNull(20)
        } else {
          statement.bindText(20, _tmpNotes)
        }
        val _tmpMediaUrlsJson: String? = entity.mediaUrlsJson
        if (_tmpMediaUrlsJson == null) {
          statement.bindNull(21)
        } else {
          statement.bindText(21, _tmpMediaUrlsJson)
        }
        statement.bindLong(22, entity.createdAt)
        statement.bindLong(23, entity.updatedAt)
        val _tmp_1: Int = if (entity.dirty) 1 else 0
        statement.bindLong(24, _tmp_1.toLong())
        val _tmpSyncedAt: Long? = entity.syncedAt
        if (_tmpSyncedAt == null) {
          statement.bindNull(25)
        } else {
          statement.bindLong(25, _tmpSyncedAt)
        }
        statement.bindText(26, entity.eventId)
      }
    }
  }

  public override suspend fun insert(event: MedicalEventEntity): Unit = performSuspending(__db,
      false, true) { _connection ->
    __insertAdapterOfMedicalEventEntity.insert(_connection, event)
  }

  public override suspend fun insertAll(events: List<MedicalEventEntity>): Unit =
      performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfMedicalEventEntity.insert(_connection, events)
  }

  public override suspend fun update(event: MedicalEventEntity): Unit = performSuspending(__db,
      false, true) { _connection ->
    __updateAdapterOfMedicalEventEntity.handle(_connection, event)
  }

  public override suspend fun findById(eventId: String): MedicalEventEntity? {
    val _sql: String = "SELECT * FROM medical_events WHERE eventId = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, eventId)
        val _columnIndexOfEventId: Int = getColumnIndexOrThrow(_stmt, "eventId")
        val _columnIndexOfBirdId: Int = getColumnIndexOrThrow(_stmt, "birdId")
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfEventType: Int = getColumnIndexOrThrow(_stmt, "eventType")
        val _columnIndexOfSeverity: Int = getColumnIndexOrThrow(_stmt, "severity")
        val _columnIndexOfEventDate: Int = getColumnIndexOrThrow(_stmt, "eventDate")
        val _columnIndexOfResolvedDate: Int = getColumnIndexOrThrow(_stmt, "resolvedDate")
        val _columnIndexOfDiagnosis: Int = getColumnIndexOrThrow(_stmt, "diagnosis")
        val _columnIndexOfSymptoms: Int = getColumnIndexOrThrow(_stmt, "symptoms")
        val _columnIndexOfTreatment: Int = getColumnIndexOrThrow(_stmt, "treatment")
        val _columnIndexOfMedication: Int = getColumnIndexOrThrow(_stmt, "medication")
        val _columnIndexOfDosage: Int = getColumnIndexOrThrow(_stmt, "dosage")
        val _columnIndexOfTreatmentDuration: Int = getColumnIndexOrThrow(_stmt, "treatmentDuration")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfOutcome: Int = getColumnIndexOrThrow(_stmt, "outcome")
        val _columnIndexOfTreatedBy: Int = getColumnIndexOrThrow(_stmt, "treatedBy")
        val _columnIndexOfVetVisit: Int = getColumnIndexOrThrow(_stmt, "vetVisit")
        val _columnIndexOfVetNotes: Int = getColumnIndexOrThrow(_stmt, "vetNotes")
        val _columnIndexOfCost: Int = getColumnIndexOrThrow(_stmt, "cost")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfMediaUrlsJson: Int = getColumnIndexOrThrow(_stmt, "mediaUrlsJson")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _result: MedicalEventEntity?
        if (_stmt.step()) {
          val _tmpEventId: String
          _tmpEventId = _stmt.getText(_columnIndexOfEventId)
          val _tmpBirdId: String
          _tmpBirdId = _stmt.getText(_columnIndexOfBirdId)
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpEventType: String
          _tmpEventType = _stmt.getText(_columnIndexOfEventType)
          val _tmpSeverity: String
          _tmpSeverity = _stmt.getText(_columnIndexOfSeverity)
          val _tmpEventDate: Long
          _tmpEventDate = _stmt.getLong(_columnIndexOfEventDate)
          val _tmpResolvedDate: Long?
          if (_stmt.isNull(_columnIndexOfResolvedDate)) {
            _tmpResolvedDate = null
          } else {
            _tmpResolvedDate = _stmt.getLong(_columnIndexOfResolvedDate)
          }
          val _tmpDiagnosis: String?
          if (_stmt.isNull(_columnIndexOfDiagnosis)) {
            _tmpDiagnosis = null
          } else {
            _tmpDiagnosis = _stmt.getText(_columnIndexOfDiagnosis)
          }
          val _tmpSymptoms: String?
          if (_stmt.isNull(_columnIndexOfSymptoms)) {
            _tmpSymptoms = null
          } else {
            _tmpSymptoms = _stmt.getText(_columnIndexOfSymptoms)
          }
          val _tmpTreatment: String?
          if (_stmt.isNull(_columnIndexOfTreatment)) {
            _tmpTreatment = null
          } else {
            _tmpTreatment = _stmt.getText(_columnIndexOfTreatment)
          }
          val _tmpMedication: String?
          if (_stmt.isNull(_columnIndexOfMedication)) {
            _tmpMedication = null
          } else {
            _tmpMedication = _stmt.getText(_columnIndexOfMedication)
          }
          val _tmpDosage: String?
          if (_stmt.isNull(_columnIndexOfDosage)) {
            _tmpDosage = null
          } else {
            _tmpDosage = _stmt.getText(_columnIndexOfDosage)
          }
          val _tmpTreatmentDuration: String?
          if (_stmt.isNull(_columnIndexOfTreatmentDuration)) {
            _tmpTreatmentDuration = null
          } else {
            _tmpTreatmentDuration = _stmt.getText(_columnIndexOfTreatmentDuration)
          }
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpOutcome: String?
          if (_stmt.isNull(_columnIndexOfOutcome)) {
            _tmpOutcome = null
          } else {
            _tmpOutcome = _stmt.getText(_columnIndexOfOutcome)
          }
          val _tmpTreatedBy: String?
          if (_stmt.isNull(_columnIndexOfTreatedBy)) {
            _tmpTreatedBy = null
          } else {
            _tmpTreatedBy = _stmt.getText(_columnIndexOfTreatedBy)
          }
          val _tmpVetVisit: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfVetVisit).toInt()
          _tmpVetVisit = _tmp != 0
          val _tmpVetNotes: String?
          if (_stmt.isNull(_columnIndexOfVetNotes)) {
            _tmpVetNotes = null
          } else {
            _tmpVetNotes = _stmt.getText(_columnIndexOfVetNotes)
          }
          val _tmpCost: Double?
          if (_stmt.isNull(_columnIndexOfCost)) {
            _tmpCost = null
          } else {
            _tmpCost = _stmt.getDouble(_columnIndexOfCost)
          }
          val _tmpNotes: String?
          if (_stmt.isNull(_columnIndexOfNotes)) {
            _tmpNotes = null
          } else {
            _tmpNotes = _stmt.getText(_columnIndexOfNotes)
          }
          val _tmpMediaUrlsJson: String?
          if (_stmt.isNull(_columnIndexOfMediaUrlsJson)) {
            _tmpMediaUrlsJson = null
          } else {
            _tmpMediaUrlsJson = _stmt.getText(_columnIndexOfMediaUrlsJson)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpDirty: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp_1 != 0
          val _tmpSyncedAt: Long?
          if (_stmt.isNull(_columnIndexOfSyncedAt)) {
            _tmpSyncedAt = null
          } else {
            _tmpSyncedAt = _stmt.getLong(_columnIndexOfSyncedAt)
          }
          _result =
              MedicalEventEntity(_tmpEventId,_tmpBirdId,_tmpFarmerId,_tmpEventType,_tmpSeverity,_tmpEventDate,_tmpResolvedDate,_tmpDiagnosis,_tmpSymptoms,_tmpTreatment,_tmpMedication,_tmpDosage,_tmpTreatmentDuration,_tmpStatus,_tmpOutcome,_tmpTreatedBy,_tmpVetVisit,_tmpVetNotes,_tmpCost,_tmpNotes,_tmpMediaUrlsJson,_tmpCreatedAt,_tmpUpdatedAt,_tmpDirty,_tmpSyncedAt)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeByBird(birdId: String): Flow<List<MedicalEventEntity>> {
    val _sql: String = "SELECT * FROM medical_events WHERE birdId = ? ORDER BY eventDate DESC"
    return createFlow(__db, false, arrayOf("medical_events")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, birdId)
        val _columnIndexOfEventId: Int = getColumnIndexOrThrow(_stmt, "eventId")
        val _columnIndexOfBirdId: Int = getColumnIndexOrThrow(_stmt, "birdId")
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfEventType: Int = getColumnIndexOrThrow(_stmt, "eventType")
        val _columnIndexOfSeverity: Int = getColumnIndexOrThrow(_stmt, "severity")
        val _columnIndexOfEventDate: Int = getColumnIndexOrThrow(_stmt, "eventDate")
        val _columnIndexOfResolvedDate: Int = getColumnIndexOrThrow(_stmt, "resolvedDate")
        val _columnIndexOfDiagnosis: Int = getColumnIndexOrThrow(_stmt, "diagnosis")
        val _columnIndexOfSymptoms: Int = getColumnIndexOrThrow(_stmt, "symptoms")
        val _columnIndexOfTreatment: Int = getColumnIndexOrThrow(_stmt, "treatment")
        val _columnIndexOfMedication: Int = getColumnIndexOrThrow(_stmt, "medication")
        val _columnIndexOfDosage: Int = getColumnIndexOrThrow(_stmt, "dosage")
        val _columnIndexOfTreatmentDuration: Int = getColumnIndexOrThrow(_stmt, "treatmentDuration")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfOutcome: Int = getColumnIndexOrThrow(_stmt, "outcome")
        val _columnIndexOfTreatedBy: Int = getColumnIndexOrThrow(_stmt, "treatedBy")
        val _columnIndexOfVetVisit: Int = getColumnIndexOrThrow(_stmt, "vetVisit")
        val _columnIndexOfVetNotes: Int = getColumnIndexOrThrow(_stmt, "vetNotes")
        val _columnIndexOfCost: Int = getColumnIndexOrThrow(_stmt, "cost")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfMediaUrlsJson: Int = getColumnIndexOrThrow(_stmt, "mediaUrlsJson")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _result: MutableList<MedicalEventEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: MedicalEventEntity
          val _tmpEventId: String
          _tmpEventId = _stmt.getText(_columnIndexOfEventId)
          val _tmpBirdId: String
          _tmpBirdId = _stmt.getText(_columnIndexOfBirdId)
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpEventType: String
          _tmpEventType = _stmt.getText(_columnIndexOfEventType)
          val _tmpSeverity: String
          _tmpSeverity = _stmt.getText(_columnIndexOfSeverity)
          val _tmpEventDate: Long
          _tmpEventDate = _stmt.getLong(_columnIndexOfEventDate)
          val _tmpResolvedDate: Long?
          if (_stmt.isNull(_columnIndexOfResolvedDate)) {
            _tmpResolvedDate = null
          } else {
            _tmpResolvedDate = _stmt.getLong(_columnIndexOfResolvedDate)
          }
          val _tmpDiagnosis: String?
          if (_stmt.isNull(_columnIndexOfDiagnosis)) {
            _tmpDiagnosis = null
          } else {
            _tmpDiagnosis = _stmt.getText(_columnIndexOfDiagnosis)
          }
          val _tmpSymptoms: String?
          if (_stmt.isNull(_columnIndexOfSymptoms)) {
            _tmpSymptoms = null
          } else {
            _tmpSymptoms = _stmt.getText(_columnIndexOfSymptoms)
          }
          val _tmpTreatment: String?
          if (_stmt.isNull(_columnIndexOfTreatment)) {
            _tmpTreatment = null
          } else {
            _tmpTreatment = _stmt.getText(_columnIndexOfTreatment)
          }
          val _tmpMedication: String?
          if (_stmt.isNull(_columnIndexOfMedication)) {
            _tmpMedication = null
          } else {
            _tmpMedication = _stmt.getText(_columnIndexOfMedication)
          }
          val _tmpDosage: String?
          if (_stmt.isNull(_columnIndexOfDosage)) {
            _tmpDosage = null
          } else {
            _tmpDosage = _stmt.getText(_columnIndexOfDosage)
          }
          val _tmpTreatmentDuration: String?
          if (_stmt.isNull(_columnIndexOfTreatmentDuration)) {
            _tmpTreatmentDuration = null
          } else {
            _tmpTreatmentDuration = _stmt.getText(_columnIndexOfTreatmentDuration)
          }
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpOutcome: String?
          if (_stmt.isNull(_columnIndexOfOutcome)) {
            _tmpOutcome = null
          } else {
            _tmpOutcome = _stmt.getText(_columnIndexOfOutcome)
          }
          val _tmpTreatedBy: String?
          if (_stmt.isNull(_columnIndexOfTreatedBy)) {
            _tmpTreatedBy = null
          } else {
            _tmpTreatedBy = _stmt.getText(_columnIndexOfTreatedBy)
          }
          val _tmpVetVisit: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfVetVisit).toInt()
          _tmpVetVisit = _tmp != 0
          val _tmpVetNotes: String?
          if (_stmt.isNull(_columnIndexOfVetNotes)) {
            _tmpVetNotes = null
          } else {
            _tmpVetNotes = _stmt.getText(_columnIndexOfVetNotes)
          }
          val _tmpCost: Double?
          if (_stmt.isNull(_columnIndexOfCost)) {
            _tmpCost = null
          } else {
            _tmpCost = _stmt.getDouble(_columnIndexOfCost)
          }
          val _tmpNotes: String?
          if (_stmt.isNull(_columnIndexOfNotes)) {
            _tmpNotes = null
          } else {
            _tmpNotes = _stmt.getText(_columnIndexOfNotes)
          }
          val _tmpMediaUrlsJson: String?
          if (_stmt.isNull(_columnIndexOfMediaUrlsJson)) {
            _tmpMediaUrlsJson = null
          } else {
            _tmpMediaUrlsJson = _stmt.getText(_columnIndexOfMediaUrlsJson)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpDirty: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp_1 != 0
          val _tmpSyncedAt: Long?
          if (_stmt.isNull(_columnIndexOfSyncedAt)) {
            _tmpSyncedAt = null
          } else {
            _tmpSyncedAt = _stmt.getLong(_columnIndexOfSyncedAt)
          }
          _item =
              MedicalEventEntity(_tmpEventId,_tmpBirdId,_tmpFarmerId,_tmpEventType,_tmpSeverity,_tmpEventDate,_tmpResolvedDate,_tmpDiagnosis,_tmpSymptoms,_tmpTreatment,_tmpMedication,_tmpDosage,_tmpTreatmentDuration,_tmpStatus,_tmpOutcome,_tmpTreatedBy,_tmpVetVisit,_tmpVetNotes,_tmpCost,_tmpNotes,_tmpMediaUrlsJson,_tmpCreatedAt,_tmpUpdatedAt,_tmpDirty,_tmpSyncedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getByBird(birdId: String): List<MedicalEventEntity> {
    val _sql: String = "SELECT * FROM medical_events WHERE birdId = ? ORDER BY eventDate DESC"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, birdId)
        val _columnIndexOfEventId: Int = getColumnIndexOrThrow(_stmt, "eventId")
        val _columnIndexOfBirdId: Int = getColumnIndexOrThrow(_stmt, "birdId")
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfEventType: Int = getColumnIndexOrThrow(_stmt, "eventType")
        val _columnIndexOfSeverity: Int = getColumnIndexOrThrow(_stmt, "severity")
        val _columnIndexOfEventDate: Int = getColumnIndexOrThrow(_stmt, "eventDate")
        val _columnIndexOfResolvedDate: Int = getColumnIndexOrThrow(_stmt, "resolvedDate")
        val _columnIndexOfDiagnosis: Int = getColumnIndexOrThrow(_stmt, "diagnosis")
        val _columnIndexOfSymptoms: Int = getColumnIndexOrThrow(_stmt, "symptoms")
        val _columnIndexOfTreatment: Int = getColumnIndexOrThrow(_stmt, "treatment")
        val _columnIndexOfMedication: Int = getColumnIndexOrThrow(_stmt, "medication")
        val _columnIndexOfDosage: Int = getColumnIndexOrThrow(_stmt, "dosage")
        val _columnIndexOfTreatmentDuration: Int = getColumnIndexOrThrow(_stmt, "treatmentDuration")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfOutcome: Int = getColumnIndexOrThrow(_stmt, "outcome")
        val _columnIndexOfTreatedBy: Int = getColumnIndexOrThrow(_stmt, "treatedBy")
        val _columnIndexOfVetVisit: Int = getColumnIndexOrThrow(_stmt, "vetVisit")
        val _columnIndexOfVetNotes: Int = getColumnIndexOrThrow(_stmt, "vetNotes")
        val _columnIndexOfCost: Int = getColumnIndexOrThrow(_stmt, "cost")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfMediaUrlsJson: Int = getColumnIndexOrThrow(_stmt, "mediaUrlsJson")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _result: MutableList<MedicalEventEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: MedicalEventEntity
          val _tmpEventId: String
          _tmpEventId = _stmt.getText(_columnIndexOfEventId)
          val _tmpBirdId: String
          _tmpBirdId = _stmt.getText(_columnIndexOfBirdId)
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpEventType: String
          _tmpEventType = _stmt.getText(_columnIndexOfEventType)
          val _tmpSeverity: String
          _tmpSeverity = _stmt.getText(_columnIndexOfSeverity)
          val _tmpEventDate: Long
          _tmpEventDate = _stmt.getLong(_columnIndexOfEventDate)
          val _tmpResolvedDate: Long?
          if (_stmt.isNull(_columnIndexOfResolvedDate)) {
            _tmpResolvedDate = null
          } else {
            _tmpResolvedDate = _stmt.getLong(_columnIndexOfResolvedDate)
          }
          val _tmpDiagnosis: String?
          if (_stmt.isNull(_columnIndexOfDiagnosis)) {
            _tmpDiagnosis = null
          } else {
            _tmpDiagnosis = _stmt.getText(_columnIndexOfDiagnosis)
          }
          val _tmpSymptoms: String?
          if (_stmt.isNull(_columnIndexOfSymptoms)) {
            _tmpSymptoms = null
          } else {
            _tmpSymptoms = _stmt.getText(_columnIndexOfSymptoms)
          }
          val _tmpTreatment: String?
          if (_stmt.isNull(_columnIndexOfTreatment)) {
            _tmpTreatment = null
          } else {
            _tmpTreatment = _stmt.getText(_columnIndexOfTreatment)
          }
          val _tmpMedication: String?
          if (_stmt.isNull(_columnIndexOfMedication)) {
            _tmpMedication = null
          } else {
            _tmpMedication = _stmt.getText(_columnIndexOfMedication)
          }
          val _tmpDosage: String?
          if (_stmt.isNull(_columnIndexOfDosage)) {
            _tmpDosage = null
          } else {
            _tmpDosage = _stmt.getText(_columnIndexOfDosage)
          }
          val _tmpTreatmentDuration: String?
          if (_stmt.isNull(_columnIndexOfTreatmentDuration)) {
            _tmpTreatmentDuration = null
          } else {
            _tmpTreatmentDuration = _stmt.getText(_columnIndexOfTreatmentDuration)
          }
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpOutcome: String?
          if (_stmt.isNull(_columnIndexOfOutcome)) {
            _tmpOutcome = null
          } else {
            _tmpOutcome = _stmt.getText(_columnIndexOfOutcome)
          }
          val _tmpTreatedBy: String?
          if (_stmt.isNull(_columnIndexOfTreatedBy)) {
            _tmpTreatedBy = null
          } else {
            _tmpTreatedBy = _stmt.getText(_columnIndexOfTreatedBy)
          }
          val _tmpVetVisit: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfVetVisit).toInt()
          _tmpVetVisit = _tmp != 0
          val _tmpVetNotes: String?
          if (_stmt.isNull(_columnIndexOfVetNotes)) {
            _tmpVetNotes = null
          } else {
            _tmpVetNotes = _stmt.getText(_columnIndexOfVetNotes)
          }
          val _tmpCost: Double?
          if (_stmt.isNull(_columnIndexOfCost)) {
            _tmpCost = null
          } else {
            _tmpCost = _stmt.getDouble(_columnIndexOfCost)
          }
          val _tmpNotes: String?
          if (_stmt.isNull(_columnIndexOfNotes)) {
            _tmpNotes = null
          } else {
            _tmpNotes = _stmt.getText(_columnIndexOfNotes)
          }
          val _tmpMediaUrlsJson: String?
          if (_stmt.isNull(_columnIndexOfMediaUrlsJson)) {
            _tmpMediaUrlsJson = null
          } else {
            _tmpMediaUrlsJson = _stmt.getText(_columnIndexOfMediaUrlsJson)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpDirty: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp_1 != 0
          val _tmpSyncedAt: Long?
          if (_stmt.isNull(_columnIndexOfSyncedAt)) {
            _tmpSyncedAt = null
          } else {
            _tmpSyncedAt = _stmt.getLong(_columnIndexOfSyncedAt)
          }
          _item =
              MedicalEventEntity(_tmpEventId,_tmpBirdId,_tmpFarmerId,_tmpEventType,_tmpSeverity,_tmpEventDate,_tmpResolvedDate,_tmpDiagnosis,_tmpSymptoms,_tmpTreatment,_tmpMedication,_tmpDosage,_tmpTreatmentDuration,_tmpStatus,_tmpOutcome,_tmpTreatedBy,_tmpVetVisit,_tmpVetNotes,_tmpCost,_tmpNotes,_tmpMediaUrlsJson,_tmpCreatedAt,_tmpUpdatedAt,_tmpDirty,_tmpSyncedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeByFarmer(farmerId: String): Flow<List<MedicalEventEntity>> {
    val _sql: String = "SELECT * FROM medical_events WHERE farmerId = ? ORDER BY eventDate DESC"
    return createFlow(__db, false, arrayOf("medical_events")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, farmerId)
        val _columnIndexOfEventId: Int = getColumnIndexOrThrow(_stmt, "eventId")
        val _columnIndexOfBirdId: Int = getColumnIndexOrThrow(_stmt, "birdId")
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfEventType: Int = getColumnIndexOrThrow(_stmt, "eventType")
        val _columnIndexOfSeverity: Int = getColumnIndexOrThrow(_stmt, "severity")
        val _columnIndexOfEventDate: Int = getColumnIndexOrThrow(_stmt, "eventDate")
        val _columnIndexOfResolvedDate: Int = getColumnIndexOrThrow(_stmt, "resolvedDate")
        val _columnIndexOfDiagnosis: Int = getColumnIndexOrThrow(_stmt, "diagnosis")
        val _columnIndexOfSymptoms: Int = getColumnIndexOrThrow(_stmt, "symptoms")
        val _columnIndexOfTreatment: Int = getColumnIndexOrThrow(_stmt, "treatment")
        val _columnIndexOfMedication: Int = getColumnIndexOrThrow(_stmt, "medication")
        val _columnIndexOfDosage: Int = getColumnIndexOrThrow(_stmt, "dosage")
        val _columnIndexOfTreatmentDuration: Int = getColumnIndexOrThrow(_stmt, "treatmentDuration")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfOutcome: Int = getColumnIndexOrThrow(_stmt, "outcome")
        val _columnIndexOfTreatedBy: Int = getColumnIndexOrThrow(_stmt, "treatedBy")
        val _columnIndexOfVetVisit: Int = getColumnIndexOrThrow(_stmt, "vetVisit")
        val _columnIndexOfVetNotes: Int = getColumnIndexOrThrow(_stmt, "vetNotes")
        val _columnIndexOfCost: Int = getColumnIndexOrThrow(_stmt, "cost")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfMediaUrlsJson: Int = getColumnIndexOrThrow(_stmt, "mediaUrlsJson")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _result: MutableList<MedicalEventEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: MedicalEventEntity
          val _tmpEventId: String
          _tmpEventId = _stmt.getText(_columnIndexOfEventId)
          val _tmpBirdId: String
          _tmpBirdId = _stmt.getText(_columnIndexOfBirdId)
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpEventType: String
          _tmpEventType = _stmt.getText(_columnIndexOfEventType)
          val _tmpSeverity: String
          _tmpSeverity = _stmt.getText(_columnIndexOfSeverity)
          val _tmpEventDate: Long
          _tmpEventDate = _stmt.getLong(_columnIndexOfEventDate)
          val _tmpResolvedDate: Long?
          if (_stmt.isNull(_columnIndexOfResolvedDate)) {
            _tmpResolvedDate = null
          } else {
            _tmpResolvedDate = _stmt.getLong(_columnIndexOfResolvedDate)
          }
          val _tmpDiagnosis: String?
          if (_stmt.isNull(_columnIndexOfDiagnosis)) {
            _tmpDiagnosis = null
          } else {
            _tmpDiagnosis = _stmt.getText(_columnIndexOfDiagnosis)
          }
          val _tmpSymptoms: String?
          if (_stmt.isNull(_columnIndexOfSymptoms)) {
            _tmpSymptoms = null
          } else {
            _tmpSymptoms = _stmt.getText(_columnIndexOfSymptoms)
          }
          val _tmpTreatment: String?
          if (_stmt.isNull(_columnIndexOfTreatment)) {
            _tmpTreatment = null
          } else {
            _tmpTreatment = _stmt.getText(_columnIndexOfTreatment)
          }
          val _tmpMedication: String?
          if (_stmt.isNull(_columnIndexOfMedication)) {
            _tmpMedication = null
          } else {
            _tmpMedication = _stmt.getText(_columnIndexOfMedication)
          }
          val _tmpDosage: String?
          if (_stmt.isNull(_columnIndexOfDosage)) {
            _tmpDosage = null
          } else {
            _tmpDosage = _stmt.getText(_columnIndexOfDosage)
          }
          val _tmpTreatmentDuration: String?
          if (_stmt.isNull(_columnIndexOfTreatmentDuration)) {
            _tmpTreatmentDuration = null
          } else {
            _tmpTreatmentDuration = _stmt.getText(_columnIndexOfTreatmentDuration)
          }
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpOutcome: String?
          if (_stmt.isNull(_columnIndexOfOutcome)) {
            _tmpOutcome = null
          } else {
            _tmpOutcome = _stmt.getText(_columnIndexOfOutcome)
          }
          val _tmpTreatedBy: String?
          if (_stmt.isNull(_columnIndexOfTreatedBy)) {
            _tmpTreatedBy = null
          } else {
            _tmpTreatedBy = _stmt.getText(_columnIndexOfTreatedBy)
          }
          val _tmpVetVisit: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfVetVisit).toInt()
          _tmpVetVisit = _tmp != 0
          val _tmpVetNotes: String?
          if (_stmt.isNull(_columnIndexOfVetNotes)) {
            _tmpVetNotes = null
          } else {
            _tmpVetNotes = _stmt.getText(_columnIndexOfVetNotes)
          }
          val _tmpCost: Double?
          if (_stmt.isNull(_columnIndexOfCost)) {
            _tmpCost = null
          } else {
            _tmpCost = _stmt.getDouble(_columnIndexOfCost)
          }
          val _tmpNotes: String?
          if (_stmt.isNull(_columnIndexOfNotes)) {
            _tmpNotes = null
          } else {
            _tmpNotes = _stmt.getText(_columnIndexOfNotes)
          }
          val _tmpMediaUrlsJson: String?
          if (_stmt.isNull(_columnIndexOfMediaUrlsJson)) {
            _tmpMediaUrlsJson = null
          } else {
            _tmpMediaUrlsJson = _stmt.getText(_columnIndexOfMediaUrlsJson)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpDirty: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp_1 != 0
          val _tmpSyncedAt: Long?
          if (_stmt.isNull(_columnIndexOfSyncedAt)) {
            _tmpSyncedAt = null
          } else {
            _tmpSyncedAt = _stmt.getLong(_columnIndexOfSyncedAt)
          }
          _item =
              MedicalEventEntity(_tmpEventId,_tmpBirdId,_tmpFarmerId,_tmpEventType,_tmpSeverity,_tmpEventDate,_tmpResolvedDate,_tmpDiagnosis,_tmpSymptoms,_tmpTreatment,_tmpMedication,_tmpDosage,_tmpTreatmentDuration,_tmpStatus,_tmpOutcome,_tmpTreatedBy,_tmpVetVisit,_tmpVetNotes,_tmpCost,_tmpNotes,_tmpMediaUrlsJson,_tmpCreatedAt,_tmpUpdatedAt,_tmpDirty,_tmpSyncedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getByFarmer(farmerId: String): List<MedicalEventEntity> {
    val _sql: String = "SELECT * FROM medical_events WHERE farmerId = ? ORDER BY eventDate DESC"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, farmerId)
        val _columnIndexOfEventId: Int = getColumnIndexOrThrow(_stmt, "eventId")
        val _columnIndexOfBirdId: Int = getColumnIndexOrThrow(_stmt, "birdId")
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfEventType: Int = getColumnIndexOrThrow(_stmt, "eventType")
        val _columnIndexOfSeverity: Int = getColumnIndexOrThrow(_stmt, "severity")
        val _columnIndexOfEventDate: Int = getColumnIndexOrThrow(_stmt, "eventDate")
        val _columnIndexOfResolvedDate: Int = getColumnIndexOrThrow(_stmt, "resolvedDate")
        val _columnIndexOfDiagnosis: Int = getColumnIndexOrThrow(_stmt, "diagnosis")
        val _columnIndexOfSymptoms: Int = getColumnIndexOrThrow(_stmt, "symptoms")
        val _columnIndexOfTreatment: Int = getColumnIndexOrThrow(_stmt, "treatment")
        val _columnIndexOfMedication: Int = getColumnIndexOrThrow(_stmt, "medication")
        val _columnIndexOfDosage: Int = getColumnIndexOrThrow(_stmt, "dosage")
        val _columnIndexOfTreatmentDuration: Int = getColumnIndexOrThrow(_stmt, "treatmentDuration")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfOutcome: Int = getColumnIndexOrThrow(_stmt, "outcome")
        val _columnIndexOfTreatedBy: Int = getColumnIndexOrThrow(_stmt, "treatedBy")
        val _columnIndexOfVetVisit: Int = getColumnIndexOrThrow(_stmt, "vetVisit")
        val _columnIndexOfVetNotes: Int = getColumnIndexOrThrow(_stmt, "vetNotes")
        val _columnIndexOfCost: Int = getColumnIndexOrThrow(_stmt, "cost")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfMediaUrlsJson: Int = getColumnIndexOrThrow(_stmt, "mediaUrlsJson")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _result: MutableList<MedicalEventEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: MedicalEventEntity
          val _tmpEventId: String
          _tmpEventId = _stmt.getText(_columnIndexOfEventId)
          val _tmpBirdId: String
          _tmpBirdId = _stmt.getText(_columnIndexOfBirdId)
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpEventType: String
          _tmpEventType = _stmt.getText(_columnIndexOfEventType)
          val _tmpSeverity: String
          _tmpSeverity = _stmt.getText(_columnIndexOfSeverity)
          val _tmpEventDate: Long
          _tmpEventDate = _stmt.getLong(_columnIndexOfEventDate)
          val _tmpResolvedDate: Long?
          if (_stmt.isNull(_columnIndexOfResolvedDate)) {
            _tmpResolvedDate = null
          } else {
            _tmpResolvedDate = _stmt.getLong(_columnIndexOfResolvedDate)
          }
          val _tmpDiagnosis: String?
          if (_stmt.isNull(_columnIndexOfDiagnosis)) {
            _tmpDiagnosis = null
          } else {
            _tmpDiagnosis = _stmt.getText(_columnIndexOfDiagnosis)
          }
          val _tmpSymptoms: String?
          if (_stmt.isNull(_columnIndexOfSymptoms)) {
            _tmpSymptoms = null
          } else {
            _tmpSymptoms = _stmt.getText(_columnIndexOfSymptoms)
          }
          val _tmpTreatment: String?
          if (_stmt.isNull(_columnIndexOfTreatment)) {
            _tmpTreatment = null
          } else {
            _tmpTreatment = _stmt.getText(_columnIndexOfTreatment)
          }
          val _tmpMedication: String?
          if (_stmt.isNull(_columnIndexOfMedication)) {
            _tmpMedication = null
          } else {
            _tmpMedication = _stmt.getText(_columnIndexOfMedication)
          }
          val _tmpDosage: String?
          if (_stmt.isNull(_columnIndexOfDosage)) {
            _tmpDosage = null
          } else {
            _tmpDosage = _stmt.getText(_columnIndexOfDosage)
          }
          val _tmpTreatmentDuration: String?
          if (_stmt.isNull(_columnIndexOfTreatmentDuration)) {
            _tmpTreatmentDuration = null
          } else {
            _tmpTreatmentDuration = _stmt.getText(_columnIndexOfTreatmentDuration)
          }
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpOutcome: String?
          if (_stmt.isNull(_columnIndexOfOutcome)) {
            _tmpOutcome = null
          } else {
            _tmpOutcome = _stmt.getText(_columnIndexOfOutcome)
          }
          val _tmpTreatedBy: String?
          if (_stmt.isNull(_columnIndexOfTreatedBy)) {
            _tmpTreatedBy = null
          } else {
            _tmpTreatedBy = _stmt.getText(_columnIndexOfTreatedBy)
          }
          val _tmpVetVisit: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfVetVisit).toInt()
          _tmpVetVisit = _tmp != 0
          val _tmpVetNotes: String?
          if (_stmt.isNull(_columnIndexOfVetNotes)) {
            _tmpVetNotes = null
          } else {
            _tmpVetNotes = _stmt.getText(_columnIndexOfVetNotes)
          }
          val _tmpCost: Double?
          if (_stmt.isNull(_columnIndexOfCost)) {
            _tmpCost = null
          } else {
            _tmpCost = _stmt.getDouble(_columnIndexOfCost)
          }
          val _tmpNotes: String?
          if (_stmt.isNull(_columnIndexOfNotes)) {
            _tmpNotes = null
          } else {
            _tmpNotes = _stmt.getText(_columnIndexOfNotes)
          }
          val _tmpMediaUrlsJson: String?
          if (_stmt.isNull(_columnIndexOfMediaUrlsJson)) {
            _tmpMediaUrlsJson = null
          } else {
            _tmpMediaUrlsJson = _stmt.getText(_columnIndexOfMediaUrlsJson)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpDirty: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp_1 != 0
          val _tmpSyncedAt: Long?
          if (_stmt.isNull(_columnIndexOfSyncedAt)) {
            _tmpSyncedAt = null
          } else {
            _tmpSyncedAt = _stmt.getLong(_columnIndexOfSyncedAt)
          }
          _item =
              MedicalEventEntity(_tmpEventId,_tmpBirdId,_tmpFarmerId,_tmpEventType,_tmpSeverity,_tmpEventDate,_tmpResolvedDate,_tmpDiagnosis,_tmpSymptoms,_tmpTreatment,_tmpMedication,_tmpDosage,_tmpTreatmentDuration,_tmpStatus,_tmpOutcome,_tmpTreatedBy,_tmpVetVisit,_tmpVetNotes,_tmpCost,_tmpNotes,_tmpMediaUrlsJson,_tmpCreatedAt,_tmpUpdatedAt,_tmpDirty,_tmpSyncedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeActiveIssues(farmerId: String): Flow<List<MedicalEventEntity>> {
    val _sql: String = """
        |
        |        SELECT * FROM medical_events 
        |        WHERE farmerId = ? 
        |        AND status = 'ACTIVE' 
        |        ORDER BY eventDate DESC
        |    
        """.trimMargin()
    return createFlow(__db, false, arrayOf("medical_events")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, farmerId)
        val _columnIndexOfEventId: Int = getColumnIndexOrThrow(_stmt, "eventId")
        val _columnIndexOfBirdId: Int = getColumnIndexOrThrow(_stmt, "birdId")
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfEventType: Int = getColumnIndexOrThrow(_stmt, "eventType")
        val _columnIndexOfSeverity: Int = getColumnIndexOrThrow(_stmt, "severity")
        val _columnIndexOfEventDate: Int = getColumnIndexOrThrow(_stmt, "eventDate")
        val _columnIndexOfResolvedDate: Int = getColumnIndexOrThrow(_stmt, "resolvedDate")
        val _columnIndexOfDiagnosis: Int = getColumnIndexOrThrow(_stmt, "diagnosis")
        val _columnIndexOfSymptoms: Int = getColumnIndexOrThrow(_stmt, "symptoms")
        val _columnIndexOfTreatment: Int = getColumnIndexOrThrow(_stmt, "treatment")
        val _columnIndexOfMedication: Int = getColumnIndexOrThrow(_stmt, "medication")
        val _columnIndexOfDosage: Int = getColumnIndexOrThrow(_stmt, "dosage")
        val _columnIndexOfTreatmentDuration: Int = getColumnIndexOrThrow(_stmt, "treatmentDuration")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfOutcome: Int = getColumnIndexOrThrow(_stmt, "outcome")
        val _columnIndexOfTreatedBy: Int = getColumnIndexOrThrow(_stmt, "treatedBy")
        val _columnIndexOfVetVisit: Int = getColumnIndexOrThrow(_stmt, "vetVisit")
        val _columnIndexOfVetNotes: Int = getColumnIndexOrThrow(_stmt, "vetNotes")
        val _columnIndexOfCost: Int = getColumnIndexOrThrow(_stmt, "cost")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfMediaUrlsJson: Int = getColumnIndexOrThrow(_stmt, "mediaUrlsJson")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _result: MutableList<MedicalEventEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: MedicalEventEntity
          val _tmpEventId: String
          _tmpEventId = _stmt.getText(_columnIndexOfEventId)
          val _tmpBirdId: String
          _tmpBirdId = _stmt.getText(_columnIndexOfBirdId)
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpEventType: String
          _tmpEventType = _stmt.getText(_columnIndexOfEventType)
          val _tmpSeverity: String
          _tmpSeverity = _stmt.getText(_columnIndexOfSeverity)
          val _tmpEventDate: Long
          _tmpEventDate = _stmt.getLong(_columnIndexOfEventDate)
          val _tmpResolvedDate: Long?
          if (_stmt.isNull(_columnIndexOfResolvedDate)) {
            _tmpResolvedDate = null
          } else {
            _tmpResolvedDate = _stmt.getLong(_columnIndexOfResolvedDate)
          }
          val _tmpDiagnosis: String?
          if (_stmt.isNull(_columnIndexOfDiagnosis)) {
            _tmpDiagnosis = null
          } else {
            _tmpDiagnosis = _stmt.getText(_columnIndexOfDiagnosis)
          }
          val _tmpSymptoms: String?
          if (_stmt.isNull(_columnIndexOfSymptoms)) {
            _tmpSymptoms = null
          } else {
            _tmpSymptoms = _stmt.getText(_columnIndexOfSymptoms)
          }
          val _tmpTreatment: String?
          if (_stmt.isNull(_columnIndexOfTreatment)) {
            _tmpTreatment = null
          } else {
            _tmpTreatment = _stmt.getText(_columnIndexOfTreatment)
          }
          val _tmpMedication: String?
          if (_stmt.isNull(_columnIndexOfMedication)) {
            _tmpMedication = null
          } else {
            _tmpMedication = _stmt.getText(_columnIndexOfMedication)
          }
          val _tmpDosage: String?
          if (_stmt.isNull(_columnIndexOfDosage)) {
            _tmpDosage = null
          } else {
            _tmpDosage = _stmt.getText(_columnIndexOfDosage)
          }
          val _tmpTreatmentDuration: String?
          if (_stmt.isNull(_columnIndexOfTreatmentDuration)) {
            _tmpTreatmentDuration = null
          } else {
            _tmpTreatmentDuration = _stmt.getText(_columnIndexOfTreatmentDuration)
          }
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpOutcome: String?
          if (_stmt.isNull(_columnIndexOfOutcome)) {
            _tmpOutcome = null
          } else {
            _tmpOutcome = _stmt.getText(_columnIndexOfOutcome)
          }
          val _tmpTreatedBy: String?
          if (_stmt.isNull(_columnIndexOfTreatedBy)) {
            _tmpTreatedBy = null
          } else {
            _tmpTreatedBy = _stmt.getText(_columnIndexOfTreatedBy)
          }
          val _tmpVetVisit: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfVetVisit).toInt()
          _tmpVetVisit = _tmp != 0
          val _tmpVetNotes: String?
          if (_stmt.isNull(_columnIndexOfVetNotes)) {
            _tmpVetNotes = null
          } else {
            _tmpVetNotes = _stmt.getText(_columnIndexOfVetNotes)
          }
          val _tmpCost: Double?
          if (_stmt.isNull(_columnIndexOfCost)) {
            _tmpCost = null
          } else {
            _tmpCost = _stmt.getDouble(_columnIndexOfCost)
          }
          val _tmpNotes: String?
          if (_stmt.isNull(_columnIndexOfNotes)) {
            _tmpNotes = null
          } else {
            _tmpNotes = _stmt.getText(_columnIndexOfNotes)
          }
          val _tmpMediaUrlsJson: String?
          if (_stmt.isNull(_columnIndexOfMediaUrlsJson)) {
            _tmpMediaUrlsJson = null
          } else {
            _tmpMediaUrlsJson = _stmt.getText(_columnIndexOfMediaUrlsJson)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpDirty: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp_1 != 0
          val _tmpSyncedAt: Long?
          if (_stmt.isNull(_columnIndexOfSyncedAt)) {
            _tmpSyncedAt = null
          } else {
            _tmpSyncedAt = _stmt.getLong(_columnIndexOfSyncedAt)
          }
          _item =
              MedicalEventEntity(_tmpEventId,_tmpBirdId,_tmpFarmerId,_tmpEventType,_tmpSeverity,_tmpEventDate,_tmpResolvedDate,_tmpDiagnosis,_tmpSymptoms,_tmpTreatment,_tmpMedication,_tmpDosage,_tmpTreatmentDuration,_tmpStatus,_tmpOutcome,_tmpTreatedBy,_tmpVetVisit,_tmpVetNotes,_tmpCost,_tmpNotes,_tmpMediaUrlsJson,_tmpCreatedAt,_tmpUpdatedAt,_tmpDirty,_tmpSyncedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeByType(farmerId: String, eventType: String):
      Flow<List<MedicalEventEntity>> {
    val _sql: String = """
        |
        |        SELECT * FROM medical_events 
        |        WHERE farmerId = ? 
        |        AND eventType = ? 
        |        ORDER BY eventDate DESC
        |    
        """.trimMargin()
    return createFlow(__db, false, arrayOf("medical_events")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, farmerId)
        _argIndex = 2
        _stmt.bindText(_argIndex, eventType)
        val _columnIndexOfEventId: Int = getColumnIndexOrThrow(_stmt, "eventId")
        val _columnIndexOfBirdId: Int = getColumnIndexOrThrow(_stmt, "birdId")
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfEventType: Int = getColumnIndexOrThrow(_stmt, "eventType")
        val _columnIndexOfSeverity: Int = getColumnIndexOrThrow(_stmt, "severity")
        val _columnIndexOfEventDate: Int = getColumnIndexOrThrow(_stmt, "eventDate")
        val _columnIndexOfResolvedDate: Int = getColumnIndexOrThrow(_stmt, "resolvedDate")
        val _columnIndexOfDiagnosis: Int = getColumnIndexOrThrow(_stmt, "diagnosis")
        val _columnIndexOfSymptoms: Int = getColumnIndexOrThrow(_stmt, "symptoms")
        val _columnIndexOfTreatment: Int = getColumnIndexOrThrow(_stmt, "treatment")
        val _columnIndexOfMedication: Int = getColumnIndexOrThrow(_stmt, "medication")
        val _columnIndexOfDosage: Int = getColumnIndexOrThrow(_stmt, "dosage")
        val _columnIndexOfTreatmentDuration: Int = getColumnIndexOrThrow(_stmt, "treatmentDuration")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfOutcome: Int = getColumnIndexOrThrow(_stmt, "outcome")
        val _columnIndexOfTreatedBy: Int = getColumnIndexOrThrow(_stmt, "treatedBy")
        val _columnIndexOfVetVisit: Int = getColumnIndexOrThrow(_stmt, "vetVisit")
        val _columnIndexOfVetNotes: Int = getColumnIndexOrThrow(_stmt, "vetNotes")
        val _columnIndexOfCost: Int = getColumnIndexOrThrow(_stmt, "cost")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfMediaUrlsJson: Int = getColumnIndexOrThrow(_stmt, "mediaUrlsJson")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _result: MutableList<MedicalEventEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: MedicalEventEntity
          val _tmpEventId: String
          _tmpEventId = _stmt.getText(_columnIndexOfEventId)
          val _tmpBirdId: String
          _tmpBirdId = _stmt.getText(_columnIndexOfBirdId)
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpEventType: String
          _tmpEventType = _stmt.getText(_columnIndexOfEventType)
          val _tmpSeverity: String
          _tmpSeverity = _stmt.getText(_columnIndexOfSeverity)
          val _tmpEventDate: Long
          _tmpEventDate = _stmt.getLong(_columnIndexOfEventDate)
          val _tmpResolvedDate: Long?
          if (_stmt.isNull(_columnIndexOfResolvedDate)) {
            _tmpResolvedDate = null
          } else {
            _tmpResolvedDate = _stmt.getLong(_columnIndexOfResolvedDate)
          }
          val _tmpDiagnosis: String?
          if (_stmt.isNull(_columnIndexOfDiagnosis)) {
            _tmpDiagnosis = null
          } else {
            _tmpDiagnosis = _stmt.getText(_columnIndexOfDiagnosis)
          }
          val _tmpSymptoms: String?
          if (_stmt.isNull(_columnIndexOfSymptoms)) {
            _tmpSymptoms = null
          } else {
            _tmpSymptoms = _stmt.getText(_columnIndexOfSymptoms)
          }
          val _tmpTreatment: String?
          if (_stmt.isNull(_columnIndexOfTreatment)) {
            _tmpTreatment = null
          } else {
            _tmpTreatment = _stmt.getText(_columnIndexOfTreatment)
          }
          val _tmpMedication: String?
          if (_stmt.isNull(_columnIndexOfMedication)) {
            _tmpMedication = null
          } else {
            _tmpMedication = _stmt.getText(_columnIndexOfMedication)
          }
          val _tmpDosage: String?
          if (_stmt.isNull(_columnIndexOfDosage)) {
            _tmpDosage = null
          } else {
            _tmpDosage = _stmt.getText(_columnIndexOfDosage)
          }
          val _tmpTreatmentDuration: String?
          if (_stmt.isNull(_columnIndexOfTreatmentDuration)) {
            _tmpTreatmentDuration = null
          } else {
            _tmpTreatmentDuration = _stmt.getText(_columnIndexOfTreatmentDuration)
          }
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpOutcome: String?
          if (_stmt.isNull(_columnIndexOfOutcome)) {
            _tmpOutcome = null
          } else {
            _tmpOutcome = _stmt.getText(_columnIndexOfOutcome)
          }
          val _tmpTreatedBy: String?
          if (_stmt.isNull(_columnIndexOfTreatedBy)) {
            _tmpTreatedBy = null
          } else {
            _tmpTreatedBy = _stmt.getText(_columnIndexOfTreatedBy)
          }
          val _tmpVetVisit: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfVetVisit).toInt()
          _tmpVetVisit = _tmp != 0
          val _tmpVetNotes: String?
          if (_stmt.isNull(_columnIndexOfVetNotes)) {
            _tmpVetNotes = null
          } else {
            _tmpVetNotes = _stmt.getText(_columnIndexOfVetNotes)
          }
          val _tmpCost: Double?
          if (_stmt.isNull(_columnIndexOfCost)) {
            _tmpCost = null
          } else {
            _tmpCost = _stmt.getDouble(_columnIndexOfCost)
          }
          val _tmpNotes: String?
          if (_stmt.isNull(_columnIndexOfNotes)) {
            _tmpNotes = null
          } else {
            _tmpNotes = _stmt.getText(_columnIndexOfNotes)
          }
          val _tmpMediaUrlsJson: String?
          if (_stmt.isNull(_columnIndexOfMediaUrlsJson)) {
            _tmpMediaUrlsJson = null
          } else {
            _tmpMediaUrlsJson = _stmt.getText(_columnIndexOfMediaUrlsJson)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpDirty: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp_1 != 0
          val _tmpSyncedAt: Long?
          if (_stmt.isNull(_columnIndexOfSyncedAt)) {
            _tmpSyncedAt = null
          } else {
            _tmpSyncedAt = _stmt.getLong(_columnIndexOfSyncedAt)
          }
          _item =
              MedicalEventEntity(_tmpEventId,_tmpBirdId,_tmpFarmerId,_tmpEventType,_tmpSeverity,_tmpEventDate,_tmpResolvedDate,_tmpDiagnosis,_tmpSymptoms,_tmpTreatment,_tmpMedication,_tmpDosage,_tmpTreatmentDuration,_tmpStatus,_tmpOutcome,_tmpTreatedBy,_tmpVetVisit,_tmpVetNotes,_tmpCost,_tmpNotes,_tmpMediaUrlsJson,_tmpCreatedAt,_tmpUpdatedAt,_tmpDirty,_tmpSyncedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun countActiveIssues(farmerId: String): Int {
    val _sql: String =
        "SELECT COUNT(*) FROM medical_events WHERE farmerId = ? AND status = 'ACTIVE'"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, farmerId)
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

  public override suspend fun countIllnessesSince(farmerId: String, since: Long): Int {
    val _sql: String =
        "SELECT COUNT(*) FROM medical_events WHERE farmerId = ? AND eventType = 'ILLNESS' AND eventDate >= ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, farmerId)
        _argIndex = 2
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

  public override suspend fun totalMedicalCostSince(farmerId: String, since: Long): Double? {
    val _sql: String = "SELECT SUM(cost) FROM medical_events WHERE farmerId = ? AND eventDate >= ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, farmerId)
        _argIndex = 2
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

  public override suspend fun getBirdsWithActiveIssues(farmerId: String): List<String> {
    val _sql: String =
        "SELECT DISTINCT birdId FROM medical_events WHERE farmerId = ? AND status = 'ACTIVE'"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, farmerId)
        val _result: MutableList<String> = mutableListOf()
        while (_stmt.step()) {
          val _item: String
          _item = _stmt.getText(0)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun delete(eventId: String) {
    val _sql: String = "DELETE FROM medical_events WHERE eventId = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, eventId)
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
