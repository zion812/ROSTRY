# Atomic Batch Operations Framework

This framework provides atomic batch operations with comprehensive validation, transaction management, and logging.

## Features

- **Atomicity**: All operations succeed or all fail (Requirements 17.1, 17.2, 17.8)
- **Pre-validation**: All items validated before transaction starts (Requirements 17.3, 17.4)
- **Batch Size Limit**: Maximum 100 items per batch (Requirement 17.7)
- **Comprehensive Logging**: Logs start, completion, and rollbacks (Requirements 17.5, 17.6)
- **Foreign Key Validation**: Validates entity references before operations (Requirements 17.3, 17.4)

## Components

### AtomicBatchProcessor

Core processor that executes batch operations atomically within database transactions.

### EntityValidator

Validates foreign key constraints before batch operations to prevent referential integrity violations.

### BatchOperationHelper

Convenience helper that combines batch processing with validation and transaction management.

## Usage Examples

### Basic Batch Operation

```kotlin
@Inject
lateinit var batchOperationHelper: BatchOperationHelper

suspend fun updateProductStatuses(productIds: List<String>) {
    val result = batchOperationHelper.executeBatch(
        items = productIds,
        operationName = "UpdateProductStatus",
        operation = { productId ->
            productDao.updateStatus(productId, "ACTIVE")
        }
    )
    
    when (result) {
        is BatchResult.Success -> {
            Log.i("Batch", "Updated ${result.processedCount} products")
        }
        is BatchResult.Failure -> {
            Log.e("Batch", "Failed: ${result.error}")
        }
        is BatchResult.ValidationFailed -> {
            Log.e("Batch", "Validation failed: ${result.message}")
        }
    }
}
```

### Batch Operation with Validation

```kotlin
class ProductValidator : InputValidator<Product> {
    override fun validate(value: Product): InputValidationResult {
        val errors = mutableListOf<InputValidationError>()
        
        if (value.name.isBlank()) {
            errors.add(InputValidationError("name", "Name is required", "NAME_REQUIRED"))
        }
        
        if (value.price <= 0) {
            errors.add(InputValidationError("price", "Price must be positive", "PRICE_INVALID"))
        }
        
        return if (errors.isEmpty()) {
            InputValidationResult.Valid
        } else {
            InputValidationResult.Invalid(errors)
        }
    }
}

suspend fun createProducts(products: List<Product>) {
    val result = batchOperationHelper.executeBatch(
        items = products,
        operationName = "CreateProducts",
        validator = ProductValidator(),
        operation = { product ->
            productDao.insert(product)
        }
    )
    
    // Handle result...
}
```

### Batch Operation with Foreign Key Validation

```kotlin
data class Transfer(
    val id: String,
    val productId: String,
    val recipientId: String,
    val timestamp: Long
)

suspend fun createTransfers(transfers: List<Transfer>) {
    // Validate recipient IDs exist before creating transfers
    val result = batchOperationHelper.executeBatchWithForeignKeyValidation(
        items = transfers,
        operationName = "CreateTransfers",
        entityType = "user",
        extractId = { it.recipientId },
        operation = { transfer ->
            transferDao.insert(transfer)
        }
    )
    
    when (result) {
        is BatchResult.Success -> {
            Log.i("Transfer", "Created ${result.processedCount} transfers")
        }
        is BatchResult.ValidationFailed -> {
            // Some recipient IDs don't exist
            Log.e("Transfer", "Invalid recipients: ${result.message}")
        }
        is BatchResult.Failure -> {
            // Transaction failed and was rolled back
            Log.e("Transfer", "Transaction failed: ${result.error}")
        }
    }
}
```

### Direct Use of AtomicBatchProcessor

For more control, you can use the AtomicBatchProcessor directly:

```kotlin
@Inject
lateinit var atomicBatchProcessor: AtomicBatchProcessor

@Inject
lateinit var database: AppDatabase

suspend fun customBatchOperation(items: List<MyItem>) {
    val result = atomicBatchProcessor.executeBatch(
        items = items,
        operationName = "CustomOperation",
        validator = MyItemValidator(),
        operation = { item ->
            // Your operation here
            myDao.process(item)
        },
        transactionBlock = { block ->
            database.runInTransaction {
                kotlinx.coroutines.runBlocking {
                    block()
                }
            }
        }
    )
    
    // Handle result...
}
```

## Batch Size Limits

The framework enforces a maximum batch size of 100 items to prevent transaction timeouts. If you need to process more items, split them into multiple batches:

```kotlin
suspend fun processLargeDataset(allItems: List<Item>) {
    val batchSize = 100
    val batches = allItems.chunked(batchSize)
    
    batches.forEachIndexed { index, batch ->
        val result = batchOperationHelper.executeBatch(
            items = batch,
            operationName = "ProcessBatch${index + 1}",
            operation = { item ->
                itemDao.process(item)
            }
        )
        
        if (result is BatchResult.Failure) {
            Log.e("Batch", "Batch ${index + 1} failed: ${result.error}")
            // Decide whether to continue or stop
            return
        }
    }
}
```

## Error Handling

All batch operations are logged comprehensively:

- **Start**: Logs operation name and item count
- **Validation**: Logs validation failures with details
- **Success**: Logs completion with processed count
- **Failure**: Logs rollback reason and failed item index
- **Error Handler**: All exceptions are reported to the centralized error handler

## Requirements Mapping

- **17.1**: All batch operations execute within transactions
- **17.2**: Any failure triggers rollback of all operations
- **17.3**: Validates all items before beginning transaction
- **17.4**: Returns validation errors without modifying database
- **17.5**: Logs start and completion with item counts
- **17.6**: Logs rollbacks with reason
- **17.7**: Limits batch sizes to 100 items
- **17.8**: Database state shows either all changes or no changes (atomicity)

## Testing

See the test files for comprehensive examples:
- Unit tests: `AtomicBatchProcessorTest.kt`
- Integration tests: `BatchOperationIntegrationTest.kt`
