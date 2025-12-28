# Data Contracts

## Room Schema
Refer to `data/database/AppDatabase.kt` for the authoritative entity list. Key categories:
- Users (`UserEntity`), products (`ProductEntity`), orders (`OrderEntity`, `OrderItemEntity`), payments (`PaymentEntity`, `RefundEntity`).
- Transfer workflow (`TransferEntity`, `TransferVerificationEntity`, `DisputeEntity`, `AuditLogEntity`).
- Social platform (`PostEntity`, `CommentEntity`, `LikeEntity`, `FollowEntity`, `GroupEntity`, `EventEntity`, `ExpertBookingEntity`, `ModerationReportEntity`, `BadgeEntity`, `ReputationEntity`, `OutgoingMessageEntity`).
- Analytics (`AnalyticsDailyEntity`, `ReportEntity`).
- Traceability (`ProductTrackingEntity`, `FamilyTreeEntity`).

All entities include standard metadata fields (`createdAt`, `updatedAt`, `isDeleted`, `deletedAt` where applicable). Type converters handle `List<String>`, `UserType`, and `VerificationStatus` enums.

### Important Tables
- **Users**: Primary key `userId`, stores profile data, trust score, verification status.
- **Products**: Primary key `productId`. Includes `isPublic` (Boolean) to distinguish Marketplace listings from Private Farm assets. Includes traceability metadata, pricing, and certification.
- **Transfers**: Primary key `transferId`, references `productId`, status fields, photos, GPS coordinates, and financial info.
- **Posts/Comments**: Primary key IDs with foreign keys to users; supports moderation status and media URLs.
- **AnalyticsDaily**: Partitioned by date (milliseconds) and role, storing aggregated counters.

Migrations `MIGRATION_2_3` through `MIGRATION_11_12` (and beyond) maintain schema evolution. Review them for column additions and default values. SQLCipher encryption is enabled by `DatabaseModule` using `SupportFactory`.

## Firebase Collections (Suggested Structure)
- **auth**: Handled natively via Firebase Auth (phone OTP).
- **users**: Mirrors `UserEntity` with real-time updates for avatars, roles, verification states.
- **products**: Catalog data, including media, pricing, status (Active/Sold).
- **markets/{region}/listings**: Regional index for quick queries.
- **transfers**: Lightweight documents tracking live transfer progress; cloud functions update on status change.
- **social/posts/{postId}`**: Comments subcollection, likes counters, moderation flags.
- **notifications/{userId}/inbox**: Push notification payloads for offline devices.
- **analytics/daily/{role}`**: Aggregated metrics mirrored for dashboards.

Depending on deployment, Firestore indexes must be configured for composite queries (e.g., feed ranking, transfer lookups).

## REST / Retrofit APIs
Configuration via `di/HttpModule.kt` and `NetworkModule.kt`. Interfaces typically include:
- **Marketplace API**: Product search, pricing, logistics estimation.
- **Analytics API**: External integrations for AI recommendations and benchmark data.
- **Compliance API**: KYC/AML validation endpoints (stubs for now, extend as needed).

Ensure request/response DTOs map cleanly to domain models. Use Retrofit converters (Gson) declared in `app/build.gradle.kts`.

## Data Synchronization Contracts
- `SyncWorker` fetches remote changes, resolves conflicts by favoring server timestamps, and updates Room via DAOs.
- Repositories expose methods like `syncProducts()` or `refreshTransfers()` to coordinate multi-source updates.
- `SessionManager` provides role/time metadata for session validation to avoid stale operations.

## Serialization
- JSON serialization via Gson with custom adapters where necessary (e.g., nested trait data).
- Exports (reports, transfer documentation) use `Gson().toJson` data packages, returned to UI or stored as artifacts.

## Validation Utilities
- `utils/VerificationUtils.kt`: GPS radius checks, identity document validation entry points.
- `utils/ValidationUtils.kt`: General input validation.
- `utils/PhoneValidation.kt`: Phone normalization (E.164).

## Governance
- Audit logs stored both locally (`AuditLogEntity`) and (optionally) mirrored to Firestore or Cloud Storage.
- Rate limiting recorded in `RateLimitEntity` to enforce API throttles (e.g., transfer cooldowns).
