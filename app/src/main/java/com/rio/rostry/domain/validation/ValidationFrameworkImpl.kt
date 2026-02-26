package com.rio.rostry.domain.validation

import javax.inject.Inject
import javax.inject.Singleton

/**
 * Central validation framework providing text sanitization, file validation,
 * foreign key checks, and batch validation.
 * 
 * Requirements: 3.1, 3.4, 17.3, 17.4
 */
@Singleton
class ValidationFrameworkImpl @Inject constructor(
    private val entityValidator: EntityValidator
) : ValidationFramework {

    private val textInputValidator = TextInputValidator()

    override fun <T> validate(value: T, validator: InputValidator<T>): InputValidationResult {
        return validator.validate(value)
    }

    override fun sanitizeText(input: String): String {
        return textInputValidator.sanitize(input)
    }

    override fun validateEmail(email: String): InputValidationResult {
        return EmailValidator.validate(email)
    }

    override fun validatePhone(phone: String): InputValidationResult {
        return PhoneValidator.validate(phone)
    }

    override fun validateCoordinates(latitude: Double, longitude: Double): InputValidationResult {
        return CoordinateValidator.validate(latitude, longitude)
    }

    override fun validateDateRange(startDate: Long, endDate: Long): InputValidationResult {
        return DateRangeValidator.validate(startDate, endDate)
    }

    override fun <T> validateBatch(
        items: List<T>,
        validator: InputValidator<T>
    ): BatchValidationResult {
        val validIndices = mutableListOf<Int>()
        val invalidMap = mutableMapOf<Int, List<InputValidationError>>()

        items.forEachIndexed { index, item ->
            val result = validator.validate(item)
            when (result) {
                is InputValidationResult.Valid -> validIndices.add(index)
                is InputValidationResult.Invalid -> invalidMap[index] = result.errors
            }
        }

        return BatchValidationResult(valid = validIndices, invalid = invalidMap)
    }

    /**
     * Validates that a foreign key reference exists in the database.
     * 
     * Requirements: 3.4, 17.3
     */
    override suspend fun validateForeignKey(entityType: String, id: String): InputValidationResult {
        return when (entityType.lowercase()) {
            "user" -> entityValidator.validateUserExists(id)
            "product" -> entityValidator.validateProductExists(id)
            "order" -> entityValidator.validateOrderExists(id)
            else -> InputValidationResult.Invalid(
                listOf(
                    InputValidationError(
                        field = "entityType",
                        message = "Unknown entity type: $entityType",
                        code = "UNKNOWN_ENTITY_TYPE"
                    )
                )
            )
        }
    }

    /**
     * Validates multiple foreign key references in a batch.
     * 
     * Requirements: 17.3, 17.4
     */
    override suspend fun validateForeignKeysBatch(
        entityType: String,
        ids: List<String>
    ): BatchValidationResult {
        return when (entityType.lowercase()) {
            "user" -> entityValidator.validateUsersExist(ids)
            "product" -> entityValidator.validateProductsExist(ids)
            "order" -> entityValidator.validateOrdersExist(ids)
            else -> {
                val invalidMap = ids.indices.associateWith {
                    listOf(
                        InputValidationError(
                            field = "entityType",
                            message = "Unknown entity type: $entityType",
                            code = "UNKNOWN_ENTITY_TYPE"
                        )
                    )
                }
                BatchValidationResult(valid = emptyList(), invalid = invalidMap)
            }
        }
    }
}

/**
 * Validation framework interface.
 */
interface ValidationFramework {
    fun <T> validate(value: T, validator: InputValidator<T>): InputValidationResult
    fun sanitizeText(input: String): String
    fun validateEmail(email: String): InputValidationResult
    fun validatePhone(phone: String): InputValidationResult
    fun validateCoordinates(latitude: Double, longitude: Double): InputValidationResult
    fun validateDateRange(startDate: Long, endDate: Long): InputValidationResult
    fun <T> validateBatch(items: List<T>, validator: InputValidator<T>): BatchValidationResult
    
    /**
     * Validates that a foreign key reference exists in the database.
     * 
     * @param entityType The type of entity (e.g., "user", "product", "order")
     * @param id The entity ID to validate
     * @return ValidationResult indicating if the entity exists
     */
    suspend fun validateForeignKey(entityType: String, id: String): InputValidationResult
    
    /**
     * Validates multiple foreign key references in a batch.
     * 
     * @param entityType The type of entity (e.g., "user", "product", "order")
     * @param ids List of entity IDs to validate
     * @return BatchValidationResult with valid and invalid indices
     */
    suspend fun validateForeignKeysBatch(entityType: String, ids: List<String>): BatchValidationResult
}
