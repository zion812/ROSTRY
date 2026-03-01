# ADR-004: Farm vs. Market Domain Decoupling (3-Tier Model)

- **Status**: Accepted
- **Date**: 2026-03-01
- **Context**
  - The current `ProductEntity.kt` implementation follows a "God Entity" pattern, forcing biological data (like `birthDate`) and commercial data (like `price`) into the same table.
  - This architecture leads to data leakage risks, increased logic complexity in ViewModels (e.g., `FarmerHomeViewModel`), and blocks inventory management scalability (e.g., selling partial stock from a larger asset).
  - The "Private/Public" flag currently used to distinguish management data from marketplace data is brittle and error-prone.

- **Decision**
  - Transition to a **3-Tier Domain Model**:
    1. **Farm Asset (`FarmAssetEntity`)**: The private source of truth for biological and physical assets (flocks, batches, equipment).
    2. **Inventory Item (`InventoryItemEntity`)**: The bridge layer for allocatable stock derived from assets (SKUs, quantities, harvest dates).
    3. **Market Listing (`MarketListingEntity`)**: The public commercial layer for sales (pricing, MoQ, logistics, marketing).
  - Introduce `FarmAssetDao` and `MarketListingDao` for specialized data access.
  - Implement a one-time migration worker (`LegacyProductMigrationWorker`) to split existing `ProductEntity` data into the new structure.

- **Consequences**
  - **Positive**: Improved security through data isolation, simplified ViewModels, and professional-grade inventory/ERP capabilities.
  - **Risks**: Complexity of the one-time migration.
  - **Mitigation**: The migration is handled by a background `SyncWorker` (implemented as `LegacyProductMigrationWorker` in `RostryApp.kt`).
  - **Operational Impact**: Requires updates to all repositories consuming product data and significant UI refactoring to separate "Management" and "Marketplace" views.

- **References**
  - [SYSTEM_BLUEPRINT.md](../../SYSTEM_BLUEPRINT.md)
  - [RostryApp.kt](../../app/src/main/java/com/rio/rostry/RostryApp.kt) (Migration scheduling)
