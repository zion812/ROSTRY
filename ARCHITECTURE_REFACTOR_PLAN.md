# Strategic Architecture Plan: The "Farm vs. Market" Decoupling

## 1. Executive Summary & Verification
**Assessment:** Your observation is 100% correct. PROVEN by `ProductEntity.kt`, which currently forces `birthDate` (biological data) and `price` (commercial data) into the same table. This "God Entity" pattern causes:
-   **Data Leakage Risk:** Private/Public flag (`status = "private"`) is brittle. A coding error could expose breeding logs to the public marketplace.
-   **Logic Complexity:** `FarmerHomeViewModel` currently burns CPU cycles filtering strictly "Market" fields vs "Management" fields.
-   **Scalability Blockers:** You cannot effectively manage inventory (e.g., "sell 50 eggs from 100 available") because the current model treats the *Product* as the *Asset*.

**The Goal:** Transition to a **3-Tier Domain Model**:
1.  **Farm Asset:** The Source of Truth (Biological/Physical).
2.  **Inventory:** The Allocatable Stock (Bridge).
3.  **Market Listing:** The Public Offer (Commercial).

---

## 2. The New Data Architecture

### A. Farm Asset (`FarmAssetEntity`)
*The "Management" Layer. Private, detailed, historical.*
**New Table:** `farm_assets`
-   **Primary Key:** `assetId` (UUID)
-   **Core:** `farmerId`, `assetType` (FLOCK, BATCH, EQUIPMENT, SHED), `name`
-   **Lifecycle:** `birthDate`, `ageWeeks`, `healthStatus`, `vaccinationRecords`
-   **Metrics:** `quantity` (Current headcount), `weight`, `mortalityCount`
-   **Traceability:** `parentIds`, `batchId`, `origin`
-   **Status:** `ACTIVE`, `QUARANTINED`, `ARCHIVED`

### B. Inventory Item (`InventoryItemEntity`)
*The "Stock" Layer. Derived from assets, ready for commerce.*
**New Table:** `farm_inventory`
-   **Primary Key:** `inventoryId` (UUID)
-   **Source:** `assetId` (FK -> FarmAsset), `farmerId`
-   **Stock:** `quantityAvailable` (e.g., 30), `quantityReserved` (In cart/pending), `unit` (KG, TRAYS, BIRDS)
-   **Type:** `SKU` (Standardized Product Codes, e.g., "EGG-TRAY-L")
-   **Expiry:** `harvestDate`, `bestBefore`

### C. Market Listing (`MarketListingEntity`)
*The "Sales" Layer. Public, marketing-focused.*
**New Table:** `market_listings`
-   **Primary Key:** `listingId` (UUID)
-   **Source:** `inventoryId` (FK -> InventoryItem) OR `assetId` (Direct asset sale)
-   **Commercial:** `price`, `currency`, `minOrderQuantity`
-   **Logistics:** `deliveryOptions` (PICKUP, DELIVERY), `location` (Geo + Address)
-   **Marketing:** `title`, `description`, `publicImageUrls`, `showcaseMode` (Boolean)
-   **Status:** `DRAFT`, `PUBLISHED`, `SOLD`, `SUSPENDED`

---

## 3. Migration Strategy (Critical)
Since we have existing data in `products`, we cannot simply delete it.

**The "Split &Deprecate" Strategy:**
1.  **Step 1:** Create new tables (`farm_assets`, `market_listings`).
2.  **Step 2:** Write a **One-Time Worker** (`LegacyProductMigrationWorker`):
    -   Iterate all `ProductEntity` rows.
    -   **If `status == "private"`:** Create `FarmAsset`.
    -   **If `status == "available"`:** Create `FarmAsset` (Record) + `Inventory` + `MarketListing` (Sales).
    -   Mark `ProductEntity` as `migrated = true` (add this column or just delete if bold).
3.  **Step 3:** Switch Repositories to read from new tables.

---

## 4. Execution Plan (Step-by-Step)

### Phase 1: Foundation (Data Layer)
-   [ ] **Create Entities:** Define `FarmAssetEntity` and `MarketListingEntity` in `data/database/entity/`.
-   [ ] **Update DAO:** Create `FarmAssetDao` (CRUD for assets) and `MarketListingDao` (Search/Filter for market).
-   [ ] **Database Migration:** Bump Room version. Add tables.

### Phase 2: Repository Logic
-   [ ] **Create `FarmAssetRepository`:**
    -   `getAssetsByFarmer(id)` instead of `getProductsBySeller`.
    -   `updateHealth(assetId, health)` - Specialized methods.
-   [ ] **Create `MarketRepository`:**
    -   `searchListings(query)` - Purely commercial search.
    -   `createListingFromAsset(assetId, price, qty)` - The "Publish" logic.

### Phase 3: UI Separation (The "Two Apps" Feel)
-   [ ] **Refactor `FarmerHomeViewModel`:**
    -   Point "My Flock" widgets to `FarmAssetRepository`.
    -   Remove pricing/sales logic from the Dashboard.
-   [ ] **Create `MyFarmScreen`:**
    -   Focus: Health, Feeding, Mortality.
    -   Action: "Sell This Batch" button -> Navigates to `CreateListingScreen`.
-   [ ] **Create `MarketplaceScreen`:**
    -   Feed of `MarketListingEntity`.
    -   Filtering based on *Price* and *Distance* (not bird health).

### Phase 4: Implementation of "Showcase Mode"
-   [ ] Add `isShowcase` flag to `FarmAsset`.
-   [ ] Update `FarmerProfileScreen`:
    -   Section 1: **For Sale** (Queries `MarketListing`).
    -   Section 2: **Farm Showcase** (Queries `FarmAsset` where `isShowcase == true`).

---

## 5. Timeline & Priority
1.  **Immeidate:** Define the Data Models (Task 1). This blocks everything else.
2.  **Next:** Create the Repositories.
3.  **Then:** UI Refactor (Farmer Dashboard first).

This plan realigns ROSTRY with professional ERP/Marketplace standards.
