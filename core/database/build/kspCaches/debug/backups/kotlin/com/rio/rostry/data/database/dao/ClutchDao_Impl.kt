package com.rio.rostry.`data`.database.dao

import androidx.room.EntityDeleteOrUpdateAdapter
import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.rio.rostry.`data`.database.entity.ClutchEntity
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
public class ClutchDao_Impl(
  __db: RoomDatabase,
) : ClutchDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfClutchEntity: EntityInsertAdapter<ClutchEntity>

  private val __updateAdapterOfClutchEntity: EntityDeleteOrUpdateAdapter<ClutchEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfClutchEntity = object : EntityInsertAdapter<ClutchEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `clutches` (`clutchId`,`breedingPairId`,`farmerId`,`sireId`,`damId`,`clutchName`,`clutchNumber`,`eggsCollected`,`collectionStartDate`,`collectionEndDate`,`setDate`,`eggsSet`,`incubatorId`,`incubationNotes`,`firstCandleDate`,`firstCandleFertile`,`firstCandleClear`,`firstCandleEarlyDead`,`secondCandleDate`,`secondCandleAlive`,`secondCandleDead`,`expectedHatchDate`,`actualHatchStartDate`,`actualHatchEndDate`,`chicksHatched`,`chicksMale`,`chicksFemale`,`chicksUnsexed`,`deadInShell`,`pippedNotHatched`,`averageChickWeight`,`chickQualityScore`,`qualityNotes`,`fertilityRate`,`hatchabilityOfFertile`,`hatchabilityOfSet`,`status`,`offspringIdsJson`,`notes`,`mediaUrlsJson`,`createdAt`,`updatedAt`,`dirty`,`syncedAt`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: ClutchEntity) {
        statement.bindText(1, entity.clutchId)
        val _tmpBreedingPairId: String? = entity.breedingPairId
        if (_tmpBreedingPairId == null) {
          statement.bindNull(2)
        } else {
          statement.bindText(2, _tmpBreedingPairId)
        }
        statement.bindText(3, entity.farmerId)
        val _tmpSireId: String? = entity.sireId
        if (_tmpSireId == null) {
          statement.bindNull(4)
        } else {
          statement.bindText(4, _tmpSireId)
        }
        val _tmpDamId: String? = entity.damId
        if (_tmpDamId == null) {
          statement.bindNull(5)
        } else {
          statement.bindText(5, _tmpDamId)
        }
        val _tmpClutchName: String? = entity.clutchName
        if (_tmpClutchName == null) {
          statement.bindNull(6)
        } else {
          statement.bindText(6, _tmpClutchName)
        }
        val _tmpClutchNumber: Int? = entity.clutchNumber
        if (_tmpClutchNumber == null) {
          statement.bindNull(7)
        } else {
          statement.bindLong(7, _tmpClutchNumber.toLong())
        }
        statement.bindLong(8, entity.eggsCollected.toLong())
        statement.bindLong(9, entity.collectionStartDate)
        val _tmpCollectionEndDate: Long? = entity.collectionEndDate
        if (_tmpCollectionEndDate == null) {
          statement.bindNull(10)
        } else {
          statement.bindLong(10, _tmpCollectionEndDate)
        }
        val _tmpSetDate: Long? = entity.setDate
        if (_tmpSetDate == null) {
          statement.bindNull(11)
        } else {
          statement.bindLong(11, _tmpSetDate)
        }
        statement.bindLong(12, entity.eggsSet.toLong())
        val _tmpIncubatorId: String? = entity.incubatorId
        if (_tmpIncubatorId == null) {
          statement.bindNull(13)
        } else {
          statement.bindText(13, _tmpIncubatorId)
        }
        val _tmpIncubationNotes: String? = entity.incubationNotes
        if (_tmpIncubationNotes == null) {
          statement.bindNull(14)
        } else {
          statement.bindText(14, _tmpIncubationNotes)
        }
        val _tmpFirstCandleDate: Long? = entity.firstCandleDate
        if (_tmpFirstCandleDate == null) {
          statement.bindNull(15)
        } else {
          statement.bindLong(15, _tmpFirstCandleDate)
        }
        statement.bindLong(16, entity.firstCandleFertile.toLong())
        statement.bindLong(17, entity.firstCandleClear.toLong())
        statement.bindLong(18, entity.firstCandleEarlyDead.toLong())
        val _tmpSecondCandleDate: Long? = entity.secondCandleDate
        if (_tmpSecondCandleDate == null) {
          statement.bindNull(19)
        } else {
          statement.bindLong(19, _tmpSecondCandleDate)
        }
        statement.bindLong(20, entity.secondCandleAlive.toLong())
        statement.bindLong(21, entity.secondCandleDead.toLong())
        val _tmpExpectedHatchDate: Long? = entity.expectedHatchDate
        if (_tmpExpectedHatchDate == null) {
          statement.bindNull(22)
        } else {
          statement.bindLong(22, _tmpExpectedHatchDate)
        }
        val _tmpActualHatchStartDate: Long? = entity.actualHatchStartDate
        if (_tmpActualHatchStartDate == null) {
          statement.bindNull(23)
        } else {
          statement.bindLong(23, _tmpActualHatchStartDate)
        }
        val _tmpActualHatchEndDate: Long? = entity.actualHatchEndDate
        if (_tmpActualHatchEndDate == null) {
          statement.bindNull(24)
        } else {
          statement.bindLong(24, _tmpActualHatchEndDate)
        }
        statement.bindLong(25, entity.chicksHatched.toLong())
        statement.bindLong(26, entity.chicksMale.toLong())
        statement.bindLong(27, entity.chicksFemale.toLong())
        statement.bindLong(28, entity.chicksUnsexed.toLong())
        statement.bindLong(29, entity.deadInShell.toLong())
        statement.bindLong(30, entity.pippedNotHatched.toLong())
        val _tmpAverageChickWeight: Double? = entity.averageChickWeight
        if (_tmpAverageChickWeight == null) {
          statement.bindNull(31)
        } else {
          statement.bindDouble(31, _tmpAverageChickWeight)
        }
        val _tmpChickQualityScore: Int? = entity.chickQualityScore
        if (_tmpChickQualityScore == null) {
          statement.bindNull(32)
        } else {
          statement.bindLong(32, _tmpChickQualityScore.toLong())
        }
        val _tmpQualityNotes: String? = entity.qualityNotes
        if (_tmpQualityNotes == null) {
          statement.bindNull(33)
        } else {
          statement.bindText(33, _tmpQualityNotes)
        }
        val _tmpFertilityRate: Double? = entity.fertilityRate
        if (_tmpFertilityRate == null) {
          statement.bindNull(34)
        } else {
          statement.bindDouble(34, _tmpFertilityRate)
        }
        val _tmpHatchabilityOfFertile: Double? = entity.hatchabilityOfFertile
        if (_tmpHatchabilityOfFertile == null) {
          statement.bindNull(35)
        } else {
          statement.bindDouble(35, _tmpHatchabilityOfFertile)
        }
        val _tmpHatchabilityOfSet: Double? = entity.hatchabilityOfSet
        if (_tmpHatchabilityOfSet == null) {
          statement.bindNull(36)
        } else {
          statement.bindDouble(36, _tmpHatchabilityOfSet)
        }
        statement.bindText(37, entity.status)
        val _tmpOffspringIdsJson: String? = entity.offspringIdsJson
        if (_tmpOffspringIdsJson == null) {
          statement.bindNull(38)
        } else {
          statement.bindText(38, _tmpOffspringIdsJson)
        }
        val _tmpNotes: String? = entity.notes
        if (_tmpNotes == null) {
          statement.bindNull(39)
        } else {
          statement.bindText(39, _tmpNotes)
        }
        val _tmpMediaUrlsJson: String? = entity.mediaUrlsJson
        if (_tmpMediaUrlsJson == null) {
          statement.bindNull(40)
        } else {
          statement.bindText(40, _tmpMediaUrlsJson)
        }
        statement.bindLong(41, entity.createdAt)
        statement.bindLong(42, entity.updatedAt)
        val _tmp: Int = if (entity.dirty) 1 else 0
        statement.bindLong(43, _tmp.toLong())
        val _tmpSyncedAt: Long? = entity.syncedAt
        if (_tmpSyncedAt == null) {
          statement.bindNull(44)
        } else {
          statement.bindLong(44, _tmpSyncedAt)
        }
      }
    }
    this.__updateAdapterOfClutchEntity = object : EntityDeleteOrUpdateAdapter<ClutchEntity>() {
      protected override fun createQuery(): String =
          "UPDATE OR ABORT `clutches` SET `clutchId` = ?,`breedingPairId` = ?,`farmerId` = ?,`sireId` = ?,`damId` = ?,`clutchName` = ?,`clutchNumber` = ?,`eggsCollected` = ?,`collectionStartDate` = ?,`collectionEndDate` = ?,`setDate` = ?,`eggsSet` = ?,`incubatorId` = ?,`incubationNotes` = ?,`firstCandleDate` = ?,`firstCandleFertile` = ?,`firstCandleClear` = ?,`firstCandleEarlyDead` = ?,`secondCandleDate` = ?,`secondCandleAlive` = ?,`secondCandleDead` = ?,`expectedHatchDate` = ?,`actualHatchStartDate` = ?,`actualHatchEndDate` = ?,`chicksHatched` = ?,`chicksMale` = ?,`chicksFemale` = ?,`chicksUnsexed` = ?,`deadInShell` = ?,`pippedNotHatched` = ?,`averageChickWeight` = ?,`chickQualityScore` = ?,`qualityNotes` = ?,`fertilityRate` = ?,`hatchabilityOfFertile` = ?,`hatchabilityOfSet` = ?,`status` = ?,`offspringIdsJson` = ?,`notes` = ?,`mediaUrlsJson` = ?,`createdAt` = ?,`updatedAt` = ?,`dirty` = ?,`syncedAt` = ? WHERE `clutchId` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: ClutchEntity) {
        statement.bindText(1, entity.clutchId)
        val _tmpBreedingPairId: String? = entity.breedingPairId
        if (_tmpBreedingPairId == null) {
          statement.bindNull(2)
        } else {
          statement.bindText(2, _tmpBreedingPairId)
        }
        statement.bindText(3, entity.farmerId)
        val _tmpSireId: String? = entity.sireId
        if (_tmpSireId == null) {
          statement.bindNull(4)
        } else {
          statement.bindText(4, _tmpSireId)
        }
        val _tmpDamId: String? = entity.damId
        if (_tmpDamId == null) {
          statement.bindNull(5)
        } else {
          statement.bindText(5, _tmpDamId)
        }
        val _tmpClutchName: String? = entity.clutchName
        if (_tmpClutchName == null) {
          statement.bindNull(6)
        } else {
          statement.bindText(6, _tmpClutchName)
        }
        val _tmpClutchNumber: Int? = entity.clutchNumber
        if (_tmpClutchNumber == null) {
          statement.bindNull(7)
        } else {
          statement.bindLong(7, _tmpClutchNumber.toLong())
        }
        statement.bindLong(8, entity.eggsCollected.toLong())
        statement.bindLong(9, entity.collectionStartDate)
        val _tmpCollectionEndDate: Long? = entity.collectionEndDate
        if (_tmpCollectionEndDate == null) {
          statement.bindNull(10)
        } else {
          statement.bindLong(10, _tmpCollectionEndDate)
        }
        val _tmpSetDate: Long? = entity.setDate
        if (_tmpSetDate == null) {
          statement.bindNull(11)
        } else {
          statement.bindLong(11, _tmpSetDate)
        }
        statement.bindLong(12, entity.eggsSet.toLong())
        val _tmpIncubatorId: String? = entity.incubatorId
        if (_tmpIncubatorId == null) {
          statement.bindNull(13)
        } else {
          statement.bindText(13, _tmpIncubatorId)
        }
        val _tmpIncubationNotes: String? = entity.incubationNotes
        if (_tmpIncubationNotes == null) {
          statement.bindNull(14)
        } else {
          statement.bindText(14, _tmpIncubationNotes)
        }
        val _tmpFirstCandleDate: Long? = entity.firstCandleDate
        if (_tmpFirstCandleDate == null) {
          statement.bindNull(15)
        } else {
          statement.bindLong(15, _tmpFirstCandleDate)
        }
        statement.bindLong(16, entity.firstCandleFertile.toLong())
        statement.bindLong(17, entity.firstCandleClear.toLong())
        statement.bindLong(18, entity.firstCandleEarlyDead.toLong())
        val _tmpSecondCandleDate: Long? = entity.secondCandleDate
        if (_tmpSecondCandleDate == null) {
          statement.bindNull(19)
        } else {
          statement.bindLong(19, _tmpSecondCandleDate)
        }
        statement.bindLong(20, entity.secondCandleAlive.toLong())
        statement.bindLong(21, entity.secondCandleDead.toLong())
        val _tmpExpectedHatchDate: Long? = entity.expectedHatchDate
        if (_tmpExpectedHatchDate == null) {
          statement.bindNull(22)
        } else {
          statement.bindLong(22, _tmpExpectedHatchDate)
        }
        val _tmpActualHatchStartDate: Long? = entity.actualHatchStartDate
        if (_tmpActualHatchStartDate == null) {
          statement.bindNull(23)
        } else {
          statement.bindLong(23, _tmpActualHatchStartDate)
        }
        val _tmpActualHatchEndDate: Long? = entity.actualHatchEndDate
        if (_tmpActualHatchEndDate == null) {
          statement.bindNull(24)
        } else {
          statement.bindLong(24, _tmpActualHatchEndDate)
        }
        statement.bindLong(25, entity.chicksHatched.toLong())
        statement.bindLong(26, entity.chicksMale.toLong())
        statement.bindLong(27, entity.chicksFemale.toLong())
        statement.bindLong(28, entity.chicksUnsexed.toLong())
        statement.bindLong(29, entity.deadInShell.toLong())
        statement.bindLong(30, entity.pippedNotHatched.toLong())
        val _tmpAverageChickWeight: Double? = entity.averageChickWeight
        if (_tmpAverageChickWeight == null) {
          statement.bindNull(31)
        } else {
          statement.bindDouble(31, _tmpAverageChickWeight)
        }
        val _tmpChickQualityScore: Int? = entity.chickQualityScore
        if (_tmpChickQualityScore == null) {
          statement.bindNull(32)
        } else {
          statement.bindLong(32, _tmpChickQualityScore.toLong())
        }
        val _tmpQualityNotes: String? = entity.qualityNotes
        if (_tmpQualityNotes == null) {
          statement.bindNull(33)
        } else {
          statement.bindText(33, _tmpQualityNotes)
        }
        val _tmpFertilityRate: Double? = entity.fertilityRate
        if (_tmpFertilityRate == null) {
          statement.bindNull(34)
        } else {
          statement.bindDouble(34, _tmpFertilityRate)
        }
        val _tmpHatchabilityOfFertile: Double? = entity.hatchabilityOfFertile
        if (_tmpHatchabilityOfFertile == null) {
          statement.bindNull(35)
        } else {
          statement.bindDouble(35, _tmpHatchabilityOfFertile)
        }
        val _tmpHatchabilityOfSet: Double? = entity.hatchabilityOfSet
        if (_tmpHatchabilityOfSet == null) {
          statement.bindNull(36)
        } else {
          statement.bindDouble(36, _tmpHatchabilityOfSet)
        }
        statement.bindText(37, entity.status)
        val _tmpOffspringIdsJson: String? = entity.offspringIdsJson
        if (_tmpOffspringIdsJson == null) {
          statement.bindNull(38)
        } else {
          statement.bindText(38, _tmpOffspringIdsJson)
        }
        val _tmpNotes: String? = entity.notes
        if (_tmpNotes == null) {
          statement.bindNull(39)
        } else {
          statement.bindText(39, _tmpNotes)
        }
        val _tmpMediaUrlsJson: String? = entity.mediaUrlsJson
        if (_tmpMediaUrlsJson == null) {
          statement.bindNull(40)
        } else {
          statement.bindText(40, _tmpMediaUrlsJson)
        }
        statement.bindLong(41, entity.createdAt)
        statement.bindLong(42, entity.updatedAt)
        val _tmp: Int = if (entity.dirty) 1 else 0
        statement.bindLong(43, _tmp.toLong())
        val _tmpSyncedAt: Long? = entity.syncedAt
        if (_tmpSyncedAt == null) {
          statement.bindNull(44)
        } else {
          statement.bindLong(44, _tmpSyncedAt)
        }
        statement.bindText(45, entity.clutchId)
      }
    }
  }

  public override suspend fun insert(clutch: ClutchEntity): Unit = performSuspending(__db, false,
      true) { _connection ->
    __insertAdapterOfClutchEntity.insert(_connection, clutch)
  }

  public override suspend fun insertAll(clutches: List<ClutchEntity>): Unit =
      performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfClutchEntity.insert(_connection, clutches)
  }

  public override suspend fun update(clutch: ClutchEntity): Unit = performSuspending(__db, false,
      true) { _connection ->
    __updateAdapterOfClutchEntity.handle(_connection, clutch)
  }

  public override suspend fun findById(clutchId: String): ClutchEntity? {
    val _sql: String = "SELECT * FROM clutches WHERE clutchId = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, clutchId)
        val _columnIndexOfClutchId: Int = getColumnIndexOrThrow(_stmt, "clutchId")
        val _columnIndexOfBreedingPairId: Int = getColumnIndexOrThrow(_stmt, "breedingPairId")
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfSireId: Int = getColumnIndexOrThrow(_stmt, "sireId")
        val _columnIndexOfDamId: Int = getColumnIndexOrThrow(_stmt, "damId")
        val _columnIndexOfClutchName: Int = getColumnIndexOrThrow(_stmt, "clutchName")
        val _columnIndexOfClutchNumber: Int = getColumnIndexOrThrow(_stmt, "clutchNumber")
        val _columnIndexOfEggsCollected: Int = getColumnIndexOrThrow(_stmt, "eggsCollected")
        val _columnIndexOfCollectionStartDate: Int = getColumnIndexOrThrow(_stmt,
            "collectionStartDate")
        val _columnIndexOfCollectionEndDate: Int = getColumnIndexOrThrow(_stmt, "collectionEndDate")
        val _columnIndexOfSetDate: Int = getColumnIndexOrThrow(_stmt, "setDate")
        val _columnIndexOfEggsSet: Int = getColumnIndexOrThrow(_stmt, "eggsSet")
        val _columnIndexOfIncubatorId: Int = getColumnIndexOrThrow(_stmt, "incubatorId")
        val _columnIndexOfIncubationNotes: Int = getColumnIndexOrThrow(_stmt, "incubationNotes")
        val _columnIndexOfFirstCandleDate: Int = getColumnIndexOrThrow(_stmt, "firstCandleDate")
        val _columnIndexOfFirstCandleFertile: Int = getColumnIndexOrThrow(_stmt,
            "firstCandleFertile")
        val _columnIndexOfFirstCandleClear: Int = getColumnIndexOrThrow(_stmt, "firstCandleClear")
        val _columnIndexOfFirstCandleEarlyDead: Int = getColumnIndexOrThrow(_stmt,
            "firstCandleEarlyDead")
        val _columnIndexOfSecondCandleDate: Int = getColumnIndexOrThrow(_stmt, "secondCandleDate")
        val _columnIndexOfSecondCandleAlive: Int = getColumnIndexOrThrow(_stmt, "secondCandleAlive")
        val _columnIndexOfSecondCandleDead: Int = getColumnIndexOrThrow(_stmt, "secondCandleDead")
        val _columnIndexOfExpectedHatchDate: Int = getColumnIndexOrThrow(_stmt, "expectedHatchDate")
        val _columnIndexOfActualHatchStartDate: Int = getColumnIndexOrThrow(_stmt,
            "actualHatchStartDate")
        val _columnIndexOfActualHatchEndDate: Int = getColumnIndexOrThrow(_stmt,
            "actualHatchEndDate")
        val _columnIndexOfChicksHatched: Int = getColumnIndexOrThrow(_stmt, "chicksHatched")
        val _columnIndexOfChicksMale: Int = getColumnIndexOrThrow(_stmt, "chicksMale")
        val _columnIndexOfChicksFemale: Int = getColumnIndexOrThrow(_stmt, "chicksFemale")
        val _columnIndexOfChicksUnsexed: Int = getColumnIndexOrThrow(_stmt, "chicksUnsexed")
        val _columnIndexOfDeadInShell: Int = getColumnIndexOrThrow(_stmt, "deadInShell")
        val _columnIndexOfPippedNotHatched: Int = getColumnIndexOrThrow(_stmt, "pippedNotHatched")
        val _columnIndexOfAverageChickWeight: Int = getColumnIndexOrThrow(_stmt,
            "averageChickWeight")
        val _columnIndexOfChickQualityScore: Int = getColumnIndexOrThrow(_stmt, "chickQualityScore")
        val _columnIndexOfQualityNotes: Int = getColumnIndexOrThrow(_stmt, "qualityNotes")
        val _columnIndexOfFertilityRate: Int = getColumnIndexOrThrow(_stmt, "fertilityRate")
        val _columnIndexOfHatchabilityOfFertile: Int = getColumnIndexOrThrow(_stmt,
            "hatchabilityOfFertile")
        val _columnIndexOfHatchabilityOfSet: Int = getColumnIndexOrThrow(_stmt, "hatchabilityOfSet")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfOffspringIdsJson: Int = getColumnIndexOrThrow(_stmt, "offspringIdsJson")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfMediaUrlsJson: Int = getColumnIndexOrThrow(_stmt, "mediaUrlsJson")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _result: ClutchEntity?
        if (_stmt.step()) {
          val _tmpClutchId: String
          _tmpClutchId = _stmt.getText(_columnIndexOfClutchId)
          val _tmpBreedingPairId: String?
          if (_stmt.isNull(_columnIndexOfBreedingPairId)) {
            _tmpBreedingPairId = null
          } else {
            _tmpBreedingPairId = _stmt.getText(_columnIndexOfBreedingPairId)
          }
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpSireId: String?
          if (_stmt.isNull(_columnIndexOfSireId)) {
            _tmpSireId = null
          } else {
            _tmpSireId = _stmt.getText(_columnIndexOfSireId)
          }
          val _tmpDamId: String?
          if (_stmt.isNull(_columnIndexOfDamId)) {
            _tmpDamId = null
          } else {
            _tmpDamId = _stmt.getText(_columnIndexOfDamId)
          }
          val _tmpClutchName: String?
          if (_stmt.isNull(_columnIndexOfClutchName)) {
            _tmpClutchName = null
          } else {
            _tmpClutchName = _stmt.getText(_columnIndexOfClutchName)
          }
          val _tmpClutchNumber: Int?
          if (_stmt.isNull(_columnIndexOfClutchNumber)) {
            _tmpClutchNumber = null
          } else {
            _tmpClutchNumber = _stmt.getLong(_columnIndexOfClutchNumber).toInt()
          }
          val _tmpEggsCollected: Int
          _tmpEggsCollected = _stmt.getLong(_columnIndexOfEggsCollected).toInt()
          val _tmpCollectionStartDate: Long
          _tmpCollectionStartDate = _stmt.getLong(_columnIndexOfCollectionStartDate)
          val _tmpCollectionEndDate: Long?
          if (_stmt.isNull(_columnIndexOfCollectionEndDate)) {
            _tmpCollectionEndDate = null
          } else {
            _tmpCollectionEndDate = _stmt.getLong(_columnIndexOfCollectionEndDate)
          }
          val _tmpSetDate: Long?
          if (_stmt.isNull(_columnIndexOfSetDate)) {
            _tmpSetDate = null
          } else {
            _tmpSetDate = _stmt.getLong(_columnIndexOfSetDate)
          }
          val _tmpEggsSet: Int
          _tmpEggsSet = _stmt.getLong(_columnIndexOfEggsSet).toInt()
          val _tmpIncubatorId: String?
          if (_stmt.isNull(_columnIndexOfIncubatorId)) {
            _tmpIncubatorId = null
          } else {
            _tmpIncubatorId = _stmt.getText(_columnIndexOfIncubatorId)
          }
          val _tmpIncubationNotes: String?
          if (_stmt.isNull(_columnIndexOfIncubationNotes)) {
            _tmpIncubationNotes = null
          } else {
            _tmpIncubationNotes = _stmt.getText(_columnIndexOfIncubationNotes)
          }
          val _tmpFirstCandleDate: Long?
          if (_stmt.isNull(_columnIndexOfFirstCandleDate)) {
            _tmpFirstCandleDate = null
          } else {
            _tmpFirstCandleDate = _stmt.getLong(_columnIndexOfFirstCandleDate)
          }
          val _tmpFirstCandleFertile: Int
          _tmpFirstCandleFertile = _stmt.getLong(_columnIndexOfFirstCandleFertile).toInt()
          val _tmpFirstCandleClear: Int
          _tmpFirstCandleClear = _stmt.getLong(_columnIndexOfFirstCandleClear).toInt()
          val _tmpFirstCandleEarlyDead: Int
          _tmpFirstCandleEarlyDead = _stmt.getLong(_columnIndexOfFirstCandleEarlyDead).toInt()
          val _tmpSecondCandleDate: Long?
          if (_stmt.isNull(_columnIndexOfSecondCandleDate)) {
            _tmpSecondCandleDate = null
          } else {
            _tmpSecondCandleDate = _stmt.getLong(_columnIndexOfSecondCandleDate)
          }
          val _tmpSecondCandleAlive: Int
          _tmpSecondCandleAlive = _stmt.getLong(_columnIndexOfSecondCandleAlive).toInt()
          val _tmpSecondCandleDead: Int
          _tmpSecondCandleDead = _stmt.getLong(_columnIndexOfSecondCandleDead).toInt()
          val _tmpExpectedHatchDate: Long?
          if (_stmt.isNull(_columnIndexOfExpectedHatchDate)) {
            _tmpExpectedHatchDate = null
          } else {
            _tmpExpectedHatchDate = _stmt.getLong(_columnIndexOfExpectedHatchDate)
          }
          val _tmpActualHatchStartDate: Long?
          if (_stmt.isNull(_columnIndexOfActualHatchStartDate)) {
            _tmpActualHatchStartDate = null
          } else {
            _tmpActualHatchStartDate = _stmt.getLong(_columnIndexOfActualHatchStartDate)
          }
          val _tmpActualHatchEndDate: Long?
          if (_stmt.isNull(_columnIndexOfActualHatchEndDate)) {
            _tmpActualHatchEndDate = null
          } else {
            _tmpActualHatchEndDate = _stmt.getLong(_columnIndexOfActualHatchEndDate)
          }
          val _tmpChicksHatched: Int
          _tmpChicksHatched = _stmt.getLong(_columnIndexOfChicksHatched).toInt()
          val _tmpChicksMale: Int
          _tmpChicksMale = _stmt.getLong(_columnIndexOfChicksMale).toInt()
          val _tmpChicksFemale: Int
          _tmpChicksFemale = _stmt.getLong(_columnIndexOfChicksFemale).toInt()
          val _tmpChicksUnsexed: Int
          _tmpChicksUnsexed = _stmt.getLong(_columnIndexOfChicksUnsexed).toInt()
          val _tmpDeadInShell: Int
          _tmpDeadInShell = _stmt.getLong(_columnIndexOfDeadInShell).toInt()
          val _tmpPippedNotHatched: Int
          _tmpPippedNotHatched = _stmt.getLong(_columnIndexOfPippedNotHatched).toInt()
          val _tmpAverageChickWeight: Double?
          if (_stmt.isNull(_columnIndexOfAverageChickWeight)) {
            _tmpAverageChickWeight = null
          } else {
            _tmpAverageChickWeight = _stmt.getDouble(_columnIndexOfAverageChickWeight)
          }
          val _tmpChickQualityScore: Int?
          if (_stmt.isNull(_columnIndexOfChickQualityScore)) {
            _tmpChickQualityScore = null
          } else {
            _tmpChickQualityScore = _stmt.getLong(_columnIndexOfChickQualityScore).toInt()
          }
          val _tmpQualityNotes: String?
          if (_stmt.isNull(_columnIndexOfQualityNotes)) {
            _tmpQualityNotes = null
          } else {
            _tmpQualityNotes = _stmt.getText(_columnIndexOfQualityNotes)
          }
          val _tmpFertilityRate: Double?
          if (_stmt.isNull(_columnIndexOfFertilityRate)) {
            _tmpFertilityRate = null
          } else {
            _tmpFertilityRate = _stmt.getDouble(_columnIndexOfFertilityRate)
          }
          val _tmpHatchabilityOfFertile: Double?
          if (_stmt.isNull(_columnIndexOfHatchabilityOfFertile)) {
            _tmpHatchabilityOfFertile = null
          } else {
            _tmpHatchabilityOfFertile = _stmt.getDouble(_columnIndexOfHatchabilityOfFertile)
          }
          val _tmpHatchabilityOfSet: Double?
          if (_stmt.isNull(_columnIndexOfHatchabilityOfSet)) {
            _tmpHatchabilityOfSet = null
          } else {
            _tmpHatchabilityOfSet = _stmt.getDouble(_columnIndexOfHatchabilityOfSet)
          }
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpOffspringIdsJson: String?
          if (_stmt.isNull(_columnIndexOfOffspringIdsJson)) {
            _tmpOffspringIdsJson = null
          } else {
            _tmpOffspringIdsJson = _stmt.getText(_columnIndexOfOffspringIdsJson)
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
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp != 0
          val _tmpSyncedAt: Long?
          if (_stmt.isNull(_columnIndexOfSyncedAt)) {
            _tmpSyncedAt = null
          } else {
            _tmpSyncedAt = _stmt.getLong(_columnIndexOfSyncedAt)
          }
          _result =
              ClutchEntity(_tmpClutchId,_tmpBreedingPairId,_tmpFarmerId,_tmpSireId,_tmpDamId,_tmpClutchName,_tmpClutchNumber,_tmpEggsCollected,_tmpCollectionStartDate,_tmpCollectionEndDate,_tmpSetDate,_tmpEggsSet,_tmpIncubatorId,_tmpIncubationNotes,_tmpFirstCandleDate,_tmpFirstCandleFertile,_tmpFirstCandleClear,_tmpFirstCandleEarlyDead,_tmpSecondCandleDate,_tmpSecondCandleAlive,_tmpSecondCandleDead,_tmpExpectedHatchDate,_tmpActualHatchStartDate,_tmpActualHatchEndDate,_tmpChicksHatched,_tmpChicksMale,_tmpChicksFemale,_tmpChicksUnsexed,_tmpDeadInShell,_tmpPippedNotHatched,_tmpAverageChickWeight,_tmpChickQualityScore,_tmpQualityNotes,_tmpFertilityRate,_tmpHatchabilityOfFertile,_tmpHatchabilityOfSet,_tmpStatus,_tmpOffspringIdsJson,_tmpNotes,_tmpMediaUrlsJson,_tmpCreatedAt,_tmpUpdatedAt,_tmpDirty,_tmpSyncedAt)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeById(clutchId: String): Flow<ClutchEntity?> {
    val _sql: String = "SELECT * FROM clutches WHERE clutchId = ?"
    return createFlow(__db, false, arrayOf("clutches")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, clutchId)
        val _columnIndexOfClutchId: Int = getColumnIndexOrThrow(_stmt, "clutchId")
        val _columnIndexOfBreedingPairId: Int = getColumnIndexOrThrow(_stmt, "breedingPairId")
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfSireId: Int = getColumnIndexOrThrow(_stmt, "sireId")
        val _columnIndexOfDamId: Int = getColumnIndexOrThrow(_stmt, "damId")
        val _columnIndexOfClutchName: Int = getColumnIndexOrThrow(_stmt, "clutchName")
        val _columnIndexOfClutchNumber: Int = getColumnIndexOrThrow(_stmt, "clutchNumber")
        val _columnIndexOfEggsCollected: Int = getColumnIndexOrThrow(_stmt, "eggsCollected")
        val _columnIndexOfCollectionStartDate: Int = getColumnIndexOrThrow(_stmt,
            "collectionStartDate")
        val _columnIndexOfCollectionEndDate: Int = getColumnIndexOrThrow(_stmt, "collectionEndDate")
        val _columnIndexOfSetDate: Int = getColumnIndexOrThrow(_stmt, "setDate")
        val _columnIndexOfEggsSet: Int = getColumnIndexOrThrow(_stmt, "eggsSet")
        val _columnIndexOfIncubatorId: Int = getColumnIndexOrThrow(_stmt, "incubatorId")
        val _columnIndexOfIncubationNotes: Int = getColumnIndexOrThrow(_stmt, "incubationNotes")
        val _columnIndexOfFirstCandleDate: Int = getColumnIndexOrThrow(_stmt, "firstCandleDate")
        val _columnIndexOfFirstCandleFertile: Int = getColumnIndexOrThrow(_stmt,
            "firstCandleFertile")
        val _columnIndexOfFirstCandleClear: Int = getColumnIndexOrThrow(_stmt, "firstCandleClear")
        val _columnIndexOfFirstCandleEarlyDead: Int = getColumnIndexOrThrow(_stmt,
            "firstCandleEarlyDead")
        val _columnIndexOfSecondCandleDate: Int = getColumnIndexOrThrow(_stmt, "secondCandleDate")
        val _columnIndexOfSecondCandleAlive: Int = getColumnIndexOrThrow(_stmt, "secondCandleAlive")
        val _columnIndexOfSecondCandleDead: Int = getColumnIndexOrThrow(_stmt, "secondCandleDead")
        val _columnIndexOfExpectedHatchDate: Int = getColumnIndexOrThrow(_stmt, "expectedHatchDate")
        val _columnIndexOfActualHatchStartDate: Int = getColumnIndexOrThrow(_stmt,
            "actualHatchStartDate")
        val _columnIndexOfActualHatchEndDate: Int = getColumnIndexOrThrow(_stmt,
            "actualHatchEndDate")
        val _columnIndexOfChicksHatched: Int = getColumnIndexOrThrow(_stmt, "chicksHatched")
        val _columnIndexOfChicksMale: Int = getColumnIndexOrThrow(_stmt, "chicksMale")
        val _columnIndexOfChicksFemale: Int = getColumnIndexOrThrow(_stmt, "chicksFemale")
        val _columnIndexOfChicksUnsexed: Int = getColumnIndexOrThrow(_stmt, "chicksUnsexed")
        val _columnIndexOfDeadInShell: Int = getColumnIndexOrThrow(_stmt, "deadInShell")
        val _columnIndexOfPippedNotHatched: Int = getColumnIndexOrThrow(_stmt, "pippedNotHatched")
        val _columnIndexOfAverageChickWeight: Int = getColumnIndexOrThrow(_stmt,
            "averageChickWeight")
        val _columnIndexOfChickQualityScore: Int = getColumnIndexOrThrow(_stmt, "chickQualityScore")
        val _columnIndexOfQualityNotes: Int = getColumnIndexOrThrow(_stmt, "qualityNotes")
        val _columnIndexOfFertilityRate: Int = getColumnIndexOrThrow(_stmt, "fertilityRate")
        val _columnIndexOfHatchabilityOfFertile: Int = getColumnIndexOrThrow(_stmt,
            "hatchabilityOfFertile")
        val _columnIndexOfHatchabilityOfSet: Int = getColumnIndexOrThrow(_stmt, "hatchabilityOfSet")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfOffspringIdsJson: Int = getColumnIndexOrThrow(_stmt, "offspringIdsJson")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfMediaUrlsJson: Int = getColumnIndexOrThrow(_stmt, "mediaUrlsJson")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _result: ClutchEntity?
        if (_stmt.step()) {
          val _tmpClutchId: String
          _tmpClutchId = _stmt.getText(_columnIndexOfClutchId)
          val _tmpBreedingPairId: String?
          if (_stmt.isNull(_columnIndexOfBreedingPairId)) {
            _tmpBreedingPairId = null
          } else {
            _tmpBreedingPairId = _stmt.getText(_columnIndexOfBreedingPairId)
          }
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpSireId: String?
          if (_stmt.isNull(_columnIndexOfSireId)) {
            _tmpSireId = null
          } else {
            _tmpSireId = _stmt.getText(_columnIndexOfSireId)
          }
          val _tmpDamId: String?
          if (_stmt.isNull(_columnIndexOfDamId)) {
            _tmpDamId = null
          } else {
            _tmpDamId = _stmt.getText(_columnIndexOfDamId)
          }
          val _tmpClutchName: String?
          if (_stmt.isNull(_columnIndexOfClutchName)) {
            _tmpClutchName = null
          } else {
            _tmpClutchName = _stmt.getText(_columnIndexOfClutchName)
          }
          val _tmpClutchNumber: Int?
          if (_stmt.isNull(_columnIndexOfClutchNumber)) {
            _tmpClutchNumber = null
          } else {
            _tmpClutchNumber = _stmt.getLong(_columnIndexOfClutchNumber).toInt()
          }
          val _tmpEggsCollected: Int
          _tmpEggsCollected = _stmt.getLong(_columnIndexOfEggsCollected).toInt()
          val _tmpCollectionStartDate: Long
          _tmpCollectionStartDate = _stmt.getLong(_columnIndexOfCollectionStartDate)
          val _tmpCollectionEndDate: Long?
          if (_stmt.isNull(_columnIndexOfCollectionEndDate)) {
            _tmpCollectionEndDate = null
          } else {
            _tmpCollectionEndDate = _stmt.getLong(_columnIndexOfCollectionEndDate)
          }
          val _tmpSetDate: Long?
          if (_stmt.isNull(_columnIndexOfSetDate)) {
            _tmpSetDate = null
          } else {
            _tmpSetDate = _stmt.getLong(_columnIndexOfSetDate)
          }
          val _tmpEggsSet: Int
          _tmpEggsSet = _stmt.getLong(_columnIndexOfEggsSet).toInt()
          val _tmpIncubatorId: String?
          if (_stmt.isNull(_columnIndexOfIncubatorId)) {
            _tmpIncubatorId = null
          } else {
            _tmpIncubatorId = _stmt.getText(_columnIndexOfIncubatorId)
          }
          val _tmpIncubationNotes: String?
          if (_stmt.isNull(_columnIndexOfIncubationNotes)) {
            _tmpIncubationNotes = null
          } else {
            _tmpIncubationNotes = _stmt.getText(_columnIndexOfIncubationNotes)
          }
          val _tmpFirstCandleDate: Long?
          if (_stmt.isNull(_columnIndexOfFirstCandleDate)) {
            _tmpFirstCandleDate = null
          } else {
            _tmpFirstCandleDate = _stmt.getLong(_columnIndexOfFirstCandleDate)
          }
          val _tmpFirstCandleFertile: Int
          _tmpFirstCandleFertile = _stmt.getLong(_columnIndexOfFirstCandleFertile).toInt()
          val _tmpFirstCandleClear: Int
          _tmpFirstCandleClear = _stmt.getLong(_columnIndexOfFirstCandleClear).toInt()
          val _tmpFirstCandleEarlyDead: Int
          _tmpFirstCandleEarlyDead = _stmt.getLong(_columnIndexOfFirstCandleEarlyDead).toInt()
          val _tmpSecondCandleDate: Long?
          if (_stmt.isNull(_columnIndexOfSecondCandleDate)) {
            _tmpSecondCandleDate = null
          } else {
            _tmpSecondCandleDate = _stmt.getLong(_columnIndexOfSecondCandleDate)
          }
          val _tmpSecondCandleAlive: Int
          _tmpSecondCandleAlive = _stmt.getLong(_columnIndexOfSecondCandleAlive).toInt()
          val _tmpSecondCandleDead: Int
          _tmpSecondCandleDead = _stmt.getLong(_columnIndexOfSecondCandleDead).toInt()
          val _tmpExpectedHatchDate: Long?
          if (_stmt.isNull(_columnIndexOfExpectedHatchDate)) {
            _tmpExpectedHatchDate = null
          } else {
            _tmpExpectedHatchDate = _stmt.getLong(_columnIndexOfExpectedHatchDate)
          }
          val _tmpActualHatchStartDate: Long?
          if (_stmt.isNull(_columnIndexOfActualHatchStartDate)) {
            _tmpActualHatchStartDate = null
          } else {
            _tmpActualHatchStartDate = _stmt.getLong(_columnIndexOfActualHatchStartDate)
          }
          val _tmpActualHatchEndDate: Long?
          if (_stmt.isNull(_columnIndexOfActualHatchEndDate)) {
            _tmpActualHatchEndDate = null
          } else {
            _tmpActualHatchEndDate = _stmt.getLong(_columnIndexOfActualHatchEndDate)
          }
          val _tmpChicksHatched: Int
          _tmpChicksHatched = _stmt.getLong(_columnIndexOfChicksHatched).toInt()
          val _tmpChicksMale: Int
          _tmpChicksMale = _stmt.getLong(_columnIndexOfChicksMale).toInt()
          val _tmpChicksFemale: Int
          _tmpChicksFemale = _stmt.getLong(_columnIndexOfChicksFemale).toInt()
          val _tmpChicksUnsexed: Int
          _tmpChicksUnsexed = _stmt.getLong(_columnIndexOfChicksUnsexed).toInt()
          val _tmpDeadInShell: Int
          _tmpDeadInShell = _stmt.getLong(_columnIndexOfDeadInShell).toInt()
          val _tmpPippedNotHatched: Int
          _tmpPippedNotHatched = _stmt.getLong(_columnIndexOfPippedNotHatched).toInt()
          val _tmpAverageChickWeight: Double?
          if (_stmt.isNull(_columnIndexOfAverageChickWeight)) {
            _tmpAverageChickWeight = null
          } else {
            _tmpAverageChickWeight = _stmt.getDouble(_columnIndexOfAverageChickWeight)
          }
          val _tmpChickQualityScore: Int?
          if (_stmt.isNull(_columnIndexOfChickQualityScore)) {
            _tmpChickQualityScore = null
          } else {
            _tmpChickQualityScore = _stmt.getLong(_columnIndexOfChickQualityScore).toInt()
          }
          val _tmpQualityNotes: String?
          if (_stmt.isNull(_columnIndexOfQualityNotes)) {
            _tmpQualityNotes = null
          } else {
            _tmpQualityNotes = _stmt.getText(_columnIndexOfQualityNotes)
          }
          val _tmpFertilityRate: Double?
          if (_stmt.isNull(_columnIndexOfFertilityRate)) {
            _tmpFertilityRate = null
          } else {
            _tmpFertilityRate = _stmt.getDouble(_columnIndexOfFertilityRate)
          }
          val _tmpHatchabilityOfFertile: Double?
          if (_stmt.isNull(_columnIndexOfHatchabilityOfFertile)) {
            _tmpHatchabilityOfFertile = null
          } else {
            _tmpHatchabilityOfFertile = _stmt.getDouble(_columnIndexOfHatchabilityOfFertile)
          }
          val _tmpHatchabilityOfSet: Double?
          if (_stmt.isNull(_columnIndexOfHatchabilityOfSet)) {
            _tmpHatchabilityOfSet = null
          } else {
            _tmpHatchabilityOfSet = _stmt.getDouble(_columnIndexOfHatchabilityOfSet)
          }
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpOffspringIdsJson: String?
          if (_stmt.isNull(_columnIndexOfOffspringIdsJson)) {
            _tmpOffspringIdsJson = null
          } else {
            _tmpOffspringIdsJson = _stmt.getText(_columnIndexOfOffspringIdsJson)
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
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp != 0
          val _tmpSyncedAt: Long?
          if (_stmt.isNull(_columnIndexOfSyncedAt)) {
            _tmpSyncedAt = null
          } else {
            _tmpSyncedAt = _stmt.getLong(_columnIndexOfSyncedAt)
          }
          _result =
              ClutchEntity(_tmpClutchId,_tmpBreedingPairId,_tmpFarmerId,_tmpSireId,_tmpDamId,_tmpClutchName,_tmpClutchNumber,_tmpEggsCollected,_tmpCollectionStartDate,_tmpCollectionEndDate,_tmpSetDate,_tmpEggsSet,_tmpIncubatorId,_tmpIncubationNotes,_tmpFirstCandleDate,_tmpFirstCandleFertile,_tmpFirstCandleClear,_tmpFirstCandleEarlyDead,_tmpSecondCandleDate,_tmpSecondCandleAlive,_tmpSecondCandleDead,_tmpExpectedHatchDate,_tmpActualHatchStartDate,_tmpActualHatchEndDate,_tmpChicksHatched,_tmpChicksMale,_tmpChicksFemale,_tmpChicksUnsexed,_tmpDeadInShell,_tmpPippedNotHatched,_tmpAverageChickWeight,_tmpChickQualityScore,_tmpQualityNotes,_tmpFertilityRate,_tmpHatchabilityOfFertile,_tmpHatchabilityOfSet,_tmpStatus,_tmpOffspringIdsJson,_tmpNotes,_tmpMediaUrlsJson,_tmpCreatedAt,_tmpUpdatedAt,_tmpDirty,_tmpSyncedAt)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeByFarmer(farmerId: String): Flow<List<ClutchEntity>> {
    val _sql: String = "SELECT * FROM clutches WHERE farmerId = ? ORDER BY createdAt DESC"
    return createFlow(__db, false, arrayOf("clutches")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, farmerId)
        val _columnIndexOfClutchId: Int = getColumnIndexOrThrow(_stmt, "clutchId")
        val _columnIndexOfBreedingPairId: Int = getColumnIndexOrThrow(_stmt, "breedingPairId")
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfSireId: Int = getColumnIndexOrThrow(_stmt, "sireId")
        val _columnIndexOfDamId: Int = getColumnIndexOrThrow(_stmt, "damId")
        val _columnIndexOfClutchName: Int = getColumnIndexOrThrow(_stmt, "clutchName")
        val _columnIndexOfClutchNumber: Int = getColumnIndexOrThrow(_stmt, "clutchNumber")
        val _columnIndexOfEggsCollected: Int = getColumnIndexOrThrow(_stmt, "eggsCollected")
        val _columnIndexOfCollectionStartDate: Int = getColumnIndexOrThrow(_stmt,
            "collectionStartDate")
        val _columnIndexOfCollectionEndDate: Int = getColumnIndexOrThrow(_stmt, "collectionEndDate")
        val _columnIndexOfSetDate: Int = getColumnIndexOrThrow(_stmt, "setDate")
        val _columnIndexOfEggsSet: Int = getColumnIndexOrThrow(_stmt, "eggsSet")
        val _columnIndexOfIncubatorId: Int = getColumnIndexOrThrow(_stmt, "incubatorId")
        val _columnIndexOfIncubationNotes: Int = getColumnIndexOrThrow(_stmt, "incubationNotes")
        val _columnIndexOfFirstCandleDate: Int = getColumnIndexOrThrow(_stmt, "firstCandleDate")
        val _columnIndexOfFirstCandleFertile: Int = getColumnIndexOrThrow(_stmt,
            "firstCandleFertile")
        val _columnIndexOfFirstCandleClear: Int = getColumnIndexOrThrow(_stmt, "firstCandleClear")
        val _columnIndexOfFirstCandleEarlyDead: Int = getColumnIndexOrThrow(_stmt,
            "firstCandleEarlyDead")
        val _columnIndexOfSecondCandleDate: Int = getColumnIndexOrThrow(_stmt, "secondCandleDate")
        val _columnIndexOfSecondCandleAlive: Int = getColumnIndexOrThrow(_stmt, "secondCandleAlive")
        val _columnIndexOfSecondCandleDead: Int = getColumnIndexOrThrow(_stmt, "secondCandleDead")
        val _columnIndexOfExpectedHatchDate: Int = getColumnIndexOrThrow(_stmt, "expectedHatchDate")
        val _columnIndexOfActualHatchStartDate: Int = getColumnIndexOrThrow(_stmt,
            "actualHatchStartDate")
        val _columnIndexOfActualHatchEndDate: Int = getColumnIndexOrThrow(_stmt,
            "actualHatchEndDate")
        val _columnIndexOfChicksHatched: Int = getColumnIndexOrThrow(_stmt, "chicksHatched")
        val _columnIndexOfChicksMale: Int = getColumnIndexOrThrow(_stmt, "chicksMale")
        val _columnIndexOfChicksFemale: Int = getColumnIndexOrThrow(_stmt, "chicksFemale")
        val _columnIndexOfChicksUnsexed: Int = getColumnIndexOrThrow(_stmt, "chicksUnsexed")
        val _columnIndexOfDeadInShell: Int = getColumnIndexOrThrow(_stmt, "deadInShell")
        val _columnIndexOfPippedNotHatched: Int = getColumnIndexOrThrow(_stmt, "pippedNotHatched")
        val _columnIndexOfAverageChickWeight: Int = getColumnIndexOrThrow(_stmt,
            "averageChickWeight")
        val _columnIndexOfChickQualityScore: Int = getColumnIndexOrThrow(_stmt, "chickQualityScore")
        val _columnIndexOfQualityNotes: Int = getColumnIndexOrThrow(_stmt, "qualityNotes")
        val _columnIndexOfFertilityRate: Int = getColumnIndexOrThrow(_stmt, "fertilityRate")
        val _columnIndexOfHatchabilityOfFertile: Int = getColumnIndexOrThrow(_stmt,
            "hatchabilityOfFertile")
        val _columnIndexOfHatchabilityOfSet: Int = getColumnIndexOrThrow(_stmt, "hatchabilityOfSet")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfOffspringIdsJson: Int = getColumnIndexOrThrow(_stmt, "offspringIdsJson")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfMediaUrlsJson: Int = getColumnIndexOrThrow(_stmt, "mediaUrlsJson")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _result: MutableList<ClutchEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: ClutchEntity
          val _tmpClutchId: String
          _tmpClutchId = _stmt.getText(_columnIndexOfClutchId)
          val _tmpBreedingPairId: String?
          if (_stmt.isNull(_columnIndexOfBreedingPairId)) {
            _tmpBreedingPairId = null
          } else {
            _tmpBreedingPairId = _stmt.getText(_columnIndexOfBreedingPairId)
          }
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpSireId: String?
          if (_stmt.isNull(_columnIndexOfSireId)) {
            _tmpSireId = null
          } else {
            _tmpSireId = _stmt.getText(_columnIndexOfSireId)
          }
          val _tmpDamId: String?
          if (_stmt.isNull(_columnIndexOfDamId)) {
            _tmpDamId = null
          } else {
            _tmpDamId = _stmt.getText(_columnIndexOfDamId)
          }
          val _tmpClutchName: String?
          if (_stmt.isNull(_columnIndexOfClutchName)) {
            _tmpClutchName = null
          } else {
            _tmpClutchName = _stmt.getText(_columnIndexOfClutchName)
          }
          val _tmpClutchNumber: Int?
          if (_stmt.isNull(_columnIndexOfClutchNumber)) {
            _tmpClutchNumber = null
          } else {
            _tmpClutchNumber = _stmt.getLong(_columnIndexOfClutchNumber).toInt()
          }
          val _tmpEggsCollected: Int
          _tmpEggsCollected = _stmt.getLong(_columnIndexOfEggsCollected).toInt()
          val _tmpCollectionStartDate: Long
          _tmpCollectionStartDate = _stmt.getLong(_columnIndexOfCollectionStartDate)
          val _tmpCollectionEndDate: Long?
          if (_stmt.isNull(_columnIndexOfCollectionEndDate)) {
            _tmpCollectionEndDate = null
          } else {
            _tmpCollectionEndDate = _stmt.getLong(_columnIndexOfCollectionEndDate)
          }
          val _tmpSetDate: Long?
          if (_stmt.isNull(_columnIndexOfSetDate)) {
            _tmpSetDate = null
          } else {
            _tmpSetDate = _stmt.getLong(_columnIndexOfSetDate)
          }
          val _tmpEggsSet: Int
          _tmpEggsSet = _stmt.getLong(_columnIndexOfEggsSet).toInt()
          val _tmpIncubatorId: String?
          if (_stmt.isNull(_columnIndexOfIncubatorId)) {
            _tmpIncubatorId = null
          } else {
            _tmpIncubatorId = _stmt.getText(_columnIndexOfIncubatorId)
          }
          val _tmpIncubationNotes: String?
          if (_stmt.isNull(_columnIndexOfIncubationNotes)) {
            _tmpIncubationNotes = null
          } else {
            _tmpIncubationNotes = _stmt.getText(_columnIndexOfIncubationNotes)
          }
          val _tmpFirstCandleDate: Long?
          if (_stmt.isNull(_columnIndexOfFirstCandleDate)) {
            _tmpFirstCandleDate = null
          } else {
            _tmpFirstCandleDate = _stmt.getLong(_columnIndexOfFirstCandleDate)
          }
          val _tmpFirstCandleFertile: Int
          _tmpFirstCandleFertile = _stmt.getLong(_columnIndexOfFirstCandleFertile).toInt()
          val _tmpFirstCandleClear: Int
          _tmpFirstCandleClear = _stmt.getLong(_columnIndexOfFirstCandleClear).toInt()
          val _tmpFirstCandleEarlyDead: Int
          _tmpFirstCandleEarlyDead = _stmt.getLong(_columnIndexOfFirstCandleEarlyDead).toInt()
          val _tmpSecondCandleDate: Long?
          if (_stmt.isNull(_columnIndexOfSecondCandleDate)) {
            _tmpSecondCandleDate = null
          } else {
            _tmpSecondCandleDate = _stmt.getLong(_columnIndexOfSecondCandleDate)
          }
          val _tmpSecondCandleAlive: Int
          _tmpSecondCandleAlive = _stmt.getLong(_columnIndexOfSecondCandleAlive).toInt()
          val _tmpSecondCandleDead: Int
          _tmpSecondCandleDead = _stmt.getLong(_columnIndexOfSecondCandleDead).toInt()
          val _tmpExpectedHatchDate: Long?
          if (_stmt.isNull(_columnIndexOfExpectedHatchDate)) {
            _tmpExpectedHatchDate = null
          } else {
            _tmpExpectedHatchDate = _stmt.getLong(_columnIndexOfExpectedHatchDate)
          }
          val _tmpActualHatchStartDate: Long?
          if (_stmt.isNull(_columnIndexOfActualHatchStartDate)) {
            _tmpActualHatchStartDate = null
          } else {
            _tmpActualHatchStartDate = _stmt.getLong(_columnIndexOfActualHatchStartDate)
          }
          val _tmpActualHatchEndDate: Long?
          if (_stmt.isNull(_columnIndexOfActualHatchEndDate)) {
            _tmpActualHatchEndDate = null
          } else {
            _tmpActualHatchEndDate = _stmt.getLong(_columnIndexOfActualHatchEndDate)
          }
          val _tmpChicksHatched: Int
          _tmpChicksHatched = _stmt.getLong(_columnIndexOfChicksHatched).toInt()
          val _tmpChicksMale: Int
          _tmpChicksMale = _stmt.getLong(_columnIndexOfChicksMale).toInt()
          val _tmpChicksFemale: Int
          _tmpChicksFemale = _stmt.getLong(_columnIndexOfChicksFemale).toInt()
          val _tmpChicksUnsexed: Int
          _tmpChicksUnsexed = _stmt.getLong(_columnIndexOfChicksUnsexed).toInt()
          val _tmpDeadInShell: Int
          _tmpDeadInShell = _stmt.getLong(_columnIndexOfDeadInShell).toInt()
          val _tmpPippedNotHatched: Int
          _tmpPippedNotHatched = _stmt.getLong(_columnIndexOfPippedNotHatched).toInt()
          val _tmpAverageChickWeight: Double?
          if (_stmt.isNull(_columnIndexOfAverageChickWeight)) {
            _tmpAverageChickWeight = null
          } else {
            _tmpAverageChickWeight = _stmt.getDouble(_columnIndexOfAverageChickWeight)
          }
          val _tmpChickQualityScore: Int?
          if (_stmt.isNull(_columnIndexOfChickQualityScore)) {
            _tmpChickQualityScore = null
          } else {
            _tmpChickQualityScore = _stmt.getLong(_columnIndexOfChickQualityScore).toInt()
          }
          val _tmpQualityNotes: String?
          if (_stmt.isNull(_columnIndexOfQualityNotes)) {
            _tmpQualityNotes = null
          } else {
            _tmpQualityNotes = _stmt.getText(_columnIndexOfQualityNotes)
          }
          val _tmpFertilityRate: Double?
          if (_stmt.isNull(_columnIndexOfFertilityRate)) {
            _tmpFertilityRate = null
          } else {
            _tmpFertilityRate = _stmt.getDouble(_columnIndexOfFertilityRate)
          }
          val _tmpHatchabilityOfFertile: Double?
          if (_stmt.isNull(_columnIndexOfHatchabilityOfFertile)) {
            _tmpHatchabilityOfFertile = null
          } else {
            _tmpHatchabilityOfFertile = _stmt.getDouble(_columnIndexOfHatchabilityOfFertile)
          }
          val _tmpHatchabilityOfSet: Double?
          if (_stmt.isNull(_columnIndexOfHatchabilityOfSet)) {
            _tmpHatchabilityOfSet = null
          } else {
            _tmpHatchabilityOfSet = _stmt.getDouble(_columnIndexOfHatchabilityOfSet)
          }
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpOffspringIdsJson: String?
          if (_stmt.isNull(_columnIndexOfOffspringIdsJson)) {
            _tmpOffspringIdsJson = null
          } else {
            _tmpOffspringIdsJson = _stmt.getText(_columnIndexOfOffspringIdsJson)
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
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp != 0
          val _tmpSyncedAt: Long?
          if (_stmt.isNull(_columnIndexOfSyncedAt)) {
            _tmpSyncedAt = null
          } else {
            _tmpSyncedAt = _stmt.getLong(_columnIndexOfSyncedAt)
          }
          _item =
              ClutchEntity(_tmpClutchId,_tmpBreedingPairId,_tmpFarmerId,_tmpSireId,_tmpDamId,_tmpClutchName,_tmpClutchNumber,_tmpEggsCollected,_tmpCollectionStartDate,_tmpCollectionEndDate,_tmpSetDate,_tmpEggsSet,_tmpIncubatorId,_tmpIncubationNotes,_tmpFirstCandleDate,_tmpFirstCandleFertile,_tmpFirstCandleClear,_tmpFirstCandleEarlyDead,_tmpSecondCandleDate,_tmpSecondCandleAlive,_tmpSecondCandleDead,_tmpExpectedHatchDate,_tmpActualHatchStartDate,_tmpActualHatchEndDate,_tmpChicksHatched,_tmpChicksMale,_tmpChicksFemale,_tmpChicksUnsexed,_tmpDeadInShell,_tmpPippedNotHatched,_tmpAverageChickWeight,_tmpChickQualityScore,_tmpQualityNotes,_tmpFertilityRate,_tmpHatchabilityOfFertile,_tmpHatchabilityOfSet,_tmpStatus,_tmpOffspringIdsJson,_tmpNotes,_tmpMediaUrlsJson,_tmpCreatedAt,_tmpUpdatedAt,_tmpDirty,_tmpSyncedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getByFarmer(farmerId: String): List<ClutchEntity> {
    val _sql: String = "SELECT * FROM clutches WHERE farmerId = ? ORDER BY createdAt DESC"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, farmerId)
        val _columnIndexOfClutchId: Int = getColumnIndexOrThrow(_stmt, "clutchId")
        val _columnIndexOfBreedingPairId: Int = getColumnIndexOrThrow(_stmt, "breedingPairId")
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfSireId: Int = getColumnIndexOrThrow(_stmt, "sireId")
        val _columnIndexOfDamId: Int = getColumnIndexOrThrow(_stmt, "damId")
        val _columnIndexOfClutchName: Int = getColumnIndexOrThrow(_stmt, "clutchName")
        val _columnIndexOfClutchNumber: Int = getColumnIndexOrThrow(_stmt, "clutchNumber")
        val _columnIndexOfEggsCollected: Int = getColumnIndexOrThrow(_stmt, "eggsCollected")
        val _columnIndexOfCollectionStartDate: Int = getColumnIndexOrThrow(_stmt,
            "collectionStartDate")
        val _columnIndexOfCollectionEndDate: Int = getColumnIndexOrThrow(_stmt, "collectionEndDate")
        val _columnIndexOfSetDate: Int = getColumnIndexOrThrow(_stmt, "setDate")
        val _columnIndexOfEggsSet: Int = getColumnIndexOrThrow(_stmt, "eggsSet")
        val _columnIndexOfIncubatorId: Int = getColumnIndexOrThrow(_stmt, "incubatorId")
        val _columnIndexOfIncubationNotes: Int = getColumnIndexOrThrow(_stmt, "incubationNotes")
        val _columnIndexOfFirstCandleDate: Int = getColumnIndexOrThrow(_stmt, "firstCandleDate")
        val _columnIndexOfFirstCandleFertile: Int = getColumnIndexOrThrow(_stmt,
            "firstCandleFertile")
        val _columnIndexOfFirstCandleClear: Int = getColumnIndexOrThrow(_stmt, "firstCandleClear")
        val _columnIndexOfFirstCandleEarlyDead: Int = getColumnIndexOrThrow(_stmt,
            "firstCandleEarlyDead")
        val _columnIndexOfSecondCandleDate: Int = getColumnIndexOrThrow(_stmt, "secondCandleDate")
        val _columnIndexOfSecondCandleAlive: Int = getColumnIndexOrThrow(_stmt, "secondCandleAlive")
        val _columnIndexOfSecondCandleDead: Int = getColumnIndexOrThrow(_stmt, "secondCandleDead")
        val _columnIndexOfExpectedHatchDate: Int = getColumnIndexOrThrow(_stmt, "expectedHatchDate")
        val _columnIndexOfActualHatchStartDate: Int = getColumnIndexOrThrow(_stmt,
            "actualHatchStartDate")
        val _columnIndexOfActualHatchEndDate: Int = getColumnIndexOrThrow(_stmt,
            "actualHatchEndDate")
        val _columnIndexOfChicksHatched: Int = getColumnIndexOrThrow(_stmt, "chicksHatched")
        val _columnIndexOfChicksMale: Int = getColumnIndexOrThrow(_stmt, "chicksMale")
        val _columnIndexOfChicksFemale: Int = getColumnIndexOrThrow(_stmt, "chicksFemale")
        val _columnIndexOfChicksUnsexed: Int = getColumnIndexOrThrow(_stmt, "chicksUnsexed")
        val _columnIndexOfDeadInShell: Int = getColumnIndexOrThrow(_stmt, "deadInShell")
        val _columnIndexOfPippedNotHatched: Int = getColumnIndexOrThrow(_stmt, "pippedNotHatched")
        val _columnIndexOfAverageChickWeight: Int = getColumnIndexOrThrow(_stmt,
            "averageChickWeight")
        val _columnIndexOfChickQualityScore: Int = getColumnIndexOrThrow(_stmt, "chickQualityScore")
        val _columnIndexOfQualityNotes: Int = getColumnIndexOrThrow(_stmt, "qualityNotes")
        val _columnIndexOfFertilityRate: Int = getColumnIndexOrThrow(_stmt, "fertilityRate")
        val _columnIndexOfHatchabilityOfFertile: Int = getColumnIndexOrThrow(_stmt,
            "hatchabilityOfFertile")
        val _columnIndexOfHatchabilityOfSet: Int = getColumnIndexOrThrow(_stmt, "hatchabilityOfSet")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfOffspringIdsJson: Int = getColumnIndexOrThrow(_stmt, "offspringIdsJson")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfMediaUrlsJson: Int = getColumnIndexOrThrow(_stmt, "mediaUrlsJson")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _result: MutableList<ClutchEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: ClutchEntity
          val _tmpClutchId: String
          _tmpClutchId = _stmt.getText(_columnIndexOfClutchId)
          val _tmpBreedingPairId: String?
          if (_stmt.isNull(_columnIndexOfBreedingPairId)) {
            _tmpBreedingPairId = null
          } else {
            _tmpBreedingPairId = _stmt.getText(_columnIndexOfBreedingPairId)
          }
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpSireId: String?
          if (_stmt.isNull(_columnIndexOfSireId)) {
            _tmpSireId = null
          } else {
            _tmpSireId = _stmt.getText(_columnIndexOfSireId)
          }
          val _tmpDamId: String?
          if (_stmt.isNull(_columnIndexOfDamId)) {
            _tmpDamId = null
          } else {
            _tmpDamId = _stmt.getText(_columnIndexOfDamId)
          }
          val _tmpClutchName: String?
          if (_stmt.isNull(_columnIndexOfClutchName)) {
            _tmpClutchName = null
          } else {
            _tmpClutchName = _stmt.getText(_columnIndexOfClutchName)
          }
          val _tmpClutchNumber: Int?
          if (_stmt.isNull(_columnIndexOfClutchNumber)) {
            _tmpClutchNumber = null
          } else {
            _tmpClutchNumber = _stmt.getLong(_columnIndexOfClutchNumber).toInt()
          }
          val _tmpEggsCollected: Int
          _tmpEggsCollected = _stmt.getLong(_columnIndexOfEggsCollected).toInt()
          val _tmpCollectionStartDate: Long
          _tmpCollectionStartDate = _stmt.getLong(_columnIndexOfCollectionStartDate)
          val _tmpCollectionEndDate: Long?
          if (_stmt.isNull(_columnIndexOfCollectionEndDate)) {
            _tmpCollectionEndDate = null
          } else {
            _tmpCollectionEndDate = _stmt.getLong(_columnIndexOfCollectionEndDate)
          }
          val _tmpSetDate: Long?
          if (_stmt.isNull(_columnIndexOfSetDate)) {
            _tmpSetDate = null
          } else {
            _tmpSetDate = _stmt.getLong(_columnIndexOfSetDate)
          }
          val _tmpEggsSet: Int
          _tmpEggsSet = _stmt.getLong(_columnIndexOfEggsSet).toInt()
          val _tmpIncubatorId: String?
          if (_stmt.isNull(_columnIndexOfIncubatorId)) {
            _tmpIncubatorId = null
          } else {
            _tmpIncubatorId = _stmt.getText(_columnIndexOfIncubatorId)
          }
          val _tmpIncubationNotes: String?
          if (_stmt.isNull(_columnIndexOfIncubationNotes)) {
            _tmpIncubationNotes = null
          } else {
            _tmpIncubationNotes = _stmt.getText(_columnIndexOfIncubationNotes)
          }
          val _tmpFirstCandleDate: Long?
          if (_stmt.isNull(_columnIndexOfFirstCandleDate)) {
            _tmpFirstCandleDate = null
          } else {
            _tmpFirstCandleDate = _stmt.getLong(_columnIndexOfFirstCandleDate)
          }
          val _tmpFirstCandleFertile: Int
          _tmpFirstCandleFertile = _stmt.getLong(_columnIndexOfFirstCandleFertile).toInt()
          val _tmpFirstCandleClear: Int
          _tmpFirstCandleClear = _stmt.getLong(_columnIndexOfFirstCandleClear).toInt()
          val _tmpFirstCandleEarlyDead: Int
          _tmpFirstCandleEarlyDead = _stmt.getLong(_columnIndexOfFirstCandleEarlyDead).toInt()
          val _tmpSecondCandleDate: Long?
          if (_stmt.isNull(_columnIndexOfSecondCandleDate)) {
            _tmpSecondCandleDate = null
          } else {
            _tmpSecondCandleDate = _stmt.getLong(_columnIndexOfSecondCandleDate)
          }
          val _tmpSecondCandleAlive: Int
          _tmpSecondCandleAlive = _stmt.getLong(_columnIndexOfSecondCandleAlive).toInt()
          val _tmpSecondCandleDead: Int
          _tmpSecondCandleDead = _stmt.getLong(_columnIndexOfSecondCandleDead).toInt()
          val _tmpExpectedHatchDate: Long?
          if (_stmt.isNull(_columnIndexOfExpectedHatchDate)) {
            _tmpExpectedHatchDate = null
          } else {
            _tmpExpectedHatchDate = _stmt.getLong(_columnIndexOfExpectedHatchDate)
          }
          val _tmpActualHatchStartDate: Long?
          if (_stmt.isNull(_columnIndexOfActualHatchStartDate)) {
            _tmpActualHatchStartDate = null
          } else {
            _tmpActualHatchStartDate = _stmt.getLong(_columnIndexOfActualHatchStartDate)
          }
          val _tmpActualHatchEndDate: Long?
          if (_stmt.isNull(_columnIndexOfActualHatchEndDate)) {
            _tmpActualHatchEndDate = null
          } else {
            _tmpActualHatchEndDate = _stmt.getLong(_columnIndexOfActualHatchEndDate)
          }
          val _tmpChicksHatched: Int
          _tmpChicksHatched = _stmt.getLong(_columnIndexOfChicksHatched).toInt()
          val _tmpChicksMale: Int
          _tmpChicksMale = _stmt.getLong(_columnIndexOfChicksMale).toInt()
          val _tmpChicksFemale: Int
          _tmpChicksFemale = _stmt.getLong(_columnIndexOfChicksFemale).toInt()
          val _tmpChicksUnsexed: Int
          _tmpChicksUnsexed = _stmt.getLong(_columnIndexOfChicksUnsexed).toInt()
          val _tmpDeadInShell: Int
          _tmpDeadInShell = _stmt.getLong(_columnIndexOfDeadInShell).toInt()
          val _tmpPippedNotHatched: Int
          _tmpPippedNotHatched = _stmt.getLong(_columnIndexOfPippedNotHatched).toInt()
          val _tmpAverageChickWeight: Double?
          if (_stmt.isNull(_columnIndexOfAverageChickWeight)) {
            _tmpAverageChickWeight = null
          } else {
            _tmpAverageChickWeight = _stmt.getDouble(_columnIndexOfAverageChickWeight)
          }
          val _tmpChickQualityScore: Int?
          if (_stmt.isNull(_columnIndexOfChickQualityScore)) {
            _tmpChickQualityScore = null
          } else {
            _tmpChickQualityScore = _stmt.getLong(_columnIndexOfChickQualityScore).toInt()
          }
          val _tmpQualityNotes: String?
          if (_stmt.isNull(_columnIndexOfQualityNotes)) {
            _tmpQualityNotes = null
          } else {
            _tmpQualityNotes = _stmt.getText(_columnIndexOfQualityNotes)
          }
          val _tmpFertilityRate: Double?
          if (_stmt.isNull(_columnIndexOfFertilityRate)) {
            _tmpFertilityRate = null
          } else {
            _tmpFertilityRate = _stmt.getDouble(_columnIndexOfFertilityRate)
          }
          val _tmpHatchabilityOfFertile: Double?
          if (_stmt.isNull(_columnIndexOfHatchabilityOfFertile)) {
            _tmpHatchabilityOfFertile = null
          } else {
            _tmpHatchabilityOfFertile = _stmt.getDouble(_columnIndexOfHatchabilityOfFertile)
          }
          val _tmpHatchabilityOfSet: Double?
          if (_stmt.isNull(_columnIndexOfHatchabilityOfSet)) {
            _tmpHatchabilityOfSet = null
          } else {
            _tmpHatchabilityOfSet = _stmt.getDouble(_columnIndexOfHatchabilityOfSet)
          }
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpOffspringIdsJson: String?
          if (_stmt.isNull(_columnIndexOfOffspringIdsJson)) {
            _tmpOffspringIdsJson = null
          } else {
            _tmpOffspringIdsJson = _stmt.getText(_columnIndexOfOffspringIdsJson)
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
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp != 0
          val _tmpSyncedAt: Long?
          if (_stmt.isNull(_columnIndexOfSyncedAt)) {
            _tmpSyncedAt = null
          } else {
            _tmpSyncedAt = _stmt.getLong(_columnIndexOfSyncedAt)
          }
          _item =
              ClutchEntity(_tmpClutchId,_tmpBreedingPairId,_tmpFarmerId,_tmpSireId,_tmpDamId,_tmpClutchName,_tmpClutchNumber,_tmpEggsCollected,_tmpCollectionStartDate,_tmpCollectionEndDate,_tmpSetDate,_tmpEggsSet,_tmpIncubatorId,_tmpIncubationNotes,_tmpFirstCandleDate,_tmpFirstCandleFertile,_tmpFirstCandleClear,_tmpFirstCandleEarlyDead,_tmpSecondCandleDate,_tmpSecondCandleAlive,_tmpSecondCandleDead,_tmpExpectedHatchDate,_tmpActualHatchStartDate,_tmpActualHatchEndDate,_tmpChicksHatched,_tmpChicksMale,_tmpChicksFemale,_tmpChicksUnsexed,_tmpDeadInShell,_tmpPippedNotHatched,_tmpAverageChickWeight,_tmpChickQualityScore,_tmpQualityNotes,_tmpFertilityRate,_tmpHatchabilityOfFertile,_tmpHatchabilityOfSet,_tmpStatus,_tmpOffspringIdsJson,_tmpNotes,_tmpMediaUrlsJson,_tmpCreatedAt,_tmpUpdatedAt,_tmpDirty,_tmpSyncedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeByBreedingPair(pairId: String): Flow<List<ClutchEntity>> {
    val _sql: String = "SELECT * FROM clutches WHERE breedingPairId = ? ORDER BY createdAt DESC"
    return createFlow(__db, false, arrayOf("clutches")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, pairId)
        val _columnIndexOfClutchId: Int = getColumnIndexOrThrow(_stmt, "clutchId")
        val _columnIndexOfBreedingPairId: Int = getColumnIndexOrThrow(_stmt, "breedingPairId")
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfSireId: Int = getColumnIndexOrThrow(_stmt, "sireId")
        val _columnIndexOfDamId: Int = getColumnIndexOrThrow(_stmt, "damId")
        val _columnIndexOfClutchName: Int = getColumnIndexOrThrow(_stmt, "clutchName")
        val _columnIndexOfClutchNumber: Int = getColumnIndexOrThrow(_stmt, "clutchNumber")
        val _columnIndexOfEggsCollected: Int = getColumnIndexOrThrow(_stmt, "eggsCollected")
        val _columnIndexOfCollectionStartDate: Int = getColumnIndexOrThrow(_stmt,
            "collectionStartDate")
        val _columnIndexOfCollectionEndDate: Int = getColumnIndexOrThrow(_stmt, "collectionEndDate")
        val _columnIndexOfSetDate: Int = getColumnIndexOrThrow(_stmt, "setDate")
        val _columnIndexOfEggsSet: Int = getColumnIndexOrThrow(_stmt, "eggsSet")
        val _columnIndexOfIncubatorId: Int = getColumnIndexOrThrow(_stmt, "incubatorId")
        val _columnIndexOfIncubationNotes: Int = getColumnIndexOrThrow(_stmt, "incubationNotes")
        val _columnIndexOfFirstCandleDate: Int = getColumnIndexOrThrow(_stmt, "firstCandleDate")
        val _columnIndexOfFirstCandleFertile: Int = getColumnIndexOrThrow(_stmt,
            "firstCandleFertile")
        val _columnIndexOfFirstCandleClear: Int = getColumnIndexOrThrow(_stmt, "firstCandleClear")
        val _columnIndexOfFirstCandleEarlyDead: Int = getColumnIndexOrThrow(_stmt,
            "firstCandleEarlyDead")
        val _columnIndexOfSecondCandleDate: Int = getColumnIndexOrThrow(_stmt, "secondCandleDate")
        val _columnIndexOfSecondCandleAlive: Int = getColumnIndexOrThrow(_stmt, "secondCandleAlive")
        val _columnIndexOfSecondCandleDead: Int = getColumnIndexOrThrow(_stmt, "secondCandleDead")
        val _columnIndexOfExpectedHatchDate: Int = getColumnIndexOrThrow(_stmt, "expectedHatchDate")
        val _columnIndexOfActualHatchStartDate: Int = getColumnIndexOrThrow(_stmt,
            "actualHatchStartDate")
        val _columnIndexOfActualHatchEndDate: Int = getColumnIndexOrThrow(_stmt,
            "actualHatchEndDate")
        val _columnIndexOfChicksHatched: Int = getColumnIndexOrThrow(_stmt, "chicksHatched")
        val _columnIndexOfChicksMale: Int = getColumnIndexOrThrow(_stmt, "chicksMale")
        val _columnIndexOfChicksFemale: Int = getColumnIndexOrThrow(_stmt, "chicksFemale")
        val _columnIndexOfChicksUnsexed: Int = getColumnIndexOrThrow(_stmt, "chicksUnsexed")
        val _columnIndexOfDeadInShell: Int = getColumnIndexOrThrow(_stmt, "deadInShell")
        val _columnIndexOfPippedNotHatched: Int = getColumnIndexOrThrow(_stmt, "pippedNotHatched")
        val _columnIndexOfAverageChickWeight: Int = getColumnIndexOrThrow(_stmt,
            "averageChickWeight")
        val _columnIndexOfChickQualityScore: Int = getColumnIndexOrThrow(_stmt, "chickQualityScore")
        val _columnIndexOfQualityNotes: Int = getColumnIndexOrThrow(_stmt, "qualityNotes")
        val _columnIndexOfFertilityRate: Int = getColumnIndexOrThrow(_stmt, "fertilityRate")
        val _columnIndexOfHatchabilityOfFertile: Int = getColumnIndexOrThrow(_stmt,
            "hatchabilityOfFertile")
        val _columnIndexOfHatchabilityOfSet: Int = getColumnIndexOrThrow(_stmt, "hatchabilityOfSet")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfOffspringIdsJson: Int = getColumnIndexOrThrow(_stmt, "offspringIdsJson")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfMediaUrlsJson: Int = getColumnIndexOrThrow(_stmt, "mediaUrlsJson")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _result: MutableList<ClutchEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: ClutchEntity
          val _tmpClutchId: String
          _tmpClutchId = _stmt.getText(_columnIndexOfClutchId)
          val _tmpBreedingPairId: String?
          if (_stmt.isNull(_columnIndexOfBreedingPairId)) {
            _tmpBreedingPairId = null
          } else {
            _tmpBreedingPairId = _stmt.getText(_columnIndexOfBreedingPairId)
          }
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpSireId: String?
          if (_stmt.isNull(_columnIndexOfSireId)) {
            _tmpSireId = null
          } else {
            _tmpSireId = _stmt.getText(_columnIndexOfSireId)
          }
          val _tmpDamId: String?
          if (_stmt.isNull(_columnIndexOfDamId)) {
            _tmpDamId = null
          } else {
            _tmpDamId = _stmt.getText(_columnIndexOfDamId)
          }
          val _tmpClutchName: String?
          if (_stmt.isNull(_columnIndexOfClutchName)) {
            _tmpClutchName = null
          } else {
            _tmpClutchName = _stmt.getText(_columnIndexOfClutchName)
          }
          val _tmpClutchNumber: Int?
          if (_stmt.isNull(_columnIndexOfClutchNumber)) {
            _tmpClutchNumber = null
          } else {
            _tmpClutchNumber = _stmt.getLong(_columnIndexOfClutchNumber).toInt()
          }
          val _tmpEggsCollected: Int
          _tmpEggsCollected = _stmt.getLong(_columnIndexOfEggsCollected).toInt()
          val _tmpCollectionStartDate: Long
          _tmpCollectionStartDate = _stmt.getLong(_columnIndexOfCollectionStartDate)
          val _tmpCollectionEndDate: Long?
          if (_stmt.isNull(_columnIndexOfCollectionEndDate)) {
            _tmpCollectionEndDate = null
          } else {
            _tmpCollectionEndDate = _stmt.getLong(_columnIndexOfCollectionEndDate)
          }
          val _tmpSetDate: Long?
          if (_stmt.isNull(_columnIndexOfSetDate)) {
            _tmpSetDate = null
          } else {
            _tmpSetDate = _stmt.getLong(_columnIndexOfSetDate)
          }
          val _tmpEggsSet: Int
          _tmpEggsSet = _stmt.getLong(_columnIndexOfEggsSet).toInt()
          val _tmpIncubatorId: String?
          if (_stmt.isNull(_columnIndexOfIncubatorId)) {
            _tmpIncubatorId = null
          } else {
            _tmpIncubatorId = _stmt.getText(_columnIndexOfIncubatorId)
          }
          val _tmpIncubationNotes: String?
          if (_stmt.isNull(_columnIndexOfIncubationNotes)) {
            _tmpIncubationNotes = null
          } else {
            _tmpIncubationNotes = _stmt.getText(_columnIndexOfIncubationNotes)
          }
          val _tmpFirstCandleDate: Long?
          if (_stmt.isNull(_columnIndexOfFirstCandleDate)) {
            _tmpFirstCandleDate = null
          } else {
            _tmpFirstCandleDate = _stmt.getLong(_columnIndexOfFirstCandleDate)
          }
          val _tmpFirstCandleFertile: Int
          _tmpFirstCandleFertile = _stmt.getLong(_columnIndexOfFirstCandleFertile).toInt()
          val _tmpFirstCandleClear: Int
          _tmpFirstCandleClear = _stmt.getLong(_columnIndexOfFirstCandleClear).toInt()
          val _tmpFirstCandleEarlyDead: Int
          _tmpFirstCandleEarlyDead = _stmt.getLong(_columnIndexOfFirstCandleEarlyDead).toInt()
          val _tmpSecondCandleDate: Long?
          if (_stmt.isNull(_columnIndexOfSecondCandleDate)) {
            _tmpSecondCandleDate = null
          } else {
            _tmpSecondCandleDate = _stmt.getLong(_columnIndexOfSecondCandleDate)
          }
          val _tmpSecondCandleAlive: Int
          _tmpSecondCandleAlive = _stmt.getLong(_columnIndexOfSecondCandleAlive).toInt()
          val _tmpSecondCandleDead: Int
          _tmpSecondCandleDead = _stmt.getLong(_columnIndexOfSecondCandleDead).toInt()
          val _tmpExpectedHatchDate: Long?
          if (_stmt.isNull(_columnIndexOfExpectedHatchDate)) {
            _tmpExpectedHatchDate = null
          } else {
            _tmpExpectedHatchDate = _stmt.getLong(_columnIndexOfExpectedHatchDate)
          }
          val _tmpActualHatchStartDate: Long?
          if (_stmt.isNull(_columnIndexOfActualHatchStartDate)) {
            _tmpActualHatchStartDate = null
          } else {
            _tmpActualHatchStartDate = _stmt.getLong(_columnIndexOfActualHatchStartDate)
          }
          val _tmpActualHatchEndDate: Long?
          if (_stmt.isNull(_columnIndexOfActualHatchEndDate)) {
            _tmpActualHatchEndDate = null
          } else {
            _tmpActualHatchEndDate = _stmt.getLong(_columnIndexOfActualHatchEndDate)
          }
          val _tmpChicksHatched: Int
          _tmpChicksHatched = _stmt.getLong(_columnIndexOfChicksHatched).toInt()
          val _tmpChicksMale: Int
          _tmpChicksMale = _stmt.getLong(_columnIndexOfChicksMale).toInt()
          val _tmpChicksFemale: Int
          _tmpChicksFemale = _stmt.getLong(_columnIndexOfChicksFemale).toInt()
          val _tmpChicksUnsexed: Int
          _tmpChicksUnsexed = _stmt.getLong(_columnIndexOfChicksUnsexed).toInt()
          val _tmpDeadInShell: Int
          _tmpDeadInShell = _stmt.getLong(_columnIndexOfDeadInShell).toInt()
          val _tmpPippedNotHatched: Int
          _tmpPippedNotHatched = _stmt.getLong(_columnIndexOfPippedNotHatched).toInt()
          val _tmpAverageChickWeight: Double?
          if (_stmt.isNull(_columnIndexOfAverageChickWeight)) {
            _tmpAverageChickWeight = null
          } else {
            _tmpAverageChickWeight = _stmt.getDouble(_columnIndexOfAverageChickWeight)
          }
          val _tmpChickQualityScore: Int?
          if (_stmt.isNull(_columnIndexOfChickQualityScore)) {
            _tmpChickQualityScore = null
          } else {
            _tmpChickQualityScore = _stmt.getLong(_columnIndexOfChickQualityScore).toInt()
          }
          val _tmpQualityNotes: String?
          if (_stmt.isNull(_columnIndexOfQualityNotes)) {
            _tmpQualityNotes = null
          } else {
            _tmpQualityNotes = _stmt.getText(_columnIndexOfQualityNotes)
          }
          val _tmpFertilityRate: Double?
          if (_stmt.isNull(_columnIndexOfFertilityRate)) {
            _tmpFertilityRate = null
          } else {
            _tmpFertilityRate = _stmt.getDouble(_columnIndexOfFertilityRate)
          }
          val _tmpHatchabilityOfFertile: Double?
          if (_stmt.isNull(_columnIndexOfHatchabilityOfFertile)) {
            _tmpHatchabilityOfFertile = null
          } else {
            _tmpHatchabilityOfFertile = _stmt.getDouble(_columnIndexOfHatchabilityOfFertile)
          }
          val _tmpHatchabilityOfSet: Double?
          if (_stmt.isNull(_columnIndexOfHatchabilityOfSet)) {
            _tmpHatchabilityOfSet = null
          } else {
            _tmpHatchabilityOfSet = _stmt.getDouble(_columnIndexOfHatchabilityOfSet)
          }
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpOffspringIdsJson: String?
          if (_stmt.isNull(_columnIndexOfOffspringIdsJson)) {
            _tmpOffspringIdsJson = null
          } else {
            _tmpOffspringIdsJson = _stmt.getText(_columnIndexOfOffspringIdsJson)
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
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp != 0
          val _tmpSyncedAt: Long?
          if (_stmt.isNull(_columnIndexOfSyncedAt)) {
            _tmpSyncedAt = null
          } else {
            _tmpSyncedAt = _stmt.getLong(_columnIndexOfSyncedAt)
          }
          _item =
              ClutchEntity(_tmpClutchId,_tmpBreedingPairId,_tmpFarmerId,_tmpSireId,_tmpDamId,_tmpClutchName,_tmpClutchNumber,_tmpEggsCollected,_tmpCollectionStartDate,_tmpCollectionEndDate,_tmpSetDate,_tmpEggsSet,_tmpIncubatorId,_tmpIncubationNotes,_tmpFirstCandleDate,_tmpFirstCandleFertile,_tmpFirstCandleClear,_tmpFirstCandleEarlyDead,_tmpSecondCandleDate,_tmpSecondCandleAlive,_tmpSecondCandleDead,_tmpExpectedHatchDate,_tmpActualHatchStartDate,_tmpActualHatchEndDate,_tmpChicksHatched,_tmpChicksMale,_tmpChicksFemale,_tmpChicksUnsexed,_tmpDeadInShell,_tmpPippedNotHatched,_tmpAverageChickWeight,_tmpChickQualityScore,_tmpQualityNotes,_tmpFertilityRate,_tmpHatchabilityOfFertile,_tmpHatchabilityOfSet,_tmpStatus,_tmpOffspringIdsJson,_tmpNotes,_tmpMediaUrlsJson,_tmpCreatedAt,_tmpUpdatedAt,_tmpDirty,_tmpSyncedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getByBreedingPair(pairId: String): List<ClutchEntity> {
    val _sql: String = "SELECT * FROM clutches WHERE breedingPairId = ? ORDER BY createdAt DESC"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, pairId)
        val _columnIndexOfClutchId: Int = getColumnIndexOrThrow(_stmt, "clutchId")
        val _columnIndexOfBreedingPairId: Int = getColumnIndexOrThrow(_stmt, "breedingPairId")
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfSireId: Int = getColumnIndexOrThrow(_stmt, "sireId")
        val _columnIndexOfDamId: Int = getColumnIndexOrThrow(_stmt, "damId")
        val _columnIndexOfClutchName: Int = getColumnIndexOrThrow(_stmt, "clutchName")
        val _columnIndexOfClutchNumber: Int = getColumnIndexOrThrow(_stmt, "clutchNumber")
        val _columnIndexOfEggsCollected: Int = getColumnIndexOrThrow(_stmt, "eggsCollected")
        val _columnIndexOfCollectionStartDate: Int = getColumnIndexOrThrow(_stmt,
            "collectionStartDate")
        val _columnIndexOfCollectionEndDate: Int = getColumnIndexOrThrow(_stmt, "collectionEndDate")
        val _columnIndexOfSetDate: Int = getColumnIndexOrThrow(_stmt, "setDate")
        val _columnIndexOfEggsSet: Int = getColumnIndexOrThrow(_stmt, "eggsSet")
        val _columnIndexOfIncubatorId: Int = getColumnIndexOrThrow(_stmt, "incubatorId")
        val _columnIndexOfIncubationNotes: Int = getColumnIndexOrThrow(_stmt, "incubationNotes")
        val _columnIndexOfFirstCandleDate: Int = getColumnIndexOrThrow(_stmt, "firstCandleDate")
        val _columnIndexOfFirstCandleFertile: Int = getColumnIndexOrThrow(_stmt,
            "firstCandleFertile")
        val _columnIndexOfFirstCandleClear: Int = getColumnIndexOrThrow(_stmt, "firstCandleClear")
        val _columnIndexOfFirstCandleEarlyDead: Int = getColumnIndexOrThrow(_stmt,
            "firstCandleEarlyDead")
        val _columnIndexOfSecondCandleDate: Int = getColumnIndexOrThrow(_stmt, "secondCandleDate")
        val _columnIndexOfSecondCandleAlive: Int = getColumnIndexOrThrow(_stmt, "secondCandleAlive")
        val _columnIndexOfSecondCandleDead: Int = getColumnIndexOrThrow(_stmt, "secondCandleDead")
        val _columnIndexOfExpectedHatchDate: Int = getColumnIndexOrThrow(_stmt, "expectedHatchDate")
        val _columnIndexOfActualHatchStartDate: Int = getColumnIndexOrThrow(_stmt,
            "actualHatchStartDate")
        val _columnIndexOfActualHatchEndDate: Int = getColumnIndexOrThrow(_stmt,
            "actualHatchEndDate")
        val _columnIndexOfChicksHatched: Int = getColumnIndexOrThrow(_stmt, "chicksHatched")
        val _columnIndexOfChicksMale: Int = getColumnIndexOrThrow(_stmt, "chicksMale")
        val _columnIndexOfChicksFemale: Int = getColumnIndexOrThrow(_stmt, "chicksFemale")
        val _columnIndexOfChicksUnsexed: Int = getColumnIndexOrThrow(_stmt, "chicksUnsexed")
        val _columnIndexOfDeadInShell: Int = getColumnIndexOrThrow(_stmt, "deadInShell")
        val _columnIndexOfPippedNotHatched: Int = getColumnIndexOrThrow(_stmt, "pippedNotHatched")
        val _columnIndexOfAverageChickWeight: Int = getColumnIndexOrThrow(_stmt,
            "averageChickWeight")
        val _columnIndexOfChickQualityScore: Int = getColumnIndexOrThrow(_stmt, "chickQualityScore")
        val _columnIndexOfQualityNotes: Int = getColumnIndexOrThrow(_stmt, "qualityNotes")
        val _columnIndexOfFertilityRate: Int = getColumnIndexOrThrow(_stmt, "fertilityRate")
        val _columnIndexOfHatchabilityOfFertile: Int = getColumnIndexOrThrow(_stmt,
            "hatchabilityOfFertile")
        val _columnIndexOfHatchabilityOfSet: Int = getColumnIndexOrThrow(_stmt, "hatchabilityOfSet")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfOffspringIdsJson: Int = getColumnIndexOrThrow(_stmt, "offspringIdsJson")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfMediaUrlsJson: Int = getColumnIndexOrThrow(_stmt, "mediaUrlsJson")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _result: MutableList<ClutchEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: ClutchEntity
          val _tmpClutchId: String
          _tmpClutchId = _stmt.getText(_columnIndexOfClutchId)
          val _tmpBreedingPairId: String?
          if (_stmt.isNull(_columnIndexOfBreedingPairId)) {
            _tmpBreedingPairId = null
          } else {
            _tmpBreedingPairId = _stmt.getText(_columnIndexOfBreedingPairId)
          }
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpSireId: String?
          if (_stmt.isNull(_columnIndexOfSireId)) {
            _tmpSireId = null
          } else {
            _tmpSireId = _stmt.getText(_columnIndexOfSireId)
          }
          val _tmpDamId: String?
          if (_stmt.isNull(_columnIndexOfDamId)) {
            _tmpDamId = null
          } else {
            _tmpDamId = _stmt.getText(_columnIndexOfDamId)
          }
          val _tmpClutchName: String?
          if (_stmt.isNull(_columnIndexOfClutchName)) {
            _tmpClutchName = null
          } else {
            _tmpClutchName = _stmt.getText(_columnIndexOfClutchName)
          }
          val _tmpClutchNumber: Int?
          if (_stmt.isNull(_columnIndexOfClutchNumber)) {
            _tmpClutchNumber = null
          } else {
            _tmpClutchNumber = _stmt.getLong(_columnIndexOfClutchNumber).toInt()
          }
          val _tmpEggsCollected: Int
          _tmpEggsCollected = _stmt.getLong(_columnIndexOfEggsCollected).toInt()
          val _tmpCollectionStartDate: Long
          _tmpCollectionStartDate = _stmt.getLong(_columnIndexOfCollectionStartDate)
          val _tmpCollectionEndDate: Long?
          if (_stmt.isNull(_columnIndexOfCollectionEndDate)) {
            _tmpCollectionEndDate = null
          } else {
            _tmpCollectionEndDate = _stmt.getLong(_columnIndexOfCollectionEndDate)
          }
          val _tmpSetDate: Long?
          if (_stmt.isNull(_columnIndexOfSetDate)) {
            _tmpSetDate = null
          } else {
            _tmpSetDate = _stmt.getLong(_columnIndexOfSetDate)
          }
          val _tmpEggsSet: Int
          _tmpEggsSet = _stmt.getLong(_columnIndexOfEggsSet).toInt()
          val _tmpIncubatorId: String?
          if (_stmt.isNull(_columnIndexOfIncubatorId)) {
            _tmpIncubatorId = null
          } else {
            _tmpIncubatorId = _stmt.getText(_columnIndexOfIncubatorId)
          }
          val _tmpIncubationNotes: String?
          if (_stmt.isNull(_columnIndexOfIncubationNotes)) {
            _tmpIncubationNotes = null
          } else {
            _tmpIncubationNotes = _stmt.getText(_columnIndexOfIncubationNotes)
          }
          val _tmpFirstCandleDate: Long?
          if (_stmt.isNull(_columnIndexOfFirstCandleDate)) {
            _tmpFirstCandleDate = null
          } else {
            _tmpFirstCandleDate = _stmt.getLong(_columnIndexOfFirstCandleDate)
          }
          val _tmpFirstCandleFertile: Int
          _tmpFirstCandleFertile = _stmt.getLong(_columnIndexOfFirstCandleFertile).toInt()
          val _tmpFirstCandleClear: Int
          _tmpFirstCandleClear = _stmt.getLong(_columnIndexOfFirstCandleClear).toInt()
          val _tmpFirstCandleEarlyDead: Int
          _tmpFirstCandleEarlyDead = _stmt.getLong(_columnIndexOfFirstCandleEarlyDead).toInt()
          val _tmpSecondCandleDate: Long?
          if (_stmt.isNull(_columnIndexOfSecondCandleDate)) {
            _tmpSecondCandleDate = null
          } else {
            _tmpSecondCandleDate = _stmt.getLong(_columnIndexOfSecondCandleDate)
          }
          val _tmpSecondCandleAlive: Int
          _tmpSecondCandleAlive = _stmt.getLong(_columnIndexOfSecondCandleAlive).toInt()
          val _tmpSecondCandleDead: Int
          _tmpSecondCandleDead = _stmt.getLong(_columnIndexOfSecondCandleDead).toInt()
          val _tmpExpectedHatchDate: Long?
          if (_stmt.isNull(_columnIndexOfExpectedHatchDate)) {
            _tmpExpectedHatchDate = null
          } else {
            _tmpExpectedHatchDate = _stmt.getLong(_columnIndexOfExpectedHatchDate)
          }
          val _tmpActualHatchStartDate: Long?
          if (_stmt.isNull(_columnIndexOfActualHatchStartDate)) {
            _tmpActualHatchStartDate = null
          } else {
            _tmpActualHatchStartDate = _stmt.getLong(_columnIndexOfActualHatchStartDate)
          }
          val _tmpActualHatchEndDate: Long?
          if (_stmt.isNull(_columnIndexOfActualHatchEndDate)) {
            _tmpActualHatchEndDate = null
          } else {
            _tmpActualHatchEndDate = _stmt.getLong(_columnIndexOfActualHatchEndDate)
          }
          val _tmpChicksHatched: Int
          _tmpChicksHatched = _stmt.getLong(_columnIndexOfChicksHatched).toInt()
          val _tmpChicksMale: Int
          _tmpChicksMale = _stmt.getLong(_columnIndexOfChicksMale).toInt()
          val _tmpChicksFemale: Int
          _tmpChicksFemale = _stmt.getLong(_columnIndexOfChicksFemale).toInt()
          val _tmpChicksUnsexed: Int
          _tmpChicksUnsexed = _stmt.getLong(_columnIndexOfChicksUnsexed).toInt()
          val _tmpDeadInShell: Int
          _tmpDeadInShell = _stmt.getLong(_columnIndexOfDeadInShell).toInt()
          val _tmpPippedNotHatched: Int
          _tmpPippedNotHatched = _stmt.getLong(_columnIndexOfPippedNotHatched).toInt()
          val _tmpAverageChickWeight: Double?
          if (_stmt.isNull(_columnIndexOfAverageChickWeight)) {
            _tmpAverageChickWeight = null
          } else {
            _tmpAverageChickWeight = _stmt.getDouble(_columnIndexOfAverageChickWeight)
          }
          val _tmpChickQualityScore: Int?
          if (_stmt.isNull(_columnIndexOfChickQualityScore)) {
            _tmpChickQualityScore = null
          } else {
            _tmpChickQualityScore = _stmt.getLong(_columnIndexOfChickQualityScore).toInt()
          }
          val _tmpQualityNotes: String?
          if (_stmt.isNull(_columnIndexOfQualityNotes)) {
            _tmpQualityNotes = null
          } else {
            _tmpQualityNotes = _stmt.getText(_columnIndexOfQualityNotes)
          }
          val _tmpFertilityRate: Double?
          if (_stmt.isNull(_columnIndexOfFertilityRate)) {
            _tmpFertilityRate = null
          } else {
            _tmpFertilityRate = _stmt.getDouble(_columnIndexOfFertilityRate)
          }
          val _tmpHatchabilityOfFertile: Double?
          if (_stmt.isNull(_columnIndexOfHatchabilityOfFertile)) {
            _tmpHatchabilityOfFertile = null
          } else {
            _tmpHatchabilityOfFertile = _stmt.getDouble(_columnIndexOfHatchabilityOfFertile)
          }
          val _tmpHatchabilityOfSet: Double?
          if (_stmt.isNull(_columnIndexOfHatchabilityOfSet)) {
            _tmpHatchabilityOfSet = null
          } else {
            _tmpHatchabilityOfSet = _stmt.getDouble(_columnIndexOfHatchabilityOfSet)
          }
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpOffspringIdsJson: String?
          if (_stmt.isNull(_columnIndexOfOffspringIdsJson)) {
            _tmpOffspringIdsJson = null
          } else {
            _tmpOffspringIdsJson = _stmt.getText(_columnIndexOfOffspringIdsJson)
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
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp != 0
          val _tmpSyncedAt: Long?
          if (_stmt.isNull(_columnIndexOfSyncedAt)) {
            _tmpSyncedAt = null
          } else {
            _tmpSyncedAt = _stmt.getLong(_columnIndexOfSyncedAt)
          }
          _item =
              ClutchEntity(_tmpClutchId,_tmpBreedingPairId,_tmpFarmerId,_tmpSireId,_tmpDamId,_tmpClutchName,_tmpClutchNumber,_tmpEggsCollected,_tmpCollectionStartDate,_tmpCollectionEndDate,_tmpSetDate,_tmpEggsSet,_tmpIncubatorId,_tmpIncubationNotes,_tmpFirstCandleDate,_tmpFirstCandleFertile,_tmpFirstCandleClear,_tmpFirstCandleEarlyDead,_tmpSecondCandleDate,_tmpSecondCandleAlive,_tmpSecondCandleDead,_tmpExpectedHatchDate,_tmpActualHatchStartDate,_tmpActualHatchEndDate,_tmpChicksHatched,_tmpChicksMale,_tmpChicksFemale,_tmpChicksUnsexed,_tmpDeadInShell,_tmpPippedNotHatched,_tmpAverageChickWeight,_tmpChickQualityScore,_tmpQualityNotes,_tmpFertilityRate,_tmpHatchabilityOfFertile,_tmpHatchabilityOfSet,_tmpStatus,_tmpOffspringIdsJson,_tmpNotes,_tmpMediaUrlsJson,_tmpCreatedAt,_tmpUpdatedAt,_tmpDirty,_tmpSyncedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeByStatus(farmerId: String, status: String): Flow<List<ClutchEntity>> {
    val _sql: String =
        "SELECT * FROM clutches WHERE farmerId = ? AND status = ? ORDER BY createdAt DESC"
    return createFlow(__db, false, arrayOf("clutches")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, farmerId)
        _argIndex = 2
        _stmt.bindText(_argIndex, status)
        val _columnIndexOfClutchId: Int = getColumnIndexOrThrow(_stmt, "clutchId")
        val _columnIndexOfBreedingPairId: Int = getColumnIndexOrThrow(_stmt, "breedingPairId")
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfSireId: Int = getColumnIndexOrThrow(_stmt, "sireId")
        val _columnIndexOfDamId: Int = getColumnIndexOrThrow(_stmt, "damId")
        val _columnIndexOfClutchName: Int = getColumnIndexOrThrow(_stmt, "clutchName")
        val _columnIndexOfClutchNumber: Int = getColumnIndexOrThrow(_stmt, "clutchNumber")
        val _columnIndexOfEggsCollected: Int = getColumnIndexOrThrow(_stmt, "eggsCollected")
        val _columnIndexOfCollectionStartDate: Int = getColumnIndexOrThrow(_stmt,
            "collectionStartDate")
        val _columnIndexOfCollectionEndDate: Int = getColumnIndexOrThrow(_stmt, "collectionEndDate")
        val _columnIndexOfSetDate: Int = getColumnIndexOrThrow(_stmt, "setDate")
        val _columnIndexOfEggsSet: Int = getColumnIndexOrThrow(_stmt, "eggsSet")
        val _columnIndexOfIncubatorId: Int = getColumnIndexOrThrow(_stmt, "incubatorId")
        val _columnIndexOfIncubationNotes: Int = getColumnIndexOrThrow(_stmt, "incubationNotes")
        val _columnIndexOfFirstCandleDate: Int = getColumnIndexOrThrow(_stmt, "firstCandleDate")
        val _columnIndexOfFirstCandleFertile: Int = getColumnIndexOrThrow(_stmt,
            "firstCandleFertile")
        val _columnIndexOfFirstCandleClear: Int = getColumnIndexOrThrow(_stmt, "firstCandleClear")
        val _columnIndexOfFirstCandleEarlyDead: Int = getColumnIndexOrThrow(_stmt,
            "firstCandleEarlyDead")
        val _columnIndexOfSecondCandleDate: Int = getColumnIndexOrThrow(_stmt, "secondCandleDate")
        val _columnIndexOfSecondCandleAlive: Int = getColumnIndexOrThrow(_stmt, "secondCandleAlive")
        val _columnIndexOfSecondCandleDead: Int = getColumnIndexOrThrow(_stmt, "secondCandleDead")
        val _columnIndexOfExpectedHatchDate: Int = getColumnIndexOrThrow(_stmt, "expectedHatchDate")
        val _columnIndexOfActualHatchStartDate: Int = getColumnIndexOrThrow(_stmt,
            "actualHatchStartDate")
        val _columnIndexOfActualHatchEndDate: Int = getColumnIndexOrThrow(_stmt,
            "actualHatchEndDate")
        val _columnIndexOfChicksHatched: Int = getColumnIndexOrThrow(_stmt, "chicksHatched")
        val _columnIndexOfChicksMale: Int = getColumnIndexOrThrow(_stmt, "chicksMale")
        val _columnIndexOfChicksFemale: Int = getColumnIndexOrThrow(_stmt, "chicksFemale")
        val _columnIndexOfChicksUnsexed: Int = getColumnIndexOrThrow(_stmt, "chicksUnsexed")
        val _columnIndexOfDeadInShell: Int = getColumnIndexOrThrow(_stmt, "deadInShell")
        val _columnIndexOfPippedNotHatched: Int = getColumnIndexOrThrow(_stmt, "pippedNotHatched")
        val _columnIndexOfAverageChickWeight: Int = getColumnIndexOrThrow(_stmt,
            "averageChickWeight")
        val _columnIndexOfChickQualityScore: Int = getColumnIndexOrThrow(_stmt, "chickQualityScore")
        val _columnIndexOfQualityNotes: Int = getColumnIndexOrThrow(_stmt, "qualityNotes")
        val _columnIndexOfFertilityRate: Int = getColumnIndexOrThrow(_stmt, "fertilityRate")
        val _columnIndexOfHatchabilityOfFertile: Int = getColumnIndexOrThrow(_stmt,
            "hatchabilityOfFertile")
        val _columnIndexOfHatchabilityOfSet: Int = getColumnIndexOrThrow(_stmt, "hatchabilityOfSet")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfOffspringIdsJson: Int = getColumnIndexOrThrow(_stmt, "offspringIdsJson")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfMediaUrlsJson: Int = getColumnIndexOrThrow(_stmt, "mediaUrlsJson")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _result: MutableList<ClutchEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: ClutchEntity
          val _tmpClutchId: String
          _tmpClutchId = _stmt.getText(_columnIndexOfClutchId)
          val _tmpBreedingPairId: String?
          if (_stmt.isNull(_columnIndexOfBreedingPairId)) {
            _tmpBreedingPairId = null
          } else {
            _tmpBreedingPairId = _stmt.getText(_columnIndexOfBreedingPairId)
          }
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpSireId: String?
          if (_stmt.isNull(_columnIndexOfSireId)) {
            _tmpSireId = null
          } else {
            _tmpSireId = _stmt.getText(_columnIndexOfSireId)
          }
          val _tmpDamId: String?
          if (_stmt.isNull(_columnIndexOfDamId)) {
            _tmpDamId = null
          } else {
            _tmpDamId = _stmt.getText(_columnIndexOfDamId)
          }
          val _tmpClutchName: String?
          if (_stmt.isNull(_columnIndexOfClutchName)) {
            _tmpClutchName = null
          } else {
            _tmpClutchName = _stmt.getText(_columnIndexOfClutchName)
          }
          val _tmpClutchNumber: Int?
          if (_stmt.isNull(_columnIndexOfClutchNumber)) {
            _tmpClutchNumber = null
          } else {
            _tmpClutchNumber = _stmt.getLong(_columnIndexOfClutchNumber).toInt()
          }
          val _tmpEggsCollected: Int
          _tmpEggsCollected = _stmt.getLong(_columnIndexOfEggsCollected).toInt()
          val _tmpCollectionStartDate: Long
          _tmpCollectionStartDate = _stmt.getLong(_columnIndexOfCollectionStartDate)
          val _tmpCollectionEndDate: Long?
          if (_stmt.isNull(_columnIndexOfCollectionEndDate)) {
            _tmpCollectionEndDate = null
          } else {
            _tmpCollectionEndDate = _stmt.getLong(_columnIndexOfCollectionEndDate)
          }
          val _tmpSetDate: Long?
          if (_stmt.isNull(_columnIndexOfSetDate)) {
            _tmpSetDate = null
          } else {
            _tmpSetDate = _stmt.getLong(_columnIndexOfSetDate)
          }
          val _tmpEggsSet: Int
          _tmpEggsSet = _stmt.getLong(_columnIndexOfEggsSet).toInt()
          val _tmpIncubatorId: String?
          if (_stmt.isNull(_columnIndexOfIncubatorId)) {
            _tmpIncubatorId = null
          } else {
            _tmpIncubatorId = _stmt.getText(_columnIndexOfIncubatorId)
          }
          val _tmpIncubationNotes: String?
          if (_stmt.isNull(_columnIndexOfIncubationNotes)) {
            _tmpIncubationNotes = null
          } else {
            _tmpIncubationNotes = _stmt.getText(_columnIndexOfIncubationNotes)
          }
          val _tmpFirstCandleDate: Long?
          if (_stmt.isNull(_columnIndexOfFirstCandleDate)) {
            _tmpFirstCandleDate = null
          } else {
            _tmpFirstCandleDate = _stmt.getLong(_columnIndexOfFirstCandleDate)
          }
          val _tmpFirstCandleFertile: Int
          _tmpFirstCandleFertile = _stmt.getLong(_columnIndexOfFirstCandleFertile).toInt()
          val _tmpFirstCandleClear: Int
          _tmpFirstCandleClear = _stmt.getLong(_columnIndexOfFirstCandleClear).toInt()
          val _tmpFirstCandleEarlyDead: Int
          _tmpFirstCandleEarlyDead = _stmt.getLong(_columnIndexOfFirstCandleEarlyDead).toInt()
          val _tmpSecondCandleDate: Long?
          if (_stmt.isNull(_columnIndexOfSecondCandleDate)) {
            _tmpSecondCandleDate = null
          } else {
            _tmpSecondCandleDate = _stmt.getLong(_columnIndexOfSecondCandleDate)
          }
          val _tmpSecondCandleAlive: Int
          _tmpSecondCandleAlive = _stmt.getLong(_columnIndexOfSecondCandleAlive).toInt()
          val _tmpSecondCandleDead: Int
          _tmpSecondCandleDead = _stmt.getLong(_columnIndexOfSecondCandleDead).toInt()
          val _tmpExpectedHatchDate: Long?
          if (_stmt.isNull(_columnIndexOfExpectedHatchDate)) {
            _tmpExpectedHatchDate = null
          } else {
            _tmpExpectedHatchDate = _stmt.getLong(_columnIndexOfExpectedHatchDate)
          }
          val _tmpActualHatchStartDate: Long?
          if (_stmt.isNull(_columnIndexOfActualHatchStartDate)) {
            _tmpActualHatchStartDate = null
          } else {
            _tmpActualHatchStartDate = _stmt.getLong(_columnIndexOfActualHatchStartDate)
          }
          val _tmpActualHatchEndDate: Long?
          if (_stmt.isNull(_columnIndexOfActualHatchEndDate)) {
            _tmpActualHatchEndDate = null
          } else {
            _tmpActualHatchEndDate = _stmt.getLong(_columnIndexOfActualHatchEndDate)
          }
          val _tmpChicksHatched: Int
          _tmpChicksHatched = _stmt.getLong(_columnIndexOfChicksHatched).toInt()
          val _tmpChicksMale: Int
          _tmpChicksMale = _stmt.getLong(_columnIndexOfChicksMale).toInt()
          val _tmpChicksFemale: Int
          _tmpChicksFemale = _stmt.getLong(_columnIndexOfChicksFemale).toInt()
          val _tmpChicksUnsexed: Int
          _tmpChicksUnsexed = _stmt.getLong(_columnIndexOfChicksUnsexed).toInt()
          val _tmpDeadInShell: Int
          _tmpDeadInShell = _stmt.getLong(_columnIndexOfDeadInShell).toInt()
          val _tmpPippedNotHatched: Int
          _tmpPippedNotHatched = _stmt.getLong(_columnIndexOfPippedNotHatched).toInt()
          val _tmpAverageChickWeight: Double?
          if (_stmt.isNull(_columnIndexOfAverageChickWeight)) {
            _tmpAverageChickWeight = null
          } else {
            _tmpAverageChickWeight = _stmt.getDouble(_columnIndexOfAverageChickWeight)
          }
          val _tmpChickQualityScore: Int?
          if (_stmt.isNull(_columnIndexOfChickQualityScore)) {
            _tmpChickQualityScore = null
          } else {
            _tmpChickQualityScore = _stmt.getLong(_columnIndexOfChickQualityScore).toInt()
          }
          val _tmpQualityNotes: String?
          if (_stmt.isNull(_columnIndexOfQualityNotes)) {
            _tmpQualityNotes = null
          } else {
            _tmpQualityNotes = _stmt.getText(_columnIndexOfQualityNotes)
          }
          val _tmpFertilityRate: Double?
          if (_stmt.isNull(_columnIndexOfFertilityRate)) {
            _tmpFertilityRate = null
          } else {
            _tmpFertilityRate = _stmt.getDouble(_columnIndexOfFertilityRate)
          }
          val _tmpHatchabilityOfFertile: Double?
          if (_stmt.isNull(_columnIndexOfHatchabilityOfFertile)) {
            _tmpHatchabilityOfFertile = null
          } else {
            _tmpHatchabilityOfFertile = _stmt.getDouble(_columnIndexOfHatchabilityOfFertile)
          }
          val _tmpHatchabilityOfSet: Double?
          if (_stmt.isNull(_columnIndexOfHatchabilityOfSet)) {
            _tmpHatchabilityOfSet = null
          } else {
            _tmpHatchabilityOfSet = _stmt.getDouble(_columnIndexOfHatchabilityOfSet)
          }
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpOffspringIdsJson: String?
          if (_stmt.isNull(_columnIndexOfOffspringIdsJson)) {
            _tmpOffspringIdsJson = null
          } else {
            _tmpOffspringIdsJson = _stmt.getText(_columnIndexOfOffspringIdsJson)
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
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp != 0
          val _tmpSyncedAt: Long?
          if (_stmt.isNull(_columnIndexOfSyncedAt)) {
            _tmpSyncedAt = null
          } else {
            _tmpSyncedAt = _stmt.getLong(_columnIndexOfSyncedAt)
          }
          _item =
              ClutchEntity(_tmpClutchId,_tmpBreedingPairId,_tmpFarmerId,_tmpSireId,_tmpDamId,_tmpClutchName,_tmpClutchNumber,_tmpEggsCollected,_tmpCollectionStartDate,_tmpCollectionEndDate,_tmpSetDate,_tmpEggsSet,_tmpIncubatorId,_tmpIncubationNotes,_tmpFirstCandleDate,_tmpFirstCandleFertile,_tmpFirstCandleClear,_tmpFirstCandleEarlyDead,_tmpSecondCandleDate,_tmpSecondCandleAlive,_tmpSecondCandleDead,_tmpExpectedHatchDate,_tmpActualHatchStartDate,_tmpActualHatchEndDate,_tmpChicksHatched,_tmpChicksMale,_tmpChicksFemale,_tmpChicksUnsexed,_tmpDeadInShell,_tmpPippedNotHatched,_tmpAverageChickWeight,_tmpChickQualityScore,_tmpQualityNotes,_tmpFertilityRate,_tmpHatchabilityOfFertile,_tmpHatchabilityOfSet,_tmpStatus,_tmpOffspringIdsJson,_tmpNotes,_tmpMediaUrlsJson,_tmpCreatedAt,_tmpUpdatedAt,_tmpDirty,_tmpSyncedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeActive(farmerId: String): Flow<List<ClutchEntity>> {
    val _sql: String = """
        |
        |        SELECT * FROM clutches 
        |        WHERE farmerId = ? 
        |        AND status IN ('COLLECTING', 'SET', 'INCUBATING', 'HATCHING') 
        |        ORDER BY expectedHatchDate ASC
        |    
        """.trimMargin()
    return createFlow(__db, false, arrayOf("clutches")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, farmerId)
        val _columnIndexOfClutchId: Int = getColumnIndexOrThrow(_stmt, "clutchId")
        val _columnIndexOfBreedingPairId: Int = getColumnIndexOrThrow(_stmt, "breedingPairId")
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfSireId: Int = getColumnIndexOrThrow(_stmt, "sireId")
        val _columnIndexOfDamId: Int = getColumnIndexOrThrow(_stmt, "damId")
        val _columnIndexOfClutchName: Int = getColumnIndexOrThrow(_stmt, "clutchName")
        val _columnIndexOfClutchNumber: Int = getColumnIndexOrThrow(_stmt, "clutchNumber")
        val _columnIndexOfEggsCollected: Int = getColumnIndexOrThrow(_stmt, "eggsCollected")
        val _columnIndexOfCollectionStartDate: Int = getColumnIndexOrThrow(_stmt,
            "collectionStartDate")
        val _columnIndexOfCollectionEndDate: Int = getColumnIndexOrThrow(_stmt, "collectionEndDate")
        val _columnIndexOfSetDate: Int = getColumnIndexOrThrow(_stmt, "setDate")
        val _columnIndexOfEggsSet: Int = getColumnIndexOrThrow(_stmt, "eggsSet")
        val _columnIndexOfIncubatorId: Int = getColumnIndexOrThrow(_stmt, "incubatorId")
        val _columnIndexOfIncubationNotes: Int = getColumnIndexOrThrow(_stmt, "incubationNotes")
        val _columnIndexOfFirstCandleDate: Int = getColumnIndexOrThrow(_stmt, "firstCandleDate")
        val _columnIndexOfFirstCandleFertile: Int = getColumnIndexOrThrow(_stmt,
            "firstCandleFertile")
        val _columnIndexOfFirstCandleClear: Int = getColumnIndexOrThrow(_stmt, "firstCandleClear")
        val _columnIndexOfFirstCandleEarlyDead: Int = getColumnIndexOrThrow(_stmt,
            "firstCandleEarlyDead")
        val _columnIndexOfSecondCandleDate: Int = getColumnIndexOrThrow(_stmt, "secondCandleDate")
        val _columnIndexOfSecondCandleAlive: Int = getColumnIndexOrThrow(_stmt, "secondCandleAlive")
        val _columnIndexOfSecondCandleDead: Int = getColumnIndexOrThrow(_stmt, "secondCandleDead")
        val _columnIndexOfExpectedHatchDate: Int = getColumnIndexOrThrow(_stmt, "expectedHatchDate")
        val _columnIndexOfActualHatchStartDate: Int = getColumnIndexOrThrow(_stmt,
            "actualHatchStartDate")
        val _columnIndexOfActualHatchEndDate: Int = getColumnIndexOrThrow(_stmt,
            "actualHatchEndDate")
        val _columnIndexOfChicksHatched: Int = getColumnIndexOrThrow(_stmt, "chicksHatched")
        val _columnIndexOfChicksMale: Int = getColumnIndexOrThrow(_stmt, "chicksMale")
        val _columnIndexOfChicksFemale: Int = getColumnIndexOrThrow(_stmt, "chicksFemale")
        val _columnIndexOfChicksUnsexed: Int = getColumnIndexOrThrow(_stmt, "chicksUnsexed")
        val _columnIndexOfDeadInShell: Int = getColumnIndexOrThrow(_stmt, "deadInShell")
        val _columnIndexOfPippedNotHatched: Int = getColumnIndexOrThrow(_stmt, "pippedNotHatched")
        val _columnIndexOfAverageChickWeight: Int = getColumnIndexOrThrow(_stmt,
            "averageChickWeight")
        val _columnIndexOfChickQualityScore: Int = getColumnIndexOrThrow(_stmt, "chickQualityScore")
        val _columnIndexOfQualityNotes: Int = getColumnIndexOrThrow(_stmt, "qualityNotes")
        val _columnIndexOfFertilityRate: Int = getColumnIndexOrThrow(_stmt, "fertilityRate")
        val _columnIndexOfHatchabilityOfFertile: Int = getColumnIndexOrThrow(_stmt,
            "hatchabilityOfFertile")
        val _columnIndexOfHatchabilityOfSet: Int = getColumnIndexOrThrow(_stmt, "hatchabilityOfSet")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfOffspringIdsJson: Int = getColumnIndexOrThrow(_stmt, "offspringIdsJson")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfMediaUrlsJson: Int = getColumnIndexOrThrow(_stmt, "mediaUrlsJson")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _result: MutableList<ClutchEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: ClutchEntity
          val _tmpClutchId: String
          _tmpClutchId = _stmt.getText(_columnIndexOfClutchId)
          val _tmpBreedingPairId: String?
          if (_stmt.isNull(_columnIndexOfBreedingPairId)) {
            _tmpBreedingPairId = null
          } else {
            _tmpBreedingPairId = _stmt.getText(_columnIndexOfBreedingPairId)
          }
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpSireId: String?
          if (_stmt.isNull(_columnIndexOfSireId)) {
            _tmpSireId = null
          } else {
            _tmpSireId = _stmt.getText(_columnIndexOfSireId)
          }
          val _tmpDamId: String?
          if (_stmt.isNull(_columnIndexOfDamId)) {
            _tmpDamId = null
          } else {
            _tmpDamId = _stmt.getText(_columnIndexOfDamId)
          }
          val _tmpClutchName: String?
          if (_stmt.isNull(_columnIndexOfClutchName)) {
            _tmpClutchName = null
          } else {
            _tmpClutchName = _stmt.getText(_columnIndexOfClutchName)
          }
          val _tmpClutchNumber: Int?
          if (_stmt.isNull(_columnIndexOfClutchNumber)) {
            _tmpClutchNumber = null
          } else {
            _tmpClutchNumber = _stmt.getLong(_columnIndexOfClutchNumber).toInt()
          }
          val _tmpEggsCollected: Int
          _tmpEggsCollected = _stmt.getLong(_columnIndexOfEggsCollected).toInt()
          val _tmpCollectionStartDate: Long
          _tmpCollectionStartDate = _stmt.getLong(_columnIndexOfCollectionStartDate)
          val _tmpCollectionEndDate: Long?
          if (_stmt.isNull(_columnIndexOfCollectionEndDate)) {
            _tmpCollectionEndDate = null
          } else {
            _tmpCollectionEndDate = _stmt.getLong(_columnIndexOfCollectionEndDate)
          }
          val _tmpSetDate: Long?
          if (_stmt.isNull(_columnIndexOfSetDate)) {
            _tmpSetDate = null
          } else {
            _tmpSetDate = _stmt.getLong(_columnIndexOfSetDate)
          }
          val _tmpEggsSet: Int
          _tmpEggsSet = _stmt.getLong(_columnIndexOfEggsSet).toInt()
          val _tmpIncubatorId: String?
          if (_stmt.isNull(_columnIndexOfIncubatorId)) {
            _tmpIncubatorId = null
          } else {
            _tmpIncubatorId = _stmt.getText(_columnIndexOfIncubatorId)
          }
          val _tmpIncubationNotes: String?
          if (_stmt.isNull(_columnIndexOfIncubationNotes)) {
            _tmpIncubationNotes = null
          } else {
            _tmpIncubationNotes = _stmt.getText(_columnIndexOfIncubationNotes)
          }
          val _tmpFirstCandleDate: Long?
          if (_stmt.isNull(_columnIndexOfFirstCandleDate)) {
            _tmpFirstCandleDate = null
          } else {
            _tmpFirstCandleDate = _stmt.getLong(_columnIndexOfFirstCandleDate)
          }
          val _tmpFirstCandleFertile: Int
          _tmpFirstCandleFertile = _stmt.getLong(_columnIndexOfFirstCandleFertile).toInt()
          val _tmpFirstCandleClear: Int
          _tmpFirstCandleClear = _stmt.getLong(_columnIndexOfFirstCandleClear).toInt()
          val _tmpFirstCandleEarlyDead: Int
          _tmpFirstCandleEarlyDead = _stmt.getLong(_columnIndexOfFirstCandleEarlyDead).toInt()
          val _tmpSecondCandleDate: Long?
          if (_stmt.isNull(_columnIndexOfSecondCandleDate)) {
            _tmpSecondCandleDate = null
          } else {
            _tmpSecondCandleDate = _stmt.getLong(_columnIndexOfSecondCandleDate)
          }
          val _tmpSecondCandleAlive: Int
          _tmpSecondCandleAlive = _stmt.getLong(_columnIndexOfSecondCandleAlive).toInt()
          val _tmpSecondCandleDead: Int
          _tmpSecondCandleDead = _stmt.getLong(_columnIndexOfSecondCandleDead).toInt()
          val _tmpExpectedHatchDate: Long?
          if (_stmt.isNull(_columnIndexOfExpectedHatchDate)) {
            _tmpExpectedHatchDate = null
          } else {
            _tmpExpectedHatchDate = _stmt.getLong(_columnIndexOfExpectedHatchDate)
          }
          val _tmpActualHatchStartDate: Long?
          if (_stmt.isNull(_columnIndexOfActualHatchStartDate)) {
            _tmpActualHatchStartDate = null
          } else {
            _tmpActualHatchStartDate = _stmt.getLong(_columnIndexOfActualHatchStartDate)
          }
          val _tmpActualHatchEndDate: Long?
          if (_stmt.isNull(_columnIndexOfActualHatchEndDate)) {
            _tmpActualHatchEndDate = null
          } else {
            _tmpActualHatchEndDate = _stmt.getLong(_columnIndexOfActualHatchEndDate)
          }
          val _tmpChicksHatched: Int
          _tmpChicksHatched = _stmt.getLong(_columnIndexOfChicksHatched).toInt()
          val _tmpChicksMale: Int
          _tmpChicksMale = _stmt.getLong(_columnIndexOfChicksMale).toInt()
          val _tmpChicksFemale: Int
          _tmpChicksFemale = _stmt.getLong(_columnIndexOfChicksFemale).toInt()
          val _tmpChicksUnsexed: Int
          _tmpChicksUnsexed = _stmt.getLong(_columnIndexOfChicksUnsexed).toInt()
          val _tmpDeadInShell: Int
          _tmpDeadInShell = _stmt.getLong(_columnIndexOfDeadInShell).toInt()
          val _tmpPippedNotHatched: Int
          _tmpPippedNotHatched = _stmt.getLong(_columnIndexOfPippedNotHatched).toInt()
          val _tmpAverageChickWeight: Double?
          if (_stmt.isNull(_columnIndexOfAverageChickWeight)) {
            _tmpAverageChickWeight = null
          } else {
            _tmpAverageChickWeight = _stmt.getDouble(_columnIndexOfAverageChickWeight)
          }
          val _tmpChickQualityScore: Int?
          if (_stmt.isNull(_columnIndexOfChickQualityScore)) {
            _tmpChickQualityScore = null
          } else {
            _tmpChickQualityScore = _stmt.getLong(_columnIndexOfChickQualityScore).toInt()
          }
          val _tmpQualityNotes: String?
          if (_stmt.isNull(_columnIndexOfQualityNotes)) {
            _tmpQualityNotes = null
          } else {
            _tmpQualityNotes = _stmt.getText(_columnIndexOfQualityNotes)
          }
          val _tmpFertilityRate: Double?
          if (_stmt.isNull(_columnIndexOfFertilityRate)) {
            _tmpFertilityRate = null
          } else {
            _tmpFertilityRate = _stmt.getDouble(_columnIndexOfFertilityRate)
          }
          val _tmpHatchabilityOfFertile: Double?
          if (_stmt.isNull(_columnIndexOfHatchabilityOfFertile)) {
            _tmpHatchabilityOfFertile = null
          } else {
            _tmpHatchabilityOfFertile = _stmt.getDouble(_columnIndexOfHatchabilityOfFertile)
          }
          val _tmpHatchabilityOfSet: Double?
          if (_stmt.isNull(_columnIndexOfHatchabilityOfSet)) {
            _tmpHatchabilityOfSet = null
          } else {
            _tmpHatchabilityOfSet = _stmt.getDouble(_columnIndexOfHatchabilityOfSet)
          }
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpOffspringIdsJson: String?
          if (_stmt.isNull(_columnIndexOfOffspringIdsJson)) {
            _tmpOffspringIdsJson = null
          } else {
            _tmpOffspringIdsJson = _stmt.getText(_columnIndexOfOffspringIdsJson)
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
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp != 0
          val _tmpSyncedAt: Long?
          if (_stmt.isNull(_columnIndexOfSyncedAt)) {
            _tmpSyncedAt = null
          } else {
            _tmpSyncedAt = _stmt.getLong(_columnIndexOfSyncedAt)
          }
          _item =
              ClutchEntity(_tmpClutchId,_tmpBreedingPairId,_tmpFarmerId,_tmpSireId,_tmpDamId,_tmpClutchName,_tmpClutchNumber,_tmpEggsCollected,_tmpCollectionStartDate,_tmpCollectionEndDate,_tmpSetDate,_tmpEggsSet,_tmpIncubatorId,_tmpIncubationNotes,_tmpFirstCandleDate,_tmpFirstCandleFertile,_tmpFirstCandleClear,_tmpFirstCandleEarlyDead,_tmpSecondCandleDate,_tmpSecondCandleAlive,_tmpSecondCandleDead,_tmpExpectedHatchDate,_tmpActualHatchStartDate,_tmpActualHatchEndDate,_tmpChicksHatched,_tmpChicksMale,_tmpChicksFemale,_tmpChicksUnsexed,_tmpDeadInShell,_tmpPippedNotHatched,_tmpAverageChickWeight,_tmpChickQualityScore,_tmpQualityNotes,_tmpFertilityRate,_tmpHatchabilityOfFertile,_tmpHatchabilityOfSet,_tmpStatus,_tmpOffspringIdsJson,_tmpNotes,_tmpMediaUrlsJson,_tmpCreatedAt,_tmpUpdatedAt,_tmpDirty,_tmpSyncedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeUpcomingHatches(
    farmerId: String,
    now: Long,
    futureDate: Long,
  ): Flow<List<ClutchEntity>> {
    val _sql: String = """
        |
        |        SELECT * FROM clutches 
        |        WHERE farmerId = ? 
        |        AND status IN ('SET', 'INCUBATING') 
        |        AND expectedHatchDate IS NOT NULL 
        |        AND expectedHatchDate BETWEEN ? AND ? 
        |        ORDER BY expectedHatchDate ASC
        |    
        """.trimMargin()
    return createFlow(__db, false, arrayOf("clutches")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, farmerId)
        _argIndex = 2
        _stmt.bindLong(_argIndex, now)
        _argIndex = 3
        _stmt.bindLong(_argIndex, futureDate)
        val _columnIndexOfClutchId: Int = getColumnIndexOrThrow(_stmt, "clutchId")
        val _columnIndexOfBreedingPairId: Int = getColumnIndexOrThrow(_stmt, "breedingPairId")
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfSireId: Int = getColumnIndexOrThrow(_stmt, "sireId")
        val _columnIndexOfDamId: Int = getColumnIndexOrThrow(_stmt, "damId")
        val _columnIndexOfClutchName: Int = getColumnIndexOrThrow(_stmt, "clutchName")
        val _columnIndexOfClutchNumber: Int = getColumnIndexOrThrow(_stmt, "clutchNumber")
        val _columnIndexOfEggsCollected: Int = getColumnIndexOrThrow(_stmt, "eggsCollected")
        val _columnIndexOfCollectionStartDate: Int = getColumnIndexOrThrow(_stmt,
            "collectionStartDate")
        val _columnIndexOfCollectionEndDate: Int = getColumnIndexOrThrow(_stmt, "collectionEndDate")
        val _columnIndexOfSetDate: Int = getColumnIndexOrThrow(_stmt, "setDate")
        val _columnIndexOfEggsSet: Int = getColumnIndexOrThrow(_stmt, "eggsSet")
        val _columnIndexOfIncubatorId: Int = getColumnIndexOrThrow(_stmt, "incubatorId")
        val _columnIndexOfIncubationNotes: Int = getColumnIndexOrThrow(_stmt, "incubationNotes")
        val _columnIndexOfFirstCandleDate: Int = getColumnIndexOrThrow(_stmt, "firstCandleDate")
        val _columnIndexOfFirstCandleFertile: Int = getColumnIndexOrThrow(_stmt,
            "firstCandleFertile")
        val _columnIndexOfFirstCandleClear: Int = getColumnIndexOrThrow(_stmt, "firstCandleClear")
        val _columnIndexOfFirstCandleEarlyDead: Int = getColumnIndexOrThrow(_stmt,
            "firstCandleEarlyDead")
        val _columnIndexOfSecondCandleDate: Int = getColumnIndexOrThrow(_stmt, "secondCandleDate")
        val _columnIndexOfSecondCandleAlive: Int = getColumnIndexOrThrow(_stmt, "secondCandleAlive")
        val _columnIndexOfSecondCandleDead: Int = getColumnIndexOrThrow(_stmt, "secondCandleDead")
        val _columnIndexOfExpectedHatchDate: Int = getColumnIndexOrThrow(_stmt, "expectedHatchDate")
        val _columnIndexOfActualHatchStartDate: Int = getColumnIndexOrThrow(_stmt,
            "actualHatchStartDate")
        val _columnIndexOfActualHatchEndDate: Int = getColumnIndexOrThrow(_stmt,
            "actualHatchEndDate")
        val _columnIndexOfChicksHatched: Int = getColumnIndexOrThrow(_stmt, "chicksHatched")
        val _columnIndexOfChicksMale: Int = getColumnIndexOrThrow(_stmt, "chicksMale")
        val _columnIndexOfChicksFemale: Int = getColumnIndexOrThrow(_stmt, "chicksFemale")
        val _columnIndexOfChicksUnsexed: Int = getColumnIndexOrThrow(_stmt, "chicksUnsexed")
        val _columnIndexOfDeadInShell: Int = getColumnIndexOrThrow(_stmt, "deadInShell")
        val _columnIndexOfPippedNotHatched: Int = getColumnIndexOrThrow(_stmt, "pippedNotHatched")
        val _columnIndexOfAverageChickWeight: Int = getColumnIndexOrThrow(_stmt,
            "averageChickWeight")
        val _columnIndexOfChickQualityScore: Int = getColumnIndexOrThrow(_stmt, "chickQualityScore")
        val _columnIndexOfQualityNotes: Int = getColumnIndexOrThrow(_stmt, "qualityNotes")
        val _columnIndexOfFertilityRate: Int = getColumnIndexOrThrow(_stmt, "fertilityRate")
        val _columnIndexOfHatchabilityOfFertile: Int = getColumnIndexOrThrow(_stmt,
            "hatchabilityOfFertile")
        val _columnIndexOfHatchabilityOfSet: Int = getColumnIndexOrThrow(_stmt, "hatchabilityOfSet")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfOffspringIdsJson: Int = getColumnIndexOrThrow(_stmt, "offspringIdsJson")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfMediaUrlsJson: Int = getColumnIndexOrThrow(_stmt, "mediaUrlsJson")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _result: MutableList<ClutchEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: ClutchEntity
          val _tmpClutchId: String
          _tmpClutchId = _stmt.getText(_columnIndexOfClutchId)
          val _tmpBreedingPairId: String?
          if (_stmt.isNull(_columnIndexOfBreedingPairId)) {
            _tmpBreedingPairId = null
          } else {
            _tmpBreedingPairId = _stmt.getText(_columnIndexOfBreedingPairId)
          }
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpSireId: String?
          if (_stmt.isNull(_columnIndexOfSireId)) {
            _tmpSireId = null
          } else {
            _tmpSireId = _stmt.getText(_columnIndexOfSireId)
          }
          val _tmpDamId: String?
          if (_stmt.isNull(_columnIndexOfDamId)) {
            _tmpDamId = null
          } else {
            _tmpDamId = _stmt.getText(_columnIndexOfDamId)
          }
          val _tmpClutchName: String?
          if (_stmt.isNull(_columnIndexOfClutchName)) {
            _tmpClutchName = null
          } else {
            _tmpClutchName = _stmt.getText(_columnIndexOfClutchName)
          }
          val _tmpClutchNumber: Int?
          if (_stmt.isNull(_columnIndexOfClutchNumber)) {
            _tmpClutchNumber = null
          } else {
            _tmpClutchNumber = _stmt.getLong(_columnIndexOfClutchNumber).toInt()
          }
          val _tmpEggsCollected: Int
          _tmpEggsCollected = _stmt.getLong(_columnIndexOfEggsCollected).toInt()
          val _tmpCollectionStartDate: Long
          _tmpCollectionStartDate = _stmt.getLong(_columnIndexOfCollectionStartDate)
          val _tmpCollectionEndDate: Long?
          if (_stmt.isNull(_columnIndexOfCollectionEndDate)) {
            _tmpCollectionEndDate = null
          } else {
            _tmpCollectionEndDate = _stmt.getLong(_columnIndexOfCollectionEndDate)
          }
          val _tmpSetDate: Long?
          if (_stmt.isNull(_columnIndexOfSetDate)) {
            _tmpSetDate = null
          } else {
            _tmpSetDate = _stmt.getLong(_columnIndexOfSetDate)
          }
          val _tmpEggsSet: Int
          _tmpEggsSet = _stmt.getLong(_columnIndexOfEggsSet).toInt()
          val _tmpIncubatorId: String?
          if (_stmt.isNull(_columnIndexOfIncubatorId)) {
            _tmpIncubatorId = null
          } else {
            _tmpIncubatorId = _stmt.getText(_columnIndexOfIncubatorId)
          }
          val _tmpIncubationNotes: String?
          if (_stmt.isNull(_columnIndexOfIncubationNotes)) {
            _tmpIncubationNotes = null
          } else {
            _tmpIncubationNotes = _stmt.getText(_columnIndexOfIncubationNotes)
          }
          val _tmpFirstCandleDate: Long?
          if (_stmt.isNull(_columnIndexOfFirstCandleDate)) {
            _tmpFirstCandleDate = null
          } else {
            _tmpFirstCandleDate = _stmt.getLong(_columnIndexOfFirstCandleDate)
          }
          val _tmpFirstCandleFertile: Int
          _tmpFirstCandleFertile = _stmt.getLong(_columnIndexOfFirstCandleFertile).toInt()
          val _tmpFirstCandleClear: Int
          _tmpFirstCandleClear = _stmt.getLong(_columnIndexOfFirstCandleClear).toInt()
          val _tmpFirstCandleEarlyDead: Int
          _tmpFirstCandleEarlyDead = _stmt.getLong(_columnIndexOfFirstCandleEarlyDead).toInt()
          val _tmpSecondCandleDate: Long?
          if (_stmt.isNull(_columnIndexOfSecondCandleDate)) {
            _tmpSecondCandleDate = null
          } else {
            _tmpSecondCandleDate = _stmt.getLong(_columnIndexOfSecondCandleDate)
          }
          val _tmpSecondCandleAlive: Int
          _tmpSecondCandleAlive = _stmt.getLong(_columnIndexOfSecondCandleAlive).toInt()
          val _tmpSecondCandleDead: Int
          _tmpSecondCandleDead = _stmt.getLong(_columnIndexOfSecondCandleDead).toInt()
          val _tmpExpectedHatchDate: Long?
          if (_stmt.isNull(_columnIndexOfExpectedHatchDate)) {
            _tmpExpectedHatchDate = null
          } else {
            _tmpExpectedHatchDate = _stmt.getLong(_columnIndexOfExpectedHatchDate)
          }
          val _tmpActualHatchStartDate: Long?
          if (_stmt.isNull(_columnIndexOfActualHatchStartDate)) {
            _tmpActualHatchStartDate = null
          } else {
            _tmpActualHatchStartDate = _stmt.getLong(_columnIndexOfActualHatchStartDate)
          }
          val _tmpActualHatchEndDate: Long?
          if (_stmt.isNull(_columnIndexOfActualHatchEndDate)) {
            _tmpActualHatchEndDate = null
          } else {
            _tmpActualHatchEndDate = _stmt.getLong(_columnIndexOfActualHatchEndDate)
          }
          val _tmpChicksHatched: Int
          _tmpChicksHatched = _stmt.getLong(_columnIndexOfChicksHatched).toInt()
          val _tmpChicksMale: Int
          _tmpChicksMale = _stmt.getLong(_columnIndexOfChicksMale).toInt()
          val _tmpChicksFemale: Int
          _tmpChicksFemale = _stmt.getLong(_columnIndexOfChicksFemale).toInt()
          val _tmpChicksUnsexed: Int
          _tmpChicksUnsexed = _stmt.getLong(_columnIndexOfChicksUnsexed).toInt()
          val _tmpDeadInShell: Int
          _tmpDeadInShell = _stmt.getLong(_columnIndexOfDeadInShell).toInt()
          val _tmpPippedNotHatched: Int
          _tmpPippedNotHatched = _stmt.getLong(_columnIndexOfPippedNotHatched).toInt()
          val _tmpAverageChickWeight: Double?
          if (_stmt.isNull(_columnIndexOfAverageChickWeight)) {
            _tmpAverageChickWeight = null
          } else {
            _tmpAverageChickWeight = _stmt.getDouble(_columnIndexOfAverageChickWeight)
          }
          val _tmpChickQualityScore: Int?
          if (_stmt.isNull(_columnIndexOfChickQualityScore)) {
            _tmpChickQualityScore = null
          } else {
            _tmpChickQualityScore = _stmt.getLong(_columnIndexOfChickQualityScore).toInt()
          }
          val _tmpQualityNotes: String?
          if (_stmt.isNull(_columnIndexOfQualityNotes)) {
            _tmpQualityNotes = null
          } else {
            _tmpQualityNotes = _stmt.getText(_columnIndexOfQualityNotes)
          }
          val _tmpFertilityRate: Double?
          if (_stmt.isNull(_columnIndexOfFertilityRate)) {
            _tmpFertilityRate = null
          } else {
            _tmpFertilityRate = _stmt.getDouble(_columnIndexOfFertilityRate)
          }
          val _tmpHatchabilityOfFertile: Double?
          if (_stmt.isNull(_columnIndexOfHatchabilityOfFertile)) {
            _tmpHatchabilityOfFertile = null
          } else {
            _tmpHatchabilityOfFertile = _stmt.getDouble(_columnIndexOfHatchabilityOfFertile)
          }
          val _tmpHatchabilityOfSet: Double?
          if (_stmt.isNull(_columnIndexOfHatchabilityOfSet)) {
            _tmpHatchabilityOfSet = null
          } else {
            _tmpHatchabilityOfSet = _stmt.getDouble(_columnIndexOfHatchabilityOfSet)
          }
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpOffspringIdsJson: String?
          if (_stmt.isNull(_columnIndexOfOffspringIdsJson)) {
            _tmpOffspringIdsJson = null
          } else {
            _tmpOffspringIdsJson = _stmt.getText(_columnIndexOfOffspringIdsJson)
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
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp != 0
          val _tmpSyncedAt: Long?
          if (_stmt.isNull(_columnIndexOfSyncedAt)) {
            _tmpSyncedAt = null
          } else {
            _tmpSyncedAt = _stmt.getLong(_columnIndexOfSyncedAt)
          }
          _item =
              ClutchEntity(_tmpClutchId,_tmpBreedingPairId,_tmpFarmerId,_tmpSireId,_tmpDamId,_tmpClutchName,_tmpClutchNumber,_tmpEggsCollected,_tmpCollectionStartDate,_tmpCollectionEndDate,_tmpSetDate,_tmpEggsSet,_tmpIncubatorId,_tmpIncubationNotes,_tmpFirstCandleDate,_tmpFirstCandleFertile,_tmpFirstCandleClear,_tmpFirstCandleEarlyDead,_tmpSecondCandleDate,_tmpSecondCandleAlive,_tmpSecondCandleDead,_tmpExpectedHatchDate,_tmpActualHatchStartDate,_tmpActualHatchEndDate,_tmpChicksHatched,_tmpChicksMale,_tmpChicksFemale,_tmpChicksUnsexed,_tmpDeadInShell,_tmpPippedNotHatched,_tmpAverageChickWeight,_tmpChickQualityScore,_tmpQualityNotes,_tmpFertilityRate,_tmpHatchabilityOfFertile,_tmpHatchabilityOfSet,_tmpStatus,_tmpOffspringIdsJson,_tmpNotes,_tmpMediaUrlsJson,_tmpCreatedAt,_tmpUpdatedAt,_tmpDirty,_tmpSyncedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getRecentCompleted(farmerId: String, limit: Int): List<ClutchEntity> {
    val _sql: String = """
        |
        |        SELECT * FROM clutches 
        |        WHERE farmerId = ? 
        |        AND status = 'COMPLETE' 
        |        ORDER BY actualHatchEndDate DESC 
        |        LIMIT ?
        |    
        """.trimMargin()
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, farmerId)
        _argIndex = 2
        _stmt.bindLong(_argIndex, limit.toLong())
        val _columnIndexOfClutchId: Int = getColumnIndexOrThrow(_stmt, "clutchId")
        val _columnIndexOfBreedingPairId: Int = getColumnIndexOrThrow(_stmt, "breedingPairId")
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfSireId: Int = getColumnIndexOrThrow(_stmt, "sireId")
        val _columnIndexOfDamId: Int = getColumnIndexOrThrow(_stmt, "damId")
        val _columnIndexOfClutchName: Int = getColumnIndexOrThrow(_stmt, "clutchName")
        val _columnIndexOfClutchNumber: Int = getColumnIndexOrThrow(_stmt, "clutchNumber")
        val _columnIndexOfEggsCollected: Int = getColumnIndexOrThrow(_stmt, "eggsCollected")
        val _columnIndexOfCollectionStartDate: Int = getColumnIndexOrThrow(_stmt,
            "collectionStartDate")
        val _columnIndexOfCollectionEndDate: Int = getColumnIndexOrThrow(_stmt, "collectionEndDate")
        val _columnIndexOfSetDate: Int = getColumnIndexOrThrow(_stmt, "setDate")
        val _columnIndexOfEggsSet: Int = getColumnIndexOrThrow(_stmt, "eggsSet")
        val _columnIndexOfIncubatorId: Int = getColumnIndexOrThrow(_stmt, "incubatorId")
        val _columnIndexOfIncubationNotes: Int = getColumnIndexOrThrow(_stmt, "incubationNotes")
        val _columnIndexOfFirstCandleDate: Int = getColumnIndexOrThrow(_stmt, "firstCandleDate")
        val _columnIndexOfFirstCandleFertile: Int = getColumnIndexOrThrow(_stmt,
            "firstCandleFertile")
        val _columnIndexOfFirstCandleClear: Int = getColumnIndexOrThrow(_stmt, "firstCandleClear")
        val _columnIndexOfFirstCandleEarlyDead: Int = getColumnIndexOrThrow(_stmt,
            "firstCandleEarlyDead")
        val _columnIndexOfSecondCandleDate: Int = getColumnIndexOrThrow(_stmt, "secondCandleDate")
        val _columnIndexOfSecondCandleAlive: Int = getColumnIndexOrThrow(_stmt, "secondCandleAlive")
        val _columnIndexOfSecondCandleDead: Int = getColumnIndexOrThrow(_stmt, "secondCandleDead")
        val _columnIndexOfExpectedHatchDate: Int = getColumnIndexOrThrow(_stmt, "expectedHatchDate")
        val _columnIndexOfActualHatchStartDate: Int = getColumnIndexOrThrow(_stmt,
            "actualHatchStartDate")
        val _columnIndexOfActualHatchEndDate: Int = getColumnIndexOrThrow(_stmt,
            "actualHatchEndDate")
        val _columnIndexOfChicksHatched: Int = getColumnIndexOrThrow(_stmt, "chicksHatched")
        val _columnIndexOfChicksMale: Int = getColumnIndexOrThrow(_stmt, "chicksMale")
        val _columnIndexOfChicksFemale: Int = getColumnIndexOrThrow(_stmt, "chicksFemale")
        val _columnIndexOfChicksUnsexed: Int = getColumnIndexOrThrow(_stmt, "chicksUnsexed")
        val _columnIndexOfDeadInShell: Int = getColumnIndexOrThrow(_stmt, "deadInShell")
        val _columnIndexOfPippedNotHatched: Int = getColumnIndexOrThrow(_stmt, "pippedNotHatched")
        val _columnIndexOfAverageChickWeight: Int = getColumnIndexOrThrow(_stmt,
            "averageChickWeight")
        val _columnIndexOfChickQualityScore: Int = getColumnIndexOrThrow(_stmt, "chickQualityScore")
        val _columnIndexOfQualityNotes: Int = getColumnIndexOrThrow(_stmt, "qualityNotes")
        val _columnIndexOfFertilityRate: Int = getColumnIndexOrThrow(_stmt, "fertilityRate")
        val _columnIndexOfHatchabilityOfFertile: Int = getColumnIndexOrThrow(_stmt,
            "hatchabilityOfFertile")
        val _columnIndexOfHatchabilityOfSet: Int = getColumnIndexOrThrow(_stmt, "hatchabilityOfSet")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfOffspringIdsJson: Int = getColumnIndexOrThrow(_stmt, "offspringIdsJson")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfMediaUrlsJson: Int = getColumnIndexOrThrow(_stmt, "mediaUrlsJson")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _result: MutableList<ClutchEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: ClutchEntity
          val _tmpClutchId: String
          _tmpClutchId = _stmt.getText(_columnIndexOfClutchId)
          val _tmpBreedingPairId: String?
          if (_stmt.isNull(_columnIndexOfBreedingPairId)) {
            _tmpBreedingPairId = null
          } else {
            _tmpBreedingPairId = _stmt.getText(_columnIndexOfBreedingPairId)
          }
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpSireId: String?
          if (_stmt.isNull(_columnIndexOfSireId)) {
            _tmpSireId = null
          } else {
            _tmpSireId = _stmt.getText(_columnIndexOfSireId)
          }
          val _tmpDamId: String?
          if (_stmt.isNull(_columnIndexOfDamId)) {
            _tmpDamId = null
          } else {
            _tmpDamId = _stmt.getText(_columnIndexOfDamId)
          }
          val _tmpClutchName: String?
          if (_stmt.isNull(_columnIndexOfClutchName)) {
            _tmpClutchName = null
          } else {
            _tmpClutchName = _stmt.getText(_columnIndexOfClutchName)
          }
          val _tmpClutchNumber: Int?
          if (_stmt.isNull(_columnIndexOfClutchNumber)) {
            _tmpClutchNumber = null
          } else {
            _tmpClutchNumber = _stmt.getLong(_columnIndexOfClutchNumber).toInt()
          }
          val _tmpEggsCollected: Int
          _tmpEggsCollected = _stmt.getLong(_columnIndexOfEggsCollected).toInt()
          val _tmpCollectionStartDate: Long
          _tmpCollectionStartDate = _stmt.getLong(_columnIndexOfCollectionStartDate)
          val _tmpCollectionEndDate: Long?
          if (_stmt.isNull(_columnIndexOfCollectionEndDate)) {
            _tmpCollectionEndDate = null
          } else {
            _tmpCollectionEndDate = _stmt.getLong(_columnIndexOfCollectionEndDate)
          }
          val _tmpSetDate: Long?
          if (_stmt.isNull(_columnIndexOfSetDate)) {
            _tmpSetDate = null
          } else {
            _tmpSetDate = _stmt.getLong(_columnIndexOfSetDate)
          }
          val _tmpEggsSet: Int
          _tmpEggsSet = _stmt.getLong(_columnIndexOfEggsSet).toInt()
          val _tmpIncubatorId: String?
          if (_stmt.isNull(_columnIndexOfIncubatorId)) {
            _tmpIncubatorId = null
          } else {
            _tmpIncubatorId = _stmt.getText(_columnIndexOfIncubatorId)
          }
          val _tmpIncubationNotes: String?
          if (_stmt.isNull(_columnIndexOfIncubationNotes)) {
            _tmpIncubationNotes = null
          } else {
            _tmpIncubationNotes = _stmt.getText(_columnIndexOfIncubationNotes)
          }
          val _tmpFirstCandleDate: Long?
          if (_stmt.isNull(_columnIndexOfFirstCandleDate)) {
            _tmpFirstCandleDate = null
          } else {
            _tmpFirstCandleDate = _stmt.getLong(_columnIndexOfFirstCandleDate)
          }
          val _tmpFirstCandleFertile: Int
          _tmpFirstCandleFertile = _stmt.getLong(_columnIndexOfFirstCandleFertile).toInt()
          val _tmpFirstCandleClear: Int
          _tmpFirstCandleClear = _stmt.getLong(_columnIndexOfFirstCandleClear).toInt()
          val _tmpFirstCandleEarlyDead: Int
          _tmpFirstCandleEarlyDead = _stmt.getLong(_columnIndexOfFirstCandleEarlyDead).toInt()
          val _tmpSecondCandleDate: Long?
          if (_stmt.isNull(_columnIndexOfSecondCandleDate)) {
            _tmpSecondCandleDate = null
          } else {
            _tmpSecondCandleDate = _stmt.getLong(_columnIndexOfSecondCandleDate)
          }
          val _tmpSecondCandleAlive: Int
          _tmpSecondCandleAlive = _stmt.getLong(_columnIndexOfSecondCandleAlive).toInt()
          val _tmpSecondCandleDead: Int
          _tmpSecondCandleDead = _stmt.getLong(_columnIndexOfSecondCandleDead).toInt()
          val _tmpExpectedHatchDate: Long?
          if (_stmt.isNull(_columnIndexOfExpectedHatchDate)) {
            _tmpExpectedHatchDate = null
          } else {
            _tmpExpectedHatchDate = _stmt.getLong(_columnIndexOfExpectedHatchDate)
          }
          val _tmpActualHatchStartDate: Long?
          if (_stmt.isNull(_columnIndexOfActualHatchStartDate)) {
            _tmpActualHatchStartDate = null
          } else {
            _tmpActualHatchStartDate = _stmt.getLong(_columnIndexOfActualHatchStartDate)
          }
          val _tmpActualHatchEndDate: Long?
          if (_stmt.isNull(_columnIndexOfActualHatchEndDate)) {
            _tmpActualHatchEndDate = null
          } else {
            _tmpActualHatchEndDate = _stmt.getLong(_columnIndexOfActualHatchEndDate)
          }
          val _tmpChicksHatched: Int
          _tmpChicksHatched = _stmt.getLong(_columnIndexOfChicksHatched).toInt()
          val _tmpChicksMale: Int
          _tmpChicksMale = _stmt.getLong(_columnIndexOfChicksMale).toInt()
          val _tmpChicksFemale: Int
          _tmpChicksFemale = _stmt.getLong(_columnIndexOfChicksFemale).toInt()
          val _tmpChicksUnsexed: Int
          _tmpChicksUnsexed = _stmt.getLong(_columnIndexOfChicksUnsexed).toInt()
          val _tmpDeadInShell: Int
          _tmpDeadInShell = _stmt.getLong(_columnIndexOfDeadInShell).toInt()
          val _tmpPippedNotHatched: Int
          _tmpPippedNotHatched = _stmt.getLong(_columnIndexOfPippedNotHatched).toInt()
          val _tmpAverageChickWeight: Double?
          if (_stmt.isNull(_columnIndexOfAverageChickWeight)) {
            _tmpAverageChickWeight = null
          } else {
            _tmpAverageChickWeight = _stmt.getDouble(_columnIndexOfAverageChickWeight)
          }
          val _tmpChickQualityScore: Int?
          if (_stmt.isNull(_columnIndexOfChickQualityScore)) {
            _tmpChickQualityScore = null
          } else {
            _tmpChickQualityScore = _stmt.getLong(_columnIndexOfChickQualityScore).toInt()
          }
          val _tmpQualityNotes: String?
          if (_stmt.isNull(_columnIndexOfQualityNotes)) {
            _tmpQualityNotes = null
          } else {
            _tmpQualityNotes = _stmt.getText(_columnIndexOfQualityNotes)
          }
          val _tmpFertilityRate: Double?
          if (_stmt.isNull(_columnIndexOfFertilityRate)) {
            _tmpFertilityRate = null
          } else {
            _tmpFertilityRate = _stmt.getDouble(_columnIndexOfFertilityRate)
          }
          val _tmpHatchabilityOfFertile: Double?
          if (_stmt.isNull(_columnIndexOfHatchabilityOfFertile)) {
            _tmpHatchabilityOfFertile = null
          } else {
            _tmpHatchabilityOfFertile = _stmt.getDouble(_columnIndexOfHatchabilityOfFertile)
          }
          val _tmpHatchabilityOfSet: Double?
          if (_stmt.isNull(_columnIndexOfHatchabilityOfSet)) {
            _tmpHatchabilityOfSet = null
          } else {
            _tmpHatchabilityOfSet = _stmt.getDouble(_columnIndexOfHatchabilityOfSet)
          }
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpOffspringIdsJson: String?
          if (_stmt.isNull(_columnIndexOfOffspringIdsJson)) {
            _tmpOffspringIdsJson = null
          } else {
            _tmpOffspringIdsJson = _stmt.getText(_columnIndexOfOffspringIdsJson)
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
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp != 0
          val _tmpSyncedAt: Long?
          if (_stmt.isNull(_columnIndexOfSyncedAt)) {
            _tmpSyncedAt = null
          } else {
            _tmpSyncedAt = _stmt.getLong(_columnIndexOfSyncedAt)
          }
          _item =
              ClutchEntity(_tmpClutchId,_tmpBreedingPairId,_tmpFarmerId,_tmpSireId,_tmpDamId,_tmpClutchName,_tmpClutchNumber,_tmpEggsCollected,_tmpCollectionStartDate,_tmpCollectionEndDate,_tmpSetDate,_tmpEggsSet,_tmpIncubatorId,_tmpIncubationNotes,_tmpFirstCandleDate,_tmpFirstCandleFertile,_tmpFirstCandleClear,_tmpFirstCandleEarlyDead,_tmpSecondCandleDate,_tmpSecondCandleAlive,_tmpSecondCandleDead,_tmpExpectedHatchDate,_tmpActualHatchStartDate,_tmpActualHatchEndDate,_tmpChicksHatched,_tmpChicksMale,_tmpChicksFemale,_tmpChicksUnsexed,_tmpDeadInShell,_tmpPippedNotHatched,_tmpAverageChickWeight,_tmpChickQualityScore,_tmpQualityNotes,_tmpFertilityRate,_tmpHatchabilityOfFertile,_tmpHatchabilityOfSet,_tmpStatus,_tmpOffspringIdsJson,_tmpNotes,_tmpMediaUrlsJson,_tmpCreatedAt,_tmpUpdatedAt,_tmpDirty,_tmpSyncedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getAverageHatchabilityForPair(pairId: String): Double? {
    val _sql: String = """
        |
        |        SELECT AVG(hatchabilityOfFertile) 
        |        FROM clutches 
        |        WHERE breedingPairId = ? 
        |        AND status = 'COMPLETE' 
        |        AND hatchabilityOfFertile IS NOT NULL
        |    
        """.trimMargin()
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, pairId)
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

  public override suspend fun getAverageFertilityForPair(pairId: String): Double? {
    val _sql: String = """
        |
        |        SELECT AVG(fertilityRate) 
        |        FROM clutches 
        |        WHERE breedingPairId = ? 
        |        AND status = 'COMPLETE' 
        |        AND fertilityRate IS NOT NULL
        |    
        """.trimMargin()
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, pairId)
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

  public override suspend fun getTotalChicksForPair(pairId: String): Int? {
    val _sql: String =
        "SELECT SUM(chicksHatched) FROM clutches WHERE breedingPairId = ? AND status = 'COMPLETE'"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, pairId)
        val _result: Int?
        if (_stmt.step()) {
          val _tmp: Int?
          if (_stmt.isNull(0)) {
            _tmp = null
          } else {
            _tmp = _stmt.getLong(0).toInt()
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

  public override suspend fun getClutchCountForPair(pairId: String): Int {
    val _sql: String = "SELECT COUNT(*) FROM clutches WHERE breedingPairId = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, pairId)
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

  public override suspend fun getTotalChicksSince(farmerId: String, since: Long): Int? {
    val _sql: String =
        "SELECT SUM(chicksHatched) FROM clutches WHERE farmerId = ? AND actualHatchEndDate >= ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, farmerId)
        _argIndex = 2
        _stmt.bindLong(_argIndex, since)
        val _result: Int?
        if (_stmt.step()) {
          val _tmp: Int?
          if (_stmt.isNull(0)) {
            _tmp = null
          } else {
            _tmp = _stmt.getLong(0).toInt()
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

  public override suspend fun getAverageHatchability(farmerId: String): Double? {
    val _sql: String =
        "SELECT AVG(hatchabilityOfSet) FROM clutches WHERE farmerId = ? AND status = 'COMPLETE' AND hatchabilityOfSet IS NOT NULL"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, farmerId)
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

  public override suspend fun countByStatus(farmerId: String, status: String): Int {
    val _sql: String = "SELECT COUNT(*) FROM clutches WHERE farmerId = ? AND status = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, farmerId)
        _argIndex = 2
        _stmt.bindText(_argIndex, status)
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

  public override suspend fun delete(clutchId: String) {
    val _sql: String = "DELETE FROM clutches WHERE clutchId = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, clutchId)
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
