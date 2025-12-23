# Zero-Cost Firebase Migration Guide

## Overview

This document describes the migration from Cloud Functions-based custom claims to a zero-cost client-side architecture. This migration saves approximately **₹775+/month** on Cloud Functions costs.

## What Changed

### Before (Expensive)
```
User writes to Firestore → Cloud Function triggers → Sets custom claims → Writes timestamp → Triggers again
```

**Problems:**
- Every user document write triggered a Cloud Function
- Cloud Functions cost ₹775+/month
- 5-15 second latency waiting for claims to propagate
- Potential infinite loop if not properly guarded

### After (Zero Cost)
```
User writes to Firestore → Firestore rules read userType directly from document → Instant authorization
```

**Benefits:**
- No Cloud Functions needed
- Zero additional cost (within Firebase free tier)
- Instant role changes (no waiting)
- Simpler architecture

## Technical Changes

### 1. Firestore Security Rules (`firestore.rules`)

The `getRole()` function now reads directly from the user document:

```javascript
function getUserData() {
  return get(/databases/$(database)/documents/users/$(request.auth.uid)).data;
}

function getRole() {
  // Backwards compatible: tries custom claims first, then falls back to document
  return request.auth.token.role != null 
    ? request.auth.token.role 
    : getUserData().userType;
}
```

**Note:** The `get()` call is cached per request, so multiple calls to `getUserData()` within the same request only count as one document read.

### 2. Android App Changes

- **SessionManager**: No longer reads role from custom claims
- **GeneralProfileViewModel**: Removed `waitForCustomClaims()` polling
- **RostryApp**: Removed claims verification wait during startup

## How to Disable Cloud Functions

### Option 1: Delete Functions (Recommended for Zero Cost)

Run from the `firebase/` directory:

```bash
# Navigate to firebase directory
cd firebase

# Delete all functions
firebase functions:delete setUserRoleClaim refreshUserClaims --force
firebase functions:delete verifyKYC --force
firebase functions:delete initiateAuction closeAuction --force
firebase functions:delete verifyPayment --force
firebase functions:delete canSendOtp setPhoneVerifiedClaim --force
```

### Option 2: Keep Functions for Future Use

If you want to keep the functions for future use but stop the costs:

1. Set `minInstances: 0` in all function configurations
2. The functions will only run when explicitly called (not on triggers)

**Edit `firebase/functions/src/index.ts` and `firebase/functions/src/roles.ts`:**

```typescript
// Comment out or delete the setUserRoleClaim trigger
// export const setUserRoleClaim = onDocumentWritten(...)
```

Then redeploy:

```bash
cd firebase
firebase deploy --only functions
```

### Option 3: Full Undeploy

```bash
cd firebase
firebase functions:delete --all --force
```

## Deploy Updated Firestore Rules

After making changes, deploy the new security rules:

```bash
cd firebase
firebase deploy --only firestore:rules
```

## Migration Checklist

- [x] Update `firestore.rules` to read userType from document
- [x] Update `SessionManager.kt` to not rely on custom claims
- [x] Update `GeneralProfileViewModel.kt` to not wait for claims
- [x] Update `RostryApp.kt` to not verify claims on startup
- [ ] Deploy updated Firestore rules
- [ ] Delete or disable Cloud Functions
- [ ] Test role-based access still works

## Testing

1. **Test role upgrade**: 
   - As GENERAL user, upgrade to FARMER
   - Verify immediate access to farmer features
   - No "waiting for claims" delay

2. **Test Firestore rules**:
   - Verify GENERAL users cannot create products
   - Verify FARMER users can create/update their products
   - Verify users can only write to their own documents

3. **Test existing users**:
   - Users with existing custom claims should still work (backwards compatible)
   - New users will use document-based roles

## Cost Impact

| Item | Before | After |
|------|--------|-------|
| Cloud Functions | ₹775/month | ₹0/month |
| Firestore Reads (+1 per request) | - | Minimal (within free tier) |
| **Total Savings** | - | **₹775+/month** |

## Rollback Plan

If issues arise, you can re-enable Cloud Functions:

1. Uncomment the functions in `index.ts` and `roles.ts`
2. Deploy: `firebase deploy --only functions`
3. Revert the Android app changes

However, the new architecture is backwards compatible - both systems can coexist.

## Questions?

The new architecture is simpler and more reliable. Custom claims are no longer needed for role-based authorization since Firestore rules can directly read from user documents.
