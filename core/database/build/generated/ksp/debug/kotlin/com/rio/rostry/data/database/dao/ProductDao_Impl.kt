package com.rio.rostry.`data`.database.dao

import androidx.room.EntityDeleteOrUpdateAdapter
import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.appendPlaceholders
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performInTransactionSuspending
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.rio.rostry.`data`.database.AppDatabase
import com.rio.rostry.`data`.database.entity.ProductEntity
import com.rio.rostry.`data`.database.entity.ProductFtsEntity
import com.rio.rostry.domain.model.LifecycleStage
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
public class ProductDao_Impl(
  __db: RoomDatabase,
) : ProductDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfProductFtsEntity: EntityInsertAdapter<ProductFtsEntity>

  private val __insertAdapterOfProductEntity: EntityInsertAdapter<ProductEntity>

  private val __updateAdapterOfProductEntity: EntityDeleteOrUpdateAdapter<ProductEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfProductFtsEntity = object : EntityInsertAdapter<ProductFtsEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `products_fts` (`productId`,`name`,`description`,`category`,`breed`,`location`,`condition`) VALUES (?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: ProductFtsEntity) {
        statement.bindText(1, entity.productId)
        statement.bindText(2, entity.name)
        statement.bindText(3, entity.description)
        statement.bindText(4, entity.category)
        val _tmpBreed: String? = entity.breed
        if (_tmpBreed == null) {
          statement.bindNull(5)
        } else {
          statement.bindText(5, _tmpBreed)
        }
        statement.bindText(6, entity.location)
        val _tmpCondition: String? = entity.condition
        if (_tmpCondition == null) {
          statement.bindNull(7)
        } else {
          statement.bindText(7, _tmpCondition)
        }
      }
    }
    this.__insertAdapterOfProductEntity = object : EntityInsertAdapter<ProductEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `products` (`productId`,`sellerId`,`name`,`description`,`category`,`price`,`quantity`,`unit`,`location`,`latitude`,`longitude`,`imageUrls`,`status`,`condition`,`harvestDate`,`expiryDate`,`birthDate`,`vaccinationRecordsJson`,`weightGrams`,`heightCm`,`gender`,`color`,`breed`,`raisingPurpose`,`healthStatus`,`birdCode`,`colorTag`,`familyTreeId`,`sourceAssetId`,`parentIdsJson`,`breedingStatus`,`transferHistoryJson`,`createdAt`,`updatedAt`,`lastModifiedAt`,`isDeleted`,`deletedAt`,`dirty`,`stage`,`lifecycleStatus`,`parentMaleId`,`parentFemaleId`,`ageWeeks`,`lastStageTransitionAt`,`breederEligibleAt`,`isBatch`,`batchId`,`splitAt`,`splitIntoIds`,`documentUrls`,`qrCodeUrl`,`customStatus`,`debug`,`deliveryOptions`,`deliveryCost`,`leadTimeDays`,`motherId`,`isBreedingUnit`,`eggsCollectedToday`,`lastEggLogDate`,`readyForSale`,`targetWeight`,`isShowcased`,`externalVideoUrl`,`recordsLockedAt`,`autoLockAfterDays`,`lineageHistoryJson`,`editCount`,`lastEditedBy`,`adminFlagged`,`moderationNote`,`metadataJson`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: ProductEntity) {
        statement.bindText(1, entity.productId)
        statement.bindText(2, entity.sellerId)
        statement.bindText(3, entity.name)
        statement.bindText(4, entity.description)
        statement.bindText(5, entity.category)
        statement.bindDouble(6, entity.price)
        statement.bindDouble(7, entity.quantity)
        statement.bindText(8, entity.unit)
        statement.bindText(9, entity.location)
        val _tmpLatitude: Double? = entity.latitude
        if (_tmpLatitude == null) {
          statement.bindNull(10)
        } else {
          statement.bindDouble(10, _tmpLatitude)
        }
        val _tmpLongitude: Double? = entity.longitude
        if (_tmpLongitude == null) {
          statement.bindNull(11)
        } else {
          statement.bindDouble(11, _tmpLongitude)
        }
        val _tmp: String? = AppDatabase.Converters.fromStringList(entity.imageUrls)
        if (_tmp == null) {
          statement.bindNull(12)
        } else {
          statement.bindText(12, _tmp)
        }
        statement.bindText(13, entity.status)
        val _tmpCondition: String? = entity.condition
        if (_tmpCondition == null) {
          statement.bindNull(14)
        } else {
          statement.bindText(14, _tmpCondition)
        }
        val _tmpHarvestDate: Long? = entity.harvestDate
        if (_tmpHarvestDate == null) {
          statement.bindNull(15)
        } else {
          statement.bindLong(15, _tmpHarvestDate)
        }
        val _tmpExpiryDate: Long? = entity.expiryDate
        if (_tmpExpiryDate == null) {
          statement.bindNull(16)
        } else {
          statement.bindLong(16, _tmpExpiryDate)
        }
        val _tmpBirthDate: Long? = entity.birthDate
        if (_tmpBirthDate == null) {
          statement.bindNull(17)
        } else {
          statement.bindLong(17, _tmpBirthDate)
        }
        val _tmpVaccinationRecordsJson: String? = entity.vaccinationRecordsJson
        if (_tmpVaccinationRecordsJson == null) {
          statement.bindNull(18)
        } else {
          statement.bindText(18, _tmpVaccinationRecordsJson)
        }
        val _tmpWeightGrams: Double? = entity.weightGrams
        if (_tmpWeightGrams == null) {
          statement.bindNull(19)
        } else {
          statement.bindDouble(19, _tmpWeightGrams)
        }
        val _tmpHeightCm: Double? = entity.heightCm
        if (_tmpHeightCm == null) {
          statement.bindNull(20)
        } else {
          statement.bindDouble(20, _tmpHeightCm)
        }
        val _tmpGender: String? = entity.gender
        if (_tmpGender == null) {
          statement.bindNull(21)
        } else {
          statement.bindText(21, _tmpGender)
        }
        val _tmpColor: String? = entity.color
        if (_tmpColor == null) {
          statement.bindNull(22)
        } else {
          statement.bindText(22, _tmpColor)
        }
        val _tmpBreed: String? = entity.breed
        if (_tmpBreed == null) {
          statement.bindNull(23)
        } else {
          statement.bindText(23, _tmpBreed)
        }
        val _tmpRaisingPurpose: String? = entity.raisingPurpose
        if (_tmpRaisingPurpose == null) {
          statement.bindNull(24)
        } else {
          statement.bindText(24, _tmpRaisingPurpose)
        }
        val _tmpHealthStatus: String? = entity.healthStatus
        if (_tmpHealthStatus == null) {
          statement.bindNull(25)
        } else {
          statement.bindText(25, _tmpHealthStatus)
        }
        val _tmpBirdCode: String? = entity.birdCode
        if (_tmpBirdCode == null) {
          statement.bindNull(26)
        } else {
          statement.bindText(26, _tmpBirdCode)
        }
        val _tmpColorTag: String? = entity.colorTag
        if (_tmpColorTag == null) {
          statement.bindNull(27)
        } else {
          statement.bindText(27, _tmpColorTag)
        }
        val _tmpFamilyTreeId: String? = entity.familyTreeId
        if (_tmpFamilyTreeId == null) {
          statement.bindNull(28)
        } else {
          statement.bindText(28, _tmpFamilyTreeId)
        }
        val _tmpSourceAssetId: String? = entity.sourceAssetId
        if (_tmpSourceAssetId == null) {
          statement.bindNull(29)
        } else {
          statement.bindText(29, _tmpSourceAssetId)
        }
        val _tmpParentIdsJson: String? = entity.parentIdsJson
        if (_tmpParentIdsJson == null) {
          statement.bindNull(30)
        } else {
          statement.bindText(30, _tmpParentIdsJson)
        }
        val _tmpBreedingStatus: String? = entity.breedingStatus
        if (_tmpBreedingStatus == null) {
          statement.bindNull(31)
        } else {
          statement.bindText(31, _tmpBreedingStatus)
        }
        val _tmpTransferHistoryJson: String? = entity.transferHistoryJson
        if (_tmpTransferHistoryJson == null) {
          statement.bindNull(32)
        } else {
          statement.bindText(32, _tmpTransferHistoryJson)
        }
        statement.bindLong(33, entity.createdAt)
        statement.bindLong(34, entity.updatedAt)
        statement.bindLong(35, entity.lastModifiedAt)
        val _tmp_1: Int = if (entity.isDeleted) 1 else 0
        statement.bindLong(36, _tmp_1.toLong())
        val _tmpDeletedAt: Long? = entity.deletedAt
        if (_tmpDeletedAt == null) {
          statement.bindNull(37)
        } else {
          statement.bindLong(37, _tmpDeletedAt)
        }
        val _tmp_2: Int = if (entity.dirty) 1 else 0
        statement.bindLong(38, _tmp_2.toLong())
        val _tmpStage: LifecycleStage? = entity.stage
        val _tmp_3: String? = AppDatabase.Converters.fromLifecycleStage(_tmpStage)
        if (_tmp_3 == null) {
          statement.bindNull(39)
        } else {
          statement.bindText(39, _tmp_3)
        }
        val _tmpLifecycleStatus: String? = entity.lifecycleStatus
        if (_tmpLifecycleStatus == null) {
          statement.bindNull(40)
        } else {
          statement.bindText(40, _tmpLifecycleStatus)
        }
        val _tmpParentMaleId: String? = entity.parentMaleId
        if (_tmpParentMaleId == null) {
          statement.bindNull(41)
        } else {
          statement.bindText(41, _tmpParentMaleId)
        }
        val _tmpParentFemaleId: String? = entity.parentFemaleId
        if (_tmpParentFemaleId == null) {
          statement.bindNull(42)
        } else {
          statement.bindText(42, _tmpParentFemaleId)
        }
        val _tmpAgeWeeks: Int? = entity.ageWeeks
        if (_tmpAgeWeeks == null) {
          statement.bindNull(43)
        } else {
          statement.bindLong(43, _tmpAgeWeeks.toLong())
        }
        val _tmpLastStageTransitionAt: Long? = entity.lastStageTransitionAt
        if (_tmpLastStageTransitionAt == null) {
          statement.bindNull(44)
        } else {
          statement.bindLong(44, _tmpLastStageTransitionAt)
        }
        val _tmpBreederEligibleAt: Long? = entity.breederEligibleAt
        if (_tmpBreederEligibleAt == null) {
          statement.bindNull(45)
        } else {
          statement.bindLong(45, _tmpBreederEligibleAt)
        }
        val _tmpIsBatch: Boolean? = entity.isBatch
        val _tmp_4: Int? = _tmpIsBatch?.let { if (it) 1 else 0 }
        if (_tmp_4 == null) {
          statement.bindNull(46)
        } else {
          statement.bindLong(46, _tmp_4.toLong())
        }
        val _tmpBatchId: String? = entity.batchId
        if (_tmpBatchId == null) {
          statement.bindNull(47)
        } else {
          statement.bindText(47, _tmpBatchId)
        }
        val _tmpSplitAt: Long? = entity.splitAt
        if (_tmpSplitAt == null) {
          statement.bindNull(48)
        } else {
          statement.bindLong(48, _tmpSplitAt)
        }
        val _tmpSplitIntoIds: String? = entity.splitIntoIds
        if (_tmpSplitIntoIds == null) {
          statement.bindNull(49)
        } else {
          statement.bindText(49, _tmpSplitIntoIds)
        }
        val _tmp_5: String? = AppDatabase.Converters.fromStringList(entity.documentUrls)
        if (_tmp_5 == null) {
          statement.bindNull(50)
        } else {
          statement.bindText(50, _tmp_5)
        }
        val _tmpQrCodeUrl: String? = entity.qrCodeUrl
        if (_tmpQrCodeUrl == null) {
          statement.bindNull(51)
        } else {
          statement.bindText(51, _tmpQrCodeUrl)
        }
        val _tmpCustomStatus: String? = entity.customStatus
        if (_tmpCustomStatus == null) {
          statement.bindNull(52)
        } else {
          statement.bindText(52, _tmpCustomStatus)
        }
        val _tmp_6: Int = if (entity.debug) 1 else 0
        statement.bindLong(53, _tmp_6.toLong())
        val _tmp_7: String? = AppDatabase.Converters.fromStringList(entity.deliveryOptions)
        if (_tmp_7 == null) {
          statement.bindNull(54)
        } else {
          statement.bindText(54, _tmp_7)
        }
        val _tmpDeliveryCost: Double? = entity.deliveryCost
        if (_tmpDeliveryCost == null) {
          statement.bindNull(55)
        } else {
          statement.bindDouble(55, _tmpDeliveryCost)
        }
        val _tmpLeadTimeDays: Int? = entity.leadTimeDays
        if (_tmpLeadTimeDays == null) {
          statement.bindNull(56)
        } else {
          statement.bindLong(56, _tmpLeadTimeDays.toLong())
        }
        val _tmpMotherId: String? = entity.motherId
        if (_tmpMotherId == null) {
          statement.bindNull(57)
        } else {
          statement.bindText(57, _tmpMotherId)
        }
        val _tmp_8: Int = if (entity.isBreedingUnit) 1 else 0
        statement.bindLong(58, _tmp_8.toLong())
        statement.bindLong(59, entity.eggsCollectedToday.toLong())
        val _tmpLastEggLogDate: Long? = entity.lastEggLogDate
        if (_tmpLastEggLogDate == null) {
          statement.bindNull(60)
        } else {
          statement.bindLong(60, _tmpLastEggLogDate)
        }
        val _tmp_9: Int = if (entity.readyForSale) 1 else 0
        statement.bindLong(61, _tmp_9.toLong())
        val _tmpTargetWeight: Double? = entity.targetWeight
        if (_tmpTargetWeight == null) {
          statement.bindNull(62)
        } else {
          statement.bindDouble(62, _tmpTargetWeight)
        }
        val _tmp_10: Int = if (entity.isShowcased) 1 else 0
        statement.bindLong(63, _tmp_10.toLong())
        val _tmpExternalVideoUrl: String? = entity.externalVideoUrl
        if (_tmpExternalVideoUrl == null) {
          statement.bindNull(64)
        } else {
          statement.bindText(64, _tmpExternalVideoUrl)
        }
        val _tmpRecordsLockedAt: Long? = entity.recordsLockedAt
        if (_tmpRecordsLockedAt == null) {
          statement.bindNull(65)
        } else {
          statement.bindLong(65, _tmpRecordsLockedAt)
        }
        statement.bindLong(66, entity.autoLockAfterDays.toLong())
        val _tmpLineageHistoryJson: String? = entity.lineageHistoryJson
        if (_tmpLineageHistoryJson == null) {
          statement.bindNull(67)
        } else {
          statement.bindText(67, _tmpLineageHistoryJson)
        }
        statement.bindLong(68, entity.editCount.toLong())
        val _tmpLastEditedBy: String? = entity.lastEditedBy
        if (_tmpLastEditedBy == null) {
          statement.bindNull(69)
        } else {
          statement.bindText(69, _tmpLastEditedBy)
        }
        val _tmp_11: Int = if (entity.adminFlagged) 1 else 0
        statement.bindLong(70, _tmp_11.toLong())
        val _tmpModerationNote: String? = entity.moderationNote
        if (_tmpModerationNote == null) {
          statement.bindNull(71)
        } else {
          statement.bindText(71, _tmpModerationNote)
        }
        statement.bindText(72, entity.metadataJson)
      }
    }
    this.__updateAdapterOfProductEntity = object : EntityDeleteOrUpdateAdapter<ProductEntity>() {
      protected override fun createQuery(): String =
          "UPDATE OR ABORT `products` SET `productId` = ?,`sellerId` = ?,`name` = ?,`description` = ?,`category` = ?,`price` = ?,`quantity` = ?,`unit` = ?,`location` = ?,`latitude` = ?,`longitude` = ?,`imageUrls` = ?,`status` = ?,`condition` = ?,`harvestDate` = ?,`expiryDate` = ?,`birthDate` = ?,`vaccinationRecordsJson` = ?,`weightGrams` = ?,`heightCm` = ?,`gender` = ?,`color` = ?,`breed` = ?,`raisingPurpose` = ?,`healthStatus` = ?,`birdCode` = ?,`colorTag` = ?,`familyTreeId` = ?,`sourceAssetId` = ?,`parentIdsJson` = ?,`breedingStatus` = ?,`transferHistoryJson` = ?,`createdAt` = ?,`updatedAt` = ?,`lastModifiedAt` = ?,`isDeleted` = ?,`deletedAt` = ?,`dirty` = ?,`stage` = ?,`lifecycleStatus` = ?,`parentMaleId` = ?,`parentFemaleId` = ?,`ageWeeks` = ?,`lastStageTransitionAt` = ?,`breederEligibleAt` = ?,`isBatch` = ?,`batchId` = ?,`splitAt` = ?,`splitIntoIds` = ?,`documentUrls` = ?,`qrCodeUrl` = ?,`customStatus` = ?,`debug` = ?,`deliveryOptions` = ?,`deliveryCost` = ?,`leadTimeDays` = ?,`motherId` = ?,`isBreedingUnit` = ?,`eggsCollectedToday` = ?,`lastEggLogDate` = ?,`readyForSale` = ?,`targetWeight` = ?,`isShowcased` = ?,`externalVideoUrl` = ?,`recordsLockedAt` = ?,`autoLockAfterDays` = ?,`lineageHistoryJson` = ?,`editCount` = ?,`lastEditedBy` = ?,`adminFlagged` = ?,`moderationNote` = ?,`metadataJson` = ? WHERE `productId` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: ProductEntity) {
        statement.bindText(1, entity.productId)
        statement.bindText(2, entity.sellerId)
        statement.bindText(3, entity.name)
        statement.bindText(4, entity.description)
        statement.bindText(5, entity.category)
        statement.bindDouble(6, entity.price)
        statement.bindDouble(7, entity.quantity)
        statement.bindText(8, entity.unit)
        statement.bindText(9, entity.location)
        val _tmpLatitude: Double? = entity.latitude
        if (_tmpLatitude == null) {
          statement.bindNull(10)
        } else {
          statement.bindDouble(10, _tmpLatitude)
        }
        val _tmpLongitude: Double? = entity.longitude
        if (_tmpLongitude == null) {
          statement.bindNull(11)
        } else {
          statement.bindDouble(11, _tmpLongitude)
        }
        val _tmp: String? = AppDatabase.Converters.fromStringList(entity.imageUrls)
        if (_tmp == null) {
          statement.bindNull(12)
        } else {
          statement.bindText(12, _tmp)
        }
        statement.bindText(13, entity.status)
        val _tmpCondition: String? = entity.condition
        if (_tmpCondition == null) {
          statement.bindNull(14)
        } else {
          statement.bindText(14, _tmpCondition)
        }
        val _tmpHarvestDate: Long? = entity.harvestDate
        if (_tmpHarvestDate == null) {
          statement.bindNull(15)
        } else {
          statement.bindLong(15, _tmpHarvestDate)
        }
        val _tmpExpiryDate: Long? = entity.expiryDate
        if (_tmpExpiryDate == null) {
          statement.bindNull(16)
        } else {
          statement.bindLong(16, _tmpExpiryDate)
        }
        val _tmpBirthDate: Long? = entity.birthDate
        if (_tmpBirthDate == null) {
          statement.bindNull(17)
        } else {
          statement.bindLong(17, _tmpBirthDate)
        }
        val _tmpVaccinationRecordsJson: String? = entity.vaccinationRecordsJson
        if (_tmpVaccinationRecordsJson == null) {
          statement.bindNull(18)
        } else {
          statement.bindText(18, _tmpVaccinationRecordsJson)
        }
        val _tmpWeightGrams: Double? = entity.weightGrams
        if (_tmpWeightGrams == null) {
          statement.bindNull(19)
        } else {
          statement.bindDouble(19, _tmpWeightGrams)
        }
        val _tmpHeightCm: Double? = entity.heightCm
        if (_tmpHeightCm == null) {
          statement.bindNull(20)
        } else {
          statement.bindDouble(20, _tmpHeightCm)
        }
        val _tmpGender: String? = entity.gender
        if (_tmpGender == null) {
          statement.bindNull(21)
        } else {
          statement.bindText(21, _tmpGender)
        }
        val _tmpColor: String? = entity.color
        if (_tmpColor == null) {
          statement.bindNull(22)
        } else {
          statement.bindText(22, _tmpColor)
        }
        val _tmpBreed: String? = entity.breed
        if (_tmpBreed == null) {
          statement.bindNull(23)
        } else {
          statement.bindText(23, _tmpBreed)
        }
        val _tmpRaisingPurpose: String? = entity.raisingPurpose
        if (_tmpRaisingPurpose == null) {
          statement.bindNull(24)
        } else {
          statement.bindText(24, _tmpRaisingPurpose)
        }
        val _tmpHealthStatus: String? = entity.healthStatus
        if (_tmpHealthStatus == null) {
          statement.bindNull(25)
        } else {
          statement.bindText(25, _tmpHealthStatus)
        }
        val _tmpBirdCode: String? = entity.birdCode
        if (_tmpBirdCode == null) {
          statement.bindNull(26)
        } else {
          statement.bindText(26, _tmpBirdCode)
        }
        val _tmpColorTag: String? = entity.colorTag
        if (_tmpColorTag == null) {
          statement.bindNull(27)
        } else {
          statement.bindText(27, _tmpColorTag)
        }
        val _tmpFamilyTreeId: String? = entity.familyTreeId
        if (_tmpFamilyTreeId == null) {
          statement.bindNull(28)
        } else {
          statement.bindText(28, _tmpFamilyTreeId)
        }
        val _tmpSourceAssetId: String? = entity.sourceAssetId
        if (_tmpSourceAssetId == null) {
          statement.bindNull(29)
        } else {
          statement.bindText(29, _tmpSourceAssetId)
        }
        val _tmpParentIdsJson: String? = entity.parentIdsJson
        if (_tmpParentIdsJson == null) {
          statement.bindNull(30)
        } else {
          statement.bindText(30, _tmpParentIdsJson)
        }
        val _tmpBreedingStatus: String? = entity.breedingStatus
        if (_tmpBreedingStatus == null) {
          statement.bindNull(31)
        } else {
          statement.bindText(31, _tmpBreedingStatus)
        }
        val _tmpTransferHistoryJson: String? = entity.transferHistoryJson
        if (_tmpTransferHistoryJson == null) {
          statement.bindNull(32)
        } else {
          statement.bindText(32, _tmpTransferHistoryJson)
        }
        statement.bindLong(33, entity.createdAt)
        statement.bindLong(34, entity.updatedAt)
        statement.bindLong(35, entity.lastModifiedAt)
        val _tmp_1: Int = if (entity.isDeleted) 1 else 0
        statement.bindLong(36, _tmp_1.toLong())
        val _tmpDeletedAt: Long? = entity.deletedAt
        if (_tmpDeletedAt == null) {
          statement.bindNull(37)
        } else {
          statement.bindLong(37, _tmpDeletedAt)
        }
        val _tmp_2: Int = if (entity.dirty) 1 else 0
        statement.bindLong(38, _tmp_2.toLong())
        val _tmpStage: LifecycleStage? = entity.stage
        val _tmp_3: String? = AppDatabase.Converters.fromLifecycleStage(_tmpStage)
        if (_tmp_3 == null) {
          statement.bindNull(39)
        } else {
          statement.bindText(39, _tmp_3)
        }
        val _tmpLifecycleStatus: String? = entity.lifecycleStatus
        if (_tmpLifecycleStatus == null) {
          statement.bindNull(40)
        } else {
          statement.bindText(40, _tmpLifecycleStatus)
        }
        val _tmpParentMaleId: String? = entity.parentMaleId
        if (_tmpParentMaleId == null) {
          statement.bindNull(41)
        } else {
          statement.bindText(41, _tmpParentMaleId)
        }
        val _tmpParentFemaleId: String? = entity.parentFemaleId
        if (_tmpParentFemaleId == null) {
          statement.bindNull(42)
        } else {
          statement.bindText(42, _tmpParentFemaleId)
        }
        val _tmpAgeWeeks: Int? = entity.ageWeeks
        if (_tmpAgeWeeks == null) {
          statement.bindNull(43)
        } else {
          statement.bindLong(43, _tmpAgeWeeks.toLong())
        }
        val _tmpLastStageTransitionAt: Long? = entity.lastStageTransitionAt
        if (_tmpLastStageTransitionAt == null) {
          statement.bindNull(44)
        } else {
          statement.bindLong(44, _tmpLastStageTransitionAt)
        }
        val _tmpBreederEligibleAt: Long? = entity.breederEligibleAt
        if (_tmpBreederEligibleAt == null) {
          statement.bindNull(45)
        } else {
          statement.bindLong(45, _tmpBreederEligibleAt)
        }
        val _tmpIsBatch: Boolean? = entity.isBatch
        val _tmp_4: Int? = _tmpIsBatch?.let { if (it) 1 else 0 }
        if (_tmp_4 == null) {
          statement.bindNull(46)
        } else {
          statement.bindLong(46, _tmp_4.toLong())
        }
        val _tmpBatchId: String? = entity.batchId
        if (_tmpBatchId == null) {
          statement.bindNull(47)
        } else {
          statement.bindText(47, _tmpBatchId)
        }
        val _tmpSplitAt: Long? = entity.splitAt
        if (_tmpSplitAt == null) {
          statement.bindNull(48)
        } else {
          statement.bindLong(48, _tmpSplitAt)
        }
        val _tmpSplitIntoIds: String? = entity.splitIntoIds
        if (_tmpSplitIntoIds == null) {
          statement.bindNull(49)
        } else {
          statement.bindText(49, _tmpSplitIntoIds)
        }
        val _tmp_5: String? = AppDatabase.Converters.fromStringList(entity.documentUrls)
        if (_tmp_5 == null) {
          statement.bindNull(50)
        } else {
          statement.bindText(50, _tmp_5)
        }
        val _tmpQrCodeUrl: String? = entity.qrCodeUrl
        if (_tmpQrCodeUrl == null) {
          statement.bindNull(51)
        } else {
          statement.bindText(51, _tmpQrCodeUrl)
        }
        val _tmpCustomStatus: String? = entity.customStatus
        if (_tmpCustomStatus == null) {
          statement.bindNull(52)
        } else {
          statement.bindText(52, _tmpCustomStatus)
        }
        val _tmp_6: Int = if (entity.debug) 1 else 0
        statement.bindLong(53, _tmp_6.toLong())
        val _tmp_7: String? = AppDatabase.Converters.fromStringList(entity.deliveryOptions)
        if (_tmp_7 == null) {
          statement.bindNull(54)
        } else {
          statement.bindText(54, _tmp_7)
        }
        val _tmpDeliveryCost: Double? = entity.deliveryCost
        if (_tmpDeliveryCost == null) {
          statement.bindNull(55)
        } else {
          statement.bindDouble(55, _tmpDeliveryCost)
        }
        val _tmpLeadTimeDays: Int? = entity.leadTimeDays
        if (_tmpLeadTimeDays == null) {
          statement.bindNull(56)
        } else {
          statement.bindLong(56, _tmpLeadTimeDays.toLong())
        }
        val _tmpMotherId: String? = entity.motherId
        if (_tmpMotherId == null) {
          statement.bindNull(57)
        } else {
          statement.bindText(57, _tmpMotherId)
        }
        val _tmp_8: Int = if (entity.isBreedingUnit) 1 else 0
        statement.bindLong(58, _tmp_8.toLong())
        statement.bindLong(59, entity.eggsCollectedToday.toLong())
        val _tmpLastEggLogDate: Long? = entity.lastEggLogDate
        if (_tmpLastEggLogDate == null) {
          statement.bindNull(60)
        } else {
          statement.bindLong(60, _tmpLastEggLogDate)
        }
        val _tmp_9: Int = if (entity.readyForSale) 1 else 0
        statement.bindLong(61, _tmp_9.toLong())
        val _tmpTargetWeight: Double? = entity.targetWeight
        if (_tmpTargetWeight == null) {
          statement.bindNull(62)
        } else {
          statement.bindDouble(62, _tmpTargetWeight)
        }
        val _tmp_10: Int = if (entity.isShowcased) 1 else 0
        statement.bindLong(63, _tmp_10.toLong())
        val _tmpExternalVideoUrl: String? = entity.externalVideoUrl
        if (_tmpExternalVideoUrl == null) {
          statement.bindNull(64)
        } else {
          statement.bindText(64, _tmpExternalVideoUrl)
        }
        val _tmpRecordsLockedAt: Long? = entity.recordsLockedAt
        if (_tmpRecordsLockedAt == null) {
          statement.bindNull(65)
        } else {
          statement.bindLong(65, _tmpRecordsLockedAt)
        }
        statement.bindLong(66, entity.autoLockAfterDays.toLong())
        val _tmpLineageHistoryJson: String? = entity.lineageHistoryJson
        if (_tmpLineageHistoryJson == null) {
          statement.bindNull(67)
        } else {
          statement.bindText(67, _tmpLineageHistoryJson)
        }
        statement.bindLong(68, entity.editCount.toLong())
        val _tmpLastEditedBy: String? = entity.lastEditedBy
        if (_tmpLastEditedBy == null) {
          statement.bindNull(69)
        } else {
          statement.bindText(69, _tmpLastEditedBy)
        }
        val _tmp_11: Int = if (entity.adminFlagged) 1 else 0
        statement.bindLong(70, _tmp_11.toLong())
        val _tmpModerationNote: String? = entity.moderationNote
        if (_tmpModerationNote == null) {
          statement.bindNull(71)
        } else {
          statement.bindText(71, _tmpModerationNote)
        }
        statement.bindText(72, entity.metadataJson)
        statement.bindText(73, entity.productId)
      }
    }
  }

  public override suspend fun insertProductFts(productFts: ProductFtsEntity): Unit =
      performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfProductFtsEntity.insert(_connection, productFts)
  }

  public override suspend fun insertProductInternal(product: ProductEntity): Unit =
      performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfProductEntity.insert(_connection, product)
  }

  public override suspend fun insertProductsInternal(products: List<ProductEntity>): Unit =
      performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfProductEntity.insert(_connection, products)
  }

  public override suspend fun updateProductInternal(product: ProductEntity): Unit =
      performSuspending(__db, false, true) { _connection ->
    __updateAdapterOfProductEntity.handle(_connection, product)
  }

  public override suspend fun insertProduct(product: ProductEntity): Unit =
      performInTransactionSuspending(__db) {
    super@ProductDao_Impl.insertProduct(product)
  }

  public override suspend fun upsert(product: ProductEntity): Unit =
      performInTransactionSuspending(__db) {
    super@ProductDao_Impl.upsert(product)
  }

  public override suspend fun deleteProduct(productId: String): Unit =
      performInTransactionSuspending(__db) {
    super@ProductDao_Impl.deleteProduct(productId)
  }

  public override suspend fun deleteProductById(productId: String): Unit =
      performInTransactionSuspending(__db) {
    super@ProductDao_Impl.deleteProductById(productId)
  }

  public override suspend fun deleteAllProducts(): Unit = performInTransactionSuspending(__db) {
    super@ProductDao_Impl.deleteAllProducts()
  }

  public override suspend fun replaceAllProducts(products: List<ProductEntity>): Unit =
      performInTransactionSuspending(__db) {
    super@ProductDao_Impl.replaceAllProducts(products)
  }

  public override fun getProductById(productId: String): Flow<ProductEntity?> {
    val _sql: String = "SELECT * FROM products WHERE productId = ?"
    return createFlow(__db, false, arrayOf("products")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, productId)
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfSellerId: Int = getColumnIndexOrThrow(_stmt, "sellerId")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfCategory: Int = getColumnIndexOrThrow(_stmt, "category")
        val _columnIndexOfPrice: Int = getColumnIndexOrThrow(_stmt, "price")
        val _columnIndexOfQuantity: Int = getColumnIndexOrThrow(_stmt, "quantity")
        val _columnIndexOfUnit: Int = getColumnIndexOrThrow(_stmt, "unit")
        val _columnIndexOfLocation: Int = getColumnIndexOrThrow(_stmt, "location")
        val _columnIndexOfLatitude: Int = getColumnIndexOrThrow(_stmt, "latitude")
        val _columnIndexOfLongitude: Int = getColumnIndexOrThrow(_stmt, "longitude")
        val _columnIndexOfImageUrls: Int = getColumnIndexOrThrow(_stmt, "imageUrls")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfCondition: Int = getColumnIndexOrThrow(_stmt, "condition")
        val _columnIndexOfHarvestDate: Int = getColumnIndexOrThrow(_stmt, "harvestDate")
        val _columnIndexOfExpiryDate: Int = getColumnIndexOrThrow(_stmt, "expiryDate")
        val _columnIndexOfBirthDate: Int = getColumnIndexOrThrow(_stmt, "birthDate")
        val _columnIndexOfVaccinationRecordsJson: Int = getColumnIndexOrThrow(_stmt,
            "vaccinationRecordsJson")
        val _columnIndexOfWeightGrams: Int = getColumnIndexOrThrow(_stmt, "weightGrams")
        val _columnIndexOfHeightCm: Int = getColumnIndexOrThrow(_stmt, "heightCm")
        val _columnIndexOfGender: Int = getColumnIndexOrThrow(_stmt, "gender")
        val _columnIndexOfColor: Int = getColumnIndexOrThrow(_stmt, "color")
        val _columnIndexOfBreed: Int = getColumnIndexOrThrow(_stmt, "breed")
        val _columnIndexOfRaisingPurpose: Int = getColumnIndexOrThrow(_stmt, "raisingPurpose")
        val _columnIndexOfHealthStatus: Int = getColumnIndexOrThrow(_stmt, "healthStatus")
        val _columnIndexOfBirdCode: Int = getColumnIndexOrThrow(_stmt, "birdCode")
        val _columnIndexOfColorTag: Int = getColumnIndexOrThrow(_stmt, "colorTag")
        val _columnIndexOfFamilyTreeId: Int = getColumnIndexOrThrow(_stmt, "familyTreeId")
        val _columnIndexOfSourceAssetId: Int = getColumnIndexOrThrow(_stmt, "sourceAssetId")
        val _columnIndexOfParentIdsJson: Int = getColumnIndexOrThrow(_stmt, "parentIdsJson")
        val _columnIndexOfBreedingStatus: Int = getColumnIndexOrThrow(_stmt, "breedingStatus")
        val _columnIndexOfTransferHistoryJson: Int = getColumnIndexOrThrow(_stmt,
            "transferHistoryJson")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfLastModifiedAt: Int = getColumnIndexOrThrow(_stmt, "lastModifiedAt")
        val _columnIndexOfIsDeleted: Int = getColumnIndexOrThrow(_stmt, "isDeleted")
        val _columnIndexOfDeletedAt: Int = getColumnIndexOrThrow(_stmt, "deletedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfStage: Int = getColumnIndexOrThrow(_stmt, "stage")
        val _columnIndexOfLifecycleStatus: Int = getColumnIndexOrThrow(_stmt, "lifecycleStatus")
        val _columnIndexOfParentMaleId: Int = getColumnIndexOrThrow(_stmt, "parentMaleId")
        val _columnIndexOfParentFemaleId: Int = getColumnIndexOrThrow(_stmt, "parentFemaleId")
        val _columnIndexOfAgeWeeks: Int = getColumnIndexOrThrow(_stmt, "ageWeeks")
        val _columnIndexOfLastStageTransitionAt: Int = getColumnIndexOrThrow(_stmt,
            "lastStageTransitionAt")
        val _columnIndexOfBreederEligibleAt: Int = getColumnIndexOrThrow(_stmt, "breederEligibleAt")
        val _columnIndexOfIsBatch: Int = getColumnIndexOrThrow(_stmt, "isBatch")
        val _columnIndexOfBatchId: Int = getColumnIndexOrThrow(_stmt, "batchId")
        val _columnIndexOfSplitAt: Int = getColumnIndexOrThrow(_stmt, "splitAt")
        val _columnIndexOfSplitIntoIds: Int = getColumnIndexOrThrow(_stmt, "splitIntoIds")
        val _columnIndexOfDocumentUrls: Int = getColumnIndexOrThrow(_stmt, "documentUrls")
        val _columnIndexOfQrCodeUrl: Int = getColumnIndexOrThrow(_stmt, "qrCodeUrl")
        val _columnIndexOfCustomStatus: Int = getColumnIndexOrThrow(_stmt, "customStatus")
        val _columnIndexOfDebug: Int = getColumnIndexOrThrow(_stmt, "debug")
        val _columnIndexOfDeliveryOptions: Int = getColumnIndexOrThrow(_stmt, "deliveryOptions")
        val _columnIndexOfDeliveryCost: Int = getColumnIndexOrThrow(_stmt, "deliveryCost")
        val _columnIndexOfLeadTimeDays: Int = getColumnIndexOrThrow(_stmt, "leadTimeDays")
        val _columnIndexOfMotherId: Int = getColumnIndexOrThrow(_stmt, "motherId")
        val _columnIndexOfIsBreedingUnit: Int = getColumnIndexOrThrow(_stmt, "isBreedingUnit")
        val _columnIndexOfEggsCollectedToday: Int = getColumnIndexOrThrow(_stmt,
            "eggsCollectedToday")
        val _columnIndexOfLastEggLogDate: Int = getColumnIndexOrThrow(_stmt, "lastEggLogDate")
        val _columnIndexOfReadyForSale: Int = getColumnIndexOrThrow(_stmt, "readyForSale")
        val _columnIndexOfTargetWeight: Int = getColumnIndexOrThrow(_stmt, "targetWeight")
        val _columnIndexOfIsShowcased: Int = getColumnIndexOrThrow(_stmt, "isShowcased")
        val _columnIndexOfExternalVideoUrl: Int = getColumnIndexOrThrow(_stmt, "externalVideoUrl")
        val _columnIndexOfRecordsLockedAt: Int = getColumnIndexOrThrow(_stmt, "recordsLockedAt")
        val _columnIndexOfAutoLockAfterDays: Int = getColumnIndexOrThrow(_stmt, "autoLockAfterDays")
        val _columnIndexOfLineageHistoryJson: Int = getColumnIndexOrThrow(_stmt,
            "lineageHistoryJson")
        val _columnIndexOfEditCount: Int = getColumnIndexOrThrow(_stmt, "editCount")
        val _columnIndexOfLastEditedBy: Int = getColumnIndexOrThrow(_stmt, "lastEditedBy")
        val _columnIndexOfAdminFlagged: Int = getColumnIndexOrThrow(_stmt, "adminFlagged")
        val _columnIndexOfModerationNote: Int = getColumnIndexOrThrow(_stmt, "moderationNote")
        val _columnIndexOfMetadataJson: Int = getColumnIndexOrThrow(_stmt, "metadataJson")
        val _result: ProductEntity?
        if (_stmt.step()) {
          val _tmpProductId: String
          _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          val _tmpSellerId: String
          _tmpSellerId = _stmt.getText(_columnIndexOfSellerId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpDescription: String
          _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          val _tmpCategory: String
          _tmpCategory = _stmt.getText(_columnIndexOfCategory)
          val _tmpPrice: Double
          _tmpPrice = _stmt.getDouble(_columnIndexOfPrice)
          val _tmpQuantity: Double
          _tmpQuantity = _stmt.getDouble(_columnIndexOfQuantity)
          val _tmpUnit: String
          _tmpUnit = _stmt.getText(_columnIndexOfUnit)
          val _tmpLocation: String
          _tmpLocation = _stmt.getText(_columnIndexOfLocation)
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
          val _tmpImageUrls: List<String>
          val _tmp: String?
          if (_stmt.isNull(_columnIndexOfImageUrls)) {
            _tmp = null
          } else {
            _tmp = _stmt.getText(_columnIndexOfImageUrls)
          }
          val _tmp_1: List<String>? = AppDatabase.Converters.toStringList(_tmp)
          if (_tmp_1 == null) {
            error("Expected NON-NULL 'kotlin.collections.List<kotlin.String>', but it was NULL.")
          } else {
            _tmpImageUrls = _tmp_1
          }
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpCondition: String?
          if (_stmt.isNull(_columnIndexOfCondition)) {
            _tmpCondition = null
          } else {
            _tmpCondition = _stmt.getText(_columnIndexOfCondition)
          }
          val _tmpHarvestDate: Long?
          if (_stmt.isNull(_columnIndexOfHarvestDate)) {
            _tmpHarvestDate = null
          } else {
            _tmpHarvestDate = _stmt.getLong(_columnIndexOfHarvestDate)
          }
          val _tmpExpiryDate: Long?
          if (_stmt.isNull(_columnIndexOfExpiryDate)) {
            _tmpExpiryDate = null
          } else {
            _tmpExpiryDate = _stmt.getLong(_columnIndexOfExpiryDate)
          }
          val _tmpBirthDate: Long?
          if (_stmt.isNull(_columnIndexOfBirthDate)) {
            _tmpBirthDate = null
          } else {
            _tmpBirthDate = _stmt.getLong(_columnIndexOfBirthDate)
          }
          val _tmpVaccinationRecordsJson: String?
          if (_stmt.isNull(_columnIndexOfVaccinationRecordsJson)) {
            _tmpVaccinationRecordsJson = null
          } else {
            _tmpVaccinationRecordsJson = _stmt.getText(_columnIndexOfVaccinationRecordsJson)
          }
          val _tmpWeightGrams: Double?
          if (_stmt.isNull(_columnIndexOfWeightGrams)) {
            _tmpWeightGrams = null
          } else {
            _tmpWeightGrams = _stmt.getDouble(_columnIndexOfWeightGrams)
          }
          val _tmpHeightCm: Double?
          if (_stmt.isNull(_columnIndexOfHeightCm)) {
            _tmpHeightCm = null
          } else {
            _tmpHeightCm = _stmt.getDouble(_columnIndexOfHeightCm)
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
          val _tmpBreed: String?
          if (_stmt.isNull(_columnIndexOfBreed)) {
            _tmpBreed = null
          } else {
            _tmpBreed = _stmt.getText(_columnIndexOfBreed)
          }
          val _tmpRaisingPurpose: String?
          if (_stmt.isNull(_columnIndexOfRaisingPurpose)) {
            _tmpRaisingPurpose = null
          } else {
            _tmpRaisingPurpose = _stmt.getText(_columnIndexOfRaisingPurpose)
          }
          val _tmpHealthStatus: String?
          if (_stmt.isNull(_columnIndexOfHealthStatus)) {
            _tmpHealthStatus = null
          } else {
            _tmpHealthStatus = _stmt.getText(_columnIndexOfHealthStatus)
          }
          val _tmpBirdCode: String?
          if (_stmt.isNull(_columnIndexOfBirdCode)) {
            _tmpBirdCode = null
          } else {
            _tmpBirdCode = _stmt.getText(_columnIndexOfBirdCode)
          }
          val _tmpColorTag: String?
          if (_stmt.isNull(_columnIndexOfColorTag)) {
            _tmpColorTag = null
          } else {
            _tmpColorTag = _stmt.getText(_columnIndexOfColorTag)
          }
          val _tmpFamilyTreeId: String?
          if (_stmt.isNull(_columnIndexOfFamilyTreeId)) {
            _tmpFamilyTreeId = null
          } else {
            _tmpFamilyTreeId = _stmt.getText(_columnIndexOfFamilyTreeId)
          }
          val _tmpSourceAssetId: String?
          if (_stmt.isNull(_columnIndexOfSourceAssetId)) {
            _tmpSourceAssetId = null
          } else {
            _tmpSourceAssetId = _stmt.getText(_columnIndexOfSourceAssetId)
          }
          val _tmpParentIdsJson: String?
          if (_stmt.isNull(_columnIndexOfParentIdsJson)) {
            _tmpParentIdsJson = null
          } else {
            _tmpParentIdsJson = _stmt.getText(_columnIndexOfParentIdsJson)
          }
          val _tmpBreedingStatus: String?
          if (_stmt.isNull(_columnIndexOfBreedingStatus)) {
            _tmpBreedingStatus = null
          } else {
            _tmpBreedingStatus = _stmt.getText(_columnIndexOfBreedingStatus)
          }
          val _tmpTransferHistoryJson: String?
          if (_stmt.isNull(_columnIndexOfTransferHistoryJson)) {
            _tmpTransferHistoryJson = null
          } else {
            _tmpTransferHistoryJson = _stmt.getText(_columnIndexOfTransferHistoryJson)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpLastModifiedAt: Long
          _tmpLastModifiedAt = _stmt.getLong(_columnIndexOfLastModifiedAt)
          val _tmpIsDeleted: Boolean
          val _tmp_2: Int
          _tmp_2 = _stmt.getLong(_columnIndexOfIsDeleted).toInt()
          _tmpIsDeleted = _tmp_2 != 0
          val _tmpDeletedAt: Long?
          if (_stmt.isNull(_columnIndexOfDeletedAt)) {
            _tmpDeletedAt = null
          } else {
            _tmpDeletedAt = _stmt.getLong(_columnIndexOfDeletedAt)
          }
          val _tmpDirty: Boolean
          val _tmp_3: Int
          _tmp_3 = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp_3 != 0
          val _tmpStage: LifecycleStage?
          val _tmp_4: String?
          if (_stmt.isNull(_columnIndexOfStage)) {
            _tmp_4 = null
          } else {
            _tmp_4 = _stmt.getText(_columnIndexOfStage)
          }
          _tmpStage = AppDatabase.Converters.toLifecycleStage(_tmp_4)
          val _tmpLifecycleStatus: String?
          if (_stmt.isNull(_columnIndexOfLifecycleStatus)) {
            _tmpLifecycleStatus = null
          } else {
            _tmpLifecycleStatus = _stmt.getText(_columnIndexOfLifecycleStatus)
          }
          val _tmpParentMaleId: String?
          if (_stmt.isNull(_columnIndexOfParentMaleId)) {
            _tmpParentMaleId = null
          } else {
            _tmpParentMaleId = _stmt.getText(_columnIndexOfParentMaleId)
          }
          val _tmpParentFemaleId: String?
          if (_stmt.isNull(_columnIndexOfParentFemaleId)) {
            _tmpParentFemaleId = null
          } else {
            _tmpParentFemaleId = _stmt.getText(_columnIndexOfParentFemaleId)
          }
          val _tmpAgeWeeks: Int?
          if (_stmt.isNull(_columnIndexOfAgeWeeks)) {
            _tmpAgeWeeks = null
          } else {
            _tmpAgeWeeks = _stmt.getLong(_columnIndexOfAgeWeeks).toInt()
          }
          val _tmpLastStageTransitionAt: Long?
          if (_stmt.isNull(_columnIndexOfLastStageTransitionAt)) {
            _tmpLastStageTransitionAt = null
          } else {
            _tmpLastStageTransitionAt = _stmt.getLong(_columnIndexOfLastStageTransitionAt)
          }
          val _tmpBreederEligibleAt: Long?
          if (_stmt.isNull(_columnIndexOfBreederEligibleAt)) {
            _tmpBreederEligibleAt = null
          } else {
            _tmpBreederEligibleAt = _stmt.getLong(_columnIndexOfBreederEligibleAt)
          }
          val _tmpIsBatch: Boolean?
          val _tmp_5: Int?
          if (_stmt.isNull(_columnIndexOfIsBatch)) {
            _tmp_5 = null
          } else {
            _tmp_5 = _stmt.getLong(_columnIndexOfIsBatch).toInt()
          }
          _tmpIsBatch = _tmp_5?.let { it != 0 }
          val _tmpBatchId: String?
          if (_stmt.isNull(_columnIndexOfBatchId)) {
            _tmpBatchId = null
          } else {
            _tmpBatchId = _stmt.getText(_columnIndexOfBatchId)
          }
          val _tmpSplitAt: Long?
          if (_stmt.isNull(_columnIndexOfSplitAt)) {
            _tmpSplitAt = null
          } else {
            _tmpSplitAt = _stmt.getLong(_columnIndexOfSplitAt)
          }
          val _tmpSplitIntoIds: String?
          if (_stmt.isNull(_columnIndexOfSplitIntoIds)) {
            _tmpSplitIntoIds = null
          } else {
            _tmpSplitIntoIds = _stmt.getText(_columnIndexOfSplitIntoIds)
          }
          val _tmpDocumentUrls: List<String>
          val _tmp_6: String?
          if (_stmt.isNull(_columnIndexOfDocumentUrls)) {
            _tmp_6 = null
          } else {
            _tmp_6 = _stmt.getText(_columnIndexOfDocumentUrls)
          }
          val _tmp_7: List<String>? = AppDatabase.Converters.toStringList(_tmp_6)
          if (_tmp_7 == null) {
            error("Expected NON-NULL 'kotlin.collections.List<kotlin.String>', but it was NULL.")
          } else {
            _tmpDocumentUrls = _tmp_7
          }
          val _tmpQrCodeUrl: String?
          if (_stmt.isNull(_columnIndexOfQrCodeUrl)) {
            _tmpQrCodeUrl = null
          } else {
            _tmpQrCodeUrl = _stmt.getText(_columnIndexOfQrCodeUrl)
          }
          val _tmpCustomStatus: String?
          if (_stmt.isNull(_columnIndexOfCustomStatus)) {
            _tmpCustomStatus = null
          } else {
            _tmpCustomStatus = _stmt.getText(_columnIndexOfCustomStatus)
          }
          val _tmpDebug: Boolean
          val _tmp_8: Int
          _tmp_8 = _stmt.getLong(_columnIndexOfDebug).toInt()
          _tmpDebug = _tmp_8 != 0
          val _tmpDeliveryOptions: List<String>
          val _tmp_9: String?
          if (_stmt.isNull(_columnIndexOfDeliveryOptions)) {
            _tmp_9 = null
          } else {
            _tmp_9 = _stmt.getText(_columnIndexOfDeliveryOptions)
          }
          val _tmp_10: List<String>? = AppDatabase.Converters.toStringList(_tmp_9)
          if (_tmp_10 == null) {
            error("Expected NON-NULL 'kotlin.collections.List<kotlin.String>', but it was NULL.")
          } else {
            _tmpDeliveryOptions = _tmp_10
          }
          val _tmpDeliveryCost: Double?
          if (_stmt.isNull(_columnIndexOfDeliveryCost)) {
            _tmpDeliveryCost = null
          } else {
            _tmpDeliveryCost = _stmt.getDouble(_columnIndexOfDeliveryCost)
          }
          val _tmpLeadTimeDays: Int?
          if (_stmt.isNull(_columnIndexOfLeadTimeDays)) {
            _tmpLeadTimeDays = null
          } else {
            _tmpLeadTimeDays = _stmt.getLong(_columnIndexOfLeadTimeDays).toInt()
          }
          val _tmpMotherId: String?
          if (_stmt.isNull(_columnIndexOfMotherId)) {
            _tmpMotherId = null
          } else {
            _tmpMotherId = _stmt.getText(_columnIndexOfMotherId)
          }
          val _tmpIsBreedingUnit: Boolean
          val _tmp_11: Int
          _tmp_11 = _stmt.getLong(_columnIndexOfIsBreedingUnit).toInt()
          _tmpIsBreedingUnit = _tmp_11 != 0
          val _tmpEggsCollectedToday: Int
          _tmpEggsCollectedToday = _stmt.getLong(_columnIndexOfEggsCollectedToday).toInt()
          val _tmpLastEggLogDate: Long?
          if (_stmt.isNull(_columnIndexOfLastEggLogDate)) {
            _tmpLastEggLogDate = null
          } else {
            _tmpLastEggLogDate = _stmt.getLong(_columnIndexOfLastEggLogDate)
          }
          val _tmpReadyForSale: Boolean
          val _tmp_12: Int
          _tmp_12 = _stmt.getLong(_columnIndexOfReadyForSale).toInt()
          _tmpReadyForSale = _tmp_12 != 0
          val _tmpTargetWeight: Double?
          if (_stmt.isNull(_columnIndexOfTargetWeight)) {
            _tmpTargetWeight = null
          } else {
            _tmpTargetWeight = _stmt.getDouble(_columnIndexOfTargetWeight)
          }
          val _tmpIsShowcased: Boolean
          val _tmp_13: Int
          _tmp_13 = _stmt.getLong(_columnIndexOfIsShowcased).toInt()
          _tmpIsShowcased = _tmp_13 != 0
          val _tmpExternalVideoUrl: String?
          if (_stmt.isNull(_columnIndexOfExternalVideoUrl)) {
            _tmpExternalVideoUrl = null
          } else {
            _tmpExternalVideoUrl = _stmt.getText(_columnIndexOfExternalVideoUrl)
          }
          val _tmpRecordsLockedAt: Long?
          if (_stmt.isNull(_columnIndexOfRecordsLockedAt)) {
            _tmpRecordsLockedAt = null
          } else {
            _tmpRecordsLockedAt = _stmt.getLong(_columnIndexOfRecordsLockedAt)
          }
          val _tmpAutoLockAfterDays: Int
          _tmpAutoLockAfterDays = _stmt.getLong(_columnIndexOfAutoLockAfterDays).toInt()
          val _tmpLineageHistoryJson: String?
          if (_stmt.isNull(_columnIndexOfLineageHistoryJson)) {
            _tmpLineageHistoryJson = null
          } else {
            _tmpLineageHistoryJson = _stmt.getText(_columnIndexOfLineageHistoryJson)
          }
          val _tmpEditCount: Int
          _tmpEditCount = _stmt.getLong(_columnIndexOfEditCount).toInt()
          val _tmpLastEditedBy: String?
          if (_stmt.isNull(_columnIndexOfLastEditedBy)) {
            _tmpLastEditedBy = null
          } else {
            _tmpLastEditedBy = _stmt.getText(_columnIndexOfLastEditedBy)
          }
          val _tmpAdminFlagged: Boolean
          val _tmp_14: Int
          _tmp_14 = _stmt.getLong(_columnIndexOfAdminFlagged).toInt()
          _tmpAdminFlagged = _tmp_14 != 0
          val _tmpModerationNote: String?
          if (_stmt.isNull(_columnIndexOfModerationNote)) {
            _tmpModerationNote = null
          } else {
            _tmpModerationNote = _stmt.getText(_columnIndexOfModerationNote)
          }
          val _tmpMetadataJson: String
          _tmpMetadataJson = _stmt.getText(_columnIndexOfMetadataJson)
          _result =
              ProductEntity(_tmpProductId,_tmpSellerId,_tmpName,_tmpDescription,_tmpCategory,_tmpPrice,_tmpQuantity,_tmpUnit,_tmpLocation,_tmpLatitude,_tmpLongitude,_tmpImageUrls,_tmpStatus,_tmpCondition,_tmpHarvestDate,_tmpExpiryDate,_tmpBirthDate,_tmpVaccinationRecordsJson,_tmpWeightGrams,_tmpHeightCm,_tmpGender,_tmpColor,_tmpBreed,_tmpRaisingPurpose,_tmpHealthStatus,_tmpBirdCode,_tmpColorTag,_tmpFamilyTreeId,_tmpSourceAssetId,_tmpParentIdsJson,_tmpBreedingStatus,_tmpTransferHistoryJson,_tmpCreatedAt,_tmpUpdatedAt,_tmpLastModifiedAt,_tmpIsDeleted,_tmpDeletedAt,_tmpDirty,_tmpStage,_tmpLifecycleStatus,_tmpParentMaleId,_tmpParentFemaleId,_tmpAgeWeeks,_tmpLastStageTransitionAt,_tmpBreederEligibleAt,_tmpIsBatch,_tmpBatchId,_tmpSplitAt,_tmpSplitIntoIds,_tmpDocumentUrls,_tmpQrCodeUrl,_tmpCustomStatus,_tmpDebug,_tmpDeliveryOptions,_tmpDeliveryCost,_tmpLeadTimeDays,_tmpMotherId,_tmpIsBreedingUnit,_tmpEggsCollectedToday,_tmpLastEggLogDate,_tmpReadyForSale,_tmpTargetWeight,_tmpIsShowcased,_tmpExternalVideoUrl,_tmpRecordsLockedAt,_tmpAutoLockAfterDays,_tmpLineageHistoryJson,_tmpEditCount,_tmpLastEditedBy,_tmpAdminFlagged,_tmpModerationNote,_tmpMetadataJson)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun findById(productId: String): ProductEntity? {
    val _sql: String = "SELECT * FROM products WHERE productId = ? LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, productId)
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfSellerId: Int = getColumnIndexOrThrow(_stmt, "sellerId")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfCategory: Int = getColumnIndexOrThrow(_stmt, "category")
        val _columnIndexOfPrice: Int = getColumnIndexOrThrow(_stmt, "price")
        val _columnIndexOfQuantity: Int = getColumnIndexOrThrow(_stmt, "quantity")
        val _columnIndexOfUnit: Int = getColumnIndexOrThrow(_stmt, "unit")
        val _columnIndexOfLocation: Int = getColumnIndexOrThrow(_stmt, "location")
        val _columnIndexOfLatitude: Int = getColumnIndexOrThrow(_stmt, "latitude")
        val _columnIndexOfLongitude: Int = getColumnIndexOrThrow(_stmt, "longitude")
        val _columnIndexOfImageUrls: Int = getColumnIndexOrThrow(_stmt, "imageUrls")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfCondition: Int = getColumnIndexOrThrow(_stmt, "condition")
        val _columnIndexOfHarvestDate: Int = getColumnIndexOrThrow(_stmt, "harvestDate")
        val _columnIndexOfExpiryDate: Int = getColumnIndexOrThrow(_stmt, "expiryDate")
        val _columnIndexOfBirthDate: Int = getColumnIndexOrThrow(_stmt, "birthDate")
        val _columnIndexOfVaccinationRecordsJson: Int = getColumnIndexOrThrow(_stmt,
            "vaccinationRecordsJson")
        val _columnIndexOfWeightGrams: Int = getColumnIndexOrThrow(_stmt, "weightGrams")
        val _columnIndexOfHeightCm: Int = getColumnIndexOrThrow(_stmt, "heightCm")
        val _columnIndexOfGender: Int = getColumnIndexOrThrow(_stmt, "gender")
        val _columnIndexOfColor: Int = getColumnIndexOrThrow(_stmt, "color")
        val _columnIndexOfBreed: Int = getColumnIndexOrThrow(_stmt, "breed")
        val _columnIndexOfRaisingPurpose: Int = getColumnIndexOrThrow(_stmt, "raisingPurpose")
        val _columnIndexOfHealthStatus: Int = getColumnIndexOrThrow(_stmt, "healthStatus")
        val _columnIndexOfBirdCode: Int = getColumnIndexOrThrow(_stmt, "birdCode")
        val _columnIndexOfColorTag: Int = getColumnIndexOrThrow(_stmt, "colorTag")
        val _columnIndexOfFamilyTreeId: Int = getColumnIndexOrThrow(_stmt, "familyTreeId")
        val _columnIndexOfSourceAssetId: Int = getColumnIndexOrThrow(_stmt, "sourceAssetId")
        val _columnIndexOfParentIdsJson: Int = getColumnIndexOrThrow(_stmt, "parentIdsJson")
        val _columnIndexOfBreedingStatus: Int = getColumnIndexOrThrow(_stmt, "breedingStatus")
        val _columnIndexOfTransferHistoryJson: Int = getColumnIndexOrThrow(_stmt,
            "transferHistoryJson")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfLastModifiedAt: Int = getColumnIndexOrThrow(_stmt, "lastModifiedAt")
        val _columnIndexOfIsDeleted: Int = getColumnIndexOrThrow(_stmt, "isDeleted")
        val _columnIndexOfDeletedAt: Int = getColumnIndexOrThrow(_stmt, "deletedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfStage: Int = getColumnIndexOrThrow(_stmt, "stage")
        val _columnIndexOfLifecycleStatus: Int = getColumnIndexOrThrow(_stmt, "lifecycleStatus")
        val _columnIndexOfParentMaleId: Int = getColumnIndexOrThrow(_stmt, "parentMaleId")
        val _columnIndexOfParentFemaleId: Int = getColumnIndexOrThrow(_stmt, "parentFemaleId")
        val _columnIndexOfAgeWeeks: Int = getColumnIndexOrThrow(_stmt, "ageWeeks")
        val _columnIndexOfLastStageTransitionAt: Int = getColumnIndexOrThrow(_stmt,
            "lastStageTransitionAt")
        val _columnIndexOfBreederEligibleAt: Int = getColumnIndexOrThrow(_stmt, "breederEligibleAt")
        val _columnIndexOfIsBatch: Int = getColumnIndexOrThrow(_stmt, "isBatch")
        val _columnIndexOfBatchId: Int = getColumnIndexOrThrow(_stmt, "batchId")
        val _columnIndexOfSplitAt: Int = getColumnIndexOrThrow(_stmt, "splitAt")
        val _columnIndexOfSplitIntoIds: Int = getColumnIndexOrThrow(_stmt, "splitIntoIds")
        val _columnIndexOfDocumentUrls: Int = getColumnIndexOrThrow(_stmt, "documentUrls")
        val _columnIndexOfQrCodeUrl: Int = getColumnIndexOrThrow(_stmt, "qrCodeUrl")
        val _columnIndexOfCustomStatus: Int = getColumnIndexOrThrow(_stmt, "customStatus")
        val _columnIndexOfDebug: Int = getColumnIndexOrThrow(_stmt, "debug")
        val _columnIndexOfDeliveryOptions: Int = getColumnIndexOrThrow(_stmt, "deliveryOptions")
        val _columnIndexOfDeliveryCost: Int = getColumnIndexOrThrow(_stmt, "deliveryCost")
        val _columnIndexOfLeadTimeDays: Int = getColumnIndexOrThrow(_stmt, "leadTimeDays")
        val _columnIndexOfMotherId: Int = getColumnIndexOrThrow(_stmt, "motherId")
        val _columnIndexOfIsBreedingUnit: Int = getColumnIndexOrThrow(_stmt, "isBreedingUnit")
        val _columnIndexOfEggsCollectedToday: Int = getColumnIndexOrThrow(_stmt,
            "eggsCollectedToday")
        val _columnIndexOfLastEggLogDate: Int = getColumnIndexOrThrow(_stmt, "lastEggLogDate")
        val _columnIndexOfReadyForSale: Int = getColumnIndexOrThrow(_stmt, "readyForSale")
        val _columnIndexOfTargetWeight: Int = getColumnIndexOrThrow(_stmt, "targetWeight")
        val _columnIndexOfIsShowcased: Int = getColumnIndexOrThrow(_stmt, "isShowcased")
        val _columnIndexOfExternalVideoUrl: Int = getColumnIndexOrThrow(_stmt, "externalVideoUrl")
        val _columnIndexOfRecordsLockedAt: Int = getColumnIndexOrThrow(_stmt, "recordsLockedAt")
        val _columnIndexOfAutoLockAfterDays: Int = getColumnIndexOrThrow(_stmt, "autoLockAfterDays")
        val _columnIndexOfLineageHistoryJson: Int = getColumnIndexOrThrow(_stmt,
            "lineageHistoryJson")
        val _columnIndexOfEditCount: Int = getColumnIndexOrThrow(_stmt, "editCount")
        val _columnIndexOfLastEditedBy: Int = getColumnIndexOrThrow(_stmt, "lastEditedBy")
        val _columnIndexOfAdminFlagged: Int = getColumnIndexOrThrow(_stmt, "adminFlagged")
        val _columnIndexOfModerationNote: Int = getColumnIndexOrThrow(_stmt, "moderationNote")
        val _columnIndexOfMetadataJson: Int = getColumnIndexOrThrow(_stmt, "metadataJson")
        val _result: ProductEntity?
        if (_stmt.step()) {
          val _tmpProductId: String
          _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          val _tmpSellerId: String
          _tmpSellerId = _stmt.getText(_columnIndexOfSellerId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpDescription: String
          _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          val _tmpCategory: String
          _tmpCategory = _stmt.getText(_columnIndexOfCategory)
          val _tmpPrice: Double
          _tmpPrice = _stmt.getDouble(_columnIndexOfPrice)
          val _tmpQuantity: Double
          _tmpQuantity = _stmt.getDouble(_columnIndexOfQuantity)
          val _tmpUnit: String
          _tmpUnit = _stmt.getText(_columnIndexOfUnit)
          val _tmpLocation: String
          _tmpLocation = _stmt.getText(_columnIndexOfLocation)
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
          val _tmpImageUrls: List<String>
          val _tmp: String?
          if (_stmt.isNull(_columnIndexOfImageUrls)) {
            _tmp = null
          } else {
            _tmp = _stmt.getText(_columnIndexOfImageUrls)
          }
          val _tmp_1: List<String>? = AppDatabase.Converters.toStringList(_tmp)
          if (_tmp_1 == null) {
            error("Expected NON-NULL 'kotlin.collections.List<kotlin.String>', but it was NULL.")
          } else {
            _tmpImageUrls = _tmp_1
          }
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpCondition: String?
          if (_stmt.isNull(_columnIndexOfCondition)) {
            _tmpCondition = null
          } else {
            _tmpCondition = _stmt.getText(_columnIndexOfCondition)
          }
          val _tmpHarvestDate: Long?
          if (_stmt.isNull(_columnIndexOfHarvestDate)) {
            _tmpHarvestDate = null
          } else {
            _tmpHarvestDate = _stmt.getLong(_columnIndexOfHarvestDate)
          }
          val _tmpExpiryDate: Long?
          if (_stmt.isNull(_columnIndexOfExpiryDate)) {
            _tmpExpiryDate = null
          } else {
            _tmpExpiryDate = _stmt.getLong(_columnIndexOfExpiryDate)
          }
          val _tmpBirthDate: Long?
          if (_stmt.isNull(_columnIndexOfBirthDate)) {
            _tmpBirthDate = null
          } else {
            _tmpBirthDate = _stmt.getLong(_columnIndexOfBirthDate)
          }
          val _tmpVaccinationRecordsJson: String?
          if (_stmt.isNull(_columnIndexOfVaccinationRecordsJson)) {
            _tmpVaccinationRecordsJson = null
          } else {
            _tmpVaccinationRecordsJson = _stmt.getText(_columnIndexOfVaccinationRecordsJson)
          }
          val _tmpWeightGrams: Double?
          if (_stmt.isNull(_columnIndexOfWeightGrams)) {
            _tmpWeightGrams = null
          } else {
            _tmpWeightGrams = _stmt.getDouble(_columnIndexOfWeightGrams)
          }
          val _tmpHeightCm: Double?
          if (_stmt.isNull(_columnIndexOfHeightCm)) {
            _tmpHeightCm = null
          } else {
            _tmpHeightCm = _stmt.getDouble(_columnIndexOfHeightCm)
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
          val _tmpBreed: String?
          if (_stmt.isNull(_columnIndexOfBreed)) {
            _tmpBreed = null
          } else {
            _tmpBreed = _stmt.getText(_columnIndexOfBreed)
          }
          val _tmpRaisingPurpose: String?
          if (_stmt.isNull(_columnIndexOfRaisingPurpose)) {
            _tmpRaisingPurpose = null
          } else {
            _tmpRaisingPurpose = _stmt.getText(_columnIndexOfRaisingPurpose)
          }
          val _tmpHealthStatus: String?
          if (_stmt.isNull(_columnIndexOfHealthStatus)) {
            _tmpHealthStatus = null
          } else {
            _tmpHealthStatus = _stmt.getText(_columnIndexOfHealthStatus)
          }
          val _tmpBirdCode: String?
          if (_stmt.isNull(_columnIndexOfBirdCode)) {
            _tmpBirdCode = null
          } else {
            _tmpBirdCode = _stmt.getText(_columnIndexOfBirdCode)
          }
          val _tmpColorTag: String?
          if (_stmt.isNull(_columnIndexOfColorTag)) {
            _tmpColorTag = null
          } else {
            _tmpColorTag = _stmt.getText(_columnIndexOfColorTag)
          }
          val _tmpFamilyTreeId: String?
          if (_stmt.isNull(_columnIndexOfFamilyTreeId)) {
            _tmpFamilyTreeId = null
          } else {
            _tmpFamilyTreeId = _stmt.getText(_columnIndexOfFamilyTreeId)
          }
          val _tmpSourceAssetId: String?
          if (_stmt.isNull(_columnIndexOfSourceAssetId)) {
            _tmpSourceAssetId = null
          } else {
            _tmpSourceAssetId = _stmt.getText(_columnIndexOfSourceAssetId)
          }
          val _tmpParentIdsJson: String?
          if (_stmt.isNull(_columnIndexOfParentIdsJson)) {
            _tmpParentIdsJson = null
          } else {
            _tmpParentIdsJson = _stmt.getText(_columnIndexOfParentIdsJson)
          }
          val _tmpBreedingStatus: String?
          if (_stmt.isNull(_columnIndexOfBreedingStatus)) {
            _tmpBreedingStatus = null
          } else {
            _tmpBreedingStatus = _stmt.getText(_columnIndexOfBreedingStatus)
          }
          val _tmpTransferHistoryJson: String?
          if (_stmt.isNull(_columnIndexOfTransferHistoryJson)) {
            _tmpTransferHistoryJson = null
          } else {
            _tmpTransferHistoryJson = _stmt.getText(_columnIndexOfTransferHistoryJson)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpLastModifiedAt: Long
          _tmpLastModifiedAt = _stmt.getLong(_columnIndexOfLastModifiedAt)
          val _tmpIsDeleted: Boolean
          val _tmp_2: Int
          _tmp_2 = _stmt.getLong(_columnIndexOfIsDeleted).toInt()
          _tmpIsDeleted = _tmp_2 != 0
          val _tmpDeletedAt: Long?
          if (_stmt.isNull(_columnIndexOfDeletedAt)) {
            _tmpDeletedAt = null
          } else {
            _tmpDeletedAt = _stmt.getLong(_columnIndexOfDeletedAt)
          }
          val _tmpDirty: Boolean
          val _tmp_3: Int
          _tmp_3 = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp_3 != 0
          val _tmpStage: LifecycleStage?
          val _tmp_4: String?
          if (_stmt.isNull(_columnIndexOfStage)) {
            _tmp_4 = null
          } else {
            _tmp_4 = _stmt.getText(_columnIndexOfStage)
          }
          _tmpStage = AppDatabase.Converters.toLifecycleStage(_tmp_4)
          val _tmpLifecycleStatus: String?
          if (_stmt.isNull(_columnIndexOfLifecycleStatus)) {
            _tmpLifecycleStatus = null
          } else {
            _tmpLifecycleStatus = _stmt.getText(_columnIndexOfLifecycleStatus)
          }
          val _tmpParentMaleId: String?
          if (_stmt.isNull(_columnIndexOfParentMaleId)) {
            _tmpParentMaleId = null
          } else {
            _tmpParentMaleId = _stmt.getText(_columnIndexOfParentMaleId)
          }
          val _tmpParentFemaleId: String?
          if (_stmt.isNull(_columnIndexOfParentFemaleId)) {
            _tmpParentFemaleId = null
          } else {
            _tmpParentFemaleId = _stmt.getText(_columnIndexOfParentFemaleId)
          }
          val _tmpAgeWeeks: Int?
          if (_stmt.isNull(_columnIndexOfAgeWeeks)) {
            _tmpAgeWeeks = null
          } else {
            _tmpAgeWeeks = _stmt.getLong(_columnIndexOfAgeWeeks).toInt()
          }
          val _tmpLastStageTransitionAt: Long?
          if (_stmt.isNull(_columnIndexOfLastStageTransitionAt)) {
            _tmpLastStageTransitionAt = null
          } else {
            _tmpLastStageTransitionAt = _stmt.getLong(_columnIndexOfLastStageTransitionAt)
          }
          val _tmpBreederEligibleAt: Long?
          if (_stmt.isNull(_columnIndexOfBreederEligibleAt)) {
            _tmpBreederEligibleAt = null
          } else {
            _tmpBreederEligibleAt = _stmt.getLong(_columnIndexOfBreederEligibleAt)
          }
          val _tmpIsBatch: Boolean?
          val _tmp_5: Int?
          if (_stmt.isNull(_columnIndexOfIsBatch)) {
            _tmp_5 = null
          } else {
            _tmp_5 = _stmt.getLong(_columnIndexOfIsBatch).toInt()
          }
          _tmpIsBatch = _tmp_5?.let { it != 0 }
          val _tmpBatchId: String?
          if (_stmt.isNull(_columnIndexOfBatchId)) {
            _tmpBatchId = null
          } else {
            _tmpBatchId = _stmt.getText(_columnIndexOfBatchId)
          }
          val _tmpSplitAt: Long?
          if (_stmt.isNull(_columnIndexOfSplitAt)) {
            _tmpSplitAt = null
          } else {
            _tmpSplitAt = _stmt.getLong(_columnIndexOfSplitAt)
          }
          val _tmpSplitIntoIds: String?
          if (_stmt.isNull(_columnIndexOfSplitIntoIds)) {
            _tmpSplitIntoIds = null
          } else {
            _tmpSplitIntoIds = _stmt.getText(_columnIndexOfSplitIntoIds)
          }
          val _tmpDocumentUrls: List<String>
          val _tmp_6: String?
          if (_stmt.isNull(_columnIndexOfDocumentUrls)) {
            _tmp_6 = null
          } else {
            _tmp_6 = _stmt.getText(_columnIndexOfDocumentUrls)
          }
          val _tmp_7: List<String>? = AppDatabase.Converters.toStringList(_tmp_6)
          if (_tmp_7 == null) {
            error("Expected NON-NULL 'kotlin.collections.List<kotlin.String>', but it was NULL.")
          } else {
            _tmpDocumentUrls = _tmp_7
          }
          val _tmpQrCodeUrl: String?
          if (_stmt.isNull(_columnIndexOfQrCodeUrl)) {
            _tmpQrCodeUrl = null
          } else {
            _tmpQrCodeUrl = _stmt.getText(_columnIndexOfQrCodeUrl)
          }
          val _tmpCustomStatus: String?
          if (_stmt.isNull(_columnIndexOfCustomStatus)) {
            _tmpCustomStatus = null
          } else {
            _tmpCustomStatus = _stmt.getText(_columnIndexOfCustomStatus)
          }
          val _tmpDebug: Boolean
          val _tmp_8: Int
          _tmp_8 = _stmt.getLong(_columnIndexOfDebug).toInt()
          _tmpDebug = _tmp_8 != 0
          val _tmpDeliveryOptions: List<String>
          val _tmp_9: String?
          if (_stmt.isNull(_columnIndexOfDeliveryOptions)) {
            _tmp_9 = null
          } else {
            _tmp_9 = _stmt.getText(_columnIndexOfDeliveryOptions)
          }
          val _tmp_10: List<String>? = AppDatabase.Converters.toStringList(_tmp_9)
          if (_tmp_10 == null) {
            error("Expected NON-NULL 'kotlin.collections.List<kotlin.String>', but it was NULL.")
          } else {
            _tmpDeliveryOptions = _tmp_10
          }
          val _tmpDeliveryCost: Double?
          if (_stmt.isNull(_columnIndexOfDeliveryCost)) {
            _tmpDeliveryCost = null
          } else {
            _tmpDeliveryCost = _stmt.getDouble(_columnIndexOfDeliveryCost)
          }
          val _tmpLeadTimeDays: Int?
          if (_stmt.isNull(_columnIndexOfLeadTimeDays)) {
            _tmpLeadTimeDays = null
          } else {
            _tmpLeadTimeDays = _stmt.getLong(_columnIndexOfLeadTimeDays).toInt()
          }
          val _tmpMotherId: String?
          if (_stmt.isNull(_columnIndexOfMotherId)) {
            _tmpMotherId = null
          } else {
            _tmpMotherId = _stmt.getText(_columnIndexOfMotherId)
          }
          val _tmpIsBreedingUnit: Boolean
          val _tmp_11: Int
          _tmp_11 = _stmt.getLong(_columnIndexOfIsBreedingUnit).toInt()
          _tmpIsBreedingUnit = _tmp_11 != 0
          val _tmpEggsCollectedToday: Int
          _tmpEggsCollectedToday = _stmt.getLong(_columnIndexOfEggsCollectedToday).toInt()
          val _tmpLastEggLogDate: Long?
          if (_stmt.isNull(_columnIndexOfLastEggLogDate)) {
            _tmpLastEggLogDate = null
          } else {
            _tmpLastEggLogDate = _stmt.getLong(_columnIndexOfLastEggLogDate)
          }
          val _tmpReadyForSale: Boolean
          val _tmp_12: Int
          _tmp_12 = _stmt.getLong(_columnIndexOfReadyForSale).toInt()
          _tmpReadyForSale = _tmp_12 != 0
          val _tmpTargetWeight: Double?
          if (_stmt.isNull(_columnIndexOfTargetWeight)) {
            _tmpTargetWeight = null
          } else {
            _tmpTargetWeight = _stmt.getDouble(_columnIndexOfTargetWeight)
          }
          val _tmpIsShowcased: Boolean
          val _tmp_13: Int
          _tmp_13 = _stmt.getLong(_columnIndexOfIsShowcased).toInt()
          _tmpIsShowcased = _tmp_13 != 0
          val _tmpExternalVideoUrl: String?
          if (_stmt.isNull(_columnIndexOfExternalVideoUrl)) {
            _tmpExternalVideoUrl = null
          } else {
            _tmpExternalVideoUrl = _stmt.getText(_columnIndexOfExternalVideoUrl)
          }
          val _tmpRecordsLockedAt: Long?
          if (_stmt.isNull(_columnIndexOfRecordsLockedAt)) {
            _tmpRecordsLockedAt = null
          } else {
            _tmpRecordsLockedAt = _stmt.getLong(_columnIndexOfRecordsLockedAt)
          }
          val _tmpAutoLockAfterDays: Int
          _tmpAutoLockAfterDays = _stmt.getLong(_columnIndexOfAutoLockAfterDays).toInt()
          val _tmpLineageHistoryJson: String?
          if (_stmt.isNull(_columnIndexOfLineageHistoryJson)) {
            _tmpLineageHistoryJson = null
          } else {
            _tmpLineageHistoryJson = _stmt.getText(_columnIndexOfLineageHistoryJson)
          }
          val _tmpEditCount: Int
          _tmpEditCount = _stmt.getLong(_columnIndexOfEditCount).toInt()
          val _tmpLastEditedBy: String?
          if (_stmt.isNull(_columnIndexOfLastEditedBy)) {
            _tmpLastEditedBy = null
          } else {
            _tmpLastEditedBy = _stmt.getText(_columnIndexOfLastEditedBy)
          }
          val _tmpAdminFlagged: Boolean
          val _tmp_14: Int
          _tmp_14 = _stmt.getLong(_columnIndexOfAdminFlagged).toInt()
          _tmpAdminFlagged = _tmp_14 != 0
          val _tmpModerationNote: String?
          if (_stmt.isNull(_columnIndexOfModerationNote)) {
            _tmpModerationNote = null
          } else {
            _tmpModerationNote = _stmt.getText(_columnIndexOfModerationNote)
          }
          val _tmpMetadataJson: String
          _tmpMetadataJson = _stmt.getText(_columnIndexOfMetadataJson)
          _result =
              ProductEntity(_tmpProductId,_tmpSellerId,_tmpName,_tmpDescription,_tmpCategory,_tmpPrice,_tmpQuantity,_tmpUnit,_tmpLocation,_tmpLatitude,_tmpLongitude,_tmpImageUrls,_tmpStatus,_tmpCondition,_tmpHarvestDate,_tmpExpiryDate,_tmpBirthDate,_tmpVaccinationRecordsJson,_tmpWeightGrams,_tmpHeightCm,_tmpGender,_tmpColor,_tmpBreed,_tmpRaisingPurpose,_tmpHealthStatus,_tmpBirdCode,_tmpColorTag,_tmpFamilyTreeId,_tmpSourceAssetId,_tmpParentIdsJson,_tmpBreedingStatus,_tmpTransferHistoryJson,_tmpCreatedAt,_tmpUpdatedAt,_tmpLastModifiedAt,_tmpIsDeleted,_tmpDeletedAt,_tmpDirty,_tmpStage,_tmpLifecycleStatus,_tmpParentMaleId,_tmpParentFemaleId,_tmpAgeWeeks,_tmpLastStageTransitionAt,_tmpBreederEligibleAt,_tmpIsBatch,_tmpBatchId,_tmpSplitAt,_tmpSplitIntoIds,_tmpDocumentUrls,_tmpQrCodeUrl,_tmpCustomStatus,_tmpDebug,_tmpDeliveryOptions,_tmpDeliveryCost,_tmpLeadTimeDays,_tmpMotherId,_tmpIsBreedingUnit,_tmpEggsCollectedToday,_tmpLastEggLogDate,_tmpReadyForSale,_tmpTargetWeight,_tmpIsShowcased,_tmpExternalVideoUrl,_tmpRecordsLockedAt,_tmpAutoLockAfterDays,_tmpLineageHistoryJson,_tmpEditCount,_tmpLastEditedBy,_tmpAdminFlagged,_tmpModerationNote,_tmpMetadataJson)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getAllProducts(): Flow<List<ProductEntity>> {
    val _sql: String = "SELECT * FROM products ORDER BY name ASC"
    return createFlow(__db, false, arrayOf("products")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfSellerId: Int = getColumnIndexOrThrow(_stmt, "sellerId")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfCategory: Int = getColumnIndexOrThrow(_stmt, "category")
        val _columnIndexOfPrice: Int = getColumnIndexOrThrow(_stmt, "price")
        val _columnIndexOfQuantity: Int = getColumnIndexOrThrow(_stmt, "quantity")
        val _columnIndexOfUnit: Int = getColumnIndexOrThrow(_stmt, "unit")
        val _columnIndexOfLocation: Int = getColumnIndexOrThrow(_stmt, "location")
        val _columnIndexOfLatitude: Int = getColumnIndexOrThrow(_stmt, "latitude")
        val _columnIndexOfLongitude: Int = getColumnIndexOrThrow(_stmt, "longitude")
        val _columnIndexOfImageUrls: Int = getColumnIndexOrThrow(_stmt, "imageUrls")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfCondition: Int = getColumnIndexOrThrow(_stmt, "condition")
        val _columnIndexOfHarvestDate: Int = getColumnIndexOrThrow(_stmt, "harvestDate")
        val _columnIndexOfExpiryDate: Int = getColumnIndexOrThrow(_stmt, "expiryDate")
        val _columnIndexOfBirthDate: Int = getColumnIndexOrThrow(_stmt, "birthDate")
        val _columnIndexOfVaccinationRecordsJson: Int = getColumnIndexOrThrow(_stmt,
            "vaccinationRecordsJson")
        val _columnIndexOfWeightGrams: Int = getColumnIndexOrThrow(_stmt, "weightGrams")
        val _columnIndexOfHeightCm: Int = getColumnIndexOrThrow(_stmt, "heightCm")
        val _columnIndexOfGender: Int = getColumnIndexOrThrow(_stmt, "gender")
        val _columnIndexOfColor: Int = getColumnIndexOrThrow(_stmt, "color")
        val _columnIndexOfBreed: Int = getColumnIndexOrThrow(_stmt, "breed")
        val _columnIndexOfRaisingPurpose: Int = getColumnIndexOrThrow(_stmt, "raisingPurpose")
        val _columnIndexOfHealthStatus: Int = getColumnIndexOrThrow(_stmt, "healthStatus")
        val _columnIndexOfBirdCode: Int = getColumnIndexOrThrow(_stmt, "birdCode")
        val _columnIndexOfColorTag: Int = getColumnIndexOrThrow(_stmt, "colorTag")
        val _columnIndexOfFamilyTreeId: Int = getColumnIndexOrThrow(_stmt, "familyTreeId")
        val _columnIndexOfSourceAssetId: Int = getColumnIndexOrThrow(_stmt, "sourceAssetId")
        val _columnIndexOfParentIdsJson: Int = getColumnIndexOrThrow(_stmt, "parentIdsJson")
        val _columnIndexOfBreedingStatus: Int = getColumnIndexOrThrow(_stmt, "breedingStatus")
        val _columnIndexOfTransferHistoryJson: Int = getColumnIndexOrThrow(_stmt,
            "transferHistoryJson")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfLastModifiedAt: Int = getColumnIndexOrThrow(_stmt, "lastModifiedAt")
        val _columnIndexOfIsDeleted: Int = getColumnIndexOrThrow(_stmt, "isDeleted")
        val _columnIndexOfDeletedAt: Int = getColumnIndexOrThrow(_stmt, "deletedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfStage: Int = getColumnIndexOrThrow(_stmt, "stage")
        val _columnIndexOfLifecycleStatus: Int = getColumnIndexOrThrow(_stmt, "lifecycleStatus")
        val _columnIndexOfParentMaleId: Int = getColumnIndexOrThrow(_stmt, "parentMaleId")
        val _columnIndexOfParentFemaleId: Int = getColumnIndexOrThrow(_stmt, "parentFemaleId")
        val _columnIndexOfAgeWeeks: Int = getColumnIndexOrThrow(_stmt, "ageWeeks")
        val _columnIndexOfLastStageTransitionAt: Int = getColumnIndexOrThrow(_stmt,
            "lastStageTransitionAt")
        val _columnIndexOfBreederEligibleAt: Int = getColumnIndexOrThrow(_stmt, "breederEligibleAt")
        val _columnIndexOfIsBatch: Int = getColumnIndexOrThrow(_stmt, "isBatch")
        val _columnIndexOfBatchId: Int = getColumnIndexOrThrow(_stmt, "batchId")
        val _columnIndexOfSplitAt: Int = getColumnIndexOrThrow(_stmt, "splitAt")
        val _columnIndexOfSplitIntoIds: Int = getColumnIndexOrThrow(_stmt, "splitIntoIds")
        val _columnIndexOfDocumentUrls: Int = getColumnIndexOrThrow(_stmt, "documentUrls")
        val _columnIndexOfQrCodeUrl: Int = getColumnIndexOrThrow(_stmt, "qrCodeUrl")
        val _columnIndexOfCustomStatus: Int = getColumnIndexOrThrow(_stmt, "customStatus")
        val _columnIndexOfDebug: Int = getColumnIndexOrThrow(_stmt, "debug")
        val _columnIndexOfDeliveryOptions: Int = getColumnIndexOrThrow(_stmt, "deliveryOptions")
        val _columnIndexOfDeliveryCost: Int = getColumnIndexOrThrow(_stmt, "deliveryCost")
        val _columnIndexOfLeadTimeDays: Int = getColumnIndexOrThrow(_stmt, "leadTimeDays")
        val _columnIndexOfMotherId: Int = getColumnIndexOrThrow(_stmt, "motherId")
        val _columnIndexOfIsBreedingUnit: Int = getColumnIndexOrThrow(_stmt, "isBreedingUnit")
        val _columnIndexOfEggsCollectedToday: Int = getColumnIndexOrThrow(_stmt,
            "eggsCollectedToday")
        val _columnIndexOfLastEggLogDate: Int = getColumnIndexOrThrow(_stmt, "lastEggLogDate")
        val _columnIndexOfReadyForSale: Int = getColumnIndexOrThrow(_stmt, "readyForSale")
        val _columnIndexOfTargetWeight: Int = getColumnIndexOrThrow(_stmt, "targetWeight")
        val _columnIndexOfIsShowcased: Int = getColumnIndexOrThrow(_stmt, "isShowcased")
        val _columnIndexOfExternalVideoUrl: Int = getColumnIndexOrThrow(_stmt, "externalVideoUrl")
        val _columnIndexOfRecordsLockedAt: Int = getColumnIndexOrThrow(_stmt, "recordsLockedAt")
        val _columnIndexOfAutoLockAfterDays: Int = getColumnIndexOrThrow(_stmt, "autoLockAfterDays")
        val _columnIndexOfLineageHistoryJson: Int = getColumnIndexOrThrow(_stmt,
            "lineageHistoryJson")
        val _columnIndexOfEditCount: Int = getColumnIndexOrThrow(_stmt, "editCount")
        val _columnIndexOfLastEditedBy: Int = getColumnIndexOrThrow(_stmt, "lastEditedBy")
        val _columnIndexOfAdminFlagged: Int = getColumnIndexOrThrow(_stmt, "adminFlagged")
        val _columnIndexOfModerationNote: Int = getColumnIndexOrThrow(_stmt, "moderationNote")
        val _columnIndexOfMetadataJson: Int = getColumnIndexOrThrow(_stmt, "metadataJson")
        val _result: MutableList<ProductEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: ProductEntity
          val _tmpProductId: String
          _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          val _tmpSellerId: String
          _tmpSellerId = _stmt.getText(_columnIndexOfSellerId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpDescription: String
          _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          val _tmpCategory: String
          _tmpCategory = _stmt.getText(_columnIndexOfCategory)
          val _tmpPrice: Double
          _tmpPrice = _stmt.getDouble(_columnIndexOfPrice)
          val _tmpQuantity: Double
          _tmpQuantity = _stmt.getDouble(_columnIndexOfQuantity)
          val _tmpUnit: String
          _tmpUnit = _stmt.getText(_columnIndexOfUnit)
          val _tmpLocation: String
          _tmpLocation = _stmt.getText(_columnIndexOfLocation)
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
          val _tmpImageUrls: List<String>
          val _tmp: String?
          if (_stmt.isNull(_columnIndexOfImageUrls)) {
            _tmp = null
          } else {
            _tmp = _stmt.getText(_columnIndexOfImageUrls)
          }
          val _tmp_1: List<String>? = AppDatabase.Converters.toStringList(_tmp)
          if (_tmp_1 == null) {
            error("Expected NON-NULL 'kotlin.collections.List<kotlin.String>', but it was NULL.")
          } else {
            _tmpImageUrls = _tmp_1
          }
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpCondition: String?
          if (_stmt.isNull(_columnIndexOfCondition)) {
            _tmpCondition = null
          } else {
            _tmpCondition = _stmt.getText(_columnIndexOfCondition)
          }
          val _tmpHarvestDate: Long?
          if (_stmt.isNull(_columnIndexOfHarvestDate)) {
            _tmpHarvestDate = null
          } else {
            _tmpHarvestDate = _stmt.getLong(_columnIndexOfHarvestDate)
          }
          val _tmpExpiryDate: Long?
          if (_stmt.isNull(_columnIndexOfExpiryDate)) {
            _tmpExpiryDate = null
          } else {
            _tmpExpiryDate = _stmt.getLong(_columnIndexOfExpiryDate)
          }
          val _tmpBirthDate: Long?
          if (_stmt.isNull(_columnIndexOfBirthDate)) {
            _tmpBirthDate = null
          } else {
            _tmpBirthDate = _stmt.getLong(_columnIndexOfBirthDate)
          }
          val _tmpVaccinationRecordsJson: String?
          if (_stmt.isNull(_columnIndexOfVaccinationRecordsJson)) {
            _tmpVaccinationRecordsJson = null
          } else {
            _tmpVaccinationRecordsJson = _stmt.getText(_columnIndexOfVaccinationRecordsJson)
          }
          val _tmpWeightGrams: Double?
          if (_stmt.isNull(_columnIndexOfWeightGrams)) {
            _tmpWeightGrams = null
          } else {
            _tmpWeightGrams = _stmt.getDouble(_columnIndexOfWeightGrams)
          }
          val _tmpHeightCm: Double?
          if (_stmt.isNull(_columnIndexOfHeightCm)) {
            _tmpHeightCm = null
          } else {
            _tmpHeightCm = _stmt.getDouble(_columnIndexOfHeightCm)
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
          val _tmpBreed: String?
          if (_stmt.isNull(_columnIndexOfBreed)) {
            _tmpBreed = null
          } else {
            _tmpBreed = _stmt.getText(_columnIndexOfBreed)
          }
          val _tmpRaisingPurpose: String?
          if (_stmt.isNull(_columnIndexOfRaisingPurpose)) {
            _tmpRaisingPurpose = null
          } else {
            _tmpRaisingPurpose = _stmt.getText(_columnIndexOfRaisingPurpose)
          }
          val _tmpHealthStatus: String?
          if (_stmt.isNull(_columnIndexOfHealthStatus)) {
            _tmpHealthStatus = null
          } else {
            _tmpHealthStatus = _stmt.getText(_columnIndexOfHealthStatus)
          }
          val _tmpBirdCode: String?
          if (_stmt.isNull(_columnIndexOfBirdCode)) {
            _tmpBirdCode = null
          } else {
            _tmpBirdCode = _stmt.getText(_columnIndexOfBirdCode)
          }
          val _tmpColorTag: String?
          if (_stmt.isNull(_columnIndexOfColorTag)) {
            _tmpColorTag = null
          } else {
            _tmpColorTag = _stmt.getText(_columnIndexOfColorTag)
          }
          val _tmpFamilyTreeId: String?
          if (_stmt.isNull(_columnIndexOfFamilyTreeId)) {
            _tmpFamilyTreeId = null
          } else {
            _tmpFamilyTreeId = _stmt.getText(_columnIndexOfFamilyTreeId)
          }
          val _tmpSourceAssetId: String?
          if (_stmt.isNull(_columnIndexOfSourceAssetId)) {
            _tmpSourceAssetId = null
          } else {
            _tmpSourceAssetId = _stmt.getText(_columnIndexOfSourceAssetId)
          }
          val _tmpParentIdsJson: String?
          if (_stmt.isNull(_columnIndexOfParentIdsJson)) {
            _tmpParentIdsJson = null
          } else {
            _tmpParentIdsJson = _stmt.getText(_columnIndexOfParentIdsJson)
          }
          val _tmpBreedingStatus: String?
          if (_stmt.isNull(_columnIndexOfBreedingStatus)) {
            _tmpBreedingStatus = null
          } else {
            _tmpBreedingStatus = _stmt.getText(_columnIndexOfBreedingStatus)
          }
          val _tmpTransferHistoryJson: String?
          if (_stmt.isNull(_columnIndexOfTransferHistoryJson)) {
            _tmpTransferHistoryJson = null
          } else {
            _tmpTransferHistoryJson = _stmt.getText(_columnIndexOfTransferHistoryJson)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpLastModifiedAt: Long
          _tmpLastModifiedAt = _stmt.getLong(_columnIndexOfLastModifiedAt)
          val _tmpIsDeleted: Boolean
          val _tmp_2: Int
          _tmp_2 = _stmt.getLong(_columnIndexOfIsDeleted).toInt()
          _tmpIsDeleted = _tmp_2 != 0
          val _tmpDeletedAt: Long?
          if (_stmt.isNull(_columnIndexOfDeletedAt)) {
            _tmpDeletedAt = null
          } else {
            _tmpDeletedAt = _stmt.getLong(_columnIndexOfDeletedAt)
          }
          val _tmpDirty: Boolean
          val _tmp_3: Int
          _tmp_3 = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp_3 != 0
          val _tmpStage: LifecycleStage?
          val _tmp_4: String?
          if (_stmt.isNull(_columnIndexOfStage)) {
            _tmp_4 = null
          } else {
            _tmp_4 = _stmt.getText(_columnIndexOfStage)
          }
          _tmpStage = AppDatabase.Converters.toLifecycleStage(_tmp_4)
          val _tmpLifecycleStatus: String?
          if (_stmt.isNull(_columnIndexOfLifecycleStatus)) {
            _tmpLifecycleStatus = null
          } else {
            _tmpLifecycleStatus = _stmt.getText(_columnIndexOfLifecycleStatus)
          }
          val _tmpParentMaleId: String?
          if (_stmt.isNull(_columnIndexOfParentMaleId)) {
            _tmpParentMaleId = null
          } else {
            _tmpParentMaleId = _stmt.getText(_columnIndexOfParentMaleId)
          }
          val _tmpParentFemaleId: String?
          if (_stmt.isNull(_columnIndexOfParentFemaleId)) {
            _tmpParentFemaleId = null
          } else {
            _tmpParentFemaleId = _stmt.getText(_columnIndexOfParentFemaleId)
          }
          val _tmpAgeWeeks: Int?
          if (_stmt.isNull(_columnIndexOfAgeWeeks)) {
            _tmpAgeWeeks = null
          } else {
            _tmpAgeWeeks = _stmt.getLong(_columnIndexOfAgeWeeks).toInt()
          }
          val _tmpLastStageTransitionAt: Long?
          if (_stmt.isNull(_columnIndexOfLastStageTransitionAt)) {
            _tmpLastStageTransitionAt = null
          } else {
            _tmpLastStageTransitionAt = _stmt.getLong(_columnIndexOfLastStageTransitionAt)
          }
          val _tmpBreederEligibleAt: Long?
          if (_stmt.isNull(_columnIndexOfBreederEligibleAt)) {
            _tmpBreederEligibleAt = null
          } else {
            _tmpBreederEligibleAt = _stmt.getLong(_columnIndexOfBreederEligibleAt)
          }
          val _tmpIsBatch: Boolean?
          val _tmp_5: Int?
          if (_stmt.isNull(_columnIndexOfIsBatch)) {
            _tmp_5 = null
          } else {
            _tmp_5 = _stmt.getLong(_columnIndexOfIsBatch).toInt()
          }
          _tmpIsBatch = _tmp_5?.let { it != 0 }
          val _tmpBatchId: String?
          if (_stmt.isNull(_columnIndexOfBatchId)) {
            _tmpBatchId = null
          } else {
            _tmpBatchId = _stmt.getText(_columnIndexOfBatchId)
          }
          val _tmpSplitAt: Long?
          if (_stmt.isNull(_columnIndexOfSplitAt)) {
            _tmpSplitAt = null
          } else {
            _tmpSplitAt = _stmt.getLong(_columnIndexOfSplitAt)
          }
          val _tmpSplitIntoIds: String?
          if (_stmt.isNull(_columnIndexOfSplitIntoIds)) {
            _tmpSplitIntoIds = null
          } else {
            _tmpSplitIntoIds = _stmt.getText(_columnIndexOfSplitIntoIds)
          }
          val _tmpDocumentUrls: List<String>
          val _tmp_6: String?
          if (_stmt.isNull(_columnIndexOfDocumentUrls)) {
            _tmp_6 = null
          } else {
            _tmp_6 = _stmt.getText(_columnIndexOfDocumentUrls)
          }
          val _tmp_7: List<String>? = AppDatabase.Converters.toStringList(_tmp_6)
          if (_tmp_7 == null) {
            error("Expected NON-NULL 'kotlin.collections.List<kotlin.String>', but it was NULL.")
          } else {
            _tmpDocumentUrls = _tmp_7
          }
          val _tmpQrCodeUrl: String?
          if (_stmt.isNull(_columnIndexOfQrCodeUrl)) {
            _tmpQrCodeUrl = null
          } else {
            _tmpQrCodeUrl = _stmt.getText(_columnIndexOfQrCodeUrl)
          }
          val _tmpCustomStatus: String?
          if (_stmt.isNull(_columnIndexOfCustomStatus)) {
            _tmpCustomStatus = null
          } else {
            _tmpCustomStatus = _stmt.getText(_columnIndexOfCustomStatus)
          }
          val _tmpDebug: Boolean
          val _tmp_8: Int
          _tmp_8 = _stmt.getLong(_columnIndexOfDebug).toInt()
          _tmpDebug = _tmp_8 != 0
          val _tmpDeliveryOptions: List<String>
          val _tmp_9: String?
          if (_stmt.isNull(_columnIndexOfDeliveryOptions)) {
            _tmp_9 = null
          } else {
            _tmp_9 = _stmt.getText(_columnIndexOfDeliveryOptions)
          }
          val _tmp_10: List<String>? = AppDatabase.Converters.toStringList(_tmp_9)
          if (_tmp_10 == null) {
            error("Expected NON-NULL 'kotlin.collections.List<kotlin.String>', but it was NULL.")
          } else {
            _tmpDeliveryOptions = _tmp_10
          }
          val _tmpDeliveryCost: Double?
          if (_stmt.isNull(_columnIndexOfDeliveryCost)) {
            _tmpDeliveryCost = null
          } else {
            _tmpDeliveryCost = _stmt.getDouble(_columnIndexOfDeliveryCost)
          }
          val _tmpLeadTimeDays: Int?
          if (_stmt.isNull(_columnIndexOfLeadTimeDays)) {
            _tmpLeadTimeDays = null
          } else {
            _tmpLeadTimeDays = _stmt.getLong(_columnIndexOfLeadTimeDays).toInt()
          }
          val _tmpMotherId: String?
          if (_stmt.isNull(_columnIndexOfMotherId)) {
            _tmpMotherId = null
          } else {
            _tmpMotherId = _stmt.getText(_columnIndexOfMotherId)
          }
          val _tmpIsBreedingUnit: Boolean
          val _tmp_11: Int
          _tmp_11 = _stmt.getLong(_columnIndexOfIsBreedingUnit).toInt()
          _tmpIsBreedingUnit = _tmp_11 != 0
          val _tmpEggsCollectedToday: Int
          _tmpEggsCollectedToday = _stmt.getLong(_columnIndexOfEggsCollectedToday).toInt()
          val _tmpLastEggLogDate: Long?
          if (_stmt.isNull(_columnIndexOfLastEggLogDate)) {
            _tmpLastEggLogDate = null
          } else {
            _tmpLastEggLogDate = _stmt.getLong(_columnIndexOfLastEggLogDate)
          }
          val _tmpReadyForSale: Boolean
          val _tmp_12: Int
          _tmp_12 = _stmt.getLong(_columnIndexOfReadyForSale).toInt()
          _tmpReadyForSale = _tmp_12 != 0
          val _tmpTargetWeight: Double?
          if (_stmt.isNull(_columnIndexOfTargetWeight)) {
            _tmpTargetWeight = null
          } else {
            _tmpTargetWeight = _stmt.getDouble(_columnIndexOfTargetWeight)
          }
          val _tmpIsShowcased: Boolean
          val _tmp_13: Int
          _tmp_13 = _stmt.getLong(_columnIndexOfIsShowcased).toInt()
          _tmpIsShowcased = _tmp_13 != 0
          val _tmpExternalVideoUrl: String?
          if (_stmt.isNull(_columnIndexOfExternalVideoUrl)) {
            _tmpExternalVideoUrl = null
          } else {
            _tmpExternalVideoUrl = _stmt.getText(_columnIndexOfExternalVideoUrl)
          }
          val _tmpRecordsLockedAt: Long?
          if (_stmt.isNull(_columnIndexOfRecordsLockedAt)) {
            _tmpRecordsLockedAt = null
          } else {
            _tmpRecordsLockedAt = _stmt.getLong(_columnIndexOfRecordsLockedAt)
          }
          val _tmpAutoLockAfterDays: Int
          _tmpAutoLockAfterDays = _stmt.getLong(_columnIndexOfAutoLockAfterDays).toInt()
          val _tmpLineageHistoryJson: String?
          if (_stmt.isNull(_columnIndexOfLineageHistoryJson)) {
            _tmpLineageHistoryJson = null
          } else {
            _tmpLineageHistoryJson = _stmt.getText(_columnIndexOfLineageHistoryJson)
          }
          val _tmpEditCount: Int
          _tmpEditCount = _stmt.getLong(_columnIndexOfEditCount).toInt()
          val _tmpLastEditedBy: String?
          if (_stmt.isNull(_columnIndexOfLastEditedBy)) {
            _tmpLastEditedBy = null
          } else {
            _tmpLastEditedBy = _stmt.getText(_columnIndexOfLastEditedBy)
          }
          val _tmpAdminFlagged: Boolean
          val _tmp_14: Int
          _tmp_14 = _stmt.getLong(_columnIndexOfAdminFlagged).toInt()
          _tmpAdminFlagged = _tmp_14 != 0
          val _tmpModerationNote: String?
          if (_stmt.isNull(_columnIndexOfModerationNote)) {
            _tmpModerationNote = null
          } else {
            _tmpModerationNote = _stmt.getText(_columnIndexOfModerationNote)
          }
          val _tmpMetadataJson: String
          _tmpMetadataJson = _stmt.getText(_columnIndexOfMetadataJson)
          _item =
              ProductEntity(_tmpProductId,_tmpSellerId,_tmpName,_tmpDescription,_tmpCategory,_tmpPrice,_tmpQuantity,_tmpUnit,_tmpLocation,_tmpLatitude,_tmpLongitude,_tmpImageUrls,_tmpStatus,_tmpCondition,_tmpHarvestDate,_tmpExpiryDate,_tmpBirthDate,_tmpVaccinationRecordsJson,_tmpWeightGrams,_tmpHeightCm,_tmpGender,_tmpColor,_tmpBreed,_tmpRaisingPurpose,_tmpHealthStatus,_tmpBirdCode,_tmpColorTag,_tmpFamilyTreeId,_tmpSourceAssetId,_tmpParentIdsJson,_tmpBreedingStatus,_tmpTransferHistoryJson,_tmpCreatedAt,_tmpUpdatedAt,_tmpLastModifiedAt,_tmpIsDeleted,_tmpDeletedAt,_tmpDirty,_tmpStage,_tmpLifecycleStatus,_tmpParentMaleId,_tmpParentFemaleId,_tmpAgeWeeks,_tmpLastStageTransitionAt,_tmpBreederEligibleAt,_tmpIsBatch,_tmpBatchId,_tmpSplitAt,_tmpSplitIntoIds,_tmpDocumentUrls,_tmpQrCodeUrl,_tmpCustomStatus,_tmpDebug,_tmpDeliveryOptions,_tmpDeliveryCost,_tmpLeadTimeDays,_tmpMotherId,_tmpIsBreedingUnit,_tmpEggsCollectedToday,_tmpLastEggLogDate,_tmpReadyForSale,_tmpTargetWeight,_tmpIsShowcased,_tmpExternalVideoUrl,_tmpRecordsLockedAt,_tmpAutoLockAfterDays,_tmpLineageHistoryJson,_tmpEditCount,_tmpLastEditedBy,_tmpAdminFlagged,_tmpModerationNote,_tmpMetadataJson)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getAllProductsSnapshot(): List<ProductEntity> {
    val _sql: String = "SELECT * FROM products ORDER BY createdAt DESC"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfSellerId: Int = getColumnIndexOrThrow(_stmt, "sellerId")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfCategory: Int = getColumnIndexOrThrow(_stmt, "category")
        val _columnIndexOfPrice: Int = getColumnIndexOrThrow(_stmt, "price")
        val _columnIndexOfQuantity: Int = getColumnIndexOrThrow(_stmt, "quantity")
        val _columnIndexOfUnit: Int = getColumnIndexOrThrow(_stmt, "unit")
        val _columnIndexOfLocation: Int = getColumnIndexOrThrow(_stmt, "location")
        val _columnIndexOfLatitude: Int = getColumnIndexOrThrow(_stmt, "latitude")
        val _columnIndexOfLongitude: Int = getColumnIndexOrThrow(_stmt, "longitude")
        val _columnIndexOfImageUrls: Int = getColumnIndexOrThrow(_stmt, "imageUrls")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfCondition: Int = getColumnIndexOrThrow(_stmt, "condition")
        val _columnIndexOfHarvestDate: Int = getColumnIndexOrThrow(_stmt, "harvestDate")
        val _columnIndexOfExpiryDate: Int = getColumnIndexOrThrow(_stmt, "expiryDate")
        val _columnIndexOfBirthDate: Int = getColumnIndexOrThrow(_stmt, "birthDate")
        val _columnIndexOfVaccinationRecordsJson: Int = getColumnIndexOrThrow(_stmt,
            "vaccinationRecordsJson")
        val _columnIndexOfWeightGrams: Int = getColumnIndexOrThrow(_stmt, "weightGrams")
        val _columnIndexOfHeightCm: Int = getColumnIndexOrThrow(_stmt, "heightCm")
        val _columnIndexOfGender: Int = getColumnIndexOrThrow(_stmt, "gender")
        val _columnIndexOfColor: Int = getColumnIndexOrThrow(_stmt, "color")
        val _columnIndexOfBreed: Int = getColumnIndexOrThrow(_stmt, "breed")
        val _columnIndexOfRaisingPurpose: Int = getColumnIndexOrThrow(_stmt, "raisingPurpose")
        val _columnIndexOfHealthStatus: Int = getColumnIndexOrThrow(_stmt, "healthStatus")
        val _columnIndexOfBirdCode: Int = getColumnIndexOrThrow(_stmt, "birdCode")
        val _columnIndexOfColorTag: Int = getColumnIndexOrThrow(_stmt, "colorTag")
        val _columnIndexOfFamilyTreeId: Int = getColumnIndexOrThrow(_stmt, "familyTreeId")
        val _columnIndexOfSourceAssetId: Int = getColumnIndexOrThrow(_stmt, "sourceAssetId")
        val _columnIndexOfParentIdsJson: Int = getColumnIndexOrThrow(_stmt, "parentIdsJson")
        val _columnIndexOfBreedingStatus: Int = getColumnIndexOrThrow(_stmt, "breedingStatus")
        val _columnIndexOfTransferHistoryJson: Int = getColumnIndexOrThrow(_stmt,
            "transferHistoryJson")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfLastModifiedAt: Int = getColumnIndexOrThrow(_stmt, "lastModifiedAt")
        val _columnIndexOfIsDeleted: Int = getColumnIndexOrThrow(_stmt, "isDeleted")
        val _columnIndexOfDeletedAt: Int = getColumnIndexOrThrow(_stmt, "deletedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfStage: Int = getColumnIndexOrThrow(_stmt, "stage")
        val _columnIndexOfLifecycleStatus: Int = getColumnIndexOrThrow(_stmt, "lifecycleStatus")
        val _columnIndexOfParentMaleId: Int = getColumnIndexOrThrow(_stmt, "parentMaleId")
        val _columnIndexOfParentFemaleId: Int = getColumnIndexOrThrow(_stmt, "parentFemaleId")
        val _columnIndexOfAgeWeeks: Int = getColumnIndexOrThrow(_stmt, "ageWeeks")
        val _columnIndexOfLastStageTransitionAt: Int = getColumnIndexOrThrow(_stmt,
            "lastStageTransitionAt")
        val _columnIndexOfBreederEligibleAt: Int = getColumnIndexOrThrow(_stmt, "breederEligibleAt")
        val _columnIndexOfIsBatch: Int = getColumnIndexOrThrow(_stmt, "isBatch")
        val _columnIndexOfBatchId: Int = getColumnIndexOrThrow(_stmt, "batchId")
        val _columnIndexOfSplitAt: Int = getColumnIndexOrThrow(_stmt, "splitAt")
        val _columnIndexOfSplitIntoIds: Int = getColumnIndexOrThrow(_stmt, "splitIntoIds")
        val _columnIndexOfDocumentUrls: Int = getColumnIndexOrThrow(_stmt, "documentUrls")
        val _columnIndexOfQrCodeUrl: Int = getColumnIndexOrThrow(_stmt, "qrCodeUrl")
        val _columnIndexOfCustomStatus: Int = getColumnIndexOrThrow(_stmt, "customStatus")
        val _columnIndexOfDebug: Int = getColumnIndexOrThrow(_stmt, "debug")
        val _columnIndexOfDeliveryOptions: Int = getColumnIndexOrThrow(_stmt, "deliveryOptions")
        val _columnIndexOfDeliveryCost: Int = getColumnIndexOrThrow(_stmt, "deliveryCost")
        val _columnIndexOfLeadTimeDays: Int = getColumnIndexOrThrow(_stmt, "leadTimeDays")
        val _columnIndexOfMotherId: Int = getColumnIndexOrThrow(_stmt, "motherId")
        val _columnIndexOfIsBreedingUnit: Int = getColumnIndexOrThrow(_stmt, "isBreedingUnit")
        val _columnIndexOfEggsCollectedToday: Int = getColumnIndexOrThrow(_stmt,
            "eggsCollectedToday")
        val _columnIndexOfLastEggLogDate: Int = getColumnIndexOrThrow(_stmt, "lastEggLogDate")
        val _columnIndexOfReadyForSale: Int = getColumnIndexOrThrow(_stmt, "readyForSale")
        val _columnIndexOfTargetWeight: Int = getColumnIndexOrThrow(_stmt, "targetWeight")
        val _columnIndexOfIsShowcased: Int = getColumnIndexOrThrow(_stmt, "isShowcased")
        val _columnIndexOfExternalVideoUrl: Int = getColumnIndexOrThrow(_stmt, "externalVideoUrl")
        val _columnIndexOfRecordsLockedAt: Int = getColumnIndexOrThrow(_stmt, "recordsLockedAt")
        val _columnIndexOfAutoLockAfterDays: Int = getColumnIndexOrThrow(_stmt, "autoLockAfterDays")
        val _columnIndexOfLineageHistoryJson: Int = getColumnIndexOrThrow(_stmt,
            "lineageHistoryJson")
        val _columnIndexOfEditCount: Int = getColumnIndexOrThrow(_stmt, "editCount")
        val _columnIndexOfLastEditedBy: Int = getColumnIndexOrThrow(_stmt, "lastEditedBy")
        val _columnIndexOfAdminFlagged: Int = getColumnIndexOrThrow(_stmt, "adminFlagged")
        val _columnIndexOfModerationNote: Int = getColumnIndexOrThrow(_stmt, "moderationNote")
        val _columnIndexOfMetadataJson: Int = getColumnIndexOrThrow(_stmt, "metadataJson")
        val _result: MutableList<ProductEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: ProductEntity
          val _tmpProductId: String
          _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          val _tmpSellerId: String
          _tmpSellerId = _stmt.getText(_columnIndexOfSellerId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpDescription: String
          _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          val _tmpCategory: String
          _tmpCategory = _stmt.getText(_columnIndexOfCategory)
          val _tmpPrice: Double
          _tmpPrice = _stmt.getDouble(_columnIndexOfPrice)
          val _tmpQuantity: Double
          _tmpQuantity = _stmt.getDouble(_columnIndexOfQuantity)
          val _tmpUnit: String
          _tmpUnit = _stmt.getText(_columnIndexOfUnit)
          val _tmpLocation: String
          _tmpLocation = _stmt.getText(_columnIndexOfLocation)
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
          val _tmpImageUrls: List<String>
          val _tmp: String?
          if (_stmt.isNull(_columnIndexOfImageUrls)) {
            _tmp = null
          } else {
            _tmp = _stmt.getText(_columnIndexOfImageUrls)
          }
          val _tmp_1: List<String>? = AppDatabase.Converters.toStringList(_tmp)
          if (_tmp_1 == null) {
            error("Expected NON-NULL 'kotlin.collections.List<kotlin.String>', but it was NULL.")
          } else {
            _tmpImageUrls = _tmp_1
          }
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpCondition: String?
          if (_stmt.isNull(_columnIndexOfCondition)) {
            _tmpCondition = null
          } else {
            _tmpCondition = _stmt.getText(_columnIndexOfCondition)
          }
          val _tmpHarvestDate: Long?
          if (_stmt.isNull(_columnIndexOfHarvestDate)) {
            _tmpHarvestDate = null
          } else {
            _tmpHarvestDate = _stmt.getLong(_columnIndexOfHarvestDate)
          }
          val _tmpExpiryDate: Long?
          if (_stmt.isNull(_columnIndexOfExpiryDate)) {
            _tmpExpiryDate = null
          } else {
            _tmpExpiryDate = _stmt.getLong(_columnIndexOfExpiryDate)
          }
          val _tmpBirthDate: Long?
          if (_stmt.isNull(_columnIndexOfBirthDate)) {
            _tmpBirthDate = null
          } else {
            _tmpBirthDate = _stmt.getLong(_columnIndexOfBirthDate)
          }
          val _tmpVaccinationRecordsJson: String?
          if (_stmt.isNull(_columnIndexOfVaccinationRecordsJson)) {
            _tmpVaccinationRecordsJson = null
          } else {
            _tmpVaccinationRecordsJson = _stmt.getText(_columnIndexOfVaccinationRecordsJson)
          }
          val _tmpWeightGrams: Double?
          if (_stmt.isNull(_columnIndexOfWeightGrams)) {
            _tmpWeightGrams = null
          } else {
            _tmpWeightGrams = _stmt.getDouble(_columnIndexOfWeightGrams)
          }
          val _tmpHeightCm: Double?
          if (_stmt.isNull(_columnIndexOfHeightCm)) {
            _tmpHeightCm = null
          } else {
            _tmpHeightCm = _stmt.getDouble(_columnIndexOfHeightCm)
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
          val _tmpBreed: String?
          if (_stmt.isNull(_columnIndexOfBreed)) {
            _tmpBreed = null
          } else {
            _tmpBreed = _stmt.getText(_columnIndexOfBreed)
          }
          val _tmpRaisingPurpose: String?
          if (_stmt.isNull(_columnIndexOfRaisingPurpose)) {
            _tmpRaisingPurpose = null
          } else {
            _tmpRaisingPurpose = _stmt.getText(_columnIndexOfRaisingPurpose)
          }
          val _tmpHealthStatus: String?
          if (_stmt.isNull(_columnIndexOfHealthStatus)) {
            _tmpHealthStatus = null
          } else {
            _tmpHealthStatus = _stmt.getText(_columnIndexOfHealthStatus)
          }
          val _tmpBirdCode: String?
          if (_stmt.isNull(_columnIndexOfBirdCode)) {
            _tmpBirdCode = null
          } else {
            _tmpBirdCode = _stmt.getText(_columnIndexOfBirdCode)
          }
          val _tmpColorTag: String?
          if (_stmt.isNull(_columnIndexOfColorTag)) {
            _tmpColorTag = null
          } else {
            _tmpColorTag = _stmt.getText(_columnIndexOfColorTag)
          }
          val _tmpFamilyTreeId: String?
          if (_stmt.isNull(_columnIndexOfFamilyTreeId)) {
            _tmpFamilyTreeId = null
          } else {
            _tmpFamilyTreeId = _stmt.getText(_columnIndexOfFamilyTreeId)
          }
          val _tmpSourceAssetId: String?
          if (_stmt.isNull(_columnIndexOfSourceAssetId)) {
            _tmpSourceAssetId = null
          } else {
            _tmpSourceAssetId = _stmt.getText(_columnIndexOfSourceAssetId)
          }
          val _tmpParentIdsJson: String?
          if (_stmt.isNull(_columnIndexOfParentIdsJson)) {
            _tmpParentIdsJson = null
          } else {
            _tmpParentIdsJson = _stmt.getText(_columnIndexOfParentIdsJson)
          }
          val _tmpBreedingStatus: String?
          if (_stmt.isNull(_columnIndexOfBreedingStatus)) {
            _tmpBreedingStatus = null
          } else {
            _tmpBreedingStatus = _stmt.getText(_columnIndexOfBreedingStatus)
          }
          val _tmpTransferHistoryJson: String?
          if (_stmt.isNull(_columnIndexOfTransferHistoryJson)) {
            _tmpTransferHistoryJson = null
          } else {
            _tmpTransferHistoryJson = _stmt.getText(_columnIndexOfTransferHistoryJson)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpLastModifiedAt: Long
          _tmpLastModifiedAt = _stmt.getLong(_columnIndexOfLastModifiedAt)
          val _tmpIsDeleted: Boolean
          val _tmp_2: Int
          _tmp_2 = _stmt.getLong(_columnIndexOfIsDeleted).toInt()
          _tmpIsDeleted = _tmp_2 != 0
          val _tmpDeletedAt: Long?
          if (_stmt.isNull(_columnIndexOfDeletedAt)) {
            _tmpDeletedAt = null
          } else {
            _tmpDeletedAt = _stmt.getLong(_columnIndexOfDeletedAt)
          }
          val _tmpDirty: Boolean
          val _tmp_3: Int
          _tmp_3 = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp_3 != 0
          val _tmpStage: LifecycleStage?
          val _tmp_4: String?
          if (_stmt.isNull(_columnIndexOfStage)) {
            _tmp_4 = null
          } else {
            _tmp_4 = _stmt.getText(_columnIndexOfStage)
          }
          _tmpStage = AppDatabase.Converters.toLifecycleStage(_tmp_4)
          val _tmpLifecycleStatus: String?
          if (_stmt.isNull(_columnIndexOfLifecycleStatus)) {
            _tmpLifecycleStatus = null
          } else {
            _tmpLifecycleStatus = _stmt.getText(_columnIndexOfLifecycleStatus)
          }
          val _tmpParentMaleId: String?
          if (_stmt.isNull(_columnIndexOfParentMaleId)) {
            _tmpParentMaleId = null
          } else {
            _tmpParentMaleId = _stmt.getText(_columnIndexOfParentMaleId)
          }
          val _tmpParentFemaleId: String?
          if (_stmt.isNull(_columnIndexOfParentFemaleId)) {
            _tmpParentFemaleId = null
          } else {
            _tmpParentFemaleId = _stmt.getText(_columnIndexOfParentFemaleId)
          }
          val _tmpAgeWeeks: Int?
          if (_stmt.isNull(_columnIndexOfAgeWeeks)) {
            _tmpAgeWeeks = null
          } else {
            _tmpAgeWeeks = _stmt.getLong(_columnIndexOfAgeWeeks).toInt()
          }
          val _tmpLastStageTransitionAt: Long?
          if (_stmt.isNull(_columnIndexOfLastStageTransitionAt)) {
            _tmpLastStageTransitionAt = null
          } else {
            _tmpLastStageTransitionAt = _stmt.getLong(_columnIndexOfLastStageTransitionAt)
          }
          val _tmpBreederEligibleAt: Long?
          if (_stmt.isNull(_columnIndexOfBreederEligibleAt)) {
            _tmpBreederEligibleAt = null
          } else {
            _tmpBreederEligibleAt = _stmt.getLong(_columnIndexOfBreederEligibleAt)
          }
          val _tmpIsBatch: Boolean?
          val _tmp_5: Int?
          if (_stmt.isNull(_columnIndexOfIsBatch)) {
            _tmp_5 = null
          } else {
            _tmp_5 = _stmt.getLong(_columnIndexOfIsBatch).toInt()
          }
          _tmpIsBatch = _tmp_5?.let { it != 0 }
          val _tmpBatchId: String?
          if (_stmt.isNull(_columnIndexOfBatchId)) {
            _tmpBatchId = null
          } else {
            _tmpBatchId = _stmt.getText(_columnIndexOfBatchId)
          }
          val _tmpSplitAt: Long?
          if (_stmt.isNull(_columnIndexOfSplitAt)) {
            _tmpSplitAt = null
          } else {
            _tmpSplitAt = _stmt.getLong(_columnIndexOfSplitAt)
          }
          val _tmpSplitIntoIds: String?
          if (_stmt.isNull(_columnIndexOfSplitIntoIds)) {
            _tmpSplitIntoIds = null
          } else {
            _tmpSplitIntoIds = _stmt.getText(_columnIndexOfSplitIntoIds)
          }
          val _tmpDocumentUrls: List<String>
          val _tmp_6: String?
          if (_stmt.isNull(_columnIndexOfDocumentUrls)) {
            _tmp_6 = null
          } else {
            _tmp_6 = _stmt.getText(_columnIndexOfDocumentUrls)
          }
          val _tmp_7: List<String>? = AppDatabase.Converters.toStringList(_tmp_6)
          if (_tmp_7 == null) {
            error("Expected NON-NULL 'kotlin.collections.List<kotlin.String>', but it was NULL.")
          } else {
            _tmpDocumentUrls = _tmp_7
          }
          val _tmpQrCodeUrl: String?
          if (_stmt.isNull(_columnIndexOfQrCodeUrl)) {
            _tmpQrCodeUrl = null
          } else {
            _tmpQrCodeUrl = _stmt.getText(_columnIndexOfQrCodeUrl)
          }
          val _tmpCustomStatus: String?
          if (_stmt.isNull(_columnIndexOfCustomStatus)) {
            _tmpCustomStatus = null
          } else {
            _tmpCustomStatus = _stmt.getText(_columnIndexOfCustomStatus)
          }
          val _tmpDebug: Boolean
          val _tmp_8: Int
          _tmp_8 = _stmt.getLong(_columnIndexOfDebug).toInt()
          _tmpDebug = _tmp_8 != 0
          val _tmpDeliveryOptions: List<String>
          val _tmp_9: String?
          if (_stmt.isNull(_columnIndexOfDeliveryOptions)) {
            _tmp_9 = null
          } else {
            _tmp_9 = _stmt.getText(_columnIndexOfDeliveryOptions)
          }
          val _tmp_10: List<String>? = AppDatabase.Converters.toStringList(_tmp_9)
          if (_tmp_10 == null) {
            error("Expected NON-NULL 'kotlin.collections.List<kotlin.String>', but it was NULL.")
          } else {
            _tmpDeliveryOptions = _tmp_10
          }
          val _tmpDeliveryCost: Double?
          if (_stmt.isNull(_columnIndexOfDeliveryCost)) {
            _tmpDeliveryCost = null
          } else {
            _tmpDeliveryCost = _stmt.getDouble(_columnIndexOfDeliveryCost)
          }
          val _tmpLeadTimeDays: Int?
          if (_stmt.isNull(_columnIndexOfLeadTimeDays)) {
            _tmpLeadTimeDays = null
          } else {
            _tmpLeadTimeDays = _stmt.getLong(_columnIndexOfLeadTimeDays).toInt()
          }
          val _tmpMotherId: String?
          if (_stmt.isNull(_columnIndexOfMotherId)) {
            _tmpMotherId = null
          } else {
            _tmpMotherId = _stmt.getText(_columnIndexOfMotherId)
          }
          val _tmpIsBreedingUnit: Boolean
          val _tmp_11: Int
          _tmp_11 = _stmt.getLong(_columnIndexOfIsBreedingUnit).toInt()
          _tmpIsBreedingUnit = _tmp_11 != 0
          val _tmpEggsCollectedToday: Int
          _tmpEggsCollectedToday = _stmt.getLong(_columnIndexOfEggsCollectedToday).toInt()
          val _tmpLastEggLogDate: Long?
          if (_stmt.isNull(_columnIndexOfLastEggLogDate)) {
            _tmpLastEggLogDate = null
          } else {
            _tmpLastEggLogDate = _stmt.getLong(_columnIndexOfLastEggLogDate)
          }
          val _tmpReadyForSale: Boolean
          val _tmp_12: Int
          _tmp_12 = _stmt.getLong(_columnIndexOfReadyForSale).toInt()
          _tmpReadyForSale = _tmp_12 != 0
          val _tmpTargetWeight: Double?
          if (_stmt.isNull(_columnIndexOfTargetWeight)) {
            _tmpTargetWeight = null
          } else {
            _tmpTargetWeight = _stmt.getDouble(_columnIndexOfTargetWeight)
          }
          val _tmpIsShowcased: Boolean
          val _tmp_13: Int
          _tmp_13 = _stmt.getLong(_columnIndexOfIsShowcased).toInt()
          _tmpIsShowcased = _tmp_13 != 0
          val _tmpExternalVideoUrl: String?
          if (_stmt.isNull(_columnIndexOfExternalVideoUrl)) {
            _tmpExternalVideoUrl = null
          } else {
            _tmpExternalVideoUrl = _stmt.getText(_columnIndexOfExternalVideoUrl)
          }
          val _tmpRecordsLockedAt: Long?
          if (_stmt.isNull(_columnIndexOfRecordsLockedAt)) {
            _tmpRecordsLockedAt = null
          } else {
            _tmpRecordsLockedAt = _stmt.getLong(_columnIndexOfRecordsLockedAt)
          }
          val _tmpAutoLockAfterDays: Int
          _tmpAutoLockAfterDays = _stmt.getLong(_columnIndexOfAutoLockAfterDays).toInt()
          val _tmpLineageHistoryJson: String?
          if (_stmt.isNull(_columnIndexOfLineageHistoryJson)) {
            _tmpLineageHistoryJson = null
          } else {
            _tmpLineageHistoryJson = _stmt.getText(_columnIndexOfLineageHistoryJson)
          }
          val _tmpEditCount: Int
          _tmpEditCount = _stmt.getLong(_columnIndexOfEditCount).toInt()
          val _tmpLastEditedBy: String?
          if (_stmt.isNull(_columnIndexOfLastEditedBy)) {
            _tmpLastEditedBy = null
          } else {
            _tmpLastEditedBy = _stmt.getText(_columnIndexOfLastEditedBy)
          }
          val _tmpAdminFlagged: Boolean
          val _tmp_14: Int
          _tmp_14 = _stmt.getLong(_columnIndexOfAdminFlagged).toInt()
          _tmpAdminFlagged = _tmp_14 != 0
          val _tmpModerationNote: String?
          if (_stmt.isNull(_columnIndexOfModerationNote)) {
            _tmpModerationNote = null
          } else {
            _tmpModerationNote = _stmt.getText(_columnIndexOfModerationNote)
          }
          val _tmpMetadataJson: String
          _tmpMetadataJson = _stmt.getText(_columnIndexOfMetadataJson)
          _item =
              ProductEntity(_tmpProductId,_tmpSellerId,_tmpName,_tmpDescription,_tmpCategory,_tmpPrice,_tmpQuantity,_tmpUnit,_tmpLocation,_tmpLatitude,_tmpLongitude,_tmpImageUrls,_tmpStatus,_tmpCondition,_tmpHarvestDate,_tmpExpiryDate,_tmpBirthDate,_tmpVaccinationRecordsJson,_tmpWeightGrams,_tmpHeightCm,_tmpGender,_tmpColor,_tmpBreed,_tmpRaisingPurpose,_tmpHealthStatus,_tmpBirdCode,_tmpColorTag,_tmpFamilyTreeId,_tmpSourceAssetId,_tmpParentIdsJson,_tmpBreedingStatus,_tmpTransferHistoryJson,_tmpCreatedAt,_tmpUpdatedAt,_tmpLastModifiedAt,_tmpIsDeleted,_tmpDeletedAt,_tmpDirty,_tmpStage,_tmpLifecycleStatus,_tmpParentMaleId,_tmpParentFemaleId,_tmpAgeWeeks,_tmpLastStageTransitionAt,_tmpBreederEligibleAt,_tmpIsBatch,_tmpBatchId,_tmpSplitAt,_tmpSplitIntoIds,_tmpDocumentUrls,_tmpQrCodeUrl,_tmpCustomStatus,_tmpDebug,_tmpDeliveryOptions,_tmpDeliveryCost,_tmpLeadTimeDays,_tmpMotherId,_tmpIsBreedingUnit,_tmpEggsCollectedToday,_tmpLastEggLogDate,_tmpReadyForSale,_tmpTargetWeight,_tmpIsShowcased,_tmpExternalVideoUrl,_tmpRecordsLockedAt,_tmpAutoLockAfterDays,_tmpLineageHistoryJson,_tmpEditCount,_tmpLastEditedBy,_tmpAdminFlagged,_tmpModerationNote,_tmpMetadataJson)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getProductsBySeller(sellerId: String): Flow<List<ProductEntity>> {
    val _sql: String = "SELECT * FROM products WHERE sellerId = ? ORDER BY createdAt DESC"
    return createFlow(__db, false, arrayOf("products")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, sellerId)
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfSellerId: Int = getColumnIndexOrThrow(_stmt, "sellerId")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfCategory: Int = getColumnIndexOrThrow(_stmt, "category")
        val _columnIndexOfPrice: Int = getColumnIndexOrThrow(_stmt, "price")
        val _columnIndexOfQuantity: Int = getColumnIndexOrThrow(_stmt, "quantity")
        val _columnIndexOfUnit: Int = getColumnIndexOrThrow(_stmt, "unit")
        val _columnIndexOfLocation: Int = getColumnIndexOrThrow(_stmt, "location")
        val _columnIndexOfLatitude: Int = getColumnIndexOrThrow(_stmt, "latitude")
        val _columnIndexOfLongitude: Int = getColumnIndexOrThrow(_stmt, "longitude")
        val _columnIndexOfImageUrls: Int = getColumnIndexOrThrow(_stmt, "imageUrls")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfCondition: Int = getColumnIndexOrThrow(_stmt, "condition")
        val _columnIndexOfHarvestDate: Int = getColumnIndexOrThrow(_stmt, "harvestDate")
        val _columnIndexOfExpiryDate: Int = getColumnIndexOrThrow(_stmt, "expiryDate")
        val _columnIndexOfBirthDate: Int = getColumnIndexOrThrow(_stmt, "birthDate")
        val _columnIndexOfVaccinationRecordsJson: Int = getColumnIndexOrThrow(_stmt,
            "vaccinationRecordsJson")
        val _columnIndexOfWeightGrams: Int = getColumnIndexOrThrow(_stmt, "weightGrams")
        val _columnIndexOfHeightCm: Int = getColumnIndexOrThrow(_stmt, "heightCm")
        val _columnIndexOfGender: Int = getColumnIndexOrThrow(_stmt, "gender")
        val _columnIndexOfColor: Int = getColumnIndexOrThrow(_stmt, "color")
        val _columnIndexOfBreed: Int = getColumnIndexOrThrow(_stmt, "breed")
        val _columnIndexOfRaisingPurpose: Int = getColumnIndexOrThrow(_stmt, "raisingPurpose")
        val _columnIndexOfHealthStatus: Int = getColumnIndexOrThrow(_stmt, "healthStatus")
        val _columnIndexOfBirdCode: Int = getColumnIndexOrThrow(_stmt, "birdCode")
        val _columnIndexOfColorTag: Int = getColumnIndexOrThrow(_stmt, "colorTag")
        val _columnIndexOfFamilyTreeId: Int = getColumnIndexOrThrow(_stmt, "familyTreeId")
        val _columnIndexOfSourceAssetId: Int = getColumnIndexOrThrow(_stmt, "sourceAssetId")
        val _columnIndexOfParentIdsJson: Int = getColumnIndexOrThrow(_stmt, "parentIdsJson")
        val _columnIndexOfBreedingStatus: Int = getColumnIndexOrThrow(_stmt, "breedingStatus")
        val _columnIndexOfTransferHistoryJson: Int = getColumnIndexOrThrow(_stmt,
            "transferHistoryJson")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfLastModifiedAt: Int = getColumnIndexOrThrow(_stmt, "lastModifiedAt")
        val _columnIndexOfIsDeleted: Int = getColumnIndexOrThrow(_stmt, "isDeleted")
        val _columnIndexOfDeletedAt: Int = getColumnIndexOrThrow(_stmt, "deletedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfStage: Int = getColumnIndexOrThrow(_stmt, "stage")
        val _columnIndexOfLifecycleStatus: Int = getColumnIndexOrThrow(_stmt, "lifecycleStatus")
        val _columnIndexOfParentMaleId: Int = getColumnIndexOrThrow(_stmt, "parentMaleId")
        val _columnIndexOfParentFemaleId: Int = getColumnIndexOrThrow(_stmt, "parentFemaleId")
        val _columnIndexOfAgeWeeks: Int = getColumnIndexOrThrow(_stmt, "ageWeeks")
        val _columnIndexOfLastStageTransitionAt: Int = getColumnIndexOrThrow(_stmt,
            "lastStageTransitionAt")
        val _columnIndexOfBreederEligibleAt: Int = getColumnIndexOrThrow(_stmt, "breederEligibleAt")
        val _columnIndexOfIsBatch: Int = getColumnIndexOrThrow(_stmt, "isBatch")
        val _columnIndexOfBatchId: Int = getColumnIndexOrThrow(_stmt, "batchId")
        val _columnIndexOfSplitAt: Int = getColumnIndexOrThrow(_stmt, "splitAt")
        val _columnIndexOfSplitIntoIds: Int = getColumnIndexOrThrow(_stmt, "splitIntoIds")
        val _columnIndexOfDocumentUrls: Int = getColumnIndexOrThrow(_stmt, "documentUrls")
        val _columnIndexOfQrCodeUrl: Int = getColumnIndexOrThrow(_stmt, "qrCodeUrl")
        val _columnIndexOfCustomStatus: Int = getColumnIndexOrThrow(_stmt, "customStatus")
        val _columnIndexOfDebug: Int = getColumnIndexOrThrow(_stmt, "debug")
        val _columnIndexOfDeliveryOptions: Int = getColumnIndexOrThrow(_stmt, "deliveryOptions")
        val _columnIndexOfDeliveryCost: Int = getColumnIndexOrThrow(_stmt, "deliveryCost")
        val _columnIndexOfLeadTimeDays: Int = getColumnIndexOrThrow(_stmt, "leadTimeDays")
        val _columnIndexOfMotherId: Int = getColumnIndexOrThrow(_stmt, "motherId")
        val _columnIndexOfIsBreedingUnit: Int = getColumnIndexOrThrow(_stmt, "isBreedingUnit")
        val _columnIndexOfEggsCollectedToday: Int = getColumnIndexOrThrow(_stmt,
            "eggsCollectedToday")
        val _columnIndexOfLastEggLogDate: Int = getColumnIndexOrThrow(_stmt, "lastEggLogDate")
        val _columnIndexOfReadyForSale: Int = getColumnIndexOrThrow(_stmt, "readyForSale")
        val _columnIndexOfTargetWeight: Int = getColumnIndexOrThrow(_stmt, "targetWeight")
        val _columnIndexOfIsShowcased: Int = getColumnIndexOrThrow(_stmt, "isShowcased")
        val _columnIndexOfExternalVideoUrl: Int = getColumnIndexOrThrow(_stmt, "externalVideoUrl")
        val _columnIndexOfRecordsLockedAt: Int = getColumnIndexOrThrow(_stmt, "recordsLockedAt")
        val _columnIndexOfAutoLockAfterDays: Int = getColumnIndexOrThrow(_stmt, "autoLockAfterDays")
        val _columnIndexOfLineageHistoryJson: Int = getColumnIndexOrThrow(_stmt,
            "lineageHistoryJson")
        val _columnIndexOfEditCount: Int = getColumnIndexOrThrow(_stmt, "editCount")
        val _columnIndexOfLastEditedBy: Int = getColumnIndexOrThrow(_stmt, "lastEditedBy")
        val _columnIndexOfAdminFlagged: Int = getColumnIndexOrThrow(_stmt, "adminFlagged")
        val _columnIndexOfModerationNote: Int = getColumnIndexOrThrow(_stmt, "moderationNote")
        val _columnIndexOfMetadataJson: Int = getColumnIndexOrThrow(_stmt, "metadataJson")
        val _result: MutableList<ProductEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: ProductEntity
          val _tmpProductId: String
          _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          val _tmpSellerId: String
          _tmpSellerId = _stmt.getText(_columnIndexOfSellerId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpDescription: String
          _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          val _tmpCategory: String
          _tmpCategory = _stmt.getText(_columnIndexOfCategory)
          val _tmpPrice: Double
          _tmpPrice = _stmt.getDouble(_columnIndexOfPrice)
          val _tmpQuantity: Double
          _tmpQuantity = _stmt.getDouble(_columnIndexOfQuantity)
          val _tmpUnit: String
          _tmpUnit = _stmt.getText(_columnIndexOfUnit)
          val _tmpLocation: String
          _tmpLocation = _stmt.getText(_columnIndexOfLocation)
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
          val _tmpImageUrls: List<String>
          val _tmp: String?
          if (_stmt.isNull(_columnIndexOfImageUrls)) {
            _tmp = null
          } else {
            _tmp = _stmt.getText(_columnIndexOfImageUrls)
          }
          val _tmp_1: List<String>? = AppDatabase.Converters.toStringList(_tmp)
          if (_tmp_1 == null) {
            error("Expected NON-NULL 'kotlin.collections.List<kotlin.String>', but it was NULL.")
          } else {
            _tmpImageUrls = _tmp_1
          }
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpCondition: String?
          if (_stmt.isNull(_columnIndexOfCondition)) {
            _tmpCondition = null
          } else {
            _tmpCondition = _stmt.getText(_columnIndexOfCondition)
          }
          val _tmpHarvestDate: Long?
          if (_stmt.isNull(_columnIndexOfHarvestDate)) {
            _tmpHarvestDate = null
          } else {
            _tmpHarvestDate = _stmt.getLong(_columnIndexOfHarvestDate)
          }
          val _tmpExpiryDate: Long?
          if (_stmt.isNull(_columnIndexOfExpiryDate)) {
            _tmpExpiryDate = null
          } else {
            _tmpExpiryDate = _stmt.getLong(_columnIndexOfExpiryDate)
          }
          val _tmpBirthDate: Long?
          if (_stmt.isNull(_columnIndexOfBirthDate)) {
            _tmpBirthDate = null
          } else {
            _tmpBirthDate = _stmt.getLong(_columnIndexOfBirthDate)
          }
          val _tmpVaccinationRecordsJson: String?
          if (_stmt.isNull(_columnIndexOfVaccinationRecordsJson)) {
            _tmpVaccinationRecordsJson = null
          } else {
            _tmpVaccinationRecordsJson = _stmt.getText(_columnIndexOfVaccinationRecordsJson)
          }
          val _tmpWeightGrams: Double?
          if (_stmt.isNull(_columnIndexOfWeightGrams)) {
            _tmpWeightGrams = null
          } else {
            _tmpWeightGrams = _stmt.getDouble(_columnIndexOfWeightGrams)
          }
          val _tmpHeightCm: Double?
          if (_stmt.isNull(_columnIndexOfHeightCm)) {
            _tmpHeightCm = null
          } else {
            _tmpHeightCm = _stmt.getDouble(_columnIndexOfHeightCm)
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
          val _tmpBreed: String?
          if (_stmt.isNull(_columnIndexOfBreed)) {
            _tmpBreed = null
          } else {
            _tmpBreed = _stmt.getText(_columnIndexOfBreed)
          }
          val _tmpRaisingPurpose: String?
          if (_stmt.isNull(_columnIndexOfRaisingPurpose)) {
            _tmpRaisingPurpose = null
          } else {
            _tmpRaisingPurpose = _stmt.getText(_columnIndexOfRaisingPurpose)
          }
          val _tmpHealthStatus: String?
          if (_stmt.isNull(_columnIndexOfHealthStatus)) {
            _tmpHealthStatus = null
          } else {
            _tmpHealthStatus = _stmt.getText(_columnIndexOfHealthStatus)
          }
          val _tmpBirdCode: String?
          if (_stmt.isNull(_columnIndexOfBirdCode)) {
            _tmpBirdCode = null
          } else {
            _tmpBirdCode = _stmt.getText(_columnIndexOfBirdCode)
          }
          val _tmpColorTag: String?
          if (_stmt.isNull(_columnIndexOfColorTag)) {
            _tmpColorTag = null
          } else {
            _tmpColorTag = _stmt.getText(_columnIndexOfColorTag)
          }
          val _tmpFamilyTreeId: String?
          if (_stmt.isNull(_columnIndexOfFamilyTreeId)) {
            _tmpFamilyTreeId = null
          } else {
            _tmpFamilyTreeId = _stmt.getText(_columnIndexOfFamilyTreeId)
          }
          val _tmpSourceAssetId: String?
          if (_stmt.isNull(_columnIndexOfSourceAssetId)) {
            _tmpSourceAssetId = null
          } else {
            _tmpSourceAssetId = _stmt.getText(_columnIndexOfSourceAssetId)
          }
          val _tmpParentIdsJson: String?
          if (_stmt.isNull(_columnIndexOfParentIdsJson)) {
            _tmpParentIdsJson = null
          } else {
            _tmpParentIdsJson = _stmt.getText(_columnIndexOfParentIdsJson)
          }
          val _tmpBreedingStatus: String?
          if (_stmt.isNull(_columnIndexOfBreedingStatus)) {
            _tmpBreedingStatus = null
          } else {
            _tmpBreedingStatus = _stmt.getText(_columnIndexOfBreedingStatus)
          }
          val _tmpTransferHistoryJson: String?
          if (_stmt.isNull(_columnIndexOfTransferHistoryJson)) {
            _tmpTransferHistoryJson = null
          } else {
            _tmpTransferHistoryJson = _stmt.getText(_columnIndexOfTransferHistoryJson)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpLastModifiedAt: Long
          _tmpLastModifiedAt = _stmt.getLong(_columnIndexOfLastModifiedAt)
          val _tmpIsDeleted: Boolean
          val _tmp_2: Int
          _tmp_2 = _stmt.getLong(_columnIndexOfIsDeleted).toInt()
          _tmpIsDeleted = _tmp_2 != 0
          val _tmpDeletedAt: Long?
          if (_stmt.isNull(_columnIndexOfDeletedAt)) {
            _tmpDeletedAt = null
          } else {
            _tmpDeletedAt = _stmt.getLong(_columnIndexOfDeletedAt)
          }
          val _tmpDirty: Boolean
          val _tmp_3: Int
          _tmp_3 = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp_3 != 0
          val _tmpStage: LifecycleStage?
          val _tmp_4: String?
          if (_stmt.isNull(_columnIndexOfStage)) {
            _tmp_4 = null
          } else {
            _tmp_4 = _stmt.getText(_columnIndexOfStage)
          }
          _tmpStage = AppDatabase.Converters.toLifecycleStage(_tmp_4)
          val _tmpLifecycleStatus: String?
          if (_stmt.isNull(_columnIndexOfLifecycleStatus)) {
            _tmpLifecycleStatus = null
          } else {
            _tmpLifecycleStatus = _stmt.getText(_columnIndexOfLifecycleStatus)
          }
          val _tmpParentMaleId: String?
          if (_stmt.isNull(_columnIndexOfParentMaleId)) {
            _tmpParentMaleId = null
          } else {
            _tmpParentMaleId = _stmt.getText(_columnIndexOfParentMaleId)
          }
          val _tmpParentFemaleId: String?
          if (_stmt.isNull(_columnIndexOfParentFemaleId)) {
            _tmpParentFemaleId = null
          } else {
            _tmpParentFemaleId = _stmt.getText(_columnIndexOfParentFemaleId)
          }
          val _tmpAgeWeeks: Int?
          if (_stmt.isNull(_columnIndexOfAgeWeeks)) {
            _tmpAgeWeeks = null
          } else {
            _tmpAgeWeeks = _stmt.getLong(_columnIndexOfAgeWeeks).toInt()
          }
          val _tmpLastStageTransitionAt: Long?
          if (_stmt.isNull(_columnIndexOfLastStageTransitionAt)) {
            _tmpLastStageTransitionAt = null
          } else {
            _tmpLastStageTransitionAt = _stmt.getLong(_columnIndexOfLastStageTransitionAt)
          }
          val _tmpBreederEligibleAt: Long?
          if (_stmt.isNull(_columnIndexOfBreederEligibleAt)) {
            _tmpBreederEligibleAt = null
          } else {
            _tmpBreederEligibleAt = _stmt.getLong(_columnIndexOfBreederEligibleAt)
          }
          val _tmpIsBatch: Boolean?
          val _tmp_5: Int?
          if (_stmt.isNull(_columnIndexOfIsBatch)) {
            _tmp_5 = null
          } else {
            _tmp_5 = _stmt.getLong(_columnIndexOfIsBatch).toInt()
          }
          _tmpIsBatch = _tmp_5?.let { it != 0 }
          val _tmpBatchId: String?
          if (_stmt.isNull(_columnIndexOfBatchId)) {
            _tmpBatchId = null
          } else {
            _tmpBatchId = _stmt.getText(_columnIndexOfBatchId)
          }
          val _tmpSplitAt: Long?
          if (_stmt.isNull(_columnIndexOfSplitAt)) {
            _tmpSplitAt = null
          } else {
            _tmpSplitAt = _stmt.getLong(_columnIndexOfSplitAt)
          }
          val _tmpSplitIntoIds: String?
          if (_stmt.isNull(_columnIndexOfSplitIntoIds)) {
            _tmpSplitIntoIds = null
          } else {
            _tmpSplitIntoIds = _stmt.getText(_columnIndexOfSplitIntoIds)
          }
          val _tmpDocumentUrls: List<String>
          val _tmp_6: String?
          if (_stmt.isNull(_columnIndexOfDocumentUrls)) {
            _tmp_6 = null
          } else {
            _tmp_6 = _stmt.getText(_columnIndexOfDocumentUrls)
          }
          val _tmp_7: List<String>? = AppDatabase.Converters.toStringList(_tmp_6)
          if (_tmp_7 == null) {
            error("Expected NON-NULL 'kotlin.collections.List<kotlin.String>', but it was NULL.")
          } else {
            _tmpDocumentUrls = _tmp_7
          }
          val _tmpQrCodeUrl: String?
          if (_stmt.isNull(_columnIndexOfQrCodeUrl)) {
            _tmpQrCodeUrl = null
          } else {
            _tmpQrCodeUrl = _stmt.getText(_columnIndexOfQrCodeUrl)
          }
          val _tmpCustomStatus: String?
          if (_stmt.isNull(_columnIndexOfCustomStatus)) {
            _tmpCustomStatus = null
          } else {
            _tmpCustomStatus = _stmt.getText(_columnIndexOfCustomStatus)
          }
          val _tmpDebug: Boolean
          val _tmp_8: Int
          _tmp_8 = _stmt.getLong(_columnIndexOfDebug).toInt()
          _tmpDebug = _tmp_8 != 0
          val _tmpDeliveryOptions: List<String>
          val _tmp_9: String?
          if (_stmt.isNull(_columnIndexOfDeliveryOptions)) {
            _tmp_9 = null
          } else {
            _tmp_9 = _stmt.getText(_columnIndexOfDeliveryOptions)
          }
          val _tmp_10: List<String>? = AppDatabase.Converters.toStringList(_tmp_9)
          if (_tmp_10 == null) {
            error("Expected NON-NULL 'kotlin.collections.List<kotlin.String>', but it was NULL.")
          } else {
            _tmpDeliveryOptions = _tmp_10
          }
          val _tmpDeliveryCost: Double?
          if (_stmt.isNull(_columnIndexOfDeliveryCost)) {
            _tmpDeliveryCost = null
          } else {
            _tmpDeliveryCost = _stmt.getDouble(_columnIndexOfDeliveryCost)
          }
          val _tmpLeadTimeDays: Int?
          if (_stmt.isNull(_columnIndexOfLeadTimeDays)) {
            _tmpLeadTimeDays = null
          } else {
            _tmpLeadTimeDays = _stmt.getLong(_columnIndexOfLeadTimeDays).toInt()
          }
          val _tmpMotherId: String?
          if (_stmt.isNull(_columnIndexOfMotherId)) {
            _tmpMotherId = null
          } else {
            _tmpMotherId = _stmt.getText(_columnIndexOfMotherId)
          }
          val _tmpIsBreedingUnit: Boolean
          val _tmp_11: Int
          _tmp_11 = _stmt.getLong(_columnIndexOfIsBreedingUnit).toInt()
          _tmpIsBreedingUnit = _tmp_11 != 0
          val _tmpEggsCollectedToday: Int
          _tmpEggsCollectedToday = _stmt.getLong(_columnIndexOfEggsCollectedToday).toInt()
          val _tmpLastEggLogDate: Long?
          if (_stmt.isNull(_columnIndexOfLastEggLogDate)) {
            _tmpLastEggLogDate = null
          } else {
            _tmpLastEggLogDate = _stmt.getLong(_columnIndexOfLastEggLogDate)
          }
          val _tmpReadyForSale: Boolean
          val _tmp_12: Int
          _tmp_12 = _stmt.getLong(_columnIndexOfReadyForSale).toInt()
          _tmpReadyForSale = _tmp_12 != 0
          val _tmpTargetWeight: Double?
          if (_stmt.isNull(_columnIndexOfTargetWeight)) {
            _tmpTargetWeight = null
          } else {
            _tmpTargetWeight = _stmt.getDouble(_columnIndexOfTargetWeight)
          }
          val _tmpIsShowcased: Boolean
          val _tmp_13: Int
          _tmp_13 = _stmt.getLong(_columnIndexOfIsShowcased).toInt()
          _tmpIsShowcased = _tmp_13 != 0
          val _tmpExternalVideoUrl: String?
          if (_stmt.isNull(_columnIndexOfExternalVideoUrl)) {
            _tmpExternalVideoUrl = null
          } else {
            _tmpExternalVideoUrl = _stmt.getText(_columnIndexOfExternalVideoUrl)
          }
          val _tmpRecordsLockedAt: Long?
          if (_stmt.isNull(_columnIndexOfRecordsLockedAt)) {
            _tmpRecordsLockedAt = null
          } else {
            _tmpRecordsLockedAt = _stmt.getLong(_columnIndexOfRecordsLockedAt)
          }
          val _tmpAutoLockAfterDays: Int
          _tmpAutoLockAfterDays = _stmt.getLong(_columnIndexOfAutoLockAfterDays).toInt()
          val _tmpLineageHistoryJson: String?
          if (_stmt.isNull(_columnIndexOfLineageHistoryJson)) {
            _tmpLineageHistoryJson = null
          } else {
            _tmpLineageHistoryJson = _stmt.getText(_columnIndexOfLineageHistoryJson)
          }
          val _tmpEditCount: Int
          _tmpEditCount = _stmt.getLong(_columnIndexOfEditCount).toInt()
          val _tmpLastEditedBy: String?
          if (_stmt.isNull(_columnIndexOfLastEditedBy)) {
            _tmpLastEditedBy = null
          } else {
            _tmpLastEditedBy = _stmt.getText(_columnIndexOfLastEditedBy)
          }
          val _tmpAdminFlagged: Boolean
          val _tmp_14: Int
          _tmp_14 = _stmt.getLong(_columnIndexOfAdminFlagged).toInt()
          _tmpAdminFlagged = _tmp_14 != 0
          val _tmpModerationNote: String?
          if (_stmt.isNull(_columnIndexOfModerationNote)) {
            _tmpModerationNote = null
          } else {
            _tmpModerationNote = _stmt.getText(_columnIndexOfModerationNote)
          }
          val _tmpMetadataJson: String
          _tmpMetadataJson = _stmt.getText(_columnIndexOfMetadataJson)
          _item =
              ProductEntity(_tmpProductId,_tmpSellerId,_tmpName,_tmpDescription,_tmpCategory,_tmpPrice,_tmpQuantity,_tmpUnit,_tmpLocation,_tmpLatitude,_tmpLongitude,_tmpImageUrls,_tmpStatus,_tmpCondition,_tmpHarvestDate,_tmpExpiryDate,_tmpBirthDate,_tmpVaccinationRecordsJson,_tmpWeightGrams,_tmpHeightCm,_tmpGender,_tmpColor,_tmpBreed,_tmpRaisingPurpose,_tmpHealthStatus,_tmpBirdCode,_tmpColorTag,_tmpFamilyTreeId,_tmpSourceAssetId,_tmpParentIdsJson,_tmpBreedingStatus,_tmpTransferHistoryJson,_tmpCreatedAt,_tmpUpdatedAt,_tmpLastModifiedAt,_tmpIsDeleted,_tmpDeletedAt,_tmpDirty,_tmpStage,_tmpLifecycleStatus,_tmpParentMaleId,_tmpParentFemaleId,_tmpAgeWeeks,_tmpLastStageTransitionAt,_tmpBreederEligibleAt,_tmpIsBatch,_tmpBatchId,_tmpSplitAt,_tmpSplitIntoIds,_tmpDocumentUrls,_tmpQrCodeUrl,_tmpCustomStatus,_tmpDebug,_tmpDeliveryOptions,_tmpDeliveryCost,_tmpLeadTimeDays,_tmpMotherId,_tmpIsBreedingUnit,_tmpEggsCollectedToday,_tmpLastEggLogDate,_tmpReadyForSale,_tmpTargetWeight,_tmpIsShowcased,_tmpExternalVideoUrl,_tmpRecordsLockedAt,_tmpAutoLockAfterDays,_tmpLineageHistoryJson,_tmpEditCount,_tmpLastEditedBy,_tmpAdminFlagged,_tmpModerationNote,_tmpMetadataJson)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getProductsBySellerSuspend(sellerId: String): List<ProductEntity> {
    val _sql: String = "SELECT * FROM products WHERE sellerId = ? ORDER BY createdAt DESC"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, sellerId)
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfSellerId: Int = getColumnIndexOrThrow(_stmt, "sellerId")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfCategory: Int = getColumnIndexOrThrow(_stmt, "category")
        val _columnIndexOfPrice: Int = getColumnIndexOrThrow(_stmt, "price")
        val _columnIndexOfQuantity: Int = getColumnIndexOrThrow(_stmt, "quantity")
        val _columnIndexOfUnit: Int = getColumnIndexOrThrow(_stmt, "unit")
        val _columnIndexOfLocation: Int = getColumnIndexOrThrow(_stmt, "location")
        val _columnIndexOfLatitude: Int = getColumnIndexOrThrow(_stmt, "latitude")
        val _columnIndexOfLongitude: Int = getColumnIndexOrThrow(_stmt, "longitude")
        val _columnIndexOfImageUrls: Int = getColumnIndexOrThrow(_stmt, "imageUrls")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfCondition: Int = getColumnIndexOrThrow(_stmt, "condition")
        val _columnIndexOfHarvestDate: Int = getColumnIndexOrThrow(_stmt, "harvestDate")
        val _columnIndexOfExpiryDate: Int = getColumnIndexOrThrow(_stmt, "expiryDate")
        val _columnIndexOfBirthDate: Int = getColumnIndexOrThrow(_stmt, "birthDate")
        val _columnIndexOfVaccinationRecordsJson: Int = getColumnIndexOrThrow(_stmt,
            "vaccinationRecordsJson")
        val _columnIndexOfWeightGrams: Int = getColumnIndexOrThrow(_stmt, "weightGrams")
        val _columnIndexOfHeightCm: Int = getColumnIndexOrThrow(_stmt, "heightCm")
        val _columnIndexOfGender: Int = getColumnIndexOrThrow(_stmt, "gender")
        val _columnIndexOfColor: Int = getColumnIndexOrThrow(_stmt, "color")
        val _columnIndexOfBreed: Int = getColumnIndexOrThrow(_stmt, "breed")
        val _columnIndexOfRaisingPurpose: Int = getColumnIndexOrThrow(_stmt, "raisingPurpose")
        val _columnIndexOfHealthStatus: Int = getColumnIndexOrThrow(_stmt, "healthStatus")
        val _columnIndexOfBirdCode: Int = getColumnIndexOrThrow(_stmt, "birdCode")
        val _columnIndexOfColorTag: Int = getColumnIndexOrThrow(_stmt, "colorTag")
        val _columnIndexOfFamilyTreeId: Int = getColumnIndexOrThrow(_stmt, "familyTreeId")
        val _columnIndexOfSourceAssetId: Int = getColumnIndexOrThrow(_stmt, "sourceAssetId")
        val _columnIndexOfParentIdsJson: Int = getColumnIndexOrThrow(_stmt, "parentIdsJson")
        val _columnIndexOfBreedingStatus: Int = getColumnIndexOrThrow(_stmt, "breedingStatus")
        val _columnIndexOfTransferHistoryJson: Int = getColumnIndexOrThrow(_stmt,
            "transferHistoryJson")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfLastModifiedAt: Int = getColumnIndexOrThrow(_stmt, "lastModifiedAt")
        val _columnIndexOfIsDeleted: Int = getColumnIndexOrThrow(_stmt, "isDeleted")
        val _columnIndexOfDeletedAt: Int = getColumnIndexOrThrow(_stmt, "deletedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfStage: Int = getColumnIndexOrThrow(_stmt, "stage")
        val _columnIndexOfLifecycleStatus: Int = getColumnIndexOrThrow(_stmt, "lifecycleStatus")
        val _columnIndexOfParentMaleId: Int = getColumnIndexOrThrow(_stmt, "parentMaleId")
        val _columnIndexOfParentFemaleId: Int = getColumnIndexOrThrow(_stmt, "parentFemaleId")
        val _columnIndexOfAgeWeeks: Int = getColumnIndexOrThrow(_stmt, "ageWeeks")
        val _columnIndexOfLastStageTransitionAt: Int = getColumnIndexOrThrow(_stmt,
            "lastStageTransitionAt")
        val _columnIndexOfBreederEligibleAt: Int = getColumnIndexOrThrow(_stmt, "breederEligibleAt")
        val _columnIndexOfIsBatch: Int = getColumnIndexOrThrow(_stmt, "isBatch")
        val _columnIndexOfBatchId: Int = getColumnIndexOrThrow(_stmt, "batchId")
        val _columnIndexOfSplitAt: Int = getColumnIndexOrThrow(_stmt, "splitAt")
        val _columnIndexOfSplitIntoIds: Int = getColumnIndexOrThrow(_stmt, "splitIntoIds")
        val _columnIndexOfDocumentUrls: Int = getColumnIndexOrThrow(_stmt, "documentUrls")
        val _columnIndexOfQrCodeUrl: Int = getColumnIndexOrThrow(_stmt, "qrCodeUrl")
        val _columnIndexOfCustomStatus: Int = getColumnIndexOrThrow(_stmt, "customStatus")
        val _columnIndexOfDebug: Int = getColumnIndexOrThrow(_stmt, "debug")
        val _columnIndexOfDeliveryOptions: Int = getColumnIndexOrThrow(_stmt, "deliveryOptions")
        val _columnIndexOfDeliveryCost: Int = getColumnIndexOrThrow(_stmt, "deliveryCost")
        val _columnIndexOfLeadTimeDays: Int = getColumnIndexOrThrow(_stmt, "leadTimeDays")
        val _columnIndexOfMotherId: Int = getColumnIndexOrThrow(_stmt, "motherId")
        val _columnIndexOfIsBreedingUnit: Int = getColumnIndexOrThrow(_stmt, "isBreedingUnit")
        val _columnIndexOfEggsCollectedToday: Int = getColumnIndexOrThrow(_stmt,
            "eggsCollectedToday")
        val _columnIndexOfLastEggLogDate: Int = getColumnIndexOrThrow(_stmt, "lastEggLogDate")
        val _columnIndexOfReadyForSale: Int = getColumnIndexOrThrow(_stmt, "readyForSale")
        val _columnIndexOfTargetWeight: Int = getColumnIndexOrThrow(_stmt, "targetWeight")
        val _columnIndexOfIsShowcased: Int = getColumnIndexOrThrow(_stmt, "isShowcased")
        val _columnIndexOfExternalVideoUrl: Int = getColumnIndexOrThrow(_stmt, "externalVideoUrl")
        val _columnIndexOfRecordsLockedAt: Int = getColumnIndexOrThrow(_stmt, "recordsLockedAt")
        val _columnIndexOfAutoLockAfterDays: Int = getColumnIndexOrThrow(_stmt, "autoLockAfterDays")
        val _columnIndexOfLineageHistoryJson: Int = getColumnIndexOrThrow(_stmt,
            "lineageHistoryJson")
        val _columnIndexOfEditCount: Int = getColumnIndexOrThrow(_stmt, "editCount")
        val _columnIndexOfLastEditedBy: Int = getColumnIndexOrThrow(_stmt, "lastEditedBy")
        val _columnIndexOfAdminFlagged: Int = getColumnIndexOrThrow(_stmt, "adminFlagged")
        val _columnIndexOfModerationNote: Int = getColumnIndexOrThrow(_stmt, "moderationNote")
        val _columnIndexOfMetadataJson: Int = getColumnIndexOrThrow(_stmt, "metadataJson")
        val _result: MutableList<ProductEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: ProductEntity
          val _tmpProductId: String
          _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          val _tmpSellerId: String
          _tmpSellerId = _stmt.getText(_columnIndexOfSellerId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpDescription: String
          _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          val _tmpCategory: String
          _tmpCategory = _stmt.getText(_columnIndexOfCategory)
          val _tmpPrice: Double
          _tmpPrice = _stmt.getDouble(_columnIndexOfPrice)
          val _tmpQuantity: Double
          _tmpQuantity = _stmt.getDouble(_columnIndexOfQuantity)
          val _tmpUnit: String
          _tmpUnit = _stmt.getText(_columnIndexOfUnit)
          val _tmpLocation: String
          _tmpLocation = _stmt.getText(_columnIndexOfLocation)
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
          val _tmpImageUrls: List<String>
          val _tmp: String?
          if (_stmt.isNull(_columnIndexOfImageUrls)) {
            _tmp = null
          } else {
            _tmp = _stmt.getText(_columnIndexOfImageUrls)
          }
          val _tmp_1: List<String>? = AppDatabase.Converters.toStringList(_tmp)
          if (_tmp_1 == null) {
            error("Expected NON-NULL 'kotlin.collections.List<kotlin.String>', but it was NULL.")
          } else {
            _tmpImageUrls = _tmp_1
          }
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpCondition: String?
          if (_stmt.isNull(_columnIndexOfCondition)) {
            _tmpCondition = null
          } else {
            _tmpCondition = _stmt.getText(_columnIndexOfCondition)
          }
          val _tmpHarvestDate: Long?
          if (_stmt.isNull(_columnIndexOfHarvestDate)) {
            _tmpHarvestDate = null
          } else {
            _tmpHarvestDate = _stmt.getLong(_columnIndexOfHarvestDate)
          }
          val _tmpExpiryDate: Long?
          if (_stmt.isNull(_columnIndexOfExpiryDate)) {
            _tmpExpiryDate = null
          } else {
            _tmpExpiryDate = _stmt.getLong(_columnIndexOfExpiryDate)
          }
          val _tmpBirthDate: Long?
          if (_stmt.isNull(_columnIndexOfBirthDate)) {
            _tmpBirthDate = null
          } else {
            _tmpBirthDate = _stmt.getLong(_columnIndexOfBirthDate)
          }
          val _tmpVaccinationRecordsJson: String?
          if (_stmt.isNull(_columnIndexOfVaccinationRecordsJson)) {
            _tmpVaccinationRecordsJson = null
          } else {
            _tmpVaccinationRecordsJson = _stmt.getText(_columnIndexOfVaccinationRecordsJson)
          }
          val _tmpWeightGrams: Double?
          if (_stmt.isNull(_columnIndexOfWeightGrams)) {
            _tmpWeightGrams = null
          } else {
            _tmpWeightGrams = _stmt.getDouble(_columnIndexOfWeightGrams)
          }
          val _tmpHeightCm: Double?
          if (_stmt.isNull(_columnIndexOfHeightCm)) {
            _tmpHeightCm = null
          } else {
            _tmpHeightCm = _stmt.getDouble(_columnIndexOfHeightCm)
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
          val _tmpBreed: String?
          if (_stmt.isNull(_columnIndexOfBreed)) {
            _tmpBreed = null
          } else {
            _tmpBreed = _stmt.getText(_columnIndexOfBreed)
          }
          val _tmpRaisingPurpose: String?
          if (_stmt.isNull(_columnIndexOfRaisingPurpose)) {
            _tmpRaisingPurpose = null
          } else {
            _tmpRaisingPurpose = _stmt.getText(_columnIndexOfRaisingPurpose)
          }
          val _tmpHealthStatus: String?
          if (_stmt.isNull(_columnIndexOfHealthStatus)) {
            _tmpHealthStatus = null
          } else {
            _tmpHealthStatus = _stmt.getText(_columnIndexOfHealthStatus)
          }
          val _tmpBirdCode: String?
          if (_stmt.isNull(_columnIndexOfBirdCode)) {
            _tmpBirdCode = null
          } else {
            _tmpBirdCode = _stmt.getText(_columnIndexOfBirdCode)
          }
          val _tmpColorTag: String?
          if (_stmt.isNull(_columnIndexOfColorTag)) {
            _tmpColorTag = null
          } else {
            _tmpColorTag = _stmt.getText(_columnIndexOfColorTag)
          }
          val _tmpFamilyTreeId: String?
          if (_stmt.isNull(_columnIndexOfFamilyTreeId)) {
            _tmpFamilyTreeId = null
          } else {
            _tmpFamilyTreeId = _stmt.getText(_columnIndexOfFamilyTreeId)
          }
          val _tmpSourceAssetId: String?
          if (_stmt.isNull(_columnIndexOfSourceAssetId)) {
            _tmpSourceAssetId = null
          } else {
            _tmpSourceAssetId = _stmt.getText(_columnIndexOfSourceAssetId)
          }
          val _tmpParentIdsJson: String?
          if (_stmt.isNull(_columnIndexOfParentIdsJson)) {
            _tmpParentIdsJson = null
          } else {
            _tmpParentIdsJson = _stmt.getText(_columnIndexOfParentIdsJson)
          }
          val _tmpBreedingStatus: String?
          if (_stmt.isNull(_columnIndexOfBreedingStatus)) {
            _tmpBreedingStatus = null
          } else {
            _tmpBreedingStatus = _stmt.getText(_columnIndexOfBreedingStatus)
          }
          val _tmpTransferHistoryJson: String?
          if (_stmt.isNull(_columnIndexOfTransferHistoryJson)) {
            _tmpTransferHistoryJson = null
          } else {
            _tmpTransferHistoryJson = _stmt.getText(_columnIndexOfTransferHistoryJson)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpLastModifiedAt: Long
          _tmpLastModifiedAt = _stmt.getLong(_columnIndexOfLastModifiedAt)
          val _tmpIsDeleted: Boolean
          val _tmp_2: Int
          _tmp_2 = _stmt.getLong(_columnIndexOfIsDeleted).toInt()
          _tmpIsDeleted = _tmp_2 != 0
          val _tmpDeletedAt: Long?
          if (_stmt.isNull(_columnIndexOfDeletedAt)) {
            _tmpDeletedAt = null
          } else {
            _tmpDeletedAt = _stmt.getLong(_columnIndexOfDeletedAt)
          }
          val _tmpDirty: Boolean
          val _tmp_3: Int
          _tmp_3 = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp_3 != 0
          val _tmpStage: LifecycleStage?
          val _tmp_4: String?
          if (_stmt.isNull(_columnIndexOfStage)) {
            _tmp_4 = null
          } else {
            _tmp_4 = _stmt.getText(_columnIndexOfStage)
          }
          _tmpStage = AppDatabase.Converters.toLifecycleStage(_tmp_4)
          val _tmpLifecycleStatus: String?
          if (_stmt.isNull(_columnIndexOfLifecycleStatus)) {
            _tmpLifecycleStatus = null
          } else {
            _tmpLifecycleStatus = _stmt.getText(_columnIndexOfLifecycleStatus)
          }
          val _tmpParentMaleId: String?
          if (_stmt.isNull(_columnIndexOfParentMaleId)) {
            _tmpParentMaleId = null
          } else {
            _tmpParentMaleId = _stmt.getText(_columnIndexOfParentMaleId)
          }
          val _tmpParentFemaleId: String?
          if (_stmt.isNull(_columnIndexOfParentFemaleId)) {
            _tmpParentFemaleId = null
          } else {
            _tmpParentFemaleId = _stmt.getText(_columnIndexOfParentFemaleId)
          }
          val _tmpAgeWeeks: Int?
          if (_stmt.isNull(_columnIndexOfAgeWeeks)) {
            _tmpAgeWeeks = null
          } else {
            _tmpAgeWeeks = _stmt.getLong(_columnIndexOfAgeWeeks).toInt()
          }
          val _tmpLastStageTransitionAt: Long?
          if (_stmt.isNull(_columnIndexOfLastStageTransitionAt)) {
            _tmpLastStageTransitionAt = null
          } else {
            _tmpLastStageTransitionAt = _stmt.getLong(_columnIndexOfLastStageTransitionAt)
          }
          val _tmpBreederEligibleAt: Long?
          if (_stmt.isNull(_columnIndexOfBreederEligibleAt)) {
            _tmpBreederEligibleAt = null
          } else {
            _tmpBreederEligibleAt = _stmt.getLong(_columnIndexOfBreederEligibleAt)
          }
          val _tmpIsBatch: Boolean?
          val _tmp_5: Int?
          if (_stmt.isNull(_columnIndexOfIsBatch)) {
            _tmp_5 = null
          } else {
            _tmp_5 = _stmt.getLong(_columnIndexOfIsBatch).toInt()
          }
          _tmpIsBatch = _tmp_5?.let { it != 0 }
          val _tmpBatchId: String?
          if (_stmt.isNull(_columnIndexOfBatchId)) {
            _tmpBatchId = null
          } else {
            _tmpBatchId = _stmt.getText(_columnIndexOfBatchId)
          }
          val _tmpSplitAt: Long?
          if (_stmt.isNull(_columnIndexOfSplitAt)) {
            _tmpSplitAt = null
          } else {
            _tmpSplitAt = _stmt.getLong(_columnIndexOfSplitAt)
          }
          val _tmpSplitIntoIds: String?
          if (_stmt.isNull(_columnIndexOfSplitIntoIds)) {
            _tmpSplitIntoIds = null
          } else {
            _tmpSplitIntoIds = _stmt.getText(_columnIndexOfSplitIntoIds)
          }
          val _tmpDocumentUrls: List<String>
          val _tmp_6: String?
          if (_stmt.isNull(_columnIndexOfDocumentUrls)) {
            _tmp_6 = null
          } else {
            _tmp_6 = _stmt.getText(_columnIndexOfDocumentUrls)
          }
          val _tmp_7: List<String>? = AppDatabase.Converters.toStringList(_tmp_6)
          if (_tmp_7 == null) {
            error("Expected NON-NULL 'kotlin.collections.List<kotlin.String>', but it was NULL.")
          } else {
            _tmpDocumentUrls = _tmp_7
          }
          val _tmpQrCodeUrl: String?
          if (_stmt.isNull(_columnIndexOfQrCodeUrl)) {
            _tmpQrCodeUrl = null
          } else {
            _tmpQrCodeUrl = _stmt.getText(_columnIndexOfQrCodeUrl)
          }
          val _tmpCustomStatus: String?
          if (_stmt.isNull(_columnIndexOfCustomStatus)) {
            _tmpCustomStatus = null
          } else {
            _tmpCustomStatus = _stmt.getText(_columnIndexOfCustomStatus)
          }
          val _tmpDebug: Boolean
          val _tmp_8: Int
          _tmp_8 = _stmt.getLong(_columnIndexOfDebug).toInt()
          _tmpDebug = _tmp_8 != 0
          val _tmpDeliveryOptions: List<String>
          val _tmp_9: String?
          if (_stmt.isNull(_columnIndexOfDeliveryOptions)) {
            _tmp_9 = null
          } else {
            _tmp_9 = _stmt.getText(_columnIndexOfDeliveryOptions)
          }
          val _tmp_10: List<String>? = AppDatabase.Converters.toStringList(_tmp_9)
          if (_tmp_10 == null) {
            error("Expected NON-NULL 'kotlin.collections.List<kotlin.String>', but it was NULL.")
          } else {
            _tmpDeliveryOptions = _tmp_10
          }
          val _tmpDeliveryCost: Double?
          if (_stmt.isNull(_columnIndexOfDeliveryCost)) {
            _tmpDeliveryCost = null
          } else {
            _tmpDeliveryCost = _stmt.getDouble(_columnIndexOfDeliveryCost)
          }
          val _tmpLeadTimeDays: Int?
          if (_stmt.isNull(_columnIndexOfLeadTimeDays)) {
            _tmpLeadTimeDays = null
          } else {
            _tmpLeadTimeDays = _stmt.getLong(_columnIndexOfLeadTimeDays).toInt()
          }
          val _tmpMotherId: String?
          if (_stmt.isNull(_columnIndexOfMotherId)) {
            _tmpMotherId = null
          } else {
            _tmpMotherId = _stmt.getText(_columnIndexOfMotherId)
          }
          val _tmpIsBreedingUnit: Boolean
          val _tmp_11: Int
          _tmp_11 = _stmt.getLong(_columnIndexOfIsBreedingUnit).toInt()
          _tmpIsBreedingUnit = _tmp_11 != 0
          val _tmpEggsCollectedToday: Int
          _tmpEggsCollectedToday = _stmt.getLong(_columnIndexOfEggsCollectedToday).toInt()
          val _tmpLastEggLogDate: Long?
          if (_stmt.isNull(_columnIndexOfLastEggLogDate)) {
            _tmpLastEggLogDate = null
          } else {
            _tmpLastEggLogDate = _stmt.getLong(_columnIndexOfLastEggLogDate)
          }
          val _tmpReadyForSale: Boolean
          val _tmp_12: Int
          _tmp_12 = _stmt.getLong(_columnIndexOfReadyForSale).toInt()
          _tmpReadyForSale = _tmp_12 != 0
          val _tmpTargetWeight: Double?
          if (_stmt.isNull(_columnIndexOfTargetWeight)) {
            _tmpTargetWeight = null
          } else {
            _tmpTargetWeight = _stmt.getDouble(_columnIndexOfTargetWeight)
          }
          val _tmpIsShowcased: Boolean
          val _tmp_13: Int
          _tmp_13 = _stmt.getLong(_columnIndexOfIsShowcased).toInt()
          _tmpIsShowcased = _tmp_13 != 0
          val _tmpExternalVideoUrl: String?
          if (_stmt.isNull(_columnIndexOfExternalVideoUrl)) {
            _tmpExternalVideoUrl = null
          } else {
            _tmpExternalVideoUrl = _stmt.getText(_columnIndexOfExternalVideoUrl)
          }
          val _tmpRecordsLockedAt: Long?
          if (_stmt.isNull(_columnIndexOfRecordsLockedAt)) {
            _tmpRecordsLockedAt = null
          } else {
            _tmpRecordsLockedAt = _stmt.getLong(_columnIndexOfRecordsLockedAt)
          }
          val _tmpAutoLockAfterDays: Int
          _tmpAutoLockAfterDays = _stmt.getLong(_columnIndexOfAutoLockAfterDays).toInt()
          val _tmpLineageHistoryJson: String?
          if (_stmt.isNull(_columnIndexOfLineageHistoryJson)) {
            _tmpLineageHistoryJson = null
          } else {
            _tmpLineageHistoryJson = _stmt.getText(_columnIndexOfLineageHistoryJson)
          }
          val _tmpEditCount: Int
          _tmpEditCount = _stmt.getLong(_columnIndexOfEditCount).toInt()
          val _tmpLastEditedBy: String?
          if (_stmt.isNull(_columnIndexOfLastEditedBy)) {
            _tmpLastEditedBy = null
          } else {
            _tmpLastEditedBy = _stmt.getText(_columnIndexOfLastEditedBy)
          }
          val _tmpAdminFlagged: Boolean
          val _tmp_14: Int
          _tmp_14 = _stmt.getLong(_columnIndexOfAdminFlagged).toInt()
          _tmpAdminFlagged = _tmp_14 != 0
          val _tmpModerationNote: String?
          if (_stmt.isNull(_columnIndexOfModerationNote)) {
            _tmpModerationNote = null
          } else {
            _tmpModerationNote = _stmt.getText(_columnIndexOfModerationNote)
          }
          val _tmpMetadataJson: String
          _tmpMetadataJson = _stmt.getText(_columnIndexOfMetadataJson)
          _item =
              ProductEntity(_tmpProductId,_tmpSellerId,_tmpName,_tmpDescription,_tmpCategory,_tmpPrice,_tmpQuantity,_tmpUnit,_tmpLocation,_tmpLatitude,_tmpLongitude,_tmpImageUrls,_tmpStatus,_tmpCondition,_tmpHarvestDate,_tmpExpiryDate,_tmpBirthDate,_tmpVaccinationRecordsJson,_tmpWeightGrams,_tmpHeightCm,_tmpGender,_tmpColor,_tmpBreed,_tmpRaisingPurpose,_tmpHealthStatus,_tmpBirdCode,_tmpColorTag,_tmpFamilyTreeId,_tmpSourceAssetId,_tmpParentIdsJson,_tmpBreedingStatus,_tmpTransferHistoryJson,_tmpCreatedAt,_tmpUpdatedAt,_tmpLastModifiedAt,_tmpIsDeleted,_tmpDeletedAt,_tmpDirty,_tmpStage,_tmpLifecycleStatus,_tmpParentMaleId,_tmpParentFemaleId,_tmpAgeWeeks,_tmpLastStageTransitionAt,_tmpBreederEligibleAt,_tmpIsBatch,_tmpBatchId,_tmpSplitAt,_tmpSplitIntoIds,_tmpDocumentUrls,_tmpQrCodeUrl,_tmpCustomStatus,_tmpDebug,_tmpDeliveryOptions,_tmpDeliveryCost,_tmpLeadTimeDays,_tmpMotherId,_tmpIsBreedingUnit,_tmpEggsCollectedToday,_tmpLastEggLogDate,_tmpReadyForSale,_tmpTargetWeight,_tmpIsShowcased,_tmpExternalVideoUrl,_tmpRecordsLockedAt,_tmpAutoLockAfterDays,_tmpLineageHistoryJson,_tmpEditCount,_tmpLastEditedBy,_tmpAdminFlagged,_tmpModerationNote,_tmpMetadataJson)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getProductsByCategory(category: String): Flow<List<ProductEntity>> {
    val _sql: String = "SELECT * FROM products WHERE category = ? ORDER BY name ASC"
    return createFlow(__db, false, arrayOf("products")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, category)
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfSellerId: Int = getColumnIndexOrThrow(_stmt, "sellerId")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfCategory: Int = getColumnIndexOrThrow(_stmt, "category")
        val _columnIndexOfPrice: Int = getColumnIndexOrThrow(_stmt, "price")
        val _columnIndexOfQuantity: Int = getColumnIndexOrThrow(_stmt, "quantity")
        val _columnIndexOfUnit: Int = getColumnIndexOrThrow(_stmt, "unit")
        val _columnIndexOfLocation: Int = getColumnIndexOrThrow(_stmt, "location")
        val _columnIndexOfLatitude: Int = getColumnIndexOrThrow(_stmt, "latitude")
        val _columnIndexOfLongitude: Int = getColumnIndexOrThrow(_stmt, "longitude")
        val _columnIndexOfImageUrls: Int = getColumnIndexOrThrow(_stmt, "imageUrls")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfCondition: Int = getColumnIndexOrThrow(_stmt, "condition")
        val _columnIndexOfHarvestDate: Int = getColumnIndexOrThrow(_stmt, "harvestDate")
        val _columnIndexOfExpiryDate: Int = getColumnIndexOrThrow(_stmt, "expiryDate")
        val _columnIndexOfBirthDate: Int = getColumnIndexOrThrow(_stmt, "birthDate")
        val _columnIndexOfVaccinationRecordsJson: Int = getColumnIndexOrThrow(_stmt,
            "vaccinationRecordsJson")
        val _columnIndexOfWeightGrams: Int = getColumnIndexOrThrow(_stmt, "weightGrams")
        val _columnIndexOfHeightCm: Int = getColumnIndexOrThrow(_stmt, "heightCm")
        val _columnIndexOfGender: Int = getColumnIndexOrThrow(_stmt, "gender")
        val _columnIndexOfColor: Int = getColumnIndexOrThrow(_stmt, "color")
        val _columnIndexOfBreed: Int = getColumnIndexOrThrow(_stmt, "breed")
        val _columnIndexOfRaisingPurpose: Int = getColumnIndexOrThrow(_stmt, "raisingPurpose")
        val _columnIndexOfHealthStatus: Int = getColumnIndexOrThrow(_stmt, "healthStatus")
        val _columnIndexOfBirdCode: Int = getColumnIndexOrThrow(_stmt, "birdCode")
        val _columnIndexOfColorTag: Int = getColumnIndexOrThrow(_stmt, "colorTag")
        val _columnIndexOfFamilyTreeId: Int = getColumnIndexOrThrow(_stmt, "familyTreeId")
        val _columnIndexOfSourceAssetId: Int = getColumnIndexOrThrow(_stmt, "sourceAssetId")
        val _columnIndexOfParentIdsJson: Int = getColumnIndexOrThrow(_stmt, "parentIdsJson")
        val _columnIndexOfBreedingStatus: Int = getColumnIndexOrThrow(_stmt, "breedingStatus")
        val _columnIndexOfTransferHistoryJson: Int = getColumnIndexOrThrow(_stmt,
            "transferHistoryJson")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfLastModifiedAt: Int = getColumnIndexOrThrow(_stmt, "lastModifiedAt")
        val _columnIndexOfIsDeleted: Int = getColumnIndexOrThrow(_stmt, "isDeleted")
        val _columnIndexOfDeletedAt: Int = getColumnIndexOrThrow(_stmt, "deletedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfStage: Int = getColumnIndexOrThrow(_stmt, "stage")
        val _columnIndexOfLifecycleStatus: Int = getColumnIndexOrThrow(_stmt, "lifecycleStatus")
        val _columnIndexOfParentMaleId: Int = getColumnIndexOrThrow(_stmt, "parentMaleId")
        val _columnIndexOfParentFemaleId: Int = getColumnIndexOrThrow(_stmt, "parentFemaleId")
        val _columnIndexOfAgeWeeks: Int = getColumnIndexOrThrow(_stmt, "ageWeeks")
        val _columnIndexOfLastStageTransitionAt: Int = getColumnIndexOrThrow(_stmt,
            "lastStageTransitionAt")
        val _columnIndexOfBreederEligibleAt: Int = getColumnIndexOrThrow(_stmt, "breederEligibleAt")
        val _columnIndexOfIsBatch: Int = getColumnIndexOrThrow(_stmt, "isBatch")
        val _columnIndexOfBatchId: Int = getColumnIndexOrThrow(_stmt, "batchId")
        val _columnIndexOfSplitAt: Int = getColumnIndexOrThrow(_stmt, "splitAt")
        val _columnIndexOfSplitIntoIds: Int = getColumnIndexOrThrow(_stmt, "splitIntoIds")
        val _columnIndexOfDocumentUrls: Int = getColumnIndexOrThrow(_stmt, "documentUrls")
        val _columnIndexOfQrCodeUrl: Int = getColumnIndexOrThrow(_stmt, "qrCodeUrl")
        val _columnIndexOfCustomStatus: Int = getColumnIndexOrThrow(_stmt, "customStatus")
        val _columnIndexOfDebug: Int = getColumnIndexOrThrow(_stmt, "debug")
        val _columnIndexOfDeliveryOptions: Int = getColumnIndexOrThrow(_stmt, "deliveryOptions")
        val _columnIndexOfDeliveryCost: Int = getColumnIndexOrThrow(_stmt, "deliveryCost")
        val _columnIndexOfLeadTimeDays: Int = getColumnIndexOrThrow(_stmt, "leadTimeDays")
        val _columnIndexOfMotherId: Int = getColumnIndexOrThrow(_stmt, "motherId")
        val _columnIndexOfIsBreedingUnit: Int = getColumnIndexOrThrow(_stmt, "isBreedingUnit")
        val _columnIndexOfEggsCollectedToday: Int = getColumnIndexOrThrow(_stmt,
            "eggsCollectedToday")
        val _columnIndexOfLastEggLogDate: Int = getColumnIndexOrThrow(_stmt, "lastEggLogDate")
        val _columnIndexOfReadyForSale: Int = getColumnIndexOrThrow(_stmt, "readyForSale")
        val _columnIndexOfTargetWeight: Int = getColumnIndexOrThrow(_stmt, "targetWeight")
        val _columnIndexOfIsShowcased: Int = getColumnIndexOrThrow(_stmt, "isShowcased")
        val _columnIndexOfExternalVideoUrl: Int = getColumnIndexOrThrow(_stmt, "externalVideoUrl")
        val _columnIndexOfRecordsLockedAt: Int = getColumnIndexOrThrow(_stmt, "recordsLockedAt")
        val _columnIndexOfAutoLockAfterDays: Int = getColumnIndexOrThrow(_stmt, "autoLockAfterDays")
        val _columnIndexOfLineageHistoryJson: Int = getColumnIndexOrThrow(_stmt,
            "lineageHistoryJson")
        val _columnIndexOfEditCount: Int = getColumnIndexOrThrow(_stmt, "editCount")
        val _columnIndexOfLastEditedBy: Int = getColumnIndexOrThrow(_stmt, "lastEditedBy")
        val _columnIndexOfAdminFlagged: Int = getColumnIndexOrThrow(_stmt, "adminFlagged")
        val _columnIndexOfModerationNote: Int = getColumnIndexOrThrow(_stmt, "moderationNote")
        val _columnIndexOfMetadataJson: Int = getColumnIndexOrThrow(_stmt, "metadataJson")
        val _result: MutableList<ProductEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: ProductEntity
          val _tmpProductId: String
          _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          val _tmpSellerId: String
          _tmpSellerId = _stmt.getText(_columnIndexOfSellerId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpDescription: String
          _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          val _tmpCategory: String
          _tmpCategory = _stmt.getText(_columnIndexOfCategory)
          val _tmpPrice: Double
          _tmpPrice = _stmt.getDouble(_columnIndexOfPrice)
          val _tmpQuantity: Double
          _tmpQuantity = _stmt.getDouble(_columnIndexOfQuantity)
          val _tmpUnit: String
          _tmpUnit = _stmt.getText(_columnIndexOfUnit)
          val _tmpLocation: String
          _tmpLocation = _stmt.getText(_columnIndexOfLocation)
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
          val _tmpImageUrls: List<String>
          val _tmp: String?
          if (_stmt.isNull(_columnIndexOfImageUrls)) {
            _tmp = null
          } else {
            _tmp = _stmt.getText(_columnIndexOfImageUrls)
          }
          val _tmp_1: List<String>? = AppDatabase.Converters.toStringList(_tmp)
          if (_tmp_1 == null) {
            error("Expected NON-NULL 'kotlin.collections.List<kotlin.String>', but it was NULL.")
          } else {
            _tmpImageUrls = _tmp_1
          }
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpCondition: String?
          if (_stmt.isNull(_columnIndexOfCondition)) {
            _tmpCondition = null
          } else {
            _tmpCondition = _stmt.getText(_columnIndexOfCondition)
          }
          val _tmpHarvestDate: Long?
          if (_stmt.isNull(_columnIndexOfHarvestDate)) {
            _tmpHarvestDate = null
          } else {
            _tmpHarvestDate = _stmt.getLong(_columnIndexOfHarvestDate)
          }
          val _tmpExpiryDate: Long?
          if (_stmt.isNull(_columnIndexOfExpiryDate)) {
            _tmpExpiryDate = null
          } else {
            _tmpExpiryDate = _stmt.getLong(_columnIndexOfExpiryDate)
          }
          val _tmpBirthDate: Long?
          if (_stmt.isNull(_columnIndexOfBirthDate)) {
            _tmpBirthDate = null
          } else {
            _tmpBirthDate = _stmt.getLong(_columnIndexOfBirthDate)
          }
          val _tmpVaccinationRecordsJson: String?
          if (_stmt.isNull(_columnIndexOfVaccinationRecordsJson)) {
            _tmpVaccinationRecordsJson = null
          } else {
            _tmpVaccinationRecordsJson = _stmt.getText(_columnIndexOfVaccinationRecordsJson)
          }
          val _tmpWeightGrams: Double?
          if (_stmt.isNull(_columnIndexOfWeightGrams)) {
            _tmpWeightGrams = null
          } else {
            _tmpWeightGrams = _stmt.getDouble(_columnIndexOfWeightGrams)
          }
          val _tmpHeightCm: Double?
          if (_stmt.isNull(_columnIndexOfHeightCm)) {
            _tmpHeightCm = null
          } else {
            _tmpHeightCm = _stmt.getDouble(_columnIndexOfHeightCm)
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
          val _tmpBreed: String?
          if (_stmt.isNull(_columnIndexOfBreed)) {
            _tmpBreed = null
          } else {
            _tmpBreed = _stmt.getText(_columnIndexOfBreed)
          }
          val _tmpRaisingPurpose: String?
          if (_stmt.isNull(_columnIndexOfRaisingPurpose)) {
            _tmpRaisingPurpose = null
          } else {
            _tmpRaisingPurpose = _stmt.getText(_columnIndexOfRaisingPurpose)
          }
          val _tmpHealthStatus: String?
          if (_stmt.isNull(_columnIndexOfHealthStatus)) {
            _tmpHealthStatus = null
          } else {
            _tmpHealthStatus = _stmt.getText(_columnIndexOfHealthStatus)
          }
          val _tmpBirdCode: String?
          if (_stmt.isNull(_columnIndexOfBirdCode)) {
            _tmpBirdCode = null
          } else {
            _tmpBirdCode = _stmt.getText(_columnIndexOfBirdCode)
          }
          val _tmpColorTag: String?
          if (_stmt.isNull(_columnIndexOfColorTag)) {
            _tmpColorTag = null
          } else {
            _tmpColorTag = _stmt.getText(_columnIndexOfColorTag)
          }
          val _tmpFamilyTreeId: String?
          if (_stmt.isNull(_columnIndexOfFamilyTreeId)) {
            _tmpFamilyTreeId = null
          } else {
            _tmpFamilyTreeId = _stmt.getText(_columnIndexOfFamilyTreeId)
          }
          val _tmpSourceAssetId: String?
          if (_stmt.isNull(_columnIndexOfSourceAssetId)) {
            _tmpSourceAssetId = null
          } else {
            _tmpSourceAssetId = _stmt.getText(_columnIndexOfSourceAssetId)
          }
          val _tmpParentIdsJson: String?
          if (_stmt.isNull(_columnIndexOfParentIdsJson)) {
            _tmpParentIdsJson = null
          } else {
            _tmpParentIdsJson = _stmt.getText(_columnIndexOfParentIdsJson)
          }
          val _tmpBreedingStatus: String?
          if (_stmt.isNull(_columnIndexOfBreedingStatus)) {
            _tmpBreedingStatus = null
          } else {
            _tmpBreedingStatus = _stmt.getText(_columnIndexOfBreedingStatus)
          }
          val _tmpTransferHistoryJson: String?
          if (_stmt.isNull(_columnIndexOfTransferHistoryJson)) {
            _tmpTransferHistoryJson = null
          } else {
            _tmpTransferHistoryJson = _stmt.getText(_columnIndexOfTransferHistoryJson)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpLastModifiedAt: Long
          _tmpLastModifiedAt = _stmt.getLong(_columnIndexOfLastModifiedAt)
          val _tmpIsDeleted: Boolean
          val _tmp_2: Int
          _tmp_2 = _stmt.getLong(_columnIndexOfIsDeleted).toInt()
          _tmpIsDeleted = _tmp_2 != 0
          val _tmpDeletedAt: Long?
          if (_stmt.isNull(_columnIndexOfDeletedAt)) {
            _tmpDeletedAt = null
          } else {
            _tmpDeletedAt = _stmt.getLong(_columnIndexOfDeletedAt)
          }
          val _tmpDirty: Boolean
          val _tmp_3: Int
          _tmp_3 = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp_3 != 0
          val _tmpStage: LifecycleStage?
          val _tmp_4: String?
          if (_stmt.isNull(_columnIndexOfStage)) {
            _tmp_4 = null
          } else {
            _tmp_4 = _stmt.getText(_columnIndexOfStage)
          }
          _tmpStage = AppDatabase.Converters.toLifecycleStage(_tmp_4)
          val _tmpLifecycleStatus: String?
          if (_stmt.isNull(_columnIndexOfLifecycleStatus)) {
            _tmpLifecycleStatus = null
          } else {
            _tmpLifecycleStatus = _stmt.getText(_columnIndexOfLifecycleStatus)
          }
          val _tmpParentMaleId: String?
          if (_stmt.isNull(_columnIndexOfParentMaleId)) {
            _tmpParentMaleId = null
          } else {
            _tmpParentMaleId = _stmt.getText(_columnIndexOfParentMaleId)
          }
          val _tmpParentFemaleId: String?
          if (_stmt.isNull(_columnIndexOfParentFemaleId)) {
            _tmpParentFemaleId = null
          } else {
            _tmpParentFemaleId = _stmt.getText(_columnIndexOfParentFemaleId)
          }
          val _tmpAgeWeeks: Int?
          if (_stmt.isNull(_columnIndexOfAgeWeeks)) {
            _tmpAgeWeeks = null
          } else {
            _tmpAgeWeeks = _stmt.getLong(_columnIndexOfAgeWeeks).toInt()
          }
          val _tmpLastStageTransitionAt: Long?
          if (_stmt.isNull(_columnIndexOfLastStageTransitionAt)) {
            _tmpLastStageTransitionAt = null
          } else {
            _tmpLastStageTransitionAt = _stmt.getLong(_columnIndexOfLastStageTransitionAt)
          }
          val _tmpBreederEligibleAt: Long?
          if (_stmt.isNull(_columnIndexOfBreederEligibleAt)) {
            _tmpBreederEligibleAt = null
          } else {
            _tmpBreederEligibleAt = _stmt.getLong(_columnIndexOfBreederEligibleAt)
          }
          val _tmpIsBatch: Boolean?
          val _tmp_5: Int?
          if (_stmt.isNull(_columnIndexOfIsBatch)) {
            _tmp_5 = null
          } else {
            _tmp_5 = _stmt.getLong(_columnIndexOfIsBatch).toInt()
          }
          _tmpIsBatch = _tmp_5?.let { it != 0 }
          val _tmpBatchId: String?
          if (_stmt.isNull(_columnIndexOfBatchId)) {
            _tmpBatchId = null
          } else {
            _tmpBatchId = _stmt.getText(_columnIndexOfBatchId)
          }
          val _tmpSplitAt: Long?
          if (_stmt.isNull(_columnIndexOfSplitAt)) {
            _tmpSplitAt = null
          } else {
            _tmpSplitAt = _stmt.getLong(_columnIndexOfSplitAt)
          }
          val _tmpSplitIntoIds: String?
          if (_stmt.isNull(_columnIndexOfSplitIntoIds)) {
            _tmpSplitIntoIds = null
          } else {
            _tmpSplitIntoIds = _stmt.getText(_columnIndexOfSplitIntoIds)
          }
          val _tmpDocumentUrls: List<String>
          val _tmp_6: String?
          if (_stmt.isNull(_columnIndexOfDocumentUrls)) {
            _tmp_6 = null
          } else {
            _tmp_6 = _stmt.getText(_columnIndexOfDocumentUrls)
          }
          val _tmp_7: List<String>? = AppDatabase.Converters.toStringList(_tmp_6)
          if (_tmp_7 == null) {
            error("Expected NON-NULL 'kotlin.collections.List<kotlin.String>', but it was NULL.")
          } else {
            _tmpDocumentUrls = _tmp_7
          }
          val _tmpQrCodeUrl: String?
          if (_stmt.isNull(_columnIndexOfQrCodeUrl)) {
            _tmpQrCodeUrl = null
          } else {
            _tmpQrCodeUrl = _stmt.getText(_columnIndexOfQrCodeUrl)
          }
          val _tmpCustomStatus: String?
          if (_stmt.isNull(_columnIndexOfCustomStatus)) {
            _tmpCustomStatus = null
          } else {
            _tmpCustomStatus = _stmt.getText(_columnIndexOfCustomStatus)
          }
          val _tmpDebug: Boolean
          val _tmp_8: Int
          _tmp_8 = _stmt.getLong(_columnIndexOfDebug).toInt()
          _tmpDebug = _tmp_8 != 0
          val _tmpDeliveryOptions: List<String>
          val _tmp_9: String?
          if (_stmt.isNull(_columnIndexOfDeliveryOptions)) {
            _tmp_9 = null
          } else {
            _tmp_9 = _stmt.getText(_columnIndexOfDeliveryOptions)
          }
          val _tmp_10: List<String>? = AppDatabase.Converters.toStringList(_tmp_9)
          if (_tmp_10 == null) {
            error("Expected NON-NULL 'kotlin.collections.List<kotlin.String>', but it was NULL.")
          } else {
            _tmpDeliveryOptions = _tmp_10
          }
          val _tmpDeliveryCost: Double?
          if (_stmt.isNull(_columnIndexOfDeliveryCost)) {
            _tmpDeliveryCost = null
          } else {
            _tmpDeliveryCost = _stmt.getDouble(_columnIndexOfDeliveryCost)
          }
          val _tmpLeadTimeDays: Int?
          if (_stmt.isNull(_columnIndexOfLeadTimeDays)) {
            _tmpLeadTimeDays = null
          } else {
            _tmpLeadTimeDays = _stmt.getLong(_columnIndexOfLeadTimeDays).toInt()
          }
          val _tmpMotherId: String?
          if (_stmt.isNull(_columnIndexOfMotherId)) {
            _tmpMotherId = null
          } else {
            _tmpMotherId = _stmt.getText(_columnIndexOfMotherId)
          }
          val _tmpIsBreedingUnit: Boolean
          val _tmp_11: Int
          _tmp_11 = _stmt.getLong(_columnIndexOfIsBreedingUnit).toInt()
          _tmpIsBreedingUnit = _tmp_11 != 0
          val _tmpEggsCollectedToday: Int
          _tmpEggsCollectedToday = _stmt.getLong(_columnIndexOfEggsCollectedToday).toInt()
          val _tmpLastEggLogDate: Long?
          if (_stmt.isNull(_columnIndexOfLastEggLogDate)) {
            _tmpLastEggLogDate = null
          } else {
            _tmpLastEggLogDate = _stmt.getLong(_columnIndexOfLastEggLogDate)
          }
          val _tmpReadyForSale: Boolean
          val _tmp_12: Int
          _tmp_12 = _stmt.getLong(_columnIndexOfReadyForSale).toInt()
          _tmpReadyForSale = _tmp_12 != 0
          val _tmpTargetWeight: Double?
          if (_stmt.isNull(_columnIndexOfTargetWeight)) {
            _tmpTargetWeight = null
          } else {
            _tmpTargetWeight = _stmt.getDouble(_columnIndexOfTargetWeight)
          }
          val _tmpIsShowcased: Boolean
          val _tmp_13: Int
          _tmp_13 = _stmt.getLong(_columnIndexOfIsShowcased).toInt()
          _tmpIsShowcased = _tmp_13 != 0
          val _tmpExternalVideoUrl: String?
          if (_stmt.isNull(_columnIndexOfExternalVideoUrl)) {
            _tmpExternalVideoUrl = null
          } else {
            _tmpExternalVideoUrl = _stmt.getText(_columnIndexOfExternalVideoUrl)
          }
          val _tmpRecordsLockedAt: Long?
          if (_stmt.isNull(_columnIndexOfRecordsLockedAt)) {
            _tmpRecordsLockedAt = null
          } else {
            _tmpRecordsLockedAt = _stmt.getLong(_columnIndexOfRecordsLockedAt)
          }
          val _tmpAutoLockAfterDays: Int
          _tmpAutoLockAfterDays = _stmt.getLong(_columnIndexOfAutoLockAfterDays).toInt()
          val _tmpLineageHistoryJson: String?
          if (_stmt.isNull(_columnIndexOfLineageHistoryJson)) {
            _tmpLineageHistoryJson = null
          } else {
            _tmpLineageHistoryJson = _stmt.getText(_columnIndexOfLineageHistoryJson)
          }
          val _tmpEditCount: Int
          _tmpEditCount = _stmt.getLong(_columnIndexOfEditCount).toInt()
          val _tmpLastEditedBy: String?
          if (_stmt.isNull(_columnIndexOfLastEditedBy)) {
            _tmpLastEditedBy = null
          } else {
            _tmpLastEditedBy = _stmt.getText(_columnIndexOfLastEditedBy)
          }
          val _tmpAdminFlagged: Boolean
          val _tmp_14: Int
          _tmp_14 = _stmt.getLong(_columnIndexOfAdminFlagged).toInt()
          _tmpAdminFlagged = _tmp_14 != 0
          val _tmpModerationNote: String?
          if (_stmt.isNull(_columnIndexOfModerationNote)) {
            _tmpModerationNote = null
          } else {
            _tmpModerationNote = _stmt.getText(_columnIndexOfModerationNote)
          }
          val _tmpMetadataJson: String
          _tmpMetadataJson = _stmt.getText(_columnIndexOfMetadataJson)
          _item =
              ProductEntity(_tmpProductId,_tmpSellerId,_tmpName,_tmpDescription,_tmpCategory,_tmpPrice,_tmpQuantity,_tmpUnit,_tmpLocation,_tmpLatitude,_tmpLongitude,_tmpImageUrls,_tmpStatus,_tmpCondition,_tmpHarvestDate,_tmpExpiryDate,_tmpBirthDate,_tmpVaccinationRecordsJson,_tmpWeightGrams,_tmpHeightCm,_tmpGender,_tmpColor,_tmpBreed,_tmpRaisingPurpose,_tmpHealthStatus,_tmpBirdCode,_tmpColorTag,_tmpFamilyTreeId,_tmpSourceAssetId,_tmpParentIdsJson,_tmpBreedingStatus,_tmpTransferHistoryJson,_tmpCreatedAt,_tmpUpdatedAt,_tmpLastModifiedAt,_tmpIsDeleted,_tmpDeletedAt,_tmpDirty,_tmpStage,_tmpLifecycleStatus,_tmpParentMaleId,_tmpParentFemaleId,_tmpAgeWeeks,_tmpLastStageTransitionAt,_tmpBreederEligibleAt,_tmpIsBatch,_tmpBatchId,_tmpSplitAt,_tmpSplitIntoIds,_tmpDocumentUrls,_tmpQrCodeUrl,_tmpCustomStatus,_tmpDebug,_tmpDeliveryOptions,_tmpDeliveryCost,_tmpLeadTimeDays,_tmpMotherId,_tmpIsBreedingUnit,_tmpEggsCollectedToday,_tmpLastEggLogDate,_tmpReadyForSale,_tmpTargetWeight,_tmpIsShowcased,_tmpExternalVideoUrl,_tmpRecordsLockedAt,_tmpAutoLockAfterDays,_tmpLineageHistoryJson,_tmpEditCount,_tmpLastEditedBy,_tmpAdminFlagged,_tmpModerationNote,_tmpMetadataJson)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getProductsByStatus(status: String): Flow<List<ProductEntity>> {
    val _sql: String = "SELECT * FROM products WHERE status = ? ORDER BY createdAt DESC"
    return createFlow(__db, false, arrayOf("products")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, status)
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfSellerId: Int = getColumnIndexOrThrow(_stmt, "sellerId")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfCategory: Int = getColumnIndexOrThrow(_stmt, "category")
        val _columnIndexOfPrice: Int = getColumnIndexOrThrow(_stmt, "price")
        val _columnIndexOfQuantity: Int = getColumnIndexOrThrow(_stmt, "quantity")
        val _columnIndexOfUnit: Int = getColumnIndexOrThrow(_stmt, "unit")
        val _columnIndexOfLocation: Int = getColumnIndexOrThrow(_stmt, "location")
        val _columnIndexOfLatitude: Int = getColumnIndexOrThrow(_stmt, "latitude")
        val _columnIndexOfLongitude: Int = getColumnIndexOrThrow(_stmt, "longitude")
        val _columnIndexOfImageUrls: Int = getColumnIndexOrThrow(_stmt, "imageUrls")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfCondition: Int = getColumnIndexOrThrow(_stmt, "condition")
        val _columnIndexOfHarvestDate: Int = getColumnIndexOrThrow(_stmt, "harvestDate")
        val _columnIndexOfExpiryDate: Int = getColumnIndexOrThrow(_stmt, "expiryDate")
        val _columnIndexOfBirthDate: Int = getColumnIndexOrThrow(_stmt, "birthDate")
        val _columnIndexOfVaccinationRecordsJson: Int = getColumnIndexOrThrow(_stmt,
            "vaccinationRecordsJson")
        val _columnIndexOfWeightGrams: Int = getColumnIndexOrThrow(_stmt, "weightGrams")
        val _columnIndexOfHeightCm: Int = getColumnIndexOrThrow(_stmt, "heightCm")
        val _columnIndexOfGender: Int = getColumnIndexOrThrow(_stmt, "gender")
        val _columnIndexOfColor: Int = getColumnIndexOrThrow(_stmt, "color")
        val _columnIndexOfBreed: Int = getColumnIndexOrThrow(_stmt, "breed")
        val _columnIndexOfRaisingPurpose: Int = getColumnIndexOrThrow(_stmt, "raisingPurpose")
        val _columnIndexOfHealthStatus: Int = getColumnIndexOrThrow(_stmt, "healthStatus")
        val _columnIndexOfBirdCode: Int = getColumnIndexOrThrow(_stmt, "birdCode")
        val _columnIndexOfColorTag: Int = getColumnIndexOrThrow(_stmt, "colorTag")
        val _columnIndexOfFamilyTreeId: Int = getColumnIndexOrThrow(_stmt, "familyTreeId")
        val _columnIndexOfSourceAssetId: Int = getColumnIndexOrThrow(_stmt, "sourceAssetId")
        val _columnIndexOfParentIdsJson: Int = getColumnIndexOrThrow(_stmt, "parentIdsJson")
        val _columnIndexOfBreedingStatus: Int = getColumnIndexOrThrow(_stmt, "breedingStatus")
        val _columnIndexOfTransferHistoryJson: Int = getColumnIndexOrThrow(_stmt,
            "transferHistoryJson")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfLastModifiedAt: Int = getColumnIndexOrThrow(_stmt, "lastModifiedAt")
        val _columnIndexOfIsDeleted: Int = getColumnIndexOrThrow(_stmt, "isDeleted")
        val _columnIndexOfDeletedAt: Int = getColumnIndexOrThrow(_stmt, "deletedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfStage: Int = getColumnIndexOrThrow(_stmt, "stage")
        val _columnIndexOfLifecycleStatus: Int = getColumnIndexOrThrow(_stmt, "lifecycleStatus")
        val _columnIndexOfParentMaleId: Int = getColumnIndexOrThrow(_stmt, "parentMaleId")
        val _columnIndexOfParentFemaleId: Int = getColumnIndexOrThrow(_stmt, "parentFemaleId")
        val _columnIndexOfAgeWeeks: Int = getColumnIndexOrThrow(_stmt, "ageWeeks")
        val _columnIndexOfLastStageTransitionAt: Int = getColumnIndexOrThrow(_stmt,
            "lastStageTransitionAt")
        val _columnIndexOfBreederEligibleAt: Int = getColumnIndexOrThrow(_stmt, "breederEligibleAt")
        val _columnIndexOfIsBatch: Int = getColumnIndexOrThrow(_stmt, "isBatch")
        val _columnIndexOfBatchId: Int = getColumnIndexOrThrow(_stmt, "batchId")
        val _columnIndexOfSplitAt: Int = getColumnIndexOrThrow(_stmt, "splitAt")
        val _columnIndexOfSplitIntoIds: Int = getColumnIndexOrThrow(_stmt, "splitIntoIds")
        val _columnIndexOfDocumentUrls: Int = getColumnIndexOrThrow(_stmt, "documentUrls")
        val _columnIndexOfQrCodeUrl: Int = getColumnIndexOrThrow(_stmt, "qrCodeUrl")
        val _columnIndexOfCustomStatus: Int = getColumnIndexOrThrow(_stmt, "customStatus")
        val _columnIndexOfDebug: Int = getColumnIndexOrThrow(_stmt, "debug")
        val _columnIndexOfDeliveryOptions: Int = getColumnIndexOrThrow(_stmt, "deliveryOptions")
        val _columnIndexOfDeliveryCost: Int = getColumnIndexOrThrow(_stmt, "deliveryCost")
        val _columnIndexOfLeadTimeDays: Int = getColumnIndexOrThrow(_stmt, "leadTimeDays")
        val _columnIndexOfMotherId: Int = getColumnIndexOrThrow(_stmt, "motherId")
        val _columnIndexOfIsBreedingUnit: Int = getColumnIndexOrThrow(_stmt, "isBreedingUnit")
        val _columnIndexOfEggsCollectedToday: Int = getColumnIndexOrThrow(_stmt,
            "eggsCollectedToday")
        val _columnIndexOfLastEggLogDate: Int = getColumnIndexOrThrow(_stmt, "lastEggLogDate")
        val _columnIndexOfReadyForSale: Int = getColumnIndexOrThrow(_stmt, "readyForSale")
        val _columnIndexOfTargetWeight: Int = getColumnIndexOrThrow(_stmt, "targetWeight")
        val _columnIndexOfIsShowcased: Int = getColumnIndexOrThrow(_stmt, "isShowcased")
        val _columnIndexOfExternalVideoUrl: Int = getColumnIndexOrThrow(_stmt, "externalVideoUrl")
        val _columnIndexOfRecordsLockedAt: Int = getColumnIndexOrThrow(_stmt, "recordsLockedAt")
        val _columnIndexOfAutoLockAfterDays: Int = getColumnIndexOrThrow(_stmt, "autoLockAfterDays")
        val _columnIndexOfLineageHistoryJson: Int = getColumnIndexOrThrow(_stmt,
            "lineageHistoryJson")
        val _columnIndexOfEditCount: Int = getColumnIndexOrThrow(_stmt, "editCount")
        val _columnIndexOfLastEditedBy: Int = getColumnIndexOrThrow(_stmt, "lastEditedBy")
        val _columnIndexOfAdminFlagged: Int = getColumnIndexOrThrow(_stmt, "adminFlagged")
        val _columnIndexOfModerationNote: Int = getColumnIndexOrThrow(_stmt, "moderationNote")
        val _columnIndexOfMetadataJson: Int = getColumnIndexOrThrow(_stmt, "metadataJson")
        val _result: MutableList<ProductEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: ProductEntity
          val _tmpProductId: String
          _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          val _tmpSellerId: String
          _tmpSellerId = _stmt.getText(_columnIndexOfSellerId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpDescription: String
          _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          val _tmpCategory: String
          _tmpCategory = _stmt.getText(_columnIndexOfCategory)
          val _tmpPrice: Double
          _tmpPrice = _stmt.getDouble(_columnIndexOfPrice)
          val _tmpQuantity: Double
          _tmpQuantity = _stmt.getDouble(_columnIndexOfQuantity)
          val _tmpUnit: String
          _tmpUnit = _stmt.getText(_columnIndexOfUnit)
          val _tmpLocation: String
          _tmpLocation = _stmt.getText(_columnIndexOfLocation)
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
          val _tmpImageUrls: List<String>
          val _tmp: String?
          if (_stmt.isNull(_columnIndexOfImageUrls)) {
            _tmp = null
          } else {
            _tmp = _stmt.getText(_columnIndexOfImageUrls)
          }
          val _tmp_1: List<String>? = AppDatabase.Converters.toStringList(_tmp)
          if (_tmp_1 == null) {
            error("Expected NON-NULL 'kotlin.collections.List<kotlin.String>', but it was NULL.")
          } else {
            _tmpImageUrls = _tmp_1
          }
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpCondition: String?
          if (_stmt.isNull(_columnIndexOfCondition)) {
            _tmpCondition = null
          } else {
            _tmpCondition = _stmt.getText(_columnIndexOfCondition)
          }
          val _tmpHarvestDate: Long?
          if (_stmt.isNull(_columnIndexOfHarvestDate)) {
            _tmpHarvestDate = null
          } else {
            _tmpHarvestDate = _stmt.getLong(_columnIndexOfHarvestDate)
          }
          val _tmpExpiryDate: Long?
          if (_stmt.isNull(_columnIndexOfExpiryDate)) {
            _tmpExpiryDate = null
          } else {
            _tmpExpiryDate = _stmt.getLong(_columnIndexOfExpiryDate)
          }
          val _tmpBirthDate: Long?
          if (_stmt.isNull(_columnIndexOfBirthDate)) {
            _tmpBirthDate = null
          } else {
            _tmpBirthDate = _stmt.getLong(_columnIndexOfBirthDate)
          }
          val _tmpVaccinationRecordsJson: String?
          if (_stmt.isNull(_columnIndexOfVaccinationRecordsJson)) {
            _tmpVaccinationRecordsJson = null
          } else {
            _tmpVaccinationRecordsJson = _stmt.getText(_columnIndexOfVaccinationRecordsJson)
          }
          val _tmpWeightGrams: Double?
          if (_stmt.isNull(_columnIndexOfWeightGrams)) {
            _tmpWeightGrams = null
          } else {
            _tmpWeightGrams = _stmt.getDouble(_columnIndexOfWeightGrams)
          }
          val _tmpHeightCm: Double?
          if (_stmt.isNull(_columnIndexOfHeightCm)) {
            _tmpHeightCm = null
          } else {
            _tmpHeightCm = _stmt.getDouble(_columnIndexOfHeightCm)
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
          val _tmpBreed: String?
          if (_stmt.isNull(_columnIndexOfBreed)) {
            _tmpBreed = null
          } else {
            _tmpBreed = _stmt.getText(_columnIndexOfBreed)
          }
          val _tmpRaisingPurpose: String?
          if (_stmt.isNull(_columnIndexOfRaisingPurpose)) {
            _tmpRaisingPurpose = null
          } else {
            _tmpRaisingPurpose = _stmt.getText(_columnIndexOfRaisingPurpose)
          }
          val _tmpHealthStatus: String?
          if (_stmt.isNull(_columnIndexOfHealthStatus)) {
            _tmpHealthStatus = null
          } else {
            _tmpHealthStatus = _stmt.getText(_columnIndexOfHealthStatus)
          }
          val _tmpBirdCode: String?
          if (_stmt.isNull(_columnIndexOfBirdCode)) {
            _tmpBirdCode = null
          } else {
            _tmpBirdCode = _stmt.getText(_columnIndexOfBirdCode)
          }
          val _tmpColorTag: String?
          if (_stmt.isNull(_columnIndexOfColorTag)) {
            _tmpColorTag = null
          } else {
            _tmpColorTag = _stmt.getText(_columnIndexOfColorTag)
          }
          val _tmpFamilyTreeId: String?
          if (_stmt.isNull(_columnIndexOfFamilyTreeId)) {
            _tmpFamilyTreeId = null
          } else {
            _tmpFamilyTreeId = _stmt.getText(_columnIndexOfFamilyTreeId)
          }
          val _tmpSourceAssetId: String?
          if (_stmt.isNull(_columnIndexOfSourceAssetId)) {
            _tmpSourceAssetId = null
          } else {
            _tmpSourceAssetId = _stmt.getText(_columnIndexOfSourceAssetId)
          }
          val _tmpParentIdsJson: String?
          if (_stmt.isNull(_columnIndexOfParentIdsJson)) {
            _tmpParentIdsJson = null
          } else {
            _tmpParentIdsJson = _stmt.getText(_columnIndexOfParentIdsJson)
          }
          val _tmpBreedingStatus: String?
          if (_stmt.isNull(_columnIndexOfBreedingStatus)) {
            _tmpBreedingStatus = null
          } else {
            _tmpBreedingStatus = _stmt.getText(_columnIndexOfBreedingStatus)
          }
          val _tmpTransferHistoryJson: String?
          if (_stmt.isNull(_columnIndexOfTransferHistoryJson)) {
            _tmpTransferHistoryJson = null
          } else {
            _tmpTransferHistoryJson = _stmt.getText(_columnIndexOfTransferHistoryJson)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpLastModifiedAt: Long
          _tmpLastModifiedAt = _stmt.getLong(_columnIndexOfLastModifiedAt)
          val _tmpIsDeleted: Boolean
          val _tmp_2: Int
          _tmp_2 = _stmt.getLong(_columnIndexOfIsDeleted).toInt()
          _tmpIsDeleted = _tmp_2 != 0
          val _tmpDeletedAt: Long?
          if (_stmt.isNull(_columnIndexOfDeletedAt)) {
            _tmpDeletedAt = null
          } else {
            _tmpDeletedAt = _stmt.getLong(_columnIndexOfDeletedAt)
          }
          val _tmpDirty: Boolean
          val _tmp_3: Int
          _tmp_3 = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp_3 != 0
          val _tmpStage: LifecycleStage?
          val _tmp_4: String?
          if (_stmt.isNull(_columnIndexOfStage)) {
            _tmp_4 = null
          } else {
            _tmp_4 = _stmt.getText(_columnIndexOfStage)
          }
          _tmpStage = AppDatabase.Converters.toLifecycleStage(_tmp_4)
          val _tmpLifecycleStatus: String?
          if (_stmt.isNull(_columnIndexOfLifecycleStatus)) {
            _tmpLifecycleStatus = null
          } else {
            _tmpLifecycleStatus = _stmt.getText(_columnIndexOfLifecycleStatus)
          }
          val _tmpParentMaleId: String?
          if (_stmt.isNull(_columnIndexOfParentMaleId)) {
            _tmpParentMaleId = null
          } else {
            _tmpParentMaleId = _stmt.getText(_columnIndexOfParentMaleId)
          }
          val _tmpParentFemaleId: String?
          if (_stmt.isNull(_columnIndexOfParentFemaleId)) {
            _tmpParentFemaleId = null
          } else {
            _tmpParentFemaleId = _stmt.getText(_columnIndexOfParentFemaleId)
          }
          val _tmpAgeWeeks: Int?
          if (_stmt.isNull(_columnIndexOfAgeWeeks)) {
            _tmpAgeWeeks = null
          } else {
            _tmpAgeWeeks = _stmt.getLong(_columnIndexOfAgeWeeks).toInt()
          }
          val _tmpLastStageTransitionAt: Long?
          if (_stmt.isNull(_columnIndexOfLastStageTransitionAt)) {
            _tmpLastStageTransitionAt = null
          } else {
            _tmpLastStageTransitionAt = _stmt.getLong(_columnIndexOfLastStageTransitionAt)
          }
          val _tmpBreederEligibleAt: Long?
          if (_stmt.isNull(_columnIndexOfBreederEligibleAt)) {
            _tmpBreederEligibleAt = null
          } else {
            _tmpBreederEligibleAt = _stmt.getLong(_columnIndexOfBreederEligibleAt)
          }
          val _tmpIsBatch: Boolean?
          val _tmp_5: Int?
          if (_stmt.isNull(_columnIndexOfIsBatch)) {
            _tmp_5 = null
          } else {
            _tmp_5 = _stmt.getLong(_columnIndexOfIsBatch).toInt()
          }
          _tmpIsBatch = _tmp_5?.let { it != 0 }
          val _tmpBatchId: String?
          if (_stmt.isNull(_columnIndexOfBatchId)) {
            _tmpBatchId = null
          } else {
            _tmpBatchId = _stmt.getText(_columnIndexOfBatchId)
          }
          val _tmpSplitAt: Long?
          if (_stmt.isNull(_columnIndexOfSplitAt)) {
            _tmpSplitAt = null
          } else {
            _tmpSplitAt = _stmt.getLong(_columnIndexOfSplitAt)
          }
          val _tmpSplitIntoIds: String?
          if (_stmt.isNull(_columnIndexOfSplitIntoIds)) {
            _tmpSplitIntoIds = null
          } else {
            _tmpSplitIntoIds = _stmt.getText(_columnIndexOfSplitIntoIds)
          }
          val _tmpDocumentUrls: List<String>
          val _tmp_6: String?
          if (_stmt.isNull(_columnIndexOfDocumentUrls)) {
            _tmp_6 = null
          } else {
            _tmp_6 = _stmt.getText(_columnIndexOfDocumentUrls)
          }
          val _tmp_7: List<String>? = AppDatabase.Converters.toStringList(_tmp_6)
          if (_tmp_7 == null) {
            error("Expected NON-NULL 'kotlin.collections.List<kotlin.String>', but it was NULL.")
          } else {
            _tmpDocumentUrls = _tmp_7
          }
          val _tmpQrCodeUrl: String?
          if (_stmt.isNull(_columnIndexOfQrCodeUrl)) {
            _tmpQrCodeUrl = null
          } else {
            _tmpQrCodeUrl = _stmt.getText(_columnIndexOfQrCodeUrl)
          }
          val _tmpCustomStatus: String?
          if (_stmt.isNull(_columnIndexOfCustomStatus)) {
            _tmpCustomStatus = null
          } else {
            _tmpCustomStatus = _stmt.getText(_columnIndexOfCustomStatus)
          }
          val _tmpDebug: Boolean
          val _tmp_8: Int
          _tmp_8 = _stmt.getLong(_columnIndexOfDebug).toInt()
          _tmpDebug = _tmp_8 != 0
          val _tmpDeliveryOptions: List<String>
          val _tmp_9: String?
          if (_stmt.isNull(_columnIndexOfDeliveryOptions)) {
            _tmp_9 = null
          } else {
            _tmp_9 = _stmt.getText(_columnIndexOfDeliveryOptions)
          }
          val _tmp_10: List<String>? = AppDatabase.Converters.toStringList(_tmp_9)
          if (_tmp_10 == null) {
            error("Expected NON-NULL 'kotlin.collections.List<kotlin.String>', but it was NULL.")
          } else {
            _tmpDeliveryOptions = _tmp_10
          }
          val _tmpDeliveryCost: Double?
          if (_stmt.isNull(_columnIndexOfDeliveryCost)) {
            _tmpDeliveryCost = null
          } else {
            _tmpDeliveryCost = _stmt.getDouble(_columnIndexOfDeliveryCost)
          }
          val _tmpLeadTimeDays: Int?
          if (_stmt.isNull(_columnIndexOfLeadTimeDays)) {
            _tmpLeadTimeDays = null
          } else {
            _tmpLeadTimeDays = _stmt.getLong(_columnIndexOfLeadTimeDays).toInt()
          }
          val _tmpMotherId: String?
          if (_stmt.isNull(_columnIndexOfMotherId)) {
            _tmpMotherId = null
          } else {
            _tmpMotherId = _stmt.getText(_columnIndexOfMotherId)
          }
          val _tmpIsBreedingUnit: Boolean
          val _tmp_11: Int
          _tmp_11 = _stmt.getLong(_columnIndexOfIsBreedingUnit).toInt()
          _tmpIsBreedingUnit = _tmp_11 != 0
          val _tmpEggsCollectedToday: Int
          _tmpEggsCollectedToday = _stmt.getLong(_columnIndexOfEggsCollectedToday).toInt()
          val _tmpLastEggLogDate: Long?
          if (_stmt.isNull(_columnIndexOfLastEggLogDate)) {
            _tmpLastEggLogDate = null
          } else {
            _tmpLastEggLogDate = _stmt.getLong(_columnIndexOfLastEggLogDate)
          }
          val _tmpReadyForSale: Boolean
          val _tmp_12: Int
          _tmp_12 = _stmt.getLong(_columnIndexOfReadyForSale).toInt()
          _tmpReadyForSale = _tmp_12 != 0
          val _tmpTargetWeight: Double?
          if (_stmt.isNull(_columnIndexOfTargetWeight)) {
            _tmpTargetWeight = null
          } else {
            _tmpTargetWeight = _stmt.getDouble(_columnIndexOfTargetWeight)
          }
          val _tmpIsShowcased: Boolean
          val _tmp_13: Int
          _tmp_13 = _stmt.getLong(_columnIndexOfIsShowcased).toInt()
          _tmpIsShowcased = _tmp_13 != 0
          val _tmpExternalVideoUrl: String?
          if (_stmt.isNull(_columnIndexOfExternalVideoUrl)) {
            _tmpExternalVideoUrl = null
          } else {
            _tmpExternalVideoUrl = _stmt.getText(_columnIndexOfExternalVideoUrl)
          }
          val _tmpRecordsLockedAt: Long?
          if (_stmt.isNull(_columnIndexOfRecordsLockedAt)) {
            _tmpRecordsLockedAt = null
          } else {
            _tmpRecordsLockedAt = _stmt.getLong(_columnIndexOfRecordsLockedAt)
          }
          val _tmpAutoLockAfterDays: Int
          _tmpAutoLockAfterDays = _stmt.getLong(_columnIndexOfAutoLockAfterDays).toInt()
          val _tmpLineageHistoryJson: String?
          if (_stmt.isNull(_columnIndexOfLineageHistoryJson)) {
            _tmpLineageHistoryJson = null
          } else {
            _tmpLineageHistoryJson = _stmt.getText(_columnIndexOfLineageHistoryJson)
          }
          val _tmpEditCount: Int
          _tmpEditCount = _stmt.getLong(_columnIndexOfEditCount).toInt()
          val _tmpLastEditedBy: String?
          if (_stmt.isNull(_columnIndexOfLastEditedBy)) {
            _tmpLastEditedBy = null
          } else {
            _tmpLastEditedBy = _stmt.getText(_columnIndexOfLastEditedBy)
          }
          val _tmpAdminFlagged: Boolean
          val _tmp_14: Int
          _tmp_14 = _stmt.getLong(_columnIndexOfAdminFlagged).toInt()
          _tmpAdminFlagged = _tmp_14 != 0
          val _tmpModerationNote: String?
          if (_stmt.isNull(_columnIndexOfModerationNote)) {
            _tmpModerationNote = null
          } else {
            _tmpModerationNote = _stmt.getText(_columnIndexOfModerationNote)
          }
          val _tmpMetadataJson: String
          _tmpMetadataJson = _stmt.getText(_columnIndexOfMetadataJson)
          _item =
              ProductEntity(_tmpProductId,_tmpSellerId,_tmpName,_tmpDescription,_tmpCategory,_tmpPrice,_tmpQuantity,_tmpUnit,_tmpLocation,_tmpLatitude,_tmpLongitude,_tmpImageUrls,_tmpStatus,_tmpCondition,_tmpHarvestDate,_tmpExpiryDate,_tmpBirthDate,_tmpVaccinationRecordsJson,_tmpWeightGrams,_tmpHeightCm,_tmpGender,_tmpColor,_tmpBreed,_tmpRaisingPurpose,_tmpHealthStatus,_tmpBirdCode,_tmpColorTag,_tmpFamilyTreeId,_tmpSourceAssetId,_tmpParentIdsJson,_tmpBreedingStatus,_tmpTransferHistoryJson,_tmpCreatedAt,_tmpUpdatedAt,_tmpLastModifiedAt,_tmpIsDeleted,_tmpDeletedAt,_tmpDirty,_tmpStage,_tmpLifecycleStatus,_tmpParentMaleId,_tmpParentFemaleId,_tmpAgeWeeks,_tmpLastStageTransitionAt,_tmpBreederEligibleAt,_tmpIsBatch,_tmpBatchId,_tmpSplitAt,_tmpSplitIntoIds,_tmpDocumentUrls,_tmpQrCodeUrl,_tmpCustomStatus,_tmpDebug,_tmpDeliveryOptions,_tmpDeliveryCost,_tmpLeadTimeDays,_tmpMotherId,_tmpIsBreedingUnit,_tmpEggsCollectedToday,_tmpLastEggLogDate,_tmpReadyForSale,_tmpTargetWeight,_tmpIsShowcased,_tmpExternalVideoUrl,_tmpRecordsLockedAt,_tmpAutoLockAfterDays,_tmpLineageHistoryJson,_tmpEditCount,_tmpLastEditedBy,_tmpAdminFlagged,_tmpModerationNote,_tmpMetadataJson)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun searchProducts(query: String): Flow<List<ProductEntity>> {
    val _sql: String = """
        |
        |        SELECT products.* FROM products 
        |        JOIN products_fts ON products.productId = products_fts.productId 
        |        WHERE products_fts MATCH ?
        |    
        """.trimMargin()
    return createFlow(__db, true, arrayOf("products", "products_fts")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, query)
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfSellerId: Int = getColumnIndexOrThrow(_stmt, "sellerId")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfCategory: Int = getColumnIndexOrThrow(_stmt, "category")
        val _columnIndexOfPrice: Int = getColumnIndexOrThrow(_stmt, "price")
        val _columnIndexOfQuantity: Int = getColumnIndexOrThrow(_stmt, "quantity")
        val _columnIndexOfUnit: Int = getColumnIndexOrThrow(_stmt, "unit")
        val _columnIndexOfLocation: Int = getColumnIndexOrThrow(_stmt, "location")
        val _columnIndexOfLatitude: Int = getColumnIndexOrThrow(_stmt, "latitude")
        val _columnIndexOfLongitude: Int = getColumnIndexOrThrow(_stmt, "longitude")
        val _columnIndexOfImageUrls: Int = getColumnIndexOrThrow(_stmt, "imageUrls")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfCondition: Int = getColumnIndexOrThrow(_stmt, "condition")
        val _columnIndexOfHarvestDate: Int = getColumnIndexOrThrow(_stmt, "harvestDate")
        val _columnIndexOfExpiryDate: Int = getColumnIndexOrThrow(_stmt, "expiryDate")
        val _columnIndexOfBirthDate: Int = getColumnIndexOrThrow(_stmt, "birthDate")
        val _columnIndexOfVaccinationRecordsJson: Int = getColumnIndexOrThrow(_stmt,
            "vaccinationRecordsJson")
        val _columnIndexOfWeightGrams: Int = getColumnIndexOrThrow(_stmt, "weightGrams")
        val _columnIndexOfHeightCm: Int = getColumnIndexOrThrow(_stmt, "heightCm")
        val _columnIndexOfGender: Int = getColumnIndexOrThrow(_stmt, "gender")
        val _columnIndexOfColor: Int = getColumnIndexOrThrow(_stmt, "color")
        val _columnIndexOfBreed: Int = getColumnIndexOrThrow(_stmt, "breed")
        val _columnIndexOfRaisingPurpose: Int = getColumnIndexOrThrow(_stmt, "raisingPurpose")
        val _columnIndexOfHealthStatus: Int = getColumnIndexOrThrow(_stmt, "healthStatus")
        val _columnIndexOfBirdCode: Int = getColumnIndexOrThrow(_stmt, "birdCode")
        val _columnIndexOfColorTag: Int = getColumnIndexOrThrow(_stmt, "colorTag")
        val _columnIndexOfFamilyTreeId: Int = getColumnIndexOrThrow(_stmt, "familyTreeId")
        val _columnIndexOfSourceAssetId: Int = getColumnIndexOrThrow(_stmt, "sourceAssetId")
        val _columnIndexOfParentIdsJson: Int = getColumnIndexOrThrow(_stmt, "parentIdsJson")
        val _columnIndexOfBreedingStatus: Int = getColumnIndexOrThrow(_stmt, "breedingStatus")
        val _columnIndexOfTransferHistoryJson: Int = getColumnIndexOrThrow(_stmt,
            "transferHistoryJson")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfLastModifiedAt: Int = getColumnIndexOrThrow(_stmt, "lastModifiedAt")
        val _columnIndexOfIsDeleted: Int = getColumnIndexOrThrow(_stmt, "isDeleted")
        val _columnIndexOfDeletedAt: Int = getColumnIndexOrThrow(_stmt, "deletedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfStage: Int = getColumnIndexOrThrow(_stmt, "stage")
        val _columnIndexOfLifecycleStatus: Int = getColumnIndexOrThrow(_stmt, "lifecycleStatus")
        val _columnIndexOfParentMaleId: Int = getColumnIndexOrThrow(_stmt, "parentMaleId")
        val _columnIndexOfParentFemaleId: Int = getColumnIndexOrThrow(_stmt, "parentFemaleId")
        val _columnIndexOfAgeWeeks: Int = getColumnIndexOrThrow(_stmt, "ageWeeks")
        val _columnIndexOfLastStageTransitionAt: Int = getColumnIndexOrThrow(_stmt,
            "lastStageTransitionAt")
        val _columnIndexOfBreederEligibleAt: Int = getColumnIndexOrThrow(_stmt, "breederEligibleAt")
        val _columnIndexOfIsBatch: Int = getColumnIndexOrThrow(_stmt, "isBatch")
        val _columnIndexOfBatchId: Int = getColumnIndexOrThrow(_stmt, "batchId")
        val _columnIndexOfSplitAt: Int = getColumnIndexOrThrow(_stmt, "splitAt")
        val _columnIndexOfSplitIntoIds: Int = getColumnIndexOrThrow(_stmt, "splitIntoIds")
        val _columnIndexOfDocumentUrls: Int = getColumnIndexOrThrow(_stmt, "documentUrls")
        val _columnIndexOfQrCodeUrl: Int = getColumnIndexOrThrow(_stmt, "qrCodeUrl")
        val _columnIndexOfCustomStatus: Int = getColumnIndexOrThrow(_stmt, "customStatus")
        val _columnIndexOfDebug: Int = getColumnIndexOrThrow(_stmt, "debug")
        val _columnIndexOfDeliveryOptions: Int = getColumnIndexOrThrow(_stmt, "deliveryOptions")
        val _columnIndexOfDeliveryCost: Int = getColumnIndexOrThrow(_stmt, "deliveryCost")
        val _columnIndexOfLeadTimeDays: Int = getColumnIndexOrThrow(_stmt, "leadTimeDays")
        val _columnIndexOfMotherId: Int = getColumnIndexOrThrow(_stmt, "motherId")
        val _columnIndexOfIsBreedingUnit: Int = getColumnIndexOrThrow(_stmt, "isBreedingUnit")
        val _columnIndexOfEggsCollectedToday: Int = getColumnIndexOrThrow(_stmt,
            "eggsCollectedToday")
        val _columnIndexOfLastEggLogDate: Int = getColumnIndexOrThrow(_stmt, "lastEggLogDate")
        val _columnIndexOfReadyForSale: Int = getColumnIndexOrThrow(_stmt, "readyForSale")
        val _columnIndexOfTargetWeight: Int = getColumnIndexOrThrow(_stmt, "targetWeight")
        val _columnIndexOfIsShowcased: Int = getColumnIndexOrThrow(_stmt, "isShowcased")
        val _columnIndexOfExternalVideoUrl: Int = getColumnIndexOrThrow(_stmt, "externalVideoUrl")
        val _columnIndexOfRecordsLockedAt: Int = getColumnIndexOrThrow(_stmt, "recordsLockedAt")
        val _columnIndexOfAutoLockAfterDays: Int = getColumnIndexOrThrow(_stmt, "autoLockAfterDays")
        val _columnIndexOfLineageHistoryJson: Int = getColumnIndexOrThrow(_stmt,
            "lineageHistoryJson")
        val _columnIndexOfEditCount: Int = getColumnIndexOrThrow(_stmt, "editCount")
        val _columnIndexOfLastEditedBy: Int = getColumnIndexOrThrow(_stmt, "lastEditedBy")
        val _columnIndexOfAdminFlagged: Int = getColumnIndexOrThrow(_stmt, "adminFlagged")
        val _columnIndexOfModerationNote: Int = getColumnIndexOrThrow(_stmt, "moderationNote")
        val _columnIndexOfMetadataJson: Int = getColumnIndexOrThrow(_stmt, "metadataJson")
        val _result: MutableList<ProductEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: ProductEntity
          val _tmpProductId: String
          _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          val _tmpSellerId: String
          _tmpSellerId = _stmt.getText(_columnIndexOfSellerId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpDescription: String
          _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          val _tmpCategory: String
          _tmpCategory = _stmt.getText(_columnIndexOfCategory)
          val _tmpPrice: Double
          _tmpPrice = _stmt.getDouble(_columnIndexOfPrice)
          val _tmpQuantity: Double
          _tmpQuantity = _stmt.getDouble(_columnIndexOfQuantity)
          val _tmpUnit: String
          _tmpUnit = _stmt.getText(_columnIndexOfUnit)
          val _tmpLocation: String
          _tmpLocation = _stmt.getText(_columnIndexOfLocation)
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
          val _tmpImageUrls: List<String>
          val _tmp: String?
          if (_stmt.isNull(_columnIndexOfImageUrls)) {
            _tmp = null
          } else {
            _tmp = _stmt.getText(_columnIndexOfImageUrls)
          }
          val _tmp_1: List<String>? = AppDatabase.Converters.toStringList(_tmp)
          if (_tmp_1 == null) {
            error("Expected NON-NULL 'kotlin.collections.List<kotlin.String>', but it was NULL.")
          } else {
            _tmpImageUrls = _tmp_1
          }
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpCondition: String?
          if (_stmt.isNull(_columnIndexOfCondition)) {
            _tmpCondition = null
          } else {
            _tmpCondition = _stmt.getText(_columnIndexOfCondition)
          }
          val _tmpHarvestDate: Long?
          if (_stmt.isNull(_columnIndexOfHarvestDate)) {
            _tmpHarvestDate = null
          } else {
            _tmpHarvestDate = _stmt.getLong(_columnIndexOfHarvestDate)
          }
          val _tmpExpiryDate: Long?
          if (_stmt.isNull(_columnIndexOfExpiryDate)) {
            _tmpExpiryDate = null
          } else {
            _tmpExpiryDate = _stmt.getLong(_columnIndexOfExpiryDate)
          }
          val _tmpBirthDate: Long?
          if (_stmt.isNull(_columnIndexOfBirthDate)) {
            _tmpBirthDate = null
          } else {
            _tmpBirthDate = _stmt.getLong(_columnIndexOfBirthDate)
          }
          val _tmpVaccinationRecordsJson: String?
          if (_stmt.isNull(_columnIndexOfVaccinationRecordsJson)) {
            _tmpVaccinationRecordsJson = null
          } else {
            _tmpVaccinationRecordsJson = _stmt.getText(_columnIndexOfVaccinationRecordsJson)
          }
          val _tmpWeightGrams: Double?
          if (_stmt.isNull(_columnIndexOfWeightGrams)) {
            _tmpWeightGrams = null
          } else {
            _tmpWeightGrams = _stmt.getDouble(_columnIndexOfWeightGrams)
          }
          val _tmpHeightCm: Double?
          if (_stmt.isNull(_columnIndexOfHeightCm)) {
            _tmpHeightCm = null
          } else {
            _tmpHeightCm = _stmt.getDouble(_columnIndexOfHeightCm)
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
          val _tmpBreed: String?
          if (_stmt.isNull(_columnIndexOfBreed)) {
            _tmpBreed = null
          } else {
            _tmpBreed = _stmt.getText(_columnIndexOfBreed)
          }
          val _tmpRaisingPurpose: String?
          if (_stmt.isNull(_columnIndexOfRaisingPurpose)) {
            _tmpRaisingPurpose = null
          } else {
            _tmpRaisingPurpose = _stmt.getText(_columnIndexOfRaisingPurpose)
          }
          val _tmpHealthStatus: String?
          if (_stmt.isNull(_columnIndexOfHealthStatus)) {
            _tmpHealthStatus = null
          } else {
            _tmpHealthStatus = _stmt.getText(_columnIndexOfHealthStatus)
          }
          val _tmpBirdCode: String?
          if (_stmt.isNull(_columnIndexOfBirdCode)) {
            _tmpBirdCode = null
          } else {
            _tmpBirdCode = _stmt.getText(_columnIndexOfBirdCode)
          }
          val _tmpColorTag: String?
          if (_stmt.isNull(_columnIndexOfColorTag)) {
            _tmpColorTag = null
          } else {
            _tmpColorTag = _stmt.getText(_columnIndexOfColorTag)
          }
          val _tmpFamilyTreeId: String?
          if (_stmt.isNull(_columnIndexOfFamilyTreeId)) {
            _tmpFamilyTreeId = null
          } else {
            _tmpFamilyTreeId = _stmt.getText(_columnIndexOfFamilyTreeId)
          }
          val _tmpSourceAssetId: String?
          if (_stmt.isNull(_columnIndexOfSourceAssetId)) {
            _tmpSourceAssetId = null
          } else {
            _tmpSourceAssetId = _stmt.getText(_columnIndexOfSourceAssetId)
          }
          val _tmpParentIdsJson: String?
          if (_stmt.isNull(_columnIndexOfParentIdsJson)) {
            _tmpParentIdsJson = null
          } else {
            _tmpParentIdsJson = _stmt.getText(_columnIndexOfParentIdsJson)
          }
          val _tmpBreedingStatus: String?
          if (_stmt.isNull(_columnIndexOfBreedingStatus)) {
            _tmpBreedingStatus = null
          } else {
            _tmpBreedingStatus = _stmt.getText(_columnIndexOfBreedingStatus)
          }
          val _tmpTransferHistoryJson: String?
          if (_stmt.isNull(_columnIndexOfTransferHistoryJson)) {
            _tmpTransferHistoryJson = null
          } else {
            _tmpTransferHistoryJson = _stmt.getText(_columnIndexOfTransferHistoryJson)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpLastModifiedAt: Long
          _tmpLastModifiedAt = _stmt.getLong(_columnIndexOfLastModifiedAt)
          val _tmpIsDeleted: Boolean
          val _tmp_2: Int
          _tmp_2 = _stmt.getLong(_columnIndexOfIsDeleted).toInt()
          _tmpIsDeleted = _tmp_2 != 0
          val _tmpDeletedAt: Long?
          if (_stmt.isNull(_columnIndexOfDeletedAt)) {
            _tmpDeletedAt = null
          } else {
            _tmpDeletedAt = _stmt.getLong(_columnIndexOfDeletedAt)
          }
          val _tmpDirty: Boolean
          val _tmp_3: Int
          _tmp_3 = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp_3 != 0
          val _tmpStage: LifecycleStage?
          val _tmp_4: String?
          if (_stmt.isNull(_columnIndexOfStage)) {
            _tmp_4 = null
          } else {
            _tmp_4 = _stmt.getText(_columnIndexOfStage)
          }
          _tmpStage = AppDatabase.Converters.toLifecycleStage(_tmp_4)
          val _tmpLifecycleStatus: String?
          if (_stmt.isNull(_columnIndexOfLifecycleStatus)) {
            _tmpLifecycleStatus = null
          } else {
            _tmpLifecycleStatus = _stmt.getText(_columnIndexOfLifecycleStatus)
          }
          val _tmpParentMaleId: String?
          if (_stmt.isNull(_columnIndexOfParentMaleId)) {
            _tmpParentMaleId = null
          } else {
            _tmpParentMaleId = _stmt.getText(_columnIndexOfParentMaleId)
          }
          val _tmpParentFemaleId: String?
          if (_stmt.isNull(_columnIndexOfParentFemaleId)) {
            _tmpParentFemaleId = null
          } else {
            _tmpParentFemaleId = _stmt.getText(_columnIndexOfParentFemaleId)
          }
          val _tmpAgeWeeks: Int?
          if (_stmt.isNull(_columnIndexOfAgeWeeks)) {
            _tmpAgeWeeks = null
          } else {
            _tmpAgeWeeks = _stmt.getLong(_columnIndexOfAgeWeeks).toInt()
          }
          val _tmpLastStageTransitionAt: Long?
          if (_stmt.isNull(_columnIndexOfLastStageTransitionAt)) {
            _tmpLastStageTransitionAt = null
          } else {
            _tmpLastStageTransitionAt = _stmt.getLong(_columnIndexOfLastStageTransitionAt)
          }
          val _tmpBreederEligibleAt: Long?
          if (_stmt.isNull(_columnIndexOfBreederEligibleAt)) {
            _tmpBreederEligibleAt = null
          } else {
            _tmpBreederEligibleAt = _stmt.getLong(_columnIndexOfBreederEligibleAt)
          }
          val _tmpIsBatch: Boolean?
          val _tmp_5: Int?
          if (_stmt.isNull(_columnIndexOfIsBatch)) {
            _tmp_5 = null
          } else {
            _tmp_5 = _stmt.getLong(_columnIndexOfIsBatch).toInt()
          }
          _tmpIsBatch = _tmp_5?.let { it != 0 }
          val _tmpBatchId: String?
          if (_stmt.isNull(_columnIndexOfBatchId)) {
            _tmpBatchId = null
          } else {
            _tmpBatchId = _stmt.getText(_columnIndexOfBatchId)
          }
          val _tmpSplitAt: Long?
          if (_stmt.isNull(_columnIndexOfSplitAt)) {
            _tmpSplitAt = null
          } else {
            _tmpSplitAt = _stmt.getLong(_columnIndexOfSplitAt)
          }
          val _tmpSplitIntoIds: String?
          if (_stmt.isNull(_columnIndexOfSplitIntoIds)) {
            _tmpSplitIntoIds = null
          } else {
            _tmpSplitIntoIds = _stmt.getText(_columnIndexOfSplitIntoIds)
          }
          val _tmpDocumentUrls: List<String>
          val _tmp_6: String?
          if (_stmt.isNull(_columnIndexOfDocumentUrls)) {
            _tmp_6 = null
          } else {
            _tmp_6 = _stmt.getText(_columnIndexOfDocumentUrls)
          }
          val _tmp_7: List<String>? = AppDatabase.Converters.toStringList(_tmp_6)
          if (_tmp_7 == null) {
            error("Expected NON-NULL 'kotlin.collections.List<kotlin.String>', but it was NULL.")
          } else {
            _tmpDocumentUrls = _tmp_7
          }
          val _tmpQrCodeUrl: String?
          if (_stmt.isNull(_columnIndexOfQrCodeUrl)) {
            _tmpQrCodeUrl = null
          } else {
            _tmpQrCodeUrl = _stmt.getText(_columnIndexOfQrCodeUrl)
          }
          val _tmpCustomStatus: String?
          if (_stmt.isNull(_columnIndexOfCustomStatus)) {
            _tmpCustomStatus = null
          } else {
            _tmpCustomStatus = _stmt.getText(_columnIndexOfCustomStatus)
          }
          val _tmpDebug: Boolean
          val _tmp_8: Int
          _tmp_8 = _stmt.getLong(_columnIndexOfDebug).toInt()
          _tmpDebug = _tmp_8 != 0
          val _tmpDeliveryOptions: List<String>
          val _tmp_9: String?
          if (_stmt.isNull(_columnIndexOfDeliveryOptions)) {
            _tmp_9 = null
          } else {
            _tmp_9 = _stmt.getText(_columnIndexOfDeliveryOptions)
          }
          val _tmp_10: List<String>? = AppDatabase.Converters.toStringList(_tmp_9)
          if (_tmp_10 == null) {
            error("Expected NON-NULL 'kotlin.collections.List<kotlin.String>', but it was NULL.")
          } else {
            _tmpDeliveryOptions = _tmp_10
          }
          val _tmpDeliveryCost: Double?
          if (_stmt.isNull(_columnIndexOfDeliveryCost)) {
            _tmpDeliveryCost = null
          } else {
            _tmpDeliveryCost = _stmt.getDouble(_columnIndexOfDeliveryCost)
          }
          val _tmpLeadTimeDays: Int?
          if (_stmt.isNull(_columnIndexOfLeadTimeDays)) {
            _tmpLeadTimeDays = null
          } else {
            _tmpLeadTimeDays = _stmt.getLong(_columnIndexOfLeadTimeDays).toInt()
          }
          val _tmpMotherId: String?
          if (_stmt.isNull(_columnIndexOfMotherId)) {
            _tmpMotherId = null
          } else {
            _tmpMotherId = _stmt.getText(_columnIndexOfMotherId)
          }
          val _tmpIsBreedingUnit: Boolean
          val _tmp_11: Int
          _tmp_11 = _stmt.getLong(_columnIndexOfIsBreedingUnit).toInt()
          _tmpIsBreedingUnit = _tmp_11 != 0
          val _tmpEggsCollectedToday: Int
          _tmpEggsCollectedToday = _stmt.getLong(_columnIndexOfEggsCollectedToday).toInt()
          val _tmpLastEggLogDate: Long?
          if (_stmt.isNull(_columnIndexOfLastEggLogDate)) {
            _tmpLastEggLogDate = null
          } else {
            _tmpLastEggLogDate = _stmt.getLong(_columnIndexOfLastEggLogDate)
          }
          val _tmpReadyForSale: Boolean
          val _tmp_12: Int
          _tmp_12 = _stmt.getLong(_columnIndexOfReadyForSale).toInt()
          _tmpReadyForSale = _tmp_12 != 0
          val _tmpTargetWeight: Double?
          if (_stmt.isNull(_columnIndexOfTargetWeight)) {
            _tmpTargetWeight = null
          } else {
            _tmpTargetWeight = _stmt.getDouble(_columnIndexOfTargetWeight)
          }
          val _tmpIsShowcased: Boolean
          val _tmp_13: Int
          _tmp_13 = _stmt.getLong(_columnIndexOfIsShowcased).toInt()
          _tmpIsShowcased = _tmp_13 != 0
          val _tmpExternalVideoUrl: String?
          if (_stmt.isNull(_columnIndexOfExternalVideoUrl)) {
            _tmpExternalVideoUrl = null
          } else {
            _tmpExternalVideoUrl = _stmt.getText(_columnIndexOfExternalVideoUrl)
          }
          val _tmpRecordsLockedAt: Long?
          if (_stmt.isNull(_columnIndexOfRecordsLockedAt)) {
            _tmpRecordsLockedAt = null
          } else {
            _tmpRecordsLockedAt = _stmt.getLong(_columnIndexOfRecordsLockedAt)
          }
          val _tmpAutoLockAfterDays: Int
          _tmpAutoLockAfterDays = _stmt.getLong(_columnIndexOfAutoLockAfterDays).toInt()
          val _tmpLineageHistoryJson: String?
          if (_stmt.isNull(_columnIndexOfLineageHistoryJson)) {
            _tmpLineageHistoryJson = null
          } else {
            _tmpLineageHistoryJson = _stmt.getText(_columnIndexOfLineageHistoryJson)
          }
          val _tmpEditCount: Int
          _tmpEditCount = _stmt.getLong(_columnIndexOfEditCount).toInt()
          val _tmpLastEditedBy: String?
          if (_stmt.isNull(_columnIndexOfLastEditedBy)) {
            _tmpLastEditedBy = null
          } else {
            _tmpLastEditedBy = _stmt.getText(_columnIndexOfLastEditedBy)
          }
          val _tmpAdminFlagged: Boolean
          val _tmp_14: Int
          _tmp_14 = _stmt.getLong(_columnIndexOfAdminFlagged).toInt()
          _tmpAdminFlagged = _tmp_14 != 0
          val _tmpModerationNote: String?
          if (_stmt.isNull(_columnIndexOfModerationNote)) {
            _tmpModerationNote = null
          } else {
            _tmpModerationNote = _stmt.getText(_columnIndexOfModerationNote)
          }
          val _tmpMetadataJson: String
          _tmpMetadataJson = _stmt.getText(_columnIndexOfMetadataJson)
          _item =
              ProductEntity(_tmpProductId,_tmpSellerId,_tmpName,_tmpDescription,_tmpCategory,_tmpPrice,_tmpQuantity,_tmpUnit,_tmpLocation,_tmpLatitude,_tmpLongitude,_tmpImageUrls,_tmpStatus,_tmpCondition,_tmpHarvestDate,_tmpExpiryDate,_tmpBirthDate,_tmpVaccinationRecordsJson,_tmpWeightGrams,_tmpHeightCm,_tmpGender,_tmpColor,_tmpBreed,_tmpRaisingPurpose,_tmpHealthStatus,_tmpBirdCode,_tmpColorTag,_tmpFamilyTreeId,_tmpSourceAssetId,_tmpParentIdsJson,_tmpBreedingStatus,_tmpTransferHistoryJson,_tmpCreatedAt,_tmpUpdatedAt,_tmpLastModifiedAt,_tmpIsDeleted,_tmpDeletedAt,_tmpDirty,_tmpStage,_tmpLifecycleStatus,_tmpParentMaleId,_tmpParentFemaleId,_tmpAgeWeeks,_tmpLastStageTransitionAt,_tmpBreederEligibleAt,_tmpIsBatch,_tmpBatchId,_tmpSplitAt,_tmpSplitIntoIds,_tmpDocumentUrls,_tmpQrCodeUrl,_tmpCustomStatus,_tmpDebug,_tmpDeliveryOptions,_tmpDeliveryCost,_tmpLeadTimeDays,_tmpMotherId,_tmpIsBreedingUnit,_tmpEggsCollectedToday,_tmpLastEggLogDate,_tmpReadyForSale,_tmpTargetWeight,_tmpIsShowcased,_tmpExternalVideoUrl,_tmpRecordsLockedAt,_tmpAutoLockAfterDays,_tmpLineageHistoryJson,_tmpEditCount,_tmpLastEditedBy,_tmpAdminFlagged,_tmpModerationNote,_tmpMetadataJson)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun countDemoProducts(): Int {
    val _sql: String = "SELECT COUNT(*) FROM products WHERE productId LIKE 'demo_prod_%'"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
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

  public override suspend fun findByBirdCode(code: String): ProductEntity? {
    val _sql: String = "SELECT * FROM products WHERE birdCode = ? LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, code)
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfSellerId: Int = getColumnIndexOrThrow(_stmt, "sellerId")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfCategory: Int = getColumnIndexOrThrow(_stmt, "category")
        val _columnIndexOfPrice: Int = getColumnIndexOrThrow(_stmt, "price")
        val _columnIndexOfQuantity: Int = getColumnIndexOrThrow(_stmt, "quantity")
        val _columnIndexOfUnit: Int = getColumnIndexOrThrow(_stmt, "unit")
        val _columnIndexOfLocation: Int = getColumnIndexOrThrow(_stmt, "location")
        val _columnIndexOfLatitude: Int = getColumnIndexOrThrow(_stmt, "latitude")
        val _columnIndexOfLongitude: Int = getColumnIndexOrThrow(_stmt, "longitude")
        val _columnIndexOfImageUrls: Int = getColumnIndexOrThrow(_stmt, "imageUrls")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfCondition: Int = getColumnIndexOrThrow(_stmt, "condition")
        val _columnIndexOfHarvestDate: Int = getColumnIndexOrThrow(_stmt, "harvestDate")
        val _columnIndexOfExpiryDate: Int = getColumnIndexOrThrow(_stmt, "expiryDate")
        val _columnIndexOfBirthDate: Int = getColumnIndexOrThrow(_stmt, "birthDate")
        val _columnIndexOfVaccinationRecordsJson: Int = getColumnIndexOrThrow(_stmt,
            "vaccinationRecordsJson")
        val _columnIndexOfWeightGrams: Int = getColumnIndexOrThrow(_stmt, "weightGrams")
        val _columnIndexOfHeightCm: Int = getColumnIndexOrThrow(_stmt, "heightCm")
        val _columnIndexOfGender: Int = getColumnIndexOrThrow(_stmt, "gender")
        val _columnIndexOfColor: Int = getColumnIndexOrThrow(_stmt, "color")
        val _columnIndexOfBreed: Int = getColumnIndexOrThrow(_stmt, "breed")
        val _columnIndexOfRaisingPurpose: Int = getColumnIndexOrThrow(_stmt, "raisingPurpose")
        val _columnIndexOfHealthStatus: Int = getColumnIndexOrThrow(_stmt, "healthStatus")
        val _columnIndexOfBirdCode: Int = getColumnIndexOrThrow(_stmt, "birdCode")
        val _columnIndexOfColorTag: Int = getColumnIndexOrThrow(_stmt, "colorTag")
        val _columnIndexOfFamilyTreeId: Int = getColumnIndexOrThrow(_stmt, "familyTreeId")
        val _columnIndexOfSourceAssetId: Int = getColumnIndexOrThrow(_stmt, "sourceAssetId")
        val _columnIndexOfParentIdsJson: Int = getColumnIndexOrThrow(_stmt, "parentIdsJson")
        val _columnIndexOfBreedingStatus: Int = getColumnIndexOrThrow(_stmt, "breedingStatus")
        val _columnIndexOfTransferHistoryJson: Int = getColumnIndexOrThrow(_stmt,
            "transferHistoryJson")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfLastModifiedAt: Int = getColumnIndexOrThrow(_stmt, "lastModifiedAt")
        val _columnIndexOfIsDeleted: Int = getColumnIndexOrThrow(_stmt, "isDeleted")
        val _columnIndexOfDeletedAt: Int = getColumnIndexOrThrow(_stmt, "deletedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfStage: Int = getColumnIndexOrThrow(_stmt, "stage")
        val _columnIndexOfLifecycleStatus: Int = getColumnIndexOrThrow(_stmt, "lifecycleStatus")
        val _columnIndexOfParentMaleId: Int = getColumnIndexOrThrow(_stmt, "parentMaleId")
        val _columnIndexOfParentFemaleId: Int = getColumnIndexOrThrow(_stmt, "parentFemaleId")
        val _columnIndexOfAgeWeeks: Int = getColumnIndexOrThrow(_stmt, "ageWeeks")
        val _columnIndexOfLastStageTransitionAt: Int = getColumnIndexOrThrow(_stmt,
            "lastStageTransitionAt")
        val _columnIndexOfBreederEligibleAt: Int = getColumnIndexOrThrow(_stmt, "breederEligibleAt")
        val _columnIndexOfIsBatch: Int = getColumnIndexOrThrow(_stmt, "isBatch")
        val _columnIndexOfBatchId: Int = getColumnIndexOrThrow(_stmt, "batchId")
        val _columnIndexOfSplitAt: Int = getColumnIndexOrThrow(_stmt, "splitAt")
        val _columnIndexOfSplitIntoIds: Int = getColumnIndexOrThrow(_stmt, "splitIntoIds")
        val _columnIndexOfDocumentUrls: Int = getColumnIndexOrThrow(_stmt, "documentUrls")
        val _columnIndexOfQrCodeUrl: Int = getColumnIndexOrThrow(_stmt, "qrCodeUrl")
        val _columnIndexOfCustomStatus: Int = getColumnIndexOrThrow(_stmt, "customStatus")
        val _columnIndexOfDebug: Int = getColumnIndexOrThrow(_stmt, "debug")
        val _columnIndexOfDeliveryOptions: Int = getColumnIndexOrThrow(_stmt, "deliveryOptions")
        val _columnIndexOfDeliveryCost: Int = getColumnIndexOrThrow(_stmt, "deliveryCost")
        val _columnIndexOfLeadTimeDays: Int = getColumnIndexOrThrow(_stmt, "leadTimeDays")
        val _columnIndexOfMotherId: Int = getColumnIndexOrThrow(_stmt, "motherId")
        val _columnIndexOfIsBreedingUnit: Int = getColumnIndexOrThrow(_stmt, "isBreedingUnit")
        val _columnIndexOfEggsCollectedToday: Int = getColumnIndexOrThrow(_stmt,
            "eggsCollectedToday")
        val _columnIndexOfLastEggLogDate: Int = getColumnIndexOrThrow(_stmt, "lastEggLogDate")
        val _columnIndexOfReadyForSale: Int = getColumnIndexOrThrow(_stmt, "readyForSale")
        val _columnIndexOfTargetWeight: Int = getColumnIndexOrThrow(_stmt, "targetWeight")
        val _columnIndexOfIsShowcased: Int = getColumnIndexOrThrow(_stmt, "isShowcased")
        val _columnIndexOfExternalVideoUrl: Int = getColumnIndexOrThrow(_stmt, "externalVideoUrl")
        val _columnIndexOfRecordsLockedAt: Int = getColumnIndexOrThrow(_stmt, "recordsLockedAt")
        val _columnIndexOfAutoLockAfterDays: Int = getColumnIndexOrThrow(_stmt, "autoLockAfterDays")
        val _columnIndexOfLineageHistoryJson: Int = getColumnIndexOrThrow(_stmt,
            "lineageHistoryJson")
        val _columnIndexOfEditCount: Int = getColumnIndexOrThrow(_stmt, "editCount")
        val _columnIndexOfLastEditedBy: Int = getColumnIndexOrThrow(_stmt, "lastEditedBy")
        val _columnIndexOfAdminFlagged: Int = getColumnIndexOrThrow(_stmt, "adminFlagged")
        val _columnIndexOfModerationNote: Int = getColumnIndexOrThrow(_stmt, "moderationNote")
        val _columnIndexOfMetadataJson: Int = getColumnIndexOrThrow(_stmt, "metadataJson")
        val _result: ProductEntity?
        if (_stmt.step()) {
          val _tmpProductId: String
          _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          val _tmpSellerId: String
          _tmpSellerId = _stmt.getText(_columnIndexOfSellerId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpDescription: String
          _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          val _tmpCategory: String
          _tmpCategory = _stmt.getText(_columnIndexOfCategory)
          val _tmpPrice: Double
          _tmpPrice = _stmt.getDouble(_columnIndexOfPrice)
          val _tmpQuantity: Double
          _tmpQuantity = _stmt.getDouble(_columnIndexOfQuantity)
          val _tmpUnit: String
          _tmpUnit = _stmt.getText(_columnIndexOfUnit)
          val _tmpLocation: String
          _tmpLocation = _stmt.getText(_columnIndexOfLocation)
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
          val _tmpImageUrls: List<String>
          val _tmp: String?
          if (_stmt.isNull(_columnIndexOfImageUrls)) {
            _tmp = null
          } else {
            _tmp = _stmt.getText(_columnIndexOfImageUrls)
          }
          val _tmp_1: List<String>? = AppDatabase.Converters.toStringList(_tmp)
          if (_tmp_1 == null) {
            error("Expected NON-NULL 'kotlin.collections.List<kotlin.String>', but it was NULL.")
          } else {
            _tmpImageUrls = _tmp_1
          }
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpCondition: String?
          if (_stmt.isNull(_columnIndexOfCondition)) {
            _tmpCondition = null
          } else {
            _tmpCondition = _stmt.getText(_columnIndexOfCondition)
          }
          val _tmpHarvestDate: Long?
          if (_stmt.isNull(_columnIndexOfHarvestDate)) {
            _tmpHarvestDate = null
          } else {
            _tmpHarvestDate = _stmt.getLong(_columnIndexOfHarvestDate)
          }
          val _tmpExpiryDate: Long?
          if (_stmt.isNull(_columnIndexOfExpiryDate)) {
            _tmpExpiryDate = null
          } else {
            _tmpExpiryDate = _stmt.getLong(_columnIndexOfExpiryDate)
          }
          val _tmpBirthDate: Long?
          if (_stmt.isNull(_columnIndexOfBirthDate)) {
            _tmpBirthDate = null
          } else {
            _tmpBirthDate = _stmt.getLong(_columnIndexOfBirthDate)
          }
          val _tmpVaccinationRecordsJson: String?
          if (_stmt.isNull(_columnIndexOfVaccinationRecordsJson)) {
            _tmpVaccinationRecordsJson = null
          } else {
            _tmpVaccinationRecordsJson = _stmt.getText(_columnIndexOfVaccinationRecordsJson)
          }
          val _tmpWeightGrams: Double?
          if (_stmt.isNull(_columnIndexOfWeightGrams)) {
            _tmpWeightGrams = null
          } else {
            _tmpWeightGrams = _stmt.getDouble(_columnIndexOfWeightGrams)
          }
          val _tmpHeightCm: Double?
          if (_stmt.isNull(_columnIndexOfHeightCm)) {
            _tmpHeightCm = null
          } else {
            _tmpHeightCm = _stmt.getDouble(_columnIndexOfHeightCm)
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
          val _tmpBreed: String?
          if (_stmt.isNull(_columnIndexOfBreed)) {
            _tmpBreed = null
          } else {
            _tmpBreed = _stmt.getText(_columnIndexOfBreed)
          }
          val _tmpRaisingPurpose: String?
          if (_stmt.isNull(_columnIndexOfRaisingPurpose)) {
            _tmpRaisingPurpose = null
          } else {
            _tmpRaisingPurpose = _stmt.getText(_columnIndexOfRaisingPurpose)
          }
          val _tmpHealthStatus: String?
          if (_stmt.isNull(_columnIndexOfHealthStatus)) {
            _tmpHealthStatus = null
          } else {
            _tmpHealthStatus = _stmt.getText(_columnIndexOfHealthStatus)
          }
          val _tmpBirdCode: String?
          if (_stmt.isNull(_columnIndexOfBirdCode)) {
            _tmpBirdCode = null
          } else {
            _tmpBirdCode = _stmt.getText(_columnIndexOfBirdCode)
          }
          val _tmpColorTag: String?
          if (_stmt.isNull(_columnIndexOfColorTag)) {
            _tmpColorTag = null
          } else {
            _tmpColorTag = _stmt.getText(_columnIndexOfColorTag)
          }
          val _tmpFamilyTreeId: String?
          if (_stmt.isNull(_columnIndexOfFamilyTreeId)) {
            _tmpFamilyTreeId = null
          } else {
            _tmpFamilyTreeId = _stmt.getText(_columnIndexOfFamilyTreeId)
          }
          val _tmpSourceAssetId: String?
          if (_stmt.isNull(_columnIndexOfSourceAssetId)) {
            _tmpSourceAssetId = null
          } else {
            _tmpSourceAssetId = _stmt.getText(_columnIndexOfSourceAssetId)
          }
          val _tmpParentIdsJson: String?
          if (_stmt.isNull(_columnIndexOfParentIdsJson)) {
            _tmpParentIdsJson = null
          } else {
            _tmpParentIdsJson = _stmt.getText(_columnIndexOfParentIdsJson)
          }
          val _tmpBreedingStatus: String?
          if (_stmt.isNull(_columnIndexOfBreedingStatus)) {
            _tmpBreedingStatus = null
          } else {
            _tmpBreedingStatus = _stmt.getText(_columnIndexOfBreedingStatus)
          }
          val _tmpTransferHistoryJson: String?
          if (_stmt.isNull(_columnIndexOfTransferHistoryJson)) {
            _tmpTransferHistoryJson = null
          } else {
            _tmpTransferHistoryJson = _stmt.getText(_columnIndexOfTransferHistoryJson)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpLastModifiedAt: Long
          _tmpLastModifiedAt = _stmt.getLong(_columnIndexOfLastModifiedAt)
          val _tmpIsDeleted: Boolean
          val _tmp_2: Int
          _tmp_2 = _stmt.getLong(_columnIndexOfIsDeleted).toInt()
          _tmpIsDeleted = _tmp_2 != 0
          val _tmpDeletedAt: Long?
          if (_stmt.isNull(_columnIndexOfDeletedAt)) {
            _tmpDeletedAt = null
          } else {
            _tmpDeletedAt = _stmt.getLong(_columnIndexOfDeletedAt)
          }
          val _tmpDirty: Boolean
          val _tmp_3: Int
          _tmp_3 = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp_3 != 0
          val _tmpStage: LifecycleStage?
          val _tmp_4: String?
          if (_stmt.isNull(_columnIndexOfStage)) {
            _tmp_4 = null
          } else {
            _tmp_4 = _stmt.getText(_columnIndexOfStage)
          }
          _tmpStage = AppDatabase.Converters.toLifecycleStage(_tmp_4)
          val _tmpLifecycleStatus: String?
          if (_stmt.isNull(_columnIndexOfLifecycleStatus)) {
            _tmpLifecycleStatus = null
          } else {
            _tmpLifecycleStatus = _stmt.getText(_columnIndexOfLifecycleStatus)
          }
          val _tmpParentMaleId: String?
          if (_stmt.isNull(_columnIndexOfParentMaleId)) {
            _tmpParentMaleId = null
          } else {
            _tmpParentMaleId = _stmt.getText(_columnIndexOfParentMaleId)
          }
          val _tmpParentFemaleId: String?
          if (_stmt.isNull(_columnIndexOfParentFemaleId)) {
            _tmpParentFemaleId = null
          } else {
            _tmpParentFemaleId = _stmt.getText(_columnIndexOfParentFemaleId)
          }
          val _tmpAgeWeeks: Int?
          if (_stmt.isNull(_columnIndexOfAgeWeeks)) {
            _tmpAgeWeeks = null
          } else {
            _tmpAgeWeeks = _stmt.getLong(_columnIndexOfAgeWeeks).toInt()
          }
          val _tmpLastStageTransitionAt: Long?
          if (_stmt.isNull(_columnIndexOfLastStageTransitionAt)) {
            _tmpLastStageTransitionAt = null
          } else {
            _tmpLastStageTransitionAt = _stmt.getLong(_columnIndexOfLastStageTransitionAt)
          }
          val _tmpBreederEligibleAt: Long?
          if (_stmt.isNull(_columnIndexOfBreederEligibleAt)) {
            _tmpBreederEligibleAt = null
          } else {
            _tmpBreederEligibleAt = _stmt.getLong(_columnIndexOfBreederEligibleAt)
          }
          val _tmpIsBatch: Boolean?
          val _tmp_5: Int?
          if (_stmt.isNull(_columnIndexOfIsBatch)) {
            _tmp_5 = null
          } else {
            _tmp_5 = _stmt.getLong(_columnIndexOfIsBatch).toInt()
          }
          _tmpIsBatch = _tmp_5?.let { it != 0 }
          val _tmpBatchId: String?
          if (_stmt.isNull(_columnIndexOfBatchId)) {
            _tmpBatchId = null
          } else {
            _tmpBatchId = _stmt.getText(_columnIndexOfBatchId)
          }
          val _tmpSplitAt: Long?
          if (_stmt.isNull(_columnIndexOfSplitAt)) {
            _tmpSplitAt = null
          } else {
            _tmpSplitAt = _stmt.getLong(_columnIndexOfSplitAt)
          }
          val _tmpSplitIntoIds: String?
          if (_stmt.isNull(_columnIndexOfSplitIntoIds)) {
            _tmpSplitIntoIds = null
          } else {
            _tmpSplitIntoIds = _stmt.getText(_columnIndexOfSplitIntoIds)
          }
          val _tmpDocumentUrls: List<String>
          val _tmp_6: String?
          if (_stmt.isNull(_columnIndexOfDocumentUrls)) {
            _tmp_6 = null
          } else {
            _tmp_6 = _stmt.getText(_columnIndexOfDocumentUrls)
          }
          val _tmp_7: List<String>? = AppDatabase.Converters.toStringList(_tmp_6)
          if (_tmp_7 == null) {
            error("Expected NON-NULL 'kotlin.collections.List<kotlin.String>', but it was NULL.")
          } else {
            _tmpDocumentUrls = _tmp_7
          }
          val _tmpQrCodeUrl: String?
          if (_stmt.isNull(_columnIndexOfQrCodeUrl)) {
            _tmpQrCodeUrl = null
          } else {
            _tmpQrCodeUrl = _stmt.getText(_columnIndexOfQrCodeUrl)
          }
          val _tmpCustomStatus: String?
          if (_stmt.isNull(_columnIndexOfCustomStatus)) {
            _tmpCustomStatus = null
          } else {
            _tmpCustomStatus = _stmt.getText(_columnIndexOfCustomStatus)
          }
          val _tmpDebug: Boolean
          val _tmp_8: Int
          _tmp_8 = _stmt.getLong(_columnIndexOfDebug).toInt()
          _tmpDebug = _tmp_8 != 0
          val _tmpDeliveryOptions: List<String>
          val _tmp_9: String?
          if (_stmt.isNull(_columnIndexOfDeliveryOptions)) {
            _tmp_9 = null
          } else {
            _tmp_9 = _stmt.getText(_columnIndexOfDeliveryOptions)
          }
          val _tmp_10: List<String>? = AppDatabase.Converters.toStringList(_tmp_9)
          if (_tmp_10 == null) {
            error("Expected NON-NULL 'kotlin.collections.List<kotlin.String>', but it was NULL.")
          } else {
            _tmpDeliveryOptions = _tmp_10
          }
          val _tmpDeliveryCost: Double?
          if (_stmt.isNull(_columnIndexOfDeliveryCost)) {
            _tmpDeliveryCost = null
          } else {
            _tmpDeliveryCost = _stmt.getDouble(_columnIndexOfDeliveryCost)
          }
          val _tmpLeadTimeDays: Int?
          if (_stmt.isNull(_columnIndexOfLeadTimeDays)) {
            _tmpLeadTimeDays = null
          } else {
            _tmpLeadTimeDays = _stmt.getLong(_columnIndexOfLeadTimeDays).toInt()
          }
          val _tmpMotherId: String?
          if (_stmt.isNull(_columnIndexOfMotherId)) {
            _tmpMotherId = null
          } else {
            _tmpMotherId = _stmt.getText(_columnIndexOfMotherId)
          }
          val _tmpIsBreedingUnit: Boolean
          val _tmp_11: Int
          _tmp_11 = _stmt.getLong(_columnIndexOfIsBreedingUnit).toInt()
          _tmpIsBreedingUnit = _tmp_11 != 0
          val _tmpEggsCollectedToday: Int
          _tmpEggsCollectedToday = _stmt.getLong(_columnIndexOfEggsCollectedToday).toInt()
          val _tmpLastEggLogDate: Long?
          if (_stmt.isNull(_columnIndexOfLastEggLogDate)) {
            _tmpLastEggLogDate = null
          } else {
            _tmpLastEggLogDate = _stmt.getLong(_columnIndexOfLastEggLogDate)
          }
          val _tmpReadyForSale: Boolean
          val _tmp_12: Int
          _tmp_12 = _stmt.getLong(_columnIndexOfReadyForSale).toInt()
          _tmpReadyForSale = _tmp_12 != 0
          val _tmpTargetWeight: Double?
          if (_stmt.isNull(_columnIndexOfTargetWeight)) {
            _tmpTargetWeight = null
          } else {
            _tmpTargetWeight = _stmt.getDouble(_columnIndexOfTargetWeight)
          }
          val _tmpIsShowcased: Boolean
          val _tmp_13: Int
          _tmp_13 = _stmt.getLong(_columnIndexOfIsShowcased).toInt()
          _tmpIsShowcased = _tmp_13 != 0
          val _tmpExternalVideoUrl: String?
          if (_stmt.isNull(_columnIndexOfExternalVideoUrl)) {
            _tmpExternalVideoUrl = null
          } else {
            _tmpExternalVideoUrl = _stmt.getText(_columnIndexOfExternalVideoUrl)
          }
          val _tmpRecordsLockedAt: Long?
          if (_stmt.isNull(_columnIndexOfRecordsLockedAt)) {
            _tmpRecordsLockedAt = null
          } else {
            _tmpRecordsLockedAt = _stmt.getLong(_columnIndexOfRecordsLockedAt)
          }
          val _tmpAutoLockAfterDays: Int
          _tmpAutoLockAfterDays = _stmt.getLong(_columnIndexOfAutoLockAfterDays).toInt()
          val _tmpLineageHistoryJson: String?
          if (_stmt.isNull(_columnIndexOfLineageHistoryJson)) {
            _tmpLineageHistoryJson = null
          } else {
            _tmpLineageHistoryJson = _stmt.getText(_columnIndexOfLineageHistoryJson)
          }
          val _tmpEditCount: Int
          _tmpEditCount = _stmt.getLong(_columnIndexOfEditCount).toInt()
          val _tmpLastEditedBy: String?
          if (_stmt.isNull(_columnIndexOfLastEditedBy)) {
            _tmpLastEditedBy = null
          } else {
            _tmpLastEditedBy = _stmt.getText(_columnIndexOfLastEditedBy)
          }
          val _tmpAdminFlagged: Boolean
          val _tmp_14: Int
          _tmp_14 = _stmt.getLong(_columnIndexOfAdminFlagged).toInt()
          _tmpAdminFlagged = _tmp_14 != 0
          val _tmpModerationNote: String?
          if (_stmt.isNull(_columnIndexOfModerationNote)) {
            _tmpModerationNote = null
          } else {
            _tmpModerationNote = _stmt.getText(_columnIndexOfModerationNote)
          }
          val _tmpMetadataJson: String
          _tmpMetadataJson = _stmt.getText(_columnIndexOfMetadataJson)
          _result =
              ProductEntity(_tmpProductId,_tmpSellerId,_tmpName,_tmpDescription,_tmpCategory,_tmpPrice,_tmpQuantity,_tmpUnit,_tmpLocation,_tmpLatitude,_tmpLongitude,_tmpImageUrls,_tmpStatus,_tmpCondition,_tmpHarvestDate,_tmpExpiryDate,_tmpBirthDate,_tmpVaccinationRecordsJson,_tmpWeightGrams,_tmpHeightCm,_tmpGender,_tmpColor,_tmpBreed,_tmpRaisingPurpose,_tmpHealthStatus,_tmpBirdCode,_tmpColorTag,_tmpFamilyTreeId,_tmpSourceAssetId,_tmpParentIdsJson,_tmpBreedingStatus,_tmpTransferHistoryJson,_tmpCreatedAt,_tmpUpdatedAt,_tmpLastModifiedAt,_tmpIsDeleted,_tmpDeletedAt,_tmpDirty,_tmpStage,_tmpLifecycleStatus,_tmpParentMaleId,_tmpParentFemaleId,_tmpAgeWeeks,_tmpLastStageTransitionAt,_tmpBreederEligibleAt,_tmpIsBatch,_tmpBatchId,_tmpSplitAt,_tmpSplitIntoIds,_tmpDocumentUrls,_tmpQrCodeUrl,_tmpCustomStatus,_tmpDebug,_tmpDeliveryOptions,_tmpDeliveryCost,_tmpLeadTimeDays,_tmpMotherId,_tmpIsBreedingUnit,_tmpEggsCollectedToday,_tmpLastEggLogDate,_tmpReadyForSale,_tmpTargetWeight,_tmpIsShowcased,_tmpExternalVideoUrl,_tmpRecordsLockedAt,_tmpAutoLockAfterDays,_tmpLineageHistoryJson,_tmpEditCount,_tmpLastEditedBy,_tmpAdminFlagged,_tmpModerationNote,_tmpMetadataJson)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun autocomplete(prefix: String, limit: Int): List<ProductEntity> {
    val _sql: String =
        "SELECT * FROM products WHERE name LIKE ? || '%' OR breed LIKE ? || '%' ORDER BY name ASC LIMIT ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, prefix)
        _argIndex = 2
        _stmt.bindText(_argIndex, prefix)
        _argIndex = 3
        _stmt.bindLong(_argIndex, limit.toLong())
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfSellerId: Int = getColumnIndexOrThrow(_stmt, "sellerId")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfCategory: Int = getColumnIndexOrThrow(_stmt, "category")
        val _columnIndexOfPrice: Int = getColumnIndexOrThrow(_stmt, "price")
        val _columnIndexOfQuantity: Int = getColumnIndexOrThrow(_stmt, "quantity")
        val _columnIndexOfUnit: Int = getColumnIndexOrThrow(_stmt, "unit")
        val _columnIndexOfLocation: Int = getColumnIndexOrThrow(_stmt, "location")
        val _columnIndexOfLatitude: Int = getColumnIndexOrThrow(_stmt, "latitude")
        val _columnIndexOfLongitude: Int = getColumnIndexOrThrow(_stmt, "longitude")
        val _columnIndexOfImageUrls: Int = getColumnIndexOrThrow(_stmt, "imageUrls")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfCondition: Int = getColumnIndexOrThrow(_stmt, "condition")
        val _columnIndexOfHarvestDate: Int = getColumnIndexOrThrow(_stmt, "harvestDate")
        val _columnIndexOfExpiryDate: Int = getColumnIndexOrThrow(_stmt, "expiryDate")
        val _columnIndexOfBirthDate: Int = getColumnIndexOrThrow(_stmt, "birthDate")
        val _columnIndexOfVaccinationRecordsJson: Int = getColumnIndexOrThrow(_stmt,
            "vaccinationRecordsJson")
        val _columnIndexOfWeightGrams: Int = getColumnIndexOrThrow(_stmt, "weightGrams")
        val _columnIndexOfHeightCm: Int = getColumnIndexOrThrow(_stmt, "heightCm")
        val _columnIndexOfGender: Int = getColumnIndexOrThrow(_stmt, "gender")
        val _columnIndexOfColor: Int = getColumnIndexOrThrow(_stmt, "color")
        val _columnIndexOfBreed: Int = getColumnIndexOrThrow(_stmt, "breed")
        val _columnIndexOfRaisingPurpose: Int = getColumnIndexOrThrow(_stmt, "raisingPurpose")
        val _columnIndexOfHealthStatus: Int = getColumnIndexOrThrow(_stmt, "healthStatus")
        val _columnIndexOfBirdCode: Int = getColumnIndexOrThrow(_stmt, "birdCode")
        val _columnIndexOfColorTag: Int = getColumnIndexOrThrow(_stmt, "colorTag")
        val _columnIndexOfFamilyTreeId: Int = getColumnIndexOrThrow(_stmt, "familyTreeId")
        val _columnIndexOfSourceAssetId: Int = getColumnIndexOrThrow(_stmt, "sourceAssetId")
        val _columnIndexOfParentIdsJson: Int = getColumnIndexOrThrow(_stmt, "parentIdsJson")
        val _columnIndexOfBreedingStatus: Int = getColumnIndexOrThrow(_stmt, "breedingStatus")
        val _columnIndexOfTransferHistoryJson: Int = getColumnIndexOrThrow(_stmt,
            "transferHistoryJson")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfLastModifiedAt: Int = getColumnIndexOrThrow(_stmt, "lastModifiedAt")
        val _columnIndexOfIsDeleted: Int = getColumnIndexOrThrow(_stmt, "isDeleted")
        val _columnIndexOfDeletedAt: Int = getColumnIndexOrThrow(_stmt, "deletedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfStage: Int = getColumnIndexOrThrow(_stmt, "stage")
        val _columnIndexOfLifecycleStatus: Int = getColumnIndexOrThrow(_stmt, "lifecycleStatus")
        val _columnIndexOfParentMaleId: Int = getColumnIndexOrThrow(_stmt, "parentMaleId")
        val _columnIndexOfParentFemaleId: Int = getColumnIndexOrThrow(_stmt, "parentFemaleId")
        val _columnIndexOfAgeWeeks: Int = getColumnIndexOrThrow(_stmt, "ageWeeks")
        val _columnIndexOfLastStageTransitionAt: Int = getColumnIndexOrThrow(_stmt,
            "lastStageTransitionAt")
        val _columnIndexOfBreederEligibleAt: Int = getColumnIndexOrThrow(_stmt, "breederEligibleAt")
        val _columnIndexOfIsBatch: Int = getColumnIndexOrThrow(_stmt, "isBatch")
        val _columnIndexOfBatchId: Int = getColumnIndexOrThrow(_stmt, "batchId")
        val _columnIndexOfSplitAt: Int = getColumnIndexOrThrow(_stmt, "splitAt")
        val _columnIndexOfSplitIntoIds: Int = getColumnIndexOrThrow(_stmt, "splitIntoIds")
        val _columnIndexOfDocumentUrls: Int = getColumnIndexOrThrow(_stmt, "documentUrls")
        val _columnIndexOfQrCodeUrl: Int = getColumnIndexOrThrow(_stmt, "qrCodeUrl")
        val _columnIndexOfCustomStatus: Int = getColumnIndexOrThrow(_stmt, "customStatus")
        val _columnIndexOfDebug: Int = getColumnIndexOrThrow(_stmt, "debug")
        val _columnIndexOfDeliveryOptions: Int = getColumnIndexOrThrow(_stmt, "deliveryOptions")
        val _columnIndexOfDeliveryCost: Int = getColumnIndexOrThrow(_stmt, "deliveryCost")
        val _columnIndexOfLeadTimeDays: Int = getColumnIndexOrThrow(_stmt, "leadTimeDays")
        val _columnIndexOfMotherId: Int = getColumnIndexOrThrow(_stmt, "motherId")
        val _columnIndexOfIsBreedingUnit: Int = getColumnIndexOrThrow(_stmt, "isBreedingUnit")
        val _columnIndexOfEggsCollectedToday: Int = getColumnIndexOrThrow(_stmt,
            "eggsCollectedToday")
        val _columnIndexOfLastEggLogDate: Int = getColumnIndexOrThrow(_stmt, "lastEggLogDate")
        val _columnIndexOfReadyForSale: Int = getColumnIndexOrThrow(_stmt, "readyForSale")
        val _columnIndexOfTargetWeight: Int = getColumnIndexOrThrow(_stmt, "targetWeight")
        val _columnIndexOfIsShowcased: Int = getColumnIndexOrThrow(_stmt, "isShowcased")
        val _columnIndexOfExternalVideoUrl: Int = getColumnIndexOrThrow(_stmt, "externalVideoUrl")
        val _columnIndexOfRecordsLockedAt: Int = getColumnIndexOrThrow(_stmt, "recordsLockedAt")
        val _columnIndexOfAutoLockAfterDays: Int = getColumnIndexOrThrow(_stmt, "autoLockAfterDays")
        val _columnIndexOfLineageHistoryJson: Int = getColumnIndexOrThrow(_stmt,
            "lineageHistoryJson")
        val _columnIndexOfEditCount: Int = getColumnIndexOrThrow(_stmt, "editCount")
        val _columnIndexOfLastEditedBy: Int = getColumnIndexOrThrow(_stmt, "lastEditedBy")
        val _columnIndexOfAdminFlagged: Int = getColumnIndexOrThrow(_stmt, "adminFlagged")
        val _columnIndexOfModerationNote: Int = getColumnIndexOrThrow(_stmt, "moderationNote")
        val _columnIndexOfMetadataJson: Int = getColumnIndexOrThrow(_stmt, "metadataJson")
        val _result: MutableList<ProductEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: ProductEntity
          val _tmpProductId: String
          _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          val _tmpSellerId: String
          _tmpSellerId = _stmt.getText(_columnIndexOfSellerId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpDescription: String
          _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          val _tmpCategory: String
          _tmpCategory = _stmt.getText(_columnIndexOfCategory)
          val _tmpPrice: Double
          _tmpPrice = _stmt.getDouble(_columnIndexOfPrice)
          val _tmpQuantity: Double
          _tmpQuantity = _stmt.getDouble(_columnIndexOfQuantity)
          val _tmpUnit: String
          _tmpUnit = _stmt.getText(_columnIndexOfUnit)
          val _tmpLocation: String
          _tmpLocation = _stmt.getText(_columnIndexOfLocation)
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
          val _tmpImageUrls: List<String>
          val _tmp: String?
          if (_stmt.isNull(_columnIndexOfImageUrls)) {
            _tmp = null
          } else {
            _tmp = _stmt.getText(_columnIndexOfImageUrls)
          }
          val _tmp_1: List<String>? = AppDatabase.Converters.toStringList(_tmp)
          if (_tmp_1 == null) {
            error("Expected NON-NULL 'kotlin.collections.List<kotlin.String>', but it was NULL.")
          } else {
            _tmpImageUrls = _tmp_1
          }
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpCondition: String?
          if (_stmt.isNull(_columnIndexOfCondition)) {
            _tmpCondition = null
          } else {
            _tmpCondition = _stmt.getText(_columnIndexOfCondition)
          }
          val _tmpHarvestDate: Long?
          if (_stmt.isNull(_columnIndexOfHarvestDate)) {
            _tmpHarvestDate = null
          } else {
            _tmpHarvestDate = _stmt.getLong(_columnIndexOfHarvestDate)
          }
          val _tmpExpiryDate: Long?
          if (_stmt.isNull(_columnIndexOfExpiryDate)) {
            _tmpExpiryDate = null
          } else {
            _tmpExpiryDate = _stmt.getLong(_columnIndexOfExpiryDate)
          }
          val _tmpBirthDate: Long?
          if (_stmt.isNull(_columnIndexOfBirthDate)) {
            _tmpBirthDate = null
          } else {
            _tmpBirthDate = _stmt.getLong(_columnIndexOfBirthDate)
          }
          val _tmpVaccinationRecordsJson: String?
          if (_stmt.isNull(_columnIndexOfVaccinationRecordsJson)) {
            _tmpVaccinationRecordsJson = null
          } else {
            _tmpVaccinationRecordsJson = _stmt.getText(_columnIndexOfVaccinationRecordsJson)
          }
          val _tmpWeightGrams: Double?
          if (_stmt.isNull(_columnIndexOfWeightGrams)) {
            _tmpWeightGrams = null
          } else {
            _tmpWeightGrams = _stmt.getDouble(_columnIndexOfWeightGrams)
          }
          val _tmpHeightCm: Double?
          if (_stmt.isNull(_columnIndexOfHeightCm)) {
            _tmpHeightCm = null
          } else {
            _tmpHeightCm = _stmt.getDouble(_columnIndexOfHeightCm)
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
          val _tmpBreed: String?
          if (_stmt.isNull(_columnIndexOfBreed)) {
            _tmpBreed = null
          } else {
            _tmpBreed = _stmt.getText(_columnIndexOfBreed)
          }
          val _tmpRaisingPurpose: String?
          if (_stmt.isNull(_columnIndexOfRaisingPurpose)) {
            _tmpRaisingPurpose = null
          } else {
            _tmpRaisingPurpose = _stmt.getText(_columnIndexOfRaisingPurpose)
          }
          val _tmpHealthStatus: String?
          if (_stmt.isNull(_columnIndexOfHealthStatus)) {
            _tmpHealthStatus = null
          } else {
            _tmpHealthStatus = _stmt.getText(_columnIndexOfHealthStatus)
          }
          val _tmpBirdCode: String?
          if (_stmt.isNull(_columnIndexOfBirdCode)) {
            _tmpBirdCode = null
          } else {
            _tmpBirdCode = _stmt.getText(_columnIndexOfBirdCode)
          }
          val _tmpColorTag: String?
          if (_stmt.isNull(_columnIndexOfColorTag)) {
            _tmpColorTag = null
          } else {
            _tmpColorTag = _stmt.getText(_columnIndexOfColorTag)
          }
          val _tmpFamilyTreeId: String?
          if (_stmt.isNull(_columnIndexOfFamilyTreeId)) {
            _tmpFamilyTreeId = null
          } else {
            _tmpFamilyTreeId = _stmt.getText(_columnIndexOfFamilyTreeId)
          }
          val _tmpSourceAssetId: String?
          if (_stmt.isNull(_columnIndexOfSourceAssetId)) {
            _tmpSourceAssetId = null
          } else {
            _tmpSourceAssetId = _stmt.getText(_columnIndexOfSourceAssetId)
          }
          val _tmpParentIdsJson: String?
          if (_stmt.isNull(_columnIndexOfParentIdsJson)) {
            _tmpParentIdsJson = null
          } else {
            _tmpParentIdsJson = _stmt.getText(_columnIndexOfParentIdsJson)
          }
          val _tmpBreedingStatus: String?
          if (_stmt.isNull(_columnIndexOfBreedingStatus)) {
            _tmpBreedingStatus = null
          } else {
            _tmpBreedingStatus = _stmt.getText(_columnIndexOfBreedingStatus)
          }
          val _tmpTransferHistoryJson: String?
          if (_stmt.isNull(_columnIndexOfTransferHistoryJson)) {
            _tmpTransferHistoryJson = null
          } else {
            _tmpTransferHistoryJson = _stmt.getText(_columnIndexOfTransferHistoryJson)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpLastModifiedAt: Long
          _tmpLastModifiedAt = _stmt.getLong(_columnIndexOfLastModifiedAt)
          val _tmpIsDeleted: Boolean
          val _tmp_2: Int
          _tmp_2 = _stmt.getLong(_columnIndexOfIsDeleted).toInt()
          _tmpIsDeleted = _tmp_2 != 0
          val _tmpDeletedAt: Long?
          if (_stmt.isNull(_columnIndexOfDeletedAt)) {
            _tmpDeletedAt = null
          } else {
            _tmpDeletedAt = _stmt.getLong(_columnIndexOfDeletedAt)
          }
          val _tmpDirty: Boolean
          val _tmp_3: Int
          _tmp_3 = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp_3 != 0
          val _tmpStage: LifecycleStage?
          val _tmp_4: String?
          if (_stmt.isNull(_columnIndexOfStage)) {
            _tmp_4 = null
          } else {
            _tmp_4 = _stmt.getText(_columnIndexOfStage)
          }
          _tmpStage = AppDatabase.Converters.toLifecycleStage(_tmp_4)
          val _tmpLifecycleStatus: String?
          if (_stmt.isNull(_columnIndexOfLifecycleStatus)) {
            _tmpLifecycleStatus = null
          } else {
            _tmpLifecycleStatus = _stmt.getText(_columnIndexOfLifecycleStatus)
          }
          val _tmpParentMaleId: String?
          if (_stmt.isNull(_columnIndexOfParentMaleId)) {
            _tmpParentMaleId = null
          } else {
            _tmpParentMaleId = _stmt.getText(_columnIndexOfParentMaleId)
          }
          val _tmpParentFemaleId: String?
          if (_stmt.isNull(_columnIndexOfParentFemaleId)) {
            _tmpParentFemaleId = null
          } else {
            _tmpParentFemaleId = _stmt.getText(_columnIndexOfParentFemaleId)
          }
          val _tmpAgeWeeks: Int?
          if (_stmt.isNull(_columnIndexOfAgeWeeks)) {
            _tmpAgeWeeks = null
          } else {
            _tmpAgeWeeks = _stmt.getLong(_columnIndexOfAgeWeeks).toInt()
          }
          val _tmpLastStageTransitionAt: Long?
          if (_stmt.isNull(_columnIndexOfLastStageTransitionAt)) {
            _tmpLastStageTransitionAt = null
          } else {
            _tmpLastStageTransitionAt = _stmt.getLong(_columnIndexOfLastStageTransitionAt)
          }
          val _tmpBreederEligibleAt: Long?
          if (_stmt.isNull(_columnIndexOfBreederEligibleAt)) {
            _tmpBreederEligibleAt = null
          } else {
            _tmpBreederEligibleAt = _stmt.getLong(_columnIndexOfBreederEligibleAt)
          }
          val _tmpIsBatch: Boolean?
          val _tmp_5: Int?
          if (_stmt.isNull(_columnIndexOfIsBatch)) {
            _tmp_5 = null
          } else {
            _tmp_5 = _stmt.getLong(_columnIndexOfIsBatch).toInt()
          }
          _tmpIsBatch = _tmp_5?.let { it != 0 }
          val _tmpBatchId: String?
          if (_stmt.isNull(_columnIndexOfBatchId)) {
            _tmpBatchId = null
          } else {
            _tmpBatchId = _stmt.getText(_columnIndexOfBatchId)
          }
          val _tmpSplitAt: Long?
          if (_stmt.isNull(_columnIndexOfSplitAt)) {
            _tmpSplitAt = null
          } else {
            _tmpSplitAt = _stmt.getLong(_columnIndexOfSplitAt)
          }
          val _tmpSplitIntoIds: String?
          if (_stmt.isNull(_columnIndexOfSplitIntoIds)) {
            _tmpSplitIntoIds = null
          } else {
            _tmpSplitIntoIds = _stmt.getText(_columnIndexOfSplitIntoIds)
          }
          val _tmpDocumentUrls: List<String>
          val _tmp_6: String?
          if (_stmt.isNull(_columnIndexOfDocumentUrls)) {
            _tmp_6 = null
          } else {
            _tmp_6 = _stmt.getText(_columnIndexOfDocumentUrls)
          }
          val _tmp_7: List<String>? = AppDatabase.Converters.toStringList(_tmp_6)
          if (_tmp_7 == null) {
            error("Expected NON-NULL 'kotlin.collections.List<kotlin.String>', but it was NULL.")
          } else {
            _tmpDocumentUrls = _tmp_7
          }
          val _tmpQrCodeUrl: String?
          if (_stmt.isNull(_columnIndexOfQrCodeUrl)) {
            _tmpQrCodeUrl = null
          } else {
            _tmpQrCodeUrl = _stmt.getText(_columnIndexOfQrCodeUrl)
          }
          val _tmpCustomStatus: String?
          if (_stmt.isNull(_columnIndexOfCustomStatus)) {
            _tmpCustomStatus = null
          } else {
            _tmpCustomStatus = _stmt.getText(_columnIndexOfCustomStatus)
          }
          val _tmpDebug: Boolean
          val _tmp_8: Int
          _tmp_8 = _stmt.getLong(_columnIndexOfDebug).toInt()
          _tmpDebug = _tmp_8 != 0
          val _tmpDeliveryOptions: List<String>
          val _tmp_9: String?
          if (_stmt.isNull(_columnIndexOfDeliveryOptions)) {
            _tmp_9 = null
          } else {
            _tmp_9 = _stmt.getText(_columnIndexOfDeliveryOptions)
          }
          val _tmp_10: List<String>? = AppDatabase.Converters.toStringList(_tmp_9)
          if (_tmp_10 == null) {
            error("Expected NON-NULL 'kotlin.collections.List<kotlin.String>', but it was NULL.")
          } else {
            _tmpDeliveryOptions = _tmp_10
          }
          val _tmpDeliveryCost: Double?
          if (_stmt.isNull(_columnIndexOfDeliveryCost)) {
            _tmpDeliveryCost = null
          } else {
            _tmpDeliveryCost = _stmt.getDouble(_columnIndexOfDeliveryCost)
          }
          val _tmpLeadTimeDays: Int?
          if (_stmt.isNull(_columnIndexOfLeadTimeDays)) {
            _tmpLeadTimeDays = null
          } else {
            _tmpLeadTimeDays = _stmt.getLong(_columnIndexOfLeadTimeDays).toInt()
          }
          val _tmpMotherId: String?
          if (_stmt.isNull(_columnIndexOfMotherId)) {
            _tmpMotherId = null
          } else {
            _tmpMotherId = _stmt.getText(_columnIndexOfMotherId)
          }
          val _tmpIsBreedingUnit: Boolean
          val _tmp_11: Int
          _tmp_11 = _stmt.getLong(_columnIndexOfIsBreedingUnit).toInt()
          _tmpIsBreedingUnit = _tmp_11 != 0
          val _tmpEggsCollectedToday: Int
          _tmpEggsCollectedToday = _stmt.getLong(_columnIndexOfEggsCollectedToday).toInt()
          val _tmpLastEggLogDate: Long?
          if (_stmt.isNull(_columnIndexOfLastEggLogDate)) {
            _tmpLastEggLogDate = null
          } else {
            _tmpLastEggLogDate = _stmt.getLong(_columnIndexOfLastEggLogDate)
          }
          val _tmpReadyForSale: Boolean
          val _tmp_12: Int
          _tmp_12 = _stmt.getLong(_columnIndexOfReadyForSale).toInt()
          _tmpReadyForSale = _tmp_12 != 0
          val _tmpTargetWeight: Double?
          if (_stmt.isNull(_columnIndexOfTargetWeight)) {
            _tmpTargetWeight = null
          } else {
            _tmpTargetWeight = _stmt.getDouble(_columnIndexOfTargetWeight)
          }
          val _tmpIsShowcased: Boolean
          val _tmp_13: Int
          _tmp_13 = _stmt.getLong(_columnIndexOfIsShowcased).toInt()
          _tmpIsShowcased = _tmp_13 != 0
          val _tmpExternalVideoUrl: String?
          if (_stmt.isNull(_columnIndexOfExternalVideoUrl)) {
            _tmpExternalVideoUrl = null
          } else {
            _tmpExternalVideoUrl = _stmt.getText(_columnIndexOfExternalVideoUrl)
          }
          val _tmpRecordsLockedAt: Long?
          if (_stmt.isNull(_columnIndexOfRecordsLockedAt)) {
            _tmpRecordsLockedAt = null
          } else {
            _tmpRecordsLockedAt = _stmt.getLong(_columnIndexOfRecordsLockedAt)
          }
          val _tmpAutoLockAfterDays: Int
          _tmpAutoLockAfterDays = _stmt.getLong(_columnIndexOfAutoLockAfterDays).toInt()
          val _tmpLineageHistoryJson: String?
          if (_stmt.isNull(_columnIndexOfLineageHistoryJson)) {
            _tmpLineageHistoryJson = null
          } else {
            _tmpLineageHistoryJson = _stmt.getText(_columnIndexOfLineageHistoryJson)
          }
          val _tmpEditCount: Int
          _tmpEditCount = _stmt.getLong(_columnIndexOfEditCount).toInt()
          val _tmpLastEditedBy: String?
          if (_stmt.isNull(_columnIndexOfLastEditedBy)) {
            _tmpLastEditedBy = null
          } else {
            _tmpLastEditedBy = _stmt.getText(_columnIndexOfLastEditedBy)
          }
          val _tmpAdminFlagged: Boolean
          val _tmp_14: Int
          _tmp_14 = _stmt.getLong(_columnIndexOfAdminFlagged).toInt()
          _tmpAdminFlagged = _tmp_14 != 0
          val _tmpModerationNote: String?
          if (_stmt.isNull(_columnIndexOfModerationNote)) {
            _tmpModerationNote = null
          } else {
            _tmpModerationNote = _stmt.getText(_columnIndexOfModerationNote)
          }
          val _tmpMetadataJson: String
          _tmpMetadataJson = _stmt.getText(_columnIndexOfMetadataJson)
          _item =
              ProductEntity(_tmpProductId,_tmpSellerId,_tmpName,_tmpDescription,_tmpCategory,_tmpPrice,_tmpQuantity,_tmpUnit,_tmpLocation,_tmpLatitude,_tmpLongitude,_tmpImageUrls,_tmpStatus,_tmpCondition,_tmpHarvestDate,_tmpExpiryDate,_tmpBirthDate,_tmpVaccinationRecordsJson,_tmpWeightGrams,_tmpHeightCm,_tmpGender,_tmpColor,_tmpBreed,_tmpRaisingPurpose,_tmpHealthStatus,_tmpBirdCode,_tmpColorTag,_tmpFamilyTreeId,_tmpSourceAssetId,_tmpParentIdsJson,_tmpBreedingStatus,_tmpTransferHistoryJson,_tmpCreatedAt,_tmpUpdatedAt,_tmpLastModifiedAt,_tmpIsDeleted,_tmpDeletedAt,_tmpDirty,_tmpStage,_tmpLifecycleStatus,_tmpParentMaleId,_tmpParentFemaleId,_tmpAgeWeeks,_tmpLastStageTransitionAt,_tmpBreederEligibleAt,_tmpIsBatch,_tmpBatchId,_tmpSplitAt,_tmpSplitIntoIds,_tmpDocumentUrls,_tmpQrCodeUrl,_tmpCustomStatus,_tmpDebug,_tmpDeliveryOptions,_tmpDeliveryCost,_tmpLeadTimeDays,_tmpMotherId,_tmpIsBreedingUnit,_tmpEggsCollectedToday,_tmpLastEggLogDate,_tmpReadyForSale,_tmpTargetWeight,_tmpIsShowcased,_tmpExternalVideoUrl,_tmpRecordsLockedAt,_tmpAutoLockAfterDays,_tmpLineageHistoryJson,_tmpEditCount,_tmpLastEditedBy,_tmpAdminFlagged,_tmpModerationNote,_tmpMetadataJson)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun filterByPriceBreed(
    minPrice: Double,
    maxPrice: Double,
    breed: String?,
    limit: Int,
    offset: Int,
  ): List<ProductEntity> {
    val _sql: String =
        "SELECT * FROM products WHERE price BETWEEN ? AND ? AND (? IS NULL OR breed = ?) AND isDeleted = 0 ORDER BY updatedAt DESC LIMIT ? OFFSET ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindDouble(_argIndex, minPrice)
        _argIndex = 2
        _stmt.bindDouble(_argIndex, maxPrice)
        _argIndex = 3
        if (breed == null) {
          _stmt.bindNull(_argIndex)
        } else {
          _stmt.bindText(_argIndex, breed)
        }
        _argIndex = 4
        if (breed == null) {
          _stmt.bindNull(_argIndex)
        } else {
          _stmt.bindText(_argIndex, breed)
        }
        _argIndex = 5
        _stmt.bindLong(_argIndex, limit.toLong())
        _argIndex = 6
        _stmt.bindLong(_argIndex, offset.toLong())
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfSellerId: Int = getColumnIndexOrThrow(_stmt, "sellerId")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfCategory: Int = getColumnIndexOrThrow(_stmt, "category")
        val _columnIndexOfPrice: Int = getColumnIndexOrThrow(_stmt, "price")
        val _columnIndexOfQuantity: Int = getColumnIndexOrThrow(_stmt, "quantity")
        val _columnIndexOfUnit: Int = getColumnIndexOrThrow(_stmt, "unit")
        val _columnIndexOfLocation: Int = getColumnIndexOrThrow(_stmt, "location")
        val _columnIndexOfLatitude: Int = getColumnIndexOrThrow(_stmt, "latitude")
        val _columnIndexOfLongitude: Int = getColumnIndexOrThrow(_stmt, "longitude")
        val _columnIndexOfImageUrls: Int = getColumnIndexOrThrow(_stmt, "imageUrls")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfCondition: Int = getColumnIndexOrThrow(_stmt, "condition")
        val _columnIndexOfHarvestDate: Int = getColumnIndexOrThrow(_stmt, "harvestDate")
        val _columnIndexOfExpiryDate: Int = getColumnIndexOrThrow(_stmt, "expiryDate")
        val _columnIndexOfBirthDate: Int = getColumnIndexOrThrow(_stmt, "birthDate")
        val _columnIndexOfVaccinationRecordsJson: Int = getColumnIndexOrThrow(_stmt,
            "vaccinationRecordsJson")
        val _columnIndexOfWeightGrams: Int = getColumnIndexOrThrow(_stmt, "weightGrams")
        val _columnIndexOfHeightCm: Int = getColumnIndexOrThrow(_stmt, "heightCm")
        val _columnIndexOfGender: Int = getColumnIndexOrThrow(_stmt, "gender")
        val _columnIndexOfColor: Int = getColumnIndexOrThrow(_stmt, "color")
        val _columnIndexOfBreed: Int = getColumnIndexOrThrow(_stmt, "breed")
        val _columnIndexOfRaisingPurpose: Int = getColumnIndexOrThrow(_stmt, "raisingPurpose")
        val _columnIndexOfHealthStatus: Int = getColumnIndexOrThrow(_stmt, "healthStatus")
        val _columnIndexOfBirdCode: Int = getColumnIndexOrThrow(_stmt, "birdCode")
        val _columnIndexOfColorTag: Int = getColumnIndexOrThrow(_stmt, "colorTag")
        val _columnIndexOfFamilyTreeId: Int = getColumnIndexOrThrow(_stmt, "familyTreeId")
        val _columnIndexOfSourceAssetId: Int = getColumnIndexOrThrow(_stmt, "sourceAssetId")
        val _columnIndexOfParentIdsJson: Int = getColumnIndexOrThrow(_stmt, "parentIdsJson")
        val _columnIndexOfBreedingStatus: Int = getColumnIndexOrThrow(_stmt, "breedingStatus")
        val _columnIndexOfTransferHistoryJson: Int = getColumnIndexOrThrow(_stmt,
            "transferHistoryJson")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfLastModifiedAt: Int = getColumnIndexOrThrow(_stmt, "lastModifiedAt")
        val _columnIndexOfIsDeleted: Int = getColumnIndexOrThrow(_stmt, "isDeleted")
        val _columnIndexOfDeletedAt: Int = getColumnIndexOrThrow(_stmt, "deletedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfStage: Int = getColumnIndexOrThrow(_stmt, "stage")
        val _columnIndexOfLifecycleStatus: Int = getColumnIndexOrThrow(_stmt, "lifecycleStatus")
        val _columnIndexOfParentMaleId: Int = getColumnIndexOrThrow(_stmt, "parentMaleId")
        val _columnIndexOfParentFemaleId: Int = getColumnIndexOrThrow(_stmt, "parentFemaleId")
        val _columnIndexOfAgeWeeks: Int = getColumnIndexOrThrow(_stmt, "ageWeeks")
        val _columnIndexOfLastStageTransitionAt: Int = getColumnIndexOrThrow(_stmt,
            "lastStageTransitionAt")
        val _columnIndexOfBreederEligibleAt: Int = getColumnIndexOrThrow(_stmt, "breederEligibleAt")
        val _columnIndexOfIsBatch: Int = getColumnIndexOrThrow(_stmt, "isBatch")
        val _columnIndexOfBatchId: Int = getColumnIndexOrThrow(_stmt, "batchId")
        val _columnIndexOfSplitAt: Int = getColumnIndexOrThrow(_stmt, "splitAt")
        val _columnIndexOfSplitIntoIds: Int = getColumnIndexOrThrow(_stmt, "splitIntoIds")
        val _columnIndexOfDocumentUrls: Int = getColumnIndexOrThrow(_stmt, "documentUrls")
        val _columnIndexOfQrCodeUrl: Int = getColumnIndexOrThrow(_stmt, "qrCodeUrl")
        val _columnIndexOfCustomStatus: Int = getColumnIndexOrThrow(_stmt, "customStatus")
        val _columnIndexOfDebug: Int = getColumnIndexOrThrow(_stmt, "debug")
        val _columnIndexOfDeliveryOptions: Int = getColumnIndexOrThrow(_stmt, "deliveryOptions")
        val _columnIndexOfDeliveryCost: Int = getColumnIndexOrThrow(_stmt, "deliveryCost")
        val _columnIndexOfLeadTimeDays: Int = getColumnIndexOrThrow(_stmt, "leadTimeDays")
        val _columnIndexOfMotherId: Int = getColumnIndexOrThrow(_stmt, "motherId")
        val _columnIndexOfIsBreedingUnit: Int = getColumnIndexOrThrow(_stmt, "isBreedingUnit")
        val _columnIndexOfEggsCollectedToday: Int = getColumnIndexOrThrow(_stmt,
            "eggsCollectedToday")
        val _columnIndexOfLastEggLogDate: Int = getColumnIndexOrThrow(_stmt, "lastEggLogDate")
        val _columnIndexOfReadyForSale: Int = getColumnIndexOrThrow(_stmt, "readyForSale")
        val _columnIndexOfTargetWeight: Int = getColumnIndexOrThrow(_stmt, "targetWeight")
        val _columnIndexOfIsShowcased: Int = getColumnIndexOrThrow(_stmt, "isShowcased")
        val _columnIndexOfExternalVideoUrl: Int = getColumnIndexOrThrow(_stmt, "externalVideoUrl")
        val _columnIndexOfRecordsLockedAt: Int = getColumnIndexOrThrow(_stmt, "recordsLockedAt")
        val _columnIndexOfAutoLockAfterDays: Int = getColumnIndexOrThrow(_stmt, "autoLockAfterDays")
        val _columnIndexOfLineageHistoryJson: Int = getColumnIndexOrThrow(_stmt,
            "lineageHistoryJson")
        val _columnIndexOfEditCount: Int = getColumnIndexOrThrow(_stmt, "editCount")
        val _columnIndexOfLastEditedBy: Int = getColumnIndexOrThrow(_stmt, "lastEditedBy")
        val _columnIndexOfAdminFlagged: Int = getColumnIndexOrThrow(_stmt, "adminFlagged")
        val _columnIndexOfModerationNote: Int = getColumnIndexOrThrow(_stmt, "moderationNote")
        val _columnIndexOfMetadataJson: Int = getColumnIndexOrThrow(_stmt, "metadataJson")
        val _result: MutableList<ProductEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: ProductEntity
          val _tmpProductId: String
          _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          val _tmpSellerId: String
          _tmpSellerId = _stmt.getText(_columnIndexOfSellerId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpDescription: String
          _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          val _tmpCategory: String
          _tmpCategory = _stmt.getText(_columnIndexOfCategory)
          val _tmpPrice: Double
          _tmpPrice = _stmt.getDouble(_columnIndexOfPrice)
          val _tmpQuantity: Double
          _tmpQuantity = _stmt.getDouble(_columnIndexOfQuantity)
          val _tmpUnit: String
          _tmpUnit = _stmt.getText(_columnIndexOfUnit)
          val _tmpLocation: String
          _tmpLocation = _stmt.getText(_columnIndexOfLocation)
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
          val _tmpImageUrls: List<String>
          val _tmp: String?
          if (_stmt.isNull(_columnIndexOfImageUrls)) {
            _tmp = null
          } else {
            _tmp = _stmt.getText(_columnIndexOfImageUrls)
          }
          val _tmp_1: List<String>? = AppDatabase.Converters.toStringList(_tmp)
          if (_tmp_1 == null) {
            error("Expected NON-NULL 'kotlin.collections.List<kotlin.String>', but it was NULL.")
          } else {
            _tmpImageUrls = _tmp_1
          }
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpCondition: String?
          if (_stmt.isNull(_columnIndexOfCondition)) {
            _tmpCondition = null
          } else {
            _tmpCondition = _stmt.getText(_columnIndexOfCondition)
          }
          val _tmpHarvestDate: Long?
          if (_stmt.isNull(_columnIndexOfHarvestDate)) {
            _tmpHarvestDate = null
          } else {
            _tmpHarvestDate = _stmt.getLong(_columnIndexOfHarvestDate)
          }
          val _tmpExpiryDate: Long?
          if (_stmt.isNull(_columnIndexOfExpiryDate)) {
            _tmpExpiryDate = null
          } else {
            _tmpExpiryDate = _stmt.getLong(_columnIndexOfExpiryDate)
          }
          val _tmpBirthDate: Long?
          if (_stmt.isNull(_columnIndexOfBirthDate)) {
            _tmpBirthDate = null
          } else {
            _tmpBirthDate = _stmt.getLong(_columnIndexOfBirthDate)
          }
          val _tmpVaccinationRecordsJson: String?
          if (_stmt.isNull(_columnIndexOfVaccinationRecordsJson)) {
            _tmpVaccinationRecordsJson = null
          } else {
            _tmpVaccinationRecordsJson = _stmt.getText(_columnIndexOfVaccinationRecordsJson)
          }
          val _tmpWeightGrams: Double?
          if (_stmt.isNull(_columnIndexOfWeightGrams)) {
            _tmpWeightGrams = null
          } else {
            _tmpWeightGrams = _stmt.getDouble(_columnIndexOfWeightGrams)
          }
          val _tmpHeightCm: Double?
          if (_stmt.isNull(_columnIndexOfHeightCm)) {
            _tmpHeightCm = null
          } else {
            _tmpHeightCm = _stmt.getDouble(_columnIndexOfHeightCm)
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
          val _tmpBreed: String?
          if (_stmt.isNull(_columnIndexOfBreed)) {
            _tmpBreed = null
          } else {
            _tmpBreed = _stmt.getText(_columnIndexOfBreed)
          }
          val _tmpRaisingPurpose: String?
          if (_stmt.isNull(_columnIndexOfRaisingPurpose)) {
            _tmpRaisingPurpose = null
          } else {
            _tmpRaisingPurpose = _stmt.getText(_columnIndexOfRaisingPurpose)
          }
          val _tmpHealthStatus: String?
          if (_stmt.isNull(_columnIndexOfHealthStatus)) {
            _tmpHealthStatus = null
          } else {
            _tmpHealthStatus = _stmt.getText(_columnIndexOfHealthStatus)
          }
          val _tmpBirdCode: String?
          if (_stmt.isNull(_columnIndexOfBirdCode)) {
            _tmpBirdCode = null
          } else {
            _tmpBirdCode = _stmt.getText(_columnIndexOfBirdCode)
          }
          val _tmpColorTag: String?
          if (_stmt.isNull(_columnIndexOfColorTag)) {
            _tmpColorTag = null
          } else {
            _tmpColorTag = _stmt.getText(_columnIndexOfColorTag)
          }
          val _tmpFamilyTreeId: String?
          if (_stmt.isNull(_columnIndexOfFamilyTreeId)) {
            _tmpFamilyTreeId = null
          } else {
            _tmpFamilyTreeId = _stmt.getText(_columnIndexOfFamilyTreeId)
          }
          val _tmpSourceAssetId: String?
          if (_stmt.isNull(_columnIndexOfSourceAssetId)) {
            _tmpSourceAssetId = null
          } else {
            _tmpSourceAssetId = _stmt.getText(_columnIndexOfSourceAssetId)
          }
          val _tmpParentIdsJson: String?
          if (_stmt.isNull(_columnIndexOfParentIdsJson)) {
            _tmpParentIdsJson = null
          } else {
            _tmpParentIdsJson = _stmt.getText(_columnIndexOfParentIdsJson)
          }
          val _tmpBreedingStatus: String?
          if (_stmt.isNull(_columnIndexOfBreedingStatus)) {
            _tmpBreedingStatus = null
          } else {
            _tmpBreedingStatus = _stmt.getText(_columnIndexOfBreedingStatus)
          }
          val _tmpTransferHistoryJson: String?
          if (_stmt.isNull(_columnIndexOfTransferHistoryJson)) {
            _tmpTransferHistoryJson = null
          } else {
            _tmpTransferHistoryJson = _stmt.getText(_columnIndexOfTransferHistoryJson)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpLastModifiedAt: Long
          _tmpLastModifiedAt = _stmt.getLong(_columnIndexOfLastModifiedAt)
          val _tmpIsDeleted: Boolean
          val _tmp_2: Int
          _tmp_2 = _stmt.getLong(_columnIndexOfIsDeleted).toInt()
          _tmpIsDeleted = _tmp_2 != 0
          val _tmpDeletedAt: Long?
          if (_stmt.isNull(_columnIndexOfDeletedAt)) {
            _tmpDeletedAt = null
          } else {
            _tmpDeletedAt = _stmt.getLong(_columnIndexOfDeletedAt)
          }
          val _tmpDirty: Boolean
          val _tmp_3: Int
          _tmp_3 = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp_3 != 0
          val _tmpStage: LifecycleStage?
          val _tmp_4: String?
          if (_stmt.isNull(_columnIndexOfStage)) {
            _tmp_4 = null
          } else {
            _tmp_4 = _stmt.getText(_columnIndexOfStage)
          }
          _tmpStage = AppDatabase.Converters.toLifecycleStage(_tmp_4)
          val _tmpLifecycleStatus: String?
          if (_stmt.isNull(_columnIndexOfLifecycleStatus)) {
            _tmpLifecycleStatus = null
          } else {
            _tmpLifecycleStatus = _stmt.getText(_columnIndexOfLifecycleStatus)
          }
          val _tmpParentMaleId: String?
          if (_stmt.isNull(_columnIndexOfParentMaleId)) {
            _tmpParentMaleId = null
          } else {
            _tmpParentMaleId = _stmt.getText(_columnIndexOfParentMaleId)
          }
          val _tmpParentFemaleId: String?
          if (_stmt.isNull(_columnIndexOfParentFemaleId)) {
            _tmpParentFemaleId = null
          } else {
            _tmpParentFemaleId = _stmt.getText(_columnIndexOfParentFemaleId)
          }
          val _tmpAgeWeeks: Int?
          if (_stmt.isNull(_columnIndexOfAgeWeeks)) {
            _tmpAgeWeeks = null
          } else {
            _tmpAgeWeeks = _stmt.getLong(_columnIndexOfAgeWeeks).toInt()
          }
          val _tmpLastStageTransitionAt: Long?
          if (_stmt.isNull(_columnIndexOfLastStageTransitionAt)) {
            _tmpLastStageTransitionAt = null
          } else {
            _tmpLastStageTransitionAt = _stmt.getLong(_columnIndexOfLastStageTransitionAt)
          }
          val _tmpBreederEligibleAt: Long?
          if (_stmt.isNull(_columnIndexOfBreederEligibleAt)) {
            _tmpBreederEligibleAt = null
          } else {
            _tmpBreederEligibleAt = _stmt.getLong(_columnIndexOfBreederEligibleAt)
          }
          val _tmpIsBatch: Boolean?
          val _tmp_5: Int?
          if (_stmt.isNull(_columnIndexOfIsBatch)) {
            _tmp_5 = null
          } else {
            _tmp_5 = _stmt.getLong(_columnIndexOfIsBatch).toInt()
          }
          _tmpIsBatch = _tmp_5?.let { it != 0 }
          val _tmpBatchId: String?
          if (_stmt.isNull(_columnIndexOfBatchId)) {
            _tmpBatchId = null
          } else {
            _tmpBatchId = _stmt.getText(_columnIndexOfBatchId)
          }
          val _tmpSplitAt: Long?
          if (_stmt.isNull(_columnIndexOfSplitAt)) {
            _tmpSplitAt = null
          } else {
            _tmpSplitAt = _stmt.getLong(_columnIndexOfSplitAt)
          }
          val _tmpSplitIntoIds: String?
          if (_stmt.isNull(_columnIndexOfSplitIntoIds)) {
            _tmpSplitIntoIds = null
          } else {
            _tmpSplitIntoIds = _stmt.getText(_columnIndexOfSplitIntoIds)
          }
          val _tmpDocumentUrls: List<String>
          val _tmp_6: String?
          if (_stmt.isNull(_columnIndexOfDocumentUrls)) {
            _tmp_6 = null
          } else {
            _tmp_6 = _stmt.getText(_columnIndexOfDocumentUrls)
          }
          val _tmp_7: List<String>? = AppDatabase.Converters.toStringList(_tmp_6)
          if (_tmp_7 == null) {
            error("Expected NON-NULL 'kotlin.collections.List<kotlin.String>', but it was NULL.")
          } else {
            _tmpDocumentUrls = _tmp_7
          }
          val _tmpQrCodeUrl: String?
          if (_stmt.isNull(_columnIndexOfQrCodeUrl)) {
            _tmpQrCodeUrl = null
          } else {
            _tmpQrCodeUrl = _stmt.getText(_columnIndexOfQrCodeUrl)
          }
          val _tmpCustomStatus: String?
          if (_stmt.isNull(_columnIndexOfCustomStatus)) {
            _tmpCustomStatus = null
          } else {
            _tmpCustomStatus = _stmt.getText(_columnIndexOfCustomStatus)
          }
          val _tmpDebug: Boolean
          val _tmp_8: Int
          _tmp_8 = _stmt.getLong(_columnIndexOfDebug).toInt()
          _tmpDebug = _tmp_8 != 0
          val _tmpDeliveryOptions: List<String>
          val _tmp_9: String?
          if (_stmt.isNull(_columnIndexOfDeliveryOptions)) {
            _tmp_9 = null
          } else {
            _tmp_9 = _stmt.getText(_columnIndexOfDeliveryOptions)
          }
          val _tmp_10: List<String>? = AppDatabase.Converters.toStringList(_tmp_9)
          if (_tmp_10 == null) {
            error("Expected NON-NULL 'kotlin.collections.List<kotlin.String>', but it was NULL.")
          } else {
            _tmpDeliveryOptions = _tmp_10
          }
          val _tmpDeliveryCost: Double?
          if (_stmt.isNull(_columnIndexOfDeliveryCost)) {
            _tmpDeliveryCost = null
          } else {
            _tmpDeliveryCost = _stmt.getDouble(_columnIndexOfDeliveryCost)
          }
          val _tmpLeadTimeDays: Int?
          if (_stmt.isNull(_columnIndexOfLeadTimeDays)) {
            _tmpLeadTimeDays = null
          } else {
            _tmpLeadTimeDays = _stmt.getLong(_columnIndexOfLeadTimeDays).toInt()
          }
          val _tmpMotherId: String?
          if (_stmt.isNull(_columnIndexOfMotherId)) {
            _tmpMotherId = null
          } else {
            _tmpMotherId = _stmt.getText(_columnIndexOfMotherId)
          }
          val _tmpIsBreedingUnit: Boolean
          val _tmp_11: Int
          _tmp_11 = _stmt.getLong(_columnIndexOfIsBreedingUnit).toInt()
          _tmpIsBreedingUnit = _tmp_11 != 0
          val _tmpEggsCollectedToday: Int
          _tmpEggsCollectedToday = _stmt.getLong(_columnIndexOfEggsCollectedToday).toInt()
          val _tmpLastEggLogDate: Long?
          if (_stmt.isNull(_columnIndexOfLastEggLogDate)) {
            _tmpLastEggLogDate = null
          } else {
            _tmpLastEggLogDate = _stmt.getLong(_columnIndexOfLastEggLogDate)
          }
          val _tmpReadyForSale: Boolean
          val _tmp_12: Int
          _tmp_12 = _stmt.getLong(_columnIndexOfReadyForSale).toInt()
          _tmpReadyForSale = _tmp_12 != 0
          val _tmpTargetWeight: Double?
          if (_stmt.isNull(_columnIndexOfTargetWeight)) {
            _tmpTargetWeight = null
          } else {
            _tmpTargetWeight = _stmt.getDouble(_columnIndexOfTargetWeight)
          }
          val _tmpIsShowcased: Boolean
          val _tmp_13: Int
          _tmp_13 = _stmt.getLong(_columnIndexOfIsShowcased).toInt()
          _tmpIsShowcased = _tmp_13 != 0
          val _tmpExternalVideoUrl: String?
          if (_stmt.isNull(_columnIndexOfExternalVideoUrl)) {
            _tmpExternalVideoUrl = null
          } else {
            _tmpExternalVideoUrl = _stmt.getText(_columnIndexOfExternalVideoUrl)
          }
          val _tmpRecordsLockedAt: Long?
          if (_stmt.isNull(_columnIndexOfRecordsLockedAt)) {
            _tmpRecordsLockedAt = null
          } else {
            _tmpRecordsLockedAt = _stmt.getLong(_columnIndexOfRecordsLockedAt)
          }
          val _tmpAutoLockAfterDays: Int
          _tmpAutoLockAfterDays = _stmt.getLong(_columnIndexOfAutoLockAfterDays).toInt()
          val _tmpLineageHistoryJson: String?
          if (_stmt.isNull(_columnIndexOfLineageHistoryJson)) {
            _tmpLineageHistoryJson = null
          } else {
            _tmpLineageHistoryJson = _stmt.getText(_columnIndexOfLineageHistoryJson)
          }
          val _tmpEditCount: Int
          _tmpEditCount = _stmt.getLong(_columnIndexOfEditCount).toInt()
          val _tmpLastEditedBy: String?
          if (_stmt.isNull(_columnIndexOfLastEditedBy)) {
            _tmpLastEditedBy = null
          } else {
            _tmpLastEditedBy = _stmt.getText(_columnIndexOfLastEditedBy)
          }
          val _tmpAdminFlagged: Boolean
          val _tmp_14: Int
          _tmp_14 = _stmt.getLong(_columnIndexOfAdminFlagged).toInt()
          _tmpAdminFlagged = _tmp_14 != 0
          val _tmpModerationNote: String?
          if (_stmt.isNull(_columnIndexOfModerationNote)) {
            _tmpModerationNote = null
          } else {
            _tmpModerationNote = _stmt.getText(_columnIndexOfModerationNote)
          }
          val _tmpMetadataJson: String
          _tmpMetadataJson = _stmt.getText(_columnIndexOfMetadataJson)
          _item =
              ProductEntity(_tmpProductId,_tmpSellerId,_tmpName,_tmpDescription,_tmpCategory,_tmpPrice,_tmpQuantity,_tmpUnit,_tmpLocation,_tmpLatitude,_tmpLongitude,_tmpImageUrls,_tmpStatus,_tmpCondition,_tmpHarvestDate,_tmpExpiryDate,_tmpBirthDate,_tmpVaccinationRecordsJson,_tmpWeightGrams,_tmpHeightCm,_tmpGender,_tmpColor,_tmpBreed,_tmpRaisingPurpose,_tmpHealthStatus,_tmpBirdCode,_tmpColorTag,_tmpFamilyTreeId,_tmpSourceAssetId,_tmpParentIdsJson,_tmpBreedingStatus,_tmpTransferHistoryJson,_tmpCreatedAt,_tmpUpdatedAt,_tmpLastModifiedAt,_tmpIsDeleted,_tmpDeletedAt,_tmpDirty,_tmpStage,_tmpLifecycleStatus,_tmpParentMaleId,_tmpParentFemaleId,_tmpAgeWeeks,_tmpLastStageTransitionAt,_tmpBreederEligibleAt,_tmpIsBatch,_tmpBatchId,_tmpSplitAt,_tmpSplitIntoIds,_tmpDocumentUrls,_tmpQrCodeUrl,_tmpCustomStatus,_tmpDebug,_tmpDeliveryOptions,_tmpDeliveryCost,_tmpLeadTimeDays,_tmpMotherId,_tmpIsBreedingUnit,_tmpEggsCollectedToday,_tmpLastEggLogDate,_tmpReadyForSale,_tmpTargetWeight,_tmpIsShowcased,_tmpExternalVideoUrl,_tmpRecordsLockedAt,_tmpAutoLockAfterDays,_tmpLineageHistoryJson,_tmpEditCount,_tmpLastEditedBy,_tmpAdminFlagged,_tmpModerationNote,_tmpMetadataJson)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun filterByAge(
    minBirth: Long?,
    maxBirth: Long?,
    limit: Int,
    offset: Int,
  ): List<ProductEntity> {
    val _sql: String =
        "SELECT * FROM products WHERE (? IS NULL OR birthDate >= ?) AND (? IS NULL OR birthDate <= ?) AND isDeleted = 0 ORDER BY birthDate DESC LIMIT ? OFFSET ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        if (minBirth == null) {
          _stmt.bindNull(_argIndex)
        } else {
          _stmt.bindLong(_argIndex, minBirth)
        }
        _argIndex = 2
        if (minBirth == null) {
          _stmt.bindNull(_argIndex)
        } else {
          _stmt.bindLong(_argIndex, minBirth)
        }
        _argIndex = 3
        if (maxBirth == null) {
          _stmt.bindNull(_argIndex)
        } else {
          _stmt.bindLong(_argIndex, maxBirth)
        }
        _argIndex = 4
        if (maxBirth == null) {
          _stmt.bindNull(_argIndex)
        } else {
          _stmt.bindLong(_argIndex, maxBirth)
        }
        _argIndex = 5
        _stmt.bindLong(_argIndex, limit.toLong())
        _argIndex = 6
        _stmt.bindLong(_argIndex, offset.toLong())
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfSellerId: Int = getColumnIndexOrThrow(_stmt, "sellerId")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfCategory: Int = getColumnIndexOrThrow(_stmt, "category")
        val _columnIndexOfPrice: Int = getColumnIndexOrThrow(_stmt, "price")
        val _columnIndexOfQuantity: Int = getColumnIndexOrThrow(_stmt, "quantity")
        val _columnIndexOfUnit: Int = getColumnIndexOrThrow(_stmt, "unit")
        val _columnIndexOfLocation: Int = getColumnIndexOrThrow(_stmt, "location")
        val _columnIndexOfLatitude: Int = getColumnIndexOrThrow(_stmt, "latitude")
        val _columnIndexOfLongitude: Int = getColumnIndexOrThrow(_stmt, "longitude")
        val _columnIndexOfImageUrls: Int = getColumnIndexOrThrow(_stmt, "imageUrls")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfCondition: Int = getColumnIndexOrThrow(_stmt, "condition")
        val _columnIndexOfHarvestDate: Int = getColumnIndexOrThrow(_stmt, "harvestDate")
        val _columnIndexOfExpiryDate: Int = getColumnIndexOrThrow(_stmt, "expiryDate")
        val _columnIndexOfBirthDate: Int = getColumnIndexOrThrow(_stmt, "birthDate")
        val _columnIndexOfVaccinationRecordsJson: Int = getColumnIndexOrThrow(_stmt,
            "vaccinationRecordsJson")
        val _columnIndexOfWeightGrams: Int = getColumnIndexOrThrow(_stmt, "weightGrams")
        val _columnIndexOfHeightCm: Int = getColumnIndexOrThrow(_stmt, "heightCm")
        val _columnIndexOfGender: Int = getColumnIndexOrThrow(_stmt, "gender")
        val _columnIndexOfColor: Int = getColumnIndexOrThrow(_stmt, "color")
        val _columnIndexOfBreed: Int = getColumnIndexOrThrow(_stmt, "breed")
        val _columnIndexOfRaisingPurpose: Int = getColumnIndexOrThrow(_stmt, "raisingPurpose")
        val _columnIndexOfHealthStatus: Int = getColumnIndexOrThrow(_stmt, "healthStatus")
        val _columnIndexOfBirdCode: Int = getColumnIndexOrThrow(_stmt, "birdCode")
        val _columnIndexOfColorTag: Int = getColumnIndexOrThrow(_stmt, "colorTag")
        val _columnIndexOfFamilyTreeId: Int = getColumnIndexOrThrow(_stmt, "familyTreeId")
        val _columnIndexOfSourceAssetId: Int = getColumnIndexOrThrow(_stmt, "sourceAssetId")
        val _columnIndexOfParentIdsJson: Int = getColumnIndexOrThrow(_stmt, "parentIdsJson")
        val _columnIndexOfBreedingStatus: Int = getColumnIndexOrThrow(_stmt, "breedingStatus")
        val _columnIndexOfTransferHistoryJson: Int = getColumnIndexOrThrow(_stmt,
            "transferHistoryJson")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfLastModifiedAt: Int = getColumnIndexOrThrow(_stmt, "lastModifiedAt")
        val _columnIndexOfIsDeleted: Int = getColumnIndexOrThrow(_stmt, "isDeleted")
        val _columnIndexOfDeletedAt: Int = getColumnIndexOrThrow(_stmt, "deletedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfStage: Int = getColumnIndexOrThrow(_stmt, "stage")
        val _columnIndexOfLifecycleStatus: Int = getColumnIndexOrThrow(_stmt, "lifecycleStatus")
        val _columnIndexOfParentMaleId: Int = getColumnIndexOrThrow(_stmt, "parentMaleId")
        val _columnIndexOfParentFemaleId: Int = getColumnIndexOrThrow(_stmt, "parentFemaleId")
        val _columnIndexOfAgeWeeks: Int = getColumnIndexOrThrow(_stmt, "ageWeeks")
        val _columnIndexOfLastStageTransitionAt: Int = getColumnIndexOrThrow(_stmt,
            "lastStageTransitionAt")
        val _columnIndexOfBreederEligibleAt: Int = getColumnIndexOrThrow(_stmt, "breederEligibleAt")
        val _columnIndexOfIsBatch: Int = getColumnIndexOrThrow(_stmt, "isBatch")
        val _columnIndexOfBatchId: Int = getColumnIndexOrThrow(_stmt, "batchId")
        val _columnIndexOfSplitAt: Int = getColumnIndexOrThrow(_stmt, "splitAt")
        val _columnIndexOfSplitIntoIds: Int = getColumnIndexOrThrow(_stmt, "splitIntoIds")
        val _columnIndexOfDocumentUrls: Int = getColumnIndexOrThrow(_stmt, "documentUrls")
        val _columnIndexOfQrCodeUrl: Int = getColumnIndexOrThrow(_stmt, "qrCodeUrl")
        val _columnIndexOfCustomStatus: Int = getColumnIndexOrThrow(_stmt, "customStatus")
        val _columnIndexOfDebug: Int = getColumnIndexOrThrow(_stmt, "debug")
        val _columnIndexOfDeliveryOptions: Int = getColumnIndexOrThrow(_stmt, "deliveryOptions")
        val _columnIndexOfDeliveryCost: Int = getColumnIndexOrThrow(_stmt, "deliveryCost")
        val _columnIndexOfLeadTimeDays: Int = getColumnIndexOrThrow(_stmt, "leadTimeDays")
        val _columnIndexOfMotherId: Int = getColumnIndexOrThrow(_stmt, "motherId")
        val _columnIndexOfIsBreedingUnit: Int = getColumnIndexOrThrow(_stmt, "isBreedingUnit")
        val _columnIndexOfEggsCollectedToday: Int = getColumnIndexOrThrow(_stmt,
            "eggsCollectedToday")
        val _columnIndexOfLastEggLogDate: Int = getColumnIndexOrThrow(_stmt, "lastEggLogDate")
        val _columnIndexOfReadyForSale: Int = getColumnIndexOrThrow(_stmt, "readyForSale")
        val _columnIndexOfTargetWeight: Int = getColumnIndexOrThrow(_stmt, "targetWeight")
        val _columnIndexOfIsShowcased: Int = getColumnIndexOrThrow(_stmt, "isShowcased")
        val _columnIndexOfExternalVideoUrl: Int = getColumnIndexOrThrow(_stmt, "externalVideoUrl")
        val _columnIndexOfRecordsLockedAt: Int = getColumnIndexOrThrow(_stmt, "recordsLockedAt")
        val _columnIndexOfAutoLockAfterDays: Int = getColumnIndexOrThrow(_stmt, "autoLockAfterDays")
        val _columnIndexOfLineageHistoryJson: Int = getColumnIndexOrThrow(_stmt,
            "lineageHistoryJson")
        val _columnIndexOfEditCount: Int = getColumnIndexOrThrow(_stmt, "editCount")
        val _columnIndexOfLastEditedBy: Int = getColumnIndexOrThrow(_stmt, "lastEditedBy")
        val _columnIndexOfAdminFlagged: Int = getColumnIndexOrThrow(_stmt, "adminFlagged")
        val _columnIndexOfModerationNote: Int = getColumnIndexOrThrow(_stmt, "moderationNote")
        val _columnIndexOfMetadataJson: Int = getColumnIndexOrThrow(_stmt, "metadataJson")
        val _result: MutableList<ProductEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: ProductEntity
          val _tmpProductId: String
          _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          val _tmpSellerId: String
          _tmpSellerId = _stmt.getText(_columnIndexOfSellerId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpDescription: String
          _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          val _tmpCategory: String
          _tmpCategory = _stmt.getText(_columnIndexOfCategory)
          val _tmpPrice: Double
          _tmpPrice = _stmt.getDouble(_columnIndexOfPrice)
          val _tmpQuantity: Double
          _tmpQuantity = _stmt.getDouble(_columnIndexOfQuantity)
          val _tmpUnit: String
          _tmpUnit = _stmt.getText(_columnIndexOfUnit)
          val _tmpLocation: String
          _tmpLocation = _stmt.getText(_columnIndexOfLocation)
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
          val _tmpImageUrls: List<String>
          val _tmp: String?
          if (_stmt.isNull(_columnIndexOfImageUrls)) {
            _tmp = null
          } else {
            _tmp = _stmt.getText(_columnIndexOfImageUrls)
          }
          val _tmp_1: List<String>? = AppDatabase.Converters.toStringList(_tmp)
          if (_tmp_1 == null) {
            error("Expected NON-NULL 'kotlin.collections.List<kotlin.String>', but it was NULL.")
          } else {
            _tmpImageUrls = _tmp_1
          }
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpCondition: String?
          if (_stmt.isNull(_columnIndexOfCondition)) {
            _tmpCondition = null
          } else {
            _tmpCondition = _stmt.getText(_columnIndexOfCondition)
          }
          val _tmpHarvestDate: Long?
          if (_stmt.isNull(_columnIndexOfHarvestDate)) {
            _tmpHarvestDate = null
          } else {
            _tmpHarvestDate = _stmt.getLong(_columnIndexOfHarvestDate)
          }
          val _tmpExpiryDate: Long?
          if (_stmt.isNull(_columnIndexOfExpiryDate)) {
            _tmpExpiryDate = null
          } else {
            _tmpExpiryDate = _stmt.getLong(_columnIndexOfExpiryDate)
          }
          val _tmpBirthDate: Long?
          if (_stmt.isNull(_columnIndexOfBirthDate)) {
            _tmpBirthDate = null
          } else {
            _tmpBirthDate = _stmt.getLong(_columnIndexOfBirthDate)
          }
          val _tmpVaccinationRecordsJson: String?
          if (_stmt.isNull(_columnIndexOfVaccinationRecordsJson)) {
            _tmpVaccinationRecordsJson = null
          } else {
            _tmpVaccinationRecordsJson = _stmt.getText(_columnIndexOfVaccinationRecordsJson)
          }
          val _tmpWeightGrams: Double?
          if (_stmt.isNull(_columnIndexOfWeightGrams)) {
            _tmpWeightGrams = null
          } else {
            _tmpWeightGrams = _stmt.getDouble(_columnIndexOfWeightGrams)
          }
          val _tmpHeightCm: Double?
          if (_stmt.isNull(_columnIndexOfHeightCm)) {
            _tmpHeightCm = null
          } else {
            _tmpHeightCm = _stmt.getDouble(_columnIndexOfHeightCm)
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
          val _tmpBreed: String?
          if (_stmt.isNull(_columnIndexOfBreed)) {
            _tmpBreed = null
          } else {
            _tmpBreed = _stmt.getText(_columnIndexOfBreed)
          }
          val _tmpRaisingPurpose: String?
          if (_stmt.isNull(_columnIndexOfRaisingPurpose)) {
            _tmpRaisingPurpose = null
          } else {
            _tmpRaisingPurpose = _stmt.getText(_columnIndexOfRaisingPurpose)
          }
          val _tmpHealthStatus: String?
          if (_stmt.isNull(_columnIndexOfHealthStatus)) {
            _tmpHealthStatus = null
          } else {
            _tmpHealthStatus = _stmt.getText(_columnIndexOfHealthStatus)
          }
          val _tmpBirdCode: String?
          if (_stmt.isNull(_columnIndexOfBirdCode)) {
            _tmpBirdCode = null
          } else {
            _tmpBirdCode = _stmt.getText(_columnIndexOfBirdCode)
          }
          val _tmpColorTag: String?
          if (_stmt.isNull(_columnIndexOfColorTag)) {
            _tmpColorTag = null
          } else {
            _tmpColorTag = _stmt.getText(_columnIndexOfColorTag)
          }
          val _tmpFamilyTreeId: String?
          if (_stmt.isNull(_columnIndexOfFamilyTreeId)) {
            _tmpFamilyTreeId = null
          } else {
            _tmpFamilyTreeId = _stmt.getText(_columnIndexOfFamilyTreeId)
          }
          val _tmpSourceAssetId: String?
          if (_stmt.isNull(_columnIndexOfSourceAssetId)) {
            _tmpSourceAssetId = null
          } else {
            _tmpSourceAssetId = _stmt.getText(_columnIndexOfSourceAssetId)
          }
          val _tmpParentIdsJson: String?
          if (_stmt.isNull(_columnIndexOfParentIdsJson)) {
            _tmpParentIdsJson = null
          } else {
            _tmpParentIdsJson = _stmt.getText(_columnIndexOfParentIdsJson)
          }
          val _tmpBreedingStatus: String?
          if (_stmt.isNull(_columnIndexOfBreedingStatus)) {
            _tmpBreedingStatus = null
          } else {
            _tmpBreedingStatus = _stmt.getText(_columnIndexOfBreedingStatus)
          }
          val _tmpTransferHistoryJson: String?
          if (_stmt.isNull(_columnIndexOfTransferHistoryJson)) {
            _tmpTransferHistoryJson = null
          } else {
            _tmpTransferHistoryJson = _stmt.getText(_columnIndexOfTransferHistoryJson)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpLastModifiedAt: Long
          _tmpLastModifiedAt = _stmt.getLong(_columnIndexOfLastModifiedAt)
          val _tmpIsDeleted: Boolean
          val _tmp_2: Int
          _tmp_2 = _stmt.getLong(_columnIndexOfIsDeleted).toInt()
          _tmpIsDeleted = _tmp_2 != 0
          val _tmpDeletedAt: Long?
          if (_stmt.isNull(_columnIndexOfDeletedAt)) {
            _tmpDeletedAt = null
          } else {
            _tmpDeletedAt = _stmt.getLong(_columnIndexOfDeletedAt)
          }
          val _tmpDirty: Boolean
          val _tmp_3: Int
          _tmp_3 = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp_3 != 0
          val _tmpStage: LifecycleStage?
          val _tmp_4: String?
          if (_stmt.isNull(_columnIndexOfStage)) {
            _tmp_4 = null
          } else {
            _tmp_4 = _stmt.getText(_columnIndexOfStage)
          }
          _tmpStage = AppDatabase.Converters.toLifecycleStage(_tmp_4)
          val _tmpLifecycleStatus: String?
          if (_stmt.isNull(_columnIndexOfLifecycleStatus)) {
            _tmpLifecycleStatus = null
          } else {
            _tmpLifecycleStatus = _stmt.getText(_columnIndexOfLifecycleStatus)
          }
          val _tmpParentMaleId: String?
          if (_stmt.isNull(_columnIndexOfParentMaleId)) {
            _tmpParentMaleId = null
          } else {
            _tmpParentMaleId = _stmt.getText(_columnIndexOfParentMaleId)
          }
          val _tmpParentFemaleId: String?
          if (_stmt.isNull(_columnIndexOfParentFemaleId)) {
            _tmpParentFemaleId = null
          } else {
            _tmpParentFemaleId = _stmt.getText(_columnIndexOfParentFemaleId)
          }
          val _tmpAgeWeeks: Int?
          if (_stmt.isNull(_columnIndexOfAgeWeeks)) {
            _tmpAgeWeeks = null
          } else {
            _tmpAgeWeeks = _stmt.getLong(_columnIndexOfAgeWeeks).toInt()
          }
          val _tmpLastStageTransitionAt: Long?
          if (_stmt.isNull(_columnIndexOfLastStageTransitionAt)) {
            _tmpLastStageTransitionAt = null
          } else {
            _tmpLastStageTransitionAt = _stmt.getLong(_columnIndexOfLastStageTransitionAt)
          }
          val _tmpBreederEligibleAt: Long?
          if (_stmt.isNull(_columnIndexOfBreederEligibleAt)) {
            _tmpBreederEligibleAt = null
          } else {
            _tmpBreederEligibleAt = _stmt.getLong(_columnIndexOfBreederEligibleAt)
          }
          val _tmpIsBatch: Boolean?
          val _tmp_5: Int?
          if (_stmt.isNull(_columnIndexOfIsBatch)) {
            _tmp_5 = null
          } else {
            _tmp_5 = _stmt.getLong(_columnIndexOfIsBatch).toInt()
          }
          _tmpIsBatch = _tmp_5?.let { it != 0 }
          val _tmpBatchId: String?
          if (_stmt.isNull(_columnIndexOfBatchId)) {
            _tmpBatchId = null
          } else {
            _tmpBatchId = _stmt.getText(_columnIndexOfBatchId)
          }
          val _tmpSplitAt: Long?
          if (_stmt.isNull(_columnIndexOfSplitAt)) {
            _tmpSplitAt = null
          } else {
            _tmpSplitAt = _stmt.getLong(_columnIndexOfSplitAt)
          }
          val _tmpSplitIntoIds: String?
          if (_stmt.isNull(_columnIndexOfSplitIntoIds)) {
            _tmpSplitIntoIds = null
          } else {
            _tmpSplitIntoIds = _stmt.getText(_columnIndexOfSplitIntoIds)
          }
          val _tmpDocumentUrls: List<String>
          val _tmp_6: String?
          if (_stmt.isNull(_columnIndexOfDocumentUrls)) {
            _tmp_6 = null
          } else {
            _tmp_6 = _stmt.getText(_columnIndexOfDocumentUrls)
          }
          val _tmp_7: List<String>? = AppDatabase.Converters.toStringList(_tmp_6)
          if (_tmp_7 == null) {
            error("Expected NON-NULL 'kotlin.collections.List<kotlin.String>', but it was NULL.")
          } else {
            _tmpDocumentUrls = _tmp_7
          }
          val _tmpQrCodeUrl: String?
          if (_stmt.isNull(_columnIndexOfQrCodeUrl)) {
            _tmpQrCodeUrl = null
          } else {
            _tmpQrCodeUrl = _stmt.getText(_columnIndexOfQrCodeUrl)
          }
          val _tmpCustomStatus: String?
          if (_stmt.isNull(_columnIndexOfCustomStatus)) {
            _tmpCustomStatus = null
          } else {
            _tmpCustomStatus = _stmt.getText(_columnIndexOfCustomStatus)
          }
          val _tmpDebug: Boolean
          val _tmp_8: Int
          _tmp_8 = _stmt.getLong(_columnIndexOfDebug).toInt()
          _tmpDebug = _tmp_8 != 0
          val _tmpDeliveryOptions: List<String>
          val _tmp_9: String?
          if (_stmt.isNull(_columnIndexOfDeliveryOptions)) {
            _tmp_9 = null
          } else {
            _tmp_9 = _stmt.getText(_columnIndexOfDeliveryOptions)
          }
          val _tmp_10: List<String>? = AppDatabase.Converters.toStringList(_tmp_9)
          if (_tmp_10 == null) {
            error("Expected NON-NULL 'kotlin.collections.List<kotlin.String>', but it was NULL.")
          } else {
            _tmpDeliveryOptions = _tmp_10
          }
          val _tmpDeliveryCost: Double?
          if (_stmt.isNull(_columnIndexOfDeliveryCost)) {
            _tmpDeliveryCost = null
          } else {
            _tmpDeliveryCost = _stmt.getDouble(_columnIndexOfDeliveryCost)
          }
          val _tmpLeadTimeDays: Int?
          if (_stmt.isNull(_columnIndexOfLeadTimeDays)) {
            _tmpLeadTimeDays = null
          } else {
            _tmpLeadTimeDays = _stmt.getLong(_columnIndexOfLeadTimeDays).toInt()
          }
          val _tmpMotherId: String?
          if (_stmt.isNull(_columnIndexOfMotherId)) {
            _tmpMotherId = null
          } else {
            _tmpMotherId = _stmt.getText(_columnIndexOfMotherId)
          }
          val _tmpIsBreedingUnit: Boolean
          val _tmp_11: Int
          _tmp_11 = _stmt.getLong(_columnIndexOfIsBreedingUnit).toInt()
          _tmpIsBreedingUnit = _tmp_11 != 0
          val _tmpEggsCollectedToday: Int
          _tmpEggsCollectedToday = _stmt.getLong(_columnIndexOfEggsCollectedToday).toInt()
          val _tmpLastEggLogDate: Long?
          if (_stmt.isNull(_columnIndexOfLastEggLogDate)) {
            _tmpLastEggLogDate = null
          } else {
            _tmpLastEggLogDate = _stmt.getLong(_columnIndexOfLastEggLogDate)
          }
          val _tmpReadyForSale: Boolean
          val _tmp_12: Int
          _tmp_12 = _stmt.getLong(_columnIndexOfReadyForSale).toInt()
          _tmpReadyForSale = _tmp_12 != 0
          val _tmpTargetWeight: Double?
          if (_stmt.isNull(_columnIndexOfTargetWeight)) {
            _tmpTargetWeight = null
          } else {
            _tmpTargetWeight = _stmt.getDouble(_columnIndexOfTargetWeight)
          }
          val _tmpIsShowcased: Boolean
          val _tmp_13: Int
          _tmp_13 = _stmt.getLong(_columnIndexOfIsShowcased).toInt()
          _tmpIsShowcased = _tmp_13 != 0
          val _tmpExternalVideoUrl: String?
          if (_stmt.isNull(_columnIndexOfExternalVideoUrl)) {
            _tmpExternalVideoUrl = null
          } else {
            _tmpExternalVideoUrl = _stmt.getText(_columnIndexOfExternalVideoUrl)
          }
          val _tmpRecordsLockedAt: Long?
          if (_stmt.isNull(_columnIndexOfRecordsLockedAt)) {
            _tmpRecordsLockedAt = null
          } else {
            _tmpRecordsLockedAt = _stmt.getLong(_columnIndexOfRecordsLockedAt)
          }
          val _tmpAutoLockAfterDays: Int
          _tmpAutoLockAfterDays = _stmt.getLong(_columnIndexOfAutoLockAfterDays).toInt()
          val _tmpLineageHistoryJson: String?
          if (_stmt.isNull(_columnIndexOfLineageHistoryJson)) {
            _tmpLineageHistoryJson = null
          } else {
            _tmpLineageHistoryJson = _stmt.getText(_columnIndexOfLineageHistoryJson)
          }
          val _tmpEditCount: Int
          _tmpEditCount = _stmt.getLong(_columnIndexOfEditCount).toInt()
          val _tmpLastEditedBy: String?
          if (_stmt.isNull(_columnIndexOfLastEditedBy)) {
            _tmpLastEditedBy = null
          } else {
            _tmpLastEditedBy = _stmt.getText(_columnIndexOfLastEditedBy)
          }
          val _tmpAdminFlagged: Boolean
          val _tmp_14: Int
          _tmp_14 = _stmt.getLong(_columnIndexOfAdminFlagged).toInt()
          _tmpAdminFlagged = _tmp_14 != 0
          val _tmpModerationNote: String?
          if (_stmt.isNull(_columnIndexOfModerationNote)) {
            _tmpModerationNote = null
          } else {
            _tmpModerationNote = _stmt.getText(_columnIndexOfModerationNote)
          }
          val _tmpMetadataJson: String
          _tmpMetadataJson = _stmt.getText(_columnIndexOfMetadataJson)
          _item =
              ProductEntity(_tmpProductId,_tmpSellerId,_tmpName,_tmpDescription,_tmpCategory,_tmpPrice,_tmpQuantity,_tmpUnit,_tmpLocation,_tmpLatitude,_tmpLongitude,_tmpImageUrls,_tmpStatus,_tmpCondition,_tmpHarvestDate,_tmpExpiryDate,_tmpBirthDate,_tmpVaccinationRecordsJson,_tmpWeightGrams,_tmpHeightCm,_tmpGender,_tmpColor,_tmpBreed,_tmpRaisingPurpose,_tmpHealthStatus,_tmpBirdCode,_tmpColorTag,_tmpFamilyTreeId,_tmpSourceAssetId,_tmpParentIdsJson,_tmpBreedingStatus,_tmpTransferHistoryJson,_tmpCreatedAt,_tmpUpdatedAt,_tmpLastModifiedAt,_tmpIsDeleted,_tmpDeletedAt,_tmpDirty,_tmpStage,_tmpLifecycleStatus,_tmpParentMaleId,_tmpParentFemaleId,_tmpAgeWeeks,_tmpLastStageTransitionAt,_tmpBreederEligibleAt,_tmpIsBatch,_tmpBatchId,_tmpSplitAt,_tmpSplitIntoIds,_tmpDocumentUrls,_tmpQrCodeUrl,_tmpCustomStatus,_tmpDebug,_tmpDeliveryOptions,_tmpDeliveryCost,_tmpLeadTimeDays,_tmpMotherId,_tmpIsBreedingUnit,_tmpEggsCollectedToday,_tmpLastEggLogDate,_tmpReadyForSale,_tmpTargetWeight,_tmpIsShowcased,_tmpExternalVideoUrl,_tmpRecordsLockedAt,_tmpAutoLockAfterDays,_tmpLineageHistoryJson,_tmpEditCount,_tmpLastEditedBy,_tmpAdminFlagged,_tmpModerationNote,_tmpMetadataJson)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun filterByBoundingBox(
    minLat: Double?,
    maxLat: Double?,
    minLng: Double?,
    maxLng: Double?,
    limit: Int,
    offset: Int,
  ): List<ProductEntity> {
    val _sql: String =
        "SELECT * FROM products WHERE (? IS NULL OR latitude >= ?) AND (? IS NULL OR latitude <= ?) AND (? IS NULL OR longitude >= ?) AND (? IS NULL OR longitude <= ?) AND isDeleted = 0 ORDER BY updatedAt DESC LIMIT ? OFFSET ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        if (minLat == null) {
          _stmt.bindNull(_argIndex)
        } else {
          _stmt.bindDouble(_argIndex, minLat)
        }
        _argIndex = 2
        if (minLat == null) {
          _stmt.bindNull(_argIndex)
        } else {
          _stmt.bindDouble(_argIndex, minLat)
        }
        _argIndex = 3
        if (maxLat == null) {
          _stmt.bindNull(_argIndex)
        } else {
          _stmt.bindDouble(_argIndex, maxLat)
        }
        _argIndex = 4
        if (maxLat == null) {
          _stmt.bindNull(_argIndex)
        } else {
          _stmt.bindDouble(_argIndex, maxLat)
        }
        _argIndex = 5
        if (minLng == null) {
          _stmt.bindNull(_argIndex)
        } else {
          _stmt.bindDouble(_argIndex, minLng)
        }
        _argIndex = 6
        if (minLng == null) {
          _stmt.bindNull(_argIndex)
        } else {
          _stmt.bindDouble(_argIndex, minLng)
        }
        _argIndex = 7
        if (maxLng == null) {
          _stmt.bindNull(_argIndex)
        } else {
          _stmt.bindDouble(_argIndex, maxLng)
        }
        _argIndex = 8
        if (maxLng == null) {
          _stmt.bindNull(_argIndex)
        } else {
          _stmt.bindDouble(_argIndex, maxLng)
        }
        _argIndex = 9
        _stmt.bindLong(_argIndex, limit.toLong())
        _argIndex = 10
        _stmt.bindLong(_argIndex, offset.toLong())
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfSellerId: Int = getColumnIndexOrThrow(_stmt, "sellerId")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfCategory: Int = getColumnIndexOrThrow(_stmt, "category")
        val _columnIndexOfPrice: Int = getColumnIndexOrThrow(_stmt, "price")
        val _columnIndexOfQuantity: Int = getColumnIndexOrThrow(_stmt, "quantity")
        val _columnIndexOfUnit: Int = getColumnIndexOrThrow(_stmt, "unit")
        val _columnIndexOfLocation: Int = getColumnIndexOrThrow(_stmt, "location")
        val _columnIndexOfLatitude: Int = getColumnIndexOrThrow(_stmt, "latitude")
        val _columnIndexOfLongitude: Int = getColumnIndexOrThrow(_stmt, "longitude")
        val _columnIndexOfImageUrls: Int = getColumnIndexOrThrow(_stmt, "imageUrls")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfCondition: Int = getColumnIndexOrThrow(_stmt, "condition")
        val _columnIndexOfHarvestDate: Int = getColumnIndexOrThrow(_stmt, "harvestDate")
        val _columnIndexOfExpiryDate: Int = getColumnIndexOrThrow(_stmt, "expiryDate")
        val _columnIndexOfBirthDate: Int = getColumnIndexOrThrow(_stmt, "birthDate")
        val _columnIndexOfVaccinationRecordsJson: Int = getColumnIndexOrThrow(_stmt,
            "vaccinationRecordsJson")
        val _columnIndexOfWeightGrams: Int = getColumnIndexOrThrow(_stmt, "weightGrams")
        val _columnIndexOfHeightCm: Int = getColumnIndexOrThrow(_stmt, "heightCm")
        val _columnIndexOfGender: Int = getColumnIndexOrThrow(_stmt, "gender")
        val _columnIndexOfColor: Int = getColumnIndexOrThrow(_stmt, "color")
        val _columnIndexOfBreed: Int = getColumnIndexOrThrow(_stmt, "breed")
        val _columnIndexOfRaisingPurpose: Int = getColumnIndexOrThrow(_stmt, "raisingPurpose")
        val _columnIndexOfHealthStatus: Int = getColumnIndexOrThrow(_stmt, "healthStatus")
        val _columnIndexOfBirdCode: Int = getColumnIndexOrThrow(_stmt, "birdCode")
        val _columnIndexOfColorTag: Int = getColumnIndexOrThrow(_stmt, "colorTag")
        val _columnIndexOfFamilyTreeId: Int = getColumnIndexOrThrow(_stmt, "familyTreeId")
        val _columnIndexOfSourceAssetId: Int = getColumnIndexOrThrow(_stmt, "sourceAssetId")
        val _columnIndexOfParentIdsJson: Int = getColumnIndexOrThrow(_stmt, "parentIdsJson")
        val _columnIndexOfBreedingStatus: Int = getColumnIndexOrThrow(_stmt, "breedingStatus")
        val _columnIndexOfTransferHistoryJson: Int = getColumnIndexOrThrow(_stmt,
            "transferHistoryJson")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfLastModifiedAt: Int = getColumnIndexOrThrow(_stmt, "lastModifiedAt")
        val _columnIndexOfIsDeleted: Int = getColumnIndexOrThrow(_stmt, "isDeleted")
        val _columnIndexOfDeletedAt: Int = getColumnIndexOrThrow(_stmt, "deletedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfStage: Int = getColumnIndexOrThrow(_stmt, "stage")
        val _columnIndexOfLifecycleStatus: Int = getColumnIndexOrThrow(_stmt, "lifecycleStatus")
        val _columnIndexOfParentMaleId: Int = getColumnIndexOrThrow(_stmt, "parentMaleId")
        val _columnIndexOfParentFemaleId: Int = getColumnIndexOrThrow(_stmt, "parentFemaleId")
        val _columnIndexOfAgeWeeks: Int = getColumnIndexOrThrow(_stmt, "ageWeeks")
        val _columnIndexOfLastStageTransitionAt: Int = getColumnIndexOrThrow(_stmt,
            "lastStageTransitionAt")
        val _columnIndexOfBreederEligibleAt: Int = getColumnIndexOrThrow(_stmt, "breederEligibleAt")
        val _columnIndexOfIsBatch: Int = getColumnIndexOrThrow(_stmt, "isBatch")
        val _columnIndexOfBatchId: Int = getColumnIndexOrThrow(_stmt, "batchId")
        val _columnIndexOfSplitAt: Int = getColumnIndexOrThrow(_stmt, "splitAt")
        val _columnIndexOfSplitIntoIds: Int = getColumnIndexOrThrow(_stmt, "splitIntoIds")
        val _columnIndexOfDocumentUrls: Int = getColumnIndexOrThrow(_stmt, "documentUrls")
        val _columnIndexOfQrCodeUrl: Int = getColumnIndexOrThrow(_stmt, "qrCodeUrl")
        val _columnIndexOfCustomStatus: Int = getColumnIndexOrThrow(_stmt, "customStatus")
        val _columnIndexOfDebug: Int = getColumnIndexOrThrow(_stmt, "debug")
        val _columnIndexOfDeliveryOptions: Int = getColumnIndexOrThrow(_stmt, "deliveryOptions")
        val _columnIndexOfDeliveryCost: Int = getColumnIndexOrThrow(_stmt, "deliveryCost")
        val _columnIndexOfLeadTimeDays: Int = getColumnIndexOrThrow(_stmt, "leadTimeDays")
        val _columnIndexOfMotherId: Int = getColumnIndexOrThrow(_stmt, "motherId")
        val _columnIndexOfIsBreedingUnit: Int = getColumnIndexOrThrow(_stmt, "isBreedingUnit")
        val _columnIndexOfEggsCollectedToday: Int = getColumnIndexOrThrow(_stmt,
            "eggsCollectedToday")
        val _columnIndexOfLastEggLogDate: Int = getColumnIndexOrThrow(_stmt, "lastEggLogDate")
        val _columnIndexOfReadyForSale: Int = getColumnIndexOrThrow(_stmt, "readyForSale")
        val _columnIndexOfTargetWeight: Int = getColumnIndexOrThrow(_stmt, "targetWeight")
        val _columnIndexOfIsShowcased: Int = getColumnIndexOrThrow(_stmt, "isShowcased")
        val _columnIndexOfExternalVideoUrl: Int = getColumnIndexOrThrow(_stmt, "externalVideoUrl")
        val _columnIndexOfRecordsLockedAt: Int = getColumnIndexOrThrow(_stmt, "recordsLockedAt")
        val _columnIndexOfAutoLockAfterDays: Int = getColumnIndexOrThrow(_stmt, "autoLockAfterDays")
        val _columnIndexOfLineageHistoryJson: Int = getColumnIndexOrThrow(_stmt,
            "lineageHistoryJson")
        val _columnIndexOfEditCount: Int = getColumnIndexOrThrow(_stmt, "editCount")
        val _columnIndexOfLastEditedBy: Int = getColumnIndexOrThrow(_stmt, "lastEditedBy")
        val _columnIndexOfAdminFlagged: Int = getColumnIndexOrThrow(_stmt, "adminFlagged")
        val _columnIndexOfModerationNote: Int = getColumnIndexOrThrow(_stmt, "moderationNote")
        val _columnIndexOfMetadataJson: Int = getColumnIndexOrThrow(_stmt, "metadataJson")
        val _result: MutableList<ProductEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: ProductEntity
          val _tmpProductId: String
          _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          val _tmpSellerId: String
          _tmpSellerId = _stmt.getText(_columnIndexOfSellerId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpDescription: String
          _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          val _tmpCategory: String
          _tmpCategory = _stmt.getText(_columnIndexOfCategory)
          val _tmpPrice: Double
          _tmpPrice = _stmt.getDouble(_columnIndexOfPrice)
          val _tmpQuantity: Double
          _tmpQuantity = _stmt.getDouble(_columnIndexOfQuantity)
          val _tmpUnit: String
          _tmpUnit = _stmt.getText(_columnIndexOfUnit)
          val _tmpLocation: String
          _tmpLocation = _stmt.getText(_columnIndexOfLocation)
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
          val _tmpImageUrls: List<String>
          val _tmp: String?
          if (_stmt.isNull(_columnIndexOfImageUrls)) {
            _tmp = null
          } else {
            _tmp = _stmt.getText(_columnIndexOfImageUrls)
          }
          val _tmp_1: List<String>? = AppDatabase.Converters.toStringList(_tmp)
          if (_tmp_1 == null) {
            error("Expected NON-NULL 'kotlin.collections.List<kotlin.String>', but it was NULL.")
          } else {
            _tmpImageUrls = _tmp_1
          }
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpCondition: String?
          if (_stmt.isNull(_columnIndexOfCondition)) {
            _tmpCondition = null
          } else {
            _tmpCondition = _stmt.getText(_columnIndexOfCondition)
          }
          val _tmpHarvestDate: Long?
          if (_stmt.isNull(_columnIndexOfHarvestDate)) {
            _tmpHarvestDate = null
          } else {
            _tmpHarvestDate = _stmt.getLong(_columnIndexOfHarvestDate)
          }
          val _tmpExpiryDate: Long?
          if (_stmt.isNull(_columnIndexOfExpiryDate)) {
            _tmpExpiryDate = null
          } else {
            _tmpExpiryDate = _stmt.getLong(_columnIndexOfExpiryDate)
          }
          val _tmpBirthDate: Long?
          if (_stmt.isNull(_columnIndexOfBirthDate)) {
            _tmpBirthDate = null
          } else {
            _tmpBirthDate = _stmt.getLong(_columnIndexOfBirthDate)
          }
          val _tmpVaccinationRecordsJson: String?
          if (_stmt.isNull(_columnIndexOfVaccinationRecordsJson)) {
            _tmpVaccinationRecordsJson = null
          } else {
            _tmpVaccinationRecordsJson = _stmt.getText(_columnIndexOfVaccinationRecordsJson)
          }
          val _tmpWeightGrams: Double?
          if (_stmt.isNull(_columnIndexOfWeightGrams)) {
            _tmpWeightGrams = null
          } else {
            _tmpWeightGrams = _stmt.getDouble(_columnIndexOfWeightGrams)
          }
          val _tmpHeightCm: Double?
          if (_stmt.isNull(_columnIndexOfHeightCm)) {
            _tmpHeightCm = null
          } else {
            _tmpHeightCm = _stmt.getDouble(_columnIndexOfHeightCm)
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
          val _tmpBreed: String?
          if (_stmt.isNull(_columnIndexOfBreed)) {
            _tmpBreed = null
          } else {
            _tmpBreed = _stmt.getText(_columnIndexOfBreed)
          }
          val _tmpRaisingPurpose: String?
          if (_stmt.isNull(_columnIndexOfRaisingPurpose)) {
            _tmpRaisingPurpose = null
          } else {
            _tmpRaisingPurpose = _stmt.getText(_columnIndexOfRaisingPurpose)
          }
          val _tmpHealthStatus: String?
          if (_stmt.isNull(_columnIndexOfHealthStatus)) {
            _tmpHealthStatus = null
          } else {
            _tmpHealthStatus = _stmt.getText(_columnIndexOfHealthStatus)
          }
          val _tmpBirdCode: String?
          if (_stmt.isNull(_columnIndexOfBirdCode)) {
            _tmpBirdCode = null
          } else {
            _tmpBirdCode = _stmt.getText(_columnIndexOfBirdCode)
          }
          val _tmpColorTag: String?
          if (_stmt.isNull(_columnIndexOfColorTag)) {
            _tmpColorTag = null
          } else {
            _tmpColorTag = _stmt.getText(_columnIndexOfColorTag)
          }
          val _tmpFamilyTreeId: String?
          if (_stmt.isNull(_columnIndexOfFamilyTreeId)) {
            _tmpFamilyTreeId = null
          } else {
            _tmpFamilyTreeId = _stmt.getText(_columnIndexOfFamilyTreeId)
          }
          val _tmpSourceAssetId: String?
          if (_stmt.isNull(_columnIndexOfSourceAssetId)) {
            _tmpSourceAssetId = null
          } else {
            _tmpSourceAssetId = _stmt.getText(_columnIndexOfSourceAssetId)
          }
          val _tmpParentIdsJson: String?
          if (_stmt.isNull(_columnIndexOfParentIdsJson)) {
            _tmpParentIdsJson = null
          } else {
            _tmpParentIdsJson = _stmt.getText(_columnIndexOfParentIdsJson)
          }
          val _tmpBreedingStatus: String?
          if (_stmt.isNull(_columnIndexOfBreedingStatus)) {
            _tmpBreedingStatus = null
          } else {
            _tmpBreedingStatus = _stmt.getText(_columnIndexOfBreedingStatus)
          }
          val _tmpTransferHistoryJson: String?
          if (_stmt.isNull(_columnIndexOfTransferHistoryJson)) {
            _tmpTransferHistoryJson = null
          } else {
            _tmpTransferHistoryJson = _stmt.getText(_columnIndexOfTransferHistoryJson)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpLastModifiedAt: Long
          _tmpLastModifiedAt = _stmt.getLong(_columnIndexOfLastModifiedAt)
          val _tmpIsDeleted: Boolean
          val _tmp_2: Int
          _tmp_2 = _stmt.getLong(_columnIndexOfIsDeleted).toInt()
          _tmpIsDeleted = _tmp_2 != 0
          val _tmpDeletedAt: Long?
          if (_stmt.isNull(_columnIndexOfDeletedAt)) {
            _tmpDeletedAt = null
          } else {
            _tmpDeletedAt = _stmt.getLong(_columnIndexOfDeletedAt)
          }
          val _tmpDirty: Boolean
          val _tmp_3: Int
          _tmp_3 = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp_3 != 0
          val _tmpStage: LifecycleStage?
          val _tmp_4: String?
          if (_stmt.isNull(_columnIndexOfStage)) {
            _tmp_4 = null
          } else {
            _tmp_4 = _stmt.getText(_columnIndexOfStage)
          }
          _tmpStage = AppDatabase.Converters.toLifecycleStage(_tmp_4)
          val _tmpLifecycleStatus: String?
          if (_stmt.isNull(_columnIndexOfLifecycleStatus)) {
            _tmpLifecycleStatus = null
          } else {
            _tmpLifecycleStatus = _stmt.getText(_columnIndexOfLifecycleStatus)
          }
          val _tmpParentMaleId: String?
          if (_stmt.isNull(_columnIndexOfParentMaleId)) {
            _tmpParentMaleId = null
          } else {
            _tmpParentMaleId = _stmt.getText(_columnIndexOfParentMaleId)
          }
          val _tmpParentFemaleId: String?
          if (_stmt.isNull(_columnIndexOfParentFemaleId)) {
            _tmpParentFemaleId = null
          } else {
            _tmpParentFemaleId = _stmt.getText(_columnIndexOfParentFemaleId)
          }
          val _tmpAgeWeeks: Int?
          if (_stmt.isNull(_columnIndexOfAgeWeeks)) {
            _tmpAgeWeeks = null
          } else {
            _tmpAgeWeeks = _stmt.getLong(_columnIndexOfAgeWeeks).toInt()
          }
          val _tmpLastStageTransitionAt: Long?
          if (_stmt.isNull(_columnIndexOfLastStageTransitionAt)) {
            _tmpLastStageTransitionAt = null
          } else {
            _tmpLastStageTransitionAt = _stmt.getLong(_columnIndexOfLastStageTransitionAt)
          }
          val _tmpBreederEligibleAt: Long?
          if (_stmt.isNull(_columnIndexOfBreederEligibleAt)) {
            _tmpBreederEligibleAt = null
          } else {
            _tmpBreederEligibleAt = _stmt.getLong(_columnIndexOfBreederEligibleAt)
          }
          val _tmpIsBatch: Boolean?
          val _tmp_5: Int?
          if (_stmt.isNull(_columnIndexOfIsBatch)) {
            _tmp_5 = null
          } else {
            _tmp_5 = _stmt.getLong(_columnIndexOfIsBatch).toInt()
          }
          _tmpIsBatch = _tmp_5?.let { it != 0 }
          val _tmpBatchId: String?
          if (_stmt.isNull(_columnIndexOfBatchId)) {
            _tmpBatchId = null
          } else {
            _tmpBatchId = _stmt.getText(_columnIndexOfBatchId)
          }
          val _tmpSplitAt: Long?
          if (_stmt.isNull(_columnIndexOfSplitAt)) {
            _tmpSplitAt = null
          } else {
            _tmpSplitAt = _stmt.getLong(_columnIndexOfSplitAt)
          }
          val _tmpSplitIntoIds: String?
          if (_stmt.isNull(_columnIndexOfSplitIntoIds)) {
            _tmpSplitIntoIds = null
          } else {
            _tmpSplitIntoIds = _stmt.getText(_columnIndexOfSplitIntoIds)
          }
          val _tmpDocumentUrls: List<String>
          val _tmp_6: String?
          if (_stmt.isNull(_columnIndexOfDocumentUrls)) {
            _tmp_6 = null
          } else {
            _tmp_6 = _stmt.getText(_columnIndexOfDocumentUrls)
          }
          val _tmp_7: List<String>? = AppDatabase.Converters.toStringList(_tmp_6)
          if (_tmp_7 == null) {
            error("Expected NON-NULL 'kotlin.collections.List<kotlin.String>', but it was NULL.")
          } else {
            _tmpDocumentUrls = _tmp_7
          }
          val _tmpQrCodeUrl: String?
          if (_stmt.isNull(_columnIndexOfQrCodeUrl)) {
            _tmpQrCodeUrl = null
          } else {
            _tmpQrCodeUrl = _stmt.getText(_columnIndexOfQrCodeUrl)
          }
          val _tmpCustomStatus: String?
          if (_stmt.isNull(_columnIndexOfCustomStatus)) {
            _tmpCustomStatus = null
          } else {
            _tmpCustomStatus = _stmt.getText(_columnIndexOfCustomStatus)
          }
          val _tmpDebug: Boolean
          val _tmp_8: Int
          _tmp_8 = _stmt.getLong(_columnIndexOfDebug).toInt()
          _tmpDebug = _tmp_8 != 0
          val _tmpDeliveryOptions: List<String>
          val _tmp_9: String?
          if (_stmt.isNull(_columnIndexOfDeliveryOptions)) {
            _tmp_9 = null
          } else {
            _tmp_9 = _stmt.getText(_columnIndexOfDeliveryOptions)
          }
          val _tmp_10: List<String>? = AppDatabase.Converters.toStringList(_tmp_9)
          if (_tmp_10 == null) {
            error("Expected NON-NULL 'kotlin.collections.List<kotlin.String>', but it was NULL.")
          } else {
            _tmpDeliveryOptions = _tmp_10
          }
          val _tmpDeliveryCost: Double?
          if (_stmt.isNull(_columnIndexOfDeliveryCost)) {
            _tmpDeliveryCost = null
          } else {
            _tmpDeliveryCost = _stmt.getDouble(_columnIndexOfDeliveryCost)
          }
          val _tmpLeadTimeDays: Int?
          if (_stmt.isNull(_columnIndexOfLeadTimeDays)) {
            _tmpLeadTimeDays = null
          } else {
            _tmpLeadTimeDays = _stmt.getLong(_columnIndexOfLeadTimeDays).toInt()
          }
          val _tmpMotherId: String?
          if (_stmt.isNull(_columnIndexOfMotherId)) {
            _tmpMotherId = null
          } else {
            _tmpMotherId = _stmt.getText(_columnIndexOfMotherId)
          }
          val _tmpIsBreedingUnit: Boolean
          val _tmp_11: Int
          _tmp_11 = _stmt.getLong(_columnIndexOfIsBreedingUnit).toInt()
          _tmpIsBreedingUnit = _tmp_11 != 0
          val _tmpEggsCollectedToday: Int
          _tmpEggsCollectedToday = _stmt.getLong(_columnIndexOfEggsCollectedToday).toInt()
          val _tmpLastEggLogDate: Long?
          if (_stmt.isNull(_columnIndexOfLastEggLogDate)) {
            _tmpLastEggLogDate = null
          } else {
            _tmpLastEggLogDate = _stmt.getLong(_columnIndexOfLastEggLogDate)
          }
          val _tmpReadyForSale: Boolean
          val _tmp_12: Int
          _tmp_12 = _stmt.getLong(_columnIndexOfReadyForSale).toInt()
          _tmpReadyForSale = _tmp_12 != 0
          val _tmpTargetWeight: Double?
          if (_stmt.isNull(_columnIndexOfTargetWeight)) {
            _tmpTargetWeight = null
          } else {
            _tmpTargetWeight = _stmt.getDouble(_columnIndexOfTargetWeight)
          }
          val _tmpIsShowcased: Boolean
          val _tmp_13: Int
          _tmp_13 = _stmt.getLong(_columnIndexOfIsShowcased).toInt()
          _tmpIsShowcased = _tmp_13 != 0
          val _tmpExternalVideoUrl: String?
          if (_stmt.isNull(_columnIndexOfExternalVideoUrl)) {
            _tmpExternalVideoUrl = null
          } else {
            _tmpExternalVideoUrl = _stmt.getText(_columnIndexOfExternalVideoUrl)
          }
          val _tmpRecordsLockedAt: Long?
          if (_stmt.isNull(_columnIndexOfRecordsLockedAt)) {
            _tmpRecordsLockedAt = null
          } else {
            _tmpRecordsLockedAt = _stmt.getLong(_columnIndexOfRecordsLockedAt)
          }
          val _tmpAutoLockAfterDays: Int
          _tmpAutoLockAfterDays = _stmt.getLong(_columnIndexOfAutoLockAfterDays).toInt()
          val _tmpLineageHistoryJson: String?
          if (_stmt.isNull(_columnIndexOfLineageHistoryJson)) {
            _tmpLineageHistoryJson = null
          } else {
            _tmpLineageHistoryJson = _stmt.getText(_columnIndexOfLineageHistoryJson)
          }
          val _tmpEditCount: Int
          _tmpEditCount = _stmt.getLong(_columnIndexOfEditCount).toInt()
          val _tmpLastEditedBy: String?
          if (_stmt.isNull(_columnIndexOfLastEditedBy)) {
            _tmpLastEditedBy = null
          } else {
            _tmpLastEditedBy = _stmt.getText(_columnIndexOfLastEditedBy)
          }
          val _tmpAdminFlagged: Boolean
          val _tmp_14: Int
          _tmp_14 = _stmt.getLong(_columnIndexOfAdminFlagged).toInt()
          _tmpAdminFlagged = _tmp_14 != 0
          val _tmpModerationNote: String?
          if (_stmt.isNull(_columnIndexOfModerationNote)) {
            _tmpModerationNote = null
          } else {
            _tmpModerationNote = _stmt.getText(_columnIndexOfModerationNote)
          }
          val _tmpMetadataJson: String
          _tmpMetadataJson = _stmt.getText(_columnIndexOfMetadataJson)
          _item =
              ProductEntity(_tmpProductId,_tmpSellerId,_tmpName,_tmpDescription,_tmpCategory,_tmpPrice,_tmpQuantity,_tmpUnit,_tmpLocation,_tmpLatitude,_tmpLongitude,_tmpImageUrls,_tmpStatus,_tmpCondition,_tmpHarvestDate,_tmpExpiryDate,_tmpBirthDate,_tmpVaccinationRecordsJson,_tmpWeightGrams,_tmpHeightCm,_tmpGender,_tmpColor,_tmpBreed,_tmpRaisingPurpose,_tmpHealthStatus,_tmpBirdCode,_tmpColorTag,_tmpFamilyTreeId,_tmpSourceAssetId,_tmpParentIdsJson,_tmpBreedingStatus,_tmpTransferHistoryJson,_tmpCreatedAt,_tmpUpdatedAt,_tmpLastModifiedAt,_tmpIsDeleted,_tmpDeletedAt,_tmpDirty,_tmpStage,_tmpLifecycleStatus,_tmpParentMaleId,_tmpParentFemaleId,_tmpAgeWeeks,_tmpLastStageTransitionAt,_tmpBreederEligibleAt,_tmpIsBatch,_tmpBatchId,_tmpSplitAt,_tmpSplitIntoIds,_tmpDocumentUrls,_tmpQrCodeUrl,_tmpCustomStatus,_tmpDebug,_tmpDeliveryOptions,_tmpDeliveryCost,_tmpLeadTimeDays,_tmpMotherId,_tmpIsBreedingUnit,_tmpEggsCollectedToday,_tmpLastEggLogDate,_tmpReadyForSale,_tmpTargetWeight,_tmpIsShowcased,_tmpExternalVideoUrl,_tmpRecordsLockedAt,_tmpAutoLockAfterDays,_tmpLineageHistoryJson,_tmpEditCount,_tmpLastEditedBy,_tmpAdminFlagged,_tmpModerationNote,_tmpMetadataJson)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun filterVerified(limit: Int, offset: Int): List<ProductEntity> {
    val _sql: String =
        "SELECT p.* FROM products p INNER JOIN users u ON p.sellerId = u.userId WHERE u.verificationStatus = 'VERIFIED' AND p.isDeleted = 0 ORDER BY p.updatedAt DESC LIMIT ? OFFSET ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, limit.toLong())
        _argIndex = 2
        _stmt.bindLong(_argIndex, offset.toLong())
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfSellerId: Int = getColumnIndexOrThrow(_stmt, "sellerId")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfCategory: Int = getColumnIndexOrThrow(_stmt, "category")
        val _columnIndexOfPrice: Int = getColumnIndexOrThrow(_stmt, "price")
        val _columnIndexOfQuantity: Int = getColumnIndexOrThrow(_stmt, "quantity")
        val _columnIndexOfUnit: Int = getColumnIndexOrThrow(_stmt, "unit")
        val _columnIndexOfLocation: Int = getColumnIndexOrThrow(_stmt, "location")
        val _columnIndexOfLatitude: Int = getColumnIndexOrThrow(_stmt, "latitude")
        val _columnIndexOfLongitude: Int = getColumnIndexOrThrow(_stmt, "longitude")
        val _columnIndexOfImageUrls: Int = getColumnIndexOrThrow(_stmt, "imageUrls")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfCondition: Int = getColumnIndexOrThrow(_stmt, "condition")
        val _columnIndexOfHarvestDate: Int = getColumnIndexOrThrow(_stmt, "harvestDate")
        val _columnIndexOfExpiryDate: Int = getColumnIndexOrThrow(_stmt, "expiryDate")
        val _columnIndexOfBirthDate: Int = getColumnIndexOrThrow(_stmt, "birthDate")
        val _columnIndexOfVaccinationRecordsJson: Int = getColumnIndexOrThrow(_stmt,
            "vaccinationRecordsJson")
        val _columnIndexOfWeightGrams: Int = getColumnIndexOrThrow(_stmt, "weightGrams")
        val _columnIndexOfHeightCm: Int = getColumnIndexOrThrow(_stmt, "heightCm")
        val _columnIndexOfGender: Int = getColumnIndexOrThrow(_stmt, "gender")
        val _columnIndexOfColor: Int = getColumnIndexOrThrow(_stmt, "color")
        val _columnIndexOfBreed: Int = getColumnIndexOrThrow(_stmt, "breed")
        val _columnIndexOfRaisingPurpose: Int = getColumnIndexOrThrow(_stmt, "raisingPurpose")
        val _columnIndexOfHealthStatus: Int = getColumnIndexOrThrow(_stmt, "healthStatus")
        val _columnIndexOfBirdCode: Int = getColumnIndexOrThrow(_stmt, "birdCode")
        val _columnIndexOfColorTag: Int = getColumnIndexOrThrow(_stmt, "colorTag")
        val _columnIndexOfFamilyTreeId: Int = getColumnIndexOrThrow(_stmt, "familyTreeId")
        val _columnIndexOfSourceAssetId: Int = getColumnIndexOrThrow(_stmt, "sourceAssetId")
        val _columnIndexOfParentIdsJson: Int = getColumnIndexOrThrow(_stmt, "parentIdsJson")
        val _columnIndexOfBreedingStatus: Int = getColumnIndexOrThrow(_stmt, "breedingStatus")
        val _columnIndexOfTransferHistoryJson: Int = getColumnIndexOrThrow(_stmt,
            "transferHistoryJson")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfLastModifiedAt: Int = getColumnIndexOrThrow(_stmt, "lastModifiedAt")
        val _columnIndexOfIsDeleted: Int = getColumnIndexOrThrow(_stmt, "isDeleted")
        val _columnIndexOfDeletedAt: Int = getColumnIndexOrThrow(_stmt, "deletedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfStage: Int = getColumnIndexOrThrow(_stmt, "stage")
        val _columnIndexOfLifecycleStatus: Int = getColumnIndexOrThrow(_stmt, "lifecycleStatus")
        val _columnIndexOfParentMaleId: Int = getColumnIndexOrThrow(_stmt, "parentMaleId")
        val _columnIndexOfParentFemaleId: Int = getColumnIndexOrThrow(_stmt, "parentFemaleId")
        val _columnIndexOfAgeWeeks: Int = getColumnIndexOrThrow(_stmt, "ageWeeks")
        val _columnIndexOfLastStageTransitionAt: Int = getColumnIndexOrThrow(_stmt,
            "lastStageTransitionAt")
        val _columnIndexOfBreederEligibleAt: Int = getColumnIndexOrThrow(_stmt, "breederEligibleAt")
        val _columnIndexOfIsBatch: Int = getColumnIndexOrThrow(_stmt, "isBatch")
        val _columnIndexOfBatchId: Int = getColumnIndexOrThrow(_stmt, "batchId")
        val _columnIndexOfSplitAt: Int = getColumnIndexOrThrow(_stmt, "splitAt")
        val _columnIndexOfSplitIntoIds: Int = getColumnIndexOrThrow(_stmt, "splitIntoIds")
        val _columnIndexOfDocumentUrls: Int = getColumnIndexOrThrow(_stmt, "documentUrls")
        val _columnIndexOfQrCodeUrl: Int = getColumnIndexOrThrow(_stmt, "qrCodeUrl")
        val _columnIndexOfCustomStatus: Int = getColumnIndexOrThrow(_stmt, "customStatus")
        val _columnIndexOfDebug: Int = getColumnIndexOrThrow(_stmt, "debug")
        val _columnIndexOfDeliveryOptions: Int = getColumnIndexOrThrow(_stmt, "deliveryOptions")
        val _columnIndexOfDeliveryCost: Int = getColumnIndexOrThrow(_stmt, "deliveryCost")
        val _columnIndexOfLeadTimeDays: Int = getColumnIndexOrThrow(_stmt, "leadTimeDays")
        val _columnIndexOfMotherId: Int = getColumnIndexOrThrow(_stmt, "motherId")
        val _columnIndexOfIsBreedingUnit: Int = getColumnIndexOrThrow(_stmt, "isBreedingUnit")
        val _columnIndexOfEggsCollectedToday: Int = getColumnIndexOrThrow(_stmt,
            "eggsCollectedToday")
        val _columnIndexOfLastEggLogDate: Int = getColumnIndexOrThrow(_stmt, "lastEggLogDate")
        val _columnIndexOfReadyForSale: Int = getColumnIndexOrThrow(_stmt, "readyForSale")
        val _columnIndexOfTargetWeight: Int = getColumnIndexOrThrow(_stmt, "targetWeight")
        val _columnIndexOfIsShowcased: Int = getColumnIndexOrThrow(_stmt, "isShowcased")
        val _columnIndexOfExternalVideoUrl: Int = getColumnIndexOrThrow(_stmt, "externalVideoUrl")
        val _columnIndexOfRecordsLockedAt: Int = getColumnIndexOrThrow(_stmt, "recordsLockedAt")
        val _columnIndexOfAutoLockAfterDays: Int = getColumnIndexOrThrow(_stmt, "autoLockAfterDays")
        val _columnIndexOfLineageHistoryJson: Int = getColumnIndexOrThrow(_stmt,
            "lineageHistoryJson")
        val _columnIndexOfEditCount: Int = getColumnIndexOrThrow(_stmt, "editCount")
        val _columnIndexOfLastEditedBy: Int = getColumnIndexOrThrow(_stmt, "lastEditedBy")
        val _columnIndexOfAdminFlagged: Int = getColumnIndexOrThrow(_stmt, "adminFlagged")
        val _columnIndexOfModerationNote: Int = getColumnIndexOrThrow(_stmt, "moderationNote")
        val _columnIndexOfMetadataJson: Int = getColumnIndexOrThrow(_stmt, "metadataJson")
        val _result: MutableList<ProductEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: ProductEntity
          val _tmpProductId: String
          _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          val _tmpSellerId: String
          _tmpSellerId = _stmt.getText(_columnIndexOfSellerId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpDescription: String
          _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          val _tmpCategory: String
          _tmpCategory = _stmt.getText(_columnIndexOfCategory)
          val _tmpPrice: Double
          _tmpPrice = _stmt.getDouble(_columnIndexOfPrice)
          val _tmpQuantity: Double
          _tmpQuantity = _stmt.getDouble(_columnIndexOfQuantity)
          val _tmpUnit: String
          _tmpUnit = _stmt.getText(_columnIndexOfUnit)
          val _tmpLocation: String
          _tmpLocation = _stmt.getText(_columnIndexOfLocation)
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
          val _tmpImageUrls: List<String>
          val _tmp: String?
          if (_stmt.isNull(_columnIndexOfImageUrls)) {
            _tmp = null
          } else {
            _tmp = _stmt.getText(_columnIndexOfImageUrls)
          }
          val _tmp_1: List<String>? = AppDatabase.Converters.toStringList(_tmp)
          if (_tmp_1 == null) {
            error("Expected NON-NULL 'kotlin.collections.List<kotlin.String>', but it was NULL.")
          } else {
            _tmpImageUrls = _tmp_1
          }
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpCondition: String?
          if (_stmt.isNull(_columnIndexOfCondition)) {
            _tmpCondition = null
          } else {
            _tmpCondition = _stmt.getText(_columnIndexOfCondition)
          }
          val _tmpHarvestDate: Long?
          if (_stmt.isNull(_columnIndexOfHarvestDate)) {
            _tmpHarvestDate = null
          } else {
            _tmpHarvestDate = _stmt.getLong(_columnIndexOfHarvestDate)
          }
          val _tmpExpiryDate: Long?
          if (_stmt.isNull(_columnIndexOfExpiryDate)) {
            _tmpExpiryDate = null
          } else {
            _tmpExpiryDate = _stmt.getLong(_columnIndexOfExpiryDate)
          }
          val _tmpBirthDate: Long?
          if (_stmt.isNull(_columnIndexOfBirthDate)) {
            _tmpBirthDate = null
          } else {
            _tmpBirthDate = _stmt.getLong(_columnIndexOfBirthDate)
          }
          val _tmpVaccinationRecordsJson: String?
          if (_stmt.isNull(_columnIndexOfVaccinationRecordsJson)) {
            _tmpVaccinationRecordsJson = null
          } else {
            _tmpVaccinationRecordsJson = _stmt.getText(_columnIndexOfVaccinationRecordsJson)
          }
          val _tmpWeightGrams: Double?
          if (_stmt.isNull(_columnIndexOfWeightGrams)) {
            _tmpWeightGrams = null
          } else {
            _tmpWeightGrams = _stmt.getDouble(_columnIndexOfWeightGrams)
          }
          val _tmpHeightCm: Double?
          if (_stmt.isNull(_columnIndexOfHeightCm)) {
            _tmpHeightCm = null
          } else {
            _tmpHeightCm = _stmt.getDouble(_columnIndexOfHeightCm)
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
          val _tmpBreed: String?
          if (_stmt.isNull(_columnIndexOfBreed)) {
            _tmpBreed = null
          } else {
            _tmpBreed = _stmt.getText(_columnIndexOfBreed)
          }
          val _tmpRaisingPurpose: String?
          if (_stmt.isNull(_columnIndexOfRaisingPurpose)) {
            _tmpRaisingPurpose = null
          } else {
            _tmpRaisingPurpose = _stmt.getText(_columnIndexOfRaisingPurpose)
          }
          val _tmpHealthStatus: String?
          if (_stmt.isNull(_columnIndexOfHealthStatus)) {
            _tmpHealthStatus = null
          } else {
            _tmpHealthStatus = _stmt.getText(_columnIndexOfHealthStatus)
          }
          val _tmpBirdCode: String?
          if (_stmt.isNull(_columnIndexOfBirdCode)) {
            _tmpBirdCode = null
          } else {
            _tmpBirdCode = _stmt.getText(_columnIndexOfBirdCode)
          }
          val _tmpColorTag: String?
          if (_stmt.isNull(_columnIndexOfColorTag)) {
            _tmpColorTag = null
          } else {
            _tmpColorTag = _stmt.getText(_columnIndexOfColorTag)
          }
          val _tmpFamilyTreeId: String?
          if (_stmt.isNull(_columnIndexOfFamilyTreeId)) {
            _tmpFamilyTreeId = null
          } else {
            _tmpFamilyTreeId = _stmt.getText(_columnIndexOfFamilyTreeId)
          }
          val _tmpSourceAssetId: String?
          if (_stmt.isNull(_columnIndexOfSourceAssetId)) {
            _tmpSourceAssetId = null
          } else {
            _tmpSourceAssetId = _stmt.getText(_columnIndexOfSourceAssetId)
          }
          val _tmpParentIdsJson: String?
          if (_stmt.isNull(_columnIndexOfParentIdsJson)) {
            _tmpParentIdsJson = null
          } else {
            _tmpParentIdsJson = _stmt.getText(_columnIndexOfParentIdsJson)
          }
          val _tmpBreedingStatus: String?
          if (_stmt.isNull(_columnIndexOfBreedingStatus)) {
            _tmpBreedingStatus = null
          } else {
            _tmpBreedingStatus = _stmt.getText(_columnIndexOfBreedingStatus)
          }
          val _tmpTransferHistoryJson: String?
          if (_stmt.isNull(_columnIndexOfTransferHistoryJson)) {
            _tmpTransferHistoryJson = null
          } else {
            _tmpTransferHistoryJson = _stmt.getText(_columnIndexOfTransferHistoryJson)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpLastModifiedAt: Long
          _tmpLastModifiedAt = _stmt.getLong(_columnIndexOfLastModifiedAt)
          val _tmpIsDeleted: Boolean
          val _tmp_2: Int
          _tmp_2 = _stmt.getLong(_columnIndexOfIsDeleted).toInt()
          _tmpIsDeleted = _tmp_2 != 0
          val _tmpDeletedAt: Long?
          if (_stmt.isNull(_columnIndexOfDeletedAt)) {
            _tmpDeletedAt = null
          } else {
            _tmpDeletedAt = _stmt.getLong(_columnIndexOfDeletedAt)
          }
          val _tmpDirty: Boolean
          val _tmp_3: Int
          _tmp_3 = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp_3 != 0
          val _tmpStage: LifecycleStage?
          val _tmp_4: String?
          if (_stmt.isNull(_columnIndexOfStage)) {
            _tmp_4 = null
          } else {
            _tmp_4 = _stmt.getText(_columnIndexOfStage)
          }
          _tmpStage = AppDatabase.Converters.toLifecycleStage(_tmp_4)
          val _tmpLifecycleStatus: String?
          if (_stmt.isNull(_columnIndexOfLifecycleStatus)) {
            _tmpLifecycleStatus = null
          } else {
            _tmpLifecycleStatus = _stmt.getText(_columnIndexOfLifecycleStatus)
          }
          val _tmpParentMaleId: String?
          if (_stmt.isNull(_columnIndexOfParentMaleId)) {
            _tmpParentMaleId = null
          } else {
            _tmpParentMaleId = _stmt.getText(_columnIndexOfParentMaleId)
          }
          val _tmpParentFemaleId: String?
          if (_stmt.isNull(_columnIndexOfParentFemaleId)) {
            _tmpParentFemaleId = null
          } else {
            _tmpParentFemaleId = _stmt.getText(_columnIndexOfParentFemaleId)
          }
          val _tmpAgeWeeks: Int?
          if (_stmt.isNull(_columnIndexOfAgeWeeks)) {
            _tmpAgeWeeks = null
          } else {
            _tmpAgeWeeks = _stmt.getLong(_columnIndexOfAgeWeeks).toInt()
          }
          val _tmpLastStageTransitionAt: Long?
          if (_stmt.isNull(_columnIndexOfLastStageTransitionAt)) {
            _tmpLastStageTransitionAt = null
          } else {
            _tmpLastStageTransitionAt = _stmt.getLong(_columnIndexOfLastStageTransitionAt)
          }
          val _tmpBreederEligibleAt: Long?
          if (_stmt.isNull(_columnIndexOfBreederEligibleAt)) {
            _tmpBreederEligibleAt = null
          } else {
            _tmpBreederEligibleAt = _stmt.getLong(_columnIndexOfBreederEligibleAt)
          }
          val _tmpIsBatch: Boolean?
          val _tmp_5: Int?
          if (_stmt.isNull(_columnIndexOfIsBatch)) {
            _tmp_5 = null
          } else {
            _tmp_5 = _stmt.getLong(_columnIndexOfIsBatch).toInt()
          }
          _tmpIsBatch = _tmp_5?.let { it != 0 }
          val _tmpBatchId: String?
          if (_stmt.isNull(_columnIndexOfBatchId)) {
            _tmpBatchId = null
          } else {
            _tmpBatchId = _stmt.getText(_columnIndexOfBatchId)
          }
          val _tmpSplitAt: Long?
          if (_stmt.isNull(_columnIndexOfSplitAt)) {
            _tmpSplitAt = null
          } else {
            _tmpSplitAt = _stmt.getLong(_columnIndexOfSplitAt)
          }
          val _tmpSplitIntoIds: String?
          if (_stmt.isNull(_columnIndexOfSplitIntoIds)) {
            _tmpSplitIntoIds = null
          } else {
            _tmpSplitIntoIds = _stmt.getText(_columnIndexOfSplitIntoIds)
          }
          val _tmpDocumentUrls: List<String>
          val _tmp_6: String?
          if (_stmt.isNull(_columnIndexOfDocumentUrls)) {
            _tmp_6 = null
          } else {
            _tmp_6 = _stmt.getText(_columnIndexOfDocumentUrls)
          }
          val _tmp_7: List<String>? = AppDatabase.Converters.toStringList(_tmp_6)
          if (_tmp_7 == null) {
            error("Expected NON-NULL 'kotlin.collections.List<kotlin.String>', but it was NULL.")
          } else {
            _tmpDocumentUrls = _tmp_7
          }
          val _tmpQrCodeUrl: String?
          if (_stmt.isNull(_columnIndexOfQrCodeUrl)) {
            _tmpQrCodeUrl = null
          } else {
            _tmpQrCodeUrl = _stmt.getText(_columnIndexOfQrCodeUrl)
          }
          val _tmpCustomStatus: String?
          if (_stmt.isNull(_columnIndexOfCustomStatus)) {
            _tmpCustomStatus = null
          } else {
            _tmpCustomStatus = _stmt.getText(_columnIndexOfCustomStatus)
          }
          val _tmpDebug: Boolean
          val _tmp_8: Int
          _tmp_8 = _stmt.getLong(_columnIndexOfDebug).toInt()
          _tmpDebug = _tmp_8 != 0
          val _tmpDeliveryOptions: List<String>
          val _tmp_9: String?
          if (_stmt.isNull(_columnIndexOfDeliveryOptions)) {
            _tmp_9 = null
          } else {
            _tmp_9 = _stmt.getText(_columnIndexOfDeliveryOptions)
          }
          val _tmp_10: List<String>? = AppDatabase.Converters.toStringList(_tmp_9)
          if (_tmp_10 == null) {
            error("Expected NON-NULL 'kotlin.collections.List<kotlin.String>', but it was NULL.")
          } else {
            _tmpDeliveryOptions = _tmp_10
          }
          val _tmpDeliveryCost: Double?
          if (_stmt.isNull(_columnIndexOfDeliveryCost)) {
            _tmpDeliveryCost = null
          } else {
            _tmpDeliveryCost = _stmt.getDouble(_columnIndexOfDeliveryCost)
          }
          val _tmpLeadTimeDays: Int?
          if (_stmt.isNull(_columnIndexOfLeadTimeDays)) {
            _tmpLeadTimeDays = null
          } else {
            _tmpLeadTimeDays = _stmt.getLong(_columnIndexOfLeadTimeDays).toInt()
          }
          val _tmpMotherId: String?
          if (_stmt.isNull(_columnIndexOfMotherId)) {
            _tmpMotherId = null
          } else {
            _tmpMotherId = _stmt.getText(_columnIndexOfMotherId)
          }
          val _tmpIsBreedingUnit: Boolean
          val _tmp_11: Int
          _tmp_11 = _stmt.getLong(_columnIndexOfIsBreedingUnit).toInt()
          _tmpIsBreedingUnit = _tmp_11 != 0
          val _tmpEggsCollectedToday: Int
          _tmpEggsCollectedToday = _stmt.getLong(_columnIndexOfEggsCollectedToday).toInt()
          val _tmpLastEggLogDate: Long?
          if (_stmt.isNull(_columnIndexOfLastEggLogDate)) {
            _tmpLastEggLogDate = null
          } else {
            _tmpLastEggLogDate = _stmt.getLong(_columnIndexOfLastEggLogDate)
          }
          val _tmpReadyForSale: Boolean
          val _tmp_12: Int
          _tmp_12 = _stmt.getLong(_columnIndexOfReadyForSale).toInt()
          _tmpReadyForSale = _tmp_12 != 0
          val _tmpTargetWeight: Double?
          if (_stmt.isNull(_columnIndexOfTargetWeight)) {
            _tmpTargetWeight = null
          } else {
            _tmpTargetWeight = _stmt.getDouble(_columnIndexOfTargetWeight)
          }
          val _tmpIsShowcased: Boolean
          val _tmp_13: Int
          _tmp_13 = _stmt.getLong(_columnIndexOfIsShowcased).toInt()
          _tmpIsShowcased = _tmp_13 != 0
          val _tmpExternalVideoUrl: String?
          if (_stmt.isNull(_columnIndexOfExternalVideoUrl)) {
            _tmpExternalVideoUrl = null
          } else {
            _tmpExternalVideoUrl = _stmt.getText(_columnIndexOfExternalVideoUrl)
          }
          val _tmpRecordsLockedAt: Long?
          if (_stmt.isNull(_columnIndexOfRecordsLockedAt)) {
            _tmpRecordsLockedAt = null
          } else {
            _tmpRecordsLockedAt = _stmt.getLong(_columnIndexOfRecordsLockedAt)
          }
          val _tmpAutoLockAfterDays: Int
          _tmpAutoLockAfterDays = _stmt.getLong(_columnIndexOfAutoLockAfterDays).toInt()
          val _tmpLineageHistoryJson: String?
          if (_stmt.isNull(_columnIndexOfLineageHistoryJson)) {
            _tmpLineageHistoryJson = null
          } else {
            _tmpLineageHistoryJson = _stmt.getText(_columnIndexOfLineageHistoryJson)
          }
          val _tmpEditCount: Int
          _tmpEditCount = _stmt.getLong(_columnIndexOfEditCount).toInt()
          val _tmpLastEditedBy: String?
          if (_stmt.isNull(_columnIndexOfLastEditedBy)) {
            _tmpLastEditedBy = null
          } else {
            _tmpLastEditedBy = _stmt.getText(_columnIndexOfLastEditedBy)
          }
          val _tmpAdminFlagged: Boolean
          val _tmp_14: Int
          _tmp_14 = _stmt.getLong(_columnIndexOfAdminFlagged).toInt()
          _tmpAdminFlagged = _tmp_14 != 0
          val _tmpModerationNote: String?
          if (_stmt.isNull(_columnIndexOfModerationNote)) {
            _tmpModerationNote = null
          } else {
            _tmpModerationNote = _stmt.getText(_columnIndexOfModerationNote)
          }
          val _tmpMetadataJson: String
          _tmpMetadataJson = _stmt.getText(_columnIndexOfMetadataJson)
          _item =
              ProductEntity(_tmpProductId,_tmpSellerId,_tmpName,_tmpDescription,_tmpCategory,_tmpPrice,_tmpQuantity,_tmpUnit,_tmpLocation,_tmpLatitude,_tmpLongitude,_tmpImageUrls,_tmpStatus,_tmpCondition,_tmpHarvestDate,_tmpExpiryDate,_tmpBirthDate,_tmpVaccinationRecordsJson,_tmpWeightGrams,_tmpHeightCm,_tmpGender,_tmpColor,_tmpBreed,_tmpRaisingPurpose,_tmpHealthStatus,_tmpBirdCode,_tmpColorTag,_tmpFamilyTreeId,_tmpSourceAssetId,_tmpParentIdsJson,_tmpBreedingStatus,_tmpTransferHistoryJson,_tmpCreatedAt,_tmpUpdatedAt,_tmpLastModifiedAt,_tmpIsDeleted,_tmpDeletedAt,_tmpDirty,_tmpStage,_tmpLifecycleStatus,_tmpParentMaleId,_tmpParentFemaleId,_tmpAgeWeeks,_tmpLastStageTransitionAt,_tmpBreederEligibleAt,_tmpIsBatch,_tmpBatchId,_tmpSplitAt,_tmpSplitIntoIds,_tmpDocumentUrls,_tmpQrCodeUrl,_tmpCustomStatus,_tmpDebug,_tmpDeliveryOptions,_tmpDeliveryCost,_tmpLeadTimeDays,_tmpMotherId,_tmpIsBreedingUnit,_tmpEggsCollectedToday,_tmpLastEggLogDate,_tmpReadyForSale,_tmpTargetWeight,_tmpIsShowcased,_tmpExternalVideoUrl,_tmpRecordsLockedAt,_tmpAutoLockAfterDays,_tmpLineageHistoryJson,_tmpEditCount,_tmpLastEditedBy,_tmpAdminFlagged,_tmpModerationNote,_tmpMetadataJson)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun filterByDateRange(
    startDate: Long,
    endDate: Long,
    limit: Int,
    offset: Int,
  ): List<ProductEntity> {
    val _sql: String =
        "SELECT * FROM products WHERE createdAt >= ? AND createdAt <= ? AND isDeleted = 0 ORDER BY createdAt DESC LIMIT ? OFFSET ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, startDate)
        _argIndex = 2
        _stmt.bindLong(_argIndex, endDate)
        _argIndex = 3
        _stmt.bindLong(_argIndex, limit.toLong())
        _argIndex = 4
        _stmt.bindLong(_argIndex, offset.toLong())
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfSellerId: Int = getColumnIndexOrThrow(_stmt, "sellerId")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfCategory: Int = getColumnIndexOrThrow(_stmt, "category")
        val _columnIndexOfPrice: Int = getColumnIndexOrThrow(_stmt, "price")
        val _columnIndexOfQuantity: Int = getColumnIndexOrThrow(_stmt, "quantity")
        val _columnIndexOfUnit: Int = getColumnIndexOrThrow(_stmt, "unit")
        val _columnIndexOfLocation: Int = getColumnIndexOrThrow(_stmt, "location")
        val _columnIndexOfLatitude: Int = getColumnIndexOrThrow(_stmt, "latitude")
        val _columnIndexOfLongitude: Int = getColumnIndexOrThrow(_stmt, "longitude")
        val _columnIndexOfImageUrls: Int = getColumnIndexOrThrow(_stmt, "imageUrls")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfCondition: Int = getColumnIndexOrThrow(_stmt, "condition")
        val _columnIndexOfHarvestDate: Int = getColumnIndexOrThrow(_stmt, "harvestDate")
        val _columnIndexOfExpiryDate: Int = getColumnIndexOrThrow(_stmt, "expiryDate")
        val _columnIndexOfBirthDate: Int = getColumnIndexOrThrow(_stmt, "birthDate")
        val _columnIndexOfVaccinationRecordsJson: Int = getColumnIndexOrThrow(_stmt,
            "vaccinationRecordsJson")
        val _columnIndexOfWeightGrams: Int = getColumnIndexOrThrow(_stmt, "weightGrams")
        val _columnIndexOfHeightCm: Int = getColumnIndexOrThrow(_stmt, "heightCm")
        val _columnIndexOfGender: Int = getColumnIndexOrThrow(_stmt, "gender")
        val _columnIndexOfColor: Int = getColumnIndexOrThrow(_stmt, "color")
        val _columnIndexOfBreed: Int = getColumnIndexOrThrow(_stmt, "breed")
        val _columnIndexOfRaisingPurpose: Int = getColumnIndexOrThrow(_stmt, "raisingPurpose")
        val _columnIndexOfHealthStatus: Int = getColumnIndexOrThrow(_stmt, "healthStatus")
        val _columnIndexOfBirdCode: Int = getColumnIndexOrThrow(_stmt, "birdCode")
        val _columnIndexOfColorTag: Int = getColumnIndexOrThrow(_stmt, "colorTag")
        val _columnIndexOfFamilyTreeId: Int = getColumnIndexOrThrow(_stmt, "familyTreeId")
        val _columnIndexOfSourceAssetId: Int = getColumnIndexOrThrow(_stmt, "sourceAssetId")
        val _columnIndexOfParentIdsJson: Int = getColumnIndexOrThrow(_stmt, "parentIdsJson")
        val _columnIndexOfBreedingStatus: Int = getColumnIndexOrThrow(_stmt, "breedingStatus")
        val _columnIndexOfTransferHistoryJson: Int = getColumnIndexOrThrow(_stmt,
            "transferHistoryJson")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfLastModifiedAt: Int = getColumnIndexOrThrow(_stmt, "lastModifiedAt")
        val _columnIndexOfIsDeleted: Int = getColumnIndexOrThrow(_stmt, "isDeleted")
        val _columnIndexOfDeletedAt: Int = getColumnIndexOrThrow(_stmt, "deletedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfStage: Int = getColumnIndexOrThrow(_stmt, "stage")
        val _columnIndexOfLifecycleStatus: Int = getColumnIndexOrThrow(_stmt, "lifecycleStatus")
        val _columnIndexOfParentMaleId: Int = getColumnIndexOrThrow(_stmt, "parentMaleId")
        val _columnIndexOfParentFemaleId: Int = getColumnIndexOrThrow(_stmt, "parentFemaleId")
        val _columnIndexOfAgeWeeks: Int = getColumnIndexOrThrow(_stmt, "ageWeeks")
        val _columnIndexOfLastStageTransitionAt: Int = getColumnIndexOrThrow(_stmt,
            "lastStageTransitionAt")
        val _columnIndexOfBreederEligibleAt: Int = getColumnIndexOrThrow(_stmt, "breederEligibleAt")
        val _columnIndexOfIsBatch: Int = getColumnIndexOrThrow(_stmt, "isBatch")
        val _columnIndexOfBatchId: Int = getColumnIndexOrThrow(_stmt, "batchId")
        val _columnIndexOfSplitAt: Int = getColumnIndexOrThrow(_stmt, "splitAt")
        val _columnIndexOfSplitIntoIds: Int = getColumnIndexOrThrow(_stmt, "splitIntoIds")
        val _columnIndexOfDocumentUrls: Int = getColumnIndexOrThrow(_stmt, "documentUrls")
        val _columnIndexOfQrCodeUrl: Int = getColumnIndexOrThrow(_stmt, "qrCodeUrl")
        val _columnIndexOfCustomStatus: Int = getColumnIndexOrThrow(_stmt, "customStatus")
        val _columnIndexOfDebug: Int = getColumnIndexOrThrow(_stmt, "debug")
        val _columnIndexOfDeliveryOptions: Int = getColumnIndexOrThrow(_stmt, "deliveryOptions")
        val _columnIndexOfDeliveryCost: Int = getColumnIndexOrThrow(_stmt, "deliveryCost")
        val _columnIndexOfLeadTimeDays: Int = getColumnIndexOrThrow(_stmt, "leadTimeDays")
        val _columnIndexOfMotherId: Int = getColumnIndexOrThrow(_stmt, "motherId")
        val _columnIndexOfIsBreedingUnit: Int = getColumnIndexOrThrow(_stmt, "isBreedingUnit")
        val _columnIndexOfEggsCollectedToday: Int = getColumnIndexOrThrow(_stmt,
            "eggsCollectedToday")
        val _columnIndexOfLastEggLogDate: Int = getColumnIndexOrThrow(_stmt, "lastEggLogDate")
        val _columnIndexOfReadyForSale: Int = getColumnIndexOrThrow(_stmt, "readyForSale")
        val _columnIndexOfTargetWeight: Int = getColumnIndexOrThrow(_stmt, "targetWeight")
        val _columnIndexOfIsShowcased: Int = getColumnIndexOrThrow(_stmt, "isShowcased")
        val _columnIndexOfExternalVideoUrl: Int = getColumnIndexOrThrow(_stmt, "externalVideoUrl")
        val _columnIndexOfRecordsLockedAt: Int = getColumnIndexOrThrow(_stmt, "recordsLockedAt")
        val _columnIndexOfAutoLockAfterDays: Int = getColumnIndexOrThrow(_stmt, "autoLockAfterDays")
        val _columnIndexOfLineageHistoryJson: Int = getColumnIndexOrThrow(_stmt,
            "lineageHistoryJson")
        val _columnIndexOfEditCount: Int = getColumnIndexOrThrow(_stmt, "editCount")
        val _columnIndexOfLastEditedBy: Int = getColumnIndexOrThrow(_stmt, "lastEditedBy")
        val _columnIndexOfAdminFlagged: Int = getColumnIndexOrThrow(_stmt, "adminFlagged")
        val _columnIndexOfModerationNote: Int = getColumnIndexOrThrow(_stmt, "moderationNote")
        val _columnIndexOfMetadataJson: Int = getColumnIndexOrThrow(_stmt, "metadataJson")
        val _result: MutableList<ProductEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: ProductEntity
          val _tmpProductId: String
          _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          val _tmpSellerId: String
          _tmpSellerId = _stmt.getText(_columnIndexOfSellerId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpDescription: String
          _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          val _tmpCategory: String
          _tmpCategory = _stmt.getText(_columnIndexOfCategory)
          val _tmpPrice: Double
          _tmpPrice = _stmt.getDouble(_columnIndexOfPrice)
          val _tmpQuantity: Double
          _tmpQuantity = _stmt.getDouble(_columnIndexOfQuantity)
          val _tmpUnit: String
          _tmpUnit = _stmt.getText(_columnIndexOfUnit)
          val _tmpLocation: String
          _tmpLocation = _stmt.getText(_columnIndexOfLocation)
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
          val _tmpImageUrls: List<String>
          val _tmp: String?
          if (_stmt.isNull(_columnIndexOfImageUrls)) {
            _tmp = null
          } else {
            _tmp = _stmt.getText(_columnIndexOfImageUrls)
          }
          val _tmp_1: List<String>? = AppDatabase.Converters.toStringList(_tmp)
          if (_tmp_1 == null) {
            error("Expected NON-NULL 'kotlin.collections.List<kotlin.String>', but it was NULL.")
          } else {
            _tmpImageUrls = _tmp_1
          }
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpCondition: String?
          if (_stmt.isNull(_columnIndexOfCondition)) {
            _tmpCondition = null
          } else {
            _tmpCondition = _stmt.getText(_columnIndexOfCondition)
          }
          val _tmpHarvestDate: Long?
          if (_stmt.isNull(_columnIndexOfHarvestDate)) {
            _tmpHarvestDate = null
          } else {
            _tmpHarvestDate = _stmt.getLong(_columnIndexOfHarvestDate)
          }
          val _tmpExpiryDate: Long?
          if (_stmt.isNull(_columnIndexOfExpiryDate)) {
            _tmpExpiryDate = null
          } else {
            _tmpExpiryDate = _stmt.getLong(_columnIndexOfExpiryDate)
          }
          val _tmpBirthDate: Long?
          if (_stmt.isNull(_columnIndexOfBirthDate)) {
            _tmpBirthDate = null
          } else {
            _tmpBirthDate = _stmt.getLong(_columnIndexOfBirthDate)
          }
          val _tmpVaccinationRecordsJson: String?
          if (_stmt.isNull(_columnIndexOfVaccinationRecordsJson)) {
            _tmpVaccinationRecordsJson = null
          } else {
            _tmpVaccinationRecordsJson = _stmt.getText(_columnIndexOfVaccinationRecordsJson)
          }
          val _tmpWeightGrams: Double?
          if (_stmt.isNull(_columnIndexOfWeightGrams)) {
            _tmpWeightGrams = null
          } else {
            _tmpWeightGrams = _stmt.getDouble(_columnIndexOfWeightGrams)
          }
          val _tmpHeightCm: Double?
          if (_stmt.isNull(_columnIndexOfHeightCm)) {
            _tmpHeightCm = null
          } else {
            _tmpHeightCm = _stmt.getDouble(_columnIndexOfHeightCm)
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
          val _tmpBreed: String?
          if (_stmt.isNull(_columnIndexOfBreed)) {
            _tmpBreed = null
          } else {
            _tmpBreed = _stmt.getText(_columnIndexOfBreed)
          }
          val _tmpRaisingPurpose: String?
          if (_stmt.isNull(_columnIndexOfRaisingPurpose)) {
            _tmpRaisingPurpose = null
          } else {
            _tmpRaisingPurpose = _stmt.getText(_columnIndexOfRaisingPurpose)
          }
          val _tmpHealthStatus: String?
          if (_stmt.isNull(_columnIndexOfHealthStatus)) {
            _tmpHealthStatus = null
          } else {
            _tmpHealthStatus = _stmt.getText(_columnIndexOfHealthStatus)
          }
          val _tmpBirdCode: String?
          if (_stmt.isNull(_columnIndexOfBirdCode)) {
            _tmpBirdCode = null
          } else {
            _tmpBirdCode = _stmt.getText(_columnIndexOfBirdCode)
          }
          val _tmpColorTag: String?
          if (_stmt.isNull(_columnIndexOfColorTag)) {
            _tmpColorTag = null
          } else {
            _tmpColorTag = _stmt.getText(_columnIndexOfColorTag)
          }
          val _tmpFamilyTreeId: String?
          if (_stmt.isNull(_columnIndexOfFamilyTreeId)) {
            _tmpFamilyTreeId = null
          } else {
            _tmpFamilyTreeId = _stmt.getText(_columnIndexOfFamilyTreeId)
          }
          val _tmpSourceAssetId: String?
          if (_stmt.isNull(_columnIndexOfSourceAssetId)) {
            _tmpSourceAssetId = null
          } else {
            _tmpSourceAssetId = _stmt.getText(_columnIndexOfSourceAssetId)
          }
          val _tmpParentIdsJson: String?
          if (_stmt.isNull(_columnIndexOfParentIdsJson)) {
            _tmpParentIdsJson = null
          } else {
            _tmpParentIdsJson = _stmt.getText(_columnIndexOfParentIdsJson)
          }
          val _tmpBreedingStatus: String?
          if (_stmt.isNull(_columnIndexOfBreedingStatus)) {
            _tmpBreedingStatus = null
          } else {
            _tmpBreedingStatus = _stmt.getText(_columnIndexOfBreedingStatus)
          }
          val _tmpTransferHistoryJson: String?
          if (_stmt.isNull(_columnIndexOfTransferHistoryJson)) {
            _tmpTransferHistoryJson = null
          } else {
            _tmpTransferHistoryJson = _stmt.getText(_columnIndexOfTransferHistoryJson)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpLastModifiedAt: Long
          _tmpLastModifiedAt = _stmt.getLong(_columnIndexOfLastModifiedAt)
          val _tmpIsDeleted: Boolean
          val _tmp_2: Int
          _tmp_2 = _stmt.getLong(_columnIndexOfIsDeleted).toInt()
          _tmpIsDeleted = _tmp_2 != 0
          val _tmpDeletedAt: Long?
          if (_stmt.isNull(_columnIndexOfDeletedAt)) {
            _tmpDeletedAt = null
          } else {
            _tmpDeletedAt = _stmt.getLong(_columnIndexOfDeletedAt)
          }
          val _tmpDirty: Boolean
          val _tmp_3: Int
          _tmp_3 = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp_3 != 0
          val _tmpStage: LifecycleStage?
          val _tmp_4: String?
          if (_stmt.isNull(_columnIndexOfStage)) {
            _tmp_4 = null
          } else {
            _tmp_4 = _stmt.getText(_columnIndexOfStage)
          }
          _tmpStage = AppDatabase.Converters.toLifecycleStage(_tmp_4)
          val _tmpLifecycleStatus: String?
          if (_stmt.isNull(_columnIndexOfLifecycleStatus)) {
            _tmpLifecycleStatus = null
          } else {
            _tmpLifecycleStatus = _stmt.getText(_columnIndexOfLifecycleStatus)
          }
          val _tmpParentMaleId: String?
          if (_stmt.isNull(_columnIndexOfParentMaleId)) {
            _tmpParentMaleId = null
          } else {
            _tmpParentMaleId = _stmt.getText(_columnIndexOfParentMaleId)
          }
          val _tmpParentFemaleId: String?
          if (_stmt.isNull(_columnIndexOfParentFemaleId)) {
            _tmpParentFemaleId = null
          } else {
            _tmpParentFemaleId = _stmt.getText(_columnIndexOfParentFemaleId)
          }
          val _tmpAgeWeeks: Int?
          if (_stmt.isNull(_columnIndexOfAgeWeeks)) {
            _tmpAgeWeeks = null
          } else {
            _tmpAgeWeeks = _stmt.getLong(_columnIndexOfAgeWeeks).toInt()
          }
          val _tmpLastStageTransitionAt: Long?
          if (_stmt.isNull(_columnIndexOfLastStageTransitionAt)) {
            _tmpLastStageTransitionAt = null
          } else {
            _tmpLastStageTransitionAt = _stmt.getLong(_columnIndexOfLastStageTransitionAt)
          }
          val _tmpBreederEligibleAt: Long?
          if (_stmt.isNull(_columnIndexOfBreederEligibleAt)) {
            _tmpBreederEligibleAt = null
          } else {
            _tmpBreederEligibleAt = _stmt.getLong(_columnIndexOfBreederEligibleAt)
          }
          val _tmpIsBatch: Boolean?
          val _tmp_5: Int?
          if (_stmt.isNull(_columnIndexOfIsBatch)) {
            _tmp_5 = null
          } else {
            _tmp_5 = _stmt.getLong(_columnIndexOfIsBatch).toInt()
          }
          _tmpIsBatch = _tmp_5?.let { it != 0 }
          val _tmpBatchId: String?
          if (_stmt.isNull(_columnIndexOfBatchId)) {
            _tmpBatchId = null
          } else {
            _tmpBatchId = _stmt.getText(_columnIndexOfBatchId)
          }
          val _tmpSplitAt: Long?
          if (_stmt.isNull(_columnIndexOfSplitAt)) {
            _tmpSplitAt = null
          } else {
            _tmpSplitAt = _stmt.getLong(_columnIndexOfSplitAt)
          }
          val _tmpSplitIntoIds: String?
          if (_stmt.isNull(_columnIndexOfSplitIntoIds)) {
            _tmpSplitIntoIds = null
          } else {
            _tmpSplitIntoIds = _stmt.getText(_columnIndexOfSplitIntoIds)
          }
          val _tmpDocumentUrls: List<String>
          val _tmp_6: String?
          if (_stmt.isNull(_columnIndexOfDocumentUrls)) {
            _tmp_6 = null
          } else {
            _tmp_6 = _stmt.getText(_columnIndexOfDocumentUrls)
          }
          val _tmp_7: List<String>? = AppDatabase.Converters.toStringList(_tmp_6)
          if (_tmp_7 == null) {
            error("Expected NON-NULL 'kotlin.collections.List<kotlin.String>', but it was NULL.")
          } else {
            _tmpDocumentUrls = _tmp_7
          }
          val _tmpQrCodeUrl: String?
          if (_stmt.isNull(_columnIndexOfQrCodeUrl)) {
            _tmpQrCodeUrl = null
          } else {
            _tmpQrCodeUrl = _stmt.getText(_columnIndexOfQrCodeUrl)
          }
          val _tmpCustomStatus: String?
          if (_stmt.isNull(_columnIndexOfCustomStatus)) {
            _tmpCustomStatus = null
          } else {
            _tmpCustomStatus = _stmt.getText(_columnIndexOfCustomStatus)
          }
          val _tmpDebug: Boolean
          val _tmp_8: Int
          _tmp_8 = _stmt.getLong(_columnIndexOfDebug).toInt()
          _tmpDebug = _tmp_8 != 0
          val _tmpDeliveryOptions: List<String>
          val _tmp_9: String?
          if (_stmt.isNull(_columnIndexOfDeliveryOptions)) {
            _tmp_9 = null
          } else {
            _tmp_9 = _stmt.getText(_columnIndexOfDeliveryOptions)
          }
          val _tmp_10: List<String>? = AppDatabase.Converters.toStringList(_tmp_9)
          if (_tmp_10 == null) {
            error("Expected NON-NULL 'kotlin.collections.List<kotlin.String>', but it was NULL.")
          } else {
            _tmpDeliveryOptions = _tmp_10
          }
          val _tmpDeliveryCost: Double?
          if (_stmt.isNull(_columnIndexOfDeliveryCost)) {
            _tmpDeliveryCost = null
          } else {
            _tmpDeliveryCost = _stmt.getDouble(_columnIndexOfDeliveryCost)
          }
          val _tmpLeadTimeDays: Int?
          if (_stmt.isNull(_columnIndexOfLeadTimeDays)) {
            _tmpLeadTimeDays = null
          } else {
            _tmpLeadTimeDays = _stmt.getLong(_columnIndexOfLeadTimeDays).toInt()
          }
          val _tmpMotherId: String?
          if (_stmt.isNull(_columnIndexOfMotherId)) {
            _tmpMotherId = null
          } else {
            _tmpMotherId = _stmt.getText(_columnIndexOfMotherId)
          }
          val _tmpIsBreedingUnit: Boolean
          val _tmp_11: Int
          _tmp_11 = _stmt.getLong(_columnIndexOfIsBreedingUnit).toInt()
          _tmpIsBreedingUnit = _tmp_11 != 0
          val _tmpEggsCollectedToday: Int
          _tmpEggsCollectedToday = _stmt.getLong(_columnIndexOfEggsCollectedToday).toInt()
          val _tmpLastEggLogDate: Long?
          if (_stmt.isNull(_columnIndexOfLastEggLogDate)) {
            _tmpLastEggLogDate = null
          } else {
            _tmpLastEggLogDate = _stmt.getLong(_columnIndexOfLastEggLogDate)
          }
          val _tmpReadyForSale: Boolean
          val _tmp_12: Int
          _tmp_12 = _stmt.getLong(_columnIndexOfReadyForSale).toInt()
          _tmpReadyForSale = _tmp_12 != 0
          val _tmpTargetWeight: Double?
          if (_stmt.isNull(_columnIndexOfTargetWeight)) {
            _tmpTargetWeight = null
          } else {
            _tmpTargetWeight = _stmt.getDouble(_columnIndexOfTargetWeight)
          }
          val _tmpIsShowcased: Boolean
          val _tmp_13: Int
          _tmp_13 = _stmt.getLong(_columnIndexOfIsShowcased).toInt()
          _tmpIsShowcased = _tmp_13 != 0
          val _tmpExternalVideoUrl: String?
          if (_stmt.isNull(_columnIndexOfExternalVideoUrl)) {
            _tmpExternalVideoUrl = null
          } else {
            _tmpExternalVideoUrl = _stmt.getText(_columnIndexOfExternalVideoUrl)
          }
          val _tmpRecordsLockedAt: Long?
          if (_stmt.isNull(_columnIndexOfRecordsLockedAt)) {
            _tmpRecordsLockedAt = null
          } else {
            _tmpRecordsLockedAt = _stmt.getLong(_columnIndexOfRecordsLockedAt)
          }
          val _tmpAutoLockAfterDays: Int
          _tmpAutoLockAfterDays = _stmt.getLong(_columnIndexOfAutoLockAfterDays).toInt()
          val _tmpLineageHistoryJson: String?
          if (_stmt.isNull(_columnIndexOfLineageHistoryJson)) {
            _tmpLineageHistoryJson = null
          } else {
            _tmpLineageHistoryJson = _stmt.getText(_columnIndexOfLineageHistoryJson)
          }
          val _tmpEditCount: Int
          _tmpEditCount = _stmt.getLong(_columnIndexOfEditCount).toInt()
          val _tmpLastEditedBy: String?
          if (_stmt.isNull(_columnIndexOfLastEditedBy)) {
            _tmpLastEditedBy = null
          } else {
            _tmpLastEditedBy = _stmt.getText(_columnIndexOfLastEditedBy)
          }
          val _tmpAdminFlagged: Boolean
          val _tmp_14: Int
          _tmp_14 = _stmt.getLong(_columnIndexOfAdminFlagged).toInt()
          _tmpAdminFlagged = _tmp_14 != 0
          val _tmpModerationNote: String?
          if (_stmt.isNull(_columnIndexOfModerationNote)) {
            _tmpModerationNote = null
          } else {
            _tmpModerationNote = _stmt.getText(_columnIndexOfModerationNote)
          }
          val _tmpMetadataJson: String
          _tmpMetadataJson = _stmt.getText(_columnIndexOfMetadataJson)
          _item =
              ProductEntity(_tmpProductId,_tmpSellerId,_tmpName,_tmpDescription,_tmpCategory,_tmpPrice,_tmpQuantity,_tmpUnit,_tmpLocation,_tmpLatitude,_tmpLongitude,_tmpImageUrls,_tmpStatus,_tmpCondition,_tmpHarvestDate,_tmpExpiryDate,_tmpBirthDate,_tmpVaccinationRecordsJson,_tmpWeightGrams,_tmpHeightCm,_tmpGender,_tmpColor,_tmpBreed,_tmpRaisingPurpose,_tmpHealthStatus,_tmpBirdCode,_tmpColorTag,_tmpFamilyTreeId,_tmpSourceAssetId,_tmpParentIdsJson,_tmpBreedingStatus,_tmpTransferHistoryJson,_tmpCreatedAt,_tmpUpdatedAt,_tmpLastModifiedAt,_tmpIsDeleted,_tmpDeletedAt,_tmpDirty,_tmpStage,_tmpLifecycleStatus,_tmpParentMaleId,_tmpParentFemaleId,_tmpAgeWeeks,_tmpLastStageTransitionAt,_tmpBreederEligibleAt,_tmpIsBatch,_tmpBatchId,_tmpSplitAt,_tmpSplitIntoIds,_tmpDocumentUrls,_tmpQrCodeUrl,_tmpCustomStatus,_tmpDebug,_tmpDeliveryOptions,_tmpDeliveryCost,_tmpLeadTimeDays,_tmpMotherId,_tmpIsBreedingUnit,_tmpEggsCollectedToday,_tmpLastEggLogDate,_tmpReadyForSale,_tmpTargetWeight,_tmpIsShowcased,_tmpExternalVideoUrl,_tmpRecordsLockedAt,_tmpAutoLockAfterDays,_tmpLineageHistoryJson,_tmpEditCount,_tmpLastEditedBy,_tmpAdminFlagged,_tmpModerationNote,_tmpMetadataJson)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getUpdatedSince(since: Long, limit: Int): List<ProductEntity> {
    val _sql: String = "SELECT * FROM products WHERE updatedAt > ? ORDER BY updatedAt ASC LIMIT ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, since)
        _argIndex = 2
        _stmt.bindLong(_argIndex, limit.toLong())
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfSellerId: Int = getColumnIndexOrThrow(_stmt, "sellerId")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfCategory: Int = getColumnIndexOrThrow(_stmt, "category")
        val _columnIndexOfPrice: Int = getColumnIndexOrThrow(_stmt, "price")
        val _columnIndexOfQuantity: Int = getColumnIndexOrThrow(_stmt, "quantity")
        val _columnIndexOfUnit: Int = getColumnIndexOrThrow(_stmt, "unit")
        val _columnIndexOfLocation: Int = getColumnIndexOrThrow(_stmt, "location")
        val _columnIndexOfLatitude: Int = getColumnIndexOrThrow(_stmt, "latitude")
        val _columnIndexOfLongitude: Int = getColumnIndexOrThrow(_stmt, "longitude")
        val _columnIndexOfImageUrls: Int = getColumnIndexOrThrow(_stmt, "imageUrls")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfCondition: Int = getColumnIndexOrThrow(_stmt, "condition")
        val _columnIndexOfHarvestDate: Int = getColumnIndexOrThrow(_stmt, "harvestDate")
        val _columnIndexOfExpiryDate: Int = getColumnIndexOrThrow(_stmt, "expiryDate")
        val _columnIndexOfBirthDate: Int = getColumnIndexOrThrow(_stmt, "birthDate")
        val _columnIndexOfVaccinationRecordsJson: Int = getColumnIndexOrThrow(_stmt,
            "vaccinationRecordsJson")
        val _columnIndexOfWeightGrams: Int = getColumnIndexOrThrow(_stmt, "weightGrams")
        val _columnIndexOfHeightCm: Int = getColumnIndexOrThrow(_stmt, "heightCm")
        val _columnIndexOfGender: Int = getColumnIndexOrThrow(_stmt, "gender")
        val _columnIndexOfColor: Int = getColumnIndexOrThrow(_stmt, "color")
        val _columnIndexOfBreed: Int = getColumnIndexOrThrow(_stmt, "breed")
        val _columnIndexOfRaisingPurpose: Int = getColumnIndexOrThrow(_stmt, "raisingPurpose")
        val _columnIndexOfHealthStatus: Int = getColumnIndexOrThrow(_stmt, "healthStatus")
        val _columnIndexOfBirdCode: Int = getColumnIndexOrThrow(_stmt, "birdCode")
        val _columnIndexOfColorTag: Int = getColumnIndexOrThrow(_stmt, "colorTag")
        val _columnIndexOfFamilyTreeId: Int = getColumnIndexOrThrow(_stmt, "familyTreeId")
        val _columnIndexOfSourceAssetId: Int = getColumnIndexOrThrow(_stmt, "sourceAssetId")
        val _columnIndexOfParentIdsJson: Int = getColumnIndexOrThrow(_stmt, "parentIdsJson")
        val _columnIndexOfBreedingStatus: Int = getColumnIndexOrThrow(_stmt, "breedingStatus")
        val _columnIndexOfTransferHistoryJson: Int = getColumnIndexOrThrow(_stmt,
            "transferHistoryJson")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfLastModifiedAt: Int = getColumnIndexOrThrow(_stmt, "lastModifiedAt")
        val _columnIndexOfIsDeleted: Int = getColumnIndexOrThrow(_stmt, "isDeleted")
        val _columnIndexOfDeletedAt: Int = getColumnIndexOrThrow(_stmt, "deletedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfStage: Int = getColumnIndexOrThrow(_stmt, "stage")
        val _columnIndexOfLifecycleStatus: Int = getColumnIndexOrThrow(_stmt, "lifecycleStatus")
        val _columnIndexOfParentMaleId: Int = getColumnIndexOrThrow(_stmt, "parentMaleId")
        val _columnIndexOfParentFemaleId: Int = getColumnIndexOrThrow(_stmt, "parentFemaleId")
        val _columnIndexOfAgeWeeks: Int = getColumnIndexOrThrow(_stmt, "ageWeeks")
        val _columnIndexOfLastStageTransitionAt: Int = getColumnIndexOrThrow(_stmt,
            "lastStageTransitionAt")
        val _columnIndexOfBreederEligibleAt: Int = getColumnIndexOrThrow(_stmt, "breederEligibleAt")
        val _columnIndexOfIsBatch: Int = getColumnIndexOrThrow(_stmt, "isBatch")
        val _columnIndexOfBatchId: Int = getColumnIndexOrThrow(_stmt, "batchId")
        val _columnIndexOfSplitAt: Int = getColumnIndexOrThrow(_stmt, "splitAt")
        val _columnIndexOfSplitIntoIds: Int = getColumnIndexOrThrow(_stmt, "splitIntoIds")
        val _columnIndexOfDocumentUrls: Int = getColumnIndexOrThrow(_stmt, "documentUrls")
        val _columnIndexOfQrCodeUrl: Int = getColumnIndexOrThrow(_stmt, "qrCodeUrl")
        val _columnIndexOfCustomStatus: Int = getColumnIndexOrThrow(_stmt, "customStatus")
        val _columnIndexOfDebug: Int = getColumnIndexOrThrow(_stmt, "debug")
        val _columnIndexOfDeliveryOptions: Int = getColumnIndexOrThrow(_stmt, "deliveryOptions")
        val _columnIndexOfDeliveryCost: Int = getColumnIndexOrThrow(_stmt, "deliveryCost")
        val _columnIndexOfLeadTimeDays: Int = getColumnIndexOrThrow(_stmt, "leadTimeDays")
        val _columnIndexOfMotherId: Int = getColumnIndexOrThrow(_stmt, "motherId")
        val _columnIndexOfIsBreedingUnit: Int = getColumnIndexOrThrow(_stmt, "isBreedingUnit")
        val _columnIndexOfEggsCollectedToday: Int = getColumnIndexOrThrow(_stmt,
            "eggsCollectedToday")
        val _columnIndexOfLastEggLogDate: Int = getColumnIndexOrThrow(_stmt, "lastEggLogDate")
        val _columnIndexOfReadyForSale: Int = getColumnIndexOrThrow(_stmt, "readyForSale")
        val _columnIndexOfTargetWeight: Int = getColumnIndexOrThrow(_stmt, "targetWeight")
        val _columnIndexOfIsShowcased: Int = getColumnIndexOrThrow(_stmt, "isShowcased")
        val _columnIndexOfExternalVideoUrl: Int = getColumnIndexOrThrow(_stmt, "externalVideoUrl")
        val _columnIndexOfRecordsLockedAt: Int = getColumnIndexOrThrow(_stmt, "recordsLockedAt")
        val _columnIndexOfAutoLockAfterDays: Int = getColumnIndexOrThrow(_stmt, "autoLockAfterDays")
        val _columnIndexOfLineageHistoryJson: Int = getColumnIndexOrThrow(_stmt,
            "lineageHistoryJson")
        val _columnIndexOfEditCount: Int = getColumnIndexOrThrow(_stmt, "editCount")
        val _columnIndexOfLastEditedBy: Int = getColumnIndexOrThrow(_stmt, "lastEditedBy")
        val _columnIndexOfAdminFlagged: Int = getColumnIndexOrThrow(_stmt, "adminFlagged")
        val _columnIndexOfModerationNote: Int = getColumnIndexOrThrow(_stmt, "moderationNote")
        val _columnIndexOfMetadataJson: Int = getColumnIndexOrThrow(_stmt, "metadataJson")
        val _result: MutableList<ProductEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: ProductEntity
          val _tmpProductId: String
          _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          val _tmpSellerId: String
          _tmpSellerId = _stmt.getText(_columnIndexOfSellerId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpDescription: String
          _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          val _tmpCategory: String
          _tmpCategory = _stmt.getText(_columnIndexOfCategory)
          val _tmpPrice: Double
          _tmpPrice = _stmt.getDouble(_columnIndexOfPrice)
          val _tmpQuantity: Double
          _tmpQuantity = _stmt.getDouble(_columnIndexOfQuantity)
          val _tmpUnit: String
          _tmpUnit = _stmt.getText(_columnIndexOfUnit)
          val _tmpLocation: String
          _tmpLocation = _stmt.getText(_columnIndexOfLocation)
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
          val _tmpImageUrls: List<String>
          val _tmp: String?
          if (_stmt.isNull(_columnIndexOfImageUrls)) {
            _tmp = null
          } else {
            _tmp = _stmt.getText(_columnIndexOfImageUrls)
          }
          val _tmp_1: List<String>? = AppDatabase.Converters.toStringList(_tmp)
          if (_tmp_1 == null) {
            error("Expected NON-NULL 'kotlin.collections.List<kotlin.String>', but it was NULL.")
          } else {
            _tmpImageUrls = _tmp_1
          }
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpCondition: String?
          if (_stmt.isNull(_columnIndexOfCondition)) {
            _tmpCondition = null
          } else {
            _tmpCondition = _stmt.getText(_columnIndexOfCondition)
          }
          val _tmpHarvestDate: Long?
          if (_stmt.isNull(_columnIndexOfHarvestDate)) {
            _tmpHarvestDate = null
          } else {
            _tmpHarvestDate = _stmt.getLong(_columnIndexOfHarvestDate)
          }
          val _tmpExpiryDate: Long?
          if (_stmt.isNull(_columnIndexOfExpiryDate)) {
            _tmpExpiryDate = null
          } else {
            _tmpExpiryDate = _stmt.getLong(_columnIndexOfExpiryDate)
          }
          val _tmpBirthDate: Long?
          if (_stmt.isNull(_columnIndexOfBirthDate)) {
            _tmpBirthDate = null
          } else {
            _tmpBirthDate = _stmt.getLong(_columnIndexOfBirthDate)
          }
          val _tmpVaccinationRecordsJson: String?
          if (_stmt.isNull(_columnIndexOfVaccinationRecordsJson)) {
            _tmpVaccinationRecordsJson = null
          } else {
            _tmpVaccinationRecordsJson = _stmt.getText(_columnIndexOfVaccinationRecordsJson)
          }
          val _tmpWeightGrams: Double?
          if (_stmt.isNull(_columnIndexOfWeightGrams)) {
            _tmpWeightGrams = null
          } else {
            _tmpWeightGrams = _stmt.getDouble(_columnIndexOfWeightGrams)
          }
          val _tmpHeightCm: Double?
          if (_stmt.isNull(_columnIndexOfHeightCm)) {
            _tmpHeightCm = null
          } else {
            _tmpHeightCm = _stmt.getDouble(_columnIndexOfHeightCm)
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
          val _tmpBreed: String?
          if (_stmt.isNull(_columnIndexOfBreed)) {
            _tmpBreed = null
          } else {
            _tmpBreed = _stmt.getText(_columnIndexOfBreed)
          }
          val _tmpRaisingPurpose: String?
          if (_stmt.isNull(_columnIndexOfRaisingPurpose)) {
            _tmpRaisingPurpose = null
          } else {
            _tmpRaisingPurpose = _stmt.getText(_columnIndexOfRaisingPurpose)
          }
          val _tmpHealthStatus: String?
          if (_stmt.isNull(_columnIndexOfHealthStatus)) {
            _tmpHealthStatus = null
          } else {
            _tmpHealthStatus = _stmt.getText(_columnIndexOfHealthStatus)
          }
          val _tmpBirdCode: String?
          if (_stmt.isNull(_columnIndexOfBirdCode)) {
            _tmpBirdCode = null
          } else {
            _tmpBirdCode = _stmt.getText(_columnIndexOfBirdCode)
          }
          val _tmpColorTag: String?
          if (_stmt.isNull(_columnIndexOfColorTag)) {
            _tmpColorTag = null
          } else {
            _tmpColorTag = _stmt.getText(_columnIndexOfColorTag)
          }
          val _tmpFamilyTreeId: String?
          if (_stmt.isNull(_columnIndexOfFamilyTreeId)) {
            _tmpFamilyTreeId = null
          } else {
            _tmpFamilyTreeId = _stmt.getText(_columnIndexOfFamilyTreeId)
          }
          val _tmpSourceAssetId: String?
          if (_stmt.isNull(_columnIndexOfSourceAssetId)) {
            _tmpSourceAssetId = null
          } else {
            _tmpSourceAssetId = _stmt.getText(_columnIndexOfSourceAssetId)
          }
          val _tmpParentIdsJson: String?
          if (_stmt.isNull(_columnIndexOfParentIdsJson)) {
            _tmpParentIdsJson = null
          } else {
            _tmpParentIdsJson = _stmt.getText(_columnIndexOfParentIdsJson)
          }
          val _tmpBreedingStatus: String?
          if (_stmt.isNull(_columnIndexOfBreedingStatus)) {
            _tmpBreedingStatus = null
          } else {
            _tmpBreedingStatus = _stmt.getText(_columnIndexOfBreedingStatus)
          }
          val _tmpTransferHistoryJson: String?
          if (_stmt.isNull(_columnIndexOfTransferHistoryJson)) {
            _tmpTransferHistoryJson = null
          } else {
            _tmpTransferHistoryJson = _stmt.getText(_columnIndexOfTransferHistoryJson)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpLastModifiedAt: Long
          _tmpLastModifiedAt = _stmt.getLong(_columnIndexOfLastModifiedAt)
          val _tmpIsDeleted: Boolean
          val _tmp_2: Int
          _tmp_2 = _stmt.getLong(_columnIndexOfIsDeleted).toInt()
          _tmpIsDeleted = _tmp_2 != 0
          val _tmpDeletedAt: Long?
          if (_stmt.isNull(_columnIndexOfDeletedAt)) {
            _tmpDeletedAt = null
          } else {
            _tmpDeletedAt = _stmt.getLong(_columnIndexOfDeletedAt)
          }
          val _tmpDirty: Boolean
          val _tmp_3: Int
          _tmp_3 = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp_3 != 0
          val _tmpStage: LifecycleStage?
          val _tmp_4: String?
          if (_stmt.isNull(_columnIndexOfStage)) {
            _tmp_4 = null
          } else {
            _tmp_4 = _stmt.getText(_columnIndexOfStage)
          }
          _tmpStage = AppDatabase.Converters.toLifecycleStage(_tmp_4)
          val _tmpLifecycleStatus: String?
          if (_stmt.isNull(_columnIndexOfLifecycleStatus)) {
            _tmpLifecycleStatus = null
          } else {
            _tmpLifecycleStatus = _stmt.getText(_columnIndexOfLifecycleStatus)
          }
          val _tmpParentMaleId: String?
          if (_stmt.isNull(_columnIndexOfParentMaleId)) {
            _tmpParentMaleId = null
          } else {
            _tmpParentMaleId = _stmt.getText(_columnIndexOfParentMaleId)
          }
          val _tmpParentFemaleId: String?
          if (_stmt.isNull(_columnIndexOfParentFemaleId)) {
            _tmpParentFemaleId = null
          } else {
            _tmpParentFemaleId = _stmt.getText(_columnIndexOfParentFemaleId)
          }
          val _tmpAgeWeeks: Int?
          if (_stmt.isNull(_columnIndexOfAgeWeeks)) {
            _tmpAgeWeeks = null
          } else {
            _tmpAgeWeeks = _stmt.getLong(_columnIndexOfAgeWeeks).toInt()
          }
          val _tmpLastStageTransitionAt: Long?
          if (_stmt.isNull(_columnIndexOfLastStageTransitionAt)) {
            _tmpLastStageTransitionAt = null
          } else {
            _tmpLastStageTransitionAt = _stmt.getLong(_columnIndexOfLastStageTransitionAt)
          }
          val _tmpBreederEligibleAt: Long?
          if (_stmt.isNull(_columnIndexOfBreederEligibleAt)) {
            _tmpBreederEligibleAt = null
          } else {
            _tmpBreederEligibleAt = _stmt.getLong(_columnIndexOfBreederEligibleAt)
          }
          val _tmpIsBatch: Boolean?
          val _tmp_5: Int?
          if (_stmt.isNull(_columnIndexOfIsBatch)) {
            _tmp_5 = null
          } else {
            _tmp_5 = _stmt.getLong(_columnIndexOfIsBatch).toInt()
          }
          _tmpIsBatch = _tmp_5?.let { it != 0 }
          val _tmpBatchId: String?
          if (_stmt.isNull(_columnIndexOfBatchId)) {
            _tmpBatchId = null
          } else {
            _tmpBatchId = _stmt.getText(_columnIndexOfBatchId)
          }
          val _tmpSplitAt: Long?
          if (_stmt.isNull(_columnIndexOfSplitAt)) {
            _tmpSplitAt = null
          } else {
            _tmpSplitAt = _stmt.getLong(_columnIndexOfSplitAt)
          }
          val _tmpSplitIntoIds: String?
          if (_stmt.isNull(_columnIndexOfSplitIntoIds)) {
            _tmpSplitIntoIds = null
          } else {
            _tmpSplitIntoIds = _stmt.getText(_columnIndexOfSplitIntoIds)
          }
          val _tmpDocumentUrls: List<String>
          val _tmp_6: String?
          if (_stmt.isNull(_columnIndexOfDocumentUrls)) {
            _tmp_6 = null
          } else {
            _tmp_6 = _stmt.getText(_columnIndexOfDocumentUrls)
          }
          val _tmp_7: List<String>? = AppDatabase.Converters.toStringList(_tmp_6)
          if (_tmp_7 == null) {
            error("Expected NON-NULL 'kotlin.collections.List<kotlin.String>', but it was NULL.")
          } else {
            _tmpDocumentUrls = _tmp_7
          }
          val _tmpQrCodeUrl: String?
          if (_stmt.isNull(_columnIndexOfQrCodeUrl)) {
            _tmpQrCodeUrl = null
          } else {
            _tmpQrCodeUrl = _stmt.getText(_columnIndexOfQrCodeUrl)
          }
          val _tmpCustomStatus: String?
          if (_stmt.isNull(_columnIndexOfCustomStatus)) {
            _tmpCustomStatus = null
          } else {
            _tmpCustomStatus = _stmt.getText(_columnIndexOfCustomStatus)
          }
          val _tmpDebug: Boolean
          val _tmp_8: Int
          _tmp_8 = _stmt.getLong(_columnIndexOfDebug).toInt()
          _tmpDebug = _tmp_8 != 0
          val _tmpDeliveryOptions: List<String>
          val _tmp_9: String?
          if (_stmt.isNull(_columnIndexOfDeliveryOptions)) {
            _tmp_9 = null
          } else {
            _tmp_9 = _stmt.getText(_columnIndexOfDeliveryOptions)
          }
          val _tmp_10: List<String>? = AppDatabase.Converters.toStringList(_tmp_9)
          if (_tmp_10 == null) {
            error("Expected NON-NULL 'kotlin.collections.List<kotlin.String>', but it was NULL.")
          } else {
            _tmpDeliveryOptions = _tmp_10
          }
          val _tmpDeliveryCost: Double?
          if (_stmt.isNull(_columnIndexOfDeliveryCost)) {
            _tmpDeliveryCost = null
          } else {
            _tmpDeliveryCost = _stmt.getDouble(_columnIndexOfDeliveryCost)
          }
          val _tmpLeadTimeDays: Int?
          if (_stmt.isNull(_columnIndexOfLeadTimeDays)) {
            _tmpLeadTimeDays = null
          } else {
            _tmpLeadTimeDays = _stmt.getLong(_columnIndexOfLeadTimeDays).toInt()
          }
          val _tmpMotherId: String?
          if (_stmt.isNull(_columnIndexOfMotherId)) {
            _tmpMotherId = null
          } else {
            _tmpMotherId = _stmt.getText(_columnIndexOfMotherId)
          }
          val _tmpIsBreedingUnit: Boolean
          val _tmp_11: Int
          _tmp_11 = _stmt.getLong(_columnIndexOfIsBreedingUnit).toInt()
          _tmpIsBreedingUnit = _tmp_11 != 0
          val _tmpEggsCollectedToday: Int
          _tmpEggsCollectedToday = _stmt.getLong(_columnIndexOfEggsCollectedToday).toInt()
          val _tmpLastEggLogDate: Long?
          if (_stmt.isNull(_columnIndexOfLastEggLogDate)) {
            _tmpLastEggLogDate = null
          } else {
            _tmpLastEggLogDate = _stmt.getLong(_columnIndexOfLastEggLogDate)
          }
          val _tmpReadyForSale: Boolean
          val _tmp_12: Int
          _tmp_12 = _stmt.getLong(_columnIndexOfReadyForSale).toInt()
          _tmpReadyForSale = _tmp_12 != 0
          val _tmpTargetWeight: Double?
          if (_stmt.isNull(_columnIndexOfTargetWeight)) {
            _tmpTargetWeight = null
          } else {
            _tmpTargetWeight = _stmt.getDouble(_columnIndexOfTargetWeight)
          }
          val _tmpIsShowcased: Boolean
          val _tmp_13: Int
          _tmp_13 = _stmt.getLong(_columnIndexOfIsShowcased).toInt()
          _tmpIsShowcased = _tmp_13 != 0
          val _tmpExternalVideoUrl: String?
          if (_stmt.isNull(_columnIndexOfExternalVideoUrl)) {
            _tmpExternalVideoUrl = null
          } else {
            _tmpExternalVideoUrl = _stmt.getText(_columnIndexOfExternalVideoUrl)
          }
          val _tmpRecordsLockedAt: Long?
          if (_stmt.isNull(_columnIndexOfRecordsLockedAt)) {
            _tmpRecordsLockedAt = null
          } else {
            _tmpRecordsLockedAt = _stmt.getLong(_columnIndexOfRecordsLockedAt)
          }
          val _tmpAutoLockAfterDays: Int
          _tmpAutoLockAfterDays = _stmt.getLong(_columnIndexOfAutoLockAfterDays).toInt()
          val _tmpLineageHistoryJson: String?
          if (_stmt.isNull(_columnIndexOfLineageHistoryJson)) {
            _tmpLineageHistoryJson = null
          } else {
            _tmpLineageHistoryJson = _stmt.getText(_columnIndexOfLineageHistoryJson)
          }
          val _tmpEditCount: Int
          _tmpEditCount = _stmt.getLong(_columnIndexOfEditCount).toInt()
          val _tmpLastEditedBy: String?
          if (_stmt.isNull(_columnIndexOfLastEditedBy)) {
            _tmpLastEditedBy = null
          } else {
            _tmpLastEditedBy = _stmt.getText(_columnIndexOfLastEditedBy)
          }
          val _tmpAdminFlagged: Boolean
          val _tmp_14: Int
          _tmp_14 = _stmt.getLong(_columnIndexOfAdminFlagged).toInt()
          _tmpAdminFlagged = _tmp_14 != 0
          val _tmpModerationNote: String?
          if (_stmt.isNull(_columnIndexOfModerationNote)) {
            _tmpModerationNote = null
          } else {
            _tmpModerationNote = _stmt.getText(_columnIndexOfModerationNote)
          }
          val _tmpMetadataJson: String
          _tmpMetadataJson = _stmt.getText(_columnIndexOfMetadataJson)
          _item =
              ProductEntity(_tmpProductId,_tmpSellerId,_tmpName,_tmpDescription,_tmpCategory,_tmpPrice,_tmpQuantity,_tmpUnit,_tmpLocation,_tmpLatitude,_tmpLongitude,_tmpImageUrls,_tmpStatus,_tmpCondition,_tmpHarvestDate,_tmpExpiryDate,_tmpBirthDate,_tmpVaccinationRecordsJson,_tmpWeightGrams,_tmpHeightCm,_tmpGender,_tmpColor,_tmpBreed,_tmpRaisingPurpose,_tmpHealthStatus,_tmpBirdCode,_tmpColorTag,_tmpFamilyTreeId,_tmpSourceAssetId,_tmpParentIdsJson,_tmpBreedingStatus,_tmpTransferHistoryJson,_tmpCreatedAt,_tmpUpdatedAt,_tmpLastModifiedAt,_tmpIsDeleted,_tmpDeletedAt,_tmpDirty,_tmpStage,_tmpLifecycleStatus,_tmpParentMaleId,_tmpParentFemaleId,_tmpAgeWeeks,_tmpLastStageTransitionAt,_tmpBreederEligibleAt,_tmpIsBatch,_tmpBatchId,_tmpSplitAt,_tmpSplitIntoIds,_tmpDocumentUrls,_tmpQrCodeUrl,_tmpCustomStatus,_tmpDebug,_tmpDeliveryOptions,_tmpDeliveryCost,_tmpLeadTimeDays,_tmpMotherId,_tmpIsBreedingUnit,_tmpEggsCollectedToday,_tmpLastEggLogDate,_tmpReadyForSale,_tmpTargetWeight,_tmpIsShowcased,_tmpExternalVideoUrl,_tmpRecordsLockedAt,_tmpAutoLockAfterDays,_tmpLineageHistoryJson,_tmpEditCount,_tmpLastEditedBy,_tmpAdminFlagged,_tmpModerationNote,_tmpMetadataJson)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun countActiveByOwnerId(ownerId: String): Int {
    val _sql: String =
        "SELECT COUNT(*) FROM products WHERE sellerId = ? AND isDeleted = 0 AND (lifecycleStatus = 'ACTIVE' OR lifecycleStatus IS NULL)"
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

  public override fun observeActiveCountByOwnerId(ownerId: String): Flow<Int> {
    val _sql: String =
        "SELECT COUNT(*) FROM products WHERE sellerId = ? AND isDeleted = 0 AND (lifecycleStatus = 'ACTIVE' OR lifecycleStatus IS NULL)"
    return createFlow(__db, false, arrayOf("products")) { _connection ->
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

  public override fun observeByStage(farmerId: String, stage: LifecycleStage):
      Flow<List<ProductEntity>> {
    val _sql: String =
        "SELECT * FROM products WHERE sellerId = ? AND stage = ? AND (lifecycleStatus = 'ACTIVE' OR lifecycleStatus IS NULL)"
    return createFlow(__db, false, arrayOf("products")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, farmerId)
        _argIndex = 2
        val _tmp: String? = AppDatabase.Converters.fromLifecycleStage(stage)
        if (_tmp == null) {
          _stmt.bindNull(_argIndex)
        } else {
          _stmt.bindText(_argIndex, _tmp)
        }
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfSellerId: Int = getColumnIndexOrThrow(_stmt, "sellerId")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfCategory: Int = getColumnIndexOrThrow(_stmt, "category")
        val _columnIndexOfPrice: Int = getColumnIndexOrThrow(_stmt, "price")
        val _columnIndexOfQuantity: Int = getColumnIndexOrThrow(_stmt, "quantity")
        val _columnIndexOfUnit: Int = getColumnIndexOrThrow(_stmt, "unit")
        val _columnIndexOfLocation: Int = getColumnIndexOrThrow(_stmt, "location")
        val _columnIndexOfLatitude: Int = getColumnIndexOrThrow(_stmt, "latitude")
        val _columnIndexOfLongitude: Int = getColumnIndexOrThrow(_stmt, "longitude")
        val _columnIndexOfImageUrls: Int = getColumnIndexOrThrow(_stmt, "imageUrls")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfCondition: Int = getColumnIndexOrThrow(_stmt, "condition")
        val _columnIndexOfHarvestDate: Int = getColumnIndexOrThrow(_stmt, "harvestDate")
        val _columnIndexOfExpiryDate: Int = getColumnIndexOrThrow(_stmt, "expiryDate")
        val _columnIndexOfBirthDate: Int = getColumnIndexOrThrow(_stmt, "birthDate")
        val _columnIndexOfVaccinationRecordsJson: Int = getColumnIndexOrThrow(_stmt,
            "vaccinationRecordsJson")
        val _columnIndexOfWeightGrams: Int = getColumnIndexOrThrow(_stmt, "weightGrams")
        val _columnIndexOfHeightCm: Int = getColumnIndexOrThrow(_stmt, "heightCm")
        val _columnIndexOfGender: Int = getColumnIndexOrThrow(_stmt, "gender")
        val _columnIndexOfColor: Int = getColumnIndexOrThrow(_stmt, "color")
        val _columnIndexOfBreed: Int = getColumnIndexOrThrow(_stmt, "breed")
        val _columnIndexOfRaisingPurpose: Int = getColumnIndexOrThrow(_stmt, "raisingPurpose")
        val _columnIndexOfHealthStatus: Int = getColumnIndexOrThrow(_stmt, "healthStatus")
        val _columnIndexOfBirdCode: Int = getColumnIndexOrThrow(_stmt, "birdCode")
        val _columnIndexOfColorTag: Int = getColumnIndexOrThrow(_stmt, "colorTag")
        val _columnIndexOfFamilyTreeId: Int = getColumnIndexOrThrow(_stmt, "familyTreeId")
        val _columnIndexOfSourceAssetId: Int = getColumnIndexOrThrow(_stmt, "sourceAssetId")
        val _columnIndexOfParentIdsJson: Int = getColumnIndexOrThrow(_stmt, "parentIdsJson")
        val _columnIndexOfBreedingStatus: Int = getColumnIndexOrThrow(_stmt, "breedingStatus")
        val _columnIndexOfTransferHistoryJson: Int = getColumnIndexOrThrow(_stmt,
            "transferHistoryJson")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfLastModifiedAt: Int = getColumnIndexOrThrow(_stmt, "lastModifiedAt")
        val _columnIndexOfIsDeleted: Int = getColumnIndexOrThrow(_stmt, "isDeleted")
        val _columnIndexOfDeletedAt: Int = getColumnIndexOrThrow(_stmt, "deletedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfStage: Int = getColumnIndexOrThrow(_stmt, "stage")
        val _columnIndexOfLifecycleStatus: Int = getColumnIndexOrThrow(_stmt, "lifecycleStatus")
        val _columnIndexOfParentMaleId: Int = getColumnIndexOrThrow(_stmt, "parentMaleId")
        val _columnIndexOfParentFemaleId: Int = getColumnIndexOrThrow(_stmt, "parentFemaleId")
        val _columnIndexOfAgeWeeks: Int = getColumnIndexOrThrow(_stmt, "ageWeeks")
        val _columnIndexOfLastStageTransitionAt: Int = getColumnIndexOrThrow(_stmt,
            "lastStageTransitionAt")
        val _columnIndexOfBreederEligibleAt: Int = getColumnIndexOrThrow(_stmt, "breederEligibleAt")
        val _columnIndexOfIsBatch: Int = getColumnIndexOrThrow(_stmt, "isBatch")
        val _columnIndexOfBatchId: Int = getColumnIndexOrThrow(_stmt, "batchId")
        val _columnIndexOfSplitAt: Int = getColumnIndexOrThrow(_stmt, "splitAt")
        val _columnIndexOfSplitIntoIds: Int = getColumnIndexOrThrow(_stmt, "splitIntoIds")
        val _columnIndexOfDocumentUrls: Int = getColumnIndexOrThrow(_stmt, "documentUrls")
        val _columnIndexOfQrCodeUrl: Int = getColumnIndexOrThrow(_stmt, "qrCodeUrl")
        val _columnIndexOfCustomStatus: Int = getColumnIndexOrThrow(_stmt, "customStatus")
        val _columnIndexOfDebug: Int = getColumnIndexOrThrow(_stmt, "debug")
        val _columnIndexOfDeliveryOptions: Int = getColumnIndexOrThrow(_stmt, "deliveryOptions")
        val _columnIndexOfDeliveryCost: Int = getColumnIndexOrThrow(_stmt, "deliveryCost")
        val _columnIndexOfLeadTimeDays: Int = getColumnIndexOrThrow(_stmt, "leadTimeDays")
        val _columnIndexOfMotherId: Int = getColumnIndexOrThrow(_stmt, "motherId")
        val _columnIndexOfIsBreedingUnit: Int = getColumnIndexOrThrow(_stmt, "isBreedingUnit")
        val _columnIndexOfEggsCollectedToday: Int = getColumnIndexOrThrow(_stmt,
            "eggsCollectedToday")
        val _columnIndexOfLastEggLogDate: Int = getColumnIndexOrThrow(_stmt, "lastEggLogDate")
        val _columnIndexOfReadyForSale: Int = getColumnIndexOrThrow(_stmt, "readyForSale")
        val _columnIndexOfTargetWeight: Int = getColumnIndexOrThrow(_stmt, "targetWeight")
        val _columnIndexOfIsShowcased: Int = getColumnIndexOrThrow(_stmt, "isShowcased")
        val _columnIndexOfExternalVideoUrl: Int = getColumnIndexOrThrow(_stmt, "externalVideoUrl")
        val _columnIndexOfRecordsLockedAt: Int = getColumnIndexOrThrow(_stmt, "recordsLockedAt")
        val _columnIndexOfAutoLockAfterDays: Int = getColumnIndexOrThrow(_stmt, "autoLockAfterDays")
        val _columnIndexOfLineageHistoryJson: Int = getColumnIndexOrThrow(_stmt,
            "lineageHistoryJson")
        val _columnIndexOfEditCount: Int = getColumnIndexOrThrow(_stmt, "editCount")
        val _columnIndexOfLastEditedBy: Int = getColumnIndexOrThrow(_stmt, "lastEditedBy")
        val _columnIndexOfAdminFlagged: Int = getColumnIndexOrThrow(_stmt, "adminFlagged")
        val _columnIndexOfModerationNote: Int = getColumnIndexOrThrow(_stmt, "moderationNote")
        val _columnIndexOfMetadataJson: Int = getColumnIndexOrThrow(_stmt, "metadataJson")
        val _result: MutableList<ProductEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: ProductEntity
          val _tmpProductId: String
          _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          val _tmpSellerId: String
          _tmpSellerId = _stmt.getText(_columnIndexOfSellerId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpDescription: String
          _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          val _tmpCategory: String
          _tmpCategory = _stmt.getText(_columnIndexOfCategory)
          val _tmpPrice: Double
          _tmpPrice = _stmt.getDouble(_columnIndexOfPrice)
          val _tmpQuantity: Double
          _tmpQuantity = _stmt.getDouble(_columnIndexOfQuantity)
          val _tmpUnit: String
          _tmpUnit = _stmt.getText(_columnIndexOfUnit)
          val _tmpLocation: String
          _tmpLocation = _stmt.getText(_columnIndexOfLocation)
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
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpCondition: String?
          if (_stmt.isNull(_columnIndexOfCondition)) {
            _tmpCondition = null
          } else {
            _tmpCondition = _stmt.getText(_columnIndexOfCondition)
          }
          val _tmpHarvestDate: Long?
          if (_stmt.isNull(_columnIndexOfHarvestDate)) {
            _tmpHarvestDate = null
          } else {
            _tmpHarvestDate = _stmt.getLong(_columnIndexOfHarvestDate)
          }
          val _tmpExpiryDate: Long?
          if (_stmt.isNull(_columnIndexOfExpiryDate)) {
            _tmpExpiryDate = null
          } else {
            _tmpExpiryDate = _stmt.getLong(_columnIndexOfExpiryDate)
          }
          val _tmpBirthDate: Long?
          if (_stmt.isNull(_columnIndexOfBirthDate)) {
            _tmpBirthDate = null
          } else {
            _tmpBirthDate = _stmt.getLong(_columnIndexOfBirthDate)
          }
          val _tmpVaccinationRecordsJson: String?
          if (_stmt.isNull(_columnIndexOfVaccinationRecordsJson)) {
            _tmpVaccinationRecordsJson = null
          } else {
            _tmpVaccinationRecordsJson = _stmt.getText(_columnIndexOfVaccinationRecordsJson)
          }
          val _tmpWeightGrams: Double?
          if (_stmt.isNull(_columnIndexOfWeightGrams)) {
            _tmpWeightGrams = null
          } else {
            _tmpWeightGrams = _stmt.getDouble(_columnIndexOfWeightGrams)
          }
          val _tmpHeightCm: Double?
          if (_stmt.isNull(_columnIndexOfHeightCm)) {
            _tmpHeightCm = null
          } else {
            _tmpHeightCm = _stmt.getDouble(_columnIndexOfHeightCm)
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
          val _tmpBreed: String?
          if (_stmt.isNull(_columnIndexOfBreed)) {
            _tmpBreed = null
          } else {
            _tmpBreed = _stmt.getText(_columnIndexOfBreed)
          }
          val _tmpRaisingPurpose: String?
          if (_stmt.isNull(_columnIndexOfRaisingPurpose)) {
            _tmpRaisingPurpose = null
          } else {
            _tmpRaisingPurpose = _stmt.getText(_columnIndexOfRaisingPurpose)
          }
          val _tmpHealthStatus: String?
          if (_stmt.isNull(_columnIndexOfHealthStatus)) {
            _tmpHealthStatus = null
          } else {
            _tmpHealthStatus = _stmt.getText(_columnIndexOfHealthStatus)
          }
          val _tmpBirdCode: String?
          if (_stmt.isNull(_columnIndexOfBirdCode)) {
            _tmpBirdCode = null
          } else {
            _tmpBirdCode = _stmt.getText(_columnIndexOfBirdCode)
          }
          val _tmpColorTag: String?
          if (_stmt.isNull(_columnIndexOfColorTag)) {
            _tmpColorTag = null
          } else {
            _tmpColorTag = _stmt.getText(_columnIndexOfColorTag)
          }
          val _tmpFamilyTreeId: String?
          if (_stmt.isNull(_columnIndexOfFamilyTreeId)) {
            _tmpFamilyTreeId = null
          } else {
            _tmpFamilyTreeId = _stmt.getText(_columnIndexOfFamilyTreeId)
          }
          val _tmpSourceAssetId: String?
          if (_stmt.isNull(_columnIndexOfSourceAssetId)) {
            _tmpSourceAssetId = null
          } else {
            _tmpSourceAssetId = _stmt.getText(_columnIndexOfSourceAssetId)
          }
          val _tmpParentIdsJson: String?
          if (_stmt.isNull(_columnIndexOfParentIdsJson)) {
            _tmpParentIdsJson = null
          } else {
            _tmpParentIdsJson = _stmt.getText(_columnIndexOfParentIdsJson)
          }
          val _tmpBreedingStatus: String?
          if (_stmt.isNull(_columnIndexOfBreedingStatus)) {
            _tmpBreedingStatus = null
          } else {
            _tmpBreedingStatus = _stmt.getText(_columnIndexOfBreedingStatus)
          }
          val _tmpTransferHistoryJson: String?
          if (_stmt.isNull(_columnIndexOfTransferHistoryJson)) {
            _tmpTransferHistoryJson = null
          } else {
            _tmpTransferHistoryJson = _stmt.getText(_columnIndexOfTransferHistoryJson)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpLastModifiedAt: Long
          _tmpLastModifiedAt = _stmt.getLong(_columnIndexOfLastModifiedAt)
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
          val _tmpStage: LifecycleStage?
          val _tmp_5: String?
          if (_stmt.isNull(_columnIndexOfStage)) {
            _tmp_5 = null
          } else {
            _tmp_5 = _stmt.getText(_columnIndexOfStage)
          }
          _tmpStage = AppDatabase.Converters.toLifecycleStage(_tmp_5)
          val _tmpLifecycleStatus: String?
          if (_stmt.isNull(_columnIndexOfLifecycleStatus)) {
            _tmpLifecycleStatus = null
          } else {
            _tmpLifecycleStatus = _stmt.getText(_columnIndexOfLifecycleStatus)
          }
          val _tmpParentMaleId: String?
          if (_stmt.isNull(_columnIndexOfParentMaleId)) {
            _tmpParentMaleId = null
          } else {
            _tmpParentMaleId = _stmt.getText(_columnIndexOfParentMaleId)
          }
          val _tmpParentFemaleId: String?
          if (_stmt.isNull(_columnIndexOfParentFemaleId)) {
            _tmpParentFemaleId = null
          } else {
            _tmpParentFemaleId = _stmt.getText(_columnIndexOfParentFemaleId)
          }
          val _tmpAgeWeeks: Int?
          if (_stmt.isNull(_columnIndexOfAgeWeeks)) {
            _tmpAgeWeeks = null
          } else {
            _tmpAgeWeeks = _stmt.getLong(_columnIndexOfAgeWeeks).toInt()
          }
          val _tmpLastStageTransitionAt: Long?
          if (_stmt.isNull(_columnIndexOfLastStageTransitionAt)) {
            _tmpLastStageTransitionAt = null
          } else {
            _tmpLastStageTransitionAt = _stmt.getLong(_columnIndexOfLastStageTransitionAt)
          }
          val _tmpBreederEligibleAt: Long?
          if (_stmt.isNull(_columnIndexOfBreederEligibleAt)) {
            _tmpBreederEligibleAt = null
          } else {
            _tmpBreederEligibleAt = _stmt.getLong(_columnIndexOfBreederEligibleAt)
          }
          val _tmpIsBatch: Boolean?
          val _tmp_6: Int?
          if (_stmt.isNull(_columnIndexOfIsBatch)) {
            _tmp_6 = null
          } else {
            _tmp_6 = _stmt.getLong(_columnIndexOfIsBatch).toInt()
          }
          _tmpIsBatch = _tmp_6?.let { it != 0 }
          val _tmpBatchId: String?
          if (_stmt.isNull(_columnIndexOfBatchId)) {
            _tmpBatchId = null
          } else {
            _tmpBatchId = _stmt.getText(_columnIndexOfBatchId)
          }
          val _tmpSplitAt: Long?
          if (_stmt.isNull(_columnIndexOfSplitAt)) {
            _tmpSplitAt = null
          } else {
            _tmpSplitAt = _stmt.getLong(_columnIndexOfSplitAt)
          }
          val _tmpSplitIntoIds: String?
          if (_stmt.isNull(_columnIndexOfSplitIntoIds)) {
            _tmpSplitIntoIds = null
          } else {
            _tmpSplitIntoIds = _stmt.getText(_columnIndexOfSplitIntoIds)
          }
          val _tmpDocumentUrls: List<String>
          val _tmp_7: String?
          if (_stmt.isNull(_columnIndexOfDocumentUrls)) {
            _tmp_7 = null
          } else {
            _tmp_7 = _stmt.getText(_columnIndexOfDocumentUrls)
          }
          val _tmp_8: List<String>? = AppDatabase.Converters.toStringList(_tmp_7)
          if (_tmp_8 == null) {
            error("Expected NON-NULL 'kotlin.collections.List<kotlin.String>', but it was NULL.")
          } else {
            _tmpDocumentUrls = _tmp_8
          }
          val _tmpQrCodeUrl: String?
          if (_stmt.isNull(_columnIndexOfQrCodeUrl)) {
            _tmpQrCodeUrl = null
          } else {
            _tmpQrCodeUrl = _stmt.getText(_columnIndexOfQrCodeUrl)
          }
          val _tmpCustomStatus: String?
          if (_stmt.isNull(_columnIndexOfCustomStatus)) {
            _tmpCustomStatus = null
          } else {
            _tmpCustomStatus = _stmt.getText(_columnIndexOfCustomStatus)
          }
          val _tmpDebug: Boolean
          val _tmp_9: Int
          _tmp_9 = _stmt.getLong(_columnIndexOfDebug).toInt()
          _tmpDebug = _tmp_9 != 0
          val _tmpDeliveryOptions: List<String>
          val _tmp_10: String?
          if (_stmt.isNull(_columnIndexOfDeliveryOptions)) {
            _tmp_10 = null
          } else {
            _tmp_10 = _stmt.getText(_columnIndexOfDeliveryOptions)
          }
          val _tmp_11: List<String>? = AppDatabase.Converters.toStringList(_tmp_10)
          if (_tmp_11 == null) {
            error("Expected NON-NULL 'kotlin.collections.List<kotlin.String>', but it was NULL.")
          } else {
            _tmpDeliveryOptions = _tmp_11
          }
          val _tmpDeliveryCost: Double?
          if (_stmt.isNull(_columnIndexOfDeliveryCost)) {
            _tmpDeliveryCost = null
          } else {
            _tmpDeliveryCost = _stmt.getDouble(_columnIndexOfDeliveryCost)
          }
          val _tmpLeadTimeDays: Int?
          if (_stmt.isNull(_columnIndexOfLeadTimeDays)) {
            _tmpLeadTimeDays = null
          } else {
            _tmpLeadTimeDays = _stmt.getLong(_columnIndexOfLeadTimeDays).toInt()
          }
          val _tmpMotherId: String?
          if (_stmt.isNull(_columnIndexOfMotherId)) {
            _tmpMotherId = null
          } else {
            _tmpMotherId = _stmt.getText(_columnIndexOfMotherId)
          }
          val _tmpIsBreedingUnit: Boolean
          val _tmp_12: Int
          _tmp_12 = _stmt.getLong(_columnIndexOfIsBreedingUnit).toInt()
          _tmpIsBreedingUnit = _tmp_12 != 0
          val _tmpEggsCollectedToday: Int
          _tmpEggsCollectedToday = _stmt.getLong(_columnIndexOfEggsCollectedToday).toInt()
          val _tmpLastEggLogDate: Long?
          if (_stmt.isNull(_columnIndexOfLastEggLogDate)) {
            _tmpLastEggLogDate = null
          } else {
            _tmpLastEggLogDate = _stmt.getLong(_columnIndexOfLastEggLogDate)
          }
          val _tmpReadyForSale: Boolean
          val _tmp_13: Int
          _tmp_13 = _stmt.getLong(_columnIndexOfReadyForSale).toInt()
          _tmpReadyForSale = _tmp_13 != 0
          val _tmpTargetWeight: Double?
          if (_stmt.isNull(_columnIndexOfTargetWeight)) {
            _tmpTargetWeight = null
          } else {
            _tmpTargetWeight = _stmt.getDouble(_columnIndexOfTargetWeight)
          }
          val _tmpIsShowcased: Boolean
          val _tmp_14: Int
          _tmp_14 = _stmt.getLong(_columnIndexOfIsShowcased).toInt()
          _tmpIsShowcased = _tmp_14 != 0
          val _tmpExternalVideoUrl: String?
          if (_stmt.isNull(_columnIndexOfExternalVideoUrl)) {
            _tmpExternalVideoUrl = null
          } else {
            _tmpExternalVideoUrl = _stmt.getText(_columnIndexOfExternalVideoUrl)
          }
          val _tmpRecordsLockedAt: Long?
          if (_stmt.isNull(_columnIndexOfRecordsLockedAt)) {
            _tmpRecordsLockedAt = null
          } else {
            _tmpRecordsLockedAt = _stmt.getLong(_columnIndexOfRecordsLockedAt)
          }
          val _tmpAutoLockAfterDays: Int
          _tmpAutoLockAfterDays = _stmt.getLong(_columnIndexOfAutoLockAfterDays).toInt()
          val _tmpLineageHistoryJson: String?
          if (_stmt.isNull(_columnIndexOfLineageHistoryJson)) {
            _tmpLineageHistoryJson = null
          } else {
            _tmpLineageHistoryJson = _stmt.getText(_columnIndexOfLineageHistoryJson)
          }
          val _tmpEditCount: Int
          _tmpEditCount = _stmt.getLong(_columnIndexOfEditCount).toInt()
          val _tmpLastEditedBy: String?
          if (_stmt.isNull(_columnIndexOfLastEditedBy)) {
            _tmpLastEditedBy = null
          } else {
            _tmpLastEditedBy = _stmt.getText(_columnIndexOfLastEditedBy)
          }
          val _tmpAdminFlagged: Boolean
          val _tmp_15: Int
          _tmp_15 = _stmt.getLong(_columnIndexOfAdminFlagged).toInt()
          _tmpAdminFlagged = _tmp_15 != 0
          val _tmpModerationNote: String?
          if (_stmt.isNull(_columnIndexOfModerationNote)) {
            _tmpModerationNote = null
          } else {
            _tmpModerationNote = _stmt.getText(_columnIndexOfModerationNote)
          }
          val _tmpMetadataJson: String
          _tmpMetadataJson = _stmt.getText(_columnIndexOfMetadataJson)
          _item =
              ProductEntity(_tmpProductId,_tmpSellerId,_tmpName,_tmpDescription,_tmpCategory,_tmpPrice,_tmpQuantity,_tmpUnit,_tmpLocation,_tmpLatitude,_tmpLongitude,_tmpImageUrls,_tmpStatus,_tmpCondition,_tmpHarvestDate,_tmpExpiryDate,_tmpBirthDate,_tmpVaccinationRecordsJson,_tmpWeightGrams,_tmpHeightCm,_tmpGender,_tmpColor,_tmpBreed,_tmpRaisingPurpose,_tmpHealthStatus,_tmpBirdCode,_tmpColorTag,_tmpFamilyTreeId,_tmpSourceAssetId,_tmpParentIdsJson,_tmpBreedingStatus,_tmpTransferHistoryJson,_tmpCreatedAt,_tmpUpdatedAt,_tmpLastModifiedAt,_tmpIsDeleted,_tmpDeletedAt,_tmpDirty,_tmpStage,_tmpLifecycleStatus,_tmpParentMaleId,_tmpParentFemaleId,_tmpAgeWeeks,_tmpLastStageTransitionAt,_tmpBreederEligibleAt,_tmpIsBatch,_tmpBatchId,_tmpSplitAt,_tmpSplitIntoIds,_tmpDocumentUrls,_tmpQrCodeUrl,_tmpCustomStatus,_tmpDebug,_tmpDeliveryOptions,_tmpDeliveryCost,_tmpLeadTimeDays,_tmpMotherId,_tmpIsBreedingUnit,_tmpEggsCollectedToday,_tmpLastEggLogDate,_tmpReadyForSale,_tmpTargetWeight,_tmpIsShowcased,_tmpExternalVideoUrl,_tmpRecordsLockedAt,_tmpAutoLockAfterDays,_tmpLineageHistoryJson,_tmpEditCount,_tmpLastEditedBy,_tmpAdminFlagged,_tmpModerationNote,_tmpMetadataJson)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeByLifecycleStatus(farmerId: String, status: String):
      Flow<List<ProductEntity>> {
    val _sql: String = "SELECT * FROM products WHERE sellerId = ? AND lifecycleStatus = ?"
    return createFlow(__db, false, arrayOf("products")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, farmerId)
        _argIndex = 2
        _stmt.bindText(_argIndex, status)
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfSellerId: Int = getColumnIndexOrThrow(_stmt, "sellerId")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfCategory: Int = getColumnIndexOrThrow(_stmt, "category")
        val _columnIndexOfPrice: Int = getColumnIndexOrThrow(_stmt, "price")
        val _columnIndexOfQuantity: Int = getColumnIndexOrThrow(_stmt, "quantity")
        val _columnIndexOfUnit: Int = getColumnIndexOrThrow(_stmt, "unit")
        val _columnIndexOfLocation: Int = getColumnIndexOrThrow(_stmt, "location")
        val _columnIndexOfLatitude: Int = getColumnIndexOrThrow(_stmt, "latitude")
        val _columnIndexOfLongitude: Int = getColumnIndexOrThrow(_stmt, "longitude")
        val _columnIndexOfImageUrls: Int = getColumnIndexOrThrow(_stmt, "imageUrls")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfCondition: Int = getColumnIndexOrThrow(_stmt, "condition")
        val _columnIndexOfHarvestDate: Int = getColumnIndexOrThrow(_stmt, "harvestDate")
        val _columnIndexOfExpiryDate: Int = getColumnIndexOrThrow(_stmt, "expiryDate")
        val _columnIndexOfBirthDate: Int = getColumnIndexOrThrow(_stmt, "birthDate")
        val _columnIndexOfVaccinationRecordsJson: Int = getColumnIndexOrThrow(_stmt,
            "vaccinationRecordsJson")
        val _columnIndexOfWeightGrams: Int = getColumnIndexOrThrow(_stmt, "weightGrams")
        val _columnIndexOfHeightCm: Int = getColumnIndexOrThrow(_stmt, "heightCm")
        val _columnIndexOfGender: Int = getColumnIndexOrThrow(_stmt, "gender")
        val _columnIndexOfColor: Int = getColumnIndexOrThrow(_stmt, "color")
        val _columnIndexOfBreed: Int = getColumnIndexOrThrow(_stmt, "breed")
        val _columnIndexOfRaisingPurpose: Int = getColumnIndexOrThrow(_stmt, "raisingPurpose")
        val _columnIndexOfHealthStatus: Int = getColumnIndexOrThrow(_stmt, "healthStatus")
        val _columnIndexOfBirdCode: Int = getColumnIndexOrThrow(_stmt, "birdCode")
        val _columnIndexOfColorTag: Int = getColumnIndexOrThrow(_stmt, "colorTag")
        val _columnIndexOfFamilyTreeId: Int = getColumnIndexOrThrow(_stmt, "familyTreeId")
        val _columnIndexOfSourceAssetId: Int = getColumnIndexOrThrow(_stmt, "sourceAssetId")
        val _columnIndexOfParentIdsJson: Int = getColumnIndexOrThrow(_stmt, "parentIdsJson")
        val _columnIndexOfBreedingStatus: Int = getColumnIndexOrThrow(_stmt, "breedingStatus")
        val _columnIndexOfTransferHistoryJson: Int = getColumnIndexOrThrow(_stmt,
            "transferHistoryJson")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfLastModifiedAt: Int = getColumnIndexOrThrow(_stmt, "lastModifiedAt")
        val _columnIndexOfIsDeleted: Int = getColumnIndexOrThrow(_stmt, "isDeleted")
        val _columnIndexOfDeletedAt: Int = getColumnIndexOrThrow(_stmt, "deletedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfStage: Int = getColumnIndexOrThrow(_stmt, "stage")
        val _columnIndexOfLifecycleStatus: Int = getColumnIndexOrThrow(_stmt, "lifecycleStatus")
        val _columnIndexOfParentMaleId: Int = getColumnIndexOrThrow(_stmt, "parentMaleId")
        val _columnIndexOfParentFemaleId: Int = getColumnIndexOrThrow(_stmt, "parentFemaleId")
        val _columnIndexOfAgeWeeks: Int = getColumnIndexOrThrow(_stmt, "ageWeeks")
        val _columnIndexOfLastStageTransitionAt: Int = getColumnIndexOrThrow(_stmt,
            "lastStageTransitionAt")
        val _columnIndexOfBreederEligibleAt: Int = getColumnIndexOrThrow(_stmt, "breederEligibleAt")
        val _columnIndexOfIsBatch: Int = getColumnIndexOrThrow(_stmt, "isBatch")
        val _columnIndexOfBatchId: Int = getColumnIndexOrThrow(_stmt, "batchId")
        val _columnIndexOfSplitAt: Int = getColumnIndexOrThrow(_stmt, "splitAt")
        val _columnIndexOfSplitIntoIds: Int = getColumnIndexOrThrow(_stmt, "splitIntoIds")
        val _columnIndexOfDocumentUrls: Int = getColumnIndexOrThrow(_stmt, "documentUrls")
        val _columnIndexOfQrCodeUrl: Int = getColumnIndexOrThrow(_stmt, "qrCodeUrl")
        val _columnIndexOfCustomStatus: Int = getColumnIndexOrThrow(_stmt, "customStatus")
        val _columnIndexOfDebug: Int = getColumnIndexOrThrow(_stmt, "debug")
        val _columnIndexOfDeliveryOptions: Int = getColumnIndexOrThrow(_stmt, "deliveryOptions")
        val _columnIndexOfDeliveryCost: Int = getColumnIndexOrThrow(_stmt, "deliveryCost")
        val _columnIndexOfLeadTimeDays: Int = getColumnIndexOrThrow(_stmt, "leadTimeDays")
        val _columnIndexOfMotherId: Int = getColumnIndexOrThrow(_stmt, "motherId")
        val _columnIndexOfIsBreedingUnit: Int = getColumnIndexOrThrow(_stmt, "isBreedingUnit")
        val _columnIndexOfEggsCollectedToday: Int = getColumnIndexOrThrow(_stmt,
            "eggsCollectedToday")
        val _columnIndexOfLastEggLogDate: Int = getColumnIndexOrThrow(_stmt, "lastEggLogDate")
        val _columnIndexOfReadyForSale: Int = getColumnIndexOrThrow(_stmt, "readyForSale")
        val _columnIndexOfTargetWeight: Int = getColumnIndexOrThrow(_stmt, "targetWeight")
        val _columnIndexOfIsShowcased: Int = getColumnIndexOrThrow(_stmt, "isShowcased")
        val _columnIndexOfExternalVideoUrl: Int = getColumnIndexOrThrow(_stmt, "externalVideoUrl")
        val _columnIndexOfRecordsLockedAt: Int = getColumnIndexOrThrow(_stmt, "recordsLockedAt")
        val _columnIndexOfAutoLockAfterDays: Int = getColumnIndexOrThrow(_stmt, "autoLockAfterDays")
        val _columnIndexOfLineageHistoryJson: Int = getColumnIndexOrThrow(_stmt,
            "lineageHistoryJson")
        val _columnIndexOfEditCount: Int = getColumnIndexOrThrow(_stmt, "editCount")
        val _columnIndexOfLastEditedBy: Int = getColumnIndexOrThrow(_stmt, "lastEditedBy")
        val _columnIndexOfAdminFlagged: Int = getColumnIndexOrThrow(_stmt, "adminFlagged")
        val _columnIndexOfModerationNote: Int = getColumnIndexOrThrow(_stmt, "moderationNote")
        val _columnIndexOfMetadataJson: Int = getColumnIndexOrThrow(_stmt, "metadataJson")
        val _result: MutableList<ProductEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: ProductEntity
          val _tmpProductId: String
          _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          val _tmpSellerId: String
          _tmpSellerId = _stmt.getText(_columnIndexOfSellerId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpDescription: String
          _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          val _tmpCategory: String
          _tmpCategory = _stmt.getText(_columnIndexOfCategory)
          val _tmpPrice: Double
          _tmpPrice = _stmt.getDouble(_columnIndexOfPrice)
          val _tmpQuantity: Double
          _tmpQuantity = _stmt.getDouble(_columnIndexOfQuantity)
          val _tmpUnit: String
          _tmpUnit = _stmt.getText(_columnIndexOfUnit)
          val _tmpLocation: String
          _tmpLocation = _stmt.getText(_columnIndexOfLocation)
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
          val _tmpImageUrls: List<String>
          val _tmp: String?
          if (_stmt.isNull(_columnIndexOfImageUrls)) {
            _tmp = null
          } else {
            _tmp = _stmt.getText(_columnIndexOfImageUrls)
          }
          val _tmp_1: List<String>? = AppDatabase.Converters.toStringList(_tmp)
          if (_tmp_1 == null) {
            error("Expected NON-NULL 'kotlin.collections.List<kotlin.String>', but it was NULL.")
          } else {
            _tmpImageUrls = _tmp_1
          }
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpCondition: String?
          if (_stmt.isNull(_columnIndexOfCondition)) {
            _tmpCondition = null
          } else {
            _tmpCondition = _stmt.getText(_columnIndexOfCondition)
          }
          val _tmpHarvestDate: Long?
          if (_stmt.isNull(_columnIndexOfHarvestDate)) {
            _tmpHarvestDate = null
          } else {
            _tmpHarvestDate = _stmt.getLong(_columnIndexOfHarvestDate)
          }
          val _tmpExpiryDate: Long?
          if (_stmt.isNull(_columnIndexOfExpiryDate)) {
            _tmpExpiryDate = null
          } else {
            _tmpExpiryDate = _stmt.getLong(_columnIndexOfExpiryDate)
          }
          val _tmpBirthDate: Long?
          if (_stmt.isNull(_columnIndexOfBirthDate)) {
            _tmpBirthDate = null
          } else {
            _tmpBirthDate = _stmt.getLong(_columnIndexOfBirthDate)
          }
          val _tmpVaccinationRecordsJson: String?
          if (_stmt.isNull(_columnIndexOfVaccinationRecordsJson)) {
            _tmpVaccinationRecordsJson = null
          } else {
            _tmpVaccinationRecordsJson = _stmt.getText(_columnIndexOfVaccinationRecordsJson)
          }
          val _tmpWeightGrams: Double?
          if (_stmt.isNull(_columnIndexOfWeightGrams)) {
            _tmpWeightGrams = null
          } else {
            _tmpWeightGrams = _stmt.getDouble(_columnIndexOfWeightGrams)
          }
          val _tmpHeightCm: Double?
          if (_stmt.isNull(_columnIndexOfHeightCm)) {
            _tmpHeightCm = null
          } else {
            _tmpHeightCm = _stmt.getDouble(_columnIndexOfHeightCm)
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
          val _tmpBreed: String?
          if (_stmt.isNull(_columnIndexOfBreed)) {
            _tmpBreed = null
          } else {
            _tmpBreed = _stmt.getText(_columnIndexOfBreed)
          }
          val _tmpRaisingPurpose: String?
          if (_stmt.isNull(_columnIndexOfRaisingPurpose)) {
            _tmpRaisingPurpose = null
          } else {
            _tmpRaisingPurpose = _stmt.getText(_columnIndexOfRaisingPurpose)
          }
          val _tmpHealthStatus: String?
          if (_stmt.isNull(_columnIndexOfHealthStatus)) {
            _tmpHealthStatus = null
          } else {
            _tmpHealthStatus = _stmt.getText(_columnIndexOfHealthStatus)
          }
          val _tmpBirdCode: String?
          if (_stmt.isNull(_columnIndexOfBirdCode)) {
            _tmpBirdCode = null
          } else {
            _tmpBirdCode = _stmt.getText(_columnIndexOfBirdCode)
          }
          val _tmpColorTag: String?
          if (_stmt.isNull(_columnIndexOfColorTag)) {
            _tmpColorTag = null
          } else {
            _tmpColorTag = _stmt.getText(_columnIndexOfColorTag)
          }
          val _tmpFamilyTreeId: String?
          if (_stmt.isNull(_columnIndexOfFamilyTreeId)) {
            _tmpFamilyTreeId = null
          } else {
            _tmpFamilyTreeId = _stmt.getText(_columnIndexOfFamilyTreeId)
          }
          val _tmpSourceAssetId: String?
          if (_stmt.isNull(_columnIndexOfSourceAssetId)) {
            _tmpSourceAssetId = null
          } else {
            _tmpSourceAssetId = _stmt.getText(_columnIndexOfSourceAssetId)
          }
          val _tmpParentIdsJson: String?
          if (_stmt.isNull(_columnIndexOfParentIdsJson)) {
            _tmpParentIdsJson = null
          } else {
            _tmpParentIdsJson = _stmt.getText(_columnIndexOfParentIdsJson)
          }
          val _tmpBreedingStatus: String?
          if (_stmt.isNull(_columnIndexOfBreedingStatus)) {
            _tmpBreedingStatus = null
          } else {
            _tmpBreedingStatus = _stmt.getText(_columnIndexOfBreedingStatus)
          }
          val _tmpTransferHistoryJson: String?
          if (_stmt.isNull(_columnIndexOfTransferHistoryJson)) {
            _tmpTransferHistoryJson = null
          } else {
            _tmpTransferHistoryJson = _stmt.getText(_columnIndexOfTransferHistoryJson)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpLastModifiedAt: Long
          _tmpLastModifiedAt = _stmt.getLong(_columnIndexOfLastModifiedAt)
          val _tmpIsDeleted: Boolean
          val _tmp_2: Int
          _tmp_2 = _stmt.getLong(_columnIndexOfIsDeleted).toInt()
          _tmpIsDeleted = _tmp_2 != 0
          val _tmpDeletedAt: Long?
          if (_stmt.isNull(_columnIndexOfDeletedAt)) {
            _tmpDeletedAt = null
          } else {
            _tmpDeletedAt = _stmt.getLong(_columnIndexOfDeletedAt)
          }
          val _tmpDirty: Boolean
          val _tmp_3: Int
          _tmp_3 = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp_3 != 0
          val _tmpStage: LifecycleStage?
          val _tmp_4: String?
          if (_stmt.isNull(_columnIndexOfStage)) {
            _tmp_4 = null
          } else {
            _tmp_4 = _stmt.getText(_columnIndexOfStage)
          }
          _tmpStage = AppDatabase.Converters.toLifecycleStage(_tmp_4)
          val _tmpLifecycleStatus: String?
          if (_stmt.isNull(_columnIndexOfLifecycleStatus)) {
            _tmpLifecycleStatus = null
          } else {
            _tmpLifecycleStatus = _stmt.getText(_columnIndexOfLifecycleStatus)
          }
          val _tmpParentMaleId: String?
          if (_stmt.isNull(_columnIndexOfParentMaleId)) {
            _tmpParentMaleId = null
          } else {
            _tmpParentMaleId = _stmt.getText(_columnIndexOfParentMaleId)
          }
          val _tmpParentFemaleId: String?
          if (_stmt.isNull(_columnIndexOfParentFemaleId)) {
            _tmpParentFemaleId = null
          } else {
            _tmpParentFemaleId = _stmt.getText(_columnIndexOfParentFemaleId)
          }
          val _tmpAgeWeeks: Int?
          if (_stmt.isNull(_columnIndexOfAgeWeeks)) {
            _tmpAgeWeeks = null
          } else {
            _tmpAgeWeeks = _stmt.getLong(_columnIndexOfAgeWeeks).toInt()
          }
          val _tmpLastStageTransitionAt: Long?
          if (_stmt.isNull(_columnIndexOfLastStageTransitionAt)) {
            _tmpLastStageTransitionAt = null
          } else {
            _tmpLastStageTransitionAt = _stmt.getLong(_columnIndexOfLastStageTransitionAt)
          }
          val _tmpBreederEligibleAt: Long?
          if (_stmt.isNull(_columnIndexOfBreederEligibleAt)) {
            _tmpBreederEligibleAt = null
          } else {
            _tmpBreederEligibleAt = _stmt.getLong(_columnIndexOfBreederEligibleAt)
          }
          val _tmpIsBatch: Boolean?
          val _tmp_5: Int?
          if (_stmt.isNull(_columnIndexOfIsBatch)) {
            _tmp_5 = null
          } else {
            _tmp_5 = _stmt.getLong(_columnIndexOfIsBatch).toInt()
          }
          _tmpIsBatch = _tmp_5?.let { it != 0 }
          val _tmpBatchId: String?
          if (_stmt.isNull(_columnIndexOfBatchId)) {
            _tmpBatchId = null
          } else {
            _tmpBatchId = _stmt.getText(_columnIndexOfBatchId)
          }
          val _tmpSplitAt: Long?
          if (_stmt.isNull(_columnIndexOfSplitAt)) {
            _tmpSplitAt = null
          } else {
            _tmpSplitAt = _stmt.getLong(_columnIndexOfSplitAt)
          }
          val _tmpSplitIntoIds: String?
          if (_stmt.isNull(_columnIndexOfSplitIntoIds)) {
            _tmpSplitIntoIds = null
          } else {
            _tmpSplitIntoIds = _stmt.getText(_columnIndexOfSplitIntoIds)
          }
          val _tmpDocumentUrls: List<String>
          val _tmp_6: String?
          if (_stmt.isNull(_columnIndexOfDocumentUrls)) {
            _tmp_6 = null
          } else {
            _tmp_6 = _stmt.getText(_columnIndexOfDocumentUrls)
          }
          val _tmp_7: List<String>? = AppDatabase.Converters.toStringList(_tmp_6)
          if (_tmp_7 == null) {
            error("Expected NON-NULL 'kotlin.collections.List<kotlin.String>', but it was NULL.")
          } else {
            _tmpDocumentUrls = _tmp_7
          }
          val _tmpQrCodeUrl: String?
          if (_stmt.isNull(_columnIndexOfQrCodeUrl)) {
            _tmpQrCodeUrl = null
          } else {
            _tmpQrCodeUrl = _stmt.getText(_columnIndexOfQrCodeUrl)
          }
          val _tmpCustomStatus: String?
          if (_stmt.isNull(_columnIndexOfCustomStatus)) {
            _tmpCustomStatus = null
          } else {
            _tmpCustomStatus = _stmt.getText(_columnIndexOfCustomStatus)
          }
          val _tmpDebug: Boolean
          val _tmp_8: Int
          _tmp_8 = _stmt.getLong(_columnIndexOfDebug).toInt()
          _tmpDebug = _tmp_8 != 0
          val _tmpDeliveryOptions: List<String>
          val _tmp_9: String?
          if (_stmt.isNull(_columnIndexOfDeliveryOptions)) {
            _tmp_9 = null
          } else {
            _tmp_9 = _stmt.getText(_columnIndexOfDeliveryOptions)
          }
          val _tmp_10: List<String>? = AppDatabase.Converters.toStringList(_tmp_9)
          if (_tmp_10 == null) {
            error("Expected NON-NULL 'kotlin.collections.List<kotlin.String>', but it was NULL.")
          } else {
            _tmpDeliveryOptions = _tmp_10
          }
          val _tmpDeliveryCost: Double?
          if (_stmt.isNull(_columnIndexOfDeliveryCost)) {
            _tmpDeliveryCost = null
          } else {
            _tmpDeliveryCost = _stmt.getDouble(_columnIndexOfDeliveryCost)
          }
          val _tmpLeadTimeDays: Int?
          if (_stmt.isNull(_columnIndexOfLeadTimeDays)) {
            _tmpLeadTimeDays = null
          } else {
            _tmpLeadTimeDays = _stmt.getLong(_columnIndexOfLeadTimeDays).toInt()
          }
          val _tmpMotherId: String?
          if (_stmt.isNull(_columnIndexOfMotherId)) {
            _tmpMotherId = null
          } else {
            _tmpMotherId = _stmt.getText(_columnIndexOfMotherId)
          }
          val _tmpIsBreedingUnit: Boolean
          val _tmp_11: Int
          _tmp_11 = _stmt.getLong(_columnIndexOfIsBreedingUnit).toInt()
          _tmpIsBreedingUnit = _tmp_11 != 0
          val _tmpEggsCollectedToday: Int
          _tmpEggsCollectedToday = _stmt.getLong(_columnIndexOfEggsCollectedToday).toInt()
          val _tmpLastEggLogDate: Long?
          if (_stmt.isNull(_columnIndexOfLastEggLogDate)) {
            _tmpLastEggLogDate = null
          } else {
            _tmpLastEggLogDate = _stmt.getLong(_columnIndexOfLastEggLogDate)
          }
          val _tmpReadyForSale: Boolean
          val _tmp_12: Int
          _tmp_12 = _stmt.getLong(_columnIndexOfReadyForSale).toInt()
          _tmpReadyForSale = _tmp_12 != 0
          val _tmpTargetWeight: Double?
          if (_stmt.isNull(_columnIndexOfTargetWeight)) {
            _tmpTargetWeight = null
          } else {
            _tmpTargetWeight = _stmt.getDouble(_columnIndexOfTargetWeight)
          }
          val _tmpIsShowcased: Boolean
          val _tmp_13: Int
          _tmp_13 = _stmt.getLong(_columnIndexOfIsShowcased).toInt()
          _tmpIsShowcased = _tmp_13 != 0
          val _tmpExternalVideoUrl: String?
          if (_stmt.isNull(_columnIndexOfExternalVideoUrl)) {
            _tmpExternalVideoUrl = null
          } else {
            _tmpExternalVideoUrl = _stmt.getText(_columnIndexOfExternalVideoUrl)
          }
          val _tmpRecordsLockedAt: Long?
          if (_stmt.isNull(_columnIndexOfRecordsLockedAt)) {
            _tmpRecordsLockedAt = null
          } else {
            _tmpRecordsLockedAt = _stmt.getLong(_columnIndexOfRecordsLockedAt)
          }
          val _tmpAutoLockAfterDays: Int
          _tmpAutoLockAfterDays = _stmt.getLong(_columnIndexOfAutoLockAfterDays).toInt()
          val _tmpLineageHistoryJson: String?
          if (_stmt.isNull(_columnIndexOfLineageHistoryJson)) {
            _tmpLineageHistoryJson = null
          } else {
            _tmpLineageHistoryJson = _stmt.getText(_columnIndexOfLineageHistoryJson)
          }
          val _tmpEditCount: Int
          _tmpEditCount = _stmt.getLong(_columnIndexOfEditCount).toInt()
          val _tmpLastEditedBy: String?
          if (_stmt.isNull(_columnIndexOfLastEditedBy)) {
            _tmpLastEditedBy = null
          } else {
            _tmpLastEditedBy = _stmt.getText(_columnIndexOfLastEditedBy)
          }
          val _tmpAdminFlagged: Boolean
          val _tmp_14: Int
          _tmp_14 = _stmt.getLong(_columnIndexOfAdminFlagged).toInt()
          _tmpAdminFlagged = _tmp_14 != 0
          val _tmpModerationNote: String?
          if (_stmt.isNull(_columnIndexOfModerationNote)) {
            _tmpModerationNote = null
          } else {
            _tmpModerationNote = _stmt.getText(_columnIndexOfModerationNote)
          }
          val _tmpMetadataJson: String
          _tmpMetadataJson = _stmt.getText(_columnIndexOfMetadataJson)
          _item =
              ProductEntity(_tmpProductId,_tmpSellerId,_tmpName,_tmpDescription,_tmpCategory,_tmpPrice,_tmpQuantity,_tmpUnit,_tmpLocation,_tmpLatitude,_tmpLongitude,_tmpImageUrls,_tmpStatus,_tmpCondition,_tmpHarvestDate,_tmpExpiryDate,_tmpBirthDate,_tmpVaccinationRecordsJson,_tmpWeightGrams,_tmpHeightCm,_tmpGender,_tmpColor,_tmpBreed,_tmpRaisingPurpose,_tmpHealthStatus,_tmpBirdCode,_tmpColorTag,_tmpFamilyTreeId,_tmpSourceAssetId,_tmpParentIdsJson,_tmpBreedingStatus,_tmpTransferHistoryJson,_tmpCreatedAt,_tmpUpdatedAt,_tmpLastModifiedAt,_tmpIsDeleted,_tmpDeletedAt,_tmpDirty,_tmpStage,_tmpLifecycleStatus,_tmpParentMaleId,_tmpParentFemaleId,_tmpAgeWeeks,_tmpLastStageTransitionAt,_tmpBreederEligibleAt,_tmpIsBatch,_tmpBatchId,_tmpSplitAt,_tmpSplitIntoIds,_tmpDocumentUrls,_tmpQrCodeUrl,_tmpCustomStatus,_tmpDebug,_tmpDeliveryOptions,_tmpDeliveryCost,_tmpLeadTimeDays,_tmpMotherId,_tmpIsBreedingUnit,_tmpEggsCollectedToday,_tmpLastEggLogDate,_tmpReadyForSale,_tmpTargetWeight,_tmpIsShowcased,_tmpExternalVideoUrl,_tmpRecordsLockedAt,_tmpAutoLockAfterDays,_tmpLineageHistoryJson,_tmpEditCount,_tmpLastEditedBy,_tmpAdminFlagged,_tmpModerationNote,_tmpMetadataJson)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeBreederCount(farmerId: String): Flow<Int> {
    val _sql: String =
        "SELECT COUNT(*) FROM products WHERE sellerId = ? AND stage = 'BREEDER' AND (lifecycleStatus = 'ACTIVE' OR lifecycleStatus IS NULL)"
    return createFlow(__db, false, arrayOf("products")) { _connection ->
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

  public override suspend fun getBreederEligible(farmerId: String, now: Long): List<ProductEntity> {
    val _sql: String =
        "SELECT * FROM products WHERE sellerId = ? AND breederEligibleAt IS NOT NULL AND breederEligibleAt <= ? AND (stage IS NULL OR stage != 'BREEDER')"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, farmerId)
        _argIndex = 2
        _stmt.bindLong(_argIndex, now)
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfSellerId: Int = getColumnIndexOrThrow(_stmt, "sellerId")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfCategory: Int = getColumnIndexOrThrow(_stmt, "category")
        val _columnIndexOfPrice: Int = getColumnIndexOrThrow(_stmt, "price")
        val _columnIndexOfQuantity: Int = getColumnIndexOrThrow(_stmt, "quantity")
        val _columnIndexOfUnit: Int = getColumnIndexOrThrow(_stmt, "unit")
        val _columnIndexOfLocation: Int = getColumnIndexOrThrow(_stmt, "location")
        val _columnIndexOfLatitude: Int = getColumnIndexOrThrow(_stmt, "latitude")
        val _columnIndexOfLongitude: Int = getColumnIndexOrThrow(_stmt, "longitude")
        val _columnIndexOfImageUrls: Int = getColumnIndexOrThrow(_stmt, "imageUrls")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfCondition: Int = getColumnIndexOrThrow(_stmt, "condition")
        val _columnIndexOfHarvestDate: Int = getColumnIndexOrThrow(_stmt, "harvestDate")
        val _columnIndexOfExpiryDate: Int = getColumnIndexOrThrow(_stmt, "expiryDate")
        val _columnIndexOfBirthDate: Int = getColumnIndexOrThrow(_stmt, "birthDate")
        val _columnIndexOfVaccinationRecordsJson: Int = getColumnIndexOrThrow(_stmt,
            "vaccinationRecordsJson")
        val _columnIndexOfWeightGrams: Int = getColumnIndexOrThrow(_stmt, "weightGrams")
        val _columnIndexOfHeightCm: Int = getColumnIndexOrThrow(_stmt, "heightCm")
        val _columnIndexOfGender: Int = getColumnIndexOrThrow(_stmt, "gender")
        val _columnIndexOfColor: Int = getColumnIndexOrThrow(_stmt, "color")
        val _columnIndexOfBreed: Int = getColumnIndexOrThrow(_stmt, "breed")
        val _columnIndexOfRaisingPurpose: Int = getColumnIndexOrThrow(_stmt, "raisingPurpose")
        val _columnIndexOfHealthStatus: Int = getColumnIndexOrThrow(_stmt, "healthStatus")
        val _columnIndexOfBirdCode: Int = getColumnIndexOrThrow(_stmt, "birdCode")
        val _columnIndexOfColorTag: Int = getColumnIndexOrThrow(_stmt, "colorTag")
        val _columnIndexOfFamilyTreeId: Int = getColumnIndexOrThrow(_stmt, "familyTreeId")
        val _columnIndexOfSourceAssetId: Int = getColumnIndexOrThrow(_stmt, "sourceAssetId")
        val _columnIndexOfParentIdsJson: Int = getColumnIndexOrThrow(_stmt, "parentIdsJson")
        val _columnIndexOfBreedingStatus: Int = getColumnIndexOrThrow(_stmt, "breedingStatus")
        val _columnIndexOfTransferHistoryJson: Int = getColumnIndexOrThrow(_stmt,
            "transferHistoryJson")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfLastModifiedAt: Int = getColumnIndexOrThrow(_stmt, "lastModifiedAt")
        val _columnIndexOfIsDeleted: Int = getColumnIndexOrThrow(_stmt, "isDeleted")
        val _columnIndexOfDeletedAt: Int = getColumnIndexOrThrow(_stmt, "deletedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfStage: Int = getColumnIndexOrThrow(_stmt, "stage")
        val _columnIndexOfLifecycleStatus: Int = getColumnIndexOrThrow(_stmt, "lifecycleStatus")
        val _columnIndexOfParentMaleId: Int = getColumnIndexOrThrow(_stmt, "parentMaleId")
        val _columnIndexOfParentFemaleId: Int = getColumnIndexOrThrow(_stmt, "parentFemaleId")
        val _columnIndexOfAgeWeeks: Int = getColumnIndexOrThrow(_stmt, "ageWeeks")
        val _columnIndexOfLastStageTransitionAt: Int = getColumnIndexOrThrow(_stmt,
            "lastStageTransitionAt")
        val _columnIndexOfBreederEligibleAt: Int = getColumnIndexOrThrow(_stmt, "breederEligibleAt")
        val _columnIndexOfIsBatch: Int = getColumnIndexOrThrow(_stmt, "isBatch")
        val _columnIndexOfBatchId: Int = getColumnIndexOrThrow(_stmt, "batchId")
        val _columnIndexOfSplitAt: Int = getColumnIndexOrThrow(_stmt, "splitAt")
        val _columnIndexOfSplitIntoIds: Int = getColumnIndexOrThrow(_stmt, "splitIntoIds")
        val _columnIndexOfDocumentUrls: Int = getColumnIndexOrThrow(_stmt, "documentUrls")
        val _columnIndexOfQrCodeUrl: Int = getColumnIndexOrThrow(_stmt, "qrCodeUrl")
        val _columnIndexOfCustomStatus: Int = getColumnIndexOrThrow(_stmt, "customStatus")
        val _columnIndexOfDebug: Int = getColumnIndexOrThrow(_stmt, "debug")
        val _columnIndexOfDeliveryOptions: Int = getColumnIndexOrThrow(_stmt, "deliveryOptions")
        val _columnIndexOfDeliveryCost: Int = getColumnIndexOrThrow(_stmt, "deliveryCost")
        val _columnIndexOfLeadTimeDays: Int = getColumnIndexOrThrow(_stmt, "leadTimeDays")
        val _columnIndexOfMotherId: Int = getColumnIndexOrThrow(_stmt, "motherId")
        val _columnIndexOfIsBreedingUnit: Int = getColumnIndexOrThrow(_stmt, "isBreedingUnit")
        val _columnIndexOfEggsCollectedToday: Int = getColumnIndexOrThrow(_stmt,
            "eggsCollectedToday")
        val _columnIndexOfLastEggLogDate: Int = getColumnIndexOrThrow(_stmt, "lastEggLogDate")
        val _columnIndexOfReadyForSale: Int = getColumnIndexOrThrow(_stmt, "readyForSale")
        val _columnIndexOfTargetWeight: Int = getColumnIndexOrThrow(_stmt, "targetWeight")
        val _columnIndexOfIsShowcased: Int = getColumnIndexOrThrow(_stmt, "isShowcased")
        val _columnIndexOfExternalVideoUrl: Int = getColumnIndexOrThrow(_stmt, "externalVideoUrl")
        val _columnIndexOfRecordsLockedAt: Int = getColumnIndexOrThrow(_stmt, "recordsLockedAt")
        val _columnIndexOfAutoLockAfterDays: Int = getColumnIndexOrThrow(_stmt, "autoLockAfterDays")
        val _columnIndexOfLineageHistoryJson: Int = getColumnIndexOrThrow(_stmt,
            "lineageHistoryJson")
        val _columnIndexOfEditCount: Int = getColumnIndexOrThrow(_stmt, "editCount")
        val _columnIndexOfLastEditedBy: Int = getColumnIndexOrThrow(_stmt, "lastEditedBy")
        val _columnIndexOfAdminFlagged: Int = getColumnIndexOrThrow(_stmt, "adminFlagged")
        val _columnIndexOfModerationNote: Int = getColumnIndexOrThrow(_stmt, "moderationNote")
        val _columnIndexOfMetadataJson: Int = getColumnIndexOrThrow(_stmt, "metadataJson")
        val _result: MutableList<ProductEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: ProductEntity
          val _tmpProductId: String
          _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          val _tmpSellerId: String
          _tmpSellerId = _stmt.getText(_columnIndexOfSellerId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpDescription: String
          _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          val _tmpCategory: String
          _tmpCategory = _stmt.getText(_columnIndexOfCategory)
          val _tmpPrice: Double
          _tmpPrice = _stmt.getDouble(_columnIndexOfPrice)
          val _tmpQuantity: Double
          _tmpQuantity = _stmt.getDouble(_columnIndexOfQuantity)
          val _tmpUnit: String
          _tmpUnit = _stmt.getText(_columnIndexOfUnit)
          val _tmpLocation: String
          _tmpLocation = _stmt.getText(_columnIndexOfLocation)
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
          val _tmpImageUrls: List<String>
          val _tmp: String?
          if (_stmt.isNull(_columnIndexOfImageUrls)) {
            _tmp = null
          } else {
            _tmp = _stmt.getText(_columnIndexOfImageUrls)
          }
          val _tmp_1: List<String>? = AppDatabase.Converters.toStringList(_tmp)
          if (_tmp_1 == null) {
            error("Expected NON-NULL 'kotlin.collections.List<kotlin.String>', but it was NULL.")
          } else {
            _tmpImageUrls = _tmp_1
          }
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpCondition: String?
          if (_stmt.isNull(_columnIndexOfCondition)) {
            _tmpCondition = null
          } else {
            _tmpCondition = _stmt.getText(_columnIndexOfCondition)
          }
          val _tmpHarvestDate: Long?
          if (_stmt.isNull(_columnIndexOfHarvestDate)) {
            _tmpHarvestDate = null
          } else {
            _tmpHarvestDate = _stmt.getLong(_columnIndexOfHarvestDate)
          }
          val _tmpExpiryDate: Long?
          if (_stmt.isNull(_columnIndexOfExpiryDate)) {
            _tmpExpiryDate = null
          } else {
            _tmpExpiryDate = _stmt.getLong(_columnIndexOfExpiryDate)
          }
          val _tmpBirthDate: Long?
          if (_stmt.isNull(_columnIndexOfBirthDate)) {
            _tmpBirthDate = null
          } else {
            _tmpBirthDate = _stmt.getLong(_columnIndexOfBirthDate)
          }
          val _tmpVaccinationRecordsJson: String?
          if (_stmt.isNull(_columnIndexOfVaccinationRecordsJson)) {
            _tmpVaccinationRecordsJson = null
          } else {
            _tmpVaccinationRecordsJson = _stmt.getText(_columnIndexOfVaccinationRecordsJson)
          }
          val _tmpWeightGrams: Double?
          if (_stmt.isNull(_columnIndexOfWeightGrams)) {
            _tmpWeightGrams = null
          } else {
            _tmpWeightGrams = _stmt.getDouble(_columnIndexOfWeightGrams)
          }
          val _tmpHeightCm: Double?
          if (_stmt.isNull(_columnIndexOfHeightCm)) {
            _tmpHeightCm = null
          } else {
            _tmpHeightCm = _stmt.getDouble(_columnIndexOfHeightCm)
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
          val _tmpBreed: String?
          if (_stmt.isNull(_columnIndexOfBreed)) {
            _tmpBreed = null
          } else {
            _tmpBreed = _stmt.getText(_columnIndexOfBreed)
          }
          val _tmpRaisingPurpose: String?
          if (_stmt.isNull(_columnIndexOfRaisingPurpose)) {
            _tmpRaisingPurpose = null
          } else {
            _tmpRaisingPurpose = _stmt.getText(_columnIndexOfRaisingPurpose)
          }
          val _tmpHealthStatus: String?
          if (_stmt.isNull(_columnIndexOfHealthStatus)) {
            _tmpHealthStatus = null
          } else {
            _tmpHealthStatus = _stmt.getText(_columnIndexOfHealthStatus)
          }
          val _tmpBirdCode: String?
          if (_stmt.isNull(_columnIndexOfBirdCode)) {
            _tmpBirdCode = null
          } else {
            _tmpBirdCode = _stmt.getText(_columnIndexOfBirdCode)
          }
          val _tmpColorTag: String?
          if (_stmt.isNull(_columnIndexOfColorTag)) {
            _tmpColorTag = null
          } else {
            _tmpColorTag = _stmt.getText(_columnIndexOfColorTag)
          }
          val _tmpFamilyTreeId: String?
          if (_stmt.isNull(_columnIndexOfFamilyTreeId)) {
            _tmpFamilyTreeId = null
          } else {
            _tmpFamilyTreeId = _stmt.getText(_columnIndexOfFamilyTreeId)
          }
          val _tmpSourceAssetId: String?
          if (_stmt.isNull(_columnIndexOfSourceAssetId)) {
            _tmpSourceAssetId = null
          } else {
            _tmpSourceAssetId = _stmt.getText(_columnIndexOfSourceAssetId)
          }
          val _tmpParentIdsJson: String?
          if (_stmt.isNull(_columnIndexOfParentIdsJson)) {
            _tmpParentIdsJson = null
          } else {
            _tmpParentIdsJson = _stmt.getText(_columnIndexOfParentIdsJson)
          }
          val _tmpBreedingStatus: String?
          if (_stmt.isNull(_columnIndexOfBreedingStatus)) {
            _tmpBreedingStatus = null
          } else {
            _tmpBreedingStatus = _stmt.getText(_columnIndexOfBreedingStatus)
          }
          val _tmpTransferHistoryJson: String?
          if (_stmt.isNull(_columnIndexOfTransferHistoryJson)) {
            _tmpTransferHistoryJson = null
          } else {
            _tmpTransferHistoryJson = _stmt.getText(_columnIndexOfTransferHistoryJson)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpLastModifiedAt: Long
          _tmpLastModifiedAt = _stmt.getLong(_columnIndexOfLastModifiedAt)
          val _tmpIsDeleted: Boolean
          val _tmp_2: Int
          _tmp_2 = _stmt.getLong(_columnIndexOfIsDeleted).toInt()
          _tmpIsDeleted = _tmp_2 != 0
          val _tmpDeletedAt: Long?
          if (_stmt.isNull(_columnIndexOfDeletedAt)) {
            _tmpDeletedAt = null
          } else {
            _tmpDeletedAt = _stmt.getLong(_columnIndexOfDeletedAt)
          }
          val _tmpDirty: Boolean
          val _tmp_3: Int
          _tmp_3 = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp_3 != 0
          val _tmpStage: LifecycleStage?
          val _tmp_4: String?
          if (_stmt.isNull(_columnIndexOfStage)) {
            _tmp_4 = null
          } else {
            _tmp_4 = _stmt.getText(_columnIndexOfStage)
          }
          _tmpStage = AppDatabase.Converters.toLifecycleStage(_tmp_4)
          val _tmpLifecycleStatus: String?
          if (_stmt.isNull(_columnIndexOfLifecycleStatus)) {
            _tmpLifecycleStatus = null
          } else {
            _tmpLifecycleStatus = _stmt.getText(_columnIndexOfLifecycleStatus)
          }
          val _tmpParentMaleId: String?
          if (_stmt.isNull(_columnIndexOfParentMaleId)) {
            _tmpParentMaleId = null
          } else {
            _tmpParentMaleId = _stmt.getText(_columnIndexOfParentMaleId)
          }
          val _tmpParentFemaleId: String?
          if (_stmt.isNull(_columnIndexOfParentFemaleId)) {
            _tmpParentFemaleId = null
          } else {
            _tmpParentFemaleId = _stmt.getText(_columnIndexOfParentFemaleId)
          }
          val _tmpAgeWeeks: Int?
          if (_stmt.isNull(_columnIndexOfAgeWeeks)) {
            _tmpAgeWeeks = null
          } else {
            _tmpAgeWeeks = _stmt.getLong(_columnIndexOfAgeWeeks).toInt()
          }
          val _tmpLastStageTransitionAt: Long?
          if (_stmt.isNull(_columnIndexOfLastStageTransitionAt)) {
            _tmpLastStageTransitionAt = null
          } else {
            _tmpLastStageTransitionAt = _stmt.getLong(_columnIndexOfLastStageTransitionAt)
          }
          val _tmpBreederEligibleAt: Long?
          if (_stmt.isNull(_columnIndexOfBreederEligibleAt)) {
            _tmpBreederEligibleAt = null
          } else {
            _tmpBreederEligibleAt = _stmt.getLong(_columnIndexOfBreederEligibleAt)
          }
          val _tmpIsBatch: Boolean?
          val _tmp_5: Int?
          if (_stmt.isNull(_columnIndexOfIsBatch)) {
            _tmp_5 = null
          } else {
            _tmp_5 = _stmt.getLong(_columnIndexOfIsBatch).toInt()
          }
          _tmpIsBatch = _tmp_5?.let { it != 0 }
          val _tmpBatchId: String?
          if (_stmt.isNull(_columnIndexOfBatchId)) {
            _tmpBatchId = null
          } else {
            _tmpBatchId = _stmt.getText(_columnIndexOfBatchId)
          }
          val _tmpSplitAt: Long?
          if (_stmt.isNull(_columnIndexOfSplitAt)) {
            _tmpSplitAt = null
          } else {
            _tmpSplitAt = _stmt.getLong(_columnIndexOfSplitAt)
          }
          val _tmpSplitIntoIds: String?
          if (_stmt.isNull(_columnIndexOfSplitIntoIds)) {
            _tmpSplitIntoIds = null
          } else {
            _tmpSplitIntoIds = _stmt.getText(_columnIndexOfSplitIntoIds)
          }
          val _tmpDocumentUrls: List<String>
          val _tmp_6: String?
          if (_stmt.isNull(_columnIndexOfDocumentUrls)) {
            _tmp_6 = null
          } else {
            _tmp_6 = _stmt.getText(_columnIndexOfDocumentUrls)
          }
          val _tmp_7: List<String>? = AppDatabase.Converters.toStringList(_tmp_6)
          if (_tmp_7 == null) {
            error("Expected NON-NULL 'kotlin.collections.List<kotlin.String>', but it was NULL.")
          } else {
            _tmpDocumentUrls = _tmp_7
          }
          val _tmpQrCodeUrl: String?
          if (_stmt.isNull(_columnIndexOfQrCodeUrl)) {
            _tmpQrCodeUrl = null
          } else {
            _tmpQrCodeUrl = _stmt.getText(_columnIndexOfQrCodeUrl)
          }
          val _tmpCustomStatus: String?
          if (_stmt.isNull(_columnIndexOfCustomStatus)) {
            _tmpCustomStatus = null
          } else {
            _tmpCustomStatus = _stmt.getText(_columnIndexOfCustomStatus)
          }
          val _tmpDebug: Boolean
          val _tmp_8: Int
          _tmp_8 = _stmt.getLong(_columnIndexOfDebug).toInt()
          _tmpDebug = _tmp_8 != 0
          val _tmpDeliveryOptions: List<String>
          val _tmp_9: String?
          if (_stmt.isNull(_columnIndexOfDeliveryOptions)) {
            _tmp_9 = null
          } else {
            _tmp_9 = _stmt.getText(_columnIndexOfDeliveryOptions)
          }
          val _tmp_10: List<String>? = AppDatabase.Converters.toStringList(_tmp_9)
          if (_tmp_10 == null) {
            error("Expected NON-NULL 'kotlin.collections.List<kotlin.String>', but it was NULL.")
          } else {
            _tmpDeliveryOptions = _tmp_10
          }
          val _tmpDeliveryCost: Double?
          if (_stmt.isNull(_columnIndexOfDeliveryCost)) {
            _tmpDeliveryCost = null
          } else {
            _tmpDeliveryCost = _stmt.getDouble(_columnIndexOfDeliveryCost)
          }
          val _tmpLeadTimeDays: Int?
          if (_stmt.isNull(_columnIndexOfLeadTimeDays)) {
            _tmpLeadTimeDays = null
          } else {
            _tmpLeadTimeDays = _stmt.getLong(_columnIndexOfLeadTimeDays).toInt()
          }
          val _tmpMotherId: String?
          if (_stmt.isNull(_columnIndexOfMotherId)) {
            _tmpMotherId = null
          } else {
            _tmpMotherId = _stmt.getText(_columnIndexOfMotherId)
          }
          val _tmpIsBreedingUnit: Boolean
          val _tmp_11: Int
          _tmp_11 = _stmt.getLong(_columnIndexOfIsBreedingUnit).toInt()
          _tmpIsBreedingUnit = _tmp_11 != 0
          val _tmpEggsCollectedToday: Int
          _tmpEggsCollectedToday = _stmt.getLong(_columnIndexOfEggsCollectedToday).toInt()
          val _tmpLastEggLogDate: Long?
          if (_stmt.isNull(_columnIndexOfLastEggLogDate)) {
            _tmpLastEggLogDate = null
          } else {
            _tmpLastEggLogDate = _stmt.getLong(_columnIndexOfLastEggLogDate)
          }
          val _tmpReadyForSale: Boolean
          val _tmp_12: Int
          _tmp_12 = _stmt.getLong(_columnIndexOfReadyForSale).toInt()
          _tmpReadyForSale = _tmp_12 != 0
          val _tmpTargetWeight: Double?
          if (_stmt.isNull(_columnIndexOfTargetWeight)) {
            _tmpTargetWeight = null
          } else {
            _tmpTargetWeight = _stmt.getDouble(_columnIndexOfTargetWeight)
          }
          val _tmpIsShowcased: Boolean
          val _tmp_13: Int
          _tmp_13 = _stmt.getLong(_columnIndexOfIsShowcased).toInt()
          _tmpIsShowcased = _tmp_13 != 0
          val _tmpExternalVideoUrl: String?
          if (_stmt.isNull(_columnIndexOfExternalVideoUrl)) {
            _tmpExternalVideoUrl = null
          } else {
            _tmpExternalVideoUrl = _stmt.getText(_columnIndexOfExternalVideoUrl)
          }
          val _tmpRecordsLockedAt: Long?
          if (_stmt.isNull(_columnIndexOfRecordsLockedAt)) {
            _tmpRecordsLockedAt = null
          } else {
            _tmpRecordsLockedAt = _stmt.getLong(_columnIndexOfRecordsLockedAt)
          }
          val _tmpAutoLockAfterDays: Int
          _tmpAutoLockAfterDays = _stmt.getLong(_columnIndexOfAutoLockAfterDays).toInt()
          val _tmpLineageHistoryJson: String?
          if (_stmt.isNull(_columnIndexOfLineageHistoryJson)) {
            _tmpLineageHistoryJson = null
          } else {
            _tmpLineageHistoryJson = _stmt.getText(_columnIndexOfLineageHistoryJson)
          }
          val _tmpEditCount: Int
          _tmpEditCount = _stmt.getLong(_columnIndexOfEditCount).toInt()
          val _tmpLastEditedBy: String?
          if (_stmt.isNull(_columnIndexOfLastEditedBy)) {
            _tmpLastEditedBy = null
          } else {
            _tmpLastEditedBy = _stmt.getText(_columnIndexOfLastEditedBy)
          }
          val _tmpAdminFlagged: Boolean
          val _tmp_14: Int
          _tmp_14 = _stmt.getLong(_columnIndexOfAdminFlagged).toInt()
          _tmpAdminFlagged = _tmp_14 != 0
          val _tmpModerationNote: String?
          if (_stmt.isNull(_columnIndexOfModerationNote)) {
            _tmpModerationNote = null
          } else {
            _tmpModerationNote = _stmt.getText(_columnIndexOfModerationNote)
          }
          val _tmpMetadataJson: String
          _tmpMetadataJson = _stmt.getText(_columnIndexOfMetadataJson)
          _item =
              ProductEntity(_tmpProductId,_tmpSellerId,_tmpName,_tmpDescription,_tmpCategory,_tmpPrice,_tmpQuantity,_tmpUnit,_tmpLocation,_tmpLatitude,_tmpLongitude,_tmpImageUrls,_tmpStatus,_tmpCondition,_tmpHarvestDate,_tmpExpiryDate,_tmpBirthDate,_tmpVaccinationRecordsJson,_tmpWeightGrams,_tmpHeightCm,_tmpGender,_tmpColor,_tmpBreed,_tmpRaisingPurpose,_tmpHealthStatus,_tmpBirdCode,_tmpColorTag,_tmpFamilyTreeId,_tmpSourceAssetId,_tmpParentIdsJson,_tmpBreedingStatus,_tmpTransferHistoryJson,_tmpCreatedAt,_tmpUpdatedAt,_tmpLastModifiedAt,_tmpIsDeleted,_tmpDeletedAt,_tmpDirty,_tmpStage,_tmpLifecycleStatus,_tmpParentMaleId,_tmpParentFemaleId,_tmpAgeWeeks,_tmpLastStageTransitionAt,_tmpBreederEligibleAt,_tmpIsBatch,_tmpBatchId,_tmpSplitAt,_tmpSplitIntoIds,_tmpDocumentUrls,_tmpQrCodeUrl,_tmpCustomStatus,_tmpDebug,_tmpDeliveryOptions,_tmpDeliveryCost,_tmpLeadTimeDays,_tmpMotherId,_tmpIsBreedingUnit,_tmpEggsCollectedToday,_tmpLastEggLogDate,_tmpReadyForSale,_tmpTargetWeight,_tmpIsShowcased,_tmpExternalVideoUrl,_tmpRecordsLockedAt,_tmpAutoLockAfterDays,_tmpLineageHistoryJson,_tmpEditCount,_tmpLastEditedBy,_tmpAdminFlagged,_tmpModerationNote,_tmpMetadataJson)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeActiveWithBirth(): Flow<List<ProductEntity>> {
    val _sql: String =
        "SELECT * FROM products WHERE (lifecycleStatus = 'ACTIVE' OR lifecycleStatus IS NULL) AND birthDate IS NOT NULL"
    return createFlow(__db, false, arrayOf("products")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfSellerId: Int = getColumnIndexOrThrow(_stmt, "sellerId")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfCategory: Int = getColumnIndexOrThrow(_stmt, "category")
        val _columnIndexOfPrice: Int = getColumnIndexOrThrow(_stmt, "price")
        val _columnIndexOfQuantity: Int = getColumnIndexOrThrow(_stmt, "quantity")
        val _columnIndexOfUnit: Int = getColumnIndexOrThrow(_stmt, "unit")
        val _columnIndexOfLocation: Int = getColumnIndexOrThrow(_stmt, "location")
        val _columnIndexOfLatitude: Int = getColumnIndexOrThrow(_stmt, "latitude")
        val _columnIndexOfLongitude: Int = getColumnIndexOrThrow(_stmt, "longitude")
        val _columnIndexOfImageUrls: Int = getColumnIndexOrThrow(_stmt, "imageUrls")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfCondition: Int = getColumnIndexOrThrow(_stmt, "condition")
        val _columnIndexOfHarvestDate: Int = getColumnIndexOrThrow(_stmt, "harvestDate")
        val _columnIndexOfExpiryDate: Int = getColumnIndexOrThrow(_stmt, "expiryDate")
        val _columnIndexOfBirthDate: Int = getColumnIndexOrThrow(_stmt, "birthDate")
        val _columnIndexOfVaccinationRecordsJson: Int = getColumnIndexOrThrow(_stmt,
            "vaccinationRecordsJson")
        val _columnIndexOfWeightGrams: Int = getColumnIndexOrThrow(_stmt, "weightGrams")
        val _columnIndexOfHeightCm: Int = getColumnIndexOrThrow(_stmt, "heightCm")
        val _columnIndexOfGender: Int = getColumnIndexOrThrow(_stmt, "gender")
        val _columnIndexOfColor: Int = getColumnIndexOrThrow(_stmt, "color")
        val _columnIndexOfBreed: Int = getColumnIndexOrThrow(_stmt, "breed")
        val _columnIndexOfRaisingPurpose: Int = getColumnIndexOrThrow(_stmt, "raisingPurpose")
        val _columnIndexOfHealthStatus: Int = getColumnIndexOrThrow(_stmt, "healthStatus")
        val _columnIndexOfBirdCode: Int = getColumnIndexOrThrow(_stmt, "birdCode")
        val _columnIndexOfColorTag: Int = getColumnIndexOrThrow(_stmt, "colorTag")
        val _columnIndexOfFamilyTreeId: Int = getColumnIndexOrThrow(_stmt, "familyTreeId")
        val _columnIndexOfSourceAssetId: Int = getColumnIndexOrThrow(_stmt, "sourceAssetId")
        val _columnIndexOfParentIdsJson: Int = getColumnIndexOrThrow(_stmt, "parentIdsJson")
        val _columnIndexOfBreedingStatus: Int = getColumnIndexOrThrow(_stmt, "breedingStatus")
        val _columnIndexOfTransferHistoryJson: Int = getColumnIndexOrThrow(_stmt,
            "transferHistoryJson")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfLastModifiedAt: Int = getColumnIndexOrThrow(_stmt, "lastModifiedAt")
        val _columnIndexOfIsDeleted: Int = getColumnIndexOrThrow(_stmt, "isDeleted")
        val _columnIndexOfDeletedAt: Int = getColumnIndexOrThrow(_stmt, "deletedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfStage: Int = getColumnIndexOrThrow(_stmt, "stage")
        val _columnIndexOfLifecycleStatus: Int = getColumnIndexOrThrow(_stmt, "lifecycleStatus")
        val _columnIndexOfParentMaleId: Int = getColumnIndexOrThrow(_stmt, "parentMaleId")
        val _columnIndexOfParentFemaleId: Int = getColumnIndexOrThrow(_stmt, "parentFemaleId")
        val _columnIndexOfAgeWeeks: Int = getColumnIndexOrThrow(_stmt, "ageWeeks")
        val _columnIndexOfLastStageTransitionAt: Int = getColumnIndexOrThrow(_stmt,
            "lastStageTransitionAt")
        val _columnIndexOfBreederEligibleAt: Int = getColumnIndexOrThrow(_stmt, "breederEligibleAt")
        val _columnIndexOfIsBatch: Int = getColumnIndexOrThrow(_stmt, "isBatch")
        val _columnIndexOfBatchId: Int = getColumnIndexOrThrow(_stmt, "batchId")
        val _columnIndexOfSplitAt: Int = getColumnIndexOrThrow(_stmt, "splitAt")
        val _columnIndexOfSplitIntoIds: Int = getColumnIndexOrThrow(_stmt, "splitIntoIds")
        val _columnIndexOfDocumentUrls: Int = getColumnIndexOrThrow(_stmt, "documentUrls")
        val _columnIndexOfQrCodeUrl: Int = getColumnIndexOrThrow(_stmt, "qrCodeUrl")
        val _columnIndexOfCustomStatus: Int = getColumnIndexOrThrow(_stmt, "customStatus")
        val _columnIndexOfDebug: Int = getColumnIndexOrThrow(_stmt, "debug")
        val _columnIndexOfDeliveryOptions: Int = getColumnIndexOrThrow(_stmt, "deliveryOptions")
        val _columnIndexOfDeliveryCost: Int = getColumnIndexOrThrow(_stmt, "deliveryCost")
        val _columnIndexOfLeadTimeDays: Int = getColumnIndexOrThrow(_stmt, "leadTimeDays")
        val _columnIndexOfMotherId: Int = getColumnIndexOrThrow(_stmt, "motherId")
        val _columnIndexOfIsBreedingUnit: Int = getColumnIndexOrThrow(_stmt, "isBreedingUnit")
        val _columnIndexOfEggsCollectedToday: Int = getColumnIndexOrThrow(_stmt,
            "eggsCollectedToday")
        val _columnIndexOfLastEggLogDate: Int = getColumnIndexOrThrow(_stmt, "lastEggLogDate")
        val _columnIndexOfReadyForSale: Int = getColumnIndexOrThrow(_stmt, "readyForSale")
        val _columnIndexOfTargetWeight: Int = getColumnIndexOrThrow(_stmt, "targetWeight")
        val _columnIndexOfIsShowcased: Int = getColumnIndexOrThrow(_stmt, "isShowcased")
        val _columnIndexOfExternalVideoUrl: Int = getColumnIndexOrThrow(_stmt, "externalVideoUrl")
        val _columnIndexOfRecordsLockedAt: Int = getColumnIndexOrThrow(_stmt, "recordsLockedAt")
        val _columnIndexOfAutoLockAfterDays: Int = getColumnIndexOrThrow(_stmt, "autoLockAfterDays")
        val _columnIndexOfLineageHistoryJson: Int = getColumnIndexOrThrow(_stmt,
            "lineageHistoryJson")
        val _columnIndexOfEditCount: Int = getColumnIndexOrThrow(_stmt, "editCount")
        val _columnIndexOfLastEditedBy: Int = getColumnIndexOrThrow(_stmt, "lastEditedBy")
        val _columnIndexOfAdminFlagged: Int = getColumnIndexOrThrow(_stmt, "adminFlagged")
        val _columnIndexOfModerationNote: Int = getColumnIndexOrThrow(_stmt, "moderationNote")
        val _columnIndexOfMetadataJson: Int = getColumnIndexOrThrow(_stmt, "metadataJson")
        val _result: MutableList<ProductEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: ProductEntity
          val _tmpProductId: String
          _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          val _tmpSellerId: String
          _tmpSellerId = _stmt.getText(_columnIndexOfSellerId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpDescription: String
          _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          val _tmpCategory: String
          _tmpCategory = _stmt.getText(_columnIndexOfCategory)
          val _tmpPrice: Double
          _tmpPrice = _stmt.getDouble(_columnIndexOfPrice)
          val _tmpQuantity: Double
          _tmpQuantity = _stmt.getDouble(_columnIndexOfQuantity)
          val _tmpUnit: String
          _tmpUnit = _stmt.getText(_columnIndexOfUnit)
          val _tmpLocation: String
          _tmpLocation = _stmt.getText(_columnIndexOfLocation)
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
          val _tmpImageUrls: List<String>
          val _tmp: String?
          if (_stmt.isNull(_columnIndexOfImageUrls)) {
            _tmp = null
          } else {
            _tmp = _stmt.getText(_columnIndexOfImageUrls)
          }
          val _tmp_1: List<String>? = AppDatabase.Converters.toStringList(_tmp)
          if (_tmp_1 == null) {
            error("Expected NON-NULL 'kotlin.collections.List<kotlin.String>', but it was NULL.")
          } else {
            _tmpImageUrls = _tmp_1
          }
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpCondition: String?
          if (_stmt.isNull(_columnIndexOfCondition)) {
            _tmpCondition = null
          } else {
            _tmpCondition = _stmt.getText(_columnIndexOfCondition)
          }
          val _tmpHarvestDate: Long?
          if (_stmt.isNull(_columnIndexOfHarvestDate)) {
            _tmpHarvestDate = null
          } else {
            _tmpHarvestDate = _stmt.getLong(_columnIndexOfHarvestDate)
          }
          val _tmpExpiryDate: Long?
          if (_stmt.isNull(_columnIndexOfExpiryDate)) {
            _tmpExpiryDate = null
          } else {
            _tmpExpiryDate = _stmt.getLong(_columnIndexOfExpiryDate)
          }
          val _tmpBirthDate: Long?
          if (_stmt.isNull(_columnIndexOfBirthDate)) {
            _tmpBirthDate = null
          } else {
            _tmpBirthDate = _stmt.getLong(_columnIndexOfBirthDate)
          }
          val _tmpVaccinationRecordsJson: String?
          if (_stmt.isNull(_columnIndexOfVaccinationRecordsJson)) {
            _tmpVaccinationRecordsJson = null
          } else {
            _tmpVaccinationRecordsJson = _stmt.getText(_columnIndexOfVaccinationRecordsJson)
          }
          val _tmpWeightGrams: Double?
          if (_stmt.isNull(_columnIndexOfWeightGrams)) {
            _tmpWeightGrams = null
          } else {
            _tmpWeightGrams = _stmt.getDouble(_columnIndexOfWeightGrams)
          }
          val _tmpHeightCm: Double?
          if (_stmt.isNull(_columnIndexOfHeightCm)) {
            _tmpHeightCm = null
          } else {
            _tmpHeightCm = _stmt.getDouble(_columnIndexOfHeightCm)
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
          val _tmpBreed: String?
          if (_stmt.isNull(_columnIndexOfBreed)) {
            _tmpBreed = null
          } else {
            _tmpBreed = _stmt.getText(_columnIndexOfBreed)
          }
          val _tmpRaisingPurpose: String?
          if (_stmt.isNull(_columnIndexOfRaisingPurpose)) {
            _tmpRaisingPurpose = null
          } else {
            _tmpRaisingPurpose = _stmt.getText(_columnIndexOfRaisingPurpose)
          }
          val _tmpHealthStatus: String?
          if (_stmt.isNull(_columnIndexOfHealthStatus)) {
            _tmpHealthStatus = null
          } else {
            _tmpHealthStatus = _stmt.getText(_columnIndexOfHealthStatus)
          }
          val _tmpBirdCode: String?
          if (_stmt.isNull(_columnIndexOfBirdCode)) {
            _tmpBirdCode = null
          } else {
            _tmpBirdCode = _stmt.getText(_columnIndexOfBirdCode)
          }
          val _tmpColorTag: String?
          if (_stmt.isNull(_columnIndexOfColorTag)) {
            _tmpColorTag = null
          } else {
            _tmpColorTag = _stmt.getText(_columnIndexOfColorTag)
          }
          val _tmpFamilyTreeId: String?
          if (_stmt.isNull(_columnIndexOfFamilyTreeId)) {
            _tmpFamilyTreeId = null
          } else {
            _tmpFamilyTreeId = _stmt.getText(_columnIndexOfFamilyTreeId)
          }
          val _tmpSourceAssetId: String?
          if (_stmt.isNull(_columnIndexOfSourceAssetId)) {
            _tmpSourceAssetId = null
          } else {
            _tmpSourceAssetId = _stmt.getText(_columnIndexOfSourceAssetId)
          }
          val _tmpParentIdsJson: String?
          if (_stmt.isNull(_columnIndexOfParentIdsJson)) {
            _tmpParentIdsJson = null
          } else {
            _tmpParentIdsJson = _stmt.getText(_columnIndexOfParentIdsJson)
          }
          val _tmpBreedingStatus: String?
          if (_stmt.isNull(_columnIndexOfBreedingStatus)) {
            _tmpBreedingStatus = null
          } else {
            _tmpBreedingStatus = _stmt.getText(_columnIndexOfBreedingStatus)
          }
          val _tmpTransferHistoryJson: String?
          if (_stmt.isNull(_columnIndexOfTransferHistoryJson)) {
            _tmpTransferHistoryJson = null
          } else {
            _tmpTransferHistoryJson = _stmt.getText(_columnIndexOfTransferHistoryJson)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpLastModifiedAt: Long
          _tmpLastModifiedAt = _stmt.getLong(_columnIndexOfLastModifiedAt)
          val _tmpIsDeleted: Boolean
          val _tmp_2: Int
          _tmp_2 = _stmt.getLong(_columnIndexOfIsDeleted).toInt()
          _tmpIsDeleted = _tmp_2 != 0
          val _tmpDeletedAt: Long?
          if (_stmt.isNull(_columnIndexOfDeletedAt)) {
            _tmpDeletedAt = null
          } else {
            _tmpDeletedAt = _stmt.getLong(_columnIndexOfDeletedAt)
          }
          val _tmpDirty: Boolean
          val _tmp_3: Int
          _tmp_3 = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp_3 != 0
          val _tmpStage: LifecycleStage?
          val _tmp_4: String?
          if (_stmt.isNull(_columnIndexOfStage)) {
            _tmp_4 = null
          } else {
            _tmp_4 = _stmt.getText(_columnIndexOfStage)
          }
          _tmpStage = AppDatabase.Converters.toLifecycleStage(_tmp_4)
          val _tmpLifecycleStatus: String?
          if (_stmt.isNull(_columnIndexOfLifecycleStatus)) {
            _tmpLifecycleStatus = null
          } else {
            _tmpLifecycleStatus = _stmt.getText(_columnIndexOfLifecycleStatus)
          }
          val _tmpParentMaleId: String?
          if (_stmt.isNull(_columnIndexOfParentMaleId)) {
            _tmpParentMaleId = null
          } else {
            _tmpParentMaleId = _stmt.getText(_columnIndexOfParentMaleId)
          }
          val _tmpParentFemaleId: String?
          if (_stmt.isNull(_columnIndexOfParentFemaleId)) {
            _tmpParentFemaleId = null
          } else {
            _tmpParentFemaleId = _stmt.getText(_columnIndexOfParentFemaleId)
          }
          val _tmpAgeWeeks: Int?
          if (_stmt.isNull(_columnIndexOfAgeWeeks)) {
            _tmpAgeWeeks = null
          } else {
            _tmpAgeWeeks = _stmt.getLong(_columnIndexOfAgeWeeks).toInt()
          }
          val _tmpLastStageTransitionAt: Long?
          if (_stmt.isNull(_columnIndexOfLastStageTransitionAt)) {
            _tmpLastStageTransitionAt = null
          } else {
            _tmpLastStageTransitionAt = _stmt.getLong(_columnIndexOfLastStageTransitionAt)
          }
          val _tmpBreederEligibleAt: Long?
          if (_stmt.isNull(_columnIndexOfBreederEligibleAt)) {
            _tmpBreederEligibleAt = null
          } else {
            _tmpBreederEligibleAt = _stmt.getLong(_columnIndexOfBreederEligibleAt)
          }
          val _tmpIsBatch: Boolean?
          val _tmp_5: Int?
          if (_stmt.isNull(_columnIndexOfIsBatch)) {
            _tmp_5 = null
          } else {
            _tmp_5 = _stmt.getLong(_columnIndexOfIsBatch).toInt()
          }
          _tmpIsBatch = _tmp_5?.let { it != 0 }
          val _tmpBatchId: String?
          if (_stmt.isNull(_columnIndexOfBatchId)) {
            _tmpBatchId = null
          } else {
            _tmpBatchId = _stmt.getText(_columnIndexOfBatchId)
          }
          val _tmpSplitAt: Long?
          if (_stmt.isNull(_columnIndexOfSplitAt)) {
            _tmpSplitAt = null
          } else {
            _tmpSplitAt = _stmt.getLong(_columnIndexOfSplitAt)
          }
          val _tmpSplitIntoIds: String?
          if (_stmt.isNull(_columnIndexOfSplitIntoIds)) {
            _tmpSplitIntoIds = null
          } else {
            _tmpSplitIntoIds = _stmt.getText(_columnIndexOfSplitIntoIds)
          }
          val _tmpDocumentUrls: List<String>
          val _tmp_6: String?
          if (_stmt.isNull(_columnIndexOfDocumentUrls)) {
            _tmp_6 = null
          } else {
            _tmp_6 = _stmt.getText(_columnIndexOfDocumentUrls)
          }
          val _tmp_7: List<String>? = AppDatabase.Converters.toStringList(_tmp_6)
          if (_tmp_7 == null) {
            error("Expected NON-NULL 'kotlin.collections.List<kotlin.String>', but it was NULL.")
          } else {
            _tmpDocumentUrls = _tmp_7
          }
          val _tmpQrCodeUrl: String?
          if (_stmt.isNull(_columnIndexOfQrCodeUrl)) {
            _tmpQrCodeUrl = null
          } else {
            _tmpQrCodeUrl = _stmt.getText(_columnIndexOfQrCodeUrl)
          }
          val _tmpCustomStatus: String?
          if (_stmt.isNull(_columnIndexOfCustomStatus)) {
            _tmpCustomStatus = null
          } else {
            _tmpCustomStatus = _stmt.getText(_columnIndexOfCustomStatus)
          }
          val _tmpDebug: Boolean
          val _tmp_8: Int
          _tmp_8 = _stmt.getLong(_columnIndexOfDebug).toInt()
          _tmpDebug = _tmp_8 != 0
          val _tmpDeliveryOptions: List<String>
          val _tmp_9: String?
          if (_stmt.isNull(_columnIndexOfDeliveryOptions)) {
            _tmp_9 = null
          } else {
            _tmp_9 = _stmt.getText(_columnIndexOfDeliveryOptions)
          }
          val _tmp_10: List<String>? = AppDatabase.Converters.toStringList(_tmp_9)
          if (_tmp_10 == null) {
            error("Expected NON-NULL 'kotlin.collections.List<kotlin.String>', but it was NULL.")
          } else {
            _tmpDeliveryOptions = _tmp_10
          }
          val _tmpDeliveryCost: Double?
          if (_stmt.isNull(_columnIndexOfDeliveryCost)) {
            _tmpDeliveryCost = null
          } else {
            _tmpDeliveryCost = _stmt.getDouble(_columnIndexOfDeliveryCost)
          }
          val _tmpLeadTimeDays: Int?
          if (_stmt.isNull(_columnIndexOfLeadTimeDays)) {
            _tmpLeadTimeDays = null
          } else {
            _tmpLeadTimeDays = _stmt.getLong(_columnIndexOfLeadTimeDays).toInt()
          }
          val _tmpMotherId: String?
          if (_stmt.isNull(_columnIndexOfMotherId)) {
            _tmpMotherId = null
          } else {
            _tmpMotherId = _stmt.getText(_columnIndexOfMotherId)
          }
          val _tmpIsBreedingUnit: Boolean
          val _tmp_11: Int
          _tmp_11 = _stmt.getLong(_columnIndexOfIsBreedingUnit).toInt()
          _tmpIsBreedingUnit = _tmp_11 != 0
          val _tmpEggsCollectedToday: Int
          _tmpEggsCollectedToday = _stmt.getLong(_columnIndexOfEggsCollectedToday).toInt()
          val _tmpLastEggLogDate: Long?
          if (_stmt.isNull(_columnIndexOfLastEggLogDate)) {
            _tmpLastEggLogDate = null
          } else {
            _tmpLastEggLogDate = _stmt.getLong(_columnIndexOfLastEggLogDate)
          }
          val _tmpReadyForSale: Boolean
          val _tmp_12: Int
          _tmp_12 = _stmt.getLong(_columnIndexOfReadyForSale).toInt()
          _tmpReadyForSale = _tmp_12 != 0
          val _tmpTargetWeight: Double?
          if (_stmt.isNull(_columnIndexOfTargetWeight)) {
            _tmpTargetWeight = null
          } else {
            _tmpTargetWeight = _stmt.getDouble(_columnIndexOfTargetWeight)
          }
          val _tmpIsShowcased: Boolean
          val _tmp_13: Int
          _tmp_13 = _stmt.getLong(_columnIndexOfIsShowcased).toInt()
          _tmpIsShowcased = _tmp_13 != 0
          val _tmpExternalVideoUrl: String?
          if (_stmt.isNull(_columnIndexOfExternalVideoUrl)) {
            _tmpExternalVideoUrl = null
          } else {
            _tmpExternalVideoUrl = _stmt.getText(_columnIndexOfExternalVideoUrl)
          }
          val _tmpRecordsLockedAt: Long?
          if (_stmt.isNull(_columnIndexOfRecordsLockedAt)) {
            _tmpRecordsLockedAt = null
          } else {
            _tmpRecordsLockedAt = _stmt.getLong(_columnIndexOfRecordsLockedAt)
          }
          val _tmpAutoLockAfterDays: Int
          _tmpAutoLockAfterDays = _stmt.getLong(_columnIndexOfAutoLockAfterDays).toInt()
          val _tmpLineageHistoryJson: String?
          if (_stmt.isNull(_columnIndexOfLineageHistoryJson)) {
            _tmpLineageHistoryJson = null
          } else {
            _tmpLineageHistoryJson = _stmt.getText(_columnIndexOfLineageHistoryJson)
          }
          val _tmpEditCount: Int
          _tmpEditCount = _stmt.getLong(_columnIndexOfEditCount).toInt()
          val _tmpLastEditedBy: String?
          if (_stmt.isNull(_columnIndexOfLastEditedBy)) {
            _tmpLastEditedBy = null
          } else {
            _tmpLastEditedBy = _stmt.getText(_columnIndexOfLastEditedBy)
          }
          val _tmpAdminFlagged: Boolean
          val _tmp_14: Int
          _tmp_14 = _stmt.getLong(_columnIndexOfAdminFlagged).toInt()
          _tmpAdminFlagged = _tmp_14 != 0
          val _tmpModerationNote: String?
          if (_stmt.isNull(_columnIndexOfModerationNote)) {
            _tmpModerationNote = null
          } else {
            _tmpModerationNote = _stmt.getText(_columnIndexOfModerationNote)
          }
          val _tmpMetadataJson: String
          _tmpMetadataJson = _stmt.getText(_columnIndexOfMetadataJson)
          _item =
              ProductEntity(_tmpProductId,_tmpSellerId,_tmpName,_tmpDescription,_tmpCategory,_tmpPrice,_tmpQuantity,_tmpUnit,_tmpLocation,_tmpLatitude,_tmpLongitude,_tmpImageUrls,_tmpStatus,_tmpCondition,_tmpHarvestDate,_tmpExpiryDate,_tmpBirthDate,_tmpVaccinationRecordsJson,_tmpWeightGrams,_tmpHeightCm,_tmpGender,_tmpColor,_tmpBreed,_tmpRaisingPurpose,_tmpHealthStatus,_tmpBirdCode,_tmpColorTag,_tmpFamilyTreeId,_tmpSourceAssetId,_tmpParentIdsJson,_tmpBreedingStatus,_tmpTransferHistoryJson,_tmpCreatedAt,_tmpUpdatedAt,_tmpLastModifiedAt,_tmpIsDeleted,_tmpDeletedAt,_tmpDirty,_tmpStage,_tmpLifecycleStatus,_tmpParentMaleId,_tmpParentFemaleId,_tmpAgeWeeks,_tmpLastStageTransitionAt,_tmpBreederEligibleAt,_tmpIsBatch,_tmpBatchId,_tmpSplitAt,_tmpSplitIntoIds,_tmpDocumentUrls,_tmpQrCodeUrl,_tmpCustomStatus,_tmpDebug,_tmpDeliveryOptions,_tmpDeliveryCost,_tmpLeadTimeDays,_tmpMotherId,_tmpIsBreedingUnit,_tmpEggsCollectedToday,_tmpLastEggLogDate,_tmpReadyForSale,_tmpTargetWeight,_tmpIsShowcased,_tmpExternalVideoUrl,_tmpRecordsLockedAt,_tmpAutoLockAfterDays,_tmpLineageHistoryJson,_tmpEditCount,_tmpLastEditedBy,_tmpAdminFlagged,_tmpModerationNote,_tmpMetadataJson)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getActiveWithBirth(): List<ProductEntity> {
    val _sql: String =
        "SELECT * FROM products WHERE (lifecycleStatus = 'ACTIVE' OR lifecycleStatus IS NULL) AND birthDate IS NOT NULL"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfSellerId: Int = getColumnIndexOrThrow(_stmt, "sellerId")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfCategory: Int = getColumnIndexOrThrow(_stmt, "category")
        val _columnIndexOfPrice: Int = getColumnIndexOrThrow(_stmt, "price")
        val _columnIndexOfQuantity: Int = getColumnIndexOrThrow(_stmt, "quantity")
        val _columnIndexOfUnit: Int = getColumnIndexOrThrow(_stmt, "unit")
        val _columnIndexOfLocation: Int = getColumnIndexOrThrow(_stmt, "location")
        val _columnIndexOfLatitude: Int = getColumnIndexOrThrow(_stmt, "latitude")
        val _columnIndexOfLongitude: Int = getColumnIndexOrThrow(_stmt, "longitude")
        val _columnIndexOfImageUrls: Int = getColumnIndexOrThrow(_stmt, "imageUrls")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfCondition: Int = getColumnIndexOrThrow(_stmt, "condition")
        val _columnIndexOfHarvestDate: Int = getColumnIndexOrThrow(_stmt, "harvestDate")
        val _columnIndexOfExpiryDate: Int = getColumnIndexOrThrow(_stmt, "expiryDate")
        val _columnIndexOfBirthDate: Int = getColumnIndexOrThrow(_stmt, "birthDate")
        val _columnIndexOfVaccinationRecordsJson: Int = getColumnIndexOrThrow(_stmt,
            "vaccinationRecordsJson")
        val _columnIndexOfWeightGrams: Int = getColumnIndexOrThrow(_stmt, "weightGrams")
        val _columnIndexOfHeightCm: Int = getColumnIndexOrThrow(_stmt, "heightCm")
        val _columnIndexOfGender: Int = getColumnIndexOrThrow(_stmt, "gender")
        val _columnIndexOfColor: Int = getColumnIndexOrThrow(_stmt, "color")
        val _columnIndexOfBreed: Int = getColumnIndexOrThrow(_stmt, "breed")
        val _columnIndexOfRaisingPurpose: Int = getColumnIndexOrThrow(_stmt, "raisingPurpose")
        val _columnIndexOfHealthStatus: Int = getColumnIndexOrThrow(_stmt, "healthStatus")
        val _columnIndexOfBirdCode: Int = getColumnIndexOrThrow(_stmt, "birdCode")
        val _columnIndexOfColorTag: Int = getColumnIndexOrThrow(_stmt, "colorTag")
        val _columnIndexOfFamilyTreeId: Int = getColumnIndexOrThrow(_stmt, "familyTreeId")
        val _columnIndexOfSourceAssetId: Int = getColumnIndexOrThrow(_stmt, "sourceAssetId")
        val _columnIndexOfParentIdsJson: Int = getColumnIndexOrThrow(_stmt, "parentIdsJson")
        val _columnIndexOfBreedingStatus: Int = getColumnIndexOrThrow(_stmt, "breedingStatus")
        val _columnIndexOfTransferHistoryJson: Int = getColumnIndexOrThrow(_stmt,
            "transferHistoryJson")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfLastModifiedAt: Int = getColumnIndexOrThrow(_stmt, "lastModifiedAt")
        val _columnIndexOfIsDeleted: Int = getColumnIndexOrThrow(_stmt, "isDeleted")
        val _columnIndexOfDeletedAt: Int = getColumnIndexOrThrow(_stmt, "deletedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfStage: Int = getColumnIndexOrThrow(_stmt, "stage")
        val _columnIndexOfLifecycleStatus: Int = getColumnIndexOrThrow(_stmt, "lifecycleStatus")
        val _columnIndexOfParentMaleId: Int = getColumnIndexOrThrow(_stmt, "parentMaleId")
        val _columnIndexOfParentFemaleId: Int = getColumnIndexOrThrow(_stmt, "parentFemaleId")
        val _columnIndexOfAgeWeeks: Int = getColumnIndexOrThrow(_stmt, "ageWeeks")
        val _columnIndexOfLastStageTransitionAt: Int = getColumnIndexOrThrow(_stmt,
            "lastStageTransitionAt")
        val _columnIndexOfBreederEligibleAt: Int = getColumnIndexOrThrow(_stmt, "breederEligibleAt")
        val _columnIndexOfIsBatch: Int = getColumnIndexOrThrow(_stmt, "isBatch")
        val _columnIndexOfBatchId: Int = getColumnIndexOrThrow(_stmt, "batchId")
        val _columnIndexOfSplitAt: Int = getColumnIndexOrThrow(_stmt, "splitAt")
        val _columnIndexOfSplitIntoIds: Int = getColumnIndexOrThrow(_stmt, "splitIntoIds")
        val _columnIndexOfDocumentUrls: Int = getColumnIndexOrThrow(_stmt, "documentUrls")
        val _columnIndexOfQrCodeUrl: Int = getColumnIndexOrThrow(_stmt, "qrCodeUrl")
        val _columnIndexOfCustomStatus: Int = getColumnIndexOrThrow(_stmt, "customStatus")
        val _columnIndexOfDebug: Int = getColumnIndexOrThrow(_stmt, "debug")
        val _columnIndexOfDeliveryOptions: Int = getColumnIndexOrThrow(_stmt, "deliveryOptions")
        val _columnIndexOfDeliveryCost: Int = getColumnIndexOrThrow(_stmt, "deliveryCost")
        val _columnIndexOfLeadTimeDays: Int = getColumnIndexOrThrow(_stmt, "leadTimeDays")
        val _columnIndexOfMotherId: Int = getColumnIndexOrThrow(_stmt, "motherId")
        val _columnIndexOfIsBreedingUnit: Int = getColumnIndexOrThrow(_stmt, "isBreedingUnit")
        val _columnIndexOfEggsCollectedToday: Int = getColumnIndexOrThrow(_stmt,
            "eggsCollectedToday")
        val _columnIndexOfLastEggLogDate: Int = getColumnIndexOrThrow(_stmt, "lastEggLogDate")
        val _columnIndexOfReadyForSale: Int = getColumnIndexOrThrow(_stmt, "readyForSale")
        val _columnIndexOfTargetWeight: Int = getColumnIndexOrThrow(_stmt, "targetWeight")
        val _columnIndexOfIsShowcased: Int = getColumnIndexOrThrow(_stmt, "isShowcased")
        val _columnIndexOfExternalVideoUrl: Int = getColumnIndexOrThrow(_stmt, "externalVideoUrl")
        val _columnIndexOfRecordsLockedAt: Int = getColumnIndexOrThrow(_stmt, "recordsLockedAt")
        val _columnIndexOfAutoLockAfterDays: Int = getColumnIndexOrThrow(_stmt, "autoLockAfterDays")
        val _columnIndexOfLineageHistoryJson: Int = getColumnIndexOrThrow(_stmt,
            "lineageHistoryJson")
        val _columnIndexOfEditCount: Int = getColumnIndexOrThrow(_stmt, "editCount")
        val _columnIndexOfLastEditedBy: Int = getColumnIndexOrThrow(_stmt, "lastEditedBy")
        val _columnIndexOfAdminFlagged: Int = getColumnIndexOrThrow(_stmt, "adminFlagged")
        val _columnIndexOfModerationNote: Int = getColumnIndexOrThrow(_stmt, "moderationNote")
        val _columnIndexOfMetadataJson: Int = getColumnIndexOrThrow(_stmt, "metadataJson")
        val _result: MutableList<ProductEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: ProductEntity
          val _tmpProductId: String
          _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          val _tmpSellerId: String
          _tmpSellerId = _stmt.getText(_columnIndexOfSellerId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpDescription: String
          _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          val _tmpCategory: String
          _tmpCategory = _stmt.getText(_columnIndexOfCategory)
          val _tmpPrice: Double
          _tmpPrice = _stmt.getDouble(_columnIndexOfPrice)
          val _tmpQuantity: Double
          _tmpQuantity = _stmt.getDouble(_columnIndexOfQuantity)
          val _tmpUnit: String
          _tmpUnit = _stmt.getText(_columnIndexOfUnit)
          val _tmpLocation: String
          _tmpLocation = _stmt.getText(_columnIndexOfLocation)
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
          val _tmpImageUrls: List<String>
          val _tmp: String?
          if (_stmt.isNull(_columnIndexOfImageUrls)) {
            _tmp = null
          } else {
            _tmp = _stmt.getText(_columnIndexOfImageUrls)
          }
          val _tmp_1: List<String>? = AppDatabase.Converters.toStringList(_tmp)
          if (_tmp_1 == null) {
            error("Expected NON-NULL 'kotlin.collections.List<kotlin.String>', but it was NULL.")
          } else {
            _tmpImageUrls = _tmp_1
          }
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpCondition: String?
          if (_stmt.isNull(_columnIndexOfCondition)) {
            _tmpCondition = null
          } else {
            _tmpCondition = _stmt.getText(_columnIndexOfCondition)
          }
          val _tmpHarvestDate: Long?
          if (_stmt.isNull(_columnIndexOfHarvestDate)) {
            _tmpHarvestDate = null
          } else {
            _tmpHarvestDate = _stmt.getLong(_columnIndexOfHarvestDate)
          }
          val _tmpExpiryDate: Long?
          if (_stmt.isNull(_columnIndexOfExpiryDate)) {
            _tmpExpiryDate = null
          } else {
            _tmpExpiryDate = _stmt.getLong(_columnIndexOfExpiryDate)
          }
          val _tmpBirthDate: Long?
          if (_stmt.isNull(_columnIndexOfBirthDate)) {
            _tmpBirthDate = null
          } else {
            _tmpBirthDate = _stmt.getLong(_columnIndexOfBirthDate)
          }
          val _tmpVaccinationRecordsJson: String?
          if (_stmt.isNull(_columnIndexOfVaccinationRecordsJson)) {
            _tmpVaccinationRecordsJson = null
          } else {
            _tmpVaccinationRecordsJson = _stmt.getText(_columnIndexOfVaccinationRecordsJson)
          }
          val _tmpWeightGrams: Double?
          if (_stmt.isNull(_columnIndexOfWeightGrams)) {
            _tmpWeightGrams = null
          } else {
            _tmpWeightGrams = _stmt.getDouble(_columnIndexOfWeightGrams)
          }
          val _tmpHeightCm: Double?
          if (_stmt.isNull(_columnIndexOfHeightCm)) {
            _tmpHeightCm = null
          } else {
            _tmpHeightCm = _stmt.getDouble(_columnIndexOfHeightCm)
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
          val _tmpBreed: String?
          if (_stmt.isNull(_columnIndexOfBreed)) {
            _tmpBreed = null
          } else {
            _tmpBreed = _stmt.getText(_columnIndexOfBreed)
          }
          val _tmpRaisingPurpose: String?
          if (_stmt.isNull(_columnIndexOfRaisingPurpose)) {
            _tmpRaisingPurpose = null
          } else {
            _tmpRaisingPurpose = _stmt.getText(_columnIndexOfRaisingPurpose)
          }
          val _tmpHealthStatus: String?
          if (_stmt.isNull(_columnIndexOfHealthStatus)) {
            _tmpHealthStatus = null
          } else {
            _tmpHealthStatus = _stmt.getText(_columnIndexOfHealthStatus)
          }
          val _tmpBirdCode: String?
          if (_stmt.isNull(_columnIndexOfBirdCode)) {
            _tmpBirdCode = null
          } else {
            _tmpBirdCode = _stmt.getText(_columnIndexOfBirdCode)
          }
          val _tmpColorTag: String?
          if (_stmt.isNull(_columnIndexOfColorTag)) {
            _tmpColorTag = null
          } else {
            _tmpColorTag = _stmt.getText(_columnIndexOfColorTag)
          }
          val _tmpFamilyTreeId: String?
          if (_stmt.isNull(_columnIndexOfFamilyTreeId)) {
            _tmpFamilyTreeId = null
          } else {
            _tmpFamilyTreeId = _stmt.getText(_columnIndexOfFamilyTreeId)
          }
          val _tmpSourceAssetId: String?
          if (_stmt.isNull(_columnIndexOfSourceAssetId)) {
            _tmpSourceAssetId = null
          } else {
            _tmpSourceAssetId = _stmt.getText(_columnIndexOfSourceAssetId)
          }
          val _tmpParentIdsJson: String?
          if (_stmt.isNull(_columnIndexOfParentIdsJson)) {
            _tmpParentIdsJson = null
          } else {
            _tmpParentIdsJson = _stmt.getText(_columnIndexOfParentIdsJson)
          }
          val _tmpBreedingStatus: String?
          if (_stmt.isNull(_columnIndexOfBreedingStatus)) {
            _tmpBreedingStatus = null
          } else {
            _tmpBreedingStatus = _stmt.getText(_columnIndexOfBreedingStatus)
          }
          val _tmpTransferHistoryJson: String?
          if (_stmt.isNull(_columnIndexOfTransferHistoryJson)) {
            _tmpTransferHistoryJson = null
          } else {
            _tmpTransferHistoryJson = _stmt.getText(_columnIndexOfTransferHistoryJson)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpLastModifiedAt: Long
          _tmpLastModifiedAt = _stmt.getLong(_columnIndexOfLastModifiedAt)
          val _tmpIsDeleted: Boolean
          val _tmp_2: Int
          _tmp_2 = _stmt.getLong(_columnIndexOfIsDeleted).toInt()
          _tmpIsDeleted = _tmp_2 != 0
          val _tmpDeletedAt: Long?
          if (_stmt.isNull(_columnIndexOfDeletedAt)) {
            _tmpDeletedAt = null
          } else {
            _tmpDeletedAt = _stmt.getLong(_columnIndexOfDeletedAt)
          }
          val _tmpDirty: Boolean
          val _tmp_3: Int
          _tmp_3 = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp_3 != 0
          val _tmpStage: LifecycleStage?
          val _tmp_4: String?
          if (_stmt.isNull(_columnIndexOfStage)) {
            _tmp_4 = null
          } else {
            _tmp_4 = _stmt.getText(_columnIndexOfStage)
          }
          _tmpStage = AppDatabase.Converters.toLifecycleStage(_tmp_4)
          val _tmpLifecycleStatus: String?
          if (_stmt.isNull(_columnIndexOfLifecycleStatus)) {
            _tmpLifecycleStatus = null
          } else {
            _tmpLifecycleStatus = _stmt.getText(_columnIndexOfLifecycleStatus)
          }
          val _tmpParentMaleId: String?
          if (_stmt.isNull(_columnIndexOfParentMaleId)) {
            _tmpParentMaleId = null
          } else {
            _tmpParentMaleId = _stmt.getText(_columnIndexOfParentMaleId)
          }
          val _tmpParentFemaleId: String?
          if (_stmt.isNull(_columnIndexOfParentFemaleId)) {
            _tmpParentFemaleId = null
          } else {
            _tmpParentFemaleId = _stmt.getText(_columnIndexOfParentFemaleId)
          }
          val _tmpAgeWeeks: Int?
          if (_stmt.isNull(_columnIndexOfAgeWeeks)) {
            _tmpAgeWeeks = null
          } else {
            _tmpAgeWeeks = _stmt.getLong(_columnIndexOfAgeWeeks).toInt()
          }
          val _tmpLastStageTransitionAt: Long?
          if (_stmt.isNull(_columnIndexOfLastStageTransitionAt)) {
            _tmpLastStageTransitionAt = null
          } else {
            _tmpLastStageTransitionAt = _stmt.getLong(_columnIndexOfLastStageTransitionAt)
          }
          val _tmpBreederEligibleAt: Long?
          if (_stmt.isNull(_columnIndexOfBreederEligibleAt)) {
            _tmpBreederEligibleAt = null
          } else {
            _tmpBreederEligibleAt = _stmt.getLong(_columnIndexOfBreederEligibleAt)
          }
          val _tmpIsBatch: Boolean?
          val _tmp_5: Int?
          if (_stmt.isNull(_columnIndexOfIsBatch)) {
            _tmp_5 = null
          } else {
            _tmp_5 = _stmt.getLong(_columnIndexOfIsBatch).toInt()
          }
          _tmpIsBatch = _tmp_5?.let { it != 0 }
          val _tmpBatchId: String?
          if (_stmt.isNull(_columnIndexOfBatchId)) {
            _tmpBatchId = null
          } else {
            _tmpBatchId = _stmt.getText(_columnIndexOfBatchId)
          }
          val _tmpSplitAt: Long?
          if (_stmt.isNull(_columnIndexOfSplitAt)) {
            _tmpSplitAt = null
          } else {
            _tmpSplitAt = _stmt.getLong(_columnIndexOfSplitAt)
          }
          val _tmpSplitIntoIds: String?
          if (_stmt.isNull(_columnIndexOfSplitIntoIds)) {
            _tmpSplitIntoIds = null
          } else {
            _tmpSplitIntoIds = _stmt.getText(_columnIndexOfSplitIntoIds)
          }
          val _tmpDocumentUrls: List<String>
          val _tmp_6: String?
          if (_stmt.isNull(_columnIndexOfDocumentUrls)) {
            _tmp_6 = null
          } else {
            _tmp_6 = _stmt.getText(_columnIndexOfDocumentUrls)
          }
          val _tmp_7: List<String>? = AppDatabase.Converters.toStringList(_tmp_6)
          if (_tmp_7 == null) {
            error("Expected NON-NULL 'kotlin.collections.List<kotlin.String>', but it was NULL.")
          } else {
            _tmpDocumentUrls = _tmp_7
          }
          val _tmpQrCodeUrl: String?
          if (_stmt.isNull(_columnIndexOfQrCodeUrl)) {
            _tmpQrCodeUrl = null
          } else {
            _tmpQrCodeUrl = _stmt.getText(_columnIndexOfQrCodeUrl)
          }
          val _tmpCustomStatus: String?
          if (_stmt.isNull(_columnIndexOfCustomStatus)) {
            _tmpCustomStatus = null
          } else {
            _tmpCustomStatus = _stmt.getText(_columnIndexOfCustomStatus)
          }
          val _tmpDebug: Boolean
          val _tmp_8: Int
          _tmp_8 = _stmt.getLong(_columnIndexOfDebug).toInt()
          _tmpDebug = _tmp_8 != 0
          val _tmpDeliveryOptions: List<String>
          val _tmp_9: String?
          if (_stmt.isNull(_columnIndexOfDeliveryOptions)) {
            _tmp_9 = null
          } else {
            _tmp_9 = _stmt.getText(_columnIndexOfDeliveryOptions)
          }
          val _tmp_10: List<String>? = AppDatabase.Converters.toStringList(_tmp_9)
          if (_tmp_10 == null) {
            error("Expected NON-NULL 'kotlin.collections.List<kotlin.String>', but it was NULL.")
          } else {
            _tmpDeliveryOptions = _tmp_10
          }
          val _tmpDeliveryCost: Double?
          if (_stmt.isNull(_columnIndexOfDeliveryCost)) {
            _tmpDeliveryCost = null
          } else {
            _tmpDeliveryCost = _stmt.getDouble(_columnIndexOfDeliveryCost)
          }
          val _tmpLeadTimeDays: Int?
          if (_stmt.isNull(_columnIndexOfLeadTimeDays)) {
            _tmpLeadTimeDays = null
          } else {
            _tmpLeadTimeDays = _stmt.getLong(_columnIndexOfLeadTimeDays).toInt()
          }
          val _tmpMotherId: String?
          if (_stmt.isNull(_columnIndexOfMotherId)) {
            _tmpMotherId = null
          } else {
            _tmpMotherId = _stmt.getText(_columnIndexOfMotherId)
          }
          val _tmpIsBreedingUnit: Boolean
          val _tmp_11: Int
          _tmp_11 = _stmt.getLong(_columnIndexOfIsBreedingUnit).toInt()
          _tmpIsBreedingUnit = _tmp_11 != 0
          val _tmpEggsCollectedToday: Int
          _tmpEggsCollectedToday = _stmt.getLong(_columnIndexOfEggsCollectedToday).toInt()
          val _tmpLastEggLogDate: Long?
          if (_stmt.isNull(_columnIndexOfLastEggLogDate)) {
            _tmpLastEggLogDate = null
          } else {
            _tmpLastEggLogDate = _stmt.getLong(_columnIndexOfLastEggLogDate)
          }
          val _tmpReadyForSale: Boolean
          val _tmp_12: Int
          _tmp_12 = _stmt.getLong(_columnIndexOfReadyForSale).toInt()
          _tmpReadyForSale = _tmp_12 != 0
          val _tmpTargetWeight: Double?
          if (_stmt.isNull(_columnIndexOfTargetWeight)) {
            _tmpTargetWeight = null
          } else {
            _tmpTargetWeight = _stmt.getDouble(_columnIndexOfTargetWeight)
          }
          val _tmpIsShowcased: Boolean
          val _tmp_13: Int
          _tmp_13 = _stmt.getLong(_columnIndexOfIsShowcased).toInt()
          _tmpIsShowcased = _tmp_13 != 0
          val _tmpExternalVideoUrl: String?
          if (_stmt.isNull(_columnIndexOfExternalVideoUrl)) {
            _tmpExternalVideoUrl = null
          } else {
            _tmpExternalVideoUrl = _stmt.getText(_columnIndexOfExternalVideoUrl)
          }
          val _tmpRecordsLockedAt: Long?
          if (_stmt.isNull(_columnIndexOfRecordsLockedAt)) {
            _tmpRecordsLockedAt = null
          } else {
            _tmpRecordsLockedAt = _stmt.getLong(_columnIndexOfRecordsLockedAt)
          }
          val _tmpAutoLockAfterDays: Int
          _tmpAutoLockAfterDays = _stmt.getLong(_columnIndexOfAutoLockAfterDays).toInt()
          val _tmpLineageHistoryJson: String?
          if (_stmt.isNull(_columnIndexOfLineageHistoryJson)) {
            _tmpLineageHistoryJson = null
          } else {
            _tmpLineageHistoryJson = _stmt.getText(_columnIndexOfLineageHistoryJson)
          }
          val _tmpEditCount: Int
          _tmpEditCount = _stmt.getLong(_columnIndexOfEditCount).toInt()
          val _tmpLastEditedBy: String?
          if (_stmt.isNull(_columnIndexOfLastEditedBy)) {
            _tmpLastEditedBy = null
          } else {
            _tmpLastEditedBy = _stmt.getText(_columnIndexOfLastEditedBy)
          }
          val _tmpAdminFlagged: Boolean
          val _tmp_14: Int
          _tmp_14 = _stmt.getLong(_columnIndexOfAdminFlagged).toInt()
          _tmpAdminFlagged = _tmp_14 != 0
          val _tmpModerationNote: String?
          if (_stmt.isNull(_columnIndexOfModerationNote)) {
            _tmpModerationNote = null
          } else {
            _tmpModerationNote = _stmt.getText(_columnIndexOfModerationNote)
          }
          val _tmpMetadataJson: String
          _tmpMetadataJson = _stmt.getText(_columnIndexOfMetadataJson)
          _item =
              ProductEntity(_tmpProductId,_tmpSellerId,_tmpName,_tmpDescription,_tmpCategory,_tmpPrice,_tmpQuantity,_tmpUnit,_tmpLocation,_tmpLatitude,_tmpLongitude,_tmpImageUrls,_tmpStatus,_tmpCondition,_tmpHarvestDate,_tmpExpiryDate,_tmpBirthDate,_tmpVaccinationRecordsJson,_tmpWeightGrams,_tmpHeightCm,_tmpGender,_tmpColor,_tmpBreed,_tmpRaisingPurpose,_tmpHealthStatus,_tmpBirdCode,_tmpColorTag,_tmpFamilyTreeId,_tmpSourceAssetId,_tmpParentIdsJson,_tmpBreedingStatus,_tmpTransferHistoryJson,_tmpCreatedAt,_tmpUpdatedAt,_tmpLastModifiedAt,_tmpIsDeleted,_tmpDeletedAt,_tmpDirty,_tmpStage,_tmpLifecycleStatus,_tmpParentMaleId,_tmpParentFemaleId,_tmpAgeWeeks,_tmpLastStageTransitionAt,_tmpBreederEligibleAt,_tmpIsBatch,_tmpBatchId,_tmpSplitAt,_tmpSplitIntoIds,_tmpDocumentUrls,_tmpQrCodeUrl,_tmpCustomStatus,_tmpDebug,_tmpDeliveryOptions,_tmpDeliveryCost,_tmpLeadTimeDays,_tmpMotherId,_tmpIsBreedingUnit,_tmpEggsCollectedToday,_tmpLastEggLogDate,_tmpReadyForSale,_tmpTargetWeight,_tmpIsShowcased,_tmpExternalVideoUrl,_tmpRecordsLockedAt,_tmpAutoLockAfterDays,_tmpLineageHistoryJson,_tmpEditCount,_tmpLastEditedBy,_tmpAdminFlagged,_tmpModerationNote,_tmpMetadataJson)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getAllBirdsWithBirthDate(): List<ProductEntity> {
    val _sql: String = "SELECT * FROM products WHERE birthDate IS NOT NULL AND isDeleted = 0"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfSellerId: Int = getColumnIndexOrThrow(_stmt, "sellerId")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfCategory: Int = getColumnIndexOrThrow(_stmt, "category")
        val _columnIndexOfPrice: Int = getColumnIndexOrThrow(_stmt, "price")
        val _columnIndexOfQuantity: Int = getColumnIndexOrThrow(_stmt, "quantity")
        val _columnIndexOfUnit: Int = getColumnIndexOrThrow(_stmt, "unit")
        val _columnIndexOfLocation: Int = getColumnIndexOrThrow(_stmt, "location")
        val _columnIndexOfLatitude: Int = getColumnIndexOrThrow(_stmt, "latitude")
        val _columnIndexOfLongitude: Int = getColumnIndexOrThrow(_stmt, "longitude")
        val _columnIndexOfImageUrls: Int = getColumnIndexOrThrow(_stmt, "imageUrls")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfCondition: Int = getColumnIndexOrThrow(_stmt, "condition")
        val _columnIndexOfHarvestDate: Int = getColumnIndexOrThrow(_stmt, "harvestDate")
        val _columnIndexOfExpiryDate: Int = getColumnIndexOrThrow(_stmt, "expiryDate")
        val _columnIndexOfBirthDate: Int = getColumnIndexOrThrow(_stmt, "birthDate")
        val _columnIndexOfVaccinationRecordsJson: Int = getColumnIndexOrThrow(_stmt,
            "vaccinationRecordsJson")
        val _columnIndexOfWeightGrams: Int = getColumnIndexOrThrow(_stmt, "weightGrams")
        val _columnIndexOfHeightCm: Int = getColumnIndexOrThrow(_stmt, "heightCm")
        val _columnIndexOfGender: Int = getColumnIndexOrThrow(_stmt, "gender")
        val _columnIndexOfColor: Int = getColumnIndexOrThrow(_stmt, "color")
        val _columnIndexOfBreed: Int = getColumnIndexOrThrow(_stmt, "breed")
        val _columnIndexOfRaisingPurpose: Int = getColumnIndexOrThrow(_stmt, "raisingPurpose")
        val _columnIndexOfHealthStatus: Int = getColumnIndexOrThrow(_stmt, "healthStatus")
        val _columnIndexOfBirdCode: Int = getColumnIndexOrThrow(_stmt, "birdCode")
        val _columnIndexOfColorTag: Int = getColumnIndexOrThrow(_stmt, "colorTag")
        val _columnIndexOfFamilyTreeId: Int = getColumnIndexOrThrow(_stmt, "familyTreeId")
        val _columnIndexOfSourceAssetId: Int = getColumnIndexOrThrow(_stmt, "sourceAssetId")
        val _columnIndexOfParentIdsJson: Int = getColumnIndexOrThrow(_stmt, "parentIdsJson")
        val _columnIndexOfBreedingStatus: Int = getColumnIndexOrThrow(_stmt, "breedingStatus")
        val _columnIndexOfTransferHistoryJson: Int = getColumnIndexOrThrow(_stmt,
            "transferHistoryJson")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfLastModifiedAt: Int = getColumnIndexOrThrow(_stmt, "lastModifiedAt")
        val _columnIndexOfIsDeleted: Int = getColumnIndexOrThrow(_stmt, "isDeleted")
        val _columnIndexOfDeletedAt: Int = getColumnIndexOrThrow(_stmt, "deletedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfStage: Int = getColumnIndexOrThrow(_stmt, "stage")
        val _columnIndexOfLifecycleStatus: Int = getColumnIndexOrThrow(_stmt, "lifecycleStatus")
        val _columnIndexOfParentMaleId: Int = getColumnIndexOrThrow(_stmt, "parentMaleId")
        val _columnIndexOfParentFemaleId: Int = getColumnIndexOrThrow(_stmt, "parentFemaleId")
        val _columnIndexOfAgeWeeks: Int = getColumnIndexOrThrow(_stmt, "ageWeeks")
        val _columnIndexOfLastStageTransitionAt: Int = getColumnIndexOrThrow(_stmt,
            "lastStageTransitionAt")
        val _columnIndexOfBreederEligibleAt: Int = getColumnIndexOrThrow(_stmt, "breederEligibleAt")
        val _columnIndexOfIsBatch: Int = getColumnIndexOrThrow(_stmt, "isBatch")
        val _columnIndexOfBatchId: Int = getColumnIndexOrThrow(_stmt, "batchId")
        val _columnIndexOfSplitAt: Int = getColumnIndexOrThrow(_stmt, "splitAt")
        val _columnIndexOfSplitIntoIds: Int = getColumnIndexOrThrow(_stmt, "splitIntoIds")
        val _columnIndexOfDocumentUrls: Int = getColumnIndexOrThrow(_stmt, "documentUrls")
        val _columnIndexOfQrCodeUrl: Int = getColumnIndexOrThrow(_stmt, "qrCodeUrl")
        val _columnIndexOfCustomStatus: Int = getColumnIndexOrThrow(_stmt, "customStatus")
        val _columnIndexOfDebug: Int = getColumnIndexOrThrow(_stmt, "debug")
        val _columnIndexOfDeliveryOptions: Int = getColumnIndexOrThrow(_stmt, "deliveryOptions")
        val _columnIndexOfDeliveryCost: Int = getColumnIndexOrThrow(_stmt, "deliveryCost")
        val _columnIndexOfLeadTimeDays: Int = getColumnIndexOrThrow(_stmt, "leadTimeDays")
        val _columnIndexOfMotherId: Int = getColumnIndexOrThrow(_stmt, "motherId")
        val _columnIndexOfIsBreedingUnit: Int = getColumnIndexOrThrow(_stmt, "isBreedingUnit")
        val _columnIndexOfEggsCollectedToday: Int = getColumnIndexOrThrow(_stmt,
            "eggsCollectedToday")
        val _columnIndexOfLastEggLogDate: Int = getColumnIndexOrThrow(_stmt, "lastEggLogDate")
        val _columnIndexOfReadyForSale: Int = getColumnIndexOrThrow(_stmt, "readyForSale")
        val _columnIndexOfTargetWeight: Int = getColumnIndexOrThrow(_stmt, "targetWeight")
        val _columnIndexOfIsShowcased: Int = getColumnIndexOrThrow(_stmt, "isShowcased")
        val _columnIndexOfExternalVideoUrl: Int = getColumnIndexOrThrow(_stmt, "externalVideoUrl")
        val _columnIndexOfRecordsLockedAt: Int = getColumnIndexOrThrow(_stmt, "recordsLockedAt")
        val _columnIndexOfAutoLockAfterDays: Int = getColumnIndexOrThrow(_stmt, "autoLockAfterDays")
        val _columnIndexOfLineageHistoryJson: Int = getColumnIndexOrThrow(_stmt,
            "lineageHistoryJson")
        val _columnIndexOfEditCount: Int = getColumnIndexOrThrow(_stmt, "editCount")
        val _columnIndexOfLastEditedBy: Int = getColumnIndexOrThrow(_stmt, "lastEditedBy")
        val _columnIndexOfAdminFlagged: Int = getColumnIndexOrThrow(_stmt, "adminFlagged")
        val _columnIndexOfModerationNote: Int = getColumnIndexOrThrow(_stmt, "moderationNote")
        val _columnIndexOfMetadataJson: Int = getColumnIndexOrThrow(_stmt, "metadataJson")
        val _result: MutableList<ProductEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: ProductEntity
          val _tmpProductId: String
          _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          val _tmpSellerId: String
          _tmpSellerId = _stmt.getText(_columnIndexOfSellerId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpDescription: String
          _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          val _tmpCategory: String
          _tmpCategory = _stmt.getText(_columnIndexOfCategory)
          val _tmpPrice: Double
          _tmpPrice = _stmt.getDouble(_columnIndexOfPrice)
          val _tmpQuantity: Double
          _tmpQuantity = _stmt.getDouble(_columnIndexOfQuantity)
          val _tmpUnit: String
          _tmpUnit = _stmt.getText(_columnIndexOfUnit)
          val _tmpLocation: String
          _tmpLocation = _stmt.getText(_columnIndexOfLocation)
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
          val _tmpImageUrls: List<String>
          val _tmp: String?
          if (_stmt.isNull(_columnIndexOfImageUrls)) {
            _tmp = null
          } else {
            _tmp = _stmt.getText(_columnIndexOfImageUrls)
          }
          val _tmp_1: List<String>? = AppDatabase.Converters.toStringList(_tmp)
          if (_tmp_1 == null) {
            error("Expected NON-NULL 'kotlin.collections.List<kotlin.String>', but it was NULL.")
          } else {
            _tmpImageUrls = _tmp_1
          }
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpCondition: String?
          if (_stmt.isNull(_columnIndexOfCondition)) {
            _tmpCondition = null
          } else {
            _tmpCondition = _stmt.getText(_columnIndexOfCondition)
          }
          val _tmpHarvestDate: Long?
          if (_stmt.isNull(_columnIndexOfHarvestDate)) {
            _tmpHarvestDate = null
          } else {
            _tmpHarvestDate = _stmt.getLong(_columnIndexOfHarvestDate)
          }
          val _tmpExpiryDate: Long?
          if (_stmt.isNull(_columnIndexOfExpiryDate)) {
            _tmpExpiryDate = null
          } else {
            _tmpExpiryDate = _stmt.getLong(_columnIndexOfExpiryDate)
          }
          val _tmpBirthDate: Long?
          if (_stmt.isNull(_columnIndexOfBirthDate)) {
            _tmpBirthDate = null
          } else {
            _tmpBirthDate = _stmt.getLong(_columnIndexOfBirthDate)
          }
          val _tmpVaccinationRecordsJson: String?
          if (_stmt.isNull(_columnIndexOfVaccinationRecordsJson)) {
            _tmpVaccinationRecordsJson = null
          } else {
            _tmpVaccinationRecordsJson = _stmt.getText(_columnIndexOfVaccinationRecordsJson)
          }
          val _tmpWeightGrams: Double?
          if (_stmt.isNull(_columnIndexOfWeightGrams)) {
            _tmpWeightGrams = null
          } else {
            _tmpWeightGrams = _stmt.getDouble(_columnIndexOfWeightGrams)
          }
          val _tmpHeightCm: Double?
          if (_stmt.isNull(_columnIndexOfHeightCm)) {
            _tmpHeightCm = null
          } else {
            _tmpHeightCm = _stmt.getDouble(_columnIndexOfHeightCm)
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
          val _tmpBreed: String?
          if (_stmt.isNull(_columnIndexOfBreed)) {
            _tmpBreed = null
          } else {
            _tmpBreed = _stmt.getText(_columnIndexOfBreed)
          }
          val _tmpRaisingPurpose: String?
          if (_stmt.isNull(_columnIndexOfRaisingPurpose)) {
            _tmpRaisingPurpose = null
          } else {
            _tmpRaisingPurpose = _stmt.getText(_columnIndexOfRaisingPurpose)
          }
          val _tmpHealthStatus: String?
          if (_stmt.isNull(_columnIndexOfHealthStatus)) {
            _tmpHealthStatus = null
          } else {
            _tmpHealthStatus = _stmt.getText(_columnIndexOfHealthStatus)
          }
          val _tmpBirdCode: String?
          if (_stmt.isNull(_columnIndexOfBirdCode)) {
            _tmpBirdCode = null
          } else {
            _tmpBirdCode = _stmt.getText(_columnIndexOfBirdCode)
          }
          val _tmpColorTag: String?
          if (_stmt.isNull(_columnIndexOfColorTag)) {
            _tmpColorTag = null
          } else {
            _tmpColorTag = _stmt.getText(_columnIndexOfColorTag)
          }
          val _tmpFamilyTreeId: String?
          if (_stmt.isNull(_columnIndexOfFamilyTreeId)) {
            _tmpFamilyTreeId = null
          } else {
            _tmpFamilyTreeId = _stmt.getText(_columnIndexOfFamilyTreeId)
          }
          val _tmpSourceAssetId: String?
          if (_stmt.isNull(_columnIndexOfSourceAssetId)) {
            _tmpSourceAssetId = null
          } else {
            _tmpSourceAssetId = _stmt.getText(_columnIndexOfSourceAssetId)
          }
          val _tmpParentIdsJson: String?
          if (_stmt.isNull(_columnIndexOfParentIdsJson)) {
            _tmpParentIdsJson = null
          } else {
            _tmpParentIdsJson = _stmt.getText(_columnIndexOfParentIdsJson)
          }
          val _tmpBreedingStatus: String?
          if (_stmt.isNull(_columnIndexOfBreedingStatus)) {
            _tmpBreedingStatus = null
          } else {
            _tmpBreedingStatus = _stmt.getText(_columnIndexOfBreedingStatus)
          }
          val _tmpTransferHistoryJson: String?
          if (_stmt.isNull(_columnIndexOfTransferHistoryJson)) {
            _tmpTransferHistoryJson = null
          } else {
            _tmpTransferHistoryJson = _stmt.getText(_columnIndexOfTransferHistoryJson)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpLastModifiedAt: Long
          _tmpLastModifiedAt = _stmt.getLong(_columnIndexOfLastModifiedAt)
          val _tmpIsDeleted: Boolean
          val _tmp_2: Int
          _tmp_2 = _stmt.getLong(_columnIndexOfIsDeleted).toInt()
          _tmpIsDeleted = _tmp_2 != 0
          val _tmpDeletedAt: Long?
          if (_stmt.isNull(_columnIndexOfDeletedAt)) {
            _tmpDeletedAt = null
          } else {
            _tmpDeletedAt = _stmt.getLong(_columnIndexOfDeletedAt)
          }
          val _tmpDirty: Boolean
          val _tmp_3: Int
          _tmp_3 = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp_3 != 0
          val _tmpStage: LifecycleStage?
          val _tmp_4: String?
          if (_stmt.isNull(_columnIndexOfStage)) {
            _tmp_4 = null
          } else {
            _tmp_4 = _stmt.getText(_columnIndexOfStage)
          }
          _tmpStage = AppDatabase.Converters.toLifecycleStage(_tmp_4)
          val _tmpLifecycleStatus: String?
          if (_stmt.isNull(_columnIndexOfLifecycleStatus)) {
            _tmpLifecycleStatus = null
          } else {
            _tmpLifecycleStatus = _stmt.getText(_columnIndexOfLifecycleStatus)
          }
          val _tmpParentMaleId: String?
          if (_stmt.isNull(_columnIndexOfParentMaleId)) {
            _tmpParentMaleId = null
          } else {
            _tmpParentMaleId = _stmt.getText(_columnIndexOfParentMaleId)
          }
          val _tmpParentFemaleId: String?
          if (_stmt.isNull(_columnIndexOfParentFemaleId)) {
            _tmpParentFemaleId = null
          } else {
            _tmpParentFemaleId = _stmt.getText(_columnIndexOfParentFemaleId)
          }
          val _tmpAgeWeeks: Int?
          if (_stmt.isNull(_columnIndexOfAgeWeeks)) {
            _tmpAgeWeeks = null
          } else {
            _tmpAgeWeeks = _stmt.getLong(_columnIndexOfAgeWeeks).toInt()
          }
          val _tmpLastStageTransitionAt: Long?
          if (_stmt.isNull(_columnIndexOfLastStageTransitionAt)) {
            _tmpLastStageTransitionAt = null
          } else {
            _tmpLastStageTransitionAt = _stmt.getLong(_columnIndexOfLastStageTransitionAt)
          }
          val _tmpBreederEligibleAt: Long?
          if (_stmt.isNull(_columnIndexOfBreederEligibleAt)) {
            _tmpBreederEligibleAt = null
          } else {
            _tmpBreederEligibleAt = _stmt.getLong(_columnIndexOfBreederEligibleAt)
          }
          val _tmpIsBatch: Boolean?
          val _tmp_5: Int?
          if (_stmt.isNull(_columnIndexOfIsBatch)) {
            _tmp_5 = null
          } else {
            _tmp_5 = _stmt.getLong(_columnIndexOfIsBatch).toInt()
          }
          _tmpIsBatch = _tmp_5?.let { it != 0 }
          val _tmpBatchId: String?
          if (_stmt.isNull(_columnIndexOfBatchId)) {
            _tmpBatchId = null
          } else {
            _tmpBatchId = _stmt.getText(_columnIndexOfBatchId)
          }
          val _tmpSplitAt: Long?
          if (_stmt.isNull(_columnIndexOfSplitAt)) {
            _tmpSplitAt = null
          } else {
            _tmpSplitAt = _stmt.getLong(_columnIndexOfSplitAt)
          }
          val _tmpSplitIntoIds: String?
          if (_stmt.isNull(_columnIndexOfSplitIntoIds)) {
            _tmpSplitIntoIds = null
          } else {
            _tmpSplitIntoIds = _stmt.getText(_columnIndexOfSplitIntoIds)
          }
          val _tmpDocumentUrls: List<String>
          val _tmp_6: String?
          if (_stmt.isNull(_columnIndexOfDocumentUrls)) {
            _tmp_6 = null
          } else {
            _tmp_6 = _stmt.getText(_columnIndexOfDocumentUrls)
          }
          val _tmp_7: List<String>? = AppDatabase.Converters.toStringList(_tmp_6)
          if (_tmp_7 == null) {
            error("Expected NON-NULL 'kotlin.collections.List<kotlin.String>', but it was NULL.")
          } else {
            _tmpDocumentUrls = _tmp_7
          }
          val _tmpQrCodeUrl: String?
          if (_stmt.isNull(_columnIndexOfQrCodeUrl)) {
            _tmpQrCodeUrl = null
          } else {
            _tmpQrCodeUrl = _stmt.getText(_columnIndexOfQrCodeUrl)
          }
          val _tmpCustomStatus: String?
          if (_stmt.isNull(_columnIndexOfCustomStatus)) {
            _tmpCustomStatus = null
          } else {
            _tmpCustomStatus = _stmt.getText(_columnIndexOfCustomStatus)
          }
          val _tmpDebug: Boolean
          val _tmp_8: Int
          _tmp_8 = _stmt.getLong(_columnIndexOfDebug).toInt()
          _tmpDebug = _tmp_8 != 0
          val _tmpDeliveryOptions: List<String>
          val _tmp_9: String?
          if (_stmt.isNull(_columnIndexOfDeliveryOptions)) {
            _tmp_9 = null
          } else {
            _tmp_9 = _stmt.getText(_columnIndexOfDeliveryOptions)
          }
          val _tmp_10: List<String>? = AppDatabase.Converters.toStringList(_tmp_9)
          if (_tmp_10 == null) {
            error("Expected NON-NULL 'kotlin.collections.List<kotlin.String>', but it was NULL.")
          } else {
            _tmpDeliveryOptions = _tmp_10
          }
          val _tmpDeliveryCost: Double?
          if (_stmt.isNull(_columnIndexOfDeliveryCost)) {
            _tmpDeliveryCost = null
          } else {
            _tmpDeliveryCost = _stmt.getDouble(_columnIndexOfDeliveryCost)
          }
          val _tmpLeadTimeDays: Int?
          if (_stmt.isNull(_columnIndexOfLeadTimeDays)) {
            _tmpLeadTimeDays = null
          } else {
            _tmpLeadTimeDays = _stmt.getLong(_columnIndexOfLeadTimeDays).toInt()
          }
          val _tmpMotherId: String?
          if (_stmt.isNull(_columnIndexOfMotherId)) {
            _tmpMotherId = null
          } else {
            _tmpMotherId = _stmt.getText(_columnIndexOfMotherId)
          }
          val _tmpIsBreedingUnit: Boolean
          val _tmp_11: Int
          _tmp_11 = _stmt.getLong(_columnIndexOfIsBreedingUnit).toInt()
          _tmpIsBreedingUnit = _tmp_11 != 0
          val _tmpEggsCollectedToday: Int
          _tmpEggsCollectedToday = _stmt.getLong(_columnIndexOfEggsCollectedToday).toInt()
          val _tmpLastEggLogDate: Long?
          if (_stmt.isNull(_columnIndexOfLastEggLogDate)) {
            _tmpLastEggLogDate = null
          } else {
            _tmpLastEggLogDate = _stmt.getLong(_columnIndexOfLastEggLogDate)
          }
          val _tmpReadyForSale: Boolean
          val _tmp_12: Int
          _tmp_12 = _stmt.getLong(_columnIndexOfReadyForSale).toInt()
          _tmpReadyForSale = _tmp_12 != 0
          val _tmpTargetWeight: Double?
          if (_stmt.isNull(_columnIndexOfTargetWeight)) {
            _tmpTargetWeight = null
          } else {
            _tmpTargetWeight = _stmt.getDouble(_columnIndexOfTargetWeight)
          }
          val _tmpIsShowcased: Boolean
          val _tmp_13: Int
          _tmp_13 = _stmt.getLong(_columnIndexOfIsShowcased).toInt()
          _tmpIsShowcased = _tmp_13 != 0
          val _tmpExternalVideoUrl: String?
          if (_stmt.isNull(_columnIndexOfExternalVideoUrl)) {
            _tmpExternalVideoUrl = null
          } else {
            _tmpExternalVideoUrl = _stmt.getText(_columnIndexOfExternalVideoUrl)
          }
          val _tmpRecordsLockedAt: Long?
          if (_stmt.isNull(_columnIndexOfRecordsLockedAt)) {
            _tmpRecordsLockedAt = null
          } else {
            _tmpRecordsLockedAt = _stmt.getLong(_columnIndexOfRecordsLockedAt)
          }
          val _tmpAutoLockAfterDays: Int
          _tmpAutoLockAfterDays = _stmt.getLong(_columnIndexOfAutoLockAfterDays).toInt()
          val _tmpLineageHistoryJson: String?
          if (_stmt.isNull(_columnIndexOfLineageHistoryJson)) {
            _tmpLineageHistoryJson = null
          } else {
            _tmpLineageHistoryJson = _stmt.getText(_columnIndexOfLineageHistoryJson)
          }
          val _tmpEditCount: Int
          _tmpEditCount = _stmt.getLong(_columnIndexOfEditCount).toInt()
          val _tmpLastEditedBy: String?
          if (_stmt.isNull(_columnIndexOfLastEditedBy)) {
            _tmpLastEditedBy = null
          } else {
            _tmpLastEditedBy = _stmt.getText(_columnIndexOfLastEditedBy)
          }
          val _tmpAdminFlagged: Boolean
          val _tmp_14: Int
          _tmp_14 = _stmt.getLong(_columnIndexOfAdminFlagged).toInt()
          _tmpAdminFlagged = _tmp_14 != 0
          val _tmpModerationNote: String?
          if (_stmt.isNull(_columnIndexOfModerationNote)) {
            _tmpModerationNote = null
          } else {
            _tmpModerationNote = _stmt.getText(_columnIndexOfModerationNote)
          }
          val _tmpMetadataJson: String
          _tmpMetadataJson = _stmt.getText(_columnIndexOfMetadataJson)
          _item =
              ProductEntity(_tmpProductId,_tmpSellerId,_tmpName,_tmpDescription,_tmpCategory,_tmpPrice,_tmpQuantity,_tmpUnit,_tmpLocation,_tmpLatitude,_tmpLongitude,_tmpImageUrls,_tmpStatus,_tmpCondition,_tmpHarvestDate,_tmpExpiryDate,_tmpBirthDate,_tmpVaccinationRecordsJson,_tmpWeightGrams,_tmpHeightCm,_tmpGender,_tmpColor,_tmpBreed,_tmpRaisingPurpose,_tmpHealthStatus,_tmpBirdCode,_tmpColorTag,_tmpFamilyTreeId,_tmpSourceAssetId,_tmpParentIdsJson,_tmpBreedingStatus,_tmpTransferHistoryJson,_tmpCreatedAt,_tmpUpdatedAt,_tmpLastModifiedAt,_tmpIsDeleted,_tmpDeletedAt,_tmpDirty,_tmpStage,_tmpLifecycleStatus,_tmpParentMaleId,_tmpParentFemaleId,_tmpAgeWeeks,_tmpLastStageTransitionAt,_tmpBreederEligibleAt,_tmpIsBatch,_tmpBatchId,_tmpSplitAt,_tmpSplitIntoIds,_tmpDocumentUrls,_tmpQrCodeUrl,_tmpCustomStatus,_tmpDebug,_tmpDeliveryOptions,_tmpDeliveryCost,_tmpLeadTimeDays,_tmpMotherId,_tmpIsBreedingUnit,_tmpEggsCollectedToday,_tmpLastEggLogDate,_tmpReadyForSale,_tmpTargetWeight,_tmpIsShowcased,_tmpExternalVideoUrl,_tmpRecordsLockedAt,_tmpAutoLockAfterDays,_tmpLineageHistoryJson,_tmpEditCount,_tmpLastEditedBy,_tmpAdminFlagged,_tmpModerationNote,_tmpMetadataJson)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeRecentlyAddedForFarmer(farmerId: String, sevenDaysAgo: Long):
      Flow<List<ProductEntity>> {
    val _sql: String =
        "SELECT * FROM products WHERE sellerId = ? AND createdAt >= ? AND isDeleted = 0 ORDER BY createdAt DESC"
    return createFlow(__db, false, arrayOf("products")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, farmerId)
        _argIndex = 2
        _stmt.bindLong(_argIndex, sevenDaysAgo)
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfSellerId: Int = getColumnIndexOrThrow(_stmt, "sellerId")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfCategory: Int = getColumnIndexOrThrow(_stmt, "category")
        val _columnIndexOfPrice: Int = getColumnIndexOrThrow(_stmt, "price")
        val _columnIndexOfQuantity: Int = getColumnIndexOrThrow(_stmt, "quantity")
        val _columnIndexOfUnit: Int = getColumnIndexOrThrow(_stmt, "unit")
        val _columnIndexOfLocation: Int = getColumnIndexOrThrow(_stmt, "location")
        val _columnIndexOfLatitude: Int = getColumnIndexOrThrow(_stmt, "latitude")
        val _columnIndexOfLongitude: Int = getColumnIndexOrThrow(_stmt, "longitude")
        val _columnIndexOfImageUrls: Int = getColumnIndexOrThrow(_stmt, "imageUrls")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfCondition: Int = getColumnIndexOrThrow(_stmt, "condition")
        val _columnIndexOfHarvestDate: Int = getColumnIndexOrThrow(_stmt, "harvestDate")
        val _columnIndexOfExpiryDate: Int = getColumnIndexOrThrow(_stmt, "expiryDate")
        val _columnIndexOfBirthDate: Int = getColumnIndexOrThrow(_stmt, "birthDate")
        val _columnIndexOfVaccinationRecordsJson: Int = getColumnIndexOrThrow(_stmt,
            "vaccinationRecordsJson")
        val _columnIndexOfWeightGrams: Int = getColumnIndexOrThrow(_stmt, "weightGrams")
        val _columnIndexOfHeightCm: Int = getColumnIndexOrThrow(_stmt, "heightCm")
        val _columnIndexOfGender: Int = getColumnIndexOrThrow(_stmt, "gender")
        val _columnIndexOfColor: Int = getColumnIndexOrThrow(_stmt, "color")
        val _columnIndexOfBreed: Int = getColumnIndexOrThrow(_stmt, "breed")
        val _columnIndexOfRaisingPurpose: Int = getColumnIndexOrThrow(_stmt, "raisingPurpose")
        val _columnIndexOfHealthStatus: Int = getColumnIndexOrThrow(_stmt, "healthStatus")
        val _columnIndexOfBirdCode: Int = getColumnIndexOrThrow(_stmt, "birdCode")
        val _columnIndexOfColorTag: Int = getColumnIndexOrThrow(_stmt, "colorTag")
        val _columnIndexOfFamilyTreeId: Int = getColumnIndexOrThrow(_stmt, "familyTreeId")
        val _columnIndexOfSourceAssetId: Int = getColumnIndexOrThrow(_stmt, "sourceAssetId")
        val _columnIndexOfParentIdsJson: Int = getColumnIndexOrThrow(_stmt, "parentIdsJson")
        val _columnIndexOfBreedingStatus: Int = getColumnIndexOrThrow(_stmt, "breedingStatus")
        val _columnIndexOfTransferHistoryJson: Int = getColumnIndexOrThrow(_stmt,
            "transferHistoryJson")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfLastModifiedAt: Int = getColumnIndexOrThrow(_stmt, "lastModifiedAt")
        val _columnIndexOfIsDeleted: Int = getColumnIndexOrThrow(_stmt, "isDeleted")
        val _columnIndexOfDeletedAt: Int = getColumnIndexOrThrow(_stmt, "deletedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfStage: Int = getColumnIndexOrThrow(_stmt, "stage")
        val _columnIndexOfLifecycleStatus: Int = getColumnIndexOrThrow(_stmt, "lifecycleStatus")
        val _columnIndexOfParentMaleId: Int = getColumnIndexOrThrow(_stmt, "parentMaleId")
        val _columnIndexOfParentFemaleId: Int = getColumnIndexOrThrow(_stmt, "parentFemaleId")
        val _columnIndexOfAgeWeeks: Int = getColumnIndexOrThrow(_stmt, "ageWeeks")
        val _columnIndexOfLastStageTransitionAt: Int = getColumnIndexOrThrow(_stmt,
            "lastStageTransitionAt")
        val _columnIndexOfBreederEligibleAt: Int = getColumnIndexOrThrow(_stmt, "breederEligibleAt")
        val _columnIndexOfIsBatch: Int = getColumnIndexOrThrow(_stmt, "isBatch")
        val _columnIndexOfBatchId: Int = getColumnIndexOrThrow(_stmt, "batchId")
        val _columnIndexOfSplitAt: Int = getColumnIndexOrThrow(_stmt, "splitAt")
        val _columnIndexOfSplitIntoIds: Int = getColumnIndexOrThrow(_stmt, "splitIntoIds")
        val _columnIndexOfDocumentUrls: Int = getColumnIndexOrThrow(_stmt, "documentUrls")
        val _columnIndexOfQrCodeUrl: Int = getColumnIndexOrThrow(_stmt, "qrCodeUrl")
        val _columnIndexOfCustomStatus: Int = getColumnIndexOrThrow(_stmt, "customStatus")
        val _columnIndexOfDebug: Int = getColumnIndexOrThrow(_stmt, "debug")
        val _columnIndexOfDeliveryOptions: Int = getColumnIndexOrThrow(_stmt, "deliveryOptions")
        val _columnIndexOfDeliveryCost: Int = getColumnIndexOrThrow(_stmt, "deliveryCost")
        val _columnIndexOfLeadTimeDays: Int = getColumnIndexOrThrow(_stmt, "leadTimeDays")
        val _columnIndexOfMotherId: Int = getColumnIndexOrThrow(_stmt, "motherId")
        val _columnIndexOfIsBreedingUnit: Int = getColumnIndexOrThrow(_stmt, "isBreedingUnit")
        val _columnIndexOfEggsCollectedToday: Int = getColumnIndexOrThrow(_stmt,
            "eggsCollectedToday")
        val _columnIndexOfLastEggLogDate: Int = getColumnIndexOrThrow(_stmt, "lastEggLogDate")
        val _columnIndexOfReadyForSale: Int = getColumnIndexOrThrow(_stmt, "readyForSale")
        val _columnIndexOfTargetWeight: Int = getColumnIndexOrThrow(_stmt, "targetWeight")
        val _columnIndexOfIsShowcased: Int = getColumnIndexOrThrow(_stmt, "isShowcased")
        val _columnIndexOfExternalVideoUrl: Int = getColumnIndexOrThrow(_stmt, "externalVideoUrl")
        val _columnIndexOfRecordsLockedAt: Int = getColumnIndexOrThrow(_stmt, "recordsLockedAt")
        val _columnIndexOfAutoLockAfterDays: Int = getColumnIndexOrThrow(_stmt, "autoLockAfterDays")
        val _columnIndexOfLineageHistoryJson: Int = getColumnIndexOrThrow(_stmt,
            "lineageHistoryJson")
        val _columnIndexOfEditCount: Int = getColumnIndexOrThrow(_stmt, "editCount")
        val _columnIndexOfLastEditedBy: Int = getColumnIndexOrThrow(_stmt, "lastEditedBy")
        val _columnIndexOfAdminFlagged: Int = getColumnIndexOrThrow(_stmt, "adminFlagged")
        val _columnIndexOfModerationNote: Int = getColumnIndexOrThrow(_stmt, "moderationNote")
        val _columnIndexOfMetadataJson: Int = getColumnIndexOrThrow(_stmt, "metadataJson")
        val _result: MutableList<ProductEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: ProductEntity
          val _tmpProductId: String
          _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          val _tmpSellerId: String
          _tmpSellerId = _stmt.getText(_columnIndexOfSellerId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpDescription: String
          _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          val _tmpCategory: String
          _tmpCategory = _stmt.getText(_columnIndexOfCategory)
          val _tmpPrice: Double
          _tmpPrice = _stmt.getDouble(_columnIndexOfPrice)
          val _tmpQuantity: Double
          _tmpQuantity = _stmt.getDouble(_columnIndexOfQuantity)
          val _tmpUnit: String
          _tmpUnit = _stmt.getText(_columnIndexOfUnit)
          val _tmpLocation: String
          _tmpLocation = _stmt.getText(_columnIndexOfLocation)
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
          val _tmpImageUrls: List<String>
          val _tmp: String?
          if (_stmt.isNull(_columnIndexOfImageUrls)) {
            _tmp = null
          } else {
            _tmp = _stmt.getText(_columnIndexOfImageUrls)
          }
          val _tmp_1: List<String>? = AppDatabase.Converters.toStringList(_tmp)
          if (_tmp_1 == null) {
            error("Expected NON-NULL 'kotlin.collections.List<kotlin.String>', but it was NULL.")
          } else {
            _tmpImageUrls = _tmp_1
          }
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpCondition: String?
          if (_stmt.isNull(_columnIndexOfCondition)) {
            _tmpCondition = null
          } else {
            _tmpCondition = _stmt.getText(_columnIndexOfCondition)
          }
          val _tmpHarvestDate: Long?
          if (_stmt.isNull(_columnIndexOfHarvestDate)) {
            _tmpHarvestDate = null
          } else {
            _tmpHarvestDate = _stmt.getLong(_columnIndexOfHarvestDate)
          }
          val _tmpExpiryDate: Long?
          if (_stmt.isNull(_columnIndexOfExpiryDate)) {
            _tmpExpiryDate = null
          } else {
            _tmpExpiryDate = _stmt.getLong(_columnIndexOfExpiryDate)
          }
          val _tmpBirthDate: Long?
          if (_stmt.isNull(_columnIndexOfBirthDate)) {
            _tmpBirthDate = null
          } else {
            _tmpBirthDate = _stmt.getLong(_columnIndexOfBirthDate)
          }
          val _tmpVaccinationRecordsJson: String?
          if (_stmt.isNull(_columnIndexOfVaccinationRecordsJson)) {
            _tmpVaccinationRecordsJson = null
          } else {
            _tmpVaccinationRecordsJson = _stmt.getText(_columnIndexOfVaccinationRecordsJson)
          }
          val _tmpWeightGrams: Double?
          if (_stmt.isNull(_columnIndexOfWeightGrams)) {
            _tmpWeightGrams = null
          } else {
            _tmpWeightGrams = _stmt.getDouble(_columnIndexOfWeightGrams)
          }
          val _tmpHeightCm: Double?
          if (_stmt.isNull(_columnIndexOfHeightCm)) {
            _tmpHeightCm = null
          } else {
            _tmpHeightCm = _stmt.getDouble(_columnIndexOfHeightCm)
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
          val _tmpBreed: String?
          if (_stmt.isNull(_columnIndexOfBreed)) {
            _tmpBreed = null
          } else {
            _tmpBreed = _stmt.getText(_columnIndexOfBreed)
          }
          val _tmpRaisingPurpose: String?
          if (_stmt.isNull(_columnIndexOfRaisingPurpose)) {
            _tmpRaisingPurpose = null
          } else {
            _tmpRaisingPurpose = _stmt.getText(_columnIndexOfRaisingPurpose)
          }
          val _tmpHealthStatus: String?
          if (_stmt.isNull(_columnIndexOfHealthStatus)) {
            _tmpHealthStatus = null
          } else {
            _tmpHealthStatus = _stmt.getText(_columnIndexOfHealthStatus)
          }
          val _tmpBirdCode: String?
          if (_stmt.isNull(_columnIndexOfBirdCode)) {
            _tmpBirdCode = null
          } else {
            _tmpBirdCode = _stmt.getText(_columnIndexOfBirdCode)
          }
          val _tmpColorTag: String?
          if (_stmt.isNull(_columnIndexOfColorTag)) {
            _tmpColorTag = null
          } else {
            _tmpColorTag = _stmt.getText(_columnIndexOfColorTag)
          }
          val _tmpFamilyTreeId: String?
          if (_stmt.isNull(_columnIndexOfFamilyTreeId)) {
            _tmpFamilyTreeId = null
          } else {
            _tmpFamilyTreeId = _stmt.getText(_columnIndexOfFamilyTreeId)
          }
          val _tmpSourceAssetId: String?
          if (_stmt.isNull(_columnIndexOfSourceAssetId)) {
            _tmpSourceAssetId = null
          } else {
            _tmpSourceAssetId = _stmt.getText(_columnIndexOfSourceAssetId)
          }
          val _tmpParentIdsJson: String?
          if (_stmt.isNull(_columnIndexOfParentIdsJson)) {
            _tmpParentIdsJson = null
          } else {
            _tmpParentIdsJson = _stmt.getText(_columnIndexOfParentIdsJson)
          }
          val _tmpBreedingStatus: String?
          if (_stmt.isNull(_columnIndexOfBreedingStatus)) {
            _tmpBreedingStatus = null
          } else {
            _tmpBreedingStatus = _stmt.getText(_columnIndexOfBreedingStatus)
          }
          val _tmpTransferHistoryJson: String?
          if (_stmt.isNull(_columnIndexOfTransferHistoryJson)) {
            _tmpTransferHistoryJson = null
          } else {
            _tmpTransferHistoryJson = _stmt.getText(_columnIndexOfTransferHistoryJson)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpLastModifiedAt: Long
          _tmpLastModifiedAt = _stmt.getLong(_columnIndexOfLastModifiedAt)
          val _tmpIsDeleted: Boolean
          val _tmp_2: Int
          _tmp_2 = _stmt.getLong(_columnIndexOfIsDeleted).toInt()
          _tmpIsDeleted = _tmp_2 != 0
          val _tmpDeletedAt: Long?
          if (_stmt.isNull(_columnIndexOfDeletedAt)) {
            _tmpDeletedAt = null
          } else {
            _tmpDeletedAt = _stmt.getLong(_columnIndexOfDeletedAt)
          }
          val _tmpDirty: Boolean
          val _tmp_3: Int
          _tmp_3 = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp_3 != 0
          val _tmpStage: LifecycleStage?
          val _tmp_4: String?
          if (_stmt.isNull(_columnIndexOfStage)) {
            _tmp_4 = null
          } else {
            _tmp_4 = _stmt.getText(_columnIndexOfStage)
          }
          _tmpStage = AppDatabase.Converters.toLifecycleStage(_tmp_4)
          val _tmpLifecycleStatus: String?
          if (_stmt.isNull(_columnIndexOfLifecycleStatus)) {
            _tmpLifecycleStatus = null
          } else {
            _tmpLifecycleStatus = _stmt.getText(_columnIndexOfLifecycleStatus)
          }
          val _tmpParentMaleId: String?
          if (_stmt.isNull(_columnIndexOfParentMaleId)) {
            _tmpParentMaleId = null
          } else {
            _tmpParentMaleId = _stmt.getText(_columnIndexOfParentMaleId)
          }
          val _tmpParentFemaleId: String?
          if (_stmt.isNull(_columnIndexOfParentFemaleId)) {
            _tmpParentFemaleId = null
          } else {
            _tmpParentFemaleId = _stmt.getText(_columnIndexOfParentFemaleId)
          }
          val _tmpAgeWeeks: Int?
          if (_stmt.isNull(_columnIndexOfAgeWeeks)) {
            _tmpAgeWeeks = null
          } else {
            _tmpAgeWeeks = _stmt.getLong(_columnIndexOfAgeWeeks).toInt()
          }
          val _tmpLastStageTransitionAt: Long?
          if (_stmt.isNull(_columnIndexOfLastStageTransitionAt)) {
            _tmpLastStageTransitionAt = null
          } else {
            _tmpLastStageTransitionAt = _stmt.getLong(_columnIndexOfLastStageTransitionAt)
          }
          val _tmpBreederEligibleAt: Long?
          if (_stmt.isNull(_columnIndexOfBreederEligibleAt)) {
            _tmpBreederEligibleAt = null
          } else {
            _tmpBreederEligibleAt = _stmt.getLong(_columnIndexOfBreederEligibleAt)
          }
          val _tmpIsBatch: Boolean?
          val _tmp_5: Int?
          if (_stmt.isNull(_columnIndexOfIsBatch)) {
            _tmp_5 = null
          } else {
            _tmp_5 = _stmt.getLong(_columnIndexOfIsBatch).toInt()
          }
          _tmpIsBatch = _tmp_5?.let { it != 0 }
          val _tmpBatchId: String?
          if (_stmt.isNull(_columnIndexOfBatchId)) {
            _tmpBatchId = null
          } else {
            _tmpBatchId = _stmt.getText(_columnIndexOfBatchId)
          }
          val _tmpSplitAt: Long?
          if (_stmt.isNull(_columnIndexOfSplitAt)) {
            _tmpSplitAt = null
          } else {
            _tmpSplitAt = _stmt.getLong(_columnIndexOfSplitAt)
          }
          val _tmpSplitIntoIds: String?
          if (_stmt.isNull(_columnIndexOfSplitIntoIds)) {
            _tmpSplitIntoIds = null
          } else {
            _tmpSplitIntoIds = _stmt.getText(_columnIndexOfSplitIntoIds)
          }
          val _tmpDocumentUrls: List<String>
          val _tmp_6: String?
          if (_stmt.isNull(_columnIndexOfDocumentUrls)) {
            _tmp_6 = null
          } else {
            _tmp_6 = _stmt.getText(_columnIndexOfDocumentUrls)
          }
          val _tmp_7: List<String>? = AppDatabase.Converters.toStringList(_tmp_6)
          if (_tmp_7 == null) {
            error("Expected NON-NULL 'kotlin.collections.List<kotlin.String>', but it was NULL.")
          } else {
            _tmpDocumentUrls = _tmp_7
          }
          val _tmpQrCodeUrl: String?
          if (_stmt.isNull(_columnIndexOfQrCodeUrl)) {
            _tmpQrCodeUrl = null
          } else {
            _tmpQrCodeUrl = _stmt.getText(_columnIndexOfQrCodeUrl)
          }
          val _tmpCustomStatus: String?
          if (_stmt.isNull(_columnIndexOfCustomStatus)) {
            _tmpCustomStatus = null
          } else {
            _tmpCustomStatus = _stmt.getText(_columnIndexOfCustomStatus)
          }
          val _tmpDebug: Boolean
          val _tmp_8: Int
          _tmp_8 = _stmt.getLong(_columnIndexOfDebug).toInt()
          _tmpDebug = _tmp_8 != 0
          val _tmpDeliveryOptions: List<String>
          val _tmp_9: String?
          if (_stmt.isNull(_columnIndexOfDeliveryOptions)) {
            _tmp_9 = null
          } else {
            _tmp_9 = _stmt.getText(_columnIndexOfDeliveryOptions)
          }
          val _tmp_10: List<String>? = AppDatabase.Converters.toStringList(_tmp_9)
          if (_tmp_10 == null) {
            error("Expected NON-NULL 'kotlin.collections.List<kotlin.String>', but it was NULL.")
          } else {
            _tmpDeliveryOptions = _tmp_10
          }
          val _tmpDeliveryCost: Double?
          if (_stmt.isNull(_columnIndexOfDeliveryCost)) {
            _tmpDeliveryCost = null
          } else {
            _tmpDeliveryCost = _stmt.getDouble(_columnIndexOfDeliveryCost)
          }
          val _tmpLeadTimeDays: Int?
          if (_stmt.isNull(_columnIndexOfLeadTimeDays)) {
            _tmpLeadTimeDays = null
          } else {
            _tmpLeadTimeDays = _stmt.getLong(_columnIndexOfLeadTimeDays).toInt()
          }
          val _tmpMotherId: String?
          if (_stmt.isNull(_columnIndexOfMotherId)) {
            _tmpMotherId = null
          } else {
            _tmpMotherId = _stmt.getText(_columnIndexOfMotherId)
          }
          val _tmpIsBreedingUnit: Boolean
          val _tmp_11: Int
          _tmp_11 = _stmt.getLong(_columnIndexOfIsBreedingUnit).toInt()
          _tmpIsBreedingUnit = _tmp_11 != 0
          val _tmpEggsCollectedToday: Int
          _tmpEggsCollectedToday = _stmt.getLong(_columnIndexOfEggsCollectedToday).toInt()
          val _tmpLastEggLogDate: Long?
          if (_stmt.isNull(_columnIndexOfLastEggLogDate)) {
            _tmpLastEggLogDate = null
          } else {
            _tmpLastEggLogDate = _stmt.getLong(_columnIndexOfLastEggLogDate)
          }
          val _tmpReadyForSale: Boolean
          val _tmp_12: Int
          _tmp_12 = _stmt.getLong(_columnIndexOfReadyForSale).toInt()
          _tmpReadyForSale = _tmp_12 != 0
          val _tmpTargetWeight: Double?
          if (_stmt.isNull(_columnIndexOfTargetWeight)) {
            _tmpTargetWeight = null
          } else {
            _tmpTargetWeight = _stmt.getDouble(_columnIndexOfTargetWeight)
          }
          val _tmpIsShowcased: Boolean
          val _tmp_13: Int
          _tmp_13 = _stmt.getLong(_columnIndexOfIsShowcased).toInt()
          _tmpIsShowcased = _tmp_13 != 0
          val _tmpExternalVideoUrl: String?
          if (_stmt.isNull(_columnIndexOfExternalVideoUrl)) {
            _tmpExternalVideoUrl = null
          } else {
            _tmpExternalVideoUrl = _stmt.getText(_columnIndexOfExternalVideoUrl)
          }
          val _tmpRecordsLockedAt: Long?
          if (_stmt.isNull(_columnIndexOfRecordsLockedAt)) {
            _tmpRecordsLockedAt = null
          } else {
            _tmpRecordsLockedAt = _stmt.getLong(_columnIndexOfRecordsLockedAt)
          }
          val _tmpAutoLockAfterDays: Int
          _tmpAutoLockAfterDays = _stmt.getLong(_columnIndexOfAutoLockAfterDays).toInt()
          val _tmpLineageHistoryJson: String?
          if (_stmt.isNull(_columnIndexOfLineageHistoryJson)) {
            _tmpLineageHistoryJson = null
          } else {
            _tmpLineageHistoryJson = _stmt.getText(_columnIndexOfLineageHistoryJson)
          }
          val _tmpEditCount: Int
          _tmpEditCount = _stmt.getLong(_columnIndexOfEditCount).toInt()
          val _tmpLastEditedBy: String?
          if (_stmt.isNull(_columnIndexOfLastEditedBy)) {
            _tmpLastEditedBy = null
          } else {
            _tmpLastEditedBy = _stmt.getText(_columnIndexOfLastEditedBy)
          }
          val _tmpAdminFlagged: Boolean
          val _tmp_14: Int
          _tmp_14 = _stmt.getLong(_columnIndexOfAdminFlagged).toInt()
          _tmpAdminFlagged = _tmp_14 != 0
          val _tmpModerationNote: String?
          if (_stmt.isNull(_columnIndexOfModerationNote)) {
            _tmpModerationNote = null
          } else {
            _tmpModerationNote = _stmt.getText(_columnIndexOfModerationNote)
          }
          val _tmpMetadataJson: String
          _tmpMetadataJson = _stmt.getText(_columnIndexOfMetadataJson)
          _item =
              ProductEntity(_tmpProductId,_tmpSellerId,_tmpName,_tmpDescription,_tmpCategory,_tmpPrice,_tmpQuantity,_tmpUnit,_tmpLocation,_tmpLatitude,_tmpLongitude,_tmpImageUrls,_tmpStatus,_tmpCondition,_tmpHarvestDate,_tmpExpiryDate,_tmpBirthDate,_tmpVaccinationRecordsJson,_tmpWeightGrams,_tmpHeightCm,_tmpGender,_tmpColor,_tmpBreed,_tmpRaisingPurpose,_tmpHealthStatus,_tmpBirdCode,_tmpColorTag,_tmpFamilyTreeId,_tmpSourceAssetId,_tmpParentIdsJson,_tmpBreedingStatus,_tmpTransferHistoryJson,_tmpCreatedAt,_tmpUpdatedAt,_tmpLastModifiedAt,_tmpIsDeleted,_tmpDeletedAt,_tmpDirty,_tmpStage,_tmpLifecycleStatus,_tmpParentMaleId,_tmpParentFemaleId,_tmpAgeWeeks,_tmpLastStageTransitionAt,_tmpBreederEligibleAt,_tmpIsBatch,_tmpBatchId,_tmpSplitAt,_tmpSplitIntoIds,_tmpDocumentUrls,_tmpQrCodeUrl,_tmpCustomStatus,_tmpDebug,_tmpDeliveryOptions,_tmpDeliveryCost,_tmpLeadTimeDays,_tmpMotherId,_tmpIsBreedingUnit,_tmpEggsCollectedToday,_tmpLastEggLogDate,_tmpReadyForSale,_tmpTargetWeight,_tmpIsShowcased,_tmpExternalVideoUrl,_tmpRecordsLockedAt,_tmpAutoLockAfterDays,_tmpLineageHistoryJson,_tmpEditCount,_tmpLastEditedBy,_tmpAdminFlagged,_tmpModerationNote,_tmpMetadataJson)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeRecentlyAddedCountForFarmer(
    farmerId: String,
    startTime: Long,
    endTime: Long,
  ): Flow<Int> {
    val _sql: String =
        "SELECT COUNT(*) FROM products WHERE sellerId = ? AND createdAt >= ? AND createdAt <= ? AND isDeleted = 0"
    return createFlow(__db, false, arrayOf("products")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, farmerId)
        _argIndex = 2
        _stmt.bindLong(_argIndex, startTime)
        _argIndex = 3
        _stmt.bindLong(_argIndex, endTime)
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

  public override fun observeEligibleForTransferCountForFarmer(farmerId: String): Flow<Int> {
    val _sql: String =
        "SELECT COUNT(*) FROM products WHERE sellerId = ? AND (lifecycleStatus = 'ACTIVE' OR lifecycleStatus IS NULL) AND ageWeeks >= 12 AND isDeleted = 0"
    return createFlow(__db, false, arrayOf("products")) { _connection ->
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

  public override suspend fun getProductsWithMissingSellers(): List<ProductEntity> {
    val _sql: String =
        "SELECT p.* FROM products p LEFT JOIN users u ON p.sellerId = u.userId WHERE u.userId IS NULL AND p.isDeleted = 0"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfSellerId: Int = getColumnIndexOrThrow(_stmt, "sellerId")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfCategory: Int = getColumnIndexOrThrow(_stmt, "category")
        val _columnIndexOfPrice: Int = getColumnIndexOrThrow(_stmt, "price")
        val _columnIndexOfQuantity: Int = getColumnIndexOrThrow(_stmt, "quantity")
        val _columnIndexOfUnit: Int = getColumnIndexOrThrow(_stmt, "unit")
        val _columnIndexOfLocation: Int = getColumnIndexOrThrow(_stmt, "location")
        val _columnIndexOfLatitude: Int = getColumnIndexOrThrow(_stmt, "latitude")
        val _columnIndexOfLongitude: Int = getColumnIndexOrThrow(_stmt, "longitude")
        val _columnIndexOfImageUrls: Int = getColumnIndexOrThrow(_stmt, "imageUrls")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfCondition: Int = getColumnIndexOrThrow(_stmt, "condition")
        val _columnIndexOfHarvestDate: Int = getColumnIndexOrThrow(_stmt, "harvestDate")
        val _columnIndexOfExpiryDate: Int = getColumnIndexOrThrow(_stmt, "expiryDate")
        val _columnIndexOfBirthDate: Int = getColumnIndexOrThrow(_stmt, "birthDate")
        val _columnIndexOfVaccinationRecordsJson: Int = getColumnIndexOrThrow(_stmt,
            "vaccinationRecordsJson")
        val _columnIndexOfWeightGrams: Int = getColumnIndexOrThrow(_stmt, "weightGrams")
        val _columnIndexOfHeightCm: Int = getColumnIndexOrThrow(_stmt, "heightCm")
        val _columnIndexOfGender: Int = getColumnIndexOrThrow(_stmt, "gender")
        val _columnIndexOfColor: Int = getColumnIndexOrThrow(_stmt, "color")
        val _columnIndexOfBreed: Int = getColumnIndexOrThrow(_stmt, "breed")
        val _columnIndexOfRaisingPurpose: Int = getColumnIndexOrThrow(_stmt, "raisingPurpose")
        val _columnIndexOfHealthStatus: Int = getColumnIndexOrThrow(_stmt, "healthStatus")
        val _columnIndexOfBirdCode: Int = getColumnIndexOrThrow(_stmt, "birdCode")
        val _columnIndexOfColorTag: Int = getColumnIndexOrThrow(_stmt, "colorTag")
        val _columnIndexOfFamilyTreeId: Int = getColumnIndexOrThrow(_stmt, "familyTreeId")
        val _columnIndexOfSourceAssetId: Int = getColumnIndexOrThrow(_stmt, "sourceAssetId")
        val _columnIndexOfParentIdsJson: Int = getColumnIndexOrThrow(_stmt, "parentIdsJson")
        val _columnIndexOfBreedingStatus: Int = getColumnIndexOrThrow(_stmt, "breedingStatus")
        val _columnIndexOfTransferHistoryJson: Int = getColumnIndexOrThrow(_stmt,
            "transferHistoryJson")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfLastModifiedAt: Int = getColumnIndexOrThrow(_stmt, "lastModifiedAt")
        val _columnIndexOfIsDeleted: Int = getColumnIndexOrThrow(_stmt, "isDeleted")
        val _columnIndexOfDeletedAt: Int = getColumnIndexOrThrow(_stmt, "deletedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfStage: Int = getColumnIndexOrThrow(_stmt, "stage")
        val _columnIndexOfLifecycleStatus: Int = getColumnIndexOrThrow(_stmt, "lifecycleStatus")
        val _columnIndexOfParentMaleId: Int = getColumnIndexOrThrow(_stmt, "parentMaleId")
        val _columnIndexOfParentFemaleId: Int = getColumnIndexOrThrow(_stmt, "parentFemaleId")
        val _columnIndexOfAgeWeeks: Int = getColumnIndexOrThrow(_stmt, "ageWeeks")
        val _columnIndexOfLastStageTransitionAt: Int = getColumnIndexOrThrow(_stmt,
            "lastStageTransitionAt")
        val _columnIndexOfBreederEligibleAt: Int = getColumnIndexOrThrow(_stmt, "breederEligibleAt")
        val _columnIndexOfIsBatch: Int = getColumnIndexOrThrow(_stmt, "isBatch")
        val _columnIndexOfBatchId: Int = getColumnIndexOrThrow(_stmt, "batchId")
        val _columnIndexOfSplitAt: Int = getColumnIndexOrThrow(_stmt, "splitAt")
        val _columnIndexOfSplitIntoIds: Int = getColumnIndexOrThrow(_stmt, "splitIntoIds")
        val _columnIndexOfDocumentUrls: Int = getColumnIndexOrThrow(_stmt, "documentUrls")
        val _columnIndexOfQrCodeUrl: Int = getColumnIndexOrThrow(_stmt, "qrCodeUrl")
        val _columnIndexOfCustomStatus: Int = getColumnIndexOrThrow(_stmt, "customStatus")
        val _columnIndexOfDebug: Int = getColumnIndexOrThrow(_stmt, "debug")
        val _columnIndexOfDeliveryOptions: Int = getColumnIndexOrThrow(_stmt, "deliveryOptions")
        val _columnIndexOfDeliveryCost: Int = getColumnIndexOrThrow(_stmt, "deliveryCost")
        val _columnIndexOfLeadTimeDays: Int = getColumnIndexOrThrow(_stmt, "leadTimeDays")
        val _columnIndexOfMotherId: Int = getColumnIndexOrThrow(_stmt, "motherId")
        val _columnIndexOfIsBreedingUnit: Int = getColumnIndexOrThrow(_stmt, "isBreedingUnit")
        val _columnIndexOfEggsCollectedToday: Int = getColumnIndexOrThrow(_stmt,
            "eggsCollectedToday")
        val _columnIndexOfLastEggLogDate: Int = getColumnIndexOrThrow(_stmt, "lastEggLogDate")
        val _columnIndexOfReadyForSale: Int = getColumnIndexOrThrow(_stmt, "readyForSale")
        val _columnIndexOfTargetWeight: Int = getColumnIndexOrThrow(_stmt, "targetWeight")
        val _columnIndexOfIsShowcased: Int = getColumnIndexOrThrow(_stmt, "isShowcased")
        val _columnIndexOfExternalVideoUrl: Int = getColumnIndexOrThrow(_stmt, "externalVideoUrl")
        val _columnIndexOfRecordsLockedAt: Int = getColumnIndexOrThrow(_stmt, "recordsLockedAt")
        val _columnIndexOfAutoLockAfterDays: Int = getColumnIndexOrThrow(_stmt, "autoLockAfterDays")
        val _columnIndexOfLineageHistoryJson: Int = getColumnIndexOrThrow(_stmt,
            "lineageHistoryJson")
        val _columnIndexOfEditCount: Int = getColumnIndexOrThrow(_stmt, "editCount")
        val _columnIndexOfLastEditedBy: Int = getColumnIndexOrThrow(_stmt, "lastEditedBy")
        val _columnIndexOfAdminFlagged: Int = getColumnIndexOrThrow(_stmt, "adminFlagged")
        val _columnIndexOfModerationNote: Int = getColumnIndexOrThrow(_stmt, "moderationNote")
        val _columnIndexOfMetadataJson: Int = getColumnIndexOrThrow(_stmt, "metadataJson")
        val _result: MutableList<ProductEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: ProductEntity
          val _tmpProductId: String
          _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          val _tmpSellerId: String
          _tmpSellerId = _stmt.getText(_columnIndexOfSellerId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpDescription: String
          _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          val _tmpCategory: String
          _tmpCategory = _stmt.getText(_columnIndexOfCategory)
          val _tmpPrice: Double
          _tmpPrice = _stmt.getDouble(_columnIndexOfPrice)
          val _tmpQuantity: Double
          _tmpQuantity = _stmt.getDouble(_columnIndexOfQuantity)
          val _tmpUnit: String
          _tmpUnit = _stmt.getText(_columnIndexOfUnit)
          val _tmpLocation: String
          _tmpLocation = _stmt.getText(_columnIndexOfLocation)
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
          val _tmpImageUrls: List<String>
          val _tmp: String?
          if (_stmt.isNull(_columnIndexOfImageUrls)) {
            _tmp = null
          } else {
            _tmp = _stmt.getText(_columnIndexOfImageUrls)
          }
          val _tmp_1: List<String>? = AppDatabase.Converters.toStringList(_tmp)
          if (_tmp_1 == null) {
            error("Expected NON-NULL 'kotlin.collections.List<kotlin.String>', but it was NULL.")
          } else {
            _tmpImageUrls = _tmp_1
          }
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpCondition: String?
          if (_stmt.isNull(_columnIndexOfCondition)) {
            _tmpCondition = null
          } else {
            _tmpCondition = _stmt.getText(_columnIndexOfCondition)
          }
          val _tmpHarvestDate: Long?
          if (_stmt.isNull(_columnIndexOfHarvestDate)) {
            _tmpHarvestDate = null
          } else {
            _tmpHarvestDate = _stmt.getLong(_columnIndexOfHarvestDate)
          }
          val _tmpExpiryDate: Long?
          if (_stmt.isNull(_columnIndexOfExpiryDate)) {
            _tmpExpiryDate = null
          } else {
            _tmpExpiryDate = _stmt.getLong(_columnIndexOfExpiryDate)
          }
          val _tmpBirthDate: Long?
          if (_stmt.isNull(_columnIndexOfBirthDate)) {
            _tmpBirthDate = null
          } else {
            _tmpBirthDate = _stmt.getLong(_columnIndexOfBirthDate)
          }
          val _tmpVaccinationRecordsJson: String?
          if (_stmt.isNull(_columnIndexOfVaccinationRecordsJson)) {
            _tmpVaccinationRecordsJson = null
          } else {
            _tmpVaccinationRecordsJson = _stmt.getText(_columnIndexOfVaccinationRecordsJson)
          }
          val _tmpWeightGrams: Double?
          if (_stmt.isNull(_columnIndexOfWeightGrams)) {
            _tmpWeightGrams = null
          } else {
            _tmpWeightGrams = _stmt.getDouble(_columnIndexOfWeightGrams)
          }
          val _tmpHeightCm: Double?
          if (_stmt.isNull(_columnIndexOfHeightCm)) {
            _tmpHeightCm = null
          } else {
            _tmpHeightCm = _stmt.getDouble(_columnIndexOfHeightCm)
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
          val _tmpBreed: String?
          if (_stmt.isNull(_columnIndexOfBreed)) {
            _tmpBreed = null
          } else {
            _tmpBreed = _stmt.getText(_columnIndexOfBreed)
          }
          val _tmpRaisingPurpose: String?
          if (_stmt.isNull(_columnIndexOfRaisingPurpose)) {
            _tmpRaisingPurpose = null
          } else {
            _tmpRaisingPurpose = _stmt.getText(_columnIndexOfRaisingPurpose)
          }
          val _tmpHealthStatus: String?
          if (_stmt.isNull(_columnIndexOfHealthStatus)) {
            _tmpHealthStatus = null
          } else {
            _tmpHealthStatus = _stmt.getText(_columnIndexOfHealthStatus)
          }
          val _tmpBirdCode: String?
          if (_stmt.isNull(_columnIndexOfBirdCode)) {
            _tmpBirdCode = null
          } else {
            _tmpBirdCode = _stmt.getText(_columnIndexOfBirdCode)
          }
          val _tmpColorTag: String?
          if (_stmt.isNull(_columnIndexOfColorTag)) {
            _tmpColorTag = null
          } else {
            _tmpColorTag = _stmt.getText(_columnIndexOfColorTag)
          }
          val _tmpFamilyTreeId: String?
          if (_stmt.isNull(_columnIndexOfFamilyTreeId)) {
            _tmpFamilyTreeId = null
          } else {
            _tmpFamilyTreeId = _stmt.getText(_columnIndexOfFamilyTreeId)
          }
          val _tmpSourceAssetId: String?
          if (_stmt.isNull(_columnIndexOfSourceAssetId)) {
            _tmpSourceAssetId = null
          } else {
            _tmpSourceAssetId = _stmt.getText(_columnIndexOfSourceAssetId)
          }
          val _tmpParentIdsJson: String?
          if (_stmt.isNull(_columnIndexOfParentIdsJson)) {
            _tmpParentIdsJson = null
          } else {
            _tmpParentIdsJson = _stmt.getText(_columnIndexOfParentIdsJson)
          }
          val _tmpBreedingStatus: String?
          if (_stmt.isNull(_columnIndexOfBreedingStatus)) {
            _tmpBreedingStatus = null
          } else {
            _tmpBreedingStatus = _stmt.getText(_columnIndexOfBreedingStatus)
          }
          val _tmpTransferHistoryJson: String?
          if (_stmt.isNull(_columnIndexOfTransferHistoryJson)) {
            _tmpTransferHistoryJson = null
          } else {
            _tmpTransferHistoryJson = _stmt.getText(_columnIndexOfTransferHistoryJson)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpLastModifiedAt: Long
          _tmpLastModifiedAt = _stmt.getLong(_columnIndexOfLastModifiedAt)
          val _tmpIsDeleted: Boolean
          val _tmp_2: Int
          _tmp_2 = _stmt.getLong(_columnIndexOfIsDeleted).toInt()
          _tmpIsDeleted = _tmp_2 != 0
          val _tmpDeletedAt: Long?
          if (_stmt.isNull(_columnIndexOfDeletedAt)) {
            _tmpDeletedAt = null
          } else {
            _tmpDeletedAt = _stmt.getLong(_columnIndexOfDeletedAt)
          }
          val _tmpDirty: Boolean
          val _tmp_3: Int
          _tmp_3 = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp_3 != 0
          val _tmpStage: LifecycleStage?
          val _tmp_4: String?
          if (_stmt.isNull(_columnIndexOfStage)) {
            _tmp_4 = null
          } else {
            _tmp_4 = _stmt.getText(_columnIndexOfStage)
          }
          _tmpStage = AppDatabase.Converters.toLifecycleStage(_tmp_4)
          val _tmpLifecycleStatus: String?
          if (_stmt.isNull(_columnIndexOfLifecycleStatus)) {
            _tmpLifecycleStatus = null
          } else {
            _tmpLifecycleStatus = _stmt.getText(_columnIndexOfLifecycleStatus)
          }
          val _tmpParentMaleId: String?
          if (_stmt.isNull(_columnIndexOfParentMaleId)) {
            _tmpParentMaleId = null
          } else {
            _tmpParentMaleId = _stmt.getText(_columnIndexOfParentMaleId)
          }
          val _tmpParentFemaleId: String?
          if (_stmt.isNull(_columnIndexOfParentFemaleId)) {
            _tmpParentFemaleId = null
          } else {
            _tmpParentFemaleId = _stmt.getText(_columnIndexOfParentFemaleId)
          }
          val _tmpAgeWeeks: Int?
          if (_stmt.isNull(_columnIndexOfAgeWeeks)) {
            _tmpAgeWeeks = null
          } else {
            _tmpAgeWeeks = _stmt.getLong(_columnIndexOfAgeWeeks).toInt()
          }
          val _tmpLastStageTransitionAt: Long?
          if (_stmt.isNull(_columnIndexOfLastStageTransitionAt)) {
            _tmpLastStageTransitionAt = null
          } else {
            _tmpLastStageTransitionAt = _stmt.getLong(_columnIndexOfLastStageTransitionAt)
          }
          val _tmpBreederEligibleAt: Long?
          if (_stmt.isNull(_columnIndexOfBreederEligibleAt)) {
            _tmpBreederEligibleAt = null
          } else {
            _tmpBreederEligibleAt = _stmt.getLong(_columnIndexOfBreederEligibleAt)
          }
          val _tmpIsBatch: Boolean?
          val _tmp_5: Int?
          if (_stmt.isNull(_columnIndexOfIsBatch)) {
            _tmp_5 = null
          } else {
            _tmp_5 = _stmt.getLong(_columnIndexOfIsBatch).toInt()
          }
          _tmpIsBatch = _tmp_5?.let { it != 0 }
          val _tmpBatchId: String?
          if (_stmt.isNull(_columnIndexOfBatchId)) {
            _tmpBatchId = null
          } else {
            _tmpBatchId = _stmt.getText(_columnIndexOfBatchId)
          }
          val _tmpSplitAt: Long?
          if (_stmt.isNull(_columnIndexOfSplitAt)) {
            _tmpSplitAt = null
          } else {
            _tmpSplitAt = _stmt.getLong(_columnIndexOfSplitAt)
          }
          val _tmpSplitIntoIds: String?
          if (_stmt.isNull(_columnIndexOfSplitIntoIds)) {
            _tmpSplitIntoIds = null
          } else {
            _tmpSplitIntoIds = _stmt.getText(_columnIndexOfSplitIntoIds)
          }
          val _tmpDocumentUrls: List<String>
          val _tmp_6: String?
          if (_stmt.isNull(_columnIndexOfDocumentUrls)) {
            _tmp_6 = null
          } else {
            _tmp_6 = _stmt.getText(_columnIndexOfDocumentUrls)
          }
          val _tmp_7: List<String>? = AppDatabase.Converters.toStringList(_tmp_6)
          if (_tmp_7 == null) {
            error("Expected NON-NULL 'kotlin.collections.List<kotlin.String>', but it was NULL.")
          } else {
            _tmpDocumentUrls = _tmp_7
          }
          val _tmpQrCodeUrl: String?
          if (_stmt.isNull(_columnIndexOfQrCodeUrl)) {
            _tmpQrCodeUrl = null
          } else {
            _tmpQrCodeUrl = _stmt.getText(_columnIndexOfQrCodeUrl)
          }
          val _tmpCustomStatus: String?
          if (_stmt.isNull(_columnIndexOfCustomStatus)) {
            _tmpCustomStatus = null
          } else {
            _tmpCustomStatus = _stmt.getText(_columnIndexOfCustomStatus)
          }
          val _tmpDebug: Boolean
          val _tmp_8: Int
          _tmp_8 = _stmt.getLong(_columnIndexOfDebug).toInt()
          _tmpDebug = _tmp_8 != 0
          val _tmpDeliveryOptions: List<String>
          val _tmp_9: String?
          if (_stmt.isNull(_columnIndexOfDeliveryOptions)) {
            _tmp_9 = null
          } else {
            _tmp_9 = _stmt.getText(_columnIndexOfDeliveryOptions)
          }
          val _tmp_10: List<String>? = AppDatabase.Converters.toStringList(_tmp_9)
          if (_tmp_10 == null) {
            error("Expected NON-NULL 'kotlin.collections.List<kotlin.String>', but it was NULL.")
          } else {
            _tmpDeliveryOptions = _tmp_10
          }
          val _tmpDeliveryCost: Double?
          if (_stmt.isNull(_columnIndexOfDeliveryCost)) {
            _tmpDeliveryCost = null
          } else {
            _tmpDeliveryCost = _stmt.getDouble(_columnIndexOfDeliveryCost)
          }
          val _tmpLeadTimeDays: Int?
          if (_stmt.isNull(_columnIndexOfLeadTimeDays)) {
            _tmpLeadTimeDays = null
          } else {
            _tmpLeadTimeDays = _stmt.getLong(_columnIndexOfLeadTimeDays).toInt()
          }
          val _tmpMotherId: String?
          if (_stmt.isNull(_columnIndexOfMotherId)) {
            _tmpMotherId = null
          } else {
            _tmpMotherId = _stmt.getText(_columnIndexOfMotherId)
          }
          val _tmpIsBreedingUnit: Boolean
          val _tmp_11: Int
          _tmp_11 = _stmt.getLong(_columnIndexOfIsBreedingUnit).toInt()
          _tmpIsBreedingUnit = _tmp_11 != 0
          val _tmpEggsCollectedToday: Int
          _tmpEggsCollectedToday = _stmt.getLong(_columnIndexOfEggsCollectedToday).toInt()
          val _tmpLastEggLogDate: Long?
          if (_stmt.isNull(_columnIndexOfLastEggLogDate)) {
            _tmpLastEggLogDate = null
          } else {
            _tmpLastEggLogDate = _stmt.getLong(_columnIndexOfLastEggLogDate)
          }
          val _tmpReadyForSale: Boolean
          val _tmp_12: Int
          _tmp_12 = _stmt.getLong(_columnIndexOfReadyForSale).toInt()
          _tmpReadyForSale = _tmp_12 != 0
          val _tmpTargetWeight: Double?
          if (_stmt.isNull(_columnIndexOfTargetWeight)) {
            _tmpTargetWeight = null
          } else {
            _tmpTargetWeight = _stmt.getDouble(_columnIndexOfTargetWeight)
          }
          val _tmpIsShowcased: Boolean
          val _tmp_13: Int
          _tmp_13 = _stmt.getLong(_columnIndexOfIsShowcased).toInt()
          _tmpIsShowcased = _tmp_13 != 0
          val _tmpExternalVideoUrl: String?
          if (_stmt.isNull(_columnIndexOfExternalVideoUrl)) {
            _tmpExternalVideoUrl = null
          } else {
            _tmpExternalVideoUrl = _stmt.getText(_columnIndexOfExternalVideoUrl)
          }
          val _tmpRecordsLockedAt: Long?
          if (_stmt.isNull(_columnIndexOfRecordsLockedAt)) {
            _tmpRecordsLockedAt = null
          } else {
            _tmpRecordsLockedAt = _stmt.getLong(_columnIndexOfRecordsLockedAt)
          }
          val _tmpAutoLockAfterDays: Int
          _tmpAutoLockAfterDays = _stmt.getLong(_columnIndexOfAutoLockAfterDays).toInt()
          val _tmpLineageHistoryJson: String?
          if (_stmt.isNull(_columnIndexOfLineageHistoryJson)) {
            _tmpLineageHistoryJson = null
          } else {
            _tmpLineageHistoryJson = _stmt.getText(_columnIndexOfLineageHistoryJson)
          }
          val _tmpEditCount: Int
          _tmpEditCount = _stmt.getLong(_columnIndexOfEditCount).toInt()
          val _tmpLastEditedBy: String?
          if (_stmt.isNull(_columnIndexOfLastEditedBy)) {
            _tmpLastEditedBy = null
          } else {
            _tmpLastEditedBy = _stmt.getText(_columnIndexOfLastEditedBy)
          }
          val _tmpAdminFlagged: Boolean
          val _tmp_14: Int
          _tmp_14 = _stmt.getLong(_columnIndexOfAdminFlagged).toInt()
          _tmpAdminFlagged = _tmp_14 != 0
          val _tmpModerationNote: String?
          if (_stmt.isNull(_columnIndexOfModerationNote)) {
            _tmpModerationNote = null
          } else {
            _tmpModerationNote = _stmt.getText(_columnIndexOfModerationNote)
          }
          val _tmpMetadataJson: String
          _tmpMetadataJson = _stmt.getText(_columnIndexOfMetadataJson)
          _item =
              ProductEntity(_tmpProductId,_tmpSellerId,_tmpName,_tmpDescription,_tmpCategory,_tmpPrice,_tmpQuantity,_tmpUnit,_tmpLocation,_tmpLatitude,_tmpLongitude,_tmpImageUrls,_tmpStatus,_tmpCondition,_tmpHarvestDate,_tmpExpiryDate,_tmpBirthDate,_tmpVaccinationRecordsJson,_tmpWeightGrams,_tmpHeightCm,_tmpGender,_tmpColor,_tmpBreed,_tmpRaisingPurpose,_tmpHealthStatus,_tmpBirdCode,_tmpColorTag,_tmpFamilyTreeId,_tmpSourceAssetId,_tmpParentIdsJson,_tmpBreedingStatus,_tmpTransferHistoryJson,_tmpCreatedAt,_tmpUpdatedAt,_tmpLastModifiedAt,_tmpIsDeleted,_tmpDeletedAt,_tmpDirty,_tmpStage,_tmpLifecycleStatus,_tmpParentMaleId,_tmpParentFemaleId,_tmpAgeWeeks,_tmpLastStageTransitionAt,_tmpBreederEligibleAt,_tmpIsBatch,_tmpBatchId,_tmpSplitAt,_tmpSplitIntoIds,_tmpDocumentUrls,_tmpQrCodeUrl,_tmpCustomStatus,_tmpDebug,_tmpDeliveryOptions,_tmpDeliveryCost,_tmpLeadTimeDays,_tmpMotherId,_tmpIsBreedingUnit,_tmpEggsCollectedToday,_tmpLastEggLogDate,_tmpReadyForSale,_tmpTargetWeight,_tmpIsShowcased,_tmpExternalVideoUrl,_tmpRecordsLockedAt,_tmpAutoLockAfterDays,_tmpLineageHistoryJson,_tmpEditCount,_tmpLastEditedBy,_tmpAdminFlagged,_tmpModerationNote,_tmpMetadataJson)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getBatchesReadyToSplit(farmerId: String, minBirthDate: Long):
      List<ProductEntity> {
    val _sql: String =
        "SELECT * FROM products WHERE sellerId = ? AND isBatch = 1 AND status = 'ACTIVE' AND isDeleted = 0 AND birthDate IS NOT NULL AND birthDate <= ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, farmerId)
        _argIndex = 2
        _stmt.bindLong(_argIndex, minBirthDate)
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfSellerId: Int = getColumnIndexOrThrow(_stmt, "sellerId")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfCategory: Int = getColumnIndexOrThrow(_stmt, "category")
        val _columnIndexOfPrice: Int = getColumnIndexOrThrow(_stmt, "price")
        val _columnIndexOfQuantity: Int = getColumnIndexOrThrow(_stmt, "quantity")
        val _columnIndexOfUnit: Int = getColumnIndexOrThrow(_stmt, "unit")
        val _columnIndexOfLocation: Int = getColumnIndexOrThrow(_stmt, "location")
        val _columnIndexOfLatitude: Int = getColumnIndexOrThrow(_stmt, "latitude")
        val _columnIndexOfLongitude: Int = getColumnIndexOrThrow(_stmt, "longitude")
        val _columnIndexOfImageUrls: Int = getColumnIndexOrThrow(_stmt, "imageUrls")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfCondition: Int = getColumnIndexOrThrow(_stmt, "condition")
        val _columnIndexOfHarvestDate: Int = getColumnIndexOrThrow(_stmt, "harvestDate")
        val _columnIndexOfExpiryDate: Int = getColumnIndexOrThrow(_stmt, "expiryDate")
        val _columnIndexOfBirthDate: Int = getColumnIndexOrThrow(_stmt, "birthDate")
        val _columnIndexOfVaccinationRecordsJson: Int = getColumnIndexOrThrow(_stmt,
            "vaccinationRecordsJson")
        val _columnIndexOfWeightGrams: Int = getColumnIndexOrThrow(_stmt, "weightGrams")
        val _columnIndexOfHeightCm: Int = getColumnIndexOrThrow(_stmt, "heightCm")
        val _columnIndexOfGender: Int = getColumnIndexOrThrow(_stmt, "gender")
        val _columnIndexOfColor: Int = getColumnIndexOrThrow(_stmt, "color")
        val _columnIndexOfBreed: Int = getColumnIndexOrThrow(_stmt, "breed")
        val _columnIndexOfRaisingPurpose: Int = getColumnIndexOrThrow(_stmt, "raisingPurpose")
        val _columnIndexOfHealthStatus: Int = getColumnIndexOrThrow(_stmt, "healthStatus")
        val _columnIndexOfBirdCode: Int = getColumnIndexOrThrow(_stmt, "birdCode")
        val _columnIndexOfColorTag: Int = getColumnIndexOrThrow(_stmt, "colorTag")
        val _columnIndexOfFamilyTreeId: Int = getColumnIndexOrThrow(_stmt, "familyTreeId")
        val _columnIndexOfSourceAssetId: Int = getColumnIndexOrThrow(_stmt, "sourceAssetId")
        val _columnIndexOfParentIdsJson: Int = getColumnIndexOrThrow(_stmt, "parentIdsJson")
        val _columnIndexOfBreedingStatus: Int = getColumnIndexOrThrow(_stmt, "breedingStatus")
        val _columnIndexOfTransferHistoryJson: Int = getColumnIndexOrThrow(_stmt,
            "transferHistoryJson")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfLastModifiedAt: Int = getColumnIndexOrThrow(_stmt, "lastModifiedAt")
        val _columnIndexOfIsDeleted: Int = getColumnIndexOrThrow(_stmt, "isDeleted")
        val _columnIndexOfDeletedAt: Int = getColumnIndexOrThrow(_stmt, "deletedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfStage: Int = getColumnIndexOrThrow(_stmt, "stage")
        val _columnIndexOfLifecycleStatus: Int = getColumnIndexOrThrow(_stmt, "lifecycleStatus")
        val _columnIndexOfParentMaleId: Int = getColumnIndexOrThrow(_stmt, "parentMaleId")
        val _columnIndexOfParentFemaleId: Int = getColumnIndexOrThrow(_stmt, "parentFemaleId")
        val _columnIndexOfAgeWeeks: Int = getColumnIndexOrThrow(_stmt, "ageWeeks")
        val _columnIndexOfLastStageTransitionAt: Int = getColumnIndexOrThrow(_stmt,
            "lastStageTransitionAt")
        val _columnIndexOfBreederEligibleAt: Int = getColumnIndexOrThrow(_stmt, "breederEligibleAt")
        val _columnIndexOfIsBatch: Int = getColumnIndexOrThrow(_stmt, "isBatch")
        val _columnIndexOfBatchId: Int = getColumnIndexOrThrow(_stmt, "batchId")
        val _columnIndexOfSplitAt: Int = getColumnIndexOrThrow(_stmt, "splitAt")
        val _columnIndexOfSplitIntoIds: Int = getColumnIndexOrThrow(_stmt, "splitIntoIds")
        val _columnIndexOfDocumentUrls: Int = getColumnIndexOrThrow(_stmt, "documentUrls")
        val _columnIndexOfQrCodeUrl: Int = getColumnIndexOrThrow(_stmt, "qrCodeUrl")
        val _columnIndexOfCustomStatus: Int = getColumnIndexOrThrow(_stmt, "customStatus")
        val _columnIndexOfDebug: Int = getColumnIndexOrThrow(_stmt, "debug")
        val _columnIndexOfDeliveryOptions: Int = getColumnIndexOrThrow(_stmt, "deliveryOptions")
        val _columnIndexOfDeliveryCost: Int = getColumnIndexOrThrow(_stmt, "deliveryCost")
        val _columnIndexOfLeadTimeDays: Int = getColumnIndexOrThrow(_stmt, "leadTimeDays")
        val _columnIndexOfMotherId: Int = getColumnIndexOrThrow(_stmt, "motherId")
        val _columnIndexOfIsBreedingUnit: Int = getColumnIndexOrThrow(_stmt, "isBreedingUnit")
        val _columnIndexOfEggsCollectedToday: Int = getColumnIndexOrThrow(_stmt,
            "eggsCollectedToday")
        val _columnIndexOfLastEggLogDate: Int = getColumnIndexOrThrow(_stmt, "lastEggLogDate")
        val _columnIndexOfReadyForSale: Int = getColumnIndexOrThrow(_stmt, "readyForSale")
        val _columnIndexOfTargetWeight: Int = getColumnIndexOrThrow(_stmt, "targetWeight")
        val _columnIndexOfIsShowcased: Int = getColumnIndexOrThrow(_stmt, "isShowcased")
        val _columnIndexOfExternalVideoUrl: Int = getColumnIndexOrThrow(_stmt, "externalVideoUrl")
        val _columnIndexOfRecordsLockedAt: Int = getColumnIndexOrThrow(_stmt, "recordsLockedAt")
        val _columnIndexOfAutoLockAfterDays: Int = getColumnIndexOrThrow(_stmt, "autoLockAfterDays")
        val _columnIndexOfLineageHistoryJson: Int = getColumnIndexOrThrow(_stmt,
            "lineageHistoryJson")
        val _columnIndexOfEditCount: Int = getColumnIndexOrThrow(_stmt, "editCount")
        val _columnIndexOfLastEditedBy: Int = getColumnIndexOrThrow(_stmt, "lastEditedBy")
        val _columnIndexOfAdminFlagged: Int = getColumnIndexOrThrow(_stmt, "adminFlagged")
        val _columnIndexOfModerationNote: Int = getColumnIndexOrThrow(_stmt, "moderationNote")
        val _columnIndexOfMetadataJson: Int = getColumnIndexOrThrow(_stmt, "metadataJson")
        val _result: MutableList<ProductEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: ProductEntity
          val _tmpProductId: String
          _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          val _tmpSellerId: String
          _tmpSellerId = _stmt.getText(_columnIndexOfSellerId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpDescription: String
          _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          val _tmpCategory: String
          _tmpCategory = _stmt.getText(_columnIndexOfCategory)
          val _tmpPrice: Double
          _tmpPrice = _stmt.getDouble(_columnIndexOfPrice)
          val _tmpQuantity: Double
          _tmpQuantity = _stmt.getDouble(_columnIndexOfQuantity)
          val _tmpUnit: String
          _tmpUnit = _stmt.getText(_columnIndexOfUnit)
          val _tmpLocation: String
          _tmpLocation = _stmt.getText(_columnIndexOfLocation)
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
          val _tmpImageUrls: List<String>
          val _tmp: String?
          if (_stmt.isNull(_columnIndexOfImageUrls)) {
            _tmp = null
          } else {
            _tmp = _stmt.getText(_columnIndexOfImageUrls)
          }
          val _tmp_1: List<String>? = AppDatabase.Converters.toStringList(_tmp)
          if (_tmp_1 == null) {
            error("Expected NON-NULL 'kotlin.collections.List<kotlin.String>', but it was NULL.")
          } else {
            _tmpImageUrls = _tmp_1
          }
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpCondition: String?
          if (_stmt.isNull(_columnIndexOfCondition)) {
            _tmpCondition = null
          } else {
            _tmpCondition = _stmt.getText(_columnIndexOfCondition)
          }
          val _tmpHarvestDate: Long?
          if (_stmt.isNull(_columnIndexOfHarvestDate)) {
            _tmpHarvestDate = null
          } else {
            _tmpHarvestDate = _stmt.getLong(_columnIndexOfHarvestDate)
          }
          val _tmpExpiryDate: Long?
          if (_stmt.isNull(_columnIndexOfExpiryDate)) {
            _tmpExpiryDate = null
          } else {
            _tmpExpiryDate = _stmt.getLong(_columnIndexOfExpiryDate)
          }
          val _tmpBirthDate: Long?
          if (_stmt.isNull(_columnIndexOfBirthDate)) {
            _tmpBirthDate = null
          } else {
            _tmpBirthDate = _stmt.getLong(_columnIndexOfBirthDate)
          }
          val _tmpVaccinationRecordsJson: String?
          if (_stmt.isNull(_columnIndexOfVaccinationRecordsJson)) {
            _tmpVaccinationRecordsJson = null
          } else {
            _tmpVaccinationRecordsJson = _stmt.getText(_columnIndexOfVaccinationRecordsJson)
          }
          val _tmpWeightGrams: Double?
          if (_stmt.isNull(_columnIndexOfWeightGrams)) {
            _tmpWeightGrams = null
          } else {
            _tmpWeightGrams = _stmt.getDouble(_columnIndexOfWeightGrams)
          }
          val _tmpHeightCm: Double?
          if (_stmt.isNull(_columnIndexOfHeightCm)) {
            _tmpHeightCm = null
          } else {
            _tmpHeightCm = _stmt.getDouble(_columnIndexOfHeightCm)
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
          val _tmpBreed: String?
          if (_stmt.isNull(_columnIndexOfBreed)) {
            _tmpBreed = null
          } else {
            _tmpBreed = _stmt.getText(_columnIndexOfBreed)
          }
          val _tmpRaisingPurpose: String?
          if (_stmt.isNull(_columnIndexOfRaisingPurpose)) {
            _tmpRaisingPurpose = null
          } else {
            _tmpRaisingPurpose = _stmt.getText(_columnIndexOfRaisingPurpose)
          }
          val _tmpHealthStatus: String?
          if (_stmt.isNull(_columnIndexOfHealthStatus)) {
            _tmpHealthStatus = null
          } else {
            _tmpHealthStatus = _stmt.getText(_columnIndexOfHealthStatus)
          }
          val _tmpBirdCode: String?
          if (_stmt.isNull(_columnIndexOfBirdCode)) {
            _tmpBirdCode = null
          } else {
            _tmpBirdCode = _stmt.getText(_columnIndexOfBirdCode)
          }
          val _tmpColorTag: String?
          if (_stmt.isNull(_columnIndexOfColorTag)) {
            _tmpColorTag = null
          } else {
            _tmpColorTag = _stmt.getText(_columnIndexOfColorTag)
          }
          val _tmpFamilyTreeId: String?
          if (_stmt.isNull(_columnIndexOfFamilyTreeId)) {
            _tmpFamilyTreeId = null
          } else {
            _tmpFamilyTreeId = _stmt.getText(_columnIndexOfFamilyTreeId)
          }
          val _tmpSourceAssetId: String?
          if (_stmt.isNull(_columnIndexOfSourceAssetId)) {
            _tmpSourceAssetId = null
          } else {
            _tmpSourceAssetId = _stmt.getText(_columnIndexOfSourceAssetId)
          }
          val _tmpParentIdsJson: String?
          if (_stmt.isNull(_columnIndexOfParentIdsJson)) {
            _tmpParentIdsJson = null
          } else {
            _tmpParentIdsJson = _stmt.getText(_columnIndexOfParentIdsJson)
          }
          val _tmpBreedingStatus: String?
          if (_stmt.isNull(_columnIndexOfBreedingStatus)) {
            _tmpBreedingStatus = null
          } else {
            _tmpBreedingStatus = _stmt.getText(_columnIndexOfBreedingStatus)
          }
          val _tmpTransferHistoryJson: String?
          if (_stmt.isNull(_columnIndexOfTransferHistoryJson)) {
            _tmpTransferHistoryJson = null
          } else {
            _tmpTransferHistoryJson = _stmt.getText(_columnIndexOfTransferHistoryJson)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpLastModifiedAt: Long
          _tmpLastModifiedAt = _stmt.getLong(_columnIndexOfLastModifiedAt)
          val _tmpIsDeleted: Boolean
          val _tmp_2: Int
          _tmp_2 = _stmt.getLong(_columnIndexOfIsDeleted).toInt()
          _tmpIsDeleted = _tmp_2 != 0
          val _tmpDeletedAt: Long?
          if (_stmt.isNull(_columnIndexOfDeletedAt)) {
            _tmpDeletedAt = null
          } else {
            _tmpDeletedAt = _stmt.getLong(_columnIndexOfDeletedAt)
          }
          val _tmpDirty: Boolean
          val _tmp_3: Int
          _tmp_3 = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp_3 != 0
          val _tmpStage: LifecycleStage?
          val _tmp_4: String?
          if (_stmt.isNull(_columnIndexOfStage)) {
            _tmp_4 = null
          } else {
            _tmp_4 = _stmt.getText(_columnIndexOfStage)
          }
          _tmpStage = AppDatabase.Converters.toLifecycleStage(_tmp_4)
          val _tmpLifecycleStatus: String?
          if (_stmt.isNull(_columnIndexOfLifecycleStatus)) {
            _tmpLifecycleStatus = null
          } else {
            _tmpLifecycleStatus = _stmt.getText(_columnIndexOfLifecycleStatus)
          }
          val _tmpParentMaleId: String?
          if (_stmt.isNull(_columnIndexOfParentMaleId)) {
            _tmpParentMaleId = null
          } else {
            _tmpParentMaleId = _stmt.getText(_columnIndexOfParentMaleId)
          }
          val _tmpParentFemaleId: String?
          if (_stmt.isNull(_columnIndexOfParentFemaleId)) {
            _tmpParentFemaleId = null
          } else {
            _tmpParentFemaleId = _stmt.getText(_columnIndexOfParentFemaleId)
          }
          val _tmpAgeWeeks: Int?
          if (_stmt.isNull(_columnIndexOfAgeWeeks)) {
            _tmpAgeWeeks = null
          } else {
            _tmpAgeWeeks = _stmt.getLong(_columnIndexOfAgeWeeks).toInt()
          }
          val _tmpLastStageTransitionAt: Long?
          if (_stmt.isNull(_columnIndexOfLastStageTransitionAt)) {
            _tmpLastStageTransitionAt = null
          } else {
            _tmpLastStageTransitionAt = _stmt.getLong(_columnIndexOfLastStageTransitionAt)
          }
          val _tmpBreederEligibleAt: Long?
          if (_stmt.isNull(_columnIndexOfBreederEligibleAt)) {
            _tmpBreederEligibleAt = null
          } else {
            _tmpBreederEligibleAt = _stmt.getLong(_columnIndexOfBreederEligibleAt)
          }
          val _tmpIsBatch: Boolean?
          val _tmp_5: Int?
          if (_stmt.isNull(_columnIndexOfIsBatch)) {
            _tmp_5 = null
          } else {
            _tmp_5 = _stmt.getLong(_columnIndexOfIsBatch).toInt()
          }
          _tmpIsBatch = _tmp_5?.let { it != 0 }
          val _tmpBatchId: String?
          if (_stmt.isNull(_columnIndexOfBatchId)) {
            _tmpBatchId = null
          } else {
            _tmpBatchId = _stmt.getText(_columnIndexOfBatchId)
          }
          val _tmpSplitAt: Long?
          if (_stmt.isNull(_columnIndexOfSplitAt)) {
            _tmpSplitAt = null
          } else {
            _tmpSplitAt = _stmt.getLong(_columnIndexOfSplitAt)
          }
          val _tmpSplitIntoIds: String?
          if (_stmt.isNull(_columnIndexOfSplitIntoIds)) {
            _tmpSplitIntoIds = null
          } else {
            _tmpSplitIntoIds = _stmt.getText(_columnIndexOfSplitIntoIds)
          }
          val _tmpDocumentUrls: List<String>
          val _tmp_6: String?
          if (_stmt.isNull(_columnIndexOfDocumentUrls)) {
            _tmp_6 = null
          } else {
            _tmp_6 = _stmt.getText(_columnIndexOfDocumentUrls)
          }
          val _tmp_7: List<String>? = AppDatabase.Converters.toStringList(_tmp_6)
          if (_tmp_7 == null) {
            error("Expected NON-NULL 'kotlin.collections.List<kotlin.String>', but it was NULL.")
          } else {
            _tmpDocumentUrls = _tmp_7
          }
          val _tmpQrCodeUrl: String?
          if (_stmt.isNull(_columnIndexOfQrCodeUrl)) {
            _tmpQrCodeUrl = null
          } else {
            _tmpQrCodeUrl = _stmt.getText(_columnIndexOfQrCodeUrl)
          }
          val _tmpCustomStatus: String?
          if (_stmt.isNull(_columnIndexOfCustomStatus)) {
            _tmpCustomStatus = null
          } else {
            _tmpCustomStatus = _stmt.getText(_columnIndexOfCustomStatus)
          }
          val _tmpDebug: Boolean
          val _tmp_8: Int
          _tmp_8 = _stmt.getLong(_columnIndexOfDebug).toInt()
          _tmpDebug = _tmp_8 != 0
          val _tmpDeliveryOptions: List<String>
          val _tmp_9: String?
          if (_stmt.isNull(_columnIndexOfDeliveryOptions)) {
            _tmp_9 = null
          } else {
            _tmp_9 = _stmt.getText(_columnIndexOfDeliveryOptions)
          }
          val _tmp_10: List<String>? = AppDatabase.Converters.toStringList(_tmp_9)
          if (_tmp_10 == null) {
            error("Expected NON-NULL 'kotlin.collections.List<kotlin.String>', but it was NULL.")
          } else {
            _tmpDeliveryOptions = _tmp_10
          }
          val _tmpDeliveryCost: Double?
          if (_stmt.isNull(_columnIndexOfDeliveryCost)) {
            _tmpDeliveryCost = null
          } else {
            _tmpDeliveryCost = _stmt.getDouble(_columnIndexOfDeliveryCost)
          }
          val _tmpLeadTimeDays: Int?
          if (_stmt.isNull(_columnIndexOfLeadTimeDays)) {
            _tmpLeadTimeDays = null
          } else {
            _tmpLeadTimeDays = _stmt.getLong(_columnIndexOfLeadTimeDays).toInt()
          }
          val _tmpMotherId: String?
          if (_stmt.isNull(_columnIndexOfMotherId)) {
            _tmpMotherId = null
          } else {
            _tmpMotherId = _stmt.getText(_columnIndexOfMotherId)
          }
          val _tmpIsBreedingUnit: Boolean
          val _tmp_11: Int
          _tmp_11 = _stmt.getLong(_columnIndexOfIsBreedingUnit).toInt()
          _tmpIsBreedingUnit = _tmp_11 != 0
          val _tmpEggsCollectedToday: Int
          _tmpEggsCollectedToday = _stmt.getLong(_columnIndexOfEggsCollectedToday).toInt()
          val _tmpLastEggLogDate: Long?
          if (_stmt.isNull(_columnIndexOfLastEggLogDate)) {
            _tmpLastEggLogDate = null
          } else {
            _tmpLastEggLogDate = _stmt.getLong(_columnIndexOfLastEggLogDate)
          }
          val _tmpReadyForSale: Boolean
          val _tmp_12: Int
          _tmp_12 = _stmt.getLong(_columnIndexOfReadyForSale).toInt()
          _tmpReadyForSale = _tmp_12 != 0
          val _tmpTargetWeight: Double?
          if (_stmt.isNull(_columnIndexOfTargetWeight)) {
            _tmpTargetWeight = null
          } else {
            _tmpTargetWeight = _stmt.getDouble(_columnIndexOfTargetWeight)
          }
          val _tmpIsShowcased: Boolean
          val _tmp_13: Int
          _tmp_13 = _stmt.getLong(_columnIndexOfIsShowcased).toInt()
          _tmpIsShowcased = _tmp_13 != 0
          val _tmpExternalVideoUrl: String?
          if (_stmt.isNull(_columnIndexOfExternalVideoUrl)) {
            _tmpExternalVideoUrl = null
          } else {
            _tmpExternalVideoUrl = _stmt.getText(_columnIndexOfExternalVideoUrl)
          }
          val _tmpRecordsLockedAt: Long?
          if (_stmt.isNull(_columnIndexOfRecordsLockedAt)) {
            _tmpRecordsLockedAt = null
          } else {
            _tmpRecordsLockedAt = _stmt.getLong(_columnIndexOfRecordsLockedAt)
          }
          val _tmpAutoLockAfterDays: Int
          _tmpAutoLockAfterDays = _stmt.getLong(_columnIndexOfAutoLockAfterDays).toInt()
          val _tmpLineageHistoryJson: String?
          if (_stmt.isNull(_columnIndexOfLineageHistoryJson)) {
            _tmpLineageHistoryJson = null
          } else {
            _tmpLineageHistoryJson = _stmt.getText(_columnIndexOfLineageHistoryJson)
          }
          val _tmpEditCount: Int
          _tmpEditCount = _stmt.getLong(_columnIndexOfEditCount).toInt()
          val _tmpLastEditedBy: String?
          if (_stmt.isNull(_columnIndexOfLastEditedBy)) {
            _tmpLastEditedBy = null
          } else {
            _tmpLastEditedBy = _stmt.getText(_columnIndexOfLastEditedBy)
          }
          val _tmpAdminFlagged: Boolean
          val _tmp_14: Int
          _tmp_14 = _stmt.getLong(_columnIndexOfAdminFlagged).toInt()
          _tmpAdminFlagged = _tmp_14 != 0
          val _tmpModerationNote: String?
          if (_stmt.isNull(_columnIndexOfModerationNote)) {
            _tmpModerationNote = null
          } else {
            _tmpModerationNote = _stmt.getText(_columnIndexOfModerationNote)
          }
          val _tmpMetadataJson: String
          _tmpMetadataJson = _stmt.getText(_columnIndexOfMetadataJson)
          _item =
              ProductEntity(_tmpProductId,_tmpSellerId,_tmpName,_tmpDescription,_tmpCategory,_tmpPrice,_tmpQuantity,_tmpUnit,_tmpLocation,_tmpLatitude,_tmpLongitude,_tmpImageUrls,_tmpStatus,_tmpCondition,_tmpHarvestDate,_tmpExpiryDate,_tmpBirthDate,_tmpVaccinationRecordsJson,_tmpWeightGrams,_tmpHeightCm,_tmpGender,_tmpColor,_tmpBreed,_tmpRaisingPurpose,_tmpHealthStatus,_tmpBirdCode,_tmpColorTag,_tmpFamilyTreeId,_tmpSourceAssetId,_tmpParentIdsJson,_tmpBreedingStatus,_tmpTransferHistoryJson,_tmpCreatedAt,_tmpUpdatedAt,_tmpLastModifiedAt,_tmpIsDeleted,_tmpDeletedAt,_tmpDirty,_tmpStage,_tmpLifecycleStatus,_tmpParentMaleId,_tmpParentFemaleId,_tmpAgeWeeks,_tmpLastStageTransitionAt,_tmpBreederEligibleAt,_tmpIsBatch,_tmpBatchId,_tmpSplitAt,_tmpSplitIntoIds,_tmpDocumentUrls,_tmpQrCodeUrl,_tmpCustomStatus,_tmpDebug,_tmpDeliveryOptions,_tmpDeliveryCost,_tmpLeadTimeDays,_tmpMotherId,_tmpIsBreedingUnit,_tmpEggsCollectedToday,_tmpLastEggLogDate,_tmpReadyForSale,_tmpTargetWeight,_tmpIsShowcased,_tmpExternalVideoUrl,_tmpRecordsLockedAt,_tmpAutoLockAfterDays,_tmpLineageHistoryJson,_tmpEditCount,_tmpLastEditedBy,_tmpAdminFlagged,_tmpModerationNote,_tmpMetadataJson)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getProductsWithMissingBirdCodes(): List<ProductEntity> {
    val _sql: String = "SELECT * FROM products WHERE birdCode IS NULL OR colorTag IS NULL"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfSellerId: Int = getColumnIndexOrThrow(_stmt, "sellerId")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfCategory: Int = getColumnIndexOrThrow(_stmt, "category")
        val _columnIndexOfPrice: Int = getColumnIndexOrThrow(_stmt, "price")
        val _columnIndexOfQuantity: Int = getColumnIndexOrThrow(_stmt, "quantity")
        val _columnIndexOfUnit: Int = getColumnIndexOrThrow(_stmt, "unit")
        val _columnIndexOfLocation: Int = getColumnIndexOrThrow(_stmt, "location")
        val _columnIndexOfLatitude: Int = getColumnIndexOrThrow(_stmt, "latitude")
        val _columnIndexOfLongitude: Int = getColumnIndexOrThrow(_stmt, "longitude")
        val _columnIndexOfImageUrls: Int = getColumnIndexOrThrow(_stmt, "imageUrls")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfCondition: Int = getColumnIndexOrThrow(_stmt, "condition")
        val _columnIndexOfHarvestDate: Int = getColumnIndexOrThrow(_stmt, "harvestDate")
        val _columnIndexOfExpiryDate: Int = getColumnIndexOrThrow(_stmt, "expiryDate")
        val _columnIndexOfBirthDate: Int = getColumnIndexOrThrow(_stmt, "birthDate")
        val _columnIndexOfVaccinationRecordsJson: Int = getColumnIndexOrThrow(_stmt,
            "vaccinationRecordsJson")
        val _columnIndexOfWeightGrams: Int = getColumnIndexOrThrow(_stmt, "weightGrams")
        val _columnIndexOfHeightCm: Int = getColumnIndexOrThrow(_stmt, "heightCm")
        val _columnIndexOfGender: Int = getColumnIndexOrThrow(_stmt, "gender")
        val _columnIndexOfColor: Int = getColumnIndexOrThrow(_stmt, "color")
        val _columnIndexOfBreed: Int = getColumnIndexOrThrow(_stmt, "breed")
        val _columnIndexOfRaisingPurpose: Int = getColumnIndexOrThrow(_stmt, "raisingPurpose")
        val _columnIndexOfHealthStatus: Int = getColumnIndexOrThrow(_stmt, "healthStatus")
        val _columnIndexOfBirdCode: Int = getColumnIndexOrThrow(_stmt, "birdCode")
        val _columnIndexOfColorTag: Int = getColumnIndexOrThrow(_stmt, "colorTag")
        val _columnIndexOfFamilyTreeId: Int = getColumnIndexOrThrow(_stmt, "familyTreeId")
        val _columnIndexOfSourceAssetId: Int = getColumnIndexOrThrow(_stmt, "sourceAssetId")
        val _columnIndexOfParentIdsJson: Int = getColumnIndexOrThrow(_stmt, "parentIdsJson")
        val _columnIndexOfBreedingStatus: Int = getColumnIndexOrThrow(_stmt, "breedingStatus")
        val _columnIndexOfTransferHistoryJson: Int = getColumnIndexOrThrow(_stmt,
            "transferHistoryJson")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfLastModifiedAt: Int = getColumnIndexOrThrow(_stmt, "lastModifiedAt")
        val _columnIndexOfIsDeleted: Int = getColumnIndexOrThrow(_stmt, "isDeleted")
        val _columnIndexOfDeletedAt: Int = getColumnIndexOrThrow(_stmt, "deletedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfStage: Int = getColumnIndexOrThrow(_stmt, "stage")
        val _columnIndexOfLifecycleStatus: Int = getColumnIndexOrThrow(_stmt, "lifecycleStatus")
        val _columnIndexOfParentMaleId: Int = getColumnIndexOrThrow(_stmt, "parentMaleId")
        val _columnIndexOfParentFemaleId: Int = getColumnIndexOrThrow(_stmt, "parentFemaleId")
        val _columnIndexOfAgeWeeks: Int = getColumnIndexOrThrow(_stmt, "ageWeeks")
        val _columnIndexOfLastStageTransitionAt: Int = getColumnIndexOrThrow(_stmt,
            "lastStageTransitionAt")
        val _columnIndexOfBreederEligibleAt: Int = getColumnIndexOrThrow(_stmt, "breederEligibleAt")
        val _columnIndexOfIsBatch: Int = getColumnIndexOrThrow(_stmt, "isBatch")
        val _columnIndexOfBatchId: Int = getColumnIndexOrThrow(_stmt, "batchId")
        val _columnIndexOfSplitAt: Int = getColumnIndexOrThrow(_stmt, "splitAt")
        val _columnIndexOfSplitIntoIds: Int = getColumnIndexOrThrow(_stmt, "splitIntoIds")
        val _columnIndexOfDocumentUrls: Int = getColumnIndexOrThrow(_stmt, "documentUrls")
        val _columnIndexOfQrCodeUrl: Int = getColumnIndexOrThrow(_stmt, "qrCodeUrl")
        val _columnIndexOfCustomStatus: Int = getColumnIndexOrThrow(_stmt, "customStatus")
        val _columnIndexOfDebug: Int = getColumnIndexOrThrow(_stmt, "debug")
        val _columnIndexOfDeliveryOptions: Int = getColumnIndexOrThrow(_stmt, "deliveryOptions")
        val _columnIndexOfDeliveryCost: Int = getColumnIndexOrThrow(_stmt, "deliveryCost")
        val _columnIndexOfLeadTimeDays: Int = getColumnIndexOrThrow(_stmt, "leadTimeDays")
        val _columnIndexOfMotherId: Int = getColumnIndexOrThrow(_stmt, "motherId")
        val _columnIndexOfIsBreedingUnit: Int = getColumnIndexOrThrow(_stmt, "isBreedingUnit")
        val _columnIndexOfEggsCollectedToday: Int = getColumnIndexOrThrow(_stmt,
            "eggsCollectedToday")
        val _columnIndexOfLastEggLogDate: Int = getColumnIndexOrThrow(_stmt, "lastEggLogDate")
        val _columnIndexOfReadyForSale: Int = getColumnIndexOrThrow(_stmt, "readyForSale")
        val _columnIndexOfTargetWeight: Int = getColumnIndexOrThrow(_stmt, "targetWeight")
        val _columnIndexOfIsShowcased: Int = getColumnIndexOrThrow(_stmt, "isShowcased")
        val _columnIndexOfExternalVideoUrl: Int = getColumnIndexOrThrow(_stmt, "externalVideoUrl")
        val _columnIndexOfRecordsLockedAt: Int = getColumnIndexOrThrow(_stmt, "recordsLockedAt")
        val _columnIndexOfAutoLockAfterDays: Int = getColumnIndexOrThrow(_stmt, "autoLockAfterDays")
        val _columnIndexOfLineageHistoryJson: Int = getColumnIndexOrThrow(_stmt,
            "lineageHistoryJson")
        val _columnIndexOfEditCount: Int = getColumnIndexOrThrow(_stmt, "editCount")
        val _columnIndexOfLastEditedBy: Int = getColumnIndexOrThrow(_stmt, "lastEditedBy")
        val _columnIndexOfAdminFlagged: Int = getColumnIndexOrThrow(_stmt, "adminFlagged")
        val _columnIndexOfModerationNote: Int = getColumnIndexOrThrow(_stmt, "moderationNote")
        val _columnIndexOfMetadataJson: Int = getColumnIndexOrThrow(_stmt, "metadataJson")
        val _result: MutableList<ProductEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: ProductEntity
          val _tmpProductId: String
          _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          val _tmpSellerId: String
          _tmpSellerId = _stmt.getText(_columnIndexOfSellerId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpDescription: String
          _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          val _tmpCategory: String
          _tmpCategory = _stmt.getText(_columnIndexOfCategory)
          val _tmpPrice: Double
          _tmpPrice = _stmt.getDouble(_columnIndexOfPrice)
          val _tmpQuantity: Double
          _tmpQuantity = _stmt.getDouble(_columnIndexOfQuantity)
          val _tmpUnit: String
          _tmpUnit = _stmt.getText(_columnIndexOfUnit)
          val _tmpLocation: String
          _tmpLocation = _stmt.getText(_columnIndexOfLocation)
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
          val _tmpImageUrls: List<String>
          val _tmp: String?
          if (_stmt.isNull(_columnIndexOfImageUrls)) {
            _tmp = null
          } else {
            _tmp = _stmt.getText(_columnIndexOfImageUrls)
          }
          val _tmp_1: List<String>? = AppDatabase.Converters.toStringList(_tmp)
          if (_tmp_1 == null) {
            error("Expected NON-NULL 'kotlin.collections.List<kotlin.String>', but it was NULL.")
          } else {
            _tmpImageUrls = _tmp_1
          }
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpCondition: String?
          if (_stmt.isNull(_columnIndexOfCondition)) {
            _tmpCondition = null
          } else {
            _tmpCondition = _stmt.getText(_columnIndexOfCondition)
          }
          val _tmpHarvestDate: Long?
          if (_stmt.isNull(_columnIndexOfHarvestDate)) {
            _tmpHarvestDate = null
          } else {
            _tmpHarvestDate = _stmt.getLong(_columnIndexOfHarvestDate)
          }
          val _tmpExpiryDate: Long?
          if (_stmt.isNull(_columnIndexOfExpiryDate)) {
            _tmpExpiryDate = null
          } else {
            _tmpExpiryDate = _stmt.getLong(_columnIndexOfExpiryDate)
          }
          val _tmpBirthDate: Long?
          if (_stmt.isNull(_columnIndexOfBirthDate)) {
            _tmpBirthDate = null
          } else {
            _tmpBirthDate = _stmt.getLong(_columnIndexOfBirthDate)
          }
          val _tmpVaccinationRecordsJson: String?
          if (_stmt.isNull(_columnIndexOfVaccinationRecordsJson)) {
            _tmpVaccinationRecordsJson = null
          } else {
            _tmpVaccinationRecordsJson = _stmt.getText(_columnIndexOfVaccinationRecordsJson)
          }
          val _tmpWeightGrams: Double?
          if (_stmt.isNull(_columnIndexOfWeightGrams)) {
            _tmpWeightGrams = null
          } else {
            _tmpWeightGrams = _stmt.getDouble(_columnIndexOfWeightGrams)
          }
          val _tmpHeightCm: Double?
          if (_stmt.isNull(_columnIndexOfHeightCm)) {
            _tmpHeightCm = null
          } else {
            _tmpHeightCm = _stmt.getDouble(_columnIndexOfHeightCm)
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
          val _tmpBreed: String?
          if (_stmt.isNull(_columnIndexOfBreed)) {
            _tmpBreed = null
          } else {
            _tmpBreed = _stmt.getText(_columnIndexOfBreed)
          }
          val _tmpRaisingPurpose: String?
          if (_stmt.isNull(_columnIndexOfRaisingPurpose)) {
            _tmpRaisingPurpose = null
          } else {
            _tmpRaisingPurpose = _stmt.getText(_columnIndexOfRaisingPurpose)
          }
          val _tmpHealthStatus: String?
          if (_stmt.isNull(_columnIndexOfHealthStatus)) {
            _tmpHealthStatus = null
          } else {
            _tmpHealthStatus = _stmt.getText(_columnIndexOfHealthStatus)
          }
          val _tmpBirdCode: String?
          if (_stmt.isNull(_columnIndexOfBirdCode)) {
            _tmpBirdCode = null
          } else {
            _tmpBirdCode = _stmt.getText(_columnIndexOfBirdCode)
          }
          val _tmpColorTag: String?
          if (_stmt.isNull(_columnIndexOfColorTag)) {
            _tmpColorTag = null
          } else {
            _tmpColorTag = _stmt.getText(_columnIndexOfColorTag)
          }
          val _tmpFamilyTreeId: String?
          if (_stmt.isNull(_columnIndexOfFamilyTreeId)) {
            _tmpFamilyTreeId = null
          } else {
            _tmpFamilyTreeId = _stmt.getText(_columnIndexOfFamilyTreeId)
          }
          val _tmpSourceAssetId: String?
          if (_stmt.isNull(_columnIndexOfSourceAssetId)) {
            _tmpSourceAssetId = null
          } else {
            _tmpSourceAssetId = _stmt.getText(_columnIndexOfSourceAssetId)
          }
          val _tmpParentIdsJson: String?
          if (_stmt.isNull(_columnIndexOfParentIdsJson)) {
            _tmpParentIdsJson = null
          } else {
            _tmpParentIdsJson = _stmt.getText(_columnIndexOfParentIdsJson)
          }
          val _tmpBreedingStatus: String?
          if (_stmt.isNull(_columnIndexOfBreedingStatus)) {
            _tmpBreedingStatus = null
          } else {
            _tmpBreedingStatus = _stmt.getText(_columnIndexOfBreedingStatus)
          }
          val _tmpTransferHistoryJson: String?
          if (_stmt.isNull(_columnIndexOfTransferHistoryJson)) {
            _tmpTransferHistoryJson = null
          } else {
            _tmpTransferHistoryJson = _stmt.getText(_columnIndexOfTransferHistoryJson)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpLastModifiedAt: Long
          _tmpLastModifiedAt = _stmt.getLong(_columnIndexOfLastModifiedAt)
          val _tmpIsDeleted: Boolean
          val _tmp_2: Int
          _tmp_2 = _stmt.getLong(_columnIndexOfIsDeleted).toInt()
          _tmpIsDeleted = _tmp_2 != 0
          val _tmpDeletedAt: Long?
          if (_stmt.isNull(_columnIndexOfDeletedAt)) {
            _tmpDeletedAt = null
          } else {
            _tmpDeletedAt = _stmt.getLong(_columnIndexOfDeletedAt)
          }
          val _tmpDirty: Boolean
          val _tmp_3: Int
          _tmp_3 = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp_3 != 0
          val _tmpStage: LifecycleStage?
          val _tmp_4: String?
          if (_stmt.isNull(_columnIndexOfStage)) {
            _tmp_4 = null
          } else {
            _tmp_4 = _stmt.getText(_columnIndexOfStage)
          }
          _tmpStage = AppDatabase.Converters.toLifecycleStage(_tmp_4)
          val _tmpLifecycleStatus: String?
          if (_stmt.isNull(_columnIndexOfLifecycleStatus)) {
            _tmpLifecycleStatus = null
          } else {
            _tmpLifecycleStatus = _stmt.getText(_columnIndexOfLifecycleStatus)
          }
          val _tmpParentMaleId: String?
          if (_stmt.isNull(_columnIndexOfParentMaleId)) {
            _tmpParentMaleId = null
          } else {
            _tmpParentMaleId = _stmt.getText(_columnIndexOfParentMaleId)
          }
          val _tmpParentFemaleId: String?
          if (_stmt.isNull(_columnIndexOfParentFemaleId)) {
            _tmpParentFemaleId = null
          } else {
            _tmpParentFemaleId = _stmt.getText(_columnIndexOfParentFemaleId)
          }
          val _tmpAgeWeeks: Int?
          if (_stmt.isNull(_columnIndexOfAgeWeeks)) {
            _tmpAgeWeeks = null
          } else {
            _tmpAgeWeeks = _stmt.getLong(_columnIndexOfAgeWeeks).toInt()
          }
          val _tmpLastStageTransitionAt: Long?
          if (_stmt.isNull(_columnIndexOfLastStageTransitionAt)) {
            _tmpLastStageTransitionAt = null
          } else {
            _tmpLastStageTransitionAt = _stmt.getLong(_columnIndexOfLastStageTransitionAt)
          }
          val _tmpBreederEligibleAt: Long?
          if (_stmt.isNull(_columnIndexOfBreederEligibleAt)) {
            _tmpBreederEligibleAt = null
          } else {
            _tmpBreederEligibleAt = _stmt.getLong(_columnIndexOfBreederEligibleAt)
          }
          val _tmpIsBatch: Boolean?
          val _tmp_5: Int?
          if (_stmt.isNull(_columnIndexOfIsBatch)) {
            _tmp_5 = null
          } else {
            _tmp_5 = _stmt.getLong(_columnIndexOfIsBatch).toInt()
          }
          _tmpIsBatch = _tmp_5?.let { it != 0 }
          val _tmpBatchId: String?
          if (_stmt.isNull(_columnIndexOfBatchId)) {
            _tmpBatchId = null
          } else {
            _tmpBatchId = _stmt.getText(_columnIndexOfBatchId)
          }
          val _tmpSplitAt: Long?
          if (_stmt.isNull(_columnIndexOfSplitAt)) {
            _tmpSplitAt = null
          } else {
            _tmpSplitAt = _stmt.getLong(_columnIndexOfSplitAt)
          }
          val _tmpSplitIntoIds: String?
          if (_stmt.isNull(_columnIndexOfSplitIntoIds)) {
            _tmpSplitIntoIds = null
          } else {
            _tmpSplitIntoIds = _stmt.getText(_columnIndexOfSplitIntoIds)
          }
          val _tmpDocumentUrls: List<String>
          val _tmp_6: String?
          if (_stmt.isNull(_columnIndexOfDocumentUrls)) {
            _tmp_6 = null
          } else {
            _tmp_6 = _stmt.getText(_columnIndexOfDocumentUrls)
          }
          val _tmp_7: List<String>? = AppDatabase.Converters.toStringList(_tmp_6)
          if (_tmp_7 == null) {
            error("Expected NON-NULL 'kotlin.collections.List<kotlin.String>', but it was NULL.")
          } else {
            _tmpDocumentUrls = _tmp_7
          }
          val _tmpQrCodeUrl: String?
          if (_stmt.isNull(_columnIndexOfQrCodeUrl)) {
            _tmpQrCodeUrl = null
          } else {
            _tmpQrCodeUrl = _stmt.getText(_columnIndexOfQrCodeUrl)
          }
          val _tmpCustomStatus: String?
          if (_stmt.isNull(_columnIndexOfCustomStatus)) {
            _tmpCustomStatus = null
          } else {
            _tmpCustomStatus = _stmt.getText(_columnIndexOfCustomStatus)
          }
          val _tmpDebug: Boolean
          val _tmp_8: Int
          _tmp_8 = _stmt.getLong(_columnIndexOfDebug).toInt()
          _tmpDebug = _tmp_8 != 0
          val _tmpDeliveryOptions: List<String>
          val _tmp_9: String?
          if (_stmt.isNull(_columnIndexOfDeliveryOptions)) {
            _tmp_9 = null
          } else {
            _tmp_9 = _stmt.getText(_columnIndexOfDeliveryOptions)
          }
          val _tmp_10: List<String>? = AppDatabase.Converters.toStringList(_tmp_9)
          if (_tmp_10 == null) {
            error("Expected NON-NULL 'kotlin.collections.List<kotlin.String>', but it was NULL.")
          } else {
            _tmpDeliveryOptions = _tmp_10
          }
          val _tmpDeliveryCost: Double?
          if (_stmt.isNull(_columnIndexOfDeliveryCost)) {
            _tmpDeliveryCost = null
          } else {
            _tmpDeliveryCost = _stmt.getDouble(_columnIndexOfDeliveryCost)
          }
          val _tmpLeadTimeDays: Int?
          if (_stmt.isNull(_columnIndexOfLeadTimeDays)) {
            _tmpLeadTimeDays = null
          } else {
            _tmpLeadTimeDays = _stmt.getLong(_columnIndexOfLeadTimeDays).toInt()
          }
          val _tmpMotherId: String?
          if (_stmt.isNull(_columnIndexOfMotherId)) {
            _tmpMotherId = null
          } else {
            _tmpMotherId = _stmt.getText(_columnIndexOfMotherId)
          }
          val _tmpIsBreedingUnit: Boolean
          val _tmp_11: Int
          _tmp_11 = _stmt.getLong(_columnIndexOfIsBreedingUnit).toInt()
          _tmpIsBreedingUnit = _tmp_11 != 0
          val _tmpEggsCollectedToday: Int
          _tmpEggsCollectedToday = _stmt.getLong(_columnIndexOfEggsCollectedToday).toInt()
          val _tmpLastEggLogDate: Long?
          if (_stmt.isNull(_columnIndexOfLastEggLogDate)) {
            _tmpLastEggLogDate = null
          } else {
            _tmpLastEggLogDate = _stmt.getLong(_columnIndexOfLastEggLogDate)
          }
          val _tmpReadyForSale: Boolean
          val _tmp_12: Int
          _tmp_12 = _stmt.getLong(_columnIndexOfReadyForSale).toInt()
          _tmpReadyForSale = _tmp_12 != 0
          val _tmpTargetWeight: Double?
          if (_stmt.isNull(_columnIndexOfTargetWeight)) {
            _tmpTargetWeight = null
          } else {
            _tmpTargetWeight = _stmt.getDouble(_columnIndexOfTargetWeight)
          }
          val _tmpIsShowcased: Boolean
          val _tmp_13: Int
          _tmp_13 = _stmt.getLong(_columnIndexOfIsShowcased).toInt()
          _tmpIsShowcased = _tmp_13 != 0
          val _tmpExternalVideoUrl: String?
          if (_stmt.isNull(_columnIndexOfExternalVideoUrl)) {
            _tmpExternalVideoUrl = null
          } else {
            _tmpExternalVideoUrl = _stmt.getText(_columnIndexOfExternalVideoUrl)
          }
          val _tmpRecordsLockedAt: Long?
          if (_stmt.isNull(_columnIndexOfRecordsLockedAt)) {
            _tmpRecordsLockedAt = null
          } else {
            _tmpRecordsLockedAt = _stmt.getLong(_columnIndexOfRecordsLockedAt)
          }
          val _tmpAutoLockAfterDays: Int
          _tmpAutoLockAfterDays = _stmt.getLong(_columnIndexOfAutoLockAfterDays).toInt()
          val _tmpLineageHistoryJson: String?
          if (_stmt.isNull(_columnIndexOfLineageHistoryJson)) {
            _tmpLineageHistoryJson = null
          } else {
            _tmpLineageHistoryJson = _stmt.getText(_columnIndexOfLineageHistoryJson)
          }
          val _tmpEditCount: Int
          _tmpEditCount = _stmt.getLong(_columnIndexOfEditCount).toInt()
          val _tmpLastEditedBy: String?
          if (_stmt.isNull(_columnIndexOfLastEditedBy)) {
            _tmpLastEditedBy = null
          } else {
            _tmpLastEditedBy = _stmt.getText(_columnIndexOfLastEditedBy)
          }
          val _tmpAdminFlagged: Boolean
          val _tmp_14: Int
          _tmp_14 = _stmt.getLong(_columnIndexOfAdminFlagged).toInt()
          _tmpAdminFlagged = _tmp_14 != 0
          val _tmpModerationNote: String?
          if (_stmt.isNull(_columnIndexOfModerationNote)) {
            _tmpModerationNote = null
          } else {
            _tmpModerationNote = _stmt.getText(_columnIndexOfModerationNote)
          }
          val _tmpMetadataJson: String
          _tmpMetadataJson = _stmt.getText(_columnIndexOfMetadataJson)
          _item =
              ProductEntity(_tmpProductId,_tmpSellerId,_tmpName,_tmpDescription,_tmpCategory,_tmpPrice,_tmpQuantity,_tmpUnit,_tmpLocation,_tmpLatitude,_tmpLongitude,_tmpImageUrls,_tmpStatus,_tmpCondition,_tmpHarvestDate,_tmpExpiryDate,_tmpBirthDate,_tmpVaccinationRecordsJson,_tmpWeightGrams,_tmpHeightCm,_tmpGender,_tmpColor,_tmpBreed,_tmpRaisingPurpose,_tmpHealthStatus,_tmpBirdCode,_tmpColorTag,_tmpFamilyTreeId,_tmpSourceAssetId,_tmpParentIdsJson,_tmpBreedingStatus,_tmpTransferHistoryJson,_tmpCreatedAt,_tmpUpdatedAt,_tmpLastModifiedAt,_tmpIsDeleted,_tmpDeletedAt,_tmpDirty,_tmpStage,_tmpLifecycleStatus,_tmpParentMaleId,_tmpParentFemaleId,_tmpAgeWeeks,_tmpLastStageTransitionAt,_tmpBreederEligibleAt,_tmpIsBatch,_tmpBatchId,_tmpSplitAt,_tmpSplitIntoIds,_tmpDocumentUrls,_tmpQrCodeUrl,_tmpCustomStatus,_tmpDebug,_tmpDeliveryOptions,_tmpDeliveryCost,_tmpLeadTimeDays,_tmpMotherId,_tmpIsBreedingUnit,_tmpEggsCollectedToday,_tmpLastEggLogDate,_tmpReadyForSale,_tmpTargetWeight,_tmpIsShowcased,_tmpExternalVideoUrl,_tmpRecordsLockedAt,_tmpAutoLockAfterDays,_tmpLineageHistoryJson,_tmpEditCount,_tmpLastEditedBy,_tmpAdminFlagged,_tmpModerationNote,_tmpMetadataJson)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeStageCounts(farmerId: String): Flow<List<StageCount>> {
    val _sql: String =
        "SELECT stage, COUNT(*) as count FROM products WHERE sellerId = ? AND (lifecycleStatus = 'ACTIVE' OR lifecycleStatus IS NULL) AND isDeleted = 0 GROUP BY stage"
    return createFlow(__db, false, arrayOf("products")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, farmerId)
        val _columnIndexOfStage: Int = 0
        val _columnIndexOfCount: Int = 1
        val _result: MutableList<StageCount> = mutableListOf()
        while (_stmt.step()) {
          val _item: StageCount
          val _tmpStage: LifecycleStage?
          val _tmp: String?
          if (_stmt.isNull(_columnIndexOfStage)) {
            _tmp = null
          } else {
            _tmp = _stmt.getText(_columnIndexOfStage)
          }
          _tmpStage = AppDatabase.Converters.toLifecycleStage(_tmp)
          val _tmpCount: Int
          _tmpCount = _stmt.getLong(_columnIndexOfCount).toInt()
          _item = StageCount(_tmpStage,_tmpCount)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeReadyToGrowCount(farmerId: String): Flow<Int> {
    val _sql: String =
        "SELECT COUNT(*) FROM products WHERE sellerId = ? AND stage = 'CHICK' AND ageWeeks >= 8 AND (lifecycleStatus = 'ACTIVE' OR lifecycleStatus IS NULL) AND isDeleted = 0"
    return createFlow(__db, false, arrayOf("products")) { _connection ->
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

  public override fun observeReadyToLayCount(farmerId: String): Flow<Int> {
    val _sql: String =
        "SELECT COUNT(*) FROM products WHERE sellerId = ? AND stage = 'GROWER' AND ageWeeks >= 18 AND (lifecycleStatus = 'ACTIVE' OR lifecycleStatus IS NULL) AND isDeleted = 0"
    return createFlow(__db, false, arrayOf("products")) { _connection ->
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

  public override suspend fun getDistinctSellerIds(): List<String> {
    val _sql: String = "SELECT DISTINCT sellerId FROM products WHERE isDeleted = 0"
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

  public override suspend fun countActiveByFarmer(farmerId: String): Int {
    val _sql: String =
        "SELECT COUNT(*) FROM products WHERE sellerId = ? AND isDeleted = 0 AND (lifecycleStatus = 'ACTIVE' OR lifecycleStatus IS NULL)"
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

  public override suspend fun countBatchesByFarmer(farmerId: String): Int {
    val _sql: String =
        "SELECT COUNT(*) FROM products WHERE sellerId = ? AND isBatch = 1 AND isDeleted = 0 AND (lifecycleStatus = 'ACTIVE' OR lifecycleStatus IS NULL)"
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

  public override suspend fun getActiveWithWeightByFarmer(farmerId: String): List<ProductEntity> {
    val _sql: String =
        "SELECT * FROM products WHERE sellerId = ? AND isDeleted = 0 AND (lifecycleStatus = 'ACTIVE' OR lifecycleStatus IS NULL) AND weightGrams IS NOT NULL AND isBatch = 0"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, farmerId)
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfSellerId: Int = getColumnIndexOrThrow(_stmt, "sellerId")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfCategory: Int = getColumnIndexOrThrow(_stmt, "category")
        val _columnIndexOfPrice: Int = getColumnIndexOrThrow(_stmt, "price")
        val _columnIndexOfQuantity: Int = getColumnIndexOrThrow(_stmt, "quantity")
        val _columnIndexOfUnit: Int = getColumnIndexOrThrow(_stmt, "unit")
        val _columnIndexOfLocation: Int = getColumnIndexOrThrow(_stmt, "location")
        val _columnIndexOfLatitude: Int = getColumnIndexOrThrow(_stmt, "latitude")
        val _columnIndexOfLongitude: Int = getColumnIndexOrThrow(_stmt, "longitude")
        val _columnIndexOfImageUrls: Int = getColumnIndexOrThrow(_stmt, "imageUrls")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfCondition: Int = getColumnIndexOrThrow(_stmt, "condition")
        val _columnIndexOfHarvestDate: Int = getColumnIndexOrThrow(_stmt, "harvestDate")
        val _columnIndexOfExpiryDate: Int = getColumnIndexOrThrow(_stmt, "expiryDate")
        val _columnIndexOfBirthDate: Int = getColumnIndexOrThrow(_stmt, "birthDate")
        val _columnIndexOfVaccinationRecordsJson: Int = getColumnIndexOrThrow(_stmt,
            "vaccinationRecordsJson")
        val _columnIndexOfWeightGrams: Int = getColumnIndexOrThrow(_stmt, "weightGrams")
        val _columnIndexOfHeightCm: Int = getColumnIndexOrThrow(_stmt, "heightCm")
        val _columnIndexOfGender: Int = getColumnIndexOrThrow(_stmt, "gender")
        val _columnIndexOfColor: Int = getColumnIndexOrThrow(_stmt, "color")
        val _columnIndexOfBreed: Int = getColumnIndexOrThrow(_stmt, "breed")
        val _columnIndexOfRaisingPurpose: Int = getColumnIndexOrThrow(_stmt, "raisingPurpose")
        val _columnIndexOfHealthStatus: Int = getColumnIndexOrThrow(_stmt, "healthStatus")
        val _columnIndexOfBirdCode: Int = getColumnIndexOrThrow(_stmt, "birdCode")
        val _columnIndexOfColorTag: Int = getColumnIndexOrThrow(_stmt, "colorTag")
        val _columnIndexOfFamilyTreeId: Int = getColumnIndexOrThrow(_stmt, "familyTreeId")
        val _columnIndexOfSourceAssetId: Int = getColumnIndexOrThrow(_stmt, "sourceAssetId")
        val _columnIndexOfParentIdsJson: Int = getColumnIndexOrThrow(_stmt, "parentIdsJson")
        val _columnIndexOfBreedingStatus: Int = getColumnIndexOrThrow(_stmt, "breedingStatus")
        val _columnIndexOfTransferHistoryJson: Int = getColumnIndexOrThrow(_stmt,
            "transferHistoryJson")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfLastModifiedAt: Int = getColumnIndexOrThrow(_stmt, "lastModifiedAt")
        val _columnIndexOfIsDeleted: Int = getColumnIndexOrThrow(_stmt, "isDeleted")
        val _columnIndexOfDeletedAt: Int = getColumnIndexOrThrow(_stmt, "deletedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfStage: Int = getColumnIndexOrThrow(_stmt, "stage")
        val _columnIndexOfLifecycleStatus: Int = getColumnIndexOrThrow(_stmt, "lifecycleStatus")
        val _columnIndexOfParentMaleId: Int = getColumnIndexOrThrow(_stmt, "parentMaleId")
        val _columnIndexOfParentFemaleId: Int = getColumnIndexOrThrow(_stmt, "parentFemaleId")
        val _columnIndexOfAgeWeeks: Int = getColumnIndexOrThrow(_stmt, "ageWeeks")
        val _columnIndexOfLastStageTransitionAt: Int = getColumnIndexOrThrow(_stmt,
            "lastStageTransitionAt")
        val _columnIndexOfBreederEligibleAt: Int = getColumnIndexOrThrow(_stmt, "breederEligibleAt")
        val _columnIndexOfIsBatch: Int = getColumnIndexOrThrow(_stmt, "isBatch")
        val _columnIndexOfBatchId: Int = getColumnIndexOrThrow(_stmt, "batchId")
        val _columnIndexOfSplitAt: Int = getColumnIndexOrThrow(_stmt, "splitAt")
        val _columnIndexOfSplitIntoIds: Int = getColumnIndexOrThrow(_stmt, "splitIntoIds")
        val _columnIndexOfDocumentUrls: Int = getColumnIndexOrThrow(_stmt, "documentUrls")
        val _columnIndexOfQrCodeUrl: Int = getColumnIndexOrThrow(_stmt, "qrCodeUrl")
        val _columnIndexOfCustomStatus: Int = getColumnIndexOrThrow(_stmt, "customStatus")
        val _columnIndexOfDebug: Int = getColumnIndexOrThrow(_stmt, "debug")
        val _columnIndexOfDeliveryOptions: Int = getColumnIndexOrThrow(_stmt, "deliveryOptions")
        val _columnIndexOfDeliveryCost: Int = getColumnIndexOrThrow(_stmt, "deliveryCost")
        val _columnIndexOfLeadTimeDays: Int = getColumnIndexOrThrow(_stmt, "leadTimeDays")
        val _columnIndexOfMotherId: Int = getColumnIndexOrThrow(_stmt, "motherId")
        val _columnIndexOfIsBreedingUnit: Int = getColumnIndexOrThrow(_stmt, "isBreedingUnit")
        val _columnIndexOfEggsCollectedToday: Int = getColumnIndexOrThrow(_stmt,
            "eggsCollectedToday")
        val _columnIndexOfLastEggLogDate: Int = getColumnIndexOrThrow(_stmt, "lastEggLogDate")
        val _columnIndexOfReadyForSale: Int = getColumnIndexOrThrow(_stmt, "readyForSale")
        val _columnIndexOfTargetWeight: Int = getColumnIndexOrThrow(_stmt, "targetWeight")
        val _columnIndexOfIsShowcased: Int = getColumnIndexOrThrow(_stmt, "isShowcased")
        val _columnIndexOfExternalVideoUrl: Int = getColumnIndexOrThrow(_stmt, "externalVideoUrl")
        val _columnIndexOfRecordsLockedAt: Int = getColumnIndexOrThrow(_stmt, "recordsLockedAt")
        val _columnIndexOfAutoLockAfterDays: Int = getColumnIndexOrThrow(_stmt, "autoLockAfterDays")
        val _columnIndexOfLineageHistoryJson: Int = getColumnIndexOrThrow(_stmt,
            "lineageHistoryJson")
        val _columnIndexOfEditCount: Int = getColumnIndexOrThrow(_stmt, "editCount")
        val _columnIndexOfLastEditedBy: Int = getColumnIndexOrThrow(_stmt, "lastEditedBy")
        val _columnIndexOfAdminFlagged: Int = getColumnIndexOrThrow(_stmt, "adminFlagged")
        val _columnIndexOfModerationNote: Int = getColumnIndexOrThrow(_stmt, "moderationNote")
        val _columnIndexOfMetadataJson: Int = getColumnIndexOrThrow(_stmt, "metadataJson")
        val _result: MutableList<ProductEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: ProductEntity
          val _tmpProductId: String
          _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          val _tmpSellerId: String
          _tmpSellerId = _stmt.getText(_columnIndexOfSellerId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpDescription: String
          _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          val _tmpCategory: String
          _tmpCategory = _stmt.getText(_columnIndexOfCategory)
          val _tmpPrice: Double
          _tmpPrice = _stmt.getDouble(_columnIndexOfPrice)
          val _tmpQuantity: Double
          _tmpQuantity = _stmt.getDouble(_columnIndexOfQuantity)
          val _tmpUnit: String
          _tmpUnit = _stmt.getText(_columnIndexOfUnit)
          val _tmpLocation: String
          _tmpLocation = _stmt.getText(_columnIndexOfLocation)
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
          val _tmpImageUrls: List<String>
          val _tmp: String?
          if (_stmt.isNull(_columnIndexOfImageUrls)) {
            _tmp = null
          } else {
            _tmp = _stmt.getText(_columnIndexOfImageUrls)
          }
          val _tmp_1: List<String>? = AppDatabase.Converters.toStringList(_tmp)
          if (_tmp_1 == null) {
            error("Expected NON-NULL 'kotlin.collections.List<kotlin.String>', but it was NULL.")
          } else {
            _tmpImageUrls = _tmp_1
          }
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpCondition: String?
          if (_stmt.isNull(_columnIndexOfCondition)) {
            _tmpCondition = null
          } else {
            _tmpCondition = _stmt.getText(_columnIndexOfCondition)
          }
          val _tmpHarvestDate: Long?
          if (_stmt.isNull(_columnIndexOfHarvestDate)) {
            _tmpHarvestDate = null
          } else {
            _tmpHarvestDate = _stmt.getLong(_columnIndexOfHarvestDate)
          }
          val _tmpExpiryDate: Long?
          if (_stmt.isNull(_columnIndexOfExpiryDate)) {
            _tmpExpiryDate = null
          } else {
            _tmpExpiryDate = _stmt.getLong(_columnIndexOfExpiryDate)
          }
          val _tmpBirthDate: Long?
          if (_stmt.isNull(_columnIndexOfBirthDate)) {
            _tmpBirthDate = null
          } else {
            _tmpBirthDate = _stmt.getLong(_columnIndexOfBirthDate)
          }
          val _tmpVaccinationRecordsJson: String?
          if (_stmt.isNull(_columnIndexOfVaccinationRecordsJson)) {
            _tmpVaccinationRecordsJson = null
          } else {
            _tmpVaccinationRecordsJson = _stmt.getText(_columnIndexOfVaccinationRecordsJson)
          }
          val _tmpWeightGrams: Double?
          if (_stmt.isNull(_columnIndexOfWeightGrams)) {
            _tmpWeightGrams = null
          } else {
            _tmpWeightGrams = _stmt.getDouble(_columnIndexOfWeightGrams)
          }
          val _tmpHeightCm: Double?
          if (_stmt.isNull(_columnIndexOfHeightCm)) {
            _tmpHeightCm = null
          } else {
            _tmpHeightCm = _stmt.getDouble(_columnIndexOfHeightCm)
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
          val _tmpBreed: String?
          if (_stmt.isNull(_columnIndexOfBreed)) {
            _tmpBreed = null
          } else {
            _tmpBreed = _stmt.getText(_columnIndexOfBreed)
          }
          val _tmpRaisingPurpose: String?
          if (_stmt.isNull(_columnIndexOfRaisingPurpose)) {
            _tmpRaisingPurpose = null
          } else {
            _tmpRaisingPurpose = _stmt.getText(_columnIndexOfRaisingPurpose)
          }
          val _tmpHealthStatus: String?
          if (_stmt.isNull(_columnIndexOfHealthStatus)) {
            _tmpHealthStatus = null
          } else {
            _tmpHealthStatus = _stmt.getText(_columnIndexOfHealthStatus)
          }
          val _tmpBirdCode: String?
          if (_stmt.isNull(_columnIndexOfBirdCode)) {
            _tmpBirdCode = null
          } else {
            _tmpBirdCode = _stmt.getText(_columnIndexOfBirdCode)
          }
          val _tmpColorTag: String?
          if (_stmt.isNull(_columnIndexOfColorTag)) {
            _tmpColorTag = null
          } else {
            _tmpColorTag = _stmt.getText(_columnIndexOfColorTag)
          }
          val _tmpFamilyTreeId: String?
          if (_stmt.isNull(_columnIndexOfFamilyTreeId)) {
            _tmpFamilyTreeId = null
          } else {
            _tmpFamilyTreeId = _stmt.getText(_columnIndexOfFamilyTreeId)
          }
          val _tmpSourceAssetId: String?
          if (_stmt.isNull(_columnIndexOfSourceAssetId)) {
            _tmpSourceAssetId = null
          } else {
            _tmpSourceAssetId = _stmt.getText(_columnIndexOfSourceAssetId)
          }
          val _tmpParentIdsJson: String?
          if (_stmt.isNull(_columnIndexOfParentIdsJson)) {
            _tmpParentIdsJson = null
          } else {
            _tmpParentIdsJson = _stmt.getText(_columnIndexOfParentIdsJson)
          }
          val _tmpBreedingStatus: String?
          if (_stmt.isNull(_columnIndexOfBreedingStatus)) {
            _tmpBreedingStatus = null
          } else {
            _tmpBreedingStatus = _stmt.getText(_columnIndexOfBreedingStatus)
          }
          val _tmpTransferHistoryJson: String?
          if (_stmt.isNull(_columnIndexOfTransferHistoryJson)) {
            _tmpTransferHistoryJson = null
          } else {
            _tmpTransferHistoryJson = _stmt.getText(_columnIndexOfTransferHistoryJson)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpLastModifiedAt: Long
          _tmpLastModifiedAt = _stmt.getLong(_columnIndexOfLastModifiedAt)
          val _tmpIsDeleted: Boolean
          val _tmp_2: Int
          _tmp_2 = _stmt.getLong(_columnIndexOfIsDeleted).toInt()
          _tmpIsDeleted = _tmp_2 != 0
          val _tmpDeletedAt: Long?
          if (_stmt.isNull(_columnIndexOfDeletedAt)) {
            _tmpDeletedAt = null
          } else {
            _tmpDeletedAt = _stmt.getLong(_columnIndexOfDeletedAt)
          }
          val _tmpDirty: Boolean
          val _tmp_3: Int
          _tmp_3 = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp_3 != 0
          val _tmpStage: LifecycleStage?
          val _tmp_4: String?
          if (_stmt.isNull(_columnIndexOfStage)) {
            _tmp_4 = null
          } else {
            _tmp_4 = _stmt.getText(_columnIndexOfStage)
          }
          _tmpStage = AppDatabase.Converters.toLifecycleStage(_tmp_4)
          val _tmpLifecycleStatus: String?
          if (_stmt.isNull(_columnIndexOfLifecycleStatus)) {
            _tmpLifecycleStatus = null
          } else {
            _tmpLifecycleStatus = _stmt.getText(_columnIndexOfLifecycleStatus)
          }
          val _tmpParentMaleId: String?
          if (_stmt.isNull(_columnIndexOfParentMaleId)) {
            _tmpParentMaleId = null
          } else {
            _tmpParentMaleId = _stmt.getText(_columnIndexOfParentMaleId)
          }
          val _tmpParentFemaleId: String?
          if (_stmt.isNull(_columnIndexOfParentFemaleId)) {
            _tmpParentFemaleId = null
          } else {
            _tmpParentFemaleId = _stmt.getText(_columnIndexOfParentFemaleId)
          }
          val _tmpAgeWeeks: Int?
          if (_stmt.isNull(_columnIndexOfAgeWeeks)) {
            _tmpAgeWeeks = null
          } else {
            _tmpAgeWeeks = _stmt.getLong(_columnIndexOfAgeWeeks).toInt()
          }
          val _tmpLastStageTransitionAt: Long?
          if (_stmt.isNull(_columnIndexOfLastStageTransitionAt)) {
            _tmpLastStageTransitionAt = null
          } else {
            _tmpLastStageTransitionAt = _stmt.getLong(_columnIndexOfLastStageTransitionAt)
          }
          val _tmpBreederEligibleAt: Long?
          if (_stmt.isNull(_columnIndexOfBreederEligibleAt)) {
            _tmpBreederEligibleAt = null
          } else {
            _tmpBreederEligibleAt = _stmt.getLong(_columnIndexOfBreederEligibleAt)
          }
          val _tmpIsBatch: Boolean?
          val _tmp_5: Int?
          if (_stmt.isNull(_columnIndexOfIsBatch)) {
            _tmp_5 = null
          } else {
            _tmp_5 = _stmt.getLong(_columnIndexOfIsBatch).toInt()
          }
          _tmpIsBatch = _tmp_5?.let { it != 0 }
          val _tmpBatchId: String?
          if (_stmt.isNull(_columnIndexOfBatchId)) {
            _tmpBatchId = null
          } else {
            _tmpBatchId = _stmt.getText(_columnIndexOfBatchId)
          }
          val _tmpSplitAt: Long?
          if (_stmt.isNull(_columnIndexOfSplitAt)) {
            _tmpSplitAt = null
          } else {
            _tmpSplitAt = _stmt.getLong(_columnIndexOfSplitAt)
          }
          val _tmpSplitIntoIds: String?
          if (_stmt.isNull(_columnIndexOfSplitIntoIds)) {
            _tmpSplitIntoIds = null
          } else {
            _tmpSplitIntoIds = _stmt.getText(_columnIndexOfSplitIntoIds)
          }
          val _tmpDocumentUrls: List<String>
          val _tmp_6: String?
          if (_stmt.isNull(_columnIndexOfDocumentUrls)) {
            _tmp_6 = null
          } else {
            _tmp_6 = _stmt.getText(_columnIndexOfDocumentUrls)
          }
          val _tmp_7: List<String>? = AppDatabase.Converters.toStringList(_tmp_6)
          if (_tmp_7 == null) {
            error("Expected NON-NULL 'kotlin.collections.List<kotlin.String>', but it was NULL.")
          } else {
            _tmpDocumentUrls = _tmp_7
          }
          val _tmpQrCodeUrl: String?
          if (_stmt.isNull(_columnIndexOfQrCodeUrl)) {
            _tmpQrCodeUrl = null
          } else {
            _tmpQrCodeUrl = _stmt.getText(_columnIndexOfQrCodeUrl)
          }
          val _tmpCustomStatus: String?
          if (_stmt.isNull(_columnIndexOfCustomStatus)) {
            _tmpCustomStatus = null
          } else {
            _tmpCustomStatus = _stmt.getText(_columnIndexOfCustomStatus)
          }
          val _tmpDebug: Boolean
          val _tmp_8: Int
          _tmp_8 = _stmt.getLong(_columnIndexOfDebug).toInt()
          _tmpDebug = _tmp_8 != 0
          val _tmpDeliveryOptions: List<String>
          val _tmp_9: String?
          if (_stmt.isNull(_columnIndexOfDeliveryOptions)) {
            _tmp_9 = null
          } else {
            _tmp_9 = _stmt.getText(_columnIndexOfDeliveryOptions)
          }
          val _tmp_10: List<String>? = AppDatabase.Converters.toStringList(_tmp_9)
          if (_tmp_10 == null) {
            error("Expected NON-NULL 'kotlin.collections.List<kotlin.String>', but it was NULL.")
          } else {
            _tmpDeliveryOptions = _tmp_10
          }
          val _tmpDeliveryCost: Double?
          if (_stmt.isNull(_columnIndexOfDeliveryCost)) {
            _tmpDeliveryCost = null
          } else {
            _tmpDeliveryCost = _stmt.getDouble(_columnIndexOfDeliveryCost)
          }
          val _tmpLeadTimeDays: Int?
          if (_stmt.isNull(_columnIndexOfLeadTimeDays)) {
            _tmpLeadTimeDays = null
          } else {
            _tmpLeadTimeDays = _stmt.getLong(_columnIndexOfLeadTimeDays).toInt()
          }
          val _tmpMotherId: String?
          if (_stmt.isNull(_columnIndexOfMotherId)) {
            _tmpMotherId = null
          } else {
            _tmpMotherId = _stmt.getText(_columnIndexOfMotherId)
          }
          val _tmpIsBreedingUnit: Boolean
          val _tmp_11: Int
          _tmp_11 = _stmt.getLong(_columnIndexOfIsBreedingUnit).toInt()
          _tmpIsBreedingUnit = _tmp_11 != 0
          val _tmpEggsCollectedToday: Int
          _tmpEggsCollectedToday = _stmt.getLong(_columnIndexOfEggsCollectedToday).toInt()
          val _tmpLastEggLogDate: Long?
          if (_stmt.isNull(_columnIndexOfLastEggLogDate)) {
            _tmpLastEggLogDate = null
          } else {
            _tmpLastEggLogDate = _stmt.getLong(_columnIndexOfLastEggLogDate)
          }
          val _tmpReadyForSale: Boolean
          val _tmp_12: Int
          _tmp_12 = _stmt.getLong(_columnIndexOfReadyForSale).toInt()
          _tmpReadyForSale = _tmp_12 != 0
          val _tmpTargetWeight: Double?
          if (_stmt.isNull(_columnIndexOfTargetWeight)) {
            _tmpTargetWeight = null
          } else {
            _tmpTargetWeight = _stmt.getDouble(_columnIndexOfTargetWeight)
          }
          val _tmpIsShowcased: Boolean
          val _tmp_13: Int
          _tmp_13 = _stmt.getLong(_columnIndexOfIsShowcased).toInt()
          _tmpIsShowcased = _tmp_13 != 0
          val _tmpExternalVideoUrl: String?
          if (_stmt.isNull(_columnIndexOfExternalVideoUrl)) {
            _tmpExternalVideoUrl = null
          } else {
            _tmpExternalVideoUrl = _stmt.getText(_columnIndexOfExternalVideoUrl)
          }
          val _tmpRecordsLockedAt: Long?
          if (_stmt.isNull(_columnIndexOfRecordsLockedAt)) {
            _tmpRecordsLockedAt = null
          } else {
            _tmpRecordsLockedAt = _stmt.getLong(_columnIndexOfRecordsLockedAt)
          }
          val _tmpAutoLockAfterDays: Int
          _tmpAutoLockAfterDays = _stmt.getLong(_columnIndexOfAutoLockAfterDays).toInt()
          val _tmpLineageHistoryJson: String?
          if (_stmt.isNull(_columnIndexOfLineageHistoryJson)) {
            _tmpLineageHistoryJson = null
          } else {
            _tmpLineageHistoryJson = _stmt.getText(_columnIndexOfLineageHistoryJson)
          }
          val _tmpEditCount: Int
          _tmpEditCount = _stmt.getLong(_columnIndexOfEditCount).toInt()
          val _tmpLastEditedBy: String?
          if (_stmt.isNull(_columnIndexOfLastEditedBy)) {
            _tmpLastEditedBy = null
          } else {
            _tmpLastEditedBy = _stmt.getText(_columnIndexOfLastEditedBy)
          }
          val _tmpAdminFlagged: Boolean
          val _tmp_14: Int
          _tmp_14 = _stmt.getLong(_columnIndexOfAdminFlagged).toInt()
          _tmpAdminFlagged = _tmp_14 != 0
          val _tmpModerationNote: String?
          if (_stmt.isNull(_columnIndexOfModerationNote)) {
            _tmpModerationNote = null
          } else {
            _tmpModerationNote = _stmt.getText(_columnIndexOfModerationNote)
          }
          val _tmpMetadataJson: String
          _tmpMetadataJson = _stmt.getText(_columnIndexOfMetadataJson)
          _item =
              ProductEntity(_tmpProductId,_tmpSellerId,_tmpName,_tmpDescription,_tmpCategory,_tmpPrice,_tmpQuantity,_tmpUnit,_tmpLocation,_tmpLatitude,_tmpLongitude,_tmpImageUrls,_tmpStatus,_tmpCondition,_tmpHarvestDate,_tmpExpiryDate,_tmpBirthDate,_tmpVaccinationRecordsJson,_tmpWeightGrams,_tmpHeightCm,_tmpGender,_tmpColor,_tmpBreed,_tmpRaisingPurpose,_tmpHealthStatus,_tmpBirdCode,_tmpColorTag,_tmpFamilyTreeId,_tmpSourceAssetId,_tmpParentIdsJson,_tmpBreedingStatus,_tmpTransferHistoryJson,_tmpCreatedAt,_tmpUpdatedAt,_tmpLastModifiedAt,_tmpIsDeleted,_tmpDeletedAt,_tmpDirty,_tmpStage,_tmpLifecycleStatus,_tmpParentMaleId,_tmpParentFemaleId,_tmpAgeWeeks,_tmpLastStageTransitionAt,_tmpBreederEligibleAt,_tmpIsBatch,_tmpBatchId,_tmpSplitAt,_tmpSplitIntoIds,_tmpDocumentUrls,_tmpQrCodeUrl,_tmpCustomStatus,_tmpDebug,_tmpDeliveryOptions,_tmpDeliveryCost,_tmpLeadTimeDays,_tmpMotherId,_tmpIsBreedingUnit,_tmpEggsCollectedToday,_tmpLastEggLogDate,_tmpReadyForSale,_tmpTargetWeight,_tmpIsShowcased,_tmpExternalVideoUrl,_tmpRecordsLockedAt,_tmpAutoLockAfterDays,_tmpLineageHistoryJson,_tmpEditCount,_tmpLastEditedBy,_tmpAdminFlagged,_tmpModerationNote,_tmpMetadataJson)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getListingForAsset(assetId: String): Flow<ProductEntity?> {
    val _sql: String = "SELECT * FROM products WHERE sourceAssetId = ? AND isDeleted = 0 LIMIT 1"
    return createFlow(__db, false, arrayOf("products")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, assetId)
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfSellerId: Int = getColumnIndexOrThrow(_stmt, "sellerId")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfCategory: Int = getColumnIndexOrThrow(_stmt, "category")
        val _columnIndexOfPrice: Int = getColumnIndexOrThrow(_stmt, "price")
        val _columnIndexOfQuantity: Int = getColumnIndexOrThrow(_stmt, "quantity")
        val _columnIndexOfUnit: Int = getColumnIndexOrThrow(_stmt, "unit")
        val _columnIndexOfLocation: Int = getColumnIndexOrThrow(_stmt, "location")
        val _columnIndexOfLatitude: Int = getColumnIndexOrThrow(_stmt, "latitude")
        val _columnIndexOfLongitude: Int = getColumnIndexOrThrow(_stmt, "longitude")
        val _columnIndexOfImageUrls: Int = getColumnIndexOrThrow(_stmt, "imageUrls")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfCondition: Int = getColumnIndexOrThrow(_stmt, "condition")
        val _columnIndexOfHarvestDate: Int = getColumnIndexOrThrow(_stmt, "harvestDate")
        val _columnIndexOfExpiryDate: Int = getColumnIndexOrThrow(_stmt, "expiryDate")
        val _columnIndexOfBirthDate: Int = getColumnIndexOrThrow(_stmt, "birthDate")
        val _columnIndexOfVaccinationRecordsJson: Int = getColumnIndexOrThrow(_stmt,
            "vaccinationRecordsJson")
        val _columnIndexOfWeightGrams: Int = getColumnIndexOrThrow(_stmt, "weightGrams")
        val _columnIndexOfHeightCm: Int = getColumnIndexOrThrow(_stmt, "heightCm")
        val _columnIndexOfGender: Int = getColumnIndexOrThrow(_stmt, "gender")
        val _columnIndexOfColor: Int = getColumnIndexOrThrow(_stmt, "color")
        val _columnIndexOfBreed: Int = getColumnIndexOrThrow(_stmt, "breed")
        val _columnIndexOfRaisingPurpose: Int = getColumnIndexOrThrow(_stmt, "raisingPurpose")
        val _columnIndexOfHealthStatus: Int = getColumnIndexOrThrow(_stmt, "healthStatus")
        val _columnIndexOfBirdCode: Int = getColumnIndexOrThrow(_stmt, "birdCode")
        val _columnIndexOfColorTag: Int = getColumnIndexOrThrow(_stmt, "colorTag")
        val _columnIndexOfFamilyTreeId: Int = getColumnIndexOrThrow(_stmt, "familyTreeId")
        val _columnIndexOfSourceAssetId: Int = getColumnIndexOrThrow(_stmt, "sourceAssetId")
        val _columnIndexOfParentIdsJson: Int = getColumnIndexOrThrow(_stmt, "parentIdsJson")
        val _columnIndexOfBreedingStatus: Int = getColumnIndexOrThrow(_stmt, "breedingStatus")
        val _columnIndexOfTransferHistoryJson: Int = getColumnIndexOrThrow(_stmt,
            "transferHistoryJson")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfLastModifiedAt: Int = getColumnIndexOrThrow(_stmt, "lastModifiedAt")
        val _columnIndexOfIsDeleted: Int = getColumnIndexOrThrow(_stmt, "isDeleted")
        val _columnIndexOfDeletedAt: Int = getColumnIndexOrThrow(_stmt, "deletedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfStage: Int = getColumnIndexOrThrow(_stmt, "stage")
        val _columnIndexOfLifecycleStatus: Int = getColumnIndexOrThrow(_stmt, "lifecycleStatus")
        val _columnIndexOfParentMaleId: Int = getColumnIndexOrThrow(_stmt, "parentMaleId")
        val _columnIndexOfParentFemaleId: Int = getColumnIndexOrThrow(_stmt, "parentFemaleId")
        val _columnIndexOfAgeWeeks: Int = getColumnIndexOrThrow(_stmt, "ageWeeks")
        val _columnIndexOfLastStageTransitionAt: Int = getColumnIndexOrThrow(_stmt,
            "lastStageTransitionAt")
        val _columnIndexOfBreederEligibleAt: Int = getColumnIndexOrThrow(_stmt, "breederEligibleAt")
        val _columnIndexOfIsBatch: Int = getColumnIndexOrThrow(_stmt, "isBatch")
        val _columnIndexOfBatchId: Int = getColumnIndexOrThrow(_stmt, "batchId")
        val _columnIndexOfSplitAt: Int = getColumnIndexOrThrow(_stmt, "splitAt")
        val _columnIndexOfSplitIntoIds: Int = getColumnIndexOrThrow(_stmt, "splitIntoIds")
        val _columnIndexOfDocumentUrls: Int = getColumnIndexOrThrow(_stmt, "documentUrls")
        val _columnIndexOfQrCodeUrl: Int = getColumnIndexOrThrow(_stmt, "qrCodeUrl")
        val _columnIndexOfCustomStatus: Int = getColumnIndexOrThrow(_stmt, "customStatus")
        val _columnIndexOfDebug: Int = getColumnIndexOrThrow(_stmt, "debug")
        val _columnIndexOfDeliveryOptions: Int = getColumnIndexOrThrow(_stmt, "deliveryOptions")
        val _columnIndexOfDeliveryCost: Int = getColumnIndexOrThrow(_stmt, "deliveryCost")
        val _columnIndexOfLeadTimeDays: Int = getColumnIndexOrThrow(_stmt, "leadTimeDays")
        val _columnIndexOfMotherId: Int = getColumnIndexOrThrow(_stmt, "motherId")
        val _columnIndexOfIsBreedingUnit: Int = getColumnIndexOrThrow(_stmt, "isBreedingUnit")
        val _columnIndexOfEggsCollectedToday: Int = getColumnIndexOrThrow(_stmt,
            "eggsCollectedToday")
        val _columnIndexOfLastEggLogDate: Int = getColumnIndexOrThrow(_stmt, "lastEggLogDate")
        val _columnIndexOfReadyForSale: Int = getColumnIndexOrThrow(_stmt, "readyForSale")
        val _columnIndexOfTargetWeight: Int = getColumnIndexOrThrow(_stmt, "targetWeight")
        val _columnIndexOfIsShowcased: Int = getColumnIndexOrThrow(_stmt, "isShowcased")
        val _columnIndexOfExternalVideoUrl: Int = getColumnIndexOrThrow(_stmt, "externalVideoUrl")
        val _columnIndexOfRecordsLockedAt: Int = getColumnIndexOrThrow(_stmt, "recordsLockedAt")
        val _columnIndexOfAutoLockAfterDays: Int = getColumnIndexOrThrow(_stmt, "autoLockAfterDays")
        val _columnIndexOfLineageHistoryJson: Int = getColumnIndexOrThrow(_stmt,
            "lineageHistoryJson")
        val _columnIndexOfEditCount: Int = getColumnIndexOrThrow(_stmt, "editCount")
        val _columnIndexOfLastEditedBy: Int = getColumnIndexOrThrow(_stmt, "lastEditedBy")
        val _columnIndexOfAdminFlagged: Int = getColumnIndexOrThrow(_stmt, "adminFlagged")
        val _columnIndexOfModerationNote: Int = getColumnIndexOrThrow(_stmt, "moderationNote")
        val _columnIndexOfMetadataJson: Int = getColumnIndexOrThrow(_stmt, "metadataJson")
        val _result: ProductEntity?
        if (_stmt.step()) {
          val _tmpProductId: String
          _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          val _tmpSellerId: String
          _tmpSellerId = _stmt.getText(_columnIndexOfSellerId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpDescription: String
          _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          val _tmpCategory: String
          _tmpCategory = _stmt.getText(_columnIndexOfCategory)
          val _tmpPrice: Double
          _tmpPrice = _stmt.getDouble(_columnIndexOfPrice)
          val _tmpQuantity: Double
          _tmpQuantity = _stmt.getDouble(_columnIndexOfQuantity)
          val _tmpUnit: String
          _tmpUnit = _stmt.getText(_columnIndexOfUnit)
          val _tmpLocation: String
          _tmpLocation = _stmt.getText(_columnIndexOfLocation)
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
          val _tmpImageUrls: List<String>
          val _tmp: String?
          if (_stmt.isNull(_columnIndexOfImageUrls)) {
            _tmp = null
          } else {
            _tmp = _stmt.getText(_columnIndexOfImageUrls)
          }
          val _tmp_1: List<String>? = AppDatabase.Converters.toStringList(_tmp)
          if (_tmp_1 == null) {
            error("Expected NON-NULL 'kotlin.collections.List<kotlin.String>', but it was NULL.")
          } else {
            _tmpImageUrls = _tmp_1
          }
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpCondition: String?
          if (_stmt.isNull(_columnIndexOfCondition)) {
            _tmpCondition = null
          } else {
            _tmpCondition = _stmt.getText(_columnIndexOfCondition)
          }
          val _tmpHarvestDate: Long?
          if (_stmt.isNull(_columnIndexOfHarvestDate)) {
            _tmpHarvestDate = null
          } else {
            _tmpHarvestDate = _stmt.getLong(_columnIndexOfHarvestDate)
          }
          val _tmpExpiryDate: Long?
          if (_stmt.isNull(_columnIndexOfExpiryDate)) {
            _tmpExpiryDate = null
          } else {
            _tmpExpiryDate = _stmt.getLong(_columnIndexOfExpiryDate)
          }
          val _tmpBirthDate: Long?
          if (_stmt.isNull(_columnIndexOfBirthDate)) {
            _tmpBirthDate = null
          } else {
            _tmpBirthDate = _stmt.getLong(_columnIndexOfBirthDate)
          }
          val _tmpVaccinationRecordsJson: String?
          if (_stmt.isNull(_columnIndexOfVaccinationRecordsJson)) {
            _tmpVaccinationRecordsJson = null
          } else {
            _tmpVaccinationRecordsJson = _stmt.getText(_columnIndexOfVaccinationRecordsJson)
          }
          val _tmpWeightGrams: Double?
          if (_stmt.isNull(_columnIndexOfWeightGrams)) {
            _tmpWeightGrams = null
          } else {
            _tmpWeightGrams = _stmt.getDouble(_columnIndexOfWeightGrams)
          }
          val _tmpHeightCm: Double?
          if (_stmt.isNull(_columnIndexOfHeightCm)) {
            _tmpHeightCm = null
          } else {
            _tmpHeightCm = _stmt.getDouble(_columnIndexOfHeightCm)
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
          val _tmpBreed: String?
          if (_stmt.isNull(_columnIndexOfBreed)) {
            _tmpBreed = null
          } else {
            _tmpBreed = _stmt.getText(_columnIndexOfBreed)
          }
          val _tmpRaisingPurpose: String?
          if (_stmt.isNull(_columnIndexOfRaisingPurpose)) {
            _tmpRaisingPurpose = null
          } else {
            _tmpRaisingPurpose = _stmt.getText(_columnIndexOfRaisingPurpose)
          }
          val _tmpHealthStatus: String?
          if (_stmt.isNull(_columnIndexOfHealthStatus)) {
            _tmpHealthStatus = null
          } else {
            _tmpHealthStatus = _stmt.getText(_columnIndexOfHealthStatus)
          }
          val _tmpBirdCode: String?
          if (_stmt.isNull(_columnIndexOfBirdCode)) {
            _tmpBirdCode = null
          } else {
            _tmpBirdCode = _stmt.getText(_columnIndexOfBirdCode)
          }
          val _tmpColorTag: String?
          if (_stmt.isNull(_columnIndexOfColorTag)) {
            _tmpColorTag = null
          } else {
            _tmpColorTag = _stmt.getText(_columnIndexOfColorTag)
          }
          val _tmpFamilyTreeId: String?
          if (_stmt.isNull(_columnIndexOfFamilyTreeId)) {
            _tmpFamilyTreeId = null
          } else {
            _tmpFamilyTreeId = _stmt.getText(_columnIndexOfFamilyTreeId)
          }
          val _tmpSourceAssetId: String?
          if (_stmt.isNull(_columnIndexOfSourceAssetId)) {
            _tmpSourceAssetId = null
          } else {
            _tmpSourceAssetId = _stmt.getText(_columnIndexOfSourceAssetId)
          }
          val _tmpParentIdsJson: String?
          if (_stmt.isNull(_columnIndexOfParentIdsJson)) {
            _tmpParentIdsJson = null
          } else {
            _tmpParentIdsJson = _stmt.getText(_columnIndexOfParentIdsJson)
          }
          val _tmpBreedingStatus: String?
          if (_stmt.isNull(_columnIndexOfBreedingStatus)) {
            _tmpBreedingStatus = null
          } else {
            _tmpBreedingStatus = _stmt.getText(_columnIndexOfBreedingStatus)
          }
          val _tmpTransferHistoryJson: String?
          if (_stmt.isNull(_columnIndexOfTransferHistoryJson)) {
            _tmpTransferHistoryJson = null
          } else {
            _tmpTransferHistoryJson = _stmt.getText(_columnIndexOfTransferHistoryJson)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpLastModifiedAt: Long
          _tmpLastModifiedAt = _stmt.getLong(_columnIndexOfLastModifiedAt)
          val _tmpIsDeleted: Boolean
          val _tmp_2: Int
          _tmp_2 = _stmt.getLong(_columnIndexOfIsDeleted).toInt()
          _tmpIsDeleted = _tmp_2 != 0
          val _tmpDeletedAt: Long?
          if (_stmt.isNull(_columnIndexOfDeletedAt)) {
            _tmpDeletedAt = null
          } else {
            _tmpDeletedAt = _stmt.getLong(_columnIndexOfDeletedAt)
          }
          val _tmpDirty: Boolean
          val _tmp_3: Int
          _tmp_3 = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp_3 != 0
          val _tmpStage: LifecycleStage?
          val _tmp_4: String?
          if (_stmt.isNull(_columnIndexOfStage)) {
            _tmp_4 = null
          } else {
            _tmp_4 = _stmt.getText(_columnIndexOfStage)
          }
          _tmpStage = AppDatabase.Converters.toLifecycleStage(_tmp_4)
          val _tmpLifecycleStatus: String?
          if (_stmt.isNull(_columnIndexOfLifecycleStatus)) {
            _tmpLifecycleStatus = null
          } else {
            _tmpLifecycleStatus = _stmt.getText(_columnIndexOfLifecycleStatus)
          }
          val _tmpParentMaleId: String?
          if (_stmt.isNull(_columnIndexOfParentMaleId)) {
            _tmpParentMaleId = null
          } else {
            _tmpParentMaleId = _stmt.getText(_columnIndexOfParentMaleId)
          }
          val _tmpParentFemaleId: String?
          if (_stmt.isNull(_columnIndexOfParentFemaleId)) {
            _tmpParentFemaleId = null
          } else {
            _tmpParentFemaleId = _stmt.getText(_columnIndexOfParentFemaleId)
          }
          val _tmpAgeWeeks: Int?
          if (_stmt.isNull(_columnIndexOfAgeWeeks)) {
            _tmpAgeWeeks = null
          } else {
            _tmpAgeWeeks = _stmt.getLong(_columnIndexOfAgeWeeks).toInt()
          }
          val _tmpLastStageTransitionAt: Long?
          if (_stmt.isNull(_columnIndexOfLastStageTransitionAt)) {
            _tmpLastStageTransitionAt = null
          } else {
            _tmpLastStageTransitionAt = _stmt.getLong(_columnIndexOfLastStageTransitionAt)
          }
          val _tmpBreederEligibleAt: Long?
          if (_stmt.isNull(_columnIndexOfBreederEligibleAt)) {
            _tmpBreederEligibleAt = null
          } else {
            _tmpBreederEligibleAt = _stmt.getLong(_columnIndexOfBreederEligibleAt)
          }
          val _tmpIsBatch: Boolean?
          val _tmp_5: Int?
          if (_stmt.isNull(_columnIndexOfIsBatch)) {
            _tmp_5 = null
          } else {
            _tmp_5 = _stmt.getLong(_columnIndexOfIsBatch).toInt()
          }
          _tmpIsBatch = _tmp_5?.let { it != 0 }
          val _tmpBatchId: String?
          if (_stmt.isNull(_columnIndexOfBatchId)) {
            _tmpBatchId = null
          } else {
            _tmpBatchId = _stmt.getText(_columnIndexOfBatchId)
          }
          val _tmpSplitAt: Long?
          if (_stmt.isNull(_columnIndexOfSplitAt)) {
            _tmpSplitAt = null
          } else {
            _tmpSplitAt = _stmt.getLong(_columnIndexOfSplitAt)
          }
          val _tmpSplitIntoIds: String?
          if (_stmt.isNull(_columnIndexOfSplitIntoIds)) {
            _tmpSplitIntoIds = null
          } else {
            _tmpSplitIntoIds = _stmt.getText(_columnIndexOfSplitIntoIds)
          }
          val _tmpDocumentUrls: List<String>
          val _tmp_6: String?
          if (_stmt.isNull(_columnIndexOfDocumentUrls)) {
            _tmp_6 = null
          } else {
            _tmp_6 = _stmt.getText(_columnIndexOfDocumentUrls)
          }
          val _tmp_7: List<String>? = AppDatabase.Converters.toStringList(_tmp_6)
          if (_tmp_7 == null) {
            error("Expected NON-NULL 'kotlin.collections.List<kotlin.String>', but it was NULL.")
          } else {
            _tmpDocumentUrls = _tmp_7
          }
          val _tmpQrCodeUrl: String?
          if (_stmt.isNull(_columnIndexOfQrCodeUrl)) {
            _tmpQrCodeUrl = null
          } else {
            _tmpQrCodeUrl = _stmt.getText(_columnIndexOfQrCodeUrl)
          }
          val _tmpCustomStatus: String?
          if (_stmt.isNull(_columnIndexOfCustomStatus)) {
            _tmpCustomStatus = null
          } else {
            _tmpCustomStatus = _stmt.getText(_columnIndexOfCustomStatus)
          }
          val _tmpDebug: Boolean
          val _tmp_8: Int
          _tmp_8 = _stmt.getLong(_columnIndexOfDebug).toInt()
          _tmpDebug = _tmp_8 != 0
          val _tmpDeliveryOptions: List<String>
          val _tmp_9: String?
          if (_stmt.isNull(_columnIndexOfDeliveryOptions)) {
            _tmp_9 = null
          } else {
            _tmp_9 = _stmt.getText(_columnIndexOfDeliveryOptions)
          }
          val _tmp_10: List<String>? = AppDatabase.Converters.toStringList(_tmp_9)
          if (_tmp_10 == null) {
            error("Expected NON-NULL 'kotlin.collections.List<kotlin.String>', but it was NULL.")
          } else {
            _tmpDeliveryOptions = _tmp_10
          }
          val _tmpDeliveryCost: Double?
          if (_stmt.isNull(_columnIndexOfDeliveryCost)) {
            _tmpDeliveryCost = null
          } else {
            _tmpDeliveryCost = _stmt.getDouble(_columnIndexOfDeliveryCost)
          }
          val _tmpLeadTimeDays: Int?
          if (_stmt.isNull(_columnIndexOfLeadTimeDays)) {
            _tmpLeadTimeDays = null
          } else {
            _tmpLeadTimeDays = _stmt.getLong(_columnIndexOfLeadTimeDays).toInt()
          }
          val _tmpMotherId: String?
          if (_stmt.isNull(_columnIndexOfMotherId)) {
            _tmpMotherId = null
          } else {
            _tmpMotherId = _stmt.getText(_columnIndexOfMotherId)
          }
          val _tmpIsBreedingUnit: Boolean
          val _tmp_11: Int
          _tmp_11 = _stmt.getLong(_columnIndexOfIsBreedingUnit).toInt()
          _tmpIsBreedingUnit = _tmp_11 != 0
          val _tmpEggsCollectedToday: Int
          _tmpEggsCollectedToday = _stmt.getLong(_columnIndexOfEggsCollectedToday).toInt()
          val _tmpLastEggLogDate: Long?
          if (_stmt.isNull(_columnIndexOfLastEggLogDate)) {
            _tmpLastEggLogDate = null
          } else {
            _tmpLastEggLogDate = _stmt.getLong(_columnIndexOfLastEggLogDate)
          }
          val _tmpReadyForSale: Boolean
          val _tmp_12: Int
          _tmp_12 = _stmt.getLong(_columnIndexOfReadyForSale).toInt()
          _tmpReadyForSale = _tmp_12 != 0
          val _tmpTargetWeight: Double?
          if (_stmt.isNull(_columnIndexOfTargetWeight)) {
            _tmpTargetWeight = null
          } else {
            _tmpTargetWeight = _stmt.getDouble(_columnIndexOfTargetWeight)
          }
          val _tmpIsShowcased: Boolean
          val _tmp_13: Int
          _tmp_13 = _stmt.getLong(_columnIndexOfIsShowcased).toInt()
          _tmpIsShowcased = _tmp_13 != 0
          val _tmpExternalVideoUrl: String?
          if (_stmt.isNull(_columnIndexOfExternalVideoUrl)) {
            _tmpExternalVideoUrl = null
          } else {
            _tmpExternalVideoUrl = _stmt.getText(_columnIndexOfExternalVideoUrl)
          }
          val _tmpRecordsLockedAt: Long?
          if (_stmt.isNull(_columnIndexOfRecordsLockedAt)) {
            _tmpRecordsLockedAt = null
          } else {
            _tmpRecordsLockedAt = _stmt.getLong(_columnIndexOfRecordsLockedAt)
          }
          val _tmpAutoLockAfterDays: Int
          _tmpAutoLockAfterDays = _stmt.getLong(_columnIndexOfAutoLockAfterDays).toInt()
          val _tmpLineageHistoryJson: String?
          if (_stmt.isNull(_columnIndexOfLineageHistoryJson)) {
            _tmpLineageHistoryJson = null
          } else {
            _tmpLineageHistoryJson = _stmt.getText(_columnIndexOfLineageHistoryJson)
          }
          val _tmpEditCount: Int
          _tmpEditCount = _stmt.getLong(_columnIndexOfEditCount).toInt()
          val _tmpLastEditedBy: String?
          if (_stmt.isNull(_columnIndexOfLastEditedBy)) {
            _tmpLastEditedBy = null
          } else {
            _tmpLastEditedBy = _stmt.getText(_columnIndexOfLastEditedBy)
          }
          val _tmpAdminFlagged: Boolean
          val _tmp_14: Int
          _tmp_14 = _stmt.getLong(_columnIndexOfAdminFlagged).toInt()
          _tmpAdminFlagged = _tmp_14 != 0
          val _tmpModerationNote: String?
          if (_stmt.isNull(_columnIndexOfModerationNote)) {
            _tmpModerationNote = null
          } else {
            _tmpModerationNote = _stmt.getText(_columnIndexOfModerationNote)
          }
          val _tmpMetadataJson: String
          _tmpMetadataJson = _stmt.getText(_columnIndexOfMetadataJson)
          _result =
              ProductEntity(_tmpProductId,_tmpSellerId,_tmpName,_tmpDescription,_tmpCategory,_tmpPrice,_tmpQuantity,_tmpUnit,_tmpLocation,_tmpLatitude,_tmpLongitude,_tmpImageUrls,_tmpStatus,_tmpCondition,_tmpHarvestDate,_tmpExpiryDate,_tmpBirthDate,_tmpVaccinationRecordsJson,_tmpWeightGrams,_tmpHeightCm,_tmpGender,_tmpColor,_tmpBreed,_tmpRaisingPurpose,_tmpHealthStatus,_tmpBirdCode,_tmpColorTag,_tmpFamilyTreeId,_tmpSourceAssetId,_tmpParentIdsJson,_tmpBreedingStatus,_tmpTransferHistoryJson,_tmpCreatedAt,_tmpUpdatedAt,_tmpLastModifiedAt,_tmpIsDeleted,_tmpDeletedAt,_tmpDirty,_tmpStage,_tmpLifecycleStatus,_tmpParentMaleId,_tmpParentFemaleId,_tmpAgeWeeks,_tmpLastStageTransitionAt,_tmpBreederEligibleAt,_tmpIsBatch,_tmpBatchId,_tmpSplitAt,_tmpSplitIntoIds,_tmpDocumentUrls,_tmpQrCodeUrl,_tmpCustomStatus,_tmpDebug,_tmpDeliveryOptions,_tmpDeliveryCost,_tmpLeadTimeDays,_tmpMotherId,_tmpIsBreedingUnit,_tmpEggsCollectedToday,_tmpLastEggLogDate,_tmpReadyForSale,_tmpTargetWeight,_tmpIsShowcased,_tmpExternalVideoUrl,_tmpRecordsLockedAt,_tmpAutoLockAfterDays,_tmpLineageHistoryJson,_tmpEditCount,_tmpLastEditedBy,_tmpAdminFlagged,_tmpModerationNote,_tmpMetadataJson)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getListingForAssetSync(assetId: String): ProductEntity? {
    val _sql: String = "SELECT * FROM products WHERE sourceAssetId = ? AND isDeleted = 0 LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, assetId)
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfSellerId: Int = getColumnIndexOrThrow(_stmt, "sellerId")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfCategory: Int = getColumnIndexOrThrow(_stmt, "category")
        val _columnIndexOfPrice: Int = getColumnIndexOrThrow(_stmt, "price")
        val _columnIndexOfQuantity: Int = getColumnIndexOrThrow(_stmt, "quantity")
        val _columnIndexOfUnit: Int = getColumnIndexOrThrow(_stmt, "unit")
        val _columnIndexOfLocation: Int = getColumnIndexOrThrow(_stmt, "location")
        val _columnIndexOfLatitude: Int = getColumnIndexOrThrow(_stmt, "latitude")
        val _columnIndexOfLongitude: Int = getColumnIndexOrThrow(_stmt, "longitude")
        val _columnIndexOfImageUrls: Int = getColumnIndexOrThrow(_stmt, "imageUrls")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfCondition: Int = getColumnIndexOrThrow(_stmt, "condition")
        val _columnIndexOfHarvestDate: Int = getColumnIndexOrThrow(_stmt, "harvestDate")
        val _columnIndexOfExpiryDate: Int = getColumnIndexOrThrow(_stmt, "expiryDate")
        val _columnIndexOfBirthDate: Int = getColumnIndexOrThrow(_stmt, "birthDate")
        val _columnIndexOfVaccinationRecordsJson: Int = getColumnIndexOrThrow(_stmt,
            "vaccinationRecordsJson")
        val _columnIndexOfWeightGrams: Int = getColumnIndexOrThrow(_stmt, "weightGrams")
        val _columnIndexOfHeightCm: Int = getColumnIndexOrThrow(_stmt, "heightCm")
        val _columnIndexOfGender: Int = getColumnIndexOrThrow(_stmt, "gender")
        val _columnIndexOfColor: Int = getColumnIndexOrThrow(_stmt, "color")
        val _columnIndexOfBreed: Int = getColumnIndexOrThrow(_stmt, "breed")
        val _columnIndexOfRaisingPurpose: Int = getColumnIndexOrThrow(_stmt, "raisingPurpose")
        val _columnIndexOfHealthStatus: Int = getColumnIndexOrThrow(_stmt, "healthStatus")
        val _columnIndexOfBirdCode: Int = getColumnIndexOrThrow(_stmt, "birdCode")
        val _columnIndexOfColorTag: Int = getColumnIndexOrThrow(_stmt, "colorTag")
        val _columnIndexOfFamilyTreeId: Int = getColumnIndexOrThrow(_stmt, "familyTreeId")
        val _columnIndexOfSourceAssetId: Int = getColumnIndexOrThrow(_stmt, "sourceAssetId")
        val _columnIndexOfParentIdsJson: Int = getColumnIndexOrThrow(_stmt, "parentIdsJson")
        val _columnIndexOfBreedingStatus: Int = getColumnIndexOrThrow(_stmt, "breedingStatus")
        val _columnIndexOfTransferHistoryJson: Int = getColumnIndexOrThrow(_stmt,
            "transferHistoryJson")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfLastModifiedAt: Int = getColumnIndexOrThrow(_stmt, "lastModifiedAt")
        val _columnIndexOfIsDeleted: Int = getColumnIndexOrThrow(_stmt, "isDeleted")
        val _columnIndexOfDeletedAt: Int = getColumnIndexOrThrow(_stmt, "deletedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfStage: Int = getColumnIndexOrThrow(_stmt, "stage")
        val _columnIndexOfLifecycleStatus: Int = getColumnIndexOrThrow(_stmt, "lifecycleStatus")
        val _columnIndexOfParentMaleId: Int = getColumnIndexOrThrow(_stmt, "parentMaleId")
        val _columnIndexOfParentFemaleId: Int = getColumnIndexOrThrow(_stmt, "parentFemaleId")
        val _columnIndexOfAgeWeeks: Int = getColumnIndexOrThrow(_stmt, "ageWeeks")
        val _columnIndexOfLastStageTransitionAt: Int = getColumnIndexOrThrow(_stmt,
            "lastStageTransitionAt")
        val _columnIndexOfBreederEligibleAt: Int = getColumnIndexOrThrow(_stmt, "breederEligibleAt")
        val _columnIndexOfIsBatch: Int = getColumnIndexOrThrow(_stmt, "isBatch")
        val _columnIndexOfBatchId: Int = getColumnIndexOrThrow(_stmt, "batchId")
        val _columnIndexOfSplitAt: Int = getColumnIndexOrThrow(_stmt, "splitAt")
        val _columnIndexOfSplitIntoIds: Int = getColumnIndexOrThrow(_stmt, "splitIntoIds")
        val _columnIndexOfDocumentUrls: Int = getColumnIndexOrThrow(_stmt, "documentUrls")
        val _columnIndexOfQrCodeUrl: Int = getColumnIndexOrThrow(_stmt, "qrCodeUrl")
        val _columnIndexOfCustomStatus: Int = getColumnIndexOrThrow(_stmt, "customStatus")
        val _columnIndexOfDebug: Int = getColumnIndexOrThrow(_stmt, "debug")
        val _columnIndexOfDeliveryOptions: Int = getColumnIndexOrThrow(_stmt, "deliveryOptions")
        val _columnIndexOfDeliveryCost: Int = getColumnIndexOrThrow(_stmt, "deliveryCost")
        val _columnIndexOfLeadTimeDays: Int = getColumnIndexOrThrow(_stmt, "leadTimeDays")
        val _columnIndexOfMotherId: Int = getColumnIndexOrThrow(_stmt, "motherId")
        val _columnIndexOfIsBreedingUnit: Int = getColumnIndexOrThrow(_stmt, "isBreedingUnit")
        val _columnIndexOfEggsCollectedToday: Int = getColumnIndexOrThrow(_stmt,
            "eggsCollectedToday")
        val _columnIndexOfLastEggLogDate: Int = getColumnIndexOrThrow(_stmt, "lastEggLogDate")
        val _columnIndexOfReadyForSale: Int = getColumnIndexOrThrow(_stmt, "readyForSale")
        val _columnIndexOfTargetWeight: Int = getColumnIndexOrThrow(_stmt, "targetWeight")
        val _columnIndexOfIsShowcased: Int = getColumnIndexOrThrow(_stmt, "isShowcased")
        val _columnIndexOfExternalVideoUrl: Int = getColumnIndexOrThrow(_stmt, "externalVideoUrl")
        val _columnIndexOfRecordsLockedAt: Int = getColumnIndexOrThrow(_stmt, "recordsLockedAt")
        val _columnIndexOfAutoLockAfterDays: Int = getColumnIndexOrThrow(_stmt, "autoLockAfterDays")
        val _columnIndexOfLineageHistoryJson: Int = getColumnIndexOrThrow(_stmt,
            "lineageHistoryJson")
        val _columnIndexOfEditCount: Int = getColumnIndexOrThrow(_stmt, "editCount")
        val _columnIndexOfLastEditedBy: Int = getColumnIndexOrThrow(_stmt, "lastEditedBy")
        val _columnIndexOfAdminFlagged: Int = getColumnIndexOrThrow(_stmt, "adminFlagged")
        val _columnIndexOfModerationNote: Int = getColumnIndexOrThrow(_stmt, "moderationNote")
        val _columnIndexOfMetadataJson: Int = getColumnIndexOrThrow(_stmt, "metadataJson")
        val _result: ProductEntity?
        if (_stmt.step()) {
          val _tmpProductId: String
          _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          val _tmpSellerId: String
          _tmpSellerId = _stmt.getText(_columnIndexOfSellerId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpDescription: String
          _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          val _tmpCategory: String
          _tmpCategory = _stmt.getText(_columnIndexOfCategory)
          val _tmpPrice: Double
          _tmpPrice = _stmt.getDouble(_columnIndexOfPrice)
          val _tmpQuantity: Double
          _tmpQuantity = _stmt.getDouble(_columnIndexOfQuantity)
          val _tmpUnit: String
          _tmpUnit = _stmt.getText(_columnIndexOfUnit)
          val _tmpLocation: String
          _tmpLocation = _stmt.getText(_columnIndexOfLocation)
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
          val _tmpImageUrls: List<String>
          val _tmp: String?
          if (_stmt.isNull(_columnIndexOfImageUrls)) {
            _tmp = null
          } else {
            _tmp = _stmt.getText(_columnIndexOfImageUrls)
          }
          val _tmp_1: List<String>? = AppDatabase.Converters.toStringList(_tmp)
          if (_tmp_1 == null) {
            error("Expected NON-NULL 'kotlin.collections.List<kotlin.String>', but it was NULL.")
          } else {
            _tmpImageUrls = _tmp_1
          }
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpCondition: String?
          if (_stmt.isNull(_columnIndexOfCondition)) {
            _tmpCondition = null
          } else {
            _tmpCondition = _stmt.getText(_columnIndexOfCondition)
          }
          val _tmpHarvestDate: Long?
          if (_stmt.isNull(_columnIndexOfHarvestDate)) {
            _tmpHarvestDate = null
          } else {
            _tmpHarvestDate = _stmt.getLong(_columnIndexOfHarvestDate)
          }
          val _tmpExpiryDate: Long?
          if (_stmt.isNull(_columnIndexOfExpiryDate)) {
            _tmpExpiryDate = null
          } else {
            _tmpExpiryDate = _stmt.getLong(_columnIndexOfExpiryDate)
          }
          val _tmpBirthDate: Long?
          if (_stmt.isNull(_columnIndexOfBirthDate)) {
            _tmpBirthDate = null
          } else {
            _tmpBirthDate = _stmt.getLong(_columnIndexOfBirthDate)
          }
          val _tmpVaccinationRecordsJson: String?
          if (_stmt.isNull(_columnIndexOfVaccinationRecordsJson)) {
            _tmpVaccinationRecordsJson = null
          } else {
            _tmpVaccinationRecordsJson = _stmt.getText(_columnIndexOfVaccinationRecordsJson)
          }
          val _tmpWeightGrams: Double?
          if (_stmt.isNull(_columnIndexOfWeightGrams)) {
            _tmpWeightGrams = null
          } else {
            _tmpWeightGrams = _stmt.getDouble(_columnIndexOfWeightGrams)
          }
          val _tmpHeightCm: Double?
          if (_stmt.isNull(_columnIndexOfHeightCm)) {
            _tmpHeightCm = null
          } else {
            _tmpHeightCm = _stmt.getDouble(_columnIndexOfHeightCm)
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
          val _tmpBreed: String?
          if (_stmt.isNull(_columnIndexOfBreed)) {
            _tmpBreed = null
          } else {
            _tmpBreed = _stmt.getText(_columnIndexOfBreed)
          }
          val _tmpRaisingPurpose: String?
          if (_stmt.isNull(_columnIndexOfRaisingPurpose)) {
            _tmpRaisingPurpose = null
          } else {
            _tmpRaisingPurpose = _stmt.getText(_columnIndexOfRaisingPurpose)
          }
          val _tmpHealthStatus: String?
          if (_stmt.isNull(_columnIndexOfHealthStatus)) {
            _tmpHealthStatus = null
          } else {
            _tmpHealthStatus = _stmt.getText(_columnIndexOfHealthStatus)
          }
          val _tmpBirdCode: String?
          if (_stmt.isNull(_columnIndexOfBirdCode)) {
            _tmpBirdCode = null
          } else {
            _tmpBirdCode = _stmt.getText(_columnIndexOfBirdCode)
          }
          val _tmpColorTag: String?
          if (_stmt.isNull(_columnIndexOfColorTag)) {
            _tmpColorTag = null
          } else {
            _tmpColorTag = _stmt.getText(_columnIndexOfColorTag)
          }
          val _tmpFamilyTreeId: String?
          if (_stmt.isNull(_columnIndexOfFamilyTreeId)) {
            _tmpFamilyTreeId = null
          } else {
            _tmpFamilyTreeId = _stmt.getText(_columnIndexOfFamilyTreeId)
          }
          val _tmpSourceAssetId: String?
          if (_stmt.isNull(_columnIndexOfSourceAssetId)) {
            _tmpSourceAssetId = null
          } else {
            _tmpSourceAssetId = _stmt.getText(_columnIndexOfSourceAssetId)
          }
          val _tmpParentIdsJson: String?
          if (_stmt.isNull(_columnIndexOfParentIdsJson)) {
            _tmpParentIdsJson = null
          } else {
            _tmpParentIdsJson = _stmt.getText(_columnIndexOfParentIdsJson)
          }
          val _tmpBreedingStatus: String?
          if (_stmt.isNull(_columnIndexOfBreedingStatus)) {
            _tmpBreedingStatus = null
          } else {
            _tmpBreedingStatus = _stmt.getText(_columnIndexOfBreedingStatus)
          }
          val _tmpTransferHistoryJson: String?
          if (_stmt.isNull(_columnIndexOfTransferHistoryJson)) {
            _tmpTransferHistoryJson = null
          } else {
            _tmpTransferHistoryJson = _stmt.getText(_columnIndexOfTransferHistoryJson)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpLastModifiedAt: Long
          _tmpLastModifiedAt = _stmt.getLong(_columnIndexOfLastModifiedAt)
          val _tmpIsDeleted: Boolean
          val _tmp_2: Int
          _tmp_2 = _stmt.getLong(_columnIndexOfIsDeleted).toInt()
          _tmpIsDeleted = _tmp_2 != 0
          val _tmpDeletedAt: Long?
          if (_stmt.isNull(_columnIndexOfDeletedAt)) {
            _tmpDeletedAt = null
          } else {
            _tmpDeletedAt = _stmt.getLong(_columnIndexOfDeletedAt)
          }
          val _tmpDirty: Boolean
          val _tmp_3: Int
          _tmp_3 = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp_3 != 0
          val _tmpStage: LifecycleStage?
          val _tmp_4: String?
          if (_stmt.isNull(_columnIndexOfStage)) {
            _tmp_4 = null
          } else {
            _tmp_4 = _stmt.getText(_columnIndexOfStage)
          }
          _tmpStage = AppDatabase.Converters.toLifecycleStage(_tmp_4)
          val _tmpLifecycleStatus: String?
          if (_stmt.isNull(_columnIndexOfLifecycleStatus)) {
            _tmpLifecycleStatus = null
          } else {
            _tmpLifecycleStatus = _stmt.getText(_columnIndexOfLifecycleStatus)
          }
          val _tmpParentMaleId: String?
          if (_stmt.isNull(_columnIndexOfParentMaleId)) {
            _tmpParentMaleId = null
          } else {
            _tmpParentMaleId = _stmt.getText(_columnIndexOfParentMaleId)
          }
          val _tmpParentFemaleId: String?
          if (_stmt.isNull(_columnIndexOfParentFemaleId)) {
            _tmpParentFemaleId = null
          } else {
            _tmpParentFemaleId = _stmt.getText(_columnIndexOfParentFemaleId)
          }
          val _tmpAgeWeeks: Int?
          if (_stmt.isNull(_columnIndexOfAgeWeeks)) {
            _tmpAgeWeeks = null
          } else {
            _tmpAgeWeeks = _stmt.getLong(_columnIndexOfAgeWeeks).toInt()
          }
          val _tmpLastStageTransitionAt: Long?
          if (_stmt.isNull(_columnIndexOfLastStageTransitionAt)) {
            _tmpLastStageTransitionAt = null
          } else {
            _tmpLastStageTransitionAt = _stmt.getLong(_columnIndexOfLastStageTransitionAt)
          }
          val _tmpBreederEligibleAt: Long?
          if (_stmt.isNull(_columnIndexOfBreederEligibleAt)) {
            _tmpBreederEligibleAt = null
          } else {
            _tmpBreederEligibleAt = _stmt.getLong(_columnIndexOfBreederEligibleAt)
          }
          val _tmpIsBatch: Boolean?
          val _tmp_5: Int?
          if (_stmt.isNull(_columnIndexOfIsBatch)) {
            _tmp_5 = null
          } else {
            _tmp_5 = _stmt.getLong(_columnIndexOfIsBatch).toInt()
          }
          _tmpIsBatch = _tmp_5?.let { it != 0 }
          val _tmpBatchId: String?
          if (_stmt.isNull(_columnIndexOfBatchId)) {
            _tmpBatchId = null
          } else {
            _tmpBatchId = _stmt.getText(_columnIndexOfBatchId)
          }
          val _tmpSplitAt: Long?
          if (_stmt.isNull(_columnIndexOfSplitAt)) {
            _tmpSplitAt = null
          } else {
            _tmpSplitAt = _stmt.getLong(_columnIndexOfSplitAt)
          }
          val _tmpSplitIntoIds: String?
          if (_stmt.isNull(_columnIndexOfSplitIntoIds)) {
            _tmpSplitIntoIds = null
          } else {
            _tmpSplitIntoIds = _stmt.getText(_columnIndexOfSplitIntoIds)
          }
          val _tmpDocumentUrls: List<String>
          val _tmp_6: String?
          if (_stmt.isNull(_columnIndexOfDocumentUrls)) {
            _tmp_6 = null
          } else {
            _tmp_6 = _stmt.getText(_columnIndexOfDocumentUrls)
          }
          val _tmp_7: List<String>? = AppDatabase.Converters.toStringList(_tmp_6)
          if (_tmp_7 == null) {
            error("Expected NON-NULL 'kotlin.collections.List<kotlin.String>', but it was NULL.")
          } else {
            _tmpDocumentUrls = _tmp_7
          }
          val _tmpQrCodeUrl: String?
          if (_stmt.isNull(_columnIndexOfQrCodeUrl)) {
            _tmpQrCodeUrl = null
          } else {
            _tmpQrCodeUrl = _stmt.getText(_columnIndexOfQrCodeUrl)
          }
          val _tmpCustomStatus: String?
          if (_stmt.isNull(_columnIndexOfCustomStatus)) {
            _tmpCustomStatus = null
          } else {
            _tmpCustomStatus = _stmt.getText(_columnIndexOfCustomStatus)
          }
          val _tmpDebug: Boolean
          val _tmp_8: Int
          _tmp_8 = _stmt.getLong(_columnIndexOfDebug).toInt()
          _tmpDebug = _tmp_8 != 0
          val _tmpDeliveryOptions: List<String>
          val _tmp_9: String?
          if (_stmt.isNull(_columnIndexOfDeliveryOptions)) {
            _tmp_9 = null
          } else {
            _tmp_9 = _stmt.getText(_columnIndexOfDeliveryOptions)
          }
          val _tmp_10: List<String>? = AppDatabase.Converters.toStringList(_tmp_9)
          if (_tmp_10 == null) {
            error("Expected NON-NULL 'kotlin.collections.List<kotlin.String>', but it was NULL.")
          } else {
            _tmpDeliveryOptions = _tmp_10
          }
          val _tmpDeliveryCost: Double?
          if (_stmt.isNull(_columnIndexOfDeliveryCost)) {
            _tmpDeliveryCost = null
          } else {
            _tmpDeliveryCost = _stmt.getDouble(_columnIndexOfDeliveryCost)
          }
          val _tmpLeadTimeDays: Int?
          if (_stmt.isNull(_columnIndexOfLeadTimeDays)) {
            _tmpLeadTimeDays = null
          } else {
            _tmpLeadTimeDays = _stmt.getLong(_columnIndexOfLeadTimeDays).toInt()
          }
          val _tmpMotherId: String?
          if (_stmt.isNull(_columnIndexOfMotherId)) {
            _tmpMotherId = null
          } else {
            _tmpMotherId = _stmt.getText(_columnIndexOfMotherId)
          }
          val _tmpIsBreedingUnit: Boolean
          val _tmp_11: Int
          _tmp_11 = _stmt.getLong(_columnIndexOfIsBreedingUnit).toInt()
          _tmpIsBreedingUnit = _tmp_11 != 0
          val _tmpEggsCollectedToday: Int
          _tmpEggsCollectedToday = _stmt.getLong(_columnIndexOfEggsCollectedToday).toInt()
          val _tmpLastEggLogDate: Long?
          if (_stmt.isNull(_columnIndexOfLastEggLogDate)) {
            _tmpLastEggLogDate = null
          } else {
            _tmpLastEggLogDate = _stmt.getLong(_columnIndexOfLastEggLogDate)
          }
          val _tmpReadyForSale: Boolean
          val _tmp_12: Int
          _tmp_12 = _stmt.getLong(_columnIndexOfReadyForSale).toInt()
          _tmpReadyForSale = _tmp_12 != 0
          val _tmpTargetWeight: Double?
          if (_stmt.isNull(_columnIndexOfTargetWeight)) {
            _tmpTargetWeight = null
          } else {
            _tmpTargetWeight = _stmt.getDouble(_columnIndexOfTargetWeight)
          }
          val _tmpIsShowcased: Boolean
          val _tmp_13: Int
          _tmp_13 = _stmt.getLong(_columnIndexOfIsShowcased).toInt()
          _tmpIsShowcased = _tmp_13 != 0
          val _tmpExternalVideoUrl: String?
          if (_stmt.isNull(_columnIndexOfExternalVideoUrl)) {
            _tmpExternalVideoUrl = null
          } else {
            _tmpExternalVideoUrl = _stmt.getText(_columnIndexOfExternalVideoUrl)
          }
          val _tmpRecordsLockedAt: Long?
          if (_stmt.isNull(_columnIndexOfRecordsLockedAt)) {
            _tmpRecordsLockedAt = null
          } else {
            _tmpRecordsLockedAt = _stmt.getLong(_columnIndexOfRecordsLockedAt)
          }
          val _tmpAutoLockAfterDays: Int
          _tmpAutoLockAfterDays = _stmt.getLong(_columnIndexOfAutoLockAfterDays).toInt()
          val _tmpLineageHistoryJson: String?
          if (_stmt.isNull(_columnIndexOfLineageHistoryJson)) {
            _tmpLineageHistoryJson = null
          } else {
            _tmpLineageHistoryJson = _stmt.getText(_columnIndexOfLineageHistoryJson)
          }
          val _tmpEditCount: Int
          _tmpEditCount = _stmt.getLong(_columnIndexOfEditCount).toInt()
          val _tmpLastEditedBy: String?
          if (_stmt.isNull(_columnIndexOfLastEditedBy)) {
            _tmpLastEditedBy = null
          } else {
            _tmpLastEditedBy = _stmt.getText(_columnIndexOfLastEditedBy)
          }
          val _tmpAdminFlagged: Boolean
          val _tmp_14: Int
          _tmp_14 = _stmt.getLong(_columnIndexOfAdminFlagged).toInt()
          _tmpAdminFlagged = _tmp_14 != 0
          val _tmpModerationNote: String?
          if (_stmt.isNull(_columnIndexOfModerationNote)) {
            _tmpModerationNote = null
          } else {
            _tmpModerationNote = _stmt.getText(_columnIndexOfModerationNote)
          }
          val _tmpMetadataJson: String
          _tmpMetadataJson = _stmt.getText(_columnIndexOfMetadataJson)
          _result =
              ProductEntity(_tmpProductId,_tmpSellerId,_tmpName,_tmpDescription,_tmpCategory,_tmpPrice,_tmpQuantity,_tmpUnit,_tmpLocation,_tmpLatitude,_tmpLongitude,_tmpImageUrls,_tmpStatus,_tmpCondition,_tmpHarvestDate,_tmpExpiryDate,_tmpBirthDate,_tmpVaccinationRecordsJson,_tmpWeightGrams,_tmpHeightCm,_tmpGender,_tmpColor,_tmpBreed,_tmpRaisingPurpose,_tmpHealthStatus,_tmpBirdCode,_tmpColorTag,_tmpFamilyTreeId,_tmpSourceAssetId,_tmpParentIdsJson,_tmpBreedingStatus,_tmpTransferHistoryJson,_tmpCreatedAt,_tmpUpdatedAt,_tmpLastModifiedAt,_tmpIsDeleted,_tmpDeletedAt,_tmpDirty,_tmpStage,_tmpLifecycleStatus,_tmpParentMaleId,_tmpParentFemaleId,_tmpAgeWeeks,_tmpLastStageTransitionAt,_tmpBreederEligibleAt,_tmpIsBatch,_tmpBatchId,_tmpSplitAt,_tmpSplitIntoIds,_tmpDocumentUrls,_tmpQrCodeUrl,_tmpCustomStatus,_tmpDebug,_tmpDeliveryOptions,_tmpDeliveryCost,_tmpLeadTimeDays,_tmpMotherId,_tmpIsBreedingUnit,_tmpEggsCollectedToday,_tmpLastEggLogDate,_tmpReadyForSale,_tmpTargetWeight,_tmpIsShowcased,_tmpExternalVideoUrl,_tmpRecordsLockedAt,_tmpAutoLockAfterDays,_tmpLineageHistoryJson,_tmpEditCount,_tmpLastEditedBy,_tmpAdminFlagged,_tmpModerationNote,_tmpMetadataJson)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun hasListingForAsset(assetId: String): Boolean {
    val _sql: String = "SELECT COUNT(*) > 0 FROM products WHERE sourceAssetId = ? AND isDeleted = 0"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, assetId)
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

  public override suspend fun getOffspring(parentId: String): List<ProductEntity> {
    val _sql: String =
        "SELECT * FROM products WHERE (parentMaleId = ? OR parentFemaleId = ?) AND isDeleted = 0"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, parentId)
        _argIndex = 2
        _stmt.bindText(_argIndex, parentId)
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfSellerId: Int = getColumnIndexOrThrow(_stmt, "sellerId")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfCategory: Int = getColumnIndexOrThrow(_stmt, "category")
        val _columnIndexOfPrice: Int = getColumnIndexOrThrow(_stmt, "price")
        val _columnIndexOfQuantity: Int = getColumnIndexOrThrow(_stmt, "quantity")
        val _columnIndexOfUnit: Int = getColumnIndexOrThrow(_stmt, "unit")
        val _columnIndexOfLocation: Int = getColumnIndexOrThrow(_stmt, "location")
        val _columnIndexOfLatitude: Int = getColumnIndexOrThrow(_stmt, "latitude")
        val _columnIndexOfLongitude: Int = getColumnIndexOrThrow(_stmt, "longitude")
        val _columnIndexOfImageUrls: Int = getColumnIndexOrThrow(_stmt, "imageUrls")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfCondition: Int = getColumnIndexOrThrow(_stmt, "condition")
        val _columnIndexOfHarvestDate: Int = getColumnIndexOrThrow(_stmt, "harvestDate")
        val _columnIndexOfExpiryDate: Int = getColumnIndexOrThrow(_stmt, "expiryDate")
        val _columnIndexOfBirthDate: Int = getColumnIndexOrThrow(_stmt, "birthDate")
        val _columnIndexOfVaccinationRecordsJson: Int = getColumnIndexOrThrow(_stmt,
            "vaccinationRecordsJson")
        val _columnIndexOfWeightGrams: Int = getColumnIndexOrThrow(_stmt, "weightGrams")
        val _columnIndexOfHeightCm: Int = getColumnIndexOrThrow(_stmt, "heightCm")
        val _columnIndexOfGender: Int = getColumnIndexOrThrow(_stmt, "gender")
        val _columnIndexOfColor: Int = getColumnIndexOrThrow(_stmt, "color")
        val _columnIndexOfBreed: Int = getColumnIndexOrThrow(_stmt, "breed")
        val _columnIndexOfRaisingPurpose: Int = getColumnIndexOrThrow(_stmt, "raisingPurpose")
        val _columnIndexOfHealthStatus: Int = getColumnIndexOrThrow(_stmt, "healthStatus")
        val _columnIndexOfBirdCode: Int = getColumnIndexOrThrow(_stmt, "birdCode")
        val _columnIndexOfColorTag: Int = getColumnIndexOrThrow(_stmt, "colorTag")
        val _columnIndexOfFamilyTreeId: Int = getColumnIndexOrThrow(_stmt, "familyTreeId")
        val _columnIndexOfSourceAssetId: Int = getColumnIndexOrThrow(_stmt, "sourceAssetId")
        val _columnIndexOfParentIdsJson: Int = getColumnIndexOrThrow(_stmt, "parentIdsJson")
        val _columnIndexOfBreedingStatus: Int = getColumnIndexOrThrow(_stmt, "breedingStatus")
        val _columnIndexOfTransferHistoryJson: Int = getColumnIndexOrThrow(_stmt,
            "transferHistoryJson")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfLastModifiedAt: Int = getColumnIndexOrThrow(_stmt, "lastModifiedAt")
        val _columnIndexOfIsDeleted: Int = getColumnIndexOrThrow(_stmt, "isDeleted")
        val _columnIndexOfDeletedAt: Int = getColumnIndexOrThrow(_stmt, "deletedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfStage: Int = getColumnIndexOrThrow(_stmt, "stage")
        val _columnIndexOfLifecycleStatus: Int = getColumnIndexOrThrow(_stmt, "lifecycleStatus")
        val _columnIndexOfParentMaleId: Int = getColumnIndexOrThrow(_stmt, "parentMaleId")
        val _columnIndexOfParentFemaleId: Int = getColumnIndexOrThrow(_stmt, "parentFemaleId")
        val _columnIndexOfAgeWeeks: Int = getColumnIndexOrThrow(_stmt, "ageWeeks")
        val _columnIndexOfLastStageTransitionAt: Int = getColumnIndexOrThrow(_stmt,
            "lastStageTransitionAt")
        val _columnIndexOfBreederEligibleAt: Int = getColumnIndexOrThrow(_stmt, "breederEligibleAt")
        val _columnIndexOfIsBatch: Int = getColumnIndexOrThrow(_stmt, "isBatch")
        val _columnIndexOfBatchId: Int = getColumnIndexOrThrow(_stmt, "batchId")
        val _columnIndexOfSplitAt: Int = getColumnIndexOrThrow(_stmt, "splitAt")
        val _columnIndexOfSplitIntoIds: Int = getColumnIndexOrThrow(_stmt, "splitIntoIds")
        val _columnIndexOfDocumentUrls: Int = getColumnIndexOrThrow(_stmt, "documentUrls")
        val _columnIndexOfQrCodeUrl: Int = getColumnIndexOrThrow(_stmt, "qrCodeUrl")
        val _columnIndexOfCustomStatus: Int = getColumnIndexOrThrow(_stmt, "customStatus")
        val _columnIndexOfDebug: Int = getColumnIndexOrThrow(_stmt, "debug")
        val _columnIndexOfDeliveryOptions: Int = getColumnIndexOrThrow(_stmt, "deliveryOptions")
        val _columnIndexOfDeliveryCost: Int = getColumnIndexOrThrow(_stmt, "deliveryCost")
        val _columnIndexOfLeadTimeDays: Int = getColumnIndexOrThrow(_stmt, "leadTimeDays")
        val _columnIndexOfMotherId: Int = getColumnIndexOrThrow(_stmt, "motherId")
        val _columnIndexOfIsBreedingUnit: Int = getColumnIndexOrThrow(_stmt, "isBreedingUnit")
        val _columnIndexOfEggsCollectedToday: Int = getColumnIndexOrThrow(_stmt,
            "eggsCollectedToday")
        val _columnIndexOfLastEggLogDate: Int = getColumnIndexOrThrow(_stmt, "lastEggLogDate")
        val _columnIndexOfReadyForSale: Int = getColumnIndexOrThrow(_stmt, "readyForSale")
        val _columnIndexOfTargetWeight: Int = getColumnIndexOrThrow(_stmt, "targetWeight")
        val _columnIndexOfIsShowcased: Int = getColumnIndexOrThrow(_stmt, "isShowcased")
        val _columnIndexOfExternalVideoUrl: Int = getColumnIndexOrThrow(_stmt, "externalVideoUrl")
        val _columnIndexOfRecordsLockedAt: Int = getColumnIndexOrThrow(_stmt, "recordsLockedAt")
        val _columnIndexOfAutoLockAfterDays: Int = getColumnIndexOrThrow(_stmt, "autoLockAfterDays")
        val _columnIndexOfLineageHistoryJson: Int = getColumnIndexOrThrow(_stmt,
            "lineageHistoryJson")
        val _columnIndexOfEditCount: Int = getColumnIndexOrThrow(_stmt, "editCount")
        val _columnIndexOfLastEditedBy: Int = getColumnIndexOrThrow(_stmt, "lastEditedBy")
        val _columnIndexOfAdminFlagged: Int = getColumnIndexOrThrow(_stmt, "adminFlagged")
        val _columnIndexOfModerationNote: Int = getColumnIndexOrThrow(_stmt, "moderationNote")
        val _columnIndexOfMetadataJson: Int = getColumnIndexOrThrow(_stmt, "metadataJson")
        val _result: MutableList<ProductEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: ProductEntity
          val _tmpProductId: String
          _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          val _tmpSellerId: String
          _tmpSellerId = _stmt.getText(_columnIndexOfSellerId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpDescription: String
          _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          val _tmpCategory: String
          _tmpCategory = _stmt.getText(_columnIndexOfCategory)
          val _tmpPrice: Double
          _tmpPrice = _stmt.getDouble(_columnIndexOfPrice)
          val _tmpQuantity: Double
          _tmpQuantity = _stmt.getDouble(_columnIndexOfQuantity)
          val _tmpUnit: String
          _tmpUnit = _stmt.getText(_columnIndexOfUnit)
          val _tmpLocation: String
          _tmpLocation = _stmt.getText(_columnIndexOfLocation)
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
          val _tmpImageUrls: List<String>
          val _tmp: String?
          if (_stmt.isNull(_columnIndexOfImageUrls)) {
            _tmp = null
          } else {
            _tmp = _stmt.getText(_columnIndexOfImageUrls)
          }
          val _tmp_1: List<String>? = AppDatabase.Converters.toStringList(_tmp)
          if (_tmp_1 == null) {
            error("Expected NON-NULL 'kotlin.collections.List<kotlin.String>', but it was NULL.")
          } else {
            _tmpImageUrls = _tmp_1
          }
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpCondition: String?
          if (_stmt.isNull(_columnIndexOfCondition)) {
            _tmpCondition = null
          } else {
            _tmpCondition = _stmt.getText(_columnIndexOfCondition)
          }
          val _tmpHarvestDate: Long?
          if (_stmt.isNull(_columnIndexOfHarvestDate)) {
            _tmpHarvestDate = null
          } else {
            _tmpHarvestDate = _stmt.getLong(_columnIndexOfHarvestDate)
          }
          val _tmpExpiryDate: Long?
          if (_stmt.isNull(_columnIndexOfExpiryDate)) {
            _tmpExpiryDate = null
          } else {
            _tmpExpiryDate = _stmt.getLong(_columnIndexOfExpiryDate)
          }
          val _tmpBirthDate: Long?
          if (_stmt.isNull(_columnIndexOfBirthDate)) {
            _tmpBirthDate = null
          } else {
            _tmpBirthDate = _stmt.getLong(_columnIndexOfBirthDate)
          }
          val _tmpVaccinationRecordsJson: String?
          if (_stmt.isNull(_columnIndexOfVaccinationRecordsJson)) {
            _tmpVaccinationRecordsJson = null
          } else {
            _tmpVaccinationRecordsJson = _stmt.getText(_columnIndexOfVaccinationRecordsJson)
          }
          val _tmpWeightGrams: Double?
          if (_stmt.isNull(_columnIndexOfWeightGrams)) {
            _tmpWeightGrams = null
          } else {
            _tmpWeightGrams = _stmt.getDouble(_columnIndexOfWeightGrams)
          }
          val _tmpHeightCm: Double?
          if (_stmt.isNull(_columnIndexOfHeightCm)) {
            _tmpHeightCm = null
          } else {
            _tmpHeightCm = _stmt.getDouble(_columnIndexOfHeightCm)
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
          val _tmpBreed: String?
          if (_stmt.isNull(_columnIndexOfBreed)) {
            _tmpBreed = null
          } else {
            _tmpBreed = _stmt.getText(_columnIndexOfBreed)
          }
          val _tmpRaisingPurpose: String?
          if (_stmt.isNull(_columnIndexOfRaisingPurpose)) {
            _tmpRaisingPurpose = null
          } else {
            _tmpRaisingPurpose = _stmt.getText(_columnIndexOfRaisingPurpose)
          }
          val _tmpHealthStatus: String?
          if (_stmt.isNull(_columnIndexOfHealthStatus)) {
            _tmpHealthStatus = null
          } else {
            _tmpHealthStatus = _stmt.getText(_columnIndexOfHealthStatus)
          }
          val _tmpBirdCode: String?
          if (_stmt.isNull(_columnIndexOfBirdCode)) {
            _tmpBirdCode = null
          } else {
            _tmpBirdCode = _stmt.getText(_columnIndexOfBirdCode)
          }
          val _tmpColorTag: String?
          if (_stmt.isNull(_columnIndexOfColorTag)) {
            _tmpColorTag = null
          } else {
            _tmpColorTag = _stmt.getText(_columnIndexOfColorTag)
          }
          val _tmpFamilyTreeId: String?
          if (_stmt.isNull(_columnIndexOfFamilyTreeId)) {
            _tmpFamilyTreeId = null
          } else {
            _tmpFamilyTreeId = _stmt.getText(_columnIndexOfFamilyTreeId)
          }
          val _tmpSourceAssetId: String?
          if (_stmt.isNull(_columnIndexOfSourceAssetId)) {
            _tmpSourceAssetId = null
          } else {
            _tmpSourceAssetId = _stmt.getText(_columnIndexOfSourceAssetId)
          }
          val _tmpParentIdsJson: String?
          if (_stmt.isNull(_columnIndexOfParentIdsJson)) {
            _tmpParentIdsJson = null
          } else {
            _tmpParentIdsJson = _stmt.getText(_columnIndexOfParentIdsJson)
          }
          val _tmpBreedingStatus: String?
          if (_stmt.isNull(_columnIndexOfBreedingStatus)) {
            _tmpBreedingStatus = null
          } else {
            _tmpBreedingStatus = _stmt.getText(_columnIndexOfBreedingStatus)
          }
          val _tmpTransferHistoryJson: String?
          if (_stmt.isNull(_columnIndexOfTransferHistoryJson)) {
            _tmpTransferHistoryJson = null
          } else {
            _tmpTransferHistoryJson = _stmt.getText(_columnIndexOfTransferHistoryJson)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpLastModifiedAt: Long
          _tmpLastModifiedAt = _stmt.getLong(_columnIndexOfLastModifiedAt)
          val _tmpIsDeleted: Boolean
          val _tmp_2: Int
          _tmp_2 = _stmt.getLong(_columnIndexOfIsDeleted).toInt()
          _tmpIsDeleted = _tmp_2 != 0
          val _tmpDeletedAt: Long?
          if (_stmt.isNull(_columnIndexOfDeletedAt)) {
            _tmpDeletedAt = null
          } else {
            _tmpDeletedAt = _stmt.getLong(_columnIndexOfDeletedAt)
          }
          val _tmpDirty: Boolean
          val _tmp_3: Int
          _tmp_3 = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp_3 != 0
          val _tmpStage: LifecycleStage?
          val _tmp_4: String?
          if (_stmt.isNull(_columnIndexOfStage)) {
            _tmp_4 = null
          } else {
            _tmp_4 = _stmt.getText(_columnIndexOfStage)
          }
          _tmpStage = AppDatabase.Converters.toLifecycleStage(_tmp_4)
          val _tmpLifecycleStatus: String?
          if (_stmt.isNull(_columnIndexOfLifecycleStatus)) {
            _tmpLifecycleStatus = null
          } else {
            _tmpLifecycleStatus = _stmt.getText(_columnIndexOfLifecycleStatus)
          }
          val _tmpParentMaleId: String?
          if (_stmt.isNull(_columnIndexOfParentMaleId)) {
            _tmpParentMaleId = null
          } else {
            _tmpParentMaleId = _stmt.getText(_columnIndexOfParentMaleId)
          }
          val _tmpParentFemaleId: String?
          if (_stmt.isNull(_columnIndexOfParentFemaleId)) {
            _tmpParentFemaleId = null
          } else {
            _tmpParentFemaleId = _stmt.getText(_columnIndexOfParentFemaleId)
          }
          val _tmpAgeWeeks: Int?
          if (_stmt.isNull(_columnIndexOfAgeWeeks)) {
            _tmpAgeWeeks = null
          } else {
            _tmpAgeWeeks = _stmt.getLong(_columnIndexOfAgeWeeks).toInt()
          }
          val _tmpLastStageTransitionAt: Long?
          if (_stmt.isNull(_columnIndexOfLastStageTransitionAt)) {
            _tmpLastStageTransitionAt = null
          } else {
            _tmpLastStageTransitionAt = _stmt.getLong(_columnIndexOfLastStageTransitionAt)
          }
          val _tmpBreederEligibleAt: Long?
          if (_stmt.isNull(_columnIndexOfBreederEligibleAt)) {
            _tmpBreederEligibleAt = null
          } else {
            _tmpBreederEligibleAt = _stmt.getLong(_columnIndexOfBreederEligibleAt)
          }
          val _tmpIsBatch: Boolean?
          val _tmp_5: Int?
          if (_stmt.isNull(_columnIndexOfIsBatch)) {
            _tmp_5 = null
          } else {
            _tmp_5 = _stmt.getLong(_columnIndexOfIsBatch).toInt()
          }
          _tmpIsBatch = _tmp_5?.let { it != 0 }
          val _tmpBatchId: String?
          if (_stmt.isNull(_columnIndexOfBatchId)) {
            _tmpBatchId = null
          } else {
            _tmpBatchId = _stmt.getText(_columnIndexOfBatchId)
          }
          val _tmpSplitAt: Long?
          if (_stmt.isNull(_columnIndexOfSplitAt)) {
            _tmpSplitAt = null
          } else {
            _tmpSplitAt = _stmt.getLong(_columnIndexOfSplitAt)
          }
          val _tmpSplitIntoIds: String?
          if (_stmt.isNull(_columnIndexOfSplitIntoIds)) {
            _tmpSplitIntoIds = null
          } else {
            _tmpSplitIntoIds = _stmt.getText(_columnIndexOfSplitIntoIds)
          }
          val _tmpDocumentUrls: List<String>
          val _tmp_6: String?
          if (_stmt.isNull(_columnIndexOfDocumentUrls)) {
            _tmp_6 = null
          } else {
            _tmp_6 = _stmt.getText(_columnIndexOfDocumentUrls)
          }
          val _tmp_7: List<String>? = AppDatabase.Converters.toStringList(_tmp_6)
          if (_tmp_7 == null) {
            error("Expected NON-NULL 'kotlin.collections.List<kotlin.String>', but it was NULL.")
          } else {
            _tmpDocumentUrls = _tmp_7
          }
          val _tmpQrCodeUrl: String?
          if (_stmt.isNull(_columnIndexOfQrCodeUrl)) {
            _tmpQrCodeUrl = null
          } else {
            _tmpQrCodeUrl = _stmt.getText(_columnIndexOfQrCodeUrl)
          }
          val _tmpCustomStatus: String?
          if (_stmt.isNull(_columnIndexOfCustomStatus)) {
            _tmpCustomStatus = null
          } else {
            _tmpCustomStatus = _stmt.getText(_columnIndexOfCustomStatus)
          }
          val _tmpDebug: Boolean
          val _tmp_8: Int
          _tmp_8 = _stmt.getLong(_columnIndexOfDebug).toInt()
          _tmpDebug = _tmp_8 != 0
          val _tmpDeliveryOptions: List<String>
          val _tmp_9: String?
          if (_stmt.isNull(_columnIndexOfDeliveryOptions)) {
            _tmp_9 = null
          } else {
            _tmp_9 = _stmt.getText(_columnIndexOfDeliveryOptions)
          }
          val _tmp_10: List<String>? = AppDatabase.Converters.toStringList(_tmp_9)
          if (_tmp_10 == null) {
            error("Expected NON-NULL 'kotlin.collections.List<kotlin.String>', but it was NULL.")
          } else {
            _tmpDeliveryOptions = _tmp_10
          }
          val _tmpDeliveryCost: Double?
          if (_stmt.isNull(_columnIndexOfDeliveryCost)) {
            _tmpDeliveryCost = null
          } else {
            _tmpDeliveryCost = _stmt.getDouble(_columnIndexOfDeliveryCost)
          }
          val _tmpLeadTimeDays: Int?
          if (_stmt.isNull(_columnIndexOfLeadTimeDays)) {
            _tmpLeadTimeDays = null
          } else {
            _tmpLeadTimeDays = _stmt.getLong(_columnIndexOfLeadTimeDays).toInt()
          }
          val _tmpMotherId: String?
          if (_stmt.isNull(_columnIndexOfMotherId)) {
            _tmpMotherId = null
          } else {
            _tmpMotherId = _stmt.getText(_columnIndexOfMotherId)
          }
          val _tmpIsBreedingUnit: Boolean
          val _tmp_11: Int
          _tmp_11 = _stmt.getLong(_columnIndexOfIsBreedingUnit).toInt()
          _tmpIsBreedingUnit = _tmp_11 != 0
          val _tmpEggsCollectedToday: Int
          _tmpEggsCollectedToday = _stmt.getLong(_columnIndexOfEggsCollectedToday).toInt()
          val _tmpLastEggLogDate: Long?
          if (_stmt.isNull(_columnIndexOfLastEggLogDate)) {
            _tmpLastEggLogDate = null
          } else {
            _tmpLastEggLogDate = _stmt.getLong(_columnIndexOfLastEggLogDate)
          }
          val _tmpReadyForSale: Boolean
          val _tmp_12: Int
          _tmp_12 = _stmt.getLong(_columnIndexOfReadyForSale).toInt()
          _tmpReadyForSale = _tmp_12 != 0
          val _tmpTargetWeight: Double?
          if (_stmt.isNull(_columnIndexOfTargetWeight)) {
            _tmpTargetWeight = null
          } else {
            _tmpTargetWeight = _stmt.getDouble(_columnIndexOfTargetWeight)
          }
          val _tmpIsShowcased: Boolean
          val _tmp_13: Int
          _tmp_13 = _stmt.getLong(_columnIndexOfIsShowcased).toInt()
          _tmpIsShowcased = _tmp_13 != 0
          val _tmpExternalVideoUrl: String?
          if (_stmt.isNull(_columnIndexOfExternalVideoUrl)) {
            _tmpExternalVideoUrl = null
          } else {
            _tmpExternalVideoUrl = _stmt.getText(_columnIndexOfExternalVideoUrl)
          }
          val _tmpRecordsLockedAt: Long?
          if (_stmt.isNull(_columnIndexOfRecordsLockedAt)) {
            _tmpRecordsLockedAt = null
          } else {
            _tmpRecordsLockedAt = _stmt.getLong(_columnIndexOfRecordsLockedAt)
          }
          val _tmpAutoLockAfterDays: Int
          _tmpAutoLockAfterDays = _stmt.getLong(_columnIndexOfAutoLockAfterDays).toInt()
          val _tmpLineageHistoryJson: String?
          if (_stmt.isNull(_columnIndexOfLineageHistoryJson)) {
            _tmpLineageHistoryJson = null
          } else {
            _tmpLineageHistoryJson = _stmt.getText(_columnIndexOfLineageHistoryJson)
          }
          val _tmpEditCount: Int
          _tmpEditCount = _stmt.getLong(_columnIndexOfEditCount).toInt()
          val _tmpLastEditedBy: String?
          if (_stmt.isNull(_columnIndexOfLastEditedBy)) {
            _tmpLastEditedBy = null
          } else {
            _tmpLastEditedBy = _stmt.getText(_columnIndexOfLastEditedBy)
          }
          val _tmpAdminFlagged: Boolean
          val _tmp_14: Int
          _tmp_14 = _stmt.getLong(_columnIndexOfAdminFlagged).toInt()
          _tmpAdminFlagged = _tmp_14 != 0
          val _tmpModerationNote: String?
          if (_stmt.isNull(_columnIndexOfModerationNote)) {
            _tmpModerationNote = null
          } else {
            _tmpModerationNote = _stmt.getText(_columnIndexOfModerationNote)
          }
          val _tmpMetadataJson: String
          _tmpMetadataJson = _stmt.getText(_columnIndexOfMetadataJson)
          _item =
              ProductEntity(_tmpProductId,_tmpSellerId,_tmpName,_tmpDescription,_tmpCategory,_tmpPrice,_tmpQuantity,_tmpUnit,_tmpLocation,_tmpLatitude,_tmpLongitude,_tmpImageUrls,_tmpStatus,_tmpCondition,_tmpHarvestDate,_tmpExpiryDate,_tmpBirthDate,_tmpVaccinationRecordsJson,_tmpWeightGrams,_tmpHeightCm,_tmpGender,_tmpColor,_tmpBreed,_tmpRaisingPurpose,_tmpHealthStatus,_tmpBirdCode,_tmpColorTag,_tmpFamilyTreeId,_tmpSourceAssetId,_tmpParentIdsJson,_tmpBreedingStatus,_tmpTransferHistoryJson,_tmpCreatedAt,_tmpUpdatedAt,_tmpLastModifiedAt,_tmpIsDeleted,_tmpDeletedAt,_tmpDirty,_tmpStage,_tmpLifecycleStatus,_tmpParentMaleId,_tmpParentFemaleId,_tmpAgeWeeks,_tmpLastStageTransitionAt,_tmpBreederEligibleAt,_tmpIsBatch,_tmpBatchId,_tmpSplitAt,_tmpSplitIntoIds,_tmpDocumentUrls,_tmpQrCodeUrl,_tmpCustomStatus,_tmpDebug,_tmpDeliveryOptions,_tmpDeliveryCost,_tmpLeadTimeDays,_tmpMotherId,_tmpIsBreedingUnit,_tmpEggsCollectedToday,_tmpLastEggLogDate,_tmpReadyForSale,_tmpTargetWeight,_tmpIsShowcased,_tmpExternalVideoUrl,_tmpRecordsLockedAt,_tmpAutoLockAfterDays,_tmpLineageHistoryJson,_tmpEditCount,_tmpLastEditedBy,_tmpAdminFlagged,_tmpModerationNote,_tmpMetadataJson)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getPotentialParents(
    ownerId: String,
    excludeId: String,
    gender: String?,
  ): List<ProductEntity> {
    val _sql: String = """
        |
        |        SELECT * FROM products 
        |        WHERE sellerId = ? 
        |        AND productId != ? 
        |        AND isBatch = 0 
        |        AND isDeleted = 0 
        |        AND (? IS NULL OR gender = ?)
        |        ORDER BY name ASC
        |    
        """.trimMargin()
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, ownerId)
        _argIndex = 2
        _stmt.bindText(_argIndex, excludeId)
        _argIndex = 3
        if (gender == null) {
          _stmt.bindNull(_argIndex)
        } else {
          _stmt.bindText(_argIndex, gender)
        }
        _argIndex = 4
        if (gender == null) {
          _stmt.bindNull(_argIndex)
        } else {
          _stmt.bindText(_argIndex, gender)
        }
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfSellerId: Int = getColumnIndexOrThrow(_stmt, "sellerId")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfCategory: Int = getColumnIndexOrThrow(_stmt, "category")
        val _columnIndexOfPrice: Int = getColumnIndexOrThrow(_stmt, "price")
        val _columnIndexOfQuantity: Int = getColumnIndexOrThrow(_stmt, "quantity")
        val _columnIndexOfUnit: Int = getColumnIndexOrThrow(_stmt, "unit")
        val _columnIndexOfLocation: Int = getColumnIndexOrThrow(_stmt, "location")
        val _columnIndexOfLatitude: Int = getColumnIndexOrThrow(_stmt, "latitude")
        val _columnIndexOfLongitude: Int = getColumnIndexOrThrow(_stmt, "longitude")
        val _columnIndexOfImageUrls: Int = getColumnIndexOrThrow(_stmt, "imageUrls")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfCondition: Int = getColumnIndexOrThrow(_stmt, "condition")
        val _columnIndexOfHarvestDate: Int = getColumnIndexOrThrow(_stmt, "harvestDate")
        val _columnIndexOfExpiryDate: Int = getColumnIndexOrThrow(_stmt, "expiryDate")
        val _columnIndexOfBirthDate: Int = getColumnIndexOrThrow(_stmt, "birthDate")
        val _columnIndexOfVaccinationRecordsJson: Int = getColumnIndexOrThrow(_stmt,
            "vaccinationRecordsJson")
        val _columnIndexOfWeightGrams: Int = getColumnIndexOrThrow(_stmt, "weightGrams")
        val _columnIndexOfHeightCm: Int = getColumnIndexOrThrow(_stmt, "heightCm")
        val _columnIndexOfGender: Int = getColumnIndexOrThrow(_stmt, "gender")
        val _columnIndexOfColor: Int = getColumnIndexOrThrow(_stmt, "color")
        val _columnIndexOfBreed: Int = getColumnIndexOrThrow(_stmt, "breed")
        val _columnIndexOfRaisingPurpose: Int = getColumnIndexOrThrow(_stmt, "raisingPurpose")
        val _columnIndexOfHealthStatus: Int = getColumnIndexOrThrow(_stmt, "healthStatus")
        val _columnIndexOfBirdCode: Int = getColumnIndexOrThrow(_stmt, "birdCode")
        val _columnIndexOfColorTag: Int = getColumnIndexOrThrow(_stmt, "colorTag")
        val _columnIndexOfFamilyTreeId: Int = getColumnIndexOrThrow(_stmt, "familyTreeId")
        val _columnIndexOfSourceAssetId: Int = getColumnIndexOrThrow(_stmt, "sourceAssetId")
        val _columnIndexOfParentIdsJson: Int = getColumnIndexOrThrow(_stmt, "parentIdsJson")
        val _columnIndexOfBreedingStatus: Int = getColumnIndexOrThrow(_stmt, "breedingStatus")
        val _columnIndexOfTransferHistoryJson: Int = getColumnIndexOrThrow(_stmt,
            "transferHistoryJson")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfLastModifiedAt: Int = getColumnIndexOrThrow(_stmt, "lastModifiedAt")
        val _columnIndexOfIsDeleted: Int = getColumnIndexOrThrow(_stmt, "isDeleted")
        val _columnIndexOfDeletedAt: Int = getColumnIndexOrThrow(_stmt, "deletedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfStage: Int = getColumnIndexOrThrow(_stmt, "stage")
        val _columnIndexOfLifecycleStatus: Int = getColumnIndexOrThrow(_stmt, "lifecycleStatus")
        val _columnIndexOfParentMaleId: Int = getColumnIndexOrThrow(_stmt, "parentMaleId")
        val _columnIndexOfParentFemaleId: Int = getColumnIndexOrThrow(_stmt, "parentFemaleId")
        val _columnIndexOfAgeWeeks: Int = getColumnIndexOrThrow(_stmt, "ageWeeks")
        val _columnIndexOfLastStageTransitionAt: Int = getColumnIndexOrThrow(_stmt,
            "lastStageTransitionAt")
        val _columnIndexOfBreederEligibleAt: Int = getColumnIndexOrThrow(_stmt, "breederEligibleAt")
        val _columnIndexOfIsBatch: Int = getColumnIndexOrThrow(_stmt, "isBatch")
        val _columnIndexOfBatchId: Int = getColumnIndexOrThrow(_stmt, "batchId")
        val _columnIndexOfSplitAt: Int = getColumnIndexOrThrow(_stmt, "splitAt")
        val _columnIndexOfSplitIntoIds: Int = getColumnIndexOrThrow(_stmt, "splitIntoIds")
        val _columnIndexOfDocumentUrls: Int = getColumnIndexOrThrow(_stmt, "documentUrls")
        val _columnIndexOfQrCodeUrl: Int = getColumnIndexOrThrow(_stmt, "qrCodeUrl")
        val _columnIndexOfCustomStatus: Int = getColumnIndexOrThrow(_stmt, "customStatus")
        val _columnIndexOfDebug: Int = getColumnIndexOrThrow(_stmt, "debug")
        val _columnIndexOfDeliveryOptions: Int = getColumnIndexOrThrow(_stmt, "deliveryOptions")
        val _columnIndexOfDeliveryCost: Int = getColumnIndexOrThrow(_stmt, "deliveryCost")
        val _columnIndexOfLeadTimeDays: Int = getColumnIndexOrThrow(_stmt, "leadTimeDays")
        val _columnIndexOfMotherId: Int = getColumnIndexOrThrow(_stmt, "motherId")
        val _columnIndexOfIsBreedingUnit: Int = getColumnIndexOrThrow(_stmt, "isBreedingUnit")
        val _columnIndexOfEggsCollectedToday: Int = getColumnIndexOrThrow(_stmt,
            "eggsCollectedToday")
        val _columnIndexOfLastEggLogDate: Int = getColumnIndexOrThrow(_stmt, "lastEggLogDate")
        val _columnIndexOfReadyForSale: Int = getColumnIndexOrThrow(_stmt, "readyForSale")
        val _columnIndexOfTargetWeight: Int = getColumnIndexOrThrow(_stmt, "targetWeight")
        val _columnIndexOfIsShowcased: Int = getColumnIndexOrThrow(_stmt, "isShowcased")
        val _columnIndexOfExternalVideoUrl: Int = getColumnIndexOrThrow(_stmt, "externalVideoUrl")
        val _columnIndexOfRecordsLockedAt: Int = getColumnIndexOrThrow(_stmt, "recordsLockedAt")
        val _columnIndexOfAutoLockAfterDays: Int = getColumnIndexOrThrow(_stmt, "autoLockAfterDays")
        val _columnIndexOfLineageHistoryJson: Int = getColumnIndexOrThrow(_stmt,
            "lineageHistoryJson")
        val _columnIndexOfEditCount: Int = getColumnIndexOrThrow(_stmt, "editCount")
        val _columnIndexOfLastEditedBy: Int = getColumnIndexOrThrow(_stmt, "lastEditedBy")
        val _columnIndexOfAdminFlagged: Int = getColumnIndexOrThrow(_stmt, "adminFlagged")
        val _columnIndexOfModerationNote: Int = getColumnIndexOrThrow(_stmt, "moderationNote")
        val _columnIndexOfMetadataJson: Int = getColumnIndexOrThrow(_stmt, "metadataJson")
        val _result: MutableList<ProductEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: ProductEntity
          val _tmpProductId: String
          _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          val _tmpSellerId: String
          _tmpSellerId = _stmt.getText(_columnIndexOfSellerId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpDescription: String
          _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          val _tmpCategory: String
          _tmpCategory = _stmt.getText(_columnIndexOfCategory)
          val _tmpPrice: Double
          _tmpPrice = _stmt.getDouble(_columnIndexOfPrice)
          val _tmpQuantity: Double
          _tmpQuantity = _stmt.getDouble(_columnIndexOfQuantity)
          val _tmpUnit: String
          _tmpUnit = _stmt.getText(_columnIndexOfUnit)
          val _tmpLocation: String
          _tmpLocation = _stmt.getText(_columnIndexOfLocation)
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
          val _tmpImageUrls: List<String>
          val _tmp: String?
          if (_stmt.isNull(_columnIndexOfImageUrls)) {
            _tmp = null
          } else {
            _tmp = _stmt.getText(_columnIndexOfImageUrls)
          }
          val _tmp_1: List<String>? = AppDatabase.Converters.toStringList(_tmp)
          if (_tmp_1 == null) {
            error("Expected NON-NULL 'kotlin.collections.List<kotlin.String>', but it was NULL.")
          } else {
            _tmpImageUrls = _tmp_1
          }
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpCondition: String?
          if (_stmt.isNull(_columnIndexOfCondition)) {
            _tmpCondition = null
          } else {
            _tmpCondition = _stmt.getText(_columnIndexOfCondition)
          }
          val _tmpHarvestDate: Long?
          if (_stmt.isNull(_columnIndexOfHarvestDate)) {
            _tmpHarvestDate = null
          } else {
            _tmpHarvestDate = _stmt.getLong(_columnIndexOfHarvestDate)
          }
          val _tmpExpiryDate: Long?
          if (_stmt.isNull(_columnIndexOfExpiryDate)) {
            _tmpExpiryDate = null
          } else {
            _tmpExpiryDate = _stmt.getLong(_columnIndexOfExpiryDate)
          }
          val _tmpBirthDate: Long?
          if (_stmt.isNull(_columnIndexOfBirthDate)) {
            _tmpBirthDate = null
          } else {
            _tmpBirthDate = _stmt.getLong(_columnIndexOfBirthDate)
          }
          val _tmpVaccinationRecordsJson: String?
          if (_stmt.isNull(_columnIndexOfVaccinationRecordsJson)) {
            _tmpVaccinationRecordsJson = null
          } else {
            _tmpVaccinationRecordsJson = _stmt.getText(_columnIndexOfVaccinationRecordsJson)
          }
          val _tmpWeightGrams: Double?
          if (_stmt.isNull(_columnIndexOfWeightGrams)) {
            _tmpWeightGrams = null
          } else {
            _tmpWeightGrams = _stmt.getDouble(_columnIndexOfWeightGrams)
          }
          val _tmpHeightCm: Double?
          if (_stmt.isNull(_columnIndexOfHeightCm)) {
            _tmpHeightCm = null
          } else {
            _tmpHeightCm = _stmt.getDouble(_columnIndexOfHeightCm)
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
          val _tmpBreed: String?
          if (_stmt.isNull(_columnIndexOfBreed)) {
            _tmpBreed = null
          } else {
            _tmpBreed = _stmt.getText(_columnIndexOfBreed)
          }
          val _tmpRaisingPurpose: String?
          if (_stmt.isNull(_columnIndexOfRaisingPurpose)) {
            _tmpRaisingPurpose = null
          } else {
            _tmpRaisingPurpose = _stmt.getText(_columnIndexOfRaisingPurpose)
          }
          val _tmpHealthStatus: String?
          if (_stmt.isNull(_columnIndexOfHealthStatus)) {
            _tmpHealthStatus = null
          } else {
            _tmpHealthStatus = _stmt.getText(_columnIndexOfHealthStatus)
          }
          val _tmpBirdCode: String?
          if (_stmt.isNull(_columnIndexOfBirdCode)) {
            _tmpBirdCode = null
          } else {
            _tmpBirdCode = _stmt.getText(_columnIndexOfBirdCode)
          }
          val _tmpColorTag: String?
          if (_stmt.isNull(_columnIndexOfColorTag)) {
            _tmpColorTag = null
          } else {
            _tmpColorTag = _stmt.getText(_columnIndexOfColorTag)
          }
          val _tmpFamilyTreeId: String?
          if (_stmt.isNull(_columnIndexOfFamilyTreeId)) {
            _tmpFamilyTreeId = null
          } else {
            _tmpFamilyTreeId = _stmt.getText(_columnIndexOfFamilyTreeId)
          }
          val _tmpSourceAssetId: String?
          if (_stmt.isNull(_columnIndexOfSourceAssetId)) {
            _tmpSourceAssetId = null
          } else {
            _tmpSourceAssetId = _stmt.getText(_columnIndexOfSourceAssetId)
          }
          val _tmpParentIdsJson: String?
          if (_stmt.isNull(_columnIndexOfParentIdsJson)) {
            _tmpParentIdsJson = null
          } else {
            _tmpParentIdsJson = _stmt.getText(_columnIndexOfParentIdsJson)
          }
          val _tmpBreedingStatus: String?
          if (_stmt.isNull(_columnIndexOfBreedingStatus)) {
            _tmpBreedingStatus = null
          } else {
            _tmpBreedingStatus = _stmt.getText(_columnIndexOfBreedingStatus)
          }
          val _tmpTransferHistoryJson: String?
          if (_stmt.isNull(_columnIndexOfTransferHistoryJson)) {
            _tmpTransferHistoryJson = null
          } else {
            _tmpTransferHistoryJson = _stmt.getText(_columnIndexOfTransferHistoryJson)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpLastModifiedAt: Long
          _tmpLastModifiedAt = _stmt.getLong(_columnIndexOfLastModifiedAt)
          val _tmpIsDeleted: Boolean
          val _tmp_2: Int
          _tmp_2 = _stmt.getLong(_columnIndexOfIsDeleted).toInt()
          _tmpIsDeleted = _tmp_2 != 0
          val _tmpDeletedAt: Long?
          if (_stmt.isNull(_columnIndexOfDeletedAt)) {
            _tmpDeletedAt = null
          } else {
            _tmpDeletedAt = _stmt.getLong(_columnIndexOfDeletedAt)
          }
          val _tmpDirty: Boolean
          val _tmp_3: Int
          _tmp_3 = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp_3 != 0
          val _tmpStage: LifecycleStage?
          val _tmp_4: String?
          if (_stmt.isNull(_columnIndexOfStage)) {
            _tmp_4 = null
          } else {
            _tmp_4 = _stmt.getText(_columnIndexOfStage)
          }
          _tmpStage = AppDatabase.Converters.toLifecycleStage(_tmp_4)
          val _tmpLifecycleStatus: String?
          if (_stmt.isNull(_columnIndexOfLifecycleStatus)) {
            _tmpLifecycleStatus = null
          } else {
            _tmpLifecycleStatus = _stmt.getText(_columnIndexOfLifecycleStatus)
          }
          val _tmpParentMaleId: String?
          if (_stmt.isNull(_columnIndexOfParentMaleId)) {
            _tmpParentMaleId = null
          } else {
            _tmpParentMaleId = _stmt.getText(_columnIndexOfParentMaleId)
          }
          val _tmpParentFemaleId: String?
          if (_stmt.isNull(_columnIndexOfParentFemaleId)) {
            _tmpParentFemaleId = null
          } else {
            _tmpParentFemaleId = _stmt.getText(_columnIndexOfParentFemaleId)
          }
          val _tmpAgeWeeks: Int?
          if (_stmt.isNull(_columnIndexOfAgeWeeks)) {
            _tmpAgeWeeks = null
          } else {
            _tmpAgeWeeks = _stmt.getLong(_columnIndexOfAgeWeeks).toInt()
          }
          val _tmpLastStageTransitionAt: Long?
          if (_stmt.isNull(_columnIndexOfLastStageTransitionAt)) {
            _tmpLastStageTransitionAt = null
          } else {
            _tmpLastStageTransitionAt = _stmt.getLong(_columnIndexOfLastStageTransitionAt)
          }
          val _tmpBreederEligibleAt: Long?
          if (_stmt.isNull(_columnIndexOfBreederEligibleAt)) {
            _tmpBreederEligibleAt = null
          } else {
            _tmpBreederEligibleAt = _stmt.getLong(_columnIndexOfBreederEligibleAt)
          }
          val _tmpIsBatch: Boolean?
          val _tmp_5: Int?
          if (_stmt.isNull(_columnIndexOfIsBatch)) {
            _tmp_5 = null
          } else {
            _tmp_5 = _stmt.getLong(_columnIndexOfIsBatch).toInt()
          }
          _tmpIsBatch = _tmp_5?.let { it != 0 }
          val _tmpBatchId: String?
          if (_stmt.isNull(_columnIndexOfBatchId)) {
            _tmpBatchId = null
          } else {
            _tmpBatchId = _stmt.getText(_columnIndexOfBatchId)
          }
          val _tmpSplitAt: Long?
          if (_stmt.isNull(_columnIndexOfSplitAt)) {
            _tmpSplitAt = null
          } else {
            _tmpSplitAt = _stmt.getLong(_columnIndexOfSplitAt)
          }
          val _tmpSplitIntoIds: String?
          if (_stmt.isNull(_columnIndexOfSplitIntoIds)) {
            _tmpSplitIntoIds = null
          } else {
            _tmpSplitIntoIds = _stmt.getText(_columnIndexOfSplitIntoIds)
          }
          val _tmpDocumentUrls: List<String>
          val _tmp_6: String?
          if (_stmt.isNull(_columnIndexOfDocumentUrls)) {
            _tmp_6 = null
          } else {
            _tmp_6 = _stmt.getText(_columnIndexOfDocumentUrls)
          }
          val _tmp_7: List<String>? = AppDatabase.Converters.toStringList(_tmp_6)
          if (_tmp_7 == null) {
            error("Expected NON-NULL 'kotlin.collections.List<kotlin.String>', but it was NULL.")
          } else {
            _tmpDocumentUrls = _tmp_7
          }
          val _tmpQrCodeUrl: String?
          if (_stmt.isNull(_columnIndexOfQrCodeUrl)) {
            _tmpQrCodeUrl = null
          } else {
            _tmpQrCodeUrl = _stmt.getText(_columnIndexOfQrCodeUrl)
          }
          val _tmpCustomStatus: String?
          if (_stmt.isNull(_columnIndexOfCustomStatus)) {
            _tmpCustomStatus = null
          } else {
            _tmpCustomStatus = _stmt.getText(_columnIndexOfCustomStatus)
          }
          val _tmpDebug: Boolean
          val _tmp_8: Int
          _tmp_8 = _stmt.getLong(_columnIndexOfDebug).toInt()
          _tmpDebug = _tmp_8 != 0
          val _tmpDeliveryOptions: List<String>
          val _tmp_9: String?
          if (_stmt.isNull(_columnIndexOfDeliveryOptions)) {
            _tmp_9 = null
          } else {
            _tmp_9 = _stmt.getText(_columnIndexOfDeliveryOptions)
          }
          val _tmp_10: List<String>? = AppDatabase.Converters.toStringList(_tmp_9)
          if (_tmp_10 == null) {
            error("Expected NON-NULL 'kotlin.collections.List<kotlin.String>', but it was NULL.")
          } else {
            _tmpDeliveryOptions = _tmp_10
          }
          val _tmpDeliveryCost: Double?
          if (_stmt.isNull(_columnIndexOfDeliveryCost)) {
            _tmpDeliveryCost = null
          } else {
            _tmpDeliveryCost = _stmt.getDouble(_columnIndexOfDeliveryCost)
          }
          val _tmpLeadTimeDays: Int?
          if (_stmt.isNull(_columnIndexOfLeadTimeDays)) {
            _tmpLeadTimeDays = null
          } else {
            _tmpLeadTimeDays = _stmt.getLong(_columnIndexOfLeadTimeDays).toInt()
          }
          val _tmpMotherId: String?
          if (_stmt.isNull(_columnIndexOfMotherId)) {
            _tmpMotherId = null
          } else {
            _tmpMotherId = _stmt.getText(_columnIndexOfMotherId)
          }
          val _tmpIsBreedingUnit: Boolean
          val _tmp_11: Int
          _tmp_11 = _stmt.getLong(_columnIndexOfIsBreedingUnit).toInt()
          _tmpIsBreedingUnit = _tmp_11 != 0
          val _tmpEggsCollectedToday: Int
          _tmpEggsCollectedToday = _stmt.getLong(_columnIndexOfEggsCollectedToday).toInt()
          val _tmpLastEggLogDate: Long?
          if (_stmt.isNull(_columnIndexOfLastEggLogDate)) {
            _tmpLastEggLogDate = null
          } else {
            _tmpLastEggLogDate = _stmt.getLong(_columnIndexOfLastEggLogDate)
          }
          val _tmpReadyForSale: Boolean
          val _tmp_12: Int
          _tmp_12 = _stmt.getLong(_columnIndexOfReadyForSale).toInt()
          _tmpReadyForSale = _tmp_12 != 0
          val _tmpTargetWeight: Double?
          if (_stmt.isNull(_columnIndexOfTargetWeight)) {
            _tmpTargetWeight = null
          } else {
            _tmpTargetWeight = _stmt.getDouble(_columnIndexOfTargetWeight)
          }
          val _tmpIsShowcased: Boolean
          val _tmp_13: Int
          _tmp_13 = _stmt.getLong(_columnIndexOfIsShowcased).toInt()
          _tmpIsShowcased = _tmp_13 != 0
          val _tmpExternalVideoUrl: String?
          if (_stmt.isNull(_columnIndexOfExternalVideoUrl)) {
            _tmpExternalVideoUrl = null
          } else {
            _tmpExternalVideoUrl = _stmt.getText(_columnIndexOfExternalVideoUrl)
          }
          val _tmpRecordsLockedAt: Long?
          if (_stmt.isNull(_columnIndexOfRecordsLockedAt)) {
            _tmpRecordsLockedAt = null
          } else {
            _tmpRecordsLockedAt = _stmt.getLong(_columnIndexOfRecordsLockedAt)
          }
          val _tmpAutoLockAfterDays: Int
          _tmpAutoLockAfterDays = _stmt.getLong(_columnIndexOfAutoLockAfterDays).toInt()
          val _tmpLineageHistoryJson: String?
          if (_stmt.isNull(_columnIndexOfLineageHistoryJson)) {
            _tmpLineageHistoryJson = null
          } else {
            _tmpLineageHistoryJson = _stmt.getText(_columnIndexOfLineageHistoryJson)
          }
          val _tmpEditCount: Int
          _tmpEditCount = _stmt.getLong(_columnIndexOfEditCount).toInt()
          val _tmpLastEditedBy: String?
          if (_stmt.isNull(_columnIndexOfLastEditedBy)) {
            _tmpLastEditedBy = null
          } else {
            _tmpLastEditedBy = _stmt.getText(_columnIndexOfLastEditedBy)
          }
          val _tmpAdminFlagged: Boolean
          val _tmp_14: Int
          _tmp_14 = _stmt.getLong(_columnIndexOfAdminFlagged).toInt()
          _tmpAdminFlagged = _tmp_14 != 0
          val _tmpModerationNote: String?
          if (_stmt.isNull(_columnIndexOfModerationNote)) {
            _tmpModerationNote = null
          } else {
            _tmpModerationNote = _stmt.getText(_columnIndexOfModerationNote)
          }
          val _tmpMetadataJson: String
          _tmpMetadataJson = _stmt.getText(_columnIndexOfMetadataJson)
          _item =
              ProductEntity(_tmpProductId,_tmpSellerId,_tmpName,_tmpDescription,_tmpCategory,_tmpPrice,_tmpQuantity,_tmpUnit,_tmpLocation,_tmpLatitude,_tmpLongitude,_tmpImageUrls,_tmpStatus,_tmpCondition,_tmpHarvestDate,_tmpExpiryDate,_tmpBirthDate,_tmpVaccinationRecordsJson,_tmpWeightGrams,_tmpHeightCm,_tmpGender,_tmpColor,_tmpBreed,_tmpRaisingPurpose,_tmpHealthStatus,_tmpBirdCode,_tmpColorTag,_tmpFamilyTreeId,_tmpSourceAssetId,_tmpParentIdsJson,_tmpBreedingStatus,_tmpTransferHistoryJson,_tmpCreatedAt,_tmpUpdatedAt,_tmpLastModifiedAt,_tmpIsDeleted,_tmpDeletedAt,_tmpDirty,_tmpStage,_tmpLifecycleStatus,_tmpParentMaleId,_tmpParentFemaleId,_tmpAgeWeeks,_tmpLastStageTransitionAt,_tmpBreederEligibleAt,_tmpIsBatch,_tmpBatchId,_tmpSplitAt,_tmpSplitIntoIds,_tmpDocumentUrls,_tmpQrCodeUrl,_tmpCustomStatus,_tmpDebug,_tmpDeliveryOptions,_tmpDeliveryCost,_tmpLeadTimeDays,_tmpMotherId,_tmpIsBreedingUnit,_tmpEggsCollectedToday,_tmpLastEggLogDate,_tmpReadyForSale,_tmpTargetWeight,_tmpIsShowcased,_tmpExternalVideoUrl,_tmpRecordsLockedAt,_tmpAutoLockAfterDays,_tmpLineageHistoryJson,_tmpEditCount,_tmpLastEditedBy,_tmpAdminFlagged,_tmpModerationNote,_tmpMetadataJson)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getOffspringBatch(parentIds: List<String>): List<ProductEntity> {
    val _stringBuilder: StringBuilder = StringBuilder()
    _stringBuilder.append("SELECT * FROM products WHERE (parentMaleId IN (")
    val _inputSize: Int = parentIds.size
    appendPlaceholders(_stringBuilder, _inputSize)
    _stringBuilder.append(") OR parentFemaleId IN (")
    val _inputSize_1: Int = parentIds.size
    appendPlaceholders(_stringBuilder, _inputSize_1)
    _stringBuilder.append(")) AND isDeleted = 0")
    val _sql: String = _stringBuilder.toString()
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        for (_item: String in parentIds) {
          _stmt.bindText(_argIndex, _item)
          _argIndex++
        }
        _argIndex = 1 + _inputSize
        for (_item_1: String in parentIds) {
          _stmt.bindText(_argIndex, _item_1)
          _argIndex++
        }
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfSellerId: Int = getColumnIndexOrThrow(_stmt, "sellerId")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfCategory: Int = getColumnIndexOrThrow(_stmt, "category")
        val _columnIndexOfPrice: Int = getColumnIndexOrThrow(_stmt, "price")
        val _columnIndexOfQuantity: Int = getColumnIndexOrThrow(_stmt, "quantity")
        val _columnIndexOfUnit: Int = getColumnIndexOrThrow(_stmt, "unit")
        val _columnIndexOfLocation: Int = getColumnIndexOrThrow(_stmt, "location")
        val _columnIndexOfLatitude: Int = getColumnIndexOrThrow(_stmt, "latitude")
        val _columnIndexOfLongitude: Int = getColumnIndexOrThrow(_stmt, "longitude")
        val _columnIndexOfImageUrls: Int = getColumnIndexOrThrow(_stmt, "imageUrls")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfCondition: Int = getColumnIndexOrThrow(_stmt, "condition")
        val _columnIndexOfHarvestDate: Int = getColumnIndexOrThrow(_stmt, "harvestDate")
        val _columnIndexOfExpiryDate: Int = getColumnIndexOrThrow(_stmt, "expiryDate")
        val _columnIndexOfBirthDate: Int = getColumnIndexOrThrow(_stmt, "birthDate")
        val _columnIndexOfVaccinationRecordsJson: Int = getColumnIndexOrThrow(_stmt,
            "vaccinationRecordsJson")
        val _columnIndexOfWeightGrams: Int = getColumnIndexOrThrow(_stmt, "weightGrams")
        val _columnIndexOfHeightCm: Int = getColumnIndexOrThrow(_stmt, "heightCm")
        val _columnIndexOfGender: Int = getColumnIndexOrThrow(_stmt, "gender")
        val _columnIndexOfColor: Int = getColumnIndexOrThrow(_stmt, "color")
        val _columnIndexOfBreed: Int = getColumnIndexOrThrow(_stmt, "breed")
        val _columnIndexOfRaisingPurpose: Int = getColumnIndexOrThrow(_stmt, "raisingPurpose")
        val _columnIndexOfHealthStatus: Int = getColumnIndexOrThrow(_stmt, "healthStatus")
        val _columnIndexOfBirdCode: Int = getColumnIndexOrThrow(_stmt, "birdCode")
        val _columnIndexOfColorTag: Int = getColumnIndexOrThrow(_stmt, "colorTag")
        val _columnIndexOfFamilyTreeId: Int = getColumnIndexOrThrow(_stmt, "familyTreeId")
        val _columnIndexOfSourceAssetId: Int = getColumnIndexOrThrow(_stmt, "sourceAssetId")
        val _columnIndexOfParentIdsJson: Int = getColumnIndexOrThrow(_stmt, "parentIdsJson")
        val _columnIndexOfBreedingStatus: Int = getColumnIndexOrThrow(_stmt, "breedingStatus")
        val _columnIndexOfTransferHistoryJson: Int = getColumnIndexOrThrow(_stmt,
            "transferHistoryJson")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfLastModifiedAt: Int = getColumnIndexOrThrow(_stmt, "lastModifiedAt")
        val _columnIndexOfIsDeleted: Int = getColumnIndexOrThrow(_stmt, "isDeleted")
        val _columnIndexOfDeletedAt: Int = getColumnIndexOrThrow(_stmt, "deletedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfStage: Int = getColumnIndexOrThrow(_stmt, "stage")
        val _columnIndexOfLifecycleStatus: Int = getColumnIndexOrThrow(_stmt, "lifecycleStatus")
        val _columnIndexOfParentMaleId: Int = getColumnIndexOrThrow(_stmt, "parentMaleId")
        val _columnIndexOfParentFemaleId: Int = getColumnIndexOrThrow(_stmt, "parentFemaleId")
        val _columnIndexOfAgeWeeks: Int = getColumnIndexOrThrow(_stmt, "ageWeeks")
        val _columnIndexOfLastStageTransitionAt: Int = getColumnIndexOrThrow(_stmt,
            "lastStageTransitionAt")
        val _columnIndexOfBreederEligibleAt: Int = getColumnIndexOrThrow(_stmt, "breederEligibleAt")
        val _columnIndexOfIsBatch: Int = getColumnIndexOrThrow(_stmt, "isBatch")
        val _columnIndexOfBatchId: Int = getColumnIndexOrThrow(_stmt, "batchId")
        val _columnIndexOfSplitAt: Int = getColumnIndexOrThrow(_stmt, "splitAt")
        val _columnIndexOfSplitIntoIds: Int = getColumnIndexOrThrow(_stmt, "splitIntoIds")
        val _columnIndexOfDocumentUrls: Int = getColumnIndexOrThrow(_stmt, "documentUrls")
        val _columnIndexOfQrCodeUrl: Int = getColumnIndexOrThrow(_stmt, "qrCodeUrl")
        val _columnIndexOfCustomStatus: Int = getColumnIndexOrThrow(_stmt, "customStatus")
        val _columnIndexOfDebug: Int = getColumnIndexOrThrow(_stmt, "debug")
        val _columnIndexOfDeliveryOptions: Int = getColumnIndexOrThrow(_stmt, "deliveryOptions")
        val _columnIndexOfDeliveryCost: Int = getColumnIndexOrThrow(_stmt, "deliveryCost")
        val _columnIndexOfLeadTimeDays: Int = getColumnIndexOrThrow(_stmt, "leadTimeDays")
        val _columnIndexOfMotherId: Int = getColumnIndexOrThrow(_stmt, "motherId")
        val _columnIndexOfIsBreedingUnit: Int = getColumnIndexOrThrow(_stmt, "isBreedingUnit")
        val _columnIndexOfEggsCollectedToday: Int = getColumnIndexOrThrow(_stmt,
            "eggsCollectedToday")
        val _columnIndexOfLastEggLogDate: Int = getColumnIndexOrThrow(_stmt, "lastEggLogDate")
        val _columnIndexOfReadyForSale: Int = getColumnIndexOrThrow(_stmt, "readyForSale")
        val _columnIndexOfTargetWeight: Int = getColumnIndexOrThrow(_stmt, "targetWeight")
        val _columnIndexOfIsShowcased: Int = getColumnIndexOrThrow(_stmt, "isShowcased")
        val _columnIndexOfExternalVideoUrl: Int = getColumnIndexOrThrow(_stmt, "externalVideoUrl")
        val _columnIndexOfRecordsLockedAt: Int = getColumnIndexOrThrow(_stmt, "recordsLockedAt")
        val _columnIndexOfAutoLockAfterDays: Int = getColumnIndexOrThrow(_stmt, "autoLockAfterDays")
        val _columnIndexOfLineageHistoryJson: Int = getColumnIndexOrThrow(_stmt,
            "lineageHistoryJson")
        val _columnIndexOfEditCount: Int = getColumnIndexOrThrow(_stmt, "editCount")
        val _columnIndexOfLastEditedBy: Int = getColumnIndexOrThrow(_stmt, "lastEditedBy")
        val _columnIndexOfAdminFlagged: Int = getColumnIndexOrThrow(_stmt, "adminFlagged")
        val _columnIndexOfModerationNote: Int = getColumnIndexOrThrow(_stmt, "moderationNote")
        val _columnIndexOfMetadataJson: Int = getColumnIndexOrThrow(_stmt, "metadataJson")
        val _result: MutableList<ProductEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item_2: ProductEntity
          val _tmpProductId: String
          _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          val _tmpSellerId: String
          _tmpSellerId = _stmt.getText(_columnIndexOfSellerId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpDescription: String
          _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          val _tmpCategory: String
          _tmpCategory = _stmt.getText(_columnIndexOfCategory)
          val _tmpPrice: Double
          _tmpPrice = _stmt.getDouble(_columnIndexOfPrice)
          val _tmpQuantity: Double
          _tmpQuantity = _stmt.getDouble(_columnIndexOfQuantity)
          val _tmpUnit: String
          _tmpUnit = _stmt.getText(_columnIndexOfUnit)
          val _tmpLocation: String
          _tmpLocation = _stmt.getText(_columnIndexOfLocation)
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
          val _tmpImageUrls: List<String>
          val _tmp: String?
          if (_stmt.isNull(_columnIndexOfImageUrls)) {
            _tmp = null
          } else {
            _tmp = _stmt.getText(_columnIndexOfImageUrls)
          }
          val _tmp_1: List<String>? = AppDatabase.Converters.toStringList(_tmp)
          if (_tmp_1 == null) {
            error("Expected NON-NULL 'kotlin.collections.List<kotlin.String>', but it was NULL.")
          } else {
            _tmpImageUrls = _tmp_1
          }
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpCondition: String?
          if (_stmt.isNull(_columnIndexOfCondition)) {
            _tmpCondition = null
          } else {
            _tmpCondition = _stmt.getText(_columnIndexOfCondition)
          }
          val _tmpHarvestDate: Long?
          if (_stmt.isNull(_columnIndexOfHarvestDate)) {
            _tmpHarvestDate = null
          } else {
            _tmpHarvestDate = _stmt.getLong(_columnIndexOfHarvestDate)
          }
          val _tmpExpiryDate: Long?
          if (_stmt.isNull(_columnIndexOfExpiryDate)) {
            _tmpExpiryDate = null
          } else {
            _tmpExpiryDate = _stmt.getLong(_columnIndexOfExpiryDate)
          }
          val _tmpBirthDate: Long?
          if (_stmt.isNull(_columnIndexOfBirthDate)) {
            _tmpBirthDate = null
          } else {
            _tmpBirthDate = _stmt.getLong(_columnIndexOfBirthDate)
          }
          val _tmpVaccinationRecordsJson: String?
          if (_stmt.isNull(_columnIndexOfVaccinationRecordsJson)) {
            _tmpVaccinationRecordsJson = null
          } else {
            _tmpVaccinationRecordsJson = _stmt.getText(_columnIndexOfVaccinationRecordsJson)
          }
          val _tmpWeightGrams: Double?
          if (_stmt.isNull(_columnIndexOfWeightGrams)) {
            _tmpWeightGrams = null
          } else {
            _tmpWeightGrams = _stmt.getDouble(_columnIndexOfWeightGrams)
          }
          val _tmpHeightCm: Double?
          if (_stmt.isNull(_columnIndexOfHeightCm)) {
            _tmpHeightCm = null
          } else {
            _tmpHeightCm = _stmt.getDouble(_columnIndexOfHeightCm)
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
          val _tmpBreed: String?
          if (_stmt.isNull(_columnIndexOfBreed)) {
            _tmpBreed = null
          } else {
            _tmpBreed = _stmt.getText(_columnIndexOfBreed)
          }
          val _tmpRaisingPurpose: String?
          if (_stmt.isNull(_columnIndexOfRaisingPurpose)) {
            _tmpRaisingPurpose = null
          } else {
            _tmpRaisingPurpose = _stmt.getText(_columnIndexOfRaisingPurpose)
          }
          val _tmpHealthStatus: String?
          if (_stmt.isNull(_columnIndexOfHealthStatus)) {
            _tmpHealthStatus = null
          } else {
            _tmpHealthStatus = _stmt.getText(_columnIndexOfHealthStatus)
          }
          val _tmpBirdCode: String?
          if (_stmt.isNull(_columnIndexOfBirdCode)) {
            _tmpBirdCode = null
          } else {
            _tmpBirdCode = _stmt.getText(_columnIndexOfBirdCode)
          }
          val _tmpColorTag: String?
          if (_stmt.isNull(_columnIndexOfColorTag)) {
            _tmpColorTag = null
          } else {
            _tmpColorTag = _stmt.getText(_columnIndexOfColorTag)
          }
          val _tmpFamilyTreeId: String?
          if (_stmt.isNull(_columnIndexOfFamilyTreeId)) {
            _tmpFamilyTreeId = null
          } else {
            _tmpFamilyTreeId = _stmt.getText(_columnIndexOfFamilyTreeId)
          }
          val _tmpSourceAssetId: String?
          if (_stmt.isNull(_columnIndexOfSourceAssetId)) {
            _tmpSourceAssetId = null
          } else {
            _tmpSourceAssetId = _stmt.getText(_columnIndexOfSourceAssetId)
          }
          val _tmpParentIdsJson: String?
          if (_stmt.isNull(_columnIndexOfParentIdsJson)) {
            _tmpParentIdsJson = null
          } else {
            _tmpParentIdsJson = _stmt.getText(_columnIndexOfParentIdsJson)
          }
          val _tmpBreedingStatus: String?
          if (_stmt.isNull(_columnIndexOfBreedingStatus)) {
            _tmpBreedingStatus = null
          } else {
            _tmpBreedingStatus = _stmt.getText(_columnIndexOfBreedingStatus)
          }
          val _tmpTransferHistoryJson: String?
          if (_stmt.isNull(_columnIndexOfTransferHistoryJson)) {
            _tmpTransferHistoryJson = null
          } else {
            _tmpTransferHistoryJson = _stmt.getText(_columnIndexOfTransferHistoryJson)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpLastModifiedAt: Long
          _tmpLastModifiedAt = _stmt.getLong(_columnIndexOfLastModifiedAt)
          val _tmpIsDeleted: Boolean
          val _tmp_2: Int
          _tmp_2 = _stmt.getLong(_columnIndexOfIsDeleted).toInt()
          _tmpIsDeleted = _tmp_2 != 0
          val _tmpDeletedAt: Long?
          if (_stmt.isNull(_columnIndexOfDeletedAt)) {
            _tmpDeletedAt = null
          } else {
            _tmpDeletedAt = _stmt.getLong(_columnIndexOfDeletedAt)
          }
          val _tmpDirty: Boolean
          val _tmp_3: Int
          _tmp_3 = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp_3 != 0
          val _tmpStage: LifecycleStage?
          val _tmp_4: String?
          if (_stmt.isNull(_columnIndexOfStage)) {
            _tmp_4 = null
          } else {
            _tmp_4 = _stmt.getText(_columnIndexOfStage)
          }
          _tmpStage = AppDatabase.Converters.toLifecycleStage(_tmp_4)
          val _tmpLifecycleStatus: String?
          if (_stmt.isNull(_columnIndexOfLifecycleStatus)) {
            _tmpLifecycleStatus = null
          } else {
            _tmpLifecycleStatus = _stmt.getText(_columnIndexOfLifecycleStatus)
          }
          val _tmpParentMaleId: String?
          if (_stmt.isNull(_columnIndexOfParentMaleId)) {
            _tmpParentMaleId = null
          } else {
            _tmpParentMaleId = _stmt.getText(_columnIndexOfParentMaleId)
          }
          val _tmpParentFemaleId: String?
          if (_stmt.isNull(_columnIndexOfParentFemaleId)) {
            _tmpParentFemaleId = null
          } else {
            _tmpParentFemaleId = _stmt.getText(_columnIndexOfParentFemaleId)
          }
          val _tmpAgeWeeks: Int?
          if (_stmt.isNull(_columnIndexOfAgeWeeks)) {
            _tmpAgeWeeks = null
          } else {
            _tmpAgeWeeks = _stmt.getLong(_columnIndexOfAgeWeeks).toInt()
          }
          val _tmpLastStageTransitionAt: Long?
          if (_stmt.isNull(_columnIndexOfLastStageTransitionAt)) {
            _tmpLastStageTransitionAt = null
          } else {
            _tmpLastStageTransitionAt = _stmt.getLong(_columnIndexOfLastStageTransitionAt)
          }
          val _tmpBreederEligibleAt: Long?
          if (_stmt.isNull(_columnIndexOfBreederEligibleAt)) {
            _tmpBreederEligibleAt = null
          } else {
            _tmpBreederEligibleAt = _stmt.getLong(_columnIndexOfBreederEligibleAt)
          }
          val _tmpIsBatch: Boolean?
          val _tmp_5: Int?
          if (_stmt.isNull(_columnIndexOfIsBatch)) {
            _tmp_5 = null
          } else {
            _tmp_5 = _stmt.getLong(_columnIndexOfIsBatch).toInt()
          }
          _tmpIsBatch = _tmp_5?.let { it != 0 }
          val _tmpBatchId: String?
          if (_stmt.isNull(_columnIndexOfBatchId)) {
            _tmpBatchId = null
          } else {
            _tmpBatchId = _stmt.getText(_columnIndexOfBatchId)
          }
          val _tmpSplitAt: Long?
          if (_stmt.isNull(_columnIndexOfSplitAt)) {
            _tmpSplitAt = null
          } else {
            _tmpSplitAt = _stmt.getLong(_columnIndexOfSplitAt)
          }
          val _tmpSplitIntoIds: String?
          if (_stmt.isNull(_columnIndexOfSplitIntoIds)) {
            _tmpSplitIntoIds = null
          } else {
            _tmpSplitIntoIds = _stmt.getText(_columnIndexOfSplitIntoIds)
          }
          val _tmpDocumentUrls: List<String>
          val _tmp_6: String?
          if (_stmt.isNull(_columnIndexOfDocumentUrls)) {
            _tmp_6 = null
          } else {
            _tmp_6 = _stmt.getText(_columnIndexOfDocumentUrls)
          }
          val _tmp_7: List<String>? = AppDatabase.Converters.toStringList(_tmp_6)
          if (_tmp_7 == null) {
            error("Expected NON-NULL 'kotlin.collections.List<kotlin.String>', but it was NULL.")
          } else {
            _tmpDocumentUrls = _tmp_7
          }
          val _tmpQrCodeUrl: String?
          if (_stmt.isNull(_columnIndexOfQrCodeUrl)) {
            _tmpQrCodeUrl = null
          } else {
            _tmpQrCodeUrl = _stmt.getText(_columnIndexOfQrCodeUrl)
          }
          val _tmpCustomStatus: String?
          if (_stmt.isNull(_columnIndexOfCustomStatus)) {
            _tmpCustomStatus = null
          } else {
            _tmpCustomStatus = _stmt.getText(_columnIndexOfCustomStatus)
          }
          val _tmpDebug: Boolean
          val _tmp_8: Int
          _tmp_8 = _stmt.getLong(_columnIndexOfDebug).toInt()
          _tmpDebug = _tmp_8 != 0
          val _tmpDeliveryOptions: List<String>
          val _tmp_9: String?
          if (_stmt.isNull(_columnIndexOfDeliveryOptions)) {
            _tmp_9 = null
          } else {
            _tmp_9 = _stmt.getText(_columnIndexOfDeliveryOptions)
          }
          val _tmp_10: List<String>? = AppDatabase.Converters.toStringList(_tmp_9)
          if (_tmp_10 == null) {
            error("Expected NON-NULL 'kotlin.collections.List<kotlin.String>', but it was NULL.")
          } else {
            _tmpDeliveryOptions = _tmp_10
          }
          val _tmpDeliveryCost: Double?
          if (_stmt.isNull(_columnIndexOfDeliveryCost)) {
            _tmpDeliveryCost = null
          } else {
            _tmpDeliveryCost = _stmt.getDouble(_columnIndexOfDeliveryCost)
          }
          val _tmpLeadTimeDays: Int?
          if (_stmt.isNull(_columnIndexOfLeadTimeDays)) {
            _tmpLeadTimeDays = null
          } else {
            _tmpLeadTimeDays = _stmt.getLong(_columnIndexOfLeadTimeDays).toInt()
          }
          val _tmpMotherId: String?
          if (_stmt.isNull(_columnIndexOfMotherId)) {
            _tmpMotherId = null
          } else {
            _tmpMotherId = _stmt.getText(_columnIndexOfMotherId)
          }
          val _tmpIsBreedingUnit: Boolean
          val _tmp_11: Int
          _tmp_11 = _stmt.getLong(_columnIndexOfIsBreedingUnit).toInt()
          _tmpIsBreedingUnit = _tmp_11 != 0
          val _tmpEggsCollectedToday: Int
          _tmpEggsCollectedToday = _stmt.getLong(_columnIndexOfEggsCollectedToday).toInt()
          val _tmpLastEggLogDate: Long?
          if (_stmt.isNull(_columnIndexOfLastEggLogDate)) {
            _tmpLastEggLogDate = null
          } else {
            _tmpLastEggLogDate = _stmt.getLong(_columnIndexOfLastEggLogDate)
          }
          val _tmpReadyForSale: Boolean
          val _tmp_12: Int
          _tmp_12 = _stmt.getLong(_columnIndexOfReadyForSale).toInt()
          _tmpReadyForSale = _tmp_12 != 0
          val _tmpTargetWeight: Double?
          if (_stmt.isNull(_columnIndexOfTargetWeight)) {
            _tmpTargetWeight = null
          } else {
            _tmpTargetWeight = _stmt.getDouble(_columnIndexOfTargetWeight)
          }
          val _tmpIsShowcased: Boolean
          val _tmp_13: Int
          _tmp_13 = _stmt.getLong(_columnIndexOfIsShowcased).toInt()
          _tmpIsShowcased = _tmp_13 != 0
          val _tmpExternalVideoUrl: String?
          if (_stmt.isNull(_columnIndexOfExternalVideoUrl)) {
            _tmpExternalVideoUrl = null
          } else {
            _tmpExternalVideoUrl = _stmt.getText(_columnIndexOfExternalVideoUrl)
          }
          val _tmpRecordsLockedAt: Long?
          if (_stmt.isNull(_columnIndexOfRecordsLockedAt)) {
            _tmpRecordsLockedAt = null
          } else {
            _tmpRecordsLockedAt = _stmt.getLong(_columnIndexOfRecordsLockedAt)
          }
          val _tmpAutoLockAfterDays: Int
          _tmpAutoLockAfterDays = _stmt.getLong(_columnIndexOfAutoLockAfterDays).toInt()
          val _tmpLineageHistoryJson: String?
          if (_stmt.isNull(_columnIndexOfLineageHistoryJson)) {
            _tmpLineageHistoryJson = null
          } else {
            _tmpLineageHistoryJson = _stmt.getText(_columnIndexOfLineageHistoryJson)
          }
          val _tmpEditCount: Int
          _tmpEditCount = _stmt.getLong(_columnIndexOfEditCount).toInt()
          val _tmpLastEditedBy: String?
          if (_stmt.isNull(_columnIndexOfLastEditedBy)) {
            _tmpLastEditedBy = null
          } else {
            _tmpLastEditedBy = _stmt.getText(_columnIndexOfLastEditedBy)
          }
          val _tmpAdminFlagged: Boolean
          val _tmp_14: Int
          _tmp_14 = _stmt.getLong(_columnIndexOfAdminFlagged).toInt()
          _tmpAdminFlagged = _tmp_14 != 0
          val _tmpModerationNote: String?
          if (_stmt.isNull(_columnIndexOfModerationNote)) {
            _tmpModerationNote = null
          } else {
            _tmpModerationNote = _stmt.getText(_columnIndexOfModerationNote)
          }
          val _tmpMetadataJson: String
          _tmpMetadataJson = _stmt.getText(_columnIndexOfMetadataJson)
          _item_2 =
              ProductEntity(_tmpProductId,_tmpSellerId,_tmpName,_tmpDescription,_tmpCategory,_tmpPrice,_tmpQuantity,_tmpUnit,_tmpLocation,_tmpLatitude,_tmpLongitude,_tmpImageUrls,_tmpStatus,_tmpCondition,_tmpHarvestDate,_tmpExpiryDate,_tmpBirthDate,_tmpVaccinationRecordsJson,_tmpWeightGrams,_tmpHeightCm,_tmpGender,_tmpColor,_tmpBreed,_tmpRaisingPurpose,_tmpHealthStatus,_tmpBirdCode,_tmpColorTag,_tmpFamilyTreeId,_tmpSourceAssetId,_tmpParentIdsJson,_tmpBreedingStatus,_tmpTransferHistoryJson,_tmpCreatedAt,_tmpUpdatedAt,_tmpLastModifiedAt,_tmpIsDeleted,_tmpDeletedAt,_tmpDirty,_tmpStage,_tmpLifecycleStatus,_tmpParentMaleId,_tmpParentFemaleId,_tmpAgeWeeks,_tmpLastStageTransitionAt,_tmpBreederEligibleAt,_tmpIsBatch,_tmpBatchId,_tmpSplitAt,_tmpSplitIntoIds,_tmpDocumentUrls,_tmpQrCodeUrl,_tmpCustomStatus,_tmpDebug,_tmpDeliveryOptions,_tmpDeliveryCost,_tmpLeadTimeDays,_tmpMotherId,_tmpIsBreedingUnit,_tmpEggsCollectedToday,_tmpLastEggLogDate,_tmpReadyForSale,_tmpTargetWeight,_tmpIsShowcased,_tmpExternalVideoUrl,_tmpRecordsLockedAt,_tmpAutoLockAfterDays,_tmpLineageHistoryJson,_tmpEditCount,_tmpLastEditedBy,_tmpAdminFlagged,_tmpModerationNote,_tmpMetadataJson)
          _result.add(_item_2)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getFlaggedProducts(): Flow<List<ProductEntity>> {
    val _sql: String = "SELECT * FROM products WHERE adminFlagged = 1 ORDER BY createdAt DESC"
    return createFlow(__db, false, arrayOf("products")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfSellerId: Int = getColumnIndexOrThrow(_stmt, "sellerId")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfCategory: Int = getColumnIndexOrThrow(_stmt, "category")
        val _columnIndexOfPrice: Int = getColumnIndexOrThrow(_stmt, "price")
        val _columnIndexOfQuantity: Int = getColumnIndexOrThrow(_stmt, "quantity")
        val _columnIndexOfUnit: Int = getColumnIndexOrThrow(_stmt, "unit")
        val _columnIndexOfLocation: Int = getColumnIndexOrThrow(_stmt, "location")
        val _columnIndexOfLatitude: Int = getColumnIndexOrThrow(_stmt, "latitude")
        val _columnIndexOfLongitude: Int = getColumnIndexOrThrow(_stmt, "longitude")
        val _columnIndexOfImageUrls: Int = getColumnIndexOrThrow(_stmt, "imageUrls")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfCondition: Int = getColumnIndexOrThrow(_stmt, "condition")
        val _columnIndexOfHarvestDate: Int = getColumnIndexOrThrow(_stmt, "harvestDate")
        val _columnIndexOfExpiryDate: Int = getColumnIndexOrThrow(_stmt, "expiryDate")
        val _columnIndexOfBirthDate: Int = getColumnIndexOrThrow(_stmt, "birthDate")
        val _columnIndexOfVaccinationRecordsJson: Int = getColumnIndexOrThrow(_stmt,
            "vaccinationRecordsJson")
        val _columnIndexOfWeightGrams: Int = getColumnIndexOrThrow(_stmt, "weightGrams")
        val _columnIndexOfHeightCm: Int = getColumnIndexOrThrow(_stmt, "heightCm")
        val _columnIndexOfGender: Int = getColumnIndexOrThrow(_stmt, "gender")
        val _columnIndexOfColor: Int = getColumnIndexOrThrow(_stmt, "color")
        val _columnIndexOfBreed: Int = getColumnIndexOrThrow(_stmt, "breed")
        val _columnIndexOfRaisingPurpose: Int = getColumnIndexOrThrow(_stmt, "raisingPurpose")
        val _columnIndexOfHealthStatus: Int = getColumnIndexOrThrow(_stmt, "healthStatus")
        val _columnIndexOfBirdCode: Int = getColumnIndexOrThrow(_stmt, "birdCode")
        val _columnIndexOfColorTag: Int = getColumnIndexOrThrow(_stmt, "colorTag")
        val _columnIndexOfFamilyTreeId: Int = getColumnIndexOrThrow(_stmt, "familyTreeId")
        val _columnIndexOfSourceAssetId: Int = getColumnIndexOrThrow(_stmt, "sourceAssetId")
        val _columnIndexOfParentIdsJson: Int = getColumnIndexOrThrow(_stmt, "parentIdsJson")
        val _columnIndexOfBreedingStatus: Int = getColumnIndexOrThrow(_stmt, "breedingStatus")
        val _columnIndexOfTransferHistoryJson: Int = getColumnIndexOrThrow(_stmt,
            "transferHistoryJson")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfLastModifiedAt: Int = getColumnIndexOrThrow(_stmt, "lastModifiedAt")
        val _columnIndexOfIsDeleted: Int = getColumnIndexOrThrow(_stmt, "isDeleted")
        val _columnIndexOfDeletedAt: Int = getColumnIndexOrThrow(_stmt, "deletedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfStage: Int = getColumnIndexOrThrow(_stmt, "stage")
        val _columnIndexOfLifecycleStatus: Int = getColumnIndexOrThrow(_stmt, "lifecycleStatus")
        val _columnIndexOfParentMaleId: Int = getColumnIndexOrThrow(_stmt, "parentMaleId")
        val _columnIndexOfParentFemaleId: Int = getColumnIndexOrThrow(_stmt, "parentFemaleId")
        val _columnIndexOfAgeWeeks: Int = getColumnIndexOrThrow(_stmt, "ageWeeks")
        val _columnIndexOfLastStageTransitionAt: Int = getColumnIndexOrThrow(_stmt,
            "lastStageTransitionAt")
        val _columnIndexOfBreederEligibleAt: Int = getColumnIndexOrThrow(_stmt, "breederEligibleAt")
        val _columnIndexOfIsBatch: Int = getColumnIndexOrThrow(_stmt, "isBatch")
        val _columnIndexOfBatchId: Int = getColumnIndexOrThrow(_stmt, "batchId")
        val _columnIndexOfSplitAt: Int = getColumnIndexOrThrow(_stmt, "splitAt")
        val _columnIndexOfSplitIntoIds: Int = getColumnIndexOrThrow(_stmt, "splitIntoIds")
        val _columnIndexOfDocumentUrls: Int = getColumnIndexOrThrow(_stmt, "documentUrls")
        val _columnIndexOfQrCodeUrl: Int = getColumnIndexOrThrow(_stmt, "qrCodeUrl")
        val _columnIndexOfCustomStatus: Int = getColumnIndexOrThrow(_stmt, "customStatus")
        val _columnIndexOfDebug: Int = getColumnIndexOrThrow(_stmt, "debug")
        val _columnIndexOfDeliveryOptions: Int = getColumnIndexOrThrow(_stmt, "deliveryOptions")
        val _columnIndexOfDeliveryCost: Int = getColumnIndexOrThrow(_stmt, "deliveryCost")
        val _columnIndexOfLeadTimeDays: Int = getColumnIndexOrThrow(_stmt, "leadTimeDays")
        val _columnIndexOfMotherId: Int = getColumnIndexOrThrow(_stmt, "motherId")
        val _columnIndexOfIsBreedingUnit: Int = getColumnIndexOrThrow(_stmt, "isBreedingUnit")
        val _columnIndexOfEggsCollectedToday: Int = getColumnIndexOrThrow(_stmt,
            "eggsCollectedToday")
        val _columnIndexOfLastEggLogDate: Int = getColumnIndexOrThrow(_stmt, "lastEggLogDate")
        val _columnIndexOfReadyForSale: Int = getColumnIndexOrThrow(_stmt, "readyForSale")
        val _columnIndexOfTargetWeight: Int = getColumnIndexOrThrow(_stmt, "targetWeight")
        val _columnIndexOfIsShowcased: Int = getColumnIndexOrThrow(_stmt, "isShowcased")
        val _columnIndexOfExternalVideoUrl: Int = getColumnIndexOrThrow(_stmt, "externalVideoUrl")
        val _columnIndexOfRecordsLockedAt: Int = getColumnIndexOrThrow(_stmt, "recordsLockedAt")
        val _columnIndexOfAutoLockAfterDays: Int = getColumnIndexOrThrow(_stmt, "autoLockAfterDays")
        val _columnIndexOfLineageHistoryJson: Int = getColumnIndexOrThrow(_stmt,
            "lineageHistoryJson")
        val _columnIndexOfEditCount: Int = getColumnIndexOrThrow(_stmt, "editCount")
        val _columnIndexOfLastEditedBy: Int = getColumnIndexOrThrow(_stmt, "lastEditedBy")
        val _columnIndexOfAdminFlagged: Int = getColumnIndexOrThrow(_stmt, "adminFlagged")
        val _columnIndexOfModerationNote: Int = getColumnIndexOrThrow(_stmt, "moderationNote")
        val _columnIndexOfMetadataJson: Int = getColumnIndexOrThrow(_stmt, "metadataJson")
        val _result: MutableList<ProductEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: ProductEntity
          val _tmpProductId: String
          _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          val _tmpSellerId: String
          _tmpSellerId = _stmt.getText(_columnIndexOfSellerId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpDescription: String
          _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          val _tmpCategory: String
          _tmpCategory = _stmt.getText(_columnIndexOfCategory)
          val _tmpPrice: Double
          _tmpPrice = _stmt.getDouble(_columnIndexOfPrice)
          val _tmpQuantity: Double
          _tmpQuantity = _stmt.getDouble(_columnIndexOfQuantity)
          val _tmpUnit: String
          _tmpUnit = _stmt.getText(_columnIndexOfUnit)
          val _tmpLocation: String
          _tmpLocation = _stmt.getText(_columnIndexOfLocation)
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
          val _tmpImageUrls: List<String>
          val _tmp: String?
          if (_stmt.isNull(_columnIndexOfImageUrls)) {
            _tmp = null
          } else {
            _tmp = _stmt.getText(_columnIndexOfImageUrls)
          }
          val _tmp_1: List<String>? = AppDatabase.Converters.toStringList(_tmp)
          if (_tmp_1 == null) {
            error("Expected NON-NULL 'kotlin.collections.List<kotlin.String>', but it was NULL.")
          } else {
            _tmpImageUrls = _tmp_1
          }
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpCondition: String?
          if (_stmt.isNull(_columnIndexOfCondition)) {
            _tmpCondition = null
          } else {
            _tmpCondition = _stmt.getText(_columnIndexOfCondition)
          }
          val _tmpHarvestDate: Long?
          if (_stmt.isNull(_columnIndexOfHarvestDate)) {
            _tmpHarvestDate = null
          } else {
            _tmpHarvestDate = _stmt.getLong(_columnIndexOfHarvestDate)
          }
          val _tmpExpiryDate: Long?
          if (_stmt.isNull(_columnIndexOfExpiryDate)) {
            _tmpExpiryDate = null
          } else {
            _tmpExpiryDate = _stmt.getLong(_columnIndexOfExpiryDate)
          }
          val _tmpBirthDate: Long?
          if (_stmt.isNull(_columnIndexOfBirthDate)) {
            _tmpBirthDate = null
          } else {
            _tmpBirthDate = _stmt.getLong(_columnIndexOfBirthDate)
          }
          val _tmpVaccinationRecordsJson: String?
          if (_stmt.isNull(_columnIndexOfVaccinationRecordsJson)) {
            _tmpVaccinationRecordsJson = null
          } else {
            _tmpVaccinationRecordsJson = _stmt.getText(_columnIndexOfVaccinationRecordsJson)
          }
          val _tmpWeightGrams: Double?
          if (_stmt.isNull(_columnIndexOfWeightGrams)) {
            _tmpWeightGrams = null
          } else {
            _tmpWeightGrams = _stmt.getDouble(_columnIndexOfWeightGrams)
          }
          val _tmpHeightCm: Double?
          if (_stmt.isNull(_columnIndexOfHeightCm)) {
            _tmpHeightCm = null
          } else {
            _tmpHeightCm = _stmt.getDouble(_columnIndexOfHeightCm)
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
          val _tmpBreed: String?
          if (_stmt.isNull(_columnIndexOfBreed)) {
            _tmpBreed = null
          } else {
            _tmpBreed = _stmt.getText(_columnIndexOfBreed)
          }
          val _tmpRaisingPurpose: String?
          if (_stmt.isNull(_columnIndexOfRaisingPurpose)) {
            _tmpRaisingPurpose = null
          } else {
            _tmpRaisingPurpose = _stmt.getText(_columnIndexOfRaisingPurpose)
          }
          val _tmpHealthStatus: String?
          if (_stmt.isNull(_columnIndexOfHealthStatus)) {
            _tmpHealthStatus = null
          } else {
            _tmpHealthStatus = _stmt.getText(_columnIndexOfHealthStatus)
          }
          val _tmpBirdCode: String?
          if (_stmt.isNull(_columnIndexOfBirdCode)) {
            _tmpBirdCode = null
          } else {
            _tmpBirdCode = _stmt.getText(_columnIndexOfBirdCode)
          }
          val _tmpColorTag: String?
          if (_stmt.isNull(_columnIndexOfColorTag)) {
            _tmpColorTag = null
          } else {
            _tmpColorTag = _stmt.getText(_columnIndexOfColorTag)
          }
          val _tmpFamilyTreeId: String?
          if (_stmt.isNull(_columnIndexOfFamilyTreeId)) {
            _tmpFamilyTreeId = null
          } else {
            _tmpFamilyTreeId = _stmt.getText(_columnIndexOfFamilyTreeId)
          }
          val _tmpSourceAssetId: String?
          if (_stmt.isNull(_columnIndexOfSourceAssetId)) {
            _tmpSourceAssetId = null
          } else {
            _tmpSourceAssetId = _stmt.getText(_columnIndexOfSourceAssetId)
          }
          val _tmpParentIdsJson: String?
          if (_stmt.isNull(_columnIndexOfParentIdsJson)) {
            _tmpParentIdsJson = null
          } else {
            _tmpParentIdsJson = _stmt.getText(_columnIndexOfParentIdsJson)
          }
          val _tmpBreedingStatus: String?
          if (_stmt.isNull(_columnIndexOfBreedingStatus)) {
            _tmpBreedingStatus = null
          } else {
            _tmpBreedingStatus = _stmt.getText(_columnIndexOfBreedingStatus)
          }
          val _tmpTransferHistoryJson: String?
          if (_stmt.isNull(_columnIndexOfTransferHistoryJson)) {
            _tmpTransferHistoryJson = null
          } else {
            _tmpTransferHistoryJson = _stmt.getText(_columnIndexOfTransferHistoryJson)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpLastModifiedAt: Long
          _tmpLastModifiedAt = _stmt.getLong(_columnIndexOfLastModifiedAt)
          val _tmpIsDeleted: Boolean
          val _tmp_2: Int
          _tmp_2 = _stmt.getLong(_columnIndexOfIsDeleted).toInt()
          _tmpIsDeleted = _tmp_2 != 0
          val _tmpDeletedAt: Long?
          if (_stmt.isNull(_columnIndexOfDeletedAt)) {
            _tmpDeletedAt = null
          } else {
            _tmpDeletedAt = _stmt.getLong(_columnIndexOfDeletedAt)
          }
          val _tmpDirty: Boolean
          val _tmp_3: Int
          _tmp_3 = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp_3 != 0
          val _tmpStage: LifecycleStage?
          val _tmp_4: String?
          if (_stmt.isNull(_columnIndexOfStage)) {
            _tmp_4 = null
          } else {
            _tmp_4 = _stmt.getText(_columnIndexOfStage)
          }
          _tmpStage = AppDatabase.Converters.toLifecycleStage(_tmp_4)
          val _tmpLifecycleStatus: String?
          if (_stmt.isNull(_columnIndexOfLifecycleStatus)) {
            _tmpLifecycleStatus = null
          } else {
            _tmpLifecycleStatus = _stmt.getText(_columnIndexOfLifecycleStatus)
          }
          val _tmpParentMaleId: String?
          if (_stmt.isNull(_columnIndexOfParentMaleId)) {
            _tmpParentMaleId = null
          } else {
            _tmpParentMaleId = _stmt.getText(_columnIndexOfParentMaleId)
          }
          val _tmpParentFemaleId: String?
          if (_stmt.isNull(_columnIndexOfParentFemaleId)) {
            _tmpParentFemaleId = null
          } else {
            _tmpParentFemaleId = _stmt.getText(_columnIndexOfParentFemaleId)
          }
          val _tmpAgeWeeks: Int?
          if (_stmt.isNull(_columnIndexOfAgeWeeks)) {
            _tmpAgeWeeks = null
          } else {
            _tmpAgeWeeks = _stmt.getLong(_columnIndexOfAgeWeeks).toInt()
          }
          val _tmpLastStageTransitionAt: Long?
          if (_stmt.isNull(_columnIndexOfLastStageTransitionAt)) {
            _tmpLastStageTransitionAt = null
          } else {
            _tmpLastStageTransitionAt = _stmt.getLong(_columnIndexOfLastStageTransitionAt)
          }
          val _tmpBreederEligibleAt: Long?
          if (_stmt.isNull(_columnIndexOfBreederEligibleAt)) {
            _tmpBreederEligibleAt = null
          } else {
            _tmpBreederEligibleAt = _stmt.getLong(_columnIndexOfBreederEligibleAt)
          }
          val _tmpIsBatch: Boolean?
          val _tmp_5: Int?
          if (_stmt.isNull(_columnIndexOfIsBatch)) {
            _tmp_5 = null
          } else {
            _tmp_5 = _stmt.getLong(_columnIndexOfIsBatch).toInt()
          }
          _tmpIsBatch = _tmp_5?.let { it != 0 }
          val _tmpBatchId: String?
          if (_stmt.isNull(_columnIndexOfBatchId)) {
            _tmpBatchId = null
          } else {
            _tmpBatchId = _stmt.getText(_columnIndexOfBatchId)
          }
          val _tmpSplitAt: Long?
          if (_stmt.isNull(_columnIndexOfSplitAt)) {
            _tmpSplitAt = null
          } else {
            _tmpSplitAt = _stmt.getLong(_columnIndexOfSplitAt)
          }
          val _tmpSplitIntoIds: String?
          if (_stmt.isNull(_columnIndexOfSplitIntoIds)) {
            _tmpSplitIntoIds = null
          } else {
            _tmpSplitIntoIds = _stmt.getText(_columnIndexOfSplitIntoIds)
          }
          val _tmpDocumentUrls: List<String>
          val _tmp_6: String?
          if (_stmt.isNull(_columnIndexOfDocumentUrls)) {
            _tmp_6 = null
          } else {
            _tmp_6 = _stmt.getText(_columnIndexOfDocumentUrls)
          }
          val _tmp_7: List<String>? = AppDatabase.Converters.toStringList(_tmp_6)
          if (_tmp_7 == null) {
            error("Expected NON-NULL 'kotlin.collections.List<kotlin.String>', but it was NULL.")
          } else {
            _tmpDocumentUrls = _tmp_7
          }
          val _tmpQrCodeUrl: String?
          if (_stmt.isNull(_columnIndexOfQrCodeUrl)) {
            _tmpQrCodeUrl = null
          } else {
            _tmpQrCodeUrl = _stmt.getText(_columnIndexOfQrCodeUrl)
          }
          val _tmpCustomStatus: String?
          if (_stmt.isNull(_columnIndexOfCustomStatus)) {
            _tmpCustomStatus = null
          } else {
            _tmpCustomStatus = _stmt.getText(_columnIndexOfCustomStatus)
          }
          val _tmpDebug: Boolean
          val _tmp_8: Int
          _tmp_8 = _stmt.getLong(_columnIndexOfDebug).toInt()
          _tmpDebug = _tmp_8 != 0
          val _tmpDeliveryOptions: List<String>
          val _tmp_9: String?
          if (_stmt.isNull(_columnIndexOfDeliveryOptions)) {
            _tmp_9 = null
          } else {
            _tmp_9 = _stmt.getText(_columnIndexOfDeliveryOptions)
          }
          val _tmp_10: List<String>? = AppDatabase.Converters.toStringList(_tmp_9)
          if (_tmp_10 == null) {
            error("Expected NON-NULL 'kotlin.collections.List<kotlin.String>', but it was NULL.")
          } else {
            _tmpDeliveryOptions = _tmp_10
          }
          val _tmpDeliveryCost: Double?
          if (_stmt.isNull(_columnIndexOfDeliveryCost)) {
            _tmpDeliveryCost = null
          } else {
            _tmpDeliveryCost = _stmt.getDouble(_columnIndexOfDeliveryCost)
          }
          val _tmpLeadTimeDays: Int?
          if (_stmt.isNull(_columnIndexOfLeadTimeDays)) {
            _tmpLeadTimeDays = null
          } else {
            _tmpLeadTimeDays = _stmt.getLong(_columnIndexOfLeadTimeDays).toInt()
          }
          val _tmpMotherId: String?
          if (_stmt.isNull(_columnIndexOfMotherId)) {
            _tmpMotherId = null
          } else {
            _tmpMotherId = _stmt.getText(_columnIndexOfMotherId)
          }
          val _tmpIsBreedingUnit: Boolean
          val _tmp_11: Int
          _tmp_11 = _stmt.getLong(_columnIndexOfIsBreedingUnit).toInt()
          _tmpIsBreedingUnit = _tmp_11 != 0
          val _tmpEggsCollectedToday: Int
          _tmpEggsCollectedToday = _stmt.getLong(_columnIndexOfEggsCollectedToday).toInt()
          val _tmpLastEggLogDate: Long?
          if (_stmt.isNull(_columnIndexOfLastEggLogDate)) {
            _tmpLastEggLogDate = null
          } else {
            _tmpLastEggLogDate = _stmt.getLong(_columnIndexOfLastEggLogDate)
          }
          val _tmpReadyForSale: Boolean
          val _tmp_12: Int
          _tmp_12 = _stmt.getLong(_columnIndexOfReadyForSale).toInt()
          _tmpReadyForSale = _tmp_12 != 0
          val _tmpTargetWeight: Double?
          if (_stmt.isNull(_columnIndexOfTargetWeight)) {
            _tmpTargetWeight = null
          } else {
            _tmpTargetWeight = _stmt.getDouble(_columnIndexOfTargetWeight)
          }
          val _tmpIsShowcased: Boolean
          val _tmp_13: Int
          _tmp_13 = _stmt.getLong(_columnIndexOfIsShowcased).toInt()
          _tmpIsShowcased = _tmp_13 != 0
          val _tmpExternalVideoUrl: String?
          if (_stmt.isNull(_columnIndexOfExternalVideoUrl)) {
            _tmpExternalVideoUrl = null
          } else {
            _tmpExternalVideoUrl = _stmt.getText(_columnIndexOfExternalVideoUrl)
          }
          val _tmpRecordsLockedAt: Long?
          if (_stmt.isNull(_columnIndexOfRecordsLockedAt)) {
            _tmpRecordsLockedAt = null
          } else {
            _tmpRecordsLockedAt = _stmt.getLong(_columnIndexOfRecordsLockedAt)
          }
          val _tmpAutoLockAfterDays: Int
          _tmpAutoLockAfterDays = _stmt.getLong(_columnIndexOfAutoLockAfterDays).toInt()
          val _tmpLineageHistoryJson: String?
          if (_stmt.isNull(_columnIndexOfLineageHistoryJson)) {
            _tmpLineageHistoryJson = null
          } else {
            _tmpLineageHistoryJson = _stmt.getText(_columnIndexOfLineageHistoryJson)
          }
          val _tmpEditCount: Int
          _tmpEditCount = _stmt.getLong(_columnIndexOfEditCount).toInt()
          val _tmpLastEditedBy: String?
          if (_stmt.isNull(_columnIndexOfLastEditedBy)) {
            _tmpLastEditedBy = null
          } else {
            _tmpLastEditedBy = _stmt.getText(_columnIndexOfLastEditedBy)
          }
          val _tmpAdminFlagged: Boolean
          val _tmp_14: Int
          _tmp_14 = _stmt.getLong(_columnIndexOfAdminFlagged).toInt()
          _tmpAdminFlagged = _tmp_14 != 0
          val _tmpModerationNote: String?
          if (_stmt.isNull(_columnIndexOfModerationNote)) {
            _tmpModerationNote = null
          } else {
            _tmpModerationNote = _stmt.getText(_columnIndexOfModerationNote)
          }
          val _tmpMetadataJson: String
          _tmpMetadataJson = _stmt.getText(_columnIndexOfMetadataJson)
          _item =
              ProductEntity(_tmpProductId,_tmpSellerId,_tmpName,_tmpDescription,_tmpCategory,_tmpPrice,_tmpQuantity,_tmpUnit,_tmpLocation,_tmpLatitude,_tmpLongitude,_tmpImageUrls,_tmpStatus,_tmpCondition,_tmpHarvestDate,_tmpExpiryDate,_tmpBirthDate,_tmpVaccinationRecordsJson,_tmpWeightGrams,_tmpHeightCm,_tmpGender,_tmpColor,_tmpBreed,_tmpRaisingPurpose,_tmpHealthStatus,_tmpBirdCode,_tmpColorTag,_tmpFamilyTreeId,_tmpSourceAssetId,_tmpParentIdsJson,_tmpBreedingStatus,_tmpTransferHistoryJson,_tmpCreatedAt,_tmpUpdatedAt,_tmpLastModifiedAt,_tmpIsDeleted,_tmpDeletedAt,_tmpDirty,_tmpStage,_tmpLifecycleStatus,_tmpParentMaleId,_tmpParentFemaleId,_tmpAgeWeeks,_tmpLastStageTransitionAt,_tmpBreederEligibleAt,_tmpIsBatch,_tmpBatchId,_tmpSplitAt,_tmpSplitIntoIds,_tmpDocumentUrls,_tmpQrCodeUrl,_tmpCustomStatus,_tmpDebug,_tmpDeliveryOptions,_tmpDeliveryCost,_tmpLeadTimeDays,_tmpMotherId,_tmpIsBreedingUnit,_tmpEggsCollectedToday,_tmpLastEggLogDate,_tmpReadyForSale,_tmpTargetWeight,_tmpIsShowcased,_tmpExternalVideoUrl,_tmpRecordsLockedAt,_tmpAutoLockAfterDays,_tmpLineageHistoryJson,_tmpEditCount,_tmpLastEditedBy,_tmpAdminFlagged,_tmpModerationNote,_tmpMetadataJson)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getProductsInBoundingBox(
    minLat: Double,
    maxLat: Double,
    minLon: Double,
    maxLon: Double,
  ): Flow<List<ProductEntity>> {
    val _sql: String = """
        |
        |        SELECT * FROM products 
        |        WHERE latitude IS NOT NULL AND longitude IS NOT NULL
        |        AND latitude BETWEEN ? AND ?
        |        AND longitude BETWEEN ? AND ?
        |        AND status != 'private'
        |        AND isDeleted = 0
        |        ORDER BY updatedAt DESC
        |    
        """.trimMargin()
    return createFlow(__db, false, arrayOf("products")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindDouble(_argIndex, minLat)
        _argIndex = 2
        _stmt.bindDouble(_argIndex, maxLat)
        _argIndex = 3
        _stmt.bindDouble(_argIndex, minLon)
        _argIndex = 4
        _stmt.bindDouble(_argIndex, maxLon)
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfSellerId: Int = getColumnIndexOrThrow(_stmt, "sellerId")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfCategory: Int = getColumnIndexOrThrow(_stmt, "category")
        val _columnIndexOfPrice: Int = getColumnIndexOrThrow(_stmt, "price")
        val _columnIndexOfQuantity: Int = getColumnIndexOrThrow(_stmt, "quantity")
        val _columnIndexOfUnit: Int = getColumnIndexOrThrow(_stmt, "unit")
        val _columnIndexOfLocation: Int = getColumnIndexOrThrow(_stmt, "location")
        val _columnIndexOfLatitude: Int = getColumnIndexOrThrow(_stmt, "latitude")
        val _columnIndexOfLongitude: Int = getColumnIndexOrThrow(_stmt, "longitude")
        val _columnIndexOfImageUrls: Int = getColumnIndexOrThrow(_stmt, "imageUrls")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfCondition: Int = getColumnIndexOrThrow(_stmt, "condition")
        val _columnIndexOfHarvestDate: Int = getColumnIndexOrThrow(_stmt, "harvestDate")
        val _columnIndexOfExpiryDate: Int = getColumnIndexOrThrow(_stmt, "expiryDate")
        val _columnIndexOfBirthDate: Int = getColumnIndexOrThrow(_stmt, "birthDate")
        val _columnIndexOfVaccinationRecordsJson: Int = getColumnIndexOrThrow(_stmt,
            "vaccinationRecordsJson")
        val _columnIndexOfWeightGrams: Int = getColumnIndexOrThrow(_stmt, "weightGrams")
        val _columnIndexOfHeightCm: Int = getColumnIndexOrThrow(_stmt, "heightCm")
        val _columnIndexOfGender: Int = getColumnIndexOrThrow(_stmt, "gender")
        val _columnIndexOfColor: Int = getColumnIndexOrThrow(_stmt, "color")
        val _columnIndexOfBreed: Int = getColumnIndexOrThrow(_stmt, "breed")
        val _columnIndexOfRaisingPurpose: Int = getColumnIndexOrThrow(_stmt, "raisingPurpose")
        val _columnIndexOfHealthStatus: Int = getColumnIndexOrThrow(_stmt, "healthStatus")
        val _columnIndexOfBirdCode: Int = getColumnIndexOrThrow(_stmt, "birdCode")
        val _columnIndexOfColorTag: Int = getColumnIndexOrThrow(_stmt, "colorTag")
        val _columnIndexOfFamilyTreeId: Int = getColumnIndexOrThrow(_stmt, "familyTreeId")
        val _columnIndexOfSourceAssetId: Int = getColumnIndexOrThrow(_stmt, "sourceAssetId")
        val _columnIndexOfParentIdsJson: Int = getColumnIndexOrThrow(_stmt, "parentIdsJson")
        val _columnIndexOfBreedingStatus: Int = getColumnIndexOrThrow(_stmt, "breedingStatus")
        val _columnIndexOfTransferHistoryJson: Int = getColumnIndexOrThrow(_stmt,
            "transferHistoryJson")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfLastModifiedAt: Int = getColumnIndexOrThrow(_stmt, "lastModifiedAt")
        val _columnIndexOfIsDeleted: Int = getColumnIndexOrThrow(_stmt, "isDeleted")
        val _columnIndexOfDeletedAt: Int = getColumnIndexOrThrow(_stmt, "deletedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfStage: Int = getColumnIndexOrThrow(_stmt, "stage")
        val _columnIndexOfLifecycleStatus: Int = getColumnIndexOrThrow(_stmt, "lifecycleStatus")
        val _columnIndexOfParentMaleId: Int = getColumnIndexOrThrow(_stmt, "parentMaleId")
        val _columnIndexOfParentFemaleId: Int = getColumnIndexOrThrow(_stmt, "parentFemaleId")
        val _columnIndexOfAgeWeeks: Int = getColumnIndexOrThrow(_stmt, "ageWeeks")
        val _columnIndexOfLastStageTransitionAt: Int = getColumnIndexOrThrow(_stmt,
            "lastStageTransitionAt")
        val _columnIndexOfBreederEligibleAt: Int = getColumnIndexOrThrow(_stmt, "breederEligibleAt")
        val _columnIndexOfIsBatch: Int = getColumnIndexOrThrow(_stmt, "isBatch")
        val _columnIndexOfBatchId: Int = getColumnIndexOrThrow(_stmt, "batchId")
        val _columnIndexOfSplitAt: Int = getColumnIndexOrThrow(_stmt, "splitAt")
        val _columnIndexOfSplitIntoIds: Int = getColumnIndexOrThrow(_stmt, "splitIntoIds")
        val _columnIndexOfDocumentUrls: Int = getColumnIndexOrThrow(_stmt, "documentUrls")
        val _columnIndexOfQrCodeUrl: Int = getColumnIndexOrThrow(_stmt, "qrCodeUrl")
        val _columnIndexOfCustomStatus: Int = getColumnIndexOrThrow(_stmt, "customStatus")
        val _columnIndexOfDebug: Int = getColumnIndexOrThrow(_stmt, "debug")
        val _columnIndexOfDeliveryOptions: Int = getColumnIndexOrThrow(_stmt, "deliveryOptions")
        val _columnIndexOfDeliveryCost: Int = getColumnIndexOrThrow(_stmt, "deliveryCost")
        val _columnIndexOfLeadTimeDays: Int = getColumnIndexOrThrow(_stmt, "leadTimeDays")
        val _columnIndexOfMotherId: Int = getColumnIndexOrThrow(_stmt, "motherId")
        val _columnIndexOfIsBreedingUnit: Int = getColumnIndexOrThrow(_stmt, "isBreedingUnit")
        val _columnIndexOfEggsCollectedToday: Int = getColumnIndexOrThrow(_stmt,
            "eggsCollectedToday")
        val _columnIndexOfLastEggLogDate: Int = getColumnIndexOrThrow(_stmt, "lastEggLogDate")
        val _columnIndexOfReadyForSale: Int = getColumnIndexOrThrow(_stmt, "readyForSale")
        val _columnIndexOfTargetWeight: Int = getColumnIndexOrThrow(_stmt, "targetWeight")
        val _columnIndexOfIsShowcased: Int = getColumnIndexOrThrow(_stmt, "isShowcased")
        val _columnIndexOfExternalVideoUrl: Int = getColumnIndexOrThrow(_stmt, "externalVideoUrl")
        val _columnIndexOfRecordsLockedAt: Int = getColumnIndexOrThrow(_stmt, "recordsLockedAt")
        val _columnIndexOfAutoLockAfterDays: Int = getColumnIndexOrThrow(_stmt, "autoLockAfterDays")
        val _columnIndexOfLineageHistoryJson: Int = getColumnIndexOrThrow(_stmt,
            "lineageHistoryJson")
        val _columnIndexOfEditCount: Int = getColumnIndexOrThrow(_stmt, "editCount")
        val _columnIndexOfLastEditedBy: Int = getColumnIndexOrThrow(_stmt, "lastEditedBy")
        val _columnIndexOfAdminFlagged: Int = getColumnIndexOrThrow(_stmt, "adminFlagged")
        val _columnIndexOfModerationNote: Int = getColumnIndexOrThrow(_stmt, "moderationNote")
        val _columnIndexOfMetadataJson: Int = getColumnIndexOrThrow(_stmt, "metadataJson")
        val _result: MutableList<ProductEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: ProductEntity
          val _tmpProductId: String
          _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          val _tmpSellerId: String
          _tmpSellerId = _stmt.getText(_columnIndexOfSellerId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpDescription: String
          _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          val _tmpCategory: String
          _tmpCategory = _stmt.getText(_columnIndexOfCategory)
          val _tmpPrice: Double
          _tmpPrice = _stmt.getDouble(_columnIndexOfPrice)
          val _tmpQuantity: Double
          _tmpQuantity = _stmt.getDouble(_columnIndexOfQuantity)
          val _tmpUnit: String
          _tmpUnit = _stmt.getText(_columnIndexOfUnit)
          val _tmpLocation: String
          _tmpLocation = _stmt.getText(_columnIndexOfLocation)
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
          val _tmpImageUrls: List<String>
          val _tmp: String?
          if (_stmt.isNull(_columnIndexOfImageUrls)) {
            _tmp = null
          } else {
            _tmp = _stmt.getText(_columnIndexOfImageUrls)
          }
          val _tmp_1: List<String>? = AppDatabase.Converters.toStringList(_tmp)
          if (_tmp_1 == null) {
            error("Expected NON-NULL 'kotlin.collections.List<kotlin.String>', but it was NULL.")
          } else {
            _tmpImageUrls = _tmp_1
          }
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpCondition: String?
          if (_stmt.isNull(_columnIndexOfCondition)) {
            _tmpCondition = null
          } else {
            _tmpCondition = _stmt.getText(_columnIndexOfCondition)
          }
          val _tmpHarvestDate: Long?
          if (_stmt.isNull(_columnIndexOfHarvestDate)) {
            _tmpHarvestDate = null
          } else {
            _tmpHarvestDate = _stmt.getLong(_columnIndexOfHarvestDate)
          }
          val _tmpExpiryDate: Long?
          if (_stmt.isNull(_columnIndexOfExpiryDate)) {
            _tmpExpiryDate = null
          } else {
            _tmpExpiryDate = _stmt.getLong(_columnIndexOfExpiryDate)
          }
          val _tmpBirthDate: Long?
          if (_stmt.isNull(_columnIndexOfBirthDate)) {
            _tmpBirthDate = null
          } else {
            _tmpBirthDate = _stmt.getLong(_columnIndexOfBirthDate)
          }
          val _tmpVaccinationRecordsJson: String?
          if (_stmt.isNull(_columnIndexOfVaccinationRecordsJson)) {
            _tmpVaccinationRecordsJson = null
          } else {
            _tmpVaccinationRecordsJson = _stmt.getText(_columnIndexOfVaccinationRecordsJson)
          }
          val _tmpWeightGrams: Double?
          if (_stmt.isNull(_columnIndexOfWeightGrams)) {
            _tmpWeightGrams = null
          } else {
            _tmpWeightGrams = _stmt.getDouble(_columnIndexOfWeightGrams)
          }
          val _tmpHeightCm: Double?
          if (_stmt.isNull(_columnIndexOfHeightCm)) {
            _tmpHeightCm = null
          } else {
            _tmpHeightCm = _stmt.getDouble(_columnIndexOfHeightCm)
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
          val _tmpBreed: String?
          if (_stmt.isNull(_columnIndexOfBreed)) {
            _tmpBreed = null
          } else {
            _tmpBreed = _stmt.getText(_columnIndexOfBreed)
          }
          val _tmpRaisingPurpose: String?
          if (_stmt.isNull(_columnIndexOfRaisingPurpose)) {
            _tmpRaisingPurpose = null
          } else {
            _tmpRaisingPurpose = _stmt.getText(_columnIndexOfRaisingPurpose)
          }
          val _tmpHealthStatus: String?
          if (_stmt.isNull(_columnIndexOfHealthStatus)) {
            _tmpHealthStatus = null
          } else {
            _tmpHealthStatus = _stmt.getText(_columnIndexOfHealthStatus)
          }
          val _tmpBirdCode: String?
          if (_stmt.isNull(_columnIndexOfBirdCode)) {
            _tmpBirdCode = null
          } else {
            _tmpBirdCode = _stmt.getText(_columnIndexOfBirdCode)
          }
          val _tmpColorTag: String?
          if (_stmt.isNull(_columnIndexOfColorTag)) {
            _tmpColorTag = null
          } else {
            _tmpColorTag = _stmt.getText(_columnIndexOfColorTag)
          }
          val _tmpFamilyTreeId: String?
          if (_stmt.isNull(_columnIndexOfFamilyTreeId)) {
            _tmpFamilyTreeId = null
          } else {
            _tmpFamilyTreeId = _stmt.getText(_columnIndexOfFamilyTreeId)
          }
          val _tmpSourceAssetId: String?
          if (_stmt.isNull(_columnIndexOfSourceAssetId)) {
            _tmpSourceAssetId = null
          } else {
            _tmpSourceAssetId = _stmt.getText(_columnIndexOfSourceAssetId)
          }
          val _tmpParentIdsJson: String?
          if (_stmt.isNull(_columnIndexOfParentIdsJson)) {
            _tmpParentIdsJson = null
          } else {
            _tmpParentIdsJson = _stmt.getText(_columnIndexOfParentIdsJson)
          }
          val _tmpBreedingStatus: String?
          if (_stmt.isNull(_columnIndexOfBreedingStatus)) {
            _tmpBreedingStatus = null
          } else {
            _tmpBreedingStatus = _stmt.getText(_columnIndexOfBreedingStatus)
          }
          val _tmpTransferHistoryJson: String?
          if (_stmt.isNull(_columnIndexOfTransferHistoryJson)) {
            _tmpTransferHistoryJson = null
          } else {
            _tmpTransferHistoryJson = _stmt.getText(_columnIndexOfTransferHistoryJson)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpLastModifiedAt: Long
          _tmpLastModifiedAt = _stmt.getLong(_columnIndexOfLastModifiedAt)
          val _tmpIsDeleted: Boolean
          val _tmp_2: Int
          _tmp_2 = _stmt.getLong(_columnIndexOfIsDeleted).toInt()
          _tmpIsDeleted = _tmp_2 != 0
          val _tmpDeletedAt: Long?
          if (_stmt.isNull(_columnIndexOfDeletedAt)) {
            _tmpDeletedAt = null
          } else {
            _tmpDeletedAt = _stmt.getLong(_columnIndexOfDeletedAt)
          }
          val _tmpDirty: Boolean
          val _tmp_3: Int
          _tmp_3 = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp_3 != 0
          val _tmpStage: LifecycleStage?
          val _tmp_4: String?
          if (_stmt.isNull(_columnIndexOfStage)) {
            _tmp_4 = null
          } else {
            _tmp_4 = _stmt.getText(_columnIndexOfStage)
          }
          _tmpStage = AppDatabase.Converters.toLifecycleStage(_tmp_4)
          val _tmpLifecycleStatus: String?
          if (_stmt.isNull(_columnIndexOfLifecycleStatus)) {
            _tmpLifecycleStatus = null
          } else {
            _tmpLifecycleStatus = _stmt.getText(_columnIndexOfLifecycleStatus)
          }
          val _tmpParentMaleId: String?
          if (_stmt.isNull(_columnIndexOfParentMaleId)) {
            _tmpParentMaleId = null
          } else {
            _tmpParentMaleId = _stmt.getText(_columnIndexOfParentMaleId)
          }
          val _tmpParentFemaleId: String?
          if (_stmt.isNull(_columnIndexOfParentFemaleId)) {
            _tmpParentFemaleId = null
          } else {
            _tmpParentFemaleId = _stmt.getText(_columnIndexOfParentFemaleId)
          }
          val _tmpAgeWeeks: Int?
          if (_stmt.isNull(_columnIndexOfAgeWeeks)) {
            _tmpAgeWeeks = null
          } else {
            _tmpAgeWeeks = _stmt.getLong(_columnIndexOfAgeWeeks).toInt()
          }
          val _tmpLastStageTransitionAt: Long?
          if (_stmt.isNull(_columnIndexOfLastStageTransitionAt)) {
            _tmpLastStageTransitionAt = null
          } else {
            _tmpLastStageTransitionAt = _stmt.getLong(_columnIndexOfLastStageTransitionAt)
          }
          val _tmpBreederEligibleAt: Long?
          if (_stmt.isNull(_columnIndexOfBreederEligibleAt)) {
            _tmpBreederEligibleAt = null
          } else {
            _tmpBreederEligibleAt = _stmt.getLong(_columnIndexOfBreederEligibleAt)
          }
          val _tmpIsBatch: Boolean?
          val _tmp_5: Int?
          if (_stmt.isNull(_columnIndexOfIsBatch)) {
            _tmp_5 = null
          } else {
            _tmp_5 = _stmt.getLong(_columnIndexOfIsBatch).toInt()
          }
          _tmpIsBatch = _tmp_5?.let { it != 0 }
          val _tmpBatchId: String?
          if (_stmt.isNull(_columnIndexOfBatchId)) {
            _tmpBatchId = null
          } else {
            _tmpBatchId = _stmt.getText(_columnIndexOfBatchId)
          }
          val _tmpSplitAt: Long?
          if (_stmt.isNull(_columnIndexOfSplitAt)) {
            _tmpSplitAt = null
          } else {
            _tmpSplitAt = _stmt.getLong(_columnIndexOfSplitAt)
          }
          val _tmpSplitIntoIds: String?
          if (_stmt.isNull(_columnIndexOfSplitIntoIds)) {
            _tmpSplitIntoIds = null
          } else {
            _tmpSplitIntoIds = _stmt.getText(_columnIndexOfSplitIntoIds)
          }
          val _tmpDocumentUrls: List<String>
          val _tmp_6: String?
          if (_stmt.isNull(_columnIndexOfDocumentUrls)) {
            _tmp_6 = null
          } else {
            _tmp_6 = _stmt.getText(_columnIndexOfDocumentUrls)
          }
          val _tmp_7: List<String>? = AppDatabase.Converters.toStringList(_tmp_6)
          if (_tmp_7 == null) {
            error("Expected NON-NULL 'kotlin.collections.List<kotlin.String>', but it was NULL.")
          } else {
            _tmpDocumentUrls = _tmp_7
          }
          val _tmpQrCodeUrl: String?
          if (_stmt.isNull(_columnIndexOfQrCodeUrl)) {
            _tmpQrCodeUrl = null
          } else {
            _tmpQrCodeUrl = _stmt.getText(_columnIndexOfQrCodeUrl)
          }
          val _tmpCustomStatus: String?
          if (_stmt.isNull(_columnIndexOfCustomStatus)) {
            _tmpCustomStatus = null
          } else {
            _tmpCustomStatus = _stmt.getText(_columnIndexOfCustomStatus)
          }
          val _tmpDebug: Boolean
          val _tmp_8: Int
          _tmp_8 = _stmt.getLong(_columnIndexOfDebug).toInt()
          _tmpDebug = _tmp_8 != 0
          val _tmpDeliveryOptions: List<String>
          val _tmp_9: String?
          if (_stmt.isNull(_columnIndexOfDeliveryOptions)) {
            _tmp_9 = null
          } else {
            _tmp_9 = _stmt.getText(_columnIndexOfDeliveryOptions)
          }
          val _tmp_10: List<String>? = AppDatabase.Converters.toStringList(_tmp_9)
          if (_tmp_10 == null) {
            error("Expected NON-NULL 'kotlin.collections.List<kotlin.String>', but it was NULL.")
          } else {
            _tmpDeliveryOptions = _tmp_10
          }
          val _tmpDeliveryCost: Double?
          if (_stmt.isNull(_columnIndexOfDeliveryCost)) {
            _tmpDeliveryCost = null
          } else {
            _tmpDeliveryCost = _stmt.getDouble(_columnIndexOfDeliveryCost)
          }
          val _tmpLeadTimeDays: Int?
          if (_stmt.isNull(_columnIndexOfLeadTimeDays)) {
            _tmpLeadTimeDays = null
          } else {
            _tmpLeadTimeDays = _stmt.getLong(_columnIndexOfLeadTimeDays).toInt()
          }
          val _tmpMotherId: String?
          if (_stmt.isNull(_columnIndexOfMotherId)) {
            _tmpMotherId = null
          } else {
            _tmpMotherId = _stmt.getText(_columnIndexOfMotherId)
          }
          val _tmpIsBreedingUnit: Boolean
          val _tmp_11: Int
          _tmp_11 = _stmt.getLong(_columnIndexOfIsBreedingUnit).toInt()
          _tmpIsBreedingUnit = _tmp_11 != 0
          val _tmpEggsCollectedToday: Int
          _tmpEggsCollectedToday = _stmt.getLong(_columnIndexOfEggsCollectedToday).toInt()
          val _tmpLastEggLogDate: Long?
          if (_stmt.isNull(_columnIndexOfLastEggLogDate)) {
            _tmpLastEggLogDate = null
          } else {
            _tmpLastEggLogDate = _stmt.getLong(_columnIndexOfLastEggLogDate)
          }
          val _tmpReadyForSale: Boolean
          val _tmp_12: Int
          _tmp_12 = _stmt.getLong(_columnIndexOfReadyForSale).toInt()
          _tmpReadyForSale = _tmp_12 != 0
          val _tmpTargetWeight: Double?
          if (_stmt.isNull(_columnIndexOfTargetWeight)) {
            _tmpTargetWeight = null
          } else {
            _tmpTargetWeight = _stmt.getDouble(_columnIndexOfTargetWeight)
          }
          val _tmpIsShowcased: Boolean
          val _tmp_13: Int
          _tmp_13 = _stmt.getLong(_columnIndexOfIsShowcased).toInt()
          _tmpIsShowcased = _tmp_13 != 0
          val _tmpExternalVideoUrl: String?
          if (_stmt.isNull(_columnIndexOfExternalVideoUrl)) {
            _tmpExternalVideoUrl = null
          } else {
            _tmpExternalVideoUrl = _stmt.getText(_columnIndexOfExternalVideoUrl)
          }
          val _tmpRecordsLockedAt: Long?
          if (_stmt.isNull(_columnIndexOfRecordsLockedAt)) {
            _tmpRecordsLockedAt = null
          } else {
            _tmpRecordsLockedAt = _stmt.getLong(_columnIndexOfRecordsLockedAt)
          }
          val _tmpAutoLockAfterDays: Int
          _tmpAutoLockAfterDays = _stmt.getLong(_columnIndexOfAutoLockAfterDays).toInt()
          val _tmpLineageHistoryJson: String?
          if (_stmt.isNull(_columnIndexOfLineageHistoryJson)) {
            _tmpLineageHistoryJson = null
          } else {
            _tmpLineageHistoryJson = _stmt.getText(_columnIndexOfLineageHistoryJson)
          }
          val _tmpEditCount: Int
          _tmpEditCount = _stmt.getLong(_columnIndexOfEditCount).toInt()
          val _tmpLastEditedBy: String?
          if (_stmt.isNull(_columnIndexOfLastEditedBy)) {
            _tmpLastEditedBy = null
          } else {
            _tmpLastEditedBy = _stmt.getText(_columnIndexOfLastEditedBy)
          }
          val _tmpAdminFlagged: Boolean
          val _tmp_14: Int
          _tmp_14 = _stmt.getLong(_columnIndexOfAdminFlagged).toInt()
          _tmpAdminFlagged = _tmp_14 != 0
          val _tmpModerationNote: String?
          if (_stmt.isNull(_columnIndexOfModerationNote)) {
            _tmpModerationNote = null
          } else {
            _tmpModerationNote = _stmt.getText(_columnIndexOfModerationNote)
          }
          val _tmpMetadataJson: String
          _tmpMetadataJson = _stmt.getText(_columnIndexOfMetadataJson)
          _item =
              ProductEntity(_tmpProductId,_tmpSellerId,_tmpName,_tmpDescription,_tmpCategory,_tmpPrice,_tmpQuantity,_tmpUnit,_tmpLocation,_tmpLatitude,_tmpLongitude,_tmpImageUrls,_tmpStatus,_tmpCondition,_tmpHarvestDate,_tmpExpiryDate,_tmpBirthDate,_tmpVaccinationRecordsJson,_tmpWeightGrams,_tmpHeightCm,_tmpGender,_tmpColor,_tmpBreed,_tmpRaisingPurpose,_tmpHealthStatus,_tmpBirdCode,_tmpColorTag,_tmpFamilyTreeId,_tmpSourceAssetId,_tmpParentIdsJson,_tmpBreedingStatus,_tmpTransferHistoryJson,_tmpCreatedAt,_tmpUpdatedAt,_tmpLastModifiedAt,_tmpIsDeleted,_tmpDeletedAt,_tmpDirty,_tmpStage,_tmpLifecycleStatus,_tmpParentMaleId,_tmpParentFemaleId,_tmpAgeWeeks,_tmpLastStageTransitionAt,_tmpBreederEligibleAt,_tmpIsBatch,_tmpBatchId,_tmpSplitAt,_tmpSplitIntoIds,_tmpDocumentUrls,_tmpQrCodeUrl,_tmpCustomStatus,_tmpDebug,_tmpDeliveryOptions,_tmpDeliveryCost,_tmpLeadTimeDays,_tmpMotherId,_tmpIsBreedingUnit,_tmpEggsCollectedToday,_tmpLastEggLogDate,_tmpReadyForSale,_tmpTargetWeight,_tmpIsShowcased,_tmpExternalVideoUrl,_tmpRecordsLockedAt,_tmpAutoLockAfterDays,_tmpLineageHistoryJson,_tmpEditCount,_tmpLastEditedBy,_tmpAdminFlagged,_tmpModerationNote,_tmpMetadataJson)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getNearbySellers(
    minLat: Double,
    maxLat: Double,
    minLon: Double,
    maxLon: Double,
  ): List<String> {
    val _sql: String = """
        |
        |        SELECT DISTINCT sellerId FROM products 
        |        WHERE latitude IS NOT NULL AND longitude IS NOT NULL
        |        AND latitude BETWEEN ? AND ?
        |        AND longitude BETWEEN ? AND ?
        |        AND status != 'private'
        |        AND isDeleted = 0
        |    
        """.trimMargin()
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindDouble(_argIndex, minLat)
        _argIndex = 2
        _stmt.bindDouble(_argIndex, maxLat)
        _argIndex = 3
        _stmt.bindDouble(_argIndex, minLon)
        _argIndex = 4
        _stmt.bindDouble(_argIndex, maxLon)
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

  public override fun getProductsByPurpose(purpose: String): Flow<List<ProductEntity>> {
    val _sql: String = """
        |
        |        SELECT * FROM products 
        |        WHERE raisingPurpose = ?
        |        AND status != 'private'
        |        AND isDeleted = 0
        |        ORDER BY updatedAt DESC
        |    
        """.trimMargin()
    return createFlow(__db, false, arrayOf("products")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, purpose)
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfSellerId: Int = getColumnIndexOrThrow(_stmt, "sellerId")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfCategory: Int = getColumnIndexOrThrow(_stmt, "category")
        val _columnIndexOfPrice: Int = getColumnIndexOrThrow(_stmt, "price")
        val _columnIndexOfQuantity: Int = getColumnIndexOrThrow(_stmt, "quantity")
        val _columnIndexOfUnit: Int = getColumnIndexOrThrow(_stmt, "unit")
        val _columnIndexOfLocation: Int = getColumnIndexOrThrow(_stmt, "location")
        val _columnIndexOfLatitude: Int = getColumnIndexOrThrow(_stmt, "latitude")
        val _columnIndexOfLongitude: Int = getColumnIndexOrThrow(_stmt, "longitude")
        val _columnIndexOfImageUrls: Int = getColumnIndexOrThrow(_stmt, "imageUrls")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfCondition: Int = getColumnIndexOrThrow(_stmt, "condition")
        val _columnIndexOfHarvestDate: Int = getColumnIndexOrThrow(_stmt, "harvestDate")
        val _columnIndexOfExpiryDate: Int = getColumnIndexOrThrow(_stmt, "expiryDate")
        val _columnIndexOfBirthDate: Int = getColumnIndexOrThrow(_stmt, "birthDate")
        val _columnIndexOfVaccinationRecordsJson: Int = getColumnIndexOrThrow(_stmt,
            "vaccinationRecordsJson")
        val _columnIndexOfWeightGrams: Int = getColumnIndexOrThrow(_stmt, "weightGrams")
        val _columnIndexOfHeightCm: Int = getColumnIndexOrThrow(_stmt, "heightCm")
        val _columnIndexOfGender: Int = getColumnIndexOrThrow(_stmt, "gender")
        val _columnIndexOfColor: Int = getColumnIndexOrThrow(_stmt, "color")
        val _columnIndexOfBreed: Int = getColumnIndexOrThrow(_stmt, "breed")
        val _columnIndexOfRaisingPurpose: Int = getColumnIndexOrThrow(_stmt, "raisingPurpose")
        val _columnIndexOfHealthStatus: Int = getColumnIndexOrThrow(_stmt, "healthStatus")
        val _columnIndexOfBirdCode: Int = getColumnIndexOrThrow(_stmt, "birdCode")
        val _columnIndexOfColorTag: Int = getColumnIndexOrThrow(_stmt, "colorTag")
        val _columnIndexOfFamilyTreeId: Int = getColumnIndexOrThrow(_stmt, "familyTreeId")
        val _columnIndexOfSourceAssetId: Int = getColumnIndexOrThrow(_stmt, "sourceAssetId")
        val _columnIndexOfParentIdsJson: Int = getColumnIndexOrThrow(_stmt, "parentIdsJson")
        val _columnIndexOfBreedingStatus: Int = getColumnIndexOrThrow(_stmt, "breedingStatus")
        val _columnIndexOfTransferHistoryJson: Int = getColumnIndexOrThrow(_stmt,
            "transferHistoryJson")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfLastModifiedAt: Int = getColumnIndexOrThrow(_stmt, "lastModifiedAt")
        val _columnIndexOfIsDeleted: Int = getColumnIndexOrThrow(_stmt, "isDeleted")
        val _columnIndexOfDeletedAt: Int = getColumnIndexOrThrow(_stmt, "deletedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfStage: Int = getColumnIndexOrThrow(_stmt, "stage")
        val _columnIndexOfLifecycleStatus: Int = getColumnIndexOrThrow(_stmt, "lifecycleStatus")
        val _columnIndexOfParentMaleId: Int = getColumnIndexOrThrow(_stmt, "parentMaleId")
        val _columnIndexOfParentFemaleId: Int = getColumnIndexOrThrow(_stmt, "parentFemaleId")
        val _columnIndexOfAgeWeeks: Int = getColumnIndexOrThrow(_stmt, "ageWeeks")
        val _columnIndexOfLastStageTransitionAt: Int = getColumnIndexOrThrow(_stmt,
            "lastStageTransitionAt")
        val _columnIndexOfBreederEligibleAt: Int = getColumnIndexOrThrow(_stmt, "breederEligibleAt")
        val _columnIndexOfIsBatch: Int = getColumnIndexOrThrow(_stmt, "isBatch")
        val _columnIndexOfBatchId: Int = getColumnIndexOrThrow(_stmt, "batchId")
        val _columnIndexOfSplitAt: Int = getColumnIndexOrThrow(_stmt, "splitAt")
        val _columnIndexOfSplitIntoIds: Int = getColumnIndexOrThrow(_stmt, "splitIntoIds")
        val _columnIndexOfDocumentUrls: Int = getColumnIndexOrThrow(_stmt, "documentUrls")
        val _columnIndexOfQrCodeUrl: Int = getColumnIndexOrThrow(_stmt, "qrCodeUrl")
        val _columnIndexOfCustomStatus: Int = getColumnIndexOrThrow(_stmt, "customStatus")
        val _columnIndexOfDebug: Int = getColumnIndexOrThrow(_stmt, "debug")
        val _columnIndexOfDeliveryOptions: Int = getColumnIndexOrThrow(_stmt, "deliveryOptions")
        val _columnIndexOfDeliveryCost: Int = getColumnIndexOrThrow(_stmt, "deliveryCost")
        val _columnIndexOfLeadTimeDays: Int = getColumnIndexOrThrow(_stmt, "leadTimeDays")
        val _columnIndexOfMotherId: Int = getColumnIndexOrThrow(_stmt, "motherId")
        val _columnIndexOfIsBreedingUnit: Int = getColumnIndexOrThrow(_stmt, "isBreedingUnit")
        val _columnIndexOfEggsCollectedToday: Int = getColumnIndexOrThrow(_stmt,
            "eggsCollectedToday")
        val _columnIndexOfLastEggLogDate: Int = getColumnIndexOrThrow(_stmt, "lastEggLogDate")
        val _columnIndexOfReadyForSale: Int = getColumnIndexOrThrow(_stmt, "readyForSale")
        val _columnIndexOfTargetWeight: Int = getColumnIndexOrThrow(_stmt, "targetWeight")
        val _columnIndexOfIsShowcased: Int = getColumnIndexOrThrow(_stmt, "isShowcased")
        val _columnIndexOfExternalVideoUrl: Int = getColumnIndexOrThrow(_stmt, "externalVideoUrl")
        val _columnIndexOfRecordsLockedAt: Int = getColumnIndexOrThrow(_stmt, "recordsLockedAt")
        val _columnIndexOfAutoLockAfterDays: Int = getColumnIndexOrThrow(_stmt, "autoLockAfterDays")
        val _columnIndexOfLineageHistoryJson: Int = getColumnIndexOrThrow(_stmt,
            "lineageHistoryJson")
        val _columnIndexOfEditCount: Int = getColumnIndexOrThrow(_stmt, "editCount")
        val _columnIndexOfLastEditedBy: Int = getColumnIndexOrThrow(_stmt, "lastEditedBy")
        val _columnIndexOfAdminFlagged: Int = getColumnIndexOrThrow(_stmt, "adminFlagged")
        val _columnIndexOfModerationNote: Int = getColumnIndexOrThrow(_stmt, "moderationNote")
        val _columnIndexOfMetadataJson: Int = getColumnIndexOrThrow(_stmt, "metadataJson")
        val _result: MutableList<ProductEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: ProductEntity
          val _tmpProductId: String
          _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          val _tmpSellerId: String
          _tmpSellerId = _stmt.getText(_columnIndexOfSellerId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpDescription: String
          _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          val _tmpCategory: String
          _tmpCategory = _stmt.getText(_columnIndexOfCategory)
          val _tmpPrice: Double
          _tmpPrice = _stmt.getDouble(_columnIndexOfPrice)
          val _tmpQuantity: Double
          _tmpQuantity = _stmt.getDouble(_columnIndexOfQuantity)
          val _tmpUnit: String
          _tmpUnit = _stmt.getText(_columnIndexOfUnit)
          val _tmpLocation: String
          _tmpLocation = _stmt.getText(_columnIndexOfLocation)
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
          val _tmpImageUrls: List<String>
          val _tmp: String?
          if (_stmt.isNull(_columnIndexOfImageUrls)) {
            _tmp = null
          } else {
            _tmp = _stmt.getText(_columnIndexOfImageUrls)
          }
          val _tmp_1: List<String>? = AppDatabase.Converters.toStringList(_tmp)
          if (_tmp_1 == null) {
            error("Expected NON-NULL 'kotlin.collections.List<kotlin.String>', but it was NULL.")
          } else {
            _tmpImageUrls = _tmp_1
          }
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpCondition: String?
          if (_stmt.isNull(_columnIndexOfCondition)) {
            _tmpCondition = null
          } else {
            _tmpCondition = _stmt.getText(_columnIndexOfCondition)
          }
          val _tmpHarvestDate: Long?
          if (_stmt.isNull(_columnIndexOfHarvestDate)) {
            _tmpHarvestDate = null
          } else {
            _tmpHarvestDate = _stmt.getLong(_columnIndexOfHarvestDate)
          }
          val _tmpExpiryDate: Long?
          if (_stmt.isNull(_columnIndexOfExpiryDate)) {
            _tmpExpiryDate = null
          } else {
            _tmpExpiryDate = _stmt.getLong(_columnIndexOfExpiryDate)
          }
          val _tmpBirthDate: Long?
          if (_stmt.isNull(_columnIndexOfBirthDate)) {
            _tmpBirthDate = null
          } else {
            _tmpBirthDate = _stmt.getLong(_columnIndexOfBirthDate)
          }
          val _tmpVaccinationRecordsJson: String?
          if (_stmt.isNull(_columnIndexOfVaccinationRecordsJson)) {
            _tmpVaccinationRecordsJson = null
          } else {
            _tmpVaccinationRecordsJson = _stmt.getText(_columnIndexOfVaccinationRecordsJson)
          }
          val _tmpWeightGrams: Double?
          if (_stmt.isNull(_columnIndexOfWeightGrams)) {
            _tmpWeightGrams = null
          } else {
            _tmpWeightGrams = _stmt.getDouble(_columnIndexOfWeightGrams)
          }
          val _tmpHeightCm: Double?
          if (_stmt.isNull(_columnIndexOfHeightCm)) {
            _tmpHeightCm = null
          } else {
            _tmpHeightCm = _stmt.getDouble(_columnIndexOfHeightCm)
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
          val _tmpBreed: String?
          if (_stmt.isNull(_columnIndexOfBreed)) {
            _tmpBreed = null
          } else {
            _tmpBreed = _stmt.getText(_columnIndexOfBreed)
          }
          val _tmpRaisingPurpose: String?
          if (_stmt.isNull(_columnIndexOfRaisingPurpose)) {
            _tmpRaisingPurpose = null
          } else {
            _tmpRaisingPurpose = _stmt.getText(_columnIndexOfRaisingPurpose)
          }
          val _tmpHealthStatus: String?
          if (_stmt.isNull(_columnIndexOfHealthStatus)) {
            _tmpHealthStatus = null
          } else {
            _tmpHealthStatus = _stmt.getText(_columnIndexOfHealthStatus)
          }
          val _tmpBirdCode: String?
          if (_stmt.isNull(_columnIndexOfBirdCode)) {
            _tmpBirdCode = null
          } else {
            _tmpBirdCode = _stmt.getText(_columnIndexOfBirdCode)
          }
          val _tmpColorTag: String?
          if (_stmt.isNull(_columnIndexOfColorTag)) {
            _tmpColorTag = null
          } else {
            _tmpColorTag = _stmt.getText(_columnIndexOfColorTag)
          }
          val _tmpFamilyTreeId: String?
          if (_stmt.isNull(_columnIndexOfFamilyTreeId)) {
            _tmpFamilyTreeId = null
          } else {
            _tmpFamilyTreeId = _stmt.getText(_columnIndexOfFamilyTreeId)
          }
          val _tmpSourceAssetId: String?
          if (_stmt.isNull(_columnIndexOfSourceAssetId)) {
            _tmpSourceAssetId = null
          } else {
            _tmpSourceAssetId = _stmt.getText(_columnIndexOfSourceAssetId)
          }
          val _tmpParentIdsJson: String?
          if (_stmt.isNull(_columnIndexOfParentIdsJson)) {
            _tmpParentIdsJson = null
          } else {
            _tmpParentIdsJson = _stmt.getText(_columnIndexOfParentIdsJson)
          }
          val _tmpBreedingStatus: String?
          if (_stmt.isNull(_columnIndexOfBreedingStatus)) {
            _tmpBreedingStatus = null
          } else {
            _tmpBreedingStatus = _stmt.getText(_columnIndexOfBreedingStatus)
          }
          val _tmpTransferHistoryJson: String?
          if (_stmt.isNull(_columnIndexOfTransferHistoryJson)) {
            _tmpTransferHistoryJson = null
          } else {
            _tmpTransferHistoryJson = _stmt.getText(_columnIndexOfTransferHistoryJson)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpLastModifiedAt: Long
          _tmpLastModifiedAt = _stmt.getLong(_columnIndexOfLastModifiedAt)
          val _tmpIsDeleted: Boolean
          val _tmp_2: Int
          _tmp_2 = _stmt.getLong(_columnIndexOfIsDeleted).toInt()
          _tmpIsDeleted = _tmp_2 != 0
          val _tmpDeletedAt: Long?
          if (_stmt.isNull(_columnIndexOfDeletedAt)) {
            _tmpDeletedAt = null
          } else {
            _tmpDeletedAt = _stmt.getLong(_columnIndexOfDeletedAt)
          }
          val _tmpDirty: Boolean
          val _tmp_3: Int
          _tmp_3 = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp_3 != 0
          val _tmpStage: LifecycleStage?
          val _tmp_4: String?
          if (_stmt.isNull(_columnIndexOfStage)) {
            _tmp_4 = null
          } else {
            _tmp_4 = _stmt.getText(_columnIndexOfStage)
          }
          _tmpStage = AppDatabase.Converters.toLifecycleStage(_tmp_4)
          val _tmpLifecycleStatus: String?
          if (_stmt.isNull(_columnIndexOfLifecycleStatus)) {
            _tmpLifecycleStatus = null
          } else {
            _tmpLifecycleStatus = _stmt.getText(_columnIndexOfLifecycleStatus)
          }
          val _tmpParentMaleId: String?
          if (_stmt.isNull(_columnIndexOfParentMaleId)) {
            _tmpParentMaleId = null
          } else {
            _tmpParentMaleId = _stmt.getText(_columnIndexOfParentMaleId)
          }
          val _tmpParentFemaleId: String?
          if (_stmt.isNull(_columnIndexOfParentFemaleId)) {
            _tmpParentFemaleId = null
          } else {
            _tmpParentFemaleId = _stmt.getText(_columnIndexOfParentFemaleId)
          }
          val _tmpAgeWeeks: Int?
          if (_stmt.isNull(_columnIndexOfAgeWeeks)) {
            _tmpAgeWeeks = null
          } else {
            _tmpAgeWeeks = _stmt.getLong(_columnIndexOfAgeWeeks).toInt()
          }
          val _tmpLastStageTransitionAt: Long?
          if (_stmt.isNull(_columnIndexOfLastStageTransitionAt)) {
            _tmpLastStageTransitionAt = null
          } else {
            _tmpLastStageTransitionAt = _stmt.getLong(_columnIndexOfLastStageTransitionAt)
          }
          val _tmpBreederEligibleAt: Long?
          if (_stmt.isNull(_columnIndexOfBreederEligibleAt)) {
            _tmpBreederEligibleAt = null
          } else {
            _tmpBreederEligibleAt = _stmt.getLong(_columnIndexOfBreederEligibleAt)
          }
          val _tmpIsBatch: Boolean?
          val _tmp_5: Int?
          if (_stmt.isNull(_columnIndexOfIsBatch)) {
            _tmp_5 = null
          } else {
            _tmp_5 = _stmt.getLong(_columnIndexOfIsBatch).toInt()
          }
          _tmpIsBatch = _tmp_5?.let { it != 0 }
          val _tmpBatchId: String?
          if (_stmt.isNull(_columnIndexOfBatchId)) {
            _tmpBatchId = null
          } else {
            _tmpBatchId = _stmt.getText(_columnIndexOfBatchId)
          }
          val _tmpSplitAt: Long?
          if (_stmt.isNull(_columnIndexOfSplitAt)) {
            _tmpSplitAt = null
          } else {
            _tmpSplitAt = _stmt.getLong(_columnIndexOfSplitAt)
          }
          val _tmpSplitIntoIds: String?
          if (_stmt.isNull(_columnIndexOfSplitIntoIds)) {
            _tmpSplitIntoIds = null
          } else {
            _tmpSplitIntoIds = _stmt.getText(_columnIndexOfSplitIntoIds)
          }
          val _tmpDocumentUrls: List<String>
          val _tmp_6: String?
          if (_stmt.isNull(_columnIndexOfDocumentUrls)) {
            _tmp_6 = null
          } else {
            _tmp_6 = _stmt.getText(_columnIndexOfDocumentUrls)
          }
          val _tmp_7: List<String>? = AppDatabase.Converters.toStringList(_tmp_6)
          if (_tmp_7 == null) {
            error("Expected NON-NULL 'kotlin.collections.List<kotlin.String>', but it was NULL.")
          } else {
            _tmpDocumentUrls = _tmp_7
          }
          val _tmpQrCodeUrl: String?
          if (_stmt.isNull(_columnIndexOfQrCodeUrl)) {
            _tmpQrCodeUrl = null
          } else {
            _tmpQrCodeUrl = _stmt.getText(_columnIndexOfQrCodeUrl)
          }
          val _tmpCustomStatus: String?
          if (_stmt.isNull(_columnIndexOfCustomStatus)) {
            _tmpCustomStatus = null
          } else {
            _tmpCustomStatus = _stmt.getText(_columnIndexOfCustomStatus)
          }
          val _tmpDebug: Boolean
          val _tmp_8: Int
          _tmp_8 = _stmt.getLong(_columnIndexOfDebug).toInt()
          _tmpDebug = _tmp_8 != 0
          val _tmpDeliveryOptions: List<String>
          val _tmp_9: String?
          if (_stmt.isNull(_columnIndexOfDeliveryOptions)) {
            _tmp_9 = null
          } else {
            _tmp_9 = _stmt.getText(_columnIndexOfDeliveryOptions)
          }
          val _tmp_10: List<String>? = AppDatabase.Converters.toStringList(_tmp_9)
          if (_tmp_10 == null) {
            error("Expected NON-NULL 'kotlin.collections.List<kotlin.String>', but it was NULL.")
          } else {
            _tmpDeliveryOptions = _tmp_10
          }
          val _tmpDeliveryCost: Double?
          if (_stmt.isNull(_columnIndexOfDeliveryCost)) {
            _tmpDeliveryCost = null
          } else {
            _tmpDeliveryCost = _stmt.getDouble(_columnIndexOfDeliveryCost)
          }
          val _tmpLeadTimeDays: Int?
          if (_stmt.isNull(_columnIndexOfLeadTimeDays)) {
            _tmpLeadTimeDays = null
          } else {
            _tmpLeadTimeDays = _stmt.getLong(_columnIndexOfLeadTimeDays).toInt()
          }
          val _tmpMotherId: String?
          if (_stmt.isNull(_columnIndexOfMotherId)) {
            _tmpMotherId = null
          } else {
            _tmpMotherId = _stmt.getText(_columnIndexOfMotherId)
          }
          val _tmpIsBreedingUnit: Boolean
          val _tmp_11: Int
          _tmp_11 = _stmt.getLong(_columnIndexOfIsBreedingUnit).toInt()
          _tmpIsBreedingUnit = _tmp_11 != 0
          val _tmpEggsCollectedToday: Int
          _tmpEggsCollectedToday = _stmt.getLong(_columnIndexOfEggsCollectedToday).toInt()
          val _tmpLastEggLogDate: Long?
          if (_stmt.isNull(_columnIndexOfLastEggLogDate)) {
            _tmpLastEggLogDate = null
          } else {
            _tmpLastEggLogDate = _stmt.getLong(_columnIndexOfLastEggLogDate)
          }
          val _tmpReadyForSale: Boolean
          val _tmp_12: Int
          _tmp_12 = _stmt.getLong(_columnIndexOfReadyForSale).toInt()
          _tmpReadyForSale = _tmp_12 != 0
          val _tmpTargetWeight: Double?
          if (_stmt.isNull(_columnIndexOfTargetWeight)) {
            _tmpTargetWeight = null
          } else {
            _tmpTargetWeight = _stmt.getDouble(_columnIndexOfTargetWeight)
          }
          val _tmpIsShowcased: Boolean
          val _tmp_13: Int
          _tmp_13 = _stmt.getLong(_columnIndexOfIsShowcased).toInt()
          _tmpIsShowcased = _tmp_13 != 0
          val _tmpExternalVideoUrl: String?
          if (_stmt.isNull(_columnIndexOfExternalVideoUrl)) {
            _tmpExternalVideoUrl = null
          } else {
            _tmpExternalVideoUrl = _stmt.getText(_columnIndexOfExternalVideoUrl)
          }
          val _tmpRecordsLockedAt: Long?
          if (_stmt.isNull(_columnIndexOfRecordsLockedAt)) {
            _tmpRecordsLockedAt = null
          } else {
            _tmpRecordsLockedAt = _stmt.getLong(_columnIndexOfRecordsLockedAt)
          }
          val _tmpAutoLockAfterDays: Int
          _tmpAutoLockAfterDays = _stmt.getLong(_columnIndexOfAutoLockAfterDays).toInt()
          val _tmpLineageHistoryJson: String?
          if (_stmt.isNull(_columnIndexOfLineageHistoryJson)) {
            _tmpLineageHistoryJson = null
          } else {
            _tmpLineageHistoryJson = _stmt.getText(_columnIndexOfLineageHistoryJson)
          }
          val _tmpEditCount: Int
          _tmpEditCount = _stmt.getLong(_columnIndexOfEditCount).toInt()
          val _tmpLastEditedBy: String?
          if (_stmt.isNull(_columnIndexOfLastEditedBy)) {
            _tmpLastEditedBy = null
          } else {
            _tmpLastEditedBy = _stmt.getText(_columnIndexOfLastEditedBy)
          }
          val _tmpAdminFlagged: Boolean
          val _tmp_14: Int
          _tmp_14 = _stmt.getLong(_columnIndexOfAdminFlagged).toInt()
          _tmpAdminFlagged = _tmp_14 != 0
          val _tmpModerationNote: String?
          if (_stmt.isNull(_columnIndexOfModerationNote)) {
            _tmpModerationNote = null
          } else {
            _tmpModerationNote = _stmt.getText(_columnIndexOfModerationNote)
          }
          val _tmpMetadataJson: String
          _tmpMetadataJson = _stmt.getText(_columnIndexOfMetadataJson)
          _item =
              ProductEntity(_tmpProductId,_tmpSellerId,_tmpName,_tmpDescription,_tmpCategory,_tmpPrice,_tmpQuantity,_tmpUnit,_tmpLocation,_tmpLatitude,_tmpLongitude,_tmpImageUrls,_tmpStatus,_tmpCondition,_tmpHarvestDate,_tmpExpiryDate,_tmpBirthDate,_tmpVaccinationRecordsJson,_tmpWeightGrams,_tmpHeightCm,_tmpGender,_tmpColor,_tmpBreed,_tmpRaisingPurpose,_tmpHealthStatus,_tmpBirdCode,_tmpColorTag,_tmpFamilyTreeId,_tmpSourceAssetId,_tmpParentIdsJson,_tmpBreedingStatus,_tmpTransferHistoryJson,_tmpCreatedAt,_tmpUpdatedAt,_tmpLastModifiedAt,_tmpIsDeleted,_tmpDeletedAt,_tmpDirty,_tmpStage,_tmpLifecycleStatus,_tmpParentMaleId,_tmpParentFemaleId,_tmpAgeWeeks,_tmpLastStageTransitionAt,_tmpBreederEligibleAt,_tmpIsBatch,_tmpBatchId,_tmpSplitAt,_tmpSplitIntoIds,_tmpDocumentUrls,_tmpQrCodeUrl,_tmpCustomStatus,_tmpDebug,_tmpDeliveryOptions,_tmpDeliveryCost,_tmpLeadTimeDays,_tmpMotherId,_tmpIsBreedingUnit,_tmpEggsCollectedToday,_tmpLastEggLogDate,_tmpReadyForSale,_tmpTargetWeight,_tmpIsShowcased,_tmpExternalVideoUrl,_tmpRecordsLockedAt,_tmpAutoLockAfterDays,_tmpLineageHistoryJson,_tmpEditCount,_tmpLastEditedBy,_tmpAdminFlagged,_tmpModerationNote,_tmpMetadataJson)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getNearbyProductsByPurpose(
    purpose: String,
    minLat: Double,
    maxLat: Double,
    minLon: Double,
    maxLon: Double,
  ): Flow<List<ProductEntity>> {
    val _sql: String = """
        |
        |        SELECT * FROM products 
        |        WHERE raisingPurpose = ?
        |        AND latitude IS NOT NULL AND longitude IS NOT NULL
        |        AND latitude BETWEEN ? AND ?
        |        AND longitude BETWEEN ? AND ?
        |        AND status != 'private'
        |        AND isDeleted = 0
        |        ORDER BY updatedAt DESC
        |    
        """.trimMargin()
    return createFlow(__db, false, arrayOf("products")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, purpose)
        _argIndex = 2
        _stmt.bindDouble(_argIndex, minLat)
        _argIndex = 3
        _stmt.bindDouble(_argIndex, maxLat)
        _argIndex = 4
        _stmt.bindDouble(_argIndex, minLon)
        _argIndex = 5
        _stmt.bindDouble(_argIndex, maxLon)
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfSellerId: Int = getColumnIndexOrThrow(_stmt, "sellerId")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfCategory: Int = getColumnIndexOrThrow(_stmt, "category")
        val _columnIndexOfPrice: Int = getColumnIndexOrThrow(_stmt, "price")
        val _columnIndexOfQuantity: Int = getColumnIndexOrThrow(_stmt, "quantity")
        val _columnIndexOfUnit: Int = getColumnIndexOrThrow(_stmt, "unit")
        val _columnIndexOfLocation: Int = getColumnIndexOrThrow(_stmt, "location")
        val _columnIndexOfLatitude: Int = getColumnIndexOrThrow(_stmt, "latitude")
        val _columnIndexOfLongitude: Int = getColumnIndexOrThrow(_stmt, "longitude")
        val _columnIndexOfImageUrls: Int = getColumnIndexOrThrow(_stmt, "imageUrls")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfCondition: Int = getColumnIndexOrThrow(_stmt, "condition")
        val _columnIndexOfHarvestDate: Int = getColumnIndexOrThrow(_stmt, "harvestDate")
        val _columnIndexOfExpiryDate: Int = getColumnIndexOrThrow(_stmt, "expiryDate")
        val _columnIndexOfBirthDate: Int = getColumnIndexOrThrow(_stmt, "birthDate")
        val _columnIndexOfVaccinationRecordsJson: Int = getColumnIndexOrThrow(_stmt,
            "vaccinationRecordsJson")
        val _columnIndexOfWeightGrams: Int = getColumnIndexOrThrow(_stmt, "weightGrams")
        val _columnIndexOfHeightCm: Int = getColumnIndexOrThrow(_stmt, "heightCm")
        val _columnIndexOfGender: Int = getColumnIndexOrThrow(_stmt, "gender")
        val _columnIndexOfColor: Int = getColumnIndexOrThrow(_stmt, "color")
        val _columnIndexOfBreed: Int = getColumnIndexOrThrow(_stmt, "breed")
        val _columnIndexOfRaisingPurpose: Int = getColumnIndexOrThrow(_stmt, "raisingPurpose")
        val _columnIndexOfHealthStatus: Int = getColumnIndexOrThrow(_stmt, "healthStatus")
        val _columnIndexOfBirdCode: Int = getColumnIndexOrThrow(_stmt, "birdCode")
        val _columnIndexOfColorTag: Int = getColumnIndexOrThrow(_stmt, "colorTag")
        val _columnIndexOfFamilyTreeId: Int = getColumnIndexOrThrow(_stmt, "familyTreeId")
        val _columnIndexOfSourceAssetId: Int = getColumnIndexOrThrow(_stmt, "sourceAssetId")
        val _columnIndexOfParentIdsJson: Int = getColumnIndexOrThrow(_stmt, "parentIdsJson")
        val _columnIndexOfBreedingStatus: Int = getColumnIndexOrThrow(_stmt, "breedingStatus")
        val _columnIndexOfTransferHistoryJson: Int = getColumnIndexOrThrow(_stmt,
            "transferHistoryJson")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfLastModifiedAt: Int = getColumnIndexOrThrow(_stmt, "lastModifiedAt")
        val _columnIndexOfIsDeleted: Int = getColumnIndexOrThrow(_stmt, "isDeleted")
        val _columnIndexOfDeletedAt: Int = getColumnIndexOrThrow(_stmt, "deletedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfStage: Int = getColumnIndexOrThrow(_stmt, "stage")
        val _columnIndexOfLifecycleStatus: Int = getColumnIndexOrThrow(_stmt, "lifecycleStatus")
        val _columnIndexOfParentMaleId: Int = getColumnIndexOrThrow(_stmt, "parentMaleId")
        val _columnIndexOfParentFemaleId: Int = getColumnIndexOrThrow(_stmt, "parentFemaleId")
        val _columnIndexOfAgeWeeks: Int = getColumnIndexOrThrow(_stmt, "ageWeeks")
        val _columnIndexOfLastStageTransitionAt: Int = getColumnIndexOrThrow(_stmt,
            "lastStageTransitionAt")
        val _columnIndexOfBreederEligibleAt: Int = getColumnIndexOrThrow(_stmt, "breederEligibleAt")
        val _columnIndexOfIsBatch: Int = getColumnIndexOrThrow(_stmt, "isBatch")
        val _columnIndexOfBatchId: Int = getColumnIndexOrThrow(_stmt, "batchId")
        val _columnIndexOfSplitAt: Int = getColumnIndexOrThrow(_stmt, "splitAt")
        val _columnIndexOfSplitIntoIds: Int = getColumnIndexOrThrow(_stmt, "splitIntoIds")
        val _columnIndexOfDocumentUrls: Int = getColumnIndexOrThrow(_stmt, "documentUrls")
        val _columnIndexOfQrCodeUrl: Int = getColumnIndexOrThrow(_stmt, "qrCodeUrl")
        val _columnIndexOfCustomStatus: Int = getColumnIndexOrThrow(_stmt, "customStatus")
        val _columnIndexOfDebug: Int = getColumnIndexOrThrow(_stmt, "debug")
        val _columnIndexOfDeliveryOptions: Int = getColumnIndexOrThrow(_stmt, "deliveryOptions")
        val _columnIndexOfDeliveryCost: Int = getColumnIndexOrThrow(_stmt, "deliveryCost")
        val _columnIndexOfLeadTimeDays: Int = getColumnIndexOrThrow(_stmt, "leadTimeDays")
        val _columnIndexOfMotherId: Int = getColumnIndexOrThrow(_stmt, "motherId")
        val _columnIndexOfIsBreedingUnit: Int = getColumnIndexOrThrow(_stmt, "isBreedingUnit")
        val _columnIndexOfEggsCollectedToday: Int = getColumnIndexOrThrow(_stmt,
            "eggsCollectedToday")
        val _columnIndexOfLastEggLogDate: Int = getColumnIndexOrThrow(_stmt, "lastEggLogDate")
        val _columnIndexOfReadyForSale: Int = getColumnIndexOrThrow(_stmt, "readyForSale")
        val _columnIndexOfTargetWeight: Int = getColumnIndexOrThrow(_stmt, "targetWeight")
        val _columnIndexOfIsShowcased: Int = getColumnIndexOrThrow(_stmt, "isShowcased")
        val _columnIndexOfExternalVideoUrl: Int = getColumnIndexOrThrow(_stmt, "externalVideoUrl")
        val _columnIndexOfRecordsLockedAt: Int = getColumnIndexOrThrow(_stmt, "recordsLockedAt")
        val _columnIndexOfAutoLockAfterDays: Int = getColumnIndexOrThrow(_stmt, "autoLockAfterDays")
        val _columnIndexOfLineageHistoryJson: Int = getColumnIndexOrThrow(_stmt,
            "lineageHistoryJson")
        val _columnIndexOfEditCount: Int = getColumnIndexOrThrow(_stmt, "editCount")
        val _columnIndexOfLastEditedBy: Int = getColumnIndexOrThrow(_stmt, "lastEditedBy")
        val _columnIndexOfAdminFlagged: Int = getColumnIndexOrThrow(_stmt, "adminFlagged")
        val _columnIndexOfModerationNote: Int = getColumnIndexOrThrow(_stmt, "moderationNote")
        val _columnIndexOfMetadataJson: Int = getColumnIndexOrThrow(_stmt, "metadataJson")
        val _result: MutableList<ProductEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: ProductEntity
          val _tmpProductId: String
          _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          val _tmpSellerId: String
          _tmpSellerId = _stmt.getText(_columnIndexOfSellerId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpDescription: String
          _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          val _tmpCategory: String
          _tmpCategory = _stmt.getText(_columnIndexOfCategory)
          val _tmpPrice: Double
          _tmpPrice = _stmt.getDouble(_columnIndexOfPrice)
          val _tmpQuantity: Double
          _tmpQuantity = _stmt.getDouble(_columnIndexOfQuantity)
          val _tmpUnit: String
          _tmpUnit = _stmt.getText(_columnIndexOfUnit)
          val _tmpLocation: String
          _tmpLocation = _stmt.getText(_columnIndexOfLocation)
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
          val _tmpImageUrls: List<String>
          val _tmp: String?
          if (_stmt.isNull(_columnIndexOfImageUrls)) {
            _tmp = null
          } else {
            _tmp = _stmt.getText(_columnIndexOfImageUrls)
          }
          val _tmp_1: List<String>? = AppDatabase.Converters.toStringList(_tmp)
          if (_tmp_1 == null) {
            error("Expected NON-NULL 'kotlin.collections.List<kotlin.String>', but it was NULL.")
          } else {
            _tmpImageUrls = _tmp_1
          }
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpCondition: String?
          if (_stmt.isNull(_columnIndexOfCondition)) {
            _tmpCondition = null
          } else {
            _tmpCondition = _stmt.getText(_columnIndexOfCondition)
          }
          val _tmpHarvestDate: Long?
          if (_stmt.isNull(_columnIndexOfHarvestDate)) {
            _tmpHarvestDate = null
          } else {
            _tmpHarvestDate = _stmt.getLong(_columnIndexOfHarvestDate)
          }
          val _tmpExpiryDate: Long?
          if (_stmt.isNull(_columnIndexOfExpiryDate)) {
            _tmpExpiryDate = null
          } else {
            _tmpExpiryDate = _stmt.getLong(_columnIndexOfExpiryDate)
          }
          val _tmpBirthDate: Long?
          if (_stmt.isNull(_columnIndexOfBirthDate)) {
            _tmpBirthDate = null
          } else {
            _tmpBirthDate = _stmt.getLong(_columnIndexOfBirthDate)
          }
          val _tmpVaccinationRecordsJson: String?
          if (_stmt.isNull(_columnIndexOfVaccinationRecordsJson)) {
            _tmpVaccinationRecordsJson = null
          } else {
            _tmpVaccinationRecordsJson = _stmt.getText(_columnIndexOfVaccinationRecordsJson)
          }
          val _tmpWeightGrams: Double?
          if (_stmt.isNull(_columnIndexOfWeightGrams)) {
            _tmpWeightGrams = null
          } else {
            _tmpWeightGrams = _stmt.getDouble(_columnIndexOfWeightGrams)
          }
          val _tmpHeightCm: Double?
          if (_stmt.isNull(_columnIndexOfHeightCm)) {
            _tmpHeightCm = null
          } else {
            _tmpHeightCm = _stmt.getDouble(_columnIndexOfHeightCm)
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
          val _tmpBreed: String?
          if (_stmt.isNull(_columnIndexOfBreed)) {
            _tmpBreed = null
          } else {
            _tmpBreed = _stmt.getText(_columnIndexOfBreed)
          }
          val _tmpRaisingPurpose: String?
          if (_stmt.isNull(_columnIndexOfRaisingPurpose)) {
            _tmpRaisingPurpose = null
          } else {
            _tmpRaisingPurpose = _stmt.getText(_columnIndexOfRaisingPurpose)
          }
          val _tmpHealthStatus: String?
          if (_stmt.isNull(_columnIndexOfHealthStatus)) {
            _tmpHealthStatus = null
          } else {
            _tmpHealthStatus = _stmt.getText(_columnIndexOfHealthStatus)
          }
          val _tmpBirdCode: String?
          if (_stmt.isNull(_columnIndexOfBirdCode)) {
            _tmpBirdCode = null
          } else {
            _tmpBirdCode = _stmt.getText(_columnIndexOfBirdCode)
          }
          val _tmpColorTag: String?
          if (_stmt.isNull(_columnIndexOfColorTag)) {
            _tmpColorTag = null
          } else {
            _tmpColorTag = _stmt.getText(_columnIndexOfColorTag)
          }
          val _tmpFamilyTreeId: String?
          if (_stmt.isNull(_columnIndexOfFamilyTreeId)) {
            _tmpFamilyTreeId = null
          } else {
            _tmpFamilyTreeId = _stmt.getText(_columnIndexOfFamilyTreeId)
          }
          val _tmpSourceAssetId: String?
          if (_stmt.isNull(_columnIndexOfSourceAssetId)) {
            _tmpSourceAssetId = null
          } else {
            _tmpSourceAssetId = _stmt.getText(_columnIndexOfSourceAssetId)
          }
          val _tmpParentIdsJson: String?
          if (_stmt.isNull(_columnIndexOfParentIdsJson)) {
            _tmpParentIdsJson = null
          } else {
            _tmpParentIdsJson = _stmt.getText(_columnIndexOfParentIdsJson)
          }
          val _tmpBreedingStatus: String?
          if (_stmt.isNull(_columnIndexOfBreedingStatus)) {
            _tmpBreedingStatus = null
          } else {
            _tmpBreedingStatus = _stmt.getText(_columnIndexOfBreedingStatus)
          }
          val _tmpTransferHistoryJson: String?
          if (_stmt.isNull(_columnIndexOfTransferHistoryJson)) {
            _tmpTransferHistoryJson = null
          } else {
            _tmpTransferHistoryJson = _stmt.getText(_columnIndexOfTransferHistoryJson)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpLastModifiedAt: Long
          _tmpLastModifiedAt = _stmt.getLong(_columnIndexOfLastModifiedAt)
          val _tmpIsDeleted: Boolean
          val _tmp_2: Int
          _tmp_2 = _stmt.getLong(_columnIndexOfIsDeleted).toInt()
          _tmpIsDeleted = _tmp_2 != 0
          val _tmpDeletedAt: Long?
          if (_stmt.isNull(_columnIndexOfDeletedAt)) {
            _tmpDeletedAt = null
          } else {
            _tmpDeletedAt = _stmt.getLong(_columnIndexOfDeletedAt)
          }
          val _tmpDirty: Boolean
          val _tmp_3: Int
          _tmp_3 = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp_3 != 0
          val _tmpStage: LifecycleStage?
          val _tmp_4: String?
          if (_stmt.isNull(_columnIndexOfStage)) {
            _tmp_4 = null
          } else {
            _tmp_4 = _stmt.getText(_columnIndexOfStage)
          }
          _tmpStage = AppDatabase.Converters.toLifecycleStage(_tmp_4)
          val _tmpLifecycleStatus: String?
          if (_stmt.isNull(_columnIndexOfLifecycleStatus)) {
            _tmpLifecycleStatus = null
          } else {
            _tmpLifecycleStatus = _stmt.getText(_columnIndexOfLifecycleStatus)
          }
          val _tmpParentMaleId: String?
          if (_stmt.isNull(_columnIndexOfParentMaleId)) {
            _tmpParentMaleId = null
          } else {
            _tmpParentMaleId = _stmt.getText(_columnIndexOfParentMaleId)
          }
          val _tmpParentFemaleId: String?
          if (_stmt.isNull(_columnIndexOfParentFemaleId)) {
            _tmpParentFemaleId = null
          } else {
            _tmpParentFemaleId = _stmt.getText(_columnIndexOfParentFemaleId)
          }
          val _tmpAgeWeeks: Int?
          if (_stmt.isNull(_columnIndexOfAgeWeeks)) {
            _tmpAgeWeeks = null
          } else {
            _tmpAgeWeeks = _stmt.getLong(_columnIndexOfAgeWeeks).toInt()
          }
          val _tmpLastStageTransitionAt: Long?
          if (_stmt.isNull(_columnIndexOfLastStageTransitionAt)) {
            _tmpLastStageTransitionAt = null
          } else {
            _tmpLastStageTransitionAt = _stmt.getLong(_columnIndexOfLastStageTransitionAt)
          }
          val _tmpBreederEligibleAt: Long?
          if (_stmt.isNull(_columnIndexOfBreederEligibleAt)) {
            _tmpBreederEligibleAt = null
          } else {
            _tmpBreederEligibleAt = _stmt.getLong(_columnIndexOfBreederEligibleAt)
          }
          val _tmpIsBatch: Boolean?
          val _tmp_5: Int?
          if (_stmt.isNull(_columnIndexOfIsBatch)) {
            _tmp_5 = null
          } else {
            _tmp_5 = _stmt.getLong(_columnIndexOfIsBatch).toInt()
          }
          _tmpIsBatch = _tmp_5?.let { it != 0 }
          val _tmpBatchId: String?
          if (_stmt.isNull(_columnIndexOfBatchId)) {
            _tmpBatchId = null
          } else {
            _tmpBatchId = _stmt.getText(_columnIndexOfBatchId)
          }
          val _tmpSplitAt: Long?
          if (_stmt.isNull(_columnIndexOfSplitAt)) {
            _tmpSplitAt = null
          } else {
            _tmpSplitAt = _stmt.getLong(_columnIndexOfSplitAt)
          }
          val _tmpSplitIntoIds: String?
          if (_stmt.isNull(_columnIndexOfSplitIntoIds)) {
            _tmpSplitIntoIds = null
          } else {
            _tmpSplitIntoIds = _stmt.getText(_columnIndexOfSplitIntoIds)
          }
          val _tmpDocumentUrls: List<String>
          val _tmp_6: String?
          if (_stmt.isNull(_columnIndexOfDocumentUrls)) {
            _tmp_6 = null
          } else {
            _tmp_6 = _stmt.getText(_columnIndexOfDocumentUrls)
          }
          val _tmp_7: List<String>? = AppDatabase.Converters.toStringList(_tmp_6)
          if (_tmp_7 == null) {
            error("Expected NON-NULL 'kotlin.collections.List<kotlin.String>', but it was NULL.")
          } else {
            _tmpDocumentUrls = _tmp_7
          }
          val _tmpQrCodeUrl: String?
          if (_stmt.isNull(_columnIndexOfQrCodeUrl)) {
            _tmpQrCodeUrl = null
          } else {
            _tmpQrCodeUrl = _stmt.getText(_columnIndexOfQrCodeUrl)
          }
          val _tmpCustomStatus: String?
          if (_stmt.isNull(_columnIndexOfCustomStatus)) {
            _tmpCustomStatus = null
          } else {
            _tmpCustomStatus = _stmt.getText(_columnIndexOfCustomStatus)
          }
          val _tmpDebug: Boolean
          val _tmp_8: Int
          _tmp_8 = _stmt.getLong(_columnIndexOfDebug).toInt()
          _tmpDebug = _tmp_8 != 0
          val _tmpDeliveryOptions: List<String>
          val _tmp_9: String?
          if (_stmt.isNull(_columnIndexOfDeliveryOptions)) {
            _tmp_9 = null
          } else {
            _tmp_9 = _stmt.getText(_columnIndexOfDeliveryOptions)
          }
          val _tmp_10: List<String>? = AppDatabase.Converters.toStringList(_tmp_9)
          if (_tmp_10 == null) {
            error("Expected NON-NULL 'kotlin.collections.List<kotlin.String>', but it was NULL.")
          } else {
            _tmpDeliveryOptions = _tmp_10
          }
          val _tmpDeliveryCost: Double?
          if (_stmt.isNull(_columnIndexOfDeliveryCost)) {
            _tmpDeliveryCost = null
          } else {
            _tmpDeliveryCost = _stmt.getDouble(_columnIndexOfDeliveryCost)
          }
          val _tmpLeadTimeDays: Int?
          if (_stmt.isNull(_columnIndexOfLeadTimeDays)) {
            _tmpLeadTimeDays = null
          } else {
            _tmpLeadTimeDays = _stmt.getLong(_columnIndexOfLeadTimeDays).toInt()
          }
          val _tmpMotherId: String?
          if (_stmt.isNull(_columnIndexOfMotherId)) {
            _tmpMotherId = null
          } else {
            _tmpMotherId = _stmt.getText(_columnIndexOfMotherId)
          }
          val _tmpIsBreedingUnit: Boolean
          val _tmp_11: Int
          _tmp_11 = _stmt.getLong(_columnIndexOfIsBreedingUnit).toInt()
          _tmpIsBreedingUnit = _tmp_11 != 0
          val _tmpEggsCollectedToday: Int
          _tmpEggsCollectedToday = _stmt.getLong(_columnIndexOfEggsCollectedToday).toInt()
          val _tmpLastEggLogDate: Long?
          if (_stmt.isNull(_columnIndexOfLastEggLogDate)) {
            _tmpLastEggLogDate = null
          } else {
            _tmpLastEggLogDate = _stmt.getLong(_columnIndexOfLastEggLogDate)
          }
          val _tmpReadyForSale: Boolean
          val _tmp_12: Int
          _tmp_12 = _stmt.getLong(_columnIndexOfReadyForSale).toInt()
          _tmpReadyForSale = _tmp_12 != 0
          val _tmpTargetWeight: Double?
          if (_stmt.isNull(_columnIndexOfTargetWeight)) {
            _tmpTargetWeight = null
          } else {
            _tmpTargetWeight = _stmt.getDouble(_columnIndexOfTargetWeight)
          }
          val _tmpIsShowcased: Boolean
          val _tmp_13: Int
          _tmp_13 = _stmt.getLong(_columnIndexOfIsShowcased).toInt()
          _tmpIsShowcased = _tmp_13 != 0
          val _tmpExternalVideoUrl: String?
          if (_stmt.isNull(_columnIndexOfExternalVideoUrl)) {
            _tmpExternalVideoUrl = null
          } else {
            _tmpExternalVideoUrl = _stmt.getText(_columnIndexOfExternalVideoUrl)
          }
          val _tmpRecordsLockedAt: Long?
          if (_stmt.isNull(_columnIndexOfRecordsLockedAt)) {
            _tmpRecordsLockedAt = null
          } else {
            _tmpRecordsLockedAt = _stmt.getLong(_columnIndexOfRecordsLockedAt)
          }
          val _tmpAutoLockAfterDays: Int
          _tmpAutoLockAfterDays = _stmt.getLong(_columnIndexOfAutoLockAfterDays).toInt()
          val _tmpLineageHistoryJson: String?
          if (_stmt.isNull(_columnIndexOfLineageHistoryJson)) {
            _tmpLineageHistoryJson = null
          } else {
            _tmpLineageHistoryJson = _stmt.getText(_columnIndexOfLineageHistoryJson)
          }
          val _tmpEditCount: Int
          _tmpEditCount = _stmt.getLong(_columnIndexOfEditCount).toInt()
          val _tmpLastEditedBy: String?
          if (_stmt.isNull(_columnIndexOfLastEditedBy)) {
            _tmpLastEditedBy = null
          } else {
            _tmpLastEditedBy = _stmt.getText(_columnIndexOfLastEditedBy)
          }
          val _tmpAdminFlagged: Boolean
          val _tmp_14: Int
          _tmp_14 = _stmt.getLong(_columnIndexOfAdminFlagged).toInt()
          _tmpAdminFlagged = _tmp_14 != 0
          val _tmpModerationNote: String?
          if (_stmt.isNull(_columnIndexOfModerationNote)) {
            _tmpModerationNote = null
          } else {
            _tmpModerationNote = _stmt.getText(_columnIndexOfModerationNote)
          }
          val _tmpMetadataJson: String
          _tmpMetadataJson = _stmt.getText(_columnIndexOfMetadataJson)
          _item =
              ProductEntity(_tmpProductId,_tmpSellerId,_tmpName,_tmpDescription,_tmpCategory,_tmpPrice,_tmpQuantity,_tmpUnit,_tmpLocation,_tmpLatitude,_tmpLongitude,_tmpImageUrls,_tmpStatus,_tmpCondition,_tmpHarvestDate,_tmpExpiryDate,_tmpBirthDate,_tmpVaccinationRecordsJson,_tmpWeightGrams,_tmpHeightCm,_tmpGender,_tmpColor,_tmpBreed,_tmpRaisingPurpose,_tmpHealthStatus,_tmpBirdCode,_tmpColorTag,_tmpFamilyTreeId,_tmpSourceAssetId,_tmpParentIdsJson,_tmpBreedingStatus,_tmpTransferHistoryJson,_tmpCreatedAt,_tmpUpdatedAt,_tmpLastModifiedAt,_tmpIsDeleted,_tmpDeletedAt,_tmpDirty,_tmpStage,_tmpLifecycleStatus,_tmpParentMaleId,_tmpParentFemaleId,_tmpAgeWeeks,_tmpLastStageTransitionAt,_tmpBreederEligibleAt,_tmpIsBatch,_tmpBatchId,_tmpSplitAt,_tmpSplitIntoIds,_tmpDocumentUrls,_tmpQrCodeUrl,_tmpCustomStatus,_tmpDebug,_tmpDeliveryOptions,_tmpDeliveryCost,_tmpLeadTimeDays,_tmpMotherId,_tmpIsBreedingUnit,_tmpEggsCollectedToday,_tmpLastEggLogDate,_tmpReadyForSale,_tmpTargetWeight,_tmpIsShowcased,_tmpExternalVideoUrl,_tmpRecordsLockedAt,_tmpAutoLockAfterDays,_tmpLineageHistoryJson,_tmpEditCount,_tmpLastEditedBy,_tmpAdminFlagged,_tmpModerationNote,_tmpMetadataJson)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun deleteProductInternal(productId: String) {
    val _sql: String = "DELETE FROM products WHERE productId = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, productId)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun deleteProductFts(productId: String) {
    val _sql: String = "DELETE FROM products_fts WHERE productId = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, productId)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun deleteAllProductsInternal() {
    val _sql: String = "DELETE FROM products"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun deleteAllProductsFts() {
    val _sql: String = "DELETE FROM products_fts"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun purgeDeleted() {
    val _sql: String = "DELETE FROM products WHERE isDeleted = 1"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun purgeStaleMarketplace(thresholdMillis: Long) {
    val _sql: String =
        "DELETE FROM products WHERE updatedAt < ? AND isDeleted = 0 AND productId NOT IN (SELECT DISTINCT productId FROM product_tracking WHERE isDeleted = 0)"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, thresholdMillis)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun updateStage(
    productId: String,
    stage: LifecycleStage,
    transitionAt: Long,
    updatedAt: Long,
  ) {
    val _sql: String =
        "UPDATE products SET stage = ?, lastStageTransitionAt = ?, updatedAt = ?, dirty = 1 WHERE productId = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        val _tmp: String? = AppDatabase.Converters.fromLifecycleStage(stage)
        if (_tmp == null) {
          _stmt.bindNull(_argIndex)
        } else {
          _stmt.bindText(_argIndex, _tmp)
        }
        _argIndex = 2
        _stmt.bindLong(_argIndex, transitionAt)
        _argIndex = 3
        _stmt.bindLong(_argIndex, updatedAt)
        _argIndex = 4
        _stmt.bindText(_argIndex, productId)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun updateLifecycleStatus(
    productId: String,
    status: String,
    updatedAt: Long,
  ) {
    val _sql: String =
        "UPDATE products SET lifecycleStatus = ?, updatedAt = ?, dirty = 1 WHERE productId = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, status)
        _argIndex = 2
        _stmt.bindLong(_argIndex, updatedAt)
        _argIndex = 3
        _stmt.bindText(_argIndex, productId)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun updateAgeWeeks(productId: String, ageWeeks: Int) {
    val _sql: String = "UPDATE products SET ageWeeks = ? WHERE productId = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, ageWeeks.toLong())
        _argIndex = 2
        _stmt.bindText(_argIndex, productId)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun updateBreederEligibleAt(
    productId: String,
    eligibleAt: Long,
    updatedAt: Long,
  ) {
    val _sql: String =
        "UPDATE products SET breederEligibleAt = ?, updatedAt = ?, dirty = 1 WHERE productId = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, eligibleAt)
        _argIndex = 2
        _stmt.bindLong(_argIndex, updatedAt)
        _argIndex = 3
        _stmt.bindText(_argIndex, productId)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun updateSellerIdAndTouch(
    productId: String,
    newSellerId: String,
    updatedAt: Long,
  ) {
    val _sql: String =
        "UPDATE products SET sellerId = ?, updatedAt = ?, dirty = 1 WHERE productId = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, newSellerId)
        _argIndex = 2
        _stmt.bindLong(_argIndex, updatedAt)
        _argIndex = 3
        _stmt.bindText(_argIndex, productId)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun updateLifecycleFields(
    productId: String,
    stage: LifecycleStage?,
    lifecycleStatus: String?,
    ageWeeks: Int?,
    lastStageTransitionAt: Long?,
    updatedAt: Long,
  ) {
    val _sql: String = """
        |
        |        UPDATE products 
        |        SET stage = ?, 
        |            lifecycleStatus = ?, 
        |            ageWeeks = ?, 
        |            lastStageTransitionAt = ?, 
        |            updatedAt = ?, 
        |            dirty = 1 
        |        WHERE productId = ?
        |    
        """.trimMargin()
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        val _tmp: String? = AppDatabase.Converters.fromLifecycleStage(stage)
        if (_tmp == null) {
          _stmt.bindNull(_argIndex)
        } else {
          _stmt.bindText(_argIndex, _tmp)
        }
        _argIndex = 2
        if (lifecycleStatus == null) {
          _stmt.bindNull(_argIndex)
        } else {
          _stmt.bindText(_argIndex, lifecycleStatus)
        }
        _argIndex = 3
        if (ageWeeks == null) {
          _stmt.bindNull(_argIndex)
        } else {
          _stmt.bindLong(_argIndex, ageWeeks.toLong())
        }
        _argIndex = 4
        if (lastStageTransitionAt == null) {
          _stmt.bindNull(_argIndex)
        } else {
          _stmt.bindLong(_argIndex, lastStageTransitionAt)
        }
        _argIndex = 5
        _stmt.bindLong(_argIndex, updatedAt)
        _argIndex = 6
        _stmt.bindText(_argIndex, productId)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun updateMetadata(
    productId: String,
    metadataJson: String,
    updatedAt: Long,
  ) {
    val _sql: String =
        "UPDATE products SET metadataJson = ?, updatedAt = ?, dirty = 1 WHERE productId = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, metadataJson)
        _argIndex = 2
        _stmt.bindLong(_argIndex, updatedAt)
        _argIndex = 3
        _stmt.bindText(_argIndex, productId)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun updateQrCodeUrl(
    productId: String,
    url: String?,
    updatedAt: Long,
  ) {
    val _sql: String =
        "UPDATE products SET qrCodeUrl = ?, updatedAt = ?, dirty = 1 WHERE productId = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        if (url == null) {
          _stmt.bindNull(_argIndex)
        } else {
          _stmt.bindText(_argIndex, url)
        }
        _argIndex = 2
        _stmt.bindLong(_argIndex, updatedAt)
        _argIndex = 3
        _stmt.bindText(_argIndex, productId)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun decrementQuantity(
    productId: String,
    amount: Int,
    updatedAt: Long,
  ) {
    val _sql: String =
        "UPDATE products SET quantity = quantity - ?, updatedAt = ?, dirty = 1 WHERE productId = ? AND quantity >= ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, amount.toLong())
        _argIndex = 2
        _stmt.bindLong(_argIndex, updatedAt)
        _argIndex = 3
        _stmt.bindText(_argIndex, productId)
        _argIndex = 4
        _stmt.bindLong(_argIndex, amount.toLong())
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun lockRecords(
    productId: String,
    lockedAt: Long,
    updatedAt: Long,
  ) {
    val _sql: String =
        "UPDATE products SET recordsLockedAt = ?, updatedAt = ?, dirty = 1 WHERE productId = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, lockedAt)
        _argIndex = 2
        _stmt.bindLong(_argIndex, updatedAt)
        _argIndex = 3
        _stmt.bindText(_argIndex, productId)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun incrementEditCount(
    productId: String,
    editorId: String,
    updatedAt: Long,
  ) {
    val _sql: String =
        "UPDATE products SET editCount = editCount + 1, lastEditedBy = ?, updatedAt = ?, dirty = 1 WHERE productId = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, editorId)
        _argIndex = 2
        _stmt.bindLong(_argIndex, updatedAt)
        _argIndex = 3
        _stmt.bindText(_argIndex, productId)
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
