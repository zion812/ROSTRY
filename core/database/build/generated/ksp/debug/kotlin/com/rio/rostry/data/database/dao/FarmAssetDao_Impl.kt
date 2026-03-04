package com.rio.rostry.`data`.database.dao

import androidx.room.EntityDeleteOrUpdateAdapter
import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.rio.rostry.`data`.database.AppDatabase
import com.rio.rostry.`data`.database.entity.FarmAssetEntity
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
public class FarmAssetDao_Impl(
  __db: RoomDatabase,
) : FarmAssetDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfFarmAssetEntity: EntityInsertAdapter<FarmAssetEntity>

  private val __updateAdapterOfFarmAssetEntity: EntityDeleteOrUpdateAdapter<FarmAssetEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfFarmAssetEntity = object : EntityInsertAdapter<FarmAssetEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `farm_assets` (`assetId`,`farmerId`,`name`,`assetType`,`category`,`status`,`isShowcase`,`locationName`,`latitude`,`longitude`,`quantity`,`initialQuantity`,`unit`,`birthDate`,`ageWeeks`,`breed`,`gender`,`color`,`healthStatus`,`raisingPurpose`,`description`,`imageUrls`,`notes`,`lifecycleSubStage`,`parentIdsJson`,`batchId`,`origin`,`birdCode`,`acquisitionPrice`,`acquisitionDate`,`acquisitionSource`,`acquisitionSourceId`,`acquisitionNotes`,`estimatedValue`,`lastVaccinationDate`,`nextVaccinationDate`,`weightGrams`,`metadataJson`,`listedAt`,`listingId`,`soldAt`,`soldToUserId`,`soldPrice`,`previousOwnerId`,`transferredAt`,`createdAt`,`updatedAt`,`isDeleted`,`deletedAt`,`dirty`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: FarmAssetEntity) {
        statement.bindText(1, entity.assetId)
        statement.bindText(2, entity.farmerId)
        statement.bindText(3, entity.name)
        statement.bindText(4, entity.assetType)
        statement.bindText(5, entity.category)
        statement.bindText(6, entity.status)
        val _tmp: Int = if (entity.isShowcase) 1 else 0
        statement.bindLong(7, _tmp.toLong())
        val _tmpLocationName: String? = entity.locationName
        if (_tmpLocationName == null) {
          statement.bindNull(8)
        } else {
          statement.bindText(8, _tmpLocationName)
        }
        val _tmpLatitude: Double? = entity.latitude
        if (_tmpLatitude == null) {
          statement.bindNull(9)
        } else {
          statement.bindDouble(9, _tmpLatitude)
        }
        val _tmpLongitude: Double? = entity.longitude
        if (_tmpLongitude == null) {
          statement.bindNull(10)
        } else {
          statement.bindDouble(10, _tmpLongitude)
        }
        statement.bindDouble(11, entity.quantity)
        statement.bindDouble(12, entity.initialQuantity)
        statement.bindText(13, entity.unit)
        val _tmpBirthDate: Long? = entity.birthDate
        if (_tmpBirthDate == null) {
          statement.bindNull(14)
        } else {
          statement.bindLong(14, _tmpBirthDate)
        }
        val _tmpAgeWeeks: Int? = entity.ageWeeks
        if (_tmpAgeWeeks == null) {
          statement.bindNull(15)
        } else {
          statement.bindLong(15, _tmpAgeWeeks.toLong())
        }
        val _tmpBreed: String? = entity.breed
        if (_tmpBreed == null) {
          statement.bindNull(16)
        } else {
          statement.bindText(16, _tmpBreed)
        }
        val _tmpGender: String? = entity.gender
        if (_tmpGender == null) {
          statement.bindNull(17)
        } else {
          statement.bindText(17, _tmpGender)
        }
        val _tmpColor: String? = entity.color
        if (_tmpColor == null) {
          statement.bindNull(18)
        } else {
          statement.bindText(18, _tmpColor)
        }
        statement.bindText(19, entity.healthStatus)
        val _tmpRaisingPurpose: String? = entity.raisingPurpose
        if (_tmpRaisingPurpose == null) {
          statement.bindNull(20)
        } else {
          statement.bindText(20, _tmpRaisingPurpose)
        }
        statement.bindText(21, entity.description)
        val _tmp_1: String? = AppDatabase.Converters.fromStringList(entity.imageUrls)
        if (_tmp_1 == null) {
          statement.bindNull(22)
        } else {
          statement.bindText(22, _tmp_1)
        }
        val _tmpNotes: String? = entity.notes
        if (_tmpNotes == null) {
          statement.bindNull(23)
        } else {
          statement.bindText(23, _tmpNotes)
        }
        val _tmpLifecycleSubStage: String? = entity.lifecycleSubStage
        if (_tmpLifecycleSubStage == null) {
          statement.bindNull(24)
        } else {
          statement.bindText(24, _tmpLifecycleSubStage)
        }
        val _tmpParentIdsJson: String? = entity.parentIdsJson
        if (_tmpParentIdsJson == null) {
          statement.bindNull(25)
        } else {
          statement.bindText(25, _tmpParentIdsJson)
        }
        val _tmpBatchId: String? = entity.batchId
        if (_tmpBatchId == null) {
          statement.bindNull(26)
        } else {
          statement.bindText(26, _tmpBatchId)
        }
        val _tmpOrigin: String? = entity.origin
        if (_tmpOrigin == null) {
          statement.bindNull(27)
        } else {
          statement.bindText(27, _tmpOrigin)
        }
        val _tmpBirdCode: String? = entity.birdCode
        if (_tmpBirdCode == null) {
          statement.bindNull(28)
        } else {
          statement.bindText(28, _tmpBirdCode)
        }
        val _tmpAcquisitionPrice: Double? = entity.acquisitionPrice
        if (_tmpAcquisitionPrice == null) {
          statement.bindNull(29)
        } else {
          statement.bindDouble(29, _tmpAcquisitionPrice)
        }
        val _tmpAcquisitionDate: Long? = entity.acquisitionDate
        if (_tmpAcquisitionDate == null) {
          statement.bindNull(30)
        } else {
          statement.bindLong(30, _tmpAcquisitionDate)
        }
        val _tmpAcquisitionSource: String? = entity.acquisitionSource
        if (_tmpAcquisitionSource == null) {
          statement.bindNull(31)
        } else {
          statement.bindText(31, _tmpAcquisitionSource)
        }
        val _tmpAcquisitionSourceId: String? = entity.acquisitionSourceId
        if (_tmpAcquisitionSourceId == null) {
          statement.bindNull(32)
        } else {
          statement.bindText(32, _tmpAcquisitionSourceId)
        }
        val _tmpAcquisitionNotes: String? = entity.acquisitionNotes
        if (_tmpAcquisitionNotes == null) {
          statement.bindNull(33)
        } else {
          statement.bindText(33, _tmpAcquisitionNotes)
        }
        val _tmpEstimatedValue: Double? = entity.estimatedValue
        if (_tmpEstimatedValue == null) {
          statement.bindNull(34)
        } else {
          statement.bindDouble(34, _tmpEstimatedValue)
        }
        val _tmpLastVaccinationDate: Long? = entity.lastVaccinationDate
        if (_tmpLastVaccinationDate == null) {
          statement.bindNull(35)
        } else {
          statement.bindLong(35, _tmpLastVaccinationDate)
        }
        val _tmpNextVaccinationDate: Long? = entity.nextVaccinationDate
        if (_tmpNextVaccinationDate == null) {
          statement.bindNull(36)
        } else {
          statement.bindLong(36, _tmpNextVaccinationDate)
        }
        val _tmpWeightGrams: Double? = entity.weightGrams
        if (_tmpWeightGrams == null) {
          statement.bindNull(37)
        } else {
          statement.bindDouble(37, _tmpWeightGrams)
        }
        statement.bindText(38, entity.metadataJson)
        val _tmpListedAt: Long? = entity.listedAt
        if (_tmpListedAt == null) {
          statement.bindNull(39)
        } else {
          statement.bindLong(39, _tmpListedAt)
        }
        val _tmpListingId: String? = entity.listingId
        if (_tmpListingId == null) {
          statement.bindNull(40)
        } else {
          statement.bindText(40, _tmpListingId)
        }
        val _tmpSoldAt: Long? = entity.soldAt
        if (_tmpSoldAt == null) {
          statement.bindNull(41)
        } else {
          statement.bindLong(41, _tmpSoldAt)
        }
        val _tmpSoldToUserId: String? = entity.soldToUserId
        if (_tmpSoldToUserId == null) {
          statement.bindNull(42)
        } else {
          statement.bindText(42, _tmpSoldToUserId)
        }
        val _tmpSoldPrice: Double? = entity.soldPrice
        if (_tmpSoldPrice == null) {
          statement.bindNull(43)
        } else {
          statement.bindDouble(43, _tmpSoldPrice)
        }
        val _tmpPreviousOwnerId: String? = entity.previousOwnerId
        if (_tmpPreviousOwnerId == null) {
          statement.bindNull(44)
        } else {
          statement.bindText(44, _tmpPreviousOwnerId)
        }
        val _tmpTransferredAt: Long? = entity.transferredAt
        if (_tmpTransferredAt == null) {
          statement.bindNull(45)
        } else {
          statement.bindLong(45, _tmpTransferredAt)
        }
        statement.bindLong(46, entity.createdAt)
        statement.bindLong(47, entity.updatedAt)
        val _tmp_2: Int = if (entity.isDeleted) 1 else 0
        statement.bindLong(48, _tmp_2.toLong())
        val _tmpDeletedAt: Long? = entity.deletedAt
        if (_tmpDeletedAt == null) {
          statement.bindNull(49)
        } else {
          statement.bindLong(49, _tmpDeletedAt)
        }
        val _tmp_3: Int = if (entity.dirty) 1 else 0
        statement.bindLong(50, _tmp_3.toLong())
      }
    }
    this.__updateAdapterOfFarmAssetEntity = object : EntityDeleteOrUpdateAdapter<FarmAssetEntity>()
        {
      protected override fun createQuery(): String =
          "UPDATE OR ABORT `farm_assets` SET `assetId` = ?,`farmerId` = ?,`name` = ?,`assetType` = ?,`category` = ?,`status` = ?,`isShowcase` = ?,`locationName` = ?,`latitude` = ?,`longitude` = ?,`quantity` = ?,`initialQuantity` = ?,`unit` = ?,`birthDate` = ?,`ageWeeks` = ?,`breed` = ?,`gender` = ?,`color` = ?,`healthStatus` = ?,`raisingPurpose` = ?,`description` = ?,`imageUrls` = ?,`notes` = ?,`lifecycleSubStage` = ?,`parentIdsJson` = ?,`batchId` = ?,`origin` = ?,`birdCode` = ?,`acquisitionPrice` = ?,`acquisitionDate` = ?,`acquisitionSource` = ?,`acquisitionSourceId` = ?,`acquisitionNotes` = ?,`estimatedValue` = ?,`lastVaccinationDate` = ?,`nextVaccinationDate` = ?,`weightGrams` = ?,`metadataJson` = ?,`listedAt` = ?,`listingId` = ?,`soldAt` = ?,`soldToUserId` = ?,`soldPrice` = ?,`previousOwnerId` = ?,`transferredAt` = ?,`createdAt` = ?,`updatedAt` = ?,`isDeleted` = ?,`deletedAt` = ?,`dirty` = ? WHERE `assetId` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: FarmAssetEntity) {
        statement.bindText(1, entity.assetId)
        statement.bindText(2, entity.farmerId)
        statement.bindText(3, entity.name)
        statement.bindText(4, entity.assetType)
        statement.bindText(5, entity.category)
        statement.bindText(6, entity.status)
        val _tmp: Int = if (entity.isShowcase) 1 else 0
        statement.bindLong(7, _tmp.toLong())
        val _tmpLocationName: String? = entity.locationName
        if (_tmpLocationName == null) {
          statement.bindNull(8)
        } else {
          statement.bindText(8, _tmpLocationName)
        }
        val _tmpLatitude: Double? = entity.latitude
        if (_tmpLatitude == null) {
          statement.bindNull(9)
        } else {
          statement.bindDouble(9, _tmpLatitude)
        }
        val _tmpLongitude: Double? = entity.longitude
        if (_tmpLongitude == null) {
          statement.bindNull(10)
        } else {
          statement.bindDouble(10, _tmpLongitude)
        }
        statement.bindDouble(11, entity.quantity)
        statement.bindDouble(12, entity.initialQuantity)
        statement.bindText(13, entity.unit)
        val _tmpBirthDate: Long? = entity.birthDate
        if (_tmpBirthDate == null) {
          statement.bindNull(14)
        } else {
          statement.bindLong(14, _tmpBirthDate)
        }
        val _tmpAgeWeeks: Int? = entity.ageWeeks
        if (_tmpAgeWeeks == null) {
          statement.bindNull(15)
        } else {
          statement.bindLong(15, _tmpAgeWeeks.toLong())
        }
        val _tmpBreed: String? = entity.breed
        if (_tmpBreed == null) {
          statement.bindNull(16)
        } else {
          statement.bindText(16, _tmpBreed)
        }
        val _tmpGender: String? = entity.gender
        if (_tmpGender == null) {
          statement.bindNull(17)
        } else {
          statement.bindText(17, _tmpGender)
        }
        val _tmpColor: String? = entity.color
        if (_tmpColor == null) {
          statement.bindNull(18)
        } else {
          statement.bindText(18, _tmpColor)
        }
        statement.bindText(19, entity.healthStatus)
        val _tmpRaisingPurpose: String? = entity.raisingPurpose
        if (_tmpRaisingPurpose == null) {
          statement.bindNull(20)
        } else {
          statement.bindText(20, _tmpRaisingPurpose)
        }
        statement.bindText(21, entity.description)
        val _tmp_1: String? = AppDatabase.Converters.fromStringList(entity.imageUrls)
        if (_tmp_1 == null) {
          statement.bindNull(22)
        } else {
          statement.bindText(22, _tmp_1)
        }
        val _tmpNotes: String? = entity.notes
        if (_tmpNotes == null) {
          statement.bindNull(23)
        } else {
          statement.bindText(23, _tmpNotes)
        }
        val _tmpLifecycleSubStage: String? = entity.lifecycleSubStage
        if (_tmpLifecycleSubStage == null) {
          statement.bindNull(24)
        } else {
          statement.bindText(24, _tmpLifecycleSubStage)
        }
        val _tmpParentIdsJson: String? = entity.parentIdsJson
        if (_tmpParentIdsJson == null) {
          statement.bindNull(25)
        } else {
          statement.bindText(25, _tmpParentIdsJson)
        }
        val _tmpBatchId: String? = entity.batchId
        if (_tmpBatchId == null) {
          statement.bindNull(26)
        } else {
          statement.bindText(26, _tmpBatchId)
        }
        val _tmpOrigin: String? = entity.origin
        if (_tmpOrigin == null) {
          statement.bindNull(27)
        } else {
          statement.bindText(27, _tmpOrigin)
        }
        val _tmpBirdCode: String? = entity.birdCode
        if (_tmpBirdCode == null) {
          statement.bindNull(28)
        } else {
          statement.bindText(28, _tmpBirdCode)
        }
        val _tmpAcquisitionPrice: Double? = entity.acquisitionPrice
        if (_tmpAcquisitionPrice == null) {
          statement.bindNull(29)
        } else {
          statement.bindDouble(29, _tmpAcquisitionPrice)
        }
        val _tmpAcquisitionDate: Long? = entity.acquisitionDate
        if (_tmpAcquisitionDate == null) {
          statement.bindNull(30)
        } else {
          statement.bindLong(30, _tmpAcquisitionDate)
        }
        val _tmpAcquisitionSource: String? = entity.acquisitionSource
        if (_tmpAcquisitionSource == null) {
          statement.bindNull(31)
        } else {
          statement.bindText(31, _tmpAcquisitionSource)
        }
        val _tmpAcquisitionSourceId: String? = entity.acquisitionSourceId
        if (_tmpAcquisitionSourceId == null) {
          statement.bindNull(32)
        } else {
          statement.bindText(32, _tmpAcquisitionSourceId)
        }
        val _tmpAcquisitionNotes: String? = entity.acquisitionNotes
        if (_tmpAcquisitionNotes == null) {
          statement.bindNull(33)
        } else {
          statement.bindText(33, _tmpAcquisitionNotes)
        }
        val _tmpEstimatedValue: Double? = entity.estimatedValue
        if (_tmpEstimatedValue == null) {
          statement.bindNull(34)
        } else {
          statement.bindDouble(34, _tmpEstimatedValue)
        }
        val _tmpLastVaccinationDate: Long? = entity.lastVaccinationDate
        if (_tmpLastVaccinationDate == null) {
          statement.bindNull(35)
        } else {
          statement.bindLong(35, _tmpLastVaccinationDate)
        }
        val _tmpNextVaccinationDate: Long? = entity.nextVaccinationDate
        if (_tmpNextVaccinationDate == null) {
          statement.bindNull(36)
        } else {
          statement.bindLong(36, _tmpNextVaccinationDate)
        }
        val _tmpWeightGrams: Double? = entity.weightGrams
        if (_tmpWeightGrams == null) {
          statement.bindNull(37)
        } else {
          statement.bindDouble(37, _tmpWeightGrams)
        }
        statement.bindText(38, entity.metadataJson)
        val _tmpListedAt: Long? = entity.listedAt
        if (_tmpListedAt == null) {
          statement.bindNull(39)
        } else {
          statement.bindLong(39, _tmpListedAt)
        }
        val _tmpListingId: String? = entity.listingId
        if (_tmpListingId == null) {
          statement.bindNull(40)
        } else {
          statement.bindText(40, _tmpListingId)
        }
        val _tmpSoldAt: Long? = entity.soldAt
        if (_tmpSoldAt == null) {
          statement.bindNull(41)
        } else {
          statement.bindLong(41, _tmpSoldAt)
        }
        val _tmpSoldToUserId: String? = entity.soldToUserId
        if (_tmpSoldToUserId == null) {
          statement.bindNull(42)
        } else {
          statement.bindText(42, _tmpSoldToUserId)
        }
        val _tmpSoldPrice: Double? = entity.soldPrice
        if (_tmpSoldPrice == null) {
          statement.bindNull(43)
        } else {
          statement.bindDouble(43, _tmpSoldPrice)
        }
        val _tmpPreviousOwnerId: String? = entity.previousOwnerId
        if (_tmpPreviousOwnerId == null) {
          statement.bindNull(44)
        } else {
          statement.bindText(44, _tmpPreviousOwnerId)
        }
        val _tmpTransferredAt: Long? = entity.transferredAt
        if (_tmpTransferredAt == null) {
          statement.bindNull(45)
        } else {
          statement.bindLong(45, _tmpTransferredAt)
        }
        statement.bindLong(46, entity.createdAt)
        statement.bindLong(47, entity.updatedAt)
        val _tmp_2: Int = if (entity.isDeleted) 1 else 0
        statement.bindLong(48, _tmp_2.toLong())
        val _tmpDeletedAt: Long? = entity.deletedAt
        if (_tmpDeletedAt == null) {
          statement.bindNull(49)
        } else {
          statement.bindLong(49, _tmpDeletedAt)
        }
        val _tmp_3: Int = if (entity.dirty) 1 else 0
        statement.bindLong(50, _tmp_3.toLong())
        statement.bindText(51, entity.assetId)
      }
    }
  }

  public override suspend fun insertAsset(asset: FarmAssetEntity): Unit = performSuspending(__db,
      false, true) { _connection ->
    __insertAdapterOfFarmAssetEntity.insert(_connection, asset)
  }

  public override suspend fun upsert(asset: FarmAssetEntity): Unit = performSuspending(__db, false,
      true) { _connection ->
    __insertAdapterOfFarmAssetEntity.insert(_connection, asset)
  }

  public override suspend fun updateAsset(asset: FarmAssetEntity): Unit = performSuspending(__db,
      false, true) { _connection ->
    __updateAdapterOfFarmAssetEntity.handle(_connection, asset)
  }

  public override fun getAssetById(assetId: String): Flow<FarmAssetEntity?> {
    val _sql: String = "SELECT * FROM farm_assets WHERE assetId = ?"
    return createFlow(__db, false, arrayOf("farm_assets")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, assetId)
        val _columnIndexOfAssetId: Int = getColumnIndexOrThrow(_stmt, "assetId")
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfAssetType: Int = getColumnIndexOrThrow(_stmt, "assetType")
        val _columnIndexOfCategory: Int = getColumnIndexOrThrow(_stmt, "category")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfIsShowcase: Int = getColumnIndexOrThrow(_stmt, "isShowcase")
        val _columnIndexOfLocationName: Int = getColumnIndexOrThrow(_stmt, "locationName")
        val _columnIndexOfLatitude: Int = getColumnIndexOrThrow(_stmt, "latitude")
        val _columnIndexOfLongitude: Int = getColumnIndexOrThrow(_stmt, "longitude")
        val _columnIndexOfQuantity: Int = getColumnIndexOrThrow(_stmt, "quantity")
        val _columnIndexOfInitialQuantity: Int = getColumnIndexOrThrow(_stmt, "initialQuantity")
        val _columnIndexOfUnit: Int = getColumnIndexOrThrow(_stmt, "unit")
        val _columnIndexOfBirthDate: Int = getColumnIndexOrThrow(_stmt, "birthDate")
        val _columnIndexOfAgeWeeks: Int = getColumnIndexOrThrow(_stmt, "ageWeeks")
        val _columnIndexOfBreed: Int = getColumnIndexOrThrow(_stmt, "breed")
        val _columnIndexOfGender: Int = getColumnIndexOrThrow(_stmt, "gender")
        val _columnIndexOfColor: Int = getColumnIndexOrThrow(_stmt, "color")
        val _columnIndexOfHealthStatus: Int = getColumnIndexOrThrow(_stmt, "healthStatus")
        val _columnIndexOfRaisingPurpose: Int = getColumnIndexOrThrow(_stmt, "raisingPurpose")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfImageUrls: Int = getColumnIndexOrThrow(_stmt, "imageUrls")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfLifecycleSubStage: Int = getColumnIndexOrThrow(_stmt, "lifecycleSubStage")
        val _columnIndexOfParentIdsJson: Int = getColumnIndexOrThrow(_stmt, "parentIdsJson")
        val _columnIndexOfBatchId: Int = getColumnIndexOrThrow(_stmt, "batchId")
        val _columnIndexOfOrigin: Int = getColumnIndexOrThrow(_stmt, "origin")
        val _columnIndexOfBirdCode: Int = getColumnIndexOrThrow(_stmt, "birdCode")
        val _columnIndexOfAcquisitionPrice: Int = getColumnIndexOrThrow(_stmt, "acquisitionPrice")
        val _columnIndexOfAcquisitionDate: Int = getColumnIndexOrThrow(_stmt, "acquisitionDate")
        val _columnIndexOfAcquisitionSource: Int = getColumnIndexOrThrow(_stmt, "acquisitionSource")
        val _columnIndexOfAcquisitionSourceId: Int = getColumnIndexOrThrow(_stmt,
            "acquisitionSourceId")
        val _columnIndexOfAcquisitionNotes: Int = getColumnIndexOrThrow(_stmt, "acquisitionNotes")
        val _columnIndexOfEstimatedValue: Int = getColumnIndexOrThrow(_stmt, "estimatedValue")
        val _columnIndexOfLastVaccinationDate: Int = getColumnIndexOrThrow(_stmt,
            "lastVaccinationDate")
        val _columnIndexOfNextVaccinationDate: Int = getColumnIndexOrThrow(_stmt,
            "nextVaccinationDate")
        val _columnIndexOfWeightGrams: Int = getColumnIndexOrThrow(_stmt, "weightGrams")
        val _columnIndexOfMetadataJson: Int = getColumnIndexOrThrow(_stmt, "metadataJson")
        val _columnIndexOfListedAt: Int = getColumnIndexOrThrow(_stmt, "listedAt")
        val _columnIndexOfListingId: Int = getColumnIndexOrThrow(_stmt, "listingId")
        val _columnIndexOfSoldAt: Int = getColumnIndexOrThrow(_stmt, "soldAt")
        val _columnIndexOfSoldToUserId: Int = getColumnIndexOrThrow(_stmt, "soldToUserId")
        val _columnIndexOfSoldPrice: Int = getColumnIndexOrThrow(_stmt, "soldPrice")
        val _columnIndexOfPreviousOwnerId: Int = getColumnIndexOrThrow(_stmt, "previousOwnerId")
        val _columnIndexOfTransferredAt: Int = getColumnIndexOrThrow(_stmt, "transferredAt")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfIsDeleted: Int = getColumnIndexOrThrow(_stmt, "isDeleted")
        val _columnIndexOfDeletedAt: Int = getColumnIndexOrThrow(_stmt, "deletedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _result: FarmAssetEntity?
        if (_stmt.step()) {
          val _tmpAssetId: String
          _tmpAssetId = _stmt.getText(_columnIndexOfAssetId)
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpAssetType: String
          _tmpAssetType = _stmt.getText(_columnIndexOfAssetType)
          val _tmpCategory: String
          _tmpCategory = _stmt.getText(_columnIndexOfCategory)
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpIsShowcase: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsShowcase).toInt()
          _tmpIsShowcase = _tmp != 0
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
          val _tmpQuantity: Double
          _tmpQuantity = _stmt.getDouble(_columnIndexOfQuantity)
          val _tmpInitialQuantity: Double
          _tmpInitialQuantity = _stmt.getDouble(_columnIndexOfInitialQuantity)
          val _tmpUnit: String
          _tmpUnit = _stmt.getText(_columnIndexOfUnit)
          val _tmpBirthDate: Long?
          if (_stmt.isNull(_columnIndexOfBirthDate)) {
            _tmpBirthDate = null
          } else {
            _tmpBirthDate = _stmt.getLong(_columnIndexOfBirthDate)
          }
          val _tmpAgeWeeks: Int?
          if (_stmt.isNull(_columnIndexOfAgeWeeks)) {
            _tmpAgeWeeks = null
          } else {
            _tmpAgeWeeks = _stmt.getLong(_columnIndexOfAgeWeeks).toInt()
          }
          val _tmpBreed: String?
          if (_stmt.isNull(_columnIndexOfBreed)) {
            _tmpBreed = null
          } else {
            _tmpBreed = _stmt.getText(_columnIndexOfBreed)
          }
          val _tmpGender: String?
          if (_stmt.isNull(_columnIndexOfGender)) {
            _tmpGender = null
          } else {
            _tmpGender = _stmt.getText(_columnIndexOfGender)
          }
          val _tmpColor: String?
          if (_stmt.isNull(_columnIndexOfColor)) {
            _tmpColor = null
          } else {
            _tmpColor = _stmt.getText(_columnIndexOfColor)
          }
          val _tmpHealthStatus: String
          _tmpHealthStatus = _stmt.getText(_columnIndexOfHealthStatus)
          val _tmpRaisingPurpose: String?
          if (_stmt.isNull(_columnIndexOfRaisingPurpose)) {
            _tmpRaisingPurpose = null
          } else {
            _tmpRaisingPurpose = _stmt.getText(_columnIndexOfRaisingPurpose)
          }
          val _tmpDescription: String
          _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          val _tmpImageUrls: List<String>
          val _tmp_1: String?
          if (_stmt.isNull(_columnIndexOfImageUrls)) {
            _tmp_1 = null
          } else {
            _tmp_1 = _stmt.getText(_columnIndexOfImageUrls)
          }
          val _tmp_2: List<String>? = AppDatabase.Converters.toStringList(_tmp_1)
          if (_tmp_2 == null) {
            error("Expected NON-NULL 'kotlin.collections.List<kotlin.String>', but it was NULL.")
          } else {
            _tmpImageUrls = _tmp_2
          }
          val _tmpNotes: String?
          if (_stmt.isNull(_columnIndexOfNotes)) {
            _tmpNotes = null
          } else {
            _tmpNotes = _stmt.getText(_columnIndexOfNotes)
          }
          val _tmpLifecycleSubStage: String?
          if (_stmt.isNull(_columnIndexOfLifecycleSubStage)) {
            _tmpLifecycleSubStage = null
          } else {
            _tmpLifecycleSubStage = _stmt.getText(_columnIndexOfLifecycleSubStage)
          }
          val _tmpParentIdsJson: String?
          if (_stmt.isNull(_columnIndexOfParentIdsJson)) {
            _tmpParentIdsJson = null
          } else {
            _tmpParentIdsJson = _stmt.getText(_columnIndexOfParentIdsJson)
          }
          val _tmpBatchId: String?
          if (_stmt.isNull(_columnIndexOfBatchId)) {
            _tmpBatchId = null
          } else {
            _tmpBatchId = _stmt.getText(_columnIndexOfBatchId)
          }
          val _tmpOrigin: String?
          if (_stmt.isNull(_columnIndexOfOrigin)) {
            _tmpOrigin = null
          } else {
            _tmpOrigin = _stmt.getText(_columnIndexOfOrigin)
          }
          val _tmpBirdCode: String?
          if (_stmt.isNull(_columnIndexOfBirdCode)) {
            _tmpBirdCode = null
          } else {
            _tmpBirdCode = _stmt.getText(_columnIndexOfBirdCode)
          }
          val _tmpAcquisitionPrice: Double?
          if (_stmt.isNull(_columnIndexOfAcquisitionPrice)) {
            _tmpAcquisitionPrice = null
          } else {
            _tmpAcquisitionPrice = _stmt.getDouble(_columnIndexOfAcquisitionPrice)
          }
          val _tmpAcquisitionDate: Long?
          if (_stmt.isNull(_columnIndexOfAcquisitionDate)) {
            _tmpAcquisitionDate = null
          } else {
            _tmpAcquisitionDate = _stmt.getLong(_columnIndexOfAcquisitionDate)
          }
          val _tmpAcquisitionSource: String?
          if (_stmt.isNull(_columnIndexOfAcquisitionSource)) {
            _tmpAcquisitionSource = null
          } else {
            _tmpAcquisitionSource = _stmt.getText(_columnIndexOfAcquisitionSource)
          }
          val _tmpAcquisitionSourceId: String?
          if (_stmt.isNull(_columnIndexOfAcquisitionSourceId)) {
            _tmpAcquisitionSourceId = null
          } else {
            _tmpAcquisitionSourceId = _stmt.getText(_columnIndexOfAcquisitionSourceId)
          }
          val _tmpAcquisitionNotes: String?
          if (_stmt.isNull(_columnIndexOfAcquisitionNotes)) {
            _tmpAcquisitionNotes = null
          } else {
            _tmpAcquisitionNotes = _stmt.getText(_columnIndexOfAcquisitionNotes)
          }
          val _tmpEstimatedValue: Double?
          if (_stmt.isNull(_columnIndexOfEstimatedValue)) {
            _tmpEstimatedValue = null
          } else {
            _tmpEstimatedValue = _stmt.getDouble(_columnIndexOfEstimatedValue)
          }
          val _tmpLastVaccinationDate: Long?
          if (_stmt.isNull(_columnIndexOfLastVaccinationDate)) {
            _tmpLastVaccinationDate = null
          } else {
            _tmpLastVaccinationDate = _stmt.getLong(_columnIndexOfLastVaccinationDate)
          }
          val _tmpNextVaccinationDate: Long?
          if (_stmt.isNull(_columnIndexOfNextVaccinationDate)) {
            _tmpNextVaccinationDate = null
          } else {
            _tmpNextVaccinationDate = _stmt.getLong(_columnIndexOfNextVaccinationDate)
          }
          val _tmpWeightGrams: Double?
          if (_stmt.isNull(_columnIndexOfWeightGrams)) {
            _tmpWeightGrams = null
          } else {
            _tmpWeightGrams = _stmt.getDouble(_columnIndexOfWeightGrams)
          }
          val _tmpMetadataJson: String
          _tmpMetadataJson = _stmt.getText(_columnIndexOfMetadataJson)
          val _tmpListedAt: Long?
          if (_stmt.isNull(_columnIndexOfListedAt)) {
            _tmpListedAt = null
          } else {
            _tmpListedAt = _stmt.getLong(_columnIndexOfListedAt)
          }
          val _tmpListingId: String?
          if (_stmt.isNull(_columnIndexOfListingId)) {
            _tmpListingId = null
          } else {
            _tmpListingId = _stmt.getText(_columnIndexOfListingId)
          }
          val _tmpSoldAt: Long?
          if (_stmt.isNull(_columnIndexOfSoldAt)) {
            _tmpSoldAt = null
          } else {
            _tmpSoldAt = _stmt.getLong(_columnIndexOfSoldAt)
          }
          val _tmpSoldToUserId: String?
          if (_stmt.isNull(_columnIndexOfSoldToUserId)) {
            _tmpSoldToUserId = null
          } else {
            _tmpSoldToUserId = _stmt.getText(_columnIndexOfSoldToUserId)
          }
          val _tmpSoldPrice: Double?
          if (_stmt.isNull(_columnIndexOfSoldPrice)) {
            _tmpSoldPrice = null
          } else {
            _tmpSoldPrice = _stmt.getDouble(_columnIndexOfSoldPrice)
          }
          val _tmpPreviousOwnerId: String?
          if (_stmt.isNull(_columnIndexOfPreviousOwnerId)) {
            _tmpPreviousOwnerId = null
          } else {
            _tmpPreviousOwnerId = _stmt.getText(_columnIndexOfPreviousOwnerId)
          }
          val _tmpTransferredAt: Long?
          if (_stmt.isNull(_columnIndexOfTransferredAt)) {
            _tmpTransferredAt = null
          } else {
            _tmpTransferredAt = _stmt.getLong(_columnIndexOfTransferredAt)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpIsDeleted: Boolean
          val _tmp_3: Int
          _tmp_3 = _stmt.getLong(_columnIndexOfIsDeleted).toInt()
          _tmpIsDeleted = _tmp_3 != 0
          val _tmpDeletedAt: Long?
          if (_stmt.isNull(_columnIndexOfDeletedAt)) {
            _tmpDeletedAt = null
          } else {
            _tmpDeletedAt = _stmt.getLong(_columnIndexOfDeletedAt)
          }
          val _tmpDirty: Boolean
          val _tmp_4: Int
          _tmp_4 = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp_4 != 0
          _result =
              FarmAssetEntity(_tmpAssetId,_tmpFarmerId,_tmpName,_tmpAssetType,_tmpCategory,_tmpStatus,_tmpIsShowcase,_tmpLocationName,_tmpLatitude,_tmpLongitude,_tmpQuantity,_tmpInitialQuantity,_tmpUnit,_tmpBirthDate,_tmpAgeWeeks,_tmpBreed,_tmpGender,_tmpColor,_tmpHealthStatus,_tmpRaisingPurpose,_tmpDescription,_tmpImageUrls,_tmpNotes,_tmpLifecycleSubStage,_tmpParentIdsJson,_tmpBatchId,_tmpOrigin,_tmpBirdCode,_tmpAcquisitionPrice,_tmpAcquisitionDate,_tmpAcquisitionSource,_tmpAcquisitionSourceId,_tmpAcquisitionNotes,_tmpEstimatedValue,_tmpLastVaccinationDate,_tmpNextVaccinationDate,_tmpWeightGrams,_tmpMetadataJson,_tmpListedAt,_tmpListingId,_tmpSoldAt,_tmpSoldToUserId,_tmpSoldPrice,_tmpPreviousOwnerId,_tmpTransferredAt,_tmpCreatedAt,_tmpUpdatedAt,_tmpIsDeleted,_tmpDeletedAt,_tmpDirty)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun findById(assetId: String): FarmAssetEntity? {
    val _sql: String = "SELECT * FROM farm_assets WHERE assetId = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, assetId)
        val _columnIndexOfAssetId: Int = getColumnIndexOrThrow(_stmt, "assetId")
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfAssetType: Int = getColumnIndexOrThrow(_stmt, "assetType")
        val _columnIndexOfCategory: Int = getColumnIndexOrThrow(_stmt, "category")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfIsShowcase: Int = getColumnIndexOrThrow(_stmt, "isShowcase")
        val _columnIndexOfLocationName: Int = getColumnIndexOrThrow(_stmt, "locationName")
        val _columnIndexOfLatitude: Int = getColumnIndexOrThrow(_stmt, "latitude")
        val _columnIndexOfLongitude: Int = getColumnIndexOrThrow(_stmt, "longitude")
        val _columnIndexOfQuantity: Int = getColumnIndexOrThrow(_stmt, "quantity")
        val _columnIndexOfInitialQuantity: Int = getColumnIndexOrThrow(_stmt, "initialQuantity")
        val _columnIndexOfUnit: Int = getColumnIndexOrThrow(_stmt, "unit")
        val _columnIndexOfBirthDate: Int = getColumnIndexOrThrow(_stmt, "birthDate")
        val _columnIndexOfAgeWeeks: Int = getColumnIndexOrThrow(_stmt, "ageWeeks")
        val _columnIndexOfBreed: Int = getColumnIndexOrThrow(_stmt, "breed")
        val _columnIndexOfGender: Int = getColumnIndexOrThrow(_stmt, "gender")
        val _columnIndexOfColor: Int = getColumnIndexOrThrow(_stmt, "color")
        val _columnIndexOfHealthStatus: Int = getColumnIndexOrThrow(_stmt, "healthStatus")
        val _columnIndexOfRaisingPurpose: Int = getColumnIndexOrThrow(_stmt, "raisingPurpose")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfImageUrls: Int = getColumnIndexOrThrow(_stmt, "imageUrls")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfLifecycleSubStage: Int = getColumnIndexOrThrow(_stmt, "lifecycleSubStage")
        val _columnIndexOfParentIdsJson: Int = getColumnIndexOrThrow(_stmt, "parentIdsJson")
        val _columnIndexOfBatchId: Int = getColumnIndexOrThrow(_stmt, "batchId")
        val _columnIndexOfOrigin: Int = getColumnIndexOrThrow(_stmt, "origin")
        val _columnIndexOfBirdCode: Int = getColumnIndexOrThrow(_stmt, "birdCode")
        val _columnIndexOfAcquisitionPrice: Int = getColumnIndexOrThrow(_stmt, "acquisitionPrice")
        val _columnIndexOfAcquisitionDate: Int = getColumnIndexOrThrow(_stmt, "acquisitionDate")
        val _columnIndexOfAcquisitionSource: Int = getColumnIndexOrThrow(_stmt, "acquisitionSource")
        val _columnIndexOfAcquisitionSourceId: Int = getColumnIndexOrThrow(_stmt,
            "acquisitionSourceId")
        val _columnIndexOfAcquisitionNotes: Int = getColumnIndexOrThrow(_stmt, "acquisitionNotes")
        val _columnIndexOfEstimatedValue: Int = getColumnIndexOrThrow(_stmt, "estimatedValue")
        val _columnIndexOfLastVaccinationDate: Int = getColumnIndexOrThrow(_stmt,
            "lastVaccinationDate")
        val _columnIndexOfNextVaccinationDate: Int = getColumnIndexOrThrow(_stmt,
            "nextVaccinationDate")
        val _columnIndexOfWeightGrams: Int = getColumnIndexOrThrow(_stmt, "weightGrams")
        val _columnIndexOfMetadataJson: Int = getColumnIndexOrThrow(_stmt, "metadataJson")
        val _columnIndexOfListedAt: Int = getColumnIndexOrThrow(_stmt, "listedAt")
        val _columnIndexOfListingId: Int = getColumnIndexOrThrow(_stmt, "listingId")
        val _columnIndexOfSoldAt: Int = getColumnIndexOrThrow(_stmt, "soldAt")
        val _columnIndexOfSoldToUserId: Int = getColumnIndexOrThrow(_stmt, "soldToUserId")
        val _columnIndexOfSoldPrice: Int = getColumnIndexOrThrow(_stmt, "soldPrice")
        val _columnIndexOfPreviousOwnerId: Int = getColumnIndexOrThrow(_stmt, "previousOwnerId")
        val _columnIndexOfTransferredAt: Int = getColumnIndexOrThrow(_stmt, "transferredAt")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfIsDeleted: Int = getColumnIndexOrThrow(_stmt, "isDeleted")
        val _columnIndexOfDeletedAt: Int = getColumnIndexOrThrow(_stmt, "deletedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _result: FarmAssetEntity?
        if (_stmt.step()) {
          val _tmpAssetId: String
          _tmpAssetId = _stmt.getText(_columnIndexOfAssetId)
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpAssetType: String
          _tmpAssetType = _stmt.getText(_columnIndexOfAssetType)
          val _tmpCategory: String
          _tmpCategory = _stmt.getText(_columnIndexOfCategory)
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpIsShowcase: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsShowcase).toInt()
          _tmpIsShowcase = _tmp != 0
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
          val _tmpQuantity: Double
          _tmpQuantity = _stmt.getDouble(_columnIndexOfQuantity)
          val _tmpInitialQuantity: Double
          _tmpInitialQuantity = _stmt.getDouble(_columnIndexOfInitialQuantity)
          val _tmpUnit: String
          _tmpUnit = _stmt.getText(_columnIndexOfUnit)
          val _tmpBirthDate: Long?
          if (_stmt.isNull(_columnIndexOfBirthDate)) {
            _tmpBirthDate = null
          } else {
            _tmpBirthDate = _stmt.getLong(_columnIndexOfBirthDate)
          }
          val _tmpAgeWeeks: Int?
          if (_stmt.isNull(_columnIndexOfAgeWeeks)) {
            _tmpAgeWeeks = null
          } else {
            _tmpAgeWeeks = _stmt.getLong(_columnIndexOfAgeWeeks).toInt()
          }
          val _tmpBreed: String?
          if (_stmt.isNull(_columnIndexOfBreed)) {
            _tmpBreed = null
          } else {
            _tmpBreed = _stmt.getText(_columnIndexOfBreed)
          }
          val _tmpGender: String?
          if (_stmt.isNull(_columnIndexOfGender)) {
            _tmpGender = null
          } else {
            _tmpGender = _stmt.getText(_columnIndexOfGender)
          }
          val _tmpColor: String?
          if (_stmt.isNull(_columnIndexOfColor)) {
            _tmpColor = null
          } else {
            _tmpColor = _stmt.getText(_columnIndexOfColor)
          }
          val _tmpHealthStatus: String
          _tmpHealthStatus = _stmt.getText(_columnIndexOfHealthStatus)
          val _tmpRaisingPurpose: String?
          if (_stmt.isNull(_columnIndexOfRaisingPurpose)) {
            _tmpRaisingPurpose = null
          } else {
            _tmpRaisingPurpose = _stmt.getText(_columnIndexOfRaisingPurpose)
          }
          val _tmpDescription: String
          _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          val _tmpImageUrls: List<String>
          val _tmp_1: String?
          if (_stmt.isNull(_columnIndexOfImageUrls)) {
            _tmp_1 = null
          } else {
            _tmp_1 = _stmt.getText(_columnIndexOfImageUrls)
          }
          val _tmp_2: List<String>? = AppDatabase.Converters.toStringList(_tmp_1)
          if (_tmp_2 == null) {
            error("Expected NON-NULL 'kotlin.collections.List<kotlin.String>', but it was NULL.")
          } else {
            _tmpImageUrls = _tmp_2
          }
          val _tmpNotes: String?
          if (_stmt.isNull(_columnIndexOfNotes)) {
            _tmpNotes = null
          } else {
            _tmpNotes = _stmt.getText(_columnIndexOfNotes)
          }
          val _tmpLifecycleSubStage: String?
          if (_stmt.isNull(_columnIndexOfLifecycleSubStage)) {
            _tmpLifecycleSubStage = null
          } else {
            _tmpLifecycleSubStage = _stmt.getText(_columnIndexOfLifecycleSubStage)
          }
          val _tmpParentIdsJson: String?
          if (_stmt.isNull(_columnIndexOfParentIdsJson)) {
            _tmpParentIdsJson = null
          } else {
            _tmpParentIdsJson = _stmt.getText(_columnIndexOfParentIdsJson)
          }
          val _tmpBatchId: String?
          if (_stmt.isNull(_columnIndexOfBatchId)) {
            _tmpBatchId = null
          } else {
            _tmpBatchId = _stmt.getText(_columnIndexOfBatchId)
          }
          val _tmpOrigin: String?
          if (_stmt.isNull(_columnIndexOfOrigin)) {
            _tmpOrigin = null
          } else {
            _tmpOrigin = _stmt.getText(_columnIndexOfOrigin)
          }
          val _tmpBirdCode: String?
          if (_stmt.isNull(_columnIndexOfBirdCode)) {
            _tmpBirdCode = null
          } else {
            _tmpBirdCode = _stmt.getText(_columnIndexOfBirdCode)
          }
          val _tmpAcquisitionPrice: Double?
          if (_stmt.isNull(_columnIndexOfAcquisitionPrice)) {
            _tmpAcquisitionPrice = null
          } else {
            _tmpAcquisitionPrice = _stmt.getDouble(_columnIndexOfAcquisitionPrice)
          }
          val _tmpAcquisitionDate: Long?
          if (_stmt.isNull(_columnIndexOfAcquisitionDate)) {
            _tmpAcquisitionDate = null
          } else {
            _tmpAcquisitionDate = _stmt.getLong(_columnIndexOfAcquisitionDate)
          }
          val _tmpAcquisitionSource: String?
          if (_stmt.isNull(_columnIndexOfAcquisitionSource)) {
            _tmpAcquisitionSource = null
          } else {
            _tmpAcquisitionSource = _stmt.getText(_columnIndexOfAcquisitionSource)
          }
          val _tmpAcquisitionSourceId: String?
          if (_stmt.isNull(_columnIndexOfAcquisitionSourceId)) {
            _tmpAcquisitionSourceId = null
          } else {
            _tmpAcquisitionSourceId = _stmt.getText(_columnIndexOfAcquisitionSourceId)
          }
          val _tmpAcquisitionNotes: String?
          if (_stmt.isNull(_columnIndexOfAcquisitionNotes)) {
            _tmpAcquisitionNotes = null
          } else {
            _tmpAcquisitionNotes = _stmt.getText(_columnIndexOfAcquisitionNotes)
          }
          val _tmpEstimatedValue: Double?
          if (_stmt.isNull(_columnIndexOfEstimatedValue)) {
            _tmpEstimatedValue = null
          } else {
            _tmpEstimatedValue = _stmt.getDouble(_columnIndexOfEstimatedValue)
          }
          val _tmpLastVaccinationDate: Long?
          if (_stmt.isNull(_columnIndexOfLastVaccinationDate)) {
            _tmpLastVaccinationDate = null
          } else {
            _tmpLastVaccinationDate = _stmt.getLong(_columnIndexOfLastVaccinationDate)
          }
          val _tmpNextVaccinationDate: Long?
          if (_stmt.isNull(_columnIndexOfNextVaccinationDate)) {
            _tmpNextVaccinationDate = null
          } else {
            _tmpNextVaccinationDate = _stmt.getLong(_columnIndexOfNextVaccinationDate)
          }
          val _tmpWeightGrams: Double?
          if (_stmt.isNull(_columnIndexOfWeightGrams)) {
            _tmpWeightGrams = null
          } else {
            _tmpWeightGrams = _stmt.getDouble(_columnIndexOfWeightGrams)
          }
          val _tmpMetadataJson: String
          _tmpMetadataJson = _stmt.getText(_columnIndexOfMetadataJson)
          val _tmpListedAt: Long?
          if (_stmt.isNull(_columnIndexOfListedAt)) {
            _tmpListedAt = null
          } else {
            _tmpListedAt = _stmt.getLong(_columnIndexOfListedAt)
          }
          val _tmpListingId: String?
          if (_stmt.isNull(_columnIndexOfListingId)) {
            _tmpListingId = null
          } else {
            _tmpListingId = _stmt.getText(_columnIndexOfListingId)
          }
          val _tmpSoldAt: Long?
          if (_stmt.isNull(_columnIndexOfSoldAt)) {
            _tmpSoldAt = null
          } else {
            _tmpSoldAt = _stmt.getLong(_columnIndexOfSoldAt)
          }
          val _tmpSoldToUserId: String?
          if (_stmt.isNull(_columnIndexOfSoldToUserId)) {
            _tmpSoldToUserId = null
          } else {
            _tmpSoldToUserId = _stmt.getText(_columnIndexOfSoldToUserId)
          }
          val _tmpSoldPrice: Double?
          if (_stmt.isNull(_columnIndexOfSoldPrice)) {
            _tmpSoldPrice = null
          } else {
            _tmpSoldPrice = _stmt.getDouble(_columnIndexOfSoldPrice)
          }
          val _tmpPreviousOwnerId: String?
          if (_stmt.isNull(_columnIndexOfPreviousOwnerId)) {
            _tmpPreviousOwnerId = null
          } else {
            _tmpPreviousOwnerId = _stmt.getText(_columnIndexOfPreviousOwnerId)
          }
          val _tmpTransferredAt: Long?
          if (_stmt.isNull(_columnIndexOfTransferredAt)) {
            _tmpTransferredAt = null
          } else {
            _tmpTransferredAt = _stmt.getLong(_columnIndexOfTransferredAt)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpIsDeleted: Boolean
          val _tmp_3: Int
          _tmp_3 = _stmt.getLong(_columnIndexOfIsDeleted).toInt()
          _tmpIsDeleted = _tmp_3 != 0
          val _tmpDeletedAt: Long?
          if (_stmt.isNull(_columnIndexOfDeletedAt)) {
            _tmpDeletedAt = null
          } else {
            _tmpDeletedAt = _stmt.getLong(_columnIndexOfDeletedAt)
          }
          val _tmpDirty: Boolean
          val _tmp_4: Int
          _tmp_4 = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp_4 != 0
          _result =
              FarmAssetEntity(_tmpAssetId,_tmpFarmerId,_tmpName,_tmpAssetType,_tmpCategory,_tmpStatus,_tmpIsShowcase,_tmpLocationName,_tmpLatitude,_tmpLongitude,_tmpQuantity,_tmpInitialQuantity,_tmpUnit,_tmpBirthDate,_tmpAgeWeeks,_tmpBreed,_tmpGender,_tmpColor,_tmpHealthStatus,_tmpRaisingPurpose,_tmpDescription,_tmpImageUrls,_tmpNotes,_tmpLifecycleSubStage,_tmpParentIdsJson,_tmpBatchId,_tmpOrigin,_tmpBirdCode,_tmpAcquisitionPrice,_tmpAcquisitionDate,_tmpAcquisitionSource,_tmpAcquisitionSourceId,_tmpAcquisitionNotes,_tmpEstimatedValue,_tmpLastVaccinationDate,_tmpNextVaccinationDate,_tmpWeightGrams,_tmpMetadataJson,_tmpListedAt,_tmpListingId,_tmpSoldAt,_tmpSoldToUserId,_tmpSoldPrice,_tmpPreviousOwnerId,_tmpTransferredAt,_tmpCreatedAt,_tmpUpdatedAt,_tmpIsDeleted,_tmpDeletedAt,_tmpDirty)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getActiveAssetsOneShot(): List<FarmAssetEntity> {
    val _sql: String = "SELECT * FROM farm_assets WHERE status = 'ACTIVE' AND isDeleted = 0"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfAssetId: Int = getColumnIndexOrThrow(_stmt, "assetId")
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfAssetType: Int = getColumnIndexOrThrow(_stmt, "assetType")
        val _columnIndexOfCategory: Int = getColumnIndexOrThrow(_stmt, "category")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfIsShowcase: Int = getColumnIndexOrThrow(_stmt, "isShowcase")
        val _columnIndexOfLocationName: Int = getColumnIndexOrThrow(_stmt, "locationName")
        val _columnIndexOfLatitude: Int = getColumnIndexOrThrow(_stmt, "latitude")
        val _columnIndexOfLongitude: Int = getColumnIndexOrThrow(_stmt, "longitude")
        val _columnIndexOfQuantity: Int = getColumnIndexOrThrow(_stmt, "quantity")
        val _columnIndexOfInitialQuantity: Int = getColumnIndexOrThrow(_stmt, "initialQuantity")
        val _columnIndexOfUnit: Int = getColumnIndexOrThrow(_stmt, "unit")
        val _columnIndexOfBirthDate: Int = getColumnIndexOrThrow(_stmt, "birthDate")
        val _columnIndexOfAgeWeeks: Int = getColumnIndexOrThrow(_stmt, "ageWeeks")
        val _columnIndexOfBreed: Int = getColumnIndexOrThrow(_stmt, "breed")
        val _columnIndexOfGender: Int = getColumnIndexOrThrow(_stmt, "gender")
        val _columnIndexOfColor: Int = getColumnIndexOrThrow(_stmt, "color")
        val _columnIndexOfHealthStatus: Int = getColumnIndexOrThrow(_stmt, "healthStatus")
        val _columnIndexOfRaisingPurpose: Int = getColumnIndexOrThrow(_stmt, "raisingPurpose")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfImageUrls: Int = getColumnIndexOrThrow(_stmt, "imageUrls")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfLifecycleSubStage: Int = getColumnIndexOrThrow(_stmt, "lifecycleSubStage")
        val _columnIndexOfParentIdsJson: Int = getColumnIndexOrThrow(_stmt, "parentIdsJson")
        val _columnIndexOfBatchId: Int = getColumnIndexOrThrow(_stmt, "batchId")
        val _columnIndexOfOrigin: Int = getColumnIndexOrThrow(_stmt, "origin")
        val _columnIndexOfBirdCode: Int = getColumnIndexOrThrow(_stmt, "birdCode")
        val _columnIndexOfAcquisitionPrice: Int = getColumnIndexOrThrow(_stmt, "acquisitionPrice")
        val _columnIndexOfAcquisitionDate: Int = getColumnIndexOrThrow(_stmt, "acquisitionDate")
        val _columnIndexOfAcquisitionSource: Int = getColumnIndexOrThrow(_stmt, "acquisitionSource")
        val _columnIndexOfAcquisitionSourceId: Int = getColumnIndexOrThrow(_stmt,
            "acquisitionSourceId")
        val _columnIndexOfAcquisitionNotes: Int = getColumnIndexOrThrow(_stmt, "acquisitionNotes")
        val _columnIndexOfEstimatedValue: Int = getColumnIndexOrThrow(_stmt, "estimatedValue")
        val _columnIndexOfLastVaccinationDate: Int = getColumnIndexOrThrow(_stmt,
            "lastVaccinationDate")
        val _columnIndexOfNextVaccinationDate: Int = getColumnIndexOrThrow(_stmt,
            "nextVaccinationDate")
        val _columnIndexOfWeightGrams: Int = getColumnIndexOrThrow(_stmt, "weightGrams")
        val _columnIndexOfMetadataJson: Int = getColumnIndexOrThrow(_stmt, "metadataJson")
        val _columnIndexOfListedAt: Int = getColumnIndexOrThrow(_stmt, "listedAt")
        val _columnIndexOfListingId: Int = getColumnIndexOrThrow(_stmt, "listingId")
        val _columnIndexOfSoldAt: Int = getColumnIndexOrThrow(_stmt, "soldAt")
        val _columnIndexOfSoldToUserId: Int = getColumnIndexOrThrow(_stmt, "soldToUserId")
        val _columnIndexOfSoldPrice: Int = getColumnIndexOrThrow(_stmt, "soldPrice")
        val _columnIndexOfPreviousOwnerId: Int = getColumnIndexOrThrow(_stmt, "previousOwnerId")
        val _columnIndexOfTransferredAt: Int = getColumnIndexOrThrow(_stmt, "transferredAt")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfIsDeleted: Int = getColumnIndexOrThrow(_stmt, "isDeleted")
        val _columnIndexOfDeletedAt: Int = getColumnIndexOrThrow(_stmt, "deletedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _result: MutableList<FarmAssetEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: FarmAssetEntity
          val _tmpAssetId: String
          _tmpAssetId = _stmt.getText(_columnIndexOfAssetId)
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpAssetType: String
          _tmpAssetType = _stmt.getText(_columnIndexOfAssetType)
          val _tmpCategory: String
          _tmpCategory = _stmt.getText(_columnIndexOfCategory)
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpIsShowcase: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsShowcase).toInt()
          _tmpIsShowcase = _tmp != 0
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
          val _tmpQuantity: Double
          _tmpQuantity = _stmt.getDouble(_columnIndexOfQuantity)
          val _tmpInitialQuantity: Double
          _tmpInitialQuantity = _stmt.getDouble(_columnIndexOfInitialQuantity)
          val _tmpUnit: String
          _tmpUnit = _stmt.getText(_columnIndexOfUnit)
          val _tmpBirthDate: Long?
          if (_stmt.isNull(_columnIndexOfBirthDate)) {
            _tmpBirthDate = null
          } else {
            _tmpBirthDate = _stmt.getLong(_columnIndexOfBirthDate)
          }
          val _tmpAgeWeeks: Int?
          if (_stmt.isNull(_columnIndexOfAgeWeeks)) {
            _tmpAgeWeeks = null
          } else {
            _tmpAgeWeeks = _stmt.getLong(_columnIndexOfAgeWeeks).toInt()
          }
          val _tmpBreed: String?
          if (_stmt.isNull(_columnIndexOfBreed)) {
            _tmpBreed = null
          } else {
            _tmpBreed = _stmt.getText(_columnIndexOfBreed)
          }
          val _tmpGender: String?
          if (_stmt.isNull(_columnIndexOfGender)) {
            _tmpGender = null
          } else {
            _tmpGender = _stmt.getText(_columnIndexOfGender)
          }
          val _tmpColor: String?
          if (_stmt.isNull(_columnIndexOfColor)) {
            _tmpColor = null
          } else {
            _tmpColor = _stmt.getText(_columnIndexOfColor)
          }
          val _tmpHealthStatus: String
          _tmpHealthStatus = _stmt.getText(_columnIndexOfHealthStatus)
          val _tmpRaisingPurpose: String?
          if (_stmt.isNull(_columnIndexOfRaisingPurpose)) {
            _tmpRaisingPurpose = null
          } else {
            _tmpRaisingPurpose = _stmt.getText(_columnIndexOfRaisingPurpose)
          }
          val _tmpDescription: String
          _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          val _tmpImageUrls: List<String>
          val _tmp_1: String?
          if (_stmt.isNull(_columnIndexOfImageUrls)) {
            _tmp_1 = null
          } else {
            _tmp_1 = _stmt.getText(_columnIndexOfImageUrls)
          }
          val _tmp_2: List<String>? = AppDatabase.Converters.toStringList(_tmp_1)
          if (_tmp_2 == null) {
            error("Expected NON-NULL 'kotlin.collections.List<kotlin.String>', but it was NULL.")
          } else {
            _tmpImageUrls = _tmp_2
          }
          val _tmpNotes: String?
          if (_stmt.isNull(_columnIndexOfNotes)) {
            _tmpNotes = null
          } else {
            _tmpNotes = _stmt.getText(_columnIndexOfNotes)
          }
          val _tmpLifecycleSubStage: String?
          if (_stmt.isNull(_columnIndexOfLifecycleSubStage)) {
            _tmpLifecycleSubStage = null
          } else {
            _tmpLifecycleSubStage = _stmt.getText(_columnIndexOfLifecycleSubStage)
          }
          val _tmpParentIdsJson: String?
          if (_stmt.isNull(_columnIndexOfParentIdsJson)) {
            _tmpParentIdsJson = null
          } else {
            _tmpParentIdsJson = _stmt.getText(_columnIndexOfParentIdsJson)
          }
          val _tmpBatchId: String?
          if (_stmt.isNull(_columnIndexOfBatchId)) {
            _tmpBatchId = null
          } else {
            _tmpBatchId = _stmt.getText(_columnIndexOfBatchId)
          }
          val _tmpOrigin: String?
          if (_stmt.isNull(_columnIndexOfOrigin)) {
            _tmpOrigin = null
          } else {
            _tmpOrigin = _stmt.getText(_columnIndexOfOrigin)
          }
          val _tmpBirdCode: String?
          if (_stmt.isNull(_columnIndexOfBirdCode)) {
            _tmpBirdCode = null
          } else {
            _tmpBirdCode = _stmt.getText(_columnIndexOfBirdCode)
          }
          val _tmpAcquisitionPrice: Double?
          if (_stmt.isNull(_columnIndexOfAcquisitionPrice)) {
            _tmpAcquisitionPrice = null
          } else {
            _tmpAcquisitionPrice = _stmt.getDouble(_columnIndexOfAcquisitionPrice)
          }
          val _tmpAcquisitionDate: Long?
          if (_stmt.isNull(_columnIndexOfAcquisitionDate)) {
            _tmpAcquisitionDate = null
          } else {
            _tmpAcquisitionDate = _stmt.getLong(_columnIndexOfAcquisitionDate)
          }
          val _tmpAcquisitionSource: String?
          if (_stmt.isNull(_columnIndexOfAcquisitionSource)) {
            _tmpAcquisitionSource = null
          } else {
            _tmpAcquisitionSource = _stmt.getText(_columnIndexOfAcquisitionSource)
          }
          val _tmpAcquisitionSourceId: String?
          if (_stmt.isNull(_columnIndexOfAcquisitionSourceId)) {
            _tmpAcquisitionSourceId = null
          } else {
            _tmpAcquisitionSourceId = _stmt.getText(_columnIndexOfAcquisitionSourceId)
          }
          val _tmpAcquisitionNotes: String?
          if (_stmt.isNull(_columnIndexOfAcquisitionNotes)) {
            _tmpAcquisitionNotes = null
          } else {
            _tmpAcquisitionNotes = _stmt.getText(_columnIndexOfAcquisitionNotes)
          }
          val _tmpEstimatedValue: Double?
          if (_stmt.isNull(_columnIndexOfEstimatedValue)) {
            _tmpEstimatedValue = null
          } else {
            _tmpEstimatedValue = _stmt.getDouble(_columnIndexOfEstimatedValue)
          }
          val _tmpLastVaccinationDate: Long?
          if (_stmt.isNull(_columnIndexOfLastVaccinationDate)) {
            _tmpLastVaccinationDate = null
          } else {
            _tmpLastVaccinationDate = _stmt.getLong(_columnIndexOfLastVaccinationDate)
          }
          val _tmpNextVaccinationDate: Long?
          if (_stmt.isNull(_columnIndexOfNextVaccinationDate)) {
            _tmpNextVaccinationDate = null
          } else {
            _tmpNextVaccinationDate = _stmt.getLong(_columnIndexOfNextVaccinationDate)
          }
          val _tmpWeightGrams: Double?
          if (_stmt.isNull(_columnIndexOfWeightGrams)) {
            _tmpWeightGrams = null
          } else {
            _tmpWeightGrams = _stmt.getDouble(_columnIndexOfWeightGrams)
          }
          val _tmpMetadataJson: String
          _tmpMetadataJson = _stmt.getText(_columnIndexOfMetadataJson)
          val _tmpListedAt: Long?
          if (_stmt.isNull(_columnIndexOfListedAt)) {
            _tmpListedAt = null
          } else {
            _tmpListedAt = _stmt.getLong(_columnIndexOfListedAt)
          }
          val _tmpListingId: String?
          if (_stmt.isNull(_columnIndexOfListingId)) {
            _tmpListingId = null
          } else {
            _tmpListingId = _stmt.getText(_columnIndexOfListingId)
          }
          val _tmpSoldAt: Long?
          if (_stmt.isNull(_columnIndexOfSoldAt)) {
            _tmpSoldAt = null
          } else {
            _tmpSoldAt = _stmt.getLong(_columnIndexOfSoldAt)
          }
          val _tmpSoldToUserId: String?
          if (_stmt.isNull(_columnIndexOfSoldToUserId)) {
            _tmpSoldToUserId = null
          } else {
            _tmpSoldToUserId = _stmt.getText(_columnIndexOfSoldToUserId)
          }
          val _tmpSoldPrice: Double?
          if (_stmt.isNull(_columnIndexOfSoldPrice)) {
            _tmpSoldPrice = null
          } else {
            _tmpSoldPrice = _stmt.getDouble(_columnIndexOfSoldPrice)
          }
          val _tmpPreviousOwnerId: String?
          if (_stmt.isNull(_columnIndexOfPreviousOwnerId)) {
            _tmpPreviousOwnerId = null
          } else {
            _tmpPreviousOwnerId = _stmt.getText(_columnIndexOfPreviousOwnerId)
          }
          val _tmpTransferredAt: Long?
          if (_stmt.isNull(_columnIndexOfTransferredAt)) {
            _tmpTransferredAt = null
          } else {
            _tmpTransferredAt = _stmt.getLong(_columnIndexOfTransferredAt)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpIsDeleted: Boolean
          val _tmp_3: Int
          _tmp_3 = _stmt.getLong(_columnIndexOfIsDeleted).toInt()
          _tmpIsDeleted = _tmp_3 != 0
          val _tmpDeletedAt: Long?
          if (_stmt.isNull(_columnIndexOfDeletedAt)) {
            _tmpDeletedAt = null
          } else {
            _tmpDeletedAt = _stmt.getLong(_columnIndexOfDeletedAt)
          }
          val _tmpDirty: Boolean
          val _tmp_4: Int
          _tmp_4 = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp_4 != 0
          _item =
              FarmAssetEntity(_tmpAssetId,_tmpFarmerId,_tmpName,_tmpAssetType,_tmpCategory,_tmpStatus,_tmpIsShowcase,_tmpLocationName,_tmpLatitude,_tmpLongitude,_tmpQuantity,_tmpInitialQuantity,_tmpUnit,_tmpBirthDate,_tmpAgeWeeks,_tmpBreed,_tmpGender,_tmpColor,_tmpHealthStatus,_tmpRaisingPurpose,_tmpDescription,_tmpImageUrls,_tmpNotes,_tmpLifecycleSubStage,_tmpParentIdsJson,_tmpBatchId,_tmpOrigin,_tmpBirdCode,_tmpAcquisitionPrice,_tmpAcquisitionDate,_tmpAcquisitionSource,_tmpAcquisitionSourceId,_tmpAcquisitionNotes,_tmpEstimatedValue,_tmpLastVaccinationDate,_tmpNextVaccinationDate,_tmpWeightGrams,_tmpMetadataJson,_tmpListedAt,_tmpListingId,_tmpSoldAt,_tmpSoldToUserId,_tmpSoldPrice,_tmpPreviousOwnerId,_tmpTransferredAt,_tmpCreatedAt,_tmpUpdatedAt,_tmpIsDeleted,_tmpDeletedAt,_tmpDirty)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getAssetsByFarmer(farmerId: String): Flow<List<FarmAssetEntity>> {
    val _sql: String =
        "SELECT * FROM farm_assets WHERE farmerId = ? AND isDeleted = 0 ORDER BY createdAt DESC"
    return createFlow(__db, false, arrayOf("farm_assets")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, farmerId)
        val _columnIndexOfAssetId: Int = getColumnIndexOrThrow(_stmt, "assetId")
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfAssetType: Int = getColumnIndexOrThrow(_stmt, "assetType")
        val _columnIndexOfCategory: Int = getColumnIndexOrThrow(_stmt, "category")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfIsShowcase: Int = getColumnIndexOrThrow(_stmt, "isShowcase")
        val _columnIndexOfLocationName: Int = getColumnIndexOrThrow(_stmt, "locationName")
        val _columnIndexOfLatitude: Int = getColumnIndexOrThrow(_stmt, "latitude")
        val _columnIndexOfLongitude: Int = getColumnIndexOrThrow(_stmt, "longitude")
        val _columnIndexOfQuantity: Int = getColumnIndexOrThrow(_stmt, "quantity")
        val _columnIndexOfInitialQuantity: Int = getColumnIndexOrThrow(_stmt, "initialQuantity")
        val _columnIndexOfUnit: Int = getColumnIndexOrThrow(_stmt, "unit")
        val _columnIndexOfBirthDate: Int = getColumnIndexOrThrow(_stmt, "birthDate")
        val _columnIndexOfAgeWeeks: Int = getColumnIndexOrThrow(_stmt, "ageWeeks")
        val _columnIndexOfBreed: Int = getColumnIndexOrThrow(_stmt, "breed")
        val _columnIndexOfGender: Int = getColumnIndexOrThrow(_stmt, "gender")
        val _columnIndexOfColor: Int = getColumnIndexOrThrow(_stmt, "color")
        val _columnIndexOfHealthStatus: Int = getColumnIndexOrThrow(_stmt, "healthStatus")
        val _columnIndexOfRaisingPurpose: Int = getColumnIndexOrThrow(_stmt, "raisingPurpose")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfImageUrls: Int = getColumnIndexOrThrow(_stmt, "imageUrls")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfLifecycleSubStage: Int = getColumnIndexOrThrow(_stmt, "lifecycleSubStage")
        val _columnIndexOfParentIdsJson: Int = getColumnIndexOrThrow(_stmt, "parentIdsJson")
        val _columnIndexOfBatchId: Int = getColumnIndexOrThrow(_stmt, "batchId")
        val _columnIndexOfOrigin: Int = getColumnIndexOrThrow(_stmt, "origin")
        val _columnIndexOfBirdCode: Int = getColumnIndexOrThrow(_stmt, "birdCode")
        val _columnIndexOfAcquisitionPrice: Int = getColumnIndexOrThrow(_stmt, "acquisitionPrice")
        val _columnIndexOfAcquisitionDate: Int = getColumnIndexOrThrow(_stmt, "acquisitionDate")
        val _columnIndexOfAcquisitionSource: Int = getColumnIndexOrThrow(_stmt, "acquisitionSource")
        val _columnIndexOfAcquisitionSourceId: Int = getColumnIndexOrThrow(_stmt,
            "acquisitionSourceId")
        val _columnIndexOfAcquisitionNotes: Int = getColumnIndexOrThrow(_stmt, "acquisitionNotes")
        val _columnIndexOfEstimatedValue: Int = getColumnIndexOrThrow(_stmt, "estimatedValue")
        val _columnIndexOfLastVaccinationDate: Int = getColumnIndexOrThrow(_stmt,
            "lastVaccinationDate")
        val _columnIndexOfNextVaccinationDate: Int = getColumnIndexOrThrow(_stmt,
            "nextVaccinationDate")
        val _columnIndexOfWeightGrams: Int = getColumnIndexOrThrow(_stmt, "weightGrams")
        val _columnIndexOfMetadataJson: Int = getColumnIndexOrThrow(_stmt, "metadataJson")
        val _columnIndexOfListedAt: Int = getColumnIndexOrThrow(_stmt, "listedAt")
        val _columnIndexOfListingId: Int = getColumnIndexOrThrow(_stmt, "listingId")
        val _columnIndexOfSoldAt: Int = getColumnIndexOrThrow(_stmt, "soldAt")
        val _columnIndexOfSoldToUserId: Int = getColumnIndexOrThrow(_stmt, "soldToUserId")
        val _columnIndexOfSoldPrice: Int = getColumnIndexOrThrow(_stmt, "soldPrice")
        val _columnIndexOfPreviousOwnerId: Int = getColumnIndexOrThrow(_stmt, "previousOwnerId")
        val _columnIndexOfTransferredAt: Int = getColumnIndexOrThrow(_stmt, "transferredAt")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfIsDeleted: Int = getColumnIndexOrThrow(_stmt, "isDeleted")
        val _columnIndexOfDeletedAt: Int = getColumnIndexOrThrow(_stmt, "deletedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _result: MutableList<FarmAssetEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: FarmAssetEntity
          val _tmpAssetId: String
          _tmpAssetId = _stmt.getText(_columnIndexOfAssetId)
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpAssetType: String
          _tmpAssetType = _stmt.getText(_columnIndexOfAssetType)
          val _tmpCategory: String
          _tmpCategory = _stmt.getText(_columnIndexOfCategory)
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpIsShowcase: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsShowcase).toInt()
          _tmpIsShowcase = _tmp != 0
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
          val _tmpQuantity: Double
          _tmpQuantity = _stmt.getDouble(_columnIndexOfQuantity)
          val _tmpInitialQuantity: Double
          _tmpInitialQuantity = _stmt.getDouble(_columnIndexOfInitialQuantity)
          val _tmpUnit: String
          _tmpUnit = _stmt.getText(_columnIndexOfUnit)
          val _tmpBirthDate: Long?
          if (_stmt.isNull(_columnIndexOfBirthDate)) {
            _tmpBirthDate = null
          } else {
            _tmpBirthDate = _stmt.getLong(_columnIndexOfBirthDate)
          }
          val _tmpAgeWeeks: Int?
          if (_stmt.isNull(_columnIndexOfAgeWeeks)) {
            _tmpAgeWeeks = null
          } else {
            _tmpAgeWeeks = _stmt.getLong(_columnIndexOfAgeWeeks).toInt()
          }
          val _tmpBreed: String?
          if (_stmt.isNull(_columnIndexOfBreed)) {
            _tmpBreed = null
          } else {
            _tmpBreed = _stmt.getText(_columnIndexOfBreed)
          }
          val _tmpGender: String?
          if (_stmt.isNull(_columnIndexOfGender)) {
            _tmpGender = null
          } else {
            _tmpGender = _stmt.getText(_columnIndexOfGender)
          }
          val _tmpColor: String?
          if (_stmt.isNull(_columnIndexOfColor)) {
            _tmpColor = null
          } else {
            _tmpColor = _stmt.getText(_columnIndexOfColor)
          }
          val _tmpHealthStatus: String
          _tmpHealthStatus = _stmt.getText(_columnIndexOfHealthStatus)
          val _tmpRaisingPurpose: String?
          if (_stmt.isNull(_columnIndexOfRaisingPurpose)) {
            _tmpRaisingPurpose = null
          } else {
            _tmpRaisingPurpose = _stmt.getText(_columnIndexOfRaisingPurpose)
          }
          val _tmpDescription: String
          _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          val _tmpImageUrls: List<String>
          val _tmp_1: String?
          if (_stmt.isNull(_columnIndexOfImageUrls)) {
            _tmp_1 = null
          } else {
            _tmp_1 = _stmt.getText(_columnIndexOfImageUrls)
          }
          val _tmp_2: List<String>? = AppDatabase.Converters.toStringList(_tmp_1)
          if (_tmp_2 == null) {
            error("Expected NON-NULL 'kotlin.collections.List<kotlin.String>', but it was NULL.")
          } else {
            _tmpImageUrls = _tmp_2
          }
          val _tmpNotes: String?
          if (_stmt.isNull(_columnIndexOfNotes)) {
            _tmpNotes = null
          } else {
            _tmpNotes = _stmt.getText(_columnIndexOfNotes)
          }
          val _tmpLifecycleSubStage: String?
          if (_stmt.isNull(_columnIndexOfLifecycleSubStage)) {
            _tmpLifecycleSubStage = null
          } else {
            _tmpLifecycleSubStage = _stmt.getText(_columnIndexOfLifecycleSubStage)
          }
          val _tmpParentIdsJson: String?
          if (_stmt.isNull(_columnIndexOfParentIdsJson)) {
            _tmpParentIdsJson = null
          } else {
            _tmpParentIdsJson = _stmt.getText(_columnIndexOfParentIdsJson)
          }
          val _tmpBatchId: String?
          if (_stmt.isNull(_columnIndexOfBatchId)) {
            _tmpBatchId = null
          } else {
            _tmpBatchId = _stmt.getText(_columnIndexOfBatchId)
          }
          val _tmpOrigin: String?
          if (_stmt.isNull(_columnIndexOfOrigin)) {
            _tmpOrigin = null
          } else {
            _tmpOrigin = _stmt.getText(_columnIndexOfOrigin)
          }
          val _tmpBirdCode: String?
          if (_stmt.isNull(_columnIndexOfBirdCode)) {
            _tmpBirdCode = null
          } else {
            _tmpBirdCode = _stmt.getText(_columnIndexOfBirdCode)
          }
          val _tmpAcquisitionPrice: Double?
          if (_stmt.isNull(_columnIndexOfAcquisitionPrice)) {
            _tmpAcquisitionPrice = null
          } else {
            _tmpAcquisitionPrice = _stmt.getDouble(_columnIndexOfAcquisitionPrice)
          }
          val _tmpAcquisitionDate: Long?
          if (_stmt.isNull(_columnIndexOfAcquisitionDate)) {
            _tmpAcquisitionDate = null
          } else {
            _tmpAcquisitionDate = _stmt.getLong(_columnIndexOfAcquisitionDate)
          }
          val _tmpAcquisitionSource: String?
          if (_stmt.isNull(_columnIndexOfAcquisitionSource)) {
            _tmpAcquisitionSource = null
          } else {
            _tmpAcquisitionSource = _stmt.getText(_columnIndexOfAcquisitionSource)
          }
          val _tmpAcquisitionSourceId: String?
          if (_stmt.isNull(_columnIndexOfAcquisitionSourceId)) {
            _tmpAcquisitionSourceId = null
          } else {
            _tmpAcquisitionSourceId = _stmt.getText(_columnIndexOfAcquisitionSourceId)
          }
          val _tmpAcquisitionNotes: String?
          if (_stmt.isNull(_columnIndexOfAcquisitionNotes)) {
            _tmpAcquisitionNotes = null
          } else {
            _tmpAcquisitionNotes = _stmt.getText(_columnIndexOfAcquisitionNotes)
          }
          val _tmpEstimatedValue: Double?
          if (_stmt.isNull(_columnIndexOfEstimatedValue)) {
            _tmpEstimatedValue = null
          } else {
            _tmpEstimatedValue = _stmt.getDouble(_columnIndexOfEstimatedValue)
          }
          val _tmpLastVaccinationDate: Long?
          if (_stmt.isNull(_columnIndexOfLastVaccinationDate)) {
            _tmpLastVaccinationDate = null
          } else {
            _tmpLastVaccinationDate = _stmt.getLong(_columnIndexOfLastVaccinationDate)
          }
          val _tmpNextVaccinationDate: Long?
          if (_stmt.isNull(_columnIndexOfNextVaccinationDate)) {
            _tmpNextVaccinationDate = null
          } else {
            _tmpNextVaccinationDate = _stmt.getLong(_columnIndexOfNextVaccinationDate)
          }
          val _tmpWeightGrams: Double?
          if (_stmt.isNull(_columnIndexOfWeightGrams)) {
            _tmpWeightGrams = null
          } else {
            _tmpWeightGrams = _stmt.getDouble(_columnIndexOfWeightGrams)
          }
          val _tmpMetadataJson: String
          _tmpMetadataJson = _stmt.getText(_columnIndexOfMetadataJson)
          val _tmpListedAt: Long?
          if (_stmt.isNull(_columnIndexOfListedAt)) {
            _tmpListedAt = null
          } else {
            _tmpListedAt = _stmt.getLong(_columnIndexOfListedAt)
          }
          val _tmpListingId: String?
          if (_stmt.isNull(_columnIndexOfListingId)) {
            _tmpListingId = null
          } else {
            _tmpListingId = _stmt.getText(_columnIndexOfListingId)
          }
          val _tmpSoldAt: Long?
          if (_stmt.isNull(_columnIndexOfSoldAt)) {
            _tmpSoldAt = null
          } else {
            _tmpSoldAt = _stmt.getLong(_columnIndexOfSoldAt)
          }
          val _tmpSoldToUserId: String?
          if (_stmt.isNull(_columnIndexOfSoldToUserId)) {
            _tmpSoldToUserId = null
          } else {
            _tmpSoldToUserId = _stmt.getText(_columnIndexOfSoldToUserId)
          }
          val _tmpSoldPrice: Double?
          if (_stmt.isNull(_columnIndexOfSoldPrice)) {
            _tmpSoldPrice = null
          } else {
            _tmpSoldPrice = _stmt.getDouble(_columnIndexOfSoldPrice)
          }
          val _tmpPreviousOwnerId: String?
          if (_stmt.isNull(_columnIndexOfPreviousOwnerId)) {
            _tmpPreviousOwnerId = null
          } else {
            _tmpPreviousOwnerId = _stmt.getText(_columnIndexOfPreviousOwnerId)
          }
          val _tmpTransferredAt: Long?
          if (_stmt.isNull(_columnIndexOfTransferredAt)) {
            _tmpTransferredAt = null
          } else {
            _tmpTransferredAt = _stmt.getLong(_columnIndexOfTransferredAt)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpIsDeleted: Boolean
          val _tmp_3: Int
          _tmp_3 = _stmt.getLong(_columnIndexOfIsDeleted).toInt()
          _tmpIsDeleted = _tmp_3 != 0
          val _tmpDeletedAt: Long?
          if (_stmt.isNull(_columnIndexOfDeletedAt)) {
            _tmpDeletedAt = null
          } else {
            _tmpDeletedAt = _stmt.getLong(_columnIndexOfDeletedAt)
          }
          val _tmpDirty: Boolean
          val _tmp_4: Int
          _tmp_4 = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp_4 != 0
          _item =
              FarmAssetEntity(_tmpAssetId,_tmpFarmerId,_tmpName,_tmpAssetType,_tmpCategory,_tmpStatus,_tmpIsShowcase,_tmpLocationName,_tmpLatitude,_tmpLongitude,_tmpQuantity,_tmpInitialQuantity,_tmpUnit,_tmpBirthDate,_tmpAgeWeeks,_tmpBreed,_tmpGender,_tmpColor,_tmpHealthStatus,_tmpRaisingPurpose,_tmpDescription,_tmpImageUrls,_tmpNotes,_tmpLifecycleSubStage,_tmpParentIdsJson,_tmpBatchId,_tmpOrigin,_tmpBirdCode,_tmpAcquisitionPrice,_tmpAcquisitionDate,_tmpAcquisitionSource,_tmpAcquisitionSourceId,_tmpAcquisitionNotes,_tmpEstimatedValue,_tmpLastVaccinationDate,_tmpNextVaccinationDate,_tmpWeightGrams,_tmpMetadataJson,_tmpListedAt,_tmpListingId,_tmpSoldAt,_tmpSoldToUserId,_tmpSoldPrice,_tmpPreviousOwnerId,_tmpTransferredAt,_tmpCreatedAt,_tmpUpdatedAt,_tmpIsDeleted,_tmpDeletedAt,_tmpDirty)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getAllAssets(farmerId: String): Flow<List<FarmAssetEntity>> {
    val _sql: String = "SELECT * FROM farm_assets WHERE farmerId = ? AND isDeleted = 0"
    return createFlow(__db, false, arrayOf("farm_assets")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, farmerId)
        val _columnIndexOfAssetId: Int = getColumnIndexOrThrow(_stmt, "assetId")
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfAssetType: Int = getColumnIndexOrThrow(_stmt, "assetType")
        val _columnIndexOfCategory: Int = getColumnIndexOrThrow(_stmt, "category")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfIsShowcase: Int = getColumnIndexOrThrow(_stmt, "isShowcase")
        val _columnIndexOfLocationName: Int = getColumnIndexOrThrow(_stmt, "locationName")
        val _columnIndexOfLatitude: Int = getColumnIndexOrThrow(_stmt, "latitude")
        val _columnIndexOfLongitude: Int = getColumnIndexOrThrow(_stmt, "longitude")
        val _columnIndexOfQuantity: Int = getColumnIndexOrThrow(_stmt, "quantity")
        val _columnIndexOfInitialQuantity: Int = getColumnIndexOrThrow(_stmt, "initialQuantity")
        val _columnIndexOfUnit: Int = getColumnIndexOrThrow(_stmt, "unit")
        val _columnIndexOfBirthDate: Int = getColumnIndexOrThrow(_stmt, "birthDate")
        val _columnIndexOfAgeWeeks: Int = getColumnIndexOrThrow(_stmt, "ageWeeks")
        val _columnIndexOfBreed: Int = getColumnIndexOrThrow(_stmt, "breed")
        val _columnIndexOfGender: Int = getColumnIndexOrThrow(_stmt, "gender")
        val _columnIndexOfColor: Int = getColumnIndexOrThrow(_stmt, "color")
        val _columnIndexOfHealthStatus: Int = getColumnIndexOrThrow(_stmt, "healthStatus")
        val _columnIndexOfRaisingPurpose: Int = getColumnIndexOrThrow(_stmt, "raisingPurpose")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfImageUrls: Int = getColumnIndexOrThrow(_stmt, "imageUrls")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfLifecycleSubStage: Int = getColumnIndexOrThrow(_stmt, "lifecycleSubStage")
        val _columnIndexOfParentIdsJson: Int = getColumnIndexOrThrow(_stmt, "parentIdsJson")
        val _columnIndexOfBatchId: Int = getColumnIndexOrThrow(_stmt, "batchId")
        val _columnIndexOfOrigin: Int = getColumnIndexOrThrow(_stmt, "origin")
        val _columnIndexOfBirdCode: Int = getColumnIndexOrThrow(_stmt, "birdCode")
        val _columnIndexOfAcquisitionPrice: Int = getColumnIndexOrThrow(_stmt, "acquisitionPrice")
        val _columnIndexOfAcquisitionDate: Int = getColumnIndexOrThrow(_stmt, "acquisitionDate")
        val _columnIndexOfAcquisitionSource: Int = getColumnIndexOrThrow(_stmt, "acquisitionSource")
        val _columnIndexOfAcquisitionSourceId: Int = getColumnIndexOrThrow(_stmt,
            "acquisitionSourceId")
        val _columnIndexOfAcquisitionNotes: Int = getColumnIndexOrThrow(_stmt, "acquisitionNotes")
        val _columnIndexOfEstimatedValue: Int = getColumnIndexOrThrow(_stmt, "estimatedValue")
        val _columnIndexOfLastVaccinationDate: Int = getColumnIndexOrThrow(_stmt,
            "lastVaccinationDate")
        val _columnIndexOfNextVaccinationDate: Int = getColumnIndexOrThrow(_stmt,
            "nextVaccinationDate")
        val _columnIndexOfWeightGrams: Int = getColumnIndexOrThrow(_stmt, "weightGrams")
        val _columnIndexOfMetadataJson: Int = getColumnIndexOrThrow(_stmt, "metadataJson")
        val _columnIndexOfListedAt: Int = getColumnIndexOrThrow(_stmt, "listedAt")
        val _columnIndexOfListingId: Int = getColumnIndexOrThrow(_stmt, "listingId")
        val _columnIndexOfSoldAt: Int = getColumnIndexOrThrow(_stmt, "soldAt")
        val _columnIndexOfSoldToUserId: Int = getColumnIndexOrThrow(_stmt, "soldToUserId")
        val _columnIndexOfSoldPrice: Int = getColumnIndexOrThrow(_stmt, "soldPrice")
        val _columnIndexOfPreviousOwnerId: Int = getColumnIndexOrThrow(_stmt, "previousOwnerId")
        val _columnIndexOfTransferredAt: Int = getColumnIndexOrThrow(_stmt, "transferredAt")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfIsDeleted: Int = getColumnIndexOrThrow(_stmt, "isDeleted")
        val _columnIndexOfDeletedAt: Int = getColumnIndexOrThrow(_stmt, "deletedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _result: MutableList<FarmAssetEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: FarmAssetEntity
          val _tmpAssetId: String
          _tmpAssetId = _stmt.getText(_columnIndexOfAssetId)
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpAssetType: String
          _tmpAssetType = _stmt.getText(_columnIndexOfAssetType)
          val _tmpCategory: String
          _tmpCategory = _stmt.getText(_columnIndexOfCategory)
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpIsShowcase: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsShowcase).toInt()
          _tmpIsShowcase = _tmp != 0
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
          val _tmpQuantity: Double
          _tmpQuantity = _stmt.getDouble(_columnIndexOfQuantity)
          val _tmpInitialQuantity: Double
          _tmpInitialQuantity = _stmt.getDouble(_columnIndexOfInitialQuantity)
          val _tmpUnit: String
          _tmpUnit = _stmt.getText(_columnIndexOfUnit)
          val _tmpBirthDate: Long?
          if (_stmt.isNull(_columnIndexOfBirthDate)) {
            _tmpBirthDate = null
          } else {
            _tmpBirthDate = _stmt.getLong(_columnIndexOfBirthDate)
          }
          val _tmpAgeWeeks: Int?
          if (_stmt.isNull(_columnIndexOfAgeWeeks)) {
            _tmpAgeWeeks = null
          } else {
            _tmpAgeWeeks = _stmt.getLong(_columnIndexOfAgeWeeks).toInt()
          }
          val _tmpBreed: String?
          if (_stmt.isNull(_columnIndexOfBreed)) {
            _tmpBreed = null
          } else {
            _tmpBreed = _stmt.getText(_columnIndexOfBreed)
          }
          val _tmpGender: String?
          if (_stmt.isNull(_columnIndexOfGender)) {
            _tmpGender = null
          } else {
            _tmpGender = _stmt.getText(_columnIndexOfGender)
          }
          val _tmpColor: String?
          if (_stmt.isNull(_columnIndexOfColor)) {
            _tmpColor = null
          } else {
            _tmpColor = _stmt.getText(_columnIndexOfColor)
          }
          val _tmpHealthStatus: String
          _tmpHealthStatus = _stmt.getText(_columnIndexOfHealthStatus)
          val _tmpRaisingPurpose: String?
          if (_stmt.isNull(_columnIndexOfRaisingPurpose)) {
            _tmpRaisingPurpose = null
          } else {
            _tmpRaisingPurpose = _stmt.getText(_columnIndexOfRaisingPurpose)
          }
          val _tmpDescription: String
          _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          val _tmpImageUrls: List<String>
          val _tmp_1: String?
          if (_stmt.isNull(_columnIndexOfImageUrls)) {
            _tmp_1 = null
          } else {
            _tmp_1 = _stmt.getText(_columnIndexOfImageUrls)
          }
          val _tmp_2: List<String>? = AppDatabase.Converters.toStringList(_tmp_1)
          if (_tmp_2 == null) {
            error("Expected NON-NULL 'kotlin.collections.List<kotlin.String>', but it was NULL.")
          } else {
            _tmpImageUrls = _tmp_2
          }
          val _tmpNotes: String?
          if (_stmt.isNull(_columnIndexOfNotes)) {
            _tmpNotes = null
          } else {
            _tmpNotes = _stmt.getText(_columnIndexOfNotes)
          }
          val _tmpLifecycleSubStage: String?
          if (_stmt.isNull(_columnIndexOfLifecycleSubStage)) {
            _tmpLifecycleSubStage = null
          } else {
            _tmpLifecycleSubStage = _stmt.getText(_columnIndexOfLifecycleSubStage)
          }
          val _tmpParentIdsJson: String?
          if (_stmt.isNull(_columnIndexOfParentIdsJson)) {
            _tmpParentIdsJson = null
          } else {
            _tmpParentIdsJson = _stmt.getText(_columnIndexOfParentIdsJson)
          }
          val _tmpBatchId: String?
          if (_stmt.isNull(_columnIndexOfBatchId)) {
            _tmpBatchId = null
          } else {
            _tmpBatchId = _stmt.getText(_columnIndexOfBatchId)
          }
          val _tmpOrigin: String?
          if (_stmt.isNull(_columnIndexOfOrigin)) {
            _tmpOrigin = null
          } else {
            _tmpOrigin = _stmt.getText(_columnIndexOfOrigin)
          }
          val _tmpBirdCode: String?
          if (_stmt.isNull(_columnIndexOfBirdCode)) {
            _tmpBirdCode = null
          } else {
            _tmpBirdCode = _stmt.getText(_columnIndexOfBirdCode)
          }
          val _tmpAcquisitionPrice: Double?
          if (_stmt.isNull(_columnIndexOfAcquisitionPrice)) {
            _tmpAcquisitionPrice = null
          } else {
            _tmpAcquisitionPrice = _stmt.getDouble(_columnIndexOfAcquisitionPrice)
          }
          val _tmpAcquisitionDate: Long?
          if (_stmt.isNull(_columnIndexOfAcquisitionDate)) {
            _tmpAcquisitionDate = null
          } else {
            _tmpAcquisitionDate = _stmt.getLong(_columnIndexOfAcquisitionDate)
          }
          val _tmpAcquisitionSource: String?
          if (_stmt.isNull(_columnIndexOfAcquisitionSource)) {
            _tmpAcquisitionSource = null
          } else {
            _tmpAcquisitionSource = _stmt.getText(_columnIndexOfAcquisitionSource)
          }
          val _tmpAcquisitionSourceId: String?
          if (_stmt.isNull(_columnIndexOfAcquisitionSourceId)) {
            _tmpAcquisitionSourceId = null
          } else {
            _tmpAcquisitionSourceId = _stmt.getText(_columnIndexOfAcquisitionSourceId)
          }
          val _tmpAcquisitionNotes: String?
          if (_stmt.isNull(_columnIndexOfAcquisitionNotes)) {
            _tmpAcquisitionNotes = null
          } else {
            _tmpAcquisitionNotes = _stmt.getText(_columnIndexOfAcquisitionNotes)
          }
          val _tmpEstimatedValue: Double?
          if (_stmt.isNull(_columnIndexOfEstimatedValue)) {
            _tmpEstimatedValue = null
          } else {
            _tmpEstimatedValue = _stmt.getDouble(_columnIndexOfEstimatedValue)
          }
          val _tmpLastVaccinationDate: Long?
          if (_stmt.isNull(_columnIndexOfLastVaccinationDate)) {
            _tmpLastVaccinationDate = null
          } else {
            _tmpLastVaccinationDate = _stmt.getLong(_columnIndexOfLastVaccinationDate)
          }
          val _tmpNextVaccinationDate: Long?
          if (_stmt.isNull(_columnIndexOfNextVaccinationDate)) {
            _tmpNextVaccinationDate = null
          } else {
            _tmpNextVaccinationDate = _stmt.getLong(_columnIndexOfNextVaccinationDate)
          }
          val _tmpWeightGrams: Double?
          if (_stmt.isNull(_columnIndexOfWeightGrams)) {
            _tmpWeightGrams = null
          } else {
            _tmpWeightGrams = _stmt.getDouble(_columnIndexOfWeightGrams)
          }
          val _tmpMetadataJson: String
          _tmpMetadataJson = _stmt.getText(_columnIndexOfMetadataJson)
          val _tmpListedAt: Long?
          if (_stmt.isNull(_columnIndexOfListedAt)) {
            _tmpListedAt = null
          } else {
            _tmpListedAt = _stmt.getLong(_columnIndexOfListedAt)
          }
          val _tmpListingId: String?
          if (_stmt.isNull(_columnIndexOfListingId)) {
            _tmpListingId = null
          } else {
            _tmpListingId = _stmt.getText(_columnIndexOfListingId)
          }
          val _tmpSoldAt: Long?
          if (_stmt.isNull(_columnIndexOfSoldAt)) {
            _tmpSoldAt = null
          } else {
            _tmpSoldAt = _stmt.getLong(_columnIndexOfSoldAt)
          }
          val _tmpSoldToUserId: String?
          if (_stmt.isNull(_columnIndexOfSoldToUserId)) {
            _tmpSoldToUserId = null
          } else {
            _tmpSoldToUserId = _stmt.getText(_columnIndexOfSoldToUserId)
          }
          val _tmpSoldPrice: Double?
          if (_stmt.isNull(_columnIndexOfSoldPrice)) {
            _tmpSoldPrice = null
          } else {
            _tmpSoldPrice = _stmt.getDouble(_columnIndexOfSoldPrice)
          }
          val _tmpPreviousOwnerId: String?
          if (_stmt.isNull(_columnIndexOfPreviousOwnerId)) {
            _tmpPreviousOwnerId = null
          } else {
            _tmpPreviousOwnerId = _stmt.getText(_columnIndexOfPreviousOwnerId)
          }
          val _tmpTransferredAt: Long?
          if (_stmt.isNull(_columnIndexOfTransferredAt)) {
            _tmpTransferredAt = null
          } else {
            _tmpTransferredAt = _stmt.getLong(_columnIndexOfTransferredAt)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpIsDeleted: Boolean
          val _tmp_3: Int
          _tmp_3 = _stmt.getLong(_columnIndexOfIsDeleted).toInt()
          _tmpIsDeleted = _tmp_3 != 0
          val _tmpDeletedAt: Long?
          if (_stmt.isNull(_columnIndexOfDeletedAt)) {
            _tmpDeletedAt = null
          } else {
            _tmpDeletedAt = _stmt.getLong(_columnIndexOfDeletedAt)
          }
          val _tmpDirty: Boolean
          val _tmp_4: Int
          _tmp_4 = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp_4 != 0
          _item =
              FarmAssetEntity(_tmpAssetId,_tmpFarmerId,_tmpName,_tmpAssetType,_tmpCategory,_tmpStatus,_tmpIsShowcase,_tmpLocationName,_tmpLatitude,_tmpLongitude,_tmpQuantity,_tmpInitialQuantity,_tmpUnit,_tmpBirthDate,_tmpAgeWeeks,_tmpBreed,_tmpGender,_tmpColor,_tmpHealthStatus,_tmpRaisingPurpose,_tmpDescription,_tmpImageUrls,_tmpNotes,_tmpLifecycleSubStage,_tmpParentIdsJson,_tmpBatchId,_tmpOrigin,_tmpBirdCode,_tmpAcquisitionPrice,_tmpAcquisitionDate,_tmpAcquisitionSource,_tmpAcquisitionSourceId,_tmpAcquisitionNotes,_tmpEstimatedValue,_tmpLastVaccinationDate,_tmpNextVaccinationDate,_tmpWeightGrams,_tmpMetadataJson,_tmpListedAt,_tmpListingId,_tmpSoldAt,_tmpSoldToUserId,_tmpSoldPrice,_tmpPreviousOwnerId,_tmpTransferredAt,_tmpCreatedAt,_tmpUpdatedAt,_tmpIsDeleted,_tmpDeletedAt,_tmpDirty)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getAssetsByType(farmerId: String, type: String): Flow<List<FarmAssetEntity>> {
    val _sql: String =
        "SELECT * FROM farm_assets WHERE farmerId = ? AND assetType = ? AND isDeleted = 0"
    return createFlow(__db, false, arrayOf("farm_assets")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, farmerId)
        _argIndex = 2
        _stmt.bindText(_argIndex, type)
        val _columnIndexOfAssetId: Int = getColumnIndexOrThrow(_stmt, "assetId")
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfAssetType: Int = getColumnIndexOrThrow(_stmt, "assetType")
        val _columnIndexOfCategory: Int = getColumnIndexOrThrow(_stmt, "category")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfIsShowcase: Int = getColumnIndexOrThrow(_stmt, "isShowcase")
        val _columnIndexOfLocationName: Int = getColumnIndexOrThrow(_stmt, "locationName")
        val _columnIndexOfLatitude: Int = getColumnIndexOrThrow(_stmt, "latitude")
        val _columnIndexOfLongitude: Int = getColumnIndexOrThrow(_stmt, "longitude")
        val _columnIndexOfQuantity: Int = getColumnIndexOrThrow(_stmt, "quantity")
        val _columnIndexOfInitialQuantity: Int = getColumnIndexOrThrow(_stmt, "initialQuantity")
        val _columnIndexOfUnit: Int = getColumnIndexOrThrow(_stmt, "unit")
        val _columnIndexOfBirthDate: Int = getColumnIndexOrThrow(_stmt, "birthDate")
        val _columnIndexOfAgeWeeks: Int = getColumnIndexOrThrow(_stmt, "ageWeeks")
        val _columnIndexOfBreed: Int = getColumnIndexOrThrow(_stmt, "breed")
        val _columnIndexOfGender: Int = getColumnIndexOrThrow(_stmt, "gender")
        val _columnIndexOfColor: Int = getColumnIndexOrThrow(_stmt, "color")
        val _columnIndexOfHealthStatus: Int = getColumnIndexOrThrow(_stmt, "healthStatus")
        val _columnIndexOfRaisingPurpose: Int = getColumnIndexOrThrow(_stmt, "raisingPurpose")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfImageUrls: Int = getColumnIndexOrThrow(_stmt, "imageUrls")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfLifecycleSubStage: Int = getColumnIndexOrThrow(_stmt, "lifecycleSubStage")
        val _columnIndexOfParentIdsJson: Int = getColumnIndexOrThrow(_stmt, "parentIdsJson")
        val _columnIndexOfBatchId: Int = getColumnIndexOrThrow(_stmt, "batchId")
        val _columnIndexOfOrigin: Int = getColumnIndexOrThrow(_stmt, "origin")
        val _columnIndexOfBirdCode: Int = getColumnIndexOrThrow(_stmt, "birdCode")
        val _columnIndexOfAcquisitionPrice: Int = getColumnIndexOrThrow(_stmt, "acquisitionPrice")
        val _columnIndexOfAcquisitionDate: Int = getColumnIndexOrThrow(_stmt, "acquisitionDate")
        val _columnIndexOfAcquisitionSource: Int = getColumnIndexOrThrow(_stmt, "acquisitionSource")
        val _columnIndexOfAcquisitionSourceId: Int = getColumnIndexOrThrow(_stmt,
            "acquisitionSourceId")
        val _columnIndexOfAcquisitionNotes: Int = getColumnIndexOrThrow(_stmt, "acquisitionNotes")
        val _columnIndexOfEstimatedValue: Int = getColumnIndexOrThrow(_stmt, "estimatedValue")
        val _columnIndexOfLastVaccinationDate: Int = getColumnIndexOrThrow(_stmt,
            "lastVaccinationDate")
        val _columnIndexOfNextVaccinationDate: Int = getColumnIndexOrThrow(_stmt,
            "nextVaccinationDate")
        val _columnIndexOfWeightGrams: Int = getColumnIndexOrThrow(_stmt, "weightGrams")
        val _columnIndexOfMetadataJson: Int = getColumnIndexOrThrow(_stmt, "metadataJson")
        val _columnIndexOfListedAt: Int = getColumnIndexOrThrow(_stmt, "listedAt")
        val _columnIndexOfListingId: Int = getColumnIndexOrThrow(_stmt, "listingId")
        val _columnIndexOfSoldAt: Int = getColumnIndexOrThrow(_stmt, "soldAt")
        val _columnIndexOfSoldToUserId: Int = getColumnIndexOrThrow(_stmt, "soldToUserId")
        val _columnIndexOfSoldPrice: Int = getColumnIndexOrThrow(_stmt, "soldPrice")
        val _columnIndexOfPreviousOwnerId: Int = getColumnIndexOrThrow(_stmt, "previousOwnerId")
        val _columnIndexOfTransferredAt: Int = getColumnIndexOrThrow(_stmt, "transferredAt")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfIsDeleted: Int = getColumnIndexOrThrow(_stmt, "isDeleted")
        val _columnIndexOfDeletedAt: Int = getColumnIndexOrThrow(_stmt, "deletedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _result: MutableList<FarmAssetEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: FarmAssetEntity
          val _tmpAssetId: String
          _tmpAssetId = _stmt.getText(_columnIndexOfAssetId)
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpAssetType: String
          _tmpAssetType = _stmt.getText(_columnIndexOfAssetType)
          val _tmpCategory: String
          _tmpCategory = _stmt.getText(_columnIndexOfCategory)
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpIsShowcase: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsShowcase).toInt()
          _tmpIsShowcase = _tmp != 0
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
          val _tmpQuantity: Double
          _tmpQuantity = _stmt.getDouble(_columnIndexOfQuantity)
          val _tmpInitialQuantity: Double
          _tmpInitialQuantity = _stmt.getDouble(_columnIndexOfInitialQuantity)
          val _tmpUnit: String
          _tmpUnit = _stmt.getText(_columnIndexOfUnit)
          val _tmpBirthDate: Long?
          if (_stmt.isNull(_columnIndexOfBirthDate)) {
            _tmpBirthDate = null
          } else {
            _tmpBirthDate = _stmt.getLong(_columnIndexOfBirthDate)
          }
          val _tmpAgeWeeks: Int?
          if (_stmt.isNull(_columnIndexOfAgeWeeks)) {
            _tmpAgeWeeks = null
          } else {
            _tmpAgeWeeks = _stmt.getLong(_columnIndexOfAgeWeeks).toInt()
          }
          val _tmpBreed: String?
          if (_stmt.isNull(_columnIndexOfBreed)) {
            _tmpBreed = null
          } else {
            _tmpBreed = _stmt.getText(_columnIndexOfBreed)
          }
          val _tmpGender: String?
          if (_stmt.isNull(_columnIndexOfGender)) {
            _tmpGender = null
          } else {
            _tmpGender = _stmt.getText(_columnIndexOfGender)
          }
          val _tmpColor: String?
          if (_stmt.isNull(_columnIndexOfColor)) {
            _tmpColor = null
          } else {
            _tmpColor = _stmt.getText(_columnIndexOfColor)
          }
          val _tmpHealthStatus: String
          _tmpHealthStatus = _stmt.getText(_columnIndexOfHealthStatus)
          val _tmpRaisingPurpose: String?
          if (_stmt.isNull(_columnIndexOfRaisingPurpose)) {
            _tmpRaisingPurpose = null
          } else {
            _tmpRaisingPurpose = _stmt.getText(_columnIndexOfRaisingPurpose)
          }
          val _tmpDescription: String
          _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          val _tmpImageUrls: List<String>
          val _tmp_1: String?
          if (_stmt.isNull(_columnIndexOfImageUrls)) {
            _tmp_1 = null
          } else {
            _tmp_1 = _stmt.getText(_columnIndexOfImageUrls)
          }
          val _tmp_2: List<String>? = AppDatabase.Converters.toStringList(_tmp_1)
          if (_tmp_2 == null) {
            error("Expected NON-NULL 'kotlin.collections.List<kotlin.String>', but it was NULL.")
          } else {
            _tmpImageUrls = _tmp_2
          }
          val _tmpNotes: String?
          if (_stmt.isNull(_columnIndexOfNotes)) {
            _tmpNotes = null
          } else {
            _tmpNotes = _stmt.getText(_columnIndexOfNotes)
          }
          val _tmpLifecycleSubStage: String?
          if (_stmt.isNull(_columnIndexOfLifecycleSubStage)) {
            _tmpLifecycleSubStage = null
          } else {
            _tmpLifecycleSubStage = _stmt.getText(_columnIndexOfLifecycleSubStage)
          }
          val _tmpParentIdsJson: String?
          if (_stmt.isNull(_columnIndexOfParentIdsJson)) {
            _tmpParentIdsJson = null
          } else {
            _tmpParentIdsJson = _stmt.getText(_columnIndexOfParentIdsJson)
          }
          val _tmpBatchId: String?
          if (_stmt.isNull(_columnIndexOfBatchId)) {
            _tmpBatchId = null
          } else {
            _tmpBatchId = _stmt.getText(_columnIndexOfBatchId)
          }
          val _tmpOrigin: String?
          if (_stmt.isNull(_columnIndexOfOrigin)) {
            _tmpOrigin = null
          } else {
            _tmpOrigin = _stmt.getText(_columnIndexOfOrigin)
          }
          val _tmpBirdCode: String?
          if (_stmt.isNull(_columnIndexOfBirdCode)) {
            _tmpBirdCode = null
          } else {
            _tmpBirdCode = _stmt.getText(_columnIndexOfBirdCode)
          }
          val _tmpAcquisitionPrice: Double?
          if (_stmt.isNull(_columnIndexOfAcquisitionPrice)) {
            _tmpAcquisitionPrice = null
          } else {
            _tmpAcquisitionPrice = _stmt.getDouble(_columnIndexOfAcquisitionPrice)
          }
          val _tmpAcquisitionDate: Long?
          if (_stmt.isNull(_columnIndexOfAcquisitionDate)) {
            _tmpAcquisitionDate = null
          } else {
            _tmpAcquisitionDate = _stmt.getLong(_columnIndexOfAcquisitionDate)
          }
          val _tmpAcquisitionSource: String?
          if (_stmt.isNull(_columnIndexOfAcquisitionSource)) {
            _tmpAcquisitionSource = null
          } else {
            _tmpAcquisitionSource = _stmt.getText(_columnIndexOfAcquisitionSource)
          }
          val _tmpAcquisitionSourceId: String?
          if (_stmt.isNull(_columnIndexOfAcquisitionSourceId)) {
            _tmpAcquisitionSourceId = null
          } else {
            _tmpAcquisitionSourceId = _stmt.getText(_columnIndexOfAcquisitionSourceId)
          }
          val _tmpAcquisitionNotes: String?
          if (_stmt.isNull(_columnIndexOfAcquisitionNotes)) {
            _tmpAcquisitionNotes = null
          } else {
            _tmpAcquisitionNotes = _stmt.getText(_columnIndexOfAcquisitionNotes)
          }
          val _tmpEstimatedValue: Double?
          if (_stmt.isNull(_columnIndexOfEstimatedValue)) {
            _tmpEstimatedValue = null
          } else {
            _tmpEstimatedValue = _stmt.getDouble(_columnIndexOfEstimatedValue)
          }
          val _tmpLastVaccinationDate: Long?
          if (_stmt.isNull(_columnIndexOfLastVaccinationDate)) {
            _tmpLastVaccinationDate = null
          } else {
            _tmpLastVaccinationDate = _stmt.getLong(_columnIndexOfLastVaccinationDate)
          }
          val _tmpNextVaccinationDate: Long?
          if (_stmt.isNull(_columnIndexOfNextVaccinationDate)) {
            _tmpNextVaccinationDate = null
          } else {
            _tmpNextVaccinationDate = _stmt.getLong(_columnIndexOfNextVaccinationDate)
          }
          val _tmpWeightGrams: Double?
          if (_stmt.isNull(_columnIndexOfWeightGrams)) {
            _tmpWeightGrams = null
          } else {
            _tmpWeightGrams = _stmt.getDouble(_columnIndexOfWeightGrams)
          }
          val _tmpMetadataJson: String
          _tmpMetadataJson = _stmt.getText(_columnIndexOfMetadataJson)
          val _tmpListedAt: Long?
          if (_stmt.isNull(_columnIndexOfListedAt)) {
            _tmpListedAt = null
          } else {
            _tmpListedAt = _stmt.getLong(_columnIndexOfListedAt)
          }
          val _tmpListingId: String?
          if (_stmt.isNull(_columnIndexOfListingId)) {
            _tmpListingId = null
          } else {
            _tmpListingId = _stmt.getText(_columnIndexOfListingId)
          }
          val _tmpSoldAt: Long?
          if (_stmt.isNull(_columnIndexOfSoldAt)) {
            _tmpSoldAt = null
          } else {
            _tmpSoldAt = _stmt.getLong(_columnIndexOfSoldAt)
          }
          val _tmpSoldToUserId: String?
          if (_stmt.isNull(_columnIndexOfSoldToUserId)) {
            _tmpSoldToUserId = null
          } else {
            _tmpSoldToUserId = _stmt.getText(_columnIndexOfSoldToUserId)
          }
          val _tmpSoldPrice: Double?
          if (_stmt.isNull(_columnIndexOfSoldPrice)) {
            _tmpSoldPrice = null
          } else {
            _tmpSoldPrice = _stmt.getDouble(_columnIndexOfSoldPrice)
          }
          val _tmpPreviousOwnerId: String?
          if (_stmt.isNull(_columnIndexOfPreviousOwnerId)) {
            _tmpPreviousOwnerId = null
          } else {
            _tmpPreviousOwnerId = _stmt.getText(_columnIndexOfPreviousOwnerId)
          }
          val _tmpTransferredAt: Long?
          if (_stmt.isNull(_columnIndexOfTransferredAt)) {
            _tmpTransferredAt = null
          } else {
            _tmpTransferredAt = _stmt.getLong(_columnIndexOfTransferredAt)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpIsDeleted: Boolean
          val _tmp_3: Int
          _tmp_3 = _stmt.getLong(_columnIndexOfIsDeleted).toInt()
          _tmpIsDeleted = _tmp_3 != 0
          val _tmpDeletedAt: Long?
          if (_stmt.isNull(_columnIndexOfDeletedAt)) {
            _tmpDeletedAt = null
          } else {
            _tmpDeletedAt = _stmt.getLong(_columnIndexOfDeletedAt)
          }
          val _tmpDirty: Boolean
          val _tmp_4: Int
          _tmp_4 = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp_4 != 0
          _item =
              FarmAssetEntity(_tmpAssetId,_tmpFarmerId,_tmpName,_tmpAssetType,_tmpCategory,_tmpStatus,_tmpIsShowcase,_tmpLocationName,_tmpLatitude,_tmpLongitude,_tmpQuantity,_tmpInitialQuantity,_tmpUnit,_tmpBirthDate,_tmpAgeWeeks,_tmpBreed,_tmpGender,_tmpColor,_tmpHealthStatus,_tmpRaisingPurpose,_tmpDescription,_tmpImageUrls,_tmpNotes,_tmpLifecycleSubStage,_tmpParentIdsJson,_tmpBatchId,_tmpOrigin,_tmpBirdCode,_tmpAcquisitionPrice,_tmpAcquisitionDate,_tmpAcquisitionSource,_tmpAcquisitionSourceId,_tmpAcquisitionNotes,_tmpEstimatedValue,_tmpLastVaccinationDate,_tmpNextVaccinationDate,_tmpWeightGrams,_tmpMetadataJson,_tmpListedAt,_tmpListingId,_tmpSoldAt,_tmpSoldToUserId,_tmpSoldPrice,_tmpPreviousOwnerId,_tmpTransferredAt,_tmpCreatedAt,_tmpUpdatedAt,_tmpIsDeleted,_tmpDeletedAt,_tmpDirty)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getShowcaseAssets(farmerId: String): Flow<List<FarmAssetEntity>> {
    val _sql: String =
        "SELECT * FROM farm_assets WHERE farmerId = ? AND isShowcase = 1 AND isDeleted = 0"
    return createFlow(__db, false, arrayOf("farm_assets")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, farmerId)
        val _columnIndexOfAssetId: Int = getColumnIndexOrThrow(_stmt, "assetId")
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfAssetType: Int = getColumnIndexOrThrow(_stmt, "assetType")
        val _columnIndexOfCategory: Int = getColumnIndexOrThrow(_stmt, "category")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfIsShowcase: Int = getColumnIndexOrThrow(_stmt, "isShowcase")
        val _columnIndexOfLocationName: Int = getColumnIndexOrThrow(_stmt, "locationName")
        val _columnIndexOfLatitude: Int = getColumnIndexOrThrow(_stmt, "latitude")
        val _columnIndexOfLongitude: Int = getColumnIndexOrThrow(_stmt, "longitude")
        val _columnIndexOfQuantity: Int = getColumnIndexOrThrow(_stmt, "quantity")
        val _columnIndexOfInitialQuantity: Int = getColumnIndexOrThrow(_stmt, "initialQuantity")
        val _columnIndexOfUnit: Int = getColumnIndexOrThrow(_stmt, "unit")
        val _columnIndexOfBirthDate: Int = getColumnIndexOrThrow(_stmt, "birthDate")
        val _columnIndexOfAgeWeeks: Int = getColumnIndexOrThrow(_stmt, "ageWeeks")
        val _columnIndexOfBreed: Int = getColumnIndexOrThrow(_stmt, "breed")
        val _columnIndexOfGender: Int = getColumnIndexOrThrow(_stmt, "gender")
        val _columnIndexOfColor: Int = getColumnIndexOrThrow(_stmt, "color")
        val _columnIndexOfHealthStatus: Int = getColumnIndexOrThrow(_stmt, "healthStatus")
        val _columnIndexOfRaisingPurpose: Int = getColumnIndexOrThrow(_stmt, "raisingPurpose")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfImageUrls: Int = getColumnIndexOrThrow(_stmt, "imageUrls")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfLifecycleSubStage: Int = getColumnIndexOrThrow(_stmt, "lifecycleSubStage")
        val _columnIndexOfParentIdsJson: Int = getColumnIndexOrThrow(_stmt, "parentIdsJson")
        val _columnIndexOfBatchId: Int = getColumnIndexOrThrow(_stmt, "batchId")
        val _columnIndexOfOrigin: Int = getColumnIndexOrThrow(_stmt, "origin")
        val _columnIndexOfBirdCode: Int = getColumnIndexOrThrow(_stmt, "birdCode")
        val _columnIndexOfAcquisitionPrice: Int = getColumnIndexOrThrow(_stmt, "acquisitionPrice")
        val _columnIndexOfAcquisitionDate: Int = getColumnIndexOrThrow(_stmt, "acquisitionDate")
        val _columnIndexOfAcquisitionSource: Int = getColumnIndexOrThrow(_stmt, "acquisitionSource")
        val _columnIndexOfAcquisitionSourceId: Int = getColumnIndexOrThrow(_stmt,
            "acquisitionSourceId")
        val _columnIndexOfAcquisitionNotes: Int = getColumnIndexOrThrow(_stmt, "acquisitionNotes")
        val _columnIndexOfEstimatedValue: Int = getColumnIndexOrThrow(_stmt, "estimatedValue")
        val _columnIndexOfLastVaccinationDate: Int = getColumnIndexOrThrow(_stmt,
            "lastVaccinationDate")
        val _columnIndexOfNextVaccinationDate: Int = getColumnIndexOrThrow(_stmt,
            "nextVaccinationDate")
        val _columnIndexOfWeightGrams: Int = getColumnIndexOrThrow(_stmt, "weightGrams")
        val _columnIndexOfMetadataJson: Int = getColumnIndexOrThrow(_stmt, "metadataJson")
        val _columnIndexOfListedAt: Int = getColumnIndexOrThrow(_stmt, "listedAt")
        val _columnIndexOfListingId: Int = getColumnIndexOrThrow(_stmt, "listingId")
        val _columnIndexOfSoldAt: Int = getColumnIndexOrThrow(_stmt, "soldAt")
        val _columnIndexOfSoldToUserId: Int = getColumnIndexOrThrow(_stmt, "soldToUserId")
        val _columnIndexOfSoldPrice: Int = getColumnIndexOrThrow(_stmt, "soldPrice")
        val _columnIndexOfPreviousOwnerId: Int = getColumnIndexOrThrow(_stmt, "previousOwnerId")
        val _columnIndexOfTransferredAt: Int = getColumnIndexOrThrow(_stmt, "transferredAt")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfIsDeleted: Int = getColumnIndexOrThrow(_stmt, "isDeleted")
        val _columnIndexOfDeletedAt: Int = getColumnIndexOrThrow(_stmt, "deletedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _result: MutableList<FarmAssetEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: FarmAssetEntity
          val _tmpAssetId: String
          _tmpAssetId = _stmt.getText(_columnIndexOfAssetId)
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpAssetType: String
          _tmpAssetType = _stmt.getText(_columnIndexOfAssetType)
          val _tmpCategory: String
          _tmpCategory = _stmt.getText(_columnIndexOfCategory)
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpIsShowcase: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsShowcase).toInt()
          _tmpIsShowcase = _tmp != 0
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
          val _tmpQuantity: Double
          _tmpQuantity = _stmt.getDouble(_columnIndexOfQuantity)
          val _tmpInitialQuantity: Double
          _tmpInitialQuantity = _stmt.getDouble(_columnIndexOfInitialQuantity)
          val _tmpUnit: String
          _tmpUnit = _stmt.getText(_columnIndexOfUnit)
          val _tmpBirthDate: Long?
          if (_stmt.isNull(_columnIndexOfBirthDate)) {
            _tmpBirthDate = null
          } else {
            _tmpBirthDate = _stmt.getLong(_columnIndexOfBirthDate)
          }
          val _tmpAgeWeeks: Int?
          if (_stmt.isNull(_columnIndexOfAgeWeeks)) {
            _tmpAgeWeeks = null
          } else {
            _tmpAgeWeeks = _stmt.getLong(_columnIndexOfAgeWeeks).toInt()
          }
          val _tmpBreed: String?
          if (_stmt.isNull(_columnIndexOfBreed)) {
            _tmpBreed = null
          } else {
            _tmpBreed = _stmt.getText(_columnIndexOfBreed)
          }
          val _tmpGender: String?
          if (_stmt.isNull(_columnIndexOfGender)) {
            _tmpGender = null
          } else {
            _tmpGender = _stmt.getText(_columnIndexOfGender)
          }
          val _tmpColor: String?
          if (_stmt.isNull(_columnIndexOfColor)) {
            _tmpColor = null
          } else {
            _tmpColor = _stmt.getText(_columnIndexOfColor)
          }
          val _tmpHealthStatus: String
          _tmpHealthStatus = _stmt.getText(_columnIndexOfHealthStatus)
          val _tmpRaisingPurpose: String?
          if (_stmt.isNull(_columnIndexOfRaisingPurpose)) {
            _tmpRaisingPurpose = null
          } else {
            _tmpRaisingPurpose = _stmt.getText(_columnIndexOfRaisingPurpose)
          }
          val _tmpDescription: String
          _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          val _tmpImageUrls: List<String>
          val _tmp_1: String?
          if (_stmt.isNull(_columnIndexOfImageUrls)) {
            _tmp_1 = null
          } else {
            _tmp_1 = _stmt.getText(_columnIndexOfImageUrls)
          }
          val _tmp_2: List<String>? = AppDatabase.Converters.toStringList(_tmp_1)
          if (_tmp_2 == null) {
            error("Expected NON-NULL 'kotlin.collections.List<kotlin.String>', but it was NULL.")
          } else {
            _tmpImageUrls = _tmp_2
          }
          val _tmpNotes: String?
          if (_stmt.isNull(_columnIndexOfNotes)) {
            _tmpNotes = null
          } else {
            _tmpNotes = _stmt.getText(_columnIndexOfNotes)
          }
          val _tmpLifecycleSubStage: String?
          if (_stmt.isNull(_columnIndexOfLifecycleSubStage)) {
            _tmpLifecycleSubStage = null
          } else {
            _tmpLifecycleSubStage = _stmt.getText(_columnIndexOfLifecycleSubStage)
          }
          val _tmpParentIdsJson: String?
          if (_stmt.isNull(_columnIndexOfParentIdsJson)) {
            _tmpParentIdsJson = null
          } else {
            _tmpParentIdsJson = _stmt.getText(_columnIndexOfParentIdsJson)
          }
          val _tmpBatchId: String?
          if (_stmt.isNull(_columnIndexOfBatchId)) {
            _tmpBatchId = null
          } else {
            _tmpBatchId = _stmt.getText(_columnIndexOfBatchId)
          }
          val _tmpOrigin: String?
          if (_stmt.isNull(_columnIndexOfOrigin)) {
            _tmpOrigin = null
          } else {
            _tmpOrigin = _stmt.getText(_columnIndexOfOrigin)
          }
          val _tmpBirdCode: String?
          if (_stmt.isNull(_columnIndexOfBirdCode)) {
            _tmpBirdCode = null
          } else {
            _tmpBirdCode = _stmt.getText(_columnIndexOfBirdCode)
          }
          val _tmpAcquisitionPrice: Double?
          if (_stmt.isNull(_columnIndexOfAcquisitionPrice)) {
            _tmpAcquisitionPrice = null
          } else {
            _tmpAcquisitionPrice = _stmt.getDouble(_columnIndexOfAcquisitionPrice)
          }
          val _tmpAcquisitionDate: Long?
          if (_stmt.isNull(_columnIndexOfAcquisitionDate)) {
            _tmpAcquisitionDate = null
          } else {
            _tmpAcquisitionDate = _stmt.getLong(_columnIndexOfAcquisitionDate)
          }
          val _tmpAcquisitionSource: String?
          if (_stmt.isNull(_columnIndexOfAcquisitionSource)) {
            _tmpAcquisitionSource = null
          } else {
            _tmpAcquisitionSource = _stmt.getText(_columnIndexOfAcquisitionSource)
          }
          val _tmpAcquisitionSourceId: String?
          if (_stmt.isNull(_columnIndexOfAcquisitionSourceId)) {
            _tmpAcquisitionSourceId = null
          } else {
            _tmpAcquisitionSourceId = _stmt.getText(_columnIndexOfAcquisitionSourceId)
          }
          val _tmpAcquisitionNotes: String?
          if (_stmt.isNull(_columnIndexOfAcquisitionNotes)) {
            _tmpAcquisitionNotes = null
          } else {
            _tmpAcquisitionNotes = _stmt.getText(_columnIndexOfAcquisitionNotes)
          }
          val _tmpEstimatedValue: Double?
          if (_stmt.isNull(_columnIndexOfEstimatedValue)) {
            _tmpEstimatedValue = null
          } else {
            _tmpEstimatedValue = _stmt.getDouble(_columnIndexOfEstimatedValue)
          }
          val _tmpLastVaccinationDate: Long?
          if (_stmt.isNull(_columnIndexOfLastVaccinationDate)) {
            _tmpLastVaccinationDate = null
          } else {
            _tmpLastVaccinationDate = _stmt.getLong(_columnIndexOfLastVaccinationDate)
          }
          val _tmpNextVaccinationDate: Long?
          if (_stmt.isNull(_columnIndexOfNextVaccinationDate)) {
            _tmpNextVaccinationDate = null
          } else {
            _tmpNextVaccinationDate = _stmt.getLong(_columnIndexOfNextVaccinationDate)
          }
          val _tmpWeightGrams: Double?
          if (_stmt.isNull(_columnIndexOfWeightGrams)) {
            _tmpWeightGrams = null
          } else {
            _tmpWeightGrams = _stmt.getDouble(_columnIndexOfWeightGrams)
          }
          val _tmpMetadataJson: String
          _tmpMetadataJson = _stmt.getText(_columnIndexOfMetadataJson)
          val _tmpListedAt: Long?
          if (_stmt.isNull(_columnIndexOfListedAt)) {
            _tmpListedAt = null
          } else {
            _tmpListedAt = _stmt.getLong(_columnIndexOfListedAt)
          }
          val _tmpListingId: String?
          if (_stmt.isNull(_columnIndexOfListingId)) {
            _tmpListingId = null
          } else {
            _tmpListingId = _stmt.getText(_columnIndexOfListingId)
          }
          val _tmpSoldAt: Long?
          if (_stmt.isNull(_columnIndexOfSoldAt)) {
            _tmpSoldAt = null
          } else {
            _tmpSoldAt = _stmt.getLong(_columnIndexOfSoldAt)
          }
          val _tmpSoldToUserId: String?
          if (_stmt.isNull(_columnIndexOfSoldToUserId)) {
            _tmpSoldToUserId = null
          } else {
            _tmpSoldToUserId = _stmt.getText(_columnIndexOfSoldToUserId)
          }
          val _tmpSoldPrice: Double?
          if (_stmt.isNull(_columnIndexOfSoldPrice)) {
            _tmpSoldPrice = null
          } else {
            _tmpSoldPrice = _stmt.getDouble(_columnIndexOfSoldPrice)
          }
          val _tmpPreviousOwnerId: String?
          if (_stmt.isNull(_columnIndexOfPreviousOwnerId)) {
            _tmpPreviousOwnerId = null
          } else {
            _tmpPreviousOwnerId = _stmt.getText(_columnIndexOfPreviousOwnerId)
          }
          val _tmpTransferredAt: Long?
          if (_stmt.isNull(_columnIndexOfTransferredAt)) {
            _tmpTransferredAt = null
          } else {
            _tmpTransferredAt = _stmt.getLong(_columnIndexOfTransferredAt)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpIsDeleted: Boolean
          val _tmp_3: Int
          _tmp_3 = _stmt.getLong(_columnIndexOfIsDeleted).toInt()
          _tmpIsDeleted = _tmp_3 != 0
          val _tmpDeletedAt: Long?
          if (_stmt.isNull(_columnIndexOfDeletedAt)) {
            _tmpDeletedAt = null
          } else {
            _tmpDeletedAt = _stmt.getLong(_columnIndexOfDeletedAt)
          }
          val _tmpDirty: Boolean
          val _tmp_4: Int
          _tmp_4 = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp_4 != 0
          _item =
              FarmAssetEntity(_tmpAssetId,_tmpFarmerId,_tmpName,_tmpAssetType,_tmpCategory,_tmpStatus,_tmpIsShowcase,_tmpLocationName,_tmpLatitude,_tmpLongitude,_tmpQuantity,_tmpInitialQuantity,_tmpUnit,_tmpBirthDate,_tmpAgeWeeks,_tmpBreed,_tmpGender,_tmpColor,_tmpHealthStatus,_tmpRaisingPurpose,_tmpDescription,_tmpImageUrls,_tmpNotes,_tmpLifecycleSubStage,_tmpParentIdsJson,_tmpBatchId,_tmpOrigin,_tmpBirdCode,_tmpAcquisitionPrice,_tmpAcquisitionDate,_tmpAcquisitionSource,_tmpAcquisitionSourceId,_tmpAcquisitionNotes,_tmpEstimatedValue,_tmpLastVaccinationDate,_tmpNextVaccinationDate,_tmpWeightGrams,_tmpMetadataJson,_tmpListedAt,_tmpListingId,_tmpSoldAt,_tmpSoldToUserId,_tmpSoldPrice,_tmpPreviousOwnerId,_tmpTransferredAt,_tmpCreatedAt,_tmpUpdatedAt,_tmpIsDeleted,_tmpDeletedAt,_tmpDirty)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun findByBirdCode(code: String): FarmAssetEntity? {
    val _sql: String = "SELECT * FROM farm_assets WHERE birdCode = ? LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, code)
        val _columnIndexOfAssetId: Int = getColumnIndexOrThrow(_stmt, "assetId")
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfAssetType: Int = getColumnIndexOrThrow(_stmt, "assetType")
        val _columnIndexOfCategory: Int = getColumnIndexOrThrow(_stmt, "category")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfIsShowcase: Int = getColumnIndexOrThrow(_stmt, "isShowcase")
        val _columnIndexOfLocationName: Int = getColumnIndexOrThrow(_stmt, "locationName")
        val _columnIndexOfLatitude: Int = getColumnIndexOrThrow(_stmt, "latitude")
        val _columnIndexOfLongitude: Int = getColumnIndexOrThrow(_stmt, "longitude")
        val _columnIndexOfQuantity: Int = getColumnIndexOrThrow(_stmt, "quantity")
        val _columnIndexOfInitialQuantity: Int = getColumnIndexOrThrow(_stmt, "initialQuantity")
        val _columnIndexOfUnit: Int = getColumnIndexOrThrow(_stmt, "unit")
        val _columnIndexOfBirthDate: Int = getColumnIndexOrThrow(_stmt, "birthDate")
        val _columnIndexOfAgeWeeks: Int = getColumnIndexOrThrow(_stmt, "ageWeeks")
        val _columnIndexOfBreed: Int = getColumnIndexOrThrow(_stmt, "breed")
        val _columnIndexOfGender: Int = getColumnIndexOrThrow(_stmt, "gender")
        val _columnIndexOfColor: Int = getColumnIndexOrThrow(_stmt, "color")
        val _columnIndexOfHealthStatus: Int = getColumnIndexOrThrow(_stmt, "healthStatus")
        val _columnIndexOfRaisingPurpose: Int = getColumnIndexOrThrow(_stmt, "raisingPurpose")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfImageUrls: Int = getColumnIndexOrThrow(_stmt, "imageUrls")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfLifecycleSubStage: Int = getColumnIndexOrThrow(_stmt, "lifecycleSubStage")
        val _columnIndexOfParentIdsJson: Int = getColumnIndexOrThrow(_stmt, "parentIdsJson")
        val _columnIndexOfBatchId: Int = getColumnIndexOrThrow(_stmt, "batchId")
        val _columnIndexOfOrigin: Int = getColumnIndexOrThrow(_stmt, "origin")
        val _columnIndexOfBirdCode: Int = getColumnIndexOrThrow(_stmt, "birdCode")
        val _columnIndexOfAcquisitionPrice: Int = getColumnIndexOrThrow(_stmt, "acquisitionPrice")
        val _columnIndexOfAcquisitionDate: Int = getColumnIndexOrThrow(_stmt, "acquisitionDate")
        val _columnIndexOfAcquisitionSource: Int = getColumnIndexOrThrow(_stmt, "acquisitionSource")
        val _columnIndexOfAcquisitionSourceId: Int = getColumnIndexOrThrow(_stmt,
            "acquisitionSourceId")
        val _columnIndexOfAcquisitionNotes: Int = getColumnIndexOrThrow(_stmt, "acquisitionNotes")
        val _columnIndexOfEstimatedValue: Int = getColumnIndexOrThrow(_stmt, "estimatedValue")
        val _columnIndexOfLastVaccinationDate: Int = getColumnIndexOrThrow(_stmt,
            "lastVaccinationDate")
        val _columnIndexOfNextVaccinationDate: Int = getColumnIndexOrThrow(_stmt,
            "nextVaccinationDate")
        val _columnIndexOfWeightGrams: Int = getColumnIndexOrThrow(_stmt, "weightGrams")
        val _columnIndexOfMetadataJson: Int = getColumnIndexOrThrow(_stmt, "metadataJson")
        val _columnIndexOfListedAt: Int = getColumnIndexOrThrow(_stmt, "listedAt")
        val _columnIndexOfListingId: Int = getColumnIndexOrThrow(_stmt, "listingId")
        val _columnIndexOfSoldAt: Int = getColumnIndexOrThrow(_stmt, "soldAt")
        val _columnIndexOfSoldToUserId: Int = getColumnIndexOrThrow(_stmt, "soldToUserId")
        val _columnIndexOfSoldPrice: Int = getColumnIndexOrThrow(_stmt, "soldPrice")
        val _columnIndexOfPreviousOwnerId: Int = getColumnIndexOrThrow(_stmt, "previousOwnerId")
        val _columnIndexOfTransferredAt: Int = getColumnIndexOrThrow(_stmt, "transferredAt")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfIsDeleted: Int = getColumnIndexOrThrow(_stmt, "isDeleted")
        val _columnIndexOfDeletedAt: Int = getColumnIndexOrThrow(_stmt, "deletedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _result: FarmAssetEntity?
        if (_stmt.step()) {
          val _tmpAssetId: String
          _tmpAssetId = _stmt.getText(_columnIndexOfAssetId)
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpAssetType: String
          _tmpAssetType = _stmt.getText(_columnIndexOfAssetType)
          val _tmpCategory: String
          _tmpCategory = _stmt.getText(_columnIndexOfCategory)
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpIsShowcase: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsShowcase).toInt()
          _tmpIsShowcase = _tmp != 0
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
          val _tmpQuantity: Double
          _tmpQuantity = _stmt.getDouble(_columnIndexOfQuantity)
          val _tmpInitialQuantity: Double
          _tmpInitialQuantity = _stmt.getDouble(_columnIndexOfInitialQuantity)
          val _tmpUnit: String
          _tmpUnit = _stmt.getText(_columnIndexOfUnit)
          val _tmpBirthDate: Long?
          if (_stmt.isNull(_columnIndexOfBirthDate)) {
            _tmpBirthDate = null
          } else {
            _tmpBirthDate = _stmt.getLong(_columnIndexOfBirthDate)
          }
          val _tmpAgeWeeks: Int?
          if (_stmt.isNull(_columnIndexOfAgeWeeks)) {
            _tmpAgeWeeks = null
          } else {
            _tmpAgeWeeks = _stmt.getLong(_columnIndexOfAgeWeeks).toInt()
          }
          val _tmpBreed: String?
          if (_stmt.isNull(_columnIndexOfBreed)) {
            _tmpBreed = null
          } else {
            _tmpBreed = _stmt.getText(_columnIndexOfBreed)
          }
          val _tmpGender: String?
          if (_stmt.isNull(_columnIndexOfGender)) {
            _tmpGender = null
          } else {
            _tmpGender = _stmt.getText(_columnIndexOfGender)
          }
          val _tmpColor: String?
          if (_stmt.isNull(_columnIndexOfColor)) {
            _tmpColor = null
          } else {
            _tmpColor = _stmt.getText(_columnIndexOfColor)
          }
          val _tmpHealthStatus: String
          _tmpHealthStatus = _stmt.getText(_columnIndexOfHealthStatus)
          val _tmpRaisingPurpose: String?
          if (_stmt.isNull(_columnIndexOfRaisingPurpose)) {
            _tmpRaisingPurpose = null
          } else {
            _tmpRaisingPurpose = _stmt.getText(_columnIndexOfRaisingPurpose)
          }
          val _tmpDescription: String
          _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          val _tmpImageUrls: List<String>
          val _tmp_1: String?
          if (_stmt.isNull(_columnIndexOfImageUrls)) {
            _tmp_1 = null
          } else {
            _tmp_1 = _stmt.getText(_columnIndexOfImageUrls)
          }
          val _tmp_2: List<String>? = AppDatabase.Converters.toStringList(_tmp_1)
          if (_tmp_2 == null) {
            error("Expected NON-NULL 'kotlin.collections.List<kotlin.String>', but it was NULL.")
          } else {
            _tmpImageUrls = _tmp_2
          }
          val _tmpNotes: String?
          if (_stmt.isNull(_columnIndexOfNotes)) {
            _tmpNotes = null
          } else {
            _tmpNotes = _stmt.getText(_columnIndexOfNotes)
          }
          val _tmpLifecycleSubStage: String?
          if (_stmt.isNull(_columnIndexOfLifecycleSubStage)) {
            _tmpLifecycleSubStage = null
          } else {
            _tmpLifecycleSubStage = _stmt.getText(_columnIndexOfLifecycleSubStage)
          }
          val _tmpParentIdsJson: String?
          if (_stmt.isNull(_columnIndexOfParentIdsJson)) {
            _tmpParentIdsJson = null
          } else {
            _tmpParentIdsJson = _stmt.getText(_columnIndexOfParentIdsJson)
          }
          val _tmpBatchId: String?
          if (_stmt.isNull(_columnIndexOfBatchId)) {
            _tmpBatchId = null
          } else {
            _tmpBatchId = _stmt.getText(_columnIndexOfBatchId)
          }
          val _tmpOrigin: String?
          if (_stmt.isNull(_columnIndexOfOrigin)) {
            _tmpOrigin = null
          } else {
            _tmpOrigin = _stmt.getText(_columnIndexOfOrigin)
          }
          val _tmpBirdCode: String?
          if (_stmt.isNull(_columnIndexOfBirdCode)) {
            _tmpBirdCode = null
          } else {
            _tmpBirdCode = _stmt.getText(_columnIndexOfBirdCode)
          }
          val _tmpAcquisitionPrice: Double?
          if (_stmt.isNull(_columnIndexOfAcquisitionPrice)) {
            _tmpAcquisitionPrice = null
          } else {
            _tmpAcquisitionPrice = _stmt.getDouble(_columnIndexOfAcquisitionPrice)
          }
          val _tmpAcquisitionDate: Long?
          if (_stmt.isNull(_columnIndexOfAcquisitionDate)) {
            _tmpAcquisitionDate = null
          } else {
            _tmpAcquisitionDate = _stmt.getLong(_columnIndexOfAcquisitionDate)
          }
          val _tmpAcquisitionSource: String?
          if (_stmt.isNull(_columnIndexOfAcquisitionSource)) {
            _tmpAcquisitionSource = null
          } else {
            _tmpAcquisitionSource = _stmt.getText(_columnIndexOfAcquisitionSource)
          }
          val _tmpAcquisitionSourceId: String?
          if (_stmt.isNull(_columnIndexOfAcquisitionSourceId)) {
            _tmpAcquisitionSourceId = null
          } else {
            _tmpAcquisitionSourceId = _stmt.getText(_columnIndexOfAcquisitionSourceId)
          }
          val _tmpAcquisitionNotes: String?
          if (_stmt.isNull(_columnIndexOfAcquisitionNotes)) {
            _tmpAcquisitionNotes = null
          } else {
            _tmpAcquisitionNotes = _stmt.getText(_columnIndexOfAcquisitionNotes)
          }
          val _tmpEstimatedValue: Double?
          if (_stmt.isNull(_columnIndexOfEstimatedValue)) {
            _tmpEstimatedValue = null
          } else {
            _tmpEstimatedValue = _stmt.getDouble(_columnIndexOfEstimatedValue)
          }
          val _tmpLastVaccinationDate: Long?
          if (_stmt.isNull(_columnIndexOfLastVaccinationDate)) {
            _tmpLastVaccinationDate = null
          } else {
            _tmpLastVaccinationDate = _stmt.getLong(_columnIndexOfLastVaccinationDate)
          }
          val _tmpNextVaccinationDate: Long?
          if (_stmt.isNull(_columnIndexOfNextVaccinationDate)) {
            _tmpNextVaccinationDate = null
          } else {
            _tmpNextVaccinationDate = _stmt.getLong(_columnIndexOfNextVaccinationDate)
          }
          val _tmpWeightGrams: Double?
          if (_stmt.isNull(_columnIndexOfWeightGrams)) {
            _tmpWeightGrams = null
          } else {
            _tmpWeightGrams = _stmt.getDouble(_columnIndexOfWeightGrams)
          }
          val _tmpMetadataJson: String
          _tmpMetadataJson = _stmt.getText(_columnIndexOfMetadataJson)
          val _tmpListedAt: Long?
          if (_stmt.isNull(_columnIndexOfListedAt)) {
            _tmpListedAt = null
          } else {
            _tmpListedAt = _stmt.getLong(_columnIndexOfListedAt)
          }
          val _tmpListingId: String?
          if (_stmt.isNull(_columnIndexOfListingId)) {
            _tmpListingId = null
          } else {
            _tmpListingId = _stmt.getText(_columnIndexOfListingId)
          }
          val _tmpSoldAt: Long?
          if (_stmt.isNull(_columnIndexOfSoldAt)) {
            _tmpSoldAt = null
          } else {
            _tmpSoldAt = _stmt.getLong(_columnIndexOfSoldAt)
          }
          val _tmpSoldToUserId: String?
          if (_stmt.isNull(_columnIndexOfSoldToUserId)) {
            _tmpSoldToUserId = null
          } else {
            _tmpSoldToUserId = _stmt.getText(_columnIndexOfSoldToUserId)
          }
          val _tmpSoldPrice: Double?
          if (_stmt.isNull(_columnIndexOfSoldPrice)) {
            _tmpSoldPrice = null
          } else {
            _tmpSoldPrice = _stmt.getDouble(_columnIndexOfSoldPrice)
          }
          val _tmpPreviousOwnerId: String?
          if (_stmt.isNull(_columnIndexOfPreviousOwnerId)) {
            _tmpPreviousOwnerId = null
          } else {
            _tmpPreviousOwnerId = _stmt.getText(_columnIndexOfPreviousOwnerId)
          }
          val _tmpTransferredAt: Long?
          if (_stmt.isNull(_columnIndexOfTransferredAt)) {
            _tmpTransferredAt = null
          } else {
            _tmpTransferredAt = _stmt.getLong(_columnIndexOfTransferredAt)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpIsDeleted: Boolean
          val _tmp_3: Int
          _tmp_3 = _stmt.getLong(_columnIndexOfIsDeleted).toInt()
          _tmpIsDeleted = _tmp_3 != 0
          val _tmpDeletedAt: Long?
          if (_stmt.isNull(_columnIndexOfDeletedAt)) {
            _tmpDeletedAt = null
          } else {
            _tmpDeletedAt = _stmt.getLong(_columnIndexOfDeletedAt)
          }
          val _tmpDirty: Boolean
          val _tmp_4: Int
          _tmp_4 = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp_4 != 0
          _result =
              FarmAssetEntity(_tmpAssetId,_tmpFarmerId,_tmpName,_tmpAssetType,_tmpCategory,_tmpStatus,_tmpIsShowcase,_tmpLocationName,_tmpLatitude,_tmpLongitude,_tmpQuantity,_tmpInitialQuantity,_tmpUnit,_tmpBirthDate,_tmpAgeWeeks,_tmpBreed,_tmpGender,_tmpColor,_tmpHealthStatus,_tmpRaisingPurpose,_tmpDescription,_tmpImageUrls,_tmpNotes,_tmpLifecycleSubStage,_tmpParentIdsJson,_tmpBatchId,_tmpOrigin,_tmpBirdCode,_tmpAcquisitionPrice,_tmpAcquisitionDate,_tmpAcquisitionSource,_tmpAcquisitionSourceId,_tmpAcquisitionNotes,_tmpEstimatedValue,_tmpLastVaccinationDate,_tmpNextVaccinationDate,_tmpWeightGrams,_tmpMetadataJson,_tmpListedAt,_tmpListingId,_tmpSoldAt,_tmpSoldToUserId,_tmpSoldPrice,_tmpPreviousOwnerId,_tmpTransferredAt,_tmpCreatedAt,_tmpUpdatedAt,_tmpIsDeleted,_tmpDeletedAt,_tmpDirty)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getUpdatedSince(sinceTime: Long, limit: Int): List<FarmAssetEntity> {
    val _sql: String = "SELECT * FROM farm_assets WHERE updatedAt > ? LIMIT ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, sinceTime)
        _argIndex = 2
        _stmt.bindLong(_argIndex, limit.toLong())
        val _columnIndexOfAssetId: Int = getColumnIndexOrThrow(_stmt, "assetId")
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfAssetType: Int = getColumnIndexOrThrow(_stmt, "assetType")
        val _columnIndexOfCategory: Int = getColumnIndexOrThrow(_stmt, "category")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfIsShowcase: Int = getColumnIndexOrThrow(_stmt, "isShowcase")
        val _columnIndexOfLocationName: Int = getColumnIndexOrThrow(_stmt, "locationName")
        val _columnIndexOfLatitude: Int = getColumnIndexOrThrow(_stmt, "latitude")
        val _columnIndexOfLongitude: Int = getColumnIndexOrThrow(_stmt, "longitude")
        val _columnIndexOfQuantity: Int = getColumnIndexOrThrow(_stmt, "quantity")
        val _columnIndexOfInitialQuantity: Int = getColumnIndexOrThrow(_stmt, "initialQuantity")
        val _columnIndexOfUnit: Int = getColumnIndexOrThrow(_stmt, "unit")
        val _columnIndexOfBirthDate: Int = getColumnIndexOrThrow(_stmt, "birthDate")
        val _columnIndexOfAgeWeeks: Int = getColumnIndexOrThrow(_stmt, "ageWeeks")
        val _columnIndexOfBreed: Int = getColumnIndexOrThrow(_stmt, "breed")
        val _columnIndexOfGender: Int = getColumnIndexOrThrow(_stmt, "gender")
        val _columnIndexOfColor: Int = getColumnIndexOrThrow(_stmt, "color")
        val _columnIndexOfHealthStatus: Int = getColumnIndexOrThrow(_stmt, "healthStatus")
        val _columnIndexOfRaisingPurpose: Int = getColumnIndexOrThrow(_stmt, "raisingPurpose")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfImageUrls: Int = getColumnIndexOrThrow(_stmt, "imageUrls")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfLifecycleSubStage: Int = getColumnIndexOrThrow(_stmt, "lifecycleSubStage")
        val _columnIndexOfParentIdsJson: Int = getColumnIndexOrThrow(_stmt, "parentIdsJson")
        val _columnIndexOfBatchId: Int = getColumnIndexOrThrow(_stmt, "batchId")
        val _columnIndexOfOrigin: Int = getColumnIndexOrThrow(_stmt, "origin")
        val _columnIndexOfBirdCode: Int = getColumnIndexOrThrow(_stmt, "birdCode")
        val _columnIndexOfAcquisitionPrice: Int = getColumnIndexOrThrow(_stmt, "acquisitionPrice")
        val _columnIndexOfAcquisitionDate: Int = getColumnIndexOrThrow(_stmt, "acquisitionDate")
        val _columnIndexOfAcquisitionSource: Int = getColumnIndexOrThrow(_stmt, "acquisitionSource")
        val _columnIndexOfAcquisitionSourceId: Int = getColumnIndexOrThrow(_stmt,
            "acquisitionSourceId")
        val _columnIndexOfAcquisitionNotes: Int = getColumnIndexOrThrow(_stmt, "acquisitionNotes")
        val _columnIndexOfEstimatedValue: Int = getColumnIndexOrThrow(_stmt, "estimatedValue")
        val _columnIndexOfLastVaccinationDate: Int = getColumnIndexOrThrow(_stmt,
            "lastVaccinationDate")
        val _columnIndexOfNextVaccinationDate: Int = getColumnIndexOrThrow(_stmt,
            "nextVaccinationDate")
        val _columnIndexOfWeightGrams: Int = getColumnIndexOrThrow(_stmt, "weightGrams")
        val _columnIndexOfMetadataJson: Int = getColumnIndexOrThrow(_stmt, "metadataJson")
        val _columnIndexOfListedAt: Int = getColumnIndexOrThrow(_stmt, "listedAt")
        val _columnIndexOfListingId: Int = getColumnIndexOrThrow(_stmt, "listingId")
        val _columnIndexOfSoldAt: Int = getColumnIndexOrThrow(_stmt, "soldAt")
        val _columnIndexOfSoldToUserId: Int = getColumnIndexOrThrow(_stmt, "soldToUserId")
        val _columnIndexOfSoldPrice: Int = getColumnIndexOrThrow(_stmt, "soldPrice")
        val _columnIndexOfPreviousOwnerId: Int = getColumnIndexOrThrow(_stmt, "previousOwnerId")
        val _columnIndexOfTransferredAt: Int = getColumnIndexOrThrow(_stmt, "transferredAt")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfIsDeleted: Int = getColumnIndexOrThrow(_stmt, "isDeleted")
        val _columnIndexOfDeletedAt: Int = getColumnIndexOrThrow(_stmt, "deletedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _result: MutableList<FarmAssetEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: FarmAssetEntity
          val _tmpAssetId: String
          _tmpAssetId = _stmt.getText(_columnIndexOfAssetId)
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpAssetType: String
          _tmpAssetType = _stmt.getText(_columnIndexOfAssetType)
          val _tmpCategory: String
          _tmpCategory = _stmt.getText(_columnIndexOfCategory)
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpIsShowcase: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsShowcase).toInt()
          _tmpIsShowcase = _tmp != 0
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
          val _tmpQuantity: Double
          _tmpQuantity = _stmt.getDouble(_columnIndexOfQuantity)
          val _tmpInitialQuantity: Double
          _tmpInitialQuantity = _stmt.getDouble(_columnIndexOfInitialQuantity)
          val _tmpUnit: String
          _tmpUnit = _stmt.getText(_columnIndexOfUnit)
          val _tmpBirthDate: Long?
          if (_stmt.isNull(_columnIndexOfBirthDate)) {
            _tmpBirthDate = null
          } else {
            _tmpBirthDate = _stmt.getLong(_columnIndexOfBirthDate)
          }
          val _tmpAgeWeeks: Int?
          if (_stmt.isNull(_columnIndexOfAgeWeeks)) {
            _tmpAgeWeeks = null
          } else {
            _tmpAgeWeeks = _stmt.getLong(_columnIndexOfAgeWeeks).toInt()
          }
          val _tmpBreed: String?
          if (_stmt.isNull(_columnIndexOfBreed)) {
            _tmpBreed = null
          } else {
            _tmpBreed = _stmt.getText(_columnIndexOfBreed)
          }
          val _tmpGender: String?
          if (_stmt.isNull(_columnIndexOfGender)) {
            _tmpGender = null
          } else {
            _tmpGender = _stmt.getText(_columnIndexOfGender)
          }
          val _tmpColor: String?
          if (_stmt.isNull(_columnIndexOfColor)) {
            _tmpColor = null
          } else {
            _tmpColor = _stmt.getText(_columnIndexOfColor)
          }
          val _tmpHealthStatus: String
          _tmpHealthStatus = _stmt.getText(_columnIndexOfHealthStatus)
          val _tmpRaisingPurpose: String?
          if (_stmt.isNull(_columnIndexOfRaisingPurpose)) {
            _tmpRaisingPurpose = null
          } else {
            _tmpRaisingPurpose = _stmt.getText(_columnIndexOfRaisingPurpose)
          }
          val _tmpDescription: String
          _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          val _tmpImageUrls: List<String>
          val _tmp_1: String?
          if (_stmt.isNull(_columnIndexOfImageUrls)) {
            _tmp_1 = null
          } else {
            _tmp_1 = _stmt.getText(_columnIndexOfImageUrls)
          }
          val _tmp_2: List<String>? = AppDatabase.Converters.toStringList(_tmp_1)
          if (_tmp_2 == null) {
            error("Expected NON-NULL 'kotlin.collections.List<kotlin.String>', but it was NULL.")
          } else {
            _tmpImageUrls = _tmp_2
          }
          val _tmpNotes: String?
          if (_stmt.isNull(_columnIndexOfNotes)) {
            _tmpNotes = null
          } else {
            _tmpNotes = _stmt.getText(_columnIndexOfNotes)
          }
          val _tmpLifecycleSubStage: String?
          if (_stmt.isNull(_columnIndexOfLifecycleSubStage)) {
            _tmpLifecycleSubStage = null
          } else {
            _tmpLifecycleSubStage = _stmt.getText(_columnIndexOfLifecycleSubStage)
          }
          val _tmpParentIdsJson: String?
          if (_stmt.isNull(_columnIndexOfParentIdsJson)) {
            _tmpParentIdsJson = null
          } else {
            _tmpParentIdsJson = _stmt.getText(_columnIndexOfParentIdsJson)
          }
          val _tmpBatchId: String?
          if (_stmt.isNull(_columnIndexOfBatchId)) {
            _tmpBatchId = null
          } else {
            _tmpBatchId = _stmt.getText(_columnIndexOfBatchId)
          }
          val _tmpOrigin: String?
          if (_stmt.isNull(_columnIndexOfOrigin)) {
            _tmpOrigin = null
          } else {
            _tmpOrigin = _stmt.getText(_columnIndexOfOrigin)
          }
          val _tmpBirdCode: String?
          if (_stmt.isNull(_columnIndexOfBirdCode)) {
            _tmpBirdCode = null
          } else {
            _tmpBirdCode = _stmt.getText(_columnIndexOfBirdCode)
          }
          val _tmpAcquisitionPrice: Double?
          if (_stmt.isNull(_columnIndexOfAcquisitionPrice)) {
            _tmpAcquisitionPrice = null
          } else {
            _tmpAcquisitionPrice = _stmt.getDouble(_columnIndexOfAcquisitionPrice)
          }
          val _tmpAcquisitionDate: Long?
          if (_stmt.isNull(_columnIndexOfAcquisitionDate)) {
            _tmpAcquisitionDate = null
          } else {
            _tmpAcquisitionDate = _stmt.getLong(_columnIndexOfAcquisitionDate)
          }
          val _tmpAcquisitionSource: String?
          if (_stmt.isNull(_columnIndexOfAcquisitionSource)) {
            _tmpAcquisitionSource = null
          } else {
            _tmpAcquisitionSource = _stmt.getText(_columnIndexOfAcquisitionSource)
          }
          val _tmpAcquisitionSourceId: String?
          if (_stmt.isNull(_columnIndexOfAcquisitionSourceId)) {
            _tmpAcquisitionSourceId = null
          } else {
            _tmpAcquisitionSourceId = _stmt.getText(_columnIndexOfAcquisitionSourceId)
          }
          val _tmpAcquisitionNotes: String?
          if (_stmt.isNull(_columnIndexOfAcquisitionNotes)) {
            _tmpAcquisitionNotes = null
          } else {
            _tmpAcquisitionNotes = _stmt.getText(_columnIndexOfAcquisitionNotes)
          }
          val _tmpEstimatedValue: Double?
          if (_stmt.isNull(_columnIndexOfEstimatedValue)) {
            _tmpEstimatedValue = null
          } else {
            _tmpEstimatedValue = _stmt.getDouble(_columnIndexOfEstimatedValue)
          }
          val _tmpLastVaccinationDate: Long?
          if (_stmt.isNull(_columnIndexOfLastVaccinationDate)) {
            _tmpLastVaccinationDate = null
          } else {
            _tmpLastVaccinationDate = _stmt.getLong(_columnIndexOfLastVaccinationDate)
          }
          val _tmpNextVaccinationDate: Long?
          if (_stmt.isNull(_columnIndexOfNextVaccinationDate)) {
            _tmpNextVaccinationDate = null
          } else {
            _tmpNextVaccinationDate = _stmt.getLong(_columnIndexOfNextVaccinationDate)
          }
          val _tmpWeightGrams: Double?
          if (_stmt.isNull(_columnIndexOfWeightGrams)) {
            _tmpWeightGrams = null
          } else {
            _tmpWeightGrams = _stmt.getDouble(_columnIndexOfWeightGrams)
          }
          val _tmpMetadataJson: String
          _tmpMetadataJson = _stmt.getText(_columnIndexOfMetadataJson)
          val _tmpListedAt: Long?
          if (_stmt.isNull(_columnIndexOfListedAt)) {
            _tmpListedAt = null
          } else {
            _tmpListedAt = _stmt.getLong(_columnIndexOfListedAt)
          }
          val _tmpListingId: String?
          if (_stmt.isNull(_columnIndexOfListingId)) {
            _tmpListingId = null
          } else {
            _tmpListingId = _stmt.getText(_columnIndexOfListingId)
          }
          val _tmpSoldAt: Long?
          if (_stmt.isNull(_columnIndexOfSoldAt)) {
            _tmpSoldAt = null
          } else {
            _tmpSoldAt = _stmt.getLong(_columnIndexOfSoldAt)
          }
          val _tmpSoldToUserId: String?
          if (_stmt.isNull(_columnIndexOfSoldToUserId)) {
            _tmpSoldToUserId = null
          } else {
            _tmpSoldToUserId = _stmt.getText(_columnIndexOfSoldToUserId)
          }
          val _tmpSoldPrice: Double?
          if (_stmt.isNull(_columnIndexOfSoldPrice)) {
            _tmpSoldPrice = null
          } else {
            _tmpSoldPrice = _stmt.getDouble(_columnIndexOfSoldPrice)
          }
          val _tmpPreviousOwnerId: String?
          if (_stmt.isNull(_columnIndexOfPreviousOwnerId)) {
            _tmpPreviousOwnerId = null
          } else {
            _tmpPreviousOwnerId = _stmt.getText(_columnIndexOfPreviousOwnerId)
          }
          val _tmpTransferredAt: Long?
          if (_stmt.isNull(_columnIndexOfTransferredAt)) {
            _tmpTransferredAt = null
          } else {
            _tmpTransferredAt = _stmt.getLong(_columnIndexOfTransferredAt)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpIsDeleted: Boolean
          val _tmp_3: Int
          _tmp_3 = _stmt.getLong(_columnIndexOfIsDeleted).toInt()
          _tmpIsDeleted = _tmp_3 != 0
          val _tmpDeletedAt: Long?
          if (_stmt.isNull(_columnIndexOfDeletedAt)) {
            _tmpDeletedAt = null
          } else {
            _tmpDeletedAt = _stmt.getLong(_columnIndexOfDeletedAt)
          }
          val _tmpDirty: Boolean
          val _tmp_4: Int
          _tmp_4 = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp_4 != 0
          _item =
              FarmAssetEntity(_tmpAssetId,_tmpFarmerId,_tmpName,_tmpAssetType,_tmpCategory,_tmpStatus,_tmpIsShowcase,_tmpLocationName,_tmpLatitude,_tmpLongitude,_tmpQuantity,_tmpInitialQuantity,_tmpUnit,_tmpBirthDate,_tmpAgeWeeks,_tmpBreed,_tmpGender,_tmpColor,_tmpHealthStatus,_tmpRaisingPurpose,_tmpDescription,_tmpImageUrls,_tmpNotes,_tmpLifecycleSubStage,_tmpParentIdsJson,_tmpBatchId,_tmpOrigin,_tmpBirdCode,_tmpAcquisitionPrice,_tmpAcquisitionDate,_tmpAcquisitionSource,_tmpAcquisitionSourceId,_tmpAcquisitionNotes,_tmpEstimatedValue,_tmpLastVaccinationDate,_tmpNextVaccinationDate,_tmpWeightGrams,_tmpMetadataJson,_tmpListedAt,_tmpListingId,_tmpSoldAt,_tmpSoldToUserId,_tmpSoldPrice,_tmpPreviousOwnerId,_tmpTransferredAt,_tmpCreatedAt,_tmpUpdatedAt,_tmpIsDeleted,_tmpDeletedAt,_tmpDirty)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getAssetCountForFarmer(farmerId: String): Int {
    val _sql: String = "SELECT COUNT(*) FROM farm_assets WHERE farmerId = ? AND isDeleted = 0"
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

  public override fun getAssetsByStatus(farmerId: String, status: String):
      Flow<List<FarmAssetEntity>> {
    val _sql: String =
        "SELECT * FROM farm_assets WHERE farmerId = ? AND status = ? AND isDeleted = 0 ORDER BY updatedAt DESC"
    return createFlow(__db, false, arrayOf("farm_assets")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, farmerId)
        _argIndex = 2
        _stmt.bindText(_argIndex, status)
        val _columnIndexOfAssetId: Int = getColumnIndexOrThrow(_stmt, "assetId")
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfAssetType: Int = getColumnIndexOrThrow(_stmt, "assetType")
        val _columnIndexOfCategory: Int = getColumnIndexOrThrow(_stmt, "category")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfIsShowcase: Int = getColumnIndexOrThrow(_stmt, "isShowcase")
        val _columnIndexOfLocationName: Int = getColumnIndexOrThrow(_stmt, "locationName")
        val _columnIndexOfLatitude: Int = getColumnIndexOrThrow(_stmt, "latitude")
        val _columnIndexOfLongitude: Int = getColumnIndexOrThrow(_stmt, "longitude")
        val _columnIndexOfQuantity: Int = getColumnIndexOrThrow(_stmt, "quantity")
        val _columnIndexOfInitialQuantity: Int = getColumnIndexOrThrow(_stmt, "initialQuantity")
        val _columnIndexOfUnit: Int = getColumnIndexOrThrow(_stmt, "unit")
        val _columnIndexOfBirthDate: Int = getColumnIndexOrThrow(_stmt, "birthDate")
        val _columnIndexOfAgeWeeks: Int = getColumnIndexOrThrow(_stmt, "ageWeeks")
        val _columnIndexOfBreed: Int = getColumnIndexOrThrow(_stmt, "breed")
        val _columnIndexOfGender: Int = getColumnIndexOrThrow(_stmt, "gender")
        val _columnIndexOfColor: Int = getColumnIndexOrThrow(_stmt, "color")
        val _columnIndexOfHealthStatus: Int = getColumnIndexOrThrow(_stmt, "healthStatus")
        val _columnIndexOfRaisingPurpose: Int = getColumnIndexOrThrow(_stmt, "raisingPurpose")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfImageUrls: Int = getColumnIndexOrThrow(_stmt, "imageUrls")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfLifecycleSubStage: Int = getColumnIndexOrThrow(_stmt, "lifecycleSubStage")
        val _columnIndexOfParentIdsJson: Int = getColumnIndexOrThrow(_stmt, "parentIdsJson")
        val _columnIndexOfBatchId: Int = getColumnIndexOrThrow(_stmt, "batchId")
        val _columnIndexOfOrigin: Int = getColumnIndexOrThrow(_stmt, "origin")
        val _columnIndexOfBirdCode: Int = getColumnIndexOrThrow(_stmt, "birdCode")
        val _columnIndexOfAcquisitionPrice: Int = getColumnIndexOrThrow(_stmt, "acquisitionPrice")
        val _columnIndexOfAcquisitionDate: Int = getColumnIndexOrThrow(_stmt, "acquisitionDate")
        val _columnIndexOfAcquisitionSource: Int = getColumnIndexOrThrow(_stmt, "acquisitionSource")
        val _columnIndexOfAcquisitionSourceId: Int = getColumnIndexOrThrow(_stmt,
            "acquisitionSourceId")
        val _columnIndexOfAcquisitionNotes: Int = getColumnIndexOrThrow(_stmt, "acquisitionNotes")
        val _columnIndexOfEstimatedValue: Int = getColumnIndexOrThrow(_stmt, "estimatedValue")
        val _columnIndexOfLastVaccinationDate: Int = getColumnIndexOrThrow(_stmt,
            "lastVaccinationDate")
        val _columnIndexOfNextVaccinationDate: Int = getColumnIndexOrThrow(_stmt,
            "nextVaccinationDate")
        val _columnIndexOfWeightGrams: Int = getColumnIndexOrThrow(_stmt, "weightGrams")
        val _columnIndexOfMetadataJson: Int = getColumnIndexOrThrow(_stmt, "metadataJson")
        val _columnIndexOfListedAt: Int = getColumnIndexOrThrow(_stmt, "listedAt")
        val _columnIndexOfListingId: Int = getColumnIndexOrThrow(_stmt, "listingId")
        val _columnIndexOfSoldAt: Int = getColumnIndexOrThrow(_stmt, "soldAt")
        val _columnIndexOfSoldToUserId: Int = getColumnIndexOrThrow(_stmt, "soldToUserId")
        val _columnIndexOfSoldPrice: Int = getColumnIndexOrThrow(_stmt, "soldPrice")
        val _columnIndexOfPreviousOwnerId: Int = getColumnIndexOrThrow(_stmt, "previousOwnerId")
        val _columnIndexOfTransferredAt: Int = getColumnIndexOrThrow(_stmt, "transferredAt")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfIsDeleted: Int = getColumnIndexOrThrow(_stmt, "isDeleted")
        val _columnIndexOfDeletedAt: Int = getColumnIndexOrThrow(_stmt, "deletedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _result: MutableList<FarmAssetEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: FarmAssetEntity
          val _tmpAssetId: String
          _tmpAssetId = _stmt.getText(_columnIndexOfAssetId)
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpAssetType: String
          _tmpAssetType = _stmt.getText(_columnIndexOfAssetType)
          val _tmpCategory: String
          _tmpCategory = _stmt.getText(_columnIndexOfCategory)
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpIsShowcase: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsShowcase).toInt()
          _tmpIsShowcase = _tmp != 0
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
          val _tmpQuantity: Double
          _tmpQuantity = _stmt.getDouble(_columnIndexOfQuantity)
          val _tmpInitialQuantity: Double
          _tmpInitialQuantity = _stmt.getDouble(_columnIndexOfInitialQuantity)
          val _tmpUnit: String
          _tmpUnit = _stmt.getText(_columnIndexOfUnit)
          val _tmpBirthDate: Long?
          if (_stmt.isNull(_columnIndexOfBirthDate)) {
            _tmpBirthDate = null
          } else {
            _tmpBirthDate = _stmt.getLong(_columnIndexOfBirthDate)
          }
          val _tmpAgeWeeks: Int?
          if (_stmt.isNull(_columnIndexOfAgeWeeks)) {
            _tmpAgeWeeks = null
          } else {
            _tmpAgeWeeks = _stmt.getLong(_columnIndexOfAgeWeeks).toInt()
          }
          val _tmpBreed: String?
          if (_stmt.isNull(_columnIndexOfBreed)) {
            _tmpBreed = null
          } else {
            _tmpBreed = _stmt.getText(_columnIndexOfBreed)
          }
          val _tmpGender: String?
          if (_stmt.isNull(_columnIndexOfGender)) {
            _tmpGender = null
          } else {
            _tmpGender = _stmt.getText(_columnIndexOfGender)
          }
          val _tmpColor: String?
          if (_stmt.isNull(_columnIndexOfColor)) {
            _tmpColor = null
          } else {
            _tmpColor = _stmt.getText(_columnIndexOfColor)
          }
          val _tmpHealthStatus: String
          _tmpHealthStatus = _stmt.getText(_columnIndexOfHealthStatus)
          val _tmpRaisingPurpose: String?
          if (_stmt.isNull(_columnIndexOfRaisingPurpose)) {
            _tmpRaisingPurpose = null
          } else {
            _tmpRaisingPurpose = _stmt.getText(_columnIndexOfRaisingPurpose)
          }
          val _tmpDescription: String
          _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          val _tmpImageUrls: List<String>
          val _tmp_1: String?
          if (_stmt.isNull(_columnIndexOfImageUrls)) {
            _tmp_1 = null
          } else {
            _tmp_1 = _stmt.getText(_columnIndexOfImageUrls)
          }
          val _tmp_2: List<String>? = AppDatabase.Converters.toStringList(_tmp_1)
          if (_tmp_2 == null) {
            error("Expected NON-NULL 'kotlin.collections.List<kotlin.String>', but it was NULL.")
          } else {
            _tmpImageUrls = _tmp_2
          }
          val _tmpNotes: String?
          if (_stmt.isNull(_columnIndexOfNotes)) {
            _tmpNotes = null
          } else {
            _tmpNotes = _stmt.getText(_columnIndexOfNotes)
          }
          val _tmpLifecycleSubStage: String?
          if (_stmt.isNull(_columnIndexOfLifecycleSubStage)) {
            _tmpLifecycleSubStage = null
          } else {
            _tmpLifecycleSubStage = _stmt.getText(_columnIndexOfLifecycleSubStage)
          }
          val _tmpParentIdsJson: String?
          if (_stmt.isNull(_columnIndexOfParentIdsJson)) {
            _tmpParentIdsJson = null
          } else {
            _tmpParentIdsJson = _stmt.getText(_columnIndexOfParentIdsJson)
          }
          val _tmpBatchId: String?
          if (_stmt.isNull(_columnIndexOfBatchId)) {
            _tmpBatchId = null
          } else {
            _tmpBatchId = _stmt.getText(_columnIndexOfBatchId)
          }
          val _tmpOrigin: String?
          if (_stmt.isNull(_columnIndexOfOrigin)) {
            _tmpOrigin = null
          } else {
            _tmpOrigin = _stmt.getText(_columnIndexOfOrigin)
          }
          val _tmpBirdCode: String?
          if (_stmt.isNull(_columnIndexOfBirdCode)) {
            _tmpBirdCode = null
          } else {
            _tmpBirdCode = _stmt.getText(_columnIndexOfBirdCode)
          }
          val _tmpAcquisitionPrice: Double?
          if (_stmt.isNull(_columnIndexOfAcquisitionPrice)) {
            _tmpAcquisitionPrice = null
          } else {
            _tmpAcquisitionPrice = _stmt.getDouble(_columnIndexOfAcquisitionPrice)
          }
          val _tmpAcquisitionDate: Long?
          if (_stmt.isNull(_columnIndexOfAcquisitionDate)) {
            _tmpAcquisitionDate = null
          } else {
            _tmpAcquisitionDate = _stmt.getLong(_columnIndexOfAcquisitionDate)
          }
          val _tmpAcquisitionSource: String?
          if (_stmt.isNull(_columnIndexOfAcquisitionSource)) {
            _tmpAcquisitionSource = null
          } else {
            _tmpAcquisitionSource = _stmt.getText(_columnIndexOfAcquisitionSource)
          }
          val _tmpAcquisitionSourceId: String?
          if (_stmt.isNull(_columnIndexOfAcquisitionSourceId)) {
            _tmpAcquisitionSourceId = null
          } else {
            _tmpAcquisitionSourceId = _stmt.getText(_columnIndexOfAcquisitionSourceId)
          }
          val _tmpAcquisitionNotes: String?
          if (_stmt.isNull(_columnIndexOfAcquisitionNotes)) {
            _tmpAcquisitionNotes = null
          } else {
            _tmpAcquisitionNotes = _stmt.getText(_columnIndexOfAcquisitionNotes)
          }
          val _tmpEstimatedValue: Double?
          if (_stmt.isNull(_columnIndexOfEstimatedValue)) {
            _tmpEstimatedValue = null
          } else {
            _tmpEstimatedValue = _stmt.getDouble(_columnIndexOfEstimatedValue)
          }
          val _tmpLastVaccinationDate: Long?
          if (_stmt.isNull(_columnIndexOfLastVaccinationDate)) {
            _tmpLastVaccinationDate = null
          } else {
            _tmpLastVaccinationDate = _stmt.getLong(_columnIndexOfLastVaccinationDate)
          }
          val _tmpNextVaccinationDate: Long?
          if (_stmt.isNull(_columnIndexOfNextVaccinationDate)) {
            _tmpNextVaccinationDate = null
          } else {
            _tmpNextVaccinationDate = _stmt.getLong(_columnIndexOfNextVaccinationDate)
          }
          val _tmpWeightGrams: Double?
          if (_stmt.isNull(_columnIndexOfWeightGrams)) {
            _tmpWeightGrams = null
          } else {
            _tmpWeightGrams = _stmt.getDouble(_columnIndexOfWeightGrams)
          }
          val _tmpMetadataJson: String
          _tmpMetadataJson = _stmt.getText(_columnIndexOfMetadataJson)
          val _tmpListedAt: Long?
          if (_stmt.isNull(_columnIndexOfListedAt)) {
            _tmpListedAt = null
          } else {
            _tmpListedAt = _stmt.getLong(_columnIndexOfListedAt)
          }
          val _tmpListingId: String?
          if (_stmt.isNull(_columnIndexOfListingId)) {
            _tmpListingId = null
          } else {
            _tmpListingId = _stmt.getText(_columnIndexOfListingId)
          }
          val _tmpSoldAt: Long?
          if (_stmt.isNull(_columnIndexOfSoldAt)) {
            _tmpSoldAt = null
          } else {
            _tmpSoldAt = _stmt.getLong(_columnIndexOfSoldAt)
          }
          val _tmpSoldToUserId: String?
          if (_stmt.isNull(_columnIndexOfSoldToUserId)) {
            _tmpSoldToUserId = null
          } else {
            _tmpSoldToUserId = _stmt.getText(_columnIndexOfSoldToUserId)
          }
          val _tmpSoldPrice: Double?
          if (_stmt.isNull(_columnIndexOfSoldPrice)) {
            _tmpSoldPrice = null
          } else {
            _tmpSoldPrice = _stmt.getDouble(_columnIndexOfSoldPrice)
          }
          val _tmpPreviousOwnerId: String?
          if (_stmt.isNull(_columnIndexOfPreviousOwnerId)) {
            _tmpPreviousOwnerId = null
          } else {
            _tmpPreviousOwnerId = _stmt.getText(_columnIndexOfPreviousOwnerId)
          }
          val _tmpTransferredAt: Long?
          if (_stmt.isNull(_columnIndexOfTransferredAt)) {
            _tmpTransferredAt = null
          } else {
            _tmpTransferredAt = _stmt.getLong(_columnIndexOfTransferredAt)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpIsDeleted: Boolean
          val _tmp_3: Int
          _tmp_3 = _stmt.getLong(_columnIndexOfIsDeleted).toInt()
          _tmpIsDeleted = _tmp_3 != 0
          val _tmpDeletedAt: Long?
          if (_stmt.isNull(_columnIndexOfDeletedAt)) {
            _tmpDeletedAt = null
          } else {
            _tmpDeletedAt = _stmt.getLong(_columnIndexOfDeletedAt)
          }
          val _tmpDirty: Boolean
          val _tmp_4: Int
          _tmp_4 = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp_4 != 0
          _item =
              FarmAssetEntity(_tmpAssetId,_tmpFarmerId,_tmpName,_tmpAssetType,_tmpCategory,_tmpStatus,_tmpIsShowcase,_tmpLocationName,_tmpLatitude,_tmpLongitude,_tmpQuantity,_tmpInitialQuantity,_tmpUnit,_tmpBirthDate,_tmpAgeWeeks,_tmpBreed,_tmpGender,_tmpColor,_tmpHealthStatus,_tmpRaisingPurpose,_tmpDescription,_tmpImageUrls,_tmpNotes,_tmpLifecycleSubStage,_tmpParentIdsJson,_tmpBatchId,_tmpOrigin,_tmpBirdCode,_tmpAcquisitionPrice,_tmpAcquisitionDate,_tmpAcquisitionSource,_tmpAcquisitionSourceId,_tmpAcquisitionNotes,_tmpEstimatedValue,_tmpLastVaccinationDate,_tmpNextVaccinationDate,_tmpWeightGrams,_tmpMetadataJson,_tmpListedAt,_tmpListingId,_tmpSoldAt,_tmpSoldToUserId,_tmpSoldPrice,_tmpPreviousOwnerId,_tmpTransferredAt,_tmpCreatedAt,_tmpUpdatedAt,_tmpIsDeleted,_tmpDeletedAt,_tmpDirty)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getSoldAssets(farmerId: String): Flow<List<FarmAssetEntity>> {
    val _sql: String =
        "SELECT * FROM farm_assets WHERE farmerId = ? AND status = 'SOLD' AND isDeleted = 0 ORDER BY soldAt DESC"
    return createFlow(__db, false, arrayOf("farm_assets")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, farmerId)
        val _columnIndexOfAssetId: Int = getColumnIndexOrThrow(_stmt, "assetId")
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfAssetType: Int = getColumnIndexOrThrow(_stmt, "assetType")
        val _columnIndexOfCategory: Int = getColumnIndexOrThrow(_stmt, "category")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfIsShowcase: Int = getColumnIndexOrThrow(_stmt, "isShowcase")
        val _columnIndexOfLocationName: Int = getColumnIndexOrThrow(_stmt, "locationName")
        val _columnIndexOfLatitude: Int = getColumnIndexOrThrow(_stmt, "latitude")
        val _columnIndexOfLongitude: Int = getColumnIndexOrThrow(_stmt, "longitude")
        val _columnIndexOfQuantity: Int = getColumnIndexOrThrow(_stmt, "quantity")
        val _columnIndexOfInitialQuantity: Int = getColumnIndexOrThrow(_stmt, "initialQuantity")
        val _columnIndexOfUnit: Int = getColumnIndexOrThrow(_stmt, "unit")
        val _columnIndexOfBirthDate: Int = getColumnIndexOrThrow(_stmt, "birthDate")
        val _columnIndexOfAgeWeeks: Int = getColumnIndexOrThrow(_stmt, "ageWeeks")
        val _columnIndexOfBreed: Int = getColumnIndexOrThrow(_stmt, "breed")
        val _columnIndexOfGender: Int = getColumnIndexOrThrow(_stmt, "gender")
        val _columnIndexOfColor: Int = getColumnIndexOrThrow(_stmt, "color")
        val _columnIndexOfHealthStatus: Int = getColumnIndexOrThrow(_stmt, "healthStatus")
        val _columnIndexOfRaisingPurpose: Int = getColumnIndexOrThrow(_stmt, "raisingPurpose")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfImageUrls: Int = getColumnIndexOrThrow(_stmt, "imageUrls")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfLifecycleSubStage: Int = getColumnIndexOrThrow(_stmt, "lifecycleSubStage")
        val _columnIndexOfParentIdsJson: Int = getColumnIndexOrThrow(_stmt, "parentIdsJson")
        val _columnIndexOfBatchId: Int = getColumnIndexOrThrow(_stmt, "batchId")
        val _columnIndexOfOrigin: Int = getColumnIndexOrThrow(_stmt, "origin")
        val _columnIndexOfBirdCode: Int = getColumnIndexOrThrow(_stmt, "birdCode")
        val _columnIndexOfAcquisitionPrice: Int = getColumnIndexOrThrow(_stmt, "acquisitionPrice")
        val _columnIndexOfAcquisitionDate: Int = getColumnIndexOrThrow(_stmt, "acquisitionDate")
        val _columnIndexOfAcquisitionSource: Int = getColumnIndexOrThrow(_stmt, "acquisitionSource")
        val _columnIndexOfAcquisitionSourceId: Int = getColumnIndexOrThrow(_stmt,
            "acquisitionSourceId")
        val _columnIndexOfAcquisitionNotes: Int = getColumnIndexOrThrow(_stmt, "acquisitionNotes")
        val _columnIndexOfEstimatedValue: Int = getColumnIndexOrThrow(_stmt, "estimatedValue")
        val _columnIndexOfLastVaccinationDate: Int = getColumnIndexOrThrow(_stmt,
            "lastVaccinationDate")
        val _columnIndexOfNextVaccinationDate: Int = getColumnIndexOrThrow(_stmt,
            "nextVaccinationDate")
        val _columnIndexOfWeightGrams: Int = getColumnIndexOrThrow(_stmt, "weightGrams")
        val _columnIndexOfMetadataJson: Int = getColumnIndexOrThrow(_stmt, "metadataJson")
        val _columnIndexOfListedAt: Int = getColumnIndexOrThrow(_stmt, "listedAt")
        val _columnIndexOfListingId: Int = getColumnIndexOrThrow(_stmt, "listingId")
        val _columnIndexOfSoldAt: Int = getColumnIndexOrThrow(_stmt, "soldAt")
        val _columnIndexOfSoldToUserId: Int = getColumnIndexOrThrow(_stmt, "soldToUserId")
        val _columnIndexOfSoldPrice: Int = getColumnIndexOrThrow(_stmt, "soldPrice")
        val _columnIndexOfPreviousOwnerId: Int = getColumnIndexOrThrow(_stmt, "previousOwnerId")
        val _columnIndexOfTransferredAt: Int = getColumnIndexOrThrow(_stmt, "transferredAt")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfIsDeleted: Int = getColumnIndexOrThrow(_stmt, "isDeleted")
        val _columnIndexOfDeletedAt: Int = getColumnIndexOrThrow(_stmt, "deletedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _result: MutableList<FarmAssetEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: FarmAssetEntity
          val _tmpAssetId: String
          _tmpAssetId = _stmt.getText(_columnIndexOfAssetId)
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpAssetType: String
          _tmpAssetType = _stmt.getText(_columnIndexOfAssetType)
          val _tmpCategory: String
          _tmpCategory = _stmt.getText(_columnIndexOfCategory)
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpIsShowcase: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsShowcase).toInt()
          _tmpIsShowcase = _tmp != 0
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
          val _tmpQuantity: Double
          _tmpQuantity = _stmt.getDouble(_columnIndexOfQuantity)
          val _tmpInitialQuantity: Double
          _tmpInitialQuantity = _stmt.getDouble(_columnIndexOfInitialQuantity)
          val _tmpUnit: String
          _tmpUnit = _stmt.getText(_columnIndexOfUnit)
          val _tmpBirthDate: Long?
          if (_stmt.isNull(_columnIndexOfBirthDate)) {
            _tmpBirthDate = null
          } else {
            _tmpBirthDate = _stmt.getLong(_columnIndexOfBirthDate)
          }
          val _tmpAgeWeeks: Int?
          if (_stmt.isNull(_columnIndexOfAgeWeeks)) {
            _tmpAgeWeeks = null
          } else {
            _tmpAgeWeeks = _stmt.getLong(_columnIndexOfAgeWeeks).toInt()
          }
          val _tmpBreed: String?
          if (_stmt.isNull(_columnIndexOfBreed)) {
            _tmpBreed = null
          } else {
            _tmpBreed = _stmt.getText(_columnIndexOfBreed)
          }
          val _tmpGender: String?
          if (_stmt.isNull(_columnIndexOfGender)) {
            _tmpGender = null
          } else {
            _tmpGender = _stmt.getText(_columnIndexOfGender)
          }
          val _tmpColor: String?
          if (_stmt.isNull(_columnIndexOfColor)) {
            _tmpColor = null
          } else {
            _tmpColor = _stmt.getText(_columnIndexOfColor)
          }
          val _tmpHealthStatus: String
          _tmpHealthStatus = _stmt.getText(_columnIndexOfHealthStatus)
          val _tmpRaisingPurpose: String?
          if (_stmt.isNull(_columnIndexOfRaisingPurpose)) {
            _tmpRaisingPurpose = null
          } else {
            _tmpRaisingPurpose = _stmt.getText(_columnIndexOfRaisingPurpose)
          }
          val _tmpDescription: String
          _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          val _tmpImageUrls: List<String>
          val _tmp_1: String?
          if (_stmt.isNull(_columnIndexOfImageUrls)) {
            _tmp_1 = null
          } else {
            _tmp_1 = _stmt.getText(_columnIndexOfImageUrls)
          }
          val _tmp_2: List<String>? = AppDatabase.Converters.toStringList(_tmp_1)
          if (_tmp_2 == null) {
            error("Expected NON-NULL 'kotlin.collections.List<kotlin.String>', but it was NULL.")
          } else {
            _tmpImageUrls = _tmp_2
          }
          val _tmpNotes: String?
          if (_stmt.isNull(_columnIndexOfNotes)) {
            _tmpNotes = null
          } else {
            _tmpNotes = _stmt.getText(_columnIndexOfNotes)
          }
          val _tmpLifecycleSubStage: String?
          if (_stmt.isNull(_columnIndexOfLifecycleSubStage)) {
            _tmpLifecycleSubStage = null
          } else {
            _tmpLifecycleSubStage = _stmt.getText(_columnIndexOfLifecycleSubStage)
          }
          val _tmpParentIdsJson: String?
          if (_stmt.isNull(_columnIndexOfParentIdsJson)) {
            _tmpParentIdsJson = null
          } else {
            _tmpParentIdsJson = _stmt.getText(_columnIndexOfParentIdsJson)
          }
          val _tmpBatchId: String?
          if (_stmt.isNull(_columnIndexOfBatchId)) {
            _tmpBatchId = null
          } else {
            _tmpBatchId = _stmt.getText(_columnIndexOfBatchId)
          }
          val _tmpOrigin: String?
          if (_stmt.isNull(_columnIndexOfOrigin)) {
            _tmpOrigin = null
          } else {
            _tmpOrigin = _stmt.getText(_columnIndexOfOrigin)
          }
          val _tmpBirdCode: String?
          if (_stmt.isNull(_columnIndexOfBirdCode)) {
            _tmpBirdCode = null
          } else {
            _tmpBirdCode = _stmt.getText(_columnIndexOfBirdCode)
          }
          val _tmpAcquisitionPrice: Double?
          if (_stmt.isNull(_columnIndexOfAcquisitionPrice)) {
            _tmpAcquisitionPrice = null
          } else {
            _tmpAcquisitionPrice = _stmt.getDouble(_columnIndexOfAcquisitionPrice)
          }
          val _tmpAcquisitionDate: Long?
          if (_stmt.isNull(_columnIndexOfAcquisitionDate)) {
            _tmpAcquisitionDate = null
          } else {
            _tmpAcquisitionDate = _stmt.getLong(_columnIndexOfAcquisitionDate)
          }
          val _tmpAcquisitionSource: String?
          if (_stmt.isNull(_columnIndexOfAcquisitionSource)) {
            _tmpAcquisitionSource = null
          } else {
            _tmpAcquisitionSource = _stmt.getText(_columnIndexOfAcquisitionSource)
          }
          val _tmpAcquisitionSourceId: String?
          if (_stmt.isNull(_columnIndexOfAcquisitionSourceId)) {
            _tmpAcquisitionSourceId = null
          } else {
            _tmpAcquisitionSourceId = _stmt.getText(_columnIndexOfAcquisitionSourceId)
          }
          val _tmpAcquisitionNotes: String?
          if (_stmt.isNull(_columnIndexOfAcquisitionNotes)) {
            _tmpAcquisitionNotes = null
          } else {
            _tmpAcquisitionNotes = _stmt.getText(_columnIndexOfAcquisitionNotes)
          }
          val _tmpEstimatedValue: Double?
          if (_stmt.isNull(_columnIndexOfEstimatedValue)) {
            _tmpEstimatedValue = null
          } else {
            _tmpEstimatedValue = _stmt.getDouble(_columnIndexOfEstimatedValue)
          }
          val _tmpLastVaccinationDate: Long?
          if (_stmt.isNull(_columnIndexOfLastVaccinationDate)) {
            _tmpLastVaccinationDate = null
          } else {
            _tmpLastVaccinationDate = _stmt.getLong(_columnIndexOfLastVaccinationDate)
          }
          val _tmpNextVaccinationDate: Long?
          if (_stmt.isNull(_columnIndexOfNextVaccinationDate)) {
            _tmpNextVaccinationDate = null
          } else {
            _tmpNextVaccinationDate = _stmt.getLong(_columnIndexOfNextVaccinationDate)
          }
          val _tmpWeightGrams: Double?
          if (_stmt.isNull(_columnIndexOfWeightGrams)) {
            _tmpWeightGrams = null
          } else {
            _tmpWeightGrams = _stmt.getDouble(_columnIndexOfWeightGrams)
          }
          val _tmpMetadataJson: String
          _tmpMetadataJson = _stmt.getText(_columnIndexOfMetadataJson)
          val _tmpListedAt: Long?
          if (_stmt.isNull(_columnIndexOfListedAt)) {
            _tmpListedAt = null
          } else {
            _tmpListedAt = _stmt.getLong(_columnIndexOfListedAt)
          }
          val _tmpListingId: String?
          if (_stmt.isNull(_columnIndexOfListingId)) {
            _tmpListingId = null
          } else {
            _tmpListingId = _stmt.getText(_columnIndexOfListingId)
          }
          val _tmpSoldAt: Long?
          if (_stmt.isNull(_columnIndexOfSoldAt)) {
            _tmpSoldAt = null
          } else {
            _tmpSoldAt = _stmt.getLong(_columnIndexOfSoldAt)
          }
          val _tmpSoldToUserId: String?
          if (_stmt.isNull(_columnIndexOfSoldToUserId)) {
            _tmpSoldToUserId = null
          } else {
            _tmpSoldToUserId = _stmt.getText(_columnIndexOfSoldToUserId)
          }
          val _tmpSoldPrice: Double?
          if (_stmt.isNull(_columnIndexOfSoldPrice)) {
            _tmpSoldPrice = null
          } else {
            _tmpSoldPrice = _stmt.getDouble(_columnIndexOfSoldPrice)
          }
          val _tmpPreviousOwnerId: String?
          if (_stmt.isNull(_columnIndexOfPreviousOwnerId)) {
            _tmpPreviousOwnerId = null
          } else {
            _tmpPreviousOwnerId = _stmt.getText(_columnIndexOfPreviousOwnerId)
          }
          val _tmpTransferredAt: Long?
          if (_stmt.isNull(_columnIndexOfTransferredAt)) {
            _tmpTransferredAt = null
          } else {
            _tmpTransferredAt = _stmt.getLong(_columnIndexOfTransferredAt)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpIsDeleted: Boolean
          val _tmp_3: Int
          _tmp_3 = _stmt.getLong(_columnIndexOfIsDeleted).toInt()
          _tmpIsDeleted = _tmp_3 != 0
          val _tmpDeletedAt: Long?
          if (_stmt.isNull(_columnIndexOfDeletedAt)) {
            _tmpDeletedAt = null
          } else {
            _tmpDeletedAt = _stmt.getLong(_columnIndexOfDeletedAt)
          }
          val _tmpDirty: Boolean
          val _tmp_4: Int
          _tmp_4 = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp_4 != 0
          _item =
              FarmAssetEntity(_tmpAssetId,_tmpFarmerId,_tmpName,_tmpAssetType,_tmpCategory,_tmpStatus,_tmpIsShowcase,_tmpLocationName,_tmpLatitude,_tmpLongitude,_tmpQuantity,_tmpInitialQuantity,_tmpUnit,_tmpBirthDate,_tmpAgeWeeks,_tmpBreed,_tmpGender,_tmpColor,_tmpHealthStatus,_tmpRaisingPurpose,_tmpDescription,_tmpImageUrls,_tmpNotes,_tmpLifecycleSubStage,_tmpParentIdsJson,_tmpBatchId,_tmpOrigin,_tmpBirdCode,_tmpAcquisitionPrice,_tmpAcquisitionDate,_tmpAcquisitionSource,_tmpAcquisitionSourceId,_tmpAcquisitionNotes,_tmpEstimatedValue,_tmpLastVaccinationDate,_tmpNextVaccinationDate,_tmpWeightGrams,_tmpMetadataJson,_tmpListedAt,_tmpListingId,_tmpSoldAt,_tmpSoldToUserId,_tmpSoldPrice,_tmpPreviousOwnerId,_tmpTransferredAt,_tmpCreatedAt,_tmpUpdatedAt,_tmpIsDeleted,_tmpDeletedAt,_tmpDirty)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getListingId(assetId: String): String? {
    val _sql: String = "SELECT listingId FROM farm_assets WHERE assetId = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, assetId)
        val _result: String?
        if (_stmt.step()) {
          if (_stmt.isNull(0)) {
            _result = null
          } else {
            _result = _stmt.getText(0)
          }
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getDirtyAssets(limit: Int): List<FarmAssetEntity> {
    val _sql: String = "SELECT * FROM farm_assets WHERE dirty = 1 LIMIT ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, limit.toLong())
        val _columnIndexOfAssetId: Int = getColumnIndexOrThrow(_stmt, "assetId")
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfAssetType: Int = getColumnIndexOrThrow(_stmt, "assetType")
        val _columnIndexOfCategory: Int = getColumnIndexOrThrow(_stmt, "category")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfIsShowcase: Int = getColumnIndexOrThrow(_stmt, "isShowcase")
        val _columnIndexOfLocationName: Int = getColumnIndexOrThrow(_stmt, "locationName")
        val _columnIndexOfLatitude: Int = getColumnIndexOrThrow(_stmt, "latitude")
        val _columnIndexOfLongitude: Int = getColumnIndexOrThrow(_stmt, "longitude")
        val _columnIndexOfQuantity: Int = getColumnIndexOrThrow(_stmt, "quantity")
        val _columnIndexOfInitialQuantity: Int = getColumnIndexOrThrow(_stmt, "initialQuantity")
        val _columnIndexOfUnit: Int = getColumnIndexOrThrow(_stmt, "unit")
        val _columnIndexOfBirthDate: Int = getColumnIndexOrThrow(_stmt, "birthDate")
        val _columnIndexOfAgeWeeks: Int = getColumnIndexOrThrow(_stmt, "ageWeeks")
        val _columnIndexOfBreed: Int = getColumnIndexOrThrow(_stmt, "breed")
        val _columnIndexOfGender: Int = getColumnIndexOrThrow(_stmt, "gender")
        val _columnIndexOfColor: Int = getColumnIndexOrThrow(_stmt, "color")
        val _columnIndexOfHealthStatus: Int = getColumnIndexOrThrow(_stmt, "healthStatus")
        val _columnIndexOfRaisingPurpose: Int = getColumnIndexOrThrow(_stmt, "raisingPurpose")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfImageUrls: Int = getColumnIndexOrThrow(_stmt, "imageUrls")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfLifecycleSubStage: Int = getColumnIndexOrThrow(_stmt, "lifecycleSubStage")
        val _columnIndexOfParentIdsJson: Int = getColumnIndexOrThrow(_stmt, "parentIdsJson")
        val _columnIndexOfBatchId: Int = getColumnIndexOrThrow(_stmt, "batchId")
        val _columnIndexOfOrigin: Int = getColumnIndexOrThrow(_stmt, "origin")
        val _columnIndexOfBirdCode: Int = getColumnIndexOrThrow(_stmt, "birdCode")
        val _columnIndexOfAcquisitionPrice: Int = getColumnIndexOrThrow(_stmt, "acquisitionPrice")
        val _columnIndexOfAcquisitionDate: Int = getColumnIndexOrThrow(_stmt, "acquisitionDate")
        val _columnIndexOfAcquisitionSource: Int = getColumnIndexOrThrow(_stmt, "acquisitionSource")
        val _columnIndexOfAcquisitionSourceId: Int = getColumnIndexOrThrow(_stmt,
            "acquisitionSourceId")
        val _columnIndexOfAcquisitionNotes: Int = getColumnIndexOrThrow(_stmt, "acquisitionNotes")
        val _columnIndexOfEstimatedValue: Int = getColumnIndexOrThrow(_stmt, "estimatedValue")
        val _columnIndexOfLastVaccinationDate: Int = getColumnIndexOrThrow(_stmt,
            "lastVaccinationDate")
        val _columnIndexOfNextVaccinationDate: Int = getColumnIndexOrThrow(_stmt,
            "nextVaccinationDate")
        val _columnIndexOfWeightGrams: Int = getColumnIndexOrThrow(_stmt, "weightGrams")
        val _columnIndexOfMetadataJson: Int = getColumnIndexOrThrow(_stmt, "metadataJson")
        val _columnIndexOfListedAt: Int = getColumnIndexOrThrow(_stmt, "listedAt")
        val _columnIndexOfListingId: Int = getColumnIndexOrThrow(_stmt, "listingId")
        val _columnIndexOfSoldAt: Int = getColumnIndexOrThrow(_stmt, "soldAt")
        val _columnIndexOfSoldToUserId: Int = getColumnIndexOrThrow(_stmt, "soldToUserId")
        val _columnIndexOfSoldPrice: Int = getColumnIndexOrThrow(_stmt, "soldPrice")
        val _columnIndexOfPreviousOwnerId: Int = getColumnIndexOrThrow(_stmt, "previousOwnerId")
        val _columnIndexOfTransferredAt: Int = getColumnIndexOrThrow(_stmt, "transferredAt")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfIsDeleted: Int = getColumnIndexOrThrow(_stmt, "isDeleted")
        val _columnIndexOfDeletedAt: Int = getColumnIndexOrThrow(_stmt, "deletedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _result: MutableList<FarmAssetEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: FarmAssetEntity
          val _tmpAssetId: String
          _tmpAssetId = _stmt.getText(_columnIndexOfAssetId)
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpAssetType: String
          _tmpAssetType = _stmt.getText(_columnIndexOfAssetType)
          val _tmpCategory: String
          _tmpCategory = _stmt.getText(_columnIndexOfCategory)
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpIsShowcase: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsShowcase).toInt()
          _tmpIsShowcase = _tmp != 0
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
          val _tmpQuantity: Double
          _tmpQuantity = _stmt.getDouble(_columnIndexOfQuantity)
          val _tmpInitialQuantity: Double
          _tmpInitialQuantity = _stmt.getDouble(_columnIndexOfInitialQuantity)
          val _tmpUnit: String
          _tmpUnit = _stmt.getText(_columnIndexOfUnit)
          val _tmpBirthDate: Long?
          if (_stmt.isNull(_columnIndexOfBirthDate)) {
            _tmpBirthDate = null
          } else {
            _tmpBirthDate = _stmt.getLong(_columnIndexOfBirthDate)
          }
          val _tmpAgeWeeks: Int?
          if (_stmt.isNull(_columnIndexOfAgeWeeks)) {
            _tmpAgeWeeks = null
          } else {
            _tmpAgeWeeks = _stmt.getLong(_columnIndexOfAgeWeeks).toInt()
          }
          val _tmpBreed: String?
          if (_stmt.isNull(_columnIndexOfBreed)) {
            _tmpBreed = null
          } else {
            _tmpBreed = _stmt.getText(_columnIndexOfBreed)
          }
          val _tmpGender: String?
          if (_stmt.isNull(_columnIndexOfGender)) {
            _tmpGender = null
          } else {
            _tmpGender = _stmt.getText(_columnIndexOfGender)
          }
          val _tmpColor: String?
          if (_stmt.isNull(_columnIndexOfColor)) {
            _tmpColor = null
          } else {
            _tmpColor = _stmt.getText(_columnIndexOfColor)
          }
          val _tmpHealthStatus: String
          _tmpHealthStatus = _stmt.getText(_columnIndexOfHealthStatus)
          val _tmpRaisingPurpose: String?
          if (_stmt.isNull(_columnIndexOfRaisingPurpose)) {
            _tmpRaisingPurpose = null
          } else {
            _tmpRaisingPurpose = _stmt.getText(_columnIndexOfRaisingPurpose)
          }
          val _tmpDescription: String
          _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          val _tmpImageUrls: List<String>
          val _tmp_1: String?
          if (_stmt.isNull(_columnIndexOfImageUrls)) {
            _tmp_1 = null
          } else {
            _tmp_1 = _stmt.getText(_columnIndexOfImageUrls)
          }
          val _tmp_2: List<String>? = AppDatabase.Converters.toStringList(_tmp_1)
          if (_tmp_2 == null) {
            error("Expected NON-NULL 'kotlin.collections.List<kotlin.String>', but it was NULL.")
          } else {
            _tmpImageUrls = _tmp_2
          }
          val _tmpNotes: String?
          if (_stmt.isNull(_columnIndexOfNotes)) {
            _tmpNotes = null
          } else {
            _tmpNotes = _stmt.getText(_columnIndexOfNotes)
          }
          val _tmpLifecycleSubStage: String?
          if (_stmt.isNull(_columnIndexOfLifecycleSubStage)) {
            _tmpLifecycleSubStage = null
          } else {
            _tmpLifecycleSubStage = _stmt.getText(_columnIndexOfLifecycleSubStage)
          }
          val _tmpParentIdsJson: String?
          if (_stmt.isNull(_columnIndexOfParentIdsJson)) {
            _tmpParentIdsJson = null
          } else {
            _tmpParentIdsJson = _stmt.getText(_columnIndexOfParentIdsJson)
          }
          val _tmpBatchId: String?
          if (_stmt.isNull(_columnIndexOfBatchId)) {
            _tmpBatchId = null
          } else {
            _tmpBatchId = _stmt.getText(_columnIndexOfBatchId)
          }
          val _tmpOrigin: String?
          if (_stmt.isNull(_columnIndexOfOrigin)) {
            _tmpOrigin = null
          } else {
            _tmpOrigin = _stmt.getText(_columnIndexOfOrigin)
          }
          val _tmpBirdCode: String?
          if (_stmt.isNull(_columnIndexOfBirdCode)) {
            _tmpBirdCode = null
          } else {
            _tmpBirdCode = _stmt.getText(_columnIndexOfBirdCode)
          }
          val _tmpAcquisitionPrice: Double?
          if (_stmt.isNull(_columnIndexOfAcquisitionPrice)) {
            _tmpAcquisitionPrice = null
          } else {
            _tmpAcquisitionPrice = _stmt.getDouble(_columnIndexOfAcquisitionPrice)
          }
          val _tmpAcquisitionDate: Long?
          if (_stmt.isNull(_columnIndexOfAcquisitionDate)) {
            _tmpAcquisitionDate = null
          } else {
            _tmpAcquisitionDate = _stmt.getLong(_columnIndexOfAcquisitionDate)
          }
          val _tmpAcquisitionSource: String?
          if (_stmt.isNull(_columnIndexOfAcquisitionSource)) {
            _tmpAcquisitionSource = null
          } else {
            _tmpAcquisitionSource = _stmt.getText(_columnIndexOfAcquisitionSource)
          }
          val _tmpAcquisitionSourceId: String?
          if (_stmt.isNull(_columnIndexOfAcquisitionSourceId)) {
            _tmpAcquisitionSourceId = null
          } else {
            _tmpAcquisitionSourceId = _stmt.getText(_columnIndexOfAcquisitionSourceId)
          }
          val _tmpAcquisitionNotes: String?
          if (_stmt.isNull(_columnIndexOfAcquisitionNotes)) {
            _tmpAcquisitionNotes = null
          } else {
            _tmpAcquisitionNotes = _stmt.getText(_columnIndexOfAcquisitionNotes)
          }
          val _tmpEstimatedValue: Double?
          if (_stmt.isNull(_columnIndexOfEstimatedValue)) {
            _tmpEstimatedValue = null
          } else {
            _tmpEstimatedValue = _stmt.getDouble(_columnIndexOfEstimatedValue)
          }
          val _tmpLastVaccinationDate: Long?
          if (_stmt.isNull(_columnIndexOfLastVaccinationDate)) {
            _tmpLastVaccinationDate = null
          } else {
            _tmpLastVaccinationDate = _stmt.getLong(_columnIndexOfLastVaccinationDate)
          }
          val _tmpNextVaccinationDate: Long?
          if (_stmt.isNull(_columnIndexOfNextVaccinationDate)) {
            _tmpNextVaccinationDate = null
          } else {
            _tmpNextVaccinationDate = _stmt.getLong(_columnIndexOfNextVaccinationDate)
          }
          val _tmpWeightGrams: Double?
          if (_stmt.isNull(_columnIndexOfWeightGrams)) {
            _tmpWeightGrams = null
          } else {
            _tmpWeightGrams = _stmt.getDouble(_columnIndexOfWeightGrams)
          }
          val _tmpMetadataJson: String
          _tmpMetadataJson = _stmt.getText(_columnIndexOfMetadataJson)
          val _tmpListedAt: Long?
          if (_stmt.isNull(_columnIndexOfListedAt)) {
            _tmpListedAt = null
          } else {
            _tmpListedAt = _stmt.getLong(_columnIndexOfListedAt)
          }
          val _tmpListingId: String?
          if (_stmt.isNull(_columnIndexOfListingId)) {
            _tmpListingId = null
          } else {
            _tmpListingId = _stmt.getText(_columnIndexOfListingId)
          }
          val _tmpSoldAt: Long?
          if (_stmt.isNull(_columnIndexOfSoldAt)) {
            _tmpSoldAt = null
          } else {
            _tmpSoldAt = _stmt.getLong(_columnIndexOfSoldAt)
          }
          val _tmpSoldToUserId: String?
          if (_stmt.isNull(_columnIndexOfSoldToUserId)) {
            _tmpSoldToUserId = null
          } else {
            _tmpSoldToUserId = _stmt.getText(_columnIndexOfSoldToUserId)
          }
          val _tmpSoldPrice: Double?
          if (_stmt.isNull(_columnIndexOfSoldPrice)) {
            _tmpSoldPrice = null
          } else {
            _tmpSoldPrice = _stmt.getDouble(_columnIndexOfSoldPrice)
          }
          val _tmpPreviousOwnerId: String?
          if (_stmt.isNull(_columnIndexOfPreviousOwnerId)) {
            _tmpPreviousOwnerId = null
          } else {
            _tmpPreviousOwnerId = _stmt.getText(_columnIndexOfPreviousOwnerId)
          }
          val _tmpTransferredAt: Long?
          if (_stmt.isNull(_columnIndexOfTransferredAt)) {
            _tmpTransferredAt = null
          } else {
            _tmpTransferredAt = _stmt.getLong(_columnIndexOfTransferredAt)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpIsDeleted: Boolean
          val _tmp_3: Int
          _tmp_3 = _stmt.getLong(_columnIndexOfIsDeleted).toInt()
          _tmpIsDeleted = _tmp_3 != 0
          val _tmpDeletedAt: Long?
          if (_stmt.isNull(_columnIndexOfDeletedAt)) {
            _tmpDeletedAt = null
          } else {
            _tmpDeletedAt = _stmt.getLong(_columnIndexOfDeletedAt)
          }
          val _tmpDirty: Boolean
          val _tmp_4: Int
          _tmp_4 = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp_4 != 0
          _item =
              FarmAssetEntity(_tmpAssetId,_tmpFarmerId,_tmpName,_tmpAssetType,_tmpCategory,_tmpStatus,_tmpIsShowcase,_tmpLocationName,_tmpLatitude,_tmpLongitude,_tmpQuantity,_tmpInitialQuantity,_tmpUnit,_tmpBirthDate,_tmpAgeWeeks,_tmpBreed,_tmpGender,_tmpColor,_tmpHealthStatus,_tmpRaisingPurpose,_tmpDescription,_tmpImageUrls,_tmpNotes,_tmpLifecycleSubStage,_tmpParentIdsJson,_tmpBatchId,_tmpOrigin,_tmpBirdCode,_tmpAcquisitionPrice,_tmpAcquisitionDate,_tmpAcquisitionSource,_tmpAcquisitionSourceId,_tmpAcquisitionNotes,_tmpEstimatedValue,_tmpLastVaccinationDate,_tmpNextVaccinationDate,_tmpWeightGrams,_tmpMetadataJson,_tmpListedAt,_tmpListingId,_tmpSoldAt,_tmpSoldToUserId,_tmpSoldPrice,_tmpPreviousOwnerId,_tmpTransferredAt,_tmpCreatedAt,_tmpUpdatedAt,_tmpIsDeleted,_tmpDeletedAt,_tmpDirty)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getAssetsWithBirthDate(farmerId: String): List<FarmAssetEntity> {
    val _sql: String =
        "SELECT * FROM farm_assets WHERE farmerId = ? AND birthDate IS NOT NULL AND isDeleted = 0"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, farmerId)
        val _columnIndexOfAssetId: Int = getColumnIndexOrThrow(_stmt, "assetId")
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfAssetType: Int = getColumnIndexOrThrow(_stmt, "assetType")
        val _columnIndexOfCategory: Int = getColumnIndexOrThrow(_stmt, "category")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfIsShowcase: Int = getColumnIndexOrThrow(_stmt, "isShowcase")
        val _columnIndexOfLocationName: Int = getColumnIndexOrThrow(_stmt, "locationName")
        val _columnIndexOfLatitude: Int = getColumnIndexOrThrow(_stmt, "latitude")
        val _columnIndexOfLongitude: Int = getColumnIndexOrThrow(_stmt, "longitude")
        val _columnIndexOfQuantity: Int = getColumnIndexOrThrow(_stmt, "quantity")
        val _columnIndexOfInitialQuantity: Int = getColumnIndexOrThrow(_stmt, "initialQuantity")
        val _columnIndexOfUnit: Int = getColumnIndexOrThrow(_stmt, "unit")
        val _columnIndexOfBirthDate: Int = getColumnIndexOrThrow(_stmt, "birthDate")
        val _columnIndexOfAgeWeeks: Int = getColumnIndexOrThrow(_stmt, "ageWeeks")
        val _columnIndexOfBreed: Int = getColumnIndexOrThrow(_stmt, "breed")
        val _columnIndexOfGender: Int = getColumnIndexOrThrow(_stmt, "gender")
        val _columnIndexOfColor: Int = getColumnIndexOrThrow(_stmt, "color")
        val _columnIndexOfHealthStatus: Int = getColumnIndexOrThrow(_stmt, "healthStatus")
        val _columnIndexOfRaisingPurpose: Int = getColumnIndexOrThrow(_stmt, "raisingPurpose")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfImageUrls: Int = getColumnIndexOrThrow(_stmt, "imageUrls")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfLifecycleSubStage: Int = getColumnIndexOrThrow(_stmt, "lifecycleSubStage")
        val _columnIndexOfParentIdsJson: Int = getColumnIndexOrThrow(_stmt, "parentIdsJson")
        val _columnIndexOfBatchId: Int = getColumnIndexOrThrow(_stmt, "batchId")
        val _columnIndexOfOrigin: Int = getColumnIndexOrThrow(_stmt, "origin")
        val _columnIndexOfBirdCode: Int = getColumnIndexOrThrow(_stmt, "birdCode")
        val _columnIndexOfAcquisitionPrice: Int = getColumnIndexOrThrow(_stmt, "acquisitionPrice")
        val _columnIndexOfAcquisitionDate: Int = getColumnIndexOrThrow(_stmt, "acquisitionDate")
        val _columnIndexOfAcquisitionSource: Int = getColumnIndexOrThrow(_stmt, "acquisitionSource")
        val _columnIndexOfAcquisitionSourceId: Int = getColumnIndexOrThrow(_stmt,
            "acquisitionSourceId")
        val _columnIndexOfAcquisitionNotes: Int = getColumnIndexOrThrow(_stmt, "acquisitionNotes")
        val _columnIndexOfEstimatedValue: Int = getColumnIndexOrThrow(_stmt, "estimatedValue")
        val _columnIndexOfLastVaccinationDate: Int = getColumnIndexOrThrow(_stmt,
            "lastVaccinationDate")
        val _columnIndexOfNextVaccinationDate: Int = getColumnIndexOrThrow(_stmt,
            "nextVaccinationDate")
        val _columnIndexOfWeightGrams: Int = getColumnIndexOrThrow(_stmt, "weightGrams")
        val _columnIndexOfMetadataJson: Int = getColumnIndexOrThrow(_stmt, "metadataJson")
        val _columnIndexOfListedAt: Int = getColumnIndexOrThrow(_stmt, "listedAt")
        val _columnIndexOfListingId: Int = getColumnIndexOrThrow(_stmt, "listingId")
        val _columnIndexOfSoldAt: Int = getColumnIndexOrThrow(_stmt, "soldAt")
        val _columnIndexOfSoldToUserId: Int = getColumnIndexOrThrow(_stmt, "soldToUserId")
        val _columnIndexOfSoldPrice: Int = getColumnIndexOrThrow(_stmt, "soldPrice")
        val _columnIndexOfPreviousOwnerId: Int = getColumnIndexOrThrow(_stmt, "previousOwnerId")
        val _columnIndexOfTransferredAt: Int = getColumnIndexOrThrow(_stmt, "transferredAt")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfIsDeleted: Int = getColumnIndexOrThrow(_stmt, "isDeleted")
        val _columnIndexOfDeletedAt: Int = getColumnIndexOrThrow(_stmt, "deletedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _result: MutableList<FarmAssetEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: FarmAssetEntity
          val _tmpAssetId: String
          _tmpAssetId = _stmt.getText(_columnIndexOfAssetId)
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpAssetType: String
          _tmpAssetType = _stmt.getText(_columnIndexOfAssetType)
          val _tmpCategory: String
          _tmpCategory = _stmt.getText(_columnIndexOfCategory)
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpIsShowcase: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsShowcase).toInt()
          _tmpIsShowcase = _tmp != 0
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
          val _tmpQuantity: Double
          _tmpQuantity = _stmt.getDouble(_columnIndexOfQuantity)
          val _tmpInitialQuantity: Double
          _tmpInitialQuantity = _stmt.getDouble(_columnIndexOfInitialQuantity)
          val _tmpUnit: String
          _tmpUnit = _stmt.getText(_columnIndexOfUnit)
          val _tmpBirthDate: Long?
          if (_stmt.isNull(_columnIndexOfBirthDate)) {
            _tmpBirthDate = null
          } else {
            _tmpBirthDate = _stmt.getLong(_columnIndexOfBirthDate)
          }
          val _tmpAgeWeeks: Int?
          if (_stmt.isNull(_columnIndexOfAgeWeeks)) {
            _tmpAgeWeeks = null
          } else {
            _tmpAgeWeeks = _stmt.getLong(_columnIndexOfAgeWeeks).toInt()
          }
          val _tmpBreed: String?
          if (_stmt.isNull(_columnIndexOfBreed)) {
            _tmpBreed = null
          } else {
            _tmpBreed = _stmt.getText(_columnIndexOfBreed)
          }
          val _tmpGender: String?
          if (_stmt.isNull(_columnIndexOfGender)) {
            _tmpGender = null
          } else {
            _tmpGender = _stmt.getText(_columnIndexOfGender)
          }
          val _tmpColor: String?
          if (_stmt.isNull(_columnIndexOfColor)) {
            _tmpColor = null
          } else {
            _tmpColor = _stmt.getText(_columnIndexOfColor)
          }
          val _tmpHealthStatus: String
          _tmpHealthStatus = _stmt.getText(_columnIndexOfHealthStatus)
          val _tmpRaisingPurpose: String?
          if (_stmt.isNull(_columnIndexOfRaisingPurpose)) {
            _tmpRaisingPurpose = null
          } else {
            _tmpRaisingPurpose = _stmt.getText(_columnIndexOfRaisingPurpose)
          }
          val _tmpDescription: String
          _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          val _tmpImageUrls: List<String>
          val _tmp_1: String?
          if (_stmt.isNull(_columnIndexOfImageUrls)) {
            _tmp_1 = null
          } else {
            _tmp_1 = _stmt.getText(_columnIndexOfImageUrls)
          }
          val _tmp_2: List<String>? = AppDatabase.Converters.toStringList(_tmp_1)
          if (_tmp_2 == null) {
            error("Expected NON-NULL 'kotlin.collections.List<kotlin.String>', but it was NULL.")
          } else {
            _tmpImageUrls = _tmp_2
          }
          val _tmpNotes: String?
          if (_stmt.isNull(_columnIndexOfNotes)) {
            _tmpNotes = null
          } else {
            _tmpNotes = _stmt.getText(_columnIndexOfNotes)
          }
          val _tmpLifecycleSubStage: String?
          if (_stmt.isNull(_columnIndexOfLifecycleSubStage)) {
            _tmpLifecycleSubStage = null
          } else {
            _tmpLifecycleSubStage = _stmt.getText(_columnIndexOfLifecycleSubStage)
          }
          val _tmpParentIdsJson: String?
          if (_stmt.isNull(_columnIndexOfParentIdsJson)) {
            _tmpParentIdsJson = null
          } else {
            _tmpParentIdsJson = _stmt.getText(_columnIndexOfParentIdsJson)
          }
          val _tmpBatchId: String?
          if (_stmt.isNull(_columnIndexOfBatchId)) {
            _tmpBatchId = null
          } else {
            _tmpBatchId = _stmt.getText(_columnIndexOfBatchId)
          }
          val _tmpOrigin: String?
          if (_stmt.isNull(_columnIndexOfOrigin)) {
            _tmpOrigin = null
          } else {
            _tmpOrigin = _stmt.getText(_columnIndexOfOrigin)
          }
          val _tmpBirdCode: String?
          if (_stmt.isNull(_columnIndexOfBirdCode)) {
            _tmpBirdCode = null
          } else {
            _tmpBirdCode = _stmt.getText(_columnIndexOfBirdCode)
          }
          val _tmpAcquisitionPrice: Double?
          if (_stmt.isNull(_columnIndexOfAcquisitionPrice)) {
            _tmpAcquisitionPrice = null
          } else {
            _tmpAcquisitionPrice = _stmt.getDouble(_columnIndexOfAcquisitionPrice)
          }
          val _tmpAcquisitionDate: Long?
          if (_stmt.isNull(_columnIndexOfAcquisitionDate)) {
            _tmpAcquisitionDate = null
          } else {
            _tmpAcquisitionDate = _stmt.getLong(_columnIndexOfAcquisitionDate)
          }
          val _tmpAcquisitionSource: String?
          if (_stmt.isNull(_columnIndexOfAcquisitionSource)) {
            _tmpAcquisitionSource = null
          } else {
            _tmpAcquisitionSource = _stmt.getText(_columnIndexOfAcquisitionSource)
          }
          val _tmpAcquisitionSourceId: String?
          if (_stmt.isNull(_columnIndexOfAcquisitionSourceId)) {
            _tmpAcquisitionSourceId = null
          } else {
            _tmpAcquisitionSourceId = _stmt.getText(_columnIndexOfAcquisitionSourceId)
          }
          val _tmpAcquisitionNotes: String?
          if (_stmt.isNull(_columnIndexOfAcquisitionNotes)) {
            _tmpAcquisitionNotes = null
          } else {
            _tmpAcquisitionNotes = _stmt.getText(_columnIndexOfAcquisitionNotes)
          }
          val _tmpEstimatedValue: Double?
          if (_stmt.isNull(_columnIndexOfEstimatedValue)) {
            _tmpEstimatedValue = null
          } else {
            _tmpEstimatedValue = _stmt.getDouble(_columnIndexOfEstimatedValue)
          }
          val _tmpLastVaccinationDate: Long?
          if (_stmt.isNull(_columnIndexOfLastVaccinationDate)) {
            _tmpLastVaccinationDate = null
          } else {
            _tmpLastVaccinationDate = _stmt.getLong(_columnIndexOfLastVaccinationDate)
          }
          val _tmpNextVaccinationDate: Long?
          if (_stmt.isNull(_columnIndexOfNextVaccinationDate)) {
            _tmpNextVaccinationDate = null
          } else {
            _tmpNextVaccinationDate = _stmt.getLong(_columnIndexOfNextVaccinationDate)
          }
          val _tmpWeightGrams: Double?
          if (_stmt.isNull(_columnIndexOfWeightGrams)) {
            _tmpWeightGrams = null
          } else {
            _tmpWeightGrams = _stmt.getDouble(_columnIndexOfWeightGrams)
          }
          val _tmpMetadataJson: String
          _tmpMetadataJson = _stmt.getText(_columnIndexOfMetadataJson)
          val _tmpListedAt: Long?
          if (_stmt.isNull(_columnIndexOfListedAt)) {
            _tmpListedAt = null
          } else {
            _tmpListedAt = _stmt.getLong(_columnIndexOfListedAt)
          }
          val _tmpListingId: String?
          if (_stmt.isNull(_columnIndexOfListingId)) {
            _tmpListingId = null
          } else {
            _tmpListingId = _stmt.getText(_columnIndexOfListingId)
          }
          val _tmpSoldAt: Long?
          if (_stmt.isNull(_columnIndexOfSoldAt)) {
            _tmpSoldAt = null
          } else {
            _tmpSoldAt = _stmt.getLong(_columnIndexOfSoldAt)
          }
          val _tmpSoldToUserId: String?
          if (_stmt.isNull(_columnIndexOfSoldToUserId)) {
            _tmpSoldToUserId = null
          } else {
            _tmpSoldToUserId = _stmt.getText(_columnIndexOfSoldToUserId)
          }
          val _tmpSoldPrice: Double?
          if (_stmt.isNull(_columnIndexOfSoldPrice)) {
            _tmpSoldPrice = null
          } else {
            _tmpSoldPrice = _stmt.getDouble(_columnIndexOfSoldPrice)
          }
          val _tmpPreviousOwnerId: String?
          if (_stmt.isNull(_columnIndexOfPreviousOwnerId)) {
            _tmpPreviousOwnerId = null
          } else {
            _tmpPreviousOwnerId = _stmt.getText(_columnIndexOfPreviousOwnerId)
          }
          val _tmpTransferredAt: Long?
          if (_stmt.isNull(_columnIndexOfTransferredAt)) {
            _tmpTransferredAt = null
          } else {
            _tmpTransferredAt = _stmt.getLong(_columnIndexOfTransferredAt)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpIsDeleted: Boolean
          val _tmp_3: Int
          _tmp_3 = _stmt.getLong(_columnIndexOfIsDeleted).toInt()
          _tmpIsDeleted = _tmp_3 != 0
          val _tmpDeletedAt: Long?
          if (_stmt.isNull(_columnIndexOfDeletedAt)) {
            _tmpDeletedAt = null
          } else {
            _tmpDeletedAt = _stmt.getLong(_columnIndexOfDeletedAt)
          }
          val _tmpDirty: Boolean
          val _tmp_4: Int
          _tmp_4 = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp_4 != 0
          _item =
              FarmAssetEntity(_tmpAssetId,_tmpFarmerId,_tmpName,_tmpAssetType,_tmpCategory,_tmpStatus,_tmpIsShowcase,_tmpLocationName,_tmpLatitude,_tmpLongitude,_tmpQuantity,_tmpInitialQuantity,_tmpUnit,_tmpBirthDate,_tmpAgeWeeks,_tmpBreed,_tmpGender,_tmpColor,_tmpHealthStatus,_tmpRaisingPurpose,_tmpDescription,_tmpImageUrls,_tmpNotes,_tmpLifecycleSubStage,_tmpParentIdsJson,_tmpBatchId,_tmpOrigin,_tmpBirdCode,_tmpAcquisitionPrice,_tmpAcquisitionDate,_tmpAcquisitionSource,_tmpAcquisitionSourceId,_tmpAcquisitionNotes,_tmpEstimatedValue,_tmpLastVaccinationDate,_tmpNextVaccinationDate,_tmpWeightGrams,_tmpMetadataJson,_tmpListedAt,_tmpListingId,_tmpSoldAt,_tmpSoldToUserId,_tmpSoldPrice,_tmpPreviousOwnerId,_tmpTransferredAt,_tmpCreatedAt,_tmpUpdatedAt,_tmpIsDeleted,_tmpDeletedAt,_tmpDirty)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getMarketReadyAssets(farmerId: String, minWeight: Double):
      List<FarmAssetEntity> {
    val _sql: String =
        "SELECT * FROM farm_assets WHERE farmerId = ? AND weightGrams >= ? AND status = 'ACTIVE' AND listingId IS NULL AND isDeleted = 0"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, farmerId)
        _argIndex = 2
        _stmt.bindDouble(_argIndex, minWeight)
        val _columnIndexOfAssetId: Int = getColumnIndexOrThrow(_stmt, "assetId")
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfAssetType: Int = getColumnIndexOrThrow(_stmt, "assetType")
        val _columnIndexOfCategory: Int = getColumnIndexOrThrow(_stmt, "category")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfIsShowcase: Int = getColumnIndexOrThrow(_stmt, "isShowcase")
        val _columnIndexOfLocationName: Int = getColumnIndexOrThrow(_stmt, "locationName")
        val _columnIndexOfLatitude: Int = getColumnIndexOrThrow(_stmt, "latitude")
        val _columnIndexOfLongitude: Int = getColumnIndexOrThrow(_stmt, "longitude")
        val _columnIndexOfQuantity: Int = getColumnIndexOrThrow(_stmt, "quantity")
        val _columnIndexOfInitialQuantity: Int = getColumnIndexOrThrow(_stmt, "initialQuantity")
        val _columnIndexOfUnit: Int = getColumnIndexOrThrow(_stmt, "unit")
        val _columnIndexOfBirthDate: Int = getColumnIndexOrThrow(_stmt, "birthDate")
        val _columnIndexOfAgeWeeks: Int = getColumnIndexOrThrow(_stmt, "ageWeeks")
        val _columnIndexOfBreed: Int = getColumnIndexOrThrow(_stmt, "breed")
        val _columnIndexOfGender: Int = getColumnIndexOrThrow(_stmt, "gender")
        val _columnIndexOfColor: Int = getColumnIndexOrThrow(_stmt, "color")
        val _columnIndexOfHealthStatus: Int = getColumnIndexOrThrow(_stmt, "healthStatus")
        val _columnIndexOfRaisingPurpose: Int = getColumnIndexOrThrow(_stmt, "raisingPurpose")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfImageUrls: Int = getColumnIndexOrThrow(_stmt, "imageUrls")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfLifecycleSubStage: Int = getColumnIndexOrThrow(_stmt, "lifecycleSubStage")
        val _columnIndexOfParentIdsJson: Int = getColumnIndexOrThrow(_stmt, "parentIdsJson")
        val _columnIndexOfBatchId: Int = getColumnIndexOrThrow(_stmt, "batchId")
        val _columnIndexOfOrigin: Int = getColumnIndexOrThrow(_stmt, "origin")
        val _columnIndexOfBirdCode: Int = getColumnIndexOrThrow(_stmt, "birdCode")
        val _columnIndexOfAcquisitionPrice: Int = getColumnIndexOrThrow(_stmt, "acquisitionPrice")
        val _columnIndexOfAcquisitionDate: Int = getColumnIndexOrThrow(_stmt, "acquisitionDate")
        val _columnIndexOfAcquisitionSource: Int = getColumnIndexOrThrow(_stmt, "acquisitionSource")
        val _columnIndexOfAcquisitionSourceId: Int = getColumnIndexOrThrow(_stmt,
            "acquisitionSourceId")
        val _columnIndexOfAcquisitionNotes: Int = getColumnIndexOrThrow(_stmt, "acquisitionNotes")
        val _columnIndexOfEstimatedValue: Int = getColumnIndexOrThrow(_stmt, "estimatedValue")
        val _columnIndexOfLastVaccinationDate: Int = getColumnIndexOrThrow(_stmt,
            "lastVaccinationDate")
        val _columnIndexOfNextVaccinationDate: Int = getColumnIndexOrThrow(_stmt,
            "nextVaccinationDate")
        val _columnIndexOfWeightGrams: Int = getColumnIndexOrThrow(_stmt, "weightGrams")
        val _columnIndexOfMetadataJson: Int = getColumnIndexOrThrow(_stmt, "metadataJson")
        val _columnIndexOfListedAt: Int = getColumnIndexOrThrow(_stmt, "listedAt")
        val _columnIndexOfListingId: Int = getColumnIndexOrThrow(_stmt, "listingId")
        val _columnIndexOfSoldAt: Int = getColumnIndexOrThrow(_stmt, "soldAt")
        val _columnIndexOfSoldToUserId: Int = getColumnIndexOrThrow(_stmt, "soldToUserId")
        val _columnIndexOfSoldPrice: Int = getColumnIndexOrThrow(_stmt, "soldPrice")
        val _columnIndexOfPreviousOwnerId: Int = getColumnIndexOrThrow(_stmt, "previousOwnerId")
        val _columnIndexOfTransferredAt: Int = getColumnIndexOrThrow(_stmt, "transferredAt")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfIsDeleted: Int = getColumnIndexOrThrow(_stmt, "isDeleted")
        val _columnIndexOfDeletedAt: Int = getColumnIndexOrThrow(_stmt, "deletedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _result: MutableList<FarmAssetEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: FarmAssetEntity
          val _tmpAssetId: String
          _tmpAssetId = _stmt.getText(_columnIndexOfAssetId)
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpAssetType: String
          _tmpAssetType = _stmt.getText(_columnIndexOfAssetType)
          val _tmpCategory: String
          _tmpCategory = _stmt.getText(_columnIndexOfCategory)
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpIsShowcase: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsShowcase).toInt()
          _tmpIsShowcase = _tmp != 0
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
          val _tmpQuantity: Double
          _tmpQuantity = _stmt.getDouble(_columnIndexOfQuantity)
          val _tmpInitialQuantity: Double
          _tmpInitialQuantity = _stmt.getDouble(_columnIndexOfInitialQuantity)
          val _tmpUnit: String
          _tmpUnit = _stmt.getText(_columnIndexOfUnit)
          val _tmpBirthDate: Long?
          if (_stmt.isNull(_columnIndexOfBirthDate)) {
            _tmpBirthDate = null
          } else {
            _tmpBirthDate = _stmt.getLong(_columnIndexOfBirthDate)
          }
          val _tmpAgeWeeks: Int?
          if (_stmt.isNull(_columnIndexOfAgeWeeks)) {
            _tmpAgeWeeks = null
          } else {
            _tmpAgeWeeks = _stmt.getLong(_columnIndexOfAgeWeeks).toInt()
          }
          val _tmpBreed: String?
          if (_stmt.isNull(_columnIndexOfBreed)) {
            _tmpBreed = null
          } else {
            _tmpBreed = _stmt.getText(_columnIndexOfBreed)
          }
          val _tmpGender: String?
          if (_stmt.isNull(_columnIndexOfGender)) {
            _tmpGender = null
          } else {
            _tmpGender = _stmt.getText(_columnIndexOfGender)
          }
          val _tmpColor: String?
          if (_stmt.isNull(_columnIndexOfColor)) {
            _tmpColor = null
          } else {
            _tmpColor = _stmt.getText(_columnIndexOfColor)
          }
          val _tmpHealthStatus: String
          _tmpHealthStatus = _stmt.getText(_columnIndexOfHealthStatus)
          val _tmpRaisingPurpose: String?
          if (_stmt.isNull(_columnIndexOfRaisingPurpose)) {
            _tmpRaisingPurpose = null
          } else {
            _tmpRaisingPurpose = _stmt.getText(_columnIndexOfRaisingPurpose)
          }
          val _tmpDescription: String
          _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          val _tmpImageUrls: List<String>
          val _tmp_1: String?
          if (_stmt.isNull(_columnIndexOfImageUrls)) {
            _tmp_1 = null
          } else {
            _tmp_1 = _stmt.getText(_columnIndexOfImageUrls)
          }
          val _tmp_2: List<String>? = AppDatabase.Converters.toStringList(_tmp_1)
          if (_tmp_2 == null) {
            error("Expected NON-NULL 'kotlin.collections.List<kotlin.String>', but it was NULL.")
          } else {
            _tmpImageUrls = _tmp_2
          }
          val _tmpNotes: String?
          if (_stmt.isNull(_columnIndexOfNotes)) {
            _tmpNotes = null
          } else {
            _tmpNotes = _stmt.getText(_columnIndexOfNotes)
          }
          val _tmpLifecycleSubStage: String?
          if (_stmt.isNull(_columnIndexOfLifecycleSubStage)) {
            _tmpLifecycleSubStage = null
          } else {
            _tmpLifecycleSubStage = _stmt.getText(_columnIndexOfLifecycleSubStage)
          }
          val _tmpParentIdsJson: String?
          if (_stmt.isNull(_columnIndexOfParentIdsJson)) {
            _tmpParentIdsJson = null
          } else {
            _tmpParentIdsJson = _stmt.getText(_columnIndexOfParentIdsJson)
          }
          val _tmpBatchId: String?
          if (_stmt.isNull(_columnIndexOfBatchId)) {
            _tmpBatchId = null
          } else {
            _tmpBatchId = _stmt.getText(_columnIndexOfBatchId)
          }
          val _tmpOrigin: String?
          if (_stmt.isNull(_columnIndexOfOrigin)) {
            _tmpOrigin = null
          } else {
            _tmpOrigin = _stmt.getText(_columnIndexOfOrigin)
          }
          val _tmpBirdCode: String?
          if (_stmt.isNull(_columnIndexOfBirdCode)) {
            _tmpBirdCode = null
          } else {
            _tmpBirdCode = _stmt.getText(_columnIndexOfBirdCode)
          }
          val _tmpAcquisitionPrice: Double?
          if (_stmt.isNull(_columnIndexOfAcquisitionPrice)) {
            _tmpAcquisitionPrice = null
          } else {
            _tmpAcquisitionPrice = _stmt.getDouble(_columnIndexOfAcquisitionPrice)
          }
          val _tmpAcquisitionDate: Long?
          if (_stmt.isNull(_columnIndexOfAcquisitionDate)) {
            _tmpAcquisitionDate = null
          } else {
            _tmpAcquisitionDate = _stmt.getLong(_columnIndexOfAcquisitionDate)
          }
          val _tmpAcquisitionSource: String?
          if (_stmt.isNull(_columnIndexOfAcquisitionSource)) {
            _tmpAcquisitionSource = null
          } else {
            _tmpAcquisitionSource = _stmt.getText(_columnIndexOfAcquisitionSource)
          }
          val _tmpAcquisitionSourceId: String?
          if (_stmt.isNull(_columnIndexOfAcquisitionSourceId)) {
            _tmpAcquisitionSourceId = null
          } else {
            _tmpAcquisitionSourceId = _stmt.getText(_columnIndexOfAcquisitionSourceId)
          }
          val _tmpAcquisitionNotes: String?
          if (_stmt.isNull(_columnIndexOfAcquisitionNotes)) {
            _tmpAcquisitionNotes = null
          } else {
            _tmpAcquisitionNotes = _stmt.getText(_columnIndexOfAcquisitionNotes)
          }
          val _tmpEstimatedValue: Double?
          if (_stmt.isNull(_columnIndexOfEstimatedValue)) {
            _tmpEstimatedValue = null
          } else {
            _tmpEstimatedValue = _stmt.getDouble(_columnIndexOfEstimatedValue)
          }
          val _tmpLastVaccinationDate: Long?
          if (_stmt.isNull(_columnIndexOfLastVaccinationDate)) {
            _tmpLastVaccinationDate = null
          } else {
            _tmpLastVaccinationDate = _stmt.getLong(_columnIndexOfLastVaccinationDate)
          }
          val _tmpNextVaccinationDate: Long?
          if (_stmt.isNull(_columnIndexOfNextVaccinationDate)) {
            _tmpNextVaccinationDate = null
          } else {
            _tmpNextVaccinationDate = _stmt.getLong(_columnIndexOfNextVaccinationDate)
          }
          val _tmpWeightGrams: Double?
          if (_stmt.isNull(_columnIndexOfWeightGrams)) {
            _tmpWeightGrams = null
          } else {
            _tmpWeightGrams = _stmt.getDouble(_columnIndexOfWeightGrams)
          }
          val _tmpMetadataJson: String
          _tmpMetadataJson = _stmt.getText(_columnIndexOfMetadataJson)
          val _tmpListedAt: Long?
          if (_stmt.isNull(_columnIndexOfListedAt)) {
            _tmpListedAt = null
          } else {
            _tmpListedAt = _stmt.getLong(_columnIndexOfListedAt)
          }
          val _tmpListingId: String?
          if (_stmt.isNull(_columnIndexOfListingId)) {
            _tmpListingId = null
          } else {
            _tmpListingId = _stmt.getText(_columnIndexOfListingId)
          }
          val _tmpSoldAt: Long?
          if (_stmt.isNull(_columnIndexOfSoldAt)) {
            _tmpSoldAt = null
          } else {
            _tmpSoldAt = _stmt.getLong(_columnIndexOfSoldAt)
          }
          val _tmpSoldToUserId: String?
          if (_stmt.isNull(_columnIndexOfSoldToUserId)) {
            _tmpSoldToUserId = null
          } else {
            _tmpSoldToUserId = _stmt.getText(_columnIndexOfSoldToUserId)
          }
          val _tmpSoldPrice: Double?
          if (_stmt.isNull(_columnIndexOfSoldPrice)) {
            _tmpSoldPrice = null
          } else {
            _tmpSoldPrice = _stmt.getDouble(_columnIndexOfSoldPrice)
          }
          val _tmpPreviousOwnerId: String?
          if (_stmt.isNull(_columnIndexOfPreviousOwnerId)) {
            _tmpPreviousOwnerId = null
          } else {
            _tmpPreviousOwnerId = _stmt.getText(_columnIndexOfPreviousOwnerId)
          }
          val _tmpTransferredAt: Long?
          if (_stmt.isNull(_columnIndexOfTransferredAt)) {
            _tmpTransferredAt = null
          } else {
            _tmpTransferredAt = _stmt.getLong(_columnIndexOfTransferredAt)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpIsDeleted: Boolean
          val _tmp_3: Int
          _tmp_3 = _stmt.getLong(_columnIndexOfIsDeleted).toInt()
          _tmpIsDeleted = _tmp_3 != 0
          val _tmpDeletedAt: Long?
          if (_stmt.isNull(_columnIndexOfDeletedAt)) {
            _tmpDeletedAt = null
          } else {
            _tmpDeletedAt = _stmt.getLong(_columnIndexOfDeletedAt)
          }
          val _tmpDirty: Boolean
          val _tmp_4: Int
          _tmp_4 = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp_4 != 0
          _item =
              FarmAssetEntity(_tmpAssetId,_tmpFarmerId,_tmpName,_tmpAssetType,_tmpCategory,_tmpStatus,_tmpIsShowcase,_tmpLocationName,_tmpLatitude,_tmpLongitude,_tmpQuantity,_tmpInitialQuantity,_tmpUnit,_tmpBirthDate,_tmpAgeWeeks,_tmpBreed,_tmpGender,_tmpColor,_tmpHealthStatus,_tmpRaisingPurpose,_tmpDescription,_tmpImageUrls,_tmpNotes,_tmpLifecycleSubStage,_tmpParentIdsJson,_tmpBatchId,_tmpOrigin,_tmpBirdCode,_tmpAcquisitionPrice,_tmpAcquisitionDate,_tmpAcquisitionSource,_tmpAcquisitionSourceId,_tmpAcquisitionNotes,_tmpEstimatedValue,_tmpLastVaccinationDate,_tmpNextVaccinationDate,_tmpWeightGrams,_tmpMetadataJson,_tmpListedAt,_tmpListingId,_tmpSoldAt,_tmpSoldToUserId,_tmpSoldPrice,_tmpPreviousOwnerId,_tmpTransferredAt,_tmpCreatedAt,_tmpUpdatedAt,_tmpIsDeleted,_tmpDeletedAt,_tmpDirty)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getAssetsNeedingVaccination(farmerId: String, now: Long):
      List<FarmAssetEntity> {
    val _sql: String =
        "SELECT * FROM farm_assets WHERE farmerId = ? AND nextVaccinationDate IS NOT NULL AND nextVaccinationDate <= ? AND isDeleted = 0"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, farmerId)
        _argIndex = 2
        _stmt.bindLong(_argIndex, now)
        val _columnIndexOfAssetId: Int = getColumnIndexOrThrow(_stmt, "assetId")
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfAssetType: Int = getColumnIndexOrThrow(_stmt, "assetType")
        val _columnIndexOfCategory: Int = getColumnIndexOrThrow(_stmt, "category")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfIsShowcase: Int = getColumnIndexOrThrow(_stmt, "isShowcase")
        val _columnIndexOfLocationName: Int = getColumnIndexOrThrow(_stmt, "locationName")
        val _columnIndexOfLatitude: Int = getColumnIndexOrThrow(_stmt, "latitude")
        val _columnIndexOfLongitude: Int = getColumnIndexOrThrow(_stmt, "longitude")
        val _columnIndexOfQuantity: Int = getColumnIndexOrThrow(_stmt, "quantity")
        val _columnIndexOfInitialQuantity: Int = getColumnIndexOrThrow(_stmt, "initialQuantity")
        val _columnIndexOfUnit: Int = getColumnIndexOrThrow(_stmt, "unit")
        val _columnIndexOfBirthDate: Int = getColumnIndexOrThrow(_stmt, "birthDate")
        val _columnIndexOfAgeWeeks: Int = getColumnIndexOrThrow(_stmt, "ageWeeks")
        val _columnIndexOfBreed: Int = getColumnIndexOrThrow(_stmt, "breed")
        val _columnIndexOfGender: Int = getColumnIndexOrThrow(_stmt, "gender")
        val _columnIndexOfColor: Int = getColumnIndexOrThrow(_stmt, "color")
        val _columnIndexOfHealthStatus: Int = getColumnIndexOrThrow(_stmt, "healthStatus")
        val _columnIndexOfRaisingPurpose: Int = getColumnIndexOrThrow(_stmt, "raisingPurpose")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfImageUrls: Int = getColumnIndexOrThrow(_stmt, "imageUrls")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfLifecycleSubStage: Int = getColumnIndexOrThrow(_stmt, "lifecycleSubStage")
        val _columnIndexOfParentIdsJson: Int = getColumnIndexOrThrow(_stmt, "parentIdsJson")
        val _columnIndexOfBatchId: Int = getColumnIndexOrThrow(_stmt, "batchId")
        val _columnIndexOfOrigin: Int = getColumnIndexOrThrow(_stmt, "origin")
        val _columnIndexOfBirdCode: Int = getColumnIndexOrThrow(_stmt, "birdCode")
        val _columnIndexOfAcquisitionPrice: Int = getColumnIndexOrThrow(_stmt, "acquisitionPrice")
        val _columnIndexOfAcquisitionDate: Int = getColumnIndexOrThrow(_stmt, "acquisitionDate")
        val _columnIndexOfAcquisitionSource: Int = getColumnIndexOrThrow(_stmt, "acquisitionSource")
        val _columnIndexOfAcquisitionSourceId: Int = getColumnIndexOrThrow(_stmt,
            "acquisitionSourceId")
        val _columnIndexOfAcquisitionNotes: Int = getColumnIndexOrThrow(_stmt, "acquisitionNotes")
        val _columnIndexOfEstimatedValue: Int = getColumnIndexOrThrow(_stmt, "estimatedValue")
        val _columnIndexOfLastVaccinationDate: Int = getColumnIndexOrThrow(_stmt,
            "lastVaccinationDate")
        val _columnIndexOfNextVaccinationDate: Int = getColumnIndexOrThrow(_stmt,
            "nextVaccinationDate")
        val _columnIndexOfWeightGrams: Int = getColumnIndexOrThrow(_stmt, "weightGrams")
        val _columnIndexOfMetadataJson: Int = getColumnIndexOrThrow(_stmt, "metadataJson")
        val _columnIndexOfListedAt: Int = getColumnIndexOrThrow(_stmt, "listedAt")
        val _columnIndexOfListingId: Int = getColumnIndexOrThrow(_stmt, "listingId")
        val _columnIndexOfSoldAt: Int = getColumnIndexOrThrow(_stmt, "soldAt")
        val _columnIndexOfSoldToUserId: Int = getColumnIndexOrThrow(_stmt, "soldToUserId")
        val _columnIndexOfSoldPrice: Int = getColumnIndexOrThrow(_stmt, "soldPrice")
        val _columnIndexOfPreviousOwnerId: Int = getColumnIndexOrThrow(_stmt, "previousOwnerId")
        val _columnIndexOfTransferredAt: Int = getColumnIndexOrThrow(_stmt, "transferredAt")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfIsDeleted: Int = getColumnIndexOrThrow(_stmt, "isDeleted")
        val _columnIndexOfDeletedAt: Int = getColumnIndexOrThrow(_stmt, "deletedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _result: MutableList<FarmAssetEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: FarmAssetEntity
          val _tmpAssetId: String
          _tmpAssetId = _stmt.getText(_columnIndexOfAssetId)
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpAssetType: String
          _tmpAssetType = _stmt.getText(_columnIndexOfAssetType)
          val _tmpCategory: String
          _tmpCategory = _stmt.getText(_columnIndexOfCategory)
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpIsShowcase: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsShowcase).toInt()
          _tmpIsShowcase = _tmp != 0
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
          val _tmpQuantity: Double
          _tmpQuantity = _stmt.getDouble(_columnIndexOfQuantity)
          val _tmpInitialQuantity: Double
          _tmpInitialQuantity = _stmt.getDouble(_columnIndexOfInitialQuantity)
          val _tmpUnit: String
          _tmpUnit = _stmt.getText(_columnIndexOfUnit)
          val _tmpBirthDate: Long?
          if (_stmt.isNull(_columnIndexOfBirthDate)) {
            _tmpBirthDate = null
          } else {
            _tmpBirthDate = _stmt.getLong(_columnIndexOfBirthDate)
          }
          val _tmpAgeWeeks: Int?
          if (_stmt.isNull(_columnIndexOfAgeWeeks)) {
            _tmpAgeWeeks = null
          } else {
            _tmpAgeWeeks = _stmt.getLong(_columnIndexOfAgeWeeks).toInt()
          }
          val _tmpBreed: String?
          if (_stmt.isNull(_columnIndexOfBreed)) {
            _tmpBreed = null
          } else {
            _tmpBreed = _stmt.getText(_columnIndexOfBreed)
          }
          val _tmpGender: String?
          if (_stmt.isNull(_columnIndexOfGender)) {
            _tmpGender = null
          } else {
            _tmpGender = _stmt.getText(_columnIndexOfGender)
          }
          val _tmpColor: String?
          if (_stmt.isNull(_columnIndexOfColor)) {
            _tmpColor = null
          } else {
            _tmpColor = _stmt.getText(_columnIndexOfColor)
          }
          val _tmpHealthStatus: String
          _tmpHealthStatus = _stmt.getText(_columnIndexOfHealthStatus)
          val _tmpRaisingPurpose: String?
          if (_stmt.isNull(_columnIndexOfRaisingPurpose)) {
            _tmpRaisingPurpose = null
          } else {
            _tmpRaisingPurpose = _stmt.getText(_columnIndexOfRaisingPurpose)
          }
          val _tmpDescription: String
          _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          val _tmpImageUrls: List<String>
          val _tmp_1: String?
          if (_stmt.isNull(_columnIndexOfImageUrls)) {
            _tmp_1 = null
          } else {
            _tmp_1 = _stmt.getText(_columnIndexOfImageUrls)
          }
          val _tmp_2: List<String>? = AppDatabase.Converters.toStringList(_tmp_1)
          if (_tmp_2 == null) {
            error("Expected NON-NULL 'kotlin.collections.List<kotlin.String>', but it was NULL.")
          } else {
            _tmpImageUrls = _tmp_2
          }
          val _tmpNotes: String?
          if (_stmt.isNull(_columnIndexOfNotes)) {
            _tmpNotes = null
          } else {
            _tmpNotes = _stmt.getText(_columnIndexOfNotes)
          }
          val _tmpLifecycleSubStage: String?
          if (_stmt.isNull(_columnIndexOfLifecycleSubStage)) {
            _tmpLifecycleSubStage = null
          } else {
            _tmpLifecycleSubStage = _stmt.getText(_columnIndexOfLifecycleSubStage)
          }
          val _tmpParentIdsJson: String?
          if (_stmt.isNull(_columnIndexOfParentIdsJson)) {
            _tmpParentIdsJson = null
          } else {
            _tmpParentIdsJson = _stmt.getText(_columnIndexOfParentIdsJson)
          }
          val _tmpBatchId: String?
          if (_stmt.isNull(_columnIndexOfBatchId)) {
            _tmpBatchId = null
          } else {
            _tmpBatchId = _stmt.getText(_columnIndexOfBatchId)
          }
          val _tmpOrigin: String?
          if (_stmt.isNull(_columnIndexOfOrigin)) {
            _tmpOrigin = null
          } else {
            _tmpOrigin = _stmt.getText(_columnIndexOfOrigin)
          }
          val _tmpBirdCode: String?
          if (_stmt.isNull(_columnIndexOfBirdCode)) {
            _tmpBirdCode = null
          } else {
            _tmpBirdCode = _stmt.getText(_columnIndexOfBirdCode)
          }
          val _tmpAcquisitionPrice: Double?
          if (_stmt.isNull(_columnIndexOfAcquisitionPrice)) {
            _tmpAcquisitionPrice = null
          } else {
            _tmpAcquisitionPrice = _stmt.getDouble(_columnIndexOfAcquisitionPrice)
          }
          val _tmpAcquisitionDate: Long?
          if (_stmt.isNull(_columnIndexOfAcquisitionDate)) {
            _tmpAcquisitionDate = null
          } else {
            _tmpAcquisitionDate = _stmt.getLong(_columnIndexOfAcquisitionDate)
          }
          val _tmpAcquisitionSource: String?
          if (_stmt.isNull(_columnIndexOfAcquisitionSource)) {
            _tmpAcquisitionSource = null
          } else {
            _tmpAcquisitionSource = _stmt.getText(_columnIndexOfAcquisitionSource)
          }
          val _tmpAcquisitionSourceId: String?
          if (_stmt.isNull(_columnIndexOfAcquisitionSourceId)) {
            _tmpAcquisitionSourceId = null
          } else {
            _tmpAcquisitionSourceId = _stmt.getText(_columnIndexOfAcquisitionSourceId)
          }
          val _tmpAcquisitionNotes: String?
          if (_stmt.isNull(_columnIndexOfAcquisitionNotes)) {
            _tmpAcquisitionNotes = null
          } else {
            _tmpAcquisitionNotes = _stmt.getText(_columnIndexOfAcquisitionNotes)
          }
          val _tmpEstimatedValue: Double?
          if (_stmt.isNull(_columnIndexOfEstimatedValue)) {
            _tmpEstimatedValue = null
          } else {
            _tmpEstimatedValue = _stmt.getDouble(_columnIndexOfEstimatedValue)
          }
          val _tmpLastVaccinationDate: Long?
          if (_stmt.isNull(_columnIndexOfLastVaccinationDate)) {
            _tmpLastVaccinationDate = null
          } else {
            _tmpLastVaccinationDate = _stmt.getLong(_columnIndexOfLastVaccinationDate)
          }
          val _tmpNextVaccinationDate: Long?
          if (_stmt.isNull(_columnIndexOfNextVaccinationDate)) {
            _tmpNextVaccinationDate = null
          } else {
            _tmpNextVaccinationDate = _stmt.getLong(_columnIndexOfNextVaccinationDate)
          }
          val _tmpWeightGrams: Double?
          if (_stmt.isNull(_columnIndexOfWeightGrams)) {
            _tmpWeightGrams = null
          } else {
            _tmpWeightGrams = _stmt.getDouble(_columnIndexOfWeightGrams)
          }
          val _tmpMetadataJson: String
          _tmpMetadataJson = _stmt.getText(_columnIndexOfMetadataJson)
          val _tmpListedAt: Long?
          if (_stmt.isNull(_columnIndexOfListedAt)) {
            _tmpListedAt = null
          } else {
            _tmpListedAt = _stmt.getLong(_columnIndexOfListedAt)
          }
          val _tmpListingId: String?
          if (_stmt.isNull(_columnIndexOfListingId)) {
            _tmpListingId = null
          } else {
            _tmpListingId = _stmt.getText(_columnIndexOfListingId)
          }
          val _tmpSoldAt: Long?
          if (_stmt.isNull(_columnIndexOfSoldAt)) {
            _tmpSoldAt = null
          } else {
            _tmpSoldAt = _stmt.getLong(_columnIndexOfSoldAt)
          }
          val _tmpSoldToUserId: String?
          if (_stmt.isNull(_columnIndexOfSoldToUserId)) {
            _tmpSoldToUserId = null
          } else {
            _tmpSoldToUserId = _stmt.getText(_columnIndexOfSoldToUserId)
          }
          val _tmpSoldPrice: Double?
          if (_stmt.isNull(_columnIndexOfSoldPrice)) {
            _tmpSoldPrice = null
          } else {
            _tmpSoldPrice = _stmt.getDouble(_columnIndexOfSoldPrice)
          }
          val _tmpPreviousOwnerId: String?
          if (_stmt.isNull(_columnIndexOfPreviousOwnerId)) {
            _tmpPreviousOwnerId = null
          } else {
            _tmpPreviousOwnerId = _stmt.getText(_columnIndexOfPreviousOwnerId)
          }
          val _tmpTransferredAt: Long?
          if (_stmt.isNull(_columnIndexOfTransferredAt)) {
            _tmpTransferredAt = null
          } else {
            _tmpTransferredAt = _stmt.getLong(_columnIndexOfTransferredAt)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpIsDeleted: Boolean
          val _tmp_3: Int
          _tmp_3 = _stmt.getLong(_columnIndexOfIsDeleted).toInt()
          _tmpIsDeleted = _tmp_3 != 0
          val _tmpDeletedAt: Long?
          if (_stmt.isNull(_columnIndexOfDeletedAt)) {
            _tmpDeletedAt = null
          } else {
            _tmpDeletedAt = _stmt.getLong(_columnIndexOfDeletedAt)
          }
          val _tmpDirty: Boolean
          val _tmp_4: Int
          _tmp_4 = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp_4 != 0
          _item =
              FarmAssetEntity(_tmpAssetId,_tmpFarmerId,_tmpName,_tmpAssetType,_tmpCategory,_tmpStatus,_tmpIsShowcase,_tmpLocationName,_tmpLatitude,_tmpLongitude,_tmpQuantity,_tmpInitialQuantity,_tmpUnit,_tmpBirthDate,_tmpAgeWeeks,_tmpBreed,_tmpGender,_tmpColor,_tmpHealthStatus,_tmpRaisingPurpose,_tmpDescription,_tmpImageUrls,_tmpNotes,_tmpLifecycleSubStage,_tmpParentIdsJson,_tmpBatchId,_tmpOrigin,_tmpBirdCode,_tmpAcquisitionPrice,_tmpAcquisitionDate,_tmpAcquisitionSource,_tmpAcquisitionSourceId,_tmpAcquisitionNotes,_tmpEstimatedValue,_tmpLastVaccinationDate,_tmpNextVaccinationDate,_tmpWeightGrams,_tmpMetadataJson,_tmpListedAt,_tmpListingId,_tmpSoldAt,_tmpSoldToUserId,_tmpSoldPrice,_tmpPreviousOwnerId,_tmpTransferredAt,_tmpCreatedAt,_tmpUpdatedAt,_tmpIsDeleted,_tmpDeletedAt,_tmpDirty)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getAllFarmerIds(): List<String> {
    val _sql: String = "SELECT DISTINCT farmerId FROM farm_assets WHERE isDeleted = 0"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
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

  public override suspend fun getCurrentQuantity(assetId: String): Double? {
    val _sql: String = "SELECT quantity FROM farm_assets WHERE assetId = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, assetId)
        val _result: Double?
        if (_stmt.step()) {
          if (_stmt.isNull(0)) {
            _result = null
          } else {
            _result = _stmt.getDouble(0)
          }
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getReadyToLayBirds(farmerId: String): Flow<List<FarmAssetEntity>> {
    val _sql: String =
        "SELECT * FROM farm_assets WHERE farmerId = ? AND gender = 'FEMALE' AND ageWeeks >= 18 AND ageWeeks <= 22 AND isDeleted = 0"
    return createFlow(__db, false, arrayOf("farm_assets")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, farmerId)
        val _columnIndexOfAssetId: Int = getColumnIndexOrThrow(_stmt, "assetId")
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfAssetType: Int = getColumnIndexOrThrow(_stmt, "assetType")
        val _columnIndexOfCategory: Int = getColumnIndexOrThrow(_stmt, "category")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfIsShowcase: Int = getColumnIndexOrThrow(_stmt, "isShowcase")
        val _columnIndexOfLocationName: Int = getColumnIndexOrThrow(_stmt, "locationName")
        val _columnIndexOfLatitude: Int = getColumnIndexOrThrow(_stmt, "latitude")
        val _columnIndexOfLongitude: Int = getColumnIndexOrThrow(_stmt, "longitude")
        val _columnIndexOfQuantity: Int = getColumnIndexOrThrow(_stmt, "quantity")
        val _columnIndexOfInitialQuantity: Int = getColumnIndexOrThrow(_stmt, "initialQuantity")
        val _columnIndexOfUnit: Int = getColumnIndexOrThrow(_stmt, "unit")
        val _columnIndexOfBirthDate: Int = getColumnIndexOrThrow(_stmt, "birthDate")
        val _columnIndexOfAgeWeeks: Int = getColumnIndexOrThrow(_stmt, "ageWeeks")
        val _columnIndexOfBreed: Int = getColumnIndexOrThrow(_stmt, "breed")
        val _columnIndexOfGender: Int = getColumnIndexOrThrow(_stmt, "gender")
        val _columnIndexOfColor: Int = getColumnIndexOrThrow(_stmt, "color")
        val _columnIndexOfHealthStatus: Int = getColumnIndexOrThrow(_stmt, "healthStatus")
        val _columnIndexOfRaisingPurpose: Int = getColumnIndexOrThrow(_stmt, "raisingPurpose")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfImageUrls: Int = getColumnIndexOrThrow(_stmt, "imageUrls")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfLifecycleSubStage: Int = getColumnIndexOrThrow(_stmt, "lifecycleSubStage")
        val _columnIndexOfParentIdsJson: Int = getColumnIndexOrThrow(_stmt, "parentIdsJson")
        val _columnIndexOfBatchId: Int = getColumnIndexOrThrow(_stmt, "batchId")
        val _columnIndexOfOrigin: Int = getColumnIndexOrThrow(_stmt, "origin")
        val _columnIndexOfBirdCode: Int = getColumnIndexOrThrow(_stmt, "birdCode")
        val _columnIndexOfAcquisitionPrice: Int = getColumnIndexOrThrow(_stmt, "acquisitionPrice")
        val _columnIndexOfAcquisitionDate: Int = getColumnIndexOrThrow(_stmt, "acquisitionDate")
        val _columnIndexOfAcquisitionSource: Int = getColumnIndexOrThrow(_stmt, "acquisitionSource")
        val _columnIndexOfAcquisitionSourceId: Int = getColumnIndexOrThrow(_stmt,
            "acquisitionSourceId")
        val _columnIndexOfAcquisitionNotes: Int = getColumnIndexOrThrow(_stmt, "acquisitionNotes")
        val _columnIndexOfEstimatedValue: Int = getColumnIndexOrThrow(_stmt, "estimatedValue")
        val _columnIndexOfLastVaccinationDate: Int = getColumnIndexOrThrow(_stmt,
            "lastVaccinationDate")
        val _columnIndexOfNextVaccinationDate: Int = getColumnIndexOrThrow(_stmt,
            "nextVaccinationDate")
        val _columnIndexOfWeightGrams: Int = getColumnIndexOrThrow(_stmt, "weightGrams")
        val _columnIndexOfMetadataJson: Int = getColumnIndexOrThrow(_stmt, "metadataJson")
        val _columnIndexOfListedAt: Int = getColumnIndexOrThrow(_stmt, "listedAt")
        val _columnIndexOfListingId: Int = getColumnIndexOrThrow(_stmt, "listingId")
        val _columnIndexOfSoldAt: Int = getColumnIndexOrThrow(_stmt, "soldAt")
        val _columnIndexOfSoldToUserId: Int = getColumnIndexOrThrow(_stmt, "soldToUserId")
        val _columnIndexOfSoldPrice: Int = getColumnIndexOrThrow(_stmt, "soldPrice")
        val _columnIndexOfPreviousOwnerId: Int = getColumnIndexOrThrow(_stmt, "previousOwnerId")
        val _columnIndexOfTransferredAt: Int = getColumnIndexOrThrow(_stmt, "transferredAt")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfIsDeleted: Int = getColumnIndexOrThrow(_stmt, "isDeleted")
        val _columnIndexOfDeletedAt: Int = getColumnIndexOrThrow(_stmt, "deletedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _result: MutableList<FarmAssetEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: FarmAssetEntity
          val _tmpAssetId: String
          _tmpAssetId = _stmt.getText(_columnIndexOfAssetId)
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpAssetType: String
          _tmpAssetType = _stmt.getText(_columnIndexOfAssetType)
          val _tmpCategory: String
          _tmpCategory = _stmt.getText(_columnIndexOfCategory)
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpIsShowcase: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsShowcase).toInt()
          _tmpIsShowcase = _tmp != 0
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
          val _tmpQuantity: Double
          _tmpQuantity = _stmt.getDouble(_columnIndexOfQuantity)
          val _tmpInitialQuantity: Double
          _tmpInitialQuantity = _stmt.getDouble(_columnIndexOfInitialQuantity)
          val _tmpUnit: String
          _tmpUnit = _stmt.getText(_columnIndexOfUnit)
          val _tmpBirthDate: Long?
          if (_stmt.isNull(_columnIndexOfBirthDate)) {
            _tmpBirthDate = null
          } else {
            _tmpBirthDate = _stmt.getLong(_columnIndexOfBirthDate)
          }
          val _tmpAgeWeeks: Int?
          if (_stmt.isNull(_columnIndexOfAgeWeeks)) {
            _tmpAgeWeeks = null
          } else {
            _tmpAgeWeeks = _stmt.getLong(_columnIndexOfAgeWeeks).toInt()
          }
          val _tmpBreed: String?
          if (_stmt.isNull(_columnIndexOfBreed)) {
            _tmpBreed = null
          } else {
            _tmpBreed = _stmt.getText(_columnIndexOfBreed)
          }
          val _tmpGender: String?
          if (_stmt.isNull(_columnIndexOfGender)) {
            _tmpGender = null
          } else {
            _tmpGender = _stmt.getText(_columnIndexOfGender)
          }
          val _tmpColor: String?
          if (_stmt.isNull(_columnIndexOfColor)) {
            _tmpColor = null
          } else {
            _tmpColor = _stmt.getText(_columnIndexOfColor)
          }
          val _tmpHealthStatus: String
          _tmpHealthStatus = _stmt.getText(_columnIndexOfHealthStatus)
          val _tmpRaisingPurpose: String?
          if (_stmt.isNull(_columnIndexOfRaisingPurpose)) {
            _tmpRaisingPurpose = null
          } else {
            _tmpRaisingPurpose = _stmt.getText(_columnIndexOfRaisingPurpose)
          }
          val _tmpDescription: String
          _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          val _tmpImageUrls: List<String>
          val _tmp_1: String?
          if (_stmt.isNull(_columnIndexOfImageUrls)) {
            _tmp_1 = null
          } else {
            _tmp_1 = _stmt.getText(_columnIndexOfImageUrls)
          }
          val _tmp_2: List<String>? = AppDatabase.Converters.toStringList(_tmp_1)
          if (_tmp_2 == null) {
            error("Expected NON-NULL 'kotlin.collections.List<kotlin.String>', but it was NULL.")
          } else {
            _tmpImageUrls = _tmp_2
          }
          val _tmpNotes: String?
          if (_stmt.isNull(_columnIndexOfNotes)) {
            _tmpNotes = null
          } else {
            _tmpNotes = _stmt.getText(_columnIndexOfNotes)
          }
          val _tmpLifecycleSubStage: String?
          if (_stmt.isNull(_columnIndexOfLifecycleSubStage)) {
            _tmpLifecycleSubStage = null
          } else {
            _tmpLifecycleSubStage = _stmt.getText(_columnIndexOfLifecycleSubStage)
          }
          val _tmpParentIdsJson: String?
          if (_stmt.isNull(_columnIndexOfParentIdsJson)) {
            _tmpParentIdsJson = null
          } else {
            _tmpParentIdsJson = _stmt.getText(_columnIndexOfParentIdsJson)
          }
          val _tmpBatchId: String?
          if (_stmt.isNull(_columnIndexOfBatchId)) {
            _tmpBatchId = null
          } else {
            _tmpBatchId = _stmt.getText(_columnIndexOfBatchId)
          }
          val _tmpOrigin: String?
          if (_stmt.isNull(_columnIndexOfOrigin)) {
            _tmpOrigin = null
          } else {
            _tmpOrigin = _stmt.getText(_columnIndexOfOrigin)
          }
          val _tmpBirdCode: String?
          if (_stmt.isNull(_columnIndexOfBirdCode)) {
            _tmpBirdCode = null
          } else {
            _tmpBirdCode = _stmt.getText(_columnIndexOfBirdCode)
          }
          val _tmpAcquisitionPrice: Double?
          if (_stmt.isNull(_columnIndexOfAcquisitionPrice)) {
            _tmpAcquisitionPrice = null
          } else {
            _tmpAcquisitionPrice = _stmt.getDouble(_columnIndexOfAcquisitionPrice)
          }
          val _tmpAcquisitionDate: Long?
          if (_stmt.isNull(_columnIndexOfAcquisitionDate)) {
            _tmpAcquisitionDate = null
          } else {
            _tmpAcquisitionDate = _stmt.getLong(_columnIndexOfAcquisitionDate)
          }
          val _tmpAcquisitionSource: String?
          if (_stmt.isNull(_columnIndexOfAcquisitionSource)) {
            _tmpAcquisitionSource = null
          } else {
            _tmpAcquisitionSource = _stmt.getText(_columnIndexOfAcquisitionSource)
          }
          val _tmpAcquisitionSourceId: String?
          if (_stmt.isNull(_columnIndexOfAcquisitionSourceId)) {
            _tmpAcquisitionSourceId = null
          } else {
            _tmpAcquisitionSourceId = _stmt.getText(_columnIndexOfAcquisitionSourceId)
          }
          val _tmpAcquisitionNotes: String?
          if (_stmt.isNull(_columnIndexOfAcquisitionNotes)) {
            _tmpAcquisitionNotes = null
          } else {
            _tmpAcquisitionNotes = _stmt.getText(_columnIndexOfAcquisitionNotes)
          }
          val _tmpEstimatedValue: Double?
          if (_stmt.isNull(_columnIndexOfEstimatedValue)) {
            _tmpEstimatedValue = null
          } else {
            _tmpEstimatedValue = _stmt.getDouble(_columnIndexOfEstimatedValue)
          }
          val _tmpLastVaccinationDate: Long?
          if (_stmt.isNull(_columnIndexOfLastVaccinationDate)) {
            _tmpLastVaccinationDate = null
          } else {
            _tmpLastVaccinationDate = _stmt.getLong(_columnIndexOfLastVaccinationDate)
          }
          val _tmpNextVaccinationDate: Long?
          if (_stmt.isNull(_columnIndexOfNextVaccinationDate)) {
            _tmpNextVaccinationDate = null
          } else {
            _tmpNextVaccinationDate = _stmt.getLong(_columnIndexOfNextVaccinationDate)
          }
          val _tmpWeightGrams: Double?
          if (_stmt.isNull(_columnIndexOfWeightGrams)) {
            _tmpWeightGrams = null
          } else {
            _tmpWeightGrams = _stmt.getDouble(_columnIndexOfWeightGrams)
          }
          val _tmpMetadataJson: String
          _tmpMetadataJson = _stmt.getText(_columnIndexOfMetadataJson)
          val _tmpListedAt: Long?
          if (_stmt.isNull(_columnIndexOfListedAt)) {
            _tmpListedAt = null
          } else {
            _tmpListedAt = _stmt.getLong(_columnIndexOfListedAt)
          }
          val _tmpListingId: String?
          if (_stmt.isNull(_columnIndexOfListingId)) {
            _tmpListingId = null
          } else {
            _tmpListingId = _stmt.getText(_columnIndexOfListingId)
          }
          val _tmpSoldAt: Long?
          if (_stmt.isNull(_columnIndexOfSoldAt)) {
            _tmpSoldAt = null
          } else {
            _tmpSoldAt = _stmt.getLong(_columnIndexOfSoldAt)
          }
          val _tmpSoldToUserId: String?
          if (_stmt.isNull(_columnIndexOfSoldToUserId)) {
            _tmpSoldToUserId = null
          } else {
            _tmpSoldToUserId = _stmt.getText(_columnIndexOfSoldToUserId)
          }
          val _tmpSoldPrice: Double?
          if (_stmt.isNull(_columnIndexOfSoldPrice)) {
            _tmpSoldPrice = null
          } else {
            _tmpSoldPrice = _stmt.getDouble(_columnIndexOfSoldPrice)
          }
          val _tmpPreviousOwnerId: String?
          if (_stmt.isNull(_columnIndexOfPreviousOwnerId)) {
            _tmpPreviousOwnerId = null
          } else {
            _tmpPreviousOwnerId = _stmt.getText(_columnIndexOfPreviousOwnerId)
          }
          val _tmpTransferredAt: Long?
          if (_stmt.isNull(_columnIndexOfTransferredAt)) {
            _tmpTransferredAt = null
          } else {
            _tmpTransferredAt = _stmt.getLong(_columnIndexOfTransferredAt)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpIsDeleted: Boolean
          val _tmp_3: Int
          _tmp_3 = _stmt.getLong(_columnIndexOfIsDeleted).toInt()
          _tmpIsDeleted = _tmp_3 != 0
          val _tmpDeletedAt: Long?
          if (_stmt.isNull(_columnIndexOfDeletedAt)) {
            _tmpDeletedAt = null
          } else {
            _tmpDeletedAt = _stmt.getLong(_columnIndexOfDeletedAt)
          }
          val _tmpDirty: Boolean
          val _tmp_4: Int
          _tmp_4 = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp_4 != 0
          _item =
              FarmAssetEntity(_tmpAssetId,_tmpFarmerId,_tmpName,_tmpAssetType,_tmpCategory,_tmpStatus,_tmpIsShowcase,_tmpLocationName,_tmpLatitude,_tmpLongitude,_tmpQuantity,_tmpInitialQuantity,_tmpUnit,_tmpBirthDate,_tmpAgeWeeks,_tmpBreed,_tmpGender,_tmpColor,_tmpHealthStatus,_tmpRaisingPurpose,_tmpDescription,_tmpImageUrls,_tmpNotes,_tmpLifecycleSubStage,_tmpParentIdsJson,_tmpBatchId,_tmpOrigin,_tmpBirdCode,_tmpAcquisitionPrice,_tmpAcquisitionDate,_tmpAcquisitionSource,_tmpAcquisitionSourceId,_tmpAcquisitionNotes,_tmpEstimatedValue,_tmpLastVaccinationDate,_tmpNextVaccinationDate,_tmpWeightGrams,_tmpMetadataJson,_tmpListedAt,_tmpListingId,_tmpSoldAt,_tmpSoldToUserId,_tmpSoldPrice,_tmpPreviousOwnerId,_tmpTransferredAt,_tmpCreatedAt,_tmpUpdatedAt,_tmpIsDeleted,_tmpDeletedAt,_tmpDirty)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getCullCandidates(farmerId: String): Flow<List<FarmAssetEntity>> {
    val _sql: String =
        "SELECT * FROM farm_assets WHERE farmerId = ? AND (ageWeeks > 72 OR healthStatus = 'SICK') AND isDeleted = 0"
    return createFlow(__db, false, arrayOf("farm_assets")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, farmerId)
        val _columnIndexOfAssetId: Int = getColumnIndexOrThrow(_stmt, "assetId")
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfAssetType: Int = getColumnIndexOrThrow(_stmt, "assetType")
        val _columnIndexOfCategory: Int = getColumnIndexOrThrow(_stmt, "category")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfIsShowcase: Int = getColumnIndexOrThrow(_stmt, "isShowcase")
        val _columnIndexOfLocationName: Int = getColumnIndexOrThrow(_stmt, "locationName")
        val _columnIndexOfLatitude: Int = getColumnIndexOrThrow(_stmt, "latitude")
        val _columnIndexOfLongitude: Int = getColumnIndexOrThrow(_stmt, "longitude")
        val _columnIndexOfQuantity: Int = getColumnIndexOrThrow(_stmt, "quantity")
        val _columnIndexOfInitialQuantity: Int = getColumnIndexOrThrow(_stmt, "initialQuantity")
        val _columnIndexOfUnit: Int = getColumnIndexOrThrow(_stmt, "unit")
        val _columnIndexOfBirthDate: Int = getColumnIndexOrThrow(_stmt, "birthDate")
        val _columnIndexOfAgeWeeks: Int = getColumnIndexOrThrow(_stmt, "ageWeeks")
        val _columnIndexOfBreed: Int = getColumnIndexOrThrow(_stmt, "breed")
        val _columnIndexOfGender: Int = getColumnIndexOrThrow(_stmt, "gender")
        val _columnIndexOfColor: Int = getColumnIndexOrThrow(_stmt, "color")
        val _columnIndexOfHealthStatus: Int = getColumnIndexOrThrow(_stmt, "healthStatus")
        val _columnIndexOfRaisingPurpose: Int = getColumnIndexOrThrow(_stmt, "raisingPurpose")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfImageUrls: Int = getColumnIndexOrThrow(_stmt, "imageUrls")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfLifecycleSubStage: Int = getColumnIndexOrThrow(_stmt, "lifecycleSubStage")
        val _columnIndexOfParentIdsJson: Int = getColumnIndexOrThrow(_stmt, "parentIdsJson")
        val _columnIndexOfBatchId: Int = getColumnIndexOrThrow(_stmt, "batchId")
        val _columnIndexOfOrigin: Int = getColumnIndexOrThrow(_stmt, "origin")
        val _columnIndexOfBirdCode: Int = getColumnIndexOrThrow(_stmt, "birdCode")
        val _columnIndexOfAcquisitionPrice: Int = getColumnIndexOrThrow(_stmt, "acquisitionPrice")
        val _columnIndexOfAcquisitionDate: Int = getColumnIndexOrThrow(_stmt, "acquisitionDate")
        val _columnIndexOfAcquisitionSource: Int = getColumnIndexOrThrow(_stmt, "acquisitionSource")
        val _columnIndexOfAcquisitionSourceId: Int = getColumnIndexOrThrow(_stmt,
            "acquisitionSourceId")
        val _columnIndexOfAcquisitionNotes: Int = getColumnIndexOrThrow(_stmt, "acquisitionNotes")
        val _columnIndexOfEstimatedValue: Int = getColumnIndexOrThrow(_stmt, "estimatedValue")
        val _columnIndexOfLastVaccinationDate: Int = getColumnIndexOrThrow(_stmt,
            "lastVaccinationDate")
        val _columnIndexOfNextVaccinationDate: Int = getColumnIndexOrThrow(_stmt,
            "nextVaccinationDate")
        val _columnIndexOfWeightGrams: Int = getColumnIndexOrThrow(_stmt, "weightGrams")
        val _columnIndexOfMetadataJson: Int = getColumnIndexOrThrow(_stmt, "metadataJson")
        val _columnIndexOfListedAt: Int = getColumnIndexOrThrow(_stmt, "listedAt")
        val _columnIndexOfListingId: Int = getColumnIndexOrThrow(_stmt, "listingId")
        val _columnIndexOfSoldAt: Int = getColumnIndexOrThrow(_stmt, "soldAt")
        val _columnIndexOfSoldToUserId: Int = getColumnIndexOrThrow(_stmt, "soldToUserId")
        val _columnIndexOfSoldPrice: Int = getColumnIndexOrThrow(_stmt, "soldPrice")
        val _columnIndexOfPreviousOwnerId: Int = getColumnIndexOrThrow(_stmt, "previousOwnerId")
        val _columnIndexOfTransferredAt: Int = getColumnIndexOrThrow(_stmt, "transferredAt")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfIsDeleted: Int = getColumnIndexOrThrow(_stmt, "isDeleted")
        val _columnIndexOfDeletedAt: Int = getColumnIndexOrThrow(_stmt, "deletedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _result: MutableList<FarmAssetEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: FarmAssetEntity
          val _tmpAssetId: String
          _tmpAssetId = _stmt.getText(_columnIndexOfAssetId)
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpAssetType: String
          _tmpAssetType = _stmt.getText(_columnIndexOfAssetType)
          val _tmpCategory: String
          _tmpCategory = _stmt.getText(_columnIndexOfCategory)
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpIsShowcase: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsShowcase).toInt()
          _tmpIsShowcase = _tmp != 0
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
          val _tmpQuantity: Double
          _tmpQuantity = _stmt.getDouble(_columnIndexOfQuantity)
          val _tmpInitialQuantity: Double
          _tmpInitialQuantity = _stmt.getDouble(_columnIndexOfInitialQuantity)
          val _tmpUnit: String
          _tmpUnit = _stmt.getText(_columnIndexOfUnit)
          val _tmpBirthDate: Long?
          if (_stmt.isNull(_columnIndexOfBirthDate)) {
            _tmpBirthDate = null
          } else {
            _tmpBirthDate = _stmt.getLong(_columnIndexOfBirthDate)
          }
          val _tmpAgeWeeks: Int?
          if (_stmt.isNull(_columnIndexOfAgeWeeks)) {
            _tmpAgeWeeks = null
          } else {
            _tmpAgeWeeks = _stmt.getLong(_columnIndexOfAgeWeeks).toInt()
          }
          val _tmpBreed: String?
          if (_stmt.isNull(_columnIndexOfBreed)) {
            _tmpBreed = null
          } else {
            _tmpBreed = _stmt.getText(_columnIndexOfBreed)
          }
          val _tmpGender: String?
          if (_stmt.isNull(_columnIndexOfGender)) {
            _tmpGender = null
          } else {
            _tmpGender = _stmt.getText(_columnIndexOfGender)
          }
          val _tmpColor: String?
          if (_stmt.isNull(_columnIndexOfColor)) {
            _tmpColor = null
          } else {
            _tmpColor = _stmt.getText(_columnIndexOfColor)
          }
          val _tmpHealthStatus: String
          _tmpHealthStatus = _stmt.getText(_columnIndexOfHealthStatus)
          val _tmpRaisingPurpose: String?
          if (_stmt.isNull(_columnIndexOfRaisingPurpose)) {
            _tmpRaisingPurpose = null
          } else {
            _tmpRaisingPurpose = _stmt.getText(_columnIndexOfRaisingPurpose)
          }
          val _tmpDescription: String
          _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          val _tmpImageUrls: List<String>
          val _tmp_1: String?
          if (_stmt.isNull(_columnIndexOfImageUrls)) {
            _tmp_1 = null
          } else {
            _tmp_1 = _stmt.getText(_columnIndexOfImageUrls)
          }
          val _tmp_2: List<String>? = AppDatabase.Converters.toStringList(_tmp_1)
          if (_tmp_2 == null) {
            error("Expected NON-NULL 'kotlin.collections.List<kotlin.String>', but it was NULL.")
          } else {
            _tmpImageUrls = _tmp_2
          }
          val _tmpNotes: String?
          if (_stmt.isNull(_columnIndexOfNotes)) {
            _tmpNotes = null
          } else {
            _tmpNotes = _stmt.getText(_columnIndexOfNotes)
          }
          val _tmpLifecycleSubStage: String?
          if (_stmt.isNull(_columnIndexOfLifecycleSubStage)) {
            _tmpLifecycleSubStage = null
          } else {
            _tmpLifecycleSubStage = _stmt.getText(_columnIndexOfLifecycleSubStage)
          }
          val _tmpParentIdsJson: String?
          if (_stmt.isNull(_columnIndexOfParentIdsJson)) {
            _tmpParentIdsJson = null
          } else {
            _tmpParentIdsJson = _stmt.getText(_columnIndexOfParentIdsJson)
          }
          val _tmpBatchId: String?
          if (_stmt.isNull(_columnIndexOfBatchId)) {
            _tmpBatchId = null
          } else {
            _tmpBatchId = _stmt.getText(_columnIndexOfBatchId)
          }
          val _tmpOrigin: String?
          if (_stmt.isNull(_columnIndexOfOrigin)) {
            _tmpOrigin = null
          } else {
            _tmpOrigin = _stmt.getText(_columnIndexOfOrigin)
          }
          val _tmpBirdCode: String?
          if (_stmt.isNull(_columnIndexOfBirdCode)) {
            _tmpBirdCode = null
          } else {
            _tmpBirdCode = _stmt.getText(_columnIndexOfBirdCode)
          }
          val _tmpAcquisitionPrice: Double?
          if (_stmt.isNull(_columnIndexOfAcquisitionPrice)) {
            _tmpAcquisitionPrice = null
          } else {
            _tmpAcquisitionPrice = _stmt.getDouble(_columnIndexOfAcquisitionPrice)
          }
          val _tmpAcquisitionDate: Long?
          if (_stmt.isNull(_columnIndexOfAcquisitionDate)) {
            _tmpAcquisitionDate = null
          } else {
            _tmpAcquisitionDate = _stmt.getLong(_columnIndexOfAcquisitionDate)
          }
          val _tmpAcquisitionSource: String?
          if (_stmt.isNull(_columnIndexOfAcquisitionSource)) {
            _tmpAcquisitionSource = null
          } else {
            _tmpAcquisitionSource = _stmt.getText(_columnIndexOfAcquisitionSource)
          }
          val _tmpAcquisitionSourceId: String?
          if (_stmt.isNull(_columnIndexOfAcquisitionSourceId)) {
            _tmpAcquisitionSourceId = null
          } else {
            _tmpAcquisitionSourceId = _stmt.getText(_columnIndexOfAcquisitionSourceId)
          }
          val _tmpAcquisitionNotes: String?
          if (_stmt.isNull(_columnIndexOfAcquisitionNotes)) {
            _tmpAcquisitionNotes = null
          } else {
            _tmpAcquisitionNotes = _stmt.getText(_columnIndexOfAcquisitionNotes)
          }
          val _tmpEstimatedValue: Double?
          if (_stmt.isNull(_columnIndexOfEstimatedValue)) {
            _tmpEstimatedValue = null
          } else {
            _tmpEstimatedValue = _stmt.getDouble(_columnIndexOfEstimatedValue)
          }
          val _tmpLastVaccinationDate: Long?
          if (_stmt.isNull(_columnIndexOfLastVaccinationDate)) {
            _tmpLastVaccinationDate = null
          } else {
            _tmpLastVaccinationDate = _stmt.getLong(_columnIndexOfLastVaccinationDate)
          }
          val _tmpNextVaccinationDate: Long?
          if (_stmt.isNull(_columnIndexOfNextVaccinationDate)) {
            _tmpNextVaccinationDate = null
          } else {
            _tmpNextVaccinationDate = _stmt.getLong(_columnIndexOfNextVaccinationDate)
          }
          val _tmpWeightGrams: Double?
          if (_stmt.isNull(_columnIndexOfWeightGrams)) {
            _tmpWeightGrams = null
          } else {
            _tmpWeightGrams = _stmt.getDouble(_columnIndexOfWeightGrams)
          }
          val _tmpMetadataJson: String
          _tmpMetadataJson = _stmt.getText(_columnIndexOfMetadataJson)
          val _tmpListedAt: Long?
          if (_stmt.isNull(_columnIndexOfListedAt)) {
            _tmpListedAt = null
          } else {
            _tmpListedAt = _stmt.getLong(_columnIndexOfListedAt)
          }
          val _tmpListingId: String?
          if (_stmt.isNull(_columnIndexOfListingId)) {
            _tmpListingId = null
          } else {
            _tmpListingId = _stmt.getText(_columnIndexOfListingId)
          }
          val _tmpSoldAt: Long?
          if (_stmt.isNull(_columnIndexOfSoldAt)) {
            _tmpSoldAt = null
          } else {
            _tmpSoldAt = _stmt.getLong(_columnIndexOfSoldAt)
          }
          val _tmpSoldToUserId: String?
          if (_stmt.isNull(_columnIndexOfSoldToUserId)) {
            _tmpSoldToUserId = null
          } else {
            _tmpSoldToUserId = _stmt.getText(_columnIndexOfSoldToUserId)
          }
          val _tmpSoldPrice: Double?
          if (_stmt.isNull(_columnIndexOfSoldPrice)) {
            _tmpSoldPrice = null
          } else {
            _tmpSoldPrice = _stmt.getDouble(_columnIndexOfSoldPrice)
          }
          val _tmpPreviousOwnerId: String?
          if (_stmt.isNull(_columnIndexOfPreviousOwnerId)) {
            _tmpPreviousOwnerId = null
          } else {
            _tmpPreviousOwnerId = _stmt.getText(_columnIndexOfPreviousOwnerId)
          }
          val _tmpTransferredAt: Long?
          if (_stmt.isNull(_columnIndexOfTransferredAt)) {
            _tmpTransferredAt = null
          } else {
            _tmpTransferredAt = _stmt.getLong(_columnIndexOfTransferredAt)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpIsDeleted: Boolean
          val _tmp_3: Int
          _tmp_3 = _stmt.getLong(_columnIndexOfIsDeleted).toInt()
          _tmpIsDeleted = _tmp_3 != 0
          val _tmpDeletedAt: Long?
          if (_stmt.isNull(_columnIndexOfDeletedAt)) {
            _tmpDeletedAt = null
          } else {
            _tmpDeletedAt = _stmt.getLong(_columnIndexOfDeletedAt)
          }
          val _tmpDirty: Boolean
          val _tmp_4: Int
          _tmp_4 = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp_4 != 0
          _item =
              FarmAssetEntity(_tmpAssetId,_tmpFarmerId,_tmpName,_tmpAssetType,_tmpCategory,_tmpStatus,_tmpIsShowcase,_tmpLocationName,_tmpLatitude,_tmpLongitude,_tmpQuantity,_tmpInitialQuantity,_tmpUnit,_tmpBirthDate,_tmpAgeWeeks,_tmpBreed,_tmpGender,_tmpColor,_tmpHealthStatus,_tmpRaisingPurpose,_tmpDescription,_tmpImageUrls,_tmpNotes,_tmpLifecycleSubStage,_tmpParentIdsJson,_tmpBatchId,_tmpOrigin,_tmpBirdCode,_tmpAcquisitionPrice,_tmpAcquisitionDate,_tmpAcquisitionSource,_tmpAcquisitionSourceId,_tmpAcquisitionNotes,_tmpEstimatedValue,_tmpLastVaccinationDate,_tmpNextVaccinationDate,_tmpWeightGrams,_tmpMetadataJson,_tmpListedAt,_tmpListingId,_tmpSoldAt,_tmpSoldToUserId,_tmpSoldPrice,_tmpPreviousOwnerId,_tmpTransferredAt,_tmpCreatedAt,_tmpUpdatedAt,_tmpIsDeleted,_tmpDeletedAt,_tmpDirty)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getVaccinationDueSoon(
    farmerId: String,
    now: Long,
    future: Long,
  ): Flow<List<FarmAssetEntity>> {
    val _sql: String =
        "SELECT * FROM farm_assets WHERE farmerId = ? AND nextVaccinationDate IS NOT NULL AND nextVaccinationDate BETWEEN ? AND ? AND isDeleted = 0"
    return createFlow(__db, false, arrayOf("farm_assets")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, farmerId)
        _argIndex = 2
        _stmt.bindLong(_argIndex, now)
        _argIndex = 3
        _stmt.bindLong(_argIndex, future)
        val _columnIndexOfAssetId: Int = getColumnIndexOrThrow(_stmt, "assetId")
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfAssetType: Int = getColumnIndexOrThrow(_stmt, "assetType")
        val _columnIndexOfCategory: Int = getColumnIndexOrThrow(_stmt, "category")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfIsShowcase: Int = getColumnIndexOrThrow(_stmt, "isShowcase")
        val _columnIndexOfLocationName: Int = getColumnIndexOrThrow(_stmt, "locationName")
        val _columnIndexOfLatitude: Int = getColumnIndexOrThrow(_stmt, "latitude")
        val _columnIndexOfLongitude: Int = getColumnIndexOrThrow(_stmt, "longitude")
        val _columnIndexOfQuantity: Int = getColumnIndexOrThrow(_stmt, "quantity")
        val _columnIndexOfInitialQuantity: Int = getColumnIndexOrThrow(_stmt, "initialQuantity")
        val _columnIndexOfUnit: Int = getColumnIndexOrThrow(_stmt, "unit")
        val _columnIndexOfBirthDate: Int = getColumnIndexOrThrow(_stmt, "birthDate")
        val _columnIndexOfAgeWeeks: Int = getColumnIndexOrThrow(_stmt, "ageWeeks")
        val _columnIndexOfBreed: Int = getColumnIndexOrThrow(_stmt, "breed")
        val _columnIndexOfGender: Int = getColumnIndexOrThrow(_stmt, "gender")
        val _columnIndexOfColor: Int = getColumnIndexOrThrow(_stmt, "color")
        val _columnIndexOfHealthStatus: Int = getColumnIndexOrThrow(_stmt, "healthStatus")
        val _columnIndexOfRaisingPurpose: Int = getColumnIndexOrThrow(_stmt, "raisingPurpose")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfImageUrls: Int = getColumnIndexOrThrow(_stmt, "imageUrls")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfLifecycleSubStage: Int = getColumnIndexOrThrow(_stmt, "lifecycleSubStage")
        val _columnIndexOfParentIdsJson: Int = getColumnIndexOrThrow(_stmt, "parentIdsJson")
        val _columnIndexOfBatchId: Int = getColumnIndexOrThrow(_stmt, "batchId")
        val _columnIndexOfOrigin: Int = getColumnIndexOrThrow(_stmt, "origin")
        val _columnIndexOfBirdCode: Int = getColumnIndexOrThrow(_stmt, "birdCode")
        val _columnIndexOfAcquisitionPrice: Int = getColumnIndexOrThrow(_stmt, "acquisitionPrice")
        val _columnIndexOfAcquisitionDate: Int = getColumnIndexOrThrow(_stmt, "acquisitionDate")
        val _columnIndexOfAcquisitionSource: Int = getColumnIndexOrThrow(_stmt, "acquisitionSource")
        val _columnIndexOfAcquisitionSourceId: Int = getColumnIndexOrThrow(_stmt,
            "acquisitionSourceId")
        val _columnIndexOfAcquisitionNotes: Int = getColumnIndexOrThrow(_stmt, "acquisitionNotes")
        val _columnIndexOfEstimatedValue: Int = getColumnIndexOrThrow(_stmt, "estimatedValue")
        val _columnIndexOfLastVaccinationDate: Int = getColumnIndexOrThrow(_stmt,
            "lastVaccinationDate")
        val _columnIndexOfNextVaccinationDate: Int = getColumnIndexOrThrow(_stmt,
            "nextVaccinationDate")
        val _columnIndexOfWeightGrams: Int = getColumnIndexOrThrow(_stmt, "weightGrams")
        val _columnIndexOfMetadataJson: Int = getColumnIndexOrThrow(_stmt, "metadataJson")
        val _columnIndexOfListedAt: Int = getColumnIndexOrThrow(_stmt, "listedAt")
        val _columnIndexOfListingId: Int = getColumnIndexOrThrow(_stmt, "listingId")
        val _columnIndexOfSoldAt: Int = getColumnIndexOrThrow(_stmt, "soldAt")
        val _columnIndexOfSoldToUserId: Int = getColumnIndexOrThrow(_stmt, "soldToUserId")
        val _columnIndexOfSoldPrice: Int = getColumnIndexOrThrow(_stmt, "soldPrice")
        val _columnIndexOfPreviousOwnerId: Int = getColumnIndexOrThrow(_stmt, "previousOwnerId")
        val _columnIndexOfTransferredAt: Int = getColumnIndexOrThrow(_stmt, "transferredAt")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfIsDeleted: Int = getColumnIndexOrThrow(_stmt, "isDeleted")
        val _columnIndexOfDeletedAt: Int = getColumnIndexOrThrow(_stmt, "deletedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _result: MutableList<FarmAssetEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: FarmAssetEntity
          val _tmpAssetId: String
          _tmpAssetId = _stmt.getText(_columnIndexOfAssetId)
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpAssetType: String
          _tmpAssetType = _stmt.getText(_columnIndexOfAssetType)
          val _tmpCategory: String
          _tmpCategory = _stmt.getText(_columnIndexOfCategory)
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpIsShowcase: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsShowcase).toInt()
          _tmpIsShowcase = _tmp != 0
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
          val _tmpQuantity: Double
          _tmpQuantity = _stmt.getDouble(_columnIndexOfQuantity)
          val _tmpInitialQuantity: Double
          _tmpInitialQuantity = _stmt.getDouble(_columnIndexOfInitialQuantity)
          val _tmpUnit: String
          _tmpUnit = _stmt.getText(_columnIndexOfUnit)
          val _tmpBirthDate: Long?
          if (_stmt.isNull(_columnIndexOfBirthDate)) {
            _tmpBirthDate = null
          } else {
            _tmpBirthDate = _stmt.getLong(_columnIndexOfBirthDate)
          }
          val _tmpAgeWeeks: Int?
          if (_stmt.isNull(_columnIndexOfAgeWeeks)) {
            _tmpAgeWeeks = null
          } else {
            _tmpAgeWeeks = _stmt.getLong(_columnIndexOfAgeWeeks).toInt()
          }
          val _tmpBreed: String?
          if (_stmt.isNull(_columnIndexOfBreed)) {
            _tmpBreed = null
          } else {
            _tmpBreed = _stmt.getText(_columnIndexOfBreed)
          }
          val _tmpGender: String?
          if (_stmt.isNull(_columnIndexOfGender)) {
            _tmpGender = null
          } else {
            _tmpGender = _stmt.getText(_columnIndexOfGender)
          }
          val _tmpColor: String?
          if (_stmt.isNull(_columnIndexOfColor)) {
            _tmpColor = null
          } else {
            _tmpColor = _stmt.getText(_columnIndexOfColor)
          }
          val _tmpHealthStatus: String
          _tmpHealthStatus = _stmt.getText(_columnIndexOfHealthStatus)
          val _tmpRaisingPurpose: String?
          if (_stmt.isNull(_columnIndexOfRaisingPurpose)) {
            _tmpRaisingPurpose = null
          } else {
            _tmpRaisingPurpose = _stmt.getText(_columnIndexOfRaisingPurpose)
          }
          val _tmpDescription: String
          _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          val _tmpImageUrls: List<String>
          val _tmp_1: String?
          if (_stmt.isNull(_columnIndexOfImageUrls)) {
            _tmp_1 = null
          } else {
            _tmp_1 = _stmt.getText(_columnIndexOfImageUrls)
          }
          val _tmp_2: List<String>? = AppDatabase.Converters.toStringList(_tmp_1)
          if (_tmp_2 == null) {
            error("Expected NON-NULL 'kotlin.collections.List<kotlin.String>', but it was NULL.")
          } else {
            _tmpImageUrls = _tmp_2
          }
          val _tmpNotes: String?
          if (_stmt.isNull(_columnIndexOfNotes)) {
            _tmpNotes = null
          } else {
            _tmpNotes = _stmt.getText(_columnIndexOfNotes)
          }
          val _tmpLifecycleSubStage: String?
          if (_stmt.isNull(_columnIndexOfLifecycleSubStage)) {
            _tmpLifecycleSubStage = null
          } else {
            _tmpLifecycleSubStage = _stmt.getText(_columnIndexOfLifecycleSubStage)
          }
          val _tmpParentIdsJson: String?
          if (_stmt.isNull(_columnIndexOfParentIdsJson)) {
            _tmpParentIdsJson = null
          } else {
            _tmpParentIdsJson = _stmt.getText(_columnIndexOfParentIdsJson)
          }
          val _tmpBatchId: String?
          if (_stmt.isNull(_columnIndexOfBatchId)) {
            _tmpBatchId = null
          } else {
            _tmpBatchId = _stmt.getText(_columnIndexOfBatchId)
          }
          val _tmpOrigin: String?
          if (_stmt.isNull(_columnIndexOfOrigin)) {
            _tmpOrigin = null
          } else {
            _tmpOrigin = _stmt.getText(_columnIndexOfOrigin)
          }
          val _tmpBirdCode: String?
          if (_stmt.isNull(_columnIndexOfBirdCode)) {
            _tmpBirdCode = null
          } else {
            _tmpBirdCode = _stmt.getText(_columnIndexOfBirdCode)
          }
          val _tmpAcquisitionPrice: Double?
          if (_stmt.isNull(_columnIndexOfAcquisitionPrice)) {
            _tmpAcquisitionPrice = null
          } else {
            _tmpAcquisitionPrice = _stmt.getDouble(_columnIndexOfAcquisitionPrice)
          }
          val _tmpAcquisitionDate: Long?
          if (_stmt.isNull(_columnIndexOfAcquisitionDate)) {
            _tmpAcquisitionDate = null
          } else {
            _tmpAcquisitionDate = _stmt.getLong(_columnIndexOfAcquisitionDate)
          }
          val _tmpAcquisitionSource: String?
          if (_stmt.isNull(_columnIndexOfAcquisitionSource)) {
            _tmpAcquisitionSource = null
          } else {
            _tmpAcquisitionSource = _stmt.getText(_columnIndexOfAcquisitionSource)
          }
          val _tmpAcquisitionSourceId: String?
          if (_stmt.isNull(_columnIndexOfAcquisitionSourceId)) {
            _tmpAcquisitionSourceId = null
          } else {
            _tmpAcquisitionSourceId = _stmt.getText(_columnIndexOfAcquisitionSourceId)
          }
          val _tmpAcquisitionNotes: String?
          if (_stmt.isNull(_columnIndexOfAcquisitionNotes)) {
            _tmpAcquisitionNotes = null
          } else {
            _tmpAcquisitionNotes = _stmt.getText(_columnIndexOfAcquisitionNotes)
          }
          val _tmpEstimatedValue: Double?
          if (_stmt.isNull(_columnIndexOfEstimatedValue)) {
            _tmpEstimatedValue = null
          } else {
            _tmpEstimatedValue = _stmt.getDouble(_columnIndexOfEstimatedValue)
          }
          val _tmpLastVaccinationDate: Long?
          if (_stmt.isNull(_columnIndexOfLastVaccinationDate)) {
            _tmpLastVaccinationDate = null
          } else {
            _tmpLastVaccinationDate = _stmt.getLong(_columnIndexOfLastVaccinationDate)
          }
          val _tmpNextVaccinationDate: Long?
          if (_stmt.isNull(_columnIndexOfNextVaccinationDate)) {
            _tmpNextVaccinationDate = null
          } else {
            _tmpNextVaccinationDate = _stmt.getLong(_columnIndexOfNextVaccinationDate)
          }
          val _tmpWeightGrams: Double?
          if (_stmt.isNull(_columnIndexOfWeightGrams)) {
            _tmpWeightGrams = null
          } else {
            _tmpWeightGrams = _stmt.getDouble(_columnIndexOfWeightGrams)
          }
          val _tmpMetadataJson: String
          _tmpMetadataJson = _stmt.getText(_columnIndexOfMetadataJson)
          val _tmpListedAt: Long?
          if (_stmt.isNull(_columnIndexOfListedAt)) {
            _tmpListedAt = null
          } else {
            _tmpListedAt = _stmt.getLong(_columnIndexOfListedAt)
          }
          val _tmpListingId: String?
          if (_stmt.isNull(_columnIndexOfListingId)) {
            _tmpListingId = null
          } else {
            _tmpListingId = _stmt.getText(_columnIndexOfListingId)
          }
          val _tmpSoldAt: Long?
          if (_stmt.isNull(_columnIndexOfSoldAt)) {
            _tmpSoldAt = null
          } else {
            _tmpSoldAt = _stmt.getLong(_columnIndexOfSoldAt)
          }
          val _tmpSoldToUserId: String?
          if (_stmt.isNull(_columnIndexOfSoldToUserId)) {
            _tmpSoldToUserId = null
          } else {
            _tmpSoldToUserId = _stmt.getText(_columnIndexOfSoldToUserId)
          }
          val _tmpSoldPrice: Double?
          if (_stmt.isNull(_columnIndexOfSoldPrice)) {
            _tmpSoldPrice = null
          } else {
            _tmpSoldPrice = _stmt.getDouble(_columnIndexOfSoldPrice)
          }
          val _tmpPreviousOwnerId: String?
          if (_stmt.isNull(_columnIndexOfPreviousOwnerId)) {
            _tmpPreviousOwnerId = null
          } else {
            _tmpPreviousOwnerId = _stmt.getText(_columnIndexOfPreviousOwnerId)
          }
          val _tmpTransferredAt: Long?
          if (_stmt.isNull(_columnIndexOfTransferredAt)) {
            _tmpTransferredAt = null
          } else {
            _tmpTransferredAt = _stmt.getLong(_columnIndexOfTransferredAt)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpIsDeleted: Boolean
          val _tmp_3: Int
          _tmp_3 = _stmt.getLong(_columnIndexOfIsDeleted).toInt()
          _tmpIsDeleted = _tmp_3 != 0
          val _tmpDeletedAt: Long?
          if (_stmt.isNull(_columnIndexOfDeletedAt)) {
            _tmpDeletedAt = null
          } else {
            _tmpDeletedAt = _stmt.getLong(_columnIndexOfDeletedAt)
          }
          val _tmpDirty: Boolean
          val _tmp_4: Int
          _tmp_4 = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp_4 != 0
          _item =
              FarmAssetEntity(_tmpAssetId,_tmpFarmerId,_tmpName,_tmpAssetType,_tmpCategory,_tmpStatus,_tmpIsShowcase,_tmpLocationName,_tmpLatitude,_tmpLongitude,_tmpQuantity,_tmpInitialQuantity,_tmpUnit,_tmpBirthDate,_tmpAgeWeeks,_tmpBreed,_tmpGender,_tmpColor,_tmpHealthStatus,_tmpRaisingPurpose,_tmpDescription,_tmpImageUrls,_tmpNotes,_tmpLifecycleSubStage,_tmpParentIdsJson,_tmpBatchId,_tmpOrigin,_tmpBirdCode,_tmpAcquisitionPrice,_tmpAcquisitionDate,_tmpAcquisitionSource,_tmpAcquisitionSourceId,_tmpAcquisitionNotes,_tmpEstimatedValue,_tmpLastVaccinationDate,_tmpNextVaccinationDate,_tmpWeightGrams,_tmpMetadataJson,_tmpListedAt,_tmpListingId,_tmpSoldAt,_tmpSoldToUserId,_tmpSoldPrice,_tmpPreviousOwnerId,_tmpTransferredAt,_tmpCreatedAt,_tmpUpdatedAt,_tmpIsDeleted,_tmpDeletedAt,_tmpDirty)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun countReadyToLayBirds(farmerId: String): Flow<Int> {
    val _sql: String =
        "SELECT COUNT(*) FROM farm_assets WHERE farmerId = ? AND gender = 'FEMALE' AND ageWeeks >= 18 AND ageWeeks <= 22 AND isDeleted = 0"
    return createFlow(__db, false, arrayOf("farm_assets")) { _connection ->
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

  public override fun countCullCandidates(farmerId: String): Flow<Int> {
    val _sql: String =
        "SELECT COUNT(*) FROM farm_assets WHERE farmerId = ? AND (ageWeeks > 72 OR healthStatus = 'SICK') AND isDeleted = 0"
    return createFlow(__db, false, arrayOf("farm_assets")) { _connection ->
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

  public override fun countVaccinationDueSoon(
    farmerId: String,
    now: Long,
    future: Long,
  ): Flow<Int> {
    val _sql: String =
        "SELECT COUNT(*) FROM farm_assets WHERE farmerId = ? AND nextVaccinationDate IS NOT NULL AND nextVaccinationDate BETWEEN ? AND ? AND isDeleted = 0"
    return createFlow(__db, false, arrayOf("farm_assets")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, farmerId)
        _argIndex = 2
        _stmt.bindLong(_argIndex, now)
        _argIndex = 3
        _stmt.bindLong(_argIndex, future)
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

  public override suspend fun countActiveByFarmer(farmerId: String): Int {
    val _sql: String =
        "SELECT COUNT(*) FROM farm_assets WHERE farmerId = ? AND status = 'ACTIVE' AND isDeleted = 0"
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

  public override suspend fun updateQuantity(
    assetId: String,
    quantity: Double,
    updatedAt: Long,
  ) {
    val _sql: String =
        "UPDATE farm_assets SET quantity = ?, updatedAt = ?, dirty = 1 WHERE assetId = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindDouble(_argIndex, quantity)
        _argIndex = 2
        _stmt.bindLong(_argIndex, updatedAt)
        _argIndex = 3
        _stmt.bindText(_argIndex, assetId)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun updateStatus(
    assetId: String,
    status: String,
    timestamp: Long,
  ) {
    val _sql: String = "UPDATE farm_assets SET status = ?, updatedAt = ? WHERE assetId = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, status)
        _argIndex = 2
        _stmt.bindLong(_argIndex, timestamp)
        _argIndex = 3
        _stmt.bindText(_argIndex, assetId)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun updateHealthStatus(
    assetId: String,
    status: String,
    updatedAt: Long,
  ) {
    val _sql: String =
        "UPDATE farm_assets SET healthStatus = ?, updatedAt = ?, dirty = 1 WHERE assetId = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, status)
        _argIndex = 2
        _stmt.bindLong(_argIndex, updatedAt)
        _argIndex = 3
        _stmt.bindText(_argIndex, assetId)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun purgeDeleted() {
    val _sql: String = "DELETE FROM farm_assets WHERE isDeleted = 1"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun markAsListed(
    assetId: String,
    listingId: String,
    listedAt: Long,
    updatedAt: Long,
  ) {
    val _sql: String =
        "UPDATE farm_assets SET status = 'LISTED', listedAt = ?, listingId = ?, updatedAt = ?, dirty = 1 WHERE assetId = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, listedAt)
        _argIndex = 2
        _stmt.bindText(_argIndex, listingId)
        _argIndex = 3
        _stmt.bindLong(_argIndex, updatedAt)
        _argIndex = 4
        _stmt.bindText(_argIndex, assetId)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun markAsDeListed(assetId: String, updatedAt: Long) {
    val _sql: String =
        "UPDATE farm_assets SET status = 'ACTIVE', listedAt = NULL, listingId = NULL, updatedAt = ?, dirty = 1 WHERE assetId = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, updatedAt)
        _argIndex = 2
        _stmt.bindText(_argIndex, assetId)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun markAsSold(
    assetId: String,
    buyerId: String,
    price: Double,
    soldAt: Long,
    updatedAt: Long,
  ) {
    val _sql: String =
        "UPDATE farm_assets SET status = 'SOLD', soldAt = ?, soldToUserId = ?, soldPrice = ?, listedAt = NULL, listingId = NULL, updatedAt = ?, dirty = 1 WHERE assetId = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, soldAt)
        _argIndex = 2
        _stmt.bindText(_argIndex, buyerId)
        _argIndex = 3
        _stmt.bindDouble(_argIndex, price)
        _argIndex = 4
        _stmt.bindLong(_argIndex, updatedAt)
        _argIndex = 5
        _stmt.bindText(_argIndex, assetId)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun clearDirty(assetId: String) {
    val _sql: String = "UPDATE farm_assets SET dirty = 0 WHERE assetId = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, assetId)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun updateAgeWeeks(
    assetId: String,
    ageWeeks: Int,
    updatedAt: Long,
  ) {
    val _sql: String = "UPDATE farm_assets SET ageWeeks = ?, updatedAt = ? WHERE assetId = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, ageWeeks.toLong())
        _argIndex = 2
        _stmt.bindLong(_argIndex, updatedAt)
        _argIndex = 3
        _stmt.bindText(_argIndex, assetId)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun updateLifecycleSubStage(
    assetId: String,
    subStage: String,
    updatedAt: Long,
  ) {
    val _sql: String =
        "UPDATE farm_assets SET lifecycleSubStage = ?, updatedAt = ? WHERE assetId = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, subStage)
        _argIndex = 2
        _stmt.bindLong(_argIndex, updatedAt)
        _argIndex = 3
        _stmt.bindText(_argIndex, assetId)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun updateMetadataJson(
    assetId: String,
    metadataJson: String,
    updatedAt: Long,
  ) {
    val _sql: String =
        "UPDATE farm_assets SET metadataJson = ?, updatedAt = ?, dirty = 1 WHERE assetId = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, metadataJson)
        _argIndex = 2
        _stmt.bindLong(_argIndex, updatedAt)
        _argIndex = 3
        _stmt.bindText(_argIndex, assetId)
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
