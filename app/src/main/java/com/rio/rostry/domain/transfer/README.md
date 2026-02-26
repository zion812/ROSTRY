# Transfer System

## Overview

The Transfer System manages product transfers between users in the ROSTRY application. It provides comprehensive functionality for searching products, finding recipients, detecting conflicts, and completing transfers atomically with full audit trail and analytics tracking.

## Components

### Core Models (`TransferModels.kt`)

- **TransferSearchRequest**: Request for searching products with filters
- **TransferFilters**: Filters for product search (category, verification status, price range)
- **RecipientSearchRequest**: Request for searching recipient users
- **TransferConflict**: Represents a conflict detected during transfer
- **ConflictType**: Enum for conflict types (OWNERSHIP_MISMATCH, STATUS_MISMATCH, DATA_INCONSISTENCY)
- **TransferOperationResult**: Sealed class for transfer operation results
- **TransferProductResult**: Product search result for transfer
- **RecipientResult**: Recipient search result

### Transfer System Interface (`TransferSystem.kt`)

Main interface defining transfer operations:

- `searchProducts()`: Search for products owned by current user
- `searchRecipients()`: Search for recipient users (excluding current user)
- `initiateTransfer()`: Start a new transfer
- `detectConflicts()`: Detect conflicts in a pending transfer
- `resolveConflict()`: Resolve a specific conflict
- `completeTransfer()`: Complete transfer atomically

### Transfer System Implementation (`TransferSystemImpl.kt`)

Concrete implementation with the following features:

#### Product Search
- Filters products by ownership (current user only)
- Supports text search on name and description
- Filters by category, verification status, and price range
- Returns structured product results

#### Recipient Search
- Searches users by name, email, or username
- Automatically excludes current user from results
- Returns structured recipient results

#### Transfer Initiation
- Validates product and recipient existence
- Validates product eligibility using EntityValidator
- Checks for existing active transfers
- Creates transfer record with PENDING status
- Creates analytics record for tracking
- Creates audit log entry

#### Conflict Detection
- Detects ownership mismatches
- Detects status mismatches
- Detects data inconsistencies
- Returns detailed conflict information

#### Transfer Completion
- Validates product eligibility one final time
- Updates ownership atomically
- Updates transfer status to COMPLETED
- Calculates and records transfer duration
- Creates audit log entry
- Integrates with existing error handling

### Analytics (`TransferAnalyticsEntity.kt`, `TransferAnalyticsDao.kt`)

Tracks transfer metrics:

- Transfer participants (sender, recipient)
- Product transferred
- Timing information (initiated, completed, duration)
- Conflict information
- Supports queries for:
  - Transfers by user, product, time period
  - Average completion time
  - Most transferred products
  - Most active transfer users
  - Transfers with conflicts

### Validation (`EntityValidator.kt`)

Added `validateProductEligibility()` method that checks:
- Product verification status (must be verified)
- Product availability (not in active order - to be implemented)

## Integration

### Dependency Injection

The `TransferModule` provides the TransferSystem implementation via Hilt:

```kotlin
@Module
@InstallIn(SingletonComponent::class)
abstract class TransferModule {
    @Binds
    @Singleton
    abstract fun bindTransferSystem(impl: TransferSystemImpl): TransferSystem
}
```

### Usage Example

```kotlin
class TransferViewModel @Inject constructor(
    private val transferSystem: TransferSystem
) : ViewModel() {
    
    fun searchProducts(query: String) {
        viewModelScope.launch {
            val request = TransferSearchRequest(
                userId = currentUserId,
                query = query,
                filters = TransferFilters(
                    verificationStatus = VerificationStatus.VERIFIED
                )
            )
            
            val result = transferSystem.searchProducts(request)
            result.onSuccess { products ->
                // Update UI with products
            }
        }
    }
    
    fun initiateTransfer(productId: String, recipientId: String) {
        viewModelScope.launch {
            when (val result = transferSystem.initiateTransfer(productId, recipientId)) {
                is TransferOperationResult.Success -> {
                    // Transfer initiated successfully
                }
                is TransferOperationResult.Failure -> {
                    // Show error message
                }
                is TransferOperationResult.ConflictDetected -> {
                    // Show conflicts to user
                }
            }
        }
    }
}
```

## Requirements Satisfied

This implementation satisfies the following requirements from the spec:

- **Requirement 8.1**: Transfer search returns products owned by current user
- **Requirement 8.2**: Filter products by name, category, verification status
- **Requirement 8.3**: Search recipients by name, email, username
- **Requirement 8.4**: Exclude current user from recipient results
- **Requirement 8.5**: Display detailed conflict information
- **Requirement 8.6**: Allow conflict resolution by selecting preferred values
- **Requirement 8.7**: Update ownership atomically
- **Requirement 8.8**: Validate product eligibility before transfers
- **Requirement 23.1**: Track transfers by user, product, and time period
- **Requirement 23.2**: Calculate average transfer completion time

## Testing

Unit tests are provided in `TransferSystemTest.kt` covering:

- Product search with ownership filtering
- Product search with query filtering
- Product search with category filtering
- Recipient search excluding current user
- Recipient search with query filtering
- Transfer initiation with validation
- Transfer initiation failure cases
- Conflict detection

## Future Enhancements

1. **Notification Integration**: Send notifications to both parties when transfer is initiated and completed
2. **Order Status Check**: Complete the product eligibility validation to check if product is in an active order
3. **Conflict Resolution UI**: Build UI components for displaying and resolving conflicts
4. **Transfer Reports**: Generate CSV/PDF reports of transfer analytics
5. **Transfer Cancellation**: Add ability to cancel pending transfers
6. **Transfer History**: Add UI for viewing transfer history with filtering

## Database Schema

### transfer_analytics Table

```sql
CREATE TABLE transfer_analytics (
    id TEXT PRIMARY KEY,
    transferId TEXT NOT NULL,
    senderId TEXT NOT NULL,
    recipientId TEXT NOT NULL,
    productId TEXT NOT NULL,
    initiatedAt INTEGER NOT NULL,
    completedAt INTEGER,
    durationSeconds INTEGER,
    hadConflicts INTEGER DEFAULT 0,
    conflictCount INTEGER DEFAULT 0,
    createdAt INTEGER NOT NULL,
    FOREIGN KEY (transferId) REFERENCES transfers(transferId),
    FOREIGN KEY (senderId) REFERENCES users(userId),
    FOREIGN KEY (recipientId) REFERENCES users(userId),
    FOREIGN KEY (productId) REFERENCES products(productId)
);

CREATE INDEX idx_transfer_analytics_sender ON transfer_analytics(senderId);
CREATE INDEX idx_transfer_analytics_recipient ON transfer_analytics(recipientId);
CREATE INDEX idx_transfer_analytics_product ON transfer_analytics(productId);
CREATE INDEX idx_transfer_analytics_date ON transfer_analytics(initiatedAt);
```

## Error Handling

All operations integrate with the centralized ErrorHandler:

- Exceptions are caught and logged with operation context
- User-friendly error messages are generated
- Failed operations return Result.failure() with descriptive errors
- All errors are tracked for monitoring and debugging

## Performance Considerations

- Product search uses existing DAO methods with in-memory filtering
- Recipient search loads all users (consider pagination for large user bases)
- Transfer completion is atomic using Room transactions
- Analytics queries are indexed for fast retrieval
- Conflict detection is lightweight and only checks essential fields
