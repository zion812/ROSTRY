# ROSTRY Free Tier Survival Architecture

**Status**: Active Production Strategy
**Plan**: Firebase Spark (Free)
**Compliance**: 100%

---

## 1. Core Philosophy: "Thick Client, Smart Rules"

To operate entirely within the Firebase Free Tier (Spark Plan), ROSTRY has shifted from a server-centric model (Cloud Functions) to a client-centric model.

**Previous Architecture (Blaze):**
- Cloud Functions for role management, resizing, validation.
- Phone Authentication (SMS cost).
- Unrestricted uploads.

**Free Tier Architecture (Spark):**
- **Logic**: Moved to Kotlin Client + Firestore Security Rules.
- **Auth**: Google Sign-In (Unlimited free).
- **Storage**: Client-side compression + Video rejection.
- **Validation**: Enforced by Firestore Rules schema validation.

---

## 2. Key Restrictions & Implementations

### A. Authentication
- **Constraint**: Phone Auth (SMS) has a strict free limit (10/day) and requires billing for overage.
- **Solution**: 
  - `PHONE_AUTH_DISABLED = true` flag in `AuthRepositoryImpl`.
  - UI hides phone input options.
  - Primary Auth: **Google Sign-In** (Unlimited).

### B. Cloud Functions
- **Constraint**: Cloud Functions are not available on Spark Plan (requires Blaze for runtime).
- **Solution**: 
  - All Cloud Functions disabled/deleted.
  - **Custom Claims Replacement**: `firestore.rules` now read `userType` directly from the `users/{uid}` document instead of token claims.
  
  ```javascript
  // Old (Functions): request.auth.token.role == 'FARMER'
  // New (Spark): get(/databases/$(database)/documents/users/$(request.auth.uid)).data.userType == 'FARMER'
  ```

### C. Storage & Bandwidth
- **Constraint**: 5GB Storage limit. 1GB/day download limit.
- **Solution**: 
  - **Video Uploads**: **BLOCKED**. `ImageCompressor.isVideoFile()` throws an exception. UI hides video pickers.
  - **Image Compression**: Aggressive client-side compression.
    - Max Dimension: **1024px**
    - Quality: **60%**
    - Format: **WebP** (Smaller than JPEG)
  - **Result**: Average image size reduced from ~2MB to ~50KB. (40x reduction).

---

## 3. Flags for Future Upgrade

When revenue allows upgrading to the Blaze Plan, toggle these flags to `false` to re-enable premium features:

| Component | File | Flag | Action on Upgrade |
|-----------|------|------|-------------------|
| Auth | `AuthRepositoryImpl.kt` | `PHONE_AUTH_DISABLED` | Set to `false` |
| Compression | `ImageCompressor.kt` | `FREE_TIER_MODE` | Set to `false` (Restore 1080p/80%) |
| UI | `MultiProviderAuthScreen.kt` | `FREE_TIER_MODE` | Set to `false` (Show phone button) |
| UI | `FarmerCreateScreen.kt` | `FREE_TIER_MODE` | Set to `false` (Show video picker) |

---

## 4. Security Implications

- **Client-Side Logic**: We trust the client implementation for UX, but **Security Rules are the final authority**.
- **Rule Updates**: Rules now validate usage limits (e.g., `request.query.limit <= 100`) to prevent accidental high-read queries.
