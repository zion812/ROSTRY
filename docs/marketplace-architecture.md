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
