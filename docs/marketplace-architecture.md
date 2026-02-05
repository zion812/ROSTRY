# Marketplace Architecture & Product Visibility

## Overview
The ROSTRY Marketplace serves two distinct purposes:
1.  **Public Marketplace**: A venue for buying and selling poultry products.
2.  **Private Farm Management**: A digital twin of the farmer's inventory for tracking growth and lineage.

To support these dual purposes without data duplication, the system employs a robust privacy and visibility model.

## Product Visibility Contexts

The `ProductEntity` serves as the single source of truth but behaves differently based on the viewing context.

| Context | Public Products | Private Products | Description |
|---------|-----------------|------------------|-------------|
| **Marketplace** | ✅ Visible | ❌ Hidden | Only items explicitly marked for sale. |
| **Digital Farm** | ✅ Visible | ✅ Visible | Complete operational view for the farmer. |
| **Inventory** | ✅ Visible | ✅ Visible | Stock management view. |

## Data Structure

### ProductEntity Extension
The `isPublic` flag restricts visibility in the public marketplace.

```kotlin
// Simplified Entity Structure
data class ProductEntity(
    val id: String,
    val farmerId: String,
    val category: String,
    val status: String, // AVAILABLE, SOLD, ARCHIVED
    val isPublic: Boolean = true, // Controls marketplace visibility
    // ... other fields
)
```

## Evidence-Based Order System

The Evidence-Based Order System ensures trust through immutable evidence collection and state-locked agreements. This system implements a 10-state workflow that provides transparency and accountability for all parties involved.

### Order States
1. **Enquiry**: Buyer expresses interest in a product
2. **Quote**: Seller responds with pricing and terms
3. **Agreement**: Both parties agree to terms
4. **Advance Payment**: Buyer pays initial amount
5. **Verification**: Payment proof uploaded and verified
6. **Dispatch**: Seller ships the product with shipping proof
7. **Delivery**: Product delivered to buyer
8. **Completion**: Buyer confirms receipt and satisfaction
9. **Dispute**: Raised if issues arise during any state
10. **Cancelled**: Order cancelled by either party or system

### Evidence-Based Verification
- **Payment Proof**: Verification with evidence upload
- **Shipping Proof**: Delivery confirmation with tracking
- **Delivery OTP**: Secure delivery confirmation
- **Immutable Evidence**: All evidence is cryptographically secured

### Order Tracking
Complete order lifecycle tracking with:
- Real-time status updates
- Evidence submission requirements
- Notification system for state changes
- Audit trail for all actions

### Payment Verification
- Multi-stage payment verification
- Evidence-based payment confirmation
- Secure payment gateway integration
- Escrow-like payment handling

### Review System
- Order-based reviews and ratings
- Seller performance tracking
- Product quality feedback
- Transaction history

## Filtering Logic

### GeneralMarketViewModel
The marketplace feed applies a filter to exclude private items.

```kotlin
// GeneralMarketViewModel.kt
val products = repository.getAllProducts()
    .map { list ->
        list.filter { product ->
            product.status == "AVAILABLE" && product.isPublic
        }
    }
```

### DigitalFarmViewModel
The farm management screens show all products belonging to the authenticated farmer, regardless of `isPublic` status.

## Toggle Mechanism
Farmers can toggle visibility from their inventory or listing details screen.
- **Make Private**: Removes from marketplace search/feed, keeps in farm records.
- **Make Public**: Publishes to marketplace if all validation criteria (images, price, location) are met.

## Order Management Components

### Order Repository
- `EvidenceOrderRepositoryImpl.kt`: Handles evidence-based order operations
- `OrderRepositoryImpl.kt`: Standard order management
- `OrderManagementRepositoryImpl.kt`: Order lifecycle management

### Order ViewModels
- `EvidenceOrderViewModel.kt`: Manages evidence-based order states
- `MyOrdersViewModel.kt`: User's order management
- `OrderTrackingViewModel.kt`: Real-time order tracking

### Order Entities
- `EvidenceOrderEntities.kt`: Order quotes, payments, delivery confirmations, order evidence, order disputes, order audit logs
- `OrderEntity.kt`: Standard order data
- `OrderItemEntity.kt`: Order item details
- `OrderTrackingEventEntity.kt`: Order state change events

### Order UI Components
- `ui/order/`: Order tracking and management screens
- `ui/order/evidence/MyOrdersScreen.kt`: User's evidence-based orders
- `ui/order/OrderTrackingScreen.kt`: Real-time order tracking
- `ui/marketplace/dispute/CreateDisputeViewModel.kt`: Dispute creation and management
