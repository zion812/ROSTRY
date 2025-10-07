# Verification System

## Overview
- **Purpose:** Increase trust and compliance. Two tracks: Farmer Location Verification and Enthusiast KYC.
- **Lifecycle:** UNVERIFIED → PENDING → VERIFIED/REJECTED. Resubmission allowed after rejection.

## Farmer Location Verification
- **Requirements:** GPS coordinates (lat/lng), farm photos, optional land ownership docs.
- **Validation:**
  - Image/document format and size checks (JPEG/PNG/PDF ≤ 5MB)
  - EXIF GPS within 500m of claimed location (warning if mismatch)
  - Photo recency (warn if > 30 days)
- **Review:** Admin confirms location on map, inspects photos/docs.
- **Benefits:** Enables farmer features and listings.

## Enthusiast KYC
- **Requirements:** Government ID (Aadhaar/PAN/DL/Passport), selfie, address proof.
- **Validation:** Format/size checks, presence of required categories.
- **Review:** Admin verifies ID authenticity, address proof, and selfie.
- **Benefits:** Unlocks higher-value purchases and features.

## Technical Architecture
- **Flow:** UI (Compose) → `VerificationViewModel` → `VerificationValidationService` → `MediaUploadManager` → `FirebaseStorageUploader` → Firebase Storage → `UserRepository` → Firestore.
- **Notifications:** `VerificationNotificationService` posts local notifications and stores to Room via `NotificationDao`.
- **Outbox:** `UploadTaskEntity` persisted via `UploadTaskDao`; `MediaUploadWorker` processes uploads with retries and progress.

### Key Components
- `utils/validation/VerificationValidationService.kt` — centralized validation (files, EXIF, submissions, duplicate checks).
- `utils/media/FirebaseStorageUploader.kt` — real Storage uploads with progress/compression.
- `utils/media/MediaUploadManager.kt` — events API, retry, cancel, outbox scheduling.
- `workers/MediaUploadWorker.kt` — background processing and progress persistence.
- `ui/verification/VerificationViewModel.kt` — validation hooks, duplicate prevention, submission flags, event handling.
- `notifications/VerificationNotificationService.kt` — local notifications for pending/approved/rejected.
- `di/VerificationModule.kt` — Hilt providers.

## Admin Workflow
- **Screen:** `ui/moderation/ModerationScreen.kt` has a "Verifications" tab (basic list now).
- **ViewModel:** `ui/moderation/ModerationViewModel.kt` exposes `pendingVerifications` and approve/reject methods that call `UserRepository.updateVerificationStatus(...)` and notify users.
- **Next steps:** Add Firestore queries for pending verifications and detailed document viewers.

## Security
- **Rules (`firebase/firestore.rules`):**
  - Added `isAdmin()` helper.
  - Users may not self-approve. Admin-only updates for `verificationStatus`, `kycVerifiedAt`, `locationVerified`, `kycRejectionReason`.
  - 24h rate-limit on `kycUploadedAt` between submissions.
  - Basic JSON-array checks for `kycDocumentUrls`/`kycImageUrls`.
  - `verification_audits` collection read-only to admins and no writes from clients.

## Testing Guide
- **Unit tests (suggested):**
  - File validation: valid/invalid MIME, missing, oversized.
  - EXIF validation: matching/mismatching GPS, old photos.
  - Submission validation: missing pieces and invalid coordinates.
  - Duplicate detection: pending/verified/recent submissions.
- **Instrumentation (suggested):**
  - Full farmer flow (pick map/current location, upload photos/docs, submit, success dialog).
  - Full enthusiast flow (select types, upload, submit, success dialog).
  - Upload progress, retry, cancellation.

## Troubleshooting
- **Uploads fail:** Check connectivity, permissions, Firebase Storage rules.
- **No EXIF GPS:** Allowed with warning; encourage photos taken on-site.
- **Duplicate error:** Wait 24 hours or until current review completes.
- **Permissions:** Ensure location and media read permissions are granted.

## Future Enhancements
- OCR for ID verification, face match selfie ↔ ID.
- Reverse geocoding and satellite imagery checks for farm locations.
- Batch moderation actions with bulk notifications.
