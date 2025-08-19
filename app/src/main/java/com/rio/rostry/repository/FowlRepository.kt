package com.rio.rostry.repository

import com.rio.rostry.data.models.Fowl
import com.rio.rostry.data.models.HealthRecord
import com.rio.rostry.utils.AppLogger
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.tasks.await

class FowlRepository(db: FirebaseFirestore? = null) {
    private val dbInstance = db ?: FirebaseFirestore.getInstance()
    private val fowlCollection = dbInstance.collection("fowls")
    private val logger = AppLogger.getInstance()

    suspend fun addFowl(fowl: Fowl): Result<String> {
        val traceName = "addFowl"
        logger.startPerformanceTrace(traceName)
        val startTime = System.currentTimeMillis()
        
        return try {
            logger.logInfo("Adding new fowl: ${fowl.name}")
            
            // Validate fowl data
            val validationError = validateFowlData(fowl)
            if (validationError != null) {
                logger.logWarning("Fowl validation failed: $validationError")
                return Result.failure(IllegalArgumentException(validationError))
            }
            
            val ref = fowlCollection.document()
            val fowlWithId = fowl.copy(
                fowlId = ref.id,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
            
            ref.set(fowlWithId).await()
            logger.logInfo("Successfully added fowl with ID: ${ref.id}")
            
            val duration = System.currentTimeMillis() - startTime
            logger.recordNetworkPerformance("addFowl", duration, true)
            logger.stopPerformanceTrace(traceName)
            
            Result.success(ref.id)
        } catch (e: FirebaseFirestoreException) {
            val duration = System.currentTimeMillis() - startTime
            logger.recordNetworkPerformance("addFowl", duration, false)
            logger.stopPerformanceTrace(traceName)
            logger.logError("Failed to add fowl to database: ${e.message}", e)
            Result.failure(DataException(DataErrorType.DATABASE_ERROR, "Failed to add fowl to database: ${e.message}"))
        } catch (e: Exception) {
            val duration = System.currentTimeMillis() - startTime
            logger.recordNetworkPerformance("addFowl", duration, false)
            logger.stopPerformanceTrace(traceName)
            logger.logError("Failed to add fowl: ${e.message}", e)
            Result.failure(DataException(DataErrorType.UNKNOWN_ERROR, "Failed to add fowl: ${e.message}"))
        }
    }

    suspend fun updateFowl(fowl: Fowl): Result<Unit> {
        val traceName = "updateFowl"
        logger.startPerformanceTrace(traceName)
        val startTime = System.currentTimeMillis()
        
        return try {
            logger.logInfo("Updating fowl: ${fowl.fowlId}")
            
            // Validate fowl data
            val validationError = validateFowlData(fowl)
            if (validationError != null) {
                logger.logWarning("Fowl validation failed: $validationError")
                return Result.failure(IllegalArgumentException(validationError))
            }
            
            // Check if fowl exists
            if (fowl.fowlId.isBlank()) {
                logger.logWarning("Fowl ID is required for update")
                return Result.failure(IllegalArgumentException("Fowl ID is required for update"))
            }
            
            fowlCollection.document(fowl.fowlId)
                .set(fowl.copy(updatedAt = System.currentTimeMillis()))
                .await()
            logger.logInfo("Successfully updated fowl with ID: ${fowl.fowlId}")
            
            val duration = System.currentTimeMillis() - startTime
            logger.recordNetworkPerformance("updateFowl", duration, true)
            logger.stopPerformanceTrace(traceName)
            
            Result.success(Unit)
        } catch (e: FirebaseFirestoreException) {
            val duration = System.currentTimeMillis() - startTime
            logger.recordNetworkPerformance("updateFowl", duration, false)
            logger.stopPerformanceTrace(traceName)
            logger.logError("Failed to update fowl in database: ${e.message}", e)
            Result.failure(DataException(DataErrorType.DATABASE_ERROR, "Failed to update fowl in database: ${e.message}"))
        } catch (e: Exception) {
            val duration = System.currentTimeMillis() - startTime
            logger.recordNetworkPerformance("updateFowl", duration, false)
            logger.stopPerformanceTrace(traceName)
            logger.logError("Failed to update fowl: ${e.message}", e)
            Result.failure(DataException(DataErrorType.UNKNOWN_ERROR, "Failed to update fowl: ${e.message}"))
        }
    }

    suspend fun deleteFowl(fowlId: String): Result<Unit> {
        val traceName = "deleteFowl"
        logger.startPerformanceTrace(traceName)
        val startTime = System.currentTimeMillis()
        
        return try {
            logger.logInfo("Deleting fowl: $fowlId")
            
            if (fowlId.isBlank()) {
                logger.logWarning("Fowl ID is required for deletion")
                return Result.failure(IllegalArgumentException("Fowl ID is required for deletion"))
            }
            
            fowlCollection.document(fowlId).delete().await()
            logger.logInfo("Successfully deleted fowl with ID: $fowlId")
            
            val duration = System.currentTimeMillis() - startTime
            logger.recordNetworkPerformance("deleteFowl", duration, true)
            logger.stopPerformanceTrace(traceName)
            
            Result.success(Unit)
        } catch (e: FirebaseFirestoreException) {
            val duration = System.currentTimeMillis() - startTime
            logger.recordNetworkPerformance("deleteFowl", duration, false)
            logger.stopPerformanceTrace(traceName)
            logger.logError("Failed to delete fowl from database: ${e.message}", e)
            Result.failure(DataException(DataErrorType.DATABASE_ERROR, "Failed to delete fowl from database: ${e.message}"))
        } catch (e: Exception) {
            val duration = System.currentTimeMillis() - startTime
            logger.recordNetworkPerformance("deleteFowl", duration, false)
            logger.stopPerformanceTrace(traceName)
            logger.logError("Failed to delete fowl: ${e.message}", e)
            Result.failure(DataException(DataErrorType.UNKNOWN_ERROR, "Failed to delete fowl: ${e.message}"))
        }
    }

    suspend fun getFowlsForUser(ownerUserId: String): Result<List<Fowl>> {
        val traceName = "getFowlsForUser"
        logger.startPerformanceTrace(traceName)
        val startTime = System.currentTimeMillis()
        
        return try {
            logger.logInfo("Fetching fowls for user: $ownerUserId")
            
            if (ownerUserId.isBlank()) {
                logger.logWarning("User ID is required")
                return Result.failure(IllegalArgumentException("User ID is required"))
            }
            
            val snapshot = fowlCollection
                .whereEqualTo("ownerUserId", ownerUserId)
                .get()
                .await()
            val fowls = snapshot.mapNotNull { document ->
                document.toObject(Fowl::class.java)
            }
            logger.logInfo("Successfully fetched ${fowls.size} fowls for user: $ownerUserId")
            
            val duration = System.currentTimeMillis() - startTime
            logger.recordNetworkPerformance("getFowlsForUser", duration, true)
            logger.stopPerformanceTrace(traceName)
            
            Result.success(fowls)
        } catch (e: FirebaseFirestoreException) {
            val duration = System.currentTimeMillis() - startTime
            logger.recordNetworkPerformance("getFowlsForUser", duration, false)
            logger.stopPerformanceTrace(traceName)
            logger.logError("Failed to fetch fowls from database: ${e.message}", e)
            Result.failure(DataException(DataErrorType.DATABASE_ERROR, "Failed to fetch fowls from database: ${e.message}"))
        } catch (e: Exception) {
            val duration = System.currentTimeMillis() - startTime
            logger.recordNetworkPerformance("getFowlsForUser", duration, false)
            logger.stopPerformanceTrace(traceName)
            logger.logError("Failed to fetch fowls: ${e.message}", e)
            Result.failure(DataException(DataErrorType.UNKNOWN_ERROR, "Failed to fetch fowls: ${e.message}"))
        }
    }

    suspend fun getFowl(fowlId: String): Result<Fowl> {
        val traceName = "getFowl"
        logger.startPerformanceTrace(traceName)
        val startTime = System.currentTimeMillis()
        
        return try {
            logger.logInfo("Fetching fowl: $fowlId")
            
            if (fowlId.isBlank()) {
                logger.logWarning("Fowl ID is required")
                return Result.failure(IllegalArgumentException("Fowl ID is required"))
            }
            
            val document = fowlCollection.document(fowlId).get().await()
            if (document.exists()) {
                val fowl = document.toObject(Fowl::class.java)
                if (fowl != null) {
                    logger.logInfo("Successfully fetched fowl: $fowlId")
                    val duration = System.currentTimeMillis() - startTime
                    logger.recordNetworkPerformance("getFowl", duration, true)
                    logger.stopPerformanceTrace(traceName)
                    Result.success(fowl)
                } else {
                    logger.logWarning("Fowl data is corrupted for ID: $fowlId")
                    val duration = System.currentTimeMillis() - startTime
                    logger.recordNetworkPerformance("getFowl", duration, false)
                    logger.stopPerformanceTrace(traceName)
                    Result.failure(DataException(DataErrorType.DATA_CORRUPTION, "Fowl data is corrupted"))
                }
            } else {
                logger.logWarning("Fowl not found: $fowlId")
                val duration = System.currentTimeMillis() - startTime
                logger.recordNetworkPerformance("getFowl", duration, false)
                logger.stopPerformanceTrace(traceName)
                Result.failure(DataException(DataErrorType.NOT_FOUND, "Fowl not found"))
            }
        } catch (e: FirebaseFirestoreException) {
            val duration = System.currentTimeMillis() - startTime
            logger.recordNetworkPerformance("getFowl", duration, false)
            logger.stopPerformanceTrace(traceName)
            logger.logError("Failed to fetch fowl from database: ${e.message}", e)
            Result.failure(DataException(DataErrorType.DATABASE_ERROR, "Failed to fetch fowl from database: ${e.message}"))
        } catch (e: Exception) {
            val duration = System.currentTimeMillis() - startTime
            logger.recordNetworkPerformance("getFowl", duration, false)
            logger.stopPerformanceTrace(traceName)
            logger.logError("Failed to fetch fowl: ${e.message}", e)
            Result.failure(DataException(DataErrorType.UNKNOWN_ERROR, "Failed to fetch fowl: ${e.message}"))
        }
    }

    suspend fun addHealthRecord(fowlId: String, healthRecord: HealthRecord): Result<Unit> {
        val traceName = "addHealthRecord"
        logger.startPerformanceTrace(traceName)
        val startTime = System.currentTimeMillis()
        
        return try {
            logger.logInfo("Adding health record to fowl: $fowlId")
            
            // Validate health record data
            val validationError = validateHealthRecordData(healthRecord)
            if (validationError != null) {
                logger.logWarning("Health record validation failed: $validationError")
                return Result.failure(IllegalArgumentException(validationError))
            }
            
            val fowlResult = getFowl(fowlId)
            if (fowlResult.isSuccess) {
                val fowl = fowlResult.getOrNull()!!
                val updatedHealthRecords = fowl.healthRecords + healthRecord
                val updatedFowl = fowl.copy(
                    healthRecords = updatedHealthRecords,
                    updatedAt = System.currentTimeMillis()
                )
                updateFowl(updatedFowl)
            } else {
                logger.logWarning("Failed to get fowl for adding health record: $fowlId")
                val duration = System.currentTimeMillis() - startTime
                logger.recordNetworkPerformance("addHealthRecord", duration, false)
                logger.stopPerformanceTrace(traceName)
                Result.failure(DataException(DataErrorType.NOT_FOUND, "Failed to get fowl: ${fowlResult.exceptionOrNull()?.message}"))
            }
        } catch (e: FirebaseFirestoreException) {
            val duration = System.currentTimeMillis() - startTime
            logger.recordNetworkPerformance("addHealthRecord", duration, false)
            logger.stopPerformanceTrace(traceName)
            logger.logError("Failed to add health record to database: ${e.message}", e)
            Result.failure(DataException(DataErrorType.DATABASE_ERROR, "Failed to add health record to database: ${e.message}"))
        } catch (e: Exception) {
            val duration = System.currentTimeMillis() - startTime
            logger.recordNetworkPerformance("addHealthRecord", duration, false)
            logger.stopPerformanceTrace(traceName)
            logger.logError("Failed to add health record: ${e.message}", e)
            Result.failure(DataException(DataErrorType.UNKNOWN_ERROR, "Failed to add health record: ${e.message}"))
        }
    }

    /**
     * Validate fowl data before saving
     */
    private fun validateFowlData(fowl: Fowl): String? {
        if (fowl.name.isBlank()) {
            return "Fowl name is required"
        }
        
        if (fowl.breed.isBlank()) {
            return "Fowl breed is required"
        }
        
        if (fowl.birthDate <= 0) {
            return "Valid birth date is required"
        }
        
        // Check if birth date is in the future
        if (fowl.birthDate > System.currentTimeMillis()) {
            return "Birth date cannot be in the future"
        }
        
        // Check if status is valid
        val validStatuses = listOf("growing", "breeder", "for sale", "sold", "deceased")
        if (fowl.status !in validStatuses) {
            return "Invalid status. Must be one of: ${validStatuses.joinToString(", ")}"
        }
        
        return null // No validation errors
    }
    
    /**
     * Validate health record data before saving
     */
    private fun validateHealthRecordData(healthRecord: HealthRecord): String? {
        if (healthRecord.type.isBlank()) {
            return "Health record type is required"
        }
        
        if (healthRecord.date <= 0) {
            return "Valid date is required for health record"
        }
        
        // Check if date is in the future
        if (healthRecord.date > System.currentTimeMillis()) {
            return "Health record date cannot be in the future"
        }
        
        return null // No validation errors
    }

    /**
     * Helper method to convert DocumentSnapshot to Fowl
     */
    private fun DocumentSnapshot.toObject(clazz: Class<Fowl>): Fowl? {
        return try {
            this.toObject(clazz)
        } catch (e: Exception) {
            null
        }
    }
}

// Data error types
enum class DataErrorType {
    NOT_FOUND,
    DATABASE_ERROR,
    DATA_CORRUPTION,
    UNKNOWN_ERROR
}

// Custom data exception
class DataException(
    val errorType: DataErrorType,
    message: String
) : Exception(message)