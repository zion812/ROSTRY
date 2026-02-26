# Data Integrity Management

This package provides comprehensive data integrity management for the ROSTRY application, including orphaned product handling, sync conflict resolution, and referential integrity checks.

## Components

### 1. OrphanedProductHandler

Handles detection and assignment of orphaned products (products with missing or invalid seller references).

**Features:**
- Detects products with non-existent sellers
- Detects products with empty sellerId
- Creates and manages a system account for orphaned products
- Assigns orphaned products to the system account
- Logs all assignments for audit trail

**Usage:**
```kotlin
@Inject lateinit var orphanedProductHandler: OrphanedProductHandler

// Detect and assign all orphaned products
val assignedCount = orphanedProductHandler.detectAndAssignOrphanedProducts()

// Check if a specific product is orphaned
val isOrphaned = orphanedProductHandler.isProductOrphaned(productId)
```

**Requirements:** 4.1

### 2. DataIntegrityManager

Manages sync conflict resolution and data consistency verification.

**Features:**
- Complete conflict resolution for all synchronized entities (Product, Order, Transfer, User)
- Multiple resolution strategies (SERVER_WINS, CLIENT_WINS, NEWEST_WINS, MERGE, ASK_USER)
- Data consistency verification after synchronization
- Repair workflows for detected inconsistencies
- Comprehensive logging for audit trail

**Conflict Resolution Strategies:**
- **SERVER_WINS**: Always use server version (safe default for critical data)
- **CLIENT_WINS**: Always use client version (for user preferences)
- **NEWEST_WINS**: Use version with latest timestamp (for user-generated content)
- **MERGE**: Attempt to merge changes field-by-field
- **ASK_USER**: Defer to user for resolution

**Usage:**
```kotlin
@Inject lateinit var dataIntegrityManager: DataIntegrityManager

// Resolve product conflict
val result = dataIntegrityManager.resolveProductConflict(
    local = localProduct,
    remote = remoteProduct,
    strategy = ConflictStrategy.NEWEST_WINS
)

when (result) {
    is ConflictResult.Resolved -> {
        // Use result.value.entity
    }
    is ConflictResult.NeedsUserInput -> {
        // Show UI for user to resolve
    }
    is ConflictResult.Error -> {
        // Handle error
    }
}

// Verify data consistency
val inconsistencies = dataIntegrityManager.verifyDataConsistency()

// Repair inconsistencies
val repairedCount = dataIntegrityManager.repairInconsistencies(inconsistencies)
```

**Consistency Checks:**
- Orphaned products (products with non-existent sellers)
- Orphaned orders (orders with non-existent buyers/sellers)
- Orphaned transfers (transfers with non-existent senders/recipients/products)
- Invalid product states (negative prices, negative quantities)
- Invalid order states (negative total amounts)

**Requirements:** 4.2, 4.4, 4.5

### 3. ReferentialIntegrityChecker

Validates foreign key constraints and implements cascade rules for entity deletion.

**Features:**
- Foreign key validation before batch operations
- Cascade rule implementation (CASCADE, SET_NULL, RESTRICT)
- Dependent entity handling according to cascade rules
- Comprehensive referential integrity checks

**Cascade Rules:**
- **CASCADE**: Delete all dependent entities when parent is deleted
- **SET_NULL**: Set foreign key to null in dependent entities when parent is deleted
- **RESTRICT**: Prevent deletion if there are dependent entities

**Usage:**
```kotlin
@Inject lateinit var referentialIntegrityChecker: ReferentialIntegrityChecker

// Validate foreign keys before batch operation
val validationResult = referentialIntegrityChecker.validateForeignKeysForBatch(
    entityType = "product",
    entityIds = listOf("prod1", "prod2", "prod3")
)

// Handle entity deletion with cascade rules
val deletionResult = referentialIntegrityChecker.handleEntityDeletion(
    entityType = "user",
    entityId = userId,
    cascadeRule = CascadeRule.RESTRICT
)

when (deletionResult) {
    is DeletionResult.Success -> {
        // Deletion successful
    }
    is DeletionResult.Restricted -> {
        // Cannot delete due to dependent entities
    }
    is DeletionResult.Error -> {
        // Handle error
    }
}

// Check all referential integrity
val violations = referentialIntegrityChecker.checkAllReferentialIntegrity()
```

**Requirements:** 4.3, 4.6, 4.7

## Data Models

### DataInconsistency
Represents a data inconsistency detected during verification.

```kotlin
data class DataInconsistency(
    val entityType: String,
    val entityId: String,
    val description: String,
    val severity: InconsistencySeverity
)

enum class InconsistencySeverity {
    LOW,      // Minor issues that don't affect functionality
    MEDIUM,   // Issues that may cause problems but aren't critical
    HIGH,     // Serious issues that should be fixed soon
    CRITICAL  // Critical issues that need immediate attention
}
```

### ReferentialIntegrityViolation
Represents a referential integrity violation.

```kotlin
data class ReferentialIntegrityViolation(
    val entityType: String,
    val entityId: String,
    val foreignKeyField: String,
    val foreignKeyValue: String,
    val referencedEntityType: String
)
```

### BatchForeignKeyValidationResult
Result of batch foreign key validation.

```kotlin
data class BatchForeignKeyValidationResult(
    val valid: List<Int>,        // Indices of valid entities
    val invalid: Map<Int, String> // Indices and error messages for invalid entities
)
```

## Integration

### With Sync System

The data integrity components integrate with the existing sync system:

```kotlin
// In SyncManager
@Inject lateinit var dataIntegrityManager: DataIntegrityManager

suspend fun syncProducts() {
    // ... sync logic ...
    
    // Resolve conflicts
    val conflicts = detectConflicts()
    for (conflict in conflicts) {
        val result = dataIntegrityManager.resolveProductConflict(
            local = conflict.local,
            remote = conflict.remote
        )
        // Handle result
    }
    
    // Verify consistency after sync
    val inconsistencies = dataIntegrityManager.verifyDataConsistency()
    if (inconsistencies.isNotEmpty()) {
        dataIntegrityManager.repairInconsistencies(inconsistencies)
    }
}
```

### With Batch Operations

The referential integrity checker integrates with batch operations:

```kotlin
// In AtomicBatchProcessor
@Inject lateinit var referentialIntegrityChecker: ReferentialIntegrityChecker

suspend fun processBatch(items: List<Item>) {
    // Validate foreign keys before batch
    val validationResult = referentialIntegrityChecker.validateForeignKeysForBatch(
        entityType = "product",
        entityIds = items.map { it.id }
    )
    
    if (validationResult.invalid.isNotEmpty()) {
        throw ValidationException("Foreign key validation failed")
    }
    
    // Process batch
    // ...
}
```

### With Background Workers

Run data integrity checks periodically:

```kotlin
class DataIntegrityWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val orphanedProductHandler: OrphanedProductHandler,
    private val dataIntegrityManager: DataIntegrityManager,
    private val referentialIntegrityChecker: ReferentialIntegrityChecker
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            // Handle orphaned products
            orphanedProductHandler.detectAndAssignOrphanedProducts()
            
            // Verify data consistency
            val inconsistencies = dataIntegrityManager.verifyDataConsistency()
            dataIntegrityManager.repairInconsistencies(inconsistencies)
            
            // Check referential integrity
            val violations = referentialIntegrityChecker.checkAllReferentialIntegrity()
            
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}
```

## Testing

Unit tests are provided for all components:

- `OrphanedProductHandlerTest`: Tests orphaned product detection and assignment
- `DataIntegrityManagerTest`: Tests conflict resolution and consistency verification (to be implemented)
- `ReferentialIntegrityCheckerTest`: Tests foreign key validation and cascade rules (to be implemented)

## Logging and Audit Trail

All data integrity operations are logged for audit purposes:

- Orphaned product assignments
- Conflict resolutions
- Data inconsistencies detected
- Referential integrity violations
- Repair operations

Logs include:
- Entity type and ID
- Operation performed
- Timestamp
- Additional context

## Performance Considerations

- Batch operations are optimized to minimize database queries
- Foreign key validation uses indexed queries
- Consistency checks can be run in background workers
- Large datasets are processed in chunks to avoid memory issues

## Future Enhancements

1. **Automated Repair**: Implement more sophisticated automated repair workflows
2. **User Notifications**: Notify users when their data has inconsistencies
3. **Admin Dashboard**: Provide UI for viewing and managing data integrity issues
4. **Metrics**: Track data integrity metrics over time
5. **Scheduled Checks**: Run integrity checks on a schedule (daily, weekly)
6. **Custom Rules**: Allow configuration of custom integrity rules
