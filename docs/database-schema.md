# ROSTRY Database Schema & API Reference

## Database Overview

- **Database**: Room (SQLite), version 91
- **Entity count**: 50+ entities
- **Migration strategy**: Incremental migrations (MIGRATION_1_2 through MIGRATION_90_91)

## Core Entities

### Product Management
| Entity | Table | Key Fields |
|--------|-------|------------|
| `ProductEntity` | `products` | productId, name, breed, category, price, sellerId, gender, ageWeeks, stage |
| `ProductDraftEntity` | `product_drafts` | draftId, productId, verifierId, fieldsJson, status, mergedAt, mergedInto |
| `MediaMetadataEntity` | `media_metadata` | mediaId, productId, type, url, thumbnailUrl, sizeBytes |

### User Management
| Entity | Table | Key Fields |
|--------|-------|------------|
| `UserEntity` | `users` | uniqueId, userId, fullName, userType, email, role |
| `KycVerificationEntity` | `kyc_verifications` | verificationId, userId, status, identityDocumentsJson, farmLocationLat/Lon |

### Marketplace & Orders
| Entity | Table | Key Fields |
|--------|-------|------------|
| `OrderEntity` | `orders` | orderId, buyerId, sellerId, status, totalPrice, createdAt |
| `OrderItemEntity` | `order_items` | itemId, orderId, productId, quantity, price |
| `DeliveryHubEntity` | `delivery_hubs` | hubId, name, latitude, longitude |
| `HubAssignmentEntity` | `hub_assignments` | productId, hubId, distanceKm, assignedAt |
| `DisputeEntity` | `disputes` | disputeId, transferId, reporterId, reportedUserId, reason, status |

### Transfer & Analytics
| Entity | Table | Key Fields |
|--------|-------|------------|
| `TransferEntity` | `transfers` | id, transferId, senderId, recipientId, productId, status |
| `TransferAnalyticsEntity` | `transfer_analytics` | id, transferId, senderId, recipientId, durationSeconds, hadConflicts |
| `ProfitabilityMetricsEntity` | `profitability_metrics` | id, entityId, entityType, revenue, costs, profit, profitMargin |

### Audit & Notifications
| Entity | Table | Key Fields |
|--------|-------|------------|
| `AuditLogEntity` | `audit_logs` | logId, type, refId, action, actorUserId, detailsJson |
| `NotificationEntity` | `notifications` | notificationId, userId, title, body, type, read, createdAt |

### Breeding & Genetics
| Entity | Table | Key Fields |
|--------|-------|------------|
| `BreedingRecordEntity` | `breeding_records` | id, sireId, damId, matingDate, expectedHatchDate |
| `HatchingLogEntity` | `hatching_logs` | id, breedingRecordId, eggsSet, fertileCount, hatchedCount |

---

## Key Interfaces

### Repositories
| Interface | Implementation | Purpose |
|-----------|---------------|---------|
| `ProductRepository` | `ProductRepositoryImpl` | Product CRUD + search |
| `UserRepository` | `UserRepositoryImpl` | User management + verification status |
| `TransferRepository` | `TransferRepositoryImpl` | Transfer lifecycle |
| `DisputeRepository` | `DisputeRepositoryImpl` | Dispute creation/resolution |

### Domain Services
| Service | Purpose |
|---------|---------|
| `RecommendationEngine` | Product recommendations (4 strategies) |
| `HubAssignmentService` | Location-based hub assignment (Haversine) |
| `AnalyticsEngineImpl` | Profitability metrics + reports |
| `DisputeManager` | Dispute lifecycle management |
| `TransferAnalyticsService` | Transfer tracking + CSV reports |
| `BreedingCompatibilityCalculator` | Genetic compatibility scoring |
| `GracefulDegradationService` | Service fallback management |
| `NotificationTriggerService` | Event-driven notifications |
| `VerificationSystemImpl` | Product verification + KYC |

### Configuration
| Data Class | Fields |
|------------|--------|
| `AppConfiguration` | security, thresholds, timeouts, features |
| `SecurityConfig` | adminIdentifiers, moderationBlocklist, allowedFileTypes |
| `ThresholdConfig` | storageQuotaMB, maxBatchSize, circuitBreakerFailureRate, hubCapacity, deliveryRadiusKm |
| `TimeoutConfig` | networkRequestSeconds, circuitBreakerOpenSeconds, retryDelaysSeconds |
| `FeatureConfig` | enableRecommendations, enableDisputes, enableBreedingCompatibility |

---

## Database Indices

Key indices for query performance:

```sql
-- Products
CREATE INDEX idx_products_seller ON products(sellerId)
CREATE INDEX idx_products_breed ON products(breed)
CREATE INDEX idx_products_category ON products(category)

-- Orders
CREATE INDEX idx_orders_buyer ON orders(buyerId)
CREATE INDEX idx_orders_seller ON orders(sellerId)
CREATE INDEX idx_orders_status ON orders(status)

-- Transfers
CREATE INDEX idx_transfers_sender ON transfers(senderId)
CREATE INDEX idx_transfers_recipient ON transfers(recipientId)
CREATE INDEX idx_transfers_product ON transfers(productId)

-- Analytics
CREATE INDEX idx_profitability_entity ON profitability_metrics(entityId, entityType)
CREATE INDEX idx_transfer_analytics_transfer ON transfer_analytics(transferId)
```
