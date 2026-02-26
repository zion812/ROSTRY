# ROSTRY Feature Documentation

## Media Upload Service

### Overview
Handles all media operations: image/video upload, compression, thumbnail generation, and retry logic.

### Components
- **`MediaUploadManager`** — Core upload orchestrator with Firebase Storage integration
- **`ImageCompressor`** — Image compression with quality tiers (free: 60%, premium: 85%)
- **`MediaUploadWorker`** — Background WorkManager worker for reliable uploads
- **`FileUploadValidator`** — Validates file type, size (max 100MB video, 10MB image), and dimensions (min 100x100)

### Upload Flow
1. User selects media → `FileUploadValidator` validates
2. `ImageCompressor` compresses image (or video passthrough)
3. `MediaUploadManager` uploads to Firebase Storage
4. On failure → `MediaUploadWorker` retries in background
5. Circuit Breaker protects against repeated failures

---

## Marketplace Recommendation Engine

### Overview
`RecommendationEngine` provides personalized product recommendations.

### Strategies (Priority Order)
1. **Personalized** — Based on purchase history (breed, category, price range)
2. **Related Products** — Same breed/category as viewed product
3. **Frequently Bought Together** — Co-occurrence analysis from orders
4. **Popular Products** — Fallback using recently updated products

### Performance
- Target: < 500ms response time
- Timing logged for each strategy
- `GracefulDegradationService` provides fallback when recommendations are unavailable

---

## Location-Based Hub Assignment

### Overview
`HubAssignmentService` assigns products to nearest delivery hubs.

### Algorithm
1. Get all hubs from `DeliveryHubDao`
2. Calculate distance using **Haversine formula**
3. Filter within configured radius (default 100km via `ThresholdConfig.deliveryRadiusKm`)
4. Check hub capacity (default 1000 via `ThresholdConfig.hubCapacity`)
5. Assign to nearest hub with available capacity
6. If all hubs full → assign to nearest with overflow flag
7. If no hubs in radius → flag for manual review

### Hub Assignment Entity
```
hub_assignments(productId, hubId, distanceKm, assignedAt, sellerLocationLat, sellerLocationLon)
```

---

## Transfer System

### Overview
Complete ownership transfer with conflict detection.

### Transfer Flow
1. Sender initiates transfer → `TransferRepository.initiateTransfer()`
2. Conflict check → only COMPLETED/CANCELLED transfers allowed for same product
3. Recipient receives notification via `NotificationTriggerService`
4. Recipient accepts → atomic ownership update
5. `TransferAnalyticsService.recordTransfer()` tracks metrics

### Timeout
- `TransferTimeoutWorker` cancels uncompleted transfers after configurable timeout
- Refund logic runs on timeout

---

## Verification System

### Overview
`VerificationSystemImpl` handles product verification and KYC.

### Product Verification
1. Verifier creates draft → `createDraft()`
2. Multiple drafts can exist per product
3. Admin merges drafts → `mergeDrafts()` with conflict resolution
4. Merged data updates product
5. Audit trail maintained in `AuditLogEntity`

### KYC Workflow
1. User submits KYC → `submitKyc()` (identity docs + farm location + photos)
2. `CoordinateValidator` validates farm coordinates
3. Admin reviews → `updateKycStatus()`
4. **Approved**: User status → VERIFIED, notification sent
5. **Rejected**: Notification with rejection reason

---

## Analytics & Profitability Engine

### Overview
`AnalyticsEngineImpl` calculates profitability and generates reports.

### Metrics
- **Revenue**: Sum of completed order items
- **Costs**: Sum of expenses + platform fees
- **Profit**: Revenue - Costs
- **Profit Margin**: (Profit / Revenue) × 100

### Reports
- **CSV**: Via `exportReportCsv()` using OpenCSV library
- **PDF**: Via `exportReportPdf()` using PdfDocument API
- **Dashboard**: `getDashboardMetrics()` with order count, top products, recent orders

### Aggregation
- `AnalyticsAggregationWorker` runs daily to pre-calculate metrics
- Stored in `profitability_metrics` table for fast retrieval

---

## Dispute Resolution

### Overview
`DisputeManager` implements the complete dispute lifecycle.

### Dispute Flow
1. **Buyer creates dispute** → `createDispute()` with order ID and evidence
2. **Seller notified** → can respond with evidence via `respondToDispute()`
3. **Admin reviews** → sees both sides in admin dashboard
4. **Resolution** → `resolveDispute()` with decision:
   - `Refund` — Full refund
   - `PartialRefund` — Partial refund
   - `Complete` — Order completes as-is
   - `Dismissed` — Dispute dismissed with reason
5. **All parties notified** of resolution
6. **Audit trail** maintained throughout

---

## Breeding Compatibility System

### Overview
`BreedingCompatibilityCalculator` provides genetic analysis for breeding pairs.

### Scoring (0-100)
| Factor | Impact |
|--------|--------|
| Base score | 100 |
| High inbreeding (COI > 0.125) | -30 |
| Lethal gene combinations | -50 |
| Sex-linked risks | -20 |
| Genetic diversity bonus | +10 |

### Features
- **Inbreeding Coefficient (COI)**: Wright's path method, traces up to 4 generations
- **Phenotype Prediction**: Based on `GeneticEngine` and `PhenotypeMapper`
- **Alternative Suggestions**: Up to 5 non-related birds ranked by genetic diversity
- **Lethal Risk Assessment**: Checks for known lethal gene combinations
- **Sex-Linked Trait Analysis**: Identifies sex-linked inheritance risks

---

## Graceful Degradation

### Overview
`GracefulDegradationService` provides fallback behavior when services are unavailable.

### Fallback Strategies
| Service | Fallback |
|---------|----------|
| Recommendations | Display popular products |
| Analytics | Display cached metrics (marked as stale) |
| Media Upload | Allow text-only product creation |
| Notifications | Queue for later delivery |

### UI Indicators
- Banner displayed when services are degraded
- Lists affected services with human-readable descriptions
- Auto-removed when services recover

### Core Workflow Priority
When 2+ services are degraded, core workflows (product CRUD, orders, transfers) are prioritized over auxiliary features.

---

## Notification Trigger Service

### Overview
`NotificationTriggerService` implements event-driven notifications.

### Triggers
| Event | Recipient | Type |
|-------|-----------|------|
| Verification complete | Product owner | VERIFICATION |
| Transfer received | Recipient | TRANSFER |
| Order status change | Buyer | ORDER |
| Lifecycle event | Owner | LIFECYCLE |
| Dispute opened | Seller | DISPUTE |
| Dispute resolved | Both parties | DISPUTE |
| KYC status change | User | KYC |

### Features
- **Preference Checking**: Respects per-category user preferences
- **Batching**: Groups notifications within 60-second window to avoid spam
- **Degradation Handling**: Queues notifications when service is unavailable
- **Delivery Target**: Within 60 seconds of triggering event
